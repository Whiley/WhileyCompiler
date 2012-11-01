// Copyright (c) 2012, David J. Pearce (djp@ecs.vuw.ac.nz)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wyil.lang.Block;
import wyone.core.Automaton;

/**
 * <p>
 * Represents a path through the body of a Wyil method or function. A branch
 * accumulates the constraints known to hold through that particular execution
 * path. These constraints can then be checked for satisfiability at various
 * critical points in the function.
 * </p>
 * <p>
 * When verifying a given function or method, the verifier starts with a single
 * branch at the beginning of the method. When split points in the control-flow
 * graph are encountered, branches are accordingly forked off to represent the
 * alternate control-flow path. At control-flow meet points, branches may also
 * be joined back together (although this is not always strictly necessary). A
 * diagrammatic view might be:
 * </p>
 * 
 * <pre>
 *  entry
 *   ||
 *   ||
 *   ##\
 *   ||\\
 *   || \\
 *   || || 
 *   || ##\
 *   || ||\\
 *   ||//  \\
 *   ##/   ||
 *   ||    ||
 *   \/    \/
 *   B1    B3
 * </pre>
 * <p>
 * In the above example, we initially start with one branch <code>B1</code>.
 * This is then forked to give branch <code>B2</code> which, in turn, is forked
 * again to give <code>B3</code>. Subsequently, branch <code>B2</code> is joined
 * back with <code>B1</code>. However, <code>B3</code> is never joined and
 * terminates separately.
 * </p>
 * <p>
 * Every branch (except the first) has a <i>parent</i> branch which it was
 * forked from. Given any two branches there is always a <i>Least Common
 * Ancestor (LCA)</i> --- that is, the latest point which is common to both
 * branches. Finding the LCA can be useful, for example, to identify constraints
 * common to both branches.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class VerificationBranch {
	/**
	 * The parent branch which this branch was forked from, or <code>null</code>
	 * if it is the initial "master" branch for the function or method in
	 * question.
	 */
	private final VerificationBranch parent;

	/**
	 * Maintains the master map of variables to unique identifiers for
	 * allocating new SSA variable indices. This is shared amongst all branches
	 * which originate from a given master (hence
	 * <code>parent == null || registry == parent.registry</code> must hold).
	 */
	private final int[] registry;

	/**
	 * Maintains the current static single assignment index for each variable in
	 * the method or function. Note,
	 * <code>environment.length == registry.length</code> must hold.
	 */
	private final int[] environment;

	/**
	 * Provides a prefix for all variables written or read on this branch. This
	 * is primarily useful for ensuring that constraints generated from external
	 * blocks (e.g. post-conditions, loop invariants, etc) can be safely merged
	 * into the outermost block.
	 */
	private final String prefix;

	/**
	 * The automaton into which all constraints are constructed and which will
	 * eventually be used to check for (un)satisfiability.
	 */
	private final Automaton automaton;

	/**
	 * The list of constraints which are known to be true at this point on the
	 * branch. Note that these are indexes into the automaton above.
	 */
	private final ArrayList<Integer> constraints;

	/**
	 * The block of Wyil bytecode instructions which this branch is traversing
	 * (note: <code>parent == null || block == parent.block</code> must hold).
	 */
	private final Block block;

	/**
	 * The origin determines the bytecode offset in block where this branch was
	 * forked from. For the master branch, this will be <code>0</code>.
	 */
	private final int origin;

	/**
	 * The bytecode index into the above block that this branch is currently at.
	 */
	private int pc;

	/**
	 * Construct the master verification branch for a given code block.
	 * 
	 * @param automaton
	 * @param block
	 */
	public VerificationBranch(Automaton automaton, Block block) {
		this(null, new int[block.numSlots()], new int[block.numSlots()], "",
				automaton, Collections.EMPTY_LIST, block, 0, 0);
	}

	private VerificationBranch(VerificationBranch parent, int[] registry,
			int[] environment, String prefix, Automaton automaton,
			List<Integer> constraints, Block block, int origin, int pc) {
		this.parent = parent;
		this.registry = registry;
		this.environment = environment;
		this.prefix = prefix;
		this.automaton = automaton;
		this.constraints = new ArrayList<Integer>(constraints);
		this.block = block;
		this.origin = origin;
		this.pc = pc;
	}

	/**
	 * Return the current Program Counter (PC) value for this branch.
	 * 
	 * @return
	 */
	public int pc() {
		return pc;
	}

	/**
	 * Reposition the Program Counter (PC) for this branch.
	 * 
	 * @param pc
	 */
	public void goTo(int pc) {
		this.pc = pc;
	}

	/**
	 * Check that this verification branch is indeed valid.
	 */
	public void validate() {

	}
	
	public int transform() {
		
	}
	
	/**
	 * Merge another branch into this one, such that the resulting branch
	 * captures the constraints from either incoming branch (i.e. this
	 * represents a meet-point in the control-flow graph).
	 * 
	 * @param other
	 */
	public void join(VerificationBranch other) {
		
	}
}
