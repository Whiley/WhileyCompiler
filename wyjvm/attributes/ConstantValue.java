// This file is part of the Wyjvm bytecode manipulation library.
//
// Wyjvm is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyjvm is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyjvm. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

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
		writer.write_u2(constantPool.get(new Constant.Utf8("ConstantValue")));
		writer.write_u4(2);
		writer.write_u2(constantPool.get(getConstantInfo()));
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
