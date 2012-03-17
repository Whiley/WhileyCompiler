package whiley.lang;

import java.math.BigInteger;
import wyjc.runtime.BigRational;

public class Math$native {
	/**
	 * Compute the sqrt of an integer to within a given error threshold
	 * 
	 * @param i
	 * @param err
	 * @return
	 */
	public static BigRational sqrt(BigInteger i, BigRational err) {
		int nbits = i.bitLength();
		if(nbits < 63) {
			long v = i.longValue();
			double r = Math.sqrt(v);
			return BigRational.valueOf(r);
		} else {
			throw new RuntimeException("need to implement general sqrt");
		}
	}
}
