package wycc.lang;

import wyfs.lang.Path;

public interface CompilationUnit {
	/**
	 * Get the path entry with which this compilation unit is associated. This
	 * may be a physical file on disk, a binary image stored in a jar file or an
	 * entry in a virtual file system.
	 * 
	 * @return
	 */
	public Path.Entry<? extends CompilationUnit> getEntry();
}
