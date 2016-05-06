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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.util.Pair;
import wycc.util.Triple;
import wyfs.io.BinaryInputStream;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.*;
import wyil.lang.Bytecode.Schema;
import wyil.lang.Bytecode.Extras;
import wyil.lang.Bytecode.Operands;
import wyil.lang.Bytecode.Types;

/**
 * Read a binary WYIL file from a byte stream and convert into the corresponding
 * WyilFile object.
 *
 * @author David J. Pearce
 *
 */
public final class WyilFileReader {
	private static final char[] magic = { 'W', 'Y', 'I', 'L', 'F', 'I', 'L', 'E' };

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

		for (int i = 0; i != 8; ++i) {
			char c = (char) input.read_u8();
			if (magic[i] != c) {
				throw new IllegalArgumentException("invalid magic number");
			}
		}

		// head blocker
		int kind = input.read_uv();
		int size = input.read_uv();
		input.pad_u8();

		if (kind != WyilFileWriter.BLOCK_Header) {
			throw new IllegalArgumentException("header block must come first");
		}

		int majorVersion = input.read_uv();
		int minorVersion = input.read_uv();

		int stringPoolCount = input.read_uv();
		int pathPoolCount = input.read_uv();
		int namePoolCount = input.read_uv();
		int typePoolCount = input.read_uv();
		int constantPoolCount = input.read_uv();

		int numBlocks = input.read_uv();

		readStringPool(stringPoolCount);
		readPathPool(pathPoolCount);
		readNamePool(namePoolCount);
		readTypePool(typePoolCount);
		readConstantPool(constantPoolCount);

		input.pad_u8();

		return readModule();
	}

	/**
	 * Read the list of strings which constitute the string pool. Each entry is
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
	 * The encoding for each string item is UTF-8.
	 * 
	 * @param count
	 * @throws IOException
	 */
	private void readStringPool(int count) throws IOException {
		final String[] myStringPool = new String[count];

		for (int i = 0; i != count; ++i) {
			int length = input.read_uv();
			try {
				byte[] data = new byte[length];
				input.read(data);
				String str = new String(data, 0, length, "UTF-8");
				myStringPool[i] = str;
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("UTF-8 Charset not supported?");
			}
		}
		stringPool = myStringPool;
	}

	/**
	 * Read the list of paths which constitute the path pool. Each entry is
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
	 * 
	 * @param count
	 * @throws IOException
	 */
	private void readPathPool(int count) throws IOException {
		final Path.ID[] myPathPool = new Path.ID[count];
		myPathPool[0] = Trie.ROOT;

		for (int i = 1; i != count; ++i) {
			int parent = input.read_uv();
			int stringIndex = input.read_uv();
			Path.ID id;
			id = myPathPool[parent];
			id = id.append(stringPool[stringIndex]);
			myPathPool[i] = id;
		}
		pathPool = myPathPool;
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
	private void readNamePool(int count) throws IOException {
		final NameID[] myNamePool = new NameID[count];

		for (int i = 0; i != count; ++i) {
			// int kind = input.read_uv();
			int pathIndex = input.read_uv();
			int nameIndex = input.read_uv();
			Path.ID id = pathPool[pathIndex];
			String name = stringPool[nameIndex];
			myNamePool[i] = new NameID(id, name);
		}

		namePool = myNamePool;
	}

	/**
	 * Read the list of constants which constitute the constant pool. Each entry
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
	private void readConstantPool(int count) throws IOException {
		final Constant[] myConstantPool = new Constant[count];

		for (int i = 0; i != count; ++i) {
			int code = input.read_uv();
			Constant constant;

			switch (code) {
			case WyilFileWriter.CONSTANT_Null:
				constant = Constant.Null;
				break;
			case WyilFileWriter.CONSTANT_False:
				constant = Constant.False;
				break;
			case WyilFileWriter.CONSTANT_True:
				constant = Constant.True;
				break;
			case WyilFileWriter.CONSTANT_Byte: {
				byte val = (byte) input.read_u8();
				constant = new Constant.Byte(val);
				break;
			}
			case WyilFileWriter.CONSTANT_Int: {
				int len = input.read_uv();
				byte[] bytes = new byte[len];
				input.read(bytes);
				BigInteger bi = new BigInteger(bytes);
				constant = new Constant.Integer(bi);
				break;
			}
			case WyilFileWriter.CONSTANT_Array: {
				int len = input.read_uv();
				ArrayList<Constant> values = new ArrayList<Constant>();
				for (int j = 0; j != len; ++j) {
					int index = input.read_uv();
					values.add(myConstantPool[index]);
				}
				constant = new Constant.Array(values);
				break;
			}
			case WyilFileWriter.CONSTANT_Record: {
				int len = input.read_uv();
				HashMap<String, Constant> tvs = new HashMap<String, Constant>();
				for (int j = 0; j != len; ++j) {
					int fieldIndex = input.read_uv();
					int constantIndex = input.read_uv();
					String str = stringPool[fieldIndex];
					tvs.put(str, myConstantPool[constantIndex]);
				}
				constant = new Constant.Record(tvs);
				break;
			}
			case WyilFileWriter.CONSTANT_Function:
			case WyilFileWriter.CONSTANT_Method: {
				int typeIndex = input.read_uv();
				int nameIndex = input.read_uv();
				Type.FunctionOrMethod t = (Type.FunctionOrMethod) typePool[typeIndex];
				NameID name = namePool[nameIndex];
				constant = new Constant.Lambda(name, t);
				break;
			}
			case WyilFileWriter.CONSTANT_Type: {
				int typeIndex = input.read_uv();
				constant = new Constant.Type(typePool[typeIndex]);
				break;
			}
			default:
				throw new RuntimeException("Unknown constant encountered in WhileyDefine: " + code);
			}
			myConstantPool[i] = constant;
		}

		constantPool = myConstantPool;
	}

	/**
	 * <p>
	 * Read the list of types which constitute the type pool. Each entry is
	 * currently formated using the binary representation of automata.
	 * </p>
	 * 
	 * <b>NOTE:</b> Eventually, automata need to be properly integrated with the
	 * WyIL file format to avoid duplication.
	 * 
	 * @param count
	 * @throws IOException
	 */
	private void readTypePool(int count) throws IOException {
		final Type[] myTypePool = new Type[count];
		Type.BinaryReader bin = new Type.BinaryReader(input);
		for (int i = 0; i != count; ++i) {
			Type t = bin.readType();
			myTypePool[i] = t;
		}

		typePool = myTypePool;
	}

	/**
	 * Read a module contained within a given WyIL file. The format is:
	 * 
	 * <pre>
	 * +-----------------+
	 * | uv : kind       |
	 * +-----------------+
	 * | uv : size       |
	 * +-----------------+
	 * ~~~~~~~~ u8 ~~~~~~~
	 * +-----------------+
	 * | uv : pathIDX    |
	 * +-----------------+
	 * | uv : nModifiers |
	 * +-----------------+
	 * | uv : nBlocks    |
	 * +-----------------+
	 * ~~~~~~~~ u8 ~~~~~~~
	 * +-----------------+
	 * | Block[nBlocks]  |
	 * +-----------------+
	 * </pre>
	 * 
	 * Here, the <code>pathIDX</code> gives the path identifiers for the module
	 * in question.
	 * 
	 * @throws IOException
	 */
	private WyilFile readModule() throws IOException {
		int kind = input.read_uv(); // block identifier
		int size = input.read_uv();
		input.pad_u8();

		int pathIdx = input.read_uv();
		int numModifiers = input.read_uv(); // unused
		int numBlocks = input.read_uv();

		input.pad_u8();

		List<WyilFile.Block> declarations = new ArrayList<WyilFile.Block>();
		for (int i = 0; i != numBlocks; ++i) {
			declarations.add(readModuleBlock());
		}

		return new WyilFile(pathPool[pathIdx], "unknown.whiley", declarations);
	}

	
	private WyilFile.Block readModuleBlock() throws IOException {
		WyilFile.Block block;
		int kind = input.read_uv();
		int size = input.read_uv();
		input.pad_u8();

		switch (kind) {
		case WyilFileWriter.BLOCK_Constant:
			block = readConstantBlock();
			break;
		case WyilFileWriter.BLOCK_Type:
			block = readTypeBlock();
			 break;
		case WyilFileWriter.BLOCK_Function:
		case WyilFileWriter.BLOCK_Method:
			block = readFunctionOrMethodBlock();
			break;
		default:
			throw new RuntimeException("unknown module block encountered (" + kind + ")");
		}

		input.pad_u8();

		return block;
	}

	/**
	 * Read a BLOCK_Constant, that is a WyIL module block representing a
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
	private WyilFile.Constant readConstantBlock() throws IOException {
		int nameIdx = input.read_uv();
		int modifiers = input.read_uv();
		int constantIdx = input.read_uv();

		return new WyilFile.Constant(generateModifiers(modifiers), stringPool[nameIdx], constantPool[constantIdx]);
	}

	/**
	 * Read a BLOCK_Type, that is a WyIL module block representing a type
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
	 * ~~~~~~~~~~ u8 ~~~~~~~~~~~~
	 * +------------------------+
	 * | CodeForest : invariant |
	 * +------------------------+
	 * </pre>
	 * 
	 * The <code>nameIdx</code> is an index into the <code>stringPool</code>
	 * representing the declaration's name, whilst <code>typeIdx</code> is an
	 * index into the <code>typePool</code> representing the type itself.
	 * Finally, <code>invariant</code> gives the type's invariant as zero or
	 * more bytecode blocks.
	 * 
	 * @throws IOException
	 */
	private WyilFile.Type readTypeBlock() throws IOException {
		int nameIdx = input.read_uv();
		int modifiers = input.read_uv();
		int typeIdx = input.read_uv();

		BytecodeForest forest = readCodeForestBlock();
		
		return new WyilFile.Type(generateModifiers(modifiers), stringPool[nameIdx], typePool[typeIdx], forest);
	}

	/**
	 * Read a BLOCK_Function or BLOCK_Method, that is a WyIL module block
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
	 * | uv : nRequires         |
	 * +------------------------+
	 * | uv : nEnsures          |
	 * +------------------------+
	 * ~~~~~~~~~~ u8 ~~~~~~~~~~~~
	 * +------------------------+
	 * | CodeForest : code      |
	 * +------------------------+
	 * </pre>
	 * 
	 * The <code>nameIdx</code> is an index into the <code>stringPool</code>
	 * representing the declaration's name, whilst <code>typeIdx</code> is an
	 * index into the <code>typePool</code> representing the function or method
	 * type itself. Finally, <code>code</code> provides all code associated with
	 * the function or method which includes any preconditions, postconditions
	 * and the body itself. Here, <code>nRequires</code> identifiers the number
	 * of roots which correspond to the precondition, whilst
	 * <code>nEnsures</code> the number of roots corresponding to the
	 * postcondition. Any root after this comprise the body.
	 * 
	 * @throws IOException
	 */
	private WyilFile.FunctionOrMethod readFunctionOrMethodBlock() throws IOException {
		int nameIdx = input.read_uv();
		int modifiers = input.read_uv();
		int typeIdx = input.read_uv();
		int nRequires = input.read_uv();
		int nEnsures = input.read_uv();

		input.pad_u8();

		Type.FunctionOrMethod type = (Type.FunctionOrMethod) typePool[typeIdx];

		BytecodeForest forest = readCodeForestBlock();

		return new WyilFile.FunctionOrMethod(generateModifiers(modifiers), stringPool[nameIdx], type, forest,
				nRequires, nEnsures);
	}
	
	/**
	 * Convert an bit pattern representing various modifiers into instances of
	 * <code>Modifier</code>.
	 * 
	 * @param modifiers
	 * @return
	 */
	private Collection<Modifier> generateModifiers(int modifiers) {
		ArrayList<Modifier> mods = new ArrayList<Modifier>();

		// first, protection modifiers
		switch (modifiers & WyilFileWriter.MODIFIER_PROTECTION_MASK) {
		case WyilFileWriter.MODIFIER_Public:
			mods.add(Modifier.PUBLIC);
			break;
		case WyilFileWriter.MODIFIER_Private:
			mods.add(Modifier.PRIVATE);
			break;
		default:
			throw new RuntimeException("Unknown modifier");
		}

		// second, mangle modifiers
		switch (modifiers & WyilFileWriter.MODIFIER_MANGLE_MASK) {
		case WyilFileWriter.MODIFIER_Native:
			mods.add(Modifier.NATIVE);
			break;
		case WyilFileWriter.MODIFIER_Export:
			mods.add(Modifier.EXPORT);
			break;
		}

		return mods;
	}
	
	/**
	 * <p>
	 * Read a code forest from the input stream. The format is:
	 * </p>
	 * 
	 * <pre>
	 * +--------------------+
	 * | uv: nRegs          |
	 * +--------------------+
	 * | uv: nBlocks        |
	 * +--------------------+
	 * | uv: nRoots         |
	 * +--------------------+
	 * | uv: nAttrs         |
	 * +--------------------+
	 * | Register[nRegs]    |
	 * +--------------------+
	 * | uv[nRoots]         |
	 * +--------------------+
	 * | CodeBlock[nBlocks] |
	 * +--------------------+
	 * | Attribute[nAttrs]  |
	 * +--------------------+
	 * </pre>
	 * 
	 * @param forest
	 *            The forest being written to the stream
	 * @param output
	 * @throws IOException
	 */
	private BytecodeForest readCodeForestBlock() throws IOException {
		input.pad_u8();
		int kind = input.read_uv(); // unused
		int size = input.read_uv(); // unused
		input.pad_u8();
		
		int nRegs = input.read_uv();
		int nBlocks = input.read_uv();
		int nRoots = input.read_uv();
		int nAttrs = input.read_uv();
		
		BytecodeForest forest = new BytecodeForest();
		
		for(int i=0;i!=nRegs;++i) {			
			BytecodeForest.Location location = readCodeLocation();
			forest.locations().add(location);
		}
		
		for(int i=0;i!=nRoots;++i) {
			int root = input.read_uv();
			forest.addRoot(root);
		}
		
		for(int i=0;i!=nBlocks;++i) {
			forest.add(readCodeBlock());
		}
		
		return forest;
	}
	
	/**
	 * Read a given code location from the input stream. The format
	 * of reach location entry is:
	 * 
	 * <pre>
	 * +-------------------+
	 * | u1 : kind         |
	 * +-------------------+
	 * | uv : nAttrs       |
	 * +-------------------+
	 * | uv : typeIdx      |
	 * +-------------------+
	 * | Attribute[nAttrs] |
	 * +-------------------+
	 * </pre>
	 * 
	 * @param output
	 * @throws IOException
	 */
	private BytecodeForest.Location readCodeLocation() throws IOException {
		boolean kind = input.read_bit();
		boolean single = input.read_bit();
		int nAttrs = input.read_uv();
		Type[] types;
		if(single) {
			int typeIdx = input.read_uv();
			types = new Type[]{typePool[typeIdx]};
		} else {
			int size = input.read_uv();
			types = new Type[size];
			for(int i=0;i!=size;++i) {
				types[i] = typePool[input.read_uv()];
			}
		}
				
		// TODO: read any attributes given
		List<Attribute> attributes = Collections.EMPTY_LIST;
		if(kind) {
			int stringIdx = input.read_uv();
			return new BytecodeForest.Variable(types[0], stringPool[stringIdx], attributes);			
		} else {
			Bytecode.Expr value = (Bytecode.Expr) readBytecode();
			return new BytecodeForest.Operand(types, value, attributes);
		}
	}
	
	/**
	 * <p>
	 * Read a block of bytecodes whilst adding newly discovered labels to a
	 * given set of label offsets. The format is:
	 * </p>
	 * 
	 * <pre>
	 * +-------------------+
	 * | uv : nCodes       |
	 * +-------------------+
	 * | uv : nAttrs       |
	 * +-------------------+
	 * | Bytecode[nCodes]  |
	 * +-------------------+
	 * | Attribute[nAttrs] |
	 * +-------------------+
	 * </pre>
	 * 
	 * <p>
	 * The block is associated with a given offset value, which indicates the
	 * offset of the first bytecode in the block to be used when calculating
	 * branch offsets.
	 * </p>
	 * 
	 * @param offset
	 * @param labels
	 * @return
	 * @throws IOException
	 */
	public BytecodeForest.Block readCodeBlock()
			throws IOException {
		int nCodes = input.read_uv();
		int nAttrs = input.read_uv();

		ArrayList<BytecodeForest.Entry> bytecodes = new ArrayList<BytecodeForest.Entry>();
		for (int i = 0; i < nCodes; ++i) {
			Bytecode.Stmt code = (Bytecode.Stmt) readBytecode();
			bytecodes.add(new BytecodeForest.Entry(code));
		}
		// TODO: read any attributes given
		return new BytecodeForest.Block(bytecodes);
	}

	private Bytecode readBytecode() throws IOException {
		int opcode = input.read_u8();
		Bytecode.Schema schema = schemas[opcode];
		
		// First, read and validate all operands and types
		int nOperands = input.read_uv();
		int nAttrs = input.read_uv();
		int[] operands = readRegisters(nOperands);
		// Second, read all extras		
		Object[] extras = readExtras(schema);
		
		// Finally, create the bytecode
		return schema.construct(opcode,operands,extras);		
	}
	
	/**
	 * Read the list of extra components defined by a given bytecode schema.
	 * Each extra is interpreted in a slightly different fashion.
	 * 
	 * @param schema
	 * @param labels
	 * @return
	 * @throws IOException
	 */
	private Object[] readExtras(Bytecode.Schema schema)
			throws IOException {
		Extras[] extras = schema.extras();
		Object[] results = new Object[extras.length];
		for(int i=0;i!=extras.length;++i) {
			switch(extras[i]) {
			case CONSTANT: {
				int constIdx = input.read_uv();
				results[i] = constantPool[constIdx];
				break;
			}				
			case STRING: {
				int stringIdx = input.read_uv();
				results[i] = stringPool[stringIdx];
				break;
			}			
			case NAME: {
				int nameIdx = input.read_uv();
				results[i] = namePool[nameIdx];
				break;
			}		
			case TYPE: {
				int typeIdx = input.read_uv();
				results[i] = typePool[typeIdx];
				break;
			}
			case BLOCK_ARRAY: {
				int nBlocks = input.read_uv();
				int[] blocks = new int[nBlocks];
				for(int j=0;j!=nBlocks;++j) {
					blocks[j] = input.read_uv();
				}
				results[i] = blocks;
				break;
			}
			case STRING_ARRAY: {
				int nStrings = input.read_uv();
				String[] strings = new String[nStrings];
				for(int j=0;j!=nStrings;++j) {
					int stringIdx = input.read_uv();
					strings[j] = stringPool[stringIdx];
				}
				results[i] = strings;
				break;
			}			
			case SWITCH_ARRAY: {
				// This is basically a special case just for the switch
				// statement.
				int nPairs = input.read_uv();
				Bytecode.Case[] pairs = new Bytecode.Case[nPairs];
				for(int j=0;j!=nPairs;++j) {
					int block = input.read_uv();
					int nConstants = input.read_uv();
					Constant[] constants = new Constant[nConstants];
					for(int k=0;k!=nConstants;++k) {
						int constIdx = input.read_uv();
						constants[k] = constantPool[constIdx];
					}					
					pairs[j] = new Bytecode.Case(block,constants);
				}
				results[i] = pairs;
				break;
			}
			default:
				throw new RuntimeException("unknown bytecode extra encountered: " + extras[i]);
			}
		}
		return results;
	}
	
	private int[] readRegisters(int nRegisters) throws IOException {
		int[] bs = new int[nRegisters];
		for (int i = 0; i != nRegisters; ++i) {
			bs[i] = input.read_uv();
		}
		return bs;
	}
	
	private Type[] readTypes(int nTypes) throws IOException {
		Type[] types = new Type[nTypes];
		for (int i = 0; i != nTypes; ++i) {
			int typeIndex = input.read_uv();
			types[i] = typePool[typeIndex];
		}
		return types;
	}
	
	/**
	 * ==================================================================
	 * Individual Bytecode Schemas
	 * ==================================================================
	 */

	private static final Schema[] schemas = new Schema[255];

	static {
		//		
		schemas[Bytecode.OPCODE_break] = new Schema(Operands.ZERO, Extras.BLOCK_ARRAY){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				int[] blocks = (int[]) extras[0];
				return new Bytecode.Break(blocks[0]);
			}
		};
		schemas[Bytecode.OPCODE_continue] = new Schema(Operands.ZERO, Extras.BLOCK_ARRAY){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				int[] blocks = (int[]) extras[0];
				return new Bytecode.Continue(blocks[0]);
			}
		};
		schemas[Bytecode.OPCODE_fail] = new Schema(Operands.ZERO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Fail();
			}
		};
		schemas[Bytecode.OPCODE_assert] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Assert(operands[0]);
			}
		};
		schemas[Bytecode.OPCODE_assume] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Assume(operands[0]);
			}
		};		

		// =========================================================================
		// Unary Operators.
		// =========================================================================
		schemas[Bytecode.OPCODE_debug] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Debug(operands[0]);
			}
		};
		schemas[Bytecode.OPCODE_return] = new Schema(Operands.MANY){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Return(operands);
			}
		};
		schemas[Bytecode.OPCODE_switch] = new Schema(Operands.ONE, Extras.SWITCH_ARRAY){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				Bytecode.Case[] cases = (Bytecode.Case[]) extras[0];
				return new Bytecode.Switch(operands[0], cases);
			}
		};
		
		// =========================================================================
		// Unary Assignables
		// =========================================================================
		schemas[Bytecode.OPCODE_assign] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				int numLhsOperands = operands[operands.length-1];
				int[] lhsOperands = Arrays.copyOfRange(operands, 0, numLhsOperands);
				int[] rhsOperands = Arrays.copyOfRange(operands, numLhsOperands, operands.length-1);
				return new Bytecode.Assign(lhsOperands,rhsOperands);
			}
		};		
		schemas[Bytecode.OPCODE_newobject] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands,Bytecode.OperatorKind.NEW);
			}
		};
		schemas[Bytecode.OPCODE_dereference] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.DEREFERENCE);
			}
		};
		schemas[Bytecode.OPCODE_bitwiseinvert] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.BITWISEINVERT);
			}
		};
		schemas[Bytecode.OPCODE_arraylength] = new Schema(Operands.ONE) {
			public Bytecode construct(int opcode, int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.ARRAYLENGTH);
			}
		};
		schemas[Bytecode.OPCODE_neg] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.NEG);
			}
		};
		schemas[Bytecode.OPCODE_logicalnot] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.NOT);
			}
		};
		schemas[Bytecode.OPCODE_fieldload] = new Schema(Operands.ONE, Extras.STRING){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.FieldLoad(operands[0], (String) extras[0]);
			}
		};
		schemas[Bytecode.OPCODE_convert] = new Schema(Operands.ONE, Extras.TYPE){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {				
				return new Bytecode.Convert(operands[0], (Type) extras[0]);
			}
		};
		schemas[Bytecode.OPCODE_const] = new Schema(Operands.ZERO, Extras.CONSTANT){
			public Bytecode construct(int opcode, int[] operands, Object[] extras) {
				return new Bytecode.Const((Constant) extras[0]);
			}
		};

		// =========================================================================
		// Binary Operators
		// =========================================================================
		schemas[Bytecode.OPCODE_if] = new Schema(Operands.ONE, Extras.BLOCK_ARRAY){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				int[] blocks = (int[]) extras[0];
				if(blocks.length == 1) {
					return new Bytecode.If(operands[0], blocks[0]);	
				} else {
					return new Bytecode.If(operands[0], blocks[0], blocks[1]);
				}
			}
		};
		
		// =========================================================================
		// Binary Assignables
		// =========================================================================
		schemas[Bytecode.OPCODE_add] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.ADD);
			}
		};
		schemas[Bytecode.OPCODE_sub] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.SUB);
			}
		};
		schemas[Bytecode.OPCODE_mul] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.MUL);
			}
		};
		schemas[Bytecode.OPCODE_div] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.DIV);
			}
		};
		schemas[Bytecode.OPCODE_rem] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.REM);
			}
		};
		schemas[Bytecode.OPCODE_eq] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.EQ);
			}
		};
		schemas[Bytecode.OPCODE_ne] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.NEQ);
			}
		};
		schemas[Bytecode.OPCODE_lt] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.LT);
			}
		};
		schemas[Bytecode.OPCODE_le] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.LTEQ);
			}
		};
		schemas[Bytecode.OPCODE_gt] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.GT);
			}
		};
		schemas[Bytecode.OPCODE_ge] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.GTEQ);
			}
		};
		schemas[Bytecode.OPCODE_logicalor] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.OR);
			}
		};
		schemas[Bytecode.OPCODE_logicaland] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.AND);
			}
		};
		schemas[Bytecode.OPCODE_bitwiseor] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.BITWISEOR);
			}
		};
		schemas[Bytecode.OPCODE_bitwisexor] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.BITWISEXOR);
			}
		};
		schemas[Bytecode.OPCODE_bitwiseand] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.BITWISEAND);
			}
		};
		schemas[Bytecode.OPCODE_shl] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.LEFTSHIFT);
			}
		};
		schemas[Bytecode.OPCODE_shr] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.RIGHTSHIFT);
			}
		};
		schemas[Bytecode.OPCODE_arrayindex] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands,Bytecode.OperatorKind.ARRAYINDEX);
			}
		};
		schemas[Bytecode.OPCODE_arraygen] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands,Bytecode.OperatorKind.ARRAYGENERATOR);
			}
		};
		schemas[Bytecode.OPCODE_is] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.IS);
			}
		};

		schemas[Bytecode.OPCODE_array] = new Schema(Operands.MANY){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.ARRAYCONSTRUCTOR);
			}
		};

		// =========================================================================
		// Nary Assignables
		// =========================================================================
		schemas[Bytecode.OPCODE_record] = new Schema(Operands.MANY){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.RECORDCONSTRUCTOR);
			}
		};
		schemas[Bytecode.OPCODE_invoke] = new Schema(Operands.MANY, Extras.TYPE, Extras.NAME) {
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				return new Bytecode.Invoke((Type.FunctionOrMethod) extras[0], operands, (NameID) extras[1]);
			}
		};
		schemas[Bytecode.OPCODE_indirectinvoke] = new Schema(Operands.MANY, Extras.TYPE){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				int src = operands[0];
				operands = Arrays.copyOfRange(operands,1,operands.length);
				return new Bytecode.IndirectInvoke((Type.FunctionOrMethod) extras[0], src, operands);
			}
		};
		schemas[Bytecode.OPCODE_lambda] = new Schema(Operands.MANY, Extras.TYPE) {
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				Type.FunctionOrMethod type = (Type.FunctionOrMethod) extras[0];
				int body = operands[0];
				int[] parameters = Arrays.copyOfRange(operands, 1, 1+type.params().size());
				int[] environment = Arrays.copyOfRange(operands, 1+type.params().size(), operands.length);
				return new Bytecode.Lambda(type,body,parameters,environment);
			}
		};
		schemas[Bytecode.OPCODE_while] = new Schema(Operands.MANY, Extras.BLOCK_ARRAY){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				int[] blocks = (int[]) extras[0];
				int body = blocks[0];
				int condition = operands[0];
				int[] invariants = Arrays.copyOfRange(operands, 1, operands.length);
				return new Bytecode.While(body,condition,invariants);
			}
		};
		schemas[Bytecode.OPCODE_dowhile] = new Schema(Operands.MANY, Extras.BLOCK_ARRAY){
			public Bytecode construct(int opcode,int[] operands, Object[] extras) {
				int[] blocks = (int[]) extras[0];
				int body = blocks[0];
				int condition = operands[0];
				int[] invariants = Arrays.copyOfRange(operands, 1, operands.length);
				return new Bytecode.DoWhile(body,condition,invariants);
			}
		};
		schemas[Bytecode.OPCODE_none] = schemas[Bytecode.OPCODE_some] = schemas[Bytecode.OPCODE_all] = new Schema(
				Operands.MANY) {
			public Bytecode construct(int opcode, int[] operands, Object[] extras) {
				int body = operands[0];
				Bytecode.Range[] ranges = new Bytecode.Range[(operands.length - 1) / 3];
				int j = 1;
				for (int i = 0; i != ranges.length; i = i + 1) {
					ranges[i] = new Bytecode.Range(operands[j++], operands[j++], operands[j++]);
				}
				Bytecode.QuantifierKind kind;
				switch(opcode) {
				case Bytecode.OPCODE_none:
					kind = Bytecode.QuantifierKind.NONE;
					break;
				case Bytecode.OPCODE_some:
					kind = Bytecode.QuantifierKind.SOME;
					break;
				case Bytecode.OPCODE_all:
					kind = Bytecode.QuantifierKind.ALL;
					break;
				default:
					// deadcpde
					throw new IllegalArgumentException();
				}
				return new Bytecode.Quantifier(kind, body, ranges);
			}
		};
	}
}
