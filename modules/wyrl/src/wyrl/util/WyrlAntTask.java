// Copyright (c) 2012, David J. Pearce (djp@ecs.vuw.ac.nz)
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

package wyrl.util;

import java.io.*;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

import wyrl.core.SpecFile;
import wyrl.core.TypeExpansion;
import wyrl.core.TypeInference;
import wyrl.io.JavaFileWriter;
import wyrl.io.JavaFileWriter;
import wyrl.io.SpecLexer;
import wyrl.io.SpecParser;

/**
 * The most basic ant task ever for compiling wyrl files.
 *
 * @author djp
 *
 */
public class WyrlAntTask extends MatchingTask {
	private File srcdir;
	private String sourceFile;
	private String outputFile;
	private boolean debug;

	public WyrlAntTask() {
	}

	public void setSrcdir(File dir) throws IOException {
		this.srcdir = dir;
	}

	public void setSource(String filename) {
		this.sourceFile = filename;
	}

	public void setOutput(String filename) {
		this.outputFile = filename;
	}

	public void setDebug(boolean flag) {
		this.debug = flag;
	}

	public void execute() throws BuildException {
		try {
			long start = System.currentTimeMillis();

			File sfile = new File(srcdir, sourceFile);
			File ofile = new File(srcdir, outputFile);

			SpecLexer lexer = new SpecLexer(new FileReader(sfile));
			SpecParser parser = new SpecParser(sfile, lexer.scan());
			SpecFile sf = parser.parse();

			int delta = 0;
			for (File dfile : dependencies(sf)) {
				if (dfile.lastModified() > ofile.lastModified()) {
					delta++;
				}
			}

			if(debug) {
				long end = System.currentTimeMillis();
				log("Parsed wyrl file ... [" + (end - start) + "ms]");
			}

			if (delta > 0) {
				// just try to neaten up the English ...
				if(delta == 1) {
					log("Compiling wyrl file (" + delta
							+ " modified dependency)");
				} else {
					log("Compiling wyrl file (" + delta
							+ " modified dependencies)");
				}

				// carry on by performing type expansion

				start = System.currentTimeMillis();
				new TypeExpansion().expand(sf);
				if(debug) {
					long end = System.currentTimeMillis();
					log("Performed type expansion ... [" + (end - start) + "ms]");
				}

				start = System.currentTimeMillis();
				new TypeInference().infer(sf);
				if(debug) {
					long end = System.currentTimeMillis();
					log("Performed type inference ... [" + (end - start) + "ms]");
				}

				start = System.currentTimeMillis();
				BufferedWriter bw = new BufferedWriter(new FileWriter(ofile),65536);
				new JavaFileWriter(bw).write(sf);
				if(debug) {
					long end = System.currentTimeMillis();
					log("Wrote target file ... [" + (end - start) + "ms]");
				}
			} else {
				log("Compiling 0 wyrl file(s)");
			}
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

    protected ArrayList<File> dependencies(SpecFile f) {
    	ArrayList<File> deps = new ArrayList<File>();
    	dependencies(f,deps);
    	return deps;
    }

    protected void dependencies(SpecFile f, ArrayList<File> files) {
    	files.add(f.file);

    	for(SpecFile.Decl d : f.declarations) {
    		if(d instanceof SpecFile.IncludeDecl) {
    			SpecFile.IncludeDecl id = (SpecFile.IncludeDecl) d;
    			dependencies(id.file,files);
    		}
    	}
    }
}
