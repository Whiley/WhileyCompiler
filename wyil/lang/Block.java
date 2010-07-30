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
	 * The following method appends a block b1 onto the front of another block
	 * b2; it creates a completely new block and, in the process, it makes sure
	 * all branch targets are updated appropriately.
	 * 
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static Block append(Block b1, Block b2) {
		return null;
	}
	
	/**
	 * Substitute one variable for another in the given block 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static Block substitute(String v1, String v2, Block block) {
		Block newBlock = new Block();
		for(Code c : block) {
			newBlock.add(Code.substitute(v1,v2,c));
		}
		return newBlock;
	}
}
