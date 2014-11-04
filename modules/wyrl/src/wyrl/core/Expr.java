// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyrl.core;

import java.util.*;
import wyrl.core.*;
import wyrl.util.*;

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
		public Object value;

		public Constant(Object val, Attribute... attributes) {
			super(attributes);
			this.value = val;
		}

		public String toString() {
			return value.toString();
		}
	}

	public static class Cast extends SyntacticElement.Impl implements Expr {
		public Type type;
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
		NUMERATOR {
			public String toString() { return "num"; }
		},
		DENOMINATOR {
			public String toString() { return "den"; }
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
		public boolean external;

		public Constructor(String name, Expr argument, boolean external,
				Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.argument = argument;
			this.external = external;
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
		SOME
	}
}
