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

		int lhs = parseCondition(automaton);

		skipWhiteSpace();

		if(index < input.length()) {
			lookahead = input.charAt(index);

			if(lookahead == ',') {
				match(",");
				int rhs = parse(automaton);
				return Closure.And(automaton,lhs,rhs);
			}
		}

		return lhs;
	}

	public int parseCondition(Automaton automaton) {
		skipWhiteSpace();
		char lookahead = input.charAt(index);

		int lhs = parseTerm(automaton);

		skipWhiteSpace();

		if(index < input.length()) {
			lookahead = input.charAt(index);

			if(lookahead == '<') {
				match("<");
			      	int rhs = parseCondition(automaton);
				return Closure.LessThan(automaton,lhs,rhs);
			} else if(lookahead == '>') {
				match(">");
				int rhs = parseCondition(automaton);
				return Closure.LessThan(automaton,rhs,lhs);
			}
		}

		return lhs;
	}

	public int parseTerm(Automaton automaton) {
		skipWhiteSpace();
		char lookahead = input.charAt(index);

		if(lookahead == '(') {
			return parseBracketed(automaton);
		} else if(Character.isDigit(lookahead)) {
			int number = readNumber();
			return Closure.Num(automaton, number);
		} else {
			String word = readWord();
			return Closure.Var(automaton, word);
		}
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
