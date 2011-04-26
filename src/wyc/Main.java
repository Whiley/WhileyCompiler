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
import java.net.*;
import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.stages.ConstantPropagation;
import wyil.stages.DefiniteAssignment;
import wyil.stages.FunctionCheck;
import wyil.stages.TypePropagation;
import wyil.util.*;
import wyjc.compiler.ClassWriter;
import wyjc.compiler.JvmBytecodeWriter;
import wyc.compiler.*;
import wyc.compiler.Compiler;
import wyc.util.*;

public class Main {
	public static final int PARSE_ERROR = 1;
	public static final int CONTEXT_ERROR = 2;
	public static final int VERIFICATION_ERROR = 3;
	public static final int RUNTIME_ERROR = 4;
	public static final int UNKNOWN_ERROR = 5;
	
	public static PrintStream errout;
	public static final int MAJOR_VERSION;
	public static final int MINOR_VERSION;
	public static final int MINOR_REVISION;
	public static final int BUILD_NUMBER;
	
	static {
		try {
			errout = new PrintStream(System.err, true, "UTF8");
		} catch(Exception e) {
			errout = System.err;
		}
		
		// determine version numbering from the MANIFEST attributes
		String versionStr = Main.class.getPackage().getImplementationVersion();
		if(versionStr != null) {
			String[] vb = versionStr.split("-");
			String[] pts = vb[0].split("\\.");
			BUILD_NUMBER = Integer.parseInt(vb[1]);
			MAJOR_VERSION = Integer.parseInt(pts[0]);
			MINOR_VERSION = Integer.parseInt(pts[1]);
			MINOR_REVISION = Integer.parseInt(pts[2]);
		} else {
			System.err.println("WARNING: version numbering unavailable");
			MAJOR_VERSION = 0;
			MINOR_VERSION = 0;
			MINOR_REVISION = 0;
			BUILD_NUMBER = 0;
		}
	}
	
	public int run(String[] args) {		
		boolean verbose = false;
		boolean verification = false;
		
		ArrayList<String> whileypath = new ArrayList<String>();
		ArrayList<String> bootpath = new ArrayList<String>();
		ArrayList<PipelineModifier> pipelineMods = new ArrayList();		
		int fileArgsBegin = 0;
		
		for (int i = 0; i != args.length; ++i) {
			if (args[i].startsWith("-")) {
				String arg = args[i];
				if(arg.equals("-help")) {
					usage();
					System.exit(0);
				} else if (arg.equals("-version")) {					
					System.out.println("Whiley-to-Java Compiler (wyjc) version " + MAJOR_VERSION + "."
							+ MINOR_VERSION + "." + MINOR_REVISION + " (build "
							+ BUILD_NUMBER + ")");				
					System.exit(0);
				} else if(arg.equals("-wp") || arg.equals("-whileypath")) {
					Collections.addAll(whileypath, args[++i]
							.split(File.pathSeparator));															
				} else if(arg.equals("-bp") || arg.equals("-bootpath")) {
					Collections.addAll(bootpath, args[++i]
					        							.split(File.pathSeparator));															
					        				} else if (arg.equals("-verbose")) {
					verbose = true;
				} else if(arg.startsWith("-X")) { 
					String[] name = args[i].substring(2).split(":");
					Map<String, String> options = Collections.EMPTY_MAP;
					if (name.length > 1) {
						options = splitOptions(name[1]);
					}
					PipelineModifier pmod = new PipelineModifier(POP.APPEND,
							name[0], null, options);
					pipelineMods.add(pmod);	
				} else if(arg.startsWith("-C")) { 
					String[] name = args[i].substring(2).split(":");
					Map<String, String> options = Collections.EMPTY_MAP;
					if (name.length > 1) {
						options = splitOptions(name[1]);
					}
					PipelineModifier pmod = new PipelineModifier(POP.REPLACE,
							name[0], null, options);
					pipelineMods.add(pmod);	
				} else if(arg.startsWith("-N")) { 
					String name = args[i].substring(2);					
					PipelineModifier pmod = new PipelineModifier(POP.REMOVE,
							name, null, null);
					pipelineMods.add(pmod);
				} else if(arg.equals("-V")) { 
					verification = true;
				} else {
					throw new RuntimeException("Unknown option: " + args[i]);
				}

				fileArgsBegin = i + 1;
			} 
		}
		
		if(fileArgsBegin == args.length) {
			usage();
			return UNKNOWN_ERROR;
		}
		
		if(bootpath.isEmpty()) {
			// In this case, no explicit bootpath has been specified on the
			// command-line. The challenge is that we want to automatically put
			// the wyrt.jar (Whiley Runtime Library) on the bootpath. To do
			// this, we want to try and determine the jarfile that was used to
			// get us to this point. Typically,
			// "java -jar wyjc.jar file.whiley". We can use wyjc.jar in place of
			// wyrt.jar, as it contains the same things.
			//
			try {
				// String jarfile = Main.class.getPackage().getImplementationTitle();
				// bootpath.add(jarfile);
				
				URI location = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();								
				if(location != null) {
					// The following code is a hack to determine the location of
					// the enclosing jar file.
					String jarfile = location.toURL().getFile().toString();					
					if(!jarfile.endsWith(".jar")) {
						// This seems to happen when calling from the ant task.
						// For some reason, despite me asking it to use a
						// particular jar file, it does not. Instead, it loads
						// using the CLASSPATH environment variable, which means
						// "."
						jarfile += "stdlib";
					}
					bootpath.add(jarfile);
				}				
			} catch(Exception e) {
				// just ignore.
			}
		}
		
		whileypath.add(0,".");
		whileypath.addAll(bootpath);

		try {
			ModuleLoader loader = new ModuleLoader(whileypath);
			List<Compiler.Stage> stages = constructPipeline(pipelineMods,loader,verification);
			Compiler compiler = createCompiler(loader,stages);

			// Now, configure compiler and loader
			loader.setLogger(compiler);
			loader.setClosedWorldAssumption(true);

			if(verbose) {
				compiler.setLogOut(System.err);
			}

			try {
				ArrayList<File> files = new ArrayList<File>();
				for(int i=fileArgsBegin;i!=args.length;++i) {
					files.add(new File(args[i]));
				}				
				compiler.compile(files);							
			} catch (ParseError e) {				
				if(e.filename() != null) {
					outputSourceError(e.filename(), e.start(), e.end(), e.getMessage());
				} else {
					System.err.println("syntax error (" + e.getMessage() + ").");
				}

				if(verbose) {
					e.printStackTrace(errout);
				}

				return PARSE_ERROR;
			} catch (VerificationError e) {				
				if(e.filename() != null) {
					outputSourceError(e.filename(), e.start(), e.end(), e.getMessage());
				} else {
					System.err.println("syntax error (" + e.getMessage() + ").");
				}

				if(verbose) {
					e.printStackTrace(errout);
				}

				return VERIFICATION_ERROR;
			} catch (SyntaxError e) {				
				if(e.filename() != null) {
					outputSourceError(e.filename(), e.start(), e.end(), e.getMessage());
				} else {
					System.err.println("syntax error (" + e.getMessage() + ").");
				}

				if(verbose) {
					e.printStackTrace(errout);
				}

				return CONTEXT_ERROR;
			} 
		} catch(Exception e) {			
			errout.println("Error: " + e.getMessage());
			if(verbose) {
				e.printStackTrace(errout);
			}
			return UNKNOWN_ERROR;
		}

		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		System.exit(new Main().run(args));			
	}
			
	/**
	 * Print out information regarding command-line arguments
	 * 
	 */
	public void usage() {
		String[][] info = {
				{ "version", "Print version information" },
				{ "verbose",
						"Print detailed information on what the compiler is doing" },
				{ "whileypath <path>",
						"Specify where to find whiley (class) files" },
				{ "wp <path>", "Specify where to find whiley (class) files" },
				{ "bootpath <path>",
						"Specify where to find whiley standard library (class) files" },
				{ "bp <path>",
						"Specify where to find whiley standard library (class) files" }};
		System.out.println("usage: wyjc <options> <source-files>");
		System.out.println("Options:");

		// first, work out gap information
		int gap = 0;

		for (String[] p : info) {
			gap = Math.max(gap, p[0].length() + 5);
		}

		// now, print the information
		for (String[] p : info) {
			System.out.print("  -" + p[0]);
			int rest = gap - p[0].length();
			for (int i = 0; i != rest; ++i) {
				System.out.print(" ");
			}
			System.out.println(p[1]);
		}
	}	
	
	/**
	 * This method simply reads in the input file, and prints out a
	 * given line of text, with little markers (i.e. '^') placed
	 * underneath a portion of it.  
	 *
	 * @param fileArg - the name of the file whose line to print
	 * @param start - the start position of the offending region.
	 * @param end - the end position of the offending region.
	 * @param message - the message to print about the error
	 */
	public void outputSourceError(String fileArg, int start, int end,
			String message) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(fileArg), "UTF8"));
						
		int line = 0;
		String lineText = "";
		while (in.ready() && start >= lineText.length()) {
			start -= lineText.length() + 1;
			end -= lineText.length() + 1;
			lineText = in.readLine();						
			line = line + 1;			
		}		
								
		errout.println(fileArg + ":" + line + ": " + message);
		//errout.println();
		errout.println(lineText);	
		for (int i = 0; i <= start; ++i) {
			if (lineText.charAt(i) == '\t') {
				errout.print("\t");
			} else {
				errout.print(" ");
			}
		}				
		for (int i = start; i <= end; ++i) {		
			errout.print("^");
		}
		errout.println("");		
	}

	protected Compiler createCompiler(ModuleLoader loader,
			List<Compiler.Stage> stages) {
		return new Compiler(loader, stages);
	}
	
	/**
	 * This splits strings of the form "x=y,v=w" into distinct components and
	 * puts them into a map. In the case of a string like "x,y=z" then x is
	 * loaded with the empty string.
	 * 
	 * @param str
	 * @return
	 */
	protected Map<String, String> splitOptions(String str) {
		HashMap<String, String> options = new HashMap<String, String>();
		String[] splits = str.split(",");
		for (String s : splits) {
			String[] p = s.split("=");
			if (p.length == 1) {
				options.put(p[0], "");
			} else {
				options.put(p[0], p[1]);
			}
		}
		return options;
	}
}
