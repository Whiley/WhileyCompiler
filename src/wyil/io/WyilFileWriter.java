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
	
	private ArrayList<TYPE_Item> typePool = new ArrayList<TYPE_Item>();
	private HashMap<Type,Integer> typeCache = new HashMap<Type,Integer>();
	
	@Override
	public void apply(WyilFile module) throws IOException {
		String filename = module.filename().replace(".whiley", ".wyasm");
		output = new BinaryOutputStream(new FileOutputStream(filename));
		
		buildPools(module);
		
		writeHeader(module);
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
		output.write(0x57); // W
		output.write(0x59); // Y
		output.write(0x49); // I
		output.write(0x4C); // L
		output.write(0x46); // F
		output.write(0x49); // I
		output.write(0x4C); // L
		output.write(0x45); // E
		
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
		
		// TODO: this could be made way more efficient.
		
		Integer index = typeCache.get(t);
		if(index == null) {
			int i = typePool.size();
			typeCache.put(t, i);
			
			TYPE_Kind kind;
			int[] children = null;
			if(t instanceof Type.Null) {
				kind = TYPE_Kind.NULL;
			} else if(t instanceof Type.Bool) {
				kind = TYPE_Kind.BOOL;
			} else if(t instanceof Type.Byte) {
				kind = TYPE_Kind.BYTE;
			} else if(t instanceof Type.Char) {
				kind = TYPE_Kind.CHAR;
			} else if(t instanceof Type.Int) {
				kind = TYPE_Kind.INT;
			} else if(t instanceof Type.Real) {
				kind = TYPE_Kind.RATIONAL;
			} else if(t instanceof Type.Strung) {
				kind = TYPE_Kind.STRING;
			} else if(t instanceof Type.List) {				
				Type.List l = (Type.List) t;
				kind = TYPE_Kind.LIST;
				children = new int[1];				
				children[0] = addTypeItem(l.element());				
			} else if(t instanceof Type.Set) {
				Type.Set l = (Type.Set) t;
				kind = TYPE_Kind.SET;
				children = new int[1];				
				children[0] = addTypeItem(l.element());
			} else if(t instanceof Type.Map) {
				Type.Map l = (Type.Map) t;
				kind = TYPE_Kind.MAP;
				children = new int[2];				
				children[0] = addTypeItem(l.key());
				children[1] = addTypeItem(l.value());
			} else if(t instanceof Type.Tuple) {
				Type.Tuple l = (Type.Tuple) t;
				kind = TYPE_Kind.TUPLE;				
				List<Type> elements = l.elements();
				children = new int[elements.size()];
				for(int k=0;k!=elements.size();++k) {
					children[k] = addTypeItem(elements.get(k));
				}
			} else if(t instanceof Type.Record) {
				Type.Record l = (Type.Record) t;
				kind = TYPE_Kind.RECORD;				
				HashSet<String> fields = l.keys();
				children = new int[fields.size()*2];
				int k = 0;
				for(String field : fields) {
					children[k] = addTypeItem(l.field(field));
					children[k+1] = addStringItem(field);
					k = k + 2;
				}
			} else if(t instanceof Type.Function) {
				kind = TYPE_Kind.FUNCTION;
				// TODO:
			} else if(t instanceof Type.Method) {
				kind = TYPE_Kind.METHOD;
				// TODO:
			} else if(t instanceof Type.Reference) {
				Type.Reference l = (Type.Reference) t;
				kind = TYPE_Kind.REFERENCE;
				children = new int[1];				
				children[0] = addTypeItem(l.element());
			} else if(t instanceof Type.Negation) {
				Type.Negation l = (Type.Negation) t;
				kind = TYPE_Kind.NEGATION;
				children = new int[1];				
				children[0] = addTypeItem(l.element());
			} else if(t instanceof Type.EffectiveList) {
				kind = TYPE_Kind.EFFECTIVE_LIST;
			} else if(t instanceof Type.Union) {					
				if(t instanceof Type.EffectiveSet) {
					kind = TYPE_Kind.EFFECTIVE_SET;
				} else if(t instanceof Type.EffectiveMap) {
					kind = TYPE_Kind.EFFECTIVE_MAP;
				} else if(t instanceof Type.EffectiveTuple) {
					kind = TYPE_Kind.EFFECTIVE_TUPLE;
				} else if(t instanceof Type.EffectiveRecord) {
					kind = TYPE_Kind.EFFECTIVE_RECORD;
				} else if(t instanceof Type.EffectiveIndexible) {
					kind = TYPE_Kind.EFFECTIVE_INDEXIBLE;
				} else if(t instanceof Type.EffectiveCollection) {
					kind = TYPE_Kind.EFFECTIVE_COLLECTION;
				} else {
					kind = TYPE_Kind.UNION;
				}
				Type.Union u = (Type.Union) t;
				HashSet<Type> bounds = u.bounds();
				children = new int[bounds.size()];
				int k = 0;
				for(Type bound : bounds) {
					children[k++] = addTypeItem(bound);
				}
			} else {
				throw new IllegalArgumentException("unknown type encountered");
			}
			typePool.add(new TYPE_Item(kind,children));
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
	
	private enum TYPE_Kind {
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
		REFERENCE(12),
		FUNCTION(13),
		METHOD(14),
		NOMINAL(15),		
		NEGATION(16),
		UNION(17),
		EFFECTIVE_COLLECTION(18),
		EFFECTIVE_INDEXIBLE(19),
		EFFECTIVE_LIST(20),
		EFFECTIVE_SET(21),
		EFFECTIVE_MAP(22),
		EFFECTIVE_TUPLE(23),
		EFFECTIVE_RECORD(24);

		private final int kind;

		private TYPE_Kind(int kind) {
			this.kind = kind;
		}

		public int kind() {
			return kind;
		}
	}
	
	/**
	 * A pool item represents a WYIL type. For example, a <code>int</code> type
	 * or <code>[int]</code> (i.e. list of int) type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	private class TYPE_Item {
		/**
		 * Type kind (e.g. INT, REAL, etc)
		 */
		public final TYPE_Kind kind;
		
		/**
		 * Indices of any children in type pool
		 */
		public final int[] children;
		
		public TYPE_Item(TYPE_Kind kind, int[] children) {
			this.kind = kind;
			this.children = Arrays.copyOf(children, children.length);
		}
	}
}
