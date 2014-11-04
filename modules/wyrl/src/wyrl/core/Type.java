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

package wyrl.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import wyautl.core.*;
import wyautl.io.BinaryAutomataWriter;
import wyfs.io.BinaryOutputStream;
import static wyrl.core.Types.*;

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
		int i = 0;
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
	 * if the result of the expression is a reference then dereference it!
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
			if (kind != K_Any && kind != K_Void && kind != K_Bool
					&& kind != K_Int && kind != K_Real && kind != K_String) {
				throw new IllegalArgumentException("Invalid atom kind");
			}
			int root = automaton.add(new Automaton.Term(kind));
			automaton.setRoot(0,root);
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
			if (kind != K_Meta && kind != K_Ref && kind != K_Not) {
				throw new IllegalArgumentException("Invalid unary kind");
			}
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.addAll(element_automaton.getRoot(0),
					element_automaton);
			int root = automaton.add(new Automaton.Term(kind, elementRoot));
			automaton.setRoot(0,root);
		}
		private Unary(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_Meta && kind != K_Ref && kind != K_Not) {
				throw new IllegalArgumentException("Invalid unary kind");
			}
		}
		public Type element() {
			int root = automaton.getRoot(0);
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
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_And && kind != K_Or && kind != K_Fun) {
				throw new IllegalArgumentException("Invalid nary kind");
			}
		}
		private Nary(int kind, int compound, Type... elements) {
			int[] children = new int[elements.length];
			for (int i = 0; i != children.length; ++i) {
				Type element = elements[i];
				Automaton element_automaton = element.automaton;
				int child = automaton.addAll(element_automaton.getRoot(0),
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
			automaton.setRoot(0,root);
		}

		public Type element(int index) {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(term.contents);
			return extract(collection.get(index));
		}

		public Type[] elements() {
			int root = automaton.getRoot(0);
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
			automaton.setRoot(0,root);
		}

		private Term(String name, Type.Ref element) {
			int stringRoot = automaton.add(new Automaton.Strung(name));
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.addAll(element_automaton.getRoot(0),
					element_automaton);
			int argument = automaton.add(new Automaton.List(stringRoot,
					elementRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.setRoot(0,root);
		}

		private Term(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_Term) {
				throw new IllegalArgumentException("Invalid nary kind");
			}
		}
		public String name() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));
			return str.value;
		}

		public Ref element() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			if(list.size() < 2) {
				return null;
			} else {
				return (Ref) extract(list.get(1));
			}
		}
	}

	public static final class Nominal extends Type {
		private Nominal(String name) {
			int stringRoot = automaton.add(new Automaton.Strung(name));
			int argument = automaton.add(new Automaton.List(stringRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.setRoot(0,root);
		}

		private Nominal(String name, Type.Ref element) {
			int stringRoot = automaton.add(new Automaton.Strung(name));
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.addAll(element_automaton.getRoot(0),
					element_automaton);
			int argument = automaton.add(new Automaton.List(stringRoot,
					elementRoot));
			int root = automaton.add(new Automaton.Term(K_Term, argument));
			automaton.setRoot(0,root);
		}

		private Nominal(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_Nominal) {
				throw new IllegalArgumentException("Invalid nary kind");
			}
		}
		public String name() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));
			return str.value;
		}

		public Type element() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			return extract(list.get(1));
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
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_Set && kind != K_Bag && kind != K_List) {
				throw new IllegalArgumentException("Invalid collection kind");
			}
		}
		private Collection(int kind, boolean unbounded,
				Type... elements) {
			if (kind != K_Set && kind != K_Bag && kind != K_List) {
				throw new IllegalArgumentException("Invalid collection kind");
			}

			int length = unbounded ? elements.length-1 : elements.length;
			int lastRoot = automaton.add(new Automaton.Term(Types.K_Void));

			int[] children = new int[length];
			for (int i = 0; i != elements.length; ++i) {
				Type element = elements[i];
				Automaton element_automaton = element.automaton;
				int child = automaton.addAll(element_automaton.getRoot(0),
						element_automaton);
				if(i == length) {
					lastRoot = child;
				} else {
					children[i] = child;
				}
			}

			int compoundRoot;
			switch (kind) {
			case K_Set:
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

			int listRoot = automaton.add(new Automaton.List(lastRoot,compoundRoot));
			int root = automaton.add(new Automaton.Term(kind, listRoot));
			automaton.setRoot(0,root);
		}

		public boolean unbounded() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Term unbounded = (Automaton.Term) automaton.get(list.get(0));
			return unbounded.kind != Types.K_Void;
		}

		public Type[] elements() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(list.get(1));
			boolean unbounded = unbounded();
			int length = unbounded ? collection.size()+1 : collection.size();

			Type[] elements = new Type[length];
			for(int i=0;i!=collection.size();++i) {
				elements[i] = extract(collection.get(i));
			}
			if(unbounded) {
				elements[length-1] = extract(list.get(0));
			}

			return elements;
		}

		public Type element() {
			Type[] elements = elements();
			if(elements.length == 0) {
				return Type.T_VOID;
			} else if(elements.length == 1) {
				return elements[0];
			} else {
				return T_OR(elements());
			}
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
	 * <p>
	 * Return true if argument (<code>t1</code>) is a subtype of this type (
	 * <code>t2</code>). This function operates in a seemingly strange way. To
	 * perform the subtype check, it computes the type <code>t1 && !t2</code>.
	 * If this reduces to type <code>void</code>, then we can be certain that
	 * <code>t1</code> is entirely closed within <code>t2</code>.
	 * </p>
	 *
	 *
	 *
	 * @param t1
	 *            --- super-type to test for.
	 * @param t2
	 *            --- sub-type to test for.
	 * @return
	 */
	public boolean isSubtype(Type t) {
//		Type result = Type.T_AND(Type.T_NOT(this),t);
//		Types.reduce(result.automaton);
//		boolean r1 = result.equals(Type.T_VOID());
//		boolean r2 = isSubtype(this,t,10);
//		if(!r1 && r2) {
//			System.err.println("REDUCTION APPROACH FAILED FOR: " + this + " :> " + t + " (" + result + ")");
//		} else if(r1 && !r2) {
//			System.err.println("MANUAL APPROACH FAILED FOR: " + this + " :> " + t);
//		}
//
//		return r1 || r2;
		return isSubtype(this,t,10);
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

	public String toString() {
		int root = automaton.getRoot(0);
		int[] headers = new int[automaton.nStates()];
		Automata.traverse(automaton,root,headers);
		return toString(root,headers);
	}

	public String toString(int root, int[] headers) {
		String body = "";
		int header = 0;
		if(root >= 0) {
			header = headers[root];
			if(header == 3) {
				// FIXME: still a bug here in the case of a header whose
				// recursive reference is not reached (i.e. because it's blocked
				// by a Nominal type).
				body = ("$" + root + "<");
				headers[root] = -1;
			} else if(header < 0) {
				return "$" + root;
			}
		}

		Automaton.Term term = (Automaton.Term) automaton.get(root);
		switch(term.kind) {
			case K_Void:
				body += "void";
				break;
			case K_Any:
				body += "any";
				break;
			case K_Bool:
				body += "bool";
				break;
			case K_Int:
				body += "int";
				break;
			case K_Real:
				body += "real";
				break;
			case K_String:
				body += "string";
				break;
			case K_Ref:
				body += "^" + toString(term.contents,headers);
				break;
			case K_Meta:
				body += "?" + toString(term.contents,headers);
				break;
			case K_Not:
				body += "!" + toString(term.contents,headers);
				break;
			case K_Or : {
				Automaton.Set set = (Automaton.Set) automaton
						.get(term.contents);
				for (int i = 0; i != set.size(); ++i) {
					if (i != 0) {
						body += "|";
					}
					body += toString(set.get(i), headers);
				}
				break;
			}
			case K_And : {
				Automaton.Set set = (Automaton.Set) automaton
						.get(term.contents);
				for (int i = 0; i != set.size(); ++i) {
					if (i != 0) {
						body += "&";
					}
					body += toString(set.get(i), headers);
				}
				break;
			}
			case K_List:
			case K_Bag:
			case K_Set: {
				Automaton.List list = (Automaton.List) automaton.get(term.contents);
				// FIXME: following 2 lines to be updated
				Automaton.Term unbounded = (Automaton.Term) automaton.get(list.get(0));
				// end
				Automaton.Collection c = (Automaton.Collection) automaton.get(list.get(1));
				String tmp = "";
				for(int i=0;i!=c.size();++i) {
					if(i != 0) {
						tmp += ",";
					}
					tmp += toString(c.get(i),headers);
				}
				if(unbounded.kind != Types.K_Void) {
					if(c.size() != 0) {
						tmp += ",";
					}
					tmp += toString(list.get(0),headers) + "...";
				}
				switch(term.kind) {
					case K_Set:
						body += "{" + tmp + "}";
						break;
					case K_Bag:
						body +=  "{|" + tmp + "|}";
					case K_List:
						body += "[" + tmp + "]";
				}
				break;
			}
			case K_Nominal: {
				Automaton.List list = (Automaton.List) automaton.get(term.contents);
				Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));
				body += str.value;
				// IUncomment following line when debugging!
				body += "<" + toString(list.get(1),headers) + ">";
				break;
			}
			case K_Term: {
				Automaton.List list = (Automaton.List) automaton.get(term.contents);
				Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));
				if(list.size() > 1) {
					body += str.value + "(" + toString(list.get(1),headers) + ")";
				} else {
					body += str.value;
				}
				break;
			}
			default:
				throw new IllegalArgumentException("unknown type encountered (" + SCHEMA.get(term.kind).name + ")");
		}

		if(header > 2) {
			body += ">";
		}

		return body;
	}

	public byte[] toBytes() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		BinaryOutputStream bos = new BinaryOutputStream(bout);
		BinaryAutomataWriter bw = new BinaryAutomataWriter(bos, SCHEMA);
		bw.write(automaton);
		bw.flush();
		return bout.toByteArray();
	}

	/**
	 * Extract the type described by a given node in the automaton. This is
	 * primarily used to extract subcomponents of a type (e.g. the element of a
	 * reference type).
	 *
	 * @param child
	 *            --- child node to be extracted.
	 * @return
	 */
	protected Type extract(int child) {
		Automaton automaton = new Automaton();
		int root = automaton.addAll(child, this.automaton);
		automaton.setRoot(0,root);
		return construct(automaton);
	}

	/**
	 * Construct a given type from an automaton. This is primarily used to
	 * reconstruct a type after expansion.
	 *
	 * @param automaton
	 * @return
	 */
	public static Type construct(Automaton automaton) {
		automaton.minimise();
		automaton.compact();

		int root = automaton.getRoot(0);
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
		case K_Nominal:
			return new Type.Nominal(automaton);
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

	/**
	 * Following is useful for bootstrapping the whole system.
	 *
	 * @param t1
	 * @param t2
	 * @return
	 */
	private static boolean isSubtype(Type t1, Type t2, int count) {
		if(t1 == null || t2 == null) {
			throw new IllegalArgumentException("arguments cannot be null");
		}
		if(count == 0) {
			return true;
		} else {
			count = count - 1;
		}
		if (t1 == t2 || (t2 instanceof Void) || t1 instanceof Any) {
			return true;
		} else if ((t1 instanceof Set && t2 instanceof Set)
				|| (t1 instanceof Bag && (t2 instanceof Set || t2 instanceof Bag))
				|| (t1 instanceof List && (t2 instanceof Set || t2 instanceof Bag || t2 instanceof List))) {
			// RULE: S-LIST
			Collection l1 = (Collection) t1;
			Collection l2 = (Collection) t2;
			Type[] l1_elements = l1.elements();
			Type[] l2_elements = l2.elements();
			if (l1_elements.length != l2_elements.length && !l1.unbounded()) {
				return false;
			} else if (l2.unbounded() && !l1.unbounded()) {
				return false;
			} else if(l2.elements().length < l1.elements().length-1) {
				return false;
			}
			int min_len = Math.min(l1_elements.length, l2_elements.length);
			for (int i = 0; i != min_len; ++i) {
				if (!isSubtype(l1_elements[i], l2_elements[i], count)) {
					return false;
				}
			}
			Type l1_last = l1_elements[l1_elements.length-1];
			for (int i = min_len; i != l2_elements.length; ++i) {
				if (!isSubtype(l1_last,l2_elements[i], count)) {
					return false;
				}
			}
			return true;
		} else if (t1 instanceof Term && t2 instanceof Term) {
			Term n1 = (Term) t1;
			Term n2 = (Term) t2;
			if(n1.name().equals(n2.name())) {
				Type n1_element = n1.element();
				Type n2_element = n2.element();
				if(n1_element == null || n2_element == null) {
					return n1_element == n2_element;
				} else {
					return isSubtype(n1_element,n2_element,count);
				}
			} else {
				//System.out.println("STAGE 6");
				return false;
			}
		} else if(t1 instanceof Nominal && t2 instanceof Nominal) {
			Nominal n1 = (Nominal) t1;
			Nominal n2 = (Nominal) t2;
			if(n1.name().equals(n2.name())) {
				return true; // early exit
			} else {
				return isSubtype(n1.element(),n2.element(),count);
			}
		} else if(t1 instanceof Nominal) {
			Nominal n1 = (Nominal) t1;
			return isSubtype(n1.element(),t2,count);
		} else if(t2 instanceof Nominal) {
			Nominal n2 = (Nominal) t2;
			return isSubtype(t1,n2.element(),count);
		} else if (t2 instanceof Or) {
			Or o2 = (Or) t2;
			for(Type b2 : o2.elements()) {
				if(!isSubtype(t1,b2,count)) {
					return false;
				}
			}
			return true;
		} else if (t1 instanceof Or) {
			Or o1 = (Or) t1;
			for(Type b1 : o1.elements()) {
				if(isSubtype(b1,t2,count)) {
					return true;
				}
			}
			return false;
		} else if (t1 instanceof Not && t2 instanceof Not) {
			Not r1 = (Not) t1;
			Not r2 = (Not) t2;
			return isSubtype(r1.element(),r2.element(),count);
		} else if(t1 instanceof Ref && t2 instanceof Ref) {
			Ref r1 = (Ref) t1;
			Ref r2 = (Ref) t2;
			return isSubtype(r1.element(),r2.element(),count);
		} else if(t1 instanceof Meta && t2 instanceof Meta) {
			Meta r1 = (Meta) t1;
			Meta r2 = (Meta) t2;
			return isSubtype(r1.element(),r2.element(),count);
		}
		return false;
	}

}

