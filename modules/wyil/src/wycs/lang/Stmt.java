package wycs.lang;

import wyone.core.Attribute;
import wyone.util.SyntacticElement;

public abstract class Stmt extends SyntacticElement.Impl {
	
	public Stmt(Attribute... attributes) {
		super(attributes);
	}
	
	public class Assert extends Stmt {
		public final Expr expr;
		
		public Assert(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;
		}		
	}
	
	public class Assume extends Stmt {
		public final Expr expr;
		
		public Assume(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;
		}
	}
	
	public class Declare extends Stmt {
		public final String name;		
		public final wyil.lang.Type type;
		
		public Declare(String name, wyil.lang.Type type, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.type = type;
		}
	}
}
