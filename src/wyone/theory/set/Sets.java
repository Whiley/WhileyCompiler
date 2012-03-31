package wyone.theory.set;

import java.util.*;
import wyone.core.*;
import wyone.theory.logic.*;

public class Sets {

	public static Formula subsetEq(Constructor lhs, Constructor rhs) {
		return new SubsetEq(true,lhs,rhs);
	}

	public static Formula subset(Constructor lhs, Constructor rhs) {
		
		if(rhs instanceof SetConstructor || rhs instanceof Value.Set) {
			// This is a useful optimisation case.
			Formula r = Value.FALSE;
			for(Constructor e : rhs.subterms()) {
				HashSet elems = new HashSet(rhs.subterms());
				elems.remove(e);					
				if(rhs instanceof SetConstructor) {
					e = new SetConstructor(elems);
				} else {
					e = Value.V_SET(elems);
				}
				r = Logic.or(r,subsetEq(lhs,e));
			}
			return r;			
		} 
		
		return Logic.and(new SubsetEq(true, lhs, rhs), Equality.notEquals(lhs, rhs));
	}
	
	public static Formula supsetEq(Constructor lhs, Constructor rhs) {
		return new SubsetEq(true,rhs,lhs);
	}
	
	public static Formula elementOf(Constructor lhs, Constructor rhs) {		
		return new SubsetEq(true,new SetConstructor(lhs),rhs);
	}	
}
