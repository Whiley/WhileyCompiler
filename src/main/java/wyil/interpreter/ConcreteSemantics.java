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

import wyal.util.NameResolver.ResolutionError;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.Type;
import wyil.lang.WyilFile.Type.Callable;

public class ConcreteSemantics implements AbstractSemantics {

	@Override
	public RValue.Null Null() {
		return RValue.Null;
	}

	@Override
	public RValue.Bool Bool(boolean value) {
		return RValue.Bool(value);
	}

	@Override
	public RValue.Byte Byte(byte value) {
		return RValue.Byte(value);
	}


	@Override
	public RValue.Int Int(BigInteger value) {
		return RValue.Int(value);
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
		return RValue.Array((RValue[]) elements);
	}

	@Override
	public RValue.Field Field(Identifier name, AbstractSemantics.RValue value) {
		return RValue.Field(name,(RValue) value);
	}

	@Override
	public RValue.Record Record(AbstractSemantics.RValue.Field... fields) {
		return RValue.Record((RValue.Field[]) fields);
	}

	@Override
	public RValue.Lambda Lambda(Decl.Callable context, Interpreter.CallStack frame) {
		return new RValue.ConcreteLambda(context, frame);
	}

	public static abstract class RValue implements AbstractSemantics.RValue {
		public static final RValue.Null Null = new RValue.Null();
		public static final RValue.Bool True = new RValue.Bool(true);
		public static final RValue.Bool False = new RValue.Bool(false);


		public static RValue.Bool Bool(boolean value) {
			return value ? RValue.True : RValue.False;
		}

		public static RValue.Byte Byte(byte value) {
			return new RValue.Byte(value);
		}

		public static RValue.Int Int(BigInteger value) {
			return new RValue.Int(value);
		}

		public static RValue.Array Array(RValue... elements) {
			return new RValue.Array(elements);
		}

		public static RValue.Record Record(RValue.Field... fields) {
			return new RValue.Record(fields);
		}

		public static RValue.Field Field(Identifier name, RValue value) {
			return new RValue.Field(name,value);
		}

		public static RValue.Cell Cell(AbstractSemantics.RValue value) {
			return new RValue.Cell((RValue) value);
		}

		public static RValue.Reference Reference(AbstractSemantics.RValue.Cell value) {
			RValue.Cell cell = (RValue.Cell) value;
			return new RValue.Reference(cell);
		}

		public static RValue.Lambda Lambda(Decl.Callable context, Interpreter.CallStack frame) {
			return new RValue.ConcreteLambda(context, frame);
		}

		/**
		 * Check whether a given value is an instanceof of a given type.
		 *
		 * @param type
		 * @return
		 * @throws ResolutionError
		 */
		@Override
		public Bool is(Type type, Interpreter.CallStack frame) {
			// Handle generic cases here
			if (type instanceof Type.Void) {
				return False;
			} else if (type instanceof Type.Nominal) {
				Type.Nominal nom = (Type.Nominal) type;
				Decl.Type decl = nom.getLink().getTarget();
				Decl.Variable var = decl.getVariableDeclaration();
				if(is(nom.getConcreteType(), frame) == True) {
					Tuple<Expr> invariant = decl.getInvariant();
					return checkInvariant(var,invariant,frame);
				}
				return False;
			} else if (type instanceof Type.Union) {
				Type.Union t = (Type.Union) type;
				for (int i=0;i!=t.size();++i) {
					Type element = t.get(i);
					if (this.is(element, frame) == True) {
						return True;
					}
				}
			} else if (type instanceof Type.Variable) {
				// NOTE: for now, type variables cannot have bounds and cannot be used in
				// runtime type tests. Therefore, we can always assume this is always true. The
				// only situation this is use is for checking type invariants within the
				// interpreter.
				return True;
			}
			// Default case.
			return False;
		}

		@Override
		public RValue convert(Type type) {
			if(type instanceof Type.Union) {
				Type.Union t = (Type.Union) type;
				for (int i=0;i!=t.size();++i) {
					Type element = t.get(i);
					RValue r = this.convert(element);
					if (r != null) {
						return r;
					}
				}
				return null;
			} else if(type instanceof Type.Nominal) {
				Type.Nominal nom = (Type.Nominal) type;
				Decl.Type decl = nom.getLink().getTarget();
				Decl.Variable var = decl.getVariableDeclaration();
				return convert(var.getType());
			} else {
				return null;
			}
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
		 * Convert to a value object which can be stored in a WyilFile.
		 * @return
		 */
		public abstract Value toValue();
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
		public Bool checkInvariant(Decl.Variable var, Tuple<Expr> invariant, Interpreter.CallStack frame) {
			if (invariant.size() > 0) {
				// One or more type invariants to check. Therefore, we need
				// to execute the invariant and determine whether or not it
				// returns true.
				frame = frame.enter(var);
				frame.putLocal(var.getName(), this);
				for (int i = 0; i != invariant.size(); ++i) {
					RValue.Bool b = frame.execute(Bool.class, invariant.get(i), frame);
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
			public Bool is(Type type, Interpreter.CallStack frame) {
				if(type instanceof Type.Null) {
					return True;
				} else {
					return super.is(type, frame);
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
				return "Null";
			}
			@Override
			public Value.Null toValue() {
				return new Value.Null();
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
			public Bool is(Type type, Interpreter.CallStack frame) {
				if(type instanceof Type.Bool) {
					return True;
				} else {
					return super.is(type, frame);
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
			@Override
			public Value.Bool toValue() {
				return new Value.Bool(value);
			}
		}

		public final static class Byte extends RValue implements AbstractSemantics.RValue.Byte {
			private final byte value;

			private Byte(byte value) {
				this.value = value;
			}

			@Override
			public RValue.Bool is(Type type, Interpreter.CallStack frame) {
				if (type instanceof Type.Byte) {
					return True;
				} else {
					return super.is(type, frame);
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

			@Override
			public Value.Byte toValue() {
				return new Value.Byte(value);
			}
		}

		public final static class Int extends RValue implements AbstractSemantics.RValue.Int {
			private final BigInteger value;

			private Int(BigInteger value) {
				this.value = value;
			}
			@Override
			public Bool is(Type type, Interpreter.CallStack frame) {
				if(type instanceof Type.Int) {
					return True;
				} else {
					return super.is(type, frame);
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

			@Override
			public Value.Int toValue() {
				return new Value.Int(value);
			}
		}

		public final static class Array extends RValue implements AbstractSemantics.RValue.Array {
			private final RValue[] elements;

			private Array(RValue... elements) {
				this.elements = elements;
			}
			@Override
			public RValue.Bool is(Type type, Interpreter.CallStack frame) {
				if(type instanceof Type.Array) {
					Type.Array t = (Type.Array) type;
					for (int i = 0; i != elements.length; ++i) {
						if (elements[i].is(t.getElement(), frame) == False) {
							return False;
						}
					}
					return True;
				} else {
					return super.is(type, frame);
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

			@Override
			public Value.Array toValue() {
				Value[] es = new Value[elements.length];
				for(int i=0;i!=es.length;++i) {
					es[i] = elements[i].toValue();
				}
				return new Value.Array(es);
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

			@Override
			public String toString() {
				return name + ":" + value;
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
			public RValue.Bool is(Type type, Interpreter.CallStack frame) {
				if (type instanceof Type.Record) {
					Type.Record t = (Type.Record) type;
					Tuple<Type.Field> tFields = t.getFields();
					for (int i = 0; i != tFields.size(); ++i) {
						Type.Field f = tFields.get(i);
						if (hasField(f.getName())) {
							RValue val = read(f.getName());
							// Matching field
							if (val.is(f.getType(), frame) == False) {
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
					return super.is(type, frame);
				}
			}

			@Override
			public RValue convert(Type type) {
				if (type instanceof Type.Record) {
					Type.Record t = (Type.Record) type;
					Tuple<Type.Field> fields = t.getFields();
					RValue.Field[] rs = new RValue.Field[fields.size()];
					for (int i = 0; i != fields.size(); ++i) {
						Type.Field ff = fields.get(i);
						Identifier name = ff.getName();
						rs[i] = new RValue.Field(name,read(name).convert(ff.getType()));
					}
					return Record(rs);
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

			@Override
			public Value toValue() {
				WyilFile.Pair<Identifier,Value>[] entries = new WyilFile.Pair[fields.length];
				for(int i=0;i!=fields.length;++i) {
					RValue.Field field = fields[i];
					entries[i] = new WyilFile.Pair<>(field.name,field.value.toValue());
				}
				return new Value.Dictionary(entries);
			}
		}

		public abstract static class Lambda extends RValue implements AbstractSemantics.RValue.Lambda {
			/**
			 * Execute this lambda with the given arguments. For example, in the normal
			 * case, we can use the interpreter to execute the body of the lambda.
			 *
			 * @param interpreter
			 * @param arguments
			 * @param item
			 * @return
			 */
			public abstract RValue[] execute(Interpreter interpreter, RValue[] arguments, SyntacticItem context);

			/**
			 * Get the callable type for this lambda
			 * @return
			 */
			public abstract Type.Callable getType();

			@Override
			public RValue convert(Type type) {
				// Create a lambda for the coercion
				if (type instanceof Type.Callable) {
					return new RValue.Lambda() {

						@Override
						public RValue[] execute(Interpreter interpreter, RValue[] arguments, SyntacticItem context) {
							return RValue.Lambda.this.execute(interpreter, arguments, context);
						}

						@Override
						public Callable getType() {
							return (Type.Callable) type;
						}

						@Override
						public Value toValue() {
							return RValue.Lambda.this.toValue();
						}

						@Override
						public RValue.Bool is(Type t, Interpreter.CallStack frame) {
							if(t instanceof Type.Callable) {
								Type.Callable tc = (Type.Callable) t;
								if(tc.equals(type)) {
									return True;
								} else {
									return False;
								}
							} else {
								return super.is(t, frame);
							}
						}
					};
				} else {
					return super.convert(type);
				}
			}
		}

		public final static class ConcreteLambda extends Lambda {
			/**
			 * Identify the declaration for this lambda
			 */
			private final Decl.Callable context;
			/**
			 * The frame which holds true at this point.
			 */
			private final Interpreter.CallStack frame;

			private ConcreteLambda(Decl.Callable context, Interpreter.CallStack frame) {
				this.context = context;
				this.frame = frame;
			}

			public Decl.Callable getContext() {
				return context;
			}

			public Interpreter.CallStack getFrame() {
				return frame;
			}

			@Override
			public Type.Callable getType() {
				return context.getType();
			}

			/**
			 * Execute this lambda with the given arguments. For a concrete lambda like
			 * this, we simply use the interpreter to execute the body of the lambda.
			 *
			 * @param interpreter
			 * @param arguments
			 * @param item
			 * @return
			 */
			@Override
			public RValue[] execute(Interpreter interpreter, RValue[] arguments, SyntacticItem item) {
				return interpreter.execute(context, frame, arguments, item);
			}

			@Override
			public RValue.Bool is(Type type, Interpreter.CallStack frame) {
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
					return super.is(type, frame);
				}
			}


			@Override
			public boolean equals(Object o) {
				if(o instanceof RValue.ConcreteLambda) {
					RValue.ConcreteLambda l = (RValue.ConcreteLambda) o;
					return context.equals(l.context);
				}
				return false;
			}

			@Override
			public int hashCode() {
				return context.hashCode() ^ context.hashCode();
			}

			@Override
			public Value toValue() {
				// FIXME: need to implement this
				return new Value.Null();
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
			public RValue.Bool is(Type type, Interpreter.CallStack frame) {
				if(type instanceof Type.Reference) {
					Type.Reference ref = (Type.Reference) type;
					return referent.value.is(ref.getElement(), frame);
				} else {
					return super.is(type, frame);
				}
			}

			@Override
			public RValue convert(Type type) {
				if(type instanceof Type.Reference) {
					return this;
				} else {
					return super.convert(type);
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

			@Override
			public String toString() {
				return "&" + System.identityHashCode(referent);
			}

			@Override
			public Value toValue() {
				// FIXME: need to implement this
				return new Value.Null();
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

			@Override
			public Value toValue() {
				// FIXME: need to implement this
				return new Value.Null();
			}
		}
	}
}