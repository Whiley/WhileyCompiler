package wyone.io;

import java.math.BigInteger;
import java.util.*;

import wyone.core.Attribute;
import wyone.util.*;
import static wyone.core.SpecFile.*;
import static wyone.io.SpecLexer.*;
import wyone.core.*;

public class SpecParser {
	private String filename;
	private ArrayList<Token> tokens;	
	private int index;

	public SpecParser(String filename, List<Token> tokens) {
		this.filename = filename;
		this.tokens = new ArrayList<Token>(tokens); 
	}
	
	public SpecFile parse() {
		ArrayList<Decl> decls = new ArrayList<Decl>();

		while(index < tokens.size()) {			
			Token t = tokens.get(index);
			if (t instanceof NewLine || t instanceof Comment) {
				matchEndLine();
			} else {
				Token lookahead = tokens.get(index);
				
				if(lookahead.text.equals("term")) {		
					decls.add(parseTermDecl());
				} else if(lookahead.text.equals("class")) {		
					decls.add(parseClassDecl());
				} else {			
					decls.add(parseRewriteDecl());
				}				
			}
		}
		
		return new SpecFile(filename,decls);
	}
		
	private Decl parseTermDecl() {
		int start = index;
		matchKeyword("term");
		Type.Term data = parseTermType();
		matchEndLine();		
		return new TermDecl(data, sourceAttr(start,index-1));
	}
		
	private Decl parseClassDecl() {
		int start = index;
		matchKeyword("class");
		String name = matchIdentifier().text;
		matchKeyword("as");
		ArrayList<String> children = new ArrayList<String>();
		boolean firstTime=true;
		do {
			if(!firstTime) {
				match(Bar.class);
			}
			firstTime=false;
			children.add(matchIdentifier().text);
		} while(index < tokens.size() && tokens.get(index) instanceof Bar);
		matchEndLine();
		return new ClassDecl(name, children, sourceAttr(start,index-1));
	}
	
	private Decl parseRewriteDecl() {
		int start = index;
		matchKeyword("rewrite");
		Pattern.Term pattern = parsePatternTerm();
		match(Colon.class);
		matchEndLine();
		List<RuleDecl> rules = parseRuleBlock(1);
		return new RewriteDecl(pattern,rules,sourceAttr(start,index-1));
	}
	
	public Pattern parsePattern() {
		skipWhiteSpace(true);
		checkNotEof();
		Token token = tokens.get(index);
		
		if (token instanceof Star) {
			match(Star.class);
			return new Pattern.Leaf(Type.T_ANY);
		} else if (token instanceof LeftCurly
				|| token instanceof LeftSquare) {
			return parsePatternCompound();
		} else if (token instanceof LeftBrace) {
			match(LeftBrace.class);
			Pattern r = parsePattern();
			match(RightBrace.class);
			return r;
		} else {
			return parsePatternTerm();
		} 
	}
	
	public Pattern.Term parsePatternTerm() {
		int start = index;
		String name = matchIdentifier().text;
		Token token = tokens.get(index);
		Pattern p;
		if(token instanceof LeftBrace || token instanceof LeftCurly || token instanceof LeftSquare) {
			p = parsePattern();
		} else {			
			p = new Pattern.Leaf(Type.T_VOID);
		}
		 	
		return new Pattern.Term(name, p, sourceAttr(start, index - 1));
	}
	
	public Pattern.Compound parsePatternCompound() {
		int start = index;		
		Type.Compound.Kind kind;
		ArrayList<Pair<Pattern, String>> params = new ArrayList();
		if (index < tokens.size() && tokens.get(index) instanceof LeftSquare) {
			match(LeftSquare.class);
			kind = Type.Compound.Kind.LIST;
		} else {
			match(LeftCurly.class);
			kind = Type.Compound.Kind.SET;
		}
		boolean firstTime = true;
		boolean unbound = false;
		while (index < tokens.size()
				&& !(tokens.get(index) instanceof RightSquare)
				&& !(tokens.get(index) instanceof RightCurly)) {
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
		switch (kind) {
		case LIST:
			match(RightSquare.class);
			break;
		case SET:
			match(RightCurly.class);
		}
		
		return new Pattern.Compound(kind, unbound, params, 
				sourceAttr(start, index - 1));		
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
		// FIXME: there's still a bug here for empty lines with arbitrary tabs
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
				
		Expr lhs = parseAddSubExpression();
		
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
			
			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.EQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof NotEquals) {
			match(NotEquals.class);			
			skipWhiteSpace(true);
			
			Expr rhs = parseAddSubExpression();			
			return new Expr.BinOp(Expr.BOp.NEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index).text.equals("is")) {
			return parseTypeEquals(lhs,start);			
		} else {
			return lhs;
		}	
	}
	
	private Expr parseTypeEquals(Expr lhs, int start) {
		matchKeyword("is");			
		skipWhiteSpace(true);
		
		Type type = parseType();
		Expr.TypeConst tc = new Expr.TypeConst(type, sourceAttr(start, index - 1));				
		
		return new Expr.BinOp(Expr.BOp.TYPEEQ, lhs, tc, sourceAttr(start,
				index - 1));
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
		Expr lhs = parseIndexTerm();
		
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
	
	private Expr parseIndexTerm() {
		checkNotEof();
		int start = index;
		int ostart = index;		
		Expr lhs = parseTerm();
		
		Token lookahead = tokens.get(index);
		
		while (lookahead instanceof LeftSquare
				|| lookahead instanceof LeftBrace || lookahead instanceof Hash) {
			ostart = start;
			start = index;
			if(lookahead instanceof LeftSquare) {
				match(LeftSquare.class);
				skipWhiteSpace(true);
				
				lookahead = tokens.get(index);
				
				if (lookahead instanceof DotDot) {
					// this indicates a sublist without a starting expression;
					// hence, start point defaults to zero
					match(DotDot.class);
					skipWhiteSpace(true);
					lookahead = tokens.get(index);
					Expr end = parseAddSubExpression();
					match(RightSquare.class);
					return new Expr.NaryOp(Expr.NOp.SUBLIST, sourceAttr(start,
							index - 1), lhs, new Expr.Constant(BigInteger.ZERO,
							sourceAttr(start, index - 1)), end);
				}
				
				Expr rhs = parseAddSubExpression();
				
				lookahead = tokens.get(index);
				if(lookahead instanceof DotDot) {					
					match(DotDot.class);
					skipWhiteSpace(true);
					lookahead = tokens.get(index);
					Expr end;
					if(lookahead instanceof RightSquare) {
						// In this case, no end of the slice has been provided.
						// Therefore, it is taken to be the length of the source
						// expression.						
						end = new Expr.UnOp(Expr.UOp.LENGTHOF, lhs, lhs
								.attribute(Attribute.Source.class));
					} else {
						end = parseAddSubExpression();						
					}
					match(RightSquare.class);
					lhs = new Expr.NaryOp(Expr.NOp.SUBLIST, sourceAttr(
							start, index - 1), lhs, rhs, end);
				} else {
					match(RightSquare.class);							
					lhs = new Expr.ListAccess(lhs, rhs, sourceAttr(start,
							index - 1));
				}
			} else if(lookahead instanceof Hash) {
				match(Hash.class);
				if(index < tokens.size() && tokens.get(index) instanceof Int) {
					BigInteger x = match(Int.class).value;		
					// FIXME: should check size here
					lhs = new Expr.TermAccess(lhs, x.intValue(), sourceAttr(start,index - 1));
				} else {
					lhs = new Expr.TermAccess(lhs, -1, sourceAttr(start,index - 1));
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
				&& tokens.get(index + 1) instanceof LeftBrace) {				
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
		} else if (token instanceof Bar) {
			return parseLengthOf();
		} else if (token instanceof LeftSquare || token instanceof LeftCurly) {
			return parseCompoundVal();
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
	
	private Expr parseCompoundVal() {
		int start = index;
		ArrayList<Expr> exprs = new ArrayList<Expr>();
		match(LeftSquare.class);
		skipWhiteSpace(true);
		boolean firstTime = true;
		checkNotEof();
		Token token = tokens.get(index);
		while(!(token instanceof RightSquare)) {
			if(!firstTime) {
				match(Comma.class);
				skipWhiteSpace(true);
			}
			firstTime=false;
			exprs.add(parseCondition());
			skipWhiteSpace(true);
			checkNotEof();
			token = tokens.get(index);
		}
		match(RightSquare.class);
		return new Expr.NaryOp(Expr.NOp.LISTGEN, exprs, sourceAttr(start,
				index - 1));
	}
			
	private Expr parseLengthOf() {
		int start = index;
		match(Bar.class);
		skipWhiteSpace(true);
		Expr e = parseIndexTerm();
		skipWhiteSpace(true);
		match(Bar.class);
		return new Expr.UnOp(Expr.UOp.LENGTHOF, e, sourceAttr(start, index - 1));
	}

	private Expr parseNegation() {
		int start = index;
		match(Minus.class);
		skipWhiteSpace(true);
		Expr e = parseIndexTerm();
		
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

		match(LeftBrace.class);
		skipWhiteSpace(true);
		Expr argument = parseConditionExpression();
		skipWhiteSpace(true);
		match(RightBrace.class);
		return new Expr.Constructor(name.text, argument, sourceAttr(start,
				index - 1));
	}
	
	private Expr parseString() {
		int start = index;
		String s = match(Strung.class).string;		
		//return new Expr.Constant(s, sourceAttr(start, index - 1));
		return null;
	}
		
	private Type parseType() {				
		skipWhiteSpace(true);
		checkNotEof();
		int start = index;
		Token token = tokens.get(index);
		Type t;
		
		if(token instanceof Star) {
			match(Star.class);
			t = Type.T_ANY;
		} else if(token.text.equals("int")) {
			matchKeyword("int");			
			t = Type.T_INT;
		} else if(token.text.equals("real")) {
			matchKeyword("real");
			t = Type.T_REAL;
		} else if(token.text.equals("void")) {
			matchKeyword("void");
			t = Type.T_VOID;
		} else if(token.text.equals("bool")) {
			matchKeyword("bool");
			t = Type.T_BOOL;
		} else if(token.text.equals("string")) {
			matchKeyword("string");
			t = Type.T_STRING;
		} else if (token instanceof LeftBrace) {
			match(LeftBrace.class);
			t = parseType();
			match(RightBrace.class);
		} else if (token instanceof LeftCurly || token instanceof LeftSquare) {		
			return parseCompoundType();
		} else {
			return parseTermType();
		}
		
		return t;
	}		
	
	private Type.Term parseTermType() {
		skipWhiteSpace(false);
		checkNotEof();
		Identifier id = matchIdentifier();		
		Type data = Type.T_VOID;
		if(index < tokens.size()) {
			Token token = tokens.get(index);
			if (token instanceof LeftBrace || token instanceof LeftCurly
					|| token instanceof LeftSquare) {
				data = parseType();
			}
		}
		return Type.T_TERM(id.text,data);
	}
	
	private Type.Compound parseCompoundType() {
		Type.Compound.Kind kind;
		if (index < tokens.size() && tokens.get(index) instanceof LeftBrace) {
			match(LeftBrace.class);
			kind = Type.Compound.Kind.LIST;
		} else {
			match(LeftCurly.class);
			kind = Type.Compound.Kind.SET;
		}
		ArrayList<Type> types = new ArrayList<Type>();
		boolean firstTime = true;
		while (index < tokens.size()
				&& !(tokens.get(index) instanceof RightBrace
						|| tokens.get(index) instanceof RightCurly || tokens
							.get(index) instanceof DotDotDot)) {
			if (!firstTime) {
				match(Comma.class);
			}
			firstTime = false;
			types.add(parseType());
		}
		boolean unbounded = false;
		if (index < tokens.size() && tokens.get(index) instanceof DotDotDot) {
			match(DotDotDot.class);
			unbounded = true;
		}
		switch (kind) {
		case LIST:
			match(RightBrace.class);
			break;
		case SET:
			match(RightCurly.class);
		}
		
		return Type.T_COMPOUND(kind, unbounded,
				types.toArray(new Type[types.size()]));
	}
	
	private boolean isTypeStart() {
		checkNotEof();
		Token token = tokens.get(index);
		if(token instanceof Keyword) {
			return token.text.equals("int") || token.text.equals("void")
					|| token.text.equals("bool") || token.text.equals("real")
					|| token.text.equals("?") || token.text.equals("*")
					|| token.text.equals("process");			
		} else if(token instanceof LeftBrace) {
			// Left brace is a difficult situation, since it can represent the
			// start of a tuple expression or the start of a typle lval.
			int tmp = index;
			match(LeftBrace.class);
			boolean r = isTypeStart();
			index = tmp;
			return r;
		} else {
			return token instanceof LeftCurly || token instanceof LeftSquare;
		}
	}

	private void skipWhiteSpace(boolean includeNewLine) {
		while (index < tokens.size()
				&& isWhiteSpace(includeNewLine, tokens.get(index))) {
			index++;
		}
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