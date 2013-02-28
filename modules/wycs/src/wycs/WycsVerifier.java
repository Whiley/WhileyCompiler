package wycs;

import java.util.ArrayList;
import java.util.HashMap;

import wybs.lang.Logger;
import wybs.lang.NameSpace;
import wybs.lang.Path;
import wybs.util.Trie;
import wycs.lang.WycsFile;

public class WycsVerifier {
	
	/**
	 * The master namespace for identifying all resources available to the
	 * builder. This includes all modules declared in the project being verified
	 * and/or defined in external resources (e.g. jar files).
	 */
	private final NameSpace namespace;		

	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import wycs.lang.*</code>
	 * corresponds to the triple <code>("wycs.lang",*,null)</code>.
	 */
	private final HashMap<Trie,ArrayList<Path.ID>> importCache = new HashMap();	
	
	private Logger logger;
	
	public WycsVerifier(NameSpace namespace) {
		this.logger = Logger.NULL;
		this.namespace = namespace;
	}

	public NameSpace namespace() {
		return namespace;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	// ======================================================================
	// Public Accessors
	// ======================================================================
		
	public boolean exists(Path.ID id) {
		try {
			return namespace.exists(id, WycsFile.ContentType);
		} catch(Exception e) {
			return false;
		}
	}

	/**
	 * Get the (compiled) module associated with a given module identifier. If
	 * the module does not exist, a resolve error is thrown.
	 * 
	 * @param mid
	 * @return
	 * @throws Exception
	 */
	public WycsFile getModule(Path.ID mid) throws Exception {
		return namespace.get(mid, WycsFile.ContentType).read();
	}
}
