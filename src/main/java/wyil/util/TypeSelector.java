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
// WITHOUT WARRANTIES OR CONDITIONS OF Type.Selector.TOP KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.util;

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
import static wyil.lang.WyilFile.TYPE_reference;
import static wyil.lang.WyilFile.TYPE_union;
import static wyil.lang.WyilFile.TYPE_tuple;
import static wyil.lang.WyilFile.TYPE_universal;
import static wyil.lang.WyilFile.TYPE_void;

import jsynheap.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Type;

public abstract class TypeSelector {

	/**
	 * Create a type selector from a given source and refinement. For example,
	 * <code>int|null</code> might be the source with <code>int</code> as the
	 * refinement.
	 *
	 * @param source
	 * @param refinement
	 * @return
	 */
	public static Type.Selector create(Type lhs, Type rhs) {
		return create(lhs,rhs,null);
	}

	private static Type.Selector create(Type t1, Type t2, BinaryRelation<Type> cache) {

		if (cache != null && cache.get(t1, t2)) {
			// FIXME: this is obviously wrong
			return Type.Selector.TOP;
		} else if (cache == null) {
			// Lazily construct cache.
			cache = new BinaryRelation.HashSet<>();
		}

		cache.set(t1, t2, true);
		//
		int t1_opcode = normalise(t1.getOpcode());
		int t2_opcode = normalise(t2.getOpcode());
		//
		if (t1_opcode == t2_opcode) {
			switch (t1_opcode) {
			case TYPE_void:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				return Type.Selector.TOP;
			case TYPE_array:
				return create((Type.Array) t1, (Type.Array) t2, cache);
			case TYPE_tuple:
				return create((Type.Tuple) t1, (Type.Tuple)t2, cache);
			case TYPE_record:
				return create((Type.Record) t1, (Type.Record)t2, cache);
			case TYPE_nominal:
				return create((Type.Nominal) t1, (Type.Nominal)t2, cache);
			case TYPE_union:
				return create((Type.Union) t1, t2, cache);
			case TYPE_reference:
				return create((Type.Reference) t1, (Type.Reference) t2, cache);
			case TYPE_method:
			case TYPE_function:
			case TYPE_property:
				return create((Type.Callable) t1, (Type.Callable) t2, cache);
			case TYPE_universal:
				return create((Type.Universal) t1, (Type.Universal) t2, cache);
			default:
				throw new IllegalArgumentException("unexpected type encountered: " + t1);
			}
		} else if (t2_opcode == TYPE_nominal) {
			return create(t1, (Type.Nominal) t2, cache);
		} else if (t2_opcode == TYPE_union) {
			return create(t1, t2, cache);
		} else if (t1_opcode == TYPE_union) {
			return create((Type.Union) t1, t2, cache);
		} else if (t1_opcode == TYPE_nominal) {
			return create((Type.Nominal) t1, (Type.Atom) t2, cache);
		} else {
			// Nothing else works except void
			return t2_opcode == TYPE_void ? Type.Selector.TOP : Type.Selector.BOTTOM;
		}
	}

	private static Type.Selector create(Type.Array t1, Type.Array t2, BinaryRelation<Type> cache) {
		Type.Selector element = create(t1.getElement(), t2.getElement(), cache);
		if (element == Type.Selector.TOP || element == Type.Selector.BOTTOM) {
			return element;
		} else {
			return new Type.Selector(element);
		}
	}

	private static Type.Selector create(Type.Tuple t1, Type.Tuple t2,
			BinaryRelation<Type> cache) {
		// Sanity check number of fields are reasonable.
		if (t1.size() != t2.size()) {
			return Type.Selector.BOTTOM;
		}
		Type.Selector[] items = new Type.Selector[t1.size()];
		// Check fields one-by-one.
		for (int i = 0; i != t1.size(); ++i) {
			Type f1 = t1.get(i);
			Type f2 = t2.get(i);
			Type.Selector s = create(f1, f2, cache);
			if (s == Type.Selector.BOTTOM) {
				return Type.Selector.BOTTOM;
			} else {
				items[i] = s;
			}
		}
		if (isTop(items)) {
			return Type.Selector.TOP;
		} else {
			return new Type.Selector(items);
		}
	}

	private static Type.Selector create(Type.Record t1, Type.Record t2,
			BinaryRelation<Type> cache) {
		Tuple<Type.Field> t1_fields = t1.getFields();
		Tuple<Type.Field> t2_fields = t2.getFields();
		// Sanity check number of fields are reasonable.
		if (t1_fields.size() != t2_fields.size()) {
			return Type.Selector.BOTTOM;
		} else if (t1.isOpen() != t2.isOpen()) {
			return Type.Selector.BOTTOM;
		}
		//
		Type.Selector[] items = new Type.Selector[t1_fields.size()];
		// Check fields one-by-one.

		// FIXME: this does not align with the relaxed subtype operator
		for (int i = 0; i != t1_fields.size(); ++i) {
			Type.Field f1 = t1_fields.get(i);
			Type.Field f2 = t2_fields.get(i);
			if (!f1.getName().equals(f2.getName())) {
				// Fields have differing names
				return Type.Selector.BOTTOM;
			}
			Type.Selector s = create(f1.getType(), f2.getType(), cache);
			if (s == Type.Selector.BOTTOM) {
				return Type.Selector.BOTTOM;
			} else {
				items[i] = s;
			}
		}
		if (isTop(items)) {
			return Type.Selector.TOP;
		} else {
			return new Type.Selector(items);
		}
	}

	private static Type.Selector create(Type.Reference t1, Type.Reference t2,
			BinaryRelation<Type> cache) {
		Type.Selector element = create(t1.getElement(), t2.getElement(), cache);
		if (element == Type.Selector.TOP) {
			return element;
		} else {
			return Type.Selector.BOTTOM;
		}
	}

	private static Type.Selector create(Type.Callable t1, Type.Callable t2, BinaryRelation<Type> cache) {
		Type t1_params = t1.getParameter();
		Type t2_params = t2.getParameter();
		Type t1_return = t1.getReturn();
		Type t2_return = t2.getReturn();
		// Eliminate easy cases first
		if (t1.getOpcode() != t2.getOpcode()) {
			return Type.Selector.BOTTOM;
		}
		// Check parameters
		if(Type.Selector.TOP != create(t1_params,t2_params)) {
			return Type.Selector.BOTTOM;
		}
		// Check return
		if(Type.Selector.TOP != create(t1_return,t2_return)) {
			return Type.Selector.BOTTOM;
		}
		// Done
		return Type.Selector.TOP;
	}

	private static Type.Selector create(Type.Universal t1, Type.Universal t2, BinaryRelation<Type> cache) {
		return t1.equals(t2) ? Type.Selector.TOP : Type.Selector.BOTTOM;
	}

	private static Type.Selector create(Type.Nominal t1, Type.Nominal t2, BinaryRelation<Type> cache) {
		Decl.Link<Decl.Type> l1 = t1.getLink();
		Decl.Link<Decl.Type> l2 = t2.getLink();
		//
		if(l1 == l2) {
			return Type.Selector.TOP;
		} else {
			return create(t1.getConcreteType(),t2.getConcreteType(),cache);
		}
	}

	private static Type.Selector create(Type t1, Type.Nominal t2, BinaryRelation<Type> cache) {
		return create(t1,t2.getConcreteType(),cache);
	}

	private static Type.Selector create(Type.Nominal t1, Type t2, BinaryRelation<Type> cache) {
		// FIXME: need to handle type invariants?
		return create(t1.getConcreteType(),t2,cache);
	}

	private static Type.Selector create(Type.Atom t1, Type.Union t2, BinaryRelation<Type> cache) {
		for(int i=0;i!=t2.size();++i) {
			Type.Selector s = create(t1,t2.get(i),cache);
			if(s == Type.Selector.TOP) {
				// Since this is an atom we know it cannot be further subdivided.
				return Type.Selector.TOP;
			}
		}
		return Type.Selector.BOTTOM;
	}

	private static Type.Selector create(Type.Union t1, Type t2, BinaryRelation<Type> cache) {
		Type.Selector[] items = new Type.Selector[t1.size()];
		for (int i = 0; i != items.length; ++i) {
			items[i] = create(t1.get(i), t2, cache);
		}
		if (isBottom(items)) {
			return Type.Selector.BOTTOM;
		} else if (isTop(items)) {
			return Type.Selector.TOP;
		} else {
			return new Type.Selector(items);
		}
	}

	private static boolean isBottom(Type.Selector[] items) {
		for (int i = 0; i != items.length; ++i) {
			if (items[i] != Type.Selector.BOTTOM) {
				return false;
			}
		}
		return true;
	}

	private static boolean isTop(Type.Selector[] items) {
		for (int i = 0; i != items.length; ++i) {
			if (items[i] != Type.Selector.TOP) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Normalise opcode for sake of simplicity. This allows us to compare the types
	 * of two operands more accurately using a switch.
	 *
	 * @param opcode
	 * @return
	 */
	protected static int normalise(int opcode) {
		switch(opcode) {
		case TYPE_method:
		case TYPE_property:
		case TYPE_function:
			return TYPE_function;
		}
		//
		return opcode;
	}
}
