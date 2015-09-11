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
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;

import wyautl.core.Automaton;
import wyautl.util.BigRational;

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
			if (least == -1 || compare(automaton, child, least) < 0) {
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
			if (greatest == -1 || compare(automaton, child, greatest) > 0) {
				greatest = child;
			}
		}
		return (Automaton.Term) automaton.get(greatest);
	}

	public static Automaton.Term maxMultiplicand(Automaton automaton, int rBag) {

		Automaton.Bag bag = (Automaton.Bag) automaton.get(rBag);
		int greatest = -1;
		for (int i = 0; i != bag.size(); ++i) {
			Automaton.Term mulTerm = (Automaton.Term) automaton.get(bag.get(i));
			Automaton.List mulChildren = (Automaton.List) automaton
					.get(mulTerm.contents);
			Automaton.Bag mulChildChildren = (Automaton.Bag) automaton
					.get(mulChildren.get(1));
			if (mulChildChildren.size() == 1) {
				int child = mulChildChildren.get(0);
				if (greatest == -1 || compare(automaton, child, greatest) > 0) {
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

		if(s1 instanceof Automaton.Constant) {
			Automaton.Constant<Comparable> b1 = (Automaton.Constant) s1;
			Automaton.Constant<Comparable> b2 = (Automaton.Constant) s2;
			return b1.value.compareTo(b2.value);
		} else if(s1 instanceof Automaton.Term) {
			Automaton.Term t1 = (Automaton.Term) s1;
			Automaton.Term t2 = (Automaton.Term) s2;
			int t1_contents = t1.contents;
			int t2_contents = t2.contents;
			if(t1_contents == Automaton.K_VOID) {
				return t2_contents == Automaton.K_VOID ? 0 : -1;
			} else if(t2_contents == Automaton.K_VOID) {
				return 1;
			} else {
				return compare(automaton,t1.contents,t2.contents);
			}
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

	public static Automaton.Real gcd(Automaton automaton, Automaton.List args) {
		// PRECONDITION: terms.size() > 0
		Automaton.Real constant = (Automaton.Real) automaton.get(args.get(0));
		Automaton.Bag terms = (Automaton.Bag) automaton.get(args.get(1));

		// Must use abs() here, otherwise can end up with negative gcd.
		// This is problematic for inequalities as it necessitate
		// changing their sign.
		BigRational gcd = constant.value.abs();

		if(gcd.equals(BigRational.ZERO)) {
			// Basically, if there is no coefficient, then ignore it.
			gcd = null;
		}

		// Now, iterate through each term examining its coefficient and
		// determining the GreatestCommonDivisor of the whole lot.
		for(int i=0;i!=terms.size();++i) {
			int child = terms.get(i);
			Automaton.Term mul = (Automaton.Term) automaton.get(child);
			Automaton.List ls = (Automaton.List) automaton.get(mul.contents);
			Automaton.Real coefficient = (Automaton.Real) automaton.get(ls.get(0));
			BigRational val = coefficient.value;
			if(gcd == null) {
				// Must use abs() here, otherwise can end up with negative gcd.
				// This is problematic for inequalities as it necessitate
				// changing their sign.
				gcd = val.abs();
			} else {
				// Note, gcd of two numbers (either of which may be negative) is
				// always positive.
				gcd = gcd.gcd(val);
			}
		}

		if(gcd == null || gcd.equals(BigRational.ZERO)) {
			// This is basically a sanity check. A zero coefficient is possible,
			// and can cause the final gcd to be zero. Likewise, it's possible
			// (at the moment) that this function can be called with
			// terms.size() == 0 and, hence, gcd == null.
			return new Automaton.Real(BigRational.ONE);
		} else {
			// Done.
			return new Automaton.Real(gcd);
		}
	}

	/**
	 * Determine whether a given variable v is contained within a given
	 * expression e.
	 *
	 * @param automaton
	 * @param args
	 * @return
	 */
	public static boolean contains(Automaton automaton, Automaton.List args) {
		int e = (int) args.get(0);
		int v = (int) args.get(1);
		return contains(automaton,e,v);
	}

	public static boolean contains(Automaton automaton, int e, int v) {
		if(e == v) { return true; }

		Automaton.State s1 = automaton.get(e);

		if(s1 instanceof Automaton.Constant) {
			return false;
		} else if(s1 instanceof Automaton.Term) {
			Automaton.Term t1 = (Automaton.Term) s1;
			if(t1.contents != Automaton.K_VOID) {
				return contains(automaton,t1.contents,v);
			}
			return false;
		} else {
			Automaton.Collection c1 = (Automaton.Collection) s1;
			for(int i=0;i!=c1.size();++i) {
				int child = c1.get(i);
				if(contains(automaton,child,v)) {
					return true;
				}
			}
			return false;
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
		Automaton.Set quantifiedVarSet = (Automaton.Set) automaton.get(args
				.get(1));

		// Construct a simple way to identified quantified variables
		boolean[] quantifiedVariables = markQuantifiedVariables(automaton,quantifiedVarSet);
		boolean[] quantifiedTriggers = markTriggers(automaton,quantifiedExpression);
		boolean[] concreteTriggers = markTriggers(automaton,concreteExpression);		
				
		// Attempt to find as many bindings as possible. This is a
		// potentially expensive operation when the quantified expression is
		// large and/or there are a large number of quantified variables.
		ArrayList<Binding> bindings = generateBindings(automaton,quantifiedVariables,quantifiedTriggers,concreteTriggers); 		
		
		// If one or more bindings have been computed, then apply them to the
		// quantified expression to produce one or more instantiated
		// expressions.
		if (bindings.size() > 0) {
			return instantiateQuantifiedExpression(automaton,quantifiedExpression,bindings);
		} else {
			// No bindings found, so just return empty set. This is an
			// optimisation because the case of no bindings has high probability
			// and we want to avoid creating a new set every time.
			return Automaton.EMPTY_SET;
		}
	}
	
	/**
	 * Mark those states in the automaton which correspond to quantified
	 * variables. This provides an easy way to identify them during traversal of
	 * the automaton.
	 * 
	 * @param automaton
	 *            --- Automaton in question
	 * @param quantifiedVarSet
	 *            --- Set of variables from quantifier.
	 * @return
	 */
	private static boolean[] markQuantifiedVariables(Automaton automaton, Automaton.Set quantifiedVarSet) {
		boolean[] quantifiedVariables = new boolean[automaton.nStates()];
		for (int i = 0; i != quantifiedVarSet.size(); ++i) {
			Automaton.List tuple = (Automaton.List) automaton
					.get(quantifiedVarSet.get(i));
			int qvar = tuple.get(0);
			quantifiedVariables[qvar] = true;
		}
		return quantifiedVariables;
	}

	/**
	 * Substitute the given bindings into the quantified expression producing one
	 * or more concrete instantiations of the expression.
	 * 
	 * @param automaton 
	 * @param quantifiedExpression --- Expression being instantiated.
	 * @param bindings --- List of bindings
	 * @return
	 */
	private static Automaton.Set instantiateQuantifiedExpression(Automaton automaton, int quantifiedExpression,
			ArrayList<Binding> bindings) {
		// Apply the substitution for the each binding to produce o given
		// instantiation.
		int bindings_size = bindings.size();
		int[] instances = new int[bindings_size];
		int index = 0;
		
		for (int i = 0; i != bindings_size; ++i) {
			Binding binding = bindings.get(i);
			if (binding.numberUnbound == 0) {
				instances[index++] = automaton.substitute(
						quantifiedExpression, binding.binding);
			} else {
				instances = Arrays.copyOfRange(instances, 0,
						instances.length - 1);
			}
		}

		return new Automaton.Set(instances);
	}
	
	/**
	 * Traverse the automaton from the given root looking for any "triggers".
	 * These are instances of a particular term type (e.g. index of)
	 * 
	 * @param automaton
	 *            --- The automaton we're traversing.
	 * @param root
	 *            --- State we're traversing from
	 * @return
	 */
	private static boolean[] markTriggers(Automaton automaton, int root) {
		boolean[] triggers = new boolean[automaton.nStates()];
		boolean[] visited = new boolean[automaton.nStates()];
		markTriggers(automaton,root,triggers,visited);		
		return triggers;
	}
	
	/**
	 * Traverse automaton from given root marking any states which are triggers.
	 * 
	 * @param automaton
	 *            --- Automaton being traversed
	 * @param root
	 *            --- State to traverse from
	 * @param triggers
	 *            --- Marked triggers
	 */
	private static void markTriggers(Automaton automaton, int root, boolean[] triggers, boolean[] visited) {		
		Automaton.State state = automaton.get(root);		
		if(root >= 0 && !visited[root]) {
			visited[root] = true;
			triggers[root] |= isTrigger(state.kind);
		} else {
			return;
		}
		// Now, continue traversing as far as possible
		
		switch(state.kind) {
		case Automaton.K_BOOL:
		case Automaton.K_INT:		
		case Automaton.K_REAL:
		case Automaton.K_STRING:
			break;
		case Automaton.K_SET:
		case Automaton.K_BAG:
		case Automaton.K_LIST: {
			Automaton.Collection c = (Automaton.Collection) state;
			for(int i=0;i!=c.size();++i) {
				markTriggers(automaton,c.get(i),triggers,visited);
			}
			break;
		}
		default: {
			Automaton.Term term = (Automaton.Term) state;
			if(term.contents != Automaton.K_VOID) {
				markTriggers(automaton,term.contents,triggers,visited);
			}
		}			
		}
	}

	/**
	 * Determine whether a given term kind is a trigger or not. Currently, this
	 * is hard-coded but it need not be.
	 * 
	 * @param kind
	 * @return
	 */
	private static boolean isTrigger(int kind) {
		return kind == Solver.K_IndexOf;
	}
	
	/**
	 * Generate all possible bindings by attempting to bind each quantified
	 * trigger with each concrete trigger.
	 * 
	 * @param automaton
	 * @param quantifiedVariables
	 * @param quantifiedTriggers
	 * @param concreteTriggers
	 * @return
	 */
	private static ArrayList<Binding> generateBindings(Automaton automaton, boolean[] quantifiedVariables,
			boolean[] quantifiedTriggers, boolean[] concreteTriggers) {
		ArrayList<Binding> bindings = new ArrayList<Binding>();
		
		for (int i = 0; i != quantifiedTriggers.length; ++i) {
			if (quantifiedTriggers[i]) {
				for (int j = 0; j != concreteTriggers.length; ++j) {
					if (concreteTriggers[j]) {
						// FIXME: I feel like the following could be improved.
						// Certainly, for matching only IndexOf expressions the
						// full bind function is unnecessary. However, for the
						// general case, we would need it. Furthermore, this
						// approach won't guarantee to bind all quantified
						// variables (though it might still work out as it'll
						// pick them off one at a time).
						ArrayList<Binding> localBindings = new ArrayList<Binding>();
						localBindings.add(new Binding(quantifiedVariables));
						bind(automaton, j, i, quantifiedVariables, localBindings);
						bindings.addAll(localBindings);
					}
				}
			}
		}

		return bindings;
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
	private static void bind(Automaton automaton, int concreteRef,
			int triggerRef, boolean[] quantifiedVariables,
			ArrayList<Binding> bindings) {
		
		// TODO: For the moment, this function can only produce one binding.
		// However, for completeness, it needs to be able to produce multiple
		// bindings.

		if (concreteRef == triggerRef) {
			// This indicates we've encountered two identical expressions,
			// neither of which can contain the variables we're binding.
			// Hence, there is no benefit from continuing.
			return;
		} else if (triggerRef >= 0 && quantifiedVariables[triggerRef]) {
			// This indicates we've hit a quantified variable, and we must
			// attempt to update all bindings accordingly.
			for (int i = 0; i != bindings.size(); ++i) {
				Binding binding = bindings.get(i);
				if (!binding.bind(concreteRef, triggerRef)) {
					// This binding failed, so discard
					bindings.remove(i);
					i = i - 1;
				}
			}

			return;
		}

		// Otherwise, we are still hunting down one or more quantified variables
		// and attempting to bind them.

		Automaton.State concreteState = automaton.get(concreteRef);
		Automaton.State triggerState = automaton.get(triggerRef);

		// Start with easy cases.
		if (concreteState.kind != triggerState.kind) {
			// This indicates two non-identical states with different kind. No
			// binding is possible here, and so all bindings we are exploring
			// fail.
			bindings.clear();
		} else if (concreteState instanceof Automaton.Constant) {
			// These are all atomic states which have different values (by
			// construction). At this stage, we ignore such minor differences to
			// enable more potential bindings to be created. 			
		} else if (concreteState instanceof Automaton.Term) {
			Automaton.Term concreteTerm = (Automaton.Term) concreteState;
			Automaton.Term triggerTerm = (Automaton.Term) triggerState;
			// In this case, we have two non-identical terms of the same
			// kind and, hence, we must continue traversing the automaton
			// in an effort to complete the binding.
			bind(automaton, concreteTerm.contents, triggerTerm.contents,
					quantifiedVariables, bindings);
		} else {
			Automaton.Collection concreteCollection = (Automaton.Collection) concreteState;
			Automaton.Collection triggerCollection = (Automaton.Collection) triggerState;

			if (concreteState instanceof Automaton.List) {
				Automaton.List concreteList = (Automaton.List) concreteCollection;
				Automaton.List triggerList = (Automaton.List) triggerCollection;
				bind(automaton, concreteList, triggerList, quantifiedVariables,
						bindings);
			} else if (concreteState instanceof Automaton.Set) {
				Automaton.Set concreteSet = (Automaton.Set) concreteState;
				Automaton.Set triggerSet = (Automaton.Set) triggerState;
				bindSetOrBag(automaton, concreteSet, triggerSet, quantifiedVariables,
						bindings);
			} else {
				Automaton.Bag concreteBag = (Automaton.Bag) concreteState;
				Automaton.Bag triggerBag = (Automaton.Bag) triggerState;
				bindSetOrBag(automaton, concreteBag, triggerBag, quantifiedVariables,
						bindings);
			}
		}
	}

	static private void bind(Automaton automaton, Automaton.List concreteList,
			Automaton.List triggerList, boolean[] quantifiedVariables,
			ArrayList<Binding> bindings) {
		// Lists are the easiest to handle, because we can perform a
		// linear comparison.
		int l1_size = concreteList.size();

		if (l1_size != triggerList.size()) {
			// Here, we have lists of different size and, hence,
			// all bindings being explored must fail.
			bindings.clear();
		} else {
			// In this case, we need to explore each child in sequence to see
			// whether or not a suitable binding can be determined.
			for (int i = 0; i != l1_size; ++i) {
				int lr1 = concreteList.get(i);
				int lr2 = triggerList.get(i);

				if (lr1 != lr2) {
					// Here, we have non-identical elements at the same
					// position. Therefore, we traverse them to look to update
					// the bindings we are exploring.
					bind(automaton, lr1, lr2, quantifiedVariables, bindings);

					if (bindings.isEmpty()) {
						// Early termination since no valid bindings
						// encountered.
						break;
					}
				}
			}
		}
	}

	static private boolean bindSetOrBag(Automaton automaton, Automaton.Collection concreteSet,
			Automaton.Collection triggerSet, boolean[] quantifiedVariables, ArrayList<Binding> bindings) {

		// Note, concrete and trigger sets do not have to have same size here.
		int concreteSize = concreteSet.size();
		int triggerSize = triggerSet.size();

		// NOTE: this matches every trigger child with a concrete child, but not
		// vice-versa. See #379.

		for (int i = 0; i != triggerSize; ++i) {
			int trigger_child = triggerSet.get(i);

			// Here, we take a copy of the current bindings list. The reason for
			// this is so we can clear bindings for each concrete item and build
			// it up again. However, since a failing match in the subsequent
			// innerBind() will clear the bindings passed in, we need to keep
			// this copy so we can recreate it.
			ArrayList<Binding> originalBindings = clone(bindings);
			bindings.clear();

			for (int j = 0; j != concreteSize; ++j) {
				int concrete_child = concreteSet.get(j);
				// Make a local copy of the original bindings. This is to allow
				// us to pass this local copy through the following child and
				// see what bindings (if any) are left.
				ArrayList<Binding> localBindings = clone(originalBindings);
				bind(automaton, concrete_child, trigger_child,
						quantifiedVariables, localBindings);
				// Finally, whatever bindings are left we add to the list of
				// bindings being explored.
				bindings.addAll(localBindings);
			}
		}

		return true;
	}

	/**
	 * Create a deep clone of this array list, including the bindings it
	 * contains.
	 *
	 * @param bindings
	 * @return
	 */
	private static ArrayList<Binding> clone(ArrayList<Binding> bindings) {
		ArrayList<Binding> newBindings = new ArrayList<Binding>(bindings);
		for (int i = 0; i != newBindings.size(); ++i) {
			Binding binding = newBindings.get(i);
			newBindings.set(i, new Binding(binding));
		}
		return newBindings;
	}

	/**
	 * <p>
	 * Represents a partial (or complete) binding, as may be encountered during
	 * (or at the end) of the binding process. The binding is simply a mapping
	 * from quantified variables to their instantiated terms.
	 * </p>
	 * <p>
	 * The binding must keep track of how many variables have actually been
	 * bound. This is because we may not end up instantiating all variables and
	 * we must know when this occurs in order to ensure the remaining variables
	 * remain quantified afterwards.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	private final static class Binding implements Comparable<Binding> {
		/**
		 * The mapping from automaton states to automaton states. Initially,
		 * each state maps to itself. As the computation proceeds, those states
		 * representing quantified variables will be mapped to other (concrete)
		 * states.
		 */
		private final int[] binding;

		/**
		 * Counts the number of unbound quantified variables. If this reaches
		 * zero, we know that every quantified variables has been bound.
		 */
		private int numberUnbound;

		/**
		 * Construct a fresh binding from a given set of quantifiedVariables.
		 *
		 * @param quantifiedVariables
		 */
		public Binding(boolean[] quantifiedVariables) {
			this.binding = new int[quantifiedVariables.length];
			// Initialise all states so they map to themselves, and count the
			// number of quantified variables.
			for (int i = 0; i != quantifiedVariables.length; ++i) {
				this.binding[i] = i;
				if (quantifiedVariables[i]) {
					this.numberUnbound++;
				}
			}
		}

		public Binding(Binding binding) {
			this.binding = Arrays.copyOf(binding.binding,
					binding.binding.length);
			this.numberUnbound = binding.numberUnbound;
		}

		/**
		 * Bind a concrete state reference to a quantified state reference.
		 *
		 * @param concreteRef
		 *            Concrete state bound to
		 * @param quantifiedRef
		 *            Variable state being bound
		 * @return
		 */
		public boolean bind(int concreteRef, int quantifiedRef) {
			int current = binding[quantifiedRef];

			if (current != quantifiedRef && current != concreteRef) {
				// In this case, the binding we've found conflicts with a
				// previously established binding of the same variable.
				// Therefore, no valid binding is possible and we must fail.
				return false;
			} else if (current == quantifiedRef) {
				// In this case, there was no previous binding for this
				// variable so we establish one now.
				binding[quantifiedRef] = concreteRef;
				numberUnbound = numberUnbound - 1;
			} else {
				// Otherwise, we leave the existing binding as is.
			}

			return true;
		}

		@Override
		public int compareTo(Binding b) {
			int c = compare(binding,b.binding);
			if(c != 0) {
				return c;
			} else if(numberUnbound < b.numberUnbound){
				return -1;
			} else if(numberUnbound > b.numberUnbound){
				return 1;
			} else {
				return 0;
			}
		}	
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof Binding) {
				Binding b = (Binding) o;
				return compareTo(b) == 0;
			} else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			return Arrays.hashCode(binding);
		}
	}
	
	private static int compare(int[] lhs, int[] rhs) {
		if(lhs.length < rhs.length) {
			return -1;
		} else if(lhs.length > rhs.length) {
			return 1;
		} else {
			for(int i=0;i!=lhs.length;++i) {
				int l = lhs[i];
				int r = rhs[i];
				if(l < r) {
					return -1;
				} else if(l > r) {
					return 1;
				}
			}
			return 0;
		}
	}
}
