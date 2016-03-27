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
	
}
