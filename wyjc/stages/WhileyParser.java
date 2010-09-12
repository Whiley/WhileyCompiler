// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjc.stages;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import wyil.lang.*;
import wyil.jvm.rt.BigRational;
import wyil.util.*;
import wyjc.lang.*;
import wyjc.lang.Stmt;
import wyjc.lang.WhileyFile.*;
import wyjc.util.*;
import wyjvm.lang.Bytecode;

import static wyjc.stages.WhileyLexer.*;

public class WhileyParser {
	private String filename;
	private ArrayList<Token> tokens;	
	private int index;

	public WhileyParser(String filename, List<Token> tokens) {
		this.filename = filename;
		this.tokens = new ArrayList<Token>(tokens); 
	}
	public WhileyFile read() {
		ArrayList<Decl> decls = new ArrayList<Decl>();
		boolean finishedImports = false;
		ArrayList<String> pkg = parsePackage();
				
		while(index < tokens.size()) {			
			Token t = tokens.get(index);
			if (t instanceof NewLine || t instanceof Comment) {
				matchEndLine();
			} else if(t instanceof Keyword) {
				Keyword k = (Keyword) t;
				if(k.text.equals("import")) {
					if(finishedImports) {
						syntaxError("import statement must come first",k);
					}
					decls.add(parseImport());
				} else {
					List<Modifier> modifiers = parseModifiers();
					
					t = tokens.get(index);
					
					if (t.text.equals("define")) {
						finishedImports = true;
						decls.add(parseDefType(modifiers));
					} else {
						finishedImports = true;
						decls.add(parseFunction(modifiers));
					} 
				}
			} else {
				finishedImports = true;
				decls.add(parseFunction(new ArrayList<Modifier>()));				
			}			
		}
		
		// Now, figure out module name from filename
		String name = filename.substring(filename
				.lastIndexOf(File.separatorChar) + 1, filename.length() - 7);
		
		return new WhileyFile(new ModuleID(pkg,name),filename,decls);
	}
	
	private ArrayList<String> parsePackage() {
		
		while (index < tokens.size()
				&& (tokens.get(index) instanceof Comment || tokens.get(index) instanceof NewLine)) {			
			parseSkip();
		}
		
		if(index < tokens.size() && tokens.get(index).text.equals("package")) {			
			matchKeyword("package");

			ArrayList<String> pkg = new ArrayList<String>();
			pkg.add(matchIdentifier().text);
						
			while (index < tokens.size() && tokens.get(index) instanceof Dot) {
				match(Dot.class);
				pkg.add(matchIdentifier().text);
			}

			matchEndLine();
			return pkg;
		} else {
			return new ArrayList<String>(); // no package
		}
	}
	
	private ImportDecl parseImport() {
		int start = index;
		matchKeyword("import");
		
		ArrayList<String> pkg = new ArrayList<String>();
		pkg.add(matchIdentifier().text);
		
		while (index < tokens.size() && tokens.get(index) instanceof Dot) {
			match(Dot.class);
			if(index < tokens.size()) {
				Token t = tokens.get(index);
				if(t.text.equals("*")) {
					match(Star.class);
					pkg.add("*");	
				} else {
					pkg.add(matchIdentifier().text);
				}
			}
		}
		
		matchEndLine();
		
		return new ImportDecl(pkg, sourceAttr(start, index - 1));
	}
	
	private FunDecl parseFunction(List<Modifier> modifiers) {			
		int start = index;		
		UnresolvedType ret = parseType();				
		// FIXME: potential bug here at end of file
		Token token = tokens.get(index+1);
		UnresolvedType receiver = null;
							
		if(token instanceof Colon) {
			receiver = parseType();			
			match(Colon.class);
			match(Colon.class);					
		}
		
		Identifier name = matchIdentifier();						
		
		match(LeftBrace.class);		
		
		// Now build up the parameter types
		List<Parameter> paramTypes = new ArrayList();
		boolean firstTime=true;		
		while (index < tokens.size()
				&& !(tokens.get(index) instanceof RightBrace)) {
			if (!firstTime) {
				match(Comma.class);
			}
			firstTime = false;
			int pstart = index;
			UnresolvedType t = parseType();
			Identifier n = matchIdentifier();
			paramTypes.add(new Parameter(t, n.text, sourceAttr(pstart,
					index - 1)));
		}
		
		match(RightBrace.class);
		
		Pair<Expr,Expr> conditions = parseRequiresEnsures();
		
		match(Colon.class);
		matchEndLine();
		
		List<Stmt> stmts = parseBlock(1);
		
		return new FunDecl(modifiers, name.text, receiver, ret, paramTypes,
				conditions.first(), conditions.second(), stmts, sourceAttr(
						start, index - 1));
	}
	
	private Decl parseDefType(List<Modifier> modifiers) {		
		int start = index; 
		matchKeyword("define");
		
		Identifier name = matchIdentifier();		
		
		matchKeyword("as");
		
		int mid = index;
		
		// At this point, there are two possibilities. Either we have a type
		// constructor, or we have an expression (which should correspond to a
		// constant).
		
		try {			
			UnresolvedType t = parseType();			
			Expr constraint = null;
			if(index < tokens.size() && tokens.get(index).text.equals("where")) {
				// this is a constrained type				
				matchKeyword("where");
				constraint = parseCondition();							
			}					
			matchEndLine();					
			return new TypeDecl(modifiers, t, name.text, constraint, sourceAttr(start,index-1));
		
		} catch(Exception e) {	
		}
		
		// Ok, failed parsing type constructor. So, backtrack and try for
		// expression.
		index = mid;	
		Expr e = parseCondition();
		
		matchEndLine();		
		return new ConstDecl(modifiers, e, name.text, sourceAttr(start,index-1));
	}
	
	private List<Modifier> parseModifiers() {
		ArrayList<Modifier> mods = new ArrayList<Modifier>();
		Token lookahead;
		while (index < tokens.size()
				&& isModifier((lookahead = tokens.get(index)))) {
			if(lookahead.text.equals("public")) {
				mods.add(Modifier.PUBLIC);
			} 
			index = index + 1;
		}
		return mods;
	}
	
	public String[] modifiers = {
			"public",
			"visible"
	};
	
	public boolean isModifier(Token tok) {
		for(String m : modifiers) {
			if(tok.text.equals(m)) {
				return true;
			}
		}
		return false;
	}
	
	private List<Stmt> parseBlock(int indent) {
		Tabs tabs = null;
		
		tabs = getIndent();
		
		ArrayList<Stmt> stmts = new ArrayList<Stmt>();
		while(tabs != null && tabs.ntabs == indent) {
			index = index + 1;
			stmts.add(parseStatement(indent));			
			tabs = getIndent();			
		}
		
		return stmts;
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
	
	private Expr parseWhere() {
		checkNotEof();		
		if(index < tokens.size() && tokens.get(index).text.equals("where")) {
			// this is a constrained type				
			matchKeyword("where");
			return parseCondition();			
		} else {
			return null;
		}
	}
	
	private Pair<Expr, Expr> parseRequiresEnsures() {
		checkNotEof();
		if (index < tokens.size() && tokens.get(index).text.equals("requires")) {
			// this is a constrained type
			matchKeyword("requires");
			Expr pre = parseCondition();
			Expr post = null;
			if (index < tokens.size() && tokens.get(index) instanceof Comma) {
				match(Comma.class);
				matchKeyword("ensures");
				post = parseCondition();
			}
			return new Pair<Expr, Expr>(pre, post);
		} else if (index < tokens.size()
				&& tokens.get(index).text.equals("ensures")) {
			// this is a constrained type
			matchKeyword("ensures");
			return new Pair<Expr, Expr>(null, parseCondition());
		} else {
			return new Pair<Expr,Expr>(null,null);
		}
	}
	
	private Stmt parseStatement(int indent) {
		checkNotEof();
		Token token = tokens.get(index);
		
		if(token.text.equals("return")) {
			return parseReturn();
		} else if(token.text.equals("assert")) {
			return parseAssert();
		} else if(token.text.equals("print")) {
			return parsePrint();
		} else if(token.text.equals("if")) {			
			return parseIf(indent);
		} else if(token.text.equals("while")) {			
			return parseWhile(indent);
		} else if(token.text.equals("for")) {			
			return parseFor(indent);
		} else if(token.text.equals("extern")) {			
			return parseExtern(indent);
		} else if(token.text.equals("spawn")) {			
			return parseSpawn();
		} else if (isTypeStart(token)) {
			return parseVarDecl();
		} else if ((index + 1) < tokens.size()
				&& tokens.get(index + 1) instanceof LeftBrace) {
			// must be a method invocation
			return parseInvokeStmt();			
		} else if (token instanceof NewLine || token instanceof Comment) {			
			return parseSkip();
		} else {
			int start = index;
			Expr t = parseIndexTerm();
			if (t instanceof Expr.Variable && (index < tokens.size()
					&& !(tokens.get(index) instanceof Equals))) {
				index = start;
				return parseVarDecl();
			} else if(t instanceof Expr.Invoke) {
				matchEndLine();
				return (Expr.Invoke) t;
			} else {
				index = start;
				return parseAssign();
			}
		}
	}		
	
	private Expr.Invoke parseInvokeStmt() {				
		int start = index;
		Identifier name = matchIdentifier();		
		match(LeftBrace.class);
		boolean firstTime=true;
		ArrayList<Expr> args = new ArrayList<Expr>();
		while (index < tokens.size()
				&& !(tokens.get(index) instanceof RightBrace)) {
			if(!firstTime) {
				match(Comma.class);
			} else {
				firstTime=false;
			}			
			Expr e = parseMulDivExpression();
			args.add(e);
			
		}
		match(RightBrace.class);
		matchEndLine();				
		
		// no receiver is possible in this case.
		return new Expr.Invoke(name.text, null, args, sourceAttr(start,index-1));
	}
	
	private Stmt parseReturn() {
		int start = index;
		matchKeyword("return");
		Expr e = null;
		if (index < tokens.size()
				&& !(tokens.get(index) instanceof NewLine || tokens.get(index) instanceof Comment)) {
			e = parseCondition();
		}
		matchEndLine();
		return new Stmt.Return(e, sourceAttr(start, index - 1));
	}
	
	private Stmt parseAssert() {
		int start = index;
		matchKeyword("assert");				
		checkNotEof();
		Expr e = parseCondition();
		matchEndLine();		
		return new Stmt.Assert(e, sourceAttr(start,index-1));
	}
	
	private Stmt parseSkip() {
		int start = index;
		matchEndLine();		
		return new Stmt.Skip(sourceAttr(start,index-1));
	}
	
	private Stmt parsePrint() {		
		int start = index;
		matchKeyword("print");		
		checkNotEof();
		Expr e = parseMulDivExpression();
		matchEndLine();		
		return new Stmt.Debug(e, sourceAttr(start,index-1));
	}
	
	private Stmt parseIf(int indent) {
		int start = index;
		matchKeyword("if");						
		Expr c = parseCondition();								
		match(Colon.class);
		matchEndLine();
		List<Stmt> tblk = parseBlock(indent+1);				
		List<Stmt> fblk = Collections.EMPTY_LIST;
		
		if ((index+1) < tokens.size() && tokens.get(index) instanceof Tabs) {
			Tabs ts = (Tabs) tokens.get(index);			
			if(ts.ntabs == indent && tokens.get(index+1).text.equals("else")) {
				match(Tabs.class);
				matchKeyword("else");
				
				if(index < tokens.size() && tokens.get(index).text.equals("if")) {
					Stmt if2 = parseIf(indent);
					fblk = new ArrayList<Stmt>();
					fblk.add(if2);
				} else {
					match(Colon.class);
					matchEndLine();
					fblk = parseBlock(indent+1);
				}
			}
		}		
		
		return new Stmt.IfElse(c,tblk,fblk, sourceAttr(start,index-1));
	}
	
	private Stmt parseWhile(int indent) {
		int start = index;
		matchKeyword("while");						
		Expr condition = parseCondition();
		Expr invariant = null;
		if(tokens.get(index).text.equals("where")) {
			matchKeyword("where");
			invariant = parseCondition();
		}
		match(Colon.class);
		matchEndLine();
		List<Stmt> blk = parseBlock(indent+1);								
		
		return new Stmt.While(condition,invariant,blk, sourceAttr(start,index-1));
	}
	
	private Stmt parseFor(int indent) {
		int start = index;
		matchKeyword("for");						
		Identifier id = matchIdentifier();
		match(ElemOf.class);
		Expr source = parseCondition();
		Expr invariant = null;
		if(tokens.get(index).text.equals("where")) {
			matchKeyword("where");
			invariant = parseCondition();
		}
		match(Colon.class);
		matchEndLine();
		List<Stmt> blk = parseBlock(indent+1);								
		
		return new Stmt.For(id.text,source,invariant,blk, sourceAttr(start,index-1));
	}
	
	private Stmt parseExtern(int indent) {
		int start = index;
		matchKeyword("extern");
		Token tok = tokens.get(index++);
		if(!tok.text.equals("jvm")) {
			syntaxError("unsupported extern language: " + tok,tok);
		}		
		match(Colon.class);
		matchEndLine();
		Tabs tabs = null;
		
		if(tokens.get(index) instanceof Tabs) {
			tabs = (Tabs) tokens.get(index);
		}
		indent = indent + 1;
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		while(tabs != null && tabs.ntabs == indent) {												
			index = index + 1;
			bytecodes.add(parseBytecode());								
			tabs = null;
			if(index < tokens.size() && tokens.get(index) instanceof Tabs) {
				tabs = (Tabs) tokens.get(index);
			} else {
				tabs = null;
			}
		}
		
		return new Stmt.ExternJvm(bytecodes,sourceAttr(start,index-1));
	}		
	
	private Bytecode parseBytecode() {
		String line = "";
		int start = index;
		while(index < tokens.size() && !(tokens.get(index) instanceof NewLine)) {
			Token tok = tokens.get(index);
			while(line.length() != tok.start) {
				line = line + " ";
			}			
			line = line + tokens.get(index).text;			
			index++;
		}				
		try {
			Bytecode b = wyjvm.util.Parser.parseBytecode(line);			
			matchEndLine();
			return b;
		} catch(wyjvm.util.Parser.ParseError err) {	
			Attribute.Source sa = sourceAttr(start,index-1);
			throw new SyntaxError(err.getMessage(),filename,sa.start,sa.end,err);
		}
	}
	
	private Stmt parseVarDecl() {
		int start = index;
		UnresolvedType type = parseType();
		Identifier var = matchIdentifier();
		Expr initialiser = null;
		if (index < tokens.size() && tokens.get(index) instanceof Equals) {
			match(Equals.class);
			initialiser = parseCondition();
		}

		matchEndLine();
		return new Stmt.VarDecl(type, var.text, initialiser, sourceAttr(
				start, index - 1));
	}
		
	private Stmt parseAssign() {		
		// standard assignment
		int start = index;
		Expr lhs = parseIndexTerm();		
		if(!(lhs instanceof Expr.LVal)) {
			syntaxError("expecting lval, found " + lhs + ".", lhs);
		}				
		match(Equals.class);		
		Expr rhs = parseCondition();
		matchEndLine();
		return new Stmt.Assign((Expr.LVal) lhs, rhs, sourceAttr(start,
				index - 1));		
	}	
	
	private Expr parseCondition() {
		checkNotEof();
		int start = index;		
		Expr c1 = parseConditionExpression();

		if(index < tokens.size() && tokens.get(index) instanceof LogicalAnd) {			
			match(LogicalAnd.class);
			Expr c2 = parseCondition();			
			return new Expr.BinOp(Expr.BOp.AND, c1, c2, sourceAttr(start,
					index - 1));
		} else if(index < tokens.size() && tokens.get(index) instanceof LogicalOr) {
			match(LogicalOr.class);
			Expr c2 = parseCondition();
			return new Expr.BinOp(Expr.BOp.OR, c1, c2, sourceAttr(start,
					index - 1));			
		} 
		return c1;		
	}
		
	private Expr parseConditionExpression() {		
		int start = index;
		
		if (index < tokens.size()
				&& tokens.get(index) instanceof WhileyLexer.None) {
			match(WhileyLexer.None.class);
			Expr.Comprehension sc = parseQuantifierSet();
			return new Expr.Comprehension(Expr.COp.NONE, null, sc.sources,
					sc.condition, sourceAttr(start, index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof WhileyLexer.Some) {
			match(WhileyLexer.Some.class);
			Expr.Comprehension sc = parseQuantifierSet();			
			return new Expr.Comprehension(Expr.COp.SOME, null, sc.sources,
					sc.condition, sourceAttr(start, index - 1));			
		} // should do FOR here;  could also do lone and one
		
		Expr lhs = parseMulDivExpression();
		
		if (index < tokens.size() && tokens.get(index) instanceof LessEquals) {
			match(LessEquals.class);				
			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.LTEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof LeftAngle) {
 			match(LeftAngle.class);				
			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.LT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof GreaterEquals) {
			match(GreaterEquals.class);	
			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.GTEQ,  lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof RightAngle) {
			match(RightAngle.class);			
			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.GT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof EqualsEquals) {
			match(EqualsEquals.class);			
			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.EQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof NotEquals) {
			match(NotEquals.class);			
			Expr rhs = parseMulDivExpression();			
			return new Expr.BinOp(Expr.BOp.NEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.TypeEquals) {
			return parseTypeEquals(lhs,start);			
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.ElemOf) {
			match(WhileyLexer.ElemOf.class);			
			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.ELEMENTOF,lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.SubsetEquals) {
			match(WhileyLexer.SubsetEquals.class);			
			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.SUBSETEQ, lhs, rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.Subset) {
			match(WhileyLexer.Subset.class);			
			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.SUBSET, lhs,  rhs, sourceAttr(start,index-1));
		} else {
			return lhs;
		}	
	}
	
	private Expr parseTypeEquals(Expr lhs, int start) {
		match(WhileyLexer.TypeEquals.class);			
		UnresolvedType type = parseType();
		Expr.TypeConst tc = new Expr.TypeConst(type, sourceAttr(start, index - 1));				
		
		return new Expr.BinOp(Expr.BOp.TYPEEQ, lhs, tc, sourceAttr(start,
				index - 1));
	}
	
	private Expr parseMulDivExpression() {
		int start = index;
		Expr lhs = parseAddSubExpression();
		
		if (index < tokens.size() && tokens.get(index) instanceof Star) {
			match(Star.class);
			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.MUL, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof RightSlash) {
			match(RightSlash.class);
			Expr rhs = parseMulDivExpression();
			return new Expr.BinOp(Expr.BOp.DIV, lhs, rhs, sourceAttr(start,
					index - 1));
		}

		return lhs;
	}
	
	private Expr parseAddSubExpression() {
		int start = index;
		Expr lhs = parseIndexTerm();

		if (index < tokens.size() && tokens.get(index) instanceof Plus) {
			match(Plus.class);
			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.ADD, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size() && tokens.get(index) instanceof Minus) {
			match(Minus.class);
			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.SUB, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size() && tokens.get(index) instanceof Union) {
			match(Union.class);
			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.UNION, lhs, rhs, sourceAttr(start,
					index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof Intersection) {
			match(Intersection.class);
			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.INTERSECTION, lhs, rhs, sourceAttr(
					start, index - 1));
		}	
		
		return lhs;
	}
	
	private Expr parseIndexTerm() {
		checkNotEof();
		int start = index;
		int ostart = index;		
		Expr lhs = parseRangeTerm();		
		Token lookahead = tokens.get(index);
		
		while (lookahead instanceof LeftSquare || lookahead instanceof Dot
				|| lookahead instanceof LeftBrace || lookahead instanceof Arrow) {
			ostart = start;
			start = index;
			if(lookahead instanceof LeftSquare) {
				match(LeftSquare.class);
				Expr rhs = parseMulDivExpression();
				lookahead = tokens.get(index);
				if(lookahead instanceof Colon) {
					match(Colon.class);
					Expr end = parseMulDivExpression();
					match(RightSquare.class);
					lhs = new Expr.NaryOp(Expr.NOp.SUBLIST, sourceAttr(
							start, index - 1), lhs, rhs, end);
				} else {
					match(RightSquare.class);							
					lhs = new Expr.ListAccess(lhs, rhs, sourceAttr(start,
							index - 1));
				}
			} else if(lookahead instanceof Arrow) {								
				match(Arrow.class);					
				int tmp = index; 
				String name = matchIdentifier().text;
				if(index < tokens.size() && tokens.get(index) instanceof LeftBrace) {
					// this indicates a method invocation.
					index = tmp; // slight backtrack
					Expr.Invoke ivk = parseInvokeExpr();							
					lhs = new Expr.Invoke(ivk.name, lhs, ivk.arguments,
							sourceAttr(ostart, index - 1));				
				} else {					
					lhs = new Expr.UnOp(Expr.UOp.PROCESSACCESS, lhs,
							sourceAttr(start, index - 1));
					lhs = new Expr.RecordAccess(lhs, name, sourceAttr(start,index - 1));			
				}
			} else {				
				match(Dot.class);
				String name = matchIdentifier().text;				
				lhs =  new Expr.RecordAccess(lhs, name, sourceAttr(start,index - 1));
			}
			if(index < tokens.size()) {
				lookahead = tokens.get(index);	
			} else {
				lookahead = null;
			}
		}
		
		return lhs;		
	}
	
	public Expr parseRangeTerm() {
		int start = index;
		Expr st = parseTerm();
		
		if ((index + 1) < tokens.size()				
				&& tokens.get(index) instanceof DotDot) {						
			match(DotDot.class);			
			Expr ed = parseTerm();
			return new Expr.BinOp(Expr.BOp.LISTRANGE, st, ed, sourceAttr(start,
					index - 1));
		} else {
			return st;
		}
	}
	
	private Expr parseTerm() {
		checkNotEof();		
		
		int start = index;
		Token token = tokens.get(index);
		if(token instanceof LeftBrace) {
			match(LeftBrace.class);
			checkNotEof();
			Expr v = parseCondition();
			checkNotEof();
			token = tokens.get(index);
			
			if(token instanceof RightBrace) {
				// this indicates just an expression surrounded by braces
				match(RightBrace.class);
				return v;
			} 
		} else if(token instanceof Star) {
			// this indicates a process dereference
			match(Star.class);
			Expr e = parseMulDivExpression();
			return new Expr.UnOp(Expr.UOp.PROCESSACCESS, e, sourceAttr(start,
					index - 1));
		} else if ((index + 1) < tokens.size()
				&& token instanceof Identifier
				&& tokens.get(index + 1) instanceof LeftBrace) {				
			// must be a method invocation			
			return parseInvokeExpr();
		} else if (token.text.equals("true")) {
			matchKeyword("true");			
			return new Expr.Constant(Value.V_BOOL(true),
					sourceAttr(start, index - 1));
		} else if (token.text.equals("false")) {	
			matchKeyword("false");
			return new Expr.Constant(Value.V_BOOL(false),
					sourceAttr(start, index - 1));			
		} else if(token.text.equals("spawn")) {
			return parseSpawn();			
		} else if (token instanceof Identifier) {
			return new Expr.Variable(matchIdentifier().text, sourceAttr(start,
					index - 1));			
		} else if (token instanceof Int) {			
			BigInteger val = match(Int.class).value;
			return new Expr.Constant(Value.V_INT(val), sourceAttr(start, index - 1));
		} else if (token instanceof Real) {
			BigRational val = match(Real.class).value;
			return new Expr.Constant(Value.V_REAL(val), sourceAttr(start,
					index - 1));			
		} else if (token instanceof Strung) {
			return parseString();
		} else if (token instanceof Minus) {
			return parseNegation();
		} else if (token instanceof Bar) {
			return parseLengthOf();
		} else if (token instanceof LeftSquare) {
			return parseListVal();
		} else if (token instanceof LeftCurly) {
			return parseSetVal();
		} else if (token instanceof EmptySet) {
			match(EmptySet.class);
			return new Expr.Constant(Value.V_SET(new ArrayList<Value>()),
					sourceAttr(start, index - 1));
		} else if (token instanceof Shreak) {
			match(Shreak.class);
			return new Expr.UnOp(Expr.UOp.NOT, parseTerm(),
					sourceAttr(start, index - 1));
		}
		syntaxError("unrecognised term.",token);
		return null;		
	}
	
	private Expr.Spawn parseSpawn() {
		int start = index;
		matchKeyword("spawn");
		Expr state = parseMulDivExpression();
		return new Expr.Spawn(state, sourceAttr(start,index - 1));
	}
	
	private Expr parseListVal() {
		int start = index;
		ArrayList<Expr> exprs = new ArrayList<Expr>();
		match(LeftSquare.class);
		boolean firstTime = true;
		checkNotEof();
		Token token = tokens.get(index);
		while(!(token instanceof RightSquare)) {
			if(!firstTime) {
				match(Comma.class);
			}
			firstTime=false;
			exprs.add(parseCondition());
			checkNotEof();
			token = tokens.get(index);
		}
		match(RightSquare.class);
		return new Expr.NaryOp(Expr.NOp.LISTGEN, exprs, sourceAttr(start,
				index - 1));
	}
	
	private Expr.Comprehension parseQuantifierSet() {
		int start = index;		
		match(LeftCurly.class);			
		Token token = tokens.get(index);			
		boolean firstTime = true;						
		List<Pair<String,Expr>> srcs = new ArrayList<Pair<String,Expr>>();
		HashSet<String> vars = new HashSet<String>();
		while(!(token instanceof Bar)) {						
			if(!firstTime) {
				match(Comma.class);
			}
			firstTime=false;
			Identifier id = matchIdentifier();
			String var = id.text;
			if(vars.contains(var)) {
				syntaxError(
						"variable "
								+ var
								+ " cannot have multiple source collections",
						id);
			} else {
				vars.add(var);
			}
			match(WhileyLexer.ElemOf.class);
			Expr src = parseConditionExpression();
			srcs.add(new Pair(var,src));
			checkNotEof();
			token = tokens.get(index);
		}
		match(Bar.class);
		Expr condition = parseCondition();		
		match(RightCurly.class);
		return new Expr.Comprehension(Expr.COp.SETCOMP, null, srcs, condition,
				sourceAttr(start, index - 1));
	}
	
	private Expr parseSetVal() {
		int start = index;		
		match(LeftCurly.class);
		ArrayList<Expr> exprs = new ArrayList<Expr>();	
		Token token = tokens.get(index);
		
		if(token instanceof RightCurly) {
			match(RightCurly.class);
			// empty set definition
			Value v = Value.V_SET(new ArrayList<Value>()); 
			return new Expr.Constant(v, sourceAttr(start, index - 1));
		}
		
		exprs.add(parseCondition());
		boolean setComp = false;
		boolean firstTime = false;
		if (index < tokens.size() && tokens.get(index) instanceof Bar) { 
			// this is a set comprehension
			setComp=true;
			match(Bar.class);
			firstTime=true;
		} else if (index < tokens.size() && tokens.get(index) instanceof Colon
				&& exprs.get(0) instanceof Expr.Variable) {
			// this is a record constructor
			Expr.Variable v = (Expr.Variable)exprs.get(0); 
			return parseRecordVal(start,v.var);
		}
		
		checkNotEof();
		token = tokens.get(index);
		while(!(token instanceof RightCurly)) {						
			if(!firstTime) {
				match(Comma.class);
			}
			firstTime=false;
			exprs.add(parseCondition());
			checkNotEof();
			token = tokens.get(index);
		}
		match(RightCurly.class);
		
		if(setComp) {
			Expr value = exprs.get(0);
			List<Pair<String,Expr>> srcs = new ArrayList<Pair<String,Expr>>();
			HashSet<String> vars = new HashSet<String>();
			Expr condition = null;			
			
			for(int i=1;i!=exprs.size();++i) {
				Expr v = exprs.get(i);				
				if(v instanceof Expr.BinOp) {
					Expr.BinOp eof = (Expr.BinOp) v;					
					if (eof.op == Expr.BOp.ELEMENTOF
							&& eof.lhs instanceof Expr.Variable) {
						String var = ((Expr.Variable) eof.lhs).var;
						if (vars.contains(var)) {
							syntaxError(
									"variable "
											+ var
											+ " cannot have multiple source collections",
									v);
						}
						vars.add(var);
						srcs.add(new Pair<String,Expr>(var,  eof.rhs));
						continue;
					} 					
				} 
				
				if((i+1) == exprs.size()) {
					condition = v;					
				} else {
					syntaxError("condition expected",v);
				}
			}			
			return new Expr.Comprehension(Expr.COp.SETCOMP, value, srcs,
					condition, sourceAttr(start, index - 1));
		} else {	
			return new Expr.NaryOp(Expr.NOp.SETGEN, exprs, sourceAttr(
					start, index - 1));
		}
	}
	
	private Expr parseRecordVal(int start, String ident) {

		// this indicates a tuple value.				)
		match(Colon.class);
		Expr e = parseMulDivExpression();
		HashMap<String,Expr> exprs = new HashMap<String,Expr>();
		exprs.put(ident, e);
		checkNotEof();
		Token token = tokens.get(index);
		while(!(token instanceof RightCurly)) {			
			match(Comma.class);
			checkNotEof();
			token = tokens.get(index);			
			Identifier n = matchIdentifier();

			if(exprs.containsKey(n.text)) {
				syntaxError("duplicate tuple key",n);
			}

			match(Colon.class);
			e = parseMulDivExpression();				
			exprs.put(n.text,e);
			checkNotEof();
			token = tokens.get(index);					
		} 
		match(RightCurly.class);

		return new Expr.RecordGen(exprs,sourceAttr(start, index - 1));
	} 
	
	private Expr parseLengthOf() {
		int start = index;
		match(Bar.class);
		Expr e = parseIndexTerm();
		match(Bar.class);
		return new Expr.UnOp(Expr.UOp.LENGTHOF, e, sourceAttr(start, index - 1));
	}

	private Expr parseNegation() {
		int start = index;
		match(Minus.class);
		Expr e = parseIndexTerm();
		
		if(e instanceof Expr.Constant) {
			Expr.Constant c = (Expr.Constant) e;
			if(c.value instanceof Value.Int) { 
				java.math.BigInteger bi = ((Value.Int)c.value).value;
				return new Expr.Constant(Value.V_INT(bi.negate()),
						sourceAttr(start, index));
			} else if(c.value instanceof Value.Real){
				BigRational br = ((Value.Real)c.value).value;				
				return new Expr.Constant(Value.V_REAL(br.negate()), sourceAttr(
						start, index));	
			}
		} 
		
		return new Expr.UnOp(Expr.UOp.NEG, e, sourceAttr(start, index));		
	}

	private Expr.Invoke parseInvokeExpr() {		
		int start = index;
		Identifier name = matchIdentifier();		
		match(LeftBrace.class);
		boolean firstTime=true;
		ArrayList<Expr> args = new ArrayList<Expr>();
		while (index < tokens.size()
				&& !(tokens.get(index) instanceof RightBrace)) {
			if(!firstTime) {
				match(Comma.class);
			} else {
				firstTime=false;
			}			
			Expr e = parseMulDivExpression();
			args.add(e);		
		}
		match(RightBrace.class);		
		return new Expr.Invoke(name.text, null, args, sourceAttr(start,index-1));
	}
	
	private Expr parseString() {
		int start = index;
		String s = match(Strung.class).string;
		ArrayList<Value> vals = new ArrayList<Value>();
		for (int i = 0; i != s.length(); ++i) {
			vals.add(Value.V_INT(BigInteger.valueOf(s.charAt(i))));
		}
		Value.List list = Value.V_LIST(vals);
		return new Expr.Constant(list, sourceAttr(start, index - 1));
	}
	
	private UnresolvedType parseType() {
		int start = index;
		UnresolvedType t = parseBaseType();
		// Now, attempt to look for union or intersection types.
		if (index < tokens.size() && tokens.get(index) instanceof Bar) {
			// this is a union type
			ArrayList<UnresolvedType.NonUnion> types = new ArrayList<UnresolvedType.NonUnion>();
			types.add((UnresolvedType.NonUnion)t);
			while (index < tokens.size() && tokens.get(index) instanceof Bar) {
				match(Bar.class);
				t = parseBaseType();
				types.add((UnresolvedType.NonUnion)t);
			}					
			return new UnresolvedType.Union(types, sourceAttr(start,index-1));
		} else {
			return t;
		}		
	}
	
	private UnresolvedType parseBaseType() {		
		checkNotEof();
		int start = index;
		Token token = tokens.get(index);
		UnresolvedType t;
		
		if(token instanceof Question) {
			match(Question.class);
			t = new UnresolvedType.Existential(sourceAttr(start,index-1));
		} else if(token instanceof Star) {
			match(Star.class);
			t = new UnresolvedType.Any(sourceAttr(start,index-1));
		} else if(token.text.equals("int")) {
			matchKeyword("int");
			t = new UnresolvedType.Int(sourceAttr(start,index-1));
		} else if(token.text.equals("real")) {
			matchKeyword("real");
			t = new UnresolvedType.Real(sourceAttr(start,index-1));
		} else if(token.text.equals("void")) {
			matchKeyword("void");
			t = new UnresolvedType.Void(sourceAttr(start,index-1));
		} else if(token.text.equals("bool")) {
			matchKeyword("bool");
			t = new UnresolvedType.Bool(sourceAttr(start,index-1));
		} else if(token.text.equals("process")) {
			matchKeyword("process");
			t = new UnresolvedType.Process(parseType(),sourceAttr(start,index-1));			
		} else if(token instanceof LeftCurly) {
			match(LeftCurly.class);
			t = parseType();
			checkNotEof();
			if(tokens.get(index) instanceof RightCurly) {
				// set type
				match(RightCurly.class);
				t = new UnresolvedType.Set(t,sourceAttr(start,index-1));
			} else {				
				// record type
				HashMap<String,UnresolvedType> types = new HashMap<String,UnresolvedType>();
				Token n = matchIdentifier();
				if(types.containsKey(n)) {
					syntaxError("duplicate tuple key",n);
				}
				types.put(n.text, t);				
				checkNotEof();
				token = tokens.get(index);
				while(!(token instanceof RightCurly)) {
					match(Comma.class);
					checkNotEof();
					token = tokens.get(index);
					UnresolvedType tmp = parseType();					
					n = matchIdentifier();
					if(types.containsKey(n)) {
						syntaxError("duplicate tuple key",n);
					}								
					types.put(n.text, tmp);					
					checkNotEof();
					token = tokens.get(index);								
				}				
				match(RightCurly.class);
				t = new UnresolvedType.Record(types, sourceAttr(start,index-1));				
			} 
		} else if(token instanceof LeftSquare) {
			match(LeftSquare.class);
			t = parseType();
			match(RightSquare.class);
			t = new UnresolvedType.List(t,sourceAttr(start,index-1));
		} else {		
			Identifier id = matchIdentifier();			
			t = new UnresolvedType.Named(id.text,sourceAttr(start,index-1));			
		}		
		
		return t;
	}		
	
	private boolean isTypeStart(Token token) {		
		if(token instanceof Keyword) {
			return token.text.equals("int") || token.text.equals("void")
					|| token.text.equals("bool") || token.text.equals("real")
					|| token.text.equals("?") || token.text.equals("*")
					|| token.text.equals("process");			
		} else {
			return token instanceof LeftCurly || token instanceof LeftSquare
					|| token instanceof LeftBrace;
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
		throw new ParseError(msg, filename, loc.start, loc.end);
	}
	
	private void syntaxError(String msg, Token t) {
		throw new ParseError(msg, filename, t.start, t.start + t.text.length() - 1);
	}		
}
