package wyil.io;

import java.io.IOException;

import wyil.lang.Module;
import wyil.lang.ModuleID;

/**
 * A WModule represents an item on the WHILEYPATH which corresponds to a Whiley
 * Module.
 * 
 * @author djp
 * 
 */
public interface WModule extends WItem {
	public ModuleID id();

	/**
	 * Open the source file for reading.
	 */
	public Module read() throws IOException;
}
