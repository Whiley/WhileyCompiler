package wyil.lang.type;

import java.util.*;
import wyil.lang.Type;

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
			this.LISTS = false;
			this.DICTIONARIES = false;
		}
	};
	
	public static final Generator.Config TYPE_CONFIG = new Generator.Config() {
		{
			this.MAX_DEPTH = 2;
			
			this.MAX_FIELDS = 2;
			this.MAX_UNIONS = 2;
			this.MAX_TUPLES = 2;	
			
			this.BOOL = false;
			this.BYTE = false;
			this.CHAR = false;
			this.INT = false;
			this.REAL = false;
			this.TUPLES = false;
			this.SETS = false;
			this.LISTS = false;
			this.DICTIONARIES = false;
		}
	};
	
	public static void main(String[] args) {
		SemanticModel model = SemanticModel.generate(MODEL_CONFIG);
		System.out.println("Generated " + model.size() + " values.");
		List<Type> types = Generator.generate(TYPE_CONFIG);
		System.out.println("Generated " + types.size() + " types.");
		
		int increment = types.size() / 50;
		
		System.out.print("[");
		for(int i=0;i!=types.size();++i) {
			if((i%increment) == 0) {
				System.out.print(" ");
			}
			Type t = types.get(i);			
			Type nt = Type.minimise(t);
			System.out.println("Minimised: " + t + " => " + nt);
			types.set(i, nt);
		}
		System.out.print("]\r[");
		
		for(int i=0;i!=types.size();++i) {
			if((i%increment) == 0) {
				System.out.print(".");
			}
			for(int j=0;j!=types.size();++j) {			
				Type t1 = types.get(i);
				Type t2 = types.get(j);
				System.out.println(t1 + " :> " + t2);
				boolean isSubtype = Type.isSubtype(t1,t2);
				System.out.println("Stage 2");
				boolean isSubset = model.isSubset(t1,t2);
				System.out.println("DONE");
				if(isSubtype && !isSubset) {
					System.out.println("Unsound: " + t2 + " <: " + t1  + ", but not " + t2 + " {= " + t1);
				} else if(!isSubtype && isSubtype) {
					System.out.println("Incomplete: " + t2 + " {= " + t1 + ", but not " + t2 + " <: " + t1);
				}								
			}
		}
	}
}
