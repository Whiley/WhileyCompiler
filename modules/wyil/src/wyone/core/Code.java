package wyone.core;

import wyone.util.SyntacticElement;

public class Code extends SyntacticElement.Impl {

	public Code(Attribute... attributes) {
		super(attributes);
	}
	
	public static final class Assign extends Code {
		public final int target;
		public final int operand;

		public Assign(int target, int operand, Attribute... attributes) {
			super(attributes);
			this.target = target;
			this.operand = operand;
		}

		public String toString() {
			return "assign %" + target + " = %" + operand;
		}
	}
	
	public static final class Constant extends Code {
		public final Object value;
		public final int target;

		public Constant(int target, Object val, Attribute... attributes) {
			super(attributes);
			this.target = target;
			this.value = val;
		}

		public String toString() {
			return "const %" + target + " = " + value.toString();
		}
	}
	
	public static final class Constructor extends Code {
		public final int target;
		public final int operand;
		public final String name;

		public Constructor(int target, int operand, String name,
				Attribute... attributes) {
			super(attributes);
			this.target = target;
			this.operand = operand;
			this.name = name;
		}

		public String toString() {
			return "construct %" + target + " = " + name + "(%" + operand + ")";
		}
	}
	
	public static final class BinOp extends Code {
		public BOp op;
		public final int target;
		public final int lhs;
		public final int rhs;

		public BinOp(BOp op, int target, int lhs, int rhs,
				Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.target = target;
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public String toString() {
			return op + " %" + target + " = %" + lhs + ", %" + rhs;
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
	public static final class ListAccess extends Code {
		public final int target;
		public final int src;
		public final int index;

		public ListAccess(int target, int src, int index, Attribute... attributes) {
			super(attributes);
			this.target = target;
			this.src = src;
			this.index = index;
		}

		public String toString() {
			return "indexof %" + target + " = %" + src + "[%" + index + "]";
		}
	}
		
	public static final class UnOp extends Code {
		public final UOp op;
		public final int target;
		public final int operand;

		public UnOp(UOp op, int target, int operand, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.target = target;
			this.operand = operand;
		}

		public String toString() {
			return op.toString() + " %" + target + " = %" + operand;
		}
	}
	
	public enum UOp {
		NOT {
			public String toString() { return "not"; }
		},
		NEG {
			public String toString() { return "neg"; }
		},
		DEREF {
			public String toString() { return "deref"; }
		},
		LENGTHOF {
			public String toString() { return "length"; }
		}		
	}	
	
	public static final class NaryOp extends Code {
		public final NOp op;
		public final int target;
		public final int[] operands;
		
		public NaryOp(NOp nop, int target, int[] operands, Attribute... attributes) {
			super(attributes);
			this.op = nop;
			this.target = target;
			this.operands = operands;
		}
		
		public NaryOp(NOp nop, int target, java.util.List<Integer> operands,
				Attribute... attributes) {
			super(attributes);
			this.op = nop;
			this.target = target;
			this.operands = new int[operands.size()];
			for (int i = 0; i != operands.size(); ++i) {
				this.operands[i] = operands.get(i);
			}
		}
		
		public String toString() {
			switch(op) {				 
				case LISTGEN:
				{
					String args = "";
					boolean firstTime=true;
					for(int e : operands) {
						if(!firstTime) {
							args += ",";
						}
						args += "%" + e;
					}
					return "[" + args + "]";
				}	
				default:
					return "";
			}
		}
	}
	
	public enum NOp {
		LISTGEN					
	}
	
	public static final class Rewrite extends Code {
		public final int target;		
		public final int operand;

		public Rewrite(int target, int operand, Attribute... attributes) {
			super(attributes);
			this.target = target;			
			this.operand = operand;
		}

		public String toString() {
			return "rewrite *%" + target + " => %" + operand;
		}
	}
	
	public static final class Return extends Code {		
		public final int operand;

		public Return(int operand, Attribute... attributes) {
			super(attributes);			
			this.operand = operand;
		}

		public String toString() {
			return "return %" + operand;
		}
	}
}
