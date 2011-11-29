package wyil.io;

import java.io.IOException;

/**
 * An item represents anything which may be on the WHILEYPATH.
 * 
 * @author djp
 * 
 */
public interface WItem {
	/**
	 * Refresh after something changed. This gives an opportunity for
	 * directories on the path to reload themselves, etc.
	 */
	public void refresh() throws IOException;
}
