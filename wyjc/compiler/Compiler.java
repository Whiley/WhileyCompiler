// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjc.compiler;

import java.io.*;
import java.util.*;

import wyil.*;
import wyil.lang.*;
import wyil.util.*;
import wyil.stages.*;
import wyil.io.*;
import wyjc.lang.*;
import wyjc.stages.*;
import wyjvm.io.ClassFileWriter;
import wyjvm.lang.ClassFile;

public class Compiler implements Logger {	
	private ModuleLoader loader;
	protected NameResolution nameResolver;
	protected ModuleBuilder typeResolver;		
	protected ArrayList<Stage> stages;
	protected ArrayList<Writer> writers;

	public interface Stage {
		public Module process(Module module, Logger logout);
	}
	public interface Writer {
		public void write(Module module, Logger logout);
	}
		
	protected int debugMode = 0;
	
	public static final int DEBUG_LEXER = 1;
	public static final int DEBUG_PARSER = 2;	
	public static final int DEBUG_CHECKS = 4;
	public static final int DEBUG_PCS = 8;
	public static final int DEBUG_VCS = 16;
			
	public Compiler(ModuleLoader loader) {
		this.loader = loader;		
		this.nameResolver = new NameResolution(loader);		
		this.typeResolver = new ModuleBuilder(loader);	
	}
	
	/**
	 * The logout output stream is used to write log information about the
	 * status of compilation. The default stream just discards everything.
	 */
	protected PrintStream logout = new PrintStream(new OutputStream() {
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
	public void setDebugMode(int debugMode) {
		this.debugMode = debugMode;
	}

	public List<WhileyFile> compile(List<File> files) throws IOException {
		ArrayList<WhileyFile> wyfiles = new ArrayList<WhileyFile>();
		for (File f : files) {
			WhileyFile wf = innerParse(f);			
			wyfiles.add(wf);			
			loader.preregister(wf.skeleton(),wf.filename);			
		}
				
		for (WhileyFile m : wyfiles) {
			resolveNames(m);			
		}
		
		List<Module> modules = buildModules(wyfiles);		
		for(Module m : modules) {
			finishCompilation(m);
		}
		
		return wyfiles;
	}
		
	/**
	 * This method simply parses a whiley file into an abstract syntax tree. It
	 * makes little effort to check whether or not the file is syntactically. In
	 * particular, it does not determine the correct type of all declarations,
	 * expressions, etc.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public WhileyFile innerParse(File file) throws IOException {
		long start = System.currentTimeMillis();	
		WhileyLexer wlexer = new WhileyLexer(file.getPath());		
		List<WhileyLexer.Token> tokens = wlexer.scan();		

		if((debugMode & DEBUG_LEXER) != 0) {
			debugLexer(tokens);
		}
		
		WhileyParser wfr = new WhileyParser(file.getPath(),tokens);	
		logTimedMessage("[" + file + "] Parsing complete", System
				.currentTimeMillis()
				- start);
		
		return wfr.read(); 
	}		
	
	/**
	 * This method puts the given module through the second half of the
	 * compilation pipeline. In particular, it propagates and generates types
	 * for all expressions used within the module, as well as checking for
	 * definite assignment and performing verification checking.
	 * 
	 * @param wf
	 */
	public void finishCompilation(Module module) {				
		// Register the updated file
		loader.register(module);
				
		writeOutputFile(module);		
	}
	
	
	protected void resolveNames(WhileyFile m) {
		long start = System.currentTimeMillis();		
		nameResolver.resolve(m);
		logTimedMessage("[" + m.filename + "] resolved names",
				System.currentTimeMillis() - start);		
		
	}
	
	protected List<Module> buildModules(List<WhileyFile> files) {
		long start = System.currentTimeMillis();		
		List<Module> modules = typeResolver.resolve(files);
		logTimedMessage("resolved types",
				System.currentTimeMillis() - start);
		return modules;		
	}
	
	/**
	 * This method is just a helper to format the output
	 */
	public void logTimedMessage(String msg, long time) {
		logout.print(msg);
		logout.print(" ");

		String t = Long.toString(time);

		for (int i = 0; i < (80 - msg.length() - t.length()); ++i) {
			logout.print(".");
		}
		logout.print(" [");
		logout.print(time);
		logout.println("ms]");
	}
	
	private void debugLexer(List<WhileyLexer.Token> tokens) {
		for(WhileyLexer.Token t : tokens) {
			if(t instanceof WhileyLexer.NewLine) {
				System.out.println("NewLine:" + t.start);
			} else if(t instanceof WhileyLexer.Tabs) {
				WhileyLexer.Tabs ts = (WhileyLexer.Tabs) t;
				System.out.println("Tabs(" + ts.ntabs + "):" + t.start);
			} else {
				String name = t.getClass().getName().substring(26);
				System.err.println(name +  ":" + t.start + " \"" + t.text + "\"");
			}
		}
	}
	
	/*
	private void debugChecks(Module wf) {
		long start = System.currentTimeMillis();
		String filename = stripExtension(wf.filename) + ".debug";
		try {
			PrintWriter printer = new PrintWriter(filename,"UTF8");
			CodeWriter writer = new CodeWriter(printer);
			writer.setWriteChecks();
			writer.write(wf);
			logTimedMessage("Wrote " + filename,System.currentTimeMillis()-start);
			printer.close();
		} catch(IOException e) {
			logTimedMessage("Failed writing " + filename,System.currentTimeMillis()-start);
		}
	}
	
	private void debugPCS(Module wf) {
		long start = System.currentTimeMillis();
		String filename = stripExtension(wf.filename()) + ".debug";
		try {
			PrintWriter printer = new PrintWriter(filename,"UTF8");
			CodeWriter writer = new CodeWriter(printer);
			writer.setWriteProofs();
			writer.write(wf);
			logTimedMessage("Wrote " + filename,System.currentTimeMillis()-start);
			printer.close();
		} catch(IOException e) {
			logTimedMessage("Failed writing " + filename,System.currentTimeMillis()-start);
		}
	}
	
	private void debugVCS(Module wf) {
		long start = System.currentTimeMillis();
		String filename = stripExtension(wf.filename()) + ".debug";
		try {
			PrintWriter printer = new PrintWriter(filename,"UTF8");
			CodeWriter writer = new CodeWriter(printer);
			writer.setWriteVerificationConditions();
			writer.write(wf);
			logTimedMessage("Wrote " + filename,System.currentTimeMillis()-start);
			printer.close();
		} catch(IOException e) {
			logTimedMessage("Failed writing " + filename,System.currentTimeMillis()-start);
		}
	}
	*/
	
	private static String stripExtension(String input) {
		int li = input.lastIndexOf('.');
		if(li == -1) { return input; } // had no extension?
		else {
			return input.substring(0,li);
		}
	}		
}
