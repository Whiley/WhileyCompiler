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

import java.math.BigInteger;
import java.util.*;
import java.io.*;

import wyautl.util.BigRational;
import static wycs.io.Lexer.*;
import wybs.lang.Attribute;
import wybs.lang.SyntaxError;
import wycs.lang.*;

public class Parser {
	private String filename;
	private ArrayList<Token> tokens;		
	private int index;

	public Parser(String filename, List<Token> tokens) {
		this.filename = filename;
		this.tokens = new ArrayList<Token>();
	}
	
	public WycsFile parse() {
		ArrayList<Stmt> decls = new ArrayList<Stmt>();
		
		while (index < tokens.size()) {
			Token t = tokens.get(index);
			if (t instanceof NewLine || t instanceof Comment) {
				matchEndLine();
			} else {
				int start = index;
				Token lookahead = tokens.get(index);
				if (lookahead instanceof Keyword
						&& lookahead.text.equals("assert")) {
					matchKeyword("assert");
					Expr condition = parseCondition();
					// TODO: parse assertion message from input file
					decls.add(Stmt.Assert("assertion failed", condition, sourceAttr(start,
							index - 1)));
				} else if (lookahead instanceof Keyword
						&& lookahead.text.equals("assume")) {
					matchKeyword("assume");
					Expr condition = parseCondition();
					decls.add(Stmt.Assume(condition, sourceAttr(start,
							index - 1)));
				} 
			}
		}
				
		return new WycsFile(null,decls);
	}	
	
	private Expr parseCondition() {
		checkNotEof();
		int start = index;		
		Expr c1 = parseConditionExpression();		
		
		if(index < tokens.size() && tokens.get(index) instanceof LogicalAnd) {			
			match(LogicalAnd.class);
			skipWhiteSpace(true);
			
			Expr c2 = parseCondition();			
			return Expr.Nary(Expr.Nary.Op.AND, new Expr[]{c1, c2}, sourceAttr(start,
					index - 1));
		} else if(index < tokens.size() && tokens.get(index) instanceof LogicalOr) {
			match(LogicalOr.class);
			skipWhiteSpace(true);
			
			Expr c2 = parseCondition();
			return Expr.Nary(Expr.Nary.Op.OR, new Expr[]{c1, c2}, sourceAttr(start,
					index - 1));			
		} 
		return c1;		
	}
		
	private Expr parseConditionExpression() {		
		int start = index;
						
		if (index < tokens.size() && tokens.get(index) instanceof All) {
			match(All.class);
			return parseQuantifier(start,true);			
		} else if (index < tokens.size() && tokens.get(index) instanceof Exists) {
			match(Exists.class);
			return parseQuantifier(start,false);			
		} 
		
		Expr lhs = parseAddSubExpression();
		
		if (index < tokens.size() && tokens.get(index) instanceof LessEquals) {
			match(LessEquals.class);				
			skipWhiteSpace(true);
			
			Expr rhs = parseAddSubExpression();
			return Expr.Binary(Expr.Binary.Op.LTEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof LeftAngle) {
 			match(LeftAngle.class);				
 			skipWhiteSpace(true);
 			
 			Expr rhs = parseAddSubExpression();
			return Expr.Binary(Expr.Binary.Op.LT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof GreaterEquals) {
			match(GreaterEquals.class);	
			skipWhiteSpace(true);			
			Expr rhs = parseAddSubExpression();
			return Expr.Binary(Expr.Binary.Op.GTEQ,  lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof RightAngle) {
			match(RightAngle.class);			
			skipWhiteSpace(true);
			
			Expr rhs = parseAddSubExpression();
			return Expr.Binary(Expr.Binary.Op.GT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof EqualsEquals) {
			match(EqualsEquals.class);			
			skipWhiteSpace(true);
			
			Expr rhs = parseAddSubExpression();
			return Expr.Binary(Expr.Binary.Op.EQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof NotEquals) {
			match(NotEquals.class);			
			skipWhiteSpace(true);
			
			Expr rhs = parseAddSubExpression();			
			return Expr.Binary(Expr.Binary.Op.NEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof ElemOf) {
			match(ElemOf.class);			
			skipWhiteSpace(true);			
			Expr rhs = parseAddSubExpression();			
			return Expr.Binary(Expr.Binary.Op.IN, lhs,  rhs, sourceAttr(start,index-1));
		} else {
			return lhs;
		}	
	}
		
	private Expr parseAddSubExpression() {
		int start = index;
		Expr lhs = parseMulDivExpression();
		
		if (index < tokens.size() && tokens.get(index) instanceof Plus) {
			match(Plus.class);
			skipWhiteSpace(true);
			Expr rhs = parseAddSubExpression();
			return Expr.Binary(Expr.Binary.Op.ADD, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size() && tokens.get(index) instanceof Minus) {
			match(Minus.class);
			skipWhiteSpace(true);
			
			Expr rhs = parseAddSubExpression();
			return Expr.Binary(Expr.Binary.Op.SUB, lhs, rhs, sourceAttr(start,
					index - 1));
		} 
		
		return lhs;
	}
	
	private Expr parseMulDivExpression() {
		int start = index;
		Expr lhs = parseIndexTerm();
		
		if (index < tokens.size() && tokens.get(index) instanceof Star) {
			match(Star.class);
			skipWhiteSpace(true);
			
			Expr rhs = parseMulDivExpression();
			return Expr.Binary(Expr.Binary.Op.MUL, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof RightSlash) {
			match(RightSlash.class);
			skipWhiteSpace(true);
			
			Expr rhs = parseMulDivExpression();
			return Expr.Binary(Expr.Binary.Op.DIV, lhs, rhs, sourceAttr(start,
					index - 1));
		}

		return lhs;
	}	
	
	private Expr parseIndexTerm() {
		checkNotEof();
		int start = index;
		int ostart = index;		
		Expr lhs = parseTerm();
		
		Token lookahead = tokens.get(index);
		
		while (lookahead instanceof LeftSquare
				|| lookahead instanceof LeftBrace || lookahead instanceof Hash) {
			start = index;
			if (lookahead instanceof LeftSquare) {
				match(LeftSquare.class);
				skipWhiteSpace(true);
				Expr rhs = parseAddSubExpression();
				skipWhiteSpace(true);
				match(RightSquare.class);
				lhs = Expr.Binary(Expr.Binary.Op.INDEXOF, lhs, rhs,
						sourceAttr(start, index - 1));
			}
			if (index < tokens.size()) {
				lookahead = tokens.get(index);
			} else {
				lookahead = null;
			}
		}
		
		return lhs;		
	}
		
	private Expr parseTerm() {		
		checkNotEof();		
		
		int start = index;
		Token token = tokens.get(index);		
		
		if(token instanceof LeftBrace) {
			match(LeftBrace.class);
			skipWhiteSpace(true);
			checkNotEof();			
			Expr v = parseCondition();			
			skipWhiteSpace(true);
			checkNotEof();
			token = tokens.get(index);			
			match(RightBrace.class);
			return v;			 		
		} else if (token.text.equals("null")) {
			matchKeyword("null");			
			return Expr.Constant(null,
					sourceAttr(start, index - 1));
		} else if (token.text.equals("true")) {
			matchKeyword("true");			
			return Expr.Constant(Value.Bool(true),
					sourceAttr(start, index - 1));
		} else if (token.text.equals("false")) {	
			matchKeyword("false");
			return Expr.Constant(Value.Bool(false),
					sourceAttr(start, index - 1));			
		} else if (token instanceof Identifier) {
			return Expr.Variable(matchIdentifier().text, sourceAttr(start,
					index - 1));			
		} else if (token instanceof Int) {			
			BigInteger val = match(Int.class).value;
			return Expr.Constant(Value.Integer(val),
					sourceAttr(start, index - 1));
		} else if (token instanceof Real) {
			BigRational val = match(Real.class).value;
			return Expr.Constant(Value.Rational(val),
					sourceAttr(start, index - 1));
		} else if (token instanceof Minus) {
			return parseNegation();
		} else if (token instanceof Shreak) {
			match(Shreak.class);
			return Expr.Unary(Expr.Unary.Op.NOT, parseTerm(), sourceAttr(
					start, index - 1));
		}
		syntaxError("unrecognised term.",token);
		return null;		
	}
	
	private Expr parseQuantifier(int start, boolean forall) {
		match(LeftCurly.class);
		skipWhiteSpace(true);
		//ArrayList<Stmt.Declare> variables = new ArrayList<Stmt.Declare>();
		boolean firstTime = true;
		Token token = tokens.get(index);
		while (!(token instanceof Bar)) {
			if (!firstTime) {
				match(Comma.class);
				skipWhiteSpace(true);
			} else {
				firstTime = false;
			}			
			//Type type = parseType();
			Identifier variable = matchIdentifier();
			//variables.add(Stmt.Declare(variable.text, type));
			skipWhiteSpace(true);
			token = tokens.get(index);
		}
		match(Bar.class);
		Expr condition = parseCondition();
		match(RightCurly.class);

//		if (forall) {
//			return Expr.ForAll(variables, condition, sourceAttr(start,
//					index - 1));
//		} else {
//			return Expr.Exists(variables, condition, sourceAttr(start,
//					index - 1));
//		}
		syntaxError("missing support for parsing quantifiers",token);
		return null;	
	}
		
	private Expr parseNegation() {
		int start = index;
		match(Minus.class);
		skipWhiteSpace(true);
		Expr e = parseIndexTerm();
		
		if (e instanceof Expr.Constant) {
			Expr.Constant c = (Expr.Constant) e;
			if (c.value instanceof Value.Integer) {
				Value.Integer i = (Value.Integer) c.value;
				java.math.BigInteger bi = (BigInteger) i.value;
				return Expr.Constant(Value.Integer(bi
						.negate()), sourceAttr(start, index));
			} else if (c.value instanceof Value.Rational) {
				Value.Rational r = (Value.Rational) c.value;
				BigRational br = (BigRational) r.value;
				return Expr.Constant(Value.Rational(br
						.negate()), sourceAttr(start, index));
			}
		}
		
		return Expr.Unary(Expr.Unary.Op.NEG, e, sourceAttr(start, index));		
	}
	
	private void skipWhiteSpace(boolean includeNewLine) {
		while (index < tokens.size()
				&& isWhiteSpace(includeNewLine, tokens.get(index))) {
			index++;
		}
	}

	private boolean isWhiteSpace(boolean includeNewLine, Token t) {
		return (includeNewLine && t instanceof Lexer.NewLine)
				|| t instanceof Lexer.Comment
				|| t instanceof Lexer.Tabs;
	}
	
	private void checkNotEof() {		
		if (index >= tokens.size()) {
			throw new SyntaxError("unexpected end-of-file", filename,
					index - 1, index - 1);
		}
		return;
	}
	
	private <T extends Token> T match(Class<T> c) {
		checkNotEof();
		Token t = tokens.get(index);
		if (!c.isInstance(t)) {			
			syntaxError("syntax error" , t);
		}
		index = index + 1;
		return (T) t;
	}
	
	private Token matchAll(Class<? extends Token>... cs) {
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
	
	private Identifier matchIdentifier() {
		checkNotEof();
		Token t = tokens.get(index);
		if (t instanceof Identifier) {
			Identifier i = (Identifier) t;
			index = index + 1;
			return i;
		}
		syntaxError("identifier expected", t);
		return null; // unreachable.
	}
	
	private Keyword matchKeyword(String keyword) {
		checkNotEof();
		Token t = tokens.get(index);
		if (t instanceof Keyword) {
			if (t.text.equals(keyword)) {
				index = index + 1;
				return (Keyword) t;
			}
		}
		syntaxError("keyword " + keyword + " expected.", t);
		return null;
	}
	
	private void matchEndLine() {
		while(index < tokens.size()) {
			Token t = tokens.get(index++);			
			if(t instanceof NewLine) {
				break;
			} else if(!(t instanceof Comment) && !(t instanceof Tabs)) {
				syntaxError("syntax error",t);
			}			
		}
	}
	
	private Attribute.Source sourceAttr(int start, int end) {
		Token t1 = tokens.get(start);
		Token t2 = tokens.get(end);
		// HACK: should really calculate the line number correctly here.
		return new Attribute.Source(t1.start,t2.end(),0);
	}
	
	private void syntaxError(String msg, Expr e) {
		Attribute.Source loc = e.attribute(Attribute.Source.class);
		throw new SyntaxError(msg, filename, loc.start, loc.end);
	}

	private void syntaxError(String msg, Token t) {
		throw new SyntaxError(msg, filename, t.start, t.start
				+ t.text.length() - 1);
	}
}