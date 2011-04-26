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

package wyjc.compiler;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import wyil.*;
import wyil.lang.*;
import wyil.stages.TypePropagation;
import wyil.util.*;
import wyjc.lang.*;
import wyjc.stages.*;

public class Compiler implements Logger {	
	protected ModuleLoader loader;
	protected ArrayList<Stage> stages;

	public interface Stage {
		public String name();
		public Module process(Module module, Logger logout);
	}
	
	public Compiler(ModuleLoader loader, List<Stage> stages) {
		this.loader = loader;		
		this.stages = new ArrayList<Stage>(stages);				
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

	public List<WhileyFile> compile(List<File> files) throws IOException {
		long startTime = System.currentTimeMillis();
		
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
			loader.register(m);
		}
		for(Module m : modules) {
			finishCompilation(m);
		}
		
		long endTime = System.currentTimeMillis();
		logTotalTime("Compiled " + files.size() + " file(s)",endTime-startTime);
		
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
		List<WhileyLexer.Token> tokens = new WhileyFilter().filter(wlexer.scan());		

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
		
		for(Stage s : stages) {
			module = s.process(module,this);
		}		
	}
		
	protected void resolveNames(WhileyFile m) {
		long start = System.currentTimeMillis();		
		new NameResolution(loader).resolve(m);
		logTimedMessage("[" + m.filename + "] resolved names",
				System.currentTimeMillis() - start);		
		
	}
	
	protected List<Module> buildModules(List<WhileyFile> files) {
		long start = System.currentTimeMillis();		
		List<Module> modules = new ModuleBuilder(loader).resolve(files);
		logTimedMessage("built modules",
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
	
	public void logTotalTime(String msg, long time) {
		
		for (int i = 0; i <= 85; ++i) {
			logout.print("=");
		}
		
		logout.println();
		
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
	
	protected static final HashMap<String, Class<? extends Stage>> registeredStages = new HashMap<String, Class<? extends Stage>>();

	/**
	 * Register a compiler stage with the system. A compiler stage requires a
	 * constructor which accepts a ModuleLoader, and Map<String,String> arguments
	 * 
	 * @param handle
	 * @param stage
	 */
	public static void registerStage(String handle, Class<? extends Stage> stage) {
		try {			
			Constructor<? extends Stage> c = stage.getConstructor(
					ModuleLoader.class, Map.class);			
			registeredStages.put(handle, stage);
			return;
		} catch(NoSuchMethodException e) {
		} 
		throw new IllegalArgumentException("cannot register stage \""
				+ handle + "\" - missing required constructor");
	}

	/**
	 * Construct an instance of a given compiler stage, using the given argument
	 * list. A constructor which accepts a ModuleLoader, and Map<String,String>
	 * arguments will be called. If such a constructor doesn't exist, an
	 * exception will be raised.
	 * 
	 * @param handle
	 * @param options
	 * @return
	 */
	public static Stage constructStage(String handle, ModuleLoader loader,
			Map<String, String> options) {
		
		Class<? extends Stage> sc = registeredStages.get(handle);
		
		try {
			if (sc != null) {
				Constructor<? extends Stage> c = sc.getConstructor(
						ModuleLoader.class, Map.class);
				Stage stage = (Stage) c.newInstance(loader, options);
				return stage;
			}
		} catch(NoSuchMethodException e) {
		} catch(InstantiationException e) {
		} catch(InvocationTargetException e) {
		} catch(IllegalAccessException e) {					
		}
		
		throw new IllegalArgumentException("invalid stage " + handle);
	}	
}
