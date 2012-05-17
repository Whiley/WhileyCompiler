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

import wyone.core.*;
import wyone.theory.list.*;
import wyone.theory.set.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.quantifier.*;
import wyone.theory.tuple.*;
import wyone.theory.type.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;
import static wyone.theory.set.WSets.*;

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
		
	public WFormula parseInput() {
		return parseFormula();		
	}	
	
	private WFormula parseFormula() {
		return parseConjunctDisjunct();
	}
	
	private WFormula parseConjunctDisjunct() {
		WFormula c1 = parsePredicate();
		
		parseWhiteSpace();				 
		
		if(index < input.length() && input.charAt(index) == '&') {
			match("&&");
			WFormula c2 = parseConjunctDisjunct();			
			return and(c1,c2);
		} else if(index < input.length() && input.charAt(index) == '|') {
			match("||");
			WFormula c2 = parseConjunctDisjunct();			
			return or(c1,c2);
		}
		return c1;
	}
		
	private WFormula parsePredicate() {
		parseWhiteSpace();
		
		int start = index;
		
		String lookahead = lookahead();
		
		if(lookahead.equals("(")) {
			match("(");			
			WFormula r = parseConjunctDisjunct();			
			match(")");
			return r;
		} else if(lookahead.equals("!")) {
			match("!");			
			return not(parsePredicate());			
		} else if(lookahead.equals("all")) {						
			match("all");					
			match("[");
						
			parseWhiteSpace();
			HashMap<WVariable,WExpr> vars = new HashMap<WVariable,WExpr>();			
			boolean firstTime=true;
			while(!lookahead().equals("|")) {		
				if(!firstTime) {
					match(",");
					parseWhiteSpace();
				}
				firstTime=false;
				String v = parseIdentifier();				
				match(":");				
				WExpr src = parseExpression();				
				WVariable var = new WVariable(v); 
				vars.put(var,src);
				// type is ignored for now								
				parseWhiteSpace();
			}
			match("|");
			WFormula f = parseConjunctDisjunct();			
			match("]");
			return new WBoundedForall(true,vars,f);						
		} else if(lookahead.equals("some")) {			
			match("some");			
			match("[");			
			parseWhiteSpace();
			HashMap<WVariable,WExpr> vars = new HashMap<WVariable,WExpr>();			
			boolean firstTime=true;
			while(!lookahead().equals("|")) {		
				if(!firstTime) {
					match(",");
					parseWhiteSpace();
				}
				firstTime=false;
				String v = parseIdentifier();
				match(":");				
				WExpr src = parseExpression();				
				WVariable var = new WVariable(v); 
				vars.put(var,src);
				// type is ignored for now								
				parseWhiteSpace();
			}
			match("|");
			WFormula f = parseConjunctDisjunct();			
			match("]");
			return new WBoundedForall(false,vars,f);							
		} 
			
		WExpr lhs = parseExpression();
		
		parseWhiteSpace();				 
		
		if ((index + 1) < input.length() && input.charAt(index) == '<'
				&& input.charAt(index + 1) == ':') {
			match("<:");
			WType rhs = parseType();
			return WTypes.subtypeOf(lhs, rhs);
		} if ((index + 2) < input.length() && input.charAt(index) == '<'
				&& input.charAt(index + 1) == '!' && input.charAt(index + 2) == ':') {
			match("<!:");
			WType rhs = parseType();
			return WTypes.subtypeOf(lhs, rhs).not();
		} else if ((index + 1) < input.length() && input.charAt(index) == '<'
				&& input.charAt(index + 1) == '=') {
			match("<=");
			WExpr rhs = parseExpression();			
			return lessThanEq(lhs, rhs);
		} else if (index < input.length() && input.charAt(index) == '<') {
			match("<");
			WExpr rhs = parseExpression();
			return lessThan(lhs,rhs);			
		} else if ((index + 1) < input.length() && input.charAt(index) == '>'
				&& input.charAt(index + 1) == '=') {
			match(">=");
			WExpr rhs = parseExpression();
			return greaterThanEq(lhs,rhs);
		} else if (index < input.length() && input.charAt(index) == '>') {
			match(">");
			WExpr rhs = parseExpression();
			return greaterThan(lhs,rhs);			
		} else if ((index + 1) < input.length() && input.charAt(index) == '='
				&& input.charAt(index + 1) == '=') {
			match("==");
			WExpr rhs = parseExpression();			
			return WExprs.equals(lhs,rhs);
		} else if ((index + 1) < input.length() && input.charAt(index) == '!'
				&& input.charAt(index + 1) == '=') {
			match("!=");
			WExpr rhs = parseExpression();
			return WExprs.notEquals(lhs, rhs);			
		} else if ((index + 1) < input.length() && input.charAt(index) == '{'
				&& input.charAt(index + 1) == '=') {
			match("{=");
			WExpr rhs = parseExpression();			
			return subsetEq(lhs, rhs);
		} else if ((index + 1) < input.length() && input.charAt(index) == '='
				&& input.charAt(index + 1) == '}') {
			match("=}");
			WExpr rhs = parseExpression();			
			return supsetEq(lhs, rhs);
		} else if ((index + 2) < input.length() && input.charAt(index) == '{'
				&& input.charAt(index + 1) == '!') {
			match("{!=");
			WExpr rhs = parseExpression();			
			return subsetEq(lhs, rhs).not();
		} else if ((index + 2) < input.length() && input.charAt(index) == '!'
				&& input.charAt(index + 1) == '}') {
			match("=!}");
			WExpr rhs = parseExpression();			
			return supsetEq(lhs, rhs).not();
		} else if(lhs instanceof WVariable) {
			WVariable v = (WVariable) lhs;
			return new WPredicate(true,v.name(),v.subterms());
		} else {
			// will need more here
			throw new SyntaxError("syntax error", filename, start,
						index - 1);			
		} 					
	}
	
	private WExpr parseExpression( ) {				
		WExpr lhs = parseMulDivExpression();
		
		parseWhiteSpace();				 
		
		while (index < input.length()
				&& (input.charAt(index) == '-' || input.charAt(index) == '+')) {
			boolean add = input.charAt(index) == '+';
			if (add) {
				match("+");
			} else {
				match("-");
			}

			WExpr rhs = parseMulDivExpression();
			if (add) {
				lhs = add(lhs, rhs);
			} else {
				lhs = subtract(lhs, rhs);
			}
		}
		
		return lhs;
	}
	
	private WExpr parseMulDivExpression( ) {
		int start = index;
		WExpr lhs = parseIndexTerm();
		parseWhiteSpace();
		
		if(index < input.length() && input.charAt(index) == '*') {
			match("*");
			WExpr rhs = parseExpression();
			return multiply(lhs,rhs);			
		} else if(index < input.length() && input.charAt(index) == '/') {
			throw new SyntaxError("Support for divide is lacking", filename,
					start, index - 1);
		} else {
			return lhs;
		}
	}
	
	private WExpr parseIndexTerm() {
		WExpr term = parseTerm();
		String lookahead = lookahead();
		while(lookahead.equals(".") || lookahead.equals("[")) {				
			if(lookahead.equals(".")) {
				match(".");
				String field = parseIdentifier();
				term = new WTupleAccess(term,field);
			} else {
				match("[");										
				WExpr index = parseExpression();									
				match("]");
				term = new WListAccess(term,index);	
			}
			lookahead = lookahead();
		}
		
		return term;		
	}
	
	private WExpr parseTerm() {
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
	
	private WExpr parseLengthTerm() {
		match("|");
		WExpr r = parseExpression();
		match("|");		
		return new WLengthOf(r);
	}
	
	private WExpr parseListTerm() {
		match("[");
		ArrayList<WExpr> params = new ArrayList<WExpr>();
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
	

	private WExpr parseSetTerm() {		
		match("{");
		HashSet<WExpr> params = new HashSet<WExpr>();
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
	
	private WExpr parseIdentifierTerm() {
		int start = index;		
		String v = parseIdentifier();
		
		ArrayList<WExpr> params = null;
		
		if(lookahead().equals("(")) {
			// this indicates a function application
			match("(");
			params = new ArrayList<WExpr>();
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
			return  new WVariable(v);
		} else {
			return new WVariable(v,params);
		}					
	}
	
	private WExpr parseBracketedTerm() {
		match("(");
		WExpr e;					
		if(lookahead(2).equals("=")) {				
			// this indicates a tuple constructor
			ArrayList<String> fields = new ArrayList();
			ArrayList<WExpr> items = new ArrayList();
			boolean firstTime = true;
			while(!lookahead().equals(")")) {
				if(!firstTime) {
					match(",");						
				}
				firstTime = false;
				String id = parseIdentifier();				
				match("=");
				WExpr val = parseExpression();
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
	
	private WExpr parseNegation() {		
		match("-");
		WExpr e = parseIndexTerm();		
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
	
	private WExpr parseNumber() {
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
			return new WNumber(new BigInteger(lhs),bottom);
		} else {
			return new WNumber(new BigInteger(input.substring(start, index)));
		}
	}
	
	private WType parseType() {
		parseWhiteSpace();
		
		int start = index;
		
		if(input.charAt(index) == '?') {
			match("?");
			return WAnyType.T_ANY;
		} else if(input.charAt(index) == '{') {
			match("{");
			WType et = parseType();
			match("}");
			return new WSetType(et);
		} else if(input.charAt(index) == '[') {
			match("[");
			WType et = parseType();
			match("]");
			return new WListType(et);
		} else if(input.charAt(index) == '(') {
			match("(");
			boolean firstTime=true;
			ArrayList<Pair<String,WType>> types = new ArrayList();
			do {
				if(!firstTime) {
					match(",");
				}
				firstTime=false;
				WType type = parseType();
				parseWhiteSpace();
				String field = parseIdentifier();
				types.add(new Pair(field,type));
			} while (index < input.length() && input.charAt(index) == ',');		
			match(")");
			return new WTupleType(types);
		} else {
			String id = parseIdentifier();
			WType r;
			if(id.equals("void")) {
				r = WIntType.T_INT;
			} else if(id.equals("int")) {
				r = WIntType.T_INT;
			} else if(id.equals("real")) {
				r = WRealType.T_REAL;
			} else if(id.equals("bool")) {
				r = WBoolType.T_BOOL;
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
