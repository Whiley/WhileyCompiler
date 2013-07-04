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

package wyrl.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.*;

import wyautl.core.Automaton;
import wyautl.io.BinaryAutomataReader;
import wyautl.util.BigRational;
import wybs.io.BinaryInputStream;
import wybs.io.BinaryOutputStream;
import wyrl.core.Attribute;
import wyrl.core.Expr;
import wyrl.core.Pattern;
import wyrl.core.SpecFile;
import wyrl.core.Type;
import wyrl.core.Types;
import wyrl.core.SpecFile.RuleDecl;
import wyrl.util.*;
import static wyrl.core.Attribute.*;
import static wyrl.core.SpecFile.*;

public class NewJavaFileWriter {
	private PrintWriter out;
	
	public NewJavaFileWriter(Writer os) {
		this.out = new PrintWriter(os);
	}

	public NewJavaFileWriter(OutputStream os) {
		this.out = new PrintWriter(os);
	}

	public void write(SpecFile spec) throws IOException {			
		reset();
		translate(spec,spec);		
	}
	
	private void translate(SpecFile spec, SpecFile root) throws IOException {						
		PrintWriter saved = out;		
		
		if(root == spec) {			

			if (!spec.pkg.equals("")) {
				myOut("package " + spec.pkg + ";");
				myOut("");
			}
			
			writeImports();
			myOut("public final class " + spec.name + " {");
			
		}
		
		for (Decl d : spec.declarations) {
			if(d instanceof IncludeDecl) {
				IncludeDecl id = (IncludeDecl) d;
				SpecFile file = id.file;
				translate(file,root);				
			} else  if (d instanceof TermDecl) {
				translate((TermDecl) d);
			} else if (d instanceof RewriteDecl) {
				translate((RewriteDecl) d,root);
			} 
		}
		
		if(root == spec) {
			writeSchema(spec);
			writeRuleArrays();
			writeTypeTests();
			writeMainMethod();
		}
		
		if(root == spec) {			
			myOut("}");
			out.close();
		}
		
		out = saved;
	}

	/**
	 * Reset all global information before proceeding to write out another file.
	 */
	protected void reset() {
		termCounter = 0;		
		reductionCounter = 0;
		inferenceCounter = 0;
	}
	
	protected void writeImports() {
		myOut("import java.io.*;");
		myOut("import java.util.*;");
		myOut("import java.math.BigInteger;");
		myOut("import wyautl.util.BigRational;");
		myOut("import wyautl.io.*;");
		myOut("import wyautl.core.*;");
		myOut("import wyautl.rw.*;");
		myOut("import wyrl.core.Type;");
		myOut("import wyrl.util.Runtime;");
		myOut();
	}
	
	public void translate(TermDecl decl) {
		myOut(1, "// term " + decl.type);
		String name = decl.type.name();
		myOut(1, "public final static int K_" + name + " = "
				+ termCounter++ + ";");
		if (decl.type.element() == null) {
			myOut(1, "public final static Automaton.Term " + name
					+ " = new Automaton.Term(K_" + name + ");");
		} else {
			Type.Ref data = decl.type.element();
			Type element = data.element();
			if(element instanceof Type.Collection) {
				// add two helpers
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, int... r0) {" );
				if(element instanceof Type.Set) { 
					myOut(2,"int r1 = automaton.add(new Automaton.Set(r0));");
				} else if(element instanceof Type.Bag) {
					myOut(2,"int r1 = automaton.add(new Automaton.Bag(r0));");
				} else {
					myOut(2,"int r1 = automaton.add(new Automaton.List(r0));");
				}
				myOut(2,"return automaton.add(new Automaton.Term(K_" + name + ", r1));");
				myOut(1,"}");
				
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, List<Integer> r0) {" );
				if(element instanceof Type.Set) { 
					myOut(2,"int r1 = automaton.add(new Automaton.Set(r0));");
				} else if(element instanceof Type.Bag) {
					myOut(2,"int r1 = automaton.add(new Automaton.Bag(r0));");
				} else {
					myOut(2,"int r1 = automaton.add(new Automaton.List(r0));");
				}
				myOut(2,"return automaton.add(new Automaton.Term(K_" + name + ", r1));");
				myOut(1,"}");
			} else if(element instanceof Type.Int) {
				// add two helpers
				myOut(1, "public final static int " + name 
						+ "(Automaton automaton, long r0) {" );			
				myOut(2,"int r1 = automaton.add(new Automaton.Int(r0));");
				myOut(2,"return automaton.add(new Automaton.Term(K_" + name + ", r1));");
				myOut(1,"}");
				
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, BigInteger r0) {" );	
				myOut(2,"int r1 = automaton.add(new Automaton.Int(r0));");
				myOut(2,"return automaton.add(new Automaton.Term(K_" + name + ", r1));");
				myOut(1,"}");
			} else if(element instanceof Type.Real) {
				// add two helpers
				myOut(1, "public final static int " + name 
						+ "(Automaton automaton, long r0) {" );			
				myOut(2,"int r1 = automaton.add(new Automaton.Real(r0));");
				myOut(2,"return automaton.add(new Automaton.Term(K_" + name + ", r1));");
				myOut(1,"}");
				
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, BigRational r0) {" );	
				myOut(2,"int r1 = automaton.add(new Automaton.Real(r0));");
				myOut(2,"return automaton.add(new Automaton.Term(K_" + name + ", r1));");
				myOut(1,"}");
			} else if(element instanceof Type.Strung) {
				// add two helpers
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, String r0) {" );	
				myOut(2,"int r1 = automaton.add(new Automaton.Strung(r0));");
				myOut(2,"return automaton.add(new Automaton.Term(K_" + name + ", r1));");
				myOut(1,"}");
			} else {
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, " + type2JavaType(data) + " r0) {" );			
				myOut(2,"return automaton.add(new Automaton.Term(K_" + name + ", r0));");
				myOut(1,"}");
			}
			
		}
		myOut();
	}

	private int termCounter = 0;
	private int reductionCounter = 0;
	private int inferenceCounter = 0;

	public void translate(RewriteDecl decl, SpecFile file) {
		boolean isReduction = decl instanceof ReduceDecl;
		Pattern.Term pattern = decl.pattern;
		Type param = pattern.attribute(Attribute.Type.class).type; 
		myOut(1, "// " + decl.pattern);
				
		if(isReduction) {
			myOut(1, "private final static class Reduction_" + reductionCounter++ + " implements ReductionRule {" );
		} else {
			myOut(1, "private final static class Inference_" + inferenceCounter++ + " implements InferenceRule {" );
		}
						
		// ===============================================
		// Probe
		// ===============================================

		myOut();
		myOut(2,"public Activation probe(Automaton automaton, int r0) {");
		// setup the environment
		Environment environment = new Environment();
		int thus = environment.allocate(param,"this");
		// translate pattern
		int level = translate(3,pattern,thus,environment);
		
		String abody = "";
		boolean firstTime=true;
		for(int i=0;i!=environment.size();++i) {
			Pair<Type,String> p = environment.get(i);
			String name = p.second();
			if(name != null) {
				if(!firstTime) {
					abody += ", ";
				}
				firstTime=false;
				abody += "r" + i;
			}
		}
		myOut(level, "Object[] state = { " + abody + " };");
		myOut(level, "BitSet dependencies = null;");
		myOut(level, "return new Activation(this,dependencies,state);");
		// close potentially open pattern
		if(level > 3) {
			while(level > 3) { myOut(--level," }"); }				
			myOut(level,"return null;");
		}
		myOut(2,"}");

		// ===============================================
		// Apply
		// ===============================================
		
		myOut();
		myOut(2,"public boolean apply(Automaton automaton, Object _state) {");
		myOut(3,"Object[] state = (Object[]) _state;");
		// first, unpack the state
		for(int i=0,j=0;i!=environment.size();++i) {
			Pair<Type, String> p = environment.get(i);
			if (p.second() != null) {
				myOut(3, type2JavaType(p.first()) + " r" + i + " = ("
						+ type2JavaType(p.first(),false) + ") state[" + j++ + "];");
			}
		}
		// second, translate the individual rules
		for(RuleDecl rd : decl.rules) {
			translate(3,rd,isReduction,environment,file);
		}
		
		myOut(level,"return false;");
		myOut(2,"}");
		
		myOut(1,"}"); // end class
	}
		
	public int translate(int level, Pattern p, int source, Environment environment) {
		if(p instanceof Pattern.Leaf) {
			return translate(level,(Pattern.Leaf) p,source,environment);
		} else if(p instanceof Pattern.Term) {
			return translate(level,(Pattern.Term) p,source,environment);
		} else if(p instanceof Pattern.Set) {
			return translate(level,(Pattern.Set) p,source,environment);
		} else if(p instanceof Pattern.Bag) {
			return translate(level,(Pattern.Bag) p,source,environment);
		} else  {
			return translate(level,(Pattern.List) p,source,environment);
		} 
	}
	
	public int translate(int level, Pattern.Leaf p, int source, Environment environment) {
		int typeIndex = register(p.type);
		myOut(level,"if(!Runtime.accepts(type" + typeIndex + ", automaton, automaton.get(r" + source + "), SCHEMA)) { return null; }");		 
		return level;
	}
	
	public int translate(int level, Pattern.Term pattern, int source,
			Environment environment) {
		Type type = pattern.attribute(Attribute.Type.class).type;
		int state = environment.allocate(Type.T_ANY());
		myOut(level, "Automaton.State r" + state + " = automaton.get(r"
				+ source + ");");
		myOut(level, "if(!(r" + state
				+ " instanceof Automaton.Term)) { return null; }");

		int term = environment.allocate(Type.T_ANY());
		myOut(level, "Automaton.Term r" + term + " = (Automaton.Term) r"
				+ state + ";");
		myOut(level, "if(r" + term + ".kind != K_" + pattern.name
				+ ") { return null; }");
		if (pattern.data != null) {
			int target = environment.allocate(Type.T_REF(type),
					pattern.variable);
			myOut(level, "int r" + target + " = r" + term + ".contents;");
			return translate(level, pattern.data, target, environment);
		} else {
			return level;
		}
	}

	public int translate(int level, Pattern.BagOrSet pattern, int source, Environment environment) {
		Type.Ref<Type.Collection> type = (Type.Ref<Type.Collection>) pattern
				.attribute(Attribute.Type.class).type;
		source = coerceFromRef(level, pattern, source, environment);
		
		if(pattern.unbounded) { 
			myOut(level,"if(r" + source + ".size() < " + (pattern.elements.length-1) + ") { return null; }");
		} else {
			myOut(level,"if(r" + source + ".size() != " + pattern.elements.length + ") { return null; }");
		}
		
		Pair<Pattern, String>[] elements = pattern.elements;		
		
		// construct a for-loop for each fixed element to match
		int[] indices = new int[elements.length];
		for (int i = 0; i != elements.length; ++i) {
			boolean isUnbounded = pattern.unbounded && (i+1) == elements.length;
			Pair<Pattern, String> p = elements[i];
			Pattern pat = p.first();
			String var = p.second();
			Type.Ref pt = (Type.Ref) pat.attribute(Attribute.Type.class).type;			
			int index;

			if(isUnbounded) {
				index = environment.allocate(pt);
				Type.Collection rt = pattern instanceof Pattern.Bag ? Type.T_BAG(true,pt) : Type.T_SET(true,pt);
				myOut(level, "int j" + index + " = 0;");
				myOut(level, "int[] t" + index + " = new int[r" + source + ".size()-" + i + "];");				
			} else {
				index = environment.allocate(pt,var);
			}
			
			String name = "i" + index;
			indices[i] = index;
			myOut(level++,"for(int " + name + "=0;" + name + "!=r" + source + ".size();++" + name + ") {");
			myOut(level, type2JavaType(pt) + " r" + index + " = r"
					+ source + ".get(" + name + ");");

			indent(level);out.print("if(");
			// check against earlier indices
			for(int j=0;j<i;++j) {
				out.print(name + " == i" + indices[j] + " || ");
			}
			
			// check matching type
			int typeIndex = register(pt);
			myOut("!Runtime.accepts(type" + typeIndex + ", automaton, r" + index + ", SCHEMA)) { continue; }");
			myOut(level);
			
			if(isUnbounded) {
				myOut(level,"t" + index + "[j" + index + "++] = r" + index + ";");
				myOut(--level,"}");
				if(pattern instanceof Pattern.Set) { 
					Type.Collection rt = Type.T_SET(true,pt);
					int rest = environment.allocate(rt,var);
					myOut(level, type2JavaType(rt) + " r" + rest + " = new Automaton.Set(t" + index + ");");
				} else {
					Type.Collection rt = Type.T_BAG(true,pt);
					int rest = environment.allocate(rt,var);
					myOut(level, type2JavaType(rt) + " r" + rest + " = new Automaton.Bag(t" + index + ");");
				}
			} else {
				level = translate(level++,pat,index,environment);
			}
		}	
		
		return level;
	}

	public int translate(int level, Pattern.List pattern, int source, Environment environment) {
		Type.Ref<Type.List> type = (Type.Ref<Type.List>) pattern
				.attribute(Attribute.Type.class).type;
		source = coerceFromRef(level, pattern, source, environment);
		
		if(pattern.unbounded) { 
			myOut(level,"if(r" + source + ".size() < " + (pattern.elements.length-1) + ") { return null; }");
		} else {
			myOut(level,"if(r" + source + ".size() != " + pattern.elements.length + ") { return null; }");
		}
				
		Pair<Pattern, String>[] elements = pattern.elements;
		for (int i = 0; i != elements.length; ++i) {
			Pair<Pattern, String> p = elements[i];
			Pattern pat = p.first();
			String var = p.second();
			Type.Ref pt = (Type.Ref) pat.attribute(Attribute.Type.class).type;
			int element;
			if(pattern.unbounded && (i+1) == elements.length) {
				Type.List tc = Type.T_LIST(true, pt);
				element = environment.allocate(tc);
				myOut(level, type2JavaType(tc) + " r" + element + " = r"
						+ source + ".sublist(" + i + ");");
			} else {
				element = environment.allocate(pt);				
				myOut(level, type2JavaType(pt) + " r" + element + " = r"
						+ source + ".get(" + i + ");");
				level = translate(level,pat, element, environment);
			}			
			if (var != null) {
				environment.put(element, var);
			}
		}
		return level;
	}
	
	public void translate(int level, RuleDecl decl, boolean isReduce, Environment environment, SpecFile file) {
		int thus = environment.get("this");
		
		// TODO: can optimise this by translating lets within the conditionals
		// in the case that the conditionals don't refer to those lets. This
		// will then prevent unnecessary object creation.
		
		for(Pair<String,Expr> let : decl.lets) {
			String letVar = let.first();
			Expr letExpr = let.second();
			int result = translate(level, letExpr, environment, file);
			environment.put(result, letVar);
		}
		if(decl.condition != null) {
			int condition = translate(level, decl.condition, environment, file);
			myOut(level++, "if(r" + condition + ") {");
		}
		int result = translate(level, decl.result, environment, file);
		result = coerceFromValue(level,decl.result,result,environment);
		
		myOut(level, "if(r" + thus + " != r" + result + ") {");
		myOut(level+1,"automaton.rewrite(r" + thus + ", r" + result + ");");
		
		// FIXME: we can potentially get rid of the swap by requiring automaton
		// to "reset" themselves to their original size before the rule began
		// (and any junk states were added).
		
		if(isReduce) {												
			myOut(level+1, "return true;");
		} else {			
			myOut(level+1, "reduce(automaton,start);");
			myOut(level+1, "if(!automaton.equals(original)) {");			
			myOut(level+2, "original.swap(automaton);");
			myOut(level+2, "reduce(original,0);");
			myOut(level+2, "return true;");
			myOut(level+1, "}");
		}
		myOut(level,"}");
		if(decl.condition != null) {
			myOut(--level,"}");
		}
	}
	
	public void writeSchema(SpecFile spec) {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Schema");
		myOut(1,
				"// =========================================================================");
		myOut();
		myOut(1, "public static final Schema SCHEMA = new Schema(new Schema.Term[]{");
		
		boolean firstTime=true;		
		for(TermDecl td : extractDecls(TermDecl.class,spec)) {
			if (!firstTime) {
				myOut(",");
			}
			firstTime=false;						
			myOut(2,"// " + td.type.toString());
			indent(2);writeSchema(td.type);
		}
		myOut();
		myOut(1, "});");
		myOut();
	}
	
	public void writeRuleArrays() {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// rules");
		myOut(1,
				"// =========================================================================");
		myOut();
		myOut(1, "public static final InferenceRule[] inferences = new InferenceRule[]{");
		
		for(int i=0;i!=inferenceCounter;++i) {
			if(i != 0) {
				myOut(2,",");
			}
			myOut("new Inference_" + i + "()");
		}
		myOut();
		myOut(1, "};");
		myOut(1, "public static final ReductionRule[] reductions = new ReductionRule[]{");
		
		for(int i=0;i!=reductionCounter;++i) {
			if(i != 0) {
				myOut(2,",");
			}
			myOut(2,"new Reduction_" + i + "()");
		}
		myOut(1, "};");
		myOut();
	}
	
	protected void writeTypeTests() throws IOException {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Type Tests");
		myOut(1,
				"// =========================================================================");
		myOut();
		
		for(int i=0;i!=typeRegister.size();++i) {
			Type t = typeRegister.get(i);
			// FIXME: is this redundant?
			t.automaton().canonicalise();
			JavaIdentifierOutputStream jout = new JavaIdentifierOutputStream();
			BinaryOutputStream bout = new BinaryOutputStream(jout);
			bout.write(t.toBytes());
			bout.close();
			// FIXME: strip out nominal types (and any other unneeded types).
			myOut(1,"// " + t);
			myOut(1,"private static Type type" + i + " = Runtime.Type(\"" + jout.toString() + "\");");
		}
		
		myOut();
	}
	
	private void writeSchema(Type.Term tt) {
		Automaton automaton = tt.automaton();
		BitSet visited = new BitSet(automaton.nStates());
		writeSchema(automaton.getRoot(0),automaton,visited);
	}
	
	private void writeSchema(int node, Automaton automaton, BitSet visited) {
		if(node < 0) {
			// bypass virtual node
		} else if (visited.get(node)) {
			out.print("Schema.Any");
			return;
		} else {
			visited.set(node);
		}
		// you can scratch your head over why this is guaranteed ;)
		Automaton.Term state = (Automaton.Term) automaton.get(node);
		switch (state.kind) {
		case wyrl.core.Types.K_Void:
			out.print("Schema.Void");
			break;
		case wyrl.core.Types.K_Any:
			out.print("Schema.Any");
			break;
			case wyrl.core.Types.K_Bool:
				out.print("Schema.Bool");
				break;
			case wyrl.core.Types.K_Int:
				out.print("Schema.Int");
				break;
			case wyrl.core.Types.K_Real:
				out.print("Schema.Real");
				break;
			case wyrl.core.Types.K_String:
				out.print("Schema.String");
				break;
			case wyrl.core.Types.K_Not:
				out.print("Schema.Not(");
				writeSchema(state.contents, automaton, visited);
				out.print(")");
				break;
			case wyrl.core.Types.K_Ref:
				writeSchema(state.contents, automaton, visited);
				break;
			case wyrl.core.Types.K_Meta:
				out.print("Schema.Meta(");
				writeSchema(state.contents, automaton, visited);
				out.print(")");
				break;
			case wyrl.core.Types.K_Nominal: {				
				// bypass the nominal marker
				Automaton.List list = (Automaton.List) automaton.get(state.contents);
				writeSchema(list.get(1), automaton, visited);
				break;
			}
			case wyrl.core.Types.K_Or: {
				out.print("Schema.Or(");
				Automaton.Set set = (Automaton.Set) automaton.get(state.contents);
				for(int i=0;i!=set.size();++i) {
					if(i != 0) { out.print(", "); }
					writeSchema(set.get(i), automaton, visited);
				}
				out.print(")");
				break;
			}
			case wyrl.core.Types.K_Set: {
				out.print("Schema.Set(");
				Automaton.List list = (Automaton.List) automaton.get(state.contents);
				// FIXME: need to deref unbounded bool here as well
				out.print("true");
				Automaton.Bag set = (Automaton.Bag) automaton.get(list.get(1));
				for(int i=0;i!=set.size();++i) {
					out.print(",");
					writeSchema(set.get(i), automaton, visited);
				}
				out.print(")");
				break;
			}
			case wyrl.core.Types.K_Bag: {
				out.print("Schema.Bag(");
				Automaton.List list = (Automaton.List) automaton.get(state.contents);
				// FIXME: need to deref unbounded bool here as well
				out.print("true");
				Automaton.Bag bag = (Automaton.Bag) automaton.get(list.get(1));
				for(int i=0;i!=bag.size();++i) {
					out.print(",");
					writeSchema(bag.get(i), automaton, visited);
				}
				out.print(")");
				break;
			}
			case wyrl.core.Types.K_List: {
				out.print("Schema.List(");
				Automaton.List list = (Automaton.List) automaton.get(state.contents);
				// FIXME: need to deref unbounded bool here as well
				out.print("true");
				Automaton.List list2 = (Automaton.List) automaton.get(list.get(1));
				for(int i=0;i!=list2.size();++i) {
					out.print(",");
					writeSchema(list2.get(i), automaton, visited);
				}
				out.print(")");
				break;
			}
			case wyrl.core.Types.K_Term: {
				out.print("Schema.Term(");
				Automaton.List list = (Automaton.List) automaton.get(state.contents);
				Automaton.Strung str = (Automaton.Strung) automaton.get(list.get(0));
				out.print("\"" + str.value + "\"");
				if(list.size() > 1) {
					out.print(",");
					writeSchema(list.get(1),automaton,visited);
				} 				
				out.print(")");
				break;
			}
			default:
				throw new RuntimeException("Unknown kind encountered: " + state.kind);
		}	
	}
	
	private <T extends Decl> ArrayList<T> extractDecls(Class<T> kind, SpecFile spec) {
		ArrayList r = new ArrayList();
		extractDecls(kind,spec,r);
		return r;
	}
	
	private <T extends Decl> void extractDecls(Class<T> kind, SpecFile spec, ArrayList<T> decls) {
		for(Decl d : spec.declarations) {
			if(kind.isInstance(d)) {
				decls.add((T)d);
			} else if(d instanceof IncludeDecl) {
				IncludeDecl id = (IncludeDecl) d;
				extractDecls(kind,id.file,decls);
			}
		}
	}
	
	public int translate(int level, Expr code, Environment environment, SpecFile file) {
		if (code instanceof Expr.Constant) {
			return translate(level,(Expr.Constant) code, environment, file);
		} else if (code instanceof Expr.UnOp) {
			return translate(level,(Expr.UnOp) code, environment, file);
		} else if (code instanceof Expr.BinOp) {
			return translate(level,(Expr.BinOp) code, environment, file);
		} else if (code instanceof Expr.NaryOp) {
			return translate(level,(Expr.NaryOp) code, environment, file);
		} else if (code instanceof Expr.Constructor) {
			return translate(level,(Expr.Constructor) code, environment, file);
		} else if (code instanceof Expr.ListAccess) {
			return translate(level,(Expr.ListAccess) code, environment, file);
		} else if (code instanceof Expr.ListUpdate) {
			return translate(level,(Expr.ListUpdate) code, environment, file);
		} else if (code instanceof Expr.Variable) {
			return translate(level,(Expr.Variable) code, environment, file);
		} else if (code instanceof Expr.Substitute) {
			return translate(level,(Expr.Substitute) code, environment, file);
		} else if(code instanceof Expr.Comprehension) {
			return translate(level,(Expr.Comprehension) code, environment, file);
		} else if(code instanceof Expr.TermAccess) {
			return translate(level,(Expr.TermAccess) code, environment, file);
		} else if(code instanceof Expr.Cast) {
			return translate(level,(Expr.Cast) code, environment, file);
		} else {
			throw new RuntimeException("unknown expression encountered - " + code);
		}
	}
	
	public int translate(int level, Expr.Cast code, Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;

		// first translate src expression, and coerce to a value
		int src = translate(level, code.src, environment, file);
		src = coerceFromRef(level, code.src, src, environment);

		// TODO: currently we only support casting from integer to real!!
		String body = "new Automaton.Real(r" + src + ".value)";

		int target = environment.allocate(type);
		myOut(level, type2JavaType(type) + " r" + target + " = " + body + ";");
		return target;
		
	}
	
	public int translate(int level, Expr.Constant code, Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		Object v = code.value;
		String rhs;
				
		if (v instanceof Boolean) {
			rhs = v.toString();
		} else if (v instanceof BigInteger) {
			BigInteger bi = (BigInteger) v;
			if(bi.bitLength() <= 64) {
				rhs = "new Automaton.Int(" + bi.longValue() + ")";
			} else {
				rhs = "new Automaton.Int(\"" + bi.toString() + "\")";	
			}
			
		} else if (v instanceof BigRational) {
			BigRational br = (BigRational) v;
			rhs = "new Automaton.Real(\"" + br.toString() + "\")";
			if(br.isInteger()) {
				long lv = br.longValue();
				if(BigRational.valueOf(lv).equals(br)) {
					// Yes, this will fit in a long value. Therefore, inline a
					// long constant as this is faster.
					rhs = "new Automaton.Real(" + lv + ")";
				}
			}
			
		} else if (v instanceof String) {
			rhs = "new Automaton.Strung(\"" + v + "\")";
		} else {		
			throw new RuntimeException("unknown constant encountered (" + v
					+ ")");
		}
		
		int target = environment.allocate(type);
		myOut(level,comment(type2JavaType(type) + " r" + target + " = " + rhs + ";",code.toString()));
		return target;
	}

	public int translate(int level, Expr.UnOp code, Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		int rhs = translate(level,code.mhs,environment,file);
		rhs = coerceFromRef(level,code.mhs, rhs, environment);
		String body;
		
		switch (code.op) {
		case LENGTHOF:
			body = "r" + rhs + ".lengthOf()";
			break;
		case NUMERATOR:
			body = "r" + rhs + ".numerator()";
			break;
		case DENOMINATOR:
			body = "r" + rhs + ".denominator()";
			break;		
		case NEG:
			body = "r" + rhs + ".negate()";
			break;
		case NOT:
			body = "!r" + rhs;
			break;
		default:
			throw new RuntimeException("unknown unary expression encountered");
		}
		
		int target = environment.allocate(type);
		myOut(level,comment(type2JavaType(type) + " r" + target + " = " + body + ";",code.toString()));
		return target;
	}

	public int translate(int level, Expr.BinOp code, Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		Type lhs_t = code.lhs.attribute(Attribute.Type.class).type;
		Type rhs_t = code.rhs.attribute(Attribute.Type.class).type;
		int lhs = translate(level,code.lhs,environment,file);
		
		String body;
		
		if(code.op == Expr.BOp.IS && code.rhs instanceof Expr.Constant) {
			// special case for runtime type tests
			Expr.Constant c = (Expr.Constant) code.rhs;			
			Type test = (Type)c.value;
			int typeIndex = register(test);
			body = "Runtime.accepts(type" + typeIndex + ", automaton, r" + lhs + ", SCHEMA)";
		} else if(code.op == Expr.BOp.AND) {
			// special case to ensure short-circuiting of AND.
			lhs = coerceFromRef(level,code.lhs, lhs, environment);
			int target = environment.allocate(type);	
			myOut(level,comment( type2JavaType(type) + " r" + target + " = " + false + ";",code.toString()));			
			myOut(level++,"if(r" + lhs + ") {");
			int rhs = translate(level,code.rhs,environment,file);
			rhs = coerceFromRef(level,code.rhs, rhs, environment);
			myOut(level,"r" + target + " = r" + rhs + ";");
			myOut(--level,"}");			
			return target;
		} else {
			int rhs = translate(level,code.rhs,environment,file);
			// First, convert operands into values (where appropriate)
			switch(code.op) {
				case EQ:
				case NEQ:
					if(lhs_t instanceof Type.Ref && rhs_t instanceof Type.Ref) {
						// OK to do nothing here...
					} else {
						lhs = coerceFromRef(level,code.lhs, lhs, environment);
						rhs = coerceFromRef(level,code.rhs, rhs, environment);
					}
					break;
				case APPEND:
					// append is a tricky case as we have support the non-symmetic cases
					// for adding a single element to the end or the beginning of a
					// list.
					lhs_t = Type.unbox(lhs_t);
					rhs_t = Type.unbox(rhs_t);

					if(lhs_t instanceof Type.Collection) {
						lhs = coerceFromRef(level,code.lhs, lhs, environment);				
					} else {
						lhs = coerceFromValue(level, code.lhs, lhs, environment);				
					}
					if(rhs_t instanceof Type.Collection) {
						rhs = coerceFromRef(level,code.rhs, rhs, environment);	
					} else {
						rhs = coerceFromValue(level,code.rhs, rhs, environment);
					}
					break;
				case IN:
					lhs = coerceFromValue(level,code.lhs,lhs,environment);
					rhs = coerceFromRef(level,code.rhs,rhs,environment);
					break;
				default:
					lhs = coerceFromRef(level,code.lhs,lhs,environment);
					rhs = coerceFromRef(level,code.rhs,rhs,environment);
			}

			// Second, construct the body of the computation			
			switch (code.op) {
				case ADD:
					body = "r" + lhs + ".add(r" + rhs + ")";
					break;
				case SUB:
					body = "r" + lhs + ".subtract(r" + rhs + ")";
					break;
				case MUL:
					body = "r" + lhs + ".multiply(r" + rhs + ")";
					break;
				case DIV:
					body = "r" + lhs + ".divide(r" + rhs + ")";
					break;
				case OR:
					body = "r" + lhs + " || r" + rhs ;
					break;
				case EQ:
					if(lhs_t instanceof Type.Ref && rhs_t instanceof Type.Ref) { 
						body = "r" + lhs + " == r" + rhs ;
					} else {
						body = "r" + lhs + ".equals(r" + rhs +")" ;
					}
					break;
				case NEQ:
					if(lhs_t instanceof Type.Ref && rhs_t instanceof Type.Ref) {
						body = "r" + lhs + " != r" + rhs ;
					} else {
						body = "!r" + lhs + ".equals(r" + rhs +")" ;
					}
					break;
				case LT:
					body = "r" + lhs + ".compareTo(r" + rhs + ")<0";
					break;
				case LTEQ:
					body = "r" + lhs + ".compareTo(r" + rhs + ")<=0";
					break;
				case GT:
					body = "r" + lhs + ".compareTo(r" + rhs + ")>0";
					break;
				case GTEQ:
					body = "r" + lhs + ".compareTo(r" + rhs + ")>=0";
					break;
				case APPEND: 
					if (lhs_t instanceof Type.Collection) {
						body = "r" + lhs + ".append(r" + rhs + ")";
					} else {
						body = "r" + rhs + ".appendFront(r" + lhs + ")";
					}
					break;
				case DIFFERENCE:
					body = "r" + lhs + ".removeAll(r" + rhs + ")";
					break;
				case IN:
					body = "r" + rhs + ".contains(r" + lhs + ")";
					break;
				case RANGE:
					body = "Runtime.rangeOf(automaton,r" + lhs + ",r" + rhs + ")";
					break;
				default:
					throw new RuntimeException("unknown binary operator encountered: "
							+ code);
			}
		}
		int target = environment.allocate(type);	
		myOut(level,comment( type2JavaType(type) + " r" + target + " = " + body + ";",code.toString()));
		return target;
	}
	
	public int translate(int level, Expr.NaryOp code, Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		String body = "new Automaton.";				
		
		if(code.op == Expr.NOp.LISTGEN) { 
			body += "List(";
		} else if(code.op == Expr.NOp.BAGGEN) { 
			body += "Bag(";
		} else {
			body += "Set(";
		}
		
		List<Expr> arguments = code.arguments;
		for(int i=0;i!=arguments.size();++i) {
			if(i != 0) {
				body += ", ";
			}
			Expr argument = arguments.get(i);
			int reg = translate(level, argument, environment, file);
			reg = coerceFromValue(level, argument, reg, environment);
			body += "r" + reg;
		}
		
		int target = environment.allocate(type);
		myOut(level,comment(type2JavaType(type) + " r" + target + " = " + body + ");",code.toString()));
		return target;
	}
	
	public int translate(int level, Expr.ListAccess code, Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		int src = translate(level,code.src, environment,file);		
		int idx = translate(level,code.index, environment,file);
		src = coerceFromRef(level,code.src, src, environment);
		idx = coerceFromRef(level,code.index, idx, environment);
		
		String body = "r" + src + ".indexOf(r" + idx + ")";
				
		int target = environment.allocate(type);
		myOut(level,comment(type2JavaType(type) + " r" + target + " = " + body + ";",code.toString()));
		return target;
	}
	
	public int translate(int level, Expr.ListUpdate code, Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		int src = translate(level,code.src, environment, file);		
		int idx = translate(level,code.index, environment, file);
		int value = translate(level,code.value, environment, file);
		
		src = coerceFromRef(level,code.src, src, environment);
		idx = coerceFromRef(level,code.index, idx, environment);
		value = coerceFromValue(level,code.value, value, environment);
		
		String body = "r" + src + ".update(r" + idx + ", r" + value + ")";
				
		int target = environment.allocate(type);
		myOut(level,comment(type2JavaType(type) + " r" + target + " = " + body + ";",code.toString()));
		return target;
	}
	
	public int translate(int level, Expr.Constructor code,
			Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		String body;

		if (code.argument == null) {
			body = code.name;
		} else {
			int arg = translate(level, code.argument, environment, file);
			if(code.external) {
				body = file.name + "$native." + code.name + "(automaton, r" + arg + ")";
			} else { 
				arg = coerceFromValue(level,code.argument,arg,environment);
				body = "new Automaton.Term(K_" + code.name + ",r"
					+  arg + ")";
			}
		}

		int target = environment.allocate(type);
		myOut(level,  type2JavaType(type) + " r" + target + " = " + body + ";");
		return target;
	}
	
	public int translate(int level, Expr.Variable code, Environment environment, SpecFile file) {
		Integer operand = environment.get(code.var);
		if(operand != null) {
			return environment.get(code.var);
		} else {
			Type type = code
					.attribute(Attribute.Type.class).type;
			int target = environment.allocate(type);
			myOut(level, type2JavaType(type) + " r" + target + " = " + code.var + ";");
			return target;
		}
	}
	
	public int translate(int level, Expr.Substitute code, Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		
		// first, translate all subexpressions and make sure they are
		// references.
		int src = translate(level, code.src, environment, file);
		src = coerceFromValue(level,code.src,src,environment);
		
		int original = translate(level, code.original, environment, file);
		original = coerceFromValue(level,code.original,original,environment);
		
		int replacement = translate(level, code.replacement, environment, file);
		replacement = coerceFromValue(level,code.replacement,replacement,environment);
		
		// second, put in place the substitution
		String body = "automaton.substitute(r" + src + ", r" + original + ", r" + replacement + ")";
		int target = environment.allocate(type);
		myOut(level,  type2JavaType(type) + " r" + target + " = " + body + ";");
		return target;
	}
	
	public int translate(int level, Expr.TermAccess code, Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;

		// first translate src expression, and coerce to a value
		int src = translate(level, code.src, environment, file);
		src = coerceFromRef(level, code.src, src, environment);

		String body = "r" + src + ".contents";

		int target = environment.allocate(type);
		myOut(level, type2JavaType(type) + " r" + target + " = " + body + ";");
		return target;
	}
	
	public int translate(int level, Expr.Comprehension expr, Environment environment, SpecFile file) {		
		Type type = expr.attribute(Attribute.Type.class).type;
		int target = environment.allocate(type);
		
		// first, translate all source expressions
		int[] sources = new int[expr.sources.size()];
		for(int i=0;i!=sources.length;++i) {
			Pair<Expr.Variable,Expr> p = expr.sources.get(i);
			int operand = translate(level,p.second(),environment,file);
			operand = coerceFromRef(level,p.second(),operand,environment);
			sources[i] = operand;									
		}
		
		// TODO: initialise result set
		myOut(level, "Automaton.List t" + target + " = new Automaton.List();");
		int startLevel = level;
		
		// initialise result register if needed
		switch(expr.cop) {		
		case NONE:
			myOut(level,type2JavaType(type) + " r" + target + " = true;");
			myOut(level,"outer:");
			break;
		case SOME:
			myOut(level,type2JavaType(type) + " r" + target + " = false;");
			myOut(level,"outer:");
			break;
		}
		
		// second, generate all the for loops
		for (int i = 0; i != sources.length; ++i) {
			Pair<Expr.Variable, Expr> p = expr.sources.get(i);
			Expr.Variable variable = p.first();
			Expr source = p.second();
			Type.Collection sourceType = (Type.Collection) source
					.attribute(Attribute.Type.class).type;
			Type elementType = variable.attribute(Attribute.Type.class).type;
			int index = environment.allocate(elementType, variable.var);
			myOut(level++, "for(int i" + index + "=0;i" + index + "<r"
					+ sources[i] + ".size();i" + index + "++) {");
			String rhs = "r"+ sources[i] + ".get(i" + index + ")";
			// FIXME: need a more general test for a reference type
			if(!(elementType instanceof Type.Ref)) {
				rhs = "automaton.get(" + rhs + ");";
			}
			myOut(level, type2JavaType(elementType) + " r" + index + " = (" + type2JavaType(elementType) + ") " + rhs + ";");			
		}
		
		if(expr.condition != null) {
			int condition = translate(level,expr.condition,environment,file);
			myOut(level++,"if(r" + condition + ") {");			
		}
		
		switch(expr.cop) {
		case SETCOMP:
		case BAGCOMP:
		case LISTCOMP:
			int result = translate(level,expr.value,environment,file);
			result = coerceFromValue(level,expr.value,result,environment);
			myOut(level,"t" + target + ".add(r" + result + ");");
			break;
		case NONE:
			myOut(level,"r" + target + " = false;");
			myOut(level,"break outer;");
			break;
		case SOME:
			myOut(level,"r" + target + " = true;");
			myOut(level,"break outer;");
			break;
		}
		// finally, terminate all the for loops
		while(level > startLevel) {
			myOut(--level,"}");
		}

		switch(expr.cop) {
		case SETCOMP:
			myOut(level, type2JavaType(type) + " r" + target
				+ " = new Automaton.Set(t" + target + ".toArray());");
			break;
		case BAGCOMP:
			myOut(level, type2JavaType(type) + " r" + target
				+ " = new Automaton.Bag(t" + target + ".toArray());");
			break;		
		case LISTCOMP:
			myOut(level, type2JavaType(type) + " r" + target
				+ " = t" + target + ";");
			break;		
		}

		return target;
	}
	
	protected void writeMainMethod() {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Main Method");
		myOut(1,
				"// =========================================================================");
		myOut();
		myOut(1, "public static void main(String[] args) throws IOException {");
		myOut(2, "try {");
		myOut(3,
				"PrettyAutomataReader reader = new PrettyAutomataReader(System.in,SCHEMA);");
		myOut(3,
				"PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,SCHEMA);");
		myOut(3, "Automaton automaton = reader.read();");
		myOut(3, "System.out.print(\"PARSED: \");");
		myOut(3, "print(automaton);");
		myOut(3, "new SimpleRewriter(inferences,reductions).apply(automaton);");
		myOut(3, "System.out.print(\"REWROTE: \");");
		myOut(3, "print(automaton);");						
		//myOut(3, "System.out.println(\"(Reductions=\" + numReductions + \", Inferences=\" + numInferences + \", Misinferences=\" + numMisinferences + \", steps = \" + numSteps + \")\");");
		myOut(2, "} catch(PrettyAutomataReader.SyntaxError ex) {");
		myOut(3, "System.err.println(ex.getMessage());");
		myOut(2, "}");
		myOut(1, "}");
		
		myOut(1,"");
		myOut(1,"static void print(Automaton automaton) {");
		myOut(2,"try {");
		myOut(3,
				"PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,SCHEMA);");
		myOut(3, "writer.write(automaton);");
		myOut(3, "writer.flush();");
		myOut(3, "System.out.println();");
		myOut(2,"} catch(IOException e) { System.err.println(\"I/O error printing automaton\"); }");
		myOut(1,"}");
	}

	public String comment(String code, String comment) {
		int nspaces = 30 - code.length();
		String r = "";
		for(int i=0;i<nspaces;++i) {
			r += " ";
		}
		return code + r + " // " + comment;
	}
	
	/**
	 * Convert a Wyrl type into its equivalent Java type.  
	 * 
	 * @param type
	 * @return
	 */
	public String type2JavaType(Type type) {
		return type2JavaType(type,true);
	}
	
	/**
	 * Convert a Wyrl type into its equivalent Java type. The user specifies
	 * whether primitive types are allowed or not. If not then, for example,
	 * <code>Type.Int</code> becomes <code>int</code>; otherwise, it becomes
	 * <code>Integer</code>.
	 * 
	 * @param type
	 * @return
	 */
	public String type2JavaType(Type type, boolean primitives) {
		if (type instanceof Type.Any) {
			return "Object";
		} else if (type instanceof Type.Int) {
			return "Automaton.Int";
		} else if (type instanceof Type.Real) {
			return "Automaton.Real";
		} else if (type instanceof Type.Bool) {
			return "boolean";
		} else if (type instanceof Type.Strung) {
			return "Automaton.Strung";
		} else if (type instanceof Type.Term) {
			return "Automaton.Term";
		} else if (type instanceof Type.Ref) {
			if(primitives) {
				return "int";
			} else {
				return "Integer";
			}
		} else if (type instanceof Type.Nominal) {
			Type.Nominal nom = (Type.Nominal) type;
			return type2JavaType(nom.element(), primitives);
		} else if (type instanceof Type.Or) {
			return "Object";
		} else if (type instanceof Type.List) {
			return "Automaton.List";			
		} else if (type instanceof Type.Bag) {
			return "Automaton.Bag";
		} else if (type instanceof Type.Set) {
			return "Automaton.Set";
		}
		throw new RuntimeException("unknown type encountered: " + type);
	}
	
	public int coerceFromValue(int level, Expr expr, int register, Environment environment) {
		Type type = expr.attribute(Attribute.Type.class).type;
		if(type instanceof Type.Ref) {
			return register;
		} else {
			Type.Ref refType = Type.T_REF(type);
			int result = environment.allocate(refType);
			String src = "r" + register;
			if(refType.element() instanceof Type.Bool) {
				// special thing needed for bools
				src = src + " ? Automaton.TRUE : Automaton.FALSE";
			}
			myOut(level, type2JavaType(refType) + " r" + result + " = automaton.add(" + src + ");");
			return result;
		}
	}

	public int coerceFromRef(int level, SyntacticElement elem, int register,
			Environment environment) {
		Type type = elem.attribute(Attribute.Type.class).type;
		
		if (type instanceof Type.Ref) {
			Type.Ref refType = (Type.Ref) type;
			Type element = refType.element();
			int result = environment.allocate(element);
			String cast = type2JavaType(element);			
			String body = "automaton.get(r" + register + ")";
			// special case needed for booleans
			if(element instanceof Type.Bool) {
				body = "((Automaton.Bool)" + body + ").value";
			} else {
				body = "(" + cast + ") " + body;
			}
			myOut(level, cast + " r" + result + " = " + body + ";");
			return result;
		} else {
			return register;
		}
	}
	
	protected void myOut() {
		myOut(0, "");
	}

	protected void myOut(int level) {
		myOut(level, "");
	}

	protected void myOut(String line) {
		myOut(0, line);
	}

	protected void myOut(int level, String line) {
		for (int i = 0; i < level; ++i) {
			out.print("\t");
		}
		out.println(line);
	}

	protected void indent(int level) {
		for (int i = 0; i < level; ++i) {
			out.print("\t");
		}
	}
		
	private HashMap<Type,Integer> registeredTypes = new HashMap<Type,Integer>();
	private ArrayList<Type> typeRegister = new ArrayList<Type>();	
	
	private int register(Type t) {
		//Types.reduce(t.automaton());
		Integer i = registeredTypes.get(t);
		if(i == null) {
			int r = typeRegister.size();
			registeredTypes.put(t, r);
			typeRegister.add(t);
			return r;
		} else {
			return i;
		}
	}
	
	private static final class Environment {
		private final HashMap<String, Integer> var2idx = new HashMap<String, Integer>();
		private final ArrayList<Pair<Type,String>> idx2var = new ArrayList<Pair<Type,String>>();

		public int size() {
			return idx2var.size();
		}
		
		public int allocate(Type t) {
			int idx = idx2var.size();
			idx2var.add(new Pair<Type,String>(t,null));
			return idx;
		}

		public int allocate(Type t, String v) {
			int idx = idx2var.size();
			idx2var.add(new Pair<Type,String>(t,v));
			var2idx.put(v, idx);
			return idx;
		}

		public Integer get(String v) {
			return var2idx.get(v);
		}

		public Pair<Type,String> get(int idx) {
			return idx2var.get(idx);
		}
		
		public void put(int idx, String v) {
			var2idx.put(v, idx);
			idx2var.set(idx,
					new Pair<Type, String>(idx2var.get(idx).first(), v));
		}

		public String toString() {
			return var2idx.toString();
		}
	}
}
