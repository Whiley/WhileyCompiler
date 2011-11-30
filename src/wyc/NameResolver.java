package wyc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.path.Path;
import wyil.util.Logger;
import wyil.util.ResolveError;

/**
 * <p>
 * A Name Resolver is responsible for searching the WHILEYPATH to resolve given
 * names and packages. For example, we may wish to resolve a name "Reader"
 * within a given import context of "import whiley.io.*". In such case, the name
 * resolve will first locate the package "whiley.io", and then decide whether
 * there is a module named "Reader".
 * </p>
 * 
 * 
 * @author djp
 * 
 */
public class NameResolver extends ModuleLoader {
	public NameResolver(Collection<Path.Root> sourcepath,
			Collection<Path.Root> whileypath, Logger logger) {
		super(sourcepath, whileypath, logger);
	}

	public NameResolver(Collection<Path.Root> sourcepath,
			Collection<Path.Root> whileypath) {
		super(sourcepath, whileypath);
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
			return true;
		} catch(ResolveError e) {
			return false;
		}
	}		
	
	/**
	 * This methods attempts to resolve the correct package for a named item,
	 * given a list of imports. Resolving the correct package may require
	 * loading modules as necessary from the WHILEYPATH and/or compiling modules
	 * for which only source code is currently available.
	 * 
	 * @param name
	 *            A module name without package specifier.
	 * @param imports
	 *            A list of import declarations to search through. Imports are
	 *            searched in order of appearance.
	 * @return The resolved name.
	 * @throws ResolveError
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(String name, List<Import> imports)
			throws ResolveError {		
		for (Import imp : imports) {
			if (imp.matchName(name)) {
				for (ModuleID mid : matchImport(imp)) {
					try {
						Skeleton mi = loadSkeleton(mid);
						if (mi.hasName(name)) {
							return new NameID(mid, name);
						}
					} catch (ResolveError rex) {
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
}
