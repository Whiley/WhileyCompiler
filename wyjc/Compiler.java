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

package wyjc;

import java.io.*;
import java.util.*;

import wyil.stages.ClassFileBuilder;
import wyil.stages.CodeWriter;
import wyil.stages.DefiniteAssignmentChecker;
import wyil.stages.FunctionChecker;
import wyil.stages.PostConditionGenerator;
import wyil.stages.RuntimeCheckGenerator;
import wyil.stages.VerificationConditionChecker;
import wyil.stages.VerificationConditionGenerator;
import wyjc.ast.ResolvedWhileyFile;
import wyjc.ast.UnresolvedWhileyFile;
import wyjc.stages.*;
import wyjc.util.ModuleID;
import wyjvm.io.ClassFileWriter;
import wyjvm.lang.ClassFile;

public class Compiler {	
	private ModuleLoader loader;
	protected HashSet<ModuleID> enqueued = new HashSet<ModuleID>();
	protected ArrayList<UnresolvedWhileyFile> queue = new ArrayList<UnresolvedWhileyFile>();	
	
	protected NameResolution nameResolver;
	protected TypeResolution typeResolver;		
	protected ResolvedFileBuilder fileBuilder = new ResolvedFileBuilder();
	protected DefiniteAssignmentChecker defAssignChecker = new DefiniteAssignmentChecker();
	protected FunctionChecker functionChecker = new FunctionChecker();
	protected ReturnValueChecker returnValueChecker = new ReturnValueChecker();
	protected RuntimeCheckGenerator checkGenerator;
	protected PostConditionGenerator postGenerator = new PostConditionGenerator();
	protected VerificationConditionGenerator vcGenerator;	
	protected VerificationConditionChecker vcChecker = new VerificationConditionChecker();	
	protected ClassFileBuilder classBuilder;	
	
	protected boolean verificationFlag = true; // verification is enabled	
	protected boolean runtimeChecksFlag = true; // runtime checks enabled
	protected int debugMode = 0;
	
	public static final int DEBUG_LEXER = 1;
	public static final int DEBUG_PARSER = 2;	
	public static final int DEBUG_CHECKS = 4;
	public static final int DEBUG_PCS = 8;
	public static final int DEBUG_VCS = 16;
			
	public Compiler(ModuleLoader loader, int whileyMajorVersion, int whileyMinorVersion) {
		this.loader = loader;
		this.typeResolver = new TypeResolution(loader);
		this.nameResolver = new NameResolution(loader);
		this.checkGenerator = new RuntimeCheckGenerator(loader);
		this.classBuilder = new ClassFileBuilder(loader,whileyMajorVersion,whileyMinorVersion);
		this.vcGenerator = new VerificationConditionGenerator(loader);
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
	
	public void setVerification(boolean flag) {
		this.verificationFlag = flag;
	}
	public void setRuntimeChecks(boolean flag) {
		this.runtimeChecksFlag = flag;
	}
	public void setDebugMode(int debugMode) {
		this.debugMode = debugMode;
	}
	
	public void compile(File file) throws IOException {
		UnresolvedWhileyFile uwf = innerParse(file);		
		loader.preregister(uwf.skeletonInfo(),uwf.filename());	
		enqueue(uwf);				
		flushQueue();			
	}
	
	public UnresolvedWhileyFile parse(File file) throws IOException {
		UnresolvedWhileyFile uwf = innerParse(file);		
		loader.preregister(uwf.skeletonInfo(),uwf.filename());						
		enqueue(uwf);
		return uwf;
	}
			
	public void enqueue(UnresolvedWhileyFile file) {
		if(!enqueued.contains(file.id())) {				
			enqueued.add(file.id());
			queue.add(file);
			resolveNames(file);
		}
	}
	
	public List<UnresolvedWhileyFile> compile(List<File> files) throws IOException {
		ArrayList<UnresolvedWhileyFile> modules = new ArrayList<UnresolvedWhileyFile>();
		for (File f : files) {
			UnresolvedWhileyFile m = innerParse(f);			
			modules.add(m);			
			loader.preregister(m.skeletonInfo(),m.filename());
			enqueued.add(m.id());
			queue.add(m);
		}
		
		for (UnresolvedWhileyFile m : modules) {
			resolveNames(m);			
		}
		
		flushQueue();
		
		return modules;
	}
	
	public void flushQueue() {
		resolveTypes(queue);
		
		while (!queue.isEmpty()) {
			UnresolvedWhileyFile m = queue.remove(0);
			finishCompilation(m);
		}
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
	public UnresolvedWhileyFile innerParse(File file) throws IOException {
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
	 * @param uwf
	 */
	public void finishCompilation(UnresolvedWhileyFile uwf) {				
		
		// Resolve the file
		ResolvedWhileyFile rwf = buildResolvedFile(uwf);
		
		// Register the updated file
		loader.register(rwf.moduleInfo());
		
		// Perform definite assignment analysis
		defAssignment(rwf);				
		
		// Check functional behaviour
		funCheck(rwf);				
				
		// Perform return value check
		returnCheck(rwf);
		
		if (runtimeChecksFlag) {
			// Generate runtime checks
			generateChecks(rwf);
		
			// Generate post conditions
			generatePostConditions(rwf);

			// Generate verification conditions
			generateVerificationConditions(rwf);

			if(verificationFlag) {
				// Check verification conditions (if applicable)
				checkVerificationConditions(rwf);
			}			
		}
	
		writeClassFile(rwf);		
	}
	
	
	protected void resolveNames(UnresolvedWhileyFile m) {
		long start = System.currentTimeMillis();		
		nameResolver.resolve(m);
		logTimedMessage("[" + m.filename() + "] resolved names",
				System.currentTimeMillis() - start);		
		
	}
	
	protected void resolveTypes(List<UnresolvedWhileyFile> files) {
		long start = System.currentTimeMillis();		
		typeResolver.resolve(files);
		logTimedMessage("resolved types",
				System.currentTimeMillis() - start);		
		
	}
	
	protected ResolvedWhileyFile buildResolvedFile(UnresolvedWhileyFile m) {
		long start = System.currentTimeMillis();		
		ResolvedWhileyFile rwf = fileBuilder.build(m);
		logTimedMessage("[" + m.filename() + "] built resolved file",
				System.currentTimeMillis() - start);
		return rwf;
	}
	
	protected void defAssignment(ResolvedWhileyFile m) {
		long start = System.currentTimeMillis();	
		defAssignChecker.verify(m);
		logTimedMessage("[" + m.filename() + "] definite assignment checked",
				System.currentTimeMillis() - start);
	}
	
	protected void funCheck(ResolvedWhileyFile m) {
		long start = System.currentTimeMillis();	
		functionChecker.check(m);
		logTimedMessage("[" + m.filename() + "] functional behaviour checked",
				System.currentTimeMillis() - start);
	}
	
	protected void returnCheck(ResolvedWhileyFile m) {
		long start = System.currentTimeMillis();	
		returnValueChecker.check(m);
		logTimedMessage("[" + m.filename() + "] return value checked",
				System.currentTimeMillis() - start);
	}
	
	protected void generateChecks(ResolvedWhileyFile m) {
		long start = System.currentTimeMillis();	
		checkGenerator.checkgen(m);
		logTimedMessage("[" + m.filename() + "] runtime checks generated",
				System.currentTimeMillis() - start);

		if((debugMode & DEBUG_CHECKS) != 0) {
			debugChecks(m);
		}
		
	}
	
	protected void generatePostConditions(ResolvedWhileyFile m) {
		long start = System.currentTimeMillis();	
		postGenerator.generate(m);
		logTimedMessage("[" + m.filename() + "] postconditions generated",
				System.currentTimeMillis() - start);
		if((debugMode & DEBUG_PCS) != 0) {
			debugPCS(m);
		}
	}
	
	protected void generateVerificationConditions(ResolvedWhileyFile m) {
		long start = System.currentTimeMillis();	
		vcGenerator.generate(m);
		logTimedMessage("[" + m.filename() + "] verification conditions generated",
				System.currentTimeMillis() - start);
		if((debugMode & DEBUG_VCS) != 0) {
			debugVCS(m);
		}
	}
	
	protected void checkVerificationConditions(ResolvedWhileyFile m) {
		long start = System.currentTimeMillis();	
		vcChecker.verify(m);
		logTimedMessage("[" + m.filename() + "] verification conditions checked",
				System.currentTimeMillis() - start);
	}
	
	protected void writeClassFile(ResolvedWhileyFile m) {
		long start = System.currentTimeMillis();
		ClassFile file = classBuilder.build(m);		
		// calculate filename
		String filename = m.filename().replace(".whiley", ".class");
		try {
			FileOutputStream out = new FileOutputStream(filename);
			ClassFileWriter writer = new ClassFileWriter(out,null);
			writer.write(file);
			logTimedMessage("[" + m.filename() + "] class file written",
					System.currentTimeMillis() - start);
		} catch(IOException ex) {
			logTimedMessage("[" + m.filename()
					+ "] failed writing class file (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start);
		}
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
	
	private void debugChecks(ResolvedWhileyFile wf) {
		long start = System.currentTimeMillis();
		String filename = stripExtension(wf.filename()) + ".debug";
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
	
	private void debugPCS(ResolvedWhileyFile wf) {
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
	
	private void debugVCS(ResolvedWhileyFile wf) {
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
	
	private static String stripExtension(String input) {
		int li = input.lastIndexOf('.');
		if(li == -1) { return input; } // had no extension?
		else {
			return input.substring(0,li);
		}
	}		
}
