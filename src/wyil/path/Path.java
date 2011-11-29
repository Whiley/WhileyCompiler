package wyil.path;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import wyil.lang.ModuleID;
import wyil.lang.PkgID;

public class Path {
	
	public interface Root {		
		/**
		 * Open the folder and see what things are inside.
		 */
		public Collection<Entry> list(PkgID pkg) throws IOException;
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
