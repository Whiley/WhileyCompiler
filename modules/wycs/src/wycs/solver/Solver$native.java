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

import java.util.ArrayList;

import wyautl.core.Automaton;

/**
 * Implements a lexiographic ordering of variable expressions.
 * 
 * @author David J. Pearce
 *
 */
public class Solver$native {
	
	/**
	 * Determine the minimum element from a bag of elements according to an
	 * internal ordering defined here.
	 * 
	 * @param automaton
	 *            The automaton being operated over
	 * @param rBag
	 *            A reference to a bag state which contains those elements to
	 *            compare.
	 * @return
	 */
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
	
	/**
	 * Determine the maximum element from a bag of elements according to an
	 * internal ordering defined here.
	 * 
	 * @param automaton
	 *            The automaton being operated over
	 * @param rBag
	 *            A reference to a bag state which contains those elements to
	 *            compare.
	 * @return
	 */
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
	
	/**
	 * Implements the internal ordering of automaton states.
	 * 
	 * @param automaton
	 * @param r1
	 *            Reference to first state to compare
	 * @param r2
	 *            Reference to second state to compare
	 * @return
	 */
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
	

	/**
	 * <p>
	 * Attempt to bind a quantified expression with a concrete expression,
	 * producing one or more candidate bindings over one or more quantified
	 * variables. Any such bindings are then used to instantiate the quantified
	 * expression. For example, consider these expressions:
	 * </p>
	 * 
	 * <pre>
	 * in(1) && forall y . !in(y)
	 * </pre>
	 * 
	 * <p>
	 * Here, we want to instantiate the quantifier with the binding
	 * <code>y=1</code> to produce a contradiction. This function will be called
	 * to bind <code>in(1)</code> against <code>in(y)</code> over the set
	 * <code>{y}</code> of quantified variables. In this case, there is only one
	 * candidate binding (i.e. <code>y=1</code>). When a binding is found the
	 * quantified expression (<code>!in(y)</code>) is instantiated after
	 * substituting all quantified variables according to the binding.
	 * Therefore, we instantiate <code>!in(y)[y/1]</code> which gives
	 * <code>!in(1)</code>.
	 * </p>
	 * <p>
	 * In many cases, there will be no possible candidate bindings. For example,
	 * attempting to bind <code>in(1)</code> with <code>out(y)</code> will fail
	 * because the predicates differ. Likewise, attempting to bind
	 * <code>in(1,2)</code> against <code>in(x,x)</code> will fail because there
	 * is no binding for <code>x</code> which can reproduce <code>in(1,2)</code>
	 * .
	 * </p>
	 * <p>
	 * When given the concrete and quantified expressions, this function must
	 * first identify <i>triggers</i> on which binding can be performed. A
	 * trigger is a matching clause in both the concrete and quantified
	 * expressions which can be used to bind the quantified variables. To
	 * understand this, consider a more complex example:
	 * </p>
	 * 
	 * <pre>
	 * in(1) && !out(1) && forall x . in(x) ==> out(x)
	 * </pre>
	 * 
	 * <p>
	 * Let's assume for this example the concrete expression is
	 * <code>in(1)</code>. Now, we cannot the quantified expression as a whole
	 * against this concrete expression, because there is no possible binding
	 * for <code>x</code> which will reproduce <code>in(1)</code> from
	 * <code>in(x) ==> out(x)</code>. Instead, we need to traverse the
	 * quantified expression looking for an appropriate trigger to use which, in
	 * this case, is <code>in(x)</code>. Having found a trigger, we can then use
	 * it to try and construct an appropriate binding and, if successful,
	 * instantiate the quantified expression as a whole.
	 * </p>
	 * 
	 * <p>
	 * Finally, it can happen that there are multiple possible bindings for any
	 * given concrete/quantified expression pairing, and this function should
	 * return them all. Here is one example:
	 * </p>
	 * 
	 * <pre>
	 * in(1,2) && forall x . !in(1,x) ==> !in(x,2)
	 * </pre>
	 * <p>
	 * In this case there are two valid bindings, namely <code>x=1</code> and
	 * <code>x=2</code>. However, only one of these bindings will produce the
	 * contradiction and, hence, it is critical to explore all possible
	 * bindings.
	 * </p>
	 * 
	 * @param automaton
	 *            The automaton being operated over.
	 * @param args
	 *            The arguments list, which consists of the concrete expression,
	 *            the list of quantified variables and the quantified
	 *            expression.
	 * @return
	 */
	public static Automaton.Set instantiate(Automaton automaton,
			Automaton.List args) {

		int concreteExpression = args.get(0);
		int quantifiedExpression = args.get(2);
		Automaton.Set quantifiedVarSet = (Automaton.Set) automaton.get(args.get(1));
		
		// Construct a simple way to identified quantified variables
		boolean[] quantifiedVariables = new boolean[automaton.nStates()];
		for(int i=0;i!=quantifiedVarSet.size();++i) {
			Automaton.List tuple = (Automaton.List) automaton.get(quantifiedVarSet.get(i));
			int qvar = tuple.get(0);
			quantifiedVariables[qvar] = true;
		}

		// Construct a list into which each completed binding is placed. Each
		// binding is a mapping from automaton states representing quantified
		// variables from e2 to their concrete counterparts from e1.
		ArrayList<int[]> bindings = new ArrayList<int[]>();

		// Attempt to find as many bindings as possible. This is a
		// potentially expensive operation when the quantified expression is
		// large and/or there are a large number of quantified variables.
		find(automaton, concreteExpression, quantifiedExpression,
				quantifiedVariables, bindings);

		// If one or more bindings have been computed, then apply them to the
		// quantified expression to produce one or more instantiated expressions.
		int bindings_size = bindings.size();
		if (bindings_size > 0) {
			// Apply the substitution for the each binding to produce o given
			// instantiation.
			int[] instances = new int[bindings_size];

			for (int i = 0; i != bindings_size; ++i) {
				instances[i] = automaton.substitute(quantifiedExpression,
						bindings.get(i));
			}

			return new Automaton.Set(instances);
		} else {
			// No bindings found, so just return empty set
			return Automaton.EMPTY_SET;
		}
	}

	// Computes the (static) reference to the null state.
	private static final int NULL = Integer.MIN_VALUE;

	/**
	 * <p>
	 * Traverse the automaton attempting to find a binding (if one exists). In
	 * essence, we descend the quantified expression looking for trigger points.
	 * These are terms of the same kind as the concrete expression we are
	 * attempting to bind against. Once a trigger is found, we proceed to try
	 * and bind against it. If the binding succeeds, this is added to the list
	 * of bindings. Eitherway, the search continues until all options are
	 * exhausted.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> this is an expensive operation. To mitigate this, we
	 * minimise the number of potential trigger points by only descending
	 * logical operators, but not through general expressions. This limits the
	 * power of quantifier instantiation, but seems a reasonable restriction at
	 * this stage.
	 * </p>
	 * 
	 * @param automaton
	 *            The automaton we're traversing.
	 * @param concreteExpression
	 *            The concrete expression we are binding against
	 * @param quantifiedExpression
	 *            The expression parameterised against v we're using to guide
	 *            the binding of quantified variables
	 * @param quantifiedVariables
	 *            The automaton states representing the quantified variables in
	 *            the quantified expression.
	 * @param bindings
	 *            The current list of mappings from automaton states
	 *            representing quantified variables to concrete states.
	 * @return
	 */
	private static void find(Automaton automaton, int concreteExpression,
			int quantifiedExpression, boolean[] quantifiedVariables,
			ArrayList<int[]> bindings) {

		Automaton.State concreteState = automaton.get(concreteExpression);
		Automaton.State quantifiedState = automaton.get(quantifiedExpression);

		if (concreteState.kind == quantifiedState.kind) {
			// This indicates a potential trigger point, so attempt to bind.
			bind(automaton, concreteExpression, quantifiedExpression,
					quantifiedVariables, bindings);
		} else {
			// This is not a trigger point. Therefore, if its a logical
			// expression continue the search through the expression; Otherwise,
			// give up.
			switch (quantifiedState.kind) {
			case Solver.K_Not: {
				Automaton.Term t2 = (Automaton.Term) quantifiedState;
				find(automaton, concreteExpression, t2.contents,
						quantifiedVariables, bindings);
				break;
			}
			case Solver.K_And:
			case Solver.K_Or: {
				Automaton.Term t2 = (Automaton.Term) quantifiedState;
				Automaton.Set s2_children = (Automaton.Set) automaton
						.get(t2.contents);
				int s2_size = s2_children.size();
				for (int i = 0; i != s2_size; ++i) {
					int s2_child = s2_children.get(i);
					// FIXME: There's a bug here in some sense because
					// we might generate a binding which covers one term from a
					// conjunction or disjunction but not all terms.
					find(automaton, concreteExpression, s2_child,
							quantifiedVariables, bindings);
				}
				break;
			}
			}
		}
	}

	private static void bind(Automaton automaton, int concreteExpression,
			int triggerExpression, boolean[] quantifiedVariables,
			ArrayList<int[]> bindings) {
		
		// First, construct candidate binding
		int[] binding = new int[automaton.nStates()];
		
		for(int i=0;i!=binding.length;++i) {
			if(quantifiedVariables[i]) {
				binding[i] = NULL;
			} else {
				binding[i] = i;
			}
		}
		
		// Second, attempt to construct a binding
		boolean bound = bind(automaton,concreteExpression,triggerExpression,quantifiedVariables,binding);
		
		// Third, if successful add to list of candidates
		if(bound) {
			bindings.add(binding);
		}
	}
	
	/**
	 * Traverse the automaton attempting to match a quantified expression
	 * against a concrete expression over a set of quantified variables. The
	 * quantified expression must be identical to the concrete expression in
	 * every respect up to the quantified variables. The remaining portions of
	 * the concrete expression are then used to form the binding for the
	 * quantified variables. This binding can still fail if an attempt is made
	 * to bind one variable to multiple distinct pieces of the concrete
	 * expression.
	 * 
	 * @param automaton
	 *            The automaton we're traversing.
	 * @param concreteRef
	 *            A reference into the concrete expression we're binding
	 *            against.
	 * @param triggerRef
	 *            A reference into the quantified expression we're binding
	 *            against.
	 * @param quantifiedVariables
	 *            The automaton states representing the quantified variables in
	 *            the quantified expression.
	 * @return
	 */
	private static boolean bind(Automaton automaton, int concreteRef,
			int triggerRef, boolean[] quantifiedVariables,
			int[] binding) {
		
		// TODO: For the moment, this function can only produce one binding.
		// However, for completeness, it needs to be able to produce multiple
		// bindings.
		
		if (concreteRef == triggerRef) {
			// This indicates we've encountered two identical expressions,
			// neither of which can contain the variables we're binding.
			// Hence, binding fails!
			return false;
		} else if (quantifiedVariables[triggerRef]) {
			// This indicates we've hit a quantified variable, and we must
			// attempt to update the binding accordingly.
			int current = binding[triggerRef];
			
			if(current != NULL && current != concreteRef) {
				// In this case, the binding we've found conflicts with a
				// previously established binding of the same variable.
				// Therefore, no valid binding is possible and we must fail.
				return false;
			} else if(current == NULL){
				// In this case, there was no previous binding for this
				// variable so we establish one now.
				binding[triggerRef] = concreteRef;
			}
			return true;
		}

		Automaton.State concreteState= automaton.get(concreteRef);
		Automaton.State triggerState = automaton.get(triggerRef);

		// Start with easy cases.
		if (concreteState.kind != triggerState.kind) {
			// This indicates two non-identical states with different kind. No
			// binding is possible here, and so binding fails.
			return false;
		} else if (concreteState instanceof Automaton.Bool || concreteState instanceof Automaton.Int
				|| concreteState instanceof Automaton.Strung) {
			// These are all atomic states which have different values (by
			// construction). Therefore, no binding is possible.
			return false;
		} else if (concreteState instanceof Automaton.Term) {
			Automaton.Term concreteTerm = (Automaton.Term) concreteState;
			Automaton.Term triggerTerm = (Automaton.Term) triggerState;
			// In this case, we have two non-identical terms of the same
			// kind and, hence, we must continue traversing the automaton
			// in an effort to complete the binding.
			return bind(automaton, concreteTerm.contents, triggerTerm.contents,
					quantifiedVariables, binding);
		} else {
			Automaton.Collection concreteCollection = (Automaton.Collection) concreteState;
			Automaton.Collection triggerCollection = (Automaton.Collection) triggerState;

			if (concreteState instanceof Automaton.List) {
				Automaton.List concreteList = (Automaton.List) concreteCollection;
				Automaton.List triggerList = (Automaton.List) triggerCollection;
				return bind(automaton, concreteList, triggerList, quantifiedVariables, binding);
			} else if (concreteState instanceof Automaton.Set) {
				Automaton.Set concreteSet = (Automaton.Set) concreteState;
				Automaton.Set triggerSet = (Automaton.Set) triggerState;
				return bind(automaton, concreteSet, triggerSet, quantifiedVariables, binding);				
			} else {
				Automaton.Bag b1 = (Automaton.Bag) concreteState;
				Automaton.Bag b2 = (Automaton.Bag) triggerState;
				// TODO: need to implement this case
				return false;
			}
		}
	}

	static private boolean bind(Automaton automaton, Automaton.List concreteList,
			Automaton.List triggerList, boolean[] quantifiedVariables, int[] binding) {
		// Lists are the easiest to handle, because we can perform a
		// linear comparison.
		int l1_size = concreteList.size();
		
		if (l1_size != triggerList.size()) {
			// Here, we have lists of different size and, hence,
			// binding must fail.
			return false;
		} 
		
		for (int i = 0; i != l1_size; ++i) {
			int lr1 = concreteList.get(i);
			int lr2 = triggerList.get(i);

			if (lr1 != lr2) {
				// Here, we have non-identical elements at the same
				// position. Therefore, we need to traverse them to look
				// for a binding.
				boolean bound = bind(automaton, lr1, lr2, quantifiedVariables,
						binding);

				if (!bound) {
					return false;
				}
			}
		}

		return true;
	}

	static private boolean bind(Automaton automaton, Automaton.Set concreteSet,
			Automaton.Set triggerSet, boolean[] quantifiedVariables,
			int[] binding) {

		// Note, concrete and trigger sets do not have to have same size here.
		int concreteSize = concreteSet.size();
		int triggerSize = triggerSet.size();
				
		// TODO: performance of this loop could potentially be improved
		// by e.g. exploiting the fact that identical nodes are likely
		// to be in the same position in both collections.

		// FIXME: there is also an inherent limitation of the following
		// loop, in that it does not explore all possible bindings. In
		// particular, the first valid binding encountered is the only
		// one considered.

		// NOTE: this matches every trigger child with a concrete child, but not
		// vice-versa.  See #379.
		
		for (int i = 0; i != triggerSize; ++i) {
			int trigger_child = triggerSet.get(i);
			boolean matched = false;

			for (int j = 0; j != concreteSize; ++j) {
				int concrete_child = concreteSet.get(j);
				if (concrete_child == trigger_child) {
					matched = true;
					break;
				} else {
					boolean bound = bind(automaton, concrete_child, trigger_child,
							quantifiedVariables, binding);
					if (bound) {
						matched = true;
						break;
					}
				}
			}
			if (!matched) {
				// Indicates no binding found for the given element.
				return false;
			}
		}

		return true;
	}
}
