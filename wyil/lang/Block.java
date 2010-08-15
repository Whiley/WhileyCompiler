package wyil.lang;

import java.util.*;

import wyil.lang.Code.Forall;

public final class Block implements Iterable<Stmt> {
	private final ArrayList<Stmt> stmts;	
	
	public Block() {
		this.stmts = new ArrayList<Stmt>();
	}
	
	public Block(Collection<Stmt> stmts) {
		this.stmts = new ArrayList<Stmt>();
		for(Stmt s : stmts) {
			add(s.code,s.attributes());
		}
	}
	
	public void add(Code c, Attribute... attributes) {
		stmts.add(new Stmt(c,attributes));
	}
	
	public void add(Code c, Collection<Attribute> attributes) {
		stmts.add(new Stmt(c,attributes));		
	}
	
	public void add(int idx, Code c, Attribute... attributes) {
		stmts.add(idx,new Stmt(c,attributes));
	}
	
	public void addAll(Collection<Stmt> stmts) {
		for(Stmt s : stmts) {
			add(s.code,s.attributes());
		}
	}
	
	public void addAll(Block stmts) {
		for(Stmt s : stmts) {
			add(s.code,s.attributes());
		}
	}
	
	public int size() {
		return stmts.size();
	}
	
	public Stmt get(int index) {
		return stmts.get(index);
	}
	
	public void remove(int index) {
		stmts.remove(index);
	}
	
	public Iterator<Stmt> iterator() {
		return stmts.iterator();
	}
	
	/**
	 * Determine the set of all used variables in this block.
	 * 
	 * @param blk
	 * @return
	 */
	public static <T extends Set<String>> T usedVariables(Block blk, T uses) {
		for (Stmt s : blk) {
			Code.usedVariables(s.code, uses);
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
		for (Stmt s : block) {			
			r.add(Code.substitute(binding, s.code),s.attributes());
			
			/*
			// First, we need to to check for captured variable clashes
			HashMap<String,CExpr> rebinding = new HashMap<String,CExpr>();
			
			for (Map.Entry<String, CExpr> src : a.sources.entrySet()) {
				if (binding.containsKey(src.getKey())) {
					// this indicates a clash
					Type t = src.getValue().type();
					if (t instanceof Type.Set) {
						t = ((Type.Set) t).element;
					} else if (t instanceof Type.List) {
						t = ((Type.List) t).element;
					}
					rebinding.put(src.getKey(), CExpr.VAR(t, newCaptureName(src
							.getKey(), binding.keySet())));
				}
			}
			
			if(rebinding.size() != 0) {
				// Ok, we encountered a clash between variables being
				// substituted and captured variables. Therefore, rename the
				// affected captured variables, before proceeding as normal.				
				HashMap<String,CExpr> srcs = new HashMap<String,CExpr>();
				for(Map.Entry<String,CExpr> src : a.sources.entrySet()) {
					CExpr v = rebinding.get(src.getKey());
					if(v != null) {						
						srcs.put(((CExpr.Variable)v).name, src.getValue());
					} else {
						srcs.put(src.getKey(), src.getValue());
					}
				}	
				a = new Forall(srcs);
			} 
			*/
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
		for (Stmt s : block) {				
			r.add(Code.registerShift(shift, s.code),s.attributes());
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
		for (Stmt s : blk) {
			Code c = s.code;
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
			b.add(c,s.attributes());
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
		for(Stmt s : block) {
			Code c = s.code;
			if(c instanceof Code.Fail) {
				c = new Code.Goto(target);
			}
			nblock.add(c,s.attributes());			
		}
		return nblock;
	}
	
	private static int idx=0;
	public static String freshLabel() {
		return "blklab" + idx++;
	}
}
