package wyil.lang.type;

import java.util.*;
import wyil.lang.*;

/**
 * The generator class is used generate lists of types, primarily for testing
 * purposes.
 * 
 * @author djp
 * 
 */
public class Generator {

	public static final class Config {
		/**
		 * MAX_FIELDS defines the maximum number of fields to explore per
		 * record.
		 */
		public int MAX_FIELDS;

		/**
		 * MAX_UNIONS defines the maximum number of disjuncts in a union that
		 * can be explored.
		 */
		public int MAX_UNIONS;
		
		/**
		 * MAX_TUPLES defines the maximum number of elements in a tuple that
		 * can be explored.
		 */
		public int MAX_TUPLES;
		
		/**
		 * MAX_DEPTH defines the maximum depth of a type.
		 */
		public int MAX_DEPTH;			
	}
	
	public static List<Type> generate(Config config) {
		ArrayList<Type> types = new ArrayList<Type>();
		generate(0,new ArrayList<Type.Node>(),config,types);
		return types;
	}
	
	private static int generate(int depth, ArrayList<Type.Node> nodes, Config config, ArrayList<Type> types) {
		if(depth == config.MAX_DEPTH) {
			generateLeaf(types);
		}
	}
	
	private static void generateLeaf()
}
