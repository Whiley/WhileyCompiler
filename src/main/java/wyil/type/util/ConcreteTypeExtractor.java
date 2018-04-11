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
package wyil.type.util;

import static wyc.lang.WhileyFile.SEMTYPE_array;
import static wyc.lang.WhileyFile.SEMTYPE_difference;
import static wyc.lang.WhileyFile.SEMTYPE_intersection;
import static wyc.lang.WhileyFile.SEMTYPE_record;
import static wyc.lang.WhileyFile.SEMTYPE_reference;
import static wyc.lang.WhileyFile.SEMTYPE_staticreference;
import static wyc.lang.WhileyFile.SEMTYPE_union;
import static wyc.lang.WhileyFile.TYPE_array;
import static wyc.lang.WhileyFile.TYPE_bool;
import static wyc.lang.WhileyFile.TYPE_byte;
import static wyc.lang.WhileyFile.TYPE_function;
import static wyc.lang.WhileyFile.TYPE_int;
import static wyc.lang.WhileyFile.TYPE_method;
import static wyc.lang.WhileyFile.TYPE_nominal;
import static wyc.lang.WhileyFile.TYPE_null;
import static wyc.lang.WhileyFile.TYPE_record;
import static wyc.lang.WhileyFile.TYPE_reference;
import static wyc.lang.WhileyFile.TYPE_staticreference;
import static wyc.lang.WhileyFile.TYPE_union;
import static wyc.lang.WhileyFile.TYPE_void;
import static wyc.util.ErrorMessages.errorMessage;

import java.util.ArrayList;
import java.util.function.BiFunction;

import wybs.lang.CompilationUnit;
import wybs.lang.NameResolver;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError;
import wybs.lang.NameResolver.ResolutionError;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.Type;
import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.subtyping.SubtypeOperator;

/**
 * <p>
 * Extract a concrete type (i.e. instance of <code>Type</code>) from a given
 * instance of <code>SemanticType</code>. In many cases, this is
 * straightforward. For example, many types already are semantic types and be
 * just returned directly (e.g. <code>bool</code>,<code>int</code>, etc). In
 * other cases, such as for records or arrays, we just need to recursively
 * reconstruct the concrete type. However, intersections and differences are the
 * most challenging. This is because there is no concrete <code>Type</code>
 * which corresponds to either <code>SemanticType.Intersection</code> or
 * <code>SemanticType.Difference</code>. Instead, we have extract the best
 * possible approximation. The following illustrates a simple situation:
 * </p>
 *
 * <pre>
 * function f(int|null x) -> (int r):
 *   if x is int:
 *     return x
 *   else:
 *     return 0
 * </pre>
 *
 * <p>
 * Here, we must determine the concrete type of the returned expression
 * <code>x</code> from its semantic type, which is
 * <code>(int|null) & int</code>. In this case, the result is simply
 * <code>int</code>.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class ConcreteTypeExtractor implements BiFunction<SemanticType,LifetimeRelation,Type> {
  private final NameResolver resolver;

  public ConcreteTypeExtractor(NameResolver resolver) {
    this.resolver = resolver;
  }

  /**
   * Extract an instance of <code>Type</code> from <code>SemanticType</code> which
   * is the best possible approximation.
   *
   * @param type
   *          The semantic type being converted into a concrete type.
   * @return
   */
  @Override
  public Type apply(SemanticType type, LifetimeRelation lifetimes) {
    switch (type.getOpcode()) {
    case SEMTYPE_array:
      return apply((SemanticType.Array) type,lifetimes);
    case SEMTYPE_reference:
    case SEMTYPE_staticreference:
      return apply((SemanticType.Reference) type,lifetimes);
    case SEMTYPE_record:
      return apply((SemanticType.Record) type,lifetimes);
    case SEMTYPE_union:
      return extractUnion((SemanticType.Union) type,lifetimes);
    case SEMTYPE_intersection:
      return extractIntersection((SemanticType.Intersection) type,lifetimes);
    case SEMTYPE_difference:
      return extractDifference((SemanticType.Difference) type,lifetimes);
    default:
      // NOTE: all other cases are already instances of Type
      return (Type) type;
    }
  }

  /**
   * Extracting a concrete type from a semantic array is relatively easy. We just
   * recursively extract the concrete element type. Example of how this can arise:
   *
   * <pre>
   * function f() -> (int[] r):
   *   return [1,2,null]
   * </pre>
   *
   * The type generated for <code>[1,2,null]</code> will be a semantic array type
   * <code>(int|null)[]</code>.
   *
   * @param type
   * @return
   */
  private Type apply(SemanticType.Array type, LifetimeRelation lifetimes) {
    return new Type.Array(apply(type.getElement(), lifetimes));
  }

  private Type apply(SemanticType.Reference type, LifetimeRelation lifetimes) {
    return new Type.Reference(apply(type.getElement(),lifetimes), type.getLifetime());
  }

  private Type apply(SemanticType.Record type, LifetimeRelation lifetimes) {
    Tuple<? extends SemanticType.Field> t_fields = type.getFields();
    Type.Field[] fields = new Type.Field[t_fields.size()];
    for (int i = 0; i != fields.length; ++i) {
      SemanticType.Field tField = t_fields.get(i);
      Type fieldT = apply(tField.getType(),lifetimes);
      fields[i] = new Type.Field(tField.getName(), fieldT);
    }
    return new Type.Record(type.isOpen(), new Tuple<>(fields));
  }

  private Type extractUnion(SemanticType.Union type, LifetimeRelation lifetimes) {
    Type[] bounds = new Type[type.size()];
    for (int i = 0; i != bounds.length; ++i) {
      bounds[i] = apply(type.get(i),lifetimes);
    }
    return new Type.Union(bounds);
  }

  private Type extractIntersection(SemanticType.Intersection type, LifetimeRelation lifetimes) {
    Type result = apply(type.get(0),lifetimes);
    for (int i = 1; i != type.size(); ++i) {
      result = intersect(result, type.get(i), true, lifetimes);
    }
    return result;
  }

  private Type extractDifference(SemanticType.Difference type, LifetimeRelation lifetimes) {
    Type result = apply(type.getLeftHandSide(), lifetimes);
    return intersect(result, type.getRightHandSide(), false, lifetimes);
  }

  /**
   * Intersect a concrete type with a semantic type. The rough idea is that the
   * semantic type "selects" from the concrete type. For example, when
   * intersecting <code>int|null</code> with <code>int</code> we are selecting the
   * latter from the former. More complex situations arise though.
   *
   * @param lhs
   *            Concrete type being intersected against
   * @param rhs
   *            Semantic type being intersected with
   * @param sign
   *            The sign of the rhs
   * @return
   */
  private Type intersect(Type lhs, SemanticType rhs, boolean sign, LifetimeRelation lifetimes) {
    int lhsKind = normalise(lhs);
    int rhsKind = normalise(rhs);
    if (lhsKind == rhsKind) {
      // Easy case.
      switch (lhsKind) {
      case TYPE_null:
        case TYPE_bool:
        case TYPE_byte:
        case TYPE_int:
        	return sign ? lhs : Type.Void;
        case TYPE_array:
          return intersect((Type.Array) lhs, (SemanticType.Array) rhs, sign, lifetimes);
        case TYPE_reference:
          return intersect((Type.Reference) lhs, (SemanticType.Reference) rhs, sign, lifetimes);
        case TYPE_record:
          return intersect((Type.Record) lhs, (SemanticType.Record) rhs, sign, lifetimes);
        case TYPE_union:
          return intersect((Type.Union) lhs, rhs, sign, lifetimes);
        case TYPE_function:
          return intersect((Type.Function) lhs, (Type.Function) rhs, sign, lifetimes);
        case TYPE_method:
          return intersect((Type.Method) lhs, (Type.Method) rhs, sign, lifetimes);
        case TYPE_nominal:
          return intersect((Type.Nominal) lhs, rhs, sign, lifetimes);
        default:
        	throw new IllegalArgumentException("invalid type encountered: " + lhs);
      }
    } else if (lhs instanceof Type.Union) {
      return intersect((Type.Union) lhs, rhs, sign, lifetimes);
    } else if (lhs instanceof Type.Nominal) {
      return intersect((Type.Nominal) lhs, rhs, sign, lifetimes);
    } else if (rhs instanceof SemanticType.Union) {
      return intersect(lhs, (SemanticType.Union) rhs, sign, lifetimes);
    } else if (rhs instanceof SemanticType.Intersection) {
      return intersect(lhs, (SemanticType.Intersection) rhs, sign, lifetimes);
    } else if (rhs instanceof SemanticType.Difference) {
      return intersect(lhs, (SemanticType.Difference) rhs, sign, lifetimes);
    } else {
      return sign ? Type.Void : lhs;
    }
  }

  /**
   * Intersect a concrete array with a semantic array. The following illustrates
   * how this might arise:
   *
   * <pre>
   * function f((int|null)[] xs) -> int:
   *    if x is int[]:
   *      return |x|
   *    else:
   *      return 0
   * </pre>
   *
   * Here, the concrete type for <code>x</code> in <code>|x|</code> will be
   * <code>(int|null)[] & int[]</code>, which produces just <code>int[]</code>.
   *
   * @param lhs
   *            Concrete type being intersected against
   * @param rhs
   *            Semantic type being intersected with
   * @param sign
   *            The sign of the rhs
   * @return
   */
  private Type intersect(Type.Array lhs, SemanticType.Array rhs, boolean sign, LifetimeRelation lifetimes) {
    return new Type.Array(intersect(lhs.getElement(), rhs.getElement(), sign, lifetimes));
  }

  private Type intersect(Type.Reference lhs, SemanticType.Reference rhs, boolean sign,
      LifetimeRelation lifetimes) {
    String lhsLifetime = extractLifetime(lhs);
    String rhsLifetime = extractLifetime(rhs);
    // Check whether rhs within lhs
    boolean lhsWithinRhs = lifetimes.isWithin(lhsLifetime, rhsLifetime);
    // Check whether lhs within rhs
    boolean rhsWithinLhs = lifetimes.isWithin(rhsLifetime, lhsLifetime);
    // Check whether elements of lhs and rhs are equivalent.
    // FIXME: equivalence test here is too simplistic and needs an emptiness test.
    Type element = intersect(lhs.getElement(), rhs.getElement(), true, lifetimes);
    boolean elemEqual = lhs.getElement().equals(element);
    // NOTE: logic below is suspect!!
    if (sign && elemEqual && lhsWithinRhs && rhsWithinLhs) {
      return lhs;
    } else if (!sign && (!elemEqual || !lhsWithinRhs || !rhsWithinLhs)) {
      return lhs;
    } else {
      return Type.Void;
    }
  }

  private Type intersect(Type.Record lhs, SemanticType.Record rhs, boolean sign, LifetimeRelation lifetimes) {
    if (sign) {
      return intersect(lhs, rhs, lifetimes);
    } else {
      return subtract(lhs, rhs, lifetimes);
    }
  }

  private Type intersect(Type.Record lhs, SemanticType.Record rhs, LifetimeRelation lifetimes) {
    Tuple<Type.Field> lhsFields = lhs.getFields();
    Tuple<? extends SemanticType.Field> rhsFields = rhs.getFields();
    // Determine the number of matching fields. That is, fields with the
    // same name.
    int matches = countMatchingFields(lhsFields, rhsFields);
    // When intersecting two records, the number of fields is only
    // allowed to differ if one of them is an open record. Therefore, we
    // need to pay careful attention to the size of the resulting match
    // in comparison with the original records.
    if (matches < lhsFields.size() && !rhs.isOpen()) {
      // Not enough matches made to meet the requirements of the lhs
      // type.
      return Type.Void;
    } else if (matches < rhsFields.size() && !lhs.isOpen()) {
      // Not enough matches made to meet the requirements of the rhs
      // type.
      return Type.Void;
    } else {
      // At this point, we know the intersection succeeds. The next
      // job is to determine the final set of field declarations.
      int lhsRemainder = lhsFields.size() - matches;
      int rhsRemainder = rhsFields.size() - matches;
      Type.Field[] fields = new Type.Field[matches + lhsRemainder + rhsRemainder];
      // Extract all matching fields first
      int index = extractMatchingFields(lhsFields, rhsFields, fields, lifetimes);
      // Extract remaining lhs fields second
      index = extractNonMatchingFields(lhsFields, rhsFields, fields, index);
      // Extract remaining rhs fields last
      index = extractNonMatchingFields(rhsFields, lhsFields, fields, index);
      // The intersection of two records can only be open when both
      // are themselves open.
      boolean isOpen = lhs.isOpen() && rhs.isOpen();
      //
      return new Type.Record(isOpen, new Tuple<>(fields));
    }
  }

  private Type subtract(Type.Record lhs, SemanticType.Record rhs, LifetimeRelation lifetimes) {
    //
    ArrayList<Type.Field> fields = new ArrayList<>();
    Tuple<Type.Field> lhsFields = lhs.getFields();
    Tuple<? extends SemanticType.Field> rhsFields = rhs.getFields();
    for (int i = 0; i != lhsFields.size(); ++i) {
      Type.Field lhsField = lhsFields.get(i);
      Identifier lhsFieldName = lhsField.getName();
      boolean matched = false;
      for (int j = 0; j != rhsFields.size(); ++j) {
        SemanticType.Field rhsField = rhsFields.get(j);
        Identifier rhsFieldName = rhsField.getName();
        if (lhsFieldName.equals(rhsFieldName)) {
          Type diff = intersect(lhsField.getType(), rhsField.getType(), false, lifetimes);
          fields.add(new Type.Field(lhsFieldName, diff));
          matched = true;
          break;
        }
      }
      //
      if (!matched && !rhs.isOpen()) {
        // We didn't find a corresponding field, and the rhs is fixed. This means the
        // rhs is not compatible with the lhs and can be ignored.
        return lhs;
      } else if (!matched) {
        // We didn't find a corresponding field, and the rhs is open. This just means
        // the rhs is not taking anything away from the lhs (for this field).
        fields.add(lhsField);
      }
    }
    return new Type.Record(lhs.isOpen(), new Tuple<>(fields));
  }

  private Type intersect(Type.Function lhs, Type.Function rhs, boolean sign, LifetimeRelation lifetimes) {
    if (lhs.equals(rhs)) {
      return sign ? lhs : Type.Void;
    } else {
      return sign ? Type.Void : lhs;
    }
  }

  private Type intersect(Type.Method lhs, Type.Method rhs, boolean sign, LifetimeRelation lifetimes) {
    if (lhs.equals(rhs)) {
      return sign ? lhs : Type.Void;
    } else {
      return sign ? Type.Void : lhs;
    }
  }

  private Type intersect(Type.Union lhs, SemanticType rhs, boolean sign, LifetimeRelation lifetimes) {
    Type[] types = new Type[lhs.size()];
    for (int i = 0; i != lhs.size(); ++i) {
      types[i] = intersect(lhs.get(i), rhs, sign, lifetimes);
    }
    return union(types);
  }

  private Type intersect(Type.Nominal lhs, SemanticType rhs, boolean sign, LifetimeRelation lifetimes) {
    try {
      Decl.Type decl = resolver.resolveExactly(lhs.getName(), Decl.Type.class);
      return intersect(decl.getVariableDeclaration().getType(), rhs, sign, lifetimes);
    } catch (ResolutionError e) {
      return syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, lhs.getName().toString()), lhs);
    }
  }

  /**
   * <p>
   * Intersect a given non-union type (e.g. int) with a given semantic union
   * (int|null). There are some tricky cases. For example, intersection
   * <code>{int|null f}</code> with <code>{int f}|{null f}</code>. The following
   * example illustrates how this can arise:
   * </p>
   *
   * <pre>
   * function f(int|{int|null f} x) -> (int|null r):
   *  if x is {int f}|{null f}:
   *      return x.f
   *  else:
   *    return null
   * </pre>
   * <p>
   * The question is, what should the resulting type of <code>x</code> be after
   * this? An easy solution is to determine the type as
   * <code>int|{int|null f}&({int f}|{null f})</code> which reduces to
   * <code>({int|null f}&{int f})|({int|null f}&{null f})</code> and, finally, to
   * <code>{int f}|{null f}</code>. But, does that make sense?
   * </p>
   *
   * <b>NOTE:</b> In the case that the rhs is negative, then this is relatively
   * straightforward.
   *
   * @param lhs
   * @param rhs
   * @param sign
   * @return
   */
	private Type intersect(Type lhs, SemanticType.Union rhs, boolean sign, LifetimeRelation lifetimes) {
		if (sign) {
			return union(lhs, sign, lifetimes, rhs.getAll());
		} else {
			return intersect(lhs, sign, lifetimes, rhs.getAll());
		}
	}

  /**
   * Intersect a given non-union type (e.g. int) with a given semantic
   * intersection (int&null). In the case the rhs is positive, then this is
   * relatively straightforward. When the rhs is negative, there are some tricky
   * cases. For example, intersection <code>{int|null f}</code> with
   * <code>!({int f}&{null f})</code>.
   *
   * @param lhs
   * @param rhs
   * @param sign
   * @return
   */
  private Type intersect(Type lhs, SemanticType.Intersection rhs, boolean sign, LifetimeRelation lifetimes) {
    if (sign) {
      return intersect(lhs, sign, lifetimes, rhs.getAll());
    } else {
      return union(lhs, sign, lifetimes, rhs.getAll());
    }
  }

  private Type intersect(Type lhs, SemanticType.Difference rhs, boolean sign, LifetimeRelation lifetimes) {
    if (sign) {
      // A & (B - C) == A & (B & !C)
      lhs = intersect(lhs, rhs.getLeftHandSide(), true, lifetimes);
      return intersect(lhs, rhs.getRightHandSide(), false, lifetimes);
    } else {
      // A & !(B - C) == A & !(B & !C) == A & (!B | C)
      Type[] types = new Type[1];
      types[0] = intersect(lhs, rhs.getLeftHandSide(), false, lifetimes);
      types[1] = intersect(lhs, rhs.getRightHandSide(), true, lifetimes);
      return union(types);
    }
  }

  private Type intersect(Type lhs, boolean sign, LifetimeRelation lifetimes, SemanticType... elements) {
    for (int i = 0; i != elements.length; ++i) {
      lhs = intersect(lhs, elements[i], sign, lifetimes);
    }
    return lhs;
  }

  private Type union(Type lhs, boolean sign, LifetimeRelation lifetimes, SemanticType... elements) {
    Type[] types = new Type[elements.length];
    for (int i = 0; i != types.length; ++i) {
      types[i] = intersect(lhs, elements[i], sign, lifetimes);
    }
    return union(types);
  }

  // ============================================================================
  // Helpers
  // ============================================================================

  private String extractLifetime(SemanticType.Reference ref) {
    if (ref.hasLifetime()) {
      return ref.getLifetime().get();
    } else {
      return "*";
    }
  }

  private Type union(Type...types) {
    types = ArrayUtils.removeAll(types, Type.Void);
    switch (types.length) {
    case 0:
      return Type.Void;
    case 1:
      return types[0];
    default:
      return new Type.Union(types);
    }
  }

  /**
   * Extract all matching fields (i.e. fields with the same name) into the result
   * array.
   *
   * @param lhsFields
   * @param rhsFields
   * @param result
   * @return
   */
  private int extractMatchingFields(Tuple<Type.Field> lhsFields,
      Tuple<? extends SemanticType.Field> rhsFields, Type.Field[] result,
      LifetimeRelation lifetimes) {
    int index = 0;
    // Extract all matching fields first.
    for (int i = 0; i != lhsFields.size(); ++i) {
      for (int j = 0; j != rhsFields.size(); ++j) {
        Type.Field lhsField = lhsFields.get(i);
        SemanticType.Field rhsField = rhsFields.get(j);
        Identifier lhsFieldName = lhsField.getName();
        Identifier rhsFieldName = rhsField.getName();
        if (lhsFieldName.equals(rhsFieldName)) {
          Type type = intersect(lhsField.getType(), rhsField.getType(), true, lifetimes);
          Type.Field combined = new Type.Field(lhsFieldName, type);
          result[index++] = combined;
        }
      }
    }
    return index;
  }

  /**
   * Extract fields from lhs which do not match any field in the rhs. That is,
   * there is no field in the rhs with the same name.
   *
   * @param lhsFields
   * @param rhsFields
   * @param result
   * @param index
   * @return
   */
  private static int extractNonMatchingFields(Tuple<? extends SemanticType.Field> lhsFields,
      Tuple<? extends SemanticType.Field> rhsFields, SemanticType.Field[] result, int index) {
    outer: for (int i = 0; i != lhsFields.size(); ++i) {
      for (int j = 0; j != rhsFields.size(); ++j) {
        SemanticType.Field lhsField = lhsFields.get(i);
        SemanticType.Field rhsField = rhsFields.get(j);
        Identifier lhsFieldName = lhsField.getName();
        Identifier rhsFieldName = rhsField.getName();
        if (lhsFieldName.equals(rhsFieldName)) {
          // This is a matching field. Therefore, continue on to the
          // next lhs field
          continue outer;
        }
      }
      result[index++] = lhsFields.get(i);
    }
    return index;
  }

  /**
   * Count the number of matching fields. That is, fields with the same name.
   *
   * @param lhsFields
   * @param rhsFields
   * @return
   */
  private static int countMatchingFields(Tuple<? extends SemanticType.Field> lhsFields,
      Tuple<? extends SemanticType.Field> rhsFields) {
    int count = 0;
    for (int i = 0; i != lhsFields.size(); ++i) {
      for (int j = 0; j != rhsFields.size(); ++j) {
        SemanticType.Field lhsField = lhsFields.get(i);
        SemanticType.Field rhsField = rhsFields.get(j);
        Identifier lhsFieldName = lhsField.getName();
        Identifier rhsFieldName = rhsField.getName();
        if (lhsFieldName.equals(rhsFieldName)) {
          count = count + 1;
        }
      }
    }
    return count;
  }

  private static int normalise(SemanticType type) {
    int opcode = type.getOpcode();
    switch (opcode) {
    case TYPE_void:
    case TYPE_null:
    case TYPE_bool:
    case TYPE_byte:
    case TYPE_int:
    case TYPE_nominal:
      return opcode;
    case TYPE_array:
    case SEMTYPE_array:
      return TYPE_array;
    case TYPE_reference:
    case TYPE_staticreference:
    case SEMTYPE_staticreference:
    case SEMTYPE_reference:
      return TYPE_reference;
    case TYPE_record:
    case SEMTYPE_record:
      return TYPE_record;
    case TYPE_union:
    case SEMTYPE_union:
      return TYPE_union;
    case TYPE_function:
      return TYPE_function;
    case TYPE_method:
      return TYPE_method;
    case SEMTYPE_intersection:
    case SEMTYPE_difference:
        return opcode;
    default:
      // Should be deadcode.
      throw new IllegalArgumentException("invalid type - " + type);
    }
  }


  private <T> T syntaxError(String msg, SyntacticItem e) {
    // FIXME: this is a kludge
    CompilationUnit cu = (CompilationUnit) e.getHeap();
    throw new SyntaxError(msg, cu.getEntry(), e);
  }
}
