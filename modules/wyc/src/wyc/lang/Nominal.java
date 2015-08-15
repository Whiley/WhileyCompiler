package wyc.lang;

import java.util.*;

import wycc.util.Pair;
import wyil.lang.Type;

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
	public static final Nominal T_INT = new Base(Type.T_INT,Type.T_INT);
	public static final Nominal T_REAL = new Base(Type.T_REAL,Type.T_REAL);
	public static final Nominal T_LIST_ANY = new List(Type.T_ARRAY_ANY,Type.T_ARRAY_ANY);

	public static Nominal construct(Type nominal, Type raw) {
		if(nominal instanceof Type.Reference && raw instanceof Type.Reference) {
			return new Reference((Type.Reference)nominal,(Type.Reference)raw);
		} else if(nominal instanceof Type.EffectiveTuple && raw instanceof Type.EffectiveTuple) {
			return new Tuple((Type.EffectiveTuple)nominal,(Type.EffectiveTuple)raw);
		} else if(nominal instanceof Type.EffectiveArray && raw instanceof Type.EffectiveArray) {
			return new List((Type.EffectiveArray)nominal,(Type.EffectiveArray)raw);
		} else if(nominal instanceof Type.EffectiveRecord && raw instanceof Type.EffectiveRecord) {
			return new Record((Type.EffectiveRecord)nominal,(Type.EffectiveRecord)raw);
		} else if(nominal instanceof Type.Function && raw instanceof Type.Function) {
			return new Function((Type.Function)nominal,(Type.Function)raw);
		} else if(nominal instanceof Type.Method && raw instanceof Type.Method) {
			return new Method((Type.Method)nominal,(Type.Method)raw);
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

	public static List List(Nominal element, boolean nonEmpty) {
		Type.Array nominal = Type.Array(element.nominal(), nonEmpty);
		Type.Array raw = Type.Array(element.raw(), nonEmpty);
		return new List(nominal,raw);
	}

	public static Record Record(boolean isOpen, java.util.Map<String,Nominal> fields) {
		HashMap<String,Type> nominalFields = new HashMap<String,Type>();
		HashMap<String,Type> rawFields = new HashMap<String,Type>();

		for(java.util.Map.Entry<String,Nominal> e : fields.entrySet()) {
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

	public static final class List extends Nominal {
		private final Type.EffectiveArray nominal;
		private final Type.EffectiveArray raw;

		List(Type.EffectiveArray nominal, Type.EffectiveArray raw) {
			this.nominal = nominal;
			this.raw = raw;
		}

		public Type nominal() {
			return (Type) nominal;
		}

		public Type raw() {
			return (Type) raw;
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

		public Nominal.List update(Nominal key, Nominal value) {
			Type n = (Type) nominal.update(key.nominal(), value.nominal());
			Type r = (Type) raw.update(key.raw(), value.raw());
			return (List) construct(n,r);
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

	public static final class Tuple extends Nominal {
		private final Type.EffectiveTuple nominal;
		private final Type.EffectiveTuple raw;

		Tuple(Type.EffectiveTuple nominal, Type.EffectiveTuple raw) {
			this.nominal = nominal;
			this.raw = raw;
		}

		public Type nominal() {
			return (Type) nominal;
		}

		public Type raw() {
			return (Type) raw;
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

	public static final class Record extends Nominal {
		private final Type.EffectiveRecord nominal;
		private final Type.EffectiveRecord raw;

		Record(Type.EffectiveRecord nominal, Type.EffectiveRecord raw) {
			this.nominal = nominal;
			this.raw = raw;
		}

		public Type nominal() {
			return (Type) nominal;
		}

		public Type raw() {
			return (Type) raw;
		}

		public HashMap<String,Nominal> fields() {
			HashMap<String,Nominal> r = new HashMap<String,Nominal>();
			HashMap<String,Type> nominalFields = nominal.fields();
			for(java.util.Map.Entry<String, Type> e : raw.fields().entrySet()) {
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
			Type.EffectiveRecord n = nominal.update(field,type.nominal());
			Type.EffectiveRecord r = raw.update(field,type.raw());
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

	public abstract static class FunctionOrMethod extends Nominal {
		abstract public Type.FunctionOrMethod nominal();

		abstract public Type.FunctionOrMethod raw();

		abstract public Nominal ret();

		abstract public Nominal param(int i);
		
		abstract public java.util.List<Nominal> params();
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
		
		public Nominal param(int i) {
			java.util.List<Type> rawElements = raw.params();
			java.util.List<Type> nominalElements = nominal.params();
			Type nominalElement = nominalElements.get(i);
			Type rawElement = rawElements.get(i);
			return construct(nominalElement,rawElement);
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

		public Nominal param(int i) {
			java.util.List<Type> rawElements = raw.params();
			java.util.List<Type> nominalElements = nominal.params();
			Type nominalElement = nominalElements.get(i);
			Type rawElement = rawElements.get(i);
			return construct(nominalElement,rawElement);
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

}
