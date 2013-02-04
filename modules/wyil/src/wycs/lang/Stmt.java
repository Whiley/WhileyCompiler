package wycs.lang;

import wyil.lang.Attribute;
import wybs.lang.SyntacticElement;

public abstract class Stmt extends SyntacticElement.Impl implements SyntacticElement {
	
	public Stmt(Attribute... attributes) {
		super(attributes);
	}

	// ==================================================================
	// Constructors
	// ==================================================================

	public static Assert Assert(Expr expr, Attribute... attributes) {
		return new Assert(expr,attributes);
	}
	
	public static Assume Assume(Expr expr, Attribute... attributes) {
		return new Assume(expr,attributes);
	}
	
	public static Declare Declare(String name, wyil.lang.Type type, Attribute... attributes) {
		return new Declare(name,type,attributes);
	}
	
	// ==================================================================
	// Classes
	// ==================================================================

	public static class Assert extends Stmt {
		public final Expr expr;
		
		private Assert(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;
		}
		
		public String toString() {
			return "assert " + expr;
		}
	}
	
	public static class Assume extends Stmt {
		public final Expr expr;
		
		private Assume(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;
		}
		
		public String toString() {
			return "assume " + expr;
		}
	}
	
	public static class Declare extends Stmt {
		public final String name;		
		public final wyil.lang.Type type;
		
		private Declare(String name, wyil.lang.Type type, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.type = type;
		}
		
		public String toString() {
			return type + " " + name; 
		}
	}
}
