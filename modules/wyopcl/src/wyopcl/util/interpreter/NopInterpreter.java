package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Nop</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Nop
 *
 */
public class NopInterpreter extends Interpreter {
	private static NopInterpreter instance;	
	private NopInterpreter(){}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static NopInterpreter getInstance(){
		if (instance == null){
			instance = new NopInterpreter();
		}
		return instance;
	}
	

	public void interpret(Codes.Nop code, StackFrame stackframe) {		
		int linenumber = stackframe.getLine();
		printMessage(stackframe, code.toString(),"");
		stackframe.setLine(++linenumber);
	}
}
