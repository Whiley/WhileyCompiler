package wyc.util;

import java.io.File;
import java.io.PrintStream;
import java.util.*;

/**
 * OptArg is a small utility for parsing command-line options. It helps to take
 * some of the hassle out of building the front-end of a Whiley compiler.
 * 
 * @author djp
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
	public final static PATHLIST PATHLIST = new PATHLIST();
	
	private static final class STRING implements Kind {
		public void process(String arg, String option, Map<String,Object> options) {
			options.put(arg,option);
		}
		public String toString() {
			return "<string>";
		}
	}
	
	private static final class INT implements Kind {
		public void process(String arg, String option, Map<String,Object> options) {
			options.put(arg,Integer.parseInt(option));
		}
		public String toString() {
			return "<int>";
		}
	}
			
	private static final class PATHLIST implements Kind {
		public void process(String arg, String option, Map<String,Object> options) {
			options.put(arg, option.split(File.pathSeparator));
		}
		public String toString() {
			return "<path>";
		}
	}
	
	public static final class MULTIARG implements Kind {
		private final Kind element;

		public MULTIARG(Kind element) {
			this.element = element;
		}

		public void process(String arg, String option, Map<String, Object> options) {
			Object o = options.get(arg);
			ArrayList val;
			if(o == null) {
				val = new ArrayList();
			} else {
				val = (ArrayList) o;
			}
			element.process(arg,option,options);
			val.add(options.get(arg));
			options.put(arg,val);			
		}
		
		public String toString() {
			return element.toString();
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
	 */
	public static Map<String,Object> parseOptions(List<String> args, OptArg... options) {
		HashMap<String,Object> result = new HashMap<String,Object>();
		HashMap<String,OptArg> optmap = new HashMap<String,OptArg>();		
		
		for(OptArg opt : options) {
			result.put(opt.option, opt.defaultValue);
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
						k.process(opt.option,param,result);
					}
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
	protected Map<String, String> splitOptions(String str) {
		HashMap<String, String> options = new HashMap<String, String>();
		String[] splits = str.split(",");
		for (String s : splits) {
			String[] p = s.split("=");
			if (p.length == 1) {
				options.put(p[0], "");
			} else {
				options.put(p[0], p[1]);
			}
		}
		return options;
	}
}
