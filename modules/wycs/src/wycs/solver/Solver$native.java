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
	
	// Computes the (static) reference to the null state.
	private static final int NULL = Automaton.K_FREE - Solver.K_Null;

	public static Automaton.Term bind(Automaton automaton, Automaton.List args) {

		// TODO: add support for generating multiple bindings (rather than just
		// returning the first found as we currently do)

		int result = find(automaton, args.get(0), args.get(1), args.get(2));
		return (Automaton.Term) automaton.get(result);
	}

	/**
	 * <p>
	 * Traverse the automaton attempting to find a binding (if one exists). In
	 * essence, we descend the parameterised expression e2 looking for trigger
	 * points. These are terms of the same kind as the concrete expression we
	 * are attempting to bind against. Once a trigger is found, we proceed to
	 * try and bind against it. If the binding succeeds, this is returned
	 * immediately. Otherwise, the search continues until all options are
	 * exhausted.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> this is an expensive operation. To mitigate this, we
	 * minimise the number of potential trigger points by only descending
	 * logical operators, but not through general expressions. This limits the
	 * power of quantifier instantiation, but is a reasonable restriction at
	 * this stage.
	 * </p>
	 * 
	 * @param automaton
	 *            The automaton we're traversing.
	 * @param r1
	 *            The concrete expression we are binding against
	 * @param v
	 *            The variable we're attempting to bind
	 * @param r2
	 *            The expression parameterised against v through which are are
	 *            searching for a trigger to try and bind against.
	 * @return
	 */
	private static int find(Automaton automaton, int r1, int v, int r2) {
		Automaton.State s1 = automaton.get(r1);
		Automaton.State s2 = automaton.get(r2);

		if (s1.kind == s2.kind) {
			// This indicates a potential trigger point, so attempt to bind.
			return bind(automaton, r1, v, r2);
		} else {
			// This is not a trigger point. Therefore, if its a logical
			// expression continue the search through the expression; Otherwise,
			// give up.
			switch (s2.kind) {
			case Solver.K_Not: {
				Automaton.Term t2 = (Automaton.Term) s2;
				return find(automaton, r1, v, t2.contents);
			}
			case Solver.K_And:
			case Solver.K_Or: {
				Automaton.Term t2 = (Automaton.Term) s2;
				Automaton.Set s2_children = (Automaton.Set) automaton
						.get(t2.contents);
				int s2_size = s2_children.size();
				for (int i = 0; i != s2_size; ++i) {
					int s2_child = s2_children.get(i);
					int r = find(automaton, r1, v, s2_child);
					if (r != NULL) {
						// binding found.
						return r;
					}
				}
			}
			}

			// Give up
			return NULL;
		}
	}

	/**
	 * Traverse the automaton attempting to match e2 against e1 up to v. This
	 * means that e2 must be identical to e1 in every respect, except when v is
	 * encountered. In such case, the remainder of e1 is returned. Furthermore,
	 * in situations where multiple binds calls are made, then the return of
	 * each must be identical else Null is returned.
	 * 
	 * @param automaton
	 *            The automaton we're traversing.
	 * @param r1
	 *            The concrete expression we are binding against
	 * @param v
	 *            The variable we're attempting to bind
	 * @param r2
	 *            The expression parameterised against v we're using to guide
	 *            the binding of v
	 * @return
	 */
	private static int bind(Automaton automaton, int r1, int v, int r2) {
		if (r1 == r2) {
			// This indicates we've encountered two identical expressions,
			// neither of which can contain the variable we're binding.
			// Hence, binding fails!
			return NULL;
		} else if (r2 == v) {
			// This indicates we've hit the variable parameter, which is the
			// success condition. Everything in the concrete expression is thus
			// matched
			return r1;
		}

		Automaton.State s1 = automaton.get(r1);
		Automaton.State s2 = automaton.get(r2);

		// Start with easy cases.
		if (s1.kind != s2.kind) {
			// This indicates two non-identical states with different kind. No
			// binding is possible here, and so binding fails.
			return NULL;
		} else if (s1 instanceof Automaton.Bool || s1 instanceof Automaton.Int
				|| s1 instanceof Automaton.Strung) {
			// These are all atomic states which have different values (by
			// construction). Therefore, no binding is possible.
			return NULL;
		} else if (s1 instanceof Automaton.Term) {
			Automaton.Term t1 = (Automaton.Term) s1;
			Automaton.Term t2 = (Automaton.Term) s2;
			// In this case, we have two non-identical terms of the same
			// kind and, hence, we must continue traversing the automaton
			// in an effort to complete the binding.
			return bind(automaton, t1.contents, v, t2.contents);
		} else {
			Automaton.Collection c1 = (Automaton.Collection) s1;
			Automaton.Collection c2 = (Automaton.Collection) s2;
			int c1_size = c1.size();

			if (c1_size != c2.size()) {
				// Here, we have collections of different size and, hence,
				// binding must fail.
				return NULL;
			} else if (s1 instanceof Automaton.List) {
				Automaton.List l1 = (Automaton.List) c1;
				Automaton.List l2 = (Automaton.List) c2;
				return bind(automaton, l1, v, l2);
			} else if (s1 instanceof Automaton.Set) {
				Automaton.Set t1 = (Automaton.Set) s1;
				Automaton.Set t2 = (Automaton.Set) s2;
				return bind(automaton, t1, v, t2);
			} else {
				Automaton.Bag b1 = (Automaton.Bag) s1;
				Automaton.Bag b2 = (Automaton.Bag) s2;
				// TODO: need to implement this case
				return NULL;
			}
		}
	}

	static private int bind(Automaton automaton, Automaton.List l1, int v,
			Automaton.List l2) {
		// Lists are the easiest to handle, because we can perform a
		// linear comparison.
		int result = NULL;
		int l1_size = l1.size();

		for (int i = 0; i != l1_size; ++i) {
			int lr1 = l1.get(i);
			int lr2 = l2.get(i);

			if (lr1 != lr2) {
				// Here, we have non-identical elements at the same
				// position. Therefore, we need to traverse them to look
				// for a binding.
				int r = bind(automaton, lr1, v, lr2);

				if (r == NULL || (result != NULL && result != r)) {
					// No binding possible, so terminate early.
					return NULL;
				} else {
					// Otherwise, we have a candidate binding.
					result = r;
				}
			}
		}

		return result;
	}

	static private int bind(Automaton automaton, Automaton.Set s1, int v,
			Automaton.Set s2) {
		int result = NULL;
		int s1_size = s1.size();

		// TODO: performance of this loop could potentially be improved
		// by e.g. exploiting the fact that identical nodes are likely
		// to be in the same position in both collections.

		// FIXME: there is also an inherent limitation of the following
		// loop, in that it does not explore all possible bindings. In
		// particular, the first valid binding encountered is the only
		// one considered.

		for (int i = 0; i != s1_size; ++i) {
			int s1_child = s1.get(i);
			boolean matched = false;

			for (int j = 0; j != s1_size; ++j) {
				int s2_child = s2.get(j);
				if (s1_child == s2_child) {
					matched = true;
					break;
				} else {
					int r = bind(automaton, s1_child, v, s2_child);
					if (r != NULL && (r == result || result == NULL)) {
						result = r;
						matched = true;
						break;
					}
				}
			}
			if (!matched) {
				// Indicates no binding found for the given element.
				return NULL;
			}
		}

		return result;
	}
}
