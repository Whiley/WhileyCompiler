package wyopcl.util.interpreter;

import java.util.Iterator;
import java.util.Map.Entry;

import wycc.lang.NameID;
import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
import wyopcl.util.Utility;
/**
 * Interprets <code>Codes.Record</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Record
 *
 */
public class FieldLoadInterpreter extends Interpreter {
	private static FieldLoadInterpreter instance;	
	private FieldLoadInterpreter(){
	}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static FieldLoadInterpreter getInstance(){
		if (instance == null){
			instance = new FieldLoadInterpreter();
		}
		return instance;
	}
	
	
	private Constant getFieldValue(Constant.Record record, String givenfield){		
		//Reads a record value from an operand register	
		Iterator<Entry<String, Constant>> values = record.values.entrySet().iterator();
		while(values.hasNext()){
			Entry<String, Constant> value = values.next();
			Constant fieldValue = value.getValue();
			if(value.getKey().equals(givenfield)){				
				return Utility.copyConstant(fieldValue);
			}
		}		
		return Constant.V_NULL;
	}
	
	/**
	 * Loads the values of the given field from a composite data.
	 * @param code
	 * @param stackframe
	 */
	public void interpret(Codes.FieldLoad code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		String givenfield = code.field;
		Constant constant = stackframe.getRegister(code.operand(0));
		Constant result = null;
		if (constant instanceof Constant.Type){
			//Temporarily solve the method problem for println....
			NameID name = new NameID(((WyilFile) config.getProperty("module")).id(), givenfield);
			result = Constant.V_LAMBDA(name, (Type.Method)code.fieldType());			
		}else if (constant instanceof Constant.Record){
			Constant.Record record = (Constant.Record)constant;
			result = getFieldValue(record, givenfield);
		}else{
			result = constant;
			//internalFailure("Not implemented!", "FieldLoadInterpreter.java", null);
		}

		stackframe.setRegister(code.target(), result);	
		printMessage(stackframe, code.toString(), "%"+ code.target() + "("+result+")");
		stackframe.setLine(++linenumber);
	}

}
