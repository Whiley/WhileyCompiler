package wyil.lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CodeUtils {
	
	/**
	 * Rename every label to a new (fresh) label and, likewise, map all
	 * instructions which use labels accordingly. This is particularly useful
	 * when importing a block from the same source multiple times into a given
	 * block.
	 * 
	 * @param block
	 * @return
	 */
	public static CodeBlock relabel(CodeBlock block) {
		HashMap<String,String> labels = new HashMap<String,String>();
		
		for (CodeBlock.Entry s : block) {
			if (s.code instanceof Codes.Label) {
				Codes.Label l = (Codes.Label) s.code;
				labels.put(l.label, freshLabel());
			}
		}
		
		CodeBlock nBlock = new CodeBlock(block.numInputs());
		
		// Finally, apply the binding and relabel any labels as well.
		for(CodeBlock.Entry s : block) {
			Code ncode = s.code.relabel(labels);
			nBlock.add(ncode,s.attributes());
		}
		
		return nBlock;
	}
	
	
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
}
