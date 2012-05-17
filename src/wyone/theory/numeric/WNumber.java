// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.theory.numeric;

import java.util.*;
import java.math.*;

import wyone.core.*;
import wyone.theory.logic.*;

public final class WNumber implements WValue {
	private static final WNumber[] cache = new WNumber[20];

	public static final WNumber MONE = WNumber.valueOf(-1);
	public static final WNumber ZERO = WNumber.valueOf(0);
	public static final WNumber ONE = WNumber.valueOf(1);
	public static final WNumber TWO = WNumber.valueOf(2);

	private final BigInteger numerator;
	private final BigInteger denominator;

	public WType type(SolverState state) {		
		if(denominator.equals(BigInteger.ONE)) {
			return WIntType.T_INT;
		} else {
			return WRealType.T_REAL;
		}
	}
	
	public WNumber(String val) {
		int idx = val.indexOf('.');
		if (idx > 0) {
			String lhs = val.substring(0, idx);
			String rhs = val.substring(idx + 1);
			BigInteger num = new BigInteger(lhs + rhs);
			BigInteger den = BigInteger.valueOf(10).pow(rhs.length());
			BigInteger gcd = num.gcd(den);
			if (!gcd.equals(BigInteger.ONE)) {
				num = num.divide(gcd);
				den = den.divide(gcd);
			}

			// normalise sign.
			BigInteger zero = BigInteger.ZERO;
			if (zero.compareTo(num) > 0 && zero.compareTo(den) >= 0) {
				num = num.negate();
				den = den.negate();
			} else if (zero.compareTo(den) == 0) {
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

	public WNumber(BigDecimal dec) {
		this(dec.unscaledValue(), BigInteger.TEN.pow(dec.scale()));
	}

	public WNumber(int numerator) {
		this(BigInteger.valueOf(numerator),BigInteger.ONE);		
	}
	
	public WNumber(BigInteger numerator) {
		this(numerator,BigInteger.ONE);		
	}

	public WNumber(BigInteger numerator, BigInteger denominator) {
		BigInteger gcd = numerator.gcd(denominator);
		if (!gcd.equals(BigInteger.ONE)) {
			numerator = numerator.divide(gcd);
			denominator = denominator.divide(gcd);
		}

		// normalise sign.
		BigInteger zero = BigInteger.ZERO;
		if (zero.compareTo(numerator) > 0 && zero.compareTo(denominator) > 0) {
			numerator = numerator.negate();
			denominator = denominator.negate();
		} else if (zero.compareTo(numerator) == 0) {
			numerator = BigInteger.ZERO;
			denominator = BigInteger.ONE;
		} else if (zero.compareTo(denominator) == 0) {
			throw new IllegalArgumentException("Cannot have zero denominator!");
		}

		// done, assign final fields.
		this.numerator = numerator;
		this.denominator = denominator;		
	}

	public WNumber(int numerator, int denominator) {
		this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
	}

	public WNumber(byte[] numbytes) {
		this.numerator = new BigInteger(numbytes);
		this.denominator = BigInteger.ONE;
	}

	public WNumber(byte[] numbytes, byte[] denbytes) {
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
	 * Get a long representation of this rational. <b>It is strongly recommended
	 * against using this method, since in most cases it will be very
	 * imprecise.</b>
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

	public <T> Set<T> match(Class<T> match) {
		if(WNumber.class == match) {
			HashSet<T> r = new HashSet();
			r.add((T)this);
			return r;
		} else {
			return Collections.EMPTY_SET;
		}
	}
	
	public WNumber substitute(Map<WExpr,WExpr> binding) {
		return this;
	}
	
	public List<WFormula> subterms() {
		return Collections.EMPTY_LIST;
	}
	
	public WLiteral rearrange(WExpr lhs) {
		if(lhs instanceof WNumber && this.equals(lhs)) {
			return WBool.TRUE;			
		} else if(lhs instanceof WValue) {
			return WBool.FALSE;
		} else {
			return lhs.rearrange(this);
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof WNumber) {
			WNumber r = (WNumber) o;
			return numerator.equals(r.numerator)
					&& denominator.equals(r.denominator);
		}
		return false;
	}

	public int hashCode() {
		return numerator.hashCode() + denominator.hashCode();
	}

	public int compareTo(WExpr e) {
		if(e instanceof WNumber) {
			WNumber n = (WNumber) e;
			BigInteger lhs = numerator.multiply(n.denominator);
			BigInteger rhs = n.numerator.multiply(denominator);
			return lhs.compareTo(rhs);
		} else if(CID < e.cid()) {
			return -1;
		} else {
			return 1;
		}
	}

	public BigDecimal round(int maxplaces) {
		BigDecimal number = BigDecimal.ZERO;
		int scale = 0;
		BigInteger num = numerator;

		while (maxplaces != scale && !num.equals(BigInteger.ZERO)) {
			if (num.compareTo(denominator) < 0) {
				num = num.multiply(BigInteger.TEN);
				scale = scale + 1;
			} else {
				BigInteger tmp = num.divide(denominator);
				num = num.mod(denominator);
				number = number.add(new BigDecimal(tmp, scale));
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
		BigDecimal bd = round(10);
		WNumber br = new WNumber(bd);
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

	// =========================================================
	// ==================== ADDITION ===========================
	// =========================================================

	public WNumber add(int r) {
		BigInteger num = numerator.add(denominator.multiply(BigInteger
				.valueOf(r)));
		return new WNumber(num, denominator);
	}

	public WNumber add(long r) {
		BigInteger num = numerator.add(denominator.multiply(BigInteger
				.valueOf(r)));
		return new WNumber(num, denominator);
	}

	public WNumber add(final BigInteger r) {
		BigInteger num = numerator.add(denominator.multiply(r));
		return new WNumber(num, denominator);
	}

	public WNumber add(final WNumber r) {
		BigInteger num = numerator.multiply(r.denominator).add(
				r.numerator.multiply(denominator));
		BigInteger den = denominator.multiply(r.denominator);
		return new WNumber(num, den);
	}

	// =========================================================
	// ==================== SUBTRACTION ========================
	// =========================================================

	public WNumber subtract(int r) {
		BigInteger num = numerator.subtract(denominator.multiply(BigInteger
				.valueOf(r)));
		return new WNumber(num, denominator);
	}

	public WNumber subtract(long r) {
		BigInteger num = numerator.subtract(denominator.multiply(BigInteger
				.valueOf(r)));
		return new WNumber(num, denominator);
	}

	public WNumber subtract(final BigInteger r) {
		BigInteger num = numerator.subtract(denominator.multiply(r));
		return new WNumber(num, denominator);
	}

	public WNumber subtract(final WNumber r) {
		BigInteger num = numerator.multiply(r.denominator).subtract(
				r.numerator.multiply(denominator));
		BigInteger den = denominator.multiply(r.denominator);
		return new WNumber(num, den);
	}

	// =========================================================
	// ==================== MULTIPLICATION =====================
	// =========================================================

	public WNumber multiply(int r) {
		BigInteger num = numerator.multiply(BigInteger.valueOf(r));
		return new WNumber(num, denominator);
	}

	public WNumber multiply(long r) {
		BigInteger num = numerator.multiply(BigInteger.valueOf(r));
		return new WNumber(num, denominator);
	}

	public WNumber multiply(final BigInteger r) {
		BigInteger num = numerator.multiply(r);
		return new WNumber(num, denominator);
	}

	public WNumber multiply(final WNumber r) {
		BigInteger num = numerator.multiply(r.numerator);
		BigInteger den = denominator.multiply(r.denominator);
		return new WNumber(num, den);
	}

	// =========================================================
	// ======================= DIVISION ========================
	// =========================================================

	public WNumber divide(int r) {
		BigInteger den = denominator.multiply(BigInteger.valueOf(r));
		return new WNumber(numerator, den);
	}

	public WNumber divide(long r) {
		BigInteger den = denominator.multiply(BigInteger.valueOf(r));
		return new WNumber(numerator, den);
	}

	public WNumber divide(BigInteger r) {
		BigInteger den = denominator.multiply(r);
		return new WNumber(numerator, den);
	}

	public WNumber divide(final WNumber r) {
		BigInteger num = numerator.multiply(r.denominator);
		BigInteger den = denominator.multiply(r.numerator);
		return new WNumber(num, den);
	}

	// =========================================================
	// ========================= OTHER =========================
	// =========================================================
	public WNumber ceil() {		
		BigInteger i = numerator;
		BigInteger d = numerator.compareTo(BigInteger.ZERO) >= 0 ? BigInteger.ONE : BigInteger.valueOf(-1);
		while (!i.divideAndRemainder(denominator)[1].equals(BigInteger.ZERO)) {
			i = i.add(d);
		}		
		return new WNumber(i, denominator);
	}
	
	public WNumber floor() {
		BigInteger i = numerator;
		BigInteger d = numerator.compareTo(BigInteger.ZERO) < 0 ? BigInteger.ONE : BigInteger.valueOf(-1);
		while (!i.divideAndRemainder(denominator)[1].equals(BigInteger.ZERO)) {
			i = i.add(d);
		}		
		return new WNumber(i, denominator);
	}
	
	public WNumber negate() {
		return new WNumber(numerator.negate(), denominator);
	}

	public static WNumber valueOf(int num, int den) {
		return new WNumber(BigInteger.valueOf(num), BigInteger.valueOf(den));
	}

	public static WNumber valueOf(long num, long den) {
		return new WNumber(BigInteger.valueOf(num), BigInteger.valueOf(den));
	}

	public static WNumber valueOf(BigInteger num) {
		return new WNumber(num);
	}

	public static WNumber valueOf(int x) {
		if (x > -10 && x <= 10) {
			int idx = x + 9;
			WNumber r = cache[idx];
			if (r == null) {
				r = new WNumber(BigInteger.valueOf(x));
				cache[idx] = r;
			}
			return r;
		} else {
			return new WNumber(BigInteger.valueOf(x));
		}
	}

	public static WNumber valueOf(long x) {
		if (x > -10 && x <= 10) {
			int idx = (int) x + 9;
			WNumber r = cache[idx];
			if (r == null) {
				r = new WNumber(BigInteger.valueOf(x));
				cache[idx] = r;
			}
			return r;
		} else {
			return new WNumber(BigInteger.valueOf(x));
		}
	}
	

	private final static int CID = WExprs.registerCID();
	public int cid() { return CID; }
}
