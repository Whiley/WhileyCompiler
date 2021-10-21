
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
import wycli.lang.SemanticVersion;
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
		Repository repository = environment.getRepository();
		// Extract configuration for this path
		Configuration config = environment.get(path);
		//
		try {
			List<Value.UTF8> includes = determineIncludes(config);
			// Determine list of files to go in package
			List<Build.Artifact> files = determinePackageContents(repository,includes);
			// Construct zip file context representing package
			ZipFile zf = createZipFile(files);
			// Get top-level repository
			Package.Repository repo = environment.getPackageResolver().getRepository();
			// Extract package name from configuration
			String name = config.get(Value.UTF8.class, Trie.fromString("package/name")).toString();
			// Extract package version from
			String version = config.get(Value.UTF8.class, Trie.fromString("package/version")).toString();
			//
			install(deploy ? 1 : 0, repo, zf, name, new SemanticVersion(version));
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
		Value.UTF8[] items = project.get(Value.Array.class, BUILD_INCLUDES).toArray(Value.UTF8.class);
		includes.addAll(Arrays.asList(items));
		// Determine platform-specific included files
		for (Path.ID id : project.matchAll(BUILD_PLATFORM_INCLUDES)) {
			items = project.get(Value.Array.class, id).toArray(Value.UTF8.class);
			includes.addAll(Arrays.asList(items));
		}
		// Done
		return includes;
	}

	/**
	 * Identify which files are to be included in the package. This is determined by
	 * the build/includes attribute in the package manifest.
	 *
	 * @return
	 * @throws IOException
	 */
	private List<Build.Artifact> determinePackageContents(Repository repo, List<Value.UTF8> includes)
			throws IOException {
		// Determine includes filter
		ArrayList<Path.Entry<?>> files = new ArrayList<>();
		// Extract root of this project
		Path.Root root = project.getRoot();
		// Add all files from the includes filter
		for(int i=0;i!=includes.size();++i) {
			// Construct a filter from the attribute itself
			Content.Filter<?> filter = createFilter(includes.get(i).toString());
			// Add all files matching the attribute
			files.addAll(root.get(filter));
		}
		// Done
		return files;
	}

	/**
	 * Given a list of files construct a corresponding ZipFile containing them.
	 *
	 * @param files
	 * @return
	 * @throws IOException
	 */
	private ZipFile createZipFile(List<Build.Artifact> files) throws IOException {
		// The set of known paths
		HashSet<Trie> paths = new HashSet<>();
		// The zip file we're creating
		ZipFile zf = new ZipFile();
		// Add each file to zip file
		for (int i = 0; i != files.size(); ++i) {
			long start = System.currentTimeMillis();
			Trie file = files.get(i);
			// Extract path
			addPaths(file.id().parent(), paths, zf);
			// Construct filename for given entry
			String filename = file.id().toString() + "." + file.contentType().getSuffix();
			// Extract bytes representing entry
			byte[] contents = readFileContents(file);
			zf.add(new ZipEntry(filename), contents);
			long time = System.currentTimeMillis() - start;
			logger.logTimedMessage("Packaging " + file.id() + "." + file.contentType().getSuffix(), time, 0);
		}
		//
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
	private void install(int count, Package.Repository repository, ZipFile pkg, String name, SemanticVersion version)
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
	 * This is a slightly strange method. Basically, it recursively adds directory
	 * entries into the ZipFile. Technically such entries should not be needed.
	 * However, due to a quirk in the way that ZipFileRoot works (in fact,
	 * AbstractFolder to be more precise) these entries are required for now.
	 *
	 * @param path
	 * @param paths
	 * @param zf
	 */
	private void addPaths(Trie path, HashSet<Trie> paths, ZipFile zf) {
		if(path.size() > 0 && !paths.contains(path)) {
			addPaths(path.parent(),paths,zf);
			// A new path encountered
			String directory = path.toString() + "/";
			zf.add(new ZipEntry(directory), new byte[0]);
			paths.add(path);
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
		InputStream in = file.inputStream();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		// Read bytes in max 1024 chunks
		byte[] data = new byte[1024];
		// Read all bytes from the input stream
		while ((nRead = in.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		// Done
		buffer.flush();
		return buffer.toByteArray();
	}

	/**
	 * Create a content filter from the string representation.
	 *
	 * @param filter
	 * @return
	 */
	private Content.Filter<?> createFilter(String filter) {
		String[] split = filter.split("\\.");
		//
		Content.Type contentType = getContentType(split[1]);
		//
		return Content.filter(split[0], contentType);
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