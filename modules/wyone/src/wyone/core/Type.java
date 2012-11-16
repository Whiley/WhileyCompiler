// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import wyautl.core.Automaton;
import wyautl.io.BinaryAutomataWriter;
import wyautl.io.BinaryOutputStream;
import wyautl.io.PrettyAutomataWriter;
import static wyone.util.type.Types.*;

public abstract class Type {

	// =============================================================
	// Public Interface
	// =============================================================
	
	private static Any T_ANY = new Any();
	private static Void T_VOID = new Void();
	private static Bool T_BOOL = new Bool();
	private static Int T_INT = new Int();
	private static Real T_REAL = new Real();
	private static Strung T_STRING = new Strung();
	private static Ref<Any> T_REFANY = new Ref(T_ANY);
	private static Meta T_METAANY = new Meta(T_ANY);
	private static List T_LISTANY = new List(true,T_ANY);
	private static Set T_SETANY = new Set(true,T_ANY);
	
	public static Any T_ANY() {
		if(T_ANY == null) {
			T_ANY = new Any();
		}
		return T_ANY;
	}
	
	public static Void T_VOID() {
		if(T_VOID == null) {
			T_VOID = new Void();
		}
		return T_VOID;
	}
	
	public static Bool T_BOOL() {
		if(T_BOOL == null) {
			T_BOOL = new Bool();
		}
		return T_BOOL;
	}
	
	public static Int T_INT() {
		if(T_INT == null) {
			T_INT = new Int();
		}
		return T_INT;
	}
	
	public static Real T_REAL() {
		if(T_REAL == null) {
			T_REAL = new Real();
		}
		return T_REAL;
	}
	
	public static Strung T_STRING() {
		if(T_STRING == null) {
			T_STRING = new Strung();
		}
		return T_STRING;
	}
	
	public static Ref<Any> T_REFANY() {
		if(T_REFANY == null) {
			T_REFANY = new Ref(T_ANY());
		}
		return T_REFANY;
	}
	
	public static Meta T_METAANY() {
		if(T_METAANY == null) {
			T_METAANY = new Meta(T_ANY());
		}
		return T_METAANY;
	}
	
	public static List T_LISTANY() {
		if(T_LISTANY == null) {
			T_LISTANY = new List(true,T_ANY());
		}
		return T_LISTANY;
	}
	
	public static Set T_SETANY() {
		if(T_SETANY == null) {
			T_SETANY = new Set(true,T_ANY());
		}
		return T_SETANY;
	}
	
	public static Collection T_COMPOUND(Type.Collection template,
			boolean unbounded, Type... elements) {
		if (template instanceof List) {
			return new List(unbounded, elements);
		} else if (template instanceof Bag) {
			return new Bag(unbounded, elements);
		} else {
			return new Set(unbounded, elements);
		}
	}
	
	public static Collection T_COMPOUND(Type.Collection template,
			boolean unbounded, java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i = 0;
		for (Type t : elements) {
			es[i++] = t;
		}
		if (template instanceof List) {
			return new List(unbounded, es);
		} else if (template instanceof Bag) {
			return new Bag(unbounded, es);
		} else {
			return new Set(unbounded, es);
		}
	}
	
	public static List T_LIST(boolean unbounded, java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i =0;
		for(Type t : elements) {
			es[i++] = t;
		}
		return new List(unbounded,es);
	}
	
	public static List T_LIST(boolean unbounded, Type... elements) {
		return new List(unbounded,elements);
	}
	
	public static Set T_SET(boolean unbounded, java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i =0;
		for(Type t : elements) {
			es[i++] = t;
		}
		return new Set(unbounded,es);
	}
	
	public static Set T_SET(boolean unbounded, Type... elements) {
		return new Set(unbounded,elements);
	}
	
	public static Bag T_BAG(boolean unbounded, java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i =0;
		for(Type t : elements) {
			es[i++] = t;
		}
		return new Bag(unbounded,es);
	}
	
	public static Bag T_BAG(boolean unbounded, Type... elements) {
		return new Bag(unbounded,elements);
	}
	
	public static Term T_TERM(String name, Type.Ref data) {
		if(data != null) {
			return new Term(name,data);
		} else {
			return new Term(name);
		}
	}
	
	public static Ref T_REF(Type element) {
		return new Ref(element);
	}
	
	public static Meta T_META(Type element) {
		return new Meta(element);
	}
	
	public static Not T_NOT(Type element) {
		return new Not(element);
	}
	
	public static And T_AND(Type... elements) {
		return new And(elements);
	}
	
	public static And T_AND(java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i = 0;
		for (Type t : elements) {
			es[i++] = t;
		}
		return new And(es);
	}
	
	public static Or T_OR(Type... elements) {
		return new Or(elements);
	}
	
	public static Or T_OR(java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i =0;
		for(Type t : elements) {
			es[i++] = t;
		}
		return new Or(es);
	}
	
	public static Fun T_FUN(Type ret, Type param) {
		return new Fun(ret,param);
	}
	
	/**
	 * Coerce the result of the given expression into a value. In other words,
	 * if the result of the expression is a reference then derference it!
	 * 
	 * @param type
	 *            --- type to unbox.
	 */
	public static Type unbox(Type type) {		
		if(type instanceof Type.Ref) {
			Type.Ref ref = (Type.Ref) type;
			return ref.element();
		} else {
			return type;
		}
	}
	
	/**
	 * Coerce the result of the given expression into a reference. In other
	 * words, if the result of the expression is a value then generate a
	 * reference to that value!
	 * 
	 * @param type
	 *            --- type to box.
	 */
	public static Type box(Type type) {
		if (type instanceof Type.Ref) {
			return type;
		} else {
			return Type.T_REF(type);
		}
	}
	
	// ==================================================================
	// Atoms
	// ==================================================================
	
	public static abstract class Atom extends Type {
		public Atom(int kind) {
			int root = automaton.add(new Automaton.Term(kind));
			automaton.setMarker(0,root);
		}
	}
	
	public static final class Any extends Atom {
		private Any() {
			super(K_Any);
		}
	}

	public static final class Void extends Atom {
		private Void() {
			super(K_Void);
		}
	}
	
	public static final class Bool extends Atom {
		private Bool() {
			super(K_Bool);
		}
	}

	public static final class Int extends Atom {
		private Int() {
			super(K_Int);
		}
	}

	public static final class Real extends Atom {
		private Real() {
			super(K_Real);
		}
	}
	
	public static final class Strung extends Atom {
		private Strung() {
			super(K_String);
		}
	}
	
	// ==================================================================
	// Unary Terms
	// ==================================================================
	
	public static abstract class Unary extends Type {
		public Unary(int kind, Type element) {		
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.addAll(element_automaton.markers[0],
					element_automaton);
			int root = automaton.add(new Automaton.Term(kind, elementRoot));
			automaton.setMarker(0,root);
		}
		private Unary(Automaton automaton) {
			super(automaton);
		}
		public Type element() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			return extract(term.contents);
		}
	}

	public static final class Meta extends Unary {
		private Meta(Type element) {
			super(K_Meta, element);
		}
		private Meta(Automaton automaton) {
			super(automaton);
		}
	}

	public static final class Ref<T extends Type> extends Unary {
		private Ref(T element) {
			super(K_Ref, element);
		}

		private Ref(Automaton automaton) {
			super(automaton);
		}

		public T element() {
			return (T) super.element();
		}
	}
	
	public static final class Not extends Unary {
		private Not(Type element) {
			super(K_Not, element);
		}

		private Not(Automaton automaton) {
			super(automaton);
		}
	}
	
	// ==================================================================
	// Nary Terms
	// ==================================================================
	
	public static abstract class Nary extends Type {
		private Nary(Automaton automaton) {
			super(automaton);
		}
		private Nary(int kind, int compound, Type... elements) {
			int[] children = new int[elements.length];
			for (int i = 0; i != children.length; ++i) {
				Type element = elements[i];
				Automaton element_automaton = element.automaton;
				int child = automaton.addAll(element_automaton.markers[0],
						element_automaton);
				children[i] = child;
			}
			int compoundRoot;
			switch (compound) {
			case K_Set:
				compoundRoot = automaton.add(new Automaton.Set(children));
				break;
			case K_Bag:
				compoundRoot = automaton.add(new Automaton.Bag(children));
				break;
			case K_List:
				compoundRoot = automaton.add(new Automaton.List(children));
				break;
			default:
				throw new IllegalArgumentException(
						"invalid compound type in Nary constructor");
			}

			int root = automaton.add(new Automaton.Term(kind, compoundRoot));
			automaton.setMarker(0,root);
		}

		public Type element(int index) {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(term.contents);
			return extract(collection.get(index));
		}

		public Type[] elements() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(term.contents);
			Type[] elements = new Type[collection.size()];
			for (int i = 0; i != elements.length; ++i) {
				elements[i] = extract(collection.get(i));
			}
			return elements;
		}
	}
	
	public static final class Term extends Type {
		private Term(String name) {
			int stringRoot = automaton.add(new Automaton.Strung(name));
			int argument = automaton.add(new Automaton.List(stringRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.setMarker(0,root);
		}

		private Term(String name, Type.Ref element) {
			int stringRoot = automaton.add(new Automaton.Strung(name));
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.addAll(element_automaton.markers[0],
					element_automaton);
			int argument = automaton.add(new Automaton.List(stringRoot,
					elementRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.setMarker(0,root);
		}

		private Term(Automaton automaton) {
			super(automaton);
		}
		public String name() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));
			return str.value;
		}
		
		public Ref element() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			if(list.size() < 2) {
				return null;
			} else {
				return (Ref) extract(list.get(1));
			}
		}		
	}
	
	public static final class Fun extends Nary {
		private Fun(Type ret, Type param) {
			super(K_Fun,K_List,ret,param);
		}
		private Fun(Automaton automaton) {
			super(automaton);
		}
		public Type ret() {
			return element(0);
		}

		public Type param() {
			return element(1);
		}
	}
	
	public static final class And extends Nary {
		private And(Type... bounds) {
			super(K_And,K_Set,bounds);
		}

		private And(Automaton automaton) {
			super(automaton);
		}		
	}
	
	public static final class Or extends Nary {
		private Or(Type... bounds) {
			super(K_Or,K_Set,bounds);
		}
		
		private Or(Automaton automaton) {
			super(automaton);
		}		
	}
	
	// ==================================================================
	// Compounds
	// ==================================================================			
	
	public static abstract class Collection extends Type {
		private Collection(Automaton automaton) {
			super(automaton);
		}
		private Collection(int kind, boolean unbounded,
				Type... elements) {
			// FIXME: this will need to be updated.
			int boolRoot = unbounded 
					? automaton.add(new Automaton.Term(K_True)) 
					: automaton.add(new Automaton.Term(K_False));
					
			int[] children = new int[elements.length];
			for (int i = 0; i != children.length; ++i) {
				Type element = elements[i];
				Automaton element_automaton = element.automaton;
				int child = automaton.addAll(element_automaton.markers[0],
						element_automaton);
				children[i] = child;
			}
			int compoundRoot;
			switch (kind) {
			case K_Set:
				compoundRoot = automaton.add(new Automaton.Set(children));
				break;
			case K_Bag:
				compoundRoot = automaton.add(new Automaton.Bag(children));
				break;
			case K_List:
				compoundRoot = automaton.add(new Automaton.List(children));
				break;
			default:
				throw new IllegalArgumentException(
						"invalid compound type in Nary constructor");
			}
			
			int listRoot = automaton.add(new Automaton.List(boolRoot,compoundRoot));
			int root = automaton.add(new Automaton.Term(kind, listRoot));
			automaton.setMarker(0,root);
		}

		public boolean unbounded() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Term bool = (Automaton.Term) automaton.get(list.get(0));
			return bool.kind == K_True;
		}
		
		public Type element(int index) {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(list.get(1));
			return extract(collection.get(index));
		}
		
		public Type[] elements() {
			int root = automaton.getMarker(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(list.get(1));
			Type[] elements = new Type[collection.size()];
			for(int i=0;i!=elements.length;++i) {
				elements[i] = extract(collection.get(i));
			}
			return elements;
		}
		
		public Type element() {
			return T_OR(elements());
		}
		
		protected String body() {
			String r = "";
			Type[] elements = elements();
			for (int i = 0; i != elements.length; ++i) {
				if (i != 0) {
					r += ",";
				}
				r += elements[i];
			}
			if (unbounded()) {
				r += "...";
			}
			return r;
		}
	}
	
	public final static class Set extends Collection {
		private Set(boolean unbounded, Type... elements) {
			super(K_Set, unbounded, elements);
		}

		private Set(Automaton automaton) {
			super(automaton);
		}
	}
	
	public final static class Bag extends Collection {
		private Bag(boolean unbounded, Type... elements) {
			super(K_Bag, unbounded, elements);
		}

		private Bag(Automaton automaton) {
			super(automaton);
		}
	}
	
	public final static class List extends Collection {
		private List(boolean unbounded, Type... elements) {
			super(K_List, unbounded, elements);
		}

		private List(Automaton automaton) {
			super(automaton);
		}
	}
	
	
	
	// =============================================================
	// Private Implementation
	// =============================================================

	protected final Automaton automaton;
	
	private Type() {
		this.automaton = new Automaton();
	}	
	
	private Type(Automaton automaton) {
		this.automaton = automaton;
	}
	
	public Automaton automaton() {
		return automaton;
	}
	
	/**
	 * Apply reduction rules to generate canonical form.
	 */
	public void minimise() {
		wyone.util.type.Types.reduce(automaton);
	}
	
	public int hashCode() {
		return automaton.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof Type) {
			Type r = (Type) o;
			return automaton.equals(r.automaton);
		}
		return false;
	}
	
	protected Type extract(int child) {
		Automaton automaton = new Automaton();
		int root = automaton.addAll(child, this.automaton);
		automaton.setMarker(0,root);
		return construct(automaton);
	}
	
	public String toString() {
		StringWriter sw = new StringWriter();
		PrettyAutomataWriter paw = new PrettyAutomataWriter(sw, SCHEMA);
		try {
			paw.write(automaton);
		} catch (IOException e) {
			// bah humbug
		}
		return sw.toString();
	}
	
	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		BinaryOutputStream bos = new BinaryOutputStream(bout);
		BinaryAutomataWriter bw = new BinaryAutomataWriter(bos, SCHEMA);
		bw.write(automaton);
		bw.flush();
		return bout.toByteArray();
	}
	
	public static Type construct(Automaton automaton) {
		int root = automaton.getMarker(0);
		Automaton.State state = automaton.get(root);
		switch(state.kind) {
		// atoms
		case K_Void:
			return Type.T_VOID;
		case K_Any:
			return Type.T_ANY;
		case K_Bool:
			return Type.T_BOOL;
		case K_Int:
			return Type.T_INT;
		case K_Real:
			return Type.T_REAL;
		case K_String:
			return Type.T_STRING;
		// unaries
		case K_Ref:
			return new Type.Ref(automaton);
		case K_Meta:
			return new Type.Meta(automaton);
		case K_Not:
			return new Type.Not(automaton);
		case K_Term:
			return new Type.Term(automaton);
		// naries
		case K_Fun:
			return new Type.Fun(automaton);
		case K_And:
			return new Type.And(automaton);
		case K_Or:
			return new Type.Or(automaton);
		// compounds
		case K_Set:
			return new Type.Set(automaton);
		case K_Bag:
			return new Type.Bag(automaton);
		case K_List:
			return new Type.List(automaton);
		default:
			throw new IllegalArgumentException("Unknown kind encountered - " + state.kind);
		}
	}
}

