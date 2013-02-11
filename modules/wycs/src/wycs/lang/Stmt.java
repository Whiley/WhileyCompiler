package wycs.lang;

import java.util.Collection;

import wybs.lang.Attribute;
import wybs.lang.SyntacticElement;

public abstract class Stmt extends SyntacticElement.Impl implements SyntacticElement {
	
	public Stmt(Attribute... attributes) {
		super(attributes);
	}
	
	public Stmt(Collection<Attribute> attributes) {
		super(attributes);
	}

	// ==================================================================
	// Constructors
	// ==================================================================

	public static Assert Assert(String message, Expr expr, Attribute... attributes) {
		return new Assert(message,expr,attributes);
	}
	
	public static Assert Assert(String message, Expr expr, Collection<Attribute> attributes) {
		return new Assert(message,expr,attributes);
	}
	
	// ==================================================================
	// Classes
	// ==================================================================

	public static class Assert extends Stmt {
		public final String message;
		public final Expr expr;
		
		private Assert(String message, Expr expr, Attribute... attributes) {
			super(attributes);
			this.message = message;
			this.expr = expr;
		}
		
		private Assert(String message, Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.message = message;
			this.expr = expr;
		}
		
		public String toString() {
			return "assert " + expr + ", \"" + message + "\"";
		}
	}	
}
