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

import wyil.lang.Module;
import wyil.stages.ModuleTransform;
import wyil.util.Logger;

public class WyilTransform implements Compiler.Stage {
	private String name;
	private ModuleTransform transform;
	
	public WyilTransform(String name, ModuleTransform transform) {
		this.name = name;
		this.transform = transform;
	}
	
	public String name() {
		return name;
	}
	
	public Module process(Module module, Logger logout) {
		long start = System.currentTimeMillis();
			
		try {
			module = transform.apply(module);
			logout.logTimedMessage("[" + module.filename() + "] applied " + name,
					System.currentTimeMillis() - start);
			return module;
		} catch(RuntimeException ex) {
			logout.logTimedMessage("[" + module.filename()
					+ "] failed on " + name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start);			
			throw ex;			
		}
	}
}
