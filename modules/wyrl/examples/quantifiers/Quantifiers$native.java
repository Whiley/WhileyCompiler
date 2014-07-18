import java.util.ArrayList;

import wyautl.core.Automaton;

public class Quantifiers$native {

/**
	 * Attempt to bind a quantified expression with a concrete expression,
	 * producing one or more candidate bindings.
	 * 
	 * @param automaton
	 *            The automaton being operated over.
	 * @param args
	 *            The arguments list, which consists of the concrete expression,
	 *            the list of quantified variables and the quantified
	 *            expression.
	 * @return
	 */
	public static Automaton.Term instantiate(Automaton automaton, Automaton.List args) {

		int concreteExpression = args.get(0);
		int v = args.get(1); // HACK
		int quantifiedExpression = args.get(2);
		
		// Construct a simple way to identified quantified variables
		boolean[] quantifiedVariables = new boolean[automaton.nStates()];
		quantifiedVariables[v] = true;
		
		// Construct a list into which each completed binding is placed. Each
		// binding is a mapping from automaton states representing quantified
		// variables from e2 to their concrete counterparts from e1. 
		ArrayList<int[]> bindings = new ArrayList<int[]>(); 
		
		// Attempt to find as many bindings as possible. This is a
		// potentially expensive operation when the quantified expression is
		// large and/or there are a large number of quantified variables.
		find(automaton, concreteExpression,quantifiedExpression,quantifiedVariables,bindings);

		// If one or more bindings have been computed, then apply them to the
		// quantified expression to produce one or more concrete expressions
		// which can be instantiated.
		int result = NULL;
		
		if(bindings.size() > 0) {
			// Apply the substitution for the first binding now.
			result = automaton.substitute(quantifiedExpression, bindings.get(0));
		} 
		
		// Done
		return (Automaton.Term) automaton.get(result);
	}

	// Computes the (static) reference to the null state.
	private static final int NULL = Automaton.K_FREE - Quantifiers.K_Null;

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
					quantifiedVariables);
		} else {
			// This is not a trigger point. Therefore, if its a logical
			// expression continue the search through the expression; Otherwise,
			// give up.
			switch (quantifiedState.kind) {
			case Quantifiers.K_Not: {
				Automaton.Term t2 = (Automaton.Term) quantifiedState;
				find(automaton, concreteExpression, t2.contents,
						quantifiedVariables, bindings);
			}
			case Quantifiers.K_And:
			case Quantifiers.K_Or: {
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
			}
			}
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
	 * @param concreteExpression
	 *            The concrete expression we are binding against
	 * @param quantifiedExpression
	 *            The expression parameterised against v we're using to guide
	 *            the binding of quantified variables
	 * @param quantifiedVariables
	 *            The automaton states representing the quantified variables in
	 *            the quantified expression.
	 * @return
	 */
	private static boolean bind(Automaton automaton, int concreteExpression,
			int quantifiedExpression, boolean[] quantifiedVariables, int[] binding) {
		if (concreteExpression == quantifiedExpression) {
			// This indicates we've encountered two identical expressions,
			// neither of which can contain the variables we're binding.
			// Hence, binding fails!
			return false;
		} else if (quantifiedVariables[quantifiedExpression]) {
			// This indicates we've hit a quantified variable, and we must
			// update the binding accordingly.
			
			// TODO: problem here is variable is already bound
			
			binding[quantifiedExpression] = concreteExpression;
			return true;
		}

		Automaton.State s1 = automaton.get(concreteExpression);
		Automaton.State s2 = automaton.get(quantifiedExpression);

		// Start with easy cases.
		if (s1.kind != s2.kind) {
			// This indicates two non-identical states with different kind. No
			// binding is possible here, and so binding fails.
			return false;
		} else if (s1 instanceof Automaton.Bool || s1 instanceof Automaton.Int
				|| s1 instanceof Automaton.Strung) {
			// These are all atomic states which have different values (by
			// construction). Therefore, no binding is possible.
			return false;
		} else if (s1 instanceof Automaton.Term) {
			Automaton.Term t1 = (Automaton.Term) s1;
			Automaton.Term t2 = (Automaton.Term) s2;
			// In this case, we have two non-identical terms of the same
			// kind and, hence, we must continue traversing the automaton
			// in an effort to complete the binding.
			return bind(automaton, t1.contents, t2.contents, quantifiedVariables, binding);
		} else {
			Automaton.Collection c1 = (Automaton.Collection) s1;
			Automaton.Collection c2 = (Automaton.Collection) s2;
			int c1_size = c1.size();

			if (c1_size != c2.size()) {
				// Here, we have collections of different size and, hence,
				// binding must fail.
				return false;
			} else if (s1 instanceof Automaton.List) {
				Automaton.List l1 = (Automaton.List) c1;
				Automaton.List l2 = (Automaton.List) c2;
				return bind(automaton, l1, l2, quantifiedVariables, binding);
			} else if (s1 instanceof Automaton.Set) {
				Automaton.Set t1 = (Automaton.Set) s1;
				Automaton.Set t2 = (Automaton.Set) s2;
				// TODO: need to implement this case
				//return bind(automaton, t1, v, t2);
				return false;
			} else {
				Automaton.Bag b1 = (Automaton.Bag) s1;
				Automaton.Bag b2 = (Automaton.Bag) s2;
				// TODO: need to implement this case
				return false;
			}
		}
	}

	static private boolean bind(Automaton automaton, Automaton.List l1,
			Automaton.List l2, boolean[] quantifiedVariables, int[] binding) {
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
				boolean bound = bind(automaton, lr1, lr2, quantifiedVariables, binding);

				if(!bound) {
					return false;
				}				
			}
		}

		return true;
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
