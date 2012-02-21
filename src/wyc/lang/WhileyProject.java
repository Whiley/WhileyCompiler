package wyc.lang;

import java.util.*;
import wyc.lang.*;
import wyil.lang.ModuleID;

/**
 * A whiley project represents the contextual information underpinning a given
 * compilation. This includes the WHILEYPATH, the package roots for all source
 * files and the binary destination folders as well. Bringing all of this
 * information together helps manage it, and enables a certain amount of global
 * analysis. For example, we can analyse dependencies between source files.
 * 
 * @author David J. Pearce
 */
public final class WhileyProject implements Iterable<WhileyFile> {	
	private final HashMap<ModuleID,WhileyFile> files = new HashMap<ModuleID,WhileyFile>();
	
	
	public Iterator<WhileyFile> iterator() {
		return files.values().iterator();
	}
	
	public boolean add(WhileyFile file) {
		return files.put(file.module, file) != null;
	}
	
	public WhileyFile get(ModuleID mid) {
		return files.get(mid);
	}
}
