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
		if(c instanceof VarAssign) {
			VarAssign va = (VarAssign) c;
			return new VarAssign(va.type,substitute(v2,v2,va.lhs),substitute(v2,v2,va.rhs));
		} else if(c instanceof VarLoad) {
			VarLoad va = (VarLoad) c;
			return new VarLoad(va.type,substitute(v2,v2,va.lhs),va.rhs);
		} else if(c instanceof IfGoto) {
			IfGoto g = (IfGoto) c;
			return new IfGoto();
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
	public final static class VarAssign extends Code {
		public final Type type;
		public final String lhs;
		public final String rhs;
		
		public VarAssign(Type type, String lhs, String rhs) {
			this.type = type;
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public boolean equals(Object o) {
			if(o instanceof VarAssign) {
				VarAssign a = (VarAssign) o;
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
	public final static class VarLoad extends Code  {		
		public final Type type;
		public final String lhs;
		public final Value rhs;
		
		public VarLoad(Type type, String lhs, Value rhs) {
			this.type = type;
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public boolean equals(Object o) {
			if (o instanceof VarLoad) {
				VarLoad a = (VarLoad) o;
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
	 * This represents a conditional branching instruction
	 * @author djp
	 *
	 */
	public final static class IfGoto extends Code  {
		public final CompOP op;
		public final String lhs;
		public final String rhs;
		public final int target;
		
		public IfGoto(CompOP op, String lhs, String rhs, int target) {
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
			this.target = target;
		}
	}
	
	public enum CompOP{ EQ,NEQ,LT,LTEQ,GT,GTEQ,ELEMOF,SUBSET,SUBSETEQ };
	
	/**
	 * This represents an unconditional branching instruction
	 * @author djp
	 *
	 */
	public final static class Goto extends Code  {
		public final int target;
		
		public Goto(int target) {
			this.target = target;
		}
	}
	
	// ==========================================
	// =============== Opcodes ==================
	// ==========================================
	
	// Arithmetic Opcodes	
	public final int ADD = 10;
	public final int SUB = 11;
	public final int MUL = 12;
	public final int DIV = 13;	
	public final int UNION = 16;
	public final int INTERSECT = 17;
	public final int DIFFERENCE = 18;			
}
