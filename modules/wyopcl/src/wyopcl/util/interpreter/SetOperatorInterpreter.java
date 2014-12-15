package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;

import java.util.Collection;
import java.util.LinkedHashSet;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.SetOperator</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.SetOperator
 *
 */
public class SetOperatorInterpreter extends Interpreter{
	private static SetOperatorInterpreter instance;	
	private SetOperatorInterpreter(){}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static SetOperatorInterpreter getInstance(){
		if (instance == null){
			instance = new SetOperatorInterpreter();
		}
		return instance;
	}
	

	public void interpret(Codes.SetOperator code, StackFrame stackframe) {		
		int linenumber = stackframe.getLine();
		
		//Read two set values
		Constant left = stackframe.getRegister(code.operand(0));
		Constant right = stackframe.getRegister(code.operand(1));
		
		//Perform the operation
		Constant result = null;
		Constant.Set lhs = null, rhs=null;
		switch(code.kind){
		case UNION:
			lhs = (Constant.Set)left;
			rhs = (Constant.Set)right;
			result = lhs.union(rhs);
			break;
		case LEFT_UNION:
			lhs = (Constant.Set)left;
			Collection<Constant> list = new LinkedHashSet<Constant>();
			list.add(right);
			result = lhs.union(Constant.V_SET(list));
			break;
		case RIGHT_UNION:
			internalFailure("Not implemented!", "InterpreterSetOperator.java", null);
			break;
		case INTERSECTION:
			lhs = (Constant.Set)left;
			rhs = (Constant.Set)right;
			result = lhs.intersect(rhs);
			break;
		case DIFFERENCE:
			lhs = (Constant.Set)left;
			rhs = (Constant.Set)right;
			result = lhs.difference(rhs);
			break;
		default:
			internalFailure("Not implemented!", "InterpreterSetOperator.java", null);
			break;
		}
		//Write the result to target register.
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(),"%"+code.target()+"("+result+")");
		stackframe.setLine(++linenumber);
	}
	
}
