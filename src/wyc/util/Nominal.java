package wyc.util;

import java.util.ArrayList;
import java.util.List;

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
public class Nominal<T extends Type> extends Pair<Type,T> {
	public Nominal(Type nominal, T raw) {
		super(nominal,raw);
	}
	
	public Type nominal() {
		return super.first;
	}
	
	public T raw() {
		return super.second;
	}
	
	public static final Nominal<Type> T_ANY = new Nominal<Type>(Type.T_ANY,Type.T_ANY);
	public static final Nominal<Type> T_VOID = new Nominal<Type>(Type.T_VOID,Type.T_VOID);
	public static final Nominal<Type> T_META = new Nominal<Type>(Type.T_META,Type.T_META);
	public static final Nominal<Type> T_BOOL = new Nominal<Type>(Type.T_BOOL,Type.T_BOOL);
	public static final Nominal<Type> T_BYTE = new Nominal<Type>(Type.T_BYTE,Type.T_BYTE);
	public static final Nominal<Type> T_CHAR = new Nominal<Type>(Type.T_CHAR,Type.T_CHAR);
	public static final Nominal<Type> T_INT = new Nominal<Type>(Type.T_INT,Type.T_INT);
	public static final Nominal<Type> T_REAL = new Nominal<Type>(Type.T_REAL,Type.T_REAL);
	public static final Nominal<Type> T_STRING = new Nominal<Type>(Type.T_STRING,Type.T_STRING);
	
	public static <S extends Type> Nominal<Type> Union(Nominal<S> t1, Nominal<S> t2) {
		Type nominal = Type.Union(t1.nominal(),t2.nominal());
		Type raw = Type.Union(t1.raw(),t2.raw());
		return new Nominal<Type>(nominal,raw);
	}
	
	public static Nominal<Type.Dictionary> Dictionary(Nominal<Type> key, Nominal<Type> value) {
		Type.Dictionary nominal = (Type.Dictionary) Type.Dictionary(key.nominal(),value.nominal());
		Type.Dictionary raw = (Type.Dictionary) Type.Dictionary(key.nominal(),value.nominal());
		return new Nominal<Type.Dictionary>(nominal,raw);
	}
	
	public static Nominal<Type> Negation(Nominal<Type> type) {
		return new Nominal<Type>(Type.Negation(type.nominal()),
				Type.Negation(type.raw()));
	}
	
	public static <T extends Type> List<Type> stripNominal(List<Nominal<T>> types) {
		ArrayList<Type> r = new ArrayList<Type>();
		for (Nominal<? extends Type> t : types) {
			r.add(t.raw());
		}
		return r;
	}
}
