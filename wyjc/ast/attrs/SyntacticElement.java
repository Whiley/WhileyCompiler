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

import java.util.List;


/**
 * A Syntactic Element represents any part of the file for which is relevant
 * to the syntactic structure of the file, and in particular parts we may
 * wish to add information too (e.g. line numbers, types, etc).
 * 
 * @author djp
 * 
 */
public interface SyntacticElement {
	/**
     * Get the list of attributes associated with this syntactice element.
     * 
     * @return
     */
	public List<Attribute> attributes();
	
	/**
     * Get the first attribute of the given class type. This is useful
     * short-hand.
     * 
     * @param c
     * @return
     */
	public <T extends Attribute> T attribute(Class<T> c);
}
