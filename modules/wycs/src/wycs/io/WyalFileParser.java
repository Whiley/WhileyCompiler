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

package wycs.io;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import wyautl.util.BigRational;
import wycc.io.AbstractLexer;
import wycc.io.Token;
import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;
import wycc.lang.SyntaxError;
import wycc.util.Pair;
import wycc.util.Triple;
import wycs.core.SemanticType;
import wycs.core.Value;
import wycs.syntax.*;
import wycs.syntax.WyalFile.Assert;
import wycs.syntax.WyalFile.Macro;
import wycs.syntax.WyalFile.Function;
import wyfs.lang.Path;
import wyfs.util.Trie;

public class WyalFileParser {
	public final int SPACES_PER_TAB = 4;
	
	protected String filename;
	protected ArrayList<Token> tokens;		
	protected int index;

	public WyalFileParser(String filename, List<Token> tokens) {
		this.filename = filename;
		this.tokens = new ArrayList<Token>(tokens);
	}
	
	public WyalFile parse() {		
		Path.ID pkg = parsePackage();
		
		String name = filename.substring(filename.lastIndexOf(File.separatorChar) + 1,filename.length()-5);
		WyalFile wf = new WyalFile(pkg.append(name),filename);

		Token lookahead;
		while ((lookahead = lookahead()) != null) {
			if (matches(lookahead,"assert")) {
				parseAssert(wf);
			} else if (matches(lookahead,"function")) {
				parseFunction(wf);
			} else if (matches(lookahead,"define")) {
				parseDefine(wf);
			} else if (matches(lookahead,"import")) {
				parseImport(wf);
			} else {
				syntaxError("unrecognised statement (" + lookahead.text + ")",
						lookahead);
				return null;
			}
		}
		
		return wf;
	}	
	
	protected Trie parsePackage() {
		Trie pkg = Trie.ROOT;

		if (matches("package")) {
			match("package");

			pkg = pkg.append(matchIdentifier().text);

			while (matches(".")) {
				match(".");
				pkg = pkg.append(matchIdentifier().text);
			}

			return pkg;
		} else {
			return pkg; // no package
		}
	}

	protected void parseImport(WyalFile wf) {
		int start = index;
		match("import");
		
		// first, check if from is used
		String name = null;
		if ((index + 1) < tokens.size()
				&& tokens.get(index + 1).text.equals("from")) {
			Token t = tokens.get(index);
			if (matches(t,"*")) {
				match("*");
				name = "*";
			} else {
				name = matchIdentifier().text;
			}
			matchIdentifier();
		}
				
		Trie filter = Trie.ROOT.append(matchIdentifier().text);
		
		Token lookahead;
		while ((lookahead = lookahead()) != null) {
			if(matches(lookahead,".")) {
				match(".");							
			} else if(matches(lookahead,"..")) {
				match("..");					
				filter = filter.append("**");
			} else {
				break;
			}
			
			if((lookahead = lookahead()) != null) {
				if(matches(lookahead,"*")) {
					match("*");
					filter = filter.append("*");	
				} else {
					filter = filter.append(matchIdentifier().text);
				}
			}
		}
							
		int end = index;		
		matchEndOfLine();
		wf.add(wf.new Import(filter, name, sourceAttr(start, end - 1)));		
	}
	
	protected void parseAssert(WyalFile wf) {
		int start = index;
		match("assert");	
		skipWhiteSpace();
		String msg = null;
		if (matches(Token.String.class)) {
			Token.String s = match(Token.String.class);
			msg = s.text.substring(1,s.text.length()-1);			
		}
		Expr condition;
		if(matches(":")) {
			match(":");
			matchEndOfLine();		
			condition = parseBlock(0,new HashSet<String>(), new HashSet<String>());
		} else {
			condition = parseCondition(new HashSet<String>(), new HashSet<String>());
		}
		wf.add(wf.new Assert(msg, condition, sourceAttr(start, index - 1)));		
	}

	protected void parseFunction(WyalFile wf) {
		int start = index;
		match("function");

		HashSet<String> environment = new HashSet<String>();
		ArrayList<String> generics = new ArrayList<String>();
		String name = parseGenericSignature(environment,generics);
		
		HashSet<String> genericSet = new HashSet<String>(generics);
		TypePattern from = parseTypePattern(genericSet,environment);
				
		// function!			
		match("=>");
		TypePattern to = parseTypePattern(genericSet, environment);

		Expr condition = null;
		if(matches("as")) {
			match("as");
			if(matches(":")) {
				match(":");
				matchEndOfLine();
				condition = parseBlock(0,genericSet, environment);
			} else {
				condition = parseCondition(genericSet, environment);
			}
		}
		wf.add(wf.new Function(name, generics, from, to, condition,
				sourceAttr(start, index - 1)));
	}
	
	protected void parseDefine(WyalFile wf) {
		int start = index;
		match("define");

		HashSet<String> environment = new HashSet<String>();
		ArrayList<String> generics = new ArrayList<String>();
		String name = parseGenericSignature(environment, generics);

		HashSet<String> genericSet = new HashSet<String>(generics);
		TypePattern from = parseTypePattern(genericSet,environment);

		match("as");
		Expr condition;
		if(matches(":")) {
			match(":");
			matchEndOfLine();
			condition = parseBlock(0,genericSet, environment);
		} else {
			condition = parseCondition(genericSet, environment);
		}
		wf.add(wf.new Macro(name, generics, from, condition, sourceAttr(start,
				index - 1)));
	}
	

	protected Expr parseBlock(int parentIndent,
			HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		int indent = scanIndent();
		if(indent <= parentIndent) {
			// empty block
			return Expr.Constant(Value.Bool(true));
		}
		parentIndent = indent;
		ArrayList<Expr> constraints = new ArrayList<Expr>();
		while(indent >= parentIndent && index < tokens.size()) {
			matchIndent(indent);
			constraints.add(parseStatement(indent,generics,environment));
			indent = scanIndent();
		}
		if(constraints.size() == 0) {
			return Expr.Constant(Value.Bool(true));
		} else if(constraints.size() == 1) {
			return constraints.get(0);
		} else {
			Expr[] operands = constraints.toArray(new Expr[constraints.size()]);
			return Expr.Nary(Expr.Nary.Op.AND, operands, sourceAttr(start,index-1));
		}
	}
	
	protected Expr parseStatement(int parentIndent,
			HashSet<String> generics, HashSet<String> environment) {
		if(matches("if")) {
			return parseIfThen(parentIndent,generics,environment);
		} else if(matches("forall")) {
			return parseSomeForAll(false,parentIndent,generics,environment);
		} else if(matches("some")) {
			return parseSomeForAll(true,parentIndent,generics,environment);
		} else if(matches("case")) {
			return parseCase(parentIndent,generics,environment);
		} else if(matches("not")) {
			return parseNot(parentIndent,generics,environment);
		} else {
			Expr e = parseCondition(generics,environment);
			matchEndOfLine();
			return e;
		}
	}
	
	protected Expr parseIfThen(int parentIndent, HashSet<String> generics,
			HashSet<String> environment) {
		int start = index;
		match("if");
		match(":");
		matchEndOfLine();
		Expr condition = parseBlock(parentIndent, generics, environment);
		match("then");
		match(":");
		matchEndOfLine();
		Expr body = parseBlock(parentIndent, generics, environment);
		return Expr.Binary(Expr.Binary.Op.IMPLIES, condition, body,
				sourceAttr(start, index - 1));
	}
	
	protected Expr parseSomeForAll(boolean isSome, int parentIndent,
			HashSet<String> generics, HashSet<String> oEnvironment) {
		HashSet<String> environment = new HashSet<String>(oEnvironment);
		int start = index;
		if(isSome) {
			match("some");
		} else {
			match("forall");
		}						
		TypePattern pattern = parseTypePattern(generics,environment);		
		Expr body;
		if(matches(";")) {
			// at this point it has become clear that we are matching an
			// expression, rather than a block statement. Therefore, the best
			// way to resolve this is to back track.
			index = start; // backtrack
			Expr r = parseCondition(generics,oEnvironment);
			matchEndOfLine();
			return r;
		} else {
			match(":");
			matchEndOfLine();			
			body = parseBlock(parentIndent,generics,environment);
		}		
		if (isSome) {
			return Expr.Exists(pattern, body, sourceAttr(start, index - 1));
		} else {
			return Expr.ForAll(pattern, body, sourceAttr(start, index - 1));
		}
	}
	
	protected Expr parseCase(int parentIndent,
			HashSet<String> generics, HashSet<String> environment) {
		int start = index;		
		match("case");
		match(":");
		matchEndOfLine();
		ArrayList<Expr> cases = new ArrayList<Expr>();
		cases.add(parseBlock(parentIndent,generics,environment));
		int indent = parentIndent;
		while(indent >= parentIndent && index < tokens.size()) {
			int tmp = index;
			matchIndent(indent);
			if(!matches("case")) {
				// breakout point
				index = tmp; // backtrack
				break;
			}
			match("case");
			match(":");
			matchEndOfLine();			
			cases.add(parseBlock(parentIndent,generics,environment));
			indent = scanIndent();
		}		
		if(cases.size() == 0) {
			return Expr.Constant(Value.Bool(true));
		} else if(cases.size() == 1) {
			return cases.get(0);
		} else {
			Expr[] operands = cases.toArray(new Expr[cases.size()]);
			return Expr.Nary(Expr.Nary.Op.OR, operands, sourceAttr(start,index-1));
		}
	}	
	
	protected Expr parseNot(int parentIndent, HashSet<String> generics,
			HashSet<String> environment) {
		int start = index;
		match("not");
		match(":");
		matchEndOfLine();
		Expr operand = parseBlock(parentIndent, generics, environment);
		return Expr.Unary(Expr.Unary.Op.NOT, operand,
				sourceAttr(start, index - 1));
	}
	
	protected String parseGenericSignature(Set<String> environment,
			List<String> generics) {
		String name = matchIdentifier().text;
		if (matches("<")) {
			// generic type
			match("<");
			boolean firstTime = true;
			Token lookahead;
			while ((lookahead = lookahead()) != null
					&& !matches(lookahead, ">")) {
				if (!firstTime) {
					match(",");
				}
				firstTime = false;
				Token.Identifier id = matchIdentifier();
				String generic = id.text;
				if (generics.contains(generic)) {
					syntaxError("duplicate generic variable", id);
				}
				generics.add(generic);
			}
			match(">");
		}
		return name;
	}
	
	protected Expr parseTupleExpression(HashSet<String> generics,
			HashSet<String> environment) {
		int start = index;
		Expr e = parseCondition(generics, environment);
		if (matches(",")) {
			// this is a tuple constructor
			ArrayList<Expr> exprs = new ArrayList<Expr>();
			exprs.add(e);
			while (matches(",")) {
				match(",");
				exprs.add(parseCondition(generics, environment));
			}
			return Expr.Nary(Expr.Nary.Op.TUPLE, exprs, sourceAttr(start,
					index - 1));
		} else {
			return e;
		}
	}
	
	protected Expr parseCondition(HashSet<String> generics,
			HashSet<String> environment) {
		checkNotEof();
		int start = index;
		Expr c1 = parseAndOrCondition(generics, environment);
		Token lookahead = lookahead();		
		if (matches(lookahead, "==>")) {
			match("==>");
			Expr c2 = parseCondition(generics, environment);
			return Expr.Binary(Expr.Binary.Op.IMPLIES, c1, c2,
					sourceAttr(start, index - 1));
		} else if (matches(lookahead, "<==>")) {
			match("<==>");
			Expr c2 = parseCondition(generics, environment);
			return Expr.Binary(Expr.Binary.Op.IFF, c1, c2,
					sourceAttr(start, index - 1));
		}		

		return c1;
	}
	
	protected Expr parseAndOrCondition(HashSet<String> generics, HashSet<String> environment) {
		checkNotEof();
		int start = index;		
		Expr c1 = parseConditionExpression(generics,environment);		

		Token lookahead = lookahead();
		
		if (matches(lookahead, "&&")) {
			match("&&");
			Expr c2 = parseAndOrCondition(generics, environment);
			return new Expr.Binary(Expr.Binary.Op.AND, c1, c2,
					sourceAttr(start, index - 1));
		} else if (matches(lookahead, "||")) {
			match("||");
			Expr c2 = parseAndOrCondition(generics, environment);
			return new Expr.Binary(Expr.Binary.Op.OR, c1, c2,
					sourceAttr(start, index - 1));
		}		
		
		return c1;		
	}
		
	protected Expr parseConditionExpression(HashSet<String> generics, HashSet<String> environment) {		
		int start = index;
			
		Token lookahead = lookahead();
		
		if (matches(lookahead,"forall",Token.sUC_FORALL)) {
			match("forall",Token.sUC_FORALL);
			return parseQuantifier(start, true, generics, environment);
		} else if (matches(lookahead,"exists",Token.sUC_EXISTS)) {
			match("exists",Token.sUC_EXISTS);
			return parseQuantifier(start, false, generics, environment);
		}
		
		Expr lhs = parseRangeExpression(generics,environment);
		
		lookahead = lookahead();
		
		if (matches(lookahead,"<=",Token.sUC_LESSEQUALS)) {
			match("<=",Token.sUC_LESSEQUALS);				
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.LTEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (matches(lookahead,"<")) {
			match("<");				
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.LT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (matches(lookahead,">=",Token.sUC_GREATEREQUALS)) {
			match(">=",Token.sUC_GREATEREQUALS);
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.GTEQ,  lhs,  rhs, sourceAttr(start,index-1));
		} else if (matches(lookahead,">")) {
			match(">");			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.GT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (matches(lookahead,"==")) {
			match("==");			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.EQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (matches(lookahead,"!=")) {
			match("!=");			
			Expr rhs = parseAddSubExpression(generics,environment);			
			return Expr.Binary(Expr.Binary.Op.NEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (matches(lookahead,"in",Token.sUC_ELEMENTOF)) {
			match("in",Token.sUC_ELEMENTOF);
			Expr rhs = parseRangeExpression(generics,environment);			
			return Expr.Binary(Expr.Binary.Op.IN, lhs,  rhs, sourceAttr(start,index-1));
		} else if (matches(lookahead,Token.sUC_SUBSETEQ)) {
			match(Token.sUC_SUBSETEQ);
			Expr rhs = parseRangeExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.SUBSETEQ, lhs, rhs, sourceAttr(start,index-1));
		} else if (matches(lookahead,Token.sUC_SUBSET)) {
			match(Token.sUC_SUBSET);
			Expr rhs = parseRangeExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.SUBSET, lhs,  rhs, sourceAttr(start,index-1));
		} 		
		
		return lhs;	
	}
	
	protected Expr parseRangeExpression(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		Expr lhs = parseAddSubExpression(generics,environment);
		
		Token lookahead = lookahead();

		if (matches(lookahead,"..")) {
			match("..");
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.RANGE, lhs, rhs, sourceAttr(start,
					index - 1));
		} 
		
		return lhs;
	}
	
	protected Expr parseAddSubExpression(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		Expr lhs = parseMulDivExpression(generics,environment);
		
		Token lookahead = lookahead();

		if (matches(lookahead,"++")) {
			match("++");
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.LISTAPPEND, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (matches(lookahead,"+")) {
			match("+");
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.ADD, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (matches(lookahead,"-")) {
			match("-");
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.SUB, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (matches(lookahead, Token.sUC_SETUNION)) {
			match(Token.sUC_SETUNION);
			Expr rhs = parseAddSubExpression(generics, environment);
			return Expr.Binary(Expr.Binary.Op.SETUNION, lhs, rhs,
					sourceAttr(start, index - 1));
		} else if (matches(lookahead, Token.sUC_SETINTERSECTION)) {
			match(Token.sUC_SETINTERSECTION);
			Expr rhs = parseAddSubExpression(generics, environment);
			return Expr.Binary(Expr.Binary.Op.SETINTERSECTION, lhs, rhs,
					sourceAttr(start, index - 1));
		}	
		
		return lhs;
	}
	
	protected Expr parseMulDivExpression(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		Expr lhs = parseIndexTerm(generics,environment);
		
		Token lookahead = lookahead();		
		if (matches(lookahead,"*")) {
			match("*");
			Expr rhs = parseMulDivExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.MUL, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (matches(lookahead,"/")) {
			match("/");
			Expr rhs = parseMulDivExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.DIV, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (matches(lookahead,"%")) {
			match("%");
			Expr rhs = parseMulDivExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.REM, lhs, rhs, sourceAttr(start,
					index - 1));
		}		

		return lhs;
	}	
	
	protected Expr parseIndexTerm(HashSet<String> generics, HashSet<String> environment) {
		checkNotEof();
		int start = index;
		int ostart = index;		
		Expr lhs = parseTerm(generics,environment);

		while (matches(lookahead(), "[")) {
			start = index;
			match("[");
			Expr rhs = parseAddSubExpression(generics,environment);
			if (matches(":=")) {
				// list update operation
				match(":=");
				Expr val = parseRangeExpression(generics, environment);
				match("]");
				lhs = Expr.Ternary(Expr.Ternary.Op.UPDATE, lhs, rhs, val,
						sourceAttr(start, index - 1));
			} else if(matches("..")) {
				// sublist operation
				match("..");
				Expr end = parseAddSubExpression(generics,environment);
				match("]");
				lhs = Expr.Ternary(Expr.Ternary.Op.SUBLIST, lhs, rhs, end,
						sourceAttr(start, index - 1));
			} else {
				match("]");
				lhs = Expr.IndexOf(lhs, rhs,sourceAttr(start, index - 1));
			}
		}
		
		return lhs;		
	}
		
	protected Expr parseTerm(HashSet<String> generics, HashSet<String> environment) {		
		checkNotEof();		
		
		int start = index;
		Token token = tokens.get(index);		
		
		if(matches("(")) {
			return parseBracketedExpression(generics,environment);
		} else if (matches("null")) {
			match("null");			
			return Expr.Constant(null,
					sourceAttr(start, index - 1));
		} else if (matches("true")) {
			match("true");			
			return Expr.Constant(Value.Bool(true),
					sourceAttr(start, index - 1));
		} else if (matches("false")) {	
			match("false");
			return Expr.Constant(Value.Bool(false),
					sourceAttr(start, index - 1));			
		} else if (matches(Token.String.class)) {
			String str = match(Token.String.class).text;
				return Expr.Constant(
						Value.String(str.substring(1, str.length() - 1)),
						sourceAttr(start, index - 1));
		} else if (matches(Token.Identifier.class)) {
			return parseVariableOrFunCall(generics,environment);
		} else if (token instanceof Token.Number) {			
			return parseNumber(generics,environment);			
		} else if (matches("-")) {
			return parseNegation(generics,environment);
		} else if (matches("|")) {			
			return parseLengthOf(generics,environment);
		} else if (matches("!")) {
			match("!");
			return Expr.Unary(Expr.Unary.Op.NOT, parseTerm(generics,environment), sourceAttr(
					start, index - 1));
		} else if (matches("{")) {
			return parseSet(generics,environment);
		} else if (matches("[")) {
			return parseList(generics,environment);
		} 		
		syntaxError("unrecognised term.",token);
		return null;		
	}
	
	/**
	 * Parse an expression beginning with a left brace. This is either a cast or
	 * bracketed expression:
	 * 
	 * <pre>
	 * BracketedExpr ::= '(' Type ')' Expr
	 *                   | '(' Expr ')'
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
	 * @param generics
	 *            The set of declared generic variables visible in the enclosing
	 *            scope. This is necessary to identify types involving generic
	 *            variables.
	 * @param environment
	 *            The set of declared variables visible in the enclosing scope.
	 *            This is necessary to identify local variables within this
	 *            expression.
	 * @return
	 */
	protected Expr parseBracketedExpression(HashSet<String> generics,
			HashSet<String> environment) {
		int start = index;
		
		match("(");
		
		// At this point, we must begin to disambiguate casts from general
		// bracketed expressions. In the case that what follows the left brace
		// is something which can only be a type, then clearly we have a cast.
		// However, in the other case, we may still have a cast since many types
		// cannot be clearly distinguished from expressions at this stage (e.g.
		// "(nat,nat)" could either be a tuple type (if "nat" is a type) or a
		// tuple expression (if "nat" is a variable or constant).
		//SyntacticType t = parseDefiniteType();
		
		//if(t != null) {
			// At this point, it's looking likely that we have a cast. However,
			// it's not certain because of the potential for nested braces. For
			// example, consider "((char) x + y)". We'll parse the outermost
			// brace and what follows *must* be parsed as either a type, or
			// bracketed type.
		//}
		
		// We still may have either a cast or a bracketed expression, and we
		// cannot tell which yet.  
		index = start;		
		Expr v = parseTupleExpression(generics, environment);

		match(")");
		
		// At this point, we now need to examine what follows to see whether
		// this is a cast or bracketed expression. See JavaDoc comments
		// above for more on this. What we do is first skip any whitespace,
		// and then see what we've got.		
				
//		if (index < tokens.size()) {
//			Token lookahead = lookahead();
//			switch(lookahead.text) {
//			
//			}
//		}

		return v;
	}
	
	protected Expr.Constant parseNumber(HashSet<String> generics, HashSet<String> environment)  {
		int start = index;
		Token.Number token = match(Token.Number.class);
		if(token.afterPoint == null) {
			return Expr.Constant(Value.Integer(token.beforePoint),
					sourceAttr(start, index - 1));
		} else {
			BigDecimal br = new BigDecimal(token.text);
			return Expr.Constant(Value.Decimal(br),
					sourceAttr(start, index - 1));
		}
	}
	
	protected Expr parseLengthOf(HashSet<String> generics,
			HashSet<String> environment) {
		int start = index;
		match("|");
		Expr e = parseRangeExpression(generics, environment);
		match("|");
		return Expr.Unary(Expr.Unary.Op.LENGTHOF, e,
				sourceAttr(start, index - 1));
	}
	
	protected Expr parseSet(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match("{");
		ArrayList<Expr> elements = new ArrayList<Expr>();
		boolean firstTime = true;
		Token lookahead = lookahead();
		while ((lookahead = lookahead()) != null && !matches(lookahead, "}")) {
			if (!firstTime) {
				match(",");
			}
			firstTime = false;
			elements.add(parseCondition(generics, environment));
		}
		match("}");
		return Expr.Nary(Expr.Nary.Op.SET, elements,
				sourceAttr(start, index - 1));
	}
	
	protected Expr parseList(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match("[");
		ArrayList<Expr> elements = new ArrayList<Expr>();
		boolean firstTime = true;
		int i = 0;
		Token lookahead;
		while ((lookahead = lookahead()) != null && !matches(lookahead,"]")) {
			if (!firstTime) {
				match(",");
			}
			firstTime = false;
			Expr rhs = parseCondition(generics, environment);
			Expr lhs = Expr.Constant(Value.Integer(BigInteger.valueOf(i++)),
					sourceAttr(start, index - 1));
			Expr pair = Expr.Nary(Expr.Nary.Op.TUPLE,
					new Expr[] { lhs, rhs }, sourceAttr(start, index - 1));
			elements.add(pair);
		}
		match("]");
		return Expr.Nary(Expr.Nary.Op.SET, elements,
				sourceAttr(start, index - 1));
	}
	
	protected Expr parseVariableOrFunCall(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		Token.Identifier id = matchIdentifier(); 
		String name = id.text;
		if(!environment.contains(name)) {
			ArrayList<SyntacticType> genericArguments = new ArrayList<SyntacticType>();
			if(matches("<")) {
				match("<");
				boolean firstTime=true;
				Token lookahead = lookahead();
				while ((lookahead = lookahead()) != null
						&& !matches(lookahead, ">")) {
					if(!firstTime) {
						match(",");
					}
					firstTime=false;
					genericArguments.add(parseSyntacticTypeUnionOrIntersection(generics));
				}
				match(">");
			} 
			if(matches("(")) {
				match("(");
				Expr argument;
				if (matches(")")) {
					// no arguments case
					argument = Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[0],
							sourceAttr(start, index - 1));
				} else {
					argument = parseTupleExpression(generics, environment);
				}
				match(")");
				return Expr.FunCall(name, genericArguments
						.toArray(new SyntacticType[genericArguments.size()]),
						argument, sourceAttr(start, index - 1));
			}
			syntaxError("unknown variable encountered",id);
			return null;
		} else {
			return Expr.Variable(name, sourceAttr(start, index - 1));
		}
	}
	
	protected Expr parseQuantifier(int start, boolean forall, HashSet<String> generics, HashSet<String> environment) {
		environment = new HashSet<String>(environment);
		boolean firstTime = true;		
		TypePattern pattern = parseTypePattern(generics,environment);
		match(";");
		Expr condition = parseCondition(generics,environment);				
		
		if (forall) {
			return Expr.ForAll(pattern, condition, sourceAttr(start,
					index - 1));
		} else {
			return Expr.Exists(pattern, condition, sourceAttr(start,
					index - 1));
		}
	}
		
	protected Expr parseNegation(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match("-");
		
		Expr e = parseIndexTerm(generics,environment);
		
		if (e instanceof Expr.Constant) {
			Expr.Constant c = (Expr.Constant) e;
			if (c.value instanceof Value.Integer) {
				Value.Integer i = (Value.Integer) c.value;
				java.math.BigInteger bi = (BigInteger) i.value;
				return Expr.Constant(Value.Integer(bi
						.negate()), sourceAttr(start, index));
			} else if (c.value instanceof Value.Decimal) {
				Value.Decimal r = (Value.Decimal) c.value;
				BigDecimal br = (BigDecimal) r.value;
				return Expr.Constant(Value.Decimal(br
						.negate()), sourceAttr(start, index));
			}
		}
		
		return Expr.Unary(Expr.Unary.Op.NEG, e, sourceAttr(start, index));		
	}
	
	protected SyntacticType parseSyntacticType(HashSet<String> generics) {
		int start = index;
		SyntacticType t = parseSyntacticTypeUnionOrIntersection(generics);

		if (matches(",")) {
			// indicates a tuple
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			types.add(t);
			while (matches(",")) {
				match(",");
				types.add(parseSyntacticTypeUnionOrIntersection(generics));
			}
			t = new SyntacticType.Tuple(types.toArray(new SyntacticType[types
					.size()]), sourceAttr(start, index - 1));
		}

		return t;
	}
	
	protected SyntacticType parseSyntacticTypeUnionOrIntersection(HashSet<String> generics) {
		int start = index;
		SyntacticType t1 = parseSyntacticTypeAtom(generics);

		Token lookahead = lookahead();		
		if (matches(lookahead, "|")) {
			match("|");
			SyntacticType t2 = parseSyntacticTypeUnionOrIntersection(generics);
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			types.add(t1);
			types.add(t2);
			t1 = new SyntacticType.Union(types.toArray(new SyntacticType[types
			                                                          .size()]), sourceAttr(start, index - 1));
		} else if (matches(lookahead, "&")) {
			match("&");
			SyntacticType t2 = parseSyntacticTypeUnionOrIntersection(generics);
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			types.add(t1);
			types.add(t2);
			t1 = new SyntacticType.Intersection(
					types.toArray(new SyntacticType[types.size()]),
					sourceAttr(start, index - 1));
		}		

		return t1;
	}
	
	protected SyntacticType parseSyntacticTypeAtom(HashSet<String> generics) {				
		
		checkNotEof();
		int start = index;
		Token token = tokens.get(index);
		SyntacticType t;
		
		if(token.text.equals("any")) {
			match("any");
			t = new SyntacticType.Primitive(SemanticType.Any,sourceAttr(start,index-1));
		} else if(token.text.equals("int")) {
			match("int");			
			t = new SyntacticType.Primitive(SemanticType.Int,sourceAttr(start,index-1));
		} else if(token.text.equals("real")) {
			match("real");		
			t = new SyntacticType.Primitive(SemanticType.Real,sourceAttr(start,index-1));
		} else if(token.text.equals("void")) {
			match("void");
			t = new SyntacticType.Primitive(SemanticType.Void,sourceAttr(start,index-1));
		} else if(token.text.equals("bool")) {
			match("bool");
			t = new SyntacticType.Primitive(SemanticType.Bool,sourceAttr(start,index-1));
		} else if(token.text.equals("string")) {
			match("string");
			t = new SyntacticType.Primitive(SemanticType.String,sourceAttr(start,index-1));
		} else if (matches(token,"(")) {
			match("(");
			if(matches(")")) {
				// empty tuple
				match(")");
				t = new SyntacticType.Tuple(new SyntacticType[0], null,
						null, sourceAttr(start, index - 1));
			} else {
				// non-empty tuple
				t = parseSyntacticType(generics);
				match(")");				
			}	
			
		} else if(matches(token,"!")) {
			match("!");
			t = new SyntacticType.Negation(parseSyntacticType(generics),sourceAttr(start,index-1));
		} else if (matches(token,"{")) {		
			match("{");
			t = new SyntacticType.Set(parseSyntacticType(generics),sourceAttr(start,index-1));
			match("}");
		} else if (matches(token,"[")) {		
			match("[");
			t = new SyntacticType.List(parseSyntacticType(generics),sourceAttr(start,index-1));
			match("]");
		} else if(token instanceof Token.Identifier) {
			String id = matchIdentifier().text;
			if(generics.contains(id)) {
				t = new SyntacticType.Variable(id,sourceAttr(start,index-1));
			} else {
				syntaxError("unknown generic type encountered",token);
				return null;
			}
		} else {
			syntaxError("unknown type encountered",token);
			return null; // deadcode
		}
					
		return t;
	}	
	
	protected TypePattern parseTypePattern(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		TypePattern t = parseTypePatternUnionOrIntersection(generics,environment);

		if (matches(",")) {
			// indicates a tuple
			ArrayList<TypePattern> types = new ArrayList<TypePattern>();
			types.add(t);
			while (matches(",")) {
				match(",");
				types.add(parseTypePatternUnionOrIntersection(generics,environment));
			}
			t = new TypePattern.Tuple(types.toArray(new TypePattern[types
					.size()]), null, null, null, sourceAttr(start,
					index - 1));
		}

		return t;
	}
	
	protected TypePattern parseTypePatternUnionOrIntersection(
			HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		TypePattern p = parseTypePatternAtom(generics, environment);

		Token lookahead = lookahead();
		
		if (matches(lookahead, "|")) {
			match("|");
			SyntacticType t = parseSyntacticTypeUnionOrIntersection(generics);
			t = new SyntacticType.Union(new SyntacticType[] {
					p.toSyntacticType(), t }, sourceAttr(start, index - 1));
			p = new TypePattern.Leaf(t, null, null, null, sourceAttr(start, index - 1));
		} else if (matches(lookahead, "&")) {
			match("&");
			SyntacticType t = parseSyntacticTypeUnionOrIntersection(generics);
			t = new SyntacticType.Intersection(new SyntacticType[] {
					p.toSyntacticType(), t }, sourceAttr(start, index - 1));
			p = new TypePattern.Leaf(t, null, null, null, sourceAttr(start, index - 1));
		}

		if(lookahead() instanceof Token.Identifier) {
			p.var = matchIdentifier().text;
			// now, update source attribute information
			Attribute.Source attr = p.attribute(Attribute.Source.class);
			p.attributes().remove(attr);
			p.attributes().add(sourceAttr(start,index-1));
			// finally, update environment so that other expressions can access
			// this name
			environment.add(p.var);		
		}
		
		// now attempt to parse accompanying constraint
		lookahead = lookahead();
		if(matches(lookahead,"in",Token.sUC_ELEMENTOF)) {
			match("in",Token.sUC_ELEMENTOF);
			Expr source = parseRangeExpression(generics,environment);
			p.source = source;
			// now, update source attribute information
			Attribute.Source attr = p.attribute(Attribute.Source.class);
			p.attributes().remove(attr);
			p.attributes().add(sourceAttr(start,index-1));
		} else if(matches(lookahead,"where")) {
			match("where");
			p.constraint = parseCondition(generics,environment);
			// now, update source attribute information
			Attribute.Source attr = p.attribute(Attribute.Source.class);
			p.attributes().remove(attr);
			p.attributes().add(sourceAttr(start,index-1));			
		}

		
		return p;
	}
	
	protected TypePattern parseTypePatternAtom(HashSet<String> generics, HashSet<String> environment) {						
		checkNotEof();
		int start = index;
		Token token = tokens.get(index);
		SyntacticType t;
		
		if(token.text.equals("any")) {
			match("any");
			t = new SyntacticType.Primitive(SemanticType.Any,sourceAttr(start,index-1));
		} else if(token.text.equals("int")) {
			match("int");			
			t = new SyntacticType.Primitive(SemanticType.Int,sourceAttr(start,index-1));
		} else if(token.text.equals("real")) {
			match("real");		
			t = new SyntacticType.Primitive(SemanticType.Real,sourceAttr(start,index-1));
		} else if(token.text.equals("void")) {
			match("void");
			t = new SyntacticType.Primitive(SemanticType.Void,sourceAttr(start,index-1));
		} else if(token.text.equals("bool")) {
			match("bool");
			t = new SyntacticType.Primitive(SemanticType.Bool,sourceAttr(start,index-1));
		} else if(token.text.equals("string")) {
			match("string");
			t = new SyntacticType.Primitive(SemanticType.String,sourceAttr(start,index-1));
		} else if (matches(token,"(")) {
			match("(");
			// now, quickly check for the empty tuple case.
			if(matches(")")) {
				// empty tuple
				match(")");
				return new TypePattern.Tuple(new TypePattern[0], null, null,
						null, sourceAttr(start, index - 1));
			} else {
				// non-empty tuple
				TypePattern p = parseTypePattern(generics,environment);
				match(")");
				return p;
			}			
		} else if(matches(token,"!")) {
			match("!");
			t = new SyntacticType.Negation(parseSyntacticType(generics),sourceAttr(start,index-1));
		} else if (matches(token,"{")) {		
			match("{");
			t = new SyntacticType.Set(parseSyntacticType(generics),sourceAttr(start,index-1));
			match("}");
		} else if (matches(token,"[")) {		
			match("[");
			t = new SyntacticType.List(parseSyntacticType(generics),sourceAttr(start,index-1));
			match("]");
		} else if(token instanceof Token.Identifier) {
			String id = matchIdentifier().text;
			if(generics.contains(id)) {
				t = new SyntacticType.Variable(id,sourceAttr(start,index-1));
			} else {
				syntaxError("unknown generic type encountered",token);
				return null;
			}
		} else {
			syntaxError("unknown type encountered",token);
			return null; // deadcode
		}		
					
		return new TypePattern.Leaf(t,null,null,null,sourceAttr(start,index-1));
	}
	
	protected void addNamedVariables(TypePattern type, HashSet<String> environment) {

		if (type.var != null) {
			if (environment.contains(type.var)) {
				syntaxError("duplicate variable name encountered", type);
			}
			environment.add(type.var);
		}

		if (type instanceof TypePattern.Tuple) {
			TypePattern.Tuple st = (TypePattern.Tuple) type;
			for (TypePattern t : st.patterns) {
				addNamedVariables(t, environment);
			}
		}
	}
	
	protected void checkNotEof() {
		skipWhiteSpace();
		if (index >= tokens.size()) {
			throw new SyntaxError("unexpected end-of-file", filename,
					index - 1, index - 1);
		}
		return;
	}
	
	protected <T extends Token> T match(Class<T> c) {
		checkNotEof();
		Token t = tokens.get(index);
		if (!c.isInstance(t)) {			
			syntaxError("syntax error" , t);
		}
		index = index + 1;
		return (T) t;
	}
	
	protected Token match(String... matches) {
		checkNotEof();
		Token t = tokens.get(index);
		for (int i = 0; i != matches.length; ++i) {
			if (t.text.equals(matches[i])) {
				index = index + 1;
				return t;
			}
		}
		syntaxError("token " + matches[0] + " expected.", t);
		return null;
	}
	
	protected Token matchAll(Class<? extends Token>... cs) {
		checkNotEof();
		Token t = tokens.get(index);
		for(Class<? extends Token> c : cs) {
			if (c.isInstance(t)) {			
				index = index + 1;
				return t;
			}
		}
		syntaxError("syntax error" , t);
		return null;
	}
	
	protected Token.Identifier matchIdentifier() {
		checkNotEof();
		Token t = tokens.get(index);
		if (t instanceof Token.Identifier) {
			Token.Identifier i = (Token.Identifier) t;
			index = index + 1;
			return i;
		}
		syntaxError("identifier expected", t);
		return null; // unreachable.
	}
			
	protected boolean matches(String... operators) {	
		int i = skipWhiteSpace(index);
		if(i < tokens.size()) {
			return matches(tokens.get(i),operators);
		}
		return false;
	}
	
	protected boolean matches(Token t, String... operators) {
		if(t == null) { return false; }
		for(int i=0;i!=operators.length;++i) {
			if(t.text.equals(operators[i])) {
				return true;
			}
		}
		return false;
	}
	
	protected void matchEndOfLine() {
		int start = index;
		Token lookahead = null;
		while (index < tokens.size()
				&& (lookahead = tokens.get(index)) instanceof Token.Whitespace) {
			index = index + 1;
			if (lookahead instanceof Token.NewLine) {
				return; // done;
			}
		}
		if (index < tokens.size()) {
			syntaxError("expected end-of-line", lookahead);
		} else if (lookahead != null) {
			syntaxError("unexpected end-of-file", lookahead);
		} else {
			// This should always be safe since we only ever call matchEndOfLine
			// after having already matched something.
			syntaxError("unexpected end-of-file", tokens.get(start - 1));
		}
	}
	

	protected int scanIndent() {
		int indent = 0;
		int i = index;
		while (i < tokens.size()
				&& (tokens.get(i) instanceof Token.Spaces || tokens
						.get(i) instanceof Token.Tabs)) {
			Token token = tokens.get(i);
			if(token instanceof Token.Spaces) {
				indent += token.text.length();
			} else {
				indent += token.text.length() * SPACES_PER_TAB;
			}
			i = i + 1;
		}
		return indent;
	}
	
	protected int matchIndent(int indent) {
		while (index < tokens.size()
				&& (tokens.get(index) instanceof Token.Spaces || tokens
						.get(index) instanceof Token.Tabs)) {
			Token token = tokens.get(index);
			if(token instanceof Token.Spaces) {
				indent -= token.text.length();
			} else {
				indent -= token.text.length() * SPACES_PER_TAB;
			}
			index = index + 1;
			if (indent < 0) {
				syntaxError("unexpected level of indentation", token);
			}
		}
		return indent;
	}
		
	protected Token lookahead() {		
		int i = skipWhiteSpace(index);
		if(i < tokens.size()) {
			return tokens.get(i);
		}
		return null;
	}
	
	protected <T extends Token> boolean matches(Class<T> c) {
		int i = skipWhiteSpace(index);		
		if (i < tokens.size()) {
			Token t = tokens.get(i);
			if (c.isInstance(t)) {
				return true;
			}
		}
		return false;
	}

	protected void skipWhiteSpace() {
		index = skipWhiteSpace(index);
	}
	
	protected int skipWhiteSpace(int start) {
		while (start < tokens.size()
				&& (tokens.get(start) instanceof Token.Whitespace || tokens
						.get(start) instanceof Token.Comment)) {
			start = start + 1;
		}
		return start;
	}
	
	protected Attribute.Source sourceAttr(int start, int end) {
		start = skipWhiteSpace(start);
		Token t1 = tokens.get(start);
		Token t2 = tokens.get(end);
		// HACK: should really calculate the line number correctly here.
		return new Attribute.Source(t1.start,t2.end(),0);
	}
	
	protected void syntaxError(String msg, SyntacticElement e) {
		Attribute.Source loc = e.attribute(Attribute.Source.class);
		throw new SyntaxError(msg, filename, loc.start, loc.end);
	}

	protected void syntaxError(String msg, Token t) {
		throw new SyntaxError(msg, filename, t.start, t.start
				+ t.text.length() - 1);
	}
}