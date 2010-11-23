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

import java.util.*;
import java.util.concurrent.*;

import wyone.theory.congruence.*;
import wyone.theory.logic.*;

public final class Solver implements Callable<Proof> {
	
	public static boolean debug = false;
	
	/**
	 * The list of theories to use when performing local inference.
	 */
	private final ArrayList<InferenceRule> theories;

	/**
	 * This is the heuristic used to split states in two based on disjunctions
	 * and other split points.
	 */
	private final SplitHeuristic splitHeuristic;
	
	/**
	 * The formula being tested for satisfiability
	 */
	private final WFormula formula;
		
	Solver(WFormula formula, 
			SplitHeuristic heuristic,InferenceRule... theories) {		
		this.formula = formula;
		this.theories = new ArrayList<InferenceRule>();
		this.splitHeuristic = heuristic;
		for (InferenceRule t : theories) {
			this.theories.add(t);
		}
	}
	
	/**
	 * Access the theories being used in this solver.
	 * 
	 * @return
	 */
	public Collection<InferenceRule> theories() {
		return theories;
	}

	/**
	 * This method attempts to check whether the formula associated with this
	 * solver is unsatisfiable or not, using the given theories and heuristic.
	 * This method will run until completion and, hence, caution should be taken
	 * since it may take a long time!
	 * 
	 * @param - timeout. The timeout in milli-seconds, after which the solver
	 *        stops searching and returns Proof.Unknown.
	 * @return
	 */
	public static synchronized Proof checkUnsatisfiable(int timeout, WFormula formula,
			SplitHeuristic heuristic,
			InferenceRule... theories) {		
		
		// System.out.println("UNSAT: " + formula + " : " + types);
 
		// The following uses the java.util.concurrent library to enforce a
		// timeout on how long the solver will run for.
		ExecutorService es = Executors.newSingleThreadExecutor ();
		FutureTask<Proof> task = new FutureTask<Proof>(new Solver(formula,
				heuristic, theories));
		es.submit(task);
		
		Proof r = Proof.UNKNOWN;
		
		try {			
			r = task.get(timeout,TimeUnit.MILLISECONDS);				
		} catch(ExecutionException ee) {
			throw (RuntimeException) ee.getCause();
		} catch(InterruptedException ie) {
			
		} catch(TimeoutException ie) {
			// timeout
		}
		
		es.shutdown();
		
		return r;
	}
	
	public Proof call() {					
		return checkUnsatisfiable();				
	}
	
	/**
	 * This method attempts to check whether the formula associated with this
	 * solver is unsatisfiable or not, using the given theories and heuristic.
	 * This method will run until completion and, hence, caution should be taken
	 * since it may take a long time!
	 * 
	 * @return
	 */
	private Proof checkUnsatisfiable() {		
		SolverState.reset_state();
		SolverState facts = new SolverState();
		facts.add(formula, this);
		return checkUnsatisfiable(facts,0);
	}

	protected Proof checkUnsatisfiable(SolverState state, int level) {		
		if(debug) { indent(level); System.out.println("STATE: " + state); }
		
		if(state.contains(WBool.FALSE)) {			
			// Here, we've reached a contradiction on this branch			
			return Proof.UNSAT;
		} else {			
			// This is the recursive case; we need to find a way to further
			// split the facts.
			List<SolverState> substates = splitHeuristic.split(state, this);
			
			if(substates == null) {
				// At this point, we've run out of things to try. So, have we
				// found a model or not ?
				HashMap<WVariable,WValue> valuation = new HashMap<WVariable,WValue>();
				for(WFormula f : state) {
					if (f instanceof WEquality) {
						WEquality eq = (WEquality) f;
						WExpr lhs = eq.lhs();
						WExpr rhs = eq.rhs();
						if (lhs instanceof WVariable && rhs instanceof WValue
								&& ((WVariable) lhs).isConcrete()) {
							valuation.put((WVariable) lhs, (WValue) rhs);
						}
					}
				}
				
				return checkModel(valuation);
			} else {				
				for(SolverState s : substates) {
					Proof p = checkUnsatisfiable(s, level+1);
					if(!(p instanceof Proof.Unsat)) {						
						return p;
					}
				}				
				return Proof.UNSAT;
			}
		}
	}
	
	private static void indent(int level) {
		for(int i=0;i!=level;++i) {
			System.out.print(" ");
		}
	}
	
	/**
	 * The purpose of this method is to check whether a proposed model is indeed
	 * a valid model, or not.
	 * 
	 * @param model --- proposed model of formula
	 * @param formula
	 * @param types --- types of formula
	 * @return
	 */
	protected Proof checkModel(Map<WVariable, WValue> model) {										
		// Check formula does indeed evaluate to true		
		WFormula f = formula.substitute((Map) model);
						
		if(f == WBool.TRUE) {
			return new Proof.Sat(model);
		} else {			
			return Proof.UNKNOWN;
		}
	}	
}
