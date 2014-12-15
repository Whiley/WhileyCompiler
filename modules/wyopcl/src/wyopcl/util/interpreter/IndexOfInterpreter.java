package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;

import java.util.Iterator;
import java.util.Map.Entry;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.IndexOf</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.IndexOf
 *
 */
public class IndexOfInterpreter extends Interpreter {
	private static IndexOfInterpreter instance;	
	private IndexOfInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static IndexOfInterpreter getInstance(){
		if (instance == null){
			instance = new IndexOfInterpreter();
		}
		return instance;
	}

	private Constant getValuefromList(Constant.List list, Constant key){
		Constant value = null;
		//Get the value associated with the key.
		if(key instanceof Constant.Integer){
			value = list.values.get(((Constant.Integer)key).value.intValue());
		}else if (key instanceof Constant.Char){
			value = list.values.get(((Constant.Char)key).value);
		}else{
			internalFailure("Not implemented!", "InterpreterIndexOf.java", null);
		}

		return value;
	}

	private Constant getValuefromRecord(Constant.Record record, Constant key){
		Constant value = null;
		//Get the value associated with the key.
		if(key instanceof Constant.Integer){
			value = record.values.get(((Constant.Integer)key).value.intValue());
		}else if (key instanceof Constant.Char){
			value = record.values.get(((Constant.Char)key).value);
		}else{
			internalFailure("Not implemented!", "InterpreterIndexOf.java", null);
		}

		return value;
	}
	/**
	 * Gets the char of a string at the given index
	 * @param strung the string
	 * @param index the index
	 * @return the char
	 */
	private Constant getValuefromStrung(Constant.Strung strung, Constant index){
		//Get the value associated with the key.
		if(index instanceof Constant.Integer){
			//Get the char at 'keyvalue' index and returns the Constant.Char object.
			char c = strung.value.charAt(((Constant.Integer)index).value.intValue());
			return Constant.V_CHAR(c);
		}
		internalFailure("Not implemented!", "InterpreterIndexOf.java", null);
		return null;
	}

	private Constant getValuefromMap(Constant.Map map, Constant key){		
		//Get the value associated with the key.
		if(map.values.containsKey(key)){
			return map.values.get(key);
		}

		//Comparing two Constatn.Decimal requires using compareTo method.
		Iterator<Entry<Constant, Constant>> iterator = map.values.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<Constant, Constant> entry = iterator.next();
			Constant entryKey = entry.getKey();
			if(entryKey.compareTo(key)==0){
				return entry.getValue();
			}			
		}


		internalFailure("Not implemented!", "InterpreterIndexOf.java", null);
		return null;
	}
	/**
	 * Gets the ith element from the composite set (i.e. List, Record,
	 * Strung, Map)
	 * @param code
	 * @param stackframe
	 */
	public void interpret(Codes.IndexOf code, StackFrame stackframe) {		
		int linenumber = stackframe.getLine();
		//Read the list from the leftOperand register.
		Constant constant = stackframe.getRegister(code.operand(0));
		Constant key = stackframe.getRegister(code.operand(1));
		Constant value = null;
		if(constant instanceof Constant.List){
			value = getValuefromList((Constant.List)constant, key);
		}else if(constant instanceof Constant.Record){
			value = getValuefromRecord((Constant.Record)constant, key);
		}else if(constant instanceof Constant.Strung){
			value = getValuefromStrung((Constant.Strung)constant, key);
		}else if (constant instanceof Constant.Map){
			value = getValuefromMap((Constant.Map) constant, key);
		}else{
			internalFailure("Not implemented!", "InterpreterIndexOf.java", null);
		}
		stackframe.setRegister(code.target(), value);			
		printMessage(stackframe, code.toString(), "%"+ code.target() + "("+value+")");
		stackframe.setLine(++linenumber);
	}

}
