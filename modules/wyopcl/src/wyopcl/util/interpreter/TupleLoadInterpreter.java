package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.TupleLoad</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.TupleLoad
 *
 */
public class TupleLoadInterpreter extends Interpreter{
	private static TupleLoadInterpreter instance;	
	private TupleLoadInterpreter(){	}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static TupleLoadInterpreter getInstance(){
		if (instance == null){
			instance = new TupleLoadInterpreter();
		}
		return instance;
	}

	public void interpret(Codes.TupleLoad code, StackFrame stackframe) {		
		int linenumber = stackframe.getLine();
		//Read the tuple from the operand register.
		Constant.Tuple tuple = (Constant.Tuple)stackframe.getRegister(code.operand(0));
		//Write the tuple
		Constant result = tuple.values.get(code.index);
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(), "%"+code.target()+"="+result);
		stackframe.setLine(++linenumber);
	}
	
}
