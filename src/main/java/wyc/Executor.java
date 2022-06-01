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
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;

import wycc.util.*;
import wyil.interpreter.Interpreter;
import wyil.interpreter.Interpreter.Heap;
import wyil.interpreter.ConcreteSemantics.RValue;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.*;

/**
 * Responsible for parsing command-line arguments and executing the
 * WhileyCompiler.
 *
 * @author David J. Pearce
 *
 */
public class Executor {
	/**
	 * Destination directory of Wyil files.
	 */
	private File wyildir = new File(".");
	/**
	 * Identify path for binary target to generate.
	 */
	private Trie target = Trie.fromString("main");
	/**
	 * WyIL dependencies to include during compilation.
	 */
	private List<File> whileypath = Collections.EMPTY_LIST;
	private List<WyilFile> dependencies = new ArrayList<>();

	public Executor setTarget(Trie target) {
		this.target = target;
		return this;
	}

	public Executor setWhileyPath(List<File> whileypath) {
		this.whileypath = whileypath;
		return this;
	}

	public Executor addDependency(WyilFile dep) {
		this.dependencies.add(dep);
		return this;
	}

	public Executor setWyilDir(File wyildir) {
		this.wyildir = wyildir;
		return this;
	}

	public boolean run(QualifiedName name, Type.Callable sig) throws IOException {
		//
		ArrayList<WyilFile> deps = new ArrayList<>(this.dependencies);
		// Read target
		deps.add(Compiler.readWyilFile(wyildir, this.target));
		// Extract any dependencies from zips
		for(File dep : whileypath) {
			Compiler.extractDependencies(dep,deps);
		}
		// Try to run the given function or method
		Interpreter interpreter = new Interpreter(System.out, deps);
		// Add native methods
		addStdNatives(interpreter);
		// Sanity check target method exists
		Decl.Callable lambda = interpreter.getCallable(name, sig);
		if(lambda == null) {
			System.err.println("method not found: " + name + ", " + sig);
			return false;
		} else {
			// Create the initial stack
			Interpreter.CallStack stack = interpreter.new CallStack();
			try {
				//
				interpreter.execute(name, sig, stack);
				return true;
			} catch (Interpreter.RuntimeError e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	private void addStdNatives(Interpreter interpreter) {
		interpreter.bindNative(QualifiedName.fromString("std::io::print"), Executor::stdIoPrint);
		interpreter.bindNative(QualifiedName.fromString("std::io::println"), Executor::stdIoPrintLn);
		interpreter.bindNative(QualifiedName.fromString("std::fs::open"), Executor::stdFsOpen);
	}


	private static RValue stdIoPrint(Heap heap, RValue[] args) {
		RValue arg = args[0];
		if(arg instanceof RValue.Int) {
			System.out.print(arg.toString());
		} else {
			RValue.Array arr = (RValue.Array) arg;
			System.out.print(new String(arr.toByteArray()));
		}
		return null;
	}

	private static RValue stdIoPrintLn(Heap heap, RValue[] args) {
		stdIoPrint(heap,args);
		System.out.println();
		return null;
	}

	private static RValue stdFsOpen(Heap heap, RValue[] args) {
		RValue.Array arg0 = (RValue.Array) args[0];
		String filename = new String(arg0.toByteArray());
		File f = new File(filename);

		if(f.exists()) {
			// FIXME: need to really think about this.
			RValue.Field readAll = new RValue.Field("read_all",null);
			RValue.Field read = new RValue.Field("read",null);
			RValue.Field write = new RValue.Field("write",null);
			RValue.Field flush = new RValue.Field("flush",null);
			RValue.Field hasMore = new RValue.Field("has_more",null);
			RValue.Field close = new RValue.Field("close",null);
			RValue.Field available = new RValue.Field("available",null);
			return new RValue.Record(readAll,read,write,flush,hasMore,close,available);
		} else {
			// THIS DOESN"T MAKE SENSE!!
			return null;
		}
	}

	/**
	 * Command-line options
	 */
	private static final OptArg[] OPTIONS = {
			// Standard options
			new OptArg("output","o",OptArg.STRING,"set target file","main"),
			new OptArg("wyildir", OptArg.FILEDIR, "Specify where to place binary (WyIL) files", new File(".")),
			new OptArg("whileypath", OptArg.FILELIST, "Specify additional dependencies", new ArrayList<>())
	};
	//
	public static void main(String[] _args) throws IOException {
		List<String> args = new ArrayList<>(Arrays.asList(_args));
		Map<String, Object> options = OptArg.parseOptions(args, OPTIONS);
		// Extract config options
		File wyildir = (File) options.get("wyildir");
		Trie target = Trie.fromString((String) options.get("output"));
		ArrayList<File> whileypath = (ArrayList<File>) options.get("whileypath");
		// Construct Main object
		Executor main = new Executor().setTarget(target).setWyilDir(wyildir).setWhileyPath(whileypath);
		// Default signature
		Type.Method sig = new Type.Method(Type.Void, Type.Void);
		//
		boolean result = true;
		for (String arg : args) {
			// Determine qualitifed name of method to invoke.
			result &= main.run(QualifiedName.fromString(arg), sig);
		}
		// Compile Whiley source file(s).

		// Produce exit code
		System.exit(result ? 0 : 1);
	}
}
