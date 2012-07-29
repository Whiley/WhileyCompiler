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

package wyil.io;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.Path;
import wybs.util.Trie;
import wyil.lang.*;
import wyil.util.Pair;
import wyil.util.BigRational;
import wyil.io.BinaryInputStream;

/**
 * Read a binary WYIL file from a byte stream and convert into the corresponding
 * WyilFile object.
 * 
 * @author David J. Pearce
 * 
 */
public final class WyilFileReader {
	private static final char[] magic = {'W','Y','I','L','F','I','L','E'};
	
	private final BinaryInputStream input;
	private String[] stringPool;
	private Path.ID[] pathPool;
	private NameID[] namePool;
	private Constant[] constantPool;
	private Type[] typePool;
	
	public WyilFileReader(String filename) throws IOException {
		this.input = new BinaryInputStream(new FileInputStream(filename));
	}
	
	public WyilFileReader(InputStream input) throws IOException {
		this.input = new BinaryInputStream(input);
	}
	
	public void close() throws IOException {
		input.close();
	}
	
	public WyilFile read() throws IOException {
				
		for(int i=0;i!=8;++i) {
			char c = (char) input.read_u8();
			if(magic[i] != c) {
				throw new IllegalArgumentException("invalid magic number");
			}
		}
		
		// head blocker
		int kind = input.read_uv();
		int size = input.read_uv();
		input.pad_u8();
		
		if(kind != WyilFileWriter.BLOCK_Header) {
			throw new IllegalArgumentException("header block must come first");
		}
		
		int majorVersion = input.read_uv();
		int minorVersion = input.read_uv();
		
		int stringPoolSize = input.read_uv();
		int pathPoolSize = input.read_uv();
		int namePoolSize = input.read_uv();
		int constantPoolSize = input.read_uv();
		int typePoolSize = input.read_uv();
				
		int numBlocks = input.read_uv();
		
		readStringPool(stringPoolSize);
		readPathPool(pathPoolSize);
		readNamePool(namePoolSize);
		readConstantPool(constantPoolSize);
		readTypePool(typePoolSize);		
		
		input.pad_u8();
						
		return readModule();				
	}
	
	private void readStringPool(int size) throws IOException {		
		final String[] myStringPool = new String[size];
		
		for(int i=0;i!=size;++i) {
			int length = input.read_uv();
			try {
				byte[] data = new byte[length];
				input.read(data);
				String str = new String(data,0,length,"UTF-8");
				myStringPool[i] = str;
			} catch(UnsupportedEncodingException e) {
				throw new RuntimeException("UTF-8 Charset not supported?");
			}
		}
		stringPool = myStringPool;
	}
	
	private void readPathPool(int size) throws IOException {
		final Path.ID[] myPathPool = new Path.ID[size];
		myPathPool[0] = Trie.ROOT;
		
		for (int i = 1; i != size; ++i) {
			int parent = input.read_uv();
			int stringIndex = input.read_uv();
			Path.ID id;
			id = myPathPool[parent];
			id = id.append(stringPool[stringIndex]);
			myPathPool[i] = id;
		}
		pathPool = myPathPool;
	}

	private void readNamePool(int size) throws IOException {
		final NameID[] myNamePool = new NameID[size];
		
		for (int i = 0; i != size; ++i) {
			// int kind = input.read_uv();
			int pathIndex = input.read_uv();
			int nameIndex = input.read_uv();
			Path.ID id = pathPool[pathIndex];
			String name = stringPool[nameIndex];
			myNamePool[i] = new NameID(id, name);
		}
		
		namePool = myNamePool;
	}

	private void readConstantPool(int size) throws IOException {		
		final Constant[] myConstantPool = new Constant[size];
				
		for(int i=0;i!=size;++i) {
			int code = input.read_uv();
			Constant constant;
			
			switch (code) {
				case WyilFileWriter.CONSTANT_Null :
					constant = Constant.V_NULL;
					break;
				case WyilFileWriter.CONSTANT_False :
					constant = Constant.V_BOOL(false);
					break;
				case WyilFileWriter.CONSTANT_True :
					constant = Constant.V_BOOL(true);
					break;
				case WyilFileWriter.CONSTANT_Byte : {
					byte val = (byte) input.read_u8();
					constant = Constant.V_BYTE(val);
					break;
				}
				case WyilFileWriter.CONSTANT_Char : {
					char val = (char) input.read_uv();
					constant = Constant.V_CHAR(val);
					break;
				}
				case WyilFileWriter.CONSTANT_Int : {
					int len = input.read_uv();
					byte[] bytes = new byte[len];
					input.read(bytes);
					BigInteger bi = new BigInteger(bytes);
					constant = Constant.V_INTEGER(bi);
					break;
				}
				case WyilFileWriter.CONSTANT_Real : {
					int len = input.read_uv();
					byte[] bytes = new byte[len];
					input.read(bytes);
					BigInteger num = new BigInteger(bytes);
					len = input.read_uv();
					bytes = new byte[len];
					input.read(bytes);
					BigInteger den = new BigInteger(bytes);
					BigRational br = new BigRational(num, den);
					constant = Constant.V_RATIONAL(br);
					break;
				}
				case WyilFileWriter.CONSTANT_String : {
					int index = input.read_uv();
					constant = Constant.V_STRING(stringPool[index]);
					break;
				}
				case WyilFileWriter.CONSTANT_List : {
					int len = input.read_uv();
					ArrayList<Constant> values = new ArrayList<Constant>();
					for (int j = 0; j != len; ++j) {
						int index = input.read_uv();							
						values.add(myConstantPool[index]);
					}
					constant = Constant.V_LIST(values);
					break;
				}
				case WyilFileWriter.CONSTANT_Set : {
					int len = input.read_uv();
					ArrayList<Constant> values = new ArrayList<Constant>();
					for (int j = 0; j != len; ++j) {
						int index = input.read_uv();
						values.add(myConstantPool[index]);
					}
					constant = Constant.V_SET(values);
					break;
				}
				case WyilFileWriter.CONSTANT_Tuple : {
					int len = input.read_uv();
					ArrayList<Constant> values = new ArrayList<Constant>();
					for (int j = 0; j != len; ++j) {
						int index = input.read_uv();
						values.add(myConstantPool[index]);
					}
					constant = Constant.V_TUPLE(values);
					break;
				}
				case WyilFileWriter.CONSTANT_Map : {
					int len = input.read_uv();
					HashSet<Pair<Constant, Constant>> values = new HashSet<Pair<Constant, Constant>>();
					for (int j = 0; j != len; ++j) {
						int keyIndex = input.read_uv();
						int valIndex = input.read_uv();
						Constant key = myConstantPool[keyIndex];
						Constant val = myConstantPool[valIndex];
						values.add(new Pair<Constant, Constant>(key, val));
					}
					constant = Constant.V_MAP(values);
					break;
				}
				case WyilFileWriter.CONSTANT_Record : {
					int len = input.read_uv();
					HashMap<String, Constant> tvs = new HashMap<String, Constant>();
					for (int j = 0; j != len; ++j) {
						int fieldIndex = input.read_uv();
						int constantIndex = input.read_uv();
						String str = stringPool[fieldIndex];
						tvs.put(str, myConstantPool[constantIndex]);
					}
					constant = Constant.V_RECORD(tvs);
					break;
				}
				case WyilFileWriter.CONSTANT_Function :
				case WyilFileWriter.CONSTANT_Method : {
					int typeIndex = input.read_uv();
					int nameIndex = input.read_uv();
					Type.FunctionOrMethod t = (Type.FunctionOrMethod) typePool[typeIndex];
					NameID name = namePool[nameIndex];
					constant = Constant.V_FUN(name, t);
					break;
				}
				default:
					throw new RuntimeException(
							"Unknown Value encountered in WhileyDefine: " + code);
			}			
			myConstantPool[i] = constant;
		}
		
		constantPool = myConstantPool;
	}

	private void readTypePool(int size) throws IOException {		
		final Type[] myTypePool = new Type[size];
		
		Type.BinaryReader bin = new Type.BinaryReader(input);
		for(int i=0;i!=size;++i) {
			myTypePool[i] = bin.readType();					
		}
		
		typePool = myTypePool;
	}
	
	private WyilFile readModule() throws IOException {		
		int kind = input.read_uv(); // block identifier
		int size = input.read_uv();	
		input.pad_u8();
		
		int pathIdx = input.read_uv();
		int modifiers = input.read_uv(); // unused
		int numBlocks = input.read_uv();
		
		input.pad_u8();
		
		List<WyilFile.Declaration> declarations = new ArrayList<WyilFile.Declaration>();
		for(int i=0;i!=numBlocks;++i) {			
			declarations.add(readModuleBlock());
		}
		
		return new WyilFile(pathPool[pathIdx],"unknown.whiley",declarations);
	}
	
	private WyilFile.Declaration readModuleBlock() throws IOException {
		int kind = input.read_uv();
		int size = input.read_uv();
		input.pad_u8();
		
		switch(kind) {
			case WyilFileWriter.BLOCK_Constant:
				return readConstantBlock();
			case WyilFileWriter.BLOCK_Type:
				return readTypeBlock();
			case WyilFileWriter.BLOCK_Function:
				return readFunctionBlock();
			case WyilFileWriter.BLOCK_Method:
				return readMethodBlock();				
			default:
				throw new RuntimeException("unknown module block encountered (" + kind + ")");
		}
	}
	
	private WyilFile.ConstantDeclaration readConstantBlock() throws IOException {		
		int nameIdx = input.read_uv();
		//System.out.println("=== CONSTANT " + stringPool.get(nameIdx));
		int modifiers = input.read_uv();
		int constantIdx = input.read_uv();
		int nBlocks = input.read_uv(); // unused
		
		input.pad_u8();				
		return new WyilFile.ConstantDeclaration(generateModifiers(modifiers),
				stringPool[nameIdx], constantPool[constantIdx]);
	}
	
	private WyilFile.TypeDeclaration readTypeBlock() throws IOException {		
		int nameIdx = input.read_uv();
		//System.out.println("=== TYPE " + stringPool.get(nameIdx));
		int modifiers = input.read_uv();
		int typeIdx = input.read_uv();		
		int nBlocks = input.read_uv();
		
		input.pad_u8();
		
		Block constraint = null;
		if(nBlocks != 0) {
			int kind = input.read_uv(); // unsued
			int size = input.read_uv();
			input.pad_u8();			
			constraint = readCodeBlock(1);
		}		
		
		return new WyilFile.TypeDeclaration(generateModifiers(modifiers),
				stringPool[nameIdx], typePool[typeIdx], constraint);
	}
	
	private WyilFile.MethodDeclaration readFunctionBlock() throws IOException {
		int nameIdx = input.read_uv();
		//System.out.println("=== FUNCTION " + stringPool.get(nameIdx));
		int modifiers = input.read_uv();
		int typeIdx = input.read_uv();		
		int numCases = input.read_uv();		
		
		input.pad_u8();
		
		Type.Function type = (Type.Function) typePool[typeIdx];		
		ArrayList<WyilFile.Case> cases = new ArrayList<WyilFile.Case>();
		for(int i=0;i!=numCases;++i) {
			int kind = input.read_uv(); // unsued
			int size = input.read_uv();
			input.pad_u8();
			
			switch(kind) {
				case WyilFileWriter.BLOCK_Case:
					cases.add(readFunctionOrMethodCase(type));
					break;
				default:
					throw new RuntimeException("Unknown function block encountered");
			}
		}
		return new WyilFile.MethodDeclaration(generateModifiers(modifiers),
				stringPool[nameIdx], type,
				cases);
	}
	
	private WyilFile.MethodDeclaration readMethodBlock() throws IOException {
		int nameIdx = input.read_uv();
		// System.out.println("=== METHOD " + stringPool.get(nameIdx));
		int modifiers = input.read_uv();
		int typeIdx = input.read_uv();		
		int numCases = input.read_uv();
		
		input.pad_u8();
		
		Type.Method type = (Type.Method) typePool[typeIdx];
		ArrayList<WyilFile.Case> cases = new ArrayList<WyilFile.Case>();
		for(int i=0;i!=numCases;++i) {
			int kind = input.read_uv(); // unsued
			int size = input.read_uv();
			input.pad_u8();
			
			switch(kind) {
				case WyilFileWriter.BLOCK_Case:
					cases.add(readFunctionOrMethodCase(type));
					break;
				default:
					throw new RuntimeException("Unknown method block encountered");
			}
		}
		return new WyilFile.MethodDeclaration(generateModifiers(modifiers),
				stringPool[nameIdx], type,
				cases);
	}
	
	private Collection<Modifier> generateModifiers(int modifiers) {
		ArrayList<Modifier> mods = new ArrayList<Modifier>();

		// first, protection modifiers
		switch (modifiers & WyilFileWriter.MODIFIER_PROTECTION_MASK) {
			case WyilFileWriter.MODIFIER_Public :
				mods.add(Modifier.PUBLIC);
				break;
			case WyilFileWriter.MODIFIER_Protected :
				mods.add(Modifier.PROTECTED);
				break;
			case WyilFileWriter.MODIFIER_Private :
				mods.add(Modifier.PRIVATE);
				break;
			default :
				throw new RuntimeException("Unknown modifier");
		}
		
		// second, mangle modifiers
		switch (modifiers & WyilFileWriter.MODIFIER_MANGLE_MASK) {
			case WyilFileWriter.MODIFIER_Native :
				mods.add(Modifier.NATIVE);
				break;
			case WyilFileWriter.MODIFIER_Export :
				mods.add(Modifier.EXPORT);
				break;			
		}

		return mods;
	}
	
	private WyilFile.Case readFunctionOrMethodCase(Type.FunctionOrMethod type) throws IOException {
		Block precondition = null;
		Block postcondition = null;
		Block body = null;		
		int numInputs = type.params().size();
		int nBlocks = input.read_uv();
		
		input.pad_u8();
		
		for (int i = 0; i != nBlocks; ++i) {			
			int kind = input.read_uv(); 
			int size = input.read_uv();
			input.pad_u8();
			
			switch (kind) {
			case WyilFileWriter.BLOCK_Precondition:
				precondition = readCodeBlock(numInputs);
				break;
			case WyilFileWriter.BLOCK_Postcondition:
				postcondition = readCodeBlock(numInputs + 1);
				break;
			case WyilFileWriter.BLOCK_Body:
				body = readCodeBlock(numInputs);
				break;
			default:
				throw new RuntimeException("Unknown case block encountered");
			}
		}

		return new WyilFile.Case(body, precondition, postcondition, Collections.EMPTY_LIST);
	}
	
	private Block readCodeBlock(int numInputs) throws IOException {
		Block block = new Block(numInputs);
		int nCodes = input.read_uv();
		HashMap<Integer,String> labels = new HashMap<Integer,String>();
		
		for(int i=0;i!=nCodes;++i) {
			Code code = readCode(i,labels);
			//System.out.println("READ: " + code);
			block.append(code);		
		}
		
		for(int i=0,j=0;i!=nCodes;++i,++j) {
			String label = labels.get(i);
			if(label != null) {
				block.insert(j++, Code.Label(label));
			}
		}

		input.pad_u8(); // necessary
		
		return block;
	}	
	
	private Code readCode(int offset, HashMap<Integer,String> labels) throws IOException {
		int opcode = input.read_u8();
		boolean wideBase = false;
		boolean wideRest = false;
		
		// deal with wide bytecodes first
		switch(opcode) {
		case Code.OPCODE_wide:
			opcode = input.read_u8();
			wideBase = true;
			break;
		case Code.OPCODE_widerest:
			opcode = input.read_u8();
			wideRest = true;
			break;
		case Code.OPCODE_widewide:
			opcode = input.read_u8();
			wideBase = true;
			wideRest = true;
			break;
		}
		
		int fmt = (opcode & Code.FMT_MASK);
		
		switch (fmt) {
		case Code.FMT_EMPTY:
			return readEmpty(opcode,wideBase,wideRest,offset,labels);
		case Code.FMT_UNARYOP:
			return readUnaryOp(opcode,wideBase,wideRest,offset,labels);
		case Code.FMT_UNARYASSIGN:
			return readUnaryAssign(opcode,wideBase,wideRest);
		case Code.FMT_BINARYOP:
			return readBinaryOp(opcode, wideBase,wideRest,offset,labels);
		case Code.FMT_BINARYASSIGN:
			return readBinaryAssign(opcode,wideBase,wideRest);
		case Code.FMT_NARYOP:
			return readNaryOp(opcode,wideBase,wideRest,offset,labels);
		case Code.FMT_NARYASSIGN:
			return readNaryAssign(opcode,wideBase,wideRest);
		case Code.FMT_OTHER:
			return readOther(opcode,wideBase,wideRest,offset,labels);
		default:
			throw new RuntimeException("unknown opcode encountered (" + opcode
					+ ")");
		}
	}
	
	private Code readEmpty(int opcode, boolean wideBase, boolean wideRest,
			int offset, HashMap<Integer, String> labels) throws IOException {				
		switch(opcode) {
		case Code.OPCODE_const: {
			int target = readBase(wideBase);
			int idx = readRest(wideRest);
			Constant c = constantPool[idx];
			return Code.Const(target,c);
		}
		case Code.OPCODE_goto: {
			int target = readTarget(wideRest,offset); 
			String lab = findLabel(target,labels);
			return Code.Goto(lab);
		}			
		case Code.OPCODE_nop:
			return Code.Nop;
		case Code.OPCODE_returnv:
			return Code.Return();		
		}
		throw new RuntimeException("unknown opcode encountered (" + opcode
				+ ")");
	}
	
	private Code readUnaryOp(int opcode, boolean wideBase, boolean wideRest, int offset, HashMap<Integer,String> labels) throws IOException {
		int operand = readBase(wideBase);
		int typeIdx = readRest(wideRest);
		Type type = typePool[typeIdx];
		switch(opcode) {
		case Code.OPCODE_debug:
			return Code.Debug(operand);		
		case Code.OPCODE_ifis: {
			int resultIdx = readRest(wideRest);
			Type result = typePool[resultIdx];
			int target = readTarget(wideRest,offset);
			String label = labels.get(target);
			return Code.IfIs(type, operand, result, label);
		}
		case Code.OPCODE_throw:
			return Code.Throw(type, operand);
		case Code.OPCODE_return:
			return Code.Return(type, operand);
		case Code.OPCODE_switch: {
			ArrayList<Pair<Constant,String>> cases = new ArrayList<Pair<Constant,String>>();
			int target = readTarget(wideRest,offset); 
			String defaultLabel = findLabel(target,labels);
			int nCases = readRest(wideRest);
			for(int i=0;i!=nCases;++i) {
				int constIdx = readTarget(wideRest,offset);
				Constant constant = constantPool[constIdx];
				target = readTarget(wideRest,offset); 
				String label = findLabel(target,labels);
				cases.add(new Pair<Constant,String>(constant,label));
			}
			return Code.Switch(type,operand,defaultLabel,cases);
		}
		}	
		throw new RuntimeException("unknown opcode encountered (" + opcode
				+ ")");
	}
	
	private Code readUnaryAssign(int opcode, boolean wideBase, boolean wideRest) throws IOException {
		int target = readBase(wideBase);
		
		int operand = readBase(wideBase);
		int typeIdx = readRest(wideRest);
		Type type = typePool[typeIdx];
		switch(opcode) {
		case Code.OPCODE_convert: {
			int i = readRest(wideRest);
			Type t = typePool[i];
			return Code.Convert(type,target,operand,t);
		}		
		case Code.OPCODE_assign:
			return Code.Assign(type, target, operand);
		case Code.OPCODE_dereference: {
			if (!(type instanceof Type.Reference)) {
				throw new RuntimeException("expected reference type");
			}
			return Code.Dereference((Type.Reference) type, target, operand);
		}
		case Code.OPCODE_fieldload: {
			if (!(type instanceof Type.EffectiveRecord)) {
				throw new RuntimeException("expected record type");
			}
			int i = readRest(wideRest);
			String field = stringPool[i];
			return Code.FieldLoad((Type.EffectiveRecord) type, target, operand,
					field);
		}
		case Code.OPCODE_invert:
			return Code.Invert(type, target, operand);
		case Code.OPCODE_newobject: {
			if (!(type instanceof Type.Reference)) {
				throw new RuntimeException("expected reference type");
			}
			return Code.NewObject((Type.Reference) type, target, operand);
		}
		case Code.OPCODE_lengthof: {
			if (!(type instanceof Type.EffectiveCollection)) {
				throw new RuntimeException("expected collection type");
			}
			return Code.LengthOf((Type.EffectiveCollection) type, target, operand);
		}
		case Code.OPCODE_move:
			return Code.Move(type, target, operand);
		case Code.OPCODE_neg:
			return Code.UnArithOp(type, target, operand, Code.UnArithKind.NEG);
		case Code.OPCODE_numerator:
			return Code.UnArithOp(type, target, operand, Code.UnArithKind.NUMERATOR);
		case Code.OPCODE_denominator:
			return Code.UnArithOp(type, target, operand, Code.UnArithKind.DENOMINATOR);
		case Code.OPCODE_not: {
			if (!(type instanceof Type.Bool)) {
				throw new RuntimeException("expected bool type");
			}
			return Code.Not(target, operand);
		}
		case Code.OPCODE_tupleload:{
			if (!(type instanceof Type.EffectiveTuple)) {
				throw new RuntimeException("expected tuple type");
			}
			int index = readRest(wideRest);
			return Code.TupleLoad((Type.Tuple) type, target, operand, index);
		}
		
		}
		throw new RuntimeException("unknown opcode encountered (" + opcode
				+ ")");
	}
	
	private Code readBinaryOp(int opcode, boolean wideBase, boolean wideRest, int offset, HashMap<Integer, String> labels)
			throws IOException {
		int leftOperand = readBase(wideBase);
		int rightOperand = readBase(wideBase);
		int typeIdx = readRest(wideRest);
		Type type = typePool[typeIdx];
		switch(opcode) {
		case Code.OPCODE_asserteq:
		case Code.OPCODE_assertne:
		case Code.OPCODE_assertlt:
		case Code.OPCODE_assertle:
		case Code.OPCODE_assertgt:
		case Code.OPCODE_assertge:
		case Code.OPCODE_assertel:
		case Code.OPCODE_assertss:
		case Code.OPCODE_assertse: {
			int msgIdx = readRest(wideRest);
			String msg = stringPool[msgIdx];
			Code.Comparator cop = Code.Comparator.values()[opcode - Code.OPCODE_asserteq];
			return Code.Assert(type, leftOperand, rightOperand, cop, msg);
		}
		case Code.OPCODE_assumeeq:
		case Code.OPCODE_assumene:
		case Code.OPCODE_assumelt:
		case Code.OPCODE_assumele:
		case Code.OPCODE_assumegt:
		case Code.OPCODE_assumege:
		case Code.OPCODE_assumeel:
		case Code.OPCODE_assumess:
		case Code.OPCODE_assumese: {
			int msgIdx = readRest(wideRest);
			String msg = stringPool[msgIdx];
			Code.Comparator cop = Code.Comparator.values()[opcode - Code.OPCODE_assumeeq];
			return Code.Assume(type, leftOperand, rightOperand, cop, msg);
		}
		case Code.OPCODE_ifeq:
		case Code.OPCODE_ifne:
		case Code.OPCODE_iflt:
		case Code.OPCODE_ifle:
		case Code.OPCODE_ifgt:
		case Code.OPCODE_ifge:
		case Code.OPCODE_ifel:
		case Code.OPCODE_ifss:
		case Code.OPCODE_ifse: {
			int target = readTarget(wideRest,offset); 
			String lab = findLabel(target,labels);
			Code.Comparator cop = Code.Comparator.values()[opcode - Code.OPCODE_ifeq];
			return Code.If(type, leftOperand, rightOperand, cop, lab);
		}
		}
		throw new RuntimeException("unknown opcode encountered (" + opcode
				+ ")");
	}
	
	private Code readBinaryAssign(int opcode, boolean wideBase, boolean wideRest) throws IOException {
		int target = readBase(wideBase);
		int leftOperand = readBase(wideBase);
		int rightOperand = readBase(wideBase);
		int typeIdx = readRest(wideRest);
		Type type = typePool[typeIdx];
		switch(opcode) {
		case Code.OPCODE_append:
		case Code.OPCODE_appendl:
		case Code.OPCODE_appendr: {
			if (!(type instanceof Type.EffectiveList)) {
				throw new RuntimeException("expecting list type");
			}
			Code.BinListKind kind = Code.BinListKind.values()[opcode
					- Code.OPCODE_append];
			return Code.BinListOp((Type.EffectiveList) type, target,
					leftOperand, rightOperand, kind);
		}
		case Code.OPCODE_sappend:
		case Code.OPCODE_sappendl:
		case Code.OPCODE_sappendr: {
			if (!(type instanceof Type.Strung)) {
				throw new RuntimeException("expecting string type");
			}
			Code.BinStringKind kind = Code.BinStringKind.values()[opcode
					- Code.OPCODE_sappend];
			return Code.BinStringOp(target, leftOperand, rightOperand, kind);
		}
		case Code.OPCODE_indexof: {
			if (!(type instanceof Type.EffectiveIndexible)) {
				throw new RuntimeException("expecting indexible type");
			}
			return Code.IndexOf((Type.EffectiveIndexible) type, target,
					leftOperand, rightOperand);
		}
		case Code.OPCODE_add:
		case Code.OPCODE_sub:
		case Code.OPCODE_mul:
		case Code.OPCODE_div:
		case Code.OPCODE_rem:
		case Code.OPCODE_range:
		case Code.OPCODE_bitwiseor:
		case Code.OPCODE_bitwisexor:
		case Code.OPCODE_bitwiseand:
		case Code.OPCODE_lshr:
		case Code.OPCODE_rshr: {
			Code.BinArithKind kind = Code.BinArithKind.values()[opcode
					- Code.OPCODE_add];
			return Code.BinArithOp(type, target, leftOperand, rightOperand,
					kind);
		}
		case Code.OPCODE_union:
		case Code.OPCODE_unionl:
		case Code.OPCODE_unionr:
		case Code.OPCODE_intersect:
		case Code.OPCODE_intersectl:
		case Code.OPCODE_intersectr:
		case Code.OPCODE_difference:
		case Code.OPCODE_differencel: {
			if (!(type instanceof Type.EffectiveSet)) {
				throw new RuntimeException("expecting set type");
			}
			Code.BinSetKind kind = Code.BinSetKind.values()[opcode
					- Code.OPCODE_union];
			return Code.BinSetOp((Type.EffectiveSet) type, target, leftOperand,
					rightOperand, kind);
		}
			
		}
		throw new RuntimeException("unknown opcode encountered (" + opcode
				+ ")");
	}
	
	private Code readNaryOp(int opcode,boolean wideBase, boolean wideRest, int offset, HashMap<Integer,String> labels) throws IOException {		
		int nOperands = readBase(wideBase);
		int[] operands = new int[nOperands];		
		for(int i=0;i!=nOperands;++i) {
			operands[i] = readBase(wideBase);			
		}
		
		if(opcode == Code.OPCODE_loop) {
			// special case which doesn't have a type.
			int target = readTarget(wideRest,offset); 
			String label = findLabel(target,labels);
			return Code.Loop(label, operands);
		}
		
		int typeIdx = readRest(wideRest);
		Type type = typePool[typeIdx];
		
		switch(opcode) {		
			case Code.OPCODE_forall : {
				if (!(type instanceof Type.EffectiveCollection)) {
					throw new RuntimeException("expected collection type");
				}
				int target = readTarget(wideRest,offset);
				String label = findLabel(target, labels);
				int indexOperand = operands[0];
				int sourceOperand = operands[1];
				operands = Arrays.copyOfRange(operands, 2, operands.length);
				return Code.ForAll((Type.EffectiveCollection) type,
						sourceOperand, indexOperand, operands, label);
			}			
			case Code.OPCODE_indirectinvokefnv :
			case Code.OPCODE_indirectinvokemdv : {
				if (!(type instanceof Type.FunctionOrMethod)) {
					throw new RuntimeException("expected function or method type");
				}
				int operand = operands[0];
				operands = Arrays.copyOfRange(operands, 1, operands.length);
				return Code.IndirectInvoke((Type.FunctionOrMethod) type,
						Code.NULL_REG, operand, operands);
			}
			case Code.OPCODE_invokefnv:
			case Code.OPCODE_invokemdv: {
				if (!(type instanceof Type.FunctionOrMethod)) {
					throw new RuntimeException("expected function or method type");
				}
				int nameIdx = readRest(wideRest);;
				NameID nid = namePool[nameIdx];
				return Code.Invoke((Type.FunctionOrMethod) type, Code.NULL_REG,
						operands, nid);
			}
		}
		throw new RuntimeException("unknown opcode encountered (" + opcode
				+ ")");
	}
	
	private Code readNaryAssign(int opcode, boolean wideBase, boolean wideRest) throws IOException {
		int target = readBase(wideBase);
		int nOperands = readBase(wideBase);
		int[] operands = new int[nOperands];
		for(int i=0;i!=nOperands;++i) {
			operands[i] = readBase(wideBase);
		}
		int typeIdx = readRest(wideRest);
		Type type = typePool[typeIdx];
		switch(opcode) {
		case Code.OPCODE_indirectinvokefn:
		case Code.OPCODE_indirectinvokemd:{
			if(!(type instanceof Type.FunctionOrMethod)) {
				throw new RuntimeException("expected function or method type");
			}
			int operand = operands[0];
			operands = Arrays.copyOfRange(operands, 1, operands.length);
			return Code.IndirectInvoke((Type.FunctionOrMethod) type,
					target, operand, operands);
		}			
		case Code.OPCODE_invokefn:
		case Code.OPCODE_invokemd: {
			if(!(type instanceof Type.FunctionOrMethod)) {
				throw new RuntimeException("expected function or method type");
			}
			int nameIdx = readRest(wideRest);
			NameID nid = namePool[nameIdx];
			return Code.Invoke((Type.FunctionOrMethod) type, target, operands,
					nid);
		}			
		case Code.OPCODE_newmap: {
			if (!(type instanceof Type.Map)) {		
				throw new RuntimeException("expected map type");
			}
			return Code.NewMap((Type.Map) type, target, operands);
		}
		case Code.OPCODE_newrecord: {
			if (!(type instanceof Type.Record)) {		
				throw new RuntimeException("expected record type");
			}
			return Code.NewRecord((Type.Record) type, target, operands);
		}
		case Code.OPCODE_newlist: {
			if (!(type instanceof Type.List)) {
				throw new RuntimeException("expected list type");
			}
			return Code.NewList((Type.List) type, target, operands);
		}
		case Code.OPCODE_newset: {
			if (!(type instanceof Type.Set)) {
				throw new RuntimeException("expected set type");
			}
			return Code.NewSet((Type.Set) type, target, operands);
		}
		case Code.OPCODE_newtuple: {
			if (!(type instanceof Type.Tuple)) {
				throw new RuntimeException("expected tuple type");
			}
			return Code.NewTuple((Type.Tuple) type, target, operands);
		}	
		case Code.OPCODE_sublist: {
			if (!(type instanceof Type.EffectiveList)) {
				throw new RuntimeException("expected list type");
			}
			return Code.SubList((Type.EffectiveList) type, target, operands[0],operands[1],operands[2]);
		}
		case Code.OPCODE_substring: {
			if (!(type instanceof Type.Strung)) {
				throw new RuntimeException("expected string type");
			}
			return Code.SubString(target, operands[0],operands[1],operands[2]);
		}
		}
		throw new RuntimeException("unknown opcode encountered (" + opcode
				+ ")");
	}
	
	private Code readOther(int opcode, boolean wideBase, boolean wideRest, int offset, HashMap<Integer,String> labels) throws IOException {		
		switch (opcode) {
			case Code.OPCODE_trycatch: {
				int operand = readBase(wideBase);
				int target = readTarget(wideRest, offset);
				String label = findLabel(target, labels);
				int nCatches = readRest(wideRest);
				ArrayList<Pair<Type, String>> catches = new ArrayList<Pair<Type, String>>();
				for (int i = 0; i != nCatches; ++i) {
					Type type = typePool[readRest(wideRest)];
					String handler = findLabel(readTarget(wideRest, offset),
							labels);
					catches.add(new Pair<Type, String>(type, handler));
				}
				return Code.TryCatch(operand,label,catches);
			}
			case Code.OPCODE_update: {
				int target = readBase(wideBase);
				int nOperands = readBase(wideBase) - 1;
				int operand = readBase(wideBase);
				int[] operands = new int[nOperands];
				for (int i = 0; i != nOperands; ++i) {
					operands[i] = readBase(wideBase);
				}
				Type beforeType = typePool[readRest(wideRest)];
				Type afterType = typePool[readRest(wideRest)];
				int nFields = readRest(wideRest);
				ArrayList<String> fields = new ArrayList<String>();
				for (int i = 0; i != nFields; ++i) {
					String field = stringPool[readRest(wideRest)];
					fields.add(field);
				}
				return Code.Update(beforeType, target, operand, operands,
						afterType, fields);
			}
		}
		throw new RuntimeException("unknown opcode encountered (" + opcode
				+ ")");
	}
	
	private int readBase(boolean wide) throws IOException {
		if(wide) {
			return input.read_uv();
		} else {
			return input.read_un(4);
		}
	}
	
	private int readRest(boolean wide) throws IOException {
		if(wide) {
			return input.read_uv();
		} else {
			return input.read_u8();
		}
	}
	
	private int readTarget(boolean wide, int offset) throws IOException {
		if(wide) {
			return input.read_uv();
		} else {			
			return (input.read_u8() + offset) - 128;
		}
	}
	
	private static int labelCount = 0;

	private static String findLabel(int target, HashMap<Integer, String> labels) {
		String label = labels.get(target);
		if (label == null) {
			label = "label" + labelCount++;
			labels.put(target, label);
		}
		return label;
	}	
}
