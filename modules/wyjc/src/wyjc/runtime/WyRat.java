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

package wyjc.runtime;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Random;

public final class WyRat extends Number implements Comparable<WyRat> {
	private static final WyRat[] cache = new WyRat[20];

	public static final WyRat MONE = WyRat.valueOf(-1);
	public static final WyRat ZERO = WyRat.valueOf(0);
	public static final WyRat ONE = WyRat.valueOf(1);

	private final BigInteger numerator;
	private final BigInteger denominator;

	public WyRat(String val) {
		int idx = val.indexOf('.');
		if(idx > 0) {
			String lhs = val.substring(0,idx);
			String rhs = val.substring(idx+1);
			BigInteger num = new BigInteger(lhs + rhs);
			BigInteger den = BigInteger.valueOf(10).pow(rhs.length());
			BigInteger gcd = num.gcd(den);
			if(!gcd.equals(BigInteger.ONE)) {
				num = num.divide(gcd);
				den = den.divide(gcd);
			}

			// normalise sign.
			BigInteger zero = BigInteger.ZERO;
			if(zero.compareTo(num) > 0 && zero.compareTo(den) >= 0) {
				num = num.negate();
				den = den.negate();
			} else if(zero.compareTo(den) == 0) {
				num = BigInteger.ZERO;
				den = BigInteger.ZERO;
			}

			// done, assign final fields.
			numerator = num;
			denominator = den;
		} else {
			numerator = new BigInteger(val);
			denominator = BigInteger.valueOf(1);
		}
	}

	public WyRat(BigDecimal dec) {
		this(dec.unscaledValue(),BigInteger.TEN.pow(dec.scale()));
	}

	public WyRat(BigInteger numerator) {
		this.numerator = numerator;
		this.denominator = BigInteger.ONE;
	}

	public WyRat(BigInteger numerator, BigInteger denominator) {
		BigInteger gcd = numerator.gcd(denominator);
		if(!gcd.equals(BigInteger.ONE)) {
			numerator = numerator.divide(gcd);
			denominator = denominator.divide(gcd);
		}

		// normalise sign.
		BigInteger zero = BigInteger.ZERO;
		if(zero.compareTo(denominator) > 0) {
			numerator = numerator.negate();
			denominator = denominator.negate();
		} else if(zero.compareTo(denominator) == 0) {
			numerator = BigInteger.ZERO;
			denominator = BigInteger.ZERO;
		}

		// done, assign final fields.
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public WyRat(int numerator, int denominator) {
		this(BigInteger.valueOf(numerator),BigInteger.valueOf(denominator));
	}

	public WyRat(byte[] numbytes) {
		this.numerator = new BigInteger(numbytes);
		this.denominator = BigInteger.ONE;
	}

	public WyRat(byte[] numbytes, byte[] denbytes) {
		this(new BigInteger(numbytes), new BigInteger(denbytes));
	}

	public BigInteger numerator() {
		return numerator;
	}

	public BigInteger denominator() {
		return denominator;
	}

	public boolean isInteger() {
		return denominator.equals(BigInteger.ONE);
	}

	/**
	 * Get an integer representation of this rational. <b>It is strongly
	 * recommended against using this method, since in most cases it will be
	 * very imprecise.</b>
	 */
	public int intValue() {
		long l = numerator.longValue();
		return (int) (l / denominator.longValue());
	}

	/**
	 * Get a long representation of this rational. <b>It is strongly
	 * recommended against using this method, since in most cases it will be
	 * very imprecise.</b>
	 */
	public long longValue() {
		long l = numerator.longValue();
		return l / denominator.longValue();
	}

	public float floatValue() {
		float l = numerator.floatValue();
		return l / denominator.floatValue();
	}

	public double doubleValue() {
		double l = numerator.doubleValue();
		return l / denominator.doubleValue();
	}

	public boolean equals(Object o) {
		if (o instanceof WyRat) {
			WyRat r = (WyRat) o;
			return numerator.equals(r.numerator)
					&& denominator.equals(r.denominator);
		}
		return false;
	}

	public int hashCode() {
		return numerator.hashCode() + denominator.hashCode();
	}

	public int compareTo(WyRat r) {
		BigInteger lhs = numerator.multiply(r.denominator);
		BigInteger rhs = r.numerator.multiply(denominator);
		return lhs.compareTo(rhs);
	}

	public BigDecimal round(int maxplaces) {
		BigDecimal number = BigDecimal.ZERO;
		int scale = 0;
		BigInteger num = numerator;

		while(maxplaces != scale && !num.equals(BigInteger.ZERO)) {
			if(num.compareTo(denominator) < 0) {
				num = num.multiply(BigInteger.TEN);
				scale = scale + 1;
			} else {
				BigInteger tmp = num.divide(denominator);
				num = num.mod(denominator);
				number = number.add(new BigDecimal(tmp,scale));
			}
		}

		return number;
	}

	/**
     * This method attempts to compute a string representation of the big
     * rational which has at most 10 decimal places. If this can't be done, then
     * it returns the number as a fraction.
     */
	public String toString() {
		if(isInteger()) {
			return numerator.toString() + ".0";
		} else {
			BigDecimal bd = round(10);
			WyRat br = new WyRat(bd);
			if (this.equals(br)) {
				String s = bd.toString();
				if (!s.contains(".")) {
					s += ".0";
				}
				return s;
			} else {

				return "(" + numerator + "/" + denominator + ")";
			}
		}
	}

	// =========================================================
	// ==================== ADDITION ===========================
	// =========================================================

	public WyRat add(int r) {
		BigInteger num = numerator.add(denominator.multiply(BigInteger.valueOf(r)));
		return new WyRat(num,denominator);
	}

	public WyRat add(long r) {
		BigInteger num = numerator.add(denominator.multiply(BigInteger.valueOf(r)));
		return new WyRat(num,denominator);
	}

	public WyRat add(final BigInteger r) {
		BigInteger num = numerator.add(denominator.multiply(r));
		return new WyRat(num,denominator);
	}

	public WyRat add(final WyRat r) {
		BigInteger num = numerator.multiply(r.denominator).add(r.numerator.multiply(denominator));
		BigInteger den = denominator.multiply(r.denominator);
		return new WyRat(num,den);
	}

	// =========================================================
	// ==================== SUBTRACTION ========================
	// =========================================================

	public WyRat subtract(int r) {
		BigInteger num = numerator.subtract(denominator.multiply(BigInteger.valueOf(r)));
		return new WyRat(num,denominator);
	}

	public WyRat subtract(long r) {
		BigInteger num = numerator.subtract(denominator.multiply(BigInteger.valueOf(r)));
		return new WyRat(num,denominator);
	}

	public WyRat subtract(final BigInteger r) {
		BigInteger num = numerator.subtract(denominator.multiply(r));
		return new WyRat(num,denominator);
	}

	public WyRat subtract(final WyRat r) {
		BigInteger num = numerator.multiply(r.denominator).subtract(
				r.numerator.multiply(denominator));
		BigInteger den = denominator.multiply(r.denominator);
		return new WyRat(num, den);
	}

	// =========================================================
	// ==================== MULTIPLICATION =====================
	// =========================================================

	public WyRat multiply(int r) {
		BigInteger num = numerator.multiply(BigInteger.valueOf(r));
		return new WyRat(num,denominator);
	}

	public WyRat multiply(long r) {
		BigInteger num = numerator.multiply(BigInteger.valueOf(r));
		return new WyRat(num,denominator);
	}

	public WyRat multiply(final BigInteger r) {
		BigInteger num = numerator.multiply(r);
		return new WyRat(num,denominator);
	}

	public WyRat multiply(final WyRat r) {
		BigInteger num = numerator.multiply(r.numerator);
		BigInteger den = denominator.multiply(r.denominator);
		return new WyRat(num, den);
	}

	// =========================================================
	// ======================= DIVISION ========================
	// =========================================================

	public WyRat divide(int r) {
		BigInteger den = denominator.multiply(BigInteger.valueOf(r));
		return new WyRat(numerator,den);
	}

	public WyRat divide(long r) {
		BigInteger den = denominator.multiply(BigInteger.valueOf(r));
		return new WyRat(numerator,den);
	}

	public WyRat divide(BigInteger r) {
		BigInteger den = denominator.multiply(r);
		return new WyRat(numerator,den);
	}

	public WyRat divide(final WyRat r) {
		BigInteger num = numerator.multiply(r.denominator);
		BigInteger den = denominator.multiply(r.numerator);
		return new WyRat(num, den);
	}

	public WyRat intDivide(final WyRat r) {
		BigInteger num = numerator.divide(r.numerator);
		return new WyRat(num);
	}

	public WyRat intRemainder(final WyRat r) {
		BigInteger num = numerator.remainder(r.numerator);
		return new WyRat(num);
	}

	// =========================================================
	// ========================= OTHER =========================
	// =========================================================
	public WyRat negate() {
		return new WyRat(numerator.negate(),denominator);
	}

	public WyRat ceil() {
		BigInteger i = numerator;
		BigInteger d = numerator.compareTo(BigInteger.ZERO) >= 0 ? BigInteger.ONE : BigInteger.valueOf(-1);
		while (!i.divideAndRemainder(denominator)[1].equals(BigInteger.ZERO)) {
			i = i.add(d);
		}
		return new WyRat(i, denominator);
	}

	public WyRat floor() {
		BigInteger i = numerator;
		BigInteger d = numerator.compareTo(BigInteger.ZERO) < 0 ? BigInteger.ONE : BigInteger.valueOf(-1);
		while (!i.divideAndRemainder(denominator)[1].equals(BigInteger.ZERO)) {
			i = i.add(d);
		}
		return new WyRat(i, denominator);
	}

	public static WyRat valueOf(int num, int den) {
		return new WyRat(BigInteger.valueOf(num),BigInteger.valueOf(den));
	}

	public static WyRat valueOf(long num, long den) {
		return new WyRat(BigInteger.valueOf(num),BigInteger.valueOf(den));
	}

	public static WyRat valueOf(BigInteger num) {
		return new WyRat(num);
	}

	public static WyRat valueOf(int x) {
		if(x > -10 && x <= 10) {
			int idx = x + 9;
			WyRat r = cache[idx];
			if(r == null) {
				r = new WyRat(BigInteger.valueOf(x));
				cache[idx] = r;
			}
			return r;
		} else {
			return new WyRat(BigInteger.valueOf(x));
		}
	}

	public static WyRat valueOf(long x) {
		if(x > -10 && x <= 10) {
			int idx = (int) x + 9;
			WyRat r = cache[idx];
			if(r == null) {
				r = new WyRat(BigInteger.valueOf(x));
				cache[idx] = r;
			}
			return r;
		} else {
			return new WyRat(BigInteger.valueOf(x));
		}
	}


	public static WyRat valueOf(double d) {
		// Check against infinities and NAN
		if(Double.isInfinite(d) || Double.isNaN(d)) {
			throw new NumberFormatException("BigInteger.valueOf() --- parameter cannot be infinity or NAN");
		}
		long l = Double.doubleToLongBits(d);

		// Pull out IEEE754 info
		boolean sign = (l&0x8000000000000000L) != 0;
		int exponent = (int) ((l & 0x7FF0000000000000L) >> 52);
		long numerator = l & 0xFFFFFFFFFFFFFL;
		final long denominator = 0x10000000000000L;
		boolean denormalised = exponent == 0;
		if(denormalised) {
			exponent = -1022;
		} else {
			exponent = exponent - 1023; // remove bias
		}

		WyRat base = valueOf(numerator, denominator).add(BigInteger.ONE);
		if (sign) {
			base = base.negate();
		}
		if(exponent >= 0) {
			// positive exponent so multiply
			BigInteger exp = BigInteger.ONE.shiftLeft(exponent);
			return base.multiply(exp);
		} else {
			// negative exponent so divide
			BigInteger exp = BigInteger.ONE.shiftLeft(-exponent);
			return base.divide(exp);
		}
	}
}
