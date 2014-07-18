import java.util.ArrayList;

import wyautl.core.Automaton;

public class Quantifiers$native {

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
	public static Automaton.Term instantiate(Automaton automaton,
			Automaton.List args) {

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
		find(automaton, concreteExpression, quantifiedExpression,
				quantifiedVariables, bindings);

		// If one or more bindings have been computed, then apply them to the
		// quantified expression to produce one or more concrete expressions
		// which can be instantiated.
		int result = NULL;

		if (bindings.size() > 0) {
			// Apply the substitution for the first binding now.
			
			// TODO: need to return multiple instantiations
			
			result = automaton
					.substitute(quantifiedExpression, bindings.get(0));
		}

		// Done
		return (Automaton.Term) automaton.get(result);
	}

	// Computes the (static) reference to the null state.
	private static final int NULL = Automaton.K_FREE - Quantifiers.K_Null;

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
			case Quantifiers.K_Not: {
				Automaton.Term t2 = (Automaton.Term) quantifiedState;
				find(automaton, concreteExpression, t2.contents,
						quantifiedVariables, bindings);
				break;
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
	 * @param concreteExpression
	 *            The concrete expression we are binding against
	 * @param triggerExpression
	 *            A portion of the quantified expression which are are
	 *            attempting to bind against.
	 * @param quantifiedVariables
	 *            The automaton states representing the quantified variables in
	 *            the quantified expression.
	 * @return
	 */
	private static boolean bind(Automaton automaton, int concreteExpression,
			int triggerExpression, boolean[] quantifiedVariables,
			int[] binding) {
		
		// TODO: For the moment, this function can only produce one binding.
		// However, for completeness, it needs to be able to produce multiple
		// bindings.
		
		if (concreteExpression == triggerExpression) {
			// This indicates we've encountered two identical expressions,
			// neither of which can contain the variables we're binding.
			// Hence, binding fails!
			return false;
		} else if (quantifiedVariables[triggerExpression]) {
			// This indicates we've hit a quantified variable, and we must
			// attempt to update the binding accordingly.
			int current = binding[triggerExpression];
			
			if(current != NULL && current != concreteExpression) {
				// In this case, the binding we've found conflicts with a
				// previously established binding of the same variable.
				// Therefore, no valid binding is possible and we must fail.
				return false;
			} else if(current == NULL){
				// In this case, there was no previous binding for this
				// variable so we establish one now.
				binding[triggerExpression] = concreteExpression;
			}
			return true;
		}

		Automaton.State s1 = automaton.get(concreteExpression);
		Automaton.State s2 = automaton.get(triggerExpression);

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
			return bind(automaton, t1.contents, t2.contents,
					quantifiedVariables, binding);
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
				// return bind(automaton, t1, v, t2);
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
				boolean bound = bind(automaton, lr1, lr2, quantifiedVariables,
						binding);

				if (!bound) {
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
