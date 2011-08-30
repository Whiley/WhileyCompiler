package wyts.io;

import java.io.IOException;

import wyil.lang.ModuleID;
import wyil.lang.NameID;
import wyil.util.Pair;
import wyjvm.io.BinaryInputStream;
import wyts.lang.InternalTypeBuilder;
import wyts.lang.Node;
import wyts.lang.Type;

public class BinaryTypeReader {
	private final BinaryInputStream reader;	
	
	public BinaryTypeReader(BinaryInputStream reader) {
		this.reader = reader;			
	}
	
	public Type read() throws IOException {
		InternalTypeBuilder builder = new InternalTypeBuilder();
		int numNodes = readLength();			
		builder.initialise(numNodes);
		for(int i=0;i!=numNodes;++i) {
			int kind = readKind();				
			switch(kind) {
			case Node.K_ANY:											
				builder.buildPrimitive(i, Type.T_ANY);
				break;
			case Node.K_VOID:
				builder.buildPrimitive(i, Type.T_VOID);
				break;
			case Node.K_NULL:
				builder.buildPrimitive(i, Type.T_NULL);
				break;
			case Node.K_BOOL:
				builder.buildPrimitive(i, Type.T_BOOL);
				break;
			case Node.K_BYTE:
				builder.buildPrimitive(i, Type.T_BYTE);
				break;
			case Node.K_CHAR:
				builder.buildPrimitive(i, Type.T_CHAR);
				break;
			case Node.K_INT:
				builder.buildPrimitive(i, Type.T_INT);
				break;
			case Node.K_RATIONAL:
				builder.buildPrimitive(i, Type.T_REAL);
				break;
			case Node.K_STRING:
				builder.buildPrimitive(i, Type.T_STRING);
				break;
			case Node.K_EXISTENTIAL:
				String module = readIdentifier();
				String name = readIdentifier();
				builder.buildExistential(i, new NameID(ModuleID.fromString(module),name));
				break;
			case Node.K_SET:
				builder.buildSet(i, readNode());
				break;
			case Node.K_LIST:
				builder.buildList(i, readNode());
				break;
			case Node.K_PROCESS:
				builder.buildProcess(i, readNode());
				break;
			case Node.K_DICTIONARY:
				builder.buildDictionary(i, readNode(), readNode());
				break;
			case Node.K_TUPLE: {
				int nelems = readLength();
				int[] elems = new int[nelems];
				for(int j=0;j!=nelems;++j) {
					elems[j] = readNode();
				}
				builder.buildTuple(i,elems);
				break;
			}
			case Node.K_RECORD: {
				int nelems = readLength();
				Pair<String,Integer>[] elems = new Pair[nelems];
				for(int j=0;j!=nelems;++j) {
					elems[j] = new Pair(readIdentifier(),readNode());
				}
				builder.buildRecord(i,elems);
				break;
			}
			case Node.K_FUNCTION: {
				int ret = readNode();
				int nelems = readLength();
				int[] elems = new int[nelems];
				for(int j=0;j!=nelems;++j) {
					elems[j] = readNode();
				}
				builder.buildFunction(i,ret,elems);
				break;
			}			
			case Node.K_HEADLESS: {									
				int ret = readNode();
				int nelems = readLength();
				int[] elems = new int[nelems];
				for(int j=0;j!=nelems;++j) {
					elems[j] = readNode();
				}
				builder.buildMethod(i,-1,ret,elems);
				break;
			}
			case Node.K_METHOD: {					
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
			case Node.K_UNION: {
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
