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

	/**
	 * Substitute all occurrences of variable from with variable to. Care must
	 * be taken when using this method, to ensure misc temporary variables are
	 * not altered as well.
	 * 
	 * @param from
	 * @param to
	 * @param block
	 * @return
	 */
	public static Block substitute(String from, String to, Block block) {
		Block r = new Block();
		for(Code c : block) {
			r.add(Code.substitute(from,to,c));
		}
		return r;
	}
}
