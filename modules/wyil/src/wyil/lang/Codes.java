package wyil.lang;

import java.util.HashMap;

public class Codes {
	
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
			if (s.code instanceof Code.Label) {
				Code.Label l = (Code.Label) s.code;
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
	
}
