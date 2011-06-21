package wyjc.runtime;

/**
 * <code>Any</code> is the root of the data hierarchy used in Whiley. All data
 * values used in Whiley are instances of <code>Any</code>. This includes sets,
 * lists, dictionaries, records, tuples and numbers.
 * 
 * @author djp
 * 
 */
public interface Any {

	/**
	 * Increment the reference count for this object. In some cases, this may
	 * have no effect. In other cases, the current reference count will be
	 * maintained an in-place updates can only occur when the reference count is
	 * one.
	 */
	public Any inc();

	/**
	 * Decrement the reference count for this object. In some cases, this may
	 * have no effect. In other cases, the current reference count will be
	 * maintained an in-place updates can only occur when the reference count is
	 * one.
	 */
	public void dec();
	
	/**
	 * The <code>instanceOf</code> method implements a runtime type test. 
	 */
	public boolean instanceOf(Type t);

	/**
	 * The <code>coerce</code> method forces this object to conform to a given
	 * type.
	 */
	public Any coerce(Type t);	
}
