// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.core;

import static wyone.core.Constructor.*;
import java.util.*;
import java.util.concurrent.*;

public final class Solver implements Callable<Constructor> {
	
	public static boolean debug = false;
	
	/**
	 * The list of inference rules to use when performing local inference.
	 */
	private final ArrayList<Rule> rules;
		
	/**
	 * The wyone constraint program being tested for satisfiability
	 */
	private final Constructor root;
		
	Solver(Constructor program, Rule... rules) {		
		this.root = program;
		
		this.rules = new ArrayList<Rule>();
		
		for (Rule t : rules) {
			this.rules.add(t);
		}
	}	

	/**
	 * This method attempts to check whether the given program is unsatisfiable
	 * or not, using the given theories and heuristic. This method will run
	 * until completion and, hence, caution should be taken since it may take a
	 * long time!
	 * 
	 * @param - timeout. The timeout in milli-seconds, after which the solver
	 *        stops searching and returns Constructor.Unknown.
	 * @return
	 */
	public static synchronized Constructor reduce(int timeout, Constructor root,
			Rule... rules) {				
 
		// The following uses the java.util.concurrent library to enforce a
		// timeout on how long the solver will run for.
		ExecutorService es = Executors.newSingleThreadExecutor ();
		FutureTask<Constructor> task = new FutureTask<Constructor>(new Solver(root,
				rules));
		es.submit(task);
		
		Constructor r = null;
		
		try {			
			r = task.get(timeout,TimeUnit.MILLISECONDS);				
		} catch(ExecutionException ee) {
			throw new RuntimeException(ee);
		} catch(InterruptedException ie) {
			
		} catch(TimeoutException ie) {
			// timeout
		}				
		
		es.shutdown();
		
		return r;
	}
	
	/**
	 * The following method is needed for the Callable interface
	 */
	public Constructor call() {					
		return reduce();				
	}
		
	private Constructor reduce() {		
		// now beging the process
		State init = new State(root);		
		return reduce(init);
	}

	protected Constructor reduce(State state) {		
		return null;		
	}
	
	/**
	 * The solver state represents one state in the current search for a
	 * satisfying solution by a given solver. The state includes all currently
	 * known constraints.  
	 * 
	 * @author djp
	 * 
	 */
	public final class State {
				
		private Constructor root;

		public State(Constructor program) {
			this.root = program;
		}
								
	}	
}
