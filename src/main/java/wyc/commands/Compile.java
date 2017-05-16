// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.commands;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import wybs.lang.SyntaxError;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.StdBuildRule;
import wybs.util.StdProject;
import wyc.builder.CompileTask;
import wyc.lang.WhileyFile;
import wyc.util.AbstractProjectCommand;
import wycc.lang.Feature.ConfigurationError;
import wycc.util.ArrayUtils;
import wycc.util.Logger;
import wyal.lang.WyalFile;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.VirtualRoot;
import wyil.builders.Wyil2WyalBuilder;
import wyil.lang.WyilFile;
import wytp.provers.AutomatedTheoremProver;

public class Compile extends AbstractProjectCommand<Compile.Result> {
	/**
	 * Result kind for this command
	 *
	 */
	public enum Result {
		SUCCESS,
		ERRORS,
		INTERNAL_FAILURE
	}

	/**
	 * Provides a generic place to which normal output should be directed. This
	 * should eventually be replaced.
	 */
	private final PrintStream sysout;

	/**
	 * Provides a generic place to which error output should be directed. This
	 * should eventually be replaced.
	 */
	private final PrintStream syserr;

	/**
	 * Signals that verbose output should be produced.
	 */
	protected boolean verbose = false;

	/**
	 * Signals that brief error reporting should be used. This is primarily used
	 * to help integration with external tools. More specifically, brief output
	 * is structured so as to be machine readable.
	 */
	protected boolean brief = false;

	/**
	 * Signals that compile-time verification of source files should be
	 * performed.
	 */
	protected boolean verify = false;

	/**
	 * Identifies which whiley source files should be considered for
	 * compilation. By default, all files reachable from srcdir are considered.
	 */
	protected Content.Filter<WhileyFile> whileyIncludes = Content.filter("**", WhileyFile.ContentType);

	/**
	 * Identifies which whiley sources files should not be considered for
	 * compilation. This overrides any identified by <code>whileyIncludes</code>
	 * . By default, no files files reachable from srcdir are excluded.
	 */
	protected Content.Filter<WhileyFile> whileyExcludes = null;

	/**
	 * Construct a new instance of this command.
	 *
	 * @param registry
	 *            The content registry being used to match files to content
	 *            types.
	 * @throws IOException
	 */
	public Compile(Content.Registry registry, Logger logger) {
		super(registry, logger);
		this.sysout = System.out;
		this.syserr = System.err;
	}

	/**
	 * Construct a new instance of this command.
	 *
	 * @param registry
	 *            The content registry being used to match files to content
	 *            types.
	 * @throws IOException
	 */
	public Compile(Content.Registry registry, Logger logger, OutputStream sysout, OutputStream syserr) {
		super(registry, logger);
		this.sysout = new PrintStream(sysout);
		this.syserr = new PrintStream(syserr);
	}

	@Override
	public String getName() {
		return "compile";
	}

	// =======================================================================
	// Configuration
	// =======================================================================

	private static final String[] SCHEMA = {
			"verbose",
			"verify",
			"brief"
	};

	@Override
	public String[] getOptions() {
		return ArrayUtils.append(super.getOptions(),SCHEMA);
	}

	@Override
	public String describe(String option) {
		switch(option) {
		case "verbose":
			return "Enable verbose output from Whiley compiler";
		case "brief":
			return "Enable brief reporting of error messages";
		case "verify":
			return "Enable verification of Whiley source files";
		default:
			return super.describe(option);
		}
	}

	@Override
	public void set(String option, Object value) throws ConfigurationError {
		switch(option) {
		case "verbose":
			this.verbose = true;
			break;
		case "brief":
			this.brief = true;
			break;
		case "verify":
			this.verify = true;
			break;
		default:
			super.set(option, value);
		}
	}

	@Override
	public String getDescription() {
		return "Compile one or more Whiley source files";
	}

	public void setVerify() {
		verify = true;
	}

	public boolean getVerify() {
		return verify;
	}

	public void setVerbose() {
		setVerbose(true);
	}

	public void setVerbose(boolean b) {
		verbose = b;
	}

	public void setBrief() {
		brief = true;
	}

	public String describeIncludes() {
		return "Specify where find Whiley source files";
	}

	public void setIncludes(Content.Filter<WhileyFile> includes) {
		this.whileyIncludes = includes;
	}

	public String describeExcludes() {
		return "Specify Whiley source files to be excluded from consideration";
	}

	public void setExcludes(Content.Filter<WhileyFile> excludes) {
		this.whileyExcludes = excludes;
	}
	// =======================================================================
	// Execute
	// =======================================================================

	@Override
	public Result execute(String... args) {
		try {
			ArrayList<File> delta = new ArrayList<>();
			for (String arg : args) {
				delta.add(new File(arg));
			}

			// FIXME: somehow, needing to use physical files at this point is
			// rather cumbersome. It would be much better if the enclosing
			// framework could handle this aspect for us.
			for (File f : delta) {
				if (!f.exists()) {
					// FIXME: sort this out!
					sysout.println("compile: file not found: " + f.getName());
					return Result.ERRORS;
				}
			}
			// Finalise the configuration before continuing.
			StdProject project = initialiseProject();
			// Determine source files to build
			List<Path.Entry<WhileyFile>> entries = whileydir.find(delta, WhileyFile.ContentType);
			// Execute the build over the set of files requested
			return compile(project,entries);
		} catch(RuntimeException e) {
			throw e;
		} catch (Exception e) {
			// FIXME: this is a problem because it is swallowing exceptions!!
			return Result.INTERNAL_FAILURE;
		}
	}

	public Result execute(List<Path.Entry<WhileyFile>> entries) {
		try {
			StdProject project = initialiseProject();
			return compile(project,entries);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			// FIXME: this is a problem because it is swallowing exceptions!!
			return Result.INTERNAL_FAILURE;
		}
	}

	// =======================================================================
	// Helpers
	// =======================================================================

	private Result compile(StdProject project, List<Path.Entry<WhileyFile>> entries) {
		// Initialise Project
		try {
			addCompilationBuildRules(project);
			if (verify) {
				addVerificationBuildRules(project);
			}
			// =====================================================================
			// Build Delta + Santity Check
			// =====================================================================
			// Build the source files
			project.build(entries);
			// Force all binary files to be written to disk (if appropriate)
			wyildir.flush();
			wyaldir.flush();
			//
			return Result.SUCCESS;
		} catch(InternalFailure e) {
			throw e;
		} catch (SyntaxError e) {
			e.outputSourceError(syserr, brief);
			if (verbose) {
				printStackTrace(syserr, e);
			}
			return Result.ERRORS;
		} catch (Exception e) {
			// now what?
			throw new RuntimeException(e);
		}
	}

	/**
	 * Add build rules necessary for compiling whiley source files into binary
	 * wyil files.
	 *
	 * @param project
	 */
	protected void addCompilationBuildRules(StdProject project) {
		addWhiley2WyilBuildRule(project);
	}

	/**
	 * Add the rule for compiling Whiley source files into WyIL files.
	 *
	 * @param project
	 */
	protected void addWhiley2WyilBuildRule(StdProject project) {
		// Rule for compiling Whiley to WyIL
		CompileTask wyilBuilder = new CompileTask(project);
		if(verbose) {
			wyilBuilder.setLogger(logger);
		}
		project.add(new StdBuildRule(wyilBuilder, whileydir, whileyIncludes, whileyExcludes, wyildir));
	}

	/**
	 * Add build rules necessary for compiling wyil binary files into wyal files
	 * for verification.
	 *
	 * @param project
	 */
	protected void addVerificationBuildRules(StdProject project) {
		// Configure build rules for verification (if applicable)
		Content.Filter<WyilFile> wyilIncludes = Content.filter("**", WyilFile.ContentType);
		Content.Filter<WyilFile> wyilExcludes = null;
		Content.Filter<WyalFile> wyalIncludes = Content.filter("**", WyalFile.ContentType);
		Content.Filter<WyalFile> wyalExcludes = null;
		// Rule for compiling WyIL to WyAL
		Wyil2WyalBuilder wyalBuilder = new Wyil2WyalBuilder(project);
		if(verbose) {
			wyalBuilder.setLogger(logger);
		}
		project.add(new StdBuildRule(wyalBuilder, wyildir, wyilIncludes, wyilExcludes, wyaldir));
		//
		wytp.types.TypeSystem typeSystem = new wytp.types.TypeSystem(project);
		AutomatedTheoremProver prover = new AutomatedTheoremProver(typeSystem);
		wyal.tasks.CompileTask wyalBuildTask = new wyal.tasks.CompileTask(project,typeSystem,prover);
		if(verbose) {
			wyalBuildTask.setLogger(logger);
		}
		if(verify) {
			wyalBuildTask.setVerify(true);
		}
		project.add(new StdBuildRule(wyalBuildTask, wyaldir, wyalIncludes, wyalExcludes, wycsdir));
	}


	public List getModifiedSourceFiles() throws IOException {
		if (whileydir == null) {
			// Note, whileyDir can be null if e.g. compiling wyil -> wyjc
			return new ArrayList();
		} else {
			return getModifiedSourceFiles(whileydir, whileyIncludes, wyildir,
					WyilFile.ContentType);
		}
	}

	/**
	 * Generate the list of source files which need to be recompiled. By
	 * default, this is done by comparing modification times of each whiley file
	 * against its corresponding wyil file. Wyil files which are out-of-date are
	 * scheduled to be recompiled.
	 *
	 * @return
	 * @throws IOException
	 */
	public static <T, S> List<Path.Entry<T>> getModifiedSourceFiles(Path.Root sourceDir,
			Content.Filter<T> sourceIncludes, Path.Root binaryDir, Content.Type<S> binaryContentType)
					throws IOException {
		// Now, touch all source files which have modification date after
		// their corresponding binary.
		ArrayList<Path.Entry<T>> sources = new ArrayList<>();

		for (Path.Entry<T> source : sourceDir.get(sourceIncludes)) {
			// currently, I'm assuming everything is modified!
			Path.Entry<S> binary = binaryDir.get(source.id(), binaryContentType);
			// first, check whether wycs file out-of-date with source file
			if (binary == null || binary.lastModified() < source.lastModified()) {
				sources.add(source);
			}
		}

		return sources;
	}

	/**
	 * Print a complete stack trace. This differs from
	 * Throwable.printStackTrace() in that it always prints all of the trace.
	 *
	 * @param out
	 * @param err
	 */
	private static void printStackTrace(PrintStream out, Throwable err) {
		out.println(err.getClass().getName() + ": " + err.getMessage());
		for(StackTraceElement ste : err.getStackTrace()) {
			out.println("\tat " + ste.toString());
		}
		if(err.getCause() != null) {
			out.print("Caused by: ");
			printStackTrace(out,err.getCause());
		}
	}
}
