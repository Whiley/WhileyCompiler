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

package wyc.core;

import static wyil.util.ErrorMessages.*;
import static wyc.util.Context.syntaxError;
import static wyc.util.Context.internalFailure;

import java.util.*;

import wyautl.lang.*;
import wyc.lang.Expr;
import wyc.lang.UnresolvedType;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Declaration;
import wyc.util.*;
import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.util.*;

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
public final class Resolver {
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
	
	
	/**
	 * The constant cache contains a cache of expanded constant values.
	 */
	HashMap<NameID, Value> constantCache = new HashMap();
	
	public Resolver(ModuleLoader loader) {
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
	 * This function checks whether the supplied module exists or not.
	 * 
	 * @param pkg
	 *            The package whose existence we want to check for.
	 * 
	 * @return true if the package exists, false otherwise.
	 */
	public boolean isModule(ModuleID mid) {
		try {
			loader.loadModule(mid);
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
	 * This methods attempts to resolve the correct package for a named item in
	 * a given context. Resolving the correct package may require loading
	 * modules as necessary from the WHILEYPATH and/or compiling modules for
	 * which only source code is currently available.
	 * 
	 * @param name
	 *            A module name without package specifier.
	 * @param context
	 *            --- context in which to resolve.
	 * @return The resolved name.
	 * @throws ResolveError
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(String name, Context context)
			throws ResolveError {		
		for (WhileyFile.Import imp : context.imports) {			
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
	 * @param context
	 *            --- context in which to resolve *
	 * @return The resolved name.
	 * @throws ResolveError
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(List<String> names, Context context) throws ResolveError {		
		if(names.size() == 1) {
			return resolveAsName(names.get(0),context);
		} else if(names.size() == 2) {
			String name = names.get(1);
			ModuleID mid = resolveAsModule(names.get(0),context);		
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
	 * This method attempts to resolve a name as a module in a given name
	 * context.
	 * 
	 * @param name
	 *            --- name to be resolved
	 * @param context
	 *            --- context in which to resolve
	 * @return
	 * @throws ResolveError
	 */
	public ModuleID resolveAsModule(String name, Context context)
			throws ResolveError {
		
		for (WhileyFile.Import imp : context.imports) {			
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
	
	public Nominal.Function resolveAsType(UnresolvedType.Function t,
			Context context) throws ResolveError {
		return (Nominal.Function) resolveAsType((UnresolvedType)t,context);
	}
	
	public Nominal.Method resolveAsType(UnresolvedType.Method t,
			Context context) throws ResolveError {		
		return (Nominal.Method) resolveAsType((UnresolvedType)t,context);
	}
	
	public Nominal.Message resolveAsType(UnresolvedType.Message t,
			Context context) throws ResolveError {		
		return (Nominal.Message) resolveAsType((UnresolvedType)t,context);
	}

	/**
	 * Resolve a type in a given context by identifying all unknown names and
	 * replacing them with nominal types.
	 * 
	 * @param type
	 *            --- type to be resolved.
	 * @param context
	 *            --- context in which to resolve the type.
	 * @return
	 * @throws ResolveError
	 */
	public Nominal resolveAsType(UnresolvedType type, Context context)
			throws ResolveError {
		Type nominalType = resolveAsType(type, context, true);
		Type rawType = resolveAsType(type, context, false);
		return Nominal.construct(nominalType, rawType);
	}
	
	private Type resolveAsType(UnresolvedType t, Context context,
			boolean nominal) {
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
				internalFailure("unrecognised type encountered ("
						+ t.getClass().getName() + ")",context,t);
				return null; // deadcode
			}
		} else {
			ArrayList<Automaton.State> states = new ArrayList<Automaton.State>();
			HashMap<NameID,Integer> roots = new HashMap<NameID,Integer>();
			resolveAsType(t,context,states,roots,nominal);
			return Type.construct(new Automaton(states));
		}
	}
	
	/**
	 * The following method resolves a type in a given context.
	 * 
	 * @param type
	 *            --- type to be resolved
	 * @param context
	 *            --- context in which to resolve the type
	 * @return
	 * @throws ResolveError
	 */
	private int resolveAsType(UnresolvedType type, Context context,
			ArrayList<Automaton.State> states, HashMap<NameID, Integer> roots, boolean nominal) {				
		
		if(type instanceof UnresolvedType.Primitive) {
			return resolveAsType((UnresolvedType.Primitive)type,context,states);
		} 
		
		int myIndex = states.size();
		int myKind;
		int[] myChildren;
		Object myData = null;
		boolean myDeterministic = true;
		
		states.add(null); // reserve space for me
		
		if(type instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) type;
			myKind = Type.K_LIST;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(lt.element,context,states,roots,nominal);
			myData = false;
		} else if(type instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) type;
			myKind = Type.K_SET;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(st.element,context,states,roots,nominal);
			myData = false;
		} else if(type instanceof UnresolvedType.Dictionary) {
			UnresolvedType.Dictionary st = (UnresolvedType.Dictionary) type;
			myKind = Type.K_DICTIONARY;
			myChildren = new int[2];
			myChildren[0] = resolveAsType(st.key,context,states,roots,nominal);
			myChildren[1] = resolveAsType(st.value,context,states,roots,nominal);			
		} else if(type instanceof UnresolvedType.Record) {
			UnresolvedType.Record tt = (UnresolvedType.Record) type;
			HashMap<String,UnresolvedType> ttTypes = tt.types;			
			Type.Record.State fields = new Type.Record.State(tt.isOpen,ttTypes.keySet());
			Collections.sort(fields);			
			myKind = Type.K_RECORD;
			myChildren = new int[fields.size()];
			for(int i=0;i!=fields.size();++i) {	
				String field = fields.get(i);
				myChildren[i] = resolveAsType(ttTypes.get(field),context,states,roots,nominal);
			}						
			myData = fields;
		} else if(type instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) type;
			ArrayList<UnresolvedType> ttTypes = tt.types;
			myKind = Type.K_TUPLE;
			myChildren = new int[ttTypes.size()];
			for(int i=0;i!=ttTypes.size();++i) {
				myChildren[i] = resolveAsType(ttTypes.get(i),context,states,roots,nominal);				
			}			
		} else if(type instanceof UnresolvedType.Nominal) {
			// This case corresponds to a user-defined type. This will be
			// defined in some module (possibly ours), and we need to identify
			// what module that is here, and save it for future use.
			UnresolvedType.Nominal dt = (UnresolvedType.Nominal) type;									
			NameID nid;
			try {
				nid = resolveAsName(dt.names, context);

				if(nominal) {
					myKind = Type.K_NOMINAL;
					myData = nid;
					myChildren = Automaton.NOCHILDREN;
				} else {
					// At this point, we're going to expand the given nominal type.
					// We're going to use resolveAsType(NameID,...) to do this which
					// will load the expanded type onto states at the current point.
					// Therefore, we need to remove the initial null we loaded on.
					states.remove(myIndex); 
					return resolveAsType(nid,states,roots);				
				}	
			} catch(ResolveError e) {
				syntaxError(e.getMessage(),context,dt,e);
				return 0; // dead-code
			} catch(SyntaxError e) {
				throw e;
			} catch(Throwable e) {
				internalFailure(e.getMessage(),context,dt,e);
				return 0; // dead-code
			}
		} else if(type instanceof UnresolvedType.Not) {	
			UnresolvedType.Not ut = (UnresolvedType.Not) type;
			myKind = Type.K_NEGATION;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element,context,states,roots,nominal);			
		} else if(type instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) type;
			ArrayList<UnresolvedType.NonUnion> utTypes = ut.bounds;
			myKind = Type.K_UNION;
			myChildren = new int[utTypes.size()];
			for(int i=0;i!=utTypes.size();++i) {
				myChildren[i] = resolveAsType(utTypes.get(i),context,states,roots,nominal);				
			}	
			myDeterministic = false;
		} else if(type instanceof UnresolvedType.Intersection) {
			internalFailure("intersection types not supported yet",context,type);
			return 0; // dead-code
		} else if(type instanceof UnresolvedType.Reference) {	
			UnresolvedType.Reference ut = (UnresolvedType.Reference) type;
			myKind = Type.K_REFERENCE;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element,context,states,roots,nominal);		
		} else {			
			UnresolvedType.FunctionOrMethodOrMessage ut = (UnresolvedType.FunctionOrMethodOrMessage) type;			
			ArrayList<UnresolvedType> utParamTypes = ut.paramTypes;
			UnresolvedType receiver = null;
			int start = 0;
			
			if(ut instanceof UnresolvedType.Message) {
				UnresolvedType.Message mt = (UnresolvedType.Message) ut;
				receiver = mt.receiver;				
				myKind = Type.K_MESSAGE;
				start++;				
			} else if(ut instanceof UnresolvedType.Method) {
				myKind = Type.K_METHOD;
			} else {
				myKind = Type.K_FUNCTION;
			}
			
			myChildren = new int[start + 2 + utParamTypes.size()];
			
			if(receiver != null) {
				myChildren[0] = resolveAsType(receiver,context,states,roots,nominal);
			}			
			myChildren[start++] = resolveAsType(ut.ret,context,states,roots,nominal);
			if(ut.throwType == null) {
				// this case indicates the user did not provide a throws clause.
				myChildren[start++] = resolveAsType(new UnresolvedType.Void(),context,states,roots,nominal);
			} else {
				myChildren[start++] = resolveAsType(ut.throwType,context,states,roots,nominal);
			}
			for(UnresolvedType pt : utParamTypes) {
				myChildren[start++] = resolveAsType(pt,context,states,roots,nominal);				
			}						
		}
		
		states.set(myIndex,new Automaton.State(myKind,myData,myDeterministic,myChildren));
		
		return myIndex;
	}
	
	private int resolveAsType(NameID key, ArrayList<Automaton.State> states, HashMap<NameID, Integer> roots)
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
			Value v = resolveAsConstant(key);
			Type t = v.type();
			if(t instanceof Type.Set) {
				Type.Set ts = (Type.Set) t;
				// FIXME: add constraints
				return append(ts.element(),states);
			} else {
				// FIXME: need a better error message!
				throw new ResolveError("type not found: " + key.name());
			}
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
			return resolveAsType(type,context(wf,td),states,roots,false);			
		}
		
		// TODO: performance can be improved here, but actually assigning the
		// constructed type into a cache of previously expanded types cache.
		// This is challenging, in the case that the type may not be complete at
		// this point. In particular, if it contains any back-links above this
		// index there could be an issue.
	}	
	
	private int resolveAsType(UnresolvedType.Primitive t,
			Context context, ArrayList<Automaton.State> states) {
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
			internalFailure("unrecognised type encountered ("
					+ t.getClass().getName() + ")",context,t);
			return 0; // dead-code
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
	// expandAsType
	// =========================================================================	

	public Nominal.EffectiveSet expandAsEffectiveSet(Nominal lhs) throws ResolveError {
		Type raw = lhs.raw();
		if(raw instanceof Type.EffectiveSet) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.EffectiveSet)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveSet) Nominal.construct(nominal,raw);
		} else {
			return null;
		}
	}

	public Nominal.EffectiveList expandAsEffectiveList(Nominal lhs) throws ResolveError {
		Type raw = lhs.raw();
		if(raw instanceof Type.EffectiveList) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.EffectiveList)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveList) Nominal.construct(nominal,raw);
		} else {
			return null;
		}
	}

	public Nominal.EffectiveDictionary expandAsEffectiveDictionary(Nominal lhs) throws ResolveError {
		Type raw = lhs.raw();
		if(raw instanceof Type.EffectiveDictionary) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.EffectiveDictionary)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveDictionary) Nominal.construct(nominal,raw);
		} else {
			return null;
		}
	}

	public Nominal.EffectiveRecord expandAsEffectiveRecord(Nominal lhs) throws ResolveError {		
		Type raw = lhs.raw();
				
		if(raw instanceof Type.Record) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.Record)) {
				nominal = (Type) raw; // discard nominal information
			}			
			return (Nominal.Record) Nominal.construct(nominal,raw);
		} else if(raw instanceof Type.UnionOfRecords) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.UnionOfRecords)) {
				nominal = (Type) raw; // discard nominal information
			}			
			return (Nominal.UnionOfRecords) Nominal.construct(nominal,raw);
		} {
			return null;
		}
	}
	
	public Nominal.EffectiveTuple expandAsEffectiveTuple(Nominal lhs) throws ResolveError {
		Type raw = lhs.raw();
		if(raw instanceof Type.EffectiveTuple) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.EffectiveTuple)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveTuple) Nominal.construct(nominal,raw);
		} else {
			return null;
		}
	}
	
	public Nominal.Reference expandAsReference(Nominal lhs) throws ResolveError {
		Type.Reference raw = Type.effectiveReference(lhs.raw());
		if(raw != null) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.Reference)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.Reference) Nominal.construct(nominal,raw);
		} else {
			return null;
		}
	}

	public Nominal.FunctionOrMethod expandAsFunctionOrMethod(Nominal lhs) throws ResolveError {
		Type.FunctionOrMethod raw = Type.effectiveFunctionOrMethod(lhs.raw());
		if(raw != null) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.FunctionOrMethod)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.FunctionOrMethod) Nominal.construct(nominal,raw);
		} else {
			return null;
		}
	}
	
	public Nominal.Message expandAsMessage(Nominal lhs) throws ResolveError {
		Type.Message raw = Type.effectiveMessage(lhs.raw());
		if(raw != null) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.Message)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.Message) Nominal.construct(nominal,raw);
		} else {
			return null;
		}
	}
	
	private Type expandOneLevel(Type type) throws ResolveError {
		if(type instanceof Type.Nominal){
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.name();			
			ModuleID mid = nid.module();
			
			WhileyFile wf = files.get(mid);
			Type r = null;
			
			if (wf != null) {			
				WhileyFile.Declaration decl = wf.declaration(nid.name());
				if(decl instanceof WhileyFile.TypeDef) {
					WhileyFile.TypeDef td = (WhileyFile.TypeDef) decl;
					r = resolveAsType(td.unresolvedType,
							context(wf, decl), true);
				} 
			} else {
				Module m = loader.loadModule(mid);
				Module.TypeDef td = m.type(nid.name());
				if(td != null) {
					r = td.type();
				}
			}
			if(r == null) {
				throw new ResolveError("unable to locate " + nid);
			}
			return expandOneLevel(r);
		} else if(type instanceof Type.Leaf 
				|| type instanceof Type.Reference
				|| type instanceof Type.Tuple
				|| type instanceof Type.Set
				|| type instanceof Type.List
				|| type instanceof Type.Dictionary
				|| type instanceof Type.Record
				|| type instanceof Type.FunctionOrMethodOrMessage
				|| type instanceof Type.Negation) {
			return type;
		} else {
			Type.Union ut = (Type.Union) type;
			ArrayList<Type> bounds = new ArrayList<Type>();
			for(Type b : ut.bounds()) {
				bounds.add(expandOneLevel(b));
			}
			return Type.Union(bounds);
		} 
	}
	
	// =========================================================================
	// ResolveAsConstant
	// =========================================================================		
	
	public Value resolveAsConstant(NameID nid) throws ResolveError {				
		return resolveAsConstant(nid,new HashSet<NameID>());		
	}
	
	public Value resolveAsConstant(Expr e, Context context) throws ResolveError {
		ExpressionTyper typer = new ExpressionTyper(this,context);
		e = typer.propagate(e, new RefCountedHashMap());
		return resolveAsConstant(e,context,new HashSet<NameID>());		
	}
	
	/**
	 * Responsible for turning a named constant expression into a value. This is
	 * done by traversing the constant's expression and recursively expanding
	 * any named constants it contains. Simplification of constants is also
	 * performed where possible.
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
		Value result = constantCache.get(key);
		if(result != null) {
			return result;
		} else if(visited.contains(key)) {
			throw new ResolveError("cyclic constant definition encountered (" + key + " -> " + key + ")");
		} else {
			visited.add(key);
		}
		
		WhileyFile wf = files.get(key.module());
		
		if (wf != null) {			
			WhileyFile.Declaration decl = wf.declaration(key.name());
			if(decl instanceof WhileyFile.Constant) {
				WhileyFile.Constant cd = (WhileyFile.Constant) decl; 				
				if (cd.resolvedValue == null) {			
					Context context = context(wf, cd);
					cd.constant = new ExpressionTyper(this,context).propagate(cd.constant, new RefCountedHashMap());
					cd.resolvedValue = resolveAsConstant(cd.constant,
							context, visited);
				}
				result = cd.resolvedValue;
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		} else {		
			Module module = loader.loadModule(key.module());
			Module.ConstDef cd = module.constant(key.name());
			if(cd != null) {
				result = cd.constant();
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		}		
		
		constantCache.put(key, result);
		
		return result;
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
	 * @param context
	 *            --- context in which to resolve this constant.
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 */
	private Value resolveAsConstant(Expr expr, Context context,
			HashSet<NameID> visited) throws ResolveError {
		try {
			if (expr instanceof Expr.Constant) {
				Expr.Constant c = (Expr.Constant) expr;
				return c.value;
			} else if (expr instanceof Expr.ConstantAccess) {
				Expr.ConstantAccess c = (Expr.ConstantAccess) expr;
				return resolveAsConstant(c.nid,visited);				
			} else if (expr instanceof Expr.BinOp) {
				Expr.BinOp bop = (Expr.BinOp) expr;
				Value lhs = resolveAsConstant(bop.lhs, context, visited);
				Value rhs = resolveAsConstant(bop.rhs, context, visited);
				return evaluate(bop,lhs,rhs,context);			
			} else if (expr instanceof Expr.Set) {
				Expr.Set nop = (Expr.Set) expr;
				ArrayList<Value> values = new ArrayList<Value>();
				for (Expr arg : nop.arguments) {
					values.add(resolveAsConstant(arg,context, visited));
				}			
				return Value.V_SET(values);			
			} else if (expr instanceof Expr.List) {
				Expr.List nop = (Expr.List) expr;
				ArrayList<Value> values = new ArrayList<Value>();
				for (Expr arg : nop.arguments) {
					values.add(resolveAsConstant(arg,context, visited));
				}			
				return Value.V_LIST(values);			
			} else if (expr instanceof Expr.Record) {
				Expr.Record rg = (Expr.Record) expr;
				HashMap<String,Value> values = new HashMap<String,Value>();
				for(Map.Entry<String,Expr> e : rg.fields.entrySet()) {
					Value v = resolveAsConstant(e.getValue(),context,visited);
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
					Value v = resolveAsConstant(e, context,visited);
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
					Value key = resolveAsConstant(e.first(), context,visited);
					Value value = resolveAsConstant(e.second(), context,visited);
					if(key == null || value == null) {
						return null;
					}
					values.add(new Pair<Value,Value>(key,value));				
				}
				return Value.V_DICTIONARY(values);
			} else if(expr instanceof Expr.AbstractFunctionOrMethodOrMessage) {
				Expr.AbstractFunctionOrMethodOrMessage f = (Expr.AbstractFunctionOrMethodOrMessage) expr;
				// FIXME: consider function parameters as well
				Pair<NameID,Nominal.FunctionOrMethod> p = resolveAsFunctionOrMethod(f.name, context);
				Type.FunctionOrMethod fmt = p.second().raw();				
				return Value.V_FUN(p.first(),p.second().raw());				
			} 
		} catch(SyntaxError.InternalFailure e) {
			throw e;
		} catch(ResolveError e) {
			throw e;
		} catch(Throwable e) {
			internalFailure(e.getMessage(),context,expr,e);
		}
		
		internalFailure("unknown constant expression: " + expr.getClass().getName(),context,expr);
		return null; // deadcode
	}

	private Value evaluate(Expr.BinOp bop, Value v1, Value v2, Context context) {
		Type lub = Type.Union(v1.type(), v2.type());
		
		// FIXME: there are bugs here related to coercions.
		
		if(Type.isSubtype(Type.T_BOOL, lub)) {
			return evaluateBoolean(bop,(Value.Bool) v1,(Value.Bool) v2, context);
		} else if(Type.isSubtype(Type.T_REAL, lub)) {
			return evaluate(bop,(Value.Rational) v1, (Value.Rational) v2, context);
		} else if(Type.isSubtype(Type.List(Type.T_ANY, false), lub)) {
			return evaluate(bop,(Value.List)v1,(Value.List)v2, context);
		} else if(Type.isSubtype(Type.Set(Type.T_ANY, false), lub)) {
			return evaluate(bop,(Value.Set) v1, (Value.Set) v2, context);
		} 
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION),context,bop);
		return null;
	}
	
	private Value evaluateBoolean(Expr.BinOp bop, Value.Bool v1, Value.Bool v2, Context context) {				
		switch(bop.op) {
		case AND:
			return Value.V_BOOL(v1.value & v2.value);
		case OR:		
			return Value.V_BOOL(v1.value | v2.value);
		case XOR:
			return Value.V_BOOL(v1.value ^ v2.value);
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),context,bop);
		return null;
	}
	
	private Value evaluate(Expr.BinOp bop, Value.Rational v1, Value.Rational v2, Context context) {		
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
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION),context,bop);
		return null;
	}
	
	private Value evaluate(Expr.BinOp bop, Value.List v1, Value.List v2, Context context) {
		switch(bop.op) {
		case ADD:
			ArrayList<Value> vals = new ArrayList<Value>(v1.values);
			vals.addAll(v2.values);
			return Value.V_LIST(vals);
		}
		syntaxError(errorMessage(INVALID_LIST_EXPRESSION),context,bop);
		return null;
	}
	
	private Value evaluate(Expr.BinOp bop, Value.Set v1, Value.Set v2, Context context) {		
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
		syntaxError(errorMessage(INVALID_SET_EXPRESSION),context,bop);
		return null;
	}	

	// =========================================================================
	// BindFunction and BindMessage
	// =========================================================================				
	
	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. In this case, no argument types are given. This means that any
	 * match is returned. However, if there are multiple matches, then an
	 * ambiguity error is reported.
	 * 
	 * @param name
	 *            --- function or method name whose type to determine.
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return
	 * @throws ResolveError
	 */
	public Pair<NameID,Nominal.FunctionOrMethod> resolveAsFunctionOrMethod(String name, 
			Context context) throws ResolveError {
		return resolveAsFunctionOrMethod(name,null,context);
	}

	/**
	 * Responsible for determining the true type of a funciont, method or
	 * message being invoked. In this case, no argument types are given. This
	 * means that any match is returned. However, if there are multiple matches,
	 * then an ambiguity error is reported.
	 * 
	 * @param name
	 *            --- function or method name whose type to determine.
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return
	 * @throws ResolveError
	 */
	public Pair<NameID,Nominal.FunctionOrMethodOrMessage> resolveAsFunctionOrMethodOrMessage(String name, 
			Context context) throws ResolveError {
		try {
			return (Pair) resolveAsFunctionOrMethod(name,null,context);
		} catch(ResolveError e) {
			return (Pair) resolveAsMessage(name,null,null,context);
		}
	}
	
	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. To do this, it must find the function/method with the most
	 * precise type that matches the argument types.
	 * 
	 * @param name
	 *            --- name of function or method whose type to determine.
	 * @param parameters
	 *            --- required parameter types for the function or method.
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return
	 * @throws ResolveError
	 */
	public Pair<NameID,Nominal.FunctionOrMethod> resolveAsFunctionOrMethod(String name, 
			List<Nominal> parameters, Context context) throws ResolveError {
		
		HashSet<Pair<NameID,Nominal.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Nominal.FunctionOrMethod>>(); 
		
		// first, try to find the matching message
		for (WhileyFile.Import imp : context.imports) {
			if (imp.matchName(name)) {
				for (ModuleID mid : matchImport(imp)) {					
					NameID nid = new NameID(mid,name);				
					addCandidateFunctionsAndMethods(nid,parameters,candidates);					
				}
			}
		}
		
		return selectCandidateFunctionOrMethod(name,parameters,candidates);
	}		
	
	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. To do this, it must find the function/method with the most
	 * precise type that matches the argument types.
	 * 
	 * @param nid
	 * @param parameters
	 * @return
	 * @throws ResolveError
	 */
	public Nominal.FunctionOrMethod resolveAsFunctionOrMethod(NameID nid, 
			List<Nominal> parameters) throws ResolveError {
		HashSet<Pair<NameID, Nominal.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Nominal.FunctionOrMethod>>();
		
		addCandidateFunctionsAndMethods(nid, parameters, candidates);
		
		return selectCandidateFunctionOrMethod(nid.name(), parameters,
				candidates).second();		
	}
	
	public Pair<NameID,Nominal.Message> resolveAsMessage(String name, Type.Reference receiver,
			List<Nominal> parameters, Context context) throws ResolveError {

		HashSet<Pair<NameID,Nominal.Message>> candidates = new HashSet<Pair<NameID,Nominal.Message>>(); 
		
		// first, try to find the matching message
		for (WhileyFile.Import imp : context.imports) {
			if (imp.matchName(name)) {
				for (ModuleID mid : matchImport(imp)) {					
					NameID nid = new NameID(mid,name);				
					addCandidateMessages(nid,parameters,candidates);					
				}
			}
		}

		return selectCandidateMessage(name,receiver,parameters,candidates);
	}
	
	public Nominal.Message resolveAsMessage(NameID nid,
			Type.Reference receiver, List<Nominal> parameters)
			throws ResolveError {
		HashSet<Pair<NameID, Nominal.Message>> candidates = new HashSet<Pair<NameID, Nominal.Message>>();
		
		addCandidateMessages(nid, parameters, candidates);
		
		return selectCandidateMessage(nid.name(), receiver, parameters, candidates).second();
	}
	
	private boolean paramSubtypes(Type.FunctionOrMethodOrMessage f1, Type.FunctionOrMethodOrMessage f2) {		
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
	
	private boolean paramStrictSubtypes(Type.FunctionOrMethodOrMessage f1, Type.FunctionOrMethodOrMessage f2) {		
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if(f1_params.size() == f2_params.size()) {
			boolean allEqual = true;
			for(int i=0;i!=f1_params.size();++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);				
				if(!Type.isImplicitCoerciveSubtype(f1_param,f2_param)) {				
					return false;
				}
				allEqual &= f1_param.equals(f2_param);
			}			
			
			// This function returns true if the parameters are a strict
			// subtype. Therefore, if they are all equal it must return false.
			
			return !allEqual;
		}
		return false;
	}
	
	private String parameterString(List<Nominal> paramTypes) {
		String paramStr = "(";
		boolean firstTime = true;
		if(paramTypes == null) {
			paramStr += "...";
		} else {
			for(Nominal t : paramTypes) {
				if(!firstTime) {
					paramStr += ",";
				}
				firstTime=false;
				paramStr += t.nominal();
			}
		}
		return paramStr + ")";		
	}
	
	private Pair<NameID,Nominal.FunctionOrMethod> selectCandidateFunctionOrMethod(String name,
			List<Nominal> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates)
			throws ResolveError {
		
		List<Type> rawParameters; 
		Type.Function target;
		
		if (parameters != null) {
			rawParameters = stripNominal(parameters);
			target = (Type.Function) Type.Function(Type.T_ANY, Type.T_ANY,
					rawParameters);
		} else {
			rawParameters = null;
			target = null;
		}

		NameID candidateID = null;
		Nominal.FunctionOrMethod candidateType = null;			
				
		for (Pair<NameID,Nominal.FunctionOrMethod> p : candidates) {
			Nominal.FunctionOrMethod nft = p.second();
			Type.FunctionOrMethod ft = nft.raw();			
			if (parameters == null || paramSubtypes(ft, target)) {
				// this is now a genuine candidate
				if(candidateType == null || paramStrictSubtypes(candidateType.raw(), ft)) {
					candidateType = nft;
					candidateID = p.first();
				} else if(!paramStrictSubtypes(ft, candidateType.raw())){ 
					// this is an ambiguous error
					String msg = name + parameterString(parameters) + " is ambiguous";
					// FIXME: should report all ambiguous matches here
					msg += "\n\tfound: " + candidateID + " : " + candidateType.nominal();
					msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
					throw new ResolveError(msg);
				}
			}			
		}				
		
		if(candidateType == null) {
			// second, didn't find matching message so generate error message
			String msg = "no match for " + name + parameterString(parameters);			
							
			for (Pair<NameID, Nominal.FunctionOrMethod> p : candidates) {
				msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
			}
			
			throw new ResolveError(msg);
		}				
		
		return new Pair(candidateID,candidateType);
	}
			
	private Pair<NameID,Nominal.Message> selectCandidateMessage(String name,
			Type.Reference receiver,
			List<Nominal> parameters,
			Collection<Pair<NameID, Nominal.Message>> candidates)
			throws ResolveError {
		
		List<Type> rawParameters; 
		Type.Function target;
		
		if (parameters != null) {
			rawParameters = stripNominal(parameters);
			target = (Type.Function) Type.Function(Type.T_ANY, Type.T_ANY,
					rawParameters);
		} else {
			rawParameters = null;
			target = null;
		}
		
		NameID candidateID = null;
		Nominal.Message candidateType = null;			
				
		for (Pair<NameID,Nominal.Message> p : candidates) {
			Nominal.Message nmt = p.second();
			Type.Message mt = nmt.raw();
			Type funrec = mt.receiver();
			
			if (receiver == funrec
					|| receiver == null
					|| (funrec != null && Type
							.isImplicitCoerciveSubtype(receiver, funrec))) {					
				// receivers match up OK ...				
				if (parameters == null || paramSubtypes(mt, target)) {
					// this is now a genuine candidate
					if(candidateType == null || paramStrictSubtypes(candidateType.raw(), mt)) {
						candidateType = nmt;
						candidateID = p.first();						
					} else if(!paramStrictSubtypes(mt, candidateType.raw())) {
						// this is an ambiguous error
						String msg = name + parameterString(parameters) + " is ambiguous";
						// FIXME: should report all ambiguous matches here
						msg += "\n\tfound: " + candidateID + " : " + candidateType.nominal();
						msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
						throw new ResolveError(msg);
					}
				}			
			}
		}				
		
		if (candidateType == null) {
			// second, didn't find matching message so generate error message
			String msg = "no match for " + receiver + "::" + name
					+ parameterString(parameters);

			for (Pair<NameID, Nominal.Message> p : candidates) {
				msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
			}

			throw new ResolveError(msg);
		}				
		
		return new Pair(candidateID,candidateType);
	}
	
	private void addCandidateFunctionsAndMethods(NameID nid,
			List<?> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates)
			throws ResolveError {
		ModuleID mid = nid.module();
		
		int nparams = parameters != null ? parameters.size() : -1;				
		
		WhileyFile wf = files.get(mid);
		if(wf != null) {
			// FIXME: need to include methods here as well
			for (WhileyFile.FunctionOrMethod f : wf.declarations(
					WhileyFile.FunctionOrMethod.class, nid.name())) {
				if (nparams == -1 || f.parameters.size() == nparams) {		
					Nominal.FunctionOrMethod ft = (Nominal.FunctionOrMethod) resolveAsType(f.unresolvedType(),context(wf,f));  
					candidates.add(new Pair(nid,ft));							
				}
			}
		} else {
			try {
				Module m = loader.loadModule(mid);
				for(Module.Method mm : m.methods()) {
					if ((mm.isFunction() || mm.isMethod())
							&& mm.name().equals(nid.name())
							&& (nparams == -1 || mm.type().params().size() == nparams)) {
						// FIXME: loss of nominal information
						Type.FunctionOrMethod t = (Type.FunctionOrMethod) mm.type();
						Nominal.FunctionOrMethod fom;
						if(t instanceof Type.Function) {
							Type.Function ft = (Type.Function) t;
							fom = new Nominal.Function(ft,ft);  
						} else {
							Type.Method mt = (Type.Method) t;
							fom = new Nominal.Method(mt,mt);
						}
						candidates.add(new Pair(nid,fom));
					}
				}
			} catch(ResolveError e) {
				
			}
		}		
	}
	
	private void addCandidateMessages(NameID nid,
			List<?> parameters,
			Collection<Pair<NameID, Nominal.Message>> candidates)
			throws ResolveError {
		ModuleID mid = nid.module();
		
		int nparams = parameters != null ? parameters.size() : -1;				
		
		WhileyFile wf = files.get(mid);
		if(wf != null) {
			// FIXME: need to include methods here as well
			for (WhileyFile.Message m : wf.declarations(
					WhileyFile.Message.class, nid.name())) {
				if (nparams == -1 || m.parameters.size() == nparams) {		
					Nominal.Message ft = (Nominal.Message) resolveAsType(m.unresolvedType(),context(wf,m));  
					candidates.add(new Pair(nid,ft));							
				}
			}
		} else {
			try {
				Module m = loader.loadModule(mid);
				for (Module.Method mm : m.methods()) {
					if (mm.isMessage()
							&& mm.name().equals(nid.name())
							&& (nparams == -1 || mm.type().params().size() == nparams)) {
						// FIXME: loss of nominal information
						Nominal.Message ft = new Nominal.Message(
								(Type.Message) mm.type(), (Type.Message) mm.type());
						candidates.add(new Pair(nid, ft));
					}
				}
			} catch(ResolveError e) {
				
			}
		}		
	}	
	
	/**
	 * Construct an appropriate list of import statements for a declaration in a
	 * given file. Thus, only import statements up to and including the given
	 * declaration will be included in the returned list.
	 * 
	 * @param wf
	 *            --- Whiley File in question to obtain list of import
	 *            statements.
	 * @param decl
	 *            --- declaration in Whiley File for which the list is desired.
	 * @return
	 */
	private Context context(WhileyFile wf,
			WhileyFile.Declaration decl) {		
		ModuleID mid = wf.module;
		ArrayList<WhileyFile.Import> imports = new ArrayList<WhileyFile.Import>();
		
		
		for (Declaration d : wf.declarations) {
			if (d instanceof WhileyFile.Import) {
				imports.add((WhileyFile.Import) d);
			}
			if (d == decl) {
				break;
			}
		}
		
		imports.add(new WhileyFile.Import(new PkgID("whiley","lang"), "*", null));		
		imports.add(new WhileyFile.Import(mid.pkg(), "*", null));
		imports.add(new WhileyFile.Import(mid.pkg(), mid.module(), "*")); 		
		
		Collections.reverse(imports);
		
		return new Context(wf,imports);
	}
	

	private static List<Type> stripNominal(List<Nominal> types) {
		ArrayList<Type> r = new ArrayList<Type>();
		for (Nominal t : types) {
			r.add(t.raw());
		}
		return r;
	}
	
}
