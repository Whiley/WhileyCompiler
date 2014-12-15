package wyopcl.util.interpreter;

import java.util.HashMap;
import java.util.Map;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.NewMap</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.NewMap
 *
 */
public class NewMapInterpreter extends Interpreter {
	private static NewMapInterpreter instance;	
	private NewMapInterpreter(){}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static NewMapInterpreter getInstance(){
		if (instance == null){
			instance = new NewMapInterpreter();
		}
		return instance;
	}
	
	/**
	 * Creates a new Constant Map using the operands from <code>Codes.NewMap</code> code.
	 * @param code the <code>Codes.NewMap</code>
	 * @param stackframe the activated stack frame.
	 */
	public void interpret(Codes.NewMap code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		//Create a Hashmap with the key and values from operand registers.
		Map<Constant, Constant> values = new HashMap<Constant, Constant>();
		for(int i = 0; i< code.operands().length; i+=2){
			Constant key = stackframe.getRegister(code.operand(i));
			Constant value = stackframe.getRegister(code.operand(i+1));
			values.put(key, value);
		}		
		Constant.Map result = Constant.V_MAP(values);
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(), "%"+ code.target() + "("+result+")");	
		stackframe.setLine(++linenumber);
		
	}
	
	
	
}
