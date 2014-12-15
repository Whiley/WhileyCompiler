package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.NewObject</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.NewObject
 *
 */
public class NewObjectInterpreter extends Interpreter {
	private static NewObjectInterpreter instance;	
	private NewObjectInterpreter(){	}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static NewObjectInterpreter getInstance(){
		if (instance == null){
			instance = new NewObjectInterpreter();
		}
		return instance;
	}
	
	
	public void interpret(Codes.NewObject code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		//Reference type = code.type();
		//Get the value from the operand register.
		Constant value = stackframe.getRegister(code.operand(0));
		//Create a reference to the value.
		Constant result = value;
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(),"%"+code.target()+"("+result+")");
		stackframe.setLine(++linenumber);
	}
}
