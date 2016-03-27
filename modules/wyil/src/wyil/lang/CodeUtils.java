package wyil.lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import wyil.lang.Codes.Comparator;

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
			if(operands[i] == Codes.NULL_REG) {
				r = r + "_";
			} else {
				r = r + "%" + operands[i];
			}
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
	public static Codes.Comparator invert(Codes.Comparator cop) {
		switch (cop) {
		case EQ:
			return Codes.Comparator.NEQ;
		case NEQ:
			return Codes.Comparator.EQ;
		case LT:
			return Codes.Comparator.GTEQ;
		case LTEQ:
			return Codes.Comparator.GT;
		case GT:
			return Codes.Comparator.LTEQ;
		case GTEQ:
			return Codes.Comparator.LT;
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
				Code code = block.get(j).code();
				if (code instanceof Codes.Label) {
					// Found a label, so register it in the labels map
					Codes.Label label = (Codes.Label) code;
					labels.put(label.label, new CodeForest.Index(i, j));
				}
			}
		}
		return labels;
	}

	/**
	 * Helper function for buildLabelMap
	 * 
	 * @param index
	 *            Current block index being traversed.
	 * @param labels
	 *            Labels map being constructed
	 * @param block
	 *            Root block
	 */
	private static void buildLabelMap(int blockID, Map<String, CodeForest.Index> labels, CodeForest forest) {
		//
		
	}
}
