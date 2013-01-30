package wycs.lang;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import wyone.core.Attribute;
import wyone.util.SyntacticElement;

public interface Expr {
	
	public static class Var extends SyntacticElement.Impl implements Expr {
		public final String name;
				
		public Var(String name, Attribute... attributes) {
			super(attributes);
			this.name = name;
		}
	}
	
	public static class Constant extends SyntacticElement.Impl implements Expr {
		public final wyil.lang.Constant constant;
		
		public Constant(wyil.lang.Constant constant, Attribute... attributes) {
			super(attributes);
			this.constant = constant;
		}
	}
	
	public static class Unary extends SyntacticElement.Impl implements Expr {
		public enum Op {
			NOT(0) {
				public String toString() {
					return "1";
				}
			},
			
			NEG(1) {
				public String toString() {
					return "-";
				}
			};
						
			public int offset;

			private Op(int offset) {
				this.offset = offset;
			}			
		}
		public Unary(Attribute... attributes) {
			super(attributes);			
		}
	}
		
	public static class Binary extends SyntacticElement.Impl implements Expr {
		public enum Op {
			AND(0) {
				public String toString() {
					return "&&";
				}
			},
			
			OR(1) {
				public String toString() {
					return "||";
				}
			},
			
			ADD(2) {
				public String toString() {
					return "+";
				}
			},
			SUB(3) {
				public String toString() {
					return "-";
				}
			},
			MUL(4) {
				public String toString() {
					return "*";
				}
			},
			DIV(5) {
				public String toString() {
					return "/";
				}
			},
			REM(6) {
				public String toString() {
					return "%";
				}
			},
			EQ(7) {
				public String toString() {
					return "==";
				}
			},
			NEQ(8) {
				public String toString() {
					return "!=";
				}
			},
			LT(9) {
				public String toString() {
					return "<";
				}
			},
			LTEQ(10) {
				public String toString() {
					return "<=";
				}
			},
			GT(11) {
				public String toString() {
					return ">";
				}
			},
			GTEQ(12) {
				public String toString() {
					return ">=";
				}
			},
			ELEMOF(13) {
				public String toString() {
					return "in";
				}
			},
			SUBSETEQ(14) {
				public String toString() {
					return "{=";
				}
			};
			
			public int offset;

			private Op(int offset) {
				this.offset = offset;
			}
		};

		public Binary(Attribute... attributes) {
			super(attributes);
		}
	}
	
	public static abstract class Quantifier extends SyntacticElement.Impl implements Expr {
		public final List<String> vars; 
		
		public Quantifier(Collection<String> vars, Attribute... attributes) {
			super(attributes);			
			this.vars = new CopyOnWriteArrayList<String>(vars);
		}
	}
	
	public static class ForAll extends Quantifier {
		public ForAll(Collection<String> vars, Attribute... attributes) {
			super(vars, attributes);						
		}
	}
	
	public static class Exists extends Quantifier {
		public Exists(Collection<String> vars, Attribute... attributes) {
			super(vars, attributes);						
		}
	}
}
