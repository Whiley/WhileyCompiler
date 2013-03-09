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
import wybs.lang.Attribute;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.AbstractFileLexer;
import wybs.util.Pair;
import wybs.util.Trie;
import wycs.lang.*;

public class WycsFileParser {
	private String filename;
	private ArrayList<AbstractFileLexer.Token> tokens;		
	private int index;

	public WycsFileParser(String filename, List<AbstractFileLexer.Token> tokens) {
		this.filename = filename;
		this.tokens = new ArrayList<AbstractFileLexer.Token>(tokens);
	}
	
	public WycsFile parse() {
		
		// first, strip out any whitespace
		for(int i=0;i!=tokens.size();) {
			AbstractFileLexer.Token lookahead = tokens.get(i);
			if (lookahead instanceof AbstractFileLexer.LineComment
				|| lookahead instanceof AbstractFileLexer.BlockComment) {
				tokens.remove(i);
			} else {
				i = i + 1;
			}
		}
		
		Path.ID pkg = parsePackage();
		
		String name = filename.substring(filename.lastIndexOf(File.separatorChar) + 1,filename.length()-5);
		WycsFile wf = new WycsFile(pkg.append(name),filename);
		
		while (index < tokens.size()) {
			AbstractFileLexer.Token lookahead = tokens.get(index);
			if (lookahead instanceof AbstractFileLexer.Keyword
					&& lookahead.text.equals("assert")) {
				parseAssert(wf);
			} else if (lookahead instanceof AbstractFileLexer.Keyword
					&& lookahead.text.equals("function")) {
				parseFunctionOrMacro(false, wf);
			} else if (lookahead instanceof AbstractFileLexer.Keyword
					&& lookahead.text.equals("define")) {
				parseFunctionOrMacro(true, wf);
			} else if (lookahead instanceof AbstractFileLexer.Keyword
					&& lookahead.text.equals("import")) {
				parseImport(wf);
			} else {
				syntaxError("unrecognised statement (" + lookahead.text + ")",
						lookahead);
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

			while (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Dot) {
				match(AbstractFileLexer.Dot.class);
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
			AbstractFileLexer.Token t = tokens.get(index);
			if (t.text.equals("*")) {
				match(AbstractFileLexer.Star.class);
				name = "*";
			} else {
				name = matchIdentifier().text;
			}
			matchIdentifier();
		}
				
		Trie filter = Trie.ROOT.append(matchIdentifier().text);
		
		while (index < tokens.size()) {
			AbstractFileLexer.Token lookahead = tokens.get(index);
			if(lookahead instanceof AbstractFileLexer.Dot) {
				match(AbstractFileLexer.Dot.class);							
			} else if(lookahead instanceof AbstractFileLexer.DotDot) {
				match(AbstractFileLexer.DotDot.class);
				filter = filter.append("**");
			} else {
				break;
			}
			
			if(index < tokens.size()) {
				AbstractFileLexer.Token t = tokens.get(index);
				if(t.text.equals("*")) {
					match(AbstractFileLexer.Star.class);
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
		if (index < tokens.size()
				&& tokens.get(index) instanceof AbstractFileLexer.String) {
			AbstractFileLexer.String s = match(AbstractFileLexer.String.class);
			msg = s.text.substring(1,s.text.length()-1);
		}		
		Expr condition = parseTupleExpression(new HashSet<String>(), new HashSet<String>());
		wf.add(wf.new Assert(msg, condition, sourceAttr(start, index - 1)));
	}
	
	private void parseFunctionOrMacro(boolean predicate, WycsFile wf) {
		int start = index;
		if(predicate) {
			matchKeyword("define");
		} else {
			matchKeyword("function");
		}
		String name = matchIdentifier().text;
		ArrayList<String> generics = new ArrayList<String>();
		if(index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.LeftAngle) {
			// generic type
			match(AbstractFileLexer.LeftAngle.class);
			boolean firstTime=true;
			while(index < tokens.size() && !(tokens.get(index) instanceof AbstractFileLexer.RightAngle)) {
				if(!firstTime) {
					match(AbstractFileLexer.Comma.class);
				}
				firstTime=false;
				AbstractFileLexer.Identifier id = matchIdentifier();
				String generic = id.text;
				if(generics.contains(generic)) {
					syntaxError("duplicate generic variable",id);
				}
				generics.add(generic);
			}
			match(AbstractFileLexer.RightAngle.class);
		}
		HashSet<String> environment = new HashSet<String>();
		HashSet<String> genericSet = new HashSet<String>(generics);
		TypePattern from = parseTypePattern(genericSet);
		TypePattern to = null;
		addNamedVariables(from,environment);	
		if(!predicate) {
			match(AbstractFileLexer.RightArrow.class);
			to = parseTypePattern(genericSet);
			addNamedVariables(to,environment);
		}		
		
		Expr condition = null;
		if (index < tokens.size()
				&& tokens.get(index) instanceof AbstractFileLexer.Keyword
				&& tokens.get(index).text.equals("where")) {
			matchKeyword("where");
			condition = parseTupleExpression(genericSet,environment);
		}
		if (predicate) {
			wf.add(wf.new Define(name, generics, from, condition, sourceAttr(
					start, index - 1)));
		} else {
			wf.add(wf.new Function(name, generics, from, to, condition,
					sourceAttr(start, index - 1)));
		}
	}
	
	private Expr parseTupleExpression(HashSet<String> generics,
			HashSet<String> environment) {
		int start = index;
		Expr e = parseCondition(generics, environment);
		if (index < tokens.size()
				&& tokens.get(index) instanceof AbstractFileLexer.Comma) {
			// this is a tuple constructor
			ArrayList<Expr> exprs = new ArrayList<Expr>();
			exprs.add(e);
			while (index < tokens.size()
					&& tokens.get(index) instanceof AbstractFileLexer.Comma) {
				match(AbstractFileLexer.Comma.class);
				exprs.add(parseCondition(generics, environment));
				checkNotEof();
			}
			return new Expr.Nary(Expr.Nary.Op.TUPLE, exprs, sourceAttr(start,
					index - 1));
		} else {
			return e;
		}
	}
	
	private Expr parseCondition(HashSet<String> generics, HashSet<String> environment) {
		checkNotEof();
		int start = index;		
		Expr c1 = parseAndOrCondition(generics,environment);				
		if(index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.LongRightDoubleArrow) {			
			match(AbstractFileLexer.LongRightDoubleArrow.class);
			
			
			Expr c2 = parseCondition(generics,environment);			
			return Expr.Binary(Expr.Binary.Op.IMPLIES, c1, c2, sourceAttr(start,
					index - 1));
		} else if(index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.LongLeftRightDoubleArrow) {			
			match(AbstractFileLexer.LongLeftRightDoubleArrow.class);
			
			
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

		if(index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.AmpersandAmpersand) {			
			match(AbstractFileLexer.AmpersandAmpersand.class);
			Expr c2 = parseAndOrCondition(generics,environment);			
			return Expr.Nary(Expr.Nary.Op.AND, new Expr[]{c1, c2}, sourceAttr(start,
					index - 1));
		} else if(index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.BarBar) {
			match(AbstractFileLexer.BarBar.class);
			Expr c2 = parseAndOrCondition(generics,environment);
			return Expr.Nary(Expr.Nary.Op.OR, new Expr[]{c1, c2}, sourceAttr(start,
					index - 1));			
		} 
		return c1;		
	}
		
	private Expr parseConditionExpression(HashSet<String> generics, HashSet<String> environment) {		
		int start = index;
						
		if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.ForAll) {
			match(AbstractFileLexer.ForAll.class);
			return parseQuantifier(start,true,generics,environment);			
		} else if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Exists) {
			match(AbstractFileLexer.Exists.class);
			return parseQuantifier(start,false,generics,environment);			
		} 
		
		Expr lhs = parseAddSubExpression(generics,environment);
		
		if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.LessEquals) {
			match(AbstractFileLexer.LessEquals.class);				
			
			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.LTEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.LeftAngle) {
 			match(AbstractFileLexer.LeftAngle.class);				
 			
 			
 			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.LT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.GreaterEquals) {
			match(AbstractFileLexer.GreaterEquals.class);	
						
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.GTEQ,  lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.RightAngle) {
			match(AbstractFileLexer.RightAngle.class);			
			
			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.GT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.EqualsEquals) {
			match(AbstractFileLexer.EqualsEquals.class);			
			
			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.EQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.NotEquals) {
			match(AbstractFileLexer.NotEquals.class);			
			
			
			Expr rhs = parseAddSubExpression(generics,environment);			
			return Expr.Binary(Expr.Binary.Op.NEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.ElemOf) {
			match(AbstractFileLexer.ElemOf.class);			
						
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
		
		if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Plus) {
			match(AbstractFileLexer.Plus.class);
			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.ADD, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Minus) {
			match(AbstractFileLexer.Minus.class);
			
			
			Expr rhs = parseAddSubExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.SUB, lhs, rhs, sourceAttr(start,
					index - 1));
		} 
		
		return lhs;
	}
	
	private Expr parseMulDivExpression(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		Expr lhs = parseIndexTerm(generics,environment);
		
		if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Star) {
			match(Star.class);
			
			
			Expr rhs = parseMulDivExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.MUL, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof AbstractFileLexer.RightSlash) {
			match(AbstractFileLexer.RightSlash.class);
			
			
			Expr rhs = parseMulDivExpression(generics,environment);
			return Expr.Binary(Expr.Binary.Op.DIV, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof AbstractFileLexer.Percent) {
			match(AbstractFileLexer.Percent.class);
			
			
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

		if(index < tokens.size()) {
			AbstractFileLexer.Token lookahead = tokens.get(index);

			while (lookahead instanceof AbstractFileLexer.LeftSquare) {
				start = index;
				if (lookahead instanceof AbstractFileLexer.LeftSquare) {
					match(AbstractFileLexer.LeftSquare.class);

					BigInteger rhs = match(AbstractFileLexer.Int.class).value;

					match(AbstractFileLexer.RightSquare.class);
					lhs = Expr.TupleLoad(lhs, rhs.intValue(), sourceAttr(start, index - 1));
				}
				if (index < tokens.size()) {
					lookahead = tokens.get(index);
				} else {
					lookahead = null;
				}
			}
		}
		
		return lhs;		
	}
		
	private Expr parseTerm(HashSet<String> generics, HashSet<String> environment) {		
		checkNotEof();		
		
		int start = index;
		AbstractFileLexer.Token token = tokens.get(index);		
		
		if(token instanceof AbstractFileLexer.LeftBrace) {
			match(AbstractFileLexer.LeftBrace.class);
			
			checkNotEof();			
			Expr v = parseTupleExpression(generics,environment);						
			checkNotEof();
			token = tokens.get(index);			
			match(AbstractFileLexer.RightBrace.class);
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
		} else if (token instanceof AbstractFileLexer.Identifier) {
			return parseVariableOrFunCall(generics,environment);
		} else if (token instanceof Int) {			
			return parseInt(generics,environment);			
		} else if (token instanceof Real) {
			BigRational val = match(Real.class).value;
			return Expr.Constant(Value.Rational(val),
					sourceAttr(start, index - 1));
		} else if (token instanceof AbstractFileLexer.Minus) {
			return parseNegation(generics,environment);
		} else if (token instanceof AbstractFileLexer.Bar) {
			return parseLengthOf(generics,environment);
		} else if (token instanceof AbstractFileLexer.Shreak) {
			match(AbstractFileLexer.Shreak.class);
			return Expr.Unary(Expr.Unary.Op.NOT, parseTerm(generics,environment), sourceAttr(
					start, index - 1));
		} else if (token instanceof AbstractFileLexer.LeftCurly) {
			return parseSet(generics,environment);
		} else if (token instanceof AbstractFileLexer.LeftSquare) {
			return parseList(generics,environment);
		} 
		syntaxError("unrecognised term.",token);
		return null;		
	}
	
	private Expr.Constant parseInt(HashSet<String> generics, HashSet<String> environment)  {
		int start = index;
		BigInteger val = match(Int.class).value;
		return Expr.Constant(Value.Integer(val),
				sourceAttr(start, index - 1));
	}
	
	private Expr parseLengthOf(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match(AbstractFileLexer.Bar.class);
		
		Expr e = parseIndexTerm(generics,environment);
		
		match(AbstractFileLexer.Bar.class);
		return Expr.Unary(Expr.Unary.Op.LENGTHOF,e, sourceAttr(start, index - 1));
	}
	
	private Expr parseSet(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match(AbstractFileLexer.LeftCurly.class);
		ArrayList<Expr> elements = new ArrayList<Expr>();
		boolean firstTime=true;
		while(index < tokens.size() && !(tokens.get(index) instanceof AbstractFileLexer.RightCurly)) {
			if(!firstTime) {
				match(Comma.class);
			}
			firstTime=false;
			elements.add(parseCondition(generics,environment));
		}
		match(AbstractFileLexer.RightCurly.class);
		return Expr.Nary(Expr.Nary.Op.SET, elements, sourceAttr(start, index - 1));
	}
	
	private Expr parseList(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match(AbstractFileLexer.LeftSquare.class);
		ArrayList<Expr> elements = new ArrayList<Expr>();
		boolean firstTime=true;
		int i = 0;
		while (index < tokens.size()
				&& !(tokens.get(index) instanceof AbstractFileLexer.RightSquare)) {
			if (!firstTime) {
				match(AbstractFileLexer.Comma.class);
			}
			firstTime = false;
			Expr rhs = parseCondition(generics, environment);
			Expr lhs = Expr.Constant(Value.Integer(BigInteger.valueOf(i++)),
					sourceAttr(start, index - 1));			
			Expr pair = new Expr.Nary(Expr.Nary.Op.TUPLE,
					new Expr[] { lhs, rhs }, sourceAttr(start, index - 1));
			elements.add(pair);
		}
		match(AbstractFileLexer.RightSquare.class);
		return Expr.Nary(Expr.Nary.Op.SET, elements, sourceAttr(start, index - 1));
	}
	
	private Expr parseVariableOrFunCall(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		String name = matchIdentifier().text;
		if(!environment.contains(name)) {
			ArrayList<SyntacticType> genericArguments = new ArrayList<SyntacticType>();
			if(tokens.get(index) instanceof AbstractFileLexer.LeftAngle) {
				match(AbstractFileLexer.LeftAngle.class);
				boolean firstTime=true;
				while(index < tokens.size() && !(tokens.get(index) instanceof AbstractFileLexer.RightAngle)) {
					if(!firstTime) {
						match(AbstractFileLexer.Comma.class);
					}
					firstTime=false;
					genericArguments.add(parseSyntacticTypeUnionOrIntersection(generics));
				}
				match(AbstractFileLexer.RightAngle.class);
			} 
			match(AbstractFileLexer.LeftBrace.class);
			Expr argument = parseTupleExpression(generics,environment);
			match(AbstractFileLexer.RightBrace.class);
			return Expr.FunCall(name, genericArguments
					.toArray(new SyntacticType[genericArguments.size()]),
					argument, sourceAttr(start, index - 1));
		} else {
			return Expr.Variable(name, sourceAttr(start, index - 1));
		}
	}
	
	private Expr parseQuantifier(int start, boolean forall, HashSet<String> generics, HashSet<String> environment) {
		match(AbstractFileLexer.LeftSquare.class);
		environment = new HashSet<String>(environment);
		ArrayList<TypePattern> unboundedVariables = new ArrayList<TypePattern>();
		boolean firstTime = true;
		AbstractFileLexer.Token token = tokens.get(index);
		ArrayList<Pair<TypePattern,Expr>> variables = new ArrayList<Pair<TypePattern,Expr>>();
		firstTime = true;
		while (!(token instanceof AbstractFileLexer.Colon)) {
			if (!firstTime) {
				match(AbstractFileLexer.Comma.class);					
			} else {
				firstTime = false;
			}			
			TypePattern pattern = parseTypePatternUnionOrIntersection(generics);
			Expr src = null;
			if(index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.ElemOf) {
				match(AbstractFileLexer.ElemOf.class);
				src = parseCondition(generics,environment);				
			}	
			addNamedVariables(pattern,environment);
			variables.add(new Pair<TypePattern,Expr>(pattern,src));
			token = tokens.get(index);
		}
		match(AbstractFileLexer.Colon.class);
		Expr condition = parseCondition(generics,environment);
		match(AbstractFileLexer.RightSquare.class);

		Pair<TypePattern,Expr>[] bounded = variables.toArray(new Pair[variables.size()]);
		
		if (forall) {
			return Expr.ForAll(bounded, condition, sourceAttr(start,
					index - 1));
		} else {
			return Expr.Exists(bounded, condition, sourceAttr(start,
					index - 1));
		}
	}
		
	private Expr parseNegation(HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		match(AbstractFileLexer.Minus.class);
		
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
	
	private SyntacticType parseSyntacticType(HashSet<String> generics) {
		int start = index;
		SyntacticType t = parseSyntacticTypeUnionOrIntersection(generics);
		
		if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Comma) {
			// indicates a tuple
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			types.add(t);
			while (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Comma) {
				match(AbstractFileLexer.Comma.class);
				types.add(parseSyntacticTypeUnionOrIntersection(generics));
			}
			t = new SyntacticType.Tuple(types.toArray(new SyntacticType[types
					.size()]), sourceAttr(start, index - 1));
		}	
		
		return t;
	}
	
	private SyntacticType parseSyntacticTypeUnionOrIntersection(HashSet<String> generics) {
		int start = index;
		SyntacticType t1 = parseSyntacticTypeAtom(generics);

		if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Bar) {
			match(AbstractFileLexer.Bar.class);
			SyntacticType t2 = parseSyntacticTypeUnionOrIntersection(generics);
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			types.add(t1);
			types.add(t2);
			t1 = new SyntacticType.Or(types.toArray(new SyntacticType[types
					.size()]), sourceAttr(start, index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof AbstractFileLexer.BitwiseAnd) {
			match(AbstractFileLexer.BitwiseAnd.class);
			SyntacticType t2 = parseSyntacticTypeUnionOrIntersection(generics);
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			types.add(t1);
			types.add(t2);
			t1 = new SyntacticType.And(types.toArray(new SyntacticType[types
					.size()]), sourceAttr(start, index - 1));
		}

		return t1;
	}
	
	private SyntacticType parseSyntacticTypeAtom(HashSet<String> generics) {				
		
		checkNotEof();
		int start = index;
		AbstractFileLexer.Token token = tokens.get(index);
		SyntacticType t;
		
		if(token.text.equals("any")) {
			matchKeyword("any");
			t = new SyntacticType.Primitive(SemanticType.Any,sourceAttr(start,index-1));
		} else if(token.text.equals("int")) {
			matchKeyword("int");			
			t = new SyntacticType.Primitive(SemanticType.Int,sourceAttr(start,index-1));
		} else if(token.text.equals("real")) {
			matchKeyword("real");		
			t = new SyntacticType.Primitive(SemanticType.Real,sourceAttr(start,index-1));
		} else if(token.text.equals("void")) {
			matchKeyword("void");
			t = new SyntacticType.Primitive(SemanticType.Void,sourceAttr(start,index-1));
		} else if(token.text.equals("bool")) {
			matchKeyword("bool");
			t = new SyntacticType.Primitive(SemanticType.Bool,sourceAttr(start,index-1));
		} else if (token instanceof AbstractFileLexer.LeftBrace) {
			match(AbstractFileLexer.LeftBrace.class);
			t = parseSyntacticType(generics);
			match(AbstractFileLexer.RightBrace.class);
		} else if(token instanceof AbstractFileLexer.Shreak) {
			match(AbstractFileLexer.Shreak.class);
			t = new SyntacticType.Not(parseSyntacticType(generics),sourceAttr(start,index-1));
		} else if (token instanceof AbstractFileLexer.LeftCurly) {		
			match(AbstractFileLexer.LeftCurly.class);
			t = new SyntacticType.Set(parseSyntacticType(generics),sourceAttr(start,index-1));
			match(AbstractFileLexer.RightCurly.class);
		} else if(token instanceof AbstractFileLexer.Identifier) {
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
	
	private TypePattern parseTypePattern(HashSet<String> generics) {
		int start = index;
		TypePattern t = parseTypePatternUnionOrIntersection(generics);
		
		if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Comma) {
			// indicates a tuple
			ArrayList<TypePattern> types = new ArrayList<TypePattern>();
			types.add(t);
			while (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Comma) {
				match(AbstractFileLexer.Comma.class);
				types.add(parseTypePatternUnionOrIntersection(generics));
			}			
			t = new TypePattern.Tuple(types.toArray(new TypePattern[types.size()]),
					null, sourceAttr(start, index - 1));
		}	
		
		return t;
	}
	
	private TypePattern parseTypePatternUnionOrIntersection(HashSet<String> generics) {
		int start = index;
		TypePattern p = parseTypePatternAtom(generics);

		if (index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Bar) {
			match(AbstractFileLexer.Bar.class);
			SyntacticType t = parseSyntacticTypeUnionOrIntersection(generics);
			t = new SyntacticType.Or(new SyntacticType[]{p.toSyntacticType(),t}, sourceAttr(start, index - 1));
			p = new TypePattern.Leaf(t, null, sourceAttr(start, index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof AbstractFileLexer.BitwiseAnd) {
			match(AbstractFileLexer.BitwiseAnd.class);
			SyntacticType t = parseSyntacticTypeUnionOrIntersection(generics);
			t = new SyntacticType.And(new SyntacticType[] {
					p.toSyntacticType(), t }, sourceAttr(start, index - 1));
			p = new TypePattern.Leaf(t, null, sourceAttr(start, index - 1));
		}


		if(index < tokens.size() && tokens.get(index) instanceof AbstractFileLexer.Identifier) {
			p.var = matchIdentifier().text;
		}
		
		return p;
	}
	
	private TypePattern parseTypePatternAtom(HashSet<String> generics) {				
		
		checkNotEof();
		int start = index;
		AbstractFileLexer.Token token = tokens.get(index);
		SyntacticType t;
		
		if(token.text.equals("any")) {
			matchKeyword("any");
			t = new SyntacticType.Primitive(SemanticType.Any,sourceAttr(start,index-1));
		} else if(token.text.equals("int")) {
			matchKeyword("int");			
			t = new SyntacticType.Primitive(SemanticType.Int,sourceAttr(start,index-1));
		} else if(token.text.equals("real")) {
			matchKeyword("real");		
			t = new SyntacticType.Primitive(SemanticType.Real,sourceAttr(start,index-1));
		} else if(token.text.equals("void")) {
			matchKeyword("void");
			t = new SyntacticType.Primitive(SemanticType.Void,sourceAttr(start,index-1));
		} else if(token.text.equals("bool")) {
			matchKeyword("bool");
			t = new SyntacticType.Primitive(SemanticType.Bool,sourceAttr(start,index-1));
		} else if (token instanceof AbstractFileLexer.LeftBrace) {
			match(AbstractFileLexer.LeftBrace.class);
			TypePattern p = parseTypePattern(generics);
			match(AbstractFileLexer.RightBrace.class);
			return p;
		} else if(token instanceof AbstractFileLexer.Shreak) {
			match(AbstractFileLexer.Shreak.class);
			t = new SyntacticType.Not(parseSyntacticType(generics),sourceAttr(start,index-1));
		} else if (token instanceof AbstractFileLexer.LeftCurly) {		
			match(AbstractFileLexer.LeftCurly.class);
			t = new SyntacticType.Set(parseSyntacticType(generics),sourceAttr(start,index-1));
			match(AbstractFileLexer.RightCurly.class);
		} else if(token instanceof AbstractFileLexer.Identifier) {
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
					
		return new TypePattern.Leaf(t,null,sourceAttr(start,index-1));
	}
	
	private void addNamedVariables(TypePattern type, HashSet<String> environment) {

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
	
	private void checkNotEof() {		
		if (index >= tokens.size()) {
			throw new SyntaxError("unexpected end-of-file", filename,
					index - 1, index - 1);
		}
		return;
	}
	
	private <T extends AbstractFileLexer.Token> T match(Class<T> c) {
		checkNotEof();
		AbstractFileLexer.Token t = tokens.get(index);
		if (!c.isInstance(t)) {			
			syntaxError("syntax error" , t);
		}
		index = index + 1;
		return (T) t;
	}
	
	private AbstractFileLexer.Token matchAll(Class<? extends AbstractFileLexer.Token>... cs) {
		checkNotEof();
		AbstractFileLexer.Token t = tokens.get(index);
		for(Class<? extends AbstractFileLexer.Token> c : cs) {
			if (c.isInstance(t)) {			
				index = index + 1;
				return t;
			}
		}
		syntaxError("syntax error" , t);
		return null;
	}
	
	private AbstractFileLexer.Identifier matchIdentifier() {
		checkNotEof();
		AbstractFileLexer.Token t = tokens.get(index);
		if (t instanceof AbstractFileLexer.Identifier) {
			AbstractFileLexer.Identifier i = (AbstractFileLexer.Identifier) t;
			index = index + 1;
			return i;
		}
		syntaxError("identifier expected", t);
		return null; // unreachable.
	}
	
	private AbstractFileLexer.Keyword matchKeyword(String keyword) {
		checkNotEof();
		AbstractFileLexer.Token t = tokens.get(index);
		if (t instanceof AbstractFileLexer.Keyword) {
			if (t.text.equals(keyword)) {
				index = index + 1;
				return (AbstractFileLexer.Keyword) t;
			}
		}
		syntaxError("keyword " + keyword + " expected.", t);
		return null;
	}
	
	private Attribute.Source sourceAttr(int start, int end) {
		AbstractFileLexer.Token t1 = tokens.get(start);
		AbstractFileLexer.Token t2 = tokens.get(end);
		// HACK: should really calculate the line number correctly here.
		return new Attribute.Source(t1.start,t2.end(),0);
	}
	
	private void syntaxError(String msg, SyntacticElement e) {
		Attribute.Source loc = e.attribute(Attribute.Source.class);
		throw new SyntaxError(msg, filename, loc.start, loc.end);
	}

	private void syntaxError(String msg, AbstractFileLexer.Token t) {
		throw new SyntaxError(msg, filename, t.start, t.start
				+ t.text.length() - 1);
	}
}