package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Throw</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Throw
 *
 */
public class ThrowInterpreter extends Interpreter {
	private static ThrowInterpreter instance;	
	private ThrowInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static ThrowInterpreter getInstance(){
		if (instance == null){
			instance = new ThrowInterpreter();
		}
		return instance;
	}
	
	public void interpret(Codes.Throw code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		Constant result = stackframe.getRegister(code.operand);
		//Pop up the current block
		if(blockstack.size() > 1){
			blockstack.pop();
		}
		//Return to the caller
		StackFrame caller = blockstack.peek();
		//Put the result to the caller's register table at index of 1.
		caller.setRegister(1, result);
		linenumber = symboltable.getCatchPos(caller.getBlock());
		caller.setLine(linenumber);
		printMessage(stackframe, code.toString(),"%"+code.operand+"="+result);	
	}
	
	
}
