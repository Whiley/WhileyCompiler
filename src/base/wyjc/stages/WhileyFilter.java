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
		for(int i=0;i!=tokens.size();++i) {
			Token t = tokens.get(i);
			Token next = (i+1) < tokens.size() ? tokens.get(i+1) : null;
			boolean afterFlag = leftNonTerminator(last);						
			boolean beforeFlag = rightNonTerminator(next);
			last = t;
			if(t instanceof Tabs && !afterNewLine) {
				// don't add in this case
			} else if(t instanceof Comment) {						
				// or this case
			} else if(isWhiteSpace(t) && afterFlag) {
				// or this case				
			} else if(isWhiteSpace(t) && beforeFlag) {
				// or this case
			} else {
				result.add(t);
			}
			if(t instanceof NewLine) {
				afterNewLine = true;
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
