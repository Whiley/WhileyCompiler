package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;
import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Invert</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Invert
 */
public class InvertInterpreter extends Interpreter {
	private static InvertInterpreter instance;

	private InvertInterpreter() {}

	/* Implement the Singleton pattern to ensure this class has one instance. */
	public static InvertInterpreter getInstance() {
		if (instance == null) {
			instance = new InvertInterpreter();
		}
		return instance;
	}
	
	public void interpret(Codes.Invert code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();		
		Constant constant = stackframe.getRegister(code.operand(0));
		Constant result = null;
		if(constant instanceof Constant.Byte){
			//perform bitwise not operation.
			byte inv =(byte) ~((Constant.Byte)constant).value;
			result = Constant.V_BYTE(inv);
			
		}else{
			internalFailure("Not implemented!", "InvertInterpreter.java", null);
			return;
		}
		stackframe.setRegister(code.target(), result);;
		
		printMessage(stackframe, code.toString(), "%"+code.target()+"("+result+")");
		stackframe.setLine(++linenumber);
	}
	
	
}
