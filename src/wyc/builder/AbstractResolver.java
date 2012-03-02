package wyc.builder;

import java.util.*;

import wyil.lang.WyilFile;
import wybs.lang.Path;
import wybs.util.ResolveError;
import wyc.lang.*;
import wyc.lang.WhileyFile.Context;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.lang.Value;
import wyil.util.Triple;


public abstract class AbstractResolver<T extends Exception> {	
	protected final WhileyBuilder builder;
	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import whiley.lang.*</code>
	 * corresponds to the triple <code>("whiley.lang",*,null)</code>.
	 */
	private final HashMap<Triple<Path.ID,String,String>,ArrayList<Path.ID>> importCache = new HashMap();	
	
	public AbstractResolver(WhileyBuilder project) {				
		this.builder = project;
	}
	
	// =========================================================================
	// Required
	// =========================================================================
	
	public abstract Nominal resolveAsType(UnresolvedType type, Context context);
	
	public abstract Nominal resolveAsUnconstrainedType(UnresolvedType type, Context context);
	
	public abstract NameID resolveAsName(String name, Context context) throws ResolveError,T;
	
	public abstract Path.ID resolveAsModule(String name, Context context) throws Exception;
	
	public abstract Value resolveAsConstant(NameID nid) throws Exception;
	
	public abstract Value resolveAsConstant(Expr e, Context context) ;
	
	// =========================================================================
	// expandAsType
	// =========================================================================	

	public Nominal.EffectiveSet expandAsEffectiveSet(Nominal lhs) throws Exception {
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

	public Nominal.EffectiveList expandAsEffectiveList(Nominal lhs) throws Exception {
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

	public Nominal.EffectiveCollection expandAsEffectiveCollection(Nominal lhs) throws Exception {
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
	
	public Nominal.EffectiveMap expandAsEffectiveMap(Nominal lhs) throws Exception {
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
	
	public Nominal.EffectiveDictionary expandAsEffectiveDictionary(Nominal lhs) throws Exception {
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

	public Nominal.EffectiveRecord expandAsEffectiveRecord(Nominal lhs) throws Exception {		
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

	public Nominal.EffectiveTuple expandAsEffectiveTuple(Nominal lhs) throws Exception {
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

	public Nominal.Reference expandAsReference(Nominal lhs) throws Exception {
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

	public Nominal.FunctionOrMethod expandAsFunctionOrMethod(Nominal lhs) throws Exception {
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

	public Nominal.Message expandAsMessage(Nominal lhs) throws Exception {
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

	private Type expandOneLevel(Type type) throws Exception {
		if(type instanceof Type.Nominal){
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.name();			
			Path.ID mid = nid.module();

			WhileyFile wf = builder.getSourceFile(mid);
			Type r = null;

			if (wf != null) {			
				WhileyFile.Declaration decl = wf.declaration(nid.name());
				if(decl instanceof WhileyFile.TypeDef) {
					WhileyFile.TypeDef td = (WhileyFile.TypeDef) decl;
					r = resolveAsType(td.unresolvedType, td)
							.nominal();
				} 
			} else {
				WyilFile m = builder.getModule(mid);
				WyilFile.TypeDef td = m.type(nid.name());
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
