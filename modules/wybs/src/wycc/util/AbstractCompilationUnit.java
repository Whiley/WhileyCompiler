package wycc.util;

import wycc.lang.CompilationUnit;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;

public class AbstractCompilationUnit<T extends CompilationUnit> implements CompilationUnit {
	protected final Path.Entry<T> entry;
	
	public AbstractCompilationUnit(Path.Entry<T> entry) {
		this.entry = entry;
	}

	@Override
	public Entry<T> getEntry() {
		return entry;
	}
}
