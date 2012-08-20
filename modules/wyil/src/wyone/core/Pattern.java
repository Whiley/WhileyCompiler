package wyone.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import wyone.core.Type.Compound.Kind;
import wyone.util.Pair;
import wyone.util.SyntacticElement;

public abstract class Pattern extends SyntacticElement.Impl {
	
	public Pattern(Attribute... attributes) {
		super(attributes);
	}	
	
	public abstract Type type();
		
	public static final class Leaf extends Pattern {
		public Type type;
		
		public Leaf(Type type) {
			this.type = type;
		}
		
		public Type type() {
			return type;
		}
		
		public String toString() {
			return type.toString();
		}		
	}
	
	public static final class Compound extends Pattern {
		public final Kind kind;
		public final ArrayList<Pair<Pattern, String>> elements;
		public final boolean unbounded;

		public Compound(Type.Compound.Kind kind, boolean unbound,
				Collection<Pair<Pattern, String>> params,
				Attribute... attributes) {
			super(attributes);
			this.kind = kind;
			this.elements = new ArrayList<Pair<Pattern, String>>(params);
			this.unbounded = unbound;			
		}
		
		public Type.Compound type() {
			Type[] types = new Type[elements.size()];
			for(int i=0;i!=types.length;++i) {
				types[i] = elements.get(i).first().type();
			}
			return Type.T_COMPOUND(kind,unbounded,types);
		}
		
		public String toString() {
			String r = "";
			switch(kind) {
			case LIST:
				r += "[";
				break;
			case SET:
				r += "{";
				break;			
			}
			boolean firstTime = true;
			for(Pair<Pattern,String> p : elements) {
				if(!firstTime) {
					r +=", ";
				}
				firstTime=false;
				Pattern pt = p.first();
				String var = p.second();
				r += pt.toString();
				if(var != null) {
					r += " " + var;
				}				
			}
			switch(kind) {
			case LIST:
				r += "]";
				break;
			case SET:
				r += "}";
				break;			
			}
			return r;
		}
	}
	
	public static final class Term extends Pattern {		
		public final String name;
		public final Pattern data;
		public final String var;
		
		public Term(String name, Pattern data, String var,
				Attribute... attributes) {
			super(attributes);
			this.name = name;	
			this.data = data;
			this.var = var;
		}
		
		public Type.Term type() {
			return Type.T_TERM(name,data.type());
		}
						
		public String toString() {
			if(data instanceof Leaf) {
				Leaf l = (Leaf) data;
				if(l.type == Type.T_VOID) {
					return name;
				} else if(var != null){
					return name + "(" + data + " " + var + ")";
				} else {
					return name + " " + data;
				}
			}
			return name + data;
		}			
	}
		
	private static int[] toIntArray(ArrayList<Integer> items) {
		int[] r = new int[items.size()];
		for(int i=0;i!=r.length;++i) {
			r[i] = items.get(i);
		}
		return r;
	}
}
