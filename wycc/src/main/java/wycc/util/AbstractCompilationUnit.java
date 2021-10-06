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
package wycc.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;

import jbfs.util.ArrayUtils;
import jbfs.util.Trie;
import wycc.lang.CompilationUnit;
import wycc.lang.SyntacticHeap;
import wycc.lang.SyntacticItem;

public abstract class AbstractCompilationUnit<T extends CompilationUnit> extends AbstractSyntacticHeap
		implements CompilationUnit {

	// ITEMS: 0000000 (0) -- 00001111 (15)
	public static final int ITEM_null = 0;
	public static final int ITEM_bool = 1;
	public static final int ITEM_int = 2;
	public static final int ITEM_utf8 = 3;
	public static final int ITEM_pair = 4;
	public static final int ITEM_tuple = 5;
	public static final int ITEM_array = 6;
	public static final int ITEM_ident = 7;
	public static final int ITEM_name = 8;
	public static final int ITEM_decimal = 9;
	public static final int ITEM_ref = 10;
	public static final int ITEM_dictionary = 11;
	public static final int ATTR_span = 14;
	public static final int ITEM_byte = 15; // deprecated

	public AbstractCompilationUnit() {

	}

	public AbstractCompilationUnit(CompilationUnit other) {
		super(other);
	}

	@Override
	public SyntacticHeap getParent() {
		return null;
	}

	/**
	 * Represents a "backlink" or "crossref" in the tree. That is, a non-owning
	 * reference which refers to another item. Copying a reference will not copy the
	 * item to which it refers and, hence, care must be taken when cloning items
	 * that contain them.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public static class Ref<T extends SyntacticItem> extends AbstractSyntacticItem {
		public Ref(T referent) {
			super(ITEM_ref,referent);
		}

		public T get() {
			return (T) get(0);
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof Ref) {
				Ref<?> r = (Ref<?>) o;
				return get() == r.get();
			}
			return false;
		}

		@Override
		public int hashCode() {
			// NOTE: whilst this is far from ideal it is necessary to break potential cycles
			// in the object graph.
			return 0;
		}

		@Override
		public SyntacticItem clone(SyntacticItem[] operands) {
			return new Ref(operands[0]);
		}

		public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.ONE, Data.ZERO,
				"ITEM_ref") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Ref(operands[0]);
			}
		};
	}


	/**
	 * Represents a pair of items in a compilation unit.
	 *
	 * @author David J. Pearce
	 *
	 * @param <K>
	 * @param <V>
	 */
	public static class Pair<K extends SyntacticItem, V extends SyntacticItem>
			extends AbstractSyntacticItem {
		public Pair(K lhs, V rhs) {
			super(ITEM_pair, lhs, rhs);
		}

		public K getFirst() {
			return (K) get(0);
		}

		public V getSecond() {
			return (V) get(1);
		}

		@Override
		public Pair<K, V> clone(SyntacticItem[] operands) {
			return new Pair<>((K) operands[0], (V) operands[1]);
		}

		@Override
		public String toString() {
			return "(" + getFirst() + ", " + getSecond() + ")";
		}

		public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.TWO, Data.ZERO,
				"ITEM_pair") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Pair<>(operands[0], operands[1]);
			}
		};
	}

	/**
	 * Represents a sequence of zero or more items in a compilation unit.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public static class Tuple<T extends SyntacticItem> extends AbstractSyntacticItem implements Iterable<T> {

		public Tuple(T... stmts) {
			super(ITEM_tuple, stmts);
		}

		public Tuple(Collection<T> stmts) {
			super(ITEM_tuple, ArrayUtils.toArray(SyntacticItem.class,stmts));
		}

		@Override
		public T get(int i) {
			return (T) super.get(i);
		}

		public Tuple<T> get(int start, int end) {
			SyntacticItem[] items = new SyntacticItem[end - start];
			for (int i = start; i < end; ++i) {
				items[i-start] = super.get(i);
			}
			return new Tuple(items);
		}

		public <S extends SyntacticItem> Tuple<S> map(Function<T, S> fn) {
			int size = size();
			SyntacticItem[] elements = new SyntacticItem[size];
			for (int i = 0; i != size; ++i) {
				elements[i] = fn.apply(get(i));
			}
			return new Tuple<>((S[]) elements);
		}

		/**
		 * Append a new item onto this tuple
		 *
		 * @param item
		 * @return
		 */
		public Tuple<T> append(T item) {
			SyntacticItem[] nitems = Arrays.copyOf(operands, operands.length+1);
			nitems[operands.length] = item;
			return new Tuple<>((T[]) nitems);
		}

		/**
		 * Append a new item onto this tuple
		 *
		 * @param item
		 * @return
		 */
		public Tuple<T> appendAll(Tuple<T> items) {
			SyntacticItem[] nitems = Arrays.copyOf(operands, operands.length + items.size());
			System.arraycopy(items.operands, 0, nitems, operands.length, items.size());
			return new Tuple<>((T[]) nitems);
		}

		/**
		 * Append a new item onto this tuple
		 *
		 * @param item
		 * @return
		 */
		public <S extends SyntacticItem> Tuple<T> removeAll(Collection<S> items) {
			SyntacticItem[] noperands = Arrays.copyOf(operands, operands.length);
			for(S item : items) {
				for(int i=0;i!=operands.length;++i) {
					if(operands[i] == item) {
						noperands[i] = null;
					}
				}
			}
			//
			noperands = ArrayUtils.removeAll(noperands, null);
			//
			return new Tuple<>((T[]) noperands);
		}

		@Override
		public Tuple<T> clone(SyntacticItem[] operands) {
			return new Tuple(ArrayUtils.toArray(SyntacticItem.class, operands));
		}

		@Override
		public String toString() {
			return "(" + toBareString() + ")";
		}

		public String toBareString() {
			String r = "";
			for (int i = 0; i != size(); ++i) {
				if (i != 0) {
					r += ",";
				}
				SyntacticItem child = get(i);
				if (child == null) {
					r += "?";
				} else {
					r += child.toString();
				}
			}
			return r;
		}

		@Override
		public Iterator<T> iterator() {
			// Create annonymous iterator for iterating over elements.
			return new Iterator<T>() {
				private int index = 0;

				@Override
				public boolean hasNext() {
					return index < size();
				}

				@Override
				public T next() {
					return get(index++);
				}

			};
		}

		public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.MANY, Data.ZERO,
				"ITEM_tuple") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Tuple<>(operands);
			}
		};
	}

	/**
	 * Represents an <i>identifier</i> in a compilation unit. For example, this
	 * could be used to represent a variable access. Or, it could be part of a
	 * partially or fully qualified name.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Identifier extends AbstractSyntacticItem implements CompilationUnit.Identifier {
		public Identifier(String name) {
			super(ITEM_ident, name.getBytes(StandardCharsets.UTF_8), new SyntacticItem[0]);
		}

		public Identifier(byte[] bytes) {
			super(ITEM_ident, bytes, new SyntacticItem[0]);
		}

		@Override
		public String get() {
			// FIXME: could cache this
			return new String(data,StandardCharsets.UTF_8);
		}

		@Override
		public Identifier clone(SyntacticItem[] operands) {
			return new Identifier(get());
		}

		@Override
		public String toString() {
			return get();
		}

		public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.ZERO,Data.MANY, "ITEM_ident") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Identifier(data);
			}
		};
	}

	/**
	 * Represents a <i>partial-</i> or <i>fully-qualified</i> name within a
	 * compilation unit.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Name extends AbstractSyntacticItem implements CompilationUnit.Name {
		public Name(Identifier... components) {
			super(ITEM_name, components);
		}

		public Name(Trie path) {
			super(ITEM_name, path2ids(path));
		}

		@Override
		public Identifier get(int i) {
			return (Identifier) super.get(i);
		}

		@Override
		public Identifier[] getAll() {
			return (Identifier[]) super.getAll();
		}

		public Identifier getLast() {
			return get(size()-1);
		}

		public Identifier[] getPath() {
			Identifier[] components = new Identifier[size()-1];
			for(int i=0;i!=components.length;++i) {
				components[i] = get(i);
			}
			return components;
		}

		@Override
		public Name clone(SyntacticItem[] operands) {
			return new Name(ArrayUtils.toArray(Identifier.class, operands));
		}

		@Override
		public String toString() {
			String r = get(0).get();
			for (int i = 1; i != size(); ++i) {
				r += "::" + get(i).get();
			}
			return r;
		}

		private static Identifier[] path2ids(Trie id) {
			Identifier[] ids = new Identifier[id.size()];
			for(int i=0;i!=id.size();++i) {
				ids[i] = new Identifier(id.get(i));
			}
			return ids;
		}

		public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.MANY, Data.ZERO,
				"ITEM_name") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Name(ArrayUtils.toArray(Identifier.class, operands));
			}
		};
	}

	/**
	 * Represents a raw value within a compilation unit. This is not a
	 * source-level item, though could be a component of a source-level item
	 * (e.g. a constant expression).
	 *
	 * @author David J. Pearce
	 *
	 */
	public abstract static class Value extends AbstractSyntacticItem {

		public Value(int opcode, byte... data) {
			super(opcode, data, new SyntacticItem[0]);
		}

		public Value(int opcode, SyntacticItem[] items) {
			super(opcode, items);
		}

		//public abstract Type getType();

		public abstract Object unwrap();

		@Override
		public String toString() {
			return getData().toString();
		}

		public static class Null extends Value {
			public Null() {
				super(ITEM_null);
			}

			@Override
			public Null clone(SyntacticItem[] operands) {
				return new Null();
			}

			@Override
			public Object unwrap() {
				return null;
			}

			@Override
			public String toString() {
				return "null";
			}

			public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.ZERO, Data.ZERO,
					"ITEM_null") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Value.Null();
				}
			};
		}

		public static class Bool extends Value {
			public Bool(boolean value) {
				super(ITEM_bool, (byte) (value ? 1 : 0));
			}

			public boolean get() {
				return (data[0] == 1);
			}

			@Override
			public Boolean unwrap() {
				return get();
			}

			@Override
			public Bool clone(SyntacticItem[] operands) {
				return new Bool(get());
			}

			@Override
			public String toString() {
				return Boolean.toString(get());
			}

			public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.ZERO, Data.ONE,
					"ITEM_bool") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Value.Bool(data[0] == 1);
				}
			};
		}

		public static class Byte extends Value {
			public Byte(byte value) {
				super(ITEM_byte, value);
			}

			public byte get() {
				return data[0];
			}

			@Override
			public java.lang.Byte unwrap() {
				return get();
			}

			@Override
			public Byte clone(SyntacticItem[] operands) {
				return new Byte(get());
			}

			public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.ZERO, Data.ONE,
					"ITEM_byte") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Value.Byte(data[0]);
				}
			};
		}


		public static class Int extends Value {

			public Int(long value) {
				this(BigInteger.valueOf(value));
			}

			public Int(BigInteger value) {
				super(ITEM_int, value.toByteArray());
			}

			public Int(byte[] bytes) {
				super(ITEM_int, bytes);
			}

			public BigInteger get() {
				return new BigInteger(data);
			}

			@Override
			public BigInteger unwrap() {
				return get();
			}

			@Override
			public Int clone(SyntacticItem[] operands) {
				return new Int(get());
			}

			@Override
			public String toString() {
				return get().toString();
			}

			public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.ZERO,Data.MANY, "ITEM_int") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Value.Int(data);
				}
			};
		}

		public static class Decimal extends Value {

			public Decimal(double value) {
				super(ITEM_decimal, toBytes(BigDecimal.valueOf(value)));
			}

			public Decimal(BigDecimal value) {
				super(ITEM_decimal, toBytes(value));
			}

			public Decimal(byte[] bytes) {
				super(ITEM_decimal, bytes);
			}

			public BigDecimal get() {
				return fromBytes(data);
			}

			@Override
			public BigDecimal unwrap() {
				return get();
			}

			@Override
			public Decimal clone(SyntacticItem[] operands) {
				return new Decimal(get());
			}

			@Override
			public String toString() {
				return get().toString();
			}

			private static byte[] toBytes(BigDecimal d) {
				int scale = d.scale();
				byte[] m = d.unscaledValue().toByteArray();
				byte[] bytes = new byte[m.length+4];
				bytes[0] = (byte) ((scale >> 24) & 0xFF);
				bytes[1] = (byte) ((scale >> 16) & 0xFF);
				bytes[2] = (byte) ((scale >> 8) & 0xFF);
				bytes[3] = (byte) (scale & 0xFF);
				System.arraycopy(m, 0, bytes, 4, m.length);
				return bytes;
			}

			private static BigDecimal fromBytes(byte[] data) {
				int scale = (data[0] << 24) | (data[1] << 16) | (data[2] << 8) | data[3];
				BigInteger m = new BigInteger(Arrays.copyOfRange(data, 4, data.length));
				return new BigDecimal(m,scale);
			}

			public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.ZERO, Data.MANY,
					"ITEM_decimal") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Value.Decimal(data);
				}
			};
		}


		public static class UTF8 extends Value {
			public UTF8(String str) {
				super(ITEM_utf8, str.getBytes());
			}

			public UTF8(byte[] bytes) {
				super(ITEM_utf8, bytes);
			}

			public byte[] get() {
				return data;
			}

			@Override
			public String unwrap() {
				return toString();
			}

			@Override
			public UTF8 clone(SyntacticItem[] operands) {
				return new UTF8(get());
			}

			@Override
			public String toString() {
				return new String(get());
			}

			public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.ZERO,Data.MANY, "ITEM_utf8") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Value.UTF8(data);
				}
			};
		}

		public static class Array extends Value {

			public Array(Value... stmts) {
				super(ITEM_array, stmts);
			}

			public Array(Collection<? extends Value> stmts) {
				super(ITEM_array, ArrayUtils.toArray(SyntacticItem.class,stmts));
			}

			@Override
			public Object unwrap() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Value get(int i) {
				return (Value) super.get(i);
			}

			@Override
			public Array clone(SyntacticItem[] operands) {
				return new Array(ArrayUtils.toArray(Value.class, operands));
			}

			@Override
			public String toString() {
				return "[" + toBareString() + "]";
			}

			public String toBareString() {
				String r = "";
				for (int i = 0; i != size(); ++i) {
					if (i != 0) {
						r += ",";
					}
					SyntacticItem child = get(i);
					if (child == null) {
						r += "?";
					} else {
						r += child.toString();
					}
				}
				return r;
			}

			public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.MANY, Data.ZERO,
					"ITEM_array") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Value.Array(ArrayUtils.toArray(Value.class, operands));
				}
			};
		}

		public static class Dictionary extends Value {
			public Dictionary(Pair<Identifier,Value>... entries) {
				super(ITEM_dictionary,entries);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Dictionary(ArrayUtils.toArray(Pair.class, operands));
			}

			@Override
			public Pair<Identifier,Value> get(int i) {
				return (Pair<Identifier,Value>) super.get(i);
			}

			@Override
			public Object unwrap() {
				HashMap<String, Object> map = new HashMap<>();
				for (int i = 0; i != size(); ++i) {
					Pair<Identifier, Value> entry = get(i);
					map.put(entry.get(0).toString(), entry.getSecond().unwrap());
				}
				return map;
			}

			@Override
			public String toString() {
				String r = "";
				for(int i=0;i!=size();++i) {
					Pair<Identifier,Value> entry = get(i);
					if(i != 0) {
						r += ", ";
					}
					r += entry.getFirst() + "=" + entry.getSecond();
				}
				return "{" + r + "}";
			}

			public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.MANY, Data.ZERO,
					"ITEM_dictionary") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Value.Dictionary(ArrayUtils.toArray(Pair.class, operands));
				}
			};
		}
	}

	// ============================================================
	// Attributes
	// ============================================================

	/**
	 * Attributes represent various additional pieces of information inferred
	 * about a given item in the heap.  For example, source line information.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Attribute {

		/**
		 * A span associates a given syntactic item with a contiguous region of
		 * text in the original source file.
		 *
		 * @author David J. Pearce
		 *
		 */
		public class Span extends AbstractSyntacticItem implements Attribute {

			public Span(SyntacticItem item, int start, int end) {
				this(item, new Value.Int(start), new Value.Int(end));
			}

			public Span(SyntacticItem target, Value.Int start, Value.Int end) {
				super(ATTR_span, target, start, end);
			}

			/**
			 * Get the item that this span is associated with.
			 *
			 * @return
			 */
			public SyntacticItem getItem() {
				return get(0);
			}

			/**
			 * Get the integer offset from the start of the stream where this
			 * span begins.
			 *
			 * @return
			 */
			public Value.Int getStart() {
				return (Value.Int) get(1);
			}

			/**
			 * Get the integer offset from the start of the stream where this
			 * span ends.
			 *
			 * @return
			 */
			public Value.Int getEnd() {
				return (Value.Int) get(2);
			}

			@Override
			public Span clone(SyntacticItem[] operands) {
				return new Span(operands[0], (Value.Int) operands[1], (Value.Int) operands[2]);
			}

			public static final SyntacticItem.Descriptor DESCRIPTOR_0 = new SyntacticItem.Descriptor(Operands.THREE, Data.ZERO,
					"ATTR_span") {
				@Override
				public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
					return new Attribute.Span(operands[0], (Value.Int) operands[1], (Value.Int) operands[2]);
				}
			};
		}
	}
}
