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

import wyone.theory.logic.*;

public final class SolverState implements Iterable<WConstraint> {
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

	
	public SolverState() {
		assertions = new BitSet();
		eliminations = new BitSet();
	}
	
	public SolverState(
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
	 * will not only add the given constraints, but will then infer all possible
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
					if(contains(WBool.FALSE)){				
						return; // early termination
					}
				} else {
					break;
				}
			}		
			//System.out.println("STATE AFTER: " + this + " (" + System.identityHashCode(this) + ")");
		}		
	}
	
	public SolverState clone() {
		SolverState nls = new SolverState(assertions, eliminations);				
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
