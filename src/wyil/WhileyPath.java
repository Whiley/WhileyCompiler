package wyil;

import java.util.*;
import java.io.*;

import wyil.lang.*;

/**
 * <p>
 * Abstracts the concept of the WHILEYPATH.  
 * </p>
 * @author djp
 * 
 */
public class WhileyPath {
		
	// =============================================================
	// Body
	// =============================================================
	
	private ArrayList<WhileyPath.Item> items;	
	
	public WhileyPath() {
		items = new ArrayList<WhileyPath.Item>();
		suffixMap = new HashMap<String,ItemCreator>();
	}

	public List<WhileyPath.Item> items() {
		return Collections.unmodifiableList(items);
	}
	
	public void add(WhileyPath.Item item) {
		items.add(item);
	}
}
