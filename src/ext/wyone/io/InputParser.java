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

package wyone.io;

import java.io.*;
import java.util.*;
import java.math.*;

import wyil.jvm.rt.BigRational;
import wyil.util.SyntaxError;
import wyjc.stages.WhileyLexer.Int;
import wyjc.stages.WhileyLexer.Real;
import wyjc.stages.WhileyLexer.Token;
import wyone.core.*;

public class InputParser {
	private String filename;
	private String input;
	private int pos;
	private HashMap<String,Class> constructors = new HashMap<String,Class>();
	
	public InputParser(File file, Map<String,Class> constructors) throws IOException {		
		this.constructors = new HashMap<String,Class>(constructors);
		this.filename = file.getPath();
		BufferedReader in = new BufferedReader(new FileReader(filename));
		
		StringBuffer text = new StringBuffer();
		while (in.ready()) {
			text.append(in.readLine());
			text.append("\n");
		}
		input = text.toString();
		pos = 0;		 
	}
	
	public InputParser(String input, Map<String,Class> constructors) {		
		this.constructors = new HashMap<String,Class>(constructors);
		this.filename = "";		
		this.input = input;
		pos = 0;		
	}
	
	public Object parse() {		
		char c = input.charAt(pos);
		if(isOperatorStart(c)) {
			return parseOperator();
		} else if(Character.isDigit(c)) {
			return parseNumber();
		} else {
			return parseTerm();
		}
	}		
	
	private Object parseOperator() {
		char c = input.charAt(pos);
		if(c == '{') {
			return parseSet();
		} else {
			return parseNumber();
		}
	}
	
	public HashSet parseSet() {
		HashSet r = new HashSet();
		match("{");
		boolean firstTime=true;
		while(pos < input.length() && input.charAt(pos) != '}') {
			if(!firstTime) {
				match(",");
			}
			firstTime=false;
			r.add(parse());
		}
		match("}");
		return r;
	}
	
	public Number parseNumber() {		
		int start = pos;
		while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
			pos = pos + 1;
		}
		if(pos < input.length() && input.charAt(pos) == '.') {
			pos = pos + 1;			
			if(pos < input.length() && input.charAt(pos) == '.') {
				// this is case for range e.g. 0..1
				pos = pos - 1;
				return new BigInteger(input.substring(start, pos));				
			}
			while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
				pos = pos + 1;
			}			
			return new BigRational(input.substring(start, pos));			
		} else {
			return new BigInteger(input.substring(start, pos));					
		}		
	}
	
	public Object parseTerm() {
		String name = parseIdentifier();
		ArrayList args = new ArrayList();
		if(pos < input.length() && input.charAt(pos) == '(') {
			match("(");
			boolean firstTime=true;
			while(pos < input.length() && input.charAt(pos) != '(') {
				if(!firstTime) {
					match(",");
				}
				firstTime=false;
				args.add(parse());
			}
			match(")");
		}				
		
		Class clazz = constructors.get(name);
		if(clazz == null) {
			throw new RuntimeException("unrecognised term: " + name + args);
		} else {
			try {
				for(java.lang.reflect.Constructor c : clazz.getConstructors()) {
					return c.newInstance(args.toArray());
				}
			} catch(Exception e) {
				throw new RuntimeException("error constructing: " + name + args,e);
			}
			throw new RuntimeException("no constructor found: " + name + args);
		}
	}
	
	public String parseIdentifier() {
		int start = pos;		
		while (pos < input.length() &&
				Character.isJavaIdentifierPart(input.charAt(pos))) {
			pos++;								
		}		
		return input.substring(start,pos);
	}
	
	private void match(String x) {
		skipWhiteSpace();
		
		if((input.length()-pos) < x.length()) {
			throw new SyntaxError("expecting " + x,filename,pos,pos);
		}
		if(!input.substring(pos,pos+x.length()).equals(x)) {
			throw new SyntaxError("expecting " + x,filename,pos,pos);			
		}
		pos += x.length();
	}

	private String lookahead() {
		return lookahead(1);
	}
	
	private String lookahead(int k) {
		int end = pos;

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

	private static final char[] operators = { '(', ')',',','{','}','[',']'};
	
	private static boolean isOperatorStart(char c) {
		for(char op : operators) {
			if(c == op) { 
				return true;
			}
		}
		return false;
	}			
	
	private void skipWhiteSpace() {
		while (pos < input.length()
				&& Character.isWhitespace(input.charAt(pos))) {			
			pos = pos + 1;
		}
	}		
}
