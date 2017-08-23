package wyil.util.interpreter;

import java.math.BigInteger;
import java.util.Arrays;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.util.AbstractWhileyFile.Declaration;
import wyc.util.AbstractWhileyFile.Type;
import wycc.util.ArrayUtils;

public abstract class ConcreteSemantics {
	public static final Null Null = new Null();
	public static final Bool True = new Bool(true);
	public static final Bool False = new Bool(false);

	public static Bool Bool(boolean value) {
		return value ? True : False;
	}

	public static Byte Byte(byte value) {
		return new Byte(value);
	}

	public static Int Int(BigInteger value) {
		return new Int(value);
	}

	public static Cell Cell(Value value) {
		return new Cell(value);
	}

	public static Reference Reference(Cell value) {
		return new Reference(value);
	}

	public static Array Array(AbstractSemantics.Value... elements) {
		return new Array(ArrayUtils.toArray(Value.class, elements));
	}

	private abstract static class Value implements AbstractSemantics.Value {
		/**
		 * Check whether a given value is an instanceof of a given type.
		 *
		 * @param type
		 * @return
		 */
		@Override
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
				for (int i=0;i!=t.size();++i) {
					Type element = t.getOperand(i);
					if (this.is(element) == True) {
						return True;
					}
				}
			} else if (type instanceof Type.Intersection) {
				Type.Intersection t = (Type.Intersection) type;
				for (int i=0;i!=t.size();++i) {
					Type element = t.getOperand(i);
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

		@Override
		public Value convert(Type type) {
			throw new RuntimeException("implement me");
		}

		/**
		 * Determine whether two values are equal
		 *
		 * @param rhs
		 * @return
		 */
		@Override
		public Bool equal(AbstractSemantics.Value rhs) {
			return Bool(this.equals(rhs));
		}

		/**
		 * Determine whether two values are not equal
		 *
		 * @param rhs
		 * @return
		 */
		@Override
		public Bool notEqual(AbstractSemantics.Value rhs) {
			return Bool(!this.equals(rhs));
		}
	}

	public final static class Null extends Value implements AbstractSemantics.Null {
		private Null() {
		}

		@Override
		public Bool is(Type type) {
			if (type instanceof Type.Null) {
				return True;
			} else {
				return super.is(type);
			}
		}

		@Override
		public Value convert(Type type) {
			if (type instanceof Type.Null) {
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

	public final static class Bool extends Value implements AbstractSemantics.Bool {
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
		public Value convert(Type type) {
			if(type instanceof Type.Bool) {
				return this;
			} else {
				return super.convert(type);
			}
		}
		@Override
		public Bool not() {
			return this == True ? False : True;
		}
		@Override
		public Bool and(AbstractSemantics.Bool _rhs) {
			Bool rhs = (Bool) _rhs;
			return Bool(value && rhs.value);
		}
		@Override
		public Bool or(AbstractSemantics.Bool _rhs) {
			Bool rhs = (Bool) _rhs;
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

	public final static class Int extends Value implements AbstractSemantics.Int {
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
		public Value convert(Type type) {
			if(type instanceof Type.Int) {
				return this;
			} else {
				return super.convert(type);
			}
		}
		@Override
		public Int negate() {
			return Int(value.negate());
		}

		@Override
		public Int add(AbstractSemantics.Int _rhs) {
			Int rhs = (Int) _rhs;
			return Int(value.add(rhs.value));
		}

		@Override
		public Int subtract(AbstractSemantics.Int _rhs) {
			Int rhs = (Int) _rhs;
			return Int(value.subtract(rhs.value));
		}

		@Override
		public Int multiply(AbstractSemantics.Int _rhs) {
			Int rhs = (Int) _rhs;
			return Int(value.multiply(rhs.value));
		}

		@Override
		public Int divide(AbstractSemantics.Int _rhs) {
			Int rhs = (Int) _rhs;
			return Int(value.divide(rhs.value));
		}

		@Override
		public Int remainder(AbstractSemantics.Int _rhs) {
			Int rhs = (Int) _rhs;
			return Int(value.remainder(rhs.value));
		}

		@Override
		public Bool lessThan(AbstractSemantics.Int _rhs) {
			Int rhs = (Int) _rhs;
			return Bool(value.compareTo(rhs.value) < 0);
		}

		@Override
		public Bool lessThanOrEqual(AbstractSemantics.Int _rhs) {
			Int rhs = (Int) _rhs;
			return Bool(value.compareTo(rhs.value) <= 0);
		}

		@Override
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

	public final static class Array extends Value implements AbstractSemantics.Array {
		private final Value[] elements;

		private Array(Value... elements) {
			this.elements = elements;
		}

		@Override
		public Bool is(Type type) {
			if (type instanceof Type.Array) {
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
		public Value convert(Type type) {
			if (type instanceof Type.Array) {
				Type.Array t = (Type.Array) type;
				Value[] values = new Value[elements.length];
				for (int i = 0; i != values.length; ++i) {
					values[i] = elements[i].convert(t.getElement());
				}
				return new Array(values);
			} else {
				return super.convert(type);
			}
		}

		@Override
		public Value read(AbstractSemantics.Int _index) {
			Int index = (Int) _index;
			int idx = index.value.intValue();
			return elements[idx];
		}

		@Override
		public Array write(AbstractSemantics.Int _index, AbstractSemantics.Value _value) {
			Int index = (Int) _index;
			Value value = (Value) _value;
			int idx = index.value.intValue();
			Value[] values = Arrays.copyOf(this.elements, this.elements.length);
			values[idx] = value;
			return new Array(values);
		}

		public Value[] getElements() {
			return elements;
		}

		@Override
		public Int length() {
			return new Int(BigInteger.valueOf(elements.length));
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof Array) && (Arrays.equals(elements, ((Array) o).elements));
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

	private final static class Field implements AbstractSemantics.Field {
		private final Identifier name;
		private final Value value;

		private Field(Identifier name, Value value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public Identifier getName() {
			return name;
		}

		@Override
		public Value getValue() {
			return value;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Field) {
				Field f = (Field) o;
				return name.equals(f.name) && value.equals(f.value);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return name.get().hashCode() ^ value.hashCode();
		}
	}

	public final static class Record extends Value implements AbstractSemantics.Record {
		private final Field[] fields;

		private Record(Field... fields) {
			this.fields = fields;
		}

		@Override
		public int size() {
			return fields.length;
		}

		@Override
		public boolean hasField(Identifier field) {
			for (int i = 0; i != fields.length; ++i) {
				Field f = fields[i];
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
						Value val = read(f.getName());
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
		public Value convert(Type type) {
			if (type instanceof Type.Record) {
				Type.Record t = (Type.Record) type;
				Tuple<Declaration.Variable> fields = t.getFields();
				Record rec = this;
				for (int i = 0; i != fields.size(); ++i) {
					Declaration.Variable f = fields.getOperand(i);
					Value v = this.read(f.getName()).convert(f.getType());
					rec = rec.write(f.getName(), v);
				}
				return rec;
			} else {
				return super.convert(type);
			}
		}
		@Override
		public Value read(Identifier field) {
			for (int i = 0; i != fields.length; ++i) {
				Field f = fields[i];
				if (f.name.equals(field)) {
					return f.value;
				}
			}
			throw new RuntimeException("Invalid record access");
		}

		@Override
		public Record write(Identifier field, AbstractSemantics.Value _value) {
			Value value = (Value) _value;
			Field[] fields = Arrays.copyOf(this.fields, this.fields.length);
			for (int i = 0; i != fields.length; ++i) {
				Field f = fields[i];
				if (f.name.equals(field)) {
					fields[i] = new Field(f.name, value);
					return new Record(fields);
				}
			}
			throw new RuntimeException("Invalid record access");
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof Record) && Arrays.equals(fields, ((Record) o).fields);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(fields);
		}
	}

	public final static class Reference extends Value {
		private final Cell referent;

		private Reference(Cell referent) {
			this.referent = referent;
		}

		public Cell deref() {
			return referent;
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof Reference) && (referent == ((Reference) o).referent);
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(referent);
		}
	}

	public final static class Cell extends Value {
		private Value value;

		private Cell(Value value) {
			this.value = value;
		}

		public Value read() {
			return value;
		}

		public void write(Value value) {
			this.value = value;
		}
	}
}