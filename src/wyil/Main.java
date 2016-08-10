package wyil;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import wybs.lang.Build;
import wybs.util.StdProject;
import wybs.lang.NameID;
import wybs.lang.SyntaxError;
import wybs.lang.SyntaxError.InternalFailure;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyil.io.WyilFilePrinter;
import wyil.io.WyilFileReader;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.util.interpreter.Interpreter;

public class Main {

	public static PrintStream errout;

	/**
	 * Initialise the error output stream so as to ensure it will display
	 * unicode characters (when possible). Additionally, extract version
	 * information from the enclosing jar file.
	 */
	static {
		try {
			errout = new PrintStream(System.err, true, "UTF-8");
		} catch(Exception e) {
			errout = System.err;
		}
	}

	/**
	 * The purpose of the binary file filter is simply to ensure only binary
	 * files are loaded in a given directory root. It is not strictly necessary
	 * for correct operation, although hopefully it offers some performance
	 * benefits.
	 */
	public static final FileFilter wyilFileFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.getName().endsWith(".wyil") || f.isDirectory();
		}
	};

	/**
	 * Default implementation of a content registry. This associates wyil files
	 * with the appropriate content type.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Registry implements Content.Registry {
		public void associate(Path.Entry e) {
			String suffix = e.suffix();

			if(suffix.equals("wyil")) {
				e.associate(WyilFile.ContentType, null);
			}
		}

		public String suffix(Content.Type<?> t) {
			if(t == WyilFile.ContentType) {
				return "wyil";
			} else {
				return "dat";
			}
		}
	}


	private static Build.Project initialiseProject(String wyilDir) throws IOException {
		Content.Registry registry = new Registry();
		DirectoryRoot wyilRoot = new DirectoryRoot(".",wyilFileFilter,registry);
		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();
		roots.add(wyilRoot);
		return new StdProject(roots);
	}

	public static void main(String[] args) {
		boolean verbose = true;
		try {
			FileInputStream fin = new FileInputStream(args[0]);
			WyilFile wf = new WyilFileReader(fin).read();
			new WyilFilePrinter(System.out).apply(wf);
			// FIXME: this is all a hack for now
			Type.Method sig = Type.Method(Collections.<Type>emptyList(), Collections.<String>emptySet(),
					Collections.<String>emptyList(), Collections.<Type>emptyList());
			NameID name = new NameID(wf.getEntry().id(),"test");
			Build.Project project = initialiseProject(".");
			Constant[] returns = new Interpreter(project,System.out).execute(name,sig);
			if(returns != null) {
				for(int i=0;i!=returns.length;++i) {
					if(i != 0) {
						System.out.println(", ");
					}
					System.out.println(returns[i]);
				}
			}
			//
		} catch (InternalFailure e) {
			e.outputSourceError(System.err);
			if(verbose) {
				e.printStackTrace(errout);
			}
		} catch (SyntaxError e) {
			e.outputSourceError(errout);
			if (verbose) {
				e.printStackTrace(errout);
			}
		} catch (Throwable e) {
			errout.println("internal failure (" + e.getMessage() + ")");
			if (verbose) {
				e.printStackTrace(errout);
			}

		}
	}
}
