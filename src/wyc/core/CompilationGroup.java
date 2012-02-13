package wyc.core;

import java.util.*;
import wyc.lang.*;
import wyil.lang.ModuleID;

/**
 * A compilation group represents the complete set of source files which are
 * being compiled in any given run of the compiler. Bringing all of the files
 * together in one place allows us to do a certain amount of global analysis.
 * For example, we can analyse dependencies between source files.
 * 
 * @author David J. Pearce
 */
public final class CompilationGroup implements Iterable<WhileyFile> {	
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
