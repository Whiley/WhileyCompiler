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

package wyc;

import java.io.*;
import java.util.*;

import wyil.*;
import wyil.io.ModuleReader;
import wyil.lang.*;
import wyil.util.*;
import wyc.lang.*;
import wyc.stages.*;

/**
 * Responsible for managing the process of turning source files into binary code
 * for execution. Each source file is passed through a pipeline of stages that
 * modify it in a variety of ways. The main stages are:
 * <ol>
 * <li>
 * <p>
 * <b>Lexing and Parsing</b>, where the source file is converted into an
 * Abstract Syntax Tree (AST) representation.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Name Resolution</b>, where the fully qualified names of all external
 * symbols are determined.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Type Propagation</b>, where the types of all expressions are determined by
 * propagation from e.g. declared parameter types.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>WYIL Generation</b>, where the the AST is converted into the Whiley
 * Intermediate Language (WYIL). A number of passes are then made over this
 * before it is ready for code generation.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Code Generation</b>. Here, the executable code is finally generated. This
 * could be Java bytecode, or something else (e.g. JavaScript).
 * </p>
 * </li>
 * </ol>
 * Every stage of the compiler can be configured by setting various options.
 * Stages can also be bypassed (typically for testing) and new ones can be
 * added.
 * 
 * @author David J. Pearce
 * 
 */
public final class Compiler implements Logger {		
	private ModuleLoader loader;	
	private NameResolver nameResolver;
	private ArrayList<Transform> stages;

	public Compiler(ModuleLoader loader, List<Transform> stages) {
		this.loader = loader;
		this.stages = new ArrayList<Transform>(stages);
		nameResolver = new NameResolver(loader);		
	}
	
	/**
	 * The logout output stream is used to write log information about the
	 * status of compilation. The default stream just discards everything.
	 */
	private PrintStream logout = new PrintStream(new OutputStream() {
		public void write(byte[] b) { /* don't do anything! */
		}

		public void write(byte[] b, int x, int y) { /* don't do anything! */
		}

		public void write(int x) { /* don't do anything! */
		}
	});
	
	public void setLogOut(OutputStream logout) {
		this.logout = new PrintStream(logout);
	}

	public List<WhileyFile> compile(List<File> files) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();		
		long memory = runtime.freeMemory();
		
		ArrayList<WhileyFile> wyfiles = new ArrayList<WhileyFile>();
		for (File f : files) {
			WhileyFile wf = innerParse(f);			
			wyfiles.add(wf);								
		}
		
		expandConstants(wyfiles);
		
		for (WhileyFile wf : wyfiles) {
			resolveNames(wf);			
		}
		
		ArrayList<Module> modules = new ArrayList<Module>();
		for (WhileyFile wf : wyfiles) {
			Module m = buildModule(wf);	
			loader.register(m);
			modules.add(m);
		}
		
		finishCompilation(modules);		
		
		long endTime = System.currentTimeMillis();
		logTotalTime("Compiled " + files.size() + " file(s)",endTime-start, memory - runtime.freeMemory());
		
		return wyfiles;
	}
		
	/**
	 * This method simply parses a whiley file into an abstract syntax tree. It
	 * makes little effort to check whether or not the file is syntactically
	 * correct. In particular, it does not determine the correct type of all
	 * declarations, expressions, etc.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private WhileyFile innerParse(File file) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();		
		long memory = runtime.freeMemory();
		WhileyLexer wlexer = new WhileyLexer(file.getPath());		
		List<WhileyLexer.Token> tokens = new WhileyFilter().filter(wlexer.scan());		

		WhileyParser wfr = new WhileyParser(file.getPath(), tokens);
		logTimedMessage("[" + file + "] Parsing complete",
				System.currentTimeMillis() - start, memory - runtime.freeMemory());
		
		WhileyFile wf = wfr.read();
		nameResolver.register(wf);		
		return wf;
	}		
	
	/**
	 * This method puts the given module through the second half of the
	 * compilation pipeline. In particular, it propagates and generates types
	 * for all expressions used within the module, as well as checking for
	 * definite assignment and performing verification checking.
	 * 
	 * @param wf
	 */
	private void finishCompilation(List<Module> modules) throws Exception {				
		// Register the updated file
		for(Module module : modules) {
			loader.register(module);
		}
		
		for(Transform stage : stages) {
			for(Module module : modules) {
				process(module,stage);
			}
		}		
	}
	
	private void process(Module module, Transform stage) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();		
		long memory = runtime.freeMemory();
		String name = name(stage.getClass().getSimpleName());		
		
		try {						
			stage.apply(module);			
			logTimedMessage("[" + module.filename() + "] applied "
					+ name, System.currentTimeMillis() - start, memory - runtime.freeMemory());
			System.gc();
		} catch (RuntimeException ex) {
			logTimedMessage("[" + module.filename() + "] failed on "
					+ name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start, memory - runtime.freeMemory());
			throw ex;
		} catch (IOException ex) {
			logTimedMessage("[" + module.filename() + "] failed on "
					+ name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start, memory - runtime.freeMemory());
			throw ex;
		}
	}
	
	private static String name(String camelCase) {
		boolean firstTime = true;
		String r = "";
		for(int i=0;i!=camelCase.length();++i) {
			char c = camelCase.charAt(i);
			if(!firstTime && Character.isUpperCase(c)) {
				r += " ";
			} 
			firstTime=false;
			r += Character.toLowerCase(c);;
		}
		return r;
	}
	
	private void expandConstants(List<WhileyFile> files) {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();		
		long memory = runtime.freeMemory();				
		new ConstantExpansion(loader).expand(files);
		
		logTimedMessage("expanded constants",
				System.currentTimeMillis() - start, memory - runtime.freeMemory());				
	}
	
	private Module buildModule(WhileyFile wf) {		
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();		
		long memory = runtime.freeMemory();		
		new TypePropagation(loader, nameResolver).propagate(wf);
		Module m = new CodeGeneration(loader).generate(wf);		
		logTimedMessage("[" + wf.filename + "] generated wyil module",
				System.currentTimeMillis() - start, memory - runtime.freeMemory());
		return m;
	}	
	
	/**
	 * This method is just a helper to format the output
	 */
	public void logTimedMessage(String msg, long time, long memory) {
		logout.print(msg);
		logout.print(" ");
		double mem = memory;
		mem = mem / (1024*1024);
		memory = (long) mem;
		String stats = " [" + Long.toString(time) + "ms";
		if(memory > 0) {
			stats += "+" + Long.toString(memory) + "mb]";
		} else if(memory < 0) {
			stats += Long.toString(memory) + "mb]";
		} else {
			stats += "]";
		}
		for (int i = 0; i < (90 - msg.length() - stats.length()); ++i) {
			logout.print(".");
		}		
		logout.println(stats);
	}	
	
	public void logTotalTime(String msg, long time, long memory) {
		memory = memory / 1024;
		
		for (int i = 0; i <= 90; ++i) {
			logout.print("=");
		}
		
		logout.println();
		
		logout.print(msg);
		logout.print(" ");

		double mem = memory;
		mem = mem / (1024*1024);
		memory = (long) mem;
		String stats = " [" + Long.toString(time) + "ms";
		if(memory > 0) {
			stats += "+" + Long.toString(memory) + "mb]";
		} else if(memory < 0) {
			stats += Long.toString(memory) + "mb]";
		} else {
			stats += "]";
		}

		for (int i = 0; i < (90 - msg.length() - stats.length()); ++i) {
			logout.print(".");
		}
		
		logout.println(stats);		
	}	
}
