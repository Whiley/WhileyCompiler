package wyopcl.util.interpreter;

import java.util.ArrayList;
import java.util.Collection;

import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Closure;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
/**
 * Interprets <code>Codes.Lambda</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.Lambda
 *
 */
public class LambdaInterpreter extends Interpreter {
	private static LambdaInterpreter instance;

	private LambdaInterpreter() {}

	/* Implement the Singleton pattern to ensure this class has one instance. */
	public static LambdaInterpreter getInstance() {
		if (instance == null) {
			instance = new LambdaInterpreter();
		}
		return instance;
	}

	public void interpret(Codes.Lambda code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		//Create a list of parameters.
		Collection<Constant> parameters = new ArrayList<Constant>();
		for(int operand: code.operands()){
			if(operand <0){
				parameters.add(null);
			}else{
				parameters.add(stackframe.getRegister(operand));
			}
		}
		//Check if the extra local parameters need adding.
		int size = parameters.size();
		if(code.type().params() != null){
			int params_size = code.type().params().size();
			while (size<params_size){
				parameters.add(null);
				size++;
			}
		}
		
		
		Constant.Tuple params = Constant.V_TUPLE(parameters);
		//Create a Constant.Closure 
		Constant.Lambda lambda = Constant.V_LAMBDA(code.name, code.type());
		Constant.Type type = Constant.V_TYPE(code.assignedType());
		Constant result = Closure.V_Closure(lambda, params, type);		
		stackframe.setRegister(code.target(), result);
		printMessage(stackframe, code.toString(),
				"%" + code.target() + "(" + result + ")\n");
		stackframe.setLine(++linenumber);
	}

}
