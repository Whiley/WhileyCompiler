package wyil;

import java.util.*;
import java.io.*;

import wyil.lang.*;

/**
 * <p>
 * Abstracts the concept of the WHILEYPATH.  
 * </p>
 * @author djp
 * 
 */
public interface WhileyPath {

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
	 * A folder represents a collection of items on the path.
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

	public interface ModuleItem extends Item {
		public ModuleID id();
		
		/**
		 * Open the source file for reading.
		 */
		public Module read() throws IOException;		
	}		
}
