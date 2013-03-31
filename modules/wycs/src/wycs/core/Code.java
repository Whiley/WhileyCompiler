package wycs.core;

import java.util.Collection;

import wybs.lang.Attribute;
import wybs.lang.SyntacticElement;

public abstract class Code<T extends SemanticType> extends SyntacticElement.Impl {
	public final T type;
	public final Op opcode;
	public final Code<?>[] operands;
	
	public Code(T type, Op opcode, Code<?>[] operands, Attribute... attributes) {
		super(attributes);
		this.type = type;
		this.opcode = opcode;
		this.operands = operands;		
	}
	
	public Code(T type, Op opcode, Code<?>[] operands, Collection<Attribute> attributes) {
		super(attributes);
		this.type = type;
		this.opcode = opcode;
		this.operands = operands;		
	}
	
	public static enum Op {
		VAR(0),
		CONST(1),
		NOT(2),
		NEG(3),
		LENGTH(4),
		ADD(5),
		SUB(6),
		MUL(7),
		DIV(8),
		REM(9),
		EQ(10),
		NEQ(11),
		LT(12),
		LTEQ(13),
		IN(14),
		SUBSET(15),
		SUBSETEQ(16),
		AND(17),
		OR(18),
		TUPLE(19),
		SET(20),
		LOAD(21),
		EXISTS(22),
		FORALL(23);
		
		public int offset;

		private Op(int offset) {
			this.offset = offset;
		}
	}
	
	private static Code[] NO_OPERANDS = new Code[0];
	
	public final class Variable extends Code<SemanticType> {
		public final int index;
		
		private Variable(SemanticType type, Code<?>[] operands, int index,
				Attribute... attributes) {
			super(type, Op.VAR, operands, attributes);
			this.index = index;
		}

		private Variable(SemanticType type, Code<?>[] operands, int index,
				Collection<Attribute> attributes) {
			super(type, Op.VAR, operands, attributes);
			this.index = index;
		}
	}
	
	public final class Constant extends Code<SemanticType> {
		public final Value value;
		
		private Constant(Value value, Attribute... attributes) {
			super(value.type(),Op.CONST,NO_OPERANDS,attributes);			
			this.value = value;
		}
		
		private Constant(Value value, Collection<Attribute> attributes) {
			super(value.type(), Op.CONST, NO_OPERANDS, attributes);
			this.value = value;
		}
	}
	
	public final class Unary extends Code<SemanticType> {
		private Unary(SemanticType type, Op opcode, Code<?> operand,
				Attribute... attributes) {
			super(type, opcode, new Code[] { operand }, attributes);
			if (opcode.offset < Op.NOT.offset
					|| opcode.offset > Op.LENGTH.offset) {
				throw new IllegalArgumentException(
						"invalid opcode for Unary constructor");
			}
		}

		private Unary(SemanticType type, Op opcode, Code<?> operand,
				Collection<Attribute> attributes) {
			super(type, opcode, new Code[] { operand }, attributes);
			if (opcode.offset < Op.NOT.offset
					|| opcode.offset > Op.LENGTH.offset) {
				throw new IllegalArgumentException(
						"invalid opcode for Unary constructor");
			}
		}
	}
	
	public final class Binary extends Code<SemanticType> {
		private Binary(SemanticType type, Op opcode, Code<?> leftOperand,
				Code<?> rightOperand, Attribute... attributes) {
			super(type, opcode, new Code[] { leftOperand, rightOperand },
					attributes);
			if (opcode.offset < Op.ADD.offset
					|| opcode.offset > Op.SUBSETEQ.offset) {
				throw new IllegalArgumentException(
						"invalid opcode for Unary constructor");
			}
		}

		private Binary(SemanticType type, Op opcode, Code<?> leftOperand,
				Code rightOperand, Collection<Attribute> attributes) {
			super(type, opcode, new Code[] { leftOperand, rightOperand },
					attributes);
			if (opcode.offset < Op.ADD.offset
					|| opcode.offset > Op.SUBSETEQ.offset) {
				throw new IllegalArgumentException(
						"invalid opcode for Binary constructor");
			}
		}
	}
	
	public final class Nary extends Code<SemanticType> {
		private Nary(SemanticType type, Op opcode, Code<?>[] operands,
				Attribute... attributes) {
			super(type, opcode, operands, attributes);
			if (opcode.offset < Op.AND.offset || opcode.offset > Op.SET.offset) {
				throw new IllegalArgumentException(
						"invalid opcode for Nary constructor");
			}
		}

		private Nary(SemanticType type, Op opcode, Code<?>[] operands,
				Collection<Attribute> attributes) {
			super(type, opcode, operands, attributes);
			if (opcode.offset < Op.AND.offset || opcode.offset > Op.SET.offset) {
				throw new IllegalArgumentException(
						"invalid opcode for Nary constructor");
			}
		}
	}
	
	public final class Load extends Code<SemanticType.Tuple> {
		public final int index;

		private Load(SemanticType.Tuple type, Code<?> source, int index,
				Attribute... attributes) {
			super(type, Op.LOAD, new Code[] { source }, attributes);
			this.index = index;
		}

		private Load(SemanticType.Tuple type, Code<?> source, int index,
				Collection<Attribute> attributes) {
			super(type, Op.LOAD, new Code[] { source }, attributes);
			this.index = index;
		}
	}
	
	public final class Quantifier extends Code<SemanticType> {
		public final SemanticType[] types;
		
		private Quantifier(SemanticType type, SemanticType[] types, Op opcode,
				Code<?> operand, Attribute... attributes) {
			super(type, opcode, new Code[] { operand }, attributes);
			if (opcode != Op.EXISTS || opcode != Op.FORALL) {
				throw new IllegalArgumentException(
						"invalid opcode for quantifier constructor");
			}
			this.types = types;
		}
		
		private Quantifier(SemanticType type, SemanticType[] types, Op opcode,
				Code<?> operand, Collection<Attribute> attributes) {
			super(type, opcode, new Code[] { operand }, attributes);
			if (opcode != Op.EXISTS || opcode != Op.FORALL) {
				throw new IllegalArgumentException(
						"invalid opcode for quantifier constructor");
			}
			this.types = types;
		}
	}
}
