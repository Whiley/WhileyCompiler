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
	// =============================================================
	// Interfaces
	// =============================================================
	
	/**
	 * An item represents anything which may be on the WHILEYPATH.
	 * 
	 * @author djp
	 * 
	 */
	public interface Item {
		/**
		 * Refresh after something changed. This gives an opportunity for
		 * directories on the path to reload themselves, etc.
		 */
		public void refresh() throws IOException;
	}

	/**
	 * A package item represents a collection of items on the path.
	 * 
	 * @author djp
	 * 
	 */
	public interface PackageItem extends Item {
		public PkgID id();

		/**
		 * Open the folder and see what things are inside.
		 */
		public Collection<Item> list() throws IOException;
	}

	/**
	 * A module item represents a single item on the path.
	 * 
	 * @author djp
	 * 
	 */
	public interface ModuleItem extends Item {
		public ModuleID id();
		
		/**
		 * Open the source file for reading.
		 */
		public Module read() throws IOException;		
	}
	
	// =============================================================
	// Body
	// =============================================================
		
	private HashMap<String,ModuleReader> suffixMap = new HashMap<String,ModuleReader>();

	
	protected ModuleReader getModuleReader(String filename) {		
		int idx = filename.lastIndexOf('.');
		if(idx > 0) {
			String suffix = filename.substring(idx+1);
			return suffixMap.get(suffix);			
		}		
		return null;
	}
}
