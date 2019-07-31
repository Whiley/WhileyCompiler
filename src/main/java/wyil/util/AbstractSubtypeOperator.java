// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.util;

import static wyil.lang.WyilFile.*;

import java.util.*;

import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wycc.util.ArrayUtils;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.*;
import wyil.lang.WyilFile.Type.Field;
import wyil.lang.WyilFile.Type.Record;
import wyil.util.SubtypeOperator.LifetimeRelation;

/**
 * <p>
 * Provides default implementations for <code>isSubtype</code> and
 * <code>bind</code>. The intention is that these be overriden to provide
 * different variants (e.g. relaxed subtype operators, etc).
 * </p>
 * <p>
 * <b>(Subtyping)</b> The default subtype operator checks whether one type is a
 * <i>strict subtype</i> of another. Unlike other subtype operators, this takes
 * into account the invariants on types. Consider these two types:
 *
 * <pre>
 * type nat is (int x) where x >= 0
 * type pos is (nat x) where x > 0
 * type tan is (int x) where x >= 0
 * </pre>
 *
 * In this case, we have <code>nat <: int</code> since <code>int</code> is
 * explicitly included in the definition of <code>nat</code>. Observe that this
 * applies transitively and, hence, <code>pos <: nat</code>. But, it does not
 * follow that <code>nat <: int</code> and, likewise, that
 * <code>pos <: nat</code>. Likewise, <code>nat <: tan</code> does not follow
 * (despite this being actually true) since we cannot reason about invariants.
 * </p>
 * <p>
 * <b>(Binding)</b> An important task is computing a "binding" between a
 * function, method or property declaration and a given set of concrete
 * arguments types. For example, consider:
 * </p>
 *
 * <pre>
 * template<T>
 * function get(T[] items, int i) -> T:
 *    return items[i]
 *
 *  function f(int[] items) -> int:
 *     return get(items,0)
 * </pre>
 *
 * <p>
 * At the point of the invocation for <code>get()</code> we must resolve the
 * declared type <code>function(T[],int)->(T)</code> against the declared
 * parameter types <code>(int[],int)</code>, yielding a binding
 * <code>T=int</code>.
 * </p>
 * <p>
 * Computing the binding between two types is non-trivial in Whiley. In addition
 * to template arguments (as above), we must handle lifetime arguments. For
 * example:
 * </p>
 *
 * <pre>
 * method <a> m(&a:int x) -> int:
 *    return *a
 *
 * ...
 *   &this:int ptr = new 1
 *   return m(ptr)
 * </pre>
 * <p>
 * At the invocation to <code>m()</code>, we need to infer the binding
 * <code>a=this</code>. A major challenge is the presence of union types. For
 * example, consider this binding problem:
 * </p>
 *
 * <pre>
 * template<S,T>
 * function f(S x, S|T y) -> S|T:
 *    return y
 *
 * function g(int p, bool|int q) -> (bool|int r):
 *    return f(p,q)
 * </pre>
 * <p>
 * At the invocation to <code>f</code> we must generate the binding
 * <code>S=int,T=bool</code>. When binding <code>bool|int</code> against
 * <code>S|T</code> we need to consider both cases where
 * <code>S=bool,T=int</code> and <code>S=int,T=bool</code>. Otherwise, we cannot
 * be sure to consider the right combination.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public abstract class AbstractSubtypeOperator implements SubtypeOperator {


	@Override
	public boolean isSubtype(Type t1, Type t2, LifetimeRelation lifetimes) {
		return isSubtype(t1,t2,lifetimes,null);
	}

	@Override
	public boolean isEmpty(QualifiedName nid, Type type) {
		return isContractive(nid, type, null);
	}

	@Override
	public Type.Callable bind(Decl.Binding<Type.Callable, Decl.Callable> binding, Tuple<Type> types,
			LifetimeRelation environment) {
		// Now attempt to bind the given candidate declarations against the concrete
		// argument types.
		Decl.Link<Decl.Callable> link = binding.getLink();
		List<Decl.Callable> candidates = link.getCandidates();
		Tuple<SyntacticItem> templateArguments = binding.getArguments();
		// Bind candidate types to given argument types which, in particular, will
		// produce bindings for template variables
		List<Binding> bindings = bindCallableCandidates(candidates, types, templateArguments, environment);
		// Sanity check bindings generated
		if (!bindings.isEmpty()) {
			// Select the most precise signature from the candidate bindings
			Binding selected = selectCallableCandidate(link.getName(), bindings, environment);
			// Check whether one was selected or not
			if (selected != null) {
				// Assign descriptor to this expression
				link.resolve(selected.getCandidateDeclaration());
				// Set inferred lifetime parameters as well
				binding.setArguments(link.getHeap().allocate(selected.getArguments()));
				//
				return selected.getConcreteType();
			}
		}
		return null;
	}

	// ===========================================================================
	// Bind
	// ===========================================================================

	/**
	 * <p>
	 * Give a list of candidate declarations, go through and determine which (if
	 * any) can be bound to the given lifetime and/or type arguments. There are two
	 * aspects to this: firstly, we must consider all possible lifetime
	 * instantiations; secondly, any binding must produce a type for which each
	 * argument is applicable. The following illustrates a simple example:
	 * </p>
	 *
	 * <pre>
	 * function f() -> (int r):
	 *    return 0
	 *
	 * function f(int x) -> (int r):
	 *    return x
	 *
	 * function g(int x) -> (int r):
	 *    return f(x)
	 * </pre>
	 * <p>
	 * For the above example, name resolution will identify both declarations for
	 * <code>f</code> as candidates. However, this method will produce only one
	 * "binding", namely that corresponding to the second declaration. This is
	 * because the first declaration is not applicable to the given arguments.
	 * </p>
	 * <p>
	 * The presence of lifetime parameters makes this process more complex. To
	 * understand why, consider this scenario:
	 * </p>
	 *
	 * <pre>
	 * method <a,b> f(&a:int p, &a:int q, &b:int r) -> (&b:int r):
	 *    return r
	 *
	 * method g():
	 *    &this:int x = new 1
	 *    &this:int y = new 2
	 *    &this:int z = new 3
	 *    f(x,y,z)
	 *    ...
	 * </pre>
	 * <p>
	 * For the invocation of <code>f(x,y,z)</code> we initially have only one
	 * candidate, namely <code>method<a,b>(&a:int,&a:int,&b:int)</code>. Observe
	 * that, by itself, this is not immediately applicable. Specifically,
	 * <code>&this:int</code> is not a subtype of <code>&a:int</code>. Instead, we
	 * must determine the binding <code>a->this,b->this</code>.
	 * </p>
	 * <p>
	 * Unfortunately, things are yet more complicated as we must be able to
	 * <i>generalise bindings</i>. Consider this alternative implementation of
	 * <code>g()</code>:
	 * </p>
	 *
	 * <pre>
	 * method <l> g(&l:int p) -> (&l:int r):
	 *    &this:int q = new 1
	 *    return f(p,q,p)
	 * </pre>
	 * <p>
	 * In this case, there are at least two possible bindings for the invocation,
	 * namely: <code>{a->this,b->l}</code> and <code>{a->l,b->l}</code>. We can
	 * safely discount e.g. <code>{a->this,b->this}</code> as <code>b->this</code>
	 * never occurs in practice and, indeed, failure to discount this would prevent
	 * the method from type checking.
	 * </p>
	 *
	 * @param candidates
	 * @param arguments
	 *            Inferred Argument Types
	 * @param templateArguments
	 *            Explicit template arguments (if provided)
	 * @param lifetimes
	 *            Within relationship between declared lifetimes
	 * @return
	 */
	protected List<Binding> bindCallableCandidates(List<Decl.Callable> candidates,
			Tuple<Type> arguments, Tuple<SyntacticItem> templateArguments,
			LifetimeRelation lifetimes) {
		ArrayList<Binding> bindings = new ArrayList<>();
		// Go through each candidate and generate all possible bindings.
		for (int i = 0; i != candidates.size(); ++i) {
			Decl.Callable candidate = candidates.get(i);
			// Generate all potential template bindings based on template arguments
			generateApplicableBindings(candidate, arguments, templateArguments, bindings, lifetimes);
		}
		// Done
		return bindings;
	}

	protected void generateApplicableBindings(Decl.Callable candidate, Tuple<Type> arguments,
			Tuple<SyntacticItem> templateArguments, List<Binding> bindings, LifetimeRelation lifetimes) {
		//
		Tuple<Template.Variable> templateParameters = candidate.getTemplate();
		Type.Callable type = candidate.getType();
		Tuple<Type> parameters = type.getParameters();
		//
		if (parameters.size() != arguments.size()
				|| (templateArguments.size() > 0 && templateArguments.size() != templateParameters.size())) {
			// (1) Differing number of parameters / arguments. Since we don't
			// support variable-length argument lists (yet), there is nothing
			// more to consider. (2) explicit lifetime arguments have been given and these
			// don't match the declared lifetime parameters.
			return;
		} else if (templateParameters.size() == 0 || templateArguments.size() > 0) {
			// In this case, either the method accepts no template parameters, or explicit
			// template parameters were given. Eitherway, we can avoid all the machinery for
			// guessing appropriate bindings.
			Type.Callable concreteType = WyilFile.substitute(candidate.getType(), candidate.getTemplate(), templateArguments);
			// Add binding (if applicable)
			if (isApplicable(concreteType, lifetimes, arguments)) {
				bindings.add(new Binding(candidate, templateArguments, concreteType));
			}
		} else if(parameters.size() > 0) {
			// Go through every argument attempting to form a binding.
			BinaryRelation.HashSet<Type> relation = new BinaryRelation.HashSet<>();
			ConstraintSet constraints = new ConstraintSet(candidate, lifetimes);
			//
			for(int i=0;i!=arguments.size();++i) {
				constraints = bind(parameters.get(i), arguments.get(i), constraints, relation);
			}
			// Expand the constraint set for each template variable to produce new bindings
			generateApplicableBindings(0, new SyntacticItem[constraints.size()], constraints, arguments, bindings);
		}
	}

	protected void generateApplicableBindings(int index, SyntacticItem[] binding, ConstraintSet constraints,
			Tuple<Type> arguments, List<Binding> bindings) {
		//
		if(index == constraints.size()) {
			// BASE CASE.
			Decl.Callable decl = constraints.getDeclaration();
			Tuple<SyntacticItem> templateArguments = new Tuple<>(binding);
			// Generate the concrete type via substitution
			Type.Callable concreteType = WyilFile.substitute(decl.getType(), decl.getTemplate(), templateArguments);
			//
			if (isApplicable(concreteType, constraints.getLifetimes(), arguments)) {
				// Record the new binding
				bindings.add(new Binding(decl, templateArguments, concreteType));
			}
		} else {
			// RECURSIVE CASE
			if (constraints.height() == 1) {
				// NOTE: Optimisation for common case where we can avoid cloning the binding.
				binding[index] = constraints.get(index,0);
				if(binding[index] != null) {
					// only proceed if was binding for template variable
					generateApplicableBindings(index + 1, binding, constraints, arguments, bindings);
				}
			} else if (constraints.height() > 1) {
				for(int i=0;i!=constraints.height();++i) {
					// FIXME: avoid copy on final item somehow?
					SyntacticItem value = constraints.get(index,i);
					if(value != null) {
						// only proceed if was binding for template variable
						SyntacticItem[] nbinding = Arrays.copyOf(binding, binding.length);
						nbinding[index] = value;
						generateApplicableBindings(index + 1, nbinding, constraints,  arguments, bindings);
					}
				}
			}
		}
	}

	/**
	 * Determine whether a given function or method declaration is applicable to a
	 * given set of argument types. If there number of arguments differs, it's
	 * definitely not applicable. Otherwise, we need every argument type to be a
	 * subtype of its corresponding parameter type.
	 *
	 * @param candidate
	 * @param args
	 * @return
	 */
	protected boolean isApplicable(Type.Callable candidate, LifetimeRelation lifetimes, Tuple<Type> args) {
		Tuple<Type> parameters = candidate.getParameters();
		if (parameters.size() != args.size()) {
			// Differing number of parameters / arguments. Since we don't
			// support variable-length argument lists (yet), there is nothing
			// more to consider.
			return false;
		} else {
			// Number of parameters matches number of arguments. Now, check that
			// each argument is a subtype of its corresponding parameter.
			for (int i = 0; i != args.size(); ++i) {
				Type param = parameters.get(i);
				if (!isSubtype(param, args.get(i), lifetimes)) {
					return false;
				}
			}
			//
			return true;
		}
	}
	/**
	 * <p>
	 * Attempt to bind a given parameter type (which may contain one or more type
	 * variables) against a given (conrete) argument type. For example, binding
	 * <code>LinkedList<T></code> against <code>LinkedList<int></code> produces the
	 * binding <code>T=int</code>.
	 * </p>
	 * <p>
	 * The essential challenge here is to recurse through the parameter type until a
	 * type variable is reached. For example, consider binding <code>{T f}</code>
	 * against <code>{int f}</code>. To extract the binding <code>T=int</code> we
	 * must recurse through the fields of the record.
	 * </p>
	 *
	 * @param index
	 * @param parameter
	 * @param argument
	 * @param arguments
	 * @param binding
	 * @param bindings
	 * @param lifetimes
	 */
	protected ConstraintSet bind(Type parameter, Type argument, ConstraintSet constraints,
			BinaryRelation<Type> assumptions) {
		if (assumptions.get(parameter, argument)) {
			// Have visited this pair before, therefore nothing further to be gained.
			// Furthermore, must terminate here to prevent infinite loop.
			return constraints;
		} else {
			assumptions.set(parameter, argument, true);
			// Recursive case. Proceed destructuring type at given index
			switch (parameter.getOpcode()) {
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				// do nothing!
				break;
			case TYPE_variable:
				constraints = bind((Type.Variable) parameter, argument, constraints, assumptions);
				break;
			case TYPE_array:
				constraints = bind((Type.Array) parameter, argument, constraints, assumptions);
				break;
			case TYPE_record:
				constraints = bind((Type.Record) parameter, argument, constraints, assumptions);
				break;
			case TYPE_staticreference:
			case TYPE_reference:
				constraints = bind((Type.Reference) parameter, argument, constraints, assumptions);
				break;
			case TYPE_function:
			case TYPE_property:
			case TYPE_method:
				constraints = bind((Type.Callable) parameter, argument, constraints, assumptions);
				break;
			case TYPE_nominal:
				constraints = bind((Type.Nominal) parameter, argument, constraints, assumptions);
				break;
			case TYPE_union:
				constraints = bind((Type.Union) parameter, argument, constraints, assumptions);
				break;
			default:
				throw new IllegalArgumentException("Unknown type encountered: " + parameter);
			}
			// Unset the assumptions from this traversal. The benefit of this is that, after
			// the method has completed, everything is as it was. Therefore, we can reuse
			// the relation.
			assumptions.set(parameter, argument, false);
			//
			return constraints;
		}
	}

	public ConstraintSet bind(Type.Variable parameter, Type argument, ConstraintSet constraints,
			BinaryRelation<Type> assumptions) {
		LifetimeRelation lifetimes = constraints.getLifetimes();
		// Simple case, bind directly against type variable.
		// No binding associated with this variable, therefore record this.
		return constraints.intersect(parameter.getOperand(), argument);
	}

	public ConstraintSet bind(Type.Array parameter, Type argument, ConstraintSet constraints,
			BinaryRelation<Type> assumptions) {
		// Attempt to extract an array type so binding can continue.
		Type.Array t = extract(Type.Array.class,argument);
		if (t != null) {
			// Array type extracted successfully, therefore continue binding.
			return bind(parameter.getElement(), t.getElement(), constraints, assumptions);
		}
		return constraints;
	}

	public ConstraintSet bind(Type.Record parameter, Type argument, ConstraintSet constraints,
			BinaryRelation<Type> assumptions) {
		// Attempt to extract record type so binding can continue.
		Type.Record t = extract(Type.Record.class,argument);
		//
		if (t != null) {
			Tuple<Type.Field> param_fields = parameter.getFields();
			Tuple<Type.Field> arg_fields = t.getFields();
			if (param_fields.size() == arg_fields.size()) {
				// FIXME: problems with open records here?
				for (int i = 0; i != param_fields.size(); ++i) {
					// FIXME: problem with name ordering here?
					constraints = bind(param_fields.get(i).getType(), arg_fields.get(i).getType(), constraints,
							assumptions);
				}
			}
		}
		return constraints;
	}

	public ConstraintSet bind(Type.Reference parameter, Type argument, ConstraintSet constraints,
			BinaryRelation<Type> assumptions) {
		// Attempt to extract reference type so binding can continue.
		Type.Reference t = extract(Type.Reference.class,argument);
		//
		if (t != null) {
			// Bind against element type
			constraints = bind(parameter.getElement(), t.getElement(), constraints, assumptions);
			// Bind against lifetime (if applicable)
			if (parameter.hasLifetime()) {
				Identifier p_lifetime = parameter.getLifetime();
				if (t.hasLifetime()) {
					constraints = constraints.intersect(p_lifetime, t.getLifetime());
				} else {
					// FIXME: unsure what to do here? Need to bind p against the static lifetime
					// somehow.
					// constraints = constraints.intersect(p_lifetime, item);
				}
			}
		}
		return constraints;
	}

	public ConstraintSet bind(Type.Callable parameter, Type argument, ConstraintSet constraints,
			BinaryRelation<Type> assumptions) {
		// Attempt to extract callable type so binding can continue.
		Type.Callable t = extract(Type.Callable.class,argument);
		//
		if (t != null) {
			// Bind against parameters and returns
			Tuple<Type> p_parameters = parameter.getParameters();
			Tuple<Type> t_parameters = t.getParameters();
			Tuple<Type> p_returns = parameter.getReturns();
			Tuple<Type> t_returns = t.getReturns();
			if (p_parameters.size() == t_parameters.size() && p_returns.size() == t_returns.size()) {
				for (int i = 0; i != p_parameters.size(); ++i) {
					constraints = bind(p_parameters.get(i), t_parameters.get(i), constraints, assumptions);
				}
				for (int i = 0; i != p_returns.size(); ++i) {
					constraints = bind(p_returns.get(i), t_returns.get(i), constraints, assumptions);
				}
			}
		}
		return constraints;
	}

	public ConstraintSet bind(Type.Nominal parameter, Type argument, ConstraintSet constraints,
			BinaryRelation<Type> assumptions) {
		// Recursively bind against the body of the nominal
		return bind(parameter.getConcreteType(), argument, constraints, assumptions);
	}

	public ConstraintSet bind(Type.Union parameter, Type argument, ConstraintSet constraints,
			BinaryRelation<Type> assumptions) {
		ConstraintSet results = bind(parameter.get(0), argument, constraints, assumptions);
		//
		for (int i = 1; i != parameter.size(); ++i) {
			results = results.union(bind(parameter.get(i), argument, constraints, assumptions));
		}
		return results;
	}

	/**
	 * Given a list of candidate function or method declarations, determine the most
	 * precise match for the supplied argument types. The given argument types must
	 * be applicable to this function or macro declaration, and it must be a subtype
	 * of all other applicable candidates.
	 *
	 * @param candidates
	 * @param args
	 * @return
	 */
	protected Binding selectCallableCandidate(Name name, List<Binding> candidates, LifetimeRelation lifetimes) {
		Binding best = null;
		Type.Callable bestType = null;
		boolean bestValidWinner = false;
		//
		for (int i = 0; i != candidates.size(); ++i) {
			Binding candidate = candidates.get(i);
			Type.Callable candidateType = candidate.getConcreteType();
			if (best == null) {
				// No other candidates are applicable so far. Hence, this
				// one is automatically promoted to the best seen so far.
				best = candidate;
				bestType = candidateType;
				bestValidWinner = true;
			} else {
				boolean csubb = isParameterSubtype(bestType, candidateType, lifetimes);
				boolean bsubc = isParameterSubtype(candidateType, bestType, lifetimes);
				//
				if (csubb && !bsubc) {
					// This candidate is a subtype of the best seen so far. Hence, it is now the
					// best seen so far.
					best = candidate;
					bestType = candidate.getConcreteType();
					bestValidWinner = true;
				} else if (bsubc && !csubb) {
					// This best so far is a subtype of this candidate. Therefore, we can simply
					// discard this candidate from consideration since it's definitely not the best.
				} else if (!csubb && !bsubc) {
					// This is the awkward case. Neither the best so far, nor the candidate, are
					// subtypes of each other. In this case, we report an error. NOTE: must perform
					// an explicit equality check above due to the present of type invariants.
					// Specifically, without this check, the system will treat two declarations with
					// identical raw types (though non-identical actual types) as the same.
					return null;
				} else {
					// This is a tricky case. We have two types after instantiation which are
					// considered identical under the raw subtype test. As such, they may not be
					// actually identical (e.g. if one has a type invariant). Furthermore, we cannot
					// stop at this stage as, in principle, we could still find an outright winner.
					bestValidWinner = false;
				}
			}
		}
		return bestValidWinner ? best : null;
	}


	// ===========================================================================================
	// Reference Helpers
	// ===========================================================================================

	/**
	 * Check whether the type signature for a given function or method declaration
	 * is a super type of a given child declaration.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	protected boolean isParameterSubtype(Type.Callable lhs, Type.Callable rhs, LifetimeRelation lifetimes) {
		Tuple<Type> parentParams = lhs.getParameters();
		Tuple<Type> childParams = rhs.getParameters();
		if (parentParams.size() != childParams.size()) {
			// Differing number of parameters / arguments. Since we don't
			// support variable-length argument lists (yet), there is nothing
			// more to consider.
			return false;
		}
		// Number of parameters matches number of arguments. Now, check that
		// each argument is a subtype of its corresponding parameter.
		for (int i = 0; i != parentParams.size(); ++i) {
			Type parentParam = parentParams.get(i);
			Type childParam = childParams.get(i);
			if (!isSubtype(parentParam, childParam, lifetimes)) {
				return false;
			}
		}
		//
		return true;
	}

	/**
	 * From an arbitrary type, extract a particular kind of type.
	 *
	 * @param kind
	 * @param type
	 * @return
	 */
	public <T extends Type> T extract(Class<T> kind, Type type) {
		if (kind.isInstance(type)) {
			return (T) type;
		} else if (type instanceof Type.Nominal) {
			Type.Nominal t = (Type.Nominal) type;
			return extract(kind, t.getConcreteType());
		} else {
			return null; // deadcode
		}
	}

	/**
	 * Represents a set of constraints on the given template variables in the form
	 * of a matrix. For example, consider this:
	 *
	 * <pre>
	 *  {T=int, l=this}
	 *  {T=bool, l=this}
	 * </pre>
	 *
	 * This indicates that there are two options for variable <code>T</code>, namely
	 * <code>int</code> and <code>bool</code>. However, for lifetime <code>l</code>
	 * only one option has been identified.
	 *
	 * @author David J. Pearce
	 *
	 */
	protected class ConstraintSet {
		private final LifetimeRelation lifetimes;
		private final Decl.Callable callable;
		private final Tuple<Template.Variable> variables;
		private final ArrayList<SyntacticItem[]> rows;

		public ConstraintSet(Decl.Callable callable, LifetimeRelation lifetimes) {
			this.lifetimes = lifetimes;
			this.callable = callable;
			this.variables = callable.getTemplate();
			this.rows = new ArrayList<>();
			// Initially, all constraints are null
			this.rows.add(new SyntacticItem[variables.size()]);
		}

		private ConstraintSet(Decl.Callable callable, LifetimeRelation lifetimes, ArrayList<SyntacticItem[]> rows) {
			this.lifetimes = lifetimes;
			this.callable = callable;
			this.variables = callable.getTemplate();
			this.rows = rows;
			simplify();
		}

		/**
		 * Get number of domain of variables (also called the <i>width</i>).
		 *
		 * @return
		 */
		public int size() {
			return variables.size();
		}

		/**
		 * Get number of constraint rows (also called the <i>height</i>).
		 *
		 * @return
		 */
		public int height() {
			return rows.size();
		}

		/**
		 * Get the value at a given column and row within the constraint set.
		 *
		 * @param x
		 * @param y
		 * @return
		 */
		public SyntacticItem get(int x, int y) {
			return rows.get(y)[x];
		}

		public LifetimeRelation getLifetimes() {
			return lifetimes;
		}

		public Decl.Callable getDeclaration() {
			return callable;
		}

		public ConstraintSet intersect(Identifier key, SyntacticItem item) {
			if(key.get().equals("*")) {
				// special case to handle static lifetime, which is not a template variable but
				// can be used in a templated declaration.
				if (item instanceof Identifier && lifetimes.isWithin(((Identifier) item).get(), "*")) {
					return this;
				} else {
					return new ConstraintSet(callable,lifetimes);
				}
			} else {
				int index = indexOf(key);
				// FIXME: could optimise this creation away.
				ArrayList<SyntacticItem[]> nrows = new ArrayList<>();
				// Iterate each row intersecting against update
				for(int i=0;i!=rows.size();++i) {
					SyntacticItem[] row = intersect(index, rows.get(i), item);
					if (row != null) {
						nrows.add(row);
					}
				}
				// Done
				return new ConstraintSet(callable,lifetimes,nrows);
			}
		}

		public ConstraintSet union(ConstraintSet other) {
			// FIXME: avoid this heap allocation sometimes?
			ArrayList<SyntacticItem[]> nrows = new ArrayList<>();
			nrows.addAll(rows);
			nrows.addAll(other.rows);
			return new ConstraintSet(callable, lifetimes, nrows);
		}

		private void simplify() {
			// Remove any duplices in the constraint set
			for (int i = rows.size() - 1; i >= 0; --i) {
				SyntacticItem[] ith_row = rows.get(i);
				for (int j = i - 1; j >= 0; --j) {
					SyntacticItem[] jth_row = rows.get(j);
					if(Arrays.equals(ith_row,jth_row)) {
						rows.remove(i);
						break;
					}
				}
			}
		}

		private SyntacticItem[] intersect(int col, SyntacticItem[] row, SyntacticItem value) {
			SyntacticItem current = row[col];
			if (value == null || subsumes(current, value)) {
				// no change required
				return row;
			} else if (current == null || subsumes(value, current)) {
				row = Arrays.copyOf(row, row.length);
				row[col] = value;
				return row;
			} else {
				return null;
			}
		}

		private boolean subsumes(SyntacticItem l, SyntacticItem r) {
			if (l instanceof Identifier && r instanceof Identifier) {
				Identifier li = (Identifier) l;
				Identifier ri = (Identifier) r;
				return lifetimes.isWithin(li.get(), ri.get());
			} else if(l instanceof Type && r instanceof Type) {
				Type lt = (Type) l;
				Type rt = (Type) r;
				return isSubtype(lt, rt, lifetimes);
			} else {
				return false;
			}
		}

		private int indexOf(Identifier key) {
			for(int i=0;i!=variables.size();++i) {
				Template.Variable tvar = variables.get(i);
				if(tvar.getName().equals(key)) {
					return i;
				}
			}
			throw new IllegalArgumentException("invalid constraint set key (" + key + ")");
		}

		@Override
		public String toString() {
			String r = "";
			for(int i=0;i!=variables.size();++i) {
				if(i!=0) { r += ","; }
				r += variables.get(i) + "=" + toString(i,rows);
			}
			return "{" + r + "}";
		}

		private String toString(int col, ArrayList<SyntacticItem[]> rows) {
			String r = "";
			for(int i=0;i!=rows.size();++i) {
				if(i != 0) {
					r += ";";
				}
				SyntacticItem item =  rows.get(i)[col];
				r += item != null ? item.toString() : "_";
			}
			return r;
		}
	}

	/**
	 * Represents a candidate binding between a callable type and declaration.
	 *
	 * @author David J. Pearce
	 *
	 */
	protected static class Binding  {
		private final Tuple<SyntacticItem> arguments;
		private final Decl.Callable candidate;
		private final Type.Callable concreteType;

		public Binding(Decl.Callable candidate, Tuple<SyntacticItem> arguments, Type.Callable concreteType) {
			this.candidate = candidate;
			this.arguments = arguments;
			this.concreteType = concreteType;
		}

		public Decl.Callable getCandidateDeclaration() {
			return candidate;
		}

		public Type.Callable getConcreteType() {
			return concreteType;
		}

		public Tuple<SyntacticItem> getArguments() {
			return arguments;
		}

		@Override
		public String toString() {
			Tuple<Template.Variable> variables = candidate.getTemplate();
			String r = "";
			for(int i=0;i!=variables.size();++i) {
				if(i != 0) {
					r = r + ",";
				}
				r += variables.get(i);
				r += "=";
				r += arguments.get(i);
			}
			return "{" + r + "}:" + candidate.getType();
		}
	}

	// ===========================================================================
	// Contractivity
	// ===========================================================================

	/**
	 * Provides a helper implementation for isContractive.
	 * @param name
	 * @param type
	 * @param visited
	 * @return
	 */
	static boolean isContractive(QualifiedName name, Type type, HashSet<QualifiedName> visited) {
		switch (type.getOpcode()) {
		case TYPE_void:
		case TYPE_any:
		case TYPE_null:
		case TYPE_bool:
		case TYPE_int:
		case TYPE_staticreference:
		case TYPE_reference:
		case TYPE_array:
		case TYPE_record:
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
		case TYPE_invariant:
		case TYPE_byte:
		case TYPE_unknown:
			return true;
		case TYPE_union: {
			Type.Union c = (Type.Union) type;
			for (int i = 0; i != c.size(); ++i) {
				if (!isContractive(name, c.get(i), visited)) {
					return false;
				}
			}
			return true;
		}
		default:
		case TYPE_nominal:
			Type.Nominal n = (Type.Nominal) type;
			Decl.Link<Decl.Type> link = n.getLink();
			Decl.Type decl = link.getTarget();
			QualifiedName nid = decl.getQualifiedName();
			if (nid.equals(name)) {
				// We have identified a non-contractive type.
				return false;
			} else if (visited != null && visited.contains(nid)) {
				// NOTE: this identifies a type (other than the one we are looking for) which is
				// not contractive. It may seem odd then, that we pretend it is in fact
				// contractive. The reason for this is simply that we cannot tell here with the
				// type we are interested in is contractive or not. Thus, to improve the error
				// messages reported we ignore this non-contractiveness here (since we know
				// it'll be caught down the track anyway).
				return true;
			} else if (decl.isRecursive()) {
				// Lazily construct the visited set as, in the vast majority of cases, this is
				// never required.
				visited = new HashSet<>();
				visited.add(nid);
			}
			return isContractive(name, decl.getType(), visited);
		}
	}

	// ===========================================================================
	// Subtyping
	// ===========================================================================

	/**
	 * A subtype operator aimed at checking whether one type is a <i>strict
	 * subtype</i> of another. Unlike other subtype operators, this takes into
	 * account the invariants on types. Consider these two types:
	 *
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * type pos is (nat x) where x > 0
	 * type tan is (int x) where x >= 0
	 * </pre>
	 *
	 * In this case, we have <code>nat <: int</code> since <code>int</code> is
	 * explicitly included in the definition of <code>nat</code>. Observe that this
	 * applies transitively and, hence, <code>pos <: nat</code>. But, it does not
	 * follow that <code>nat <: int</code> and, likewise, that
	 * <code>pos <: nat</code>. Likewise, <code>nat <: tan</code> does not follow
	 * (despite this being actually true) since we cannot reason about invariants.
	 *
	 * @author David J. Pearce
	 *
	 */
	protected boolean isSubtype(Type t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		int t1_opcode = normalise(t1.getOpcode());
		int t2_opcode = normalise(t2.getOpcode());
		//
		if (t1_opcode == t2_opcode) {
			switch (t1_opcode) {
			case TYPE_void:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				return true;
			case TYPE_array:
				return isSubtype((Type.Array) t1, (Type.Array) t2, lifetimes, cache);
			case TYPE_record:
				return isSubtype((Type.Record) t1, (Type.Record)t2, lifetimes, cache);
			case TYPE_nominal:
				return isSubtype((Type.Nominal) t1, (Type.Nominal)t2, lifetimes, cache);
			case TYPE_union:
				return isSubtype(t1, (Type.Union) t2, lifetimes, cache);
			case TYPE_staticreference:
			case TYPE_reference:
				return isSubtype((Type.Reference) t1, (Type.Reference) t2, lifetimes, cache);
			case TYPE_method:
			case TYPE_function:
			case TYPE_property:
				return isSubtype((Type.Callable) t1, (Type.Callable) t2, lifetimes, cache);
			case TYPE_variable:
				return isSubtype((Type.Variable) t1, (Type.Variable) t2, lifetimes, cache);
			default:
				throw new IllegalArgumentException("unexpected type encountered: " + t1);
			}
		} else if (t2_opcode == TYPE_nominal) {
			return isSubtype(t1, (Type.Nominal) t2, lifetimes, cache);
		} else if (t2_opcode == TYPE_union) {
			return isSubtype(t1, (Type.Union) t2, lifetimes, cache);
		} else if (t1_opcode == TYPE_union) {
			return isSubtype((Type.Union) t1, t2, lifetimes, cache);
		} else if (t1_opcode == TYPE_nominal) {
			return isSubtype((Type.Nominal) t1, (Type.Atom) t2, lifetimes, cache);
		} else {
			// Nothing else works except void
			return t2_opcode == TYPE_void;
		}
	}

	protected boolean isSubtype(Type.Array t1, Type.Array t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		return isSubtype(t1.getElement(), t2.getElement(), lifetimes, cache);
	}

	protected boolean isSubtype(Type.Record t1, Type.Record t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		Tuple<Type.Field> t1_fields = t1.getFields();
		Tuple<Type.Field> t2_fields = t2.getFields();
		// Sanity check number of fields are reasonable.
		if(t1_fields.size() != t2_fields.size()) {
			return false;
		} else if(t1.isOpen() != t2.isOpen()) {
			return false;
		}
		// Check fields one-by-one.
		for (int i = 0; i != t1_fields.size(); ++i) {
			Type.Field f1 = t1_fields.get(i);
			Type.Field f2 = t2_fields.get(i);
			if (!f1.getName().equals(f2.getName())) {
				// Fields have differing names
				return false;
			} else if (!isSubtype(f1.getType(), f2.getType(), lifetimes, cache)) {
				// Fields are not subtypes
				return false;
			}
		}
		// Done
		return true;
	}

	protected boolean isSubtype(Type.Reference t1, Type.Reference t2, LifetimeRelation lifetimes,
			BinaryRelation<Type> cache) {
		String l1 = extractLifetime(t1);
		String l2 = extractLifetime(t2);
		//
		return lifetimes.isWithin(l1, l2) && areEquivalent(t1.getElement(), t2.getElement(), lifetimes);
	}

	protected boolean isSubtype(Type.Callable t1, Type.Callable t2, LifetimeRelation lifetimes,
			BinaryRelation<Type> cache) {
		Tuple<Type> t1_params = t1.getParameters();
		Tuple<Type> t2_params = t2.getParameters();
		Tuple<Type> t1_returns = t1.getReturns();
		Tuple<Type> t2_returns = t2.getReturns();
		// Eliminate easy cases first
		if (t1.getOpcode() != t2.getOpcode() || t1_params.size() != t2_params.size()
				|| t1_returns.size() != t2_returns.size()) {
			return false;
		}
		// Check parameters
		for(int i=0;i!=t1_params.size();++i) {
			if(!areEquivalent(t1_params.get(i),t2_params.get(i),lifetimes)) {
				return false;
			}
		}
		// Check returns
		for(int i=0;i!=t1_returns.size();++i) {
			if(!areEquivalent(t1_returns.get(i),t2_returns.get(i),lifetimes)) {
				return false;
			}
		}
		// Check lifetimes
		if(t1 instanceof Type.Method) {
			Type.Method m1 = (Type.Method) t1;
			Type.Method m2 = (Type.Method) t2;
			Tuple<Identifier> m1_lifetimes = m1.getLifetimeParameters();
			Tuple<Identifier> m2_lifetimes = m2.getLifetimeParameters();
			Tuple<Identifier> m1_captured = m1.getCapturedLifetimes();
			Tuple<Identifier> m2_captured = m2.getCapturedLifetimes();
			// FIXME: it's not clear to me what we need to do here. I think one problem is
			// that we must normalise lifetimes somehow.
			if (m1_lifetimes.size() > 0 || m2_lifetimes.size() > 0) {
				throw new RuntimeException("must implement this!");
			} else if (m1_captured.size() > 0 || m2_captured.size() > 0) {
				throw new RuntimeException("must implement this!");
			}
		}
		// Done
		return true;
	}

	protected boolean isSubtype(Type.Variable t1, Type.Variable t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		return t1.getOperand().equals(t2.getOperand());
	}

	protected boolean isSubtype(Type t1, Type.Union t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		for(int i=0;i!=t2.size();++i) {
			if (!isSubtype(t1, t2.get(i), lifetimes, cache)) {
				return false;
			}
		}
		return true;
	}

	protected boolean isSubtype(Type.Union t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		for (int i = 0; i != t1.size(); ++i) {
			if (isSubtype(t1.get(i), t2, lifetimes, cache)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isSubtype(Type.Nominal t1, Type.Nominal t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		Decl.Type d1 = t1.getLink().getTarget();
		Decl.Type d2 = t2.getLink().getTarget();
		// FIXME: only need to check for coinductive case when both types are recursive.
		// If either is not recursive, then are guaranteed to eventually terminate.
		if (cache != null && cache.get(t1, t2)) {
			return true;
		} else if (cache == null) {
			// Lazily construct cache.
			cache = new BinaryRelation.HashSet<>();
		}
		cache.set(t1, t2, true);
		//
		Tuple<Expr> t1_invariant = d1.getInvariant();
		Tuple<Expr> t2_invariant = d2.getInvariant();
		// Dispatch easy cases
		if (d1 == d2) {
			Tuple<Type> t1s = t1.getParameters();
			Tuple<Type> t2s = t2.getParameters();
			for (int i = 0; i != t1s.size(); ++i) {
				if (!isSubtype(t1s.get(i), t2s.get(i), lifetimes, cache)) {
					return false;
				}
			}
			return true;
		} else {
			boolean left = isSubtype(t1_invariant, t2_invariant);
			boolean right = isSubtype(t2_invariant, t1_invariant);
			if(left || right) {
				Type tt1 = left ? t1.getConcreteType() : t1;
				Type tt2 = right ? t2.getConcreteType() : t2;
				return isSubtype(tt1,tt2, lifetimes, cache);
			} else {
				return false;
			}
		}
	}

	/**
	 * Check whether a nominal type is a subtype of an atom (i.e. not a nominal or
	 * union). For example, <code>int :> nat</code> or <code>{nat f} :> rec</code>.
	 * This is actually easy as an invariants on the nominal type can be ignored
	 * (since they already imply it is a subtype).
	 *
	 * @param t1
	 * @param t2
	 * @param lifetimes
	 * @return
	 */
	protected boolean isSubtype(Type t1, Type.Nominal t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		return isSubtype(t1, t2.getConcreteType(), lifetimes, cache);
	}

	/**
	 * Check whether a nominal type is a supertype of an atom (i.e. not a nominal or
	 * union). For example, <code>int <: nat</code> or <code>{nat f} <: rec</code>.
	 * This is harder because the invariant cannot be reasoned about. In fact, the
	 * only case where this can hold true is when there is no invariant.
	 *
	 * @param t1
	 * @param t2
	 * @param lifetimes
	 * @return
	 */
	protected boolean isSubtype(Type.Nominal t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		Decl.Type d1 = t1.getLink().getTarget();
		Tuple<Expr> t1_invariant = d1.getInvariant();
		// Dispatch easy cases
		if (isSubtype(t1_invariant, EMPTY_INVARIANT)) {
			return isSubtype(t1.getConcreteType(), t2, lifetimes, cache);
		} else {
			return false;
		}
	}

	/**
	 * Determine whether one invariant is a subtype of another. In other words, the
	 * subtype invariant implies the supertype invariant.
	 *
	 * @param lhs The "super" type
	 * @param rhs The "sub" type
	 * @return
	 */
	protected abstract boolean isSubtype(Tuple<Expr> lhs, Tuple<Expr> rhs);

	/**
	 * Determine whether two types are "equivalent" or not.
	 *
	 * @param t1
	 * @param t2
	 * @param lifetimes
	 * @return
	 */
	protected boolean areEquivalent(Type t1, Type t2, LifetimeRelation lifetimes) {
		// NOTE: this is a temporary solution.
		return isSubtype(t1, t2, lifetimes, null) && isSubtype(t2, t1, lifetimes, null);
	}

	// ===============================================================================
	// Type Selector
	// ===============================================================================

	/**
	 * Select one type from a given type.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	@Override
	public Type subtract(Type t1, Type t2) {
		return subtract(t1,t2,new BinaryRelation.HashSet<>());
	}

	private Type subtract(Type t1, Type t2, BinaryRelation<Type> cache) {

		int t1_opcode = t1.getOpcode();
		int t2_opcode = t2.getOpcode();
		//
		if(t1.equals(t2)) {
			// Easy case
			return Type.Void;
		} else if(t1_opcode == t2_opcode) {
			switch(t1_opcode) {
			case TYPE_void:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				return Type.Void;
			case TYPE_array:
			case TYPE_staticreference:
			case TYPE_reference:
			case TYPE_method:
			case TYPE_function:
			case TYPE_property:
				return t1;
			case TYPE_record:
				return subtract((Type.Record) t1, (Type.Record) t2, cache);
			case TYPE_nominal:
				return subtract((Type.Nominal) t1, (Type.Nominal) t2, cache);
			case TYPE_union:
				return subtract((Type.Union) t1, t2, cache);
			default:
				throw new IllegalArgumentException("unexpected type encountered: " + t1);
			}
		} else if (t2_opcode == TYPE_union) {
			return subtract(t1, (Type.Union) t2, cache);
		} else if (t1_opcode == TYPE_union) {
			return subtract((Type.Union) t1, t2, cache);
		} else if (t2_opcode == TYPE_nominal) {
			return subtract(t1, (Type.Nominal) t2, cache);
		} else if (t1_opcode == TYPE_nominal) {
			return subtract((Type.Nominal) t1, t2, cache);
		} else {
			return t1;
		}
	}

	/**
	 * Subtraction of records is possible in a limited number of cases.
	 *
	 * @param t1
	 * @param t2
	 * @return
	 */
	public Type subtract(Type.Record t1, Type.Record t2, BinaryRelation<Type> cache) {
		Tuple<Type.Field> t1_fields = t1.getFields();
		Tuple<Type.Field> t2_fields = t2.getFields();
		if(t1_fields.size() != t2_fields.size() || t1.isOpen() || t1.isOpen()) {
			// Don't attempt anything
			return t1;
		}
		Type.Field[] r_fields = new Type.Field[t1_fields.size()];
		boolean found = false;
		for(int i=0;i!=t1_fields.size();++i) {
			Type.Field f1 = t1_fields.get(i);
			Type.Field f2 = t2_fields.get(i);
			if(!f1.getName().equals(f2.getName())) {
				// Give up
				return t1;
			}
			if(!f1.getType().equals(f2.getType())) {
				if(found) {
					return t1;
				} else {
					found = true;
					Type tmp = subtract(f1.getType(), f2.getType(), cache);
					r_fields[i] = new Type.Field(f1.getName(), tmp);
				}
			} else {
				r_fields[i] = f1;
			}
		}
		return new Type.Record(false,new Tuple<>(r_fields));
	}

	public Type subtract(Type.Nominal t1, Type.Nominal t2, BinaryRelation<Type> cache) {
		// FIXME: only need to check for coinductive case when both types are recursive.
		// If either is not recursive, then are guaranteed to eventually terminate.
		if (cache != null && cache.get(t1, t2)) {
			return t1;
		} else if (cache == null) {
			// Lazily construct cache.
			cache = new BinaryRelation.HashSet<>();
		}
		cache.set(t1, t2, true);
		//
		Decl.Type d1 = t1.getLink().getTarget();
		// NOTE: the following invariant check is essentially something akin to
		// determining whether or not this is a union.
		if (d1.getInvariant().size() == 0) {
			return subtract(t1.getConcreteType(), (Type) t2, cache);
		} else {
			return t1;
		}
	}

	public Type subtract(Type t1, Type.Nominal t2, BinaryRelation<Type> cache) {
		Decl.Type d2 = t2.getLink().getTarget();
		// NOTE: the following invariant check is essentially something akin to
		// determining whether or not this is a union.
		if (d2.getInvariant().size() == 0) {
			return subtract(t1,t2.getConcreteType(),cache);
		} else {
			return t1;
		}
	}

	public Type subtract(Type.Nominal t1, Type t2, BinaryRelation<Type> cache) {
		Decl.Type d1 = t1.getLink().getTarget();
		// NOTE: the following invariant check is essentially something akin to
		// determining whether or not this is a union.
		if (d1.getInvariant().size() == 0) {
			return subtract(t1.getConcreteType(), t2, cache);
		} else {
			return t1;
		}
	}

	public Type subtract(Type t1, Type.Union t2, BinaryRelation<Type> cache) {
		for (int i = 0; i != t2.size(); ++i) {
			t1 = subtract(t1, t2.get(i), cache);
		}
		return t1;
	}
	public Type subtract(Type.Union t1, Type t2, BinaryRelation<Type> cache) {
		Type[] types = new Type[t1.size()];
		for(int i=0;i!=t1.size();++i) {
			types[i] = subtract(t1.get(i),t2,cache);
		}
		// Remove any selected cases
		types = ArrayUtils.removeAll(types, Type.Void);
		//
		switch(types.length) {
		case 0:
			return Type.Void;
		case 1:
			return types[0];
		default:
			return new Type.Union(types);
		}
	}
	// ===============================================================================
	// Helpers
	// ===============================================================================

	private static final Tuple<Expr> EMPTY_INVARIANT = new Tuple<>();

	/**
	 * Extract the lifetime from a given reference type.
	 *
	 * @param ref
	 * @return
	 */
	protected String extractLifetime(Type.Reference ref) {
		if (ref.hasLifetime()) {
			return ref.getLifetime().get();
		} else {
			return "*";
		}
	}

	/**
	 * Normalise opcode for sake of simplicity. This allows us to compare the types
	 * of two operands more accurately using a switch.
	 *
	 * @param opcode
	 * @return
	 */
	protected int normalise(int opcode) {
		switch(opcode) {
		case TYPE_reference:
		case TYPE_staticreference:
			return TYPE_reference;
		case TYPE_method:
		case TYPE_property:
		case TYPE_function:
			return TYPE_function;
		}
		//
		return opcode;
	}
}
