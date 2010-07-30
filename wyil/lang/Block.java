package wyil.lang;

import java.util.*;

public final class Block extends ArrayList<Code> {
	public Block() {
		super();
	}
	public Block(Collection<Code> codes) {
		super(codes);
	}
	public Block(Code... codes) {
		super(Arrays.asList(codes));
	}

	/**
	 * Determine the set of all used variables in this block.
	 * 
	 * @param blk
	 * @return
	 */
	public static <T extends Set<String>> T usedVariables(Block blk, T uses) {
		for (Code c : blk) {
			Code.usedVariables(c, uses);
		}
		return uses;
	}
}
