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

import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.lang.CompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.Type;

/**
 * <p>
 * A type mangler is responsible for turning the list of parameter types for a
 * function, method or property into an appropriate "mangle". That is, a string
 * which uniquely encodes the type. This is necessary because Whiley support
 * overloading of functions, methods and properties. For example, consider this
 * Whiley code:
 * </p>
 *
 * <pre>
 * function append(int[] xs, int x):
 *    ...
 *
 * function append(int[] xs, int[] ys):
 *    ...
 * </pre>
 *
 * <p>
 * Each of these is translated into a WyLL method of the form
 * <code>append_X</code> where <code>X</code> is the appropriate mangle. For the
 * first function above, an appropriate mangle might be "aii", indicating an
 * array of integers and an integer parameter. Then, the second would be "aiai"
 * indicating two arrays of integers.
 * </p>
 * <p>
 * The actual rules for generating the mangle are platform-dependent. This is
 * because they are constrained by the permitted set of characters in an
 * identifier. As such, the concept of a type mangle is a parameter to the
 * translation process to allow platform-specific customisation.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface TypeMangler {
	/**
	 * Construct the mangle for a given sequence of zero or more types.
	 *
	 * @param types
	 * @return
	 */
	public String getMangle(Type... types);

	/**
	 * Construct the mangle for a given sequence of zero or more types.
	 *
	 * @param types
	 * @return
	 */
	public String getMangle(Type type);

	/**
	 * Provides a default implementation of a type mangler. This uses a standard
	 * translation of Whiley types into ASCII strings containing only lowercase and
	 * uppercase characters, the digits and the underscore. The translation is:
	 *
	 * <ul>
	 * <li>
	 * <p>
	 * <b>Primitives</b>. Each primitive types is encoded using a single capital
	 * letter. Here, <code>Any</code> is <code>A</code>, <code>Null</code> is
	 * <code>N</code>, <code>Bool</code> is <code>B</code>, <code>Int</code> is
	 * <code>I</code>.
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <b>Nominals</b>. These are encoded using a single uppercase prefix
	 * <code>Q</code> followed by, for each component, the number of characters and
	 * then the component identifier. For example, the name
	 * <code>std::ascii::string</code> gives <code>Q3std4ascii6string</code>.
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <b>Arrays</b>. These are encoded using a single lowercase prefix
	 * <code>a</code>, followed by the mangled element type. For example,
	 * <code>int[]</code> gives "<code>aI</code>".
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <b>References</b>. These are encoded using a single lowercase prefix of
	 * either <code>q</code>. For non-static references, this is followed by the
	 * lifetime index which is a De Bruijn index. Eitherway, the mangled element
	 * type follows next. For example, <code>&int</code> gives "<code>qI</code>",
	 * whilst <code>&l:bool</code> gives "<code>q0B</code>" (assuming lifetime
	 * <code>l</code> is the first declared lifetime variable).
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <b>Records</b>. These are encoded using a single lowercase prefix
	 * <code>r</code> followed by the number of fields then, for each field, its
	 * mangled type followed by the number of characters in the field name and then
	 * the field name itself. For example, the type <code>{int op, bool flag}</code>
	 * gives the mangle "<code>r2I2opB4flag</code>". This encoding accounts
	 * correctly for the possibility of nested records.
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <b>Callables</b>. Functions, Methods and Properties are encoded using the
	 * prefixes <code>f</code>, <code>m</code> and <code>p</code> respectively.
	 * These are followed by the number of parameters and the parameter mangles and,
	 * likewise, for the returns. For example, <code>function(int,bool)->any</code>
	 * becomes "<code>f2IB1A</code>".
	 * </p>
	 * </li>
	 * <li>
	 * <p>
	 * <b>Unions, Intersections and Negations</b>. Negations are handled as for
	 * arrays using the prefix <code>n</code>. Unions and intersections are handled
	 * using the prefixes <code>u</code> and <code>i</code> respectively. This is
	 * followed by the number of components and then the components themselves. For
	 * example, <code>int|null</code> becomes "<code>u2IN</code>".
	 * </p>
	 * </li>
	 * </ul>
	 *
	 * @author David J. Pearce
	 *
	 */
	public class Default implements TypeMangler {

		@Override
		public String getMangle(Type... types) {
			StringBuilder mangle = new StringBuilder();
			for (int i = 0; i != types.length; ++i) {
				writeTypeMangle(types[i], mangle);
			}
			return mangle.toString();
		}

		@Override
		public String getMangle(Type types) {
			StringBuilder mangle = new StringBuilder();
			for (int i = 0; i != types.shape(); ++i) {
				writeTypeMangle(types.dimension(i), mangle);
			}
			return mangle.toString();
		}

		private void writeTypeMangle(Type t, StringBuilder mangle) {
			switch (t.getOpcode()) {
			case TYPE_void:
				mangle.append("V");
				break;
			case TYPE_null:
				mangle.append("N");
				break;
			case TYPE_bool:
				mangle.append("B");
				break;
			case TYPE_byte:
				mangle.append("U");
				break;
			case TYPE_int:
				mangle.append("I");
				break;
			case TYPE_array:
				writeTypeMangleArray((Type.Array) t, mangle);
				break;
			case TYPE_reference:
				writeTypeMangleReference((Type.Reference) t, mangle);
				break;
			case TYPE_record:
				writeTypeMangleRecord((Type.Record) t, mangle);
				break;
			case TYPE_nominal:
				writeTypeMangleNominal((Type.Nominal) t, mangle);
				break;
			case TYPE_function:
				writeTypeMangleFunctionOrMethod('f', (Type.Callable) t, mangle);
				break;
			case TYPE_method:
				writeTypeMangleFunctionOrMethod('m', (Type.Callable) t, mangle);
				break;
			case TYPE_property:
				writeTypeMangleFunctionOrMethod('p', (Type.Callable) t, mangle);
				break;
			case TYPE_tuple:
				writeTypeMangleTuple((Type.Tuple) t, mangle);
				break;
			case TYPE_union:
				writeTypeMangleUnion((Type.Union) t, mangle);
				break;
			case TYPE_universal:
				writeTypeMangleVariable((Type.Universal) t, mangle);
				break;
			default:
				throw new IllegalArgumentException("unknown type encountered: " + t.getClass().getName());
			}
		}

		private void writeTypeMangleArray(Type.Array t, StringBuilder mangle) {
			mangle.append('a');
			writeTypeMangle(t.getElement(), mangle);
		}

		private void writeTypeMangleReference(Type.Reference t, StringBuilder mangle) {
			mangle.append('q');
			writeTypeMangle(t.getElement(), mangle);
		}

		private void writeTypeMangleRecord(Type.Record rt, StringBuilder mangle) {
			mangle.append('r');
			Tuple<Type.Field> fields = rt.getFields();
			mangle.append(fields.size());
			for (int i = 0; i != fields.size(); ++i) {
				Type.Field field = fields.get(i);
				writeTypeMangle(field.getType(), mangle);
				String fieldName = field.getName().get();
				mangle.append(fieldName.length());
				mangle.append(fieldName);
			}
		}

		private void writeTypeMangleNominal(Type.Nominal t, StringBuilder mangle) {
			// FIXME: need to deal with qualified names properly!
			Name name = t.getLink().getName();
			mangle.append('Q');
			for (int i = 0; i != name.size(); ++i) {
				String component = name.get(i).get();
				mangle.append(component.length());
				mangle.append(component);
			}
		}

		private void writeTypeMangleFunctionOrMethod(char prefix, Type.Callable t, StringBuilder mangle) {
			mangle.append(prefix);
			writeTypeMangle(t.getParameter(), mangle);
			writeTypeMangle(t.getReturn(), mangle);
		}

		private void writeTypeMangleUnion(Type.Union t, StringBuilder mangle) {
			mangle.append('u');
			mangle.append(t.size());
			for (int i = 0; i != t.size(); ++i) {
				writeTypeMangle(t.get(i), mangle);
			}
		}

		private void writeTypeMangleTuple(Type.Tuple t, StringBuilder mangle) {
			// Tuples are the only types which may start with a digit.
			mangle.append(t.size());
			for (int i = 0; i != t.size(); ++i) {
				writeTypeMangle(t.get(i), mangle);
			}
		}

		private void writeTypeMangleVariable(Type.Universal t, StringBuilder mangle) {
			String name = t.getOperand().toString();
			mangle.append("v" + name.length() + name);
		}

		private int find(Identifier lifetime, Tuple<Identifier> lifetimes) {
			for (int i = 0; i != lifetimes.size(); ++i) {
				if (lifetimes.get(i).equals(lifetime)) {
					return i;
				}
			}
			throw new IllegalArgumentException("invalid lifetime " + lifetime + " in " + lifetimes);
		}
	}
}
