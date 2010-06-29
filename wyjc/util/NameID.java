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

package wyjc.util;

import java.io.File;
import java.util.*;

/**
 * A Name Identifier consists of a module, and a name within that module. The
 * purpose of this is to provide a uniform way of referring to modules +
 * names throughout the compiler.
 * 
 * @author djp
 * 
 */
public class NameID {
	private ModuleID module;
	private String name;
		
	public NameID(ModuleID module, String name) {		
		this.module = module;
		this.name = name;
	}

	public String name() {
		return name;
	}
	
	public ModuleID module() {
		return module;
	}
	
	public String toString() {
		return module + ":" + name;
	}
	
	public int hashCode() {
		return name.hashCode() ^ module.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof NameID) {
			NameID u = (NameID) o;			
			return u.module.equals(module) && u.name.equals(name);						
		}
		return false;
	}
}
