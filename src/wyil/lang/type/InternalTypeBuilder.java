package wyil.lang.type;

import static wyil.lang.type.Node.K_DICTIONARY;
import static wyil.lang.type.Node.K_EXISTENTIAL;
import static wyil.lang.type.Node.K_FUNCTION;
import static wyil.lang.type.Node.K_LIST;
import static wyil.lang.type.Node.K_METHOD;
import static wyil.lang.type.Node.K_PROCESS;
import static wyil.lang.type.Node.K_RECORD;
import static wyil.lang.type.Node.K_SET;
import static wyil.lang.type.Node.K_TUPLE;
import static wyil.lang.type.Node.K_UNION;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.util.Pair;

/**
 * The internal builder is essentially the way we deserialise types. That
 * is, clients create an instance of internal builder and then call the
 * methods (as directed by their own type representation). At the end, we
 * have a fully built type --- neat!
 * 
 * @author djp
 * 
 */
public class InternalTypeBuilder implements TypeBuilder {	
	private Node[] nodes;
	
	public Type type() {
		return Type.construct(nodes);
	}	
	public void initialise(int numNodes) {
		nodes = new Node[numNodes];
	}
	public void buildPrimitive(int index, Type.Leaf type) {
		nodes[index] = new Node(Node.leafKind(type),null);
	}
	public void buildExistential(int index, NameID name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"existential name cannot be null");
		}
		nodes[index] = new Node(K_EXISTENTIAL,name);
	}

	public void buildSet(int index, int element) {
		nodes[index] = new Node(K_SET,element);
	}

	public void buildList(int index, int element) {
		nodes[index] = new Node(K_LIST,element);
	}

	public void buildProcess(int index, int element) {
		nodes[index] = new Node(K_PROCESS,element);
	}

	public void buildDictionary(int index, int key, int value) {
		nodes[index] = new Node(K_DICTIONARY,new Pair(key,value));
	}

	public void buildTuple(int index, int... elements) {
		nodes[index] = new Node(K_TUPLE,elements);
	}

	public void buildRecord(int index, Pair<String, Integer>... fields) {
		nodes[index] = new Node(K_RECORD,fields);
	}

	public void buildFunction(int index, int ret, int... parameters) {
		int[] items = new int[parameters.length+2];
		items[0] = -1;
		items[1] = ret;
		System.arraycopy(parameters,0,items,2,parameters.length);
		nodes[index] = new Node(K_FUNCTION,items);
	}

	public void buildMethod(int index, int receiver, int ret,
			int... parameters) {
		int[] items = new int[parameters.length+2];
		items[0] = receiver;
		items[1] = ret;
		System.arraycopy(parameters,0,items,2,parameters.length);
		nodes[index] = new Node(K_METHOD,items);
	}
	
	public void buildUnion(int index, int... bounds) {
		nodes[index] = new Node(K_UNION,bounds);
	}
}
