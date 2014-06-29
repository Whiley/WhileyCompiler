package wycs.io;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static wycc.lang.SyntaxError.internalFailure;
import static wycs.io.NewWyalFileLexer.Token.Kind.*;
import wycs.core.Value;
import wycs.io.NewWyalFileLexer.Token;
import wycs.syntax.*;
import wycc.lang.SyntaxError;
import wycc.lang.SyntacticElement;
import wycc.lang.Attribute;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyfs.util.Trie;

public class NewWyalFileParser {
	private String filename;
	private ArrayList<Token> tokens;
	private int index;
	
	public NewWyalFileParser(String filename, List<Token> tokens) {
		this.filename = filename;
		this.tokens = new ArrayList<Token>(tokens);
	}

	/**
	 * Read a <code>WyalFile</code> from the token stream. If the stream is
	 * invalid in some way (e.g. contains a syntax error, etc) then a
	 * <code>SyntaxError</code> is thrown.
	 * 
	 * @return
	 */
	public WyalFile read() {
		Path.ID pkg = parsePackage();

		// Now, figure out module name from filename
		// FIXME: this is a hack!
		String name = filename.substring(
				filename.lastIndexOf(File.separatorChar) + 1,
				filename.length() - 7);
		WyalFile wf = new WyalFile(pkg.append(name), filename);

		skipWhiteSpace();
		while (index < tokens.size()) {
			Token lookahead = tokens.get(index);
			if (lookahead.kind == Import) {
				parseImportDeclaration(wf);
			} else {
				List<WyalFile.Modifier> modifiers = parseModifiers();
				checkNotEof();
				lookahead = tokens.get(index);
				if (lookahead.text.equals("assume")) {
					parseAssumeDeclaration(wf, modifiers);
				} else if (lookahead.text.equals("assert")) {
					parseAssertDeclaration(wf, modifiers);
				} else if (lookahead.text.equals("type")) {
					parseTypeDeclaration(wf, modifiers);
				} else if (lookahead.text.equals("constant")) {
					parseConstantDeclaration(wf, modifiers);
				} else if (lookahead.kind == Function) {
					parseFunctionDeclaration(wf, modifiers);
				}  else {
					syntaxError("unrecognised declaration", lookahead);
				}
			}
			skipWhiteSpace();
		}

		return wf;
	}
	
	private Trie parsePackage() {
		Trie pkg = Trie.ROOT;

		if (tryAndMatch(true, Package) != null) {
			// found a package keyword
			pkg = pkg.append(match(Identifier).text);

			while (tryAndMatch(true, Dot) != null) {
				pkg = pkg.append(match(Identifier).text);
			}

			matchEndLine();
			return pkg;
		} else {
			return pkg; // no package
		}
	}

	/**
	 * Parse an import declaration, which is of the form:
	 * 
	 * <pre>
	 * ImportDecl ::= Identifier ["from" ('*' | Identifier)] ( ('.' | '..') ('*' | Identifier) )*
	 * </pre>
	 * 
	 * @param wf
	 *            WyalFile being constructed
	 */
	private void parseImportDeclaration(WyalFile wf) {
		int start = index;

		match(Import);

		// First, parse "from" usage (if applicable)
		Token token = tryAndMatch(true, Identifier, Star);
		if (token == null) {
			syntaxError("expected identifier or '*' here", token);
		}
		String name = token.text;
		// NOTE: we don't specify "from" as a keyword because this prevents it
		// from being used as a variable identifier.
		Token lookahead;
		if ((lookahead = tryAndMatchOnLine(Identifier)) != null) {
			// Ok, this must be "from"
			if (!lookahead.text.equals("from")) {
				syntaxError("expected \"from\" here", lookahead);
			}
			token = match(Identifier);
		}

		// Second, parse package string
		Trie filter = Trie.ROOT.append(token.text);
		token = null;
		while ((token = tryAndMatch(true, Dot, DotDot)) != null) {
			if (token.kind == DotDot) {
				filter = filter.append("**");
			}
			if (tryAndMatch(true, Star) != null) {
				filter = filter.append("*");
			} else {
				filter = filter.append(match(Identifier).text);
			}
		}

		int end = index;
		matchEndLine();

		wf.add(wf.new Import(filter, name, sourceAttr(start, end - 1)));
	}
	

	private List<WyalFile.Modifier> parseModifiers() {
		ArrayList<WyalFile.Modifier> mods = new ArrayList<WyalFile.Modifier>();
		Token lookahead;
		boolean visible = false;
		while ((lookahead = tryAndMatch(true, Public, Protected, Private)) != null) {
			switch(lookahead.kind) {
			case Public:
			case Protected:
			case Private:
				if(visible) {
					syntaxError("visibility modifier already given",lookahead);
				}
			}
			switch (lookahead.kind) {
			case Public:
				mods.add(WyalFile.Modifier.PUBLIC);
				visible = true;
				break;
			case Protected:
				mods.add(WyalFile.Modifier.PROTECTED);
				visible = true;
				break;
			case Private:
				mods.add(WyalFile.Modifier.PRIVATE);
				visible = true;
				break;			
			}
		}
		return mods;
	}
	
	/**
	 * Parse a <i>function declaration</i> which has the form:
	 * 
	 * <pre>
	 * FunctionDeclaration ::= "function" TypePattern "=>" TypePattern (FunctionMethodClause)* ':' NewLine Block
	 * 
	 * FunctionMethodClause ::= "throws" Type | "requires" Expr | "ensures" Expr
	 * </pre>
	 * 
	 * Here, the first type pattern (i.e. before "=>") is referred to as the
	 * "parameter", whilst the second is referred to as the "return". There are
	 * three kinds of option clause:
	 * 
	 * <ul>
	 * <li><b>Throws clause</b>. This defines the exceptions which may be thrown
	 * by this function. Multiple clauses may be given, and these are taken
	 * together as a union. Furthermore, the convention is to specify the throws
	 * clause before the others.</li>
	 * <li><b>Requires clause</b>. This defines a constraint on the permissible
	 * values of the parameters on entry to the function or method, and is often
	 * referred to as the "precondition". This expression may refer to any
	 * variables declared within the parameter type pattern. Multiple clauses
	 * may be given, and these are taken together as a conjunction. Furthermore,
	 * the convention is to specify the requires clause(s) before any ensure(s)
	 * clauses.</li>
	 * <li><b>Ensures clause</b>. This defines a constraint on the permissible
	 * values of the the function or method's return value, and is often
	 * referred to as the "postcondition". This expression may refer to any
	 * variables declared within either the parameter or return type pattern.
	 * Multiple clauses may be given, and these are taken together as a
	 * conjunction. Furthermore, the convention is to specify the requires
	 * clause(s) after the others.</li>
	 * </ul>
	 * 
	 * <p>
	 * The following function declaration provides a small example to
	 * illustrate:
	 * </p>
	 * 
	 * <pre>
	 * function max(int x, int y) => (int z)
	 * // return must be greater than either parameter
	 * ensures x <= z && y <= z
	 * // return must equal one of the parmaeters
	 * ensures x == z || y == z
	 * </pre>
	 * 
	 * <p>
	 * Here, we see the specification for the well-known <code>max()</code>
	 * function which returns the largest of its parameters. This does not throw
	 * any exceptions, and does not enforce any preconditions on its parameters.
	 * </p>
	 */
	private void parseFunctionDeclaration(WyalFile wf,
			List<WyalFile.Modifier> modifiers) {
		int start = index;

		match(Function);
		Token name = match(Identifier);

		// Parse function or method parameters
		match(LeftBrace);

		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		HashSet<String> environment = new HashSet<String>();

		boolean firstTime = true;
		while (eventuallyMatch(RightBrace) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			int pStart = index;
			Pair<SyntacticType, Token> p = parseMixedType();
			Token id = p.second();
			if (environment.contains(id.text)) {
				syntaxError("parameter already declared", id);
			}
			parameters.add(wf.new Parameter(p.first(), id.text, sourceAttr(
					pStart, index - 1)));
			environment.add(id.text);
		}

		// Parse (optional) return type
		TypePattern ret;
		HashSet<String> ensuresEnvironment = environment;

		match(EqualsGreater);
		// Explicit return type is given, so parse it! We first clone the
		// environent and create a special one only for use within ensures
		// clauses, since these are the only expressions which may refer to
		// variables declared in the return type.
		ensuresEnvironment = new HashSet<String>(environment);
		ret = parseTypePattern(ensuresEnvironment, true);
	
		// Parse optional throws/requires/ensures clauses

		ArrayList<Expr> requires = new ArrayList<Expr>();
		ArrayList<Expr> ensures = new ArrayList<Expr>();
		// FIXME: following should be a list!
		SyntacticType throwws = SyntacticType.Void();

		Token lookahead;
		while ((lookahead = tryAndMatch(true, Requires, Ensures, Throws)) != null) {
			switch (lookahead.kind) {
			case Requires:
				// NOTE: expression terminated by ':'
				requires.add(parseLogicalExpression(wf, environment, true));
				break;
			case Ensures:
				// Use the ensuresEnvironment here to get access to any
				// variables declared in the return type pattern.
				// NOTE: expression terminated by ':'
				ensures.add(parseLogicalExpression(wf, ensuresEnvironment, true));
				break;
			case Throws:
				throwws = parseType();
				break;
			}
		}

		matchEndLine();

		WyalFile.Declaration declaration = wf.new Function(modifiers, name.text, ret,
					parameters, requires, ensures, throwws, sourceAttr(
							start, index - 1));
		wf.add(declaration);
	}
	
	/**
	 * Parse a type declaration in a Wyal source file, which has the form:
	 * 
	 * <pre>
	 * "type" Identifier "is" TypePattern ["where" Expr]
	 * </pre>
	 * 
	 * Here, the type pattern specifies a type which may additionally be adorned
	 * with variable names. The "where" clause is optional and is often referred
	 * to as the type's "constraint". Variables defined within the type pattern
	 * may be used within this constraint expressions. A simple example to
	 * illustrate is:
	 * 
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * </pre>
	 * 
	 * Here, we are defining a <i>constrained type</i> called <code>nat</code>
	 * which represents the set of natural numbers (i.e the non-negative
	 * integers). Type declarations may also have modifiers, such as
	 * <code>public</code> and <code>private</code>.
	 * 
	 * @see wycs.syntax.WyalFile.Type
	 * 
	 * @param wf
	 *            --- The Wyal file in which this declaration is defined.
	 * @param modifiers
	 *            --- The list of modifiers for this declaration (which were
	 *            already parsed before this method was called).
	 */
	public void parseTypeDeclaration(WyalFile wf, List<WyalFile.Modifier> modifiers) {
		int start = index;
		// Match identifier rather than kind e.g. Type to avoid "type" being a
		// keyword.
		match(Identifier);
		//
		Token name = match(Identifier);
		match(Is);
		// Parse the type pattern
		TypePattern pattern = parseTypePattern(new HashSet<String>(), false);

		Expr constraint = null;
		// Check whether or not there is an optional "where" clause.
		if (tryAndMatch(true, Where) != null) {
			// Yes, there is a "where" clause so parse the constraint. First,
			// construct the environment which will be used to identify the set
			// of declared variables in the current scope.
			HashSet<String> environment = new HashSet<String>();
			pattern.addDeclaredVariables(environment);
			constraint = parseLogicalExpression(wf, environment, false);
		}
		int end = index;
		matchEndLine();

		WyalFile.Declaration declaration = wf.new Type(modifiers, pattern,
				name.text, constraint, sourceAttr(start, end - 1));
		wf.add(declaration);
		return;
	}
	
	/**
	 * Parse a constant declaration in a Wyal source file, which has the form:
	 * 
	 * <pre>
	 * ConstantDeclaration ::= "constant" Identifier "is"Expr
	 * </pre>
	 * 
	 * A simple example to illustrate is:
	 * 
	 * <pre>
	 * constant PI is 3.141592654
	 * </pre>
	 * 
	 * Here, we are defining a constant called <code>PI</code> which represents
	 * the decimal value "3.141592654". Constant declarations may also have
	 * modifiers, such as <code>public</code> and <code>private</code>.
	 * 
	 * @see wycs.syntax.WyalFile.Constant
	 * 
	 * @param wf
	 *            --- The Whiley file in which this declaration is defined.
	 * @param modifiers
	 *            --- The list of modifiers for this declaration (which were
	 *            already parsed before this method was called).
	 */
	private void parseConstantDeclaration(WyalFile wf,
			List<WyalFile.Modifier> modifiers) {
		int start = index;
		// Match identifier rather than kind e.g. constant to avoid "constant"
		// being a
		// keyword.
		match(Identifier);
		//
		Token name = match(Identifier);
		match(Is);
		Expr e = parseMultiExpression(wf, new HashSet<String>(), false);
		int end = index;
		matchEndLine();
		WyalFile.Declaration declaration = wf.new Constant(modifiers, e,
				name.text, sourceAttr(start, end - 1));
		wf.add(declaration);
	}
	
	/**
	 * Parse a tuple expression, which has the form:
	 * 
	 * <pre>
	 * TupleExpr::= Expr (',' Expr)*
	 * </pre>
	 * 
	 * Tuple expressions are expressions which can return multiple values (i.e.
	 * tuples). In many situations, tuple expressions are not permitted since
	 * tuples cannot be used in that context.
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseMultiExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		Expr lhs = parseUnitExpression(wf, environment, terminated);

		if (tryAndMatch(terminated, Comma) != null) {
			// Indicates this is a tuple expression.
			ArrayList<Expr> elements = new ArrayList<Expr>();
			elements.add(lhs);
			// Add all expressions separated by a comma
			do {
				elements.add(parseUnitExpression(wf, environment, terminated));
			} while (tryAndMatch(terminated, Comma) != null);
			// Done
			return new Expr.Nary(Expr.Nary.Op.TUPLE,elements, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse a unit expression, which has the form:
	 * 
	 * <pre>
	 * UnitExpr::= LogicalExpression
	 * </pre>
	 * 
	 * <p>
	 * A unit expression is essentially any expression, except that it is not
	 * allowed to be a tuple expression. More specifically, it cannot be
	 * followed by ',' (e.g. because the enclosing context uses ',').
	 * </p>
	 * 
	 * <p>
	 * As an example consider a record expression, such as
	 * <code>{x: e1, y: e2}</code>. Here, the sub-expression "e1" must be a
	 * non-tuple expression since it is followed by ',' to signal the start of
	 * the next field "y". Of course, e1 can be a tuple expression if we use
	 * brackets as these help disambiguate the context.
	 * </p>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * @return
	 */
	private Expr parseUnitExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		return parseLogicalExpression(wf, environment, terminated);
	}

	/**
	 * Parse a logical expression of the form:
	 * 
	 * <pre>
	 * Expr ::= AndOrExpr [ "==>" UnitExpr]
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseLogicalExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		checkNotEof();
		int start = index;
		Expr lhs = parseAndOrExpression(wf, environment, terminated);
		Token lookahead = tryAndMatch(terminated,  LogicalImplication, LogicalIff);
		if (lookahead != null) {
			switch (lookahead.kind) {

			case LogicalImplication: {
				Expr rhs = parseUnitExpression(wf, environment, terminated);
				return new Expr.Binary(Expr.Binary.Op.IMPLIES, lhs, rhs, sourceAttr(start,
						index - 1));
			}
			case LogicalIff: {
				Expr rhs = parseUnitExpression(wf, environment, terminated);
				return new Expr.Binary(Expr.Binary.Op.IFF, lhs, rhs, sourceAttr(start,
						index - 1));
			}
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}

		}

		return lhs;
	}

	/**
	 * Parse a logical expression of the form:
	 * 
	 * <pre>
	 * Expr ::= ConditionExpr [ ( "&&" | "||" ) Expr]
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseAndOrExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		checkNotEof();
		int start = index;
		Expr lhs = parseConditionExpression(wf, environment, terminated);
		Token lookahead = tryAndMatch(terminated, LogicalAnd, LogicalOr);
		if (lookahead != null) {
			Expr.Binary.Op bop;
			switch (lookahead.kind) {
			case LogicalAnd:
				bop = Expr.Binary.Op.AND;
				break;
			case LogicalOr:
				bop = Expr.Binary.Op.OR;
				break;
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}
			Expr rhs = parseUnitExpression(wf, environment, terminated);
			return new Expr.Binary(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse a condition expression.
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseConditionExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		Token lookahead;

		// First, attempt to parse quantifiers (e.g. some, all, no, etc)
		if ((lookahead = tryAndMatch(terminated, Some, No, All)) != null) {
			return parseQuantifierExpression(lookahead, wf, environment,
					terminated);
		}

		Expr lhs = parseAppendExpression(wf, environment, terminated);

		lookahead = tryAndMatch(terminated, LessEquals, LeftAngle,
				GreaterEquals, RightAngle, EqualsEquals, NotEquals, In, Is,
				Subset, SubsetEquals, Superset, SupersetEquals);

		if (lookahead != null) {
			Expr.Binary.Op bop;
			switch (lookahead.kind) {
			case LessEquals:
				bop = Expr.Binary.Op.LTEQ;
				break;
			case LeftAngle:
				bop = Expr.Binary.Op.LT;
				break;
			case GreaterEquals:
				bop = Expr.Binary.Op.GTEQ;
				break;
			case RightAngle:
				bop = Expr.Binary.Op.GT;
				break;
			case EqualsEquals:
				bop = Expr.Binary.Op.EQ;
				break;
			case NotEquals:
				bop = Expr.Binary.Op.NEQ;
				break;
			case In:
				bop = Expr.Binary.Op.IN;
				break;
			case Is:
				SyntacticType type = parseUnitType();
				Expr.TypeVal rhs = new Expr.TypeVal(type, sourceAttr(start,
						index - 1));
				return Expr.Binary(Expr.Binary.Op.IS, lhs, rhs, sourceAttr(start,
						index - 1));
			case Subset:
				bop = Expr.Binary.Op.SUBSET;
				break;
			case SubsetEquals:
				bop = Expr.Binary.Op.SUBSETEQ;
				break;
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}

			Expr rhs = parseAppendExpression(wf, environment, terminated);
			return new Expr.Binary(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse a quantifier expression, which is of the form:
	 * 
	 * <pre>
	 * QuantExpr ::= ("no" | "some" | "all") 
	 *               '{' 
	 *                   Identifier "in" Expr (',' Identifier "in" Expr)+ 
	 *                   '|' LogicalExpr
	 *               '}'
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @param environment
	 * @return
	 */
	private Expr parseQuantifierExpression(Token lookahead, WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index - 1;

		// Determine the quantifier operation
		Expr.COp cop;
		switch (lookahead.kind) {
		case No:
			cop = Expr.COp.NONE;
			break;
		case Some:
			cop = Expr.COp.SOME;
			break;
		case All:
			cop = Expr.COp.ALL;
			break;
		default:
			cop = null; // deadcode
		}

		match(LeftCurly);

		// Parse one or more source variables / expressions
		environment = new HashSet<String>(environment);
		List<Pair<String, Expr>> srcs = new ArrayList<Pair<String, Expr>>();
		boolean firstTime = true;

		do {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			Token id = match(Identifier);
			if (environment.contains(id.text)) {
				// It is already defined which is a syntax error
				syntaxError("variable already declared", id);
			}
			match(In);
			// We have to parse an Append Expression here, which is the most
			// general form of expression that can generate a collection of some
			// kind. All expressions higher up (e.g. logical expressions) cannot
			// generate collections. Furthermore, the bitwise or expression
			// could lead to ambiguity and, hence, we bypass that an consider
			// append expressions only.
			Expr src = parseAppendExpression(wf, environment, terminated);
			srcs.add(new Pair<String, Expr>(id.text, src));
			environment.add(id.text);
		} while (eventuallyMatch(VerticalBar) == null);

		// Parse condition over source variables
		Expr condition = parseLogicalExpression(wf, environment, terminated);

		match(RightCurly);

		// Done
		return new Expr.Comprehension(cop, null, srcs, condition, sourceAttr(
				start, index - 1));
	}

	/**
	 * Parse an append expression, which has the form:
	 * 
	 * <pre>
	 * AppendExpr ::= RangeExpr ( "++" RangeExpr)*
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseAppendExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		Expr lhs = parseRangeExpression(wf, environment, terminated);

		while (tryAndMatch(terminated, PlusPlus) != null) {
			Expr rhs = parseRangeExpression(wf, environment, terminated);
			lhs = new Expr.Binary(Expr.Binary.Op.LISTAPPEND, lhs, rhs, sourceAttr(
					start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse a range expression, which has the form:
	 * 
	 * <pre>
	 * RangeExpr ::= ShiftExpr [ ".." ShiftExpr ]
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseRangeExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		Expr lhs = parseAdditiveExpression(wf, environment, terminated);

		if (tryAndMatch(terminated, DotDot) != null) {
			Expr rhs = parseAdditiveExpression(wf, environment, terminated);
			return new Expr.Binary(Expr.Binary.Op.RANGE, lhs, rhs, sourceAttr(start,
					index - 1));
		}

		return lhs;
	}

	/**
	 * Parse an additive expression.
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseAdditiveExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		Expr lhs = parseMultiplicativeExpression(wf, environment, terminated);

		Token lookahead;
		while ((lookahead = tryAndMatch(terminated, Plus, Minus)) != null) {
			Expr.Binary.Op bop;
			switch (lookahead.kind) {
			case Plus:
				bop = Expr.Binary.Op.ADD;
				break;
			case Minus:
				bop = Expr.Binary.Op.SUB;
				break;
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}

			Expr rhs = parseMultiplicativeExpression(wf, environment,
					terminated);
			lhs = new Expr.Binary(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse a multiplicative expression.
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseMultiplicativeExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		Expr lhs = parseAccessExpression(wf, environment, terminated);

		Token lookahead = tryAndMatch(terminated, Star, RightSlash, Percent);
		if (lookahead != null) {
			Expr.Binary.Op bop;
			switch (lookahead.kind) {
			case Star:
				bop = Expr.Binary.Op.MUL;
				break;
			case RightSlash:
				bop = Expr.Binary.Op.DIV;
				break;
			case Percent:
				bop = Expr.Binary.Op.REM;
				break;
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}
			Expr rhs = parseAccessExpression(wf, environment, terminated);
			return new Expr.Binary(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse an <i>access expression</i>, which has the form:
	 * 
	 * <pre>
	 * AccessExpr::= PrimaryExpr
	 *            | AccessExpr '[' AdditiveExpr ']'
	 *            | AccessExpr '[' AdditiveExpr ".." AdditiveExpr ']'                   
	 *            | AccessExpr '.' Identifier
	 *            | AccessExpr '.' Identifier '(' [ Expr (',' Expr)* ] ')'
	 *            | AccessExpr "=>" Identifier
	 * </pre>
	 * 
	 * <p>
	 * Access expressions are challenging for several reasons. First, they are
	 * <i>left-recursive</i>, making them more difficult to parse correctly.
	 * Secondly, there are several different forms above and, of these, some
	 * generate multiple AST nodes as well (see below).
	 * </p>
	 * 
	 * <p>
	 * This parser attempts to construct the most accurate AST possible and this
	 * requires disambiguating otherwise identical forms. For example, an
	 * expression of the form "aaa.bbb.ccc" can correspond to either a field
	 * access, or a constant expression (e.g. with a package/module specifier).
	 * Likewise, an expression of the form "aaa.bbb.ccc()" can correspond to an
	 * indirect function/method call, or a direct function/method call with a
	 * package/module specifier. To disambiguate these forms, the parser relies
	 * on the fact any sequence of field-accesses must begin with a local
	 * variable.
	 * </p>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseAccessExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		Expr lhs = parseTermExpression(wf, environment, terminated);
		Token token;

		while ((token = tryAndMatchOnLine(LeftSquare)) != null
				|| (token = tryAndMatch(terminated, Dot)) != null) {
			switch (token.kind) {
			case LeftSquare:
				// At this point, there are two possibilities: an access
				// expression (e.g. x[i]), or a sublist (e.g. xs[0..1], xs[..1],
				// xs[0..]). We have to disambiguate these four different
				// possibilities.

				// Since ".." is not the valid start of a statement, we can
				// safely set terminated=true for tryAndMatch().
				if (tryAndMatch(true, DotDot) != null) {
					// This indicates a sublist expression of the form
					// "xs[..e]". Therefore, we inject 0 as the start value for
					// the sublist expression.
					Expr st = new Expr.Constant(Value.Integer(BigInteger.ZERO),
							sourceAttr(start, index - 1));
					// NOTE: expression guaranteed to be terminated by ']'.
					Expr end = parseAdditiveExpression(wf, environment, true);
					match(RightSquare);
					lhs = new Expr.SubList(lhs, st, end, sourceAttr(start,
							index - 1));
				} else {
					// This indicates either a list access or a sublist of the
					// forms xs[a..b] and xs[a..]
					//
					// NOTE: expression guaranteed to be terminated by ']'.
					Expr rhs = parseAdditiveExpression(wf, environment, true);
					// Check whether this is a sublist expression
					if (tryAndMatch(terminated, DotDot) != null) {
						// Yes, this is a sublist but we still need to
						// disambiguate the two possible forms xs[x..y] and
						// xs[x..].
						//
						// NOTE: expression guaranteed to be terminated by ']'.
						if (tryAndMatch(true, RightSquare) != null) {
							// This is a sublist of the form xs[x..]. In this
							// case, we inject |xs| as the end expression.
							Expr end = Expr.Unary(Expr.Unary.Op.LENGTHOF, lhs,
									sourceAttr(start, index - 1));
							lhs = new Expr.SubList(lhs, rhs, end, sourceAttr(
									start, index - 1));
						} else {
							// This is a sublist of the form xs[x..y].
							// Therefore, we need to parse the end expression.
							// NOTE: expression guaranteed to be terminated by
							// ']'.
							Expr end = parseAdditiveExpression(wf, environment,
									true);
							match(RightSquare);
							lhs = new Expr.SubList(lhs, rhs, end, sourceAttr(
									start, index - 1));
						}
					} else {
						// Nope, this is a plain old list access expression
						match(RightSquare);
						lhs = new Expr.IndexOf(lhs, rhs, sourceAttr(start,
								index - 1));
					}
				}
				break;
			case Dot:
				// At this point, we could have a field access, a package access
				// or a method/function invocation. Therefore, we start by
				// parsing the field access and then check whether or not its an
				// invocation.
				String name = match(Identifier).text;
				// This indicates we have either a direct or indirect access or
				// invocation. We can disambiguate between these two categories
				// by examining what we have parsed already. A direct access or
				// invocation requires a sequence of identifiers where the first
				// is not a declared variable name.
				Path.ID id = parsePossiblePathID(lhs, environment);
				
				if (tryAndMatch(terminated, LeftBrace) != null) {
					// This indicates a direct or indirect invocation. First,
					// parse arguments to invocation
					ArrayList<Expr> arguments = parseInvocationArguments(wf,
							environment);
					// Second, determine what kind of invocation we have.
					if(id == null) {
						// This indicates we have an indirect invocation
						lhs = new Expr.FieldAccess(lhs, name, sourceAttr(
								start, index - 1));
						lhs = new Expr.AbstractIndirectInvoke(lhs, arguments,
								sourceAttr(start, index - 1));
					} else {
						// This indicates we have an direct invocation
						lhs = new Expr.AbstractInvoke(name, id, arguments,
								sourceAttr(start, index - 1));
					}

				} else if(id != null) {
					// Must be a qualified constant access
					lhs = new Expr.ConstantAccess(name, id, sourceAttr(
							start, index - 1));
				} else {
					// Must be a plain old field access.
					lhs = Expr.FieldAccess(lhs, name, sourceAttr(
							start, index - 1));
				}
			}
		}

		return lhs;
	}

	/**
	 * Attempt to parse a possible module identifier. This will reflect a true
	 * module identifier only if the root variable is not in the given
	 * environment.
	 * 
	 * @param src
	 * @param environment
	 * @return
	 */
	private Path.ID parsePossiblePathID(Expr src, HashSet<String> environment) {
		if(src instanceof Expr.Variable) {
			// this is a local variable, indicating that the we did not have
			// a module identifier.
			return null;
		} else if(src instanceof Expr.ConstantAccess) {
			Expr.ConstantAccess ca = (Expr.ConstantAccess) src;
			return Trie.ROOT.append(ca.name);
		} else if(src instanceof Expr.FieldAccess) {
			Expr.FieldAccess ada = (Expr.FieldAccess) src;
			Path.ID id = parsePossiblePathID(ada.src,environment);
			if(id != null) {
				return id.append(ada.name);
			} else {
				return null;
			}
		} else {
			return null;
		} 
	}
	
	/**
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseTermExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		checkNotEof();

		int start = index;
		Token token = tokens.get(index);

		switch (token.kind) {
		case LeftBrace:
			return parseBracketedExpression(wf, environment, terminated);		
		case Identifier:
			match(Identifier);
			if (tryAndMatch(terminated, LeftBrace) != null) {
				return parseInvokeExpression(wf, environment, start, token,
						terminated);
			} else if (environment.contains(token.text)) {
				// Signals a local variable access
				return new Expr.Variable(token.text, sourceAttr(start,
						index - 1));
			} else {
				// Otherwise, this must be a constant access of some kind.
				// Observe that, at this point, we cannot determine whether or
				// not this is a constant-access or a package-access which marks
				// the beginning of a constant-access.
				return new Expr.ConstantAccess(token.text, null, sourceAttr(
						start, index - 1));
			}
		case Null:
			return new Expr.Constant(Value.Null, sourceAttr(
					start, index++));
		case True:
			return new Expr.Constant(Value.Bool(true),
					sourceAttr(start, index++));
		case False:
			return new Expr.Constant(Value.Bool(false),
					sourceAttr(start, index++));		
		case CharValue: {
			char c = parseCharacter(token.text);
			return new Expr
					.Constant(Value.Character(c), sourceAttr(start, index++));
		}
		case IntValue: {
			BigInteger val = new BigInteger(token.text);
			return new Expr.Constant(Value.Integer(val), sourceAttr(start,
					index++));
		}
		case RealValue: {
			BigDecimal val = new BigDecimal(token.text);
			return new Expr
					.Constant(Value.Decimal(val), sourceAttr(start, index++));
		}
		case StringValue: {
			String str = parseString(token.text);
			return new Expr.Constant(Value.String(str), sourceAttr(start, index++));
		}
		case Minus:
			return parseNegationExpression(wf, environment, terminated);
		case VerticalBar:
			return parseLengthOfExpression(wf, environment, terminated);
		case LeftSquare:
			return parseListExpression(wf, environment, terminated);
		case LeftCurly:
			return parseRecordOrSetOrMapExpression(wf, environment, terminated);
		case Shreak:
			return parseLogicalNotExpression(wf, environment, terminated);		
		}

		syntaxError("unrecognised term", token);
		return null;
	}

	/**
	 * Parse an expression beginning with a left brace. This is either a cast or
	 * bracketed expression:
	 * 
	 * <pre>
	 * BracketedExpr ::= '(' Type ')' Expr
	 *                      | '(' Expr ')'
	 * </pre>
	 * 
	 * <p>
	 * The challenge here is to disambiguate the two forms (which is similar to
	 * the problem of disambiguating a variable declaration from e.g. an
	 * assignment). Getting this right is actually quite tricky, and we need to
	 * consider what permissible things can follow a cast and/or a bracketed
	 * expression. To simplify things, we only consider up to the end of the
	 * current line in determining whether this is a cast or not. That means
	 * that the expression following a cast *must* reside on the same line as
	 * the cast.
	 * </p>
	 * 
	 * <p>
	 * A cast can be followed by the start of any valid expression. This
	 * includes: identifiers (e.g. "(T) x"), braces of various kinds (e.g.
	 * "(T) [1,2]" or "(T) (1,2)"), unary operators (e.g. "(T) !x", "(T) |xs|",
	 * etc). A bracketed expression, on the other hand, can be followed by a
	 * binary operator (e.g. "(e) + 1"), a left- or right-brace (e.g.
	 * "(1 + (x+1))" or "(*f)(1)") or a newline.
	 * </p>
	 * <p>
	 * Most of these are easy to disambiguate by the following rules:
	 * </p>
	 * <ul>
	 * <li>If what follows is a binary operator (e.g. +, -, etc) then this is an
	 * bracketed expression, not a cast.</li>
	 * <li>If what follows is a right-brace then this is a bracketed expression,
	 * not a cast.</li>
	 * <li>Otherwise, this is a cast.</li>
	 * </ul>
	 * <p>
	 * Unfortunately, there are two problematic casts: '-' and '('. In Java, the
	 * problem of '-' is resolved carefully as follows:
	 * </p>
	 * 
	 * <pre>
	 * CastExpr::= ( PrimitiveType Dimsopt ) UnaryExpression
	 *                 | ( ReferenceType ) UnaryExpressionNotPlusMinus
	 * </pre>
	 * 
	 * See JLS 15.16 (Cast Expressions). This means that, in cases where we can
	 * be certain we have a type, then a general expression may follow;
	 * otherwise, only a restricted expression may follow.
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseBracketedExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		match(LeftBrace);

		// At this point, we must begin to disambiguate casts from general
		// bracketed expressions. In the case that what follows the left brace
		// is something which can only be a type, then clearly we have a cast.
		// However, in the other case, we may still have a cast since many types
		// cannot be clearly distinguished from expressions at this stage (e.g.
		// "(nat,nat)" could either be a tuple type (if "nat" is a type) or a
		// tuple expression (if "nat" is a variable or constant).

		SyntacticType t = parseDefiniteType();

		if (t != null) {
			// At this point, it's looking likely that we have a cast. However,
			// it's not certain because of the potential for nested braces. For
			// example, consider "((char) x + y)". We'll parse the outermost
			// brace and what follows *must* be parsed as either a type, or
			// bracketed type.
			if (tryAndMatch(true, RightBrace) != null) {
				// Ok, finally, we are sure that it is definitely a cast.
				Expr e = parseMultiExpression(wf, environment, terminated);
				return new Expr.Cast(t, e, sourceAttr(start, index - 1));
			}
		}
		// We still may have either a cast or a bracketed expression, and we
		// cannot tell which yet.  
		index = start;
		match(LeftBrace);
		Expr e = parseMultiExpression(wf, environment, true);
		match(RightBrace);

		// At this point, we now need to examine what follows to see whether
		// this is a cast or bracketed expression. See JavaDoc comments
		// above for more on this. What we do is first skip any whitespace,
		// and then see what we've got.
		int next = skipLineSpace(index);		
		if (next < tokens.size()) {
			Token lookahead = tokens.get(next);

			switch (lookahead.kind) {
			case Null:
			case True:
			case False:
			case CharValue:
			case IntValue:
			case RealValue:
			case StringValue:
			case LeftSquare:
			case LeftCurly:

				// FIXME: there is a bug here when parsing a quantified
				// expression such as
				//
				// "all { i in 0 .. (|items| - 1) | items[i] < items[i + 1] }"
				//
				// This is because the trailing vertical bar makes it look
				// like this is a cast.

			case LeftBrace:			
			case VerticalBar:
			case Shreak:
			case Identifier: {
				// Ok, this must be cast so back tract and reparse
				// expression as a type.
				index = start; // backtrack
				SyntacticType type = parseUnitType();				
				// Now, parse cast expression
				e = parseUnitExpression(wf, environment, terminated);
				return new Expr.Cast(type, e, sourceAttr(start, index - 1));
			}
			default:
				// default case, fall through and assume bracketed
				// expression
			}
		}
		// Assume bracketed
		return e;
	}

	/**
	 * Parse a list constructor expression, which is of the form:
	 * 
	 * <pre>
	 * ListExpr ::= '[' [ Expr (',' Expr)* ] ']'
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseListExpression(WyalFile wf, HashSet<String> environment,
			boolean terminated) {
		int start = index;
		match(LeftSquare);
		ArrayList<Expr> exprs = new ArrayList<Expr>();

		boolean firstTime = true;
		while (eventuallyMatch(RightSquare) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			// NOTE: we require the following expression be a "non-tuple"
			// expression. That is, it cannot be composed using ',' unless
			// braces enclose the entire expression. This is because the outer
			// list constructor expression is used ',' to distinguish elements.
			// Also, expression is guaranteed to be terminated, either by ']' or
			// ','.
			exprs.add(parseUnitExpression(wf, environment, true));
		}

		return new Expr
				.Nary(Expr.Nary.Op.LIST, exprs, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a record, set or map constructor, which are of the form:
	 * 
	 * <pre>
	 * RecordExpr ::= '{' Identifier ':' Expr (',' Identifier ':' Expr)* '}'
	 * SetExpr   ::= '{' [ Expr (',' Expr)* ] '}'
	 * MapExpr   ::= '{' Expr "=>" Expr ( ',' Expr "=>" Expr)* '}'
	 * SetComprehension ::= '{' Expr '|' 
	 * 							Identifier "in" Expr (',' Identifier "in" Expr)*
	 *                          [',' Expr] '}'
	 * </pre>
	 * 
	 * Disambiguating these three forms is relatively straightforward. We parse
	 * the left curly brace. Then, if what follows is a right curly brace then
	 * we have a set expression. Otherwise, we parse the first expression, then
	 * examine what follows. If it's ':', then we have a record expression;
	 * otherwise, we have a set expression.
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseRecordOrSetOrMapExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		match(LeftCurly);
		// Check for empty set or empty map
		if (tryAndMatch(terminated, RightCurly) != null) {
			// Yes. parsed empty set
			return new Expr.Nary(Expr.Nary.Op.SET, Collections.EMPTY_LIST,
					sourceAttr(start, index - 1));
		} else if (tryAndMatch(terminated, EqualsGreater) != null) {
			// Yes. parsed empty map
			match(RightCurly);
			return new Expr.Nary(Expr.Nary.Op.MAP, Collections.EMPTY_LIST,
					sourceAttr(start, index - 1));
		}
		// Parse first expression for disambiguation purposes
		// NOTE: we require the following expression be a "non-tuple"
		// expression. That is, it cannot be composed using ',' unless
		// braces enclose the entire expression. This is because the outer
		// set/map/record constructor expressions use ',' to distinguish
		// elements.
		Expr e = parseBitwiseXorExpression(wf, environment, terminated);
		// Now, see what follows and disambiguate
		if (tryAndMatch(terminated, Colon) != null) {
			// Ok, it's a ':' so we have a record constructor
			index = start;
			return parseRecordExpression(wf, environment, terminated);
		} else if (tryAndMatch(terminated, EqualsGreater) != null) {
			// Ok, it's a "=>" so we have a record constructor
			index = start;
			return parseMapExpression(wf, environment, terminated);
		} else if (tryAndMatch(terminated, VerticalBar) != null) {
			// Ok, it's a "|" so we have a set comprehension
			index = start;
			return parseSetComprehension(wf, environment, terminated);
		} else {
			// otherwise, assume a set expression
			index = start;
			return parseSetExpression(wf, environment, terminated);
		}
	}

	/**
	 * Parse a record constructor, which is of the form:
	 * 
	 * <pre>
	 * RecordExpr ::= '{' Identifier ':' Expr (',' Identifier ':' Expr)* '}'
	 * </pre>
	 * 
	 * During parsing, we additionally check that each identifier is unique;
	 * otherwise, an error is reported.
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseRecordExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		match(LeftCurly);
		HashSet<String> keys = new HashSet<String>();
		HashMap<String, Expr> exprs = new HashMap<String, Expr>();

		boolean firstTime = true;
		while (eventuallyMatch(RightCurly) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			// Parse field name being constructed
			Token n = match(Identifier);
			// Check field name is unique
			if (keys.contains(n.text)) {
				syntaxError("duplicate tuple key", n);
			}
			match(Colon);
			// Parse expression being assigned to field
			// NOTE: we require the following expression be a "non-tuple"
			// expression. That is, it cannot be composed using ',' unless
			// braces enclose the entire expression. This is because the outer
			// record constructor expression is used ',' to distinguish fields.
			// Also, expression is guaranteed to be terminated, either by '}' or
			// ','.
			Expr e = parseUnitExpression(wf, environment, true);
			exprs.put(n.text, e);
			keys.add(n.text);
		}

		return new Expr.Record(exprs, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a map constructor expression, which is of the form:
	 * 
	 * <pre>
	 * MapExpr::= '{' Expr "=>" Expr (',' Expr "=>" Expr)* } '}'
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseMapExpression(WyalFile wf, HashSet<String> environment,
			boolean terminated) {
		int start = index;
		match(LeftCurly);
		ArrayList<Pair<Expr, Expr>> exprs = new ArrayList<Pair<Expr, Expr>>();

		// Match zero or more expressions separated by commas
		boolean firstTime = true;
		while (eventuallyMatch(RightCurly) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			Expr from = parseUnitExpression(wf, environment, terminated);
			match(EqualsGreater);
			// NOTE: we require the following expression be a "non-tuple"
			// expression. That is, it cannot be composed using ',' unless
			// braces enclose the entire expression. This is because the outer
			// map constructor expression is used ',' to distinguish elements.
			// Also, expression is guaranteed to be terminated, either by '}' or
			// ','.
			Expr to = parseUnitExpression(wf, environment, true);
			exprs.add(new Pair<Expr, Expr>(from, to));
		}
		// done
		return new Expr.Nary(Expr.Nary.Op.MAP, exprs, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a set constructor expression, which is of the form:
	 * 
	 * <pre>
	 * SetExpr::= '{' [ Expr (',' Expr)* } '}'
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseSetExpression(WyalFile wf, HashSet<String> environment,
			boolean terminated) {
		int start = index;
		match(LeftCurly);
		ArrayList<Expr> exprs = new ArrayList<Expr>();

		// Match zero or more expressions separated by commas
		boolean firstTime = true;
		while (eventuallyMatch(RightCurly) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			// NOTE: we require the following expression be a "non-tuple"
			// expression. That is, it cannot be composed using ',' unless
			// braces enclose the entire expression. This is because the outer
			// set constructor expression is used ',' to distinguish elements.
			// Also, expression is guaranteed to be terminated, either by '}' or
			// ','.
			exprs.add(parseUnitExpression(wf, environment, true));
		}
		// done
		return new Expr.Nary(Expr.Nary.Op.SET,exprs, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a set comprehension expression, which is of the form:
	 * 
	 * <pre>
	 * 	SetComprehension ::= '{' Expr '|' 
	 *      					Identifier "in" Expr (',' Identifier "in" Expr)*
	 *                          [',' Expr] '}'
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseSetComprehension(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		match(LeftCurly);

		int e_start = index; // marker
		// We cannot parse a bitwise or expression here, because this would
		// conflict with the vertical bar that we are expecting. Therefore, we
		// have to parse something lower in the expression "hierarchy". In this
		// case, the highest expression which is not a bitwise or is the bitwise
		// xor. Unfortunately, that also rules out many otherwise sensible
		// expressions (e.g. logical expressions). However, expression is
		// guaranteed to be terminated by '|'.
		Expr value = parseBitwiseXorExpression(wf, environment, true);

		match(VerticalBar);

		// Clone the environment so that we can include those variables which
		// are declared by the comprehension.
		environment = new HashSet<String>(environment);
		
		// Match zero or more source expressions separated by commas. 
		Expr condition = null;
		ArrayList<Pair<String, Expr>> srcs = new ArrayList<Pair<String, Expr>>();
		boolean firstTime = true;
		do {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			// NOTE: we require the following expression be a "non-tuple"
			// expression. That is, it cannot be composed using ',' unless
			// braces enclose the entire expression. This is because the outer
			// set constructor expression is used ',' to distinguish elements.
			// However, expression is guaranteed to be terminated either by '}'
			// or by ','.
			Expr e = parseUnitExpression(wf, environment, true);
			
			if (e instanceof Expr.Binary
					&& ((Expr.Binary) e).op == Expr.Binary.Op.IN
					&& ((Expr.Binary) e).lhs instanceof Expr.ConstantAccess) {
				Expr.Binary bop = (Expr.Binary) e;
				String var = ((Expr.ConstantAccess) bop.lhs).name;
				Expr src = bop.rhs;
				if (environment.contains(var)) {
					// It is already defined which is a syntax error
					syntaxError("variable already declared", bop.lhs);
				}
				srcs.add(new Pair<String, Expr>(var, src));
				environment.add(var);
			} else {
				condition = e;
				match(RightCurly);
				break;
			}
		} while (eventuallyMatch(RightCurly) == null);

		// At this point, we do something a little wierd. We backtrack and
		// reparse the original expression using the updated environment. This
		// ensures that all variable accesses are correctly noted as local
		// variable accesses.
		int end = index; // save
		index = e_start; // backtrack
		// Repeat of restrictiveness discussed above. Expression guaranteed to
		// be terminated by '|' (as before).
		value = parseBitwiseXorExpression(wf, environment, true);
		index = end; // restore
		// done
		return new Expr.Comprehension(Expr.COp.SETCOMP, value, srcs, condition,
				sourceAttr(start, index - 1));
	}

	/**
	 * Parse a length of expression, which is of the form:
	 * 
	 * <pre>
	 * TermExpr::= ...
	 *                 |  '|' Expr '|'
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseLengthOfExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		match(VerticalBar);
		// We have to parse an Append Expression here, which is the most general
		// form of expression that can generate a collection of some kind. All
		// expressions higher up (e.g. logical expressions) cannot generate
		// collections. Furthermore, the bitwise or expression could lead to
		// ambiguity and, hence, we bypass that an consider append expressions
		// only. However, the expression is guaranteed to be terminated by '|'.
		Expr e = parseAppendExpression(wf, environment, true);
		match(VerticalBar);
		return new Expr.Unary(Expr.Unary.Op.LENGTHOF, e,
				sourceAttr(start, index - 1));
	}

	/**
	 * Parse a negation expression, which is of the form:
	 * 
	 * <pre>
	 * TermExpr::= ...
	 *                 |  '-' Expr
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseNegationExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		match(Minus);
		Expr e = parseAccessExpression(wf, environment, terminated);

		// FIXME: we shouldn't be doing constant folding here, as it's
		// unnecessary at this point and should be performed later during
		// constant propagation.

		if (e instanceof Expr.Constant) {
			Expr.Constant c = (Expr.Constant) e;
			if (c.value instanceof Value.Decimal) {
				BigDecimal br = ((Value.Decimal) c.value).value;
				return new Expr.Constant(Value.Decimal(br
						.negate()), sourceAttr(start, index));
			}
		}

		return new Expr.Unary(Expr.Unary.Op.NEG, e, sourceAttr(start, index));
	}

	/**
	 * Parse an invocation expression, which has the form:
	 * 
	 * <pre>
	 * InvokeExpr::= Identifier '(' [ Expr (',' Expr)* ] ')'
	 * </pre>
	 * 
	 * Observe that this when this function is called, we're assuming that the
	 * identifier and opening brace has already been matched.
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseInvokeExpression(WyalFile wf,
			HashSet<String> environment, int start, Token name,
			boolean terminated) {
		// First, parse the generic arguments to this invocation.
		ArrayList<SyntacticType> types = parseGenericArguments(wf, environment);
		
		// Second, parse the arguments to this invocation.
		ArrayList<Expr> args = parseInvocationArguments(wf, environment);
		
		// unqualified direct invocation
		return new Expr.FunCall(name.text, types, args, sourceAttr(start, index - 1));				
	}

	/**
	 * Parse a sequence of arguments separated by commas that ends in a
	 * right-brace:
	 * 
	 * <pre>
	 * ArgumentList ::= [ Expr (',' Expr)* ] ')'
	 * </pre>
	 * 
	 * Note, when this function is called we're assuming the left brace was
	 * already parsed.
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private ArrayList<SyntacticType> parseGenericArguments(WyalFile wf,
			HashSet<String> environment) {
		
		// TODO: parse generic parameters
		
		return new ArrayList<SyntacticType>();
	}
	
	/**
	 * Parse a sequence of arguments separated by commas that ends in a
	 * right-brace:
	 * 
	 * <pre>
	 * ArgumentList ::= [ Expr (',' Expr)* ] ')'
	 * </pre>
	 * 
	 * Note, when this function is called we're assuming the left brace was
	 * already parsed.
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @return
	 */
	private ArrayList<Expr> parseInvocationArguments(WyalFile wf,
			HashSet<String> environment) {
		boolean firstTime = true;
		ArrayList<Expr> args = new ArrayList<Expr>();
		while (eventuallyMatch(RightBrace) == null) {
			if (!firstTime) {
				match(Comma);
			} else {
				firstTime = false;
			}
			// NOTE: we require the following expression be a "non-tuple"
			// expression. That is, it cannot be composed using ',' unless
			// braces enclose the entire expression. This is because the outer
			// invocation expression is used ',' to distinguish arguments.
			// However, expression is guaranteed to be terminated either by ')'
			// or by ','.
			Expr e = parseUnitExpression(wf, environment, true);

			args.add(e);
		}
		return args;
	}

	/**
	 * Parse a logical not expression, which has the form:
	 * 
	 * <pre>
	 * TermExpr::= ...
	 *       | '!' Expr
	 * </pre>
	 * 
	 * @param wf
	 *            The enclosing WyalFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @param terminated
	 *            This indicates that the expression is known to be terminated
	 *            (or not). An expression that's known to be terminated is one
	 *            which is guaranteed to be followed by something. This is
	 *            important because it means that we can ignore any newline
	 *            characters encountered in parsing this expression, and that
	 *            we'll never overrun the end of the expression (i.e. because
	 *            there's guaranteed to be something which terminates this
	 *            expression). A classic situation where terminated is true is
	 *            when parsing an expression surrounded in braces. In such case,
	 *            we know the right-brace will always terminate this expression.
	 * 
	 * @return
	 */
	private Expr parseLogicalNotExpression(WyalFile wf,
			HashSet<String> environment, boolean terminated) {
		int start = index;
		match(Shreak);
		// Note: cannot parse unit expression here, because that messes up the
		// precedence. For example, !result ==> other should be parsed as
		// (!result) ==> other, not !(result ==> other).
		Expr expression = parseConditionExpression(wf, environment, terminated);
		return new Expr.Unary(Expr.Unary.Op.NOT, expression, sourceAttr(start,
				index - 1));
	}


	/**
	 * Attempt to parse something which maybe a type, or an expression. The
	 * semantics of this function dictate that it returns an instanceof
	 * SyntacticType *only* if what it finds *cannot* be parsed as an
	 * expression, but can be parsed as a type. Otherwise, the state is left
	 * unchanged.
	 * 
	 * @return An instance of SyntacticType or null.
	 */
	public SyntacticType parseDefiniteType() {
		int start = index; // backtrack point
		try {
			SyntacticType type = parseType();
			if (mustParseAsType(type)) {
				return type;
			}
		} catch (SyntaxError e) {

		}
		index = start; // backtrack
		return null;
	}

	/**
	 * <p>
	 * Determine whether or not the given type can be parsed as an expression.
	 * In many cases, a type can (e.g. <code>{x}</code> is both a valid type and
	 * expression). However, some types are not also expressions (e.g.
	 * <code>int</code>, <code>{int f}</code>, <code>&int</code>, etc).
	 * </p>
	 * 
	 * <p>
	 * This function *must* return false if what the given type could not be
	 * parsed as an expression. However, if what it can be parsed as an
	 * expression, then this function must return false (even if we will
	 * eventually treat this as a type). This function is called from either the
	 * beginning of a statement (i.e. to disambiguate variable declarations), or
	 * after matching a left brace (i.e. to disambiguate casts).
	 * </p>
	 * 
	 * @param index
	 *            Position in the token stream to begin looking from.
	 * @return
	 */
	private boolean mustParseAsType(SyntacticType type) {

		if (type instanceof SyntacticType.Primitive) {
			// All primitive types must be parsed as types, since their
			// identifiers are keywords.
			return true;
		} else if (type instanceof SyntacticType.Record) {
			// Record types must be parsed as types, since e.g. {int f} is not a
			// valid expression.
			return true;
		} else if (type instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple tt = (SyntacticType.Tuple) type;
			boolean result = false;
			for (SyntacticType element : tt.types) {
				result |= mustParseAsType(element);
			}
			return result;
		} else if (type instanceof SyntacticType.FunctionOrMethod) {
			SyntacticType.FunctionOrMethod tt = (SyntacticType.FunctionOrMethod) type;
			boolean result = false;
			for (SyntacticType element : tt.paramTypes) {
				result |= mustParseAsType(element);
			}
			return result | mustParseAsType(tt.ret);
		} else if (type instanceof SyntacticType.Intersection) {
			SyntacticType.Intersection tt = (SyntacticType.Intersection) type;
			boolean result = false;
			for (SyntacticType element : tt.bounds) {
				result |= mustParseAsType(element);
			}
			return result;
		} else if (type instanceof SyntacticType.List) {
			SyntacticType.List tt = (SyntacticType.List) type;
			return mustParseAsType(tt.element);
		} else if (type instanceof SyntacticType.Map) {
			SyntacticType.Map tt = (SyntacticType.Map) type;
			return mustParseAsType(tt.key) || mustParseAsType(tt.value);
		} else if (type instanceof SyntacticType.Negation) {
			SyntacticType.Negation tt = (SyntacticType.Negation) type;
			return mustParseAsType(tt.element);
		} else if (type instanceof SyntacticType.Nominal) {
			return false; // always can be an expression
		} else if (type instanceof SyntacticType.Reference) {
			SyntacticType.Reference tt = (SyntacticType.Reference) type;
			return mustParseAsType(tt.element);
		} else if (type instanceof SyntacticType.Set) {
			SyntacticType.Set tt = (SyntacticType.Set) type;
			return mustParseAsType(tt.element);
		} else if (type instanceof SyntacticType.Union) {
			SyntacticType.Union tt = (SyntacticType.Union) type;
			boolean result = false;
			for (SyntacticType element : tt.bounds) {
				result |= mustParseAsType(element);
			}
			return result;
		} else {
			// Error!
			internalFailure("unknown syntactic type encountered", filename,
					type);
			return false; // deadcode
		}
	}

	/**
	 * Attempt to parse something which maybe a type pattern, or an expression.
	 * The semantics of this function dictate that it returns an instanceof
	 * TypePattern *only* if what it finds *cannot* be parsed as an expression,
	 * but can be parsed as a type pattern. Otherwise, the state is left
	 * unchanged.
	 * 
	 * @param environment
	 *            Contains the set of variables previously declared in the
	 *            current type pattern. This is essentially used as a record in
	 *            order to spot invalid attempts to redeclare the same variables
	 *            (e.g. as in "int x, int x")
	 * @param terminated
	 *            This indicates that the type is known to be terminated (or
	 *            not). A type that's known to be terminated is one which is
	 *            guaranteed to be followed by something. This is important
	 *            because it means that we can ignore any newline characters
	 *            encountered in parsing this type, and that we'll never overrun
	 *            the end of the type (i.e. because there's guaranteed to be
	 *            something which terminates this type). A classic situation
	 *            where terminated is true is when parsing a type surrounded in
	 *            braces. In such case, we know the right-brace will always
	 *            terminate this type.
	 * 
	 * @return An instance of TypePattern or null.
	 */
	public TypePattern parsePossibleTypePattern(HashSet<String> environment,
			boolean terminated) {
		int start = index; // backtrack point
		// clone environment to prevent effects on calling context
		environment = new HashSet<String>(environment);
		try {
			TypePattern pattern = parseTypePattern(environment, terminated);
			// At this point, we have parsed a potential type pattern. However,
			// if it declares no variables then this could actually be an
			// expression and we need to return null. Therefore, count the
			// number of declared variables.
			HashSet<String> declared = new HashSet<String>();
			pattern.addDeclaredVariables(declared);
			// If the count of declared variables is non-zero, then definitely
			// not an expression. Otherwise, look to see whether the pattern
			// describes something which must be a type. If not, then fall
			// through and return null.
			if (declared.size() > 0
					|| mustParseAsType(pattern.toSyntacticType())) {
				return pattern;
			}
		} catch (SyntaxError e) {

		}
		index = start; // backtrack
		return null;
	}

	/**
	 * <p>
	 * Determine whether or not the given pattern can be parsed as an
	 * expression. In many cases, a type can (e.g. <code>{x}</code> is both a
	 * valid type and expression). However, some types are not also expressions
	 * (e.g. <code>int</code>, <code>{int f}</code>, <code>&int</code>, etc).
	 * </p>
	 * 
	 * <p>
	 * This function *must* return false if what the given pattern could not be
	 * parsed as an expression. However, if what it can be parsed as an
	 * expression, then this function must return false (even if we will
	 * eventually treat this as a type). This function is called from either the
	 * beginning of a statement (i.e. to disambiguate variable declarations), or
	 * after matching a left brace (i.e. to disambiguate casts).
	 * </p>
	 * 
	 * @param index
	 *            Position in the token stream to begin looking from.
	 * @return
	 */
	private boolean mustParseAsTypePattern(TypePattern pattern) {
		if (pattern instanceof TypePattern.Intersection) {
			TypePattern.Intersection tp = (TypePattern.Intersection) pattern;
			for (TypePattern el : tp.elements) {
				if (mustParseAsTypePattern(el)) {
					return true;
				}
			}
			return false;
		} else if (pattern instanceof TypePattern.Union) {
			TypePattern.Union tp = (TypePattern.Union) pattern;
			for (TypePattern el : tp.elements) {
				if (mustParseAsTypePattern(el)) {
					return true;
				}
			}
			return false;
		} else if (pattern instanceof TypePattern.Record) {
			return true;
		} else if (pattern instanceof TypePattern.Tuple) {
			TypePattern.Tuple tp = (TypePattern.Tuple) pattern;
			for (TypePattern el : tp.elements) {
				if (mustParseAsTypePattern(el)) {
					return true;
				}
			}
			return false;
		} else {
			TypePattern.Leaf leaf = (TypePattern.Leaf) pattern;
			return leaf.var != null || mustParseAsType(leaf.type);
		}
	}

	/**
	 * Parse top-level type pattern, which is of the form:
	 * 
	 * <pre>
	 * TypePattern ::= Type Ident
	 *              |  TypePattern [Ident]  ( ',' TypePattern [Ident] )*
	 *              |  TypePattern [Ident]  '/' TypePattern [Ident]
	 * </pre>
	 * 
	 * @param environment
	 *            Contains the set of variables previously declared in the
	 *            current type pattern. This is essentially used as a record in
	 *            order to spot invalid attempts to redeclare the same variables
	 *            (e.g. as in "int x, int x")
	 * @param terminated
	 *            This indicates that the type is known to be terminated (or
	 *            not). A type that's known to be terminated is one which is
	 *            guaranteed to be followed by something. This is important
	 *            because it means that we can ignore any newline characters
	 *            encountered in parsing this type, and that we'll never overrun
	 *            the end of the type (i.e. because there's guaranteed to be
	 *            something which terminates this type). A classic situation
	 *            where terminated is true is when parsing a type surrounded in
	 *            braces. In such case, we know the right-brace will always
	 *            terminate this type.
	 * @return
	 */
	private TypePattern parseTypePattern(HashSet<String> environment,
			boolean terminated) {
		int start = index;

		TypePattern leaf = parseUnionTypePattern(environment, terminated);
		leaf.addDeclaredVariables(environment);
		
		if (tryAndMatch(terminated, Comma) != null) {
			// Ok, this is a tuple type pattern
			ArrayList<TypePattern> result = new ArrayList<TypePattern>();
			result.add(leaf);			
			do {
				leaf = parseUnionTypePattern(environment, terminated);
				leaf.addDeclaredVariables(environment);
				result.add(leaf);
			} while (tryAndMatch(terminated, Comma) != null);

			// NOTE: The optional variable identifier must be null here as, if
			// one existed, it would be given to the element
			return new TypePattern.Tuple(result, null, sourceAttr(start,
					index - 1));
		} else {
			// this is just a leaf pattern
			return leaf;
		}
	}

	/**
	 * Parse a uniontype pattern "compound", which has the form:
	 * 
	 * <pre>
	 * UnionTypePattern ::= IntersectionTypePattern ('|' IntersectionTypePattern)*
	 * </pre>
	 * 
	 * @param environment
	 *            Contains the set of variables previously declared in the
	 *            current type pattern. This is essentially used as a record in
	 *            order to spot invalid attempts to redeclare the same variables
	 *            (e.g. as in "int x, int x")
	 * @param terminated
	 *            This indicates that the type is known to be terminated (or
	 *            not). A type that's known to be terminated is one which is
	 *            guaranteed to be followed by something. This is important
	 *            because it means that we can ignore any newline characters
	 *            encountered in parsing this type, and that we'll never overrun
	 *            the end of the type (i.e. because there's guaranteed to be
	 *            something which terminates this type). A classic situation
	 *            where terminated is true is when parsing a type surrounded in
	 *            braces. In such case, we know the right-brace will always
	 *            terminate this type.
	 * 
	 * @return
	 */
	public TypePattern parseUnionTypePattern(HashSet<String> environment,
			boolean terminated) {
		int start = index;
		TypePattern t = parseIntersectionTypePattern(environment, terminated);

		// Now, attempt to look for union and/or intersection types
		if (tryAndMatch(terminated, VerticalBar) != null) {
			// This is a union type
			ArrayList<TypePattern> types = new ArrayList<TypePattern>();
			types.add(t);
			do {
				types.add(parseIntersectionTypePattern(environment, terminated));
			} while (tryAndMatch(terminated, VerticalBar) != null);
			return new TypePattern.Union(types, null, sourceAttr(start,
					index - 1));
		} else {
			return t;
		}
	}

	/**
	 * Parse an intersection type pattern, which has the form:
	 * 
	 * <pre>
	 * IntersectionTypePattern ::= RationalTypePattern ('&' RationalTypePattern)*
	 * </pre>
	 * 
	 * @param environment
	 *            Contains the set of variables previously declared in the
	 *            current type pattern. This is essentially used as a record in
	 *            order to spot invalid attempts to redeclare the same variables
	 *            (e.g. as in "int x, int x")
	 * @param terminated
	 *            This indicates that the type is known to be terminated (or
	 *            not). A type that's known to be terminated is one which is
	 *            guaranteed to be followed by something. This is important
	 *            because it means that we can ignore any newline characters
	 *            encountered in parsing this type, and that we'll never overrun
	 *            the end of the type (i.e. because there's guaranteed to be
	 *            something which terminates this type). A classic situation
	 *            where terminated is true is when parsing a type surrounded in
	 *            braces. In such case, we know the right-brace will always
	 *            terminate this type.
	 * @return
	 */
	public TypePattern parseIntersectionTypePattern(
			HashSet<String> environment, boolean terminated) {
		int start = index;
		TypePattern t = parseRationalTypePattern(environment, terminated);

		// Now, attempt to look for union and/or intersection types
		if (tryAndMatch(terminated, Ampersand) != null) {
			// This is a union type
			ArrayList<TypePattern> types = new ArrayList<TypePattern>();
			types.add(t);
			do {
				types.add(parseRationalTypePattern(environment, terminated));
			} while (tryAndMatch(terminated, Ampersand) != null);
			return new TypePattern.Intersection(types, null, sourceAttr(start,
					index - 1));
		} else {
			return t;
		}
	}

	/**
	 * Parse a rational type pattern, which has the form:
	 * 
	 * <pre>
	 * RationalTypePattern ::= TypePatternTerm '/' TypePatternTerm
	 * </pre>
	 * 
	 * @param environment
	 *            Contains the set of variables previously declared in the
	 *            current type pattern. This is essentially used as a record in
	 *            order to spot invalid attempts to redeclare the same variables
	 *            (e.g. as in "int x, int x")
	 * @param terminated
	 *            This indicates that the type is known to be terminated (or
	 *            not). A type that's known to be terminated is one which is
	 *            guaranteed to be followed by something. This is important
	 *            because it means that we can ignore any newline characters
	 *            encountered in parsing this type, and that we'll never overrun
	 *            the end of the type (i.e. because there's guaranteed to be
	 *            something which terminates this type). A classic situation
	 *            where terminated is true is when parsing a type surrounded in
	 *            braces. In such case, we know the right-brace will always
	 *            terminate this type.
	 * @return
	 */
	public TypePattern parseRationalTypePattern(HashSet<String> environment,
			boolean terminated) {
		int start = index;
		TypePattern numerator = parseTypePatternTerm(environment, terminated);

		// Now, attempt to look for union and/or intersection types
		if (tryAndMatch(terminated, RightSlash) != null) {
			// This is a rational type pattern
			TypePattern denominator = parseTypePatternTerm(environment,
					terminated);
			boolean lhs = numerator.toSyntacticType() instanceof SyntacticType.Int;
			if (!lhs) {
				syntaxError("invalid numerator for rational pattern", numerator);
			}
			boolean rhs = denominator.toSyntacticType() instanceof SyntacticType.Int;
			if (!rhs) {
				syntaxError("invalid denominator for rational pattern",
						numerator);
			}
			return new TypePattern.Rational(numerator, denominator, null,
					sourceAttr(start, index - 1));
		} else {
			return numerator;
		}
	}

	/**
	 * Parse a type pattern leaf, which has the form:
	 * 
	 * <pre>
	 * TypePatternTerm ::= Type [Ident]
	 * </pre>
	 * 
	 * @param environment
	 *            Contains the set of variables previously declared in the
	 *            current type pattern. This is essentially used as a record in
	 *            order to spot invalid attempts to redeclare the same variables
	 *            (e.g. as in "int x, int x")
	 * @param terminated
	 *            This indicates that the type is known to be terminated (or
	 *            not). A type that's known to be terminated is one which is
	 *            guaranteed to be followed by something. This is important
	 *            because it means that we can ignore any newline characters
	 *            encountered in parsing this type, and that we'll never overrun
	 *            the end of the type (i.e. because there's guaranteed to be
	 *            something which terminates this type). A classic situation
	 *            where terminated is true is when parsing a type surrounded in
	 *            braces. In such case, we know the right-brace will always
	 *            terminate this type.
	 * @return
	 */
	public TypePattern parseTypePatternTerm(HashSet<String> environment,
			boolean terminated) {
		int start = index;
		TypePattern result;

		if (tryAndMatch(terminated, LeftBrace) != null) {
			// Bracketed type pattern
			result = parseTypePattern(environment, true);
			match(RightBrace);
			Expr.Variable name = parseTypePatternVar(terminated);
			if (name != null) {
				return new TypePattern.Leaf(result.toSyntacticType(), name,
						sourceAttr(start, index - 1));
			} else {
				return result;
			}
		} else if (tryAndMatch(terminated, LeftCurly) != null) {
			// Record, Set or Map type pattern

			// We could do better here in the case of record types which
			// have nested type patterns. However, it seems an unlikely use case
			// we just ignore it for now and acknowledge that, at some point, it
			// might be nice to do better.
			index = start; // backtrack
			SyntacticType type = parseSetOrMapOrRecordType();
			Expr.Variable name = parseTypePatternVar(terminated);
			if (name == null && type instanceof SyntacticType.Record) {
				return new TypePattern.Record((SyntacticType.Record) type,
						sourceAttr(start, index - 1));
			} else {
				return new TypePattern.Leaf(type, name, sourceAttr(start,
						index - 1));
			}
		} else {
			// Leaf
			SyntacticType type = parseType();
			Expr.Variable name = parseTypePatternVar(terminated);

			return new TypePattern.Leaf(type, name,
					sourceAttr(start, index - 1));
		}
	}

	public Expr.Variable parseTypePatternVar(boolean terminated) {
		// Now, try and match the optional variable identifier
		int start = index;
		Token id = tryAndMatch(terminated, Identifier);
		if (id != null) {
			return new Expr.Variable(id.text, sourceAttr(start, index - 1));
		} else {
			return null;
		}
	}

	/**
	 * Parse a top-level type, which is of the form:
	 * 
	 * <pre>
	 * TupleType ::= Type (',' Type)* 
	 * </pre>
	 * 
	 * @see wyc.lang.SyntacticType.Tuple
	 * @return
	 */
	private SyntacticType parseType() {
		int start = index;
		SyntacticType type = parseUnionType();

		if (tryAndMatch(true, Comma) != null) {
			// Match one or more types separated by commas
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			types.add(type);
			do {
				types.add(parseUnionType());
			} while (tryAndMatch(true, Comma) != null);

			return new SyntacticType.Tuple(types, sourceAttr(start, index - 1));
		} else {
			return type;
		}
	}
	
	/**
	 * Parse a unit (i.e. non-tuple type). This method is a place-hold which
	 * redirects to whatever the appropriate entry point for non-tuple types is.
	 * Note that tuple types can be parsed, but they must be bracketed.
	 * 
	 * @return
	 */
	private SyntacticType parseUnitType() {
		return parseUnionType();
	}
	/**
	 * Parse a union type, which is of the form:
	 * 
	 * <pre>
	 * UnionType ::= IntersectionType ('|' IntersectionType)*
	 * </pre>
	 * 
	 * @return
	 */
	private SyntacticType parseUnionType() {
		int start = index;
		SyntacticType t = parseIntersectionType();

		// Now, attempt to look for union and/or intersection types
		if (tryAndMatch(true, VerticalBar) != null) {
			// This is a union type
			ArrayList types = new ArrayList<SyntacticType>();
			types.add(t);
			do {
				types.add(parseIntersectionType());
			} while (tryAndMatch(true, VerticalBar) != null);
			return new SyntacticType.Union(types, sourceAttr(start, index - 1));
		} else {
			return t;
		}
	}

	/**
	 * Parse an intersection type, which is of the form:
	 * 
	 * <pre>
	 * IntersectionType ::= BaseType ('&' BaseType)*
	 * </pre>
	 * 
	 * @return
	 */
	private SyntacticType parseIntersectionType() {
		int start = index;
		SyntacticType t = parseBaseType();

		// Now, attempt to look for union and/or intersection types
		if (tryAndMatch(true, Ampersand) != null) {
			// This is a union type
			ArrayList types = new ArrayList<SyntacticType>();
			types.add(t);
			do {
				types.add(parseBaseType());
			} while (tryAndMatch(true, Ampersand) != null);
			return new SyntacticType.Intersection(types, sourceAttr(start,
					index - 1));
		} else {
			return t;
		}
	}

	private SyntacticType parseBaseType() {
		checkNotEof();
		int start = index;
		Token token = tokens.get(index);
		SyntacticType t;
		
		switch (token.kind) {
		case Void:
			return new SyntacticType.Void(sourceAttr(start, index++));
		case Any:
			return new SyntacticType.Any(sourceAttr(start, index++));
		case Null:
			return new SyntacticType.Null(sourceAttr(start, index++));
		case Bool:
			return new SyntacticType.Bool(sourceAttr(start, index++));
		case Char:
			return new SyntacticType.Char(sourceAttr(start, index++));
		case Int:
			return new SyntacticType.Int(sourceAttr(start, index++));
		case Real:
			return new SyntacticType.Real(sourceAttr(start, index++));
		case String:
			return new SyntacticType.Strung(sourceAttr(start, index++));
		case LeftBrace:
			return parseBracketedType();
		case LeftCurly:
			return parseSetOrMapOrRecordType();
		case LeftSquare:
			return parseListType();
		case Shreak:
			return parseNegationType();
		case Identifier:
			return parseNominalType();
		default:
			syntaxError("unknown type encountered", token);
			return null;
		}
	}

	/**
	 * Parse a negation type, which is of the form:
	 * 
	 * <pre>
	 * NegationType ::= '!' Type
	 * </pre>
	 * 
	 * @return
	 */
	private SyntacticType parseNegationType() {
		int start = index;
		match(Shreak);
		SyntacticType element = parseType();
		return new SyntacticType.Negation(element, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a bracketed type, which is of the form:
	 * 
	 * <pre>
	 * BracketedType ::= '(' Type ')'
	 * </pre>
	 * 
	 * @return
	 */
	private SyntacticType parseBracketedType() {
		int start = index;
		match(LeftBrace);
		SyntacticType type = parseType();
		match(RightBrace);		
		return type;
	}

	/**
	 * Parse a list type, which is of the form:
	 * 
	 * <pre>
	 * ListType ::= '[' Type ']'
	 * </pre>
	 * 
	 * @return
	 */
	private SyntacticType parseListType() {
		int start = index;
		match(LeftSquare);
		SyntacticType element = parseType();
		match(RightSquare);
		return new SyntacticType.List(element, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a set, map or record type, which are of the form:
	 * 
	 * <pre>
	 * SetType ::= '{' Type '}'
	 * MapType ::= '{' Type "=>" Type '}'
	 * RecordType ::= '{' Type Identifier (',' Type Identifier)* [ ',' "..." ] '}'
	 * </pre>
	 * 
	 * Disambiguating these three forms is relatively straightforward as all
	 * three must be terminated by a right curly brace. Therefore, after parsing
	 * the first Type, we simply check what follows. One complication is the
	 * potential for "mixed types" where the field name and type and intertwined
	 * (e.g. function read()=>[byte]).
	 * 
	 * @return
	 */
	private SyntacticType parseSetOrMapOrRecordType() {
		int start = index;
		match(LeftCurly);

		// First, we need to disambiguate between a set, map or record type. The
		// complication is the potential for mixed types. For example, when
		// parsing "{ function f(int)=>int }", the first element is not a type.
		// Therefore, we have to first decide whether or not we have a mixed
		// type, or a normal type.

		if (!mustParseAsMixedType()) {
			int t_start = index; // backtrack point

			SyntacticType type = parseType();

			if (tryAndMatch(true, RightCurly) != null) {
				// This indicates a set type was encountered.
				return new SyntacticType.Set(type, sourceAttr(start, index - 1));
			} else if (tryAndMatch(true, EqualsGreater) != null) {
				// This indicates a map type was encountered.
				SyntacticType value = parseType();
				match(RightCurly);
				return new SyntacticType.Map(type, value, sourceAttr(start,
						index - 1));
			}
			// At this point, we definitely have a record type (or an error).
			// Therefore, we backtrack and parse the potentially mixed type
			// properly.
			index = t_start; // backtrack
		}

		HashMap<String, SyntacticType> types = new HashMap<String, SyntacticType>();
		// Otherwise, we have a record type and we must continue to parse
		// the remainder of the first field.

		Pair<SyntacticType, Token> p = parseMixedType();
		types.put(p.second().text, p.first());

		// Now, we continue to parse any remaining fields.
		boolean isOpen = false;
		while (eventuallyMatch(RightCurly) == null) {
			match(Comma);

			if (tryAndMatch(true, DotDotDot) != null) {
				// this signals an "open" record type
				match(RightCurly);
				isOpen = true;
				break;
			} else {
				p = parseMixedType();
				Token id = p.second();
				if (types.containsKey(id.text)) {
					syntaxError("duplicate record key", id);
				}
				types.put(id.text, p.first());
			}
		}
		// Done
		return new SyntacticType.Record(isOpen, types, sourceAttr(start,
				index - 1));
	}


	/**
	 * Parse a nominal type, which is of the form:
	 * 
	 * <pre>
	 * NominalType ::= Identifier ('.' Identifier)*
	 * </pre>
	 * 
	 * @see wyc.lang.SyntacticType.Nominal
	 * @return
	 */
	private SyntacticType parseNominalType() {
		int start = index;
		ArrayList<String> names = new ArrayList<String>();

		// Match one or more identifiers separated by dots
		do {
			names.add(match(Identifier).text);
		} while (tryAndMatch(true, Dot) != null);

		return new SyntacticType.Nominal(names, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a potentially mixed-type, which is of the form:
	 * 
	 * <pre>
	 * MixedType ::= Type Identifier
	 *            |  "function" Type Identifier '(' [Type (',' Type)* ] ')' "=>" Type [ "throws" Type ]
	 *            |  "method" Type Identifier '(' [Type (',' Type)* ] ')' "=>" Type [ "throws" Type ]
	 * </pre>
	 * 
	 * @return
	 */
	private Pair<SyntacticType, Token> parseMixedType() {
		Token lookahead;
		int start = index;

		if ((lookahead = tryAndMatch(true, Function)) != null) {
			// At this point, we *might* have a mixed function / method type
			// definition. To disambiguate, we need to see whether an identifier
			// follows or not.
			Token id = tryAndMatch(true, Identifier);

			if (id != null) {
				// Yes, we have found a mixed function / method type definition.
				// Therefore, we continue to pass the remaining type parameters.

				ArrayList<SyntacticType> paramTypes = new ArrayList<SyntacticType>();
				match(LeftBrace);

				boolean firstTime = true;
				while (eventuallyMatch(RightBrace) == null) {
					if (!firstTime) {
						match(Comma);
					}
					firstTime = false;
					paramTypes.add(parseUnitType());
				}

				SyntacticType ret;

				if (lookahead.kind == Function) {
					// Functions require a return type (since otherwise they are
					// just nops)
					match(EqualsGreater);
					// Third, parse the return type
					ret = parseUnitType();

				} else if (tryAndMatch(true, EqualsGreater) != null) {
					// Third, parse the (optional) return type. Observe that
					// this is forced to be a
					// unit type. This means that any tuple return types must be
					// in braces. The reason for this is that a trailing comma
					// may be part of an enclosing record type and we must
					// disambiguate
					// this.
					ret = parseUnitType();										
				} else {
					// If no return is given, then default to void.
					ret = new SyntacticType.Void();
				}
				// Fourth, parse the optional throws type
				SyntacticType throwsType = null;
				if (tryAndMatch(true, Throws) != null) {
					throwsType = parseType();
				}

				// Done
				SyntacticType type;
				if (lookahead.kind == Token.Kind.Function) {
					type = new SyntacticType.Function(ret, throwsType,
							paramTypes, sourceAttr(start, index - 1));
				} else {
					type = new SyntacticType.Method(ret, throwsType,
							paramTypes, sourceAttr(start, index - 1));
				}
				return new Pair<SyntacticType, Token>(type, id);
			} else {
				// In this case, we failed to match a mixed type. Therefore, we
				// backtrack and parse as two separate items (i.e. type
				// identifier).
				index = start; // backtrack
			}
		}

		// This is the normal case, where we expect an identifier to follow the
		// type.
		SyntacticType type = parseType();
		Token id = match(Identifier);
		return new Pair<SyntacticType, Token>(type, id);
	}

	public boolean mustParseAsMixedType() {
		int start = index;
		if (tryAndMatch(true, Function, Method) != null
				&& tryAndMatch(true, Identifier) != null) {
			// Yes, this is a mixed type
			index = start;
			return true;
		} else {
			// No, this is not a mixed type
			index = start;
			return false;
		}
	}

	/**
	 * Match a given token kind, whilst moving passed any whitespace encountered
	 * inbetween. In the case that meet the end of the stream, or we don't match
	 * the expected token, then an error is thrown.
	 * 
	 * @param kind
	 * @return
	 */
	private Token match(Token.Kind kind) {
		checkNotEof();
		Token token = tokens.get(index++);
		if (token.kind != kind) {
			syntaxError("expecting \"" + kind + "\" here", token);
		}
		return token;
	}

	/**
	 * Match a given sequence of tokens, whilst moving passed any whitespace
	 * encountered inbetween. In the case that meet the end of the stream, or we
	 * don't match the expected tokens in the expected order, then an error is
	 * thrown.
	 * 
	 * @param operator
	 * @return
	 */
	private Token[] match(Token.Kind... kinds) {
		Token[] result = new Token[kinds.length];
		for (int i = 0; i != result.length; ++i) {
			checkNotEof();
			Token token = tokens.get(index++);
			if (token.kind == kinds[i]) {
				result[i] = token;
			} else {
				syntaxError("Expected \"" + kinds[i] + "\" here", token);
			}
		}
		return result;
	}

	/**
	 * Attempt to match a given kind of token with the view that it must
	 * *eventually* be matched. This differs from <code>tryAndMatch()</code>
	 * because it calls <code>checkNotEof()</code>. Thus, it is guaranteed to
	 * skip any whitespace encountered in between. This is safe because we know
	 * there is a terminating token still to come.
	 * 
	 * @param kind
	 * @return
	 */
	private Token eventuallyMatch(Token.Kind kind) {
		checkNotEof();
		Token token = tokens.get(index);
		if (token.kind != kind) {
			return null;
		} else {
			index = index + 1;
			return token;
		}
	}

	/**
	 * Attempt to match a given token(s), whilst ignoring any whitespace in
	 * between. Note that, in the case it fails to match, then the index will be
	 * unchanged. This latter point is important, otherwise we could
	 * accidentally gobble up some important indentation. If more than one kind
	 * is provided then this will try to match any of them.
	 * 
	 * @param terminated
	 *            Indicates whether or not this function should be concerned
	 *            with new lines. The terminated flag indicates whether or not
	 *            the current construct being parsed is known to be terminated.
	 *            If so, then we don't need to worry about newlines and can
	 *            greedily consume them (i.e. since we'll eventually run into
	 *            the terminating symbol).
	 * @param kinds
	 * 
	 * @return
	 */
	private Token tryAndMatch(boolean terminated, Token.Kind... kinds) {
		// If the construct being parsed is know to be terminated, then we can
		// skip all whitespace. Otherwise, we can't skip newlines as these are
		// significant.
		int next = terminated ? skipWhiteSpace(index) : skipLineSpace(index);

		if (next < tokens.size()) {
			Token t = tokens.get(next);
			for (int i = 0; i != kinds.length; ++i) {
				if (t.kind == kinds[i]) {
					index = next + 1;
					return t;
				}
			}
		}
		return null;
	}

	/**
	 * Attempt to match a given token on the *same* line, whilst ignoring any
	 * whitespace in between. Note that, in the case it fails to match, then the
	 * index will be unchanged. This latter point is important, otherwise we
	 * could accidentally gobble up some important indentation.
	 * 
	 * @param kind
	 * @return
	 */
	private Token tryAndMatchOnLine(Token.Kind kind) {
		int next = skipLineSpace(index);
		if (next < tokens.size()) {
			Token t = tokens.get(next);
			if (t.kind == kind) {
				index = next + 1;
				return t;
			}
		}
		return null;
	}

	/**
	 * Match a the end of a line. This is required to signal, for example, the
	 * end of the current statement.
	 */
	private void matchEndLine() {
		// First, parse all whitespace characters except for new lines
		index = skipLineSpace(index);

		// Second, check whether we've reached the end-of-file (as signaled by
		// running out of tokens), or we've encountered some token which not a
		// newline.
		if (index >= tokens.size()) {
			return; // EOF
		} else if (tokens.get(index).kind != NewLine) {
			syntaxError("expected end-of-line", tokens.get(index));
		} else {
			index = index + 1;
		}
	}

	/**
	 * Check that the End-Of-File has not been reached. This method should be
	 * called from contexts where we are expecting something to follow.
	 */
	private void checkNotEof() {
		skipWhiteSpace();
		if (index >= tokens.size()) {
			throw new SyntaxError("unexpected end-of-file", filename,
					index - 1, index - 1);
		}
	}

	/**
	 * Skip over any whitespace characters.
	 */
	private void skipWhiteSpace() {
		index = skipWhiteSpace(index);
	}

	/**
	 * Skip over any whitespace characters, starting from a given index and
	 * returning the first index passed any whitespace encountered.
	 */
	private int skipWhiteSpace(int index) {
		while (index < tokens.size() && isWhiteSpace(tokens.get(index))) {
			index++;
		}
		return index;
	}

	/**
	 * Skip over any whitespace characters that are permitted on a given line
	 * (i.e. all except newlines), starting from a given index and returning the
	 * first index passed any whitespace encountered.
	 */
	private int skipLineSpace(int index) {
		while (index < tokens.size() && isLineSpace(tokens.get(index))) {
			index++;
		}
		return index;
	}

	/**
	 * Skip over any empty lines. That is lines which contain only whitespace
	 * and comments.
	 */
	private void skipEmptyLines() {
		int tmp = index;
		do {
			tmp = skipLineSpace(tmp);
			if (tmp < tokens.size()
					&& tokens.get(tmp).kind != NewLine) {
				return; // done
			} else if(tmp >= tokens.size()) {
				index = tmp;
				return; // end-of-file reached
			}
			// otherwise, skip newline and continue
			tmp = tmp + 1;
			index = tmp;
		} while (true);
		// deadcode
	}

	/**
	 * Define what is considered to be whitespace.
	 * 
	 * @param token
	 * @return
	 */
	private boolean isWhiteSpace(Token token) {
		return token.kind == NewLine || isLineSpace(token);
	}

	/**
	 * Define what is considered to be linespace.
	 * 
	 * @param token
	 * @return
	 */
	private boolean isLineSpace(Token token) {
		return token.kind == Token.Kind.Indent
				|| token.kind == Token.Kind.LineComment
				|| token.kind == Token.Kind.BlockComment;
	}

	/**
	 * Parse a character from a string of the form 'c' or '\c'.
	 * 
	 * @param input
	 * @return
	 */
	private char parseCharacter(String input) {
		int pos = 1;
		char c = input.charAt(pos++);
		if (c == '\\') {
			// escape code
			switch (input.charAt(pos++)) {
			case 'b':
				c = '\b';
				break;
			case 't':
				c = '\t';
				break;
			case 'n':
				c = '\n';
				break;
			case 'f':
				c = '\f';
				break;
			case 'r':
				c = '\r';
				break;
			case '"':
				c = '\"';
				break;
			case '\'':
				c = '\'';
				break;
			case '\\':
				c = '\\';
				break;			
			default:
				throw new RuntimeException("unrecognised escape character");
			}
		}
		return c;
	}

	/**
	 * Parse a string whilst interpreting all escape characters.
	 * 
	 * @param v
	 * @return
	 */
	protected String parseString(String v) {
		/*
		 * Parsing a string requires several steps to be taken. First, we need
		 * to strip quotes from the ends of the string.
		 */
		v = v.substring(1, v.length() - 1);
		// Second, step through the string and replace escaped characters
		for (int i = 0; i < v.length(); i++) {
			if (v.charAt(i) == '\\') {
				if (v.length() <= i + 1) {
					throw new RuntimeException("unexpected end-of-string");
				} else {
					char replace = 0;
					int len = 2;
					switch (v.charAt(i + 1)) {
					case 'b':
						replace = '\b';
						break;
					case 't':
						replace = '\t';
						break;
					case 'n':
						replace = '\n';
						break;
					case 'f':
						replace = '\f';
						break;
					case 'r':
						replace = '\r';
						break;
					case '"':
						replace = '\"';
						break;
					case '\'':
						replace = '\'';
						break;
					case '\\':
						replace = '\\';
						break;
					case 'u':
						len = 6; // unicode escapes are six digits long,
						// including "slash u"
						String unicode = v.substring(i + 2, i + 6);
						replace = (char) Integer.parseInt(unicode, 16); // unicode
						break;
					default:
						throw new RuntimeException("unknown escape character");
					}
					v = v.substring(0, i) + replace + v.substring(i + len);
				}
			}
		}
		return v;
	}

	/**
	 * Parse a token representing a byte value. Every such token is a sequence
	 * of one or more binary digits ('0' or '1') followed by 'b'. For example,
	 * "00110b" is parsed as the byte value 6.
	 * 
	 * @param input
	 *            The token representing the byte value.
	 * @return
	 */
	private byte parseByte(Token input) {
		String text = input.text;
		if (text.length() > 9) {
			syntaxError("invalid binary literal (too long)", input);
		}
		int val = 0;
		for (int i = 0; i != text.length() - 1; ++i) {
			val = val << 1;
			char c = text.charAt(i);
			if (c == '1') {
				val = val | 1;
			} else if (c == '0') {

			} else {
				syntaxError("invalid binary literal (invalid characters)",
						input);
			}
		}
		return (byte) val;
	}

	private Attribute.Source sourceAttr(int start, int end) {
		Token t1 = tokens.get(start);
		Token t2 = tokens.get(end);
		// FIXME: problem here with the line numbering ?
		return new Attribute.Source(t1.start, t2.end(), 0);
	}

	private void syntaxError(String msg, SyntacticElement e) {
		Attribute.Source loc = e.attribute(Attribute.Source.class);
		throw new SyntaxError(msg, filename, loc.start, loc.end);
	}

	private void syntaxError(String msg, Token t) {
		throw new SyntaxError(msg, filename, t.start, t.start + t.text.length()
				- 1);
	}

	/**
	 * Represents a given amount of indentation. Specifically, a count of tabs
	 * and spaces. Observe that the order in which tabs / spaces occurred is not
	 * retained.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	private static class Indent extends Token {
		private final int countOfSpaces;
		private final int countOfTabs;

		public Indent(String text, int pos) {
			super(Token.Kind.Indent, text, pos);
			// Count the number of spaces and tabs
			int nSpaces = 0;
			int nTabs = 0;
			for (int i = 0; i != text.length(); ++i) {
				char c = text.charAt(i);
				switch (c) {
				case ' ':
					nSpaces++;
					break;
				case '\t':
					nTabs++;
					break;
				default:
					throw new IllegalArgumentException(
							"Space or tab character expected");
				}
			}
			countOfSpaces = nSpaces;
			countOfTabs = nTabs;
		}

		/**
		 * Test whether this indentation is considered "less than or equivalent"
		 * to another indentation. For example, an indentation of 2 spaces is
		 * considered less than an indentation of 3 spaces, etc.
		 * 
		 * @param other
		 *            The indent to compare against.
		 * @return
		 */
		public boolean lessThanEq(Indent other) {
			return countOfSpaces <= other.countOfSpaces
					&& countOfTabs <= other.countOfTabs;
		}

		/**
		 * Test whether this indentation is considered "equivalent" to another
		 * indentation. For example, an indentation of 3 spaces followed by 1
		 * tab is considered equivalent to an indentation of 1 tab followed by 3
		 * spaces, etc.
		 * 
		 * @param other
		 *            The indent to compare against.
		 * @return
		 */
		public boolean equivalent(Indent other) {
			return countOfSpaces == other.countOfSpaces
					&& countOfTabs == other.countOfTabs;
		}
	}

	/**
	 * An abstract indentation which represents the indentation of top-level
	 * declarations, such as function declarations. This is used to simplify the
	 * code for parsing indentation.
	 */
	private static final Indent ROOT_INDENT = new Indent("", 0);
}
