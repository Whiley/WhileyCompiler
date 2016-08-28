package wyc.lang;

import java.util.*;

import wycc.util.Pair;
import wyil.lang.Type;
import wyil.util.TypeSystem;

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

	// public abstract Type raw();

	public String toString() {
		return nominal().toString();
	}

	public static final Nominal T_ANY = new Base(Type.T_ANY);
	public static final Nominal T_VOID = new Base(Type.T_VOID);
	public static final Nominal T_NULL = new Base(Type.T_NULL);
	public static final Nominal T_NOTNULL = Negation(T_NULL);
	public static final Nominal T_META = new Base(Type.T_META);
	public static final Nominal T_BOOL = new Base(Type.T_BOOL);
	public static final Nominal T_BYTE = new Base(Type.T_BYTE);
	public static final Nominal T_INT = new Base(Type.T_INT);
	public static final Nominal T_ARRAY_ANY = new Array(Type.T_ARRAY_ANY);

	public static Nominal construct(Type nominal) {
		if(nominal instanceof Type.Reference) {
			return new Reference((Type.Reference)nominal);
		} else if(nominal instanceof Type.EffectiveArray) {
			return new Array((Type.EffectiveArray)nominal);
		} else if(nominal instanceof Type.EffectiveRecord) {
			return new Record((Type.EffectiveRecord)nominal);
		} else if(nominal instanceof Type.Function) {
			return new Function((Type.Function)nominal);
		} else if(nominal instanceof Type.Method) {
			return new Method((Type.Method)nominal);
		} else {
			return new Base(nominal);
		}
	}

	public static Nominal intersect(Nominal lhs, Nominal rhs, TypeSystem typeSystem) {
		Type nominal = typeSystem.intersect(lhs.nominal(),rhs.nominal());
		return Nominal.construct(nominal);
	}

	public static Reference Reference(Nominal element, String lifetime) {
		Type.Reference nominal = Type.Reference(element.nominal(), lifetime);
		return new Reference(nominal);
	}

	public static Array Array(Nominal element, boolean nonEmpty) {
		Type.Array nominal = Type.Array(element.nominal(), nonEmpty);
		return new Array(nominal);
	}

	public static Record Record(boolean isOpen, java.util.Map<String,Nominal> fields) {
		HashMap<String,Type> nominalFields = new HashMap<String,Type>();

		for(java.util.Map.Entry<String,Nominal> e : fields.entrySet()) {
			String field = e.getKey();
			Nominal type = e.getValue();
			nominalFields.put(field,type.nominal());
		}

		Type.Record nominal = Type.Record(isOpen,nominalFields);
		return new Record(nominal);
	}

	public static Nominal Union(Nominal t1, Nominal t2) {
		Type nominal = Type.Union(t1.nominal(),t2.nominal());
		return construct(nominal);
	}

	public static Nominal Negation(Nominal type) {
		Type nominal = Type.Negation(type.nominal());
		return construct(nominal);
	}

	public static class Base extends Nominal {
		private final Type nominal;

		Base(Type nominal) {
			this.nominal = nominal;
		}

		public Type nominal() {
			return nominal;
		}

		public boolean equals(Object o) {
			if (o instanceof Base) {
				Base b = (Base) o;
				return nominal.equals(b.nominal());
			}
			return false;
		}

		public int hashCode() {
			return nominal.hashCode();
		}
	}

	public static final class Reference extends Nominal {
		private final Type.Reference nominal;

		Reference(Type.Reference nominal) {
			this.nominal = nominal;
		}

		public Type.Reference nominal() {
			return nominal;
		}

		public Nominal element() {
			return construct(nominal.element());
		}

		public boolean equals(Object o) {
			if (o instanceof Reference) {
				Reference b = (Reference) o;
				return nominal.equals(b.nominal());
			}
			return false;
		}

		public int hashCode() {
			return nominal.hashCode();
		}
	}

	public static final class Array extends Nominal {
		private final Type.EffectiveArray nominal;

		Array(Type.EffectiveArray nominal) {
			this.nominal = nominal;
		}

		public Type nominal() {
			return (Type) nominal;
		}

		public Nominal key() {
			return T_INT;
		}

		public Nominal value() {
			return element();
		}

		public Nominal element() {
			return construct(nominal.element());
		}

		public Nominal.Array update(Nominal key, Nominal value) {
			Type n = (Type) nominal.update(key.nominal(), value.nominal());
			return (Array) construct(n);
		}

		public boolean equals(Object o) {
			if (o instanceof Array) {
				Array b = (Array) o;
				return nominal.equals(b.nominal());
			}
			return false;
		}

		public int hashCode() {
			return nominal.hashCode();
		}
	}

	public static final class Record extends Nominal {
		private final Type.EffectiveRecord nominal;

		Record(Type.EffectiveRecord nominal) {
			this.nominal = nominal;
		}

		public Type nominal() {
			return (Type) nominal;
		}

		public HashMap<String,Nominal> fields() {
			HashMap<String,Nominal> r = new HashMap<String,Nominal>();
			HashMap<String,Type> nominalFields = nominal.fields();
			for(java.util.Map.Entry<String, Type> e : nominal.fields().entrySet()) {
				String key = e.getKey();
				Type nominalField = nominalFields.get(key);
				r.put(e.getKey(), Nominal.construct(nominalField));
			}
			return r;
		}

		public Nominal field(String field) {
			Type rawField = nominal.fields().get(field);
			if(rawField == null) {
				return null;
			} else {
				return construct(nominal.fields().get(field));
			}
		}

		public Record update(String field, Nominal type) {
			Type.EffectiveRecord n = nominal.update(field,type.nominal());
			return new Record(n);
		}

		public boolean equals(Object o) {
			if (o instanceof Record) {
				Record b = (Record) o;
				return nominal.equals(b.nominal());
			}
			return false;
		}

		public int hashCode() {
			return nominal.hashCode();
		}
	}

	public abstract static class FunctionOrMethod extends Nominal {
		abstract public Type.FunctionOrMethod nominal();

		public Nominal ret(int i) {
			List<Type> nominalElements = nominal().returns();
			Type nominalElement = nominalElements.get(i);
			return construct(nominalElement);
		}
		
		public List<Nominal> returns() {
			ArrayList<Nominal> r = new ArrayList<Nominal>();
			List<Type> nominalElements = nominal().returns();
			for(int i=0;i!=nominalElements.size();++i) {
				Type nominalElement = nominalElements.get(i);
				r.add(construct(nominalElement));
			}
			return r;
		}
				
		public Nominal param(int i) {
			List<Type> nominalElements = nominal().params();
			Type nominalElement = nominalElements.get(i);
			return construct(nominalElement);
		}
		
		public List<Nominal> params() {
			ArrayList<Nominal> r = new ArrayList<Nominal>();
			List<Type> nominalElements = nominal().params();
			for(int i=0;i!=nominalElements.size();++i) {
				Type nominalElement = nominalElements.get(i);
				r.add(construct(nominalElement));
			}
			return r;
		}
	}

	public static final class Function extends FunctionOrMethod {
		private final Type.Function nominal;

		public Function(Type.Function nominal) {
			this.nominal = nominal;
		}

		public Type.Function nominal() {
			return nominal;
		}

		public boolean equals(Object o) {
			if (o instanceof Function) {
				Function b = (Function) o;
				return nominal.equals(b.nominal());
			}
			return false;
		}

		public int hashCode() {
			return nominal.hashCode();
		}
	}

	public static final class Method extends FunctionOrMethod {
		private final Type.Method nominal;

		public Method(Type.Method nominal) {
			this.nominal = nominal;
		}

		public Type.Method nominal() {
			return nominal;
		}

		public boolean equals(Object o) {
			if (o instanceof Method) {
				Method b = (Method) o;
				return nominal.equals(b.nominal());
			}
			return false;
		}

		public int hashCode() {
			return nominal.hashCode();
		}
	}

}
