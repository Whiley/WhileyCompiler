package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.TryCatch</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.TryCatch
 *
 */
public class TryCatchInterpreter extends Interpreter{
	private static TryCatchInterpreter instance;	
	public TryCatchInterpreter(){		
	}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static TryCatchInterpreter getInstance(){
		if (instance == null){
			instance = new TryCatchInterpreter();
		}
		return instance;
	}

	public void interpret(Codes.TryCatch code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		printMessage(stackframe, code.toString(),"");
		stackframe.setLine(++linenumber);
		
	}
	
}
