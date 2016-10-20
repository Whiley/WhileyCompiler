// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.checks;

import java.util.*;

import wybs.lang.Build;
import wybs.lang.SyntaxError;
import wycc.util.Pair;
import wyil.lang.*;
import wyil.lang.Bytecode.*;
import wyil.lang.SyntaxTree.Location;

import static wyil.util.ErrorMessages.*;

/**
 * <p>
 * Performs a number of simplistic checks that a module is syntactically
 * correct. This includes the following
 * </p>
 * <ul>
 * <li><b>Functions cannot have side-effects</b>. This includes sending messages
 * to actors, calling headless methods and spawning processes.</li>
 * <li><b>Functions/Methods cannot throw exceptions unless they are
 * declared</b>. Thus, if a method or function throws an exception an
 * appropriate throws clause is required.
 * <li><b>Every catch handler must catch something</b>. It is a syntax error if
 * a catch handler exists which can never catch anything (i.e. it is dead-code).
 * </li>
 * </ul>
 *
 * @author David J. Pearce
 *
 */
public class ModuleCheck implements Build.Stage<WyilFile> {
	private WyilFile file;

	public ModuleCheck(Build.Task builder) {

	}

	@Override
	public void apply(WyilFile module) {
		this.file = module;

		// FIXME: check type invariants

		for (WyilFile.FunctionOrMethod method : module.functionOrMethods()) {
			check(method);
		}
	}

	public void check(WyilFile.FunctionOrMethod method) {
		// FIXME: check pre/postconditions
		if(method.isFunction()) {
			checkFunctionPure(method);
		}
	}

	/**
	 * Check a given function is pure. That is all invocations within the
	 * function are to themselves to pure functions, and no heap memory is used.
	 *
	 * @param c
	 */
	protected void checkFunctionPure(WyilFile.FunctionOrMethod c) {
		checkFunctionPure(0,new HashSet<Integer>(),c.getTree());
	}

	protected void checkFunctionPure(int blockID, HashSet<Integer> visited, SyntaxTree enclosing) {
		visited.add(blockID);
		Location<Bytecode.Block> block = (Location<Bytecode.Block>) enclosing.getLocation(blockID);
		for (int i = 0; i != block.numberOfOperands(); ++i) {
			Location<?> e = block.getOperand(i);
			Bytecode code = e.getBytecode();
			if (code instanceof Bytecode.Invoke && ((Bytecode.Invoke) code).type() instanceof Type.Method) {
				// internal message send
				throw new SyntaxError(errorMessage(METHODCALL_NOT_PERMITTED_IN_FUNCTION), file.getEntry(), e);
			} else if (code instanceof Bytecode.IndirectInvoke
					&& ((Bytecode.IndirectInvoke) code).type() instanceof Type.Method) {
				throw new SyntaxError(errorMessage(METHODCALL_NOT_PERMITTED_IN_FUNCTION), file.getEntry(), e);
			} else if (code.getOpcode() == Bytecode.OPCODE_newobject) {
				throw new SyntaxError(errorMessage(ALLOCATION_NOT_PERMITTED_IN_FUNCTION), file.getEntry(), e);
			} else if (code.getOpcode() == Bytecode.OPCODE_dereference) {
				throw new SyntaxError(errorMessage(REFERENCE_ACCESS_NOT_PERMITTED_IN_FUNCTION), file.getEntry(), e);
			} else if (code instanceof Bytecode.Stmt) {
				Bytecode.Stmt a = (Bytecode.Stmt) code;
				for (int j = 0; j != a.numberOfBlocks(); ++j) {
					int subblock = a.getBlock(j);
					// The visited check is necessary to handle break and
					// continue bytecodes. These contain the block identifier of
					// their enclosing loop and, hence, following this would
					// lead to an infinite loop.
					if (!visited.contains(subblock)) {
						checkFunctionPure(subblock, visited, enclosing);
					}
				}
			}
		}
	}
}
