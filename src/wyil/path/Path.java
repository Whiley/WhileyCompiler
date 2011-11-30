package wyil.path;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import wyil.lang.ModuleID;
import wyil.lang.PkgID;

public class Path {
	
	public interface Root {		
		
		/**
		 * Check whether or not a given package is contained.
		 */
		public boolean exists(PkgID pid) throws Exception;
		
		/**
		 * Lookup a given module.
		 * 
		 * @param mid
		 *            --- id of module to lookup.
		 * @return
		 * @throws IOException
		 */
		public Entry lookup(ModuleID mid) throws Exception;
		
		/**
		 * List contents of a given package.
		 * 
		 * @param pid
		 * @return
		 * @throws IOException
		 */
		public Collection<Entry> list(PkgID pid) throws Exception;
	}
	
	/**
	 * A path entry represents an item reachable from root on the WHILEYPATH
	 * which corresponds to a Whiley Module.
	 * 
	 * @author djp
	 * 
	 */
	public interface Entry {
		public ModuleID id();

		/**
		 * Return the suffix of the item in question. This is necessary to
		 * determine how we will process this item.
		 * 
		 * @return
		 */
		public String suffix();
		
		/**
		 * Return a string indicating the location of this entry.  
		 * @return
		 */
		public String location();
		
		/**
		 * 
		 * @return
		 */
		public long lastModified();
		
		/**
		 * Open the source file for reading.
		 */
		public InputStream contents() throws IOException;
	}
}
