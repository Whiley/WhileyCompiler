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
package wyil.type;

import java.util.List;

import wybs.util.AbstractCompilationUnit.Name;
import wyc.util.WhileyFileResolver;
import wyil.type.SubtypeOperator.LifetimeRelation;
import wyil.type.SubtypeOperator.SemanticType;
import wyil.type.subtyping.RelaxedSubtypeOperator;
import wyil.type.subtyping.StrictSubtypeOperator;

import static wyc.lang.WhileyFile.*;
import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;

/**
 * <p>
 * The type system is responsible for managing the relationship between
 * nominal types and their underlying types. Every visible type has an
 * underlying type associated with it which, in some cases, will be the same.
 * For example, the underlying type associated with type <code>int</code> is
 * simply <code>int</code>. However, in many cases, there is a difference. For
 * example:
 * </p>
 *
 * <pre>
 * type nat is (int x) where x >= 0
 * </pre>
 *
 * <p>
 * In this case, the underlying type associated with the type <code>nat</code>
 * is <code>int</code>. This class provides a way to determine the underlying
 * type associated with a given type.
 * </p>
 * <p>
 * <b>NOTE:</b> in principle, this could cache expanded types for performance
 * reasons (though it currently does not).
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class TypeSystem {
	private final NameResolver resolver;
	private final SubtypeOperator strictSubtypeOperator;
	private final SubtypeOperator coerciveSubtypeOperator;

	public TypeSystem(Build.Project project) {
		this.resolver = new WhileyFileResolver(project);
		this.strictSubtypeOperator = new StrictSubtypeOperator(this);
		this.coerciveSubtypeOperator = new RelaxedSubtypeOperator(this);
	}

	public NameResolver getResolver() {
		// FIXME: should this method exist?
		return resolver;
	}

	public SemanticType toSemanticType(Type type) {
		return strictSubtypeOperator.toSemanticType(type);
	}

	/**
	 * <p>
	 * Contractive types are types which cannot accept value because they have
	 * an <i>unterminated cycle</i>. An unterminated cycle has no leaf nodes
	 * terminating it. For example, <code>X<{X field}></code> is contractive,
	 * where as <code>X<{null|X field}></code> is not.
	 * </p>
	 *
	 * <p>
	 * This method returns true if the type is contractive, or contains a
	 * contractive subcomponent. For example, <code>null|X<{X field}></code> is
	 * considered contracted.
	 * </p>
	 *
	 * @param type --- type to test for contractivity.
	 * @return
	 * @throws ResolutionError
	 * @throws ResolveError
	 */
	public boolean isContractive(NameID nid, Type type) throws ResolutionError {
		return strictSubtypeOperator.isContractive(nid, type);
	}

	/**
	 * <p>
	 * Check whether a type is equivalent to <code>void</code> or not. The
	 * complexities of Whiley's type system mean that this is not always obvious.
	 * For example, the type <code>int&(!int)</code> is equivalent to
	 * <code>void</code>. Likewise, is the type <code>!any</code>. Another
	 * interesting case is the following:
	 * </p>
	 *
	 * <pre>
	 * type T is { T t }
	 * </pre>
	 *
	 * <p>
	 * This is only considered equivalent to <code>void</code> under an
	 * <i>inductive</i> interpretation of types (which is assumed in Whiley). The
	 * distinction is that, under a <i>coinductive</i> interpretation, instances of
	 * <code>T</code> do exist which, by construction, are infinite chains. Since
	 * such chains cannot be constructed in Whiley, we can disregard them.
	 * </p>
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	public boolean isVoid(SemanticType type, LifetimeRelation lifetimes) throws ResolutionError {
		return strictSubtypeOperator.isVoid(type, lifetimes);
	}

	/**
	 * <p>
	 * Determine whether one type is a <i>raw subtype</i> of another.
	 * Specifically, whether the raw type of <code>rhs</code> is a subtype of
	 * <code>lhs</code>'s raw type (i.e.
	 * "<code>&lfloor;lhs&rfloor; :> &lfloor;rhs&rfloor;</code>"). The raw type
	 * is that which ignores any type invariants involved. Thus, one must be
	 * careful when interpreting the meaning of this operation. Specifically,
	 * "<code>&lfloor;lhs&rfloor; :> &lfloor;rhs&rfloor;</code>" <b>does not
	 * imply</b> that "<code>lhs :> rhs</code>" holds. However, if
	 * "<code>&lfloor;lhs&rfloor; :> &lfloor;rhs&rfloor;</code>" does not hold,
	 * then it <b>does follow</b> that "<code>lhs :> rhs</code>" also does not
	 * hold.
	 * </p>
	 *
	 * <p>
	 * Depending on the exact language of types involved, this can be a
	 * surprisingly complex operation. For example, in the presence of
	 * <i>union</i>, <i>intersection</i> and <i>negation</i> types, the subtype
	 * algorithm is surprisingly intricate.
	 * </p>
	 *
	 * @param lhs
	 *            The candidate "supertype". That is, lhs's raw type may be a
	 *            supertype of <code>rhs</code>'s raw type.
	 * @param rhs
	 *            The candidate "subtype". That is, rhs's raw type may be a
	 *            subtype of <code>lhs</code>'s raw type.
	 * @return
	 * @throws ResolutionError
	 *             Occurs when a nominal type is encountered whose name cannot
	 *             be resolved properly. For example, it resolves to more than
	 *             one possible matching declaration, or it cannot be resolved
	 *             to a corresponding type declaration.
	 */
	public boolean isRawCoerciveSubtype(SemanticType lhs, SemanticType rhs, LifetimeRelation lifetimes) throws ResolutionError {
		return coerciveSubtypeOperator.isSubtype(lhs,rhs,lifetimes) != SubtypeOperator.Result.False;
	}


	// ========================================================================
	// Inference
	// ========================================================================

	// ========================================================================
	// Resolution
	// ========================================================================

	public <T extends Decl.Named> T resolveExactly(Name name, Class<T> kind)
			throws ResolutionError {
		return resolver.resolveExactly(name,kind);
	}

	public <T extends Decl.Callable> T resolveExactly(Name name, Type.Callable signature, Class<T> kind)
			throws ResolutionError {
		for(T decl : resolveAll(name,kind)) {
			if(decl.getType().equals(signature)) {
				return decl;
			}
		}
		return null;
	}

	public <T extends Decl.Named> List<T> resolveAll(Name name, Class<T> kind)
			throws ResolutionError {
		return resolver.resolveAll(name,kind);
	}
}
