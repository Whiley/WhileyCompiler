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
import java.util.List;

import jbfs.core.Build;
import jbfs.core.SourceFile;
import jbfs.util.Trie;
import wycc.util.AbstractCompilationUnit.Attribute;
import wycc.util.AbstractCompilationUnit.Attribute.Span;

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
		outputSourceError(output,false);
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
		SourceFile source = getSourceEntry(element);
		// Read the enclosing line so we can print it
		SourceFile.Line line = source.getEnclosingLine(span.getStart().get().intValue());
		//
		if(line == null) {
			output.println("Internal failure: " + getMessage());
		} else if(brief) {
			printBriefError(output,entry,line,getMessage());
		} else {
			printFullError(output,entry,span,line,getMessage());
		}
	}

	private void printBriefError(PrintStream output, Build.Artifact entry, SourceFile.Line enclosing, String message) {
		int start = enclosing.getOffset();
		int end = start + enclosing.getLength();
		output.print(entry.getPath() + ":" + enclosing.getNumber() + ":"
				+ start + ":" + end + ":\""
				+ escapeMessage(message) + "\"");
		// Done
		output.println();
	}

	private void printFullError(PrintStream output, Build.Artifact entry, Span span, SourceFile.Line enclosing, String message) {
		output.println(entry.getPath() + ":" + enclosing.getNumber() + ": " + message);
		printLineHighlight(output,span, enclosing);
	}

	public static void printLineHighlight(PrintStream output, Span span, SourceFile.Line enclosing) {
		// Extract line text
		String text = enclosing.getText();
		// Determine start and end of span
		int start = span.getStart().get().intValue() - enclosing.getOffset();
		int end = Math.min(text.length() - 1, span.getEnd().get().intValue() - enclosing.getOffset());
		// NOTE: in the following lines I don't print characters
		// individually. The reason for this is that it messes up the
		// ANT task output.
		output.println(text);
		// First, mirror indendation
		String str = "";
		for (int i = 0; i < start; ++i) {
			if (text.charAt(i) == '\t') {
				str += "\t";
			} else {
				str += " ";
			}
		}
		// Second, place highlights
		for (int i = start; i <= end; ++i) {
			str += "^";
		}
		output.println(str);
	}

	public static SourceFile getSourceEntry(SyntacticItem item) {
		SyntacticHeap heap = item.getHeap();
		//
		if(heap instanceof Build.Artifact) {
			Build.Artifact a = (Build.Artifact) heap;
			Trie id = a.getPath();
			return getSourceEntry(id,a.getSourceArtifacts());
		} else {
			return null;
		}
	}

	public static SourceFile getSourceEntry(Trie id, SourceFile... sources) {
		//
		for (SourceFile s : sources) {
			if (id.equals(s.getPath())) {
				return s;
			}
		}
		return null;
	}

	public static SourceFile getSourceEntry(Trie id, List<? extends Build.Artifact> sources) {
		//
		for (Build.Artifact s : sources) {
			if (id.equals(s.getPath())) {
				// FIXME: this is broken
				return (SourceFile) s;
			}
		}
		return null;
	}

	private static SourceFile.Line readEnclosingLine(Build.Artifact entry, Attribute.Span span) {
		if(entry instanceof SourceFile) {
			return ((SourceFile)entry).getEnclosingLine(span.getStart().get().intValue());
		} else {
			System.out.println("GOT HERE " + entry.getClass().getName());
			return null;
		}
	}

	public static final long serialVersionUID = 1l;

	private static String escapeMessage(String message) {
		message = message.replace("\n", "\\n");
		message = message.replace("\"", "\\\"");
		return message;
	}

}
