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
import wyjc.ast.stmts.Stmt;
import wyjc.ast.types.FunType;
import wyjc.ast.types.Type;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;

public class Invoke extends SyntacticElementImpl implements Stmt, Expr {
	private final String name;
	private ModuleID module;
	private final Expr target;	
	private FunType ftype;
	
	private final ArrayList<Expr> arguments;
	
	public Invoke(String name, Expr target, List<Expr> arguments,
			Attribute... attributes) {
		super(attributes);		
		this.name = name;
		this.target = target;
		this.arguments = new ArrayList<Expr>(arguments);		
	}
	
	public Invoke(String name, Expr target, List<Expr> arguments,
			Collection<Attribute> attributes) {
		super(attributes);
		this.target = target;
		this.name = name;		
		this.arguments = new ArrayList<Expr>(arguments);		
	}
	
	public Invoke(String name, ModuleID module, FunType ftype, Expr target,
			List<Expr> arguments, Attribute... attributes) {
		super(attributes);
		this.target = target;
		this.name = name;		
		this.module = module;
		this.ftype = ftype;
		this.arguments = new ArrayList<Expr>(arguments);		
	}
	
	public Invoke(String name, ModuleID module, FunType ftype, Expr target,
			List<Expr> arguments, Collection<Attribute> attributes) {
		super(attributes);
		this.target = target;
		this.name = name;
		this.module = module;
		this.ftype = ftype;
		this.arguments = new ArrayList<Expr>(arguments);		
	}
	
	public Type type(Map<String,Type> environment) {
		return ftype.returnType();
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
		ArrayList<Expr> args = new ArrayList<Expr>();
		for(Expr e : arguments) {
			args.add(e.substitute(binding));
		}
		Expr targ = target == null ? null : target.substitute(binding);
		return new Invoke(name,module,ftype,targ,args,attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			ArrayList<Expr> args = new ArrayList<Expr>();		
			for(Expr e : arguments) {
				args.add(e.replace(binding));
			}
			Expr targ = target == null ? null : target.replace(binding);
			return new Invoke(name,module,ftype,targ,args,attributes());
		}
	}
	
	public <T> List<T> match(Class<T> match) {
		ArrayList<T> r = new ArrayList();
		for(Expr p : arguments) {
			r.addAll(p.match(match));
		}
		if(match.isInstance(this)) {
			r.add((T) this);
		}
		return r;
	}
	
	public Set<Variable> uses() {
		HashSet<Variable> r = new HashSet<Variable>();
		for(Expr p : arguments) {
			r.addAll(p.uses());
		}
		return r;
	}
	
	public String name() {
		return name;
	}
	
	public List<Expr> arguments() {
		return arguments;
	}	
	
	public Expr reduce(Map<String, Type> environment) {
		// FIXME: this could be improved ?
		return this;
	}
		
	public Triple<WExpr, WFormula, WEnvironment> convert(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError {
		
		// FIXME: there is a bug here, when the same method is encountered in
        // several distincts places in a method call.

		WFormula argConstraints = WBool.TRUE;
		WFormula constraints = null;
		
		ArrayList<WExpr> params = new ArrayList<WExpr>();

		for(Expr p : arguments) {			
			Triple<WExpr, WFormula, WEnvironment> pc = p.convert(environment,
					loader);
			params.add(pc.first());
			argConstraints = WFormulas.and(argConstraints,pc.second());			
		}
		
		WVariable retLabel = new WVariable("&" + name, params);							
		environment = new HashMap<String,Type>(environment);
		environment.put("$", ftype.returnType());		
		
		WEnvironment wenv = new wyone.util.WHashEnv();
		wenv.put(retLabel.name(),ftype.convert());				
		
		ModuleInfo mi = loader.loadModule(module);
		
		for (ModuleInfo.Method function : mi.method(name, ftype)) {
			WFormula mcs = WBool.TRUE;
			
			if (function.postCondition() != null) {
				
				// Ok, here we need to translate the post-condition

				HashMap<WExpr, WExpr> binding = new HashMap<WExpr,WExpr>();
				binding.put(new WVariable("$"), retLabel);
				FunType f_type = function.type();
				List<String> f_params = function.parameterNames();
				
				environment = new HashMap<String, Type>(environment);

				for (int i = 0; i != f_params.size(); ++i) {
					String p_name = f_params.get(i);
					WExpr p_expr = params.get(i);
					Type p_type = f_type.parameters().get(i);
					environment.put(p_name, p_type);
					binding.put(new WVariable(p_name), p_expr);					
				}

				Triple<WFormula, WFormula,WEnvironment> pc = function.postCondition()
						.convertCondition(environment, loader);
		
				wenv.putAll(pc.third());
				
				mcs = WFormulas.and(pc.first(), pc.second()).substitute(
						binding);
			}
			
			if (constraints == null) {
				constraints = mcs;
			} else {
				constraints = WFormulas.or(constraints, mcs);
			}
		}								
		
		return new Triple<WExpr, WFormula, WEnvironment>(retLabel, constraints, wenv);
	}
	
	public boolean equals(Object o) {
		if(o instanceof Invoke) {
			Invoke i = (Invoke) o;
			if (name.equals(i.name)
					&& (target == i.target || (target != null && target
							.equals(i.target)))) {
				if (module == i.module
						|| (module != null && module.equals(i.module))) {
					return ftype.equals(i.ftype) && arguments.equals(i.arguments);					
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
		hc += arguments.hashCode();
		return hc;
	}
	
	public String toString() {
		String r = "";
		if (target != null) {
			r += target.toString() + "->";
		}
		r += name + "(";
		boolean firstTime = true;
		for(Expr e : arguments) {
			if(!firstTime) {
				r += ",";
			}
			firstTime=false;
			r += e;
		}
		return r + ")";
		
	}
}
