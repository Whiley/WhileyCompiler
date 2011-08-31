package wyts.lang;

import java.util.BitSet;

/**
 * <p>
 * A subset relation encodes subset information between the states of two
 * automata. One node is a subset of another if every value accepted by the
 * first is accepted by the second.
 * </p>
 * 
 * <p>
 * The domains of the two automata are referred to as the <code>from</code> and
 * <code>to</code> domains. Thus, <code>isSubset(i,j)</code> indicates node
 * <code>i</code> in the <code>from</code> domain is a subset of node
 * <code>j<code> in the <code>to</code> domain.
 * </p>
 */
public final class SubsetRelation {

	/** 
	 * Indicates the size of the "source" domain.
	 */
	private final int fromDomain;
	
	/**
	 * Indicates the size of the "target" domain.
	 */
	private final int toDomain;

	/**
	 * Stores subset relation as a binary matrix for dimension
	 * <code>fromDomain</code> x <code>toDomain</code>. This matrix
	 * <code>r</code> is organised into row-major order, where
	 * <code>r[i][j]</code> implies <code>j</code> is a subset of <code>j</code>
	 * .
	 */
	private final BitSet subSets;
	
	/**
	 * Stores subset relation as a binary matrix for dimension
	 * <code>fromDomain</code> x <code>toDomain</code>. This matrix
	 * <code>r</code> is organised into row-major order, where
	 * <code>r[i][j]</code> implies <code>i <: j</code>.
	 */
	private final BitSet superSets;

	public SubsetRelation(int fromDomain, int toDomain) {
		this.fromDomain = fromDomain;
		this.toDomain = toDomain;
		this.subSets = new BitSet(fromDomain*toDomain);
		this.superSets = new BitSet(fromDomain*toDomain);
		
		// Initially, set all sub- and super-types as true			
		subSets.set(0,subSets.size(),true);
		superSets.set(0,superSets.size(),true);
	}
	
	/**
	 * Check whether a a given node is a subset of another.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean isSubSet(int from, int to) {
		return subSets.get((toDomain*from) + to);
	}
	
	/**
	 * Check whether a a given node is a superset of another.
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean isSuperSet(int from, int to) {
		return superSets.get((toDomain*from) + to);
	}

	/**
	 * Set the subtype flag for a given pair in the relation.
	 * 
	 * @param from
	 * @param to
	 * @param flag
	 */
	public void setSubSet(int from, int to, boolean flag) {
		subSets.set((toDomain*from) + to,flag);			
	}
	
	/**
	 * Set the supertype flag for a given pair in the relation.
	 * 
	 * @param from
	 * @param to
	 * @param flag
	 */
	public void setSuperSet(int from, int to, boolean flag) {
		superSets.set((toDomain*from) + to,flag);			
	}		
	
	public String toString() {			
		return toString(subSets) + "\n" + toString(superSets);
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