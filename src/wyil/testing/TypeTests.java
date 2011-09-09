package wyil.testing;

import java.io.*;
import java.util.ArrayList;

import wyautl.io.BinaryAutomataReader;
import wyautl.io.GenericReader;
import wyautl.lang.Automata;
import wyautl.lang.DefaultInterpretation;
import wyautl.lang.DefaultInterpretation.Value;
import wyautl.util.Tester;
import wyil.lang.Type;
import wyjvm.io.BinaryInputStream;

import org.junit.*;
import static org.junit.Assert.*;


public class TypeTests {	
	public @Test void test_1() {
		checkIsSubtype("void","void");
	}
	public @Test void test_2() {
		checkNotSubtype("void","any");
	}
	public @Test void test_3() {
		checkNotSubtype("void","null");
	}
	public @Test void test_4() {
		checkNotSubtype("void","int");
	}
	public @Test void test_5() {
		checkNotSubtype("void","[void]");
	}
	public @Test void test_6() {
		checkNotSubtype("void","[any]");
	}
	public @Test void test_7() {
		checkNotSubtype("void","[null]");
	}
	public @Test void test_8() {
		checkNotSubtype("void","[int]");
	}
	public @Test void test_9() {
		checkNotSubtype("any","void");
	}
	public @Test void test_10() {
		checkIsSubtype("any","any");
	}
	public @Test void test_11() {
		checkNotSubtype("any","null");
	}
	public @Test void test_12() {
		checkNotSubtype("any","int");
	}
	public @Test void test_13() {
		checkNotSubtype("any","[void]");
	}
	public @Test void test_14() {
		checkNotSubtype("any","[any]");
	}
	public @Test void test_15() {
		checkNotSubtype("any","[null]");
	}
	public @Test void test_16() {
		checkNotSubtype("any","[int]");
	}
	public @Test void test_17() {
		checkNotSubtype("null","void");
	}
	public @Test void test_18() {
		checkNotSubtype("null","any");
	}
	public @Test void test_19() {
		checkIsSubtype("null","null");
	}
	public @Test void test_20() {
		checkNotSubtype("null","int");
	}
	public @Test void test_21() {
		checkNotSubtype("null","[void]");
	}
	public @Test void test_22() {
		checkNotSubtype("null","[any]");
	}
	public @Test void test_23() {
		checkNotSubtype("null","[null]");
	}
	public @Test void test_24() {
		checkNotSubtype("null","[int]");
	}
	public @Test void test_25() {
		checkNotSubtype("int","void");
	}
	public @Test void test_26() {
		checkNotSubtype("int","any");
	}
	public @Test void test_27() {
		checkNotSubtype("int","null");
	}
	public @Test void test_28() {
		checkIsSubtype("int","int");
	}
	public @Test void test_29() {
		checkNotSubtype("int","[void]");
	}
	public @Test void test_30() {
		checkNotSubtype("int","[any]");
	}
	public @Test void test_31() {
		checkNotSubtype("int","[null]");
	}
	public @Test void test_32() {
		checkNotSubtype("int","[int]");
	}
	public @Test void test_33() {
		checkNotSubtype("[void]","void");
	}
	public @Test void test_34() {
		checkNotSubtype("[void]","any");
	}
	public @Test void test_35() {
		checkNotSubtype("[void]","null");
	}
	public @Test void test_36() {
		checkNotSubtype("[void]","int");
	}
	public @Test void test_37() {
		checkIsSubtype("[void]","[void]");
	}
	public @Test void test_38() {
		checkNotSubtype("[void]","[any]");
	}
	public @Test void test_39() {
		checkNotSubtype("[void]","[null]");
	}
	public @Test void test_40() {
		checkNotSubtype("[void]","[int]");
	}
	public @Test void test_41() {
		checkNotSubtype("[any]","void");
	}
	public @Test void test_42() {
		checkNotSubtype("[any]","any");
	}
	public @Test void test_43() {
		checkNotSubtype("[any]","null");
	}
	public @Test void test_44() {
		checkNotSubtype("[any]","int");
	}
	public @Test void test_45() {
		checkNotSubtype("[any]","[void]");
	}
	public @Test void test_46() {
		checkIsSubtype("[any]","[any]");
	}
	public @Test void test_47() {
		checkNotSubtype("[any]","[null]");
	}
	public @Test void test_48() {
		checkNotSubtype("[any]","[int]");
	}
	public @Test void test_49() {
		checkNotSubtype("[null]","void");
	}
	public @Test void test_50() {
		checkNotSubtype("[null]","any");
	}
	public @Test void test_51() {
		checkNotSubtype("[null]","null");
	}
	public @Test void test_52() {
		checkNotSubtype("[null]","int");
	}
	public @Test void test_53() {
		checkNotSubtype("[null]","[void]");
	}
	public @Test void test_54() {
		checkNotSubtype("[null]","[any]");
	}
	public @Test void test_55() {
		checkIsSubtype("[null]","[null]");
	}
	public @Test void test_56() {
		checkNotSubtype("[null]","[int]");
	}
	public @Test void test_57() {
		checkNotSubtype("[int]","void");
	}
	public @Test void test_58() {
		checkNotSubtype("[int]","any");
	}
	public @Test void test_59() {
		checkNotSubtype("[int]","null");
	}
	public @Test void test_60() {
		checkNotSubtype("[int]","int");
	}
	public @Test void test_61() {
		checkNotSubtype("[int]","[void]");
	}
	public @Test void test_62() {
		checkNotSubtype("[int]","[any]");
	}
	public @Test void test_63() {
		checkNotSubtype("[int]","[null]");
	}
	public @Test void test_64() {
		checkIsSubtype("[int]","[int]");
	}

	public void checkIsSubtype(String from, String to) {
		Type ft = Type.fromString(from);
		Type tt = Type.fromString(to);
		assertTrue(Type.isSubtype(ft,tt));
	}
	
	public void checkNotSubtype(String from, String to) {
		Type ft = Type.fromString(from);
		Type tt = Type.fromString(to);
		assertFalse(Type.isSubtype(ft,tt));
	}
	
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
