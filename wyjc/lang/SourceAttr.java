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



/**
 * A Source location Attribute identifies a position within an originating
 * source file. This is useful for producing good error messages.
 * 
 * @author djp
 * 
 */
public class SourceAttr implements Attribute {
	private String filename;
	private int start;	
	private int end;	

	public SourceAttr(String filename, int start, int end) {
		this.filename = filename;
		this.start = start;
		this.end = end;		
	}

	/**
	 * Get the source filename that this line corresponds to.
	 * @return
	 */
	public String filename() { return filename; }
	
	/**
	 * Get the index of the first character in this element.
	 * 
	 * @return 
	 */		
	public int start() { return start; }

	/**
	 * Get the index of the last character in this element.
	 * 
	 * @return 
	 */
	public int end() { return end; }
	
	public String toString() {
		return filename + ":" + start + ":" + end;
	}
}
