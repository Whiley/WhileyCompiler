package wyopcl.util.interpreter;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Return</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Return
 *
 */
public class ReturnInterpreter extends Interpreter {
	private static ReturnInterpreter instance;	
	private ReturnInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static ReturnInterpreter getInstance(){
		if (instance == null){
			instance = new ReturnInterpreter();
		}
		return instance;
	}


	public void interpret(Codes.Return code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		int return_reg = stackframe.getReturn_reg();
		blockstack.pop();
		//Get the previous block
		
		if(!blockstack.isEmpty()){
			StackFrame caller = blockstack.peek();				
			linenumber = caller.getLine();		
			//Check if the results are returned.
			if (code.type != Type.T_VOID) {
				//Read the values from the operand register.
				Constant return_value = stackframe.getRegister(code.operand);
				printMessage(stackframe, code.toString(),
						"%"+return_reg + "("+return_value+")");
				//Return the result by updating the register.
				caller.setRegister(return_reg, return_value);
			}else{			
				printMessage(caller, code.toString(), "");
			}
			caller.setLine(++linenumber);
		}else{
			//Do nothing since the code is completed.
		}
		
	}

}
