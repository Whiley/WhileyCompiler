package wyc.util;

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
	public static final Nominal T_STRING = new Base(Type.T_STRING,Type.T_STRING);
	
	public static Nominal construct(Type nominal, Type raw) {
		if(raw instanceof Type.Reference) {
			if(nominal instanceof Type.Reference) {
				return new Reference((Type.Reference)nominal,(Type.Reference)raw);
			} else {
				return new Reference((Type.Reference)raw,(Type.Reference)raw);
			}
		} else if(raw instanceof Type.Tuple) {
			if(nominal instanceof Type.Tuple) {
				return new Tuple((Type.Tuple)nominal,(Type.Tuple)raw);
			} else {
				return new Tuple((Type.Tuple)raw,(Type.Tuple)raw);
			}
		} else if(raw instanceof Type.Set) {
			if(nominal instanceof Type.Set) { 
				return new Set((Type.Set)nominal,(Type.Set)raw);
			} else {
				return new Set((Type.Set)raw,(Type.Set)raw);
			}			
		} else if(raw instanceof Type.List) {
			if(nominal instanceof Type.List) {
				return new List((Type.List)nominal,(Type.List)raw);
			} else {
				return new List((Type.List)raw,(Type.List)raw);
			}
		} else if(raw instanceof Type.Dictionary) {
			if(nominal instanceof Type.Dictionary) {
				return new Dictionary((Type.Dictionary)nominal,(Type.Dictionary)raw);
			} else {
				return new Dictionary((Type.Dictionary)raw,(Type.Dictionary)raw);
			}
		} else if(raw instanceof Type.Record) {
			if(nominal instanceof Type.Record) {
				return new Record((Type.Record)nominal,(Type.Record)raw);
			} else {
				return new Record((Type.Record)raw,(Type.Record)raw);
			}
		} else if(raw instanceof Type.Function) {
			if(nominal instanceof Type.Function) {
				return new Function((Type.Function)nominal,(Type.Function)raw);
			} else {
				return new Function((Type.Function)raw,(Type.Function)raw);
			}
		} else if(raw instanceof Type.Method) {
			if(nominal instanceof Type.Method) {
				return new Method((Type.Method)nominal,(Type.Method)raw);
			} else {
				return new Method((Type.Method)raw,(Type.Method)raw);
			}
		} else if(raw instanceof Type.Message) {
			if(nominal instanceof Type.Message) {
				return new Message((Type.Message)nominal,(Type.Message)raw);
			} else {
				return new Message((Type.Message)raw,(Type.Message)raw);
			}
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
	
	public static final class Base extends Nominal {
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
	
	public static final class Reference extends Nominal {
		private final Type.Reference nominal;
		private final Type.Reference raw;
		
		public Reference(Type.Reference nominal, Type.Reference raw) {
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
	
	public static final class Set extends Nominal {
		private final Type.Set nominal;
		private final Type.Set raw;
		
		public Set(Type.Set nominal, Type.Set raw) {
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
	
	public static final class List extends Nominal {
		private final Type.List nominal;
		private final Type.List raw;
		
		public List(Type.List nominal, Type.List raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.List nominal() {
			return nominal;
		}
		
		public Type.List raw() {
			return raw;
		}
		
		public Nominal element() {
			return construct(nominal.element(),raw.element());			
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
	
	public static final class Dictionary extends Nominal {
		private final Type.Dictionary nominal;
		private final Type.Dictionary raw;
		
		public Dictionary(Type.Dictionary nominal, Type.Dictionary raw) {
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
	
	public static final class Tuple extends Nominal {
		private final Type.Tuple nominal;
		private final Type.Tuple raw;
		
		public Tuple(Type.Tuple nominal, Type.Tuple raw) {
			this.nominal = nominal;
			this.raw = raw;
		}
		
		public Type.Tuple nominal() {
			return nominal;
		}
		
		public Type.Tuple raw() {
			return raw;
		}
		
		public java.util.List<Nominal> elements() {			
			// FIXME: loss of nominal information
			ArrayList<Nominal> r = new ArrayList<Nominal>();
			for(Type e : raw.elements()) {
				r.add(construct(e,e));
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
		
	public static final class Record extends Nominal {
		private final Type.Record nominal;
		private final Type.Record raw;
		
		public Record(Type.Record nominal, Type.Record raw) {
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
			for(Map.Entry<String, Type> e : raw.fields().entrySet()) {
				r.put(e.getKey(), Nominal.construct(e.getValue(),e.getValue()));
			}
			return r;
		}
		
		public Nominal field(String field) {
			// FIXME: loss of nominal information
			Type t = raw.fields().get(field);
			if(t == null) {
				return null;
			} else {
				return construct(t,t);
			}
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
			// FIXME: loss of nominal information
			ArrayList<Nominal> r = new ArrayList<Nominal>();
			for(Type e : raw.params()) {
				r.add(construct(e,e));
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
			// FIXME: loss of nominal information
			ArrayList<Nominal> r = new ArrayList<Nominal>();
			for(Type e : raw.params()) {
				r.add(construct(e,e));
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
			// FIXME: loss of nominal information
			ArrayList<Nominal> r = new ArrayList<Nominal>();
			for(Type e : raw.params()) {
				r.add(construct(e,e));
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
