package wyc.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wybs.util.StdBuildRule;
import wybs.util.StdProject;
import wyc.builder.CompileTask;
import wyc.lang.WhileyFile;
import wyc.util.AbstractProjectCommand;
import wycc.util.AbstractCommand;
import wycc.util.Logger;
import wycs.builders.Wyal2WycsBuilder;
import wycs.syntax.WyalFile;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.VirtualRoot;
import wyil.builders.Wyil2WyalBuilder;
import wyil.lang.WyilFile;

public class Compile extends AbstractProjectCommand {

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
	 * Construct a new instance of this command.
	 * 
	 * @param registry
	 *            The content registry being used to match files to content
	 *            types.
	 * @throws IOException 
	 */
	public Compile(Content.Registry registry, Logger logger) {
		super(registry, logger, "verify","brief");
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
			finaliseConfiguration();
			StdProject project = initialiseProject();
			addCompilationBuildRules(project);
			if(verify) {
				addVerificationBuildRules(project);
			}
			// =====================================================================
			// Build Delta + Santity Check
			// =====================================================================

			ArrayList<File> delta = new ArrayList<File>();
			for (String arg : args) {
				delta.add(new File(arg));
			}

			// FIXME: somehow, needing to use physical files at this point is
			// rather cumbersome. It would be much better if the enclosing
			// framework could handle this aspect for us.
			for(File f : delta) {
				if(!f.exists()) {
					// FIXME: sort this out!
					System.out.println("wyc: file not found: " + f.getName());
					return;
				}
			}

			// =====================================================================
			// Run Build Task
			// =====================================================================
			// Determine source files to build
			List<Path.Entry<WhileyFile>> entries = whileydir.find(delta, WhileyFile.ContentType);
			// Build the source files
			project.build(entries);
			// Force all wyil files to be written to disk
			wyildir.flush();
		} catch(Exception e) {
			// now what?
			throw new RuntimeException(e);
		}
	}

	// =======================================================================
	// Helpers
	// =======================================================================
	
	

	/**
	 * Add build rules necessary for compiling whiley source files into binary
	 * wyil files.
	 * 
	 * @param project
	 */
	private void addCompilationBuildRules(StdProject project) {
		// Configure build rules for normal compilation		
		Content.Filter<WhileyFile> whileyIncludes = Content.filter("**", WhileyFile.ContentType);
		Content.Filter<WhileyFile> whileyExcludes = null;
		// Rule for compiling Whiley to WyIL
		CompileTask wyilBuilder = new CompileTask(project);
		wyilBuilder.setLogger(logger);
		project.add(new StdBuildRule(wyilBuilder, whileydir, whileyIncludes, whileyExcludes, wyildir));
	}

	/**
	 * Add build rules necessary for compiling wyil binary files into wyal files
	 * for verification.
	 * 
	 * @param project
	 */
	private void addVerificationBuildRules(StdProject project) {
		// Configure build rules for verification (if applicable)
		Content.Filter<WyilFile> wyilIncludes = Content.filter("**", WyilFile.ContentType);
		Content.Filter<WyilFile> wyilExcludes = null;
		Content.Filter<WyalFile> wyalIncludes = Content.filter("**", WyalFile.ContentType);
		Content.Filter<WyalFile> wyalExcludes = null;
		// Rule for compiling WyIL to WyAL
		Wyil2WyalBuilder wyalBuilder = new Wyil2WyalBuilder(project);
		project.add(new StdBuildRule(wyalBuilder, wyildir, wyilIncludes, wyilExcludes, wyaldir));
		// Rule for compiling WyAL to WyCS
		Wyal2WycsBuilder wycsBuilder = new Wyal2WycsBuilder(project);
		project.add(new StdBuildRule(wycsBuilder, wyaldir, wyalIncludes, wyalExcludes, wycsdir));
	}

}
