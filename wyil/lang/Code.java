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

import java.util.Set;

public abstract class Code {

	// ==========================================
	// =============== Methods ==================
	// ==========================================
	
	/**
	 * Determine which variables are used by this code.
	 */
	public static void usedVariables(Code c, Set<String> uses) {
		if(c instanceof Assign) {
			Assign a = (Assign) c;
			uses.add(a.lhs);
			RVal.usedVariables(a.rhs,uses);
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
		public final RVal rhs;
		
		public Assign(Type type, String lhs, RVal rhs) {
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

	public final static class Return extends Code {
		public final Type type;
		public final RVal rhs;
		
		public Return(Type type,RVal rhs) {
			this.type = type;			
			this.rhs = rhs;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Return) {
				Return a = (Return) o;
				return type.equals(a.type) && rhs.equals(a.rhs);
				
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode() + rhs.hashCode();			
		}
		
		public String toString() {
			return "return[" + type + "] " + rhs;
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
		public final RVal rhs1;
		public final RVal rhs2;
		
		public BinOp(Type type, BOP op, String lhs, RVal rhs1, RVal rhs2) {
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
	
	public final static class UnOp extends Code {
		public final UOP op;
		public final Type type;
		public final String lhs;
		public final RVal rhs;		
		
		public UnOp(Type type, UOP op, String lhs, RVal rhs) {
			this.op = op;
			this.type = type;
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public boolean equals(Object o) {
			if(o instanceof UnOp) {
				UnOp a = (UnOp) o;
				return op == a.op && type.equals(a.type) && lhs.equals(a.lhs)
						&& rhs.equals(a.rhs);
				
			}
			return false;
		}
		
		public int hashCode() {
			return op.hashCode() + type.hashCode() + lhs.hashCode()
					+ rhs.hashCode();
		}
		
		public String toString() {
			if(op == UOP.LENGTHOF){
				return type + " " + lhs + " := |" + rhs + "|";
			} else {
				return type + " " + lhs + " := " + op + rhs;
			}
		}		
	}
	
	public final static class Debug extends Code {				
		public final RVal rhs;
		
		public Debug(RVal rhs) {			
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
	
	/**
	 * This represents a conditional branching instruction
	 * @author djp
	 *
	 */
	public final static class IfGoto extends Code {
		public final BOP op;
		public final Type type;
		public final RVal lhs;
		public final RVal rhs;
		public final String target;

		public IfGoto(Type type, BOP op, RVal lhs, RVal rhs, String target) {
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
	public enum UOP { 
		NEG() {
			public String toString() { return "-"; }
		},
		NOT() {
			public String toString() { return "!"; }
		},
		LENGTHOF() {
			public String toString() { return "||"; }
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
