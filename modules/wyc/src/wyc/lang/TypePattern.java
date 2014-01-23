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
import java.util.List;

import wybs.lang.Attribute;
import wybs.lang.SyntacticElement;

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
	
	/**
	 * A type pattern leaf is simply a syntactic type, along with an optional
	 * variable identifier.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Leaf extends TypePattern {
		public final SyntacticType type;
		public final String var;
		
		public Leaf(SyntacticType type, String var, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.var = var;
		}
		
		public Leaf(SyntacticType type, String var, List<Attribute> attributes) {
			super(attributes);
			this.type = type;
			this.var = var;
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
	}
}
