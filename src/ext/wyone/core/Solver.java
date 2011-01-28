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
	 * The wyone program being tested for satisfiability
	 */
	private final List<WConstraint> program;
		
	Solver(List<WConstraint> program, 
			SplitHeuristic heuristic,InferenceRule... theories) {		
		this.program = program;
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
	 * This method attempts to check whether the given program is unsatisfiable
	 * or not, using the given theories and heuristic. This method will run
	 * until completion and, hence, caution should be taken since it may take a
	 * long time!
	 * 
	 * @param - timeout. The timeout in milli-seconds, after which the solver
	 *        stops searching and returns Proof.Unknown.
	 * @return
	 */
	public static synchronized Proof checkUnsatisfiable(int timeout, List<WConstraint> program,
			SplitHeuristic heuristic,
			InferenceRule... theories) {				
 
		// The following uses the java.util.concurrent library to enforce a
		// timeout on how long the solver will run for.
		ExecutorService es = Executors.newSingleThreadExecutor ();
		FutureTask<Proof> task = new FutureTask<Proof>(new Solver(program,
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
		State.reset_state();
		State facts = new State();
		facts.addAll(program, this);
		return checkUnsatisfiable(facts,0);
	}

	protected Proof checkUnsatisfiable(State state, int level) {		
		if(debug) { indent(level); System.out.println("STATE: " + state); }
		
		if(state.contains(WValue.FALSE)) {			
			// Here, we've reached a contradiction on this branch			
			return Proof.UNSAT;
		} else {			
			// This is the recursive case; we need to find a way to further
			// split the facts.
			List<State> substates = splitHeuristic.split(state, this);
			
			if(substates == null) {
				// At this point, we've run out of things to try. So, have we
				// found a model or not ?
				HashMap<Variable,WValue> valuation = new HashMap<Variable,WValue>();
				
				/*
				 * FIXME: need to put this back ... when I figured out how I identify assignments.
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
				*/
				return checkModel(valuation);
			} else {				
				for(State s : substates) {
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
	protected Proof checkModel(Map<Variable, WValue> model) {										
		// Check formula does indeed evaluate to true		
		for(WConstraint c : program) {
			WConstraint f = c.substitute((Map) model);
			if(f != WValue.TRUE) {
				return Proof.UNKNOWN; 
			}
		}
								
		return new Proof.Sat(model);		
	}

	/**
	 * The solver state represents one state in the current search for a
	 * satisfying solution by a given solver. The state includes all currently
	 * known constraints.  
	 * 
	 * @author djp
	 * 
	 */
	public final static class State implements Iterable<WConstraint> {
		/**
		 * The assignment is a global mapping of formulas to integer numbers
		 * which are, in effect, unique references for them.
		 */
		private static HashMap<WConstraint,Integer> assignments = new HashMap<WConstraint,Integer>();

		/**
		 * The rassignments lists is the inverse map of the assignments list. Each
		 * formula is located at a given index.
		 */
		private static ArrayList<WConstraint> rassignments = new ArrayList<WConstraint>();
		
		/**
		 * The assertions bitset detemines which assigned facts are currently
		 * active.
		 */
		private final BitSet assertions;

		/**
		 * The eliminations bitset detemines which assigned facts were active, but
		 * have been subsumed by something else.
		 */
		private final BitSet eliminations;

		
		public State() {
			assertions = new BitSet();
			eliminations = new BitSet();
		}
		
		public State(
				BitSet assertions, BitSet eliminations) {
			this.assertions = (BitSet) assertions.clone();
			this.eliminations = (BitSet) eliminations.clone();
		}
		
		public boolean contains(WConstraint f) {
			Integer x = assignments.get(f);
			return x != null ? assertions.get(x) : false;				
		}
		
		public Iterator<WConstraint> iterator() {
			return new AssertionIterator(assertions,0);
		}

		/**
		 * The add method is designed to be called by external clients. This method
		 * will not only add the given constraint, but will then infer all possible
		 * implied constraints as well.
		 * 
		 * @param f
		 * @param solver
		 */
		public void add(WConstraint f, Solver solver) {		
			worklist.clear();
			internal_add(f);
			infer(solver);					
		}

		/**
		 * The add method is designed to be called by external clients. This method
		 * will not only add the given constraints, but will then infer all possible
		 * implied constraints as well.
		 * 
		 * @param f
		 * @param solver
		 */
		public void addAll(Collection<WConstraint> fs, Solver solver) {		
			worklist.clear();
			for(WConstraint f : fs) {
				internal_add(f);
			}
			infer(solver);					
		}
		
		/**
		 * The infer method is designed to be called by inference rules. This method
		 * doesn't immediately infer consequences from the expression; it assumes we're
		 * already in the process of doing that.
		 * 
		 * @param f
		 * @param solver
		 */
		public void infer(WConstraint f, Solver solver) {			
			internal_add(f);					
		}

		/**
		 * The following worklist is a bit of a hack, but it works nicely. Making it
		 * static certainly improves overall performance, however there will be a
		 * distinct problem when moving to a parallel solver implementation.
		 */
		private static final ArrayList<Integer> worklist = new ArrayList<Integer>();
		
		/**
		 * A formula is eliminated if it is implied by something else already present
		 * in the state. This is useful for reducing the overall number of formulas
		 * being considered. For example, x < 2 is subsumed by x < 1.
		 * 
		 * @param f
		 */
		public void eliminate(WConstraint oldf) {
			Integer x = assignments.get(oldf);							
			if(x != null) {			
				assertions.clear(x);			
				eliminations.set(x);
			}		
		}

		/**
		 * The purpose of the following method is to determine all new facts which
		 * are immediate consequences of some fact being asserted.
		 */
		private void infer(Solver solver) {		
			for(int i=0;i!=worklist.size();++i) {
				Integer x = worklist.get(i);			
				WConstraint f = rassignments.get(x);
				//System.out.println("STATE BEFORE: " + this + " (" + System.identityHashCode(this) + "), i=" + i + "/" + worklist.size() + " : " + f);
				for(InferenceRule ir : solver.theories()) {				
					if(assertions.get(x)) {					
						ir.infer(f, this, solver);
						if(contains(WValue.FALSE)){				
							return; // early termination
						}
					} else {
						break;
					}
				}		
				//System.out.println("STATE AFTER: " + this + " (" + System.identityHashCode(this) + ")");
			}		
		}
		
		public State clone() {
			State nls = new State(assertions, eliminations);				
			return nls;
		}
			
		public String toString() {
			String r = "[";
			boolean firstTime=true;
			for(WConstraint f : this) {
				if(!firstTime) {
					r += ", ";
				}
				firstTime=false;
				r += f;			
			}
			r += "]";
			return r;
		}
		
		public static void reset_state() {
			rassignments = new ArrayList<WConstraint>();
			assignments = new HashMap();
		}
		
		private void internal_add(WConstraint f) {				
			Integer x = assignments.get(f);
			
			if(x == null) {			
				// no previous assignment, so make one
				int assignment = assignments.size();
				assignments.put(f,assignment);
				rassignments.add(f);
				assertions.set(assignment);
				worklist.add(assignment);						
			} else if (!assertions.get(x) && !eliminations.get(x)) {
				assertions.set(x);			
				worklist.add(x);			
			}
		}
			
		private static final class AssertionIterator implements Iterator<WConstraint> {
			private final BitSet assertions;
			private int index;
			
			public AssertionIterator(BitSet assertions, int start) {			
				this.assertions = assertions;
				// initialise the index position
				index = assertions.nextSetBit(start);			
			}
			
			public boolean hasNext() {
				return index != -1;
			}
			
			public WConstraint next() {
				WConstraint f = rassignments.get(index); 
				index = assertions.nextSetBit(index+1);
				return f;
			}
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
		}
	}
}
