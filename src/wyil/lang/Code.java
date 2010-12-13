// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.lang;

import java.util.*;

import wyil.lang.CExpr.LVal;
import wyjvm.lang.Bytecode;

public abstract class Code {

	// ==========================================
	// =============== Methods ==================
	// ==========================================
	
	public static final int INTERNAL_TYPES = 1;
	public static final int SHORT_TYPES = 2;

	/**
	 * The following method turns a Code into a string. There are several flags
	 * that can be provided:
	 * <ul>
	 * <li><b>INTERNAL_TYPES</b>: include internal type information. That is,
	 * the determined type of each expression is reported as well.</li>
	 * <li><b>SHORT_TYPES</b>: display only the short form of a type. In the
	 * case of recursive or named types, only the name is returned</li>
	 * </ul>
	 * 
	 * @param code
	 * @param flags
	 * @return
	 */
	public static String toString(Code code, int flags) {
		if(code instanceof Assign) {
			Assign a = (Assign) code;
			if(a.lhs != null) {
				return CExpr.toString(a.lhs,flags) + " = " + CExpr.toString(a.rhs,flags);
			} else {
				return CExpr.toString(a.rhs,flags);
			}			
		} else if(code instanceof Debug) {
			Debug a = (Debug) code;						
			return "debug " + CExpr.toString(a.rhs,flags);
		} else if(code instanceof Fail) {
			Fail a = (Fail) code;						
			return "fail " + a.msg;
		} else if(code instanceof Label) {
			Label a = (Label) code;						
			return a.label + ":";
		} else if(code instanceof Goto) {
			Goto a = (Goto) code;						
			return "goto " + a.target;
		} else if(code instanceof Skip) {
			return "skip";
		} else if(code instanceof IfGoto) {
			IfGoto a = (IfGoto) code;			
			return "if " + CExpr.toString(a.lhs, flags) + " " + a.op.toString()
					+ " " + CExpr.toString(a.rhs, flags) + " goto " + a.target;
		} else if(code instanceof Return) {
			Return a = (Return) code;			
			if(a.rhs != null) {
				return "return " + CExpr.toString(a.rhs, flags);
			}
			return "return";
		} else if(code instanceof Recurse) {
			Recurse a = (Recurse) code;						
			return "recurse " + CExpr.toString(a.rhs, flags);		
		} else if(code instanceof Check) {
			Check a = (Check) code;						
			return "check:";
		} else if(code instanceof Induct) {
			Induct a = (Induct) code;	
			return "induct " + CExpr.toString(a.variable, flags) + " over "
					+ CExpr.toString(a.source, flags) + ":";
		} else if(code instanceof Forall) {
			Forall a = (Forall) code;	
			return "for " + CExpr.toString(a.variable, flags) + " in "
					+ CExpr.toString(a.source, flags) + ":";	
		} else if(code instanceof Loop) {
			Loop a = (Loop) code;
			String r = "loop ";
			for(CExpr.LVar lv : a.modifies) {
				r += lv;
			}
			return r + ":";	
		} else {
			throw new IllegalArgumentException("Unknown code encountered: " + code);
		}
	}
	
	/**
	 * Determine which variables are used by this code.
	 */
	public static <T> void match(Code c, Class<T> match, Collection<T> matches) {
		if(match.isInstance(c)) {
			matches.add((T)c);
		}
		
		if(c instanceof Assign) {
			Assign a = (Assign) c;
			if(a.lhs != null) {
				CExpr.match(a.lhs,match,matches);
			}
			CExpr.match(a.rhs,match,matches);
		} else if(c instanceof Debug) {
			Debug a = (Debug) c;						
			CExpr.match(a.rhs,match,matches);
		} else if(c instanceof IfGoto) {
			IfGoto a = (IfGoto) c;			
			CExpr.match(a.lhs,match,matches);
			CExpr.match(a.rhs,match,matches);
		} else if(c instanceof Return) {
			Return a = (Return) c;			
			if(a.rhs != null) {
				CExpr.match(a.rhs,match,matches);
			}
		} else if(c instanceof Recurse) {
			Recurse a = (Recurse) c;						
			CExpr.match(a.rhs,match,matches);			
		} else if(c instanceof Induct) {
			Induct a = (Induct) c;	
			CExpr.match(a.variable, match,matches);
			CExpr.match(a.source, match,matches);						
		} else if(c instanceof Forall) {
			Forall a = (Forall) c;	
			CExpr.match(a.variable, match,matches);
			CExpr.match(a.source, match,matches);						
		} 
	}

	public static HashSet<String> usedVariables(Code code) {
		HashSet<CExpr.LVar> uses = new HashSet<CExpr.LVar>();		
		Code.match(code,CExpr.LVar.class,uses);		
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
	 * Substitute all occurrences of variable from with variable to.
	 * 
	 * @param c
	 * @param uses
	 */
	public static Code substitute(HashMap<String,CExpr> binding, Code c) {
		if(c instanceof Assign) {
			Assign a = (Assign) c;
			LVal lhs = null;
			if(a.lhs != null) {
				lhs = (LVal) CExpr.substitute(binding, a.lhs);
			}
			return new Assign(lhs, CExpr.substitute(binding, a.rhs));
		} else if(c instanceof Debug) {
			Debug a = (Debug) c;
			return new Debug(CExpr.substitute(binding, a.rhs));
		} else if(c instanceof IfGoto) {
			IfGoto u = (IfGoto) c;
			return new IfGoto(u.op, CExpr.substitute(binding, u.lhs),
					CExpr.substitute(binding, u.rhs), u.target);
		} else if(c instanceof Return) {
			Return a = (Return) c;
			if (a.rhs != null) {
				return new Return(CExpr.substitute(binding, a.rhs));
			}
			return a;
		} else if(c instanceof Recurse) {
			Recurse a = (Recurse) c;			
			return new Recurse(CExpr.substitute(binding, a.rhs));			
		} else if(c instanceof Induct) {			
			Induct a = (Induct) c;				
			return new Induct(a.label, (CExpr.Register) CExpr
					.substitute(binding, a.variable), CExpr.substitute(binding,
					a.source));
		} else if(c instanceof Forall) {			
			Forall a = (Forall) c;				
			return new Forall(a.label, a.invariant, (CExpr.Register) CExpr
					.substitute(binding, a.variable), CExpr.substitute(binding,
					a.source), a.modifies);
		} else {
			return c;
		}
	}	
	
	/**
	 * The register shift method is responsible for mapping every register with
	 * index i, to be a register with index i + shift. This is used to guarantee
	 * that the registers of blocks inserted into other blocks do not collide.
	 * 
	 * @param shift
	 * @param body
	 * @return
	 */
	public static Code registerShift(int shift, Code c) {
		if (c instanceof Assign) {
			Assign a = (Assign) c;			
			LVal lhs = null;
			if(a.lhs != null) {
				lhs = (LVal) CExpr.registerShift(shift, a.lhs);
			}
			return new Assign(lhs, CExpr.registerShift(shift, a.rhs));
		} else if (c instanceof Debug) {
			Debug a = (Debug) c;
			return new Debug((LVal) CExpr.registerShift(shift, a.rhs));
		} else if (c instanceof IfGoto) {
			IfGoto u = (IfGoto) c;
			return new IfGoto(u.op, CExpr.registerShift(shift, u.lhs),
					CExpr.registerShift(shift, u.rhs), u.target);
		} else if(c instanceof Return) {
			Return a = (Return) c;
			if (a.rhs != null) {
				return new Return(CExpr.registerShift(shift, a.rhs));
			}
			return a;
		} else if(c instanceof Recurse) {
			Recurse a = (Recurse) c;			
			return new Recurse(CExpr.registerShift(shift, a.rhs));			
		} else if(c instanceof Induct) {
			Induct i = (Induct) c;			
			return new Induct(i.label, (CExpr.Register) CExpr.registerShift(shift,
					i.variable), CExpr.registerShift(shift, i.source));			
		} else if(c instanceof Forall) {
			Forall a = (Forall) c;	
			return new Forall(a.label, a.invariant, (CExpr.Register) CExpr
					.registerShift(shift, a.variable), CExpr.registerShift(
					shift, a.source), a.modifies);
		} else {
			return c;
		}
	}
	
	/**
	 * This represents a simple assignment between two variables.
	 * 
	 * @author djp
	 * 
	 */
	public final static class Assign extends Code {		
		public final LVal lhs;
		public final CExpr rhs;
		
		public Assign(LVal lhs, CExpr rhs) {			
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Assign) {
				Assign a = (Assign) o;
				return lhs.equals(a.lhs) && rhs.equals(a.rhs);
				
			}
			return false;
		}
		
		public int hashCode() {
			return lhs.hashCode() + rhs.hashCode();			
		}
		
		public String toString() {
			if(lhs == null) {
				return rhs.toString();
			}
			return lhs + " := " + rhs;
		}		
	}

	public final static class Return extends Code {
		public final CExpr rhs;
		
		public Return(CExpr rhs) {					
			this.rhs = rhs;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Return) {
				Return a = (Return) o;
				return rhs.equals(a.rhs);
				
			}
			return false;
		}
		
		public int hashCode() {
			return rhs.hashCode();			
		}
		
		public String toString() {
			if(rhs == null) {
				return "return";
			} else {
				return "return " + rhs;
			}
		}		
	}
		
	public final static class Debug extends Code {				
		public final CExpr rhs;
		
		public Debug(CExpr rhs) {			
			this.rhs = rhs;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Debug) {
				Debug a = (Debug) o;
				return rhs.equals(a.rhs);
				
			}
			return false;
		}
		
		public int hashCode() {
			return rhs.hashCode();			
		}
		
		public String toString() {
			return "debug " + rhs;
		}		
	}
	
	public final static class Fail extends Code {				
		public final String msg;
		
		public Fail(String msg) {			
			this.msg = msg;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Fail) {
				Fail a = (Fail) o;
				return msg.equals(a.msg);
				
			}
			return false;
		}
		
		public int hashCode() {
			return msg.hashCode();			
		}
		
		public String toString() {
			return "fail \"" + msg + "\"";
		}		
	}
	
	/**
	 * This represents a conditional branching instruction
	 * @author djp
	 *
	 */
	public final static class IfGoto extends Code {
		public final COP op;
		public final CExpr lhs;
		public final CExpr rhs;
		public final String target;

		public IfGoto(COP op, CExpr lhs, CExpr rhs, String target) {
			this.op = op;			
			this.lhs = lhs;
			this.rhs = rhs;
			this.target = target;
		}
		
		public int hashCode() {
			return op.hashCode() + lhs.hashCode()
					+ rhs.hashCode() + target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof IfGoto) {
				IfGoto ig = (IfGoto) o;
				return op == ig.op 
						&& lhs.equals(ig.lhs) && rhs.equals(ig.rhs)
						&& target.equals(ig.target);
			}
			return false;
		}
	
		public String toString() {
			return "if " + lhs + " " + op + " " + rhs + " goto " + target;
		}
	}	
	
	/**
	 * This represents an unconditional branching instruction
	 * @author djp
	 *
	 */
	public final static class Goto extends Code  {
		public final String target;
		
		public Goto(String target) {
			this.target = target;
		}
		
		public int hashCode() {
			return target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Goto) {
				return target.equals(((Goto)o).target);
			}
			return false;
		}
		
		public String toString() {
			return "goto " + target;
		}
	}	
	
	/**
	 * A start code indicates the start of a special block.  
	 * @author djp
	 *
	 */
	public abstract static class Start extends Code  {
		public final String label;
		
		public Start(String label) {
			this.label = label;
		}
		
		public int hashCode() {
			return label.hashCode();
		}		
	}
	
	public abstract static class End extends Code  {
		public final String target;
		
		public End(String target) {
			this.target = target;
		}
		
		public int hashCode() {
			return target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof End) {
				return target.equals(((End)o).target);
			}
			return false;
		}
		
		public String toString() {
			return "end " + target;
		}
	}
	
	/**
	 * This represents the target of a branching instruction
	 * @author djp
	 *
	 */
	public final static class Label extends Code  {
		public final String label;
		
		public Label(String label) {
			this.label = label;
		}
		
		public int hashCode() {
			return label.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Label) {
				return label.equals(((Label)o).label);
			}
			return false;
		}
		
		public String toString() {
			return label + ":";
		}
	}	
	
	public static class Skip extends Code  {
		public int hashCode() {
			return 1;
		}
		
		public boolean equals(Object o) {
			return o instanceof Skip;
		}
		
		public String toString() {
			return "skip";
		}
	}
	public static class ExternJvm extends Skip  {
		public final List<Bytecode> bytecodes;

		public ExternJvm(Collection<Bytecode> bytecodes) {
			this.bytecodes = Collections
					.unmodifiableList(new ArrayList<Bytecode>(bytecodes));
		}
		
		public int hashCode() {
			return 1;
		}
		
		public boolean equals(Object o) {
			return o instanceof Skip;
		}
		
		public String toString() {
			return "skip";
		}
	}

	public static class Loop extends Start {
		public final HashSet<CExpr.LVar> modifies;
		public final Block invariant;

		public Loop(String label, Block invariant,
				Collection<CExpr.LVar> modifies) {
			super(label);
			this.invariant = invariant;
			this.modifies = new HashSet<CExpr.LVar>(modifies);
		}

		public boolean equals(Object o) {
			if (o instanceof Loop) {
				Loop a = (Loop) o;
				if (invariant != null) {
					return label.equals(a.label)
							&& invariant.equals(a.invariant)
							&& a.modifies.equals(modifies);
				} else {
					return label.equals(a.label) && a.invariant == null
							&& a.modifies.equals(modifies);
				}
			}
			return false;
		}

		public int hashCode() {
			return label.hashCode() + modifies.hashCode();
		}

		public String toString() {
			String r = "";
			if (modifies.size() > 0) {
				r += " ";
				boolean firstTime = true;
				for (CExpr.LVar v : modifies) {
					if (!firstTime) {
						r += ", ";
					}
					firstTime = false;
					r += v;
				}
			}
			return "loop" + r + ":";
		}
	}

	public static class Recurse extends Code {
		public final CExpr rhs;
		
		public Recurse(CExpr rhs) {
			this.rhs = rhs;
		}
		
		public int hashCode() {
			return rhs.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Recurse) {
				Recurse r = (Recurse) o;
				return rhs.equals(r.rhs);
			}
			return false;
		}
		
		public String toString() {
			return "recurse " + rhs;
		}
	}
	
	public static class Induct extends Start {
		public final CExpr.Register variable;
		public final CExpr source;

		public Induct(String label, CExpr.Register variable, CExpr source) {
			super(label);
			this.variable = variable;
			this.source = source;
		}

		public boolean equals(Object o) {
			if (o instanceof Induct) {
				Induct a = (Induct) o;
				return label.equals(a.label) && a.variable.equals(variable)
						&& a.source.equals(source);
			}
			return false;
		}

		public int hashCode() {
			return label.hashCode() + variable.hashCode() + source.hashCode();
		}

		public String toString() {
			return "induct %" + variable.index + " over " + source + ":";
		}
	}


	public final static class InductEnd extends End {
		public InductEnd(String label) {
			super(label);
		}

		public boolean equals(Object o) {
			if (o instanceof InductEnd) {
				return target.equals(((InductEnd) o).target);
			}
			return false;
		}

		public String toString() {
			return "end ";
		}
	}

	public static class LoopEnd extends End {
		public LoopEnd(String label) {
			super(label);
		}
		
		public boolean equals(Object o) {
			if(o instanceof LoopEnd) {
				return target.equals(((LoopEnd)o).target);
			}
			return false;
		}
		
		public String toString() {
			return "end";
		}	
	}
	
	public final static class Forall extends Loop {
		public final CExpr.Register variable;
		public final CExpr source;

		public Forall(String label, Block invariant, CExpr.Register variable,
				CExpr source) {
			super(label, invariant, Collections.EMPTY_SET);
			this.variable = variable;
			this.source = source;
		}

		public Forall(String label, Block invariant, CExpr.Register variable,
				CExpr source, Collection<CExpr.LVar> modifies) {
			super(label, invariant, modifies);
			this.variable = variable;
			this.source = source;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Forall) {
				Forall a = (Forall) o;
				return label.equals(a.label) && variable.equals(a.variable)
						&& source.equals(a.source);
			}
			return false;
		}

		public int hashCode() {
			return source.hashCode() + label.hashCode() + variable.hashCode();
		}

		public String toString() {			
			if(modifies.isEmpty()) {
				return "for " + variable + " in " + source + ":";
			} else {
				String r = "modifies ";
				boolean firstTime=true;
				for(CExpr.LVar m : modifies) {
					if(!firstTime) {
						r += ", ";
					}
					firstTime=false;
					r += m.name();
				}
				return "for " + variable + " in " + source + " " + r + ":";
			}
		}
	}
	
	public final static class ForallEnd extends LoopEnd {
		public ForallEnd(String label) {
			super(label);
		}

		public boolean equals(Object o) {
			if (o instanceof ForallEnd) {
				return target.equals(((ForallEnd) o).target);
			}
			return false;
		}

		public String toString() {
			return "end";
		}
	}
	
	/**
	 * A Check code indicates the start of static check block, which is where
	 * variables and constraints are tested to ensure they meet the various
	 * 
	 * @author djp
	 * 
	 */
	public final static class Check extends Start {									
		public Check(String label) {								
			super(label);			
		}
				
		public boolean equals(Object o) {
			if (o instanceof Check) {
				Check a = (Check) o;
				return label.equals(a.label); 
			}
			return false;
		}
				
		public String toString() {
			return "check:";												
		}
	}
	
	public final static class CheckEnd extends End {
		public CheckEnd(String label) {
			super(label);
		}
		
		public boolean equals(Object o) {
			if(o instanceof CheckEnd) {
				return target.equals(((CheckEnd)o).target);
			}
			return false;
		}
		
		public String toString() {
			return "end ";
		}	
	}
	
	public enum COP { 
		EQ() {
			public String toString() { return "=="; }
		},
		NEQ{
			public String toString() { return "!="; }
		},
		LT{
			public String toString() { return "<"; }
		},
		LTEQ{
			public String toString() { return "<="; }
		},
		GT{
			public String toString() { return ">"; }
		},
		GTEQ{
			public String toString() { return ">="; }
		},
		ELEMOF{
			public String toString() { return "in"; }
		},
		SUBSET{
			public String toString() { return "<"; }
		},
		SUBSETEQ{
			public String toString() { return "<="; }
		},
		SUBTYPEEQ() {
			public String toString() { return "<:"; }
		},
		NSUBTYPEEQ() {
			public String toString() { return "<!"; }
		}
	};		
}
