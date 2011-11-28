package wyil;

import java.util.*;
import wyil.io.*;

/**
 * <p>
 * Abstracts the concept of the WHILEYPATH.  
 * </p>
 * @author djp
 * 
 */
public class WhileyPath {
	private ArrayList<WSystem.Item> items = new ArrayList<WSystem.Item>();

	public List<WSystem.Item> items() {
		return Collections.unmodifiableList(items);
	}

	public void add(WSystem.Item item) {
		items.add(item);
	}
}
