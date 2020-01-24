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

import static wyil.lang.WyilFile.TYPE_array;
import static wyil.lang.WyilFile.TYPE_bool;
import static wyil.lang.WyilFile.TYPE_byte;
import static wyil.lang.WyilFile.TYPE_existential;
import static wyil.lang.WyilFile.TYPE_function;
import static wyil.lang.WyilFile.TYPE_int;
import static wyil.lang.WyilFile.TYPE_method;
import static wyil.lang.WyilFile.TYPE_nominal;
import static wyil.lang.WyilFile.TYPE_null;
import static wyil.lang.WyilFile.TYPE_property;
import static wyil.lang.WyilFile.TYPE_record;
import static wyil.lang.WyilFile.TYPE_reference;
import static wyil.lang.WyilFile.TYPE_staticreference;
import static wyil.lang.WyilFile.TYPE_tuple;
import static wyil.lang.WyilFile.TYPE_union;
import static wyil.lang.WyilFile.TYPE_variable;
import static wyil.lang.WyilFile.TYPE_void;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wycc.util.ArrayUtils;
import wyil.lang.WyilFile.*;
import wyil.util.SubtypeOperator.LifetimeRelation;

/**
 * Provides a straightforward implementation of <code>Typing</code>.
 *
 * @author David J. Pearce
 *
 */
public class AbstractTyping implements Typing {
	/**
	 * The sequence expressions being being typed.
	 */
	private final Expr[] schema;
	/**
	 * Provides mapping from expressions to type variables.
	 */
	private final IdentityHashMap<Expr,Integer> mapping;
	/**
	 * Square matrix of typing environments
	 */
	private final Typing.Environment[] environments;

	public AbstractTyping(Expr[] schema, Typing.Environment... environments) {
		this.schema = schema;
		this.mapping = new IdentityHashMap<>();
		for (int i = 0, j=0; i != schema.length; ++i) {
			mapping.put(schema[i], j);
			j = j + typingWidth(schema[i]) + 1;
		}
		this.environments = environments;
	}

	protected AbstractTyping(Expr[] schema, IdentityHashMap<Expr,Integer> mapping, Typing.Environment[] environments) {
		this.schema = schema;
		this.mapping = mapping;
		this.environments = environments;
	}

	/**
	 * Return flag indicating whether there are no valid typings remaining, or not.
	 * This is equivalent to <code>height() == 0</code>.
	 *
	 * @return
	 */
	@Override
	public boolean empty() {
		return environments.length == 0;
	}

	/**
	 * Get the number of type variables managed by this typing. This is largely
	 * determined by the size of the expression being typed.
	 *
	 * @return
	 */
	@Override
	public int width() {
		return schema.length;
	}

	/**
	 * Return the number of typings that remain under consideration. If this is
	 * zero, then there are no valid typings and the original expression cannot be
	 * typed. On the other hand, if there is more than one valid typing (at the end)
	 * then the original expression is ambiguous.
	 *
	 * @return
	 */
	@Override
	public int height() {
		return environments.length;
	}

	/**
	 * Get the ith environment associated with this constraint set.
	 *
	 * @param ith
	 * @return
	 */
	@Override
	public Typing.Environment getEnvironment(int ith) {
		return environments[ith];
	}

	@Override
	public Expr getExpression(int ith) {
		return schema[ith];
	}

	@Override
	public Type[] types(Expr expression) {
		return types(mapping.get(expression));
	}

	@Override
	public Type[] types(int var) {
		// Extract all types for given variable
		Type[] types = new Type[environments.length];
		for (int i = 0; i != types.length; ++i) {
			types[i] = environments[i].get(var);
		}
		// Done
		return types;
	}

	@Override
	public int indexOf(Expr expression) {
		return mapping.get(expression);
	}

	@Override
	public int[] indexOf(Tuple<Expr> expressions) {
		int[] indices = new int[expressions.size()];
		for(int i=0;i!=indices.length;++i) {
			indices[i] = mapping.get(expressions.get(i));
		}
		return indices;
	}

	/**
	 * Invalidate all rows in this constraints set.
	 * @return
	 */
	@Override
	public Typing invalidate() {
		return copy();
	}

	@Override
	public Typing concretize() {
		Typing.Environment[] nenvs = new Typing.Environment[environments.length];
		for(int i=0;i!=nenvs.length;++i) {
			nenvs[i] = environments[i].concretize();
		}
		return copy(ArrayUtils.removeAll(nenvs, null));
	}

	/**
	 * Apply a given function to all rows of the typing matrix producing a
	 * potentially updated set of typing constraints. As part of this process, rows
	 * may be invalidated if they fail to meet some criteria.
	 *
	 * @param fn The mapping function which is applied to each row. This returns
	 *           either an updated row, or <code>null</code>, if the row is
	 *           invalidated.
	 * @return
	 */
	@Override
	public Typing map(Function<Typing.Environment,Typing.Environment> fn) {
		Typing.Environment[] ntypes = environments;
		//
		for (int i = 0; i < environments.length; i = i + 1) {
			final Typing.Environment before = ntypes[i];
			Typing.Environment after = fn.apply(before);
			if (before == after) {
				// No change, so do nothing
				continue;
			} else if (ntypes == environments) {
				// something changed, so clone
				ntypes = Arrays.copyOf(environments, environments.length);
			}
			ntypes[i] = after;
		}
		// Remove all invalid rows
		return copy(ArrayUtils.removeAll(ntypes, null));
	}

	/**
	 * Project each row of the typing matrix into zero or more rows, thus producing
	 * a potentially updated constraint set. As part of this process, rows may be
	 * added or removed based on various criteria.
	 *
	 * @param fn The mapping function which is applied to each row. This returns
	 *           zero or more updated rows. Note that it should not return
	 *           <code>null</code>.
	 * @return
	 */
	@Override
	public Typing project(Function<Typing.Environment, Typing.Environment[]> fn) {
		ArrayList<Typing.Environment> ntypes = new ArrayList<>();
		//
		for (int i = 0; i < environments.length; i = i + 1) {
			final Typing.Environment before = environments[i];
			Typing.Environment[] after = fn.apply(before);
			for (int j = 0; j != after.length; ++j) {
				ntypes.add(after[j]);
			}
		}
		// Remove all invalid rows
		return copy(ntypes.toArray(new Typing.Environment[ntypes.size()]));
	}

	@Override
	public String toString() {
		if (environments.length == 0) {
			return "_|_";
		} else {
			String r = "";
			for (Typing.Environment row : environments) {
				r = r + row;
			}
			return r;
		}
	}

	/**
	 * A simple mechanism to clone typings whilst preserving their top-level
	 * identity.
	 *
	 * @param environtments
	 * @return
	 */
	protected Typing copy(Typing.Environment... environments) {
		return new AbstractTyping(schema, mapping, environments);
	}

	// ============================================================================================================
	// Environment
	// ============================================================================================================

	public static class Environment implements Typing.Environment {
		/**
		 * Provides algorithm for subtyping (to be deprecated).
		 */
		private final SubtypeOperator subtyping;
		/**
		 * Provides information about lifetimes.
		 */
		private final LifetimeRelation lifetimes;
		/**
		 * Mapping of type variable indices to their current types.
		 */
		private Type[] types;

		public Environment(SubtypeOperator subtyping, LifetimeRelation lifetimes, Type... types) {
			this.types = types;
			this.subtyping = subtyping;
			this.lifetimes = lifetimes;
		}

		@Override
		public boolean empty() {
			return false;
		}

		/**
		 * Return the "width" of this environment (i.e. the number of allocated
		 * variables).
		 *
		 * @return
		 */
		@Override
		public int size() {
			return types.length;
		}

		/**
		 * Get the type associated with a given variable.
		 *
		 * @param variable
		 * @return
		 */
		@Override
		public Type get(int variable) {
			return types[variable];
		}

		/**
		 * Get the types associated with zero or more variables in this environment.
		 *
		 * @param variables
		 * @return
		 */
		@Override
		public Type[] getAll(int... variables) {
			Type[] ts = new Type[variables.length];
			for(int i=0;i!=ts.length;++i) {
				ts[i] = types[variables[i]];
			}
			return ts;
		}

		@Override
		public Environment concretize() {
			Type[] ts = types;
			for (int i = 0; i != ts.length; ++i) {
				Type before = types[i];
				Type after = AbstractTyping.concretize(before, this);
				if (before instanceof Type.Void) {
					// NOTE: this indicates a type variable which has not been bound. Hence, this
					// environment should be invalidated.
					return null;
				} else if (ts == types && before != after) {
					ts = Arrays.copyOf(types, types.length);
				}
				ts[i] = after;
			}
			if (ts == types) {
				return this;
			} else {
				return new Environment(subtyping, lifetimes, ts);
			}
		}

		@Override
		public Environment set(int variable, Type type) {
			if(type == null) {
				// Any attempt to assign a variable a null type always invalidates the
				// environment.
				return null;
			} else if(variable < types.length && types[variable].equals(type)) {
				// No change
				return this;
			} else {
				// Change
				int length = Math.max(variable + 1, types.length);
				Type[] ntypes = Arrays.copyOf(types, length);
				for(int i=types.length;i<length;++i) {
					ntypes[i] = Type.Void;
				}
				ntypes[variable] = type;
				return new Environment(subtyping, lifetimes, ntypes);
			}
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Environment) {
				Environment e = (Environment) o;
				return Arrays.equals(types, e.types);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(types);
		}

		@Override
		public String toString() {
			String body = "";
			for(int i=0;i!=types.length;++i) {
				if(i != 0) {
					body = body + ", ";
				}
				Type t = types[i];
				if(t == null) {
					body += "#" + i;
				} else {
					body += "#" + i + ":=" + t;
				}
			}
			return "{" + body + "}";
		}

		@Override
		public boolean isWithin(String inner, String outer) {
			return lifetimes.isWithin(inner, outer);
		}

		@Override
		public boolean isSubtype(Type lhs, Type rhs) {
			return subtyping.isSubtype(lhs, rhs, lifetimes);
		}

		@Override
		public Typing.Environment[] bind(Type lhs, Type rhs) {
			if(rhs == null || lhs == null || (!lhs.equals(Type.Void) && rhs.equals(Type.Void))) {
				return new Typing.Environment[0];
			} else {
				BinaryRelation<Type> rel = new BinaryRelation.HashSet<>();
				return AbstractTyping.bind(lhs, rhs, this, rel).toArray();
			}
		}

		@Override
		public Typing.Set union(Typing.Set other) {
			if (this == other) {
				return other;
			} else {
				Typing.Environment[] nenvs = ArrayUtils.append(this, other.toArray());
				// Remove duplicates
				nenvs = ArrayUtils.removeDuplicates(nenvs);
				// Done
				return (nenvs.length > 0) ? new Set(nenvs) : EMPTY_SET;
			}
		}

		@Override
		public Typing.Environment[] toArray() {
			return new Typing.Environment[] {this};
		}

		@Override
		public Typing.Set map(Function<Typing.Environment, Typing.Environment> fn) {
			Typing.Environment env = fn.apply(this);
			return (env != null) ? env : EMPTY_SET;
		}
	}

	// ===============================================================================================================
	// Typing.Set
	// ===============================================================================================================

	private static Set EMPTY_SET = new Set();

	/**
	 * Represents a set of typing environments which can be combined in various
	 * ways. This is similar, though not identical, to the notion of a typing. A key
	 * difference is the lack of any connection with expressions.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class Set implements Typing.Set {
		/**
		 * Square matrix of typing environments
		 */
		private final Typing.Environment[] environments;

		public Set(Typing.Environment... environments) {
			this.environments = environments;
		}

		@Override
		public boolean empty() {
			return environments.length == 0;
		}

		/**
		 * Union two constraint sets together. This combines all constraints from both
		 * sets.
		 *
		 * @param other
		 * @return
		 */
		@Override
		public Typing.Set union(Typing.Set other) {
			if (other == this) {
				// NOTE: this is a useful optimisation as this is likely in many cases.
				return this;
			}
			Typing.Environment[] nenvs = ArrayUtils.append(environments, other.toArray());
			// Remove duplicates
			nenvs = ArrayUtils.removeDuplicates(nenvs);
			// Done
			return new Set(nenvs);
		}

		/**
		 * Apply a given function to all rows of the typing matrix producing a
		 * potentially updated set of typing constraints. As part of this process, rows
		 * may be invalidated if they fail to meet some criteria.
		 *
		 * @param fn The mapping function which is applied to each row. This returns
		 *           either an updated row, or <code>null</code>, if the row is
		 *           invalidated.
		 * @return
		 */
		@Override
		public Set map(Function<Typing.Environment,Typing.Environment> fn) {
			Typing.Environment[] nenvs = environments;
			//
			for (int i = 0; i < environments.length; i = i + 1) {
				final Typing.Environment before = nenvs[i];
				Typing.Environment after = fn.apply(before);
				if (before == after) {
					// No change, so do nothing
					continue;
				} else if (nenvs == environments) {
					// something changed, so clone
					nenvs = Arrays.copyOf(environments, environments.length);
				}
				nenvs[i] = after;
			}
			// Remove all invalid rows
			nenvs = ArrayUtils.removeAll(nenvs, null);
			if(nenvs.length == 0) {
				return EMPTY_SET;
			} else {
				return new Set(nenvs);
			}
		}

		/**
		 * Access the environments internal to this typing.
		 *
		 * @return
		 */
		@Override
		public Typing.Environment[] toArray() {
			return environments;
		}

		@Override
		public String toString() {
			if (environments.length == 0) {
				return "_|_";
			} else {
				String r = "";
				for (Typing.Environment row : environments) {
					r = r + row;
				}
				return r;
			}
		}
	}

	// ===============================================================================================================
	// Binding Algorithm
	// ===============================================================================================================

	/**
	 * <p>
	 * Attempt to bind a given type (which may contain one or more existential type
	 * variables) against a given (conrete) argument type. For example, binding
	 * <code>LinkedList<?1></code> against <code>LinkedList<int></code> produces the
	 * binding <code>?1=int</code>.
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
	protected static Typing.Set bind(Type parameter, Type argument, Typing.Set constraints,
			BinaryRelation<Type> assumptions) {
		if (assumptions.get(parameter, argument)) {
			// Have visited this pair before, therefore nothing further to be gained.
			// Furthermore, must terminate here to prevent infinite loop.
			return constraints;
		} else {
			assumptions.set(parameter, argument, true);
		};
		//
		int p = normalise(parameter.getOpcode());
		int a = normalise(argument.getOpcode());
		// Recursive case. Proceed destructuring type at given index
		if(p == a) {
			switch (p) {
			case TYPE_void:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				// do nothing!
				break;
			case TYPE_variable:
				constraints = bindVariable((Type.Variable) parameter, (Type.Variable) argument, constraints, assumptions);
				break;
			case TYPE_existential:
				constraints = bindExistential((Type.Existential) parameter, argument, constraints, assumptions);
				break;
			case TYPE_array:
				constraints = bindArray((Type.Array) parameter, (Type.Array) argument, constraints, assumptions);
				break;
			case TYPE_record:
				constraints = bindRecord((Type.Record) parameter, (Type.Record)argument, constraints, assumptions);
				break;
			case TYPE_tuple:
				constraints = bindTuple((Type.Tuple) parameter, (Type.Tuple) argument, constraints, assumptions);
				break;
			case TYPE_staticreference:
			case TYPE_reference:
				constraints = bindReference((Type.Reference) parameter, (Type.Reference) argument, constraints, assumptions);
				break;
			case TYPE_function:
			case TYPE_property:
			case TYPE_method:
				constraints = bindCallable((Type.Callable) parameter, (Type.Callable) argument, constraints, assumptions);
				break;
			case TYPE_nominal:
				constraints = bindNominal((Type.Nominal) parameter, argument, constraints, assumptions);
				break;
			case TYPE_union:
				constraints = bindUnion((Type.Union) parameter, argument, constraints, assumptions);
				break;
			default:
				throw new IllegalArgumentException("Unknown type encountered: " + parameter);
			}
		} else if(a == TYPE_void) {
			// fall through
		} else if(p == TYPE_nominal) {
			constraints = bindNominal((Type.Nominal) parameter, argument, constraints, assumptions);
		} else if(a == TYPE_nominal) {
			constraints = bindNominal(parameter, (Type.Nominal) argument, constraints, assumptions);
		} else if(p == TYPE_existential) {
			constraints = bindExistential((Type.Existential) parameter, argument, constraints, assumptions);
		} else if(a == TYPE_existential) {
			constraints = bindExistential(parameter, (Type.Existential) argument, constraints, assumptions);
		} else if(p == TYPE_union) {
			constraints = bindUnion((Type.Union) parameter, argument, constraints, assumptions);
		} else if(a == TYPE_union) {
			constraints = bindUnion(parameter, (Type.Union) argument, constraints, assumptions);
		} else {
			return EMPTY_SET;
		}
		// Unset the assumptions from this traversal. The benefit of this is that, after
		// the method has completed, everything is as it was. Therefore, we can reuse
		// the relation.
		assumptions.set(parameter, argument, false);
		return constraints;
	}

	public static Typing.Set bindExistential(Type.Existential parameter, Type argument,
			Typing.Set constraints, BinaryRelation<Type> assumptions) {
		final int arg = parameter.get();
		return constraints.map(row -> boundAbove(row, arg,argument));
	}


	public static Typing.Set bindExistential(Type parameter, Type.Existential argument,
			Typing.Set constraints, BinaryRelation<Type> assumptions) {
		final int arg = argument.get();
		return constraints.map(row -> boundBelow(row, parameter, arg));
	}

	public static Typing.Environment boundAbove(Typing.Environment typing, int _lhs, Type rhs) {
		// FIXME: this is still broken.
		Type lhs = typing.get(_lhs);
		if (lhs instanceof Type.Void) {
			return typing.set(_lhs, rhs);
		} else if (typing.isSubtype(lhs, rhs)) {
			return typing;
		} else {
			return typing.set(_lhs, new Type.Union(lhs, rhs));
		}
	}

	public static Typing.Environment boundBelow(Typing.Environment typing, Type lhs, int _rhs) {
		// FIXME: this is still broken.
		Type rhs = typing.get(_rhs);
		if (rhs instanceof Type.Void) {
			return typing.set(_rhs, lhs);
		} else if (typing.isSubtype(lhs, rhs)) {
			return typing;
		} else {
			return null;
		}
	}

	public static Typing.Set bindVariable(Type.Variable parameter, Type.Variable argument,
			Typing.Set constraints, BinaryRelation<Type> assumptions) {
		if (parameter.getOperand().equals(argument.getOperand())) {
			return constraints;
		} else {
			return EMPTY_SET;
		}
	}

	public static Typing.Set bindArray(Type.Array parameter, Type.Array argument, Typing.Set constraints,
			BinaryRelation<Type> assumptions) {
		// Array type extracted successfully, therefore continue binding.
		return bind(parameter.getElement(), argument.getElement(), constraints, assumptions);
	}

	public static Typing.Set bindRecord(Type.Record t1, Type.Record t2, Typing.Set constraints,
			BinaryRelation<Type> assumptions) {
		// Attempt to extract record type so binding can continue.
		Tuple<Type.Field> t1_fields = t1.getFields();
		Tuple<Type.Field> t2_fields = t2.getFields();
		// Sanity check number of fields are reasonable.
		// Sanity check number of fields are reasonable.
		if (t1_fields.size() > t2_fields.size()) {
			return EMPTY_SET;
		} else if (t2.isOpen() && !t1.isOpen()) {
			return EMPTY_SET;
		} else if(!t1.isOpen() && t1_fields.size() != t2.getFields().size()) {
			return EMPTY_SET;
		}
		// NOTE: the following is O(n^2) but, in reality, will be faster than the
		// alternative (sorting fields into an array). That's because we expect a very
		// small number of fields in practice.
		for (int i = 0; i != t1_fields.size(); ++i) {
			Type.Field f1 = t1_fields.get(i);
			boolean matched = false;
			for (int j = 0; j != t2_fields.size(); ++j) {
				Type.Field f2 = t2_fields.get(j);
				if (f1.getName().equals(f2.getName())) {
					// Matched field
					constraints = bind(f1.getType(), f2.getType(), constraints,
							assumptions);
					matched = true;
				}
			}
			// Check we actually matched the field!
			if (!matched) {
				return EMPTY_SET;
			}
		}
		return constraints;
	}

	public static Typing.Set bindTuple(Type.Tuple parameter, Type.Tuple argument, Typing.Set constraints,
			BinaryRelation<Type> assumptions) {
		if (parameter.size() == argument.size()) {
			for (int i = 0; i != argument.size(); ++i) {
				constraints = bind(parameter.get(i), argument.get(i), constraints, assumptions);
			}
			return constraints;
		} else {
			return EMPTY_SET;
		}
	}

	public static Typing.Set bindReference(Type.Reference parameter, Type.Reference  argument, Typing.Set constraints,
			BinaryRelation<Type> assumptions) {
		// Bind against element type
		constraints = bind(parameter.getElement(), argument.getElement(), constraints, assumptions);
		// Bind against lifetime (if applicable)
		if (parameter.hasLifetime()) {
			Identifier p_lifetime = parameter.getLifetime();
			if (argument.hasLifetime()) {
				constraints = constraints.map(row -> within(row,p_lifetime, argument.getLifetime()));
			} else {
				// FIXME: unsure what to do here? Need to bind p against the static lifetime
				// somehow.
				// constraints = constraints.intersect(p_lifetime, item);
			}
		}
		return constraints;
	}

	public static Typing.Environment within(Typing.Environment env, Identifier inner, Identifier outer) {
		if(env.isWithin(inner.get(), outer.get())) {
			return env;
		} else {
			return null;
		}
	}

	public static Typing.Set bindCallable(Type.Callable lhs, Type.Callable rhs, Typing.Set constraints,
			BinaryRelation<Type> assumptions) {
		// Parameter binding is contra-variant
		constraints = bind(rhs.getParameter(), lhs.getParameter(), constraints, assumptions);
		// Return binding is co-variant.
		constraints = bind(lhs.getReturn(), rhs.getReturn(), constraints, assumptions);
		// Done.
		return constraints;
	}

	public static Typing.Set bindNominal(Type.Nominal parameter, Type argument, Typing.Set constraints,
			BinaryRelation<Type> assumptions) {
		return bind(parameter.getConcreteType(), argument, constraints, assumptions);
	}

	public static Typing.Set bindNominal(Type parameter, Type.Nominal argument, Typing.Set constraints,
			BinaryRelation<Type> assumptions) {
		return bind(parameter, argument.getConcreteType(), constraints, assumptions);
	}

	public static Typing.Set bindUnion(Type.Union parameter, Type argument, Typing.Set constraints,
			BinaryRelation<Type> assumptions) {
		Typing.Set results = null;
		//
		for (int i = 0; i != parameter.size(); ++i) {
			Typing.Set tmp = bind(parameter.get(i), argument, constraints, assumptions);
			if(tmp != constraints && !tmp.empty()) {
				results = (results == null) ? tmp : results.union(tmp);
			}
		}
		return (results == null) ? constraints : results;
	}

	public static Typing.Set bindUnion(Type parameter, Type.Union argument, Typing.Set constraints,
			BinaryRelation<Type> assumptions) {
		for (int i = 0; i != argument.size(); ++i) {
			constraints = bind(parameter, argument.get(i), constraints, assumptions);
		}
		return constraints;
	}

	// ==========================================================================================
	// Helpers
	// ==========================================================================================

	/**
	 * Flattern an expression tree into a left-to-right linear sequence. For
	 * example, <code>x+1</code> becomes <code>[x,1,+]</code>, etc.
	 *
	 * @param e
	 * @return
	 */
	public static Expr[] flattern(Expr e, Build.Meter meter) {
		ArrayList<Expr> rs = new ArrayList<>();
		new AbstractVisitor(meter) {
			@Override
			public void visitDeclaration(Decl d) {}
			@Override
			public void visitStatement(Stmt s) {}
			@Override
			public void visitType(Type t) {}
			@Override
			public void visitLogicalNot(Expr.LogicalNot e) {}
			@Override
			public void visitLogicalOr(Expr.LogicalOr e) {}
			@Override
			public void visitLogicalAnd(Expr.LogicalAnd e) {}
			@Override
			public void visitLogicalIff(Expr.LogicalIff e) {}
			@Override
			public void visitLogicalImplication(Expr.LogicalImplication e) {}
			@Override
			public void visitUniversalQuantifier(Expr.UniversalQuantifier e) {}
			@Override
			public void visitExistentialQuantifier(Expr.ExistentialQuantifier e) {}
			@Override
			public void visitExpression(Expr e) {
				super.visitExpression(e);
				rs.add(e);
			};
		}.visitExpression(e);
		//
		return rs.toArray(new Expr[rs.size()]);
	}


	/**
	 * Determine the "typing width" of an expression. This is the number of type
	 * variables required for representing this expression in a given typing. Only
	 * invocations can be represented by more than one typing variable, as these
	 * must account for the selected candidate and any template arguments being
	 * inferred.
	 *
	 * @param e
	 * @return
	 */
	public static int typingWidth(Expr e) {
		// NOTE: performing this calculation over and over is costly, and some kind of
		// caching system could help.
		if (e instanceof Expr.Invoke) {
			// Every invoke expression is represented requires two or more meta variables.
			// The first holds the candidate type which is used to identify the
			// corresponding declaration candidate. The second is the concrete type which is
			// used to handle parameter binding. After this we have zero or more variables
			// to handle existential types when inferring template arguments.
			Expr.Invoke ivk = (Expr.Invoke) e;
			int max = 0;
			if(ivk.getBinding().getArguments().size() == 0) {
				// Only consider invocations where we must infer template arguments. All other
				// invocations can be ignored since we will just substitute the provided
				// template arguments directly.
				List<Decl.Callable> candidates = ivk.getLink().getCandidates();
				for (int i = 0; i != candidates.size(); ++i) {
					Decl.Callable ith = candidates.get(i);
					max = Math.max(max, ith.getTemplate().size());
				}
			}
			return 2 + max;
		} else {
			return 0;
		}
	}
	/**
	 * A simple helper function for concretization.
	 *
	 * @param type
	 * @param env
	 * @return
	 */
	private static Type concretize(Type type, Environment env) {
		switch(type.getOpcode()) {
		case TYPE_void:
		case TYPE_null:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
		case TYPE_variable:
			break;
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			Type before = t.getElement();
			Type after = concretize(before,env);
			if(before != after) {
				return new Type.Array(after);
			}
			break;
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> before = t.getFields();
			Tuple<Type.Field> after = concretize(before,env);
			//
			if(before != after) {
				return new Type.Record(t.isOpen(),after);
			}
			break;
		}
		case TYPE_tuple: {
			Type.Tuple t = (Type.Tuple) type;
			Type[] before = t.getAll();
			Type[] after = concretize(before,env);
			//
			if(before != after) {
				return Type.Tuple.create(after);
			}
			break;
		}
		case TYPE_staticreference:
		case TYPE_reference: {
			Type.Reference t = (Type.Reference) type;
			Type before = t.getElement();
			Type after = concretize(before,env);
			if(before != after) {
				if (t.hasLifetime()) {
					return new Type.Reference(after, t.getLifetime());
				} else {
					return new Type.Reference(after);
				}
			}
			break;
		}
		case TYPE_function: {
			Type.Function t = (Type.Function) type;
			Type param = concretize(t.getParameter(),env);
			Type ret = concretize(t.getReturn(),env);
			if(param != t.getParameter() || ret != t.getReturn()) {
				return new Type.Function(param, ret);
			}
			break;
		}
		case TYPE_property: {
			Type.Property t = (Type.Property) type;
			Type param = concretize(t.getParameter(),env);
			if(param != t.getParameter()) {
				return new Type.Property(param,t.getReturn());
			}
			break;
		}
		case TYPE_method: {
			Type.Method t = (Type.Method) type;
			Type param = concretize(t.getParameter(),env);
			Type ret = concretize(t.getReturn(),env);
			if(param != t.getParameter() || ret != t.getReturn()) {
				return new Type.Method(param, ret, t.getCapturedLifetimes(), t.getLifetimeParameters());
			}
			break;
		}
		case TYPE_nominal: {
			Type.Nominal t = (Type.Nominal) type;
			Tuple<Type> before = t.getParameters();
			Tuple<Type> after = concretize2(before,env);
			//
			if(before != after) {
				return new Type.Nominal(t.getLink(), after);
			}
			break;
		}
		case TYPE_union: {
			Type.Union t = (Type.Union) type;
			Type[] before = t.getAll();
			Type[] after = concretize(before,env);
			//
			if(before != after) {
				return new Type.Union(after);
			}
			break;
		}
		case TYPE_existential: {
			Type.Existential e = (Type.Existential) type;
			// NOTE: this could result in an infinite loop in the case of an existential
			// which depends upon itself.
			return concretize(env.get(e.get()), env);
		}
		}
		return type;
	}

	private static Type[] concretize(Type[] types, Environment env) {
		Type[] ntypes = types;
		for(int i=0;i!=ntypes.length;++i) {
			Type before = types[i];
			Type after = concretize(before, env);
			if(before != after && ntypes == types) {
				ntypes = Arrays.copyOf(types, types.length);
			}
			ntypes[i] = after;
		}
		return ntypes;
	}

	private static Tuple<Type.Field> concretize(Tuple<Type.Field> fields, Environment env) {
		for(int i=0;i!=fields.size();++i) {
			Type.Field field = fields.get(i);
			Type before = field.getType();
			Type after = concretize(before, env);
			if(before != after) {
				// Something has changed
				Type.Field[] nfields = new Type.Field[fields.size()];
				for(i=0;i!=fields.size();++i) {
					field = fields.get(i);
					before = field.getType();
					after = concretize(before, env);
					if(before != after) {
						nfields[i] = new Type.Field(field.getName(),after);
					} else {
						nfields[i] = field;
					}
				}
				return new Tuple<>(nfields);
			}
		}
		return fields;
	}

	private static Tuple<Type> concretize2(Tuple<Type> fields, Environment env) {
		for(int i=0;i!=fields.size();++i) {
			Type before = fields.get(i);
			Type after = concretize(before, env);
			if(before != after) {
				// Something has changed
				Type[] nfields = new Type[fields.size()];
				for(i=0;i!=fields.size();++i) {
					before = fields.get(i);
					nfields[i] = concretize(before, env);
				}
				return new Tuple<>(nfields);
			}
		}
		return fields;
	}
	/**
	 * Normalise opcode for sake of simplicity. This allows us to compare the types
	 * of two operands more accurately using a switch.
	 *
	 * @param opcode
	 * @return
	 */
	private static int normalise(int opcode) {
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
