package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Debug</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Debug
 *
 */
public class DebugInterpreter extends Interpreter{
	private static DebugInterpreter instance;	
	private DebugInterpreter(){	}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static DebugInterpreter getInstance(){
		if (instance == null){
			instance = new DebugInterpreter();
		}
		return instance;
	}	
	
	public void interpret(Codes.Debug code, StackFrame stackframe) {	
		int linenumber = stackframe.getLine();
		//Read a string from operand register.
		Constant.Strung str = (Constant.Strung)stackframe.getRegister(code.operand);
		//Replace the double quotes.
		System.out.print(str.value.replaceAll("\"", ""));
		printMessage(stackframe, code.toString(),"");
		stackframe.setLine(++linenumber);
	}
	
}
