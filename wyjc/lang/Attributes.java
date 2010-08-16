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

package wyjc.lang;

import wyil.lang.*;
import wyil.lang.Type;

/**
 * An attribute is simply a piece of information that we may wish to
 * attached to a syntactic element,
 * 
 * @author djp
 * 
 */
public interface Attributes {

	public static final class Fun implements Attribute {
		public final wyil.lang.Type.Fun type;

		public Fun(wyil.lang.Type.Fun type) {
			this.type = type;
		}
	}
	
	public static final class Module implements Attribute {
		public final ModuleID module;
		
		public Module(ModuleID module) {
			this.module = module;
		}
	}
	
	public static final class Alias implements Attribute {
		public final Expr alias;
		
		public Alias(Expr alias) {
			this.alias = alias;
		}
	}	
}