package wyopcl.util.interpreter;

import java.util.ArrayList;
import java.util.List;

import wyil.lang.Code.Block;
import wyil.lang.Codes;
import wyil.lang.Constant;
import wyopcl.util.Closure;
import wyopcl.util.Interpreter;
import wyopcl.util.StackFrame;
import wyopcl.util.Utility;
/**
 * Interprets <code>Codes.IndirectInvoke</code> bytecode.
 * @author Min-Hsien Weng
 * @see wyil.lang.Codes.IndirectInvoke
 *
 */
public class IndirectInvokeInterpreter extends Interpreter {

	private static IndirectInvokeInterpreter instance;

	private IndirectInvokeInterpreter() {}

	/* Implement the Singleton pattern to ensure this class has one instance. */
	public static IndirectInvokeInterpreter getInstance() {
		if (instance == null) {
			instance = new IndirectInvokeInterpreter();
		}
		return instance;
	}
	/**
	 * Invokes the function
	 * @param code
	 * @param stackframe
	 */
	private void invokeRuntimeFunction(Codes.IndirectInvoke code, StackFrame stackframe) {
		int linenumber = stackframe.getLine();
		Constant.Lambda lambda = (Constant.Lambda) stackframe.getRegister(code.reference());		
		String name = lambda.name.name();

		// Create a list of parameters.
		List<Constant> params = new ArrayList<Constant>();				
		for (int param: code.parameters()) {
			params.add(stackframe.getRegister(param));
		}

		String str ="";
		if (name.equalsIgnoreCase("println") || name.equalsIgnoreCase("print")) {		
			// Invoke the function
			Utility.invokeJavaPrintFunction(name, code.type().params(), params);
			printMessage(stackframe, code.toString(), str);
			stackframe.setLine(++linenumber);
			params = null;
		} else {
			Block blk = Interpreter.getFuncBlockByName(lambda.name.toString(), code.type());
			StackFrame newStackFrame = Utility.invokeFunction(blk,
															  stackframe.getDepth(),
															  lambda.name.toString(),
															  params,
															  code.target());
			// Start invoking a new block.
			blockstack.push(newStackFrame);
			printMessage(stackframe, code.toString(), str);
		}		
	}

	/**
	 * Invokes anonymous function, pass the known and unknown parameters
	 * @param code
	 * @param stackframe the activated stack frame
	 */
	private void invokeAnonymousFunction(Codes.IndirectInvoke code, StackFrame stackframe) {
		// Get the depth
		Closure closure = (Closure) stackframe.getRegister(code.reference());
		String name = closure.lambda.name.module() +":" + closure.lambda.name.name();
		Block blk = Interpreter.getFuncBlockByName(name);
		// Create a new StackFrame
		StackFrame newStackFrame = new StackFrame(stackframe.getDepth() + 1, blk, 0, closure.lambda.name.toString(), code.target());
		// Pass the input parameters.
		int index = 0, unknownParams = 0;
		for (Constant value : closure.params.values) {
			if (value == null) {
				// unknown parameter, so get it from the current stack frame
				newStackFrame.setRegister(index, stackframe.getRegister(code.parameter(unknownParams)));
				unknownParams++;
			} else {
				// known parameter, so copy it into the new stack frame
				newStackFrame.setRegister(index, value);
			}
			index++;
		}	

		// Pushes the stack frame of anonymous function to the stack.
		blockstack.push(newStackFrame);
		printMessage(stackframe, code.toString(),
				"%" + code.target() + "(" + stackframe.getRegister(code.target()) + ")\n");
	}
	
	/**
	 * Invokes anonymous or runtime function.
	 * @param code
	 * @param stackframe
	 */
	public void interpret(Codes.IndirectInvoke code, StackFrame stackframe) {
		Constant func = stackframe.getRegister(code.reference());
		if (func instanceof Closure) {
			invokeAnonymousFunction(code, stackframe);
		} else {
			invokeRuntimeFunction(code, stackframe);
		}
	}

}
