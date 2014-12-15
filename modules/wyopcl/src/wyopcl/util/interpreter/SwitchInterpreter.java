package wyopcl.util.interpreter;

import java.util.ArrayList;
import java.util.Iterator;

import wycc.util.Pair;
import wyil.lang.Code.Block;
import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Switch</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Switch
 *
 */
public class SwitchInterpreter extends Interpreter {
	private static SwitchInterpreter instance;

	private SwitchInterpreter() {}

	/* Implement the Singleton pattern to ensure this class has one instance. */
	public static SwitchInterpreter getInstance() {
		if (instance == null) {
			instance = new SwitchInterpreter();
		}
		return instance;
	}

	public void interpret(Codes.Switch code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		Block block = stackframe.getBlock();		
		Constant value = stackframe.getRegister(code.operand);
		ArrayList<Pair<Constant, String>> branches = code.branches;		
		Iterator<Pair<Constant, String>> iterator = branches.iterator();
		boolean isMatched = false;
		while(iterator.hasNext()){
			Pair<Constant, String> next = iterator.next();
			//Check if the value matches the key
			if(next.first().equals(value)){
				//Go to the label
				String label = next.second();				
				linenumber = symboltable.getBlockPosByLabel(block, label);
				isMatched = true;
				break;
			}
		}
		
		//If not matched, go to default
		if(!isMatched){
			linenumber = symboltable.getBlockPosByLabel(block, code.defaultTarget);
		}
		
		printMessage(stackframe, code.toString(), "%" + code.operand + "(" + value + ")");
		stackframe.setLine(linenumber);

	}

}
