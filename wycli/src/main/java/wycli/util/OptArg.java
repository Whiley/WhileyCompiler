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

import java.io.File;
import java.io.PrintStream;
import java.util.*;

import wycli.util.OptArg;

/**
 * A small utility for parsing command-line options. It helps to take some of
 * the hassle out of building the front-end of a Whiley compiler.
 *
 * @author David J. Pearce
 *
 */
public final class OptArg {
	/**
	 * The long form of the option. (e.g. for "-version", the long form is
	 * "version")
	 */
	public final String option;

	/**
	 * The short form of the option. (e.g. for "-version", the short form might
	 * be "v" as in "-v")
	 */
	public final String shortForm;

	/**
	 * The kind of argument accepted by this option (if any).
	 */
	public final Kind argument;

	/**
	 * A description of the option. This is used when printing out "usage"
	 * information.
	 */
	public final String description;

	/**
	 * A default value for the option (assuming it accepts an argument). This
	 * may be null if there is no default value.
	 */
	public final Object defaultValue;

	/**
	 * Construct an option object which does not accept an argument.
	 *
	 * @param option
	 * @param argument
	 * @param description
	 * @param defaultValue
	 */
	public OptArg(String option,String description) {
		this.option = option;
		this.shortForm = null;
		this.argument = null;
		this.description = description;
		this.defaultValue = null;
	}

	/**
	 * Construct an option object with a short form which does not accept an argument.
	 *
	 * @param option
	 * @param shortForm
	 * @param argument
	 * @param description
	 * @param defaultValue
	 */
	public OptArg(String option,String shortForm, String description) {
		this.option = option;
		this.shortForm = shortForm;
		this.argument = null;
		this.description = description;
		this.defaultValue = null;
	}

	/**
	 * Construct an option object which accepts an argument.
	 *
	 * @param option
	 * @param argument
	 * @param description
	 * @param defaultValue
	 */
	public OptArg(String option, Kind argument,
			String description) {
		this.option = option;
		this.shortForm = null;
		this.argument = argument;
		this.description = description;
		this.defaultValue = null;
	}

	/**
	 * Construct an option object with a short form which accepts an argument.
	 *
	 * @param option
	 * @param shortForm
	 * @param argument
	 * @param description
	 * @param defaultValue
	 */
	public OptArg(String option, String shortForm, Kind argument,
			String description) {
		this.option = option;
		this.shortForm = shortForm;
		this.argument = argument;
		this.description = description;
		this.defaultValue = null;
	}

	/**
	 * Construct an option object which accepts an argument and has a default value.
	 *
	 * @param option
	 * @param argument
	 * @param description
	 * @param defaultValue
	 */
	public OptArg(String option, Kind argument,
			String description, Object defaultValue) {
		this.option = option;
		this.shortForm = null;
		this.argument = argument;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	/**
	 * Construct an option object with a short form which accepts an argument and has a default value.
	 *
	 * @param option
	 * @param argument
	 * @param description
	 * @param defaultValue
	 */
	public OptArg(String option, String shortForm, Kind argument,
			String description, Object defaultValue) {
		this.option = option;
		this.shortForm = shortForm;
		this.argument = argument;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	interface Kind {
		void process(String arg, String option, Map<String,Object> options);
	}

	public final static STRING STRING = new STRING();
	public final static INT INT = new INT();
	public final static FILE FILE = new FILE();
	public final static FILEDIR FILEDIR = new FILEDIR();
	public final static FILELIST FILELIST = new FILELIST();
	public final static OPTIONSMAP OPTIONSMAP = new OPTIONSMAP();

	private static final class STRING implements Kind {
		@Override
		public void process(String arg, String option, Map<String,Object> options) {
			options.put(arg,option);
		}
		@Override
		public String toString() {
			return "<string>";
		}
	}

	private static final class INT implements Kind {
		@Override
		public void process(String arg, String option, Map<String,Object> options) {
			options.put(arg,Integer.parseInt(option));
		}
		@Override
		public String toString() {
			return "<int>";
		}
	}

	private static final class FILE implements Kind {
		@Override
		public void process(String arg, String option,
				Map<String, Object> options) {
			options.put(arg, new File(option));
		}
		@Override
		public String toString() {
			return "<file>";
		}
	}

	private static final class FILEDIR implements Kind {
		@Override
		public void process(String arg, String option,
				Map<String, Object> options) {
			File dir = new File(option);
			if(!dir.isDirectory()) {
				throw new IllegalArgumentException("invalid path --- " + arg);
			}
			options.put(arg, dir);
		}
		@Override
		public String toString() {
			return "<filedir>";
		}
	}

	private static final class FILELIST implements Kind {
		@Override
		public void process(String arg, String option, Map<String,Object> options) {
			ArrayList<File> rs = new ArrayList<>();
			for(String r : option.split(File.pathSeparator)) {
				rs.add(new File(r));
			}
			options.put(arg, rs);
		}
		@Override
		public String toString() {
			return "<filelist>";
		}
	}

	private static final class OPTIONSMAP implements Kind {
		@Override
		public void process(String arg, String option, Map<String,Object> options) {
			String[] name = option.split(":");
			Map<String, Object> config = Collections.EMPTY_MAP;
			if (name.length > 1) {
				config = splitConfig(name[1]);
			}
			Map<String,Map<String,Object>> values = (Map<String,Map<String,Object>>) options.get(arg);

			if(values == null) {
				values = new HashMap<>();
				options.put(arg, values);
			}

			Map<String,Object> attributes = values.get(name[0]);

			if(attributes == null) {
				values.put(name[0], config);
			} else {
				attributes.putAll(config);
			}
		}
		@Override
		public String toString() {
			return "name:[attribute=value]+";
		}
	}

	/**
	 * Parse options from the list of arguments, removing those which are
	 * recognised. Anything which is not recognised is left as is.
	 *
	 * @param args
	 *            --- the list of argument strings. This is modified by removing
	 *            those which are processed.
	 * @param options
	 *            --- the list of OptArg defining which options should be
	 *            processed
	 * @throws --- a <code>RuntimeException</code> if an unrecognised option is
	 *         encountered (that is, a token starting with '-')..
	 */
	public static Map<String,Object> parseOptions(List<String> args, OptArg... options) {
		HashMap<String,Object> result = new HashMap<>();
		HashMap<String,OptArg> optmap = new HashMap<>();

		for(OptArg opt : options) {
			if(opt.defaultValue != null) {
				result.put(opt.option, opt.defaultValue);
			}
			optmap.put(opt.option, opt);
			optmap.put(opt.shortForm, opt);
		}

		Iterator<String> iter = args.iterator();
		while(iter.hasNext()) {
			String arg = iter.next();
			if (arg.startsWith("-")) {
				arg = arg.substring(1,arg.length());
				OptArg opt = optmap.get(arg);
				if(opt != null) {
					// matched
					iter.remove(); // remove option from args list
					Kind k = opt.argument;
					if(k != null) {
						String param = iter.next();
						iter.remove();
						k.process(opt.option,param,result);
					} else {
						result.put(opt.option,null);
					}
				} else {
					throw new RuntimeException("unknown command-line option: -" + arg);
				}
			}
		}

		return result;
	}

	public static void usage(PrintStream output, OptArg...options) {
		// first, work out gap information
		int gap = 0;
		ArrayList<OptArg> opts = new ArrayList();
		for (OptArg opt : options) {
			opts.add(opt);
			int len = opt.option.length();
			if(opt.argument != null) {
				len = len + opt.argument.toString().length();
			}
			if(opt.shortForm != null) {
				len = len + opt.shortForm.length();
				opts.add(new OptArg(opt.shortForm,opt.argument,opt.description + " [short form]"));
			}
			gap = Math.max(gap, len);
		}

		gap = gap + 1;

		// now, print the information
		for (OptArg opt : opts) {
			output.print("  -" + opt.option);
			int rest = gap - opt.option.length();
			output.print(" ");
			if(opt.argument != null) {
				String arg = opt.argument.toString();
				rest -= arg.length();
				output.print(arg);
			}
			for (int i = 0; i < rest; ++i) {
				output.print(" ");
			}
			output.println(opt.description);
		}
	}

	/**
	 * This splits strings of the form "x=y,v=w" into distinct components and
	 * puts them into a map. In the case of a string like "x,y=z" then x is
	 * loaded with the empty string.
	 *
	 * @param str
	 * @return
	 */
	private static Map<String, Object> splitConfig(String str) {
		HashMap<String, Object> options = new HashMap<>();
		String[] splits = str.split(",");
		for (String s : splits) {
			String[] p = s.split("=");
			options.put(p[0], parseValue(p[1]));
		}
		return options;
	}

	private static Object parseValue(String str) {
		if(str.equals("true")) {
			return Boolean.TRUE;
		} else if(str.equals("false")) {
			return Boolean.FALSE;
		} else if(Character.isDigit(str.charAt(0))) {
			if(str.charAt(str.length()-1) == 'L') {
				return Long.parseLong(str.substring(0,str.length()-1));
			} else {
				return Integer.parseInt(str);
			}
		} else  {
			return str;
		}
	}
}
