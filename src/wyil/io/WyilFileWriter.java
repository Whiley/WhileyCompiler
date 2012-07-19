package wyil.io;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import wyil.Transform;
import wybs.lang.Path;
import wyil.lang.*;
import wyil.util.Pair;
import wyjc.attributes.WhileyType;
import wyjc.runtime.BigRational;
import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.Constant;

public class WyilFileWriter implements Transform {
	private static final int MAJOR_VERSION = 0;
	private static final int MINOR_VERSION = 1;
	
	private BinaryOutputStream output;
	
	private ArrayList<String> stringPool  = new ArrayList<String>();
	private HashMap<String,Integer> stringCache = new HashMap<String,Integer>();
	
	private ArrayList<PATH_Item> pathPool = new ArrayList<PATH_Item>();
	private HashMap<Path.ID,Integer> pathCache = new HashMap<Path.ID,Integer>();
	
	private ArrayList<NAME_Item> namePool = new ArrayList<NAME_Item>();
	private HashMap<Pair<NAME_Kind,Path.ID>,Integer> nameCache = new HashMap<Pair<NAME_Kind,Path.ID>,Integer>();
	
	private ArrayList<Value> constantPool = new ArrayList<Value>();
	private HashMap<Value,Integer> constantCache = new HashMap<Value,Integer>();
	
	private ArrayList<Type> typePool = new ArrayList<Type>();
	private HashMap<Type,Integer> typeCache = new HashMap<Type,Integer>();
	
	public WyilFileWriter(wybs.lang.Builder builder) {

	}
	
	@Override
	public void apply(WyilFile module) throws IOException {
		String filename = module.filename().replace(".whiley", ".wyil");
		output = new BinaryOutputStream(new FileOutputStream(filename));
		
		buildPools(module);
		
		writeHeader(module);
		writePools();
		
		output.close();
	}	
	
	/**
	 * Write the header information for this WYIL file.
	 * 
	 * @param module
	 * @throws IOException
	 */
	private void writeHeader(WyilFile module)
			throws IOException {
		
		// first, write magic number
		output.write_u1(0x57); // W
		output.write_u1(0x59); // Y
		output.write_u1(0x49); // I
		output.write_u1(0x4C); // L
		output.write_u1(0x46); // F
		output.write_u1(0x49); // I
		output.write_u1(0x4C); // L
		output.write_u1(0x45); // E
		
		// second, write the file version number 
		output.write_uv(MAJOR_VERSION); 
		output.write_uv(MINOR_VERSION); 
		
		// third, write the various pool sizes
		output.write_uv(stringPool.size());
		output.write_uv(pathPool.size());
		output.write_uv(namePool.size());
		output.write_uv(constantPool.size());
		output.write_uv(typePool.size());		
		
		// finally, write the number of blocks
		output.write_uv(module.declarations().size());
	}
	
	private void writePools() throws IOException{
		writeStringPool();
		writePathPool();
		writeNamePool();
		writeConstantPool();
		writeTypePool();
	}
	
	/**
	 * Write the list of strings making up the string pool in UTF8.
	 * 
	 * @throws IOException
	 */
	private void writeStringPool() throws IOException {
		System.out.println("Writing " + stringPool.size() + " string item(s).");
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
	
	private void writePathPool() throws IOException {
		System.out.println("Writing " + stringPool.size() + " path item(s).");
		for (PATH_Item p : pathPool) {
			output.write_uv(p.parentIndex + 1);
			output.write_uv(p.stringIndex);
		}
	}

	private void writeNamePool() throws IOException {
		System.out.println("Writing " + stringPool.size() + " name item(s).");
		for (NAME_Item p : namePool) {
			output.write_uv(p.kind.kind());
			output.write_uv(p.pathIndex);
		}
	}

	private void writeConstantPool() throws IOException {
		System.out.println("Writing " + stringPool.size() + " constant item(s).");
		ValueWriter bout = new ValueWriter(output);
		for (Value v : constantPool) {
			bout.write(v);
		}
	}

	private void writeTypePool() throws IOException {
		System.out.println("Writing " + stringPool.size() + " type item(s).");
		Type.BinaryWriter bout = new Type.BinaryWriter(output);
		for (Type t : typePool) {
			bout.write(t);
		}
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
		
		addPathItem(NAME_Kind.MODULE,module.id());
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
	}
	
	private void buildPools(WyilFile.ConstantDeclaration declaration) {
		addStringItem(declaration.name());
		addConstantItem(declaration.constant());
	}

	private void buildPools(WyilFile.MethodDeclaration declaration) {
		addStringItem(declaration.name());
		addTypeItem(declaration.type());
	}
	
	private int addPathItem(NAME_Kind kind, Path.ID pid) {
		Pair<NAME_Kind,Path.ID> p = new Pair(kind,pid);
		Integer index = nameCache.get(p);
		if(index == null) {
			int i = namePool.size();
			nameCache.put(p, i);
			namePool.add(new NAME_Item(kind,addPathItem(pid)));
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
			int i = constantPool.size();
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
		public final NAME_Kind kind;
		
		/**
		 * Index of path for this item in path pool
		 */
		public final int pathIndex;
		
		public NAME_Item(NAME_Kind kind, int pathIndex) {
			this.kind = kind;
			this.pathIndex = pathIndex;
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
			output.write_u1(NULL);
		}
		
		public void write(Value.Bool expr) throws IOException {
			
			if(expr.value) {
				output.write_u1(TRUE);
			} else {
				output.write_u1(FALSE);
			}
		}
		
		public void write(Value.Byte expr) throws IOException {		
			output.write_u1(BYTEVAL);		
			output.write_u1(expr.value);		
		}
		
		public void write(Value.Char expr) throws IOException {		
			output.write_u1(CHARVAL);		
			output.write_u2(expr.value);		
		}
		
		public void write(Value.Integer expr) throws IOException {		
			output.write_u1(INTVAL);		
			BigInteger num = expr.value;
			byte[] numbytes = num.toByteArray();
			// FIXME: bug here for constants that require more than 65535 bytes
			output.write_u2(numbytes.length);
			output.write(numbytes);
		}

		public void write(Value.Rational expr) throws IOException {		
			
			output.write_u1(REALVAL);
			BigRational br = expr.value;
			BigInteger num = br.numerator();
			BigInteger den = br.denominator();

			byte[] numbytes = num.toByteArray();
			// FIXME: bug here for constants that require more than 65535 bytes
			output.write_u2(numbytes.length);
			output.write(numbytes);

			byte[] denbytes = den.toByteArray();
			// FIXME: bug here for constants that require more than 65535 bytes
			output.write_u2(denbytes.length);
			output.write(denbytes);
		}
			
		public void write(Value.Strung expr) throws IOException {	
			output.write_u1(STRINGVAL);
			String value = expr.value;
			int valueLength = value.length();		
			output.write_u2(valueLength);
			for(int i=0;i!=valueLength;++i) {
				output.write_u2(value.charAt(i));
			}
		}
		public void write(Value.Set expr) throws IOException {
			output.write_u1(SETVAL);
			output.write_u2(expr.values.size());
			for(Value v : expr.values) {
				write(v);
			}
		}
		
		public void write(Value.List expr) throws IOException {
			output.write_u1(LISTVAL);
			output.write_u2(expr.values.size());
			for(Value v : expr.values) {
				write(v);
			}
		}
		
		public void write(Value.Map expr) throws IOException {
			output.write_u1(MAPVAL);
			output.write_u2(expr.values.size());
			for(java.util.Map.Entry<Value,Value> e : expr.values.entrySet()) {
				write(e.getKey());
				write(e.getValue());
			}
		}
		
		public void write(Value.Record expr) throws IOException {
			output.write_u1(RECORDVAL);
			output.write_u2(expr.values.size());
			for(java.util.Map.Entry<String,Value> v : expr.values.entrySet()) {
				output.write_u2(stringCache.get(v.getKey()));
				write(v.getValue());
			}
		}
		
		public void write(Value.Tuple expr) throws IOException {
			output.write_u1(TUPLEVAL); // FIXME: should be TUPLE!!!
			output.write_u2(expr.values.size());
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
	// Value Identifiers
	// =========================================================================

	public final static int NULL = 0;
	public final static int TRUE = 1;
	public final static int FALSE = 2;
	public final static int CHARVAL = 3;
	public final static int INTVAL = 4;
	public final static int REALVAL = 5;
	public final static int SETVAL = 6;
	public final static int LISTVAL = 7;
	public final static int RECORDVAL = 8;
	public final static int TUPLEVAL = 9;
	public final static int BYTEVAL = 10;
	public final static int STRINGVAL = 11;
	public final static int MAPVAL = 12;
	public final static int FUNCTIONVAL = 13;
	public final static int METHODVAL = 14;
	public final static int MESSAGEVAL = 15;
}
