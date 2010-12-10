// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.jvm.rt;

import java.math.BigInteger;

/**
 * The BigNumbers class provides a simple, consistent encoding of unbounded
 * integers and unbounded rationals. For efficiency reasons, these are
 * represented purely as arrays of bytes. The first byte in the array indicates
 * whether or not the array represents a rational, or an integer. In fact, it
 * contains the number of bytes the represent the numerator and, hence, this is
 * zero for an integer.
 * 
 * @author djp
 * 
 */
public final class BigNumbers {
	
	// NOTE; this is not an efficient implementation!!!
	// See these:
	//
	// * http://www.apfloat.org/apfloat_java/
	//
	// * http://jscience.org/
	
	
	/**
	 * Construct an unbounded integer from an int.
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] valueOf(int value) {
		byte[] bytes;
		
		if(value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
			bytes = new byte[2];
			bytes[1] = (byte) value;
		} else if(value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
			bytes = new byte[3];
			short v = (short) value;
			bytes[2] = (byte) v;
			bytes[1] = (byte) (v >> 8);
		} else {
			bytes = new byte[5];
			bytes[4] = (byte) value;
			bytes[3] = (byte) (value >> 8);
			bytes[2] = (byte) (value >> 16);
			bytes[1] = (byte) (value >> 24);
		}
		
		// Finally, set header
		bytes[0] = 0;		
		
		return bytes;
	}
	
	public static byte[] valueOf(BigInteger bi) {
		byte[] bytes = bi.toByteArray();
		byte[] nbytes = new byte[bytes.length+1];
		System.arraycopy(bytes,0,nbytes,1,bytes.length);
		nbytes[0] = 0;
		return nbytes;
	}
	
	
	public static BigInteger toBigInteger(byte[] bytes) {
		if(bytes[0] != 0) { throw new IllegalArgumentException("Cannot convert rational to BigInteger"); }
		byte[] nbytes = new byte[bytes.length-1];
		System.arraycopy(bytes,1,nbytes,0,nbytes.length);
		return new BigInteger(nbytes);
	}
		
	private static byte[] intAdd(byte[] lhs, byte[] rhs) {
		BigInteger l = toBigInteger(lhs);
		BigInteger r = toBigInteger(rhs);		
		return valueOf(l.add(r));
	}
	
	private static byte[] intSub(byte[] lhs, byte[] rhs) {
		BigInteger l = toBigInteger(lhs);
		BigInteger r = toBigInteger(rhs);		
		return valueOf(l.subtract(r));
	}
	
	private static byte[] intMul(byte[] lhs, byte[] rhs) {
		BigInteger l = toBigInteger(lhs);
		BigInteger r = toBigInteger(rhs);		
		return valueOf(l.multiply(r));
	}
	
	private static byte[] intDiv(byte[] lhs, byte[] rhs) {
		BigInteger l = toBigInteger(lhs);
		BigInteger r = toBigInteger(rhs);		
		return valueOf(l.divide(r));
	}
	
	// ==================================================================================
	// Private Methods
	// ==================================================================================
	
	// ==================================================================================
	// Test Methods
	// ==================================================================================
	
	public static void main(String[] args) {
		for(int i=-Integer.MIN_VALUE;i!=Integer.MAX_VALUE;++i) {
			byte[] bytes = valueOf(i);
			BigInteger bi = toBigInteger(bytes);
			if(!bi.equals(BigInteger.valueOf(i))) {
				System.err.println("ERROR: " + i);
			}
		}
		
		System.out.println("Done");
	}
	
	
}
