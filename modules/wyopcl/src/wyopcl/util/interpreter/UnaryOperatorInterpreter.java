package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;
import wyil.lang.Codes;
import wyil.lang.Codes.UnaryOperatorKind;
import wyil.lang.Constant;
import wyopcl.util.DecimalFraction;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;

/**
 * Interprets <code>Codes.UnaryOperator</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.UnaryOperator
 *
 */
public class UnaryOperatorInterpreter extends Interpreter {
	private static UnaryOperatorInterpreter instance;	
	private UnaryOperatorInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static UnaryOperatorInterpreter getInstance(){
		if (instance == null){
			instance = new UnaryOperatorInterpreter();
		}
		return instance;
	}
	
	private Constant performArithmetic(UnaryOperatorKind kind, Constant number){
		if(number instanceof Constant.Integer){
			Constant.Integer integer = (Constant.Integer)number;
			switch(kind){
			case NEG:
				return Constant.V_INTEGER(integer.value.negate());
			case NUMERATOR:
				internalFailure("Not implemented!", "UnaryOperatorInterpreter.java", null);
				return null;
			case DENOMINATOR:
				internalFailure("Not implemented!", "UnaryOperatorInterpreter.java", null);
				return null;
			default:
				internalFailure("Not implemented!", "UnaryOperatorInterpreter.java", null);
				return null;
			}
		}else if(number instanceof Constant.Decimal){
			Constant.Decimal decimal = (Constant.Decimal)number;
			switch(kind){
			case NEG:
				return Constant.V_DECIMAL(decimal.value.negate());
			case NUMERATOR:
				return DecimalFraction.V_DecimalFraction(decimal).getNumerator();						
			case DENOMINATOR:
				return DecimalFraction.V_DecimalFraction(decimal).getDenominator();						
			default:
				internalFailure("Not implemented!", "UnaryOperatorInterpreter.java", null);
				return null;
			}
			
		}else{
			internalFailure("Not implemented!", "UnaryOperatorInterpreter.java", null);
			return null;
		}		
	}	
	
	public void interpret(Codes.UnaryOperator code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		Constant result = null;
		Constant number = stackframe.getRegister(code.operand(0));
		result = performArithmetic(code.kind, number);		
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(), "%"+code.target() + "("+result+")");
		stackframe.setLine(++linenumber);
		
		
	}
	
}
