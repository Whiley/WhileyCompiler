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
		}
	};
	
	public static final Generator.Config TYPE_CONFIG = new Generator.Config() {
		{
			this.MAX_FIELDS = 2;
			this.MAX_UNIONS = 2;
			this.MAX_TUPLES = 2;
			this.MAX_DEPTH = 2;
		}
	};
	
	public static void main(String[] args) {
		SemanticModel model = SemanticModel.generate(MODEL_CONFIG);
		System.out.println("Generated " + model.size() + " values.");
		List<Type> types = Generator.generate(TYPE_CONFIG);
		System.out.println("Generated " + types.size() + " types.");
		
		for(int i=0;i!=types.size();++i) {
			for(int j=0;j!=types.size();++j) {
				Type t1 = types.get(i);
				Type t2 = types.get(j);
				boolean isSubtype = Type.isSubtype(t1,t2);
				boolean isSubset = model.isSubset(t1,t2);
				
				if(isSubtype && !isSubset) {
					System.out.println("Unsound: " + t1 + " :> " + t2);
				} else if(!isSubtype && isSubtype) {
					System.out.println("Incomplete: " + t1 + " :> " + t2);
				}								
			}
		}
	}
}
