package wyone.core;

public class Types {
	
	/**
	 * <p>
	 * Return true if <code>t2</code> is a subtype of <code>t1</code>. This
	 * function operates in a seemingly strange way. To perform the subtype
	 * check, it computes the type <code>!t1 && t2</code>. If this reduces to
	 * type <code>void</code>, then we can be certain that <code>t2</code> is
	 * entirely closed within <code>t1</code>.
	 * </p>
	 * 
	 * 
	 * 
	 * @param t1
	 *            --- super-type to test for.
	 * @param t2
	 *            --- sub-type to test for.
	 * @return
	 */
	public static boolean isSubtype(Type t1, Type t2) {		
		Type result = Type.T_AND(Type.T_NOT(t1),t2);
		return result.equals(Type.T_VOID());
	}		
}
