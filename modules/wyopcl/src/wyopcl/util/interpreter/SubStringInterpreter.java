package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.SubString</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.SubString
 *
 */
public class SubStringInterpreter extends Interpreter {
	private static SubStringInterpreter instance;	
	private SubStringInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static SubStringInterpreter getInstance(){
		if (instance == null){
			instance = new SubStringInterpreter();
		}
		return instance;
	}
	
	public void interpret(Codes.SubString code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		Constant result = null;		
		Constant.Strung source = (Constant.Strung)stackframe.getRegister(code.operand(0));
		Constant.Integer beginIndex = (Constant.Integer)stackframe.getRegister(code.operand(1));
		Constant.Integer endIndex = (Constant.Integer)stackframe.getRegister(code.operand(2));
		
		String substring = source.value.substring(beginIndex.value.intValue(), endIndex.value.intValue());
		result = Constant.V_STRING(substring);
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(), "%"+code.target() + "("+result+")");
		stackframe.setLine(++linenumber);		
	}
}
