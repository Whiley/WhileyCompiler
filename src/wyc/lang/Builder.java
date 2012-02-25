package wyc.lang;

import java.util.*;

import wyc.io.ContentType;
import wyc.io.Path;
import wyil.util.Pair;

/**
 * <p>
 * A builder is responsible for transforming files from one content type to
 * another. Typically this revolves around compiling the source file into some
 * kind of binary, although other kinds of transformations are possible.
 * </p>
 * 
 * <p>
 * A given builder may support multiple transformations and the builder must
 * declare each of these.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public interface Builder {
	
	/**
	 * <p>Return the list of support transformations. Most builders will probably
	 * only support a single transformation (e.g. whiley => wyil). However, in
	 * some special cases, multiple transformations may be desired.</p>
	 * 
	 * @return
	 */
	public Set<Pair<ContentType,ContentType>> transforms();
	
	/**
	 * Build a given set of source files to produce a given set of target files.
	 * The location of the target files is determined by the build map.
	 * 
	 * @param map
	 *            --- a mapping from source files to target files.
	 * @param delta
	 *            --- the set of files to be built.
	 */
	public void build(Map<Path.Entry<?>,Path.Entry<?>> map, List<Path.Entry<?>> delta);
}
