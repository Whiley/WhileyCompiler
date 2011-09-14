package wyautl.util;

import java.util.BitSet;

/**
 * A binary matrix represents a matrix of binary digits.
 * 
 * @author David J. Pearce
 * 
 */
public final class BinaryMatrix {
	/** 
	 * Holds the number of rows.
	 */
	private final int rows;
	
	/** 
	 * Holds the number of columns.
	 */
	private final int cols;
	
	/**
	 * The binary data of this matrix.
	 */
	private final BitSet data;

	/**
	 * Construct an empty binary matrix of a given number of rows and columns.
	 * 
	 * @param rows --- number of rows.
	 * @param cols --- number of columns.
	 * @param value --- initial value of all cells.
	 */
	public BinaryMatrix(int rows, int cols, boolean value) {	
		this.cols = cols;
		this.rows = rows;
		data = new BitSet(cols*rows);
		data.set(0,data.size(),value);		
	}
	
	public void set(int row, int col, boolean value) {
		data.set((row*cols)+col, value);
	}	
	
	public boolean get(int row, int col) {
		return data.get((row*cols)+col);
	}
	
	public String toString() {
		String r = "{";
		for(int i=0;i!=rows;++i) {
			for(int j=0;j!=cols;++j) {
				if(get(i,j)) {
					r = r + "(" + i + "," + j + ")";
				}
			}	
		}
		return r + "}";						
	}
}
