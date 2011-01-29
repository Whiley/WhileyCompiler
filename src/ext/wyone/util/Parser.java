// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.util;

import java.io.*;
import java.util.*;
import java.math.*;

import wyil.lang.Type;
import wyone.core.*;
import wyone.theory.logic.*;
// import wyone.theory.list.*;
// import wyone.theory.set.*;
import wyone.theory.numeric.*;
// import wyone.theory.quantifier.*;
// import wyone.theory.tuple.*;
import static wyone.core.Constructor.*;
import static wyone.theory.logic.Logic.*;
import static wyone.theory.numeric.Numerics.*;
import static wyone.theory.congruence.Equality.*;
// import static wyone.theory.set.WSets.*;

public class Parser {
	private String filename;
	private String input;
	private int index;
	
	public Parser(File file) throws IOException {		
		this.filename = file.getPath();
		BufferedReader in = new BufferedReader(new FileReader(filename));
		
		StringBuffer text = new StringBuffer();
		while (in.ready()) {
			text.append(in.readLine());
			text.append("\n");
		}
		input = text.toString();
		index = 0;		
	}
	
	public Parser(String input) {		
		this.filename = "";		
		this.input = input;
		index = 0;		
	}
		
	public Constraint parseInput() {
		return parseFormula();		
	}	
	
	private Constraint parseFormula() {
		return parseConjunctDisjunct();
	}
	
	private Constraint parseConjunctDisjunct() {
		Constraint c1 = parsePredicate();
		
		parseWhiteSpace();				 
		
		if(index < input.length() && input.charAt(index) == '&') {
			match("&&");
			Constraint c2 = parseConjunctDisjunct();			
			return and(c1,c2);
		} else if(index < input.length() && input.charAt(index) == '|') {
			match("||");
			Constraint c2 = parseConjunctDisjunct();			
			return or(c1,c2);
		}
		return c1;
	}
		
	private Constraint parsePredicate() {
		parseWhiteSpace();
		
		int start = index;
		
		String lookahead = lookahead();
		
		if(lookahead.equals("(")) {
			match("(");			
			Constraint r = parseConjunctDisjunct();			
			match(")");
			return r;
		} else if(lookahead.equals("!")) {
			match("!");			
			return not(parsePredicate());			
		} else if(lookahead.equals("all")) {						
			match("all");					
			match("[");
						
			parseWhiteSpace();
			HashMap<Variable,Constructor> vars = new HashMap<Variable,Constructor>();			
			boolean firstTime=true;
			while(!lookahead().equals("|")) {		
				if(!firstTime) {
					match(",");
					parseWhiteSpace();
				}
				firstTime=false;
				String v = parseIdentifier();				
				match(":");				
				Constructor src = parseExpression();				
				Variable var = new Variable(v); 
				vars.put(var,src);
				// type is ignored for now								
				parseWhiteSpace();
			}
			match("|");
			Constraint f = parseConjunctDisjunct();			
			match("]");
			return new WBoundedForall(true,vars,f);						
		} else if(lookahead.equals("some")) {			
			match("some");			
			match("[");			
			parseWhiteSpace();
			HashMap<Variable,Constructor> vars = new HashMap<Variable,Constructor>();			
			boolean firstTime=true;
			while(!lookahead().equals("|")) {		
				if(!firstTime) {
					match(",");
					parseWhiteSpace();
				}
				firstTime=false;
				String v = parseIdentifier();
				match(":");				
				Constructor src = parseExpression();				
				Variable var = new Variable(v); 
				vars.put(var,src);
				// type is ignored for now								
				parseWhiteSpace();
			}
			match("|");
			Constraint f = parseConjunctDisjunct();			
			match("]");
			return new WBoundedForall(false,vars,f);							
		} 
			
		Constructor lhs = parseExpression();
		
		parseWhiteSpace();				 
		
		if ((index + 1) < input.length() && input.charAt(index) == '<'
				&& input.charAt(index + 1) == ':') {
			match("<:");
			Type rhs = parseType();
			return new Subtype(true,rhs,lhs);
		} if ((index + 2) < input.length() && input.charAt(index) == '<'
				&& input.charAt(index + 1) == '!' && input.charAt(index + 2) == ':') {
			match("<!:");
			Type rhs = parseType();
			return new Subtype(false,rhs,lhs);
		} else if ((index + 1) < input.length() && input.charAt(index) == '<'
				&& input.charAt(index + 1) == '=') {
			match("<=");
			Constructor rhs = parseExpression();			
			return lessThanEq(lhs, rhs);
		} else if (index < input.length() && input.charAt(index) == '<') {
			match("<");
			Constructor rhs = parseExpression();
			return lessThan(lhs,rhs);			
		} else if ((index + 1) < input.length() && input.charAt(index) == '>'
				&& input.charAt(index + 1) == '=') {
			match(">=");
			Constructor rhs = parseExpression();
			return greaterThanEq(lhs,rhs);
		} else if (index < input.length() && input.charAt(index) == '>') {
			match(">");
			Constructor rhs = parseExpression();
			return greaterThan(lhs,rhs);			
		} else if ((index + 1) < input.length() && input.charAt(index) == '='
				&& input.charAt(index + 1) == '=') {
			match("==");
			Constructor rhs = parseExpression();			
			return equals(lhs,rhs);
		} else if ((index + 1) < input.length() && input.charAt(index) == '!'
				&& input.charAt(index + 1) == '=') {
			match("!=");
			Constructor rhs = parseExpression();
			return notEquals(lhs, rhs);			
		} else if ((index + 1) < input.length() && input.charAt(index) == '{'
				&& input.charAt(index + 1) == '=') {
			match("{=");
			Constructor rhs = parseExpression();			
			return subsetEq(lhs, rhs);
		} else if ((index + 1) < input.length() && input.charAt(index) == '='
				&& input.charAt(index + 1) == '}') {
			match("=}");
			Constructor rhs = parseExpression();			
			return supsetEq(lhs, rhs);
		} else if ((index + 2) < input.length() && input.charAt(index) == '{'
				&& input.charAt(index + 1) == '!') {
			match("{!=");
			Constructor rhs = parseExpression();			
			return subsetEq(lhs, rhs).not();
		} else if ((index + 2) < input.length() && input.charAt(index) == '!'
				&& input.charAt(index + 1) == '}') {
			match("=!}");
			Constructor rhs = parseExpression();			
			return supsetEq(lhs, rhs).not();
		} else if(lhs instanceof Variable) {
			Variable v = (Variable) lhs;
			return new WPredicate(true,v.name(),v.subterms());
		} else {
			// will need more here
			throw new SyntaxError("syntax error", filename, start,
						index - 1);			
		} 					
	}
	
	private Constructor parseExpression( ) {				
		Constructor lhs = parseMulDivExpression();
		
		parseWhiteSpace();				 
		
		while (index < input.length()
				&& (input.charAt(index) == '-' || input.charAt(index) == '+')) {
			boolean add = input.charAt(index) == '+';
			if (add) {
				match("+");
			} else {
				match("-");
			}

			Constructor rhs = parseMulDivExpression();
			if (add) {
				lhs = add(lhs, rhs);
			} else {
				lhs = subtract(lhs, rhs);
			}
		}
		
		return lhs;
	}
	
	private Constructor parseMulDivExpression( ) {
		int start = index;
		Constructor lhs = parseIndexTerm();
		parseWhiteSpace();
		
		if(index < input.length() && input.charAt(index) == '*') {
			match("*");
			Constructor rhs = parseExpression();
			return multiply(lhs,rhs);			
		} else if(index < input.length() && input.charAt(index) == '/') {
			throw new SyntaxError("Support for divide is lacking", filename,
					start, index - 1);
		} else {
			return lhs;
		}
	}
	
	private Constructor parseIndexTerm() {
		Constructor term = parseTerm();
		String lookahead = lookahead();
		while(lookahead.equals(".") || lookahead.equals("[")) {				
			if(lookahead.equals(".")) {
				match(".");
				String field = parseIdentifier();
				term = new WTupleAccess(term,field);
			} else {
				match("[");										
				Constructor index = parseExpression();									
				match("]");
				term = new WListAccess(term,index);	
			}
			lookahead = lookahead();
		}
		
		return term;		
	}
	
	private Constructor parseTerm() {
		parseWhiteSpace();
		
		int start = index;		
		if(index >= input.length()) {
			throw new SyntaxError("syntax error", filename, start, index);
		}
		char c = input.charAt(index);
		if (c == '(') {			
			return parseBracketedTerm();			
		} else if (c == '[') {			
			return parseListTerm();			
		} else if (c == '{') {			
			return parseSetTerm();			
		} else if (c == '|') {			
			return parseLengthTerm();			
		} else if (index < input.length()
				&& (Character.isJavaIdentifierStart(c) || c == '%' || c == '$' || c == ':')) {
			return parseIdentifierTerm();			
		} else if (Character.isDigit(c)) {
			return parseNumber();
		} else if (c == '-') {
			return parseNegation();
		} else {
			throw new SyntaxError("syntax error", filename, start, index);
		}
	}
	
	private Constructor parseLengthTerm() {
		match("|");
		Constructor r = parseExpression();
		match("|");		
		return new WLengthOf(r);
	}
	
	private Constructor parseListTerm() {
		match("[");
		ArrayList<Constructor> params = new ArrayList<Constructor>();
		String lookahead = lookahead();
		boolean firstTime = true;
		while(!lookahead.equals("]")) {
			if(!firstTime) {
				match(",");
				lookahead = lookahead();
			}
			firstTime=false;							
			params.add(parseExpression());
			lookahead = lookahead();
		}
		match("]");
		return new WListConstructor(params).substitute(Collections.EMPTY_MAP);
	}
	

	private Constructor parseSetTerm() {		
		match("{");
		HashSet<Constructor> params = new HashSet<Constructor>();
		String lookahead = lookahead();
		boolean firstTime = true;
		while(!lookahead.equals("}")) {
			if(!firstTime) {
				match(",");
				lookahead = lookahead();
			}
			firstTime=false;							
			params.add(parseExpression());
			lookahead = lookahead();
		}		
		match("}");		
		return new WSetConstructor(params).substitute(Collections.EMPTY_MAP);
	}
	
	private Constructor parseIdentifierTerm() {
		int start = index;		
		String v = parseIdentifier();
		
		ArrayList<Constructor> params = null;
		
		if(lookahead().equals("(")) {
			// this indicates a function application
			match("(");
			params = new ArrayList<Constructor>();
			String lookahead = lookahead();
			boolean firstTime = true;
			while(!lookahead.equals(")")) {
				if(!firstTime) {
					match(",");
					lookahead = lookahead();
				}
				firstTime=false;							
				params.add(parseExpression());
				lookahead = lookahead();
			}
			match(")");								
		} 
				
		if(params == null) {
			return  new Variable(v);
		} else {
			return new Variable(v,params);
		}					
	}
	
	private Constructor parseBracketedTerm() {
		match("(");
		Constructor e;					
		if(lookahead(2).equals("=")) {				
			// this indicates a tuple constructor
			ArrayList<String> fields = new ArrayList();
			ArrayList<Constructor> items = new ArrayList();
			boolean firstTime = true;
			while(!lookahead().equals(")")) {
				if(!firstTime) {
					match(",");						
				}
				firstTime = false;
				String id = parseIdentifier();				
				match("=");
				Constructor val = parseExpression();
				fields.add(id);
				items.add(val);					
			}
			e = new WTupleConstructor(fields,items);				
		} else {
			// normal bracketed expression
			e = parseExpression();				
		}
		
		match(")");
		return e;
	}
	
	private Constructor parseNegation() {		
		match("-");
		Constructor e = parseIndexTerm();		
		return negate(e);
	}
	
	private String parseIdentifier() {		
		String id = Character.toString(input.charAt(index));
		index++; // skip past the identifier start
		if(index == input.length()) {
			return id;
		}
		char c = input.charAt(index);
		while (Character.isJavaIdentifierPart(c)
				|| c == '%' || c == '$' || c == ':') {
			id += Character.toString(input.charAt(index));
			index = index + 1;
			if(index < input.length()) {
				c = input.charAt(index);
			} else {
				break;
			}
		}
		return id;
	}
	
	private Constructor parseNumber() {
		int start = index;
		while (index < input.length() && Character.isDigit(input.charAt(index))) {
			index = index + 1;
		}
		if(index < input.length() && input.charAt(index) == '.') {
			index = index + 1;
			int start2 = index;
			while (index < input.length() && Character.isDigit(input.charAt(index))) {
				index = index + 1;
			}
			String lhs = input.substring(start, start2-1);
			String rhs = input.substring(start2, index);
			lhs = lhs + rhs;
			BigInteger bottom = BigInteger.valueOf(10);
			bottom = bottom.pow(rhs.length());
			return Value.V_NUM(new BigInteger(lhs),bottom);
		} else {
			return Value.V_NUM(new BigInteger(input.substring(start, index)));
		}
	}
	
	private Type parseType() {
		parseWhiteSpace();
		
		int start = index;
		
		if(input.charAt(index) == '?') {
			match("?");
			return Type.T_ANY;
		} else if(input.charAt(index) == '{') {
			match("{");
			Type et = parseType();
			match("}");
			return Type.T_SET(et);
		} else if(input.charAt(index) == '[') {
			match("[");
			Type et = parseType();
			match("]");
			return Type.T_LIST(et);
		} else if(input.charAt(index) == '(') {
			match("(");
			boolean firstTime=true;
			Map<String,Type> types = new HashMap();
			do {
				if(!firstTime) {
					match(",");
				}
				firstTime=false;
				Type type = parseType();
				parseWhiteSpace();
				String field = parseIdentifier();
				types.put(field,type);
			} while (index < input.length() && input.charAt(index) == ',');		
			match(")");
			return Type.T_RECORD(types);
		} else {
			String id = parseIdentifier();
			Type r;
			// FIXME: this needs improving
			if(id.equals("void")) {
				r = Type.T_INT;
			} else if(id.equals("int")) {
				r = Type.T_INT;
			} else if(id.equals("real")) {
				r = Type.T_REAL;
			} else if(id.equals("bool")) {
				r = Type.T_BOOL;
			} else {
				throw new SyntaxError("unknown type", filename, start,
						index - 1);				
			}
		
			return r;
		}		
	}
	
	private void match(String x) {
		parseWhiteSpace();
		
		if((input.length()-index) < x.length()) {
			throw new SyntaxError("expecting " + x,filename,index,index);
		}
		if(!input.substring(index,index+x.length()).equals(x)) {
			throw new SyntaxError("expecting " + x,filename,index,index);			
		}
		index += x.length();
	}

	private String lookahead() {
		return lookahead(1);
	}
	
	private String lookahead(int k) {
		int end = index;

		for (int i = 1; i != k; ++i) {
			// first, skip whitespace
			while (end < input.length()
					&& Character.isWhitespace(input.charAt(end))) {
				end = end + 1;
			}
			// skip this token
			end += lookaheadFrom(end).length();
		}

		return lookaheadFrom(end);
	}
	
	private String lookaheadFrom(int start) {		
		StringBuilder str = new StringBuilder();
		int end = start;
	
		while (end < input.length()
				&& Character.isWhitespace(input.charAt(end))) {
			end = end + 1;
		}					

		if (end < input.length() && isOperatorStart(input.charAt(end))) {
			str = str.append(input.charAt(end));
		} else {
			while (end < input.length()
					&& !Character.isWhitespace(input.charAt(end))
					&& !isOperatorStart(input.charAt(end))) {
				str = str.append(input.charAt(end));
				end = end + 1;
			}
		}	
		return str.toString();
	}	

	private static final char[] operators = { '+', '-', '*', '/', '(', ')',
			'<', '>', '}', '{', ';', '[', ']', '|', '!', '=', ',', '.', ':'};
	
	private static boolean isOperatorStart(char c) {
		for(char op : operators) {
			if(c == op) { 
				return true;
			}
		}
		return false;
	}			
	
	private void parseWhiteSpace() {
		while (index < input.length()
				&& Character.isWhitespace(input.charAt(index))) {			
			index = index + 1;
		}
	}		
}
