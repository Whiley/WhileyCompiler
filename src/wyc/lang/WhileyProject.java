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
	 * The source path is a list of locations which must be searched in
	 * ascending order for whiley files.
	 */
	private ArrayList<Path.Root> sourcepath;
	
	/**
	 * The whiley path is a list of locations which must be searched in
	 * ascending order for wyil files.
	 */
	private ArrayList<Path.Root> whileypath;	
	
	/**
	 * A cache of those source files which have already been loaded.
	 */
	private final HashMap<ModuleID,WhileyFile> srcFileCache = new HashMap<ModuleID,WhileyFile>();
		
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
