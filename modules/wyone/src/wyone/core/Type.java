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

import java.util.*;
import static wyone.util.type.Types.*;

public abstract class Type {

	// =============================================================
	// Public Interface
	// =============================================================
	
	public static final Any T_ANY = new Any();
	public static final Void T_VOID = new Void();
	public static final Bool T_BOOL = new Bool();
	public static final Int T_INT = new Int();
	public static final Real T_REAL = new Real();
	public static final Strung T_STRING = new Strung();
	public static final Ref<Any> T_REFANY = new Ref(T_ANY);
	public static final Meta T_METAANY = new Meta(T_ANY);
	public static final List T_LISTANY = new List(true,T_ANY);
	public static final Set T_SETANY = new Set(true,T_ANY);
	
	public static Compound T_COMPOUND(Type.Compound template,
			boolean unbounded, Type... elements) {
		if (template instanceof List) {
			return new List(unbounded, elements);
		} else if (template instanceof Bag) {
			return new Bag(unbounded, elements);
		} else {
			return new Set(unbounded, elements);
		}
	}
	
	public static Compound T_COMPOUND(Type.Compound template,
			boolean unbounded, Collection<Type> elements) {
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
	
	public static List T_LIST(boolean unbounded, Collection<Type> elements) {
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
	
	public static Set T_SET(boolean unbounded, Collection<Type> elements) {
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
	
	public static Bag T_BAG(boolean unbounded, Collection<Type> elements) {
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
	
	public static And T_AND(Collection<Type> elements) {
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
	
	public static Or T_OR(Collection<Type> elements) {
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
			automaton.mark(root);
		}
	}
	
	public static final class Any extends Atom {
		private Any() {
			super(K_Any);
		}

		public String toString() {
			return "any";
		}
	}

	public static final class Void extends Atom {
		private Void() {
			super(K_Void);
		}

		public String toString() {
			return "void";
		}
	}
	
	public static final class Bool extends Atom {
		private Bool() {
			super(K_Bool);
		}

		public String toString() {
			return "bool";
		}
	}

	public static final class Int extends Atom {
		private Int() {
			super(K_Int);
		}

		public String toString() {
			return "int";
		}
	}

	public static final class Real extends Atom {
		private Real() {
			super(K_Real);
		}

		public String toString() {
			return "real";
		}
	}
	
	public static final class Strung extends Atom {
		private Strung() {
			super(K_String);
		}

		public String toString() {
			return "string";
		}
	}
	
	// ==================================================================
	// Unary Terms
	// ==================================================================
	
	private static abstract class Unary extends Type {
		public Unary(int kind, Type element) {
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.copyFrom(element_automaton.roots[0],
					element_automaton);
			int root = automaton.add(new Automaton.Term(kind, elementRoot));
			automaton.mark(root);
		}

		public Type element() {
			int root = automaton.root(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			return extract(term.contents);
		}
	}

	public static final class Meta extends Unary {
		public Meta(Type element) {
			super(K_Meta, element);
		}

		public String toString() {
			return "?" + element();
		}
	}

	public static final class Ref<T extends Type> extends Unary {
		public Ref(T element) {
			super(K_Ref, element);
		}

		public String toString() {
			return "^" + element();
		}
	}
	
	public static final class Not extends Unary {
		public Not(Type element) {
			super(K_Not, element);
		}

		public String toString() {
			return "!" + element();
		}
	}
	
	// ==================================================================
	// Nary Terms
	// ==================================================================
	
	private static abstract class Nary extends Type {
		public Nary(int kind, int compound, Type... elements) {

			int[] children = new int[elements.length];
			for (int i = 0; i != children.length; ++i) {
				Type element = elements[i];
				Automaton element_automaton = element.automaton;
				int child = automaton.copyFrom(element_automaton.roots[0],
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
			automaton.mark(root);
		}

		public Type element(int index) {
			int root = automaton.root(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Compound compound = (Automaton.Compound) automaton
					.get(term.contents);
			return extract(compound.get(index));
		}

		public Type[] elements() {
			int root = automaton.root(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Compound compound = (Automaton.Compound) automaton
					.get(term.contents);
			Type[] elements = new Type[compound.length];
			for (int i = 0; i != elements.length; ++i) {
				elements[i] = extract(compound.get(i));
			}
			return elements;
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
			return r;
		}
	}
	
	public static final class Term extends Type {
		private Term(String name) {
			int stringRoot = automaton.add(new Automaton.Strung(name));			
			int argument = automaton.add(new Automaton.List(stringRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.mark(root);
		}
		private Term(String name, Type.Ref element) {
			int stringRoot = automaton.add(new Automaton.Strung(name));
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.copyFrom(element_automaton.roots[0],
					element_automaton);
			int argument = automaton.add(new Automaton.List(stringRoot,
					elementRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.mark(root);
		}

		public String name() {
			int root = automaton.root(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));
			return str.value;
		}
		
		public Ref element() {
			int root = automaton.root(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			if(list.length < 2) {
				return null;
			} else {
				return (Ref) extract(list.get(1));
			}
		}
		
		public String toString() {
			String name = name();
			Type element = element();
			if (element instanceof Void) {
				return name;
			} else if (element instanceof Compound) {
				return name + element;
			} else {
				return name + "(" + element + ")";
			}
		}
	}
	
	public static final class Fun extends Nary {
		private Fun(Type ret, Type param) {
			super(K_Fun,K_List,ret,param);
		}

		public Type ret() {
			return element(0);
		}

		public Type param() {
			return element(1);
		}

		public String toString() {
			return ret() + "=>" + param();
		}
	}
	
	public static final class And extends Nary {
		private And(Type... bounds) {
			super(K_And,K_Set,bounds);
		}

		public String toString() {
			return "And{" + body() + "}";
		}
	}
	
	public static final class Or extends Nary {
		private Or(Type... bounds) {
			super(K_Or,K_Set,bounds);
		}

		public String toString() {
			return "Or{" + body() + "}";
		}
	}
	
	// ==================================================================
	// Compounds
	// ==================================================================			
	
	public static abstract class Compound extends Type {
		public Compound(int kind, boolean unbounded,
				Type... elements) {
			int boolRoot = unbounded 
					? automaton.add(new Automaton.Term(K_True)) 
					: automaton.add(new Automaton.Term(K_False));
					
			int[] children = new int[elements.length];
			for (int i = 0; i != children.length; ++i) {
				Type element = elements[i];
				Automaton element_automaton = element.automaton;
				int child = automaton.copyFrom(element_automaton.roots[0],
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
			automaton.mark(root);
		}

		public boolean unbounded() {
			int root = automaton.root(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Term bool = (Automaton.Term) automaton.get(list.get(0));
			return bool.kind == K_True;
		}
		
		public Type element(int index) {
			int root = automaton.root(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Compound compound = (Automaton.Compound) automaton
					.get(list.get(1));
			return extract(compound.get(index));
		}
		
		public Type[] elements() {
			int root = automaton.root(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Compound compound = (Automaton.Compound) automaton
					.get(list.get(1));
			Type[] elements = new Type[compound.length];
			for(int i=0;i!=elements.length;++i) {
				elements[i] = extract(compound.get(i));
			}
			return elements;
		}
		
		public Type element() {
			// return union of all elements
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
	
	public final static class Set extends Compound {
		private Set(boolean unbounded, Type... elements) {
			super(K_Set,unbounded,elements);
		}
		
		public String toString() {
			return "{" + body() + "}";
		}
	}
	
	public final static class Bag extends Compound {
		private Bag(boolean unbounded, Type... elements) {
			super(K_Bag, unbounded, elements);
		}

		public String toString() {
			return "{|" + body() + "|}";
		}
	}
	
	public final static class List extends Compound {
		private List(boolean unbounded, Type... elements) {
			super(K_List,unbounded,elements);
		}
		
		public String toString() {
			return "[" + body() + "]";				
		}
	}
	
	
	
	// =============================================================
	// Private Implementation
	// =============================================================

	protected final Automaton automaton;
	
	private Type() {
		this.automaton = new Automaton(SCHEMA);
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
		// TODO:
	}
}

