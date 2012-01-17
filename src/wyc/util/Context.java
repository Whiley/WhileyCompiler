package wyc.util;

import java.util.*;
import wyc.lang.WhileyFile;

/**
 * A context captures all the necessary information to look up a given name in a
 * given file, and to report an error on some AST element in that file.
 * 
 * @author David J. Pearce
 * 
 */
public class Context {
	public final WhileyFile file;
	public final ArrayList<WhileyFile.Import> imports;

	public Context(WhileyFile file, List<WhileyFile.Import> imports) {
		this.file = file;
		this.imports = new ArrayList<WhileyFile.Import>(imports);
	}
}
