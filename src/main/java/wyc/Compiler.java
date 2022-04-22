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
import java.util.zip.*;

import wyc.lang.WhileyFile;
import wyc.task.CompileTask;
import wycc.lang.Syntactic;
import wycc.util.*;
import wyil.io.WyilFileReader;
import wyil.io.WyilFileWriter;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Attr.SyntaxError;

/**
 * Responsible for parsing command-line arguments and executing the
 * WhileyCompiler.
 *
 * @author David J. Pearce
 *
 */
public class Compiler {
	/**
	 * The outgoing mailbox for this compiler. Essentially, all generated syntax
	 * errors are sent here.
	 */
	private MailBox<SyntaxError> mailbox = new PrintStreamErrorHandler(System.out);
	/**
	 * Source directory containing whiley files.
	 */
	private File whileydir = new File(".");
	/**
	 * Destination directory of Wyil files.
	 */
	private File wyildir = new File(".");
	/**
	 * Identify paths for source files to compile.
	 */
	private List<Trie> sources = new ArrayList<>();
	/**
	 * Identify path for binary target to generate.
	 */
	private Trie target = Trie.fromString("main");
	/**
	 * WyIL dependencies to include during compilation.
	 */
	private List<File> whileypath = Collections.EMPTY_LIST;
	private List<WyilFile> dependencies = new ArrayList<>();
	private boolean verification;
	private boolean counterexamples;
	private boolean strict;
	private boolean linking;

	public Compiler setErrorHandler(MailBox<SyntaxError> mailbox) {
		this.mailbox = mailbox;
		return this;
	}

	public Compiler setStrict(boolean b) {
		this.strict = b;
		return this;
	}

	public Compiler setLinking(boolean b) {
		this.linking = b;
		return this;
	}

	public Compiler setVerification(boolean b) {
		this.verification = b;
		return this;
	}

	public Compiler setCounterExamples(boolean b) {
		this.counterexamples = b;
		return this;
	}

	public Compiler setTarget(Trie target) {
		this.target = target;
		return this;
	}

	public Compiler addSource(Trie source) {
		this.sources.add(source);
		return this;
	}

	public Compiler setWhileyPath(List<File> whileypath) {
		this.whileypath = whileypath;
		return this;
	}

	public Compiler addDependency(WyilFile dep) {
		this.dependencies.add(dep);
		return this;
	}

	public Compiler setWhileyDir(File whileydir) {
		this.whileydir = whileydir;
		return this;
	}

	public Compiler setWyilDir(File wyildir) {
		this.wyildir = wyildir;
		return this;
	}

	public boolean run() throws IOException {
		List<WhileyFile> whileyfiles = new ArrayList<>();
		// Read source files
		for (Trie sf : sources) {
			WhileyFile wf = readWhileyFile(whileydir, sf);
			whileyfiles.add(wf);
		}
		ArrayList<WyilFile> deps = new ArrayList<>(this.dependencies);
		// Extract any dependencies from zips
		for(File dep : whileypath) {
			extractDependencies(dep,deps);
		}
		// Compile WyilFile
		CompileTask task = new CompileTask(target, deps).setStrict(strict).setLinking(linking);
		Pair<WyilFile, Boolean> r = task.compile(whileyfiles);
		// Read out binary file from build repository
		WyilFile binary = r.first();
		// Print out syntactic markers
		writeSyntacticMarkers(mailbox,binary);
		// Write generated WyIL file
		writeWyilFile(wyildir,target,binary);
		//
		return r.second() && binary.isValid();
	}

	/**
	 * Command-line options
	 */
	private static final OptArg[] OPTIONS = {
			// Standard options
			new OptArg("verbose","v","set verbose output"),
			new OptArg("strict","s","set strict mode"),
			new OptArg("linking","l","set linking mode"),
			new OptArg("output","o",OptArg.STRING,"set output file","main"),
			new OptArg("whileydir", OptArg.FILEDIR, "Specify where to find Whiley source files", new File(".")),
			new OptArg("wyildir", OptArg.FILEDIR, "Specify where to place binary (WyIL) files", new File(".")),
			new OptArg("whileypath", OptArg.FILELIST, "Specify additional dependencies", new ArrayList<>())
	};
	//
	public static void main(String[] _args) throws IOException {
		List<String> args = new ArrayList<>(Arrays.asList(_args));
		Map<String, Object> options = OptArg.parseOptions(args, OPTIONS);
		// Extract config options
		boolean strict = options.containsKey("strict");
		boolean linking = options.containsKey("linking");
		File whileydir = (File) options.get("whileydir");
		File wyildir = (File) options.get("wyildir");
		Trie target = Trie.fromString((String) options.get("output"));
		ArrayList<File> whileypath = (ArrayList<File>) options.get("whileypath");
		// Construct Main object
		Compiler main = new Compiler().setTarget(target).setStrict(strict).setLinking(linking).setWhileyDir(whileydir)
				.setWyilDir(wyildir).setWhileyPath(whileypath);
		//
		for(String arg : args) {
			arg = arg.replace(".whiley", "");
			main.addSource(Trie.fromNativeString(arg));
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
	// Syntactic Markers
	// =============================================================================

	/**
	 * Print out all syntactic markers active within a given piece of content.
	 *
	 * @param executor
	 * @throws IOException
	 */
	public static void writeSyntacticMarkers(MailBox<SyntaxError> mailbox, WyilFile target) throws IOException {
		// Extract all syntactic markers from entries in the build graph
		List<Syntactic.Marker> items = extractSyntacticMarkers(target);
		// For each marker, print out error messages appropriately
		for (int i = 0; i != items.size(); ++i) {
			WyilFile.Attr.SyntaxError marker = (WyilFile.Attr.SyntaxError) items.get(i);
			// Log the error message
			mailbox.send(marker);
		}
	}

//	public static List<Syntactic.Marker> extractSyntacticMarkers(WyilFile... binaries) throws IOException {
//		List<Syntactic.Marker> annotated = new ArrayList<>();
//		//
//		for (WyilFile b : binaries) {
//			// If the object in question can be decoded as a syntactic heap then we can look
//			// for syntactic messages.
//			if (b instanceof Syntactic.Heap) {
//				annotated.addAll(extractSyntacticMarkers(b));
//			}
//		}
//		//
//		return annotated;
//	}

	/**
	 * Traverse a binary which has been generated looking for error
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

	/**
	 * A super simplistic error handler which is used for writing error messages to
	 * a given output stream (e.g. stdout).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class PrintStreamErrorHandler implements MailBox<SyntaxError> {
		private final PrintStream output;

		public PrintStreamErrorHandler(PrintStream output) {
			this.output = output;
		}

		@Override
		public void send(SyntaxError marker) {
			// Identify enclosing source file
			String filename = marker.getSource().toString() + ".whiley";
			// Determine the source-file span for the given syntactic marker.
			Syntactic.Span span = marker.getTarget().getAncestor(AbstractCompilationUnit.Attribute.Span.class);
			// print the error message
			output.println(filename + "|" + span.getStart() + "|" + span.getEnd() + "|" + marker.getErrorCode() + "|"
					+ marker.getMessage().replace("\n", "\\n"));
		}
	}
}
