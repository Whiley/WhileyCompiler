package wyil.util.interpreter;

import static wyil.lang.WyilFile.*;
import wybs.util.AbstractCompilationUnit.Identifier;

public class AbstractSemantics {

	public interface Value {
		/**
		 * Check whether this value is an instance of of a given type. For example,
		 * the value "1" is an instanceof the type "int".
		 *
		 * @param type
		 * @return
		 */
		public Bool is(Type type);

		/**
		 * Convert this value into a given representation. In the case that this is
		 * impossible, then the result is undefined. For example, turning an integer
		 * into a record is undefined.
		 *
		 * @param type
		 * @return
		 */
		public Value convert(Type type);

		/**
		 * Determine whether two values are equal.
		 *
		 * @param rhs
		 * @return
		 */
		public Bool equal(Value rhs);

		/**
		 * Determine whether two values are not equal
		 *
		 * @param rhs
		 * @return
		 */
		public Bool notEqual(Value rhs);
	}

	/**
	 * Represents the unit value null. This value supports no operations other
	 * than equality and type testing.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Null {

	}

	/**
	 * Represents the set of boolean values which contains only "true" and
	 * "false".  This supports the classical operators on logical values.
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

	public interface Byte extends Value {

		public Byte invert();

		public Byte and(Byte rhs);

		public Byte or(Byte rhs);

		public Byte xor(Byte rhs);

		public Byte shl(Int rhs);

		public Byte shr(Int rhs);
	}

	public interface Int {

		public Int negate();

		public Int add(Int rhs);

		public Int subtract(Int rhs);

		public Int multiply(Int rhs);

		public Int divide(Int rhs);

		public Int remainder(Int rhs);

		public Bool lessThan(Int rhs);

		public Bool lessThanOrEqual(Int rhs);

		public int intValue();
	}

	public interface Array extends Value {

		public Value read(Int index);

		public Array write(Int index, Value value);

		public Int length();

	}

	public interface Field {
		public Identifier getName();

		public Value getValue();
	}

	public interface Record extends Value {

		public int size();

		public boolean hasField(Identifier field);

		public Value read(Identifier field);

		public Record write(Identifier field, Value value);
	}

	public interface Lambda extends Value {

	}

	public interface Reference extends Value {

		public Cell deref();

	}

	public interface Cell extends Value {

		public Value read();

		public void write(Value value);
	}

	public interface Undefined extends Null, Bool, Byte, Int, Array, Record, Lambda, Reference, Cell {

	}
}