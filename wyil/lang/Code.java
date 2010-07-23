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

	/**
	 * This represents a simple assignment between two variables.
	 * 
	 * @author djp
	 * 
	 */
	public static class Assign {
		public Type type;
		public int lhs;
		public int rhs;
		
		public Assign(Type type, int lhs, int rhs) {
			this.type = type;
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Assign) {
				Assign a = (Assign) o;
				return type.equals(a.type) && lhs == a.lhs && rhs == a.rhs;
				
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode() + lhs + rhs;			
		}
		
		public String toString() {
			return "$" + lhs + " := " + "$" + rhs;
		}		
	}
	
	/**
	 * This represents a conditional branching instruction
	 * @author djp
	 *
	 */
	public static class IfGoto {
		public int op;
		public int lhs;
		public int rhs;
		public int target;
	}
	
	/**
	 * This represents an unconditional branching instruction
	 * @author djp
	 *
	 */
	public static class Goto {
		public int target;
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
	
	// Comparator Opcodes
	public final int EQ = 20;
	public final int NEQ = 21;
	public final int LT = 21;
	public final int LTEQ = 22;
	public final int GT = 23;
	public final int GTEQ = 23;
	public final int ELEMOF = 24;
	public final int SUBSET = 25;
	public final int SUBSETEQ = 26;
	public final int SUPSET = 27;
	public final int SUPSETEQ = 28;
			
}
