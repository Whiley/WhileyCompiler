// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil.transforms;

import java.io.IOException;
import java.util.*;

import wybs.lang.Builder;
import wycc.lang.Transform;
import wycc.util.Pair;
import wyil.lang.*;
import wyil.util.AttributedCodeBlock;

/**
 * <p>
 * Removes dead-code from method and function bodies, preconditions,
 * postconditions and type invariants. Dead-code is defined as code which is
 * unreachable through the corresponding control-flow graph starting from the
 * entry point. For example, consider:
 * </p>
 * 
 * <pre>
 *   const %1 = 0                 
 *   ifge %0, %1 goto label0           
 *   neg %2 = %0                       
 *   assign %0 = %2
 *   goto label0
 *   const %1 = 0   // unreachable            
 * .label0                                 
 *   return %0                         
 *   return         // unreachable
 * </pre>
 * <p>
 * Here, we can see two bytecodes which are unreachable by any path through the
 * control-flow graph starting from the entry point. Both of these, therefore,
 * are considered dead-code and can safely be removed.
 * </p>
 * <p>
 * The algorithm traverses a given code block using (roughly speaking) a
 * depth-first search of the corresponding control-flow graph. Bytecodes which
 * are not visited during this traversal are guaranteed to be unreachable and,
 * hence, are removed. Observe that, however, some unreachable bytecodes may
 * remain if their unreachable status depends on more than just reachability
 * (i.e. an unrealisable path).
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class DeadCodeElimination implements Transform<WyilFile> {

	/**
	 * Determines whether constant propagation is enabled or not.
	 */
	private boolean enabled = getEnable();


	public static String describeEnable() {
		return "Enable/disable constant propagation";
	}

	public static boolean getEnable() {
		return true; // default value
	}

	public void setEnable(boolean flag) {
		this.enabled = flag;
	}

	public DeadCodeElimination(Builder builder) {

	}

	/**
	 * Apply this transformation to all type, function and method declarations
	 * of a given module.
	 */
	public void apply(WyilFile module) throws IOException {
		if(enabled) {
			for(WyilFile.Type type : module.types()) {
				transform(type);
			}
			for(WyilFile.FunctionOrMethod method : module.functionOrMethods()) {
				transform(method);
			}
		}
	}

	/**
	 * Transform the invariant block for a given type declaration (if one
	 * exists).
	 * 
	 * @param type
	 */
	public void transform(WyilFile.Type type) {
		AttributedCodeBlock invariant = type.invariant();

		if (invariant != null) {
			transform(invariant);
		}
	}

	/**
	 * Transform all code blocks found within a given function or method
	 * declaration.
	 * 
	 * @param method
	 */
	public void transform(WyilFile.FunctionOrMethod method) {		
				
		// First, process any preconditions.		
		for(AttributedCodeBlock precondition : method.precondition()) {
			transform(precondition);
		}
		
		// Second, process the method's body (if there is one).
		AttributedCodeBlock body = method.body();
		if(body != null) {
			transform(body);
		}
		
		// Finally, process any postconditions.
		for(AttributedCodeBlock postcondition : method.postcondition()) {
			transform(postcondition);
		}
	}
	
	/**
	 * Traverse all reachable bytecodes in the given block, starting from the
	 * entry point. The traversal corresponds (roughly speaking) to a
	 * depth-first search of the control-flow graph. Bytecodes which are not
	 * visited during this traversal are guaranteed to be unreachable and,
	 * hence, are removed. Observe that, however, some unreachable bytecodes may
	 * remain if their unreachable status depends on more than just reachability
	 * (i.e. an unrealisable path).
	 * 
	 * @param block
	 */
	private void transform(AttributedCodeBlock block) {
		CodeBlock.Index entry = new CodeBlock.Index(CodeBlock.Index.ROOT); 
		// Construct the label map which will label labels to their bytecode
		// indices.  This allows is to correctly handle branching instructions. 
		HashMap<String,CodeBlock.Index> labelMap = new HashMap<String,CodeBlock.Index>();
		buildLabelMap(CodeBlock.Index.ROOT,block,labelMap);
		// Initialise the worklist which will contain the set of bytecode
		// addresses remaining to be explored. Initially, this is populated just
		// with the root index. An invariant of the worklist is that all entries
		// on the worklist have already been added to the visited set.
		Stack<CodeBlock.Index> worklist = new Stack();
		worklist.push(entry);
		// Initialise the visited set, which contains the set of bytecodes which
		// have been explored. When the search is completed, this will tell us
		// which bytecodes are dead-code (i.e. because they weren't explored).
		HashSet<CodeBlock.Index> visited = new HashSet<CodeBlock.Index>();
		visited.add(entry);
		// Now, iterate until all branches of exploration have been explored and
		// the worklist is empty.
		while(!worklist.isEmpty()) {
			CodeBlock.Index index = worklist.pop();
			Code code = block.get(index);

			if(code instanceof Codes.Goto) {
				Codes.Goto g = (Codes.Goto) code;
				addTarget(labelMap.get(g.target),visited,worklist,block);
			} else if(code instanceof Codes.If) {
				Codes.If ig = (Codes.If) code;
				addTarget(index.next(),visited,worklist,block);
				addTarget(labelMap.get(ig.target),visited,worklist,block);
			} else if(code instanceof Codes.IfIs) {
				Codes.IfIs ig = (Codes.IfIs) code;
				addTarget(index.next(),visited,worklist,block);
				addTarget(labelMap.get(ig.target),visited,worklist,block);
			} else if(code instanceof Codes.Switch) {
				Codes.Switch sw = (Codes.Switch) code;
				for(Pair<Constant,String> p : sw.branches) {
					addTarget(labelMap.get(p.second()),visited,worklist,block);
				}
				addTarget(labelMap.get(sw.defaultTarget),visited,worklist,block);
			} else if(code instanceof Codes.Return || code instanceof Codes.Fail) {
				// Terminating bytecodes
			} else if(code instanceof CodeBlock) {
				// This is a block which contains other bytecodes and/or blocks.
				// Therefore, we need to explore the block as well. In all
				// cases, we want to also continue onto the following statement
				// as well.
				addTarget(index.next(),visited,worklist,block);
				addTarget(index.firstWithin(),visited,worklist,block);
			} else {
				// Sequential bytecode, so fall through to next index (if it
				// exists).
				addTarget(index.next(),visited,worklist,block);
			}
		}

		// Now, remove all visited indices from the set of all indices within
		// the block to determine which ones are dead code.
		List<CodeBlock.Index> indices = block.indices();
		indices.removeAll(visited);
		// FIXME: need to actually remove unused bytecodes!		
		System.out.println("FIXME --- DeadCodeElimination");
		// block.removeAll(indices);
	}

	private static void buildLabelMap(CodeBlock.Index parent, CodeBlock block,
			HashMap<String, CodeBlock.Index> map) {
		for (int i = 0; i != block.size(); ++i) {
			Code c = block.get(i);
			if (c instanceof Codes.Label) {
				Codes.Label l = (Codes.Label) c;
				map.put(l.label, new CodeBlock.Index(parent, i));
			} else if (c instanceof CodeBlock) {
				buildLabelMap(new CodeBlock.Index(parent, i), (CodeBlock) c,
						map);
			}
		}
	}

	/**
	 * Added a given bytecode to the list of bytecodes which have been explored.
	 * Furthermore, in the case that the bytecode has not previously been
	 * explored, it is added to the worklist.
	 * 
	 * @param index
	 *            --- The index of the bytecode in question.
	 * @param visited
	 *            --- The list of bytecodes which have been previously explored.
	 * @param worklist
	 *            --- The list of bytecodes which remain to be explored.
	 */
	private static void addTarget(CodeBlock.Index index,
			HashSet<CodeBlock.Index> visited, Stack<CodeBlock.Index> worklist,
			AttributedCodeBlock block) {
		// First, check whether or not this bytecode exists, which is necessary
		// as it won't exist if we've run over the end of a block (for
		// example). Second, check whether or not this bytecode has been visited
		// already as, if so, we don't need to visit it again.
		if(block.contains(index) && !visited.contains(index)) {
			visited.add(index);
			worklist.push(index);
		}
	}
}
