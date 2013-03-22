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

public class WycsFileStructuredParser extends WycsFileClassicalParser {
	public final int SPACES_PER_TAB = 4;
	
	public WycsFileStructuredParser(String filename, List<Token> tokens) {
		super(filename,tokens);
	}
	
	@Override
	protected ArrayList<Token> filterTokenStream(List<Token> tokens) {
		// first, strip out any whitespace
		ArrayList<Token> ntokens = new ArrayList<Token>(tokens);
		for(int i=0;i!=tokens.size();i=i+1) {
			Token lookahead = tokens.get(i);
			if (lookahead instanceof Token.LineComment
					|| lookahead instanceof Token.BlockComment) {
				// filter these ones out
			} else {
				ntokens.add(lookahead);
			}
		}
		return ntokens;
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
		match(":");
		matchEndOfLine();
		
		Expr condition = parseBlock(0,new HashSet<String>(), new HashSet<String>());					
		wf.add(wf.new Assert(msg, condition, sourceAttr(start, index - 1)));		
	}
	
	protected Expr parseBlock(int parentIndent, int expectedIndent,
			HashSet<String> generics, HashSet<String> environment) {
		int indent = scanIndent();
		while(indent > parentIndent) {
			matchIndent();
			if((expectedIndent != parentIndent) && ) {
				
			}
			indent = scanIndent();
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
	
	protected int matchIndent() {
		int indent = 0;
		while (index < tokens.size()
				&& (tokens.get(index) instanceof Token.Spaces || tokens
						.get(index) instanceof Token.Tabs)) {
			Token token = tokens.get(index);
			if(token instanceof Token.Spaces) {
				indent += token.text.length();
			} else {
				indent += token.text.length() * SPACES_PER_TAB;
			}
			index = index + 1;
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
	
	protected void skipWhiteSpace() {
		while (index < tokens.size()
				&& tokens.get(index) instanceof Token.Whitespace) {
			index = index + 1;
		}
	}
}