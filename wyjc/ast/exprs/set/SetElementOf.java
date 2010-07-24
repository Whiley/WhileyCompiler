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

package wyjc.ast.exprs.set;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.exprs.real.*;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.types.*;
import wyjc.lang.Attribute;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.set.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;

public class SetElementOf extends ConditionBinOp<Expr> implements Condition {
	public SetElementOf(Expr lhs, Expr rhs, Attribute... attributes) {
		super(lhs,rhs,attributes);		
	}
	
	public SetElementOf(Expr lhs, Expr rhs, Collection<Attribute> attributes) {
		super(lhs,rhs,attributes);		
	}
	
	public Condition substitute(Map<String,Expr> binding) {
		Expr l = lhs.substitute(binding);
		Expr r = (Expr) rhs.substitute(binding);
		return new SetElementOf(l,r,attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr l = lhs.replace(binding);
			Expr r = rhs.replace(binding);
			return new SetElementOf(l,r,attributes());
		}
	}	
	
	public Condition reduce(Map<String, Type> environment) {
		Expr l =  lhs.reduce(environment);
		Expr r = (Expr) rhs.reduce(environment);
						
		if (l instanceof Value && r instanceof SetVal) {
			SetVal sv = (SetVal) r;
			return new BoolVal(sv.getValues().contains(l), attributes());
		} else if(r instanceof SetUnion) {
			SetUnion su = (SetUnion) r;
			Or e = new Or(new SetElementOf(l, su.lhs(), attributes()),
					new SetElementOf(l, su.rhs(), attributes()), attributes());			
			return e.reduce(environment);
		} else if(r instanceof SetIntersection) {
			SetIntersection su = (SetIntersection) r;
			And e = new And(new SetElementOf(l, su.lhs(), attributes()),
					new SetElementOf(l, su.rhs(), attributes()), attributes());
			return e.reduce(environment);
		} else if(l instanceof Variable && r instanceof SetVal) {
			Variable v = (Variable) l;			
			Type t = v.type(environment);
			SetVal sc = (SetVal) r;
			Condition c = null;
			
			for(Expr e : sc.getValues()) {
				Condition eq;
				if(t instanceof BoolType) {
					eq = new BoolEquals(v,(Expr) e);
				} else if(t instanceof IntType) {
					eq = new IntEquals(v,(Expr) e);
				} else if(t instanceof RealType) {
					eq = new RealEquals(v,(Expr) e);
				} else if(t instanceof ListType) {
					eq = new ListEquals(v,(Expr) e);
				} else if(t instanceof SetType) {
					eq = new SetEquals(v,(Expr) e);
				} else if(t instanceof TupleType) {
					eq = new TupleEquals(v,(Expr) e);
				} else {					// 
					throw new RuntimeException("Unknown type encountered: " + t.getClass().getName());
				}
				c = c == null ? eq : new Or(eq,c);				
			}
			
			return c.reduce(environment);
		} else if(l instanceof Variable && r instanceof SetGenerator) {
			Variable v = (Variable) l;
			Type t = v.type(environment);
			SetGenerator sc = (SetGenerator) r;
			Condition c = null;
			
			for(Expr e : sc.getValues()) {
				Condition eq;
				if(t instanceof BoolType) {
					eq = new BoolEquals(v,(Expr) e);
				} else if(t instanceof IntType) {
					eq = new IntEquals(v,(Expr) e);
				} else if(t instanceof RealType) {
					eq = new RealEquals(v,(Expr) e);
				} else if(t instanceof ListType) {
					eq = new ListEquals(v,(Expr) e);
				} else if(t instanceof SetType) {
					eq = new SetEquals(v,(Expr) e);
				} else if(t instanceof TupleType) {
					eq = new TupleEquals(v,(Expr) e);
				} else {
					// probably for def types --- need to get the flat type somehow!
					throw new RuntimeException("Unknown type encountered: " + t.getClass().getName());
				}
				c = c == null ? eq : new Or(eq,c);				
			}
			
			return c.reduce(environment);
		} else if(r instanceof SetComprehension) {
			SetComprehension sc = (SetComprehension) r;
			
			Expr e = sc.sign();
			if(e instanceof Variable) {
				Variable v = (Variable) e;
				// this is a nice simple case
				SetElementOf eof = new SetElementOf(l,sc.source(v.name()),attributes());
				HashMap<String,Expr> binding = new HashMap<String,Expr>();
				binding.put(v.name(),l);
				And and = new And(eof, sc.condition().substitute(binding),
						attributes());
				return and.reduce(environment);
			} else {
				// this is a slightly more awkward case
				// FIXME: deal with this case properly.
				System.out.println("WARNING: SetElementOf.reduce() incomplete");
				return new SetElementOf(l, r, attributes());
			}
		} 
		
		return new SetElementOf(l, r, attributes());		
	}
	
	public Pair<WFormula, WFormula> convertCondition(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {		
		Pair<WExpr,WFormula> l = lhs.convert(environment, loader);
		Pair<WExpr,WFormula> r = rhs.convert(environment, loader);					
		WFormula constraints = WFormulas.and(l.second(),r.second());		
		
		WExpr ls;		
		
		if(l.first() instanceof WValue) {
			ls = new WSetVal((WValue)l.first());
		} else {
			ls = new WSetConstructor(l.first());
		}		
				
		return new Pair(new WSubsetEq(true, ls, r.first()),constraints);
	}  	
	
	public String toString() {
		String l = lhs.toString();
		String r = rhs.toString();
		return l + " in " + r;
	}
}
