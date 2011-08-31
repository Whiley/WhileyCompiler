package wyautl.lang;

import java.util.BitSet;

/**
 * A binary matrix represents a matrix of binary digits.
 * 
 * @author djp
 * 
 */
public final class BinaryMatrix {
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
	 * @param cols --- number of columns.
	 * @param rows --- number of rows.
	 * @param value --- initial value of all cells.
	 */
	public BinaryMatrix(int cols, int rows, boolean value) {	
		this.cols = cols;
		data = new BitSet(cols*rows);
		data.set(0,data.size(),value);		
	}
	
	public void set(int col, int row, boolean value) {
		data.set((row*cols)+col, value);
	}	
	
	public boolean get(int col, int row) {
		return data.get((row*cols)+col);
	}
}
