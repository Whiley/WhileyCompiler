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

import java.util.*;

import static wyc.stages.WhileyLexer.*;

/**
 * The purpose of the Whiley Filter is to filter out whitespace that is not
 * syntactically significant. This includes comments, but also tabs in certain
 * positions. For example, a tab which occurs immediately after e.g. a "+"
 * operator is always ignored, since it doesn't make sense.
 * 
 * @author David J. Pearce
 * 
 */
public class WhileyFilter {
	public List<Token> filter(List<Token> tokens) {
		ArrayList<Token> result = new ArrayList<Token>();				
		boolean afterNewLine = true;		
		Token last = null;		
		int ncurly = 0;
		int nsquare = 0;
		for(int i=0;i!=tokens.size();++i) {			
			Token t = tokens.get(i);
			Token next = (i+1) < tokens.size() ? tokens.get(i+1) : null;
			boolean afterFlag = leftNonTerminator(last);						
			boolean beforeFlag = rightNonTerminator(next);
			boolean withinBraces = ncurly > 0 || nsquare > 0;			
			if(t instanceof Tabs && !afterNewLine) {
				// don't add in this case
			} else if(t instanceof NewLine && afterNewLine) {
				// or this case
			} else if(t instanceof LineComment) {						
				// or this case
			} else if(isWhiteSpace(t) && afterFlag) {
				// or this case				
			} else if(isWhiteSpace(t) && beforeFlag) {
				// or this case
			} else if(isWhiteSpace(t) && withinBraces) {
				// or this case!
			} else {				
				result.add(t);
				last = t;			
				afterNewLine = false; // reset
				if(t instanceof NewLine) {
					afterNewLine = true;
				} else if(t instanceof LeftCurly) {
					ncurly++;
				} else if(t instanceof RightCurly) {
					ncurly = Math.max(0, ncurly-1);
				} else if(t instanceof LeftSquare) {
					ncurly++;
				} else if(t instanceof RightSquare) {
					ncurly = Math.max(0, ncurly-1);
				}
			}
		}
				
		return result;
	}
	
	public static final Class[] leftNonTerminators = { 
		Comma.class,
		LeftBrace.class,
		LeftSquare.class,		
		LeftCurly.class,
		Plus.class,
		Minus.class,		
		LeftSlash.class,
		Shreak.class,
		Dot.class,
		Equals.class,
		EqualsEquals.class,
		NotEquals.class,
		LessEquals.class,
		GreaterEquals.class,
		LeftAngle.class,
		RightAngle.class,
		None.class,
		Some.class,
		ElemOf.class,
		Union.class,
		Intersection.class,
		Subset.class,
		Supset.class,
		SubsetEquals.class,
		SupsetEquals.class,
		LogicalAnd.class,
		LogicalOr.class,
		LogicalNot.class,
		Ampersand.class,
		RightArrow.class
	};
	
	public boolean leftNonTerminator(Token t) {
		for(Class<Token> nt : leftNonTerminators) {
			if(nt.isInstance(t)) {
				return true;
			}
		}
		return false;
	}
	
	public static final Class[] rightNonTerminators = { 
		Comma.class,
		RightBrace.class,
		RightSquare.class,		
		RightCurly.class,
		Plus.class,
		Minus.class,		
		LeftSlash.class,
		//Shreak.class,		
		Dot.class,
		Equals.class,
		EqualsEquals.class,
		NotEquals.class,
		LessEquals.class,
		GreaterEquals.class,
		LeftAngle.class,
		RightAngle.class,
		ElemOf.class,
		Union.class,
		Intersection.class,
		Subset.class,
		Supset.class,
		SubsetEquals.class,
		SupsetEquals.class,
		LogicalAnd.class,
		LogicalOr.class,
		LogicalNot.class,
		Ampersand.class,
		RightArrow.class,
		NewLine.class,
		LineComment.class
	};
	
	public boolean rightNonTerminator(Token t) {
		for(Class<Token> nt : rightNonTerminators) {
			if(nt.isInstance(t)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isWhiteSpace(Token t) {
		return t instanceof NewLine || t instanceof Tabs; 
	}
}
