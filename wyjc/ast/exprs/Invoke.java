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

package wyjc.ast.exprs;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.*;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.stmts.Stmt;
import wyjc.ast.types.FunType;
import wyjc.ast.types.Type;
import wyjc.ast.types.Types;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.type.WTypes;

public class Invoke extends SyntacticElementImpl implements Stmt, Expr {
	private final String name;
	private ModuleID module;
	private final Expr target;	
	private final Expr argument;
	private FunType ftype;
	
	public Invoke(String name, Expr target, Expr argument,
			Attribute... attributes) {
		super(attributes);		
		this.name = name;
		this.target = target;
		this.argument = argument;		
	}
	
	public Invoke(String name, Expr target, Expr argument,
			Collection<Attribute> attributes) {
		super(attributes);
		this.target = target;
		this.name = name;		
		this.argument = argument;		
	}
	
	public Invoke(String name, ModuleID module, FunType ftype, Expr target,
			Expr argument, Attribute... attributes) {
		super(attributes);
		this.target = target;
		this.name = name;		
		this.module = module;
		this.ftype = ftype;
		this.argument = argument;		
	}
	
	public Invoke(String name, ModuleID module, FunType ftype, Expr target,
			Expr argument, Collection<Attribute> attributes) {
		super(attributes);
		this.target = target;
		this.name = name;
		this.module = module;
		this.ftype = ftype;
		this.argument = argument;		
	}
	
	public Type type(Map<String,Type> environment) {
		return ftype.postState();
	}
	
	public FunType funType() {
		return ftype;
	}
	
	public Expr target() {
		return target;
	}
	
	public ModuleID module() {
		return module;
	}
	
	public void resolve(ModuleID module, FunType ftype) {
		this.module = module;
		this.ftype = ftype;
	}
		
	public Expr substitute(Map<String,Expr> binding) {		
		Expr targ = target == null ? null : target.substitute(binding);
		return new Invoke(name, module, ftype, targ, argument
				.substitute(binding), attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {			
			Expr targ = target == null ? null : target.replace(binding);
			return new Invoke(name, module, ftype, targ, argument
					.replace(binding), attributes());
		}
	}
	
	public <T> List<T> match(Class<T> match) {
		ArrayList<T> r = new ArrayList();
		r.addAll(argument.match(match));		
		if(match.isInstance(this)) {
			r.add((T) this);
		}
		return r;
	}
	
	public Set<Variable> uses() {
		HashSet<Variable> r = new HashSet<Variable>();		
		r.addAll(argument.uses());		
		if(target != null) {
			r.addAll(target.uses());
		}
		return r;
	}
	
	public String name() {
		return name;
	}
	
	public Expr argument() {
		return argument;
	}	
	
	public Expr reduce(Map<String, Type> environment) {
		Expr argument = this.argument;
		Expr target = this.target;		
		argument = argument.reduce(environment);		
		if(target != null) {
			target = target.reduce(environment);
		}
		return new Invoke(name, module, ftype, target, argument, attributes());		
	}
		
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError {
		
		// FIXME: there is a bug here, when the same method is encountered in
        // several distincts places in a method call.
		
		WFormula constraints = null;
		
		ArrayList<WExpr> params = new ArrayList<WExpr>();
		
		Pair<WExpr, WFormula> pc = argument.convert(environment,
					loader);
		WType p_type = ftype.preState().convert();
		params.add(pc.first());
		WFormula argConstraints = WFormulas.and(pc.second(), WTypes.subtypeOf(
				pc.first(), p_type));					
		
		WVariable retLabel = new WVariable("&" + name, params);							
		environment = new HashMap<String,Type>(environment);
		environment.put("$", ftype.postState());		
				
		constraints = null;		
		
		ModuleInfo mi = loader.loadModule(module);
		
		for (ModuleInfo.Method function : mi.method(name, ftype)) {
			WFormula mcs = WBool.TRUE;
			
			Condition postCondition = Types.expandConstraints(function.type());			
									
			/**
			 * FIXME: need to update this for sure 
			if (postCondition != null) {				
				// Ok, here we need to translate the post-condition
				HashMap<WExpr, WExpr> binding = new HashMap<WExpr,WExpr>();
				binding.put(new WVariable("$"), retLabel);
				FunType f_type = function.type();				
				environment = new HashMap<String, Type>(environment);
				
				Type posttype = f_type.postState();
				environment.put("$", posttype);
				binding.put(new WVariable("$"), argument);					

				Pair<WFormula, WFormula> postc = postCondition
						.convertCondition(environment, loader);
		
				mcs = WFormulas.and(postc.first(), postc.second()).substitute(
						binding);
			}
			*/
			if (constraints == null) {
				constraints = mcs;
			} else {
				constraints = WFormulas.or(constraints, mcs);
			}
		}								
		
		constraints = WFormulas.and(constraints, WTypes.subtypeOf(retLabel,
				ftype.postState().convert()));
		
		return new Pair<WExpr, WFormula>(retLabel, constraints);
	}
	
	public boolean equals(Object o) {
		if(o instanceof Invoke) {
			Invoke i = (Invoke) o;
			if (name.equals(i.name)
					&& (target == i.target || (target != null && target
							.equals(i.target)))) {
				if (module == i.module
						|| (module != null && module.equals(i.module))) {
					return ftype.equals(i.ftype) && argument.equals(i.argument);					
				}
			}
		} 
		return false;		
	}
	
	public int hashCode() {
		int hc = 0;
		if(target != null) {
			hc += target.hashCode();
		}
		if(module != null) {
			hc += module.hashCode();
		}
		hc += argument.hashCode();
		return hc;
	}
	
	public String toString() {
		String r = "";
		if (target != null) {
			r += target.toString() + "->";
		}
		if (argument instanceof TupleGenerator || argument instanceof TupleVal) {
			return name + argument;
		} else {
			return name + " " + argument;
		}
	}
}
