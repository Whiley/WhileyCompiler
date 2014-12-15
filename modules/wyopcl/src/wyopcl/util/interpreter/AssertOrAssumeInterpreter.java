package wyopcl.util.interpreter;


import wyil.lang.Codes;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.AssertOrAssume</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.AssertOrAssume
 */
public class AssertOrAssumeInterpreter extends Interpreter {
	private static AssertOrAssumeInterpreter instance;	
	private AssertOrAssumeInterpreter(){
	}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static AssertOrAssumeInterpreter getInstance(){	
		if (instance == null){
			instance = new AssertOrAssumeInterpreter();
		}	
		return instance;
	}	
	
	public void interpret(Codes.AssertOrAssume code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();		
		printMessage(stackframe, code.toString(), "");
		stackframe.setLine(++linenumber);
	}
	
	
	
}
