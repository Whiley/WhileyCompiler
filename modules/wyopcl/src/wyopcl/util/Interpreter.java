package wyopcl.util;

import static wycc.lang.SyntaxError.internalFailure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import wybs.lang.Build.Project;
import wybs.lang.Builder;
import wycc.lang.SyntaxError;
import wycc.util.Logger;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyfs.lang.Path.Root;
import wyil.lang.Code;
import wyil.lang.Code.Block;
import wyil.lang.Codes;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.Type.FunctionOrMethod;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Case;
import wyil.lang.WyilFile.FunctionOrMethodDeclaration;
import wyopcl.util.interpreter.AssertOrAssumeInterpreter;
import wyopcl.util.interpreter.AssignInterpreter;
import wyopcl.util.interpreter.BinaryOperatorInterpreter;
import wyopcl.util.interpreter.ConstantInterpreter;
import wyopcl.util.interpreter.ConvertInterpreter;
import wyopcl.util.interpreter.DebugInterpreter;
import wyopcl.util.interpreter.DereferenceInterpreter;
import wyopcl.util.interpreter.FailInterpreter;
import wyopcl.util.interpreter.FieldLoadInterpreter;
import wyopcl.util.interpreter.ForAllInterpreter;
import wyopcl.util.interpreter.GotoInterpreter;
import wyopcl.util.interpreter.IfInterpreter;
import wyopcl.util.interpreter.IfIsInterpreter;
import wyopcl.util.interpreter.IndexOfInterpreter;
import wyopcl.util.interpreter.IndirectInvokeInterpreter;
import wyopcl.util.interpreter.InvertInterpreter;
import wyopcl.util.interpreter.InvokeInterpreter;
import wyopcl.util.interpreter.LabelInterpreter;
import wyopcl.util.interpreter.LambdaInterpreter;
import wyopcl.util.interpreter.LengthOfInterpreter;
import wyopcl.util.interpreter.ListOperatorInterpreter;
import wyopcl.util.interpreter.LoopEndInterpreter;
import wyopcl.util.interpreter.LoopInterpreter;
import wyopcl.util.interpreter.NewListInterpreter;
import wyopcl.util.interpreter.NewMapInterpreter;
import wyopcl.util.interpreter.NewObjectInterpreter;
import wyopcl.util.interpreter.NewRecordInterpreter;
import wyopcl.util.interpreter.NewSetInterpreter;
import wyopcl.util.interpreter.NewTupleInterpreter;
import wyopcl.util.interpreter.NopInterpreter;
import wyopcl.util.interpreter.ReturnInterpreter;
import wyopcl.util.interpreter.SetOperatorInterpreter;
import wyopcl.util.interpreter.StringOperatorInterpreter;
import wyopcl.util.interpreter.SubListInterpreter;
import wyopcl.util.interpreter.SubStringInterpreter;
import wyopcl.util.interpreter.SwitchInterpreter;
import wyopcl.util.interpreter.ThrowInterpreter;
import wyopcl.util.interpreter.TryCatchInterpreter;
import wyopcl.util.interpreter.TupleLoadInterpreter;
import wyopcl.util.interpreter.UnaryOperatorInterpreter;
import wyopcl.util.interpreter.UpdateInterpreter;

/*Declare the abstract Interpreter class, methods and variables. */
/**
 * Entry point of WyIL intepreter to iterate over bytecode, build control flow graph, 
 * and infer bounds.  
 * @author Min-Hsien Weng
 *
 */
public class Interpreter implements Builder {
	//Store a hashmap inside a hashmap.
	protected static HashMap<String, HashMap<FunctionOrMethod, Block>> blocktable = new HashMap<String, HashMap<FunctionOrMethod, Block>>();
	protected static Stack<StackFrame> blockstack = new Stack<StackFrame>();
	protected static SymbolTable symboltable = new SymbolTable();
	protected static InterpreterConfiguration config;

	public Interpreter(){	

	}	

	public Interpreter(InterpreterConfiguration config){
		Interpreter.config = config;
	}

	public boolean isVerbose() {
		return (boolean) config.getProperty("verbose");
	}	


	@Override
	public Project project() {
		return (Project) config.getProperty("project");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<wyfs.lang.Path.Entry<?>> build(
			Collection<Pair<wyfs.lang.Path.Entry<?>, Root>> delta)
					throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();

		HashSet<Path.Entry<?>> generatedFiles = new HashSet<Path.Entry<?>>();
		for(Pair<Path.Entry<?>,Path.Root> p : delta) {
			//Path.Root dst = p.second();
			Path.Entry<WyilFile> sf = (Path.Entry<WyilFile>) p.first();
			WyilFile module = sf.read();
			config.setProperty("module", module);
			config.setProperty("filename", module.filename());
			this.preprocessor(module);
			//Get started with the main method.
			this.interpret(module);
		}

		long endTime = System.currentTimeMillis();
		Logger logger = (Logger) config.getProperty("logger");
		logger.logTimedMessage("Wyil interpreter completed.\nFile:" + config.getProperty("filename"),
				(endTime - start), memory - runtime.freeMemory());
		return generatedFiles;
	}



	private void scanLabelsinBlock(Block blk){					
		//Pre-scan the code block and keep the symbol label map.
		for(int pos = 0; pos <blk.size(); pos++){
			Code code = blk.get(pos).code;
			String label = null;
			if(code instanceof Codes.LoopEnd){
				label = ((Codes.LoopEnd)code).label+"LoopEnd";							
				//Go to the next statement after the loop end.
				symboltable.addLabelLoc(blk, label, pos+1);														
			}else if(code instanceof Codes.TryEnd){
				symboltable.addLabelLoc(blk, ((Codes.TryEnd) code).label, pos);
			}else if(code instanceof Codes.Label){
				//Put the label map into the queue.
				label = ((Codes.Label)code).label;
				symboltable.addLabelLoc(blk, label, pos);							
			}else if (code instanceof Codes.ForAll){
				label = ((Codes.Loop)code).target;
				symboltable.addLabelLoc(blk, label, pos);
			}else if(code instanceof Codes.Loop){								
				//This case includes Code.Loop and Code.ForAll
				label = ((Codes.Loop)code).target;
				symboltable.addLabelLoc(blk, label, pos);							
			}else if(code instanceof Codes.TryCatch){
				symboltable.addTryCatchLoc((Codes.TryCatch)code, pos);
			}else{
				//Do nothing
			}

			if(label != null && isVerbose()){
				System.out.println(label+"--->"+pos);
			}
		}
	}

	/*Scans the methods*/
	protected void preprocessor(WyilFile module){
		String id = module.id().toString();
		for(WyilFile.FunctionOrMethodDeclaration method : module.functionOrMethods()) {
			String name = id+":"+method.name();
			HashMap<FunctionOrMethod, Block> blocks = blocktable.get(name);
			if(blocks == null){
				blocks = new HashMap<FunctionOrMethod, Block>();
			}
			for(Case mcase : method.cases()){
				List<Block.Entry> entries = new ArrayList<Block.Entry>();
				//Add the entries in the precondition.
				List<Block> pre_list = mcase.precondition();
				if(pre_list != null){
					for(Block pre : pre_list){
						entries.addAll(pre);
					}					
				}	
				//Add the entries in the body block.
				entries.addAll(mcase.body());

				//Add the entries in the postcondition.
				List<Block> post_list = mcase.postcondition();
				if(post_list != null){
					for(Block post: post_list){
						entries.addAll(post);
					}					
				}

				Block block = new Block(mcase.body().numInputs(), entries);
				blocks.put(method.type(), block);				
				scanLabelsinBlock(block);
			}

			blocktable.put(name, blocks);
		}

	}


	/**
	 * Get the function block by name plus type (if provided)
	 * @param name the name of method or function
	 * @param functionOrMethod the method or function types
	 * @return the function block that contains a list of bytecode. 
	 */
	public static Block getFuncBlockByName(String name, FunctionOrMethod... functionOrMethod){
		//Get the Hashmap of function block by name	
		//Get the Block for the corresponding function/method.
		if(blocktable.containsKey(name)){
			HashMap<FunctionOrMethod, Block> hashMap = blocktable.get(name);
			if (functionOrMethod == null || functionOrMethod.length == 0){	
				return (Block) hashMap.values().toArray()[0];
			}else{
				return hashMap.get(functionOrMethod[0]);
			}
		}
		return null;		
	}
	/**
	 * Passes the command-line arguments and interprets each bytecode of the main function.  
	 * @param module in-memory WyIL bytecode
	 */
	protected void interpret(WyilFile module){
		//Get the main method
		FunctionOrMethodDeclaration main_method= module.functionOrMethod("main").get(0);
		for(Case mcase:main_method.cases()){
			StackFrame mainframe = new StackFrame(1, mcase.body(), 0, main_method.name(), -1);
			//Put a Constant.Record to the register 0.				
			int index = 0;
			for(wyil.lang.Type param : main_method.type().params()){
				//The values are used to create the Constant.Record.
				HashMap<String, Constant> values = new HashMap<String, Constant>();
				//put 'args' and 'out' fields into values.
				for(Entry<String, Type> entry : ((Type.Record)param).fields().entrySet()){
					values.put(entry.getKey(), Constant.V_TYPE(entry.getValue()));
				}
				//Create a List of Constant objects.
				ArrayList<Constant> arguments = new ArrayList<Constant>();
				for(String arg: (String[]) config.getProperty("arguments")){
					arguments.add(Constant.V_STRING(arg));
				}
				//Replace the value of args with the argument list.
				values.put("args", Constant.V_LIST(arguments));
				mainframe.setRegister(index, Constant.V_RECORD(values));
				index++;
			}
			blockstack.push(mainframe);				
		}


		while(!blockstack.isEmpty()){
			StackFrame stackframe = blockstack.peek();
			Block block = stackframe.getBlock();
			int linenumber = stackframe.getLine();
			if(linenumber < block.size()){
				Block.Entry entry = block.get(linenumber);				
				this.dispatch(entry, stackframe);
			}else{
				//Finish this block and pop it up from the stack.
				blockstack.pop();
			}
		}
	}

	/**
	 * Dispatch each bytecode along with the active stack frame to an individual interpreter of bytecode type.
	 * 
	 * @param entry the bytecode.
	 * @param stackframe the active stack frame.
	 */
	private void dispatch(Block.Entry entry, StackFrame stackframe) {	
		Code code = entry.code;
		try{
			if (code instanceof Codes.AssertOrAssume) {			
				AssertOrAssumeInterpreter.getInstance().interpret((Codes.AssertOrAssume)code, stackframe);
			} else if (code instanceof Codes.Assign) {			
				AssignInterpreter.getInstance().interpret((Codes.Assign)code, stackframe);
			} else if (code instanceof Codes.BinaryOperator) {			
				BinaryOperatorInterpreter.getInstance().interpret((Codes.BinaryOperator)code, stackframe);
			} else if (code instanceof Codes.ListOperator) {
				ListOperatorInterpreter.getInstance().interpret((Codes.ListOperator)code, stackframe);
			} else if (code instanceof Codes.StringOperator) {
				StringOperatorInterpreter.getInstance().interpret((Codes.StringOperator)code, stackframe);
			} else if (code instanceof Codes.Convert) {			
				ConvertInterpreter.getInstance().interpret((Codes.Convert)code, stackframe);
			} else if (code instanceof Codes.Const) {			
				ConstantInterpreter.getInstance().interpret((Codes.Const)code, stackframe);
			} else if (code instanceof Codes.Debug) {
				DebugInterpreter.getInstance().interpret((Codes.Debug)code, stackframe);
			} else if (code instanceof Codes.Dereference) {
				DereferenceInterpreter.getInstance().interpret((Codes.Dereference)code, stackframe);
			} else if (code instanceof Codes.Fail) {
				FailInterpreter.getInstance().interpret((Codes.Fail)code, stackframe);
			} else if (code instanceof Codes.FieldLoad) {		
				FieldLoadInterpreter.getInstance().interpret((Codes.FieldLoad)code, stackframe);			
			} else if (code instanceof Codes.ForAll) {				
				ForAllInterpreter.getInstance().interpret((Codes.ForAll)code, stackframe);
			} else if (code instanceof Codes.Goto) {	
				GotoInterpreter.getInstance().interpret((Codes.Goto)code, stackframe);
			} else if (code instanceof Codes.If) {
				IfInterpreter.getInstance().interpret((Codes.If)code, stackframe);			
			} else if (code instanceof Codes.IfIs) {
				IfIsInterpreter.getInstance().interpret((Codes.IfIs)code, stackframe);
			} else if (code instanceof Codes.IndirectInvoke) {			
				IndirectInvokeInterpreter.getInstance().interpret((Codes.IndirectInvoke)code, stackframe);
			} else if (code instanceof Codes.Invoke) {			
				InvokeInterpreter.getInstance().interpret((Codes.Invoke)code, stackframe);
			} else if (code instanceof Codes.Invert) {
				InvertInterpreter.getInstance().interpret((Codes.Invert)code, stackframe);
			} else if (code instanceof Codes.LoopEnd) {
				LoopEndInterpreter.getInstance().interpret((Codes.LoopEnd)code, stackframe);									
			} else if (code instanceof Codes.Label) {
				LabelInterpreter.getInstance().interpret((Codes.Label)code, stackframe);
			} else if (code instanceof Codes.Lambda) {
				LambdaInterpreter.getInstance().interpret((Codes.Lambda)code, stackframe);
			} else if (code instanceof Codes.LengthOf) {			
				LengthOfInterpreter.getInstance().interpret((Codes.LengthOf)code, stackframe);
			} else if (code instanceof Codes.IndexOf) {			
				IndexOfInterpreter.getInstance().interpret((Codes.IndexOf)code, stackframe);
			} else if (code instanceof Codes.Loop) {			
				LoopInterpreter.getInstance().interpret((Codes.Loop)code, stackframe);			
			} else if (code instanceof Codes.Move) {
				internalFailure("Not implemented!", (String)config.getProperty("filename"), entry);
			} else if (code instanceof Codes.NewMap) {
				NewMapInterpreter.getInstance().interpret((Codes.NewMap)code, stackframe);
			} else if (code instanceof Codes.NewList) {			
				NewListInterpreter.getInstance().interpret((Codes.NewList)code, stackframe);
			} else if (code instanceof Codes.NewRecord) {
				NewRecordInterpreter.getInstance().interpret((Codes.NewRecord)code, stackframe);
			} else if (code instanceof Codes.NewSet) {
				NewSetInterpreter.getInstance().interpret((Codes.NewSet)code, stackframe);
			} else if (code instanceof Codes.NewTuple) {
				NewTupleInterpreter.getInstance().interpret((Codes.NewTuple)code, stackframe);
			} else if (code instanceof Codes.Return) {			
				ReturnInterpreter.getInstance().interpret((Codes.Return)code, stackframe);
			} else if (code instanceof Codes.NewObject) {
				NewObjectInterpreter.getInstance().interpret((Codes.NewObject)code, stackframe);
			} else if (code instanceof Codes.Nop) {
				NopInterpreter.getInstance().interpret((Codes.Nop)code, stackframe);
			} else if (code instanceof Codes.SetOperator){
				SetOperatorInterpreter.getInstance().interpret((Codes.SetOperator)code, stackframe);
			} else if (code instanceof Codes.SubList) {
				SubListInterpreter.getInstance().interpret((Codes.SubList)code, stackframe);
			} else if (code instanceof Codes.SubString) {
				SubStringInterpreter.getInstance().interpret((Codes.SubString)code, stackframe);
			} else if (code instanceof Codes.Switch) {
				SwitchInterpreter.getInstance().interpret((Codes.Switch)code, stackframe);
			} else if (code instanceof Codes.Throw) {
				ThrowInterpreter.getInstance().interpret((Codes.Throw)code, stackframe);
			} else if (code instanceof Codes.TryCatch) {
				TryCatchInterpreter.getInstance().interpret((Codes.TryCatch)code, stackframe);
			} else if (code instanceof Codes.TupleLoad) {
				TupleLoadInterpreter.getInstance().interpret((Codes.TupleLoad)code, stackframe);
			} else if (code instanceof Codes.UnaryOperator){
				UnaryOperatorInterpreter.getInstance().interpret((Codes.UnaryOperator)code, stackframe);
			} else if (code instanceof Codes.Update) {
				UpdateInterpreter.getInstance().interpret((Codes.Update)code, stackframe);
			} else {
				internalFailure("unknown wyil code encountered (" + code + ")", (String)config.getProperty("filename"), entry);
			}
		} catch (SyntaxError ex) {
			throw ex;	
		} catch (Exception ex) {		
			internalFailure(ex.getMessage(), (String)config.getProperty("filename"), entry, ex);
		}

	}	

	/**
	 * prints out the interpretation for each line of bytecode.
	 * @param stackframe the active stack frame
	 * @param input the 
	 * @param output
	 */
	public void printMessage(StackFrame stackframe, String input, String output){
		if(isVerbose()){
			System.out.println(stackframe.getDepth()+" "+stackframe.getName()+"."+stackframe.getLine()
					+" ["+input+"] "+output+"\n");
		}

	}
	
	public void printVerificationMessage(StackFrame stackframe, String input, String output){
		System.err.println(stackframe.getDepth()+" "+stackframe.getName()+"."+stackframe.getLine()
				+" ["+input+"] "+output+"\n");
		System.exit(-1);
	}

}
