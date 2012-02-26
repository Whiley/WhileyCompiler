package wyc.util;

import java.util.*;
import wyc.lang.*;

/**
 * The master root provides a way to combine multiple roots together.
 * 
 * @author David J. Pearce
 * 
 */
public class AbstractNameSpace implements Path.Root {
	
	/**
	 * The roots of named objects in the namespace described by this root. 
	 */	
	private final Path.Root[] roots;
	
	public AbstractNameSpace(Collection<Path.Root> roots) {
		this.roots = new Path.Root[roots.size()];
		int i = 0;
		for(Path.Root r : roots) {
			this.roots[i++] = r;
		}
	}
	
	public boolean exists(Path.ID id, Content.Type<?> ct) throws Exception {
		for(int i=0;i!=roots.length;++i) {
			if(roots[i].exists(id,ct)) {
				return true;
			}
		}
		return false;
	}
	
	public <T> Path.Entry<T> get(Path.ID id, Content.Type<T> ct) throws Exception {
		for(int i=0;i!=roots.length;++i) {
			Path.Entry<T> e = roots[i].get(id,ct);
			if(e != null) {
				return e;
			}
		}
		return null;
	}
	
	public <T> ArrayList<Path.Entry<T>> get(Content.Filter<T> filter) throws Exception {
		ArrayList<Path.Entry<T>> r = new ArrayList<Path.Entry<T>>();
		for(int i=0;i!=roots.length;++i) {
			r.addAll(roots[i].get(filter));
		}
		return r;
	}
	
	public <T> HashSet<Path.ID> match(Content.Filter<T> filter) throws Exception {
		HashSet<Path.ID> r = new HashSet<Path.ID>();
		for(int i=0;i!=roots.length;++i) {
			r.addAll(roots[i].match(filter));
		}
		return r;
	}
}
