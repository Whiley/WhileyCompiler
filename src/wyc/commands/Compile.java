package wyc.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import wybs.util.StdBuildRule;
import wybs.util.StdProject;
import wyc.builder.CompileTask;
import wyc.lang.WhileyFile;
import wycc.util.AbstractCommand;
import wycs.builders.Wyal2WycsBuilder;
import wycs.syntax.WyalFile;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.VirtualRoot;
import wyil.builders.Wyil2WyalBuilder;
import wyil.lang.WyilFile;

public class Compile extends AbstractCommand {
	
	/**
	 * The master project content type registry. This is needed for the build
	 * system to determine the content type of files it finds on the file
	 * system.
	 */
	public final Content.Registry registry;

	/**
	 * Signals that brief error reporting should be used. This is primarily used
	 * to help integration with external tools. More specifically, brief output
	 * is structured so as to be machine readable.
	 */
	private boolean brief = false;
	
	/**
	 * Signals that compile-time verification of source files should be
	 * performed.
	 */
	private boolean verify = false;
	
	/**
	 * The location in which whiley source files a located, or null if not
	 * specified.  The default value is the current directory.
	 */
	private File whileydir = null;
	
	/**
	 * The location in which wyil binary files are stored, or null if not
	 * specified.
	 */
	private File wyildir = null;
	
	/**
	 * The location in which wyal source files are stored, or null if not
	 * specified.
	 */
	private File wyaldir = null;
	
	/**
	 * The location in which wycs binary files are stored, or null if not
	 * specified.
	 */
	private File wycsdir = null;
	
	/**
	 * Construct a new instance of this command.
	 * 
	 * @param registry
	 *            The content registry being used to match files to content
	 *            types.
	 */
	public Compile(Content.Registry registry) {
		super("verify","brief","whileypath","whileydir","wyildir","wyaldir");
		this.registry = registry;
	}
	
	// =======================================================================
	// Configuration
	// =======================================================================
	
	public String describeVerify() {
		return "Enable verification of Whiley source files";
	}
	
	public void setVerify() {
		verify = true;
	}
	
	public String describeBrief() {
		return "Enable brief reporting of error messages";
	}
	
	public void setBrief() {
		brief = true;
	}
	
	public String describeWhileypath() {
		return "Specify where to find compiled Whiley (WyIL) files";
	}
	
	public String describeWhileydir() {
		return "Specify where to find Whiley source files";
	}
	
	public String describeWyildir() {
		return "Specify where to find place compiled Whiley (WyIL) files";
	}
	
	public String describeWyaldir() {
		return "Specify where to find place generated verification (WyAL) files";
	}
	
	@Override
	public String getDescription() {
		return "Compile one or more Whiley source files";
	}
	
	// =======================================================================
	// Execute
	// =======================================================================
	
	@Override
	public void execute(String... args) {
		// Initialise Project
		try {
			StdProject project = initialiseProject();
		} catch(Exception e) {
			// now what?
			throw new RuntimeException(e);
		}
	}

	// =======================================================================
	// Helpers
	// =======================================================================
	
	/**
	 *
	 * @return
	 * @throws IOException
	 */
	private StdProject initialiseProject() throws IOException {
		// Condigure project roots
		DirectoryRoot whileyroot = getDirectoryRoot(whileydir);
		Path.Root wyilroot = getAbstractRoot(wyildir);
		Path.Root wyalroot = getAbstractRoot(wyaldir);
		Path.Root wycsroot = getAbstractRoot(wycsdir);
		
		// Add roots and construct project
		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();
		roots.add(whileyroot);
		roots.add(wyilroot);
		roots.add(wyalroot);
		roots.add(wycsroot);

		StdProject project = new StdProject(roots);

		// Configure build rules for normal compilation		
		Content.Filter<WhileyFile> whileyIncludes = Content.filter("**", WhileyFile.ContentType);
		Content.Filter<WhileyFile> whileyExcludes = null;
		// Rule for compiling Whiley to WyIL
		CompileTask wyilBuilder = new CompileTask(project);		
		project.add(new StdBuildRule(wyilBuilder, whileyroot, whileyIncludes, whileyExcludes, wyilroot));
		
		// Configure build rules for verification (if applicable)
		if(verify) {
			Content.Filter<WyilFile> wyilIncludes = Content.filter("**", WyilFile.ContentType);
			Content.Filter<WyilFile> wyilExcludes = null;
			Content.Filter<WyalFile> wyalIncludes = Content.filter("**", WyalFile.ContentType);
			Content.Filter<WyalFile> wyalExcludes = null;		
			// Rule for compiling WyIL to WyAL
			Wyil2WyalBuilder wyalBuilder = new Wyil2WyalBuilder(project);
			project.add(new StdBuildRule(wyalBuilder, wyilroot, wyilIncludes, wyilExcludes, wyalroot));
			// Rule for compiling WyAL to WyCS
			Wyal2WycsBuilder wycsBuilder = new Wyal2WycsBuilder(project);
			project.add(new StdBuildRule(wycsBuilder, wyalroot, wyalIncludes, wyalExcludes, wycsroot));
		}
		
		// Construct the project
		return project;
	}
	
	/**
	 * Construct a root which must correspond to a physical directory.
	 * 
	 * @throws IOException
	 * 
	 */
	private DirectoryRoot getDirectoryRoot(File dir) throws IOException {
		if(dir == null) {
			dir = new File(".");
		} 
		return new DirectoryRoot(dir,registry);
	}
	
	/**
	 * Construct a root which is either virtual or corresponds to a physical
	 * directory.
	 * 
	 * @throws IOException
	 * 
	 */
	private Path.Root getAbstractRoot(File dir) throws IOException {
		if(dir != null) {
			return new DirectoryRoot(dir,registry);
		} else {
			return new VirtualRoot(registry);
		}
	}
}
