package wycs.lang;

import java.util.Collection;

import wyil.lang.Attribute;
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
	
	public static Assume Assume(Expr expr, Attribute... attributes) {
		return new Assume(expr,attributes);
	}
	
	public static Assume Assume(Expr expr, Collection<Attribute> attributes) {
		return new Assume(expr,attributes);
	}
	
	public static Declare Declare(String name, wyil.lang.Type type, Attribute... attributes) {
		return new Declare(name,type,attributes);
	}
	
	public static Declare Declare(String name, wyil.lang.Type type, Collection<Attribute> attributes) {
		return new Declare(name,type,attributes);
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
	
	public static class Assume extends Stmt {
		public final Expr expr;
		
		private Assume(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;
		}
		
		private Assume(Expr expr, Collection<Attribute> attributes) {
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
		
		private Declare(String name, wyil.lang.Type type, Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.type = type;
		}
		
		public String toString() {
			return type + " " + name; 
		}
	}
}
