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

package wyc;

import java.util.*;

import wyautl.lang.*;
import wyc.lang.UnresolvedType;
import wyc.lang.WhileyFile;
import wyc.util.Nominal;
import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.util.*;
import wyil.util.path.Path;

/**
 * <p>
 * A Name Resolver is responsible for searching the WHILEYPATH to resolve given
 * names and packages. For example, we may wish to resolve a name "Reader"
 * within a given import context of "import whiley.io.*". In such case, the name
 * resolver will first locate the package "whiley.io", and then decide whether
 * there is a module named "Reader".
 * </p>
 * 
 * 
 * @author David J. Pearce
 * 
 */
public final class NameResolver {
	private final ModuleLoader loader;
	
	/**
	 * A map from module identifiers to WhileyFile objects. This is required to
	 * permit registration of source files during compilation.
	 */
	private HashMap<ModuleID, WhileyFile> files = new HashMap<ModuleID, WhileyFile>();

	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import whiley.lang.*</code>
	 * corresponds to the triple <code>("whiley.lang",*,null)</code>.
	 */
	private HashMap<Triple<PkgID,String,String>,ArrayList<ModuleID>> importCache = new HashMap();
	
	public NameResolver(ModuleLoader loader) {
		this.loader = loader;
	}
	
	/**
	 * Register a given WhileyFile with this loader. This ensures that when
	 * WhileyFile requests are made, this WhileyFile will be used instead of
	 * searching for it on the whileypath.
	 * 
	 * @param WhileyFile
	 *            --- WhileyFile to preregister.
	 */
	public void register(WhileyFile WhileyFile) {		
		files.put(WhileyFile.module, WhileyFile);			
	}
	
	/**
	 * This function checks whether the supplied package exists or not.
	 * 
	 * @param pkg
	 *            The package whose existence we want to check for.
	 * 
	 * @return true if the package exists, false otherwise.
	 */
	public boolean isPackage(PkgID pkg) {
		try {
			loader.resolvePackage(pkg);
			return true;
		} catch(ResolveError e) {
			return false;
		}
	}		
	
	/**
	 * This function checks whether the supplied name exists or not.
	 * 
	 * @param nid
	 *            The name whose existence we want to check for.
	 * 
	 * @return true if the package exists, false otherwise.
	 */
	public boolean isName(NameID nid) {		
		ModuleID mid = nid.module();
		WhileyFile wf = files.get(mid);
		if(wf != null) {
			return wf.hasName(nid.name());
		} else {
			try {
				Module m = loader.loadModule(mid);
				return m.hasName(nid.name());
			} catch(ResolveError e) {
				return false;
			}
		}
	}		
	
	/**
	 * This methods attempts to resolve the correct package for a named item,
	 * given a list of imports. Resolving the correct package may require
	 * loading modules as necessary from the WHILEYPATH and/or compiling modules
	 * for which only source code is currently available.
	 * 
	 * @param name
	 *            A module name without package specifier.
	 * @param imports
	 *            A list of import declarations to search through. Imports are
	 *            searched in order of appearance.
	 * @return The resolved name.
	 * @throws ResolveError
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(String name, List<Import> imports)
			throws ResolveError {		
		for (Import imp : imports) {
			if (imp.matchName(name)) {
				for (ModuleID mid : matchImport(imp)) {					
					NameID nid = new NameID(mid, name); 
					if (isName(nid)) {
						return nid;
					}					
				}
			}
		}

		throw new ResolveError("name not found: " + name);
	}

	/**
	 * This methods attempts to resolve the given list of names into a single
	 * named item (e.g. type, method, constant, etc). For example,
	 * <code>["whiley","lang","Math","max"]</code> would be resolved, since
	 * <code>whiley.lang.Math.max</code> is a valid function name. In contrast,
	 * <code>["whiley","lang","Math"]</code> does not resolve since
	 * <code>whiley.lang.Math</code> refers to a module.
	 * 
	 * @param names
	 *            A list of components making up the name, which may include the
	 *            package and enclosing module.
	 * @param imports
	 *            A list of import declarations to search through. Imports are
	 *            searched in order of appearance.
	 * @return The resolved name.
	 * @throws ResolveError
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(List<String> names, List<Import> imports) throws ResolveError {
		if(names.size() == 1) {
			return resolveAsName(names.get(0),imports);
		} else if(names.size() == 2) {
			String name = names.get(1);
			ModuleID mid = resolveAsModule(names.get(0),imports);		
			NameID nid = new NameID(mid, name); 
			if (isName(nid)) {
				return nid;
			} 
		} else {
			String name = names.get(names.size()-1);
			String module = names.get(names.size()-2);
			PkgID pkg = new PkgID(names.subList(0,names.size()-2));
			ModuleID mid = new ModuleID(pkg,module);
			NameID nid = new NameID(mid, name); 
			if (isName(nid)) {
				return nid;
			} 			
		}
		
		String name = null;
		for(String n : names) {
			if(name != null) {
				name = name + "." + n;
			} else {
				name = n;
			}			
		}
		throw new ResolveError("name not found: " + name);
	}	
	
	/**
	 * This method attempts to resolve the given name as a module name, given a
	 * list of imports.
	 * 
	 * @param name
	 * @param imports
	 * @return
	 * @throws ResolveError
	 */
	public ModuleID resolveAsModule(String name, List<Import> imports)
			throws ResolveError {
		
		for (Import imp : imports) {
			for(ModuleID mid : matchImport(imp)) {				
				if(mid.module().equals(name)) {
					return mid;
				}
			}
		}
				
		throw new ResolveError("module not found: " + name);
	}
	
	/**
	 * Resolve a given type by identifiying all unknown names and replacing them
	 * with nominal types.
	 * 
	 * @param t
	 * @param imports
	 * @return
	 * @throws ResolveError
	 */
	public Nominal<Type> resolveAsType(UnresolvedType t, List<Import> imports) throws ResolveError {		
		Type nominalType = resolve(t,imports);
		Type rawType = expand(nominalType);
		return new Nominal<Type>(nominalType,rawType);
	}
	
	public Value resolveAsConstant(NameID nid) throws ResolveError {				
		WhileyFile wf = files.get(nid.module());
		if (wf != null) {			
			Value v = wf.constant(nid.name());
			if(v != null) {
				return v;
			} else {				
				throw new ResolveError("unable to find constant " + nid);
			}
		}		
		Module module = loader.loadModule(nid.module());
		Module.ConstDef cd = module.constant(nid.name());
		if(cd != null) {
			return cd.constant();
		} else {
			throw new ResolveError("unable to find constant " + nid);
		}
	}
	
	/**
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 * 
	 * @param imp
	 * @return
	 */
	private List<ModuleID> matchImport(Import imp) {			
		Triple<PkgID,String,String> key = new Triple(imp.pkg,imp.module,imp.name);
		ArrayList<ModuleID> matches = importCache.get(key);
		if(matches != null) {
			// cache hit
			return matches;
		} else {					
			// cache miss
			matches = new ArrayList<ModuleID>();
			for (PkgID pid : matchPackage(imp.pkg)) {
				try {					
					for(ModuleID mid : loader.loadPackage(pid)) {
						if (imp.matchModule(mid.module())) {
							matches.add(mid);
						}
					}					
				} catch (ResolveError ex) {
					// dead code
				} 
			}
			importCache.put(key, matches);
		}
		return matches;
	}
	
	/**
	 * This method takes a given package id from an import declaration, and
	 * expands it to find all matching packages. Note, the package id may
	 * contain various wildcard characters to match multiple actual packages.
	 * 
	 * @param imp
	 * @return
	 */
	private List<PkgID> matchPackage(PkgID pkg) {
		ArrayList<PkgID> matches = new ArrayList<PkgID>();
		try {
			loader.resolvePackage(pkg);
			matches.add(pkg);
		} catch(ResolveError er) {}
		return matches;
	}

	/**
	 * Bind function is responsible for determining the true type of a method or
	 * function being invoked. To do this, it must find the function/method
	 * with the most precise type that matches the argument types.
	 * 
	 * @param nid
	 * @param qualification
	 * @param paramTypes
	 * @param elem
	 * @return
	 * @throws ResolveError
	 */
	public Type.Function bindFunctionOrMethod(NameID nid, 
			ArrayList<Type> paramTypes) throws ResolveError {

		Type.Function target = (Type.Function) Type.Function(Type.T_ANY,
				Type.T_ANY, paramTypes);
		Type.Function candidate = null;				
		
		List<Nominal<Type.Function>> targets = lookupFunction(nid.module(),nid.name()); 		
		
		for (Nominal<Type.Function> nft : targets) {
			Type.Function ft = nft.raw();
			if (ft.params().size() == paramTypes.size()						
					&& paramSubtypes(ft, target)
					&& (candidate == null || paramSubtypes(candidate,ft))) {					
				candidate = ft;								
			}
		}				
		
		// Check whether we actually found something. If not, print a useful
		// error message.
		if(candidate == null) {			
			String msg = "no match for " + nid.name() + parameterString(paramTypes);
			boolean firstTime = true;
			int count = 0;
			for (Nominal<Type.Function> nft : targets) {
				Type.Function ft = (Type.Function) nft.nominal();
				if(firstTime) {
					msg += "\n\tfound: " + nid.name() +  parameterString(ft.params());
				} else {
					msg += "\n\tand: " + nid.name() +  parameterString(ft.params());
				}				
				if(++count < targets.size()) {
					msg += ",";
				}
			}
			
			// need to think about this one
			throw new ResolveError(msg);
		}
		
		return candidate;
	}
	
	public Type.Method bindMessage(NameID nid, Type.Process receiver,
			ArrayList<Type> paramTypes) throws ResolveError {

		Type.Method target = (Type.Method) Type.Method(receiver, Type.T_ANY,
				Type.T_ANY, paramTypes);
		Type.Method candidate = null;				
		
		List<Nominal<Type.Function>> targets = lookupFunction(nid.module(),nid.name()); 
		
		for (Nominal<Type.Function> nft : targets) {
			Type.Function ft = nft.raw();
			if(ft instanceof Type.Method) { 				
				Type.Method mt = (Type.Method) ft; 
				Type funrec = mt.receiver();	
				if (receiver == funrec
						|| (receiver != null && funrec != null && Type
						.isImplicitCoerciveSubtype(receiver, funrec))) {					
					// receivers match up OK ...				
					if (mt.params().size() == paramTypes.size()						
							&& paramSubtypes(mt, target)
							&& (candidate == null || paramSubtypes(candidate,mt))) {					
						candidate = mt;					
					}
				}
			}
		}				
		
		// Check whether we actually found something. If not, print a useful
		// error message.
		if(candidate == null) {
			String rec = "::";
			if(receiver != null) {				
				rec = receiver.toString() + "::";
			}
			String msg = "no match for " + rec + nid.name() + parameterString(paramTypes);
			boolean firstTime = true;
			int count = 0;
			for(Nominal<Type.Function> nft : targets) {
				rec = "";
				Type.Function ft = (Type.Function) nft.nominal();
				if(ft instanceof Type.Method) {
					Type.Method mt = (Type.Method) ft;
					if(mt.receiver() != null) {
						rec = mt.receiver().toString();
					}
					rec = rec + "::";
				}
				if(firstTime) {
					msg += "\n\tfound: " + rec + nid.name() +  parameterString(ft.params());
				} else {
					msg += "\n\tand: " + rec + nid.name() +  parameterString(ft.params());
				}				
				if(++count < targets.size()) {
					msg += ",";
				}
			}
			throw new ResolveError(msg);			
		}
		
		return candidate;
	}
	
	private boolean paramSubtypes(Type.Function f1, Type.Function f2) {		
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if(f1_params.size() == f2_params.size()) {
			for(int i=0;i!=f1_params.size();++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);				
				if(!Type.isImplicitCoerciveSubtype(f1_param,f2_param)) {				
					return false;
				}
			}			
			return true;
		}
		return false;
	}
	
	private String parameterString(List<Type> paramTypes) {
		String paramStr = "(";
		boolean firstTime = true;
		for(Type t : paramTypes) {
			if(!firstTime) {
				paramStr += ",";
			}
			firstTime=false;
			paramStr += t;
		}
		return paramStr + ")";
	}
	
	private List<Nominal<Type.Function>> lookupFunction(ModuleID mid,
			String name) throws ResolveError {
		WhileyFile wf = files.get(mid);
		if (wf != null) {
			List<Type.Function> nominals = wf.functionOrMethod(name);
			ArrayList<Nominal<Type.Function>> r = new ArrayList();
			for (Type.Function tf : nominals) {
				r.add(new Nominal<Type.Function>(tf, (Type.Function) expand(tf)));
			}
			return r;
		}

		Module module = loader.loadModule(mid);
		ArrayList<Nominal<Type.Function>> r = new ArrayList();
		for (Module.Method f : module.method(name)) {
			// FIXME: loss of nominal information here
			r.add(new Nominal<Type.Function>(f.type(), f.type()));
		}
		return r;
	}
	
	/**
	 * The following method resolves a given type using a list of import
	 * statements.
	 * 
	 * @param t
	 * @param imports
	 * @return
	 * @throws ResolveError
	 */
	private Type resolve(UnresolvedType t, List<Import> imports) throws ResolveError {		
		if (t instanceof UnresolvedType.Any) {
			return Type.T_ANY;
		} else if (t instanceof UnresolvedType.Void) {
			return Type.T_VOID;
		} else if (t instanceof UnresolvedType.Null) {
			return Type.T_NULL;
		} else if (t instanceof UnresolvedType.Bool) {
			return Type.T_BOOL;
		} else if (t instanceof UnresolvedType.Byte) {
			return Type.T_BYTE;
		} else if (t instanceof UnresolvedType.Char) {
			return Type.T_CHAR;
		} else if (t instanceof UnresolvedType.Int) {
			return Type.T_INT;
		} else if (t instanceof UnresolvedType.Real) {
			return Type.T_REAL;
		} else if (t instanceof UnresolvedType.Strung) {
			return Type.T_STRING;
		} else if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;
			Type element = resolve(lt.element,imports);
			return Type.List(element,false);
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;
			Type element = resolve(st.element,imports);
			return Type.Set(element,false);
		} else if(t instanceof UnresolvedType.Dictionary) {
			UnresolvedType.Dictionary st = (UnresolvedType.Dictionary) t;
			Type key = resolve(st.key,imports);
			Type value = resolve(st.value,imports);
			return Type.Dictionary(key,value);
		} else if(t instanceof UnresolvedType.Record) {
			UnresolvedType.Record tt = (UnresolvedType.Record) t;
			HashMap<String,Type> fields = new HashMap<String,Type>();
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {				
				Type type = resolve(e.getValue(),imports);
				fields.put(e.getKey(), type);
			}
			return Type.Record(tt.isOpen,fields);
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			ArrayList<Type> types = new ArrayList<Type>();
			for(UnresolvedType e : tt.types) {
				Type type = resolve(e,imports);
				types.add(type);
			}
			return Type.Tuple(types);
		} else if(t instanceof UnresolvedType.Nominal) {
			// This case corresponds to a user-defined type. This will be
			// defined in some module (possibly ours), and we need to identify
			// what module that is here, and save it for future use.
			UnresolvedType.Nominal dt = (UnresolvedType.Nominal) t;						
			NameID nid = resolveAsName(dt.names, imports);			
			return Type.Nominal(nid);
		} else if(t instanceof UnresolvedType.Not) {	
			UnresolvedType.Not ut = (UnresolvedType.Not) t;
			Type type = resolve(ut.element,imports);
			return Type.Negation(type);
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			ArrayList<Type> bounds = new ArrayList<Type>();
			for(UnresolvedType b : ut.bounds) {
				Type bound = resolve(b,imports);
				bounds.add(bound);
			}
			return Type.Union(bounds);
		} else if(t instanceof UnresolvedType.Intersection) {
			UnresolvedType.Intersection ut = (UnresolvedType.Intersection) t;
			for(UnresolvedType b : ut.bounds) {
				resolve(b,imports);
			}
			throw new RuntimeException("CANNOT COPE WITH INTERSECTIONS!");
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			Type type = resolve(ut.element,imports);
			return Type.Process(type);
		} else {	
			UnresolvedType.Fun ut = (UnresolvedType.Fun) t;
			Type ret = resolve(ut.ret,imports);			
			
			ArrayList<Type> params = new ArrayList<Type>();
			for(UnresolvedType p : ut.paramTypes) {
				params.add(resolve(p,imports));
			}
			
			if(ut.receiver != null) {
				Type receiver = resolve(ut.receiver,imports);
				return Type.Method(receiver, ret, Type.T_VOID, params);
			} else {
				return Type.Function(ret, Type.T_VOID, params);
			}
		} 
	}
	
	/**
	 * This method fully expands a given type.
	 * 
	 * @param type
	 * @return
	 */
	public Type expand(Type type) throws ResolveError {				
		if(type instanceof Type.Leaf && !(type instanceof Type.Nominal)) {
			return type; // no expansion possible
		}
		Automaton automaton = Type.destruct(type);

		// first, check whether it's actually worth doing anything
		boolean hasNominal = false;
		for(Automaton.State state : automaton.states) {
			if(state.kind == Type.K_NOMINAL) {
				hasNominal = true;
				break;
			}
		}		
		if(hasNominal) {		
			// ok, possibility of expansion exists
			ArrayList<Automaton.State> states = new ArrayList<Automaton.State>();
			HashMap<NameID,Integer> roots = new HashMap<NameID,Integer>();
			expand(0,automaton,roots,states);
			automaton = new Automaton(states);
			return Type.construct(automaton);
		} else {
			return type;
		}
	}

	private int expand(int index, Automaton automaton,
			HashMap<NameID, Integer> roots, ArrayList<Automaton.State> states)
			throws ResolveError {
		
		Automaton.State state = automaton.states[index];
		int kind = state.kind;
		
		if(kind == Type.K_NOMINAL) {
			NameID key = (NameID) state.data;
			return expand(key,roots,states);
		} else {
			int myIndex = states.size();			
			states.add(null);
			int[] ochildren = state.children;
			int[] nchildren = new int[ochildren.length];
			for(int i=0;i!=ochildren.length;++i) {
				nchildren[i] = expand(ochildren[i],automaton,roots,states);
			}
			boolean deterministic = kind != Type.K_UNION;		
			Automaton.State myState = new Automaton.State(kind,state.data,deterministic,nchildren);
			states.set(myIndex,myState);
			return myIndex;
		}
	}

	private int expand(NameID key, HashMap<NameID, Integer> roots,
			ArrayList<Automaton.State> states) throws ResolveError {
		
		// First, check the various caches we have
		Integer root = roots.get(key);			
		if (root != null) { return root; } 		
		
		// check whether this type is external or not
		WhileyFile wf = files.get(key.module());
		if (wf == null) {						
			// indicates a non-local key which we can resolve immediately			
			Module mi = loader.loadModule(key.module());
			Module.TypeDef td = mi.type(key.name());	
			return append(td.type(),states);			
		} 
		
		Type type = wf.type(key.name());
		if(type == null) {
			// FIXME: need a better error message!
			throw new ResolveError("type not present in module: " + key.name());
		}
		
		// following is needed to terminate any recursion
		roots.put(key, states.size());

		// now, expand the given type fully			
		if(type instanceof Type.Leaf) {
			// to avoid unnecessarily creating an array of size one
			int myIndex = states.size();
			int kind = Type.leafKind((Type.Leaf)type);			
			Object data = Type.leafData((Type.Leaf)type);
			states.add(new Automaton.State(kind,data,true,Automaton.NOCHILDREN));
			return myIndex;
		} else {
			return expand(0, Type.destruct(type), roots, states);
		}
		
		// TODO: performance can be improved here, but actually assigning the
		// constructed type into a cache of previously expanded types cache.
		// This is challenging, in the case that the type may not be complete at
		// this point. In particular, if it contains any back-links above this
		// index there could be an issue.
	}	
		
	private static int append(Type type, ArrayList<Automaton.State> states) {
		int myIndex = states.size();
		Automaton automaton = Type.destruct(type);
		Automaton.State[] tStates = automaton.states;
		int[] rmap = new int[tStates.length];
		for (int i = 0, j = myIndex; i != rmap.length; ++i, ++j) {
			rmap[i] = j;
		}
		for (Automaton.State state : tStates) {
			states.add(Automata.remap(state, rmap));
		}
		return myIndex;
	}
}
