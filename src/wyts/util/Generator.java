package wyts.util;

import java.util.*;
import wyil.lang.*;
import wyts.lang.Node;
import wyts.lang.Automata;

/**
 * The generator class is used generate lists of types, primarily for testing
 * purposes.
 * 
 * @author djp
 * 
 */
public class Generator {

	public static class Config {
		
		/**
		 * Flag to include NULL in the types generated.
		 */
		public boolean ANY = true;
		
		/**
		 * Flag to include NULL in the types generated.
		 */
		public boolean NULL = true;
		
		/**
		 * Flag to include bool in the types generated.
		 */
		public boolean BOOL = true;
		
		/**
		 * Flag to include int in the types generated.
		 */
		public boolean INT = true;
		
		/**
		 * Flag to include real in the types generated.
		 */
		public boolean REAL = true;
		
		/**
		 * Flag to include char in the types generated.
		 */
		public boolean CHAR = true;
		
		/**
		 * Flag to include byte in the types generated.
		 */
		public boolean BYTE = true;
		
		/**
		 * Flag to include tuples in the types generated.
		 */
		public boolean TUPLES = true;
		
		/**
		 * Flag to include lists in the types generated.
		 */
		public boolean LISTS = true;
		
		/**
		 * Flag to include sets in the types generated.
		 */
		public boolean SETS = true;
		
		/**
		 * Flag to include dictionaries in the types generated.
		 */
		public boolean DICTIONARIES = true;
		
		/**
		 * Flag to include records in the types generated.
		 */
		public boolean RECORDS = true;
		
		/**
		 * Flag to include unions in the types generated.
		 */
		public boolean UNIONS = true;
		
		/**
		 * Flag to include recursives in the types generated.
		 */
		public boolean RECURSIVES = true;
		
		/**
		 * MAX_FIELDS defines the maximum number of fields to explore per
		 * record.
		 */
		public int MAX_FIELDS = 2;

		/**
		 * MAX_UNIONS defines the maximum number of disjuncts in a union that
		 * can be explored.
		 */
		public int MAX_UNIONS = 2;
		
		/**
		 * MAX_TUPLES defines the maximum number of elements in a tuple that
		 * can be explored.
		 */
		public int MAX_TUPLES = 2;
		
		/**
		 * MAX_DEPTH defines the maximum depth of a type.
		 */
		public int MAX_DEPTH = 2;			
	}
	
	public static List<Automata> generate(Config config) {
		ArrayList<Automata> types = new ArrayList<Automata>();
		int RECURSIVE_ROOT = -1;
		// types.add(Type.T_VOID);
		if(config.ANY) {
			types.add(Automata.T_ANY);
		}
		if(config.NULL) {		
			types.add(Automata.T_NULL);
		}
		if(config.BYTE) {
			types.add(Automata.T_BYTE);
		}
		if(config.CHAR){ 
			types.add(Automata.T_CHAR);
		}
		if(config.INT) { 
			types.add(Automata.T_INT);
		}
		if(config.REAL) { 
			types.add(Automata.T_REAL);
		}
		if(config.RECURSIVES) {
			RECURSIVE_ROOT = types.size();
			types.add(Automata.T_LABEL("X"));
		}
		//types.add(Type.T_STRING);
		
		for(int i=0;i!=config.MAX_DEPTH;++i) {
			int end = types.size();
			if(config.LISTS) { addListTypes(types,end); }
			if(config.SETS) { addSetTypes(types,end); }
			//addProcessTypes(types,end);
			if(config.TUPLES) { addTupleTypes(types,config.MAX_TUPLES,end); }
			if(config.RECORDS) { addRecordTypes(types,config.MAX_FIELDS,end); }
			if(config.UNIONS) { addUnionTypes(types,config.MAX_UNIONS,end); } 
			if(config.RECURSIVES) { addRecursiveTypes(RECURSIVE_ROOT,types,end); } 
		}
		
		for(int i=0;i!=types.size();++i) {
			Automata t = types.get(i);
			if(Automata.isOpen(t)) {
				// yuk ... should be an easier way of doing this!
				if(t instanceof Automata.Compound && ((Automata.Compound)t).nodes[0].kind == Node.K_LABEL) {
					types.remove(i--);
				} else {
					types.set(i,Automata.T_RECURSIVE("X",t));
				}
			}
		}
		return types;
	}
	
	private static void addRecursiveTypes(int root, ArrayList<Automata> types, int end) {
		for(int i=0;i!=end;++i) {
			Automata t = types.get(i);
			if(i != root && Automata.isOpen("X",t)) {				
				types.add(Automata.T_RECURSIVE("X", t));
			}
		}
	}
	
	private static void addListTypes(ArrayList<Automata> types, int end) {
		for(int i=0;i!=end;++i) {
			Automata t = types.get(i);
			types.add(Automata.T_LIST(t));
		}
	}
	
	private static void addSetTypes(ArrayList<Automata> types, int end) {
		for(int i=0;i!=end;++i) {
			Automata t = types.get(i);
			types.add(Automata.T_SET(t));
		}
	}
	
	private static void addProcessTypes(ArrayList<Automata> types, int end) {
		for(int i=0;i!=end;++i) {
			Automata t = types.get(i);
			types.add(Automata.T_PROCESS(t));
		}
	}
	
	private static void addTupleTypes(ArrayList<Automata> types, int MAX_TUPLES, int end) {
		for(int i=2;i<=MAX_TUPLES;++i) {
			Automata[] elems = new Automata[i];
			addTupleTypes(elems,types,0,end);			
		}
	}
	
	private static void addTupleTypes(Automata[] elems, ArrayList<Automata> types, int dim, int end) {
		if(dim == elems.length) {
			types.add(Automata.T_TUPLE(elems));
		} else {
			for(int i=0;i!=end;++i) {
				elems[dim] = types.get(i);
				addTupleTypes(elems,types,dim+1,end);
			}
		}
	}
	
	private static void addRecordTypes(ArrayList<Automata> types, int MAX_FIELDS, int end) {
		for(int i=1;i<=MAX_FIELDS;++i) {
			Automata[] elems = new Automata[i];
			addRecordTypes(elems,types,0,end);			
		}
	}
	
	private static void addRecordTypes(Automata[] elems, ArrayList<Automata> types, int dim, int end) {
		if(dim == elems.length) {
			HashMap<String,Automata> fields = new HashMap<String,Automata>();
			for(int i=0;i!=elems.length;++i) {
				fields.put("field" + i,elems[i]);
			}
			types.add(Automata.T_RECORD(fields));
		} else {
			for(int i=0;i!=end;++i) {
				elems[dim] = types.get(i);
				addRecordTypes(elems,types,dim+1,end);
			}
		}
	}
	
	private static void addUnionTypes(ArrayList<Automata> types, int MAX_TUPLES, int end) {
		for(int i=2;i<=MAX_TUPLES;++i) {
			Automata[] elems = new Automata[i];
			addUnionTypes(elems,types,0,end);			
		}
	}
	
	private static void addUnionTypes(Automata[] elems, ArrayList<Automata> types, int dim, int end) {
		if(dim == elems.length) {
			types.add(Automata.T_UNION(elems));
		} else {
			for(int i=0;i!=end;++i) {
				elems[dim] = types.get(i);
				addUnionTypes(elems,types,dim+1,end);
			}
		}
	}
	
	public static void main(String[] args) {
		Config config = new Config();
		config.MAX_FIELDS = 2;
		config.MAX_UNIONS = 2;
		config.MAX_TUPLES = 2;
		config.MAX_DEPTH = 2;
		
		List<Automata> types = generate(config);
		for(Automata t : types) {
			System.out.println(t);
		}
		
		System.out.println("Generated " + types.size() + " types");
	}
}
