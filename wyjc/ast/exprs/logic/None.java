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

package wyjc.ast.exprs.logic;

import java.math.BigInteger;
import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.set.*;
import wyjc.ast.exprs.integer.IntVal;
import wyjc.ast.exprs.list.*;
import wyjc.ast.types.*;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.list.WListAccess;
import wyone.theory.logic.*;
import wyone.theory.quantifier.*;
import wyone.theory.set.*;

public class None extends UnOp<SetComprehension> implements Condition {
	public None(SetComprehension e, Attribute... attributes) {
		super(e,Types.T_BOOL(null),attributes);		
	}
	
	public None(SetComprehension e, Collection<Attribute> attributes) {
		super(e,Types.T_BOOL(null),attributes);		
	}
	
	public BoolType type(Map<String,Type> env) {
		return Types.T_BOOL(null);
	}
	
	public Condition substitute(Map<String,Expr> binding) {
		SetComprehension e = expr.substitute(binding);		
		return new None(e,attributes());
	}	

	public Expr replace(Map<Expr, Expr> binding) {
		Expr t = binding.get(this);
		if (t != null) {
			return t;
		} else {
			Expr l = expr.replace(binding);
			return new None((SetComprehension) l, attributes());
		}
	}
    
	public Condition reduce(Map<String, Type> environment) {
		Expr r = expr.reduce(environment);
		
		if (r instanceof SetVal) {
			SetVal sv = (SetVal) r;
			if(sv.getValues().size() > 0) {
				return new BoolVal(false, attributes());
			} else {
				return new BoolVal(true, attributes());
			}
		} else if(r instanceof SetGenerator) {
			SetVal sv = (SetVal) r;
			if(sv.getValues().size() > 0) {
				return new BoolVal(false, attributes());
			} else {
				return new BoolVal(true, attributes());
			}
		} else {
			// no further reduction possible.
			return new None((SetComprehension) r, attributes());
		}
	}
	
    public String toString() {
    	String r = "no {";
    	boolean firstTime=true;
    	for(Pair<String,Expr> p : expr.sources()) {
    		if(!firstTime) {
    			r += ", ";
    		}
    		firstTime=false;
    		r += p.first() + " in " + p.second();
    	}
    	return r + " | " + expr.condition() + "}";
    }
  
    public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		return null; // FIXME
	}

	public Pair<WFormula, WFormula> convertCondition(
			Map<String, Type> environment, ModuleLoader loader)
			throws ResolveError {
		
		SetComprehension sc = (SetComprehension) expr;				
		WFormula constraints = WBool.TRUE;
		HashMap<WVariable,WExpr> variables = new HashMap();
		HashMap<String,Type> nenv = new HashMap(environment);
		HashMap<WExpr,WExpr> binding = new HashMap();
				
		for (Pair<String, Expr> p : sc.sources()) {
			String name = p.first();
			Expr src = p.second();			
			SetType st = (SetType) src.type(environment);
			WVariable v = new WVariable(name);
			
			boolean hackOn = false;
			
			if(src instanceof RangeGenerator && st instanceof ListType) {
				// This is essentially a rather big hack; but, it definitely
				// helps reduce the theorem proving burden, since this is a very
				// common case arising from the treatment of list element
				// assignment expressions in PostConditionGenerator.
				RangeGenerator rg = (RangeGenerator) src;
				Expr start = rg.start();
				Expr end = rg.end();
				if (start instanceof IntVal
						&& ((IntVal) start).value().equals(BigInteger.ZERO)
						&& end instanceof ListLength) {
					// ok, we have a hit on the hack
					src = ((ListLength)end).mhs();
					hackOn = true;
				}
			}
			
			Pair<WExpr,WFormula> re = src.convert(environment, loader);
			if(st instanceof ListType) {
				nenv.put(name, st.element());
				if(!hackOn) {
					binding.put(v, new WListAccess(re.first(),v));
				}
			} else {
				nenv.put(name, st.element());
			}
									
			variables.put(v,re.first());
			constraints = WFormulas.and(constraints,re.second());												
		}
		
		Pair<WFormula,WFormula> mhs = sc.condition().convertCondition(nenv, loader);
		constraints = WFormulas.and(constraints,mhs.second()).substitute(binding);		
				
		return new Pair(
				new WBoundedForall(true, variables, mhs.first().not().substitute(binding)),
				constraints);	
	}
}
