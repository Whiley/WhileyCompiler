//Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
// * Redistributions of source code must retain the above copyright
//   notice, this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright
//   notice, this list of conditions and the following disclaimer in the
//   documentation and/or other materials provided with the distribution.
// * Neither the name of the <organization> nor the
//   names of its contributors may be used to endorse or promote products
//   derived from this software without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
//ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
//DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
//(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
//LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
//ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
//(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wycs;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import wyautl.io.PrettyAutomataReader;

import static wycc.lang.SyntaxError.*;
import wycc.lang.Pipeline;
import wycc.lang.SyntaxError;
import wycc.lang.Transform;
import wycc.util.OptArg;
import wycs.solver.Solver;
import wycs.transforms.VerificationCheck;
import wycs.util.WycsBuildTask;

/**
* The main class provides all of the necessary plumbing to process command-line
* options, construct an appropriate pipeline and then instantiate the Whiley
* Compiler to generate class files.
*
* @author David J. Pearce
*
*/
public class WycsMain {

	public static PrintStream errout;
	public static final int MAJOR_VERSION;
	public static final int MINOR_VERSION;
	public static final int MINOR_REVISION;
	public static final int BUILD_NUMBER;

	public static final int SUCCESS=0;
	public static final int SYNTAX_ERROR=1;
	public static final int INTERNAL_FAILURE=2;

	public static final OptArg[] DEFAULT_OPTIONS = new OptArg[] {
			new OptArg("help", "Print this help information"),
			new OptArg("version", "Print version information"),
			new OptArg("verbose",
					"Print detailed information on what the compiler is doing"),
			new OptArg("debug", "Print detailed debugging information"),
			new OptArg("decompile", "d", "Decompile a give wycs binary file"),
			new OptArg("wycspath", "wp", OptArg.FILELIST,
					"Specify where to find wycs (binary) files",
					new ArrayList<String>()),
			new OptArg("bootpath", "bp", OptArg.FILELIST,
					"Specify where to find wycs standard library files",
					new ArrayList<String>()),
			new OptArg("wyaldir", "wd", OptArg.FILEDIR,
					"Specify where to find wyal source files", new File(".")),
			new OptArg("wycsdir", OptArg.FILEDIR,
					"Specify where to find wycs binaryfiles", new File(".")),
			new OptArg("X", OptArg.PIPELINECONFIGURE,
					"Configure existing pipeline stage"),
			new OptArg("A", OptArg.PIPELINEAPPEND, "append new pipeline stage"),
			new OptArg("R", OptArg.PIPELINEREMOVE,
					"Remove existing pipeline stage"),
			new OptArg("wyone", "Debug wyone files") };

	/**
	 * Initialise the error output stream so as to ensure it will display
	 * unicode characters (when possible). Additionally, extract version
	 * information from the enclosing jar file.
	 */
	static {
		try {
			errout = new PrintStream(System.err, true, "UTF-8");
		} catch(Exception e) {
			errout = System.err;
			System.err.println("Warning: terminal does not support unicode");
		}

		// determine version numbering from the MANIFEST attributes
		String versionStr = WycsMain.class.getPackage().getImplementationVersion();
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
	protected final WycsBuildTask builder;

	// =========================================================================
	// Constructors & Configuration
	// =========================================================================

	public WycsMain(WycsBuildTask builder, OptArg[] options) {
		this.options = options;
		this.builder = builder;
	}

    // =========================================================================
	// Run Method
	// =========================================================================

	public int run(String[] _args) {
		boolean verbose = false;

		try {
			// =====================================================================
			// Process Options
			// =====================================================================

			ArrayList<String> args = new ArrayList<String>(Arrays.asList(_args));
			Map<String, Object> values = OptArg.parseOptions(args, options);

			// Second, check if we're printing version
			if (values.containsKey("version")) {
				System.out.println("Whiley Constraint Solver (wycs) version "
						+ MAJOR_VERSION + "." + MINOR_VERSION + "."
						+ MINOR_REVISION + " (build " + BUILD_NUMBER + ")");
				return SUCCESS;
			}

			// Otherwise, if no files to compile specified, then print usage
			if (args.isEmpty() || values.containsKey("help")) {
				System.out.println("usage: wycs <options> <source-files>");
				OptArg.usage(System.out, options);
				usage(System.out, WycsBuildTask.defaultPipeline);
				return SUCCESS;
			}

			if (values.containsKey("wyone")) {				
				FileReader input = new FileReader(args.get(0));
				PrettyAutomataReader reader = new PrettyAutomataReader(input, Solver.SCHEMA);				
				VerificationCheck.unsat(reader.read(),VerificationCheck.RewriteMode.UNFAIR,500,values.containsKey("debug"));
				return SUCCESS;
			}
			
			verbose = values.containsKey("verbose");

			// =====================================================================
			// Construct & Configure Build Task
			// =====================================================================

			builder.setVerbose(verbose);
			builder.setDebug(values.containsKey("debug"));
			builder.setDecompile(values.containsKey("decompile"));

			ArrayList<Pipeline.Modifier> pipelineModifiers = (ArrayList) values
					.get("pipeline");
			if (pipelineModifiers != null) {
				builder.setPipelineModifiers(pipelineModifiers);
			}

			File wyalDir = (File) values.get("wyaldir");
			builder.setWyalDir(wyalDir);

			File wycsDir = (File) values.get("wycsdir");
			builder.setWycsDir(wycsDir);

			ArrayList<File> bootpath = (ArrayList<File>) values.get("bootpath");
			builder.setBootPath(bootpath);

			ArrayList<File> wycspath = (ArrayList<File>) values.get("wycspath");
			builder.setWycsPath(wycspath);

			ArrayList<File> delta = new ArrayList<File>();
			for (String arg : args) {
				delta.add(new File(arg));
			}

			// sanity check we've actually compiling things that exist
			for(File f : delta) {
				if(!f.exists()) {
					System.out.println("wycs: file not found: " + f.getName());
					return INTERNAL_FAILURE;
				}
			}
			// =====================================================================
			// Run Build Task
			// =====================================================================

			builder.build(delta);

		} catch (InternalFailure e) {
			e.outputSourceError(errout,false);
			if (verbose) {
				e.printStackTrace(errout);
			}
			return INTERNAL_FAILURE;
		} catch (SyntaxError e) {
			e.outputSourceError(errout,false);
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
		out.println("\nPipeline configuration:");
		for(Pipeline.Template template : stages) {
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
	// Main Method
	// =========================================================================

	public static void main(String[] args) {
		System.exit(new WycsMain(new WycsBuildTask(), DEFAULT_OPTIONS).run(args));
	}

}
