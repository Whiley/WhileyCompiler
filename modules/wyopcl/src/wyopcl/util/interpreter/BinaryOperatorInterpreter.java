package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Constant.Integer;
import wyopcl.util.DecimalFraction;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.BinaryOperator</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.BinaryOperator
 */
public class BinaryOperatorInterpreter extends Interpreter {

	private static BinaryOperatorInterpreter instance;	
	private BinaryOperatorInterpreter(){
	}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static BinaryOperatorInterpreter getInstance(){
		if (instance == null){
			instance = new BinaryOperatorInterpreter();
		}
		return instance;
	}

	/**
	 * Perform arithmetic operations on the operands.
	 * @param left the left operand
	 * @param right the right operand
	 * @param code the <code>Codes.BinaryOperator</code> operator
	 * @return the resulting constant
	 */
	private Constant performOperation(Constant left, Constant right, Codes.BinaryOperator code){
		byte leftValue, rightValue;		
		// Check the operator
		switch (code.kind) {
		case ADD:
			if(left instanceof Constant.Integer){
				return ((Constant.Integer)left).add((Constant.Integer)right);
			}else if (left instanceof Constant.Decimal){
				return ((Constant.Decimal)left).add(((Constant.Decimal)right));
			}else if (left instanceof Constant.Char){
				//Result = (char)(char + char)
				return Constant.V_CHAR((char)(((Constant.Char)left).value + ((Constant.Char)right).value));
			} 
			break;
		case SUB:			
			if(left instanceof Constant.Integer){
				return ((Constant.Integer)left).subtract((Constant.Integer)right);
			}else if (left instanceof Constant.Decimal){
				return ((Constant.Decimal)left).subtract(((Constant.Decimal)right));
			}else if (left instanceof Constant.Char){
				//Result = (int)(char - char)
				return Constant.V_INTEGER(BigInteger.valueOf(((Constant.Char)left).value -  ((Constant.Char)right).value));
			} 
			break;
		case MUL:		
			if(left instanceof Constant.Integer){
				return ((Constant.Integer)left).multiply((Constant.Integer)right);
			}else if (left instanceof Constant.Decimal){
				return ((Constant.Decimal)left).multiply(((Constant.Decimal)right));
			}else if (left instanceof Constant.Char){
				//Char * char
				return Constant.V_INTEGER(BigInteger.valueOf((int)((Constant.Char)left).value * (int)((Constant.Char)right).value));
			}
			break;
		case DIV:			
			if(left instanceof Constant.Integer){
				return ((Constant.Integer)left).divide((Constant.Integer)right);
			}
			if (left instanceof Constant.Decimal){
				Constant.Decimal num = (Constant.Decimal)left;
				Constant.Decimal denum = (Constant.Decimal)right;	
				try{										
					//The divide method occurs Arithmetic Exceptions when the results of dividing two BigDecimal has no 
					//rounded resulting values.
					return num.divide(denum);
				}catch(ArithmeticException ex){
					//In the case of (1/3), the division result is infinite.
					return DecimalFraction.V_DecimalFraction(num, denum);					
				}
			}
			
			if(left instanceof DecimalFraction){				
				DecimalFraction left_frac = (DecimalFraction)left;
				DecimalFraction right_frac = DecimalFraction.V_DecimalFraction(((Constant.Decimal)right));
				
				Integer num = left_frac.getNumerator().multiply(right_frac.getDenominator());
				Integer denum = left_frac.getDenominator().multiply(right_frac.getNumerator());
				return  DecimalFraction.V_DecimalFraction(num, denum);				
			}
			
			break;
		case REM:
			if(left instanceof Constant.Integer){
				return ((Constant.Integer)left).remainder((Constant.Integer)right);
			}
			break;			
		case RANGE:
			//Create a List.
			ArrayList<Constant> values = new ArrayList<Constant>();
			//Iterates the numbers in the range.
			BigInteger start = ((Constant.Integer)left).value;
			BigInteger end = ((Constant.Integer)right).value;
			while(!start.equals(end)){
				values.add(Constant.V_INTEGER(start));
				start = start.add(BigInteger.ONE);		
			}
			//Put the list into the result.
			return Constant.V_LIST(values);
		case BITWISEAND:
			leftValue = ((Constant.Byte)left).value;
			rightValue = ((Constant.Byte)right).value;
			return Constant.V_BYTE((byte) (leftValue & rightValue));			
		case BITWISEOR:
			leftValue = ((Constant.Byte)left).value;
			rightValue = ((Constant.Byte)right).value;
			return Constant.V_BYTE((byte)(leftValue | rightValue));		
		case BITWISEXOR:
			leftValue = ((Constant.Byte)left).value;
			rightValue = ((Constant.Byte)right).value;
			return Constant.V_BYTE((byte)(leftValue ^ rightValue));			
		case LEFTSHIFT:
			leftValue = ((Constant.Byte)left).value;
			int pos = ((Constant.Integer)right).value.intValue();
			return Constant.V_BYTE((byte)(leftValue << pos));			
		case RIGHTSHIFT:
			leftValue = ((Constant.Byte)left).value;
			pos = ((Constant.Integer)right).value.intValue();
			//0x000000FF is added to avoid leftValue being casted to integer
			//before shifting and to fill the zeros to the left.
			return Constant.V_BYTE((byte)((0x000000FF & leftValue)>>pos));			
		default:
			break;			
		}		
		internalFailure("Not implemented!", "BinaryOperatorInterpreter.java", null);
		return null;
	}
	
	/**
	 * Executes code 
	 * @param code <code>Codes.BinaryOperator</code> code
	 * @param stackframe the activated stack frame
	 */
	public void interpret(Codes.BinaryOperator code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();		
		Constant result = performOperation(stackframe.getRegister(code.operand(0)),
										   stackframe.getRegister(code.operand(1)),
										   code);
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(), "%"+ code.target() + "("+result+")");
		stackframe.setLine(++linenumber);
	}

}
