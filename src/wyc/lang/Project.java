package wyc.lang;

import java.util.*;

import wyc.util.*;
import wyil.util.Logger;
import wysrc.lang.WhileyFile;

/**
 * A Whiley project represents the contextual information underpinning a given
 * compilation. Bringing all of this information together helps manage it, and
 * enables a certain amount of global analysis. For example, we can analyse
 * dependencies between source files.
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
	 * The builders associated with this project for transforming content. It is
	 * assumed that for any given transformation there is only one possible
	 * pathway described by the builders.
	 */
	private final ArrayList<Builder> builders;
	
	/**
	 * The logger is used to log messages for the project.
	 */
	private Logger logger = Logger.NULL;
	
	public Project(NameSpace namespace) {
		this.builders = new ArrayList<Builder>();
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
	public void add(Builder builder) {
		builders.add(builder);
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
}
