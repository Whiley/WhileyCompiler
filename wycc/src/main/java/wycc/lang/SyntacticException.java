// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wycc.lang;

import java.io.PrintStream;

import jbfs.core.Build;
import wycc.util.AbstractCompilationUnit.Attribute;

/**
 * Represents an exception which has been raised on a synctic item. The purpose
 * of the exception is to identify this item in order that better information
 * can be reported.
 *
 * @author David J. Pearce
 *
 */
public class SyntacticException extends RuntimeException {
	/**
	 * The file entry to which this error applies
	 */
	private Build.Artifact entry;

	/**
	 * The SyntacticElement to which this error refers
	 */
	private SyntacticItem element;

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
	public SyntacticException(String msg, Build.Artifact entry, SyntacticItem element) {
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
	public SyntacticException(String msg, Build.Artifact entry, SyntacticItem element, Throwable ex) {
		super(msg,ex);
		this.entry = entry;
		this.element = element;
	}

	/**
	 * Get the syntactic element to which this error is attached.
	 *
	 * @return
	 */
	public SyntacticItem getElement() {
		return element;
	}

	/**
	 * Get the syntactic entry to which this error refers
	 * @return
	 */
	public Build.Artifact getEntry() {
		return entry;
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
		Attribute.Span span;
		if (entry == null || element == null) {
			output.println("Internal failure: " + getMessage());
			return;
		} else if(element instanceof Attribute.Span) {
			span = (Attribute.Span) element;
		} else  {
			SyntacticHeap parent = element.getHeap();
			span = parent.getParent(element,Attribute.Span.class);
		}
		//
		EnclosingLine enclosing = (span == null) ? null : readEnclosingLine(entry, span);
		if(enclosing == null) {
			output.println("Internal failure: " + getMessage());
		} else if(brief) {
			printBriefError(output,entry,enclosing,getMessage());
		} else {
			printFullError(output,entry,enclosing,getMessage());
		}
	}

	private void printBriefError(PrintStream output, Build.Artifact entry, EnclosingLine enclosing, String message) {
		output.print(entry.getPath() + ":" + enclosing.lineNumber + ":"
				+ enclosing.columnStart() + ":"
				+ enclosing.columnEnd() + ":\""
				+ escapeMessage(message) + "\"");
		// Done
		output.println();
	}

	private void printFullError(PrintStream output, Build.Artifact entry, EnclosingLine enclosing, String message) {
		output.println(entry.getPath() + ":" + enclosing.lineNumber + ": " + message);

		printLineHighlight(output,enclosing);
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

	private static EnclosingLine readEnclosingLine(Build.Artifact entry, Attribute.Span location) {
//		int spanStart = location.getStart().get().intValue();
//		int spanEnd = location.getEnd().get().intValue();
//		int line = 0;
//		int lineStart = 0;
//		int lineEnd = 0;
//		StringBuilder text = new StringBuilder();
//		try {
//			BufferedReader in = new BufferedReader(new InputStreamReader(entry.inputStream(), "UTF-8"));
//
//			// first, read whole file
//			int len = 0;
//			char[] buf = new char[1024];
//			while ((len = in.read(buf)) != -1) {
//				text.append(buf, 0, len);
//			}
//
//			while (lineEnd < text.length() && lineEnd <= spanStart) {
//				lineStart = lineEnd;
//				lineEnd = parseLine(text, lineEnd);
//				line = line + 1;
//			}
//		} catch (IOException e) {
//			return null;
//		}
//		lineEnd = Math.min(lineEnd, text.length());
//
//		return new EnclosingLine(spanStart, spanEnd, line, lineStart, lineEnd,
//				text.substring(lineStart, lineEnd));
		throw new IllegalArgumentException("GOT HERE"); // Build.Entry should be a SourceFile
	}

	public static final long serialVersionUID = 1l;

	private static String escapeMessage(String message) {
		message = message.replace("\n", "\\n");
		message = message.replace("\"", "\\\"");
		return message;
	}

}
