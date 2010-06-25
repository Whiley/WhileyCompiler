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

import wyjc.ast.*;
import wyjc.ast.UnresolvedWhileyFile.*;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.exprs.process.*;
import wyjc.ast.exprs.real.*;
import wyjc.ast.exprs.set.*;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.stmts.*;
import wyjc.ast.types.*;
import wyjc.ast.types.unresolved.*;
import wyjc.jvm.rt.BigRational;
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
	public UnresolvedWhileyFile read() {
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
		
		return new UnresolvedWhileyFile(new ModuleID(pkg,name),filename,decls);
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
		UnresolvedType returnType = parseType();		
		FunDecl.Return ret = new FunDecl.Return(returnType,sourceAttr(start,index-1));
		// FIXME: potential bug here at end of file
		Token token = tokens.get(index+1);
		FunDecl.Receiver receiver = null;
							
		if(token instanceof Colon) {
			UnresolvedType process = parseType();
			receiver = new FunDecl.Receiver(process);
			match(Colon.class);
			match(Colon.class);					
		}
		
		Identifier name = matchIdentifier();						
		
		match(LeftBrace.class);		
		
		// Now build up the parameter types
		List<FunDecl.Parameter> paramTypes = new ArrayList();
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
			paramTypes.add(new FunDecl.Parameter(t, n.text, sourceAttr(pstart,
					index - 1)));
		}
		
		match(RightBrace.class);
		
		// now, look to see if we have requires and ensures clauses.
		Pair<Condition,Condition> ppc = parseRequiresEnsures();
		
		match(Colon.class);
		matchEndLine();
		
		List<Stmt> stmts = parseBlock(1);
		
		return new FunDecl(modifiers, name.text, receiver, ret, paramTypes, ppc.first(), ppc
				.second(), stmts, sourceAttr(start, index - 1));
	}
	
	private Decl parseDefType(List<Modifier> modifiers) {		
		int start = index; 
		matchKeyword("define");
		
		// At this point, there are two possibilities. Either we have a type
		// constructor, or we have an expression (which should correspond to a
		// constant).
		
		try {
			UnresolvedType t = parseType();
			Condition constraint = null;
			if(index < tokens.size() && tokens.get(index).text.equals("where")) {
				// this is a constrained type
				matchKeyword("where");
				constraint = parseRealCondition();
				
				Pair<Type,Condition> munged = mungeConstrainedTypes(t,constraint);
				t = munged.first();
				constraint = munged.second();
				
			}
			matchKeyword("as");
			Identifier name = matchIdentifier();			
			matchEndLine();		
			return new TypeDecl(modifiers, t, constraint, name.text, sourceAttr(start,index-1));
		
		} catch(Exception e) {}
		
		// Ok, failed parsing type constructor. So, backtrack and try for
		// expression.
		index = start;
		matchKeyword("define");		
		Expr e = parseCondition();
		matchKeyword("as");
		Identifier name = matchIdentifier();		
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
	
	/**
	 * The following method may seem quite strange. Basically, the idea is to
	 * try and sensibly prize the constraint appart and/or add scope information
	 * (where needed). For example, consider the following definitions:
	 * 
	 * <pre>
	 *   define (int op, [int] rest) where op &gt; 0 as msg
	 *   define [(int op)] where op &gt; 0 as mstList 
	 * </pre>
	 * 
	 * Basically, we need to translate these definitions into a more usuable
	 * form, such as:
	 * 
	 * <pre>
	 *   define (int op, [int] rest) where $.op &gt; 0 as msg
	 *   define (int op) where $ &gt; 0 as tmp$1
	 *   define [tmp$1] as mstList 
	 * </pre>
	 * 
	 * @param t
	 * @param constraint
	 * @return
	 */
	private Pair<Type,Condition> mungeConstrainedTypes(UnresolvedType t, Condition constraint) {
		// for the moment, it's really a hack.
		if(t instanceof UnresolvedTupleType) {
			UnresolvedTupleType tt = (UnresolvedTupleType) t;
			HashMap<String,Expr> binding = new HashMap<String,Expr>();
			for (Map.Entry<String, UnresolvedType> e : tt.types().entrySet()) {
				binding.put(e.getKey(), new TupleAccess(new Variable("$"), e
						.getKey()));
			}
			constraint = constraint.substitute(binding);
		} 
		
		return new Pair(t,constraint);
	}
	
	private Pair<Condition,Condition> parseRequiresEnsures() {
		checkNotEof();
		Condition preCondition = null;
		Condition postCondition = null;		
		Token token = tokens.get(index);
		
		if (token instanceof Keyword) {
			Keyword k = (Keyword) token;
			if (k.text.equals("requires")) {				
				matchKeyword("requires");
				preCondition = parseRealCondition();
			} 

			checkNotEof();
			token = tokens.get(index);
			if (token instanceof Comma) {
				match(Comma.class);
				checkNotEof();
				token = tokens.get(index);
			}
		}
		
		if (token instanceof Keyword) {
			Keyword k = (Keyword) token;
			if(k.text.equals("ensures")) {		
				matchKeyword("ensures");
				postCondition = parseRealCondition();
			}
		}
		
		return new Pair(preCondition,postCondition);
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
			if (t instanceof Variable && (index < tokens.size()
					&& !(tokens.get(index) instanceof Equals))) {
				index = start;
				return parseVarDecl();
			} else if(t instanceof Invoke) {
				matchEndLine();
				return (Invoke) t;
			} else {
				index = start;
				return parseAssign();
			}
		}
	}		
	
	private Invoke parseInvokeStmt() {				
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
		return new Invoke(name.text, null, args, sourceAttr(start,index-1));
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
		return new Return(e, sourceAttr(start,index-1));
	}
	
	private Stmt parseAssert() {
		int start = index;
		matchKeyword("assert");				
		checkNotEof();
		Condition e = parseRealCondition();
		matchEndLine();		
		return new Assertion(e, sourceAttr(start,index-1));
	}
	
	private Stmt parseSkip() {
		int start = index;
		matchEndLine();		
		return new Skip(sourceAttr(start,index-1));
	}
	
	private Stmt parsePrint() {		
		int start = index;
		matchKeyword("print");		
		checkNotEof();
		Expr e = parseMulDivExpression();
		matchEndLine();		
		return new Print(e, sourceAttr(start,index-1));
	}
	
	private Stmt parseIf(int indent) {
		int start = index;
		matchKeyword("if");						
		Condition c = parseRealCondition();								
		match(Colon.class);
		matchEndLine();
		List<Stmt> tblk = parseBlock(indent+1);				
		List<Stmt> fblk = null;
		
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
		
		return new IfElse(c,tblk,fblk, sourceAttr(start,index-1));
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
		
		return new ExternJvm(bytecodes,sourceAttr(start,index-1));
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
			SourceAttr sa = sourceAttr(start,index-1);
			throw new SyntaxError(err.getMessage(),filename,sa.start(),sa.end(),err);
		}
	}
	
	private Stmt parseVarDecl() {
		int start = index;
		UnresolvedType type = parseType();
		Identifier var = matchIdentifier();
		Expr initialiser = null;
		if (index < tokens.size() && tokens.get(index) instanceof Equals) {
			match(Equals.class);
			initialiser = parseConditionExpression();
		}

		matchEndLine();
		return new UnresolvedVarDecl(type, var.text, initialiser, sourceAttr(
				start, index - 1));
	}
		
	private Stmt parseAssign() {		
		// standard assignment
		int start = index;
		Expr lhs = parseIndexTerm();		
		if(!(lhs instanceof LVal)) {
			syntaxError("expecting lval, found " + lhs + ".", lhs);
		}				
		match(Equals.class);		
		Expr rhs = parseCondition();
		matchEndLine();
		return new Assign((LVal) lhs,rhs, sourceAttr(start,index-1));		
	}	
	
	private Expr parseCondition() {
		checkNotEof();
		int start = index;		
		Expr c1 = parseConditionExpression();

		if(index < tokens.size() && tokens.get(index) instanceof LogicalAnd) {			
			if (c1 instanceof Variable || c1 instanceof TupleAccess
					|| c1 instanceof ListAccess
					|| c1 instanceof Invoke) {
				c1 = new IntEquals(new BoolVal(true, c1.attributes()), c1, c1.attributes());
			}						
			checkCondition(c1);			
			match(LogicalAnd.class);
			Condition c2 = parseRealCondition();			
			return new And((Condition)c1,c2, sourceAttr(start,index-1));
		} else if(index < tokens.size() && tokens.get(index) instanceof LogicalOr) {
			if (c1 instanceof Variable || c1 instanceof TupleAccess
					|| c1 instanceof ListAccess) {
				c1 = new IntEquals(new BoolVal(true, c1.attributes()), c1, c1.attributes());
			}			
			checkCondition(c1);
			match(LogicalOr.class);
			Condition c2 = parseRealCondition();
			return new Or((Condition)c1,c2, sourceAttr(start,index-1));
		} 
		return c1;		
	}
	
	private void checkCondition(Expr c) {
		if(!(c instanceof Condition)) {
			syntaxError("Bool expression expected.",c);
		}		
	}
	
	private Condition parseRealCondition() {
		Expr e = parseCondition();
		
		if (e instanceof Variable || e instanceof TupleAccess
				|| e instanceof ListAccess
				|| e instanceof Invoke
				) {
			e = new IntEquals(new BoolVal(true, e.attributes()),e , e
					.attributes());
		} 
		
		checkCondition(e);
		
		return (Condition)e;
	}
	
	private Condition parseRealConditionExpression() {
		Expr e = parseConditionExpression();
		
		if (e instanceof Variable || e instanceof TupleAccess
				|| e instanceof ListAccess
				|| e instanceof Invoke
				) {
			e = new IntEquals(new BoolVal(true, e.attributes()),e , e
					.attributes());
		} 
		
		checkCondition(e);
		
		return (Condition)e;
	}		
	private Expr parseConditionExpression() {		
		int start = index;
		
		if (index < tokens.size()
				&& tokens.get(index) instanceof WhileyLexer.None) {
			match(WhileyLexer.None.class);
			SetComprehension sc = parseQuantifierSet();
			return new wyjc.ast.exprs.logic.None(sc, sourceAttr(start,index-1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof WhileyLexer.Some) {
			match(WhileyLexer.Some.class);
			SetComprehension sc = parseQuantifierSet();			
			return new wyjc.ast.exprs.logic.Some(sc, sourceAttr(start,index-1));
		} 
		
		Expr lhs = parseMulDivExpression();
		
		if (index < tokens.size() && tokens.get(index) instanceof LessEquals) {
			match(LessEquals.class);				
			Expr rhs = parseMulDivExpression();
			return new IntLessThanEquals( lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof LeftAngle) {
 			match(LeftAngle.class);				
			Expr rhs = parseMulDivExpression();
			return new IntLessThan( lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof GreaterEquals) {
			match(GreaterEquals.class);	
			Expr rhs = parseMulDivExpression();
			return new IntGreaterThanEquals( lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof RightAngle) {
			match(RightAngle.class);			
			Expr rhs = parseMulDivExpression();
			return new IntGreaterThan( lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof EqualsEquals) {
			match(EqualsEquals.class);			
			Expr rhs = parseMulDivExpression();
			return new IntEquals( lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof NotEquals) {
			match(NotEquals.class);			
			Expr rhs = parseMulDivExpression();			
			return new IntNotEquals( lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.ElemOf) {
			match(WhileyLexer.ElemOf.class);			
			Expr rhs = parseMulDivExpression();
			return new wyjc.ast.exprs.set.SetElementOf(lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.SubsetEquals) {
			match(WhileyLexer.SubsetEquals.class);			
			Expr rhs = parseMulDivExpression();
			return new wyjc.ast.exprs.set.SubsetEq( lhs, rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.Subset) {
			match(WhileyLexer.Subset.class);			
			Expr rhs = parseMulDivExpression();
			return new wyjc.ast.exprs.set.Subset( lhs,  rhs, sourceAttr(start,index-1));
		} else if(lhs instanceof Variable) {
			return (Variable) lhs;
		} else if(lhs instanceof Not) {
			return (Not) lhs;
		} else {
			return lhs;
		}	
	}
	
	private Expr parseMulDivExpression() {
		int start = index;
		Expr lhs = parseAddSubExpression();
		
		if(index < tokens.size() && tokens.get(index) instanceof Star) {
			match(Star.class);
			Expr rhs = parseMulDivExpression();
			return new IntMul( lhs, rhs, sourceAttr(start,index-1));	
		} else if(index < tokens.size() && tokens.get(index) instanceof RightSlash) {
			match(RightSlash.class);
			Expr rhs = parseMulDivExpression();
			return new IntDiv( lhs, rhs, sourceAttr(start,index-1));
		}
		
		return lhs;
	}
	
	private Expr parseAddSubExpression() {
		int start = index;
		Expr lhs = parseIndexTerm();
		
		if(index < tokens.size() && tokens.get(index) instanceof Plus) {
			match(Plus.class);
			Expr rhs = parseAddSubExpression();
			return new IntAdd( lhs, rhs, sourceAttr(start,index-1));	
		} else if(index < tokens.size() && tokens.get(index) instanceof Minus) {
			match(Minus.class);
			Expr rhs = parseAddSubExpression();
			return new IntSub( lhs, rhs, sourceAttr(start,index-1));
		} else if(index < tokens.size() && tokens.get(index) instanceof Union) {			
			match(Union.class);
			Expr rhs = parseAddSubExpression();
			return new SetUnion( lhs, rhs, sourceAttr(start,index-1));
		} else if(index < tokens.size() && tokens.get(index) instanceof Intersection) {			
			match(Intersection.class);
			Expr rhs = parseAddSubExpression();
			return new SetIntersection( lhs, rhs, sourceAttr(start,index-1));
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
					lhs =  new ListSublist( lhs,  rhs, end, sourceAttr(start,index - 1));
				} else {
					match(RightSquare.class);							
					lhs =  new ListAccess( lhs,  rhs, sourceAttr(start,index - 1));
				}
			} else if(lookahead instanceof Arrow) {								
				match(Arrow.class);					
				int tmp = index; 
				String name = matchIdentifier().text;
				if(index < tokens.size() && tokens.get(index) instanceof LeftBrace) {
					// this indicates a method invocation.
					index = tmp; // slight backtrack
					Invoke ivk = parseInvokeExpr();							
					lhs = new Invoke(ivk.name(), lhs, ivk.arguments(),
							sourceAttr(ostart, index - 1));				
				} else {					
					lhs = new ProcessAccess(lhs,sourceAttr(start,index - 1));
					lhs = new TupleAccess(lhs, name, sourceAttr(start,index - 1));			
				}
			} else {				
				match(Dot.class);
				String name = matchIdentifier().text;				
				lhs =  new TupleAccess( lhs, name, sourceAttr(start,index - 1));
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
			return new RangeGenerator(st,ed,sourceAttr(start,index-1));
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
			} else if(v instanceof Variable){			
				// this indicates a tuple value.				)
				match(Colon.class);
				Expr e = parseMulDivExpression();
				HashMap<String,Expr> exprs = new HashMap<String,Expr>();
				exprs.put(((Variable) v).name(), e);
				checkNotEof();
				token = tokens.get(index);
				while(!(token instanceof RightBrace)) {
					if(token instanceof Comma) {
						match(Comma.class);
						checkNotEof();
						token = tokens.get(index);
					}
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
				match(RightBrace.class);

				return new TupleGenerator(exprs,sourceAttr(start, index - 1));
			} 
		} else if(token instanceof Star) {
			// this indicates a process dereference
			match(Star.class);
			Expr e = parseMulDivExpression();
			return new ProcessAccess(e, sourceAttr(start, index - 1));
		} else if ((index + 1) < tokens.size()
				&& token instanceof Identifier
				&& tokens.get(index + 1) instanceof LeftBrace) {				
			// must be a method invocation			
			return parseInvokeExpr();
		} else if (token.text.equals("true")) {
			matchKeyword("true");			
			return new BoolVal(true,
					sourceAttr(start, index - 1));
		} else if (token.text.equals("false")) {	
			matchKeyword("false");
			return new BoolVal(false, sourceAttr(start, index - 1));
		} else if(token.text.equals("spawn")) {
			return parseSpawn();			
		} else if (token instanceof Identifier) {
			return new Variable(matchIdentifier().text, sourceAttr(start,
					index - 1));			
		} else if (token instanceof Int) {			
			BigInteger val = match(Int.class).value;
			return new IntVal(val, sourceAttr(start,index-1));
		} else if (token instanceof Real) {
			BigRational val = match(Real.class).value;
			return new RealVal(val, sourceAttr(start,index-1));
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
			return new SetVal(new ArrayList<Value>(), sourceAttr(start, index - 1));
		} else if (token instanceof Shreak) {
			match(Shreak.class);
			return new Not(parseRealConditionExpression(), sourceAttr(start, index - 1));
		}
		syntaxError("unrecognised term.",token);
		return null;		
	}
	
	private Spawn parseSpawn() {
		int start = index;
		matchKeyword("spawn");
		Expr state = parseMulDivExpression();
		return new Spawn(state, sourceAttr(start, index - 1));
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
		return new ListGenerator(exprs, sourceAttr(start, index - 1));
	}
	
	private SetComprehension parseQuantifierSet() {
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
		Condition condition = parseRealCondition();
		match(RightCurly.class);
		Expr value = new IntVal(0); // this is a dummy				
		return new SetComprehension(value, srcs, condition, sourceAttr(
				start, index - 1));
	}
	
	private Expr parseSetVal() {
		int start = index;		
		match(LeftCurly.class);
		ArrayList<Expr> exprs = new ArrayList<Expr>();	
		Token token = tokens.get(index);
		
		if(token instanceof RightCurly) {
			match(RightCurly.class);
			// empty set definition
			return new SetVal(new ArrayList<Value>(),sourceAttr(start,index-1));
		}
		
		exprs.add(parseCondition());
		boolean setComp = false;
		boolean firstTime = false;
		if (index < tokens.size() && tokens.get(index) instanceof Bar) { 
			// this is a set comprehension
			setComp=true;
			match(Bar.class);
			firstTime=true;
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
			Condition condition = null;			
			
			for(int i=1;i!=exprs.size();++i) {
				Expr v = exprs.get(i);
				if(v instanceof SetElementOf) {
					SetElementOf eof = (SetElementOf) v;
					if(eof.lhs() instanceof Variable) {
						String var = ((Variable) eof.lhs()).name();
						if (vars.contains(var)) {
							syntaxError(
									"variable "
											+ var
											+ " cannot have multiple source collections",
									v);
						}
						vars.add(var);
						srcs.add(new Pair<String,Expr>(var,  eof.rhs()));
						continue;
					}
				} else if(v instanceof Condition) {	
					if(condition != null) {
						condition = new And(condition,(Condition)v, v.attributes());
					} else {
						condition = (Condition)v;
					}
				} else {
					syntaxError("condition expected",v);
				}
			}
			return new SetComprehension(value, srcs, condition, sourceAttr(
					start, index - 1));
		} else {	
			return new SetGenerator(exprs,sourceAttr(start, index - 1));
		}
	}
	
	private Expr parseLengthOf() {
		int start = index;
		match(Bar.class);
		Expr e = parseIndexTerm();
		match(Bar.class);
		return new ListLength( e, sourceAttr(start, index - 1));
	}

	private Expr parseNegation() {
		int start = index;
		match(Minus.class);
		Expr e = parseIndexTerm();
		if(e instanceof IntVal) {
			IntVal ne = (IntVal) e;
			java.math.BigInteger bi = ne.value();
			return new IntVal(bi.negate(), sourceAttr(start,index));
		} else if(e instanceof RealVal) {
			RealVal ne = (RealVal) e;
			BigRational br = ne.value();
			return new RealVal(br.negate(), sourceAttr(start,index));
		} else {
			return new IntNegate( e, sourceAttr(start,index));
		}
	}

	private Invoke parseInvokeExpr() {		
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
		return new Invoke(name.text, null, args, sourceAttr(start,index-1));
	}
	
	private Expr parseString() {
		int start = index;
		String s = match(Strung.class).string;		
		ArrayList<Value> vals = new ArrayList<Value>();
		for(int i=0;i!=s.length();++i) {
			vals.add(new IntVal(s.charAt(i)));
		}		
		return new StringVal(vals,sourceAttr(start,index-1));
	}
	
	private UnresolvedType parseType() {		
		checkNotEof();		
		Token token = tokens.get(index);
		UnresolvedType t;
		
		if(token instanceof Question) {
			match(Question.class);
			t = Types.T_EXISTENTIAL;
		} else if(token instanceof Star) {
			match(Star.class);
			t = Types.T_ANY;
		} else if(token.text.equals("int")) {
			matchKeyword("int");
			t = Types.T_INT;
		} else if(token.text.equals("real")) {
			matchKeyword("real");
			t = Types.T_REAL;
		} else if(token.text.equals("void")) {
			matchKeyword("void");
			t = Types.T_VOID;
		} else if(token.text.equals("bool")) {
			matchKeyword("bool");
			t = Types.T_BOOL;
		} else if(token.text.equals("process")) {
			matchKeyword("process");
			t = new UnresolvedProcessType(parseType());			
		} else if(token instanceof LeftBrace) {
			match(LeftBrace.class);
			HashMap<String,UnresolvedType> types = new HashMap<String,UnresolvedType>();
			checkNotEof();
			token = tokens.get(index);
			while(!(token instanceof RightBrace)) {
				UnresolvedType tmp = parseType();
				
				Token n = matchIdentifier();
				if(types.containsKey(n)) {
					syntaxError("duplicate tuple key",n);
				}								
				types.put(n.text, tmp);
				checkNotEof();
				token = tokens.get(index);			
				if(token instanceof Comma) {
					match(Comma.class);
					checkNotEof();
					token = tokens.get(index);
				}
			}
			match(RightBrace.class);
			t = new UnresolvedTupleType(types);
		} else if(token instanceof LeftCurly) {
			match(LeftCurly.class);
			t = new UnresolvedSetType(parseType());
			match(RightCurly.class);
		} else if(token instanceof LeftSquare) {
			match(LeftSquare.class);
			t = new UnresolvedListType(parseType());
			match(RightSquare.class);
		} else {		
			Identifier id = matchIdentifier();			
			t = new UserDefType(id.text);			
		}		
		
		// Now, attempt to look for union or intersection types.
		if (index < tokens.size() && tokens.get(index) instanceof Bar) {
			// this is a union type.
			match(Bar.class);
			UnresolvedType t2 = parseType();
			// now, calculate the appropriate union type.
			return new UnresolvedUnionType(t, t2);
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
	
	private SourceAttr sourceAttr(int start, int end) {
		Token t1 = tokens.get(start);
		Token t2 = tokens.get(end);
		return new SourceAttr(filename,t1.start,t2.end());
	}
	
	private void syntaxError(String msg, Expr e) {
		SourceAttr loc = e.attribute(SourceAttr.class);
		throw new ParseError(msg, filename, loc.start(), loc.end());
	}
	
	private void syntaxError(String msg, Token t) {
		throw new ParseError(msg, filename, t.start, t.start + t.text.length() - 1);
	}		
}
