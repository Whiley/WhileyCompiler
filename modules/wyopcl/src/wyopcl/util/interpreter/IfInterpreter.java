package wyopcl.util.interpreter;

import java.util.Iterator;

import wyil.lang.Codes;
import wyil.lang.Code.Block;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.If</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.If
 *
 */
public class IfInterpreter extends Interpreter {
	private static IfInterpreter instance;	
	private IfInterpreter(){}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static IfInterpreter getInstance(){
		if (instance == null){
			instance = new IfInterpreter();
		}
		return instance;
	}
	
	
	private boolean compare(Codes.Comparator op, Constant left, Constant right){
		Constant.Set leftSet, rightSet;
		
		boolean satisfiable = false;
		switch (op) {
		case EQ:			
			if (left.compareTo(right) == 0){
				satisfiable = true;		
			}
			break;
		case NEQ:				
			if (left.compareTo(right) != 0) {				
				satisfiable = true;
			}
			break;
		case LT:			
			if (left.compareTo(right) < 0) {				
				satisfiable = true;
			}
			break;
		case LTEQ:			
			if (left.compareTo(right) <= 0) {				
				satisfiable = true;
			}
			break;
		case GT:					
			if (left.compareTo(right) > 0) {				
				satisfiable = true;
			}
			break;
		case GTEQ:			
			if (left.compareTo(right) >= 0) {				
				satisfiable = true;				
			}
			break;
		case IN:			
			if(right instanceof Constant.Set){
				rightSet = (Constant.Set)right;
				if(rightSet.values.contains(left)){
					satisfiable = true;
				}
			}else if (right instanceof Constant.List){
				Constant.List list = (Constant.List)right;
				if(list.values.contains(left)){
					satisfiable = true;
				}
			}			
			break;
		case SUBSET:
			leftSet = (Constant.Set)left;
			rightSet = (Constant.Set)right;
			//Check if two sets are identical.
			if(leftSet.equals(rightSet)){
				return false;
			}
			//Check if every element in leftSet is also an element in rightSet
			Iterator<Constant> iterator = leftSet.values.iterator();
			while(iterator.hasNext()){
				Constant constant = iterator.next();
				if(!rightSet.values.contains(constant)){
					return false;
				}
			}			
			return true;
		case SUBSETEQ:
			leftSet = (Constant.Set)left;
			rightSet = (Constant.Set)right;
			satisfiable = rightSet.values.containsAll(leftSet.values);
			break;
		default:			
			satisfiable = true;
			throw new RuntimeException("unknown comparator encountered:"+op);
		}
		
		return satisfiable;
	}	

	public void interpret(Codes.If code, StackFrame stackframe) {				
		int linenumber = stackframe.getLine();
		Constant left = stackframe.getRegister(code.leftOperand);
		Constant right = stackframe.getRegister(code.rightOperand);
		boolean satisfiable = false;
		satisfiable = compare(code.op, left, right);
		
		if(satisfiable){
			//Go to the if branch
			Block block = stackframe.getBlock();
			linenumber = symboltable.getBlockPosByLabel(block, code.target);
		}else{
			linenumber++;
		}
		printMessage(stackframe, code.toString(), "(" +satisfiable+")");
		stackframe.setLine(linenumber);

	}

}
