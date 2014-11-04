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

package wyautl.core;

/**
 * Provides a simple mechanism for validating that a given automaton is well-formed.
 *
 * @author David J. Pearce
 *
 */
public class Schema {
	private final Term[] states;

	public Schema(Term[] states) {
		this.states = states;
	}

	public int size() {
		return states.length;
	}

	public Term get(int i) {
		return states[i];
	}

	public boolean validate(Automaton automaton) {
		// at some point, it would be nice to implement this!
		return false;
	}

	public static final Any Any = new Any();

	public static final Void Void = new Void();

	public static final Bool Bool = new Bool();

	public static final Int Int = new Int();

	public static final Real Real = new Real();

	public static final Strung String = new Strung();

	public static Term Term(String name) {
		return new Term(name,null);
	}

	public static Term Term(String name, State contents) {
		return new Term(name,contents);
	}

	public static Not Not(State states) {
		return new Not(states);
	}

	public static Or Or(State... states) {
		return new Or(states);
	}

	public static Set Set(boolean unbounded, State... states) {
		return new Set(unbounded,states);
	}

	public static Bag Bag(boolean unbounded, State... states) {
		return new Bag(unbounded,states);
	}

	public static List List(boolean unbounded, State... states) {
		return new List(unbounded,states);
	}

	public abstract static class State {

	}

	public abstract static class Constant extends State {

	}

	public static class Any extends Constant {
		private Any() {}
	}

	public static class Void extends Constant {
		private Void() {}
	}

	public static class Bool extends Constant {
		private Bool() {}
	}

	public static class Int extends Constant {
		private Int() {}
	}

	public static class Real extends Constant {
		private Real() {}
	}

	public static class Strung extends Constant {
		private Strung() {}
	}

	public static class Term extends State {
		public final String name;
		public final State child;

		private Term(String name, State contents) {
			this.name = name;
			this.child = contents;
		}
	}

	public static class Not {
		public final State child;

		private Not(State child) {
			this.child = child;
		}
	}

	public abstract static class Compound extends State {
		public final State[] children;

		private Compound(State... children) {
			this.children = children;
		}
	}

	public static class Or extends Compound {
		private Or(State... states) {
			super(states);
		}
	}

	public abstract static class Collection extends Compound {
		public final boolean unbounded;

		private Collection(boolean unbounded, State... states) {
			super(states);
			this.unbounded = unbounded;
		}
	}

	public static class Set extends Collection {
		private Set(boolean unbounded, State... states) {
			super(unbounded,states);
		}
	}

	public static class Bag extends Collection {
		private Bag(boolean unbounded, State... states) {
			super(unbounded,states);
		}
	}

	public static class List extends Collection {
		private List(boolean unbounded, State... states) {
			super(unbounded,states);
		}
	}
}
