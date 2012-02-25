package wyc.lang;

import java.util.*;

import wyc.util.*;
import wyil.util.Logger;
import wysrc.lang.SourceFile;

/**
 * A Whiley project represents the contextual information underpinning a given
 * compilation. This includes the WHILEYPATH, the package roots for all source
 * files and the binary destination folders as well. Bringing all of this
 * information together helps manage it, and enables a certain amount of global
 * analysis. For example, we can analyse dependencies between source files.
 * 
 * @author David J. Pearce
 */
public class Project extends MasterRoot {

	/**
	 * The builders associated with this project for transforming content. It is
	 * assumed that for any given transformation there is only one possible
	 * pathway described by the builders.
	 */
	private final ArrayList<Builder> builders;
	
	/**
	 * The map from 
	 */
	private final Map<Path.ID,Path.ID> mapping;
	
	/**
	 * The logger is used to log messages for the project.
	 */
	private Logger logger = Logger.NULL;
	
	public Project(Collection<Path.Root> roots, List<Builder> builders,
			Map<Path.ID, Path.ID> mapping) {
		super(roots);
		this.builders = new ArrayList<Builder>(builders);
		this.mapping = mapping;
	}
	
	// ======================================================================
	// Public Configuration Interface
	// ======================================================================		
		
	/**
	 * Set the logger for this module loader.
	 * 
	 * @param logger
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	
	// ======================================================================
	// Public Mutator Interface
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
