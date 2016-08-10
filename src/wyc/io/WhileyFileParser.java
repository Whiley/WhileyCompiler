// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyc.io;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wybs.lang.Attribute;
import wybs.lang.NameID;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wyc.lang.*;
import wyc.lang.Expr.ConstantAccess;
import wyc.io.WhileyFileLexer.Token;
import static wyil.util.ErrorMessages.*;
import static wybs.lang.SyntaxError.*;
import static wyc.io.WhileyFileLexer.Token.Kind.*;

import wyc.lang.WhileyFile.*;
import wycommon.util.Pair;
import wycommon.util.Triple;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.Modifier;
import wyil.lang.Constant;

/**
 * Convert a list of tokens into an Abstract Syntax Tree (AST) representing the
 * original source file in question. No effort is made to check whether or not
 * the generated tree is syntactically correct. Subsequent stages of the
 * compiler are responsible for doing this.
 *
 * @author David J. Pearce
 *
 */
public class WhileyFileParser {
	private final Path.Entry<WhileyFile> entry;
	private ArrayList<Token> tokens;
	private int index;

	public WhileyFileParser(Path.Entry<WhileyFile> entry, List<Token> tokens) {
		this.entry = entry;
		this.tokens = new ArrayList<Token>(tokens);
	}

	/**
	 * Read a <code>WhileyFile</code> from the token stream. If the stream is
	 * invalid in some way (e.g. contains a syntax error, etc) then a
	 * <code>SyntaxError</code> is thrown.
	 *
	 * @return
	 */
	public WhileyFile read() {
		Path.ID pkg = parsePackage();
		WhileyFile wf = new WhileyFile(entry);
		// FIXME: check package is consistent?

		skipWhiteSpace();
		while (index < tokens.size()) {
			Token lookahead = tokens.get(index);
			if (lookahead.kind == Import) {
				parseImportDeclaration(wf);
			} else {
				List<Modifier> modifiers = parseModifiers();
				checkNotEof();
				lookahead = tokens.get(index);
				if (lookahead.text.equals("type")) {
					parseTypeDeclaration(wf, modifiers);
				} else if (lookahead.text.equals("constant")) {
					parseConstantDeclaration(wf, modifiers);
				} else if (lookahead.kind == Function) {
					parseFunctionOrMethodDeclaration(wf, modifiers, true);
				} else if (lookahead.kind == Method) {
					parseFunctionOrMethodDeclaration(wf, modifiers, false);
				} else {
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
	 */
	private void parseImportDeclaration(WhileyFile wf) {
		int start = index;

		match(Import);

		// First, parse "from" usage (if applicable)
		Token token = tryAndMatch(true, Identifier, Star);
		if (token == null) {
			syntaxError("expected identifier or '*' here", tokens.get(index));
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

		wf.add(new WhileyFile.Import(filter, name, sourceAttr(start, end - 1)));
	}

	private List<Modifier> parseModifiers() {
		ArrayList<Modifier> mods = new ArrayList<Modifier>();
		Token lookahead;
		boolean visible = false;
		while ((lookahead = tryAndMatch(true, Public, Private,
				Native, Export)) != null) {
			switch(lookahead.kind) {
			case Public:
			case Private:
				if(visible) {
					syntaxError("visibility modifier already given",lookahead);
				}
			}
			switch (lookahead.kind) {
			case Public:
				mods.add(Modifier.PUBLIC);
				visible = true;
				break;
			case Private:
				mods.add(Modifier.PRIVATE);
				visible = true;
				break;
			case Native:
				mods.add(Modifier.NATIVE);
				break;
			case Export:
				mods.add(Modifier.EXPORT);
				break;
			}
		}
		return mods;
	}

	/**
	 * Parse a <i>function declaration</i> or <i>method declaration</i>, which
	 * have the form:
	 *
	 * <pre>
	 * FunctionDeclaration ::= "function" TypePattern "->" TypePattern (FunctionMethodClause)* ':' NewLine Block
	 *
	 * MethodDeclaration ::= "method" TypePattern "->" TypePattern (FunctionMethodClause)* ':' NewLine Block
	 *
	 * FunctionMethodClause ::= "requires" Expr | "ensures" Expr
	 * </pre>
	 *
	 * Here, the first type pattern (i.e. before "->") is referred to as the
	 * "parameter", whilst the second is referred to as the "return". There are
	 * two kinds of option clause:
	 *
	 * <ul>
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
	 * function max(int x, int y) -> (int z)
	 * // return must be greater than either parameter
	 * ensures x <= z && y <= z
	 * // return must equal one of the parmaeters
	 * ensures x == z || y == z:
	 *     ...
	 * </pre>
	 *
	 * <p>
	 * Here, we see the specification for the well-known <code>max()</code>
	 * function which returns the largest of its parameters. This does not throw
	 * any exceptions, and does not enforce any preconditions on its parameters.
	 * </p>
	 */
	private void parseFunctionOrMethodDeclaration(WhileyFile wf,
			List<Modifier> modifiers, boolean isFunction) {
		int start = index;

		EnclosingScope scope = new EnclosingScope();
		List<String> lifetimeParameters;

		if (isFunction) {
			match(Function);
			lifetimeParameters = Collections.emptyList();
		} else {
			match(Method);

			// Lifetime parameters
			lifetimeParameters = parseOptionalLifetimeParameters(scope);
		}

		Token name = match(Identifier);

		// Parse function or method parameters
		List<Parameter> parameters = parseParameters(wf,scope);
		
		// Parse (optional) return type
		List<Parameter> returns = Collections.EMPTY_LIST;

		if (tryAndMatch(true, MinusGreater) != null) {
			// Explicit return type is given, so parse it! We first clone the
			// environent and create a special one only for use within ensures
			// clauses, since these are the only expressions which may refer to
			// variables declared in the return type.
			returns = parseOptionalParameters(wf,scope);		
		} 

		// Parse optional requires/ensures clauses
		ArrayList<Expr> requires = new ArrayList<Expr>();
		ArrayList<Expr> ensures = new ArrayList<Expr>();
		
		Token lookahead;
		while ((lookahead = tryAndMatch(true, Requires, Ensures)) != null) {
			switch (lookahead.kind) {
			case Requires:
				// NOTE: expression terminated by ':'
				requires.add(parseLogicalExpression(wf, scope, true));
				break;
			case Ensures:
				// Use the ensuresEnvironment here to get access to any
				// variables declared in the return type pattern.
				// NOTE: expression terminated by ':'
				ensures.add(parseLogicalExpression(wf, scope, true));
				break;
			}
		}

		// At this point, we need to decide whether or there is a method body.
		List<Stmt> stmts;
		int end;

		if (modifiers.contains(Modifier.NATIVE)) {
			// This is a native function or method which does not have a body.
			end = index;
			matchEndLine();
			stmts = Collections.EMPTY_LIST;
		} else {
			match(Colon);
			end = index;
			matchEndLine();
			scope.declareThisLifetime();
			stmts = parseBlock(wf, scope, false);
		}

		WhileyFile.Declaration declaration;
		if (isFunction) {
			declaration = wf.new Function(modifiers, name.text, returns, parameters, requires, ensures, stmts,
					sourceAttr(start, end - 1));
		} else {
			declaration = wf.new Method(modifiers, name.text, returns, parameters,
					lifetimeParameters, requires, ensures, stmts, sourceAttr(start, end - 1));
		}
		wf.add(declaration);
	}

	public List<Parameter> parseParameters(WhileyFile wf, EnclosingScope scope) {
		match(LeftBrace);
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		boolean firstTime = true;
		while (eventuallyMatch(RightBrace) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			int pStart = index;
			Pair<SyntacticType, Token> p = parseMixedType(scope);
			Token id = p.second();
			scope.declareVariable(id);
			parameters.add(wf.new Parameter(p.first(), id.text, sourceAttr(
					pStart, index - 1)));			
		}
		return parameters;
	}
	

	public List<Parameter> parseOptionalParameters(WhileyFile wf, EnclosingScope scope) {
		int next = skipWhiteSpace(index);
		if(next < tokens.size() && tokens.get(next).kind == LeftBrace) {
			return parseParameters(wf, scope);
		} else {			
			Parameter p = parseOptionalParameter(wf, scope);
			ArrayList<Parameter> ps = new ArrayList<Parameter>();
			ps.add(p);
			return ps;
		}
	}
	
	public Parameter parseOptionalParameter(WhileyFile wf, EnclosingScope scope) {
		int start = index;
		boolean braced = false;
		SyntacticType type;
		String name;
		if(tryAndMatch(true,LeftBrace) != null) {		
			Pair<SyntacticType, Token> p = parseMixedType(scope);
			type = p.first();
			name = p.second().text;
			scope.declareVariable(p.second());
			match(RightBrace);
		} else {
			type = parseType(scope);
			name = null;
		}
		return wf.new Parameter(type, name, sourceAttr(start, index - 1));
	}	
	
	/**
	 * Parse a type declaration in a Whiley source file, which has the form:
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
	 * @see wyc.lang.WhileyFile.Type
	 *
	 * @param wf
	 *            --- The Whiley file in which this declaration is defined.
	 * @param modifiers
	 *            --- The list of modifiers for this declaration (which were
	 *            already parsed before this method was called).
	 */
	public void parseTypeDeclaration(WhileyFile wf, List<Modifier> modifiers) {
		int start = index;
		// Match identifier rather than kind e.g. Type to avoid "type" being a
		// keyword.
		match(Identifier);
		//
		Token name = match(Identifier);
		match(Is);
		// Parse the type pattern
		EnclosingScope scope = new EnclosingScope();
		Parameter p = parseOptionalParameter(wf, scope);				
		ArrayList<Expr> invariant = new ArrayList<Expr>();
		// Check whether or not there is an optional "where" clause.
		while (tryAndMatch(true, Where) != null) {
			// Yes, there is a "where" clause so parse the constraint. First,
			// construct the environment which will be used to identify the set
			// of declared variables in the current scope.
			invariant.add(parseLogicalExpression(wf, scope, false));
		}
		int end = index;
		matchEndLine();
		WhileyFile.Declaration declaration = wf.new Type(modifiers, p, name.text, invariant,
				sourceAttr(start, end - 1));
		wf.add(declaration);
		return;
	}

	/**
	 * Parse a constant declaration in a Whiley source file, which has the form:
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
	 * @see wyc.lang.WhileyFile.Constant
	 *
	 * @param wf
	 *            --- The Whiley file in which this declaration is defined.
	 * @param modifiers
	 *            --- The list of modifiers for this declaration (which were
	 *            already parsed before this method was called).
	 */
	private void parseConstantDeclaration(WhileyFile wf,
			List<Modifier> modifiers) {
		int start = index;
		// Match identifier rather than kind e.g. constant to avoid "constant"
		// being a
		// keyword.
		match(Identifier);
		//
		Token name = match(Identifier);
		match(Is);
		Expr e = parseExpression(wf, new EnclosingScope(), false);
		int end = index;
		matchEndLine();
		WhileyFile.Declaration declaration = wf.new Constant(modifiers, e,
				name.text, sourceAttr(start, end - 1));
		wf.add(declaration);
	}

	/**
	 * Parse a block of zero or more statements which share the same indentation
	 * level. Their indentation level must be strictly greater than that of
	 * their parent, otherwise the end of block is signaled. The <i>indentation
	 * level</i> for the block is set by the first statement encountered
	 * (assuming their is one). An error occurs if a subsequent statement is
	 * reached with an indentation level <i>greater</i> than the block's
	 * indentation level.
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param parentIndent
	 *            The indentation level of the parent, for which all statements
	 *            in this block must have a greater indent. May not be
	 *            <code>null</code>.
	 * @param isLoop
	 *            Indicates whether or not this block represents the body of a
	 *            loop. This is important in order to setup the scope for this
	 *            block appropriately.
	 * @return
	 */
	private List<Stmt> parseBlock(WhileyFile wf, EnclosingScope scope, boolean isLoop) {

		// First, determine the initial indentation of this block based on the
		// first statement (or null if there is no statement).
		Indent indent = getIndent();

		// We must clone the environment here, in order to ensure variables
		// declared within this block are properly scoped.
		EnclosingScope blockScope = scope.newEnclosingScope(indent, isLoop);		
		
		// Second, check that this is indeed the initial indentation for this
		// block (i.e. that it is strictly greater than parent indent).
		if (indent == null || indent.lessThanEq(scope.getIndent())) {
			// Initial indent either doesn't exist or is not strictly greater
			// than parent indent and,therefore, signals an empty block.
			//
			return Collections.EMPTY_LIST;
		} else {
			// Initial indent is valid, so we proceed parsing statements with
			// the appropriate level of indent.
			//
			ArrayList<Stmt> stmts = new ArrayList<Stmt>();
			Indent nextIndent;
			while ((nextIndent = getIndent()) != null
					&& indent.lessThanEq(nextIndent)) {
				// At this point, nextIndent contains the indent of the current
				// statement. However, this still may not be equivalent to this
				// block's indentation level.

				// First, check the indentation matches that for this block.
				if (!indent.equivalent(nextIndent)) {
					// No, it's not equivalent so signal an error.
					syntaxError("unexpected end-of-block", nextIndent);
				}

				// Second, parse the actual statement at this point!
				stmts.add(parseStatement(wf, blockScope));
			}

			return stmts;
		}
	}

	/**
	 * Determine the indentation as given by the Indent token at this point (if
	 * any). If none, then <code>null</code> is returned.
	 *
	 * @return
	 */
	private Indent getIndent() {
		skipEmptyLines();
		if (index < tokens.size()) {
			Token token = tokens.get(index);
			if (token.kind == Indent) {
				return new Indent(token.text, token.start);
			}
			return null;
		}
		return null;
	}

	/**
	 * Parse a given statement. There are essentially two forms of statement:
	 * <code>simple</code> and <code>compound</code>. Simple statements (e.g.
	 * assignment, <code>debug</code>, etc) are terminated by a
	 * <code>NewLine</code> token, although they may span multiple lines if an
	 * expression does. Compound statements (e.g. <code>if</code>,
	 * <code>while</code>, etc) themselves contain blocks of statements and are
	 * not (generally) terminated by a <code>NewLine</code>.
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private Stmt parseStatement(WhileyFile wf, EnclosingScope scope) {
		checkNotEof();
		Token lookahead = tokens.get(index);

		// First, attempt to parse the easy statement forms.

		switch (lookahead.kind) {
		case Assert:
			return parseAssertStatement(wf, scope);
		case Assume:
			return parseAssumeStatement(wf, scope);
		case Break:
			return parseBreakStatement(scope);
		case Continue:
			return parseContinueStatement(scope);
		case Do:
			return parseDoWhileStatement(wf, scope);
		case Debug:
			return parseDebugStatement(wf, scope);
		case Fail:
			return parseFailStatement(scope);
		case If:
			return parseIfStatement(wf, scope);
		case Return:
			return parseReturnStatement(wf, scope);
		case While:
			return parseWhileStatement(wf, scope);
		case Skip:
			return parseSkipStatement(scope);
		case Switch:
			return parseSwitchStatement(wf, scope);
		default:
			// fall through to the more difficult cases
		}

		// At this point, we have three possibilities remaining: variable
		// declaration, invocation, assignment, or a named block.
		// The latter one can be detected easily as it is just an identifier
		// followed by a colon. To disambiguate the remaining cases, we
		// first determine whether or not what follows *must* be parsed as a
		// type (i.e. parsing it as an expression would fail). If so, then it
		// must be a variable declaration that follows. Otherwise, it can still
		// be *any* of the three forms, but we definitely have an
		// expression-like thing at this point. Therefore, we parse that
		// expression and see what this gives and/or what follows...
		return parseHeadlessStatement(wf, scope);
	}

	/**
	 * A headless statement is one which has no identifying keyword. The set of
	 * headless statements include assignments, invocations, variable
	 * declarations and named blocks.
	 * 
	 * @param wf
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private Stmt parseHeadlessStatement(WhileyFile wf, EnclosingScope scope) {
		int start = index;

		// See if it is a named block
		Token blockName = tryAndMatch(true, Identifier);
		if (blockName != null) {
			if (tryAndMatch(true, Colon) != null && isAtEOL()) {
				int end = index;
				matchEndLine();
				scope = scope.newEnclosingScope();
				scope.declareLifetime(blockName);

				List<Stmt> body = parseBlock(wf, scope, false);
				return new Stmt.NamedBlock(blockName.text, body, sourceAttr(start, end - 1));
			} else {
				index = start; // backtrack
			}
		}

		// Remaining cases: assignments, invocations and variable declarations
		SyntacticType type = parseDefiniteType(scope);

		if (type == null) {
			// Can still be a variable declaration, assignment or invocation.
			Expr e = parseExpression(wf, scope, false);
			if (e instanceof Expr.AbstractInvoke || e instanceof Expr.AbstractIndirectInvoke) {
				// Must be an invocation since these are neither valid
				// lvals (i.e. they cannot be assigned) nor types.
				matchEndLine();
				return (Stmt) e;
			} else if (tryAndMatch(true, Equals) != null) {
				// Must be an assignment a valid type cannot be followed by "="
				// on its own. Therefore, we backtrack and attempt to parse the
				// expression as an lval (i.e. as part of an assignment
				// statement).
				index = start; // backtrack
				//
				return parseAssignmentStatement(wf, scope);
			} else if (tryAndMatch(true, Comma) != null) {
				// Must be an multi-assignment 
				index = start; // backtrack
				//
				return parseAssignmentStatement(wf, scope);
			} else {
				// At this point, we must be left with a variable declaration.
				// Therefore, we backtrack and parse the expression again as a
				// type.
				index = start; // backtrack
				type = parseType(scope);
			}
		}
		// Must be a variable declaration here.
		Token name = match(Identifier);
		WhileyFile.Parameter decl = wf.new Parameter(type, name.text, sourceAttr(start, index - 1));
		return parseVariableDeclaration(start, decl, wf, scope);
	}
	
	/**
	 * Parse a variable declaration statement which has the form:
	 *
	 * <pre>
	 * Type Identifier ['=' Expr] NewLine
	 * </pre>
	 *
	 * The optional <code>Expression</code> assignment is referred to as an
	 * <i>initialiser</i>.
	 *
	 * @param parameter
	 *            The declared type for the variable, which will have already
	 *            been parsed when disambiguating this statement from another.
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.VariableDeclaration
	 *
	 * @return
	 */
	private Stmt.VariableDeclaration parseVariableDeclaration(int start,
			Parameter parameter, WhileyFile wf, EnclosingScope scope) {		

		// Ensure at least one variable is defined by this pattern.		
		// Check that declared variables are not already defined.		
		scope.checkNameAvailable(parameter);

		// A variable declaration may optionally be assigned an initialiser
		// expression.
		Expr initialiser = null;
		if (tryAndMatch(true, Token.Kind.Equals) != null) {
			initialiser = parseExpression(wf, scope, false);
		}
		// Now, a new line indicates the end-of-statement
		int end = index;
		matchEndLine();
		// Finally, register the new variable in the enclosing scope. This
		// should be done after parsing the initialiser expression to prevent it
		// from referring to this variable.
		scope.declareVariable(parameter);
		// Done.
		return new Stmt.VariableDeclaration(parameter, initialiser, sourceAttr(
				start, end - 1));
	}

	/**
	 * Parse a return statement, which has the form:
	 *
	 * <pre>
	 * ReturnStmt ::= "return" [Expr] NewLine
	 * </pre>
	 *
	 * The optional expression is referred to as the <i>return value</i>. Note
	 * that, the returned expression (if there is one) must begin on the same
	 * line as the return statement itself.
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Return
	 * @return
	 */
	private Stmt.Return parseReturnStatement(WhileyFile wf,
			EnclosingScope scope) {
		int start = index;

		match(Return);
		// A return statement may optionally have one or more return
		// expressions.  Therefore, we first skip all whitespace on the given line.
		int next = skipLineSpace(index);
		// Then, we check whether or not we reached the end of the line. If not,
		// then we assume what's remaining is the returned expression. This
		// means expressions must start on the same line as a return. Otherwise,
		// a potentially cryptic error message will be given.
		List<Expr> returns = Collections.EMPTY_LIST;
		if (next < tokens.size() && tokens.get(next).kind != NewLine) {
			returns = parseExpressions(wf, scope,false); 
		}
		// Finally, at this point we are expecting a new-line to signal the
		// end-of-statement.
		int end = index;
		matchEndLine();
		// Done.
		return new Stmt.Return(returns, sourceAttr(start, end - 1));
	}

	/**
	 * Parse an assert statement, which is of the form:
	 *
	 * <pre>
	 * AssertStmt ::= "assert" Expr
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Assert
	 * @return
	 */
	private Stmt.Assert parseAssertStatement(WhileyFile wf,
			EnclosingScope scope) {
		int start = index;
		// Match the assert keyword
		match(Assert);
		// Parse the expression to be printed
		Expr e = parseLogicalExpression(wf, scope, false);
		// Finally, at this point we are expecting a new-line to signal the
		// end-of-statement.
		int end = index;
		matchEndLine();
		// Done.
		return new Stmt.Assert(e, sourceAttr(start, end - 1));
	}

	/**
	 * Parse an assume statement, which is of the form:
	 *
	 * <pre>
	 * AssumeStmt ::= "assume" Expr
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Assume
	 * @return
	 */
	private Stmt.Assume parseAssumeStatement(WhileyFile wf,
			EnclosingScope scope) {
		int start = index;
		// Match the assume keyword
		match(Assume);
		// Parse the expression to be printed
		Expr e = parseLogicalExpression(wf, scope, false);
		// Finally, at this point we are expecting a new-line to signal the
		// end-of-statement.
		int end = index;
		matchEndLine();
		// Done.
		return new Stmt.Assume(e, sourceAttr(start, end - 1));
	}

	/**
	 * Parse a break statement, which is of the form:
	 *
	 * <pre>
	 * BreakStmt ::= "break"
	 * </pre>
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Break
	 * @return
	 */
	private Stmt.Break parseBreakStatement(EnclosingScope scope) {
		int start = index;
		// Match the break keyword
		Token t = match(Break);
		int end = index;
		matchEndLine();
		// Check that break statement makes sense at this point.
		if(!scope.isInLoop()) {
			syntaxError(errorMessage(BREAK_OUTSIDE_SWITCH_OR_LOOP),t);
		}
		// Done.
		return new Stmt.Break(sourceAttr(start, end - 1));
	}

	/**
	 * Parse a continue statement, which is of the form:
	 *
	 * <pre>
	 * ContinueStmt ::= "continue"
	 * </pre>
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Continue
	 * @return
	 */
	private Stmt.Continue parseContinueStatement(EnclosingScope scope) {
		int start = index;
		// Match the continue keyword
		Token t = match(Continue);
		int end = index;
		matchEndLine();
		// Check that continue statement makes sense at this point.
		if(!scope.isInLoop()) {
			syntaxError(errorMessage(CONTINUE_OUTSIDE_LOOP),t);
		}
		// Done.
		return new Stmt.Continue(sourceAttr(start, end - 1));
	}

	/**
	 * Parse a debug statement, which is of the form:
	 *
	 * <pre>
	 * DebugStmt ::= "debug" Expr
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Debug
	 * @return
	 */
	private Stmt.Debug parseDebugStatement(WhileyFile wf, EnclosingScope scope) {
		int start = index;
		// Match the debug keyword
		match(Debug);
		// Parse the expression to be printed
		Expr e = parseExpression(wf, scope, false);
		// Finally, at this point we are expecting a new-line to signal the
		// end-of-statement.
		int end = index;
		matchEndLine();
		// Done.
		return new Stmt.Debug(e, sourceAttr(start, end - 1));
	}

	/**
	 * Parse a do-while statement, which has the form:
	 *
	 * <pre>
	 * DoWhileStmt ::= "do" ':' NewLine Block "where" Expr ("where" Expr)*
	 * </pre>
	 *
	 * @see wyc.lang.Stmt.DoWhile
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 * @author David J. Pearce
	 *
	 */
	private Stmt parseDoWhileStatement(WhileyFile wf, EnclosingScope scope) {
		int start = index;
		match(Do);
		match(Colon);
		int end = index;
		matchEndLine();
		// match the block
		List<Stmt> blk = parseBlock(wf, scope, true);
		// match while and condition
		match(While);
		Expr condition = parseLogicalExpression(wf, scope, false);
		// Parse the loop invariants
		List<Expr> invariants = new ArrayList<Expr>();
		while (tryAndMatch(true, Where) != null) {
			invariants.add(parseLogicalExpression(wf, scope, false));
		}
		matchEndLine();
		return new Stmt.DoWhile(condition, invariants, blk, sourceAttr(start,
				end - 1));
	}

	/**
	 * Parse a fail statement, which is of the form:
	 *
	 * <pre>
	 * FailStmt ::= "fail"
	 * </pre>
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Fail
	 * @return
	 */
	private Stmt.Fail parseFailStatement(EnclosingScope scope) {
		int start = index;
		// Match the fail keyword
		match(Fail);
		int end = index;
		matchEndLine();
		// Done.
		return new Stmt.Fail(sourceAttr(start, end - 1));
	}

	/**
	 * Parse a classical if-else statement, which is has the form:
	 *
	 * <pre>
	 * "if" Expr ':' NewLine Block ["else" ':' NewLine Block]
	 * </pre>
	 *
	 * The first expression is referred to as the <i>condition</i>, while the
	 * first block is referred to as the <i>true branch</i>. The optional second
	 * block is referred to as the <i>false branch</i>.
	 *
	 * @see wyc.lang.Stmt.IfElse
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private Stmt.IfElse parseIfStatement(WhileyFile wf, EnclosingScope scope) {
		int start = index;
		// An if statement begins with the keyword "if", followed by an
		// expression representing the condition.
		match(If);
		// NOTE: expression terminated by ':'
		Expr c = parseLogicalExpression(wf, scope, true);
		// The a colon to signal the start of a block.
		match(Colon);
		matchEndLine();

		int end = index;
		// First, parse the true branch, which is required
		List<Stmt> tblk = parseBlock(wf, scope, scope.isInLoop());

		// Second, attempt to parse the false branch, which is optional.
		List<Stmt> fblk = Collections.emptyList();
		if (tryAndMatchAtIndent(true, scope.getIndent(), Else) != null) {
			int if_start = index;
			if (tryAndMatch(true, If) != null) {
				// This is an if-chain, so backtrack and parse a complete If
				index = if_start;
				fblk = new ArrayList<Stmt>();
				fblk.add(parseIfStatement(wf, scope));
			} else {
				match(Colon);
				matchEndLine();
				fblk = parseBlock(wf, scope, scope.isInLoop());
			}
		}
		// Done!
		return new Stmt.IfElse(c, tblk, fblk, sourceAttr(start, end - 1));
	}

	/**
	 * Parse a while statement, which has the form:
	 *
	 * <pre>
	 * WhileStmt ::= "while" Expr ("where" Expr)* ':' NewLine Block
	 * </pre>
	 *
	 * @see wyc.lang.Stmt.While
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 * @author David J. Pearce
	 *
	 */
	private Stmt parseWhileStatement(WhileyFile wf, EnclosingScope scope) {
		int start = index;
		match(While);
		// NOTE: expression terminated by ':'
		Expr condition = parseLogicalExpression(wf, scope, true);
		// Parse the loop invariants
		List<Expr> invariants = new ArrayList<Expr>();
		while (tryAndMatch(true, Where) != null) {
			// NOTE: expression terminated by ':'
			invariants.add(parseLogicalExpression(wf, scope, true));
		}
		match(Colon);
		int end = index;
		matchEndLine();
		List<Stmt> blk = parseBlock(wf, scope, true);
		return new Stmt.While(condition, invariants, blk, sourceAttr(start,
				end - 1));
	}

	/**
	 * Parse a skip statement, which is of the form:
	 *
	 * <pre>
	 * SkipStmt ::= "skip"
	 * </pre>
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Skip
	 * @return
	 */
	private Stmt.Skip parseSkipStatement(EnclosingScope scope) {
		int start = index;
		// Match the break keyword
		match(Skip);
		int end = index;
		matchEndLine();
		// Done.
		return new Stmt.Skip(sourceAttr(start, end - 1));
	}

	/**
	 * Parse a switch statement, which has the form:
	 *
	 * <pre>
	 * SwitchStmt ::= "switch" Expr ':' NewLine CaseStmt+
	 *
	 * CaseStmt ::= "case" UnitExpr (',' UnitExpr)* ':' NewLine Block
	 * </pre>
	 *
	 * @see wyc.lang.Stmt.Switch
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 * @author David J. Pearce
	 *
	 */
	private Stmt parseSwitchStatement(WhileyFile wf, EnclosingScope scope) {
		int start = index;
		match(Switch);
		// NOTE: expression terminated by ':'
		Expr condition = parseExpression(wf, scope, true);
		match(Colon);
		int end = index;
		matchEndLine();
		// Match case block
		List<Stmt.Case> cases = parseCaseBlock(wf, scope);
		// Done
		return new Stmt.Switch(condition, cases, sourceAttr(start, end - 1));
	}

	/**
	 * Parse a block of zero or more case statements which share the same
	 * indentation level. Their indentation level must be strictly greater than
	 * that of their parent, otherwise the end of block is signalled. The
	 * <i>indentation level</i> for the block is set by the first statement
	 * encountered (assuming their is one). An error occurs if a subsequent
	 * statement is reached with an indentation level <i>greater</i> than the
	 * block's indentation level.
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private List<Stmt.Case> parseCaseBlock(WhileyFile wf, EnclosingScope scope) {


		// First, determine the initial indentation of this block based on the
		// first statement (or null if there is no statement).
		Indent indent = getIndent();
		
		// We must create a new scope to ensure variables declared within this
		// block are not visible in the enclosing scope.		
		EnclosingScope caseScope = scope.newEnclosingScope(indent);
		
		// Second, check that this is indeed the initial indentation for this
		// block (i.e. that it is strictly greater than parent indent).
		if (indent == null || indent.lessThanEq(scope.getIndent())) {
			// Initial indent either doesn't exist or is not strictly greater
			// than parent indent and,therefore, signals an empty block.
			//
			return Collections.EMPTY_LIST;
		} else {
			// Initial indent is valid, so we proceed parsing case statements
			// with the appropriate level of indent.
			//
			ArrayList<Stmt.Case> cases = new ArrayList<Stmt.Case>();
			
			Indent nextIndent;
			while ((nextIndent = getIndent()) != null
					&& indent.lessThanEq(nextIndent)) {
				// At this point, nextIndent contains the indent of the current
				// statement. However, this still may not be equivalent to this
				// block's indentation level.

				// First, check the indentation matches that for this block.
				if (!indent.equivalent(nextIndent)) {
					// No, it's not equivalent so signal an error.
					syntaxError("unexpected end-of-block", indent);
				}

				// Second, parse the actual case statement at this point!
				cases.add(parseCaseStatement(wf, caseScope));
			}
			checkForDuplicateDefault(cases);
			return cases;
		}
	}

	/**
	 * Check whether we have a duplicate default statement, or a case which
	 * occurs after a default statement (and, hence, is unreachable).
	 * 
	 * @param cases
	 */
	private void checkForDuplicateDefault(List<Stmt.Case> cases) {
		boolean hasDefault = false;
		for(Stmt.Case c: cases) {
			if(c.expr.size() > 0 && hasDefault) {
				syntaxError(errorMessage(UNREACHABLE_CODE), c);					
			} else if(c.expr.size() == 0 && hasDefault) {
				syntaxError(errorMessage(DUPLICATE_DEFAULT_LABEL), c);
			} else {
				hasDefault = c.expr.size() == 0;
			}
		}
	}
	/**
	 * Parse a case Statement, which has the form:
	 *
	 * <pre>
	 * CaseStmt ::= "case" NonTupleExpr (',' NonTupleExpression)* ':' NewLine Block
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private Stmt.Case parseCaseStatement(WhileyFile wf, EnclosingScope scope) {
		int start = index;
		List<Expr> values;
		if (tryAndMatch(true, Default) != null) {
			values = Collections.EMPTY_LIST;
		} else {
			match(Case);
			// Now, parse one or more constant expressions
			values = new ArrayList<Expr>();
			do {
				// NOTE: expression terminated by ':'
				values.add(parseExpression(wf, scope, true));
			} while (tryAndMatch(true, Comma) != null);
		}
		match(Colon);
		int end = index;
		matchEndLine();
		List<Stmt> stmts = parseBlock(wf, scope, scope.isInLoop());
		return new Stmt.Case(values, stmts, sourceAttr(start, end - 1));
	}

	/**
	 * Parse an assignment statement, which has the form:
	 *
	 * <pre>
	 * AssignStmt ::= LVal '=' Expr
	 * </pre>
	 *
	 * Here the <code>lhs</code> must be an <code>LVal</code> --- that is, an
	 * expression permitted on the left-side of an assignment. The following
	 * illustrates different possible assignment statements:
	 *
	 * <pre>
	 * x = y       // variable assignment
	 * x,y = z     // multi-assignment
	 * x.f = y     // field assignment
	 * x[i] = y    // array assignment
	 * x[i].f = y  // compound assignment
	 * </pre>
	 *
	 * The last assignment here illustrates that the left-hand side of an
	 * assignment can be arbitrarily complex, involving nested assignments into
	 * arrays and records.
	 *
	 * @see wyc.lang.Stmt.Assign
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @return
	 */
	private Stmt parseAssignmentStatement(WhileyFile wf, EnclosingScope scope) {
		int start = index;
		List<Expr.LVal> lvals = parseLVals(wf, scope);
		match(Equals);
		List<Expr> rvals = parseExpressions(wf, scope, false);
		int end = index;
		matchEndLine();
		return new Stmt.Assign(lvals, rvals, sourceAttr(start, end - 1));
	}

	/**
	 * Parse an "lval" expression, which is a subset of the possible expressions
	 * forms permitted on the left-hand side of an assignment. LVals are of the
	 * form:
	 *
	 * <pre>
	 * LVal ::= LValTerm (',' LValTerm)* ')'
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @return
	 */
	private List<Expr.LVal> parseLVals(WhileyFile wf, EnclosingScope scope) {
		int start = index;
		ArrayList<Expr.LVal> elements = new ArrayList<Expr.LVal>();				
		elements.add(parseLVal(index, wf, scope));
		
		// Check whether we have a multiple lvals or not
		while (tryAndMatch(true, Comma) != null) {
			// Add all expressions separated by a comma
			elements.add(parseLVal(index,wf, scope));
			// Done
		}

		return elements;
	}
	
	private Expr.LVal parseLVal(int start, WhileyFile wf, EnclosingScope scope) {
		return parseAccessLVal(start, wf, scope);
	}

	/**
	 * Parse an access lval, which is of the form:
	 *
	 * <pre>
	 * AccessLVal ::= TermLVal
	 * 			 | AccessLVal '.' Identifier     // Field assignment
	 *           | AccessLVal '->' Identifier // dereference field assigmment
	 *           | '*' AccessLVal  // dereference assigmment
	 *           | AccessLVal '[' Expr ']' // index assigmment
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @return
	 */
	private Expr.LVal parseAccessLVal(int start, WhileyFile wf, EnclosingScope scope) {
		Expr.LVal lhs = parseLValTerm(start, wf, scope);
		Token token;

		while ((token = tryAndMatchOnLine(LeftSquare)) != null
				|| (token = tryAndMatch(true, Dot, MinusGreater)) != null) {
			switch (token.kind) {
			case LeftSquare:
				// NOTE: expression is terminated by ']'
				Expr rhs = parseAdditiveExpression(wf, scope, true);
				match(RightSquare);
				lhs = new Expr.IndexOf(lhs, rhs, sourceAttr(start, index - 1));
				break;
			case MinusGreater:
				lhs = new Expr.Dereference(lhs, sourceAttr(start, index - 1));
				// Fall Through
			case Dot:
				String name = match(Identifier).text;
				lhs = new Expr.FieldAccess(lhs, name, sourceAttr(start,
						index - 1));
				break;
			}
		}

		return lhs;
	}

	/**
	 * Parse an lval term, which is of the form:
	 *
	 * <pre>
	 * TermLVal ::= Identifier             // Variable assignment
	 *           | '(' LVal ')'            // Bracketed assignment
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @return
	 */
	private Expr.LVal parseLValTerm(int start, WhileyFile wf, EnclosingScope scope) {
		checkNotEof();
		// First, attempt to disambiguate the easy forms:
		Token lookahead = tokens.get(index);
		switch (lookahead.kind) {
		case Identifier:
			match(Identifier);
			return new Expr.AssignedVariable(lookahead.text, sourceAttr(start,
					index - 1));
		case LeftBrace: {
			match(LeftBrace);
			Expr.LVal lval = parseLVal(start, wf, scope);
			match(RightBrace);
			return lval;
		}
		case Star: {
			match(Star);
			Expr.LVal lval = parseLVal(start, wf, scope);
			return new Expr.Dereference(lval, sourceAttr(start, index - 1));
		}
		default:
			syntaxError("unrecognised lval", lookahead);
			return null; // dead-code
		}
	}
	
	/**
	 * Parse a "multi-expression"; that is, a sequence of one or more
	 * expressions separated by comma's
	 * 
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	public List<Expr> parseExpressions(WhileyFile wf, EnclosingScope scope, boolean terminated) {
		ArrayList<Expr> returns = new ArrayList<Expr>();
		// A return statement may optionally have a return expression.
		// Therefore, we first skip all whitespace on the given line.
		int next = skipLineSpace(index);
		// Then, we check whether or not we reached the end of the line. If not,
		// then we assume what's remaining is the returned expression. This
		// means expressions must start on the same line as a return. Otherwise,
		// a potentially cryptic error message will be given.
		returns.add(parseExpression(wf, scope, terminated));
		while(tryAndMatch(false,Comma) != null) {
			returns.add(parseExpression(wf, scope, terminated));
		}
		return returns;
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseExpression(WhileyFile wf, EnclosingScope scope, boolean terminated) {
		return parseLogicalExpression(wf, scope, terminated);
	}

	/**
	 * Parse a logical expression of the form:
	 *
	 * <pre>
	 * Expr ::= AndOrExpr [ "==>" UnitExpr]
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseLogicalExpression(WhileyFile wf, EnclosingScope scope, boolean terminated) {
		checkNotEof();
		int start = index;
		Expr lhs = parseAndOrExpression(wf, scope, terminated);
		Token lookahead = tryAndMatch(terminated,  LogicalImplication, LogicalIff);
		if (lookahead != null) {
			switch (lookahead.kind) {

			case LogicalImplication: {
				Expr rhs = parseExpression(wf, scope, terminated);
				// FIXME: this is something of a hack, although it does work. It
				// would be nicer to have a binary expression kind for logical
				// implication.
				lhs = new Expr.UnOp(Expr.UOp.NOT, lhs, sourceAttr(start,
						index - 1));
				//
				return new Expr.BinOp(Expr.BOp.OR, lhs, rhs, sourceAttr(start,
						index - 1));
			}
			case LogicalIff: {
				Expr rhs = parseExpression(wf, scope, terminated);
				// FIXME: this is something of a hack, although it does work. It
				// would be nicer to have a binary expression kind for logical
				// implication.
				Expr nlhs = new Expr.UnOp(Expr.UOp.NOT, lhs, sourceAttr(start,
						index - 1));
				Expr nrhs = new Expr.UnOp(Expr.UOp.NOT, rhs, sourceAttr(start,
						index - 1));
				//
				nlhs = new Expr.BinOp(Expr.BOp.AND, nlhs, nrhs, sourceAttr(start,
						index - 1));
				nrhs = new Expr.BinOp(Expr.BOp.AND, lhs, rhs, sourceAttr(start,
						index - 1));
				//
				return new Expr.BinOp(Expr.BOp.OR, nlhs, nrhs, sourceAttr(start,
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseAndOrExpression(WhileyFile wf, EnclosingScope scope, boolean terminated) {
		checkNotEof();
		int start = index;
		Expr lhs = parseBitwiseOrExpression(wf, scope, terminated);
		Token lookahead = tryAndMatch(terminated, LogicalAnd, LogicalOr);
		if (lookahead != null) {
			Expr.BOp bop;
			switch (lookahead.kind) {
			case LogicalAnd:
				bop = Expr.BOp.AND;
				break;
			case LogicalOr:
				bop = Expr.BOp.OR;
				break;
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}
			Expr rhs = parseExpression(wf, scope, terminated);
			return new Expr.BinOp(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse an bitwise "inclusive or" expression
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseBitwiseOrExpression(WhileyFile wf, EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseBitwiseXorExpression(wf, scope, terminated);

		if (tryAndMatch(terminated, VerticalBar) != null) {
			Expr rhs = parseExpression(wf, scope, terminated);
			return new Expr.BinOp(Expr.BOp.BITWISEOR, lhs, rhs, sourceAttr(
					start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse an bitwise "exclusive or" expression
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseBitwiseXorExpression(WhileyFile wf, EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseBitwiseAndExpression(wf, scope, terminated);

		if (tryAndMatch(terminated, Caret) != null) {
			Expr rhs = parseExpression(wf, scope, terminated);
			return new Expr.BinOp(Expr.BOp.BITWISEXOR, lhs, rhs, sourceAttr(
					start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse an bitwise "and" expression
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseBitwiseAndExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseConditionExpression(wf, scope, terminated);

		if (tryAndMatch(terminated, Ampersand) != null) {
			Expr rhs = parseExpression(wf, scope, terminated);
			return new Expr.BinOp(Expr.BOp.BITWISEAND, lhs, rhs, sourceAttr(
					start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse a condition expression.
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseConditionExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		Token lookahead;

		// First, attempt to parse quantifiers (e.g. some, all, no, etc)
		if ((lookahead = tryAndMatch(terminated, Some, All)) != null) {
			return parseQuantifierExpression(lookahead, wf, scope,
					terminated);
		}

		Expr lhs = parseShiftExpression(wf, scope, terminated);

		lookahead = tryAndMatch(terminated, LessEquals, LeftAngle,
				GreaterEquals, RightAngle, EqualsEquals, NotEquals, Is,
				Subset, SubsetEquals, Superset, SupersetEquals);

		if (lookahead != null) {
			Expr.BOp bop;
			switch (lookahead.kind) {
			case LessEquals:
				bop = Expr.BOp.LTEQ;
				break;
			case LeftAngle:
				bop = Expr.BOp.LT;
				break;
			case GreaterEquals:
				bop = Expr.BOp.GTEQ;
				break;
			case RightAngle:
				bop = Expr.BOp.GT;
				break;
			case EqualsEquals:
				bop = Expr.BOp.EQ;
				break;
			case NotEquals:
				bop = Expr.BOp.NEQ;
				break;
			case Is:
				SyntacticType type = parseType(scope);
				Expr.TypeVal rhs = new Expr.TypeVal(type, sourceAttr(start,
						index - 1));
				return new Expr.BinOp(Expr.BOp.IS, lhs, rhs, sourceAttr(start,
						index - 1));
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}

			Expr rhs = parseShiftExpression(wf, scope, terminated);
			return new Expr.BinOp(bop, lhs, rhs, sourceAttr(start, index - 1));
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseQuantifierExpression(Token lookahead, WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index - 1;

		// Determine the quantifier operation
		Expr.QOp cop;
		switch (lookahead.kind) {
		case Some:
			cop = Expr.QOp.SOME;
			break;
		case All:
			cop = Expr.QOp.ALL;
			break;
		default:
			cop = null; // deadcode
		}

		match(LeftCurly);

		// Parse one or more source variables / expressions
		scope = scope.newEnclosingScope();		
		List<Triple<String, Expr, Expr>> srcs = new ArrayList<Triple<String, Expr, Expr>>();
		boolean firstTime = true;

		do {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			Token id = match(Identifier);
			scope.checkNameAvailable(id);
			match(In);
			Expr lhs = parseAdditiveExpression(wf, scope, terminated);
			match(DotDot);
			Expr rhs = parseAdditiveExpression(wf, scope, terminated);			
			srcs.add(new Triple<String, Expr, Expr>(id.text, lhs, rhs));
			scope.declareVariable(id);
		} while (eventuallyMatch(VerticalBar) == null);

		// Parse condition over source variables
		Expr condition = parseLogicalExpression(wf, scope, terminated);

		match(RightCurly);

		// Done
		return new Expr.Quantifier(cop, srcs, condition, sourceAttr(start,
				index - 1));
	}


	/**
	 * Parse a range expression, which has the form:
	 *
	 * <pre>
	 * RangeExpr ::= ShiftExpr [ ".." ShiftExpr ]
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseRangeExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseShiftExpression(wf, scope, terminated);

		if (tryAndMatch(terminated, DotDot) != null) {
			Expr rhs = parseAdditiveExpression(wf, scope, terminated);
			return new Expr.BinOp(Expr.BOp.RANGE, lhs, rhs, sourceAttr(start,
					index - 1));
		}

		return lhs;
	}
	
	/**
	 * Parse a shift expression, which has the form:
	 *
	 * <pre>
	 * ShiftExpr ::= AdditiveExpr [ ( "<<" | ">>" ) AdditiveExpr ]
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseShiftExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseAdditiveExpression(wf, scope, terminated);

		Token lookahead;
		while ((lookahead = tryAndMatch(terminated, LeftAngleLeftAngle,
				RightAngleRightAngle)) != null) {
			Expr rhs = parseAdditiveExpression(wf, scope, terminated);
			Expr.BOp bop = null;
			switch (lookahead.kind) {
			case LeftAngleLeftAngle:
				bop = Expr.BOp.LEFTSHIFT;
				break;
			case RightAngleRightAngle:
				bop = Expr.BOp.RIGHTSHIFT;
				break;
			}
			lhs = new Expr.BinOp(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse an additive expression.
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseAdditiveExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseMultiplicativeExpression(wf, scope, terminated);

		Token lookahead;
		while ((lookahead = tryAndMatch(terminated, Plus, Minus)) != null) {
			Expr.BOp bop;
			switch (lookahead.kind) {
			case Plus:
				bop = Expr.BOp.ADD;
				break;
			case Minus:
				bop = Expr.BOp.SUB;
				break;
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}

			Expr rhs = parseMultiplicativeExpression(wf, scope,
					terminated);
			lhs = new Expr.BinOp(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	/**
	 * Parse a multiplicative expression.
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseMultiplicativeExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseAccessExpression(wf, scope, terminated);

		Token lookahead = tryAndMatch(terminated, Star, RightSlash, Percent);
		if (lookahead != null) {
			Expr.BOp bop;
			switch (lookahead.kind) {
			case Star:
				bop = Expr.BOp.MUL;
				break;
			case RightSlash:
				bop = Expr.BOp.DIV;
				break;
			case Percent:
				bop = Expr.BOp.REM;
				break;
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}
			Expr rhs = parseAccessExpression(wf, scope, terminated);
			return new Expr.BinOp(bop, lhs, rhs, sourceAttr(start, index - 1));
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
	 *            | AccessExpr "->" Identifier
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseAccessExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseTermExpression(wf, scope, terminated);
		Token token;

		while ((token = tryAndMatchOnLine(LeftSquare)) != null
				|| (token = tryAndMatch(terminated, Dot, MinusGreater)) != null) {
			switch (token.kind) {
			case LeftSquare:				
				// NOTE: expression guaranteed to be terminated by ']'.
				Expr rhs = parseAdditiveExpression(wf, scope, true);
				// This is a plain old array access expression
				match(RightSquare);
				lhs = new Expr.IndexOf(lhs, rhs, sourceAttr(start,
							index - 1));				
				break;
			case MinusGreater:
				lhs = new Expr.Dereference(lhs, sourceAttr(start, index - 1));
				// Fall through
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
				Path.ID id = parsePossiblePathID(lhs, scope);

				// First we have to see if it is a method invocation. We can
				// have optional lifetime arguments in angle brackets.
				boolean isInvocation = false;
				List<String> lifetimeArguments = null;
				if (tryAndMatch(terminated, LeftBrace) != null) {
					isInvocation = true;
				} else if (lookaheadSequence(terminated, LeftAngle)) {
					// This one is a little tricky, as we need some lookahead
					// effort. We want to see whether it is a method invocation with
					// lifetime arguments. But "Identifier < ..." can also be a
					// boolean expression!
					int oldindex = index;
					match(LeftAngle);
					Token lifetime = tryAndMatch(terminated, RightAngle, Identifier, This, Star);
					if (lifetime != null && (
							lifetime.kind != Identifier // then it's definitely a lifetime
							|| scope.isLifetime(lifetime.text))) {
						isInvocation = true;
						index--; // don't forget the first argument!
						lifetimeArguments = parseLifetimeArguments(wf, scope);
						match(LeftBrace);
					} else {
						index = oldindex; // backtrack
					}
				}
				if (isInvocation) {
					// This indicates a direct or indirect invocation. First,
					// parse arguments to invocation
					ArrayList<Expr> arguments = parseInvocationArguments(wf,scope);
					// Second, determine what kind of invocation we have.
					if(id == null) {
						// This indicates we have an indirect invocation
						lhs = new Expr.FieldAccess(lhs, name, sourceAttr(
								start, index - 1));
						lhs = new Expr.AbstractIndirectInvoke(lhs, arguments,
								lifetimeArguments, sourceAttr(start, index - 1));
					} else {
						// This indicates we have an direct invocation
						lhs = new Expr.AbstractInvoke(name, id, arguments,
								lifetimeArguments, sourceAttr(start, index - 1));
					}

				} else if(id != null) {
					// Must be a qualified constant access
					lhs = new Expr.ConstantAccess(name, id, sourceAttr(
							start, index - 1));
				} else {
					// Must be a plain old field access.
					lhs = new Expr.FieldAccess(lhs, name, sourceAttr(
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private Path.ID parsePossiblePathID(Expr src, EnclosingScope scope) {
		if(src instanceof Expr.LocalVariable) {
			// this is a local variable, indicating that the we did not have
			// a module identifier.
			return null;
		} else if(src instanceof Expr.ConstantAccess) {
			Expr.ConstantAccess ca = (Expr.ConstantAccess) src;
			return Trie.ROOT.append(ca.name);
		} else if(src instanceof Expr.FieldAccess) {
			Expr.FieldAccess ada = (Expr.FieldAccess) src;
			Path.ID id = parsePossiblePathID(ada.src, scope);
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseTermExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		checkNotEof();

		int start = index;
		Token token = tokens.get(index);

		switch (token.kind) {
		case LeftBrace:
			return parseBracketedExpression(wf, scope, terminated);
		case New:
		case This:
			return parseNewExpression(wf, scope, terminated);
		case Identifier:
			match(Identifier);
			if (tryAndMatch(terminated, LeftBrace) != null) {
				return parseInvokeExpression(wf, scope, start, token,
						terminated, null);
			} else if (lookaheadSequence(terminated, Colon, New)) {
				// Identifier is lifetime name in "new" expression
				index = start;
				return parseNewExpression(wf, scope, terminated);
			} else if (lookaheadSequence(terminated, LeftAngle)) {
				// This one is a little tricky, as we need some lookahead
				// effort. We want to see whether it is a method invocation with
				// lifetime arguments. But "Identifier < ..." can also be a
				// boolean expression!
				int oldindex = index;
				match(LeftAngle);
				Token lifetime = tryAndMatch(terminated, RightAngle, Identifier, This, Star);
				if (lifetime != null && (
						lifetime.kind != Identifier // then it's definitely a lifetime
						|| scope.isLifetime(lifetime.text))) {
					index--; // don't forget the first argument!
					List<String> lifetimeArguments = parseLifetimeArguments(wf, scope);
					match(LeftBrace);
					return parseInvokeExpression(
							wf, scope, start, token, terminated, lifetimeArguments);
				} else {
					index = oldindex; // backtrack
				}
			} // no else if, in case the former one didn't return
			if (scope.isVariable(token.text)) {
				// Signals a local variable access
				return new Expr.LocalVariable(token.text, sourceAttr(start,
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
			return new Expr.Constant(wyil.lang.Constant.Null, sourceAttr(
					start, index++));
		case True:
			return new Expr.Constant(Constant.True,
					sourceAttr(start, index++));
		case False:
			return new Expr.Constant(Constant.False,
					sourceAttr(start, index++));
		case ByteValue: {
			byte val = parseByte(token);
			return new Expr.Constant(new Constant.Byte(val),
					sourceAttr(start, index++));
		}
		case CharValue: {
			BigInteger c = parseCharacter(token.text);
			return new Expr.Constant(new Constant.Integer(c), sourceAttr(
					start, index++));
		}
		case IntValue: {
			BigInteger val = new BigInteger(token.text);
			return new Expr.Constant(new Constant.Integer(val),
					sourceAttr(start, index++));
		}
		case StringValue: {
			List<Constant> str = parseString(token.text);
			return new Expr.Constant(new Constant.Array(str),
					sourceAttr(start, index++));
		}
		case Minus:
			return parseNegationExpression(wf, scope, terminated);
		case VerticalBar:
			return parseLengthOfExpression(wf, scope, terminated);
		case LeftSquare:
			return parseArrayInitialiserOrGeneratorExpression(wf, scope, terminated);
		case LeftCurly:
			return parseRecordExpression(wf, scope, terminated);
		case Shreak:
			return parseLogicalNotExpression(wf, scope, terminated);
		case Star:
			if (lookaheadSequence(terminated, Star, Colon, New)) {
				// Star is default lifetime
				return parseNewExpression(wf, scope, terminated);
			}
			return parseDereferenceExpression(wf, scope, terminated);
		case Tilde:
			return parseBitwiseComplementExpression(wf, scope, terminated);
		case Ampersand:
			return parseLambdaOrAddressExpression(wf, scope, terminated);
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseBracketedExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(LeftBrace);

		// At this point, we must begin to disambiguate casts from general
		// bracketed expressions. In the case that what follows the left brace
		// is something which can only be a type, then clearly we have a cast.
		// However, in the other case, we may still have a cast since many types
		// cannot be clearly distinguished from expressions at this stage (e.g.
		// "(nat,nat)" could either be a tuple type (if "nat" is a type) or a
		// tuple expression (if "nat" is a variable or constant).

		SyntacticType t = parseDefiniteType(scope);

		if (t != null) {
			// At this point, it's looking likely that we have a cast. However,
			// it's not certain because of the potential for nested braces. For
			// example, consider "((char) x + y)". We'll parse the outermost
			// brace and what follows *must* be parsed as either a type, or
			// bracketed type.
			if (tryAndMatch(true, RightBrace) != null) {
				// Ok, finally, we are sure that it is definitely a cast.
				Expr e = parseExpression(wf, scope, terminated);
				return new Expr.Cast(t, e, sourceAttr(start, index - 1));
			}
		}
		// We still may have either a cast or a bracketed expression, and we
		// cannot tell which yet.
		index = start;
		match(LeftBrace);
		Expr e = parseExpression(wf, scope, true);
		match(RightBrace);

		// Now check whether this must be an expression, or could still be a
		// cast.
		if(!mustParseAsExpr(e)) {

			// At this point, we may still have a cast. Therefore, we now
			// examine what follows to see whether this is a cast or bracketed
			// expression. See JavaDoc comments above for more on this. What we
			// do is first skip any whitespace, and then see what we've got.
			int next = skipLineSpace(index);
			if (next < tokens.size()) {
				Token lookahead = tokens.get(next);

				switch (lookahead.kind) {
				case Null:
				case True:
				case False:
				case ByteValue:
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
					SyntacticType type = parseType(scope);
					// Now, parse cast expression
					e = parseExpression(wf, scope, terminated);
					return new Expr.Cast(type, e, sourceAttr(start, index - 1));
				}
				default:
					// default case, fall through and assume bracketed
					// expression
				}
			}
		}
		// Assume bracketed
		return e;
	}

	/**
	 * Parse an array initialiser or generator expression, which is of the form:
	 *
	 * <pre>
	 * ArrayExpr ::= '[' [ Expr (',' Expr)+ ] ']'
	 *             | '[' Expr ';' Expr ']' 
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseArrayInitialiserOrGeneratorExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(LeftSquare);
		Expr expr = parseExpression(wf, scope, true);
		// Finally, disambiguate
		if(tryAndMatch(true,SemiColon) != null) {
			// this is an array generator
			index = start;
			return parseArrayGeneratorExpression(wf, scope,terminated);
		} else {		
			// this is an array initialiser
			index = start;
			return parseArrayInitialiserExpression(wf, scope,terminated);
		}
	}
	
	/**
	 * Parse an array initialiser expression, which is of the form:
	 *
	 * <pre>
	 * ArrayInitialiserExpr ::= '[' [ Expr (',' Expr)+ ] ']' 
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseArrayInitialiserExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(LeftSquare);
		ArrayList<Expr> exprs = new ArrayList<Expr>();

		boolean firstTime = true;
		do {		
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
			exprs.add(parseExpression(wf, scope, true));
		} while (eventuallyMatch(RightSquare) == null);

		return new Expr.ArrayInitialiser(exprs, sourceAttr(start, index - 1));
	}

	/**
	 * Parse an array generator expression, which is of the form:
	 *
	 * <pre>
	 * ArrayGeneratorExpr ::= '[' Expr ';' Expr ']' 
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseArrayGeneratorExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(LeftSquare);
		Expr element = parseExpression(wf, scope, true);
		match(SemiColon);
		Expr count = parseExpression(wf, scope, true);
		match(RightSquare);
		return new Expr.ArrayGenerator(element,count,sourceAttr(start, index - 1));
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseRecordExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
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
				syntaxError("duplicate record key", n);
			}
			match(Colon);
			// Parse expression being assigned to field
			// NOTE: we require the following expression be a "non-tuple"
			// expression. That is, it cannot be composed using ',' unless
			// braces enclose the entire expression. This is because the outer
			// record constructor expression is used ',' to distinguish fields.
			// Also, expression is guaranteed to be terminated, either by '}' or
			// ','.
			Expr e = parseExpression(wf, scope, true);
			exprs.put(n.text, e);
			keys.add(n.text);
		}

		return new Expr.Record(exprs, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a new expression, which is of the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *                 |  "new" Expr
	 *                 |  Lifetime ":" "new" Identifier Expr
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseNewExpression(WhileyFile wf, EnclosingScope scope,
			boolean terminated) {
		int start = index;

		// try to match a lifetime
		String lifetime;
		Token lifetimeIdentifier = tryAndMatch(terminated, Identifier, This, Star);
		if (lifetimeIdentifier != null) {
			scope.mustBeLifetime(lifetimeIdentifier);
			lifetime = lifetimeIdentifier.text;
			match(Colon);
		} else {
			lifetime = "*";
		}

		match(New);
		Expr e = parseExpression(wf, scope, terminated);
		return new Expr.New(e, lifetime, sourceAttr(start, index - 1));
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseLengthOfExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(VerticalBar);
		// We have to parse an Append Expression here, which is the most general
		// form of expression that can generate a collection of some kind. All
		// expressions higher up (e.g. logical expressions) cannot generate
		// collections. Furthermore, the bitwise or expression could lead to
		// ambiguity and, hence, we bypass that an consider append expressions
		// only. However, the expression is guaranteed to be terminated by '|'.
		Expr e = parseShiftExpression(wf, scope, true);
		match(VerticalBar);
		return new Expr.UnOp(Expr.UOp.ARRAYLENGTH, e, sourceAttr(start, index - 1));
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseNegationExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Minus);
		Expr e = parseAccessExpression(wf, scope, terminated);
		return new Expr.UnOp(Expr.UOp.NEG, e, sourceAttr(start, index - 1));
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseInvokeExpression(WhileyFile wf, EnclosingScope scope, int start, Token name,
			boolean terminated, List<String> lifetimeArguments) {
		// First, parse the arguments to this invocation.
		ArrayList<Expr> args = parseInvocationArguments(wf, scope);

		// Second, determine what kind of invocation we have. If the name of the
		// method is a local variable, then it must be an indirect invocation on
		// this variable.
		if (scope.isVariable(name.text)) {
			// indirect invocation on local variable
			Expr.LocalVariable lv = new Expr.LocalVariable(name.text,
					sourceAttr(start, start));
			return new Expr.AbstractIndirectInvoke(lv, args, lifetimeArguments,
					sourceAttr(start, index - 1));
		} else {
			// unqualified direct invocation
			return new Expr.AbstractInvoke(name.text, null, args, lifetimeArguments,
					sourceAttr(start, index - 1));
		}
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private ArrayList<Expr> parseInvocationArguments(WhileyFile wf,
			EnclosingScope scope) {
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
			Expr e = parseExpression(wf, scope, true);

			args.add(e);
		}
		return args;
	}

	/**
	 * Parse a sequence of lifetime arguments separated by commas that ends in a
	 * right-angle:
	 *
	 * <pre>
	 * LifetimeArguments ::= [ Lifetime (',' Lifetime)* ] '>'
	 * </pre>
	 *
	 * Note, when this function is called we're assuming the left angle was
	 * already parsed.
	 *
	 * @param wf
	 * @param scope
	 * @return
	 */
	private ArrayList<String> parseLifetimeArguments(WhileyFile wf, EnclosingScope scope) {
		boolean firstTime = true;
		ArrayList<String> lifetimeArgs = new ArrayList<String>();
		while (eventuallyMatch(RightAngle) == null) {
			if (!firstTime) {
				match(Comma);
			} else {
				firstTime = false;
			}

			// termindated by '>'
			String lifetime = parseLifetime(scope, true);

			lifetimeArgs.add(lifetime);
		}
		return lifetimeArgs;
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
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseLogicalNotExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Shreak);
		// Note: cannot parse unit expression here, because that messes up the
		// precedence. For example, !result ==> other should be parsed as
		// (!result) ==> other, not !(result ==> other).
		Expr expression = parseConditionExpression(wf, scope, terminated);
		return new Expr.UnOp(Expr.UOp.NOT, expression, sourceAttr(start,
				index - 1));
	}

	/**
	 * Parse a dereference expression, which has the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *                 | '*' Expr
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @return
	 */
	private Expr parseDereferenceExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Star);
		Expr expression = parseExpression(wf, scope, terminated);
		return new Expr.Dereference(expression, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a lambda or address expression, which have the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *                 | '&' [ '[' [ Lifetime   (',' Lifetime  )* ] ']' ]
	 *                       [ '<' [ Identifier (',' Identifier)* ] '>' ]
	 *                   '(' [Type Identifier (',' Type Identifier)*] '->' Expr ')'
	 *                 | '&' Identifier [ '(' Type (',' Type)* ')']
	 * </pre>
	 *
	 * Disambiguating these two forms is relatively straightforward, and we just
	 * look to see what follows the '&'.
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseLambdaOrAddressExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Ampersand);
		if (tryAndMatch(terminated, LeftBrace, LeftSquare, LeftAngle) != null) {
			index = start; // backtrack
			return parseLambdaExpression(wf, scope, terminated);
		} else {
			index = start; // backtrack
			return parseAddressExpression(wf, scope, terminated);
		}
	}

	/**
	 * Parse a lambda expression, which has the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *                 | '&' [ '[' [ Lifetime   (',' Lifetime  )* ] ']' ]
	 *                       [ '<' [ Identifier (',' Identifier)* ] '>' ]
	 *                   '(' [Type Identifier (',' Type Identifier)*] '->' Expr ')'
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseLambdaExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Ampersand);

		// First parse the context lifetimes with the original scope
		Set<String> contextLifetimes = parseOptionalContextLifetimes(scope);

		// Now we create a new scope for this lambda expression.
		// It keeps all variables but only the given context lifetimes.
		// But it keeps all unavailable names, i.e. unaccessible lifetimes
		// from the outer scope cannot be redeclared.
		scope = scope.newEnclosingScope(contextLifetimes);

		// Parse the optional lifetime parameters
		List<String> lifetimeParameters = parseOptionalLifetimeParameters(scope);

		match(LeftBrace);
		ArrayList<WhileyFile.Parameter> parameters = new ArrayList<WhileyFile.Parameter>();
		boolean firstTime = true;
		while (eventuallyMatch(MinusGreater) == null) {
			int p_start = index;
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			SyntacticType type = parseType(scope);
			Token id = match(Identifier);
			scope.declareVariable(id);
			parameters.add(wf.new Parameter(type, id.text, sourceAttr(p_start,
					index - 1)));
		}

		// NOTE: expression guanrateed to be terminated by ')'
		Expr body = parseExpression(wf, scope, true);
		match(RightBrace);

		return new Expr.Lambda(parameters, contextLifetimes, lifetimeParameters, body, sourceAttr(start, index - 1));
	}

	/**
	 * Parse an address expression, which has the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *                 | '&' Identifier [ '(' Type (',' Type)* ')']
	 * </pre>
	 *
	 * @param wf
	 *            The enclosing WhileyFile being constructed. This is necessary
	 *            to construct some nested declarations (e.g. parameters for
	 *            lambdas)
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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
	private Expr parseAddressExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {

		int start = index;
		match(Ampersand);
		Token id = match(Identifier);

		// Check whether or not parameters are supplied
		if (tryAndMatch(terminated, LeftBrace) != null) {
			// Yes, parameters are supplied!
			ArrayList<SyntacticType> parameters = new ArrayList<SyntacticType>();
			boolean firstTime = true;
			while (eventuallyMatch(RightBrace) == null) {
				int p_start = index;
				if (!firstTime) {
					match(Comma);
				}
				firstTime = false;
				SyntacticType type = parseType(scope);
				parameters.add(type);
			}
			return new Expr.AbstractFunctionOrMethod(id.text, parameters,
					null, sourceAttr(start, index - 1));
		} else {
			// No, parameters are not supplied.
			return new Expr.AbstractFunctionOrMethod(id.text, null, null,
					sourceAttr(start, index - 1));
		}
	}

	/**
	 * Parse a bitwise complement expression, which has the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *                 | '~' Expr// bitwise complement
	 * </pre>
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
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

	private Expr parseBitwiseComplementExpression(WhileyFile wf,
			EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Tilde);
		Expr expression = parseExpression(wf, scope, terminated);
		return new Expr.UnOp(Expr.UOp.INVERT, expression, sourceAttr(start,
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
	public SyntacticType parseDefiniteType(EnclosingScope scope) {
		int start = index; // backtrack point
		try {
			SyntacticType type = parseType(scope);
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
		} else if (type instanceof SyntacticType.FunctionOrMethod) {
			// "function" and "method" are keywords, cannot parse as expression.
			return true;
		} else if (type instanceof SyntacticType.Intersection) {
			SyntacticType.Intersection tt = (SyntacticType.Intersection) type;
			boolean result = false;
			for (SyntacticType element : tt.bounds) {
				result |= mustParseAsType(element);
			}
			return result;
		} else if (type instanceof SyntacticType.Array) {			
			return true;
		} else if (type instanceof SyntacticType.Negation) {
			SyntacticType.Negation tt = (SyntacticType.Negation) type;
			return mustParseAsType(tt.element);
		} else if (type instanceof SyntacticType.Nominal) {
			return false; // always can be an expression
		} else if (type instanceof SyntacticType.Reference) {
			SyntacticType.Reference tt = (SyntacticType.Reference) type;
			if (tt.lifetime.equals("this") ||
					tt.lifetime.equals("*") && tt.lifetimeWasExplicit) {
				// &this and &* is not a valid expression because "this" is keyword
				// &ident could also be an address expression
				return true;
			}
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
			throw new InternalFailure("unknown syntactic type encountered", entry, type);
		}
	}

	/**
	 * <p>
	 * Determine whether a given expression can *only* be parsed as an
	 * expression, not as a type. This is necessary to check whether a given
	 * unknown expression could be a cast or not. If it must be parsed as an
	 * expression, then it clearly cannot be parsed as a type and, hence, this
	 * is not a cast.
	 * </p>
	 * <p>
	 * The reason that something must be parsed as an expression is because it
	 * contains something which cannot be part of a type. For example,
	 * <code>(*x)</code> could not form part of a cast because the dereference
	 * operator is not permitted within a type. In contrast,
	 * <code>(x.y.f)</code> could be a type if e.g. <code>x.y</code> is a fully
	 * qualified file and <code>f</code> a named item within that.
	 * </p>
	 *
	 * @param e Expression to be checked.
	 * @return
	 */
	private boolean mustParseAsExpr(Expr e) {
		if(e instanceof Expr.LocalVariable) {
			return true;
		} else if(e instanceof Expr.AbstractVariable) {
			return false; // unknown
		} else if(e instanceof Expr.ConstantAccess) {
			return false;
		} else if(e instanceof Expr.FieldAccess) {
			Expr.FieldAccess fa = (Expr.FieldAccess) e;
			return mustParseAsExpr(fa.src);
		} else if(e instanceof Expr.BinOp) {
			Expr.BinOp bop = (Expr.BinOp) e;
			switch (bop.op) {
			case BITWISEOR:
			case BITWISEAND:
				return mustParseAsExpr(bop.lhs) || mustParseAsExpr(bop.rhs);
			}
			return false;
		} else if(e instanceof Expr.UnOp) {
			Expr.UnOp uop = (Expr.UnOp) e;
			switch(uop.op) {
			case NOT:
				return mustParseAsExpr(uop.mhs);
			case ARRAYLENGTH:
			case INVERT:
				return true;
			default:
				return false;
			}
		} else if(e instanceof Expr.AbstractFunctionOrMethod) {
			return true;
		} else if(e instanceof Expr.AbstractInvoke) {
			return true;
		} else if(e instanceof Expr.AbstractIndirectInvoke) {
			return true;
		} else if(e instanceof Expr.Dereference) {
			return true;
		} else if(e instanceof Expr.Cast) {
			return true;
		} else if(e instanceof Expr.Constant) {
			return true;
		} else if(e instanceof Expr.Quantifier) {
			return true;
		} else if(e instanceof Expr.IndexOf) {
			return true;
		} else if(e instanceof Expr.Lambda) {
			return true;
		} else if(e instanceof Expr.ArrayInitialiser) {
			return true;
		} else if(e instanceof Expr.New) {
			return true;
		} else if(e instanceof Expr.Record) {
			return true;
		} else {
			throw new InternalFailure("unknown expression encountered",entry,e);
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
	private SyntacticType parseType(EnclosingScope scope) {
		return parseUnionType(scope);
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
	private SyntacticType parseUnionType(EnclosingScope scope) {
		int start = index;
		SyntacticType t = parseIntersectionType(scope);

		// Now, attempt to look for union and/or intersection types
		if (tryAndMatch(true, VerticalBar) != null) {
			// This is a union type
			ArrayList types = new ArrayList<SyntacticType>();
			types.add(t);
			do {
				types.add(parseIntersectionType(scope));
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
	private SyntacticType parseIntersectionType(EnclosingScope scope) {
		int start = index;
		SyntacticType t = parseArrayType(scope);

		// Now, attempt to look for union and/or intersection types
		if (tryAndMatch(true, Ampersand) != null) {
			// This is a union type
			ArrayList types = new ArrayList<SyntacticType>();
			types.add(t);
			do {
				types.add(parseArrayType(scope));
			} while (tryAndMatch(true, Ampersand) != null);
			return new SyntacticType.Intersection(types, sourceAttr(start,
					index - 1));
		} else {
			return t;
		}
	}

	/**
	 * Parse an array type, which is of the form:
	 *
	 * <pre>
	 * ArrayType ::= Type '[' ']'
	 * </pre>
	 *
	 * @return
	 */
	private SyntacticType parseArrayType(EnclosingScope scope) {
		int start = index;
		SyntacticType element = parseBaseType(scope);
		
		while (tryAndMatch(true, LeftSquare) != null) {
			match(RightSquare);
			element = new SyntacticType.Array(element, sourceAttr(start, index - 1));
		}
		
		return element;
	}
	
	private SyntacticType parseBaseType(EnclosingScope scope) {
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
		case Byte:
			return new SyntacticType.Byte(sourceAttr(start, index++));
		case Int:
			return new SyntacticType.Int(sourceAttr(start, index++));
		case LeftBrace:
			return parseBracketedType(scope);
		case LeftCurly:
			return parseRecordType(scope);
		case Shreak:
			return parseNegationType(scope);
		case Ampersand:
			return parseReferenceType(scope);
		case Identifier:
			return parseNominalType();
		case Function:
			return parseFunctionOrMethodType(true, scope);
		case Method:
			return parseFunctionOrMethodType(false, scope);
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
	private SyntacticType parseNegationType(EnclosingScope scope) {
		int start = index;
		match(Shreak);
		SyntacticType element = parseArrayType(scope);
		return new SyntacticType.Negation(element, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a reference type, which is of the form:
	 *
	 * <pre>
	 * ReferenceType ::= '&' Type
	 *                 | '&' Lifetime ':' Type
	 *      Lifetime ::= Identifier | 'this' | '*'
	 * </pre>
	 *
	 * @return
	 */
	private SyntacticType parseReferenceType(EnclosingScope scope) {
		int start = index;
		match(Ampersand);

		// Try to parse an annotated lifetime
		int backtrack = index;
		Token lifetimeIdentifier = tryAndMatch(true, Identifier, This, Star);
		if (lifetimeIdentifier != null) {
			// We cannot allow a newline after the colon, as it would
			// unintentionally match a return type that happens to be reference
			// type without lifetime annotation (return type in method signature
			// is always followed by colon and newline).
			if (tryAndMatch(true, Colon) != null && !isAtEOL()) {
				// Now we know that there is an annotated lifetime
				scope.mustBeLifetime(lifetimeIdentifier);
				SyntacticType element = parseArrayType(scope);
				return new SyntacticType.Reference(element, lifetimeIdentifier.text, true,
						sourceAttr(start, index - 1));
			}
		}
		index = backtrack;

		SyntacticType element = parseArrayType(scope);
		return new SyntacticType.Reference(element, "*", false, sourceAttr(start, index - 1));
	}

	/**
	 * Parse a currently declared lifetime.
	 * 
	 * @return the matched lifetime name
	 */
	private String parseLifetime(EnclosingScope scope, boolean terminated) {
		int next = terminated ? skipWhiteSpace(index) : skipLineSpace(index);
		if (next < tokens.size()) {
			Token t = tokens.get(next);
			if (t.kind == Identifier || t.kind == This || t.kind == Star) {
				index = next + 1;
				scope.mustBeLifetime(t);
				return t.text;
			}
			syntaxError("expectiong a lifetime identifier here", t);
		}
		syntaxError("unexpected end-of-file", tokens.get(next - 1));
		throw new RuntimeException("deadcode"); // dead-code
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
	private SyntacticType parseBracketedType(EnclosingScope scope) {
		int start = index;
		match(LeftBrace);
		SyntacticType type = parseType(scope);
		match(RightBrace);
		return type;
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
	 * (e.g. function read()->[byte]).
	 *
	 * @return
	 */
	private SyntacticType parseRecordType(EnclosingScope scope) {
		int start = index;
		match(LeftCurly);

		HashMap<String, SyntacticType> types = new HashMap<String, SyntacticType>();
		Pair<SyntacticType, Token> p = parseMixedType(scope);
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
				p = parseMixedType(scope);
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
	 * Parse a function or method type, which is of the form:
	 *
	 * <pre>
	 * FunctionType ::= "function" [Type (',' Type)* ] "->" Type
	 * MethodType   ::= "method" [Type (',' Type)* ] "->" Type
	 * </pre>
	 *
	 * At the moment, it is required that parameters for a function or method
	 * type are enclosed in braces. In principle, we would like to relax this.
	 * However, this is difficult to make work because there is not way to
	 * invoke a function or method without using braces.
	 *
	 * @return
	 */
	private SyntacticType parseFunctionOrMethodType(boolean isFunction, EnclosingScope scope) {
		int start = index;

		List<String> lifetimeParameters;
		Set<String> contextLifetimes;
		if (isFunction) {
			match(Function);
			contextLifetimes = Collections.emptySet();
			lifetimeParameters = Collections.emptyList();
		} else {
			match(Method);
			contextLifetimes = parseOptionalContextLifetimes(scope);
			scope = scope.newEnclosingScope();
			lifetimeParameters = parseOptionalLifetimeParameters(scope);
		}

		// First, parse the parameter type(s).
		List<SyntacticType> paramTypes = parseParameterTypes(scope);
		List<SyntacticType> returnTypes = Collections.emptyList();

		// Second, parse the right arrow.
		if (isFunction) {
			// Functions require a return type (since otherwise they are just
			// nops)
			match(MinusGreater);
			// Third, parse the return types.
			returnTypes = parseOptionalParameterTypes(scope);
		} else if (tryAndMatch(true, MinusGreater) != null) {
			// Methods have an optional return type
			// Third, parse the return type
			returnTypes = parseOptionalParameterTypes(scope);
		} 

		// Done
		if (isFunction) {
			return new SyntacticType.Function(returnTypes, paramTypes, sourceAttr(start, index - 1));
		} else {
			return new SyntacticType.Method(returnTypes, paramTypes, contextLifetimes,
					lifetimeParameters, sourceAttr(start, index - 1));
		}
	}	

	/**
	 * Parse a potentially mixed-type, which is of the form:
	 *
	 * <pre>
	 * MixedType ::= Type Identifier
	 *            |  "function" Type Identifier '(' [Type (',' Type)* ] ')' "->" Type
	 *            |  "method" Type Identifier '(' [Type (',' Type)* ] ')' "->" Type
	 * </pre>
	 *
	 * @return
	 */
	private Pair<SyntacticType, Token> parseMixedType(EnclosingScope scope) {
		Token lookahead;
		int start = index;

		if ((lookahead = tryAndMatch(true, Function, Method)) != null) {
			// At this point, we *might* have a mixed function / method type
			// definition. To disambiguate, we need to see whether an identifier
			// follows or not.
			// Similar to normal method declarations, the lifetime parameters
			// go before the method name. We do not allow to have context lifetimes
			// for mixed method types.
			List<String> lifetimeParameters = Collections.emptyList();
			if (lookahead.kind == Method && tryAndMatch(true, LeftAngle) != null) {
				// mixed method type with lifetime parameters
				scope = scope.newEnclosingScope();
				lifetimeParameters = parseLifetimeParameters(scope);
			}

			// Now try to parse the identifier
			Token id = tryAndMatch(true, Identifier);

			if (id != null) {
				// Yes, we have found a mixed function / method type definition.
				// Therefore, we continue to pass the remaining type parameters.

				List<SyntacticType> paramTypes = parseParameterTypes(scope);
				List<SyntacticType> returnTypes = Collections.emptyList();
				
				if (lookahead.kind == Function) {
					// Functions require a return type (since otherwise they are
					// just nops)
					match(MinusGreater);
					// Third, parse the return type
					returnTypes = parseOptionalParameterTypes(scope);
				} else if (tryAndMatch(true, MinusGreater) != null) {
					// Third, parse the (optional) return type. Observe that
					// this is forced to be a
					// unit type. This means that any tuple return types must be
					// in braces. The reason for this is that a trailing comma
					// may be part of an enclosing record type and we must
					// disambiguate
					// this.
					returnTypes = parseOptionalParameterTypes(scope);
				} 

				// Done
				SyntacticType type;
				if (lookahead.kind == Token.Kind.Function) {
					type = new SyntacticType.Function(returnTypes, paramTypes, sourceAttr(start, index - 1));
				} else {
					type = new SyntacticType.Method(returnTypes, paramTypes, Collections.<String>emptySet(),
							lifetimeParameters, sourceAttr(start, index - 1));
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
		SyntacticType type = parseType(scope);
		Token id = match(Identifier);
		return new Pair<SyntacticType, Token>(type, id);
	}

	public List<SyntacticType> parseOptionalParameterTypes(EnclosingScope scope) {
		int next = skipWhiteSpace(index);
		if(next < tokens.size() && tokens.get(next).kind == LeftBrace) {
			return parseParameterTypes(scope);
		} else {
			SyntacticType t = parseType(scope);
			ArrayList<SyntacticType> rs = new ArrayList<SyntacticType>();
			rs.add(t);
			return rs;
		}		
	}
	
	public List<SyntacticType> parseParameterTypes(EnclosingScope scope) {
		ArrayList<SyntacticType> paramTypes = new ArrayList<SyntacticType>();
		match(LeftBrace);

		boolean firstTime = true;
		while (eventuallyMatch(RightBrace) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			paramTypes.add(parseType(scope));
		}
		
		return paramTypes;
	}

	/**
	 * Attention: Enters the lifetime names to the passed scope!
	 * @param scope
	 * @return
	 */
	public List<String> parseOptionalLifetimeParameters(EnclosingScope scope) {
		if (tryAndMatch(true, LeftAngle) != null && tryAndMatch(true, RightAngle) == null) {
			// The if above skips an empty list of identifiers "<>"!
			return parseLifetimeParameters(scope);
		}
		return Collections.emptyList();
	}

	/**
	 * Attention: Enters the lifetime names to the passed scope!
	 * Assumes that '<' has already been matched.
	 * @param scope
	 * @return
	 */
	private List<String> parseLifetimeParameters(EnclosingScope scope) {
		List<String> lifetimeParameters = new ArrayList<String>();
		do {
			Token lifetimeIdentifier = match(Identifier);
			scope.declareLifetime(lifetimeIdentifier);
			lifetimeParameters.add(lifetimeIdentifier.text);
		} while (tryAndMatch(true, Comma) != null);
		match(RightAngle);
		return lifetimeParameters;
	}


	/**
	 * @param scope
	 * @return
	 */
	public Set<String> parseOptionalContextLifetimes(EnclosingScope scope) {
		if (tryAndMatch(true, LeftSquare) != null && tryAndMatch(true, RightSquare) == null) {
			// The if above skips an empty list of identifiers "[]"!
			Set<String> contextLifetimes = new HashSet<String>();
			do {
				contextLifetimes.add(parseLifetime(scope, true));
			} while (tryAndMatch(true, Comma) != null);
			match(RightSquare);
			return contextLifetimes;
		}
		return Collections.emptySet();
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
	 * Attempt to match a given token(s) at a given level of indent, whilst
	 * ignoring any whitespace in between. Note that, in the case it fails to
	 * match, then the index will be unchanged. This latter point is important,
	 * otherwise we could accidentally gobble up some important indentation. If
	 * more than one kind is provided then this will try to match any of them.
	 *
	 * @param terminated
	 *            Indicates whether or not this function should be concerned
	 *            with new lines. The terminated flag indicates whether or not
	 *            the current construct being parsed is known to be terminated.
	 *            If so, then we don't need to worry about newlines and can
	 *            greedily consume them (i.e. since we'll eventually run into
	 *            the terminating symbol).
	 * @param indent
	 *            The indentation level to try and match the tokens at.
	 * @param kinds
	 *
	 * @return
	 */
	private Token tryAndMatchAtIndent(boolean terminated, Indent indent, Token.Kind... kinds) {
		int start = index;
		Indent r = getIndent();
		if(r != null && r.equivalent(indent)) {
			Token t = tryAndMatch(terminated,kinds);
			if(t != null) {
				return r;
			}
		}
		// backtrack in all failing cases.
		index = start;
		return null;
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
	 * Attempt to match a given sequence of tokens in the given order, whilst
	 * ignoring any whitespace in between. Note that, in any case, the index
	 * will be unchanged!
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
	 * @return whether the sequence matches
	 */
	private boolean lookaheadSequence(boolean terminated, Token.Kind... kinds) {
		int next = index;
		for (Token.Kind k : kinds) {
			next = terminated ? skipWhiteSpace(next) : skipLineSpace(next);
			if (next >= tokens.size() || tokens.get(next++).kind != k) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether the current index is, after skipping all line spaces, at
	 * the end of a line. This method does not change the state!
	 * 
	 * @return whether index is at end of line
	 */
	private boolean isAtEOL() {
		int next = skipLineSpace(index);
		return next >= tokens.size() || tokens.get(next).kind == NewLine;
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
			if(index > 0) {
				syntaxError("unexpected end-of-file",tokens.get(index-1));
			} else {
				// I believe this is actually dead-code, since checkNotEof()
				// won't be called before at least one token is matched.
				throw new SyntaxError("unexpected end-of-file", entry, null);
			}
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
					&& tokens.get(tmp).kind != Token.Kind.NewLine) {
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
		return token.kind == Token.Kind.NewLine || isLineSpace(token);
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
	private BigInteger parseCharacter(String input) {
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
		return BigInteger.valueOf(c);
	}

	/**
	 * Parse a string constant whilst interpreting all escape characters.
	 *
	 * @param v
	 * @return
	 */
	protected List<Constant> parseString(String v) {
		/*
		 * Parsing a string requires several steps to be taken. First, we need
		 * to strip quotes from the ends of the string.
		 */
		v = v.substring(1, v.length() - 1);

		ArrayList<Constant> result = new ArrayList<Constant>();
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
						i = i + 5;
						break;
					default:
						throw new RuntimeException("unknown escape character");
					}
					result.add(new Constant.Integer(BigInteger.valueOf(replace)));
					i = i + 1;
				}
			} else {
				result.add(new Constant.Integer(BigInteger.valueOf(v.charAt(i))));
			}
		}
		return result;
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
		throw new SyntaxError(msg, entry, e);
	}

	private void syntaxError(String msg, Token t) {
		// FIXME: this is clearly not a sensible approach
		SyntacticElement unknown = new SyntacticElement.Impl() {
		};
		unknown.attributes().add(new Attribute.Source(t.start, t.start + t.text.length() - 1, -1));
		throw new SyntaxError(msg, entry, unknown);

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
	
	/**
	 * The enclosing scope provides contextual information about the enclosing
	 * scope for the given statement or expression being parsed.
	 * 
	 * @author David J. Pearce
	 *
	 */
	private class EnclosingScope {
		/**
		 * The indent level of the enclosing scope.
		 */		
		private final Indent indent;
		
		/**
		 * The set of declared variables in the enclosing scope.
		 */
		private final HashSet<String> variables;

		/**
		 * The set of declared lifetimes in the enclosing scope.
		 */
		private final HashSet<String> lifetimes;

		/**
		 * The set of all names that cannot be used for variables or lifetimes.
		 * They are either in the variables or lifetimes set, or a special lifetime,
		 * or they are unavailable because it is an unaccessible lifetime from
		 * an outer scope.
		 */
		private final HashSet<String> unavailableNames;
		
		/**
		 * A simple flag that tells us whether or not we are currently within a
		 * loop. This is necessary to stop break or continue statements which
		 * are written outside of a loop.
		 */
		private final boolean inLoop;
		
		public EnclosingScope() {
			this.indent = ROOT_INDENT;
			this.variables = new HashSet<String>();
			this.lifetimes = new HashSet<String>();
			this.unavailableNames = new HashSet<String>();
			this.inLoop = false;

			// prevent declaring these lifetimes
			this.unavailableNames.add("*");
			this.unavailableNames.add("this");
		}
		
		private EnclosingScope(Indent indent, Set<String> variables, Set<String> lifetimes,
				Set<String> unavailableNames, boolean inLoop) {
			this.indent = indent;
			this.variables = new HashSet<String>(variables);
			this.lifetimes = new HashSet<String>(lifetimes);
			this.unavailableNames = new HashSet<String>(unavailableNames);
			this.inLoop = inLoop;
		}
		
		public Indent getIndent() {
			return indent;
		}
		
		public boolean isInLoop() {
			return inLoop;
		}

		/**
		 * Check whether a given name corresponds to a declared variable in this
		 * scope.
		 * 
		 * @param name
		 * @return
		 */
		public boolean isVariable(String name) {
			return this.variables.contains(name);
		}

		/**
		 * Check whether a given name corresponds to a declared lifetime in this
		 * scope.
		 * 
		 * @param name
		 * @return
		 */
		public boolean isLifetime(String name) {
			return name.equals("*") || this.lifetimes.contains(name);
		}

		/**
		 * Checks that the given identifier is a declared lifetime.
		 * 
		 * @param id
		 * @throws SyntaxError
		 *             if the given identifier is not a lifetime
		 */
		public void mustBeLifetime(Token id) {
			if (!this.isLifetime(id.text)) {
				syntaxError("use of undeclared lifetime", id);
			}
		}

		/**
		 * Check whether a given name is available, i.e. can be declared.
		 * 
		 * @param id
		 *            identifier that holds the name to check
		 * @throws SyntaxError
		 *             if the name is unavailable (already declared)
		 */
		public void checkNameAvailable(Token id) {
			if (this.unavailableNames.contains(id.text)) {
				// name is not available!
				syntaxError("name already declared", id);
			}
		}

		/**
		 * Check whether a given name is available, i.e. can be declared.
		 * 
		 * @param p
		 *            parameter that holds the name to check
		 * @throws SyntaxError
		 *             if the name is unavailable (already declared)
		 */
		public void checkNameAvailable(Parameter p) {
			if (this.unavailableNames.contains(p.name)) {
				// name is not available!
				syntaxError("name already declared", p);
			}
		}

		/**
		 * Declare a new variable in this scope.
		 * 
		 * @param id
		 *            identifier that holds the name to declare
		 * @throws SyntaxError
		 *             if the name is already declared
		 */
		public void declareVariable(Token id) {
			if (!this.unavailableNames.add(id.text)) {
				// name is not available!
				syntaxError("name already declared", id);
			}
			this.variables.add(id.text);
		}

		/**
		 * Declare a new variable in this scope.
		 * 
		 * @param p
		 *            parameter that holds the name to declare
		 * @throws SyntaxError
		 *             if the name is already declared
		 */
		public void declareVariable(Parameter p) {
			if (!this.unavailableNames.add(p.name)) {
				// name is not available!
				syntaxError("name already declared", p);
			}
			this.variables.add(p.name);
		}

		/**
		 * Declare a new lifetime in this scope.
		 * 
		 * @param id
		 *            identifier that holds the name to declare
		 * @throws SyntaxError
		 *             if the name is already declared
		 */
		public void declareLifetime(Token id) {
			if (!this.unavailableNames.add(id.text)) {
				// name is not available!
				syntaxError("name already declared", id);
			}
			this.lifetimes.add(id.text);
		}

		/**
		 * Make lifetime "this" available.
		 */
		public void declareThisLifetime() {
			this.lifetimes.add("this");
		}

		/**
		 * Create a new enclosing scope in which variables can be declared which
		 * are remain invisible to this enclosing scope. All variables declared
		 * in this enclosing scope remain declared in the new enclosing scope.
		 * 
		 * @param indent
		 *            the indent level for the new scope
		 * 
		 * @return
		 */
		public EnclosingScope newEnclosingScope() {
			return new EnclosingScope(indent,variables,lifetimes,unavailableNames,inLoop);
		}
		
		/**
		 * Create a new enclosing scope in which variables can be declared which
		 * are remain invisible to this enclosing scope. All variables declared
		 * in this enclosing scope remain declared in the new enclosing scope.
		 * 
		 * @param indent
		 *            the indent level for the new scope
		 * 
		 * @return
		 */
		public EnclosingScope newEnclosingScope(Indent indent) {
			return new EnclosingScope(indent,variables,lifetimes,unavailableNames,inLoop);
		}
		
		/**
		 * Create a new enclosing scope in which variables can be declared which
		 * are remain invisible to this enclosing scope. All variables declared
		 * in this enclosing scope remain declared in the new enclosing scope.
		 * 
		 * @param indent
		 *            the indent level for the new scope
		 * 
		 * @return
		 */
		public EnclosingScope newEnclosingScope(Indent indent, boolean inLoop) {
			return new EnclosingScope(indent,variables,lifetimes,unavailableNames,inLoop);
		}

		/**
		 * Create a new enclosing scope in which variables can be declared which
		 * are remain invisible to this enclosing scope. All variables declared
		 * in this enclosing scope remain declared in the new enclosing scope.
		 * 
		 * @param indent
		 *            the indent level for the new scope
		 * 
		 * @return
		 */
		public EnclosingScope newEnclosingScope(Set<String> contextLifetimes) {
			return new EnclosingScope(indent,variables,contextLifetimes,unavailableNames,false);
		}
	}	
}
