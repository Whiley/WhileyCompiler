package wyil.builders;

import static wyil.util.ErrorMessages.internalFailure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import wybs.lang.Builder;
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.SyntacticElement;
import wycs.core.Value;
import wycs.syntax.Expr;
import wycs.syntax.SyntacticType;
import wycs.syntax.TypePattern;
import wycs.syntax.WyalFile;
import wyil.lang.Bytecode;
import wyil.lang.BytecodeForest;
import wyil.lang.Location;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.util.TypeExpander;
import wyil.util.interpreter.Interpreter.Context;

/**
 * <p>
 * Responsible for generating verification conditions from a given WyIL file. A
 * verification condition is a logical condition which must be shown to hold in
 * order for the underlying WyIL program to considered "correct". The
 * Verification Condition Generator (VCG) examines in turn each function or
 * method in a given WyIL file. The VCG traverses each control-flow graph
 * emitting verification conditions as it discovers them. The following
 * illustrates:
 * </p>
 * 
 * <pre>
 * function abs(int x) -> (int r)
 * ensures r >= 0:
 *     //
 *     if x >= 0:
 *        return x
 *     else:
 *        return -x
 * </pre>
 *
 * <p>
 * The above function can be viewed in a slightly more precise fashion as
 * follows, where the block structure is indicated:
 * </p>
 *
 * <pre>
 * +-----------------------------+ (1)
 * |function abs(int x) -> (int r)
 * |ensures r >= 0:
 * |  +--------------------------+ (2)
 * |  |  //
 * |  |  if x >= 0:
 * |  |   +----------------------+ (3)
 * |  |   | return x
 * |  |   +----------------------+
 * |  |  else:
 * |  |   +----------------------+ (4)
 * |  |   | return -x
 * |  |   +----------------------+
 * |  +--------------------------+
 * +-----------------------------+
 * </pre>
 * 
 * <p>
 * The VCG will generate exactly two verification conditions from this function
 * corresponding to the paths "1,2,3" and "1,2,4". These verification conditions
 * are required to ensure that, given the information know at the point of each
 * return, we can establish the post-condition holds. The two verification
 * conditions are:
 * </p>
 * 
 * <ul>
 * <li><b>1,2,3:</b> <code>x >= 0 ==> x >= 0</code>. This verification corresponds to
 * the case where the if condition is known to be true.</li>
 * <li><b>1,2,4:</b> <code>x < 0 ==> -x >= 0</code>. This verification corresponds to
 * the case where the if condition is known to be false.</li>
 * </ul>
 * 
 * <p>
 * The VCG attempts to generate verification conditions which are easier to read
 * by making use of macros as much as possible. For example, each clause of a
 * function/method's precondition or postcondition is turned into a distinct
 * (named) macro.
 * </p>
 * 
 * @author David J. Pearce
 *
 */
public class VerificationConditionGenerator {
	private final Builder builder;
	private final TypeExpander expander;

	public VerificationConditionGenerator(Builder builder) {
		this.builder = builder;
		this.expander = new TypeExpander(builder.project());
	}

	// ===============================================================================
	// Top-Level Controller
	// ===============================================================================

	public WyalFile translate(WyilFile wyilFile) {
		WyalFile wyalFile = new WyalFile(wyilFile.id(), wyilFile.filename());

		for (WyilFile.Block b : wyilFile.blocks()) {
			if (b instanceof WyilFile.Constant) {
				translate((WyilFile.Constant) b, wyilFile);
			} else if (b instanceof WyilFile.Type) {
				translate((WyilFile.Type) b, wyalFile);
			} else if (b instanceof WyilFile.FunctionOrMethod) {
				WyilFile.FunctionOrMethod method = (WyilFile.FunctionOrMethod) b;
				translate(method, wyilFile);
			}
		}

		return wyalFile;
	}

	/**
	 * Translate a constant declaration into WyAL. At the moment, this does
	 * nothing because constant declarations are not supported in WyAL files.
	 */
	private void translate(WyilFile.Constant decl, WyilFile wyilFile) {
	}

	/**
	 * Transform a type declaration into verification conditions as necessary.
	 * In particular, the type should be "inhabitable". This means, for example,
	 * that the invariant does not contradict itself. Furthermore, we need to
	 * translate the type invariant into a macro block.
	 * 
	 * @param declaration
	 *            The type declaration being translated.
	 * @param wyalFile
	 *            The WyAL file being constructed
	 */
	private void translate(WyilFile.Type declaration, WyalFile wyalFile) {
		BytecodeForest forest = declaration.invariant();

		// First, translate the invariant (if applicable)
		Expr.Variable var = null;
		Expr invariant = null;

		if (forest.numRoots() > 0) {
			Location.Variable v = (Location.Variable) forest.getLocation(0);
			var = new Expr.Variable(v.name(), v.attributes());
			for (int i = 0; i != forest.numRoots(); ++i) {
				int index = forest.getRoot(i);
				Expr clause = translate(forest.getLocation(index));
				// FIXME: this is ugly. Instead, WyAL files could support
				// multiple invariant clauses?
				if (invariant == null) {
					invariant = clause;
				} else {
					invariant = new Expr.Binary(Expr.Binary.Op.AND, invariant, clause);
				}
			}
		}

		// Convert the type into a type pattern
		SyntacticType type = convert(declaration.type(), declaration);
		TypePattern.Leaf pattern = new TypePattern.Leaf(type, var);
		// Done
		WyalFile.Type td = wyalFile.new Type(declaration.name(), Collections.EMPTY_LIST, pattern, invariant,
				declaration.attributes());
		wyalFile.add(td);
	}

	/**
	 * Transform a function or method into its WyAL equivalent. This involves
	 * creating zero or more verification conditions to check the various
	 * conditions within the method itself.
	 * 
	 * @param method
	 * @param wyilFile
	 */
	private void translate(WyilFile.FunctionOrMethod method, WyilFile wyilFile) {
	}

	// =========================================================================
	// Statements
	// =========================================================================

	// =========================================================================
	// Operands
	// =========================================================================

	/**
	 * Transform a given bytecode location into its equivalent WyAL expression.
	 * 
	 * @param location
	 *            The bytecode location to be translated
	 * @return
	 */
	private Expr translate(Location loc) {
		if (loc instanceof Location.Variable) {
			Location.Variable var = (Location.Variable) loc;
			return new Expr.Variable(var.name(), var.attributes());
		} else {
			Location.Operand operand = (Location.Operand) loc;
			Bytecode.Expr bytecode = operand.value();
			switch (bytecode.opcode()) {
			case Bytecode.OPCODE_const:
				return translate((Bytecode.Const) bytecode, operand);
			case Bytecode.OPCODE_convert:
				return translate((Bytecode.Convert) bytecode, operand);
			case Bytecode.OPCODE_fieldload:
				return translate((Bytecode.FieldLoad) bytecode, operand);
			case Bytecode.OPCODE_indirectinvoke:
				return translate((Bytecode.IndirectInvoke) bytecode, operand);
			case Bytecode.OPCODE_invoke:
				return translate((Bytecode.Invoke) bytecode, operand);
			case Bytecode.OPCODE_lambda:
				return translate((Bytecode.Lambda) bytecode, operand);
			case Bytecode.OPCODE_none:
			case Bytecode.OPCODE_some:
			case Bytecode.OPCODE_all:
				return translate((Bytecode.Quantifier) bytecode, operand);
			default:
				return translate((Bytecode.Operator) bytecode, operand);
			}
		}
	}

	private Expr translate(Bytecode.Const code, Location.Operand context) {
		Value value = convert(code.constant(),context);
	}

	private Expr translate(Bytecode.Convert code, Location.Operand context) {
		// FIXME: need to implemet this
		throw new RuntimeException("Implement me!");
	}

	private Expr translate(Bytecode.FieldLoad code, Location.Operand context) {
		// FIXME: need to implemet this
		throw new RuntimeException("Implement me!");
	}

	private Expr translate(Bytecode.IndirectInvoke code, Location.Operand context) {
		// FIXME: need to implemet this
		throw new RuntimeException("Implement me!");
	}

	private Expr translate(Bytecode.Invoke code, Location.Operand context) {
		// FIXME: need to implemet this
		throw new RuntimeException("Implement me!");
	}

	private Expr translate(Bytecode.Lambda code, Location.Operand context) {
		// FIXME: need to implemet this
		throw new RuntimeException("Implement me!");
	}

	private Expr translate(Bytecode.Operator code, Location.Operand context) {
		// FIXME: need to implemet this
		throw new RuntimeException("Implement me!");
	}

	private Expr translate(Bytecode.Quantifier code, Location.Operand context) {
		// FIXME: need to implemet this
		throw new RuntimeException("Implement me!");
	}

	// =========================================================================
	// Helpers
	// =========================================================================

	/**
	 * Convert a WyIL constant into its equivalent WyCS constant. In some cases,
	 * this is a direct translation. In other cases, WyIL constants are encoded
	 * using more primitive WyCS values.
	 * 
	 * @param c
	 *            --- The WyIL constant to be converted.
	 * @param context
	 *            Additional contextual information associated with the point of
	 *            this conversion. These are used for debugging purposes to
	 *            associate any errors generated with a source line.
	 * @return
	 */
	public Value convert(Constant c, WyilFile.Block context) {
		if (c instanceof Constant.Null) {
			return wycs.core.Value.Null;
		} else if (c instanceof Constant.Bool) {
			Constant.Bool cb = (Constant.Bool) c;
			return wycs.core.Value.Bool(cb.value());
		} else if (c instanceof Constant.Byte) {
			Constant.Byte cb = (Constant.Byte) c;
			return wycs.core.Value.Integer(BigInteger.valueOf(cb.value()));
		} else if (c instanceof Constant.Integer) {
			Constant.Integer cb = (Constant.Integer) c;
			return wycs.core.Value.Integer(cb.value());
		} else if (c instanceof Constant.Array) {
			Constant.Array cb = (Constant.Array) c;
			List<Constant> cb_values = cb.values();
			ArrayList<Value> items = new ArrayList<Value>();
			for (int i = 0; i != cb_values.size(); ++i) {
				items.add(convert(cb_values.get(i), context));
			}
			return Value.Array(items);
		} else if (c instanceof Constant.Record) {
			Constant.Record rb = (Constant.Record) c;

			// NOTE: records are currently translated into WyCS as tuples,
			// where each field is allocated a slot based on an alphabetical sorting
			// of field names. It's unclear at this stage whether or not that is
			// a general solution. In particular, it would seem to be broken for
			// type testing.

			ArrayList<String> fields = new ArrayList<String>(rb.values().keySet());
			Collections.sort(fields);
			ArrayList<Value> values = new ArrayList<Value>();
			for (String field : fields) {
				values.add(convert(rb.values().get(field), context));
			}
			return wycs.core.Value.Tuple(values);
		} else {
			internalFailure("unknown constant encountered (" + c + ")", context.parent().filename(),
					context.attributes());
			return null;
		}
	}
	
	/**
	 * Convert a WyIL type into its equivalent WyCS type. In some cases, this is
	 * a direct translation. In other cases, WyIL types are encoded using more
	 * primitive WyCS types.
	 * 
	 * @param type
	 *            The WyIL type to be converted.
	 * @param context
	 *            Additional contextual information associated with the point of
	 *            this conversion. These are used for debugging purposes to
	 *            associate any errors generated with a source line.
	 * @return
	 */
	private static SyntacticType convert(Type type, WyilFile.Block context) {
		// FIXME: this is fundamentally broken in the case of recursive types.
		// See Issue #298.
		if (type instanceof Type.Any) {
			return new SyntacticType.Any(context.attributes());
		} else if (type instanceof Type.Void) {
			return new SyntacticType.Void(context.attributes());
		} else if (type instanceof Type.Null) {
			return new SyntacticType.Null(context.attributes());
		} else if (type instanceof Type.Bool) {
			return new SyntacticType.Bool(context.attributes());
		} else if (type instanceof Type.Byte) {
			// FIXME: implement SyntacticType.Byte
			// return new SyntacticType.Byte(attributes(branch);
			return new SyntacticType.Int(context.attributes());
		} else if (type instanceof Type.Int) {
			return new SyntacticType.Int(context.attributes());
		} else if (type instanceof Type.Array) {
			Type.Array lt = (Type.Array) type;
			SyntacticType element = convert(lt.element(),context);
			// ugly.
			return new SyntacticType.List(element);
		} else if (type instanceof Type.Record) {
			Type.Record rt = (Type.Record) type;
			HashMap<String, Type> fields = rt.fields();
			ArrayList<String> names = new ArrayList<String>(fields.keySet());
			ArrayList<SyntacticType> elements = new ArrayList<SyntacticType>();
			Collections.sort(names);
			for (int i = 0; i != names.size(); ++i) {
				String field = names.get(i);
				elements.add(convert(fields.get(field), context));
			}
			return new SyntacticType.Tuple(elements);
		} else if (type instanceof Type.Reference) {
			// FIXME: how to translate this??
			return new SyntacticType.Any();
		} else if (type instanceof Type.Union) {
			Type.Union tu = (Type.Union) type;
			HashSet<Type> tu_elements = tu.bounds();
			ArrayList<SyntacticType> elements = new ArrayList<SyntacticType>();
			for (Type te : tu_elements) {
				elements.add(convert(te, context));
			}
			return new SyntacticType.Union(elements);
		} else if (type instanceof Type.Negation) {
			Type.Negation nt = (Type.Negation) type;
			SyntacticType element = convert(nt.element(), context);
			return new SyntacticType.Negation(element);
		} else if (type instanceof Type.FunctionOrMethod) {
			Type.FunctionOrMethod ft = (Type.FunctionOrMethod) type;
			// FIXME: need to do something better here
			return new SyntacticType.Any();
		} else if (type instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.name();
			ArrayList<String> names = new ArrayList<String>();
			for (String pc : nid.module()) {
				names.add(pc);
			}
			names.add(nid.name());
			return new SyntacticType.Nominal(names, context.attributes());
		} else {
			internalFailure("unknown type encountered (" + type.getClass().getName() + ")", context.parent().filename(),
					context.attributes());
			return null;
		}
	}
}
