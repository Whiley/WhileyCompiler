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

package wyjc.ast.attrs;

import java.util.*;
import java.util.concurrent.*;


public class SyntacticElementImpl  implements SyntacticElement {
	private List<Attribute> attributes;
	
	public SyntacticElementImpl() {
		// I use copy on write here, since for the most part I don't expect
		// attributes to change, and hence can be safely aliased. But, when they
		// do change I need fresh copies.
		attributes = new CopyOnWriteArrayList<Attribute>();
	}
	
	public SyntacticElementImpl(Attribute x) {
		attributes = new ArrayList<Attribute>();
		attributes.add(x);
	}
	
	public SyntacticElementImpl(Collection<Attribute> attributes) {
		this.attributes = new ArrayList<Attribute>(attributes);			
	}
	
	public SyntacticElementImpl(Attribute[] attributes) {
		this.attributes = new ArrayList<Attribute>(Arrays.asList(attributes));			
	}
	
	public List<Attribute> attributes() { return attributes; }
	
	public <T extends Attribute> T attribute(Class<T> c) {
		for(Attribute a : attributes) {
			if(c.isInstance(a)) {
				return (T) a;
			}
		}
		return null;
	}		
}



