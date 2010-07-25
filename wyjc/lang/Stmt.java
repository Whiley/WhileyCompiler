package wyjc.lang;

import java.util.*;

import wyjvm.lang.Bytecode;

public interface Stmt extends SyntacticElement {
	
	public static final class VarDecl extends SyntacticElement.Impl implements
			Stmt {
		public final UnresolvedType type;
		public final String name;
		public final Expr initialiser;

		public VarDecl(UnresolvedType type, String name, Expr init,
				Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
			this.initialiser = init;
		}

		public VarDecl(UnresolvedType type, String name, Expr init,
				Collection<Attribute> attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
			this.initialiser = init;
		}
	}
	
	public static final class Assign extends SyntacticElement.Impl implements Stmt {
		public final Expr.LVal lhs;
		public final Expr rhs;

		public Assign(Expr.LVal lhs, Expr rhs, Attribute... attributes) {
			super(attributes);
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public Assign(Expr.LVal lhs, Expr rhs, Collection<Attribute> attributes) {
			super(attributes);
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public String toString() {
			return lhs + " = " + rhs;
		}
	}
	
	public static final class Assert extends SyntacticElement.Impl implements Stmt {
		public final Expr expr;		

		public Assert(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;			
		}

		public Assert(String msg, Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.expr = expr;
		}
		
		public String toString() {
			return "assert " + expr;
		}
	}
	
	public static final class Return extends SyntacticElement.Impl implements Stmt {
		public final Expr expr;		

		public Return(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;			
		}

		public Return(Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.expr = expr;			
		}
		
		public String toString() {
			if(expr != null) {
				return "return " + expr;
			} else {
				return "return";
			}
		}
	}
	
	public static final class IfElse extends SyntacticElement.Impl implements Stmt {
		public final Expr condition;
		public final ArrayList<Stmt> trueBranch;
		public final ArrayList<Stmt> falseBranch;
		
		public IfElse(Expr condition, List<Stmt> trueBranch,
				List<Stmt> falseBranch, Attribute... attributes) {
			super(attributes);
			this.condition = condition;
			this.trueBranch = new ArrayList<Stmt>(trueBranch);
			this.falseBranch = new ArrayList<Stmt>(falseBranch);
		}
		
		public IfElse(Expr condition, List<Stmt> trueBranch,
				List<Stmt> falseBranch, Collection<Attribute> attributes) {
			super(attributes);
			this.condition = condition;
			this.trueBranch = new ArrayList<Stmt>(trueBranch);
			this.falseBranch = new ArrayList<Stmt>(falseBranch);
		}
	}
	
	public static class Skip extends SyntacticElement.Impl implements Stmt {
		public Skip(Attribute... attributes) {
			super(attributes);
		}

		public Skip(Collection<Attribute> attributes) {
			super(attributes);
		}
	}
	
	public static final class Debug extends Skip {
		public final Expr expr;		

		public Debug(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;			
		}

		public Debug(Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.expr = expr;			
		}
		
		public String toString() {
			return "debug " + expr;			
		}
	}
	
	public static final class ExternJvm extends Skip implements Stmt {
		public ArrayList<Bytecode> bytecodes;
		
		public ExternJvm(Collection<Bytecode> bytecodes, Attribute... attributes) {
			super(attributes);
			this.bytecodes = new ArrayList<Bytecode>(bytecodes);
		}
		
		public ExternJvm(Collection<Bytecode> bytecodes, Collection<Attribute> attributes) {
			super(attributes);
			this.bytecodes = new ArrayList<Bytecode>(bytecodes);
		}		
	}
}
