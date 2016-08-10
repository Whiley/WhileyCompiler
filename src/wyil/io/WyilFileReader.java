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

import wybs.lang.Attribute;
import wybs.lang.NameID;
import wyfs.io.BinaryInputStream;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.*;
import wyil.lang.Bytecode.Expr;
import wyil.lang.SyntaxTree.Location;
import wyil.util.AbstractBytecode;

/**
 * Read a binary WYIL file from a byte stream and convert into the corresponding
 * WyilFile object.
 *
 * @author David J. Pearce
 *
 */
public final class WyilFileReader {
	private static final char[] magic = { 'W', 'Y', 'I', 'L', 'F', 'I', 'L', 'E' };

	private final Path.Entry<WyilFile> entry;
	private final BinaryInputStream input;
	private String[] stringPool;
	private Path.ID[] pathPool;
	private NameID[] namePool;
	private Constant[] constantPool;
	private Type[] typePool;

	public WyilFileReader(Path.Entry<WyilFile> entry) throws IOException {
		this.entry = entry;
		this.input = new BinaryInputStream(entry.inputStream());
	}

	/**
	 * Construct a WyilFileReader to read a WyilFile in headless mode. That is,
	 * where the file is not associated with a Path.Entry.
	 * 
	 * @param input
	 */
	public WyilFileReader(InputStream input) throws IOException {
		this.entry = null;
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
				constant = new Constant.FunctionOrMethod(name, t);
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
		WyilFile wyilFile = new WyilFile(entry);
		for (int i = 0; i != numBlocks; ++i) {
			readModuleBlock(wyilFile);
		}

		return wyilFile;
	}

	
	private void readModuleBlock(WyilFile parent) throws IOException {
		int kind = input.read_uv();
		int size = input.read_uv();
		input.pad_u8();

		switch (kind) {
		case WyilFileWriter.BLOCK_Constant:
			readConstantBlock(parent);
			break;
		case WyilFileWriter.BLOCK_Type:
			readTypeBlock(parent);
			 break;
		case WyilFileWriter.BLOCK_Function:
		case WyilFileWriter.BLOCK_Method:
			readFunctionOrMethodBlock(parent);
			break;
		default:
			throw new RuntimeException("unknown module block encountered (" + kind + ")");
		}

		input.pad_u8();
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
	private void readConstantBlock(WyilFile parent) throws IOException {
		int nameIdx = input.read_uv();
		int modifiers = input.read_uv();
		int constantIdx = input.read_uv();

		WyilFile.Block block = new WyilFile.Constant(parent, generateModifiers(modifiers), stringPool[nameIdx],
				constantPool[constantIdx]);
		parent.blocks().add(block);
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
	private void readTypeBlock(WyilFile parent) throws IOException {
		int nameIdx = input.read_uv();
		int modifiers = input.read_uv();
		int typeIdx = input.read_uv();
		int nInvariants = input.read_uv();
		//
		Collection<Modifier> mods = generateModifiers(modifiers);
		String name = stringPool[nameIdx];
		Type type = typePool[typeIdx];
		//
		int[] invariant = new int[nInvariants];		
		for (int i = 0; i != nInvariants; ++i) {
			invariant[i] = input.read_uv();
		}
		WyilFile.Type decl = new WyilFile.Type(parent, mods, name, type);
		SyntaxTree tree = readSyntaxTree(decl);
		//
		for (int i = 0; i != nInvariants;++i) {
			Location<Bytecode.Expr> expr = (Location<Expr>) tree.getLocation(invariant[i]);
			decl.getInvariant().add(expr);
		}
		parent.blocks().add(decl);
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
	private void readFunctionOrMethodBlock(WyilFile parent) throws IOException {
		int nameIdx = input.read_uv();
		int modifiers = input.read_uv();
		int typeIdx = input.read_uv();
		int nPreconditions = input.read_uv();
		int nPostconditions = input.read_uv();
		//
		Collection<Modifier> mods = generateModifiers(modifiers);
		String name = stringPool[nameIdx];
		Type.FunctionOrMethod type = (Type.FunctionOrMethod) typePool[typeIdx];
		//
		WyilFile.FunctionOrMethod decl = new WyilFile.FunctionOrMethod(parent, mods, name, type);
		int[] precondition = new int[nPreconditions];
		for (int i = 0; i != nPreconditions; ++i) {
			precondition[i] = input.read_uv();
		}
		int[] postcondition = new int[nPostconditions];
		for (int i = 0; i != nPostconditions; ++i) {
			postcondition[i] = input.read_uv();
		}
		//
		int body = input.read_uv();
		//
		readSyntaxTree(decl);
		SyntaxTree tree = decl.getTree();
		//
		for (int i = 0; i != nPreconditions; ++i) {
			Location<Bytecode.Expr> expr = (Location<Expr>) tree.getLocation(precondition[i]);
			decl.getPrecondition().add(expr);
		}
		//
		for (int i = 0; i != nPostconditions; ++i) {
			Location<Bytecode.Expr> expr = (Location<Expr>) tree.getLocation(postcondition[i]);
			decl.getPostcondition().add(expr);
		}
		//
		Location<Bytecode.Block> loc = (Location<Bytecode.Block>) tree.getLocation(body);
		decl.setBody(loc);
		//
		parent.blocks().add(decl);
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
	 * Read a syntax tree from the output stream. The format
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
	 * @param parent
	 * @return
	 * @throws IOException
	 */
	private SyntaxTree readSyntaxTree(WyilFile.Declaration parent) throws IOException {
		SyntaxTree tree = parent.getTree();
		int nLocs = input.read_uv();
		for(int i=0;i!=nLocs;++i) {
			tree.getLocations().add(readLocation(tree));
		}
		return tree;
	}
	
	/**
	 * Read details of a Location from the input stream. The format of a
	 * location is:
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
	 * @param output
	 * @throws IOException
	 */
	private SyntaxTree.Location<?> readLocation(SyntaxTree tree) throws IOException {
		int nTypes = input.read_uv();
		Type[] types = new Type[nTypes];
		for (int i = 0; i != types.length; ++i) {
			int typeIdx = input.read_uv();
			types[i] = typePool[typeIdx];
		}
		int nAttrs = input.read_uv();
		Bytecode bytecode = readBytecode();
		//
		List<Attribute> attributes = new ArrayList<Attribute>();
		//
		return new SyntaxTree.Location<Bytecode>(tree, types, bytecode, attributes);
	}
	
	/**
	 * <p>
	 * REad a given bytecode whose format is currently given as follows:
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
	 * </pre>
	 * 
	 * <p>
	 * <b>NOTE:</b> The intention is to support a range of different bytecode
	 * formats in order to optimise the common cases. For example, when there
	 * are no targets, no operands, no types, etc. Furthermore, when the size of
	 * items can be reduced from uv to u4, etc.
	 * </p>
	 */
	private Bytecode readBytecode() throws IOException {
		int opcode = input.read_u8();
		int nAttrs = input.read_uv();
		// FIXME: read attributes!
		Bytecode.Schema schema = AbstractBytecode.schemas[opcode];
		// First, read and validate all operands, groups and blocks
		int[] operands = readOperands(schema);
		int[][] groups = readOperandGroups(schema);
		int[] blocks = readBlocks(schema);
		// Second, read all extras		
		Object[] extras = readExtras(schema);		
		// Finally, create the bytecode
		return schema.construct(opcode,operands,groups,blocks,extras);		
	}
	
	private int[] readOperands(Bytecode.Schema schema) throws IOException {		
		switch(schema.getOperands()) {
		case ZERO:
			// do nout
			return null;
		case ONE:
			int o = input.read_uv();
			return new int[] { o }; 
		case TWO:
			int o1 = input.read_uv();
			int o2 = input.read_uv();
			return new int[] { o1, o2 };
		case MANY:
		default:
			return readUnboundArray();
		}
	}
	
	private int[][] readOperandGroups(Bytecode.Schema schema) throws IOException {
		switch(schema.getOperandGroups()) {
		case ZERO:
			// do nout
			return null;
		case ONE:
			int[] o = readUnboundArray();
			return new int[][] { o }; 
		case TWO:
			int[] o1 = readUnboundArray();
			int[] o2 = readUnboundArray();
			return new int[][] { o1, o2 };
		case MANY:
		default:
			int size = input.read_uv();
			int[][] os = new int[size][];
			for(int i=0;i!=size;++i) {
				os[i] = readUnboundArray();
			}
			return os;
		}
	}
	
	private int[] readBlocks(Bytecode.Schema schema) throws IOException {
		switch(schema.getBlocks()) {
		case ZERO:
			// do nout
			return null;
		case ONE:
			int o = input.read_uv();
			return new int[] { o }; 
		case TWO:
			int o1 = input.read_uv();
			int o2 = input.read_uv();
			return new int[] { o1, o2 };
		case MANY:
		default:
			return readUnboundArray();
		}
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
		Bytecode.Extras[] extras = schema.extras();
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
	
	private int[] readUnboundArray() throws IOException {
		int size = input.read_uv();
		int[] array = new int[size];
		for(int i=0;i!=size;++i) {
			array[i] = input.read_uv();
		}
		return array;
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
}
