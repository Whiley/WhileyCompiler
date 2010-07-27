package wyjc.stages;

import static wyil.util.SyntaxError.syntaxError;

import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Module.*;
import wyil.util.*;
import wyjc.lang.*;
import wyjc.lang.WhileyFile.Decl;
import wyjc.lang.WhileyFile.TypeDecl;

public class ModuleBuilder {
	private final ModuleLoader loader;
	private HashSet<ModuleID> modules;
	private HashMap<NameID,Block> types;
	private HashMap<NameID,Expr> unresolved;
	
	public ModuleBuilder(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public Module build(List<WhileyFile> files) {					
		modules = new HashSet<ModuleID>();		
		types = new HashMap<NameID,Block>();		
		unresolved = new HashMap<NameID,Expr>();
		
		for (WhileyFile f : files) {			
			modules.add(f.module);
		}
		
		// Stage 1 ... resolve and check constraints of all named types
		generateTypes(files);
		
		return new Module(wf.module,wf.filename,methods,types,constants);
	}
	
	/**
	 * The following method visits every define type statement in every whiley
	 * file being compiled, and determines its (expanded) constraints.  
	 * 
	 * @param files
	 */
	protected void generateTypes(List<WhileyFile> files) {		
		HashMap<NameID,SyntacticElement> srcs = new HashMap();
		
		// second construct list.
		for(WhileyFile f : files) {
			for(Decl d : f.declarations) {
				if(d instanceof TypeDecl) {
					TypeDecl td = (TypeDecl) d;
					NameID key = new NameID(f.module,td.name());					
					unresolved.put(key, td.constraint);
					srcs.put(key,d);
				}
			}
		}
		
		// third expand all types
		for(NameID key : unresolved.keySet()) {
			try {
				HashMap<NameID, Block> cache = new HashMap<NameID,Block>();			
				Block constraint = expandConstraint(key,cache);									
				types.put(key,constraint);				
			} catch(ResolveError ex) {
				syntaxError(ex.getMessage(),srcs.get(key),ex);
			}
		}		
	}
	
	protected Block expandConstraint(NameID key,
			HashMap<NameID, Block> cache) throws ResolveError {

		Block t = types.get(key);
		Block cached = cache.get(key);					
		
		if(cached != null) { 
			return cached; 
		} else if(t != null) {
			return t;
		} else if(!modules.contains(key.module())) {			
			// indicates a non-local key which we can resolve immediately
			Module mi = loader.loadModule(key.module());
			Module.TypeDef td = mi.type(key.name());						
			return td.constraint();
		}

		// following is needed to terminate any recursion
		cache.put(key, Type.T_RECURSIVE(key.toString(),null));
		
		// Ok, expand the type properly then
		Expr ut = unresolved.get(key);
		
		t = expandConstraint(ut, cache);
				 		
		if(Type.isOpenRecursive(key,t)) {
			// recursive case
			t = Type.T_RECURSIVE(key.toString(),t);			
		} 
		
		cache.put(key, t);
		
		// Done
		return t;		
	}	
	
	protected Type expandConstraint(Block t, HashMap<NameID, Type> cache) {
		if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;			
			return Type.T_LIST(expandType(lt.element, cache));
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;			
			return Type.T_SET(expandType(st.element, cache));
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			HashMap<String,Type> types = new HashMap<String,Type>();
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				types.put(e.getKey(),expandType(e.getValue(),cache));
			}
			return Type.T_TUPLE(types);
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			HashSet<Type.NonUnion> bounds = new HashSet<Type.NonUnion>();
			for(UnresolvedType b : ut.bounds) {
				Type bt = expandType(b,cache);
				if(bt instanceof Type.NonUnion) {
					bounds.add((Type.NonUnion)bt);
				} else {
					bounds.addAll(((Type.Union)bt).bounds);
				}
			}
			if(bounds.size() == 1) {
				return bounds.iterator().next();
			} else {
				return Type.T_UNION(bounds);
			}
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			return Type.T_PROCESS(expandType(ut.element,cache));			
		} else if(t instanceof UnresolvedType.Named) {
			UnresolvedType.Named dt = (UnresolvedType.Named) t;						
			Attribute.Module modInfo = dt.attribute(Attribute.Module.class);
			NameID name = new NameID(modInfo.module,dt.name);
			
			try {
				Type et = expandType(name,cache);
			
				if (Type.isExistential(et)) {
					System.out.println("GOT EXISTENTIAL FOR: " + et);
					return Type.T_NAMED(modInfo.module, dt.name, et);
				} else {
					return et;
				}
			} catch (ResolveError rex) {
				syntaxError(rex.getMessage(), t, rex);
				return null;
			}			
		}  else {
			// for base cases
			return resolve(t);
		}
	}
}
