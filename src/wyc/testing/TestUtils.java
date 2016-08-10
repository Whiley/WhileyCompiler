package wyc.testing;

import static org.junit.Assert.fail;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.util.StdProject;
import wyc.WycMain;
import wyc.util.WycBuildTask;
import wycommon.util.Pair;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.DirectoryRoot;
import wyfs.util.Trie;
import wyil.Main.Registry;
import wyil.io.WyilFilePrinter;
import wyil.io.WyilFileReader;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.util.interpreter.Interpreter;

/**
 * Provides some simple helper functions used by all test harnesses.
 *
 * @author David J. Pearce
 *
 */
public class TestUtils {

	/**
	 * Scan a directory to get the names of all the whiley source files
	 * in that directory. The list of file names can be used as input
	 * parameters to a JUnit test.
	 *
	 * If the system property <code>test.name.contains</code> is set,
	 * then the list of files returned will be filtered. Only file
	 * names that contain the property will be returned. This makes it
	 * possible to run a subset of tests when testing interactively
	 * from the command line.
	 *
	 * @param srcDir The path of the directory to scan.
	 */
	public static Collection<Object[]> findTestNames(String srcDir) {
		final String suffix = ".whiley";
		String containsFilter = System.getProperty("test.name.contains");

		ArrayList<Object[]> testcases = new ArrayList<Object[]>();
		for (File f : new File(srcDir).listFiles()) {
			// Check it's a file
			if (!f.isFile()) continue;
			String name = f.getName();
			// Check it's a whiley source file
			if (!name.endsWith(suffix)) continue;
			// Get rid of ".whiley" extension
			String testName = name.substring(0, name.length() - suffix.length());
			// If there's a filter, check the name matches
			if (containsFilter != null && !testName.contains(containsFilter)) continue;
			testcases.add(new Object[] { testName });
		}
		// Sort the result by filename
		Collections.sort(testcases, new Comparator<Object[]>() {
				@Override
				public int compare(Object[] o1, Object[] o2) {
					return ((String) o1[0]).compareTo((String) o2[0]);
				}
		});
		return testcases;
	}

	/**
	 * Run the Whiley Compiler with the given list of arguments.
	 *
	 * @param args
	 *            --- list of command-line arguments to provide to the Whiley
	 *            Compiler.
	 * @return
	 */
	public static Pair<Integer,String> compile(String... args) {
		ByteArrayOutputStream syserr = new ByteArrayOutputStream();
		ByteArrayOutputStream sysout = new ByteArrayOutputStream();
		int exitCode = new WycMain(new WycBuildTask(), WycMain.DEFAULT_OPTIONS, sysout, syserr)
				.run(args);
		byte[] errBytes = syserr.toByteArray();
		byte[] outBytes = sysout.toByteArray();
		String output = new String(errBytes) + new String(outBytes);
		return new Pair<Integer,String>(exitCode,output);
	}

	/**
	 * Execute a given WyIL file using the default interpreter.
	 * 
	 * @param wyilDir
	 *            The root directory to look for the WyIL file.
	 * @param id
	 *            The name of the WyIL file
	 * @throws IOException
	 */
	public static void execWyil(String wyilDir, Path.ID id) throws IOException {
		Type.Method sig = Type.Method(Collections.<Type>emptyList(), Collections.<String>emptySet(),
				Collections.<String>emptyList(), Collections.<Type>emptyList());
		NameID name = new NameID(id,"test");
		Build.Project project = initialiseProject(wyilDir);
		new Interpreter(project,null).execute(name,sig);
	}

	private static Build.Project initialiseProject(String wyilDir) throws IOException {
		Content.Registry registry = new Registry();
		DirectoryRoot wyilRoot = new DirectoryRoot(wyilDir,registry);
		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();
		roots.add(wyilRoot);
		return new StdProject(roots);
	}

	/**
	 * Execute a given class file using the "java" command, and return all
	 * output written to stdout. In the case of some kind of failure, write the
	 * generated stderr stream to this processes stdout.
	 *
	 * @param classPath
	 *            Class path to use when executing Java code. Note, directories
	 *            can always be safely separated with '/', and path separated
	 *            with ':'.
	 * @param srcDir
	 *            Path to root of package containing class. Note, directories
	 *            can always be safely separated with '/'.
	 * @param className
	 *            Name of class to execute
	 * @param args
	 *            Arguments to supply on the command-line.
	 * @return All output generated from the class that was written to stdout.
	 */
	public static String execClass(String classPath, String srcDir, String className, String... args) {
		try {
			classPath = classPath.replace('/', File.separatorChar);
			classPath = classPath.replace(':', File.pathSeparatorChar);
			srcDir = srcDir.replace('/', File.separatorChar);
			String tmp = "java -cp " + classPath + " " + className;
			for (String arg : args) {
				tmp += " " + arg;
			}
			Process p = Runtime.getRuntime().exec(tmp, null, new File(srcDir));

			StringBuffer syserr = new StringBuffer();
			StringBuffer sysout = new StringBuffer();
			new StreamGrabber(p.getErrorStream(), syserr);
			new StreamGrabber(p.getInputStream(), sysout);
			int exitCode = p.waitFor();
			if (exitCode != 0) {
				System.err
						.println("============================================================");
				System.err.println(className);
				System.err
						.println("============================================================");
				System.err.println(syserr);
				return null;
			} else {
				return sysout.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Problem running compiled test");
		}

		return null;
	}

	/**
	 * Compare the output of executing java on the test case with a reference
	 * file. If the output differs from the reference output, then the offending
	 * line is written to the stdout and an exception is thrown.
	 *
	 * @param output
	 *            This provides the output from executing java on the test case.
	 * @param referenceFile
	 *            The full path to the reference file. This should use the
	 *            appropriate separator char for the host operating system.
	 */
	public static void compare(String output, String referenceFile) {
		try {
			BufferedReader outReader = new BufferedReader(new StringReader(
					output));
			BufferedReader refReader = new BufferedReader(new FileReader(
					new File(referenceFile)));

			boolean match = true;
			while (true) {
				String l1 = refReader.readLine();
				String l2 = outReader.readLine();
				if (l1 != null && l2 != null) {
					if (!l1.equals(l2)) {
						System.err.println(" < " + l1);
						System.err.println(" > " + l2);
						match = false;
					}
				} else if (l1 != null) {
					System.err.println(" < " + l1);
					match = false;
				} else if (l2 != null) {
					System.err.println(" > " + l2);
					match = false;
				} else {
					break;
				}
			}
			if (!match) {
				System.err.println();
				fail("Output doesn't match reference");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	/**
	 * Grab everything produced by a given input stream until the End-Of-File
	 * (EOF) is reached. This is implemented as a separate thread to ensure that
	 * reading from other streams can happen concurrently. For example, we can
	 * read concurrently from <code>stdin</code> and <code>stderr</code> for
	 * some process without blocking that process.
	 *
	 * @author David J. Pearce
	 *
	 */
	static public class StreamGrabber extends Thread {
		private InputStream input;
		private StringBuffer buffer;

		StreamGrabber(InputStream input, StringBuffer buffer) {
			this.input = input;
			this.buffer = buffer;
			start();
		}

		public void run() {
			try {
				int nextChar;
				// keep reading!!
				while ((nextChar = input.read()) != -1) {
					buffer.append((char) nextChar);
				}
			} catch (IOException ioe) {
			}
		}
	}
}
