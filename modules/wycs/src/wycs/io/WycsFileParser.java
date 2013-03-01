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
import java.math.BigInteger;
import java.util.*;

import wyautl.util.BigRational;
import static wycs.io.WycsFileLexer.*;
import wybs.lang.Attribute;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.Pair;
import wybs.util.Trie;
import wycs.lang.*;

public class WycsFileParser {
	private String filename;
	private ArrayList<Token> tokens;		
	private int index;

	public WycsFileParser(String filename, List<Token> tokens) {
		this.filename = filename;
		this.tokens = new ArrayList<Token>(tokens);
	}
	
	public WycsFile parse() {
		
		// first, strip out any whitespace
		for(int i=0;i!=tokens.size();) {
			Token lookahead = tokens.get(i);
			if (lookahead instanceof LineComment
				|| lookahead instanceof BlockComment) {
				tokens.remove(i);
			} else {
				i = i + 1;
			}
		}
		
		Path.ID pkg = parsePackage();
		
		String name = filename.substring(filename.lastIndexOf(File.separatorChar) + 1,filename.length()-5);
		WycsFile wf = new WycsFile(pkg.append(name),filename);
		
		while (index < tokens.size()) {
			Token lookahead = tokens.get(index);
			if (lookahead instanceof Keyword && lookahead.text.equals("assert")) {
				parseAssert(wf);
			} else if (lookahead instanceof Keyword
					&& lookahead.text.equals("function")) {
				parseFunctionOrPredicate(false,wf);
			} else if (lookahead instanceof Keyword
					&& lookahead.text.equals("define")) {
				parseFunctionOrPredicate(true,wf);
			} else if (lookahead instanceof Keyword
					&& lookahead.text.equals("import")) {
				parseImport(wf);
			} else {
				syntaxError("unrecognised statement.", lookahead);
				return null;
			}
		}		
		
		return wf;
	}	
	
	private Trie parsePackage() {
		Trie pkg = Trie.ROOT;

		if (index < tokens.size() && tokens.get(index).text.equals("package")) {
			matchKeyword("package");

			pkg = pkg.append(matchIdentifier().text);

			while (index < tokens.size() && tokens.get(index) instanceof Dot) {
				match(Dot.class);
				pkg = pkg.append(matchIdentifier().text);
			}

			return pkg;
		} else {
			return pkg; // no package
		}
	}

	private void parseImport(WycsFile wf) {
		int start = index;
		matchKeyword("import");
		
		// first, check if from is used
		String name = null;
		if ((index + 1) < tokens.size()
				&& tokens.get(index + 1).text.equals("from")) {
			Token t = tokens.get(index);
			if (t.text.equals("*")) {
				match(Star.class);
				name = "*";
			} else {
				name = matchIdentifier().text;
			}
			matchIdentifier();
		}
				
		Trie filter = Trie.ROOT.append(matchIdentifier().text);
		
		while (index < tokens.size()) {
			Token lookahead = tokens.get(index);
			if(lookahead instanceof Dot) {
				match(Dot.class);							
			} else if(lookahead instanceof DotDot) {
				match(DotDot.class);
				filter = filter.append("**");
			} else {
				break;
			}
			
			if(index < tokens.size()) {
				Token t = tokens.get(index);
				if(t.text.equals("*")) {
					match(Star.class);
					filter = filter.append("*");	
				} else {
					filter = filter.append(matchIdentifier().text);
				}
			}
		}
							
		int end = index;		
		
		wf.add(wf.new Import(filter, name, sourceAttr(start, end - 1)));		
	}
	
	private void parseAssert(WycsFile wf) {
		int start = index;
		matchKeyword("assert");
		String msg = null;
		if(index < tokens.size() && tokens.get(index) instanceof WycsFileLexer.Strung) {
			Strung s = match(Strung.class);
			msg = s.string;
		}
		Expr condition = parseTupleExpression(new HashSet<String>(), new HashSet<String>());
		wf.add(wf.new Assert(msg, condition, sourceAttr(start, index - 1)));
	}
	
	private void parseFunctionOrPredicate(boolean predicate, WycsFile wf) {
		int start = index;
		if(predicate) {
			matchKeyword("define");
		} else {
			matchKeyword("function");
		}
		String name = matchIdentifier().text;
		HashSet<String> generics = new HashSet<String>();
		if(index < tokens.size() && tokens.get(index) instanceof LeftAngle) {
			// generic type
			match(LeftAngle.class);
			boolean firstTime=true;
			while(index < tokens.size() && !(tokens.get(index) instanceof RightAngle)) {
				if(!firstTime) {
					match(Comma.class);
				}
				firstTime=false;
				Identifier id = matchIdentifier();
				String generic = id.text;
				if(generics.contains(generic)) {
					syntaxError("duplicate generic variable",id);
				}
				generics.add(generic);
			}
			match(RightAngle.class);
		}
		HashSet<String> environment = new HashSet<String>();
		SyntacticType from = parseSyntacticType(generics,true);
		SyntacticType to = null;
		addNamedVariables(from,environment);	
		if(!predicate) {
			match(RightArrow.class);
			to = parseSyntacticType(generics,true);
			addNamedVariables(to,environment);
		}		
		
		Expr condition = null;
		if(index < tokens.size() && tokens.get(index) instanceof Keyword && tokens.get(index).text.equals("where")) {
			matchKeyword("where");
			condition = parseTupleExpression(generics,environment);
		}
		if (predicate) {
			wf.add(wf.new Define(name, generics, from, condition, sourceAttr(
					start, index - 1)));
		} else {
			wf.add(wf.new Function(name, generics, from, to, condition,
					sourceAttr(start, index - 1)));
		}
	}
	
	private Expr parseTupleExpression(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		Expr e = parseCondition(generics,environment);		
		if (index < tokens.size() && tokens.get(index) instanceof Comma) {
			// this is a tuple constructor
			ArrayList<Expr> exprs = new ArrayList<Expr>();
			exprs.add(e);
			while (index < tokens.size() && tokens.get(index) instanceof Comma) {
				match(Comma.class);
				exprs.add(parseCondition(generics,environment));
				checkNotEof();
			}
			return new Expr.Nary(Expr.Nary.Op.TUPLE,exprs,sourceAttr(start,index-1));
		} else {
			return e;
		}
	}
	
	private Expr parseCondition(HashSet<String> generics, HashSet<String> environment) {
		checkNotEof();
		int start = index;		
		Expr c1 = parseAndOrCondition(generics,environment);				
		if(index < tokens.size() && tokens.get(index) instanceof LongRightArrow) {			
			match(LongRightArrow.class);
			
			
			Expr c2 = parseCondition(generics,environment);			
			return Expr.Binary(Expr.Binary.Op.IMPLIES, c1, c2, sourceAttr(start,
					index - 1));
		} else if(index < tokens.size() && tokens.get(index) instanceof LongLeftRightArrow) {			
			match(LongLeftRightArrow.class);
			
			
			Expr c2 = parseCondition(generics,environment);			
			return Expr.Binary(Expr.Binary.Op.IFF, c1, c2, sourceAttr(start,
					index - 1));
		}
		
		return c1;
	}
	
	private Expr parseAndOrCondition(HashSet<String> generics, HashSet<String> environment) {
		checkNotEof();
		int start = index;		
		Expr c1 = parseConditionExpression(generics,environment);		

		if(index < tokens.size() && tokens.get(index) instanceof LogicalAnd) {			
			match(LogicalAnd.class);
			Expr c2 = parseAndOrCondition(generics,environment);			
			return Expr.Nary(Expr.Nary.Op.AND, new Expr[]{c1, c2}, sourceAttr(start,
					index - 1));
		} else if(index < tokens.size() && tokens.get(index) instanceof LogicalOr) {
			match(LogicalOr.class);
			Expr c2 = parseAndOrCondition(generics,environment);
			return Expr.Nary(Expr.Nary.Op.OR, new Expr[]{c1, c2}, sourceAttr(start,
					index - 1));			
		} 
		return c1;		
	}
		
	private Expr parseConditionExpression(HashSet<String> generics, HashSet<String> environment) {		
		int start = index;
						
		if (index < tokens.size() && tokens.get(index) instanceof ForAll) {
			match(ForAll.class);
			return parseQuantifier(start,true,generics,environment);			
		} else if (index < tokens.size() && tokens.get(index) instanceof Exists) {
			match(Exists.class);
			return parseQuantifier(start,false,generics,environment);			
		} 
		
		Expr lhs = parseAddSubExpression(generics,environment);
		
		if (index < tokens.size() && tokens.get(index) instanceof LessEquals) {
			match(LessEquals.class);				
			
			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.LTEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof LeftAngle) {
 			match(LeftAngle.class);				
 			
 			
 			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.LT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof GreaterEquals) {
			match(GreaterEquals.class);	
						
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.GTEQ,  lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof RightAngle) {
			match(RightAngle.class);			
			
			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.GT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof EqualsEquals) {
			match(EqualsEquals.class);			
			
			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.EQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof NotEquals) {
			match(NotEquals.class);			
			
			
			Expr rhs = parseAddSubExpression(generics,environment);			
			return Expr.Binary(Expr.Binary.Op.NEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof ElemOf) {
			match(ElemOf.class);			
						
			Expr rhs = parseAddSubExpression(generics,environment);			
			return Expr.Binary(Expr.Binary.Op.IN, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WycsFileLexer.SubsetEquals) {
			match(WycsFileLexer.SubsetEquals.class);									
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.SUBSETEQ, lhs, rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WycsFileLexer.Subset) {
			match(WycsFileLexer.Subset.class);									
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.SUBSET, lhs,  rhs, sourceAttr(start,index-1));
		} else {
			return lhs;
		}	
	}
		
	private Expr parseAddSubExpression(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		Expr lhs = parseMulDivExpression(generics,environment);
		
		if (index < tokens.size() && tokens.get(index) instanceof Plus) {
			match(Plus.class);
			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.ADD, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size() && tokens.get(index) instanceof Minus) {
			match(Minus.class);
			
			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.SUB, lhs, rhs, sourceAttr(start,
					index - 1));
		} 
		
		return lhs;
	}
	
	private Expr parseMulDivExpression(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		Expr lhs = parseIndexTerm(generics,environment);
		
		if (index < tokens.size() && tokens.get(index) instanceof Star) {
			match(Star.class);
			
			
			Expr rhs = parseMulDivExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.MUL, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof RightSlash) {
			match(RightSlash.class);
			
			
			Expr rhs = parseMulDivExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.DIV, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof Percent) {
			match(Percent.class);
			
			
			Expr rhs = parseMulDivExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.REM, lhs, rhs, sourceAttr(start,
					index - 1));
		}

		return lhs;
	}	
	
	private Expr parseIndexTerm(HashSet<String> generics, HashSet<String> environment) {
		checkNotEof();
		int start = index;
		int ostart = index;		
		Expr lhs = parseTerm(generics,environment);

//		if(index < tokens.size()) {
//			Token lookahead = tokens.get(index);
//
//			while (lookahead instanceof LeftSquare) {
//				start = index;
//				if (lookahead instanceof LeftSquare) {
//					match(LeftSquare.class);
//
//					Expr rhs = parseAddSubExpression(generics,environment);
//
//					match(RightSquare.class);
//					lhs = Expr.Binary(Expr.Binary.Op.INDEXOF, lhs, rhs,
//							sourceAttr(start, index - 1));
//				}
//				if (index < tokens.size()) {
//					lookahead = tokens.get(index);
//				} else {
//					lookahead = null;
//				}
//			}
//		}
		
		return lhs;		
	}
		
	private Expr parseTerm(HashSet<String> generics, HashSet<String> environment) {		
		checkNotEof();		
		
		int start = index;
		Token token = tokens.get(index);		
		
		if(token instanceof LeftBrace) {
			match(LeftBrace.class);
			
			checkNotEof();			
			Expr v = parseTupleExpression(generics,environment);						
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
			return parseVariableOrFunCall(generics,environment);
		} else if (token instanceof Int) {			
			BigInteger val = match(Int.class).value;
			return Expr.Constant(Value.Integer(val),
					sourceAttr(start, index - 1));
		} else if (token instanceof Real) {
			BigRational val = match(Real.class).value;
			return Expr.Constant(Value.Rational(val),
					sourceAttr(start, index - 1));
		} else if (token instanceof Minus) {
			return parseNegation(generics,environment);
		} else if (token instanceof Bar) {
			return parseLengthOf(generics,environment);
		} else if (token instanceof Shreak) {
			match(Shreak.class);
			return Expr.Unary(Expr.Unary.Op.NOT, parseTerm(generics,environment), sourceAttr(
					start, index - 1));
		} else if (token instanceof LeftCurly) {
			return parseSet(generics,environment);
		} else if (token instanceof LeftSquare) {
			return parseList(generics,environment);
		} 
		syntaxError("unrecognised term.",token);
		return null;		
	}
	
	private Expr parseLengthOf(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match(Bar.class);
		
		Expr e = parseIndexTerm(generics,environment);
		
		match(Bar.class);
		return Expr.Unary(Expr.Unary.Op.LENGTHOF,e, sourceAttr(start, index - 1));
	}
	
	private Expr parseSet(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match(LeftCurly.class);
		ArrayList<Expr> elements = new ArrayList<Expr>();
		boolean firstTime=true;
		while(index < tokens.size() && !(tokens.get(index) instanceof RightCurly)) {
			if(!firstTime) {
				match(Comma.class);
			}
			firstTime=false;
			elements.add(parseCondition(generics,environment));
		}
		match(RightCurly.class);
		return Expr.Nary(Expr.Nary.Op.SET, elements, sourceAttr(start, index - 1));
	}
	
	private Expr parseList(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match(LeftSquare.class);
		ArrayList<Expr> elements = new ArrayList<Expr>();
		boolean firstTime=true;
		int i = 0;
		while (index < tokens.size()
				&& !(tokens.get(index) instanceof RightSquare)) {
			if (!firstTime) {
				match(Comma.class);
			}
			firstTime = false;
			Expr rhs = parseCondition(generics, environment);
			Expr lhs = Expr.Constant(Value.Integer(BigInteger.valueOf(i++)),
					sourceAttr(start, index - 1));			
			Expr pair = new Expr.Nary(Expr.Nary.Op.TUPLE,
					new Expr[] { lhs, rhs }, sourceAttr(start, index - 1));
			elements.add(pair);
		}
		match(RightSquare.class);
		return Expr.Nary(Expr.Nary.Op.SET, elements, sourceAttr(start, index - 1));
	}
	
	private Expr parseVariableOrFunCall(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		String name = matchIdentifier().text;
		if(!environment.contains(name)) {
			ArrayList<SyntacticType> genericArguments = new ArrayList<SyntacticType>();
			if(tokens.get(index) instanceof LeftAngle) {
				match(LeftAngle.class);
				boolean firstTime=true;
				while(index < tokens.size() && !(tokens.get(index) instanceof RightAngle)) {
					if(!firstTime) {
						match(Comma.class);
					}
					firstTime=false;
					genericArguments.add(parseSyntacticType(generics,false));
				}
				match(RightAngle.class);
			} 
			match(LeftBrace.class);
			Expr argument = parseTupleExpression(generics,environment);
			match(RightBrace.class);
			return Expr.FunCall(name, genericArguments
					.toArray(new SyntacticType[genericArguments.size()]),
					argument, sourceAttr(start, index - 1));
		} else {
			return Expr.Variable(name, sourceAttr(start, index - 1));
		}
	}
	
	private Expr parseQuantifier(int start, boolean forall, HashSet<String> generics, HashSet<String> environment) {
		match(LeftSquare.class);
		environment = new HashSet<String>(environment);
		ArrayList<SyntacticType> unboundedVariables = new ArrayList<SyntacticType>();
		boolean firstTime = true;
		Token token = tokens.get(index);
		while (!(token instanceof Colon) && !(token instanceof SemiColon)) {
			if (!firstTime) {
				match(Comma.class);				
			} else {
				firstTime = false;
			}			
			SyntacticType type = parseSyntacticType(generics,false);
			addNamedVariables(type,environment);
			unboundedVariables.add(type);
			
			token = tokens.get(index);
		}
		ArrayList<Pair<String,Expr>> boundedVariables = new ArrayList<Pair<String,Expr>>();
		if(token instanceof SemiColon) {
			match(SemiColon.class);
			firstTime = true;
			while (!(token instanceof Colon)) {
				if (!firstTime) {
					match(Comma.class);					
				} else {
					firstTime = false;
				}			
				String name = matchIdentifier().text;
				match(ElemOf.class);
				Expr expr = parseCondition(generics,environment);
				boundedVariables.add(new Pair<String,Expr>(name,expr));
				environment.add(name);
				token = tokens.get(index);
			}
		}
		match(Colon.class);
		Expr condition = parseCondition(generics,environment);
		match(RightSquare.class);

		if (forall) {
			return Expr.ForAll(unboundedVariables, boundedVariables, condition, sourceAttr(start,
					index - 1));
		} else {
			return Expr.Exists(unboundedVariables, boundedVariables, condition, sourceAttr(start,
					index - 1));
		}
	}
		
	private Expr parseNegation(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match(Minus.class);
		
		Expr e = parseIndexTerm(generics,environment);
		
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
	
	private SyntacticType parseSyntacticType(HashSet<String> generics, boolean topLevelTuples) {				
		
		checkNotEof();
		int start = index;
		Token token = tokens.get(index);
		SyntacticType t;
		
		if(token.text.equals("any")) {
			matchKeyword("any");
			t = new SyntacticType.Primitive(null,SemanticType.Any,sourceAttr(start,index-1));
		} else if(token.text.equals("int")) {
			matchKeyword("int");			
			t = new SyntacticType.Primitive(null,SemanticType.Int,sourceAttr(start,index-1));
		} else if(token.text.equals("real")) {
			matchKeyword("real");		
			t = new SyntacticType.Primitive(null,SemanticType.Real,sourceAttr(start,index-1));
		} else if(token.text.equals("void")) {
			matchKeyword("void");
			t = new SyntacticType.Primitive(null,SemanticType.Void,sourceAttr(start,index-1));
		} else if(token.text.equals("bool")) {
			matchKeyword("bool");
			t = new SyntacticType.Primitive(null,SemanticType.Bool,sourceAttr(start,index-1));
		} else if (token instanceof LeftBrace) {
			match(LeftBrace.class);
			t = parseSyntacticType(generics,true);
			match(RightBrace.class);
		} else if(token instanceof Shreak) {
			match(Shreak.class);
			t = new SyntacticType.Not(null,parseSyntacticType(generics,false),sourceAttr(start,index-1));
		} else if (token instanceof LeftCurly) {		
			match(LeftCurly.class);
			t = new SyntacticType.Set(null,parseSyntacticType(generics,true),sourceAttr(start,index-1));
			match(RightCurly.class);
		} else if(token instanceof Identifier) {
			String id = matchIdentifier().text;
			if(generics.contains(id)) {
				t = new SyntacticType.Var(null,id,sourceAttr(start,index-1));
			} else {
				syntaxError("unknown generic type encountered",token);
				return null;
			}
		} else {
			syntaxError("unknown type encountered",token);
			return null; // deadcode
		}
		
		
		if(index < tokens.size() && tokens.get(index) instanceof Identifier) {
			t.name = matchIdentifier().text;
		}
		
		if (topLevelTuples && index < tokens.size() && tokens.get(index) instanceof Comma) {
			// indicates a tuple
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			types.add(t);
			while (index < tokens.size() && tokens.get(index) instanceof Comma) {
				match(Comma.class);
				types.add(parseSyntacticType(generics,false));
			}
			t = new SyntacticType.Tuple(null,types,sourceAttr(start,index-1));
		}		
		
		return t;
	}	
	
	private void addNamedVariables(SyntacticType type,
			HashSet<String> environment) {
		
		if(type.name != null) {
			if(environment.contains(type.name)) {
				syntaxError("duplicate variable name encountered",type);
			}
			environment.add(type.name);
		}
		
		if(type instanceof SyntacticType.And) {
			// Don't go further here, since we currently can't use the names.
		} else if(type instanceof SyntacticType.Or) {
			// Don't go further here, since we currently can't use the names.
		} else if(type instanceof SyntacticType.Not) {
			SyntacticType.Not st = (SyntacticType.Not) type;			
			addNamedVariables(st.element,environment);
		} else if(type instanceof SyntacticType.Set) {
			// Don't go further here, since we currently can't use the names.
		} else if(type instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple st = (SyntacticType.Tuple) type;
			for(SyntacticType t : st.elements) {
				addNamedVariables(t,environment);
			}
		}
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
	
	private Attribute.Source sourceAttr(int start, int end) {
		Token t1 = tokens.get(start);
		Token t2 = tokens.get(end);
		// HACK: should really calculate the line number correctly here.
		return new Attribute.Source(t1.start,t2.end(),0);
	}
	
	private void syntaxError(String msg, SyntacticElement e) {
		Attribute.Source loc = e.attribute(Attribute.Source.class);
		throw new SyntaxError(msg, filename, loc.start, loc.end);
	}

	private void syntaxError(String msg, Token t) {
		throw new SyntaxError(msg, filename, t.start, t.start
				+ t.text.length() - 1);
	}
}