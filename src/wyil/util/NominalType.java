package wyil.util;

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
public class NominalType<T extends Type> extends Pair<Type,T> {
	public NominalType(Type nominal, T raw) {
		super(nominal,raw);
	}
	
	public Type nominal() {
		return super.first;
	}
	
	public T raw() {
		return super.second;
	}
}
