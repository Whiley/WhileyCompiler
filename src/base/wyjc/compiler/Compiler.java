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
import java.lang.reflect.*;

import wyil.*;
import wyil.lang.*;
import wyil.stages.TypePropagation;
import wyil.util.*;
import wyil.io.*;
import wyjc.lang.*;
import wyjc.stages.*;
import wyjvm.io.ClassFileWriter;
import wyjvm.lang.ClassFile;

public class Compiler implements Logger {	
	private ModuleLoader loader;
	protected NameResolution nameResolver;
	protected ModuleBuilder moduleBuilder;
	protected TypePropagation typeInference;
	protected ArrayList<Stage> stages;

	public interface Stage {
		public String name();
		public Module process(Module module, Logger logout);
	}
	
	public Compiler(ModuleLoader loader, List<Stage> stages) {
		this.loader = loader;		
		this.stages = new ArrayList<Stage>(stages);
		this.nameResolver = new NameResolution(loader);		
		this.moduleBuilder = new ModuleBuilder(loader);
		this.typeInference = new TypePropagation(loader);	
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
		modules = typeModules(modules);
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
		List<WhileyLexer.Token> tokens = wlexer.scan();		

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
		nameResolver.resolve(m);
		logTimedMessage("[" + m.filename + "] resolved names",
				System.currentTimeMillis() - start);		
		
	}
	
	protected List<Module> buildModules(List<WhileyFile> files) {
		long start = System.currentTimeMillis();		
		List<Module> modules = moduleBuilder.resolve(files);
		logTimedMessage("built modules",
				System.currentTimeMillis() - start);
		return modules;		
	}
	
	protected List<Module> typeModules(List<Module> files) {
		long start = System.currentTimeMillis();			
		for(Module m : files) {
			loader.register(m);
		}
		ArrayList<Module> modules = new ArrayList<Module>();
		for(Module m : files) {
			modules.add(typeInference.apply(m));									
			// modules.add(m);
		}		
		logTimedMessage("inferred types",
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
