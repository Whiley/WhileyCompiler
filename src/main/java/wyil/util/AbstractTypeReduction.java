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
import java.util.function.Function;

import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.Type;

/**
 * A starting point for describing coinductive reductions of types to a given
 * value. They key difficulty with types in Whiley is that they can be recursive
 * and this often leads to an infinite loop when traversing a type. For example,
 * consider this simple type:
 *
 * <pre>
 * type List is null | { int data, List next}
 * </pre>
 *
 * A type such as <code>List</code> is represented as <i>nominal type</i>
 * referring to its declaration. Thus, when traversing a <code>List</code> type
 * we will eventually reach the <code>List</code> declaration for a second time.
 * If we did not stop at this point, then we would enter an infinite loop. The
 * purpose of this class is simply to managing such traversals.
 *
 *
 * @author David J. Pearce
 *
 * @param <R>
 */
public abstract class AbstractTypeReduction<R> implements Function<Type,R> {
	private HashSet<Type> visited = null;

	@Override
	public R apply(Type type) {
		return visitType(type);
	}

	private R visitType(Type type) {
		switch (type.getOpcode()) {
		case TYPE_any:
			return constructTypeAny((Type.Any) type);
		case TYPE_array:
			return visitTypeArray((Type.Array) type);
		case TYPE_bool:
			return constructTypeBool((Type.Bool) type);
		case TYPE_byte:
			return constructTypeByte((Type.Byte) type);
		case TYPE_int:
			return constructTypeInt((Type.Int) type);
		case TYPE_nominal:
			return visitTypeNominal((Type.Nominal) type);
		case TYPE_null:
			return constructTypeNull((Type.Null) type);
		case TYPE_record:
			return visitTypeRecord((Type.Record) type);
		case TYPE_reference:
			return visitTypeReference((Type.Reference) type);
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
			return visitTypeCallable((Type.Callable) type);
		case TYPE_tuple:
			return visitTypeTuple((Type.Tuple) type);
		case TYPE_union:
			return visitTypeUnion((Type.Union) type);
		case TYPE_unknown:
			return constructTypeUnresolved((Type.Unknown) type);
		case TYPE_void:
			return constructTypeVoid((Type.Void) type);
		case TYPE_universal:
			return constructTypeVariable((Type.Universal) type);
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	private R visitTypeCallable(Type.Callable type) {
		switch (type.getOpcode()) {
		case TYPE_function:
			visitTypeFunction((Type.Function) type);
		case TYPE_method:
			visitTypeMethod((Type.Method) type);
		case TYPE_property:
			visitTypeProperty((Type.Property) type);
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	private R visitTypeArray(Type.Array type) {
		R item = visitType(type.getElement());
		return constructTypeArray(type,item);
	}

	private R visitTypeFunction(Type.Function type) {
		R p = visitType(type.getParameter());
		R r = visitType(type.getReturn());
		return constructTypeFunction(type,p,r);
	}

	private R visitTypeMethod(Type.Method type) {
		R p = visitType(type.getParameter());
		R r = visitType(type.getReturn());
		return constructTypeMethod(type,p,r);
	}

	private R visitTypeNominal(Type.Nominal type) {
		Type concreteType = type.getConcreteType();
		//
		if(visited != null && visited.contains(concreteType)) {
			// Coinductive instantation encountered
			return constructTypeNominal(type,null);
		} else if(visited == null) {
			visited = new HashSet<>();
		}
		visited.add(concreteType);
		R item = visitType(type.getConcreteType());
		visited.remove(concreteType);
		return constructTypeNominal(type,item);
	}

	private R visitTypeProperty(Type.Property type) {
		R p = visitType(type.getParameter());
		R r = visitType(type.getReturn());
		return constructTypeProperty(type,p,r);
	}

	private R visitTypeRecord(Type.Record type) {
		ArrayList<R> items = new ArrayList<>();
		Tuple<Type.Field> fields = type.getFields();
		for(int i=0;i!=fields.size();++i) {
			items.add(visitType(fields.get(i).getType()));
		}
		return constructTypeRecord(type,items);
	}

	private R visitTypeReference(Type.Reference type) {
		R item = visitType(type.getElement());
		return constructTypeReference(type,item);
	}

	private R visitTypeTuple(Type.Tuple type) {
		ArrayList<R> items = new ArrayList<>();

		for (int i = 0; i != type.size(); ++i) {
			items.add(visitType(type.get(i)));
		}
		return constructTypeTuple(type,items);
	}

	private R visitTypeUnion(Type.Union type) {
		ArrayList<R> items = new ArrayList<>();
		for(int i=0;i!=type.size();++i) {
			items.add(visitType(type.get(i)));
		}
		return constructTypeUnion(type,items);
	}

	/**
	 * Construct a result for the type <code>any</code>. In many cases, this will
	 * not be necessary
	 *
	 * @param type
	 * @return
	 */
	public R constructTypeAny(Type.Any type) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Construct a result for a given array type and (constructed) element.
	 *
	 * @param type
	 * @param child
	 * @return
	 */
	abstract public R constructTypeArray(Type.Array type, R child);

	/**
	 * Construct a reult for a given <code>bool</code> type.
	 *
	 * @param type
	 * @return
	 */
	abstract public R constructTypeBool(Type.Bool type);

	/**
	 * Construct a reult for a given <code>byte</code> type.
	 *
	 * @param type
	 * @return
	 */
	abstract public R constructTypeByte(Type.Byte type);

	/**
	 * Construct a reult for a given <code>int</code> type.
	 *
	 * @param type
	 * @return
	 */
	abstract public R constructTypeInt(Type.Int type);

	/**
	 * Construct a result for a given function type and (constructed) parameter and
	 * return elements.
	 *
	 * @param type
	 * @param param
	 * @param ret
	 * @return
	 */
	abstract public R constructTypeFunction(Type.Function type, R param, R ret);

	/**
	 * Construct a result for a given method type and (constructed) parameter and
	 * return elements.
	 *
	 * @param type
	 * @param param
	 * @param ret
	 * @return
	 */
	abstract public R constructTypeMethod(Type.Method type, R param, R ret);

	/**
	 * Construct a result for a given nominal type and (constructed) concrete
	 * element. Observe that, if <code>child == null</code> them this is a
	 * coinductive construction. In otherwords, this is a recursive type and we are
	 * seeing the declaration for the second time.
	 *
	 * @param type
	 * @param param
	 * @param ret
	 * @return
	 */
	abstract public R constructTypeNominal(Type.Nominal type, R child);

	/**
	 * Construct a reult for a given <code>null</code> type.
	 *
	 * @param type
	 * @return
	 */
	abstract public R constructTypeNull(Type.Null type);

	/**
	 * Construct a result for a given property type and (constructed) parameter and
	 * return elements.
	 *
	 * @param type
	 * @param param
	 * @param ret
	 * @return
	 */
	abstract public R constructTypeProperty(Type.Property type, R param, R ret);

	/**
	 * Construct a result for a given record type and (constructed) field elements.
	 *
	 * @param type
	 * @param param
	 * @param ret
	 * @return
	 */
	abstract public R constructTypeRecord(Type.Record type, List<R> children);

	/**
	 * Construct a result for a given reference type and (constructed) element.
	 *
	 * @param type
	 * @param param
	 * @param ret
	 * @return
	 */
	abstract public R constructTypeReference(Type.Reference type, R child);

	/**
	 * Construct a result for a given tuple type and (constructed) elements.
	 *
	 * @param type
	 * @param param
	 * @param ret
	 * @return
	 */
	abstract public R constructTypeTuple(Type.Tuple type, List<R> children);

	/**
	 * Construct a result for a given union type and (constructed) elements.
	 *
	 * @param type
	 * @param param
	 * @param ret
	 * @return
	 */
	abstract public R constructTypeUnion(Type.Union type, List<R> children);

	/**
	 * Construct a reult for an unresolved type.
	 *
	 * @param type
	 * @return
	 */
	abstract public R constructTypeUnresolved(Type.Unknown type);

	/**
	 * Construct a reult for  a given <code>void</code> type.
	 *
	 * @param type
	 * @return
	 */
	abstract public R constructTypeVoid(Type.Void type);

	/**
	 * Construct a reult for  a given (universal) type variable.
	 *
	 * @param type
	 * @return
	 */
	abstract public R constructTypeVariable(Type.Universal type);
}
