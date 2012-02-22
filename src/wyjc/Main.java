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

package wyjc;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.jar.JarFile;

import wyc.Pipeline;
import wyc.Compiler;
import wyc.lang.WhileyProject;
import wyc.util.*;
import wyil.*;
import wyil.util.*;
import wyil.util.path.*;
import static wyil.util.SyntaxError.*;
import static wyc.util.OptArg.*;
import wyjc.io.*;
import wyjc.transforms.*;

/**
 * The main class provides all of the necessary plumbing to process command-line
 * options, construct an appropriate pipeline and then instantiate the Whiley
 * Compiler to generate class files.
 * 
 * @author David J. Pearce
 * 
 */
public class Main {
	
	public static PrintStream errout;
	public static final int MAJOR_VERSION;
	public static final int MINOR_VERSION;
	public static final int MINOR_REVISION;
	public static final int BUILD_NUMBER;

	public static final int SUCCESS=0;
	public static final int SYNTAX_ERROR=1;
	public static final int INTERNAL_FAILURE=2;
	
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
		String versionStr = Main.class.getPackage().getImplementationVersion();
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
		
		// register additional pipeline transforms used by this compiler.
		Pipeline.register(ClassWriter.class);
		Pipeline.register(JvmBytecodeWriter.class);
	}

	/**
	 * The command-line options accepted by the main method.
	 */
	public static final OptArg[] options = new OptArg[] {
			new OptArg("version", "Print version information"),			
			new OptArg("verbose",
					"Print detailed information on what the compiler is doing"),
			new OptArg("whileypath", "wp", PATHLIST,
					"Specify where to find whiley (binary) files",
					new ArrayList<String>()),
			new OptArg("bootpath", "bp", PATHLIST,
					"Specify where to find whiley standard library files",					
					new ArrayList<String>()),
			new OptArg("sourcepath", "sp", PATHLIST,
					"Specify where to find whiley (source) files",
					new ArrayList<String>()),
			new OptArg("outputdir", "d", STRING,
					"Specify where to place generated class files",
					null),
			new OptArg("X", PIPELINEAPPEND, "append new pipeline stage"),
			new OptArg("C", PIPELINECONFIGURE,
					"configure existing pipeline stage"),
			new OptArg("R", PIPELINEREMOVE, "remove existing pipeline stage"),
			new OptArg(
					"pause",
					"Do not start compiling until character read from input stream (this is to allow time for visualvm to connect)"),	
		};

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
	public static void initialiseBootPath(List<Path.Root> bootpath) {
		if(bootpath.isEmpty()) {
		
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
					bootpath.add(new JarFileRoot(jarfile));
				}				
			} catch(Exception e) {
				// just ignore.
			}
		}
	}
	
	/**
	 * In the case that no explicit source path is provided as a command-line
	 * argument, then "." is used as the source path. This mirrors the behaviour
	 * of "javac". What it means, however, is that if you're compiling from
	 * somewhere other than the package root, then you can quickly get into
	 * problems.
	 * 
	 * @param sourcepath
	 * @throws IOException
	 */
	public static ArrayList<Path.Root> initialiseSourceRoots(
			List<String> sourcepath, BinaryDirectoryRoot outputDirectory,
			boolean verbose) throws IOException {
		ArrayList<Path.Root> nitems = new ArrayList<Path.Root>();
		if (sourcepath.isEmpty()) {
			nitems.add(new SourceDirectoryRoot(".", outputDirectory));
		} else {			
			for (String root : sourcepath) {
				try {
					nitems.add(new SourceDirectoryRoot(root,outputDirectory));					
				} catch (IOException e) {
					if (verbose) {
						System.err.println("Warning: " + root
								+ " is not a valid package root");
					}
				}
			}			
		}
		return nitems;
	}

	/**
	 * The following is just a helper method. It assumes the list given contains
	 * the names of directories or jarfiles on the filesystem.
	 * 
	 * @param items
	 * @return
	 */
	private static List<Path.Root> initialiseBinaryRoots(List<String> roots,
			boolean verbose) {
		ArrayList<Path.Root> nitems = new ArrayList<Path.Root>();
		for (String root : roots) {
			try {
				if (root.endsWith(".jar")) {
					nitems.add(new JarFileRoot(root));
				} else {
					nitems.add(new BinaryDirectoryRoot(root));
				}
			} catch (IOException e) {
				if (verbose) {
					System.err.println("Warning: " + root
							+ " is not a valid package root");
				}
			}
		}
		return nitems;
	}
	
	/**
	 * The run method is responsible for processing command-line arguments and
	 * constructing an appropriate Compiler instance.
	 * 
	 * @param _args
	 */
	public static int run(String[] _args) {		
		// First, process any command-line options using the OptArg utility.
		ArrayList<String> args = new ArrayList<String>(Arrays.asList(_args));
		Map<String,Object> values = OptArg.parseOptions(args,options);
		
		// Second, check if we're printing version
		if(values.containsKey("version")) {
			System.out.println("Whiley-to-Java Compiler (wyjc) version " + MAJOR_VERSION + "."
					+ MINOR_VERSION + "." + MINOR_REVISION + " (build "
					+ BUILD_NUMBER + ")");				
			return 0;
		}
		
		// Otherwise, if no files to compile specified, then print usage
		if(args.isEmpty()) {
			System.out.println("usage: wyjc <options> <source-files>");
			OptArg.usage(System.out, options);
			System.exit(1);
		}
		
		if(values.containsKey("pause")) {
			System.out.println("Press any key to begin...");
			try {
				System.in.read();
			} catch(IOException e) {
				
			}
		}
		
		// read out option values
		boolean verbose = values.containsKey("verbose");
		String outputdir = (String) values.get("outputdir");
						
		ArrayList<Pipeline.Modifier> pipelineModifiers = (ArrayList) values.get("pipeline"); 		
		
		try {											
			// initialise the boot path appropriately
			List<Path.Root> bootpath = initialiseBinaryRoots((ArrayList) values.get("bootpath"),verbose);
			initialiseBootPath(bootpath);

			// initialise the whiley path appropriately
			List<Path.Root> whileypath = initialiseBinaryRoots((ArrayList) values.get("whileypath"),verbose);	
			
			// initialise the source path appropriately
			BinaryDirectoryRoot bindir = null;
			if (outputdir != null) {
				bindir = new BinaryDirectoryRoot(outputdir);
			}
			List<Path.Root> sourcepath = initialiseSourceRoots(
					(ArrayList) values.get("sourcepath"), bindir, verbose);
			
			// now initialise the whiley path			
			whileypath.addAll(bootpath);

			// now construct a pipline and initialise the compiler		
			WhileyProject project = new WhileyProject(sourcepath,whileypath);
			project.setModuleReader("class",  new ClassFileLoader());
			ArrayList<Pipeline.Template> templates = new ArrayList(Pipeline.defaultPipeline);
			templates.add(new Pipeline.Template(ClassWriter.class, Collections.EMPTY_MAP));

			Pipeline pipeline = new Pipeline(templates, project);
			
			if(pipelineModifiers != null) {
				pipeline.apply(pipelineModifiers);
			}
			
			if (outputdir != null) {
				pipeline.setOption(ClassWriter.class, "outputDirectory",
						outputdir);
			}
			
			List<Transform> stages = pipeline.instantiate();
			Compiler compiler = new Compiler(project,stages);		
			project.setLogger(compiler);		

			if(verbose) {			
				compiler.setLogOut(System.err);
			}
		
			// finally, let's compile some files!!!
		
			ArrayList<File> files = new ArrayList<File>();
			for (String file : args) {
				files.add(new File(file));
			}
			compiler.compile(files);
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
	
	public static void main(String[] args) {		
		System.exit(new Main().run(args));
	}
}
