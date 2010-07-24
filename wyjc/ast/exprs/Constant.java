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

package wyjc.ast.exprs;

import java.util.*;

import wyjc.ast.*;
import wyjc.ast.attrs.*;
import wyjc.lang.Attribute;
import wyil.lang.ModuleID;
import wyone.core.*;
import wyone.theory.logic.*;

/**
 * This class represents a variable used in an expression.
 */
public class Constant extends Variable {	
	protected final ModuleID mid;

	public Constant(String var, ModuleID mid, Attribute... attributes) {
		super(var,attributes);		
		this.mid = mid;
	}

	public Constant(String var, ModuleID mid, Collection<Attribute> attributes) {
		super(var,attributes);		
		this.mid = mid;
	}
	
	public ModuleID module() {
		return mid;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Constant) {
			Constant c = (Constant) o;
			return mid.equals(c.mid) && var.equals(c.var);
		} 
		return false;		
	}
	
	public int hashCode() {
		return mid.hashCode() ^ var.hashCode();
	}
	
	public String toString() { return var; }
}
