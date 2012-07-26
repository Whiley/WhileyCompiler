package wyil.io;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.Path;
import wyil.lang.*;
import wyil.util.Pair;
import wyjc.runtime.BigRational;
import wyjvm.io.BinaryOutputStream;

/**
 * <p>
 * Responsible for writing a WyilFile to an output stream in binary form. The
 * binary format is structured to given maximum flexibility and to avoid
 * built-in limitations in terms of e.g. maximum sizes, etc.
 * </p>
 * 
 * <p>
 * The primitive component of a WyilFile is a <i>block</i>. Each block has a
 * kind, a given and then the payload. Blocks which are not recognised can be
 * ignored by skipping over their payload. Blocks are always byte aligned, but
 * their contents may not be.
 * </p>
 * 
 * <p>
 * A binary Wyil file begins with a header and a resource pool stratified into
 * sections containing the string constants, general constants, types and more.
 * Following the header are zero or more module blocks. Additional top-level
 * block kinds (e.g. for licenses) may be specified in the future. Each module
 * block consists of zero or more declaration blocks, which include method and
 * function declarations, type declarations and constant declarations.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class WyilFileWriter {
	private static final int MAJOR_VERSION = 0;
	private static final int MINOR_VERSION = 1;
	
	private final BinaryOutputStream out;
	
	private final ArrayList<String> stringPool  = new ArrayList<String>();
	private final HashMap<String,Integer> stringCache = new HashMap<String,Integer>();	
	private final ArrayList<PATH_Item> pathPool = new ArrayList<PATH_Item>();
	private final HashMap<Path.ID,Integer> pathCache = new HashMap<Path.ID,Integer>();	
	private final ArrayList<NAME_Item> namePool = new ArrayList<NAME_Item>();
	private final HashMap<NameID,Integer> nameCache = new HashMap<NameID,Integer>();	
	private final ArrayList<Value> constantPool = new ArrayList<Value>();
	private final HashMap<Value,Integer> constantCache = new HashMap<Value,Integer>();	
	private final ArrayList<Type> typePool = new ArrayList<Type>();
	private final HashMap<Type,Integer> typeCache = new HashMap<Type,Integer>();
	
	public WyilFileWriter(OutputStream output) {
		this.out = new BinaryOutputStream(output); 
	}
	
	public void close() throws IOException {
		out.close();
	}	
	
	public void write(WyilFile module) throws IOException {				
		// first, write magic number
		out.write_u8(0x57); // W
		out.write_u8(0x59); // Y
		out.write_u8(0x49); // I
		out.write_u8(0x4C); // L
		out.write_u8(0x46); // F
		out.write_u8(0x49); // I
		out.write_u8(0x4C); // L
		out.write_u8(0x45); // E

		// second, build pools
		buildPools(module);
		
		// third, write head block
		writeBlock(BLOCK_Header,module,out);
		
		// fourth, write module block(s)
		writeBlock(BLOCK_Module,module,out);
		
		out.flush();
	}	
	
	private void writeBlock(int kind, Object data, BinaryOutputStream output)
			throws IOException {
		
		output.pad_u8(); // pad out to next byte boundary
		
		// determine bytes for block
		byte[] bytes = null;
		switch(kind) {
			case BLOCK_Header:
				bytes = generateHeaderBlock((WyilFile) data);
				break;
			case BLOCK_Module:
				bytes = generateModuleBlock((WyilFile) data);
				break;
			case BLOCK_Type:
				bytes = generateTypeBlock((WyilFile.TypeDeclaration) data);
				break;
			case BLOCK_Constant:
				bytes = generateConstantBlock((WyilFile.ConstantDeclaration) data);
				break;
			case BLOCK_Function:
				bytes = generateMethodBlock((WyilFile.MethodDeclaration) data);
				break;
			case BLOCK_Method:
				bytes = generateMethodBlock((WyilFile.MethodDeclaration) data);
				break;
			case BLOCK_Case:
				bytes = generateMethodCaseBlock((WyilFile.Case) data);
				break;
			case BLOCK_Body:
			case BLOCK_Precondition:
			case BLOCK_Postcondition:
			case BLOCK_Constraint:
				bytes = generateCodeBlock((Block) data);
				break;
		}
		
		output.write_uv(kind);
		output.write_uv(bytes.length);
		output.pad_u8(); // pad out to next byte boundary
		output.write(bytes);
	}
	
	/**
	 * Write the header information for this WYIL file, including the stratified
	 * resource pool.
	 * 
	 * @param module
	 * 
	 * @throws IOException
	 */
	private byte[] generateHeaderBlock(WyilFile module)
			throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();		
		BinaryOutputStream output = new BinaryOutputStream(bytes);
		
		// second, write the file version number		
		output.write_uv(MAJOR_VERSION); 
		output.write_uv(MINOR_VERSION); 
		
		// third, write the various pool sizes
		output.write_uv(stringPool.size());
		output.write_uv(pathPool.size());
		output.write_uv(namePool.size());
		output.write_uv(constantPool.size());
		output.write_uv(typePool.size());		
		
		// finally, write the number of remaining blocks
		output.write_uv(module.declarations().size());
				
		writeStringPool(output);
		writePathPool(output);
		writeNamePool(output);
		writeConstantPool(output);
		writeTypePool(output);
		
		output.close();
		
		return bytes.toByteArray();
	}
	
	/**
	 * Write the list of strings making up the string pool in UTF8.
	 * 
	 * @throws IOException
	 */
	private void writeStringPool(BinaryOutputStream output) throws IOException {
		//System.out.println("Writing " + stringPool.size() + " string item(s).");
		for (String s : stringPool) {
			try {
				byte[] bytes = s.getBytes("UTF-8");
				output.write_uv(bytes.length);				
				output.write(bytes, 0, bytes.length);
			} catch (UnsupportedEncodingException e) {
				// hmmm, this aint pretty ;)
			}
		}
	}
	
	private void writePathPool(BinaryOutputStream output) throws IOException {
		//System.out.println("Writing " + stringPool.size() + " path item(s).");
		for (PATH_Item p : pathPool) {
			output.write_uv(p.parentIndex + 1);
			output.write_uv(p.stringIndex);
		}
	}

	private void writeNamePool(BinaryOutputStream output) throws IOException {
		//System.out.println("Writing " + stringPool.size() + " name item(s).");
		for (NAME_Item p : namePool) {
			//output.write_uv(p.kind.kind());
			output.write_uv(p.pathIndex);
			output.write_uv(p.nameIndex);
		}
	}

	private void writeConstantPool(BinaryOutputStream output) throws IOException {
		//System.out.println("Writing " + stringPool.size() + " constant item(s).");
		ValueWriter bout = new ValueWriter(output);
		for (Value v : constantPool) {
			bout.write(v);
		}
	}

	private void writeTypePool(BinaryOutputStream output) throws IOException {
		//System.out.println("Writing " + stringPool.size() + " type item(s).");
		Type.BinaryWriter bout = new Type.BinaryWriter(output);
		for (Type t : typePool) {
			bout.write(t);
		}
	}
	
	private byte[] generateModuleBlock(WyilFile module) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);
		
		output.write_uv(pathCache.get(module.id())); // FIXME: BROKEN!	
		output.write_uv(module.declarations().size());
		
		for(WyilFile.Declaration d : module.declarations()) {
			writeModuleBlock(d,output);
		}		
		
		return bytes.toByteArray();
	}
	
	private void writeModuleBlock(WyilFile.Declaration d,
			BinaryOutputStream output) throws IOException {
		if(d instanceof WyilFile.ConstantDeclaration) {
			writeBlock(BLOCK_Constant, d ,output);			
		} else if(d instanceof WyilFile.TypeDeclaration) {
			writeBlock(BLOCK_Type, d, output);			
		} else if(d instanceof WyilFile.MethodDeclaration) {
			WyilFile.MethodDeclaration md = (WyilFile.MethodDeclaration) d;
			if(md.type() instanceof Type.Function) {
				writeBlock(BLOCK_Function, d, output);
			} else {
				writeBlock(BLOCK_Method, d, output);
			}
		} 
	}
		
	private byte[] generateConstantBlock(WyilFile.ConstantDeclaration cd) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);
					
		// TODO: write modifiers
		output.write_uv(stringCache.get(cd.name()));
		output.write_uv(constantCache.get(cd.constant()));
		output.write_uv(0); // no sub-blocks
		// TODO: write annotations
		
		output.close();
		return bytes.toByteArray();
	}
	
	private byte[] generateTypeBlock(WyilFile.TypeDeclaration td) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);
				
		// TODO: write modifiers
		output.write_uv(stringCache.get(td.name()));
		output.write_uv(typeCache.get(td.type()));
		if(td.constraint() == null) {
			output.write_uv(0); // no sub-blocks
		} else {
			output.write_uv(1); // one sub-block
			writeBlock(BLOCK_Constraint,td.constraint(),output);
		}

		output.close();
		return bytes.toByteArray();
	}

	private byte[] generateMethodBlock(WyilFile.MethodDeclaration md) throws IOException {		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);
		
		output.write_uv(stringCache.get(md.name()));
		output.write_uv(typeCache.get(md.type()));			
		// TODO: write modifiers				
		output.write_uv(md.cases().size());

		for(WyilFile.Case c : md.cases()) {
			writeBlock(BLOCK_Case,c,output);		
		}
		
		// TODO: write annotations
		output.close();
		return bytes.toByteArray();
	}
	
	private byte[] generateMethodCaseBlock(WyilFile.Case c) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);
		
		int n = 0;
		n += c.precondition() != null ? 1 : 0;
		n += c.postcondition() != null ? 1 : 0;
		n += c.body() != null ? 1 : 0;
		output.write_uv(n);
		if(c.precondition() != null) {			
			writeBlock(BLOCK_Precondition,c.precondition(),output);
		}
		if(c.postcondition() != null) {			
			writeBlock(BLOCK_Postcondition,c.postcondition(),output);			
		}
		if(c.body() != null) {
			writeBlock(BLOCK_Body,c.body(),output);			
		}
		// TODO: write annotations
		
		output.close();
		return bytes.toByteArray();
	}
	
	private byte[] generateCodeBlock(Block block) throws IOException {		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();		
		BinaryOutputStream output = new BinaryOutputStream(bytes);
				
		HashMap<String,Integer> labels = new HashMap<String,Integer>();
		
		int nlabels = 0;
		int offset = 0;
		for(Block.Entry e : block) {
			Code code = e.code;
			if(code instanceof Code.Label) {
				Code.Label l = (Code.Label) code;
				labels.put(l.label, offset);
				nlabels++;
			}
			offset++;
		}
		
		output.write_uv(block.size()-nlabels); // instruction count (not same as block size!)
		offset = 0;
		for(Block.Entry e : block) {		
			if(e.code instanceof Code.Label) {
				
			} else {
				writeCode(e.code, offset++, labels, output);
			}
		}
		
		output.close();
		return bytes.toByteArray();
	}
	
	private void writeCode(Code code, int offset,
			HashMap<String, Integer> labels, BinaryOutputStream output)
			throws IOException {

		// first determine whether we need some kind of wide instruction.
		int width = calculateWidth(code, offset, labels);

		switch (width) {
			case Code.OPCODE_wide :
				output.write_u8(width);
				writeBase(true, code, output);
				writeRest(false, code, offset, labels, output);
				break;
			case Code.OPCODE_widerest :
				output.write_u8(width);
				writeBase(false, code, output);
				writeRest(true, code, offset, labels, output);
				break;
			case Code.OPCODE_widewide :
				output.write_u8(width);
				writeBase(true, code, output);
				writeRest(true, code, offset, labels, output);
				break;
			default :
				writeBase(false, code, output);
				writeRest(false, code, offset, labels, output);
		}
	}
	
	/**
	 * Write the "base" part of a bytecode instruction. This includes only the
	 * opcode itself and the operands (if any).
	 * 
	 * @param wide
	 *            --- indicates whether we should be writing the base in "wide"
	 *            format. That is, using the unlimited representation of
	 *            integers. In the alternative "short" representation, all
	 *            operands are written using exactly 4 unsigned bits. This means
	 *            we can encode registers 0-15, which covers the majority of
	 *            cases. The wide format is then used for cases when we have to
	 *            write a register operand whose index is > 15.
	 * @param code
	 *            --- The bytecode to be written.
	 * @param offset
	 *            --- The current offset of this bytecode in the bytecode array
	 *            being generated. This offset is measured in complete
	 *            bytecodes, not in e.g. bytes. Therefore, the first bytecode
	 *            has offset zero, the second bytecode has offset 1, etc. The
	 *            offset is required
	 * @param output
	 *            --- The binary stream to write this bytecode to.
	 * @throws IOException
	 */
	private void writeBase(boolean wide, Code code, 
			BinaryOutputStream output) throws IOException {

		// second, deal with standard instruction formats
		output.write_u8(code.opcode());
		
		if(code instanceof Code.AbstractUnaryOp) {
			Code.AbstractUnaryOp<Type> a = (Code.AbstractUnaryOp) code;
			if(a.operand != Code.NULL_REG) { 				
				writeBase(wide,a.operand,output);
			} else {
				// possible only for empty return
			}			
		} else if(code instanceof Code.AbstractBinaryOp) {
			Code.AbstractBinaryOp<Type> a = (Code.AbstractBinaryOp) code;		
			writeBase(wide,a.leftOperand,output);
			writeBase(wide,a.rightOperand,output);
		} else if(code instanceof Code.AbstractUnaryAssignable) {
			Code.AbstractUnaryAssignable<Type> a = (Code.AbstractUnaryAssignable) code;
			writeBase(wide,a.target,output);
			writeBase(wide,a.operand,output);
		} else if(code instanceof Code.AbstractBinaryAssignable) {
			Code.AbstractBinaryAssignable<Type> a = (Code.AbstractBinaryAssignable) code;
			writeBase(wide, a.target,output);
			writeBase(wide, a.leftOperand,output);
			writeBase(wide, a.rightOperand,output);
		} else if(code instanceof Code.AbstractNaryAssignable) {
			Code.AbstractNaryAssignable<Type> a = (Code.AbstractNaryAssignable) code;
			if(a.target != Code.NULL_REG) {
				writeBase(wide,a.target,output);
			}
			int[] operands = a.operands;			
			writeBase(wide,operands.length,output);
			for(int i=0;i!=operands.length;++i) {
				writeBase(wide,operands[i],output);
			}
		} else if(code instanceof Code.AbstractSplitNaryAssignable) {
			Code.AbstractSplitNaryAssignable<Type> a = (Code.AbstractSplitNaryAssignable) code;
			if(a.target != Code.NULL_REG) {
				writeBase(wide,a.target,output);
			}			
			int[] operands = a.operands;			
			writeBase(wide,operands.length+1,output);
			writeBase(wide,a.operand,output);
			for(int i=0;i!=operands.length;++i) {
				writeBase(wide,operands[i],output);
			}
		} else if(code instanceof Code.AbstractAssignable) {
			Code.AbstractAssignable c = (Code.AbstractAssignable) code;
			writeBase(wide,c.target,output);
		} else if(code instanceof Code.ForAll) {
			Code.ForAll l = (Code.ForAll) code;			
			int[] operands = l.modifiedOperands;	
			writeBase(wide,operands.length + 2,output);
			writeBase(wide,l.indexOperand,output);
			writeBase(wide,l.sourceOperand,output);			
			for(int i=0;i!=operands.length;++i) {
				writeBase(wide,operands[i],output);
			}
		} else if(code instanceof Code.Loop) {
			Code.Loop l = (Code.Loop) code;			
			int[] operands = l.modifiedOperands;	
			writeBase(wide,operands.length,output);
			for(int i=0;i!=operands.length;++i) {
				writeBase(wide,operands[i],output);
			}
		} else if(code instanceof Code.TryCatch) {
			Code.TryCatch tc = (Code.TryCatch) code;			
			writeBase(wide,tc.operand,output);
		} 
	}
	
	/**
	 * Write the "rest" of a bytecode instruction. This includes any additional
	 * information, such as the type and/or other pool items required for the
	 * bytecode.
	 * 
	 * @param wide
	 *            --- indicates whether we should be writing the rest in "wide"
	 *            format. That is, using the unlimited representation of
	 *            integers. In the alternative "short" representation, all pool
	 *            indices are written using exactly 8 unsigned bits. This means
	 *            we can encode pool indices 0-255, which covers a large number
	 *            of cases. The wide format is then used for cases when we have
	 *            to a pool index > 255.
	 * @param code
	 *            --- The bytecode to be written.
	 * @param offset
	 *            --- The current offset of this bytecode in the bytecode array
	 *            being generated. This offset is measured in complete
	 *            bytecodes, not in e.g. bytes. Therefore, the first bytecode
	 *            has offset zero, the second bytecode has offset 1, etc. The
	 *            offset is required for calculating jump targets for branching
	 *            instructions (e.g. goto). Since jump targets (in short form)
	 *            are encoded as a relative offset, we need to know our current
	 *            offset to compute the relative target.
	 * @param labels
	 *            --- A map from label to offset. This is required to determine
	 *            the (relative) jump offset for a branching instruction.
	 * @param output
	 *            --- The binary output stream to which this bytecode is being
	 *            written.
	 * @throws IOException
	 */
	private void writeRest(boolean wide, Code code, int offset,
			HashMap<String, Integer> labels, BinaryOutputStream output) throws IOException {
		
		if(code instanceof Code.AbstractUnaryOp) {
			Code.AbstractUnaryOp<Type> a = (Code.AbstractUnaryOp) code;
			if(a.operand != Code.NULL_REG) { 
				writeRest(wide,typeCache.get(a.type),output);
			} else {
				// possible only for empty return
			}
		} else if(code instanceof Code.AbstractBinaryOp) {
			Code.AbstractBinaryOp<Type> a = (Code.AbstractBinaryOp) code;
			writeRest(wide,typeCache.get(a.type),output);
		} else if(code instanceof Code.AbstractUnaryAssignable) {
			Code.AbstractUnaryAssignable<Type> a = (Code.AbstractUnaryAssignable) code;
			writeRest(wide,typeCache.get(a.type),output);
		} else if(code instanceof Code.AbstractBinaryAssignable) {
			Code.AbstractBinaryAssignable<Type> a = (Code.AbstractBinaryAssignable) code;
			writeRest(wide,typeCache.get(a.type),output);
		} else if(code instanceof Code.AbstractNaryAssignable) {
			Code.AbstractNaryAssignable<Type> a = (Code.AbstractNaryAssignable) code;
			writeRest(wide,typeCache.get(a.type),output);
		} else if(code instanceof Code.AbstractSplitNaryAssignable) {
			Code.AbstractSplitNaryAssignable<Type> a = (Code.AbstractSplitNaryAssignable) code;			
			writeRest(wide,typeCache.get(a.type),output);
		}	
		// now deal with non-uniform instructions
		// First, deal with special cases
		if(code instanceof Code.AssertOrAssume) {
			Code.AssertOrAssume c = (Code.AssertOrAssume) code;
			writeRest(wide,stringCache.get(c.msg),output);
		} else if(code instanceof Code.Const) {
			Code.Const c = (Code.Const) code;
			writeRest(wide,constantCache.get(c.constant),output);
		} else if(code instanceof Code.Convert) {
			Code.Convert c = (Code.Convert) code;
			writeRest(wide,typeCache.get(c.result),output);
		} else if(code instanceof Code.FieldLoad) {
			Code.FieldLoad c = (Code.FieldLoad) code;
			writeRest(wide,stringCache.get(c.field),output);			
		} else if(code instanceof Code.ForAll) {
			Code.ForAll f = (Code.ForAll) code;
			writeRest(wide,typeCache.get(f.type),output);
			int target = labels.get(f.target);
			writeTarget(wide,offset,target,output);
		} else if(code instanceof Code.IfIs) {
			Code.IfIs c = (Code.IfIs) code;
			int target = labels.get(c.target) - offset;			
			writeRest(wide,typeCache.get(c.rightOperand),output); 
			writeTarget(wide,offset,target,output);			
		} else if(code instanceof Code.If) {
			Code.If c = (Code.If) code;
			int target = labels.get(c.target);
			writeTarget(wide,offset,target,output);
		} else if(code instanceof Code.Goto) {
			Code.Goto c = (Code.Goto) code;
			int target = labels.get(c.target); 
			writeTarget(wide,offset,target,output);
		} else if(code instanceof Code.Invoke) {
			Code.Invoke c = (Code.Invoke) code;
			writeRest(wide,nameCache.get(c.name),output);			
		} else if(code instanceof Code.Loop) {
			Code.Loop l = (Code.Loop) code;
			int target = labels.get(l.target);
			writeTarget(wide,offset,target,output);
		} else if(code instanceof Code.Update) {
			Code.Update c = (Code.Update) code;
			List<String> fields = c.fields;
			writeRest(wide,typeCache.get(c.afterType),output);
			writeRest(wide,fields.size(),output);
			for (int i = 0; i != fields.size(); ++i) {
				writeRest(wide, stringCache.get(fields.get(i)), output);
			}
		} else if(code instanceof Code.Switch) {
			Code.Switch c = (Code.Switch) code;
			List<Pair<Value,String>> branches = c.branches;
			int target = labels.get(c.defaultTarget) - offset; 			
			writeTarget(wide,offset,target,output);
			writeRest(wide,branches.size(),output);
			for(Pair<Value,String> b : branches) {
				writeRest(wide,constantCache.get(b.first()),output);
				target = labels.get(b.second()); 
				writeTarget(wide,offset,target,output);
			}
		} else if(code instanceof Code.TryCatch) {
			Code.TryCatch tc = (Code.TryCatch) code;
			int target = labels.get(tc.target);
			ArrayList<Pair<Type,String>> catches = tc.catches;
			writeTarget(wide,offset,target,output);
			writeRest(wide,catches.size(),output);
			for (int i = 0; i != catches.size(); ++i) {
				Pair<Type, String> handler = catches.get(i);
				writeRest(wide, typeCache.get(handler.first()), output);
				writeTarget(wide, offset, labels.get(handler.second()), output);
			}
		} else if(code instanceof Code.TupleLoad) {
			Code.TupleLoad c = (Code.TupleLoad) code;			
			writeRest(wide,c.index,output);
		} 
	}
	
	private void writeBase(boolean wide, int value, BinaryOutputStream output) throws IOException {
		if(wide) {
			output.write_uv(value);
		} else {
			if(value >= 16) {
				throw new IllegalArgumentException("invalid base value");
			}
			output.write_un(value,4);
		}
	}
	
	private void writeRest(boolean wide, int value, BinaryOutputStream output) throws IOException {
		if(wide) {
			output.write_uv(value);
		} else {
			if(value >= 256) {
				throw new IllegalArgumentException("invalid base value");
			}
			output.write_u8(value);
		}
	}	
	
	private void writeTarget(boolean wide, int offset, int target, BinaryOutputStream output) throws IOException {
		if(wide) {
			output.write_uv(target);
		} else {
			output.write_u8((target-offset) + 128);
		}
	}
	
	/**
	 * Calculate the "width" of a given bytecode. That is, whether or not either
	 * of the base or remainder components need to be encoded using the "wide"
	 * format. The wide format allows for unlimited precision, but occupies more
	 * space. The alternative "short" format uses fixed precision, but cannot
	 * encode all possible register operands and/or pool indices.
	 * 
	 * @param code
	 *            --- The bytecode whose width is to be determined.
	 * @param offset
	 *            --- The current offset of this bytecode in the bytecode array
	 *            being generated. This offset is measured in complete
	 *            bytecodes, not in e.g. bytes. Therefore, the first bytecode
	 *            has offset zero, the second bytecode has offset 1, etc. The
	 *            offset is required for calculating jump targets for branching
	 *            instructions (e.g. goto). Since jump targets (in short form)
	 *            are encoded as a relative offset, we need to know our current
	 *            offset to compute the relative target.
	 * @param labels
	 *            --- A map from label to offset. This is required to determine
	 *            the (relative) jump offset for a branching instruction.
	 * @return
	 */
	private int calculateWidth(Code code, int offset, HashMap<String,Integer> labels) {
		int maxBase = 0;
		int maxRest = 0;
		
		if(code instanceof Code.AbstractUnaryOp) {
			Code.AbstractUnaryOp<Type> a = (Code.AbstractUnaryOp) code;
			maxBase = a.operand;
			maxRest = typeCache.get(a.type);
		} else if(code instanceof Code.AbstractBinaryOp) {
			Code.AbstractBinaryOp<Type> a = (Code.AbstractBinaryOp) code;
			maxBase = Math.max(a.leftOperand,a.rightOperand);
			maxRest = typeCache.get(a.type);
		} else if(code instanceof Code.AbstractUnaryAssignable) {
			Code.AbstractUnaryAssignable<Type> a = (Code.AbstractUnaryAssignable) code;
			maxBase = Math.max(a.target,a.operand);
			maxRest = typeCache.get(a.type);
		} else if(code instanceof Code.AbstractBinaryAssignable) {
			Code.AbstractBinaryAssignable<Type> a = (Code.AbstractBinaryAssignable) code;
			maxBase = Math.max(a.leftOperand,a.rightOperand);
			maxBase = Math.max(a.target,maxBase);
			maxRest = typeCache.get(a.type);
		} else if(code instanceof Code.AbstractNaryAssignable) {
			Code.AbstractNaryAssignable<Type> a = (Code.AbstractNaryAssignable) code;
			int[] operands = a.operands;
			maxBase = Math.max(a.target,operands.length);
			for(int i=0;i!=operands.length;++i) {
				maxBase = Math.max(maxBase,operands[i]);
			}
			maxRest = typeCache.get(a.type);
		} else if(code instanceof Code.AbstractSplitNaryAssignable) {
			Code.AbstractSplitNaryAssignable<Type> a = (Code.AbstractSplitNaryAssignable) code;			
			int[] operands = a.operands;
			maxBase = Math.max(a.target,operands.length+1);
			maxBase = Math.max(maxBase,a.operand);
			for(int i=0;i!=operands.length;++i) {
				maxBase = Math.max(maxBase,operands[i]);
			}
			maxRest = typeCache.get(a.type);
		} else if(code instanceof Code.AbstractAssignable) {
			Code.AbstractAssignable a = (Code.AbstractAssignable) code;
			maxBase = a.target;			
		} 
		
		// now, deal with non-uniform opcodes
		if(code instanceof Code.AssertOrAssume) {
			Code.AssertOrAssume c = (Code.AssertOrAssume) code;
			maxRest = Math.max(maxRest,stringCache.get(c.msg));
		} else if(code instanceof Code.Const) {
			Code.Const c = (Code.Const) code;
			maxRest = Math.max(maxRest,constantCache.get(c.constant));
		} else if(code instanceof Code.Convert) {
			Code.Convert c = (Code.Convert) code;
			maxRest = Math.max(maxRest,typeCache.get(c.result));
		} else if(code instanceof Code.FieldLoad) {
			Code.FieldLoad c = (Code.FieldLoad) code;
			maxRest = Math.max(maxRest,stringCache.get(c.field));			
		} else if(code instanceof Code.ForAll) {
			Code.ForAll f = (Code.ForAll) code;
			int[] operands = f.modifiedOperands;
			maxBase = Math.max(f.sourceOperand, f.indexOperand);				
			for(int i=0;i!=operands.length;++i) {
				maxBase = Math.max(maxBase, operands[i]);
			}
			maxRest = Math.max(maxRest,typeCache.get(f.type));
			maxRest = Math.max(maxRest,targetWidth(f.target, offset, labels));
		} else if(code instanceof Code.IfIs) {
			Code.IfIs c = (Code.IfIs) code;
			maxRest = Math.max(maxRest,typeCache.get(c.rightOperand)); 
			maxRest = Math.max(maxRest,targetWidth(c.target, offset, labels));			
		} else if(code instanceof Code.If) {
			Code.If c = (Code.If) code;
			maxRest = Math.max(maxRest,targetWidth(c.target, offset, labels));
		} else if(code instanceof Code.Goto) {
			Code.Goto c = (Code.Goto) code;			
			maxRest = Math.max(maxRest,targetWidth(c.target, offset, labels));
		} else if(code instanceof Code.Invoke) {
			Code.Invoke c = (Code.Invoke) code;
			maxRest = Math.max(maxRest,nameCache.get(c.name));			
		} else if(code instanceof Code.Loop) {
			Code.Loop l = (Code.Loop) code;
			int[] operands = l.modifiedOperands;
			maxBase = 0;				
			for(int i=0;i!=operands.length;++i) {
				maxBase = Math.max(maxBase, operands[i]);
			}
			maxRest = Math.max(maxRest,targetWidth(l.target, offset, labels));
		} else if(code instanceof Code.Update) {
			Code.Update c = (Code.Update) code;
			maxRest = Math.max(maxRest,typeCache.get(c.afterType));
			ArrayList<String> fields = c.fields; 
			for(int i=0;i!=fields.size();++i) {
				String field = fields.get(i);
				maxRest = Math.max(maxRest,stringCache.get(field));				
			}
		} else if(code instanceof Code.Switch) {
			Code.Switch c = (Code.Switch) code;
			List<Pair<Value,String>> branches = c.branches;
			maxRest = Math.max(maxRest,targetWidth(c.defaultTarget, offset, labels));
			maxRest = Math.max(maxRest,branches.size());
			for(Pair<Value,String> b : branches) {
				maxRest = Math.max(maxRest,constantCache.get(b.first()));
				maxRest = Math.max(maxRest,targetWidth(b.second(), offset, labels));
			}
		} else if(code instanceof Code.TryCatch) {
			Code.TryCatch tc = (Code.TryCatch) code;
			maxBase = tc.operand;			
			maxRest = Math.max(maxRest,
					targetWidth(tc.target, offset, labels));
			ArrayList<Pair<Type,String>> catches = tc.catches;
			maxRest = Math.max(maxRest,catches.size());
			for(int i=0;i!=catches.size();++i) {
				Pair<Type,String> handler = catches.get(i);				
				maxRest = Math.max(maxRest,typeCache.get(handler.first()));
				maxRest = Math.max(maxRest,
						targetWidth(handler.second(), offset, labels));
			}			
		} else if(code instanceof Code.TupleLoad) {
			Code.TupleLoad c = (Code.TupleLoad) code;			
			maxRest = Math.max(maxRest,c.index);
		} 
		
		if(maxBase < 16) {
			if(maxRest < 256) {
				return 0; // standard
			} else {
				return Code.OPCODE_widerest; 
			}
		} else {
			if(maxRest < 256) {
				return Code.OPCODE_wide; 
			} else {
				return Code.OPCODE_widewide; 
			}
		}
	}
	
	private int targetWidth(String label, int offset,
			HashMap<String, Integer> labels) {
		int target = labels.get(label) - offset;
		return target + 128;
	}
	
	private void buildPools(WyilFile module) {
		stringPool.clear();
		stringCache.clear();
		
		pathPool.clear();
		pathCache.clear();
		
		namePool.clear();
		nameCache.clear();
		
		constantPool.clear();
		constantCache.clear();
		
		typePool.clear();
		typeCache.clear();
		
		addPathItem(module.id());
		for(WyilFile.Declaration d : module.declarations()) {
			buildPools(d);
		}
	}
	
	private void buildPools(WyilFile.Declaration declaration) {
		if(declaration instanceof WyilFile.TypeDeclaration) {
			buildPools((WyilFile.TypeDeclaration)declaration);
		} else if(declaration instanceof WyilFile.ConstantDeclaration) {
			buildPools((WyilFile.ConstantDeclaration)declaration);
		} else if(declaration instanceof WyilFile.MethodDeclaration) {
			buildPools((WyilFile.MethodDeclaration)declaration);
		} 
	}
	
	private void buildPools(WyilFile.TypeDeclaration declaration) {
		addStringItem(declaration.name());
		addTypeItem(declaration.type());		
		buildPools(declaration.constraint());		
	}
	
	private void buildPools(WyilFile.ConstantDeclaration declaration) {
		addStringItem(declaration.name());
		addConstantItem(declaration.constant());
	}

	private void buildPools(WyilFile.MethodDeclaration declaration) {
		addStringItem(declaration.name());
		addTypeItem(declaration.type());
		for(WyilFile.Case c : declaration.cases()) {			
			buildPools(c.precondition());						
			buildPools(c.body());						
			buildPools(c.postcondition());			
		}
	}
	
	private void buildPools(Block block) {
		if(block == null) { return; }
		for(Block.Entry e : block) {
			buildPools(e.code);
		}
	}
	
	private void buildPools(Code code) {
		
		// First, deal with special cases
		if(code instanceof Code.AssertOrAssume) {
			Code.AssertOrAssume c = (Code.AssertOrAssume) code;
			addStringItem(c.msg);
		} else if(code instanceof Code.Const) {
			Code.Const c = (Code.Const) code;
			addConstantItem(c.constant);
		} else if(code instanceof Code.Convert) {
			Code.Convert c = (Code.Convert) code;
			addTypeItem(c.result);
		} else if(code instanceof Code.FieldLoad) {
			Code.FieldLoad c = (Code.FieldLoad) code;
			addStringItem(c.field);
		} else if(code instanceof Code.ForAll) {
			Code.ForAll s = (Code.ForAll) code;
			addTypeItem((Type)s.type);			
		}else if(code instanceof Code.IfIs) {
			Code.IfIs c = (Code.IfIs) code;
			addTypeItem(c.type);
			addTypeItem(c.rightOperand);
		} else if(code instanceof Code.Invoke) {
			Code.Invoke c = (Code.Invoke) code;
			addNameItem(c.name);			
		} else if(code instanceof Code.Update) {
			Code.Update c = (Code.Update) code;
			addTypeItem(c.type);
			addTypeItem(c.afterType);
			for(Code.LVal l : c) {
				if(l instanceof Code.RecordLVal) {
					Code.RecordLVal lv = (Code.RecordLVal) l;
					addStringItem(lv.field);
				}
			}
		} else if(code instanceof Code.Switch) {
			Code.Switch s = (Code.Switch) code;
			addTypeItem(s.type);
			for(Pair<Value,String> b : s.branches) {
				addConstantItem(b.first());
			}
		} else if(code instanceof Code.TryCatch) {
			Code.TryCatch tc = (Code.TryCatch) code;
			ArrayList<Pair<Type, String>> catches = tc.catches;
			for (int i = 0; i != catches.size(); ++i) {
				Pair<Type, String> handler = catches.get(i);
				addTypeItem(handler.first());
			}
		}
		
		// Second, deal with standard cases
		if(code instanceof Code.AbstractUnaryOp) {
			Code.AbstractUnaryOp<Type> a = (Code.AbstractUnaryOp) code;
			addTypeItem(a.type);
		} else if(code instanceof Code.AbstractBinaryOp) {
			Code.AbstractBinaryOp<Type> a = (Code.AbstractBinaryOp) code;
			addTypeItem(a.type);
		} else if(code instanceof Code.AbstractUnaryAssignable) {
			Code.AbstractUnaryAssignable<Type> a = (Code.AbstractUnaryAssignable) code;
			addTypeItem(a.type);
		} else if(code instanceof Code.AbstractBinaryAssignable) {
			Code.AbstractBinaryAssignable<Type> a = (Code.AbstractBinaryAssignable) code;
			addTypeItem(a.type);
		} else if(code instanceof Code.AbstractNaryAssignable) {
			Code.AbstractNaryAssignable<Type> a = (Code.AbstractNaryAssignable) code;
			addTypeItem(a.type);
		} else if(code instanceof Code.AbstractSplitNaryAssignable) {
			Code.AbstractSplitNaryAssignable<Type> a = (Code.AbstractSplitNaryAssignable) code;
			addTypeItem(a.type);
		}
	}
	
	private int addNameItem(NameID name) {
		Integer index = nameCache.get(name);
		if(index == null) {
			int i = namePool.size();
			nameCache.put(name, i);
			namePool.add(new NAME_Item(addPathItem(name.module()),
					addStringItem(name.name())));
			return i;			
		} else {
			return index;
		}
	}
	
	private int addStringItem(String string) {
		Integer index = stringCache.get(string);
		if(index == null) {
			int i = stringPool.size();
			stringCache.put(string, i);
			stringPool.add(string);
			return i;
		} else {
			return index;
		}
	}
	
	private int addPathItem(Path.ID pid) {
		if(pid.last().equals("")) {
			return -1;
		}
		
		Integer index = pathCache.get(pid);
		if(index == null) {
			int i = pathPool.size();
			pathCache.put(pid, i);
			pathPool.add(new PATH_Item(addPathItem(pid.parent()),addStringItem(pid.last())));
			return i;
		} else {
			return index;
		}
	}
	
	private int addTypeItem(Type t) {
		
		// TODO: this could be made way more efficient. In particular, we should
		// combine resources into a proper aliased pool rather than write out
		// types individually ... because that's sooooo inefficient!
		
		Integer index = typeCache.get(t);
		if(index == null) {
			int i = typePool.size();
			typeCache.put(t, i);
			typePool.add(t);			
			return i;
		} else {
			return index;
		}
	}
	
	private int addConstantItem(Value v) {
		
		// TODO: this could be made way more efficient. In particular, we should
		// combine resources into a proper aliased pool rather than write out
		// types individually ... because that's sooooo inefficient!
	
		Integer index = constantCache.get(v);
		if(index == null) {
			int i = constantPool.size();
			constantCache.put(v, i);
			constantPool.add(v);
			addConstantStringItems(v);
			return i;
		}
		return index;
	}
	
	private void addConstantStringItems(Value v) {
		if(v instanceof Value.Strung) {
			Value.Strung s = (Value.Strung) v;
			addStringItem(s.value);
		} else if(v instanceof Value.List) {
			Value.List l = (Value.List) v;				
			for (Value e : l.values) {
				addConstantStringItems(e);
			}
		} else if(v instanceof Value.Set) {
			Value.Set s = (Value.Set) v;
			for (Value e : s.values) {
				addConstantStringItems(e);
			}
		} else if(v instanceof Value.Map) {				
			Value.Map m = (Value.Map) v;
			for (Map.Entry<Value,Value> e : m.values.entrySet()) {
				addConstantStringItems(e.getKey());
				addConstantStringItems(e.getValue());
			}
		} else if(v instanceof Value.Tuple) {
			Value.Tuple t = (Value.Tuple) v;
			for (Value e : t.values) {
				addConstantStringItems(e);
			}
		} else if(v instanceof Value.Record) {
			Value.Record r = (Value.Record) v;
			for (Map.Entry<String,Value> e : r.values.entrySet()) {
				addStringItem(e.getKey());
				addConstantStringItems(e.getValue());
			}				
		} else if(v instanceof Value.FunctionOrMethod){
			// TODO
		} 			
	} 
	
	/**
	 * An PATH_Item represents a path item.
	 * 
	 * @author David J. Pearce
	 *
	 */
	private class PATH_Item {
		/**
		 * The index in the path pool of the parent for this item, or -1 if it
		 * has not parent.
		 */
		public final int parentIndex;
		
		/**
		 * The index of this path component in the string pool
		 */
		public final int stringIndex;
		
		public PATH_Item(int parentIndex, int stringIndex) {
			this.parentIndex = parentIndex;
			this.stringIndex = stringIndex;
		}		
	}
	
	private enum NAME_Kind {
		PACKAGE(0), 
		MODULE(1), 
		CONSTANT(2), 
		TYPE(3), 
		FUNCTION(4), 
		METHOD(5);

		private final int kind;

		private NAME_Kind(int kind) {
			this.kind = kind;
		}

		public int kind() {
			return kind;
		}
	}
	
	/**
	 * A NAME_Item represents a named path item, such as a package, module or
	 * something within a module (e.g. a function or method declaration).
	 * 
	 * @author David J. Pearce
	 * 
	 */
	private class NAME_Item {
		/**
		 * The kind of name item this represents.
		 */
		// public final NAME_Kind kind;
		
		/**
		 * Index of path for this item in path pool
		 */
		public final int pathIndex;
		
		/**
		 * Index of string for this named item
		 */
		public final int nameIndex;
		
		public NAME_Item(/*NAME_Kind kind, */int pathIndex, int nameIndex) {
			//this.kind = kind;
			this.pathIndex = pathIndex;
			this.nameIndex = nameIndex;
		}				
	}		
	
	// FIXME: this is really a temporary class because we ideally want to
	// exploit the potential for sharing amongst substructure of values.
	private class ValueWriter {
		private final BinaryOutputStream output;
		
		public ValueWriter(BinaryOutputStream output) {
			this.output = output;
		}
		
		public void write(Value val) throws IOException {
			if(val instanceof Value.Null) {
				write((Value.Null) val);
			} else if(val instanceof Value.Bool) {
				write((Value.Bool) val);
			} else if(val instanceof Value.Byte) {
				write((Value.Byte) val);
			} else if(val instanceof Value.Char) {
				write((Value.Char) val);
			} else if(val instanceof Value.Integer) {
				write((Value.Integer) val);
			} else if(val instanceof Value.Rational) {
				write((Value.Rational) val);
			} else if(val instanceof Value.Strung) {
				write((Value.Strung) val);
			} else if(val instanceof Value.Set) {
				write((Value.Set) val);
			} else if(val instanceof Value.List) {
				write((Value.List) val);
			} else if(val instanceof Value.Map) {
				write((Value.Map) val);
			} else if(val instanceof Value.Record) {
				write((Value.Record) val);
			} else if(val instanceof Value.Tuple) {
				write((Value.Tuple) val);
			} else if(val instanceof Value.FunctionOrMethod) {
				write((Value.FunctionOrMethod) val);
			} else {
				throw new RuntimeException("Unknown value encountered - " + val);
			}
		}
		
		public void write(Value.Null expr) throws IOException {				
			output.write_u8(CONSTANT_Null);
		}
		
		public void write(Value.Bool expr) throws IOException {
			
			if(expr.value) {
				output.write_u8(CONSTANT_True);
			} else {
				output.write_u8(CONSTANT_False);
			}
		}
		
		public void write(Value.Byte expr) throws IOException {		
			output.write_u8(CONSTANT_Byte);		
			output.write_u8(expr.value);		
		}
		
		public void write(Value.Char expr) throws IOException {		
			output.write_u8(CONSTANT_Char);		
			output.write_u16(expr.value);		
		}
		
		public void write(Value.Integer expr) throws IOException {		
			output.write_u8(CONSTANT_Int);		
			BigInteger num = expr.value;
			byte[] numbytes = num.toByteArray();
			// FIXME: bug here for constants that require more than 65535 bytes
			output.write_u16(numbytes.length);
			output.write(numbytes);
		}

		public void write(Value.Rational expr) throws IOException {		
			
			output.write_u8(CONSTANT_Real);
			BigRational br = expr.value;
			BigInteger num = br.numerator();
			BigInteger den = br.denominator();

			byte[] numbytes = num.toByteArray();
			// FIXME: bug here for constants that require more than 65535 bytes
			output.write_u16(numbytes.length);
			output.write(numbytes);

			byte[] denbytes = den.toByteArray();
			// FIXME: bug here for constants that require more than 65535 bytes
			output.write_u16(denbytes.length);
			output.write(denbytes);
		}
			
		public void write(Value.Strung expr) throws IOException {	
			output.write_u8(CONSTANT_String);
			String value = expr.value;
			int valueLength = value.length();		
			output.write_u16(valueLength);
			for(int i=0;i!=valueLength;++i) {
				output.write_u16(value.charAt(i));
			}
		}
		public void write(Value.Set expr) throws IOException {
			output.write_u8(CONSTANT_Set);
			output.write_u16(expr.values.size());
			for(Value v : expr.values) {
				write(v);
			}
		}
		
		public void write(Value.List expr) throws IOException {
			output.write_u8(CONSTANT_List);
			output.write_u16(expr.values.size());
			for(Value v : expr.values) {
				write(v);
			}
		}
		
		public void write(Value.Map expr) throws IOException {
			output.write_u8(CONSTANT_Map);
			output.write_u16(expr.values.size());
			for(java.util.Map.Entry<Value,Value> e : expr.values.entrySet()) {
				write(e.getKey());
				write(e.getValue());
			}
		}
		
		public void write(Value.Record expr) throws IOException {
			output.write_u8(CONSTANT_Record);
			output.write_u16(expr.values.size());
			for(java.util.Map.Entry<String,Value> v : expr.values.entrySet()) {
				output.write_u16(stringCache.get(v.getKey()));
				write(v.getValue());
			}
		}
		
		public void write(Value.Tuple expr) throws IOException {
			output.write_u8(CONSTANT_Tuple); // FIXME: should be TUPLE!!!
			output.write_u16(expr.values.size());
			for(Value v : expr.values) {
				write(v);
			}
		}
		
		public void write(Value.FunctionOrMethod expr) throws IOException {
			throw new RuntimeException("ValueWriter.write(Value.FunctionOrMethod) undefined");
//			Type.FunctionOrMethod t = expr.type();
//			if(t instanceof Type.Function) {
//				output.write_u1(FUNCTIONVAL);			
//			} else if(t instanceof Type.Method) {
//				output.write_u1(METHODVAL);
//			} else {
//				output.write_u1(MESSAGEVAL);
//			}
//			
//			WhileyType.write(t, writer, constantPool);
//			String value = expr.name.toString();
//			int valueLength = value.length();		
//			output.write_u2(valueLength);
//			for(int i=0;i!=valueLength;++i) {
//				output.write_u2(value.charAt(i));
//			}	
		}
	}

	// =========================================================================
	// BLOCK identifiers
	// =========================================================================
	
	public final static int BLOCK_Header = 0;
	public final static int BLOCK_Module = 1;
	public final static int BLOCK_Documentation = 2;
	public final static int BLOCK_License = 3;
	// ... (anticipating some others here)
	public final static int BLOCK_Type = 10;
	public final static int BLOCK_Constant = 11;
	public final static int BLOCK_Function = 12;
	public final static int BLOCK_Method = 13;
	public final static int BLOCK_Case = 14;
	// ... (anticipating some others here)
	public final static int BLOCK_Body = 20;
	public final static int BLOCK_Precondition = 21;
	public final static int BLOCK_Postcondition = 22;
	public final static int BLOCK_Constraint = 23;
	// ... (anticipating some others here)
	
	// =========================================================================
	// CONSTANT identifies
	// =========================================================================

	public final static int CONSTANT_Null = 0;
	public final static int CONSTANT_True = 1;
	public final static int CONSTANT_False = 2;
	public final static int CONSTANT_Byte = 3;
	public final static int CONSTANT_Char = 4;
	public final static int CONSTANT_Int = 5;
	public final static int CONSTANT_Real = 6;
	public final static int CONSTANT_Set = 7;	
	public final static int CONSTANT_String = 8;	
	public final static int CONSTANT_List = 9;
	public final static int CONSTANT_Record = 10;
	public final static int CONSTANT_Tuple = 11;
	public final static int CONSTANT_Map = 12;
	public final static int CONSTANT_Function = 13;
	public final static int CONSTANT_Method = 14;
}
