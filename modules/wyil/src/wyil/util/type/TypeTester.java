// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil.util.type;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;

import wyautl_old.lang.*;
import wyautl_old.lang.DefaultInterpretation.Term;
import wyautl_old.util.Tester;
import wyfs.io.BinaryInputStream;
import wyil.lang.Type;

public class TypeTester {

	/**
	 * In the type interpretation, we must override the default interpretation
	 * to deal with union, intersection, negation, any, void, list and set
	 * types.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class TypeInterpretation extends DefaultInterpretation {

		/**
		 * The purpose of the visited relation is to help ensure termination in
		 * the presence of contraction. That is, types with a direct recursive
		 * cycle involving only unions or intersections.
		 */
		private BitSet visited;

		public boolean accepts(Automaton automaton, Term value) {
			visited = new BitSet(automaton.size());
			return super.accepts(automaton,value);
		}

		public boolean accepts(int index, Automaton automaton, Term value) {
			Automaton.State state = automaton.states[index];

			if (visited.get(index)) {
				return false;
			} else if (state.kind != Type.K_UNION
					&& state.kind != Type.K_NEGATION) {
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
				int child = automaton.states[index].children[0];
				Term[] values = value.children;
				for(int i=0;i!=values.length;++i) {
					Term vchild = values[i];
					if(!accepts(child,automaton,vchild)) {
						return false;
					}
				}
				return true;
			}
			case Type.K_FUNCTION:
			case Type.K_METHOD: {
				int[] schildren = state.children;
				Term[] vchildren = value.children;
				if(schildren.length != vchildren.length) {
					return false;
				}
				int length = schildren.length;
				int sNumParams = (Integer) state.data;
				// First, do parameters (which are contravariant).
				for(int i=0;i<sNumParams;++i) {
					int schild = schildren[i];
					Term vchild = vchildren[i];
					if(accepts(schild,automaton,vchild)) {
						return false;
					}
				}
				// Second, do return values (which are covariant)
				for(int i=sNumParams;i<schildren.length;++i) {
					if(!accepts(schildren[i],automaton,vchildren[i])) {
						return false;
					}	
				}
				
				// Third, do return values (which should be contra-variant)
				return true;
			}
			case Type.K_NEGATION: {
				int child = automaton.states[index].children[0];
				visited.set(index);
				return !accepts(child,automaton,value);
			}
			case Type.K_UNION: {
				int[] children = automaton.states[index].children;
				visited.set(index);
				BitSet copy = visited;
				for(int child : children) {
					visited = (BitSet) copy.clone();
					if(accepts(child,automaton,value)) {
						return true;
					}
				}
				copy.clear();
				return false;
			}
			}
			return super.accepts(index,automaton,value);
		}
	}

	private static final TypeInterpretation interpretation = new TypeInterpretation();

	public static boolean isModelSubtype(Automaton a1, Automaton a2, ArrayList<Term> model) {
		for(Term v : model) {
			if (interpretation.accepts(a2, v) && !interpretation.accepts(a1, v)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isModelEmpty(Automaton a1, ArrayList<Term> model) {
		for(Term v : model) {
			if (interpretation.accepts(a1, v)) {
				return false;
			}
		}
		return true;
	}

	public static void generateCanonicalisationTests(ArrayList<Automaton> types)
			throws IOException {

		System.out.println("package wyil.testing;");
		System.out.println("import org.junit.*;");
		System.out.println("import static org.junit.Assert.*;");
		System.out.println("import wyil.lang.Type;");
		System.out.println();
		System.out.println("public class SimplifyTests {");
		int count = 1;
		for (int i = 0; i != types.size(); ++i) {
			Automaton a1 = types.get(i);
			Type t1 = Type.construct(types.get(i));
			if (t1 == Type.T_VOID) {
				continue;
			}
			count++;
			System.out.println("\t@Test public void test_" + count++ + "() {");
			System.out.println("\t\tcheckSimplification(\"" + t1 + "\");");
			System.out.println("\t}");
		}
		System.out.println();
		System.out.println("\tprivate void checkSimplification(String from) {");
		System.out.println("\t\tType type = Type.fromString(from);");
		System.out.println("\t\tType simplified = Type.normalise(type);");
		System.out.println("\t\tassertTrue(Type.isSubtype(type,simplified));");
		System.out.println("\t\tassertTrue(Type.isSubtype(simplified,type));");
		System.out.println("\t}");
		System.out.println("}");
		System.err.println("Wrote " + count + " simplification tests.");
	}

	public static void generateSubtypeTests(ArrayList<Automaton> types,
			ArrayList<Term> model) throws IOException {
		System.out.println("// This file was automatically generated.");
		System.out.println("package wyil.testing;");
		System.out.println("import org.junit.*;");
		System.out.println("import static org.junit.Assert.*;");
		System.out.println("import wyil.lang.Type;");
		System.out.println();
		System.out.println("public class SubtypeTests {");
		int count = 1;
		for(int i=0;i!=types.size();++i) {
			Automaton a1 = types.get(i);
			Type t1 = Type.construct(types.get(i));
			if(t1 == Type.T_VOID) { continue; }
			for(int j=0;j<types.size();++j) {
				Automaton a2 = types.get(j);
				Type t2 = Type.construct(types.get(j));
				if(t2 == Type.T_VOID) { continue; }
				System.out.print("\t@Test public void test_" + count++ + "() { ");
				if(isModelSubtype(a1,a2,model)) {
					System.out.println("checkIsSubtype(\"" + t1 + "\",\"" + t2 + "\"); }");
				} else {
					System.out.println("checkNotSubtype(\"" + t1 + "\",\"" + t2 + "\"); }");
				}
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
			String mode = args[index++];
			ArrayList<DefaultInterpretation.Term> model = Tester.readModel(
					new Type.BinaryReader(new BinaryInputStream(
							new FileInputStream(args[index]))), verbose);
			ArrayList<Automaton> types = Tester.readAutomatas(
					new Type.BinaryReader(new BinaryInputStream(
							new FileInputStream(args[index+1]))), verbose);

			if(mode.equals("-subtypes")) {
				generateSubtypeTests(types,model);
			} else {
				generateCanonicalisationTests(types);
			}

		} catch(IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
