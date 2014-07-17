import wyautl.core.Automaton;

public class Quantifiers$native {
	
	public static Automaton.Term bind(Automaton automaton, Automaton.List args) {
		// FIXME: this is just a dummy for now
		return (Automaton.Term) automaton.get(args.get(2));
	}
}
