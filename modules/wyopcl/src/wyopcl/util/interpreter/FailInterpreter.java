package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Fail</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Fail
 */
public class FailInterpreter extends Interpreter {
	private static FailInterpreter instance;	
	private FailInterpreter(){	}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static FailInterpreter getInstance(){
		if (instance == null){
			instance = new FailInterpreter();
		}
		return instance;
	}
	
	public void interpret(Codes.Fail code, StackFrame stackframe) {
		//Throw out an assertion error.
		throw new RuntimeException(code.message.value);
		//internalFailure(code.message, "FailInterpreter.java", null);
		
	}
	
	
}
