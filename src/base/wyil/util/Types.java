package wyil.util;

import java.io.IOException;

import wyil.lang.NameID;
import wyil.lang.Type;
import wyjvm.io.BinaryOutputStream;

public class Types {

	/**
	 * The BinaryBuilder converts a type into a short binary string.
	 * 
	 * @author djp
	 * 
	 */
	public static class BinaryBuilder implements Type.Builder {		
		private final BinaryOutputStream writer;	
		
		public BinaryBuilder(BinaryOutputStream writer) {
			this.writer = writer;			
		}
		
		public void initialise(int numNodes) {
			try {
				writeLength(numNodes);
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildPrimitive(int index, Type.Leaf t) {
			try {
				if(t == Type.T_ANY) {
					writeKind(ANY_TYPE );
				} else if(t == Type.T_VOID) {
					writeKind(VOID_TYPE);
				} else if(t == Type.T_NULL) {
					writeKind(NULL_TYPE );
				} else if(t == Type.T_BOOL) {
					writeKind(BOOL_TYPE );			
				} else if(t == Type.T_INT) {			
					writeKind(INT_TYPE );		
				} else if(t == Type.T_REAL) {
					writeKind(REAL_TYPE );			
				} else {
					throw new RuntimeException("unknown type encountered: " + t);		
				}
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}			
		}

		public void buildExistential(int index, NameID name) {
			try {
				writeKind(EXISTENTIAL_TYPE);				
				writeIdentifier(name.module().toString());
				writeIdentifier(name.name());				
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildSet(int index, int element) {
			try {
				writeKind(SET_TYPE);			
				writeNode(element);
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildList(int index, int element) {
			try {
				writeKind(LIST_TYPE);
				writeNode(element);
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildProcess(int index, int element) {
			try {
				writeKind(PROCESS_TYPE);	
				writeNode(element);				
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildDictionary(int index, int key, int value) {
			try {
				writeKind(DICTIONARY_TYPE);
				writeNode(key);
				writeNode(value);
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildTuple(int index, int... elements) {
			try {
				writeKind(TUPLE_TYPE);
				// FIXME: bug here if number of entries > 64K
				writeNode(elements.length);
				for(int e : elements) {					
					writeNode(e);					
				}	
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildRecord(int index, Pair<String, Integer>... fields) {
			try {				
				writeKind(RECORD_TYPE );
				// FIXME: bug here if number of entries > 64K
				writeLength(fields.length);
				for(Pair<String,Integer> p : fields) {
					writeIdentifier(p.first());										
					writeNode(p.second());					
				}			
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}

		public void buildFunction(int index, int receiver, int ret,
				int... parameters) {
			try {
				if (receiver != -1) {
					writeKind(METH_TYPE);
					writeNode(receiver);
				} else {
					writeKind(FUN_TYPE);
				}
				writeKind(ret);
				writeLength(parameters.length);
				for (int p : parameters) {
					writeNode(p);
				}
			} catch (IOException e) {
				throw new RuntimeException("internal failure", e);
			}
		}

		public void buildUnion(int index, int... bounds) {
			try {				
				writeKind(UNION_TYPE );			
				writeLength(bounds.length);
				for(int b : bounds) {
					writeNode(b);
				}	
			} catch(IOException e) {
				throw new RuntimeException("internal failure",e);
			}
		}
		
		protected void writeKind(int kind) throws IOException {
			writer.write_un(kind,4);
		}
		
		protected void writeLength(int len) throws IOException {
			writer.write_uv(len);
		}
		
		protected void writeNode(int node) throws IOException {
			writer.write_uv(node);			
		}
		
		/**
		 * An identifier is a string made up of characters from
		 * [A-Za-z_][A-Za-z0-9_]*
		 * 
		 * @param identifier
		 * @throws IOException
		 */
		protected void writeIdentifier(String id) throws IOException {
			writeLength(id.length());
			for(int i=0;i!=id.length();++i) {
				writer.write_un(decode(id.charAt(i)),6);
			}
		}
		
		public static int decode(char c) {
			if(c == '$') {
				return 0;
			} else if(c >= '0' && c <= '9') {
				return (c - '0') + 1;
			} else if(c >= 'A' && c <= 'Z') {
				return (c - 'A') + 11;
			} else if(c == '_') {
				return 37;
			} else if(c >= 'a' && c <= 'z') {
				return (c - 'a') + 38;
			} else {
				throw new IllegalArgumentException("invalid character in identifier: " + c);
			}
		}
		
	}	
	
	public static final int EXISTENTIAL_TYPE = 1;
	public static final int ANY_TYPE = 2;
	public static final int VOID_TYPE = 3;
	public static final int NULL_TYPE = 4;
	public static final int BOOL_TYPE = 5;
	public static final int INT_TYPE = 6;
	public static final int REAL_TYPE = 7;
	public static final int LIST_TYPE = 8;
	public static final int SET_TYPE = 9;
	public static final int DICTIONARY_TYPE = 10;
	public static final int TUPLE_TYPE = 11;
	public static final int RECORD_TYPE = 12;
	public static final int UNION_TYPE = 13;
	public static final int INTERSECTION_TYPE = 14;
	public static final int PROCESS_TYPE = 15;	
	public static final int FUN_TYPE = 16;
	public static final int METH_TYPE = 17;
	public static final int CONSTRAINT_MASK = 32;
}
