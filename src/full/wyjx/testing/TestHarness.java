// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjx.testing;

import static org.junit.Assert.fail;

import java.io.*;

import wyjx.Main;

public class TestHarness {
	private String srcPath;    // path to source files	
	private String outputPath; // path to output files
	private String outputExtension; // the extension of output files
	
	/**
	 * Construct a test harness object.
	 * 
	 * @param srcPath
	 *            The path to the source files to be tested
	 * @param outputPath
	 *            The path to the sample output files to compare against.
	 * @param outputExtension
	 *            The extension of output files
	 * @param verification
	 *            if true, the verifier is used.
	 */
	public TestHarness(String srcPath, String outputPath,
			String outputExtension) {
		this.srcPath = srcPath.replace('/', File.separatorChar);
		this.outputPath = outputPath.replace('/', File.separatorChar);
		this.outputExtension = outputExtension;		
	}
	
	/**
	 * Compile and execute a test case, whilst comparing its output against the
	 * sample output.
	 * 
	 * @param name
	 *            Name of the test to run. This must correspond to an executable
	 *            Java file in the srcPath of the same name.
	 */
	protected void runTest(String name, String... params) {				
		final String[] args = new String[3 + params.length];
		args[0] = "-wp";
		args[1] = "lib/wyrt.jar";		
		for (int i = 0; i != params.length; ++i) {
			args[i + 2] = params[i];
		}
		args[args.length - 1] = srcPath + File.separatorChar + name + ".whiley";
		
		if (new Main().run(args) != 0) {
			fail("couldn't compile test!");
		} else {
			String output = run(srcPath, name, args);
			compare(output, outputPath + File.separatorChar + name + "."
					+ outputExtension);
		}
	}
	
	
	protected void parserFailTest(String name) {
		name = srcPath + File.separatorChar + name + ".whiley";

		if (compile("-wp", "lib/wyrt.jar", name) != Main.PARSE_ERROR) {
			fail("Test parsed when it shouldn't have!");
		}

	}
	
	protected void contextFailTest(String name) {				
		name = srcPath + File.separatorChar + name + ".whiley";

		if (compile("-wp", "lib/wyrt.jar",name) != Main.CONTEXT_ERROR) {
			fail("Test compiled when it shouldn't have!");
		}
	}
	
	protected void verificationFailTest(String name) {				
		name = srcPath + File.separatorChar + name + ".whiley";

		if (compile("-wp", "lib/wyrt.jar", "-V", name) != Main.CONTEXT_ERROR) {
			fail("Test compiled when it shouldn't have!");
		}
	}

	protected void verificationRunTest(String name) {				
		String fullName = srcPath + File.separatorChar + name + ".whiley";
		
		if(compile("-wp", "lib/wyrt.jar", "-V", fullName) != 0) { 
			fail("couldn't compile test!");
		} else {
			String output = run(srcPath,name,"-wp", "lib/wyrt.jar");				
			compare(output, outputPath + File.separatorChar + name
					+ "." + outputExtension);
		}
	}
		
	protected void runtimeFailTest(String name) {				
		String fullName = srcPath + File.separatorChar + name + ".whiley";
		
		if(compile("-wp", "lib/wyrt.jar",fullName) != 0) { 
			fail("couldn't compile test!");
		} else {
			String output = run(srcPath,name,"-wp", "lib/wyrt.jar");				
			if(output != null) {
				fail("test should have failed at runtime!");
			}
		}
	}
	
	private static int compile(String... args) {
		return new Main().run(args);
	}
	
	private static String run(String path, String name, String... args) {
		try {
			// We need to have
			String classpath = "../../../" + File.pathSeparator + "."
					+ File.pathSeparator + "../../../lib/wyrt.jar";
			classpath = classpath.replace('/', File.separatorChar);
			String tmp = "java -cp " + classpath + " " + name;
			Process p = Runtime.getRuntime().exec(tmp, null, new File(path));

			StringBuffer syserr = new StringBuffer();
			StringBuffer sysout = new StringBuffer();
			new StreamGrabber(p.getErrorStream(), syserr);
			new StreamGrabber(p.getInputStream(), sysout);
			int exitCode = p.waitFor();
			System.err.println(syserr);
			if (exitCode != 0) {
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
	 * file.
	 * 
	 * @param output
	 *            This provides the output from executing java on the test case.
	 * @param referenceFile
	 *            The full path to the reference file. This should use the
	 *            appropriate separator char for the host operating system.
	 */
	private static void compare(String output, String referenceFile) {
		try {			
			BufferedReader outReader = new BufferedReader(new StringReader(output));
			BufferedReader refReader = new BufferedReader(new FileReader(
					new File(referenceFile)));
			
			while (refReader.ready() && outReader.ready()) {
				String a = refReader.readLine();
				String b = outReader.readLine();
				
				if (a.equals(b)) {
					continue;
				} else {
					System.err.println(" > " + a);
					System.err.println(" < " + b);
					throw new Error("Output doesn't match reference");
				}
			}
						
			String l1 = outReader.readLine();
			String l2 = refReader.readLine();
			if (l1 == null && l2 == null) return;
			do {
				l1 = outReader.readLine();
				l2 = refReader.readLine();
				if (l1 != null) {
					System.err.println(" < " + l1);
				} else if (l2 != null) {
					System.err.println(" > " + l2);
				}
			} while(l1 != null && l2 != null);			
			
			fail("Files do not match");
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	static public class StreamGrabber extends Thread {
		private InputStream input;
		private StringBuffer buffer;

		StreamGrabber(InputStream input,StringBuffer buffer) {
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
