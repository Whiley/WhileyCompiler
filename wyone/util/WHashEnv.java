package wyone.util;

import java.util.HashMap;
import java.util.Map;

import wyone.core.*;

public class WHashEnv extends HashMap<String,WType> implements WEnvironment, Cloneable {
	
	public WHashEnv() {
		super();
	}
	
	public WHashEnv(Map<? extends String, ? extends WType> map) {
		super(map);
	}
	
	public Object clone() {
		return new WHashEnv(this);
	}
	
	/**
	 * Set the type of a variable in the typing environment.
	 * 
	 * @param var
	 * @param type
	 */
	public WType put(String var,WType type) {
		WType old = get(var);
		if (old != null && !old.equals(type)) {			
			throw new IllegalArgumentException("Variable " + var
					+ " cannot have types: " + type + " & " + old);
		}
		return super.put(var, type);
	}
			
	/**
	 * The full type returns the true type of the variable in question. This may
	 * be a function type, if the variable accepts arguments.
	 * 
	 * @param var
	 * @return
	 */
	public WType fullType(String var) {
		return get(var);
	}

	/**
	 * This returns type that this expression will evaluate to. So, in the case
	 * of a function, then this will be its return type.
	 * 
	 * @param var
	 * @return
	 */
	public WType evalType(String var) {
		WType t = get(var);
		if(t instanceof WFunType) {
			WFunType ft = (WFunType) t;
			return ft.returnType();
		} else {
			return t;
		}
	}
	
	public String toString() {
		String r = "[";
		boolean firstTime=true;
		for(Map.Entry<String,WType> e : entrySet()) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += e.getKey() + "->" + e.getValue();
		}
		return r + "]";
	}
}


