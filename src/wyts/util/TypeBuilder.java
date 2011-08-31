package wyts.util;

import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.util.Pair;

/**
 * The Type.Builder interface is essentially a way of separating the
 * internals of the type implementation from clients which may want to
 * serialise a given type graph.
 */
public interface TypeBuilder {

	/**
	 * Set the number of nodes required for the type being built. This
	 * method is called once, before all other methods are called. The
	 * intention is to give builders a chance to statically initialise data
	 * structures based on the number of nodes required.
	 * 
	 * @param numNodes
	 */
	void initialise(int numNodes);

	void buildPrimitive(int index, Type.Leaf type);

	void buildExistential(int index, NameID name);

	void buildSet(int index, int element);

	void buildList(int index, int element);

	void buildProcess(int index, int element);

	void buildDictionary(int index, int key, int value);

	void buildTuple(int index, int... elements);

	void buildRecord(int index, Pair<String, Integer>... fields);

	void buildFunction(int index, int ret, int... parameters);
	
	void buildMethod(int index, int receiver, int ret, int... parameters);
	
	void buildUnion(int index, int... bounds);
	
	void buildIntersection(int index, int... bounds);
	
	void buildDifference(int index, int left, int right);
}
