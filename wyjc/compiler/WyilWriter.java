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

package wyjc.compiler;

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
