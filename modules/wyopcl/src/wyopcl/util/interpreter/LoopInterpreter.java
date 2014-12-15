package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Loop</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Loop
 *
 */
public class LoopInterpreter extends Interpreter {
	private static LoopInterpreter instance;	
	private LoopInterpreter(){}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static LoopInterpreter getInstance(){
		if (instance == null){
			instance = new LoopInterpreter();
		}
		return instance;
	}
	
	/**
	 * Prints out the bytecode without any further action.
	 * @param code
	 * @param stackframe
	 */
	public void interpret(Codes.Loop code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		printMessage(stackframe, code.toString(), "("+code.target+")");
		stackframe.setLine(++linenumber);
	}

}
