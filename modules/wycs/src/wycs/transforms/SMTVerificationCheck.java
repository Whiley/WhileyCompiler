package wycs.transforms;

import wybs.lang.Transform;
import wycs.core.WycsFile;

public class SMTVerificationCheck implements Transform<WycsFile> {
	/**
	 * Determines whether this transform is enabled or not.
	 */
	private boolean enabled = getEnable();

	/**
	 * Determines whether debugging is enabled or not
	 */
	private boolean debug = getDebug();
	
	// ======================================================================
	// Constructor(s)
	// ======================================================================


	// ======================================================================
	// Configuration Methods
	// ======================================================================

	public static String describeEnable() {
		return "Enable/disable verification";
	}

	public static boolean getEnable() {
		return false; // default value
	}

	public void setEnable(boolean flag) {
		this.enabled = flag;
	}

	public static String describeDebug() {
		return "Enable/disable debugging information";
	}

	public static boolean getDebug() {
		return false; // default value
	}

	public void setDebug(boolean flag) {
		this.debug = flag;
	}
	
	// ======================================================================
	// Apply Method
	// ======================================================================

	@Override
	public void apply(WycsFile file) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
