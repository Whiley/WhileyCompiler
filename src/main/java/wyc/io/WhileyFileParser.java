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
package wyc.io;

import static wyc.io.WhileyFileLexer.Token.Kind.All;
import static wyc.io.WhileyFileLexer.Token.Kind.Ampersand;
import static wyc.io.WhileyFileLexer.Token.Kind.Assert;
import static wyc.io.WhileyFileLexer.Token.Kind.Assume;
import static wyc.io.WhileyFileLexer.Token.Kind.Break;
import static wyc.io.WhileyFileLexer.Token.Kind.Caret;
import static wyc.io.WhileyFileLexer.Token.Kind.Case;
import static wyc.io.WhileyFileLexer.Token.Kind.Colon;
import static wyc.io.WhileyFileLexer.Token.Kind.ColonColon;
import static wyc.io.WhileyFileLexer.Token.Kind.Comma;
import static wyc.io.WhileyFileLexer.Token.Kind.Continue;
import static wyc.io.WhileyFileLexer.Token.Kind.Debug;
import static wyc.io.WhileyFileLexer.Token.Kind.Default;
import static wyc.io.WhileyFileLexer.Token.Kind.Do;
import static wyc.io.WhileyFileLexer.Token.Kind.Dot;
import static wyc.io.WhileyFileLexer.Token.Kind.DotDot;
import static wyc.io.WhileyFileLexer.Token.Kind.DotDotDot;
import static wyc.io.WhileyFileLexer.Token.Kind.Else;
import static wyc.io.WhileyFileLexer.Token.Kind.Ensures;
import static wyc.io.WhileyFileLexer.Token.Kind.Equals;
import static wyc.io.WhileyFileLexer.Token.Kind.EqualsEquals;
import static wyc.io.WhileyFileLexer.Token.Kind.Export;
import static wyc.io.WhileyFileLexer.Token.Kind.Fail;
import static wyc.io.WhileyFileLexer.Token.Kind.For;
import static wyc.io.WhileyFileLexer.Token.Kind.Final;
import static wyc.io.WhileyFileLexer.Token.Kind.Function;
import static wyc.io.WhileyFileLexer.Token.Kind.GreaterEquals;
import static wyc.io.WhileyFileLexer.Token.Kind.Identifier;
import static wyc.io.WhileyFileLexer.Token.Kind.If;
import static wyc.io.WhileyFileLexer.Token.Kind.Import;
import static wyc.io.WhileyFileLexer.Token.Kind.In;
import static wyc.io.WhileyFileLexer.Token.Kind.Indent;
import static wyc.io.WhileyFileLexer.Token.Kind.Is;
import static wyc.io.WhileyFileLexer.Token.Kind.LeftAngle;
import static wyc.io.WhileyFileLexer.Token.Kind.LeftAngleLeftAngle;
import static wyc.io.WhileyFileLexer.Token.Kind.LeftBrace;
import static wyc.io.WhileyFileLexer.Token.Kind.LeftCurly;
import static wyc.io.WhileyFileLexer.Token.Kind.LeftSquare;
import static wyc.io.WhileyFileLexer.Token.Kind.LessEquals;
import static wyc.io.WhileyFileLexer.Token.Kind.LogicalAnd;
import static wyc.io.WhileyFileLexer.Token.Kind.LogicalIff;
import static wyc.io.WhileyFileLexer.Token.Kind.LogicalImplication;
import static wyc.io.WhileyFileLexer.Token.Kind.LogicalOr;
import static wyc.io.WhileyFileLexer.Token.Kind.Method;
import static wyc.io.WhileyFileLexer.Token.Kind.Minus;
import static wyc.io.WhileyFileLexer.Token.Kind.MinusGreater;
import static wyc.io.WhileyFileLexer.Token.Kind.Native;
import static wyc.io.WhileyFileLexer.Token.Kind.New;
import static wyc.io.WhileyFileLexer.Token.Kind.NewLine;
import static wyc.io.WhileyFileLexer.Token.Kind.NotEquals;
import static wyc.io.WhileyFileLexer.Token.Kind.Package;
import static wyc.io.WhileyFileLexer.Token.Kind.Percent;
import static wyc.io.WhileyFileLexer.Token.Kind.Plus;
import static wyc.io.WhileyFileLexer.Token.Kind.Private;
import static wyc.io.WhileyFileLexer.Token.Kind.Property;
import static wyc.io.WhileyFileLexer.Token.Kind.Public;
import static wyc.io.WhileyFileLexer.Token.Kind.Requires;
import static wyc.io.WhileyFileLexer.Token.Kind.Return;
import static wyc.io.WhileyFileLexer.Token.Kind.RightAngle;
import static wyc.io.WhileyFileLexer.Token.Kind.RightAngleRightAngle;
import static wyc.io.WhileyFileLexer.Token.Kind.RightBrace;
import static wyc.io.WhileyFileLexer.Token.Kind.RightCurly;
import static wyc.io.WhileyFileLexer.Token.Kind.RightSlash;
import static wyc.io.WhileyFileLexer.Token.Kind.RightSquare;
import static wyc.io.WhileyFileLexer.Token.Kind.SemiColon;
import static wyc.io.WhileyFileLexer.Token.Kind.Shreak;
import static wyc.io.WhileyFileLexer.Token.Kind.Skip;
import static wyc.io.WhileyFileLexer.Token.Kind.Some;
import static wyc.io.WhileyFileLexer.Token.Kind.Star;
import static wyc.io.WhileyFileLexer.Token.Kind.Subset;
import static wyc.io.WhileyFileLexer.Token.Kind.SubsetEquals;
import static wyc.io.WhileyFileLexer.Token.Kind.Superset;
import static wyc.io.WhileyFileLexer.Token.Kind.SupersetEquals;
import static wyc.io.WhileyFileLexer.Token.Kind.Switch;
import static wyc.io.WhileyFileLexer.Token.Kind.This;
import static wyc.io.WhileyFileLexer.Token.Kind.Tilde;
import static wyc.io.WhileyFileLexer.Token.Kind.QuestionMark;
import static wyc.io.WhileyFileLexer.Token.Kind.VerticalBar;
import static wyc.io.WhileyFileLexer.Token.Kind.Where;
import static wyc.io.WhileyFileLexer.Token.Kind.While;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wybs.lang.Build;
import wybs.lang.SyntacticException;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Attribute;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Pair;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.io.WhileyFileLexer.Token;
import wyc.lang.WhileyFile;
import wyc.util.ErrorMessages;
import wyfs.lang.Path;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.LVal;
import wyil.lang.WyilFile.Modifier;
import wyil.lang.WyilFile.Stmt;
import wyil.lang.WyilFile.Template;
import wyil.lang.WyilFile.Template.Variance;
import wyil.lang.WyilFile.Type;

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
	private final WyilFile parent;
	private final WhileyFile source;
	private ArrayList<Token> tokens;
	private int index;

	public WhileyFileParser(WyilFile target, WhileyFile source) {
		if(target == null) {
			throw new IllegalArgumentException("target cannot be null");
		} else if(source == null) {
			throw new IllegalArgumentException("source cannot be null");
		}
		this.parent = target;
		this.source = source;
		this.tokens = new ArrayList<>(source.getTokens());
	}

	/**
	 * Read a <code>WhileyFile</code> from the token stream. If the stream is
	 * invalid in some way (e.g. contains a syntax error, etc) then a
	 * <code>SyntaxError</code> is thrown.
	 *
	 * @return
	 */
	public boolean read(Build.Meter meter) {
		boolean status = true;
		ArrayList<Decl> declarations = new ArrayList<>();
		Name name = new Name(new Identifier(source.getID().last()));
		try {
			name = parseModuleName(source.getID());
			skipWhiteSpace();
			while (index < tokens.size()) {
				// Parse next logical declaration
				declarations.add(parseDeclaration(meter));
				skipWhiteSpace();
			}
		} catch (ParseError e) {
			// Allocate an unknown declaration to represent this parse error.
			Decl d = parent.allocate(new Decl.Unknown());
			// Add to declarations for enclosing unit
			declarations.add(d);
			// Give the unknown declaration a span corresponding to exact point of error.
			parent.allocate(new Attribute.Span(d,e.getStart(),e.getEnd()));
			// Generate a syntax error which identifies the parse error
			ErrorMessages.syntaxError(d, e.getErrorCode(),e.context);
			// Signal that we have failed
			status = false;
		}
		// Finally, construct the new file.
		Tuple<Decl> decls = new Tuple<>(declarations);
		Decl.Unit module = new Decl.Unit(name, decls);
		//
		Decl.Unit nunit = parent.allocate(module);
		Decl.Unit ounit = parent.getModule().putUnit(nunit);
		if (ounit != null) {
			parent.replace(ounit, nunit);
		}
		return status;
	}

	private Name parseModuleName(Path.ID id) {
		ArrayList<Identifier> components = new ArrayList<>();
		if (tryAndMatch(true, Package) != null) {
			// found a package keyword
			components.add(parseIdentifier());

			while (tryAndMatch(true, ColonColon) != null) {
				components.add(parseIdentifier());
			}

			matchEndLine();
		}
		Identifier[] bits = components.toArray(new Identifier[components.size() + 1]);
		// FIXME: this is so completely broken
		bits[bits.length - 1] = new Identifier(id.last());
		return new Name(bits);
	}

	private Decl parseDeclaration(Build.Meter meter) {
		meter.step("declaration");
		int start = index;
		Token lookahead = tokens.get(index);
		if (lookahead.kind == Import) {
			return parseImportDeclaration(meter);
		} else {
			return parseNamedDeclaration(meter);
		}
	}

	private Decl.Named<? extends Type> parseNamedDeclaration(Build.Meter meter) {
		// Parse any modifiers
		Tuple<Modifier> modifiers = parseModifiers(Public, Private, Native, Export, Final);
		checkNotEof();
		//
		Token lookahead = tokens.get(index);
		if (lookahead.text.equals("type")) {
			return parseTypeDeclaration(meter,modifiers);
		} else if (lookahead.kind == Function) {
			return parseFunctionOrMethodDeclaration(meter,modifiers, true);
		} else if (lookahead.kind == Method) {
			return parseFunctionOrMethodDeclaration(meter,modifiers, false);
		} else if (lookahead.kind == Property) {
			return parsePropertyDeclaration(meter,modifiers);
		} else {
			// Fall back
			return parseStaticVariableDeclaration(meter,modifiers);
		}
	}

	/**
	 * Parse an import declaration, which is of the form:
	 *
	 * <pre>
	 * ImportDecl ::= Identifier ["from" ('*' | Identifier)] ( '::' ('*' | Identifier) )*
	 * </pre>
	 */
	private Decl parseImportDeclaration(Build.Meter meter) {
		int start = index;
		EnclosingScope scope = new EnclosingScope(meter);
		match(Import);
		Tuple<Identifier> names = parseOptionalFroms(scope);
		Tuple<Identifier> filterPath = parseFilterPath(scope);
		Decl.Import imprt;
		if(names != null) {
			imprt = new Decl.Import(filterPath, false, names);
		} else {
			names = parseOptionalWiths(scope);
			if(names != null) {
				imprt = new Decl.Import(filterPath, true, names);
			} else {
				imprt = new Decl.Import(filterPath);
			}
		}
		matchEndLine();
		return annotateSourceLocation(imprt, start);
	}

	private Tuple<Identifier> parseOptionalFroms(EnclosingScope scope) {
		int start = index;
		ArrayList<Identifier> froms = new ArrayList<>();
		froms.add(parseStarOrIdentifier(scope));
		while (tryAndMatch(false, Comma) != null) {
			froms.add(parseIdentifier());
		}
		// Lookahead to see whether optional "from" component was specified or not.
		Token lookahead = tryAndMatch(true, Identifier);
		if (lookahead != null) {
			// Optional from identifier was given
			if(lookahead.text.equals("with")) {
				// Backtrack
				index = start;
				return null;
			} else if (!lookahead.text.equals("from")) {
				syntaxError(WyilFile.EXPECTING_TOKEN, lookahead, new Value.UTF8("from"));
			}
			return new Tuple<>(froms);
		} else {
			// Optional from identifier was not given. Therefore, backtrack.
			index = start;
			return null;
		}
	}

	private Tuple<Identifier> parseOptionalWiths(EnclosingScope scope) {
		// Lookahead to see whether optional "with" component was specified or not.
		Token lookahead = tryAndMatch(false, Identifier);
		if (lookahead != null) {
			// Optional from identifier was given
			if (!lookahead.text.equals("with")) {
				syntaxError(WyilFile.EXPECTING_TOKEN, lookahead, new Value.UTF8("with"));
			}
			ArrayList<Identifier> withs = new ArrayList<>();
			withs.add(parseStarOrIdentifier(scope));
			while (tryAndMatch(false, Comma) != null) {
				withs.add(parseIdentifier());
			}
			return new Tuple<>(withs);
		} else {
			return null;
		}
	}

	private Tuple<Identifier> parseFilterPath(EnclosingScope scope) {
		// Parse package filter string
		ArrayList<Identifier> components = new ArrayList<>();
		components.add(parseIdentifier());
		while (tryAndMatch(true, ColonColon) != null) {
			Identifier component = parseStarOrIdentifier(scope);
			components.add(component);
		}
		//
		return new Tuple<>(components);
	}

	private Identifier parseStarOrIdentifier(EnclosingScope scope) {
		if (tryAndMatch(true, Star) != null) {
			return new Identifier("*");
		} else {
			return parseIdentifier();
		}
	}

	private Tuple<Modifier> parseModifiers(Token.Kind... kinds) {
		ArrayList<Modifier> mods = new ArrayList<>();
		Token lookahead;
		boolean visible = false;
		while ((lookahead = tryAndMatch(true, kinds)) != null) {
			switch (lookahead.kind) {
			case Public:
			case Private:
				if (visible) {
					syntaxError(WyilFile.DUPLICATE_VISIBILITY_MODIFIER, lookahead);
				}
			}
			switch (lookahead.kind) {
			case Public:
				mods.add(annotateSourceLocation(new Modifier.Public(),index-1));
				visible = true;
				break;
			case Private:
				mods.add(annotateSourceLocation(new Modifier.Private(),index-1));
				visible = true;
				break;
			case Native:
				mods.add(annotateSourceLocation(new Modifier.Native(),index-1));
				break;
			case Export:
				mods.add(annotateSourceLocation(new Modifier.Export(),index-1));
				break;
			case Final:
				mods.add(annotateSourceLocation(new Modifier.Final(),index-1));
				break;
			}
		}
		return new Tuple<>(mods);
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
	private Decl.FunctionOrMethod parseFunctionOrMethodDeclaration(Build.Meter meter, Tuple<Modifier> modifiers,
			boolean isFunction) {
		int start = index;
		// Create appropriate enclosing scope
		EnclosingScope scope = new EnclosingScope(meter);
		//
		if (isFunction) {
			match(Function);
		} else {
			match(Method);
		}
		Identifier name = parseIdentifier();
		// Parse template parameters
		Tuple<Template.Variable> template = parseOptionalTemplate(scope);
		// Parse function or method parameters
		Tuple<Decl.Variable> parameters = parseParameters(scope,RightBrace);
		// Parse (optional) return type
		Tuple<Decl.Variable> returns;
		//
		if (tryAndMatch(true, MinusGreater) != null) {
			// Explicit return type is given, so parse it! We first clone the
			// environent and create a special one only for use within ensures
			// clauses, since these are the only expressions which may refer to
			// variables declared in the return type.
			returns = parseOptionalParameters(scope);
		} else {
			// No returns provided
			returns = new Tuple<>();
		}
		// Parse optional requires/ensures clauses
		Tuple<Expr> requires = parseInvariant(scope,Requires);
		Tuple<Expr> ensures = parseInvariant(scope,Ensures);
		// Parse function or method body (if not native)
		Stmt.Block body;
		int end;
		if(modifiers.match(Modifier.Native.class) == null) {
			// Not native function or method
			match(Colon);
			end = index;
			matchEndLine();
			body = parseBlock(scope, false);
		} else {
			end = index;
			matchEndLine();
			// FIXME: having empty block seems wasteful
			body = new Stmt.Block();
		}
		//
		WyilFile.Decl.FunctionOrMethod declaration;
		if (isFunction) {
			declaration = new Decl.Function(modifiers, name, template, parameters, returns, requires, ensures,
					body);
		} else {
			declaration = new Decl.Method(modifiers, name, template, parameters, returns, requires, ensures, body);
		}
		return annotateSourceLocation(declaration,start,end-1);
	}

	/**
	 * Parse a <i>property declaration</i> which has the form:
	 *
	 * <pre>
	 * ProeprtyDeclaration ::= "property" Parameters "->" Parameters (WhereClause)*
	 * PropertyClause ::= "where" Expr
	 * </pre>
	 *
	 */
	private Decl.Property parsePropertyDeclaration(Build.Meter meter, Tuple<Modifier> modifiers) {
		EnclosingScope scope = new EnclosingScope(meter);
		int start = index;
		match(Property);
		Identifier name = parseIdentifier();
		Tuple<Template.Variable> template = parseOptionalTemplate(scope);
		Tuple<Decl.Variable> parameters = parseParameters(scope,RightBrace);
		Tuple<Expr> invariant = parseInvariant(scope,Where);
		//
		int end = index;
		matchEndLine();
		return annotateSourceLocation(new Decl.Property(modifiers, name, template, parameters, invariant), start);
	}

	public Tuple<Decl.Variable> parseParameters(EnclosingScope scope, Token.Kind terminator) {
		match(LeftBrace);
		ArrayList<Decl.Variable> parameters = new ArrayList<>();
		boolean firstTime = true;
		while (eventuallyMatch(terminator) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			int start = index;
			Tuple<Modifier> modifiers = parseModifiers(Final);
			Pair<Type, Identifier> p = parseMixedType(scope);
			Identifier id = p.getSecond();
			Decl.Variable decl = new Decl.Variable(modifiers, id, p.getFirst());
			decl = annotateSourceLocation(decl, start);
			scope.declareVariable(decl);
			parameters.add(decl);
		}
		return new Tuple<>(parameters);
	}

	public Tuple<Expr> parseInvariant(EnclosingScope scope, Token.Kind kind) {
		ArrayList<Expr> invariant = new ArrayList<>();
		// Check whether or not there are optional "where" clauses.
		while (tryAndMatch(true, kind) != null) {
			invariant.add(parseLogicalExpression(scope, false));
		}
		return new Tuple<>(invariant);
	}

	public Tuple<Decl.Variable> parseOptionalParameters(EnclosingScope scope) {
		int next = skipWhiteSpace(index);
		if (next < tokens.size() && tokens.get(next).kind == LeftBrace) {
			return parseParameters(scope,RightBrace);
		} else {
			return new Tuple<>(parseOptionalParameter(scope));
		}
	}

	public Decl.Variable parseOptionalParameter(EnclosingScope scope) {
		int start = index;
		boolean braced = false;
		Type type;
		Identifier name;
		if (tryAndMatch(true, LeftBrace) != null) {
			Pair<Type, Identifier> p = parseMixedType(scope);
			type = p.getFirst();
			name = p.getSecond();
			match(RightBrace);
		} else {
			type = parseType(scope);
			// The following anonymous variable name is used in order that it
			// can be accessed via "field aliases", which occur in the case of
			// record type declarations.
			name = new Identifier("$");
		}
		// FIXME: actually parse modifiers?
		Tuple<Modifier> modifiers = new Tuple<>();
		Decl.Variable decl = new Decl.Variable(modifiers, name, type);
		decl = annotateSourceLocation(decl, start);
		scope.declareVariable(decl);
		return decl;
	}

	/**
	 * Parse a type declaration in a Whiley source file, which has the form:
	 *
	 * <pre>
	 * "type" Identifier "is" TypePattern ("where" Expr)*
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
	 * @see wyil.lang.WyilFile.Type
	 *
	 * @param modifiers
	 *            --- The list of modifiers for this declaration (which were
	 *            already parsed before this method was called).
	 */
	public Decl.Type parseTypeDeclaration(Build.Meter meter, Tuple<Modifier> modifiers) {
		int start = index;
		EnclosingScope scope = new EnclosingScope(meter);
		//
		match(Identifier); // type
		// Parse type name
		Identifier name = parseIdentifier();
		// Parse templare variables
		Tuple<Template.Variable> template = parseOptionalTemplate(scope);
		match(Is);
		// Parse the type pattern
		Decl.Variable var = parseOptionalParameter(scope);
		addFieldAliases(var, scope);
		Tuple<Expr> invariant = parseInvariant(scope, Where);
		int end = index;
		matchEndLine();
		return annotateSourceLocation(new Decl.Type(modifiers, name, template, var, invariant), start);
	}

	/**
	 * In the special case of a record type declaration, those fields contained
	 * in the record are registered as "field aliases". This means they can be
	 * referred to directly from the type invariant, rather than requiring an
	 * additional variable be declared. For example, the following is permitted:
	 *
	 * <pre>
	 * type Point is {int x, int y} where x >= 0 && y >= 0
	 * </pre>
	 *
	 * Here, <code>x</code> and <code>y</code> are "field aliases" within the
	 * scope of the type invariant. In essence, what happens is that the above
	 * is silently transformed into the following:
	 *
	 * <pre>
	 * type Point is ({int x, int y} $) where $.x >= 0 && $.y >= 0
	 * </pre>
	 *
	 * The anonymous variable name <code>$</code> is chosen because it cannot
	 * conflict with a declared variable in the program source (i.e. it is not a
	 * valid variable identifier).
	 *
	 * @param p
	 * @param scope
	 */
	private void addFieldAliases(Decl.Variable p, EnclosingScope scope) {
		Type t = p.getType();
		if (t instanceof Type.Record) {
			// This is currently the only situation in which field aliases can
			// arise.
			Type.Record r = (Type.Record) t;
			for (Type.Field fd : r.getFields()) {
				scope.declareFieldAlias(fd.getName());
			}
		}
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
	 * @see wyc.lang.WhielyFile.StaticVariable
	 *
	 * @param modifiers
	 *            --- The list of modifiers for this declaration (which were
	 *            already parsed before this method was called).
	 */
	private Decl.StaticVariable parseStaticVariableDeclaration(Build.Meter meter, Tuple<Modifier> modifiers) {
		//
		int start = index;
		EnclosingScope scope = new EnclosingScope(meter);
		//
		Type type = parseType(scope);
		//
		Identifier name = parseIdentifier();
		match(Equals);
		Expr e = parseExpression(scope, false);
		int end = index;
		matchEndLine();
		return annotateSourceLocation(new Decl.StaticVariable(modifiers, name, type, e), start);
	}

	private Tuple<Template.Variable> parseOptionalTemplate(EnclosingScope scope) {
		skipWhiteSpace();
		if (index < tokens.size() && tokens.get(index).kind == LeftAngle) {
			return parseTemplate(scope);
		} else {
			return new Tuple<>();
		}
	}

	private Tuple<Template.Variable> parseTemplate(EnclosingScope scope) {
		ArrayList<Template.Variable> vars = new ArrayList<>();
		Token left = match(LeftAngle);
		Token right = null;
		//
		while ((right = eventuallyMatch(RightAngle)) == null) {
			if (!vars.isEmpty()) {
				match(Comma);
			}
			Template.Variable var = parseTemplateVariable();
			if(vars.contains(var)) {
				syntaxError(WyilFile.DUPLICATE_TEMPLATE_VARIABLE, var);
			} else {
				vars.add(var);
				scope.declareTemplateVariable(var);
			}
		}
		if (vars.size() == 0) {
			syntaxError(WyilFile.MISSING_TYPE_VARIABLE, left, right);
		}
		return new Tuple<>(vars);
	}

	private Template.Variable parseTemplateVariable() {
		skipWhiteSpace();
		int start = index;
		// type variable
		Identifier name = parseIdentifier();
		return annotateSourceLocation(new Template.Type(name, Template.Variance.UNKNOWN), start);
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
	private Stmt.Block parseBlock(EnclosingScope scope, boolean isLoop) {
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
			return new Stmt.Block();
		} else {
			// Initial indent is valid, so we proceed parsing statements with
			// the appropriate level of indent.
			ArrayList<Stmt> stmts = new ArrayList<>();
			Indent nextIndent;
			while ((nextIndent = getIndent()) != null && indent.lessThanEq(nextIndent)) {
				// At this point, nextIndent contains the indent of the current
				// statement. However, this still may not be equivalent to this
				// block's indentation level.
				//
				// First, check the indentation matches that for this block.
				if (!indent.equivalent(nextIndent)) {
					// No, it's not equivalent so signal an error.
					syntaxError(WyilFile.UNEXPECTED_BLOCK_END, nextIndent);
				}
				// Second, parse the actual statement at this point!
				stmts.add(parseStatement(blockScope));
			}
			// Finally, construct the block
			return new Stmt.Block(stmts.toArray(new Stmt[stmts.size()]));
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private Stmt parseStatement(EnclosingScope scope) {
		scope.step("statement");
		//
		checkNotEof();
		Token lookahead = tokens.get(index);

		// First, attempt to parse the easy statement forms.

		switch (lookahead.kind) {
		case Assert:
			return parseAssertStatement(scope);
		case Assume:
			return parseAssumeStatement(scope);
		case Break:
			return parseBreakStatement(scope);
		case Continue:
			return parseContinueStatement(scope);
		case Do:
			return parseDoWhileStatement(scope);
		case Debug:
			return parseDebugStatement(scope);
		case Fail:
			return parseFailStatement(scope);
		case For:
			return parseForStatement(scope);
		case If:
			return parseIfStatement(scope);
		case Return:
			return parseReturnStatement(scope);
		case While:
			return parseWhileStatement(scope);
		case Skip:
			return parseSkipStatement(scope);
		case Switch:
			return parseSwitchStatement(scope);
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
		return parseHeadlessStatement(scope);
	}

	/**
	 * A headless statement is one which has no identifying keyword. The set of
	 * headless statements include assignments, invocations, variable
	 * declarations and named blocks.
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private Stmt parseHeadlessStatement(EnclosingScope scope) {
		int start = index;
		// See if it is a named block
		Identifier blockName = parseOptionalIdentifier(scope);
		if (blockName != null) {
			if (tryAndMatch(true, Colon) != null && isAtEOL()) {
				int end = index;
				matchEndLine();
				scope = scope.newEnclosingScope();
				//scope.declareLifetime(blockName);
				Stmt.Block body = parseBlock(scope, false);
				return annotateSourceLocation(new Stmt.NamedBlock(blockName, body), start);
			} else {
				index = start; // backtrack
			}
		}
		// assignment   : Identifier | LeftBrace | Star
		// variable decl: Identifier | LeftBrace | LeftCurly | Ampersand
		// invoke       : Identifier | LeftBrace | Star
		int r = isStatementInitialiser(scope);
		if (r > 0) {
			// Must be a statement initialiser as this is the only situation in which a type
			// can be followed by an identifier.
			return parseInitialiserStatement(scope, r > 1);
		}
		// Must be an assignment or invocation
		Expr e = parseExpression(scope, false);
		//
		if (e instanceof Expr.Invoke || e instanceof Expr.IndirectInvoke) {
			// Must be an invocation since these are neither valid
			// lvals (i.e. they cannot be assigned) nor types.
			matchEndLine();
			return e;
		} else {
			// At this point, the only remaining option is an assignment statement.
			// Therefore, it must be that.
			index = start; // backtrack
			//
			return parseAssignmentStatement(scope);
		}
	}

	private int isStatementInitialiser(EnclosingScope scope) {
		int result;
		int start = index;
		// First, try and match unit initialiser
		if (tryAndMatch(false, Final) != null || (skipType(scope) && tryAndMatch(false, Identifier) != null)) {
			// matched!
			result = 1;
		} else {
			index = start; // backtrack
			// Second, try and match multi-variable initialiser
			if(tryAndMatch(false,LeftBrace) != null) {
				result = isStatementInitialiser(scope);
				index = start; // backtrack
				if(result > 0) {
					result = result + 1;
				}
			} else {
				result = 0;
			}
		}
		index = start; // backtrack
		// Give up
		return result;
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
	 * @param scope The enclosing scope for this statement, which determines the set
	 *              of visible (i.e. declared) variables and also the current
	 *              indentation level.
	 * @param multi Indicates whether or not we have a multi-variable initialiser.
	 *
	 * @return
	 */
	private Stmt.Initialiser parseInitialiserStatement(EnclosingScope scope, boolean multi) {
		int start = index;
		ArrayList<Decl.Variable> variables = new ArrayList<>();
		//
		if(multi) {
			match(LeftBrace);
			do {
				Tuple<Modifier> modifiers = parseModifiers(Final);
				Type type = parseType(scope);
				Identifier name = parseIdentifier();
				// Check that declared variables are not already defined.
				scope.checkNameAvailable(name);
				variables.add(new Decl.Variable(modifiers, name, type));
			} while(tryAndMatch(true, Comma) != null);
			match(RightBrace);
		} else {
			Tuple<Modifier> modifiers = parseModifiers(Final);
			Type type = parseType(scope);
			Identifier name = parseIdentifier();
			scope.checkNameAvailable(name);
			variables.add(new Decl.Variable(modifiers, name, type));
		}
		// A variable declaration may optionally be assigned an initialiser
		// expression.
		Expr initialiser = null;
		Stmt.Initialiser stmt;
		if (tryAndMatch(true, Token.Kind.Equals) != null) {
			initialiser = parseExpression(scope, false);
			stmt = new Stmt.Initialiser(new Tuple<>(variables),initialiser);
		} else {
			stmt = new Stmt.Initialiser(new Tuple<>(variables));
		}
		// Now, a new line indicates the end-of-statement
		int end = index;
		matchEndLine();
		// Done.
		stmt = annotateSourceLocation(stmt, start);
		// Finally, register all new variables in the enclosing scope. This
		// should be done after parsing the initialiser expression to prevent it
		// from referring to this variable.
		for(Decl.Variable decl : stmt.getVariables()) {
			scope.declareVariable(decl);
		}
		return stmt;
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Return
	 * @return
	 */
	private Stmt.Return parseReturnStatement(EnclosingScope scope) {
		int start = index;
		match(Return);
		// A return statement may optionally have one or more return
		// expressions. Therefore, we first skip all whitespace on the given
		// line.
		int next = skipLineSpace(index);
		// Then, we check whether or not we reached the end of the line. If not,
		// then we assume what's remaining is the returned expression. This
		// means expressions must start on the same line as a return. Otherwise,
		// a potentially cryptic error message will be given.
		Stmt.Return stmt;
		if (next < tokens.size() && tokens.get(next).kind != NewLine) {
			Tuple<Expr> returns = parseExpressions(scope, false);
			switch(returns.size()) {
			case 0:
				stmt = new Stmt.Return();
				break;
			case 1:
				stmt = new Stmt.Return(returns.get(0));
				break;
			default:
				stmt = new Stmt.Return(new Expr.TupleInitialiser(Type.Void, returns));
			}
		} else {
			stmt = new Stmt.Return();
		}
		// Finally, at this point we are expecting a new-line to signal the
		// end-of-statement.
		int end = index;
		matchEndLine();
		// Done.
		return annotateSourceLocation(stmt, start, end-1);
	}

	/**
	 * Parse an assert statement, which is of the form:
	 *
	 * <pre>
	 * AssertStmt ::= "assert" Expr
	 * </pre>
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Assert
	 * @return
	 */
	private Stmt.Assert parseAssertStatement(EnclosingScope scope) {
		int start = index;
		// Match the assert keyword
		match(Assert);
		// Parse the expression to be printed
		Expr e = parseLogicalExpression(scope, false);
		// Finally, at this point we are expecting a new-line to signal the
		// end-of-statement.
		int end = index;
		matchEndLine();
		// Done.
		return annotateSourceLocation(new Stmt.Assert(e), start);
	}

	/**
	 * Parse an assume statement, which is of the form:
	 *
	 * <pre>
	 * AssumeStmt ::= "assume" Expr
	 * </pre>
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Assume
	 * @return
	 */
	private Stmt.Assume parseAssumeStatement(EnclosingScope scope) {
		int start = index;
		// Match the assume keyword
		match(Assume);
		// Parse the expression to be printed
		Expr e = parseLogicalExpression(scope, false);
		// Finally, at this point we are expecting a new-line to signal the
		// end-of-statement.
		int end = index;
		matchEndLine();
		// Done.
		return annotateSourceLocation(new Stmt.Assume(e), start);
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
		if (!scope.isInLoop()) {
			syntaxError(WyilFile.BREAK_OUTSIDE_SWITCH_OR_LOOP, t);
		}
		// Done.
		return annotateSourceLocation(new Stmt.Break(),start,end-1);
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
		if (!scope.isInLoop()) {
			syntaxError(WyilFile.CONTINUE_OUTSIDE_LOOP, t);
		}
		// Done.
		return annotateSourceLocation(new Stmt.Continue(),start,end-1);
	}

	/**
	 * Parse a debug statement, which is of the form:
	 *
	 * <pre>
	 * DebugStmt ::= "debug" Expr
	 * </pre>
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @see wyc.lang.Stmt.Debug
	 * @return
	 */
	private Stmt.Debug parseDebugStatement(EnclosingScope scope) {
		int start = index;
		// Match the debug keyword
		match(Debug);
		// Parse the expression to be printed
		Expr e = parseExpression(scope, false);
		// Finally, at this point we are expecting a new-line to signal the
		// end-of-statement.
		int end = index;
		matchEndLine();
		// Done.
		return annotateSourceLocation(new Stmt.Debug(e), start, end-1);
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 * @author David J. Pearce
	 *
	 */
	private Stmt parseDoWhileStatement(EnclosingScope scope) {
		int start = index;
		match(Do);
		match(Colon);
		int end = index;
		matchEndLine();
		// match the block
		Stmt.Block blk = parseBlock(scope, true);
		// match while and condition
		match(While);
		Expr condition = parseLogicalExpression(scope, false);
		// Parse the loop invariants
		Tuple<Expr> invariant = parseInvariant(scope,Where);
		matchEndLine();
		return annotateSourceLocation(new Stmt.DoWhile(condition, invariant, new Tuple<>(), blk), start, end-1);
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
		return annotateSourceLocation(new Stmt.Fail(),start,end-1);
	}

	private Stmt.For parseForStatement(EnclosingScope scope) {
		int start = index;
		match(For);
		Identifier id = parseIdentifier();
		scope.checkNameAvailable(id);
		match(In);
		Expr range = parseRangeExpression(scope, true);
		// NOTE: unclear whether including final modifier here makes sense. However, it
		// is necessary to prevent assignments to the index variable(s).
		Decl.StaticVariable decl = new Decl.StaticVariable(new Tuple<>(new Modifier.Final()), id, new Type.Int(),
				range);
		// Clone scope for index variable
		scope = scope.newEnclosingScope();
		// Must allocate declaration here so as to prevent it being copied.
		decl = annotateSourceLocation(decl, start);
		scope.declareVariable(decl);
		// Parse the loop invariants
		Tuple<Expr> invariants = parseInvariant(scope,Where);
		match(Colon);
		matchEndLine();
		Stmt.Block block = parseBlock(scope, true);
		return new Stmt.For(decl, invariants, new Tuple<>(), block);
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private Stmt.IfElse parseIfStatement(EnclosingScope scope) {
		int start = index;
		// An if statement begins with the keyword "if", followed by an
		// expression representing the condition.
		match(If);
		// NOTE: expression terminated by ':'
		Expr c = parseLogicalExpression(scope, true);
		// The a colon to signal the start of a block.
		match(Colon);
		matchEndLine();

		int end = index;
		// First, parse the true branch, which is required
		Stmt.Block tblk = parseBlock(scope, scope.isInLoop());
		// Second, attempt to parse the false branch, which is optional.
		Stmt.Block fblk = null;
		if (tryAndMatchAtIndent(true, scope.getIndent(), Else) != null) {
			int if_start = index;
			if (tryAndMatch(true, If) != null) {
				// This is an if-chain, so backtrack and parse a complete If
				index = if_start;
				fblk = new Stmt.Block(parseIfStatement(scope));
			} else {
				match(Colon);
				matchEndLine();
				fblk = parseBlock(scope, scope.isInLoop());
			}
		}
		Stmt.IfElse stmt;
		if(fblk == null) {
			stmt = new Stmt.IfElse(c, tblk);
		} else {
			stmt = new Stmt.IfElse(c, tblk, fblk);
		}
		// Done!
		return annotateSourceLocation(stmt, start, end-1);
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 * @author David J. Pearce
	 *
	 */
	private Stmt parseWhileStatement(EnclosingScope scope) {
		int start = index;
		match(While);
		// NOTE: expression terminated by ':'
		Expr condition = parseLogicalExpression(scope, true);
		// Parse the loop invariants
		Tuple<Expr> invariants = parseInvariant(scope,Where);
		match(Colon);
		int end = index;
		matchEndLine();
		Stmt.Block blk = parseBlock(scope, true);
		return annotateSourceLocation(new Stmt.While(condition, invariants, new Tuple<>(), blk), start, end-1);
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
		return annotateSourceLocation(new Stmt.Skip(),start,end-1);
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 * @author David J. Pearce
	 *
	 */
	private Stmt parseSwitchStatement(EnclosingScope scope) {
		int start = index;
		match(Switch);
		// NOTE: expression terminated by ':'
		Expr condition = parseExpression(scope, true);
		match(Colon);
		int end = index;
		matchEndLine();
		// Match case block
		Tuple<Stmt.Case> cases = parseCaseBlock(scope);
		// Done
		return annotateSourceLocation(new Stmt.Switch(condition, cases), start,end-1);
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private Tuple<Stmt.Case> parseCaseBlock(EnclosingScope scope) {

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
			return new Tuple<>();
		} else {
			// Initial indent is valid, so we proceed parsing case statements
			// with the appropriate level of indent.
			//
			ArrayList<Stmt.Case> cases = new ArrayList<>();

			Indent nextIndent;
			while ((nextIndent = getIndent()) != null && indent.lessThanEq(nextIndent)) {
				// At this point, nextIndent contains the indent of the current
				// statement. However, this still may not be equivalent to this
				// block's indentation level.

				// First, check the indentation matches that for this block.
				if (!indent.equivalent(nextIndent)) {
					// No, it's not equivalent so signal an error.
					syntaxError(WyilFile.UNEXPECTED_BLOCK_END, indent);
				}

				// Second, parse the actual case statement at this point!
				cases.add(parseCaseStatement(caseScope));
			}
			checkForDuplicateDefault(cases);
			checkForDuplicateConditions(cases);
			return new Tuple<>(cases);
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
		for(int i=0;i!=cases.size();++i) {
			Stmt.Case c = cases.get(i);
			if (c.getConditions().size() > 0 && hasDefault) {
				syntaxError(WyilFile.UNREACHABLE_CODE, c);
			} else if (c.getConditions().size() == 0 && hasDefault) {
				syntaxError(WyilFile.DUPLICATE_DEFAULT_LABEL, c);
			} else {
				hasDefault = c.getConditions().size() == 0;
			}
		}
	}

	/**
	 * Check whether we have duplicate case conditions or not. Observe that this is
	 * relatively simplistic and does not perform any complex simplifications.
	 * Therefore, some duplicates are missed because they require simplification.
	 * For example, the condition <code>1+1</code> and <code>2</code> are not
	 * considered duplicates here.  See #648 for more.
	 */
	private void checkForDuplicateConditions(List<Stmt.Case> cases) {
		HashSet<Expr> seen = new HashSet<>();
		for(int i=0;i!=cases.size();++i) {
			Stmt.Case c = cases.get(i);
			Tuple<Expr> conditions = c.getConditions();
			// Check whether any of these conditions already seen.
			for(int j=0;j!=conditions.size();++j) {
				Expr condition = conditions.get(j);
				if(seen.contains(condition)) {
					syntaxError(WyilFile.DUPLICATE_CASE_LABEL, condition);
				} else {
					seen.add(condition);
				}
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 * @return
	 */
	private Stmt.Case parseCaseStatement(EnclosingScope scope) {
		int start = index;
		List<Expr> values;
		if (tryAndMatch(true, Default) != null) {
			values = Collections.EMPTY_LIST;
		} else {
			match(Case);
			// Now, parse one or more constant expressions
			values = new ArrayList<>();
			do {
				// NOTE: expression terminated by ':'
				values.add(parseExpression(scope, true));
			} while (tryAndMatch(true, Comma) != null);
		}
		match(Colon);
		int end = index;
		matchEndLine();
		Stmt.Block stmts = parseBlock(scope, scope.isInLoop());
		return annotateSourceLocation(new Stmt.Case(new Tuple<>(values), stmts), start,end-1);
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @return
	 */
	private Stmt parseAssignmentStatement(EnclosingScope scope) {
		int start = index;
		Tuple<LVal> lvals = parseLVals(scope);
		match(Equals);
		Tuple<Expr> rvals = parseExpressions(scope, false);
		int end = index;
		matchEndLine();
		return annotateSourceLocation(new Stmt.Assign(lvals, rvals), start,end-1);
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @return
	 */
	private Tuple<LVal> parseLVals(EnclosingScope scope) {
		int start = index;
		ArrayList<LVal> elements = new ArrayList<>();
		elements.add(parseLVal(index, scope));

		// Check whether we have a multiple lvals or not
		while (tryAndMatch(true, Comma) != null) {
			// Add all expressions separated by a comma
			elements.add(parseLVal(index, scope));
			// Done
		}

		return new Tuple<>(elements);
	}

	private LVal parseLVal(int start, EnclosingScope scope) {
		return parseAccessLVal(start, scope);
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @return
	 */
	private LVal parseAccessLVal(int start, EnclosingScope scope) {
		LVal lhs = parseLValTerm(start, scope);
		Token token;
		while ((token = tryAndMatchOnLine(LeftSquare)) != null
				|| (token = tryAndMatch(true, Dot, MinusGreater)) != null) {
			switch (token.kind) {
			case LeftSquare: {
				// NOTE: expression is terminated by ']'
				Expr rhs = parseAdditiveExpression(scope, true);
				match(RightSquare);
				lhs = new Expr.ArrayAccess(Type.Void, lhs, rhs);
				break;
			}
			case MinusGreater: {
				Identifier name = parseIdentifier();
				lhs = new Expr.FieldDereference(Type.Void, lhs, name);
				break;
			}
			case Dot: {
				Identifier name = parseIdentifier();
				lhs = new Expr.RecordAccess(Type.Void, lhs, name);
				break;
			}
			}
			lhs = annotateSourceLocation(lhs,start);
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
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @return
	 */
	private LVal parseLValTerm(int start, EnclosingScope scope) {
		checkNotEof();
		start = index;
		// First, attempt to disambiguate the easy forms:
		Token lookahead = tokens.get(index);
		switch (lookahead.kind) {
		case Identifier:
			Identifier name = parseIdentifier();
			LVal var;
			if(scope.isVariable(name)) {
				var = new Expr.VariableAccess(Type.Void, scope.getVariableDeclaration(name));
			} else {
				var = new Expr.StaticVariableAccess(Type.Void, new Decl.Link<>(new Name(name)));
			}
			return annotateSourceLocation(var, start);
		case LeftBrace: {
			match(LeftBrace);
			ArrayList<Expr> lvals = new ArrayList<>();
			lvals.add(parseLVal(start, scope));
			while (tryAndMatchOnLine(RightBrace) == null) {
				match(Comma);
				lvals.add(parseLVal(start, scope));
			}
			if(lvals.size() > 1) {
				return annotateSourceLocation(new Expr.TupleInitialiser(Type.Void, new Tuple<>(lvals)), start);
			} else {
				return annotateSourceLocation((LVal) lvals.get(0), start);
			}
		}
		case Star: {
			match(Star);
			LVal lval = parseLVal(start, scope);
			return annotateSourceLocation(new Expr.Dereference(Type.Void, lval), start);
		}
		default:
			syntaxError(WyilFile.UNKNOWN_LVAL, lookahead);
			return null; // dead-code
		}
	}

	/**
	 * Parse a "multi-expression"; that is, a sequence of one or more
	 * expressions separated by comma's
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
	 * @return
	 */
	public Tuple<Expr> parseExpressions(EnclosingScope scope, boolean terminated) {
		ArrayList<Expr> returns = new ArrayList<>();
		// A return statement may optionally have a return expression.
		// Therefore, we first skip all whitespace on the given line.
		int next = skipLineSpace(index);
		// Then, we check whether or not we reached the end of the line. If not,
		// then we assume what's remaining is the returned expression. This
		// means expressions must start on the same line as a return. Otherwise,
		// a potentially cryptic error message will be given.
		returns.add(parseExpression(scope, terminated));
		while (tryAndMatch(false, Comma) != null) {
			returns.add(parseExpression(scope, terminated));
		}
		return new Tuple<>(returns);
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
	private Expr parseExpression(EnclosingScope scope, boolean terminated) {
		scope.step("expression");
		return parseLogicalExpression(scope, terminated);
	}

	/**
	 * Parse a logical expression of the form:
	 *
	 * <pre>
	 * Expr ::= AndOrExpr [ "==>" UnitExpr]
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
	private Expr parseLogicalExpression(EnclosingScope scope, boolean terminated) {
		checkNotEof();
		int start = index;
		Expr lhs = parseAndOrExpression(scope, terminated);
		Token lookahead = tryAndMatch(terminated, LogicalImplication, LogicalIff);
		//System.out.println("PARSED: " + lhs + " : " + lookahead);
		if (lookahead != null) {
			switch (lookahead.kind) {
			case LogicalImplication: {
				Expr rhs = parseAndOrExpression(scope, terminated);
				lhs = new Expr.LogicalImplication(lhs, rhs);
				break;
			}
			case LogicalIff: {
				Expr rhs = parseAndOrExpression(scope, terminated);
				lhs = new Expr.LogicalIff(lhs, rhs);
				break;
			}
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}
			lhs = annotateSourceLocation(lhs,start);
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
	private Expr parseAndOrExpression(EnclosingScope scope, boolean terminated) {
		checkNotEof();
		int start = index;
		Expr lhs = parseBitwiseOrExpression(scope, terminated);
		Token lookahead = tryAndMatch(terminated, LogicalAnd, LogicalOr);
		if (lookahead != null) {
			switch (lookahead.kind) {
			case LogicalAnd: {
				Expr rhs = parseAndOrExpression(scope, terminated);
				lhs = annotateSourceLocation(new Expr.LogicalAnd(new Tuple<>(lhs, rhs)),start);
				break;
			}
			case LogicalOr: {
				Expr rhs = parseAndOrExpression(scope, terminated);
				lhs = annotateSourceLocation(new Expr.LogicalOr(new Tuple<>(lhs, rhs)), start);
				break;
			}
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}
		}
		return lhs;
	}

	/**
	 * Parse an bitwise "inclusive or" expression
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
	private Expr parseBitwiseOrExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseBitwiseXorExpression(scope, terminated);

		if (tryAndMatch(terminated, VerticalBar) != null) {
			Expr rhs = parseBitwiseOrExpression(scope, terminated);
			return annotateSourceLocation(new Expr.BitwiseOr(Type.Byte, new Tuple<>(lhs, rhs)), start);
		}

		return lhs;
	}

	/**
	 * Parse an bitwise "exclusive or" expression
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
	private Expr parseBitwiseXorExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseBitwiseAndExpression(scope, terminated);

		if (tryAndMatch(terminated, Caret) != null) {
			Expr rhs = parseBitwiseXorExpression(scope, terminated);
			return annotateSourceLocation(new Expr.BitwiseXor(Type.Byte, new Tuple<>(lhs, rhs)), start);
		}

		return lhs;
	}

	/**
	 * Parse an bitwise "and" expression
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
	private Expr parseBitwiseAndExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseConditionExpression(scope, terminated);

		if (tryAndMatch(terminated, Ampersand) != null) {
			Expr rhs = parseBitwiseAndExpression(scope, terminated);
			return annotateSourceLocation(new Expr.BitwiseAnd(Type.Byte, new Tuple<>(lhs, rhs)), start);
		}

		return lhs;
	}

	/**
	 * Parse a condition expression.
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
	private Expr parseConditionExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		Token lookahead;

		// First, attempt to parse quantifiers (e.g. some, all, no, etc)
		if ((lookahead = tryAndMatch(terminated, Some, All)) != null) {
			return parseQuantifierExpression(lookahead, scope, terminated);
		}

		Expr lhs = parseShiftExpression(scope, terminated);

		lookahead = tryAndMatch(terminated, LessEquals, LeftAngle, GreaterEquals, RightAngle, EqualsEquals, NotEquals,
				Is, Subset, SubsetEquals, Superset, SupersetEquals);

		if (lookahead != null && lookahead.kind == Is) {
			Type type = parseType(scope);
			lhs = annotateSourceLocation(new Expr.Is(lhs, type), start);
		} else if (lookahead != null) {
			Expr rhs = parseShiftExpression(scope, terminated);
			//
			switch (lookahead.kind) {
			case LessEquals:
				lhs = new Expr.IntegerLessThanOrEqual(lhs, rhs);
				break;
			case LeftAngle:
				lhs = new Expr.IntegerLessThan(lhs, rhs);
				break;
			case GreaterEquals:
				lhs = new Expr.IntegerGreaterThanOrEqual(lhs, rhs);
				break;
			case RightAngle:
				lhs = new Expr.IntegerGreaterThan(lhs, rhs);
				break;
			case EqualsEquals:
				lhs = new Expr.Equal(lhs, rhs);
				break;
			case NotEquals:
				lhs = new Expr.NotEqual(lhs, rhs);
				break;
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}
			lhs = annotateSourceLocation(lhs,start);
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
	private Expr parseQuantifierExpression(Token lookahead, EnclosingScope scope, boolean terminated) {
		int start = index - 1;
		scope = scope.newEnclosingScope();
		match(LeftCurly);
		// Parse one or more source variables / expressions
		Tuple<Decl.StaticVariable> parameters = parseQuantifierParameters(scope);
		// Parse condition over source variables
		Expr condition = parseLogicalExpression(scope, true);
		//
		match(RightCurly);
		//
		Expr.Quantifier qf;
		if (lookahead.kind == All) {
			qf = new Expr.UniversalQuantifier(parameters, condition);
		} else {
			qf = new Expr.ExistentialQuantifier(parameters, condition);
		}
		return annotateSourceLocation(qf, start);
	}

	private Tuple<Decl.StaticVariable> parseQuantifierParameters(EnclosingScope scope) {
		boolean firstTime = true;
		ArrayList<Decl.StaticVariable> parameters = new ArrayList<>();
		do {
			if (!firstTime) {
				match(Comma);
			}
			int start = index;
			firstTime = false;
			Identifier id = parseIdentifier();
			scope.checkNameAvailable(id);
			match(In);
			Expr range = parseRangeExpression(scope, true);
			// FIXME: need to add initialiser here
			Decl.StaticVariable decl = new Decl.StaticVariable(new Tuple<>(), id, new Type.Int(), range);
			decl = annotateSourceLocation(decl, start);
			parameters.add(decl);
			scope.declareVariable(decl);
		} while (eventuallyMatch(VerticalBar) == null);

		return new Tuple<>(parameters);
	}

	/**
	 * Parse a range expression, which has the form:
	 *
	 * <pre>
	 * RangeExpr ::= Expr ".." Expr
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
	private Expr parseRangeExpression(EnclosingScope scope, boolean terminated) {
		skipWhiteSpace();
		int start = index;
		Expr lhs = parseAdditiveExpression(scope, true);
		match(DotDot);
		Expr rhs = parseAdditiveExpression(scope, true);
		Expr range = new Expr.ArrayRange(Type.Void, lhs, rhs);
		return annotateSourceLocation(range, start);
	}

	/**
	 * Parse a shift expression, which has the form:
	 *
	 * <pre>
	 * ShiftExpr ::= AdditiveExpr [ ( "<<" | ">>" ) AdditiveExpr ]
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
	private Expr parseShiftExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseAdditiveExpression(scope, terminated);

		Token lookahead;
		while ((lookahead = tryAndMatch(terminated, LeftAngleLeftAngle, RightAngleRightAngle)) != null) {
			Expr rhs = parseAdditiveExpression(scope, terminated);
			switch (lookahead.kind) {
			case LeftAngleLeftAngle:
				lhs = new Expr.BitwiseShiftLeft(Type.Byte, lhs, rhs);
				break;
			case RightAngleRightAngle:
				lhs = new Expr.BitwiseShiftRight(Type.Byte, lhs, rhs);
				break;
			}
			annotateSourceLocation(lhs, start);
		}

		return lhs;
	}

	/**
	 * Parse an additive expression.
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
	private Expr parseAdditiveExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseMultiplicativeExpression(scope, terminated);

		Token lookahead;
		while ((lookahead = tryAndMatch(terminated, Plus, Minus)) != null) {
			Expr rhs = parseMultiplicativeExpression(scope, terminated);
			switch (lookahead.kind) {
			case Plus:
				lhs = new Expr.IntegerAddition(Type.Void, lhs, rhs);
				break;
			case Minus:
				lhs = new Expr.IntegerSubtraction(Type.Void, lhs, rhs);
				break;
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}
			lhs = annotateSourceLocation(lhs, start);
		}
		return lhs;
	}

	/**
	 * Parse a multiplicative expression.
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
	private Expr parseMultiplicativeExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseAccessExpression(scope, terminated);

		Token lookahead = tryAndMatch(terminated, Star, RightSlash, Percent);
		if (lookahead != null) {
			Expr rhs = parseAccessExpression(scope, terminated);
			switch (lookahead.kind) {
			case Star:
				lhs = new Expr.IntegerMultiplication(Type.Void, lhs, rhs);
				break;
			case RightSlash:
				lhs = new Expr.IntegerDivision(Type.Void, lhs, rhs);
				break;
			case Percent:
				lhs = new Expr.IntegerRemainder(Type.Void, lhs, rhs);
				break;
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}
			lhs = annotateSourceLocation(lhs, start);
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
	private Expr parseAccessExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		Expr lhs = parseTermExpression(scope, terminated);
		Token token;

		while ((token = tryAndMatchOnLine(LeftSquare)) != null
				|| (token = tryAndMatch(terminated, Dot, MinusGreater, ColonColon)) != null) {
			switch (token.kind) {
			case LeftSquare:
				// NOTE: expression guaranteed to be terminated by ']'.
				Expr rhs = parseAdditiveExpression(scope, true);
				// This is a plain old array access expression
				match(RightSquare);
				lhs = new Expr.ArrayAccess(Type.Void, lhs, rhs);
				break;
			case MinusGreater: {
				// At this point, we could have a field access, or a
				// method/function invocation. Therefore, we start by
				// parsing the field access and then check whether or not its an
				// invocation.
				int slice = index;
				Identifier name = parseIdentifier();
				lhs = new Expr.FieldDereference(Type.Void, lhs, name);
				lhs = parseDotAccess(lhs, scope, slice, terminated);
				break;
			}
			case Dot: {
				// At this point, we could have a field access, or a
				// method/function invocation. Therefore, we start by
				// parsing the field access and then check whether or not its an
				// invocation.
				int slice = index;
				Identifier name = parseIdentifier();
				lhs = new Expr.RecordAccess(Type.Void, lhs, name);
				lhs = parseDotAccess(lhs, scope, slice, terminated);
				break;
			}
			case ColonColon:
				// At this point, we have a qualified access.
				index = start;
				lhs = parseQualifiedAccess(scope, terminated);
				break;
			}
			// Attached source information
			lhs = annotateSourceLocation(lhs,start);
		}

		return lhs;
	}

	private Expr parseDotAccess(Expr lhs, EnclosingScope scope, int start, boolean terminated) {
		// First we have to see if it is a method invocation. We can
		// have optional lifetime arguments in angle brackets.
		boolean isInvocation = false;
		if (tryAndMatch(terminated, LeftBrace) != null) {
			isInvocation = true;
		}
		if (isInvocation) {
			// This indicates an indirect invocation. First,
			// parse arguments to invocation
			Tuple<Expr> arguments = parseInvocationArguments(scope);
			// Now construct indirect expression
			lhs = annotateSourceLocation(lhs, start, start);
			lhs = new Expr.IndirectInvoke(Type.Void, lhs, arguments);
		}
		return annotateSourceLocation(lhs,start);
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
	private Expr parseQualifiedAccess(EnclosingScope scope, boolean terminated) {
		int start = index;
		// Parse qualified name
		Name name = parseName(scope);
		// Construct link to be resolved
		Decl.Link link = new Decl.Link<>(name);
		// Decide what we've got
		int mid = index;
		Expr expr;
		if (skipTemplate(scope) && tryAndMatch(terminated, LeftBrace) != null) {
			// backtrack
			index = mid;
			// parse any optional template arguments
			Tuple<Type> templateArguments = parseOptionalTemplateArguments(scope, terminated);
			// Construct binding to be resolved
			Decl.Binding<Type.Callable, Decl.Callable> binding = new Decl.Binding<>(link, templateArguments);
			// Repeat this
			match(LeftBrace);
			// Parse arguments to invocation
			Tuple<Expr> arguments = parseInvocationArguments(scope);
			// This indicates we have an direct invocation
			expr = new Expr.Invoke(binding, arguments);
		} else {
			// Must be a qualified static variable access
			expr = new Expr.StaticVariableAccess(Type.Void, link);
		}
		return annotateSourceLocation(expr, start);
	}

	/**
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
	private Expr parseTermExpression(EnclosingScope scope, boolean terminated) {
		checkNotEof();

		int start = index;
		Token token = tokens.get(index);

		switch (token.kind) {
		case LeftBrace:
			return parseBracketedOrTupleOrCastExpression(scope, terminated);
		case New:
		case This:
			return parseNewExpression(scope, terminated);
		case Identifier: {
			Identifier name = parseIdentifier();
			Expr term;
			if (tryAndMatch(terminated, LeftBrace) != null) {
				return parseInvokeExpression(scope, start, name, terminated, new Tuple<>());
			} else if (lookaheadSequence(terminated, Colon, New)) {
				// Identifier is lifetime name in "new" expression
				index = start;
				return parseNewExpression(scope, terminated);
			} else if (lookaheadSequence(terminated, LeftAngle)) {
				int tStart = index;
				// NOTE: need to check right brance after right angle to avoid strange
				// expressions (e.g. [ x < T,S > 1 ])
				if(skipTemplate(scope) && lookaheadSequence(terminated, LeftBrace)) {
					index = tStart;
					// This one is a little tricky, as we need some lookahead
					// effort. We want to see whether it is a method invocation with
					// lifetime arguments. But "Identifier < ..." can also be a
					// boolean expression!
					Tuple<Type> template = parseTemplateArguments(scope,terminated);
					match(LeftBrace);
					return parseInvokeExpression(scope, start, name, terminated, template);
				} else {
					// backtrack
					index = tStart;
				}
			} else if (lookaheadSequence(terminated, LeftCurly)) {
				// This indicates a named record initialiser which consists of a
				// name followed by a record initialiser.
				return parseRecordInitialiser(name, scope, terminated);
			} // no else if, in case the former one didn't return
			if (scope.isVariable(name)) {
				// Signals a local variable access
				Decl.Variable decl = scope.getVariableDeclaration(name);
				Expr var = new Expr.VariableAccess(Type.Void, scope.getVariableDeclaration(name));
				return annotateSourceLocation(var, start);
			} else if (scope.isFieldAlias(name)) {
				// Signals a field alias
				Decl.Variable var = scope.getVariableDeclaration(new Identifier("$"));
				Expr access = new Expr.RecordAccess(Type.Void, new Expr.VariableAccess(Type.Void, var), name);
				return annotateSourceLocation(access,start);
			} else {
				// Otherwise, this must be a static access of some kind.
				// Observe that, at this point, we cannot determine whether or
				// not this is a constant-access or a package-access which marks
				// the beginning of a constant-access.
				Decl.Link<Decl.StaticVariable> link = new Decl.Link(new Name(name));
				Expr var = new Expr.StaticVariableAccess(Type.Void, link);
				return annotateSourceLocation(var, start);
			}
		}
		case Null:
			return annotateSourceLocation(new Expr.Constant(Type.Void, new Value.Null()), index++);
		case True:
			return annotateSourceLocation(new Expr.Constant(Type.Void, new Value.Bool(true)), index++);
		case False:
			return annotateSourceLocation(new Expr.Constant(Type.Void, new Value.Bool(false)), index++);
		case BinaryLiteral: {
			byte val = parseBinaryLiteral(token);
			return annotateSourceLocation(new Expr.Constant(Type.Void, new Value.Byte(val)), index++);
		}
		case CharLiteral: {
			BigInteger val = parseCharacter(token.text);
			return annotateSourceLocation(new Expr.Constant(Type.Void, new Value.Int(val)), index++);
		}
		case IntegerLiteral: {
			BigInteger val = parseIntegerLiteral(token);
			return annotateSourceLocation(new Expr.Constant(Type.Void, new Value.Int(val)), index++);
		}
		case HexLiteral: {
			BigInteger val = parseHexLiteral(token);
			return annotateSourceLocation(new Expr.Constant(Type.Void, new Value.Int(val)), index++);
		}
		case StringLiteral: {
			byte[] val = parseUnicodeString(token);
			return annotateSourceLocation(new Expr.Constant(Type.Void, new Value.UTF8(val)), index++);
		}
		case Minus:
			return parseNegationExpression(scope, terminated);
		case VerticalBar:
			return parseLengthOfExpression(scope, terminated);
		case LeftSquare:
			return parseArrayInitialiserOrGeneratorExpression(scope, terminated);
		case LeftCurly:
			return parseRecordInitialiser(null, scope, terminated);
		case Shreak:
			return parseLogicalNotExpression(scope, terminated);
		case Star:
			if (lookaheadSequence(terminated, Star, Colon, New)) {
				// Star is default lifetime
				return parseNewExpression(scope, terminated);
			}
			return parseDereferenceExpression(scope, terminated);
		case Tilde:
			return parseBitwiseComplementExpression(scope, terminated);
		case Ampersand:
			return parseLambdaExpression(scope, terminated);
		}

		syntaxError(WyilFile.UNKNOWN_TERM, token);
		return null;
	}

	/**
	 * Decide whether we have a template argument list ahead. This actually requires
	 * infinite lookahead but, in practice, is pretty minimal work.
	 *
	 * @param scope
	 * @return
	 */
	public boolean skipTemplate(EnclosingScope scope) {
		int start = index;
		if (tryAndMatch(false, LeftAngle) == null) {
			return true;
		} else {
			boolean firstTime = true;
			while (tryAndMatch(false, RightAngle) == null) {
				if (!firstTime && tryAndMatch(false,Comma) == null) {
					// Failed to match a comma.  Something is wrong
					index = start;
					return false;
				} else if (!skipLifetimeIdentifier(scope) && !skipType(scope)) {
					index = start;
					return false;
				}
				firstTime = false;
			}
		}
		//
		return true;
	}

	public Tuple<Type> parseOptionalTemplateArguments(EnclosingScope scope, boolean terminated) {
		skipWhiteSpace();
		if(index < tokens.size() && tokens.get(index).kind == LeftAngle) {
			return parseTemplateArguments(scope,terminated);
		} else {
			return new Tuple<>();
		}
	}

	public Tuple<Type> parseTemplateArguments(EnclosingScope scope, boolean terminated) {
		Token left = match(LeftAngle);
		boolean firstTime = true;
		ArrayList<Type> template = new ArrayList<>();
		// Attempt to parse a template argument
		while (eventuallyMatch(RightAngle) == null) {
			if (!firstTime) {
				match(Comma);
			} else {
				firstTime = false;
			}
			// terminated by '>'
			template.add(parseType(scope));
		}
		return new Tuple<>(template);
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
	 * includes: identifiers (e.g. "(T) x"), braces of various kinds (e.g. "(T)
	 * [1,2]" or "(T) (1,2)"), unary operators (e.g. "(T) !x", "(T) |xs|", etc).
	 * A bracketed expression, on the other hand, can be followed by a binary
	 * operator (e.g. "(e) + 1"), a left- or right-brace (e.g. "(1 + (x+1))" or
	 * "(*f)(1)") or a newline.
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
	private Expr parseBracketedOrTupleOrCastExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(LeftBrace);
		// At this point, we must begin to disambiguate casts from general
		// bracketed expressions. In the case that what follows the left brace
		// is something which can only be a type, then clearly we have a cast.
		// However, in the other case, we may still have a cast since many types
		// cannot be clearly distinguished from expressions at this stage (e.g.
		// "(nat,nat)" could either be a tuple type (if "nat" is a type) or a
		// tuple expression (if "nat" is a variable or constant).
		if (skipType(scope) && tryAndMatch(true, RightBrace) != null) {
			// must be a cast expression
			index = start; // backtrack
			return parseCastExpression(scope, terminated);
		} else {
			index = start; // backtrack
			return parseBracketedOrTupleExpression(scope, terminated);
		}
	}

	public Expr parseBracketedOrTupleExpression(EnclosingScope scope, boolean terminated) {
		// We still may have either a cast or a bracketed expression, and we
		// cannot tell which yet.
		match(LeftBrace);
		ArrayList<Expr> es = new ArrayList<>();
		es.add(parseExpression(scope, true));
		while(tryAndMatch(true,RightBrace) == null) {
			match(Comma);
			es.add(parseExpression(scope, true));
		}
		// What have we got?
		if(es.size() == 1) {
			// Assume bracketed
			return es.get(0);
		} else {
			return new Expr.TupleInitialiser(Type.Void, new Tuple<>(es));
		}
	}

	public Expr parseCastExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(LeftBrace);
		Type t = parseType(scope);
		match(RightBrace);
		Expr e = parseExpression(scope, terminated);
		return annotateSourceLocation(new Expr.Cast(t, e), start);
	}

	/**
	 * Parse an array initialiser or generator expression, which is of the form:
	 *
	 * <pre>
	 * ArrayExpr ::= '[' [ Expr (',' Expr)+ ] ']'
	 *             | '[' Expr ';' Expr ']'
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
	private Expr parseArrayInitialiserOrGeneratorExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(LeftSquare);
		if (tryAndMatch(true, RightSquare) != null) {
			// this is an empty array initialiser
			index = start;
			return parseArrayInitialiserExpression(scope, terminated);
		} else {
			Expr expr = parseExpression(scope, true);
			// Finally, disambiguate
			if (tryAndMatch(true, SemiColon) != null) {
				// this is an array generator
				index = start;
				return parseArrayGeneratorExpression(scope, terminated);
			} else {
				// this is an array initialiser
				index = start;
				return parseArrayInitialiserExpression(scope, terminated);
			}
		}
	}

	/**
	 * Parse an array initialiser expression, which is of the form:
	 *
	 * <pre>
	 * ArrayInitialiserExpr ::= '[' [ Expr (',' Expr)+ ] ']'
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
	private Expr parseArrayInitialiserExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(LeftSquare);
		ArrayList<Expr> exprs = new ArrayList<>();
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
			exprs.add(parseExpression(scope, true));
		}
		// Convert to array
		return annotateSourceLocation(new Expr.ArrayInitialiser(Type.Void, new Tuple<>(exprs)), start);
	}

	/**
	 * Parse an array generator expression, which is of the form:
	 *
	 * <pre>
	 * ArrayGeneratorExpr ::= '[' Expr ';' Expr ']'
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
	private Expr parseArrayGeneratorExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(LeftSquare);
		Expr element = parseExpression(scope, true);
		match(SemiColon);
		Expr count = parseExpression(scope, true);
		match(RightSquare);
		return annotateSourceLocation(new Expr.ArrayGenerator(Type.Void, element, count), start);
	}

	/**
	 * Parse a record initialiser, which is of the form:
	 *
	 * <pre>
	 * RecordExpr ::= '{' Identifier ':' Expr (',' Identifier ':' Expr)* '}'
	 * </pre>
	 *
	 * During parsing, we additionally check that each identifier is unique;
	 * otherwise, an error is reported.
	 *
	 * @param name
	 *            An optional name component for the record initialiser. If
	 *            null, then this is an anonymous record initialiser. Otherwise,
	 *            it is a named record initialiser.
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
	private Expr parseRecordInitialiser(Identifier name, EnclosingScope scope, boolean terminated) {
		int start = index;
		match(LeftCurly);
		HashSet<String> keys = new HashSet<>();
		ArrayList<Identifier> fields = new ArrayList<>();
		ArrayList<Expr> operands = new ArrayList<>();

		boolean firstTime = true;
		while (eventuallyMatch(RightCurly) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			// Parse field name being constructed
			Identifier field = parseIdentifier();
			// Check field name is unique
			if (keys.contains(field.get())) {
				syntaxError(WyilFile.DUPLICATE_FIELD, field);
			}
			match(Colon);
			// Parse expression being assigned to field
			// NOTE: we require the following expression be a "non-tuple"
			// expression. That is, it cannot be composed using ',' unless
			// braces enclose the entire expression. This is because the outer
			// record constructor expression is used ',' to distinguish fields.
			// Also, expression is guaranteed to be terminated, either by '}' or
			// ','.
			Expr initialiser = parseExpression(scope, true);
			fields.add(field);
			operands.add(initialiser);
			keys.add(field.get());
		}
		// handle naming

		// FIXME: Need to support named record initialisers. The suggestion here
		// is to support arbitrary named initialisers. The reason for this being
		// we could then support named arrays and other types as well? Not sure
		// what the real difference from a cast is though.
		return annotateSourceLocation(new Expr.RecordInitialiser(Type.Void, new Tuple<>(fields), new Tuple<>(operands)),
				start);
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
	private Expr parseNewExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(New);
		Expr e = parseExpression(scope, terminated);
		// Construct correct expression form
		e = new Expr.New(Type.Void, e);
		// Done
		return annotateSourceLocation(e, start);
	}

	/**
	 * Parse a length of expression, which is of the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *                 |  '|' Expr '|'
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
	private Expr parseLengthOfExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(VerticalBar);
		// We have to parse an Append Expression here, which is the most general
		// form of expression that can generate a collection of some kind. All
		// expressions higher up (e.g. logical expressions) cannot generate
		// collections. Furthermore, the bitwise or expression could lead to
		// ambiguity and, hence, we bypass that an consider append expressions
		// only. However, the expression is guaranteed to be terminated by '|'.
		Expr e = parseShiftExpression(scope, true);
		match(VerticalBar);
		return annotateSourceLocation(new Expr.ArrayLength(Type.Void, e), start);
	}

	/**
	 * Parse a negation expression, which is of the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *                 |  '-' Expr
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
	private Expr parseNegationExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Minus);
		Expr e = parseAccessExpression(scope, terminated);
		return annotateSourceLocation(new Expr.IntegerNegation(Type.Void, e), start);
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
	private Expr parseInvokeExpression(EnclosingScope scope, int start, Identifier name, boolean terminated,
			Tuple<Type> templateArguments) {
		// First, parse the arguments to this invocation.
		Tuple<Expr> args = parseInvocationArguments(scope);
		// Second, determine what kind of invocation we have. If the name of the
		// method is a local variable, then it must be an indirect invocation on
		// this variable.
		if (scope.isVariable(name) && templateArguments.size() == 0) {
			// Indirect invocation on local variable. In this case, template arguments not permitted
			Decl.Variable decl = scope.getVariableDeclaration(name);
			Expr.VariableAccess var = annotateSourceLocation(new Expr.VariableAccess(Type.Void, decl), start,
					start);
			return annotateSourceLocation(new Expr.IndirectInvoke(Type.Void, var, args), start);
		} else {
			// unqualified direct invocation
			Name nm = annotateSourceLocation(new Name(name), start, start);
			Decl.Link<Decl.Callable> link = new Decl.Link(nm);
			return annotateSourceLocation(new Expr.Invoke(new Decl.Binding<>(link, templateArguments), args), start);
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
	private Tuple<Expr> parseInvocationArguments(EnclosingScope scope) {
		boolean firstTime = true;
		ArrayList<Expr> args = new ArrayList<>();
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
			Expr e = parseExpression(scope, true);

			args.add(e);
		}
		return new Tuple<>(args);
	}

	/**
	 * Parse a logical not expression, which has the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *       | '!' Expr
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
	private Expr parseLogicalNotExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Shreak);
		// Note: cannot parse unit expression here, because that messes up the
		// precedence. For example, !result ==> other should be parsed as
		// (!result) ==> other, not !(result ==> other).
		Expr expression = parseConditionExpression(scope, terminated);
		return annotateSourceLocation(new Expr.LogicalNot(expression), start);
	}

	/**
	 * Parse a dereference expression, which has the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *                 | '*' Expr
	 * </pre>
	 *
	 * @param scope
	 *            The enclosing scope for this statement, which determines the
	 *            set of visible (i.e. declared) variables and also the current
	 *            indentation level.
	 *
	 * @return
	 */
	private Expr parseDereferenceExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Star);
		Expr expression = parseTermExpression(scope, terminated);
		return annotateSourceLocation(new Expr.Dereference(Type.Void, expression), start);
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
	private Expr parseLambdaExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Ampersand);
		if (tryAndMatch(terminated, LeftBrace, LeftSquare, LeftAngle) != null) {
			index = start; // backtrack
			return parseLambdaInitialiser(scope, terminated);
		} else {
			index = start; // backtrack
			return parseLambdaConstant(scope, terminated);
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
	private Expr parseLambdaInitialiser(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Ampersand);
		// Now we create a new scope for this lambda expression.
		// It keeps all variables but only the given captured lifetimes.
		// But it keeps all unavailable names, i.e. unaccessible lifetimes
		// from the outer scope cannot be redeclared.
		scope = scope.newEnclosingScope();
		Tuple<Decl.Variable> parameters = parseParameters(scope,MinusGreater);
		// NOTE: expression guanrateed to be terminated by ')'
		Expr body = parseExpression(scope, true);
		match(RightBrace);
		return annotateSourceLocation(
				new Decl.Lambda(new Tuple<>(), new Identifier(""), parameters, body, new Type.Unknown()), start);
	}

	/**
	 * Parse an address expression, which has the form:
	 *
	 * <pre>
	 * TermExpr::= ...
	 *                 | '&' Identifier [ '(' Type (',' Type)* ')']
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
	private Expr parseLambdaConstant(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Ampersand);
		Name name = parseName(scope);
		Type parameters;
		// Check whether or not parameters are supplied
		if (tryAndMatch(terminated, LeftBrace) != null) {
			// Yes, parameters are supplied!
			ArrayList<Type> tmp = new ArrayList<>();
			boolean firstTime = true;
			while (eventuallyMatch(RightBrace) == null) {
				int p_start = index;
				if (!firstTime) {
					match(Comma);
				}
				firstTime = false;
				Type type = parseType(scope);
				tmp.add(type);
			}
			parameters = Type.Tuple.create(tmp);
		} else {
			// No, parameters are not supplied.
			parameters = Type.Void;
		}
		Decl.Link link = new Decl.Link<Decl.Callable>(name);
		Decl.Binding<Type.Callable, Decl.Callable> binding = new Decl.Binding<>(link, new Tuple<>());
		return annotateSourceLocation(new Expr.LambdaAccess(binding, parameters), start);
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
	private Expr parseBitwiseComplementExpression(EnclosingScope scope, boolean terminated) {
		int start = index;
		match(Tilde);
		Expr expression = parseExpression(scope, terminated);
		return annotateSourceLocation(new Expr.BitwiseComplement(Type.Void, expression), start);
	}

	/**
	 * Attempt to parse something which maybe a type, or an expression. The
	 * semantics of this function dictate that it returns an instanceof
	 * Type *only* if what it finds *cannot* be parsed as an
	 * expression, but can be parsed as a type. Otherwise, the state is left
	 * unchanged.
	 *
	 * @return An instance of Type or null.
	 */
	public Type parseDefiniteType(EnclosingScope scope) {
		int start = index; // backtrack point
		try {
			Type type = parseType(scope);
			if (mustParseAsType(type)) {
				return type;
			}
		} catch (SyntacticException e) {

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
	private boolean mustParseAsType(Type type) {
		if (type instanceof Type.Primitive) {
			// All primitive types must be parsed as types, since their
			// identifiers are keywords.
			return true;
		} else if (type instanceof Type.Record) {
			// Record types must be parsed as types, since e.g. {int f} is not a
			// valid expression.
			return true;
		} else if (type instanceof Type.Callable) {
			// "function" and "method" are keywords, cannot parse as expression.
			return true;
		} else if (type instanceof Type.Array) {
			return true;
		} else if (type instanceof Type.Nominal) {
			return false; // always can be an expression
		} else if (type instanceof Type.Reference) {
			Type.Reference tt = (Type.Reference) type;
			return mustParseAsType(tt.getElement());
		} else if (type instanceof Type.Union) {
			Type.Union tt = (Type.Union) type;
			boolean result = false;
			for(int i=0;i!=tt.size();++i) {
				result |= mustParseAsType(tt.get(i));
			}
			return result;
		} else {
			// Error!
			throw new SyntacticException("unknown syntactic type encountered", parent.getEntry(), type);
		}
	}

	public boolean skipType(EnclosingScope scope) {
		if (skipTypeArray(scope)) {
			while (tryAndMatch(false, Ampersand, VerticalBar) != null) {
				if (!skipTypeArray(scope)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean skipTypeArray(EnclosingScope scope) {
		if (skipTypeTerm(scope)) {
			while(tryAndMatch(false,LeftSquare) != null) {
				if(tryAndMatch(false,RightSquare) == null) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean skipTypeTerm(EnclosingScope scope) {
		skipWhiteSpace();
		Token token = tokens.get(index);
		switch (token.kind) {
		case Null:
		case Bool:
		case Byte:
		case Int:
			match(token.kind);
			return true;
		case LeftBrace:
			return skipBracketedType(scope);
		case LeftCurly:
			return skipRecordType(scope);
		case Ampersand:
			return skipReferenceType(scope);
		case Identifier:
			return skipNominalType(scope);
		case Function:
			return skipFunctionType(scope);
		case Method:
			return skipMethodType(scope);
		default:
			return false;
		}
	}

	public boolean skipBracketedType(EnclosingScope scope) {
		match(LeftBrace);
		return skipType(scope) && tryAndMatch(false, RightBrace) != null;
	}

	public boolean skipReferenceType(EnclosingScope scope) {
		match(Ampersand);
		return skipOptionalLifetimeIdentifier(scope) && skipReferenceModifier(scope) && skipType(scope);
	}

	public boolean skipReferenceModifier(EnclosingScope scope) {
		tryAndMatch(false, QuestionMark);
		return true;
	}

	public boolean skipOptionalLifetimeIdentifier(EnclosingScope scope) {
		int start = index;
		if(tryAndMatch(false,Identifier,Star,This) != null) {
			if(tryAndMatch(false,Colon) != null) {
				return true;
			}
		}
		index = start;
		return true;
	}

	public boolean skipLifetimeIdentifier(EnclosingScope scope) {
		int start = index;
		if(tryAndMatch(false,Identifier,Star,This) != null) {
			return true;
		}
		index = start;
		return false;
	}

	public boolean skipNominalType(EnclosingScope scope) {
		boolean definite = false;
		Token token = match(Identifier);
		Identifier id = new Identifier(token.text);
		// Pass all path components
		while(tryAndMatch(false, ColonColon) != null) {
			if(tryAndMatch(false, Identifier) == null) {
				// Something when properly wrong.
				return false;
			} else {
				definite = true;
			}
		}
		// Attempt to parse type parameters (if present)
		if(tryAndMatch(false, LeftAngle) != null) {
			boolean firstTime = true;
			while(tryAndMatch(false, RightAngle) == null) {
				if(!firstTime && tryAndMatch(false,Comma) == null) {
					// something went wrong
					return false;
				} else if(!skipType(scope)) {
					// something went wrong
					return false;
				}
				firstTime=false;
			}
		}
		// If encountered a path (e.g. std::math) then we definitely have a type.
		// Otherwise, is a type only if not already a local variable.
		if(definite || !scope.isVariable(id)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean skipRecordType(EnclosingScope scope) {
		match(LeftCurly);
		boolean firstTime = true;
		while (eventuallyMatch(RightCurly) == null) {
			if (!firstTime && tryAndMatch(false, Comma) == null) {
				return false;
			} else if (tryAndMatch(false, DotDotDot) != null) {
				// this signals an "open" record type
				return tryAndMatch(false, RightCurly) != null;
			} else if (!skipType(scope)) {
				return false;
			} else if (tryAndMatch(false, Identifier) == null) {
				return false;
			}
			firstTime = false;
		}
		return true;
	}

	public boolean skipFunctionType(EnclosingScope scope) {
		match(Function);
		return skipParameterTypes(scope) && (tryAndMatch(false, MinusGreater) != null) && skipParameterTypes(scope);
	}

	public boolean skipMethodType(EnclosingScope scope) {
		match(Method);
		return skipOptionalLifetimes(scope) && skipParameterTypes(scope) && (tryAndMatch(false, MinusGreater) != null)
				&& skipParameterTypes(scope);
	}

	public boolean skipOptionalLifetimes(EnclosingScope scope) {
		if (tryAndMatch(false, LeftAngle) == null) {
			// no lifetimes is OK
			return true;
		} else {
			boolean firstTime = true;
			while (eventuallyMatch(RightAngle) == null) {
				if (!firstTime && tryAndMatch(false, Comma) == null) {
					return false;
				} else if (tryAndMatch(false, Identifier) == null) {
					return false;
				}
				firstTime = false;
			}
			return true;
		}
	}

	public boolean skipParameterTypes(EnclosingScope scope) {
		if(tryAndMatch(false,LeftBrace) != null) {
			boolean firstTime = true;
			while(eventuallyMatch(RightBrace) == null) {
				if (!firstTime && tryAndMatch(false, Comma) == null) {
					return false;
				} else if(!skipType(scope)) {
					return false;
				}
				firstTime=false;
			}
			return true;
		}
		return false;
	}
	/**
	 * Parse a top-level type, which is of the form:
	 *
	 * <pre>
	 * TupleType ::= Type (',' Type)*
	 * </pre>
	 *
	 * @see wyc.lang.Type.Tuple
	 * @return
	 */
	public Type parseType(EnclosingScope scope) {
		scope.step("type");
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
	private Type parseUnionType(EnclosingScope scope) {
		int start = index;
		Type t = parseIntersectionType(scope);
		// Now, attempt to look for union and/or intersection types
		if (tryAndMatch(true, VerticalBar) != null) {
			// This is a union type
			ArrayList<Type> types = new ArrayList<>();
			types.add(t);
			do {
				Type type = parseIntersectionType(scope);
				if(types.contains(type)) {
					syntaxError(WyilFile.EMPTY_TYPE, type);
				}
				types.add(type);
			} while (tryAndMatch(true, VerticalBar) != null);
			//
			Type[] bounds = types.toArray(new Type[types.size()]);
			t = annotateSourceLocation(new Type.Union(bounds), start);
		}
		return t;
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
	private Type parseIntersectionType(EnclosingScope scope) {
		int start = index;
		Type t = parseArrayType(scope);

		// Now, attempt to look for union and/or intersection types

// =============================================================================
// FIXME: this is commented out in connection with RFC#20. The intention is that
// it should be restored at some point in the future.
// =============================================================================
//		if (tryAndMatch(true, Ampersand) != null) {
//			// This is a union type
//			ArrayList<Type> types = new ArrayList<>();
//			types.add(t);
//			do {
//				types.add(parseArrayType(scope));
//			} while (tryAndMatch(true, Ampersand) != null);
//			//
//			Type[] bounds = types.toArray(new Type[types.size()]);
//			t = annotateSourceLocation(new Type.Intersection(bounds),start);
//		}
		return t;
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
	private Type parseArrayType(EnclosingScope scope) {
		int start = index;
		Type element = parseBaseType(scope);
		while (tryAndMatch(true, LeftSquare) != null) {
			match(RightSquare);
			element = annotateSourceLocation(new Type.Array(element),start);
		}

		return element;
	}

	private Type parseBaseType(EnclosingScope scope) {
		checkNotEof();
		int start = index;
		Token token = tokens.get(index);
		Type t;

		switch (token.kind) {
		case Null:
			t = new Type.Null();
			break;
		case Bool:
			t = new Type.Bool();
			break;
		case Byte:
			t = new Type.Byte();
			break;
		case Int:
			t = new Type.Int();
			break;
		case LeftBrace:
			return parseBracketedType(scope);
		case LeftCurly:
			return parseRecordType(scope);
		case Ampersand:
			return parseReferenceType(scope);
		case Identifier:
			return parseNominalType(scope);
		case Function:
			return parseFunctionOrMethodType(true, scope);
		case Method:
			return parseFunctionOrMethodType(false, scope);
		default:
			syntaxError(WyilFile.UNKNOWN_TYPE, token);
			return null;
		}
		match(token.kind);
		return annotateSourceLocation(t,start);
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
	private Type parseReferenceType(EnclosingScope scope) {
		int start = index;
		match(Ampersand);
		// Parse bound
		Type element = parseArrayType(scope);
		Type type = new Type.Reference(element);
		return annotateSourceLocation(type, start);
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
	private Type parseBracketedType(EnclosingScope scope) {
		match(LeftBrace);
		Type type = parseType(scope);
		if (tryAndMatch(true, Comma) == null) {
			match(RightBrace);
			return type;
		} else {
			ArrayList<Type> paramTypes = new ArrayList<>();
			paramTypes.add(type);
			paramTypes.add(parseType(scope));
			while (eventuallyMatch(RightBrace) == null) {
				match(Comma);
				paramTypes.add(parseType(scope));
			}
			return Type.Tuple.create(paramTypes);
		}
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
	private Type parseRecordType(EnclosingScope scope) {
		int start = index;
		match(LeftCurly);
		ArrayList<Type.Field> types = new ArrayList<>();
		Pair<Type, Identifier> p = parseMixedType(scope);
		types.add(new Type.Field(p.getSecond(), p.getFirst()));
		HashSet<Identifier> names = new HashSet<>();
		names.add(p.getSecond());
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
				Identifier id = p.getSecond();
				if (names.contains(id)) {
					syntaxError(WyilFile.DUPLICATE_FIELD, id);
				}
				names.add(id);
				types.add(new Type.Field(id, p.getFirst()));
			}
		}
		// Done
		Tuple<Type.Field> fields = new Tuple<>(types);
		return annotateSourceLocation(new Type.Record(isOpen, fields), start);
	}

	/**
	 * Parse a nominal type, which is of the form:
	 *
	 * <pre>
	 * NominalType ::= Identifier ('.' Identifier)*
	 * </pre>
	 *
	 * @see wyc.lang.Type.Nominal
	 * @return
	 */
	private Type parseNominalType(EnclosingScope scope) {
		int start = index;
		Name name = parseName(scope);
		if (name.size() == 1 && scope.isTypeVariable(name.get(0))) {
			return annotateSourceLocation(new Type.Universal(name.get(0)), start);
		} else {
			Tuple<Type> types = parseTypeParameters(scope);
			return annotateSourceLocation(new Type.Nominal(new Decl.Link(name),types), start);
		}
	}

	private Tuple<Type> parseTypeParameters(EnclosingScope scope) {
		if (tryAndMatch(false, LeftAngle) != null) {
			ArrayList<Type> types = new ArrayList<>();
			while (eventuallyMatch(RightAngle) == null) {
				if (!types.isEmpty()) {
					match(Comma);
				}
				types.add(parseType(scope));
			}
			return new Tuple<>(types);
		} else {
			return new Tuple<>();
		}
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
	private Type parseFunctionOrMethodType(boolean isFunction, EnclosingScope scope) {
		int start = index;
		if (isFunction) {
			match(Function);
		} else {
			match(Method);
			scope = scope.newEnclosingScope();
		}
		// First, parse the parameter type(s).
		Type paramTypes = parseParameterTypes(scope);
		Type returnType = Type.Void;
		// Second, parse the right arrow.
		if (isFunction) {
			// Functions require a return type (since otherwise they are just
			// nops)
			match(MinusGreater);
			// Third, parse the return types.
			returnType = parseType(scope);
		} else if (tryAndMatch(true, MinusGreater) != null) {
			// Third, parse the return type
			returnType = parseType(scope);
		}
		// Done
		Type type;
		if (isFunction) {
			type = new Type.Function(paramTypes, returnType);
		} else {
			type = new Type.Method(paramTypes, returnType);
		}
		return annotateSourceLocation(type,start);
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
	private Pair<Type, Identifier> parseMixedType(EnclosingScope scope) {
		Token lookahead;
		int start = index;

		if ((lookahead = tryAndMatch(true, Function, Method)) != null) {
			// At this point, we *might* have a mixed function / method type
			// definition. To disambiguate, we need to see whether an identifier
			// follows or not.
			// Similar to normal method declarations, the lifetime parameters
			// go before the method name. We do not allow to have captured
			// lifetimes for mixed method types.
			// Now try to parse the identifier
			Identifier id = parseOptionalIdentifier(scope);

			if (id != null) {
				// Yes, we have found a mixed function / method type definition.
				// Therefore, we continue to pass the remaining type parameters.
				Type paramTypes = parseParameterTypes(scope);
				Type returnType = Type.Void;

				if (lookahead.kind == Function) {
					// Functions require a return type (since otherwise they are
					// just nops)
					match(MinusGreater);
					// Third, parse the return type
					returnType = parseType(scope);
				} else if (tryAndMatch(true, MinusGreater) != null) {
					// Third, parse the (optional) return type. Observe that
					// this is forced to be a
					// unit type. This means that any tuple return types must be
					// in braces. The reason for this is that a trailing comma
					// may be part of an enclosing record type and we must
					// disambiguate
					// this.
					returnType = parseType(scope);
				}

				// Done
				Type type;
				if (lookahead.kind == Token.Kind.Function) {
					type = new Type.Function(paramTypes, returnType);
				} else {
					type = new Type.Method(paramTypes, returnType);
				}
				return new Pair<>(annotateSourceLocation(type,start), id);
			} else {
				// In this case, we failed to match a mixed type. Therefore, we
				// backtrack and parse as two separate items (i.e. type
				// identifier).
				index = start; // backtrack
			}
		}

		// This is the normal case, where we expect an identifier to follow the
		// type.
		Type type = parseType(scope);
		Identifier id = parseIdentifier();
		return new Pair<>(type, id);
	}

	private Type parseParameterTypes(EnclosingScope scope) {
		match(LeftBrace);
		ArrayList<Type> paramTypes = new ArrayList<>();
		while (eventuallyMatch(RightBrace) == null) {
			if(!paramTypes.isEmpty()) {
				match(Comma);
			}
			paramTypes.add(parseType(scope));
		}
		return Type.Tuple.create(paramTypes);
	}


	public Type parseOptionalParameterTypes(EnclosingScope scope) {
		int next = skipWhiteSpace(index);
		if (next < tokens.size() && tokens.get(next).kind == LeftBrace) {
			return parseParameterTypes(scope);
		} else {
			return parseType(scope);
		}
	}

	private Name parseName(EnclosingScope scope) {
		int start = index;
		List<Identifier> components = new ArrayList<>();
		components.add(parseIdentifier());
		while (tryAndMatch(false, ColonColon) != null) {
			components.add(parseIdentifier());
		}
		Name nid = new Name(components.toArray(new Identifier[components.size()]));
		return annotateSourceLocation(nid,start);
	}

	private Identifier parseOptionalIdentifier(EnclosingScope scope) {
		int start = index;
		Token token = tryAndMatch(false, Identifier);
		if (token != null) {
			Identifier id = new Identifier(token.text);
			return annotateSourceLocation(id,start);
		} else {
			return null;
		}
	}

	private Identifier parseIdentifier() {
		int start = skipWhiteSpace(index);
		Token token = match(Identifier);
		Identifier id = new Identifier(token.text);
		return annotateSourceLocation(id, start);
	}

	public boolean mustParseAsMixedType() {
		int start = index;
		if (tryAndMatch(true, Function, Method) != null && tryAndMatch(true, Identifier) != null) {
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
			syntaxError(WyilFile.EXPECTING_TOKEN, token, new Value.UTF8(kind.toString()));
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
				syntaxError(WyilFile.EXPECTING_TOKEN, token, new Value.UTF8(kinds[i].toString()));
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
		if (r != null && r.equivalent(indent)) {
			Token t = tryAndMatch(terminated, kinds);
			if (t != null) {
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
			syntaxError(WyilFile.UNEXPECTED_EOF, tokens.get(index));
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
			syntaxError(WyilFile.UNEXPECTED_EOF, tokens.get(index - 1));
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
			if (tmp < tokens.size() && tokens.get(tmp).kind != Token.Kind.NewLine) {
				return; // done
			} else if (tmp >= tokens.size()) {
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
		return token.kind == Token.Kind.Indent || token.kind == Token.Kind.LineComment
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
	protected byte[] parseUnicodeString(Token token) {
		String v = token.text;
		/*
		 * Parsing a string requires several steps to be taken. First, we need
		 * to strip quotes from the ends of the string.
		 */
		v = v.substring(1, v.length() - 1);

		StringBuffer result = new StringBuffer();
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
					result.append(replace);
					i = i + 1;
				}
			} else {
				result.append(v.charAt(i));
			}
		}
		try {
			// Now, convert string into a sequence of UTF8 bytes.
			return result.toString().getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			// This really should be deadcode
			syntaxError(WyilFile.INVALID_UNICODE_LITERAL, token);
			return null; // deadcode
		}
	}

	/**
	 * Parse a token representing an integer literal, such as "112", "1_024", etc.
	 *
	 * @param input
	 *            The token representing the integer value.
	 * @return
	 */
	private BigInteger parseIntegerLiteral(Token input) {
		return new BigInteger(input.text.replace("_", ""));
	}

	/**
	 * Parse a token representing a binary literal, such as "0b0110", "0b1111_0101",
	 * etc.
	 *
	 * @param input
	 *            The token representing the byte value.
	 * @return
	 */
	private byte parseBinaryLiteral(Token input) {
		String text = input.text;
		if (text.length() > 11) {
			// FIXME: this will be deprecated!
			syntaxError(WyilFile.INVALID_BINARY_LITERAL, input);
		}
		int val = 0;
		// Start past 0b
		for (int i = 2; i != text.length(); ++i) {
			char c = text.charAt(i);
			// Skip underscore
			if(c == '_') { continue; }
			val = val << 1;
			if (c == '1') {
				val = val | 1;
			} else if (c == '0') {

			} else {
				syntaxError(WyilFile.INVALID_BINARY_LITERAL, input);
			}
		}
		return (byte) val;
	}

	/**
	 * Parse a token representing a hex literal, such as "0x
	 *
	 * @param input
	 *            The token representing the byte value.
	 * @return
	 */
	private BigInteger parseHexLiteral(Token input) {
		String text = input.text;
		// Start past 0x
		for (int i = 2; i != text.length(); ++i) {
			char c = text.charAt(i);
			if(c != '_' && !isHexDigit(c)) {
				syntaxError(WyilFile.INVALID_HEX_LITERAL, input);
			}
		}
		// Remove "0x" and "_"
		text = input.text.substring(2).replace("_", "");
		return new BigInteger(text,16);
	}

	private boolean isHexDigit(char c) {
		return ('0' <= c && c <= '9') || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F');
	}

	private String[] toStringArray(Tuple<Identifier> identifiers) {
		String[] strings = new String[identifiers.size()];
		for (int i = 0; i != strings.length; ++i) {
			strings[i] = identifiers.get(i).get();
		}
		return strings;
	}

	private void syntaxError(int errcode, SyntacticItem e) {
		// FIXME: personally I think this method indicates something is wrong.
		// Specifically, we've actually parsed something but we are still not happy with
		// it.
		Attribute.Span span = e.getParent(Attribute.Span.class);
		throw new ParseError(errcode, span.getStart().get().intValue(), span.getEnd().get().intValue());
	}

	private void syntaxError(int errcode, Token t, Value... context) {
		throw new ParseError(errcode, t.start, t.end(), context);
	}

	private void syntaxError(int errcode, Token left, Token right) {
		throw new ParseError(errcode, left.start, right.end());
	}

	private <T extends SyntacticItem> T annotateSourceLocation(T item, int start) {
		return annotateSourceLocation(item, start, index - 1);
	}

	private <T extends SyntacticItem> T annotateSourceLocation(T item, int start, int end) {
		// Allocate item to enclosing WhileyFile. This is necessary so that the
		// annotations can then be correctly allocated as well.
		item = parent.allocate(item);
		// Determine the first and last token representing this span.
		Token first = tokens.get(start);
		Token last = tokens.get(end);
		parent.allocate(new Attribute.Span(item,first.start,last.end()));
		return item;
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
					throw new IllegalArgumentException("Space or tab character expected");
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
			return countOfSpaces <= other.countOfSpaces && countOfTabs <= other.countOfTabs;
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
			return countOfSpaces == other.countOfSpaces && countOfTabs == other.countOfTabs;
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
	public class EnclosingScope {
		/**
		 * Enclosing meter for profiling information
		 */
		private final Build.Meter meter;
		/**
		 * The indent level of the enclosing scope.
		 */
		private final Indent indent;
		/**
		 * The set of declared variables in the enclosing scope.
		 */
		private final HashMap<Identifier,Decl.Variable> environment;
		/**
		 * The set of field aliases in the enclosing scope. A field alias occurs
		 * for a record declaration where, for convenience, we allow the type
		 * invariant to refer directly to the field, rather than through a
		 * declared variable.
		 */
		private final HashSet<Identifier> fieldAliases;

		/**
		 * The set of declared type variables in the enclosing scope.
		 */
		private final HashSet<Identifier> typeVariables;

		/**
		 * A simple flag that tells us whether or not we are currently within a
		 * loop. This is necessary to stop break or continue statements which
		 * are written outside of a loop.
		 */
		private final boolean inLoop;

		public EnclosingScope(Build.Meter meter) {
			this.meter = meter;
			this.indent = ROOT_INDENT;
			this.environment = new HashMap<>();
			this.fieldAliases = new HashSet<>();
			this.typeVariables = new HashSet<>();
			this.inLoop = false;
		}

		private EnclosingScope(Build.Meter meter, Indent indent, Map<Identifier, Decl.Variable> variables,
				Set<Identifier> fieldAliases, Set<Identifier> typeVariables,
				boolean inLoop) {
			this.meter = meter;
			this.indent = indent;
			this.environment = new HashMap<>(variables);
			this.fieldAliases = new HashSet<>(fieldAliases);
			this.typeVariables = new HashSet<>(typeVariables);
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
		public boolean isVariable(Identifier name) {
			return environment.containsKey(name);
		}

		/**
		 * Check whether a given name corresponds to a "field alias" in this
		 * scope. A field alias occurs for a record declaration where, for
		 * convenience, we allow the type invariant to refer directly to the
		 * field, rather than through a declared variable.
		 */
		public boolean isFieldAlias(Identifier name) {
			return fieldAliases.contains(name);
		}

		/**
		 * Check whether a given name is a type variable or not.
		 *
		 * @param name
		 * @return
		 */
		public boolean isTypeVariable(Identifier name) {
			return this.typeVariables.contains(name);
		}

		/**
		 * Check whether a given name is available, i.e. can be declared.
		 *
		 * @param id
		 *            identifier that holds the name to check
		 * @throws SyntacticException
		 *             if the name is unavailable (already declared)
		 */
		public void checkNameAvailable(Identifier id) {
			if (!isAvailableName(id)) {
				// name is not available!
				syntaxError(WyilFile.DUPLICATE_DECLARATION, id);
			}
		}

		/**
		 * Get the declaration index corresponding to a given local variable
		 *
		 * @param name
		 * @return
		 */
		public Decl.Variable getVariableDeclaration(Identifier name) {
			return environment.get(name);
		}

		public void step(String tag) {
			meter.step(tag);
		}

		/**
		 * Declare a new variable in this scope.
		 *
		 * @param id
		 *            identifier that holds the name to declare
		 * @throws SyntacticException
		 *             if the name is already declared
		 */
		public void declareVariable(Decl.Variable decl) {
			Identifier id = decl.getName();
			if (!isAvailableName(id)) {
				// name is not available!
				syntaxError(WyilFile.DUPLICATE_DECLARATION, id);
			}
			this.environment.put(id, decl);
		}

		/**
		 * Declare a new field alias in this scope.
		 *
		 * @param alias
		 *            The field alias to declare
		 */
		public void declareFieldAlias(Identifier alias) {
			fieldAliases.add(alias);
		}

		/**
		 * Declare a new type variables
		 * @param name
		 */
		public void declareTemplateVariable(Template.Variable var) {
			Identifier name = var.getName();
			if(!isAvailableName(name)) {
				// name is not available!
				syntaxError(WyilFile.DUPLICATE_DECLARATION, name);
			} else {
				typeVariables.add(name);
			}
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
			return new EnclosingScope(meter,indent, environment, fieldAliases, typeVariables, inLoop);
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
			return new EnclosingScope(meter,indent, environment, fieldAliases, typeVariables, inLoop);
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
			return new EnclosingScope(meter,indent, environment, fieldAliases, typeVariables, inLoop);
		}

		private boolean isAvailableName(Identifier name) {
			if (environment.containsKey(name) || typeVariables.contains(name)) {
				return false;
			} else {
				String str = name.toString();
				return !str.equals("*") && !str.equals("this");
			}
		}
	}

	private static class ParseError extends Error {
		private final int errcode;
		private final int start;
		private final int end;
		private final SyntacticItem[] context;

		public ParseError(int errcode, int start, int end, SyntacticItem... context) {
			super("parse error");
			this.errcode = errcode;
			this.start = start;
			this.end = end;
			this.context = context;
		}

		public int getErrorCode() {
			return errcode;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}
	}
}
