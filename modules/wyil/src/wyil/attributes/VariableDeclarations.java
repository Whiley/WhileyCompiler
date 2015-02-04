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

package wyil.attributes;

import java.util.List;

import wyil.attributes.VariableDeclarations.Declaration;
import wyil.lang.Attribute;
import wyil.lang.Type;

/**
 * <p>
 * Provides the declaration information for each register within a given WyIL
 * bytecode block. This includes declared <i>types</i>, <i>names</i> and
 * <i>constraints</i>.
 * </p>
 *
 * <p>
 * Declared type information is typically taken directly from the variable
 * declarations in the source language. However, in some cases, they may be
 * inferred from those declarations. For example, consider the following Whiley
 * code snippet:
 * </p>
 *
 * <pre>
 * function f(int x, int y) => int:
 *    x = x + (y * 2)
 *    return x
 * </pre>
 *
 * This might generate the following WyIL bytecode:
 *
 * <pre>
 * const %2 = 2
 * mul %3 = %1, %2
 * add %0 = %0, %3
 * return %0
 * </pre>
 *
 * <p>
 * Here, the local variables <code>%0</code> and <code>%1</code> will have
 * declared types in the originating source file. However, the registers
 * <code>%2</code> and <code>%3</code> will not. Nevertheless, their types can
 * be inferred from the source expressions which gave rise to them.
 * </p>
 *
 * <p>
 * The declared information associated with each variable also include an optional
 * constraint. As before, such information is
 * either taken directly from the originating source code, or inferred from it
 * in some way. In this case, however, inferring such information is
 * challenging. For example, a simple approach for integer variables might be to
 * employ an <i>integer range analysis</i> to constraint the possible bounds of
 * any integer variables.
 * </p>
 * <p>
 * Finally, each declaration can include an optional <i>variable name</i>. The
 * intention here is to retain useful identifying information from the original
 * source file, to help with debugging and/or error reporting.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class VariableDeclarations implements Attribute {
	private Declaration[] declarations;

	public VariableDeclarations(Declaration... declarations) {
		this.declarations = declarations;
	}

	public VariableDeclarations(List<Declaration> declarations) {
		this.declarations = declarations.toArray(new Declaration[declarations.size()]);
	}

	public Declaration get(int register) {
		return declarations[register];
	}

	public void set(int register, Declaration declaration) {
		declarations[register] = declaration;
	}

	public int size() {
		return declarations.length;
	}
	
	/**
	 * Represents the declaration information associated with a given register.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Declaration {
		private final Type type;
		// private final AttributableBlock constraint ?
		private final String name;

		public Declaration(Type type, String name) {
			this.type = type;
			this.name = name;
		}

		public Type type() {
			return type;
		}

		public String name() {
			return name;
		}
		
		public String toString() {
			return type + " " + name;
		}
	}
}
