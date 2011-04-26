package wyc.compiler;

import java.util.Map;

import wyc.Main.POP;

/**
 * A Pipeline consists of a number of stages which are applied to the
 * intermediate language (wyil). A pipeline is instantiated before being used to
 * create an instance of Compiler.
 * 
 * @author djp
 * 
 */
public class PipelineBuilder {

	/**
	 * The pipeline stage captures an uninstantiated stage in the pipeline.
	 * 
	 * @author djp
	 */
	private static class Stage {		
		public final String name;
		public final String arg;
		public final Map<String,String> options;
		
		public Stage(String name, String arg,
				Map<String, String> options) {			
			this.name = name;
			this.arg = arg;
			this.options = options;
		}
	}
	
	/**
	 * The pipeline modifier captures a requested adjustment to the compilation
	 * pipeline.
	 * 
	 * @author djp
	 */
	private static class Modifier {
		public final POP op;
		public final String name;
		public final String arg;
		public final Map<String,String> options;
		
		public Modifier(POP pop, String name, String arg,
				Map<String, String> options) {
			this.op = pop;
			this.name = name;
			this.arg = arg;
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
}
