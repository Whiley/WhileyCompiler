// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wycore.lang;

import java.util.*;

import wyc.lang.WhileyFile;
import wycore.util.*;
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
	private final ArrayList<BuildRule> rules;
	
	/**
	 * The logger is used to log messages for the project.
	 */
	private Logger logger = Logger.NULL;
	
	public Project(NameSpace namespace) {
		this.rules = new ArrayList<BuildRule>();
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
	 * @param data.builder
	 */
	public void add(BuildRule rule) {
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
	
	public void build(Collection<Path.Entry<?>> sources) throws Exception {
		HashSet<Path.Entry<?>> allTargets = new HashSet();
		
		// Firstly, initialise list of targets to rebuild.		
		for (BuildRule r : rules) {
			for (Path.Entry<?> source : sources) {
				allTargets.addAll(r.dependentsOf(source));
			}
		}
		
		// Secondly, add all dependents on those being rebuilt.
		int oldSize;
		do {
			oldSize = allTargets.size();
			for (BuildRule r : rules) {
				for (Path.Entry<?> target : allTargets) {
					allTargets.addAll(r.dependentsOf(target));
				}
			}
		} while (allTargets.size() != oldSize);
		
		// Finally, build all identified targets!
		do {
			oldSize = allTargets.size();
			for(BuildRule r : rules) {
				r.apply(allTargets);
			}
		} while(allTargets.size() < oldSize);
		
		// If we didn't manage to build all the targets, then this indicates
		// that some kind of cyclic dependency situation is present.
		if(!allTargets.isEmpty()) {
			System.out.println("Cyclic dependency!");
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
}
