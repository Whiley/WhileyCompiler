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

package wyjvm.attributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.Constant;
import wyjvm.lang.Constant.Info;
import wyjvm.lang.JvmType;

/**
 * Consists of zero or more stack map frames. Each stack map frame specifies
 * (either explicitly or implicitly) a bytecode offset, the verification types
 * for the local variables, and the verification types for the operand stack.
 * 
 * @author David J. Pearce
 * 
 */
public class StackMapTable implements BytecodeAttribute {
	private final Frame[] frames;
	
	public StackMapTable(Frame[] frames) {
		this.frames = frames.clone(); 
	}
	

	@Override
	public String name() {
		// FIXME: put back the right name when the attribute is written properly
		// to disk.
		return "StackMapTable2";
	}

	@Override
	public void write(BinaryOutputStream writer,
			Map<Info, Integer> constantPool, ClassLoader loader)
			throws IOException {
		// TODO: implement me!		
		// only empty attribute written
		writer.write_u2(constantPool.get(new Constant.Utf8(name())));
		writer.write_u4(0);
	}

	@Override
	public void addPoolItems(Set<Info> constantPool, ClassLoader loader) {
		// TODO: implement me!
		Constant.addPoolItem(new Constant.Utf8(name()), constantPool);
	}

	@Override
	public void print(PrintWriter output, Map<Info, Integer> constantPool,
			ClassLoader loader) throws IOException {
		// TODO: implement me!		
	}
	
	/**
	 * Returns the stack frame at the given bytecode index. Observe that this is
	 * not the bytecode offset; rather, it's the index into the array returned
	 * by <code>Code.bytecodes</code>.
	 * 
	 * @param index
	 * @return
	 */
	public Frame frameAt(int index) {
		return frames[index];
	}
	
	/**
	 * Represents a full stack frame.
	 * 
	 * @author David J. Pearce 
	 * 
	 */
	public static class Frame {		
		/**
		 * Number of local variables represented in this frame.
		 */
		public final int numLocals;
		
		/**
		 * Number of stack items represented in this frame.
		 */
		public final int numStackItems;
		
		/**
		 * The array of types for this frame. The length of this array is
		 * numLocals + numStackItems.
		 */
		public final JvmType[] types;
	
		public Frame(int numLocals, int numStackItems,
				JvmType[] types) {
			if (types.length < (numLocals + numStackItems)) {
				throw new IllegalArgumentException("invalid number of types");
			}
			this.numLocals = numLocals;
			this.numStackItems = numStackItems;
			this.types = new JvmType[numLocals + numStackItems];
			for (int i = 0; i != this.types.length; ++i) {
				this.types[i] = types[i];
			}
		}
		

		public String toString() {
			String r = "[";
			
			for(int i=0;i!=numLocals;++i) {
				if(i != 0) {
					r = r + ", ";
				}
				r = r + types[i];
			}
			
			r = r + " | ";
			
			for(int i=0;i!=numStackItems;++i) {
				if(i != 0) {
					r = r + ", ";
				}
				r = r + types[numLocals + i];
			}
			
			return r + "]";
		}
	}
}
