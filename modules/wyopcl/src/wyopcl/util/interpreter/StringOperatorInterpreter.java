package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;
import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.StringOperator</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.StringOperator
 *
 */
public class StringOperatorInterpreter extends Interpreter {
	private static StringOperatorInterpreter instance;	
	private StringOperatorInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static StringOperatorInterpreter getInstance(){
		if (instance == null){
			instance = new StringOperatorInterpreter();
		}
		return instance;
	}
	/**
	 * Appends a given char or string to the existing string.
	 * 
	 * @param op the type of appending
	 * @param left the existing string
	 * @param right the given char or string
	 * @return the padded string
	 */
	private Constant.Strung performStringOperation(Codes.StringOperatorKind op, Constant.Strung left, Constant right){
		//Check the operator
		switch (op){
		case APPEND:
			//Check the type of right operand before appending those two operands.
			if (right instanceof Constant.Strung){
				return Constant.V_STRING(left.value + ((Constant.Strung)right).value);			
			}
			break;
		case LEFT_APPEND:
			if(right instanceof Constant.Char){
				return Constant.V_STRING(left.value + ((Constant.Char)right).value);
			}
			break;
		case RIGHT_APPEND:
			if(right instanceof Constant.Char){
				return Constant.V_STRING(((Constant.Char)right).value + left.value);
			}
		default:
			break;
		}
		internalFailure("Not implemented!", "InterpreterStringOperator.java", null);
		return null;
	}

	/**
	 * Appends a string or char to the existing string.
	 * @param code
	 * @param stackframe
	 */
	public void interpret(Codes.StringOperator code, StackFrame stackframe) {		
		int linenumber = stackframe.getLine();
		//Read two string from the operands
		Constant.Strung result =
				performStringOperation(code.kind,
				 					   (Constant.Strung)stackframe.getRegister(code.operand(0)),
									   stackframe.getRegister(code.operand(1)));
		//Write the result to the target register.
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(),"%"+code.target()+"("+result+")");
		stackframe.setLine(++linenumber);
	}





}
