package wyil.lang;

/**
 * <p>
 * An Attribute represents a chunk of meta-data associated with one or more
 * aspects of a WyIL file. Attributes are non-essential. That is, the generated
 * WyIL bytecode should be executable without them. However, attributes provide
 * a way to keep otherwise useful information. For example, line number
 * information from the original source file to aid with error reporting, or the
 * original variable name that a given WyIL register corresponds to.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface Attribute {

	/**
	 * <p>
	 * An attribute map is an attribute which specifically associates meta-data
	 * with individual bytecodes, as opposed to other attributes which
	 * associated data with larger constructs (e.g. methods, functions, types,
	 * etc).
	 * </p>
	 *
	 * <p>
	 * The primary reason for distinguishing between attributes and attribute
	 * maps is that, in the latter, we want to enable the safe modification of
	 * bytecodes (e.g. inserting, deleting, replacing, etc). When such
	 * operations occur, an attribute map must be informed to ensure that the
	 * mapping is correctly preserved.
	 * </p>
	 *
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Map<T extends Attribute> extends Attribute {
		/**
		 * Returns the type of attribute that this attribute map handles.
		 *
		 * @return
		 */
		Class<? extends Attribute> type();

		/**
		 * Get the meta-data associated with a given bytecode location. Here, a
		 * location is a n-dimensional numeric identifier to capture bytecodes
		 * which may be nested inside other bytecodes.
		 *
		 * @param location
		 *            The location to be updated.
		 * @return The data associated with the given location, or null if no
		 *         such data exists.
		 */
		T get(CodeBlock.Index location);

		/**
		 * Assign or update the meta-data associated with a given bytecode
		 * location. Here, a location is a n-dimensional numeric identifier to
		 * capture bytecodes which may be nested inside other bytecodes.
		 *
		 * @param location
		 *            The location to be updated
		 * @param data
		 *            The data to assign to the given location
		 */
		void put(CodeBlock.Index location, T data);
		
		/**
		 * Insert meta-data associated with a given bytecode a given location.
		 * Here, a location is a n-dimensional numeric identifier to capture
		 * bytecodes which may be nested inside other bytecodes. Note that this
		 * will "shift down" all identifies which logically follow the given
		 * identify in its block, and those contained.
		 * 
		 * @param location
		 *            The location to be insert
		 * @param data
		 *            The data to assign to the given location
		 */
		void insert(CodeBlock.Index location, T data);
	}
}
