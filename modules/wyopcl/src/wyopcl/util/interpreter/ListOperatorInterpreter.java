package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;

import java.util.ArrayList;
import java.util.Iterator;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Constant.List;
import wyil.lang.Type;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.ListOperator</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.ListOperator
 *
 */
public class ListOperatorInterpreter extends Interpreter{
	private static ListOperatorInterpreter instance;	
	private ListOperatorInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static ListOperatorInterpreter getInstance(){
		if (instance == null){
			instance = new ListOperatorInterpreter();
		}
		return instance;
	}
	/**
	 * Recursively check if a Constant.List contains any element.
	 * @param list
	 * @param type
	 * @return false if the list contains elements. Return true if the list has no elements.
	 */
	private boolean checkEmpty(Constant.List list){
		for(Constant elem: list.values){				
			if(elem instanceof Constant.List){
				return checkEmpty((Constant.List)elem);
			}
		}
			
		if(!list.values.isEmpty()){
			return false;
		}
		
		return true;
	}
	
	

	public void interpret(Codes.ListOperator code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();		
		//Read the list from two operands.
		Constant.List left = (Constant.List) stackframe.getRegister(code.operand(0));
		Constant.List right = (Constant.List) stackframe.getRegister(code.operand(1));
		//Perform the list operation (e.g. append two lists)
		Constant.List result = null;
		switch(code.kind){
		case APPEND:
			//Create a new array list to append the left and right list.
			ArrayList<Constant> values = new ArrayList<Constant>();
			//Type elementType = code.type().element();
			//Check the left is empty. If so, then do not add to the new list.
			if(!checkEmpty(left)){
				values.addAll(left.values);
			}			
			values.addAll(right.values);			
			result = Constant.V_LIST(values);
			break;
		case LEFT_APPEND:
			left.values.addAll(right.values);
			result = Constant.V_LIST(left.values);
			break;
		case RIGHT_APPEND:
			right.values.addAll(left.values);
			result = Constant.V_LIST(right.values);
			break;
		default:
			internalFailure("Not implemented!", "ListOperatorInterpreter.java", null);
			break;
		}
		//Set the result to the target register.
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(), "%"+ code.target() + "("+result+")\n");
		stackframe.setLine(++linenumber);
	}
	
}
