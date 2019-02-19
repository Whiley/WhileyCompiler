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

import static wyil.lang.WyilFile.SEMTYPE_array;
import static wyil.lang.WyilFile.SEMTYPE_difference;
import static wyil.lang.WyilFile.SEMTYPE_intersection;
import static wyil.lang.WyilFile.SEMTYPE_record;
import static wyil.lang.WyilFile.SEMTYPE_reference;
import static wyil.lang.WyilFile.SEMTYPE_staticreference;
import static wyil.lang.WyilFile.SEMTYPE_union;

import java.util.function.BiFunction;

import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.SemanticType;
import wyil.lang.WyilFile.Type;
import wyil.type.subtyping.EmptinessTest;
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
public class ConcreteTypeExtractor implements BiFunction<SemanticType, LifetimeRelation, Type> {
	private final TypeIntersector intersector;
	private final TypeSubtractor subtractor;

	public ConcreteTypeExtractor(EmptinessTest<SemanticType> emptiness) {
		SubtypeOperator op = new SubtypeOperator(emptiness);
		this.intersector = new TypeIntersector(op);
		this.subtractor = new TypeSubtractor(op);
	}

	/**
	 * Extract an instance of <code>Type</code> from <code>SemanticType</code> which
	 * is the best possible approximation.
	 *
	 * @param type
	 *            The semantic type being converted into a concrete type.
	 * @return
	 */
	@Override
	public Type apply(SemanticType type, LifetimeRelation lifetimes) {
		switch (type.getOpcode()) {
		case SEMTYPE_array:
			return apply((SemanticType.Array) type, lifetimes);
		case SEMTYPE_reference:
		case SEMTYPE_staticreference:
			return apply((SemanticType.Reference) type, lifetimes);
		case SEMTYPE_record:
			return apply((SemanticType.Record) type, lifetimes);
		case SEMTYPE_union:
			return apply((SemanticType.Union) type, lifetimes);
		case SEMTYPE_intersection:
			return apply((SemanticType.Intersection) type, lifetimes);
		case SEMTYPE_difference:
			return apply((SemanticType.Difference) type, lifetimes);
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
		return new Type.Reference(apply(type.getElement(), lifetimes), type.getLifetime());
	}

	private Type apply(SemanticType.Record type, LifetimeRelation lifetimes) {
		Tuple<? extends SemanticType.Field> t_fields = type.getFields();
		Type.Field[] fields = new Type.Field[t_fields.size()];
		for (int i = 0; i != fields.length; ++i) {
			SemanticType.Field tField = t_fields.getOperand(i);
			Type fieldT = apply(tField.getType(), lifetimes);
			fields[i] = new Type.Field(tField.getName(), fieldT);
		}
		return new Type.Record(type.isOpen(), new Tuple<>(fields));
	}

	private Type apply(SemanticType.Union type, LifetimeRelation lifetimes) {
		Type[] bounds = new Type[type.size()];
		for (int i = 0; i != bounds.length; ++i) {
			bounds[i] = apply(type.getOperand(i), lifetimes);
		}
		return new Type.Union(bounds);
	}

	private Type apply(SemanticType.Intersection type, LifetimeRelation lifetimes) {
		Type result = apply(type.getOperand(0), lifetimes);
		for (int i = 1; i != type.size(); ++i) {
			Type ith = apply(type.getOperand(i), lifetimes);
			result = intersector.apply(result, ith, lifetimes);
		}
		return result;
	}

	private Type apply(SemanticType.Difference type, LifetimeRelation lifetimes) {
		Type lhs = apply(type.getLeftHandSide(), lifetimes);
		Type rhs = apply(type.getRightHandSide(), lifetimes);
		return subtractor.apply(lhs, rhs, lifetimes);
	}
}
