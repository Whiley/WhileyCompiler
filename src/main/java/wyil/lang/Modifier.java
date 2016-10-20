// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

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
		@Override
		public String toString() { return "public"; }
	}

	public static final class Private implements Modifier {
		private Private() {}
		@Override
		public String toString() { return "private"; }
	}

	public static final class Native implements Modifier {
		private Native() {}
		@Override
		public String toString() { return "native"; }
	}

	public static final class Export implements Modifier {
		private Export() {}
		@Override
		public String toString() { return "export"; }
	}
}
