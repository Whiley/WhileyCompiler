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

import wyil.util.SyntaxError;
import wyone.core.*;

public class InputParser {
	private String filename;
	private String input;
	private int index;
	
	public InputParser(File file) throws IOException {		
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
	
	public InputParser(String input) {		
		this.filename = "";		
		this.input = input;
		index = 0;		
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
