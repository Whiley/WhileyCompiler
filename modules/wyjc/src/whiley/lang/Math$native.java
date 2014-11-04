package whiley.lang;

import java.math.BigInteger;
import wyjc.runtime.WyRat;

public class Math$native {
	/**
	 * Compute the sqrt of an integer to within a given error threshold
	 *
	 * @param i
	 * @param err
	 * @return
	 */
	public static WyRat sqrt(BigInteger i, WyRat err) {
		int nbits = i.bitLength();
		if(nbits < 32) {
			// Note, need to restrict to ints, since long=>double conversion is
			// lossy in Java.
			int v = i.intValue();
			double r = java.lang.Math.sqrt(v);
			return WyRat.valueOf(r);
		} else {
			throw new RuntimeException("need to implement general sqrt");
		}
	}
	public static WyRat sin(WyRat x) {
		return WyRat.valueOf(java.lang.Math.sin(x.doubleValue()));
	}
	public static WyRat cos(WyRat x) {
		return WyRat.valueOf(java.lang.Math.cos(x.doubleValue()));
	}
	public static WyRat tan(WyRat x) {
		return WyRat.valueOf(java.lang.Math.tan(x.doubleValue()));
	}
	public static WyRat asin(WyRat x) {
		return WyRat.valueOf(java.lang.Math.asin(x.doubleValue()));
	}
	public static WyRat acos(WyRat x) {
		return WyRat.valueOf(java.lang.Math.acos(x.doubleValue()));
	}
	public static WyRat atan(WyRat x) {
		return WyRat.valueOf(java.lang.Math.atan(x.doubleValue()));
	}
	public static WyRat random() {
		return WyRat.valueOf(java.lang.Math.random());
	}
	public static WyRat exp(WyRat x) {
		return WyRat.valueOf(java.lang.Math.exp(x.doubleValue()));
	}
	public static WyRat exp10(WyRat x) {
		return WyRat.valueOf(java.lang.Math.pow(10.0, x.doubleValue()));
	}
	public static WyRat log(WyRat x) {
		return WyRat.valueOf(java.lang.Math.log(x.doubleValue()));
	}
	public static WyRat log10(WyRat x) {
		return WyRat.valueOf(java.lang.Math.log10(x.doubleValue()));
	}
}

