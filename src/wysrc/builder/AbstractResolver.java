package wysrc.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static wysrc.lang.SourceFile.*;
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
import wysrc.lang.*;
import wysrc.lang.SourceFile.Context;


public abstract class AbstractResolver {	
	protected final Builder builder;
	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import whiley.lang.*</code>
	 * corresponds to the triple <code>("whiley.lang",*,null)</code>.
	 */
	private final HashMap<Triple<PkgID,String,String>,ArrayList<ModuleID>> importCache = new HashMap();	
	
	public AbstractResolver(Builder project) {				
		this.builder = project;
	}
	
	// =========================================================================
	// Required
	// =========================================================================
	
	public abstract Nominal resolveAsType(UnresolvedType type, Context context);
	
	public abstract Nominal resolveAsUnconstrainedType(UnresolvedType type, Context context);
	
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

	public Nominal.EffectiveCollection expandAsEffectiveCollection(Nominal lhs) throws ResolveError {
		Type raw = lhs.raw();
		if(raw instanceof Type.EffectiveCollection) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.EffectiveCollection)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveCollection) Nominal.construct(nominal,raw);
		} else {
			return null;
		}
	}
	
	public Nominal.EffectiveMap expandAsEffectiveMap(Nominal lhs) throws ResolveError {
		Type raw = lhs.raw();
		if(raw instanceof Type.EffectiveMap) {
			Type nominal = expandOneLevel(lhs.nominal());
			if(!(nominal instanceof Type.EffectiveMap)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveMap) Nominal.construct(nominal,raw);
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

			SourceFile wf = builder.getSourceFile(mid);
			Type r = null;

			if (wf != null) {			
				SourceFile.Declaration decl = wf.declaration(nid.name());
				if(decl instanceof SourceFile.TypeDef) {
					SourceFile.TypeDef td = (SourceFile.TypeDef) decl;
					r = resolveAsType(td.unresolvedType, td)
							.nominal();
				} 
			} else {
				Module m = builder.getModule(mid);
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

}
