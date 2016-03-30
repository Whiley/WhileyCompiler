package wyil.lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import wyil.lang.Bytecode.Comparator;

public class CodeUtils {

	private static int _idx=0;
	public static String freshLabel() {
		return "blklab" + _idx++;
	}

	public static String arrayToString(int... operands) {
		String r = "(";
		for (int i = 0; i != operands.length; ++i) {
			if (i != 0) {
				r = r + ", ";
			}
			r = r + "%" + operands[i];			
		}
		return r + ")";
	}

	public static int[] toIntArray(Collection<Integer> operands) {
		int[] ops = new int[operands.size()];
		int i = 0;
		for (Integer o : operands) {
			ops[i++] = o;
		}
		return ops;
	}

	public static int[] remapOperands(Map<Integer, Integer> binding, int[] operands) {
		int[] nOperands = operands;
		for (int i = 0; i != nOperands.length; ++i) {
			int o = operands[i];
			Integer nOperand = binding.get(o);
			if (nOperand != null) {
				if (nOperands == operands) {
					nOperands = Arrays.copyOf(operands, operands.length);
				}
				nOperands[i] = nOperand;
			}
		}
		return nOperands;
	}

	/**
	 * Determine the inverse comparator, or null if no inverse exists.
	 *
	 * @param cop
	 * @return
	 */
	public static Bytecode.Comparator invert(Bytecode.Comparator cop) {
		switch (cop) {
		case EQ:
			return Bytecode.Comparator.NEQ;
		case NEQ:
			return Bytecode.Comparator.EQ;
		case LT:
			return Bytecode.Comparator.GTEQ;
		case LTEQ:
			return Bytecode.Comparator.GT;
		case GT:
			return Bytecode.Comparator.LTEQ;
		case GTEQ:
			return Bytecode.Comparator.LT;
		}
		return null;
	}
	
	/**
	 * Construct a mapping from labels to their block indices within a root
	 * block. This is useful so they can easily be resolved during the
	 * subsequent traversal of the block.
	 * 
	 * @param block
	 * @return
	 */
	public static Map<String, CodeForest.Index> buildLabelMap(CodeForest forest) {
		HashMap<String, CodeForest.Index> labels = new HashMap<String, CodeForest.Index>();
		for (int i = 0; i != forest.numBlocks(); ++i) {
			CodeForest.Block block = forest.get(i);
			for (int j = 0; j != block.size(); ++j) {
				Bytecode code = block.get(j).code();
				if (code instanceof Bytecode.Label) {
					// Found a label, so register it in the labels map
					Bytecode.Label label = (Bytecode.Label) code;
					labels.put(label.label, new CodeForest.Index(i, j));
				}
			}
		}
		return labels;
	}
}
