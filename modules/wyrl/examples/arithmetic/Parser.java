import wyautl.core.Automaton;

public class Parser {
	private String input;
	private int index; 
	
	public Parser(String input) {
		this.input = input;
		this.index = 0;
	}
	
	public int parse(Automaton automaton) {
		skipWhiteSpace();
		char lookahead = input.charAt(index);
		
		int lhs;
		
		if(lookahead == '(') {
			lhs = parseBracketed(automaton);			
		} else if(Character.isDigit(lookahead)) {
			int number = readNumber();
			lhs = Arithmetic.Num(automaton, number);
		} else {
			String word = readWord();
			lhs = Arithmetic.Var(automaton, word);
		}

		skipWhiteSpace();
		
		if(index < input.length()) {
			lookahead = input.charAt(index);
			
			if(lookahead == '+') {
				match("+");
				int rhs = parse(automaton);
				lhs = Arithmetic.Sum(automaton,
						automaton.add(new Automaton.Real(0)),
						automaton.add(new Automaton.Bag(lhs, rhs)));
			} else if(lookahead == '-') {
				match("-");
				int rhs = parse(automaton);
				lhs = Arithmetic.Sum(automaton, automaton
						.add(new Automaton.Real(0)), automaton
						.add(new Automaton.Bag(lhs, Arithmetic.Mul(automaton,
								automaton.add(new Automaton.Real(-1)),
			automaton.add(new Automaton.Bag(rhs))))));		
			} else if(lookahead == '*') {
				match("*");
				int rhs = parse(automaton);
				lhs = Arithmetic.Mul(automaton,
						automaton.add(new Automaton.Real(1)),
						automaton.add(new Automaton.Bag(lhs, rhs)));		
			} 
		}
		
		return lhs;
	}
	
	private int parseBracketed(Automaton automaton) {
		match("(");
		int root = parse(automaton);
		match(")");
		return root;
	}
	
	private String readWord() {
		int start = index;
		while (index < input.length()
				&& Character.isLetter(input.charAt(index))) {
			index++;
		}
		return input.substring(start, index);
	}

	private int readNumber() {
		int start = index;
		while (index < input.length() && Character.isDigit(input.charAt(index))) {
			index = index + 1;
		}
		return Integer.parseInt(input.substring(start, index));
	}
		
	private void match(String text) {
		skipWhiteSpace();
		if(input.startsWith(text,index)) {
			index += text.length();
		} else {
			error();
		}
	}
	
	private void skipWhiteSpace() {		
		while (index < input.length()
				&& (input.charAt(index) == ' ' || input.charAt(index) == '\n')) {
			index = index + 1;
		}
	}	
	
	private void error() {
		final String msg = "Cannot parse character '"
			+ input.charAt(index)
		    + "' at position " + index + " of input '" + input + "'\n";
		throw new RuntimeException(msg);		
	}
}
