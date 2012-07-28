package whiley.lang;

import java.math.BigInteger;
import wyjc.runtime.WhileyRational;

public class Math$native {
	/**
	 * Compute the sqrt of an integer to within a given error threshold
	 * 
	 * @param i
	 * @param err
	 * @return
	 */
	public static WhileyRational sqrt(BigInteger i, WhileyRational err) {
		int nbits = i.bitLength();
		if(nbits < 32) {
			// Note, need to restrict to ints, since long=>double conversion is
			// lossy in Java. 
			int v = i.intValue();
			double r = Math.sqrt(v);
			return WhileyRational.valueOf(r);
		} else {
			throw new RuntimeException("need to implement general sqrt");
		}
	}
}
