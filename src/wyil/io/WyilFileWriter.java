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

import wybs.lang.NameID;
import wycommon.util.Pair;
import wyfs.io.BinaryOutputStream;
import wyfs.lang.Path;
import wyil.lang.*;
import wyil.lang.SyntaxTree.Location;
import wyil.util.AbstractBytecode;
import wyautl.util.BigRational;

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

	private final ArrayList<String> stringPool = new ArrayList<String>();
	private final HashMap<String, Integer> stringCache = new HashMap<String, Integer>();
	private final ArrayList<PATH_Item> pathPool = new ArrayList<PATH_Item>();
	private final HashMap<Path.ID, Integer> pathCache = new HashMap<Path.ID, Integer>();
	private final ArrayList<NAME_Item> namePool = new ArrayList<NAME_Item>();
	private final HashMap<NameID, Integer> nameCache = new HashMap<NameID, Integer>();
	private final ArrayList<Constant> constantPool = new ArrayList<Constant>();
	private final HashMap<Constant, Integer> constantCache = new HashMap<Constant, Integer>();
	private final ArrayList<Type> typePool = new ArrayList<Type>();
	private final HashMap<Type, Integer> typeCache = new HashMap<Type, Integer>();

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
		writeBlock(BLOCK_Header, module, out);

		// fourth, write module block(s)
		writeBlock(BLOCK_Module, module, out);

		out.flush();
	}

	private void writeBlock(int kind, Object data, BinaryOutputStream output) throws IOException {

		// determine bytes for block
		byte[] bytes = null;
		switch (kind) {
		case BLOCK_Header:
			bytes = generateHeaderBlock((WyilFile) data);
			break;
		case BLOCK_Module:
			bytes = generateModuleBlock((WyilFile) data);
			break;
		case BLOCK_Type:
			bytes = generateTypeBlock((WyilFile.Type) data);
			break;
		case BLOCK_Constant:
			bytes = generateConstantBlock((WyilFile.Constant) data);
			break;
		case BLOCK_Function:
		case BLOCK_Method:
			bytes = generateFunctionOrMethodBlock((WyilFile.FunctionOrMethod) data);
			break;
		}

		output.pad_u8(); // pad out to next byte boundary
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
	private byte[] generateHeaderBlock(WyilFile module) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);

		// second, write the file version number
		output.write_uv(MAJOR_VERSION);
		output.write_uv(MINOR_VERSION);

		// third, write the various pool sizes
		output.write_uv(stringPool.size());
		output.write_uv(pathPool.size());
		output.write_uv(namePool.size());
		output.write_uv(typePool.size());
		output.write_uv(constantPool.size());

		// finally, write the number of remaining blocks
		output.write_uv(module.blocks().size());

		writeStringPool(output);
		writePathPool(output);
		writeNamePool(output);
		writeTypePool(output);
		writeConstantPool(output);

		output.close();

		return bytes.toByteArray();
	}

	/**
	 * Write the list of strings making up the string pool. Each entry is
	 * formated like so:
	 * 
	 * <pre>
	 * +-----------------+
	 * | uv : len        |
	 * +-----------------+
	 * | u8[len] : bytes |
	 * +-----------------+
	 * </pre>
	 * 
	 * The encoding for each string item is UTF8.
	 *
	 * @throws IOException
	 */
	private void writeStringPool(BinaryOutputStream output) throws IOException {
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

	/**
	 * Write the list of paths which constitute the path pool. Each entry is
	 * formated like so:
	 * 
	 * <pre>
	 * +-----------------+
	 * | uv : parent     |
	 * +-----------------+
	 * | uv : stringIdx  |
	 * +-----------------+
	 * </pre>
	 * 
	 * Each entry is a child of some parent entry, with index zero being
	 * automatically designated the "root". The <code>stringIdx</code>
	 * corresponds to an index in the string pool.
	 */
	private void writePathPool(BinaryOutputStream output) throws IOException {
		for (int i = 1; i < pathPool.size(); ++i) {
			PATH_Item p = pathPool.get(i);
			output.write_uv(p.parentIndex);
			output.write_uv(p.stringIndex);
		}
	}

	/**
	 * Read the list of names which constitute the name pool. Each entry is
	 * formated like so:
	 * 
	 * <pre>
	 * +-----------------+
	 * | uv : pathIdx    |
	 * +-----------------+
	 * | uv : stringIdx  |
	 * +-----------------+
	 * </pre>
	 * 
	 * Each entry consists of a path component and a name component, both of
	 * which index the path and string pools (respectively).
	 * 
	 * @param count
	 * @throws IOException
	 */
	private void writeNamePool(BinaryOutputStream output) throws IOException {
		for (NAME_Item p : namePool) {
			output.write_uv(p.pathIndex);
			output.write_uv(p.nameIndex);
		}
	}

	/**
	 * Write the list of constants which constitute the constant pool. Each entry
	 * is formated like so:
	 * 
	 * <pre>
	 * +-----------------+
	 * | uv : code       |
	 * +-----------------+
	 * | ... payload ... |
	 * +-----------------+
	 * </pre>
	 * 
	 * Here, the size of the payload is determined by the constant code. In some
	 * cases, there is no payload (e.g. for the constant NULL). In other case,
	 * there can be numerous bytes contained in the payload (e.g. for an Integer
	 * constant).
	 * 
	 * @param count
	 * @throws IOException
	 */
	private void writeConstantPool(BinaryOutputStream output) throws IOException {
		for (Constant val : constantPool) {
			if (val instanceof Constant.Null) {
				output.write_uv(CONSTANT_Null);
			} else if (val instanceof Constant.Bool) {
				Constant.Bool b = (Constant.Bool) val;
				output.write_uv(b.value() ? CONSTANT_True : CONSTANT_False);
			} else if (val instanceof Constant.Byte) {
				Constant.Byte b = (Constant.Byte) val;
				output.write_uv(CONSTANT_Byte);
				output.write_u8(b.value());
			} else if (val instanceof Constant.Integer) {
				Constant.Integer i = (Constant.Integer) val;
				BigInteger num = i.value();
				byte[] numbytes = num.toByteArray();
				output.write_uv(CONSTANT_Int);
				output.write_uv(numbytes.length);
				output.write(numbytes);
			} else if (val instanceof Constant.Array) {
				Constant.Array s = (Constant.Array) val;
				output.write_uv(CONSTANT_Array);
				output.write_uv(s.values().size());
				for (Constant v : s.values()) {
					int index = constantCache.get(v);
					output.write_uv(index);
				}
			} else if (val instanceof Constant.Record) {
				Constant.Record r = (Constant.Record) val;
				output.write_uv(CONSTANT_Record);
				output.write_uv(r.values().size());
				for (java.util.Map.Entry<String, Constant> v : r.values().entrySet()) {
					output.write_uv(stringCache.get(v.getKey()));
					int index = constantCache.get(v.getValue());
					output.write_uv(index);
				}
			} else if (val instanceof Constant.FunctionOrMethod) {
				Constant.FunctionOrMethod fm = (Constant.FunctionOrMethod) val;
				Type.FunctionOrMethod t = fm.type();
				output.write_uv(t instanceof Type.Function ? CONSTANT_Function : CONSTANT_Method);
				output.write_uv(typeCache.get(t));
				output.write_uv(nameCache.get(fm.name()));
			} else if (val instanceof Constant.Type) {
				Constant.Type ct = (Constant.Type) val;
				output.write_uv(CONSTANT_Type);
				output.write_uv(typeCache.get(ct.value()));				
			} else {
				throw new RuntimeException("Unknown value encountered - " + val);
			}
		}
	}

	private void writeTypePool(BinaryOutputStream output) throws IOException {
		Type.BinaryWriter bout = new Type.BinaryWriter(output);
		for (Type t : typePool) {
			bout.write(t);
		}
	}

	private byte[] generateModuleBlock(WyilFile module) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);

		output.write_uv(pathCache.get(module.getEntry().id()));
		output.write_uv(MODIFIER_Public); // for now
		output.write_uv(module.blocks().size());

		for (WyilFile.Block d : module.blocks()) {
			writeModuleBlock(d, output);
		}

		output.close();

		return bytes.toByteArray();
	}

	private void writeModuleBlock(WyilFile.Block d, BinaryOutputStream output) throws IOException {
		if (d instanceof WyilFile.Constant) {
			writeBlock(BLOCK_Constant, d, output);
		} else if (d instanceof WyilFile.Type) {
			writeBlock(BLOCK_Type, d, output);
		} else if (d instanceof WyilFile.FunctionOrMethod) {
			WyilFile.FunctionOrMethod md = (WyilFile.FunctionOrMethod) d;
			if (md.type() instanceof Type.Function) {
				writeBlock(BLOCK_Function, d, output);
			} else {
				writeBlock(BLOCK_Method, d, output);
			}
		}
	}

	/**
	 * Construct a BLOCK_Constant, that is a WyIL module block representing a
	 * constant declaration. The format is:
	 * 
	 * <pre>
	 * +-----------------+
	 * | uv : nameIdx    |
	 * +-----------------+
	 * | uv : Modifiers  |
	 * +-----------------+
	 * | uv : constIdx   |
	 * +-----------------+
	 * ~~~~~~~ u8 ~~~~~~~~
	 * </pre>
	 * 
	 * The <code>nameIdx</code> is an index into the <code>stringPool</code>
	 * representing the declaration's name, whilst <code>constIdx</code> is an
	 * index into the <code>constantPool</code> representing the constant value
	 * itself.
	 * 
	 * @throws IOException
	 */
	private byte[] generateConstantBlock(WyilFile.Constant cd) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);

		output.write_uv(stringCache.get(cd.name()));
		output.write_uv(generateModifiers(cd.modifiers()));
		output.write_uv(constantCache.get(cd.constant()));
		// TODO: write attributes

		output.close();
		return bytes.toByteArray();
	}

	/**
	 * Construct a BLOCK_Type, that is a WyIL module block representing a type
	 * declaration. The format is:
	 * 
	 * <pre>
	 * +------------------------+
	 * | uv : nameIdx           |
	 * +------------------------+
	 * | uv : Modifiers         |
	 * +------------------------+
	 * | uv : typeIdx           |
	 * +------------------------+
	 * | uv : nInvariants       |
	 * +------------------------+ 
	 * | uv[nInvariants]        |
	 * +------------------------+
	 * | SyntaxTree             |
	 * +------------------------+
	 * ~~~~~~~~~~ u8 ~~~~~~~~~~~~
	 * </pre>
	 * 
	 * The <code>nameIdx</code> is an index into the <code>stringPool</code>
	 * representing the declaration's name, whilst <code>typeIdx</code> is an
	 * index into the <code>typePool</code> representing the type itself.
	 * 
	 * @throws IOException
	 */
	private byte[] generateTypeBlock(WyilFile.Type td) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);
		//
		int nameIdx = stringCache.get(td.name());
		int modifiers = generateModifiers(td.modifiers());
		int typeIdx = typeCache.get(td.type());
		List<Location<Bytecode.Expr>> invariant = td.getInvariant();
		//
		output.write_uv(nameIdx);
		output.write_uv(modifiers);
		output.write_uv(typeIdx);
		output.write_uv(invariant.size());
		for(Location<?> clause : invariant) {
			output.write_uv(clause.getIndex());
		}
		writeSyntaxTree(td.getTree(),output);
		output.close();
		return bytes.toByteArray();
	}

	/**
	 * Construct a BLOCK_Function or BLOCK_Method, that is a WyIL module block
	 * representing a function or method declaration. The format is:
	 * 
	 * <pre>
	 * +------------------------+
	 * | uv : nameIdx           |
	 * +------------------------+
	 * | uv : Modifiers         |
	 * +------------------------+
	 * | uv : typeIdx           |
	 * +------------------------+
	 * | uv : nPreconditions    |
	 * +------------------------+
	 * | uv : nPostconditions   |
	 * +------------------------+
	 * | uv[nPreconditions]     |
	 * +------------------------+
	 * | uv[nPostconditions]    |
	 * +------------------------+
	 * | uv : body              |
	 * +------------------------+
	 * | SyntaTree              |
	 * +------------------------+
	 * ~~~~~~~~~~ u8 ~~~~~~~~~~~~
	 * </pre>
	 * 
	 * The <code>nameIdx</code> is an index into the <code>stringPool</code>
	 * representing the declaration's name, whilst <code>typeIdx</code> is an
	 * index into the <code>typePool</code> representing the function or method
	 * type itself. 
	 * 
	 * @throws IOException
	 */
	private byte[] generateFunctionOrMethodBlock(WyilFile.FunctionOrMethod md) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);
		//
		int nameIdx = stringCache.get(md.name());
		int modifiers = generateModifiers(md.modifiers());
		int typeIdx = typeCache.get(md.type());
		List<Location<Bytecode.Expr>> precondition = md.getPrecondition();
		List<Location<Bytecode.Expr>> postcondition = md.getPostcondition();		
		//
		output.write_uv(nameIdx);
		output.write_uv(modifiers);
		output.write_uv(typeIdx);
		output.write_uv(precondition.size());
		output.write_uv(postcondition.size());
		//
		for(Location<Bytecode.Expr> clause : precondition) {
			output.write_uv(clause.getIndex());
		}
		for(Location<Bytecode.Expr> clause : postcondition) {
			output.write_uv(clause.getIndex());
		}
		output.write_uv(md.getBody().getIndex());
		writeSyntaxTree(md.getTree(),output);		

		output.close();
		
		return bytes.toByteArray();
	}

	/**
	 * Write a syntax tree to the output stream. The format
	 * of a syntax tree is one of the following:
	 *
	 * <pre>
	 * +-------------------+
	 * | uv : nLocs        |
	 * +-------------------+ 
	 * | Locations[nLocs]  |
	 * +-------------------+
	 * </pre>
	 * 
	 * 
	 * 
	 * @param register
	 *            Register to be written out
	 * @param output
	 * @throws  
	 */
	private void writeSyntaxTree(SyntaxTree tree, BinaryOutputStream output) throws IOException {
		List<Location<?>> locations = tree.getLocations();
		output.write_uv(locations.size());
		for(int i=0;i!=locations.size();++i) {
			Location<?> location = locations.get(i);
			writeLocation(location,output);
		}		
	}
	
	/**
	 * Write details of a Location to the output stream. The format
	 * of a location is:
	 * 
	 * <pre>
	 * +-------------------+
	 * | uv : nTypes       |
	 * +-------------------+
	 * | uv[] : typeIdxs   |
	 * +-------------------+
	 * | uv : nAttrs       |
	 * +-------------------+
	 * | Bytecode          |
	 * +-------------------+ 
	 * | Attribute[nAttrs] |
	 * +-------------------+
	 * </pre>
	 * 
	 */
	private void writeLocation(SyntaxTree.Location<?> location, BinaryOutputStream output) throws IOException {
		output.write_uv(location.numberOfTypes());
		for(int i=0;i!=location.numberOfTypes();++i) {
			output.write_uv(typeCache.get(location.getType(i)));
		}
		output.write_uv(0); // no attributes for now
		writeBytecode(location.getBytecode(), output);
	}

	/**
	 * <p>
	 * Write out a given bytecode whose format is currently given as follows:
	 * </p>
	 * 
	 * <pre>
	 * +-------------------+
	 * | u8 : opcode       |
	 * +-------------------+
	 * | uv : nAttrs       |	 
	 * +-------------------+
	 * | Attribute[nAttrs] | 
	 * +-------------------+
	 *        ...
	 * 
	 * </pre>
	 * 
	 * <p>
	 * <b>NOTE:</b> The intention is to support a range of different bytecode
	 * formats in order to optimise the common cases. For example, when there
	 * are no targets, no operands, no types, etc. Furthermore, when the size of
	 * items can be reduced from uv to u4, etc.
	 * </p>
	 */
	private void writeBytecode(Bytecode code, BinaryOutputStream output)
			throws IOException {
		output.write_u8(code.getOpcode());
		output.write_uv(0); // attributes
		writeOperands(code,output);
		writeOperandGroups(code,output);
		if (code instanceof Bytecode.Stmt) {
			writeBlocks((Bytecode.Stmt) code, output);
		}
		writeExtras(code, output);
	}

	private void writeOperands(Bytecode code, BinaryOutputStream output) throws IOException {
		Bytecode.Schema schema = AbstractBytecode.schemas[code.getOpcode()];
		switch(schema.getOperands()) {
		case ZERO:
			// do nout
			break;
		case ONE:
			output.write_uv(code.getOperand(0));
			break;
		case TWO:
			output.write_uv(code.getOperand(0));
			output.write_uv(code.getOperand(1));
			break;
		case MANY:
			writeUnboundArray(code.getOperands(),output);
		}		
	}
	
	private void writeOperandGroups(Bytecode code, BinaryOutputStream output) throws IOException {		
		Bytecode.Schema schema = AbstractBytecode.schemas[code.getOpcode()];
		switch(schema.getOperandGroups()) {
		case ZERO:
			// do nout
			break;
		case ONE:
			writeUnboundArray(code.getOperandGroup(0),output);
			break;
		case TWO:			
			writeUnboundArray(code.getOperandGroup(0),output);
			writeUnboundArray(code.getOperandGroup(1),output);
			break;
		case MANY:
			output.write_uv(code.numberOfOperandGroups());
			for(int i=0;i!=code.numberOfOperandGroups();++i) {
				writeUnboundArray(code.getOperandGroup(i),output);
			}			
		}		
	}
	
	private void writeBlocks(Bytecode.Stmt code, BinaryOutputStream output) throws IOException {
		Bytecode.Schema schema = AbstractBytecode.schemas[code.getOpcode()];
		switch(schema.getBlocks()) {
		case ZERO:
			// do nout
			break;
		case ONE:
			output.write_uv(code.getBlock(0));
			break;
		case TWO:
			output.write_uv(code.getBlock(0));
			output.write_uv(code.getBlock(1));
			break;
		case MANY:
			writeUnboundArray(code.getBlocks(),output);
		}		
	}
	private void writeUnboundArray(int[] values, BinaryOutputStream output) throws IOException {
		output.write_uv(values.length);	
		// Write operand locations
		for (int i = 0; i != values.length; ++i) {
			output.write_uv(values[i]);
		}	
	}
	
	/**
	 * Write the "rest" of a bytecode instruction. This includes additional
	 * information as specified for the given opcode. For compound bytecodes,
	 * this includes the block identifier(s) of the nested block(s) in question.
	 * For branching bytecodes, it will include the branch destination as a
	 * relative offset. Other bytecodes include indices which identify constants
	 * in one of the pools.
	 *
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
	 * @param output
	 *            --- The binary output stream to which this bytecode is being
	 *            written.
	 * @throws IOException
	 */
	private void writeExtras(Bytecode code, BinaryOutputStream output) throws IOException {
		//
		switch (code.getOpcode()) {		
		case Bytecode.OPCODE_const: {
			Bytecode.Const c = (Bytecode.Const) code;
			output.write_uv(constantCache.get(c.constant()));
			break;
		}
		case Bytecode.OPCODE_fieldload: {
			Bytecode.FieldLoad c = (Bytecode.FieldLoad) code;
			output.write_uv(stringCache.get(c.fieldName()));
			break;
		}
		case Bytecode.OPCODE_indirectinvoke: {
			Bytecode.IndirectInvoke c = (Bytecode.IndirectInvoke) code;
			output.write_uv(typeCache.get(c.type()));
			break;
		}
		case Bytecode.OPCODE_namedblock: {
			Bytecode.NamedBlock c = (Bytecode.NamedBlock) code;
			output.write_uv(stringCache.get(c.getName()));
			break;
		}
		case Bytecode.OPCODE_invoke: {
			Bytecode.Invoke c = (Bytecode.Invoke) code;
			output.write_uv(typeCache.get(c.type()));
			output.write_uv(nameCache.get(c.name()));
			break;
		}
		case Bytecode.OPCODE_lambda: {
			Bytecode.Lambda c = (Bytecode.Lambda) code;
			output.write_uv(typeCache.get(c.type()));
			break;
		}		
		case Bytecode.OPCODE_switch: {
			Bytecode.Switch c = (Bytecode.Switch) code;
			Bytecode.Case[] cases = c.cases();
			output.write_uv(cases.length);
			for (int i = 0; i != cases.length; ++i) {
				Bytecode.Case cAse = cases[i];
				Constant[] values = cAse.values();
				output.write_uv(cAse.block());
				output.write_uv(values.length);
				for (int j = 0; j != values.length; ++j) {
					output.write_uv(constantCache.get(values[j]));
				}
			}
			break;
		}
		case Bytecode.OPCODE_vardecl:
		case Bytecode.OPCODE_vardeclinit: {
			Bytecode.VariableDeclaration d = (Bytecode.VariableDeclaration) code;
			output.write_uv(stringCache.get(d.getName()));
			break;
		}
		}
	}

	private int generateModifiers(Collection<Modifier> modifiers) {
		int mods = 0;
		for (Modifier m : modifiers) {
			if (m == Modifier.PUBLIC) {
				mods |= MODIFIER_Public;
			} else if (m == Modifier.PRIVATE) {
				mods |= MODIFIER_Private;
			} else if (m == Modifier.NATIVE) {
				mods |= MODIFIER_Native;
			} else if (m == Modifier.EXPORT) {
				mods |= MODIFIER_Export;
			}
		}
		return mods;
	}
	
	private void buildPools(WyilFile module) {
		stringPool.clear();
		stringCache.clear();

		pathPool.clear();
		pathCache.clear();
		// preload the path root
		pathPool.add(null);
		pathCache.put(wyfs.util.Trie.ROOT, 0);

		namePool.clear();
		nameCache.clear();

		constantPool.clear();
		constantCache.clear();

		typePool.clear();
		typeCache.clear();

		addPathItem(module.getEntry().id());
		for (WyilFile.Block d : module.blocks()) {
			buildPools(d);
		}
	}

	private void buildPools(WyilFile.Block declaration) {
		if (declaration instanceof WyilFile.Type) {
			buildPools((WyilFile.Type) declaration);
		} else if (declaration instanceof WyilFile.Constant) {
			buildPools((WyilFile.Constant) declaration);
		} else if (declaration instanceof WyilFile.FunctionOrMethod) {
			buildPools((WyilFile.FunctionOrMethod) declaration);
		}
	}

	private void buildPools(WyilFile.Constant declaration) {
		addStringItem(declaration.name());
		addConstantItem(declaration.constant());
	}

	private void buildPools(WyilFile.Type declaration) {
		addStringItem(declaration.name());
		addTypeItem(declaration.type());
		buildPools(declaration.getTree());
	}

	private void buildPools(WyilFile.FunctionOrMethod declaration) {
		addStringItem(declaration.name());
		addTypeItem(declaration.type());
		buildPools(declaration.getTree());
	}

	private void buildPools(SyntaxTree tree) {
		for(Location<?> e : tree.getLocations()) {
			buildPools(e);
		}
	}
	
	private void buildPools(SyntaxTree.Location<?> loc) {
		for(int i=0;i!=loc.numberOfTypes();++i) {
			addTypeItem(loc.getType(i));
		}
		buildPools(loc.getBytecode());
	}
	
	private void buildPools(Bytecode code) {
		if (code instanceof Bytecode.Const) {
			Bytecode.Const c = (Bytecode.Const) code;
			addConstantItem(c.constant());
		} else if (code instanceof Bytecode.FieldLoad) {
			Bytecode.FieldLoad c = (Bytecode.FieldLoad) code;
			addStringItem(c.fieldName());
		} else if (code instanceof Bytecode.Invoke) {
			Bytecode.Invoke c = (Bytecode.Invoke) code;
			addNameItem(c.name());
			addTypeItem(c.type());
		} else if(code instanceof Bytecode.NamedBlock) {
			Bytecode.NamedBlock nb = (Bytecode.NamedBlock) code;
			addStringItem(nb.getName());
		} else if (code instanceof Bytecode.Switch) {
			Bytecode.Switch s = (Bytecode.Switch) code;
			for (Bytecode.Case cs : s.cases()) {
				for (Constant c : cs.values()) {
					addConstantItem(c);
				}
			}
		} else if (code instanceof Bytecode.IndirectInvoke) {
			Bytecode.IndirectInvoke b = (Bytecode.IndirectInvoke) code;
			addTypeItem(b.type());
		} else if (code instanceof Bytecode.Invoke) {
			Bytecode.Invoke b = (Bytecode.Invoke) code;
			addTypeItem(b.type());
		} else if (code instanceof Bytecode.VariableDeclaration) {
			Bytecode.VariableDeclaration vd = (Bytecode.VariableDeclaration) code;
			addStringItem(vd.getName());
		} 
	}

	private int addNameItem(NameID name) {
		Integer index = nameCache.get(name);
		if (index == null) {
			int i = namePool.size();
			nameCache.put(name, i);
			namePool.add(new NAME_Item(addPathItem(name.module()), addStringItem(name.name())));
			return i;
		} else {
			return index;
		}
	}

	private int addStringItem(String string) {
		Integer index = stringCache.get(string);
		if (index == null) {
			int i = stringPool.size();
			stringCache.put(string, i);
			stringPool.add(string);
			return i;
		} else {
			return index;
		}
	}

	private int addPathItem(Path.ID pid) {
		Integer index = pathCache.get(pid);
		if (index == null) {
			int parent = addPathItem(pid.parent());
			int i = pathPool.size();
			pathPool.add(new PATH_Item(parent, addStringItem(pid.last())));
			pathCache.put(pid, i);
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
		if (index == null) {
			int i = typePool.size();
			typeCache.put(t, i);
			typePool.add(t);
			return i;
		} else {
			return index;
		}
	}

	private int addConstantItem(Constant v) {

		Integer index = constantCache.get(v);
		if (index == null) {
			// All subitems must have lower indices than the containing item.
			// So, we must add subitems first before attempting to allocate a
			// place for this value.
			addConstantSubitems(v);

			// finally allocate space for this constant.
			int i = constantPool.size();
			constantCache.put(v, i);
			constantPool.add(v);
			return i;
		}
		return index;
	}

	private void addConstantSubitems(Constant v) {
		if (v instanceof Constant.Array) {
			Constant.Array l = (Constant.Array) v;
			for (Constant e : l.values()) {
				addConstantItem(e);
			}
		} else if (v instanceof Constant.Record) {
			Constant.Record r = (Constant.Record) v;
			for (Map.Entry<String, Constant> e : r.values().entrySet()) {
				addStringItem(e.getKey());
				addConstantItem(e.getValue());
			}
		} else if (v instanceof Constant.FunctionOrMethod) {
			Constant.FunctionOrMethod fm = (Constant.FunctionOrMethod) v;
			addTypeItem(fm.type());
			addNameItem(fm.name());
		} else if (v instanceof Constant.Type) {
			Constant.Type ct = (Constant.Type) v;
			addTypeItem(ct.value());
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
		PACKAGE(0), MODULE(1), CONSTANT(2), TYPE(3), FUNCTION(4), METHOD(5);

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

		public NAME_Item(/* NAME_Kind kind, */int pathIndex, int nameIndex) {
			// this.kind = kind;
			this.pathIndex = pathIndex;
			this.nameIndex = nameIndex;
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
	// ... (anticipating some others here)

	// =========================================================================
	// CONSTANT identifiers
	// =========================================================================

	public final static int CONSTANT_Null = 0;
	public final static int CONSTANT_True = 1;
	public final static int CONSTANT_False = 2;
	public final static int CONSTANT_Byte = 3;
	public final static int CONSTANT_Int = 5;
	// public final static int CONSTANT_Real = 6;
	// public final static int CONSTANT_Set = 7;
	public final static int CONSTANT_Array = 9;
	public final static int CONSTANT_Record = 10;
	// public final static int CONSTANT_Tuple = 11;
	public final static int CONSTANT_Type = 12;
	public final static int CONSTANT_Function = 13;
	public final static int CONSTANT_Method = 14;

	// =========================================================================
	// MODIFIER identifiers
	// =========================================================================

	public final static int MODIFIER_PROTECTION_MASK = 3;
	public final static int MODIFIER_Private = 0;
	public final static int MODIFIER_Public = 1;
	// public final static int MODIFIER_Protected = 2; // for later
	// public final static int MODIFIER_Package = 3;
	// public final static int MODIFIER_Module = 4;

	public final static int MODIFIER_MANGLE_MASK = 3 << 4;
	public final static int MODIFIER_Native = 1 << 4;
	public final static int MODIFIER_Export = 2 << 4;
	// public final static int MODIFIER_Total = 3 << 4;
}
