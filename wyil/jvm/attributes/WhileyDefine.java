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

package wyil.jvm.attributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import wyil.lang.*;
import wyjvm.io.BinaryInputStream;
import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.BytecodeAttributeReader;
import wyjvm.lang.Constant;

/**
 * The WhileyVersion attribute is simply a marker used to indicate that a class
 * file was generated from a whiley source file. This is useful in
 * multi-platform scenarios where we might have multiple source languages.
 * 
 * @author djp
 * 
 */
public class WhileyDefine implements BytecodeAttribute {
	private String defName;
	private Value value;
	private Block block;
	private Type type;
	
	public WhileyDefine(String name, Value expr) {
		this.defName = name;		
		this.value = expr;
	}
	
	public WhileyDefine(String name, Type type, Block block) {
		this.defName = name;
		this.type = type;
		this.block = block;		
	}
	
	public String name() {
		return "WhileyDefine";
	}
	
	public String defName() {
		return defName;
	}
	
	public Type type() {
		return type;
	}
	
	public Value value() {
		return value;
	}
	
	public Block block() {
		return block;
	}
	
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {		
		
		// First, construct a byte array for the type and/or condition being
        // defined. This must be done first as we need to determine its length
        // so this can be written as part of the attribute header.
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryOutputStream iw = new BinaryOutputStream(out);

		if(type == null) {
			iw.write_u1(0); // CONDITION ONLY			
			write(value,iw, constantPool);			
		} else if(value == null) {
			iw.write_u1(1); // TYPE ONLY
			WhileyType.write(type, iw, constantPool);
		} else {									
			// unreachable			
		}						
		
		writer.write_u2(constantPool.get(new Constant.Utf8(name())));
		writer.write_u4(out.size() + 2);		
		writer.write_u2(constantPool.get(new Constant.Utf8(defName)));
		writer.write(out.toByteArray());				
	}	
		
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {		
		Constant.addPoolItem(new Constant.Utf8(name()), constantPool);	
		Constant.addPoolItem(new Constant.Utf8(defName), constantPool);
		
		if(value != null) {
			WhileyBlock.addPoolItems(value, constantPool);
		}
		if(type != null) {
			WhileyType.addPoolItems(type, constantPool);
		}				
	}
	
	protected void write(Value v, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {		
		WhileyBlock.writeValue(v,writer,constantPool);				
	}
	
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {

		if (type == null) {
			output.println("  WhileyDefine: " + defName + " as " + value);
		} else if (block == null) {
			output.println("  WhileyDefine: " + defName + " as " + type);
		} else {
			output.println("  WhileyDefine: " + defName + " as " + type);
			for(Code c : block) {
				wyil.io.WyilFileWriter.write(1,c,output);
			}
		}
	}
	
	public static class Reader implements BytecodeAttributeReader {		
		public String name() {
			return "WhileyDefine";
		}
		
		public WhileyDefine read(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			input.read_u2(); // attribute name index code
			input.read_u4(); // attribute length 
			int nameIdx = input.read_u2();
			
			String name = ((Constant.Utf8) constantPool.get(nameIdx)).str;
			int sw = input.read_u1();			
			
			if(sw == 0) {				
				// Condition only
				Value value = WhileyBlock.Reader.readValue(input,constantPool);				
				return new WhileyDefine(name,value);			
			} else if(sw == 1) {
				// type only
				Type type = WhileyType.Reader.readType(input,constantPool);
				return new WhileyDefine(name,type,null);
			} else {				
				// both				
				Type type = WhileyType.Reader.readType(input,constantPool);											
				Block blk = WhileyBlock.Reader.readBlock(input,constantPool);
				return new WhileyDefine(name,type,blk);
			}
		}						
	}	
}
