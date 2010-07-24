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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wyil.util.Triple;
import wyjvm.io.BinaryOutputStream;
import wyjvm.io.ClassFileReader;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.Constant;
import wyjvm.lang.JvmType;
import wyjvm.lang.Modifier;

public class InnerClasses implements BytecodeAttribute {
	protected List<Triple<JvmType.Clazz,JvmType.Clazz,List<Modifier>>> inners;	
	protected JvmType.Clazz type;
	
	public String name() {
		return "InnerClasses";
	}
	
	/**
	 * Create an InnerClasses attribute (see JLS Section 4.7.5).
	 * 
	 * @param type - the type of the class containing this attribute.
	 * @param inners - the types and modifiers for all classes contained in this class.
	 * @param outers-  the types and modifiers for all classes containing this class.
	 */
	public InnerClasses(JvmType.Clazz type,
			List<Triple<JvmType.Clazz, JvmType.Clazz, List<Modifier>>> inners) {
		this.type = type;
		this.inners = inners;		
	}
	
	public List<Triple<JvmType.Clazz,JvmType.Clazz,List<Modifier>>> inners() {
		return inners;
	}
	
	public JvmType.Clazz type() {
		return type;
	}
	
	/**
	 * When this method is called, the attribute must add all items that it
	 * needs to the constant pool.
	 * 
	 * @param constantPool
	 */
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {
		Constant.addPoolItem(new Constant.Utf8("InnerClasses"), constantPool);
		for(Triple<JvmType.Clazz,JvmType.Clazz,List<Modifier>> i : inners) {			
			if(i.first() != null) {
				Constant.addPoolItem(Constant.buildClass(i.first()),constantPool);
			}
			if(i.second() != null) {
				Constant.addPoolItem(Constant.buildClass(i.second()),constantPool);
			}
			String name = i.second().lastComponent().first();
			Constant.addPoolItem(new Constant.Utf8(name),constantPool);										
		}		
	}
	
	public void print(PrintWriter output, Map<Constant.Info, Integer> constantPool, ClassLoader loader) {
		output.println("  InnerClasses:");
		
		for(Triple<JvmType.Clazz,JvmType.Clazz,List<Modifier>> i : inners) {
			String name = i.second().lastComponent().first();
			int nameIndex = constantPool.get(new Constant.Utf8(name));
			int outerIndex = 0;
			if(i.first() != null) {
				outerIndex = constantPool.get(Constant.buildClass(i.first()));
			}			
			int innerIndex = 0;
			if(i.second() != null) {
				innerIndex = constantPool.get(Constant.buildClass(i.second()));
			}
			output.print("   ");			
			output.print(nameIndex + " (");
			// FIXME: I remove BytecodeFileWriter.  May want to put it back in sometime.
			// BytecodeFileWriter.writeModifiers(i.third(),output);					
			output.println(") = " + innerIndex + " of " + outerIndex);
		}			
	}
	
	/**
     * Write attribute detailing what direct inner classes there are for this
     * class, or what inner class this class is in.
     * 
     * @param clazz
     * @param constantPool
     */
	public void write(BinaryOutputStream output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {
		output.write_u2(constantPool.get(new Constant.Utf8("InnerClasses")));
		
		int ninners = inners.size();
		
		output.write_u4(2 + (8 * ninners));
		output.write_u2(ninners);
		
		for(Triple<JvmType.Clazz,JvmType.Clazz,List<Modifier>> i : inners) {
			if(i.second() == null) {								
				output.write_u2(0);
			} else {
				output.write_u2(constantPool.get(Constant.buildClass(i.second())));
			}
			if(i.first() == null) {
				output.write_u2(0);
			} else {
				output.write_u2(constantPool.get(Constant.buildClass(i.first())));
			}
			String name = i.second().lastComponent().first();
			output.write_u2(constantPool.get(new Constant.Utf8(name)));			
			writeInnerModifiers(i.third(),output);			
		}		
	}
	
	private static void writeInnerModifiers(List<Modifier> modifiers,
			BinaryOutputStream output)
			throws IOException {
		int mods = 0;
		for (Modifier m : modifiers) {
			if (m instanceof Modifier.Public) {
				mods |= ClassFileReader.ACC_PUBLIC;
			} else if (m instanceof Modifier.Private) {
				mods |= ClassFileReader.ACC_PRIVATE;
			} else if (m instanceof Modifier.Protected) {
				mods |= ClassFileReader.ACC_PROTECTED;
			} else if (m instanceof Modifier.Static) {
				mods |= ClassFileReader.ACC_STATIC;
			} else if (m instanceof Modifier.Final) {
				mods |= ClassFileReader.ACC_FINAL;
			} else if (m instanceof Modifier.Interface) {
				mods |= ClassFileReader.ACC_INTERFACE;
			} else if (m instanceof Modifier.Abstract) {
				mods |= ClassFileReader.ACC_ABSTRACT;
			} else if (m instanceof Modifier.Synthetic) {
				mods |= ClassFileReader.ACC_SYNTHETIC;
			} else if (m instanceof Modifier.Enum) {
				mods |= ClassFileReader.ACC_ENUM;
			} 
		}

		output.write_u2(mods);
}
	
}
