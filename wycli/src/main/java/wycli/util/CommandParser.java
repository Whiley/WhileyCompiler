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
package wycli.util;

import java.util.ArrayList;
import java.util.List;

import wycli.lang.Command;
import wycli.lang.Command.Option;
import wycli.lang.Command.Template;
import wycc.util.Pair;

/**
 * <p>
 * A generic mechanism for parsing command-line options, which is perhaps
 * reminiscent of optarg, etc. The key here is the structure of command-line
 * arguments:
 * </p>
 *
 * <pre>
 * wy <tool / project options> (command <options> <values>)*
 * </pre>
 *
 * <p>
 * Each level corresponds to a deeper command within the hierarchy. Furthermore,
 * each also corresponds to entries in a configuration file as well.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class CommandParser {
	/**
	 * The list of command roots.
	 */
	private final Command.Descriptor root;

	public CommandParser(Command.Descriptor root) {
		this.root = root;
	}

	/**
	 * Parse a given set of command-line arguments to produce an appropriate command
	 * template.
	 *
	 * @param args
	 */
	public Command.Template parse(String[] args) {
		return parse(root,args,0);
	}

	/**
	 * Parse a given set of command-line arguments starting from a given index
	 * position to produce an appropriate command template.
	 *
	 * @param args
	 * @param index
	 */
	protected Command.Template parse(Command.Descriptor root, String[] args, int index) {
		ArrayList<Command.Option> options = new ArrayList<>();
		ArrayList<String> arguments = new ArrayList<>();
		//
		Command.Template sub = null;
		while (index < args.length) {
			String arg = args[index];
			if (isLongOption(arg)) {
				options.add(parseLongOption(root, args[index]));
			} else if (isCommand(arg, root.getCommands())) {
				Command.Descriptor cmd = getCommandDescriptor(arg, root.getCommands());
				sub = parse(cmd, args, index + 1);
				break;
			} else {
				arguments.add(arg);
			}
			index = index + 1;
		}
		//

		Command.Options optionMap = new OptionsMap(options, root.getOptionDescriptors());
		//
		return new ConcreteTemplate(root, optionMap, arguments, sub);
	}

	protected boolean isLongOption(String arg) {
		return arg.startsWith("--");
	}

	public Option parseLongOption(Command.Descriptor cmd, String arg) {
		List<Option.Descriptor> descriptors = cmd.getOptionDescriptors();
		arg = arg.replace("--", "");
		String[] splits = arg.split("=");
		String key = splits[0];
		String value = "true";
		if (splits.length > 1) {
			value = splits[1];
		} else if (splits.length > 2) {
			throw new IllegalArgumentException("invalid option: " + arg);
		}
		for (int i = 0; i != descriptors.size(); ++i) {
			Option.Descriptor descriptor = descriptors.get(i);
			if (descriptor.getName().equals(key)) {
				// matched
				return descriptor.Initialise(value);
			}
		}
		throw new IllegalArgumentException("invalid option: " + arg);
	}

	protected boolean isCommand(String arg, List<Command.Descriptor> descriptors) {
		for (int i = 0; i != descriptors.size(); ++i) {
			Command.Descriptor descriptor = descriptors.get(i);
			if (arg.equals(descriptor.getName())) {
				return true;
			}
		}
		return false;
	}

	protected Command.Descriptor getCommandDescriptor(String arg, List<Command.Descriptor> descriptors) {
		for (int i = 0; i != descriptors.size(); ++i) {
			Command.Descriptor descriptor = descriptors.get(i);
			if (arg.equals(descriptor.getName())) {
				return descriptor;
			}
		}
		throw new IllegalArgumentException("invalid command: " + arg);
	}


	/**
	 * Parse an option which is either a string of the form "--name" or
	 * "--name=data". Here, name is an arbitrary string and data is a string
	 * representing a data value.
	 *
	 * @param arg
	 *            The option argument to be parsed.
	 * @return
	 */
	private static Pair<String,Object> parseOption(String arg) {
		arg = arg.substring(2);
		String[] split = arg.split("=");
		Object data = null;
		if(split.length > 1) {
			data = parseData(split[1]);
		}
		return new Pair<>(split[0],data);
	}

	/**
	 * Parse a given string representing a data value into an instance of Data.
	 *
	 * @param str
	 *            The string to be parsed.
	 * @return
	 */
	private static Object parseData(String str) {
		if (str.equals("true")) {
			return true;
		} else if (str.equals("false")) {
			return false;
		} else if (Character.isDigit(str.charAt(0))) {
			// number
			return Integer.parseInt(str);
		} else {
			return str;
		}
	}

	protected static class ConcreteTemplate implements Command.Template {
		private final Command.Descriptor descriptor;
		private final Command.Options options;
		private final List<String> arguments;
		private final Command.Template sub;

		public ConcreteTemplate(Command.Descriptor descriptor,  Command.Options options, List<String> arguments,
				Command.Template sub) {
			this.descriptor = descriptor;
			this.options = options;
			this.arguments = arguments;
			this.sub = sub;
		}

		@Override
		public Command.Descriptor getCommandDescriptor() {
			return descriptor;
		}

		@Override
		public List<String> getArguments() {
			return arguments;
		}

		@Override
		public Template getChild() {
			return sub;
		}

		@Override
		public Command.Options getOptions() {
			return options;
		}
	}

	public static class OptionsMap implements Command.Options {
		private Option.Descriptor[] descriptors;
		private Option[] options;

		public OptionsMap(List<Option> options, List<Option.Descriptor> descriptors) {
			this.options = options.toArray(new Option[options.size()]);
			this.descriptors = descriptors.toArray(new Option.Descriptor[descriptors.size()]);
		}

		@Override
		public boolean has(String name) {
			for (int i = 0; i != options.length; ++i) {
				Option option = options[i];
				if (option.getDescriptor().getName().equals(name)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public <T> T get(String name, Class<T> kind) {
			// Check for given values
			for (int i = 0; i != options.length; ++i) {
				Option option = options[i];
				if (option.getDescriptor().getName().equals(name)) {
					return option.get(kind);
				}
			}
			// Check for default values
			for (int i = 0; i != descriptors.length; ++i) {
				Option.Descriptor d = descriptors[i];
				Object val = d.getDefaultValue();
				if (kind.isInstance(val)) {
					return (T) val;
				}
			}
			throw new IllegalArgumentException("invalid option " + name);
		}

		@Override
		public String toString() {
			String r = "{";
			for (int i = 0; i != options.length; ++i) {
				if(i!=0) {
					r = r + ",";
				}
				Option option = options[i];
				r = r + option.getDescriptor().getName();
				r = r + "=" + option.get(Object.class);
			}
			return r + "}";
		}
	}
}
