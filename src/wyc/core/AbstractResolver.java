package wyc.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static wyc.lang.WhileyFile.*;
import wyc.lang.*;
import wyc.lang.WhileyFile.Context;
import wyil.ModuleLoader;
import wyil.lang.Module;
import wyil.lang.ModuleID;
import wyil.lang.NameID;
import wyil.lang.PkgID;
import wyil.lang.Type;
import wyil.lang.Value;
import wyil.util.Pair;
import wyil.util.ResolveError;
import wyil.util.Triple;


public abstract class AbstractResolver {	
	protected final ModuleLoader loader;
	protected final CompilationGroup files;
	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import whiley.lang.*</code>
	 * corresponds to the triple <code>("whiley.lang",*,null)</code>.
	 */
	private final HashMap<Triple<PkgID,String,String>,ArrayList<ModuleID>> importCache = new HashMap();	
	
	public AbstractResolver(ModuleLoader loader, CompilationGroup files) {		
		this.loader = loader;
		this.files = files;
	}

	/**
	 * This function checks whether the supplied name exists or not.
	 * 
	 * @param nid
	 *            The name whose existence we want to check for.
	 * 
	 * @return true if the package exists, false otherwise.
	 */
	protected boolean isName(NameID nid) {		
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
	 * This function checks whether the supplied package exists or not.
	 * 
	 * @param pkg
	 *            The package whose existence we want to check for.
	 * 
	 * @return true if the package exists, false otherwise.
	 */
	protected boolean isPackage(PkgID pkg) {
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
	protected boolean isModule(ModuleID mid) {
		try {
			if(files.get(mid) == null) {
				loader.loadModule(mid);
			}
			return true;
		} catch(ResolveError e) {
			return false;
		}
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
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 * 
	 * @param imp
	 * @return
	 */
	public List<ModuleID> imports(WhileyFile.Import imp) {			
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
	
	// =========================================================================
	// Required
	// =========================================================================
	
	public abstract Nominal resolveAsType(UnresolvedType type, Context context);
	
	public abstract NameID resolveAsName(String name, Context context) throws ResolveError;
	
	public abstract ModuleID resolveAsModule(String name, Context context) throws ResolveError;
	
	public abstract Value resolveAsConstant(NameID nid) throws ResolveError;
	
	public abstract Value resolveAsConstant(Expr e, Context context) ;
	
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

	protected Type expandOneLevel(Type type) throws ResolveError {
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
					r = resolveAsType(td.unresolvedType, td)
							.nominal();
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
	// BindFunction and BindMessage
	// =========================================================================				

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

	public Nominal.Message resolveAsMessage(NameID nid,
			Type.Reference receiver, List<Nominal> parameters)
			throws ResolveError {
		HashSet<Pair<NameID, Nominal.Message>> candidates = new HashSet<Pair<NameID, Nominal.Message>>();

		addCandidateMessages(nid, parameters, candidates);

		return selectCandidateMessage(nid.name(), receiver, parameters,
				candidates).second();
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
	public Pair<NameID, Nominal.FunctionOrMethodOrMessage> resolveAsFunctionOrMethodOrMessage(
			String name, Context context) throws ResolveError {
		try {
			return (Pair) resolveAsFunctionOrMethod(name, null, context);
		} catch (ResolveError e) {
			return (Pair) resolveAsMessage(name, null, null, context);
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
		for (WhileyFile.Import imp : context.imports()) {
			if (imp.matchName(name)) {
				for (ModuleID mid : imports(imp)) {					
					NameID nid = new NameID(mid,name);				
					addCandidateFunctionsAndMethods(nid,parameters,candidates);					
				}
			}
		}

		return selectCandidateFunctionOrMethod(name,parameters,candidates);
	}
	
	public Pair<NameID,Nominal.Message> resolveAsMessage(String name, Type.Reference receiver,
			List<Nominal> parameters, Context context) throws ResolveError {

		HashSet<Pair<NameID,Nominal.Message>> candidates = new HashSet<Pair<NameID,Nominal.Message>>(); 

		// first, try to find the matching message
		for (WhileyFile.Import imp : context.imports()) {
			if (imp.matchName(name)) {
				for (ModuleID mid : imports(imp)) {					
					NameID nid = new NameID(mid,name);				
					addCandidateMessages(nid,parameters,candidates);					
				}
			}
		}

		return selectCandidateMessage(name,receiver,parameters,candidates);
	}
	
	protected boolean paramSubtypes(Type.FunctionOrMethodOrMessage f1, Type.FunctionOrMethodOrMessage f2) {		
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

	protected boolean paramStrictSubtypes(Type.FunctionOrMethodOrMessage f1, Type.FunctionOrMethodOrMessage f2) {		
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

	protected String parameterString(List<Nominal> paramTypes) {
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

	protected Pair<NameID,Nominal.FunctionOrMethod> selectCandidateFunctionOrMethod(String name,
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

		return new Pair<NameID,Nominal.FunctionOrMethod>(candidateID,candidateType);
	}

	protected Pair<NameID,Nominal.Message> selectCandidateMessage(String name,
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

		return new Pair<NameID,Nominal.Message>(candidateID,candidateType);
	}

	protected void addCandidateFunctionsAndMethods(NameID nid,
			List<?> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates)
					throws ResolveError {
		ModuleID mid = nid.module();

		int nparams = parameters != null ? parameters.size() : -1;				

		WhileyFile wf = files.get(mid);
		if (wf != null) {
			for (WhileyFile.FunctionOrMethod f : wf.declarations(
					WhileyFile.FunctionOrMethod.class, nid.name())) {
				if (nparams == -1 || f.parameters.size() == nparams) {
					Nominal.FunctionOrMethod ft = (Nominal.FunctionOrMethod) resolveAsType(
							f.unresolvedType(), f);
					candidates.add(new Pair<NameID, Nominal.FunctionOrMethod>(
							nid, ft));
				}
			}
		} else {
			try {
				Module m = loader.loadModule(mid);
				for (Module.Method mm : m.methods()) {
					if ((mm.isFunction() || mm.isMethod())
							&& mm.name().equals(nid.name())
							&& (nparams == -1 || mm.type().params().size() == nparams)) {
						// FIXME: loss of nominal information
						Type.FunctionOrMethod t = (Type.FunctionOrMethod) mm
								.type();
						Nominal.FunctionOrMethod fom;
						if (t instanceof Type.Function) {
							Type.Function ft = (Type.Function) t;
							fom = new Nominal.Function(ft, ft);
						} else {
							Type.Method mt = (Type.Method) t;
							fom = new Nominal.Method(mt, mt);
						}
						candidates
								.add(new Pair<NameID, Nominal.FunctionOrMethod>(
										nid, fom));
					}
				}
			} catch (ResolveError e) {

			}
		}
	}

	protected void addCandidateMessages(NameID nid, List<?> parameters,
			Collection<Pair<NameID, Nominal.Message>> candidates)
			throws ResolveError {
		ModuleID mid = nid.module();

		int nparams = parameters != null ? parameters.size() : -1;

		WhileyFile wf = files.get(mid);
		if (wf != null) {
			for (WhileyFile.Message m : wf.declarations(
					WhileyFile.Message.class, nid.name())) {
				if (nparams == -1 || m.parameters.size() == nparams) {
					Nominal.Message ft = (Nominal.Message) resolveAsType(
							m.unresolvedType(), m);
					candidates.add(new Pair<NameID, Nominal.Message>(nid, ft));
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
								(Type.Message) mm.type(),
								(Type.Message) mm.type());
						candidates.add(new Pair<NameID, Nominal.Message>(nid,
								ft));
					}
				}
			} catch (ResolveError e) {

			}
		}
	}

	protected static List<Type> stripNominal(List<Nominal> types) {
		ArrayList<Type> r = new ArrayList<Type>();
		for (Nominal t : types) {
			r.add(t.raw());
		}
		return r;
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

}
