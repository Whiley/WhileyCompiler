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

package wyil.lang;

import java.util.*;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;
import wycc.util.Pair;
import wyil.util.*;

/**
 * <p>
 * Represents a complete sequence of bytecode instructions. For example, every
 * function or method body is a single Block. Likewise, the invariant for a give
 * type is a Block. Finally, a Block permits attributes to be attached to every
 * bytecode it contains. An example attribute is one for holding the location of
 * the source code which generated the bytecode.
 * </p>
 * 
 * <p>
 * Every Block has a number of dedicated input variables, and an arbitrary
 * number of additional temporary variables. Each variable is allocated to a
 * slot number, starting from zero and with all inputs coming first. 
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class CodeBlock extends ArrayList<CodeBlock.Entry> implements List<CodeBlock.Entry> {
	private final int numInputs;
			
	public CodeBlock(int numInputs) {		
		this.numInputs = numInputs;
	}
	
	public CodeBlock(int numInputs, Collection<Entry> entries) {
		super(entries);		
		this.numInputs = numInputs;
	}

	// ===================================================================
	// Accessor Methods
	// ===================================================================

	/**
	 * Return the number of input variables for this block.
	 * 
	 * @return
	 */
	public int numInputs() {
		return numInputs;
	}
	
	/**
	 * Determine the number of slots used in this block.
	 * 
	 * @return
	 */
	public int numSlots() {		
		HashSet<Integer> slots = new HashSet<Integer>();
		for(Entry s : this) {
			s.code.registers(slots);
		}
		int r = 0;
		for(int i : slots) {
			r = Math.max(r,i+1);
		}		
		return Math.max(numInputs,r);
	}

	/**
	 * Determine the exact slots used in this block.
	 * 
	 * @return
	 */
	public Set<Integer> slots() {
		HashSet<Integer> slots = new HashSet<Integer>();
		for(Entry s : this) {
			s.code.registers(slots);
		}
		return slots;
	}
					
	// ===================================================================
	// Append Methods
	// ===================================================================	

	/**
	 * Append a bytecode onto the end of this block. It is assumed that the
	 * bytecode employs the same environment as this block.
	 * 
	 * @param code
	 *            --- bytecode to append
	 * @param attributes
	 *            --- attributes associated with bytecode.
	 */
	public boolean add(Code code, Attribute... attributes) {
		return add(new Entry(code,attributes));
	}

	/**
	 * Append a bytecode onto the end of this block. It is assumed that the
	 * bytecode employs the same environment as this block.
	 * 
	 * @param code
	 *            --- bytecode to append
	 * @param attributes
	 *            --- attributes associated with bytecode.
	 */
	public boolean add(Code code, Collection<Attribute> attributes) {
		return add(new Entry(code,attributes));		
	}
	
	// ===================================================================
	// Insert Methods
	// ===================================================================
	
	/**
	 * <p>Insert a bytecode at a given position in this block. It is assumed that
	 * the bytecode employs the same environment as this block. The bytecode at
	 * the given position (and any after it) are shifted one position down.</p>
	 * 
	 * @param index --- position to insert at.
	 * @param code --- bytecode to insert at the given position.
	 * @param attributes
	 */
	public void add(int index, Code code, Attribute... attributes) {
		add(index,new Entry(code,attributes));
	}
	
	/**
	 * <p>Insert a bytecode at a given position in this block. It is assumed that
	 * the bytecode employs the same environment as this block. The bytecode at
	 * the given position (and any after it) are shifted one position down.</p>
	 * 
	 * @param index --- position to insert at.
	 * @param code --- bytecode to insert at the given position.
	 * @param attributes
	 */
	public void add(int index, Code code, Collection<Attribute> attributes) {
		add(index,new Entry(code,attributes));
	}

	// ===================================================================
	// Replace and Remove Methods
	// ===================================================================

	/**
	 * <p>
	 * Replace the bytecode at a given position in this block with another. It
	 * is assumed that the bytecode employs the same environment as this block.
	 * </p>
	 *
	 * @param index --- position of bytecode to replace.
	 * @param code --- bytecode to replace with.
	 * @param attributes
	 */
	public void set(int index, Code code, Attribute... attributes) {
		set(index,new Entry(code,attributes));
	}
	
	/**
	 * <p>
	 * Replace the bytecode at a given position in this block with another. It
	 * is assumed that the bytecode employs the same environment as this block.
	 * </p>
	 * 
	 * @param index --- position of bytecode to replace.
	 * @param code --- bytecode to replace with.
	 * @param attributes
	 */
	public void set(int index, Code code, Collection<Attribute> attributes) {
		set(index, new Entry(code, attributes));
	}
	
	// ===================================================================
	// Miscellaneous
	// =================================================================== 

	/**
	 * Represents an individual bytecode and those attributes currently
	 * associated with it (if any) in the block.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Entry extends SyntacticElement.Impl {
		public final Code code;
		
		public Entry(Code code, Attribute... attributes) {
			super(attributes);
			this.code = code;
		}
		
		public Entry(Code code, Collection<Attribute> attributes) {
			super(attributes);
			this.code = code;
		}
				
		public String toString() {
			String r = code.toString();
			if(attributes().size() > 0) {
				r += " # ";
				boolean firstTime=true;
				for(Attribute a : attributes()) {
					if(!firstTime) {
						r += ", ";
					}
					firstTime=false;
					r += a;
				}
			}
			return r;
		}
	}			
}
