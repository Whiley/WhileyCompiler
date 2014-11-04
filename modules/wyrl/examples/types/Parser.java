import java.util.ArrayList;

import wyautl.core.Automaton;

public class Parser {
	private String input;
	private int index;

	public Parser(String input) {
		this.input = input;
		this.index = 0;
	}

	public int parse(Automaton automaton) {

		int lhs = parseAndOr(automaton);
		skipWhiteSpace();

		if (index < input.length() && input.charAt(index) == ',') {
			ArrayList<Integer> elements = new ArrayList<Integer>();
			elements.add(lhs);
			while (index < input.length() && input.charAt(index) == ',') {
				match(",");
				elements.add(parseAndOr(automaton));
				skipWhiteSpace();
			}
			int[] es = new int[elements.size()];
			for (int i = 0; i != es.length; ++i) {
				es[i] = elements.get(i);
			}
			lhs = Types.Tuple(automaton, es);
		}

		return lhs;
	}

	public int parseAndOr(Automaton automaton) {

		int lhs = parseTerm(automaton);
		skipWhiteSpace();

		if(index < input.length()) {
			char lookahead = input.charAt(index);

			if(lookahead == '&') {
				match("&");
				int rhs = parseAndOr(automaton);
				lhs = Types.Intersect(automaton, lhs, rhs);
			} else if(lookahead == '|') {
				match("|");
				int rhs = parseAndOr(automaton);
				lhs = Types.Union(automaton, lhs, rhs);
			}
		}

		return lhs;
	}

	public int parseTerm(Automaton automaton) {
		skipWhiteSpace();
		char lookahead = input.charAt(index);

		if(lookahead == '(') {
			return parseBracketed(automaton);
		} else if(lookahead == '!') {
			match("!");
			return Types.Not(automaton, parseTerm(automaton));
		} else {
			String word = readWord();
			if(word.equals("int")) {
				return automaton.add(Types.Int);
			} else if(word.equals("any")) {
				return automaton.add(Types.Any);
			} else {
				throw new RuntimeException("unknown keyword: " + word);
			}
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
