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

import java.util.HashSet;

import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.*;
import wyil.util.SubtypeOperator.LifetimeRelation;

public interface SubtypeOperator {

	/**
	 * <p>
	 * Determine whether the <code>rhs</code> type is a <i>subtype</i> of the
	 * <code>lhs</code> (denoted <code>lhs :> rhs</code>). In the presence of type
	 * invariants, this operation is undecidable. Therefore, a <i>three-valued</i>
	 * logic is employed. Either it was concluded that the subtype relation
	 * <i>definitely holds</i>, or that it <i>definitely does not hold</i> that it
	 * is <i>unknown</i> whether it holds or not.
	 * </p>
	 *
	 * <p>
	 * For example, <code>int|null :> int</code> definitely holds. Likewise,
	 * <code>int :> int|null</code> definitely does not hold. However, whether or
	 * not <code>nat :> pos</code> holds depends on the type invariants given for
	 * <code>nat</code> and <code>pos</code> which this operator cannot reason
	 * about. Observe that, in some cases, we do get effective reasoning about types
	 * with invariants. For example, <code>null|nat :> nat</code> will be determined
	 * to definitely hold, despite the fact that <code>nat</code> has a type
	 * invariant.
	 * </p>
	 *
	 * <p>
	 * Depending on the exact language of types involved, this can be a surprisingly
	 * complex operation. For example, in the presence of <i>union</i>,
	 * <i>intersection</i> and <i>negation</i> types, the subtype algorithm is
	 * surprisingly intricate.
	 * </p>
	 *
	 * @param lhs
	 *            The candidate "supertype". That is, lhs's raw type may be a
	 *            supertype of <code>rhs</code>'s raw type.
	 * @param rhs
	 *            The candidate "subtype". That is, rhs's raw type may be a subtype
	 *            of <code>lhs</code>'s raw type.
	 * @param lifetimes
	 *            The within relation between lifetimes that should be used when
	 *            determine whether the <code>rhs</code> is a subtype of the
	 *            <code>lhs</code>.
	 * @return
	 */
	public boolean isSubtype(Type lhs, Type rhs, LifetimeRelation lifetimes);

	/**
	 * <p>
	 * Empty (i.e non-contractive) types are types which cannot accept a value
	 * because they have an <i>unterminated cycle</i>. An unterminated cycle has no
	 * leaf nodes terminating it. For example, <code>X<{X field}></code> is
	 * contractive, where as <code>X<{null|X field}></code> is not.
	 * </p>
	 *
	 * <p>
	 * This method returns true if the type is contractive, or contains a
	 * contractive subcomponent. For example, <code>null|X<{X field}></code> is
	 * considered contractive.
	 * </p>
	 *
	 * @param type --- type to test for contractivity.
	 * @return
	 * @throws ResolveError
	 */
	public boolean isEmpty(QualifiedName nid, Type type);

	/**
	 * Select one type from another to producing the result. For example, selecting
	 * <code>null</code> from <code>int|null</code> produces <code>int</code>.
	 *
	 * @param t1
	 * @param t2
	 * @return
	 */
	public Type subtract(Type t1, Type t2);

	/**
	 * Bind a sequence of argument types against a given function, method or
	 * property declaration to produce a binding which maps type & lifetime
	 * variables to concrete types / lifetimes. Or, if not binding exists, return
	 * <code>null</code>.  For example:
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
	 * @param binding
	 *            Binding being resolved
	 * @param arguments
	 *            Argument types being used for inference
	 * @param environment
	 *            The enclosing lifetime relation
	 * @return
	 */
	public Type.Callable bind(Decl.Binding<Type.Callable, Decl.Callable> binding, Tuple<Type> arguments,
			LifetimeRelation environment);

	/**
	 * <p>
	 * A lifetime relation determines, for any two lifetimes <code>l</code> and
	 * <code>m</code>, whether <code>l</code> is contained within <code>m</code> or
	 * not. This information is critical for subtype checking of reference types.
	 * Consider this minimal example:
	 * </p>
	 *
	 * <pre>
	 * method create() -> (&*:int r):
	 *    return this:new 42
	 * </pre>
	 * <p>
	 * This example should not compile. The reason is that the lifetime
	 * <code>this</code> is contained <i>within</i> the static lifetime
	 * <code>*</code>. Thus, the cell allocated within <code>create()</code> will be
	 * deallocated when the method ends and, hence, the method will return a
	 * <i>dangling reference</i>.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface LifetimeRelation {
		/**
		 * Determine whether one lifetime is contained entirely within another. This is
		 * the critical test for ensuring sound subtyping between references.
		 * Specifically, an assignment <code>&l:T p = q</code> is only considered safe
		 * if it can be shown that the lifetime of the cell referred to by
		 * <code>p</code> is <i>within</i> that of <code>q</code>.
		 *
		 * @param outer
		 * @param inner
		 * @return
		 */
		public boolean isWithin(String inner, String outer);
	}

	/**
	 * The default subtype operator checks whether one type is a <i>strict
	 * subtype</i> of another. Unlike other subtype operators, this takes into
	 * account the invariants on types. Consider these types:
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
	 */
	public static class Strict extends AbstractSubtypeOperator {

		@Override
		protected boolean isSubtype(Tuple<Expr> lhs, Tuple<Expr> rhs) {
			// NOTE: in principle, we could potentially do more here.
			return lhs.size() == 0;
		}

	}

	/**
	 * The relaxed subtype operator checks whether one type is a subtype of another
	 * with respect to their <i>underlying types</i>. This means that invariants are
	 * ignored.Consider these types:
	 *
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * type pos is (nat x) where x > 0
	 * type tan is (int x) where x >= 0
	 * </pre>
	 *
	 * In this case, each of the above types is a subtype of the other.
	 *
	 * @author David J Pearce
	 *
	 */
	public static class Relaxed extends AbstractSubtypeOperator {
		@Override
		protected boolean isSubtype(Tuple<Expr> lhs, Tuple<Expr> rhs) {
			return true;
		}

		@Override
		protected boolean isSubtype(Type.Record t1, Type.Record t2, LifetimeRelation lifetimes,
				BinaryRelation<Type> cache) {
			Tuple<Type.Field> t1_fields = t1.getFields();
			Tuple<Type.Field> t2_fields = t2.getFields();
			// Sanity check number of fields are reasonable.
			if (t1_fields.size() > t2_fields.size()) {
				return false;
			} else if (t2.isOpen() && !t1.isOpen()) {
				return false;
			} else if(!t1.isOpen() && t1_fields.size() != t2.getFields().size()) {
				return false;
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
						if (!isSubtype(f1.getType(), f2.getType(), lifetimes, cache)) {
							return false;
						} else {
							matched = true;
						}
					}
				}
				// Check we actually matched the field!
				if (!matched) {
					return false;
				}
			}
			// Done
			return true;
		}

		@Override
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
			// Check parameters (contra-variant)
			for(int i=0;i!=t1_params.size();++i) {
				if(!isSubtype(t2_params.get(i),t1_params.get(i),lifetimes)) {
					return false;
				}
			}
			// Check returns (co-variant)
			for(int i=0;i!=t1_returns.size();++i) {
				if(!isSubtype(t1_returns.get(i),t2_returns.get(i),lifetimes)) {
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
	}
}