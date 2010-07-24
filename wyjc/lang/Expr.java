package wyjc.lang;

import java.util.*;

import wyil.lang.ModuleID;
import wyil.lang.Value;

public interface Expr extends SyntacticElement {
	
	public interface LVal {}
	
	public static class Variable extends SyntacticElement.Impl implements Expr, LVal {
		protected final String var;

		public Variable(String var, Attribute... attributes) {
			super(attributes);
			this.var = var;
		}

		public Variable(String var, Collection<Attribute> attributes) {
			super(attributes);
			this.var = var;
		}

		public String toString() {
			return var;
		}
	}
	
	public static class NamedConstant extends Variable {
		public final ModuleID mid;

		public NamedConstant(String var, ModuleID mid, Attribute... attributes) {
			super(var, attributes);
			this.mid = mid;
		}

		public NamedConstant(String var, ModuleID mid,
				Collection<Attribute> attributes) {
			super(var, attributes);
			this.mid = mid;
		}
	}

	public static class Constant extends SyntacticElement.Impl implements Expr {
		public final Value val;

		public Constant(Value val, Attribute... attributes) {
			super(attributes);
			this.val = val;
		}

		public Constant(Value val, Collection<Attribute> attributes) {
			super(attributes);
			this.val = val;
		}
	}

	public enum BOp { 
		AND,
		OR,
		ADD,
		SUB,
		MUL,
		DIV,		
		UNION,
		INTERSECTION,
		EQ,
		NEQ,
		LT,
		LTEQ,
		GT,
		GTEQ,
		SUBSET,
		SUBSETEQ,
		ELEMENTOF
	};
		
	public static class BinOp extends SyntacticElement.Impl implements Expr {
		public final BOp op;
		public final Expr lhs;
		public final Expr rhs;
		
		public BinOp(BOp op, Expr lhs, Expr rhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public BinOp(BOp op, Expr lhs, Expr rhs,
				Collection<Attribute> attributes) {
			super(attributes);
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
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

		public UnOp(UOp op, Expr mhs,
				Collection<Attribute> attributes) {
			super(attributes);
			this.op = op;
			this.mhs = mhs;			
		}
	}
	
	public static class Invoke extends SyntacticElement.Impl implements Expr,Stmt {
		public final String name;
		public final Expr receiver;
		public final List<Expr> arguments;
		
		public Invoke(String name, Expr receiver, List<Expr> arguments,
				Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.receiver = receiver;
			this.arguments = arguments;
		}
		
		public Invoke(String name, Expr receiver, List<Expr> arguments,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.receiver = receiver;
			this.arguments = arguments;
		}
	}
}
