// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyjc.runtime;

import java.io.IOException;
import java.util.*;

import wyfs.io.BinaryInputStream;
import wyrl.io.JavaIdentifierInputStream;
import static wyil.lang.Type.K_VOID;
import static wyil.lang.Type.K_ANY;
import static wyil.lang.Type.K_META;
import static wyil.lang.Type.K_NULL;
import static wyil.lang.Type.K_BOOL;
import static wyil.lang.Type.K_BYTE;
import static wyil.lang.Type.K_CHAR;
import static wyil.lang.Type.K_INT;
import static wyil.lang.Type.K_RATIONAL;
import static wyil.lang.Type.K_STRING;
import static wyil.lang.Type.K_TUPLE;
import static wyil.lang.Type.K_SET;
import static wyil.lang.Type.K_LIST;
import static wyil.lang.Type.K_MAP;
import static wyil.lang.Type.K_REFERENCE;
import static wyil.lang.Type.K_RECORD;
import static wyil.lang.Type.K_UNION;
import static wyil.lang.Type.K_NEGATION;
import static wyil.lang.Type.K_FUNCTION;
import static wyil.lang.Type.K_METHOD;
import static wyil.lang.Type.K_NOMINAL;

public abstract class WyType {

	public final int kind;

	private WyType(int kind) {
		this.kind = kind;
	}

	public static final Void VOID = new Void();
	public static final Any ANY = new Any();
	public static final Meta META = new Meta();
	public static final Null NULL = new Null();
	public static final Bool BOOL = new Bool();
	public static final Byte BYTE = new Byte();
	public static final Char CHAR = new Char();
	public static final Integer INT = new Integer();
	public static final Rational REAL = new Rational();
	public static final Strung STRING = new Strung();

	private static final class Void extends WyType { Void() {super(K_VOID);}}
	private static final class Any extends WyType { Any() {super(K_ANY);}}
	private static final class Meta extends WyType { Meta() {super(K_META);}}
	private static final class Null extends WyType { Null() {super(K_NULL);}}
	private static final class Bool extends WyType { Bool() {super(K_BOOL);}}
	private static final class Byte extends WyType { Byte() {super(K_BYTE);}}
	private static final class Char extends WyType { Char() {super(K_CHAR);}}
	private static final class Integer extends WyType { Integer() {super(K_INT);}}
	private static final class Rational extends WyType { Rational() {super(K_RATIONAL);}}
	private static final class Strung extends WyType { Strung() {super(K_STRING);}}

	public static final class Reference extends WyType {
		public WyType element;

		public Reference(WyType element) {
			super(K_REFERENCE);
			this.element = element;
		}
	}

	public static final class List extends WyType {
		public WyType element;
		public final boolean nonEmpty;

		public List(WyType element, boolean nonEmpty) {
			super(K_LIST);
			this.element = element;
			this.nonEmpty = nonEmpty;
		}
	}

	public static final class Set extends WyType {
		public WyType element;
		public final boolean nonEmpty;

		public Set(WyType element, boolean nonEmpty) {
			super(K_SET);
			this.element = element;
			this.nonEmpty = nonEmpty;
		}
	}

	public static final class Dictionary extends WyType {
		public WyType key;
		public WyType value;

		public Dictionary(WyType key, WyType value) {
			super(K_MAP);
			this.key = key;
			this.value = value;
		}
	}

	public static final class Record extends WyType {
		public final String[] names;
		public final WyType[] types;
		public final boolean isOpen;
		public Record(String[] names, WyType[] types, boolean open) {
			super(K_RECORD);
			this.names = names;
			this.types = types;
			this.isOpen = open;
		}
	}

	public static final class Tuple extends WyType {
		public final WyType[] types;
		public Tuple(WyType[] types) {
			super(K_TUPLE);
			this.types = types;
		}
	}

	public static final class Union extends WyType {
		public final WyType[] bounds;
		public Union(WyType... bounds) {
			super(K_UNION);
			this.bounds = bounds;
		}
	}

	private static final class Nominal extends WyType {
		public final String name;
		public Nominal(String name) {
			super(K_NOMINAL);
			this.name = name;
		}
	}

	public static final class Negation extends WyType {
		public WyType element;

		public Negation(WyType element) {
			super(K_NEGATION);
			this.element = element;
		}
	}

	public static final class Function extends WyType {
		public final WyType[] returns;
		public final WyType[] parameters;
		public Function(WyType[] returns, WyType[] parameters) {
			super(K_FUNCTION);
			this.parameters = parameters;
			this.returns = returns;
		}
	}
	
	public static final class Method extends WyType {
		public final WyType[] returns;
		public final WyType[] parameters;
		public Method(WyType[] returns, WyType[] parameters) {
			super(K_METHOD);
			this.parameters = parameters;
			this.returns = returns;
		}
	}
	
	public static final class Label extends WyType {
		public final int label;
		public Label(int label) {
			super(-1);
			this.label = label;
		}
	}

	public static WyType valueOf(String str) throws IOException {
		JavaIdentifierInputStream jin = new JavaIdentifierInputStream(str);
		BinaryInputStream bin = new BinaryInputStream(jin);
		ArrayList<WyType> nodes = new ArrayList<WyType>();
		int size = bin.read_uv();
		for(int i=0;i!=size;++i) {
			nodes.add(readNode(bin, nodes));
		}
		for(int i=0;i!=size;++i) {
			substitute(nodes.get(i),nodes);
		}
		return nodes.get(0);
	}

	private static WyType readNode(BinaryInputStream reader, ArrayList<WyType> nodes) throws IOException {
		int kind = reader.read_uv();
		boolean deterministic = reader.read_bit();
		int nchildren = reader.read_uv();
		WyType[] children = new WyType[nchildren];
		for (int i=0;i!=nchildren;++i) {
			children[i]=new Label(reader.read_uv());
		}
		switch(kind) {
		case K_VOID:
			return VOID;
		case K_ANY:
			return ANY;
		case K_META:
			return META;
		case K_NULL:
			return NULL;
		case K_BOOL:
			return BOOL;
		case K_BYTE:
			return BYTE;
		case K_CHAR:
			return CHAR;
		case K_INT:
			return INT;
		case K_RATIONAL:
			return REAL;
		case K_STRING:
			return STRING;
		case K_TUPLE: {
			return new Tuple(children);
		}
		case K_SET: {
			boolean nonEmpty = reader.read_bit();
			return new Set(children[0],nonEmpty);
		}
		case K_LIST: {
			boolean nonEmpty = reader.read_bit();
			return new List(children[0],nonEmpty);
		}
		case K_MAP: {
			return new Dictionary(children[0],children[1]);
		}
		case K_REFERENCE: {
			return new Reference(children[0]);
		}
		case K_RECORD: {
			boolean isOpen = reader.read_bit();
			int nfields = reader.read_uv();
			String[] fields = new String[nfields];
			for(int i=0;i!=nfields;++i) {
				fields[i] = readString(reader);
			}
			return new Record(fields,children,isOpen);
		}
		case K_UNION: {
			return new Union(children);
		}
		case K_NEGATION: {
			return new Negation(children[0]);
		}
		case K_NOMINAL: {
			String module = readString(reader);
			String name = readString(reader);
			return new WyType.Nominal(module + ":" + name);
		}
		case K_FUNCTION: {
			int numParams = reader.read_uv();
			return new WyType.Function(Arrays.copyOfRange(children, numParams, children.length),
					Arrays.copyOfRange(children, 0, numParams));
		}
		case K_METHOD: {
			int numParams = reader.read_uv();
			return new WyType.Method(Arrays.copyOfRange(children, numParams, children.length),
					Arrays.copyOfRange(children, 0, numParams));
		}
		}

		throw new RuntimeException("unknow type encountered (kind: " + kind + ")");
	}

	private static String readString(BinaryInputStream reader) throws IOException {
		String r = "";
		int nchars = reader.read_uv();
		for(int i=0;i!=nchars;++i) {
			char c = (char) reader.read_u16();
			r = r + c;
		}
		return r;
	}

	private static void substitute(WyType type, ArrayList<WyType> nodes) {
		switch(type.kind) {
		case K_VOID:
		case K_ANY:
		case K_META:
		case K_NULL:
		case K_BOOL:
		case K_BYTE:
		case K_CHAR:
		case K_INT:
		case K_RATIONAL:
		case K_STRING:
		case K_NOMINAL:
			return;
		case K_TUPLE: {
			Tuple t = (Tuple) type;
			substitute(t.types,nodes);
			return;
		}
		case K_SET: {
			Set t = (Set) type;
			t.element = substitute((Label)t.element,nodes);
			return;
		}
		case K_LIST: {
			List t = (List) type;
			t.element = substitute((Label)t.element,nodes);
			return;
		}
		case K_MAP: {
			Dictionary t = (Dictionary) type;
			t.key = substitute((Label)t.key,nodes);
			t.value = substitute((Label)t.value,nodes);
			return;
		}
		case K_REFERENCE: {
			Reference t = (Reference) type;
			t.element = substitute((Label)t.element,nodes);
			return;
		}
		case K_RECORD:  {
			Record t = (Record) type;
			substitute(t.types,nodes);
			return;
		}
		case K_UNION: {
			Union t = (Union) type;
			substitute(t.bounds,nodes);
			return;
		}
		case K_NEGATION: {
			Negation t = (Negation) type;
			t.element = substitute((Label)t.element,nodes);
			return;
		}
		case K_FUNCTION: {
			Function t = (Function) type;
			substitute(t.returns,nodes);
			substitute(t.parameters,nodes);
			return;
		}
		case K_METHOD: {
			Method t = (Method) type;
			substitute(t.returns,nodes);
			substitute(t.parameters,nodes);
			return;
		}
		}
		throw new RuntimeException("unknow type encountered (kind: " + type.kind + ")");
	}

	private static void substitute(WyType[] types, ArrayList<WyType> nodes) {
		for(int i=0;i!=types.length;++i) {
			Label type = (Label) types[i];
			types[i] = nodes.get(type.label);
		}
	}

	private static WyType substitute(Label type, ArrayList<WyType> nodes) {
		return nodes.get(type.label);
	}

	/**
	 * <p>
	 * This method connects up recursive links in a given type. In particular,
	 * it replaces all occurrences of variable <code>var</code> with
	 * <code>root</code>.
	 * </p>
	 *
	 * <b>NOTE:</b> the resulting type may contain a cycle. For this reason, the
	 * visited relation is required to ensure termination in the presence of
	 * such cycles.
	 *
	 * @param type
	 *            - The type currently be explored
	 * @param label
	 *            - The label to substitute for
	 * @param root
	 *            - The root of the recursive type. Variable <code>var</code>
	 *            will be replaced with this.
	 * @param visited
	 *            - contains all of the visited nodes. This is needed to ensure
	 *            termination in the presence of cycles.
	 * @return
	 */
	private static WyType substitute(WyType type, String label, WyType root, HashSet<WyType> visited) {
		if(visited.contains(type)) {
			return type;
		} else {
			visited.add(type);
		}
		switch(type.kind) {
			case K_ANY:
			case K_VOID:
			case K_NULL:
			case K_INT:
			case K_RATIONAL:
			case K_STRING:
				break;
			case K_NOMINAL:
			{
				WyType.Nominal leaf = (WyType.Nominal) type;
				if(leaf.name.equals(label)) {
					return root;
				} else {
					return leaf;
				}
			}
			case K_LIST:
			{
				WyType.List list = (WyType.List) type;
				list.element = substitute(list.element,label,root,visited);
				break;
			}
			case K_SET:
			{
				WyType.Set set = (WyType.Set) type;
				set.element = substitute(set.element,label,root,visited);
				break;
			}
			case K_MAP:
			{
				WyType.Dictionary dict = (WyType.Dictionary) type;
				dict.key = substitute(dict.key,label,root,visited);
				dict.value = substitute(dict.value,label,root,visited);
				break;
			}
			case K_RECORD:
			{
				WyType.Record rec = (WyType.Record) type;
				WyType[] types = rec.types;
				for(int i=0;i!=types.length;++i) {
					types[i] = substitute(types[i],label,root,visited);
				}
				break;
			}
			case K_NEGATION:
			{
				WyType.Negation not = (WyType.Negation) type;
				not.element = substitute(not.element,label,root,visited);
				break;
			}
			case K_UNION:
			{
				WyType.Union un = (WyType.Union) type;
				WyType[] types = un.bounds;
				for(int i=0;i!=types.length;++i) {
					types[i] = substitute(types[i],label,root,visited);
				}
				break;
			}
			case K_FUNCTION:
			{
				WyType.Function ft = (WyType.Function) type;
				WyType[] paramTypes = ft.parameters;
				for(int i=0;i!=paramTypes.length;++i) {
					paramTypes[i] = substitute(paramTypes[i],label,root,visited);
				}
				WyType[] returnTypes = ft.returns;
				for(int i=0;i!=returnTypes.length;++i) {
					returnTypes[i] = substitute(returnTypes[i],label,root,visited);
				}
				break;
			}
			case K_METHOD:
			{
				WyType.Method ft = (WyType.Method) type;
				WyType[] types = ft.parameters;
				for(int i=0;i!=types.length;++i) {
					types[i] = substitute(types[i],label,root,visited);
				}
				WyType[] returnTypes = ft.returns;
				for(int i=0;i!=returnTypes.length;++i) {
					returnTypes[i] = substitute(returnTypes[i],label,root,visited);
				}
				break;
			}
		}
		return type;
	}
	
	private static String toString(WyType t) {
		return toString(t, new HashSet<WyType>());
	}
	
	private static String toString(WyType t, HashSet<WyType> visited) {
		if(visited.contains(t)) {
			return "...";
		} else {
			visited.add(t);
		}
		switch (t.kind) {
		case K_ANY:
			return "any";
		case K_VOID:
			return "void";
		case K_NULL:
			return "null";
		case K_INT:
			return "int";
		case K_RATIONAL:
			return "real";
		case K_STRING:
			return "string";
		case K_NOMINAL: {
			WyType.Nominal leaf = (WyType.Nominal) t;
			return leaf.name;
		}
		case K_LIST: {
			WyType.List list = (WyType.List) t;
			return toString(list.element,visited) + "[]";
		}
		case K_RECORD: {
			WyType.Record rec = (WyType.Record) t;
			WyType[] types = rec.types;
			String r = "";
			for (int i = 0; i != types.length; ++i) {
				if (i != 0) {
					r = r + ",";
				}
				r += toString(types[i],visited) + " " + rec.names[i];
			}
			return "{" + r + "}";
		}
		case K_NEGATION: {
			WyType.Negation not = (WyType.Negation) t;
			return "!" + toString(not.element,visited);
		}
		case K_UNION: {
			WyType.Union un = (WyType.Union) t;
			WyType[] types = un.bounds;
			String r = "";
			for (int i = 0; i != types.length; ++i) {
				if (i != 0) {
					r = r + "|";
				}
				r += toString(types[i],visited);
			}
			return r;
		}
		case K_FUNCTION: {
			WyType.Function ft = (WyType.Function) t;
			WyType[] types = ft.parameters;
			String r = "function(";
			for (int i = 0; i != types.length; ++i) {
				if (i != 0) {
					r = r + ",";
				}
				r += toString(types[i],visited);
			}
			return r + ")->(" + toString(ft.returns,visited) + ")";
		}
		case K_METHOD: {
			WyType.Method ft = (WyType.Method) t;
			WyType[] types = ft.parameters;
			return "function" + toString(ft.parameters, visited) + "->" + toString(ft.returns, visited);
		}
		}

		throw new RuntimeException("unknow type encountered (kind: " + t.kind + ")");
	}
	
	private static String toString(WyType[] types, HashSet<WyType> visited) {
		String r = "(";
		for (int i = 0; i != types.length; ++i) {
			if (i != 0) {
				r = r + ",";
			}
			r += toString(types[i],visited);
		}
		return r + ")";
	}
}
