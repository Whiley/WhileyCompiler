package wyts.util;

import java.util.*;

import wyts.lang.Automata;

public class TypeTester {
	public static final SemanticModel.Config MODEL_CONFIG = new SemanticModel.Config() {
		{
			this.MAX_DEPTH = 2;
			
			this.MIN_INT = 0;
			this.MAX_INT = 1;
			this.MAX_LIST = 2;
			this.MAX_SET = 2;
			this.MAX_ELEMS = 2;
			this.MAX_FIELDS = 2;
			
			this.BOOLS = false;
			this.BYTES = false;
			this.CHARS = false;
			this.REALS = false;
			this.TUPLES = false;
			this.SETS = false;
			this.LISTS = true;
			this.DICTIONARIES = false;
		}
	};
	
	public static final Generator.Config TYPE_CONFIG = new Generator.Config() {
		{
			this.MAX_DEPTH = 3;
			
			this.MAX_FIELDS = 2;
			this.MAX_UNIONS = 2;
			this.MAX_TUPLES = 2;	
			
			this.ANY = true;
			this.BOOL = false;
			this.BYTE = false;
			this.CHAR = false;
			this.INT = true;
			this.REAL = false;
			this.TUPLES = false;
			this.SETS = false;
			this.LISTS = true;
			this.DICTIONARIES = false;
			this.RECURSIVES = true;
		}
	};
	
	public static List<Automata> minimise(List<Automata> types) {
		HashSet<Automata> visited = new HashSet<Automata>();
		ArrayList<Automata> ntypes = new ArrayList<Automata>();
		for(Automata t : types) {
			t = Automata.minimise(t);
			if(!visited.contains(t)) {
				ntypes.add(t);
				visited.add(t);
			}
		}
		return ntypes;
	}
	
	public static void main(String[] args) {
		SemanticModel model = SemanticModel.generate(MODEL_CONFIG);
		System.out.println("Generated " + model.size() + " values.");		
		List<Automata> types = Generator.generate(TYPE_CONFIG);
		System.out.println("Generated " + types.size() + " types.");
		types = minimise(types);
		System.out.println("Reduced to " + types.size() + " types.");
		
		int increment = Math.max(1, types.size() / 50);
		
		System.out.print("[");
		for(int i=0;i!=types.size();++i) {
			if((i%increment) == 0) {
				System.out.print(" ");
			}			
			Automata t = types.get(i);
			//System.out.println("TYPE: " + t);
			//PrintBuilder tp = new PrintBuilder(System.out);
			//Type.build(tp, t);
			
		}
		System.out.print("]\r[");
		
		for(int i=0;i!=types.size();++i) {
			if((i%increment) == 0) {
				System.out.print(".");
			}
			for(int j=0;j!=types.size();++j) {			
				Automata t1 = types.get(i);
				Automata t2 = types.get(j);
				boolean isSubtype = Automata.isSubtype(t1,t2);
				boolean isSubset = model.isSubset(t1,t2);
				
				if(isSubtype && !isSubset) {
					System.out.println("Unsound: " + t2 + " <: " + t1  + ", but not " + t2 + " {= " + t1);
				} else if(!isSubtype && isSubtype) {
					System.out.println("Incomplete: " + t2 + " {= " + t1 + ", but not " + t2 + " <: " + t1);
				}								
			}
		}
	}
}
