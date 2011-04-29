package wyc.compiler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import wyil.*;
import wyil.transforms.*;

/**
 * A Pipeline consists of a number of stages which are applied to the
 * intermediate language (wyil). A pipeline is instantiated before being used to
 * create an instance of Compiler.
 * 
 * @author djp
 * 
 */
public class Pipeline {
	private static final HashMap<String,Class<? extends Transform>> bindings = new HashMap();
	private final ModuleLoader loader;	
	private final ArrayList<Template> stages;
	
	public Pipeline(List<Template> stages,
			ModuleLoader loader) {		
		this.stages = new ArrayList<Template>(stages);
		this.loader = loader;
	}

	public static final List<Template> defaultPipeline = Collections
			.unmodifiableList(new ArrayList<Template>() {
				{
					add(new Template(WyilFileWriter.class, Collections.EMPTY_MAP));
					add(new Template(TypePropagation.class, Collections.EMPTY_MAP));
					add(new Template(DefiniteAssignment.class, Collections.EMPTY_MAP));
					add(new Template(ConstantPropagation.class, Collections.EMPTY_MAP));
					add(new Template(FunctionCheck.class, Collections.EMPTY_MAP));					
				}
			});
	/**
	 * Apply a list of modifiers in the order of appearance. Modifiers may
	 * remove stages, add new stages or reconfigure existing stages.
	 * 
	 * @param modifiers
	 */
	public void apply(List<Modifier> modifiers) {
		for (Modifier p : modifiers) {
			Class<? extends Transform> stage = bindings.get(p.name);
			switch(p.op) {
			case APPEND:
				stages.add(new Template(stage,p.options));
				break;
			case REPLACE:
			{
				int index = findTransform(p.name);
				stages.set(index,new Template(stage,p.options));
				break;
			}
			case REMOVE:
			{
				int index = findTransform(p.name);
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
	 * @author djp
	 */
	private static class Template {					
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
			
			try {
				
				// first, create the instance
				Constructor<? extends Transform> c = clazz.getConstructor(
						ModuleLoader.class);
				Transform stage = (Transform) c.newInstance(loader);
				
				// second, configure the instance
				for(Map.Entry<String,Object> e : options.entrySet()) {
					String name = "set" + e.getKey();
					Object value = e.getValue();
					Method m = clazz.getDeclaredMethod(name, value.getClass());
					m.invoke(stage, value);
				}
				
				return stage;				
			} catch(NoSuchMethodException e) {
			} catch(InstantiationException e) {
			} catch(InvocationTargetException e) {
			} catch(IllegalAccessException e) {					
			}
			
			throw new IllegalArgumentException("invalid stage: " + clazz.getName());
		}
	}
	
	/**
	 * The pipeline modifier captures a requested adjustment to the compilation
	 * pipeline.
	 * 
	 * @author djp
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
	
	private enum POP {
		APPEND,
		BEFORE,
		AFTER,
		REPLACE,
		REMOVE
	}
	
	private int findTransform(String match) {
		int i=0;
		for(Template stage : stages) {
			if(matchTransformName(match,stage.clazz.getName())) {
				return i;
			}
			++i;
		}
		throw new IllegalArgumentException("invalid stage name \"" + match + "\"");
	}
	
	private static boolean matchTransformName(String match, String name) {
		name = name.toLowerCase();
		if(match.equals(name) || name.startsWith(match)) {
			return true;
		}
		String initials = splitInitials(name);
		if(match.equals(initials)) {
			return true;
		}
		return false;
	}
	
	private static String splitInitials(String name) {
		String[] words = name.split(" ");
		String r = "";
		for(String w : words) {
			r += w.charAt(0);
		}
		return r;
	}
}
