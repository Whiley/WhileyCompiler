package wyil.util.interpreter;

import java.math.BigInteger;
import java.util.Arrays;
import static wyil.lang.WyilFile.*;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.util.AbstractWhileyFile.Declaration;

public abstract class RValue {
	public static final RValue.Null Null = new Null();
	public static final RValue.Bool True = new Bool(true);
	public static final RValue.Bool False = new Bool(false);

	public static Bool Bool(boolean value) {
		return value ? True : False;
	}

	public static Byte Byte(byte value) {
		return new Byte(value);
	}

	public static Int Int(BigInteger value) {
		return new Int(value);
	}

	public static Cell Cell(RValue value) {
		return new Cell(value);
	}

	public static Reference Reference(Cell value) {
		return new Reference(value);
	}

	public static Array Array(RValue... elements) {
		return new Array(elements);
	}

	/**
	 * Check whether a given value is an instanceof of a given type.
	 *
	 * @param type
	 * @return
	 */
	public Bool is(Type type) {
		// Handle generic cases here
		if (type instanceof Type.Any) {
			return True;
		} else if (type instanceof Type.Void) {
			return False;
		} else if (type instanceof Type.Nominal) {
			// FIXME: need to implement this!!
			throw new RuntimeException("Implement me");
		} else if (type instanceof Type.Union) {
			Type.Union t = (Type.Union) type;
			for (Type element : t.getOperands()) {
				if (this.is(element) == True) {
					return True;
				}
			}
		} else if (type instanceof Type.Intersection) {
			Type.Intersection t = (Type.Intersection) type;
			for (Type element : t.getOperands()) {
				if (this.is(element) == False) {
					return False;
				}
			}
			return True;
		} else if (type instanceof Type.Negation) {
			Type.Negation t = (Type.Negation) type;
			return this.is(t.getElement()).not();
		}
		// Default case.
		return False;
	}

	public RValue convert(Type type) {
		throw new RuntimeException("implement me");
	}

	/**
	 * Determine whether two values are equal
	 *
	 * @param rhs
	 * @return
	 */
	public Bool equal(RValue rhs) {
		return Bool(this.equals(rhs));
	}

	/**
	 * Determine whether two values are not equal
	 *
	 * @param rhs
	 * @return
	 */
	public Bool notEqual(RValue rhs) {
		return Bool(!this.equals(rhs));
	}

	public final static class Null extends RValue {
		private Null() {
		}
		@Override
		public Bool is(Type type) {
			if(type instanceof Type.Null) {
				return True;
			} else {
				return super.is(type);
			}
		}
		@Override
		public RValue convert(Type type) {
			if(type instanceof Type.Null) {
				return this;
			} else {
				return super.convert(type);
			}
		}
		@Override
		public boolean equals(Object o) {
			return this == o;
		}
		@Override
		public int hashCode() {
			return 0;
		}
		@Override
		public String toString() {
			return null;
		}
	}

	public final static class Bool extends RValue {
		private final boolean value;

		private Bool(boolean value) {
			this.value = value;
		}

		public boolean boolValue() {
			return value;
		}
		@Override
		public Bool is(Type type) {
			if(type instanceof Type.Bool) {
				return True;
			} else {
				return super.is(type);
			}
		}
		@Override
		public RValue convert(Type type) {
			if(type instanceof Type.Bool) {
				return this;
			} else {
				return super.convert(type);
			}
		}
		public Bool not() {
			return this == True ? False : True;
		}
		public Bool and(Bool rhs) {
			return Bool(value && rhs.value);
		}
		public Bool or(Bool rhs) {
			return Bool(value || rhs.value);
		}
		@Override
		public boolean equals(Object o) {
			return this == o;
		}
		@Override
		public int hashCode() {
			return Boolean.hashCode(value);
		}
		@Override
		public String toString() {
			return Boolean.toString(value);
		}
	}

	public final static class Byte extends RValue {
		private final byte value;

		private Byte(byte value) {
			this.value = value;
		}
		@Override
		public Bool is(Type type) {
			if(type instanceof Type.Byte) {
				return True;
			} else {
				return super.is(type);
			}
		}
		@Override
		public RValue convert(Type type) {
			if(type instanceof Type.Byte) {
				return this;
			} else {
				return super.convert(type);
			}
		}
		public Byte invert() {
			return Byte((byte) ~value);
		}

		public Byte and(Byte rhs) {
			return Byte((byte) (value & rhs.value));
		}

		public Byte or(Byte rhs) {
			return Byte((byte) (value | rhs.value));
		}

		public Byte xor(Byte rhs) {
			return Byte((byte) (value ^ rhs.value));
		}

		public Byte shl(Int rhs) {
			return Byte((byte) (value << rhs.intValue()));
		}

		public Byte shr(Int rhs) {
			return Byte((byte) (value >> rhs.intValue()));
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof RValue.Byte) && (value == ((RValue.Byte) o).value);
		}

		@Override
		public int hashCode() {
			return value;
		}

		@Override
		public String toString() {
			return Integer.toBinaryString(value);
		}
	}

	public final static class Int extends RValue {
		private final BigInteger value;

		private Int(BigInteger value) {
			this.value = value;
		}
		@Override
		public Bool is(Type type) {
			if(type instanceof Type.Int) {
				return True;
			} else {
				return super.is(type);
			}
		}
		@Override
		public RValue convert(Type type) {
			if(type instanceof Type.Int) {
				return this;
			} else {
				return super.convert(type);
			}
		}
		public Int negate() {
			return Int(value.negate());
		}

		public Int add(Int rhs) {
			return Int(value.add(rhs.value));
		}

		public Int subtract(Int rhs) {
			return Int(value.subtract(rhs.value));
		}

		public Int multiply(Int rhs) {
			return Int(value.multiply(rhs.value));
		}

		public Int divide(Int rhs) {
			return Int(value.divide(rhs.value));
		}

		public Int remainder(Int rhs) {
			return Int(value.remainder(rhs.value));
		}

		public Bool lessThan(Int rhs) {
			return Bool(value.compareTo(rhs.value) < 0);
		}

		public Bool lessThanOrEqual(Int rhs) {
			return Bool(value.compareTo(rhs.value) <= 0);
		}

		public int intValue() {
			return value.intValue();
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof Int) && value.equals(((Int)o).value);
		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	public final static class Array extends RValue {
		private final RValue[] elements;

		private Array(RValue... elements) {
			this.elements = elements;
		}
		@Override
		public Bool is(Type type) {
			if(type instanceof Type.Array) {
				Type.Array t = (Type.Array) type;
				for (int i = 0; i != elements.length; ++i) {
					if (elements[i].is(t.getElement()) == False) {
						return False;
					}
				}
				return True;
			} else {
				return super.is(type);
			}
		}

		@Override
		public RValue convert(Type type) {
			if (type instanceof Type.Array) {
				Type.Array t = (Type.Array) type;
				RValue[] values = new RValue[elements.length];
				for (int i = 0; i != values.length; ++i) {
					values[i] = elements[i].convert(t.getElement());
				}
				return RValue.Array(values);
			} else {
				return super.convert(type);
			}
		}
		public RValue read(RValue.Int index) {
			int idx = index.value.intValue();
			return elements[idx];
		}

		public Array write(RValue.Int index, RValue value) {
			int idx = index.value.intValue();
			RValue[] values = Arrays.copyOf(this.elements, this.elements.length);
			values[idx] = value;
			return new Array(values);
		}

		public RValue[] getElements() {
			return elements;
		}

		public RValue.Int length() {
			return new RValue.Int(BigInteger.valueOf(elements.length));
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof RValue.Array) && (Arrays.equals(elements, ((RValue.Array) o).elements));
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(elements);
		}

		@Override
		public String toString() {
			return Arrays.toString(elements);
		}
	}

	private final static class Field {
		public final Identifier name;
		public final RValue value;

		private Field(Identifier name, RValue value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof RValue.Field) {
				RValue.Field f = (RValue.Field) o;
				return name.equals(f.name) && value.equals(f.value);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return name.get().hashCode() ^ value.hashCode();
		}
	}

	public final static class Record extends RValue {
		private final RValue.Field[] fields;

		private Record(RValue.Field... fields) {
			this.fields = fields;
		}

		public int size() {
			return fields.length;
		}

		public boolean hasField(Identifier field) {
			for (int i = 0; i != fields.length; ++i) {
				RValue.Field f = fields[i];
				if (f.name.equals(field)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public Bool is(Type type) {
			if (type instanceof Type.Record) {
				Type.Record t = (Type.Record) type;
				Tuple<Declaration.Variable> tFields = t.getFields();
				for (int i = 0; i != tFields.size(); ++i) {
					Declaration.Variable f = tFields.getOperand(i);
					if (hasField(f.getName())) {
						RValue val = read(f.getName());
						// Matching field
						if (val.is(f.getSignature()) == False) {
							// Field not member of type
							return False;
						}
					} else {
						// No matching field
						return False;
					}
				}
				return Bool(t.isOpen() || fields.length == tFields.size());
			} else {
				return super.is(type);
			}
		}

		@Override
		public RValue convert(Type type) {
			if (type instanceof Type.Record) {
				Type.Record t = (Type.Record) type;
				Tuple<Declaration.Variable> fields = t.getFields();
				RValue.Record rec = this;
				for (int i = 0; i != fields.size(); ++i) {
					Declaration.Variable f = fields.getOperand(i);
					RValue v = this.read(f.getName()).convert(f.getType());
					rec = rec.write(f.getName(), v);
				}
				return rec;
			} else {
				return super.convert(type);
			}
		}
		public RValue read(Identifier field) {
			for (int i = 0; i != fields.length; ++i) {
				RValue.Field f = fields[i];
				if (f.name.equals(field)) {
					return f.value;
				}
			}
			throw new RuntimeException("Invalid record access");
		}

		public Record write(Identifier field, RValue value) {
			RValue.Field[] fields = Arrays.copyOf(this.fields, this.fields.length);
			for (int i = 0; i != fields.length; ++i) {
				RValue.Field f = fields[i];
				if (f.name.equals(field)) {
					fields[i] = new RValue.Field(f.name, value);
					return new Record(fields);
				}
			}
			throw new RuntimeException("Invalid record access");
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof RValue.Record) && Arrays.equals(fields, ((RValue.Record) o).fields);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(fields);
		}
	}

	public final static class Lambda extends RValue {

	}

	public final static class Reference extends RValue {
		private final RValue.Cell referent;

		private Reference(RValue.Cell referent) {
			this.referent = referent;
		}

		public RValue.Cell deref() {
			return referent;
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof RValue.Reference) && (referent == ((RValue.Reference) o).referent);
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(referent);
		}
	}

	public final static class Cell extends RValue {
		private RValue value;

		private Cell(RValue value) {
			this.value = value;
		}

		public RValue read() {
			return value;
		}

		public void write(RValue value) {
			this.value = value;
		}
	}
}