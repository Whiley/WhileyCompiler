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
import java.util.*;

import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.Constant;
import wyjvm.lang.JvmType;

public class Exceptions implements BytecodeAttribute {
	protected List<JvmType.Clazz> exceptions;
	
	public Exceptions(List<JvmType.Clazz> exceptions) {
		this.exceptions = exceptions;
	}
	
	public String name() {
		return "Exceptions";
	}
	
	public List<JvmType.Clazz> exceptions() {
		return exceptions;
	}
	
	/**
	 * This method requires the attribute to write itself to the binary stream.
	 * 
	 * @param writer
	 * @returns the number of bytes written.
	 * @throws IOException
	 */
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader) throws IOException {		
		writer.write_u16(constantPool.get(new Constant.Utf8("Exceptions")));
		writer.write_u32(2 + (2 * exceptions.size()));
		writer.write_u16(exceptions.size());
		for (JvmType.Clazz e : exceptions) {
			writer.write_u16(constantPool.get(Constant.buildClass(e)));
		}
	}
	
	/**
	 * When this method is called, the attribute must add all items that it
	 * needs to the constant pool.
	 * 
	 * @param constantPool
	 */
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {
		Constant.addPoolItem(new Constant.Utf8("Exceptions"), constantPool);
		for (JvmType.Clazz e : exceptions) {
			Constant.addPoolItem(Constant.buildClass(e), constantPool);
		}
	}
	
	public void print(PrintWriter output, Map<Constant.Info, Integer> constantPool, ClassLoader loader) {
		output.println("  Exceptions:");
		boolean firstTime = true; 
		output.print("   ");
		for(JvmType.Clazz e : exceptions) {
			if(!firstTime) {
				output.print(", ");
			}
			firstTime=false;
			output.print(e);
		}
		output.println();
	}	
}
