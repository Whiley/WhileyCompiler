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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.*;

import wyautl.core.Automaton;
import wyautl.rw.InferenceRule;
import wyautl.rw.ReductionRule;
import wyautl.rw.SimpleRewriteStrategy;
import wyautl.util.BigRational;
import wyfs.io.BinaryOutputStream;
import wyrl.core.Attribute;
import wyrl.core.Expr;
import wyrl.core.Exprs;
import wyrl.core.Pattern;
import wyrl.core.SpecFile;
import wyrl.core.Type;
import wyrl.core.SpecFile.RuleDecl;
import wyrl.util.*;
import static wyrl.core.SpecFile.*;

/**
 * Responsible for translating a <code>SpecFile</code> into Java source code.
 *
 * @author David J. Pearce
 *
 */
public class JavaFileWriter {
	private PrintWriter out;
	private final HashMap<String, Type.Term> terms = new HashMap<String, Type.Term>();

	public JavaFileWriter(Writer os) {
		this.out = new PrintWriter(os);
	}

	public JavaFileWriter(OutputStream os) {
		this.out = new PrintWriter(os);
	}

	public void write(SpecFile spec) throws IOException {
		reset();
		translate(spec, spec);
	}

	private void translate(SpecFile spec, SpecFile root) throws IOException {
		PrintWriter saved = out;

		if (root == spec) {
			buildTerms(root);

			if (!spec.pkg.equals("")) {
				myOut("package " + spec.pkg + ";");
				myOut("");
			}

			writeImports();
			myOut("public final class " + spec.name + " {");

		}

		for (Decl d : spec.declarations) {
			if (d instanceof IncludeDecl) {
				IncludeDecl id = (IncludeDecl) d;
				SpecFile file = id.file;
				translate(file, root);
			} else if (d instanceof TermDecl) {
				translate((TermDecl) d);
			} else if (d instanceof RewriteDecl) {
				translate((RewriteDecl) d, root);
			}
		}

		if (root == spec) {
			writeSchema(spec);
			writeTypeTests();
			writePatterns(spec);
			writeRuleArrays(spec);
			writeMainMethod();
		}

		if (root == spec) {
			myOut("}");
			out.close();
		}

		out = saved;
	}

	public void buildTerms(SpecFile spec) {
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.IncludeDecl) {
				SpecFile.IncludeDecl id = (SpecFile.IncludeDecl) d;
				buildTerms(id.file);
			} else if (d instanceof SpecFile.TermDecl) {
				SpecFile.TermDecl td = (SpecFile.TermDecl) d;
				terms.put(td.type.name(), td.type);
			}
		}
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
		myOut("import wyrl.core.*;");
		myOut("import wyrl.util.Runtime;");
		myOut("import wyrl.util.Pair;");
		myOut("import wyrl.util.AbstractRewriteRule;");
		myOut();
	}

	public void translate(TermDecl decl) {
		myOut(1, "// term " + decl.type);
		String name = decl.type.name();
		myOut(1, "public final static int K_" + name + " = " + termCounter++
				+ ";");
		if (decl.type.element() == null) {
			myOut(1, "public final static Automaton.Term " + name
					+ " = new Automaton.Term(K_" + name + ");");
		} else {
			Type.Ref<?> data = decl.type.element();
			Type element = data.element();
			if (element instanceof Type.Collection) {
				// add two helpers
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, int... r0) {");
				if (element instanceof Type.Set) {
					myOut(2, "int r1 = automaton.add(new Automaton.Set(r0));");
				} else if (element instanceof Type.Bag) {
					myOut(2, "int r1 = automaton.add(new Automaton.Bag(r0));");
				} else {
					myOut(2, "int r1 = automaton.add(new Automaton.List(r0));");
				}
				myOut(2, "return automaton.add(new Automaton.Term(K_" + name
						+ ", r1));");
				myOut(1, "}");

				myOut(1, "public final static int " + name
						+ "(Automaton automaton, List<Integer> r0) {");
				if (element instanceof Type.Set) {
					myOut(2, "int r1 = automaton.add(new Automaton.Set(r0));");
				} else if (element instanceof Type.Bag) {
					myOut(2, "int r1 = automaton.add(new Automaton.Bag(r0));");
				} else {
					myOut(2, "int r1 = automaton.add(new Automaton.List(r0));");
				}
				myOut(2, "return automaton.add(new Automaton.Term(K_" + name
						+ ", r1));");
				myOut(1, "}");
			} else if (element instanceof Type.Int) {
				// add two helpers
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, long r0) {");
				myOut(2, "int r1 = automaton.add(new Automaton.Int(r0));");
				myOut(2, "return automaton.add(new Automaton.Term(K_" + name
						+ ", r1));");
				myOut(1, "}");

				myOut(1, "public final static int " + name
						+ "(Automaton automaton, BigInteger r0) {");
				myOut(2, "int r1 = automaton.add(new Automaton.Int(r0));");
				myOut(2, "return automaton.add(new Automaton.Term(K_" + name
						+ ", r1));");
				myOut(1, "}");
			} else if (element instanceof Type.Real) {
				// add two helpers
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, long r0) {");
				myOut(2, "int r1 = automaton.add(new Automaton.Real(r0));");
				myOut(2, "return automaton.add(new Automaton.Term(K_" + name
						+ ", r1));");
				myOut(1, "}");

				myOut(1, "public final static int " + name
						+ "(Automaton automaton, BigRational r0) {");
				myOut(2, "int r1 = automaton.add(new Automaton.Real(r0));");
				myOut(2, "return automaton.add(new Automaton.Term(K_" + name
						+ ", r1));");
				myOut(1, "}");
			} else if (element instanceof Type.Strung) {
				// add two helpers
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, String r0) {");
				myOut(2, "int r1 = automaton.add(new Automaton.Strung(r0));");
				myOut(2, "return automaton.add(new Automaton.Term(K_" + name
						+ ", r1));");
				myOut(1, "}");
			} else {
				myOut(1, "public final static int " + name
						+ "(Automaton automaton, " + type2JavaType(data)
						+ " r0) {");
				myOut(2, "return automaton.add(new Automaton.Term(K_" + name
						+ ", r0));");
				myOut(1, "}");
			}

		}
		myOut();
	}

	private int termCounter = 0;
	private int reductionCounter = 0;
	private int inferenceCounter = 0;

	public void translate(RewriteDecl decl, SpecFile file) {
		register(decl.pattern);

		boolean isReduction = decl instanceof ReduceDecl;
		Type param = decl.pattern.attribute(Attribute.Type.class).type;

		if(decl.name != null) {
			myOut(1, "// " + decl.name);
		}

		String className = isReduction ? "Reduction_" + reductionCounter++ : "Inference_" + inferenceCounter++;

		if (isReduction) {
			myOut(1, "private final static class " + className
					+ " extends AbstractRewriteRule implements ReductionRule {");
		} else {
			myOut(1, "private final static class " + className
					+ " extends AbstractRewriteRule implements InferenceRule {");
		}

		// ===============================================
		// Constructor
		// ===============================================
		myOut();
		myOut(2,"public " + className + "(Pattern.Term pattern) { super(pattern); }");

		// ===============================================
		// probe()
		// ===============================================
		myOut();

		myOut(2,
				"public final void probe(Automaton automaton, int root, List<Activation> activations) {");
		Environment environment = new Environment();
		int thus = environment.allocate(param, "this");
		myOut(3, "int r" + thus + " = root;");
		int level = translatePatternMatch(3, decl.pattern, null, thus,
				environment);

		// Add the appropriate activation
		indent(level);
		out.print("int[] state = {");
		for (int i = 0; i != environment.size(); ++i) {
			Pair<Type, String> t = environment.get(i);
			if (i != 0) {
				out.print(", ");
			}
			if (t.first() == Type.T_VOID()) {
				// In this case, we have allocated a temporary variable which
				// should not be loaded into the activation state (because it
				// will be out of scope).
				out.print("0");
			} else {
				out.print("r" + i);
			}
		}
		out.println("};");

		myOut(level, "activations.add(new Activation(this,null,state));");

		// close the pattern match
		while (level > 2) {
			myOut(--level, "}");
		}

		// ===============================================
		// apply()
		// ===============================================

		myOut();
		myOut(2,
				"public final int apply(Automaton automaton, int[] state) {");
		myOut(3, "int nStates = automaton.nStates();");

		// first, unpack the state
		environment = new Environment();
		thus = environment.allocate(param, "this");
		myOut(3, "int r" + thus + " = state[0];");
		translateStateUnpack(3, decl.pattern, thus, environment);

		// second, translate the individual rules
		for (RuleDecl rd : decl.rules) {
			translate(3, rd, isReduction, environment, file);
		}

		myOut(3, "automaton.resize(nStates);");
		myOut(3, "return Automaton.K_VOID;");
		myOut(2, "}");

		// ===============================================
		// name() and rank()
		// ===============================================

		myOut(2, "public final String name() { return \"" + decl.name + "\"; }");
		myOut(2, "public final int rank() { return " + decl.rank + "; }");

		// ===============================================
		// min / max reduction sizes
		// ===============================================

		myOut();
		//
		int minComplexity = RewriteComplexity.minimumChange(decl);
		myOut(2, "public final int minimum() { return " + minComplexity + "; }");
		//myOut(2, "public final int minimum() { return 0; }");
		myOut(2, "public final int maximum() { return Integer.MAX_VALUE; }");

		myOut(1, "}"); // end class
	}

	/**
	 * Translate the test to see whether a pattern is accepted or not. A key
	 * requirement of this translation procedure is that it does not allocate
	 * *any* memory during the process.
	 *
	 * @param pattern
	 *            The pattern being translated
	 * @param freeRegister
	 *            The next available free register.
	 * @return The next available free register after this translation.
	 */
	protected int translatePatternMatch(int level, Pattern pattern,
			Type declared, int source, Environment environment) {
		if (pattern instanceof Pattern.Leaf) {
			return translatePatternMatch(level, (Pattern.Leaf) pattern,
					stripNominalsAndRefs(declared), source, environment);
		} else if (pattern instanceof Pattern.Term) {
			return translatePatternMatch(level, (Pattern.Term) pattern,
					stripNominalsAndRefs(declared), source, environment);
		} else if (pattern instanceof Pattern.BagOrSet) {
			// I think the cast from declared to Type.Collection is guaranteed
			// to be safe since we can't have e.g. a union of [int]|{int}
			return translatePatternMatch(level, (Pattern.BagOrSet) pattern,
					(Type.Collection) stripNominalsAndRefs(declared), source,
					environment);
		} else {
			// I think the cast from declared to Type.List is guaranteed to be
			// safe since we can't have e.g. a union of [int]|{int}
			return translatePatternMatch(level, (Pattern.List) pattern,
					(Type.List) stripNominalsAndRefs(declared), source, environment);
		}
	}

	public int translatePatternMatch(int level, Pattern.Leaf pattern,
			Type declared, int source, Environment environment) {
		Type element = pattern.type().element();

		if (element == Type.T_ANY() || element.isSubtype(declared)) {
			// In this very special case, we don't need to do anything since
			// we're guarantted to have a match based on the context.
			return level;
		} else {
			int typeIndex = register(pattern.type);
			myOut(level++, "if(Runtime.accepts(type" + typeIndex
					+ ",automaton,automaton.get(r" + source + "), SCHEMA)) {");
			return level;
		}
	}

	public int translatePatternMatch(int level, Pattern.Term pattern,
			Type declared, int source, Environment environment) {

		myOut(level, "Automaton.State s" + source + " = automaton.get(r"
				+ source + ");");

		// ====================================================================
		// First, determine what we know about this term.
		// ====================================================================
		Type.Ref element = null;

		if (declared instanceof Type.Term) {
			// In this case, we are guaranteed to have a term at this point
			// (and, in fact, it must match this pattern otherwise, we'd have a
			// type error).
			Type.Term tt = (Type.Term) declared;
			element = tt.element();
		} else {
			// In this case, don't know much about the term we are matching.
			// Furthermore, we need to check whether we have the right
			// term.
			myOut(level++, "if(s" + source + ".kind == K_" + pattern.name
					+ ") {");
			Type.Term concrete = terms.get(pattern.name);
			element = concrete.element();
		}

		// ====================================================================
		// Second, recursively check whether the data element matches
		// ====================================================================
		if (pattern.data != null) {
			myOut(level, "Automaton.Term t" + source + " = (Automaton.Term) s"
					+ source + ";");
			int target = environment.allocate(Type.T_ANY(), pattern.variable);
			myOut(level, "int r" + target + " = t" + source + ".contents;");
			return translatePatternMatch(level, pattern.data,
					element.element(), target, environment);
		} else {
			return level;
		}
	}

	public int translatePatternMatch(int level, Pattern.List pattern,
			Type.List declared, int source, Environment environment) {
		if (pattern.unbounded) {
			return translateUnboundedPatternMatch(level, pattern, declared,
					source, environment);
		} else {
			return translateBoundedPatternMatch(level, pattern, declared,
					source, environment);
		}
	}

	public int translateBoundedPatternMatch(int level, Pattern.List pattern,
			Type.List declared, int source, Environment environment) {
		myOut(level, "Automaton.State s" + source + " = automaton.get(r"
				+ source + ");");
		myOut(level, "Automaton.List l" + source + " = (Automaton.List) s"
				+ source + ";");

		// ====================================================================
		// First, extract what we know about this list
		// ====================================================================

		Pair<Pattern, String>[] pattern_elements = pattern.elements;
		Type[] declared_elements = declared.elements();

		if (declared.unbounded()) {
			// In this case, we have a fixed-size list pattern being matched
			// against an unbounded list type. Therefore, we must check that the
			// type being matched does indeed have the right size.
			myOut(level++, "if(l" + source + ".size() == "
					+ pattern_elements.length + ") {");
		}

		// ====================================================================
		// Second, recursively check sub-elements.
		// ====================================================================

		// Don't visit final element, since this is the unbounded match.
		for (int i = 0, j = 0; i != pattern_elements.length; ++i) {
			Pair<Pattern, String> p = pattern_elements[i];
			int element = environment.allocate(Type.T_ANY());
			myOut(level, "int r" + element + " = l" + source + ".get(" + i
					+ ");");
			level = translatePatternMatch(level, p.first(), declared_elements[j],
					element, environment);

			// Increment j upto (but not past) the final declared element.
			j = Math.min(j + 1, declared_elements.length - 1);
		}

		// Done.

		return level;
	}

	public int translateUnboundedPatternMatch(int level, Pattern.List pattern,
			Type.List declared, int source, Environment environment) {
		myOut(level, "Automaton.State s" + source + " = automaton.get(r"
				+ source + ");");
		myOut(level, "Automaton.List l" + source + " = (Automaton.List) s"
				+ source + ";");

		// ====================================================================
		// First, extract what we know about this list
		// ====================================================================

		Pair<Pattern, String>[] pattern_elements = pattern.elements;
		Type[] declared_elements = declared.elements();

		if (pattern_elements.length != declared_elements.length) {
			// In this case, we have an unbounded list pattern being matched
			// against an unbounded list type, but the former required more
			// elements than are guaranteed by the latter; therefore, we need to
			// check there are enough elements.
			myOut(level++, "if(l" + source + ".size() >= "
					+ (pattern_elements.length - 1) + ") {");
		}

		// ====================================================================
		// Second, recursively check sub-elements.
		// ====================================================================

		// Don't visit final element, since this is the unbounded match.
		for (int i = 0, j = 0; i != (pattern_elements.length-1); ++i) {
			Pair<Pattern, String> p = pattern_elements[i];
			int element = environment.allocate(Type.T_ANY());
			myOut(level, "int r" + element + " = l" + source + ".get(" + i
					+ ");");
			level = translatePatternMatch(level, p.first(), declared_elements[j],
					element, environment);

			// Increment j upto (but not past) the final declared element.
			j = Math.min(j + 1, declared_elements.length - 1);
		}

		// ====================================================================
		// Third, check all remaining elements against the unbounded match.
		// ====================================================================

		int lastPatternElementIndex = pattern_elements.length-1;
		Pattern lastPatternElement = pattern_elements[lastPatternElementIndex].first();
		Type lastDeclaredElement = declared_elements[declared_elements.length-1];
		int element = environment.allocate(Type.T_VOID());

		if(!willSkip(lastPatternElement,lastDeclaredElement)) {

			// Only include the loop if we really need it. In many cases, this
			// is not necessary because it's just matching against what we
			// already can guarantee is true.

			String idx = "i" + source;
			myOut(level, "boolean m" + source + " = true;");
			myOut(level++, "for(int " + idx + "=" + lastPatternElementIndex
					+ "; " + idx + " < l" + source + ".size(); " + idx + "++) {");
			myOut(level, "int r" + element + " = l" + source + ".get("
					+ idx + ");");
			int myLevel = level;
			level = translatePatternMatch(level, lastPatternElement,
					lastDeclaredElement, element, environment.clone());
			if (myLevel != level) {
				myOut(level, "continue;");
				myOut(--level, "}");				
				while (level > myLevel) {
					myOut(--level, "}");
				}
				myOut(level,"m" + source + " = false;");
				myOut(level,"break;");
				myOut(--level,"}");
			} 			
			myOut(level++, "if(m" + source + ") {");
		}
		// done

		return level;
	}

	public int translatePatternMatch(int level, Pattern.BagOrSet pattern,
			Type.Collection declared, int source, Environment environment) {
		if (pattern.unbounded) {
			return translateUnboundedPatternMatch(level, pattern, declared,
					source, environment);
		} else {
			return translateBoundedPatternMatch(level, pattern, declared,
					source, environment);
		}
	}

	public int translateBoundedPatternMatch(int level, Pattern.BagOrSet pattern,
			Type.Collection declared, int source, Environment environment) {

		myOut(level, "Automaton.State s" + source + " = automaton.get(r"
				+ source + ");");
		myOut(level, "Automaton.Collection c" + source
				+ " = (Automaton.Collection) s" + source + ";");

		// ====================================================================
		// First, extract what we know about this set or bag
		// ====================================================================

		Pair<Pattern, String>[] elements = pattern.elements;
		Type[] declared_elements = declared.elements();
		if (declared.unbounded()) {
			// In this case, we have a fixed-size list pattern being matched
			// against an unbounded list type. Therefore, we must check that the
			// type being matched does indeed have the right size.
			myOut(level++, "if(c" + source + ".size() == " + elements.length
					+ ") {");
		}

		// ====================================================================
		// Second, recursively check sub-elements.
		// ====================================================================

		// What we do here is construct a series of nested for-loops (one for
		// each pattern element) which goes through each element of the source
		// collection and attempts to match the element. In doing this, we must
		// ensure that no previously matched elements are matched again.

		int[] indices = new int[elements.length];
		for (int i = 0, j = 0; i != elements.length; ++i) {
			Pattern pat = elements[i].first();
			int item = environment.allocate(Type.T_ANY());
			int index = environment.allocate(Type.T_ANY());
			String idx = "r" + index;
			indices[i] = index;

			// Construct the for-loop for this element
			myOut(level++, "for(int " + idx + "=0;" + idx + "!=c" + source
					+ ".size();++" + idx + ") {");

			// Check that the current element from the source collection is not
			// already matched. If this is the first pattern element (i.e. i ==
			// 0), then we don't need to do anything since nothing could have
			// been matched yet.
			if (i != 0) {
				indent(level);
				out.print("if(");
				// check against earlier indices
				for (int k = 0; k < i; ++k) {
					if (k != 0) {
						out.print(" || ");
					}
					out.print(idx + " == r" + indices[k]);
				}
				out.println(") { continue; }");
			}
			myOut(level, "int r" + item + " = c" + source + ".get(" + idx
					+ ");");

			level = translatePatternMatch(level, pat, declared_elements[j], item, environment);

			// Increment j upto (but not past) the final declared element.
			j = Math.min(j + 1, declared_elements.length - 1);
		}

		// Done.

		return level;
	}

	public int translateUnboundedPatternMatch(int level, Pattern.BagOrSet pattern,
			Type.Collection declared, int source, Environment environment) {
		myOut(level, "Automaton.State s" + source + " = automaton.get(r"
				+ source + ");");
		myOut(level, "Automaton.Collection c" + source
				+ " = (Automaton.Collection) s" + source + ";");

		// ====================================================================
		// First, extract what we know about this set or bag
		// ====================================================================

		Pair<Pattern, String>[] pattern_elements = pattern.elements;
		Type[] declared_elements = declared.elements();

		if (pattern_elements.length != declared_elements.length) {
			// In this case, we have an unbounded list pattern being matched
			// against an unbounded list type, but the former required more
			// elements than are guaranteed by the latter; therefore, we need to
			// check there are enough elements.
			myOut(level++, "if(c" + source + ".size() >= "
					+ (pattern_elements.length - 1) + ") {");
		}

		// ====================================================================
		// Second, recursively check sub-elements.
		// ====================================================================

		// What we do here is construct a series of nested for-loops (one for
		// each pattern element) which goes through each element of the source
		// collection and attempts to match the element. In doing this, we must
		// ensure that no previously matched elements are matched again.
		// Furthermore, in the case of an unbounded match (i.e. where the
		// pattern has a generic match against all remaining elements), then we
		// simply go through all unmatched elements making sure they match the
		// required pattern.

		int[] indices = new int[pattern_elements.length];
		for (int i = 0, j = 0; i != pattern_elements.length - 1; ++i) {
			Pattern pat = pattern_elements[i].first();
			int item = environment.allocate(Type.T_ANY());
			int index = environment.allocate(Type.T_ANY());
			String idx = "r" + index;
			indices[i] = index;

			// Construct the for-loop for this element
			myOut(level++, "for(int " + idx + "=0;" + idx + "!=c" + source
					+ ".size();++" + idx + ") {");

			// Check that the current element from the source collection is not
			// already matched. If this is the first pattern element (i.e. i ==
			// 0), then we don't need to do anything since nothing could have
			// been matched yet.
			if (i != 0) {
				indent(level);
				out.print("if(");
				// check against earlier indices
				for (int k = 0; k < i; ++k) {
					if (k != 0) {
						out.print(" || ");
					}
					out.print(idx + " == r" + indices[k]);
				}
				out.println(") { continue; }");
			}
			myOut(level, "int r" + item + " = c" + source + ".get(" + idx
					+ ");");

			level = translatePatternMatch(level, pat, declared_elements[j], item, environment);

			// Increment j upto (but not past) the final declared element.
			j = Math.min(j + 1, declared_elements.length - 1);
		}

		// ====================================================================
		// Third, check all remaining elements against the unbounded match.
		// ====================================================================
		int lastPatternElementIndex = pattern_elements.length-1;
		Pattern lastPatternElement = pattern_elements[lastPatternElementIndex].first();
		Type lastDeclaredElement = declared_elements[declared_elements.length-1];
		int item = environment.allocate(Type.T_VOID());

		if(!willSkip(lastPatternElement,lastDeclaredElement)) {

			// Only include the loop if we really need it. In many cases, this
			// is not necessary because it's just matching against what we
			// already can guarantee is true.

			String idx = "i" + item;
			myOut(level, "boolean m" + source + "_" + lastPatternElementIndex + " = true;");

			// Construct the for-loop for this element
			myOut(level++, "for(int " + idx + "=0;" + idx + "!=c" + source
					+ ".size();++" + idx + ") {");
			
			// Check that the current element from the source collection is not
			// already matched. If this is the first pattern element (i.e. i ==
			// 0), then we don't need to do anything since nothing could have
			// been matched yet.
			if (lastPatternElementIndex != 0) {
				indent(level);
				out.print("if(");
				// check against earlier indices
				for (int j = 0; j < lastPatternElementIndex; ++j) {
					if (j != 0) {
						out.print(" || ");
					}
					out.print(idx + " == r" + indices[j]);
				}
				out.println(") { continue; }");
			}
			myOut(level, "int r" + item + " = c" + source + ".get(" + idx
					+ ");");
			int myLevel = level;
			level = translatePatternMatch(level, lastPatternElement,
					lastDeclaredElement, item, environment.clone());

			// In the case that pattern is unbounded, we match all non-matched
			// items against the last pattern element. This time, we construct a
			// loop which sets a flag if it finds one that doesn't match and
			// exits early.
			if (myLevel != level) {
				myOut(level, "continue;");
				myOut(--level, "}");				
				while (level > myLevel) {
					myOut(--level, "}");
				}
				myOut(level,"m" + source + "_" + lastPatternElementIndex + " = false;");
				myOut(level,"break;");
				myOut(--level,"}");
			} 
			
			myOut(level++, "if(m" + source + "_" + lastPatternElementIndex + ") {");
		}

		// Done.
		return level;
	}

	/**
	 * The purpose of this method is to determine whether or not the given
	 * pattern actually needs to be matched in any way.
	 *
	 * @param pattern
	 * @param declared
	 * @return
	 */
	protected boolean willSkip(Pattern pattern, Type declared) {
		declared = stripNominalsAndRefs(declared);

		if (pattern instanceof Pattern.Leaf) {
			Pattern.Leaf leaf = (Pattern.Leaf) pattern;
			Type element = leaf.type().element();

			if (element == Type.T_ANY() || element.isSubtype(declared)) {
				// In this very special case, we don't need to do anything since
				// we're guarantted to have a match based on the context.
				return true;
			}
		} else if (pattern instanceof Pattern.Term
				&& declared instanceof Type.Term) {
			Pattern.Term pt = (Pattern.Term) pattern;
			Type.Term tt = (Type.Term) declared;
			if (pt.name == tt.name()) {
				if (pt.data != null && tt.element() != null) {
					return willSkip(pt.data, tt.element());
				} else {
					return pt.data == null && tt.element() == null;
				}
			}
		} else if (pattern instanceof Pattern.Collection
				&& declared instanceof Type.Collection) {
			Pattern.Collection pc = (Pattern.Collection) pattern;
			Type.Collection tc = (Type.Collection) declared;
			// I believe we can assume they must be of the same collection kind
			// here.  Otherwise, it would have failed to type check.
			Pair<Pattern,String>[] pc_elements = pc.elements;
			Type[] tc_elements = tc.elements();
			if (pc.unbounded == tc.unbounded()
					&& pc_elements.length == tc_elements.length) {
				for (int i = 0; i != tc_elements.length; ++i) {
					if (!willSkip(pc_elements[i].first(), tc_elements[i])) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Here, we simply read out all of the registers from the state. We also
	 * assign named variables so they can be used subsequently.
	 *
	 * @param level
	 * @param pattern
	 * @param environment
	 */
	protected void translateStateUnpack(int level, Pattern pattern, int source,
			Environment environment) {
		if (pattern instanceof Pattern.Leaf) {
			translateStateUnpack(level, (Pattern.Leaf) pattern, source,
					environment);
		} else if (pattern instanceof Pattern.Term) {
			translateStateUnpack(level, (Pattern.Term) pattern, source,
					environment);
		} else if (pattern instanceof Pattern.BagOrSet) {
			translateStateUnpack(level, (Pattern.BagOrSet) pattern, source,
					environment);
		} else {
			translateStateUnpack(level, (Pattern.List) pattern, source,
					environment);
		}
	}

	protected void translateStateUnpack(int level, Pattern.Leaf pattern,
			int source, Environment environment) {
		// Don't need to do anything!!
	}

	protected void translateStateUnpack(int level, Pattern.Term pattern,
			int source, Environment environment) {
		if (pattern.data != null) {
			int target = environment.allocate(Type.T_ANY(), pattern.variable);
			if (pattern.variable != null) {
				myOut(level, "int r" + target + " = state[" + target + "]; // " + pattern.variable);
			}
			translateStateUnpack(level, pattern.data, target, environment);
		}
	}

	protected void translateStateUnpack(int level, Pattern.BagOrSet pattern,
			int source, Environment environment) {

		Pair<Pattern, String>[] elements = pattern.elements;
		int[] indices = new int[elements.length];
		for (int i = 0; i != elements.length; ++i) {
			Pair<Pattern, String> p = elements[i];
			String p_name = p.second();
			int item = environment.allocate(Type.T_ANY(), p_name);
			if (pattern.unbounded && (i + 1) == elements.length) {
				if (p_name != null) {
					String src = "s" + source;
					myOut(level, "Automaton.Collection " + src
							+ " = (Automaton.Collection) automaton.get(state["
							+ source + "]);");
					String array = src + "children";
					myOut(level, "int[] " + array + " = new int[" + src
							+ ".size() - " + i + "];");
					String idx = "s" + source + "i";
					String jdx = "s" + source + "j";
					myOut(level, "for(int " + idx + "=0, " + jdx + "=0; " + idx
							+ " != " + src + ".size();++" + idx + ") {");
					if (i != 0) {
						indent(level + 1);
						out.print("if(");
						for (int j = 0; j < i; ++j) {
							if (j != 0) {
								out.print(" || ");
							}
							out.print(idx + " == r" + indices[j]);
						}
						out.println(") { continue; }");
					}
					myOut(level + 1, array + "[" + jdx + "++] = " + src + ".get(" + idx
							+ ");");
					myOut(level, "}");
					if (pattern instanceof Pattern.Set) {
						myOut(level, "Automaton.Set r" + item
								+ " = new Automaton.Set(" + array + ");");
					} else {
						myOut(level, "Automaton.Bag r" + item
								+ " = new Automaton.Bag(" + array + ");");
					}
				}

				// NOTE: calling translate unpack here is strictly unnecessary
				// because we cannot map an unbounded pattern to a single
				// variable name.

			} else {
				int index = environment.allocate(Type.T_VOID());
				indices[i] = index;
				if (p_name != null) {
					myOut(level, "int r" + item + " = state[" + item + "]; // " + p_name);
				}
				myOut(level, "int r" + index + " = state[" + index + "];");
				translateStateUnpack(level, p.first(), item, environment);
			}
		}
	}

	protected void translateStateUnpack(int level, Pattern.List pattern,
			int source, Environment environment) {

		Pair<Pattern, String>[] elements = pattern.elements;
		for (int i = 0; i != elements.length; ++i) {
			Pair<Pattern, String> p = elements[i];
			String p_name = p.second();
			if (pattern.unbounded && (i + 1) == elements.length) {
				int target = environment.allocate(Type.T_VOID(), p_name);
				if (p_name != null) {
					myOut(level, "Automaton.List r" + target
							+ " = ((Automaton.List) automaton.get(state["
							+ source + "])).sublist(" + i + ");");
				}

				// NOTE: calling translate unpack here is strictly unnecessary
				// because we cannot map an unbounded pattern to a single
				// variable name.

			} else {
				int target = environment.allocate(Type.T_ANY(), p_name);
				if (p_name != null) {
					myOut(level, "int r" + target + " = state[" + target + "]; // " + p_name);
				}
				translateStateUnpack(level, p.first(), target, environment);
			}
		}
	}

	/**
	 * Register all the types associated with this pattern and its children.
	 * This is necessary in order that we can make sure those types are
	 * instantiated before the corresponding pattern constructor is called in
	 * the generated file.
	 *
	 * @param p
	 *            --- Pattern to register.
	 */
	public void register(Pattern p) {
		if (p instanceof Pattern.Leaf) {
			Pattern.Leaf pl = (Pattern.Leaf) p;
			int typeIndex = register(pl.type);
		} else if (p instanceof Pattern.Term) {
			Pattern.Term pt = (Pattern.Term) p;
			if (pt.data != null) {
				register(pt.data);
			}
		} else if (p instanceof Pattern.Collection) {
			Pattern.Collection pc = (Pattern.Collection) p;
			for (Pair<Pattern, String> e : pc.elements) {
				register(e.first());
			}
		}
	}

	public void translate(int level, RuleDecl decl, boolean isReduce,
			Environment environment, SpecFile file) {

		// TODO: can optimise this by translating lets within the conditionals
		// in the case that the conditionals don't refer to those lets. This
		// will then prevent unnecessary object creation.

		boolean conditionDone = false;
		if (decl.condition != null && allVariablesDefined(decl.condition,environment)) {
			int condition = translate(level, decl.condition, environment, file);
			myOut(level++, "if(r" + condition + ") {");
			conditionDone = true;
		}

		for (Pair<String, Expr> let : decl.lets) {
			String letVar = let.first();
			Expr letExpr = let.second();
			int result = translate(level, letExpr, environment, file);
			environment.put(result, letVar);
			//
			if (!conditionDone && decl.condition != null
					&& allVariablesDefined(decl.condition, environment)) {
				int condition = translate(level, decl.condition, environment,
						file);
				myOut(level++, "if(r" + condition + ") {");
				conditionDone = true;
			}
		}

		if(!conditionDone && decl.condition != null) {
			// sanity check
			throw new RuntimeException("internal failure: condition not written, but was required");
		}

		int result = translate(level, decl.result, environment, file);
		result = coerceFromValue(level, decl.result, result, environment);
		int thus = environment.get("this");
		myOut(level, "if(r" + thus + " != r" + result + ") {");
		myOut(level + 1, "return automaton.rewrite(r" + thus + ", r" + result + ");");
		myOut(level, "}");
		if (decl.condition != null) {
			myOut(--level, "}");
		}
	}

	protected void writePatterns(SpecFile spec) throws IOException {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Patterns");
		myOut(1,
				"// =========================================================================");
		myOut();

		int counter = 0;
		for (Decl d : getAllDeclarations(spec)) {
			if (d instanceof RewriteDecl) {
				RewriteDecl rd = (RewriteDecl) d;
				indent(1);
				out.print("private final static Pattern.Term pattern" + counter++
						+ " = ");
				translate(2, rd.pattern);
				myOut(";");
			}
		}
	}

	public void translate(int level, Pattern p) {
		if (p instanceof Pattern.Leaf) {
			Pattern.Leaf pl = (Pattern.Leaf) p;
			int typeIndex = register(pl.type);
			out.print("new Pattern.Leaf(type" + typeIndex + ")");
		} else if (p instanceof Pattern.Term) {
			Pattern.Term pt = (Pattern.Term) p;
			out.print("new Pattern.Term(\"" + pt.name + "\",");
			if (pt.data != null) {
				myOut();
				indent(level);
				translate(level + 1, pt.data);
				out.println(",");
				indent(level);
			} else {
				out.print("null,");
			}
			if (pt.variable != null) {
				out.print("\"" + pt.variable + "\")");
			} else {
				out.print("null)");
			}
		} else if (p instanceof Pattern.Collection) {
			Pattern.Collection pc = (Pattern.Collection) p;
			String kind;
			if (p instanceof Pattern.Set) {
				kind = "Set";
			} else if (p instanceof Pattern.Bag) {
				kind = "Bag";
			} else {
				kind = "List";
			}
			out.print("new Pattern." + kind + "(" + pc.unbounded
					+ ", new Pair[]{");
			for (int i = 0; i != pc.elements.length; ++i) {
				Pair<Pattern, String> e = pc.elements[i];
				Pattern ep = e.first();
				String es = e.second();
				if (i != 0) {
					out.println(", ");
				} else {
					out.println();
				}
				indent(level);
				out.print("new Pair(");
				translate(level + 1, ep);
				if (es == null) {
					out.print(",null)");
				} else {
					out.print(", \"" + es + "\")");
				}
			}
			out.print("})");
		}
	}

	public void writeSchema(SpecFile spec) {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Schema");
		myOut(1,
				"// =========================================================================");
		myOut();
		myOut(1,
				"public static final Schema SCHEMA = new Schema(new Schema.Term[]{");

		boolean firstTime = true;
		for (TermDecl td : extractDecls(TermDecl.class, spec)) {
			if (!firstTime) {
				myOut(",");
			}
			firstTime = false;
			myOut(2, "// " + td.type.toString());
			indent(2);
			writeSchema(td.type);
		}
		myOut();
		myOut(1, "});");
		myOut();
	}

	public void writeRuleArrays(SpecFile spec) {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// rules");
		myOut(1,
				"// =========================================================================");
		myOut();
		myOut(1,
				"public static final InferenceRule[] inferences = new InferenceRule[]{");

		int inferCounter = 0;
		int patternCounter = 0;
		List<Decl> declarations = getAllDeclarations(spec);
		for (Decl d : declarations) {
			if (d instanceof InferDecl) {
				if (inferCounter != 0) {
					out.println(",");
				}
				indent(2);
				out.print("new Inference_" + inferCounter + "(pattern" + patternCounter + ")");
				inferCounter++;
			}
			if(d instanceof RewriteDecl) {
				patternCounter++;
			}
		}

		myOut();
		myOut(1, "};");
		myOut(1,
				"public static final ReductionRule[] reductions = new ReductionRule[]{");

		int reduceCounter = 0;
		patternCounter = 0;
		for (Decl d : declarations) {
			if (d instanceof ReduceDecl) {
				if (reduceCounter != 0) {
					out.println(",");
				}
				indent(2);
				out.print("new Reduction_" + reduceCounter + "(pattern" + patternCounter + ")");
				reduceCounter++;
			}
			if(d instanceof RewriteDecl) {
				patternCounter++;
			}
		}
		myOut();
		myOut(1, "};");
		myOut();
	}

	protected void writeTypeTests() throws IOException {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Types");
		myOut(1,
				"// =========================================================================");
		myOut();

		for (int i = 0; i != typeRegister.size(); ++i) {
			Type t = typeRegister.get(i);
			JavaIdentifierOutputStream jout = new JavaIdentifierOutputStream();
			BinaryOutputStream bout = new BinaryOutputStream(jout);
			bout.write(t.toBytes());
			bout.flush();
			bout.close();
			// FIXME: strip out nominal types (and any other unneeded types).
			myOut(1, "// " + t);
			myOut(1, "private static Type type" + i + " = Runtime.Type(\""
					+ jout.toString() + "\");");
		}

		myOut();
	}

	private void writeSchema(Type.Term tt) {
		Automaton automaton = tt.automaton();
		BitSet visited = new BitSet(automaton.nStates());
		writeSchema(automaton.getRoot(0), automaton, visited);
	}

	private void writeSchema(int node, Automaton automaton, BitSet visited) {
		if (node < 0) {
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
			Automaton.List list = (Automaton.List) automaton
					.get(state.contents);
			writeSchema(list.get(1), automaton, visited);
			break;
		}
		case wyrl.core.Types.K_Or: {
			out.print("Schema.Or(");
			Automaton.Set set = (Automaton.Set) automaton.get(state.contents);
			for (int i = 0; i != set.size(); ++i) {
				if (i != 0) {
					out.print(", ");
				}
				writeSchema(set.get(i), automaton, visited);
			}
			out.print(")");
			break;
		}
		case wyrl.core.Types.K_Set: {
			out.print("Schema.Set(");
			Automaton.List list = (Automaton.List) automaton
					.get(state.contents);
			// FIXME: need to deref unbounded bool here as well
			out.print("true");
			Automaton.Bag set = (Automaton.Bag) automaton.get(list.get(1));
			for (int i = 0; i != set.size(); ++i) {
				out.print(",");
				writeSchema(set.get(i), automaton, visited);
			}
			out.print(")");
			break;
		}
		case wyrl.core.Types.K_Bag: {
			out.print("Schema.Bag(");
			Automaton.List list = (Automaton.List) automaton
					.get(state.contents);
			// FIXME: need to deref unbounded bool here as well
			out.print("true");
			Automaton.Bag bag = (Automaton.Bag) automaton.get(list.get(1));
			for (int i = 0; i != bag.size(); ++i) {
				out.print(",");
				writeSchema(bag.get(i), automaton, visited);
			}
			out.print(")");
			break;
		}
		case wyrl.core.Types.K_List: {
			out.print("Schema.List(");
			Automaton.List list = (Automaton.List) automaton
					.get(state.contents);
			// FIXME: need to deref unbounded bool here as well
			out.print("true");
			Automaton.List list2 = (Automaton.List) automaton.get(list.get(1));
			for (int i = 0; i != list2.size(); ++i) {
				out.print(",");
				writeSchema(list2.get(i), automaton, visited);
			}
			out.print(")");
			break;
		}
		case wyrl.core.Types.K_Term: {
			out.print("Schema.Term(");
			Automaton.List list = (Automaton.List) automaton
					.get(state.contents);
			Automaton.Strung str = (Automaton.Strung) automaton
					.get(list.get(0));
			out.print("\"" + str.value + "\"");
			if (list.size() > 1) {
				out.print(",");
				writeSchema(list.get(1), automaton, visited);
			}
			out.print(")");
			break;
		}
		default:
			throw new RuntimeException("Unknown kind encountered: "
					+ state.kind);
		}
	}

	private <T extends Decl> ArrayList<T> extractDecls(Class<T> kind,
			SpecFile spec) {
		ArrayList r = new ArrayList();
		extractDecls(kind, spec, r);
		return r;
	}

	private <T extends Decl> void extractDecls(Class<T> kind, SpecFile spec,
			ArrayList<T> decls) {
		for (Decl d : spec.declarations) {
			if (kind.isInstance(d)) {
				decls.add((T) d);
			} else if (d instanceof IncludeDecl) {
				IncludeDecl id = (IncludeDecl) d;
				extractDecls(kind, id.file, decls);
			}
		}
	}

	public int translate(int level, Expr code, Environment environment,
			SpecFile file) {
		if (code instanceof Expr.Constant) {
			return translate(level, (Expr.Constant) code, environment, file);
		} else if (code instanceof Expr.UnOp) {
			return translate(level, (Expr.UnOp) code, environment, file);
		} else if (code instanceof Expr.BinOp) {
			return translate(level, (Expr.BinOp) code, environment, file);
		} else if (code instanceof Expr.NaryOp) {
			return translate(level, (Expr.NaryOp) code, environment, file);
		} else if (code instanceof Expr.Constructor) {
			return translate(level, (Expr.Constructor) code, environment, file);
		} else if (code instanceof Expr.ListAccess) {
			return translate(level, (Expr.ListAccess) code, environment, file);
		} else if (code instanceof Expr.ListUpdate) {
			return translate(level, (Expr.ListUpdate) code, environment, file);
		} else if (code instanceof Expr.Variable) {
			return translate(level, (Expr.Variable) code, environment, file);
		} else if (code instanceof Expr.Substitute) {
			return translate(level, (Expr.Substitute) code, environment, file);
		} else if (code instanceof Expr.Comprehension) {
			return translate(level, (Expr.Comprehension) code, environment,
					file);
		} else if (code instanceof Expr.TermAccess) {
			return translate(level, (Expr.TermAccess) code, environment, file);
		} else if (code instanceof Expr.Cast) {
			return translate(level, (Expr.Cast) code, environment, file);
		} else {
			throw new RuntimeException("unknown expression encountered - "
					+ code);
		}
	}

	public int translate(int level, Expr.Cast code, Environment environment,
			SpecFile file) {
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

	public int translate(int level, Expr.Constant code,
			Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		Object v = code.value;
		String rhs;

		if (v instanceof Boolean) {
			rhs = v.toString();
		} else if (v instanceof BigInteger) {
			BigInteger bi = (BigInteger) v;
			if (bi.bitLength() <= 64) {
				rhs = "new Automaton.Int(" + bi.longValue() + ")";
			} else {
				rhs = "new Automaton.Int(\"" + bi.toString() + "\")";
			}

		} else if (v instanceof BigRational) {
			BigRational br = (BigRational) v;
			rhs = "new Automaton.Real(\"" + br.toString() + "\")";
			if (br.isInteger()) {
				long lv = br.longValue();
				if (BigRational.valueOf(lv).equals(br)) {
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
		myOut(level,
				comment(type2JavaType(type) + " r" + target + " = " + rhs + ";",
						code.toString()));
		return target;
	}

	public int translate(int level, Expr.UnOp code, Environment environment,
			SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		int rhs = translate(level, code.mhs, environment, file);
		rhs = coerceFromRef(level, code.mhs, rhs, environment);
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
		myOut(level,
				comment(type2JavaType(type) + " r" + target + " = " + body
						+ ";", code.toString()));
		return target;
	}

	public int translate(int level, Expr.BinOp code, Environment environment,
			SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		Type lhs_t = code.lhs.attribute(Attribute.Type.class).type;
		Type rhs_t = code.rhs.attribute(Attribute.Type.class).type;
		int lhs = translate(level, code.lhs, environment, file);

		String body;

		if (code.op == Expr.BOp.IS && code.rhs instanceof Expr.Constant) {
			// special case for runtime type tests
			Expr.Constant c = (Expr.Constant) code.rhs;
			Type test = (Type) c.value;
			int typeIndex = register(test);
			body = "Runtime.accepts(type" + typeIndex + ", automaton, r" + lhs
					+ ", SCHEMA)";
		} else if (code.op == Expr.BOp.AND) {
			// special case to ensure short-circuiting of AND.
			lhs = coerceFromRef(level, code.lhs, lhs, environment);
			int target = environment.allocate(type);
			myOut(level,
					comment(type2JavaType(type) + " r" + target + " = " + false
							+ ";", code.toString()));
			myOut(level++, "if(r" + lhs + ") {");
			int rhs = translate(level, code.rhs, environment, file);
			rhs = coerceFromRef(level, code.rhs, rhs, environment);
			myOut(level, "r" + target + " = r" + rhs + ";");
			myOut(--level, "}");
			return target;
		} else {
			int rhs = translate(level, code.rhs, environment, file);
			// First, convert operands into values (where appropriate)
			switch (code.op) {
			case EQ:
			case NEQ:
				if (lhs_t instanceof Type.Ref && rhs_t instanceof Type.Ref) {
					// OK to do nothing here...
				} else {
					lhs = coerceFromRef(level, code.lhs, lhs, environment);
					rhs = coerceFromRef(level, code.rhs, rhs, environment);
				}
				break;
			case APPEND:
				// append is a tricky case as we have support the non-symmetic
				// cases
				// for adding a single element to the end or the beginning of a
				// list.
				lhs_t = Type.unbox(lhs_t);
				rhs_t = Type.unbox(rhs_t);

				if (lhs_t instanceof Type.Collection) {
					lhs = coerceFromRef(level, code.lhs, lhs, environment);
				} else {
					lhs = coerceFromValue(level, code.lhs, lhs, environment);
				}
				if (rhs_t instanceof Type.Collection) {
					rhs = coerceFromRef(level, code.rhs, rhs, environment);
				} else {
					rhs = coerceFromValue(level, code.rhs, rhs, environment);
				}
				break;
			case IN:
				lhs = coerceFromValue(level, code.lhs, lhs, environment);
				rhs = coerceFromRef(level, code.rhs, rhs, environment);
				break;
			default:
				lhs = coerceFromRef(level, code.lhs, lhs, environment);
				rhs = coerceFromRef(level, code.rhs, rhs, environment);
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
				body = "r" + lhs + " || r" + rhs;
				break;
			case EQ:
				if (lhs_t instanceof Type.Ref && rhs_t instanceof Type.Ref) {
					body = "r" + lhs + " == r" + rhs;
				} else {
					body = "r" + lhs + ".equals(r" + rhs + ")";
				}
				break;
			case NEQ:
				if (lhs_t instanceof Type.Ref && rhs_t instanceof Type.Ref) {
					body = "r" + lhs + " != r" + rhs;
				} else {
					body = "!r" + lhs + ".equals(r" + rhs + ")";
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
				throw new RuntimeException(
						"unknown binary operator encountered: " + code);
			}
		}
		int target = environment.allocate(type);
		myOut(level,
				comment(type2JavaType(type) + " r" + target + " = " + body
						+ ";", code.toString()));
		return target;
	}

	public int translate(int level, Expr.NaryOp code, Environment environment,
			SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		String body = "new Automaton.";

		if (code.op == Expr.NOp.LISTGEN) {
			body += "List(";
		} else if (code.op == Expr.NOp.BAGGEN) {
			body += "Bag(";
		} else {
			body += "Set(";
		}

		List<Expr> arguments = code.arguments;
		for (int i = 0; i != arguments.size(); ++i) {
			if (i != 0) {
				body += ", ";
			}
			Expr argument = arguments.get(i);
			int reg = translate(level, argument, environment, file);
			reg = coerceFromValue(level, argument, reg, environment);
			body += "r" + reg;
		}

		int target = environment.allocate(type);
		myOut(level,
				comment(type2JavaType(type) + " r" + target + " = " + body
						+ ");", code.toString()));
		return target;
	}

	public int translate(int level, Expr.ListAccess code,
			Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		int src = translate(level, code.src, environment, file);
		int idx = translate(level, code.index, environment, file);
		src = coerceFromRef(level, code.src, src, environment);
		idx = coerceFromRef(level, code.index, idx, environment);

		String body = "r" + src + ".indexOf(r" + idx + ")";

		int target = environment.allocate(type);
		myOut(level,
				comment(type2JavaType(type) + " r" + target + " = " + body
						+ ";", code.toString()));
		return target;
	}

	public int translate(int level, Expr.ListUpdate code,
			Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;
		int src = translate(level, code.src, environment, file);
		int idx = translate(level, code.index, environment, file);
		int value = translate(level, code.value, environment, file);

		src = coerceFromRef(level, code.src, src, environment);
		idx = coerceFromRef(level, code.index, idx, environment);
		value = coerceFromValue(level, code.value, value, environment);

		String body = "r" + src + ".update(r" + idx + ", r" + value + ")";

		int target = environment.allocate(type);
		myOut(level,
				comment(type2JavaType(type) + " r" + target + " = " + body
						+ ";", code.toString()));
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
			if (code.external) {
				body = file.name + "$native." + code.name + "(automaton, r"
						+ arg + ")";
			} else {
				arg = coerceFromValue(level, code.argument, arg, environment);
				body = "new Automaton.Term(K_" + code.name + ", r" + arg + ")";
			}
		}

		int target = environment.allocate(type);
		myOut(level, type2JavaType(type) + " r" + target + " = " + body + ";");
		return target;
	}

	public int translate(int level, Expr.Variable code,
			Environment environment, SpecFile file) {
		Integer operand = environment.get(code.var);
		if (operand != null) {
			return operand;
		} else {
			Type type = code.attribute(Attribute.Type.class).type;
			int target = environment.allocate(type);
			myOut(level, type2JavaType(type) + " r" + target + " = " + code.var
					+ ";");
			return target;
		}
	}

	public int translate(int level, Expr.Substitute code,
			Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;

		// first, translate all subexpressions and make sure they are
		// references.
		int src = translate(level, code.src, environment, file);
		src = coerceFromValue(level, code.src, src, environment);

		int original = translate(level, code.original, environment, file);
		original = coerceFromValue(level, code.original, original, environment);

		int replacement = translate(level, code.replacement, environment, file);
		replacement = coerceFromValue(level, code.replacement, replacement,
				environment);

		// second, put in place the substitution
		String body = "automaton.substitute(r" + src + ", r" + original + ", r"
				+ replacement + ")";
		int target = environment.allocate(type);
		myOut(level, type2JavaType(type) + " r" + target + " = " + body + ";");
		return target;
	}

	public int translate(int level, Expr.TermAccess code,
			Environment environment, SpecFile file) {
		Type type = code.attribute(Attribute.Type.class).type;

		// first translate src expression, and coerce to a value
		int src = translate(level, code.src, environment, file);
		src = coerceFromRef(level, code.src, src, environment);

		String body = "r" + src + ".contents";

		int target = environment.allocate(type);
		myOut(level, type2JavaType(type) + " r" + target + " = " + body + ";");
		return target;
	}

	public int translate(int level, Expr.Comprehension expr,
			Environment environment, SpecFile file) {
		Type type = expr.attribute(Attribute.Type.class).type;
		int target = environment.allocate(type);

		// first, translate all source expressions
		int[] sources = new int[expr.sources.size()];
		for (int i = 0; i != sources.length; ++i) {
			Pair<Expr.Variable, Expr> p = expr.sources.get(i);
			int operand = translate(level, p.second(), environment, file);
			operand = coerceFromRef(level, p.second(), operand, environment);
			sources[i] = operand;
		}

		// TODO: initialise result set
		myOut(level, "Automaton.List t" + target + " = new Automaton.List();");
		int startLevel = level;

		// initialise result register if needed
		switch (expr.cop) {
		case NONE:
			myOut(level, type2JavaType(type) + " r" + target + " = true;");
			myOut(level, "outer:");
			break;
		case SOME:
			myOut(level, type2JavaType(type) + " r" + target + " = false;");
			myOut(level, "outer:");
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
			String rhs = "r" + sources[i] + ".get(i" + index + ")";
			// FIXME: need a more general test for a reference type
			if (!(elementType instanceof Type.Ref)) {
				rhs = "automaton.get(" + rhs + ");";
			}
			myOut(level, type2JavaType(elementType) + " r" + index + " = ("
					+ type2JavaType(elementType) + ") " + rhs + ";");
		}

		if (expr.condition != null) {
			int condition = translate(level, expr.condition, environment, file);
			myOut(level++, "if(r" + condition + ") {");
		}

		switch (expr.cop) {
		case SETCOMP:
		case BAGCOMP:
		case LISTCOMP:
			int result = translate(level, expr.value, environment, file);
			result = coerceFromValue(level, expr.value, result, environment);
			myOut(level, "t" + target + ".add(r" + result + ");");
			break;
		case NONE:
			myOut(level, "r" + target + " = false;");
			myOut(level, "break outer;");
			break;
		case SOME:
			myOut(level, "r" + target + " = true;");
			myOut(level, "break outer;");
			break;
		}
		// finally, terminate all the for loops
		while (level > startLevel) {
			myOut(--level, "}");
		}

		switch (expr.cop) {
		case SETCOMP:
			myOut(level, type2JavaType(type) + " r" + target
					+ " = new Automaton.Set(t" + target + ".toArray());");
			break;
		case BAGCOMP:
			myOut(level, type2JavaType(type) + " r" + target
					+ " = new Automaton.Bag(t" + target + ".toArray());");
			break;
		case LISTCOMP:
			myOut(level, type2JavaType(type) + " r" + target + " = t" + target
					+ ";");
			break;
		}

		return target;
	}

	protected void writeMainMethod() {
		myOut();
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
		myOut(3, "IterativeRewriter.Strategy<InferenceRule> inferenceStrategy = new SimpleRewriteStrategy<InferenceRule>(automaton, inferences);");
		myOut(3, "IterativeRewriter.Strategy<ReductionRule> reductionStrategy = new SimpleRewriteStrategy<ReductionRule>(automaton, reductions);");
		myOut(3, "IterativeRewriter rw = new IterativeRewriter(automaton,inferenceStrategy, reductionStrategy, SCHEMA);");
		myOut(3, "rw.apply();");
		myOut(3, "System.out.print(\"REWROTE: \");");
		myOut(3, "print(automaton);");
		myOut(3, "System.out.println(\"\\n\\n=> (\" + rw.getStats() + \")\\n\");");
		myOut(2, "} catch(PrettyAutomataReader.SyntaxError ex) {");
		myOut(3, "System.err.println(ex.getMessage());");
		myOut(2, "}");
		myOut(1, "}");

		myOut(1, "");
		myOut(1, "static void print(Automaton automaton) {");
		myOut(2, "try {");
		myOut(3,
				"PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,SCHEMA);");
		myOut(3, "writer.write(automaton);");
		myOut(3, "writer.flush();");
		myOut(3, "System.out.println();");
		myOut(2,
				"} catch(IOException e) { System.err.println(\"I/O error printing automaton\"); }");
		myOut(1, "}");
	}

	public String comment(String code, String comment) {
		int nspaces = 30 - code.length();
		String r = "";
		for (int i = 0; i < nspaces; ++i) {
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
		return type2JavaType(type, true);
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
			if (primitives) {
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

	public int coerceFromValue(int level, Expr expr, int register,
			Environment environment) {
		Type type = expr.attribute(Attribute.Type.class).type;
		if (type instanceof Type.Ref) {
			return register;
		} else {
			Type.Ref refType = Type.T_REF(type);
			int result = environment.allocate(refType);
			String src = "r" + register;
			if (refType.element() instanceof Type.Bool) {
				// special thing needed for bools
				src = src + " ? Automaton.TRUE : Automaton.FALSE";
			}
			myOut(level, type2JavaType(refType) + " r" + result
					+ " = automaton.add(" + src + ");");
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
			if (element instanceof Type.Bool) {
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

	protected Type stripNominalsAndRefs(Type t) {
		if (t instanceof Type.Nominal) {
			Type.Nominal n = (Type.Nominal) t;
			return stripNominalsAndRefs(n.element());
		} else if (t instanceof Type.Ref) {
			Type.Ref n = (Type.Ref) t;
			return stripNominalsAndRefs(n.element());
		} else {
			return t;
		}
	}

	public List<Decl> getAllDeclarations(SpecFile spec) {
		ArrayList<Decl> declarations = new ArrayList<Decl>();
		getAllDeclarations(spec,declarations);
		return declarations;
	}
	public void getAllDeclarations(SpecFile spec, ArrayList<Decl> decls) {
		for (Decl d : spec.declarations) {
			if(d instanceof IncludeDecl) {
				IncludeDecl id = (IncludeDecl) d;
				getAllDeclarations(id.file,decls);
			} else {
				decls.add(d);
			}
		}
	}

	/**
	 * Check whether all variables used in a given expression are defined or
	 * not.
	 *
	 * @param e
	 * @param enviroment
	 * @return
	 */
	public boolean allVariablesDefined(Expr e, Environment environment) {
		HashSet<String> uses = Exprs.uses(e);
		for (String s : uses) {
			if (environment.get(s) == null) {
				return false;
			}
		}
		return true;
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

	private HashMap<Type, Integer> registeredTypes = new HashMap<Type, Integer>();
	private ArrayList<Type> typeRegister = new ArrayList<Type>();

	private int register(Type t) {
		// t.automaton().minimise();
		// t.automaton().canonicalise();
		// Types.reduce(t.automaton());
		Integer i = registeredTypes.get(t);
		if (i == null) {
			int r = typeRegister.size();
			registeredTypes.put(t, r);
			typeRegister.add(t);
			return r;
		} else {
			return i;
		}
	}

	private static final class Environment {
		private final HashMap<String, Integer> var2idx;
		private final ArrayList<Pair<Type, String>> idx2var;

		public Environment() {
			this.var2idx = new HashMap<String, Integer>();
			this.idx2var = new ArrayList<Pair<Type, String>>();
		}

		private Environment(HashMap<String, Integer> var2idx,
				ArrayList<Pair<Type, String>> idx2var) {
			this.var2idx = var2idx;
			this.idx2var = idx2var;
		}

		public int size() {
			return idx2var.size();
		}

		public int allocate(Type t) {
			int idx = idx2var.size();
			idx2var.add(new Pair<Type, String>(t, null));
			return idx;
		}

		public int allocate(Type t, String v) {
			int idx = idx2var.size();
			idx2var.add(new Pair<Type, String>(t, v));
			var2idx.put(v, idx);
			return idx;
		}

		public Integer get(String v) {
			return var2idx.get(v);
		}

		public Pair<Type, String> get(int idx) {
			return idx2var.get(idx);
		}

		public void put(int idx, String v) {
			var2idx.put(v, idx);
			idx2var.set(idx,
					new Pair<Type, String>(idx2var.get(idx).first(), v));
		}

		public Environment clone() {
			return new Environment((HashMap) var2idx.clone(),
					(ArrayList) idx2var.clone());
		}

		public String toString() {
			return var2idx.toString();
		}
	}
}
