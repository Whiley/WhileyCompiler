package wyopcl.util.interpreter;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.NewRecord</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.NewRecord
 *
 */
public class NewRecordInterpreter extends Interpreter{

	private static NewRecordInterpreter instance;	
	private NewRecordInterpreter(){	}
	
	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static NewRecordInterpreter getInstance(){
		if (instance == null){
			instance = new NewRecordInterpreter();
		}
		return instance;
	}
	
	public void interpret(Codes.NewRecord code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		//Use the LinkedHashMap to create the records.
		Map<String, Constant> values = new TreeMap<String, Constant>();
		//Get the field names and types.		
		Map<String, Type> fields = new TreeMap<String, Type>(code.type().fields());
		Iterator<String> iterator = fields.keySet().iterator();
		int index = 0;
		//Assign the field value in accordance with the sequence of field names.
		while(iterator.hasNext()){
			String key = iterator.next();			
			Constant value = stackframe.getRegister(code.operand(index));
			values.put(key, value);
			index++;
		}		
		//Construct the Record value
		Constant.Record result = Constant.V_RECORD(values);
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(),"%"+code.target()+"("+result+")");
		stackframe.setLine(++linenumber);
	}
	
}
