package wycs.io;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wyautl.core.Automaton;
import wyautl.io.BinaryAutomataWriter;
import wyautl.util.BigRational;
import wycc.lang.NameID;
import wycc.util.Pair;
import wycc.util.Triple;
import wycs.core.*;
import wyfs.io.BinaryOutputStream;
import wyfs.lang.Path;

public class WycsFileWriter {
	private static final int MAJOR_VERSION = 0;
	private static final int MINOR_VERSION = 1;

	private BinaryOutputStream out;

	private final ArrayList<String> stringPool  = new ArrayList<String>();
	private final HashMap<String,Integer> stringCache = new HashMap<String,Integer>();
	private final ArrayList<PATH_Item> pathPool = new ArrayList<PATH_Item>();
	private final HashMap<Path.ID,Integer> pathCache = new HashMap<Path.ID,Integer>();
	private final ArrayList<NAME_Item> namePool = new ArrayList<NAME_Item>();
	private final HashMap<NameID,Integer> nameCache = new HashMap<NameID,Integer>();
	private final ArrayList<Value> constantPool = new ArrayList<Value>();
	private final HashMap<Value,Integer> constantCache = new HashMap<Value,Integer>();
	private final ArrayList<SemanticType> typePool = new ArrayList<SemanticType>();
	private final HashMap<SemanticType,Integer> typeCache = new HashMap<SemanticType,Integer>();

	public WycsFileWriter(OutputStream output) {
		this.out = new BinaryOutputStream(output);
	}

	public void write(WycsFile module) throws IOException {
		// first, write magic number
		out.write_u8(0x57); // W
		out.write_u8(0x59); // Y
		out.write_u8(0x43); // C
		out.write_u8(0x53); // S
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
				bytes = generateHeaderBlock((WycsFile) data);
				break;
			case BLOCK_Module:
				bytes = generateModuleBlock((WycsFile) data);
				break;
			case BLOCK_Macro:
				bytes = generateMacroBlock((WycsFile.Macro) data);
				break;
			case BLOCK_Function:
				bytes = generateFunctionBlock((WycsFile.Function) data);
				break;
			case BLOCK_Type:
				bytes = generateTypeBlock((WycsFile.Type) data);
				break;			
			case BLOCK_Assert:
				bytes = generateAssertBlock((WycsFile.Assert) data);
				break;
			case BLOCK_Code:
				bytes = generateCodeBlock((Code) data);
				break;
		}

		output.write_uv(kind);
		output.write_uv(bytes.length);
		output.pad_u8(); // pad out to next byte boundary
		output.write(bytes);
	}

	/**
	 * Write the header information for this WYCS file, including the stratified
	 * resource pool.
	 *
	 * @param module
	 *
	 * @throws IOException
	 */
	private byte[] generateHeaderBlock(WycsFile module)
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
		output.write_uv(typePool.size());
		output.write_uv(constantPool.size());

		// finally, write the number of remaining blocks
		output.write_uv(module.declarations().size());

		writeStringPool(output);
		writePathPool(output);
		writeNamePool(output);
		writeTypePool(output);
		writeConstantPool(output);

		output.close();

		return bytes.toByteArray();
	}

	private byte[] generateModuleBlock(WycsFile module) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);

		output.write_uv(pathCache.get(module.id())); // FIXME: BROKEN!
		output.write_uv(module.declarations().size());

		for (WycsFile.Declaration d : module.declarations()) {
			writeModuleBlock(d, output);
		}

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
		for(int i=1;i<pathPool.size();++i) {
			PATH_Item p = pathPool.get(i);
			output.write_uv(p.parentIndex);
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
		// System.out.println("Writing " + constantPool.size() + " constant item(s).");

		for (Value val : constantPool) {
			if(val instanceof Value.Null) {
				Value.Null b = (Value.Null) val;
				output.write_uv(CONSTANT_Null);
			} else if(val instanceof Value.Bool) {
				Value.Bool b = (Value.Bool) val;
				output.write_uv(b.value ? CONSTANT_True : CONSTANT_False);
			} else if(val instanceof Value.Integer) {
				Value.Integer i = (Value.Integer) val;
				BigInteger num = i.value;
				byte[] numbytes = num.toByteArray();
				output.write_uv(CONSTANT_Int);
				output.write_uv(numbytes.length);
				output.write(numbytes);
			} else if(val instanceof Value.String) {
				Value.String s = (Value.String) val;
				output.write_uv(CONSTANT_String);
				String value = s.value;
				output.write_uv(stringCache.get(value));

			} else if(val instanceof Value.Decimal) {
				Value.Decimal r = (Value.Decimal) val;
				output.write_uv(CONSTANT_Real);
				BigInteger mantissa = r.value.unscaledValue();
				int exponent = r.value.scale();
				byte[] bytes = mantissa.toByteArray();
				output.write_uv(bytes.length);
				output.write(bytes);
				output.write_uv(exponent);

			} else if(val instanceof Value.Set) {
				Value.Set s = (Value.Set) val;
				output.write_uv(CONSTANT_Set);
				output.write_uv(s.values.size());
				for(Value v : s.values) {
					int index = constantCache.get(v);
					output.write_uv(index);
				}

			} else if(val instanceof Value.Tuple) {
				Value.Tuple t = (Value.Tuple) val;
				output.write_uv(CONSTANT_Tuple);
				output.write_uv(t.values.size());
				for(Value v : t.values) {
					int index = constantCache.get(v);
					output.write_uv(index);
				}
			} else {
				throw new RuntimeException("Unknown value encountered - " + val);
			}
		}
	}

	private void writeTypePool(BinaryOutputStream output) throws IOException {
		// First, we accumulate the automata for all types in the pool into one
		// automaton. This helps reduce the amount of redundancy between types.
		Automaton global = new Automaton();
		for (int i = 0; i != typePool.size(); ++i) {
			Automaton automaton = typePool.get(i).automaton();
			int root = global.addAll(automaton.getRoot(0), automaton);
			global.setRoot(i, root);
		}

		global.minimise();
		global.compact();
		// FIXME: put this back in!!
		// global.canonicalise();

		// Second, we write the single global automaton to the output stream.
		BinaryAutomataWriter writer = new BinaryAutomataWriter(output,
				Types.SCHEMA);
		writer.write(global);
	}

	private void writeModuleBlock(WycsFile.Declaration d,
			BinaryOutputStream output) throws IOException {
		if(d instanceof WycsFile.Macro) {
			writeBlock(BLOCK_Macro, d ,output);
		} else if(d instanceof WycsFile.Type) {
			writeBlock(BLOCK_Type, d ,output);
		} else if(d instanceof WycsFile.Function) {
			writeBlock(BLOCK_Function, d, output);
		} else if(d instanceof WycsFile.Assert) {
			writeBlock(BLOCK_Assert, d, output);
		}
	}

	private byte[] generateMacroBlock(WycsFile.Macro md) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);

		output.write_uv(stringCache.get(md.name()));
		output.write_uv(typeCache.get(md.type));
		output.write_uv(1);
		writeBlock(BLOCK_Code,md.condition,output);

		output.close();
		return bytes.toByteArray();
	}

	private byte[] generateTypeBlock(WycsFile.Type md) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);

		output.write_uv(stringCache.get(md.name()));
		output.write_uv(typeCache.get(md.type));
		if(md.invariant == null) {
			output.write_uv(0);			
		} else {
			output.write_uv(1);
			writeBlock(BLOCK_Code,md.invariant,output);
		}

		output.close();
		return bytes.toByteArray();
	}
	
	private byte[] generateFunctionBlock(WycsFile.Function fd) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);

		output.write_uv(stringCache.get(fd.name()));
		output.write_uv(typeCache.get(fd.type));
		if(fd.constraint == null) {
			output.write_uv(0); // no sub-blocks
		} else {
			output.write_uv(1); // one sub-block
			writeBlock(BLOCK_Code,fd.constraint,output);
		}

		output.close();
		return bytes.toByteArray();
	}

	private byte[] generateAssertBlock(WycsFile.Assert td) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);

		output.write_uv(stringCache.get(td.name()));
		output.write_uv(1); // one sub-block
		writeBlock(BLOCK_Code,td.condition,output);

		output.close();
		return bytes.toByteArray();
	}

	/**
	 * Flatten a Wycs bytecode into a byte stream. Each Wycs bytecode represents
	 * a tree of operations and, hence, this function recursively traverses the
	 * tree.
	 *
	 * @param code
	 * @return
	 * @throws IOException
	 */
	private byte[] generateCodeBlock(Code<?> code) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		BinaryOutputStream output = new BinaryOutputStream(bytes);

		writeCode(code,output);

		output.close();
		return bytes.toByteArray();
	}

	/**
	 * <p>
	 * Write a given code to a binary output stream. Every bytecode is written
	 * in the following structure:
	 * </p>
	 *
	 * <pre>
	 * +---------------+
	 * | opcode        |
	 * +---------------+
	 * | type index    |
	 * +---------------+
	 * | operand count |
	 * +---------------+
	 * | 1st operand   |
	 * +---------------+
	 * |               |
	 *
	 * ...
	 * |               |
	 * +---------------+
	 * | Nth operand   |
	 * +---------------+
	 * | Payload       |
	 * +---------------+
	 * </pre>
	 *
	 * <p>
	 * Each cell here is of varying size and is written using one or more 4-bit
	 * nibbles. The type index is an index into the type pool which gives the
	 * type associated with this bytecode. The operand count determines the
	 * number of operands (if any). The payload is an option field which
	 * contains bytecode specific data. For example, a constant bytecode will
	 * contain an index into the constant pool here.
	 * </p>
	 *
	 * @param code
	 * @param output
	 * @throws IOException
	 */
	private void writeCode(Code<?> code, BinaryOutputStream output) throws IOException {
		if(code == null) {
			// this is a special case
			output.write_u8(Code.Op.NULL.offset);
		} else {
			output.write_u8(code.opcode.offset);
			output.write_uv(typeCache.get(code.type));
			output.write_uv(code.operands.length);
			for(int i=0;i!=code.operands.length;++i) {
				writeCode(code.operands[i],output);
			}
			// now, write bytecode specific stuff
			switch(code.opcode){
			case VAR: {
				Code.Variable v = (Code.Variable) code;
				output.write_uv(v.index);
				break;
			}
			case CAST: {
				Code.Cast c = (Code.Cast) code;
				output.write_uv(typeCache.get(c.target));
				break;
			}
			case CONST: {
				Code.Constant c = (Code.Constant) code;
				output.write_uv(constantCache.get(c.value));
				break;
			}
			case IS: {
				Code.Is is = (Code.Is) code; 
				output.write_uv(typeCache.get(is.test));
				break;
			}
			case LOAD: {
				Code.Load c = (Code.Load) code;
				output.write_uv(c.index);
				break;
			}
			case FORALL:
			case EXISTS: {
				Code.Quantifier c = (Code.Quantifier) code;
				output.write_uv(c.types.length);
				for (Pair<SemanticType,Integer> t : c.types) {
					output.write_uv(typeCache.get(t.first()));
					output.write_uv(t.second());
				}
				break;
			}
			case FUNCALL: {
				Code.FunCall c = (Code.FunCall) code;
				output.write_uv(nameCache.get(c.nid));
				output.write_uv(c.binding.length);
				for (SemanticType t : c.binding) {
					output.write_uv(typeCache.get(t));					
				}				
			}
			}
		}
	}

	/**
	 * Build the various pools of items (strings, types, constants, etc) which
	 * are used within the bytecodes, and within the declarations (e.g.
	 * assertions, etc). Each pool consists of an array of indexed items of the
	 * same kind. Items in some pools (e.g. constant) may refer to items in
	 * other pools (e.g. string) and this referencing is done via an index into
	 * the given pool.
	 *
	 * @param module
	 */
	private void buildPools(WycsFile module) {
		stringPool.clear();
		stringCache.clear();

		pathPool.clear();
		pathCache.clear();
		// preload the path root
		pathPool.add(null);
		pathCache.put(wyfs.util.Trie.ROOT,0);

		constantPool.clear();
		constantCache.clear();

		typePool.clear();
		typeCache.clear();

		addPathItem(module.id());
		for(WycsFile.Declaration d : module.declarations()) {
			buildPools(d);
		}
	}

	private void buildPools(WycsFile.Declaration declaration) {
		if(declaration instanceof WycsFile.Macro) {
			buildPools((WycsFile.Macro)declaration);
		} else if(declaration instanceof WycsFile.Type) {
			buildPools((WycsFile.Type)declaration);
		} else if(declaration instanceof WycsFile.Function) {
			buildPools((WycsFile.Function)declaration);
		} else if(declaration instanceof WycsFile.Assert) {
			buildPools((WycsFile.Assert)declaration);
		}
	}

	private void buildPools(WycsFile.Macro declaration) {
		addStringItem(declaration.name());
		addTypeItem(declaration.type);
		buildPools(declaration.condition);
	}

	private void buildPools(WycsFile.Type declaration) {
		addStringItem(declaration.name());
		addTypeItem(declaration.type);
		if(declaration.invariant != null) {
			buildPools(declaration.invariant);
		}
	}

	private void buildPools(WycsFile.Function declaration) {
		addStringItem(declaration.name());
		addTypeItem(declaration.type);
		if(declaration.constraint != null) {
			buildPools(declaration.constraint);
		}
	}

	private void buildPools(WycsFile.Assert declaration) {
		addStringItem(declaration.name());
		buildPools(declaration.condition);
	}

	private void buildPools(Code code) {

		// First, deal with special cases
		if(code instanceof Code.Constant) {
			Code.Constant c = (Code.Constant) code;
			addConstantItem(c.value);
		} else if(code instanceof Code.Cast) {
			Code.Cast c = (Code.Cast) code;
			addTypeItem(c.target);			
		} else if(code instanceof Code.Quantifier) {
			Code.Quantifier c = (Code.Quantifier) code;
			for(Pair<SemanticType,Integer> p : c.types) {
				addTypeItem(p.first());
			}
		} else if(code instanceof Code.FunCall) {
			Code.FunCall c = (Code.FunCall) code;
			addNameItem(c.nid);
			for(SemanticType t : c.binding) {
				addTypeItem(t);
			}
		} else if(code instanceof Code.Is) {
			Code.Is c = (Code.Is) code;
			addTypeItem(c.test);
		} 

		// Second, deal with standard cases
		addTypeItem(code.type);

		for(Code operand : code.operands) {
			buildPools(operand);
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
		Integer index = pathCache.get(pid);
		if(index == null) {
			int parent = addPathItem(pid.parent());
			int i = pathPool.size();
			pathPool.add(new PATH_Item(parent,addStringItem(pid.last())));
			pathCache.put(pid, i);
			return i;
		} else {
			return index;
		}
	}

	private int addTypeItem(SemanticType t) {

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

		Integer index = constantCache.get(v);
		if(index == null) {
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

	private void addConstantSubitems(Value v) {
		if(v instanceof Value.String) {
			Value.String s = (Value.String) v;
			addStringItem(s.value);
		} else if(v instanceof Value.Set) {
			Value.Set s = (Value.Set) v;
			for (Value e : s.values) {
				addConstantItem(e);
			}
		} else if(v instanceof Value.Tuple) {
			Value.Tuple t = (Value.Tuple) v;
			for (Value e : t.values) {
				addConstantItem(e);
			}
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

	// =========================================================================
	// BLOCK identifiers
	// =========================================================================

	public final static int BLOCK_Header = 0;
	public final static int BLOCK_Module = 1;
	public final static int BLOCK_Documentation = 2;
	public final static int BLOCK_License = 3;
	// ... (anticipating some others here)
	public final static int BLOCK_Code = 10;
	public final static int BLOCK_Macro = 11;
	public final static int BLOCK_Function = 12;
	public final static int BLOCK_Assert = 13;
	public final static int BLOCK_Type = 14;
	// ... (anticipating some others here)

	// =========================================================================
	// CONSTANT identifiers
	// =========================================================================

	public final static int CONSTANT_Null = 0;
	public final static int CONSTANT_True = 1;
	public final static int CONSTANT_False = 2;
	public final static int CONSTANT_Int = 3;
	public final static int CONSTANT_Real = 4;
	public final static int CONSTANT_Set = 5;
	public final static int CONSTANT_String = 6;
	public final static int CONSTANT_Tuple = 7;
}
