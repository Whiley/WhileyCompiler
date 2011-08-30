package wyts.lang.type;

import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.util.Pair;


/**
 * This class provides an empty implementation of a type builder, which is
 * useful for define simple builders.
 * 
 * @author djp
 * 
 */
public class AbstractTypeBuilder implements TypeBuilder {
	
	public void initialise(int numNodes) {
	}

	public void buildPrimitive(int index, Type.Leaf type) {
	}

	public void buildExistential(int index, NameID name) {
	}

	public void buildSet(int index, int element) {
	}

	public void buildList(int index, int element) {
	}

	public void buildProcess(int index, int element) {
	}

	public void buildDictionary(int index, int key, int value) {
	}

	public void buildTuple(int index, int... elements) {
	}

	public void buildRecord(int index, Pair<String, Integer>... fields) {
	}

	public void buildFunction(int index, int ret,
			int... parameters) {
	}
	
	public void buildMethod(int index, int receiver, int ret,
			int... parameters) {
	}

	public void buildUnion(int index, int... bounds) {
	}
	
	public void buildIntersection(int index, int... bounds) {
	}
	
	public void buildDifference(int index, int left, int right) {
	}
}
