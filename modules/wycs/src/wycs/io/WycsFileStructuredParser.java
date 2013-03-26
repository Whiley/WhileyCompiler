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
import wybs.io.AbstractLexer;
import wybs.io.Token;
import wybs.lang.Attribute;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.Pair;
import wybs.util.Trie;
import wycs.lang.*;
import wycs.lang.WycsFile.Assert;
import wycs.lang.WycsFile.Define;
import wycs.lang.WycsFile.Function;

public class WycsFileStructuredParser extends WycsFileClassicalParser {
	public final int SPACES_PER_TAB = 4;
	
	public WycsFileStructuredParser(String filename, List<Token> tokens) {
		super(filename,tokens);
	}
	
	@Override
	protected void parseImport(WycsFile wf) {
		super.parseImport(wf);
		matchEndOfLine();
	}
	
	@Override
	protected void parseAssert(WycsFile wf) {
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

	@Override
	protected void parseFunction(WycsFile wf) {
		int start = index;
		match("function");

		HashSet<String> environment = new HashSet<String>();
		ArrayList<String> generics = new ArrayList<String>();
		String name = parseGenericSignature(environment,generics);
		
		HashSet<String> genericSet = new HashSet<String>(generics);
		TypePattern from = parseTypePattern(genericSet);
		addNamedVariables(from,environment);
				
		// function!			
		match("=>");
		TypePattern to = parseTypePattern(genericSet);
		addNamedVariables(to, environment);
		Expr condition = null;
		if(matches("where")) {
			match("where");
			match(":");
			matchEndOfLine();
			condition = parseBlock(0,genericSet, environment);
		}
		wf.add(wf.new Function(name, generics, from, to, condition,
				sourceAttr(start, index - 1)));
	}
	
	@Override
	protected void parseDefine(WycsFile wf) {
		int start = index;
		match("define");

		HashSet<String> environment = new HashSet<String>();
		ArrayList<String> generics = new ArrayList<String>();
		String name = parseGenericSignature(environment, generics);

		HashSet<String> genericSet = new HashSet<String>(generics);
		TypePattern from = parseTypePattern(genericSet);
		addNamedVariables(from, environment);

		match("as");
		match(":");
		matchEndOfLine();
		Expr condition = parseBlock(0,genericSet, environment);
		wf.add(wf.new Define(name, generics, from, condition, sourceAttr(start,
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
			HashSet<String> generics, HashSet<String> environment) {
		int start = index;
		if(isSome) {
			match("some");
		} else {
			match("forall");
		}
		match("(");
		ArrayList<Pair<TypePattern,Expr>> variables = new ArrayList<Pair<TypePattern,Expr>>();
		boolean firstTime = true;
		while (!matches(")")) {
			if (!firstTime) {
				match(",");					
			} else {
				firstTime = false;
			}			
			TypePattern pattern = parseTypePatternUnionOrIntersection(generics);
			Expr src = null;
			if(matches("in",Token.sUC_ELEMENTOF)) {
				match("in",Token.sUC_ELEMENTOF);
				src = parseCondition(generics,environment);				
			}	
			addNamedVariables(pattern,environment);
			variables.add(new Pair<TypePattern,Expr>(pattern,src));
		}
		match(")");
		Expr body;
		if(matches(":")) {
			match(":");
			matchEndOfLine();
			body = parseBlock(parentIndent,generics,environment);
		} else {
			body = parseCondition(generics,environment);
		}
		Pair<TypePattern, Expr>[] varArray = variables
				.toArray(new Pair[variables.size()]);
		if (isSome) {
			return Expr.Exists(varArray, body, sourceAttr(start, index - 1));
		} else {
			return Expr.ForAll(varArray, body, sourceAttr(start, index - 1));
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
}