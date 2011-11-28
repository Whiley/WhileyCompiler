package wyc;

import java.io.InputStream;
import java.util.List;

import wyil.WhileyPath;
import wyil.lang.*;
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
public class NameResolver {
	private WhileyPath whileypath;
	
	/**
	 * This function checks whether the supplied package exists or not.
	 * 
	 * @param pkg
	 *            The package whose existence we want to check for.
	 * 
	 * @return true if the package exists, false otherwise.
	 */
	public boolean isPackage(PkgID id) {
		
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
	public NameID resolveAsName(String name, List<Import> imports) throws ResolveError {
		
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
	public ModuleID resolveAsModule(String name, List<Import> imports) throws ResolveError {
		
	}

	/**
	 * Open an input stream to access the contents of a given module. If the
	 * module cannot be found, then a resolve error is thrown.
	 */
	public InputStream open(ModuleID module) throws ResolveError {
		
	}
}
