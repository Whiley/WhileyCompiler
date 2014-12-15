package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
import wyopcl.util.Utility;
/**
 * Interprets <code>Codes.Assign</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Assign
 */
public class AssignInterpreter extends Interpreter {
	private static AssignInterpreter instance;	
	private AssignInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static AssignInterpreter getInstance(){
		if (instance == null){
			instance = new AssignInterpreter();
		}
		return instance;
	}

	public void interpret(Codes.Assign code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		Constant constant = stackframe.getRegister(code.operand(0));
		Constant result;
		if (code.type() instanceof Type.Reference){
			result = constant;
		}else {
			//Copy the value 
			result = Utility.copyConstant(constant);
		}		
		stackframe.setRegister(code.target(), result);		
		printMessage(stackframe, code.toString(), "%"+ code.target() + "("+result+")");
		stackframe.setLine(++linenumber);
	}

}
