// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyll.command;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import wybs.util.StdBuildRule;
import wybs.util.StdProject;
import wyc.command.Compile;
import wycc.util.ArrayUtils;
import wycc.util.Logger;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyll.core.WyllFile;
import wyll.task.LowLevelCompileTask;
import wyc.lang.WhileyFile;

public class LowLevelCompile extends Compile {

	/**
	 * The location in which class generated wyll files are stored, or null if not
	 * specified.
	 */
	protected DirectoryRoot wylldir;

	/**
	 * Determine whether or not to generate in debug mode.
	 */
	protected boolean debug = true;

	/**
	 * Construct a new instance of this command.
	 *
	 * @param registry
	 *            The content registry being used to match files to content
	 *            types.
	 * @throws IOException
	 */
	public LowLevelCompile(Content.Registry registry, Logger logger) {
		super(registry, logger);
	}

	/**
	 * Construct a new instance of this command.
	 *
	 * @param registry
	 *            The content registry being used to match files to content
	 *            types.
	 * @throws IOException
	 */
	public LowLevelCompile(Content.Registry registry, Logger logger, OutputStream sysout, OutputStream syserr) {
		super(registry, logger, sysout, syserr);
	}


	@Override
	public String getName() {
		return "llcompile";
	}

	@Override
	public String getDescription() {
		return "Compile Whiley source files to the Low Level Language ";
	}

	private static final String[] SCHEMA = {
			"wylldir",
			"debug"
	};

	@Override
	public String[] getOptions() {
		return ArrayUtils.append(super.getOptions(),SCHEMA);
	}

	@Override
	public void set(String option, Object value) throws ConfigurationError {
		try {
			switch(option) {
			case "wylldir":
				setWylldir(new File((String)value));
				break;
			case "debug":
				setDebug((boolean)value);
				break;
			default:
				super.set(option, value);
			}
		} catch(IOException e) {
			throw new ConfigurationError(e);
		}
	}

	@Override
	public String describe(String option) {
		switch(option) {
		case "wylldir":
			return "Specify where to place generated wyll files";
		case "debug":
			return "Set debug mode (default is ON)";
		default:
			return super.describe(option);
		}
	}

	public void setWylldir(File dir) throws IOException {
		this.wylldir = new DirectoryRoot(dir,registry);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	protected void finaliseConfiguration() throws IOException {
		super.finaliseConfiguration();
		this.wylldir = getDirectoryRoot(wylldir,wyildir);
	}

	@Override
	protected Result compile(StdProject project, List<? extends Path.Entry<?>> entries) {
		try {
			Result r = super.compile(project, entries);
			wylldir.flush();
			return r;
		} catch (IOException e) {
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
	@Override
	protected void addCompilationBuildRules(StdProject project) {
		super.addCompilationBuildRules(project);
		addWyil2WyllBuildRule(project);
	}

	protected void addWyil2WyllBuildRule(StdProject project) {
		// Configure build rules for normal compilation
		Content.Filter<WhileyFile> wyilIncludes = Content.filter("**", WhileyFile.BinaryContentType);
		Content.Filter<WhileyFile> wyilExcludes = null;
		// Rule for compiling Whiley to WyIL
		LowLevelCompileTask wyllBuilder = new LowLevelCompileTask(project);
		if (verbose) {
			wyllBuilder.setLogger(logger);
		}
		wyllBuilder.setDebug(debug);
		project.add(new StdBuildRule(wyllBuilder, wyildir, wyilIncludes, wyilExcludes, wylldir));
	}

	@Override
	public List<? extends Path.Entry<?>> getModifiedSourceFiles() throws IOException {
		return getModifiedSourceFiles(wyildir, wyilIncludes, wylldir, WyllFile.ContentType);
	}
}
