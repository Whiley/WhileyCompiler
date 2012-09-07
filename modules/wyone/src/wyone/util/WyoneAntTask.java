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

package wyone.util;

import java.io.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

import wyone.core.WyoneFile;
import wyone.io.JavaFileWriter;
import wyone.spec.*;

/**
 * The most basic ant task ever for compiling wyone files.
 * 
 * @author djp
 * 
 */
public class WyoneAntTask extends MatchingTask {
	private String sourceFile;
	private String outputFile;
	
	public WyoneAntTask() {
	}
	
	public void setSource(String filename) {
		this.sourceFile = filename;
	}
	
	public void setOutput(String filename) {
		this.outputFile = filename;
	}
	
    public void execute() throws BuildException { 
    	try {
    		SpecLexer lexer = new SpecLexer(sourceFile);
			SpecParser parser = new SpecParser(sourceFile, lexer.scan());
			SpecFile sf = parser.parse();
			new TypeInference().infer(sf);
			WyoneFile wyf = new Spec2WyoneBuilder().build(sf);
			// new SpecFileWriter(oFile).write(spec);
			new JavaFileWriter(new FileWriter(outputFile)).write(wyf);	
    	} catch(Exception e) {
    		throw new BuildException(e);
    	}
    }       	
}
