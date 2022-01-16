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
package wyc;

import java.io.*;
import java.util.*;

import wyc.lang.WhileyFile;
import wyc.task.CompileTask;
import wyc.util.ErrorMessages;
import wycc.lang.Syntactic;
import wycc.util.*;
import wyil.io.WyilFileReader;
import wyil.io.WyilFileWriter;
import wyil.lang.WyilFile;

/**
 * Responsible for parsing command-line arguments and executing the
 * WhileyCompiler.
 *
 * @author David J. Pearce
 *
 */
public class Main {
	/**
	 * Command-line options
	 */
	private static final OptArg[] OPTIONS = {
			// Standard options
			new OptArg("verbose","v","set verbose output"),
			new OptArg("strict","s","set strict mode"),
			new OptArg("output","o",OptArg.STRING,"set output file","main"),
			new OptArg("whileydir", OptArg.FILEDIR, "Specify where to find Whiley source files", new File(".")),
			new OptArg("wyildir", OptArg.FILEDIR, "Specify where to place binary (WyIL) files", new File("."))
	};
	//
	public static void main(String[] _args) throws IOException {
		List<String> args = new ArrayList<>(Arrays.asList(_args));
		Map<String, Object> options = OptArg.parseOptions(args, OPTIONS);
		// Extract config options
		boolean strict = options.containsKey("strict");
		File whileydir = (File) options.get("whileydir");
		File wyildir = (File) options.get("wyildir");
		Trie target = Trie.fromString((String) options.get("output"));
		// Compile Whiley source file(s).
		boolean result = compile(whileydir,wyildir,args,target);
		// Produce exit code
		System.exit(result ? 0 : 1);
	}

	public static boolean compile(File whileydir, File wyildir, List<String> srcfiles, Trie target) throws IOException {
		List<WhileyFile> whileyfiles = new ArrayList<>();
		boolean strict = false;
		// Read source files
		for (String wf : srcfiles) {
			Trie path = Trie.fromString(wf.replace(".whiley", ""));
			whileyfiles.add(readWhileyFile(path, whileydir, wf));
		}
		// Compile WyilFile
		CompileTask task = new CompileTask(target).setStrict(strict);
		Pair<WyilFile, Boolean> r = task.compile(whileyfiles);
		// Read out binary file from build repository
		WyilFile binary = r.first();
		// Print out syntactic markers
		printSyntacticMarkers(System.err, binary);
		// Write generated WyIL file
		writeWyilFile(binary,wyildir);
		//
		return binary.isValid();
	}

	/**
	 * Read a Whiley source file from the file system.
	 *
	 * @param id
	 * @param dir
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static WhileyFile readWhileyFile(Trie id, File dir, String filename) throws IOException {
		try(FileInputStream fin = new FileInputStream(new File(dir,filename))) {
			return new WhileyFile(id, fin.readAllBytes());
		}
	}
	/**
	 * Read a WyilFile from the file system.
	 *
	 * @param id
	 * @param dir
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static WyilFile readWyilFile(Trie id, File dir, String filename) throws IOException {
		try(FileInputStream fin = new FileInputStream(new File(dir,filename))) {
			return new WyilFileReader(fin).read(id);
		}
	}

	public static void writeWyilFile(WyilFile wf, File dir) throws IOException {
		String filename = wf.getPath().toNativeString() + ".wyil";
		try(FileOutputStream fout = new FileOutputStream(new File(dir,filename))) {
			new WyilFileWriter(fout).write(wf);
			fout.flush();
		}
	}

	// =============================================================================
	// Print Markers
	// =============================================================================

	/**
	 * Print out all syntactic markers active within a given piece of content.
	 *
	 * @param executor
	 * @throws IOException
	 */
	public static void printSyntacticMarkers(PrintStream output, WyilFile target) throws IOException {
		// Extract all syntactic markers from entries in the build graph
		List<Syntactic.Marker> items = extractSyntacticMarkers(target);
		// For each marker, print out error messages appropriately
		for (int i = 0; i != items.size(); ++i) {
			// Log the error message
			printSyntacticMarkers(output, items.get(i), target.getSourceArtifacts());
		}
	}

	/**
	 * Print a given syntactic marker.
	 *
	 * @param output
	 * @param marker
	 * @param sources
	 */
	public static void printSyntacticMarkers(PrintStream output, Syntactic.Marker marker,
			List<WhileyFile> sources) {
		// Identify enclosing source file
		WhileyFile source = getSourceEntry(marker.getSource(), sources);
		String filename = marker.getSource().toString() + ".whiley";
		// Determine the source-file span for the given syntactic marker.
		Syntactic.Span span = marker.getTarget().getAncestor(AbstractCompilationUnit.Attribute.Span.class);
		// Read the enclosing line so we can print it
		TextFile.Line line = source.getEnclosingLine(span.getStart());
		// Sanity check we found it
		if (line != null) {
			// print the error message
			output.println(filename + ":" + line.getNumber() + ": " + marker.getMessage());
			// Finally print the line highlight
			printLineHighlight(output, span, line);
		} else {
			output.println(filename + ":?: " + marker.getMessage());
		}
	}

	public static List<Syntactic.Marker> extractSyntacticMarkers(WyilFile... binaries) throws IOException {
		List<Syntactic.Marker> annotated = new ArrayList<>();
		//
		for (WyilFile b : binaries) {
			// If the object in question can be decoded as a syntactic heap then we can look
			// for syntactic messages.
			if (b instanceof Syntactic.Heap) {
				annotated.addAll(extractSyntacticMarkers((Syntactic.Heap) b));
			}
		}
		//
		return annotated;
	}

	/**
	 * Traverse the various binaries which have been generated looking for error
	 * messages.
	 *
	 * @param binaries
	 * @return
	 * @throws IOException
	 */
	public static List<Syntactic.Marker> extractSyntacticMarkers(Syntactic.Heap h) throws IOException {
		List<Syntactic.Marker> annotated = new ArrayList<>();
		// FIXME: this just reports all syntactic markers
		annotated.addAll(h.findAll(Syntactic.Marker.class));
		//
		return annotated;
	}

	private static void printLineHighlight(PrintStream output, Syntactic.Span span, TextFile.Line enclosing) {
		// Extract line text
		String text = enclosing.getText();
		// Determine start and end of span
		int start = span.getStart() - enclosing.getOffset();
		int end = Math.min(text.length() - 1, span.getEnd() - enclosing.getOffset());
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


	public static WhileyFile getSourceEntry(Syntactic.Item item) {
		Syntactic.Heap heap = item.getHeap();
		//
		if(heap instanceof WyilFile) {
			WyilFile a = (WyilFile) heap;
			Trie id = a.getPath();
			return getSourceEntry(id,a.getSourceArtifacts());
		} else {
			return null;
		}
	}

	public static WhileyFile getSourceEntry(Trie id, List<WhileyFile> sources) {
		//
		for (WhileyFile s : sources) {
			if (id.equals(s.getPath())) {
				return s;
			}
		}
		return null;
	}

}
