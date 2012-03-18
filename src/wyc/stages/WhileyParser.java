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

package wyc.stages;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.Path;
import wybs.lang.SyntaxError;
import wybs.util.Trie;
import wyc.lang.*;
import wyc.lang.WhileyFile.*;
import wyc.util.*;
import wyil.lang.*;
import wyil.util.*;
import wyjc.runtime.BigRational;
import wyjvm.lang.Bytecode;

import static wyc.stages.WhileyLexer.*;

/**
 * Convert a list of tokens into an Abstract Syntax Tree (AST) representing the
 * original source file in question. No effort is made to check whether or not
 * the generated tree is syntactically correct. Subsequent stages of the
 * compiler are responsible for doing this.
 * 
 * @author David J. Pearce
 * 
 */
public final class WhileyParser {
	private String filename;
	private ArrayList<Token> tokens;	
	private int index;
	
	public WhileyParser(String filename, List<Token> tokens) {
		this.filename = filename;
		this.tokens = new ArrayList<Token>(tokens); 		
	}
	public WhileyFile read() {		
		Path.ID pkg = parsePackage();

		// Now, figure out module name from filename
		String name = filename.substring(filename.lastIndexOf(File.separatorChar) + 1,filename.length()-7);		
		WhileyFile wf = new WhileyFile(pkg.append(name),filename);
		
		while(index < tokens.size()) {			
			Token t = tokens.get(index);
			if (t instanceof NewLine || t instanceof LineComment|| t instanceof BlockComment) {
				matchEndLine();
			} else if(t instanceof Keyword) {
				Keyword k = (Keyword) t;
				if(k.text.equals("import")) {					
					parseImport(wf);
				} else {
					List<Modifier> modifiers = parseModifiers();
					
					t = tokens.get(index);
					
					if (t.text.equals("define")) {
						parseDefType(modifiers, wf);
					} else {
						parseFunctionOrMethodOrMessage(modifiers, wf);
					} 
				}
			} else {
				parseFunctionOrMethodOrMessage(new ArrayList<Modifier>(),wf);				
			}			
		}
		
		return wf;
	}
	
	private Trie parsePackage() {
		
		while (index < tokens.size()
				&& (tokens.get(index) instanceof LineComment || tokens.get(index) instanceof NewLine)) {			
			parseSkip();
		}
		
		Trie pkg = Trie.ROOT;
		
		if(index < tokens.size() && tokens.get(index).text.equals("package")) {			
			matchKeyword("package");
			
			pkg = pkg.append(matchIdentifier().text);
						
			while (index < tokens.size() && tokens.get(index) instanceof Dot) {
				match(Dot.class);
				pkg = pkg.append(matchIdentifier().text);
			}

			matchEndLine();
			return pkg;
		} else {
			return pkg; // no package
		}
	}
	
	private void parseImport(WhileyFile wf) {
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
		matchEndLine();
		
		wf.add(new Import(filter, name, sourceAttr(start,
				end - 1)));
	}
	
	private void parseFunctionOrMethodOrMessage(List<Modifier> modifiers, WhileyFile wf) {			
		int start = index;		
		UnresolvedType ret = parseType();				
		// FIXME: potential bug here at end of file		
		UnresolvedType receiver = null;
		boolean method = false;
		boolean message = false;	
		
		if(tokens.get(index) instanceof ColonColon) {
			// headless method
			method = true;
			match(ColonColon.class);
		} else if(tokens.get(index+1) instanceof ColonColon) {
			message = true;
			receiver = parseType();			
			match(ColonColon.class);							
		}
		
		Identifier name = matchIdentifier();						
		
		match(LeftBrace.class);		
		
		// Now build up the parameter types
		List<Parameter> paramTypes = new ArrayList();
		HashSet<String> paramNames = new HashSet<String>();
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
			if(paramNames.contains(n.text)) {
				syntaxError("duplicate parameter name",n);
			} else if(!n.text.equals("$") && !n.text.equals("this")){
				paramNames.add(n.text);
			} else {
				syntaxError("parameter name not permitted",n);
			}
			paramTypes.add(wf.new Parameter(t, n.text, sourceAttr(pstart,
					index - 1)));
		}
		
		match(RightBrace.class);	
		Pair<Expr,Expr> conditions = parseRequiresEnsures();	
		UnresolvedType throwType = parseThrowsClause();
		match(Colon.class);
		int end = index;
		matchEndLine();
		
		List<Stmt> stmts = parseBlock(1);
		Declaration declaration;
		if(message) {
			declaration = wf.new Message(modifiers, name.text, receiver, ret, paramTypes,
					conditions.first(), conditions.second(), throwType, stmts,
					sourceAttr(start, end - 1));
		} else if(method) {
			declaration = wf.new Method(modifiers, name.text, ret, paramTypes,
					conditions.first(), conditions.second(), throwType, stmts,
					sourceAttr(start, end - 1));
		}else {
			declaration = wf.new Function(modifiers, name.text, ret, paramTypes,
					conditions.first(), conditions.second(), throwType, stmts,
					sourceAttr(start, end - 1));
		}
		wf.add(declaration);
	}
	
	private void parseDefType(List<Modifier> modifiers, WhileyFile wf) {		
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
			if (index < tokens.size() && tokens.get(index).text.equals("where")) {
				// this is a constrained type
				matchKeyword("where");
				
				constraint = parseCondition(false);
			}
			int end = index;			
			matchEndLine();			
			Declaration declaration = wf.new TypeDef(modifiers, t, name.text, constraint, sourceAttr(start,end-1));
			wf.add(declaration);
			return;
		} catch(Exception e) {}
		
		// Ok, failed parsing type constructor. So, backtrack and try for
		// expression.
		index = mid;		
		Expr e = parseCondition(false);
		int end = index;
		matchEndLine();		
		Declaration declaration = wf.new Constant(modifiers, e, name.text, sourceAttr(start,end-1));
		wf.add(declaration);
	}
	
	private List<Modifier> parseModifiers() {
		ArrayList<Modifier> mods = new ArrayList<Modifier>();
		Token lookahead;
		while (index < tokens.size()
				&& isModifier((lookahead = tokens.get(index)))) {
			if(lookahead.text.equals("public")) {
				mods.add(Modifier.PUBLIC);
			} else if(lookahead.text.equals("native")) {
				mods.add(Modifier.NATIVE);
			} else if(lookahead.text.equals("export")) {
				mods.add(Modifier.EXPORT);
			} 
			index = index + 1;
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
	
	private void parseIndent(int indent) {
		if(index < tokens.size()) {
			Token t = tokens.get(index);
			if(t instanceof Tabs && ((Tabs)t).ntabs == indent) {
				index = index + 1;	
			} else {
				syntaxError("unexpected end-of-block",t);	
			}
		} else {
			throw new SyntaxError("unexpected end-of-file",filename,index,index);
		}		
	}
	
	private Tabs getIndent() {
		// FIXME: there's still a bug here for empty lines with arbitrary tabs
		if (index < tokens.size() && tokens.get(index) instanceof Tabs) {
			return (Tabs) tokens.get(index);
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof LineComment) {
			// This indicates a completely empty line. In which case, we just
			// ignore it.
			matchEndLine();
			return getIndent();
		} else {
			return null;
		}
	}
	
	private UnresolvedType parseThrowsClause() {
		checkNotEof();
		if (index < tokens.size() && tokens.get(index).text.equals("throws")) {
			matchKeyword("throws");
			return parseType();
		}
		return new UnresolvedType.Void();
	}
	private Pair<Expr, Expr> parseRequiresEnsures() {
		
		checkNotEof();
		if (index < tokens.size() && tokens.get(index).text.equals("requires")) {
			// this is a constrained type
			matchKeyword("requires");
			Expr pre = parseCondition(false);
			Expr post = null;
			if (index < tokens.size() && tokens.get(index) instanceof Comma) {
				match(Comma.class);
				matchKeyword("ensures");
				post = parseCondition(false);
			}
			return new Pair<Expr, Expr>(pre, post);
		} else if (index < tokens.size()
				&& tokens.get(index).text.equals("ensures")) {
			// this is a constrained type
			matchKeyword("ensures");
			return new Pair<Expr, Expr>(null, parseCondition(false));
		} else {
			return new Pair<Expr, Expr>(null, null);
		}
	}
	
	private Stmt parseStatement(int indent) {		
		checkNotEof();
		Token token = tokens.get(index);
		
		if(token.text.equals("skip")) {
			return parseSkip();
		} else if(token.text.equals("return")) {
			return parseReturn();
		} else if(token.text.equals("assert")) {
			return parseAssert();
		} else if(token.text.equals("debug")) {
			return parseDebug();
		} else if(token.text.equals("if")) {			
			return parseIf(indent);
		} else if(token.text.equals("switch")) {			
			return parseSwitch(indent);
		} else if(token.text.equals("try")) {			
			return parseTryCatch(indent);
		} else if(token.text.equals("break")) {			
			return parseBreak(indent);
		} else if(token.text.equals("throw")) {			
			return parseThrow(indent);
		} else if(token.text.equals("do")) {			
			return parseDoWhile(indent);
		} else if(token.text.equals("while")) {			
			return parseWhile(indent);
		} else if(token.text.equals("for")) {			
			return parseFor(indent);
		} else if(token.text.equals("new")) {			
			return parseNew();
		} else if ((index + 1) < tokens.size()
				&& tokens.get(index + 1) instanceof LeftBrace) {
			// must be a method invocation
			return parseInvokeStmt();			
		} else {
			int start = index;
			Expr t = parseTupleExpression();
			if(t instanceof Expr.AbstractInvoke) {
				matchEndLine();
				return (Expr.AbstractInvoke) t;
			} else {
				index = start;
				return parseAssign();
			}
		}
	}		
	
	private Expr.AbstractInvoke parseInvokeStmt() {				
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
			Expr e = parseBitwiseExpression(false);
			args.add(e);
			
		}
		match(RightBrace.class);
		int end = index;
		matchEndLine();				
		
		// no receiver is possible in this case.
		return new Expr.AbstractInvoke(name.text, null, args, false, sourceAttr(start,end-1));
	}
	
	private Stmt parseReturn() {
		int start = index;
		matchKeyword("return");
		Expr e = null;
		if (index < tokens.size()
				&& !(tokens.get(index) instanceof NewLine || tokens.get(index) instanceof LineComment)) {
			e = parseTupleExpression();
		}
		int end = index;
		matchEndLine();
		return new Stmt.Return(e, sourceAttr(start, end - 1));
	}
	
	private Stmt parseAssert() {
		int start = index;
		matchKeyword("assert");						
		checkNotEof();
		Expr e = parseCondition(false);
		int end = index;
		matchEndLine();		
		return new Stmt.Assert(e, sourceAttr(start,end));
	}
	
	private Stmt parseSkip() {
		int start = index;
		matchKeyword("skip");
		matchEndLine();		
		return new Stmt.Skip(sourceAttr(start,index-1));
	}
	
	private Stmt parseDebug() {		
		int start = index;
		matchKeyword("debug");		
		checkNotEof();
		Expr e = parseBitwiseExpression(false);
		int end = index;
		matchEndLine();		
		return new Stmt.Debug(e, sourceAttr(start,end-1));
	}
	
	private Stmt parseIf(int indent) {
		int start = index;
		matchKeyword("if");						
		Expr c = parseCondition(false);								
		match(Colon.class);
		int end = index;
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
		
		return new Stmt.IfElse(c,tblk,fblk, sourceAttr(start,end-1));
	}
	
	private Stmt.Case parseCase(int indent) {
		checkNotEof();
		int start = index;
		List<Expr> values;
		if(index < tokens.size() && tokens.get(index).text.equals("default")) {				
			matchKeyword("default");
			values = Collections.EMPTY_LIST;			
		} else {
			matchKeyword("case");
			values = new ArrayList<Expr>();
			values.add(parseCondition(false));
			while(index < tokens.size() && tokens.get(index) instanceof Comma) {				
				match(Comma.class);
				values.add(parseCondition(false));
			}
		}		
		match(Colon.class);
		int end = index;
		matchEndLine();		
		List<Stmt> stmts = parseBlock(indent+1);
		return new Stmt.Case(values,stmts,sourceAttr(start,end-1));
	}
	
	private ArrayList<Stmt.Case> parseCaseBlock(int indent) {
		Tabs tabs = null;
		
		tabs = getIndent();
		
		ArrayList<Stmt.Case> cases = new ArrayList<Stmt.Case>();
		while(tabs != null && tabs.ntabs >= indent) {
			index = index + 1;
			cases.add(parseCase(indent));			
			tabs = getIndent();			
		}
		
		return cases;
	}
	
	private Stmt parseSwitch(int indent) {
		int start = index;
		matchKeyword("switch");
		Expr c = parseBitwiseExpression(false);								
		match(Colon.class);
		int end = index;
		matchEndLine();
		ArrayList<Stmt.Case> cases = parseCaseBlock(indent+1);		
		return new Stmt.Switch(c, cases, sourceAttr(start,end-1));
	}
	
	private Stmt.Catch parseCatch(int indent) {
		checkNotEof();
		int start = index;		
		matchKeyword("catch");
		match(LeftBrace.class);
		UnresolvedType type = parseType();
		String variable = matchIdentifier().text;
		match(RightBrace.class);
		match(Colon.class);
		int end = index;
		matchEndLine();		
		List<Stmt> stmts = parseBlock(indent+1);
		return new Stmt.Catch(type,variable,stmts,sourceAttr(start,end-1));
	}
	
	private ArrayList<Stmt.Catch> parseCatchBlock(int indent) {
		Tabs tabs = null;
		
		tabs = getIndent();
		
		ArrayList<Stmt.Catch> catches = new ArrayList<Stmt.Catch>();
		while(tabs != null && tabs.ntabs >= indent) {
			index = index + 1; // skip tabs
			if(index < tokens.size() && tokens.get(index).text.equals("catch")) {				
				catches.add(parseCatch(indent));			
				tabs = getIndent();
			} else {
				index = index - 1; // undo
				break;
			}
		}
		
		return catches;
	}
	
	private Stmt parseTryCatch(int indent) {
		int start = index;
		matchKeyword("try");									
		match(Colon.class);
		int end = index;
		matchEndLine();
		List<Stmt> blk = parseBlock(indent+1);
		List<Stmt.Catch> catches = parseCatchBlock(indent);		
		return new Stmt.TryCatch(blk, catches, sourceAttr(start,end-1));
	}
	
	private Stmt parseThrow(int indent) {
		int start = index;
		matchKeyword("throw");
		Expr c = parseBitwiseExpression(false);
		int end = index;
		matchEndLine();		
		return new Stmt.Throw(c,sourceAttr(start,end-1));
	}
	
	private Stmt parseBreak(int indent) {
		int start = index;
		matchKeyword("break");
		int end = index;
		matchEndLine();		
		return new Stmt.Break(sourceAttr(start,end-1));
	}
	
	private Stmt parseWhile(int indent) {
		int start = index;
		matchKeyword("while");						
		Expr condition = parseCondition(false);
		Expr invariant = null;
		if (tokens.get(index).text.equals("where")) {
			matchKeyword("where");
			invariant = parseCondition(false);
		}
		match(Colon.class);
		int end = index;
		matchEndLine();
		List<Stmt> blk = parseBlock(indent+1);								
		
		return new Stmt.While(condition,invariant,blk, sourceAttr(start,end-1));
	}
	
	private Stmt parseDoWhile(int indent) {
		int start = index;
		matchKeyword("do");						
		Expr invariant = null;
		if (tokens.get(index).text.equals("where")) {
			matchKeyword("where");
			invariant = parseCondition(false);
		}
		match(Colon.class);
		int end = index;
		matchEndLine();
		List<Stmt> blk = parseBlock(indent+1);								
		parseIndent(indent);
		matchKeyword("while");
		Expr condition = parseCondition(false);
		matchEndLine();
		
		return new Stmt.DoWhile(condition,invariant,blk, sourceAttr(start,end-1));
	}
	
	private Stmt parseFor(int indent) {
		int start = index;
		matchKeyword("for");				
		ArrayList<String> variables = new ArrayList<String>();
		variables.add(matchIdentifier().text);				
		if(index < tokens.size() && tokens.get(index) instanceof Comma) {
			match(Comma.class);
			variables.add(matchIdentifier().text);
		}
		match(ElemOf.class);
		Expr source = parseCondition(false);		
		Expr invariant = null;
		if(tokens.get(index).text.equals("where")) {
			matchKeyword("where");
			invariant = parseCondition(false);
		}
		match(Colon.class);
		int end = index;
		matchEndLine();
		List<Stmt> blk = parseBlock(indent+1);								

		return new Stmt.ForAll(variables,source,invariant,blk, sourceAttr(start,end-1));
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
	
	private Stmt parseAssign() {		
		// standard assignment
		int start = index;
		Expr lhs = parseTupleExpression();		
		if(!(lhs instanceof Expr.LVal)) {
			syntaxError("expecting lval, found " + lhs + ".", lhs);
		}				
		match(Equals.class);		
		Expr rhs = parseCondition(false);
		int end = index;
		matchEndLine();
		return new Stmt.Assign((Expr.LVal) lhs, rhs, sourceAttr(start,
				end - 1));		
	}	
	

	private Expr parseTupleExpression() {
		int start = index;
		Expr e = parseCondition(false);		
		if (index < tokens.size() && tokens.get(index) instanceof Comma) {
			// this is a tuple constructor
			ArrayList<Expr> exprs = new ArrayList<Expr>();
			exprs.add(e);
			while (index < tokens.size() && tokens.get(index) instanceof Comma) {
				match(Comma.class);
				exprs.add(parseCondition(false));
				checkNotEof();
			}
			return new Expr.Tuple(exprs,sourceAttr(start,index-1));
		} else {
			return e;
		}
	}

	/**
	 * The startSetComp flag is used to indicate whether this expression is the
	 * first first value of a set expression. This is necessary in order to
	 * disambiguate bitwise or from set comprehension. For example, consider
	 * this expression:
	 * 
	 * <pre>
	 * { x | x in xs, y == x }
	 * </pre>
	 * 
	 * To realise that this expression is a set comprehension, we must know that
	 * <code>x in xs</code> results a boolean type and, hence, cannot be
	 * converted into a <code>byte</code>. This kind of knowledge is beyond what
	 * the parser knows at this stage. Therefore, the first item of any set
	 * expression will not greedily consume the <code>|</code>. This means that,
	 * if we want a boolean set which uses bitwise or, then we have to
	 * disambiguate with braces like so:
	 * <pre>
	 * { (x|y), z } // valid set expression, not set comprehension
	 * </pre> 
	 * @param startSetComp
	 * @return
	 */
	private Expr parseCondition(boolean startSet) {
		checkNotEof();
		int start = index;		
		Expr c1 = parseConditionExpression(startSet);		
		
		if(index < tokens.size() && tokens.get(index) instanceof LogicalAnd) {			
			match(LogicalAnd.class);
			
			
			Expr c2 = parseCondition(startSet);			
			return new Expr.BinOp(Expr.BOp.AND, c1, c2, sourceAttr(start,
					index - 1));
		} else if(index < tokens.size() && tokens.get(index) instanceof LogicalOr) {
			match(LogicalOr.class);
			
			
			Expr c2 = parseCondition(startSet);
			return new Expr.BinOp(Expr.BOp.OR, c1, c2, sourceAttr(start,
					index - 1));			
		} 
		return c1;		
	}
		
	private Expr parseConditionExpression(boolean startSet) {		
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
		
		Expr lhs = parseBitwiseExpression(startSet);
		
		if (index < tokens.size() && tokens.get(index) instanceof LessEquals) {
			match(LessEquals.class);				
			
			
			Expr rhs = parseBitwiseExpression(startSet);
			return new Expr.BinOp(Expr.BOp.LTEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof LeftAngle) {
 			match(LeftAngle.class);				
 			
 			
 			Expr rhs = parseBitwiseExpression(startSet);
			return new Expr.BinOp(Expr.BOp.LT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof GreaterEquals) {
			match(GreaterEquals.class);	
			
			
			Expr rhs = parseBitwiseExpression(startSet);
			return new Expr.BinOp(Expr.BOp.GTEQ,  lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof RightAngle) {
			match(RightAngle.class);			
			
			
			Expr rhs = parseBitwiseExpression(startSet);
			return new Expr.BinOp(Expr.BOp.GT, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof EqualsEquals) {
			match(EqualsEquals.class);			
			
			
			Expr rhs = parseBitwiseExpression(startSet);
			return new Expr.BinOp(Expr.BOp.EQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof NotEquals) {
			match(NotEquals.class);									
			Expr rhs = parseBitwiseExpression(startSet);			
			return new Expr.BinOp(Expr.BOp.NEQ, lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.InstanceOf) {
			return parseTypeEquals(lhs,start);			
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.ElemOf) {
			match(WhileyLexer.ElemOf.class);									
			Expr rhs = parseBitwiseExpression(startSet);
			return new Expr.BinOp(Expr.BOp.ELEMENTOF,lhs,  rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.SubsetEquals) {
			match(WhileyLexer.SubsetEquals.class);									
			Expr rhs = parseBitwiseExpression(startSet);
			return new Expr.BinOp(Expr.BOp.SUBSETEQ, lhs, rhs, sourceAttr(start,index-1));
		} else if (index < tokens.size() && tokens.get(index) instanceof WhileyLexer.Subset) {
			match(WhileyLexer.Subset.class);									
			Expr rhs = parseBitwiseExpression(startSet);
			return new Expr.BinOp(Expr.BOp.SUBSET, lhs,  rhs, sourceAttr(start,index-1));
		} else {
			return lhs;
		}	
	}
	
	private Expr parseTypeEquals(Expr lhs, int start) {
		match(WhileyLexer.InstanceOf.class);			
				
		UnresolvedType type = parseType();
		Expr.TypeVal tc = new Expr.TypeVal(type, sourceAttr(start, index - 1));				
		
		return new Expr.BinOp(Expr.BOp.IS, lhs, tc, sourceAttr(start,
				index - 1));
	}
	
	private static boolean isBitwiseTok(Token tok, boolean startSet) {
		return tok instanceof Ampersand || tok instanceof Caret || (!startSet && tok instanceof Bar);
	}
	
	private static Expr.BOp bitwiseOp(Token tok) {
		if(tok instanceof Ampersand) {
			return Expr.BOp.BITWISEAND;
		} else if(tok instanceof Bar) {
			return Expr.BOp.BITWISEOR;
		} else {
			return Expr.BOp.BITWISEXOR;
		} 		
	}
	
	private Expr parseBitwiseExpression(boolean startSet) {
		int start = index;
		ArrayList<Expr> exprs = new ArrayList<Expr>();
		ArrayList<Expr.BOp> ops = new ArrayList<Expr.BOp>();
		ArrayList<Integer> ends = new ArrayList<Integer>();
		exprs.add(parseShiftExpression());
		
		while(index < tokens.size() && isBitwiseTok(tokens.get(index),startSet)) {
			Token token = tokens.get(index);
			match(token.getClass());
			ops.add(bitwiseOp(token));
			exprs.add(parseShiftExpression());	
			ends.add(index-1);
		}
		
		Expr result = exprs.get(0);
		
		for(int i=1;i<exprs.size();++i) {
			Expr rhs = exprs.get(i);
			Expr.BOp bop = ops.get(i-1);			
			result = new Expr.BinOp(bop, result, rhs,  sourceAttr(start,
					ends.get(i-1)));
		}
		
		return result;
	}
	
	private static boolean isShiftTok(Token tok) {
		return tok instanceof LeftLeftAngle || tok instanceof RightRightAngle;
	}
	
	private static Expr.BOp shiftOp(Token tok) {
		if(tok instanceof LeftLeftAngle) {
			return Expr.BOp.LEFTSHIFT;
		} else {
			return Expr.BOp.RIGHTSHIFT;
		} 
	}
	
	private Expr parseShiftExpression() {
		int start = index;
		ArrayList<Expr> exprs = new ArrayList<Expr>();
		ArrayList<Expr.BOp> ops = new ArrayList<Expr.BOp>();
		ArrayList<Integer> ends = new ArrayList<Integer>();
		exprs.add(parseRangeExpression());
		
		while(index < tokens.size() && isShiftTok(tokens.get(index))) {
			Token token = tokens.get(index);
			match(token.getClass());
			ops.add(shiftOp(token));
			exprs.add(parseRangeExpression());	
			ends.add(index);
		}
		
		Expr result = exprs.get(0);
		
		for(int i=1;i<exprs.size();++i) {
			Expr rhs = exprs.get(i);
			Expr.BOp bop = ops.get(i-1);			
			result = new Expr.BinOp(bop, result, rhs,  sourceAttr(start,
					ends.get(i-1)));
		}
		
		return result;
	}
	
	private Expr parseRangeExpression() {
		int start = index;			
		Expr lhs = parseAddSubExpression();		
		
		if(index < tokens.size() && tokens.get(index) instanceof DotDot) {			
			match(DotDot.class);
			Expr rhs = parseAddSubExpression();
			return new Expr.BinOp(Expr.BOp.RANGE, lhs, rhs, sourceAttr(start,
					index - 1));
		} else {		
			return lhs;
		}
	}
	
	private static boolean isAddSubTok(Token tok) {
		return tok instanceof Plus || tok instanceof Minus
				|| tok instanceof Union || tok instanceof Intersection;
	}
	
	private static Expr.BOp addSubOp(Token tok) {
		if(tok instanceof Plus) {
			return Expr.BOp.ADD;
		} else if(tok instanceof Minus) {
			return Expr.BOp.SUB;
		} else if(tok instanceof Union) {
			return Expr.BOp.UNION;
		} else {
			return Expr.BOp.INTERSECTION;
		} 
	}
	
	private Expr parseAddSubExpression() {
		int start = index;
		ArrayList<Expr> exprs = new ArrayList<Expr>();
		ArrayList<Expr.BOp> ops = new ArrayList<Expr.BOp>();
		ArrayList<Integer> ends = new ArrayList<Integer>();
		exprs.add(parseMulDivExpression());
		
		while(index < tokens.size() && isAddSubTok(tokens.get(index))) {
			Token token = tokens.get(index);
			match(token.getClass());
			ops.add(addSubOp(token));
			exprs.add(parseMulDivExpression());	
			ends.add(index-1);
		}
		
		Expr result = exprs.get(0);
		
		for(int i=1;i<exprs.size();++i) {
			Expr rhs = exprs.get(i);
			Expr.BOp bop = ops.get(i-1);			
			result = new Expr.BinOp(bop, result, rhs,  sourceAttr(start,
					ends.get(i-1)));
		}
		
		return result;
	}		
		
	private static boolean isMulDivTok(Token t) {
		return t instanceof Star || t instanceof RightSlash || t instanceof Percent;
	}
	
	private Expr.BOp mulDivOp(Token t) {
		if(t instanceof Star) {
			return Expr.BOp.MUL;
		} else if(t instanceof RightSlash) {
			return Expr.BOp.DIV;
		} else {
			return Expr.BOp.REM;
		}
	}
	
	private Expr parseMulDivExpression() {
		int start = index;
		ArrayList<Expr> exprs = new ArrayList<Expr>();
		ArrayList<Expr.BOp> ops = new ArrayList<Expr.BOp>();
		ArrayList<Integer> ends = new ArrayList<Integer>();
		exprs.add(parseCastExpression());
		
		while(index < tokens.size() && isMulDivTok(tokens.get(index))) {
			Token token = tokens.get(index);
			match(token.getClass());
			ops.add(mulDivOp(token));
			exprs.add(parseCastExpression());	
			ends.add(index);
		}
		
		Expr result = exprs.get(0);
		
		for(int i=1;i<exprs.size();++i) {
			Expr rhs = exprs.get(i);
			Expr.BOp bop = ops.get(i-1);			
			result = new Expr.BinOp(bop, result, rhs,  sourceAttr(start,
					ends.get(i-1)));
		}
		
		return result;		
	}	
	
	private Expr parseCastExpression() {
		Token lookahead = tokens.get(index);
		if(lookahead instanceof LeftBrace) {
			int start = index;
			try {
				match(LeftBrace.class);
				UnresolvedType type = parseType();
				match(RightBrace.class);
				Expr expr = parseIndexTerm();
				return new Expr.Convert(type, expr, sourceAttr(start,
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
		Expr lhs = parseTerm();
		
		if(index < tokens.size()) {
			Token lookahead = tokens.get(index);

			while (lookahead instanceof LeftSquare 
					|| lookahead instanceof Dot
					|| lookahead instanceof Question		
					|| lookahead instanceof Shreak
					|| lookahead instanceof RightArrow
					|| lookahead instanceof LeftBrace) {				
				if(lookahead instanceof LeftSquare) {
					match(LeftSquare.class);				

					lookahead = tokens.get(index);

					if (lookahead instanceof DotDot) {
						// this indicates a sublist without a starting expression;
						// hence, start point defaults to zero
						match(DotDot.class);

						lookahead = tokens.get(index);
						Expr end = parseAddSubExpression();
						match(RightSquare.class);
						return new Expr.SubList(lhs, new Expr.Constant(
								Value.V_INTEGER(BigInteger.ZERO), sourceAttr(
										start, index - 1)), end, sourceAttr(
								start, index - 1));
					}

					Expr rhs = parseAddSubExpression();

					lookahead = tokens.get(index);
					if(lookahead instanceof DotDot) {					
						match(DotDot.class);

						lookahead = tokens.get(index);
						Expr end;
						if(lookahead instanceof RightSquare) {
							// In this case, no end of the slice has been provided.
							// Therefore, it is taken to be the length of the source
							// expression.						
							end = new Expr.LengthOf(lhs, lhs
									.attribute(Attribute.Source.class));
						} else {
							end = parseBitwiseExpression(false);						
						}
						match(RightSquare.class);
						lhs = new Expr.SubList(lhs, rhs, end, sourceAttr(start,
								index - 1));
					} else {
						match(RightSquare.class);							
						lhs = new Expr.IndexOf(lhs, rhs, sourceAttr(start,
								index - 1));
					}
				} else if(lookahead instanceof Dot || lookahead instanceof RightArrow) {				
					if(lookahead instanceof Dot) {
						match(Dot.class);
					} else {
						match(RightArrow.class);
						lhs = new Expr.Dereference(lhs,sourceAttr(start,index - 1));	
					}
					int tmp = index;
					String name = matchIdentifier().text; 	
					if(index < tokens.size() && tokens.get(index) instanceof LeftBrace) {
						// this indicates a method invocation.
						index = tmp; // slight backtrack
						Expr.AbstractInvoke<?> ivk = parseInvokeExpr();							
						lhs = new Expr.AbstractInvoke(ivk.name, lhs, ivk.arguments,
								true, sourceAttr(
										start, index - 1));				
					} else {
						lhs =  new Expr.AbstractDotAccess(lhs, name, sourceAttr(start,index - 1));
					}
				} else if(lookahead instanceof Question) {
					match(Question.class);								 						
					Expr.AbstractInvoke<?> ivk = parseInvokeExpr();							
					lhs = new Expr.AbstractInvoke(ivk.name, lhs, ivk.arguments,
							true, sourceAttr(
									start, index - 1));								
				} else {
					match(Shreak.class);								 						
					Expr.AbstractInvoke<?> ivk = parseInvokeExpr();							
					lhs = new Expr.AbstractInvoke(ivk.name, lhs, ivk.arguments,
							false, sourceAttr(
									start, index - 1));								
				}
				if(index < tokens.size()) {
					lookahead = tokens.get(index);	
				} else {
					lookahead = null;
				}
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
			
			checkNotEof();			
			Expr v = parseTupleExpression();			
			
			checkNotEof();
			token = tokens.get(index);			
			match(RightBrace.class);
			return v;			 		
		} else if(token instanceof Star) {
			// this indicates a process dereference
			match(Star.class);
			
			Expr e = parseTerm();
			return new Expr.Dereference(e, sourceAttr(start,
					index - 1));
		} else if ((index + 1) < tokens.size()
				&& token instanceof Identifier
				&& tokens.get(index + 1) instanceof LeftBrace) {				
			// must be a method invocation			
			return parseInvokeExpr();
		} else if (token.text.equals("null")) {
			matchKeyword("null");			
			return new Expr.Constant(Value.V_NULL,
					sourceAttr(start, index - 1));
		} else if (token.text.equals("true")) {
			matchKeyword("true");			
			return new Expr.Constant(Value.V_BOOL(true),
					sourceAttr(start, index - 1));
		} else if (token.text.equals("false")) {	
			matchKeyword("false");
			return new Expr.Constant(Value.V_BOOL(false),
					sourceAttr(start, index - 1));			
		} else if(token.text.equals("new")) {
			return parseNew();			
		} else if (token instanceof Identifier) {
			return new Expr.AbstractVariable(matchIdentifier().text, sourceAttr(start,
					index - 1));			
		} else if (token instanceof WhileyLexer.Byte) {			
			byte val = match(WhileyLexer.Byte.class).value;
			return new Expr.Constant(Value.V_BYTE(val), sourceAttr(start, index - 1));
		} else if (token instanceof Char) {			
			char val = match(Char.class).value;
			return new Expr.Constant(Value.V_CHAR(val), sourceAttr(start, index - 1));
		} else if (token instanceof Int) {			
			BigInteger val = match(Int.class).value;
			return new Expr.Constant(Value.V_INTEGER(val), sourceAttr(start, index - 1));
		} else if (token instanceof Real) {
			BigRational val = match(Real.class).value;
			return new Expr.Constant(Value.V_RATIONAL(val), sourceAttr(start,
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
			return new Expr.UnOp(Expr.UOp.NOT, parseIndexTerm(),
					sourceAttr(start, index - 1));
		} else if (token instanceof Tilde) {
			match(Tilde.class);
			return new Expr.UnOp(Expr.UOp.INVERT, parseIndexTerm(),
					sourceAttr(start, index - 1));
		} else if (token instanceof Ampersand) {
		      return parseFunVal();
	    }
		syntaxError("unrecognised term (" + token.text + ")",token);
		return null;		
	}
	
	private Expr parseFunVal() {
		int start = index;
		match(Ampersand.class);
		String funName = matchIdentifier().text;
		ArrayList<UnresolvedType> paramTypes = null;

		if (tokens.get(index) instanceof LeftBrace) {
			// parse parameter types
			paramTypes = new ArrayList<UnresolvedType>();
			match(LeftBrace.class);
			boolean firstTime = true;
			while (index < tokens.size()
					&& !(tokens.get(index) instanceof RightBrace)) {
				if (!firstTime) {
					match(Comma.class);
				}
				firstTime = false;
				UnresolvedType ut = parseType();
				paramTypes.add(ut);
			}
			match(RightBrace.class);
		}
		return new Expr.AbstractFunctionOrMethodOrMessage(funName, paramTypes, sourceAttr(start, index - 1));			
	}

	private Expr.New parseNew() {
		int start = index;
		matchKeyword("new");
		
		Expr state = parseBitwiseExpression(false);
		return new Expr.New(state, sourceAttr(start,index - 1));
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
			exprs.add(parseCondition(false));
			
			checkNotEof();
			token = tokens.get(index);
		}
		match(RightSquare.class);
		return new Expr.List(exprs, sourceAttr(start, index - 1));
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
			
			Expr src = parseConditionExpression(true);			
			srcs.add(new Pair(var,src));
			
			checkNotEof();
			token = tokens.get(index);
		}
		match(Bar.class);		
		Expr condition = parseCondition(false);
		
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
			Value v = Value.V_SET(Collections.EMPTY_LIST); 
			return new Expr.Constant(v, sourceAttr(start, index - 1));
		} else if(token instanceof StrongRightArrow) {
			match(StrongRightArrow.class);		
			match(RightCurly.class);			
			// empty dictionary definition
			Value v = Value.V_DICTIONARY(Collections.EMPTY_SET); 
			return new Expr.Constant(v, sourceAttr(start, index - 1));
		}
		
		// NOTE. need to indicate this is the start of a set expression. This is
		// necessary to ensure that the <code|</code> operator is not consumed
		// as a bitwise or.
		exprs.add(parseCondition(true)); 
		
		
		boolean setComp = false;
		boolean firstTime = false;
		if (index < tokens.size() && tokens.get(index) instanceof Bar) { 
			// this is a set comprehension
			setComp=true;
			match(Bar.class);
			firstTime=true;
		} else if(index < tokens.size() && tokens.get(index) instanceof StrongRightArrow) {
			// this is a dictionary constructor					
			return parseDictionaryVal(start,exprs.get(0));
		} else if (index < tokens.size() && tokens.get(index) instanceof Colon
				&& exprs.get(0) instanceof Expr.AbstractVariable) {
			// this is a record constructor
			Expr.AbstractVariable v = (Expr.AbstractVariable)exprs.get(0); 
			return parseRecordVal(start,v.var);
		}
		
		checkNotEof();
		token = tokens.get(index);
		while(!(token instanceof RightCurly)) {						
			if(!firstTime) {
				match(Comma.class);				
			}
			firstTime=false;
			exprs.add(parseCondition(false));
			
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
							&& eof.lhs instanceof Expr.AbstractVariable) {
						String var = ((Expr.AbstractVariable) eof.lhs).var;
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
			return new Expr.Set(exprs, sourceAttr(start, index - 1));
		}
	}
	
	private Expr parseDictionaryVal(int start, Expr key) {
		ArrayList<Pair<Expr,Expr>> pairs = new ArrayList<Pair<Expr,Expr>>();		
		match(StrongRightArrow.class);
		Expr value = parseCondition(false);	
		pairs.add(new Pair<Expr,Expr>(key,value));
		
		Token token = tokens.get(index);		
		while(!(token instanceof RightCurly)) {									
			match(Comma.class);
			
			key = parseCondition(false);
			match(StrongRightArrow.class);
			value = parseCondition(false);
			pairs.add(new Pair<Expr,Expr>(key,value));
			
			checkNotEof();
			token = tokens.get(index);
		}
		match(RightCurly.class);
		return new Expr.Dictionary(pairs,sourceAttr(start, index - 1));
	}
	
	private Expr parseRecordVal(int start, String ident) {

		// this indicates a record value.				
		match(Colon.class);
		
		Expr e = parseBitwiseExpression(false);
		
		
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
			
			e = parseBitwiseExpression(false);				
			exprs.put(n.text,e);
			checkNotEof();
			token = tokens.get(index);					
		} 
		match(RightCurly.class);

		return new Expr.Record(exprs,sourceAttr(start, index - 1));
	} 
	
	private Expr parseLengthOf() {
		int start = index;
		match(Bar.class);
		
		Expr e = parseIndexTerm();
		
		match(Bar.class);
		return new Expr.LengthOf(e, sourceAttr(start, index - 1));
	}

	private Expr parseNegation() {
		int start = index;
		match(Minus.class);
		
		Expr e = parseIndexTerm();
		
		if(e instanceof Expr.Constant) {
			Expr.Constant c = (Expr.Constant) e;
			if (c.value instanceof Value.Rational) {
				BigRational br = ((Value.Rational) c.value).value;
				return new Expr.Constant(Value.V_RATIONAL(br.negate()),
						sourceAttr(start, index));
			}
		} 
		
		return new Expr.UnOp(Expr.UOp.NEG, e, sourceAttr(start, index));		
	}

	private Expr.AbstractInvoke parseInvokeExpr() {		
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
			Expr e = parseBitwiseExpression(false);
			
			args.add(e);		
		}
		match(RightBrace.class);		
		return new Expr.AbstractInvoke(name.text, null, args, false, sourceAttr(start,index-1));
	}
	
	private Expr parseString() {
		int start = index;
		String s = match(Strung.class).string;
		Value.Strung str = Value.V_STRING(s);
		return new Expr.Constant(str, sourceAttr(start, index - 1));
	}
	
	private UnresolvedType parseType() {
		int start = index;
				
		UnresolvedType t = parseUnionIntersectionType();
		
		if ((index + 1) < tokens.size()
				&& tokens.get(index) instanceof ColonColon
				&& tokens.get(index + 1) instanceof LeftBrace) {
			// this is a headless method type

			match(ColonColon.class);
			match(LeftBrace.class);
			ArrayList<UnresolvedType> types = new ArrayList<UnresolvedType>();
			boolean firstTime = true;
			while (index < tokens.size()
					&& !(tokens.get(index) instanceof RightBrace)) {
				if (!firstTime) {
					match(Comma.class);
				}
				firstTime = false;
				types.add(parseType());
			}
			match(RightBrace.class);
			return new UnresolvedType.Method(t, null, types, sourceAttr(start, index - 1));
		} else if (index < tokens.size() && tokens.get(index) instanceof LeftBrace) {
			// this is a function type type
			match(LeftBrace.class);
			ArrayList<UnresolvedType> types = new ArrayList<UnresolvedType>();
			boolean firstTime = true;
			while (index < tokens.size()
					&& !(tokens.get(index) instanceof RightBrace)) {
				if (!firstTime) {
					match(Comma.class);
				}
				firstTime = false;
				types.add(parseType());
			}
			match(RightBrace.class);
			UnresolvedType receiver = null;
			if (index < tokens.size() && (tokens.get(index) instanceof ColonColon)) {				
				// this indicates a method type								
				if(types.size() != 1) {
					syntaxError("receiver type required for method type",tokens.get(index));
				} else {
					receiver = types.get(0);
					types.clear();
				}
				match(ColonColon.class);
				match(LeftBrace.class);
				firstTime = true;
				while (index < tokens.size()
						&& !(tokens.get(index) instanceof RightBrace)) {
					if (!firstTime) {
						match(Comma.class);
					}
					firstTime = false;
					types.add(parseType());
				}
				match(RightBrace.class);
			}
			if(receiver == null) {
				return new UnresolvedType.Function(t, null, types, sourceAttr(start, index - 1));
			} else {
				return new UnresolvedType.Message(receiver, t, null, types, sourceAttr(start, index - 1));
			}
		} else {
			return t;
		}
	}
	
	private UnresolvedType parseUnionIntersectionType() {
		int start = index;
		UnresolvedType t = parseNegationType();
		// Now, attempt to look for negation, union or intersection types.
		if (index < tokens.size() && tokens.get(index) instanceof Bar) {
			// this is a union type
			ArrayList<UnresolvedType.NonUnion> types = new ArrayList<UnresolvedType.NonUnion>();
			types.add((UnresolvedType.NonUnion) t);
			while (index < tokens.size() && tokens.get(index) instanceof Bar) {
				match(Bar.class);
				// the following is needed because the lexer filter cannot
				// distinguish between a lengthof operator, and union type.
				skipWhiteSpace();
				t = parseNegationType();
				types.add((UnresolvedType.NonUnion) t);
			}
			return new UnresolvedType.Union(types, sourceAttr(start, index - 1));
		} else if (index < tokens.size()
				&& tokens.get(index) instanceof Ampersand) {
			// this is an intersection type
			ArrayList<UnresolvedType> types = new ArrayList<UnresolvedType>();
			types.add(t);
			while (index < tokens.size()
					&& tokens.get(index) instanceof Ampersand) {
				match(Ampersand.class);
				// the following is needed because the lexer filter cannot
				// distinguish between a lengthof operator, and union type.
				skipWhiteSpace();
				t = parseNegationType();
				types.add(t);
			}
			return new UnresolvedType.Intersection(types, sourceAttr(start,
					index - 1));
		} else {
			return t;
		}
	}
	
	private UnresolvedType parseNegationType() {
		int start = index;				
		if (index < tokens.size() && tokens.get(index) instanceof Shreak) {			
			// this is a negation type
			match(Shreak.class);
			return new UnresolvedType.Not(parseNegationType(),sourceAttr(start, index - 1));
		} else {
			return parseBraceType();
		}
	}
	
	private UnresolvedType parseBraceType() {			
		if (index < tokens.size() && tokens.get(index) instanceof LeftBrace) {
			// tuple type or bracketed type
			int start = index;
			match(LeftBrace.class);
			UnresolvedType t = parseType();
			skipWhiteSpace();
			if (index < tokens.size() && tokens.get(index) instanceof Comma) {
				// tuple type
				ArrayList<UnresolvedType> types = new ArrayList<UnresolvedType>();
				types.add(t);				
				while (index < tokens.size()
						&& tokens.get(index) instanceof Comma) {					
					match(Comma.class);
					types.add(parseType());
					skipWhiteSpace();
				}
				match(RightBrace.class);
				return new UnresolvedType.Tuple(types, sourceAttr(start, index - 1));
			} else {
				// bracketed type
				match(RightBrace.class);
				return t;
			}			
		} else {
			return parseBaseType();
		}
	}
	
	private UnresolvedType parseBaseType() {				
		checkNotEof();
		int start = index;
		Token token = tokens.get(index);
		UnresolvedType t;
		
		if(token.text.equals("any")) {
			matchKeyword("any");
			t = new UnresolvedType.Any(sourceAttr(start,index-1));
		} else if(token.text.equals("null")) {
			matchKeyword("null");
			t = new UnresolvedType.Null(sourceAttr(start,index-1));
		} else if(token.text.equals("byte")) {
			matchKeyword("byte");			
			t = new UnresolvedType.Byte(sourceAttr(start,index-1));
		} else if(token.text.equals("char")) {
			matchKeyword("char");			
			t = new UnresolvedType.Char(sourceAttr(start,index-1));
		} else if(token.text.equals("int")) {
			matchKeyword("int");			
			t = new UnresolvedType.Int(sourceAttr(start,index-1));
		} else if(token.text.equals("real")) {
			matchKeyword("real");
			t = new UnresolvedType.Real(sourceAttr(start,index-1));
		} else if(token.text.equals("string")) {
			matchKeyword("string");
			t = new UnresolvedType.Strung(sourceAttr(start,index-1));
		} else if(token.text.equals("void")) {
			matchKeyword("void");
			t = new UnresolvedType.Void(sourceAttr(start,index-1));
		} else if(token.text.equals("bool")) {
			matchKeyword("bool");
			t = new UnresolvedType.Bool(sourceAttr(start,index-1));
		} else if(token.text.equals("ref")) {
			matchKeyword("ref");
			t = new UnresolvedType.Reference(parseType(),sourceAttr(start,index-1));			
		} else if(token instanceof LeftBrace) {
			match(LeftBrace.class);
			
			ArrayList<UnresolvedType> types = new ArrayList<UnresolvedType>();
			types.add(parseType());
			match(Comma.class);
			
			types.add(parseType());
			checkNotEof();
			token = tokens.get(index);
			while(!(token instanceof RightBrace)) {
				match(Comma.class);
				
				types.add(parseType());
				checkNotEof();
				token = tokens.get(index);
			}
			match(RightBrace.class);
			return new UnresolvedType.Tuple(types);
		} else if(token instanceof LeftCurly) {		
			match(LeftCurly.class);
			
			t = parseType();			
			
			checkNotEof();
			if(tokens.get(index) instanceof RightCurly) {
				// set type
				match(RightCurly.class);
				t = new UnresolvedType.Set(t,sourceAttr(start,index-1));
			} else if(tokens.get(index) instanceof StrongRightArrow) {
				// map type
				match(StrongRightArrow.class);
				UnresolvedType v = parseType();			
				match(RightCurly.class);
				t = new UnresolvedType.Dictionary(t,v,sourceAttr(start,index-1));				
			} else {				
				// record type
				HashMap<String,UnresolvedType> types = new HashMap<String,UnresolvedType>();
				Token n = matchIdentifier();				
				if(types.containsKey(n)) {
					syntaxError("duplicate record key",n);
				}
				types.put(n.text, t);
				
				checkNotEof();
				token = tokens.get(index);
				boolean isOpen = false;
				
				while(!(token instanceof RightCurly)) {
					match(Comma.class);
					
					checkNotEof();
					token = tokens.get(index);
					
					if(token instanceof DotDotDot) {
						// special case indicates an open record
						match(DotDotDot.class);
						isOpen = true;
						break;
					}
					
					UnresolvedType tmp = parseType();
					
					n = matchIdentifier();
					
					if(types.containsKey(n)) {
						syntaxError("duplicate record key",n);
					}								
					types.put(n.text, tmp);					
					checkNotEof();
					token = tokens.get(index);								
				}				
							
				match(RightCurly.class);
				t = new UnresolvedType.Record(isOpen, types, sourceAttr(start,index-1));				
			} 
		} else if(token instanceof LeftSquare) {
			match(LeftSquare.class);			
			t = parseType();			
			match(RightSquare.class);
			t = new UnresolvedType.List(t,sourceAttr(start,index-1));
		} else {		
			ArrayList<String> names = new ArrayList<String>();
			names.add(matchIdentifier().text);			
			while(index < tokens.size() && tokens.get(index) instanceof Dot) {
				match(Dot.class);
				names.add(matchIdentifier().text);
			}
			t = new UnresolvedType.Nominal(names,sourceAttr(start,index-1));			
		}		
		
		return t;
	}		
	
	private void skipWhiteSpace() {
		while (index < tokens.size() && isWhiteSpace(tokens.get(index))) {
			index++;
		}
	}

	private boolean isWhiteSpace(Token t) {
		return t instanceof WhileyLexer.NewLine
				|| t instanceof WhileyLexer.LineComment
				|| t instanceof WhileyLexer.BlockComment
				|| t instanceof WhileyLexer.Tabs;
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
			} else if(!(t instanceof LineComment) && !(t instanceof BlockComment) &&!(t instanceof Tabs)) {
				syntaxError("unexpected token encountered (" + t.text + ")",t);
			}			
		}
	}
	
	private Attribute.Source sourceAttr(int start, int end) {		
		Token t1 = tokens.get(start);				
		Token t2 = tokens.get(end);
		return new Attribute.Source(t1.start,t2.end(),t1.line);
	}
	
	private void syntaxError(String msg, Expr e) {
		Attribute.Source loc = e.attribute(Attribute.Source.class);
		throw new SyntaxError(msg, filename, loc.start, loc.end);
	}
	
	private void syntaxError(String msg, Token t) {
		throw new SyntaxError(msg, filename, t.start, t.start + t.text.length() - 1);
	}		
}
