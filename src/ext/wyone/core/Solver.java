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
import wyone.theory.logic.Conjunct;

public final class Solver implements Callable<Proof> {
	
	public static boolean debug = true;
	
	/**
	 * The list of inference rules to use when performing local inference.
	 */
	private final ArrayList<Rule> rules;

	/**
	 * This is the heuristic used to split states in two based on disjunctions
	 * and other split points.
	 */
	private final Heuristic splitHeuristic;
	
	/**
	 * The assignment is a global mapping of formulas to integer numbers
	 * which are, in effect, unique references for them.
	 */
	private final HashMap<Constraint,Integer> assignments = new HashMap<Constraint,Integer>();

	/**
	 * The rassignments lists is the inverse map of the assignments list. Each
	 * formula is located at a given index.
	 */
	private final ArrayList<Constraint> rassignments = new ArrayList<Constraint>();	
	
	/**
	 * The wyone constraint program being tested for satisfiability
	 */
	private final Constraint program;
		
	Solver(Constraint program, 
			Heuristic heuristic,Rule... theories) {		
		this.program = program;
		this.rules = new ArrayList<Rule>();
		this.splitHeuristic = heuristic;
		for (Rule t : theories) {
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
	 *        stops searching and returns Proof.Unknown.
	 * @return
	 */
	public static synchronized Proof checkUnsatisfiable(int timeout, Constraint program,
			Heuristic heuristic, Rule... theories) {				
 
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
	
	/**
	 * The following method is needed for the Callable interface
	 */
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
		// First, make sure these are reset properly
		rassignments.clear();
		assignments.clear();
		// now beging the process
		State init = new State();
		init.add(program, this);
		return checkUnsatisfiable(init,0);
	}

	protected Proof checkUnsatisfiable(State state, int level) {		
		if(debug) { indent(level); System.out.println("STATE: " + state); }
		
		if(state.contains(Value.FALSE)) {			
			// Here, we've reached a contradiction on this branch			
			return Proof.UNSAT;
		} else {			
			// This is the recursive case; we need to find a way to further
			// split the facts.
			List<State> substates = splitHeuristic.split(state, this);
			
			if(substates == null) {
				// At this point, we've run out of things to try. So, have we
				// found a model or not ?
				HashMap<Variable,Value> valuation = new HashMap<Variable,Value>();
				
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
	protected Proof checkModel(Map<Variable, Value> model) {										
		// Check program does indeed evaluate to true		
		Constraint f = program.substitute((Map) model);
		if(f != Value.TRUE) {
			return Proof.UNKNOWN; 
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
	public final class State implements Iterable<Constraint> {
				
		/**
		 * The assertions bitset detemines which assigned facts are currently
		 * active.
		 */
		private final BitSet assertions;

		/**
		 * The eliminations bitset detemines which assigned facts were active,
		 * but have been subsumed by something else. This is needed to prevent
		 * cyclic inference, where we repeatedly infer a fact which was
		 * previosly subsumed, and then subsume it again.
		 */
		private final BitSet eliminations;

		/**
		 * The worklist is used to hold the set of new constraints which have
		 * been inferred as a result of adding some constraint to the state.
		 * Essentially, this list identifies the delta of new constraints as a
		 * result of the add.
		 */
		private final ArrayList<Integer> worklist = new ArrayList<Integer>();
		
		public State() {
			assertions = new BitSet();
			eliminations = new BitSet();
		}
		
		public State(
				BitSet assertions, BitSet eliminations) {
			this.assertions = (BitSet) assertions.clone();
			this.eliminations = (BitSet) eliminations.clone();
		}
		
		public boolean contains(Constraint f) {
			Integer x = assignments.get(f);
			return x != null ? assertions.get(x) : false;				
		}
		
		public Iterator<Constraint> iterator() {
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
		public void add(Constraint f, Solver solver) {		
			worklist.clear();
			internal_add(f);			
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
		public void infer(Constraint f, Solver solver) {			
			internal_add(f);					
		}

		/**
		 * A formula is eliminated if it is implied by something else already present
		 * in the state. This is useful for reducing the overall number of formulas
		 * being considered. For example, x < 2 is subsumed by x < 1.
		 * 
		 * @param f
		 */
		public void eliminate(Constraint oldf) {
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
				Constraint f = rassignments.get(x);
				System.out.println("STATE BEFORE: " + this + " (" + System.identityHashCode(this) + "), i=" + i + "/" + worklist.size() + " : " + f);
				for(Rule ir : solver.rules) {				
					if(assertions.get(x)) {					
						ir.infer(f, this, solver);
						if(contains(Value.FALSE)){				
							return; // early termination
						}
					} else {
						break;
					}
				}		
				System.out.println("STATE AFTER: " + this + " (" + System.identityHashCode(this) + ")");
			}		
		}
		
		public State clone() {
			State nls = new State(assertions, eliminations);				
			return nls;
		}
			
		public String toString() {
			String r = "[";
			boolean firstTime=true;
			for(Constraint f : this) {
				if(!firstTime) {
					r += ", ";
				}
				firstTime=false;
				r += f;			
			}
			r += "]";
			return r;
		}
		
		private void internal_add(Constraint f) {				
			if(f instanceof Conjunct) {
				// We never add a conjunct directly; rather, we always break it
				// down into its constituent constraints.
				Conjunct c = (Conjunct) f;
				for(Constraint nc : c.subterms()) {
					internal_add(nc);
				}
			} else {
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
		}				
	}
	
	private final class AssertionIterator implements Iterator<Constraint> {
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
		
		public Constraint next() {
			Constraint f = rassignments.get(index); 
			index = assertions.nextSetBit(index+1);
			return f;
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * A split heuristic is used to split a given solver state into one or more
	 * sub-states. For example, the split might be done on a disjunction, whereby
	 * the different operands of the disjunction are asserted in corresponding
	 * sub-states.
	 * 
	 * @author djp
	 * 
	 */
	public interface Heuristic {
		
		/**
		 * Given a solver state, generate as many sub-states as possible. If the
		 * returned list is empty, this indicates no further splits are possible.
		 * 
		 * @param state
		 * @return
		 */
		public List<Solver.State> split(Solver.State state, Solver solver); 
	}

	/**
	 * An inference rule represents a way of generating new constraints from
	 * existing ones. In some cases, the new constraints may subsume the old
	 * ones. For example, suppose we have:
	 * 
	 * <pre>
	 * x < 1
	 * y < 0
	 * x == y
	 * </pre>
	 * 
	 * In this case, we might generate <code>x < 0</code> by applying an
	 * inference rule which substitutes y for x in the program. Thus,
	 * <code>x<0</code> will subsume <code>x<1</code>.
	 * 
	 * @author djp
	 * 
	 */
	public interface Rule {
		
		/**
		 * Given a set of facts, delta, infer all new facts which arise as a
		 * consequence of this in the given state.
		 * 
		 * @param delta
		 *            --- Formula recently added to state
		 * @param state
		 *            --- A current solver state
		 * @param state
		 *            --- The current solver instance
		 */
		public void infer(Constraint delta, Solver.State state, Solver solver);
	}
}
