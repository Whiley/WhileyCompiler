package wyopcl.util.interpreter;

import java.util.List;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.SubList</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.SubList
 *
 */
public class SubListInterpreter extends Interpreter{
	private static SubListInterpreter instance;	
	private SubListInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static SubListInterpreter getInstance(){
		if (instance == null){
			instance = new SubListInterpreter();
		}
		return instance;
	}
	
	public void interpret(Codes.SubList code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		
		//Get the list from the operand register (index=0).
		Constant.List list = (Constant.List)stackframe.getRegister(code.operand(0));
		//Get two index operands (index=1 or index=2).
		Constant.Integer fromIndex = (Constant.Integer)stackframe.getRegister(code.operand(1));
		Constant.Integer toIndex = (Constant.Integer)stackframe.getRegister(code.operand(2));
		//Computes the sublist from index1 to index2.
		List<Constant> sublist = list.values.subList(fromIndex.value.intValue(), toIndex.value.intValue());
		//Write the result to the target register.
		Constant result = Constant.V_LIST(sublist);
		stackframe.setRegister(code.target(), result);
		
		printMessage(stackframe, code.toString(),
				"%"+code.target() + "("+result+")");
		
		stackframe.setLine(++linenumber);
	}
	
}
