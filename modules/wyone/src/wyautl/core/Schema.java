package wyautl.core;

/**
 * Provides a simple mechanism for validating that a given automaton is well-formed.
 * 
 * @author David J. Pearce
 * 
 */
public class Schema {
	private final Term[] states;
	
	public Schema(Term[] states) {
		this.states = states;
	}
	
	public int size() {
		return states.length;
	}
	
	public Term get(int i) {
		return states[i];
	}
	
	public boolean validate(Automaton automaton) {
		// at some point, it would be nice to implement this!
		return false;
	}
	
	public static final Any Any = new Any();
	
	public static final Int Int = new Int();
	
	public static final Real Real = new Real();
	
	public static final Strung String = new Strung();
	
	public static Term Term(String name) {
		return new Term(name,null);
	}
	
	public static Term Term(String name, State contents) {
		return new Term(name,contents);
	}
	
	public static Not Not(State states) {
		return new Not(states);
	}
	
	public static Or Or(State... states) {
		return new Or(states);
	}
	
	public static Set Set(boolean unbounded, State... states) {
		return new Set(unbounded,states);
	}
	
	public static Bag Bag(boolean unbounded, State... states) {
		return new Bag(unbounded,states);
	}
	
	public static List List(boolean unbounded, State... states) {
		return new List(unbounded,states);
	}
	
	public abstract static class State {
		
	}
	
	public abstract static class Constant extends State {
		
	}
	
	public static class Any extends Constant {
		private Any() {}
	}
	
	public static class Int extends Constant {
		private Int() {}
	}
	
	public static class Real extends Constant {
		private Real() {}
	}
	
	public static class Strung extends Constant {
		private Strung() {}
	}
	
	public static class Term extends State {
		public final String name;
		public final State child;
		
		private Term(String name, State contents) {
			this.name = name;
			this.child = contents;
		}
	}
	
	public static class Not {
		public final State child;
		
		private Not(State child) {
			this.child = child;
		}
	}
	
	public abstract static class Compound extends State {
		public final State[] children;

		private Compound(State... children) {
			this.children = children;
		}
	}
	
	public static class Or extends Compound {
		private Or(State... states) {
			super(states);
		}
	}
	
	public abstract static class Collection extends Compound {
		public final boolean unbounded;
		
		private Collection(boolean unbounded, State... states) {
			super(states);
			this.unbounded = unbounded;
		}
	}
	
	public static class Set extends Collection {
		private Set(boolean unbounded, State... states) {
			super(unbounded,states);
		}
	}
	
	public static class Bag extends Collection {
		private Bag(boolean unbounded, State... states) {
			super(unbounded,states);
		}
	}

	public static class List extends Collection {
		private List(boolean unbounded, State... states) {
			super(unbounded,states);
		}
	}
}
