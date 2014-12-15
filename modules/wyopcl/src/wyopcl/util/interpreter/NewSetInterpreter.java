package wyopcl.util.interpreter;

import java.util.LinkedHashSet;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.NewSet</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.NewSet
 *
 */
public class NewSetInterpreter extends Interpreter {
	private static NewSetInterpreter instance;	
	private NewSetInterpreter(){ }
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static NewSetInterpreter getInstance(){
		if (instance == null){
			instance = new NewSetInterpreter();
		}
		return instance;
	}
	
	
	public void interpret(Codes.NewSet code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		LinkedHashSet<Constant> values = new LinkedHashSet<Constant>();
		
		for(int operand:code.operands()){
			Constant value = stackframe.getRegister(operand);
			values.add(value);
		}
		//The ordering is not preserved.
		Constant.Set result = Constant.V_SET(values);
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(),"%"+code.target()+"("+result+")");
		stackframe.setLine(++linenumber);
	}
	
	
}
