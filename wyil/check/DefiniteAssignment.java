package wyil.check;

import wyil.lang.*;
import java.util.*;

/**
 * <p>
 * The purpose of this class is to check that all variables are defined before
 * being used. For example:
 * </p>
 * 
 * <pre>
 * int f() {
 * 	int z;
 * 	return z + 1;
 * }
 * </pre>
 * 
 * <p>
 * In the above example, variable z is used in the return statement before it
 * has been defined any value. This is considered a syntax error in whiley.
 * </p>
 * @author djp
 * 
 */
public class DefiniteAssignment implements ModuleCheck {
	public void check(Module module) {
		for(Module.Method method : module.methods()) {
			check(method);
		}
	}
	
	public void check(Module.Method method) {
		for(Module.Case cas : method.cases()) {
			check(cas);
		}
	}
	
	public void check(Module.Case cas) {
		// the undefined set containts the names of all variables and registers
		// which are undefined at any given point.
		HashSet<String> undefined = new HashSet<String>();
	}
}
