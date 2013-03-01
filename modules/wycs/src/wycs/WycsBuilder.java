package wycs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wyautl.core.Automaton;
import static wybs.lang.SyntaxError.*;
import wybs.lang.*;
import wybs.lang.Path.Entry;
import wybs.util.Pair;
import wybs.util.ResolveError;
import wybs.util.Trie;
import wycs.lang.WycsFile;

public class WycsBuilder implements Builder {
	
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
	
	/**
	 * A map of the source files currently being compiled.
	 */
	private final HashMap<Path.ID, Path.Entry<WycsFile>> srcFiles = new HashMap<Path.ID, Path.Entry<WycsFile>>();
	
	private Logger logger;
	
	public WycsBuilder(NameSpace namespace) {
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
	// Build Method
	// ======================================================================
		

	@Override
	public void build(List<Pair<Entry<?>, Entry<?>>> delta) throws Exception {
		// TODO Auto-generated method stub
		
	}	
	
	// ======================================================================
	// Public Accessors
	// ======================================================================
		
	/**
	 * Check whether or not a given Wycs module exists.
	 * 
	 * @param mid
	 *            --- fully qualified name.
	 * @return
	 */
	public boolean exists(Path.ID mid) {
		try {
			return namespace.exists(mid, WycsFile.ContentType);
		} catch(Exception e) {
			return false;
		}
	}

	/**
	 * Get the Wycs module associated with a given module identifier. If the
	 * module does not exist, a resolve error is thrown.
	 * 
	 * @param mid
	 * @return
	 * @throws Exception
	 */
	public WycsFile getModule(Path.ID mid) throws Exception {
		return namespace.get(mid, WycsFile.ContentType).read();
	}
	
	public <T extends WycsFile.Declaration> Pair<NameID, T> resolveAs(
			String name, Class<T> type, WycsFile.Context context)
			throws ResolveError {
		for (WycsFile.Import imp : context.imports()) {
			for (Path.ID id : imports(imp.filter)) {
				try {
					WycsFile wf = getModule(id);
					T d = wf.declaration(name, type);
					if (d != null) {
						return new Pair<NameID, T>(new NameID(id, name), d);
					}
				} catch (ResolveError e) {
					throw e;
				} catch (Exception e) {
					internalFailure(e.getMessage(), context.file().filename(),
							context, e);
				}
			}
		}

		throw new ResolveError("cannot resolve name as function: " + name);
	}
	
	/**
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 * 
	 * @param key
	 *            --- Path name which potentially contains a wildcard.
	 * @return
	 */
	public List<Path.ID> imports(Trie key) throws ResolveError {		
		try {
			ArrayList<Path.ID> matches = importCache.get(key);
			if (matches != null) {
				// cache hit
				return matches;
			} else {
				// cache miss
				matches = new ArrayList<Path.ID>();	
				
				for(Path.Entry<WycsFile> sf : srcFiles.values()) {
					if(key.matches(sf.id())) {						
						matches.add(sf.id());
					}
				}

				if(key.isConcrete()) {
					// A concrete key is one which does not contain a wildcard.
					// Therefore, it corresponds to exactly one possible item.
					// It is helpful, from a performance perspective, to use
					// NameSpace.exists() in such case, as this conveys the fact
					// that we're only interested in a single item.
					if(namespace.exists(key,WycsFile.ContentType)) {
						matches.add(key);
					}
				} else {
					Content.Filter<?> binFilter = Content.filter(key,
							WycsFile.ContentType);
					for (Path.ID mid : namespace.match(binFilter)) {					
						matches.add(mid);
					}
				}
												
				importCache.put(key, matches);
			}
			
			return matches;
		} catch(Exception e) {
			throw new ResolveError(e.getMessage(),e);
		}
	}
}
