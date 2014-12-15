package wyopcl.util.interpreter;

import java.util.ArrayList;
import java.util.Collection;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Constant.Tuple;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.NewTuple</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.NewTuple
 *
 */
public class NewTupleInterpreter extends Interpreter{
	private static NewTupleInterpreter instance;	
	private NewTupleInterpreter(){}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static NewTupleInterpreter getInstance(){
		if (instance == null){
			instance = new NewTupleInterpreter();
		}
		return instance;
	}
	
	public void interpret(Codes.NewTuple code, StackFrame stackframe) {		
		int linenumber = stackframe.getLine();
		//Construct a new tuple from one or more operand registers.
		Collection<Constant> values = new ArrayList<Constant>();
		for(int oprand : code.operands()){
			values.add(stackframe.getRegister(oprand));
		}
		//Write the tuple to the target register.
		Tuple result = Constant.V_TUPLE(values);
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(),"%"+code.target()+"="+result);
		stackframe.setLine(++linenumber);
	}
	
}
