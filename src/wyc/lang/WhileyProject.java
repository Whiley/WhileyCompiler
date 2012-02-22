package wyc.lang;

import java.util.*;

import wyc.lang.*;
import wyil.lang.ModuleID;
import wyil.util.path.Path;

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
	/**
	 * The source soots are locations which may contain the root of a package
	 * structure containing source files.
	 */
	private ArrayList<Path.Root> srcRoots;
	
	/**
	 * The library roots represent external libraries required for compiling
	 * this project. They may exist in several forms, including as jar files or
	 * directory roots (for binary files).
	 */
	private ArrayList<Path.Root> libRoots;	
	
	/**
	 * A cache of those source files which have already been loaded.
	 */
	private final HashMap<ModuleID,WhileyFile> srcFileCache = new HashMap<ModuleID,WhileyFile>();
	
	public WhileyProject(Collection<Path.Root> srcRoots, Collection<Path.Root> libRoots) {
		this.srcRoots = new ArrayList<Path.Root>(srcRoots);
		this.libRoots = new ArrayList<Path.Root>(libRoots);
	}
	
	/**
	 * Determine the complete set of files that need to be recompiled. This
	 * includes all source files either out a binary equivalent, or whose
	 * modification time is more recent. Furthermore, it additionally includes
	 * all dependents for those files, and their dependents, etc.
	 * 
	 * @return
	 */
	public List<Path.Entry> determineDelta() {
		ArrayList<Path.Entry> delta = new ArrayList<Path.Entry>();
		for(Path.Root srcRoot : srcRoots) {
			for(Path.Entry e : srcRoot.list()) {
				if(e.suffix().equals("whiley")) {
					delta.add(e);
				}
			}
		}
		return delta;
	}
	
	public Iterator<WhileyFile> iterator() {
		return srcFileCache.values().iterator();
	}
	
	public boolean add(WhileyFile file) {
		return srcFileCache.put(file.module, file) != null;
	}
	
	public WhileyFile get(ModuleID mid) {
		return srcFileCache.get(mid);
	}
}
