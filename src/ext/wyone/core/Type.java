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

package wyone.core;

import java.util.*;

import wyil.lang.Attribute;
import wyil.util.SyntacticElement;

public interface Type extends SyntacticElement {

	public static final class Any extends SyntacticElement.Impl implements Type {
		public Any(Attribute... attributes) {
			super(attributes);
		}		
		public String toString() {
			return "*";
		}
	}
	public static final class Void extends SyntacticElement.Impl implements Type {
		public Void(Attribute... attributes) {
			super(attributes);
		}
		public String toString() {
			return "void";
		}
	}	
	public static final class Bool extends SyntacticElement.Impl implements Type {
		public Bool(Attribute... attributes) {
			super(attributes);
		}
		public String toString() {
			return "bool";
		}
	}
	public static final class Int extends SyntacticElement.Impl implements Type {
		public Int(Attribute... attributes) {
			super(attributes);
		}
		public String toString() {
			return "int";
		}
	}
	public static final class Real extends SyntacticElement.Impl implements Type {
		public Real(Attribute... attributes) {
			super(attributes);
		}
		public String toString() {
			return "real";
		}
	}
	public static final class Named extends SyntacticElement.Impl implements Type {		
		public final String name;		
		public Named(String name, Attribute... attributes) {
			super(attributes);
			this.name = name;
		}		
		public String toString() {
			return name;
		}
	}
	public static final class List extends SyntacticElement.Impl implements Type {
		public final Type element;
		public List(Type element, Attribute... attributes) {
			super(attributes);
			this.element = element;			
		}
		public String toString() {
			return "[" + element + "]";			
		}
	}
	public static final class Set extends SyntacticElement.Impl implements Type {
		public final Type element;
		public Set(Type element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}
		public String toString() {
			return "{" + element + "}";			
		}
	}	
	public static final class Record extends SyntacticElement.Impl implements Type {
		public final HashMap<String,Type> types;
		public Record(Map<String,Type> types, Attribute... attributes) {
			super(attributes);
			if(types.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.types = new HashMap<String,Type>(types);
		}
	}
	public static final class Tuple extends SyntacticElement.Impl implements Type {
		public final ArrayList<Type> types;
		public Tuple(Collection<Type> types, Attribute... attributes) {
			super(attributes);
			if(types.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.types = new ArrayList<Type>(types);
		}
	}	
}

