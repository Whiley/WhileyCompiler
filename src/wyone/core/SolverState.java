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

public final class SolverState implements Iterable<WFormula> {
	/**
	 * The assignment is a global mapping of formulas to integer numbers
	 * which are, in effect, unique references for them.
	 */
	private static HashMap<WFormula,Integer> assignments = new HashMap<WFormula,Integer>();

	/**
	 * The rassignments lists is the inverse map of the assignments list. Each
	 * formula is located at a given index.
	 */
	private static ArrayList<WFormula> rassignments = new ArrayList<WFormula>();
	
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
	
	public boolean contains(WFormula f) {
		Integer x = assignments.get(f);
		return x != null ? assertions.get(x) : false;				
	}
	
	public Iterator<WFormula> iterator() {
		return new AssertionIterator(assertions,0);
	}

	public void add(WFormula f, Solver solver) {		
		if(f == WBool.TRUE) {
			// do nothing
		} else {
			worklist.clear();
			internal_add(f);
			infer(solver);			
		}
		
	}

	/**
	 * The infer method is designed to be called by inference rules. This method
	 * doesn't immediately infer consequences of the formula f; it assumes we're
	 * already in the process of doing that.
	 * 
	 * @param f
	 * @param solver
	 */
	public void infer(WFormula f, Solver solver) {			
		if(f == WBool.TRUE) {
			// do nothing
		} else if(f instanceof WConjunct) {
			WConjunct c = (WConjunct) f;				
			for(WFormula p : c.subterms()) {
				infer(p,solver);
			}
		} else {
			internal_add(f);		
		}		
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
	public void eliminate(WFormula oldf) {
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
			WFormula f = rassignments.get(x);
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
	
	/**
	 * The purpose of this method is to reduce a complex formula (e.g.
	 * disjunction) in the presence of existing literals. For example, x==1|x!=2
	 * reduces to true if x==1 is already asserted.
	 * 
	 * @param f
	 * @return
	 */
	public WFormula reduce(WFormula f) {		
		if(f instanceof WBool) {
			return f;
		} else if (f instanceof WLiteral) {			
			if (contains(f)) {				
				return WBool.TRUE;
			} else if (contains(f.not())) {				
				return WBool.FALSE;
			} else {				
				return f;
			}
		} else if(f instanceof WConjunct) {
			WConjunct c = (WConjunct) f;
			HashSet<WFormula> lits = new HashSet<WFormula>();
			boolean changed=false;
			for(WFormula dl : c) {
				WFormula d = reduce(dl);
				changed |= dl != d;
				if(d == WBool.FALSE) {
					return WBool.FALSE; 
				} else if(d != WBool.TRUE) {
					if(d instanceof WConjunct) {
						WConjunct cd = (WConjunct) d;
						lits.addAll(cd.subterms());
					} else {					
						lits.add(d);
					}
				} 
			}
			if(lits.size() == 0) {
				return WBool.TRUE;
			} else if(lits.size() == 1) {
				return lits.iterator().next();
			} else if(changed) {
				return new WConjunct(lits);
			} else {
				return c;
			}
		} else if(f instanceof WDisjunct) {
			WDisjunct d = (WDisjunct) f;
			HashSet<WFormula> lits = new HashSet<WFormula>();
			boolean changed = false;
			for(WFormula cl : d) {
				WFormula c = reduce(cl);
				changed |= cl != c;
				if(c == WBool.TRUE) {
					return WBool.TRUE; 
				} else if(c != WBool.FALSE) {
					if(c instanceof WDisjunct) {
						WDisjunct cd = (WDisjunct) c;
						lits.addAll(cd.subterms());
					} else {
						lits.add(c);
					}
				}
			}
			if(lits.size() == 0) {
				return WBool.FALSE;
			} else if(lits.size() == 1) {
				return lits.iterator().next();
			} else if(changed) {
				return new WDisjunct(lits);
			} else {
				return d;
			}
		}
		
		return f;
	}	

	public SolverState clone() {
		SolverState nls = new SolverState(assertions, eliminations);				
		return nls;
	}
		
	public String toString() {
		String r = "[";
		boolean firstTime=true;
		for(WFormula f : this) {
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
		rassignments = new ArrayList<WFormula>();
		assignments = new HashMap();
	}
	
	private void internal_add(WFormula f) {		
		f = reduce(f); 		
		
		if(f instanceof WConjunct) {
			WConjunct wc = (WConjunct) f;
			for(WFormula nf : wc.subterms()) {
				internal_add(nf);
			}
			return;
		} 
				
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
		
	private static final class AssertionIterator implements Iterator<WFormula> {
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
		
		public WFormula next() {
			WFormula f = rassignments.get(index); 
			index = assertions.nextSetBit(index+1);
			return f;
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
