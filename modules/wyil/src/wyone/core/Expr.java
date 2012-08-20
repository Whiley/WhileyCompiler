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

	public static class TypeConst extends SyntacticElement.Impl implements Expr {
		public final Type type;

		public TypeConst(Type val, Attribute... attributes) {
			super(attributes);
			this.type = val;
		}
		
		public String toString() {
			return type.toString();
		}
	}
	
	public static class BinOp extends SyntacticElement.Impl implements Expr {
		public BOp op;
		public final Expr lhs;
		public final Expr rhs;
		
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


	public enum BOp { 
		AND {
			public String toString() { return "&&"; }
		},
		OR{
			public String toString() { return "||"; }
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
		EQ{
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
		APPEND{
			public String toString() { return "++"; }
		},
		RANGE{
			public String toString() { return ".."; }
		},
		TYPEEQ{
			public String toString() { return "is"; }
		}
	};
	
	// A list access is very similar to a BinOp, except that it can be assiged.
	public static class ListAccess extends SyntacticElement.Impl implements
			Expr, LVal {		
		public final Expr src;
		public final Expr index;
		
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

	public static class TermAccess extends SyntacticElement.Impl implements
			Expr, LVal {		
		public final Expr src;
		public final int index;
		
		public TermAccess(Expr src, int index, Attribute... attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
		}
		
		public TermAccess(Expr src, int index, Collection<Attribute> attributes) {
			super(attributes);
			this.src = src;
			this.index = index;
		}
					
		public String toString() {
			return src + "#" + index;
		}
	}
	
	public enum UOp {
		NOT,
		NEG,
		LENGTHOF		
	}
	
	public static class UnOp extends SyntacticElement.Impl implements Expr {
		public final UOp op;
		public final Expr mhs;		
		
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
	
	public static class NaryOp extends SyntacticElement.Impl implements Expr {
		public final NOp op;
		public final ArrayList<Expr> arguments;
		public NaryOp(NOp nop, Collection<Expr> arguments, Attribute... attributes) {
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
				 
				case LISTGEN:
				{
					String args = "";
					boolean firstTime=true;
					for(Expr e : arguments) {
						if(!firstTime) {
							args += ",";
						}
						args += e;
					}
					return "[" + args + "]";
				}	
				default:
					return arguments.get(0) + "[" + arguments.get(1) + ".." + arguments.get(2) + "]";
			}
		}
	}
	
	public enum NOp {
		LISTGEN,
		SUBLIST					
	}
	
	public static class Constructor extends SyntacticElement.Impl implements Expr {
		public final String name;		
		public final Expr argument;
		
		public Constructor(String name, Expr argument,
				Attribute... attributes) {
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
		public final Expr value;
		public final ArrayList<Pair<String,Expr>> sources;
		public final Expr condition;
		
		public Comprehension(COp cop, Expr value,
				Collection<Pair<String, Expr>> sources, Expr condition,
				Attribute... attributes) {
			super(attributes);
			this.cop = cop;
			this.value = value;
			this.condition = condition;
			this.sources = new ArrayList<Pair<String, Expr>>(sources);
		}
	}
	
	public enum COp {
		LISTCOMP,
		NONE, // implies value == null					
		SOME, // implies value == null
	}				
}
