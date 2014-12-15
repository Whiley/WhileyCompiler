package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;

import java.math.BigInteger;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.LengthOf</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.LengthOf
 *
 */
public class LengthOfInterpreter extends Interpreter{
	private static LengthOfInterpreter instance;	
	private LengthOfInterpreter(){}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static LengthOfInterpreter getInstance(){
		if (instance == null){
			instance = new LengthOfInterpreter();
		}
		return instance;
	}

	
	public void interpret(Codes.LengthOf code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		
		//Read a effective collection (list, set or map) from the operand register.
		Constant collection = stackframe.getRegister(code.operand(0));
		int length = 0;
		Constant result = null;
		if(collection instanceof Constant.List){
			//Cast the collection to a List.
			length = ((Constant.List)collection).values.size();
			result =  Constant.V_INTEGER(BigInteger.valueOf(length));
		}else if (collection instanceof Constant.Record){
			length = ((Constant.Record)collection).values.size();
			result =  Constant.V_INTEGER(BigInteger.valueOf(length));
		}else if (collection instanceof Constant.Strung){
			length = ((Constant.Strung)collection).value.length();
			result =  Constant.V_INTEGER(BigInteger.valueOf(length));
		}else if(collection instanceof Constant.Set){
			length = ((Constant.Set)collection).values.size();
			result =  Constant.V_INTEGER(BigInteger.valueOf(length));
		}else if(collection instanceof Constant.Map){
			length = ((Constant.Map)collection).values.size();
			result =  Constant.V_INTEGER(BigInteger.valueOf(length));
		}else if (collection instanceof Constant.Null){
			result = collection;
		} else{
			internalFailure("Not implemented!", "InterpreterLengthOf.java", null);
		}
		
		//Write the length to register.
		//Constant result =  Constant.V_INTEGER(BigInteger.valueOf(length));
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(), "%"+ code.target() + "("+result+")");
		stackframe.setLine(++linenumber);
	}
}
