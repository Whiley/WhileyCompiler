package wycore.lang;

import java.util.*;

import wyc.lang.WhileyFile;
import wycore.util.*;
import wyil.util.Logger;
import wyil.util.Pair;

/**
 * Represents the contextual information underpinning a given compilation.
 * Bringing all of this information together helps manage it, and enables a
 * certain amount of global analysis. For example, we can analyse dependencies
 * between source files.
 * 
 * @author David J. Pearce
 */
public class Project {

	/**
	 * The namespace is responsible for storing and retrieving all named items
	 * used within the project.
	 */
	private final NameSpace namespace;
	
	/**
	 * The rules associated with this project for transforming content. It is
	 * assumed that for any given transformation there is only one possible
	 * pathway described.
	 */
	private final ArrayList<Rule> rules;
	
	/**
	 * The logger is used to log messages for the project.
	 */
	private Logger logger = Logger.NULL;
	
	public Project(NameSpace namespace) {
		this.rules = new ArrayList<Rule>();
		this.namespace = namespace;
	}
	
	// ======================================================================
	// Configuration Interface
	// ======================================================================		
		
	/**
	 * Set the logger for this module loader.
	 * 
	 * @param logger
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * Add a new builder to this project.
	 * 
	 * @param builder
	 */
	public void add(Rule rule) {
		rules.add(rule);
	}
	
	// ======================================================================
	// Accessors	
	// ======================================================================		

	public NameSpace namespace() {
		return namespace;
	}
	
	// ======================================================================
	// Mutators
	// ======================================================================		

	/**
	 * Build the project using the given project builder(s).
	 */
	public void build() throws Exception {
		boolean allDone = false;
		while (!allDone) {
			allDone = true;
			for (Rule r : rules) {
				allDone &= r.apply(this);
			}
		}
	}
	
	/**
	 * Log a message, along with a time. The time is used to indicate how long
	 * it took for the action being reported. This is used primarily to signal
	 * that a given stage has been completed in a certain amount of time.
	 * 
	 * @param msg
	 * @param time --- total time taken for stage
     * @param memory --- difference in available free memory
	 */
	public void logTimedMessage(String msg, long time, long memory) {
		logger.logTimedMessage(msg, time, memory);
	}
	
	// ======================================================================
	// Types
	// ======================================================================		
			
	public static class Rule {
		private final Builder builder;
		private ArrayList<Path.Filter> matches;

		public Rule(Builder builder, Collection<Path.Filter> rules) {
			this.builder = builder;
			this.matches = new ArrayList(rules);		
		}
		
		public Rule(Builder builder, Path.Filter... filters) {
			this.builder = builder;
			this.matches = new ArrayList();
			for(Path.Filter r : filters) {
				this.matches.add(r);
			}
		}
		
		/**
		 * Apply the given rule by examining items it matches, and checking
		 * whether any are modified. If so, the builder it called upon to build
		 * them. A rule can fail if one or more modified items depends on some
		 * modified item outside the scope of this builder (i.e. described in
		 * another rule).
		 * 
		 * @param project
		 *            --- project on which this rule is applied.
		 * @return --- true if the rule succeeded, false if it is was blocked by
		 *         a dependency.
		 * @throws Exception
		 */
		public boolean apply(Project project) throws Exception {
			NameSpace namespace = project.namespace;
			ArrayList<Path.Entry<?>> delta = null;
			
			for (Path.Filter<?> filter : matches) {	
				for (Path.Entry<?> e : namespace.get(filter)) {
					if (e.isModified()) {
						if (delta == null) {
							delta = new ArrayList<Path.Entry<?>>();
						}
						delta.add(e);
					}
				}
			}
			
			if (delta != null) {
				// ok, there is something to build ... so do it!				
				builder.build(delta);
			} 
			
			return true;
		}
	}
}
