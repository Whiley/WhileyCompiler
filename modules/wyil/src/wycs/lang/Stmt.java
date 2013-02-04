package wycs.lang;

import wyil.lang.Attribute;
import wybs.lang.SyntacticElement;

public abstract class Stmt extends SyntacticElement.Impl implements SyntacticElement {
	
	public Stmt(Attribute... attributes) {
		super(attributes);
	}
	
	public static class Assert extends Stmt {
		public final Expr expr;
		
		public Assert(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;
		}
		
		public String toString() {
			return "assert " + expr;
		}
	}
	
	public static class Assume extends Stmt {
		public final Expr expr;
		
		public Assume(Expr expr, Attribute... attributes) {
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
		
		public Declare(String name, wyil.lang.Type type, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.type = type;
		}
		
		public String toString() {
			return type + " " + name; 
		}
	}
}
