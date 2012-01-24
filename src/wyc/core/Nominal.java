package wyc.core;

import java.util.*;

import wyil.lang.Type;
import wyil.util.Pair;

/**
 * A type pair is used to simplify the problem of dealing with nominal and raw
 * types. It is essentially just a pair, but it makes clear which position
 * contains the nominal type and which contains the raw type.
 * 
 * @author David J. Pearce
 * 
 * @param <T>
 */
public abstract class Nominal {
		
	public abstract Type nominal();
	
	public abstract Type raw();
	
	public String toString() {
		return "(" + nominal() + ", " + raw() + ")";
	}
	
	public static final Nominal T_ANY = new Base(Type.T_ANY,Type.T_ANY);
	public static final Nominal T_VOID = new Base(Type.T_VOID,Type.T_VOID);
	public static final Nominal T_NULL = new Base(Type.T_NULL,Type.T_NULL);
	public static final Nominal T_NOTNULL = Negation(T_NULL);
	public static final Nominal T_META = new Base(Type.T_META,Type.T_META);
	public static final Nominal T_BOOL = new Base(Type.T_BOOL,Type.T_BOOL);
	public static final Nominal T_BYTE = new Base(Type.T_BYTE,Type.T_BYTE);
	public static final Nominal T_CHAR = new Base(Type.T_CHAR,Type.T_CHAR);
	public static final Nominal T_INT = new Base(Type.T_INT,Type.T_INT);
	public static final Nominal T_REAL = new Base(Type.T_REAL,Type.T_REAL);
	public static final Nominal T_STRING = new Strung(Type.T_STRING,Type.T_STRING);
	
	public static Nominal construct(Type nominal, Type raw) {
		if(raw instanceof Type.Reference && nominal instanceof Type.Reference) {
			return new Reference((Type.Reference)nominal,(Type.Reference)raw);			
		} else if(raw instanceof Type.Tuple && nominal instanceof Type.Tuple) {
			return new Tuple((Type.Tuple)nominal,(Type.Tuple)raw);			
		} else if(raw instanceof Type.UnionOfTuples && nominal instanceof Type.UnionOfTuples) {
			return new UnionOfTuples((Type.UnionOfTuples)nominal,(Type.UnionOfTuples)raw);			
		} else if(raw instanceof Type.Set && nominal instanceof Type.Set) { 
			return new Set((Type.Set)nominal,(Type.Set)raw);					
		} else if(raw instanceof Type.UnionOfSets && nominal instanceof Type.UnionOfSets) {
			return new UnionOfSets((Type.UnionOfSets)nominal,(Type.UnionOfSets)raw);			
		} else if(raw instanceof Type.List && nominal instanceof Type.List) {
			return new List((Type.List)nominal,(Type.List)raw);			
		} else if(raw instanceof Type.UnionOfLists && nominal instanceof Type.UnionOfLists) {
			return new UnionOfLists((Type.UnionOfLists)nominal,(Type.UnionOfLists)raw);			
		}  else if(raw instanceof Type.Dictionary && nominal instanceof Type.Dictionary) {
			return new Dictionary((Type.Dictionary)nominal,(Type.Dictionary)raw);			
		} else if(raw instanceof Type.UnionOfDictionaries && nominal instanceof Type.UnionOfDictionaries) {
			return new UnionOfDictionaries((Type.UnionOfDictionaries)nominal,(Type.UnionOfDictionaries)raw);
		} else if(raw instanceof Type.UnionOfMaps && nominal instanceof Type.UnionOfMaps) {
			return new UnionOfMaps((Type.UnionOfMaps)nominal,(Type.UnionOfMaps)raw);			
		}else if(raw instanceof Type.UnionOfCollections && nominal instanceof Type.UnionOfCollections) {
			return new UnionOfCollections((Type.UnionOfCollections)nominal,(Type.UnionOfCollections)raw);			
		} else if(raw instanceof Type.Record && nominal instanceof Type.Record) {
			return new Record((Type.Record)nominal,(Type.Record)raw);		
		} else if(raw instanceof Type.UnionOfRecords && nominal instanceof Type.UnionOfRecords) {
			return new UnionOfRecords((Type.UnionOfRecords)nominal,(Type.UnionOfRecords)raw);		
		} else if(raw instanceof Type.Function && nominal instanceof Type.Function) {
			return new Function((Type.Function)nominal,(Type.Function)raw);			
		} else if(raw instanceof Type.Method && nominal instanceof Type.Method) {
			return new Method((Type.Method)nominal,(Type.Method)raw);			
		} else if(raw instanceof Type.Message && nominal instanceof Type.Message) {
			return new Message((Type.Message)nominal,(Type.Message)raw);			
		} else if(raw instanceof Type.Strung && nominal instanceof Type.Strung) {
			return new Strung(nominal,raw);
		} else {
			return new Base(nominal,raw);
		}
	}
	
	public static Nominal intersect(Nominal lhs, Nominal rhs) {		
		Type nominal = Type.intersect(lhs.nominal(),rhs.nominal());		
		Type raw = Type.intersect(lhs.raw(),rhs.raw());
		return Nominal.construct(nominal, raw);
	}
		
	public static Tuple Tuple(Nominal... elements) {
		ArrayList<Type> rawElements = new ArrayList<Type>();
		ArrayList<Type> nominalElements = new ArrayList<Type>();
		for(Nominal e : elements) {
			rawElements.add(e.raw());
			nominalElements.add(e.raw());
		}
		Type.Tuple rawType = Type.Tuple(rawElements);
		Type.Tuple nominalType = Type.Tuple(nominalElements);
		return new Tuple(nominalType,rawType);
	}
	
	public static Tuple Tuple(Collection<Nominal> elements) {
		ArrayList<Type> rawElements = new ArrayList<Type>();
		ArrayList<Type> nominalElements = new ArrayList<Type>();
		for(Nominal e : elements) {
			rawElements.add(e.raw());
			nominalElements.add(e.raw());
		}
		Type.Tuple rawType = Type.Tuple(rawElements);
		Type.Tuple nominalType = Type.Tuple(nominalElements);
		return new Tuple(nominalType,rawType);
	}
	
	public static Reference Reference(Nominal element) {
		Type.Reference nominal = Type.Reference(element.nominal());
		Type.Reference raw = Type.Reference(element.raw());
		return new Reference(nominal,raw);
	}
	
	public static Set Set(Nominal element, boolean nonEmpty) {
		Type.Set nominal = Type.Set(element.nominal(), nonEmpty);
		Type.Set raw = Type.Set(element.raw(), nonEmpty);
		return new Set(nominal,raw);
	}
	
	public static List List(Nominal element, boolean nonEmpty) {
		Type.List nominal = Type.List(element.nominal(), nonEmpty);
		Type.List raw = Type.List(element.raw(), nonEmpty);
		return new List(nominal,raw);
	}
	
	public static Dictionary Dictionary(Nominal key, Nominal value) {
		Type.Dictionary nominal = Type.Dictionary(key.nominal(),value.nominal());
		Type.Dictionary raw = Type.Dictionary(key.raw(),value.raw());
		return new Dictionary(nominal,raw);
	}
	
	public static Record Record(boolean isOpen, java.util.Map<String,Nominal> fields) {
		HashMap<String,Type> nominalFields = new HashMap<String,Type>();
		HashMap<String,Type> rawFields = new HashMap<String,Type>();
		
		for(Map.Entry<String,Nominal> e : fields.entrySet()) {
			String field = e.getKey();
			Nominal type = e.getValue();
			nominalFields.put(field,type.nominal());
			rawFields.put(field,type.raw());
		}
		
		Type.Record nominal = Type.Record(isOpen,nominalFields);
		Type.Record raw = Type.Record(isOpen,rawFields);
		return new Record(nominal,raw);
	}
	
	public static Nominal Union(Nominal t1, Nominal t2) {
		Type nominal = Type.Union(t1.nominal(),t2.nominal());
		Type raw = Type.Union(t1.raw(),t2.raw());
		return construct(nominal,raw);
	}		
	
	public static Nominal Negation(Nominal type) {
		Type nominal = Type.Negation(type.nominal());
		Type raw = Type.Negation(type.raw());
		return construct(nominal,raw);		
	}	
	
	public static class Base extends Nominal {
		private final Type raw;
		private final Type nominal;
		
		Base(Type nominal, Type raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type nominal() {
			return nominal;
		}
		
		public Type raw() {
			return raw;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Base) {
				Base b = (Base) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public static final class Strung extends Base implements EffectiveCollection {
		Strung(Type nominal, Type raw) {
			super(nominal,raw);
		}
		public Nominal element() {
			return Nominal.T_CHAR;
		}
		public Type.Strung nominal() {
			return Type.T_STRING;
		}
		
		public Type.Strung raw() {
			return Type.T_STRING;
		}
	}
	
	public static final class Reference extends Nominal {
		private final Type.Reference nominal;
		private final Type.Reference raw;
		
		Reference(Type.Reference nominal, Type.Reference raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.Reference nominal() {
			return nominal;
		}
		
		public Type.Reference raw() {
			return raw;
		}
		
		public Nominal element() {
			return construct(nominal.element(),raw.element());
		}
		
		public boolean equals(Object o) {
			if (o instanceof Reference) {
				Reference b = (Reference) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public interface EffectiveCollection {
		public Type.EffectiveCollection raw();		
		public Type.EffectiveCollection nominal();		
		public Nominal element();			
	}
	
	public interface EffectiveSet extends EffectiveCollection {
		public Type.EffectiveSet raw();		
		public Type.EffectiveSet nominal();		
		public Nominal element();							
	}
	
	public static final class Set extends Nominal implements EffectiveSet {
		private final Type.Set nominal;
		private final Type.Set raw;
		
		Set(Type.Set nominal, Type.Set raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.Set nominal() {
			return nominal;
		}
		
		public Type.Set raw() {
			return raw;
		}
		
		public Nominal element() {
			return construct(nominal.element(),raw.element());			
		}
		
		public boolean equals(Object o) {
			if (o instanceof Set) {
				Set b = (Set) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public interface EffectiveMap extends EffectiveCollection {
		public Type.EffectiveMap raw();		
		public Type.EffectiveMap nominal();		
		public Nominal element();					
		public EffectiveMap update(Nominal key,Nominal value);
	}
	
	public interface EffectiveList extends EffectiveMap {
		public Type.EffectiveList raw();		
		public Type.EffectiveList nominal();		
		public Nominal element();							
	}
	
	public static final class List extends Nominal implements EffectiveList {
		private final Type.List nominal;
		private final Type.List raw;
		
		List(Type.List nominal, Type.List raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.List nominal() {
			return nominal;
		}
		
		public Type.List raw() {
			return raw;
		}
		
		public Nominal key() {
			return T_INT;
		}
		
		public Nominal value() {
			return element();
		}
		
		public Nominal element() {
			return construct(nominal.element(),raw.element());			
		}
		
		public Nominal.EffectiveMap update(Nominal key, Nominal value) {
			return (EffectiveMap) construct((Type) nominal.update(key.nominal(), value.nominal()),
					(Type) raw.update(key.raw(), value.raw()));
		}
		
		public boolean equals(Object o) {
			if (o instanceof List) {
				List b = (List) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public interface EffectiveDictionary extends EffectiveMap {
		public Type.EffectiveDictionary raw();		
		public Type.EffectiveDictionary nominal();		
		public Nominal key();					
		public Nominal value();
		public EffectiveDictionary update(Nominal key, Nominal value);
	}
	
	public static final class Dictionary extends Nominal implements EffectiveDictionary {
		private final Type.Dictionary nominal;
		private final Type.Dictionary raw;
		
		Dictionary(Type.Dictionary nominal, Type.Dictionary raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.Dictionary nominal() {
			return nominal;
		}
		
		public Type.Dictionary raw() {
			return raw;
		}
		
		public Nominal key() {
			return construct(nominal.key(),raw.key());			
		}
		
		public Nominal value() {
			return construct(nominal.value(),raw.value());			
		}
		
		public Nominal element() {
			return Tuple(key(),value());
		}
		
		public Nominal.Dictionary update(Nominal key, Nominal value) {
			key = Nominal.Union(key, key());
			value = Nominal.Union(value, value());
			return Nominal.Dictionary(key,value);
		}		
		
		public boolean equals(Object o) {
			if (o instanceof Dictionary) {
				Dictionary b = (Dictionary) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public interface EffectiveTuple {
		public Type.EffectiveTuple raw();
		
		public Type.EffectiveTuple nominal();
		
		public Nominal element(int index);
		
		public ArrayList<Nominal> elements();		
	}
		
	public static final class Tuple extends Nominal implements EffectiveTuple {
		private final Type.Tuple nominal;
		private final Type.Tuple raw;
		
		Tuple(Type.Tuple nominal, Type.Tuple raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.Tuple nominal() {
			return nominal;
		}
		
		public Type.Tuple raw() {
			return raw;
		}
		
		public Nominal element(int index) {
			return construct(nominal.element(index),raw.element(index));
		}
		
		public ArrayList<Nominal> elements() {			
			ArrayList<Nominal> r = new ArrayList<Nominal>();
			java.util.List<Type> rawElements = raw.elements();
			java.util.List<Type> nominalElements = nominal.elements();
			for(int i=0;i!=rawElements.size();++i) {
				Type nominalElement = nominalElements.get(i);
				Type rawElement = rawElements.get(i);				
				r.add(construct(nominalElement,rawElement));
			}
			return r;			
		}
		
		public boolean equals(Object o) {
			if (o instanceof Tuple) {
				Tuple b = (Tuple) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public interface EffectiveRecord {
		public Type.EffectiveRecord raw();
		
		public Type.EffectiveRecord nominal();
		
		public Nominal field(String field);
		
		public HashMap<String,Nominal> fields();
		
		public EffectiveRecord update(String field, Nominal type);
	}
	
	public static final class Record extends Nominal implements EffectiveRecord {
		private final Type.Record nominal;
		private final Type.Record raw;
		
		Record(Type.Record nominal, Type.Record raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public boolean isOpen() {
			return raw.isOpen();
		}
		
		public Type.Record nominal() {
			return nominal;
		}
		
		public Type.Record raw() {
			return raw;
		}
		
		public HashMap<String,Nominal> fields() {
			HashMap<String,Nominal> r = new HashMap<String,Nominal>();
			HashMap<String,Type> nominalFields = nominal.fields();
			for(Map.Entry<String, Type> e : raw.fields().entrySet()) {
				String key = e.getKey();
				Type rawField = e.getValue();
				Type nominalField = nominalFields.get(key);
				r.put(e.getKey(), Nominal.construct(nominalField,rawField));
			}
			return r;
		}
		
		public Nominal field(String field) {
			Type rawField = raw.fields().get(field);
			if(rawField == null) {
				return null;
			} else {
				return construct(nominal.fields().get(field),rawField);
			}
		}
		
		public Record update(String field, Nominal type) {
			Type.Record n = nominal.update(field,type.nominal());
			Type.Record r = raw.update(field,type.raw());
			return new Record(n,r);
		}
		
		public boolean equals(Object o) {
			if (o instanceof Record) {
				Record b = (Record) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public static final class UnionOfCollections extends Nominal implements EffectiveCollection {
		private final Type.UnionOfCollections nominal;
		private final Type.UnionOfCollections raw;
		
		UnionOfCollections(Type.UnionOfCollections nominal, Type.UnionOfCollections raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.UnionOfCollections nominal() {
			return nominal;
		}
		
		public Type.UnionOfCollections raw() {
			return raw;
		}
				
		public Nominal element() {
			return construct(nominal.element(),raw.element());			
		}		
		
		public boolean equals(Object o) {
			if (o instanceof UnionOfCollections) {
				UnionOfCollections b = (UnionOfCollections) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public static final class UnionOfMaps extends Nominal implements EffectiveMap {
		private final Type.UnionOfMaps nominal;
		private final Type.UnionOfMaps raw;
		
		UnionOfMaps(Type.UnionOfMaps nominal, Type.UnionOfMaps raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.UnionOfMaps nominal() {
			return nominal;
		}
		
		public Type.UnionOfMaps raw() {
			return raw;
		}
			
		public Nominal key() {
			return construct(nominal.key(),raw.key());
		}
		
		public Nominal value() {
			return construct(nominal.value(),raw.value());			
		}
		
		public Nominal element() {
			return construct(nominal.element(),raw.element());			
		}
		
		public Nominal.EffectiveMap update(Nominal key, Nominal value) {
			return (EffectiveMap) construct((Type) nominal.update(key.nominal(), value.nominal()),
					(Type) raw.update(key.raw(), value.raw()));
		}
		
		public boolean equals(Object o) {
			if (o instanceof UnionOfMaps) {
				UnionOfMaps b = (UnionOfMaps) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public static final class UnionOfSets extends Nominal implements EffectiveSet {
		private final Type.UnionOfSets nominal;
		private final Type.UnionOfSets raw;
		
		UnionOfSets(Type.UnionOfSets nominal, Type.UnionOfSets raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.UnionOfSets nominal() {
			return nominal;
		}
		
		public Type.UnionOfSets raw() {
			return raw;
		}
				
		public Nominal element() {
			return construct(nominal.element(),raw.element());			
		}		
		
		public boolean equals(Object o) {
			if (o instanceof UnionOfSets) {
				UnionOfSets b = (UnionOfSets) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}	
	
	public static final class UnionOfDictionaries extends Nominal implements EffectiveDictionary {
		private final Type.UnionOfDictionaries nominal;
		private final Type.UnionOfDictionaries raw;
		
		UnionOfDictionaries(Type.UnionOfDictionaries nominal, Type.UnionOfDictionaries raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.UnionOfDictionaries nominal() {
			return nominal;
		}
		
		public Type.UnionOfDictionaries raw() {
			return raw;
		}
				
		public Nominal key() {
			return construct(nominal.key(),raw.key());			
		}
		
		public Nominal value() {
			return construct(nominal.value(),raw.value());			
		}
				
		public Nominal element() {
			return Tuple(key(),value());
		}
		
		public EffectiveDictionary update(Nominal key, Nominal value) {
			Type.EffectiveDictionary n = nominal.update(key.nominal(),value.nominal());
			Type.EffectiveDictionary r = raw.update(key.raw(),value.raw());
			return (EffectiveDictionary) construct((Type) n, (Type)r);
		}
		
		public boolean equals(Object o) {
			if (o instanceof UnionOfDictionaries) {
				UnionOfDictionaries b = (UnionOfDictionaries) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}	
	
	public static final class UnionOfLists extends Nominal implements EffectiveList {
		private final Type.UnionOfLists nominal;
		private final Type.UnionOfLists raw;
		
		UnionOfLists(Type.UnionOfLists nominal, Type.UnionOfLists raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.UnionOfLists nominal() {
			return nominal;
		}
		
		public Type.UnionOfLists raw() {
			return raw;
		}
			
		public Nominal key() {
			return T_INT;
		}
		
		public Nominal value() {
			return element();
		}
		
		public Nominal element() {
			return construct(nominal.element(),raw.element());			
		}
		
		public Nominal.EffectiveMap update(Nominal key, Nominal value) {
			return (EffectiveMap) construct((Type) nominal.update(key.nominal(), value.nominal()),
					(Type) raw.update(key.raw(), value.raw()));
		}
		
		public boolean equals(Object o) {
			if (o instanceof UnionOfLists) {
				UnionOfLists b = (UnionOfLists) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}	
	
	public static final class UnionOfRecords extends Nominal implements EffectiveRecord {
		private final Type.UnionOfRecords nominal;
		private final Type.UnionOfRecords raw;
		
		UnionOfRecords(Type.UnionOfRecords nominal, Type.UnionOfRecords raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.UnionOfRecords nominal() {
			return nominal;
		}
		
		public Type.UnionOfRecords raw() {
			return raw;
		}
		
		public HashMap<String,Nominal> fields() {
			HashMap<String,Nominal> r = new HashMap<String,Nominal>();
			HashMap<String,Type> nominalFields = nominal.fields();
			for(Map.Entry<String, Type> e : raw.fields().entrySet()) {
				String key = e.getKey();
				Type rawField = e.getValue();
				Type nominalField = nominalFields.get(key);
				r.put(e.getKey(), Nominal.construct(nominalField,rawField));
			}
			return r;
		}
		
		public Nominal field(String field) {
			Type rawField = raw.fields().get(field);
			if(rawField == null) {
				return null;
			} else {
				return construct(nominal.fields().get(field),rawField);
			}
		}
		
		public EffectiveRecord update(String field, Nominal type) {
			Type.EffectiveRecord n = nominal.update(field,type.nominal());
			Type.EffectiveRecord r = raw.update(field,type.raw());
			return (EffectiveRecord) construct((Type) n, (Type)r);
		}
		
		public boolean equals(Object o) {
			if (o instanceof UnionOfRecords) {
				UnionOfRecords b = (UnionOfRecords) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public static final class UnionOfTuples extends Nominal implements EffectiveTuple {
		private final Type.UnionOfTuples nominal;
		private final Type.UnionOfTuples raw;
		
		UnionOfTuples(Type.UnionOfTuples nominal, Type.UnionOfTuples raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.UnionOfTuples nominal() {
			return nominal;
		}
		
		public Type.UnionOfTuples raw() {
			return raw;
		}
		
		public Nominal element(int index) {
			return construct(nominal.element(index),raw.element(index));
		}
		
		public ArrayList<Nominal> elements() {
			ArrayList<Nominal> r = new ArrayList<Nominal>();			
			for(int i=0;i!=raw.size();++i) {
				r.add(Nominal.construct(nominal.element(i),raw.element(i)));
			}
			return r;
		}		
				
		public boolean equals(Object o) {
			if (o instanceof UnionOfTuples) {
				UnionOfTuples b = (UnionOfTuples) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public abstract static class FunctionOrMethodOrMessage extends Nominal {
		
		abstract public Type.FunctionOrMethodOrMessage raw();
		
		abstract public Type.FunctionOrMethodOrMessage nominal();
		
		
		abstract public Nominal ret();
		
		abstract public java.util.List<Nominal> params();
	}
	
	public abstract static class FunctionOrMethod extends FunctionOrMethodOrMessage {
		abstract public Type.FunctionOrMethod nominal();
		
		abstract public Type.FunctionOrMethod raw();		
	}
	
	public static final class Function extends FunctionOrMethod {
		private final Type.Function nominal;
		private final Type.Function raw;
		
		public Function(Type.Function nominal, Type.Function raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.Function nominal() {
			return nominal;
		}
		
		public Type.Function raw() {
			return raw;
		}
		
		public Nominal ret() {
			return construct(nominal.ret(),raw.ret());
		}
		
		public java.util.List<Nominal> params() {			
			ArrayList<Nominal> r = new ArrayList<Nominal>();
			java.util.List<Type> rawElements = raw.params();
			java.util.List<Type> nominalElements = nominal.params();
			for(int i=0;i!=rawElements.size();++i) {
				Type nominalElement = nominalElements.get(i);
				Type rawElement = rawElements.get(i);				
				r.add(construct(nominalElement,rawElement));
			}				
			return r;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Function) {
				Function b = (Function) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public static final class Method extends FunctionOrMethod {
		private final Type.Method nominal;
		private final Type.Method raw;
		
		public Method(Type.Method nominal, Type.Method raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.Method nominal() {
			return nominal;
		}
		
		public Type.Method raw() {
			return raw;
		}
		
		public Nominal ret() {
			return construct(nominal.ret(),raw.ret());
		}
		
		public java.util.List<Nominal> params() {			
			ArrayList<Nominal> r = new ArrayList<Nominal>();
			java.util.List<Type> rawElements = raw.params();
			java.util.List<Type> nominalElements = nominal.params();
			for(int i=0;i!=rawElements.size();++i) {
				Type nominalElement = nominalElements.get(i);
				Type rawElement = rawElements.get(i);				
				r.add(construct(nominalElement,rawElement));
			}				
			return r;		
		}
		
		
		public boolean equals(Object o) {
			if (o instanceof Method) {
				Method b = (Method) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}
	
	public static final class Message extends FunctionOrMethodOrMessage {
		private final Type.Message nominal;
		private final Type.Message raw;
		
		public Message(Type.Message nominal, Type.Message raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.Message nominal() {
			return nominal;
		}
		
		public Type.Message raw() {
			return raw;
		}
		
		public Nominal receiver() {
			return construct(nominal.receiver(),raw.receiver());
		}
		
		public Nominal ret() {
			return construct(nominal.ret(),raw.ret());
		}
		
		public java.util.List<Nominal> params() {			
			ArrayList<Nominal> r = new ArrayList<Nominal>();
			java.util.List<Type> rawElements = raw.params();
			java.util.List<Type> nominalElements = nominal.params();
			for(int i=0;i!=rawElements.size();++i) {
				Type nominalElement = nominalElements.get(i);
				Type rawElement = rawElements.get(i);				
				r.add(construct(nominalElement,rawElement));
			}				
			return r;			
		}
		
		public boolean equals(Object o) {
			if (o instanceof Message) {
				Message b = (Message) o;
				return nominal.equals(b.nominal()) && raw.equals(b.raw());
			}
			return false;
		}
		
		public int hashCode() {
			return raw.hashCode();
		}
	}	
}
