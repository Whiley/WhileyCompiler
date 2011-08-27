package wyil.lang.type;

import java.util.BitSet;

/**
 * A subtype relation encodes both a subtype and a supertype relation
 * between two separate domains (called <code>from</code> and
 * <code>to</code>).
 */
public final class SubtypeRelation {

	/** 
	 * Indicates the size of the "source" domain.
	 */
	private final int fromDomain;
	
	/**
	 * Indicates the size of the "target" domain.
	 */
	private final int toDomain;
	
	/**
	 * Stores subtype relation as a binary matrix for dimenaion
	 * <code>fromDomain</code> x <code>toDomain</code>. This matrix
	 * <code>r</code> is organised into row-major order, where
	 * <code>r[i][j]</code> implies <code>i :> j</code>.
	 */
	private final BitSet subTypes;
	
	/**
	 * Stores subtype relation as a binary matrix for dimenaion
	 * <code>fromDomain</code> x <code>toDomain</code>. This matrix
	 * <code>r</code> is organised into row-major order, where
	 * <code>r[i][j]</code> implies <code>i <: j</code>.
	 */
	private final BitSet superTypes;

	public SubtypeRelation(int fromDomain, int toDomain) {
		this.fromDomain = fromDomain;
		this.toDomain = toDomain;
		this.subTypes = new BitSet(fromDomain*toDomain);
		this.superTypes = new BitSet(fromDomain*toDomain);
		
		// Initially, set all sub- and super-types as true			
		subTypes.set(0,subTypes.size(),true);
		superTypes.set(0,superTypes.size(),true);
	}
	
	/**
	 * Check whether a a given node is a subtype of another.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean isSubtype(int from, int to) {
		return subTypes.get((toDomain*from) + to);
	}
	
	/**
	 * Check whether a a given node is a supertype of another.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean isSupertype(int from, int to) {
		return superTypes.get((toDomain*from) + to);
	}

	/**
	 * Set the subtype flag for a given pair in the relation.
	 * 
	 * @param from
	 * @param to
	 * @param flag
	 */
	public void setSubtype(int from, int to, boolean flag) {
		subTypes.set((toDomain*from) + to,flag);			
	}
	
	/**
	 * Set the supertype flag for a given pair in the relation.
	 * 
	 * @param from
	 * @param to
	 * @param flag
	 */
	public void setSupertype(int from, int to, boolean flag) {
		superTypes.set((toDomain*from) + to,flag);			
	}
	
	public String toString() {			
		return toString(subTypes) + "\n" + toString(superTypes);
	}
	
	public String toString(BitSet matrix) {
		String r = " |";
		for(int i=0;i!=toDomain;++i) {
			r = r + " " + (i%10);
		}
		r = r + "\n-+";
		for(int i=0;i!=toDomain;++i) {
			r = r + "--";
		}
		r = r + "\n";
		for(int i=0;i!=fromDomain;++i) {	
			r = r + (i%10) + "|";;
			for(int j=0;j!=toDomain;++j) {
				if(matrix.get((i*toDomain)+j)) {
					r += " 1";
				} else {
					r += " 0";
				}
			}	
			r = r + "\n";
		}
		return r;
	}

}