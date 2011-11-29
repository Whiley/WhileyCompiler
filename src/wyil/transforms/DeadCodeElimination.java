package wyil.transforms;

import java.io.IOException;
import java.util.*;

import wyil.*;
import wyil.lang.*;
import wyil.util.Pair;

/**
 * Removes dead-code from method and function bodies in a given bytecode.
 * Dead=code is defined as code which is unreachable from the entry point by
 * bytecodes sequentially, and traversing along all branches and exceptional
 * edges as appropriate.
 * 
 * @author djp
 * 
 */
public class DeadCodeElimination implements Transform {

	public DeadCodeElimination(ModuleLoader loader) {
		
	}
	
	public void apply(Module module) throws IOException {
		for(Module.TypeDef type : module.types()) {
			transform(type);
		}		
		for(Module.Method method : module.methods()) {
			transform(method);
		}
	}
	
	public void transform(Module.TypeDef type) {
		Block constraint = type.constraint();
		
		if (constraint != null) {
			transform(constraint);
		}
	}

	public void transform(Module.Method method) {		
		for(Module.Case c : method.cases()) {
			transform(c,method);
		}
	}
	
	public void transform(Module.Case mcase, Module.Method method) {	
		Block body = mcase.body();
		transform(body);
	}
	
	private void transform(Block block) {
		HashMap<String,Integer> labelMap = buildLabelMap(block);
		BitSet visited = new BitSet(block.size());
		Stack<Integer> worklist = new Stack();
		worklist.push(0);
		visited.set(0);
		while(!worklist.isEmpty()) {
			int index = worklist.pop();
					
			Code code = block.get(index).code;
			
			if(code instanceof Code.Goto) {
				Code.Goto g = (Code.Goto) code;				
				addTarget(labelMap.get(g.target),visited,worklist);
			} else if(code instanceof Code.IfGoto) {								
				Code.IfGoto ig = (Code.IfGoto) code;				
				addTarget(index+1,visited,worklist);
				addTarget(labelMap.get(ig.target),visited,worklist);				
			} else if(code instanceof Code.IfType) {								
				Code.IfType ig = (Code.IfType) code;				
				addTarget(index+1,visited,worklist);
				addTarget(labelMap.get(ig.target),visited,worklist);				
			} else if(code instanceof Code.Switch) {
				Code.Switch sw = (Code.Switch) code;
				for(Pair<Value,String> p : sw.branches) {
					addTarget(labelMap.get(p.second()),visited,worklist);
				}
				addTarget(labelMap.get(sw.defaultTarget),visited,worklist);
			} else if(code instanceof Code.TryCatch) {
				Code.TryCatch tc = (Code.TryCatch) code;
				for(Pair<Type,String> p : tc.catches) {
					addTarget(labelMap.get(p.second()),visited,worklist);
				}
				addTarget(index+1,visited,worklist);
			} else if(code instanceof Code.Throw || code instanceof Code.Return) {
				// terminating bytecode
			} else {
				// sequential bytecode	
				if((index+1) < block.size()) {
					addTarget(index+1,visited,worklist);
				}
			}
		}
		
		// Now, remove unvisited blocks!
		int index=0;
		int size = block.size();
		for(int i=0;i!=size;++i) {
			if(!visited.get(i)) {
				block.remove(index);
			} else {
				index = index + 1;
			}
		}
	}
	
	private static HashMap<String,Integer> buildLabelMap(Block block) {
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		for(int i=0;i!=block.size();++i) {
			Code c = block.get(i).code;
			if(c instanceof Code.Label) {
				Code.Label l = (Code.Label) c;				
				map.put(l.label,i);
			}
		}
		return map;
	}
	
	private static void addTarget(int index, BitSet visited, Stack<Integer> worklist) {
		if(!visited.get(index)) {
			visited.set(index);			
			worklist.push(index);
		}
	}
}
