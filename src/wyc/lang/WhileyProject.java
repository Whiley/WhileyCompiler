package wyc.lang;

import java.util.*;

import wyc.lang.*;
import wyc.util.path.Path;
import wyil.io.ModuleReader;
import wyil.lang.*;
import wyil.ModuleLoader;
import wyil.util.Logger;
import wyil.util.ResolveError;
import wyil.util.Triple;

/**
 * A Whiley project represents the contextual information underpinning a given
 * compilation. This includes the WHILEYPATH, the package roots for all source
 * files and the binary destination folders as well. Bringing all of this
 * information together helps manage it, and enables a certain amount of global
 * analysis. For example, we can analyse dependencies between source files.
 * 
 * @author David J. Pearce
 */
public final class WhileyProject implements Iterable<WhileyFile>, ModuleLoader {	
	/**
	 * The source roots are locations which may contain the root of a package
	 * structure containing source files.
	 */
	private ArrayList<Path.Root> srcRoots;
	
	/**
	 * The external roots represent external libraries required for compiling
	 * this project. They may exist in several forms, including as jar files or
	 * directory roots (for binary files).
	 */
	private ArrayList<Path.Root> externalRoots;	
	
	/**
	 * A cache of those source files which have already been loaded.
	 */
	private final HashMap<ModuleID,WhileyFile> srcFileCache = new HashMap<ModuleID,WhileyFile>();
	
	/**
	 * A map from module identifiers to module objects. This is the master cache
	 * of modules which have been loaded during the compilation process. Once a
	 * module has been entered into the moduletable, it will not be loaded
	 * again.
	 */
	private HashMap<ModuleID, Module> binFileCache = new HashMap<ModuleID, Module>();	
	
	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import whiley.lang.*</code>
	 * corresponds to the triple <code>("whiley.lang",*,null)</code>.
	 */
	private final HashMap<Triple<PkgID,String,String>,ArrayList<ModuleID>> importCache = new HashMap();	
	
	/**
	 * The suffix map maps suffixes to module readers for those suffixes.
	 */
	private final HashMap<String,ModuleReader> suffixMap = new HashMap<String,ModuleReader>();
	
	/**
	 * The logger is used to log messages for the project.
	 */
	private Logger logger;
	
	public WhileyProject(Collection<Path.Root> srcRoots, Collection<Path.Root> libRoots) {
		this.srcRoots = new ArrayList<Path.Root>(srcRoots);
		this.externalRoots = new ArrayList<Path.Root>(libRoots);
		logger = Logger.NULL;
	}
	
	// ======================================================================
	// Public Configuration Interface
	// ======================================================================		
	
	/**
	 * Set the logger for this module loader.
	 * @param logger
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * Associate a given module reader with a given suffix.
	 * 
	 * @param suffix
	 *            --- filename extension of reader to associate with.
	 * @param reader
	 */
	public void setModuleReader(String suffix, ModuleReader reader) {
		suffixMap.put(suffix, reader);
	}

	
	// FIXME: to be deprecated
	public void register(Module m) {
		binFileCache.put(m.id(), m);
	}
	
	// ======================================================================
	// Public Accessor Interface
	// ======================================================================		
	
	/**
	 * Determine the complete set of files that need to be recompiled. This
	 * includes all source files either out a binary equivalent, or whose
	 * modification time is more recent. Furthermore, it additionally includes
	 * all dependents for those files, and their dependents, etc.
	 * 
	 * @return
	 */
//	public List<Path.Entry> getDelta() {
//		ArrayList<Path.Entry> delta = new ArrayList<Path.Entry>();
//		for(Path.Root srcRoot : sourcepath) {
//			for(Path.Entry e : srcRoot.list()) {
//				if(e.suffix().equals("whiley")) {
//					delta.add(e);
//				}
//			}
//		}
//		return delta;
//	}
	
	/**
	 * Determine whether a given package actually exists or not.
	 * 
	 * @param pid --- Package ID to check
	 * @return
	 */
	public boolean isPackage(PkgID pid) {
		try {
			for(Path.Root root : srcRoots) {
				if(root.exists(pid)) {
					return true;
				}
			}
			for(Path.Root root : externalRoots) {
				if(root.exists(pid)) {
					return true;
				}
			}
		} catch(Exception e) {
			// FIXME: figure how best to propagate this exception
		}
		
		return false;
	}
	
	/**
	 * Determine whether a given module actually exists or not.
	 * 
	 * @param mid --- Module ID to check
	 * @return
	 */
	public boolean isModule(ModuleID mid) {
		try {
			for(Path.Root root : srcRoots) {
				if(root.lookup(mid) != null) {
					return true;
				}
			}
			for(Path.Root root : externalRoots) {
				if(root.lookup(mid) != null) {
					return true;
				}
			}
		} catch(Exception e) {
			// FIXME: figure how best to propagate this exception
		}
		return false;
	}
		
	/**
	 * Determine whether a given name exists or not.
	 * 
	 * @param nid --- Name ID to check
	 * @return
	 */
	public boolean isName(NameID nid) {
		ModuleID mid = nid.module();
		WhileyFile wf = get(mid);
		if(wf != null) {
			// FIXME: check for the right kind of name
			return wf.hasName(nid.name());
		} else {
			try {
				Module m = loadModule(mid);
				// FIXME: check for the right kind of name
				return m.hasName(nid.name());
			} catch(ResolveError e) {
				return false;
			}
		}
	}	
	
	/**
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 * 
	 * @param imp
	 * @return
	 */
	public List<ModuleID> imports(WhileyFile.Import imp) {
		Triple<PkgID, String, String> key = new Triple(imp.pkg, imp.module,
				imp.name);
		ArrayList<ModuleID> matches = importCache.get(key);
		if (matches != null) {
			// cache hit
			return matches;
		} else {
			// cache miss
			matches = new ArrayList<ModuleID>();
			for (PkgID pid : matchPackage(imp.pkg)) {
				try {
					for (ModuleID mid : loadPackage(pid)) {
						if (imp.matchModule(mid.module())) {
							matches.add(mid);
						}
					}
				} catch (ResolveError ex) {
					// dead code
				}
			}
			importCache.put(key, matches);
		}
		return matches;
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
	
	// ======================================================================
	// Private Implementation
	// ======================================================================
	
	private Set<ModuleID> loadPackage(PkgID pid) throws ResolveError {
		HashSet<ModuleID> contents = new HashSet<ModuleID>();
		try {
			for(Path.Root root : srcRoots) {			
				for (Path.Entry e : root.list(pid)) {
					contents.add(e.id());
				}
			}
			for(Path.Root root : externalRoots) {			
				for (Path.Entry e : root.list(pid)) {
					contents.add(e.id());
				}
			}
		} catch(Exception e) {
			// FIXME: figure how best to propagate this exception
		}
		return contents;
	}
	
	/**
	 * This method takes a given package id from an import declaration, and
	 * expands it to find all matching packages. Note, the package id may
	 * contain various wildcard characters to match multiple actual packages.
	 * 
	 * @param imp
	 * @return
	 */
	private List<PkgID> matchPackage(PkgID pkg) {
		// FIXME: this method is junk
		if(isPackage(pkg)) {
			ArrayList<PkgID> matches = new ArrayList<PkgID>();				
			matches.add(pkg);
			return matches;
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	
	/**
	 * This method attempts to load a whiley module. The module is searched for
	 * on the WHILEYPATH. A resolve error is thrown if the module cannot be
	 * found or otherwise loaded.
	 * 
	 * @param module
	 *            The module to load
	 * @return the loaded module
	 */
	public Module loadModule(ModuleID module) throws ResolveError {		
		Module m = binFileCache.get(module);
						
		if (m != null) {
			return m; // module was previously loaded and cached
		} 
		
		try {
			// ok, now look for module inside package roots.
			Path.Entry entry = null;
			for(Path.Root root : srcRoots) {
				entry = root.lookup(module);
				if(entry != null) {
					break;
				}
			}
			for(Path.Root root : externalRoots) {
				entry = root.lookup(module);
				if(entry != null) {
					break;
				}
			}
			if(entry == null) {
				throw new ResolveError("Unable to find module: " + module);
			}			
			m = readModuleInfo(entry);
			if(m == null) {
				throw new ResolveError("Unable to find module: " + module);
			}
			return m;						
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception e) {				
			throw new ResolveError("Unable to find module: " + module,e);
		}	
	}		
	
	private Module readModuleInfo(Path.Entry entry) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long time = System.currentTimeMillis();
		long memory = runtime.freeMemory();
		ModuleReader reader = suffixMap.get(entry.suffix());
		ModuleID mid = entry.id();		
		Module mi = reader.read(mid, entry.contents());
				
		if(mi != null) {
			logger.logTimedMessage("Loaded " + entry.location() + ":" + mid,
					System.currentTimeMillis() - time, memory - runtime.freeMemory());
			binFileCache.put(mi.id(), mi);
		} else {
			
			logger.logTimedMessage("Ignored " + entry.location() + ":" + mid,
					System.currentTimeMillis() - time, memory - runtime.freeMemory());
		}
		
		return mi;
	}
}
