// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import wyjc.io.ClassFileLoader; // to be deprecated
import wyil.io.*;
import wyil.lang.*;
import wyil.path.Path;
import wyil.util.*;

/**
 * Responsible for locating whiley modules on the WHILEYPATH, and retaining
 * information about them which can be used to compile other whiley files. This
 * is a critical component of the Whiley compiler.
 * 
 * @author David J. Pearce
 * 
 */
public class ModuleLoader {
	/**
	 * The source path is a list of locations which must be searched in
	 * ascending order for whiley files.
	 */
	protected ArrayList<Path.Root> sourcepath;
	
	/**
	 * The whiley path is a list of locations which must be searched in
	 * ascending order for wyil files.
	 */
	protected ArrayList<Path.Root> whileypath;
	
	/**
	 * A map from module identifiers to module objects. This is the master cache
	 * of modules which have been loaded during the compilation process. Once a
	 * module has been entered into the moduletable, it will not be loaded
	 * again.
	 */
	protected HashMap<ModuleID, Module> moduletable = new HashMap<ModuleID, Module>();

	/**
	 * A map from module identifiers to skeleton objects. This is required to
	 * permit preregistration of source files during compilation.
	 */
	protected HashMap<ModuleID, Skeleton> skeletontable = new HashMap<ModuleID, Skeleton>();

	/**
	 * This identifies which packages have had their contents fully resolved.
	 * All items in a resolved package must have been loaded into the filetable.
	 */
	protected HashMap<PkgID, ArrayList<Path.Root>> packageroots = new HashMap<PkgID, ArrayList<Path.Root>>();

	/**
     * The failed packages set is a collection of packages which have been
     * requested, but are known not to exist. The purpose of this cache is
     * simply to speed up package resolution.
     */
	protected final HashSet<PkgID> failedPackages = new HashSet<PkgID>();
	
	/**
	 * The suffix map maps suffixes to module readers for those suffixes.
	 */
	protected final HashMap<String,ModuleReader> suffixMap = new HashMap<String,ModuleReader>();	
	
	/**
	 * Provides basic information regarding what names are defined within a
	 * module. It represents the minimal knowledge regarding a module that we
	 * can have. Skeletons are used early on in the compilation process to help
	 * with name resolution.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public abstract static class Skeleton {
		protected final ModuleID mid;

		public Skeleton(ModuleID mid) {
			this.mid = mid;
		}
		
		public ModuleID id() {
			return mid;
		}

		public abstract boolean hasName(String name);
	}
		
	/**
	 * The logger is used to log messages from the module loader.
	 */
	private Logger logger;
	
	public ModuleLoader(Collection<Path.Root> sourcepath, Collection<Path.Root> whileypath, Logger logger) {
		this.logger = logger;
		this.sourcepath = new ArrayList<Path.Root>(sourcepath);
		this.whileypath = new ArrayList<Path.Root>(whileypath);		
	}
	
	public ModuleLoader(Collection<Path.Root> sourcepath, Collection<Path.Root> whileypath) {
		this.logger = Logger.NULL;
		this.sourcepath = new ArrayList<Path.Root>(sourcepath);
		this.whileypath = new ArrayList<Path.Root>(whileypath);		
	}
	
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
		
	/**
	 * Register a given skeleton with this loader. This ensures that when
	 * skeleton requests are made, this skeleton will be used instead of
	 * searching for it on the whileypath.
	 * 
	 * @param skeleton
	 *            --- skeleton to preregister.
	 */
	public void preregister(Skeleton skeleton) {		
		skeletontable.put(skeleton.id(), skeleton);			
	}
	
	/**
	 * Register a given module with this loader. This ensures that when requests
	 * are made for this module, this will be returned instead of searching for
	 * it on the whileypath.
	 * 
	 * @param module
	 *            --- module to register.
	 */
	public void register(Module module) {			
		moduletable.put(module.id(), module);	
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
		Module m = moduletable.get(module);
		
		if(m != null) {
			return m; // module was previously loaded and cached
		}
			
		// module has not been previously loaded.
		resolvePackage(module.pkg());						
		
		try {
			// ok, now look for module inside package roots.
			Path.Entry entry = null;
			for(Path.Root root : packageroots.get(module.pkg())) {
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
	
	/**
	 * This method attempts to load a whiley module skeleton. A skeleton
	 * provides signature information about a module. For example, the signature
	 * of all methods. However, a skeleton does not provide access to a methods
	 * body. The skeleton is looked up in the internal skeleton table. If not
	 * found there, then the WHILEYPATH is searched. A resolve error is thrown
	 * if the module cannot be found or otherwise loaded.
	 * 
	 * @param module
	 *            The module skeleton to load
	 * @return the loaded module
	 */
	public Skeleton loadSkeleton(ModuleID module) throws ResolveError {
		Skeleton skeleton = skeletontable.get(module);
		if(skeleton != null) {
			return skeleton;
		} 		
		Module m = moduletable.get(module);
		if(m != null) {
			return m; // module was previously loaded and cached
		}
		
		// module has not been previously loaded.
		resolvePackage(module.pkg());
				
		try {
			// ok, now look for skeleton inside package roots.
			Path.Entry entry = null;
			for(Path.Root root : packageroots.get(module.pkg())) {
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
	
	/**
	 * This method searches the WHILEYPATH looking for a matching package. If
	 * the package is found, it's contents are loaded. Otherwise, a resolve
	 * error is thrown.
	 * 
	 * @param pkg
	 *            --- the package to look for
	 * @return
	 */
	public void resolvePackage(PkgID pkg) throws ResolveError {							
		// First, check if we have already resolved this package.						
		if(packageroots.containsKey(pkg)) {
			return;
		} else if(failedPackages.contains(pkg)) {			
			// yes, it's already been resolved but it doesn't exist.
			throw new ResolveError("package not found: " + pkg);
		}

		ArrayList<Path.Root> roots = new ArrayList<Path.Root>();
		try {
			// package not been previously resolved, so first try sourcepath.
			for (Path.Root c : sourcepath) {
				if(c.exists(pkg)) {					
					roots.add(c);
				}				
			}
			// second, try whileypath.
			for (Path.Root c : whileypath) {
				if(c.exists(pkg)) {					
					roots.add(c);
				}
			}
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception e) {							
			// silently ignore.
		}
				
		if(!roots.isEmpty()) {	
			packageroots.put(pkg,roots);
		} else {
			failedPackages.add(pkg);
			throw new ResolveError("package not found: " + pkg);
		}
	}	
	
	private Module readModuleInfo(Path.Entry entry) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long time = System.currentTimeMillis();
		long memory = runtime.freeMemory();
		ModuleReader reader = suffixMap.get(entry.suffix());
		Module mi = reader.read(entry.id(), entry.contents());
		
		if(mi != null) {
			logger.logTimedMessage("Loaded " + entry.location() + ":" + entry.id(),
					System.currentTimeMillis() - time, memory - runtime.freeMemory());
			moduletable.put(mi.id(), mi);
		} else {
			logger.logTimedMessage("Ignored " + entry.location() + ":" + entry.id(),
					System.currentTimeMillis() - time, memory - runtime.freeMemory());
		}
		return mi;
	}
}
