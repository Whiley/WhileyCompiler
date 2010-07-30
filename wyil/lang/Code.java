// This file is part of the Whiley Intermediate Language (wyil).
//
// The Whiley Intermediate Language is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley Intermediate Language is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley Intermediate Language. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.lang;

public abstract class Code {

	// ==========================================
	// =============== Methods ==================
	// ==========================================
	
	public static Code substitute(String v1, String v2, Code c) {
		if(c instanceof Assign) {
			Assign va = (Assign) c;
			return new Assign(va.type,substitute(v1,v2,va.lhs),substitute(v2,v2,va.rhs));
		} else if(c instanceof Load) {
			Load va = (Load) c;
			return new Load(va.type,substitute(v1,v2,va.lhs),va.rhs);
		} else if(c instanceof IfGoto) {
			IfGoto ig = (IfGoto) c;
			return new IfGoto(ig.type, ig.op, substitute(v2, v2, ig.lhs),
					substitute(v1, v2, ig.rhs), ig.target);
		} else {
			return c;
		}
	}
	
	private static String substitute(String v1, String v2, String s) {
		if(s.equals(v1)) {
			return v2;
		} else {
			return s;
		}
	}
	
	/**
	 * This represents a simple assignment between two variables.
	 * 
	 * @author djp
	 * 
	 */
	public final static class Assign extends Code {
		public final Type type;
		public final String lhs;
		public final String rhs;
		
		public Assign(Type type, String lhs, String rhs) {
			this.type = type;
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Assign) {
				Assign a = (Assign) o;
				return type.equals(a.type) && lhs.equals(a.lhs) && rhs.equals(a.rhs);
				
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode() + lhs.hashCode() + rhs.hashCode();			
		}
		
		public String toString() {
			return type + " " + lhs + " := " + rhs;
		}		
	}

	/**
	 * This represents the assignment of a constant to a variable
	 * 
	 * @author djp
	 * 
	 */
	public final static class Load extends Code  {		
		public final Type type;
		public final String lhs;
		public final Value rhs;
		
		public Load(Type type, String lhs, Value rhs) {
			this.type = type;
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Load) {
				Load a = (Load) o;
				return type.equals(a.type) && lhs.equals(a.lhs)
						&& rhs.equals(a.rhs);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode() + lhs.hashCode() + rhs.hashCode();			
		}
		
		public String toString() {
			return type + " " + lhs + " := " + rhs;
		}		
	}
	
	/**
	 * This represents a simple assignment between two variables.
	 * 
	 * @author djp
	 * 
	 */
	public final static class BinOp extends Code {
		public final BOP op;
		public final Type type;
		public final String lhs;
		public final String rhs1;
		public final String rhs2;
		
		public BinOp(Type type, BOP op, String lhs, String rhs1, String rhs2) {
			this.op = op;
			this.type = type;
			this.lhs = lhs;
			this.rhs1 = rhs1;
			this.rhs2 = rhs2;
		}
		
		public boolean equals(Object o) {
			if(o instanceof BinOp) {
				BinOp a = (BinOp) o;
				return op == a.op && type.equals(a.type) && lhs.equals(a.lhs)
						&& rhs1.equals(a.rhs1) && rhs2.equals(a.rhs2);
				
			}
			return false;
		}
		
		public int hashCode() {
			return op.hashCode() + type.hashCode() + lhs.hashCode()
					+ rhs1.hashCode() + rhs2.hashCode();
		}
		
		public String toString() {
			return type + " " + lhs + " := " + rhs1 + " " + op + " " + rhs2;
		}		
	}
	
	
	/**
	 * This represents a conditional branching instruction
	 * @author djp
	 *
	 */
	public final static class IfGoto extends Code {
		public final BOP op;
		public final Type type;
		public final String lhs;
		public final String rhs;
		public final String target;

		public IfGoto(Type type, BOP op, String lhs, String rhs, String target) {
			this.op = op;
			this.type = type;
			this.lhs = lhs;
			this.rhs = rhs;
			this.target = target;
		}
		
		public int hashCode() {
			return op.hashCode() + type.hashCode() + lhs.hashCode()
					+ rhs.hashCode() + target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof IfGoto) {
				IfGoto ig = (IfGoto) o;
				return op == ig.op && type.equals(ig.type)
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
			return "." + label;
		}
	}	
	
	public enum BOP { 
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
		ADD{
			public String toString() { return "+"; }
		},
		SUB{
			public String toString() { return "-"; }
		},
		MUL{
			public String toString() { return "*"; }
		},
		DIV{
			public String toString() { return "/"; }
		},
		UNION{
			public String toString() { return "+"; }
		},
		INTERSECT{
			public String toString() { return "&"; }
		},
		DIFFERENCE{
			public String toString() { return "-"; }
		}
	};	
}
