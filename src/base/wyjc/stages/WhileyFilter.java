package wyjc.stages;

import java.util.*;
import static wyjc.stages.WhileyLexer.*;

/**
 * The purpose of the Whiley Filter is to filter out whitespace that is not
 * syntactically significant. This includes comments, but also tabs in certain
 * positions. For example, a tab which occurs immediately after e.g. a "+"
 * operator is always ignored, since it doesn't make sense.
 * 
 * @author djp
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
			} else if(t instanceof Comment) {						
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
			}
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
		
		return result;
	}
	
	public static final Class[] leftNonTerminators = { 
		Comma.class,
		LeftBrace.class,
		LeftSquare.class,		
		LeftCurly.class,
		Plus.class,
		Minus.class,
		Star.class,
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
		AddressOf.class,
		Arrow.class,		
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
		Star.class,
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
		AddressOf.class,
		Arrow.class,		
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
