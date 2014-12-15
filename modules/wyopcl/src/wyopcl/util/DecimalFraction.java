package wyopcl.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import wyil.lang.Constant;
/**
 * This class converts the division of two Constant.Decimals or a Constant.Decimal
 * to a fraction (numerator/denominator)
 * @author Min-Hsien Weng
 *
 */
public final class DecimalFraction extends Constant {
	
	private Constant.Integer numerator, denominator;
	/**
	 * Private constructor
	 */
	private DecimalFraction(){
		numerator = null;
		denominator = null;
	}
	
	/**
	 * Reduces the fraction to lowest terms using the common divisor
	 * @param num the numerator
	 * @param denum the denominator
	 */
	private void reduceFraction(BigInteger num, BigInteger denum){
		BigInteger gcd = denum.gcd(num);
		this.numerator = Constant.V_INTEGER(num.divide(gcd));
		this.denominator = Constant.V_INTEGER(denum.divide(gcd));
	}	
	/**
	 * Constructor with integral number and integral denominator
	 * @param num
	 * @param denum
	 * @return
	 */
	public static DecimalFraction V_DecimalFraction(Constant.Integer num, Constant.Integer denum){
		DecimalFraction frac = new DecimalFraction();
		frac.reduceFraction(num.value, denum.value);
		return frac;
	}
	
	
	/**
	 * Constructor with real number and real denominator
	 * @param num the numerator 
	 * @param denum the denominator
	 * @return an instance of DecimalFraction
	 */
	public static DecimalFraction V_DecimalFraction(Constant.Decimal num, Constant.Decimal denum){
		DecimalFraction fraction = new DecimalFraction();
		int scale = Math.max(num.value.scale(), denum.value.scale());
		fraction.reduceFraction(num.value.multiply(BigDecimal.TEN.pow(scale)).toBigInteger(),
								denum.value.multiply(BigDecimal.TEN.pow(scale)).toBigInteger());
		return fraction;
	}
	
	/**
	 * Constructor with a Constant.Decimal 
	 * @param decimal the decimal
	 * @return an instance of DecimalFraction
	 */
	public static DecimalFraction V_DecimalFraction(Constant.Decimal quotient){
		DecimalFraction fraction = new DecimalFraction();
		BigDecimal denum = BigDecimal.TEN.pow(quotient.value.scale());
		BigDecimal num = quotient.value.multiply(denum);
		fraction.reduceFraction(num.toBigInteger(), denum.toBigInteger());
		return fraction;
	}
	
	public Constant.Integer getDenominator() {
		return denominator;
	}

	public Constant.Integer getNumerator() {
		return numerator;
	}

	@Override
	public String toString() {
		//Check if denominator is 1. If so, then return numerator.
		if(this.denominator.value.equals(BigInteger.ONE)){
			return this.numerator+".0";
		}		
		return "("+this.numerator+"/"+this.denominator+")";		
	}

	@Override
	public int compareTo(Constant arg0) {
		if(arg0 instanceof DecimalFraction) {
			DecimalFraction frac = (DecimalFraction)arg0;
			int compareTo = this.numerator.compareTo(frac.numerator);
			if(compareTo==0){
				return this.denominator.compareTo(frac.denominator);
			}
			return compareTo;
		} 
		return -1; // for other types.  
		
	}

	@Override
	public wyil.lang.Type type() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
