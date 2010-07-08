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

import wyone.theory.congruence.*;
import wyone.theory.logic.*;

public final class Solver extends Thread {
	
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
	
	/**
	 * The following fields are used for the return value.
	 */
	private volatile Thread owner;		
	private volatile Proof value;
	private volatile RuntimeException exception;
	
	Solver(WFormula formula, 
			SplitHeuristic heuristic,InferenceRule... theories) {
		this.owner = Thread.currentThread();
		this.formula = formula;
		this.theories = new ArrayList<InferenceRule>();
		this.splitHeuristic = heuristic;
		for (InferenceRule t : theories) {
			this.theories.add(t);
		}
	}
	
	public WVariable newSkolem(WType type) {
		// The frustrating thing about skolems here, is that they permeate all
		// possible states of computation; not just those states where the
		// skolem is actually used.
		WVariable v = WVariable.freshVar();
		types.put(v.name(), type);
		return v;
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
	 * @param - timeout. The timeout in seconds, after which the solver stops
	 *        searching and returns Proof.Unknown.
	 * @return
	 */
	public static Proof checkUnsatisfiable(int timeout, WFormula formula,
			SplitHeuristic heuristic,
			InferenceRule... theories) {		
		
		// System.out.println("UNSAT: " + formula + " : " + types);
		
		Solver solver = new Solver(formula,heuristic,theories);
				
		try {
			solver.start();
			// now sleep
			Thread.sleep(timeout * 1000);
			// signal solver to stop
			solver.owner = null; // important
			solver.interrupt();
			Thread.yield(); // let other threads run
			// return unknown result
			return Proof.UNKNOWN;			
		} catch(InterruptedException e) {
			// caught signal from solver
		}	

		// indicates solver completed
		if(solver.exception != null) {
			// propagate solver exception
			throw solver.exception;
		} else {
			return solver.value;
		}
	}
	
	public void run() {
		try {
			value = checkUnsatisfiable();			
			if(owner != null) {
				owner.interrupt();
				Thread.yield(); // let other threads run
			}
		} catch(RuntimeException e) {
			exception = e;	
			if(owner != null) {
				owner.interrupt();
				Thread.yield(); // let other threads run
			}
		} catch(InterruptedException e) {
			
		}												
	}
	
	/**
	 * This method attempts to check whether the formula associated with this
	 * solver is unsatisfiable or not, using the given theories and heuristic.
	 * This method will run until completion and, hence, caution should be taken
	 * since it may take a long time!
	 * 
	 * @return
	 */
	private Proof checkUnsatisfiable() throws InterruptedException {		
		SolverState.reset_state();
		SolverState facts = new SolverState();
		facts.add(formula, this);
		return checkUnsatisfiable(facts,0);
	}

	protected Proof checkUnsatisfiable(SolverState state, int level) throws InterruptedException {		
		if(debug) { indent(level); System.out.println("STATE: " + state); }
		
		if(Thread.currentThread().isInterrupted()) {			
			throw new InterruptedException();
		} else if(state.contains(WBool.FALSE)) {			
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
