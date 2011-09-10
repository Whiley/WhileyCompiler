package wyil.util;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;

import wyautl.lang.*;
import wyautl.lang.DefaultInterpretation.Value;
import wyautl.util.Tester;
import wyil.lang.Type;



public class TypeTester {	

	/**
	 * In the type interpretation, we must override the default interpretation
	 * to deal with union, intersection, negation, any, void, list and set
	 * types.
	 * 
	 * @author djp
	 * 
	 */
	public static class TypeInterpretation extends DefaultInterpretation {
		public boolean accepts(int index, Automata automata, Value value) {			
			Automata.State state = automata.states[index];
			switch(state.kind) {
			case Type.K_ANY:
				return true; // easy
			case Type.K_VOID:
				return false; // easy
			case Type.K_LIST:
			case Type.K_SET: {
				if(value.kind != state.kind) { 
					return false;
				}
				int child = automata.states[index].children[0];
				Value[] values = value.children;				
				for(int i=0;i!=values.length;++i) {									
					Value vchild = values[i];
					if(!accepts(child,automata,vchild)) {
						return false;
					}
				}
				return true;
			}
			case Type.K_NOT: {
				int child = automata.states[index].children[0];
				return !accepts(child,automata,value);
			}
			case Type.K_UNION: {
				int[] children = automata.states[index].children;
				for(int child : children) {					
					if(accepts(child,automata,value)) {
						return true;
					}
				}
				return false;
			}
			case Type.K_INTERSECTION: {
				int[] children = automata.states[index].children;
				for(int child : children) {
					if(!accepts(child,automata,value)) {
						return false;
					}
				}
				return true;
			}
			}
			return super.accepts(index,automata,value);
		}
	}
	
	private static final TypeInterpretation interpretation = new TypeInterpretation();
	
	public static boolean isModelSubtype(Automata a1, Automata a2, ArrayList<Value> model) {
		for(Value v : model) {
			if(interpretation.accepts(a2,v) && !interpretation.accepts(a1,v)) {
				return false;
			}
		}
		return true;
	}
	
	
	public static void generateTests(ArrayList<Automata> types, ArrayList<Value> model) {
		System.out.println("// This file was automatically generated.");
		System.out.println("package wyil.testing;");
		System.out.println("import org.junit.*;");
		System.out.println("import static org.junit.Assert.*;");
		System.out.println("import wyil.lang.Type;");
		System.out.println();
		System.out.println("public class SubtypeTests {");
		int count = 1;
		for(int i=0;i!=types.size();++i) {
			Automata a1 = types.get(i);
			Type t1 = Type.construct(types.get(i));			
			if(t1 == Type.T_VOID) { continue; } 
			for(int j=0;j<types.size();++j) {
				Automata a2 = types.get(j);
				Type t2 = Type.construct(types.get(j));				
				if(t2 == Type.T_VOID) { continue; }				
				System.out.println("\t@Test public void test_" + count++ + "() {");
				if(isModelSubtype(a1,a2,model)) {								
					System.out.println("\t\tcheckIsSubtype(\"" + t1 + "\",\"" + t2 + "\");");
				} else {
					System.out.println("\t\tcheckNotSubtype(\"" + t1 + "\",\"" + t2 + "\");");
				}
				System.out.println("\t}");
			}
		}
		System.out.println();
		System.out.println("\tprivate void checkIsSubtype(String from, String to) {");
		System.out.println("\t\tType ft = Type.fromString(from);");
		System.out.println("\t\tType tt = Type.fromString(to);");
		System.out.println("\t\tassertTrue(Type.isSubtype(ft,tt));");
		System.out.println("\t}");
		System.out.println("\tprivate void checkNotSubtype(String from, String to) {");
		System.out.println("\t\tType ft = Type.fromString(from);");
		System.out.println("\t\tType tt = Type.fromString(to);");		
		System.out.println("\t\tassertFalse(Type.isSubtype(ft,tt));");
		System.out.println("\t}");
		System.out.println("}");
		System.err.println("Wrote " + count + " subtype tests.");
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
