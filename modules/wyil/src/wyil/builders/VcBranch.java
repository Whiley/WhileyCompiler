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

package wyil.builders;

import java.util.Arrays;
import java.util.BitSet;

import wycc.util.Pair;
import wycs.core.Value;
import wycs.syntax.Expr;
import wyil.lang.*;

/**
 * <p>
 * Represents a path through the body of a Wyil method or function. A branch
 * accumulates the constraints known to hold through that particular execution
 * path. These constraints can then be checked for satisfiability at various
 * critical points in the function (i.e. by generating verification conditions).
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
 * Every branch (except the first) has one or more <i>parent</i> branches which
 * it was either forked or joined from. Given any two branches there is always a
 * <i>Least Common Ancestor (LCA)</i> --- that is, the latest point which is
 * common to both branches. Finding the LCA can be useful, for example, to
 * identify constraints common to both branches.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class VcBranch {

	public enum State {
		ACTIVE, // Branch currently progressing through code block
		INTERNAL, // Branch is internal (i.e. has been forked or joined)
		TERMINATED, // Branch reached return statement
		FAILED // Branch reached failed statement
	}

	/**
	 * The parent branch which this branch was forked from, or <code>null</code>
	 * if it is the initial "master" branch for the function or method in
	 * question.
	 */
	private final VcBranch[] parents;

	/**
	 * Maintains the current assignment of variables to expressions.
	 */
	private final Expr[] environment;

	/**
	 * A fixed list of variable "prefixes". These are the names given to each
	 * register slot, and which are then appended with their SSA number to form
	 * an actual register name. Note that the prefixes are fixed for the entire
	 * branch graph of a function / method.
	 */
	private final String[] prefixes;

	/**
	 * For each variable we maintain the current "version". This is an integer
	 * value which is used to determine the appropriate SSA number for a given
	 * variable.
	 */
	final int[] versions;

	/**
	 * Contains the accumulated constraints in the order they were added.
	 */
	private Expr constraints;

	/**
	 * The bytecode index into the above block that this branch is currently at.
	 */
	private CodeBlock.Index pc;

	/**
	 * Indicates the state of this branch. In particular, whether its active,
	 * terminated or failed. A branch is terminated when a return bytecode is
	 * reached. Likewise, a branch is failed when a fail statement is bytecode.
	 * Otherwise, a branch is active.
	 */
	private State state;

	/**
	 * Construct the master verification branch for a given attributed code
	 * block. The pc for the master branch of a block is the root index (i.e.
	 * the branch begins at the entry of the block).
	 *
	 * @param numInputs
	 *            --- the minimum number of register slots required
	 * @param prefixes
	 *            --- Variable names to use as prefixes when generating register
	 *            names. If null, the default names are used instead.
	 */
	public VcBranch(int numInputs, String[] prefixes) {
		int numSlots = numInputs;
		this.parents = new VcBranch[0];
		this.environment = new Expr[numSlots];
		this.versions = new int[numSlots];
		this.constraints = null;
		this.pc = new CodeBlock.Index(CodeBlock.Index.ROOT);
		this.state = State.ACTIVE;

		if (prefixes == null) {
			// Construct default variable prefixes if none are given.
			this.prefixes = new String[numSlots];
			for (int i = 0; i != numSlots; ++i) {
				this.prefixes[i] = "r" + i;
			}
		} else {
			this.prefixes = prefixes;
		}
	}

	/**
	 * Private constructor used for forking a child-branch from a parent branch.
	 *
	 * @param parent
	 *            --- parent branch being forked from.
	 */
	private VcBranch(VcBranch parent) {
		this.parents = new VcBranch[] { parent };
		this.environment = parent.environment.clone();
		this.versions = Arrays.copyOf(parent.versions,
				parent.versions.length);
		this.constraints = null;
		this.prefixes = parent.prefixes;
		this.pc = parent.pc;
		this.state = State.ACTIVE;
	}

	/**
	 * Private constructor used for joining two or more parent branches.
	 *
	 * @param parent
	 *            --- Parent branches being forked from. There must be at least
	 *            two of these.
	 * @param environment
	 *            --- Environment for this branch which is the converged
	 *            environments of all branches.
	 * @param state
	 *            --- State which this branch should be in. This may not be
	 *            active, for example, if none of the parents were active.
	 * 
	 */
	private VcBranch(VcBranch[] parents, Expr[] environment, 
			int[] versions, State state, String[] prefixes) {
		this.parents = parents;
		this.environment = environment;
		this.versions = versions;		
		this.constraints = null;
		this.pc = parents[0].pc;
		this.state = state;
		this.prefixes = prefixes;
	}

	/**
	 * Return the current Program Counter (PC) value for this branch. This must
	 * be a valid index into the code block this branch is operating over.
	 *
	 * @return
	 */
	public CodeBlock.Index pc() {
		return pc;
	}

	/**
	 * Return the parents for this node. Note, the root node has no parents.
	 * 
	 * @return
	 */
	public VcBranch[] parents() {
		return parents;
	}

	/**
	 * Return the constraints which are known to hold on this branch segment,
	 * excluding those which hold on ancestor branches.
	 */
	public Expr constraints() {
		if (constraints == null) {
			return new Expr.Constant(Value.Bool(true));
		} else {
			return constraints;
		}
	}

	public int numSlots() {
		return environment.length;
	}
	
	/**
	 * Determine the current state of this branch. In particular, whether or not
	 * it is ACTIVE.
	 * 
	 * @return
	 */
	public State state() {
		return state;
	}

	/**
	 * Update the state of this branch.
	 * 
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Get the static list of prefixes
	 * 
	 * @return
	 */
	public String[] prefixes() {
		return prefixes;
	}
	
	/**
	 * Update the program counter for this branch. This can, for example, be
	 * used to move the branch to the next logical instruction. Or, it can be
	 * used to jump the branch to an entirely different location.
	 * 
	 * <p>
	 * <b>NOTE:</b>The branch must be ACTIVE for this operation to be permitted,
	 * since it changes the state of the branch.
	 * </p>
	 * 
	 * @param pc
	 */
	public void goTo(CodeBlock.Index pc) {
		if (state != State.ACTIVE) {
			// Sanity check
			throw new IllegalArgumentException(
					"Attempt to modify an inactive branch");
		}
		this.pc = pc;
	}

	/**
	 * Get the constraint variable which corresponds to the given Wyil bytecode
	 * register at this point on this branch.
	 *
	 * @param register
	 * @return
	 */
	public Expr read(int register) {
		return environment[register];
	}

	/**
	 * Assign an expression to a given Wyil bytecode register. This stores the
	 * assigned expression for recall when the given register is subsequently
	 * read.
	 * <p>
	 * <b>NOTE:</b>The branch must be ACTIVE for this operation to be permitted,
	 * since it changes the state of the branch.
	 * </p>
	 * 
	 * @param register
	 *            --- Register being written.
	 * @param expr
	 *            --- Expression being assigned.
	 */
	public void write(int register, Expr expr) {
		if (state != State.ACTIVE) {
			// Sanity check
			throw new IllegalArgumentException(
					"Attempt to modify an inactive branch");
		}
		versions[register]++;
		environment[register] = expr;
	}

	/**
	 * Terminate the current flow for a given register and begin a new one. In
	 * terms of static-single assignment, this means simply change the index of
	 * the register in question. This is also known in the verification
	 * community as "havocing" the variable, or sending the variable to "havoc".
	 * <p>
	 * <b>NOTE:</b>The branch must be ACTIVE for this operation to be permitted,
	 * since it changes the state of the branch.
	 * </p>
	 * 
	 * @param register
	 *            Register number to havoc
	 * @param type
	 *            Type of register being havoced
	 */
	public Expr.Variable havoc(int register) {
		if (state != State.ACTIVE) {
			// Sanity check
			throw new IllegalArgumentException(
					"Attempt to modify an inactive branch");
		}		
		// to invalidate a variable, we assign it a "skolem" constant. That is,
		// a fresh variable which has not been previously encountered in the
		// branch.			
		versions[register] = versions[register] + 1;			
		String prefix = prefixes[register] == null ? "r%" + register : prefixes[register];		
		Expr.Variable var = new Expr.Variable(prefix + "$"
				+ versions[register]);
		environment[register] = var;	
		return var;
	}

	/**
	 * Assume a given condition holds on this branch.
	 * 
	 * <p>
	 * <b>NOTE:</b>The branch must be ACTIVE for this operation to be permitted,
	 * since it changes the state of the branch.
	 * </p>
	 * 
	 * @param e
	 *            The condition to assume
	 */
	public void assume(Expr e) {
		if (state != State.ACTIVE) {
			// Sanity check
			throw new IllegalArgumentException(
					"Attempt to modify an inactive branch");
		}
		if (constraints == null) {
			constraints = e;
		} else {
			constraints = new Expr.Binary(Expr.Binary.Op.AND, constraints, e);
		}
	}

	/**
	 * <p>
	 * Fork this branch into a child branch with this branch as parent. This
	 * branch enters the INTERNAL state and is now immutable (otherwise, changes
	 * to this branch would change its children as well). The child branch is
	 * (initially) identical to this branch in every way. However, the
	 * expectation is that it will diverge as verification condition generation
	 * progresses. Finally, multiple children may be forked from the same
	 * parent.
	 * </p>
	 *
	 * <pre>
	 *    B1
	 *    ||
	 *    ||
	 *    ##    <- origin
	 *    | \
	 *    ||\\
	 *    || \\
	 *    \/  \/
	 *    B1  B2
	 * </pre>
	 * <p>
	 * The origin for the forked branch is the <code>PC</code value at the split
	 * point. Initially, the <code>PC</code> value for the forked branch is
	 * identical to that of the parent, however it is expected that a
	 * <code>goTo</code> will be used immediately after the fork to jump the
	 * child branched to their logical starting point.
	 * </p>
	 *
	 * @return --- The child branch which is forked off this branch.
	 */
	public VcBranch fork() {
		// Mark this branch as having been forked and, hence, it is now
		// inactive.
		this.state = State.INTERNAL;
		// Construct the two child branch
		return new VcBranch(this);
	}

	/**
	 * <p>
	 * Join two (or more) branches together to form a single active branch with
	 * two (or more) parents. The parent branches enter the INTERNAL state and,
	 * hence, become immutable (since, otherwise, changes to them would affect
	 * their children).
	 * </p>
	 * 
	 * <pre>
	 *        B1 	  B2
	 *        ||      ||
	 *         \\    //
	 *          \\  //
	 *           \\//
	 *            ##
	 *            B3
	 * </pre>
	 * <p>
	 * An important concern is how the environment in the child branch is
	 * constructed from its parents. The issue being that the environments in
	 * the parent branches may diverged. HOW TO RESOLVE THIS??
	 * </p>
	 * 
	 * @param parent
	 *            --- The parent branches which are being joined with this one.
	 *            These must all have the same PC value as this branch.
	 */
	public VcBranch join(VcBranch... parents) {
		// Quick sanity check that all branches being joined have same pc
		// location and are in the same state.
		State parentState = this.state;
		for (VcBranch parent : parents) {
			if (!pc.equals(parent.pc())) {
				throw new IllegalArgumentException(
						"Attempt to join parents at different locations");
			} else if (!state.equals(parent.state())) {
				throw new IllegalArgumentException(
						"Attempt to join parents in different states");
			}
		}
		// Mark all branches involved in the join as internal. This
		// effectively renders them immutable from now on, thereby preventing
		// changes which could affect the joined branch or its children.
		for (VcBranch parent : parents) {
			parent.state = State.INTERNAL;
		}
		this.state = State.INTERNAL;

		// Construct the array of all branches, including this.
		VcBranch[] nparents = new VcBranch[parents.length + 1];
		System.arraycopy(parents, 0, nparents, 1, parents.length);
		nparents[0] = this;

		// Converge versions between the different parents. This is done
		// simply by calculating the max subscript for each variable across all
		// environments.		
		int[] nVersions = convergeVersions(nparents);
		
		// Converge environments to ensure obtain a single environment which
		// correctly represents the environments of all branches being joined.
		// In some cases, individual environments may need to be patched to get
		// them all into identical states.
		Expr[] nEnvironment = convergeEnvironments(nparents, nVersions);

		// Finally, create the new branch representing the join of all branches,
		// and with those branches as its declared parents.
		return new VcBranch(nparents, nEnvironment, nVersions, parentState,
				prefixes);
	}

	/**
	 * Converge the versions across all parent branches. This is done by
	 * calculating the max subscript for each variable across all environments.
	 * For example:
	 * 
	 * <pre>
	 * Branch 1:   Branch 2:
	 * 
	 * |0|1|2|3|   |0|1|2|3|
	 * =========   =========
	 * |2|2|3|2|   |2|1|4|2|
	 * </pre>
	 * 
	 * Here, the register slot number if given on top, and then subscript is
	 * given below. The converged subscripts array would then be:
	 * 
	 * <pre>
	 * |0|1|2|3|
	 * =========
	 * |2|2|4|2|
	 * </pre>
	 * 
	 * Here, for example, slot 0 retains subscript 2 as this is true for both
	 * branches. However, slot 2 is given subscript 4 as this is the maximum
	 * subscript for that slot across both branches.
	 * 
	 * @param parents
	 * @return
	 */
	private int[] convergeVersions(VcBranch[] parents) {
		int[] nVersions = new int[versions.length];
		for (int i = 0; i != parents.length; ++i) {
			int[] pSubscripts = parents[i].versions;
			for (int j = 0; j != nVersions.length; ++j) {
				nVersions[j] = Math.max(nVersions[j], pSubscripts[j]);
			}
		}
		return nVersions;
	}

	/**
	 * Convert environments from one or more branches together. The key
	 * challenge here lies with variables that have diverged between branches.
	 * For example, consider converging two branch environments:
	 * 
	 * <pre>
	 * Branch 1:   Branch 2:
	 * 
	 * |0|1|2|3|   |0|1|2|3|
	 * =========   =========
	 * |X|Y|Z|/|   |X|Z|/|/|
	 * </pre>
	 * 
	 * <p>
	 * Here, we see the four main cases to be concerned with. Firstly, register
	 * 0 has the same value on both branches (namely, X) and, hence, will have
	 * this same value in the final joined branch. Register 1 has different
	 * values in both branches and will need to be patched. Register 2 has null
	 * on one branch signalling it is undefined on that branch and, hence, will
	 * be undefined in the resulting joined branch. Finally, register 3 is
	 * undefined on both branches and hence will be undefined in the final
	 * joined branch as well.
	 * </p>
	 * 
	 * <p>
	 * Before joining the above two branches they need to be patched to ensure
	 * register 1 has the same value on both branches. This is done by creating
	 * a new variable to store the value from each branch. The following
	 * illustrates the two patched environments (which are children of the two
	 * above):
	 * </p>
	 * 
	 * <pre>
	 * Branch 3:   Branch 4:
	 * parent: 1   parent: 2
	 * 
	 * |0|1|2|3|   |0|1|2|3|
	 * =========   =========
	 * |X|W|Z|/|   |X|W|/|/|
	 * 
	 * (W == Y)    (W == Z)
	 * </pre>
	 * 
	 * <p>
	 * Below the patched branches we can see the constraints which have been
	 * assumed to ensure information from both branches is correctly retained.
	 * The finaly joined branch is thus:
	 * </p>
	 * 
	 * <pre>
	 * Branch 5:
	 * parents: 3,4
	 * 
	 * |0|1|2|3|
	 * =========
	 * |X|W|/|/|
	 * </pre>
	 * 
	 * Finally, it is worth noting that in the special case that no variables
	 * need to be patched, then no patch environments need to be created.
	 * 
	 * @param branches
	 *            --- Branches whose environments are to be converged.
	 * @param versions
	 *            --- the converged set of subscripts
	 * 
	 * @return
	 */
	private Expr[] convergeEnvironments(VcBranch[] branches, int[] versions) {
		Expr[] newEnvironment = Arrays.copyOf(environment, environment.length);
		// First, go through and find all registers whose values differ between
		// parents. These registers will need to be patched.
		Pair<BitSet,BitSet> pvs = determinePatchVariables(newEnvironment, branches);
		BitSet toPatch = pvs.first();
		BitSet toNull = pvs.second();
		// Second, patch any registers which were marked in previous phase. Such
		// registers are given new names which are common to all branches, and
		// appropriate assumptions are added to connect the old register names
		// with the new.
		if (toPatch.isEmpty() && toNull.isEmpty()) {
			// This is the special case where there are no variables needing to
			// be patched or nulled.
			return newEnvironment;
		} else {
			// In this case, there is at least one varaible which needs to be
			// patched. First, we go through and fork all branches to create the
			// patch branches.
			for (int j = 0; j != branches.length; ++j) {
				branches[j] = branches[j].fork();
			}
			// Now, patch each variable which is marked for patching.
			for (int i = 0; i != newEnvironment.length; ++i) {
				if(toNull.get(i)) {
					// This register needs to be nulled since it was undefined
					// in one or more of the source branches. This check needs
					// to come before the patch check, because a variable can be
					// marked as both toNull and toPatch. In such case, it
					// should be nulled.
					newEnvironment[i] = null;
				} else if (toPatch.get(i)) {
					// This register needs to be patched. First, check whether
					// this register has a prefix or not.
					String prefix = prefixes[i] == null ? "r%" + i : prefixes[i];
					Expr.Variable var = new Expr.Variable(prefix + "$" + versions[i]);
					for (int j = 0; j != branches.length; ++j) {
						branches[j].assume(new Expr.Binary(Expr.Binary.Op.EQ,
								var, branches[j].read(i)));
					}
					newEnvironment[i] = var;
				} 
			}
			// Done
			return newEnvironment;
		}
	}

	/**
	 * Determine which variables differ between two or more branches. Such
	 * variables need to be "patched" or "nulled".
	 * 
	 * @param environment
	 * @param parents
	 * @return
	 */
	private static Pair<BitSet,BitSet> determinePatchVariables(Expr[] environment,
			VcBranch[] parents) {
		BitSet toPatch = new BitSet(environment.length);
		BitSet toNull = new BitSet(environment.length);
		for (int i = 0; i != environment.length; ++i) {
			environment[i] = parents[0].environment[i];
			for (int j = 0; j != parents.length; ++j) {
				VcBranch parent = parents[j];
				if (environment[i] == null || parent.environment[i] == null) {
					// In this case, the variable is undefined on one or both
					// parents. This means it cannot be used after this point
					// and, hence, can be safey ignored.
					toNull.set(i);
				} else if (!parent.environment[i].equivalent(environment[i])) {
					// In this case, there is some difference between this parent's
					// environment and at least one others. In such case, the
					// variable in question needs to be patched.
					toPatch.set(i);
				}
			}
		}
		return new Pair<BitSet,BitSet>(toPatch,toNull);
	}

}
