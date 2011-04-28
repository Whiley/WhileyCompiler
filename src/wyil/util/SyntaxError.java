// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import wyil.lang.Attribute;

/**
 * This exception is thrown when a syntax error occurs in the parser. 
 * 
 * @author djp
 * 
 */
public class SyntaxError extends RuntimeException {
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
	public SyntaxError(String msg, String filename, int start, int end, Throwable ex) {
		super(ex);
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

	/**
	 * Output the syntax error to a given output stream.
	 */
	public void outputSourceError(PrintStream output) {
		if(filename == null) {
			output.println("syntax error: " + getMessage());
		} else {
			int line = 0;
			String lineText = "";

			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(filename), "UTF8"));

				while (in.ready() && start >= lineText.length()) {
					start -= lineText.length() + 1;
					end -= lineText.length() + 1;
					lineText = in.readLine();
					line = line + 1;
				}
			} catch (IOException e) {
				output.println("syntax error: " + getMessage());
				return;
			}

			output.println(filename + ":" + line + ": " + getMessage());
			// errout.println();
			output.println(lineText);

			for (int i = 0; i <= start; ++i) {
				if (lineText.charAt(i) == '\t') {
					output.print("\t");
				} else {
					output.print(" ");
				}
			}
			for (int i = start; i <= end; ++i) {
				output.print("^");
			}
			output.println("");
		} 
	}
	
	public static final long serialVersionUID = 1l;
	

	public static void syntaxError(String msg, String filename, SyntacticElement elem) {
		int start = -1;
		int end = -1;
		
		Attribute.Source attr = (Attribute.Source) elem.attribute(Attribute.Source.class);
		if(attr != null) {
			start=attr.start;
			end=attr.end;			
		}
		
		throw new SyntaxError(msg, filename, start, end);
	}

	public static void syntaxError(String msg, String filename,
			SyntacticElement elem, Throwable ex) {
		int start = -1;
		int end = -1;		
		
		Attribute.Source attr = (Attribute.Source) elem.attribute(Attribute.Source.class);
		if(attr != null) {
			start=attr.start;
			end=attr.end;			
		}
		
		throw new SyntaxError(msg, filename, start, end, ex);
	}
	
	
}
