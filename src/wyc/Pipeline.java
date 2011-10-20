package wyc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import wyc.stages.BackPropagation;
import wyc.stages.TypePropagation;
import wyil.*;
import wyil.transforms.*;

/**
 * A Pipeline consists of a number of stages which are applied to the
 * intermediate language (wyil). A pipeline is instantiated before being used to
 * create an instance of Compiler.
 * 
 * @author David J. Pearce
 * 
 */
public class Pipeline {

	/**
	 * Identify transforms which are registered for use with the Whiley
	 * Compiler.
	 */
	private static final ArrayList<Class<? extends Transform>> transforms = new ArrayList();

	/**
	 * The module loader is passed to a given transform when it is instantiated.
	 * In some special cases, a transform will want access to the module loader.
	 */
	private final ModuleLoader loader;

	/**
	 * The list of stage templates which make up this pipeline. When the
	 * pipeline is instantiated, these stages are instantiated.
	 */
	private final ArrayList<Template> stages;
	
	public Pipeline(List<Template> stages,
			ModuleLoader loader) {		
		this.stages = new ArrayList<Template>(stages);
		this.loader = loader;
	}

	public static final List<Template> defaultPipeline = Collections
			.unmodifiableList(new ArrayList<Template>() {
				{					
					add(new Template(TypePropagation.class, Collections.EMPTY_MAP));
					add(new Template(WyilFileWriter.class, Collections.EMPTY_MAP));
					add(new Template(DefiniteAssignment.class, Collections.EMPTY_MAP));
					add(new Template(ModuleCheck.class, Collections.EMPTY_MAP));		
					add(new Template(ConstraintInline.class, Collections.EMPTY_MAP));					
					add(new Template(BackPropagation.class, Collections.EMPTY_MAP));
					// Constant Propagation is disabled as there are some
					// serious problems with that phase.
					//add(new Template(ConstantPropagation.class, Collections.EMPTY_MAP));
					add(new Template(CoercionCheck.class, Collections.EMPTY_MAP));
					add(new Template(DeadCodeElimination.class, Collections.EMPTY_MAP));
				}
			});

	/**
	 * Register default transforms. This is necessary so they can be referred to
	 * from the command-line using abbreviated names, rather than their full
	 * names.
	 */
	static {
		register(TypePropagation.class);
		register(BackPropagation.class);
		register(DefiniteAssignment.class);
		register(ConstantPropagation.class);
		register(ModuleCheck.class);
		register(ConstraintInline.class);
		register(CoercionCheck.class);
		register(WyilFileWriter.class);
		register(DeadCodeElimination.class);
	}
	
	/**
	 * Apply a list of modifiers in the order of appearance. Modifiers may
	 * remove stages, add new stages or reconfigure existing stages.
	 * 
	 * @param modifiers
	 */
	public void apply(List<Modifier> modifiers) {
		for (Modifier p : modifiers) {
			Class<? extends Transform> stage = lookupTransform(p.name);			
			if(stage == null) {
				throw new IllegalArgumentException("invalid pipeline stage \"" + p.name + "\"");
			}
			switch(p.op) {
			case APPEND:
				stages.add(new Template(stage,p.options));
				break;
			case REPLACE:
			{
				int index = findTransform(lookupTransform(p.name));
				stages.set(index,new Template(stage,p.options));
				break;
			}
			case REMOVE:
			{
				int index = findTransform(lookupTransform(p.name));
				stages.remove(index);
				break;			
			}
			}			
		}		
	}
	
	/**
	 * The following instantiates a compiler pipeline starting from the default
	 * pipeline and applying those modifiers requested
	 * 
	 * @param modifiers
	 * @return
	 */
	public List<Transform> instantiate() {
		ArrayList<Transform> pipeline = new ArrayList<Transform>();
		for (Template s : stages) {
			pipeline.add(s.instantiate(loader));
		}
		return pipeline;
	}

	/**
	 * A template is an uninstantiated pipeline stage. This contains all of the
	 * necessary information to instantiate the stage.
	 * 
	 * @author David J. Pearce
	 */
	public static class Template {					
		Class<? extends Transform> clazz;
		public final Map<String,Object> options;
		
		public Template(Class<? extends Transform> clazz, 
				Map<String, Object> options) {
			this.clazz = clazz;			
			this.options = options;
		}

		/**
		 * Construct an instance of a given compiler stage, using the given argument
		 * list. A constructor which accepts a ModuleLoader, and Map<String,String>
		 * arguments will be called. If such a constructor doesn't exist, an
		 * exception will be raised.
		 * 
		 * @return
		 */
		public Transform instantiate(ModuleLoader loader) {			
			Transform stage;
			
			// first, instantiate the transform
			try {				
				Constructor<? extends Transform> c = clazz.getConstructor(
						ModuleLoader.class);
				stage = (Transform) c.newInstance(loader);
										
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException(
						"failed to instantiate transform \""
								+ clazz.getSimpleName() + "\"",e);
			} catch (InstantiationException e) {
				throw new IllegalArgumentException(
						"failed to instantiate transform \""
								+ clazz.getSimpleName() + "\"",e);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException(
						"failed to instantiate transform \""
								+ clazz.getSimpleName() + "\"",e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException(
						"failed to instantiate transform \""
								+ clazz.getSimpleName() + "\"",e);
			}
			

			// second, configure the instance
			String attribute = "";
			try {
				for (Map.Entry<String, Object> e : options.entrySet()) {
					attribute = e.getKey();
					String name = "set" + capitalise(e.getKey());
					Object value = e.getValue();
					Method m;
					if(value instanceof Boolean) {
						m = clazz.getDeclaredMethod(name, boolean.class);
					} else if(value instanceof Integer) {
						m = clazz.getDeclaredMethod(name, int.class);
					} else {
						// default
						m = clazz.getDeclaredMethod(name, value.getClass());
					}					
					m.invoke(stage, value);
				}
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("failed to set attribute \""
						+ attribute + "\" on transform \""
						+ clazz.getSimpleName() + "\"",e);
			} catch(InvocationTargetException e) {
				throw new IllegalArgumentException("failed to set attribute \""
						+ attribute + "\" on transform \""
						+ clazz.getSimpleName() + "\"",e);
			} catch(IllegalAccessException e) {					
				throw new IllegalArgumentException("failed to set attribute \""
						+ attribute + "\" on transform \""
						+ clazz.getSimpleName() + "\"",e);
			}
			
			return stage;
		}
	}
	
	/**
	 * Make the first letter of the string a captial.
	 * @param str
	 * @return
	 */
	private static String capitalise(String str) {
		String rest = str.substring(1);
		char c = Character.toUpperCase(str.charAt(0));
		return c + rest;		
	}

	/**
	 * The pipeline modifier captures a requested adjustment to the compilation
	 * pipeline.
	 * 
	 * @author David J. Pearce
	 */
	public static class Modifier {
		public final POP op;
		public final String name;		
		public final Map<String,Object> options;
		
		public Modifier(POP pop, String name, Map<String, Object> options) {
			this.op = pop;
			this.name = name;			
			this.options = options;
		}
	}
	
	public enum POP {
		APPEND,
		BEFORE,
		AFTER,
		REPLACE,
		REMOVE
	}

	/**
	 * Search through the pipeline looking form the first matching stage.
	 * 
	 * @param match
	 * @return
	 */
	private int findTransform(Class<? extends Transform> match) {
		int i = 0;
		for (Template stage : stages) {
			if (stage.clazz == match) {
				return i;
			}
			++i;
		}
		throw new IllegalArgumentException("invalid stage name \"" + match
				+ "\"");
	}

	/**
	 * Register a transform with the system, in order that it can be used in a
	 * given Pipeline. This is particularly useful because it allows transforms
	 * to be referred to by abbreviations in pipeline modifiers.
	 * 
	 * @param transform
	 */
	public static void register(Class<? extends Transform> transform) {
		transforms.add(transform);
	}

	/**
	 * Lookup a transform in the list of registered transforms. This matches the
	 * given name again the class names of registered transforms. The matching
	 * of names is case-insensitive and will also match a substring.  
	 * 
	 * @param name
	 * @return
	 */
	public static Class<? extends Transform> lookupTransform(String name) {
		name = name.toLowerCase();
		for (Class<? extends Transform> t : transforms) {
			String tn = t.getSimpleName().toLowerCase();
			if (tn.startsWith(name)) {				
				return t;
			}
		}
		throw new IllegalArgumentException("no transform matching \"" + name
				+ "\"");
	}	
}
