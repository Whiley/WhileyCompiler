package wyjc;

import java.io.*;
import java.net.URI;
import java.util.*;

import wyc.Pipeline;
import wyc.Compiler;
import wyc.util.*;
import wyil.*;
import wyil.util.*;
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
	
	public static int threadCount;

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
					"Specify where to find whiley files",
					new ArrayList<String>()),
			new OptArg("bootpath", "bp", PATHLIST,
					"Specify where to find whiley standard library files",
					new ArrayList<String>()),
			new OptArg("thread-count", "tc", INT,
					"Specify the number of threads to use when run", (Object) (-1)),
			new OptArg("X", PIPELINEAPPEND, "append new pipeline stage"),
			new OptArg("C", PIPELINECONFIGURE,
					"configure existing pipeline stage"),
			new OptArg("R", PIPELINEREMOVE, "remove existing pipeline stage") };

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
	public static void initialiseBootpath(ArrayList<String> bootpath) {
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
					bootpath.add(jarfile);
				}				
			} catch(Exception e) {
				// just ignore.
			}
		}
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
				
		// read out option values
		ArrayList<String> whileypath = (ArrayList) values.get("whileypath");
		ArrayList<String> bootpath = (ArrayList) values.get("bootpath");
		ArrayList<Pipeline.Modifier> pipelineModifiers = (ArrayList) values.get("pipeline"); 
		boolean verbose = values.containsKey("verbose");
		threadCount = (Integer) values.get("thread-count");
		
		try {			
			// initialise the boot path appropriately
			initialiseBootpath(bootpath);

			// now initialise the whiley path
			whileypath.add(0,".");
			whileypath.addAll(bootpath);

			// now construct a pipline and initialise the compiler		
			ClassFileLoader classLoader = new ClassFileLoader();
			ModuleLoader moduleLoader = new ModuleLoader(whileypath, classLoader);
			ArrayList<Pipeline.Template> templates = new ArrayList(Pipeline.defaultPipeline);
			templates.add(new Pipeline.Template(ClassWriter.class,Collections.EMPTY_MAP));
			Pipeline pipeline = new Pipeline(templates, moduleLoader);
			if(pipelineModifiers != null) {
				pipeline.apply(pipelineModifiers);
			}
			List<Transform> stages = pipeline.instantiate();
			Compiler compiler = new Compiler(moduleLoader,stages);		
			moduleLoader.setLogger(compiler);		

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
			errout.println("internal failure: " + e.getMessage());
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
