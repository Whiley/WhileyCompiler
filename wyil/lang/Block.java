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
	public static Block substitute(String from, RVal to, Block block) {
		HashMap<String,RVal> binding = new HashMap<String,RVal>();
		binding.put(from,to);
		return substitute(binding,block);
	}

	/**
	 * Substitute all occurrences of a given set of variables for another, as
	 * defined by the binding. Care must be taken when using this method, to
	 * ensure misc temporary variables are not altered as well.
	 * 
	 * @param from
	 * @param to
	 * @param block
	 * @return
	 */
	public static Block substitute(HashMap<String, RVal> binding, Block block) {
		Block r = new Block();
		for (Code c : block) {
			r.add(Code.substitute(binding, c));
		}
		return r;
	}

	/**
	 * This method relabels a given block to ensure its labels do not clash with
	 * any generated so far via freshLabel(). This is particularly important
	 * when bringing blocks in from the wild (e.g. as constraints for external
	 * methods), as we have no idea what labels they may have.
	 * 
	 * @param blk
	 * @return
	 */
	public static Block relabel(Block blk) {
		Block b = new Block();
		HashMap<String, String> nlabels = new HashMap<String, String>();
		for (Code c : blk) {			
			if (c instanceof Code.Label) {
				Code.Label l = (Code.Label) c;
				String label = nlabels.get(l.label);
				if (label == null) {
					label = freshLabel();
					nlabels.put(l.label, label);
				}
				c = new Code.Label(label);
			} else if (c instanceof Code.Goto) {
				Code.Goto g = (Code.Goto) c;
				String target = nlabels.get(g.target);
				if (target == null) {
					target = freshLabel();
					nlabels.put(g.target, target);
				}
				c = new Code.Goto(target);
			} else if (c instanceof Code.IfGoto) {
				Code.IfGoto g = (Code.IfGoto) c;
				String target = nlabels.get(g.target);
				if (target == null) {
					target = freshLabel();
					nlabels.put(g.target, target);
				}
				c = new Code.IfGoto(g.type, g.op, g.lhs, g.rhs, target);
			}			
			b.add(c);
		}
		return b;
	}
	
	private static int idx=0;
	public static String freshLabel() {
		return "label" + idx++;
	}
}
