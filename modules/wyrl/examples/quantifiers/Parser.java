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
		skipWhiteSpace();
		char lookahead = input.charAt(index);
		
		int lhs;
		
		if(lookahead == '(') {
			lhs = parseBracketed(automaton);			
		} else if(lookahead == '!') {
			match("!");
			return Quantifiers.Not(automaton, parse(automaton));	
		} else {
			String word = readWord();
			if(word.equals("true")) {
				lhs = automaton.add(Quantifiers.True);
			} else if(word.equals("false")) {
				lhs = automaton.add(Quantifiers.False);
			} else if(word.equals("forall")) {
				lhs = parseQuantifier(true,automaton);
			} else if(word.equals("exists")) {
				lhs = parseQuantifier(false,automaton);
			} else {
				lhs = parseVariableOrFunction(automaton,word);
			}
		}

		skipWhiteSpace();
		
		if(index < input.length()) {
			lookahead = input.charAt(index);
			
			if(lookahead == '&') {
				match("&&");
				int rhs = parse(automaton);
				lhs = Quantifiers.And(automaton, lhs, rhs);
			} else if(lookahead == '|') {
				match("||");
				int rhs = parse(automaton);
				lhs = Quantifiers.Or(automaton, lhs, rhs);				
			} else if(lookahead == '=') {
				match("==>");
				int rhs = parse(automaton);
				lhs = Quantifiers.Or(automaton, Quantifiers.Not(automaton, lhs), rhs);				
			} 
		}
		
		return lhs;
	}
	
	private int parseVariableOrFunction(Automaton automaton, String word) {
		// At this point, we either have a function or a variable.
		skipWhiteSpace();
		
		if(index < input.length() && input.charAt(index) == '(') {
			// Looks like a function application
			match("(");
			ArrayList<Integer> arguments = new ArrayList<Integer>();
			// first argument is actually the function name
			arguments.add(automaton.add(new Automaton.Strung(word)));			
			boolean firstTime = true;
			while(index < input.length() && input.charAt(index) != ')') {
				if(!firstTime) {
					match(",");
				}
				firstTime = false;				
				arguments.add(parse(automaton));
				skipWhiteSpace();
			}
			match(")");
			
			return Quantifiers.Fn(automaton,arguments);
		} else {
			// Looks like just a variable
			return Quantifiers.Var(automaton, word);
		}

	}
	
	private int parseQuantifier(boolean isUniversal, Automaton automaton) {		
		skipWhiteSpace();
		
		// Parse the variables
		ArrayList<Integer> variables = new ArrayList<Integer>();
		boolean firstTime = true;
		while(index < input.length() && input.charAt(index) != '.') {
			if(!firstTime) {
				match(",");
			}
			firstTime = false;
			String name = readWord();
			variables.add(Quantifiers.Var(automaton, name));
			skipWhiteSpace();
		}
		
		match(".");
		
		// Parse the expression
		int body = parse(automaton);
		int vars = automaton.add(new Automaton.Set(variables));
		
		if(isUniversal) {		
			return Quantifiers.ForAll(automaton, vars, body);
		} else {
			return Quantifiers.Exists(automaton, vars, body);
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
