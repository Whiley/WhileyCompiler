package wyil.transforms;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;

import wybs.lang.Builder;
import wycc.lang.Transform;
import wyil.lang.CodeForest;
import wyil.lang.Code;
import wyil.lang.Codes;
import wyil.lang.Type;
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
public class LoopVariants implements Transform<WyilFile> {
	private String filename;

	/**
	 * Determines whether constant propagation is enabled or not.
	 */
	private boolean enabled = getEnable();

	public LoopVariants(Builder builder) {

	}

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
		if (enabled) {
			filename = module.filename();

			for (WyilFile.Type type : module.types()) {
				infer(type);
			}

			for (WyilFile.FunctionOrMethod method : module
					.functionOrMethods()) {
				infer(method);
			}
		}
	}

	public void infer(WyilFile.Type type) {
		CodeForest forest = type.invariant();
		for(int i=0;i!=forest.numRoots();++i) {
			infer(forest.getRoot(i),forest);
		}
	}

	public void infer(WyilFile.FunctionOrMethod method) {		
		CodeForest forest = method.code();
		for(int i=0;i!=forest.numRoots();++i) {
			infer(forest.getRoot(i),forest);
		}		
	}

	/**
	 * Determine the modified variables for a given block of Wyil bytecodes. In
	 * doing this, infer the modified operands for any loop bytecodes
	 * encountered.
	 *
	 * @param block
	 * @param method
	 * @return
	 */
	protected BitSet infer(int blockID, CodeForest forest) {
		CodeForest.Block block = forest.get(blockID);
		BitSet modified = new BitSet(forest.registers().size());
		int size = block.size();
		for (int i = 0; i < size; ++i) {
			CodeForest.Entry e = block.get(i);
			Code code = e.code();

			if (code instanceof Code.AbstractBytecode) {
				Code.AbstractBytecode aa = (Code.AbstractBytecode) code;
				for (int target : aa.targets()) {
					modified.set(target);
				}
			}
			if (code instanceof Code.AbstractCompoundBytecode) {
				Code.AbstractCompoundBytecode body = (Code.AbstractCompoundBytecode) code;
				BitSet loopModified = infer(body.block(), forest);
				if (code instanceof Codes.Quantify) {
					// Unset the modified status of the index operand, it is
					// already implied that this is modified.
					Codes.Quantify qc = (Codes.Quantify) code;
					loopModified.clear(qc.indexOperand());
					code = Codes.Quantify(qc.startOperand(), qc.endOperand(), qc.indexOperand(), toArray(loopModified),
							qc.block());
					block.set(i, code, e.attributes());
				} else if (code instanceof Codes.Loop) {
					Codes.Loop loop = (Codes.Loop) code;
					code = Codes.Loop(toArray(loopModified), loop.block());
					block.set(i, code, e.attributes());
				}

				modified.or(loopModified);
			}
		}
		return modified;
	}

	protected int[] toArray(BitSet bs) {
		int[] arr = new int[bs.cardinality()];
		for (int i = bs.nextSetBit(0), j = 0; i >= 0; i = bs.nextSetBit(i + 1), ++j) {
			arr[j] = i;
		}
		return arr;
	}
}
