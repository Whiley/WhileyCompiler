package wyc.util.path;

import java.io.*;
import java.util.HashMap;

/**
 * A content type provides an abstract mechanism for reading and writing file in
 * a given format. Whiley source files (*.whiley) are one example, whilst JVM
 * class files (*.class) are another.
 * 
 * @author David J. Pearce
 * 
 * @param <T>
 */
public interface ContentType<T> {
	
	/**
	 * Check whether a given suffix (e.g. "txt") matches this content type, or
	 * not.
	 * 
	 * @param suffix
	 * @return
	 */
	public boolean matches(String suffix);
	
	/**
	 * Read the contents of source matching this content type.
	 * 
	 * @param input
	 *            --- input stream representing in the format described by this
	 *            content type.
	 * @return
	 */
	public T read(Path.Entry entry) throws Exception;
	
	/**
	 * Convert a given value into an appropriate byte stream and write it to a
	 * given output.
	 * 
	 * @param output
	 *            --- stream which this value is to be written to.
	 * @param value
	 *            --- value to be converted into bytes.
	 */
	public void write(Path.Entry entry, T value) throws Exception;
	
	public interface Registry {
		/**
		 * Get the content type associated with a given suffix.
		 * 
		 * @param suffix
		 * @return
		 */
		public ContentType<?> get(String suffix);
	}
	
	public static class RegistryImpl implements Registry {
		private final HashMap<String,ContentType> types = new HashMap<String,ContentType>();
				
		public void register(String suffix, ContentType<?> contentType) {
			types.put(suffix,contentType);
		}
		
		public ContentType<?> get(String suffix) {
			return types.get(suffix);
		}
	}		
}
