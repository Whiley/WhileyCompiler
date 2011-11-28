package wyil;

import java.io.*;
import wyil.lang.*;

/**
 * <p>
 * A module reader is responsible for physically reading information about a
 * given module. Distincts module readers will be needed for different
 * underlying implementation of Whiley. For example, on the JVM, we might want
 * an instance which reads module information directly from a JVM classfile.
 * </p>
 * 
 * @author djp
 * 
 */
public interface ModuleReader {
	
	/**
	 * Read a given given module from an input stream.
	 * 
	 * @param module
	 *            --- the identifier of the module being read.
	 * @param input
	 *            --- an input stream containing the contents of the module to
	 *            be read.
	 * @return
	 * @throws IOException
	 */
	public Module read(ModuleID module, InputStream input) throws IOException;
}
