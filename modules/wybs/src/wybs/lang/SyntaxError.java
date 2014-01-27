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

package wybs.lang;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This exception is thrown when a syntax error occurs in the parser.
 * 
 * @author David J. Pearce
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
		this.start = start;
		this.end = end;
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
	public SyntaxError(String msg, String filename, int start, int end,
			Throwable ex) {
		super(ex);
		this.msg = msg;
		this.filename = filename;
		this.start = start;
		this.end = end;
	}

	public String getMessage() {
		if (msg != null) {
			return msg;
		} else {
			return "";
		}
	}

	/**
	 * Error message
	 * 
	 * @return
	 */
	public String msg() {
		return msg;
	}

	/**
	 * Filename for file where the error arose.
	 * 
	 * @return
	 */
	public String filename() {
		return filename;
	}

	/**
	 * Get index of first character of offending location.
	 * 
	 * @return
	 */
	public int start() {
		return start;
	}

	/**
	 * Get index of last character of offending location.
	 * 
	 * @return
	 */
	public int end() {
		return end;
	}

	/**
	 * Output the syntax error to a given output stream in full form. In full
	 * form, contextual information from the originating source file is
	 * included.
	 */
	public void outputSourceError(PrintStream output) {
		outputSourceError(output,true);
	}
	
	/**
	 * Output the syntax error to a given output stream in either full or brief
	 * form. Brief form is intended to be used by 3rd party tools and is easier
	 * to parse. In full form, contextual information from the originating
	 * source file is included.
	 */
	public void outputSourceError(PrintStream output, boolean brief) {
		if (filename == null) {
			output.println("syntax error: " + getMessage());
		} else {
			int line = 0;
			int lineStart = 0;
			int lineEnd = 0;
			StringBuilder text = new StringBuilder();
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(filename), "UTF-8"));

				// first, read whole file
				int len = 0;
				char[] buf = new char[1024];
				while ((len = in.read(buf)) != -1) {
					text.append(buf, 0, len);
				}

				while (lineEnd < text.length() && lineEnd <= start) {
					lineStart = lineEnd;
					lineEnd = parseLine(text, lineEnd);
					line = line + 1;
				}
			} catch (IOException e) {
				output.println("syntax error: " + getMessage());
				return;
			}
			lineEnd = Math.min(lineEnd, text.length());

			if (brief) {
				// brief form
				output.println(filename + ":" + line + ":"
						+ (start - lineStart) + ":" + (end - lineStart) + ":\""
						+ getMessage() + "\"");
			} else {
				// Full form
				output.println(filename + ":" + line + ": " + getMessage());
				// NOTE: in the following lines I don't print characters
				// individually. The reason for this is that it messes up the
				// ANT
				// task output.
				String str = "";
				for (int i = lineStart; i < lineEnd; ++i) {
					str = str + text.charAt(i);
				}
				if (str.length() > 0 && str.charAt(str.length() - 1) == '\n') {
					output.print(str);
				} else {
					// this must be the very last line of output and, in this
					// particular case, there is no new-line character provided.
					// Therefore, we need to provide one ourselves!
					output.println(str);
				}
				str = "";
				for (int i = lineStart; i < start; ++i) {
					if (text.charAt(i) == '\t') {
						str += "\t";
					} else {
						str += " ";
					}
				}
				for (int i = start; i <= end; ++i) {
					str += "^";
				}
				output.println(str);
			}
		}
	}

	private static int parseLine(StringBuilder buf, int index) {
		while (index < buf.length() && buf.charAt(index) != '\n') {
			index++;
		}
		return index + 1;
	}
	
	public static final long serialVersionUID = 1l;

	public static void syntaxError(String msg, String filename,
			SyntacticElement elem) {
		int start = -1;
		int end = -1;

		Attribute.Source attr = (Attribute.Source) elem
				.attribute(Attribute.Source.class);
		if (attr != null) {
			start = attr.start;
			end = attr.end;
		}

		throw new SyntaxError(msg, filename, start, end);
	}

	public static void syntaxError(String msg, String filename,
			SyntacticElement elem, Throwable ex) {
		int start = -1;
		int end = -1;

		Attribute.Source attr = (Attribute.Source) elem
				.attribute(Attribute.Source.class);
		if (attr != null) {
			start = attr.start;
			end = attr.end;
		}

		throw new SyntaxError(msg, filename, start, end, ex);
	}

	/**
	 * An internal failure is a special form of syntax error which indicates
	 * something went wrong whilst processing some piece of syntax. In other
	 * words, is an internal error in the compiler, rather than a mistake in the
	 * input program.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class InternalFailure extends SyntaxError {
		public InternalFailure(String msg, String filename, int start, int end) {
			super(msg, filename, start, end);
		}

		public InternalFailure(String msg, String filename, int start, int end,
				Throwable ex) {
			super(msg, filename, start, end, ex);
		}

		public String getMessage() {
			String msg = super.getMessage();
			if (msg == null || msg.equals("")) {
				return "internal failure";
			} else {
				return "internal failure, " + msg;
			}
		}
	}

	public static void internalFailure(String msg, String filename,
			SyntacticElement elem) {
		int start = -1;
		int end = -1;

		Attribute.Source attr = (Attribute.Source) elem
				.attribute(Attribute.Source.class);
		if (attr != null) {
			start = attr.start;
			end = attr.end;
		}

		throw new InternalFailure(msg, filename, start, end);
	}

	public static void internalFailure(String msg, String filename,
			SyntacticElement elem, Throwable ex) {
		int start = -1;
		int end = -1;

		Attribute.Source attr = (Attribute.Source) elem
				.attribute(Attribute.Source.class);
		if (attr != null) {
			start = attr.start;
			end = attr.end;
		}

		throw new InternalFailure(msg, filename, start, end, ex);
	}

}
