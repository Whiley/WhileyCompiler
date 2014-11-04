// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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
