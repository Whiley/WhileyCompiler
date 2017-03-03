// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.builders;

import static wyil.util.ErrorMessages.errorMessage;

import java.math.BigInteger;
import java.util.*;

import wybs.lang.Attribute;
import wybs.lang.NameID;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.ResolveError;
import wycc.util.Pair;
import wycc.util.ArrayUtils;
import wyal.lang.SyntacticItem;
import wyal.lang.WyalFile;
import wyal.lang.WyalFile.Declaration;
import wyal.lang.WyalFile.Expr;
import wyal.lang.WyalFile.Opcode;
//import wyal.lang.WyalFile.Type;
import wyal.lang.WyalFile.Value;
import wyal.lang.WyalFile.Declaration.Named;
import wyal.util.AutomatedTheoremProver;
import wyfs.lang.Path;
import wyfs.lang.Path.ID;
import wyfs.util.Trie;
import wyil.lang.Bytecode;
import wyil.lang.Bytecode.*;
import wyil.lang.SyntaxTree;
import wyil.lang.SyntaxTree.Location;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.util.ErrorMessages;
import wyil.util.SyntaxTrees;
import wyil.util.TypeSystem;

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
 * <li><b>1,2,3:</b> <code>x >= 0 ==> x >= 0</code>. This verification
 * corresponds to the case where the if condition is known to be true.</li>
 * <li><b>1,2,4:</b> <code>x < 0 ==> -x >= 0</code>. This verification
 * corresponds to the case where the if condition is known to be false.</li>
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
	private final Wyil2WyalBuilder builder;
	private final TypeSystem typeSystem;

	public VerificationConditionGenerator(Wyil2WyalBuilder builder) {
		this.builder = builder;
		this.typeSystem = new TypeSystem(builder.project());
	}

	// ===============================================================================
	// Top-Level Controller
	// ===============================================================================

	/**
	 * Translate a WyilFile into a WyalFile which contains the verification
	 * conditions necessary to establish that all functions and methods in the
	 * WyilFile meet their specifications, and that no array-out-of-bounds or
	 * division-by-zero exceptions are possible (amongst other things).
	 *
	 * @param wyilFile
	 *            The input file to be translated
	 * @return
	 */
	public WyalFile translate(WyilFile wyilFile, Path.Entry<WyalFile> target) {
		WyalFile wyalFile = new WyalFile(target);

		for (WyilFile.Block b : wyilFile.blocks()) {
			if (b instanceof WyilFile.Constant) {
				translateConstantDeclaration((WyilFile.Constant) b, wyalFile);
			} else if (b instanceof WyilFile.Type) {
				translateTypeDeclaration((WyilFile.Type) b, wyalFile);
			} else if (b instanceof WyilFile.FunctionOrMethod) {
				WyilFile.FunctionOrMethod method = (WyilFile.FunctionOrMethod) b;
				translateFunctionOrMethodDeclaration(method, wyalFile);
			}
		}

		return wyalFile;
	}

	/**
	 * Translate a constant declaration into WyAL. At the moment, this does
	 * nothing because constant declarations are not supported in WyAL files.
	 *
	 * @param declaration
	 *            The type declaration being translated.
	 * @param wyalFile
	 *            The WyAL file being constructed
	 */
	private void translateConstantDeclaration(WyilFile.Constant decl, WyalFile wyalFile) {
		// FIXME: WyAL file format should support constants
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
	private void translateTypeDeclaration(WyilFile.Type declaration, WyalFile wyalFile) {
		SyntaxTree tree = declaration.getTree();
		List<Location<Bytecode.Expr>> invariants = declaration.getInvariant();
		WyalFile.Stmt.Block[] invariant = new WyalFile.Stmt.Block[invariants.size()];
		WyalFile.Type type = convert(declaration.type(), declaration);
		WyalFile.VariableDeclaration var;
		if(invariants.size() > 0) {
			Location<VariableDeclaration> v = (Location<VariableDeclaration>) tree.getLocation(0);
			// First, translate the invariant (if applicable)
			GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
			LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
			var = localEnvironment.read(v.getIndex());
			for (int i = 0; i != invariant.length; ++i) {
				invariant[i] = translateAsBlock(invariants.get(i), localEnvironment);
			}
		} else {
			var = new WyalFile.VariableDeclaration(type,new WyalFile.Identifier("this"));
		}
		// Done
		WyalFile.Identifier name = new WyalFile.Identifier(declaration.name());
		WyalFile.Declaration td = new WyalFile.Declaration.Named.Type(name, var, invariant);
		td.attributes().addAll(declaration.attributes());
		wyalFile.allocate(td);
	}

	/**
	 * Transform a function or method declaration into verification conditions
	 * as necessary. This is done by traversing the control-flow graph of the
	 * function or method in question. Verifications are emitted when conditions
	 * are encountered which must be checked. For example, that the
	 * preconditions are met at a function invocation.
	 *
	 * @param declaration
	 *            The function or method declaration being translated.
	 * @param wyalFile
	 *            The WyAL file being constructed
	 */
	private void translateFunctionOrMethodDeclaration(WyilFile.FunctionOrMethod declaration, WyalFile wyalFile) {
		// Create the prototype for this function or method. This is the
		// function or method declaration which can be used within verification
		// conditions to refer to this function or method. This does not include
		// a body, since function or methods are treated as being
		// "uninterpreted" for the purposes of verification.
		createFunctionOrMethodPrototype(declaration, wyalFile);

		// Create macros representing the individual clauses of the function or
		// method's precondition and postcondition. These macros can then be
		// called either to assume the precondition/postcondition or to check
		// them. Using individual clauses helps to provide better error
		// messages.
		translatePreconditionMacros(declaration, wyalFile);
		translatePostconditionMacros(declaration, wyalFile);

		// The environments are needed to prevent clashes between variable
		// versions across verification conditions, and also to type variables
		// used in verification conditions.
		GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
		LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);

		// Generate the initial assumption set for a given function or method,
		// which roughly corresponds to its precondition.
		AssumptionSet assumptions = generateFunctionOrMethodAssumptionSet(declaration, localEnvironment);
		// Generate verification conditions by propagating forwards through the
		// control-flow graph of the function or method in question. For each
		// statement encountered, generate the preconditions which must hold
		// true at that point. Furthermore, generate the effect of this
		// statement on the current state.
		List<VerificationCondition> vcs = new ArrayList<>();
		Context context = new Context(wyalFile, assumptions, localEnvironment, localEnvironment, null, vcs);
		translateStatementBlock(declaration.getBody(), context);
		//
		// Translate each generated verification condition into an assertion in
		// the underlying WyalFile.
		createAssertions(declaration, vcs, globalEnvironment, wyalFile);
	}

	/**
	 * Translate the sequence of invariant expressions which constitute the
	 * precondition of a function or method into corresponding macro
	 * declarations.
	 *
	 * @param declaration
	 * @param environment
	 * @param wyalFile
	 */
	private void translatePreconditionMacros(WyilFile.FunctionOrMethod declaration, WyalFile wyalFile) {
		List<Location<Bytecode.Expr>> invariants = declaration.getPrecondition();
		//
		String prefix = declaration.name() + "_requires_";
		//
		for (int i = 0; i != invariants.size(); ++i) {
			String name = prefix + i;
			// Construct fresh environment for this macro. This is necessary to
			// avoid name clashes with subsequent macros.
			GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
			LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
			WyalFile.VariableDeclaration[] type = generatePreconditionParameters(declaration,
					localEnvironment);
			// Translate expression itself
			WyalFile.Stmt.Block clause = translateAsBlock(invariants.get(i),
					localEnvironment);
			// Capture any free variables. This is necessary to deal with any
			// variable aliases introduced by type test operators.
// FIXME:
//			clause = captureFreeVariables(declaration, globalEnvironment,
//					clause);
			//
			WyalFile.Declaration d = new WyalFile.Declaration.Named.Macro(new WyalFile.Identifier(name), type, clause);
			wyalFile.allocate(d);
		}
	}

	/**
	 * Translate the sequence of invariant expressions which constitute the
	 * postcondition of a function or method into corresponding macro
	 * declarations.
	 *
	 * @param declaration
	 * @param environment
	 * @param wyalFile
	 */
	private void translatePostconditionMacros(WyilFile.FunctionOrMethod declaration, WyalFile wyalFile) {
		List<Location<Bytecode.Expr>> invariants = declaration.getPostcondition();
		//
		String prefix = declaration.name() + "_ensures_";
		//
		for (int i = 0; i != invariants.size(); ++i) {
			String name = prefix + i;
			// Construct fresh environment for this macro. This is necessary to
			// avoid name clashes with subsequent macros.
			GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
			LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
			WyalFile.VariableDeclaration[] type = generatePostconditionTypePattern(declaration,
					localEnvironment);
			WyalFile.Stmt.Block clause = translateAsBlock(invariants.get(i),
					localEnvironment.clone());
			// Capture any free variables. This is necessary to deal with any
			// variable aliases introduced by type test operators.
// FIXME: put this back in
//			clause = captureFreeVariables(declaration, globalEnvironment,
//					clause);
			//
			WyalFile.Declaration d = new WyalFile.Declaration.Named.Macro(new WyalFile.Identifier(name), type, clause);
			wyalFile.allocate(d);
		}
	}

	// private Expr captureFreeVariables(WyilFile.Declaration declaration,
	// GlobalEnvironment globalEnvironment,
	// Expr clause) {
	// HashSet<String> freeVariables = new HashSet<>();
	// HashSet<String> freeAliases = new HashSet<>();
	// clause.freeVariables(freeVariables);
	// for (String var : freeVariables) {
	// if (globalEnvironment.getParent(var) != null) {
	// // This indicates that the given variable is an alias, rather
	// // than a top-level declaration.
	// freeAliases.add(var);
	// }
	// }
	// // Determine any variable aliases as necessary.
	// if (freeAliases.size() > 0) {
	// // This indicates there are one or more free variables in the
	// // clause. Hence, these must be universally quantified to ensure the
	// // clause is well=typed.
	// TypePattern types = generateExpressionTypePattern(declaration,
	// globalEnvironment, freeAliases);
	// Expr aliases = determineVariableAliases(globalEnvironment, freeAliases);
	// //
	// clause = new Expr.ForAll(types, implies(aliases, clause));
	// }
	// return clause;
	// }

	/**
	 * Generate the initial assumption set for a function or method. This is
	 * essentially made up of the precondition(s) for that function or method.
	 *
	 * @param declaration
	 * @return
	 */
	private AssumptionSet generateFunctionOrMethodAssumptionSet(WyilFile.FunctionOrMethod declaration,
			LocalEnvironment environment) {
		SyntaxTree tree = declaration.getTree();
		String prefix = declaration.name() + "_requires_";
		Expr[] preconditions = new Expr[declaration.getPrecondition().size()];
		Expr[] arguments = new Expr[declaration.type().params().length];
		// Translate parameters as arguments to invocation
		for (int i = 0; i != arguments.length; ++i) {
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) tree.getLocation(i);
			WyalFile.VariableDeclaration versionedVar = environment.read(var.getIndex());
			arguments[i] = new Expr.VariableAccess(versionedVar);
		}
		//
		for (int i = 0; i != preconditions.length; ++i) {
			WyalFile.Name name = new WyalFile.Name(new WyalFile.Identifier(prefix + i));
			preconditions[i] = new Expr.Invoke(null, name, arguments);
		}
		// Add all the preconditions as assupmtions
		return AssumptionSet.ROOT.add(preconditions);
	}

	// =========================================================================
	// Statements
	// =========================================================================

	private Context translateStatementBlock(Location<Block> block, Context context) {
		for (int i = 0; i != block.numberOfOperands(); ++i) {
			Location<?> stmt = block.getOperand(i);
			context = translateStatement(stmt, context);
			if (stmt.getBytecode() instanceof Bytecode.Return) {
				return null;
			}
		}
		return context;
	}

	@SuppressWarnings("unchecked")
	private Context translateStatement(Location<?> stmt, Context context) {
		SyntaxTree tree = stmt.getEnclosingTree();
		WyilFile.Declaration decl = tree.getEnclosingDeclaration();
		//
		try {
			switch (stmt.getOpcode()) {
			case Bytecode.OPCODE_assert:
				return translateAssert((Location<Assert>) stmt, context);
			case Bytecode.OPCODE_assign:
				return translateAssign((Location<Assign>) stmt, context);
			case Bytecode.OPCODE_assume:
				return translateAssume((Location<Assume>) stmt, context);
			case Bytecode.OPCODE_break:
				return translateBreak((Location<Break>) stmt, context);
			case Bytecode.OPCODE_continue:
				return translateContinue((Location<Continue>) stmt, context);
			case Bytecode.OPCODE_debug:
				return context;
			case Bytecode.OPCODE_dowhile:
				return translateDoWhile((Location<DoWhile>) stmt, context);
			case Bytecode.OPCODE_fail:
				return translateFail((Location<Fail>) stmt, context);
			case Bytecode.OPCODE_if:
			case Bytecode.OPCODE_ifelse:
				return translateIf((Location<If>) stmt, context);
			case Bytecode.OPCODE_indirectinvoke:
				checkExpressionPreconditions(stmt, context);
				translateIndirectInvoke((Location<IndirectInvoke>) stmt, context.getEnvironment());
				return context;
			case Bytecode.OPCODE_invoke:
				checkExpressionPreconditions(stmt, context);
				translateInvoke((Location<Invoke>) stmt, context.getEnvironment());
				return context;
			case Bytecode.OPCODE_namedblock:
				return translateNamedBlock((Location<NamedBlock>) stmt, context);
			case Bytecode.OPCODE_return:
				return translateReturn((Location<Return>) stmt, context);
			case Bytecode.OPCODE_skip:
				return translateSkip((Location<Skip>) stmt, context);
			case Bytecode.OPCODE_switch:
				return translateSwitch((Location<Switch>) stmt, context);
			case Bytecode.OPCODE_while:
				return translateWhile((Location<While>) stmt, context);
			case Bytecode.OPCODE_vardecl:
			case Bytecode.OPCODE_vardeclinit:
				return translateVariableDeclaration((Location<VariableDeclaration>) stmt, context);
			default:
				throw new InternalFailure("unknown statement encountered (" + stmt + ")", decl.parent().getEntry(),
						stmt);
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			throw new InternalFailure(e.getMessage(), decl.parent().getEntry(), stmt, e);
		}
	}

	/**
	 * Translate an assert statement. This emits a verification condition which
	 * ensures the assert condition holds, given the current context.
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateAssert(Location<Assert> stmt, Context context) {
		Location<?> operand = stmt.getOperand(0);
		Pair<Expr, Context> p = translateExpressionWithChecks(operand, context);
		Expr condition = p.first();
		context = p.second();
		//
		VerificationCondition verificationCondition = new VerificationCondition("assertion failed", context.assumptions,
				condition, operand.attributes());
		context.emit(verificationCondition);
		//
		return context.assume(condition);
	}

	/**
	 * Translate an assign statement. This updates the version number of the
	 * underlying assigned variable.
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateAssign(Location<Assign> stmt, Context context) {
		Location<?>[] lhs = stmt.getOperandGroup(0);
		Location<?>[] rhs = stmt.getOperandGroup(1);

		// TODO: generate checks for type invariants #666

		for (int i = 0, j = 0; i != rhs.length; ++i) {
			Location<?> rval = rhs[i];
			Location<?>[] lval = java.util.Arrays.copyOfRange(lhs, j, rval.numberOfTypes());
			context = translateAssign(lval, rval, context);
			j = j + rval.numberOfTypes();
		}
		// Done
		return context;
	}

	/**
	 * Translate an individual assignment from one rval to one or more lvals. If
	 * there are multiple lvals, then a tuple is created to represent the
	 * left-hand side.
	 *
	 * @param lval
	 *            One or more expressions representing the left-hand side
	 * @param rval
	 *            A single expression representing the right-hand side
	 * @param context
	 * @return
	 */
	private Context translateAssign(Location<?>[] lval, Location<?> rval, Context context) {
		Pair<Expr, Context> rp = translateExpressionWithChecks(rval, context);
		context = rp.second();
		Expr[] ls = new Expr[lval.length];
		for (int i = 0; i != ls.length; ++i) {
			context = translateSingleAssignment(lval[i], rp.first(), context);
		}
		return context;
	}

	/**
	 * Translate an individual assignment from one rval to exactly one lval.
	 *
	 * @param lval
	 *            A single location representing the left-hand side
	 * @param rval
	 *            A single expression representing the right-hand side
	 * @param context
	 * @return
	 */
	private Context translateSingleAssignment(Location<?> lval, Expr rval, Context context) {

		// FIXME: this method is a bit of a kludge. It would be nicer,
		// eventually, to have all right-hand side expression represented in
		// WyTP directly. This could potentially be done by including an update
		// operation in WyTP ... ?

		switch (lval.getOpcode()) {
		case Bytecode.OPCODE_arrayindex:
			return translateArrayAssign((Location<Operator>) lval, rval, context);
		case Bytecode.OPCODE_dereference:
			// There's nothing useful we can do here.
			return translateDereference(lval, rval, context);
		case Bytecode.OPCODE_fieldload:
			return translateRecordAssign((Location<FieldLoad>) lval, rval, context);
		case Bytecode.OPCODE_varmove:
		case Bytecode.OPCODE_varcopy:
			return translateVariableAssign((Location<VariableAccess>) lval, rval, context);
		default:
			throw new InternalFailure("unknown lval encountered (" + lval + ")", context.getEnclosingFile().getEntry(),
					lval);
		}
	}

	/**
	 * Translate an assignment to a field.
	 *
	 * @param lval
	 *            The field access expression
	 * @param result
	 *            The value being assigned to the given array element
	 * @param context
	 *            The enclosing context
	 * @return
	 */
	private Context translateRecordAssign(Location<FieldLoad> lval, Expr rval,Context context) {
		SyntaxTree tree = lval.getEnclosingTree();
		WyilFile.Declaration decl = tree.getEnclosingDeclaration();
		try {
			Bytecode.FieldLoad bytecode = lval.getBytecode();
			Type.EffectiveRecord type =
					typeSystem.expandAsEffectiveRecord(lval.getOperand(0).getType());
			// Translate source expression
			Pair<Expr, Context> p =
					translateExpressionWithChecks(lval.getOperand(0), context);
			Expr originalSource = p.first();
			context = p.second();
			// Generate new source expression based of havoced variable
			Location<VariableAccess> var = extractAssignedVariable(lval);
			if (var != null) {
				context = context.havoc(var);
			}
			Expr newSource = translateExpression(lval.getOperand(0),
					context.getEnvironment());
			String[] fields = type.getFieldNames();
			//
			for (int i = 0; i != type.size(); ++i) {
				String field = fields[i];
				WyalFile.Identifier fieldIdentifier = new WyalFile.Identifier(field);
				Expr oldField = field.equals(bytecode.fieldName()) ? rval
						: new Expr.RecordAccess(originalSource, fieldIdentifier);
				oldField.attributes().addAll(lval.attributes());
				Expr newField = new Expr.RecordAccess(newSource, fieldIdentifier);
				context = context.assume(new Expr.Operator(Opcode.EXPR_eq, oldField, newField));
			}
			return context;
		} catch (ResolveError e) {
			throw new InternalFailure(e.getMessage(), decl.parent().getEntry(),
					lval, e);
		}
	}

	/**
	 * Translate an assignment to an array element.
	 *
	 * @param lval
	 *            The array assignment expression
	 * @param result
	 *            The value being assigned to the given array element
	 * @param context
	 *            The enclosing context
	 * @return
	 */
	private Context translateArrayAssign(Location<Operator> lval, Expr rval, Context context) {
		SyntaxTree tree = lval.getEnclosingTree();
		WyilFile.Declaration decl = tree.getEnclosingDeclaration();
		try {
			Type elementType = typeSystem.expandAsEffectiveArray(lval.getOperand(0).getType())
					.getWriteableElementType();
			// Translate src and index expressions
			Pair<Expr, Context> p1 = translateExpressionWithChecks(lval.getOperand(0), context);
			Expr originalSource = p1.first();
			context = p1.second();
			Pair<Expr, Context> p2 = translateExpressionWithChecks(lval.getOperand(1), context);
			Expr index = p2.first();
			context = p2.second();
			// Emit verification conditions to check access in bounds
			checkIndexOutOfBounds(lval, context);
			// Generate new source expression based of havoced variable
			Location<VariableAccess> var = extractAssignedVariable(lval);
			if (var != null) {
				context = context.havoc(var);
			}
			Expr newSource = translateExpression(lval.getOperand(0), context.getEnvironment());
			// Construct connection between new source expression and original
			// source expression
			Expr.Operator arrayUpdate = new Expr.Operator(Opcode.EXPR_arrupdt, originalSource, index, rval);
			Expr assumption = new Expr.Operator(Opcode.EXPR_eq,newSource,arrayUpdate);
			//
			return context.assume(assumption);
		} catch (ResolveError e) {
			throw new InternalFailure(e.getMessage(), decl.parent().getEntry(), lval, e);
		}
	}

	/**
	 * Translate an assignment to a variable
	 *
	 * @param lval
	 *            The array assignment expression
	 * @param result
	 *            The value being assigned to the given array element
	 * @param context
	 *            The enclosing context
	 * @return
	 */
	private Context translateDereference(Location<?> lval, Expr rval, Context context) {
		Expr e = translateAsUnknown(lval, context.getEnvironment());
		return context;
	}

	/**
	 * Translate an assignment to a variable
	 *
	 * @param lval
	 *            The array assignment expression
	 * @param result
	 *            The value being assigned to the given array element
	 * @param context
	 *            The enclosing context
	 * @return
	 */
	private Context translateVariableAssign(Location<VariableAccess> lval, Expr rval,Context context) {
		Location<VariableDeclaration> decl = (Location<VariableDeclaration>) lval.getOperand(0);
		context = context.havoc(decl.getIndex());
		WyalFile.VariableDeclaration nVersionedVar = context.read(decl);
		Expr.VariableAccess var = new Expr.VariableAccess(nVersionedVar);
		return context.assume(new Expr.Operator(Opcode.EXPR_eq, var, rval));
	}

	/**
	 * Determine the variable at the root of a given sequence of assignments, or
	 * return null if there is no statically determinable variable.
	 *
	 * @param lval
	 * @return
	 */
	private Location<VariableAccess> extractAssignedVariable(Location<?> lval) {
		SyntaxTree tree = lval.getEnclosingTree();
		WyilFile.Declaration decl = tree.getEnclosingDeclaration();
		//
		switch (lval.getOpcode()) {
		case Bytecode.OPCODE_arrayindex:
			return extractAssignedVariable(lval.getOperand(0));
		case Bytecode.OPCODE_dereference:
			return null;
		case Bytecode.OPCODE_fieldload:
			return extractAssignedVariable(lval.getOperand(0));
		case Bytecode.OPCODE_varmove:
		case Bytecode.OPCODE_varcopy:
			return (Location<VariableAccess>) lval;
		default:
			throw new InternalFailure("unknown lval encountered (" + lval + ")", decl.parent().getEntry(), lval);
		}
	}

	/**
	 * Translate an assume statement. This simply updates the current context to
	 * assume that the given condition holds true (i.e. regardless of whether it
	 * does or not). The purpose of assume statements is to allow some level of
	 * interaction between the programmer and the verifier. That is, the
	 * programmer can assume things which he/she knows to be true which the
	 * verifier cannot prove (for whatever reason).
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateAssume(Location<Assume> stmt, Context context) {
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getOperand(0), context);
		Expr condition = p.first();
		context = p.second();
		return context.assume(condition);
	}

	/**
	 * Translate a break statement. This takes the current context and pushes it
	 * into the enclosing loop scope. It will then be extracted later and used.
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateBreak(Location<Break> stmt, Context context) {
		LoopScope enclosingLoop = context.getEnclosingLoopScope();
		enclosingLoop.addBreakContext(context);
		return null;
	}

	/**
	 * Translate a continue statement. This takes the current context and pushes
	 * it into the enclosing loop scope. It will then be extracted later and
	 * used.
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateContinue(Location<Continue> stmt, Context context) {
		LoopScope enclosingLoop = context.getEnclosingLoopScope();
		enclosingLoop.addContinueContext(context);
		return null;
	}

	/**
	 * Translate a DoWhile statement.
	 *
	 * @param stmt
	 * @param context
	 * @return
	 */
	private Context translateDoWhile(Location<DoWhile> stmt, Context context) {
		WyilFile.Declaration declaration = context.getEnvironment().getParent().enclosingDeclaration;
		Location<?>[] loopInvariant = stmt.getOperandGroup(0);
		// Translate the loop invariant and generate appropriate macro
		translateLoopInvariantMacros(loopInvariant, declaration, context.wyalFile);
		// Rule 1. Check loop invariant after first iteration
		LoopScope firstScope = new LoopScope();
		Context beforeFirstBodyContext = context.newLoopScope(firstScope);
		Context afterFirstBodyContext = translateStatementBlock(stmt.getBlock(0), beforeFirstBodyContext);
		// Join continue contexts together since they must also preserve the
		// loop invariant
		afterFirstBodyContext = joinDescendants(beforeFirstBodyContext, afterFirstBodyContext,
				firstScope.continueContexts);
		//
		checkLoopInvariant("loop invariant not established by first iteration", loopInvariant, afterFirstBodyContext);
		// Rule 2. Check loop invariant preserved on subsequence iterations. On
		// entry to the loop body we must havoc all modified variables. This is
		// necessary as such variables should retain their values from before
		// the loop.
		LoopScope arbitraryScope = new LoopScope();
		Context beforeArbitraryBodyContext = context.newLoopScope(arbitraryScope).havoc(stmt.getOperandGroup(1));
		beforeArbitraryBodyContext = assumeLoopInvariant(loopInvariant, beforeArbitraryBodyContext);
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getOperand(0), beforeArbitraryBodyContext);
		Expr trueCondition = p.first();
		beforeArbitraryBodyContext = p.second().assume(trueCondition);
		Context afterArbitraryBodyContext = translateStatementBlock(stmt.getBlock(0), beforeArbitraryBodyContext);
		// Join continue contexts together since they must also preserve the
		// loop invariant
		afterArbitraryBodyContext = joinDescendants(beforeArbitraryBodyContext, afterArbitraryBodyContext,
				arbitraryScope.continueContexts);
		//
		checkLoopInvariant("loop invariant not restored", loopInvariant, afterArbitraryBodyContext);
		// Rule 3. Assume loop invariant holds.
		Context exitContext = context.havoc(stmt.getOperandGroup(1));
		exitContext = assumeLoopInvariant(loopInvariant, exitContext);
		Expr falseCondition = invertCondition(translateExpression(stmt.getOperand(0), exitContext.getEnvironment()),
				stmt.getOperand(0));
		exitContext = exitContext.assume(falseCondition);
		//
		// Finally, need to join any break contexts from either first iteration
		// or arbitrary iteration
		exitContext = joinDescendants(context, exitContext, firstScope.breakContexts, arbitraryScope.breakContexts);
		//
		return exitContext;
	}

	/**
	 * Translate a fail statement. Execution should never reach such a
	 * statement. Hence, we need to emit a verification condition to ensure this
	 * is the case.
	 *
	 * @param stmt
	 * @param context
	 * @return
	 */
	private Context translateFail(Location<Fail> stmt, Context context) {
		Expr condition = new Expr.Constant(new Value.Bool(false));
		//
		VerificationCondition verificationCondition = new VerificationCondition("possible panic", context.assumptions,
				condition, stmt.attributes());
		context.emit(verificationCondition);
		//
		return null;
	}

	/**
	 * Translate an if statement. This translates the true and false branches
	 * and then recombines them together to form an updated environment. This is
	 * challenging when the environments are updated independently in both
	 * branches.
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateIf(Location<If> stmt, Context context) {
		//
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getOperand(0), context);
		Expr trueCondition = p.first();
		// FIXME: this is broken as includes assumptions propagated through
		// logical &&'s
		context = p.second();
		Expr falseCondition = invertCondition(trueCondition, stmt.getOperand(0));
		//
		Context trueContext = context.assume(trueCondition);
		Context falseContext = context.assume(falseCondition);
		//
		trueContext = translateStatementBlock(stmt.getBlock(0), trueContext);
		if (stmt.numberOfBlocks() > 1) {
			falseContext = translateStatementBlock(stmt.getBlock(1), falseContext);
		}
		// Finally, we must join the two context's back together. This ensures
		// that information from either side is properly preserved
		return joinDescendants(context, new Context[] { trueContext, falseContext });
	}

	/**
	 * Translate a named block
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateNamedBlock(Location<NamedBlock> stmt, Context context) {
		return translateStatementBlock(stmt.getBlock(0), context);
	}

	/**
	 * Translate a return statement. If a return value is given, then this must
	 * ensure that the post-condition of the enclosing function or method is met
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateReturn(Location<Return> stmt, Context context) {
		//
		SyntaxTree tree = stmt.getEnclosingTree();
		WyilFile.FunctionOrMethod declaration = (WyilFile.FunctionOrMethod) tree.getEnclosingDeclaration();
		Type.FunctionOrMethod type = declaration.type();
		Location<?>[] returns = stmt.getOperands();
		List<Location<Bytecode.Expr>> postcondition = declaration.getPostcondition();
		//
		if (returns.length > 0) {
			// There is at least one return value. Therefore, we need to check
			// any preconditions for those return expressions and, potentially,
			// ensure any postconditions of the cnlosing function/method are
			// met.
			Pair<Expr[], Context> p = translateExpressionsWithChecks(returns, context);
			Expr[] exprs = p.first();
			context = p.second();
			//
			if (postcondition.size() > 0) {
				// There is at least one return value and at least one
				// postcondition clause. Therefore, we need to check the return
				// values against the post condition(s). One of the difficulties
				// here is that the postcondition will refer to parameters as
				// they were on entry to the function/method, not as they are
				// now.
				Expr[] arguments = new Expr[type.params().length +
				                            type.returns().length];
				// Translate parameters as arguments to post-condition
				// invocation
				for (int i = 0; i != type.params().length; ++i) {
					Location<VariableDeclaration> var =
							(Location<VariableDeclaration>) tree.getLocation(i);
					WyalFile.VariableDeclaration vd = context.readFirst(var);
					arguments[i] = new Expr.VariableAccess(vd);
				}
				// Copy over return expressions as arguments for invocation(s)
				System.arraycopy(exprs, 0, arguments, type.params().length,
						exprs.length);
				//
				String prefix = declaration.name() + "_ensures_";
				// Finally, generate an appropriate verification condition to
				// check
				// each postcondition clause
				for (int i = 0; i != postcondition.size(); ++i) {
					WyalFile.Name name = new WyalFile.Name(new WyalFile.Identifier(prefix + i));
					Expr clause = new Expr.Invoke(null, name, arguments);
					context.emit(new VerificationCondition("postcondition not satisfied", context.assumptions, clause,
							stmt.attributes()));
				}
			}
		}
		// Return null to signal that execution does not continue after this
		// return statement.
		return null;
	}

	/**
	 * Translate a skip statement, which obviously does nothing
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateSkip(Location<Skip> stmt, Context context) {
		return context;
	}

	/**
	 * Translate a switch statement.
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateSwitch(Location<Switch> stmt, Context context) {
		Bytecode.Switch bytecode = stmt.getBytecode();
		Bytecode.Case[] cases = bytecode.cases();
		//
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getOperand(0), context);
		Expr value = p.first();
		context = p.second();
		//
		WyalFile.Stmt defaultValue = null;
		Context[] descendants = new Context[cases.length + 1];
		Context defaultContext = null;
		//
		for (int i = 0; i != cases.length; ++i) {
			Bytecode.Case caSe = cases[i];
			Context caseContext;
			// Setup knowledge from case values
			if (!caSe.isDefault()) {
				WyalFile.Stmt e = null;
				for (Constant constant : caSe.values()) {
					Expr v = convert(constant, stmt);
					e = or(e, new Expr.Operator(WyalFile.Opcode.EXPR_eq, value, v));
					defaultValue = and(defaultValue, new Expr.Operator(WyalFile.Opcode.EXPR_neq, value, v));
				}
				caseContext = context.assume(e);
				descendants[i] = translateStatementBlock(stmt.getBlock(i), caseContext);
			} else {
				defaultContext = context.assume(defaultValue);
				defaultContext = translateStatementBlock(stmt.getBlock(i), defaultContext);
			}
		}
		// Sort out default context
		if (defaultContext == null) {
			// indicates no default block was present, so we just assume what we
			// know and treat it as a fall through.
			defaultContext = context.assume(defaultValue);
		}
		descendants[descendants.length - 1] = defaultContext;
		//
		return joinDescendants(context, descendants);
	}

	/**
	 * Translate a While statement.
	 *
	 * @param stmt
	 * @param context
	 * @return
	 */
	private Context translateWhile(Location<While> stmt, Context context) {
		WyilFile.Declaration declaration = context.getEnvironment().getParent().enclosingDeclaration;
		Location<?>[] loopInvariant = stmt.getOperandGroup(0);
		// Translate the loop invariant and generate appropriate macro
		translateLoopInvariantMacros(loopInvariant, declaration, context.wyalFile);
		// Rule 1. Check loop invariant on entry
		checkLoopInvariant("loop invariant does not hold on entry", loopInvariant, context);
		// Rule 2. Check loop invariant preserved. On entry to the loop body we
		// must havoc all modified variables. This is necessary as such
		// variables should retain their values from before the loop.
		LoopScope scope = new LoopScope();
		Context beforeBodyContext = context.newLoopScope(scope).havoc(stmt.getOperandGroup(1));
		beforeBodyContext = assumeLoopInvariant(loopInvariant, beforeBodyContext);
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getOperand(0), beforeBodyContext);
		Expr trueCondition = p.first();
		beforeBodyContext = p.second().assume(trueCondition);
		Context afterBodyContext = translateStatementBlock(stmt.getBlock(0), beforeBodyContext);
		// Join continue contexts together since they must also preserve the
		// loop invariant
		afterBodyContext = joinDescendants(beforeBodyContext, afterBodyContext, scope.continueContexts);
		checkLoopInvariant("loop invariant not restored", loopInvariant, afterBodyContext);
		// Rule 3. Assume loop invariant holds.
		Context exitContext = context.havoc(stmt.getOperandGroup(1));
		exitContext = assumeLoopInvariant(loopInvariant, exitContext);
		Expr falseCondition = invertCondition(translateExpression(stmt.getOperand(0), exitContext.getEnvironment()),
				stmt.getOperand(0));
		exitContext = exitContext.assume(falseCondition);
		//
		// Finally, need to join any break contexts
		exitContext = joinDescendants(context, exitContext, scope.breakContexts);
		//
		return exitContext;
	}

	/**
	 * Translate the sequence of invariant expressions which constitute the loop
	 * invariant of a loop into one or more macros
	 *
	 * @param loopInvariant
	 *            The clauses making up the loop invariant
	 * @param environment
	 * @param wyalFile
	 */
	private void translateLoopInvariantMacros(Location<?>[] loopInvariant, WyilFile.Declaration declaration,
			WyalFile wyalFile) {
		//
		String prefix = declaration.name() + "_loopinvariant_";
		//
		for (int i = 0; i != loopInvariant.length; ++i) {
			Location<?> clause = loopInvariant[i];
			WyalFile.Identifier name = new WyalFile.Identifier(prefix + clause.getIndex());
			// Construct fresh environment for this macro. This is necessary to
			// avoid name clashes with subsequent macros.
			GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
			LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
			WyalFile.VariableDeclaration[] vars = generateLoopInvariantTypePattern(declaration, loopInvariant,
					localEnvironment);
			WyalFile.Stmt.Block e = translateAsBlock(clause, localEnvironment.clone());
			Named.Macro macro = new Named.Macro(name, vars, e);
			wyalFile.allocate(macro);
		}
	}

	/**
	 * Emit verification condition(s) to ensure that the clauses of loop
	 * invariant hold at a given point
	 *
	 * @param loopInvariant
	 *            The clauses making up the loop invariant
	 * @param context
	 */
	private void checkLoopInvariant(String msg, Location<?>[] loopInvariant, Context context) {
		//
		LocalEnvironment environment = context.getEnvironment();
		WyilFile.FunctionOrMethod declaration = (WyilFile.FunctionOrMethod) environment
				.getParent().enclosingDeclaration;
		SyntaxTree tree = declaration.getTree();
		// FIXME: this is completely broken in the case of multiple loops. The
		// problem is that we need to distinguish the macro names based on some
		// kind of block identifier.
		String prefix = declaration.name() + "_loopinvariant_";
		// Construct argument to invocation
		int[] localVariables = SyntaxTrees.determineUsedVariables(loopInvariant);
		Expr[] arguments = new Expr[localVariables.length];
		for (int i = 0; i != arguments.length; ++i) {
			Location<VariableAccess> var = (Location<VariableAccess>)
					tree.getLocation(localVariables[i]);
			arguments[i] = new
					Expr.VariableAccess(environment.read(var.getIndex()));
		}
		//
		for (int i = 0; i != loopInvariant.length; ++i) {
			Location<?> clause = loopInvariant[i];
			WyalFile.Name name = new WyalFile.Name(new WyalFile.Identifier(prefix + clause.getIndex()));
			Expr macroCall = new Expr.Invoke(null, name, arguments);
			context.emit(new VerificationCondition(msg, context.assumptions,
					macroCall, clause.attributes()));
		}
	}

	private Context assumeLoopInvariant(Location<?>[] loopInvariant, Context context) {
		//
		LocalEnvironment environment = context.getEnvironment();
		WyilFile.FunctionOrMethod declaration = (WyilFile.FunctionOrMethod) environment
				.getParent().enclosingDeclaration;
		SyntaxTree tree = declaration.getTree();
		// FIXME: this is completely broken in the case of multiple loops. The
		// problem is that we need to distinguish the macro names based on some
		// kind of block identifier.
		String prefix = declaration.name() + "_loopinvariant_";
		// Construct argument to invocation
		int[] localVariables = SyntaxTrees.determineUsedVariables(loopInvariant);
		Expr[] arguments = new Expr[localVariables.length];
		for (int i = 0; i != arguments.length; ++i) {
			Location<VariableAccess> var = (Location<VariableAccess>)
					tree.getLocation(localVariables[i]);
			arguments[i] = new Expr.VariableAccess(environment.read(var.getIndex()));
		}
		//
		for (int i = 0; i != loopInvariant.length; ++i) {
			Location<?> clause = loopInvariant[i];
			WyalFile.Name name = new WyalFile.Name(new WyalFile.Identifier(prefix + clause.getIndex()));
			Expr macroCall = new Expr.Invoke(null, name, arguments);
			context = context.assume(macroCall);
		}
		//
		return context;
	}

	/**
	 * Translate a variable declaration.
	 *
	 * @param stmt
	 * @param context
	 * @return
	 */
	private Context translateVariableDeclaration(Location<VariableDeclaration> stmt, Context context) {
		if (stmt.numberOfOperands() > 0) {
			Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getOperand(0), context);
			context = context.write(stmt.getIndex(), p.first());
		}
		return context;
	}

	// =========================================================================
	// Checked Expressions
	// =========================================================================

	/**
	 * Translate zero or more expressions into their equivalent WyAL
	 * expressions. At the same time, emit verification conditions to check that
	 * the expression's preconditions. For example, in the expression
	 * <code>x[i] + 1</code> we need to check that <code>i</code> is within
	 * bounds.
	 *
	 * @param expr
	 *            --- Expression to be translated
	 * @param context
	 *            --- Context in which translation is occurring
	 * @return
	 */
	private Pair<Expr[], Context> translateExpressionsWithChecks(Location<?>[] exprs, Context context) {
		// Generate expression preconditions as verification conditions
		for (Location<?> expr : exprs) {
			checkExpressionPreconditions(expr, context);
		}
		// Gather up any postconditions from function invocations
		for (Location<?> expr : exprs) {
			context = assumeExpressionPostconditions(expr, context);
		}
		// Translate expression in the normal fashion
		return new Pair<>(translateExpressions(exprs, context.getEnvironment()), context);
	}

	/**
	 * Translate a given expression into its equivalent WyAL expression. At the
	 * same time, emit verification conditions to check that the expression's
	 * preconditions. For example, in the expression <code>x[i] + 1</code> we
	 * need to check that <code>i</code> is within bounds.
	 *
	 * @param expr
	 *            --- Expression to be translated
	 * @param context
	 *            --- Context in which translation is occurring
	 * @return
	 */
	private Pair<Expr, Context> translateExpressionWithChecks(Location<?> expr, Context context) {
		// Generate expression preconditions as verification conditions
		checkExpressionPreconditions(expr, context);
		// Gather up any postconditions from function invocations
		context = assumeExpressionPostconditions(expr, context);
		// Translate expression in the normal fashion
		return new Pair<>(translateExpression(expr, context.getEnvironment()), context);
	}

	@SuppressWarnings("unchecked")
	private void checkExpressionPreconditions(Location<?> expr, Context context) {
		WyilFile.Declaration decl = expr.getEnclosingTree().getEnclosingDeclaration();
		try {
			// First, recurse all subexpressions
			int opcode = expr.getOpcode();
			if (opcode == Bytecode.OPCODE_logicaland) {
				// In the case of a logical and condition we need to propagate
				// the left-hand side as an assumption into the right-hand side.
				// This is an artifact of short-circuiting whereby terms on the
				// right-hand side only execute when the left-hand side is known
				// to hold.
				for (int i = 0; i != expr.numberOfOperands(); ++i) {
					checkExpressionPreconditions(expr.getOperand(i), context);
					Expr e = translateExpression(expr.getOperand(i), context.getEnvironment());
					context = context.assume(e);
				}
			} else if (opcode != Bytecode.OPCODE_varcopy && opcode != Bytecode.OPCODE_varmove) {
				// In the case of a general expression, we just recurse any
				// subexpressions without propagating information forward. We
				// must ignore variable accesses here, because they refer back
				// to the relevant variable declaration.
				for (int i = 0; i != expr.numberOfOperands(); ++i) {
					checkExpressionPreconditions(expr.getOperand(i), context);
				}
				for (int i = 0; i != expr.numberOfOperandGroups(); ++i) {
					Location<?>[] group = expr.getOperandGroup(i);
					for (Location<?> e : group) {
						checkExpressionPreconditions(e, context);
					}
				}
			}
			// Second, perform actual precondition checks
			switch (expr.getOpcode()) {
			case Bytecode.OPCODE_invoke:
				checkInvokePreconditions((Location<Invoke>) expr, context);
				break;
			case Bytecode.OPCODE_div:
			case Bytecode.OPCODE_rem:
				checkDivideByZero((Location<Operator>) expr, context);
				break;
			case Bytecode.OPCODE_arrayindex:
				checkIndexOutOfBounds((Location<Operator>) expr, context);
				break;
			case Bytecode.OPCODE_arraygen:
				checkArrayGeneratorLength((Location<Operator>) expr, context);
				break;
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			throw new InternalFailure(e.getMessage(), decl.parent().getEntry(), expr, e);
		}
	}

	private void checkInvokePreconditions(Location<Invoke> expr, Context context) throws Exception {
		WyilFile.Declaration declaration = expr.getEnclosingTree().getEnclosingDeclaration();
		Bytecode.Invoke bytecode = expr.getBytecode();
		//
		WyilFile.FunctionOrMethod fm = lookupFunctionOrMethod(bytecode.name(), bytecode.type(), expr);
		int numPreconditions = fm.getPrecondition().size();
		//
		if (numPreconditions > 0) {
			// There is at least one precondition for the function/method being
			// called. Therefore, we need to generate a verification condition
			// which will check that the precondition holds.
			//
			Expr[] arguments = translateExpressions(expr.getOperands(), context.getEnvironment());
			String prefix = bytecode.name().name() + "_requires_";
			// Finally, generate an appropriate verification condition to check
			// each precondition clause
			for (int i = 0; i != numPreconditions; ++i) {
				// FIXME: name needs proper path information
				WyalFile.Name name = new WyalFile.Name(new WyalFile.Identifier(prefix + i));
				Expr clause = new Expr.Invoke(null, name, arguments);
				context.emit(new VerificationCondition("precondition not satisfied", context.assumptions, clause,
						expr.attributes()));
			}
		}
	}

	private void checkDivideByZero(Location<Operator> expr, Context context) {
		Expr rhs = translateExpression(expr.getOperand(1), context.getEnvironment());
		Value zero = new Value.Int(BigInteger.ZERO);
		Expr.Constant constant = new Expr.Constant(zero);
		Expr neqZero = new Expr.Operator(WyalFile.Opcode.EXPR_neq, rhs, constant);
		//
		context.emit(new VerificationCondition("division by zero", context.assumptions, neqZero, expr.attributes()));
	}

	private void checkIndexOutOfBounds(Location<Operator> expr, Context context) {
		Expr src = translateExpression(expr.getOperand(0), context.getEnvironment());
		Expr idx = translateExpression(expr.getOperand(1), context.getEnvironment());
		Expr zero = new Expr.Constant(new Value.Int(BigInteger.ZERO));
		Expr length = new Expr.Operator(WyalFile.Opcode.EXPR_arrlen, src);
		//
		Expr negTest = new Expr.Operator(WyalFile.Opcode.EXPR_gteq, idx, zero);
		Expr lenTest = new Expr.Operator(WyalFile.Opcode.EXPR_lt, idx, length);
		//
		context.emit(new VerificationCondition("index out of bounds (negative)", context.assumptions, negTest,
				expr.attributes()));
		context.emit(new VerificationCondition("index out of bounds (not less than length)", context.assumptions,
				lenTest, expr.attributes()));
	}

	private void checkArrayGeneratorLength(Location<Operator> expr, Context context) {
		Expr rhs = translateExpression(expr.getOperand(1), context.getEnvironment());
		Value zero = new Value.Int(BigInteger.ZERO);
		Expr.Constant constant = new Expr.Constant(zero);
		Expr neqZero = new Expr.Operator(WyalFile.Opcode.EXPR_gteq, rhs, constant);
		//
		context.emit(
				new VerificationCondition("negative length possible", context.assumptions, neqZero, expr.attributes()));
	}

	private Context assumeExpressionPostconditions(Location<?> expr, Context context) {
		WyilFile.Declaration decl = expr.getEnclosingTree().getEnclosingDeclaration();
		try {
			// First, propagate through all subexpressions
			for (int i = 0; i != expr.numberOfOperands(); ++i) {
				context = assumeExpressionPostconditions(expr.getOperand(i), context);
			}
			for (int i = 0; i != expr.numberOfOperandGroups(); ++i) {
				Location<?>[] group = expr.getOperandGroup(i);
				for (Location<?> e : group) {
					context = assumeExpressionPostconditions(e, context);
				}
			}
			switch (expr.getOpcode()) {
			case Bytecode.OPCODE_invoke:
				context = assumeInvokePostconditions((Location<Invoke>) expr, context);
				break;
			}
			return context;
		} catch (InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			throw new InternalFailure(e.getMessage(), decl.parent().getEntry(), expr, e);
		}
	}

	private Context assumeInvokePostconditions(Location<Invoke> expr, Context context) throws Exception {
		WyilFile.Declaration declaration = expr.getEnclosingTree().getEnclosingDeclaration();
		Bytecode.Invoke bytecode = expr.getBytecode();
		//
		WyilFile.FunctionOrMethod fm = lookupFunctionOrMethod(bytecode.name(), bytecode.type(), expr);
		int numPostconditions = fm.getPostcondition().size();
		//
		if (numPostconditions > 0) {
			// There is at least one postcondition for the function/method being
			// called. Therefore, we need to generate a verification condition
			// which will check that the precondition holds.
			//
			Expr[] parameters = translateExpressions(expr.getOperands(), context.getEnvironment());
			Expr[] arguments = java.util.Arrays.copyOf(parameters, parameters.length + fm.type().returns().length);
			// FIXME: following broken for multiple returns
			arguments[arguments.length - 1] = translateExpression(expr, context.getEnvironment());
			//
			String prefix = bytecode.name().name() + "_ensures_";
			// Finally, generate an appropriate verification condition to check
			// each precondition clause
			for (int i = 0; i != numPostconditions; ++i) {
				// FIXME: name needs proper path information
				WyalFile.Name name = new WyalFile.Name(new WyalFile.Identifier(prefix + i));
				Expr clause = new Expr.Invoke(null, name, arguments);
				context = context.assume(clause);
			}
		}
		//
		return context;
	}

	private WyalFile.Stmt.Block translateAsBlock(Location<?> loc, LocalEnvironment environment) {
		WyalFile.Stmt stmt = translateAsStatement(loc, environment);
		return new WyalFile.Stmt.Block(stmt);
	}

	private WyalFile.Stmt translateAsStatement(Location<?> loc, LocalEnvironment environment) {
		return translateExpression(loc, environment);
	}

	// =========================================================================
	// Expression
	// =========================================================================

	private Expr[] translateExpressions(Location<?>[] loc, LocalEnvironment environment) {
		Expr[] results = new Expr[loc.length];
		for (int i = 0; i != results.length; ++i) {
			results[i] = translateExpression(loc[i], environment);
		}
		return results;
	}

	/**
	 * Transform a given bytecode location into its equivalent WyAL expression.
	 *
	 * @param location
	 *            The bytecode location to be translated
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Expr translateExpression(Location<?> loc, LocalEnvironment environment) {
		WyilFile.Declaration decl = loc.getEnclosingTree().getEnclosingDeclaration();
		Expr result;
		try {
			switch (loc.getOpcode()) {
			case Bytecode.OPCODE_const:
				result = translateConstant((Location<Const>) loc, environment);
				break;
			case Bytecode.OPCODE_convert:
				result = translateConvert((Location<Convert>) loc, environment);
				break;
			case Bytecode.OPCODE_fieldload:
				result = translateFieldLoad((Location<FieldLoad>) loc, environment);
				break;
			case Bytecode.OPCODE_indirectinvoke:
				result = translateIndirectInvoke((Location<IndirectInvoke>) loc, environment);
				break;
			case Bytecode.OPCODE_invoke:
				result = translateInvoke((Location<Invoke>) loc, environment);
				break;
			case Bytecode.OPCODE_lambda:
				result = translateLambda((Location<Lambda>) loc, environment);
				break;
			case Bytecode.OPCODE_some:
			case Bytecode.OPCODE_all:
				result = translateQuantifier((Location<Quantifier>) loc, environment);
				break;
			case Bytecode.OPCODE_varmove:
			case Bytecode.OPCODE_varcopy:
				result = translateVariableAccess((Location<VariableAccess>) loc, environment);
				break;
			default:
				result = translateOperator((Location<Operator>) loc, environment);
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			throw new InternalFailure(e.getMessage(), decl.parent().getEntry(), loc, e);
		}
		result.attributes().addAll(loc.attributes());
		return result;
	}

	private Expr translateConstant(Location<Const> expr, LocalEnvironment environment) {
		Bytecode.Const bytecode = expr.getBytecode();
		// FIXME: the following is something of a hack to avoid having to
		// translate function pointers. However, it's not a general fix, and i'm
		// not sure what the right solution is. Perhaps having an "unknown
		// value" as the WyCS level might work (and be useful to replace
		// translateAsUnknown).
		if (bytecode.constant() instanceof Constant.FunctionOrMethod) {
			return translateAsUnknown(expr, environment);
		}
		return convert(bytecode.constant(), expr);
	}

	private Expr translateConvert(Location<Convert> expr, LocalEnvironment environment) {
		// TODO: check whether need to do any more here
		return translateExpression(expr.getOperand(0), environment);
	}

	private Expr translateFieldLoad(Location<FieldLoad> expr, LocalEnvironment environment) {
		try {
			Bytecode.FieldLoad bytecode = expr.getBytecode();
			Location<?> srcOperand = expr.getOperand(0);
			Type.EffectiveRecord er = typeSystem.expandAsEffectiveRecord(srcOperand.getType());
			// Now, translate source expression
			Expr src = translateExpression(srcOperand, environment);
			// Generate field name identifier
			WyalFile.Identifier field = new WyalFile.Identifier(bytecode.fieldName());
			// Done
			return new Expr.RecordAccess(src, field);
		} catch (ResolveError e) {
			SyntaxTree tree = expr.getEnclosingTree();
			throw new InternalFailure(e.getMessage(), tree.getEnclosingDeclaration().parent().getEntry(), expr, e);
		}
	}

	private Expr translateIndirectInvoke(Location<IndirectInvoke> expr, LocalEnvironment environment) {
		// FIXME: need to implement this
		return translateAsUnknown(expr, environment);
	}

	private Expr translateInvoke(Location<Invoke> expr, LocalEnvironment environment) {
		Bytecode.Invoke bytecode = expr.getBytecode();
		Expr[] operands = translateExpressions(expr.getOperands(), environment);
		//
		// FIXME: name needs proper path information
		WyalFile.Name name = convert(bytecode.name());
		return new Expr.Invoke(null, name, operands);
	}

	private Expr translateLambda(Location<Lambda> expr, LocalEnvironment environment) {
		// FIXME: need to implement this
		return translateAsUnknown(expr, environment);
	}

	private Expr translateOperator(Location<Operator> expr, LocalEnvironment environment) {
		Bytecode.Operator bytecode = expr.getBytecode();
		Bytecode.OperatorKind kind = bytecode.kind();
		switch (kind) {
		case NOT:
			return translateNotOperator(expr, environment);
		case NEG:
		case ARRAYLENGTH:
			return translateUnaryOperator(unaryOperatorMap.get(kind), expr, environment);
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
		case GTEQ:
		case AND:
		case OR:
			return translateBinaryOperator(binaryOperatorMap.get(kind), expr, environment);
		case IS:
			return translateIs(expr, environment);
		case ARRAYINDEX:
			return translateArrayIndex(expr, environment);
		case ARRAYCONSTRUCTOR:
			return translateArrayInitialiser(expr, environment);
		case ARRAYGENERATOR:
			return translateArrayGenerator(expr, environment);
		case RECORDCONSTRUCTOR:
			return translateRecordInitialiser(expr, environment);
		case RIGHTSHIFT:
		case LEFTSHIFT:
		case BITWISEAND:
		case BITWISEOR:
		case BITWISEXOR:
		case BITWISEINVERT:
		case DEREFERENCE:
		case NEW:
			return translateAsUnknown(expr, environment);
		default:
			// FIXME: need to implement this
			throw new RuntimeException("Implement me! " + kind);
		}
	}

	private Expr translateNotOperator(Location<Operator> expr, LocalEnvironment environment) {
		Expr e = translateExpression(expr.getOperand(0), environment);
		return invertCondition(e, expr.getOperand(0));
	}

	private Expr translateUnaryOperator(WyalFile.Opcode op, Location<Operator> expr, LocalEnvironment environment) {
		Expr e = translateExpression(expr.getOperand(0), environment);
		return new Expr.Operator(op, e);
	}

	private Expr translateBinaryOperator(WyalFile.Opcode op, Location<Operator> expr, LocalEnvironment environment) {
		Expr lhs = translateExpression(expr.getOperand(0), environment);
		Expr rhs = translateExpression(expr.getOperand(1), environment);
		return new Expr.Operator(op, lhs, rhs);
	}

	private Expr translateIs(Location<Operator> expr, LocalEnvironment environment) {
		Expr lhs = translateExpression(expr.getOperand(0), environment);
		Location<Const> rhs = (Location<Const>) expr.getOperand(1);
		Bytecode.Const bytecode = rhs.getBytecode();
		Constant.Type constant = (Constant.Type) bytecode.constant();
		WyalFile.Type typeTest = convert(constant.value(), environment.getParent().enclosingDeclaration);
		return new Expr.Is(lhs, typeTest);
	}

	private Expr translateArrayIndex(Location<Operator> expr, LocalEnvironment environment) {
		Expr lhs = translateExpression(expr.getOperand(0), environment);
		Expr rhs = translateExpression(expr.getOperand(1), environment);
		return new Expr.Operator(WyalFile.Opcode.EXPR_arridx, lhs, rhs);
	}

	private Expr translateArrayGenerator(Location<Operator> expr, LocalEnvironment environment) {
		Expr element = translateExpression(expr.getOperand(0), environment);
		Expr count = translateExpression(expr.getOperand(1), environment);
		environment = environment.write(expr.getIndex());
		return new Expr.Operator(WyalFile.Opcode.EXPR_arrgen, element, count);
	}

	private Expr translateArrayInitialiser(Location<Operator> expr, LocalEnvironment environment) {
		Expr[] vals = translateExpressions(expr.getOperands(), environment);
		return new Expr.Operator(WyalFile.Opcode.EXPR_arrinit, vals);
	}

	private Expr translateQuantifier(Location<Quantifier> expr, LocalEnvironment environment) {
		Bytecode.Quantifier bytecode = expr.getBytecode();
		// Determine the type and names of each quantified variable.
		WyalFile.VariableDeclaration[] pattern = generateQuantifierTypePattern(expr);
		// Apply quantifier ranges
		Expr ranges = generateQuantifierRanges(expr, environment);
		// Generate quantifier body
		Expr body = translateExpression(expr.getOperand(0), environment);
		// Generate quantifier expression
		switch (bytecode.kind()) {
		case ALL:
			body = new Expr.Operator(Opcode.EXPR_implies, ranges, body);
			return new Expr.Quantifier(Opcode.EXPR_forall,pattern, body);
		case SOME:
		default:
			body = new Expr.Operator(Opcode.EXPR_and, ranges, body);
			return new Expr.Quantifier(Opcode.EXPR_exists,pattern, body);
		}
	}

	private Expr translateRecordInitialiser(Location<Operator> expr, LocalEnvironment environment)  {
		SyntaxTree tree = expr.getEnclosingTree();
		WyilFile.Declaration decl = tree.getEnclosingDeclaration();
		try {
			Type.EffectiveRecord t = typeSystem.expandAsEffectiveRecord(expr.getType());
			String[] fields = t.getFieldNames();
			Expr[] vals = translateExpressions(expr.getOperands(), environment);
			WyalFile.Pair<WyalFile.Identifier, Expr>[] pairs = new WyalFile.Pair[vals.length];
			//
			for (int i = 0; i != vals.length; ++i) {
				pairs[i] = new WyalFile.Pair<>(new WyalFile.Identifier(fields[i]), vals[i]);
			}
			return new Expr.RecordInitialiser(pairs);
		} catch (ResolveError e) {
			throw new InternalFailure(e.getMessage(), decl.parent().getEntry(), expr, e);
		}
	}

	/**
	 * Translating as unknown basically means we're not representing the
	 * operation in question at the verification level. This could be something
	 * that we'll implement in the future, or maybe not.
	 *
	 * @param expr
	 * @param environment
	 * @return
	 */
	private Expr translateAsUnknown(Location<?> expr, LocalEnvironment environment) {
		// What we're doing here is creating a completely fresh variable to
		// represent the return value. This is basically saying the return value
		// could be anything, and we don't care what.
		 environment = environment.write(expr.getIndex());
		 WyalFile.VariableDeclaration r = environment.read(expr.getIndex());
		 return new Expr.VariableAccess(r);
	}

	/**
	 * Generate a type pattern representing the type and name of all quantifier
	 * variables described by this quantifier.
	 *
	 * @param expr
	 * @return
	 */
	private WyalFile.VariableDeclaration[] generateQuantifierTypePattern(Location<Quantifier> expr) {
		SyntaxTree tree = expr.getEnclosingTree();
		WyilFile.Declaration decl = tree.getEnclosingDeclaration();
		//
		WyalFile.VariableDeclaration[] vardecls = new WyalFile.VariableDeclaration[expr.numberOfOperandGroups()];
		for (int i = 0; i != expr.numberOfOperandGroups(); ++i) {
			Location<?>[] group = expr.getOperandGroup(i);
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) group[0];
			WyalFile.Type varType = convert(var.getType(), decl);
			WyalFile.Identifier varName = new WyalFile.Identifier(var.getBytecode().getName());
			vardecls[i] = new WyalFile.VariableDeclaration(varType, varName);
		}
		return vardecls;
	}

	/**
	 * Generate a logical conjunction which represents the given ranges of all
	 * quantified variables. That is a conjunction of the form
	 * <code>start <= var && var < end</code>.
	 *
	 * @return
	 */
	private Expr generateQuantifierRanges(Location<Quantifier> expr, LocalEnvironment environment) {
		Expr ranges = null;
		for (int i = 0; i != expr.numberOfOperandGroups(); ++i) {
			Location<?>[] group = expr.getOperandGroup(i);
			Location<VariableDeclaration> var = (Location<VariableDeclaration>)
					group[0];
			WyalFile.VariableDeclaration varDecl = environment.read(var.getIndex());
			Expr.VariableAccess varExpr = new Expr.VariableAccess(varDecl);
			Expr startExpr = translateExpression(group[1], environment);
			Expr endExpr = translateExpression(group[2], environment);
			Expr lhs = new Expr.Operator(WyalFile.Opcode.EXPR_lteq, startExpr,
					varExpr);
			Expr rhs = new Expr.Operator(WyalFile.Opcode.EXPR_lt, varExpr,
					endExpr);
			ranges = and(ranges, and(lhs, rhs));
		}
		return ranges;
	}

	private Expr translateVariableAccess(Location<VariableAccess> expr, LocalEnvironment environment) {
		Location<?> decl = expr.getOperand(0);
		Bytecode bytecode = decl.getBytecode();
		WyalFile.VariableDeclaration var;
		if (bytecode instanceof VariableDeclaration) {
			// In this case, we have a direct read of top-level variable.
			var = environment.read(decl.getIndex());
		} else {
			// In this case, we are reading an alias of a top-level variable. We
			// need to record this information to preserve the equality between
			// these two variables later on.
			AliasDeclaration alias = (AliasDeclaration) bytecode;
			var = environment.readAlias(decl.getIndex(), alias.getOperand(0));
		}
		//
		return new Expr.VariableAccess(var);
	}

	// =========================================================================
	// Helpers
	// =========================================================================

	/**
	 * Construct an implication from one expression to another
	 *
	 * @param antecedent
	 * @param consequent
	 * @return
	 */
	private WyalFile.Stmt implies(WyalFile.Stmt antecedent, WyalFile.Stmt consequent) {
		if (antecedent == null) {
			return consequent;
		} else {
			WyalFile.Stmt.Block antecedentBlock = new WyalFile.Stmt.Block(antecedent);
			WyalFile.Stmt.Block consequentBlock = new WyalFile.Stmt.Block(consequent);
			return new WyalFile.Stmt.IfThen(antecedentBlock, consequentBlock);
		}
	}

	/**
	 * Construct a conjunction of two expressions
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private WyalFile.Stmt and(WyalFile.Stmt lhs, WyalFile.Stmt rhs) {
		if (lhs == null) {
			return rhs;
		} else if (rhs == null) {
			return rhs;
		} else {
			return new WyalFile.Stmt.Block(lhs, rhs);
		}
	}

	private Expr and(Expr lhs, Expr rhs) {
		if (lhs == null) {
			return rhs;
		} else if (rhs == null) {
			return rhs;
		} else {
			return new Expr.Operator(Opcode.EXPR_and,lhs, rhs);
		}
	}

	/**
	 * Construct a disjunct of two expressions
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private WyalFile.Stmt or(WyalFile.Stmt lhs, WyalFile.Stmt rhs) {
		if (lhs == null) {
			return rhs;
		} else if (rhs == null) {
			return rhs;
		} else {
			WyalFile.Stmt.Block lhsBlock = new WyalFile.Stmt.Block(lhs);
			WyalFile.Stmt.Block rhsBlock = new WyalFile.Stmt.Block(rhs);
			return new WyalFile.Stmt.CaseOf(lhsBlock,rhsBlock);
		}
	}

	/**
	 * Join one or more descendant context's together. To understand this,
	 * consider the following snippet, annotated with context information:
	 *
	 * <pre>
	 * // Context: y >= 0
	 * if x >= 0:
	 *    x = x + 1
	 *    // Context: y >= 0 && x >= 0 && x$1 == x + 1
	 * else:
	 *    x = -x
	 *    // Context: y >= 0 && x < 0 && x$2 == x + 1
	 * //
	 * Context: ?
	 * </pre>
	 *
	 * At this point, we have two goals in combining the contextual information
	 * back together. Firstly, we want to factor out the parts common to both
	 * (e.g. <code>y >= 0</code> above). Secondly, we need to determine the
	 * appropriate version for variables modified on one or both branches (e.g.
	 * <code>x</code> above). Thus, the joined context for the above would be:
	 *
	 * <pre>
	 * y >= 0 && ((x >= 0 && x$1 == x + 1 && x$3 == x$1) || (x < 0 && x$2 == -x && x$3 == x$2))
	 * </pre>
	 *
	 * In the resulting environment, the current version of <code>x</code> would
	 * then be <code>x$3</code>. To determine affected variables we simplify
	 * identify any variable with a different version between at least two
	 * context's.
	 *
	 * @param ancestor
	 *            Distinguished context for join, which is an ancestor of those
	 *            context's being joined.
	 * @param descendants
	 *            Descendant context's being joined. Again, these maybe null for
	 *            branches which terminate (e.g. via return).
	 * @return
	 */
	private Context joinDescendants(Context ancestor, Context[] descendants) {
		// Santity check parameters, as they maybe null. This happens in case of
		// branches which terminate (e.g. via return or break).
		descendants = removeNull(descendants);
		if (descendants.length == 0) {
			// In this case, the are actually no active descendants. Hence, the
			// resulting context is null to indicate no branches escape this
			// meet point.
			return null;
		} else if (descendants.length == 1) {
			// If there's only one, then we don't need to join it.
			return descendants[0];
		}
		//
		LocalEnvironment joinedEnvironment = joinEnvironments(descendants);
		//
		AssumptionSet[] descendentAssumptions = new AssumptionSet[descendants.length];
		for (int i = 0; i != descendants.length; ++i) {
			Context ithContext = descendants[i];
			LocalEnvironment ithEnvironment = ithContext.environment;
			AssumptionSet ithAssumptions = ithContext.assumptions;
			descendentAssumptions[i] = updateVariableVersions(ithAssumptions, ithEnvironment, joinedEnvironment);
		}
		//
		AssumptionSet joinedAssumptions = ancestor.assumptions.joinDescendants(descendentAssumptions);
		//
		return new Context(ancestor.wyalFile, joinedAssumptions, joinedEnvironment, ancestor.initialEnvironment,
				ancestor.enclosingLoop, ancestor.verificationConditions);
	}

	private Context joinDescendants(Context ancestor, Context firstDescendant, List<Context> descendants1,
			List<Context> descendants2) {
		ArrayList<Context> descendants = new ArrayList<>(descendants1);
		descendants.addAll(descendants2);
		return joinDescendants(ancestor, firstDescendant, descendants);
	}

	private Context joinDescendants(Context ancestor, Context firstDescendant, List<Context> descendants) {
		Context[] ds = descendants.toArray(new Context[descendants.size() + 1]);
		ds[descendants.size()] = firstDescendant;
		return joinDescendants(ancestor, ds);
	}

	/**
	 * Bring a given assumption set which is consistent with an original
	 * environment up-to-date with a new environment.
	 *
	 * @param assumptions
	 *            The assumption set associated with a given context being
	 *            joined together.
	 * @param original
	 *            The original environment associated with the given context.
	 *            This maps from location indices to version numbers and is
	 *            consistent with the given assumption set.
	 * @param updated
	 *            The updated mapping from location indices to version numbers.
	 *            In many cases, these will be the same as in the original
	 *            environment. However, some versions will have been updated
	 *            because they were modified in one or more context's being
	 *            joined. In such case, the given assumption set must be brought
	 *            up to date with the new version numbers.
	 * @return
	 */
	private AssumptionSet updateVariableVersions(AssumptionSet assumptions, LocalEnvironment original,
			LocalEnvironment updated) {
		for (Map.Entry<Integer, WyalFile.VariableDeclaration> e : updated.locals.entrySet()) {
			Integer varIndex = e.getKey();
			WyalFile.VariableDeclaration newVarVersionedName = e.getValue();
			WyalFile.VariableDeclaration oldVarVersionedName = original.read(varIndex);
			if (!oldVarVersionedName.equals(newVarVersionedName)) {
				// indicates a version change of the given variable.
				Expr.VariableAccess oldVar = new Expr.VariableAccess(oldVarVersionedName);
				Expr.VariableAccess newVar = new Expr.VariableAccess(newVarVersionedName);
				assumptions = assumptions.add(new Expr.Operator(Opcode.EXPR_eq, newVar, oldVar));
			}
		}
		return assumptions;
	}

	/**
	 * Join the local environments of one or more context's together. This means
	 * retaining variable versions which are the same for all context's,
	 * allocating new versions for those which are different in at least one
	 * case, and removing those which aren't present it at least one.
	 *
	 * @param contexts
	 *            Array of at least one non-null Context
	 * @return
	 */
	private LocalEnvironment joinEnvironments(Context... contexts) {
		//
		Context head = contexts[0];
		GlobalEnvironment global = head.getEnvironment().getParent();
		HashSet<Integer> modified = new HashSet<>();
		HashSet<Integer> deleted = new HashSet<>();
		Map<Integer, WyalFile.VariableDeclaration> headLocals = head.environment.locals;

		// Compute the modified and deleted sets
		for (int i = 1; i < contexts.length; ++i) {
			Context ithContext = contexts[i];
			Map<Integer, WyalFile.VariableDeclaration> ithLocals = ithContext.environment.locals;
			// First check env against head
			for (Map.Entry<Integer, WyalFile.VariableDeclaration> e : ithLocals.entrySet()) {
				Integer key = e.getKey();
				WyalFile.VariableDeclaration s1 = e.getValue();
				WyalFile.VariableDeclaration s2 = headLocals.get(key);
				if (s1 == null) {
					deleted.add(key);
				} else if (!s1.equals(s2)) {
					modified.add(key);
				}
			}
			// Second, check head against env
			for (Map.Entry<Integer, WyalFile.VariableDeclaration> e : headLocals.entrySet()) {
				Integer key = e.getKey();
				WyalFile.VariableDeclaration s1 = e.getValue();
				WyalFile.VariableDeclaration s2 = ithLocals.get(key);
				if (s1 == null) {
					deleted.add(key);
				} else if (!s1.equals(s2)) {
					modified.add(key);
				}
			}
		}
		// Finally, construct the combined local map
		HashMap<Integer, WyalFile.VariableDeclaration> combinedLocals = new HashMap<>();
		for (Map.Entry<Integer, WyalFile.VariableDeclaration> e : headLocals.entrySet()) {
			Integer key = e.getKey();
			WyalFile.VariableDeclaration value = e.getValue();
			if (deleted.contains(key)) {
				// Ignore this entry. This must be checked before we look at
				// modified (since variable can be marked both).
				continue;
			} else if (modified.contains(key)) {
				// Update version number
				value = global.allocateVersion(key);
			}
			combinedLocals.put(key, value);
		}
		// Now, use the modified and deleted sets to build the new environment
		return new LocalEnvironment(global, combinedLocals);
	}

	/**
	 * Construct a function or method prototype with a given name and type. The
	 * function or method can then be called elsewhere as an uninterpreted
	 * function. The function or method doesn't have a body but is used as a
	 * name to be referred to from assertions.
	 *
	 * @param declaration
	 *            --- the function or method declaration in question
	 * @param wyalFile
	 *            --- the file onto which this function is created.
	 * @return
	 */
	private void createFunctionOrMethodPrototype(WyilFile.FunctionOrMethod declaration, WyalFile wyalFile) {
		SyntaxTree tree = declaration.getTree();
		Type[] params = declaration.type().params();
		Type[] returns = declaration.type().returns();
		//
		WyalFile.VariableDeclaration[] parameters = new WyalFile.VariableDeclaration[params.length];
		// second, set initial environment
		int loc = 0;
		for (int i = 0; i != params.length; ++i, ++loc) {
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) tree.getLocation(loc);
			WyalFile.Type parameterType = convert(params[i], declaration);
			WyalFile.Identifier parameterName = new WyalFile.Identifier(var.getBytecode().getName());
			parameters[i] = new WyalFile.VariableDeclaration(parameterType, parameterName);
		}
		WyalFile.VariableDeclaration[] wyalReturns = new WyalFile.VariableDeclaration[returns.length];
		// second, set initial environment
		for (int i = 0; i != returns.length; ++i, ++loc) {
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) tree.getLocation(loc);
			WyalFile.Type returnType = convert(returns[i], declaration);
			WyalFile.Identifier returnName = new WyalFile.Identifier(var.getBytecode().getName());
			wyalReturns[i] = new WyalFile.VariableDeclaration(returnType, returnName);
		}
		//
		WyalFile.Identifier name = new WyalFile.Identifier(declaration.name());
		wyalFile.allocate(new Declaration.Named.Function(name, parameters, wyalReturns));
	}

	/**
	 * Turn each verification condition into an assertion in the underlying
	 * WyalFile being generated. The main challenge here is to ensure that all
	 * variables used in the assertion are properly typed.
	 *
	 * @param declaration
	 *            The enclosing function or method declaration
	 * @param vcs
	 *            The list of verification conditions which have been generated
	 * @param environment
	 *            The global environment which maps all versioned variables to
	 *            their underlying locations. This is necessary to determine the
	 *            type of all free variables.
	 * @param wyalFile
	 *            The WyAL file being generated
	 */
	private void createAssertions(WyilFile.FunctionOrMethod declaration, List<VerificationCondition> vcs,
			GlobalEnvironment environment, WyalFile wyalFile) {
		// FIXME: should be logged somehow?
		for (int i = 0; i != vcs.size(); ++i) {
			VerificationCondition vc = vcs.get(i);
			// Build the actual verification condition
			WyalFile.Stmt.Block verificationCondition = buildVerificationCondition(declaration, environment, vc);
			// Add generated verification condition as assertion
			WyalFile.Declaration.Assert assrt = new WyalFile.Declaration.Assert(verificationCondition, vc.description);
			assrt.attributes().addAll(vc.attributes());
			wyalFile.allocate(assrt);
		}
	}

	/**
	 * Construct a fully typed and quantified expression for representing a
	 * verification condition. Aside from flattening the various components, it
	 * must also determine appropriate variable types, including those for
	 * aliased variables.
	 *
	 * @param vc
	 * @param environment
	 * @return
	 */
	public WyalFile.Stmt.Block buildVerificationCondition(WyilFile.FunctionOrMethod declaration,
			GlobalEnvironment environment, VerificationCondition vc) {
		WyalFile.Stmt antecedent = flatten(vc.antecedent);
		Expr consequent = vc.consequent;
		HashSet<WyalFile.VariableDeclaration> freeVariables = new HashSet<>();
		freeVariables(antecedent,freeVariables);
		freeVariables(consequent,freeVariables);
		// Determine any variable aliases as necessary.
		Expr aliases = determineVariableAliases(environment, freeVariables);
		// Construct the initial condition
		WyalFile.Stmt verificationCondition = implies(and(aliases, antecedent), vc.consequent);
		// Now, generate type information for any free variables
		if (freeVariables.size() > 0) {
			// This indicates there are one or more free variables in the
			// verification condition. Hence, these must be universally
			// quantified to ensure the vc is well=typed.
			WyalFile.VariableDeclaration[] parameters = freeVariables
					.toArray(new WyalFile.VariableDeclaration[freeVariables.size()]);
			WyalFile.Stmt.Block qfBody = new WyalFile.Stmt.Block(verificationCondition);
			verificationCondition = new WyalFile.Stmt.Quantifier(Opcode.STMT_forall, parameters, qfBody);
		}
		// Done
		return new WyalFile.Stmt.Block(verificationCondition);
	}

	/**
	 * Flatten a given assumption set into a single logical condition. The key
	 * challenge here is to try and do this as efficiency as possible.
	 *
	 * @param assumptions
	 * @return
	 */
	private WyalFile.Stmt flatten(AssumptionSet assumptions) {
		WyalFile.Stmt result = flattenUpto(assumptions, null);
		if (result == null) {
			return new Expr.Constant(new Value.Bool(true));
		} else {
			return result;
		}
	}

	/**
	 * Flatten an assumption set upto a given ancestor. That is, do not include
	 * the ancestor or any of its ancestors in the results. This is a little
	 * like taking the difference of the given assumptions and the given
	 * ancestor's assumptions.
	 *
	 * @param assumptions
	 *            The assumption set to be flattened
	 * @param ancestor
	 *            An ancestor of the given assumption set, or null to indicate
	 *            all ancestors should be included
	 * @return
	 */
	private WyalFile.Stmt flattenUpto(AssumptionSet assumptions, AssumptionSet ancestor) {

		if (assumptions == ancestor) {
			// We have reached the ancestor
			return null;
		} else {
			// Flattern parent assumptions
			AssumptionSet[] parents = assumptions.parents;
			WyalFile.Stmt e = null;
			switch (parents.length) {
			case 0:
				// do nothing
				break;
			case 1:
				// easy
				e = flattenUpto(parents[0], ancestor);
				break;
			default:
				// harder
				AssumptionSet lca = assumptions.commonAncestor;
				WyalFile.Stmt factor = flattenUpto(lca, ancestor);
				for (int i = 0; i != parents.length; ++i) {
					e = or(e, flattenUpto(parents[i], lca));
				}
				e = and(factor, e);
			}

			// Combine with local assumptions (if applicable)
			WyalFile.Stmt[] local = assumptions.assumptions;
			for (int i = 0; i != local.length; ++i) {
				e = and(e, local[i]);
			}
			//
			return e;
		}
	}

	/**
	 * Determine any variable aliases which need to be accounted for. This is
	 * done by adding an equality between the aliased variables to ensure they
	 * have the same value.
	 *
	 * @param environment
	 * @param freeVariables
	 * @return
	 */
	private Expr determineVariableAliases(GlobalEnvironment environment,
			Set<WyalFile.VariableDeclaration> freeVariables) {
		Expr aliases = null;
		// for (String var : freeVariables) {
		// WyalFile.VariableDeclaration parent = environment.getParent(var);
		// if (parent != null) {
		// // This indicates a variable alias, so construct the necessary
		// // equality.
		// Expr.VariableAccess lhs = new Expr.VariableAccess(var);
		// Expr.VariableAccess rhs = new Expr.VariableAccess(parent);
		// Expr aliasEquality = new Expr.Operator(WyalFile.Opcode.EXPR_eq, lhs,
		// rhs);
		// //
		// aliases = and(aliases, aliasEquality);
		// }
		// }
		return aliases;
	}

	/**
	 * Packaged the set of free variables up into a type pattern (for now).
	 *
	 * @param declaration
	 *            The enclosing function or method declaration
	 * @param environment
	 *            The global environment which maps all versioned variables to
	 *            their underlying locations. This is necessary to determine the
	 *            type of all free variables.
	 * @param freeVariables
	 *            Set of free variables to allocate
	 * @return
	 */
	// private TypePattern generateExpressionTypePattern(WyilFile.Declaration
	// declaration, GlobalEnvironment environment,
	// Set<String> freeVariables) {
	// SyntaxTree tree = declaration.getTree();
	//
	// TypePattern[] patterns = new TypePattern[freeVariables.size()];
	// int index = 0;
	// for (String var : freeVariables) {
	// Expr.Variable v = new Expr.Variable(var);
	// Location<?> l = tree.getLocation(environment.resolve(var));
	// WyalFile.Type wycsType = convert(l.getType(), declaration);
	// patterns[index++] = new TypePattern.Leaf(wycsType, v);
	// }
	// if (patterns.length == 1) {
	// return patterns[0];
	// } else {
	// return new TypePattern.Tuple(patterns);
	// }
	// }

	/**
	 * Convert the parameter types for a given function or method declaration
	 * into a corresponding list of type patterns. This is primarily useful for
	 * generating declarations from functions or method.
	 *
	 * @param params
	 * @param declaration
	 * @return
	 */
	private WyalFile.VariableDeclaration[] generatePreconditionParameters(WyilFile.FunctionOrMethod declaration,
			LocalEnvironment environment) {
		Type[] params = declaration.type().params();
		int[] parameterLocations = ArrayUtils.range(0, params.length);
		return generateParameterDeclarations(declaration, environment, parameterLocations);
	}

	/**
	 * Convert the return types for a given function or method declaration into
	 * a corresponding list of type patterns. This is primarily useful for
	 * generating declarations from functions or method.
	 *
	 * @param params
	 * @param declaration
	 * @return
	 */
	private WyalFile.VariableDeclaration[] generatePostconditionTypePattern(WyilFile.FunctionOrMethod declaration,
			LocalEnvironment environment) {
		Type[] params = declaration.type().params();
		Type[] returns = declaration.type().returns();
		int[] parameterLocations = ArrayUtils.range(0, params.length);
		int[] returnLocations = ArrayUtils.range(parameterLocations.length,
				parameterLocations.length + returns.length);
		return generateParameterDeclarations(declaration, environment, parameterLocations, returnLocations);
	}

	/**
	 * Convert the types of local variables in scope at a given position within
	 * a function or method into a type pattern. This is primarily useful for
	 * determining the types for a loop invariant macro.
	 *
	 * @param params
	 * @param declaration
	 * @return
	 */
	private WyalFile.VariableDeclaration[] generateLoopInvariantTypePattern(WyilFile.Declaration declaration,
			Location<?>[] loopInvariant, LocalEnvironment environment) {
		int[] localVariableLocations = SyntaxTrees.determineUsedVariables(loopInvariant);
		return generateParameterDeclarations(declaration, environment, localVariableLocations);
	}

	/**
	 * Convert a list of types from a given declaration into a corresponding
	 * list of type patterns. This is primarily useful for generating
	 * declarations from functions or method.
	 *
	 * @param types
	 * @param declaration
	 * @return
	 */
	private WyalFile.VariableDeclaration[] generateParameterDeclarations(WyilFile.Declaration declaration,
			LocalEnvironment environment, int[]... groups) {
		//
		SyntaxTree tree = declaration.getTree();
		int[] locations = flattern(groups);
		WyalFile.VariableDeclaration[] patterns = new WyalFile.VariableDeclaration[locations.length];
		// second, set initial environment
		for (int i = 0; i != locations.length; ++i) {
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) tree.getLocation(locations[i]);
			patterns[i] = environment.read(var.getIndex());
		}
		//
		return patterns;
	}

	/**
	 * Convert a Name identifier from a WyIL into one suitable for a WyAL file.
	 *
	 * @param id
	 * @return
	 */
	private static WyalFile.Name convert(NameID id) {
		Path.ID prefix = id.module();
		WyalFile.Identifier[] components = new WyalFile.Identifier[prefix.size() + 1];
		for (int i = 0; i != prefix.size(); ++i) {
			components[i] = new WyalFile.Identifier(prefix.get(i));
		}
		components[prefix.size()] = new WyalFile.Identifier(id.name());
		return new WyalFile.Name(components);
	}

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
	private Expr convert(Constant c, SyntaxTree.Location<?> context) {
		Value v;
		if (c instanceof Constant.Null) {
			v = new WyalFile.Value.Null();
		} else if (c instanceof Constant.Bool) {
			Constant.Bool cb = (Constant.Bool) c;
			v = new WyalFile.Value.Bool(cb.value());
		} else if (c instanceof Constant.Byte) {
			Constant.Byte cb = (Constant.Byte) c;
			v = new WyalFile.Value.Int(cb.value());
		} else if (c instanceof Constant.Integer) {
			Constant.Integer cb = (Constant.Integer) c;
			v = new WyalFile.Value.Int(cb.value());
		} else if (c instanceof Constant.Array) {
			Constant.Array cb = (Constant.Array) c;
			List<Constant> cb_values = cb.values();
			Expr[] items = new Expr[cb_values.size()];
			for (int i = 0; i != cb_values.size(); ++i) {
				items[i] = convert(cb_values.get(i), context);
			}
			return new Expr.Operator(Opcode.EXPR_arrinit, items);
		} else if (c instanceof Constant.Record) {
			Constant.Record cr = (Constant.Record) c;
			HashMap<String, Constant> fields = cr.values();
			WyalFile.Pair<WyalFile.Identifier, Expr>[] pairs = new WyalFile.Pair[fields.size()];
			//
			int i = 0;
			for (Map.Entry<String, Constant> e : fields.entrySet()) {
				WyalFile.Expr val = convert(e.getValue(), context);
				pairs[i++] = new WyalFile.Pair<>(new WyalFile.Identifier(e.getKey()), val);
			}
			return new Expr.RecordInitialiser(pairs);
		} else {
			WyilFile.Declaration decl = context.getEnclosingTree().getEnclosingDeclaration();
			throw new InternalFailure("unknown constant encountered (" + c + ")", decl.parent().getEntry(), context);
		}
		//
		return new Expr.Constant(v);
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
	private static WyalFile.Type convert(Type type, WyilFile.Block context) {
		// FIXME: this is fundamentally broken in the case of recursive types.
		// See Issue #298.
		WyalFile.Type result;
		if (type == Type.T_ANY) {
			result = new WyalFile.Type.Any();
		} else if (type == Type.T_VOID) {
			result = new WyalFile.Type.Void();
		} else if (type == Type.T_NULL) {
			result = new WyalFile.Type.Null();
		} else if (type == Type.T_BOOL) {
			result = new WyalFile.Type.Bool();
		} else if (type == Type.T_BYTE) {
			// FIXME: implement WyalFile.Type.Byte
			// return new WyalFile.Type.Byte(attributes(branch);
			result = new WyalFile.Type.Int();
		} else if (type == Type.T_INT) {
			result = new WyalFile.Type.Int();
		} else if (type instanceof Type.Array) {
			Type.Array lt = (Type.Array) type;
			WyalFile.Type element = convert(lt.element(), context);
			result = new WyalFile.Type.Array(element);
		} else if (type instanceof Type.Record) {
			Type.Record rt = (Type.Record) type;
			String[] names = rt.getFieldNames();
			WyalFile.FieldDeclaration[] elements = new WyalFile.FieldDeclaration[names.length];
			for (int i = 0; i != names.length; ++i) {
				String fieldName = names[i];
				WyalFile.Type fieldType = convert(rt.getField(fieldName), context);
				elements[i] = new WyalFile.FieldDeclaration(fieldType, new WyalFile.Identifier(fieldName));
			}
			result = new WyalFile.Type.Record(elements);
		} else if (type instanceof Type.Reference) {
			// FIXME: how to translate this??
			result = new WyalFile.Type.Any();
		} else if (type instanceof Type.Union) {
			Type.Union tu = (Type.Union) type;
			Type[] tu_elements = tu.bounds();
			WyalFile.Type[] elements = new WyalFile.Type[tu_elements.length];
			for (int i = 0; i != tu_elements.length; ++i) {
				elements[i] = convert(tu_elements[i], context);
			}
			result = new WyalFile.Type.Union(elements);
		} else if (type instanceof Type.Intersection) {
			Type.Intersection t = (Type.Intersection) type;
			Type[] t_elements = t.bounds();
			WyalFile.Type[] elements = new WyalFile.Type[t_elements.length];
			for (int i = 0; i != t_elements.length; ++i) {
				elements[i] = convert(t_elements[i], context);
			}
			result = new WyalFile.Type.Intersection(elements);
		} else if (type instanceof Type.Negation) {
			Type.Negation nt = (Type.Negation) type;
			WyalFile.Type element = convert(nt.element(), context);
			result = new WyalFile.Type.Negation(element);
		} else if (type instanceof Type.FunctionOrMethod) {
			Type.FunctionOrMethod ft = (Type.FunctionOrMethod) type;
			// FIXME: need to do something better here
			result = new WyalFile.Type.Any();
		} else if (type instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.name();
			Path.ID mid = nid.module();
			result = new WyalFile.Type.Nominal(convert(nid));
		} else {
			throw new InternalFailure("unknown type encountered (" + type.getClass().getName() + ")",
					context.parent().getEntry(), context);
		}
		//
		result.attributes().addAll(context.attributes());
		//
		return result;
	}

	/**
	 * Determine all free variables which are used within the given expression.
	 * A free variable is one which is not bound within the expression itself.
	 *
	 * @param e
	 * @param freeVars
	 */
	public void freeVariables(SyntacticItem e, Set<WyalFile.VariableDeclaration> freeVars) {
		if(e instanceof Expr.VariableAccess) {
			Expr.VariableAccess va = (Expr.VariableAccess)e;
			freeVars.add(va.getVariableDeclaration());
		} else if(e instanceof Expr.Quantifier) {
			Expr.Quantifier q = (Expr.Quantifier) e;
			freeVariables(q.getBody(), freeVars);
			// Remove any bound variables
			for (WyalFile.VariableDeclaration vd : q.getParameters().getOperands()) {
				freeVars.remove(vd);
			}
		} else {
			for(int i=0;i!=e.size();++i) {
				SyntacticItem item = e.getOperand(i);
				if(item != null) {
					freeVariables(item,freeVars);
				}
			}
		}
	}

	/**
	 * Lookup a given function or method. This maybe contained in the same file,
	 * or in a different file. This may require loading that file in memory to
	 * access this information.
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
	public WyilFile.FunctionOrMethod lookupFunctionOrMethod(NameID name, Type.FunctionOrMethod fun,
			SyntaxTree.Location<?> stmt) throws Exception {
		SyntaxTree tree = stmt.getEnclosingTree();
		WyilFile.Declaration decl = tree.getEnclosingDeclaration();
		//
		Path.Entry<WyilFile> e = builder.project().get(name.module(), WyilFile.ContentType);
		if (e == null) {
			throw new InternalFailure(errorMessage(ErrorMessages.RESOLUTION_ERROR, name.module().toString()),
					decl.parent().getEntry(), stmt);
		}
		WyilFile m = e.read();
		WyilFile.FunctionOrMethod method = m.functionOrMethod(name.name(), fun);

		return method;
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
	public Expr invertCondition(Expr expr, Location<?> elem) {
		if (expr instanceof Expr.Operator) {
			Expr.Operator binTest = (Expr.Operator) expr;
			WyalFile.Opcode op = null;
			switch (binTest.getOpcode()) {
			case EXPR_eq:
				op = Opcode.EXPR_neq;
				break;
			case EXPR_neq:
				op = Opcode.EXPR_eq;
				break;
			case EXPR_gteq:
				op = Opcode.EXPR_lt;
				break;
			case EXPR_gt:
				op = Opcode.EXPR_lteq;
				break;
			case EXPR_lteq:
				op = Opcode.EXPR_gt;
				break;
			case EXPR_lt:
				op = Opcode.EXPR_gteq;
				break;
			case EXPR_and: {
				Expr[] operands = invertConditions(binTest.getOperands(), elem);
				return new Expr.Operator(Opcode.EXPR_or, operands);
			}
			case EXPR_or: {
				Expr[] operands = invertConditions(binTest.getOperands(), elem);
				return new Expr.Operator(Opcode.EXPR_and, operands);
			}
			}
			if (op != null) {
				return new Expr.Operator(op, binTest.getOperands());
			}
		} else if (expr instanceof Expr.Is) {
			Expr.Is ei = (Expr.Is) expr;
			WyalFile.Type type = ei.getTypeTest();
			return new Expr.Is(ei.getExpr(), new WyalFile.Type.Negation(type));
		}
		// Otherwise, compare against false
		// FIXME: this is just wierd and needs to be fixed.
		return new Expr.Operator(Opcode.EXPR_not, expr);
	}

	public Expr[] invertConditions(Expr[] expr, Location<?> elem) {
		Expr[] rs = new Expr[expr.length];
		for (int i = 0; i != expr.length; ++i) {
			rs[i] = invertCondition(expr[i], elem);
		}
		return rs;
	}

	/**
	 * Create exact copy of a given array, but with evey null element removed.
	 *
	 * @param items
	 * @return
	 */
	private static <T> T[] removeNull(T[] items) {
		int count = 0;
		for (int i = 0; i != items.length; ++i) {
			if (items[i] == null) {
				count = count + 1;
			}
		}
		if (count == 0) {
			return items;
		} else {
			T[] rs = java.util.Arrays.copyOf(items, items.length - count);
			for (int i = 0, j = 0; i != items.length; ++i) {
				T item = items[i];
				if (item != null) {
					rs[j++] = item;
				}
			}

			return rs;
		}
	}

	public static int[] flattern(int[][] groups) {
		int length = 0;
		for (int i = 0; i != groups.length; ++i) {
			length += groups[i].length;
		}
		//
		int[] result = new int[length];
		for (int i = 0, j = 0; i != groups.length; ++i) {
			int[] group = groups[i];
			System.arraycopy(group, 0, result, j, group.length);
			j = j + group.length;
		}
		//
		return result;
	}

	// =============================================================
	// Assumptions
	// =============================================================

	/**
	 * Provides an immutable assumption set which (in principle) can be factored
	 * more precisely than a flat collection.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class AssumptionSet {
		/**
		 * The least common ancestor for all parents sets. A parent may be the
		 * common ancestor. Making this explicit is not strictly necessary, but
		 * helps with the flattening process.
		 */
		private final AssumptionSet commonAncestor;

		/**
		 * The set of parent sets from which this assumption set is derived.
		 */
		private final AssumptionSet[] parents;

		/**
		 * The set of assumptions explicitly provided by this assumption set.
		 * The complete set of assumptions includes those of the parents as
		 * well.
		 */
		private final WyalFile.Stmt[] assumptions;

		private AssumptionSet(AssumptionSet commonAncestor, AssumptionSet[] parents, WyalFile.Stmt... assumptions) {
			this.commonAncestor = commonAncestor;
			this.parents = parents;
			this.assumptions = assumptions;
		}

		public AssumptionSet add(WyalFile.Stmt... assumptions) {
			return new AssumptionSet(this, new AssumptionSet[] { this }, assumptions);
		}

		public AssumptionSet joinDescendants(AssumptionSet... descendants) {
			if (descendants.length == 1) {
				return descendants[0];
			} else {
				return new AssumptionSet(this, descendants);
			}
		}

		public static final AssumptionSet ROOT = new AssumptionSet(null, new AssumptionSet[0]);
	}

	// =============================================================
	// Verification Conditions
	// =============================================================

	/**
	 * Provides a simple structure representing a verification condition. This
	 * will be turned into an assertion of some form. A verification is always
	 * of the form "X ==> Y", where X is the "antecedent" and Y the
	 * "consequent". More specifically, X represents the knowledge known at the
	 * given point and Y is the condition we are attempting to assert.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class VerificationCondition extends SyntacticElement.Impl {
		private final String description;
		private final AssumptionSet antecedent;
		private final Expr consequent;

		private VerificationCondition(String description, AssumptionSet antecedent, Expr consequent,
				List<Attribute> attributes) {
			super(attributes);
			this.description = description;
			this.antecedent = antecedent;
			this.consequent = consequent;
		}
	}

	// =============================================================
	// Environments
	// =============================================================

	/**
	 * The global environment provides a global allocation of "versioned"
	 * variables. This ensures that across any related set of environments, no
	 * version clashes are possible between variables of the same name. This
	 * also means that we can determine the underlying location that each
	 * variable corresponds to.
	 *
	 * @author David J. Pearce
	 *
	 */
	private class GlobalEnvironment {
		/**
		 * Provides a link back to the enclosing declaration. This is necessary
		 * to enable us to convert location indices into variable names and
		 * types.
		 */
		private final WyilFile.Declaration enclosingDeclaration;

		/**
		 * Maps versioned variable strings to their underlying location index. A
		 * version variable string is of the form "x$1" where "x" is the
		 * variable name and "1" the version number.
		 */
		private final Map<String, Integer> allocation;

		/**
		 * Maps aliased variables to their parent variable. That is the variable
		 * which is being aliased.
		 */
		private final Map<WyalFile.VariableDeclaration, WyalFile.VariableDeclaration> parents;

		/**
		 * Provides a global mapping of all local variable names to the next
		 * unused version numbers. This is done with variable names rather than
		 * location indices because it is possible two have different variables
		 * with the same name,
		 */
		private final Map<String, Integer> versions;

		public GlobalEnvironment(WyilFile.Declaration enclosingDeclaration) {
			this.enclosingDeclaration = enclosingDeclaration;
			this.allocation = new HashMap<>();
			this.parents = new HashMap<>();
			this.versions = new HashMap<>();
		}

		/**
		 * Get the parent for a potential variable alias, or null if there is no
		 * alias.
		 *
		 * @param alias
		 * @return
		 */
		public WyalFile.VariableDeclaration getParent(String alias) {
			return parents.get(alias);
		}

		/**
		 * Get the location index from a versioned variable name of the form
		 * "x$1"
		 *
		 * @param versionedVariable
		 * @return
		 */
		public int resolve(String versionedVariable) {
			return allocation.get(versionedVariable);
		}

		/**
		 * Allocation a new versioned variable name of the form "x$1" for a
		 * given location index
		 *
		 * @param index
		 * @return
		 */
		public WyalFile.VariableDeclaration allocateVersion(int index) {
			SyntaxTree tree = enclosingDeclaration.getTree();
			Location<?> loc = tree.getLocation(index);
			WyalFile.Type type = convert(loc.getType(), enclosingDeclaration);
			Bytecode bytecode = loc.getBytecode();
			String name;
			if (bytecode instanceof Bytecode.VariableDeclaration) {
				Bytecode.VariableDeclaration v = (Bytecode.VariableDeclaration) bytecode;
				name = v.getName();
			} else if (bytecode instanceof Bytecode.AliasDeclaration) {
				Location<Bytecode.VariableDeclaration> v = getVariableDeclaration(loc);
				// This indicates an alias declaration
				name = v.getBytecode().getName();
			} else {
				// This indicates an unnamed location
				name = "$" + index;
			}
			// Allocate a new version number for this variable
			Integer version = versions.get(name);
			String versionedVar;
			if (version == null) {
				version = 0;
				// Variables with version 0 just take the original name. This is
				// not necessary, but it makes for slightly nicer verification
				// conditions.
				versionedVar = name;
			} else {
				version = version + 1;
				versionedVar = name + "$" + version;
			}
			versions.put(name, version);
			// Create the versioned variable name and remember which location it
			// corresponds to.
			allocation.put(versionedVar, index);
			//
			return new WyalFile.VariableDeclaration(type, new WyalFile.Identifier(versionedVar));
		}

		/**
		 * Add a new variable alias for a variable to its parent
		 *
		 * @param leftVar
		 * @param rightVar
		 */
		public void addVariableAlias(WyalFile.VariableDeclaration alias, WyalFile.VariableDeclaration parent) {
			parents.put(alias, parent);
		}
	}

	/**
	 * The local environment provides a mapping from local variables in the
	 * current scope to their current version number. Local environments are
	 * transitively immutable objects, except for the global environment they
	 * refer to.
	 *
	 * @author David J. Pearce
	 *
	 */
	private class LocalEnvironment {
		/**
		 * Provides access to the global environment
		 */
		private final GlobalEnvironment global;

		/**
		 * Maps all local variables in scope to their current versioned variable
		 * names
		 */
		private final Map<Integer, WyalFile.VariableDeclaration> locals;

		public LocalEnvironment(GlobalEnvironment global) {
			this.global = global;
			this.locals = new HashMap<>();
		}

		public LocalEnvironment(GlobalEnvironment global, Map<Integer, WyalFile.VariableDeclaration> locals) {
			this.global = global;
			this.locals = new HashMap<>(locals);
		}

		/**
		 * Get the enclosing global environment for this local environment
		 *
		 * @return
		 */
		public GlobalEnvironment getParent() {
			return global;
		}

		/**
		 * Read the current versioned variable name for a given location index.
		 *
		 * @param index
		 * @return
		 */
		public WyalFile.VariableDeclaration read(int index) {
			WyalFile.VariableDeclaration vv = locals.get(index);
			if (vv == null) {
				vv = global.allocateVersion(index);
				locals.put(index, vv);
			}
			return vv;
		}

		/**
		 * Read the current versioned variable name for a given (aliased)
		 * location index.
		 *
		 * @param alias
		 *            The variable being read (which is an alias)
		 * @param parent
		 *            The variable which this is an alias of
		 * @return
		 */
		public WyalFile.VariableDeclaration readAlias(int alias, int parent) {
			WyalFile.VariableDeclaration aliasedVariable = read(alias);
			WyalFile.VariableDeclaration parentVariable = read(parent);
			global.addVariableAlias(aliasedVariable, parentVariable);
			return aliasedVariable;
		}

		/**
		 * Create a new version for each variable in a sequence of variables.
		 * This create a completely new local environment.
		 *
		 * @param index
		 */
		public LocalEnvironment write(int... indices) {
			LocalEnvironment nenv = new LocalEnvironment(global, locals);
			for (int i = 0; i != indices.length; ++i) {
				nenv.locals.put(indices[i], global.allocateVersion(indices[i]));
			}
			return nenv;
		}

		@Override
		public LocalEnvironment clone() {
			return new LocalEnvironment(global, locals);
		}
	}

	public Location<VariableDeclaration> getVariableDeclaration(Location<?> decl) {
		switch (decl.getOpcode()) {
		case Bytecode.OPCODE_aliasdecl:
		case Bytecode.OPCODE_varmove:
		case Bytecode.OPCODE_varcopy:
			return getVariableDeclaration(decl.getOperand(0));
		case Bytecode.OPCODE_vardecl:
		case Bytecode.OPCODE_vardeclinit:
			return (Location<VariableDeclaration>) decl;
		default:
			throw new RuntimeException("internal failure --- dead code reached");
		}
	}

	// =============================================================
	// LoopScope
	// =============================================================
	/**
	 * Represents the enclosing "loop scope". This is needed for dealing with
	 * break and continue statements. Basically, as a way of taking the context
	 * at the point of the statement in question and moving it out to the
	 * enclosing loop.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class LoopScope {
		private List<Context> breakContexts;
		private List<Context> continueContexts;

		public LoopScope() {
			this.breakContexts = new ArrayList<>();
			this.continueContexts = new ArrayList<>();
		}

		public List<Context> breakContexts() {
			return breakContexts;
		}

		public List<Context> continueContexts() {
			return continueContexts;
		}

		public void addBreakContext(Context context) {
			breakContexts.add(context);
		}

		public void addContinueContext(Context context) {
			continueContexts.add(context);
		}
	}

	// =============================================================
	// Context
	// =============================================================

	/**
	 * Represents a given translation context.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class Context {
		/**
		 * Represents the wyalfile being generated. This is useful if we want to
		 * add macro definitions, etc.
		 */
		private final WyalFile wyalFile;

		/**
		 * The list of generated verification conditions.
		 */
		private final List<VerificationCondition> verificationConditions;

		/**
		 * The set of assumptions which are known to hold at a given point
		 * during generation.
		 */
		private final AssumptionSet assumptions;

		/**
		 * The local environment mapping variables to their current version
		 * numbers
		 */
		private final LocalEnvironment environment;

		/**
		 * The initial environment mapping variables to their initial version
		 * numbers.  This is useful for determining the "first" version of a variable.
		 */
		private final LocalEnvironment initialEnvironment;

		/**
		 * A reference to the enclosing loop scope, or null if no such scope.
		 */
		private final LoopScope enclosingLoop;

		public Context(WyalFile wyalFile, AssumptionSet assumptions, LocalEnvironment environment, LocalEnvironment initial,
				LoopScope enclosingLoop, List<VerificationCondition> vcs) {
			this.wyalFile = wyalFile;
			this.assumptions = assumptions;
			this.environment = environment;
			this.initialEnvironment = initial;
			this.verificationConditions = vcs;
			this.enclosingLoop = enclosingLoop;
		}

		public WyilFile getEnclosingFile() {
			return environment.getParent().enclosingDeclaration.parent();
		}

		/**
		 * Get the local environment associated witht his context
		 *
		 * @return
		 */
		public LocalEnvironment getEnvironment() {
			return environment;
		}

		/**
		 * Get the enclosing loop scope.
		 *
		 * @return
		 */
		public LoopScope getEnclosingLoopScope() {
			return enclosingLoop;
		}

		/**
		 * Generate a new context from this one where a give condition is
		 * assumed to hold.
		 *
		 * @param condition
		 * @return
		 */
		public Context assume(WyalFile.Stmt... conditions) {
			AssumptionSet nAssumptions = assumptions.add(conditions);
			return new Context(wyalFile, nAssumptions, environment, initialEnvironment, enclosingLoop, verificationConditions);
		}

		/**
		 * Emit a verification condition which ensures a given assertion holds
		 * true in this translation context.
		 *
		 * @param vc
		 *            The verification condition to be emitted
		 * @return
		 */
		public void emit(VerificationCondition vc) {
			verificationConditions.add(vc);
		}

		/**
		 * Assign an expression to a given variable. This results in the version
		 * number for that variable being increased. Thus, any historical
		 * references to that variable in the set of assumptions remain valid.
		 *
		 * @param lhs
		 *            The index of the location being assigned
		 * @param rhs
		 * @return
		 */
		public Context write(int lhs, Expr rhs) {
			// Update version number of the assigned variable
			LocalEnvironment nEnvironment = environment.write(lhs);
			WyalFile.VariableDeclaration nVersionedVar = nEnvironment.read(lhs);
			// Update assumption sets to reflect the "assigment"
			Expr.VariableAccess var = new Expr.VariableAccess(nVersionedVar);
			Expr condition = new Expr.Operator(Opcode.EXPR_eq, var, rhs);
			AssumptionSet nAssumptions = assumptions.add(condition);
			//
			return new Context(wyalFile, nAssumptions, nEnvironment, initialEnvironment, enclosingLoop, verificationConditions);
		}

		public WyalFile.VariableDeclaration read(Location<?> expr) {
			return environment.read(expr.getIndex());
		}

		public WyalFile.VariableDeclaration readFirst(Location<?> expr) {
			return initialEnvironment.read(expr.getIndex());
		}

		public Context havoc(int lhs) {
			LocalEnvironment nEnvironment = environment.write(lhs);
			//
			return new Context(wyalFile, assumptions, nEnvironment, initialEnvironment, enclosingLoop, verificationConditions);
		}

		/**
		 * Havoc a number of variable accesses. This results in the version
		 * numbers for those variables being increased. Thus, any historical
		 * references to those variables in the set of assumptions remain valid.
		 *
		 * @param lhs
		 *            The variable accesses being havoced
		 * @return
		 */
		public Context havoc(Location<?>... exprs) {
			// Update version number of the assigned variables
			int[] vars = new int[exprs.length];
			for (int i = 0; i != exprs.length; ++i) {
				// At this point, we're assuming only variable accesses can be
				// havoced. However, potentially, it might make sense to open
				// this up a little.
				Location<VariableAccess> va = (Location<VariableAccess>) exprs[i];
				vars[i] = va.getOperand(0).getIndex();
			}
			LocalEnvironment nEnvironment = environment.write(vars);
			// done
			return new Context(wyalFile, assumptions, nEnvironment, initialEnvironment, enclosingLoop, verificationConditions);
		}

		/**
		 * Construct a context within a given loop scope.
		 *
		 * @param scope
		 * @return
		 */
		public Context newLoopScope(LoopScope scope) {
			return new Context(wyalFile, assumptions, environment, initialEnvironment, scope, verificationConditions);
		}
	}

	// =============================================================
	// Operator Maps
	// =============================================================

	/**
	 * Maps unary bytecodes into unary expression opcodes.
	 */
	private static Map<Bytecode.OperatorKind, WyalFile.Opcode> unaryOperatorMap;

	/**
	 * Maps binary bytecodes into binary expression opcodes.
	 */
	private static Map<Bytecode.OperatorKind, WyalFile.Opcode> binaryOperatorMap;

	static {
		// Configure operator maps. This is done using maps to ensure that
		// changes in one operator kind does not have knock-on effects. This
		// used to be a problem when an array was used to implement the mapping.

		// =====================================================================
		// Unary operator map
		// =====================================================================
		unaryOperatorMap = new HashMap<>();
		// Arithmetic
		unaryOperatorMap.put(Bytecode.OperatorKind.NEG, WyalFile.Opcode.EXPR_neg);
		// Logical
		unaryOperatorMap.put(Bytecode.OperatorKind.NOT, WyalFile.Opcode.EXPR_not);
		// Array
		unaryOperatorMap.put(Bytecode.OperatorKind.ARRAYLENGTH, WyalFile.Opcode.EXPR_arrlen);

		// =====================================================================
		// Binary operator map
		// =====================================================================
		binaryOperatorMap = new HashMap<>();
		// Arithmetic
		binaryOperatorMap.put(Bytecode.OperatorKind.ADD, WyalFile.Opcode.EXPR_add);
		binaryOperatorMap.put(Bytecode.OperatorKind.SUB, WyalFile.Opcode.EXPR_sub);
		binaryOperatorMap.put(Bytecode.OperatorKind.MUL, WyalFile.Opcode.EXPR_mul);
		binaryOperatorMap.put(Bytecode.OperatorKind.DIV, WyalFile.Opcode.EXPR_div);
		binaryOperatorMap.put(Bytecode.OperatorKind.REM, WyalFile.Opcode.EXPR_rem);
		// Equality
		binaryOperatorMap.put(Bytecode.OperatorKind.EQ, WyalFile.Opcode.EXPR_eq);
		binaryOperatorMap.put(Bytecode.OperatorKind.NEQ, WyalFile.Opcode.EXPR_neq);
		// Relational
		binaryOperatorMap.put(Bytecode.OperatorKind.LT, WyalFile.Opcode.EXPR_lt);
		binaryOperatorMap.put(Bytecode.OperatorKind.GT, WyalFile.Opcode.EXPR_gt);
		binaryOperatorMap.put(Bytecode.OperatorKind.LTEQ, WyalFile.Opcode.EXPR_lteq);
		binaryOperatorMap.put(Bytecode.OperatorKind.GTEQ, WyalFile.Opcode.EXPR_gteq);
		// Logical
		binaryOperatorMap.put(Bytecode.OperatorKind.AND, WyalFile.Opcode.EXPR_and);
		binaryOperatorMap.put(Bytecode.OperatorKind.OR, WyalFile.Opcode.EXPR_or);
	}
}
