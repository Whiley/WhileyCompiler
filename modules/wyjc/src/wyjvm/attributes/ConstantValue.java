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

public class ConstantValue implements BytecodeAttribute {	
	private Object constant;
	
	/**
	 * A ConstantValue attribute. Here, the constant must be either an
	 * instanceof of Boolean, Byte, Character, Short, Integer, Float, Double or
	 * String.
	 * 
	 * @param constant
	 */
	public ConstantValue(Object constant) {		
		this.constant = constant;
		if (getConstantInfo() == null) {
			throw new IllegalArgumentException(
					"constant must be instance of Boolean, Byte, Character, Short, Integer, Float, Double or String (got "
							+ constant.getClass().getName() + ")");
		}
	}
	
	public String name() {
		return "ConstantValue";
	}
	
	public Object constant() {
		return constant;
	}
	
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {
		Constant.addPoolItem(new Constant.Utf8("ConstantValue"), constantPool);
		Constant.addPoolItem(getConstantInfo(), constantPool);
	}
	
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader) throws IOException {
		writer.write_u16(constantPool.get(new Constant.Utf8("ConstantValue")));
		writer.write_u32(2);
		writer.write_u16(constantPool.get(getConstantInfo()));
	}
	
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {
		String type;
		if (constant instanceof Byte || constant instanceof Character
				|| constant instanceof Boolean || constant instanceof Short
				|| constant instanceof Integer) {
			type = "int";
		} else if (constant instanceof Long) {
			type = "long";
		} else if (constant instanceof Float) {
			type = "float";
		} else {
			type = "double";
		}
		output.println("  Constant value: " + type + " " + constant.toString());
	}
	
	private Constant.Info getConstantInfo() {
		if(constant instanceof Boolean) {
			boolean b = (Boolean) constant;
			if(b) {
				return new Constant.Integer(1);
			} else {
				return new Constant.Integer(0);
			}
		} else if(constant instanceof Byte) {
			return new Constant.Integer((Byte)constant);
		} else if(constant instanceof Character) {
			return new Constant.Integer((Character)constant);
		} else if(constant instanceof Short) {
			return new Constant.Integer((Short)constant);
		} else if(constant instanceof Integer) {
			return new Constant.Integer((Integer)constant);
		} else if(constant instanceof Long) {
			return new Constant.Long((Long)constant);
		} else if(constant instanceof Float) {
			return new Constant.Float((Float)constant);
		} else if(constant instanceof Double) {
			return new Constant.Double((Double)constant);
		} else if(constant instanceof String) {
			return new Constant.String(new Constant.Utf8((String) constant));
		} else {		
			return null;
		}
	}
}
