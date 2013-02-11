package wycs.lang;

import wyautl.core.*;
import static wycs.lang.Types.*;

public class Type {

	// =============================================================
	// Public Interface
	// =============================================================
	
	public static final Any Any = new Any();
	public static final Void Void = new Void();
	public static final Bool Bool = new Bool();
	public static final Int Int = new Int();
	public static final Real Real = new Real();
	public static final Set SetAny = new Set(Any);
	
	public static Tuple Tuple(java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i = 0;
		for(Type t : elements) {
			es[i++] = t;
		}
		return new Tuple(es);
	}
	
	public static Set Set(Type element) {
		return new Set(element);
	}
	
	public static Not Not(Type element) {
		return new Not(element);
	}
	
	public static And And(Type... elements) {
		return new And(elements);
	}
	
	public static And And(java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i = 0;
		for (Type t : elements) {
			es[i++] = t;
		}
		return new And(es);
	}
	
	public static Or Or(Type... elements) {
		return new Or(elements);
	}
	
	public static Or Or(java.util.Collection<Type> elements) {
		Type[] es = new Type[elements.size()];
		int i =0;
		for(Type t : elements) {
			es[i++] = t;
		}
		return new Or(es);
	}
	
	// ==================================================================
	// Atoms
	// ==================================================================
	
	public static abstract class Atom extends Type {
		public Atom(int kind) {
			if (kind != K_Any && kind != K_Void && kind != K_Bool
					&& kind != K_Int && kind != K_Real) {
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
	
	// ==================================================================
	// Unary Terms
	// ==================================================================
	
	public static abstract class Unary extends Type {
		public Unary(int kind, Type element) {		
			if (kind != K_Not) {
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
			if (kind != K_Not) {
				throw new IllegalArgumentException("Invalid unary kind");
			}
		}
		public Type element() {
			int root = automaton.getRoot(0);
			Automaton.Term term = (Automaton.Term) automaton.get(root);
			return extract(term.contents);
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
			if (kind != K_And && kind != K_Or) {
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
	
	public static final class And extends Nary {
		private And(Type... bounds) {
			super(K_And,wyone.core.Types.K_Set,bounds);
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
	
	public final static class Set extends Unary {
		private Set(Type element) {
			super(K_Set, element);
		}

		private Set(Automaton automaton) {
			super(automaton);
		}
	}
	
	public final static class Tuple extends Nary{
		private Tuple(Type... elements) {
			super(K_Tuple, wyone.core.Types.K_List, elements);
		}

		private Tuple(Automaton automaton) {
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
		//return isSubtype(this,t,10);
		return false;
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
				String tmp = "";
				for(int i=0;i!=elements.size();++i) {
					if(i != 0) {
						tmp += ",";
					}
					tmp += toString(elements.get(i),headers);
				}				
				body += "(" + tmp + ")";					
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
		case K_Not:
			return new Type.Not(automaton);
		case K_And:
			return new Type.And(automaton);
		case K_Or:
			return new Type.Or(automaton);
		// compounds
		case K_Set:
			return new Type.Set(automaton);
		case K_Tuple:
			return new Type.Tuple(automaton);
		default:
			throw new IllegalArgumentException("Unknown kind encountered - " + state.kind);
		}
	}
}
