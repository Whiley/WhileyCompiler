package wyc.util;

import java.io.File;
import java.util.*;

/**
 * OptArg is a small utility for parsing command-line options. It helps to take
 * some of the hassle out of building the front-end of a Whiley compiler.
 * 
 * @author djp
 * 
 */
public final class OptArg {
	public final HashMap<String,Object> options;
	public final ArrayList<String> others;
	
	OptArg(HashMap<String,Object> options, ArrayList<String> others) {
		this.options = options;
		this.others = others;
	}
	
	interface Kind {
		void process(String arg, String option, Map<String,Object> options);
	}

	public final static NOARG NOARG = new NOARG();
	public final static STRING STRING = new STRING();
	public final static INT INT = new INT();
	public final static PATHLIST PATHLIST = new PATHLIST();
	
	private static final class NOARG implements Kind {
		public void process(String arg, String option, Map<String,Object> options) {
			options.put(arg,null);
		}
	}
	
	private static final class STRING implements Kind {
		public void process(String arg, String option, Map<String,Object> options) {
			options.put(arg,option);
		}
	}
	
	private static final class INT implements Kind {
		public void process(String arg, String option, Map<String,Object> options) {
			options.put(arg,Integer.parseInt(option));
		}
	}
			
	private static final class PATHLIST implements Kind {
		public void process(String arg, String option, Map<String,Object> options) {
			options.put(arg, option.split(File.pathSeparator));
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
	}
	
	public static OptArg parseOptions(String[] args,
			Map<String, Kind> template, Map<String, Object> defaults) {
		HashMap<String,Object> options = new HashMap(defaults);
		ArrayList<String> others = new ArrayList();
		
		for (int i = 0; i != args.length; ++i) {
			if (args[i].startsWith("-")) {
				String arg = args[i].substring(1,args[i].length());				
				Kind k = template.get(arg);
				if(k instanceof NOARG) {
					k.process(arg,null,options);
				} else {				
					k.process(arg,args[++i],options);
				}
			} else {
				others.add(args[i]);
			}
		}
		return new OptArg(options,others);
	}
}
