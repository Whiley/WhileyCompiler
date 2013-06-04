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
		
		// second, harder (potentially recursive case);
		switch(s1.kind) {
		case Solver.K_Var:	
			return compareVar(automaton,(Automaton.Term)s1,(Automaton.Term)s2);
		case Solver.K_Load:
			return compareLoad(automaton,(Automaton.Term)s1,(Automaton.Term)s2);
		case Solver.K_Fn:
			return compareFn(automaton,(Automaton.Term)s1,(Automaton.Term)s2);
		case Solver.K_LengthOf:
			return compareLength(automaton,(Automaton.Term)s1,(Automaton.Term)s2);
		}
		
		throw new IllegalArgumentException("Unknown variable expression encountered!");
	}
	
	private static int compareVar(Automaton automaton, Automaton.Term r1, Automaton.Term r2) {
		Automaton.Strung s1 = (Automaton.Strung) automaton.get(r1.contents);
		Automaton.Strung s2 = (Automaton.Strung) automaton.get(r2.contents);
		return s1.compareTo(s2);
	}
	
	private static int compareLoad(Automaton automaton, Automaton.Term r1,
			Automaton.Term r2) {
		Automaton.List l1 = (Automaton.List) automaton.get(r1.contents);
		Automaton.List l2 = (Automaton.List) automaton.get(r2.contents);
		Automaton.Int i1 = (Automaton.Int) automaton.get(l1.get(1));
		Automaton.Int i2 = (Automaton.Int) automaton.get(l2.get(1));
		int c = i1.compareTo(i2);
		if (c != 0) {
			return c;
		} else {
			return compare(automaton, l1.get(0), l2.get(0));
		}
	}
	
	private static int compareFn(Automaton automaton, Automaton.Term r1, Automaton.Term r2) {
		Automaton.List l1 = (Automaton.List) automaton.get(r1.contents);
		Automaton.List l2 = (Automaton.List) automaton.get(r2.contents);
		
		if(l1.size() < l2.size()) {
			return -1;
		} else if(l1.size() > l2.size()) {
			return 1;
		}
		
		Automaton.Strung s1 = (Automaton.Strung) automaton.get(l1.get(0));
		Automaton.Strung s2 = (Automaton.Strung) automaton.get(l2.get(0));
		int c = s1.compareTo(s2);
		if (c != 0) { return c; }
		
		for(int i=1;i!=l1.size();++i) {
			c = compare(automaton, l1.get(i), l2.get(i));
			if(c != 0) {
				return c;
			}
		}
		
		return 0;		
	}
	
	private static int compareLength(Automaton automaton, Automaton.Term r1, Automaton.Term r2) {
		return compare(automaton,r1.contents,r2.contents);
	}
}
