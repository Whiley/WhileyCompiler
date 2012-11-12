package wyone.core;

import wyone.core.Type.Any;
import wyone.core.Type.Bag;
import wyone.core.Type.List;
import wyone.core.Type.Void;

public class Types {
	
	/**
	 * Return true if t2 is a subtype of t1 in the context of the given type
	 * hierarchy.  
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static boolean isSubtype(Type t1, Type t2) {
		// TODO:
		return false;
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
		} else if(t instanceof Type.Strung) {
			return "S";
		} else if(t instanceof Type.Ref) {
			Type.Ref r = (Type.Ref) t;
			return "^" + type2str(r.element());
		} else if(t instanceof Type.Fun) {
			Type.Fun f = (Type.Fun) t;
			return type2str(f.ret()) + "(" + type2str(f.param()) + ")";
		} else if(t instanceof Type.Compound) {
			Type.Compound st = (Type.Compound) t;
			String r = "";
			Type[] elements = st.elements();
			for(Type p : elements){
				r += type2str(p);
			}
			if(st.unbounded()) {
				r += ".";
			}
			if(st instanceof List) {			
				return "[" + r + "]";
			} else if(st instanceof Bag) {
				return "|" + r + "|";
			} else {
				return "{" + r + "}";
			}	
		} else if (t instanceof Type.Term) {
			Type.Term st = (Type.Term) t;
			String r = "T" + st.name();
			Type element = st.element();
			if (element != null) {
				return r + type2str(element);
			} else {
				return r;
			}
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}
	}
	
}
