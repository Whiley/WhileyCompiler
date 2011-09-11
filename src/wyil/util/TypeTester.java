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
		
		/**
		 * The purpose of the visited relation is to help ensure termination in
		 * the presence of contraction. That is, types with a direct recursive
		 * cycle involving only unions or intersections.
		 */
		private BitSet visited;
		
		public boolean accepts(Automata automata, Value value) {
			visited = new BitSet(automata.size());
			return super.accepts(automata,value); 
		}
		
		public boolean accepts(int index, Automata automata, Value value) {
			Automata.State state = automata.states[index];
			
			if (visited.get(index)) {
				return false;
			} else if (state.kind != Type.K_UNION
					&& state.kind != Type.K_INTERSECTION
					&& state.kind != Type.K_NOT) {
				visited.clear();
			}
						
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
				visited.set(index);				
				return !accepts(child,automata,value);
			}
			case Type.K_UNION: {
				int[] children = automata.states[index].children;
				visited.set(index);
				BitSet copy = visited;
				for(int child : children) {
					visited = (BitSet) copy.clone();
					if(accepts(child,automata,value)) {
						return true;
					}
				}				
				copy.clear();
				return false;
			}
			case Type.K_INTERSECTION: {
				int[] children = automata.states[index].children;
				visited.set(index);
				BitSet copy = visited;
				for(int child : children) {
					visited = (BitSet) copy.clone();
					if(!accepts(child,automata,value)) {
						return false;
					}
				}
				copy.clear();
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

	public static void generateSimplificationTests(ArrayList<Automata> types)
			throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter("wyil/testing/SimplifyTests.java"));
		out.println("package wyil.testing;");
		out.println("import org.junit.*;");
		out.println("import static org.junit.Assert.*;");
		out.println("import wyil.lang.Type;");
		out.println();
		out.println("public class SimplifyTests {");
		int count = 1;
		for(int i=0;i!=types.size();++i) {
			Automata a1 = types.get(i);
			Type t1 = Type.construct(types.get(i));			
			if(t1 == Type.T_VOID) { continue; }
			count++;
			out.println("\t@Test public void test_" + count++ + "() {");
			out.println("\t\tcheckSimplification(\"" + t1 + "\");");
			out.println("\t}");
		}
		out.println();
		out.println("\tprivate void checkSimplification(String from) {");
		out.println("\t\tType type = Type.fromString(from);");
		out.println("\t\tType simplified = Type.minimise(type);");
		out.println("\t\tassertTrue(Type.isSubtype(type,simplified));");
		out.println("\t\tassertTrue(Type.isSubtype(simplified,type));");
		out.println("\t}");
		out.println("}");
		out.close();
		System.err.println("Wrote " + count + " simplification tests.");
	}
	
	public static void generateSubtypeTests(ArrayList<Automata> types,
			ArrayList<Value> model) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter("wyil/testing/SubtypeTests.java"));
		out.println("// This file was automatically generated.");
		out.println("package wyil.testing;");
		out.println("import org.junit.*;");
		out.println("import static org.junit.Assert.*;");
		out.println("import wyil.lang.Type;");
		out.println();
		out.println("public class SubtypeTests {");
		int count = 1;
		for(int i=0;i!=types.size();++i) {
			Automata a1 = types.get(i);
			Type t1 = Type.construct(types.get(i));			
			if(t1 == Type.T_VOID) { continue; } 
			for(int j=0;j<types.size();++j) {
				Automata a2 = types.get(j);
				Type t2 = Type.construct(types.get(j));				
				if(t2 == Type.T_VOID) { continue; }				
				out.println("\t@Test public void test_" + count++ + "() {");
				if(isModelSubtype(a1,a2,model)) {								
					out.println("\t\tcheckIsSubtype(\"" + t1 + "\",\"" + t2 + "\");");
				} else {
					out.println("\t\tcheckNotSubtype(\"" + t1 + "\",\"" + t2 + "\");");
				}
				out.println("\t}");
			}
		}
		out.println();
		out.println("\tprivate void checkIsSubtype(String from, String to) {");
		out.println("\t\tType ft = Type.fromString(from);");
		out.println("\t\tType tt = Type.fromString(to);");
		out.println("\t\tassertTrue(Type.isSubtype(ft,tt));");
		out.println("\t}");
		out.println("\tprivate void checkNotSubtype(String from, String to) {");
		out.println("\t\tType ft = Type.fromString(from);");
		out.println("\t\tType tt = Type.fromString(to);");		
		out.println("\t\tassertFalse(Type.isSubtype(ft,tt));");
		out.println("\t}");
		out.println("}");
		out.close();
		System.err.println("Wrote " + count + " subtype tests.");
	}
		
	public static boolean verbose = false;
	
	public static void main(String[] args) {
		try {			
			boolean binaryIn = true;
			int index = 0;
			ArrayList<DefaultInterpretation.Value> model = Tester.readModel(binaryIn,args[index],verbose);
			ArrayList<Automata> types = Tester.readAutomatas(binaryIn,args[index+1],verbose);						
			generateSubtypeTests(types,model);
			generateSimplificationTests(types);
			
		} catch(IOException e) {
			System.err.println(e.getMessage());			
		}
	}
}
