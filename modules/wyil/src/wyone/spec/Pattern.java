// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.spec;

import java.util.*;

import wyone.core.*;
import wyone.util.*;
import static wyone.core.Type.Compound.*;
import static wyone.util.SyntaxError.syntaxError;

public abstract class Pattern extends SyntacticElement.Impl {
	
	private Pattern(Attribute... attributes) {
		super(attributes);
	}
	
	/**
	 * Translate the declared type in a pattern construct into an actual type.
	 * Essentially, this corresponds to discarding all of the parameter(s) used
	 * for matching subcomponents of the type.
	 * 
	 * @return
	 */
	public abstract Type.Ref type();
	
	
	public static final class Leaf extends Pattern {
		public final Type type;
		
		public Leaf(Type type, Attribute... attributes) {
			super(attributes);
			this.type = type;
		}
		
		public Type.Ref type() {
			return Type.T_REF(type);
		}
		
		public String toString() {
			return type.toString();
		}		
	}
	
	public static final class Term extends Pattern {		
		public final String name;
		public final Pattern data;
		public final String variable;
		
		public Term(String name, Pattern data, String variable, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.data = data;					
			this.variable = variable;
		}
		
		public Type.Ref type() {
			Type.Ref d = null;
			if(data != null) {
				d = data.type();
			}
			return Type.T_REF(Type.T_TERM(name, d));
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
	}
	
	public static abstract class Compound extends Pattern {
		public final Pair<Pattern,String>[] elements;
		public final boolean unbounded;
		
		public Compound(boolean unbounded, Pair<Pattern,String>[] elements, Attribute... attributes) {
			super(attributes);
			this.elements = elements;
			this.unbounded = unbounded;
		}
		
		public Compound(boolean unbounded, java.util.List<Pair<Pattern,String>> elements, Attribute... attributes) {
			super(attributes);
			this.elements = elements.toArray(new Pair[elements.size()]);			
			this.unbounded = unbounded;
		}
				
		public String toString() {
			String r = "";
			for(int i=0;i!=elements.length;++i) {
				if(i!=0) {
					r += ",";
				}
				r += elements[i];
			}
			if(unbounded) {
				r += "...";
			}
			if(this instanceof List) {
				return "[" + r + "]";
			} else {
				return "{" + r + "}";			
			}			
		}
	}
	
	public final static class List extends Compound {
		public List(boolean unbounded, Pair<Pattern,String>[] elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}
		
		public List(boolean unbounded, java.util.List<Pair<Pattern,String>> elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}	
		
		public Type.Ref type() {
			// FIXME: following should be an ArrayList<Type.Ref>
			ArrayList<Type> types = new ArrayList<Type>();
			for (Pair<Pattern, String> ps : elements) {
				types.add(ps.first().type());
			}
			return Type.T_REF(Type.T_LIST(unbounded, types));
		}
		
		public String toString() {
			String r = "";
			for(int i=0;i!=elements.length;++i) {
				if(i!=0) {
					r += ",";
				}
				r += elements[i];
			}
			if(unbounded) {
				r += "...";
			}			
			return "[" + r + "]";					
		}
	}
	
	public final static class Set extends Compound {
		public Set(boolean unbounded, Pair<Pattern,String>[] elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}
		
		public Set(boolean unbounded, java.util.List<Pair<Pattern,String>> elements, Attribute... attributes) {
			super(unbounded,elements,attributes);
		}	

		public Type.Ref type() {
			// FIXME: following should be an ArrayList<Type.Ref>
			ArrayList<Type> types = new ArrayList<Type>();
			for (Pair<Pattern, String> ps : elements) {
				types.add(ps.first().type());
			}
			// TODO: T_SET ?
			return Type.T_REF(Type.T_LIST(unbounded, types));
		}

		public String toString() {
			String r = "";
			for(int i=0;i!=elements.length;++i) {
				if(i!=0) {
					r += ",";
				}
				r += elements[i];
			}
			if(unbounded) {
				r += "...";
			}			
			return "{" + r + "}";					
		}
	}
}

