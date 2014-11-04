// Copyright (c) 2014, David J. Pearce (djp@ecs.vuw.ac.nz)
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

package wyc.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;

/**
 * <p>
 * A type pattern represents the destructuring of a type into variables
 * representing its subcomponents. For example, <code>(int x, int y)</code> is a
 * tuple pattern where the variables <code>x</code> and <code>y</code> identify
 * the two subcomponents.
 * </p>
 *
 * Type patterns are used (amongst other things) for type declarations, and the
 * parameter and return for function/method declarations. For example:
 *
 * <pre>
 * type nat is (int x) where x >= 0
 * </pre>
 *
 * This illustrates a type declaration which uses a type pattern to declare the
 * variable <code>x</code>, such that it can be subsequently used in the types
 * invariant.
 *
 * @author David J. Pearce
 *
 */
public abstract class TypePattern extends SyntacticElement.Impl {

	private TypePattern(Attribute... attributes) {
		super(attributes);
	}

	private TypePattern(List<Attribute> attributes) {
		super(attributes);
	}

	public abstract SyntacticType toSyntacticType();

	public abstract void addDeclaredVariables(Collection<String> variables);

	/**
	 * A type pattern leaf is simply a syntactic type, along with an optional
	 * variable identifier.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Leaf extends TypePattern {
		public final SyntacticType type;
		public final Expr.LocalVariable var;

		public Leaf(SyntacticType type, Expr.LocalVariable var, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.var = var;
		}

		public Leaf(SyntacticType type, Expr.LocalVariable var, List<Attribute> attributes) {
			super(attributes);
			this.type = type;
			this.var = var;
		}

		public SyntacticType toSyntacticType() {
			return type;
		}

		public void addDeclaredVariables(Collection<String> variables) {
			if(var != null) {
				variables.add(var.var);
			}
		}
	}

	/**
	 * A rational type pattern is simply a sequence of two type patterns
	 * seperated by '/' separated by commas.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Rational extends TypePattern {
		public final TypePattern numerator;
		public final TypePattern denominator;

		public Rational(TypePattern numerator, TypePattern denominator,
				Attribute... attributes) {
			super(attributes);
			this.numerator = numerator;
			this.denominator = denominator;
		}

		public Rational(TypePattern numerator, TypePattern denominator,
				List<Attribute> attributes) {
			super(attributes);
			this.numerator = numerator;
			this.denominator = denominator;
		}

		public SyntacticType.Real toSyntacticType() {
			return new SyntacticType.Real(attributes());
		}

		public void addDeclaredVariables(Collection<String> variables) {
			numerator.addDeclaredVariables(variables);
			denominator.addDeclaredVariables(variables);
		}
	}

	/**
	 * A type pattern tuple is simply a sequence of two or type patterns
	 * separated by commas.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Tuple extends TypePattern {
		public final List<TypePattern> elements;

		public Tuple(List<TypePattern> elements,
				Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public Tuple(List<TypePattern> elements,
				List<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public SyntacticType.Tuple toSyntacticType() {
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			for (int i = 0; i != elements.size(); ++i) {
				types.add(elements.get(i).toSyntacticType());
			}
			return new SyntacticType.Tuple(types, attributes());
		}

		public void addDeclaredVariables(Collection<String> variables) {
			for(TypePattern p : elements) {
				p.addDeclaredVariables(variables);
			}
		}
	}

	/**
	 * A record type pattern is simply a sequence of two or type patterns
	 * separated by commas enclosed in curly braces.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Record extends TypePattern {
		public final List<TypePattern.Leaf> elements;
		public final boolean isOpen;

		public Record(SyntacticType.Record record, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern.Leaf>();
			this.isOpen = record.isOpen;
			for (Map.Entry<String, SyntacticType> e : record.types.entrySet()) {
				String field = e.getKey();
				// FIXME: missing source attribute information on local variable
				elements.add(new TypePattern.Leaf(e.getValue(),
						new Expr.LocalVariable(field), e.getValue()
								.attributes()));
			}
		}

		public Record(List<TypePattern.Leaf> elements, boolean isOpen,
				Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern.Leaf>(elements);
			this.isOpen = isOpen;
		}

		public Record(List<TypePattern.Leaf> elements, boolean isOpen,
				List<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern.Leaf>(elements);
			this.isOpen = isOpen;
		}

		public SyntacticType.Record toSyntacticType() {
			HashMap<String, SyntacticType> types = new HashMap<String, SyntacticType>();
			for (int i = 0; i != elements.size(); ++i) {
				TypePattern.Leaf tp = elements.get(i);
				types.put(tp.var.var, tp.toSyntacticType());
			}
			return new SyntacticType.Record(isOpen, types, attributes());
		}

		public void addDeclaredVariables(Collection<String> variables) {
			for(TypePattern p : elements) {
				p.addDeclaredVariables(variables);
			}
		}
	}

	/**
	 * A union type pattern is a sequence of type patterns separated by a
	 * vertical bar ('|').
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Union extends TypePattern {
		public final List<TypePattern> elements;

		public Union(List<TypePattern> elements, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public Union(List<TypePattern> elements, List<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public SyntacticType.Union toSyntacticType() {
			ArrayList<SyntacticType.NonUnion> types = new ArrayList<SyntacticType.NonUnion>();
			for (int i = 0; i != elements.size(); ++i) {
				TypePattern tp = (TypePattern) elements.get(i);
				types.add((SyntacticType.NonUnion) tp.toSyntacticType());
			}
			return new SyntacticType.Union(types, attributes());
		}

		public void addDeclaredVariables(Collection<String> variables) {
			// TODO: at some point, we can extend this further to look at the
			// elements type we have and try to extract common variables.
		}
	}

	/**
	 * An intersection type pattern is a sequence of type patterns separated by a
	 * vertical bar ('&').
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Intersection extends TypePattern {
		public final List<TypePattern> elements;


		public Intersection(List<TypePattern> elements,
				Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public Intersection(List<TypePattern> elements,
				List<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public SyntacticType.Intersection toSyntacticType() {
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			for (int i = 0; i != elements.size(); ++i) {
				TypePattern tp = (TypePattern) elements.get(i);
				types.add(tp.toSyntacticType());
			}
			return new SyntacticType.Intersection(types, attributes());
		}

		public void addDeclaredVariables(Collection<String> variables) {
			// TODO: at some point, we can extend this further to look at the
			// elements type we have and try to extract common variables.
		}
	}
}
