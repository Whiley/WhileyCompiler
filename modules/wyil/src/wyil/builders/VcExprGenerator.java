package wyil.builders;

import static wyil.util.ErrorMessages.errorMessage;
import static wyil.util.ErrorMessages.internalFailure;
import static wyil.util.ErrorMessages.syntaxError;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import wybs.lang.Builder;
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.SyntaxError;
import wycc.lang.SyntaxError.InternalFailure;
import wycs.core.Value;
import wycs.syntax.Expr;
import wycs.syntax.SyntacticType;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.Bytecode;
import wyil.lang.BytecodeForest;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.util.ErrorMessages;

public class VcExprGenerator {
	private final String filename;
	private final Builder builder;
	private final VcUtils utils;
	
	public VcExprGenerator(String filename, Builder builder, VcUtils utils) {
		this.filename = filename;
		this.builder = builder;
		this.utils = utils;
	}
	/**
	 * Dispatch transform over unit bytecodes. Each unit bytecode is guaranteed
	 * to continue afterwards, and not to fork any new branches.
	 * 
	 * @param code
	 *            The bytecode being transformed over.
	 * @param block
	 *            The root block being iterated over.
	 * @param branch
	 *            The branch on entry to the bytecode.
	 */
	public void transform(Bytecode code, BytecodeForest forest, VcBranch branch) {
		try {
			if (code instanceof Bytecode.Operator) {
				transform((Bytecode.Operator) code, forest, branch);				
			} else if (code instanceof Bytecode.Convert) {
				transform((Bytecode.Convert) code, forest, branch);
			} else if (code instanceof Bytecode.Const) {
				transform((Bytecode.Const) code, forest, branch);
			} else if (code instanceof Bytecode.Debug) {
				// skip
			} else if (code instanceof Bytecode.FieldLoad) {
				transform((Bytecode.FieldLoad) code, forest, branch);
			} else if (code instanceof Bytecode.IndirectInvoke) {
				transform((Bytecode.IndirectInvoke) code, forest, branch);
			} else if (code instanceof Bytecode.Invoke) {
				transform((Bytecode.Invoke) code, forest, branch);
			} else if (code instanceof Bytecode.Label) {
				// skip
			} else if (code instanceof Bytecode.Update) {
				transform((Bytecode.Update) code, forest, branch);
			} else if (code instanceof Bytecode.Lambda) {
				transform((Bytecode.Lambda) code, forest, branch);
			} else {
				internalFailure("unknown: " + code.getClass().getName(), filename,
						forest.get(branch.pc()).attributes());
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (SyntaxError e) {
			throw e;
		} catch (Throwable e) {
			internalFailure(e.getMessage(), filename, e, forest.get(branch.pc()).attributes());
		}
	}
	
	/**
	 * Maps unary bytecodes into expression opcodes.
	 */
	private static Expr.Unary.Op[] unaryOperatorMap = {
			Expr.Unary.Op.NEG, // neg
			Expr.Unary.Op.NOT, // not
			null, // invert
			null, // deref
			Expr.Unary.Op.LENGTHOF
	};
	
	/**
	 * Maps binary bytecodes into expression opcodes.
	 */
	private static Expr.Binary.Op[] binaryOperatorMap = {
			null, // neg
			null, // not
			null, // invert
			null, // deref
			null, // lengthof
			Expr.Binary.Op.ADD,
			Expr.Binary.Op.SUB, 
			Expr.Binary.Op.MUL, 
			Expr.Binary.Op.DIV,
			Expr.Binary.Op.REM,
			Expr.Binary.Op.EQ,
			Expr.Binary.Op.NEQ,
			Expr.Binary.Op.LT,
			Expr.Binary.Op.LTEQ,
			Expr.Binary.Op.GT,
			Expr.Binary.Op.GTEQ,
			null, // bitwise or
			null, // bitwise xor
			null, // bitwise and
			null, // left shift
			null // right shift
	};

	protected void transform(Bytecode.Operator code, BytecodeForest forest, VcBranch branch) {
		switch(code.kind()) {
		case ASSIGN:
			for (int i = 0; i != code.operands().length; ++i) {
				branch.write(code.target(i), branch.read(code.operand(i)));
			}
			break;
		case NOT:
		case NEG:
		case ARRAYLENGTH: {
			Bytecode.Operator bc = (Bytecode.Operator) code;
			transformUnary(unaryOperatorMap[code.kind().ordinal()], bc, branch, forest);
			break;
		}
		case BITWISEINVERT: 
		case DEREFERENCE: {
			branch.havoc(code.target(0));
			break;
		}
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case REM:
		case EQ:
		case NEQ:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ: {
			transformBinary(binaryOperatorMap[code.kind().ordinal()], code, branch, forest);
			break;
		}
		case BITWISEAND:
		case BITWISEOR:
		case BITWISEXOR: 
		case LEFTSHIFT:
		case RIGHTSHIFT: {
			branch.havoc(code.target(0));
			break;
		}
		case ARRAYINDEX: {
			Expr src = branch.read(code.operand(0));
			Expr idx = branch.read(code.operand(1));
			branch.write(code.target(0),
					new Expr.IndexOf(src, idx, VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes())));
			break;
		}
		case ARRAYGENERATOR:
			transformArrayGenerator(code,forest,branch);
			break;
		case ARRAYCONSTRUCTOR:
			transformNary(Expr.Nary.Op.ARRAY,code,branch,forest);
			break;
		case RECORDCONSTRUCTOR:
			transformNary(Expr.Nary.Op.TUPLE,code,branch,forest);
			break;
		case NEW:
			branch.havoc(code.target(0));
			break;
		}
	}
	
	protected void transform(Bytecode.Convert code, BytecodeForest forest, VcBranch branch) {
		Collection<Attribute> attributes = VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes());
		Expr result = branch.read(code.operand(0));
		SyntacticType type = utils.convert(code.result(), forest.get(branch.pc()).attributes());
		branch.write(code.target(0), new Expr.Cast(type, result, attributes));
	}

	protected void transform(Bytecode.Const code, BytecodeForest forest, VcBranch branch) {
		Value val = utils.convert(code.constant(), forest, branch);
		branch.write(code.target(), new Expr.Constant(val, VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes())));
	}

	protected void transform(Bytecode.Debug code, BytecodeForest forest,
			VcBranch branch) {
		// do nout
	}

	protected void transform(Bytecode.FieldLoad code, BytecodeForest forest, VcBranch branch) {
		Type.EffectiveRecord er = (Type.EffectiveRecord) code.type(0); 
		ArrayList<String> fields = new ArrayList<String>(er.fields().keySet());
		Collections.sort(fields);
		Expr src = branch.read(code.operand(0));
		Expr index = new Expr.Constant(Value.Integer(BigInteger.valueOf(fields.indexOf(code.fieldName()))));
		Expr result = new Expr.IndexOf(src, index, VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes()));
		branch.write(code.target(0), result);
	}

	protected void transform(Bytecode.IndirectInvoke code,
			BytecodeForest forest, VcBranch branch) {
		for(int target : code.targets()) {
			branch.havoc(target);
		}
	}

	protected void transform(Bytecode.Invoke code, BytecodeForest forest,
			VcBranch branch) throws Exception {
		Collection<wyil.lang.Attribute> attributes =  forest.get(branch.pc()).attributes();
		Collection<Attribute> wyccAttributes = VcUtils.toWycsAttributes(attributes);
		int[] code_operands = code.operands();
		int[] targets = code.targets();
		
		if (targets.length > 0) {
			// Need to assume the post-condition holds.
			Expr[] operands = new Expr[code_operands.length];
			for (int i = 0; i != code_operands.length; ++i) {
				operands[i] = branch.read(code_operands[i]);
			}
			Expr argument = operands.length == 1 ? operands[0] : new Expr.Nary(
					Expr.Nary.Op.TUPLE, operands,wyccAttributes);
			branch.write(code.targets()[0], new Expr.Invoke(code.name().name(),
					code.name().module(), Collections.EMPTY_LIST, argument,
					wyccAttributes));

			// This is a potential fix for #488, although it doesn't work
			// FIXME: needs to updated to handle multiple returns as well
			if (utils.containsNominal(code.type(0).returns().get(0), attributes)) {
				// This is required to handle the implicit constraints implied
				// by a nominal type. See #488.
				Expr nominalTest = new Expr.Is(branch.read(code.targets()[0]),
						utils.convert(code.type(0).returns().get(0), attributes));
				branch.assume(nominalTest);
			}

			// Here, we must find the name of the corresponding postcondition so
			// that we can assume it.
			int numPostconditions = countPostconditions(code.name(), code.type(0), forest, branch);

			if (numPostconditions > 0) {
				// To assume the post-condition holds after the method, we
				// simply called the corresponding post-condition macros.
				Expr[] arguments = new Expr[operands.length + targets.length];
				System.arraycopy(operands, 0, arguments, 0, operands.length);
				for(int i=0;i!=targets.length;++i) {
					arguments[operands.length+i] = branch.read(targets[i]);						
				}				
				String prefix = code.name().name() + "_ensures_";
				for (int i = 0; i != numPostconditions; ++i) {
					Expr.Invoke macro = new Expr.Invoke(prefix + i,
							code.name().module(), Collections.EMPTY_LIST,
							new Expr.Nary(Expr.Nary.Op.TUPLE, arguments));
					branch.assume(macro);
				}
			}
		}
	}

	protected void transformArrayGenerator(Bytecode.Operator code, BytecodeForest forest, VcBranch branch) {
		Type elementType = ((Type.Array) code.type(0)).element();
		Collection<wyil.lang.Attribute> wyilAttributes = forest.get(branch.pc()).attributes();
		Collection<Attribute> attributes = VcUtils.toWycsAttributes(wyilAttributes);
		Expr element = branch.read(code.operand(0));
		Expr count = branch.read(code.operand(1));
		branch.havoc(code.target(0));
		Expr arg = new Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { branch.read(code.target(0)), element, count },
				attributes);
		ArrayList<SyntacticType> generics = new ArrayList<SyntacticType>();
		generics.add(utils.convert(elementType, wyilAttributes));
		Expr.Invoke macro = new Expr.Invoke("generate", Trie.fromString("wycs/core/Array"), generics, arg);
		branch.assume(macro);
	}
	
	protected void transform(Bytecode.Lambda code, BytecodeForest forest, VcBranch branch) {
		// TODO: implement lambdas somehow?
		branch.havoc(code.target(0));
	}

	protected void transform(Bytecode.Update code, BytecodeForest forest, VcBranch branch) {
		Expr result = branch.read(code.result());
		Expr oldSource = branch.read(code.target(0));
		Expr newSource = branch.havoc(code.target(0));
		updateHelper(code.iterator(), oldSource, newSource, result, branch, forest);
	}

	protected void updateHelper(Iterator<Bytecode.LVal> iter, Expr oldSource, Expr newSource, Expr result, VcBranch branch,
			BytecodeForest forest) {
		Collection<Attribute> attributes = VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes());
		if (!iter.hasNext()) {
			branch.assume(new Expr.Binary(Expr.Binary.Op.EQ, newSource, result, attributes));
		} else {
			Bytecode.LVal lv = iter.next();
			if (lv instanceof Bytecode.RecordLVal) {
				Bytecode.RecordLVal rlv = (Bytecode.RecordLVal) lv;
				ArrayList<String> fields = new ArrayList<String>(rlv.rawType().fields().keySet());
				Collections.sort(fields);
				int index = fields.indexOf(rlv.field);
				for (int i = 0; i != fields.size(); ++i) {
					Expr j = new Expr.Constant(Value.Integer(BigInteger.valueOf(i)));
					Expr oldS = new Expr.IndexOf(oldSource, j, attributes);
					Expr newS = new Expr.IndexOf(newSource, j, attributes);
					if (i != index) {
						branch.assume(new Expr.Binary(Expr.Binary.Op.EQ, oldS, newS, attributes));
					} else {
						updateHelper(iter, oldS, newS, result, branch, forest);
					}
				}
			} else if (lv instanceof Bytecode.ArrayLVal) {
				Bytecode.ArrayLVal rlv = (Bytecode.ArrayLVal) lv;
				Expr index = branch.read(rlv.indexOperand);
				Expr oldS = new Expr.IndexOf(oldSource, index, attributes);
				Expr newS = new Expr.IndexOf(newSource, index, attributes);
				updateHelper(iter, oldS, newS, result, branch, forest);
				Expr arg = new Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { oldSource, newSource, index }, attributes);
				ArrayList<SyntacticType> generics = new ArrayList<SyntacticType>();
				generics.add(utils.convert(rlv.rawType().element(),Collections.EMPTY_LIST));
				Expr.Invoke macro = new Expr.Invoke("update", Trie.fromString("wycs/core/Array"), generics, arg);
				branch.assume(macro);
			}
		}
	}
	
	/**
	 * Transform an assignable unary bytecode using a given target operator.
	 * This must read the operand and then create the appropriate target
	 * expression. Finally, the result of the bytecode must be written back to
	 * the enclosing branch.
	 * 
	 * @param operator
	 *            --- The target operator
	 * @param code
	 *            --- The bytecode being translated
	 * @param branch
	 *            --- The enclosing branch
	 */
	protected void transformUnary(Expr.Unary.Op operator, Bytecode code, VcBranch branch,
			BytecodeForest forest) {
		Expr lhs = branch.read(code.operand(0));
		branch.write(code.target(0),
				new Expr.Unary(operator, lhs, VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes())));
	}

	/**
	 * Transform an assignable binary bytecode using a given target operator.
	 * This must read both operands and then create the appropriate target
	 * expression. Finally, the result of the bytecode must be written back to
	 * the enclosing branch.
	 * 
	 * @param operator
	 *            --- The target operator
	 * @param code
	 *            --- The bytecode being translated
	 * @param branch
	 *            --- The enclosing branch
	 */
	protected void transformBinary(Expr.Binary.Op operator, Bytecode code, VcBranch branch,
			BytecodeForest forest) {
		Expr lhs = branch.read(code.operand(0));
		Expr rhs = branch.read(code.operand(1));

		if (operator != null) {
			branch.write(code.target(0),
					new Expr.Binary(operator, lhs, rhs, VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes())));
		} else {
			// In this case, we have a binary operator which we don't know how
			// to translate into WyCS. Therefore, we need to invalidate the
			// target register to signal this.
			branch.havoc(code.target(0));
		}
	}

	/**
	 * Transform an assignable nary bytecode using a given target operator. This
	 * must read all operands and then create the appropriate target expression.
	 * Finally, the result of the bytecode must be written back to the enclosing
	 * branch.
	 * 
	 * @param operator
	 *            --- The target operator
	 * @param code
	 *            --- The bytecode being translated
	 * @param branch
	 *            --- The enclosing branch
	 */
	protected void transformNary(Expr.Nary.Op operator, Bytecode code, VcBranch branch,
			BytecodeForest forest) {
		int[] code_operands = code.operands();
		Expr[] vals = new Expr[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		branch.write(code.target(0), new Expr.Nary(operator, vals, VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes())));
	}

	/**
	 * Find the postcondition associated with a given function or method. This
	 * maybe contained in the same file, or in a different file. This may
	 * require loading that file in memory to access this information.
	 * 
	 * @param name
	 *            --- Fully qualified name of function
	 * @param fun
	 *            --- Type of fucntion.
	 * @param block
	 *            --- Enclosing block (for debugging purposes).
	 * @param branch
	 *            --- Enclosing branch (for debugging purposes).
	 * @return
	 * @throws Exception
	 */
	private int countPostconditions(NameID name, Type.FunctionOrMethod fun,
			BytecodeForest forest, VcBranch branch) throws Exception {
		Path.Entry<WyilFile> e = builder.project().get(name.module(), WyilFile.ContentType);
		if (e == null) {
			syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, name.module().toString()), filename,
					forest.get(branch.pc()).attributes());
		}
		WyilFile m = e.read();
		WyilFile.FunctionOrMethod method = m.functionOrMethod(name.name(), fun);
		return method.postconditions().length;
	}
}
