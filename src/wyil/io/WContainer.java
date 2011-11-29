package wyil.io;

import java.io.IOException;
import java.util.Collection;

import wyil.lang.PkgID;

public interface WContainer extends WItem {
	public PkgID id();

	/**
	 * Open the folder and see what things are inside.
	 */
	public Collection<WItem> list() throws IOException;
}
