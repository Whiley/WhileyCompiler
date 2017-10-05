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
import wyil.type.extractors.ReadableTypeExtractor;
import wyil.type.extractors.WriteableTypeExtractor;
import wyil.type.rewriters.AlgebraicTypeSimplifier;
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
	private final TypeExtractor<Type,Object> readableTypeExtractor;
	private final TypeExtractor<Type,Object> writeableTypeExtractor;
//	private final TypeInvariantExtractor typeInvariantExtractor;
	private final TypeRewriter typeSimplifier;

	public TypeSystem(Build.Project project) {
		this.resolver = new WhileyFileResolver(project);
		this.strictSubtypeOperator = new StrictSubtypeOperator(this);
		this.coerciveSubtypeOperator = new RelaxedSubtypeOperator(this);
		this.readableTypeExtractor = new ReadableTypeExtractor(resolver,this);
		this.writeableTypeExtractor = new WriteableTypeExtractor(resolver,this);
//		this.typeInvariantExtractor = new TypeInvariantExtractor(resolver);
		this.typeSimplifier = new AlgebraicTypeSimplifier();
	}

	public NameResolver getResolver() {
		// FIXME: should this method exist?
		return resolver;
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
	public boolean isVoid(Type type, LifetimeRelation lifetimes) throws ResolutionError {
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
	public boolean isRawCoerciveSubtype(Type lhs, Type rhs, LifetimeRelation lifetimes) throws ResolutionError {
		return coerciveSubtypeOperator.isSubtype(lhs,rhs,lifetimes) != SubtypeOperator.Result.False;
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
	public boolean isRawSubtype(Type lhs, Type rhs, LifetimeRelation lifetimes) throws ResolutionError {
		return strictSubtypeOperator.isSubtype(lhs,rhs,lifetimes) != SubtypeOperator.Result.False;
	}

	/**
	 * For a given type extract its readable type, such as a readable record or
	 * array type. For example, the type
	 * <code>({int x, int y}|{int x, int z})</code> has readable record type
	 * <code>{int x, ...}</code>.
	 *
	 * @param type
	 * @param lifetimes
	 * @return
	 * @throws ResolutionError
	 */
	public Type extractReadableType(Type type, LifetimeRelation lifetimes) throws ResolutionError {
		return readableTypeExtractor.extract(type, lifetimes, null);
	}

	/**
	 * For a given type, extract its readable record type. For example, the type
	 * <code>({int x, int y}|{int x, int z})</code> has readable record type
	 * <code>{int x, ...}</code>. The following illustrates some more cases:
	 *
	 * <pre>
	 * {int x, int y} | null    ==> null
	 * {int x, int y} | {int x} ==> {int x, ...}
	 * {int x, int y} | {int x, bool y} ==> {int x, int|bool y}
	 * {int x, int y} & null    ==> null
	 * {int x, int y} & {int x} ==> null
	 * {int x, int y} & {int x, int|bool y} ==> {int x, int y}
	 * </pre>
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	public Type.Record extractReadableRecord(Type type, LifetimeRelation lifetimes) throws ResolutionError {
		type = readableTypeExtractor.extract(type, lifetimes, null);
		if(type instanceof Type.Record) {
			return (Type.Record) type;
		} else {
			return null;
		}
	}

	/**
	 * For a given type, extract its writeable record type. For example, the type
	 * <code>({int x, int y}|{int x, int z})</code> has writeable record type
	 * <code>{int x, ...}</code>. The following illustrates some more cases:
	 *
	 * <pre>
	 * {int x, int y} | null    ==> null
	 * {int x, int y} | {int x} ==> {int x, ...}
	 * {int x, int y} | {int x, bool y} ==> {int x, int|bool y}
	 * {int x, int y} & null    ==> null
	 * {int x, int y} & {int x} ==> null
	 * {int x, int y} & {int x, int|bool y} ==> {int x, int y}
	 * </pre>
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	public Type.Record extractWriteableRecord(Type type, LifetimeRelation lifetimes) throws ResolutionError {
		type = writeableTypeExtractor.extract(type, lifetimes, null);
		if(type instanceof Type.Record) {
			return (Type.Record) type;
		} else {
			return null;
		}
	}

	/**
	 * Extract the readable array type from a given type. For example, the type
	 * <code>(int[])|(bool[])</code> has a readable array type of
	 * <code>(int|bool)[]</code>.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	public Type.Array extractReadableArray(Type type, LifetimeRelation lifetimes) throws ResolutionError {
		type = readableTypeExtractor.extract(type, lifetimes, null);
		if(type instanceof Type.Array) {
			return (Type.Array) type;
		} else {
			return null;
		}
	}

	/**
	 * Extract the writeable array type from a given type. For example, the type
	 * <code>(any[])|(bool[])</code> has a readable array type of
	 * <code>bool[]</code>.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	public Type.Array extractWriteableArray(Type type, LifetimeRelation lifetimes) throws ResolutionError {
		type = writeableTypeExtractor.extract(type, lifetimes, null);
		if(type instanceof Type.Array) {
			return (Type.Array) type;
		} else {
			return null;
		}
	}


	/**
	 * Extract the readable reference type from a given type. This is relatively
	 * straightforward. For example, <code>&int</code> is extracted as
	 * <code>&int</code>. However, <code>(&int)|(&bool)</code> is not extracted
	 * as as <code>&(int|bool)</code>.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	public Type.Reference extractReadableReference(Type type, LifetimeRelation lifetimes) throws ResolutionError {
		type = readableTypeExtractor.extract(type, lifetimes, null);
		if(type instanceof Type.Reference) {
			return (Type.Reference) type;
		} else {
			return null;
		}
	}

	/**
	 * Extract the writeable reference type from a given type. This is relatively
	 * straightforward. For example, <code>&int</code> is extracted as
	 * <code>&int</code>. However, <code>(&int)|(&bool)</code> is not extracted
	 * as as <code>&(int|bool)</code>.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	public Type.Reference extractWriteableReference(Type type, LifetimeRelation lifetimes) throws ResolutionError {
		type = writeableTypeExtractor.extract(type, lifetimes, null);
		if(type instanceof Type.Reference) {
			return (Type.Reference) type;
		} else {
			return null;
		}
	}

	/**
	 * Responsible for extracting a "readable lambda" from a given type. This is
	 * relatively straightforward. For example,
	 * <code>function(int)->(int)</code> is extracted as itself. However,
	 * <code>function(T1)->(int)|function(T2)->(int)</code> is not currently
	 * extracted as as <code>function(T1&T2)->(int)</code>.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	public Type.Callable extractReadableLambda(Type type, LifetimeRelation lifetimes) throws ResolutionError {
		type = readableTypeExtractor.extract(type, lifetimes, null);
		if(type instanceof Type.Callable) {
			return (Type.Callable) type;
		} else {
			return null;
		}
	}

	/**
	 * Extracting the invariant (if any) from a given type. For example,
	 * consider the following type declaration:
	 *
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * </pre>
	 *
	 * Then, extracting the invariant from type <code>nat</code> gives
	 * <code>x >= 0</code>. Likewise, extracting the invariant from the type
	 * <code>bool|int</code> gives the invariant
	 * <code>(x is int) ==> (x >= 0)</code>. Finally, extracting the invariant
	 * from the type <code>nat[]</code> gives the invariant
	 * <code>forall(int i).(0 <= i
	 * && i < |xs| ==> xs[i] >= 0)</code>.
	 *
	 *
	 */
//	public Formula extractInvariant(Type type, Expr root) throws ResolutionError {
//		return typeInvariantExtractor.extract(type,root);
//	}

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

	// ========================================================================
	// Simplification
	// ========================================================================

	public Type simplify(Type type) {
		return typeSimplifier.rewrite(type);
	}
}
