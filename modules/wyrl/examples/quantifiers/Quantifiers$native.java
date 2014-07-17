import wyautl.core.Automaton;

public class Quantifiers$native {
	
	// Computes the (static) reference to the null state.
	private static final int NULL = Automaton.K_FREE - Quantifiers.K_Null;
	
	public static Automaton.Term bind(Automaton automaton, Automaton.List args) {
		
		// TODO: add support for generating multiple bindings (rather than just
		// returning the first found as we currently do)
		
		int result = find(automaton,args.get(0),args.get(1),args.get(2));
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
		
		if(s1.kind == s2.kind) {			
			// This indicates a potential trigger point, so attempt to bind.
			return bind(automaton,r1,v,r2);
		} else {
			// This is not a trigger point. Therefore, if its a logical
			// expression continue the search through the expression; Otherwise,
			// give up.
			switch(s2.kind) {
			case Quantifiers.K_Not: {
				Automaton.Term t2 = (Automaton.Term) s2;
				return find(automaton,r1,v,t2.contents);
			}				
			case Quantifiers.K_And:
			case Quantifiers.K_Or: {
				// FIXME: implement this!
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
		if(r1 == r2) {
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
		if(s1.kind != s2.kind) {
			// This indicates two non-identical states with different kind.  No binding is possible here, and so binding fails.
			return NULL;
		} else if (s1 instanceof Automaton.Bool || s1 instanceof Automaton.Int
				|| s1 instanceof Automaton.Strung) {
			// These are all atomic states which have different values (by
			// construction). Therefore, no binding is possible.
			return NULL;
		} else if(s1 instanceof Automaton.Term) {
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
			
			if(c1_size != c2.size()) {
				// Here, we have collections of different size and, hence, binding must fail.
				return NULL;
			} else if(s1 instanceof Automaton.List){
				// Lists are the easiest to handle, because we can perform a
				// linear comparison.
				Automaton.List l1 = (Automaton.List) c1;
				Automaton.List l2 = (Automaton.List) c2;
				int result = NULL;
				
				for(int i=0;i!=c1_size;++i) {
					int lr1 = l1.get(i);
					int lr2 = l2.get(i);
					
					if(lr1 != lr2) {
						// Here, we have non-identical elements at the same
						// position. Therefore, we need to traverse them to look
						// for a binding.
						int r = bind(automaton,lr1,v,lr2);
						
						if(r == NULL || (result != NULL && result != r)) {
							// No binding possible, so terminate early.
							return NULL;
						} else {
							// Otherwise, we have a candidate binding.
							result = r;
						}						
					}
				}
				
				return result;				
			} else if(s1 instanceof Automaton.Set) { 
				Automaton.Set t1 = (Automaton.Set) s1;
				Automaton.Set t2 = (Automaton.Set) s2;
				
				// TODO: need to implement this case								
				return NULL;
				
			} else {
				Automaton.Bag b1 = (Automaton.Bag) s1;
				Automaton.Bag b2 = (Automaton.Bag) s2;

				// TODO: need to implement this case
				return NULL;
			}
		}
	}	

}
