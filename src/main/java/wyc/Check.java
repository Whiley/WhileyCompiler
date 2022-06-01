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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.*;

import wyc.Compiler.PrintStreamErrorHandler;
import wyc.lang.WhileyFile;
import wyc.task.CompileTask;
import wyc.task.QuickCheck;
import wyc.task.QuickCheck.Context;
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
public class Check {
	/**
	 * The outgoing mailbox for this compiler. Essentially, all generated syntax
	 * errors are sent here.
	 */
	private MailBox<SyntaxError> mailbox = new PrintStreamErrorHandler(System.out);
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
	/**
	 * List of config modifiers.
	 */
	private List<Function<QuickCheck.Context,QuickCheck.Context>> configs = new ArrayList<>();

	public Check addConfig(Function<Context,Context> config) {
		configs.add(config);
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
		//
		// Start with default context
		QuickCheck.Context context = QuickCheck.DEFAULT_CONTEXT;
		// Apply all configurations
		for(Function<Context,Context> c : configs) {
			context = c.apply(context);
		}
		// FIXME: dependencies!
		// Check each WyIL file requested
		for(Trie source : sources) {
			WyilFile wf = wyc.Compiler.readWyilFile(wyildir, source);
			// Extract source file
			task.check(wf, context, Collections.EMPTY_LIST);
			// Write out any syntactic markers
			Compiler.writeSyntacticMarkers(mailbox, wf);
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
			new OptArg("whileypath", OptArg.FILELIST, "Specify additional dependencies", new ArrayList<>()),
			new OptArg("min",OptArg.INT, "Specify minimum integer value", QuickCheck.DEFAULT_CONTEXT.getIntegerMinimum()),
			new OptArg("max",OptArg.INT, "Specify maximum integer value", QuickCheck.DEFAULT_CONTEXT.getIntegerMaximum()),
			new OptArg("length",OptArg.INT, "Specify maximum array length", QuickCheck.DEFAULT_CONTEXT.getMaxArrayLength()),
			new OptArg("depth",OptArg.INT, "Specify maximum recursive type depth", QuickCheck.DEFAULT_CONTEXT.getRecursiveTypeDepth()),
			new OptArg("width",OptArg.INT, "Specify maximum alias width", QuickCheck.DEFAULT_CONTEXT.getAliasingWidth()),
			new OptArg("rotation",OptArg.INT, "Specify lambda rotation", QuickCheck.DEFAULT_CONTEXT.getLambdaWidth()),
			new OptArg("timeout",OptArg.LONG, "Specify maximum alias width", QuickCheck.DEFAULT_CONTEXT.getTimeout()),
			new OptArg("ignores",OptArg.STRINGARRAY, "Specify methods to ignore", QuickCheck.DEFAULT_CONTEXT.getIgnores())
	};
	//
	public static void main(String[] _args) throws IOException {
		List<String> args = new ArrayList<>(Arrays.asList(_args));
		Map<String, Object> options = OptArg.parseOptions(args, OPTIONS);
		// Extract config options
		boolean verbose = options.containsKey("verbose");
		File wyildir = (File) options.get("wyildir");
		ArrayList<File> whileypath = (ArrayList<File>) options.get("whileypath");
		int minInt = (Integer) options.get("min");
		int maxInt = (Integer) options.get("max");
		int maxArrayLength = (Integer) options.get("length");
		int maxTypeDepth = (Integer) options.get("depth");
		int maxAliasWidth = (Integer) options.get("width");
		int rotation = (Integer) options.get("rotation");
		long timeout = (Long) options.get("timeout");
		String[] ignores = (String[]) options.get("ignores");
		// Construct Main object
		Check main = new Check().setVerbose(verbose).setWyilDir(wyildir).setWhileyPath(whileypath)
				.addConfig(c -> c.setIntegerRange(minInt, maxInt)).addConfig(c -> c.setAliasingWidth(maxAliasWidth))
				.addConfig(c -> c.setArrayLength(maxArrayLength)).addConfig(c -> c.setTypeDepth(maxTypeDepth))
				.addConfig(c -> c.setLambdaWidth(rotation)).addConfig(c -> c.setTimeout(timeout))
				.addConfig(c -> c.setIgnores(ignores));
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
