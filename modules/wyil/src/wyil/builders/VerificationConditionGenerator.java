package wyil.builders;

import static wyil.lang.Bytecode.OPCODE_aliasdecl;
import static wyil.lang.Bytecode.OPCODE_varaccess;
import static wyil.lang.Bytecode.OPCODE_vardecl;
import static wyil.lang.Bytecode.OPCODE_vardeclinit;
import static wyil.util.ErrorMessages.errorMessage;
import static wyil.util.ErrorMessages.internalFailure;

import java.math.BigInteger;
import java.util.*;

import sun.text.normalizer.RangeValueIterator;
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.SyntacticElement;
import wycc.lang.SyntaxError.InternalFailure;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wycs.core.Value;
import wycs.syntax.Expr;
import wycs.syntax.SyntacticType;
import wycs.syntax.TypePattern;
import wycs.syntax.WyalFile;
import wyfs.lang.Path;
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
	public WyalFile translate(WyilFile wyilFile) {
		WyalFile wyalFile = new WyalFile(wyilFile.id(), wyilFile.filename());

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
		// First, translate the invariant (if applicable)
		Expr.Variable var = null;
		Expr invariant = null;

		if (invariants.size() > 0) {
			Location<VariableDeclaration> v = (Location<VariableDeclaration>) tree.getLocation(0);
			var = new Expr.Variable(v.getBytecode().getName(), v.attributes());
			invariant = generateTypeInvariants(declaration, invariants, var);
		}
		// FIXME: add inhabitability check
		// Convert the type into a type pattern
		SyntacticType type = convert(declaration.type(), declaration);
		TypePattern.Leaf pattern = new TypePattern.Leaf(type, var);
		// Done
		WyalFile.Type td = wyalFile.new Type(declaration.name(), Collections.EMPTY_LIST, pattern, invariant,
				declaration.attributes());
		wyalFile.add(td);
	}

	/**
	 * Translate each of the clauses representing the invariant of a type.
	 * 
	 * @param invariants
	 * @param var
	 * @return
	 */
	private Expr generateTypeInvariants(WyilFile.Type declaration, List<Location<Bytecode.Expr>> invariants,
			Expr.Variable var) {
		GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
		LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
		Expr invariant = null;
		for (int i = 0; i != invariants.size(); ++i) {
			Expr clause = translateExpression(invariants.get(i), localEnvironment);
			// FIXME: this is ugly. Instead, WyAL files could support
			// multiple invariant clauses?
			invariant = and(invariant, clause);
		}
		return invariant;
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
		List<VerificationCondition> vcs = new ArrayList<VerificationCondition>();
		Context context = new Context(wyalFile, assumptions, localEnvironment, null, vcs);
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
			TypePattern type = generatePreconditionTypePattern(declaration, localEnvironment);
			// Translate expression itself
			Expr clause = translateExpression(invariants.get(i), localEnvironment);
			// Capture any free variables. This is necessary to deal with any
			// variable aliases introduced by type test operators.
			clause = captureFreeVariables(declaration, globalEnvironment, clause);
			//
			wyalFile.add(
					wyalFile.new Macro(name, Collections.EMPTY_LIST, type, clause, invariants.get(i).attributes()));
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
			TypePattern type = generatePostconditionTypePattern(declaration, localEnvironment);
			Expr clause = translateExpression(invariants.get(i), localEnvironment.clone());
			// Capture any free variables. This is necessary to deal with any
			// variable aliases introduced by type test operators.
			clause = captureFreeVariables(declaration, globalEnvironment, clause);
			//
			wyalFile.add(
					wyalFile.new Macro(name, Collections.EMPTY_LIST, type, clause, invariants.get(i).attributes()));
		}
	}

	private Expr captureFreeVariables(WyilFile.Declaration declaration, GlobalEnvironment globalEnvironment,
			Expr clause) {
		HashSet<String> freeVariables = new HashSet<String>();
		HashSet<String> freeAliases = new HashSet<String>();
		clause.freeVariables(freeVariables);
		for (String var : freeVariables) {
			if (globalEnvironment.getParent(var) != null) {
				// This indicates that the given variable is an alias, rather
				// than a top-level declaration.
				freeAliases.add(var);
			}
		}
		// Determine any variable aliases as necessary.
		if (freeAliases.size() > 0) {
			// This indicates there are one or more free variables in the
			// clause. Hence, these must be universally quantified to ensure the
			// clause is well=typed.
			TypePattern types = generateExpressionTypePattern(declaration, globalEnvironment, freeAliases);
			Expr aliases = determineVariableAliases(globalEnvironment, freeAliases);
			//
			clause = new Expr.ForAll(types, implies(aliases, clause));
		}
		return clause;
	}

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
		Expr[] arguments = new Expr[declaration.type().params().size()];
		// Translate parameters as arguments to invocation
		for (int i = 0; i != arguments.length; ++i) {
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) tree.getLocation(i);
			String versionedName = environment.read(var.getIndex());
			arguments[i] = new Expr.Variable(versionedName, var.attributes());
		}
		//
		Expr argument = arguments.length == 1 ? arguments[0] : new Expr.Nary(Expr.Nary.Op.TUPLE, arguments);
		//
		for (int i = 0; i != preconditions.length; ++i) {
			preconditions[i] = new Expr.Invoke(prefix + i, declaration.parent().id(), Collections.EMPTY_LIST, argument);
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
				internalFailure("unknown statement encountered (" + stmt + ")", decl.parent().filename(),
						stmt.attributes());
				return null; // deadcode
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			internalFailure(e.getMessage(), decl.parent().filename(), e, stmt.attributes());
			throw e; // deadcode
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
			Location<?>[] lval = Arrays.copyOfRange(lhs, j, rval.numberOfTypes());
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
		for(int i=0;i!=ls.length;++i) {
			Pair<Expr,Context> lp = translateSingleAssignment(lval[i], context);
			ls[i] = lp.first();
			context = lp.second();
		}
		Expr lhs = ls.length == 1 ? ls[0] : new Expr.Nary(Expr.Nary.Op.TUPLE, ls);
		//
		Expr condition = new Expr.Binary(Expr.Binary.Op.EQ, lhs, rp.first());
		//
		return context.assume(condition);
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
	private Pair<Expr,Context> translateSingleAssignment(Location<?> lval, Context context) {

		// FIXME: this method is a bit of a kludge. It would be nicer,
		// eventually, to have all right-hand side expression represented in
		// WyTP directly. This could potentially be done by including an update
		// operation in WyTP ... ?

		switch (lval.getOpcode()) {
		case Bytecode.OPCODE_arrayindex:
			return translateArrayAssign((Location<Operator>) lval, context);
		case Bytecode.OPCODE_dereference:
			// There's nothing useful we can do here.
			return translateDereference((Location<Operator>) lval, context);
		case Bytecode.OPCODE_fieldload:
			return translateRecordAssign((Location<FieldLoad>) lval, context);
		case Bytecode.OPCODE_varaccess:
			return translateVariableAssign((Location<VariableAccess>) lval, context);
		default:
			internalFailure("unknown lval encountered (" + lval + ")", context.getEnclosingFile().filename(),
					lval.attributes());
			return null;
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
	private Pair<Expr,Context> translateRecordAssign(Location<FieldLoad> lval, Context context) {
		SyntaxTree tree = lval.getEnclosingTree();
		WyilFile.Declaration decl = tree.getEnclosingDeclaration();
		try {
			Bytecode.FieldLoad bytecode = lval.getBytecode();
			Type.EffectiveRecord type = typeSystem.expandAsEffectiveRecord(lval.getOperand(0).getType());
			// Translate source expression
			Pair<Expr, Context> p = translateExpressionWithChecks(lval.getOperand(0), context);
			Expr originalSource = p.first();
			context = p.second();
			// Generate new source expression based of havoced variable
			Location<VariableAccess> var = extractAssignedVariable(lval);
			if (var != null) {
				context = context.havoc(var);
			}
			Expr newSource = translateExpression(lval.getOperand(0), context.getEnvironment());
			//
			ArrayList<String> fields = new ArrayList<String>(type.fields().keySet());
			Collections.sort(fields);
			int index = fields.indexOf(bytecode.fieldName());
			for (int i = 0; i != fields.size(); ++i) {
				if (i != index) {
					Expr j = new Expr.Constant(Value.Integer(BigInteger.valueOf(i)));
					Expr oldField = new Expr.IndexOf(originalSource, j, lval.attributes());
					Expr newField = new Expr.IndexOf(newSource, j, lval.attributes());
					context = context.assume(new Expr.Binary(Expr.Binary.Op.EQ, oldField, newField, lval.attributes()));
				}
			}
			Expr j = new Expr.Constant(Value.Integer(BigInteger.valueOf(index)));
			Expr newField = new Expr.IndexOf(newSource, j, lval.attributes());
			return new Pair<>(newField,context);
		} catch (ResolveError e) {
			internalFailure(e.getMessage(), decl.parent().filename(), e, lval.attributes());
			return null;
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
	private Pair<Expr,Context> translateArrayAssign(Location<Operator> lval, Context context) {
		SyntaxTree tree = lval.getEnclosingTree();
		WyilFile.Declaration decl = tree.getEnclosingDeclaration();
		try {
			Type elementType = typeSystem.expandAsEffectiveArray(lval.getOperand(0).getType()).element();
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
			Expr arg = new Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { originalSource, newSource, index },
					lval.attributes());
			ArrayList<SyntacticType> generics = new ArrayList<SyntacticType>();
			generics.add(convert(elementType, decl));
			Expr.Invoke macro = new Expr.Invoke("update", Trie.fromString("wycs/core/Array"), generics, arg);
			// Construct connection between new source expression element and
			// result
			Expr newLVal = new Expr.IndexOf(newSource, index, lval.attributes());
			//
			return new Pair<>(newLVal,context.assume(macro));
		} catch (ResolveError e) {
			internalFailure(e.getMessage(), decl.parent().filename(), e, lval.attributes());
			return null;
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
	private Pair<Expr,Context> translateDereference(Location<?> lval, Context context) {
		Expr e = translateAsUnknown(lval,context.getEnvironment());
		return new Pair<>(e,context);
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
	private Pair<Expr,Context> translateVariableAssign(Location<VariableAccess> lval, Context context) {
		Location<VariableDeclaration> decl = (Location<VariableDeclaration>) lval.getOperand(0);
		context = context.havoc(decl.getIndex());
		String nVersionedVar = context.read(decl);
		Expr.Variable var = new Expr.Variable(nVersionedVar);
		return new Pair<>(var,context);
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
		case Bytecode.OPCODE_varaccess:
			return (Location<VariableAccess>) lval;
		default:
			internalFailure("unknown lval encountered (" + lval + ")", decl.parent().filename(), lval.attributes());
			return null;
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
		Expr condition = new Expr.Constant(Value.Bool(false), stmt.attributes());
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
				Expr[] arguments = new Expr[type.params().size() + type.returns().size()];
				// Translate parameters as arguments to post-condition
				// invocation
				for (int i = 0; i != type.params().size(); ++i) {
					Location<VariableDeclaration> var = (Location<VariableDeclaration>) tree.getLocation(i);
					arguments[i] = new Expr.Variable(var.getBytecode().getName(), var.attributes());
				}
				// Copy over return expressions as arguments for invocation(s)
				System.arraycopy(exprs, 0, arguments, type.params().size(), exprs.length);
				//
				Expr argument = arguments.length == 1 ? arguments[0] : new Expr.Nary(Expr.Nary.Op.TUPLE, arguments);
				String prefix = declaration.name() + "_ensures_";
				// Finally, generate an appropriate verification condition to
				// check
				// each postcondition clause
				for (int i = 0; i != postcondition.size(); ++i) {
					Expr clause = new Expr.Invoke(prefix + i, declaration.parent().id(), Collections.EMPTY_LIST,
							argument);
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
		Expr defaultValue = null;
		Context[] descendants = new Context[cases.length + 1];
		Context defaultContext = null;
		//
		for (int i = 0; i != cases.length; ++i) {
			Bytecode.Case caSe = cases[i];
			Context caseContext;
			// Setup knowledge from case values
			if (!caSe.isDefault()) {
				Expr e = null;
				for (Constant constant : caSe.values()) {
					Expr.Constant v = new Expr.Constant(convert(constant, stmt));
					e = or(e, new Expr.Binary(Expr.Binary.Op.EQ, value, v, stmt.attributes()));
					defaultValue = and(defaultValue, new Expr.Binary(Expr.Binary.Op.NEQ, value, v, stmt.attributes()));
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
			String name = prefix + clause.getIndex();
			// Construct fresh environment for this macro. This is necessary to
			// avoid name clashes with subsequent macros.
			GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
			LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
			TypePattern type = generateLoopInvariantTypePattern(declaration, loopInvariant, localEnvironment);
			Expr e = translateExpression(clause, localEnvironment.clone());
			wyalFile.add(wyalFile.new Macro(name, Collections.EMPTY_LIST, type, e, e.attributes()));
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
			Location<VariableAccess> var = (Location<VariableAccess>) tree.getLocation(localVariables[i]);
			arguments[i] = new Expr.Variable(environment.read(var.getIndex()), var.attributes());
		}
		Expr argument = arguments.length == 1 ? arguments[0] : new Expr.Nary(Expr.Nary.Op.TUPLE, arguments);
		//
		for (int i = 0; i != loopInvariant.length; ++i) {
			Location<?> clause = loopInvariant[i];
			Expr macroCall = new Expr.Invoke(prefix + clause.getIndex(), declaration.parent().id(),
					Collections.EMPTY_LIST, argument, clause.attributes());
			context.emit(new VerificationCondition(msg, context.assumptions, macroCall, clause.attributes()));
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
			Location<VariableAccess> var = (Location<VariableAccess>) tree.getLocation(localVariables[i]);
			arguments[i] = new Expr.Variable(environment.read(var.getIndex()), var.attributes());
		}
		Expr argument = arguments.length == 1 ? arguments[0] : new Expr.Nary(Expr.Nary.Op.TUPLE, arguments);
		//
		for (int i = 0; i != loopInvariant.length; ++i) {
			Location<?> clause = loopInvariant[i];
			Expr macroCall = new Expr.Invoke(prefix + clause.getIndex(), declaration.parent().id(),
					Collections.EMPTY_LIST, argument, clause.attributes());
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
				// the
				// left-hand side as an assumption into the right-hand side.
				// This is
				// an artifact of short-circuiting whereby terms on the
				// right-hand
				// side only execute when the left-hand side is known to hold.
				for (int i = 0; i != expr.numberOfOperands(); ++i) {
					checkExpressionPreconditions(expr.getOperand(i), context);
					Expr e = translateExpression(expr.getOperand(i), context.getEnvironment());
					context = context.assume(e);
				}
			} else if (opcode != Bytecode.OPCODE_varaccess) {
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
			internalFailure(e.getMessage(), decl.parent().filename(), e, expr.attributes());
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
			Expr argument = arguments.length == 1 ? arguments[0] : new Expr.Nary(Expr.Nary.Op.TUPLE, arguments);
			String prefix = bytecode.name().name() + "_requires_";
			// Finally, generate an appropriate verification condition to check
			// each precondition clause
			for (int i = 0; i != numPreconditions; ++i) {
				Expr clause = new Expr.Invoke(prefix + i, declaration.parent().id(), Collections.EMPTY_LIST, argument);
				context.emit(new VerificationCondition("precondition not satisfied", context.assumptions, clause,
						expr.attributes()));
			}
		}
	}

	private void checkDivideByZero(Location<Operator> expr, Context context) {
		Expr rhs = translateExpression(expr.getOperand(1), context.getEnvironment());
		Value zero = Value.Integer(BigInteger.ZERO);
		Expr.Constant constant = new Expr.Constant(zero, rhs.attributes());
		Expr neqZero = new Expr.Binary(Expr.Binary.Op.NEQ, rhs, constant, rhs.attributes());
		//
		context.emit(new VerificationCondition("division by zero", context.assumptions, neqZero, expr.attributes()));
	}

	private void checkIndexOutOfBounds(Location<Operator> expr, Context context) {
		Expr src = translateExpression(expr.getOperand(0), context.getEnvironment());
		Expr idx = translateExpression(expr.getOperand(1), context.getEnvironment());
		Expr zero = new Expr.Constant(Value.Integer(BigInteger.ZERO));
		Expr length = new Expr.Unary(Expr.Unary.Op.LENGTHOF, src);
		//
		Expr negTest = new Expr.Binary(Expr.Binary.Op.GTEQ, idx, zero, idx.attributes());
		Expr lenTest = new Expr.Binary(Expr.Binary.Op.LT, idx, length, idx.attributes());
		//
		context.emit(new VerificationCondition("index out of bounds (negative)", context.assumptions, negTest,
				expr.attributes()));
		context.emit(new VerificationCondition("index out of bounds (not less than length)", context.assumptions,
				lenTest, expr.attributes()));
	}

	private void checkArrayGeneratorLength(Location<Operator> expr, Context context) {
		Expr rhs = translateExpression(expr.getOperand(1), context.getEnvironment());
		Value zero = Value.Integer(BigInteger.ZERO);
		Expr.Constant constant = new Expr.Constant(zero, rhs.attributes());
		Expr neqZero = new Expr.Binary(Expr.Binary.Op.GTEQ, rhs, constant, rhs.attributes());
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
			internalFailure(e.getMessage(), decl.parent().filename(), e, expr.attributes());
			return null;
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
			Expr[] arguments = Arrays.copyOf(parameters, parameters.length + fm.type().returns().size());
			// FIXME: following broken for multiple returns
			arguments[arguments.length - 1] = translateExpression(expr, context.getEnvironment());
			//
			Expr argument = arguments.length == 1 ? arguments[0] : new Expr.Nary(Expr.Nary.Op.TUPLE, arguments);
			String prefix = bytecode.name().name() + "_ensures_";
			// Finally, generate an appropriate verification condition to check
			// each precondition clause
			for (int i = 0; i != numPostconditions; ++i) {
				Expr clause = new Expr.Invoke(prefix + i, declaration.parent().id(), Collections.EMPTY_LIST, argument);
				context = context.assume(clause);
			}
		}
		//
		return context;
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
		try {
			switch (loc.getOpcode()) {
			case Bytecode.OPCODE_const:
				return translateConstant((Location<Const>) loc, environment);
			case Bytecode.OPCODE_convert:
				return translateConvert((Location<Convert>) loc, environment);
			case Bytecode.OPCODE_fieldload:
				return translateFieldLoad((Location<FieldLoad>) loc, environment);
			case Bytecode.OPCODE_indirectinvoke:
				return translateIndirectInvoke((Location<IndirectInvoke>) loc, environment);
			case Bytecode.OPCODE_invoke:
				return translateInvoke((Location<Invoke>) loc, environment);
			case Bytecode.OPCODE_lambda:
				return translateLambda((Location<Lambda>) loc, environment);
			case Bytecode.OPCODE_some:
			case Bytecode.OPCODE_all:
				return translateQuantifier((Location<Quantifier>) loc, environment);
			case Bytecode.OPCODE_varaccess:
				return translateVariableAccess((Location<VariableAccess>) loc, environment);
			default:
				return translateOperator((Location<Operator>) loc, environment);
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			internalFailure(e.getMessage(), decl.parent().filename(), e, loc.attributes());
			throw e; // deadcode
		}
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
		Value value = convert(bytecode.constant(), expr);
		return new Expr.Constant(value, expr.attributes());
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
			// FIXME: need to include Records in WyCS
			// We need to determine and sort the fields here because records are
			// implemented as WyCS Tuples.
			ArrayList<String> fields = new ArrayList<String>(er.fields().keySet());
			Collections.sort(fields);
			// Now, translate source expression
			Expr src = translateExpression(srcOperand, environment);
			// Generate index expression, which is a tuple index
			Expr index = new Expr.Constant(Value.Integer(BigInteger.valueOf(fields.indexOf(bytecode.fieldName()))));
			// Done
			return new Expr.IndexOf(src, index, expr.attributes());
		} catch (ResolveError e) {
			SyntaxTree tree = expr.getEnclosingTree();
			internalFailure(e.getMessage(), tree.getEnclosingDeclaration().parent().filename(), e, expr.attributes());
			return null;
		}
	}

	private Expr translateIndirectInvoke(Location<IndirectInvoke> expr, LocalEnvironment environment) {
		// FIXME: need to implement this
		return translateAsUnknown(expr, environment);
	}

	private Expr translateInvoke(Location<Invoke> expr, LocalEnvironment environment) {
		Bytecode.Invoke bytecode = expr.getBytecode();
		Expr[] operands = translateExpressions(expr.getOperands(), environment);
		Expr argument = operands.length == 1 ? operands[0]
				: new Expr.Nary(Expr.Nary.Op.TUPLE, operands, expr.attributes());
		//
		return new Expr.Invoke(bytecode.name().name(), bytecode.name().module(), Collections.EMPTY_LIST, argument,
				expr.attributes());
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

	private Expr translateUnaryOperator(Expr.Unary.Op op, Location<Operator> expr, LocalEnvironment environment) {
		Expr e = translateExpression(expr.getOperand(0), environment);
		return new Expr.Unary(op, e, expr.attributes());
	}

	private Expr translateBinaryOperator(Expr.Binary.Op op, Location<Operator> expr, LocalEnvironment environment) {
		Expr lhs = translateExpression(expr.getOperand(0), environment);
		Expr rhs = translateExpression(expr.getOperand(1), environment);
		return new Expr.Binary(op, lhs, rhs, expr.attributes());
	}

	private Expr translateIs(Location<Operator> expr, LocalEnvironment environment) {
		Expr lhs = translateExpression(expr.getOperand(0), environment);
		Location<Const> rhs = (Location<Const>) expr.getOperand(1);
		Bytecode.Const bytecode = rhs.getBytecode();
		Constant.Type constant = (Constant.Type) bytecode.constant();
		SyntacticType typeTest = convert(constant.value(), environment.getParent().enclosingDeclaration);
		return new Expr.Is(lhs, typeTest, expr.attributes());
	}

	private Expr translateArrayIndex(Location<Operator> expr, LocalEnvironment environment) {
		Expr lhs = translateExpression(expr.getOperand(0), environment);
		Expr rhs = translateExpression(expr.getOperand(1), environment);
		return new Expr.IndexOf(lhs, rhs, expr.attributes());
	}

	private Expr translateArrayGenerator(Location<Operator> expr, LocalEnvironment environment) {
		Expr element = translateExpression(expr.getOperand(0), environment);
		Expr count = translateExpression(expr.getOperand(1), environment);
		environment = environment.write(expr.getIndex());
		return new Expr.Binary(Expr.Binary.Op.ARRAYGEN, element, count, expr.attributes());
	}

	private Expr translateArrayInitialiser(Location<Operator> expr, LocalEnvironment environment) {
		Expr[] vals = translateExpressions(expr.getOperands(), environment);
		return new Expr.Nary(Expr.Nary.Op.ARRAY, vals, expr.attributes());
	}

	private Expr translateQuantifier(Location<Quantifier> expr, LocalEnvironment environment) {
		Bytecode.Quantifier bytecode = expr.getBytecode();
		// Determine the type and names of each quantified variable.
		TypePattern pattern = generateQuantifierTypePattern(expr);
		// Apply quantifier ranges
		Expr ranges = generateQuantifierRanges(expr, environment);
		// Generate quantifier body
		Expr body = translateExpression(expr.getOperand(0), environment);
		body = implies(ranges, body);
		// Generate quantifier expression
		switch (bytecode.kind()) {
		case ALL:
			return new Expr.ForAll(pattern, body, expr.attributes());
		case SOME:
		default:
			return new Expr.Exists(pattern, body, expr.attributes());
		}
	}

	private Expr translateRecordInitialiser(Location<Operator> expr, LocalEnvironment environment) {
		Expr[] vals = translateExpressions(expr.getOperands(), environment);
		return new Expr.Nary(Expr.Nary.Op.TUPLE, vals, expr.attributes());
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
		String r = environment.read(expr.getIndex());
		return new Expr.Variable(r, expr.attributes());
	}

	/**
	 * Generate a type pattern representing the type and name of all quantifier
	 * variables described by this quantifier.
	 * 
	 * @param expr
	 * @return
	 */
	private TypePattern generateQuantifierTypePattern(Location<Quantifier> expr) {
		SyntaxTree tree = expr.getEnclosingTree();
		WyilFile.Declaration decl = tree.getEnclosingDeclaration();
		//
		List<TypePattern> params = new ArrayList<TypePattern>();
		for (int i = 0; i != expr.numberOfOperandGroups(); ++i) {
			Location<?>[] group = expr.getOperandGroup(i);
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) group[0];
			SyntacticType varType = convert(var.getType(), decl);
			Expr.Variable varExpr = new Expr.Variable(var.getBytecode().getName(), var.attributes());
			params.add(new TypePattern.Leaf(varType, varExpr));
		}
		return new TypePattern.Tuple(params);
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
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) group[0];
			Expr.Variable varExpr = new Expr.Variable(var.getBytecode().getName(), var.attributes());
			Expr startExpr = translateExpression(group[1], environment);
			Expr endExpr = translateExpression(group[2], environment);
			Expr lhs = new Expr.Binary(Expr.Binary.Op.LTEQ, startExpr, varExpr);
			Expr rhs = new Expr.Binary(Expr.Binary.Op.LT, varExpr, endExpr);
			ranges = and(ranges, and(lhs, rhs));
		}
		return ranges;
	}

	private Expr translateVariableAccess(Location<VariableAccess> expr, LocalEnvironment environment) {
		Location<?> decl = (Location<?>) expr.getOperand(0);
		Bytecode bytecode = decl.getBytecode();
		String var;
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
		return new Expr.Variable(var, expr.attributes());
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
	private Expr implies(Expr antecedent, Expr consequent) {
		if (antecedent == null) {
			return consequent;
		} else {
			return new Expr.Binary(Expr.Binary.Op.IMPLIES, antecedent, consequent);
		}
	}

	/**
	 * Construct a conjunction of two expressions
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private Expr and(Expr lhs, Expr rhs) {
		if (lhs == null) {
			return rhs;
		} else if (rhs == null) {
			return rhs;
		} else {
			return new Expr.Binary(Expr.Binary.Op.AND, lhs, rhs);
		}
	}

	/**
	 * Construct a disjunct of two expressions
	 * 
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private Expr or(Expr lhs, Expr rhs) {
		if (lhs == null) {
			return rhs;
		} else if (rhs == null) {
			return rhs;
		} else {
			return new Expr.Binary(Expr.Binary.Op.OR, lhs, rhs);
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
		return new Context(ancestor.wyalFile, joinedAssumptions, joinedEnvironment, ancestor.enclosingLoop,
				ancestor.verificationConditions);
	}

	private Context joinDescendants(Context ancestor, Context firstDescendant, List<Context> descendants1,
			List<Context> descendants2) {
		ArrayList<Context> descendants = new ArrayList<Context>(descendants1);
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
		for (Map.Entry<Integer, String> e : updated.locals.entrySet()) {
			Integer varIndex = e.getKey();
			String newVarVersionedName = e.getValue();
			String oldVarVersionedName = original.read(varIndex);
			if (!oldVarVersionedName.equals(newVarVersionedName)) {
				// indicates a version change of the given variable.
				Expr.Variable oldVar = new Expr.Variable(oldVarVersionedName);
				Expr.Variable newVar = new Expr.Variable(newVarVersionedName);
				assumptions = assumptions.add(new Expr.Binary(Expr.Binary.Op.EQ, newVar, oldVar));
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
		HashSet<Integer> modified = new HashSet<Integer>();
		HashSet<Integer> deleted = new HashSet<Integer>();
		Map<Integer, String> headLocals = head.environment.locals;

		// Compute the modified and deleted sets
		for (int i = 1; i < contexts.length; ++i) {
			Context ithContext = contexts[i];
			Map<Integer, String> ithLocals = ithContext.environment.locals;
			// First check env against head
			for (Map.Entry<Integer, String> e : ithLocals.entrySet()) {
				Integer key = e.getKey();
				String s1 = e.getValue();
				String s2 = headLocals.get(key);
				if (s1 == null) {
					deleted.add(key);
				} else if (!s1.equals(s2)) {
					modified.add(key);
				}
			}
			// Second, check head against env
			for (Map.Entry<Integer, String> e : headLocals.entrySet()) {
				Integer key = e.getKey();
				String s1 = e.getValue();
				String s2 = ithLocals.get(key);
				if (s1 == null) {
					deleted.add(key);
				} else if (!s1.equals(s2)) {
					modified.add(key);
				}
			}
		}
		// Finally, construct the combined local map
		HashMap<Integer, String> combinedLocals = new HashMap<Integer, String>();
		for (Map.Entry<Integer, String> e : headLocals.entrySet()) {
			Integer key = e.getKey();
			String value = e.getValue();
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
		List<Type> params = declaration.type().params();
		List<Type> returns = declaration.type().returns();
		//
		TypePattern.Leaf[] parameterPatterns = new TypePattern.Leaf[params.size()];
		// second, set initial environment
		int loc = 0;
		for (int i = 0; i != params.size(); ++i, ++loc) {
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) tree.getLocation(loc);
			Expr.Variable v = new Expr.Variable(var.getBytecode().getName());
			SyntacticType parameterType = convert(params.get(i), declaration);
			parameterPatterns[i] = new TypePattern.Leaf(parameterType, v);
		}
		TypePattern.Leaf[] returnPatterns = new TypePattern.Leaf[returns.size()];
		// second, set initial environment
		for (int i = 0; i != returns.size(); ++i, ++loc) {
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) tree.getLocation(loc);
			Expr.Variable v = new Expr.Variable(var.getBytecode().getName());
			SyntacticType returnType = convert(returns.get(i), declaration);
			returnPatterns[i] = new TypePattern.Leaf(returnType, v);
		}
		// Construct the type declaration for the new block macro
		TypePattern from = new TypePattern.Tuple(parameterPatterns);
		TypePattern to = new TypePattern.Tuple(returnPatterns);
		//
		wyalFile.add(wyalFile.new Function(declaration.name(), Collections.EMPTY_LIST, from, to, null));
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
			Expr verificationCondition = buildVerificationCondition(declaration, environment, vc);
			// Add generated verification condition as assertion
			wyalFile.add(wyalFile.new Assert(vc.description, verificationCondition, vc.attributes()));
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
	public Expr buildVerificationCondition(WyilFile.FunctionOrMethod declaration, GlobalEnvironment environment,
			VerificationCondition vc) {
		Expr antecedent = flatten(vc.antecedent);
		Expr consequent = vc.consequent;
		HashSet<String> freeVariables = new HashSet<String>();
		antecedent.freeVariables(freeVariables);
		consequent.freeVariables(freeVariables);
		// Determine any variable aliases as necessary.
		Expr aliases = determineVariableAliases(environment, freeVariables);
		// Construct the initial condition
		Expr verificationCondition = implies(and(aliases, antecedent), vc.consequent);
		// Now, generate type information for any free variables
		if (freeVariables.size() > 0) {
			// This indicates there are one or more free variables in the
			// verification condition. Hence, these must be universally
			// quantified to ensure the vc is well=typed.
			TypePattern types = generateExpressionTypePattern(declaration, environment, freeVariables);
			verificationCondition = new Expr.ForAll(types, verificationCondition);
		}
		// Done
		return verificationCondition;
	}

	/**
	 * Flatten a given assumption set into a single logical condition. The key
	 * challenge here is to try and do this as efficiency as possible.
	 * 
	 * @param assumptions
	 * @return
	 */
	private Expr flatten(AssumptionSet assumptions) {
		Expr result = flattenUpto(assumptions, null);
		if (result == null) {
			return new Expr.Constant(Value.Bool(true));
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
	private Expr flattenUpto(AssumptionSet assumptions, AssumptionSet ancestor) {

		if (assumptions == ancestor) {
			// We have reached the ancestor
			return null;
		} else {
			// Flattern parent assumptions
			AssumptionSet[] parents = assumptions.parents;
			Expr e = null;
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
				Expr factor = flattenUpto(lca, ancestor);
				for (int i = 0; i != parents.length; ++i) {
					e = or(e, flattenUpto(parents[i], lca));
				}
				e = and(factor, e);
			}

			// Combine with local assumptions (if applicable)
			Expr[] local = assumptions.assumptions;
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
	private Expr determineVariableAliases(GlobalEnvironment environment, Set<String> freeVariables) {
		Expr aliases = null;
		for (String var : freeVariables) {
			String parent = environment.getParent(var);
			if (parent != null) {
				// This indicates a variable alias, so construct the necessary
				// equality.
				Expr.Variable lhs = new Expr.Variable(var);
				Expr.Variable rhs = new Expr.Variable(parent);
				Expr aliasEquality = new Expr.Binary(Expr.Binary.Op.EQ, lhs, rhs);
				//
				aliases = and(aliases, aliasEquality);
			}
		}
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
	private TypePattern generateExpressionTypePattern(WyilFile.Declaration declaration, GlobalEnvironment environment,
			Set<String> freeVariables) {
		SyntaxTree tree = declaration.getTree();

		TypePattern[] patterns = new TypePattern[freeVariables.size()];
		int index = 0;
		for (String var : freeVariables) {
			Expr.Variable v = new Expr.Variable(var);
			Location<?> l = tree.getLocation(environment.resolve(var));
			SyntacticType wycsType = convert(l.getType(), declaration);
			patterns[index++] = new TypePattern.Leaf(wycsType, v);
		}
		if (patterns.length == 1) {
			return patterns[0];
		} else {
			return new TypePattern.Tuple(patterns);
		}
	}

	/**
	 * Convert the parameter types for a given function or method declaration
	 * into a corresponding list of type patterns. This is primarily useful for
	 * generating declarations from functions or method.
	 * 
	 * @param params
	 * @param declaration
	 * @return
	 */
	private TypePattern generatePreconditionTypePattern(WyilFile.FunctionOrMethod declaration,
			LocalEnvironment environment) {
		List<Type> params = declaration.type().params();
		int[] parameterLocations = range(0, params.size());
		return generateTypePatterns(declaration, environment, parameterLocations);
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
	private TypePattern generatePostconditionTypePattern(WyilFile.FunctionOrMethod declaration,
			LocalEnvironment environment) {
		List<Type> params = declaration.type().params();
		List<Type> returns = declaration.type().returns();
		int[] parameterLocations = range(0, params.size());
		int[] returnLocations = range(parameterLocations.length, parameterLocations.length + returns.size());
		return generateTypePatterns(declaration, environment, parameterLocations, returnLocations);
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
	private TypePattern generateLoopInvariantTypePattern(WyilFile.Declaration declaration, Location<?>[] loopInvariant,
			LocalEnvironment environment) {
		int[] localVariableLocations = SyntaxTrees.determineUsedVariables(loopInvariant);
		return generateTypePatterns(declaration, environment, localVariableLocations);
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
	private TypePattern generateTypePatterns(WyilFile.Declaration declaration, LocalEnvironment environment,
			int[]... groups) {
		//
		SyntaxTree tree = declaration.getTree();
		int[] locations = flattern(groups);
		TypePattern.Leaf[] patterns = new TypePattern.Leaf[locations.length];
		// second, set initial environment
		for (int i = 0; i != locations.length; ++i) {
			Location<VariableDeclaration> var = (Location<VariableDeclaration>) tree.getLocation(locations[i]);
			String versionedName = environment.read(var.getIndex());
			Expr.Variable v = new Expr.Variable(versionedName);
			SyntacticType parameterType = convert(var.getType(), declaration);
			patterns[i] = new TypePattern.Leaf(parameterType, v);
		}
		//
		if (patterns.length == 1) {
			return patterns[0];
		} else {
			return new TypePattern.Tuple(patterns);
		}
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
	private Value convert(Constant c, SyntaxTree.Location<?> context) {
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
			// where each field is allocated a slot based on an alphabetical
			// sorting
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
			WyilFile.Declaration decl = context.getEnclosingTree().getEnclosingDeclaration();
			internalFailure("unknown constant encountered (" + c + ")", decl.parent().filename(), context.attributes());
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
			SyntacticType element = convert(lt.element(), context);
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
			internalFailure(errorMessage(ErrorMessages.RESOLUTION_ERROR, name.module().toString()),
					decl.parent().filename(), stmt.attributes());
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
		if (expr instanceof Expr.Binary) {
			Expr.Binary binTest = (Expr.Binary) expr;
			Expr.Binary.Op op = null;
			switch (binTest.op) {
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
			case AND: {
				Expr lhs = invertCondition(binTest.leftOperand, elem);
				Expr rhs = invertCondition(binTest.rightOperand, elem);
				return new Expr.Binary(Expr.Binary.Op.OR, lhs, rhs, expr.attributes());
			}
			case OR: {
				Expr lhs = invertCondition(binTest.leftOperand, elem);
				Expr rhs = invertCondition(binTest.rightOperand, elem);
				return new Expr.Binary(Expr.Binary.Op.AND, lhs, rhs, expr.attributes());
			}
			}
			if (op != null) {
				return new Expr.Binary(op, binTest.leftOperand, binTest.rightOperand, expr.attributes());
			}
		} else if (expr instanceof Expr.Is) {
			Expr.Is ei = (Expr.Is) expr;
			SyntacticType type = ei.rightOperand;
			return new Expr.Is(ei.leftOperand, new SyntacticType.Negation(type, type.attributes()), ei.attributes());
		}
		// Otherwise, compare against false
		// FIXME: this is just wierd and needs to be fixed.
		return new Expr.Unary(Expr.Unary.Op.NOT, expr);
	}

	private static <T> T[] append(T[] lhs, T[] rhs) {
		T[] rs = Arrays.copyOf(lhs, lhs.length + rhs.length);
		System.arraycopy(rhs, 0, rs, lhs.length, rhs.length);
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
			T[] rs = Arrays.copyOf(items, items.length - count);
			for (int i = 0, j = 0; i != items.length; ++i) {
				T item = items[i];
				if (item != null) {
					rs[j++] = item;
				}
			}

			return rs;
		}
	}

	public static int[] range(int start, int end) {
		int[] rs = new int[Math.abs(end - start)];
		for (int i = start; i < end; ++i) {
			rs[i - start] = i;
		}
		return rs;
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
		private final Expr[] assumptions;

		private AssumptionSet(AssumptionSet commonAncestor, AssumptionSet[] parents, Expr... assumptions) {
			this.commonAncestor = commonAncestor;
			this.parents = parents;
			this.assumptions = assumptions;
		}

		public AssumptionSet add(Expr... assumptions) {
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
		private final Map<String, String> parents;

		/**
		 * Provides a global mapping of all local variable names to the next
		 * unused version numbers. This is done with variable names rather than
		 * location indices because it is possible two have different variables
		 * with the same name,
		 */
		private final Map<String, Integer> versions;

		public GlobalEnvironment(WyilFile.Declaration enclosingDeclaration) {
			this.enclosingDeclaration = enclosingDeclaration;
			this.allocation = new HashMap<String, Integer>();
			this.parents = new HashMap<String, String>();
			this.versions = new HashMap<String, Integer>();
		}

		/**
		 * Get the parent for a potential variable alias, or null if there is no
		 * alias.
		 * 
		 * @param alias
		 * @return
		 */
		public String getParent(String alias) {
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
		public String allocateVersion(int index) {
			SyntaxTree tree = enclosingDeclaration.getTree();
			Location<?> loc = tree.getLocation(index);
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
			return versionedVar;
		}

		/**
		 * Add a new variable alias for a variable to its parent
		 * 
		 * @param leftVar
		 * @param rightVar
		 */
		public void addVariableAlias(String alias, String parent) {
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
		private final Map<Integer, String> locals;

		public LocalEnvironment(GlobalEnvironment global) {
			this.global = global;
			this.locals = new HashMap<Integer, String>();
		}

		public LocalEnvironment(GlobalEnvironment global, Map<Integer, String> locals) {
			this.global = global;
			this.locals = new HashMap<Integer, String>(locals);
		}

		/**
		 * Get the envlosing global environment for this local environment
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
		public String read(int index) {
			String vv = locals.get(index);
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
		public String readAlias(int alias, int parent) {
			String aliasedVariable = read(alias);
			String parentVariable = read(parent);
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

		public LocalEnvironment clone() {
			return new LocalEnvironment(global, locals);
		}
	}

	public Location<VariableDeclaration> getVariableDeclaration(Location<?> decl) {
		switch (decl.getOpcode()) {
		case OPCODE_aliasdecl:
		case OPCODE_varaccess:
			return getVariableDeclaration(decl.getOperand(0));
		case OPCODE_vardecl:
		case OPCODE_vardeclinit:
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
			this.breakContexts = new ArrayList<Context>();
			this.continueContexts = new ArrayList<Context>();
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
		 * A reference to the enclosing loop scope, or null if no such scope.
		 */
		private final LoopScope enclosingLoop;

		public Context(WyalFile wyalFile, AssumptionSet assumptions, LocalEnvironment environment,
				LoopScope enclosingLoop, List<VerificationCondition> vcs) {
			this.wyalFile = wyalFile;
			this.assumptions = assumptions;
			this.environment = environment;
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
		public Context assume(Expr... conditions) {
			AssumptionSet nAssumptions = assumptions.add(conditions);
			return new Context(wyalFile, nAssumptions, environment, enclosingLoop, verificationConditions);
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
			String nVersionedVar = nEnvironment.read(lhs);
			// Update assumption sets to reflect the "assigment"
			Expr.Variable var = new Expr.Variable(nVersionedVar);
			Expr condition = new Expr.Binary(Expr.Binary.Op.EQ, var, rhs);
			AssumptionSet nAssumptions = assumptions.add(condition);
			//
			return new Context(wyalFile, nAssumptions, nEnvironment, enclosingLoop, verificationConditions);
		}

		public String read(Location<?> expr) {
			return environment.read(expr.getIndex());
		}

		public Context havoc(int lhs) {
			LocalEnvironment nEnvironment = environment.write(lhs);
			//
			return new Context(wyalFile, assumptions, nEnvironment, enclosingLoop, verificationConditions);
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
			return new Context(wyalFile, assumptions, nEnvironment, enclosingLoop, verificationConditions);
		}

		/**
		 * Construct a context within a given loop scope.
		 * 
		 * @param scope
		 * @return
		 */
		public Context newLoopScope(LoopScope scope) {
			return new Context(wyalFile, assumptions, environment, scope, verificationConditions);
		}
	}

	// =============================================================
	// Operator Maps
	// =============================================================

	/**
	 * Maps unary bytecodes into unary expression opcodes.
	 */
	private static Map<Bytecode.OperatorKind, Expr.Unary.Op> unaryOperatorMap;

	/**
	 * Maps binary bytecodes into binary expression opcodes.
	 */
	private static Map<Bytecode.OperatorKind, Expr.Binary.Op> binaryOperatorMap;

	static {
		// Configure operator maps. This is done using maps to ensure that
		// changes in one operator kind does not have knock-on effects. This
		// used to be a problem when an array was used to implement the mapping.

		// =====================================================================
		// Unary operator map
		// =====================================================================
		unaryOperatorMap = new HashMap<Bytecode.OperatorKind, Expr.Unary.Op>();
		// Arithmetic
		unaryOperatorMap.put(Bytecode.OperatorKind.NEG, Expr.Unary.Op.NEG);
		// Logical
		unaryOperatorMap.put(Bytecode.OperatorKind.NOT, Expr.Unary.Op.NOT);
		// Array
		unaryOperatorMap.put(Bytecode.OperatorKind.ARRAYLENGTH, Expr.Unary.Op.LENGTHOF);

		// =====================================================================
		// Binary operator map
		// =====================================================================
		binaryOperatorMap = new HashMap<Bytecode.OperatorKind, Expr.Binary.Op>();
		// Arithmetic
		binaryOperatorMap.put(Bytecode.OperatorKind.ADD, Expr.Binary.Op.ADD);
		binaryOperatorMap.put(Bytecode.OperatorKind.SUB, Expr.Binary.Op.SUB);
		binaryOperatorMap.put(Bytecode.OperatorKind.MUL, Expr.Binary.Op.MUL);
		binaryOperatorMap.put(Bytecode.OperatorKind.DIV, Expr.Binary.Op.DIV);
		binaryOperatorMap.put(Bytecode.OperatorKind.REM, Expr.Binary.Op.REM);
		// Equality
		binaryOperatorMap.put(Bytecode.OperatorKind.EQ, Expr.Binary.Op.EQ);
		binaryOperatorMap.put(Bytecode.OperatorKind.NEQ, Expr.Binary.Op.NEQ);
		// Relational
		binaryOperatorMap.put(Bytecode.OperatorKind.LT, Expr.Binary.Op.LT);
		binaryOperatorMap.put(Bytecode.OperatorKind.GT, Expr.Binary.Op.GT);
		binaryOperatorMap.put(Bytecode.OperatorKind.LTEQ, Expr.Binary.Op.LTEQ);
		binaryOperatorMap.put(Bytecode.OperatorKind.GTEQ, Expr.Binary.Op.GTEQ);
		// Logical
		binaryOperatorMap.put(Bytecode.OperatorKind.AND, Expr.Binary.Op.AND);
		binaryOperatorMap.put(Bytecode.OperatorKind.OR, Expr.Binary.Op.OR);
	}
}
