
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
package wycli.commands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;

import jbfs.core.Content;
import jbfs.util.Trie;
import jbfs.util.ZipFile;
import jbfs.core.Build;
import jbfs.core.Build.Repository;
import wycc.util.Logger;
import wycc.util.AbstractCompilationUnit.Value;
import wycli.cfg.Configuration;
import wycli.cfg.Configuration.Schema;
import wycli.lang.Command;
import wycli.lang.Package;
import wycli.lang.Semantic;
import wycli.lang.Command.Option;
import wycli.lang.Command.Template;

public class InstallCmd implements Command {
	public static final Trie BUILD_INCLUDES = Trie.fromString("build/includes");
	public static final Trie BUILD_PLATFORM_INCLUDES = Trie.fromString("build/*/includes");

	/**
	 * The descriptor for this command.
	 */
	public static final Command.Descriptor DESCRIPTOR = new Command.Descriptor() {
		@Override
		public String getName() {
			return "install";
		}

		@Override
		public String getDescription() {
			return "Install package into local repository";
		}

		@Override
		public List<Option.Descriptor> getOptionDescriptors() {
			return Arrays
					.asList(Command.OPTION_FLAG("deploy", "deploy package to remote repository"));
		}

		@Override
		public Schema getConfigurationSchema() {
			return Configuration.EMPTY_SCHEMA;
		}

		@Override
		public List<Descriptor> getCommands() {
			return Collections.EMPTY_LIST;
		}

		@Override
		public Command initialise(Command.Environment environment) {
			return new InstallCmd(environment, System.out, System.err);
		}

	};

	/**
	 * Provides a generic place to which error output should be directed. This
	 * should eventually be replaced.
	 */
	private Logger logger;

	/**
	 * The enclosing environment for this command
	 */
	private final Command.Environment environment;

	/**
	 * Provides a generic place to which normal output should be directed. This
	 * should eventually be replaced.
	 */
	private final PrintStream sysout;

	public InstallCmd(Command.Environment environment, OutputStream sysout, OutputStream syserr) {
		this.environment = environment;
		this.logger = environment.getLogger();
		this.sysout = new PrintStream(sysout);
	}

	@Override
	public Descriptor getDescriptor() {
		return DESCRIPTOR;
	}

	@Override
	public void initialise() {
		// Nothing to do here?
	}

	@Override
	public void finalise() {
		// Nothing to do here?
	}

	@Override
	public boolean execute(Trie path, Template template) throws Exception {
		boolean deploy = template.getOptions().has("deploy");
		// Extract configuration for this path
		Configuration config = environment.get(path);
		//
		try {
			List<Value.UTF8> includes = determineIncludes(config);
			// Construct zip file context representing package
			ZipFile zf = createZipFile(includes);
			// Get top-level repository
			Package.Repository repo = environment.getPackageResolver().getRepository();
			// Extract package name from configuration
			String name = config.get(Value.UTF8.class, Trie.fromString("package/name")).toString();
			// Extract package version from
			String version = config.get(Value.UTF8.class, Trie.fromString("package/version")).toString();
			//
			install(deploy ? 1 : 0, repo, zf, name, new Semantic.Version(version));
			// Done
			return true;
		} catch (IOException e) {
			logger.logTimedMessage(e.getMessage(), 0, 0);
			return false;
		}
	}

	/**
	 * Determine the set of included files for this package. This is determined in
	 * two ways. Firstly, there is a a default set of top-level includes. Secondly,
	 * there is a set of platform specific includes.
	 *
	 * @param project
	 * @return
	 */
	private List<Value.UTF8> determineIncludes(Configuration config) {
		ArrayList<Value.UTF8> includes = new ArrayList<>();
		// Determine default included files
		Value.UTF8[] items = config.get(Value.Array.class, BUILD_INCLUDES).toArray(Value.UTF8.class);
		includes.addAll(Arrays.asList(items));
		// Determine platform-specific included files
		for (Trie id : config.matchAll(BUILD_PLATFORM_INCLUDES)) {
			items = config.get(Value.Array.class, id).toArray(Value.UTF8.class);
			includes.addAll(Arrays.asList(items));
		}
		// Done
		return includes;
	}

	private ZipFile createZipFile(List<Value.UTF8> includes)
			throws IOException {
		// Determine includes filter
		ZipFile zf = new ZipFile(environment.getContentRegistry());
		// Extract root of this project
		Content.Root root = environment.getWorkspaceRoot();
		// Add all files from the includes filter
		for (int i = 0; i != includes.size(); ++i) {
			// Construct a filter from the attribute itself
			String[] split = includes.get(i).toString().split("\\.");
			Content.Type contentType = getContentType(split[1]);
			Content.Filter<?> filter = Content.Filter(contentType, Trie.fromString(split[0]));
			// Add all files matching the attribute
			for (Trie p : root.match(filter)) {
				long start = System.currentTimeMillis();
				Build.Artifact file = (Build.Artifact) root.get(contentType, p);
				// Extract bytes representing entry
				byte[] contents = readFileContents(file);
				zf.add(file.getContentType(), file.getPath(), contents);
				long time = System.currentTimeMillis() - start;
				logger.logTimedMessage("Packaging " + file.getPath() + "." + file.getContentType().getSuffix(), time,
						0);
			}
		}
		// Done
		return zf;
	}

	/**
	 * Recursively install a given package into the first <code>n</code>
	 * repositories in the chain of all repositories.
	 *
	 * @param count
	 * @param repository
	 * @param pkg
	 * @param name
	 * @param version
	 * @throws IOException
	 */
	private void install(int count, Package.Repository repository, ZipFile pkg, String name, Semantic.Version version)
			throws IOException {
		repository.put(pkg, name, version);
		//
		repository = repository.getParent();
		//
		if (count > 0 && repository != null) {
			install(count - 1, repository, pkg, name, version);
		}
	}

	/**
	 * Read the contents of a given file into a byte array.
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] readFileContents(Build.Artifact file) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Content.Type ct = file.getContentType();
		ct.write(buffer, file);
		// Done
		buffer.flush();
		return buffer.toByteArray();
	}

	/**
	 * Determine the content type from the suffix of a given build/includes
	 * attribute entry.
	 *
	 * @param suffix
	 * @return
	 */
	private Content.Type getContentType(String suffix) {
		Content.Type<?> ct = environment.getContentRegistry().contentType(suffix);
		//
		if(ct != null) {
			return ct;
		} else {
			// miss
			throw new IllegalArgumentException("unknown content-type: " + suffix);
		}
	}
}