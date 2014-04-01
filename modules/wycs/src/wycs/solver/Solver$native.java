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

package wycs.solver;

import wyautl.core.Automaton;

/**
 * Implements a lexiographic ordering of variable expressions.
 * 
 * @author David J. Pearce
 *
 */
public class Solver$native {
	
	public static Automaton.Term min(Automaton automaton, int rBag) {
		Automaton.Bag bag = (Automaton.Bag) automaton.get(rBag);
		int least = -1;
		for(int i=0;i!=bag.size();++i) {
			int child = bag.get(i);
			if (least == -1 || compare(automaton, child, least) == -1) {
				least = child;
			}
		}
		return (Automaton.Term) automaton.get(least);
	}
	
	public static Automaton.Term max(Automaton automaton, int rBag) {
		Automaton.Bag bag = (Automaton.Bag) automaton.get(rBag);
		int greatest = -1;
		for (int i = 0; i != bag.size(); ++i) {
			int child = bag.get(i);
			if (greatest == -1 || compare(automaton, child, greatest) == 1) {
				greatest = child;
			}
		}
		return (Automaton.Term) automaton.get(greatest);
	}

	public static Automaton.Term maxMultiplicand(Automaton automaton, int rBag) {
		Automaton.Bag bag = (Automaton.Bag) automaton.get(rBag);
		int greatest = -1;
		for(int i=0;i!=bag.size();++i) {
		    Automaton.Term mulTerm = (Automaton.Term) automaton.get(bag.get(i));
 		    Automaton.List mulChildren = (Automaton.List) automaton.get(mulTerm.contents);
		    Automaton.Bag mulChildChildren = (Automaton.Bag) automaton.get(mulChildren.get(1));
		    if (mulChildChildren.size() == 1) {
			int child = mulChildChildren.get(0);
			if(greatest == -1 || compare(automaton, child, greatest) == 1) {
			    greatest = child;
			}
		    }
		}
		return (Automaton.Term) automaton.get(greatest);
	}
	
	private static int compare(Automaton automaton, int r1, int r2) {
		if(r1 == r2) { return 0; }
		Automaton.State s1 = automaton.get(r1);
		Automaton.State s2 = automaton.get(r2);
		// first, easy case
		if(s1.kind < s2.kind) {
			return -1;
		} else if(s1.kind > s2.kind) {
			return 1;
		}
		
		if(s1 instanceof Automaton.Bool) {
			Automaton.Bool b1 = (Automaton.Bool) s1;
			Automaton.Bool b2 = (Automaton.Bool) s2;
			return b1.value.compareTo(b2.value);
		} else if(s1 instanceof Automaton.Int) {
			Automaton.Int i1 = (Automaton.Int) s1;
			Automaton.Int i2 = (Automaton.Int) s2;
			return i1.value.compareTo(i2.value);
		} else if(s1 instanceof Automaton.Strung) {
			Automaton.Strung i1 = (Automaton.Strung) s1;
			Automaton.Strung i2 = (Automaton.Strung) s2;
			return i1.value.compareTo(i2.value);
		} else if(s1 instanceof Automaton.Term) {
			Automaton.Term t1 = (Automaton.Term) s1;
			Automaton.Term t2 = (Automaton.Term) s2;
			return compare(automaton,t1.contents,t2.contents);
		} else {
			Automaton.Collection c1 = (Automaton.Collection) s1;
			Automaton.Collection c2 = (Automaton.Collection) s2;
			int c1_size = c1.size();
			int c2_size = c2.size();
			if(c1_size < c2_size) {
				return -1;
			} else if(c1_size > c2_size) {
				return 1;
			}
			for(int i=0;i!=c1_size;++i) {
				int c = compare(automaton,c1.get(i),c2.get(i));
				if(c != 0) {
					return c;
				}
			}
			return 0;
		}		
	}	
}
