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

import wybs.lang.*;
import wybs.util.*;
import wyc.builder.WhileyBuilder;
import wyc.lang.WhileyFile;
import wyc.util.*;
import wyil.*;
import wyil.lang.WyilFile;
import wyil.util.*;
import static wybs.lang.SyntaxError.*;
import static wyc.util.OptArg.*;

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
	
	public static int threadCount;

	public static final int SUCCESS=0;
	public static final int SYNTAX_ERROR=1;
	public static final int INTERNAL_FAILURE=2;

	/**
	 * The master project content type registry.
	 */
	public static final Content.Registry registry = new Content.Registry() {
	
		public void associate(Path.Entry e) {
			if(e.suffix().equals("whiley")) {
				e.associate(WhileyFile.ContentType, null);
			} else if(e.suffix().equals("class")) {
				// this could be either a normal JVM class, or a Wyil class. We
				// need to determine which.
				try { 					
					WyilFile c = WyilFile.ContentType.read(e, e.inputStream());
					if(c != null) {
						e.associate(WyilFile.ContentType,c);
					}					
				} catch(Exception ex) {
					// hmmm, not ideal
				}
			} 
		}
		
		public String suffix(Content.Type<?> t) {
			if(t == WhileyFile.ContentType) {
				return "whiley";
			} else if(t == WyilFile.ContentType) {
				return "class";
			} else {
				return "dat";
			}
		}
	};
	
	/**
	 * The purpose of the file filter is simply to prevent loading all different
	 * kinds of files in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter fileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().endsWith(".whiley") || f.getName().endsWith(".class") || f.isDirectory();
		}
	};
	
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
			new OptArg("thread-count", "tc", INT,
					"Specify the number of threads to use when run", (Object) (-1)),
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
					bootpath.add(new JarFileRoot(jarfile,registry));
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
	public static ArrayList<DirectoryRoot> initialiseSourceRoots(
			List<String> sourcepath, boolean verbose) throws IOException {
		ArrayList<DirectoryRoot> nitems = new ArrayList<DirectoryRoot>();
		if (sourcepath.isEmpty()) {
			nitems.add(new DirectoryRoot(".", fileFilter,registry));
		} else {			
			for (String root : sourcepath) {
				try {
					nitems.add(new DirectoryRoot(root,fileFilter,registry));					
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
	private static List<Path.Root> initialiseExternalRoots(List<String> roots,
			boolean verbose) {
		ArrayList<Path.Root> nitems = new ArrayList<Path.Root>();
		for (String root : roots) {
			try {
				if (root.endsWith(".jar")) {
					nitems.add(new JarFileRoot(root,registry));
				} else {
					nitems.add(new DirectoryRoot(root,fileFilter,registry));
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

		threadCount = (Integer) values.get("thread-count");
		String outputdir = (String) values.get("outputdir");
						
		ArrayList<Pipeline.Modifier> pipelineModifiers = (ArrayList) values.get("pipeline"); 		
		
		try {				
			// initialise target root appropriately (if one is provided)
			DirectoryRoot target = null;
			
			ArrayList<Path.Root> roots = new ArrayList<Path.Root>();
			if (outputdir != null) {
				// if an output directory is specified, everything is redirected
				// to that.
				target = new DirectoryRoot(outputdir,fileFilter,registry); 
				roots.add(target);
			} 
			
			// initialise the source roots appropriately
			List<DirectoryRoot> sourceRoots = initialiseSourceRoots(
					(ArrayList) values.get("sourcepath"), verbose);
			roots.addAll(sourceRoots);
			
			// initialise the external roots appropriately
			List<Path.Root> externalRoots = initialiseExternalRoots((ArrayList) values.get("whileypath"),verbose);	
			roots.addAll(externalRoots);
			
			// initialise the boot path appropriately
			List<Path.Root> bootpath = initialiseExternalRoots((ArrayList) values.get("bootpath"),verbose);
			initialiseBootPath(bootpath);
			roots.addAll(bootpath);
			
			// finally, construct the project	
			SimpleProject project = new SimpleProject(roots) {        		
        		public Path.ID create(String s) {
        			return Trie.fromString(s);
        		}
        	};		

			// now, initialise builder appropriately
			Pipeline pipeline = new Pipeline(Pipeline.defaultPipeline);

			if(pipelineModifiers != null) {
				pipeline.apply(pipelineModifiers);
			}
	
			WhileyBuilder builder = new WhileyBuilder(project,pipeline);	
			
			if(verbose) {			
				builder.setLogger(new Logger.Default(System.err));
			}		
			
			Content.Filter includes = Content.filter(Trie.fromString("**"),WhileyFile.ContentType);
			StandardBuildRule rule = new StandardBuildRule(builder);
			for(DirectoryRoot source : sourceRoots) {
				if(target != null) {
					rule.add(source, includes, target, WyilFile.ContentType);
				} else {
					rule.add(source, includes, source, WyilFile.ContentType);
				}
			}
			project.add(rule);
			
			// Now, touch all files indicated on command-line	
			ArrayList<Path.Entry<?>> sources = new ArrayList<Path.Entry<?>>();
			
			for(DirectoryRoot source : sourceRoots) {				
				File loc = source.location();
				String locPath = loc.getCanonicalPath();
				for (String _file : args) {
					String filePath = new File(_file).getCanonicalPath();
					if(filePath.startsWith(locPath)) {
						int end = locPath.length();
						if(end > 1) {
							end++;
						}
						String module = filePath.substring(end).replace(File.separatorChar, '.');
						module = module.substring(0,module.length()-7);						
						Path.ID mid = Trie.fromString(module);						
						Path.Entry<WhileyFile> e = source.get(mid,WhileyFile.ContentType);
						if (e != null) {							
							sources.add(e);
						}
					}
				}
			}
		
			// finally, let's compile some files!!!					
			project.build(sources);
			project.flush(); // flush all built components to disk
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
