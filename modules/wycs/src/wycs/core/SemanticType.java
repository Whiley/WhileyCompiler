package wycs.core;

import java.io.IOException;

import java.util.Map;
import wyautl.core.*;
import wyautl.io.PrettyAutomataWriter;
import static wycs.core.Types.*;

public abstract class SemanticType {

	// =============================================================
	// Public Interface
	// =============================================================
	
	public static final Any Any = new Any();
	public static final Void Void = new Void();
	public static final Bool Bool = new Bool();
	public static final Int Int = new Int();
	public static final Real Real = new Real();
	public static final String String = new String();
	public static final SemanticType IntOrReal = Or(Int,Real);
	public static final Set SetAny = new Set(Any);
	public static final Set SetTupleAnyAny = Set(Tuple(Any,Any));
	
	public static Var Var(java.lang.String name) {
		return new Var(name);
	}
	
	public static Tuple Tuple(SemanticType... elements) {
		for (SemanticType t : elements) {
			if (t instanceof SemanticType.Void) {
				throw new IllegalArgumentException(
						"Tuple type cannot contain void elementt");
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
	
	public static Set Set(SemanticType element) {
		return new Set(element);
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
			SemanticType.Var... generics) {
		return new Function(from, to, generics);
	}
	
	// ==================================================================
	// Atoms
	// ==================================================================
	
	public static abstract class Atom extends SemanticType {
		public Atom(int kind) {
			if (kind != K_Any && kind != K_Void && kind != K_Bool
					&& kind != K_String && kind != K_Int && kind != K_Real) {
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

	public static final class String extends Atom {
		private String() {
			super(K_String);
		}
	}
	
	public static class Var extends SemanticType {
		public Var(java.lang.String name) {					
			int root = Types.Var(automaton, name);
			automaton.setRoot(0,root);
		}
		private Var(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_Var) {
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
	
	
	// ==================================================================
	// Unary Terms
	// ==================================================================
	
	public static abstract class Unary extends SemanticType {
		public Unary(int kind, SemanticType element) {		
			if (kind != K_Not && kind != K_Set) {
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
			if (kind != K_Not && kind != K_Set) {
				throw new IllegalArgumentException("Invalid unary kind");
			}
		}
		public SemanticType element() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			return extract(term.contents);
		}
	}

	public static final class Not extends Unary {
		private Not(SemanticType element) {
			super(K_Not, element);
		}

		private Not(Automaton automaton) {
			super(automaton);
		}
	}
	
	// ==================================================================
	// Nary Terms
	// ==================================================================
	
	public static abstract class Nary extends SemanticType {
		private Nary(Automaton automaton) {
			super(automaton);
			int kind = automaton.get(automaton.getRoot(0)).kind;
			if (kind != K_And && kind != K_Or && kind != K_Tuple
					&& kind != K_Function) {
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
			case wyone.core.Types.K_Set:
				compoundRoot = automaton.add(new Automaton.Set(children));
				break;
			case wyone.core.Types.K_Bag:
				compoundRoot = automaton.add(new Automaton.Bag(children));
				break;
			case wyone.core.Types.K_List:
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
			super(K_And,wyone.core.Types.K_Set,bounds);
		}

		private And(Automaton automaton) {
			super(automaton);
		}		
	}
	
	public static class Or extends Nary {
		private Or(SemanticType... bounds) {
			super(K_Or,wyone.core.Types.K_Set,bounds);
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
	
	public final static class Set extends Unary {
		private Set(SemanticType element) {
			super(K_Set, element);
		}

		private Set(Automaton automaton) {
			super(automaton);
		}
	}
	
	public final static class Tuple extends Nary implements EffectiveTuple {
		private Tuple(SemanticType... elements) {
			super(K_Tuple, wyone.core.Types.K_List, elements);
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
		private Function(SemanticType from, SemanticType to, SemanticType.Var... generics) {
			super(K_Function, wyone.core.Types.K_List, append(from,to,generics));
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
		
		public SemanticType.Var[] generics() {
			SemanticType[] elements = elements();
			SemanticType.Var[] generics = new SemanticType.Var[elements.length-2];
			for (int i = 2; i != elements.length; ++i) {
				generics[i - 2] = (SemanticType.Var) elements[i];
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
		Automaton nAutomaton = new Automaton(automaton);
		
		int[] keys = new int[binding.size()];
		int[] types = new int[binding.size()];
		
		int i=0;
		for(Map.Entry<java.lang.String, SemanticType> e : binding.entrySet()) {
			java.lang.String key = e.getKey();
			SemanticType type = e.getValue();
			keys[i] = Types.Var(nAutomaton, key);
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
			case K_Var:
				Automaton.Strung s = (Automaton.Strung) automaton.get(term.contents); 
				body += s.value;
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
			case K_Set: 
				body += "{" + toString(term.contents,headers) + "}";
				break;
			case K_Tuple: {
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
			case K_Function: {
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
		automaton.minimise();
		
		int root = automaton.getRoot(0);
		Automaton.State state = automaton.get(root);
		switch(state.kind) {
		// atoms
		case K_Void:
			return Void;
		case K_Any:
			return Any;
		case K_Bool:
			return Bool;
		case K_Int:
			return Int;
		case K_Real:
			return Real;
		case K_Var:
			return new SemanticType.Var(automaton);
		case K_Not:
			return new SemanticType.Not(automaton);
		case K_And:
			return new SemanticType.And(automaton);
		case K_Or: {
			SemanticType.Or t = new SemanticType.Or(automaton);
			if(isOrTuple(t)) {
				return new SemanticType.OrTuple(automaton);
			} else {
				return t;
			}
		}
		// compounds
		case K_Set:
			return new SemanticType.Set(automaton);
		case K_Tuple:
			return new SemanticType.Tuple(automaton);
		case K_Function:
			return new SemanticType.Function(automaton);
		default:
			throw new IllegalArgumentException("Unknown kind encountered - " + state.kind);
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
		Types.reduce(result.automaton);		
		boolean r = result.equals(SemanticType.Void);
//		System.out.println("CHECKING SUBTYPE: " + t1 + " :> " + t2 + " : " + r);		
//		try {
//			new PrettyAutomataWriter(System.err, SCHEMA, "And",
//					"Or").write(result.automaton);
//			System.out.println();
//		} catch(IOException e) {}
		return r;
	}
	
	private static SemanticType[] append(SemanticType t1, SemanticType t2, SemanticType... ts) {
		SemanticType[] r = new SemanticType[ts.length+2];
		r[0] = t1;
		r[1] = t2;
		System.arraycopy(ts, 0, r, 2, ts.length);
		return r;
	}
}
