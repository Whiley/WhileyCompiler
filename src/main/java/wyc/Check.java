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
		// Determine logger based on verbose mode
		Logger logger = verbose ? new Logger.Default(System.out) : Logger.NULL;
		// Construct QuickCheck task
		QuickCheck task = new QuickCheck(logger);
		// Start with default context
		QuickCheck.Context context = QuickCheck.DEFAULT_CONTEXT;
		// FIXME: modify context!
		// FIXME: dependencies!
		// Check each WyIL file requested
		for(Trie source : sources) {
			WyilFile wf = wyc.Compiler.readWyilFile(wyildir, source);
			// Extract source file
			task.check(wf, context, Collections.EMPTY_LIST);
			// Print out any syntactic markers
			Compiler.printSyntacticMarkers(out, wf);
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
}
