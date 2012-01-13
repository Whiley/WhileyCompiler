package wyc;

import wyc.util.Nominal;
import wyil.lang.Type;

public class Expander {


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
}
