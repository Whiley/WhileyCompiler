package wyc.type.util;

import wyc.lang.WhileyFile.Type;
import wycc.util.ArrayUtils;

/**
 * A simple collection of rewrite rules that attempts to simplify types in
 * relatively obvious ways.
 *
 * @author David J. Pearce
 *
 */
public class StdTypeRewriter extends AbstractTypeRewriter {
	private static Type T_ANY = new Type.Any();
	private static Type T_VOID = new Type.Void();

	@Override
	protected Type rewriteNegation(Type.Negation type) {
		Type element = type.getElement();
		Type nElement = rewrite(element);
		if (nElement instanceof Type.Negation || nElement instanceof Type.Union
				|| nElement instanceof Type.Intersection) {
			return negate(nElement);
		} else if (element == nElement) {
			return type;
		} else {
			return new Type.Negation(nElement);
		}
	}

	@Override
	protected Type rewriteUnion(Type.Union type) {
		Type[] types = type.getAll();
		Type[] nTypes = rewrite(types);
		// Check whether matches any
		if (ArrayUtils.firstIndexOf(types, T_ANY) >= 0) {
			// Any union containing any equals any
			return T_ANY;
		}
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
		// Remove all occurences of void
		nTypes = ArrayUtils.removeAll(nTypes, T_ANY);
		//
		if(types.length == 0) {
			return new Type.Any();
		} else if(types.length == 1) {
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

	private static Type[] negate(Type[] types) {
		Type[] nTypes = new Type[types.length];
		for(int i=0;i!=types.length;++i) {
			nTypes[i] = negate(types[i]);
		}
		return nTypes;
	}

	private static Type negate(Type type) {
		if (type instanceof Type.Negation) {
			return ((Type.Negation) type).getElement();
		} else if (type instanceof Type.Union) {
			Type.Union disjunct = (Type.Union) type;
			Type[] negated = negate(disjunct.getAll());
			return new Type.Intersection(negated);
		} else if (type instanceof Type.Intersection) {
			Type.Intersection conjunct = (Type.Intersection) type;
			Type[] negated = negate(conjunct.getAll());
			return new Type.Union(negated);
		} else {
			return new Type.Negation(type);
		}
	}
}
