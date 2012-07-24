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

import wyjc.io.JavaIdentifierInputStream;
import wyjvm.io.BinaryInputStream;

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
import static wyil.lang.Type.K_NOMINAL;

public abstract class Type {
	
	public final int kind;
	
	private Type(int kind) {
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
	
	private static final class Void extends Type { Void() {super(K_VOID);}}
	private static final class Any extends Type { Any() {super(K_ANY);}}
	private static final class Meta extends Type { Meta() {super(K_META);}}
	private static final class Null extends Type { Null() {super(K_NULL);}}
	private static final class Bool extends Type { Bool() {super(K_BOOL);}}
	private static final class Byte extends Type { Byte() {super(K_BYTE);}}
	private static final class Char extends Type { Char() {super(K_CHAR);}}
	private static final class Integer extends Type { Integer() {super(K_INT);}}
	private static final class Rational extends Type { Rational() {super(K_RATIONAL);}}
	private static final class Strung extends Type { Strung() {super(K_STRING);}}

	public static final class Reference extends Type {
		public Type element;
		
		public Reference(Type element) {
			super(K_REFERENCE);
			this.element = element;
		}
	}

	
	public static final class List extends Type {
		public Type element;
		public final boolean nonEmpty;

		public List(Type element, boolean nonEmpty) {
			super(K_LIST);
			this.element = element;
			this.nonEmpty = nonEmpty;
		}
	}
	
	public static final class Set extends Type {
		public Type element;
		public final boolean nonEmpty;
		
		public Set(Type element, boolean nonEmpty) {
			super(K_SET);
			this.element = element;
			this.nonEmpty = nonEmpty;
		}
	}
	
	public static final class Dictionary extends Type {
		public Type key;
		public Type value;
		
		public Dictionary(Type key, Type value) {
			super(K_MAP);
			this.key = key;
			this.value = value;
		}
	}
	
	public static final class Record extends Type {
		public final String[] names;
		public final Type[] types;
		public final boolean isOpen;
		public Record(String[] names, Type[] types, boolean open) {
			super(K_RECORD);
			this.names = names;
			this.types = types;
			this.isOpen = open;
		}
	}
	
	public static final class Tuple extends Type {		
		public final Type[] types;
		public Tuple(Type[] types) {
			super(K_TUPLE);			
			this.types = types;
		}
	}
	
	public static final class Union extends Type {
		public final Type[] bounds;		
		public Union(Type... bounds) {
			super(K_UNION);
			this.bounds = bounds;
		}
	}
	
	private static final class Nominal extends Type {
		public final String name;
		public Nominal(String name) {
			super(K_NOMINAL);
			this.name = name;
		}
	}
	
	public static final class Negation extends Type {
		public Type element;
		
		public Negation(Type element) {
			super(K_NEGATION);
			this.element = element;
		}
	}	
	
	public static final class Label extends Type {
		public final int label;
		public Label(int label) {
			super(-1);
			this.label = label;
		}
	}
	
	public static Type valueOf(String str) throws IOException {
		JavaIdentifierInputStream jin = new JavaIdentifierInputStream(str);
		BinaryInputStream bin = new BinaryInputStream(jin);
		ArrayList<Type> nodes = new ArrayList<Type>();
		int size = bin.read_uv();
		for(int i=0;i!=size;++i) {
			nodes.add(readNode(bin, nodes));
		}		
		for(int i=0;i!=size;++i) {
			substitute(nodes.get(i),nodes);
		}
		return nodes.get(0);
	}
	
	private static Type readNode(BinaryInputStream reader, ArrayList<Type> nodes) throws IOException {
		int kind = reader.read_uv();
		boolean deterministic = reader.read_bit();
		int nchildren = reader.read_uv();
		Type[] children = new Type[nchildren];		
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
			return new Type.Nominal(module + ":" + name);
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
	
	private static void substitute(Type type, ArrayList<Type> nodes) {
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
		}
		throw new RuntimeException("unknow type encountered (kind: " + type.kind + ")");
	}
	
	private static void substitute(Type[] types, ArrayList<Type> nodes) {
		for(int i=0;i!=types.length;++i) {
			Label type = (Label) types[i];
			types[i] = nodes.get(type.label);			
		}
	}
	
	private static Type substitute(Label type, ArrayList<Type> nodes) {		
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
	private static Type substitute(Type type, String label, Type root, HashSet<Type> visited) {
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
				Type.Nominal leaf = (Type.Nominal) type;
				if(leaf.name.equals(label)) {
					return root;
				} else {
					return leaf;
				}				
			}
			case K_LIST:
			{
				Type.List list = (Type.List) type;
				list.element = substitute(list.element,label,root,visited); 
				break;
			}
			case K_SET:
			{
				Type.Set set = (Type.Set) type;
				set.element = substitute(set.element,label,root,visited); 
				break;
			}
			case K_MAP:
			{
				Type.Dictionary dict = (Type.Dictionary) type;
				dict.key = substitute(dict.key,label,root,visited); 
				dict.value = substitute(dict.value,label,root,visited);
				break;
			}
			case K_RECORD:
			{
				Type.Record rec = (Type.Record) type;
				Type[] types = rec.types;
				for(int i=0;i!=types.length;++i) {
					types[i] = substitute(types[i],label,root,visited);
				}
				break;
			}
			case K_NEGATION:
			{
				Type.Negation not = (Type.Negation) type;
				not.element = substitute(not.element,label,root,visited); 
				break;
			}
			case K_UNION:
			{
				Type.Union un = (Type.Union) type;
				Type[] types = un.bounds;
				for(int i=0;i!=types.length;++i) {
					types[i] = substitute(types[i],label,root,visited);
				}
				break;
			}
		}			
		return type;
	}	
}
