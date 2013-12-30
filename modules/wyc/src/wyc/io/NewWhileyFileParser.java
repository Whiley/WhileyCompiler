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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import wybs.lang.Attribute;
import wybs.lang.Path;
import wybs.lang.SyntaxError;
import wybs.util.Pair;
import wybs.util.Trie;
import wyc.lang.*;
import wyc.io.NewWhileyFileLexer.Token;
import static wyc.io.NewWhileyFileLexer.Token.Kind.*;
import wyc.lang.WhileyFile.*;
import wyil.lang.Modifier;
import static wyc.lang.WhileyFile.*;
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
public class NewWhileyFileParser {
	private String filename;
	private ArrayList<Token> tokens;	
	private int index;
	
	public NewWhileyFileParser(String filename, List<Token> tokens) {
		this.filename = filename;
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
		
		// Now, figure out module name from filename
		// FIXME: this is a hack!
		String name = filename.substring(filename.lastIndexOf(File.separatorChar) + 1,filename.length()-7);		
		WhileyFile wf = new WhileyFile(pkg.append(name),filename);

		skipWhiteSpace();
		while (index < tokens.size()) {
			Token t = tokens.get(index);
			if(t.kind == Import) {
				parseImportDeclaration(wf);
			} else {
				List<Modifier> modifiers = parseModifiers();

				switch (t.kind) {				
				case Type:
					parseTypeDeclaration(wf,modifiers);
					break;
				case Constant:
					parseConstantDeclaration(wf,modifiers);
					break;
				case Function:
					parseFunctionOrMethodDeclaration(wf,modifiers,true);
					break;
				case Method:
					parseFunctionOrMethodDeclaration(wf,modifiers,false);
					break;

				default:
					syntaxError("unrecognised declaration", t);
				}
			}
			skipWhiteSpace();
		}

		return wf;
	}

	private Trie parsePackage() {
		Trie pkg = Trie.ROOT;

		if(tryAndMatch(Package) != null) {			
			// found a package keyword
			pkg = pkg.append(match(Identifier).text);

			while (tryAndMatch(Dot) != null) {
				pkg = pkg.append(match(Identifier).text);
			}

			matchEndLine();
			return pkg;
		} else {
			return pkg; // no package
		}
	}
	
	/**
	 * Parse an import declaration which is of the form:
	 * <pre>
	 * "import" [Identifier|Star "from"] Identifier ('.' Identifier|'*')*
	 * </pre>
	 * 
	 * @param wf
	 */
	private void parseImportDeclaration(WhileyFile wf) {		
		int start = index;
		
		match(Import);
		
		// First, parse "from" usage (if applicable)				
		String name = null;
		Token lookahead = tryAndMatch(Identifier,Star); 
		if(tryAndMatch(From) != null) {
			name = lookahead.text;
			lookahead = match(Identifier);
		} else if(lookahead.kind == Star) {
			syntaxError("wildcard match only permitted on files",lookahead);
		}
			
		// Second, parse package string
		Trie filter = Trie.ROOT.append(lookahead.text);		
		Token token = null;		
		while((token=tryAndMatch(Dot,DotDot)) != null) {
			if(token.kind == DotDot) {				
				filter = filter.append("**");
			} 
			if(tryAndMatch(Star) != null) {
				filter = filter.append("*");
			} else {
				filter = filter.append(match(Identifier).text);
			}			
		}
							
		int end = index;
		matchEndLine();
		
		wf.add(new WhileyFile.Import(filter, name, sourceAttr(start,
				end - 1)));
	}
	
	private List<Modifier> parseModifiers() {
		ArrayList<Modifier> mods = new ArrayList<Modifier>();
		Token lookahead;
		while((lookahead = tryAndMatch(Public,Protected,Private,Native,Export)) != null) {
			switch(lookahead.kind) {
			case Public:
				mods.add(Modifier.PUBLIC);
				break;
			case Protected:
				mods.add(Modifier.PROTECTED);
				break;
			case Private:
				mods.add(Modifier.PRIVATE);
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
	
	private String[] modifiers = {
			"public",
			"export",
			"native"			
	};
	
	private boolean isModifier(Token tok) {
		for(String m : modifiers) {
			if(tok.text.equals(m)) {
				return true;
			}
		}
		return false;
	}
	
	private void parseFunctionOrMethodDeclaration(WhileyFile wf, List<Modifier> modifiers, boolean isFunction) {
		int start = index;

		if(isFunction) {
			match(Function);	
		} else {
			match(Method);
		}
		
		Token name = match(Identifier);
		
		Pattern from = parsePattern();		
		match(EqualsGreater); // "=>" 		
		Pattern to = parsePattern();
		
		ArrayList<Expr> requires = null;
		ArrayList<Expr> ensures = null;
		ArrayList<SyntacticType> throwws = null;
		
		match(Colon);  
		int end = index;
		matchEndLine();
		List<Stmt> stmts = parseBlock(ROOT_INDENT);
		
		WhileyFile.Declaration declaration;
		if(isFunction) {
			declaration = wf.new Method(modifiers, name.text, from, to,
					requires, ensures, throwws, stmts,
					sourceAttr(start, end - 1));
		} else {
			declaration = wf.new Function(modifiers, name.text, from, to,
					requires, ensures, throwws, stmts,
					sourceAttr(start, end - 1));
		}
		wf.add(declaration);		
	}

	private void parseTypeDeclaration(WhileyFile wf, List<Modifier> modifiers) {
		int start = index;
		Token[] tokens = match(Type, Token.Kind.Identifier, Token.Kind.Is);
		SyntacticType t = parseType();
		int end = index;
		matchEndLine();
		
		// TODO: create type declaration!!
	}

	private void parseConstantDeclaration(WhileyFile wf, List<Modifier> modifiers) {
		int start = index;

		Token[] tokens = match(Constant, Token.Kind.Identifier,
				Token.Kind.Is);

		Expr e = parseExpression();
		int end = index;
		matchEndLine();

		// TODO: create constant declaration!!
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
	 * The indentation level of the parent, for which all statements
	 * in this block must have a greater indent. May not be
	 * <code>null</code>.
	 * @return
	 */
	private List<Stmt> parseBlock(Indent parentIndent) {
		// First, determine the initial indentation of this block based on the
		// first statement (or null if there is no statement).
		Indent indent = getIndent();

		// Second, check that this is indeed the initial indentation for this
		// block (i.e. that it is strictly greater than parent indent).
		if (indent == null || indent.lessThanEq(parentIndent)) {
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
					syntaxError("unexpected end-of-block", indent);
				}

				// Second, parse the actual statement at this point!
				stmts.add(parseStatement(indent));
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
		if(index < tokens.size()) {
			Token token = tokens.get(index);
			if(token.kind == Indent) {
				return new Indent(token.text,token.start);
			}
			return null;
		}
		return null;
	}

	/**
	 * Parse a given statement. There are essentially two forms of statement:
	 * <code>simple</code> and <code>compound</code>. Simple statements (e.g.
	 * assignment, <code>print</code>, etc) are always occupy a single line and
	 * are terminated by a <code>NewLine</code> token. Compound statements (e.g.
	 * <code>if</code>, <code>while</code>, etc) themselves contain blocks of
	 * statements and are not (generally) terminated by a <code>NewLine</code>.
	 *
	 * @param indent
	 * The indent level for the current statement. This is needed in
	 * order to constraint the indent level for any sub-blocks (e.g.
	 * for <code>while</code> or <code>if</code> statements).
	 *
	 * @return
	 */
	private Stmt parseStatement(Indent indent) {
		checkNotEof();
		Token lookahead = tokens.get(index);

		switch(lookahead.kind) {
		case Return:
			return parseReturnStatement();
		case If:
			return parseIfStatement(indent);
		case While:
			return parseWhile(indent);
		case For:
			return parseFor(indent);		
		}

		if (isStartOfType(index)) {                        
			return parseVariableDeclaration();
		} else {
			// invocation or assignment
			int start = index;
			Expr t = parseExpression();
			if (t instanceof Expr.AbstractInvoke) {
				matchEndLine();
				return (Expr.AbstractInvoke) t;
			} else {
				index = start;
				return parseAssign();
			}
		}
	}

	/**
	 * <p>
	 * Determine (to a coarse approximation) whether or not a given position
	 * marks the beginning of a type declaration or not. This is important to
	 * help determine whether or not this is the beginning of a variable
	 * declaration.
	 * </p>
	 * 
	 * <p>
	 * This function *must* true if what follows cannot be parsed as an
	 * expression. However, if what follows can be parsed as an expression, then
	 * it is safe for this function to return false (even if that expression
	 * will eventually be determined as a type).
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> this function is assumed to be called from either the
	 * beginning of a statement (i.e. to disambiguate variable declarations), or
	 * after matching a left brace (i.e. to disambiguate casts). This has
	 * important consequences, since it determines what possible things we might
	 * expect to encounter at this point.
	 * </p>
	 * 
	 * @param index
	 *            Position in the token stream to begin looking from.
	 * @return
	 */
	private boolean isStartOfType(int index) {
		if (index >= tokens.size()) {
			return false;
		}

		Token token = tokens.get(index);
		switch(token.kind) {
		case Any:
		case Void:
		case Null:
		case Bool:
		case Byte:
		case Int:
		case Real:
		case Char:
		case String:
			return true;
		case LeftCurly:
		case LeftSquare:                                
			return isStartOfType(index + 1);
		case Shreak:
			return isStartOfType(index + 1);
		}                

		return false;
	}
	
	/**
	 * Parse a variable declaration statement, which has the form:
	 *
	 * <pre>
	 * Type Identifier ['=' Expression] NewLine
	 * </pre>
	 *
	 * The optional <code>Expression</code> assignment is referred to as an
	 * <i>initialiser</i>.
	 *
	 * @return
	 */
	private Stmt.VariableDeclaration parseVariableDeclaration() {
		int start = index;
		// Every variable declaration consists of a declared type and variable
		// name.
		SyntacticType type = parseType();
		Token id = match(Identifier);
		// A variable declaration may optionally be assigned an initialiser
		// expression.
		Expr initialiser = null;
		if (tryAndMatch(Token.Kind.Equals) != null) {
			initialiser = parseExpression();
		}
		// Finally, a new line indicates the end-of-statement
		int end = index;
		matchEndLine();                
		// Done.
		return new Stmt.VariableDeclaration(type, id.text, initialiser,
				sourceAttr(start, end - 1));
	}

	/**
	 * Parse a return statement, which has the form:
	 *
	 * <pre>
	 * "return" [Expression] NewLine
	 * </pre>
	 *
	 * The optional expression is referred to as the <i>return value</i>.
	 * Observe that, when this function is called, we're assuming that "return"
	 * has already been matched.
	 *
	 * @return
	 */
	private Stmt.Return parseReturnStatement() {
		int start = index;

		match(Return);

		Expr e = null;
		// A return statement may optionally have a return expression.
		// Therefore, we first skip all whitespace on the given line.
		int next = skipLineSpace(index);
		// Then, we check whether or not we reached the end of the line. If not,
		// then we assume what's remaining is the returned expression.
		// TODO: note this means expressions must start on the same line as a
		// return. Otherwise, a potentially cryptic error message will be given.
		if (next < tokens.size() && tokens.get(next).kind != NewLine) {
			e = parseExpression();
		}
		// Finally, at this point we are expecting a new-line to signal the
		// end-of-statement.
		int end = index;
		matchEndLine();
		// Done.
		return new Stmt.Return(e, sourceAttr(start, end - 1));
	}
	
	/**
	 * Parse an if statement, which is has the form:
	 *
	 * <pre>
	 * if Expression ':' NewLine Block ["else" ':' NewLine Block]
	 * </pre>
	 *
	 * As usual, the <code>else</block> is optional.
	 *
	 * @param indent
	 * @return
	 */
	private Stmt parseIfStatement(Indent indent) {
		int start = index;
		// An if statement begins with the keyword "if", followed by an
		// expression representing the condition.		
		match(If);
		Expr c = parseExpression();
		// The a colon to signal the start of a block.
		match(Colon);
		matchEndLine();

		int end = index;
		// First, parse the true branch, which is required
		List<Stmt> tblk = parseBlock(indent);

		// Second, attempt to parse the false branch, which is optional.
		List<Stmt> fblk = Collections.emptyList();
		if (tryAndMatch(Else) != null) {        
			// TODO: support "else if" chaining.                        
			match(Colon);
			matchEndLine();                        
			fblk = parseBlock(indent);
		}
		// Done!
		return new Stmt.IfElse(c, tblk, fblk, sourceAttr(start, end - 1));
	}

	/**
	 * Parse a while statement, which has the form:
	 * 
	 * <pre>
	 * "while" Expression ':' NewLine Block
	 * </pre>
	 * 
	 * @param indent
	 * @return
	 */
	private Stmt parseWhile(Indent indent) {
		int start = index;
		match(While);
		Expr condition = parseExpression();
		List<Expr> invariants = new ArrayList<Expr>();
		match(Colon);
		int end = index;
		matchEndLine();                
		List<Stmt> blk = parseBlock(indent);
		return new Stmt.While(condition, invariants, blk, sourceAttr(start, end - 1));
	}

	private Stmt parseFor(Indent indent) {
		int start = index;
		match(For);
		ArrayList<String> variables = new ArrayList<String>();
		variables.add(match(Identifier).text);		
		// FIXME: should be matching (untyped?) Pattern here.		
		if(tryAndMatch(Comma) != null) {
			variables.add(match(Identifier).text);
		}
		match(In);
		Expr source = parseExpression();
		// Parse invariant and variant
		Expr invariant = null;
		if(tryAndMatch(Where) != null) {
			invariant = parseExpression();
		}
		// match start of block
		match(Colon);
		int end = index;
		matchEndLine();
		// parse block
		List<Stmt> blk = parseBlock(indent);
		return new Stmt.ForAll(variables,source,invariant,blk, sourceAttr(start,end-1));
	}

	/**
	 * Parse an assignment statement of the form "lval = expression".
	 *
	 * @return
	 */
	private Stmt parseAssign() {
		// standard assignment
		int start = index;
		
		// FIXME: needs to parse LVal?
		
		Expr lhs = parseExpression();
		if (!(lhs instanceof Expr.LVal)) {
			syntaxError("expecting lval, found " + lhs + ".", lhs);
		}
		match(Equals);
		Expr rhs = parseExpression();
		int end = index;
		matchEndLine();
		return new Stmt.Assign((Expr.LVal) lhs, rhs, sourceAttr(start, end - 1));
	}

	private Expr parseExpression() {
		checkNotEof();
		int start = index;
		Expr lhs = parseConditionExpression();

		Token lookahead = tryAndMatch(LogicalAnd,LogicalOr);
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
			Expr rhs = parseExpression();
			return new Expr.BinOp(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	private Expr parseConditionExpression() {
		int start = index;

		// TODO: parse quantifiers
		
		Expr lhs = parseAppendExpression();

		// TODO: more comparators to go here.
		Token lookahead = tryAndMatch(
					LessEquals,
					LeftAngle,
					GreaterEquals,
					RightAngle,
					EqualsEquals,
					NotEquals,
					Is);

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
				SyntacticType type = parseType();
				Expr.TypeVal rhs = new Expr.TypeVal(type, sourceAttr(start, index - 1));
				return new Expr.BinOp(Expr.BOp.IS,lhs, rhs, sourceAttr(start, index - 1));
			default:
				throw new RuntimeException("deadcode"); // dead-code
			}

			Expr rhs = parseExpression();
			return new Expr.BinOp(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;                
	}

	private Expr parseAppendExpression() {
		int start = index;
		Expr lhs = parseRangeExpression();

		if (tryAndMatch(PlusPlus) != null) {
			Expr rhs = parseExpression();
			return new Expr.BinOp(Expr.BOp.LISTAPPEND, lhs, rhs, sourceAttr(start,
					index - 1));
		}

		return lhs;
	}

	private Expr parseRangeExpression() {
		int start = index;
		Expr lhs = parseAddSubExpression();

		if(tryAndMatch(DotDot) != null) {
			Expr rhs = parseExpression();
			return new Expr.BinOp(Expr.BOp.RANGE, lhs, rhs, sourceAttr(start,
					index - 1));
		}			
		
		return lhs;		
	}

	private Expr parseAddSubExpression() {
		int start = index;
		Expr lhs = parseMulDivExpression();

		Token lookahead = tryAndMatch(Plus,Minus);
		if (lookahead != null) {
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
			Expr rhs = parseExpression();
			return new Expr.BinOp(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	private Expr parseMulDivExpression() {
		int start = index;
		Expr lhs = parseIndexTerm();

		Token lookahead = tryAndMatch(Star,RightSlash,Percent);
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
			Expr rhs = parseExpression();
			return new Expr.BinOp(bop, lhs, rhs, sourceAttr(start, index - 1));
		}

		return lhs;
	}

	private Expr parseIndexTerm() {                
		int start = index;
		Expr lhs = parseTerm();
		Token token;
		
		// FIXME: sublist, dereference arrow

		while ((token = tryAndMatchOnLine(LeftSquare)) != null
				|| (token = tryAndMatch(Dot)) != null) {
			start = index;
			if (token.kind == LeftSquare) {
				Expr rhs = parseAddSubExpression();
				match(RightSquare);
				lhs = new Expr.IndexOf(lhs, rhs, sourceAttr(start, index - 1));
			} else {
				String name = match(Identifier).text;
				lhs = new Expr.RecordAccess(lhs, name, sourceAttr(start,
						index - 1));
			}
		}

		return lhs;
	}

	private Expr parseTerm() {
		checkNotEof();

		int start = index;
		Token token = tokens.get(index++);                

		switch(token.kind) {
		case LeftBrace:
			if (isStartOfType(index)) {
				// indicates a cast
				SyntacticType t = parseType();
				match(RightBrace);
				Expr e = parseExpression();
				return new Expr.Cast(t, e, sourceAttr(start, index - 1));
			} else {
				Expr e = parseExpression();                                
				match(RightBrace);
				return e;
			}
		case Identifier:
			if (tryAndMatch(LeftBrace) != null) {
				// FIXME: bug here because we've already matched the identifier
				return parseInvokeExpr(start,token);
			} else {
				return new Expr.Variable(token.text, sourceAttr(start,
						index - 1));
			}
		case Null:
			return new Expr.Constant(wyil.lang.Constant.V_NULL, sourceAttr(start, index - 1));
		case True:                        
			return new Expr.Constant(wyil.lang.Constant.V_BOOL(true), sourceAttr(start, index - 1));
		case False:
			return new Expr.Constant(wyil.lang.Constant.V_BOOL(false), sourceAttr(start, index - 1));
		case CharValue: {
			char c = parseCharacter(token.text);
			return new Expr.Constant(wyil.lang.Constant.V_CHAR(c), sourceAttr(
					start, index - 1));
		}
		case IntValue: {			
			BigInteger val = new BigInteger(token.text);
			return new Expr.Constant(wyil.lang.Constant.V_INTEGER(val), sourceAttr(
					start, index - 1));
		}
		case RealValue: {
			BigDecimal val = new BigDecimal(token.text);
			return new Expr.Constant(wyil.lang.Constant.V_DECIMAL(val), sourceAttr(
					start, index - 1));
		}		
		case StringValue: {
			String str = parseString(token.text);
			return new Expr.Constant(wyil.lang.Constant.V_STRING(str), sourceAttr(start,
					index - 1));
		}
		case Minus:
			return parseNegation(start);
		case VerticalBar:
			return parseLengthOf(start);
		case LeftSquare:
			return parseListVal(start);
		case LeftCurly:
			return parseRecordVal(start);
		case Shreak:
			return new Expr.UnOp(Expr.UOp.NOT, parseTerm(), sourceAttr(start,
					index - 1));
		}

		syntaxError("unrecognised term", token);
		return null;
	}

	private Expr parseListVal(int start) {
		ArrayList<Expr> exprs = new ArrayList<Expr>();

		boolean firstTime = true;
		while (eventuallyMatch(RightSquare) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;
			exprs.add(parseExpression());
		}

		return new Expr.List(exprs, sourceAttr(start, index - 1));
	}

	private Expr parseRecordVal(int start) {
		HashSet<String> keys = new HashSet<String>();
		HashMap<String, Expr> exprs = new HashMap<String, Expr>();

		Token token = tokens.get(index);
		boolean firstTime = true;
		while (eventuallyMatch(RightCurly) == null) {
			if (!firstTime) {
				match(Comma);
			}
			firstTime = false;

			checkNotEof();
			token = tokens.get(index);
			Token n = match(Identifier);

			if (keys.contains(n.text)) {
				syntaxError("duplicate tuple key", n);
			}

			match(Colon);

			Expr e = parseExpression();
			exprs.put(n.text, e);
			keys.add(n.text);
			checkNotEof();
			token = tokens.get(index);
		}

		return new Expr.Record(exprs, sourceAttr(start, index - 1));
	}

	private Expr parseLengthOf(int start) {                
		Expr e = parseIndexTerm();
		match(VerticalBar);
		return new Expr.LengthOf(e,
				sourceAttr(start, index - 1));
	}

	private Expr parseNegation(int start) {
		Expr e = parseIndexTerm();

		if(e instanceof Expr.Constant) {
			Expr.Constant c = (Expr.Constant) e;
			if (c.value instanceof Constant.Decimal) {
				BigDecimal br = ((Constant.Decimal) c.value).value;
				return new Expr.Constant(wyil.lang.Constant.V_DECIMAL(br.negate()),
						sourceAttr(start, index));
			}
		} 		

		return new Expr.UnOp(Expr.UOp.NEG, e, sourceAttr(start, index));
	}

	/**
	 * Parse an invocation expression, which has the form:
	 * 
	 * <pre>
	 * Identifier '(' ( Expression )* ')'
	 * </pre>
	 * 
	 * Observe that this when this function is called, we're assuming that the
	 * identifier and opening brace has already been matched.
	 * 
	 * @return
	 */
	private Expr.AbstractInvoke parseInvokeExpr(int start, Token name) {
		boolean firstTime = true;
		ArrayList<Expr> args = new ArrayList<Expr>();
		while (eventuallyMatch(RightBrace) == null) {
			if (!firstTime) {
				match(Comma);
			} else {
				firstTime = false;
			}
			Expr e = parseExpression();

			args.add(e);
		}
		
		return new Expr.AbstractInvoke(name.text, null, args, sourceAttr(start, index - 1));
	}

	private SyntacticType parseType() {
		int start = index;
		SyntacticType t = parseBaseType();

		// Now, attempt to look for union types
		if (tryAndMatch(VerticalBar) != null) {
			// this is a union type
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			types.add(t);
			do {
				types.add(parseBaseType());
			} while (tryAndMatch(VerticalBar) != null);
			return new SyntacticType.Union(types, sourceAttr(start, index - 1));
		} else {
			return t;
		}
	}

	private SyntacticType parseBaseType() {
		checkNotEof();
		int start = index;
		Token token = tokens.get(index++);
		SyntacticType t;

		switch (token.kind) {
		case Null:
			return new SyntacticType.Null(sourceAttr(start, index - 1));
		case Void:
			return new SyntacticType.Void(sourceAttr(start, index - 1));
		case Byte:
			return new SyntacticType.Byte(sourceAttr(start, index - 1));
		case Bool:
			return new SyntacticType.Bool(sourceAttr(start, index - 1));
		case Char:
			return new SyntacticType.Char(sourceAttr(start, index - 1));
		case Int:
			return new SyntacticType.Int(sourceAttr(start, index - 1));
		case Real:
			return new SyntacticType.Real(sourceAttr(start, index - 1));
		case String:
			return new SyntacticType.Strung(sourceAttr(start, index - 1));
		case LeftCurly:
			HashMap<String, SyntacticType> types = new HashMap<String, SyntacticType>();

			boolean firstTime = true;
			while (eventuallyMatch(RightCurly) == null) {
				if (!firstTime) {
					match(Comma);
				}
				firstTime = false;

				checkNotEof();
				token = tokens.get(index);
				SyntacticType tmp = parseType();

				Token n = match(Identifier);

				if (types.containsKey(n.text)) {
					syntaxError("duplicate tuple key", n);
				}
				types.put(n.text, tmp);
				checkNotEof();
				token = tokens.get(index);
			}

			return new SyntacticType.Record(types, sourceAttr(start, index - 1));
		case LeftSquare:
			t = parseType();
			match(RightSquare);
			return new SyntacticType.List(t, sourceAttr(start, index - 1));
		case Identifier:
			return new SyntacticType.Named(token.text, sourceAttr(start, index - 1));
		default:
			syntaxError("unknown type encountered",token);
			return null;
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
	 * @param kind
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
	 * accidentally gobble up some important indentation.  If more than one kind is provided then this will try to match any of them.
	 *
	 * @param kind
	 * @return
	 */
	private Token tryAndMatch(Token.Kind... kinds) {                
		int next = skipWhiteSpace(index);
		if(next < tokens.size()) {
			Token t = tokens.get(next);
			for(int i=0;i!=kinds.length;++i) {
				if(t.kind == kinds[i]) {
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
		if(next < tokens.size()) {
			Token t = tokens.get(next);
			if(t.kind == kind) {
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
			throw new SyntaxError("unexpected end-of-file", filename,
					index - 1, index - 1);
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
		return token.kind == Token.Kind.Indent;
	}

	/**
	 * Parse a character from a string of the form 'c' or '\c'.
	 *
	 * @param input
	 * @return
	 */
	public char parseCharacter(String input) {                
		int pos = 1;
		char c = input.charAt(pos++);
		if (c == '\\') {
			// escape code
			switch (input.charAt(pos++)) {
			case 't':
				c = '\t';
				break;
			case 'n':
				c = '\n';
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

	private Attribute.Source sourceAttr(int start, int end) {
		Token t1 = tokens.get(start);
		Token t2 = tokens.get(end);
		// FIXME: problem here with the line numbering ?
		return new Attribute.Source(t1.start, t2.end(), 0);
	}

	private void syntaxError(String msg, Expr e) {
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
	public static class Indent extends Token {
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
		 * The indent to compare against.
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
		 * The indent to compare against.
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
