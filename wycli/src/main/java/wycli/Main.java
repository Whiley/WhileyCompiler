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
package wycli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import wycc.lang.Filter;
import wycc.util.AbstractCompilationUnit.Value;
import wycc.util.ByteRepository;
import wycc.lang.Build;
import wycc.lang.Content;
import wycc.lang.SyntacticException;
import wycc.util.DirectoryRoot;
import wycc.util.Logger;
import wycli.cfg.*;
import wycli.cfg.Configuration.Schema;
import wycli.commands.HelpSystem;
import wycli.lang.Command;
import wycli.lang.Package;
import wycli.lang.Plugin;
import wycli.util.LocalPackageRepository;
import wycli.util.RemotePackageRepository;
import wycli.util.StdPackageResolver;
import wycli.util.CommandParser;
import wycc.util.Pair;
import wycc.util.ZipFile;
import wycc.lang.Path;

/**
 * Provides a command-line interface to the Whiley Compiler Collection. This is
 * responsible for various tasks, such as loading various configuration files
 * from disk, activating plugins, parsing command-line arguments and actually
 * activating the tool itself.
 *
 * @author David J. Pearce
 *
 */
public class Main implements Command.Environment {
	/**
	 * Path to the dependency repository within the global root.
	 */
	public static final Path DEFAULT_REPOSITORY_PATH = Path.fromString("repository");

	public static final Command.Descriptor[] DEFAULT_COMMANDS = {
			HelpSystem.DESCRIPTOR, wycli.commands.BuildSystem.DESCRIPTOR
	};

	// ========================================================================
	// Instance Fields
	// ========================================================================
	private Logger logger = Logger.NULL;
	private Build.Meter meter = Build.NULL_METER;
	/**
	 * Package resolver is reponsible for resolving packages in remote repositories and caching them in the
	 * global repository.
	 */
	private final Package.Resolver resolver;
	/**
	 * Plugin environment provides access to information sourced from the plugins, such as available
	 * content-types, commands, etc.
	 */
	private final Plugin.Environment env;
	/**
	 * The main repository for storing build artifacts and source files which is properly versioned.
	 */
	private final Build.Repository repository;
	/**
	 *
	 */
	private final Schema localSchema;

	public Main(Plugin.Environment env, Iterable<Build.Artifact> entries, Content.Root packageRepository)
			throws IOException {
		this.env = env;
		this.repository = new ByteRepository(env, entries);
		this.localSchema = constructSchema();
		// Setup package resolver
		this.resolver = new StdPackageResolver(this, new RemotePackageRepository(this, packageRepository));
	}

	@Override
	public List<Command.Descriptor> getCommandDescriptors() {
		return env.getCommandDescriptors();
	}

	@Override
	public List<Command.Platform> getCommandPlatforms() {
		return env.getCommandPlatforms();
	}

	@Override
	public Package.Resolver getPackageResolver() {
		return resolver;
	}

	@Override
	public Content.Registry getContentRegistry() {
		return env;
	}

	@Override
	public Build.Repository getRepository() {
		return repository;
	}

	@Override
	public Configuration get(Path path) {
		try {
			ArrayList<Configuration> files = new ArrayList<>();
			// Pull out all configuration files upto the root
			while (path != null) {
				ConfigFile cf = repository.get(ConfigFile.ContentType, path.append("wy"));
				if (cf != null) {
					Configuration c = cf.toConfiguration(localSchema, false);
					files.add(c);
				}
				path = path.parent();
			}
			// Construct the combinator
			return new ConfigurationCombinator(files.toArray(new Configuration[files.size()]));
		} catch(IOException e) {
			// FIXME: this is a hack for now. A better solution is to read all configuration
			// files into memory at the beginning.
			throw new RuntimeException(e);
		}
	}

	@Override
	public Build.Meter getMeter() {
		return meter;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
		this.env.setLogger(logger);
	}

	public void setMeter(Build.Meter meter) {
		this.meter = meter;
	}

	private Schema constructSchema() {
		List<Command.Platform> buildPlatforms = getCommandPlatforms();
		List<Command.Descriptor> cmdDescriptors = getCommandDescriptors();

		Configuration.Schema[] schemas = new Configuration.Schema[buildPlatforms.size() + cmdDescriptors.size() + 1];
		int index = 0;
		schemas[index++] = Schemas.PACKAGE;
		for (int i = 0; i != buildPlatforms.size(); ++i) {
			Command.Platform platform = buildPlatforms.get(i);
			schemas[index++] = platform.getConfigurationSchema();
		}
		for (int i = 0; i != cmdDescriptors.size(); ++i) {
			Command.Descriptor cmd = cmdDescriptors.get(i);
			schemas[index++] = cmd.getConfigurationSchema();
		}
		// Construct combined schema
		return Configuration.toCombinedSchema(schemas);
	}

	// ==================================================================
	// Main Method
	// ==================================================================

	public static void main(String[] args) throws Exception {
		Logger logger = BOOT_LOGGER;
		// Determine system-wide directory. This contains configuration relevant to the
		// entire ecosystem, such as the set of active plugins.
		DirectoryRoot SystemDir = determineSystemRoot();
		// Read the system configuration file
		Configuration system = readConfigFile(SystemDir, Path.fromString("wy"), logger, Schemas.SYSTEM_CONFIG_SCHEMA);
		// Construct plugin environment and activate plugins
		Plugin.Environment penv = activatePlugins(system, logger);
		// Register content type for configuration files
		penv.register(Content.Type.class, ConfigFile.ContentType);
		penv.register(Content.Type.class, ZipFile.ContentType);
		// Determine user-wide directory
		DirectoryRoot globalDir = determineGlobalRoot(logger, penv);
		// Identify repository
		Content.Root repositoryDir = globalDir.subroot(DEFAULT_REPOSITORY_PATH);
		// Determine top-level directory and relative path
		Pair<File, Path> lrp = determineLocalRootDirectory();
		File localDir = lrp.first();
		Path path = lrp.second();
		// Construct build directory
		File buildDir = determineBuildDirectory(localDir, logger);
		// Construct workding directory
		DirectoryRoot dir = new DirectoryRoot(penv, localDir);
		// Extract build artifacts
		List<Build.Artifact> artifacts = new ArrayList<>();
		for (Content content : dir) {
			if (content instanceof Build.Artifact) {
				artifacts.add((Build.Artifact) content);
			}
		}
		// Construct command environment!
		Main menv = new Main(penv, artifacts, repositoryDir);
		// Execute the given command
		int exitCode = exec(menv, path, args);
		// Write back all artifacts to the working director
		for(Build.Artifact b : menv.getRepository().last()) {
			dir.put(b.getPath(), b);
		}
		// Flush working directory to disk
		dir.flush();
		// Done
		System.exit(exitCode);
	}

	public static int exec(Main menv, Path path, String[] args) {
		// Add default descriptors
		menv.getCommandDescriptors().addAll(Arrays.asList(DEFAULT_COMMANDS));
		// Construct environment and execute arguments
		Command.Descriptor descriptor = wycli.commands.Root.DESCRIPTOR(menv.getCommandDescriptors());
		// Parse the given command-line
		Command.Template template = new CommandParser(descriptor).parse(args);
		// Apply verbose setting
		boolean verbose = template.getOptions().get("verbose", Boolean.class);
		int profile = template.getOptions().get("profile", Integer.class);
		if(verbose || profile > 0) {
			// Configure environment
			menv.setLogger(BOOT_LOGGER);
			menv.setMeter(new Meter("Build", BOOT_LOGGER, profile));
		}
		// Done
		try {
			// Create command instance
			Command instance = descriptor.initialise(menv);
			// Execute command
			boolean ec = instance.execute(path,template);
			// Done
			return ec ? 0 : 1;
		} catch(SyntacticException e) {
			e.outputSourceError(System.err, false);
			if (verbose) {
				printStackTrace(System.err, e);
			}
			return 1;
		} catch (Exception e) {
			System.err.println("Internal failure: " + e.getMessage());
			if(verbose) {
				e.printStackTrace();
			}
			return 2;
		}
	}

	// ==================================================================
	// Helpers
	// ==================================================================

	/**
	 * Determine the system root. That is, the installation directory for the
	 * compiler itself.
	 *
	 * @param tool
	 * @return
	 * @throws IOException
	 */
	private static DirectoryRoot determineSystemRoot() throws IOException {
		String whileyhome = System.getenv("WHILEYHOME");
		if (whileyhome == null) {
			System.err.println("error: WHILEYHOME environment variable not set");
			System.exit(-1);
		}
		return new DirectoryRoot(BOOT_REGISTRY, new File(whileyhome));
	}

	/**
	 * Determine the global root. That is, the hidden whiley directory in the user's
	 * home directory (e.g. ~/.whiley).
	 *
	 * @param tool
	 * @return
	 * @throws IOException
	 */
	private static DirectoryRoot determineGlobalRoot(Logger logger, Content.Registry registry) throws IOException {
		String userhome = System.getProperty("user.home");
		File whileydir = new File(userhome + File.separator + ".whiley");
		if (!whileydir.exists()) {
			logger.logTimedMessage("mkdir " + whileydir.toString(), 0, 0);
			whileydir.mkdirs();
		}
		return new DirectoryRoot(registry, whileydir);
	}

	/**
	 * Determine the build root. That is, the hidden whiley directory at the top-level of the build system.
	 *
	 * @param dir root path of the build system we are in.
	 * @return
	 * @throws IOException
	 */
	private static File determineBuildDirectory(File dir, Logger logger) throws IOException {
		File whileydir = new File(dir + File.separator + ".whiley");
		if (!whileydir.exists()) {
			logger.logTimedMessage("mkdir " + whileydir.toString(), 0, 0);
			whileydir.mkdirs();
		}
		// NOTE: should not be a file repository!
		return whileydir;
	}

	/**
	 * Determine where the root of this project is. This is the nearest enclosing
	 * directory containing a "wy.toml" file. The point is that we may be operating
	 * in some subdirectory of the project and want the tool to automatically search
	 * out the real root for us.
	 *
	 * @return
	 * @throws IOException
	 */
	private static Pair<File, Path> determineLocalRootDirectory() throws IOException {
		// Search for inner configuration.
		File inner = findConfigFile(new File("."));
		if (inner == null) {
			throw new IllegalArgumentException("unable to find build configuration (\"wy.toml\")");
		}
		// Search for enclosing configuration (if applicable).
		File outer = findConfigFile(inner.getParentFile());
		if (outer == null) {
			// No enclosing configuration found.
			return new Pair<>(inner, Path.ROOT);
		} else {
			// Calculate relative path
			String path = inner.getPath().replace(outer.getPath(), "").replace(File.separatorChar, '/');
			// Done
			return new Pair<>(outer, Path.fromString(path));
		}
	}

	/**
	 * Activate the set of registed plugins which the tool uses. Currently this list
	 * is statically determined, but eventually it will be possible to dynamically
	 * add plugins to the system.
	 *
	 * @param verbose
	 * @param locations
	 * @return
	 */
	private static Plugin.Environment activatePlugins(Configuration global, Logger logger) {
		Plugin.Environment env = new Plugin.Environment(logger);
		// Determine the set of install plugins
		List<Path> plugins = global.matchAll(Filter.fromString("plugins/*"));
		// start modules
		for (Path id : plugins) {
			Value.UTF8 activator = global.get(Value.UTF8.class, id);
			// Only activate if enabled
			try {
				Class<?> c = Class.forName(activator.toString());
				Plugin.Activator instance = (Plugin.Activator) c.newInstance();
				env.activate(instance);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		// Done
		return env;
	}

	private static File findConfigFile(File dir) {
		// Traverse up the directory hierarchy
		while (dir != null && dir.exists() && dir.isDirectory()) {
			File wyf = new File(dir + File.separator + "wy.toml");
			if (wyf.exists()) {
				return dir;
			}
			// Traverse back up the directory hierarchy looking for a suitable directory.
			dir = dir.getParentFile();
		}
		// If we get here then it means we didn't find a root, therefore just use
		// current directory.
		return null;
	}

	/**
	 * Used for reading the various configuration files prior to instantiating the
	 * main tool itself.
	 */
	public static Content.Registry BOOT_REGISTRY = new Content.DefaultRegistry()
			.register(ConfigFile.ContentType, "toml").register(ZipFile.ContentType, "zip");

	/**
	 * Simple default logger
	 */
	public static Logger BOOT_LOGGER = new Logger.Default(System.err);

	/**
	 * Attempt to read a configuration file from a given root.
	 *
	 * @param name
	 * @param root
	 * @return
	 * @throws IOException
	 */
	public static Configuration readConfigFile(DirectoryRoot root, Path id, Logger logger,
			Configuration.Schema... schemas) throws IOException {
		// Combine schemas together
		Configuration.Schema schema = Configuration.toCombinedSchema(schemas);
		try {
			// Read the configuration file
			ConfigFile cf = root.get(ConfigFile.ContentType, id);
			// Log the event
			logger.logTimedMessage("Read " + root.getDirectory() + "/" + id + ".toml", 0, 0);
			// Construct configuration according to given schema
			return cf.toConfiguration(schema, false);
		} catch (SyntacticException e) {
			e.outputSourceError(System.out, false);
			System.exit(-1);
			return null;
		}
	}

	/**
	 * Print a complete stack trace. This differs from Throwable.printStackTrace()
	 * in that it always prints all of the trace.
	 *
	 * @param out
	 * @param err
	 */
	private static void printStackTrace(PrintStream out, Throwable err) {
		out.println(err.getClass().getName() + ": " + err.getMessage());
		for (StackTraceElement ste : err.getStackTrace()) {
			out.println("\tat " + ste.toString());
		}
		if (err.getCause() != null) {
			out.print("Caused by: ");
			printStackTrace(out, err.getCause());
		}
	}


	public static class Meter implements Build.Meter {
		private final String name;
		private final Logger logger;
		private final int depth;
		private Meter parent;
		private final long time;
		private final long memory;
		private final Map<String,Integer> counts;

		public Meter(String name, Logger logger, int depth) {
			this.name = name;
			this.logger = logger;
			this.depth = depth;
			this.parent = null;
			this.time = System.currentTimeMillis();
			this.memory = Runtime.getRuntime().freeMemory();
			this.counts = new HashMap<>();
		}

		@Override
		public Build.Meter fork(String name) {
			if(depth > 0) {
				Meter r = new Meter(name,logger,depth-1);
				r.parent = this;
				return r;
			} else {
				return wycc.lang.Build.NULL_METER;
			}
		}

		@Override
		public void step(String tag) {
			Integer i = counts.get(tag);
			if (i == null) {
				i = 1;
			} else {
				i = i + 1;
			}
			counts.put(tag, i);
		}

		@Override
		public void done() {
			long t = System.currentTimeMillis();
			long m = Runtime.getRuntime().freeMemory();
			logger.logTimedMessage(name, t - time, m - memory);
			ArrayList<String> keys = new ArrayList<>(counts.keySet());
			Collections.sort(keys);
			for(String key : keys) {
				logger.logTimedMessage(name + "@" + key + "(" + counts.get(key) + " steps)", 0, 0);
			}
		}
	}
}
