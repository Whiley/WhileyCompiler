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

package wyjc.testing;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.types.Environment.Variable;

import wyjc.Main;
import wyjc.testing.TestHarness.StreamGrabber;

public abstract class BenchHarness {

  private final String srcPath; // path to source files
  private final int times;

  public BenchHarness(String srcPath, int times) {
    this.srcPath = srcPath.replace('/', File.separatorChar);
    this.times = times;
  }

  protected long runBench(String name, String... params)
      throws InterruptedException, IOException {
    final String[] args = new String[3];
    args[0] = "-wp";
    args[1] = "lib/wyrt.jar";
    args[2] = srcPath + File.separatorChar + name + ".whiley";

    if (!compile(args)) {
      return -1;
    } else {
      long time = 0;
      for (int i = 0; i < times; ++i) {
      	long next = run(srcPath, name, params);
      	if (next < 0) {
      		return next;
      	}
      	time += next;
      }
      return time / times;
    }
  }

  private static boolean compile(String... args) {
    return Main.run(args) == 0;
  }

  private static long run(String path, String name, String... params)
      throws InterruptedException, IOException {
  	int index = name.lastIndexOf('/') + 1;
  	path = ensureSeparator(path) + name.substring(0, index);
  	String home = ensureSeparator(System.getenv("WHILEY_HOME"));
    String classpath = home + "lib/wyrt.jar" + File.pathSeparator + ".";
    classpath = classpath.replace('/', File.separatorChar);
    String tmp = "java -cp " + classpath + " " + name.substring(index);
    
    for (String param : params) {
      tmp += " " + param;
    }

    long time = System.currentTimeMillis();
    Process p = Runtime.getRuntime().exec(tmp, null, new File(path));
    
    if (p.waitFor() != 0) {
      System.err.println("Run returned with bad exit code.");
      return -1;
    }
    
    return System.currentTimeMillis() - time;
  }
  
  private static String ensureSeparator(String input) {
  	if (!input.endsWith(File.separator)) {
  		input += File.separator;
  	}
  	return input;
  }

}
