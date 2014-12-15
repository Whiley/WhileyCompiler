package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Dereference</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Dereference
 *
 */
public class DereferenceInterpreter extends Interpreter {
	private static DereferenceInterpreter instance;

	private DereferenceInterpreter() {	}

	/* Implement the Singleton pattern to ensure this class has one instance. */
	public static DereferenceInterpreter getInstance() {
		if (instance == null) {
			instance = new DereferenceInterpreter();
		}
		return instance;
	}
	
	public void interpret(Codes.Dereference code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		//Get the reference constant
		Constant reference = stackframe.getRegister(code.operand(0));
		//Deference it and set it to the target register.
		stackframe.setRegister(code.target(), reference);
		printMessage(stackframe, code.toString(),"%"+code.target()+"("+reference+")");
		stackframe.setLine(++linenumber);
	}
}
