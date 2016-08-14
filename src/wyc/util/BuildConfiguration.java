package wyc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyfs.lang.Content;
import wyfs.lang.Path;

/**
 * The build configuration encapsulates those aspects of the build which are
 * configurable.
 * 
 * @author David J. Pearce
 *
 */
public class BuildConfiguration {

	/**
	 * Map content types to the roots in which they should be stored. For
	 * example, we might map "whiley" source files to the "src" directory, and
	 * "wyil" binary files to the "bin" directory.
	 */
	private final Map<Content.Type<?>, Path.Root> folders = new HashMap<>();

	/**
	 * The path identifies additional items (i.e. libraries or directories)
	 * which the compiler uses to resolve symbols (e.g. module names, functions,
	 * etc).
	 */
	private final ArrayList<Path.Root> path = new ArrayList<Path.Root>();
	
	/**
	 * A mapping from attribute names to values
	 */
	private final HashMap<String,Object> attributes = new HashMap<>();
	
	/**
	 * Set the folder associated with a given content type. This identifies
	 * where files of that type are stored.
	 * 
	 * @param contentType
	 * @param folder
	 */
	public void setFolder(Content.Type<?> contentType, Path.Root folder) {
		this.folders.put(contentType, folder);
	}
	
	/**
	 * Get the root associated with a given content type.
	 * 
	 * @param contentType
	 * @return
	 */
	public Path.Root getFolder(Content.Type<?> contentType) {
		return folders.get(contentType);
	}
	
	/**
	 * Get all folders associated with this build configuration.
	 * 
	 * @return
	 */
	public Collection<Path.Root> getFolders() {
		return folders.values();
	}
	
	/**
	 * Get the path elements identified in this configuration.
	 * 
	 * @return
	 */
	public List<Path.Root> getPath() {
		return path;
	}
	
	/**
	 * Get the value of a given attribute.
	 * 
	 * @param name
	 *            The name of the attribute in question.
	 * @param type
	 *            The expected type of the value. This is just a convenience to
	 *            reduce casts.
	 * @return
	 */
	public <T> T getAttribute(String name, Class<T> type) {
		// FIXME: should handle default values?
		return (T) attributes.get(name);
	}
	
	/**
	 * Configure a specific attribute associated with this build configuration.
	 * 
	 * @param name
	 *            The name of the attribute in question.
	 * @param value
	 *            The value to set this attribute. This will be checked against
	 *            the schema to ensure it is of the appropriate type.
	 */
	public void setAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}
}
