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
public class ModuleTable {
	/**
	 * The Closed World Assumption indicates whether or not we should attempt to
	 * compile source files that we encouter.
	 */
	private boolean closedWorldAssumption = true;
	
	/**
	 * The whiley path is a list of locations which must be searched in
	 * ascending order for whiley files.
	 */
	private ArrayList<Path.Root> whileypath;
	
	/**
	 * A map from module identifiers to module objects. This is the master cache
	 * of modules which have been loaded during the compilation process. Once a
	 * module has been entered into the moduletable, it will not be loaded
	 * again.
	 */
	private HashMap<ModuleID, Module> moduletable = new HashMap<ModuleID, Module>();

	/**
	 * A map from module identifiers to skeleton objects. This is required to
	 * permit preregistration of source files during compilation.
	 */
	private HashMap<ModuleID, Skeleton> skeletontable = new HashMap<ModuleID, Skeleton>();

	/**
	 * A map from module identifiers to file system locations. This helps us to
	 * quickly find and load a given module. Once the module is loaded, it will
	 * be placed into the moduletable.
	 */
	private HashMap<ModuleID,Path.Entry> filetable = new HashMap<ModuleID,Path.Entry>();

	/**
	 * This identifies which packages have had their contents fully resolved.
	 * All items in a resolved package must have been loaded into the filetable.
	 */
	private HashMap<PkgID, HashSet<ModuleID>> packages = new HashMap<PkgID, HashSet<ModuleID>>();
	
	/**
     * The failed packages set is a collection of packages which have been
     * requested, but are known not to exist. The purpose of this cache is
     * simply to speed up package resolution.
     */
	private final HashSet<PkgID> failedPackages = new HashSet<PkgID>();
	
	/**
	 * The suffix map maps suffixes to module readers for those suffixes.
	 */
	private static final HashMap<String,ModuleReader> suffixMap = new HashMap<String,ModuleReader>();	
	
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
	
	public ModuleTable(Collection<Path.Root> whileypath, Logger logger) {
		this.logger = logger;
		this.whileypath = new ArrayList<Path.Root>(whileypath);		
	}
	
	public ModuleTable(Collection<Path.Root> whileypath) {
		this.logger = Logger.NULL;
		this.whileypath = new ArrayList<Path.Root>(whileypath);		
	}
	
	public void setClosedWorldAssumption(boolean flag) {
		closedWorldAssumption = flag;
	}
	
	/**
	 * Set the logger for this module loader.
	 * @param logger
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public static void setModuleReader(String suffix, ModuleReader reader) {
		suffixMap.put(suffix, reader);
	}
	
	public static ModuleReader getModuleReader(String suffix) {		
		return suffixMap.get(suffix);					
	}
	
	/**
	 * This function checks whether the supplied package exists or not.
	 * 
	 * @param pkg
	 *            The package whose existence we want to check for.
	 * 
	 * @return true if the package exists, false otherwise.
	 */
	public boolean isPackage(PkgID pkg) {
		try {
			resolvePackage(pkg);
			return packages.containsKey(pkg);
		} catch(ResolveError e) {
			return false;
		}
	}	
	
	public void preregister(Skeleton skeleton, String filename) {		
		skeletontable.put(skeleton.id(), skeleton);			
	}
	
	public void register(Module module) {			
		moduletable.put(module.id(), module);	
	}		
	
	/**
	 * This methods attempts to resolve the correct package for a named item,
	 * given a list of imports. Resolving the correct package may require
	 * loading modules as necessary from the whileypath and/or compiling modules
	 * for which only source code is currently available.
	 * 
	 * @param name
	 *            A module name without package specifier.
	 * @param imports
	 *            A list of import declarations to search through. Imports are
	 *            searched in order of appearance.
	 * @return The resolved package.
	 * @throws ModuleNotFoundException
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(String name, List<Import> imports)
			throws ResolveError {	
		
		for (Import imp : imports) {
			if(imp.matchName(name)) {
				for(ModuleID mid : matchImport(imp)) {
					try {														
						Skeleton mi = loadSkeleton(mid);					
						if (mi.hasName(name)) {
							return new NameID(mid,name);
						} 					
					} catch(ResolveError rex) {
						// ignore. This indicates we simply couldn't resolve
                        // this module. For example, if it wasn't a whiley class
                        // file.						
					}
				}
			}
		}
		
		throw new ResolveError("name not found: " + name);
	}
	
	public NameID resolveAsName(List<String> names, List<Import> imports) throws ResolveError {
		if(names.size() == 1) {
			return resolveAsName(names.get(0),imports);
		} else if(names.size() == 2) {
			String name = names.get(1);
			ModuleID mid = resolveAsModule(names.get(0),imports);
			Skeleton mi = loadSkeleton(mid);					
			if (mi.hasName(name)) {
				return new NameID(mid,name);
			} 
		} else {
			String name = names.get(names.size()-1);
			String module = names.get(names.size()-2);
			PkgID pkg = new PkgID(names.subList(0,names.size()-2));
			ModuleID mid = new ModuleID(pkg,module);
			Skeleton mi = loadSkeleton(mid);					
			if (mi.hasName(name)) {
				return new NameID(mid,name);
			}
		}
		
		String name = null;
		for(String n : names) {
			if(name != null) {
				name = name + "." + n;
			} else {
				name = n;
			}			
		}
		throw new ResolveError("name not found: " + name);
	}
	
	/**
	 * This method attempts to resolve the given name as a module name, given a
	 * list of imports.
	 * 
	 * @param name
	 * @param imports
	 * @return
	 * @throws ResolveError
	 */
	public ModuleID resolveAsModule(String name, List<Import> imports)
			throws ResolveError {
		
		for (Import imp : imports) {
			for(ModuleID mid : matchImport(imp)) {				
				if(mid.module().equals(name)) {
					return mid;
				}
			}
		}
		
		
		throw new ResolveError("module not found: " + name);
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
			// ok, now look for module inside package.
			Path.Entry wmod = filetable.get(module);
			if(wmod == null) {
				throw new ResolveError("Unable to find module: " + module);
			}
			return readModuleInfo(wmod);				
		} catch(IOException io) {				
			throw new ResolveError("Unable to find module: " + module,io);
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
			PathUnit wmod = filetable.get(module);
			if(wmod == null) {
				throw new ResolveError("Unable to find module: " + module);
			}
			return readModuleInfo(wmod);
		} catch(IOException io) {
			throw new ResolveError("Unagle to find module: " + module,io);
		}			
	}	

	/**
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 * 
	 * @param imp
	 * @return
	 */
	private List<ModuleID> matchImport(Import imp) {
		ArrayList<ModuleID> matches = new ArrayList<ModuleID>();
		for (PkgID pid : matchPackage(imp.pkg)) {
			try {
				resolvePackage(pid);;				
				for (ModuleID m : packages.get(pid)) {					
					if (imp.matchModule(m.module())) {
						matches.add(m);
					}
				}
			} catch (ResolveError ex) {
				// dead code
			}
		}
		return matches;
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
		ArrayList<PkgID> matches = new ArrayList<PkgID>();
		try {
			resolvePackage(pkg);
			matches.add(pkg);
		} catch(ResolveError er) {}
		return matches;
	}
	
	/**
     * This method searches the WHILEYPATH looking for a matching package.
     * 
     * @param pkg --- the package to look for
     * @return
     */
	private void resolvePackage(PkgID pkg) throws ResolveError {			
		// First, check if we have already resolved this package.						
		if(packages.containsKey(pkg)) {
			return;
		} else if(failedPackages.contains(pkg)) {
			// yes, it's already been resolved but it doesn't exist.
			throw new ResolveError("package not found: " + pkg);
		}

		// package has not been previously resolved, so try whileypath.
		HashSet<ModuleID> contents = new HashSet<ModuleID>();		
		for (PathContainer c : whileypath) {
			// load package contents
			for(PathItem item : c.list(pkg)) {
				if(item instanceof PathUnit) {					
					PathUnit mod = (PathUnit) item; 
					filetable.put(mod.id(), mod);
					contents.add(mod.id());
				}			
			}
		}
				
		if(!contents.isEmpty()) {			
			packages.put(pkg,contents);
		} else {
			failedPackages.add(pkg);
			throw new ResolveError("package not found: " + pkg);
		}
	}	
	
	private Module readModuleInfo(PathUnit moduleInfo) throws IOException {
		long time = System.currentTimeMillis();

		Module mi = moduleInfo.read();
		logger.logTimedMessage("Loaded " + mi.id(), System.currentTimeMillis()
				- time);
		moduletable.put(mi.id(), mi);
		return mi;
	} 	
}
