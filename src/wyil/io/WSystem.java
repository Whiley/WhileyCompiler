package wyil.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import wyil.lang.Module;
import wyil.lang.ModuleID;
import wyil.lang.PkgID;

/**
 * A WSystem provides an abstraction of the underlying filesystem, which is
 * suitable for the compiler. The purpose of abstracting the underlying file
 * system is primiarily to easy integration with frameworks that provide their
 * own abstractions. Thus, by implementing our own WSystem, we can 
 * 
 * @author djp
 * 
 */
public class WSystem {

	/**
	 * A module item represents a single item on the path.
	 * 
	 * @author djp
	 * 
	 */
	
	// =============================================================
	// Body
	// =============================================================
		
	private static final HashMap<String,ModuleReader> suffixMap = new HashMap<String,ModuleReader>();

	public static void register(String suffix, ModuleReader reader) {
		suffixMap.put(suffix, reader);
	}
	
	public static ModuleReader getModuleReader(String suffix) {		
		return suffixMap.get(suffix);					
	}
}
