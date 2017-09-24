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
package wyil.interpreter;

import java.math.BigInteger;

import wybs.lang.NameResolver.ResolutionError;
import wybs.util.AbstractCompilationUnit.Identifier;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Stmt;
import wyc.lang.WhileyFile.Type;

/**
 * Provides a generic notion of data types for use within the interpreter. Each
 * data type provides the corresponding operations which can be performed on it.
 * However, the exact manner in which the data type is implemented is left
 * unspecified. This leaves open the possibility of different possible
 * implementations. For example, we can have different notions of a concrete
 * semantics which represent the underlying data in different ways (e.g.
 * high-level versus low-level). Likewise we could have a symbolic notion of
 * semantics which is useful, for example, for performing model checking or
 * something other form of static analysis.
 *
 * @author David J. Pearce
 *
 */
public interface AbstractSemantics {

	/**
	 * Create a new <code>null</code> value.
	 *
	 * @return
	 */
	public RValue.Null Null();

	/**
	 * Create a new <code>bool</code> value.
	 *
	 * @return
	 */
	public RValue.Bool Bool(boolean value);

	/**
	 * Create a new <code>byte</code> value.
	 *
	 * @return
	 */
	public RValue.Byte Byte(byte value);

	/**
	 * Create a new <code>int</code> value.
	 *
	 * @return
	 */
	public RValue.Int Int(BigInteger value);

	/**
	 * Create a new cell value.
	 *
	 * @return
	 */
	public RValue.Cell Cell(RValue value);

	/**
	 * Create a new reference value.
	 *
	 * @return
	 */
	public RValue.Reference Reference(RValue.Cell value);

	/**
	 * Create a new array value.
	 *
	 * @return
	 */
	public RValue.Array Array(RValue... elements);

	/**
	 * Create a new field value.
	 *
	 * @return
	 */
	public RValue.Field Field(Identifier name, RValue value);

	/**
	 * Create a new record value.
	 *
	 * @return
	 */
	public RValue.Record Record(RValue.Field... fields);

	/**
	 * Create a new lambda value.
	 *
	 * @return
	 */
	public RValue.Lambda Lambda(Decl.Callable context, Interpreter.CallStack frame, Stmt body);

	/**
	 * Represents a value which may occur in a "read" position within a
	 * statement or expression. In essence, the only non-read positions are
	 * those on the immediate left-hand side of an assignment.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface RValue {
		/**
		 * Check whether this value is an instance of of a given type. For
		 * example, the value "1" is an instanceof the type "int".
		 *
		 * @param type
		 * @return
		 */
		public Bool is(Type type, Interpreter instance) throws ResolutionError;

		/**
		 * Convert this value into a given representation. In the case that this
		 * is impossible, then the result is undefined. For example, turning an
		 * integer into a record is undefined.
		 *
		 * @param type
		 * @return
		 */
		public RValue convert(Type type);

		/**
		 * Determine whether two values are equal.
		 *
		 * @param rhs
		 * @return
		 */
		public Bool equal(RValue rhs);

		/**
		 * Determine whether two values are not equal
		 *
		 * @param rhs
		 * @return
		 */
		public Bool notEqual(RValue rhs);

		/**
		 * Represents the unit value null. This value supports no operations
		 * other than equality and type testing.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Null {

		}

		/**
		 * Represents the set of boolean values which contains only "true" and
		 * "false". This supports the classical operators on logical values.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Bool {

			/**
			 * Return the logical negation of this value.
			 *
			 * @return
			 */
			public Bool not();

			/**
			 * Return the conjunction of this value and another.
			 *
			 * @param rhs
			 * @return
			 */
			public Bool and(Bool rhs);

			/**
			 * Return the disjunction of this value and another.
			 *
			 * @param rhs
			 * @return
			 */
			public Bool or(Bool rhs);
		}

		/**
		 * Represents an abstract byte value which supports the usual array of
		 * bitwise operations.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Byte extends RValue {

			/**
			 * Return the bitwise inversion of this value. For example,
			 * <code>101</code> is <code>010</code> inverted.
			 *
			 * @return
			 */
			public Byte invert();

			/**
			 * Return the bitwise and of each corresponding bit in the left and
			 * right-hand sides. For example, <code>101</code> and
			 * <code>001</code> gives <code>001</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Byte and(Byte rhs);

			/**
			 * Return the bitwise or of each corresponding bit in the left and
			 * right-hand sides. For example, <code>101</code> and
			 * <code>001</code> gives <code>101</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Byte or(Byte rhs);

			/**
			 * Return the bitwise exclusive-or of each corresponding bit in the
			 * left and right-hand sides. For example, <code>101</code> and
			 * <code>001</code> gives <code>100</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Byte xor(Byte rhs);

			/**
			 * Return the result of shifting this byte to the left by a given
			 * number of positions. For example, <code>101</code> and
			 * <code>2</code> gives <code>10100</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Byte shl(Int rhs);

			/**
			 * Return the result of shifting this byte to the right by a given
			 * number of positions. For example, <code>1101</code> and
			 * <code>2</code> gives <code>11</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Byte shr(Int rhs);
		}

		/**
		 * Represents an unbound (signed) integer value.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Int {

			/**
			 * Return the negation of this value. For example, <code>3</code>
			 * becomes <code>-3</code>.
			 *
			 * @return
			 */
			public Int negate();

			/**
			 * Return the sum of this value and a given value. For example,
			 * <code>1</code> and <code>2</code> give <code>3</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Int add(Int rhs);

			/**
			 * Return the subtraction of a given value from this value. For
			 * example, <code>1</code> and <code>2</code> give <code>-1</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Int subtract(Int rhs);

			/**
			 * Return the product of this value and a given value. For example,
			 * <code>2</code> and <code>3</code> give <code>6</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Int multiply(Int rhs);

			/**
			 * Return the integer division of this value and a given value. For
			 * example, <code>6</code> and <code>3</code> gives <code>2</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Int divide(Int rhs);

			/**
			 * Return the remainder from an integer division of this value and a
			 * given value. For example, <code>5</code> and <code>3</code> gives
			 * <code>2</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Int remainder(Int rhs);

			/**
			 * Determine whether this value is below a given value. For example,
			 * <code>5</code> and <code>3</code> gives <code>false</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Bool lessThan(Int rhs);

			/**
			 * Determine whether this value is below or equal a given value. For
			 * example, <code>5</code> and <code>3</code> gives
			 * <code>false</code>.
			 *
			 * @param rhs
			 * @return
			 */
			public Bool lessThanOrEqual(Int rhs);

			public int intValue();
		}

		/**
		 * Represents a contiguous sequence of zero or more values.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Array extends RValue {

			/**
			 * Read the value at a given position in this array.
			 *
			 * @param index
			 * @return
			 */
			public RValue read(Int index);

			/**
			 * Write a value to the given position in this array, returning the
			 * updated array.
			 *
			 * @param index
			 * @param value
			 * @return
			 */
			public Array write(Int index, RValue value);

			/**
			 * Return the length of this array.
			 *
			 * @return
			 */
			public Int length();

		}

		/**
		 * Represents a given field in a record.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Field {
			/**
			 * Return the name of this field.
			 *
			 * @return
			 */
			public Identifier getName();

			/**
			 * Return the value associated with this field.
			 *
			 * @return
			 */
			public RValue getValue();
		}

		/**
		 * Represents a record value.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Record extends RValue {

			/**
			 * Return the number of fields in this record.
			 *
			 * @return
			 */
			public int size();

			/**
			 * Determine whether or not this record has a given field.
			 *
			 * @param field
			 * @return
			 */
			public boolean hasField(Identifier field);

			/**
			 * Read the value associated with a given field in this record.
			 *
			 * @param field
			 * @return
			 */
			public RValue read(Identifier field);

			/**
			 * Write a value to a given field in this record, producing an
			 * updated copy of this record.
			 *
			 * @param field
			 * @param value
			 * @return
			 */
			public Record write(Identifier field, RValue value);
		}

		public interface Lambda extends RValue {

		}

		/**
		 * Represents a reference to a given cell object.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Reference extends RValue {

			/**
			 * Return the cell to which this reference points.
			 *
			 * @return
			 */
			public Cell deref();

		}

		/**
		 * Represents a heap-allocated object.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Cell extends RValue {

			/**
			 * Return the value associated with this cell
			 *
			 * @return
			 */
			public RValue read();

			/**
			 * Write a value to this cell. This does not returned an updated
			 * version of the cell but, rather, performs an in-place update.
			 *
			 * @param value
			 */
			public void write(RValue value);
		}

		public interface Undefined extends Null, Bool, Byte, Int, Array, Record, Lambda, Reference, Cell {

		}
	}
}