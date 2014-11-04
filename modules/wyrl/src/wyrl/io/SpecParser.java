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

package wyrl.io;

import java.math.BigInteger;
import java.util.*;
import java.io.*;

import wyautl.util.BigRational;
import static wyrl.core.SpecFile.*;
import static wyrl.io.SpecLexer.*;
import wyrl.core.*;
import wyrl.core.Attribute.Source;
import wyrl.core.Expr.UOp;
import wyrl.core.SpecFile.Decl;
import wyrl.core.SpecFile.RewriteDecl;
import wyrl.core.SpecFile.RuleDecl;
import wyrl.core.SpecFile.TermDecl;
import wyrl.core.SpecFile.TypeDecl;
import wyrl.core.Type.Collection;
import wyrl.core.Type.Ref;
import wyrl.core.Type.Term;
import wyrl.util.*;

public class SpecParser {
	private File filename;
	private ArrayList<Token> tokens;
	private HashSet<File> included;
	private int index;

	public SpecParser(File file, List<Token> tokens) {
		this(file,tokens,new HashSet<File>());
	}

	private SpecParser(File file, List<Token> tokens, HashSet<File> included) {
		this.filename = file;
		this.tokens = new ArrayList<Token>(tokens);
		this.included = included;
	}

	public SpecFile parse() {
		ArrayList<Decl> decls = new ArrayList<Decl>();
		String pkg = parsePackage();
		while(index < tokens.size()) {
			Token t = tokens.get(index);
			if (t instanceof NewLine || t instanceof Comment) {
				matchEndLine();
			} else {
				Token lookahead = tokens.get(index);

				if(lookahead.text.equals("include")) {
					Decl id = parseIncludeDecl();
					// id can be null if the included file was already included
					// elsewhere.
					if(id != null) { decls.add(id); }
				} else if(lookahead.text.equals("term")) {
					decls.add(parseTermDecl());
				} else if(lookahead.text.equals("define")) {
					decls.add(parseTypeDecl());
				} else if(lookahead.text.equals("function")) {
					decls.add(parseFunctionDecl());
				} else {
					decls.add(parseRewriteDecl());
				}
			}
		}

		return new SpecFile(pkg, moduleName(), filename, decls);
	}

	private String moduleName() {
		String name = filename.getName();
		int idx = name.lastIndexOf('.');
		if(idx < 0) {
			return name;
		} else {
			return name.substring(0,idx);
		}
	}

	private String parsePackage() {
		skipWhiteSpace(true);
		Token lookahead = tokens.get(index);
		if(lookahead.text.equals("package")) {
			matchKeyword("package");
			String pkg = matchIdentifier().text;
			while((lookahead=tokens.get(index)) instanceof Dot) {
				match(Dot.class);
				pkg = pkg + "." + matchIdentifier().text;
			}
			return pkg;
		} else {
			return ""; // empty package
		}
	}

	private Decl parseIncludeDecl() {
		int start = index;
		Token token = tokens.get(index);
		matchKeyword("include");
		String relativeFilename = match(Strung.class).string;
		File incFile = new File(filename.getParent(),relativeFilename);
		matchEndLine();

		if(!included.contains(incFile)) {
			try {
				SpecLexer lexer = new SpecLexer(incFile);
				SpecParser parser = new SpecParser(incFile, lexer.scan(), included);
				SpecFile sf = parser.parse();
				included.add(incFile);
				return new IncludeDecl(sf, sourceAttr(start,index-1));
			} catch(IOException e) {
				syntaxError(e.getMessage(),token);
			}
		}

		return null;
	}

	private Decl parseTermDecl() {
		int start = index;
		matchKeyword("term");
		Type.Term data = parseTermType();
		matchEndLine();
		return new TermDecl(data, sourceAttr(start,index-1));
	}

	private Decl parseTypeDecl() {
		int start = index;
		matchKeyword("define");
		String name = matchIdentifier().text;
		matchKeyword("as");
		ArrayList<Type> types = new ArrayList<Type>();
		boolean firstTime=true;
		boolean isOpen = false;
		do {
			if (!firstTime) {
				if (isOpen) {
					syntaxError("'...' must mark end of declaration",
							tokens.get(index));
				}
				match(Bar.class);
			}
			firstTime=false;
			if(index < tokens.size() && tokens.get(index) instanceof DotDotDot) {
				match(DotDotDot.class);
				isOpen = true;
			} else {
				// FIXME: problem with parsing nested unions here
				types.add(parseType());
				skipWhiteSpace(true);
			}
		} while(index < tokens.size() && tokens.get(index) instanceof Bar);

		Type type;
		if(types.size() == 1) {
			type = types.get(0);
		} else {
			type = Type.T_OR(types);
		}

		return new TypeDecl(name, type, isOpen, sourceAttr(start,index-1));
	}

	private Decl parseRewriteDecl() {
		int start = index;
		Token lookahead = tokens.get(index);
		boolean reduce;
		if(lookahead.text.equals("reduce")) {
			matchKeyword("reduce");
			reduce = true;
		} else {
			matchKeyword("infer");
			reduce = false;
		}
		Pattern.Term pattern = (Pattern.Term) parsePatternTerm();
		Pair<String,Integer> nameAndRank = parseNameAndRank();
		match(Colon.class);
		matchEndLine();
		List<RuleDecl> rules = parseRuleBlock(1);

		String name = nameAndRank.first();
		int rank = nameAndRank.second();

		if(reduce) {
			return new ReduceDecl(pattern,rules,name,rank,sourceAttr(start,index-1));
		} else {
			return new InferDecl(pattern,rules,name,rank,sourceAttr(start,index-1));
		}
	}

	private Pair<String,Integer> parseNameAndRank() {
		String name = "";
		int rank = 0;
		skipWhiteSpace(true);
		Token lookahead = tokens.get(index);
		if(lookahead.text.equals("name")) {
			matchKeyword("name");
			Strung s = match(Strung.class);
			name = s.text.substring(1,s.text.length()-1);
		}
		skipWhiteSpace(true);
		lookahead = tokens.get(index);
		if(lookahead.text.equals("rank")) {
			matchKeyword("rank");
			Int i = match(Int.class);
			rank = i.value.intValue();
		}

		return new Pair<String,Integer>(name,rank);
	}

	private Decl parseFunctionDecl() {
		int start = index;
		matchKeyword("function");
		String name = matchIdentifier().text;
		Type from = parseType();
		match(Arrow.class);
		Type to = parseType();
		matchEndLine();
		return new FunctionDecl(name, from, to, sourceAttr(start, index - 1));
	}

	public Pattern parsePattern() {
		skipWhiteSpace(true);
		checkNotEof();
		Token token = tokens.get(index);

		if (token instanceof Star) {
			match(Star.class);
			return new Pattern.Leaf(Type.T_ANY());
		} else if (token instanceof LeftCurly
				|| token instanceof LeftCurlyBar
				|| token instanceof LeftSquare) {
			return parsePatternCompound();
		} else if (token instanceof LeftBrace) {
			Pattern r = parsePattern();
			return r;
		} else if(token instanceof Identifier){
			return parsePatternTerm();
		} else {
			return new Pattern.Leaf(parseType());
		}
	}

	public Pattern parsePatternTerm() {
		int start = index;
		String name = matchIdentifier().text;
		Token token = tokens.get(index);
		String var = null;
		Pattern p;
		if (token instanceof LeftCurly
				|| token instanceof LeftCurlyBar
				|| token instanceof LeftSquare) {
			p = parsePatternCompound();
		} else if(token instanceof LeftBrace) {
			match(LeftBrace.class);
			p = parsePattern();
			if (index < tokens.size()
					&& tokens.get(index) instanceof Identifier) {
				var = matchIdentifier().text;
			}
			match(RightBrace.class);
		} else {
			// in this case, it's not a pattern.
			return new Pattern.Leaf(Type.T_TERM(name, null), sourceAttr(start,
					index - 1));
		}

		return new Pattern.Term(name, p, var, sourceAttr(start, index - 1));
	}

	public Pattern.Collection parsePatternCompound() {
		int start = index;
		int kind; // 0 for set, 1 for bag, 2 for list
		ArrayList<Pair<Pattern, String>> params = new ArrayList();
		if (index < tokens.size() && tokens.get(index) instanceof LeftSquare) {
			match(LeftSquare.class);
			kind = 2;
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof LeftCurlyBar) {
			match(LeftCurlyBar.class);
			kind = 1;
		} else {
			match(LeftCurly.class);
			kind = 0;
		}
		boolean firstTime = true;
		boolean unbound = false;
		while (index < tokens.size()
				&& !(tokens.get(index) instanceof RightSquare)
				&& !(tokens.get(index) instanceof RightCurly)
				&& !(tokens.get(index) instanceof BarRightCurly)) {
			if (unbound) {
				syntaxError("... must be last match", tokens.get(index));
			}
			if (!firstTime) {
				match(Comma.class);
			}
			firstTime = false;
			Pattern p = parsePattern();
			if (index < tokens.size()
					&& tokens.get(index) instanceof DotDotDot) {
				match(DotDotDot.class);
				unbound = true;
			}
			String n = null;
			if (index < tokens.size()
					&& tokens.get(index) instanceof Identifier) {
				n = matchIdentifier().text;
			}
			params.add(new Pair<Pattern, String>(p, n));
		}
		switch(kind) {
		case 2:
			match(RightSquare.class);
			return new Pattern.List(unbound, params,
					sourceAttr(start, index - 1));
		case 1:
			match(BarRightCurly.class);
			return new Pattern.Bag(unbound, params,
					sourceAttr(start, index - 1));
		default:
			match(RightCurly.class);
			return new Pattern.Set(unbound, params,
					sourceAttr(start, index - 1));
		}
	}

	public List<RuleDecl> parseRuleBlock(int indent) {
		Tabs tabs = getIndent();

		ArrayList<RuleDecl> rules = new ArrayList<RuleDecl>();
		while(tabs != null && tabs.ntabs == indent) {
			index = index + 1;
			rules.add(parseRule());
			tabs = getIndent();
		}

		return rules;
	}

	private Tabs getIndent() {
		skipEmptyLines();
		if (index < tokens.size() && tokens.get(index) instanceof Tabs) {
			return (Tabs) tokens.get(index);
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof Comment) {
			// This indicates a completely empty line. In which case, we just
			// ignore it.
			matchEndLine();
			return getIndent();
		} else {
			return null;
		}
	}

	/**
	 * Skip over any empty lines. That is lines which contain only whitespace
	 * and comments.
	 */
	private void skipEmptyLines() {
		int tmp = index;
		do {
			tmp = skipWhiteSpace(tmp,false);
			if (tmp < tokens.size() && !(tokens.get(tmp) instanceof NewLine)) {
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


	public RuleDecl parseRule() {
		int start = index;
		match(Arrow.class);
		ArrayList<Pair<String,Expr>> lets = new ArrayList();
		if(index < tokens.size() && tokens.get(index).text.equals("let")) {
			matchKeyword("let");
			boolean firstTime=true;
			do {
				if(!firstTime) {
					match(Comma.class);
					skipWhiteSpace(true);
				}
				firstTime=false;
				String id = matchIdentifier().text;
				match(Equals.class);
				Expr rhs = parseAddSubExpression();
				lets.add(new Pair(id,rhs));
				skipWhiteSpace(true);
			} while(index < tokens.size() && tokens.get(index) instanceof Comma);
			match(ElemOf.class);
		}
		Expr result = parseAddSubExpression();
		skipWhiteSpace(true);
		if(index < tokens.size() && tokens.get(index) instanceof Comma) {
			match(Comma.class);
			matchKeyword("if");
			Expr condition = parseCondition();
			matchEndLine();
			return new RuleDecl(lets,result,condition,sourceAttr(start,index-1));
		} else {
			return new RuleDecl(lets,result,null,sourceAttr(start,index-1));
		}
	}

	private Expr parseCondition() {
		checkNotEof();
		int start = index;
		Expr c1 = parseConditionExpression();

		if(index < tokens.size() && tokens.get(index) instanceof LogicalAnd) {
			match(LogicalAnd.class);
			skipWhiteSpace(true);

			Expr c2 = parseCondition();
			return new Expr.BinOp(Expr.BOp.AND, c1, c2, sourceAttr(start,
					index - 1));
		} else if(index < tokens.size() && tokens.get(index) instanceof LogicalOr) {
			match(LogicalOr.class);
			skipWhiteSpace(true);

			Expr c2 = parseCondition();
			return new Expr.BinOp(Expr.BOp.OR, c1, c2, sourceAttr(start,
					index - 1));
		}
		return c1;
	}

	private Expr parseConditionExpression() {
		int start = index;

		if (index < tokens.size() && tokens.get(index) instanceof None) {
			match(None.class);
			return parseQuantifierSet(start,Expr.COp.NONE);
		} else if (index < tokens.size() && tokens.get(index) instanceof Some) {
			match(Some.class);
			return parseQuantifierSet(start,Expr.COp.SOME);
		}

		Expr lhs = parseRangeExpression();

		if (index < tokens.size() && tokens.get(index) instanceof LessEquals) {
			match(LessEquals.class);
			skipWhiteSpace(true);

			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.LTEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof LeftAngle) {
 			match(LeftAngle.class);
 			skipWhiteSpace(true);

 			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.LT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof GreaterEquals) {
			match(GreaterEquals.class);
			skipWhiteSpace(true);
			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.GTEQ,  lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof RightAngle) {
			match(RightAngle.class);
			skipWhiteSpace(true);

			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.GT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof EqualsEquals) {
			match(EqualsEquals.class);
			skipWhiteSpace(true);

			Expr rhs = parseRangeExpression();
			return new Expr.BinOp(Expr.BOp.EQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof NotEquals) {
			match(NotEquals.class);
			skipWhiteSpace(true);

			Expr rhs = parseRangeExpression();
			return new Expr.BinOp(Expr.BOp.NEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof ElemOf) {
			match(ElemOf.class);
			skipWhiteSpace(true);
			Expr rhs = parseRangeExpression();
			return new Expr.BinOp(Expr.BOp.IN, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index).text.equals("is")) {
			return parseTypeEquals(lhs,start);
		} else {
			return lhs;
		}
	}

	private Expr parseTypeEquals(Expr lhs, int start) {
		matchKeyword("is");
		skipWhiteSpace(true);

		Type type = Type.T_REF(parseType());
		Expr.Constant tc = new Expr.Constant(type, sourceAttr(start, index - 1));

		return new Expr.BinOp(Expr.BOp.IS, lhs, tc, sourceAttr(start,
				index - 1));
	}

	private Expr parseRangeExpression() {
		int start = index;
		Expr lhs = parseAddSubExpression();

		if (index < tokens.size() && tokens.get(index) instanceof DotDot) {
			match(DotDot.class);
			skipWhiteSpace(true);
			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.RANGE, lhs,  rhs, sourceAttr(start,index-1));
		}

		return lhs;
	}

	private Expr parseAddSubExpression() {
		int start = index;
		Expr lhs = parseMulDivExpression();

		if (index < tokens.size() && tokens.get(index) instanceof Plus) {
			match(Plus.class);
			skipWhiteSpace(true);
			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.ADD, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size() && tokens.get(index) instanceof Minus) {
			match(Minus.class);
			skipWhiteSpace(true);

			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.SUB, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size() && tokens.get(index) instanceof PlusPlus) {
			// wrong precidence
			match(PlusPlus.class);
			skipWhiteSpace(true);

			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.APPEND, lhs, rhs, sourceAttr(start,
					index - 1));
		}

		return lhs;
	}

	private Expr parseMulDivExpression() {
		int start = index;
		Expr lhs = parseCastExpression();

		if (index < tokens.size() && tokens.get(index) instanceof Star) {
			match(Star.class);
			skipWhiteSpace(true);

			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.MUL, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof RightSlash) {
			match(RightSlash.class);
			skipWhiteSpace(true);

			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.DIV, lhs, rhs, sourceAttr(start,
					index - 1));
		}

		return lhs;
	}

	private Expr parseCastExpression() {
		Token lookahead = tokens.get(index);
		if(lookahead instanceof LeftBrace) {
			int start = index;
			try {
				match(LeftBrace.class);
				Type type = parseType();
				match(RightBrace.class);
				Expr expr = parseIndexTerm();
				return new Expr.Cast(type, expr, sourceAttr(start,
						index - 1));
			} catch(SyntaxError e) {
				// ok, failed parsing the cast expression ... cannot be a cast
				// then!  restart assuming just an index term...
				index = start;
			}
		}
		return parseIndexTerm();
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
			if(lookahead instanceof LeftSquare) {
				match(LeftSquare.class);
				skipWhiteSpace(true);
				Expr rhs = parseCondition();
				skipWhiteSpace(true);
				if (index < tokens.size()
						&& tokens.get(index) instanceof LeftSlash) {
					// substitution expression
					match(LeftSlash.class);
					Expr e = parseAddSubExpression();
					lhs = new Expr.Substitute(lhs, rhs, e, sourceAttr(start,
							index - 1));
					match(RightSquare.class);
				} else if (index < tokens.size()
						&& tokens.get(index) instanceof Assignment) {
					// list update expression
					match(Assignment.class);
					Expr e = parseAddSubExpression();
					lhs = new Expr.ListUpdate(lhs, rhs, e, sourceAttr(start,
							index - 1));
					match(RightSquare.class);
				} else if (lhs instanceof Expr.Variable
						&& index < tokens.size()
						&& tokens.get(index) instanceof Comma) {
					// here, we have a case of mistaken identity
					index = ostart; // back track
					return parseConstructorExpr();
				} else {
					match(RightSquare.class);
					lhs = new Expr.ListAccess(lhs, rhs, sourceAttr(start,
						index - 1));
				}
			}
			if(index < tokens.size()) {
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
		} else if ((index + 1) < tokens.size()
				&& token instanceof Identifier
				&& (tokens.get(index + 1) instanceof LeftBrace
						// || tokens.get(index + 1) instanceof LeftSquare
						|| tokens.get(index + 1) instanceof LeftCurly
						|| tokens.get(index + 1) instanceof LeftCurlyBar)) {
			// must be a method invocation
			return parseConstructorExpr();
		} else if (token.text.equals("null")) {
			matchKeyword("null");
			return new Expr.Constant(null,
					sourceAttr(start, index - 1));
		} else if (token.text.equals("true")) {
			matchKeyword("true");
			return new Expr.Constant(true,
					sourceAttr(start, index - 1));
		} else if (token.text.equals("false")) {
			matchKeyword("false");
			return new Expr.Constant(false,
					sourceAttr(start, index - 1));
		} else if (token.text.equals("num")) {
			return parseNumerator();
		} else if (token.text.equals("den")) {
			return parseDenominator();
		} else if (token instanceof Identifier) {
			return new Expr.Variable(matchIdentifier().text, sourceAttr(start,
					index - 1));
		} else if (token instanceof Int) {
			BigInteger val = match(Int.class).value;
			return new Expr.Constant(val, sourceAttr(start, index - 1));
		} else if (token instanceof Real) {
			BigRational val = match(Real.class).value;
			return new Expr.Constant(val, sourceAttr(start,
					index - 1));
		} else if (token instanceof Strung) {
			return parseString();
		} else if (token instanceof Minus) {
			return parseNegation();
		} else if (token instanceof Star) {
			return parseDeref();
		} else if (token instanceof Bar) {
			return parseLengthOf();
		} else if (token instanceof LeftSquare) {
			return parseListVal();
		} else if (token instanceof LeftCurlyBar) {
			return parseBagVal();
		} else if (token instanceof LeftCurly) {
			return parseSetVal();
		} else if (token instanceof EmptySet) {
			match(EmptySet.class);
			return new Expr.Constant(new HashSet(),
					sourceAttr(start, index - 1));
		} else if (token instanceof Shreak) {
			match(Shreak.class);
			return new Expr.UnOp(Expr.UOp.NOT, parseTerm(),
					sourceAttr(start, index - 1));
		}
		syntaxError("unrecognised term.",token);
		return null;
	}

	private Expr parseListVal() {
		int start = index;
		ArrayList<Expr> exprs = new ArrayList<Expr>();
		match(LeftSquare.class);
		skipWhiteSpace(true);
		checkNotEof();
		Token token = tokens.get(index);

		if(!(token instanceof RightSquare)) {
			Expr expr = parseCondition();
			skipWhiteSpace(true);
			token = tokens.get(index);
			if(token instanceof Bar) {
				// comprehension
				return parseComprehensionRest(start,expr,Expr.COp.LISTCOMP);
			}
			exprs.add(expr);
		}

		while(!(token instanceof RightSquare)) {
			match(Comma.class);
			skipWhiteSpace(true);
			exprs.add(parseCondition());
			skipWhiteSpace(true);
			checkNotEof();
			token = tokens.get(index);
		}

		match(RightSquare.class);
		return new Expr.NaryOp(Expr.NOp.LISTGEN, exprs, sourceAttr(start,
				index - 1));
	}

	private Expr parseBagVal() {
		int start = index;
		ArrayList<Expr> exprs = new ArrayList<Expr>();
		match(LeftCurlyBar.class);
		skipWhiteSpace(true);
		Token token = tokens.get(index);

		if(!(token instanceof BarRightCurly)) {
			Expr expr = parseCondition();
			skipWhiteSpace(true);
			token = tokens.get(index);
			if(token instanceof Bar) {
				// comprehension
				return parseComprehensionRest(start,expr,Expr.COp.BAGCOMP);
			}
			exprs.add(expr);
		}

		while(!(token instanceof BarRightCurly)) {
			match(Comma.class);
			skipWhiteSpace(true);
			exprs.add(parseCondition());
			skipWhiteSpace(true);
			checkNotEof();
			token = tokens.get(index);
		}

		match(BarRightCurly.class);
		return new Expr.NaryOp(Expr.NOp.BAGGEN, exprs, sourceAttr(start,
				index - 1));
	}

	private Expr parseSetVal() {
		int start = index;
		ArrayList<Expr> exprs = new ArrayList<Expr>();
		match(LeftCurly.class);
		skipWhiteSpace(true);
		Token token = tokens.get(index);

		if(!(token instanceof RightCurly)) {
			Expr expr = parseCondition();
			skipWhiteSpace(true);
			token = tokens.get(index);
			if(token instanceof Bar) {
				// comprehension
				return parseComprehensionRest(start,expr,Expr.COp.SETCOMP);
			}
			exprs.add(expr);
		}

		while(!(token instanceof RightCurly)) {
			match(Comma.class);
			skipWhiteSpace(true);
			exprs.add(parseCondition());
			skipWhiteSpace(true);
			checkNotEof();
			token = tokens.get(index);
		}

		match(RightCurly.class);
		return new Expr.NaryOp(Expr.NOp.SETGEN, exprs, sourceAttr(start,
				index - 1));
	}

	private Expr parseComprehensionRest(int start, Expr result, Expr.COp cop) {
		match(Bar.class);
		skipWhiteSpace(true);
		ArrayList<Pair<Expr.Variable,Expr>> sources = new ArrayList<Pair<Expr.Variable,Expr>>();
		Expr condition = null;
		boolean firstTime = true;
		Token token = tokens.get(index);

		outer: while(true) {
			if(condition != null) {
				syntaxError("condition must come last in comprehension",
						token);
			}
			if(!firstTime) {
				match(Comma.class);
				skipWhiteSpace(true);
			} else {
				firstTime=false;
			}
			condition = parseCondition();
			Pair<Expr.Variable,Expr> source = extractComprehensionSource(condition);
			if(source != null) {
				condition = null;
				sources.add(source);
			}
			skipWhiteSpace(true);
			token = tokens.get(index);

			switch(cop) {
				case SETCOMP:
					if(token instanceof RightCurly) {
						match(RightCurly.class);
						break outer;
					}
					break;
				case BAGCOMP:
					if(token instanceof BarRightCurly) {
						match(BarRightCurly.class);
						break outer;
					}
					break;
				case LISTCOMP:
					if(token instanceof RightSquare) {
						match(RightSquare.class);
						break outer;
					}
					break;
			}

		}

		return new Expr.Comprehension(cop, result, sources, condition, sourceAttr(start,index-1));
	}

	private Expr parseQuantifierSet(int start, Expr.COp cop) {
		match(LeftCurly.class);
		skipWhiteSpace(true);
		ArrayList<Pair<Expr.Variable,Expr>> sources = new ArrayList<Pair<Expr.Variable,Expr>>();
		boolean firstTime = true;
		Token token = tokens.get(index);
		while(!(token instanceof Bar)) {
			if(!firstTime) {
				match(Comma.class);
				skipWhiteSpace(true);
			} else {
				firstTime=false;
			}
			Expr condition = parseConditionExpression();
			Pair<Expr.Variable,Expr> source = extractComprehensionSource(condition);
			if(source == null) {
				syntaxError("source expression required",
						token);
			}
			sources.add(source);
			skipWhiteSpace(true);
			token = tokens.get(index);
		}
		match(Bar.class);
		Expr condition = parseCondition();
		match(RightCurly.class);

		return new Expr.Comprehension(cop, null, sources, condition, sourceAttr(start,index-1));
	}
	private Pair<Expr.Variable, Expr> extractComprehensionSource(Expr expr) {
		if (expr instanceof Expr.BinOp) {
			Expr.BinOp bop = (Expr.BinOp) expr;
			if (bop.op == Expr.BOp.IN && bop.lhs instanceof Expr.Variable) {
				Expr.Variable var = (Expr.Variable) bop.lhs;
				return new Pair<Expr.Variable, Expr>(var, bop.rhs);
			}
		}
		return null;
	}

	private Expr parseDeref() {
		int start = index;
		match(Star.class);
		Expr e = parseCastExpression();
		return new Expr.TermAccess(e, sourceAttr(start, index - 1));
	}

	private Expr parseLengthOf() {
		int start = index;
		match(Bar.class);
		skipWhiteSpace(true);
		Expr e = parseCastExpression();
		skipWhiteSpace(true);
		match(Bar.class);
		return new Expr.UnOp(Expr.UOp.LENGTHOF, e, sourceAttr(start, index - 1));
	}

	private Expr parseNumerator() {
		int start = index;
		matchKeyword("num");
		skipWhiteSpace(true);
		Expr e = parseCastExpression();
		return new Expr.UnOp(UOp.NUMERATOR, e, sourceAttr(start, index - 1));
	}

	private Expr parseDenominator() {
		int start = index;
		matchKeyword("den");
		skipWhiteSpace(true);
		Expr e = parseCastExpression();
		return new Expr.UnOp(UOp.DENOMINATOR, e, sourceAttr(start, index - 1));
	}

	private Expr parseNegation() {
		int start = index;
		match(Minus.class);
		skipWhiteSpace(true);
		Expr e = parseCastExpression();

		if(e instanceof Expr.Constant) {
			Expr.Constant c = (Expr.Constant) e;
			if(c.value instanceof BigInteger) {
				java.math.BigInteger bi = (BigInteger)c.value;
				return new Expr.Constant(bi.negate(), sourceAttr(start, index));
			}
		}

		return new Expr.UnOp(Expr.UOp.NEG, e, sourceAttr(start, index));
	}

	private Expr.Constructor parseConstructorExpr() {
		int start = index;
		Identifier name = matchIdentifier();
		skipWhiteSpace(true);
		checkNotEof();
		Token token = tokens.get(index);
		Expr argument;
		if(token instanceof LeftBrace) {
			match(LeftBrace.class);
			argument = parseConditionExpression();
			match(RightBrace.class);
		} else if(token instanceof LeftSquare) {
			argument = parseListVal();
		} else if(token instanceof LeftCurlyBar) {
			argument = parseBagVal();
		} else if(token instanceof LeftCurly) {
			argument = parseSetVal();
		} else {
			syntaxError("unknown token encountered",token);
			return null;
		}

		return new Expr.Constructor(name.text, argument, false, sourceAttr(
				start, index - 1));
	}

	private Expr parseString() {
		int start = index;
		String s = match(Strung.class).string;
		return new Expr.Constant(s, sourceAttr(start, index - 1));
	}

	private Type parseType() {
		skipWhiteSpace(true);
		checkNotEof();
		int start = index;
		Token token = tokens.get(index);
		Type t;

		if(token instanceof Star) {
			match(Star.class);
			t = Type.T_ANY();
		} else if(token.text.equals("int")) {
			matchKeyword("int");
			t = Type.T_INT();
		} else if(token.text.equals("real")) {
			matchKeyword("real");
			t = Type.T_REAL();
		} else if(token.text.equals("void")) {
			matchKeyword("void");
			t = Type.T_VOID();
		} else if(token.text.equals("bool")) {
			matchKeyword("bool");
			t = Type.T_BOOL();
		} else if(token.text.equals("string")) {
			matchKeyword("string");
			t = Type.T_STRING();
		} else if (token instanceof LeftBrace) {
			match(LeftBrace.class);
			t = parseType();
			match(RightBrace.class);
		} else if(token instanceof Shreak) {
			match(Shreak.class);
			t = Type.T_NOT(parseType());
		} else if (token instanceof LeftCurly || token instanceof LeftCurlyBar
				|| token instanceof LeftSquare) {
			t = parseCompoundType();
		} else {
			t = parseTermType();
		}

		return t;
	}

	private Type.Term parseTermType() {
		skipWhiteSpace(false);
		checkNotEof();
		Identifier id = matchIdentifier();
		Type.Ref data = null;
		if (index < tokens.size()) {
			Token token = tokens.get(index);
			if (token instanceof LeftBrace || token instanceof LeftCurly
					|| token instanceof LeftCurlyBar
					|| token instanceof LeftSquare) {
				data = Type.T_REF(parseType());
			}
		}
		return Type.T_TERM(id.text, data);
	}

	private Type.Collection parseCompoundType() {
		int kind; // 0 = set, 1 = bag, 2 = list
		if (index < tokens.size() && tokens.get(index) instanceof LeftSquare) {
			match(LeftSquare.class);
			kind = 2;
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof LeftCurlyBar) {
			match(LeftCurlyBar.class);
			kind = 1;
		} else {
			match(LeftCurly.class);
			kind = 0;
		}
		ArrayList<Type> types = new ArrayList<Type>();
		boolean firstTime = true;
		while (index < tokens.size()
				&& !(tokens.get(index) instanceof RightSquare
						|| tokens.get(index) instanceof RightCurly
						|| tokens.get(index) instanceof BarRightCurly
						|| tokens.get(index) instanceof DotDotDot)) {
			if (!firstTime) {
				match(Comma.class);
			}
			firstTime = false;
			types.add(Type.T_REF(parseType()));
		}
		boolean unbounded = false;
		if (index < tokens.size() && tokens.get(index) instanceof DotDotDot) {
			match(DotDotDot.class);
			unbounded = true;
		}
		switch(kind) {
		case 2:
			match(RightSquare.class);
			return Type.T_LIST(unbounded,types);
		case 1:
			match(BarRightCurly.class);
			return Type.T_BAG(unbounded,types);
		default:
			match(RightCurly.class);
			return Type.T_SET(unbounded,types);
		}
	}

	private void skipWhiteSpace(boolean includeNewLine) {
		index = skipWhiteSpace(index,includeNewLine);
	}

	private int skipWhiteSpace(int index, boolean includeNewLine) {
		while (index < tokens.size()
				&& isWhiteSpace(includeNewLine, tokens.get(index))) {
			index++;
		}
		return index;
	}

	private boolean isWhiteSpace(boolean includeNewLine, Token t) {
		return (includeNewLine && t instanceof SpecLexer.NewLine)
				|| t instanceof SpecLexer.Comment
				|| t instanceof SpecLexer.Tabs;
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
		return new Attribute.Source(t1.start,t2.end());
	}

	private void syntaxError(String msg, Expr e) {
		Attribute.Source loc = e.attribute(Attribute.Source.class);
		throw new SyntaxError(msg, filename, loc.start, loc.end);
	}

	private void syntaxError(String msg, Token t) {
		throw new SyntaxError(msg, filename, t.start, t.start + t.text.length() - 1);
	}
}