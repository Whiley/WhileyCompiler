package wyil.transforms;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;

import wybs.lang.Builder;
import wycc.lang.Transform;
import wyil.lang.CodeBlock;
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
		CodeBlock invariant = type.invariant();
		if (invariant != null) {
			infer(invariant);
		}
	}

	public void infer(WyilFile.FunctionOrMethod method) {		
		CodeBlock body = method.body();
		if(body != null) {
			infer(body);
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
	protected BitSet infer(CodeBlock block) {
		BitSet modified = new BitSet(block.numSlots());
		int size = block.size();
		for(int i=0;i<size;++i) {
			Code code = block.get(i);

			if (code instanceof Code.AbstractAssignable) {
				Code.AbstractAssignable aa = (Code.AbstractAssignable) code;
				if (aa.target() != Codes.NULL_REG) {
					modified.set(aa.target());
				}
			}
			if (code instanceof Code.Compound) {
				Code.Compound body = (Code.Compound) code;
				BitSet loopModified = infer(body);
				if (code instanceof Codes.ForAll) {
					// Unset the modified status of the index operand, it is
					// already implied that this is modified.
					Codes.ForAll fall = (Codes.ForAll) code;
					loopModified.clear(fall.indexOperand);
					if(code instanceof Codes.Quantify) {
						code = Codes.Quantify(fall.type, fall.sourceOperand,
								fall.indexOperand, toArray(loopModified),
								fall.bytecodes());
					} else {
						code = Codes.ForAll(fall.type, fall.sourceOperand,
								fall.indexOperand, toArray(loopModified),
								fall.bytecodes());
					}
					block.set(i, code);
				} else if (code instanceof Codes.Loop) {
					Codes.Loop loop = (Codes.Loop) code;
					code = Codes.Loop(toArray(loopModified), loop.bytecodes());
					block.set(i, code);
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
