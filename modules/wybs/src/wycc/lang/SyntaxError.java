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

package wycc.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import wyfs.lang.Path;

/**
 * This exception is thrown when a syntax error occurs in the parser.
 *
 * @author David J. Pearce
 *
 */
public class SyntaxError extends RuntimeException {
	/**
	 * The file entry to which this error applies
	 */
	private Path.Entry<?> entry;
	
	/**
	 * The SyntacticElement to which this error refers
	 */
	private SyntacticElement element;

	/**
	 * Identify a syntax error at a particular point in a file.
	 *
	 * @param msg
	 *            Message detailing the problem.
	 * @param entry
	 *            The path entry for the compilation unit this error refers to
	 * @param element
	 *            The syntactic element to this error refers 
	 */
	public SyntaxError(String msg, Path.Entry<?> entry, SyntacticElement element) {
		super(msg);
		this.entry = entry;
		this.element = element;
	}

	/**
	 * Identify a syntax error at a particular point in a file.
	 *
	 * @param msg
	 *            Message detailing the problem.
	 * @param entry
	 *            The path entry for the compilation unit this error refers to
	 * @param element
	 *            The syntactic element to this error refers
	 */
	public SyntaxError(String msg, Path.Entry<?> entry, SyntacticElement element, Throwable ex) {
		super(msg,ex);
		this.entry = entry;
		this.element = element;
	}

	/**
	 * Get the syntactic element to which this error is attached.
	 * 
	 * @return
	 */
	public SyntacticElement getElement() {
		return element;
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
		if (entry == null) {
			output.println("syntax error: " + getMessage());
		} else {
			EnclosingLine enclosing = readEnclosingLine(entry, element.attribute(Attribute.Source.class));
			if(enclosing == null) {
				output.println("syntax error: " + getMessage());
			} else if(brief) {
				printBriefError(output,entry,enclosing,getMessage());
			} else {
				printFullError(output,entry,enclosing,getMessage());
			}
		}
	}

	private void printBriefError(PrintStream output, Path.Entry<?> entry, EnclosingLine enclosing, String message) {
		output.print(entry.location() + ":" + enclosing.lineNumber + ":"
				+ enclosing.columnStart() + ":"
				+ enclosing.columnEnd() + ":\""
				+ escapeMessage(message) + "\"");

		// Now print contextual information (if applicable)
//		if(context != null && context.length > 0) {
//			output.print(":");
//			boolean firstTime=true;
//			for(Attribute.Origin o : context) {
//				if(!firstTime) {
//					output.print(",");
//				}
//				firstTime=false;
//				enclosing = readEnclosingLine(o.filename, o.start, o.end);
//				output.print(filename + ":" + enclosing.lineNumber + ":"
//						+ enclosing.columnStart() + ":"
//						+ enclosing.columnEnd());
//			}
//		}

		// Done
		output.println();
	}

	private void printFullError(PrintStream output, Path.Entry<?> entry, EnclosingLine enclosing, String message) {
		
		output.println(entry.location() + ":" + enclosing.lineNumber + ": " + message);

		printLineHighlight(output,enclosing);

		// Now print contextual information (if applicable)
//		if(context != null && context.length > 0) {
//			for(Attribute.Origin o : context) {
//				output.println();
//				enclosing = readEnclosingLine(o.filename, o.start, o.end);
//				output.println(o.filename + ":" + enclosing.lineNumber + " (context)");
//				printLineHighlight(output,enclosing);
//			}
//		}
	}

	private void printLineHighlight(PrintStream output,
			EnclosingLine enclosing) {
		// NOTE: in the following lines I don't print characters
		// individually. The reason for this is that it messes up the
		// ANT task output.
		String str = enclosing.lineText;

		if (str.length() > 0 && str.charAt(str.length() - 1) == '\n') {
			output.print(str);
		} else {
			// this must be the very last line of output and, in this
			// particular case, there is no new-line character provided.
			// Therefore, we need to provide one ourselves!
			output.println(str);
		}
		str = "";
		for (int i = 0; i < enclosing.columnStart(); ++i) {
			if (enclosing.lineText.charAt(i) == '\t') {
				str += "\t";
			} else {
				str += " ";
			}
		}
		for (int i = enclosing.columnStart(); i <= enclosing.columnEnd(); ++i) {
			str += "^";
		}
		output.println(str);
	}

	private static int parseLine(StringBuilder buf, int index) {
		while (index < buf.length() && buf.charAt(index) != '\n') {
			index++;
		}
		return index + 1;
	}

	private static class EnclosingLine {
		private int lineNumber;
		private int start;
		private int end;
		private int lineStart;
		private int lineEnd;
		private String lineText;

		public EnclosingLine(int start, int end, int lineNumber, int lineStart, int lineEnd, String lineText) {
			this.start = start;
			this.end = end;
			this.lineNumber = lineNumber;
			this.lineStart = lineStart;
			this.lineEnd = lineEnd;
			this.lineText = lineText;
		}

		public int columnStart() {
			return start - lineStart;
		}

		public int columnEnd() {
			return Math.min(end, lineEnd) - lineStart;
		}
	}

	private static EnclosingLine readEnclosingLine(Path.Entry<?> entry, Attribute.Source location) {
		int line = 0;
		int lineStart = 0;
		int lineEnd = 0;
		StringBuilder text = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(entry.inputStream(), "UTF-8"));

			// first, read whole file
			int len = 0;
			char[] buf = new char[1024];
			while ((len = in.read(buf)) != -1) {
				text.append(buf, 0, len);
			}

			while (lineEnd < text.length() && lineEnd <= location.start) {
				lineStart = lineEnd;
				lineEnd = parseLine(text, lineEnd);
				line = line + 1;
			}
		} catch (IOException e) {
			return null;
		}
		lineEnd = Math.min(lineEnd, text.length());

		return new EnclosingLine(location.start, location.end, line, lineStart, lineEnd,
				text.substring(lineStart, lineEnd));
	}

	public static final long serialVersionUID = 1l;

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
		public InternalFailure(String msg, Path.Entry<? extends CompilationUnit> entry, SyntacticElement element) {
			super(msg, entry, element);
		}

		public InternalFailure(String msg, Path.Entry<? extends CompilationUnit> entry, SyntacticElement element,
				Throwable ex) {
			super(msg, entry, element, ex);
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
	
	private static String escapeMessage(String message) {
		message = message.replace("\n", "\\n");
		message = message.replace("\"", "\\\"");
		return message;
	}

}
