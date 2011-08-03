package wyil.util;

import java.io.IOException;
import java.util.*;

import wyil.lang.ModuleID;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyjvm.io.*;

public class Types {

	public static class BinaryReader {
		private final BinaryInputStream reader;	
		
		public BinaryReader(BinaryInputStream reader) {
			this.reader = reader;			
		}
		
		public Type read() throws IOException {
			Type.InternalBuilder builder = new Type.InternalBuilder();
			int numNodes = readLength();			
			builder.initialise(numNodes);
			for(int i=0;i!=numNodes;++i) {
				int kind = readKind();				
				switch(kind) {
				case ANY_TYPE:											
					builder.buildPrimitive(i, Type.T_ANY);
					break;
				case VOID_TYPE:
					builder.buildPrimitive(i, Type.T_VOID);
					break;
				case NULL_TYPE:
					builder.buildPrimitive(i, Type.T_NULL);
					break;
				case BOOL_TYPE:
					builder.buildPrimitive(i, Type.T_BOOL);
					break;
				case BYTE_TYPE:
					builder.buildPrimitive(i, Type.T_BYTE);
					break;
				case CHAR_TYPE:
					builder.buildPrimitive(i, Type.T_CHAR);
					break;
				case INT_TYPE:
					builder.buildPrimitive(i, Type.T_INT);
					break;
				case REAL_TYPE:
					builder.buildPrimitive(i, Type.T_REAL);
					break;
				case STRING_TYPE:
					builder.buildPrimitive(i, Type.T_STRING);
					break;
				case EXISTENTIAL_TYPE:
					String module = readIdentifier();
					String name = readIdentifier();
					builder.buildExistential(i, new NameID(ModuleID.fromString(module),name));
					break;
				case SET_TYPE:
					builder.buildSet(i, readNode());
					break;
				case LIST_TYPE:
					builder.buildList(i, readNode());
					break;
				case PROCESS_TYPE:
					builder.buildProcess(i, readNode());
					break;
				case DICTIONARY_TYPE:
					builder.buildDictionary(i, readNode(), readNode());
					break;
				case TUPLE_TYPE: {
					int nelems = readLength();
					int[] elems = new int[nelems];
					for(int j=0;j!=nelems;++j) {
						elems[j] = readNode();
					}
					builder.buildTuple(i,elems);
					break;
				}
				case RECORD_TYPE: {
					int nelems = readLength();
					Pair<String,Integer>[] elems = new Pair[nelems];
					for(int j=0;j!=nelems;++j) {
						elems[j] = new Pair(readIdentifier(),readNode());
					}
					builder.buildRecord(i,elems);
					break;
				}
				case FUN_TYPE: {
					int ret = readNode();
					int nelems = readLength();
					int[] elems = new int[nelems];
					for(int j=0;j!=nelems;++j) {
						elems[j] = readNode();
					}
					builder.buildFunction(i,ret,elems);
					break;
				}
				case METH_TYPE: {					
					int rec = readNode();
					int ret = readNode();
					int nelems = readLength();
					int[] elems = new int[nelems];
					for(int j=0;j!=nelems;++j) {
						elems[j] = readNode();
					}
					builder.buildMethod(i,rec,ret,elems);
					break;
				}
				case UNION_TYPE: {
					int nelems = readLength();
					int[] elems = new int[nelems];
					for(int j=0;j!=nelems;++j) {
						elems[j] = readNode();
					}
					builder.buildUnion(i,elems);
					break;
				}
				}
			}
			return builder.type();						
		}
		
		private int readLength() throws IOException {
			return reader.read_uv();
		}
		
		public int readKind() throws IOException {
			return reader.read_un(5);
		}
		
		private int readNode() throws IOException {
			return reader.read_uv();
		}
	
		/**
		 * An identifier is a string made up of characters from
		 * [A-Za-z_][A-Za-z0-9_]*
		 * 
		 * @param identifier
		 * @throws IOException
		 */
		protected String readIdentifier() throws IOException {
			int num = readLength();			
			StringBuilder buf = new StringBuilder();
			for(int i=0;i!=num;++i) {
				buf.append((char) reader.read_un(7));
			}
			return buf.toString();
		}		
	}
	
	/**
	 * The BinaryBuilder converts a type into a short binary string.
	 * 
	 * @author djp
	 * 
	 */
	public static class BinaryWriter implements Type.Builder {		
		private final BinaryOutputStream writer;	
		
		public BinaryWriter(BinaryOutputStream writer) {
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
				} else if(t == Type.T_BYTE) {			
					writeKind(BYTE_TYPE );		
				} else if(t == Type.T_CHAR) {			
					writeKind(CHAR_TYPE );		
				} else if(t == Type.T_INT) {			
					writeKind(INT_TYPE );		
				} else if(t == Type.T_REAL) {
					writeKind(REAL_TYPE );			
				} else if(t == Type.T_STRING) {
					writeKind(STRING_TYPE);			
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
				writeLength(elements.length);
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

		public void buildFunction(int index, int ret,
				int... parameters) {
			try {				
				writeKind(FUN_TYPE);				
				writeNode(ret);
				writeLength(parameters.length);
				for (int p : parameters) {
					writeNode(p);
				}
			} catch (IOException e) {
				throw new RuntimeException("internal failure", e);
			}
		}

		public void buildMethod(int index, int receiver, int ret,
				int... parameters) {
			try {				
				writeKind(METH_TYPE);
				writeNode(receiver);				
				writeNode(ret);
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
			writer.write_un(kind,5);
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
				writer.write_un(id.charAt(i),7);
			}
		}		
	}	
	
	public static final int EXISTENTIAL_TYPE = 1;
	public static final int ANY_TYPE = 2;
	public static final int VOID_TYPE = 3;
	public static final int NULL_TYPE = 4;
	public static final int BOOL_TYPE = 5;
	public static final int BYTE_TYPE = 6;
	public static final int CHAR_TYPE = 7;
	public static final int INT_TYPE = 8;
	public static final int REAL_TYPE = 9;
	public static final int STRING_TYPE = 10;
	public static final int LIST_TYPE = 11;
	public static final int SET_TYPE = 12;
	public static final int DICTIONARY_TYPE = 13;
	public static final int TUPLE_TYPE = 14;
	public static final int RECORD_TYPE = 15;
	public static final int UNION_TYPE = 16;
	public static final int INTERSECTION_TYPE = 17;
	public static final int PROCESS_TYPE = 18;	
	public static final int FUN_TYPE = 19;
	public static final int METH_TYPE = 20;
	public static final int CONSTRAINT_MASK = 32;	
}
