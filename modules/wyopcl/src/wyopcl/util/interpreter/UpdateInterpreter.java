package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
import wyopcl.util.Utility;
/**
 * Interprets <code>Codes.Update</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Update
 *
 */
public class UpdateInterpreter extends Interpreter {
	private static UpdateInterpreter instance;

	private UpdateInterpreter() {}

	/* Implement the Singleton pattern to ensure this class has one instance. */
	public static UpdateInterpreter getInstance() {
		if (instance == null) {
			instance = new UpdateInterpreter();
		}
		return instance;
	}

	private Constant.List update(Constant.List list, Constant givenValue, int updateIndex) {
		// Deeply copy and clone the values in the original Constant.List.		
		ArrayList<Constant> values = new ArrayList<Constant>(list.values.size());
		Iterator<Constant> iterator = list.values.iterator();
		while(iterator.hasNext()){
			Constant constant = iterator.next();
			values.add(Utility.copyConstant(constant));
		}

		if (values.size() <=updateIndex) {
			values.add(givenValue);
		} else {			
			values.set(updateIndex, givenValue);
		}

		return Constant.V_LIST(values);

	}

	private Constant.Record update(Constant.Record record, Constant givenValue, String field){
		HashMap<String, Constant> values = new HashMap<String, Constant>(record.values);
		values.put(field, givenValue);		
		return Constant.V_RECORD(values);
	}

	private Constant.Map update(Constant.Map map, Constant key, Constant Value){
		HashMap<Constant, Constant> values = new HashMap<Constant, Constant>(map.values);
		values.put(key, Value);
		return Constant.V_MAP(values);
	}
	/**
	 * Gets the given value and updates the list with given value at the given position.
	 * If the element is a list or record, then update its sublist w  
	 * 
	 * @param code
	 * @param stackframe
	 * @param list the list
	 * @param givenValue the given value
	 * @return the updated list
	 */
	private Constant.List updateList(StackFrame stackframe, Constant.List list, Constant givenValue, int[] keys, String... field) {
		// Get the index
		int index = ((Constant.Integer) stackframe.getRegister(keys[0])).value.intValue();
		Constant element = list.values.get(index);
		//Check if the element is a compound data set.
		if (element instanceof Constant.List) {
			if(keys.length==2){
				//Get the subIndex
				int subindex = ((Constant.Integer) stackframe.getRegister(keys[1])).value.intValue();
				givenValue = update((Constant.List)element, givenValue, subindex);
			}									
		}

		if(element instanceof Constant.Record){			
			//Update the record with given value at the given field.
			givenValue = update((Constant.Record)element, givenValue, field[0]);			
		} 

		//Update the list with the given value at the index position.
		return update(list, givenValue, index);
	}		
	/**
	 * Gets 
	 * @param code
	 * @param stackframe
	 * @return
	 */
	private Constant.Record updateRecord(Codes.Update code, StackFrame stackframe, Constant.Record record, Constant givenValue) {
		HashMap<String, Constant> values = new HashMap<String, Constant>(record.values);
		String[] fields = code.fields.toArray(new String[code.fields.size()]);
		//String field = fields[0];

		// Get the field value
		Constant fieldValue = values.get(fields[0]);
		if (fieldValue instanceof Constant.List) {
			Constant.List list = (Constant.List) fieldValue;
			//int index = 0;
			//Check if the list is empty. If so, then get the updated index.
			/*Constant key = stackframe.getRegister(code.key(0));
			if(key instanceof Constant.Integer){
				int	index = ((Constant.Integer) key).value.intValue();
				//Update the list with the given value.
				givenValue = update(list, givenValue, index);
			}*/				
		}

		if (fieldValue instanceof Constant.Record && fields.length == 2){
			//Check if there is another field that requires to update the given value.
				//Do the update for the nested record. 
			givenValue = update((Constant.Record)fieldValue, givenValue, fields[1]);			
		} 
		return update(record, givenValue, fields[0]);
	}

	private Constant.Strung updateStrung(Codes.Update code, StackFrame stackframe) {
		Constant.Strung strung = (Constant.Strung) stackframe.getRegister(code.target());
		Constant.Char updatedValue = (Constant.Char) stackframe.getRegister(code.result());
		// Get the index
		Constant.Integer updateIndex = (Constant.Integer) stackframe.getRegister(code.operand(0));

		// Replace a char in the strung at the index.
		StringBuilder newStrung = new StringBuilder(strung.value);
		newStrung.setCharAt(updateIndex.value.intValue(), updatedValue.value);
		return Constant.V_STRING(newStrung.toString());
	}



	private Constant.Map updateMap(Codes.Update code, StackFrame stackframe) {
		Constant.Map map = (Constant.Map) stackframe.getRegister(code.target());
		Constant givenValue = stackframe.getRegister(code.result());
		// Get the key
		int[] operands = code.operands();
		Constant key = stackframe.getRegister(operands[0]);
		// Get the existing value
		Constant value = map.values.get(key);
		
		if (value != null && value instanceof Constant.List) {
			//Update the element
			int index = ((Constant.Integer) stackframe.getRegister(operands[1])).value.intValue();
			givenValue = update((Constant.List)value, givenValue, index);
		}  
		// Update the map
		return update(map, key, givenValue);
	}

	private Constant updateReference(Codes.Update code, StackFrame stackframe) {
		Constant reference = stackframe.getRegister(code.target());
		Constant updatedValue = null;
		Constant givenValue = stackframe.getRegister(code.result());
		if (reference instanceof Constant.Record) {
			Constant.Record record = (Constant.Record)reference;
			HashMap<String, Constant> values = record.values;
			String[] fields = code.fields.toArray(new String[code.fields.size()]);
			// Get the field value
			Constant fieldValue = values.get(fields[0]);
			//Update the value
			if(fieldValue != null){
				values.put(fields[0], givenValue);
			}else{
				internalFailure("Not implemented!", "UpdateInterpreter.java", null);
				return null;
			}			
			return reference;
		}
		
		if(reference instanceof Constant.Integer){
			updatedValue = (Constant.Integer)reference;
			return updatedValue;
		}

		internalFailure("Not implemented!", "UpdateInterpreter.java", null);
		return null;
	}
	/**
	 * Update the constant with the given constant.
	 * @param code
	 * @param stackframe
	 */
	public void interpret(Codes.Update code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		Constant result = null;
		// Popup the compound type (lists, dictionaries, strings, records and
		// references
		Type afterType = code.afterType;
		Constant givenValue = stackframe.getRegister(code.result());
		if (afterType instanceof Type.List) {
			Constant.List list = (Constant.List) stackframe.getRegister(code.target());
			//Check if there is any given field. 
			if(code.fields!=null&& code.fields.size()==1){
				result = updateList(stackframe, list, givenValue, code.keys(), code.fields.get(0));	
			}else{
				result = updateList(stackframe, list, givenValue,  code.keys());	
			}				
		} else if (afterType instanceof Type.Record) {
			Constant.Record record = (Constant.Record) stackframe.getRegister(code.target());
			result = updateRecord(code, stackframe, record, givenValue);
		} else if (afterType instanceof Type.Strung) {
			result = updateStrung(code, stackframe);
		} else if (afterType instanceof Type.Map) {
			result = updateMap(code, stackframe);
		} else if (afterType instanceof Type.Reference) {
			result = updateReference(code, stackframe);
		} else {
			internalFailure("Not implemented!", "UpdateInterpreter.java", null);
		}

		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(), "%" + code.target() + "(" + result + ")");
		stackframe.setLine(++linenumber);
	}

}
