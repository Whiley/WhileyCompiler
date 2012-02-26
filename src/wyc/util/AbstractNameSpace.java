package wyc.util;

import java.util.*;
import wyc.lang.*;

/**
 * The master root provides a way to combine multiple roots together.
 * 
 * @author David J. Pearce
 * 
 */
public abstract class AbstractNameSpace implements NameSpace {
	
	/**
	 * The roots of named objects in the namespace described by this root. 
	 */	
	protected final ArrayList<Path.Root> roots;
	
	public AbstractNameSpace(Collection<Path.Root> roots) {
		this.roots = new ArrayList<Path.Root>(roots);
	}
	
	// ======================================================================
	// Accessors
	// ======================================================================		
	
	public boolean exists(Path.ID id, Content.Type<?> ct) throws Exception {
		for(int i=0;i!=roots.size();++i) {
			if(roots.get(i).exists(id, ct)) {
				return true;
			}
		}
		return false;
	}
	
	public <T> Path.Entry<T> get(Path.ID id, Content.Type<T> ct) throws Exception {
		for(int i=0;i!=roots.size();++i) {
			Path.Entry<T> e = roots.get(i).get(id, ct);
			if(e != null) {
				return e;
			}			
		}
		throw new ResolveError("unable to locate " + id);
	}
	
	public <T> ArrayList<Path.Entry<T>> get(Path.Filter<T> filter) throws Exception {
		ArrayList<Path.Entry<T>> r = new ArrayList<Path.Entry<T>>();
		for(int i=0;i!=roots.size();++i) {
			r.addAll(roots.get(i).get(filter));
		}
		return r;
	}
	
	public <T> HashSet<Path.ID> match(Path.Filter<T> filter) throws Exception {
		HashSet<Path.ID> r = new HashSet<Path.ID>();
		for(int i=0;i!=roots.size();++i) {
			r.addAll(roots.get(i).match(filter));
		}
		return r;
	}
	
	// ======================================================================
	// Mutators
	// ======================================================================		

	public abstract <T> Path.Entry<T> create(Path.ID id, Content.Type<T> ct) throws Exception;
	
	public void flush() throws Exception {
		for(int i=0;i!=roots.size();++i) {
			roots.get(i).flush();
		}
	}
	
	public void refresh() throws Exception {
		for(int i=0;i!=roots.size();++i) {
			roots.get(i).refresh();
		}
	}
	
	// ======================================================================
	// Other
	// ======================================================================		

	/**
	 * Create a Path ID from a string, where '/' separates path components.
	 * 
	 * @param s
	 * @return
	 */
	public abstract Path.ID create(String s);
}
