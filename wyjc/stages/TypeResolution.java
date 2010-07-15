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

package wyjc.stages;

import static wyjc.util.SyntaxError.*;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.ModuleInfo;
import wyjc.ast.UnresolvedWhileyFile;
import wyjc.ast.UnresolvedWhileyFile.*;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.exprs.process.*;
import wyjc.ast.exprs.real.*;
import wyjc.ast.exprs.set.*;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.stmts.*;
import wyjc.ast.types.*;
import wyjc.ast.types.unresolved.*;
import wyjc.util.*;

public class TypeResolution {	
	private ModuleLoader loader;
	private HashSet<ModuleID> modules;
	private HashMap<NameID,Expr> constants;
	private HashMap<NameID,Type> types;	
	private HashMap<NameID,SyntacticElement> srcs;
	private HashMap<NameID,UnresolvedType> unresolved;
	private HashMap<NameID,List<ModuleInfo.Method>> functions;
	
	public TypeResolution(ModuleLoader loader) {
		this.loader = loader;		
	}
	
	public void resolve(List<UnresolvedWhileyFile> files) {
		modules = new HashSet<ModuleID>();
		constants = new HashMap<NameID,Expr>();
		types = new HashMap<NameID,Type>();
		srcs = new HashMap<NameID,SyntacticElement>();
		unresolved = new HashMap<NameID,UnresolvedType>();
		functions = new HashMap<NameID,List<ModuleInfo.Method>>();
		
		for(UnresolvedWhileyFile f : files) {			
			modules.add(f.id());
		}
		
		generateConstants(files);		
		generateTypes(files);		
		
		for(UnresolvedWhileyFile f : files) {
			resolve(f);
		}
		
		HashMap<String,Type> environment = new HashMap<String,Type>();
		for (NameID key : new HashSet<NameID>(
				types.keySet())) {			
			Type type = types.get(key);
									
			environment.put("$", type);
			Condition c = type.constraint();
			if(c != null) {								
				c = (Condition) check(c, environment).second();				
			}
			types.put(key, Types.recondition(type, c));
		}
							
		for(UnresolvedWhileyFile f : files) {
			check(f);
		}
	}
	
    public void resolve(UnresolvedWhileyFile wf) {		
		for(UnresolvedWhileyFile.Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {				
				ModuleInfo.Method m = resolve((FunDecl)d);
				addFunDecl(m,wf.id());
			}
		}				
	}			
	
    public ModuleInfo.Method resolve(FunDecl f) {
		HashMap<String,Type> environment = new HashMap<String,Type>();
		ArrayList<Type> paramTypes = new ArrayList<Type>();
		
		for(FunDecl.Parameter p : f.parameters()) {
			try {
				if(p.type() == Types.T_VOID) {
					syntaxError("parameter cannot be declared void",f);
				} 
				Type t = expandType(p.type(),types);				
				environment.put(p.name(), t);						
				p.attributes().add(new TypeAttr(t));				
				paramTypes.add(t);
			} catch(ResolveError rex) {
				syntaxError(rex.getMessage(),p,rex);
			}
		}
		
		FunDecl.Return ret = f.returnType();
		Type r_t;
		try {
			r_t = expandType(ret.type(),types);
		} catch(ResolveError rex) {
			syntaxError(rex.getMessage(),ret,rex);
			return null; // unreachable
		}
		ret.attributes().add(new TypeAttr(r_t));	
		
		// FIXME: some problem here as no constraint on function type
		FunType ft = new FunType(r_t,paramTypes,null);
		f.attributes().add(new TypeAttr(ft));
		
		FunDecl.Receiver rec = f.receiver();
		Type recType = null;
		if(rec != null) {
			try {
				r_t = expandType(rec.type(),types);
				recType = r_t;
			} catch(ResolveError rex) {
				syntaxError(rex.getMessage(),ret,rex);
				return null; // unreachable
			}
			rec.attributes().add(new TypeAttr(r_t));
		} 
		
		// FIXME: constraints on receiver are lost.
		return new ModuleInfo.Method(recType, f.name(), ft, f.preCondition(), f
				.postCondition(), f.parameterNames());
	}
	
    
	protected void generateConstants(List<UnresolvedWhileyFile> files) {
		HashMap<NameID,Expr> exprs = new HashMap();
		
		// first construct list.
		for(UnresolvedWhileyFile f : files) {
			for(Decl d : f.declarations()) {
				if(d instanceof ConstDecl) {
					ConstDecl cd = (ConstDecl) d;
					NameID key = new NameID(f.id(),cd.name());
					exprs.put(key, cd.constant());
					srcs.put(key,d);
				}
			}
		}
		
		for(NameID k : exprs.keySet()) {	
			try {
				expandConstant(k,exprs);
			} catch(ResolveError rex) {
				syntaxError(rex.getMessage(),srcs.get(k),rex);
			}
		}
		
		for(NameID k : constants.keySet()) {			
			Expr c = constants.get(k);
			Pair<Type,Expr> p = check(c,new HashMap<String,Type>());
			c = p.second().reduce(new HashMap<String,Type>());
			if(c instanceof Value) {
				Value v = (Value) c;				
				constants.put(k,c);
				if(v instanceof SetVal) {
					SetVal sv = (SetVal) v;
					SetType vt = (SetType) v.type();;
					Condition vstc = null;
					Variable lhs = new Variable("$", v
									.attribute(SourceAttr.class));
					for(Value elem : sv.getValues()) {
						Condition ec = buildEquals(vt.element(),lhs,elem);						
						vstc = vstc == null ? ec : new Or(vstc,ec,c.attribute(SourceAttr.class));
					}					
					types.put(k,Types.recondition(vt.element(),vstc));
				}
			} else {
				syntaxError("invalid constant definition",srcs.get(k));
			}			
		}
	}
	
	protected Expr expandConstant(NameID key,
			HashMap<NameID, Expr> exprs) throws ResolveError {
		
		Expr e = exprs.get(key);
		
		if(constants.get(key) != null) {
			return e;
		} else if(!modules.contains(key.module())) {
			// indicates a non-local key
			ModuleInfo mi = loader.loadModule(key.module());
			return mi.constant(key.name()).constant();
		} else if(e == null) {
			// this indicates a cyclic definition.
			syntaxError("cyclic constant definition encountered",srcs.get(key));
		} else {
			exprs.put(key, null); // mark this node as visited
		}
				
		Set<Variable> uses = e.uses();
		
		// First, we need to compute the correct binding for all constant
        // references.
		
		HashMap<String,Expr> binding = new HashMap<String,Expr>();			
		
		for(Variable u : uses) {
			if(u instanceof Constant) {
				// FIXME: there's a bug here for constants which are defined in
                // external class files.
				Constant c = (Constant) u;
				NameID ck = new NameID(c.module(),c.name());
				Expr v = constants.get(ck);
				if(v == null) {
					v = expandConstant(ck,exprs);
				}
				binding.put(c.name(), v);				
			}
		}
		
		// Finally, apply the binding to produce a fully inline
        // constant expression.
		e = e.substitute(binding);
		constants.put(key, e);
		return e;
	}
	
	protected void generateTypes(List<UnresolvedWhileyFile> files) {		
		HashMap<NameID,SyntacticElement> srcs = new HashMap();
		
		// second construct list.
		for(UnresolvedWhileyFile f : files) {
			for(Decl d : f.declarations()) {
				if(d instanceof TypeDecl) {
					TypeDecl td = (TypeDecl) d;
					NameID key = new NameID(f.id(),td.name());					
					unresolved.put(key, td.type());
					srcs.put(key,d);
				}
			}
		}
		
		// third expand all types
		for(NameID k : unresolved.keySet()) {
			try {
				expandType(k);
			} catch(ResolveError ex) {
				syntaxError(ex.getMessage(),srcs.get(k),ex);
			}
		}
	}
	
	protected Type expandType(NameID key) throws ResolveError {		
		HashMap<NameID, Type> cache = new HashMap<NameID,Type>();			
		Type t = expandTypeHelper(key,cache);		
		t = simplifyRecursiveTypes(t);		
		types.put(key,t);							
		return t;
	}

	/**
	 * The purpose of this method is to simplify the name of any recursive type
	 * variables so that, instead of using the full module and name for each
	 * variable, we use something like X, Y, etc.
	 * 
	 * @param p
	 * @return
	 */
	protected Type simplifyRecursiveTypes(Type p) {
		HashMap<String, Type> t_binding = new HashMap<String, Type>();
		HashMap<String, Type> r_binding = new HashMap<String, Type>();
						
		int nameIdx = 0;
		
		for (RecursiveType t : p.match(RecursiveType.class)) {
			String n = t.name();
			if (!t_binding.containsKey(n) && nameIdx < names.length) {
				String name = names[nameIdx++];
				t_binding.put(n, new RecursiveType(name, null, null));
			}
			if(t.type() != null) {
				r_binding.put(t.name(), t);
			}
		}		
		
		Type ntype = p.substitute(t_binding);
		
		if(p.constraint() == null) {
			return p;
		}
		
		// Conditions (e.g. type gates) are difficult, since they involve types
		// that were generated during the type graph traversal. This means that
		// they may contain "open" recursive types. That is, types whose parent
		// was on the stack when the condition was created, leaving just the
		// empty leaf to be used in the condition. We need to unroll these leafs
		// by one level. The problem is that we can't do the unrolling when the
		// conditions are created since we don't know who they're parents are at
		// that point. Probably a more elegant solution is to separate the
		// generation of types from the generation and/or check of the
		// conditions.
		//
		// This is what the r_binding is for ---> closing the open loops.

		HashMap<Expr,Expr> c_binding = new HashMap();		
		
		for (TypeEquals tg : p.constraint().match(TypeEquals.class)) {
			Type t = tg.lhsTest().substitute(r_binding).substitute(t_binding);
			c_binding.put(tg, new TypeEquals(t, tg.variable(), tg.lhs(),
					tg.rhs(), tg.attributes()));
		}
					
		for (TypeGate tg : p.constraint().match(TypeGate.class)) {
			Type t = tg.lhsTest().substitute(r_binding).substitute(t_binding);
			c_binding.put(tg, new TypeGate(t, tg.variable(), tg.lhs(),
					tg.rhs(), tg.attributes()));
		}
		
		return Types.recondition(ntype, (Condition) p.constraint().replace(c_binding));
	}
	protected static final String[] names = {"X","Y","Z","U","V","W","P","Q","R","S","T"}; 
	private static boolean isRecursive(NameID root, Type type) {
		String rootName = root.toString();
		for(RecursiveType rt : type.match(RecursiveType.class)) {
			if(rt.name().equals(rootName)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isRecursive(Type type) {
		return !type.match(RecursiveType.class).isEmpty();
	}
	
	protected Type expandTypeHelper(NameID key,
			HashMap<NameID, Type> cache) throws ResolveError {

		Type t = types.get(key);
		
		if(t != null && !isRecursive(t)) {
			return t;
		} else if(!modules.contains(key.module())) {			
			// indicates a non-local key
			ModuleInfo mi = loader.loadModule(key.module());
			ModuleInfo.TypeDef td = mi.type(key.name());			
			return td.type();
		}
		
		// Now, check for a cached expansion.
		Type cached = cache.get(key);					
		if(cached != null) { return cached; }		
		
		// following is needed to terminate any recursion
		cache.put(key, new RecursiveType(key.toString(),null,null));
		
		// Ok, expand the type properly then
		UnresolvedType ut = unresolved.get(key);
		
		t = expandType(ut, cache);
				 		
		if(isRecursive(key,t)) {
			// recursive case
			t = new RecursiveType(key.toString(),t,null);			
		} 
		
		cache.put(key, t);
		
		// Done
		return t;		
	}	
	
	protected Type expandType(UnresolvedType ut,
			HashMap<NameID, Type> cache) throws ResolveError {							
		
		if(ut instanceof Type) {
			// covers all primitive types etc
			return (Type) ut;
		} else if(ut instanceof UserDefType) {
			UserDefType ult = (UserDefType) ut;			;
			Type et = expandTypeHelper(new NameID(ult.module(), ult
					.name()), cache);			
			if(et.isExistential()) {
				return new NamedType(ult.module(),ult.name(), et);				
			} else {
				return et;
			}
		} else if(ut instanceof UnresolvedListType) {		
			UnresolvedListType ult = (UnresolvedListType) ut;
			Type tc = expandType(ult.element(),cache); 					
			return new ListType(tc);
		} else if(ut instanceof UnresolvedSetType) {
			UnresolvedSetType ult = (UnresolvedSetType) ut;
			Type tc = expandType(ult.element(),cache); 			
			return new SetType(tc);					
		} else if(ut instanceof UnresolvedTupleType) {
			UnresolvedTupleType utt = (UnresolvedTupleType) ut;			
			HashMap<String,Type> types = new HashMap<String,Type>();
			for(Map.Entry<String,UnresolvedType> e : utt.types().entrySet()) {
				String key = e.getKey();
				Type tc = expandType(e.getValue(),cache);				
				types.put(key, tc);		
			}					
			return new TupleType(types);
		} else if(ut instanceof UnresolvedUnionType) {
			UnresolvedUnionType utt = (UnresolvedUnionType) ut;
			
			Type t = Types.T_VOID;			
			
			for(UnresolvedType bound : utt.types()) {				
				Type rb = expandType(bound,cache);
				t = Types.leastUpperBound(t,rb);								
			}			
			
			return t;			
		} else  {			
			// must be process type
			UnresolvedProcessType ult = (UnresolvedProcessType) ut;
			Type tc = expandType(ult.element(),cache);			
			return new ProcessType(tc);					
		} 
	}
	
	/**
	 * This method expands a type, whilst also checking that the expanded constraint is type safe. 
	 */
	protected Type expandAndCheck(UnresolvedType t,
			SyntacticElement elem) throws ResolveError {				
		Type tc = expandType(t,types);		
		Condition c = tc.constraint();		
		if(c != null) {
			HashMap<String,Type> env = new HashMap<String,Type>();
			env.put("$",tc);			
			c = (Condition) check(c,env).second();
			renumber(c,elem.attribute(SourceAttr.class));
		}				
		return Types.recondition(tc,c);
	}
	
	protected void addFunDecl(ModuleInfo.Method fun, ModuleID id) {
		NameID key = new NameID(id,fun.name());
		List<ModuleInfo.Method> funs = functions.get(key);
		if(funs == null) {
			funs = new ArrayList<ModuleInfo.Method>();
			functions.put(key, funs);
		}
		// FIXME: could attempt to remove duplicate cases here
		funs.add(fun);
	}
	
	public void check(UnresolvedWhileyFile wf) {	
		for(UnresolvedWhileyFile.Decl d : wf.declarations()) {
			try {
				if(d instanceof ImportDecl) {
					// do nothing
				} else if(d instanceof ConstDecl) {					
					check((ConstDecl)d, wf.id());
				} else if(d instanceof TypeDecl) {					
					check((TypeDecl)d,wf);
				} else {					
					check((FunDecl)d);
				}				
			} catch(SyntaxError se) {
				throw se;
			} catch(Exception rex) {
				syntaxError(rex.getMessage(),d,rex);
			}
		}		
	}
	
	public void check(ConstDecl d, ModuleID mid) {
		try {
			NameID key = new NameID(mid,d.name());
			Value v = (Value) constants.get(key);						
			d.setConstant(v); 
			d.attributes().add(new TypeAttr(v.type()));
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure",d,ex);
		}
	}
	
	public void check(TypeDecl d, UnresolvedWhileyFile wf) {		
		try {
			NameID key = new NameID(wf.id(),d.name());
			Type t = types.get(key);
			expandAndCheck(d.type(),d);										
			d.attributes().add(new TypeAttr(t));			
		} catch(SyntaxError se) {			
			throw se;
		} catch(Exception ex) {		
			syntaxError("internal failure",d,ex);
		}
	}
	
	public void check(FunDecl f) throws ResolveError {
		HashMap<String,Type> environment = new HashMap<String,Type>();		
		HashMap<String,Type> declared = new HashMap<String,Type>();
						
		for(FunDecl.Parameter p : f.parameters()) {			
			if(p.name().equals("this")) {
				syntaxError("parameter name not permitted",p);
			} else if(environment.containsKey(p.name())) {
				syntaxError("duplicate parameter name",p);
			}			
			Type tc = expandAndCheck(p.type(),p);			
			environment.put(p.name(), tc);
			declared.put(p.name(), tc);			
		}
				
		FunDecl.Return ret = f.returnType();
		Type rp = expandAndCheck(ret.type(),ret);
		
		// FIXME: lost return / receiver constraint?
		
		FunDecl.Receiver rec = f.receiver();
		if(rec != null) {
			rp = expandAndCheck(rec.type(),rec);
			environment.put("this",rp);						
		}
		
		if(f.preCondition() != null) {
			Pair<Type,Expr> precond = check(f.preCondition(),environment);
			f.setPreCondition((Condition) precond.second());
		}
		
		ArrayList<Stmt> nstmts = new ArrayList<Stmt>();
		
		for(Stmt s : f.statements()) {
			nstmts.add(check(s,environment,declared,f));
		}
				
		f.statements().clear();
		f.statements().addAll(nstmts);
		
		if(f.postCondition() != null) {
			if(!(f.returnType().type() == Types.T_VOID)) {
				environment.put("$",rp);
			}
			Pair<Type,Expr> postcond = check(f.postCondition(),environment);
			f.setPostCondition((Condition)postcond.second());
		}				
	}
	
	protected Stmt check(Stmt s, HashMap<String, Type> environment,
			HashMap<String, Type> declared, FunDecl f) {
		try {			
			if (s instanceof Skip || s instanceof Read) {
				return s;
			} else if(s instanceof Print) {
				return check((Print)s,environment);
			} else if (s instanceof Assign) {
				return check((Assign)s, environment, declared);			
			} else if (s instanceof IfElse) {
				return check((IfElse)s,environment,declared,f);	
			} else if (s instanceof Return) {
				return check((Return)s,environment,f);			
			} else if (s instanceof Assertion) {
				return check((Assertion) s,environment);
			} else if (s instanceof Check) {
				return check((Check) s,environment);
			} else if (s instanceof Invoke) {
				return (Stmt) check((Invoke) s, environment).second();
			} else if (s instanceof UnresolvedVarDecl) {
				return check((UnresolvedVarDecl) s, environment, declared);
			} else if (s instanceof Spawn) {
				return (Stmt) (check((Spawn) s, environment).second());
			} else {
				syntaxError("unknown statement encountered: " + s.getClass().getName(), s);
				return null;
			}
		} catch(SyntaxError se) {						
			throw se;			
		} catch(Exception e) {
			syntaxError(e.getMessage(),s,e);
			return null;
		}
	}
	
	protected Stmt check(UnresolvedVarDecl s,
			HashMap<String, Type> environment, HashMap<String, Type> declared) {
		Expr initialiser = s.initialiser();
		Type s_type = null;		
		
		if (environment.get(s.name()) != null) {
			syntaxError("duplicate variable declaration.", s);
		} else if (s.type() == Types.T_VOID) {
			// cannot declare a variable to have void type!
			syntaxError("variable cannot be declared void", s);
		} else {
			try {
				s_type = expandAndCheck(s.type(),s);				
			} catch(ResolveError rex) {
				syntaxError(rex.getMessage(),s,rex);
			}
		}		
		if (initialiser != null) {
			Pair<Type, Expr> t = check(initialiser, environment);
			initialiser = t.second();
			checkSubtype(s_type, t.first(), s);
			environment.put(s.name(), t.first());
			declared.put(s.name(), s_type);
		} else {
			declared.put(s.name(), s_type);
			environment.put(s.name(), s_type);
		}
		
		return new VarDecl(s_type, s.name(),
				initialiser, s.attribute(SourceAttr.class));
	}	
	
	protected Stmt check(Print s, HashMap<String,Type> environment) {
		Pair<Type,Expr> e = check(s.expr(),environment);
		checkSubtype(new ListType(Types.T_INT(null)),e.first(),s);
		return new Print(e.second(),s.attributes());
	}

	protected Stmt check(Assign s, HashMap<String, Type> environment,
			HashMap<String, Type> declared) throws ResolveError {		
		Pair<Type,Expr> lhs = check(s.lhs(),environment);		
		Pair<Type,Expr> rhs = check(s.rhs(),environment);
						
		if(lhs.second() instanceof Variable) {						
			// This represents the full update of a variable						
			Variable v = (Variable) lhs.second();
			
			if(v.name().equals("this")) {
				// this is basic santiy checka
				syntaxError("cannot assign variable: " + v,v);
			}
			
			Type dp = declared.get(v.name());			
			checkSubtype(dp,rhs.first(),s);			
			environment.put(v.name(), rhs.first());
			// no implict cast required --- by definition variable has correct
			// type.
			return new Assign((LVal) lhs.second(), rhs.second(), s.attributes());
		} else {
			// This represents a partial update of a variable. For the moment,
			// these are not supported as, frankly, they are pretty trick to do
			// at the best of times.
			checkSubtype(lhs.first(),rhs.first(),s);

			return new Assign((LVal) lhs.second(), rhs
					.second(), s.attributes());
		}		
	}	
	
	protected Stmt check(IfElse s, HashMap<String, Type> environment,
			HashMap<String, Type> declared, FunDecl f) {
		Pair<Type,Expr> cond = check(s.condition(),environment);
		checkSubtype(Types.T_BOOL(null),cond.first(),s);
		ArrayList<Stmt> tb = new ArrayList<Stmt>();
		ArrayList<Stmt> fb = null;
		for(Stmt st : s.trueBranch()) {			
			tb.add(check(st,environment,declared,f));	
		}
		if(s.falseBranch() != null) {
			fb = new ArrayList<Stmt>();
			for(Stmt st : s.falseBranch()) {
				fb.add(check(st,environment,declared,f));
			}
		}
		return new IfElse((Condition) cond.second(),tb,fb,s.attributes());
	}

	protected Stmt check(Return s, HashMap<String,Type> environment, FunDecl f) {
		Expr expr = s.expr();
		if(expr != null) {
			Pair<Type,Expr> rhs = check(s.expr(),environment);
			Type lhs = f.returnType().attribute(TypeAttr.class).type();
			checkSubtype(lhs,rhs.first(),s);
			return new Return(rhs.second(), s.attributes());
		} else {
			return s;
		}
	}
	
	protected Stmt check(Assertion s, HashMap<String,Type> environment) {
		Pair<Type,Expr> cond = check(s.condition(),environment);
		checkSubtype(Types.T_BOOL(null),cond.first(),s);
		return new Assertion((Condition) cond.second(),s.attributes());
	}
	
	protected Stmt check(Check s, HashMap<String,Type> environment) {
		Pair<Type,Expr> cond = check(s.condition(),environment);
		checkSubtype(Types.T_BOOL(null),cond.first(),s);
		return new Check(s.message(), (Condition) cond.second(), s.attributes());
	}
	
	protected Pair<Type,Expr> check(Invoke s, HashMap<String,Type> environment) throws ResolveError {						
		List<Expr> args = s.arguments();
		
		ArrayList<Type> ptypes = new ArrayList();
		ArrayList<Expr> nargs = new ArrayList();
		for(Expr e : args) {			
			Pair<Type,Expr> rhs = check(e,environment);
			ptypes.add(rhs.first());
			nargs.add(rhs.second());
		}
		Expr target = s.target();
		Type receiver = null;
		if(target != null) {			
			Pair<Type,Expr> tmp = check(target,environment);
			target = tmp.second();
			receiver = tmp.first();			
		}
		
		FunType funtype = bindFunction(s.module(), s.name(), receiver, ptypes,s);
		
		if(funtype == null) {
			syntaxError("invalid or ambiguous method call",s);
		}
				
		for(int i=0;i!=nargs.size();++i) {			
			Expr narg = nargs.get(i);				
			nargs.set(i,narg);
		}
		
		Type rt = funtype.returnType();
		
		Invoke ns = new Invoke(s.name(),target,nargs,s.attributes());		
		ns.resolve(s.module(),funtype);
		return new Pair<Type,Expr>(rt,ns);
	}
	
	protected Pair<Type,Expr> check(Expr e, HashMap<String,Type> environment) {
		Pair<Type,Expr> retType;
		try {
			if (e instanceof IntVal) {
				retType = new Pair<Type,Expr>(Types.T_INT(null), e);
			} else if (e instanceof RealVal) {
				retType = new Pair<Type,Expr>(Types.T_REAL(null), e);
			} else if (e instanceof BoolVal) {
				retType = new Pair<Type,Expr>(Types.T_BOOL(null), e);
			} else if (e instanceof RangeVal) {
				retType = new Pair<Type,Expr>(((Value)e).type(), e);
			} else if (e instanceof Constant) {			
				retType = check((Constant) e, environment);
			} else if (e instanceof Variable) {
				retType = check((Variable) e, environment);
			} else if (e instanceof BinOp) {
				retType = check((BinOp) e, environment);
			} else if (e instanceof IntNegate) {
				retType = check((IntNegate) e, environment);
			} else if(e instanceof UnresolvedTypeEquals) {
				retType = check((UnresolvedTypeEquals)e,environment);				
			} else if (e instanceof Invoke) {
				retType = check((Invoke) e, environment);
				if (retType.first() == Types.T_VOID) {
					syntaxError("method has void return type.", e);
					return null;
				}
			} else if (e instanceof Not) {
				retType = check((Not) e, environment);
			} else if (e instanceof RangeGenerator) {
				retType = check((RangeGenerator) e, environment);
			} else if (e instanceof ListLength) {
				retType = check((ListLength) e, environment);
			} else if (e instanceof Some) {
				retType = check((Some) e, environment);
			} else if (e instanceof None) {
				retType = check((None) e, environment);
			} else if(e instanceof StringVal) {
				retType = check((StringVal) e, environment);
			} else if (e instanceof ListVal) {
				retType = check((ListVal) e, environment);
			} else if (e instanceof ListGenerator) {
				retType = check((ListGenerator) e, environment);
			} else if (e instanceof ListAccess) {
				retType = check((ListAccess) e, environment);
			} else if (e instanceof ListSublist) {
				retType = check((ListSublist) e, environment);
			} else if (e instanceof SetVal) {
				retType = check((SetVal) e, environment);
			} else if (e instanceof SetGenerator) {
				retType = check((SetGenerator) e, environment);
			} else if (e instanceof SetComprehension) {
				retType = check((SetComprehension) e, environment);
			} else if (e instanceof SetLength) {
				retType = check((SetLength) e, environment);
			} else if (e instanceof TupleVal) {
				retType = check((TupleVal) e, environment);
			} else if (e instanceof TupleGenerator) {
				retType = check((TupleGenerator) e, environment);
			} else if (e instanceof TupleAccess) {
				retType = check((TupleAccess) e, environment);
			} else if (e instanceof TypeGate) {
				retType = check((TypeGate) e, environment);
			} else if (e instanceof TypeEquals) {
				retType = check((TypeEquals) e, environment);
			} else if (e instanceof Spawn) {
				retType = check((Spawn) e, environment);
			} else if (e instanceof ProcessAccess) {
				retType = check((ProcessAccess) e, environment);
			} else {
				syntaxError("unknown expression encountered: " + e.getClass().getName(), e);
				return null;
			}
		} catch(SyntaxError se) {
			throw se;			
		} catch(Exception ex) {
			syntaxError("internal failure", e, ex);
			return null;
		}

		return retType;
	}
	
	protected Pair<Type,Expr> check(RangeGenerator e, HashMap<String,Type> environment) {					
		Pair<Type,Expr> start = check(e.start(),environment);
		Pair<Type,Expr> end = check(e.end(),environment);
		Type t = Types.leastUpperBound(start.first(), end.first());
		
		return new Pair(new ListType(t), new RangeGenerator(start.second(), end
				.second(), e.attributes()));
	}
	
	protected Pair<Type,Expr> check(ListAccess e, HashMap<String,Type> environment) {
		Pair<Type,Expr> lhs = check(e.source(), environment);
		Pair<Type,Expr> rhs = check(e.index(), environment);
		
		checkType(rhs.first(),IntType.class,e.index());
		
		ListType at = checkType(lhs.first(),ListType.class,e.source());
		return new Pair(at.element(), new ListAccess( lhs.second(),
				 rhs.second(), e.attributes()));		
	}
	
	protected Pair<Type,Expr> check(ListSublist e, HashMap<String,Type> environment) {
		Pair<Type,Expr> lhs = check(e.source(), environment);
		Pair<Type,Expr> start = check(e.start(), environment);
		Pair<Type,Expr> end = check(e.end(), environment);
		
		checkType(start.first(),IntType.class,e.start());
		checkType(end.first(),IntType.class,e.end());		
		ListType at = checkType(lhs.first(),ListType.class,e.source());
		
		return new Pair(at, new ListSublist( lhs.second(),
				 start.second(), end.second(), e.attributes()));		
	}
	
	protected Pair<Type,Expr> check(ListGenerator e, HashMap<String,Type> environment) {
		List<Expr> exprs = e.getValues();		
		Type t = Types.T_VOID;
		ArrayList<Expr> nexprs = new ArrayList<Expr>();				
		for(int i=0;i!=exprs.size();++i) {
			Pair<Type,Expr> et = check(exprs.get(i), environment);
			t = Types.leastUpperBound(t,et.first());
			nexprs.add(et.second());
		}
		
		return new Pair(new ListType(t), new ListGenerator(nexprs, e
				.attributes()));
	}
	
	protected Pair<Type,Expr> check(StringVal e, HashMap<String,Type> environment) {
		return check((ListVal)e,environment);		
	}
	
	protected Pair<Type,Expr> check(ListVal e, HashMap<String,Type> environment) {
		List<Value> exprs = e.getValues();
		Type t = Types.T_VOID;
			
		for(int i=0;i!=exprs.size();++i) {
			Pair<Type,Expr> et = check(exprs.get(i), environment);
			t = Types.leastUpperBound(t,et.first());			
		}
		
		return new Pair(new ListType(t), new ListVal(exprs,e.attributes()));
	}
	
	protected Pair<Type,Expr> check(ListLength e, HashMap<String,Type> environment) {
		Pair<Type,Expr> lhs = check(e.mhs(), environment);	
		Type ft = flattern(lhs.first());
		if(ft instanceof ListType) {
			checkSubtype(new ListType(Types.T_ANY),lhs.first(),e.mhs());
			return new Pair(Types.T_INT(null),new ListLength(lhs.second(),e.attributes()));
		} else {
			checkSubtype(new SetType(Types.T_ANY),lhs.first(),e.mhs());	
			return new Pair(Types.T_INT(null),new SetLength(lhs.second(),e.attributes()));			
		}		
	}
			
	protected Pair<Type,Expr> check(SetComprehension e, HashMap<String,Type> environment) {		
		HashMap<String,Type> valueEnv = (HashMap<String,Type>) environment.clone();
		List<Pair<String,Expr>> sources = e.sources();		
		List<Pair<String,Expr>> nsources = new ArrayList<Pair<String,Expr>>();
		
		Type anySetType = new SetType(Types.T_ANY);
		Type anySetListType = Types.leastUpperBound(anySetType,new ListType(Types.T_ANY));
		
		for(Pair<String,Expr> me : sources) {			
			String s = me.first();
			
			if(environment.get(s) != null) {
				syntaxError("variable " + s + " already defined",e);
			}
			
			Expr src = me.second();
			Pair<Type,Expr> p = check(src,valueEnv); 
			nsources.add(new Pair<String,Expr>(s,  p.second()));
			
			checkSubtype(anySetListType,p.first(),src);
			Type pt = flattern(p.first());			
			if(pt instanceof SetType) {
				SetType st = (SetType) pt;			
				valueEnv.put(s,st.element());
			} else {
				ListType st = (ListType) pt;			
				valueEnv.put(s,st.element());
			}
		}
				
		Condition c = e.condition();
		
		if(c != null) {
			c =(Condition) check(e.condition(),valueEnv).second();
		}
		
		Pair<Type,Expr> p = check(e.sign(),valueEnv);		
		Expr ne = new SetComprehension(p.second(),nsources,c,e.attributes());
		
		return new Pair<Type,Expr>(new SetType(p.first()),ne);
	}
	
	protected Pair<Type,Expr> check(SetGenerator e, HashMap<String,Type> environment) {
		List<Expr> exprs = e.getValues();
		
		ArrayList<Expr> nexprs = new ArrayList<Expr>();
		
		Type t = Types.T_VOID;
				
		for(Expr v : exprs) {
			Pair<Type,Expr> et = check(v, environment);			
			t = Types.leastUpperBound(t,et.first());			
			nexprs.add( et.second());
		}
		
		return new Pair(new SetType(t),
				new SetGenerator(nexprs, e.attributes()));
	}
	
	protected Pair<Type,Expr> check(SetVal e, HashMap<String,Type> environment) {
		Set<Value> exprs = e.getValues();
		
		Type t = Types.T_VOID;
		for(Value v : exprs) {
			Pair<Type,Expr> et = check(v, environment);			
			t = Types.leastUpperBound(t,et.first());			
		}				
		
		return new Pair(new SetType(t), new SetVal(exprs, e
				.attributes()));
	}
	protected Pair<Type,Expr> check(SetLength e, HashMap<String,Type> environment) {
		Pair<Type,Expr> lhs = check(e.mhs(), environment);	

		checkSubtype(new SetType(Types.T_ANY),lhs.first(),e.mhs());	
		return new Pair(Types.T_INT(null),new SetLength(lhs.second(),e.attributes()));			
	}		
	protected Pair<Type,Expr> check(Some e, HashMap<String,Type> environment) {
		Pair<Type,Expr> lhs = check(e.mhs(), environment);		
		checkSubtype(new SetType(Types.T_ANY),lhs.first(),e.mhs());		
		return new Pair(Types.T_BOOL(null), new Some((SetComprehension) lhs
				.second(), e.attributes()));
	}
	
	protected Pair<Type,Expr> check(None e, HashMap<String,Type> environment) {
		Pair<Type,Expr> lhs = check(e.mhs(), environment);		
		checkSubtype(new SetType(Types.T_ANY),lhs.first(),e.mhs());		
		return new Pair(Types.T_BOOL(null), new None((SetComprehension) lhs
				.second(), e.attributes()));
	}
	
	protected Pair<Type,Expr> check(TupleAccess e, HashMap<String,Type> environment) {
		Pair<Type,Expr> lhs = check(e.source(), environment);			
		TupleType at = Types.effectiveTupleType(lhs.first(),lhs.second()); 		
		Type et = at.get(e.name());
		
		if(et == null) {						
			syntaxError("tuple has no field named: " + e.name(),e);
		}				
		
		return new Pair(et, new TupleAccess(lhs.second(), e.name(), e
				.attributes()));		
	}
	
	protected Pair<Type,Expr> check(TupleGenerator e, HashMap<String,Type> environment) {				
		HashMap<String,Expr> exprs = new HashMap();
		HashMap<String,Type> types = new HashMap();
		
		for(Map.Entry<String,Expr>  p : e) {		
			Pair<Type,Expr> et = check(p.getValue(), environment);
			types.put(p.getKey(), et.first());
			exprs.put(p.getKey(), et.second());			
		}		
		return new Pair(new TupleType(types),new TupleGenerator(exprs,e.attributes()));
	}
	
	protected Pair<Type,Expr> check(TupleVal e, HashMap<String,Type> environment) {				
		HashMap<String,Value> exprs = new HashMap();
		HashMap<String,Type> types = new HashMap();
		
		for(Map.Entry<String,Value>  p : e.values().entrySet()) {		
			Pair<Type,Expr> et = check(p.getValue(), environment);
			types.put(p.getKey(), et.first());					
		}		
		return new Pair(new TupleType(types),new TupleVal(exprs,e.attributes()));
	}
			
	protected Pair<Type,Expr> check(Not e, HashMap<String,Type> environment) {
		Pair<Type,Expr> lhs = check(e.mhs(), environment);
		checkSubtype(lhs.first(),Types.T_BOOL(null),e.mhs());		
		return new Pair(lhs.first(), new Not((Condition) lhs.second(), e
				.attributes()));
	}			
	
	protected Pair<Type,Expr> check(IntNegate e, HashMap<String,Type> environment) {
		Pair<Type,Expr> lhs = check(e.mhs(),environment);		
		if(lhs.first() instanceof RealType) {
			return new Pair(lhs.first(),new RealNegate( lhs.second(),e.attributes()));
		} else if(lhs.first() instanceof IntType) {
			return new Pair(lhs.first(),new IntNegate( lhs.second(),e.attributes()));
		}
		syntaxError("expecting int or real type, found " + lhs + ".",e.mhs());
		return null;
	}
	
	protected Pair<Type,Expr> check(BinOp e, HashMap<String,Type> environment) {		
		Pair<Type,Expr> lhs = check(e.lhs(),environment);
		Pair<Type,Expr> rhs = check(e.rhs(),environment);
				
		if(e instanceof And) {
			checkSubtype(Types.T_BOOL(null), lhs.first(), e);
			checkSubtype(Types.T_BOOL(null), rhs.first(), e);
			return new Pair(Types.T_BOOL(null), new And((Condition) lhs.second(),
					(Condition) rhs.second(), e.attributes()));
		} else if(e instanceof Or) {
			checkSubtype(Types.T_BOOL(null), lhs.first(), e);
			checkSubtype(Types.T_BOOL(null), rhs.first(), e);
			return new Pair(Types.T_BOOL(null), new Or((Condition) lhs.second(),
					(Condition) rhs.second(), e.attributes()));
		} else if(e instanceof ListElementOf) {								
			checkSubtype(new ListType(Types.T_ANY), rhs.first(), e);
			ListType rhst = (ListType) rhs.first();
			checkSubtype(rhst.element(),lhs.first(),e);
			return new Pair(Types.T_BOOL(null), new ListElementOf(lhs.second(),
					rhs.second(), e.attributes()));			
		} else if(e instanceof SetElementOf) {			
			if(rhs.first() instanceof SetType) {				
				SetType rhst = (SetType) rhs.first();
				checkSubtype(rhst.element(),lhs.first(),e);
				return new Pair(Types.T_BOOL(null), new SetElementOf(lhs.second(),
						 rhs.second(), e.attributes()));			
			} else {			
				checkSubtype(new ListType(Types.T_ANY), rhs.first(), e);
				ListType rhst = (ListType) rhs.first();
				checkSubtype(rhst.element(),lhs.first(),e);
				return new Pair(Types.T_BOOL(null), new ListElementOf(lhs.second(),
						 rhs.second(), e.attributes()));
			}
		} else if(e instanceof Subset) {
			checkSubtype(new SetType(Types.T_ANY), lhs.first(), e);
			checkSubtype(new SetType(Types.T_ANY), rhs.first(), e);			
			SetType rhst = (SetType) rhs.first();
			SetType lhst = (SetType) lhs.first();
			checkSubtype(lhst.element(),rhst.element(),e);
			return new Pair(Types.T_BOOL(null), new Subset( lhs.second(),
					 rhs.second(), e.attributes()));
		} else if(e instanceof SubsetEq) {
			checkSubtype(new SetType(Types.T_ANY), lhs.first(), e);
			checkSubtype(new SetType(Types.T_ANY), rhs.first(), e);
			SetType rhst = (SetType) rhs.first();
			SetType lhst = (SetType) lhs.first();
			checkSubtype(lhst.element(),rhst.element(),e);
			return new Pair(Types.T_BOOL(null), new SubsetEq( lhs.second(),
					 rhs.second(), e.attributes()));
		} else if(e instanceof SetUnion) {			
			checkSubtype(new SetType(Types.T_ANY), lhs.first(), e);
			checkSubtype(new SetType(Types.T_ANY), rhs.first(), e);
			SetType rhst = (SetType) rhs.first();
			SetType lhst = (SetType) lhs.first();		
			return new Pair(Types.leastUpperBound(lhst, rhst), new SetUnion( lhs
					.second(),  rhs.second(), e.attributes()));
		} else if(e instanceof SetIntersection) {			
			checkSubtype(new SetType(Types.T_ANY), lhs.first(), e);
			checkSubtype(new SetType(Types.T_ANY), rhs.first(), e);
			SetType rhst = (SetType) rhs.first();
			SetType lhst = (SetType) lhs.first();
									
			return new Pair(Types.leastUpperBound(lhst, rhst),
					new SetIntersection( lhs.second(),  rhs.second(), e
							.attributes()));
		} else if(e instanceof SetDifference) {			
			checkSubtype(lhs.first(), new SetType(Types.T_ANY), e);
			checkSubtype(rhs.first(), new SetType(Types.T_ANY), e);
			SetType rhst = (SetType) rhs.first();
			SetType lhst = (SetType) lhs.first();
						
			checkSubtype(lhst.element(),rhst.element(),e);
			checkSubtype(rhst.element(),lhst.element(),e);
			
			return new Pair(lhst, new SetDifference( lhs.second(),
					 rhs.second(), e.attributes()));
		} 
		
		Type lhs_t = flattern(lhs.first());
		Type rhs_t = flattern(rhs.first());						
				
		Type target = Types.leastUpperBound(lhs_t, rhs_t);
		
		// Now, consider tuple types			
		if(lhs_t instanceof TupleType && rhs_t instanceof TupleType) {
			TupleType t1 = (TupleType) lhs_t;
			TupleType t2 = (TupleType) rhs_t;
			
			if(!t1.types().equals(t2.types())) {
				syntaxError("cannot compare type " + t1 + " with type " + t2,e);
			}
									
			if (e instanceof IntEquals || e instanceof TupleEquals) {
				return new Pair(Types.T_BOOL(null), new TupleEquals(
						 lhs.second(),rhs.second(), e.attributes()));
			} else if (e instanceof IntNotEquals  || e instanceof TupleNotEquals) {
				return new Pair(Types.T_BOOL(null), new TupleNotEquals(
						 lhs.second(),rhs.second(), e.attributes()));
			}
		} else if(lhs_t instanceof TupleType || rhs_t instanceof TupleType) {
			syntaxError("expecting type " + lhs,rhs.second());
		}
		
		// now consider list types
		if(lhs_t instanceof ListType && rhs_t instanceof ListType) {
			ListType t1 = (ListType) lhs_t;
			ListType t2 = (ListType) rhs_t;
												
			if (!Types.isBaseSubtype(t1, t2)
					&& !Types.isBaseSubtype(t2, t1)) {
				syntaxError("cannot compare type " + t1 + " with type " + t2,e);
			}
			
			if (e instanceof IntEquals || e instanceof ListEquals) {
				return new Pair(Types.T_BOOL(null), new ListEquals(
						 lhs.second(),rhs.second(), e
								.attributes()));
			} else if (e instanceof IntNotEquals || e instanceof ListNotEquals) {
				return new Pair(Types.T_BOOL(null), new ListNotEquals(lhs.second(),
						rhs.second(), e.attributes()));
			} else if(e instanceof IntAdd) {				
				return new Pair(Types.leastUpperBound(t1, t2), new ListAppend(
						lhs.second(), rhs.second(), e.attributes()));
			}
		} else if(lhs_t instanceof ListType || rhs_t instanceof ListType) {
			syntaxError("expecting type " + lhs_t + ", got " + rhs_t, rhs
					.second());
		}
				
		// now consider set types
		if(lhs_t instanceof SetType && rhs_t instanceof SetType) {
			SetType t1 = (SetType) lhs_t;
			SetType t2 = (SetType) rhs_t;
												
			if(!Types.isBaseSubtype(t1, t2) && !Types.isBaseSubtype(t2, t1)) {
				syntaxError("cannot compare type " + t1 + " with type " + t2,e);
			}
			
			if (e instanceof IntEquals || e instanceof SetEquals) {
				return new Pair(Types.T_BOOL(null), new SetEquals(lhs.second(), rhs
						.second(), e.attributes()));
			} else if (e instanceof IntNotEquals || e instanceof SetNotEquals) {
				return new Pair(Types.T_BOOL(null), new SetNotEquals(lhs.second(),
						rhs.second(), e.attributes()));
			} else if (e instanceof IntNotEquals || e instanceof SetNotEquals) {
				return new Pair(Types.T_BOOL(null), new SetNotEquals(lhs.second(),
						rhs.second(), e.attributes()));
			} else if(e instanceof IntAdd) {
				return new Pair(
						Types.leastUpperBound(t1,t2),
						new SetUnion(lhs.second(), rhs.second(), e.attributes()));
			} else if(e instanceof IntSub) {
				return new Pair(
						Types.leastUpperBound(t1,t2),
						new SetDifference(lhs.second(), rhs.second(), e.attributes()));
			} 
					
		} else if(lhs_t instanceof SetType || rhs_t instanceof SetType) {
			syntaxError("expecting type " + lhs_t + ", got " + rhs_t, rhs
					.second());
		}
		
		// now consider boolean types
		if(lhs_t instanceof BoolType && rhs_t instanceof BoolType) {
			if (e instanceof IntEquals || e instanceof BoolEquals) {				
				return new Pair(Types.T_BOOL(null), new BoolEquals(lhs.second(),
						rhs.second(), e.attributes()));
			} else if (e instanceof IntNotEquals || e instanceof BoolNotEquals) {				
				return new Pair(Types.T_BOOL(null), new BoolNotEquals(lhs.second(),
						rhs.second(), e.attributes()));
			}
		} else if(lhs_t instanceof BoolType || rhs_t instanceof BoolType) {
			syntaxError("expecting type " + lhs_t + ", got " + rhs_t, rhs
					.second());
		}
		/**
		 * At this stage, we've processed all of the non-numeric binary
		 * operators. So, all we have left are the numeric binary operators.
		 */									
		
		if (target instanceof RealType || rhs_t instanceof RealType) {			
			target = Types.T_REAL(null);
		} else if(!(target instanceof IntType)) {
			syntaxError("expecting int or real type",lhs.second());
		} else if(!(rhs_t instanceof IntType)) {
			syntaxError("expecting int or real type",rhs.second());
		} 
		
		if (target instanceof IntType) {
			if (e instanceof IntEquals) {
				return new Pair(Types.T_BOOL(null), new IntEquals(lhs.second(), rhs
						.second(), e.attributes()));
			} else if (e instanceof IntNotEquals) {
				return new Pair(Types.T_BOOL(null), new IntNotEquals(lhs.second(),
						rhs.second(), e.attributes()));
			} else if (e instanceof IntLessThan) {
				return new Pair(Types.T_BOOL(null), new IntLessThan(lhs.second(),
						rhs.second(), e.attributes()));
			} else if (e instanceof IntLessThanEquals) {
				return new Pair(Types.T_BOOL(null), new IntLessThanEquals(lhs
						.second(), rhs.second(), e.attributes()));
			} else if (e instanceof IntGreaterThan) {
				return new Pair(Types.T_BOOL(null), new IntGreaterThan(
						lhs.second(), rhs.second(), e.attributes()));
			} else if (e instanceof IntGreaterThanEquals) {
				return new Pair(Types.T_BOOL(null), new IntGreaterThanEquals(lhs
						.second(), rhs.second(), e.attributes()));
			} else if (e instanceof IntAdd) {
				return new Pair(Types.T_INT(null), new IntAdd(lhs.second(), rhs
						.second(), e.attributes()));
			} else if (e instanceof IntSub) {
				return new Pair(Types.T_INT(null), new IntSub(lhs.second(), rhs
						.second(), e.attributes()));
			} else if (e instanceof IntMul) {
				return new Pair(Types.T_INT(null), new IntMul(lhs.second(), rhs
						.second(), e.attributes()));
			} else if (e instanceof IntDiv) {
				return new Pair(Types.T_INT(null), new IntDiv(lhs.second(), rhs
						.second(), e.attributes()));
			}
		} else {
			if (e instanceof IntEquals || e instanceof RealEquals) {
				return new Pair(Types.T_BOOL(null), new RealEquals(lhs.second(),
						rhs.second(), e.attributes()));
			} else if (e instanceof IntNotEquals || e instanceof RealNotEquals) {
				return new Pair(Types.T_BOOL(null), new RealNotEquals(lhs.second(),
						rhs.second(), e.attributes()));
			}						
			
			if (e instanceof IntLessThan || e instanceof RealLessThan) {
				return new Pair(Types.T_BOOL(null), new RealLessThan(lhs.second(),
						rhs.second(), e.attributes()));
			} else if (e instanceof IntLessThanEquals
					|| e instanceof RealLessThanEquals) {
				return new Pair(Types.T_BOOL(null), new RealLessThanEquals(lhs
						.second(), rhs.second(), e.attributes()));
			} else if (e instanceof IntGreaterThan
					|| e instanceof RealGreaterThan) {
				return new Pair(Types.T_BOOL(null), new RealGreaterThan(lhs
						.second(), rhs.second(), e.attributes()));
			} else if (e instanceof IntGreaterThanEquals
					|| e instanceof RealGreaterThanEquals) {
				return new Pair(Types.T_BOOL(null), new RealGreaterThanEquals(lhs
						.second(), rhs.second(), e.attributes()));
			} else if (e instanceof IntAdd || e instanceof RealAdd) {
				return new Pair(Types.T_REAL(null), new RealAdd(lhs.second(), rhs
						.second(), e.attributes()));
			} else if (e instanceof IntSub || e instanceof RealSub) {
				return new Pair(Types.T_REAL(null), new RealSub(lhs.second(), rhs
						.second(), e.attributes()));
			} else if (e instanceof IntMul || e instanceof RealMul) {
				return new Pair(Types.T_REAL(null), new RealMul(lhs.second(), rhs
						.second(), e.attributes()));
			} else if (e instanceof IntDiv || e instanceof RealDiv) {
				return new Pair(Types.T_REAL(null), new RealDiv(lhs.second(), rhs
						.second(), e.attributes()));
			}
		}
				
		syntaxError("cannot add types " + lhs.first() + " and " + rhs.first(),e);
		return null;
	}
	
	protected Pair<Type, Expr> check(UnresolvedTypeEquals ueq,
			HashMap<String, Type> environment) throws ResolveError {
		Pair<Type, Expr> lhs = check(ueq.lhs(), environment);
		Type rhs_t = expandAndCheck(ueq.type(),ueq);
		Type lhs_t = lhs.first();		

		// now check it makes sense
		if (!Types.isBaseSubtype(lhs_t, rhs_t)
				&& !Types.isBaseSubtype(rhs_t, lhs_t)) {
			syntaxError("cannot match type " + lhs_t + " against " + rhs_t, ueq);
		}
		
		Condition condition = (Condition) check(ueq.rhs(),environment).second();  
		String var = Variable.freshVar();				
		
		// FIXME: I think there's a problem here as the condition needs to have
		// all occurences of lhs replaced with var.
		
		return new Pair<Type, Expr>(Types.T_BOOL(null), new TypeEquals(rhs_t, var, lhs
				.second(), condition, ueq.attributes()));
	}
	
	protected Pair<Type,Expr> check(Variable v, HashMap<String,Type> environment) {
		Type t = environment.get(v.name());		
		
		if (t == null) {
			syntaxError("variable " + v.name() + " has not been declared.", v);			
		} 
				
		return new Pair<Type,Expr>(t,v);
	}
	
	protected Pair<Type, Expr> check(Constant c,
			HashMap<String,Type> environment) throws ResolveError {
		if (c.module() == null) {
			syntaxError("unresolved constant: " + c.name(), c);
		}
		NameID key = new NameID(c.module(), c.name());
		Value v;		
		if(modules.contains(key.module())) {
			v = (Value) constants.get(key);	
		} else {
			// indicates a non-local key
			ModuleInfo mi = loader.loadModule(key.module());
			v = mi.constant(key.name()).constant();				 
		}				
		return new Pair<Type, Expr>(v.type(), v);
	}
	
	protected Pair<Type, Expr> check(TypeGate c, HashMap<String,Type> environment) {				
		Pair<Type,Expr> lhs = check(c.lhs(),environment);			
		
		if(!Types.isBaseSubtype(c.lhsTest(),lhs.first())) {			
			// we have to clone the environment, since it's effects only apply
			// to the contained condition.
			environment = new HashMap<String,Type>(environment);
			environment.put(c.variable(), c.lhsTest());
		}
		
		Pair<Type,Expr> rhs = check(c.rhs(),environment);
		
		return new Pair<Type, Expr>(rhs.first(), new TypeGate(c.lhsTest(), c
				.variable(), lhs.second(), (Condition) rhs.second(), c
				.attributes()));
	}
	
	protected Pair<Type, Expr> check(TypeEquals c, HashMap<String,Type> environment) {				
		Pair<Type,Expr> lhs = check(c.lhs(),environment);			
		
		if(!Types.isBaseSubtype(c.lhsTest(), lhs.first())) {			
			// we have to clone the environment, since it's effects only apply
			// to the contained condition.
			environment = new HashMap<String,Type>(environment);
			environment.put(c.variable(), c.lhsTest());
		}
		
		Pair<Type,Expr> rhs = check(c.rhs(),environment);
		
		return new Pair<Type, Expr>(rhs.first(), new TypeEquals(c.lhsTest(), c
				.variable(), lhs.second(), (Condition) rhs.second(), c
				.attributes()));
	}
		
	protected Pair<Type, Expr> check(Spawn c, HashMap<String,Type> environment) {
		Pair<Type,Expr> p = check(c.mhs(),environment);		
		// there is nothing to do here really
		c = new Spawn(p.second(), c.attributes());
		return new Pair<Type, Expr>(new ProcessType(p.first()), c);
	}	
	
	protected Pair<Type, Expr> check(ProcessAccess c, HashMap<String,Type> environment) {
		Pair<Type,Expr> p = check(c.mhs(),environment);		
		ProcessType pt = (ProcessType) p.first();
		// there is nothing to do here really
		c = new ProcessAccess(p.second(), c.attributes());
		return new Pair<Type, Expr>(pt.element(), c);
	}	
	
	
	protected Condition buildEquals(Type t, Expr lhs, Expr rhs) {		
		if(t instanceof IntType) {
			return new IntEquals(lhs,rhs);
		} else if(t instanceof RealType) {
			return new RealEquals(lhs,rhs);
		} else if(t instanceof ListType) {
			return new ListEquals(lhs,rhs);
		} else if(t instanceof SetType) {
			return new SetEquals(lhs,rhs);
		} else if(t instanceof TupleType) {
			return new TupleEquals(lhs,rhs);
		} else {
			syntaxError("cannot equate " + lhs + " with " + rhs,rhs);
			return null;
		}
	}
		
	protected <T extends Type> T checkType(Type t, Class<T> clazz,
			SyntacticElement elem) {
		if(t instanceof NamedType) {
			t = ((NamedType)t).type();
		}
		if (clazz.isInstance(t)) {
			return (T) t;
		} else {
			syntaxError("expected type " + clazz.getName() + " found " + t,
					elem);
			return null;
		}
	}	
	
	protected FunType bindFunction(ModuleID mid, String name, Type receiver,
			List<Type> paramTypes,
			SyntacticElement elem) throws ResolveError {
		
		FunType candidate = null;
		List<Type> candidateTypes = null;
								
		for (ModuleInfo.Method fun : lookupMethod(mid,name)) {								
			FunType ft = (FunType) fun.type();			
			Type funrec = null;
			if(fun.receiver() != null) {
				funrec = fun.receiver();
			}
			
			if (receiver == funrec
					|| (receiver != null && funrec != null && Types
							.isBaseSubtype(funrec, receiver))) {				
				// receivers match up OK ... 
				if (ft.parameters().size() == paramTypes.size()
						&& fun.name().equals(name)
						&& isSubtype(ft.parameters(), paramTypes, elem)
						&& (candidateTypes == null || isSubtype(candidateTypes, ft
								.parameters(), elem))) {							
					// This declaration is a candidate. Now, we need to see if our
					// candidate type signature is as precise as possible.
					if(candidateTypes == null) {					
						candidateTypes = ft.parameters();
						candidate = ft;
					} else if(isSubtype(candidateTypes,ft.parameters(), elem)) {
						candidate = ft;
					} else if(!isSubtype(ft.parameters(), candidateTypes, elem)) {					
						return null;
					}								
				}
			}
		}				
		
		return candidate;
	}
	
	protected List<ModuleInfo.Method> lookupMethod(ModuleID mid, String name)
			throws ResolveError {
		if (modules.contains(mid)) {
			NameID key = new NameID(mid, name);
			return functions.get(key);
		} else {
			ModuleInfo module = loader.loadModule(mid);
			return module.method(name);
		}
	}
	
	protected boolean isSubtype(List<Type> p1types, List<Type> p2types,
			SyntacticElement elem) {
		
		for (int i = 0; i != p1types.size(); ++i) {
			Type p1 = p1types.get(i);
			Type p2 = p2types.get(i);			
			if (!Types.isBaseSubtype(p1, p2)) {				
				return false;
			}
		}

		return true;
	}
	
	protected Type flattern(Type t) {
		if(t instanceof NamedType) {
			return ((NamedType)t).type();
		}
		return t;
	}
	
	/**
	 * <p>This method checks that type t2 is a subtype of type t1.</p>
	 * @param t1
	 * @param t2
	 * @param elem
	 */
	protected void checkSubtype(Type t1, Type t2, SyntacticElement elem) {		
		if (!Types.isBaseSubtype(t1, t2)) {
			syntaxError("expected type " + t1 + ", got type " + t2 + ".", elem);
		}
	}
	
	/**
	 * This method simply renumbers every syntactic element.
	 * 
	 * @param e
	 * @param loc
	 */
	protected void renumber(Expr e, SourceAttr loc) {
		for(SyntacticElement elem : e.match(SyntacticElement.class)) {
			SourceAttr old = elem.attribute(SourceAttr.class);
			if(old != null) {
				elem.attributes().remove(old);
			} 
			elem.attributes().add(loc);
		}
	}
}
