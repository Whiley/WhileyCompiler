package wyopcl.util.interpreter;

import static wycc.lang.SyntaxError.internalFailure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;

/**
 * Interprets <code>Codes.ForAll</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.ForAll
 *
 */
public class ForAllInterpreter extends Interpreter {

	private static ForAllInterpreter instance;	
	private ForAllInterpreter(){}

	/*Implement the Singleton pattern to ensure this class has one instance.*/
	public static ForAllInterpreter getInstance(){
		if (instance == null){
			instance = new ForAllInterpreter();
		}
		return instance;
	}
	/**
	 * Goes to the loop end.
	 * @param code
	 * @param stackframe
	 */
	private void gotoLoopEnd(Codes.ForAll code, StackFrame stackframe){
		stackframe.setRegister(code.indexOperand, null);
		int linenumber = symboltable.getBlockPosByLabel(stackframe.getBlock(), code.target+"LoopEnd");
		stackframe.setLine(linenumber);
	}
	/**
	 * Goes to the loop body.
	 * @param code
	 * @param stackframe
	 * @param indexOperand
	 */
	private void gotoLoopBody(Codes.ForAll code, StackFrame stackframe, Constant indexOperand){
		int linenumber = stackframe.getLine();
		//stackframe.setLoop_index(code.target, index);
		stackframe.setRegister(code.indexOperand, indexOperand);
		printMessage(stackframe, code.toString(), "%"+ code.indexOperand + "("+indexOperand+")");
		stackframe.setLine(++linenumber);
	}
	
	
	private void gotoLoopBody(Codes.ForAll code, StackFrame stackframe, int index, Constant indexOperand){		
		stackframe.setLoop_index(code.target, index);
		gotoLoopBody(code, stackframe, indexOperand);	
	}
	
	
	private void iterateOverListSet(Codes.ForAll code, StackFrame stackframe, Constant[] array){
		Constant indexOperand = stackframe.getRegister(code.indexOperand);
		if(array.length > 0){
			//Get the current index
			int index = stackframe.getLoop_index(code.target);
			if(indexOperand == null || index == -1){
				index = 0;
			}else{
				index++;				
			}			
			//Check if the index is out-of-boundary. If not, then go into the loop.
			if(index < array.length){
				gotoLoopBody(code, stackframe, index, array[index]);
				return;
			}
		}		
		gotoLoopEnd(code, stackframe);
	
	}
	
	/**
	 * Iterates over all elements in a map
	 * @param map
	 * @param code
	 * @param stackframe
	 */
	private void iterateOverMap(Constant.Map map, Codes.ForAll code, StackFrame stackframe){
		Constant indexOperand = stackframe.getRegister(code.indexOperand);
		
		HashMap<Constant, Constant> values = map.values;
		if(values != null && values.size()>0){
			Iterator<Entry<Constant, Constant>> iterator = values.entrySet().iterator();
			Entry<Constant, Constant> entry = null;
			if(indexOperand != null){
				//Find the entry that matches the indexOperand
				Constant.Tuple tuple = (Constant.Tuple)indexOperand;
				while(iterator.hasNext()){
					//Check if the tuple matches one of the entries
					entry = iterator.next();
					if(entry.getKey()==tuple.values.get(0) && entry.getValue() == tuple.values.get(1)){
						break;
					}
				}				
			}
			if(iterator.hasNext()){
				entry = iterator.next();
				//Constant.Tuple tuple = null;				
				Collection<Constant> list = new ArrayList<Constant>();
				list.add(entry.getKey());
				list.add(entry.getValue());
				//Create a tuple
				indexOperand = Constant.V_TUPLE(list);
				gotoLoopBody(code, stackframe, indexOperand);
				return;
			}			
		}		
		gotoLoopEnd(code, stackframe);
	}	
	/**
	 * Iterate over the single record
	 * @param record
	 * @param code
	 * @param stackframe
	 */
	private void iterateOverRecord(Constant.Record record, Codes.ForAll code, StackFrame stackframe){
		Constant indexOperand = stackframe.getRegister(code.indexOperand);
		if(indexOperand == null){
			gotoLoopBody(code, stackframe, record);
		}else{
			gotoLoopEnd(code, stackframe);
		}		
	}
	
	/**
	 * Iterates each element in the composite data set and stores the activated
	 * element at the indexOperand.
	 * @param code
	 * @param stackframe
	 */
	public void interpret(Codes.ForAll code, StackFrame stackframe) {		
		//Get the index
		Constant source = stackframe.getRegister(code.sourceOperand);		
		if(source instanceof Constant.List){			
			Constant.List list = (Constant.List)source;
			Constant[] array = new Constant[list.values.size()];
			array = list.values.toArray(array);
			iterateOverListSet(code, stackframe, array);
		}else if (source instanceof Constant.Map){			
			iterateOverMap((Constant.Map)source, code, stackframe);
		}else if (source instanceof Constant.Set){
			Constant.Set set = (Constant.Set)source;
			Constant[] array = new Constant[set.values.size()];
			array = set.values.toArray(array);
			iterateOverListSet(code, stackframe, array);
		}else if(source instanceof Constant.Strung){
			Constant.Strung strung = (Constant.Strung)source;
			Constant.Char[] chars = new Constant.Char[strung.value.length()];
			for(int index=0;index<strung.value.length();index++){
				chars[index] = Constant.V_CHAR(strung.value.charAt(index));
			}
			iterateOverListSet(code, stackframe, chars);
		}else if (source instanceof Constant.Null){
			//Go to loop end
			gotoLoopEnd(code, stackframe);
		}else if (source instanceof Constant.Record){
			Constant.Record record = (Constant.Record)source;
			iterateOverRecord(record, code, stackframe);
		}else{
			internalFailure("Not implemented!", "InterpreterForAll.java", null);
		}
		
		
	}

}
