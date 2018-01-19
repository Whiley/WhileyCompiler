// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.stage;

import static wyc.util.ErrorMessages.errorMessage;

import java.math.BigInteger;
import java.util.*;

import wybs.lang.Attribute;
import wybs.lang.NameID;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError.InternalFailure;
import wycc.util.Pair;
import wycc.util.ArrayUtils;
import wyal.lang.WyalFile;
import wyal.lang.WyalFile.Declaration;
import wyal.lang.WyalFile.Expr;
//import wyal.lang.WyalFile.Type;
import static wyal.lang.WyalFile.Value;
import wyal.lang.WyalFile.Declaration.Named;
import wyfs.lang.Path;
import wyfs.lang.Path.ID;
import wyfs.util.Trie;
import wyc.lang.WhileyFile;
import wyc.task.Wyil2WyalBuilder;
import wyc.util.AbstractConsumer;
import wyc.util.AbstractVisitor;

import static wyc.lang.WhileyFile.*;

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
 * <li><b>(path: 1,2,3)</b> <code>(x >= 0) ==> (x >= 0)</code>. This verification
 * condition corresponds to the case where the if condition is known to be true.</li>
 * <li><b>(path: 1,2,4)</b> <code> (x < 0) ==> (-x >= 0)</code>. This verification
 * condition corresponds to the case where the if condition is known to be false.</li>
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
	private final NameResolver resolver;
	private final WyalFile wyalFile;

	public VerificationConditionGenerator(WyalFile wyalFile, NameResolver resolver) {
		this.resolver = resolver;
		this.wyalFile = wyalFile;
	}

	// ===============================================================================
	// Top-Level Controller
	// ===============================================================================

	/**
	 * Translate a WhileyFile into a WyalFile which contains the verification
	 * conditions necessary to establish that all functions and methods in the
	 * WhileyFile meet their specifications, and that no array-out-of-bounds or
	 * division-by-zero exceptions are possible (amongst other things).
	 *
	 * @param wyilFile
	 *            The input file to be translated
	 * @return
	 */
	public WyalFile translate(WhileyFile wyilFile) {
		for(WhileyFile.Decl decl : wyilFile.getDeclarations()) {
			if(decl instanceof WhileyFile.Decl.Import) {
				translateImportDeclaration((WhileyFile.Decl.Import) decl);
			} else if (decl instanceof WhileyFile.Decl.StaticVariable) {
				translateConstantDeclaration((WhileyFile.Decl.StaticVariable) decl);
			} else if (decl instanceof WhileyFile.Decl.Type) {
				translateTypeDeclaration((WhileyFile.Decl.Type) decl);
			} else if (decl instanceof WhileyFile.Decl.Property) {
				translatePropertyDeclaration((WhileyFile.Decl.Property) decl);
			} else if (decl instanceof WhileyFile.Decl.FunctionOrMethod) {
				WhileyFile.Decl.FunctionOrMethod method = (WhileyFile.Decl.FunctionOrMethod) decl;
				translateFunctionOrMethodDeclaration(method);
			}
		}

		return wyalFile;
	}

	private void translateImportDeclaration(WhileyFile.Decl.Import decl) {
		WyalFile.Declaration.Import imprt = new WyalFile.Declaration.Import(decl.getPath().toArray(Identifier.class));
		allocate(imprt, getSpan(decl));
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
	private void translateConstantDeclaration(WhileyFile.Decl.StaticVariable decl) {
		if(decl.hasInitialiser()) {
			// The environments are needed to prevent clashes between variable
			// versions across verification conditions, and also to type variables
			// used in verification conditions.
			GlobalEnvironment globalEnvironment = new GlobalEnvironment(decl);
			LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
			List<VerificationCondition> vcs = new ArrayList<>();
			Context context = new Context(wyalFile, AssumptionSet.ROOT, localEnvironment, localEnvironment, null, vcs);
			//
			Pair<Expr, Context> rp = translateExpressionWithChecks(decl.getInitialiser(), null, context);
			generateTypeInvariantCheck(decl.getType(),rp.first(),context);
			// Translate each generated verification condition into an assertion in
			// the underlying WyalFile.
			createAssertions(decl, vcs, globalEnvironment);
		}
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
	private void translateTypeDeclaration(WhileyFile.Decl.Type declaration) {
		WhileyFile.Tuple<WhileyFile.Expr> invariants = declaration.getInvariant();
		WyalFile.Stmt.Block[] invariant = new WyalFile.Stmt.Block[invariants.size()];
		WyalFile.Type type = convert(declaration.getType(), declaration);
		WyalFile.VariableDeclaration var;
		if (invariants.size() > 0) {
			WhileyFile.Decl.Variable v = declaration.getVariableDeclaration();
			// First, translate the invariant (if applicable)
			GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
			LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
			var = localEnvironment.read(v);
			for (int i = 0; i != invariant.length; ++i) {
				invariant[i] = translateAsBlock(invariants.get(i), localEnvironment);
			}
		} else {
			var = new WyalFile.VariableDeclaration(type, new WyalFile.Identifier("self"));
		}
		// Done
		WyalFile.Identifier name = declaration.getName();
		WyalFile.Declaration td = new WyalFile.Declaration.Named.Type(name, var, invariant);
		allocate(td,declaration.getParent(WhileyFile.Attribute.Span.class));
	}

	private void translatePropertyDeclaration(WhileyFile.Decl.Property declaration) {
		//
		GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
		LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
		WyalFile.VariableDeclaration[] type = generatePreconditionParameters(declaration, localEnvironment);
		Tuple<WhileyFile.Expr> invariants = declaration.getInvariant();
		//
		WyalFile.Stmt[] stmts = new WyalFile.Stmt[invariants.size()];
		//
		for (int i = 0; i != invariants.size(); ++i) {
			// Translate expression itself
			stmts[i] = translateAsBlock(invariants.get(i), localEnvironment);
		}
		//
		WyalFile.Stmt.Block block = new WyalFile.Stmt.Block(stmts);
		WyalFile.Identifier name = declaration.getName();
		WyalFile.Declaration pd = new WyalFile.Declaration.Named.Macro(name, type, block);
		allocate(pd,declaration.getParent(WhileyFile.Attribute.Span.class));
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
	private void translateFunctionOrMethodDeclaration(WhileyFile.Decl.FunctionOrMethod declaration) {
		// Create the prototype for this function or method. This is the
		// function or method declaration which can be used within verification
		// conditions to refer to this function or method. This does not include
		// a body, since function or methods are treated as being
		// "uninterpreted" for the purposes of verification.
		createFunctionOrMethodPrototype(declaration);

		// Create macros representing the individual clauses of the function or
		// method's precondition and postcondition. These macros can then be
		// called either to assume the precondition/postcondition or to check
		// them. Using individual clauses helps to provide better error
		// messages.
		translatePreconditionMacros(declaration);
		translatePostconditionMacros(declaration);

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
		createAssertions(declaration, vcs, globalEnvironment);
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
	private void translatePreconditionMacros(WhileyFile.Decl.FunctionOrMethod declaration) {
		Tuple<WhileyFile.Expr> invariants = declaration.getRequires();
		//
		String prefix = declaration.getName() + "_requires_";
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
			WyalFile.Stmt.Block clause = translateAsBlock(invariants.get(i), localEnvironment);
			// Capture any free variables. This is necessary to deal with any
			// variable aliases introduced by type test operators.
			clause = captureFreeVariables(declaration, globalEnvironment, clause);
			//
			WyalFile.Identifier ident = new WyalFile.Identifier(name);
			WyalFile.Declaration md = new WyalFile.Declaration.Named.Macro(ident, type, clause);
			allocate(md,invariants.get(i).getParent(WhileyFile.Attribute.Span.class));
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
	private void translatePostconditionMacros(WhileyFile.Decl.FunctionOrMethod declaration) {
		Tuple<WhileyFile.Expr> invariants = declaration.getEnsures();
		//
		String prefix = declaration.getName() + "_ensures_";
		//
		for (int i = 0; i != invariants.size(); ++i) {
			String name = prefix + i;
			// Construct fresh environment for this macro. This is necessary to
			// avoid name clashes with subsequent macros.
			GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
			LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
			WyalFile.VariableDeclaration[] type = generatePostconditionTypePattern(declaration, localEnvironment);
			WyalFile.Stmt.Block clause = translateAsBlock(invariants.get(i), localEnvironment.clone());
			// Capture any free variables. This is necessary to deal with any
			// variable aliases introduced by type test operators.
			clause = captureFreeVariables(declaration, globalEnvironment, clause);
			//
			WyalFile.Identifier ident = new WyalFile.Identifier(name);
			WyalFile.Declaration md = new WyalFile.Declaration.Named.Macro(ident, type, clause);
			allocate(md, invariants.get(i).getParent(WhileyFile.Attribute.Span.class));
		}
	}

	private WyalFile.Stmt.Block captureFreeVariables(WhileyFile.Decl declaration,
			GlobalEnvironment globalEnvironment,
			WyalFile.Stmt.Block clause) {
		HashSet<WyalFile.VariableDeclaration> freeVariables = new HashSet<>();
		HashSet<WyalFile.VariableDeclaration> freeAliases = new HashSet<>();
		freeVariables(clause,freeVariables);
		for (WyalFile.VariableDeclaration var : freeVariables) {
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
			WyalFile.VariableDeclaration[] types = generateExpressionTypePattern(declaration, globalEnvironment,
					freeAliases);
			Expr aliases = determineVariableAliases(globalEnvironment, freeAliases);
			//
			WyalFile.Stmt.Block body = new WyalFile.Stmt.Block(implies(aliases, clause));
			return new WyalFile.Stmt.Block(new WyalFile.Stmt.UniversalQuantifier(types, body));
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
	private AssumptionSet generateFunctionOrMethodAssumptionSet(WhileyFile.Decl.FunctionOrMethod declaration,
			LocalEnvironment environment) {
		Tuple<WhileyFile.Decl.Variable> parameters = declaration.getParameters();
		String prefix = declaration.getName() + "_requires_";
		Expr[] preconditions = new Expr[declaration.getRequires().size()];
		Expr[] arguments = new Expr[parameters.size()];
		// Translate parameters as arguments to invocation
		for (int i = 0; i != arguments.length; ++i) {
			WhileyFile.Decl.Variable var = parameters.get(i);
			WyalFile.VariableDeclaration versionedVar = environment.read(var);
			arguments[i] = new Expr.VariableAccess(versionedVar);
		}
		//
		for (int i = 0; i != preconditions.length; ++i) {
			WyalFile.Name name = new WyalFile.Name(new WyalFile.Identifier(prefix + i));
			preconditions[i] = new Expr.Invoke(null, name, null, arguments);
		}
		// Add all the preconditions as assupmtions
		return AssumptionSet.ROOT.add(preconditions);
	}

	// =========================================================================
	// Statements
	// =========================================================================

	private Context translateStatementBlock(WhileyFile.Stmt.Block block, Context context) {
		for (int i = 0; i != block.size(); ++i) {
			WhileyFile.Stmt stmt = block.get(i);
			context = translateStatement(stmt, context);
			if (stmt instanceof WhileyFile.Stmt.Return) {
				return null;
			}
		}
		return context;
	}

	@SuppressWarnings("unchecked")
	private Context translateStatement(WhileyFile.Stmt stmt, Context context) {
		//
		try {
			switch (stmt.getOpcode()) {
			case WhileyFile.STMT_assert:
				return translateAssert((WhileyFile.Stmt.Assert) stmt, context);
			case WhileyFile.STMT_assign:
				return translateAssign((WhileyFile.Stmt.Assign) stmt, context);
			case WhileyFile.STMT_assume:
				return translateAssume((WhileyFile.Stmt.Assume) stmt, context);
			case WhileyFile.STMT_break:
				return translateBreak((WhileyFile.Stmt.Break) stmt, context);
			case WhileyFile.STMT_continue:
				return translateContinue((WhileyFile.Stmt.Continue) stmt, context);
			case WhileyFile.STMT_debug:
				return context;
			case WhileyFile.STMT_dowhile:
				return translateDoWhile((WhileyFile.Stmt.DoWhile) stmt, context);
			case WhileyFile.STMT_fail:
				return translateFail((WhileyFile.Stmt.Fail) stmt, context);
			case WhileyFile.STMT_if:
			case WhileyFile.STMT_ifelse:
				return translateIf((WhileyFile.Stmt.IfElse) stmt, context);
			case WhileyFile.EXPR_indirectinvoke:
			case WhileyFile.EXPR_invoke: {
				WhileyFile.Expr expr = (WhileyFile.Expr) stmt;
				Pair<Expr,Context> r = translateExpressionWithChecks(expr, null, context);
				return r.second();
			}
			case WhileyFile.STMT_namedblock:
				return translateNamedBlock((WhileyFile.Stmt.NamedBlock) stmt, context);
			case WhileyFile.STMT_return:
				return translateReturn((WhileyFile.Stmt.Return) stmt, context);
			case WhileyFile.STMT_skip:
				return translateSkip((WhileyFile.Stmt.Skip) stmt, context);
			case WhileyFile.STMT_switch:
				return translateSwitch((WhileyFile.Stmt.Switch) stmt, context);
			case WhileyFile.STMT_while:
				return translateWhile((WhileyFile.Stmt.While) stmt, context);
			case WhileyFile.DECL_variable:
			case WhileyFile.DECL_variableinitialiser:
				return translateVariableDeclaration((WhileyFile.Decl.Variable) stmt, context);
			default:
				throw new InternalFailure("unknown statement encountered (" + stmt + ")",
						((WhileyFile) stmt.getHeap()).getEntry(), stmt);
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			throw new InternalFailure(e.getMessage(), ((WhileyFile) stmt.getHeap()).getEntry(), stmt, e);
		}
	}

	/**
	 * Translate an assert statement. This emits a verification condition which
	 * ensures the assert condition holds, given the current context.
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateAssert(WhileyFile.Stmt.Assert stmt, Context context) {
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getCondition(), null, context);
		Expr condition = p.first();
		context = p.second();
		//
		VerificationCondition verificationCondition = new VerificationCondition("assertion failed", context.assumptions,
				condition, stmt.getCondition().getParent(WhileyFile.Attribute.Span.class));
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
	 * @throws ResolutionError
	 */
	private Context translateAssign(WhileyFile.Stmt.Assign stmt, Context context) throws ResolutionError {
		Tuple<WhileyFile.LVal> lhs = stmt.getLeftHandSide();
		Tuple<WhileyFile.Expr> rhs = stmt.getRightHandSide();
		WhileyFile.LVal[][] lvals = new LVal[rhs.size()][];
		Expr[][] rvals = new Expr[rhs.size()][];
		// First, generate bundles
		for (int i = 0, j = 0; i != rhs.size(); ++i) {
			WhileyFile.Expr rval = rhs.get(i);
			lvals[i] = generateEmptyLValBundle(rval.getTypes(),lhs,j);
			j += lvals[i].length;
			Pair<Expr[],Context> p = generateRValBundle(rval,context);
			rvals[i] = p.first();
			context = p.second();
		}
		// Second, apply the bundles to implement assignments.
		for (int i = 0; i != rhs.size(); ++i) {
			context = translateAssign(lvals[i], rvals[i], context);
		}
		// Done
		return context;
	}

	private WhileyFile.LVal[] generateEmptyLValBundle(Tuple<WhileyFile.Type> types, Tuple<WhileyFile.LVal> lhs, int j) {
		WhileyFile.LVal[] lval;
		if(types == null) {
			lval = new WhileyFile.LVal[] {lhs.get(j)};
		} else {
			lval = new WhileyFile.LVal[types.size()];
			for(int k=0;k!=types.size();++k) {
				lval[k] = lhs.get(j++);
			}
		}
		return lval;
	}

	private Pair<Expr[], Context> generateRValBundle(WhileyFile.Expr rhs, Context context) {
		Tuple<WhileyFile.Type> types = rhs.getTypes();
		int size = (types == null) ? 1 : types.size();
		Expr[] rvals = new Expr[size];
		for (int i = 0; i != rvals.length; ++i) {
			if (i == 0) {
				// First time around, we should generate appropriate
				// precondition checks. We also need to determine whether the
				// selector is null or not.
				Integer selector = (size == 1) ? null : i;
				Pair<Expr, Context> rp = translateExpressionWithChecks(rhs, selector, context);
				context = rp.second();
				rvals[i] = rp.first();
			} else {
				// Second time around, don't need to regenerate the precondition
				// checks. We also know that the selector should be non-null.
				rvals[i] = translateExpression(rhs, i, context.getEnvironment());
			}
		}
		//
		return new Pair<>(rvals, context);
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
	 * @throws ResolutionError
	 */
	private Context translateAssign(WhileyFile.LVal[] lval, Expr[] rval, Context context) throws ResolutionError {
		Expr[] ls = new Expr[lval.length];
		for (int i = 0; i != ls.length; ++i) {
			WhileyFile.LVal lhs = lval[i];
			generateTypeInvariantCheck(lhs.getType(), rval[i], context);
			context = translateSingleAssignment(lval[i], rval[i], context);
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
	private Context translateSingleAssignment(WhileyFile.LVal lval, Expr rval, Context context) {

		// FIXME: this method is a bit of a kludge. It would be nicer,
		// eventually, to have all right-hand side expression represented in
		// WyTP directly. This could potentially be done by including an update
		// operation in WyTP ... ?

		switch (lval.getOpcode()) {
		case WhileyFile.EXPR_arrayaccess:
		case WhileyFile.EXPR_arrayborrow:
			return translateArrayAssign((WhileyFile.Expr.ArrayAccess) lval, rval, context);
		case WhileyFile.EXPR_dereference:
			return translateDereference((WhileyFile.Expr.Dereference) lval, rval, context);
		case WhileyFile.EXPR_recordaccess:
		case WhileyFile.EXPR_recordborrow:
			return translateRecordAssign((WhileyFile.Expr.RecordAccess) lval, rval, context);
		case WhileyFile.EXPR_variablemove:
		case WhileyFile.EXPR_variablecopy:
			return translateVariableAssign((WhileyFile.Expr.VariableAccess) lval, rval, context);
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
	private Context translateRecordAssign(WhileyFile.Expr.RecordAccess lval, Expr rval,Context context) {
		// Translate src expression
		Pair<Expr, Context> p1 = translateExpressionWithChecks(lval.getOperand(), null, context);
		Expr source = p1.first();
		WyalFile.Identifier field = new WyalFile.Identifier(lval.getField().toString());
		// Construct record update for "pass thru"
		Expr update = new Expr.RecordUpdate(source, field, rval);
		return translateSingleAssignment((WhileyFile.LVal) lval.getOperand(),update,p1.second());
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
	private Context translateArrayAssign(WhileyFile.Expr.ArrayAccess lval, Expr rval, Context context) {
		// Translate src and index expressions
		Pair<Expr, Context> p1 = translateExpressionWithChecks(lval.getFirstOperand(), null, context);
		Pair<Expr, Context> p2 = translateExpressionWithChecks(lval.getSecondOperand(), null, p1.second());
		Expr source = p1.first();
		Expr index = p2.first();
		// Emit verification conditions to check access in bounds
		checkExpressionPreconditions(lval, p2.second());
		// Construct array update for "pass thru"
		Expr.Operator update = new Expr.ArrayUpdate(source, index, rval);
		return translateSingleAssignment((LVal) lval.getFirstOperand(), update, p2.second());
	}

	/**
	 * Translate an indirect assignment through a reference.
	 *
	 * @param lval
	 *            The array assignment expression
	 * @param result
	 *            The value being assigned to the given array element
	 * @param context
	 *            The enclosing context
	 * @return
	 */
	private Context translateDereference(WhileyFile.Expr.Dereference lval, Expr rval, Context context) {
		Expr e = translateDereference(lval,context.getEnvironment());
		return context.assume(new Expr.Equal(e, rval));
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
	private Context translateVariableAssign(WhileyFile.Expr.VariableAccess lval, Expr rval,Context context) {
		WhileyFile.Decl.Variable decl = lval.getVariableDeclaration();
		context = context.havoc(decl);
		WyalFile.VariableDeclaration nVersionedVar = context.read(decl);
		Expr.VariableAccess var = new Expr.VariableAccess(nVersionedVar);
		return context.assume(new Expr.Equal(var, rval));
	}

	/**
	 * Determine the variable at the root of a given sequence of assignments, or
	 * return null if there is no statically determinable variable.
	 *
	 * @param lval
	 * @return
	 */
	private WhileyFile.Expr.VariableAccess extractAssignedVariable(WhileyFile.LVal lval) {
		//
		switch (lval.getOpcode()) {
		case WhileyFile.EXPR_arrayaccess:
		case WhileyFile.EXPR_arrayborrow:
			Expr.ArrayAccess ae = (Expr.ArrayAccess) lval;
			return extractAssignedVariable((LVal) ae.getSource());
		case WhileyFile.EXPR_dereference:
			return null;
		case WhileyFile.EXPR_recordaccess:
		case WhileyFile.EXPR_recordborrow:
			Expr.RecordAccess ar = (Expr.RecordAccess) lval;
			return extractAssignedVariable((LVal) ar.getSource());
		case WhileyFile.EXPR_variablemove:
		case WhileyFile.EXPR_variablecopy:
			return (WhileyFile.Expr.VariableAccess) lval;
		default:
			throw new InternalFailure("unknown lval encountered (" + lval + ")",
					((WhileyFile) lval.getHeap()).getEntry(), lval);
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
	private Context translateAssume(WhileyFile.Stmt.Assume stmt, Context context) {
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getCondition(), null, context);
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
	private Context translateBreak(WhileyFile.Stmt.Break stmt, Context context) {
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
	private Context translateContinue(WhileyFile.Stmt.Continue stmt, Context context) {
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
	private Context translateDoWhile(WhileyFile.Stmt.DoWhile stmt, Context context) {
		WhileyFile.Decl.FunctionOrMethod declaration = (WhileyFile.Decl.FunctionOrMethod) context
				.getEnvironment().getParent().enclosingDeclaration;
		// Translate the loop invariant and generate appropriate macro
		translateLoopInvariantMacros(stmt, declaration, context.wyalFile);
		// Rule 1. Check loop invariant after first iteration
		LoopScope firstScope = new LoopScope();
		Context beforeFirstBodyContext = context.newLoopScope(firstScope);
		Context afterFirstBodyContext = translateStatementBlock(stmt.getBody(), beforeFirstBodyContext);
		// Join continue contexts together since they must also preserve the
		// loop invariant
		afterFirstBodyContext = joinDescendants(beforeFirstBodyContext, afterFirstBodyContext,
				firstScope.continueContexts);
		//
		checkLoopInvariant("loop invariant not established by first iteration", stmt, afterFirstBodyContext);
		// Rule 2. Check loop invariant preserved on subsequence iterations. On
		// entry to the loop body we must havoc all modified variables. This is
		// necessary as such variables should retain their values from before
		// the loop.
		LoopScope arbitraryScope = new LoopScope();
		Context beforeArbitraryBodyContext = context.newLoopScope(arbitraryScope).havoc(stmt.getModified());
		beforeArbitraryBodyContext = assumeLoopInvariant(stmt, beforeArbitraryBodyContext);
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getCondition(), null, beforeArbitraryBodyContext);
		Expr trueCondition = p.first();
		beforeArbitraryBodyContext = p.second().assume(trueCondition);
		Context afterArbitraryBodyContext = translateStatementBlock(stmt.getBody(), beforeArbitraryBodyContext);
		// Join continue contexts together since they must also preserve the
		// loop invariant
		afterArbitraryBodyContext = joinDescendants(beforeArbitraryBodyContext, afterArbitraryBodyContext,
				arbitraryScope.continueContexts);
		//
		checkLoopInvariant("loop invariant not restored", stmt, afterArbitraryBodyContext);
		// Rule 3. Assume loop invariant holds.
		Context exitContext = context.havoc(stmt.getModified());
		exitContext = assumeLoopInvariant(stmt, exitContext);
		Expr falseCondition = invertCondition(
				translateExpression(stmt.getCondition(), null, exitContext.getEnvironment()), stmt.getCondition());
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
	private Context translateFail(WhileyFile.Stmt.Fail stmt, Context context) {
		Expr condition = new Expr.Constant(new Value.Bool(false));
		//
		VerificationCondition verificationCondition = new VerificationCondition("possible panic", context.assumptions,
				condition, stmt.getParent(WhileyFile.Attribute.Span.class));
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
	private Context translateIf(WhileyFile.Stmt.IfElse stmt, Context context) {
		//
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getCondition(), null, context);
		Expr trueCondition = p.first();
		// FIXME: this is broken as includes assumptions propagated through
		// logical &&'s
		context = p.second();
		Expr falseCondition = invertCondition(trueCondition, stmt.getCondition());
		//
		Context trueContext = context.assume(trueCondition);
		Context falseContext = context.assume(falseCondition);
		//
		trueContext = translateStatementBlock(stmt.getTrueBranch(), trueContext);
		if (stmt.hasFalseBranch()) {
			falseContext = translateStatementBlock(stmt.getFalseBranch(), falseContext);
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
	private Context translateNamedBlock(WhileyFile.Stmt.NamedBlock stmt, Context context) {
		return translateStatementBlock(stmt.getBlock(), context);
	}

	/**
	 * Translate a return statement. If a return value is given, then this must
	 * ensure that the post-condition of the enclosing function or method is met
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateReturn(WhileyFile.Stmt.Return stmt, Context context) {
		//
		Tuple<WhileyFile.Expr> returns = stmt.getReturns();
		//
		if (returns.size() > 0) {
			// There is at least one return value. Therefore, we need to check
			// any preconditions for those return expressions and, potentially,
			// ensure any postconditions of the cnlosing function/method are
			// met.
			Pair<Expr[], Context> p = translateExpressionsWithChecks(returns, context);
			Expr[] exprs = p.first();
			context = p.second();
			//
			generateReturnTypeInvariantCheck(stmt,exprs,context);
			generatePostconditionChecks(stmt,exprs,context);
		}
		// Return null to signal that execution does not continue after this
		// return statement.
		return null;
	}

	/**
	 * Generate a return type check in the case that it is necessary. For
	 * example, if the return type contains a type invariant then it is likely
	 * to be necessary. However, in the special case that the value being
	 * returned is already of appropriate type, then it is not.
	 *
	 * @param stmt
	 * @param exprs
	 * @param context
	 * @throws ResolveError
	 */
	private void generateReturnTypeInvariantCheck(WhileyFile.Stmt.Return stmt, Expr[] exprs, Context context) {
		WhileyFile.Decl.FunctionOrMethod declaration = (WhileyFile.Decl.FunctionOrMethod) context
				.getEnvironment().getParent().enclosingDeclaration;
		Tuple<WhileyFile.Type> returnTypes = declaration.getType().getReturns();
		//
		for (int i = 0; i != exprs.length; ++i) {
			WhileyFile.Type returnType = returnTypes.get(i);
			// FIXME: at this point, we want to determine whether or not the
			// check is actually required. To do this, we need to check whether
			// the actualType is a true subtype of the returnType.
			generateTypeInvariantCheck(returnType, exprs[i], context);
		}
	}

	public void generateTypeInvariantCheck(Type lhs, Expr rhs, Context context) {
		if (typeMayHaveInvariant(lhs, context)) {
			WyalFile.Type typeTest = convert(lhs, context.getEnvironment().getParent().enclosingDeclaration);
			Expr clause = new Expr.Is(rhs, typeTest);
			context.emit(new VerificationCondition("type invariant not satisfied", context.assumptions, clause,
					getSpan(rhs)));
		}
	}

	/**
	 * Generate the post-condition checks necessary at a return statement in a
	 * function or method.
	 *
	 * @param stmt
	 * @param exprs
	 * @param context
	 */
	private void generatePostconditionChecks(WhileyFile.Stmt.Return stmt, Expr[] exprs, Context context) {
		WhileyFile.Decl.FunctionOrMethod declaration = (WhileyFile.Decl.FunctionOrMethod) context
				.getEnvironment().getParent().enclosingDeclaration;
		WhileyFile.Tuple<WhileyFile.Expr> postcondition = declaration.getEnsures();
		Tuple<WhileyFile.Decl.Variable> parameters = declaration.getParameters();
		Tuple<WhileyFile.Decl.Variable> returns = declaration.getReturns();
		// First, check whether or not there are any postconditions!
		if (postcondition.size() > 0) {
			// There is at least one return value and at least one
			// postcondition clause. Therefore, we need to check the return
			// values against the post condition(s). One of the difficulties
			// here is that the postcondition will refer to parameters as
			// they were on entry to the function/method, not as they are
			// now.
			Expr[] arguments = new Expr[parameters.size() + returns.size()];
			// Translate parameters as arguments to post-condition
			// invocation
			for (int i = 0; i != parameters.size(); ++i) {
				WhileyFile.Decl.Variable var = parameters.get(i);
				WyalFile.VariableDeclaration vd = context.readFirst(var);
				arguments[i] = new Expr.VariableAccess(vd);
			}
			// Copy over return expressions as arguments for invocation(s)
			System.arraycopy(exprs, 0, arguments, parameters.size(), exprs.length);
			//
			String prefix = declaration.getName() + "_ensures_";
			// Finally, generate an appropriate verification condition to
			// check each postcondition clause
			for (int i = 0; i != postcondition.size(); ++i) {
				WyalFile.Name name = new WyalFile.Name(new WyalFile.Identifier(prefix + i));
				Expr clause = new Expr.Invoke(null, name, null, arguments);
				context.emit(new VerificationCondition("postcondition not satisfied", context.assumptions, clause,
						stmt.getParent(WhileyFile.Attribute.Span.class)));
			}
		}
	}

	/**
	 * Translate a skip statement, which obviously does nothing
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateSkip(WhileyFile.Stmt.Skip stmt, Context context) {
		return context;
	}

	/**
	 * Translate a switch statement.
	 *
	 * @param stmt
	 * @param wyalFile
	 */
	private Context translateSwitch(WhileyFile.Stmt.Switch stmt, Context context) {
		Tuple<WhileyFile.Stmt.Case> cases = stmt.getCases();
		//
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getCondition(), null, context);
		Expr value = p.first();
		context = p.second();
		//
		WyalFile.Stmt defaultValue = null;
		Context[] descendants = new Context[cases.size() + 1];
		Context defaultContext = null;
		boolean hasDefault = false;
		//
		for (int i = 0; i != cases.size(); ++i) {
			WhileyFile.Stmt.Case caSe = cases.get(i);
			Context caseContext;
			// Setup knowledge from case values
			if (!caSe.isDefault()) {
				WyalFile.Stmt e = null;
				for (WhileyFile.Expr constant : caSe.getConditions()) {
					//Expr v = convert(constant, stmt, context.getEnvironment());
					Expr v = translateExpression(constant, null, context.getEnvironment());
					e = or(e, new Expr.Equal(value, v));
					defaultValue = and(defaultValue, new Expr.NotEqual(value, v));
				}
				caseContext = context.assume(e);
				descendants[i] = translateStatementBlock(caSe.getBlock(), caseContext);
			} else {
				defaultContext = context.assume(defaultValue);
				defaultContext = translateStatementBlock(caSe.getBlock(), defaultContext);
				hasDefault = true;
			}
		}
		// Sort out default context
		if (!hasDefault) {
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
	private Context translateWhile(WhileyFile.Stmt.While stmt, Context context) {
		WhileyFile.Decl.FunctionOrMethod declaration = (WhileyFile.Decl.FunctionOrMethod) context
				.getEnvironment().getParent().enclosingDeclaration;
		// Translate the loop invariant and generate appropriate macro
		translateLoopInvariantMacros(stmt, declaration, context.wyalFile);
		// Rule 1. Check loop invariant on entry
		checkLoopInvariant("loop invariant does not hold on entry", stmt, context);
		// Rule 2. Check loop invariant preserved. On entry to the loop body we
		// must havoc all modified variables. This is necessary as such
		// variables should retain their values from before the loop.
		LoopScope scope = new LoopScope();
		Context beforeBodyContext = context.newLoopScope(scope).havoc(stmt.getModified());
		beforeBodyContext = assumeLoopInvariant(stmt, beforeBodyContext);
		Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getCondition(), null, beforeBodyContext);
		Expr trueCondition = p.first();
		beforeBodyContext = p.second().assume(trueCondition);
		Context afterBodyContext = translateStatementBlock(stmt.getBody(), beforeBodyContext);
		// Join continue contexts together since they must also preserve the
		// loop invariant
		afterBodyContext = joinDescendants(beforeBodyContext, afterBodyContext, scope.continueContexts);
		checkLoopInvariant("loop invariant not restored", stmt, afterBodyContext);
		// Rule 3. Assume loop invariant holds.
		Context exitContext = context.havoc(stmt.getModified());
		exitContext = assumeLoopInvariant(stmt, exitContext);
		Expr falseCondition = invertCondition(
				translateExpression(stmt.getCondition(), null, exitContext.getEnvironment()), stmt.getCondition());
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
	private void translateLoopInvariantMacros(Stmt.Loop stmt, WhileyFile.Decl.FunctionOrMethod declaration,
			WyalFile wyalFile) {
		//
		String prefix = declaration.getName() + "_loopinvariant_";
		Tuple<WhileyFile.Expr> loopInvariant = stmt.getInvariant();
		//
		for (int i = 0; i != loopInvariant.size(); ++i) {
			WhileyFile.Expr clause = loopInvariant.get(i);
			WyalFile.Identifier name = new WyalFile.Identifier(prefix + clause.getIndex());
			// Construct fresh environment for this macro. This is necessary to
			// avoid name clashes with subsequent macros.
			GlobalEnvironment globalEnvironment = new GlobalEnvironment(declaration);
			LocalEnvironment localEnvironment = new LocalEnvironment(globalEnvironment);
			WyalFile.VariableDeclaration[] vars = generateLoopInvariantParameterDeclarations(stmt, localEnvironment);
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
	private void checkLoopInvariant(String msg, Stmt.Loop stmt, Context context) {
		//
		Tuple<WhileyFile.Expr> loopInvariant = stmt.getInvariant();
		LocalEnvironment environment = context.getEnvironment();
		WhileyFile.Decl.FunctionOrMethod declaration = (WhileyFile.Decl.FunctionOrMethod) environment
				.getParent().enclosingDeclaration;
		// FIXME: this is completely broken in the case of multiple loops. The
		// problem is that we need to distinguish the macro names based on some
		// kind of block identifier.
		String prefix = declaration.getName() + "_loopinvariant_";
		// Construct argument to invocation
		Tuple<WhileyFile.Decl.Variable> localVariables = determineUsedVariables(stmt.getInvariant());
		Expr[] arguments = new Expr[localVariables.size()];
		for (int i = 0; i != arguments.length; ++i) {
			WhileyFile.Decl.Variable var = localVariables.get(i);
			arguments[i] = new Expr.VariableAccess(environment.read(var));
		}
		//
		for (int i = 0; i != loopInvariant.size(); ++i) {
			WhileyFile.Expr clause = loopInvariant.get(i);
			WyalFile.Name name = new WyalFile.Name(new WyalFile.Identifier(prefix + clause.getIndex()));
			Expr macroCall = new Expr.Invoke(null, name, null, arguments);
			context.emit(new VerificationCondition(msg, context.assumptions, macroCall,
					clause.getParent(WhileyFile.Attribute.Span.class)));
		}
	}

	private Context assumeLoopInvariant(Stmt.Loop stmt, Context context) {
		//
		Tuple<WhileyFile.Expr> loopInvariant = stmt.getInvariant();
		LocalEnvironment environment = context.getEnvironment();
		WhileyFile.Decl.FunctionOrMethod declaration = (WhileyFile.Decl.FunctionOrMethod) environment
				.getParent().enclosingDeclaration;
		// FIXME: this is completely broken in the case of multiple loops. The
		// problem is that we need to distinguish the macro names based on some
		// kind of block identifier.
		String prefix = declaration.getName() + "_loopinvariant_";
		// Construct argument to invocation
		Tuple<WhileyFile.Decl.Variable> localVariables = determineUsedVariables(stmt.getInvariant());
		Expr[] arguments = new Expr[localVariables.size()];
		for (int i = 0; i != arguments.length; ++i) {
			WhileyFile.Decl.Variable var = localVariables.get(i);
			arguments[i] = new Expr.VariableAccess(environment.read(var));
		}
		//
		for (int i = 0; i != loopInvariant.size(); ++i) {
			WhileyFile.Expr clause = loopInvariant.get(i);
			WyalFile.Name name = new WyalFile.Name(new WyalFile.Identifier(prefix + clause.getIndex()));
			Expr macroCall = new Expr.Invoke(null, name, null, arguments);
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
	private Context translateVariableDeclaration(WhileyFile.Decl.Variable stmt, Context context) {
		if (stmt.hasInitialiser()) {
			Pair<Expr, Context> p = translateExpressionWithChecks(stmt.getInitialiser(), null, context);
			context = p.second();
			generateTypeInvariantCheck(stmt.getType(),p.first(),context);
			context = context.write(stmt, p.first());
		}
		//
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
	public Pair<Expr[], Context> translateExpressionsWithChecks(Tuple<WhileyFile.Expr> exprs, Context context) {
		// Generate expression preconditions as verification conditions
		for (WhileyFile.Expr expr : exprs) {
			checkExpressionPreconditions(expr, context);
		}
		// Gather up any postconditions from function invocations.
		for (WhileyFile.Expr expr : exprs) {
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
	public Pair<Expr, Context> translateExpressionWithChecks(WhileyFile.Expr expr, Integer selector, Context context) {
		// Generate expression preconditions as verification conditions
		checkExpressionPreconditions(expr, context);
		// Gather up any postconditions from function invocations.
		context = assumeExpressionPostconditions(expr, context);
		// Translate expression in the normal fashion
		return new Pair<>(translateExpression(expr, selector, context.getEnvironment()), context);
	}

	@SuppressWarnings("unchecked")
	private void checkExpressionPreconditions(WhileyFile.Expr expr, Context context) {
		new PreconditionGenerator(this).apply(expr, context);
	}

	private Context assumeExpressionPostconditions(WhileyFile.Expr expr, Context context) {
		try {
			// First, propagate through all subexpressions
			for (int i = 0; i != expr.size(); ++i) {
				SyntacticItem operand = expr.get(i);
				if(operand instanceof WhileyFile.Expr) {
					context = assumeExpressionPostconditions((WhileyFile.Expr) operand, context);
				} else if(operand instanceof WhileyFile.Pair || operand instanceof WhileyFile.Tuple) {
					for (int j = 0; j != operand.size(); ++j) {
						SyntacticItem suboperand = operand.get(j);
						if(suboperand instanceof WhileyFile.Expr) {
							context = assumeExpressionPostconditions((WhileyFile.Expr) suboperand, context);
						}
					}
				}
			}
			switch (expr.getOpcode()) {
			case WhileyFile.EXPR_invoke:
				context = assumeInvokePostconditions((WhileyFile.Expr.Invoke) expr, context);
				break;
			}
			return context;
		} catch (InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			throw new InternalFailure(e.getMessage(), ((WhileyFile) expr.getHeap()).getEntry(), expr, e);
		}
	}

	private Context assumeInvokePostconditions(WhileyFile.Expr.Invoke expr, Context context) throws Exception {
		//WhileyFile.Declaration declaration = expr.getEnclosingTree().getEnclosingDeclaration();
		//
		WhileyFile.Decl.Callable fmp = lookupFunctionOrMethodOrProperty(expr.getName(), expr.getSignature(), expr);
		if(fmp instanceof WhileyFile.Decl.FunctionOrMethod) {
			WhileyFile.Decl.FunctionOrMethod fm = (WhileyFile.Decl.FunctionOrMethod) fmp;
			int numPostconditions = fm.getEnsures().size();
			//
			if (numPostconditions > 0) {
				// There is at least one postcondition for the function/method being
				// called. Therefore, we need to generate a verification condition
				// which will check that the precondition holds.
				//
				Type.Callable fmt = fm.getType();
				Expr[] parameters = translateExpressions(expr.getOperands(), context.getEnvironment());
				Expr[] arguments = java.util.Arrays.copyOf(parameters, parameters.length + fmt.getReturns().size());
				//
				for (int i = 0; i != fmt.getReturns().size(); ++i) {
					Integer selector = fmt.getReturns().size() > 1 ? i : null;
					arguments[parameters.length + i] = translateInvoke(expr, selector, context.getEnvironment());
				}
				//
				String prefix = fmp.getName() + "_ensures_";
				// Finally, generate an appropriate verification condition to check
				// each precondition clause
				for (int i = 0; i != numPostconditions; ++i) {
					// FIXME: name needs proper path information
					WyalFile.Name name = convert(fmp.getQualifiedName().toNameID().module(),prefix + i,expr);
					Expr clause = new Expr.Invoke(null, name, null, arguments);
					context = context.assume(clause);
				}
			}
		}
		//
		return context;
	}

	private WyalFile.Stmt.Block translateAsBlock(WhileyFile.Expr expr, LocalEnvironment environment) {
		WyalFile.Stmt stmt = translateAsStatement(expr, environment);
		return new WyalFile.Stmt.Block(stmt);
	}

	private WyalFile.Stmt translateAsStatement(WhileyFile.Expr expr, LocalEnvironment environment) {
		return translateExpression(expr, null, environment);
	}

	// =========================================================================
	// Expression
	// =========================================================================

	public Expr[] translateExpressions(Tuple<WhileyFile.Expr> loc, LocalEnvironment environment) {
		ArrayList<Expr> results = new ArrayList<>();
		for (int i = 0; i != loc.size(); ++i) {
			WhileyFile.Expr operand = loc.get(i);
			Tuple<Type> types = operand.getTypes();
			if (types == null) {
				results.add(translateExpression(loc.get(i), null, environment));
			} else {
				for (int j = 0; j != types.size(); ++j) {
					results.add(translateExpression(operand, j, environment));
				}
			}
		}
		return results.toArray(new Expr[results.size()]);
	}

	/**
	 * Transform a given bytecode location into its equivalent WyAL expression.
	 *
	 * @param location
	 *            The bytecode location to be translated
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Expr translateExpression(WhileyFile.Expr expr, Integer selector, LocalEnvironment environment) {
		Expr result;
		try {
			switch (expr.getOpcode()) {
			case WhileyFile.EXPR_constant:
				result = translateConstant((WhileyFile.Expr.Constant) expr, environment);
				break;
			case WhileyFile.EXPR_cast:
				result = translateConvert((WhileyFile.Expr.Cast) expr, environment);
				break;
			case WhileyFile.EXPR_recordaccess:
			case WhileyFile.EXPR_recordborrow:
				result = translateFieldLoad((WhileyFile.Expr.RecordAccess) expr, environment);
				break;
			case WhileyFile.EXPR_indirectinvoke:
				result = translateIndirectInvoke((WhileyFile.Expr.IndirectInvoke) expr, environment);
				break;
			case WhileyFile.EXPR_invoke:
				result = translateInvoke((WhileyFile.Expr.Invoke) expr, selector, environment);
				break;
			case WhileyFile.DECL_lambda:
				result = translateLambda((WhileyFile.Decl.Lambda) expr, environment);
				break;
			case WhileyFile.EXPR_lambdaaccess:
				result = translateLambda((WhileyFile.Expr.LambdaAccess) expr, environment);
				break;
			case WhileyFile.EXPR_logicalexistential:
			case WhileyFile.EXPR_logicaluniversal:
				result = translateQuantifier((WhileyFile.Expr.Quantifier) expr, environment);
				break;
			case WhileyFile.EXPR_variablemove:
			case WhileyFile.EXPR_variablecopy:
				result = translateVariableAccess((WhileyFile.Expr.VariableAccess) expr, environment);
				break;
			case WhileyFile.EXPR_staticvariable:
				result = translateStaticVariableAccess((WhileyFile.Expr.StaticVariableAccess) expr, environment);
				break;
			case WhileyFile.EXPR_logicalnot:
				result = translateNotOperator((WhileyFile.Expr.LogicalNot) expr, environment);
				break;
			case WhileyFile.EXPR_integernegation:
				result = translateArithmeticNegation((WhileyFile.Expr.IntegerNegation) expr, environment);
				break;
			case WhileyFile.EXPR_integeraddition:
			case WhileyFile.EXPR_integersubtraction:
			case WhileyFile.EXPR_integermultiplication:
			case WhileyFile.EXPR_integerdivision:
			case WhileyFile.EXPR_integerremainder:
			case WhileyFile.EXPR_equal:
			case WhileyFile.EXPR_notequal:
			case WhileyFile.EXPR_integerlessthan:
			case WhileyFile.EXPR_integerlessequal:
			case WhileyFile.EXPR_integergreaterthan:
			case WhileyFile.EXPR_integergreaterequal:
			case WhileyFile.EXPR_logiaclimplication:
			case WhileyFile.EXPR_logicaliff:
				result = translateBinaryOperator((WhileyFile.Expr.BinaryOperator) expr, environment);
				break;
			case WhileyFile.EXPR_logicaland:
			case WhileyFile.EXPR_logicalor:
				result = translateNaryOperator((WhileyFile.Expr.NaryOperator) expr, environment);
				break;
			case WhileyFile.EXPR_is:
				result = translateIs((WhileyFile.Expr.Is) expr, environment);
				break;
			case WhileyFile.EXPR_arrayaccess:
			case WhileyFile.EXPR_arrayborrow:
				result = translateArrayIndex((WhileyFile.Expr.ArrayAccess) expr, environment);
				break;
			case WhileyFile.EXPR_arrayinitialiser:
				result = translateArrayInitialiser((WhileyFile.Expr.ArrayInitialiser) expr, environment);
				break;
			case WhileyFile.EXPR_arraygenerator:
				result = translateArrayGenerator((WhileyFile.Expr.ArrayGenerator) expr, environment);
				break;
			case WhileyFile.EXPR_recordinitialiser:
				result = translateRecordInitialiser((WhileyFile.Expr.RecordInitialiser) expr, environment);
				break;
			case WhileyFile.EXPR_arraylength:
				result = translateArrayLength((WhileyFile.Expr.ArrayLength) expr, environment);
				break;
			case WhileyFile.EXPR_dereference:
				result = translateDereference((WhileyFile.Expr.Dereference) expr, environment);
				break;
			case WhileyFile.EXPR_bitwiseshr:
			case WhileyFile.EXPR_bitwiseshl:
			case WhileyFile.EXPR_bitwiseand:
			case WhileyFile.EXPR_bitwiseor:
			case WhileyFile.EXPR_bitwisexor:
			case WhileyFile.EXPR_bitwisenot:
			case WhileyFile.EXPR_new:
				result = translateAsUnknown(expr, environment);
				break;
			default:
				throw new RuntimeException("Deadcode reached (" + expr.getClass().getName() +")");
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (Throwable e) {
			throw new InternalFailure(e.getMessage(), ((WhileyFile) expr.getHeap()).getEntry(), expr, e);
		}
		return allocate(result,expr.getParent(WhileyFile.Attribute.Span.class));
	}

	private Expr translateConstant(WhileyFile.Expr.Constant expr, LocalEnvironment environment) {
		WhileyFile.Value value = expr.getValue();
		if(value instanceof WhileyFile.Value.UTF8) {
			// FIXME: yes, this is a bit of a hack. The basic problem is that WyAL doesn't
			// really handle UTF8 constants properly, even though it does know about them.
			byte[] bytes = ((WhileyFile.Value.UTF8) value).get();
			Expr[] es = new Expr[bytes.length];
			for(int i=0;i!=bytes.length;++i) {
				WyalFile.Value.Int bv = new WyalFile.Value.Int(bytes[i]);
				es[i] = new WyalFile.Expr.Constant(bv);
			}
			return new WyalFile.Expr.ArrayInitialiser(es);
		} else if(value instanceof WhileyFile.Value.Byte) {
			byte b = ((WhileyFile.Value.Byte) value).get();
			WyalFile.Value.Int bv = new WyalFile.Value.Int(b);
			return new WyalFile.Expr.Constant(bv);
		} else {
			return new WyalFile.Expr.Constant(expr.getValue());
		}
	}

	private Expr translateConvert(WhileyFile.Expr.Cast expr, LocalEnvironment environment) {
		// TODO: check whether need to do any more here
		return translateExpression(expr.getOperand(), null, environment);
	}

	private Expr translateFieldLoad(WhileyFile.Expr.RecordAccess expr, LocalEnvironment environment) {
		// Now, translate source expression
		Expr src = translateExpression(expr.getOperand(), null, environment);
		// Generate field name identifier
		WyalFile.Identifier field = new WyalFile.Identifier(expr.getField().toString());
		// Done
		return new Expr.RecordAccess(src, field);
	}

	private Expr translateIndirectInvoke(WhileyFile.Expr.IndirectInvoke expr, LocalEnvironment environment) {
		// FIXME: need to implement this
		return translateAsUnknown(expr, environment);
	}

	public Expr translateInvoke(WhileyFile.Expr.Invoke expr, Integer selector, LocalEnvironment environment) {
		Expr[] operands = translateExpressions(expr.getOperands(), environment);
		// FIXME: name needs proper path information
		return new Expr.Invoke(null, expr.getName(), selector, operands);
	}

	private Expr translateLambda(WhileyFile.Decl.Lambda expr, LocalEnvironment environment) {
		// FIXME: need to implement this
		return translateAsUnknown(expr, environment);
	}

	private Expr translateLambda(WhileyFile.Expr.LambdaAccess expr, LocalEnvironment environment) {
		// FIXME: need to implement this
		return translateAsUnknown(expr, environment);
	}

	private Expr translateNotOperator(WhileyFile.Expr.LogicalNot expr, LocalEnvironment environment) {
		Expr e = translateExpression(expr.getOperand(), null, environment);
		return invertCondition(e, expr.getOperand());
	}

	private Expr translateArithmeticNegation(WhileyFile.Expr.IntegerNegation expr, LocalEnvironment environment) {
		Expr e = translateExpression(expr.getOperand(), null, environment);
		return new Expr.Negation(e);
	}

	private Expr translateBinaryOperator(WhileyFile.Expr.BinaryOperator expr, LocalEnvironment environment) {
		Expr lhs = translateExpression(expr.getFirstOperand(), null, environment);
		Expr rhs = translateExpression(expr.getSecondOperand(), null, environment);
		// FIXME: problem with > 2 operands
		switch(expr.getOpcode()) {
		case WhileyFile.EXPR_integeraddition:
			return new Expr.Addition(lhs, rhs);
		case WhileyFile.EXPR_integersubtraction:
			return new Expr.Subtraction(lhs, rhs);
		case WhileyFile.EXPR_integermultiplication:
			return new Expr.Multiplication(lhs, rhs);
		case WhileyFile.EXPR_integerdivision:
			return new Expr.Division(lhs, rhs);
		case WhileyFile.EXPR_integerremainder:
			return new Expr.Remainder(lhs, rhs);
		case WhileyFile.EXPR_equal:
			return new Expr.Equal(lhs, rhs);
		case WhileyFile.EXPR_notequal:
			return new Expr.NotEqual(lhs, rhs);
		case WhileyFile.EXPR_integerlessthan:
			return new Expr.LessThan(lhs, rhs);
		case WhileyFile.EXPR_integerlessequal:
			return new Expr.LessThanOrEqual(lhs, rhs);
		case WhileyFile.EXPR_integergreaterthan:
			return new Expr.GreaterThan(lhs, rhs);
		case WhileyFile.EXPR_integergreaterequal:
			return new Expr.GreaterThanOrEqual(lhs, rhs);
		case WhileyFile.EXPR_logiaclimplication:
			return new Expr.LogicalImplication(lhs, rhs);
		case WhileyFile.EXPR_logicaliff:
			return new Expr.LogicalIff(lhs, rhs);
		default:
			throw new RuntimeException("Internal failure --- dead code reached");
		}

	}


	private Expr translateNaryOperator(WhileyFile.Expr.NaryOperator expr, LocalEnvironment environment) {
		Expr[] operands = translateExpressions(expr.getOperands(),environment);
		switch(expr.getOpcode()) {
		case WhileyFile.EXPR_logicaland:
			return new Expr.LogicalAnd(operands);
		case WhileyFile.EXPR_logicalor:
			return new Expr.LogicalOr(operands);
		default:
			throw new RuntimeException("Internal failure --- dead code reached");
		}

	}

	private Expr translateIs(WhileyFile.Expr.Is expr, LocalEnvironment environment) {
		Expr lhs = translateExpression(expr.getOperand(), null, environment);
		WyalFile.Type typeTest = convert(expr.getTestType(), environment.getParent().enclosingDeclaration);
		return new Expr.Is(lhs, typeTest);
	}

	private Expr translateArrayIndex(WhileyFile.Expr.ArrayAccess expr, LocalEnvironment environment) {
		Expr lhs = translateExpression(expr.getFirstOperand(), null, environment);
		Expr rhs = translateExpression(expr.getSecondOperand(), null, environment);
		return new Expr.ArrayAccess(lhs, rhs);
	}

	private Expr translateArrayGenerator(WhileyFile.Expr.ArrayGenerator expr, LocalEnvironment environment) {
		Expr element = translateExpression(expr.getFirstOperand(), null, environment);
		Expr count = translateExpression(expr.getSecondOperand(), null, environment);

		// FIXME: this needs to be put back somehow
		// environment = environment.write(expr.getIndex());

		return new Expr.ArrayGenerator(element, count);
	}

	private Expr translateArrayInitialiser(WhileyFile.Expr.ArrayInitialiser expr, LocalEnvironment environment) {
		Tuple<WhileyFile.Expr> operands = expr.getOperands();
		Expr[] vals = new Expr[operands.size()];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = translateExpression(operands.get(i), null, environment);
		}
		return new Expr.ArrayInitialiser(vals);
	}

	private Expr translateArrayLength(WhileyFile.Expr.ArrayLength expr, LocalEnvironment environment) {
		Expr e = translateExpression(expr.getOperand(), null, environment);
		return new Expr.ArrayLength(e);
	}

	private Expr translateDereference(WhileyFile.Expr.Dereference expr, LocalEnvironment environment) {
		Expr e = translateExpression(expr.getOperand(), null, environment);
		return new Expr.Dereference(e);
	}

	private Expr translateQuantifier(WhileyFile.Expr.Quantifier expr, LocalEnvironment environment) {
		// Determine the type and names of each quantified variable.
		WyalFile.VariableDeclaration[] pattern = generateQuantifierTypePattern(expr, environment);
		// Apply quantifier ranges
		Expr ranges = generateQuantifierRanges(expr, environment);
		// Generate quantifier body
		Expr body = translateExpression(expr.getOperand(), null, environment);
		// Generate quantifier expression
		if(expr instanceof WhileyFile.Expr.UniversalQuantifier) {
			body = new Expr.LogicalImplication(ranges, body);
			return new Expr.UniversalQuantifier(pattern, body);
		} else {
			body = new Expr.LogicalAnd(ranges, body);
			return new Expr.ExistentialQuantifier(pattern, body);
		}
	}

	private Expr translateRecordInitialiser(WhileyFile.Expr.RecordInitialiser expr, LocalEnvironment environment)  {
		Tuple<WhileyFile.Identifier> fields = expr.getFields();
		Tuple<WhileyFile.Expr> operands = expr.getOperands();
		WyalFile.Pair<WyalFile.Identifier, Expr>[] pairs = new WyalFile.Pair[fields.size()];
		//
		for (int i = 0; i != fields.size(); ++i) {
			Identifier field = new WyalFile.Identifier(fields.get(i).get());
			Expr init = translateExpression(operands.get(i), null, environment);
			pairs[i] = new WyalFile.Pair<>(field, init);
		}
		return new Expr.RecordInitialiser(pairs);
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
	private Expr translateAsUnknown(WhileyFile.Expr expr, LocalEnvironment environment) {
		// What we're doing here is creating a completely fresh variable to
		// represent the return value. This is basically saying the return value
		// could be anything, and we don't care what.
		String name = "r" + Integer.toString(expr.getIndex());
		WyalFile.Type type = convert(expr.getType(), expr);
		WyalFile.VariableDeclaration vf = allocate(
				new WyalFile.VariableDeclaration(type, new WyalFile.Identifier(name)), null);
		 //environment = environment.write(expr.getIndex());
		 //WyalFile.VariableDeclaration r = environment.read(expr.getIndex());
		 return new Expr.VariableAccess(vf);
		 //throw new IllegalArgumentException("Implement me");
	}

	/**
	 * Generate a type pattern representing the type and name of all quantifier
	 * variables described by this quantifier.
	 *
	 * @param expr
	 * @return
	 */
	private WyalFile.VariableDeclaration[] generateQuantifierTypePattern(WhileyFile.Expr.Quantifier expr,
			LocalEnvironment environment) {
		//
		Tuple<WhileyFile.Decl.Variable> params = expr.getParameters();
		WyalFile.VariableDeclaration[] vardecls = new WyalFile.VariableDeclaration[params.size()];
		for (int i = 0; i != params.size(); ++i) {
			WhileyFile.Decl.Variable var = params.get(i);
			vardecls[i] = environment.read(var);
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
	private Expr generateQuantifierRanges(WhileyFile.Expr.Quantifier expr, LocalEnvironment environment) {
		Expr ranges = null;
		Tuple<WhileyFile.Decl.Variable> parameters = expr.getParameters();
		for (int i = 0; i != parameters.size(); ++i) {
			WhileyFile.Decl.Variable var = parameters.get(i);
			WhileyFile.Expr.ArrayRange range = (WhileyFile.Expr.ArrayRange) var.getInitialiser();
			WyalFile.VariableDeclaration varDecl = environment.read(var);
			Expr.VariableAccess varExpr = new Expr.VariableAccess(varDecl);
			Expr startExpr = translateExpression(range.getFirstOperand(), null, environment);
			Expr endExpr = translateExpression(range.getSecondOperand(), null, environment);
			Expr lhs = new Expr.LessThanOrEqual(startExpr, varExpr);
			Expr rhs = new Expr.LessThan(varExpr, endExpr);
			ranges = and(ranges, and(lhs, rhs));
		}
		return ranges;
	}

	private Expr translateVariableAccess(WhileyFile.Expr.VariableAccess expr, LocalEnvironment environment) {
		WhileyFile.Decl.Variable decl = expr.getVariableDeclaration();
		WyalFile.VariableDeclaration var = environment.read(decl);
		return new Expr.VariableAccess(var);
	}

	private Expr translateStaticVariableAccess(WhileyFile.Expr.StaticVariableAccess expr, LocalEnvironment environment) {
		try {
			// FIXME: yes, this is a hack to temporarily handle the transition from
			// constants to static variables.
			WhileyFile.Decl.StaticVariable decl = resolver.resolveExactly(expr.getName(), WhileyFile.Decl.StaticVariable.class);
			return translateExpression(decl.getInitialiser(), null, environment);
		} catch (ResolutionError e) {
			throw new RuntimeException(e);
		}
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
			return new Expr.LogicalAnd(lhs, rhs);
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

		for (Map.Entry<WhileyFile.Decl.Variable, WyalFile.VariableDeclaration> e : updated.locals.entrySet()) {
			WhileyFile.Decl.Variable var = e.getKey();
			WyalFile.VariableDeclaration newVarVersionedName = e.getValue();
			WyalFile.VariableDeclaration oldVarVersionedName = original.read(var);
			if (!oldVarVersionedName.equals(newVarVersionedName)) {
				// indicates a version change of the given variable.
				Expr.VariableAccess oldVar = new Expr.VariableAccess(oldVarVersionedName);
				Expr.VariableAccess newVar = new Expr.VariableAccess(newVarVersionedName);
				assumptions = assumptions.add(new Expr.Equal(newVar, oldVar));
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
		HashSet<WhileyFile.Decl.Variable> modified = new HashSet<>();
		HashSet<WhileyFile.Decl.Variable> deleted = new HashSet<>();
		Map<WhileyFile.Decl.Variable, WyalFile.VariableDeclaration> headLocals = head.environment.locals;

		// Compute the modified and deleted sets
		for (int i = 1; i < contexts.length; ++i) {
			Context ithContext = contexts[i];
			Map<WhileyFile.Decl.Variable, WyalFile.VariableDeclaration> ithLocals = ithContext.environment.locals;
			// First check env against head
			for (Map.Entry<WhileyFile.Decl.Variable, WyalFile.VariableDeclaration> e : ithLocals.entrySet()) {
				WhileyFile.Decl.Variable key = e.getKey();
				WyalFile.VariableDeclaration s1 = e.getValue();
				WyalFile.VariableDeclaration s2 = headLocals.get(key);
				if (s1 == null) {
					deleted.add(key);
				} else if (!s1.equals(s2)) {
					modified.add(key);
				}
			}
			// Second, check head against env
			for (Map.Entry<WhileyFile.Decl.Variable, WyalFile.VariableDeclaration> e : headLocals.entrySet()) {
				WhileyFile.Decl.Variable key = e.getKey();
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
		IdentityHashMap<WhileyFile.Decl.Variable, WyalFile.VariableDeclaration> combinedLocals = new IdentityHashMap<>();

		for (Map.Entry<WhileyFile.Decl.Variable, WyalFile.VariableDeclaration> e : headLocals.entrySet()) {
			WhileyFile.Decl.Variable key = e.getKey();
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
	private void createFunctionOrMethodPrototype(WhileyFile.Decl.FunctionOrMethod declaration) {
		Tuple<WhileyFile.Decl.Variable> params = declaration.getParameters();
		Tuple<WhileyFile.Decl.Variable> returns = declaration.getReturns();
		//
		WyalFile.VariableDeclaration[] parameters = new WyalFile.VariableDeclaration[params.size()];
		// second, set initial environment
		for (int i = 0; i != params.size(); ++i) {
			WhileyFile.Decl.Variable var = params.get(i);
			WyalFile.Type parameterType = convert(var.getType(), declaration);
			WyalFile.Identifier parameterName = new WyalFile.Identifier(var.getName().get());
			parameters[i] = new WyalFile.VariableDeclaration(parameterType, parameterName);
		}
		WyalFile.VariableDeclaration[] wyalReturns = new WyalFile.VariableDeclaration[returns.size()];
		// second, set initial environment
		for (int i = 0; i != returns.size(); ++i) {
			WhileyFile.Decl.Variable var = returns.get(i);
			WyalFile.Type returnType = convert(var.getType(), declaration);
			WyalFile.Identifier returnName = new WyalFile.Identifier(var.getName().get());
			wyalReturns[i] = new WyalFile.VariableDeclaration(returnType, returnName);
		}
		//
		WyalFile.Identifier name = new WyalFile.Identifier(declaration.getName().get());
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
	private void createAssertions(WhileyFile.Decl declaration, List<VerificationCondition> vcs,
			GlobalEnvironment environment) {
		// FIXME: should be logged somehow?
		for (int i = 0; i != vcs.size(); ++i) {
			VerificationCondition vc = vcs.get(i);
			// Build the actual verification condition
			WyalFile.Stmt.Block verificationCondition = buildVerificationCondition(declaration, environment, vc);
			// Add generated verification condition as assertion
			WyalFile.Declaration.Assert assrt = new WyalFile.Declaration.Assert(verificationCondition, vc.description);
			allocate(assrt,vc.getSpan());
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
	public WyalFile.Stmt.Block buildVerificationCondition(WhileyFile.Decl declaration,
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
			verificationCondition = new WyalFile.Stmt.UniversalQuantifier(parameters, qfBody);
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
		for (WyalFile.VariableDeclaration var : freeVariables) {
			WyalFile.VariableDeclaration parent = environment.getParent(var);
			if (parent != null) {
				// This indicates a variable alias, so construct the necessary
				// equality.
				Expr.VariableAccess lhs = new Expr.VariableAccess(var);
				Expr.VariableAccess rhs = new Expr.VariableAccess(parent);
				Expr aliasEquality = new Expr.Equal(lhs, rhs);
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
	private WyalFile.VariableDeclaration[] generateExpressionTypePattern(WhileyFile.Decl
			declaration, GlobalEnvironment environment,
			Set<WyalFile.VariableDeclaration> freeVariables) {
		WyalFile.VariableDeclaration[] patterns = new WyalFile.VariableDeclaration[freeVariables.size()];
		int index = 0;
		for (WyalFile.VariableDeclaration var : freeVariables) {
			patterns[index++] = var;
		}
		return patterns;
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
	private WyalFile.VariableDeclaration[] generatePreconditionParameters(WhileyFile.Decl.Callable declaration,
			LocalEnvironment environment) {
		Tuple<WhileyFile.Decl.Variable> params = declaration.getParameters();
		WyalFile.VariableDeclaration[] vars = new WyalFile.VariableDeclaration[params.size()];
		// second, set initial environment
		for (int i = 0; i != params.size(); ++i) {
			WhileyFile.Decl.Variable var = params.get(i);
			vars[i] = environment.read(var);
		}
		return vars;
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
	private WyalFile.VariableDeclaration[] generatePostconditionTypePattern(
			WhileyFile.Decl.FunctionOrMethod declaration, LocalEnvironment environment) {
		Tuple<Decl.Variable> params = declaration.getParameters();
		Tuple<Decl.Variable> returns = declaration.getReturns();
		WyalFile.VariableDeclaration[] vars = new WyalFile.VariableDeclaration[params.size() + returns.size()];
		// second, set initial environment
		for (int i = 0; i != params.size(); ++i) {
			WhileyFile.Decl.Variable var = params.get(i);
			vars[i] = environment.read(var);
		}
		for (int i = 0; i != returns.size(); ++i) {
			WhileyFile.Decl.Variable var = returns.get(i);
			vars[i + params.size()] = environment.read(var);
		}
		//
		return vars;
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
	private WyalFile.VariableDeclaration[] generateLoopInvariantParameterDeclarations(Stmt.Loop loop,
			LocalEnvironment environment) {
		// Extract all used variables within the loop invariant. This is necessary to
		// determine what parameters are required for the loop invariant macros.
		Tuple<Decl.Variable> modified = determineUsedVariables(loop.getInvariant());
		WyalFile.VariableDeclaration[] vars = new WyalFile.VariableDeclaration[modified.size()];
		// second, set initial environment
		for (int i = 0; i != modified.size(); ++i) {
			WhileyFile.Decl.Variable var = modified.get(i);
			vars[i] = environment.read(var);
		}
		return vars;
	}

	/**
	 * Create a simple visitor for extracting all variable access expressions from a
	 * given expression (or statement).
	 */
	private static final AbstractConsumer<HashSet<Decl.Variable>> usedVariableExtractor = new AbstractConsumer<HashSet<Decl.Variable>>() {
		@Override
		public void visitVariableAccess(WhileyFile.Expr.VariableAccess expr, HashSet<Decl.Variable> used) {
			used.add(expr.getVariableDeclaration());
		}
		@Override
		public void visitUniversalQuantifier(WhileyFile.Expr.UniversalQuantifier expr, HashSet<Decl.Variable> used) {
			visitVariables(expr.getParameters(), used);
			visitExpression(expr.getOperand(), used);
			removeAllDeclared(expr.getParameters(),used);
		}
		@Override
		public void visitExistentialQuantifier(WhileyFile.Expr.ExistentialQuantifier expr, HashSet<Decl.Variable> used) {
			visitVariables(expr.getParameters(), used);
			visitExpression(expr.getOperand(), used);
			removeAllDeclared(expr.getParameters(),used);
		}
		@Override
		public void visitType(WhileyFile.Type type, HashSet<Decl.Variable> used) {
			// No need to visit types
		}

		private void removeAllDeclared(Tuple<Decl.Variable> parameters, HashSet<Decl.Variable> used) {
			for (int i = 0; i != parameters.size(); ++i) {
				used.remove(parameters.get(i));
			}
		}
	};

	/**
	 * Determine the set of used variables in a given set of expressions. A used
	 * variable is one referred to by a VariableAccess expression.
	 *
	 * @param expression
	 * @return
	 */
	public Tuple<Decl.Variable> determineUsedVariables(Tuple<WhileyFile.Expr> exprs) {
		HashSet<Decl.Variable> used = new HashSet<>();
		usedVariableExtractor.visitExpressions(exprs, used);
		return new Tuple<>(used);
	}

	/**
	 * Convert a Name identifier from a WyIL into one suitable for a WyAL file.
	 *
	 * @param id
	 * @return
	 */
	public WyalFile.Name convert(NameID id, SyntacticItem context) {
		return convert(id.module(), id.name(), context);
	}

	public WyalFile.Name convert(Path.ID module, String name, SyntacticItem context) {
		if(module.equals(wyalFile.getEntry().id())) {
			// This is a local name. Therefore, it does not need to be fully
			// qualified.
			module = Trie.ROOT;
		}
		WyalFile.Identifier[] components = new WyalFile.Identifier[module.size()+1];
		for(int i=0;i!=module.size();++i) {
			WyalFile.Identifier id = new WyalFile.Identifier(module.get(i));
			components[i] = id;
		}
		WyalFile.Identifier id = new WyalFile.Identifier(name);
		components[module.size()] = id;
		WyalFile.Name n = new WyalFile.Name(components);
		return allocate(n,context.getParent(WhileyFile.Attribute.Span.class));
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
	public WyalFile.Type convert(WhileyFile.Type type, SyntacticItem context) {
		// FIXME: this is fundamentally broken in the case of recursive types.
		// See Issue #298.
		WyalFile.Type result;
		if (type instanceof Type.Void) {
			result = new WyalFile.Type.Void();
		} else if (type instanceof Type.Null) {
			result = new WyalFile.Type.Null();
		} else if (type instanceof Type.Bool) {
			result = new WyalFile.Type.Bool();
		} else if (type instanceof Type.Byte) {
			// FIXME: implement WyalFile.Type.Byte
			// return new WyalFile.Type.Byte(attributes(branch);
			result = new WyalFile.Type.Int();
		} else if (type instanceof Type.Int) {
			result = new WyalFile.Type.Int();
		} else if (type instanceof Type.Array) {
			Type.Array lt = (Type.Array) type;
			WyalFile.Type elem = convert(lt.getElement(), context);
			result = new WyalFile.Type.Array(elem);
		} else if (type instanceof Type.Record) {
			Type.Record rt = (Type.Record) type;
			Tuple<Type.Field> fields = rt.getFields();
			WyalFile.FieldDeclaration[] elements = new WyalFile.FieldDeclaration[fields.size()];
			for (int i = 0; i != elements.length; ++i) {
				Type.Field field = fields.get(i);
				WyalFile.Type fieldType = convert(field.getType(), context);
				elements[i] = new WyalFile.FieldDeclaration(fieldType, new WyalFile.Identifier(field.getName().get()));
			}
			result = new WyalFile.Type.Record(rt.isOpen(),elements);
		} else if (type instanceof Type.Reference) {
			Type.Reference lt = (Type.Reference) type;
			WyalFile.Type elem = convert(lt.getElement(), context);
			String lifetime = lt.hasLifetime() ? lt.getLifetime().get() : "*";
			result = new WyalFile.Type.Reference(elem, new WyalFile.Identifier(lifetime));
		} else if (type instanceof Type.Union) {
			Type.Union tu = (Type.Union) type;
			WyalFile.Type[] elements = new WyalFile.Type[tu.size()];
			for (int i = 0; i != tu.size(); ++i) {
				elements[i] = convert(tu.get(i), context);
			}
			result = new WyalFile.Type.Union(elements);
		} else if (type instanceof Type.Callable) {
			Type.Callable ft = (Type.Callable) type;
			// FIXME: need to do something better here
			result = new WyalFile.Type.Any();
		} else if (type instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.getName().toNameID();
			result = new WyalFile.Type.Nominal(convert(nid,type));
		} else {
			throw new InternalFailure("unknown type encountered (" + type.getClass().getName() + ")",
					((WhileyFile) type.getHeap()).getEntry(), context);
		}
		//
		result = allocate(result,context.getParent(WhileyFile.Attribute.Span.class));
		//
		return result;
	}

	/**
	 * Perform a simple check to see whether or not a given type may have an
	 * invariant, or not.
	 *
	 * @param type
	 * @return
	 */
	private static boolean typeMayHaveInvariant(Type type, Context context) {
		if (type instanceof Type.Void) {
			return false;
		} else if (type instanceof Type.Null) {
			return false;
		} else if (type instanceof Type.Bool) {
			return false;
		} else if (type instanceof Type.Byte) {
			return false;
		} else if (type instanceof Type.Int) {
			return false;
		} else if (type instanceof Type.Array) {
			Type.Array lt = (Type.Array) type;
			return typeMayHaveInvariant(lt.getElement(), context);
		} else if (type instanceof Type.Record) {
			Type.Record rt = (Type.Record) type;
			Tuple<Type.Field> fields = rt.getFields();
			for (int i = 0; i != fields.size(); ++i) {
				Type.Field field = fields.get(i);
				if (typeMayHaveInvariant(field.getType(), context)) {
					return true;
				}
			}
			return false;
		} else if (type instanceof Type.Reference) {
			Type.Reference lt = (Type.Reference) type;
			return typeMayHaveInvariant(lt.getElement(), context);
		} else if (type instanceof Type.Union) {
			Type.Union t = (Type.Union) type;
			for(int i=0;i!=t.size();++i) {
				if(typeMayHaveInvariant(t.get(i), context)) {
					return true;
				}
			}
			return false;
		} else if (type instanceof Type.Callable) {
			Type.Callable ft = (Type.Callable) type;
			return typeMayHaveInvariant(ft.getParameters(), context) || typeMayHaveInvariant(ft.getReturns(), context);
		} else if (type instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.getName().toNameID();
			// HACK
			return true;
		} else {
			throw new RuntimeException("Unknown type encountered");
		}
	}

	private static boolean typeMayHaveInvariant(Tuple<Type> types, Context context) {
		for (int i = 0; i != types.size(); ++i) {
			if(typeMayHaveInvariant(types.get(i),context)) {
				return true;
			}
		}
		return false;
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
			for (WyalFile.VariableDeclaration vd : q.getParameters()) {
				freeVars.remove(vd);
			}
		} else {
			for(int i=0;i!=e.size();++i) {
				SyntacticItem item = e.get(i);
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
	public WhileyFile.Decl.Callable lookupFunctionOrMethodOrProperty(WhileyFile.Name name, Type.Callable signature,
			WhileyFile.Stmt stmt) throws NameResolver.ResolutionError {
		//
		for (WhileyFile.Decl.Callable decl : resolver.resolveAll(name, WhileyFile.Decl.Callable.class)) {
			if (decl.getType().equals(signature)) {
				return decl;
			}
		}
		return null;
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
	public Expr invertCondition(Expr expr, WhileyFile.Expr elem) {
		if (expr instanceof Expr.Operator) {
			Expr.Operator binTest = (Expr.Operator) expr;
			switch (binTest.getOpcode()) {
			case WyalFile.EXPR_eq:
				return new Expr.NotEqual(binTest.getAll());
			case WyalFile.EXPR_neq:
				return new Expr.Equal(binTest.getAll());
			case WyalFile.EXPR_gteq:
				return new Expr.LessThan(binTest.getAll());
			case WyalFile.EXPR_gt:
				return new Expr.LessThanOrEqual(binTest.getAll());
			case WyalFile.EXPR_lteq:
				return new Expr.GreaterThan(binTest.getAll());
			case WyalFile.EXPR_lt:
				return new Expr.GreaterThanOrEqual(binTest.getAll());
			case WyalFile.EXPR_and: {
				Expr[] operands = invertConditions(binTest.getAll(), elem);
				return new Expr.LogicalOr(operands);
			}
			case WyalFile.EXPR_or: {
				Expr[] operands = invertConditions(binTest.getAll(), elem);
				return new Expr.LogicalAnd(operands);
			}
			}
		} else if (expr instanceof Expr.Is) {
			Expr.Is ei = (Expr.Is) expr;
			WyalFile.Type type = ei.getTestType();
			return new Expr.Is(ei.getTestExpr(), new WyalFile.Type.Negation(type));
		}
		// Otherwise, compare against false
		// FIXME: this is just wierd and needs to be fixed.
		return new Expr.LogicalNot(expr);
	}

	public Expr[] invertConditions(Expr[] expr, WhileyFile.Expr elem) {
		Expr[] rs = new Expr[expr.length];
		for (int i = 0; i != expr.length; ++i) {
			rs[i] = invertCondition(expr[i], elem);
		}
		return rs;
	}

	private WhileyFile.Attribute.Span getSpan(SyntacticItem item) {
		WhileyFile.Attribute.Span span = null;
		if (item.getHeap() != null) {
			span = item.getParent(WhileyFile.Attribute.Span.class);
		}
		if (span == null) {
			Attribute.Source source = item.attribute(Attribute.Source.class);
			if (source != null) {
				span = new WhileyFile.Attribute.Span(null, source.start, source.end);
			}
		}
		return span;
	}

	private <T extends SyntacticItem> T allocate(T item, WhileyFile.Attribute.Span span) {
		item = wyalFile.allocate(item);
		if(span != null) {
			int start = span.getStart().get().intValue();
			int end = span.getEnd().get().intValue();
			item.attributes().add(new Attribute.Source(start, end, 0));
		}
		return item;
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
	public static class VerificationCondition extends SyntacticElement.Impl {
		private final String description;
		private final AssumptionSet antecedent;
		private final Expr consequent;
		private final WhileyFile.Attribute.Span span;

		public VerificationCondition(String description, AssumptionSet antecedent, Expr consequent, WhileyFile.Attribute.Span span) {
			this.description = description;
			this.antecedent = antecedent;
			this.consequent = consequent;
			this.span = span;
		}

		public WhileyFile.Attribute.Span getSpan() {
			return span;
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
		private final WhileyFile.Decl enclosingDeclaration;

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

		public GlobalEnvironment(WhileyFile.Decl enclosingDeclaration) {
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
		public WyalFile.VariableDeclaration getParent(WyalFile.VariableDeclaration alias) {
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
		public WyalFile.VariableDeclaration allocateVersion(WhileyFile.Decl.Variable var) {
			String name = var.getName().get();
			WyalFile.Type type = convert(var.getType(), var);
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
			allocation.put(versionedVar, var.getIndex());
			//
			// The following is necessary to ensure that the alias structure of
			// VariableDeclarations is properly preserved.
			return allocate(new WyalFile.VariableDeclaration(type, new WyalFile.Identifier(versionedVar)), null);
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
	public class LocalEnvironment {
		/**
		 * Provides access to the global environment
		 */
		private final GlobalEnvironment global;

		/**
		 * Maps all local variables in scope to their current versioned variable
		 * names
		 */
		private final Map<WhileyFile.Decl.Variable, WyalFile.VariableDeclaration> locals;

		public LocalEnvironment(GlobalEnvironment global) {
			this.global = global;
			this.locals = new IdentityHashMap<>();
		}

		public LocalEnvironment(GlobalEnvironment global, Map<WhileyFile.Decl.Variable, WyalFile.VariableDeclaration> locals) {
			this.global = global;
			this.locals = new IdentityHashMap<>(locals);
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
		public WyalFile.VariableDeclaration read(WhileyFile.Decl.Variable var) {
			WyalFile.VariableDeclaration vv = locals.get(var);
			if (vv == null) {
				vv = global.allocateVersion(var);
				locals.put(var, vv);
			}
			return vv;
		}

		/**
		 * Create a new version for each variable in a sequence of variables.
		 * This create a completely new local environment.
		 *
		 * @param index
		 */
		public LocalEnvironment write(Tuple<WhileyFile.Decl.Variable> vars) {
			LocalEnvironment nenv = new LocalEnvironment(global, locals);
			for (int i = 0; i != vars.size(); ++i) {
				nenv.locals.put(vars.get(i), global.allocateVersion(vars.get(i)));
			}
			return nenv;
		}

		public LocalEnvironment write(WhileyFile.Decl.Variable var) {
			LocalEnvironment nenv = new LocalEnvironment(global, locals);
			nenv.locals.put(var, global.allocateVersion(var));
			return nenv;
		}

		@Override
		public LocalEnvironment clone() {
			return new LocalEnvironment(global, locals);
		}
	}

	public WhileyFile.Decl.Variable getVariableDeclaration(WhileyFile.Expr.VariableAccess access) {
		return access.getVariableDeclaration();
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
	public static class Context {
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

		public WhileyFile getEnclosingFile() {
			return (WhileyFile) environment.getParent().enclosingDeclaration.getHeap();
		}

		public AssumptionSet getAssumptions() {
			return assumptions;
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
		public Context write(WhileyFile.Decl.Variable lhs, Expr rhs) {
			// Update version number of the assigned variable
			LocalEnvironment nEnvironment = environment.write(lhs);
			WyalFile.VariableDeclaration nVersionedVar = nEnvironment.read(lhs);
			// Update assumption sets to reflect the "assigment"
			Expr.VariableAccess var = new Expr.VariableAccess(nVersionedVar);
			Expr condition = new Expr.Equal(var, rhs);
			AssumptionSet nAssumptions = assumptions.add(condition);
			//
			return new Context(wyalFile, nAssumptions, nEnvironment, initialEnvironment, enclosingLoop,
					verificationConditions);
		}

		public WyalFile.VariableDeclaration read(WhileyFile.Decl.Variable expr) {
			return environment.read(expr);
		}

		public WyalFile.VariableDeclaration readFirst(WhileyFile.Decl.Variable expr) {
			return initialEnvironment.read(expr);
		}

		public Context havoc(WhileyFile.Decl.Variable lhs) {
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
		public Context havoc(Tuple<WhileyFile.Decl.Variable> vars) {
			LocalEnvironment nEnvironment = environment.write(vars);
			// done
			return new Context(wyalFile, assumptions, nEnvironment, initialEnvironment, enclosingLoop,
					verificationConditions);
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
}
