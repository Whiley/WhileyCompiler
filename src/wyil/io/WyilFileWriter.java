package wyil.io;

import java.io.*;
import java.util.*;

import wyil.Transform;
import wybs.lang.Path;
import wyil.lang.*;
import wyil.util.Pair;
import wyjvm.io.BinaryOutputStream;

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
	
	private ArrayList<CONSTANT_Item> constantPool = new ArrayList<CONSTANT_Item>();
	private HashMap<Value,Integer> constantCache = new HashMap<Value,Integer>();
	
	private ArrayList<Type> typePool = new ArrayList<Type>();
	private HashMap<Type,Integer> typeCache = new HashMap<Type,Integer>();
	
	public WyilFileWriter(wybs.lang.Builder builder) {

	}
	
	@Override
	public void apply(WyilFile module) throws IOException {
		String filename = module.filename().replace(".whiley", ".wyil");
		output = new BinaryOutputStream(new FileOutputStream(filename));
		
		System.out.println("GOT HERE");
		
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
		for (String s : stringPool) {
			try {
				byte[] bytes = s.getBytes("UTF-8");
				System.out.println("WRITING: " + bytes.length);
				output.write_uv(bytes.length);				
				output.write(bytes, 0, bytes.length);
			} catch (UnsupportedEncodingException e) {
				// hmmm, this aint pretty ;)
			}
		}
	}
	
	private void writePathPool() throws IOException {
		for (PATH_Item p : pathPool) {
			output.write_uv(p.parentIndex + 1);
			output.write_uv(p.stringIndex);
		}
	}

	private void writeNamePool() throws IOException {
		for (NAME_Item p : namePool) {
			output.write_uv(p.kind.kind());
			output.write_uv(p.pathIndex);
		}
	}

	private void writeConstantPool() {

	}

	private void writeTypePool() {

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
		if(pid == null) {
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
		Integer index = constantCache.get(v);
		if(index == null) {
			int i = constantPool.size();
			constantCache.put(v, i);
			CONSTANT_Kind kind;
			int[] children = null;
			if(v instanceof Value.Null) {
				kind = CONSTANT_Kind.NULL;
			} else if(v instanceof Value.Bool) {
				kind = CONSTANT_Kind.BOOL;
			} else if(v instanceof Value.Byte) {
				kind = CONSTANT_Kind.BYTE;
			} else if(v instanceof Value.Char) {
				kind = CONSTANT_Kind.CHAR;
			} else if(v instanceof Value.Integer) {
				kind = CONSTANT_Kind.INT;
			} else if(v instanceof Value.Rational) {
				kind = CONSTANT_Kind.RATIONAL;
			} else if(v instanceof Value.Strung) {
				kind = CONSTANT_Kind.STRING;
			} else if(v instanceof Value.List) {
				Value.List l = (Value.List) v;
				kind = CONSTANT_Kind.LIST;
				children = new int[l.values.size()];
				for (int k = 0; k != children.length; ++k) {
					children[k] = addConstantItem(l.values.get(k));
				}
			} else if(v instanceof Value.Set) {
				Value.Set s = (Value.Set) v;
				kind = CONSTANT_Kind.SET;				
				children = new int[s.values.size()];
				ArrayList<Value> values = new ArrayList<Value>(s.values);
				for (int k = 0; k != children.length; ++k) {					
					children[k] = addConstantItem(values.get(k));
				}
			} else if(v instanceof Value.Map) {				
				Value.Map s = (Value.Map) v;
				kind = CONSTANT_Kind.MAP;								
				ArrayList<Value> values = new ArrayList<Value>(s.values.keySet());
				children = new int[values.size() * 2];
				for (int k = 0; k != values.size(); ++k) {				
					Value key = values.get(k);
					Value val = s.values.get(key);					
					children[k * 2] = addConstantItem(key);
					children[(k * 2) + 1] = addConstantItem(val);
				}
			} else if(v instanceof Value.Tuple) {
				Value.Tuple t = (Value.Tuple) v;
				kind = CONSTANT_Kind.TUPLE;
				children = new int[t.values.size()];
				for (int k = 0; k != children.length; ++k) {
					children[k] = addConstantItem(t.values.get(k));
				}
			} else if(v instanceof Value.Record) {
				Value.Record r = (Value.Record) v;
				kind = CONSTANT_Kind.RECORD;
				ArrayList<String> fields = new ArrayList<String>(r.values.keySet());
				children = new int[fields.size()*2];				
				for (int k = 0; k != fields.size(); ++k) {
					String field = fields.get(k);
					children[k << 1] = addStringItem(field);
					children[(k << 1) + 1] = addConstantItem(r.values.get(field));
				}
			} else if(v instanceof Value.FunctionOrMethod){
				kind = CONSTANT_Kind.FUNCTION_OR_METHOD;
				// TODO
			} else {
				throw new IllegalArgumentException("unknown value encountered");
			}
			constantPool.add(new CONSTANT_Item(kind,children));
			return i;
		} else {
			return index;
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
	
	
	private enum CONSTANT_Kind {
		NULL(0), 
		BOOL(1),
		BYTE(2),
		CHAR(3),
		INT(4),
		RATIONAL(5),
		STRING(6),
		LIST(7),
		SET(8),
		MAP(9),
		TUPLE(10),
		RECORD(11),
		FUNCTION_OR_METHOD(12);

		private final int kind;

		private CONSTANT_Kind(int kind) {
			this.kind = kind;
		}

		public int kind() {
			return kind;
		}
	}

	/**
	 * Represents a WYIL Constant value.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	private class CONSTANT_Item {
		public final CONSTANT_Kind kind;

		public final int[] children;
				
		public CONSTANT_Item(CONSTANT_Kind kind, int... children) {
			this.kind = kind;
			this.children = children;
		}
	}
}
