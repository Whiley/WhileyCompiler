package wyil.builders;

import static wyil.util.ErrorMessages.errorMessage;
import static wyil.util.ErrorMessages.internalFailure;
import static wyil.util.ErrorMessages.syntaxError;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import wybs.lang.Builder;
import wycc.lang.NameID;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wycs.core.Value;
import wycs.syntax.Expr;
import wycs.syntax.SyntacticType;
import wycs.syntax.TypePattern;
import wycs.syntax.WyalFile;
import wycs.syntax.WyalFile.Function;
import wyfs.lang.Path;
import wyil.attributes.VariableDeclarations;
import wyil.lang.Code;
import wyil.lang.CodeForest;
import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.util.ErrorMessages;
import wyil.util.TypeExpander;

public class VcUtils {
	private final String filename;
	private final Builder builder;
	private final TypeExpander expander;
	
	public VcUtils(String filename, Builder builder, TypeExpander expander) {
		this.filename = filename;
		this.builder = builder;
		this.expander = expander;
	}
	
	/**
	 * Convert a WyIL constant into its equivalent WyCS constant. In some cases,
	 * this is a direct translation. In other cases, WyIL constants are encoded
	 * using more primitive WyCS values.
	 * 
	 * @param c
	 *            --- The WyIL constant to be converted.
	 * @param block
	 *            --- The block within which this conversion is taking place
	 *            (for debugging purposes)
	 * @param branch
	 *            --- The branch within which this conversion is taking place
	 *            (for debugging purposes)
	 * @return
	 */
	public Value convert(Constant c, CodeForest forest, VcBranch branch) {
		if (c instanceof Constant.Null) {
			return wycs.core.Value.Null;
		} else if (c instanceof Constant.Bool) {
			Constant.Bool cb = (Constant.Bool) c;
			return wycs.core.Value.Bool(cb.value);
		} else if (c instanceof Constant.Byte) {
			Constant.Byte cb = (Constant.Byte) c;
			return wycs.core.Value.Integer(BigInteger.valueOf(cb.value));
		} else if (c instanceof Constant.Integer) {
			Constant.Integer cb = (Constant.Integer) c;
			return wycs.core.Value.Integer(cb.value);
		} else if (c instanceof Constant.Array) {
			Constant.Array cb = (Constant.Array) c;
			List<Constant> cb_values = cb.values;
			ArrayList<Value> items = new ArrayList<Value>();
			for (int i = 0; i != cb_values.size(); ++i) {
				items.add(convert(cb_values.get(i), forest, branch));				
			}
			return Value.Array(items);
		} else if (c instanceof Constant.Record) {
			Constant.Record rb = (Constant.Record) c;

			// NOTE:: records are currently translated into WyCS as tuples,
			// where
			// each field is allocated a slot based on an alphabetical sorting
			// of field names. It's unclear at this stage whether or not that is
			// a general solution. In particular, it would seem to be brokwn for
			// type testing.

			ArrayList<String> fields = new ArrayList<String>(rb.values.keySet());
			Collections.sort(fields);
			ArrayList<Value> values = new ArrayList<Value>();
			for (String field : fields) {
				values.add(convert(rb.values.get(field), forest, branch));
			}
			return wycs.core.Value.Tuple(values);
		} else {
			internalFailure("unknown constant encountered (" + c + ")",
					filename, forest.get(branch.pc()).attributes());
			return null;
		}
	}
	
	/**
	 * Construct a function prototype with a given name and type. The function
	 * can then be called elsewhere as an uninterpreted function. The function
	 * doesn't have a body but is used as a name to be referred to from
	 * assertions.
	 *
	 * @param wyalFile
	 *            --- the file onto which this function is created.
	 * @param name
	 *            --- the nameto give to the generated macro.
	 * @param params
	 *            --- parameter types to use.
	 * @param returns
	 *            --- return types to use
	 * @return
	 */
	public void createFunctionPrototype(WyalFile wyalFile, String name, List<Type> params, List<Type> returns) {

		TypePattern.Leaf[] parameterPatterns = new TypePattern.Leaf[params.size()];
		// second, set initial environment
		for (int i = 0; i != params.size(); ++i) {
			Expr.Variable v = new Expr.Variable("r" + i);
			// FIXME: what attributes to pass into convert?
			parameterPatterns[i] = new TypePattern.Leaf(convert(params.get(i),
					Collections.EMPTY_LIST), v);
		}
		TypePattern.Leaf[] returnPatterns = new TypePattern.Leaf[returns.size()];
		// second, set initial environment
		for (int i = 0; i != returns.size(); ++i) {
			Expr.Variable v = new Expr.Variable("r" + i);
			returnPatterns[i] = new TypePattern.Leaf(convert(returns.get(i),
					Collections.EMPTY_LIST), v);
		}
		// Construct the type declaration for the new block macro
		TypePattern from = new TypePattern.Tuple(parameterPatterns);
		TypePattern to = new TypePattern.Tuple(returnPatterns);

		wyalFile.add(wyalFile.new Function(name, Collections.EMPTY_LIST, from, to, null));
	}
	
	/**
	 * Convert a WyIL type into its equivalent WyCS type. In some cases, this is
	 * a direct translation. In other cases, WyIL constants are encoded using
	 * more primitive WyCS types.
	 * 
	 * @param t
	 *            --- The WyIL type to be converted.
	 * @param attributes
	 *            --- The attributes associated with the point of this
	 *            conversion. These are used for debugging purposes to associate
	 *            any errors generated with a source line.
	 * @return
	 */
	public SyntacticType convert(Type t, Collection<wyil.lang.Attribute> attributes) {
		// FIXME: this is fundamentally broken in the case of recursive types.
		// See Issue #298.
		if (t instanceof Type.Any) {
			return new SyntacticType.Any(toWycsAttributes(attributes));
		} else if (t instanceof Type.Void) {
			return new SyntacticType.Void(toWycsAttributes(attributes));
		} else if (t instanceof Type.Null) {
			return new SyntacticType.Null(toWycsAttributes(attributes));
		} else if (t instanceof Type.Bool) {
			return new SyntacticType.Bool(toWycsAttributes(attributes));
		} else if (t instanceof Type.Byte) {
			// FIXME: implement SyntacticType.Byte
			// return new SyntacticType.Byte(attributes(branch));
			return new SyntacticType.Int(toWycsAttributes(attributes));
		} else if (t instanceof Type.Int) {
			return new SyntacticType.Int(toWycsAttributes(attributes));
		} else if (t instanceof Type.Array) {
			Type.Array lt = (Type.Array) t;
			SyntacticType element = convert(lt.element(), attributes);
			// ugly.
			return new SyntacticType.List(element);
		} else if (t instanceof Type.Record) {
			Type.Record rt = (Type.Record) t;
			HashMap<String, Type> fields = rt.fields();
			ArrayList<String> names = new ArrayList<String>(fields.keySet());
			ArrayList<SyntacticType> elements = new ArrayList<SyntacticType>();
			Collections.sort(names);
			for (int i = 0; i != names.size(); ++i) {
				String field = names.get(i);
				elements.add(convert(fields.get(field), attributes));
			}
			return new SyntacticType.Tuple(elements);
		} else if (t instanceof Type.Reference) {
			// FIXME: how to translate this??
			return new SyntacticType.Any();
		} else if (t instanceof Type.Union) {
			Type.Union tu = (Type.Union) t;
			HashSet<Type> tu_elements = tu.bounds();
			ArrayList<SyntacticType> elements = new ArrayList<SyntacticType>();
			for (Type te : tu_elements) {
				elements.add(convert(te, attributes));
			}
			return new SyntacticType.Union(elements);
		} else if (t instanceof Type.Negation) {
			Type.Negation nt = (Type.Negation) t;
			SyntacticType element = convert(nt.element(), attributes);
			return new SyntacticType.Negation(element);
		} else if (t instanceof Type.FunctionOrMethod) {
			Type.FunctionOrMethod ft = (Type.FunctionOrMethod) t;
			return new SyntacticType.Any();
		} else if (t instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) t;
			NameID nid = nt.name();
			ArrayList<String> names = new ArrayList<String>();
			for (String pc : nid.module()) {
				names.add(pc);
			}
			names.add(nid.name());
			return new SyntacticType.Nominal(names,
					toWycsAttributes(attributes));
		} else {
			internalFailure("unknown type encountered (" + t.getClass().getName() + ")", filename, attributes);
			return null;
		}
	}

	/**
	 * Convert a list of WyIL attributes into a corresponding list of
	 * WycsAttributes. Note that, in some cases, no conversion is possible and
	 * such attributes are silently dropped.
	 * 
	 * @param branch
	 * @return
	 */
	public static Collection<wycc.lang.Attribute> toWycsAttributes(Collection<wyil.lang.Attribute> wyilAttributes) {
		ArrayList<wycc.lang.Attribute> wycsAttributes = new ArrayList<wycc.lang.Attribute>();
		// iterate each attribute and convert those which can be convered.
		for (wyil.lang.Attribute attr : wyilAttributes) {
			if (attr instanceof wyil.attributes.SourceLocation) {
				wyil.attributes.SourceLocation l = (wyil.attributes.SourceLocation) attr;
				wycsAttributes.add(new wycc.lang.Attribute.Source(l.start(), l.end(), 0));
			}
		}
		return wycsAttributes;
	}

	/**
	 * Generate the logically inverted expression corresponding to a given
	 * comparator. For example, inverting "<=" gives ">", inverting "==" gives
	 * "!=", etc.
	 *
	 * @param test
	 *            --- the binary comparator being inverted.
	 * @return
	 */
	public Expr invert(Expr.Binary test) {
		Expr.Binary.Op op;
		switch (test.op) {
		case EQ:
			op = Expr.Binary.Op.NEQ;
			break;
		case NEQ:
			op = Expr.Binary.Op.EQ;
			break;
		case GTEQ:
			op = Expr.Binary.Op.LT;
			break;
		case GT:
			op = Expr.Binary.Op.LTEQ;
			break;
		case LTEQ:
			op = Expr.Binary.Op.GT;
			break;
		case LT:
			op = Expr.Binary.Op.GTEQ;
			break;
		default:
			wycc.lang.SyntaxError.internalFailure("unknown comparator ("
					+ test.op + ")", filename, test);
			return null;
		}

		return new Expr.Binary(op, test.leftOperand, test.rightOperand,
				test.attributes());
	}
	

	/**
	 * Make an objective assessment as to whether a type may include an
	 * invariant or not. The purpose here is reduce the number of verification
	 * conditions generated with respect to constrained types. The algorithm is
	 * currently very simple. It essentially looks to see whether or not the
	 * type contains a nominal component. If so, the answer is "yes", otherwise
	 * the answer is "no".
	 * 
	 * @return
	 */
	public boolean containsNominal(Type t,
			Collection<wyil.lang.Attribute> attributes) {
		// FIXME: this is fundamentally broken in the case of recursive types.
		// See Issue #298.
		if (t instanceof Type.Any || t instanceof Type.Void
				|| t instanceof Type.Null || t instanceof Type.Bool
				|| t instanceof Type.Byte || t instanceof Type.Int) {
			return false;
		} else if (t instanceof Type.Array) {
			Type.Array lt = (Type.Array) t;
			return containsNominal(lt.element(), attributes);
		} else if (t instanceof Type.Record) {
			Type.Record rt = (Type.Record) t;
			for (Type field : rt.fields().values()) {
				if (containsNominal(field, attributes)) {
					return true;
				}
			}
			return false;
		} else if (t instanceof Type.Reference) {
			Type.Reference lt = (Type.Reference) t;
			return containsNominal(lt.element(), attributes);
		} else if (t instanceof Type.Union) {
			Type.Union tu = (Type.Union) t;
			for (Type te : tu.bounds()) {
				if (containsNominal(te, attributes)) {
					return true;
				}
			}
			return false;
		} else if (t instanceof Type.Negation) {
			Type.Negation nt = (Type.Negation) t;
			return containsNominal(nt.element(), attributes);
		} else if (t instanceof Type.FunctionOrMethod) {
			Type.FunctionOrMethod ft = (Type.FunctionOrMethod) t;
			for (Type pt : ft.params()) {
				if (containsNominal(pt, attributes)) {
					return true;
				}
			}
			for (Type pt : ft.returns()) {
				if (containsNominal(pt, attributes)) {
					return true;
				}
			}
			return false;
		} else if (t instanceof Type.Nominal) {
			return true;
		} else {
			internalFailure("unknown type encountered ("
					+ t.getClass().getName() + ")", filename, attributes);
			return false;
		}
	}
	
	/**
	 * Generate verification conditions to enforce the necessary preconditions
	 * for a given bytecode. For example, to protect against division by zero or
	 * an out-of-bounds access.
	 * 
	 * @param code
	 * @param branch
	 * @param branches
	 * @param block
	 */
	public Pair<String,Expr>[] getPreconditions(Code code, VcBranch branch,
			Type[] environment, CodeForest forest) {
		//
		try {
			switch (code.opcode()) {
			case Code.OPCODE_div:
			case Code.OPCODE_rem:
				return divideByZeroCheck((Codes.Operator) code, branch);
			case Code.OPCODE_indexof:
				return indexOutOfBoundsChecks((Codes.IndexOf) code, branch);
			case Code.OPCODE_arrygen:
				return arrayGeneratorChecks((Codes.ArrayGenerator) code, branch);
			case Code.OPCODE_update:
				return updateChecks((Codes.Update) code, branch);
			case Code.OPCODE_invoke:
				return preconditionCheck((Codes.Invoke) code, branch, environment, forest);
			}
			return new Pair[0];
		} catch (Exception e) {
			internalFailure(e.getMessage(), filename, e);
			return null; // deadcode
		}
	}

	/**
	 * Generate preconditions to protected against a possible divide by zero.
	 * This essentially boils down to ensureing the divisor is non-zero.
	 * 
	 * @param binOp
	 *            --- The division or remainder bytecode
	 * @param branch
	 *            --- The branch the division is on.
	 * @return
	 */
	public Pair<String, Expr>[] divideByZeroCheck(Codes.Operator binOp, VcBranch branch) {
		Expr rhs = branch.read(binOp.operand(1));
		Value zero;
		if (binOp.type(0) instanceof Type.Int) {
			zero = Value.Integer(BigInteger.ZERO);
		} else {
			zero = Value.Decimal(BigDecimal.ZERO);
		}
		Expr.Constant constant = new Expr.Constant(zero, rhs.attributes());
		return new Pair[] {
				new Pair("division by zero", new Expr.Binary(Expr.Binary.Op.NEQ, rhs, constant, rhs.attributes())) };
	}

	/**
	 * Generate preconditions necessary to protect against an out-of-bounds
	 * access. For lists, this means ensuring the index is non-negative and less
	 * than the list length.
	 * 
	 * @param code
	 *            --- The indexOf bytecode
	 * @param branch
	 *            --- The branch the bytecode is on.
	 * @return
	 */
	public Pair<String,Expr>[] indexOutOfBoundsChecks(Codes.IndexOf code, VcBranch branch) {
		if (code.type(0) instanceof Type.EffectiveArray) {
			Expr src = branch.read(code.operand(0));
			Expr idx = branch.read(code.operand(1));
			Expr zero = new Expr.Constant(Value.Integer(BigInteger.ZERO),
					idx.attributes());
			Expr length = new Expr.Unary(Expr.Unary.Op.LENGTHOF, src,
					idx.attributes());
			return new Pair[] {
					new Pair("index out of bounds (negative)", new Expr.Binary(
							Expr.Binary.Op.GTEQ, idx, zero, idx.attributes())),
					new Pair("index out of bounds (not less than length)",
							new Expr.Binary(Expr.Binary.Op.LT, idx, length,
									idx.attributes())), };			
		} else {
			// FIXME: should do something here! At a minimum, generate a warning
			// that this has not been implemented yet.
			return new Pair[0];
		}
	}

	/**
	 * Generate preconditions necessary to protect against a negative array
	 * size.
	 * 
	 * @param code
	 *            --- The array generator bytecode
	 * @param branch
	 *            --- The branch the bytecode is on.
	 * @return
	 */
	public Pair<String,Expr>[] arrayGeneratorChecks(Codes.ArrayGenerator code, VcBranch branch) {
		Expr idx = branch.read(code.operand(1));
		Expr zero = new Expr.Constant(Value.Integer(BigInteger.ZERO),
				idx.attributes());
		return new Pair[] {
				new Pair("negative length possible", new Expr.Binary(
						Expr.Binary.Op.GTEQ, idx, zero, idx.attributes()))
		};
	}
	
	/**
	 * Generate preconditions necessary to ensure the preconditions for a method
	 * or method invocation are met.
	 * 
	 * @param code
	 *            --- The invoke bytecode
	 * @param branch
	 *            --- The branch on which the invocation is on.
	 * @param block
	 *            --- The containing block of code.
	 * @return
	 * @throws Exception
	 */
	public Pair<String,Expr>[] preconditionCheck(Codes.Invoke code, VcBranch branch,
			Type[] environment, CodeForest forest) throws Exception {
		ArrayList<Pair<String,Expr>> preconditions = new ArrayList<>();
		//
		// First, check for any potentially constrained types.    
		//
		List<wyil.lang.Attribute> attributes = forest.get(branch.pc()).attributes();
		List<Type> code_type_params = code.type(0).params();		
		int[] code_operands = code.operands();
		for (int i = 0; i != code_operands.length; ++i) {
			Type t = code_type_params.get(i);
			if (containsNominal(t, attributes)) {
				int operand = code_operands[i];
				Type rawType = expand(environment[operand], attributes);
				Expr rawTest = new Expr.Is(branch.read(operand), convert(rawType, attributes));
				Expr nominalTest = new Expr.Is(branch.read(operand), convert(t, attributes));
				preconditions.add(new Pair("type invariant not satisfied (argument " + i + ")",
						new Expr.Binary(Expr.Binary.Op.IMPLIES, rawTest, nominalTest)));
			}
		}
		//
		int numPreconditions = countPreconditions(code.name, code.type(0), forest, branch);
		//
		if (numPreconditions > 0) {
			// First, read out the operands from the branch
			Expr[] operands = new Expr[code_operands.length];
			for (int i = 0; i != code_operands.length; ++i) {
				operands[i] = branch.read(code_operands[i]);
			}
			// To check the pre-condition holds after the method, we
			// simply called the corresponding pre-condition macros.
			String prefix = code.name.name() + "_requires_";

			Expr argument = operands.length == 1 ? operands[0] : new Expr.Nary(Expr.Nary.Op.TUPLE, operands);
			for (int i = 0; i < numPreconditions; ++i) {
				Expr precondition = new Expr.Invoke(prefix + i, code.name.module(), Collections.EMPTY_LIST, argument);
				preconditions.add(new Pair<String, Expr>("precondition not satisfied", precondition));

			}
		}
		return preconditions.toArray(new Pair[preconditions.size()]);
	}

	/**
	 * Ensure all preconditions for an update bytecode are met. For example,
	 * that any array updates are within bounds, etc.
	 * 
	 * @param code
	 *            --- The update bytecode.
	 * @param branch
	 *            --- The branch containing the update bytecode.
	 * @return
	 */
	public Pair<String,Expr>[] updateChecks(Codes.Update code, VcBranch branch) {
		ArrayList<Pair<String,Expr>> preconditions = new ArrayList<Pair<String,Expr>>();

		Expr src = branch.read(code.target(0));

		for (Codes.LVal lval : code) {
			if (lval instanceof Codes.ArrayLVal) {
				Codes.ArrayLVal lv = (Codes.ArrayLVal) lval;
				Expr idx = branch.read(lv.indexOperand);
				Expr zero = new Expr.Constant(Value.Integer(BigInteger.ZERO),
						idx.attributes());
				Expr length = new Expr.Unary(Expr.Unary.Op.LENGTHOF, src,
						idx.attributes());
				preconditions.add(new Pair("index out of bounds (negative)",
						new Expr.Binary(Expr.Binary.Op.GTEQ, idx, zero, idx
								.attributes())));
				preconditions.add(new Pair(
						"index out of bounds (not less than length)",
						new Expr.Binary(Expr.Binary.Op.LT, idx, length, idx
								.attributes())));
				src = new Expr.IndexOf(src, idx);
			} else if (lval instanceof Codes.RecordLVal) {
				Codes.RecordLVal lv = (Codes.RecordLVal) lval;
				ArrayList<String> fields = new ArrayList<String>(lv.rawType()
						.fields().keySet());
				Collections.sort(fields);
				Expr index = new Expr.Constant(Value.Integer(BigInteger
						.valueOf(fields.indexOf(lv.field))));
				src = new Expr.IndexOf(src, index);
			} else {
				// FIXME: need to implement dereference operations.
			}
		}

		return preconditions.toArray(new Pair[preconditions.size()]);
	}

	
	/**
	 * Find the precondition associated with a given function or method. This
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
	public int countPreconditions(NameID name, Type.FunctionOrMethod fun,
			CodeForest forest, VcBranch branch) throws Exception {
		Path.Entry<WyilFile> e = builder.project().get(name.module(), WyilFile.ContentType);
		if (e == null) {
			syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, name.module().toString()), filename,
					forest.get(branch.pc()).attributes());
		}
		WyilFile m = e.read();
		WyilFile.FunctionOrMethod method = m.functionOrMethod(name.name(), fun);

		return method.preconditions().length;
	}

	/**
	 * Determine the originating register number for this variable. This is made
	 * difficult because of variable versioning. All variable names and versions
	 * are encoded into a string of the form "n$v", where n is the variable name
	 * (A.K.A. the prefix) and "v" is the version.
	 * 
	 * @param variable
	 * @return
	 */
	public static int determineRegister(String variable, String[] prefixes) {
		// First determine the variable name (i.e. the prefix).
		int dollarIndex = variable.indexOf('$');
		String prefix;
		if (dollarIndex != -1) {
			// In this case, the variable name was of the form "n$v" where n is
			// the name, and v is the version. We don't need the version here,
			// so strip it off.
			prefix = variable.substring(0, dollarIndex);
		} else {
			// In this case, no version is given and, hence, there is nothing to
			// strip off.
			prefix = variable;
		}
		// Now, check whether this is a raw register identifier, or a named
		// variable identifier.
		if(prefix.startsWith("r%")) {
			// This is a raw register identifier. Therefore, we can extract the
			// register number directly.
			return Integer.parseInt(prefix.substring(2));
		} else {
			// This is a named varaible identifier. Therefore, we need to look
			// through the known list of named variable prefixes to see whether
			// or not we can find it (which we should be able to do).
			for (int i = 0; i != prefixes.length; ++i) {
				if (prefix.equals(prefixes[i])) {
					return i;
				}
			}
			// Should be impossible to get here.
			throw new RuntimeException(
					"Unreachable code reached whilst looking for: " + variable);
		}
	}
	
	private Type expand(Type t, Collection<wyil.lang.Attribute> attributes) {
		try {
			return expander.getUnderlyingType(t);
		} catch (ResolveError re) {
			internalFailure(re.getMessage(), filename, attributes);
		} catch (IOException re) {
			internalFailure(re.getMessage(), filename, attributes);
		}
		return null; // dead-code
	}
	
	/**
	 * Returns the prefix array which gives the names of all registers declared
	 * in a given block.
	 * 
	 * @param d
	 * @return
	 */
	public static Pair<String[], Type[]> parseRegisterDeclarations(CodeForest forest) {
		List<CodeForest.Register> regs = forest.registers();
		String[] prefixes = new String[regs.size()];
		Type[] types = new Type[regs.size()];
		for (int i = 0; i != prefixes.length; ++i) {
			CodeForest.Register d = regs.get(i);			
			prefixes[i] = d.name();
			types[i] = d.type();
		}
		return new Pair<>(prefixes, types);		
	}
}
