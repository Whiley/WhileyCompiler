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
package wycli.lang;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import wycc.lang.Build;
import wycc.lang.Content;
import wycc.lang.Build.Repository;
import wycc.lang.Build.Meter;
import wycc.lang.Path;
import wycc.util.Logger;
import wycli.cfg.Configuration;

/**
 * A command which can be executed (e.g. from the command-line)
 *
 * @author David J. Pearce
 *
 */
public interface Command {

	/**
	 * Get a descriptor for this command.
	 *
	 * @return
	 */
	public Descriptor getDescriptor();

	/**
	 * Perform any necessary initialisation for this command (e.g. opening
	 * resources).
	 */
	public void initialise();

	/**
	 * Perform any necessary finalisation for this command (e.g. closing resources).
	 */
	public void finalise();

	/**
	 * Execute this command with the given arguments. Every invocation of this
	 * function occurs after a single call to <code>initialise()</code> and before
	 * any calls are made to <code>finalise()</code>. Observer, however, that this
	 * command may be executed multiple times.
	 */
	public boolean execute(Path path, Template template) throws Exception;

	/**
	 * Defines an environment in which commands can be executed.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Environment {
		/**
		 * Get the command descriptors available in this environment.
		 *
		 * @return
		 */
		List<Command.Descriptor> getCommandDescriptors();

		/**
		 * Get the package resolver associated with this environment.
		 *
		 * @return
		 */
		Package.Resolver getPackageResolver();

		/**
		 * Get the registry used for resolving content types in this environment.
		 *
		 * @return
		 */
		public Content.Registry getContentRegistry();

		/**
		 * Get the set of build platforms which are active in this environment.
		 *
		 * @return
		 */
		public List<Platform> getCommandPlatforms();

		/**
		 * Get the build repository associated with this environment.
		 *
		 * @return
		 */
		public Repository getRepository();

		/**
		 * Get the configuration associated with a given build path.  The key is that the configuration at a given path
		 * is the combination of all configurations on parent paths leading to that point, plus any specific
		 * configuration at that point.
		 *
		 * @param path
		 * @return
		 */
		public Configuration get(Path path);

		/**
		 * Get the top-level meter for this environment.
		 *
		 * @return
		 */
		public Meter getMeter();

		/**
		 * Get the default logger used in this environment.
		 */
		public Logger getLogger();
	}

	/**
	 * Provides a high-level concept of a target platform. These are registered by
	 * various backends to support different compilation targets.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Platform {
		/**
		 * Get the unique name identifying this platform.
		 *
		 * @return
		 */
		public String getName();

		/**
		 * Get the configuration schema for this build platform. This specifies the
		 * permitted set of options for the platform, including their types, etc.
		 *
		 * @return
		 */
		public Configuration.Schema getConfigurationSchema();

		/**
		 * Initialise this platform to produce a build task which can be used for
		 * compiling.
		 *
		 * @param project
		 *            Enclosing project for this build task
		 * @return
		 */
		public Build.Task initialise(Path path, Environment environment) throws IOException;
	}

	/**
	 * Provides a descriptive information about this command. This includes
	 * information such as the name of the command, a description of the command as
	 * well as the set of arguments which are accepted.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Descriptor {
		/**
		 * Get the name of this command. This should uniquely identify the command in
		 * question.
		 *
		 * @return
		 */
		public String getName();

		/**
		 * Get a description of this command.
		 *
		 * @return
		 */
		public String getDescription();

		/**
		 * Get the list of configurable options for this command.
		 *
		 * @return
		 */
		public List<Option.Descriptor> getOptionDescriptors();

		/**
		 * Get the list of configurable options for this command.
		 *
		 * @return
		 */
		public Configuration.Schema getConfigurationSchema();

		/**
		 * Get descriptors for any sub-commands of this command.
		 *
		 * @return
		 */
		public List<Descriptor> getCommands();

		/**
		 * Initialise the corresponding command in a given environment.
		 *
		 * @param environment   Provides access to the various runtime features provided
		 *                      by the environment. This includes various important
		 *                      details gleaned from the configuration, such as the set
		 *                      of available build platforms and content types.
		 * @param options       List of option modifiers for this command.
		 * @return
		 */
		public Command initialise(Command.Environment parent);
	}

	/**
	 * A generic interface for access command options.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Options {
		/**
		 * Check whether a given option is given.
		 *
		 * @param name
		 * @return
		 */
		public boolean has(String name);
		/**
		 * Get the value associate with a given named option.
		 *
		 * @param kind
		 * @return
		 */
		public <T> T get(String name, Class<T> kind);
	}

	/**
	 * Describes a configurable option for a given command.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Option {

		/**
		 * Get the descriptor from which this instance was created.
		 *
		 * @return
		 */
		public Option.Descriptor getDescriptor();

		/**
		 * Get the value associate with this option.
		 *
		 * @param kind
		 * @return
		 */
		public <T> T get(Class<T> kind);

		/**
		 * Provides a descriptor for the option.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Descriptor {
			/**
			 * Get the option name.
			 *
			 * @return
			 */
			public String getName();

			/**
			 * Get the description for the argument
			 * @return
			 */
			public String getArgumentDescription();

			/**
			 * Get a suitable description for the option.
			 *
			 * @return
			 */
			public String getDescription();

			/**
			 * Get the default value for this option (or null if no suitable default).
			 *
			 * @return
			 */
			public Object getDefaultValue();

			/**
			 * Construct a given option from a given argument string.
			 *
			 * @param arg
			 * @return
			 */
			public Option Initialise(String arg);
		}
	}

	public interface Template {
		/**
		 * Get the command being described by this template.
		 *
		 * @return
		 */
		public Command.Descriptor getCommandDescriptor();

		/**
		 * Get the options described by this template, in the order in which they should
		 * be applied.
		 *
		 * @return
		 */
		public Command.Options getOptions();

		/**
		 * Get the arguments described by this template, in the order in which they
		 * should be applied.
		 *
		 * @return
		 */
		public List<String> getArguments();

		/**
		 * Get the child template (if any) given for this template. If no template, then
		 * this returns <code>null</code>.
		 *
		 * @return
		 */
		public Template getChild();
	}

	/**
	 * A generic class for handling option descriptors.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class AbstractOptionDescriptor implements Option.Descriptor {
		private final String name;
		private final String argDescription;
		private final String description;
		private final Object defaultValue;

		AbstractOptionDescriptor(String name, String argDescription, String description, Object defaultValue) {
			this.name = name;
			this.argDescription = argDescription;
			this.description = description;
			this.defaultValue = defaultValue;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getArgumentDescription() {
			return argDescription;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public Object getDefaultValue() {
			return defaultValue;
		}
	}

	public static class OptionValue implements Option {
		private final Option.Descriptor descriptor;
		private final Object contents;

		public OptionValue(Option.Descriptor descriptor, Object contents) {
			this.descriptor = descriptor;
			this.contents = contents;
		}

		@Override
		public Descriptor getDescriptor() {
			return descriptor;
		}

		@Override
		public <T> T get(Class<T> kind) {
			if(kind.isInstance(contents)) {
				return (T) contents;
			} else {
				throw new IllegalArgumentException(
						"expected option value " + kind.getSimpleName() + ", got " + contents);
			}
		}

		@Override
		public String toString() {
			return descriptor.getName() + "=" + contents;
		}
	}

	/**
	 * An integer option which cannot be negative.
	 *
	 * @param name
	 * @param argument
	 * @param description

	 * @return
	 */
	public static Option.Descriptor OPTION_NONNEGATIVE_INTEGER(String name, String description) {
		return OPTION_INTEGER(name, "<n>", description + " (non-negative)", (n) -> (n >= 0), null);
	}

	/**
	 * An integer option which cannot be negative.
	 *
	 * @param name
	 * @param argument
	 * @param description
	 * @param defaultValue
	 *            the default value to use
	 * @return
	 */
	public static Option.Descriptor OPTION_NONNEGATIVE_INTEGER(String name, String description, int defaultValue) {
		return OPTION_INTEGER(name, "<n>", description + " (non-negative, default " + defaultValue + ")",
				(n) -> (n >= 0), defaultValue);
	}


	/**
	 * An integer option which must be positive.
	 *
	 * @param name
	 * @param argument
	 * @param description
	 * @param defaultValue
	 *            the default value to use
	 * @return
	 */
	public static Option.Descriptor OPTION_POSITIVE_INTEGER(String name, String description, int defaultValue) {
		return OPTION_INTEGER(name, "<n>", description + " (positive, default " + defaultValue + ")", (n) -> (n > 0), defaultValue);
	}

	/**
	 * An integer option with a constraint
	 *
	 * @param name
	 * @param description
	 * @return
	 */
	public static Option.Descriptor OPTION_INTEGER(String name, String argument, String description,
			Predicate<Integer> constraint, Integer defaultValue) {
		return new AbstractOptionDescriptor(name, argument, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				int value = Integer.parseInt(arg);
				if (constraint.test(value)) {
					return new OptionValue(this, value);
				} else {
					throw new IllegalArgumentException("invalid integer value");
				}
			}
		};
	}

	/**
	 * An integer option which cannot be negative.
	 *
	 * @param name
	 * @param argument
	 * @param description

	 * @return
	 */
	public static Option.Descriptor OPTION_BOUNDED_DOUBLE(String name, String description, double low, double high) {
		return OPTION_DOUBLE(name, "<n>", description + " (between " + low + ".." + high + ")",
				(n) -> (n >= low && n <= high), low, high, null);
	}

	/**
	 * A decimal option with a constraint
	 *
	 * @param name
	 * @param description
	 * @return
	 */
	public static Option.Descriptor OPTION_DOUBLE(String name, String argument, String description,
			Predicate<Double> constraint, double low, double high, Double defaultValue) {
		return new AbstractOptionDescriptor(name, argument, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				double value = Double.parseDouble(arg);
				if (constraint.test(value)) {
					return new OptionValue(this, value);
				} else {
					throw new IllegalArgumentException("invalid double value");
				}
			}
		};
	}

	public static Option.Descriptor OPTION_FLAG(String name, String description) {
		return new AbstractOptionDescriptor(name, null, description, null) {
			@Override
			public Option Initialise(String arg) {
				if(arg.equals("false") || arg.equals("true")) {
					// If specified then should be true
					return new OptionValue(this, Boolean.parseBoolean(arg));
				} else {
					throw new IllegalArgumentException("invalid argument for " + name + " (expected nothing, \"true\" or \"false\")");
				}
			}
		};
	}


	public static Option.Descriptor OPTION_FLAG(String name, String description,
			boolean defaultValue) {
		return new AbstractOptionDescriptor(name, null, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				if(arg.equals("false") || arg.equals("true")) {
					// If specified then should be true
					return new OptionValue(this, Boolean.parseBoolean(arg));
				} else {
					throw new IllegalArgumentException("invalid argument for " + name + " (expected nothing, \"true\" or \"false\")");
				}
			}
		};
	}


	/**
	 * An string option
	 *
	 * @param name
	 * @param argument
	 * @param description

	 * @return
	 */
	public static Option.Descriptor OPTION_STRING(String name, String description, String defaultValue) {
		return new AbstractOptionDescriptor(name, null, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				return new OptionValue(this, arg);
			}
		};
	}

}
