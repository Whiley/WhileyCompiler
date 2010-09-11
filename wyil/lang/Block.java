package wyil.lang;

import java.util.*;

import wyil.lang.Code.Forall;
import wyil.util.SyntacticElement;

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
	
	public void add(int idx, Code c, Collection<Attribute> attributes) {
		stmts.add(idx,new Stmt(c,attributes));
	}
	
	public void addAll(Collection<Stmt> stmts) {
		for(Stmt s : stmts) {
			add(s.code,s.attributes());
		}
	}
	
	public void addAll(int idx, Collection<Stmt> stmts) {		
		for(Stmt s : stmts) {
			add(idx++,s.code,s.attributes());
		}
	}
	
	public void addAll(Block stmts) {
		for(Stmt s : stmts) {
			add(s.code,s.attributes());
		}
	}
	
	public void addAll(int idx, Block stmts) {
		for(Stmt s : stmts) {
			add(idx++, s.code,s.attributes());
		}
	}
	
	public int size() {
		return stmts.size();
	}
	
	public Stmt get(int index) {
		return stmts.get(index);
	}
	
	public void set(int index, Code code, Attribute... attributes) {
		stmts.set(index,new Stmt(code,attributes));
	}
	
	public void set(int index, Code code, Collection<Attribute> attributes) {
		stmts.set(index, new Stmt(code, attributes));
	}
	
	public void remove(int index) {
		stmts.remove(index);
	}
	
	public Block subblock(int start, int end) {
		return new Block(stmts.subList(start, end));
	}
	
	public Iterator<Stmt> iterator() {
		return stmts.iterator();
	}
	
	public String toString() {
		String r = "[";
		
		boolean firstTime=true;
		for(Stmt s : stmts) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += s.toString();
		}
		
		return r + "]";
	}
	
	/**
	 * Determine the set of all used variables in this block.
	 * 
	 * @param blk
	 * @return
	 */
	public static <T> void match(Block blk, Class<T> match,
			Collection<T> matches) {
		for (Stmt s : blk) {
			Code.match(s.code, match, matches);
		}
	}

	/**
	 * Determine the set of used variable names, which includes registers and
	 * variables.
	 */
	public static HashSet<String> usedVariables(Block blk) {
		HashSet<CExpr.LVar> uses = new HashSet<CExpr.LVar>();
		for(Stmt stmt : blk) {
			Code.match(stmt.code,CExpr.LVar.class,uses);
		}
		HashSet<String> r = new HashSet<String>();
		for(CExpr.LVar v : uses) {
			if(v instanceof CExpr.Variable) {
				r.add(((CExpr.Variable)v).name);
			} else {
				r.add("%" + ((CExpr.Register)v).index);
			}
		}
		return r;
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
	 * @param block --- maybe null, in which case return is null
	 * @return
	 */
	public static Block substitute(HashMap<String, CExpr> binding, Block block) {
		if(block == null) { return null; }
		Block r = new Block();
		for (Stmt s : block) {			
			r.add(Code.substitute(binding, s.code),s.attributes());			
		}
		return r;				
	}

	public static void addCheck(int freeReg, Block block, Block constraint,
			SyntacticElement elem) {
		if (constraint == null) {
			return;
		}
		Attribute.Source attr = elem.attribute(Attribute.Source.class);
		constraint = resource(constraint, attr);
		constraint = relabel(constraint);
		constraint = registerShift(freeReg, constraint);
		String label = freshLabel();
		block.add(new Code.Check(label), attr);
		block.addAll(constraint);
		block.add(new Code.CheckEnd(label), attr);
	}
	
	public static void addCheck(int freeReg, Block block, Block constraint,
			HashMap<String, CExpr> binding, SyntacticElement elem) {
		if(constraint == null) { return; }
		Attribute.Source attr = elem.attribute(Attribute.Source.class);
		constraint = resource(constraint,attr);
		constraint = relabel(constraint);
		constraint = registerShift(freeReg,constraint);
		constraint = substitute(binding,constraint);
		String label = freshLabel();
		block.add(new Code.Check(label),attr);
		block.addAll(constraint);
		block.add(new Code.CheckEnd(label),attr);
	}
	
	/**
	 * The register shift method is responsible for mapping every register with
	 * index i, to be a register with index i + shift. This is used to guarantee
	 * that the registers of blocks inserted into other blocks do not collide.
	 * 
	 * @param shift --- amount to shift
	 * @param block --- maybe null, in which case return is null
	 * @return
	 */
	public static Block registerShift(int shift, Block block) {
		if(block == null) { return null; }
		Block r = new Block();
		for (Stmt s : block) {				
			r.add(Code.registerShift(shift, s.code),s.attributes());
		}
		return r;
	}

	/**
	 * This method updates the source attributes for all statements in a block.
	 * This is typically done in conjunction with a substitution, when we're
	 * inlining constraints from e.g. pre- and post-conditions.
	 * 
	 * @param block
	 * @param nsrc
	 * @return
	 */
	public static Block resource(Block block, Attribute.Source nsrc) {
		if(block == null) {
			return null;
		}
		Block nblock = new Block();
		for(Stmt s : block) {
			List<Attribute> attrs = s.attributes();
			Attribute src = s.attribute(Attribute.Source.class);
			attrs.remove(src);
			attrs.add(nsrc);
			nblock.add(s.code,attrs);
		}
		return nblock;
	}
	
	/**
	 * This method relabels a given block to ensure its labels do not clash with
	 * any generated so far via freshLabel(). This is particularly important
	 * when bringing blocks in from the wild (e.g. as constraints for external
	 * methods), as we have no idea what labels they may have.
	 * 
	 * @param blk --- maybe null, in which case return is null.
	 * @return
	 */
	public static Block relabel(Block blk) {	
		if(blk == null) { return null; }
		HashMap<String, String> nlabels = new HashMap<String, String>();
		return relabelHelper(blk,nlabels);
	}
	
	private static Block relabelHelper(Block blk, HashMap<String,String> nlabels) {
		Block b = new Block();
		for(Stmt s : blk) {
			Code c = s.code;
			if(c instanceof Code.Label) {
				Code.Label l = (Code.Label) c;
				String label = freshLabel();
				nlabels.put(l.label, label);
			} else if(c instanceof Code.Start) {
				Code.Start l = (Code.Start) c;
				String label = freshLabel();
				nlabels.put(l.label, label);
			}
		}
		for (Stmt s : blk) {
			Code c = s.code;
			if (c instanceof Code.Label) {
				Code.Label l = (Code.Label) c;
				String label = nlabels.get(l.label);
				if (label == null) { label = l.label; }
				c = new Code.Label(label);
			} else if (c instanceof Code.Goto) {
				Code.Goto g = (Code.Goto) c;
				String target = nlabels.get(g.target);
				if (target == null) { target = g.target; }
				c = new Code.Goto(target);
			} else if (c instanceof Code.IfGoto) {
				Code.IfGoto g = (Code.IfGoto) c;
				String target = nlabels.get(g.target);
				if (target == null) { target = g.target; }
				c = new Code.IfGoto(g.op, g.lhs, g.rhs, target);
			} else if (c instanceof Code.Induct) {
				Code.Induct i = (Code.Induct) c;
				String label = nlabels.get(i.label);				
				c = new Code.Induct(label, i.variable, i.source);
			} else if (c instanceof Code.InductEnd) {
				Code.InductEnd l = (Code.InductEnd) c;
				String label = nlabels.get(l.target);
				if (label == null) { label = l.target; }
				c = new Code.InductEnd(label);
			} else if (c instanceof Code.Forall) {
				Code.Forall l = (Code.Forall) c;
				String label = nlabels.get(l.label);				
				c = new Code.Forall(label, l.invariant, l.variable, l.source,
						l.modifies);
			} else if (c instanceof Code.ForallEnd) {
				Code.ForallEnd l = (Code.ForallEnd) c;
				String label = nlabels.get(l.target);
				if (label == null) { label = l.target; }
				c = new Code.ForallEnd(label);
			} else if (c instanceof Code.Loop) {
				Code.Loop l = (Code.Loop) c;
				String label = nlabels.get(l.label);				
				c = new Code.Loop(label,l.invariant,l.modifies);
			} else if (c instanceof Code.LoopEnd) {
				Code.LoopEnd l = (Code.LoopEnd) c;
				String label = nlabels.get(l.target);
				if (label == null) { label = l.target; }
				c = new Code.LoopEnd(label);
			} else if (c instanceof Code.Check) {
				Code.Check l = (Code.Check) c;
				String label = nlabels.get(l.label);
				if (label == null) { label = l.label; }
				c = new Code.Check(label);
			} else if (c instanceof Code.CheckEnd) {
				Code.CheckEnd l = (Code.CheckEnd) c;
				String label = nlabels.get(l.target);
				if (label == null) { label = l.target; }
				c = new Code.CheckEnd(label);
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
	
	private static int _idx=0;
	public static String freshLabel() {
		return "blklab" + _idx++;
	}
}
