package wybs.util;

import java.util.HashMap;

import wybs.lang.Build;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;

/**
 * Provides a straightforward implementation of the Build.Graph interface.
 * 
 * @author David J. Pearce
 *
 */
public class StdBuildGraph implements Build.Graph {
	/**
	 * The derived from relation maps child entries to the parents they are
	 * derived from.
	 */
	private HashMap<Path.Entry<?>, Path.Entry<?>> derivedFrom = new HashMap<>();

	@Override
	public Entry<?> parent(Entry<?> child) {
		return derivedFrom.get(child);
	}

	@Override
	public void registerDerivation(Path.Entry<?> parent, Path.Entry<?> child) {
		derivedFrom.put(child, parent);
	}
}
