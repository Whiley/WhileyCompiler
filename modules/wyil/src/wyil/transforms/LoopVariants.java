package wyil.transforms;

import wyil.Transform;
import wyil.lang.Block;
import wyil.lang.WyilFile;

/**
 * <p>
 * Responsible for determining what the modified variables (a.k.a variants) in a
 * loop are. It is a requirement for correctly formed Wyil bytecodes that any
 * variable which may be assigned within a loop is explicitly declared. This is
 * important, for example, when generating verification conditions during the
 * verification process.
 * </p>
 * 
 * <p>
 * For example, consider this Whiley program:
 * </p>
 * 
 * <pre>
 * int sum(int stride, [int] list):
 *     r = 0
 *     i = 0
 *     while i < |list|:
 *         r = r + list[i]
 *         i = i + stride
 *     return r *
 * </pre>
 * <p>
 * In the above program, the variables <code>r</code> and <code>i</code> are
 * <i>loop variants</i> because they are assigned within the body of the loop.
 * Note, however, that <code>stride</code> is not a loop variant because it
 * remains constant (i.e. invariant) for the duration of the loop.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class LoopVariants implements Transform {
	private String filename;
	
	/**
	 * Determines whether constant propagation is enabled or not.
	 */
	private boolean enabled = getEnable();
	

	public static String describeEnable() {
		return "Enable/disable loop variant inference";
	}
	
	public static boolean getEnable() {
		return true; // default value
	}
	
	public void setEnable(boolean flag) {
		this.enabled = flag;
	}
		
	public void apply(WyilFile module) {
		filename = module.filename();
		
		for(WyilFile.MethodDeclaration method : module.methods()) {
			check(method);
		}
	}
	
	public void check(WyilFile.MethodDeclaration method) {				
		for (WyilFile.Case c : method.cases()) {
			check(c.body(), method);
		}		
	}
	
	protected void check(Block block, WyilFile.MethodDeclaration method) {
		
	}
}
