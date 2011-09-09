package wyil.util;

import java.io.*;
import java.util.ArrayList;

import wyautl.lang.*;
import wyautl.lang.DefaultInterpretation.Value;
import wyautl.util.Tester;
import wyil.lang.Type;



public class TypeTester {	
	private static final DefaultInterpretation interpretation = new DefaultInterpretation();
	
	public static boolean isModelSubtype(Automata a1, Automata a2, ArrayList<Value> model) {
		for(Value v : model) {
			if(interpretation.accepts(a2,v) && !interpretation.accepts(a1,v)) {
				return false;
			}
		}
		return true;
	}
	
	public static void generateTests(ArrayList<Automata> types, ArrayList<Value> model) {
		int count = 1;
		for(int i=0;i!=types.size();++i) {
			Automata a1 = types.get(i);
			Type t1 = Type.construct(types.get(i));
			for(int j=0;j<types.size();++j) {
				Automata a2 = types.get(j);
				Type t2 = Type.construct(types.get(j));
				System.out.println("\tpublic @Test void test_" + count++ + "() {");
				if(isModelSubtype(a1,a2,model)) {								
					System.out.println("\t\tcheckIsSubtype(\"" + t1 + "\",\"" + t2 + "\");");
				} else {
					System.out.println("\t\tcheckNotSubtype(\"" + t1 + "\",\"" + t2 + "\");");
				}
				System.out.println("\t}");
			}
		}
	}
		
	public static boolean verbose = false;
	
	public static void main(String[] args) {
		try {			
			boolean binaryIn = true;
			int index = 0;
			ArrayList<DefaultInterpretation.Value> model = Tester.readModel(binaryIn,args[index],verbose);
			ArrayList<Automata> types = Tester.readAutomatas(binaryIn,args[index+1],verbose);
			generateTests(types,model);
			
		} catch(IOException e) {
			
		}
	}
}
