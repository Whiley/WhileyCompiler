// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wycc.testing;

import static org.junit.Assert.fail;

import java.io.*;
import java.util.Properties;

import wyc.WycMain;
import wycc.util.WyccBuildTask;

public class TestHarness {
	private static final String WYJC_PATH="../../../modules/wyjc/src/";
	private static final String WYIL_PATH="../../../modules/wyil/src/";
	private static String WYRT_PATH;
	private static final String WYCC_Script = "../../../bin/wycc -E -q ";

	static {

		// The purpose of this is to figure out what the proper name for the
		// wyrt file is.

		File file = new File("../../lib/");
		for(String f : file.list()) {
			if(f.startsWith("wyrt-v")) {
				WYRT_PATH="../../lib/" + f;
			}
		}
	}
	private String sourcepath;    // path to source files	
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
		this.sourcepath = srcPath.replace('/', File.separatorChar);
		this.outputPath = outputPath.replace('/', File.separatorChar);
		this.outputExtension = outputExtension;		
	}
	
	/**
	 * Compile and execute a test case, whilst comparing its output against the
	 * sample output. The test fails if either it does not compile, or running
	 * it does not produce the sample output.
	 * 
	 * @param name
	 *            Name of the test to run. This must correspond to an executable
	 *            Java file in the srcPath of the same name.
	 */
	protected void runTest(String name) {
		runTest(name, "", 0);
	}
	protected void runTest(String name, String opts) {
		runTest(name, opts, 0);
	}
	protected void runTest(String name, String opts, int optf) {

		//String filename = sourcepath + File.separatorChar + name + ".whiley";
		//if (compile("-wd", sourcepath, "-wp", WYRT_PATH, filename) != WycMain.SUCCESS) {
		//	fail("couldn't compile test!");
		//} else {
			String output = run(sourcepath, name, opts);
			if (optf == 0) { 
				compare(output, outputPath + File.separatorChar + name + "." + outputExtension);
			} else { 
				compare(output, outputPath + File.separatorChar + name + "." + outputExtension + optf);
			}
		//}
	}

	/**
	 * Compile and execute a test case with verification enabled, whilst
	 * comparing its output against the sample output. The test fails if either
	 * it does not compile, or running it does not produce the sample output.
	 * Enabling verification means that the verified must pass the given files
	 * and, hence, this is all about testing the verifier.
	 * 
	 * @param name
	 *            Name of the test to run. This must correspond to an executable
	 *            Java file in the srcPath of the same name.
	 */
	protected void verifyRunTest(String name) {
		String filename = sourcepath + File.separatorChar + name + ".whiley";

		//if (compile("-wd", sourcepath, "-wp", WYRT_PATH, "-X",
		//		"verification:enable=true", filename) != WycMain.SUCCESS) {
		//	fail("couldn't compile test!");
		//} else {
			String output = run(sourcepath, name);
			compare(output, outputPath + File.separatorChar + name + "."
					+ outputExtension);
		//}
	}
	
	/**
	 * Compile and execute a syntactically invalid test case with verification
	 * disabled. Since verification is disabled, runtime checks are instead
	 * inserted to catch constraint violations (which would otherwise be caught
	 * by the verifier). Therefore, the expectation is that it should fail at
	 * runtime with an assertion failure and, hence, the test fails if it
	 * doesn't do this.
	 * 
	 * @param name
	 *            Name of the test to run. This must correspond to an executable
	 *            Java file in the srcPath of the same name.
	 */
	protected void runtimeFailTest(String name) {				
		String fullName = sourcepath + File.separatorChar + name + ".whiley";
		
		//if (compile("-wd", sourcepath, "-wp", WYRT_PATH, fullName) != WycMain.SUCCESS) { 
		//	fail("couldn't compile test!");
		//} else {
			String output = run(sourcepath,name);				
			if(output != null) {
				fail("test should have failed at runtime!");
			}
		//}
	}
	
	private static int compile(String... args) {
		return new WycMain(new WyccBuildTask(), WycMain.DEFAULT_OPTIONS).run(args);
	}
	private static String run(String path, String name) {
		return run(path, name, "");
	}
	private static String run(String path, String name, String opts) {
		// **** should really check the opts to make sure that they are kosher.
		try {
			// We need to have
			//String classpath = "." + File.pathSeparator + WYIL_PATH
			//		+ File.pathSeparator + WYJC_PATH;
			//classpath = classpath.replace('/', File.separatorChar);
			String tmp =  WYCC_Script + opts + name + ".whiley";
			// tmp+= "& sleep 1";
			int cnt = 0;
			
			//Properties foo = System.getProperties();
			//System.err.println(foo.getProperty("user.dir"));
			
			
			StringBuffer syserr = new StringBuffer(4*1024);
			StringBuffer sysout = new StringBuffer(4*1024);
			
			// the subprocess is really three threads
			Process p = Runtime.getRuntime().exec(tmp, null, new File(path));
			//ProcessBuilder bud = new ProcessBuilder();
			//System.err.println(bud.directory().getName());

			//bud.directory(new File(path));
			//if (opts == "" || opts == "  "){
			//	bud.command("../../../bin/wycc", "-E", "-q", name + ".whiley");
			//} else {
			//	bud.command("../../../bin/wycc", "-E", "-q", opts, name + ".whiley");
			//}
			//bud.command("../../../bin/wycc", "-E", "-q", opts + name + ".whiley");
			//Process p = bud.start();
			//BufferedReader rd1 = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//BufferedReader rd2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			StreamGrabber p3 = new StreamGrabber(p.getInputStream(), sysout);
			StreamGrabber p2 = new StreamGrabber(p.getErrorStream(), syserr);

			int exitCode = p.waitFor();
			// we will not be ready to examine the output until all three threads have finished
			while (!p2.done || !p3.done) {
				Thread.currentThread().yield();
				// the rest of this loop is likely just garbage; the done's and the yield are the real work
				cnt++;
				if (cnt > 1024*1024*1024) {
					System.err.println("============================================================");
					System.err.println("	foo");
					System.err.println("============================================================");
					return null;
				}
			}

			if (exitCode != 0) {
				System.err.println("============================================================");
				System.err.println(name);
				System.err.println("============================================================");
				System.err.println(sysout);
				System.err.println("============================================================");
				System.err.println("name is " + name);
				System.err.println("path is " + path);
				System.err.println("exitCode is " + exitCode);
				System.err.println("============================================================");
				System.err.println(syserr);
				return null;
			//} else {
			//	return sysout.toString();
			}
			tmp = sysout.toString();
			if (opts == "  ") {
				System.err.println("============================================================");
				System.err.println(name + " yielded results of length " + tmp.length());
				System.err.println("============================================================");
				
			}
			return tmp;
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
		//private BufferedReader inb;
		private StringBuffer buffer;
		public volatile boolean done;

		StreamGrabber(InputStream input,StringBuffer buffer) {
			//BufferedReader rd1 = new BufferedReader(new InputStreamReader(p.getInputStream()))
			this.input = input;
			//this.inb = new BufferedReader(new InputStreamReader(input));
			this.buffer = buffer;
			this.done = false;
			this.start();
		}

		public void run() {
			//String line;
			int nextChar;
			
			try {
				//while (true) {
				//	line = inb.readLine();
				//	if (line == null) {
				//		break;
				//	}
				//	this.buffer.append(line + "\n");
				//}
				// keep reading!!
				while ((nextChar = input.read()) != -1) {
					buffer.append((char) nextChar);
				}
			} catch (IOException ioe) {
				System.err.println("============================================================");
				System.err.println(ioe);
				System.err.println("============================================================");
				
			} finally {
				done = true;
			}
		}
		
	}
}
