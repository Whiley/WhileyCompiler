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

/**
 * This exception is thrown when a syntax error occurs in the parser. 
 * 
 * @author djp
 * 
 */
public final class SyntaxError extends RuntimeException {
	private String msg;
	private String filename;
	private int start;
	private int end;		
	
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
	public SyntaxError(String msg, String filename, int start, int end) {
		this.msg = msg;
		this.filename = filename;
		this.start=start;		
		this.end=end;		
	}	
	
	public String getMessage() {
		if(msg != null) {
			return msg;
		} else {
			return "";
		}
	}
	
	/**
	 * Error message
	 * @return
	 */
	public String msg() { return msg; }	
	
	/**
	 * Filename for file where the error arose.
	 * @return
	 */
	public String filename() { return filename; }	
	
	/**
	 * Get index of first character of offending location.
	 * @return
	 */
	public int start() { return start; }	
	
	/**
	 * Get index of last character of offending location.
	 * @return
	 */
	public int end() { return end; }
	
	public static final long serialVersionUID = 1l;
}
