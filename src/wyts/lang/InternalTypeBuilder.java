package wyts.lang;

import static wyts.lang.Node.*;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.util.Pair;
import wyts.util.TypeBuilder;

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
	
	public void buildIntersection(int index, int... bounds) {
		nodes[index] = new Node(K_INTERSECTION,bounds);
	}
	
	public void buildDifference(int index, int left, int right) {
		int[] bounds = new int[]{left,right};
		nodes[index] = new Node(K_UNION,bounds);
	}
}
