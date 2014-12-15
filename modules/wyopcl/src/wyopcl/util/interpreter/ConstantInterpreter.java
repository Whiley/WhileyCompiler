package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Const</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Const
 *
 */
public class ConstantInterpreter extends Interpreter {
	private static ConstantInterpreter instance;
	
	private ConstantInterpreter(){	}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static ConstantInterpreter getInstance(){
		if (instance == null){
			instance = new ConstantInterpreter();
		}
		return instance;
	}

	/**
	 * Gets the constant from bytecode and puts it into the register table at
	 * the specific position.
	 * @param code
	 * @param stackframe
	 */
	public void interpret(Codes.Const code, StackFrame stackframe) {		
		// Add the register
		int linenumber = stackframe.getLine();
		Constant result = code.constant;		
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(), "%"+ code.target() + "("+result+")");
		//Set the next line number
		stackframe.setLine(++linenumber);
	}

}
