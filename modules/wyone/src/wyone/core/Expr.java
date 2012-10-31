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

package wyone.core;

import java.util.*;
import wyone.core.*;
import wyone.util.*;

public interface Expr extends SyntacticElement {

	public interface LVal extends Expr {}
	
	public static class Variable extends SyntacticElement.Impl implements Expr, LVal {
		public final String var;
		public boolean isConstructor = false; 

		public Variable(String var, Attribute... attributes) {
			super(attributes);
			this.var = var;
		}

		public String toString() {
			return var;
		}
	}
	
	public static class Constant extends SyntacticElement.Impl implements Expr {
		public final Object value;

		public Constant(Object val, Attribute... attributes) {
			super(attributes);
			this.value = val;
		}
		
		public String toString() {
			return value.toString();
		}
	}
	
	public static class Cast extends SyntacticElement.Impl implements Expr {
		public final Type type;
		public Expr src;

		public Cast(Type type, Expr src, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.src = src;
		}

		public Cast(Type type, Expr src, int index,
				Collection<Attribute> attributes) {
			super(attributes);
			this.type = type;
			this.src = src;
		}

		public String toString() {
			return "(" + type + ") " + src;
		}
	}

	public enum BOp { 
		AND {
			public String toString() { return "&&"; }
		},
		OR{
			public String toString() { return "||"; }
		},
		ADD{
			public String toString() { return "add"; }
		},
		SUB{
			public String toString() { return "sub"; }
		},
		MUL{
			public String toString() { return "mul"; }
		},
		DIV{
			public String toString() { return "div"; }
		},				
		EQ{
			public String toString() { return "eq"; }
		},
		NEQ{
			public String toString() { return "neq"; }
		},
		LT{
			public String toString() { return "lt"; }
		},
		LTEQ{
			public String toString() { return "le"; }
		},
		GT{
			public String toString() { return "gt"; }
		},
		GTEQ{
			public String toString() { return "ge"; }
		},
		IN{
			public String toString() { return "in"; }
		},
		APPEND{
			public String toString() { return "append"; }
		},
		DIFFERENCE{
			public String toString() { return "difference"; }
		},
		RANGE{
			public String toString() { return "range"; }
		},
		IS{
			public String toString() { return "is"; }
		}
	};
	
	public static class BinOp extends SyntacticElement.Impl implements Expr {
		public BOp op;
		public Expr lhs;
		public Expr rhs;
		
		public BinOp(BOp op, Expr lhs, Expr rhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public BinOp(BOp op, Expr lhs, Expr rhs, Collection<Attribute> attributes) {
			super(attributes);
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public String toString() {
			
			return lhs + " " + op + " " + rhs;
		}
	}
	
	// A list access is very similar to a BinOp, except that it can be assiged.
	public static class ListAccess extends SyntacticElement.Impl implements
			Expr, LVal {		
		public Expr src;
		public Expr index;
		
		public ListAccess(Expr src, Expr index, Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
		}
		
		public ListAccess(Expr src, Expr index, Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
		}
					
		public String toString() {
			return src + "[" + index + "]";
		}
	}

	public static class ListUpdate extends SyntacticElement.Impl implements
	Expr, LVal {		
		public Expr src;
		public Expr index;
		public Expr value;

		public ListUpdate(Expr src, Expr index, Expr value, Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
			this.value = value;
		}

		public ListUpdate(Expr src, Expr index, Expr value, Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
			this.value = value;
		}

		public String toString() {
			return src + "[" + index + " = " + value + "]";
		}
	}
	
	public static class Substitute extends SyntacticElement.Impl implements
	Expr {		
		public Expr src;
		public Expr original;
		public Expr replacement;

		public Substitute(Expr src, Expr original, Expr replacement, Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.original = original;
			this.replacement = replacement;
		}

		public Substitute(Expr src, Expr original, Expr replacement, Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.original = original;
			this.replacement = replacement;
		}

		public String toString() {
			return src + "[" + original + " \\ " + replacement + "]";
		}
	}
	public static class TermAccess extends SyntacticElement.Impl implements
			Expr, LVal {		
		public Expr src;
		
		public TermAccess(Expr src, Attribute... attributes) {
			super(attributes);
			this.src = src;
		}
		
		public TermAccess(Expr src, int index, Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
		}
					
		public String toString() {
			return "*" + src;
		}
	}
	
	public enum UOp {
		NOT {
			public String toString() { return "not"; }
		},
		NEG {
			public String toString() { return "neg"; }
		},
		LENGTHOF {
			public String toString() { return "length"; }
		}		
	}	
	
	public static class UnOp extends SyntacticElement.Impl implements Expr {
		public final UOp op;
		public Expr mhs;		
		
		public UnOp(UOp op, Expr mhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.mhs = mhs;			
		}
		
		public String toString() {
			if(op == UOp.NOT) {
				return "!" + mhs.toString();
			} else if(op == UOp.NEG) {
				return "-" + mhs.toString();
			} else {
				return "|" + mhs.toString() + "|";
			}
		}
	}
	
	public enum NOp {
		LISTGEN,
		SETGEN,
		BAGGEN
	}
		
	public static class NaryOp extends SyntacticElement.Impl implements Expr {
		public final NOp op;
		public final ArrayList<Expr> arguments;
		public NaryOp(NOp nop, Collection<Expr> arguments, Attribute... attributes) {
			super(attributes);
			this.op = nop;
			this.arguments = new ArrayList<Expr>(arguments);
		}
		public NaryOp(NOp nop, Collection<Expr> arguments, Collection<Attribute> attributes) {
			super(attributes);
			this.op = nop;
			this.arguments = new ArrayList<Expr>(arguments);
		}
		public NaryOp(NOp nop, Attribute attribute, Expr... arguments) {
			super(attribute);
			this.op = nop;
			this.arguments = new ArrayList<Expr>();
			for(Expr a : arguments) {
				this.arguments.add(a);
			}
		}
		public String toString() {
			switch(op) {
				 
			case SETGEN:
			case BAGGEN:
			case LISTGEN: {
				String args = "";
				boolean firstTime = true;
				for (Expr e : arguments) {
					if (!firstTime) {
						args += ",";
					}
					args += e;
				}
				if(op == Expr.NOp.SETGEN) {
					return "{" + args + "}";
				} else if(op == Expr.NOp.BAGGEN) {
					return "{|" + args + "|}";
				} else {
					return "[" + args + "]";
				}
			}
			default:
				return arguments.get(0) + "[" + arguments.get(1) + ".."
						+ arguments.get(2) + "]";
			}
		}
	}
	
	public static class Constructor extends SyntacticElement.Impl implements Expr {
		public final String name;		
		public Expr argument;
		
		public Constructor(String name, Expr argument,
				Attribute... attributes) {
			super(attributes);
			this.name = name;			
			this.argument = argument;
		}
		
		public Constructor(String name, Expr argument,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;			
			this.argument = argument;
		}
		
		public String toString() {
			return name + "(" + argument + ")";			
		}
	}
	
	public static class Comprehension extends SyntacticElement.Impl implements Expr {
		public final COp cop;
		public Expr value;
		public final ArrayList<Pair<Expr.Variable,Expr>> sources;
		public Expr condition;
		
		public Comprehension(COp cop, Expr value,
				Collection<Pair<Expr.Variable, Expr>> sources, Expr condition,
				Attribute... attributes) {
			super(attributes);
			this.cop = cop;
			this.value = value;
			this.condition = condition;
			this.sources = new ArrayList<Pair<Expr.Variable, Expr>>(sources);
		}
	}
	
	public enum COp {
		SETCOMP,
		BAGCOMP,
		LISTCOMP,
		NONE, // implies value == null					
		SOME, // implies value == null
	}				
}
