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

public abstract class Type extends SyntacticElement.Impl implements SyntacticElement {

	public Type(Attribute... attributes) {
		super(attributes);
	}			
	
	public static final class Any  extends Type {
		public Any(Attribute... attributes) {
			super(attributes);
		}		
		public String toString() {
			return "*";
		}
	}
	public static final class Void  extends Type {
		public Void(Attribute... attributes) {
			super(attributes);
		}
		public String toString() {
			return "void";
		}
	}	
	public static final class Bool  extends Type {
		public Bool(Attribute... attributes) {
			super(attributes);
		}
		public String toString() {
			return "bool";
		}
	}
	public static final class Int  extends Type {
		public Int(Attribute... attributes) {
			super(attributes);
		}
		public String toString() {
			return "int";
		}
	}
	public static final class Real  extends Type {
		public Real(Attribute... attributes) {
			super(attributes);
		}
		public String toString() {
			return "real";
		}
	}
	public static final class Named  extends Type {		
		public final String name;		
		public Named(String name, Attribute... attributes) {
			super(attributes);
			this.name = name;
		}		
		public String toString() {
			return name;
		}
	}
	public static final class List  extends Type {
		public final Type element;
		public List(Type element, Attribute... attributes) {
			super(attributes);
			this.element = element;			
		}
		public String toString() {
			return "[" + element + "]";			
		}
	}
	public static final class Set  extends Type {
		public final Type element;
		public Set(Type element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}
		public String toString() {
			return "{" + element + "}";			
		}
	}	
	public static final class Record  extends Type {
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
	public static final class Tuple  extends Type {
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
	
	public static String type2str(Type t) {
		if(t instanceof Any) {
			return "*";
		} else if(t instanceof Void) {
			return "V";
		} else if(t instanceof Type.Bool) {
			return "B";
		} else if(t instanceof Type.Int) {
			return "I";
		} else if(t instanceof Type.Real) {
			return "R";
		} else if(t instanceof Type.List) {
			Type.List st = (Type.List) t;
			return "[" + type2str(st.element) + "]";
		} else if(t instanceof Type.Set) {
			Type.Set st = (Type.Set) t;
			return "{" + type2str(st.element) + "}";
		} else if(t instanceof Type.Record) {
			Type.Record st = (Type.Record) t;
			ArrayList<String> keys = new ArrayList<String>(st.types.keySet());
			Collections.sort(keys);
			String r="(";
			for(String k : keys) {
				Type kt = st.types.get(k);
				r += k + ":" + type2str(kt);
			}			
			return r + ")";
		} else if(t instanceof Type.Named) {
			Type.Named st = (Type.Named) t;
			return "N" + st.name + ";";
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}
	}

}

