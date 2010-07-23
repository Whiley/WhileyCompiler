// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjc.util;

import wyil.util.SyntaxError;

public class ParseError extends SyntaxError {
	/**
	 * Identify a syntax error at a particular point in a file.
	 * 
	 * @param msg
	 *            Message detailing the problem.
	 * @param filename
	 *            The source file that this error is referring to.
	 * @param line
	 *            Line number within file containing problem.
	 * @param column
	 *            Column within line of file containing problem.
	 */
	public ParseError(String msg, String filename, int start, int end) {
		super(msg,filename,start,end);		
	}	
	
	/**
	 * Identify a syntax error at a particular point in a file.
	 * 
	 * @param msg
	 *            Message detailing the problem.
	 * @param filename
	 *            The source file that this error is referring to.
	 * @param line
	 *            Line number within file containing problem.
	 * @param column
	 *            Column within line of file containing problem.
	 */
	public ParseError(String msg, String filename, int start, int end, Throwable ex) {
		super(msg,filename,start,end,ex);			
	}
}
