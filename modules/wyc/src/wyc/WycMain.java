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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;

import wybs.lang.*;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.*;
import wyc.builder.Whiley2WyilBuilder;
import wyc.lang.WhileyFile;
import wyc.util.*;
import wyil.*;
import wyil.Pipeline.Template;
import wyil.lang.WyilFile;
import wyil.util.*;
import static wybs.lang.SyntaxError.*;

/**
 * The main class provides all of the necessary plumbing to process command-line
 * options, construct an appropriate pipeline and then instantiate the Whiley
 * Compiler to generate class files.
 * 
 * @author David J. Pearce
 * 
 */
public class WycMain {
	
	public static PrintStream errout;
	public static final int MAJOR_VERSION;
	public static final int MINOR_VERSION;
	public static final int MINOR_REVISION;
	public static final int BUILD_NUMBER;
	
	public static final int SUCCESS=0;
	public static final int SYNTAX_ERROR=1;
	public static final int INTERNAL_FAILURE=2;

	public static final OptArg[] DEFAULT_OPTIONS = new OptArg[]{
			new OptArg("help", "Print this help information"),
			new OptArg("version", "Print version information"),
			new OptArg("verbose",
					"Print detailed information on what the compiler is doing"),
			new OptArg("whileypath", "wp", OptArg.FILELIST,
					"Specify where to find whiley (binary) files",
					new ArrayList<String>()),
			new OptArg("bootpath", "bp", OptArg.FILELIST,
					"Specify where to find whiley standard library files",
					defaultBootPath()),
			new OptArg("whileydir", "wd", OptArg.FILEDIR,
					"Specify where to find whiley source files",
					new File(".")),
			new OptArg("wyildir", "od", OptArg.FILEDIR,
					"Specify where to place generated (wyil) binary files",
					new File(".")),
			new OptArg("X", OptArg.PIPELINECONFIGURE,
					"configure existing pipeline stage"),
			new OptArg("A", OptArg.PIPELINEAPPEND, "append new pipeline stage"),
			new OptArg("R", OptArg.PIPELINEREMOVE, "remove existing pipeline stage")};
	
	/**
	 * Initialise the error output stream so as to ensure it will display
	 * unicode characters (when possible). Additionally, extract version
	 * information from the enclosing jar file.
	 */
	static {
		try {
			errout = new PrintStream(System.err, true, "UTF8");
		} catch(Exception e) {
			errout = System.err;
		}
		
		// determine version numbering from the MANIFEST attributes
		String versionStr = WycMain.class.getPackage().getImplementationVersion();
		if(versionStr != null) {
			String[] vb = versionStr.split("-");
			String[] pts = vb[0].split("\\.");
			if(vb.length == 1) {
				BUILD_NUMBER=0;
			} else {
			BUILD_NUMBER = Integer.parseInt(vb[1]); }
			
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
				
	// =========================================================================
	// Instance Fields
	// =========================================================================
						
	/**
	 * The command-line options accepted by the main method.
	 */
	protected final OptArg[] options;

	
	/**
	 * The build-task responsible for actually compiling and building files.
	 */
    protected final WycBuildTask builder;
		
    // =========================================================================
 	// Constructors & Configuration
 	// =========================================================================
 	
    public WycMain(WycBuildTask builder, OptArg[] options) {
    	this.options = options;
    	this.builder = builder;
    }

    // =========================================================================
 	// Run Method
 	// =========================================================================
 	    
    public int run(String[] _args) {
		
	    // =====================================================================
	 	// Process Options
	 	// =====================================================================
    	
    	ArrayList<String> args = new ArrayList<String>(Arrays.asList(_args));
		Map<String, Object> values = OptArg.parseOptions(args, options);

		// Second, check if we're printing version
		if (values.containsKey("version")) {
			System.out.println("Whiley Compiler (wyc) version " + MAJOR_VERSION
					+ "." + MINOR_VERSION + "." + MINOR_REVISION + " (build "
					+ BUILD_NUMBER + ")");			
			return SUCCESS;
		}

		// Otherwise, if no files to compile specified, then print usage
		if (args.isEmpty() || values.containsKey("help")) {
			System.out.println("usage: wyc <options> <source-files>");
			OptArg.usage(System.out, options);
			usage(System.out, Pipeline.defaultPipeline);
			return SUCCESS;
		}

		// =====================================================================
		// Construct & Configure Build Task
		// =====================================================================

		boolean verbose = values.containsKey("verbose");
		builder.setVerbose(verbose);
		
		try {
				
			ArrayList<Pipeline.Modifier> pipelineModifiers = (ArrayList) values
					.get("pipeline");
			if(pipelineModifiers != null) {
				builder.setPipelineModifiers(pipelineModifiers);
			}
			
			File whileyDir = (File) values.get("whileydir");
			builder.setWhileyDir(whileyDir);			
			
			File wyilDir = (File) values.get("wyildir");
			builder.setWyilDir(wyilDir);			

			ArrayList<File> bootpath =  (ArrayList<File>) values.get("bootpath");
			builder.setBootPath(bootpath);

			ArrayList<File> whileypath = (ArrayList<File>) values.get("whileypath");
			builder.setWhileyPath(whileypath);

			ArrayList<File> delta = new ArrayList<File>();
			for(String arg : args) {
				delta.add(new File(arg));
			}
			
		// =====================================================================
		// Run Build Task
		// =====================================================================
			
			builder.build(delta);
			
		} catch (InternalFailure e) {
			e.outputSourceError(errout);
			if (verbose) {
				e.printStackTrace(errout);
			}
			return INTERNAL_FAILURE;
		} catch (SyntaxError e) {
			e.outputSourceError(errout);
			if (verbose) {
				e.printStackTrace(errout);
			}
			return SYNTAX_ERROR;
		} catch (Throwable e) {
			errout.println("internal failure (" + e.getMessage() + ")");
			if (verbose) {
				e.printStackTrace(errout);
			}
			return INTERNAL_FAILURE;
		}

		return SUCCESS;
	}
    
    // =========================================================================
 	// Helper Methods
 	// =========================================================================
 	    
	/**
	 * Print out the available list of options for the given pipeline 
	 */
	protected void usage(PrintStream out, List<Pipeline.Template> stages) {
		out.println("\nstage configuration:");
		for(Template template : stages) {
			Class<? extends Transform> t = template.clazz;
			out.println("  -X " + t.getSimpleName().toLowerCase() + ":\t");			
			for(Method m : t.getDeclaredMethods()) {
				String name = m.getName();
				if(name.startsWith("set")) {
					String shortName = name.substring(3).toLowerCase();
					out.print("    " + shortName + "(" + argValues(m) + ")");
					// print default value
					try {
						Method getter = t.getDeclaredMethod(name.replace("set", "get"));
						Object v = getter.invoke(null);						
						out.print("[default=" + v + "]");						
					} catch(NoSuchMethodException e) {
						// just ignore
					} catch (IllegalArgumentException e) {
						// just ignore
					} catch (IllegalAccessException e) {
						// just ignore						
					} catch (InvocationTargetException e) {
						// just ignore						
					}
					// print description
					try {
						Method desc = t.getDeclaredMethod(name.replace("set", "describe"));
						Object v = desc.invoke(null);
						out.print("\t" + v);
					} catch(NoSuchMethodException e) {
						// just ignore
					} catch (IllegalArgumentException e) {
						// just ignore
					} catch (IllegalAccessException e) {
						// just ignore						
					} catch (InvocationTargetException e) {
						// just ignore						
					}
					out.println();
				}				
			}			
		}
	}
	
	protected static String argValues(Method m) {
		String r = "";
		for(Class<?> p : m.getParameterTypes()) {
			if(p == boolean.class) {
				r = r + "boolean";
			} else if(p == int.class) {
				r = r + "int";
			} else if(p == String.class) {
				r = r + "string";
			}
		}
		return r;
	}	
	
    // =========================================================================
 	// Misc Methods
 	// ========================================================================= 	   
	
	/**
	 * In the case that no explicit bootpath has been specified on the
	 * command-line, we need to add a default location. The challenge is that we
	 * want to automatically put the wyrt.jar (Whiley Runtime Library) on the
	 * bootpath. To do this, we want to try and determine the jarfile that was
	 * used to get us to this point. Typically,
	 * <code>"java -jar wyjc.jar file.whiley"</code>. We can use
	 * <code>wyjc.jar</code> in place of <code>wyrt.jar</code>, as it contains
	 * the same things.
	 * 
	 * @param bootpath
	 */
	public static List<File> defaultBootPath() {
		ArrayList<File> bootpath = new ArrayList<File>();
		
		try {			
			// String jarfile = Main.class.getPackage().getImplementationTitle();
			// bootpath.add(jarfile);

			URI location = WycMain.class.getProtectionDomain().getCodeSource().getLocation().toURI();								
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
				bootpath.add(new File(jarfile));
			}				
		} catch(Exception e) {
			// just ignore.
		}		
		
		return bootpath;
	}	
	
    // =========================================================================
 	// Main Method
 	// =========================================================================
 	    	
	public static void main(String[] args) {
		System.exit(new WycMain(new WycBuildTask(), DEFAULT_OPTIONS).run(args));
	}
}