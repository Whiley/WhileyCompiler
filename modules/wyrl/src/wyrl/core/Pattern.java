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

package wyrl.core;

import java.util.*;

import wyrl.core.*;
import wyrl.util.*;
import static wyrl.core.Type.Collection.*;
import static wyrl.util.SyntaxError.syntaxError;

public abstract class Pattern extends SyntacticElement.Impl {

	private Pattern(Attribute... attributes) {
		super(attributes);
	}

	/**
	 * Get a list of the variables declared in this pattern, along with their
	 * declared type.
	 *
	 * @return
	 */
	public abstract java.util.List<Pair<String,Type>> declarations();

	public abstract Type.Ref type();

	public static final class Leaf extends Pattern {
		public Type type;

		public Leaf(Type type, Attribute... attributes) {
			super(attributes);
			this.type = type;
		}

		public String toString() {
			return type.toString();
		}

		@Override
		public java.util.List<Pair<String,Type>> declarations() {
			return new ArrayList<Pair<String, Type>>();
		}

		@Override
		public Type.Ref type() {
			return Type.T_REF(type);
		}
	}

	public static final class Term extends Pattern {
		public String name;
		public Pattern data;
		public String variable;

		public Term(String name, Pattern data, String variable, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.data = data;
			this.variable = variable;
		}

		public String toString() {
			if(data != null) {
				if(variable != null) {
					return name + "(" + data + " " + variable + ")";
				} else {
					return name + "(" + data + ")";
				}
			} else {
				return name;
			}
		}

		public java.util.List<Pair<String, Type>> declarations() {
			java.util.List<Pair<String, Type>> decls;
			if (data != null) {
				decls = data.declarations();
			} else {
				decls = new ArrayList<Pair<String, Type>>();
			}
			if (variable != null) {
				decls.add(new Pair<String, Type>(variable, type()));
			}
			return decls;
		}

		@Override
		public Type.Ref type() {
			Type.Ref d = data != null ? data.type() : null;
			return Type.T_REF(Type.T_TERM(name, d));
		}
	}

	public static abstract class Collection extends Pattern {
		public final Pair<Pattern,String>[] elements;
		public final boolean unbounded;

		public Collection(boolean unbounded, Pair<Pattern,String>[] elements, Attribute... attributes) {
			super(attributes);
			this.elements = elements;
			this.unbounded = unbounded;
		}

		public Collection(boolean unbounded, java.util.List<Pair<Pattern,String>> elements, Attribute... attributes) {
			super(attributes);
			this.elements = elements.toArray(new Pair[elements.size()]);
			this.unbounded = unbounded;
		}

		public java.util.List<Pair<String, Type>> declarations() {
			ArrayList<Pair<String, Type>> decls = new ArrayList<Pair<String,Type>>();
			for(int i=0;i!=elements.length;++i) {
				Pair<Pattern,String> element = elements[i];
				Pattern pattern = element.first();
				String variable = element.second();
				// First, add all declarations from children of element
				decls.addAll(element.first().declarations());
				// Second, add element declaration (if exists)
				if (variable != null) {
					Type type = pattern.type();
					if (unbounded && (i + 1) == elements.length) {
						if (this instanceof Pattern.Set) {
							type = Type.T_SET(true, type);
						} else if (this instanceof Pattern.Bag) {
							type = Type.T_BAG(true, type);
						} else {
							type = Type.T_LIST(true, type);
						}
					}
					decls.add(new Pair<String, Type>(variable, type));
				}
			}
			return decls;
		}

		@Override
		public Type.Ref type() {
			Type[] types = new Type[elements.length];
			for(int i=0;i!=types.length;++i) {
				types[i] = elements[i].first().type();
			}
			if(this instanceof Pattern.Set) {
				return Type.T_REF(Type.T_SET(unbounded, types));
			} else if(this instanceof Pattern.Bag) {
				return Type.T_REF(Type.T_BAG(unbounded, types));
			} else {
				return Type.T_REF(Type.T_LIST(unbounded, types));
			}
		}
	}

	public final static class List extends Collection {
		public List(boolean unbounded, Pair<Pattern,String>[] elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}

		public List(boolean unbounded, java.util.List<Pair<Pattern,String>> elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}

		public String toString() {
			String r = "";
			for(int i=0;i!=elements.length;++i) {
				Pair<Pattern,String> element = elements[i];
				Pattern pattern = element.first();
				String variable = element.second();
				if(i!=0) {
					r += ", ";
				}
				r += pattern;
				if(variable != null) {
					r += " " + variable;
				}

			}
			if(unbounded) {
				r += "...";
			}
			return "[" + r + "]";
		}
	}

	public abstract static class BagOrSet extends Collection {
		public BagOrSet(boolean unbounded, Pair<Pattern,String>[] elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}

		public BagOrSet(boolean unbounded, java.util.List<Pair<Pattern,String>> elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}
	}

	public final static class Set extends BagOrSet {
		public Set(boolean unbounded, Pair<Pattern,String>[] elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}

		public Set(boolean unbounded, java.util.List<Pair<Pattern,String>> elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}

		public String toString() {
			String r = "";
			for(int i=0;i!=elements.length;++i) {
				Pair<Pattern,String> element = elements[i];
				Pattern pattern = element.first();
				String variable = element.second();
				if(i!=0) {
					r += ", ";
				}
				r += pattern;
				if(variable != null) {
					r += " " + variable;
				}
			}
			if(unbounded) {
				r += "...";
			}
			return "{" + r + "}";
		}
	}

	public final static class Bag extends BagOrSet {
		public Bag(boolean unbounded, Pair<Pattern,String>[] elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}

		public Bag(boolean unbounded, java.util.List<Pair<Pattern,String>> elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}

		public String toString() {
			String r = "";
			for(int i=0;i!=elements.length;++i) {
				Pair<Pattern,String> element = elements[i];
				Pattern pattern = element.first();
				String variable = element.second();
				if(i!=0) {
					r += ", ";
				}
				r += pattern;
				if(variable != null) {
					r += " " + variable;
				}
			}
			if(unbounded) {
				r += "...";
			}
			return "{|" + r + "|}";
		}
	}
}

