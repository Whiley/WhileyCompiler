package wyc.builder;

import java.util.HashMap;
import java.util.Set;


public final class Environment {
	private final HashMap<String,Nominal> map;
	private int count; // refCount
	
	public Environment() {
		count = 1;
		map = new HashMap<String,Nominal>();
	}
	
	private Environment(HashMap<String,Nominal> types) {
		count = 1;
		this.map = (HashMap<String,Nominal>) types.clone();
	}

	public Nominal get(String key) {
		return map.get(key);
	}
	
	public boolean containsKey(String key) {
		return map.containsKey(key);
	}
	
	public Set<String> keySet() {
		return map.keySet();
	}
	
	public Environment put(String key, Nominal value) {
		if(count == 1) {
			map.put(key,value);
			return this;
		} else {				
			Environment nenv = new Environment(map);
			nenv.map.put(key,value);
			count--;
			return nenv;
		}
	}
	
	public Environment putAll(Environment env) {
		if(count == 1) {
			HashMap<String,Nominal> envTypes = env.map;							
			map.putAll(envTypes);			
			return this;
		} else { 
			Environment nenv = new Environment(map);
			HashMap<String,Nominal> envTypes = env.map;			
			nenv.map.putAll(envTypes);			
			count--;
			return nenv;				
		}
	}
	
	public Environment remove(String key) {
		if(count == 1) {
			map.remove(key);
			return this;
		} else {				
			Environment nenv = new Environment(map);
			nenv.map.remove(key);
			count--;
			return nenv;
		}
	}		
	
	public Environment clone() {
		count++;
		return this;
	}
	
	public void free() {
		--count;			
	}
	
	public String toString() {
		return map.toString();
	}
	
	public int hashCode() {
		return map.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof Environment) {
			Environment r = (Environment) o;
			return map.equals(r.map);
		}
		return false;
	}
}
