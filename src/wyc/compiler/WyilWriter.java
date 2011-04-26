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

package wyc.compiler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import wyil.ModuleLoader;
import wyil.lang.Module;
import wyil.util.Logger;
import wyil.io.*;
import wyjvm.io.*;
import wyjvm.lang.ClassFile;

public class WyilWriter implements Compiler.Stage {	
	private boolean writeTypes;
	private boolean writeLabels;
	private boolean writeAttrs;
	
	public WyilWriter(ModuleLoader loader, Map<String, String> options) {
		writeTypes = options.containsKey("types");		
		writeLabels = options.containsKey("labels");
		writeAttrs = !options.containsKey("nattrs");
	}
	
	public String name() {
		return "wyil file writer";
	}	
	
	public Module process(Module m, Logger logout) {
		long start = System.currentTimeMillis();
				
		// calculate filename
		String filename = m.filename().replace(".whiley", ".wyil");
		try {
			FileOutputStream out = new FileOutputStream(filename);
			WyilFileWriter writer = new WyilFileWriter(out);
			writer.setWriteTypes(writeTypes);
			writer.setWriteLabels(writeLabels);
			writer.setWriteAttributes(writeAttrs);
			writer.write(m);
			out.flush();
			logout.logTimedMessage("[" + m.filename() + "] wyil file written",
					System.currentTimeMillis() - start);			
		} catch(IOException ex) {
			logout.logTimedMessage("[" + m.filename()
					+ "] failed writing wyil file (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start);
		}
		return m;
	}	
	
}
