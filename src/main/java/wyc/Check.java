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
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.*;

import wyc.lang.WhileyFile;
import wyc.task.CompileTask;
import wyc.task.QuickCheck;
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
public class Check {
	/**
	 * The output stream from this compiler.
	 */
	private PrintStream out = System.out;
	/**
	 *
	 */
	private boolean verbose = false;
	/**
	 * Destination directory of Wyil files.
	 */
	private File wyildir = new File(".");
	/**
	 * Identify paths for source files to compile.
	 */
	private List<Trie> sources = new ArrayList<>();
	/**
	 * WyIL dependencies to include during compilation.
	 */
	private List<File> whileypath = Collections.EMPTY_LIST;

	public Check setOutput(PrintStream pout) {
		this.out = pout;
		return this;
	}

	public Check addSource(Trie source) {
		this.sources.add(source);
		return this;
	}

	public Check setVerbose(boolean verbose) {
		this.verbose = verbose;
		return this;
	}

	public Check setWhileyPath(List<File> whileypath) {
		this.whileypath = whileypath;
		return this;
	}

	public Check setWyilDir(File wyildir) {
		this.wyildir = wyildir;
		return this;
	}

	public boolean run() throws IOException {
		boolean result = true;
		// Choose logger based on verbose mode
		Logger logger = verbose ? new Logger.Default(System.out) : Logger.NULL;
		QuickCheck task = new QuickCheck(logger);
		QuickCheck.Context context = QuickCheck.DEFAULT_CONTEXT;
		// FIXME: change context

		// Add sources
		for(Trie source : sources) {
			WyilFile wf = wyc.Compiler.readWyilFile(wyildir, source);
			// Extract source file
			task.check(wf, context, Collections.EMPTY_LIST);
			//
			result &= wf.isValid();
		}
		// DOne
		return result;
	}

	/**
	 * Command-line options
	 */
	private static final OptArg[] OPTIONS = {
			// Standard options
			new OptArg("verbose","v","set verbose output"),
			new OptArg("wyildir", OptArg.FILEDIR, "Specify where to find binary (WyIL) files", new File(".")),
			new OptArg("whileypath", OptArg.FILELIST, "Specify additional dependencies", new ArrayList<>())
	};
	//
	public static void main(String[] _args) throws IOException {
		List<String> args = new ArrayList<>(Arrays.asList(_args));
		Map<String, Object> options = OptArg.parseOptions(args, OPTIONS);
		// Extract config options
		boolean verbose = options.containsKey("verbose");
		File wyildir = (File) options.get("wyildir");
		ArrayList<File> whileypath = (ArrayList<File>) options.get("whileypath");
		// Construct Main object
		Check main = new Check().setVerbose(verbose).setWyilDir(wyildir).setWhileyPath(whileypath);
		//
		for(String arg : args) {
			main.addSource(Trie.fromString(arg));
		}
		// Compile Whiley source file(s).
		boolean result = main.run();
		// Produce exit code
		System.exit(result ? 0 : 1);
	}

	public static void extractDependencies(File dep, List<WyilFile> dependencies) throws IOException {
		String suffix = getSuffix(dep.getName());
		//
		switch(suffix) {
		case "zip":
			extractFromZip(dep,dependencies);
			break;
		default:
			throw new IllegalArgumentException("invalid whileypath entry \"" + dep.getName() + "\"");
		}
	}

	/**
	 * Extract all WyilFiles contained in a zipfile.
	 *
	 * @param dep
	 * @param dependencies
	 * @throws IOException
	 */
	public static void extractFromZip(File dep, List<WyilFile> dependencies) throws IOException {
		ZipFile zf = new ZipFile(dep);
		Enumeration<? extends ZipEntry> entries = zf.entries();
		while(entries.hasMoreElements()) {
			ZipEntry e = entries.nextElement();
			String suffix = getSuffix(e.getName());
			if(suffix != null && suffix.equals("wyil")) {
				WyilFile wf = new WyilFileReader(zf.getInputStream(e)).read();
				dependencies.add(wf);
			}
		}
		zf.close();
	}

	/**
	 * Get the path associated with a given filename. For example, given
	 * "std/collections/stuff.wyil"
	 *
	 * @param t
	 * @return
	 */
	private static Trie getPath(String t) {
		int i = t.lastIndexOf('.');
		if (i >= 0) {
			t = t.substring(0,i);
		}
		return Trie.fromString(t.replace(File.separatorChar, '/'));
	}

	/**
	 * Extract the suffix from a given filename. For example, given "std-0.3.2.zip"
	 * we return "zip".
	 *
	 * @param t
	 * @return
	 */
	private static String getSuffix(String t) {
		int i = t.lastIndexOf('.');
		if (i >= 0) {
			return t.substring(i + 1);
		} else {
			return null;
		}
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
	public static WhileyFile readWhileyFile(File dir, Trie path) throws IOException {
		// First, determine filename
		String filename = path.toNativeString() + ".whiley";
		// Second Read the file
		try (FileInputStream fin = new FileInputStream(new File(dir, filename))) {
			return new WhileyFile(path, fin.readAllBytes());
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
	public static WyilFile readWyilFile(File dir, Trie path) throws IOException {
		// First, determine filename
		String filename = path.toNativeString() + ".wyil";
		// Second Read the file
		try (FileInputStream fin = new FileInputStream(new File(dir, filename))) {
			return new WyilFileReader(fin).read();
		}
	}

	/**
	 * Write a given WyilFile to disk using the given directory as a root.
	 *
	 * @param wf
	 * @param dir
	 * @throws IOException
	 */
	public static void writeWyilFile(File dir, Trie target, WyilFile wf) throws IOException {
		// First, determine filename
		String filename = target.toNativeString() + ".wyil";
		// First, determine filename
		try (FileOutputStream fout = new FileOutputStream(new File(dir, filename))) {
			new WyilFileWriter(fout).write(wf);
			fout.flush();
		}
	}

	// =============================================================================
	// Print Markers
	// =============================================================================

	public static void printBriefSyntacticMarkers(PrintStream output, WyilFile target) throws IOException {
		// When printing brief syntactic markers, we don't need the source WhileyFiles.
		printSyntacticMarkers(output,target,Collections.EMPTY_LIST,true);
	}

	/**
	 * Print out all syntactic markers active within a given piece of content.
	 *
	 * @param executor
	 * @throws IOException
	 */
	public static void printSyntacticMarkers(PrintStream output, WyilFile target, List<WhileyFile> sources, boolean brief) throws IOException {
		// Extract all syntactic markers from entries in the build graph
		List<Syntactic.Marker> items = extractSyntacticMarkers(target);
		// For each marker, print out error messages appropriately
		for (int i = 0; i != items.size(); ++i) {
			// Log the error message
			printSyntacticMarkers(output, items.get(i), sources, brief);
		}
	}

	public static void printSyntacticMarkers(PrintStream output, Syntactic.Marker marker, List<WhileyFile> sources,
			boolean brief) {
		// Identify enclosing source file
		String filename = marker.getSource().toString() + ".whiley";
		// Determine the source-file span for the given syntactic marker.
		Syntactic.Span span = marker.getTarget().getAncestor(AbstractCompilationUnit.Attribute.Span.class);
		// Sanity check we found it
			// print the error message
			if(brief) {
				output.println(filename + "|" + span.getStart() + "|" + span.getEnd() + "| " + marker.getMessage());
			} else {
				WhileyFile source = getSourceEntry(marker.getSource(), sources);
				// Read the enclosing line so we can print it
				TextFile.Line line = source.getEnclosingLine(span.getStart());
				if (line != null) {
					output.println(filename + ":" + line.getNumber() + ": " + marker.getMessage());
					// Finally print the line highlight
					printLineHighlight(output, span, line);
				} else {
					output.println(filename + ":?: " + marker.getMessage());
				}
			}
	}

	public static List<Syntactic.Marker> extractSyntacticMarkers(WyilFile... binaries) throws IOException {
		List<Syntactic.Marker> annotated = new ArrayList<>();
		//
		for (WyilFile b : binaries) {
			// If the object in question can be decoded as a syntactic heap then we can look
			// for syntactic messages.
			if (b instanceof Syntactic.Heap) {
				annotated.addAll(extractSyntacticMarkers(b));
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
