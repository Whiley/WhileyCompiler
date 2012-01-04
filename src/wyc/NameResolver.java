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

import static wyil.util.ErrorMessages.CYCLIC_CONSTANT_DECLARATION;
import static wyil.util.ErrorMessages.INVALID_BINARY_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_BOOLEAN_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_CONSTANT_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_LIST_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_NUMERIC_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_SET_EXPRESSION;
import static wyil.util.ErrorMessages.errorMessage;
import static wyil.util.SyntaxError.syntaxError;

import java.util.*;

import wyautl.lang.*;
import wyc.lang.Attributes;
import wyc.lang.Expr;
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
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 * 
	 * @param imp
	 * @return
	 */
	private List<ModuleID> matchImport(WhileyFile.Import imp) {			
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

	// =========================================================================
	// ResolveAsName
	// =========================================================================		
	
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
	public NameID resolveAsName(String name, List<WhileyFile.Import> imports)
			throws ResolveError {		
		for (WhileyFile.Import imp : imports) {
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
	public NameID resolveAsName(List<String> names, List<WhileyFile.Import> imports) throws ResolveError {
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
	public ModuleID resolveAsModule(String name, List<WhileyFile.Import> imports)
			throws ResolveError {
		
		for (WhileyFile.Import imp : imports) {
			for(ModuleID mid : matchImport(imp)) {				
				if(mid.module().equals(name)) {
					return mid;
				}
			}
		}
				
		throw new ResolveError("module not found: " + name);
	}
	
	// =========================================================================
	// ResolveAsType
	// =========================================================================
	
	/**
	 * Resolve a given type by identifying all unknown names and replacing them
	 * with nominal types.
	 * 
	 * @param t
	 * @param imports
	 * @return
	 * @throws ResolveError
	 */
	public Nominal<Type> resolveAsType(UnresolvedType t,
			List<WhileyFile.Import> imports) throws ResolveError {
		Type nominalType = resolveAsType(t, imports, true);
		Type rawType = resolveAsType(t, imports, false);
		return new Nominal<Type>(nominalType, rawType);
	}
	
	private Type resolveAsType(UnresolvedType t, List<WhileyFile.Import> imports,
			boolean nominal) throws ResolveError {
		if(t instanceof UnresolvedType.Primitive) { 
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
			} else {
				throw new ResolveError("unrecognised type encountered ("
						+ t.getClass().getName() + ")");
			}
		} else {
			ArrayList<Automaton.State> states = new ArrayList<Automaton.State>();
			HashMap<NameID,Integer> roots = new HashMap<NameID,Integer>();
			resolveAsType(t,imports,states,roots,nominal);
			return Type.construct(new Automaton(states));
		}
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
	private int resolveAsType(UnresolvedType t, List<WhileyFile.Import> imports,
			ArrayList<Automaton.State> states, HashMap<NameID, Integer> roots,
			boolean nominal) throws ResolveError {				
		if(t instanceof UnresolvedType.Primitive) {
			return resolveAsType((UnresolvedType.Primitive)t,imports,states);
		} 
		
		int myIndex = states.size();
		int myKind;
		int[] myChildren;
		Object myData = null;
		boolean myDeterministic = true;
		
		states.add(null); // reserve space for me
		
		if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;
			myKind = Type.K_LIST;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(lt.element,imports,states,roots,nominal);
			myData = false;
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;
			myKind = Type.K_SET;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(st.element,imports,states,roots,nominal);
			myData = false;
		} else if(t instanceof UnresolvedType.Dictionary) {
			UnresolvedType.Dictionary st = (UnresolvedType.Dictionary) t;
			myKind = Type.K_DICTIONARY;
			myChildren = new int[2];
			myChildren[0] = resolveAsType(st.key,imports,states,roots,nominal);
			myChildren[1] = resolveAsType(st.value,imports,states,roots,nominal);			
		} else if(t instanceof UnresolvedType.Record) {
			UnresolvedType.Record tt = (UnresolvedType.Record) t;
			HashMap<String,UnresolvedType> ttTypes = tt.types;
			Type.Record.State fields = new Type.Record.State(tt.isOpen,ttTypes.keySet());
			Collections.sort(fields);			
			myKind = Type.K_RECORD;
			myChildren = new int[fields.size()];
			for(int i=0;i!=fields.size();++i) {	
				String field = fields.get(i);
				myChildren[i] = resolveAsType(ttTypes.get(field),imports,states,roots,nominal);
			}			
			myData = fields;
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			ArrayList<UnresolvedType> ttTypes = tt.types;
			myKind = Type.K_TUPLE;
			myChildren = new int[ttTypes.size()];
			for(int i=0;i!=ttTypes.size();++i) {
				myChildren[i] = resolveAsType(ttTypes.get(i),imports,states,roots,nominal);
				
			}			
		} else if(t instanceof UnresolvedType.Nominal) {
			// This case corresponds to a user-defined type. This will be
			// defined in some module (possibly ours), and we need to identify
			// what module that is here, and save it for future use.
			UnresolvedType.Nominal dt = (UnresolvedType.Nominal) t;						
			NameID nid = resolveAsName(dt.names, imports);
			if(nominal) {
				myKind = Type.K_NOMINAL;
				myData = nid;
				myChildren = Automaton.NOCHILDREN;
			} else {
				resolveAsType(nid,imports,states,roots);
				return myIndex;
			}
		} else if(t instanceof UnresolvedType.Not) {	
			UnresolvedType.Not ut = (UnresolvedType.Not) t;
			myKind = Type.K_NEGATION;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element,imports,states,roots,nominal);			
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			ArrayList<UnresolvedType.NonUnion> utTypes = ut.bounds;
			myKind = Type.K_UNION;
			myChildren = new int[utTypes.size()];
			for(int i=0;i!=utTypes.size();++i) {
				myChildren[i] = resolveAsType(utTypes.get(i),imports,states,roots,nominal);
				
			}	
			myDeterministic = false;
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			myKind = Type.K_PROCESS;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element,imports,states,roots,nominal);		
		} else {	
			UnresolvedType.FunctionOrMethodOrMessage ut = (UnresolvedType.FunctionOrMethodOrMessage) t;			
			ArrayList<UnresolvedType> utParamTypes = ut.paramTypes;
			UnresolvedType receiver = null;
			int start = 0;
			
			if(ut instanceof UnresolvedType.Message) {
				UnresolvedType.Message mt = (UnresolvedType.Message) ut;
				receiver = mt.receiver;
				if(receiver == null) {
					myKind = Type.K_HEADLESS;
				} else {
					myKind = Type.K_METHOD;
					start++;
				}
			} else {
				myKind = Type.K_FUNCTION;
			}
			
			myChildren = new int[start + 2 + utParamTypes.size()];
			
			if(receiver != null) {
				myChildren[0] = resolveAsType(receiver,imports,states,roots,nominal);
			}			
			myChildren[start++] = resolveAsType(ut.ret,imports,states,roots,nominal);
			// FIXME: following is a hack since UnresolvedType.Fun doesn't include a throws clause
			myChildren[start++] = resolveAsType(new UnresolvedType.Void(),imports,states,roots,nominal);						
			for(UnresolvedType pt : utParamTypes) {
				myChildren[start++] = resolveAsType(pt,imports,states,roots,nominal);				
			}						
		}
		
		states.set(myIndex,new Automaton.State(myKind,myData,myDeterministic,myChildren));
		
		return myIndex;
	}
	
	private int resolveAsType(NameID key, List<WhileyFile.Import> imports,
			ArrayList<Automaton.State> states, HashMap<NameID, Integer> roots)
			throws ResolveError {
		
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
		
		WhileyFile.TypeDef td = wf.typeDecl(key.name());
		if(td == null) {
			// FIXME: need a better error message!
			throw new ResolveError("type not present in module: " + key.name());
		}
		
		// following is needed to terminate any recursion
		roots.put(key, states.size());
		UnresolvedType type = td.unresolvedType;
		
		// now, expand the given type fully			
		if(type instanceof Type.Leaf) {
			// to avoid unnecessarily creating an array of size one
			int myIndex = states.size();
			int kind = Type.leafKind((Type.Leaf)type);			
			Object data = Type.leafData((Type.Leaf)type);
			states.add(new Automaton.State(kind,data,true,Automaton.NOCHILDREN));
			return myIndex;
		} else {
			return resolveAsType(type,imports,states,roots,false);
		}
		
		// TODO: performance can be improved here, but actually assigning the
		// constructed type into a cache of previously expanded types cache.
		// This is challenging, in the case that the type may not be complete at
		// this point. In particular, if it contains any back-links above this
		// index there could be an issue.
	}	
	
	private int resolveAsType(UnresolvedType.Primitive t,
			List<WhileyFile.Import> imports, ArrayList<Automaton.State> states)
			throws ResolveError {
		int myIndex = states.size();
		int kind;
		if (t instanceof UnresolvedType.Any) {
			kind = Type.K_ANY;
		} else if (t instanceof UnresolvedType.Void) {
			kind = Type.K_VOID;
		} else if (t instanceof UnresolvedType.Null) {
			kind = Type.K_NULL;
		} else if (t instanceof UnresolvedType.Bool) {
			kind = Type.K_BOOL;
		} else if (t instanceof UnresolvedType.Byte) {
			kind = Type.K_BYTE;
		} else if (t instanceof UnresolvedType.Char) {
			kind = Type.K_CHAR;
		} else if (t instanceof UnresolvedType.Int) {
			kind = Type.K_INT;
		} else if (t instanceof UnresolvedType.Real) {
			kind = Type.K_RATIONAL;
		} else if (t instanceof UnresolvedType.Strung) {
			kind = Type.K_STRING;
		} else {		
			throw new ResolveError("unrecognised type encountered ("
					+ t.getClass().getName() + ")");
		}
		states.add(new Automaton.State(kind, null, true,
				Automaton.NOCHILDREN));
		return myIndex;
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
	
	// =========================================================================
	// ResolveAsConstant
	// =========================================================================		
	
	public Value resolveAsConstant(NameID nid) throws ResolveError {				
		return resolveAsConstant(nid,null);		
	}
		
	/**
	 * The expand constant method is responsible for turning a named constant
	 * expression into a value. This is done by traversing the constant's
	 * expression and recursively expanding any named constants it contains.
	 * Simplification of constants is also performed where possible.
	 * 
	 * @param key
	 *            --- name of constant we are expanding.
	 * @param exprs
	 *            --- mapping of all names to their( declared) expressions
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 * @return
	 * @throws ResolveError
	 */
	private Value resolveAsConstant(NameID key, HashSet<NameID> visited) throws ResolveError {		
		WhileyFile wf = files.get(key.module());
		if (wf != null) {			
			WhileyFile.Declaration decl = wf.declaration(key.name());
			if(decl instanceof WhileyFile.Constant) {
				WhileyFile.Constant cd = (WhileyFile.Constant) decl; 				
				if (cd.value == null) {
					cd.value = resolveAsConstant(cd.constant, wf.filename,
							visited);
				}
				return cd.value;
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		} else {		
			Module module = loader.loadModule(key.module());
			Module.ConstDef cd = module.constant(key.name());
			if(cd != null) {
				return cd.constant();
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		}		
	}
	
	/**
	 * The following is a helper method for expandConstant. It takes a given
	 * expression (rather than the name of a constant) and expands to a value
	 * (where possible). If the expression contains, for example, method or
	 * function declarations then this will certainly fail (producing a syntax
	 * error).
	 * 
	 * @param key
	 *            --- name of constant we are expanding.
	 * @param exprs
	 *            --- mapping of all names to their( declared) expressions
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 */
	private Value resolveAsConstant(Expr expr, String filename,HashSet<NameID> visited)
			throws ResolveError {
		if (expr instanceof Expr.Constant) {
			Expr.Constant c = (Expr.Constant) expr;
			return c.value;
		} else if (expr instanceof Expr.BinOp) {
			Expr.BinOp bop = (Expr.BinOp) expr;
			Value lhs = resolveAsConstant(bop.lhs, filename, visited);
			Value rhs = resolveAsConstant(bop.rhs, filename, visited);
			return evaluate(bop, lhs, rhs, filename);			
		} else if (expr instanceof Expr.Set) {
			Expr.Set nop = (Expr.Set) expr;
			ArrayList<Value> values = new ArrayList<Value>();
			for (Expr arg : nop.arguments) {
				values.add(resolveAsConstant(arg, filename, visited));
			}			
			return Value.V_SET(values);			
		} else if (expr instanceof Expr.List) {
			Expr.List nop = (Expr.List) expr;
			ArrayList<Value> values = new ArrayList<Value>();
			for (Expr arg : nop.arguments) {
				values.add(resolveAsConstant(arg, filename, visited));
			}			
			return Value.V_LIST(values);			
		} else if (expr instanceof Expr.Record) {
			Expr.Record rg = (Expr.Record) expr;
			HashMap<String,Value> values = new HashMap<String,Value>();
			for(Map.Entry<String,Expr> e : rg.fields.entrySet()) {
				Value v = resolveAsConstant(e.getValue(),filename,visited);
				if(v == null) {
					return null;
				}
				values.put(e.getKey(), v);
			}
			return Value.V_RECORD(values);
		} else if (expr instanceof Expr.Tuple) {
			Expr.Tuple rg = (Expr.Tuple) expr;			
			ArrayList<Value> values = new ArrayList<Value>();			
			for(Expr e : rg.fields) {
				Value v = resolveAsConstant(e,filename,visited);
				if(v == null) {
					return null;
				}
				values.add(v);				
			}
			return Value.V_TUPLE(values);
		}  else if (expr instanceof Expr.Dictionary) {
			Expr.Dictionary rg = (Expr.Dictionary) expr;			
			HashSet<Pair<Value,Value>> values = new HashSet<Pair<Value,Value>>();			
			for(Pair<Expr,Expr> e : rg.pairs) {
				Value key = resolveAsConstant(e.first(),filename,visited);
				Value value = resolveAsConstant(e.second(),filename,visited);
				if(key == null || value == null) {
					return null;
				}
				values.add(new Pair<Value,Value>(key,value));				
			}
			return Value.V_DICTIONARY(values);
		} else if(expr instanceof Expr.Function) {
			Expr.Function f = (Expr.Function) expr;
			Attributes.Module mid = expr.attribute(Attributes.Module.class);
			if (mid != null) {
				NameID name = new NameID(mid.module, f.name);
				Type.Function tf = null;							
				return Value.V_FUN(name, tf);	
			}					
		}
		
		syntaxError(errorMessage(INVALID_CONSTANT_EXPRESSION), filename, expr);
		return null;
	}

	private Value evaluate(Expr.BinOp bop, Value v1, Value v2, String filename) {
		Type lub = Type.Union(v1.type(), v2.type());
		
		// FIXME: there are bugs here related to coercions.
		
		if(Type.isSubtype(Type.T_BOOL, lub)) {
			return evaluateBoolean(bop,(Value.Bool) v1,(Value.Bool) v2, filename);
		} else if(Type.isSubtype(Type.T_REAL, lub)) {
			return evaluate(bop,(Value.Rational) v1, (Value.Rational) v2, filename);
		} else if(Type.isSubtype(Type.List(Type.T_ANY, false), lub)) {
			return evaluate(bop,(Value.List)v1,(Value.List)v2, filename);
		} else if(Type.isSubtype(Type.Set(Type.T_ANY, false), lub)) {
			return evaluate(bop,(Value.Set) v1, (Value.Set) v2, filename);
		} 
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION),filename,bop);
		return null;
	}
	
	private Value evaluateBoolean(Expr.BinOp bop, Value.Bool v1, Value.Bool v2, String filename) {				
		switch(bop.op) {
		case AND:
			return Value.V_BOOL(v1.value & v2.value);
		case OR:		
			return Value.V_BOOL(v1.value | v2.value);
		case XOR:
			return Value.V_BOOL(v1.value ^ v2.value);
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),filename,bop);
		return null;
	}
	
	private Value evaluate(Expr.BinOp bop, Value.Rational v1, Value.Rational v2, String filename) {		
		switch(bop.op) {
		case ADD:
			return Value.V_RATIONAL(v1.value.add(v2.value));
		case SUB:
			return Value.V_RATIONAL(v1.value.subtract(v2.value));
		case MUL:
			return Value.V_RATIONAL(v1.value.multiply(v2.value));
		case DIV:
			return Value.V_RATIONAL(v1.value.divide(v2.value));
		case REM:
			return Value.V_RATIONAL(v1.value.intRemainder(v2.value));	
		}
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION),filename,bop);
		return null;
	}
	
	private Value evaluate(Expr.BinOp bop, Value.List v1, Value.List v2, String filename) {
		switch(bop.op) {
		case ADD:
			ArrayList<Value> vals = new ArrayList<Value>(v1.values);
			vals.addAll(v2.values);
			return Value.V_LIST(vals);
		}
		syntaxError(errorMessage(INVALID_LIST_EXPRESSION),filename,bop);
		return null;
	}
	
	private Value evaluate(Expr.BinOp bop, Value.Set v1, Value.Set v2, String filename) {		
		switch(bop.op) {
		case UNION:
		{
			HashSet<Value> vals = new HashSet<Value>(v1.values);			
			vals.addAll(v2.values);
			return Value.V_SET(vals);
		}
		case INTERSECTION:
		{
			HashSet<Value> vals = new HashSet<Value>();			
			for(Value v : v1.values) {
				if(v2.values.contains(v)) {
					vals.add(v);
				}
			}			
			return Value.V_SET(vals);
		}
		case SUB:
		{
			HashSet<Value> vals = new HashSet<Value>();			
			for(Value v : v1.values) {
				if(!v2.values.contains(v)) {
					vals.add(v);
				}
			}			
			return Value.V_SET(vals);
		}
		}
		syntaxError(errorMessage(INVALID_SET_EXPRESSION),filename,bop);
		return null;
	}	

	// =========================================================================
	// BindFunction and BindMessage
	// =========================================================================				
	
	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. To do this, it must find the function/method with the most
	 * precise type that matches the argument types.
	 * 
	 * @param nid
	 * @param qualification
	 * @param parameters
	 * @param elem
	 * @return
	 * @throws ResolveError
	 */
	public Pair<NameID,Nominal<Type.Function>> resolveAsFunctionOrMethod(String name, 
			List<Nominal<Type>> parameters, List<WhileyFile.Import> imports) throws ResolveError {

		List<Type> rawParameters = Nominal.stripNominal(parameters);
		
		// first, try to find the matching message
		for (WhileyFile.Import imp : imports) {
			if (imp.matchName(name)) {
				for (ModuleID mid : matchImport(imp)) {
					NameID nid = new NameID(mid,name);										
					Nominal<Type.Function> ft = lookupFunctionOrMethod(nid,rawParameters);
					if(ft != null) {
						return new Pair<NameID,Nominal<Type.Function>>(nid,ft);
					}
				}
			}
		}

		String msg = "no match for " + name + parameterString(parameters);
		// second, didn't find matching message so generate error message
		for (WhileyFile.Import imp : imports) {
			if (imp.matchName(name)) {
				for (ModuleID mid : matchImport(imp)) {
					NameID nid = new NameID(mid,name);	
					for(Nominal<Type.Function> ft : listFunctionsAndMethods(nid,parameters.size())) {					
						// FIXME: produce nominal information here
						msg += "\n\tfound: " + nid + " : " + ft.nominal();
					}					
				}
			}
		}
		throw new ResolveError(msg);
	}
	
	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. To do this, it must find the function/method with the most
	 * precise type that matches the argument types.
	 * 
	 * @param nid
	 * @param qualification
	 * @param parameters
	 * @param elem
	 * @return
	 * @throws ResolveError
	 */
	public Nominal<Type.Function> resolveAsFunctionOrMethod(NameID nid, 
			List<Nominal<Type>> parameters) throws ResolveError {

		List<Type> rawParameters = Nominal.stripNominal(parameters);		
		Nominal<Type.Function> candidate = lookupFunctionOrMethod(nid,rawParameters); 		
							
		// Check whether we actually found something. If not, print a useful
		// error message.
		if(candidate == null) {			
			String msg = "no match for " + nid + parameterString(parameters);
			boolean firstTime = true;			
			for (Nominal<Type.Function> nft : listFunctionsAndMethods(nid,parameters.size())) {				
				Type.Function ft = (Type.Function) nft.nominal();				
				if(firstTime) {
					msg += "\n\tfound: " + nid.name() +  rawParameterString(ft.params());
				} else {
					msg += "\n\tand: " + nid.name() +  rawParameterString(ft.params());
				}								
			}
			
			// need to think about this one
			throw new ResolveError(msg);
		}
		
		return candidate;
	}
	
	public Pair<NameID,Nominal<Type.Method>> resolveAsMessage(String name, Type.Process receiver,
			List<Nominal<Type>> parameters, List<WhileyFile.Import> imports) throws ResolveError {

		// first, try to find the matching message
		List<Type> rawParameters = Nominal.stripNominal(parameters);	
		for (WhileyFile.Import imp : imports) {
			if (imp.matchName(name)) {
				for (ModuleID mid : matchImport(imp)) {
					NameID nid = new NameID(mid,name);										
					Nominal<Type.Method> mt = lookupMessage(nid,receiver,rawParameters);
					if(mt != null) {
						return new Pair<NameID,Nominal<Type.Method>>(nid,mt);
					}
				}
			}
		}
		
		String rec = receiver.toString() + "::";
		
		String msg = "no match for " + name + parameterString(parameters);
		// second, didn't find matching message so generate error message
		for (WhileyFile.Import imp : imports) {
			if (imp.matchName(name)) {
				for (ModuleID mid : matchImport(imp)) {
					NameID nid = new NameID(mid,name);	
					for(Nominal<Type.Method> ft : listMessages(nid,parameters.size())) {						
						// FIXME: produce nominal information here
						msg += "\n\tfound: " + nid + " : " + ft;
					}					
				}
			}
		}
		throw new ResolveError(msg);
	}
	
	public Nominal<Type.Method> resolveAsMessage(NameID nid, Type.Process receiver,
			List<Nominal<Type>> parameters) throws ResolveError {
		
		List<Type> rawParameters = Nominal.stripNominal(parameters);	
		Nominal<Type.Method> candidate = lookupMessage(nid,receiver,rawParameters); 
						
		// Check whether we actually found something. If not, print a useful
		// error message.
		if(candidate == null) {
			String rec = receiver.toString() + "::";			
			String msg = "no match for " + nid + parameterString(parameters);
			boolean firstTime = true;			
			for (Nominal<Type.Method> nmt : listMessages(nid,parameters.size())) {
				Type.Method mt = nmt.raw();
				if(firstTime) {
					msg += "\n\tfound: " + mt.receiver() + nid.name() +  rawParameterString(mt.params());
				} else {
					msg += "\n\tand: " + mt.receiver() + nid.name() +  rawParameterString(mt.params());
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
	
	private String parameterString(List<Nominal<Type>> paramTypes) {
		String paramStr = "(";
		boolean firstTime = true;
		for(Nominal<Type> t : paramTypes) {
			if(!firstTime) {
				paramStr += ",";
			}
			firstTime=false;
			paramStr += t.nominal();
		}
		return paramStr + ")";		
	}
	
	private String rawParameterString(List<Type> paramTypes) {
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
	
	
	private Nominal<Type.Function> lookupFunctionOrMethod(NameID nid, 
			List<Type> parameters) throws ResolveError {
		
		Type.Function target = (Type.Function) Type.Function(Type.T_ANY,
				Type.T_ANY, parameters);
		Nominal<Type.Function> candidate = null;			
				
		for (Nominal<Type.Function> nft : listFunctionsAndMethods(nid, parameters.size())) {
			Type.Function ft = nft.raw();
			if (ft instanceof Type.Method) {
				Type.Method mt = (Type.Method) ft;
				if (mt.receiver() != null) {
					continue; // cannot resolve as function or method
				}
			}
			if (ft.params().size() == parameters.size()
					&& paramSubtypes(ft, target)
					&& (candidate == null || paramSubtypes(candidate.raw(), ft))) {
				candidate = nft;
			}
		}				
		
		return candidate;
	}
	
	private Nominal<Type.Method> lookupMessage(NameID nid, Type.Process receiver,
			List<Type> parameters) throws ResolveError {
		
		Type.Method target = (Type.Method) Type.Method(receiver, Type.T_ANY,
				Type.T_ANY, parameters);
		Nominal<Type.Method> candidate = null;						
		
		for (Nominal<Type.Method> nft : listMessages(nid,parameters.size())) {
			Type.Function ft = nft.raw();
			if(ft instanceof Type.Method) { 				
				Type.Method mt = (Type.Method) ft; 
				Type funrec = mt.receiver();	
				if (receiver == funrec
						|| (receiver != null && funrec != null && Type
						.isImplicitCoerciveSubtype(receiver, funrec))) {					
					// receivers match up OK ...				
					if (mt.params().size() == parameters.size()						
							&& paramSubtypes(mt, target)
							&& (candidate == null || paramSubtypes(candidate.raw(),mt))) {	
						// The following cast is safe since we've already
						// checked the type of ft.
						candidate = (Nominal) nft;
					}
				}
			}
		}	
		
		return candidate;
	}
	
	private ArrayList<Nominal<Type.Function>> listFunctionsAndMethods(
			NameID nid, int nparams) throws ResolveError {
		ModuleID mid = nid.module();
		ArrayList<Nominal<Type.Function>> r = new ArrayList<Nominal<Type.Function>>();		
		
		WhileyFile wf = files.get(mid);
		if(wf != null) {
			// FIXME: need to include methods here as well
			for (WhileyFile.Function f : wf.declarations(
					WhileyFile.Function.class, nid.name())) {
				if (f.parameters.size() == nparams) {
					r.add((Nominal) resolveAsType(f.unresolvedType(),
							wf.declarations(WhileyFile.Import.class, f)));
				}
			}
		} else {
			try {
				Module m = loader.loadModule(mid);
				for(Module.Method mm : m.methods()) {
					if ((mm.isFunction() || mm.isMethod())
							&& mm.name().equals(nid.name())
							&& mm.type().params().size() == nparams) {
						// FIXME: loss of nominal information
						r.add(new Nominal<Type.Function>(mm.type(), mm.type()));
					}
				}
			} catch(ResolveError e) {
				
			}
		}
		return r;
	}
	
	private ArrayList<Nominal<Type.Method>> listMessages(NameID nid, int nparams)
			throws ResolveError {		
		ModuleID mid = nid.module();
		ArrayList<Nominal<Type.Method>> r = new ArrayList<Nominal<Type.Method>>();		
		
		WhileyFile wf = files.get(mid);
		if(wf != null) {
			// FIXME: need to exclude methods here
			for (WhileyFile.Message m : wf.declarations(
					WhileyFile.Message.class, nid.name())) {
				if (m.parameters.size() == nparams) {
					r.add((Nominal) resolveAsType(m.unresolvedType(),
							wf.declarations(WhileyFile.Import.class, m)));
				}
			}
		} else {
			try {
				Module m = loader.loadModule(mid);
				for(Module.Method mm : m.methods()) {
					if (mm.isMessage() && mm.name().equals(nid.name())
							&& mm.type().params().size() == nparams) {
						// FIXME: loss of nominal information
						r.add(new Nominal(mm.type(), mm.type()));
					}
				}
			} catch(ResolveError e) {
				
			}
		}
		return r;		
	}
	
}
