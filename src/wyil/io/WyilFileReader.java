package wyil.io;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wybs.lang.Path;
import wybs.util.Trie;
import wyil.lang.*;
import wyil.util.Pair;
import wyjc.runtime.BigRational;
import wyjvm.io.BinaryInputStream;

/**
 * Read a binary WYIL file from a byte stream and convert into the corresponding
 * WyilFile object.
 * 
 * @author David J. Pearce
 * 
 */
public class WyilFileReader {
	private static final char[] magic = {'W','Y','I','L','F','I','L','E'};
	
	private final BinaryInputStream input;
	private final ArrayList<String> stringPool = new ArrayList<String>();
	private final ArrayList<Path.ID> pathPool = new ArrayList<Path.ID>();
	private final ArrayList<NameID> namePool = new ArrayList<NameID>();
	private final ArrayList<Value> constantPool = new ArrayList<Value>();
	private final ArrayList<Type> typePool = new ArrayList<Type>();
	
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
		
		System.out.println("VERSION: " + majorVersion + "." + minorVersion);		
		
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
		System.out.println("=== TYPES ===");		
		stringPool.clear();
		for(int i=0;i!=size;++i) {
			int length = input.read_uv();
			try {
				byte[] data = new byte[length];
				input.read(data);
				String str = new String(data,0,length,"UTF-8");
				System.out.println("#" + i + " = " + str);
				stringPool.add(str);
			} catch(UnsupportedEncodingException e) {
				throw new RuntimeException("UTF-8 Charset not supported?");
			}
		}
	}
	
	private void readPathPool(int size) throws IOException {
		System.out.println("=== PATHS ===");
		pathPool.clear();
		for(int i=0;i!=size;++i) {
			int parent = input.read_uv();
			int stringIndex = input.read_uv();
			Path.ID id;
			if(parent == 0) {
				id = Trie.ROOT;
			} else {
				id = pathPool.get(parent-1);
			}
			id = id.append(stringPool.get(stringIndex));
			pathPool.add(id);
			System.out.println("#" + i + " = " + id);
		}
	}

	private void readNamePool(int size) throws IOException {
		System.out.println("=== NAMES ===");
		namePool.clear();
		for (int i = 0; i != size; ++i) {
			// int kind = input.read_uv();
			int pathIndex = input.read_uv();
			int nameIndex = input.read_uv();
			Path.ID id = pathPool.get(pathIndex);
			String name = stringPool.get(nameIndex);
			namePool.add(new NameID(id, name));
		}
	}

	private void readConstantPool(int size) throws IOException {
		System.out.println("=== CONSTANTS ===");
		constantPool.clear();
		ValueReader bin = new ValueReader(input);
		for(int i=0;i!=size;++i) {
			Value v = bin.read();
			System.out.println("#" + i + " = " + v);
			constantPool.add(v);
		}
	}

	private void readTypePool(int size) throws IOException {
		System.out.println("=== TYPES ===");
		typePool.clear();
		Type.BinaryReader bin = new Type.BinaryReader(input);
		for(int i=0;i!=size;++i) {
			Type t = bin.readType();			
			typePool.add(t);
			System.out.println("#" + i + " = " + t);
		}
	}
	
	private WyilFile readModule() throws IOException {
		System.out.println("=== MODULE ===");		
		int kind = input.read_uv(); // block identifier
		int size = input.read_uv();	
		input.pad_u8();
		
		int nameIdx = input.read_uv();
		int numBlocks = input.read_uv();
		System.out.println("ID: " + pathPool.get(nameIdx));
		System.out.println("NUM BLOCKS: " + numBlocks);
		List<WyilFile.Declaration> declarations = new ArrayList<WyilFile.Declaration>();
		for(int i=0;i!=numBlocks;++i) {			
			declarations.add(readModuleBlock());
		}
		System.out.println("DONE");
		return new WyilFile(pathPool.get(nameIdx),"unknown.whiley",declarations);
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
		System.out.println("=== CONSTANT " + stringPool.get(nameIdx));
		
		int constantIdx = input.read_uv();
		int nBlocks = input.read_uv(); // unused
		
		input.pad_u8();				
		return new WyilFile.ConstantDeclaration(Collections.EMPTY_LIST,
				stringPool.get(nameIdx), constantPool.get(constantIdx));
	}
	
	private WyilFile.TypeDeclaration readTypeBlock() throws IOException {		
		int nameIdx = input.read_uv();
		System.out.println("=== TYPE " + stringPool.get(nameIdx));
		int typeIdx = input.read_uv();
		int nBlocks = input.read_uv();
		Block constraint = null;
		if(nBlocks != 0) {
			int kind = input.read_uv(); // unsued
			int size = input.read_uv();
			input.pad_u8();			
			constraint = readCodeBlock(1);
		}		
		
		input.pad_u8();
		
		return new WyilFile.TypeDeclaration(Collections.EMPTY_LIST,
				stringPool.get(nameIdx), typePool.get(typeIdx), constraint);
	}
	
	private WyilFile.MethodDeclaration readFunctionBlock() throws IOException {
		int nameIdx = input.read_uv();
		System.out.println("=== FUNCTION " + stringPool.get(nameIdx));
		int typeIdx = input.read_uv();
		int numCases = input.read_uv();		
		Type.Function type = (Type.Function) typePool.get(typeIdx);		
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
		return new WyilFile.MethodDeclaration(Collections.EMPTY_LIST,
				stringPool.get(nameIdx), type,
				cases);
	}
	
	private WyilFile.MethodDeclaration readMethodBlock() throws IOException {
		int nameIdx = input.read_uv();
		System.out.println("=== METHOD " + stringPool.get(nameIdx));
		
		int typeIdx = input.read_uv();
		int numCases = input.read_uv();
		
		Type.Method type = (Type.Method) typePool.get(typeIdx);
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
		return new WyilFile.MethodDeclaration(Collections.EMPTY_LIST,
				stringPool.get(nameIdx), type,
				cases);
	}
	
	private WyilFile.Case readFunctionOrMethodCase(Type.FunctionOrMethod type) throws IOException {
		Block precondition = null;
		Block postcondition = null;
		Block body = null;
		int numInputs = type.params().size();
		int nBlocks = input.read_uv();
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
			System.out.println("READ: " + code);
			block.append(code);
		}
				
		// FIXME: insert label opcodes.
		
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
			return readBinaryOp(opcode, wideBase,wideRest,offset, labels);
		case Code.FMT_BINARYASSIGN:
			return readBinaryAssign(opcode,wideBase,wideRest);
		case Code.FMT_NARYOP:
			return readNaryOp(opcode,wideBase,wideRest);
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
			Value c = constantPool.get(idx);
			return Code.Const(target,c);
		}
		case Code.OPCODE_goto: {
			int target = readRest(wideRest); 
			String lab = findLabel(offset + target,labels);
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
		Type type = typePool.get(typeIdx);
		switch(opcode) {
		case Code.OPCODE_debug:
			return Code.Debug(operand);		
		case Code.OPCODE_ifis: {
			int resultIdx = readRest(wideRest);
			Type result = typePool.get(resultIdx);
			int target = readTarget(wideRest,offset);
			String label = labels.get(target);
			return Code.IfIs(type, operand, result, label);
		}
		case Code.OPCODE_throw:
			return Code.Throw(type, operand);
		case Code.OPCODE_return:
			return Code.Return(type, operand);
		case Code.OPCODE_switch: {
			ArrayList<Pair<Value,String>> cases = new ArrayList<Pair<Value,String>>();
			int target = input.read_u8(); 
			String defaultLabel = findLabel(offset + target,labels);
			int nCases = readRest(wideRest);
			for(int i=0;i!=nCases;++i) {
				int constIdx = readTarget(wideRest,offset);
				Value constant = constantPool.get(constIdx);
				target = readRest(wideRest); 
				String label = findLabel(target,labels);
				cases.add(new Pair<Value,String>(constant,label));
			}
			return Code.Switch(type,operand,defaultLabel,cases);
		}
		}	
		throw new RuntimeException("unknown opcode encountered (" + opcode
				+ ")");
	}
	
	private Code readUnaryAssign(int opcode, boolean wideBase, boolean wideRest) throws IOException {
		int target = readBase(wideBase);
		System.out.println("TARGET: " + target);
		int operand = readBase(wideBase);
		int typeIdx = readRest(wideRest);
		Type type = typePool.get(typeIdx);
		switch(opcode) {
		case Code.OPCODE_convert: {
			int i = readRest(wideRest);
			Type t = typePool.get(i);
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
			String field = stringPool.get(i);
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
		Type type = typePool.get(typeIdx);
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
			String msg = stringPool.get(msgIdx);
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
			String msg = stringPool.get(msgIdx);
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
		Type type = typePool.get(typeIdx);
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
	
	private Code readNaryOp(int opcode,boolean wideBase, boolean wideRest) throws IOException {		
		int nOperands = readBase(wideBase);
		int[] operands = new int[nOperands];
		for(int i=0;i!=nOperands;++i) {
			operands[i] = readBase(wideBase);
		}
		int typeIdx = readRest(wideRest);
		Type type = typePool.get(typeIdx);
		switch(opcode) {
		case Code.OPCODE_indirectinvokemdv: {
			if(!(type instanceof Type.FunctionOrMethod)) {
				throw new RuntimeException("expected method type");
			}
			int operand = operands[0];
			operands = Arrays.copyOfRange(operands, 1, operands.length);
			return Code.IndirectInvoke((Type.FunctionOrMethod) type,
					Code.NULL_REG, operand, operands);
		}
		case Code.OPCODE_invokemdv: {
			if(!(type instanceof Type.FunctionOrMethod)) {
				throw new RuntimeException("expected method type");
			}
			int nameIdx = readRest(wideRest);;
			NameID nid = namePool.get(nameIdx);
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
		Type type = typePool.get(typeIdx);
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
			NameID nid = namePool.get(nameIdx);
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
		switch(opcode) {		
		case Code.OPCODE_trycatch:
			// FIXME: todo
		case Code.OPCODE_update:
			// FIXME: todo
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
			return input.read_u8() + offset - 128;
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
	
	public final class ValueReader  {		
		private BinaryInputStream reader;
		
		public ValueReader(BinaryInputStream input) {
			this.reader = input;	
		}
		
		public Value read() throws IOException {		
			int code = reader.read_u8();				
			switch (code) {			
			case WyilFileWriter.CONSTANT_Null:
				return Value.V_NULL;
			case WyilFileWriter.CONSTANT_False:
				return Value.V_BOOL(false);
			case WyilFileWriter.CONSTANT_True:
				return Value.V_BOOL(true);				
			case WyilFileWriter.CONSTANT_Byte:			
			{
				byte val = (byte) reader.read_u8();				
				return Value.V_BYTE(val);
			}
			case WyilFileWriter.CONSTANT_Char:			
			{
				char val = (char) reader.read_u16();				
				return Value.V_CHAR(val);
			}
			case WyilFileWriter.CONSTANT_Int:			
			{
				int len = reader.read_u16();				
				byte[] bytes = new byte[len];
				reader.read(bytes);
				BigInteger bi = new BigInteger(bytes);
				return Value.V_INTEGER(bi);
			}
			case WyilFileWriter.CONSTANT_Real:			
			{
				int len = reader.read_u16();
				byte[] bytes = new byte[len];
				reader.read(bytes);
				BigInteger num = new BigInteger(bytes);
				len = reader.read_u16();
				bytes = new byte[len];
				reader.read(bytes);
				BigInteger den = new BigInteger(bytes);
				BigRational br = new BigRational(num,den);
				return Value.V_RATIONAL(br);
			}
			case WyilFileWriter.CONSTANT_String:
			{
				int len = reader.read_u16();
				StringBuffer sb = new StringBuffer();
				for(int i=0;i!=len;++i) {
					char c = (char) reader.read_u16();
					sb.append(c);
				}
				return Value.V_STRING(sb.toString());
			}
			case WyilFileWriter.CONSTANT_List:
			{
				int len = reader.read_u16();
				ArrayList<Value> values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) read());
				}
				return Value.V_LIST(values);
			}
			case WyilFileWriter.CONSTANT_Set:
			{
				int len = reader.read_u16();
				ArrayList<Value> values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) read());
				}
				return Value.V_SET(values);
			}
			case WyilFileWriter.CONSTANT_Tuple:
			{
				int len = reader.read_u16();
				ArrayList<Value> values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) read());
				}
				return Value.V_TUPLE(values);
			}
			case WyilFileWriter.CONSTANT_Record:
			{
				int len = reader.read_u16();
				HashMap<String,Value> tvs = new HashMap<String,Value>();
				for(int i=0;i!=len;++i) {
					int idx = reader.read_u16();
					String str = stringPool.get(idx);
					Value lhs = (Value) read();
					tvs.put(str, lhs);
				}
				return Value.V_RECORD(tvs);
			}			
			}
			throw new RuntimeException("Unknown Value encountered in WhileyDefine: " + code);
		}
	}	
	
}
