package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wycc.lang.NameID;
import wyil.lang.Code.Block;
import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyjc.runtime.WyList;
import wyjc.runtime.WyRat;
import wyjc.runtime.WyRecord;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
import wyopcl.util.Utility;
/**
 * Interprets <code>Codes.Invoke</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Invoke
 *
 */
public class InvokeInterpreter extends Interpreter {

	private static InvokeInterpreter instance;	
	private InvokeInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static InvokeInterpreter getInstance(){
		if (instance == null){
			instance = new InvokeInterpreter();
		}
		return instance;
	}

	/***
	 * Convert the Java object to the object of given Constant type.
	 * @param obj an Java object
	 * @param toType the given Constant type
	 * @return the Constant object of the specified type. 
	 */
	private Constant convertJavaObjectToConstant(NameID nameID, Object obj, wyil.lang.Type toType) {
		if (toType instanceof Type.Strung) {
			if (obj instanceof BigDecimal) {
				return Constant.V_STRING(((BigDecimal) obj).toPlainString());
			}
			//trim the beginning and ending quotes.
			return Constant.V_STRING(obj.toString().replaceAll("^\"|\"$", ""));
		} 

		if (toType instanceof Type.Int) {			
			if(obj instanceof WyRat){
				return Constant.V_INTEGER(((WyRat)obj).numerator());
			}			
			return Constant.V_INTEGER((BigInteger) obj);
		} 

		if (toType instanceof Type.Bool){
			return Constant.V_BOOL((Boolean)obj);
		}

		if (toType instanceof Type.Byte) {			
			if(obj instanceof WyList){
				return Constant.V_BYTE(new Byte((Byte) ((WyList) obj).get(0)));
			}			
			return Constant.V_BYTE(new Byte((Byte) obj));
		}

		if (toType instanceof Type.List){
			Collection<Constant> values = new ArrayList<Constant>();
			WyList wylist = (WyList)obj;
			for(Object wyObj : wylist){
				Type elemType = ((Type.List)toType).element();
				values.add(convertJavaObjectToConstant(nameID, wyObj, elemType));
			}
			return Constant.V_LIST(values);
		}
		
		if(toType instanceof Type.Record){
			Type.Record recordType = (Type.Record)toType;
			WyRecord wyrecord = (WyRecord)obj;			
			Map<String, Constant> values = new HashMap<String, Constant>();
			for(String field: wyrecord.keySet()){
				Type fieldType = recordType.field(field);
				Object value = wyrecord.get(field);
				values.put(field, convertJavaObjectToConstant(nameID, value, fieldType));
			}			
			return Constant.V_RECORD(values);
		}
		
		//Convert a Java obj to a constant method.
		//But Constant class does not have the constructor for method.
		//This method is not yet complete and essential for executing 001_avg benchmark program.
		if(toType instanceof Type.Method){
			Type.Method methodType = (Type.Method)toType;			
			return Constant.V_LAMBDA(nameID, methodType);			
		}		
		
		internalFailure("Not implemented!", "InvokeInterpreter.java", null);
		return null;

	}


	/**
	 * Directly invoke the function/method from Whiley runtime library
	 * @param code
	 * @param stackframe
	 */
	private void invokeRuntimeFunction(Codes.Invoke code, StackFrame stackframe, List<Constant> params){
		int linenumber = stackframe.getLine();		
				
		
		//Directly invoke the function/method.
		try {			
			//Load the Class
			Object obj = Utility.invokeWhileyRuntimeFucntion(this.getClass().getClassLoader(), 
															 code.name.module().toString().replace('/', '.'),
															 code.name.name(),
															 code.type().params(),
															 params);
			// returned_obj into Constant.					
			Constant result = convertJavaObjectToConstant(code.name, obj, code.assignedType());
			stackframe.setRegister(code.target(), result);
			printMessage(stackframe, code.toString(),"%"+code.target()+"("+result+")");
			stackframe.setLine(++linenumber);
		} catch (Exception e) {				
			//Pop up the current block
			if(blockstack.size() > 1){
				blockstack.pop();
			}
			//Return to the caller
			StackFrame caller = blockstack.peek();
			linenumber = symboltable.getCatchPos(caller.getBlock());
			caller.setLine(linenumber);
		}
		
		
	}



	/**
	 * Invokes either the function or the Whiley runtime function.
	 * @param code
	 * @param stackframe
	 */
	public void interpret(Codes.Invoke code, StackFrame stackframe) {
		
		//Find the right block
		Block blk = Interpreter.getFuncBlockByName(code.name.toString(), code.type());
		// Create a list of parameters.
		List<Constant> params = new ArrayList<Constant>();				
		for (int operand: code.operands()) {
			params.add(stackframe.getRegister(operand));
		}
		
		if (blk != null){			
			StackFrame newStackFrame = Utility.invokeFunction(blk,
															  stackframe.getDepth(),
															  code.name.name(),
															  params,
															  code.target());
			//Push the function block to the stack		
			blockstack.push(newStackFrame);
			printMessage(stackframe, code.toString(),"");
		}else{
			invokeRuntimeFunction(code, stackframe, params);
		}
		
		params = null;
	}

}
