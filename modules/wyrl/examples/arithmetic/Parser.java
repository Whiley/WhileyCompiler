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
				return Arithmetic.And(automaton,lhs,rhs);
			}
		}

		return lhs;
	}

	public int parseCondition(Automaton automaton) {
		skipWhiteSpace();
		char lookahead = input.charAt(index);

		int lhs = parseMulDiv(automaton);

		skipWhiteSpace();

		if(index < input.length()) {
			lookahead = input.charAt(index);

			if(lookahead == '<') {
				match("<");
				if(index < input.length() && input.charAt(index) == '=') {
					match("=");
					int rhs = parseCondition(automaton);
					return LessThanEq(automaton,lhs,rhs);
				} else {
					int rhs = parseCondition(automaton);
					return LessThan(automaton,lhs,rhs);
				}
			} else if(lookahead == '>') {
				match(">");
				if(index < input.length() && input.charAt(index) == '=') {
					match("=");
					int rhs = parseCondition(automaton);
					return LessThanEq(automaton,rhs,lhs);
				} else {
					int rhs = parseCondition(automaton);
					return LessThan(automaton,rhs,lhs);
				}
			} else if(lookahead == '=') {
				match("==");
				int rhs = parseCondition(automaton);
				return Arithmetic.And(automaton,LessThanEq(automaton,lhs,rhs),LessThanEq(automaton,rhs,lhs));
			} else if(lookahead == '!') {
				match("!=");
				int rhs = parseCondition(automaton);
				return Arithmetic.Or(automaton,LessThan(automaton,lhs,rhs),LessThan(automaton,rhs,lhs));
			}
		}

		return lhs;
	}

	public int parseMulDiv(Automaton automaton) {
		skipWhiteSpace();
		char lookahead = input.charAt(index);

		int lhs = parseAddSub(automaton);

		skipWhiteSpace();

		if(index < input.length()) {
			lookahead = input.charAt(index);

			if(lookahead == '*') {
				match("*");
				int rhs = parseMulDiv(automaton);
				return Mul(automaton,lhs,rhs);
			}
		}

		return lhs;
	}

	public int parseAddSub(Automaton automaton) {
		skipWhiteSpace();
		char lookahead = input.charAt(index);

		int lhs = parseTerm(automaton);

		skipWhiteSpace();

		if(index < input.length()) {
			lookahead = input.charAt(index);

			if(lookahead == '+') {
				match("+");
				int rhs = parseAddSub(automaton);
				return Add(automaton,lhs,rhs);
			} else if(lookahead == '-') {
				match("-");
				int rhs = parseAddSub(automaton);
				return Add(automaton,lhs,Neg(automaton,rhs));
			}
		}

		return lhs;
	}


	public int parseTerm(Automaton automaton) {
		skipWhiteSpace();
		char lookahead = input.charAt(index);

		if(lookahead == '(') {
			return parseBracketed(automaton);
		} else if(lookahead == '-') {
			match("-");
			int number = readNumber();
			return Arithmetic.Num(automaton, -number);
		} else if(Character.isDigit(lookahead)) {
			int number = readNumber();
			return Arithmetic.Num(automaton, number);
		} else {
			String word = readWord();
			return Arithmetic.Var(automaton, word);
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

	private int Add(Automaton automaton, int lhs, int rhs) {
		return Arithmetic.Sum(automaton,
				automaton.add(new Automaton.Real(0)),
				automaton.add(new Automaton.Bag(lhs, rhs)));
	}

	private int Neg(Automaton automaton, int lhs) {
		return Arithmetic.Mul(automaton, automaton.add(new Automaton.Real(-1)),
				automaton.add(new Automaton.Bag(lhs)));
	}

	private int Mul(Automaton automaton, int lhs, int rhs) {
		return Arithmetic.Mul(automaton,
				automaton.add(new Automaton.Real(1)),
				automaton.add(new Automaton.Bag(lhs, rhs)));
	}

	public int LessThan(Automaton automaton, int lhs,
			int rhs) {
		// lhs < rhs ==> lhs + 1 <= rhs ==> 0 < rhs -(lhs + 1)
		lhs = Add(automaton,lhs,Arithmetic.Num(automaton, 1));
		return Arithmetic.Inequality(automaton, Add(automaton, Neg(automaton, lhs), rhs));
	}

	public int LessThanEq(Automaton automaton, int lhs,
			int rhs) {
		return Arithmetic.Inequality(automaton, Add(automaton, Neg(automaton, lhs), rhs));
	}
}
