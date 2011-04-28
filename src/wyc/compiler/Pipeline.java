package wyc.compiler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import wyc.compiler.WyCompiler.Stage;
import wyil.ModuleLoader;

/**
 * A Pipeline consists of a number of stages which are applied to the
 * intermediate language (wyil). A pipeline is instantiated before being used to
 * create an instance of Compiler.
 * 
 * @author djp
 * 
 */
public class Pipeline {
	private static final HashMap<String,Class<? extends Stage>> bindings = new HashMap();
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
					add(new Template("wyil", WyilWriter.class,
							Collections.EMPTY_MAP));
				}
			});
	
	static {
		register("wyil",WyilTransform.class);
		// new to register more here.
		// type propagation
		// definite assignment
		// constant propagation
		// function check
		// wyil file write
	}
	
	/**
	 * Apply a list of modifiers in the order of appearance. Modifiers may
	 * remove stages, add new stages or reconfigure existing stages.
	 * 
	 * @param modifiers
	 */
	public void apply(List<Modifier> modifiers) {
		for (Modifier p : modifiers) {
			Class<? extends Stage> stage = bindings.get(p.name);
			switch(p.op) {
			case APPEND:
				stages.add(new Template(p.name,stage,p.options));
				break;
			case REPLACE:
			{
				int index = findStage(p.name);
				stages.set(index,new Template(p.name,stage,p.options));
				break;
			}
			case REMOVE:
			{
				int index = findStage(p.name);
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
	public List<Stage> instantiate() {
		ArrayList<Stage> pipeline = new ArrayList<Stage>();
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
		public final Class<? extends Stage> clazz;		
		public final String name;
		public final Map<String,Object> options;
		
		public Template(String name, Class<? extends Stage> clazz,
				Map<String, Object> options) {
			this.name = name;
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
		public Stage instantiate(ModuleLoader loader) {
			try {				
				Constructor<? extends Stage> c = clazz.getConstructor(
						ModuleLoader.class, Map.class);
				Stage stage = (Stage) c.newInstance(loader, options);
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
	
	private int findStage(String match) {
		int i=0;
		for(Template stage : stages) {
			if(matchStageName(match,stage.name)) {
				return i;
			}
			++i;
		}
		throw new IllegalArgumentException("invalid stage name \"" + match + "\"");
	}
	
	private static boolean matchStageName(String match, String name) {
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
	
	/**
	 * Register a compiler stage with the system. A compiler stage requires a
	 * constructor which accepts a ModuleLoader, and Map<String,String> arguments
	 *
	 * @param name
	 * @param stage
	 */
	public static void register(String name, Class<? extends Stage> stage) {
		try {
			Constructor<? extends Stage> c = stage.getConstructor(
					ModuleLoader.class, Map.class);
			bindings.put(name, stage);
			return;
		} catch(NoSuchMethodException e) {
		}
		throw new IllegalArgumentException("cannot register stage \""
				+ name + "\" - missing required constructor");
	}	
}
