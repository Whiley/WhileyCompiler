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

package wyjc.ast.exprs.list;

import java.util.*;
import java.math.*;

import wyjc.ModuleLoader;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.IntVal;
import wyjc.ast.types.ListType;
import wyjc.ast.types.Type;
import wyjc.ast.types.Types;
import wyjc.lang.Attribute;
import wyjc.lang.SyntacticElementImpl;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.quantifier.*;
import wyone.theory.type.WTypes;
import wyone.theory.list.*;
import static wyjc.util.SyntaxError.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;


public final class ListAppend extends BinOp<Expr> {	
	public ListAppend(Expr lhs, Expr rhs, Attribute... attributes)  {
		super(lhs, rhs, null,attributes);		
	}
	
	public ListAppend(Expr lhs, Expr rhs, Collection<Attribute> attributes)  {
		super(lhs, rhs, null,attributes);		
	}
	
	public Type type(Map<String,Type> environment) {
		Type _lhs_t = lhs.type(environment);
		Type _rhs_t = rhs.type(environment);
		if(!(_lhs_t instanceof ListType)) {
			syntaxError("expecting list type",lhs);	
		}
		if(!(_rhs_t instanceof ListType)) {
			syntaxError("expecting list type",rhs);	
		}
		ListType lhs_t = (ListType) _lhs_t;
		ListType rhs_t = (ListType) _rhs_t;
		Type elem_t = Types.leastUpperBound(lhs_t.element(), rhs_t.element());
		return new ListType(elem_t);
	}
	
    public Expr substitute(Map<String,Expr> binding) {
		Expr l = lhs.substitute(binding);
		Expr r = rhs.substitute(binding);		
		return new ListAppend(l,r,attributes());
	}
    
    public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr l = lhs.replace(binding);
			Expr r = rhs.replace(binding);			
			return new ListAppend(l,r,attributes());
		}
	}
    	    
    public Expr reduce(Map<String, Type> environment) {
		Expr l = lhs.reduce(environment);
		Expr r = rhs.reduce(environment);		
		
		if (lhs instanceof ListVal && rhs instanceof ListVal) {
			ListVal lv = (ListVal) l;
			ListVal rv = (ListVal) r;
			List<Value> lvs = lv.getValues();
			List<Value> rvs = rv.getValues();
			ArrayList<Value> nvs = new ArrayList<Value>(lvs);
			nvs.addAll(rvs);			
			return new ListVal(nvs,attributes());
		} else {
			// no further reduction possible.
			return new ListAppend(l,r, attributes());
		}
	}
        
    public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {		
		Pair<WExpr,WFormula> l = lhs.convert(environment, loader);
		Pair<WExpr,WFormula> r = rhs.convert(environment, loader);		
			
		WVariable retVar = WVariable.freshVar();
		
		// first, identify new length					
		WFormula lenConstraints = WExprs.equals(new WLengthOf(retVar), add(new WLengthOf(l.first()),
				new WLengthOf(r.first())));
		
		
		// second, pump from left src into retVar
		WVariable i = WVariable.freshVar();
		HashMap<WVariable,WExpr> variables = new HashMap();
		variables.put(i,l.first());
		WFormula lhs = WExprs.equals(new WListAccess(l.first(), i),
				new WListAccess(retVar, i));
		WFormula forall1 = new WBoundedForall(true,variables,lhs);
		
		// third, pump from right src into retVar
		i = WVariable.freshVar();
		variables = new HashMap();
		variables.put(i,r.first());
		WFormula rhs = WExprs.equals(new WListAccess(r.first(), i),
				new WListAccess(retVar, add(i,new WLengthOf(l.first()))));
		WFormula forall2 = new WBoundedForall(true,variables,rhs);
		
			// finally, pump from retvar into left src
		i = WVariable.freshVar();
		variables = new HashMap();
		variables.put(i,retVar);
		WFormula l1 = lessThan(i,new WLengthOf(l.first()));
		WFormula r1 = WExprs.equals(new WListAccess(l.first(), i),
				new WListAccess(retVar, i));
		WFormula l2 = greaterThanEq(i,new WLengthOf(l.first()));
		WFormula r2 = WExprs.equals(new WListAccess(r.first(), subtract(i,
				new WLengthOf(l.first()))), new WListAccess(retVar, i));

		WFormula forall3 = new WBoundedForall(true, variables, WFormulas.and(
				WFormulas.implies(l1, r1), WFormulas.implies(l2, r2)));
		
		WFormula constraints = WFormulas.and(l.second(), r.second(),
				lenConstraints, forall1, forall2, forall3, WTypes.subtypeOf(
						retVar, type(environment).convert()));

		return new Pair<WExpr,WFormula>(retVar, constraints);
	}       
	    
    public String toString() {
    	return lhs + " + " + rhs;
    }
}
