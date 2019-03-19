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
package wyil.type.binding;

import static wyil.lang.WyilFile.TYPE_array;
import static wyil.lang.WyilFile.TYPE_bool;
import static wyil.lang.WyilFile.TYPE_byte;
import static wyil.lang.WyilFile.TYPE_function;
import static wyil.lang.WyilFile.TYPE_int;
import static wyil.lang.WyilFile.TYPE_method;
import static wyil.lang.WyilFile.TYPE_nominal;
import static wyil.lang.WyilFile.TYPE_null;
import static wyil.lang.WyilFile.TYPE_property;
import static wyil.lang.WyilFile.TYPE_record;
import static wyil.lang.WyilFile.TYPE_union;
import static wyil.lang.WyilFile.TYPE_variable;
import static wyil.lang.WyilFile.TYPE_reference;
import static wyil.lang.WyilFile.TYPE_staticreference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.*;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.util.BinaryRelation;
import wyil.type.util.ConcreteTypeExtractor;
import wyil.type.util.HashSetBinaryRelation;
import wyil.type.util.ReadWriteTypeExtractor;

public class RelaxedTypeResolver implements TypeResolver {
	private final SubtypeOperator subtypeOperator;
	private final ConcreteTypeExtractor concreteTypeExtractor;
	private final ReadWriteTypeExtractor rwTypeExtractor;

	public RelaxedTypeResolver(SubtypeOperator subtypeOperator, ConcreteTypeExtractor concreteTypeExtractor,
			ReadWriteTypeExtractor rwTypeExtractor) {
		this.subtypeOperator = subtypeOperator;
		this.concreteTypeExtractor = concreteTypeExtractor;
		this.rwTypeExtractor = rwTypeExtractor;
	}

	/**
	 * Determine appropriate lifetime bindings for a given set of candidate function
	 * or method declarations and concrete argument types. For example:
	 *
	 * <pre>
	 * method f<a>(&a:int ptr):
	 *    ...
	 *
	 * method g() -> int:
	 *    &this:int ptr = this::new(1)
	 *    f(ptr)
	 *    return *ptr
	 * </pre>
	 *
	 * Here, the invocation <code>f(ptr)</code> needs to bind the parameter type
	 * <code>&a:int</code> with the concrete argument type <code>&this:int</code> by
	 * mapping lifetime parameter <code>a</code> to lifetime argument
	 * <code>this</code>.
	 *
	 * @param link
	 * @param candidates
	 * @param arguments
	 * @param lifetimeArguments
	 * @param lifetimes
	 * @return
	 */
	@Override
	public Type.Callable bind(Decl.Binding<Type.Callable,Decl.Callable> binding, Tuple<? extends SemanticType> types,
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
			if(selected != null) {
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
	private List<Binding> bindCallableCandidates(List<Decl.Callable> candidates,
			Tuple<? extends SemanticType> arguments, Tuple<SyntacticItem> templateArguments,
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

	private void generateApplicableBindings(Decl.Callable candidate, Tuple<? extends SemanticType> arguments,
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
			HashSetBinaryRelation<SemanticType> relation = new HashSetBinaryRelation<>();
			ConstraintSet constraints = new ConstraintSet(candidate, lifetimes);
			for(int i=0;i!=arguments.size();++i) {
				constraints = bind(parameters.get(i), arguments.get(i), constraints, relation);
			}
			// Expand the constraint set for each template variable to produce new bindings
			generateApplicableBindings(0, new SyntacticItem[constraints.size()], constraints, arguments, bindings);
		}
	}

	private void generateApplicableBindings(int index, SyntacticItem[] binding, ConstraintSet constraints,
			Tuple<? extends SemanticType> arguments, List<Binding> bindings) {
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
	private boolean isApplicable(Type.Callable candidate, LifetimeRelation lifetimes,
			Tuple<? extends SemanticType> args) {
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
				SemanticType param = parameters.get(i);
				if (!subtypeOperator.isSubtype(param, args.get(i), lifetimes)) {
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
	private ConstraintSet bind(Type parameter, SemanticType argument, ConstraintSet constraints,
			BinaryRelation<SemanticType> assumptions) {
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

	public ConstraintSet bind(Type.Variable parameter, SemanticType argument, ConstraintSet constraints,
			BinaryRelation<SemanticType> assumptions) {
		LifetimeRelation lifetimes = constraints.getLifetimes();
		// Simple case, bind directly against type variable.
		Type t = concreteTypeExtractor.apply(argument, lifetimes);
		// Sanity check whether this worked or not.
		if (t instanceof Type.Void) {
			// extraction failed for some reason
		} else {
			// No binding associated with this variable, therefore record this.
			constraints = constraints.intersect(parameter.getOperand(), t);
		}
		return constraints;
	}

	public ConstraintSet bind(Type.Array parameter, SemanticType argument, ConstraintSet constraints,
			BinaryRelation<SemanticType> assumptions) {
		LifetimeRelation lifetimes = constraints.getLifetimes();
		// Attempt to extract an array type so binding can continue.
		SemanticType.Array t = rwTypeExtractor.apply(argument, lifetimes,
				ReadWriteTypeExtractor.READABLE_ARRAY);
		if (t != null) {
			// Array type extracted successfully, therefore continue binding.
			return bind(parameter.getElement(), t.getElement(), constraints, assumptions);
		}
		return constraints;
	}

	public ConstraintSet bind(Type.Record parameter, SemanticType argument, ConstraintSet constraints,
			BinaryRelation<SemanticType> assumptions) {
		LifetimeRelation lifetimes = constraints.getLifetimes();
		// Attempt to extract record type so binding can continue.
		SemanticType.Record t = rwTypeExtractor.apply(argument, lifetimes, ReadWriteTypeExtractor.READABLE_RECORD);
		//
		if (t != null) {
			Tuple<Type.Field> param_fields = parameter.getFields();
			Tuple<? extends SemanticType.Field> arg_fields = t.getFields();
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

	public ConstraintSet bind(Type.Reference parameter, SemanticType argument, ConstraintSet constraints,
			BinaryRelation<SemanticType> assumptions) {
		LifetimeRelation lifetimes = constraints.getLifetimes();
		// Attempt to extract reference type so binding can continue.
		SemanticType.Reference t = rwTypeExtractor.apply(argument, lifetimes,
				ReadWriteTypeExtractor.READABLE_REFERENCE);
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

	public ConstraintSet bind(Type.Callable parameter, SemanticType argument, ConstraintSet constraints,
			BinaryRelation<SemanticType> assumptions) {
		LifetimeRelation lifetimes = constraints.getLifetimes();
		// Attempt to extract callable type so binding can continue.
		Type.Callable t = rwTypeExtractor.apply(argument, lifetimes, ReadWriteTypeExtractor.READABLE_CALLABLE);
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

	public ConstraintSet bind(Type.Nominal parameter, SemanticType argument, ConstraintSet constraints,
			BinaryRelation<SemanticType> assumptions) {
		// Recursively bind against the body of the nominal
		return bind(parameter.getConcreteType(), argument, constraints, assumptions);
	}

	public ConstraintSet bind(Type.Union parameter, SemanticType argument, ConstraintSet constraints,
			BinaryRelation<SemanticType> assumptions) {
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
	private Binding selectCallableCandidate(Name name, List<Binding> candidates, LifetimeRelation lifetimes) {
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
				boolean csubb = isSubtype(bestType, candidateType, lifetimes);
				boolean bsubc = isSubtype(candidateType, bestType, lifetimes);
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
	private boolean isSubtype(Type.Callable lhs, Type.Callable rhs, LifetimeRelation lifetimes) {
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
			SemanticType parentParam = parentParams.get(i);
			SemanticType childParam = childParams.get(i);
			if (!subtypeOperator.isSubtype(parentParam, childParam, lifetimes)) {
				return false;
			}
		}
		//
		return true;
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
	private class ConstraintSet {
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

		public boolean subsumes(SyntacticItem l, SyntacticItem r) {
			if (l instanceof Identifier && r instanceof Identifier) {
				Identifier li = (Identifier) l;
				Identifier ri = (Identifier) r;
				return lifetimes.isWithin(li.get(), ri.get());
			} else if(l instanceof Type && r instanceof Type) {
				Type lt = (Type) l;
				Type rt = (Type) r;
				return subtypeOperator.isSubtype(lt, rt, lifetimes);
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
	private static class Binding  {
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
}
