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

package wyjc.ast.types.unresolved;

import wyjc.util.ModuleID;

/**
 * An unresolved type represents a possible type, as determined by the parser.
 * Essentially, this means it was an unknown identifier found in a types
 * position. The key issue here, is that it's not until type resolution that we
 * will know whether or not this is really a valid type.
 * 
 * @author djp
 * 
 */
public class UserDefType implements UnresolvedType {
	private String name;
	private ModuleID module;
	
	public UserDefType(String name) {
		this.name = name;
	}
	
	public UserDefType(String name, ModuleID module) {
		this.name = name;
	}
	
	public void resolve(ModuleID module) {
		this.module = module;
	}
	
	public String name() {
		return name;
	}
	
	public ModuleID module() {
		return module;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof UserDefType) {
			UserDefType dt = (UserDefType) o;
			if(module == null) {
				return name.equals(dt.name) && module == dt.module;
			} else {
				return name.equals(dt.name) && module.equals(dt.module);
			}
		}
		return false;
	}
	
	public String toString() {
		return name;
	}		
}
