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

package wyjvm.util;

import java.util.*;

import wyjvm.io.*;
import wyjvm.lang.*;

public class Parser {
	private static final HashSet<String> loadStoreBytecodes = new HashSet<String>() {
		{
			add("istore");
			add("lstore");
			add("astore");
			add("fstore");
			add("dstore");
			add("iload");
			add("lload");
			add("aload");
			add("fload");
			add("dload");
		}
	};
	
	private static final HashSet<String> arrayLoadStoreBytecodes = new HashSet<String>() {
		{
			add("iastore");
			add("lastore");
			add("bastore");
			add("castore");
			add("aastore");
			add("fastore");
			add("dastore");
			add("iaload");
			add("laload");
			add("baload");
			add("caload");
			add("aaload");
			add("faload");
			add("daload");		
		}
	};
	
	private static final HashSet<String> fieldBytecodes = new HashSet<String>() {
		{
			add("getstatic");
			add("getfield");				
			add("putstatic");
			add("putfield");
		}
	};
	
	private static final HashSet<String> invokeBytecodes = new HashSet<String>() {
		{
			add("invokespecial");				
			add("invokevirtual");
			add("invokeinterface");
			add("invokestatic");
		}
	};
	
	private static final HashSet<String> returnBytecodes = new HashSet<String>() {
		{
			add("return");				
			add("areturn");
			add("ireturn");
			add("freturn");
			add("dreturn");
		}
	};
	
	private static final HashSet<String> constBytecodes = new HashSet<String>() {
		{
			add("iconst_m1");				
			add("iconst_0");
			add("iconst_1");
			add("iconst_2");
			add("iconst_3");
			add("iconst_4");
			add("iconst_5");
			add("lconst_0");
			add("lconst_1");
			
			add("bipush");
			add("sipush");
			add("ldc");
			add("ldc_w");
		}
	};
	
	public static Bytecode parseBytecode(String text) throws ParseError {			
		// first, tokenise the string
		
		ArrayList<String> tokens = new ArrayList<String>();
		int index = 0;		
		while(index < text.length()) {
			index = parseWhiteSpace(text,index);
			int start = index;
			while (index < text.length()
					&& !Character.isWhitespace(text.charAt(index))) {
				if(text.charAt(index++) == '"') {
					// parse String					
					while(index < text.length() && text.charAt(index++) != '"');
				}
			}
			tokens.add(text.substring(start,index));
		}
		
		if(tokens.size() == 0) {
			return null;
		}
		
		// second, dispatch on token kind
		String kind = tokens.get(0);				
		
		if(loadStoreBytecodes.contains(kind)) {
			return parseLoadStoreBytecode(tokens);
		} else if(arrayLoadStoreBytecodes.contains(kind)) {
			return parseArrayLoadStoreBytecode(tokens);
		} else if(invokeBytecodes.contains(kind)) {
			return parseInvokeBytecode(tokens);
		} else if(returnBytecodes.contains(kind)) {
			return parseReturnBytecode(tokens);
		} else if(fieldBytecodes.contains(kind)) {
			return parseFieldBytecode(tokens);
		} else if(constBytecodes.contains(kind)) {
			return parseConstBytecode(tokens);
		} else if(kind.equals("goto") && tokens.size() == 2) {
			return new Bytecode.Goto(tokens.get(1));
		} else if(tokens.size() == 1 && kind.charAt(kind.length()-1) == ':') {		
			// label
			return new Bytecode.Label(kind.substring(0,kind.length()-1));
		} else {
			error("syntax error");
			return null;
		}
	}
	
	public static Bytecode parseLoadStoreBytecode(ArrayList<String> tokens) throws ParseError {			
		String kind = tokens.get(0);
		JvmType type = parseType(kind.charAt(0)); 
		kind = kind.substring(1,kind.length());
		int slot;
		
		if(tokens.size() == 1) {
			int idx = kind.indexOf('_');
			if(idx == -1) {
				error("slot argument required");
			}
			slot = Integer.parseInt(kind.substring(idx + 1, kind.length()));
		} else {			
			slot = Integer.parseInt(tokens.get(1));
		}
		if(kind.startsWith("store")) {
			return new Bytecode.Store(slot,type);
		} else {
			return new Bytecode.Load(slot,type);
		}
	}

	public static Bytecode parseArrayLoadStoreBytecode(ArrayList<String> tokens) throws ParseError {			
		String kind = tokens.get(0);
		JvmType type = parseType(kind.charAt(0)); 
		kind = kind.substring(2,kind.length());
				
		if(kind.startsWith("store")) {
			return new Bytecode.ArrayStore(new JvmType.Array(type));
		} else {
			return new Bytecode.ArrayLoad(new JvmType.Array(type));
		}
	}
	
	public static Bytecode parseInvokeBytecode(ArrayList<String> tokens)
			throws ParseError {		
		if (tokens.size() != 2) {
			error("wrong number of arguments");
		}
		int mode;
		String kind = tokens.get(0);
		if (kind.equals("invokevirtual")) {
			mode = Bytecode.VIRTUAL;
		} else if (kind.equals("invokeinterface")) {
			mode = Bytecode.INTERFACE;
		} else if (kind.equals("invokespecial")) {
			mode = Bytecode.SPECIAL;
		} else {
			mode = Bytecode.STATIC;
		}

		String[] split = tokens.get(1).split("\\.");
		JvmType.Clazz owner = ClassFileReader.parseClassDescriptor("L" + split[0] + ";");
		split = split[1].split(":");
		String name = split[0];
		JvmType.Function type = ClassFileReader.parseMethodDescriptor(split[1]);
		return new Bytecode.Invoke(owner, name, type, mode);
	}
	
	public static Bytecode parseReturnBytecode(ArrayList<String> tokens)
	throws ParseError {		
		if (tokens.size() != 1) {
			error("wrong number of arguments");
		}
		String kind = tokens.get(0);
		if(kind.equals("return")) {
			return new Bytecode.Return(null);
		} else {
			JvmType type = parseType(kind.charAt(0));
			return new Bytecode.Return(type);
		}
	}
	
	public static Bytecode parseFieldBytecode(ArrayList<String> tokens)
			throws ParseError {
		if (tokens.size() != 2) {
			error("wrong number of arguments");
		}
		String[] split = tokens.get(1).split("\\.");
		JvmType.Clazz owner = ClassFileReader.parseClassDescriptor("L" + split[0] + ";");
		split = split[1].split(":");
		String name = split[0];
		JvmType type = ClassFileReader.parseDescriptor(split[1]);
		String kind = tokens.get(0);
		if(kind.equals("getstatic")) {
			return new Bytecode.GetField(owner,name,type,Bytecode.STATIC);
		} else if(kind.equals("getfield")) {
			return new Bytecode.GetField(owner,name,type,Bytecode.VIRTUAL);
		} else if(kind.equals("putfield")) {
			return new Bytecode.PutField(owner,name,type,Bytecode.VIRTUAL);
		} else {
			return new Bytecode.PutField(owner,name,type,Bytecode.STATIC);
		}
	}
	
	public static Bytecode parseConstBytecode(ArrayList<String> tokens)
			throws ParseError {
		JvmType type;
		Object constant;
		String kind = tokens.get(0);
		if(tokens.size() == 1) {
			type = parseType(kind.charAt(0));
			int idx = kind.indexOf('_');
			if(idx == -1) {
				error("constant argument required");
			}
			
			if(type instanceof JvmType.Int) {
				constant = Integer.parseInt(kind.substring(idx + 1, kind.length()));
			} else if(type instanceof JvmType.Long) {
				constant = Long.parseLong(kind.substring(idx + 1, kind.length()));
			} else {
				error("syntax error");
				return null;
			}
		} else if(kind.startsWith("ldc")) {
			String s = tokens.get(1);
			if(s.charAt(0) == '\"') {				
				if (s.length() < 2 || s.charAt(s.length() - 1) != '\"') {
					error("syntax error");
				}
				constant = s.substring(1,s.length()-1);
			} else if(s.contains(".")) {
				// need to think here about how to distinguish float/double values
				constant = Float.parseFloat(s);
			} else {
				constant = Long.parseLong(s);
			}
		} else {
			constant = Integer.parseInt(tokens.get(1));
		}
		
		return new Bytecode.LoadConst(constant);
	}
	
	public static JvmType parseType(char c) throws ParseError {
		if(c == 'l') {
			return new JvmType.Long();
		} else if(c == 'i') {
			return new JvmType.Int();
		} else if(c == 'f') {
			return new JvmType.Float();
		} else if(c == 'd') {
			return new JvmType.Double();
		} else if(c == 'b') {
			return new JvmType.Bool();
		} else if(c == 'c') {
			return new JvmType.Char();
		} else if(c == 's') {
			return new JvmType.Short();
		} else if(c == 'a') {
			return JvmTypes.JAVA_LANG_OBJECT;
		} else {
			error("invalid bytecode type: " + c);
			return null;
		}
	}
	
	private static int parseWhiteSpace(String text, int index) {
		while (index < text.length()
				&& Character.isWhitespace(text.charAt(index))) {
			index++;
		}
		return index;
	}
	
	public static class ParseError extends Exception {
		public ParseError(String msg) {
			super(msg);
		}
	}
	
	public static void error(String msg) throws ParseError {
		throw new ParseError(msg);
	}
}
