package wycs.core;

import java.io.IOException;
import java.util.Map;

import wyautl.core.*;
import wyautl.rw.RewriteProof;
import wyautl.rw.RewriteStep;
import wyautl.rw.Rewriter;
import wyautl.util.BatchRewriter;
import wycc.lang.NameID;
import static wycs.core.Types.*;

public abstract class SemanticType {

	// =============================================================
	// Public Interface
	// =============================================================

	public static final Any Any = new Any();
	public static final Void Void = new Void();
	public static final Null Null = new Null();
	public static final Bool Bool = new Bool();
	public static final Int Int = new Int();
	public static final Real Real = new Real();
	public static final String String = new String();
	public static final SemanticType IntOrReal = Or(Int,Real);
	public static final Array ArrayAny = new Array(Any);
	
	public static Var Var(java.lang.String name) {
		return new Var(name);
	}

	public static Nominal Nominal(NameID name) {
		return new Nominal(name);
	}
	
	public static Tuple Tuple(SemanticType... elements) {
		for (SemanticType t : elements) {
			if (t instanceof SemanticType.Void) {
				throw new IllegalArgumentException(
						"Tuple type cannot contain void element");
			}
		}
		return new Tuple(elements);
	}

	public static Tuple Tuple(java.util.Collection<SemanticType> elements) {
		SemanticType[] es = new SemanticType[elements.size()];
		int i = 0;
		for (SemanticType t : elements) {
			if (t instanceof SemanticType.Void) {
				throw new IllegalArgumentException(
						"Tuple type cannot contain void element");
			}
			es[i++] = t;
		}
		return new Tuple(es);
	}

	public static Array Array(SemanticType element) {
		return new Array(element);
	}

	public static SemanticType Not(SemanticType element) {
		// FIXME: this could be more efficient
		return construct(new Not(element).automaton);
	}

	public static SemanticType And(SemanticType... elements) {
		// FIXME: this could be more efficient
		return construct(new And(elements).automaton);
	}

	public static SemanticType And(java.util.Collection<SemanticType> elements) {
		SemanticType[] es = new SemanticType[elements.size()];
		int i = 0;
		for (SemanticType t : elements) {
			es[i++] = t;
		}
		// FIXME: this could be more efficient
		return construct(new And(es).automaton);
	}

	public static SemanticType Or(SemanticType... elements) {
		// FIXME: this could be more efficient
		return construct(new Or(elements).automaton);
	}

	public static SemanticType Or(java.util.Collection<SemanticType> elements) {
		SemanticType[] es = new SemanticType[elements.size()];
		int i = 0;
		for (SemanticType t : elements) {
			es[i++] = t;
		}
		// FIXME: this could be more efficient
		return construct(new Or(es).automaton);
	}

	public static Function Function(SemanticType from, SemanticType to,
			SemanticType... generics) {
		return new Function(from, to, generics);
	}

	// ==================================================================
	// Atoms
	// ==================================================================

	public static abstract class Atom extends SemanticType {
		public Atom(int kind) {
			if (kind != K_AnyT && kind != K_VoidT && kind != K_NullT
					&& kind != K_BoolT && kind != K_StringT && kind != K_IntT
					&& kind != K_RealT) {
				throw new IllegalArgumentException("Invalid atom kind");
			}
			int root = automaton.add(new Automaton.Term(kind));
			automaton.setRoot(0, root);
		}

		@Override
		public SemanticType substitute(Map<java.lang.String, SemanticType> binding) {
			// atom can never have anything substituted.
			return this;
		}
	}

	public static final class Any extends Atom {
		private Any() {
			super(K_AnyT);
		}
	}

	public static final class Void extends Atom {
		private Void() {
			super(K_VoidT);
		}
	}

	public static final class Null extends Atom {
		private Null() {
			super(K_NullT);
		}
	}

	public static final class Bool extends Atom {
		private Bool() {
			super(K_BoolT);
		}
	}

	public static final class Int extends Atom {
		private Int() {
			super(K_IntT);
		}
	}

	public static final class Real extends Atom {
		private Real() {
			super(K_RealT);
		}
	}

	public static final class String extends Atom {
		private String() {
			super(K_StringT);
		}
	}

	public static class Var extends SemanticType {
		public Var(java.lang.String name) {
			int root = Types.VarT(automaton, name);
			automaton.setRoot(0,root);
		}
		private Var(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_VarT) {
				throw new IllegalArgumentException("Invalid variable kind");
			}
		}
		public java.lang.String name() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Strung str = (Automaton.Strung) automaton.get(term.contents);
			return str.value;
		}
	}

	public static class Nominal extends SemanticType {
		public Nominal(NameID nid) {
			int root = Types.NominalT(automaton, nid.toString());
			automaton.setRoot(0,root);
		}
		private Nominal(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_NominalT) {
				throw new IllegalArgumentException("Invalid variable kind");
			}
		}
		public NameID name() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Strung str = (Automaton.Strung) automaton.get(term.contents);
			return NameID.fromString(str.value);
		}
	}
	
	// ==================================================================
	// Unary Terms
	// ==================================================================

	public static class Not extends SemanticType {
		public Not(SemanticType element) {
			Automaton element_automaton = element.automaton;
			int elementRoot = automaton.addAll(element_automaton.getRoot(0),
					element_automaton);
			int root = automaton.add(new Automaton.Term(K_NotT, elementRoot));
			automaton.setRoot(0,root);
		}
		private Not(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_NotT) {
				throw new IllegalArgumentException("Invalid unary kind");
			}
		}
		public SemanticType element() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			return extract(term.contents);
		}
	}

	public final static class Array extends SemanticType {
		
		private Array(SemanticType element) {			
			Automaton element_automaton = element.automaton;
			int child = automaton.addAll(element_automaton.getRoot(0),
					element_automaton);
			int root = automaton.add(new Automaton.Term(K_ArrayT, child));
			automaton.setRoot(0,root);
		}

		private Array(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_ArrayT) {
				throw new IllegalArgumentException("Invalid set kind");
			}
		}

		public SemanticType element() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);			
			return extract(term.contents);
		}
	}


	// ==================================================================
	// Nary Terms
	// ==================================================================

	public static abstract class Nary extends SemanticType {
		private Nary(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_AndT && kind != K_OrT && kind != K_TupleT
					&& kind != K_FunctionT) {
				throw new IllegalArgumentException("Invalid nary kind");
			}
		}
		private Nary(int kind, int compound, SemanticType... elements) {
			int[] children = new int[elements.length];
			for (int i = 0; i != children.length; ++i) {
				SemanticType element = elements[i];
				Automaton element_automaton = element.automaton;
				int child = automaton.addAll(element_automaton.getRoot(0),
						element_automaton);
				children[i] = child;
			}
			int compoundRoot;
			switch (compound) {
			case wyrl.core.Types.K_Set:
				compoundRoot = automaton.add(new Automaton.Set(children));
				break;
			case wyrl.core.Types.K_Bag:
				compoundRoot = automaton.add(new Automaton.Bag(children));
				break;
			case wyrl.core.Types.K_List:
				compoundRoot = automaton.add(new Automaton.List(children));
				break;
			default:
				throw new IllegalArgumentException(
						"invalid compound type in Nary constructor");
			}

			int root = automaton.add(new Automaton.Term(kind, compoundRoot));
			automaton.setRoot(0,root);
		}

		public SemanticType element(int index) {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(term.contents);
			return extract(collection.get(index));
		}

		public SemanticType[] elements() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			Automaton.Collection collection = (Automaton.Collection) automaton
					.get(term.contents);
			SemanticType[] elements = new SemanticType[collection.size()];
			for (int i = 0; i != elements.length; ++i) {
				elements[i] = extract(collection.get(i));
			}
			return elements;
		}
	}

	public static final class And extends Nary {
		private And(SemanticType... bounds) {
			super(K_AndT,wyrl.core.Types.K_Set,bounds);
		}

		private And(Automaton automaton) {
			super(automaton);
		}
	}

	public static class Or extends Nary {
		private Or(SemanticType... bounds) {
			super(K_OrT,wyrl.core.Types.K_Set,bounds);
		}

		private Or(Automaton automaton) {
			super(automaton);
		}
	}

	public static class OrTuple extends Or implements EffectiveTuple {
		private OrTuple(SemanticType... bounds) {
			super(bounds);
		}

		private OrTuple(Automaton automaton) {
			super(automaton);
		}

		public int size() {
			int size = Integer.MAX_VALUE;
			SemanticType[] elements = elements();
			for(int i=0;i!=elements.length;++i) {
				SemanticType.Tuple tt = (SemanticType.Tuple) elements[i];
				size = Math.min(size, tt.elements().length);
			}
			return size;
		}

		public SemanticType tupleElement(int index) {
			SemanticType[] elements = elements();
			SemanticType[] bounds = new SemanticType[elements.length];
			for (int i = 0; i != elements.length; ++i) {
				SemanticType.Tuple tt = (SemanticType.Tuple) elements[i];
				bounds[i] = tt.element(i);
			}
			return SemanticType.Or(bounds);
		}

		public SemanticType.Tuple tupleType() {
			SemanticType[] elements = elements();
			SemanticType[] bounds = new SemanticType[size()];
			SemanticType.Tuple result = null;
			for (int i = 0; i != elements.length; ++i) {
				SemanticType.Tuple tt = (SemanticType.Tuple) elements[i];
				for(int j=0;j!=bounds.length;++j) {
					SemanticType j_b1 = bounds[i];
					SemanticType j_b2 = tt.element(j);
					if(j_b1 == null) {
						bounds[j] = tt.element(j);
					} else {
						bounds[j] = SemanticType.Or(j_b1,j_b2);
					}
					result = SemanticType.Tuple(bounds);
				}
			}
			return result;
		}
	}

	// ==================================================================
	// Compounds
	// ==================================================================

	/**
	 * An effective tuple is either a tuple, or a union of tuples.
	 *
	 * @author djp
	 *
	 */
	public interface EffectiveTuple {

		/**
		 * Returns the number of direct addressable elements. That is, the
		 * smallest number of elements in any tuples.
		 *
		 * @return
		 */
		public int size();

		/**
		 * Returns the effective type of the element at the given index.
		 *
		 * @param index
		 * @return
		 */
		public SemanticType tupleElement(int index);

		/**
		 * Returns the effective tuple type.
		 * @return
		 */
		public SemanticType.Tuple tupleType();
	}

	public final static class Tuple extends Nary implements EffectiveTuple {
		private Tuple(SemanticType... elements) {
			super(K_TupleT, wyrl.core.Types.K_List, elements);
		}

		private Tuple(Automaton automaton) {
			super(automaton);
		}

		public int size() {
			return elements().length;
		}

		public SemanticType tupleElement(int index) {
			return element(index);
		}

		public SemanticType.Tuple tupleType() {
			return this;
		}
	}

	public final static class Function extends Nary {
		private Function(SemanticType from, SemanticType to, SemanticType... generics) {
			super(K_FunctionT, wyrl.core.Types.K_List, append(from,to,generics));
		}

		private Function(Automaton automaton) {
			super(automaton);
		}

		public SemanticType from() {
			return element(0);
		}

		public SemanticType to() {
			return element(1);
		}

		public SemanticType[] generics() {
			SemanticType[] elements = elements();
			SemanticType[] generics = new SemanticType[elements.length-2];
			for (int i = 2; i != elements.length; ++i) {
				generics[i - 2] = elements[i];
			}
			return generics;
		}
	}

	// =============================================================
	// Private Implementation
	// =============================================================

	protected final Automaton automaton;

	private SemanticType() {
		this.automaton = new Automaton();
	}

	private SemanticType(Automaton automaton) {
		this.automaton = automaton;
	}

	public Automaton automaton() {
		return automaton;
	}

	public int hashCode() {
		return automaton.hashCode();
	}

	public boolean equals(Object o) {
		if (o instanceof SemanticType) {
			SemanticType r = (SemanticType) o;
			return automaton.equals(r.automaton);
		}
		return false;
	}

	/**
	 * Substitute type variables for concrete types according to a given
	 * binding.
	 *
	 * @param binding
	 *            --- a map from type variable's to concrete types.
	 * @return
	 */
	public SemanticType substitute(Map<java.lang.String,SemanticType> binding) {
		// First, check whether a matching type variable exists.
		boolean matched = false;
		for(int i=0;i!=automaton.nStates();++i) {
			Automaton.State s = (Automaton.State) automaton.get(i);
			if(s != null && s.kind == Types.K_VarT) {
				Automaton.Term t = (Automaton.Term) s;
				Automaton.Strung str = (Automaton.Strung) automaton.get(t.contents);
				if(binding.containsKey(str.value)) {
					matched=true;
					break;
				}
			}
		}

		if(!matched) {
			return this;
		} else {
			// Second, perform the substitution
			Automaton nAutomaton = new Automaton(automaton);

			int[] keys = new int[binding.size()];
			int[] types = new int[binding.size()];

			int i=0;
			for(Map.Entry<java.lang.String, SemanticType> e : binding.entrySet()) {
				java.lang.String key = e.getKey();
				SemanticType type = e.getValue();
				keys[i] = Types.VarT(nAutomaton, key);
				types[i++] = nAutomaton.addAll(type.automaton.getRoot(0), type.automaton);
			}

			int root = nAutomaton.getRoot(0);
			int[] mapping = new int[nAutomaton.nStates()];
			for(i=0;i!=mapping.length;++i) {
				mapping[i] = i;
			}
			for(i=0;i!=keys.length;++i) {
				mapping[keys[i]] = types[i];
			}
			nAutomaton.setRoot(0, nAutomaton.substitute(root, mapping));
			return construct(nAutomaton);
		}
	}

	public java.lang.String toString() {
		int root = automaton.getRoot(0);
		int[] headers = new int[automaton.nStates()];
		Automata.traverse(automaton,root,headers);
		return toString(root,headers);
	}

	public java.lang.String toString(int root, int[] headers) {
		java.lang.String body = "";
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
			case K_VoidT:
				body += "void";
				break;
			case K_AnyT:
				body += "any";
				break;
			case K_NullT:
				body += "null";
				break;
			case K_BoolT:
				body += "bool";
				break;
			case K_IntT:
				body += "int";
				break;
			case K_RealT:
				body += "real";
				break;
			case K_StringT:
				body += "string";
				break;
			case K_VarT: {
				Automaton.Strung s = (Automaton.Strung) automaton.get(term.contents);
				body += s.value;
				break;
			}
			case K_NominalT: {
				Automaton.Strung s = (Automaton.Strung) automaton.get(term.contents);
				body += s.value;
				break;
			}
			case K_NotT:
				body += "!" + toString(term.contents,headers);
				break;
			case K_OrT : {
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
			case K_AndT : {
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
			case K_ArrayT:
				body += "[" + toString(term.contents,headers) + "]";				
				break;
			case K_TupleT: {
				Automaton.List elements = (Automaton.List) automaton.get(term.contents);
				java.lang.String tmp = "";
				for(int i=0;i!=elements.size();++i) {
					if(i != 0) {
						tmp += ",";
					}
					tmp += toString(elements.get(i),headers);
				}
				body += "(" + tmp + ")";
				break;
			}
			case K_FunctionT: {
				Automaton.List elements = (Automaton.List) automaton.get(term.contents);
				java.lang.String tmp = "";
				if(elements.size() > 2) {
					for(int i=2;i<elements.size();++i) {
						if(i != 2) {
							tmp += ",";
						}
						tmp += toString(elements.get(i),headers);
					}
					body += "<" + tmp + ">";
				}
				body += toString(elements.get(0), headers) + "=>"
					+ toString(elements.get(1), headers);
				break;
			}
			default:
				throw new IllegalArgumentException("unknown type encountered (" + SCHEMA.get(term.kind).name + ")");
		}

		if(header > 1) {
			body += ">";
		}

		return body;
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
	protected SemanticType extract(int child) {
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
	public static SemanticType construct(Automaton automaton) {
		// First, we canonicalise the automaton
		automaton = reduce(automaton);
		automaton.minimise();
		automaton.compact();
		automaton.canonicalise();

		// Second, construct the object representing the type
		int root = automaton.getRoot(0);
		Automaton.State state = automaton.get(root);
		switch(state.kind) {
		// atoms
		case K_VoidT:
			return Void;
		case K_AnyT:
			return Any;
		case K_NullT:
			return Null;
		case K_BoolT:
			return Bool;
		case K_IntT:
			return Int;
		case K_RealT:
			return Real;
		case K_VarT:
			return new SemanticType.Var(automaton);
		case K_NominalT:
			return new SemanticType.Nominal(automaton);
		case K_StringT:
			return String;
		// connectives
		case K_NotT:
			return new SemanticType.Not(automaton);
		case K_AndT:
			return new SemanticType.And(automaton);
		case K_OrT: {
			SemanticType.Or t = new SemanticType.Or(automaton);
			if(isOrTuple(t)) {
				return new SemanticType.OrTuple(automaton);
			} else {
				return t;
			}
		}
		// compounds
		case K_ArrayT:
			return new SemanticType.Array(automaton);
		case K_TupleT:
			return new SemanticType.Tuple(automaton);
		case K_FunctionT:
			return new SemanticType.Function(automaton);
		default:
			int kind = state.kind;
			if(kind < 0) {
				kind = -kind + Automaton.K_FREE;
			}
			throw new IllegalArgumentException("Unknown kind encountered - " + SCHEMA.get(kind).name);
		}
	}

	/**
	 * Check whether or not this is a union of tuples
	 *
	 * @param type
	 * @return
	 */
	private static boolean isOrTuple(SemanticType.Or type) {
		SemanticType[] elements = type.elements();
		for (int i = 0; i != elements.length; ++i) {
			if (!(elements[i] instanceof SemanticType.Tuple)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check that t1 :> t2 or, equivalently, that t2 is a subtype of t1. A type
	 * <code>t1</code> is said to be a subtype of another type <code>t2</code>
	 * iff the semantic set described by <code>t1</code> contains that described
	 * by <code>t2</code>.
	 *
	 * @param t1
	 *            --- Semantic type to test whether contains <code>t2</code>.
	 * @param t2
	 *            --- Semantic type to test whether contained by <code>t1</code>.
	 */
	public static boolean isSubtype(SemanticType t1, SemanticType t2) {
		SemanticType result = SemanticType.And(SemanticType.Not(t1),t2);
//		try {
//			new PrettyAutomataWriter(System.err, SCHEMA, "And",
//					"Or").write(result.automaton);
//			System.out.println();
//		} catch(IOException e) {}	
		boolean r = result.equals(SemanticType.Void);
//		System.out.println("CHECKING SUBTYPE: " + t1 + " :> " + t2 + " : " + r);
//		try {
//			new PrettyAutomataWriter(System.err, SCHEMA, "And",
//					"Or").write(result.automaton);
//			System.out.println();
//		} catch(IOException e) {}
		return r;
	}

	/**
	 * Attempt to bind a generic type against a concrete type. This will fail if
	 * no possible binding exists, otherwise it produces a binding from
	 * variables in the generic type to components from the concrete type.
	 * Examples include:
	 * <ul>
	 * <li>Binding <code>T</code> against <code>int</code> produces the binding
	 * <code>{T=>int}</code>.</li>
	 * <li>Binding <code>(T,int)</code> against <code>(int,int)</code> produces
	 * the binding <code>{T=>int}</code>.</li>
	 * <li>Binding <code>(S,T)</code> against <code>(int,bool)</code> produces
	 * the binding <code>{S=>int,T=>bool}</code>.</li>
	 * <li>Binding <code>(T,T)</code> against <code>(int,bool)</code> fails
	 * produces the binding <code>{T=>(int|bool)}</code>.</li>
	 * </ul>
	 *
	 * <b>NOTE:</b> this function is not yet fully implemented, and will not
	 * always produce a binding when one exists.
	 *
	 * @param generic
	 *            --- the generic type whose variables we are trying to bind.
	 * @param concrete
	 *            --- the concrete type whose subcomponents will be matched
	 *            against variables contained in the generic type.
	 * @param binding
	 *            --- a map into which the binding from variables names in
	 *            generic to subcomponents of concrete will be placed.
	 * @return --- true if a binding was found, or false otherwise.
	 */
	public static boolean bind(SemanticType generic, SemanticType concrete,
			java.util.Map<java.lang.String, SemanticType> binding) {

		// FIXME: this function is broken for recursive types!

		// FIXME: this function should also be moved into SemanticType

		// Whilst this function is cool, it's basically very difficult to make
		// it work well. I wonder whether or not there's a better way to
		// implement this?

		if(generic.equals(concrete)) {
			// this is a match, so we don't need to do anything.
			return true;
		} else if(generic instanceof SemanticType.Var) {
			SemanticType.Var var = (SemanticType.Var) generic;
			SemanticType b = binding.get(var.name());
			if(b != null && !b.equals(concrete)) {
				// this indicates we've already bound this argument to something
				// different.
				return false;
			}
			binding.put(var.name(), concrete);
			return true;
		} else if (generic instanceof SemanticType.Array
				&& concrete instanceof SemanticType.Array) {
			SemanticType.Array pt = (SemanticType.Array) generic;
			SemanticType.Array at = (SemanticType.Array) concrete;
			return bind(pt.element(),at.element(),binding);
		} else if (generic instanceof SemanticType.Tuple
				&& concrete instanceof SemanticType.Tuple) {
			SemanticType.Tuple pt = (SemanticType.Tuple) generic;
			SemanticType.Tuple at = (SemanticType.Tuple) concrete;
			SemanticType[] pt_elements = pt.elements();
			SemanticType[] at_elements = at.elements();
			if(pt_elements.length != at_elements.length) {
				return false;
			} else {
				for(int i=0;i!=pt_elements.length;++i) {
					if(!bind(pt_elements[i],at_elements[i],binding)) {
						return false;
					}
				}
				return true;
			}
		} else {
			// basically assume failure [though we could do better, e.g. for
			// unions, etc].
			return false;
		}
	}

	private static SemanticType[] append(SemanticType t1, SemanticType t2, SemanticType... ts) {
		SemanticType[] r = new SemanticType[ts.length+2];
		r[0] = t1;
		r[1] = t2;
		System.arraycopy(ts, 0, r, 2, ts.length);
		return r;
	}

	private static Automaton reduce(Automaton automaton) {
		Rewriter rewriter = new BatchRewriter(Types.SCHEMA, Types.reductions);
		RewriteProof st = rewriter.apply(rewriter.initialise(automaton));
		if(st.size() > 0) {
			return st.last().automaton();
		} else {
			return automaton;
		}
	}	
}
