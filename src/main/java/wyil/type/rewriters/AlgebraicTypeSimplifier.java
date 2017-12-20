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
package wyil.type.rewriters;

import wyc.lang.WhileyFile.Type;
import wycc.util.ArrayUtils;

/**
 * A simple collection of rewrite rules that attempts to simplify types in
 * relatively obvious ways. Specifically, the following rules are applied:
 *
 * <ul>
 * <li>
 * <p>
 * <b>Flattening</b>. Nested unions and intersections types of the same kind are
 * flattened into one. For exmple, <code>T1|(T2|T3)</code> is flattened into
 * <code>T1|T2|T3.</code>
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Duplicate Elimination</b>. Duplicates are removed from unions and
 * intersections. For example, <code>T1|T1|T2</code> becomes <code>T1|T2</code>.
 * </p>
 * </li>
 *
 * <li>
 * <p>
 * <b>Void Reduction</b>. An intersection containing <code>void</code> (e.g.
 * <code>int&!any</code>) is reduced to <code>void</code>. In contrast, a union
 * containing <code>void</code> (e.g. <code>int|!any</code>) has that element
 * removed (e.g. to give <code>int</code>).
 * </p>
 * </li>
 *
 * <li>
 * <p>
 * <b>Any Reduction</b>. A union containing <code>any</code> (e.g.
 * <code>int|any</code>) is reduced to <code>any</code>. In contrast, an
 * intersection containing <code>any</code> (e.g. <code>int|any</code>) has that
 * element removed (e.g. to give <code>int</code>).
 * </p>
 * </li>
 *
 * <li>
 * <p>
 * <b>Unit Reduction</b>. A union or intersection containing a single element is
 * reduced to that element.
 * </p>
 * </li>
 *
 * <li>
 * <p>
 * <b>Negation Reduction</b>. The negation <code>!any</code> is reduced to
 * <code>void</code>. Likewise, <code>!void</code> is reduced to
 * <code>any</code>.
 * </p>
 * </li>
 *
 * <li>
 * <p>
 * <b>Negation Elimination</b>. A negation of a negation (e.g. <code>!!T</code>)
 * is reduced to the element value (e.g. <code>T</code>).
 * </p>
 * </li>
 *
 * <li>
 * <p>
 * <b>DeMorgan's Laws</b>. A negation of a union or intersection of types (e.g.
 * <code>!(T1|T2)</code>) is reduced to an intersection or union of negated
 * types (e.g. <code>(!T1)&(!T2)</code>).
 * </p>
 * </li>
 *
 * </ul>
 *
 * Whilst these rules are not comprehensive, they provide a good degree of
 * simplification which can greatly enhance the readability of a given type.
 *
 * @author David J. Pearce
 *
 */
public class AlgebraicTypeSimplifier extends AbstractTypeRewriter {
	private static Type T_VOID = new Type.Void();

	@Override
	protected Type rewriteDifference(Type.Difference type) {
		Type lhs = type.getLeftHandSide();
		Type rhs = type.getRightHandSide();
		Type nLhs = rewrite(lhs);
		Type nRhs = rewrite(rhs);
		if (nLhs instanceof Type.Primitive && nRhs instanceof Type.Primitive) {
			return nLhs.equals(nLhs) ? Type.Void : nLhs;
		} else if (nLhs instanceof Type.Union) {
			Type.Union union = (Type.Union) nLhs;
			Type[] types = new Type[union.size()];
			for (int i = 0; i != union.size(); ++i) {
				types[i] = rewriteDifference(new Type.Difference(union.get(i), nRhs));
			}
			return rewriteUnion(new Type.Union(types));
		} else if(nRhs instanceof Type.Union) {
			Type.Union union = (Type.Union) nRhs;
			Type[] types = new Type[union.size()];
			for(int i=0;i!=types.length;++i) {
				types[i] = rewriteDifference(new Type.Difference(nLhs, union.get(i)));
			}
			return rewriteIntersection(new Type.Intersection(types));
		} else if (nRhs instanceof Type.Void) {
			return nLhs;
		} else if (nLhs == lhs && nRhs == rhs) {
			return type;
		} else {
			// FIXME: we could do more here I think. In particular, if we have two non-open
			// records.
			return new Type.Difference(nLhs, nRhs);
		}
	}

	@Override
	protected Type rewriteUnion(Type.Union type) {
		Type[] types = type.getAll();
		Type[] nTypes = rewrite(types);
		// Expand any nested unions
		nTypes = inlineNestedDisjuncts(nTypes);
		// Remove any duplicate types
		nTypes = ArrayUtils.removeDuplicates(nTypes);
		// Remove all occurences of void
		nTypes = ArrayUtils.removeAll(nTypes, T_VOID);
		//
		if(types.length == 0) {
			return new Type.Void();
		} else if(types.length == 1) {
			return types[0];
		} else if(types == nTypes) {
			return type;
		} else {
			return new Type.Union(nTypes);
		}
	}

	@Override
	protected Type rewriteIntersection(Type.Intersection type) {
		Type[] types = type.getAll();
		Type[] nTypes = rewrite(types);
		// Check whether matches any
		if (ArrayUtils.firstIndexOf(types, T_VOID) >= 0) {
			// Any intersection containing void equals void
			return T_VOID;
		}
		// Expand any nested unions
		nTypes = inlineNestedConjuncts(nTypes);
		// Remove any duplicate types
		nTypes = ArrayUtils.removeDuplicates(nTypes);
		//
		if(types.length == 1) {
			return types[0];
		} else if(types == nTypes) {
			return type;
		} else {
			return new Type.Intersection(nTypes);
		}
	}

	private Type[] inlineNestedDisjuncts(Type[] types) {
		Type[] nTypes = types;
		for(int i=0;i!=nTypes.length;++i) {
			Type type = nTypes[i];
			if(type instanceof Type.Union) {
				// We found a nested disjunct!
				Type.Union disjunct = (Type.Union) type;
				Type[] nested = disjunct.getAll();
				// Inline the nested disjunct's operands
				nTypes = inlineNestedArray(nTypes,i,nested);
				// Can safely skip all elements in nested since disjunct already
				// in simplified form by construction.
				i += (nested.length - 1);
			}
		}
		return nTypes;
	}

	private Type[] inlineNestedConjuncts(Type[] types) {
		Type[] nTypes = types;
		for (int i = 0; i != nTypes.length; ++i) {
			Type type = nTypes[i];
			if (type instanceof Type.Intersection) {
				// We found a nested conjunct!
				Type.Intersection conjunct = (Type.Intersection) type;
				Type[] nested = conjunct.getAll();
				// Inline the nested conjunct's operands
				nTypes = inlineNestedArray(nTypes, i, nested);
				// Can safely skip all elements in nested since conjunct already
				// in simplified form by construction.
				i += (nested.length - 1);
			}
		}
		return nTypes;
	}

	private static Type[] inlineNestedArray(Type[] parent, int index, Type[] child) {
		Type[] types = new Type[parent.length + child.length - 1];
		System.arraycopy(parent, 0, types, 0, index);
		System.arraycopy(child, 0, types, index, child.length);
		System.arraycopy(parent, index + 1, types, index + child.length, parent.length - (index + 1));
		return types;
	}
}
