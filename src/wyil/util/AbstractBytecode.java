package wyil.util;

import java.util.Arrays;

import wybs.lang.NameID;
import wyil.lang.Bytecode;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.Bytecode.Blocks;
import wyil.lang.Bytecode.Extras;
import wyil.lang.Bytecode.OperandGroups;
import wyil.lang.Bytecode.Operands;
import wyil.lang.Bytecode.Schema;


public abstract class AbstractBytecode {
	private final int[] operands;
	private final int[][] operandGroups;
	private final int[] blocks;

	public AbstractBytecode() {
		this.operands = null;
		this.operandGroups = null;
		this.blocks = null;
	}

	public AbstractBytecode(int operand) {
		this.operands = new int[] { operand };
		this.operandGroups = null;
		this.blocks = null;
	}
	
	public AbstractBytecode(int[] operands) {
		this.operands = operands;
		this.operandGroups = null;
		this.blocks = null;
	}
	
	public AbstractBytecode(int[][] operandGroups) {
		this.operands = null;
		this.operandGroups = operandGroups;
		this.blocks = null;
	}
	
	public AbstractBytecode(int operand, int[][] operandGroups) {
		this.operands = new int[] { operand };
		this.operandGroups = operandGroups;
		this.blocks = null;
	}

	public AbstractBytecode(int[] operands, int[][] operandGroups) {
		this.operands = operands;
		this.operandGroups = operandGroups;
		this.blocks = null;
	}

	public AbstractBytecode(int operand, int[][] operandGroups, int[] blocks) {
		this.operands = new int[] {operand};
		this.operandGroups = operandGroups;
		this.blocks = blocks;
	}
	
	public AbstractBytecode(int[] operands, int[][] operandGroups, int[] blocks) {
		this.operands = operands;
		this.operandGroups = operandGroups;
		this.blocks = blocks;
	}

	
	@Override
	public int hashCode() {
		return getOpcode() ^ Arrays.hashCode(getOperands()) & Arrays.deepHashCode(operandGroups);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AbstractBytecode) {
			AbstractBytecode bo = (AbstractBytecode) o;
			return getOpcode() == bo.getOpcode() && Arrays.equals(getOperands(), bo.getOperands())
					&& Arrays.deepEquals(operandGroups, operandGroups) && Arrays.equals(blocks, bo.blocks);
		}
		return false;
	}

	/**
	 * Return the opcode value of this bytecode.
	 * 
	 * @return
	 */
	public abstract int getOpcode();

	/**
	 * Return the top-level operands in this bytecode.
	 *
	 * @return
	 */
	public int[] getOperands() {
		if(operands == null) {
			return new int[0];
		} else {
			return operands;
		}
	}

	/**
	 * Return the number of top-level operands in this bytecode
	 * @return
	 */
	public int numberOfOperands() {
		if(operands == null) {
			return 0;
		} else {
			return operands.length;
		}
	}
	
	/**
	 * Return the ith top-level operand in this bytecode.
	 * 
	 * @param i
	 * @return
	 */
	public int getOperand(int i) {
		return operands[i];
	}

	/**
	 * Get the number of operand groups in this bytecode
	 * 
	 * @return
	 */
	public int numberOfOperandGroups() {
		if(operandGroups == null) {
			return 0;
		} else {
			return operandGroups.length;
		}
	}

	/**
	 * Get the ith operand group in this bytecode
	 * 
	 * @param i
	 * @return
	 */
	public int[] getOperandGroup(int i) {
		return operandGroups[i];
	}

	/**
	 * Determine the number of blocks contained in this bytecode.
	 * 
	 * @return
	 */
	public int numberOfBlocks() {
		if(blocks == null) {
			return 0;
		} else {
			return blocks.length;
		}
	}

	/**
	 * Get the ith block contained in this statement
	 * 
	 * @param i
	 * @return
	 */
	public int getBlock(int i) {
		return blocks[i];
	}

	/**
	 * Get the blocks contained in this statement
	 * 
	 * @param i
	 * @return
	 */
	public int[] getBlocks() {
		if(blocks == null) {
			return new int[0];
		} else {
			return blocks;
		}
	}
	
	/**
	 * ==================================================================
	 * Individual Bytecode Schemas
	 * ==================================================================
	 */

	public static final Schema[] schemas = new Schema[255];

	static {
		//		
		schemas[Bytecode.OPCODE_add] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.ADD);
			}
		};
		schemas[Bytecode.OPCODE_aliasdecl] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.AliasDeclaration(operands[0]);
			}
		};
		schemas[Bytecode.OPCODE_array] = new Schema(Operands.MANY){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.ARRAYCONSTRUCTOR);
			}
		};
		schemas[Bytecode.OPCODE_arrayindex] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands,Bytecode.OperatorKind.ARRAYINDEX);
			}
		};
		schemas[Bytecode.OPCODE_arraygen] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands,Bytecode.OperatorKind.ARRAYGENERATOR);
			}
		};
		schemas[Bytecode.OPCODE_arraylength] = new Schema(Operands.ONE) {
			public Bytecode construct(int opcode, int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.ARRAYLENGTH);
			}
		};
		schemas[Bytecode.OPCODE_assert] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Assert(operands[0]);
			}
		};
		schemas[Bytecode.OPCODE_assign] = new Schema(Operands.ZERO, OperandGroups.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {				
				return new Bytecode.Assign(groups[0],groups[1]);
			}
		};
		schemas[Bytecode.OPCODE_assume] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Assume(operands[0]);
			}
		};	
		schemas[Bytecode.OPCODE_bitwiseinvert] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.BITWISEINVERT);
			}
		};
		schemas[Bytecode.OPCODE_bitwiseor] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.BITWISEOR);
			}
		};
		schemas[Bytecode.OPCODE_bitwisexor] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.BITWISEXOR);
			}
		};
		schemas[Bytecode.OPCODE_bitwiseand] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.BITWISEAND);
			}
		};
		schemas[Bytecode.OPCODE_block] = new Schema(Operands.MANY){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Block(operands);
			}
		};
		schemas[Bytecode.OPCODE_break] = new Schema(Operands.ZERO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Break();
			}
		};
		schemas[Bytecode.OPCODE_const] = new Schema(Operands.ZERO, Extras.CONSTANT){
			public Bytecode construct(int opcode, int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Const((Constant) extras[0]);
			}
		};
		schemas[Bytecode.OPCODE_continue] = new Schema(Operands.ZERO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Continue();
			}
		};
		schemas[Bytecode.OPCODE_convert] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {				
				return new Bytecode.Convert(operands[0]);
			}
		};
		schemas[Bytecode.OPCODE_debug] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Debug(operands[0]);
			}
		};
		schemas[Bytecode.OPCODE_dereference] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.DEREFERENCE);
			}
		};
		schemas[Bytecode.OPCODE_div] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.DIV);
			}
		};
		schemas[Bytecode.OPCODE_dowhile] = new Schema(Operands.ONE, OperandGroups.TWO, Blocks.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				int body = blocks[0];
				int condition = operands[0];
				int[] invariants = groups[0];
				int[] modified = groups[1];
				return new Bytecode.DoWhile(body,condition,invariants,modified);
			}
		};
		schemas[Bytecode.OPCODE_eq] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.EQ);
			}
		};
		schemas[Bytecode.OPCODE_if] = new Schema(Operands.ONE, OperandGroups.ZERO, Blocks.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				int trueBranch = blocks[0];
				return new Bytecode.If(operands[0], trueBranch);					
			}
		};
		schemas[Bytecode.OPCODE_ifelse] = new Schema(Operands.ONE, OperandGroups.ZERO, Blocks.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				int trueBranch = blocks[0];
				int falseBranch = blocks[1];
				return new Bytecode.If(operands[0], trueBranch, falseBranch);
			}
		};
		schemas[Bytecode.OPCODE_fail] = new Schema(Operands.ZERO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Fail();
			}
		};
		schemas[Bytecode.OPCODE_fieldload] = new Schema(Operands.ONE, Extras.STRING){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.FieldLoad(operands[0], (String) extras[0]);
			}
		};
		schemas[Bytecode.OPCODE_gt] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.GT);
			}
		};
		schemas[Bytecode.OPCODE_ge] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.GTEQ);
			}
		};
		schemas[Bytecode.OPCODE_invoke] = new Schema(Operands.MANY, Extras.TYPE, Extras.NAME) {
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Invoke((Type.FunctionOrMethod) extras[0], operands, (NameID) extras[1]);
			}
		};
		schemas[Bytecode.OPCODE_indirectinvoke] = new Schema(Operands.ONE, OperandGroups.ONE, Extras.TYPE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.IndirectInvoke((Type.FunctionOrMethod) extras[0], operands[0], groups[0]);
			}
		};
		schemas[Bytecode.OPCODE_is] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.IS);
			}
		};
		schemas[Bytecode.OPCODE_lambda] = new Schema(Operands.ONE, OperandGroups.TWO, Extras.TYPE) {
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				Type.FunctionOrMethod type = (Type.FunctionOrMethod) extras[0];
				int body = operands[0];
				int[] parameters = groups[0];
				int[] environment = groups[1];
				return new Bytecode.Lambda(type,body,parameters,environment);
			}
		};
		schemas[Bytecode.OPCODE_lt] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.LT);
			}
		};
		schemas[Bytecode.OPCODE_le] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.LTEQ);
			}
		};
		schemas[Bytecode.OPCODE_logicalor] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.OR);
			}
		};
		schemas[Bytecode.OPCODE_logicaland] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.AND);
			}
		};
		schemas[Bytecode.OPCODE_logicalnot] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.NOT);
			}
		};
		schemas[Bytecode.OPCODE_mul] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.MUL);
			}
		};
		schemas[Bytecode.OPCODE_namedblock] = new Schema(Operands.ZERO, OperandGroups.ZERO, Blocks.ONE, Extras.STRING) {
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				String name = (String) extras[0];
				return new Bytecode.NamedBlock(blocks[0],name);
			}
		};
		schemas[Bytecode.OPCODE_ne] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.NEQ);
			}
		};
		schemas[Bytecode.OPCODE_neg] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.NEG);
			}
		};
		schemas[Bytecode.OPCODE_newobject] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands,Bytecode.OperatorKind.NEW);
			}
		};
		schemas[Bytecode.OPCODE_record] = new Schema(Operands.MANY){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.RECORDCONSTRUCTOR);
			}
		};
		schemas[Bytecode.OPCODE_rem] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.REM);
			}
		};
		schemas[Bytecode.OPCODE_return] = new Schema(Operands.MANY){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Return(operands);
			}
		};
		schemas[Bytecode.OPCODE_shl] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.LEFTSHIFT);
			}
		};
		schemas[Bytecode.OPCODE_shr] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.RIGHTSHIFT);
			}
		};
		schemas[Bytecode.OPCODE_skip] = new Schema(Operands.ZERO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Skip();
			}
		};
		schemas[Bytecode.OPCODE_sub] = new Schema(Operands.TWO){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.Operator(operands, Bytecode.OperatorKind.SUB);
			}
		};
		schemas[Bytecode.OPCODE_switch] = new Schema(Operands.ONE, OperandGroups.ZERO, Extras.SWITCH_ARRAY) {
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				Bytecode.Case[] cases = (Bytecode.Case[]) extras[0];
				return new Bytecode.Switch(operands[0], cases);
			}
		};
		schemas[Bytecode.OPCODE_vardecl] = new Schema(Operands.ZERO,Extras.STRING){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				String name = (String) extras[0];
				return new Bytecode.VariableDeclaration(name);
			}
		};
		schemas[Bytecode.OPCODE_vardeclinit] = new Schema(Operands.ONE,Extras.STRING){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				String name = (String) extras[0];
				return new Bytecode.VariableDeclaration(name,operands[0]);
			}
		};
		schemas[Bytecode.OPCODE_varaccess] = new Schema(Operands.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				return new Bytecode.VariableAccess(operands[0]);
			}
		};
		schemas[Bytecode.OPCODE_while] = new Schema(Operands.ONE, OperandGroups.TWO, Blocks.ONE){
			public Bytecode construct(int opcode,int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				int body = blocks[0];
				int condition = operands[0];
				int[] invariants = groups[0];
				int[] modified = groups[1];
				return new Bytecode.While(body,condition,invariants,modified);
			}
		};
		// Quantifiers
		schemas[Bytecode.OPCODE_some] = schemas[Bytecode.OPCODE_all] = new Schema(
				Operands.ONE, OperandGroups.MANY) {
			public Bytecode construct(int opcode, int[] operands, int[][] groups, int[] blocks, Object[] extras) {
				int body = operands[0];
				Bytecode.Range[] ranges = new Bytecode.Range[groups.length];
				for (int i = 0; i != ranges.length; i = i + 1) {
					int[] group = groups[i];
					ranges[i] = new Bytecode.Range(group[0],group[1],group[2]);
				}
				Bytecode.QuantifierKind kind;
				switch(opcode) {
				case Bytecode.OPCODE_some:
					kind = Bytecode.QuantifierKind.SOME;
					break;
				case Bytecode.OPCODE_all:
					kind = Bytecode.QuantifierKind.ALL;
					break;
				default:
					// deadcpde
					throw new IllegalArgumentException();
				}
				return new Bytecode.Quantifier(kind, body, ranges);
			}
		};		
	}
}
