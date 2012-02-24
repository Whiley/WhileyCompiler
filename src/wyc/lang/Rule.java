package wyc.lang;

import java.util.*;
import wyc.io.Path;

public class Rule {
	/**
	 * Maps every source root to a target root. 
	 */
	private final IdentityHashMap<Path.Root,Path.Root> mapping;
		
	/**
	 * Builder responsible for implementing this rule.
	 */
	private final Builder builder;
	
	public Rule(Builder builder) {
		this.builder = builder;
		this.mapping = new IdentityHashMap<Path.Root,Path.Root>();
	}
	
	/**
	 * Add a mapping from a source root to a target root.
	 * 
	 * @param source
	 * @param target
	 */
	public void add(Path.Root source,Path.Root target) {
		mapping.put(source,target);
	}
}
