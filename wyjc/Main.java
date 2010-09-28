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

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.stages.*;
import wyil.util.*;
import wyjc.compiler.*;
import wyjc.compiler.Compiler;
import wyjc.util.*;

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
	
	public static int run(String[] args) {		
		boolean verbose = false;
		
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
			String jarfile = Main.class.getPackage().getImplementationTitle();
			bootpath.add(jarfile);
		}
		
		whileypath.add(0,".");
		whileypath.addAll(bootpath);

		try {
			ModuleLoader loader = new ModuleLoader(whileypath);
			ArrayList<Compiler.Stage> stages = constructPipeline(pipelineMods,loader);
			Compiler compiler = new Compiler(loader,stages);

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
		System.exit(run(args));			
	}
			
	/**
	 * Print out information regarding command-line arguments
	 * 
	 */
	public static void usage() {
		String[][] info = {
				{ "version", "Print version information" },
				{ "verbose",
						"Print detailed information on what the compiler is doing" },				
				{ "Nvc",
						"Don't check constraints at compile time" }, 
				{ "nrc",
				"Don't check constraints at runtime\n" } ,				
				{"whileypath <path>", "Specify where to find whiley (class) files"},
				{"wp <path>", "Specify where to find whiley (class) files"},
				{"bootpath <path>",
				"Specify where to find whiley standard library (class) files"},
				{"bp <path>", "Specify where to find whiley standard library (class) files"},				
				{ "debug:lexer",
				"Generate debug information for the lexer" },
				{ "debug:checks",
				"Generate debug information on generated checks" },
				{ "debug:pcs",
				"Generate debug information on propagated conditions" },
				{ "debug:vcs",
				"Generate debug information on verification conditions" }};
		System.out.println("usage: wjc <options> <source-files>");
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
	public static void outputSourceError(String fileArg, int start, int end,
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

	/**
	 * This splits strings of the form "x=y,v=w" into distinct components and
	 * puts them into a map. In the case of a string like "x,y=z" then x is
	 * loaded with the empty string.
	 * 
	 * @param str
	 * @return
	 */
	private static Map<String, String> splitOptions(String str) {
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

	private static ArrayList<Compiler.Stage> constructPipeline(
			List<PipelineModifier> pmods, ModuleLoader loader) {

		ArrayList<Compiler.Stage> stages = new ArrayList<Compiler.Stage>();
		
		// First, construct the default pipeline
		stages.add(new WyilWriter(loader,Collections.EMPTY_MAP));
		stages.add(new WyilTransform("dispatch inline", new PreconditionInline(
				loader)));
		stages.add(new WyilTransform("type propagation", new TypePropagation(
				loader)));
		stages.add(new WyilTransform("definite assignment",
				new DefiniteAssignment()));
		stages.add(new WyilTransform("constant propagation",
				new ConstantPropagation(loader)));
		stages.add(new WyilTransform("branch prediction",
				new ExpectedInference(loader)));
		stages.add(new WyilTransform("verification check",
				new ConstraintPropagation(loader, true, 1000)));
		stages.add(new WyilTransform("function check",
				new FunctionCheck(loader)));
		stages
				.add(new WyilTransform("failure check",
						new FailureCheck(loader)));
		stages.add(new ClassWriter(loader));
		
		// Second, make requested pipeline adjustments
		registerDefaultStages();
		for (PipelineModifier p : pmods) {
			switch(p.op) {
			case APPEND:
				stages.add(Compiler.constructStage(p.name,loader,p.options));
				break;
			case REPLACE:
			{
				int index = matchStage(p.name,stages);
				stages.set(index,Compiler.constructStage(p.name,loader,p.options));
				break;
			}
			case REMOVE:
			{
				int index = matchStage(p.name,stages);
				stages.remove(index);
				break;			
			}
			}			
		}
		
		return stages;
	}
	

	public static void registerDefaultStages() {
		Compiler.registerStage("wyil",WyilWriter.class);
		Compiler.registerStage("jvm",JvmBytecodeWriter.class);
		Compiler.registerStage("class",ClassWriter.class);
	}
	
	private static int matchStage(String match, List<Compiler.Stage> stages) {
		int i=0;
		for(Compiler.Stage stage : stages) {
			if(matchStageName(match,stage.name())) {
				return i;
			}
			++i;
		}
		throw new IllegalArgumentException("invalid stage name \"" + match + "\"");
	}
	
	private static boolean matchStageName(String match, String name) {
		if(match.equals(name) || name.startsWith(match)) {
			return true;
		}
		String initials = splitInitials(name);
		if(match.equals(initials)) {
			return true;
		}
		return false;
	}
	
	private static String splitInitials(String name) {
		String[] words = name.split(" ");
		String r = "";
		for(String w : words) {
			r += w.charAt(0);
		}
		return r;
	}
	
	/**
	 * The pipeline modifier captures a requested adjustment to the compilation
	 * pipeline.
	 * 
	 * @author djp
	 */
	private static class PipelineModifier {
		public final POP op;
		public final String name;
		public final String arg;
		public final Map<String,String> options;
		
		public PipelineModifier(POP pop, String name, String arg,
				Map<String, String> options) {
			this.op = pop;
			this.name = name;
			this.arg = arg;
			this.options = options;
		}
	}
	
	private enum POP {
		APPEND,
		BEFORE,
		AFTER,
		REPLACE,
		REMOVE
	}
}
