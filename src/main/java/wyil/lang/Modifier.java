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

package wyil.lang;

/**
 * <p>
 * Represents a protection modifier on a module item. For example, all
 * declarations (e.g. functions, types, etc) can be marked as
 * <code>public</code> or <code>private</code>.
 * </p>
 * <p>
 * The modifiers <code>native</code> and <code>export</code> are used to enable
 * inter-operation with other languages. By declaring a function or method as
 * <code>native</code> you are signaling that its implementation is provided
 * elsewhere (e.g. it's implemented in Java code directly). By marking a
 * function or method with <code>export</code>, you are declaring that external
 * code may call it. For example, you have some Java code that needs to call it.
 * The modifier is required because, by default, all the names of all methods
 * and functions are <i>mangled</i> to include type information and enable
 * overloading. Therefore, a method/function marked with <code>export</code>
 * will generate a function without name mangling.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface Modifier {
	public static final Modifier PUBLIC = new Public();
	public static final Modifier PRIVATE = new Private();
	public static final Modifier NATIVE = new Native();
	public static final Modifier EXPORT = new Export();

	public static final class Public implements Modifier {
		private Public() {}
		public String toString() { return "public"; }
	}

	public static final class Private implements Modifier {
		private Private() {}
		public String toString() { return "private"; }
	}

	public static final class Native implements Modifier {
		private Native() {}
		public String toString() { return "native"; }
	}

	public static final class Export implements Modifier {
		private Export() {}
		public String toString() { return "export"; }
	}
}
