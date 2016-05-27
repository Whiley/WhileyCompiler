package wyil.builders;

import static wyil.util.ErrorMessages.internalFailure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import wybs.lang.Builder;
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.SyntacticElement;
import wycs.syntax.Expr;
import wycs.syntax.SyntacticType;
import wycs.syntax.TypePattern;
import wycs.syntax.WyalFile;
import wyil.lang.Bytecode;
import wyil.lang.BytecodeForest;
import wyil.lang.BytecodeForest.Location;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.util.TypeExpander;
import wyil.util.interpreter.Interpreter.Context;

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
				translate((WyilFile.Type) b, wyilFile, wyalFile);
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
	 * @param typeDecl
	 * @param wyilFile
	 */
	private void translate(WyilFile.Type typeDecl, WyilFile wyilFile, WyalFile wyalFile) {
		BytecodeForest forest = typeDecl.invariant();
		Context context = new Context(wyilFile.filename(), forest);

		// First, translate the invariant (if applicable)
		Expr.Variable var = null;
		Expr invariant = null;

		if (forest.numRoots() > 0) {
			BytecodeForest.Variable v = (BytecodeForest.Variable) forest.getLocation(0);
			var = new Expr.Variable(v.name(), v.attributes());
			for (int i = 0; i != forest.numRoots(); ++i) {
				Expr clause = translate(forest.getRoot(i), context);
				// FIXME: this is ugly
				if (invariant == null) {
					invariant = clause;
				} else {
					invariant = new Expr.Binary(Expr.Binary.Op.AND, invariant, clause);
				}
			}
		}

		// Convert the type into a type pattern
		TypePattern.Leaf pattern = new TypePattern.Leaf(convert(typeDecl.type(), typeDecl, context), var);
		// Done
		WyalFile.Type td = wyalFile.new Type(typeDecl.name(), Collections.EMPTY_LIST, pattern, invariant,
				typeDecl.attributes());
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
	 * Transform a given bytecode operand into its equivalent WyAL expression.
	 * 
	 * @param operand
	 *            The operand index to be translateed
	 * @param context
	 *            Contextual information at the point of translateation
	 * @return
	 */
	private Expr translate(int operand, Context context) {
		BytecodeForest.Location loc = (BytecodeForest.Location) context.getLocation(operand);
		Constant val; 
		if (loc instanceof BytecodeForest.Variable) {
			BytecodeForest.Variable var = (BytecodeForest.Variable) loc;
			return new Expr.Variable(var.name(), var.attributes());
		} else {
			BytecodeForest.Operand o = (BytecodeForest.Operand) loc;
			Bytecode.Expr bytecode = o.value();
			switch (bytecode.opcode()) {
			case Bytecode.OPCODE_const:
				return translate((Bytecode.Const) bytecode, context);				
			case Bytecode.OPCODE_convert:
				return translate((Bytecode.Convert) bytecode, context);
			case Bytecode.OPCODE_fieldload:
				return translate((Bytecode.FieldLoad) bytecode, context);
			case Bytecode.OPCODE_indirectinvoke:
				return translate((Bytecode.IndirectInvoke) bytecode, context);
			case Bytecode.OPCODE_invoke:
				return translate((Bytecode.Invoke) bytecode, context);
			case Bytecode.OPCODE_lambda:
				return translate((Bytecode.Lambda) bytecode, context);
			case Bytecode.OPCODE_none:
			case Bytecode.OPCODE_some:
			case Bytecode.OPCODE_all:
				return translate((Bytecode.Quantifier) bytecode, context);
			default:
				return translate((Bytecode.Operator) bytecode, context);
			}
		}	
	}
	
	private Expr translate(Bytecode.Const code, Context context) {
		// FIXME: need to implemet this
		return null;
	}
	
	private Expr translate(Bytecode.Convert code, Context context) {
		// FIXME: need to implemet this
		return null;
	}
	
	private Expr translate(Bytecode.FieldLoad code, Context context) {
		// FIXME: need to implemet this
		return null;
	}
	
	private Expr translate(Bytecode.IndirectInvoke code, Context context) {
		// FIXME: need to implemet this
		return null;
	}
		
	private Expr translate(Bytecode.Invoke code, Context context) {
		// FIXME: need to implemet this
		return null;
	}
	
	private Expr translate(Bytecode.Lambda code, Context context) {
		// FIXME: need to implemet this
		return null;
	}

	private Expr translate(Bytecode.Operator code, Context context) {
		// FIXME: need to implemet this
		return null;
	}
	
	private Expr translate(Bytecode.Quantifier code, Context context) {
		// FIXME: need to implemet this
		return null;
	}
		
	// =========================================================================
	// Helper
	// =========================================================================
	
	/**
	 * Convert a WyIL type into its equivalent WyCS type. In some cases, this is
	 * a direct translation. In other cases, WyIL types are encoded using more
	 * primitive WyCS types.
	 * 
	 * @param type
	 *            The WyIL type to be converted.
	 * @param elem
	 *            The syntactic element associated with the point of this
	 *            conversion. These are used for debugging purposes to associate
	 *            any errors generated with a source line.
	 * @param context
	 *            Additional contextual information useful for error reporting.
	 * @return
	 */
	private static SyntacticType convert(Type type, SyntacticElement elem, Context context) {
		// FIXME: this is fundamentally broken in the case of recursive types.
		// See Issue #298.
		if (type instanceof Type.Any) {
			return new SyntacticType.Any(elem.attributes());
		} else if (type instanceof Type.Void) {
			return new SyntacticType.Void(elem.attributes());
		} else if (type instanceof Type.Null) {
			return new SyntacticType.Null(elem.attributes());
		} else if (type instanceof Type.Bool) {
			return new SyntacticType.Bool(elem.attributes());
		} else if (type instanceof Type.Byte) {
			// FIXME: implement SyntacticType.Byte
			// return new SyntacticType.Byte(attributes(branch);
			return new SyntacticType.Int(elem.attributes());
		} else if (type instanceof Type.Int) {
			return new SyntacticType.Int(elem.attributes());
		} else if (type instanceof Type.Array) {
			Type.Array lt = (Type.Array) type;
			SyntacticType element = convert(lt.element(), elem, context);
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
				elements.add(convert(fields.get(field), elem, context));
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
				elements.add(convert(te, elem, context));
			}
			return new SyntacticType.Union(elements);
		} else if (type instanceof Type.Negation) {
			Type.Negation nt = (Type.Negation) type;
			SyntacticType element = convert(nt.element(), elem, context);
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
			return new SyntacticType.Nominal(names, elem.attributes());
		} else {
			internalFailure("unknown type encountered (" + type.getClass().getName() + ")", context.getFilename(),
					elem.attributes());
			return null;
		}
	}
	
	private class Context {
		private final String filename;
		private final BytecodeForest forest;
		
		public Context(String filename, BytecodeForest forest) {
			this.filename = filename;
			this.forest = forest;
		}
		
		public String getFilename() {
			return filename;
		}
		
		public Location getLocation(int operand) {
			return forest.getLocation(operand);
		}
	}
}
