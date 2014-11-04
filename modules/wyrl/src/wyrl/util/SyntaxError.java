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

package wyrl.util;

import java.io.File;

import wyrl.core.Attribute;

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
	 * @param file
	 *            The source file that this error is referring to.
	 * @param line
	 *            Line number within file containing problem.
	 * @param column
	 *            Column within line of file containing problem.
	 */
	public SyntaxError(String msg, File file, int start, int end) {
		this.msg = msg;
		this.filename = file.getPath();
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

	public static final long serialVersionUID = 1l;


	public static void syntaxError(String msg, File file, SyntacticElement elem) {
		syntaxError(msg,file.getPath(),elem);
	}

	public static void syntaxError(String msg, File file, SyntacticElement elem, Throwable ex) {
		syntaxError(msg,file.getPath(),elem,ex);
	}

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
