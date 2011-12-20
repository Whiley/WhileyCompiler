package wyc.util;

import java.util.HashMap;

public final class RefCountedHashMap<K,V> {
	private final HashMap<K,V> map;
	private int count; // refCount
	
	public RefCountedHashMap() {
		count = 1;
		map = new HashMap<K,V>();
	}
	
	private RefCountedHashMap(HashMap<K,V> types) {
		count = 1;
		this.map = (HashMap<K,V>) types.clone();
	}

	public V get(K key) {
		return map.get(key);
	}
	
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}
	
	public RefCountedHashMap<K,V> put(K key, V value) {
		if(count == 1) {
			map.put(key,value);
			return this;
		} else {				
			RefCountedHashMap<K,V> nenv = new RefCountedHashMap<K,V>(map);
			nenv.map.put(key,value);
			count--;
			return nenv;
		}
	}
	
	public RefCountedHashMap<K,V> putAll(RefCountedHashMap<K,V> env) {
		if(count == 1) {
			HashMap<K,V> envTypes = env.map;							
			map.putAll(envTypes);			
			return this;
		} else { 
			RefCountedHashMap<K,V> nenv = new RefCountedHashMap<K,V>(map);
			HashMap<K,V> envTypes = env.map;			
			nenv.map.putAll(envTypes);			
			count--;
			return nenv;				
		}
	}
	
	public RefCountedHashMap<K,V> remove(K key) {
		if(count == 1) {
			map.remove(key);
			return this;
		} else {				
			RefCountedHashMap<K,V> nenv = new RefCountedHashMap<K,V>(map);
			nenv.map.remove(key);
			count--;
			return nenv;
		}
	}		
	
	public RefCountedHashMap<K,V> clone() {
		count++;
		return this;
	}
	
	public void free() {
		--count;			
	}
}
