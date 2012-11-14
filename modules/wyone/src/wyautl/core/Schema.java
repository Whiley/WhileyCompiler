package wyautl.core;

/**
 * Provides a simple mechanism for validating that a given automaton is well-formed.
 * 
 * @author David J. Pearfe
 * 
 */
public class Schema {
	private Term[] states;
	
	public boolean validate(Automaton automaton) {
		for(int i=0;i!=automaton.nStates();++i) {
			Automaton.State state = automaton.get(i);
			if(state instanceof Automaton.Term) { 
				validate((Automaton.Term) state,automaton);
			}
		}
	}
	
	public static final Any Any = new Any();
	
	public static final Int Int = new Int();
	
	public static final Real Real = new Real();
	
	public static final Strung String = new Strung();
	
	public static Term Term(String name, State contents) {
		return new Term(name,contents);
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
	
	public abstract static class Constant {
		
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
	
	public static class Term {
		public final String name;
		public final State contents;
		
		private Term(String name, State contents) {
			this.name = name;
			this.contents = contents;
		}
	}
	
	public abstract static class Collection extends State {
		public final State[] states;
		public final boolean unbounded;
		
		private Collection(boolean unbounded, State... states) {
			this.unbounded = unbounded;
			this.states = states;
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
