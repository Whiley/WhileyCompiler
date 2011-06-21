package wyjc.runtime;

/**
 * <code>Any</code> is the root of the data heirarchy used in Whiley. All data
 * values used in Whiley are instances of <code>Any</code>. This includes sets,
 * lists, dictionaries, records, tuples and numbers.
 * 
 * @author djp
 * 
 */
public abstract class Any {

	/**
	 * Increment the reference count for this object. In some cases, this may
	 * have no effect. In other cases, the current reference count will be
	 * maintained an in-place updates can only occur when the reference count is
	 * one.
	 */
	public abstract void incCount();

	/**
	 * Decrement the reference count for this object. In some cases, this may
	 * have no effect. In other cases, the current reference count will be
	 * maintained an in-place updates can only occur when the reference count is
	 * one.
	 */
	public abstract void decCount();
	
	/**
	 * The <code>instanceOf</code> method implements a runtime type test. 
	 */
	public abstract boolean instanceOf(Type t);
}
