package wyc;

import java.util.ArrayList;

import wyc.util.Nominal;
import wyil.ModuleLoader;
import wyil.lang.Module;
import wyil.lang.ModuleID;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.util.ResolveError;

public class Expander {
	private ModuleLoader loader;

	// =========================================================================
	// expandAsList
	// =========================================================================	
		
	public static Nominal.Set expandAsSet(Nominal lhs) {
		Type.Set r = Type.effectiveSetType(lhs.raw());
		if(r != null) {
			return (Nominal.Set) Nominal.construct(r,r);
		} else {
			return null;
		}
	}
	
	public static Nominal.List expandAsList(Nominal lhs) {
		Type.List r = Type.effectiveListType(lhs.raw());
		if(r != null) {
			return (Nominal.List) Nominal.construct(r,r);
		} else {
			return null;
		}
	}
	
	public static Nominal.Dictionary expandAsDictionary(Nominal lhs) {
		Type.Dictionary r = Type.effectiveDictionaryType(lhs.raw());
		if(r != null) {
			return (Nominal.Dictionary) Nominal.construct(r,r);
		} else {
			return null;
		}
	}
	
	public static Nominal.Record expandAsRecord(Nominal lhs) {
		Type.Record r = Type.effectiveRecordType(lhs.raw());
		if(r != null) {
			return (Nominal.Record) Nominal.construct(r,r);
		} else {
			return null;
		}
	}
	
	public static Nominal.Reference expandAsReference(Nominal lhs) {
		Type.Reference r = Type.effectiveReferenceType(lhs.raw());
		if(r != null) {
			return (Nominal.Reference) Nominal.construct(r,r);
		} else {
			return null;
		}
	}
	
	private Type expandOneLevel(Type type) throws ResolveError {
		if(type instanceof Type.Leaf 
			|| type instanceof Type.Reference
			|| type instanceof Type.Tuple
			|| type instanceof Type.Set
			|| type instanceof Type.List
			|| type instanceof Type.Dictionary
			|| type instanceof Type.Record
			|| type instanceof Type.FunctionOrMethodOrMessage
			|| type instanceof Type.Negation) {
			return type;
		} else if(type instanceof Type.Union) {
			Type.Union ut = (Type.Union) type;
			ArrayList<Type> bounds = new ArrayList<Type>();
			for(Type b : ut.bounds()) {
				bounds.add(expandOneLevel(b));
			}
			return Type.Union(bounds);
		} else {
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.name();
			ModuleID mid = nid.module();
			Module m = loader.loadModule(mid);
			Module.TypeDef td = m.type(nid.name());
			if(td == null) {
				throw new ResolveError("unable to locate " + nid);
			}
			return expandOneLevel(td.type());
		} 
	}
}
