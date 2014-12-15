package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Label</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Label
 *
 */
public class LabelInterpreter extends Interpreter {
	private static LabelInterpreter instance;	
	private LabelInterpreter(){	}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static LabelInterpreter getInstance(){
		if (instance == null){
			instance = new LabelInterpreter();
		}
		return instance;
	}
	
	public void interpret(Codes.Label code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		printMessage(stackframe, code.toString(), "("+code.label+")");
		stackframe.setLine(++linenumber);
	}

}
