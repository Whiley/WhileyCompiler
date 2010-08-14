package wyil.lang;

import java.util.*;

public final class Block implements Iterable<Code> {
	private final ArrayList<Code> codes;	
	
	public Block() {
		this.codes = new ArrayList<Code>();
	}
	public Block(Collection<Code> codes) {
		this.codes = new ArrayList<Code>(codes);
	}
	public Block(Code... codes) {
		this.codes = new ArrayList<Code>();
		for(Code c : codes) {
			this.codes.add(c);
		}
	}

	public Iterator<Code> iterator() {
		return codes.iterator();
	}
	
	public void add(Code c) {
		codes.add(c);
	}
	
	public void addAll(Block blk) {
		this.codes.addAll(blk.codes);
	}
	
	public void add(int idx, Code c) {
		this.codes.add(idx,c);
	}
	
	public void remove(int idx) {
		this.codes.remove(idx);
	}
	
	public Code code(int idx) {
		return codes.get(idx);
	}
	
	public int size() {
		return this.codes.size();
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
	public static Block substitute(String from, CExpr to, Block block) {
		HashMap<String,CExpr> binding = new HashMap<String,CExpr>();
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
	public static Block substitute(HashMap<String, CExpr> binding, Block block) {
		Block r = new Block();
		for (Code c : block) {
			r.add(Code.substitute(binding, c));
		}
		return r;
	}

	/**
	 * The register shift method is responsible for mapping every register with
	 * index i, to be a register with index i + shift. This is used to guarantee
	 * that the registers of blocks inserted into other blocks do not collide.
	 * 
	 * @param shift --- amount to shift
	 * @param block
	 * @return
	 */
	public static Block registerShift(int shift, Block block) {
		Block r = new Block();
		for (Code c : block) {
			r.add(Code.registerShift(shift, c));
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
		HashMap<String, String> nlabels = new HashMap<String, String>();
		return relabelHelper(blk,nlabels);
	}
	
	private static Block relabelHelper(Block blk, HashMap<String,String> nlabels) {
		Block b = new Block();
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
			} else if(c instanceof Code.Forall) {
				Code.Forall fa = (Code.Forall) c; 
				c = new Code.Forall(fa.sources,relabelHelper(fa.body,nlabels));
			}
			b.add(c);
		}				
		
		return b;
	}

	/**
	 * This method is used to chain blocks together. More specifically, it
	 * identifies fail points within a block and redirects them a given target.
	 * 
	 * @param target
	 * @param block
	 * @return
	 */
	public static Block chain(String target, Block block) {
		Block nblock = new Block();
		for(Code c : block) {
			if(c instanceof Code.Fail) {
				c = new Code.Goto(target);
			}
			nblock.add(c);			
		}
		return nblock;
	}
	
	private static int idx=0;
	public static String freshLabel() {
		return "blklab" + idx++;
	}
}
