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
import java.util.Arrays;
import java.util.Comparator;

import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.interpreter.Interpreter.CallStack;

import static wyc.lang.WhileyFile.*;

public class ConcreteSemantics implements AbstractSemantics {

	@Override
	public RValue.Null Null() {
		return RValue.Null;
	}

	@Override
	public RValue.Bool Bool(boolean value) {
		return value ? RValue.True : RValue.False;
	}

	@Override
	public RValue.Byte Byte(byte value) {
		return new RValue.Byte(value);
	}

	@Override
	public RValue.Int Int(BigInteger value) {
		return new RValue.Int(value);
	}

	@Override
	public RValue.Cell Cell(AbstractSemantics.RValue value) {
		return new RValue.Cell((RValue) value);
	}

	@Override
	public RValue.Reference Reference(AbstractSemantics.RValue.Cell value) {
		RValue.Cell cell = (RValue.Cell) value;
		return new RValue.Reference(cell);
	}

	@Override
	public RValue.Array Array(AbstractSemantics.RValue... elements) {
		return new RValue.Array((RValue[]) elements);
	}

	@Override
	public RValue.Field Field(Identifier name, AbstractSemantics.RValue value) {
		return new RValue.Field(name,(RValue) value);
	}

	@Override
	public RValue.Record Record(AbstractSemantics.RValue.Field... fields) {
		return new RValue.Record((RValue.Field[]) fields);
	}

	@Override
	public RValue.Lambda Lambda(Decl.Callable context, Interpreter.CallStack frame, Stmt body) {
		return new RValue.Lambda(context, frame, body);
	}

	public static abstract class RValue implements AbstractSemantics.RValue {
		public static final RValue.Null Null = new RValue.Null();
		public static final RValue.Bool True = new RValue.Bool(true);
		public static final RValue.Bool False = new RValue.Bool(false);

		/**
		 * Check whether a given value is an instanceof of a given type.
		 *
		 * @param type
		 * @return
		 * @throws ResolutionError
		 */
		@Override
		public Bool is(Type type, Interpreter instance) throws ResolutionError {
			// Handle generic cases here
			if (type instanceof Type.Void) {
				return False;
			} else if (type instanceof Type.Nominal) {
				Type.Nominal nom = (Type.Nominal) type;
				NameResolver resolver = instance.getNameResolver();
				Decl.Type decl = resolver.resolveExactly(nom.getName(), Decl.Type.class);
				Decl.Variable var = decl.getVariableDeclaration();
				if(is(var.getType(), instance) == True) {
					Tuple<Expr> invariant = decl.getInvariant();
					return checkInvariant(var,invariant,instance);
				}
				return False;
			} else if (type instanceof Type.Union) {
				Type.Union t = (Type.Union) type;
				for (int i=0;i!=t.size();++i) {
					Type element = t.get(i);
					if (this.is(element, instance) == True) {
						return True;
					}
				}
			}
			// Default case.
			return False;
		}

		@Override
		public RValue convert(Type type) {
			// At the moment, this appears to be sound because there are no actual
			// coercion which need to take place.
			return this;
		}

		/**
		 * Determine whether two values are equal
		 *
		 * @param rhs
		 * @return
		 */
		@Override
		public Bool equal(AbstractSemantics.RValue rhs) {
			return this.equals(rhs) ? True : False;
		}

		/**
		 * Determine whether two values are not equal
		 *
		 * @param rhs
		 * @return
		 */
		@Override
		public Bool notEqual(AbstractSemantics.RValue rhs) {
			return this.equals(rhs) ? False : True;
		}

		/**
		 * Check whether the invariant for a given nominal type holds for this value
		 * or not. This requires physically evaluating the invariant to see whether
		 * or not it holds true.
		 *
		 * @param var
		 * @param invariant
		 * @param instance
		 * @return
		 */
		public Bool checkInvariant(Decl.Variable var, Tuple<Expr> invariant, Interpreter instance) {
			if (invariant.size() > 0) {
				// One or more type invariants to check. Therefore, we need
				// to execute the invariant and determine whether or not it
				// returns true.
				Interpreter.CallStack frame = instance.new CallStack();
				frame.putLocal(var.getName(), this);
				for (int i = 0; i != invariant.size(); ++i) {
					RValue.Bool b = instance.executeExpression(Bool.class, invariant.get(i), frame);
					if (b == False) {
						return b;
					}
				}
			}
			// success
			return True;
		}


		public final static class Null extends RValue implements AbstractSemantics.RValue.Null {
			private Null() {
			}
			@Override
			public Bool is(Type type, Interpreter instance) throws ResolutionError {
				if(type instanceof Type.Null) {
					return True;
				} else {
					return super.is(type, instance);
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

		public final static class Bool extends RValue implements AbstractSemantics.RValue.Bool {
			private final boolean value;

			private Bool(boolean value) {
				this.value = value;
			}

			public boolean boolValue() {
				return value;
			}
			@Override
			public Bool is(Type type, Interpreter instance) throws ResolutionError {
				if(type instanceof Type.Bool) {
					return True;
				} else {
					return super.is(type, instance);
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
			@Override
			public Bool not() {
				return this == True ? False : True;
			}
			@Override
			public Bool and(AbstractSemantics.RValue.Bool _rhs) {
				Bool rhs = (Bool) _rhs;
				return (value && rhs.value) ? True : False;
			}
			@Override
			public Bool or(AbstractSemantics.RValue.Bool _rhs) {
				Bool rhs = (Bool) _rhs;
				return (value || rhs.value) ? True : False;
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

		public final static class Byte extends RValue implements AbstractSemantics.RValue.Byte {
			private final byte value;

			private Byte(byte value) {
				this.value = value;
			}

			@Override
			public RValue.Bool is(Type type, Interpreter instance) throws ResolutionError {
				if (type instanceof Type.Byte) {
					return True;
				} else {
					return super.is(type, instance);
				}
			}

			@Override
			public RValue convert(Type type) {
				if (type instanceof Type.Byte) {
					return this;
				} else {
					return super.convert(type);
				}
			}

			@Override
			public RValue.Byte invert() {
				return new RValue.Byte((byte) ~value);
			}

			@Override
			public RValue.Byte and(AbstractSemantics.RValue.Byte _rhs) {
				RValue.Byte rhs = (RValue.Byte) _rhs;
				return new RValue.Byte((byte) (value & rhs.value));
			}

			@Override
			public RValue.Byte or(AbstractSemantics.RValue.Byte _rhs) {
				RValue.Byte rhs = (RValue.Byte) _rhs;
				return new RValue.Byte((byte) (value | rhs.value));
			}

			@Override
			public RValue.Byte xor(AbstractSemantics.RValue.Byte _rhs) {
				RValue.Byte rhs = (RValue.Byte) _rhs;
				return new RValue.Byte((byte) (value ^ rhs.value));
			}

			@Override
			public RValue.Byte shl(AbstractSemantics.RValue.Int _rhs) {
				RValue.Int rhs = (RValue.Int) _rhs;
				return new RValue.Byte((byte) (value << rhs.intValue()));
			}

			@Override
			public RValue.Byte shr(AbstractSemantics.RValue.Int _rhs) {
				RValue.Int rhs = (RValue.Int) _rhs;
				return new RValue.Byte((byte) (value >> rhs.intValue()));
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

		public final static class Int extends RValue implements AbstractSemantics.RValue.Int {
			private final BigInteger value;

			private Int(BigInteger value) {
				this.value = value;
			}
			@Override
			public Bool is(Type type, Interpreter instance) throws ResolutionError {
				if(type instanceof Type.Int) {
					return True;
				} else {
					return super.is(type, instance);
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

			@Override
			public Int negate() {
				return new Int(value.negate());
			}

			@Override
			public Int add(AbstractSemantics.RValue.Int _rhs) {
				RValue.Int rhs = (RValue.Int) _rhs;
				return new Int(value.add(rhs.value));
			}

			@Override
			public Int subtract(AbstractSemantics.RValue.Int _rhs)  {
				RValue.Int rhs = (RValue.Int) _rhs;
				return new Int(value.subtract(rhs.value));
			}

			@Override
			public Int multiply(AbstractSemantics.RValue.Int _rhs) {
				RValue.Int rhs = (RValue.Int) _rhs;
				return new Int(value.multiply(rhs.value));
			}

			@Override
			public Int divide(AbstractSemantics.RValue.Int _rhs) {
				RValue.Int rhs = (RValue.Int) _rhs;
				return new Int(value.divide(rhs.value));
			}

			@Override
			public Int remainder(AbstractSemantics.RValue.Int _rhs) {
				RValue.Int rhs = (RValue.Int) _rhs;
				return new Int(value.remainder(rhs.value));
			}

			@Override
			public Bool lessThan(AbstractSemantics.RValue.Int _rhs) {
				RValue.Int rhs = (RValue.Int) _rhs;
				return (value.compareTo(rhs.value) < 0) ? True : False;
			}

			@Override
			public Bool lessThanOrEqual(AbstractSemantics.RValue.Int _rhs) {
				RValue.Int rhs = (RValue.Int) _rhs;
				return (value.compareTo(rhs.value) <= 0) ? True : False;
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

		public final static class Array extends RValue implements AbstractSemantics.RValue.Array {
			private final RValue[] elements;

			private Array(RValue... elements) {
				this.elements = elements;
			}
			@Override
			public RValue.Bool is(Type type, Interpreter instance) throws ResolutionError {
				if(type instanceof Type.Array) {
					Type.Array t = (Type.Array) type;
					for (int i = 0; i != elements.length; ++i) {
						if (elements[i].is(t.getElement(), instance) == False) {
							return False;
						}
					}
					return True;
				} else {
					return super.is(type, instance);
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
					return new RValue.Array(values);
				} else {
					return super.convert(type);
				}
			}
			@Override
			public RValue read(AbstractSemantics.RValue.Int _index) {
				RValue.Int index = (RValue.Int) _index;
				int idx = index.value.intValue();
				if(idx < 0 || idx >= elements.length) {
					throw new AssertionError("out-of-bounds array access");
				}
				return elements[idx];
			}

			@Override
			public RValue.Array write(AbstractSemantics.RValue.Int _index, AbstractSemantics.RValue value) {
				RValue.Int index = (RValue.Int)_index;
				int idx = index.value.intValue();
				RValue[] values = Arrays.copyOf(this.elements, this.elements.length);
				values[idx] = (RValue) value;
				return new RValue.Array(values);
			}

			public RValue[] getElements() {
				return elements;
			}

			@Override
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

		public final static class Field implements AbstractSemantics.RValue.Field {
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

			@Override
			public Identifier getName() {
				return name;
			}

			@Override
			public RValue getValue() {
				return value;
			}
		}

		public final static class Record extends RValue implements AbstractSemantics.RValue.Record {
			private final RValue.Field[] fields;

			private Record(RValue.Field... fields) {
				this.fields = fields;
				// Sort fields according by name. This avoids any difficulties when
				// comparing records initialised with different field orders.
				Arrays.sort(fields, new Comparator<RValue.Field>() {
					@Override
					public int compare(RValue.Field f1, RValue.Field f2) {
						return f1.name.compareTo(f2.name);
					}
				});
			}

			@Override
			public int size() {
				return fields.length;
			}

			@Override
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
			public RValue.Bool is(Type type, Interpreter instance) throws ResolutionError {
				if (type instanceof Type.Record) {
					Type.Record t = (Type.Record) type;
					Tuple<Type.Field> tFields = t.getFields();
					for (int i = 0; i != tFields.size(); ++i) {
						Type.Field f = tFields.get(i);
						if (hasField(f.getName())) {
							RValue val = read(f.getName());
							// Matching field
							if (val.is(f.getType(), instance) == False) {
								// Field not member of type
								return False;
							}
						} else {
							// No matching field
							return False;
						}
					}
					return (t.isOpen() || fields.length == tFields.size()) ? True : False;
				} else {
					return super.is(type, instance);
				}
			}

			@Override
			public RValue convert(Type type) {
				if (type instanceof Type.Record) {
					Type.Record t = (Type.Record) type;
					Tuple<Type.Field> fields = t.getFields();
					RValue.Record rec = this;
					for (int i = 0; i != fields.size(); ++i) {
						Type.Field f = fields.get(i);
						RValue v = this.read(f.getName()).convert(f.getType());
						rec = rec.write(f.getName(), v);
					}
					return rec;
				} else {
					return super.convert(type);
				}
			}
			@Override
			public RValue read(Identifier field) {
				for (int i = 0; i != fields.length; ++i) {
					RValue.Field f = fields[i];
					if (f.name.equals(field)) {
						return f.value;
					}
				}
				throw new RuntimeException("Invalid record access");
			}

			@Override
			public RValue.Record write(Identifier field, AbstractSemantics.RValue value) {
				RValue.Field[] fields = Arrays.copyOf(this.fields, this.fields.length);
				for (int i = 0; i != fields.length; ++i) {
					RValue.Field f = fields[i];
					if (f.name.equals(field)) {
						fields[i] = new RValue.Field(f.name, (RValue) value);
						return new RValue.Record(fields);
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

			@Override
			public String toString() {
				String r = "{";
				for(int i=0;i!=fields.length;++i) {
					if(i != 0) {
						r = r + ",";
					}
					RValue.Field field = fields[i];
					r += field.name + ":" + field.value;
				}
				return r + "}";
			}
		}

		public final static class Lambda extends RValue implements AbstractSemantics.RValue.Lambda {
			/**
			 * Identify the declaration for this lambda
			 */
			private final Decl.Callable context;
			/**
			 * The frame which holds true at this point.
			 */
			private final Interpreter.CallStack frame;
			/**
			 * The body of the lambda. This is either a stmt block or an expression.
			 */
			private final Stmt body;

			private Lambda(Decl.Callable context, Interpreter.CallStack frame, Stmt body) {
				this.context = context;
				this.frame = frame;
				this.body = body;
			}

			public Decl.Callable getContext() {
				return context;
			}

			public Interpreter.CallStack getFrame() {
				return frame;
			}

			public Stmt getBody() {
				return body;
			}

			@Override
			public RValue.Bool is(Type type, Interpreter instance) throws ResolutionError {
				if(type instanceof Type.Callable) {
					Type.Callable tc = (Type.Callable) type;
					// FIXME: this is really a hack, since we need to perform a full
					// subtype test at this point. There remain some interesting
					// questions as to what should and should not be support here.
					if(tc.equals(context.getType())) {
						return True;
					} else {
						return False;
					}
				} else {
					return super.is(type, instance);
				}
			}

			@Override
			public boolean equals(Object o) {
				if(o instanceof RValue.Lambda) {
					RValue.Lambda l = (RValue.Lambda) o;
					return context.equals(l.context) && body.equals(l.body);
				}
				return false;
			}

			@Override
			public int hashCode() {
				return context.hashCode() ^ context.hashCode();
			}
		}

		public final static class Reference extends RValue implements AbstractSemantics.RValue.Reference {
			private final RValue.Cell referent;

			private Reference(RValue.Cell referent) {
				this.referent = referent;
			}

			@Override
			public RValue.Cell deref() {
				return referent;
			}

			@Override
			public RValue.Bool is(Type type, Interpreter instance) throws ResolutionError {
				if(type instanceof Type.Reference) {
					Type.Reference ref = (Type.Reference) type;
					return referent.value.is(ref.getElement(), instance);
				} else {
					return super.is(type, instance);
				}
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

		public final static class Cell extends RValue implements AbstractSemantics.RValue.Cell {
			private RValue value;

			private Cell(RValue value) {
				this.value = value;
			}

			@Override
			public RValue read() {
				return value;
			}

			@Override
			public void write(AbstractSemantics.RValue value) {
				this.value = (RValue) value;
			}
		}
	}

	public static abstract class LValue {
		abstract public RValue read(CallStack frame);
		abstract public void write(CallStack frame, RValue rhs);

		public static final class Variable extends LValue {
			private final Identifier name;

			public Variable(Identifier name) {
				this.name = name;
			}

			@Override
			public RValue read(CallStack frame) {
				return frame.getLocal(name);
			}

			@Override
			public void write(CallStack frame, RValue rhs) {
				frame.putLocal(name, rhs);
			}
		}

		public static class Array extends LValue {
			private final LValue src;
			private final RValue.Int index;

			public Array(LValue src, RValue.Int index) {
				this.src = src;
				this.index = index;
			}

			@Override
			public RValue read(CallStack frame) {
				RValue.Array src = Interpreter.checkType(this.src.read(frame), null, RValue.Array.class);
				return src.read(index);
			}

			@Override
			public void write(CallStack frame, RValue value) {
				RValue.Array arr = Interpreter.checkType(this.src.read(frame), null, RValue.Array.class);
				src.write(frame, arr.write(index, value));
			}
		}

		public static class Record extends LValue {
			private final LValue src;
			private final Identifier field;

			public Record(LValue src, Identifier field) {
				this.src = src;
				this.field = field;
			}

			@Override
			public RValue read(CallStack frame) {
				RValue.Record src = Interpreter.checkType(this.src.read(frame), null, RValue.Record.class);
				return src.read(field);
			}

			@Override
			public void write(CallStack frame, RValue value) {
				RValue.Record rec = Interpreter.checkType(this.src.read(frame), null, RValue.Record.class);
				src.write(frame, rec.write(field, value));
			}
		}

		public static class Dereference extends LValue {
			private final LValue src;

			public Dereference(LValue src) {
				this.src = src;
			}

			@Override
			public RValue read(CallStack frame) {
				RValue.Reference ref = Interpreter.checkType(src.read(frame), null, RValue.Reference.class);
				RValue.Cell cell = ref.deref();
				return cell.read();
			}

			@Override
			public void write(CallStack frame, RValue rhs) {
				RValue.Reference ref = Interpreter.checkType(src.read(frame), null, RValue.Reference.class);
				RValue.Cell cell = ref.deref();
				cell.write(rhs);
			}
		}
	}
}