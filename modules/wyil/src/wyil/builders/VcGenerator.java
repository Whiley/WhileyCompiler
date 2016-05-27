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

import wycc.lang.SyntaxError.InternalFailure;
import wycc.lang.SyntaxError;
import static wyil.util.ErrorMessages.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.*;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.attributes.VariableDeclarations;
import wyil.builders.VcBranch.State;
import wyil.lang.*;
import wyil.lang.BytecodeForest.Index;
import wyil.util.ErrorMessages;
import wyil.util.TypeExpander;
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wycs.core.SemanticType;
import wycs.core.Value;
import wycs.syntax.*;
import wycs.syntax.Expr.Is;
import wycs.syntax.TypePattern.Tuple;

/**
 * Responsible for converting a given Wyil bytecode into an appropriate
 * constraint which encodes its semantics.
 *
 * @author David J. Pearce
 *
 */
public class VcGenerator {
	private final Builder builder;
	private final TypeExpander expander;
	private VcExprGenerator unitGen;
	private VcUtils utils;
	private String filename;
	private WyalFile wyalFile;
	WyilFile.FunctionOrMethod method;

	public VcGenerator(Builder builder) {
		this.builder = builder;
		this.expander = new TypeExpander(builder.project());
	}

	// ===============================================================================
	// Top-Level Controller
	// ===============================================================================

	protected WyalFile transform(WyilFile wyilFile) {
		filename = wyilFile.filename();
		utils = new VcUtils(filename,builder,expander);
		unitGen = new VcExprGenerator(filename,builder,utils);
		wyalFile = new WyalFile(wyilFile.id(), filename);
		addImports();
		
		for (WyilFile.Block b : wyilFile.blocks()) {
			if (b instanceof WyilFile.Constant) {
				transform((WyilFile.Constant) b, wyilFile);
			} else if (b instanceof WyilFile.Type) {
				transform((WyilFile.Type) b, wyilFile);
			} else if (b instanceof WyilFile.FunctionOrMethod) {
				WyilFile.FunctionOrMethod method = (WyilFile.FunctionOrMethod) b;				
				transform(method, wyilFile);				
			}
		}

		return wyalFile;
	}

	/**
	 * Add necessary imports from the theorem prover's library.
	 */
	private void addImports() {
	}
	
	protected void transform(WyilFile.Constant decl, WyilFile wyilFile) {
		NameID name = new NameID(wyilFile.id(), decl.name());
	}

	/**
	 * Transform a type declaration into verification conditions as necessary.
	 * In particular, the type should be "inhabitable". This means, for example,
	 * that the invariant does not contradict itself. Furthermore, we need to
	 * transform the type invariant into a macro block.
	 * 
	 * @param typeDecl
	 * @param wyilFile
	 */
	protected void transform(WyilFile.Type typeDecl, WyilFile wyilFile) {
		BytecodeForest forest = typeDecl.invariant();
		Expr invariant = null;
		// FIXME: get the register prefix!
		Expr.Variable var = new Expr.Variable("r0");
		if (forest.numBlocks() > 0) {
			BytecodeForest.Index root = new BytecodeForest.Index(forest.getRoot(0), 0);
			VcBranch master = new VcBranch(Math.max(1, forest.numRegisters()), root, null);
			master.write(0, var);
			// Pass the given branch through the type invariant, producing
			// exactly one exit branch from which we can generate the invariant
			// expression.
			Type[] environment = new Type[] { typeDecl.type() };
			List<VcBranch> exitBranches = transform(master, root, true, environment, forest);
			// At this point, we are guaranteed exactly one exit branch because
			// there is only ever one exit point from an invariant.
			for (VcBranch exitBranch : exitBranches) {
				if (exitBranch.state() == VcBranch.State.TERMINATED) {
					invariant = generateAssumptions(exitBranch, null);
					break;
				}
			}

			// FIXME: Need to add the inhabitable check here. This has to be an
			// existential quantifier.
		}

		TypePattern.Leaf pattern = new TypePattern.Leaf(utils.convert(typeDecl.type(), Collections.EMPTY_LIST), var);

		wyalFile.add(wyalFile.new Type(typeDecl.name(), Collections.EMPTY_LIST,
				pattern, invariant, VcUtils.toWycsAttributes(typeDecl.attributes())));
	}

	protected void transform(WyilFile.FunctionOrMethod method, WyilFile wyilFile) {

		this.method = method;

		Type.FunctionOrMethod fmm = method.type();
		BytecodeForest forest = method.code();
		int[] preconditions = method.preconditions();
		int[] postconditions = method.postconditions();				
		// First, translate pre- and post-conditions into macro blocks. These
		// can then be used in various places to assume or enforce pre /
		// post-conditions. For example, when ensure a pre-condition is met at
		// an invocation site, we can call this macro directly.
		String prefix = method.name() + "_requires_";
		for (int i = 0; i != preconditions.length; ++i) {
			BytecodeForest.Index pc = new BytecodeForest.Index(preconditions[i], 0);
			buildMacroBlock(prefix + i, pc, forest, fmm.params(), true);
		}
		prefix = method.name() + "_ensures_";
		List<Type> postEnvironment = append(fmm.params(), fmm.returns());		
		for (int i = 0; i != postconditions.length; ++i) {
			BytecodeForest.Index pc = new BytecodeForest.Index(postconditions[i], 0);
			buildMacroBlock(prefix + i, pc, forest, postEnvironment, true);
		}

		// Finally, add a function representing this function or method.
		utils.createFunctionPrototype(wyalFile, method.name(), fmm.params(), fmm.returns());

		if (method.hasModifier(Modifier.NATIVE)) {
			// We don't consider native methods because they have empty bodies,
			// and attempting to pass these through to the verification
			// condition generator will cause problems.
			return;
		}

		Pair<String[], Type[]> registerInfo = VcUtils.parseRegisterDeclarations(forest);
		String[] prefixes = registerInfo.first();
		Type[] bodyEnvironment = registerInfo.second();
		// Construct the master branch and initialise all parameters with their
		// declared types in the master branch. The master branch needs to have
		// at least as many slots as there are parameters, though may require
		// more if the body uses them.
		BytecodeForest.Index pc = new BytecodeForest.Index(method.body(), 0);
		VcBranch master = new VcBranch(Math.max(forest.numRegisters(), fmm.params().size()), pc, prefixes);

		Expr[] arguments = new Expr[fmm.params().size()];
		for (int i = 0; i != fmm.params().size(); ++i) {
			Expr.Variable v = new Expr.Variable(prefixes[i]);
			master.write(i, v);
			arguments[i] = v;
		}

		// Second, assume all preconditions. To do this, we simply invoke the
		// precondition macro for each one.
		prefix = method.name() + "_requires_";
		for (int i = 0; i != preconditions.length; ++i) {
			Expr arg = arguments.length == 1 ? arguments[0] : new Expr.Nary(
					Expr.Nary.Op.TUPLE, arguments);
			Expr.Invoke macro = new Expr.Invoke(prefix + i, wyilFile.id(),
					Collections.EMPTY_LIST, arg);
			master.assume(macro);
		}

		// Traverse the function or method's body. This can produce potentially
		// many terminated and failed branches, though none should still be
		// active. Terminated branches are those which have reached a return
		// statement, whilst failed branches are those which have reached a fail
		// statement.
		List<VcBranch> exitBranches = transform(master, pc, false, bodyEnvironment, forest);

		// Examine all branches produced from the body. Each should be in one of
		// two states: terminated or failed. Failed states indicate some
		// internal assertion was not met (for example, a loop invariant was not
		// restored, etc). In contrast, terminated states indicate those which
		// have successfully reached the end of the function. For these cases,
		// we need to then check the post-condition.
		for (VcBranch branch : exitBranches) {
			switch (branch.state()) {
			case FAILED: {
				// This is a failed branch state. In this case, we construct a
				// verification condition which enforces that the constraints
				// leading up to this position cannot hold. In other words, that
				// this is an unreachable path.
				Expr vc = buildVerificationCondition(new Expr.Constant(Value.Bool(false)), branch, bodyEnvironment,
						forest);
				wyalFile.add(wyalFile.new Assert("assertion failed", vc,
						VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes())));
				break;
			}
			case TERMINATED: {
				if (fmm.returns().isEmpty()) {
					// In this case, there is not return value and, hence, there
					// is no need to ensure the postcondition holds.
				} else {
					List<wyil.lang.Attribute> attributes = forest.get(branch.pc()).attributes();
					Collection<wycc.lang.Attribute> wycsAttributes = VcUtils.toWycsAttributes(attributes);
					// Find the return statement in question
					Bytecode.Return ret = (Bytecode.Return) forest.get(branch.pc()).code();
					// Construct verification check to ensure that return
					// type invariant holds
					// FIXME: need proper support for multiple returns
					Expr returnedOperand = branch.read(ret.operand(0));					
					Type rawType = expand(bodyEnvironment[ret.operand(0)],attributes);
					Expr rawTest = new Expr.Is(returnedOperand,
							utils.convert(rawType, attributes));
					// FIXME: needs to handle all returns
					if (utils.containsNominal(fmm.returns().get(0),attributes)) {
						// FIXME: we need the raw test here, because the
						// verifier can't work out the type of the expression
						// otherwise.						
						Expr nominalTest = new Expr.Is(returnedOperand,
								utils.convert(fmm.returns().get(0), attributes));
						Expr vc = buildVerificationCondition(nominalTest,
								branch, bodyEnvironment, forest, rawTest);
						// FIXME: add contextual information here
						wyalFile.add(wyalFile.new Assert(
								"return type invariant not satisfied", vc,
								wycsAttributes));
					}
					// Construct arguments for the macro invocation. Only
					// the returned value is read from the branch at the current
					// pc. The other arguments correspond to the parameters
					// which held on entry to this function.
					arguments = new Expr[fmm.params().size() + 1];
					for (int i = 0; i != fmm.params().size(); ++i) {
						arguments[i] = new Expr.Variable(prefixes[i]);
					}
					
					arguments[fmm.params().size()] = returnedOperand;
					// For each postcondition generate a separate
					// verification condition. Doing this allows us to gather
					// more detailed context information in the case of a
					// failure about which post-condition is failing.
					prefix = method.name() + "_ensures_";
					for (int i = 0; i != postconditions.length; ++i) {
						Expr arg = arguments.length == 1 ? arguments[0] : new Expr.Nary(Expr.Nary.Op.TUPLE, arguments);
						Expr.Invoke macro = new Expr.Invoke(prefix + i, wyilFile.id(), Collections.EMPTY_LIST, arg);
						Expr vc = buildVerificationCondition(macro, branch, bodyEnvironment, forest, rawTest);
						// FIXME: add contextual information here
						wyalFile.add(wyalFile.new Assert("postcondition not satisfied", vc,
								VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes())));
					}
				}
				break;
			}
			default:
				// This should be impossible to reach!
				internalFailure("unreachable code reached! (" + branch.state() + ")", filename,
						forest.get(branch.pc()).attributes());
			}
		}
	}

	// ===============================================================================
	// Block controller
	// ===============================================================================

	/**
	 * Transform a branch through a given outermost block, producing the
	 * constraints which hold at the end. In the case of a function or method
	 * body, every branch will be terminated with a return statement and, in
	 * this case only, this will return null.
	 * 
	 * @param branch
	 *            Branch state going into the block
	 * @param block
	 *            Block being transformed over
	 * 
	 * @return List of branches which reach the end of the block.
	 */
	public List<VcBranch> transform(VcBranch branch, BytecodeForest.Index root, boolean isInvariant, Type[] environment,
			BytecodeForest forest) {
		// Construct the label map which is needed for conditional branches
		Map<String, BytecodeForest.Index> labels = Bytecode.buildLabelMap(forest);
		Pair<VcBranch, List<VcBranch>> p = transform(root.block(), 0, null, branch, false, isInvariant, environment,
				labels, forest);
		// Ok, return list of exit branches
		return p.second();
	}

	/**
	 * <p>
	 * Transform a given branch over a block of zero or more statements. In the
	 * case of a straight-line sequence, this is guaranteed to produce at most
	 * one outgoing branch (zero is possible if sequence includes a return). For
	 * an instruction sequence with one or more conditional branches, multiple
	 * branches may be produced representing the different possible traversals.
	 * </p>
	 * <p>
	 * This function symbolically executes each branch it is maintaining until
	 * it either terminates (e.g. returns), or leaves the block. Furthermore,
	 * branches which terminate at the same pc are joined together. This ensures
	 * that, for example, only one branch is returned in the case of blocks with
	 * single exit paths.
	 * </p>
	 * 
	 * @param parent
	 *            The index in the root block of the given block being iterated
	 *            over.
	 * @param offset
	 *            The offset within the block to start from. In most cases, this
	 *            is zero (i.e. the start of the block). However, to support
	 *            loop invariants, we need the ability to restart a block from
	 *            just after the invariant.
	 * @param entryState
	 *            The initial state on entry to the block. This is assumed to be
	 *            located at the first instruction of the block.
	 * @param breakOnInvariant
	 *            With this flag enabled, the transformer will continue looping
	 *            around the block until an invariant bytecode is found, at
	 *            which point it will break. If no invariant is found, this will
	 *            loop forever.
	 * @param environment
	 *            The mapping of registers to their declared types.
	 * @param labels
	 *            The map from labels to their block locations
	 * @param block
	 *            The block being transformed over.
	 * @return A pair consisting of: the active branch (if any) which has fallen
	 *         through the end of the block and is now located at the
	 *         instruction following the parent; and, the list of zero or more
	 *         branches which have terminated or failed. Note, when there is no
	 *         parent, then there can be no fall-through either and, instead,
	 *         this is marked as terminated.
	 * 
	 */
	protected Pair<VcBranch, List<VcBranch>> transform(int block, int offset, BytecodeForest.Index parent,
			VcBranch entryState, boolean breakOnInvariant, boolean isInvariant, Type[] environment,
			Map<String, BytecodeForest.Index> labels, BytecodeForest forest) {
		// Move state to correct location
		entryState.goTo(new BytecodeForest.Index(block, offset));
		BytecodeForest.Block blk = forest.get(block);
		// Construct list of branches being processed.
		Stack<VcBranch> worklist = new Stack<VcBranch>();
		ArrayList<VcBranch> exitBranches = new ArrayList<VcBranch>();
		ArrayList<VcBranch> fallThruBranches = new ArrayList<VcBranch>();		
		worklist.push(entryState);
		// Process all branches in the worklist until it is empty and there are
		// none left to process.
		while (worklist.size() > 0) {
			VcBranch branch = worklist.pop();

			// The program counter represents the current position of the branch
			// being explored.
			BytecodeForest.Index pc = branch.pc();
			// Determine whether to continue executing this branch, or whether
			// it has completed within this scope.
			if (pc.block() != block || branch.state() != VcBranch.State.ACTIVE) {
				// This indicates the given branch has either exited this block
				// via a non-local branch, or terminated in some fashion.
				// Therefore, this branch is moved into the list of exit
				// branches.
				exitBranches.add(branch);
			} else if (pc.block() == block && pc.offset() >= blk.size()) {
				// This indicates the given branch has exited this block by
				// falling through. We now need to check for breakOnInvariant.
				if (breakOnInvariant) {
					// Break On Invariant is enabled, therefore continue going
					// around.
					branch.goTo(parent);
				} else if (parent != null) {
					// No reset, allow to exit branch as normal. First, set
					// branch location to the next instruction following the
					// parent instruction containing this block.
					branch.goTo(parent.next());
				} else {
					internalFailure("unreachable code reached!", filename, forest.get(pc).attributes());
				}
				fallThruBranches.add(branch);
			} else {
				// Continue executing this branch as it is still within the
				// scope of this block.
				Bytecode code = forest.get(pc).code();
				// Now, dispatch statements. Control statements are treated
				// specially from unit statements.
				if (code instanceof Bytecode.AssertOrAssume) {
					if (breakOnInvariant && code instanceof Bytecode.Invariant) {
						// In this special case, we have reached the invariant
						// bytecode and, hence, we break out of this loop. This
						// is needed for handling loop invariants where we need
						// to do special things when the invariant is
						// encountered.
						fallThruBranches.add(branch);
					} else {
						boolean isAssert = code instanceof Bytecode.Assert;
						Pair<VcBranch, List<VcBranch>> p = transform((Bytecode.AssertOrAssume) code, isAssert, branch,
								environment, labels, forest);
						if(p.first() != null) {
							worklist.add(p.first());
						}
						worklist.addAll(p.second());
					}
				} else if (code instanceof Bytecode.If
						|| code instanceof Bytecode.IfIs
						|| code instanceof Bytecode.Switch
						|| code instanceof Bytecode.Compound) {
					List<VcBranch> bs;
					if (code instanceof Bytecode.If) {
						bs = transform((Bytecode.If) code, branch, labels, forest);
					} else if (code instanceof Bytecode.IfIs) {
						bs = transform((Bytecode.IfIs) code, branch, labels, forest);
					} else if (code instanceof Bytecode.Switch) {
						bs = transform((Bytecode.Switch) code, branch, labels,
								forest);
					} else if (code instanceof Bytecode.Quantify) {
						bs = transform((Bytecode.Quantify) code, branch,
								isInvariant, environment, labels, forest);
					} else {
						bs = transform((Bytecode.Loop) code, branch, environment,
								labels, forest);
					}
					worklist.addAll(bs);

				} else if (code instanceof Bytecode.Goto) {
					transform((Bytecode.Goto) code, branch, labels, forest);
					worklist.push(branch);
				} else if (code instanceof Bytecode.Return) {
					transform((Bytecode.Return) code, branch);
					exitBranches.add(branch);
				} else if (code instanceof Bytecode.Fail) {
					transform((Bytecode.Fail) code, branch, forest);
					exitBranches.add(branch);
				} else {
					// Unit statement. First, check whether or not there are any
					// preconditions for this statement and, if so, add
					// appropriate verification conditions to enforce them.
					if (!isInvariant) {
						Pair<String, Expr>[] preconditions = utils.getPreconditions(code, branch, environment, forest);
						if (preconditions.length > 0) {
							// This bytecode has one or more preconditions which
							// need to be asserted. Therefore, for each, create
							// a
							// failed branch to ensure the precondition is met.
							for (int i = 0; i != preconditions.length; ++i) {
								Pair<String, Expr> p = preconditions[i];
								Expr vc = buildVerificationCondition(
										p.second(), branch, environment, forest);
								wyalFile.add(wyalFile.new Assert(p.first(), vc,
										VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes())));
							}
							// We need to fork the branch here, because it must
							// have
							// INTERNAL state by now (i.e. because of the forks
							// above).
							branch = branch.fork();
						}
					}
					//
					unitGen.transform(code, forest, branch);
					branch.goTo(pc.next());
					worklist.push(branch);
				}
			}
		}
		
		joinAll(exitBranches);
		joinAll(fallThruBranches);
		VcBranch fallThru = null;
		// Select the fall through branch
		if (fallThruBranches.size() == 1) {
			fallThru = fallThruBranches.get(0);
		} else if (fallThruBranches.size() > 1) {
			// Should be unreachable. Sanity check for now.
			internalFailure("unreacahble code reached", filename, forest.get(parent).attributes());
		}

		return new Pair<VcBranch, List<VcBranch>>(fallThru, exitBranches);
	}

	/**
	 * Join all branches with matching PC locations. Branches are joined by
	 * taking the disjunction of those paths which differ, whilst still
	 * including those common to both as a conjunction.
	 * 
	 * @param branches
	 */
	private void joinAll(ArrayList<VcBranch> branches) {
		// First, go through and join all branches with the same pc using the
		// lower numbered branch as the "master". Once a branch has been joined,
		// simply null it out.
		for (int i = 0; i < branches.size(); ++i) {
			VcBranch i_branch = branches.get(i);
			if (i_branch != null) {
				BytecodeForest.Index i_pc = i_branch.pc();
				// Now, the goal is to identify all remaining branches which are
				// at the same location. These can then be all joined together
				// in one go. First, we count how many their are
				int count = 0;
				for (int j = i + 1; j < branches.size(); ++j) {
					VcBranch j_branch = branches.get(j);
					if (j_branch != null && i_pc.equals(j_branch.pc())) {
						count = count + 1;
					}
				}
				// Second, we store them up into the array and remove them from
				// the branches array.
				VcBranch[] matches = new VcBranch[count];
				count = 0;
				for (int j = i + 1; j < branches.size(); ++j) {
					VcBranch j_branch = branches.get(j);
					if (j_branch != null && i_pc.equals(j_branch.pc())) {
						matches[count++] = j_branch;
						branches.set(j, null);
					}
				}
				// Finally, if there are matching branches, we them all together
				// producing an updated branch which lives on.

				if (matches.length > 0) {
					VcBranch nBranch = i_branch.join(matches);
					branches.set(i, nBranch);
				}
			}
		}
		// At this stage, the branches array may contain null entries as a
		// result of the joining process. We now go through and compact all the
		// remaining branches to the lower indices, and the null entries to the
		// upper indices.
		int j = 0;
		for (int i = 0; i != branches.size(); ++i) {
			VcBranch b = branches.get(i);
			if (b != null) {
				branches.set(j++, b);
			}
		}
		// Finally, remove all null entries at the top of the branches array.
		while (branches.size() != j) {
			branches.remove(j);
		}
		// Done.
	}

	
	// ===============================================================================
	// Control Bytecodes
	// ===============================================================================

	/**
	 * <p>
	 * Transform a branch through a loop bytecode. This is done by splitting the
	 * entry branch into the case for the loop body, and the case for the loop
	 * after. First, modified variables are invalidated to disconnect them from
	 * information which held before the loop. Second, the loop invariant is
	 * assumed as this provides the only information known about modified
	 * variables.
	 * </p>
	 * 
	 * <p>
	 * For the case of the loop body, there are several scenarios. For branches
	 * which make it to the end of the body, the loop invariant must be
	 * reestablished. For branches which exit the loop, these are then folded
	 * into enclosing scope.
	 * </p>
	 * 
	 * @param code
	 *            The enclosing loop bytecode to be transformed.
	 * @param branch
	 *            The branch state going into this bytecode.
	 * @param environment
	 *            The mapping of registers to their declared types.
	 * @param labels
	 *            The mapping of labels to locations in the given block.
	 * @param block
	 *            The enclosing code block. This is needed to access source
	 *            location information.
	 */
	protected List<VcBranch> transform(Bytecode.Loop code, VcBranch branch,
			Type[] environment, Map<String, BytecodeForest.Index> labels,
			BytecodeForest forest) {
		return transformLoopHelper(code, branch, environment, labels, forest).second();
	}

	/**
	 * <p>
	 * Transform a branch through a loop bytecode. This is done by splitting the
	 * entry branch into the case for the loop body, and the case for the loop
	 * after. First, modified variables are invalidated to disconnect them from
	 * information which held before the loop. Second, the loop invariant is
	 * assumed as this provides the only information known about modified
	 * variables.
	 * </p>
	 * 
	 * <p>
	 * For the case of the loop body, there are several scenarios. For branches
	 * which make it to the end of the body, the loop invariant must be
	 * reestablished. For branches which exit the loop, these are then folded
	 * into enclosing scope.
	 * </p>
	 * 
	 * @param code
	 *            The bytecode being transformed.
	 * @param branch
	 *            The current branch being transformed
	 * @param environment
	 *            The mapping of registers to their declared types.
	 * @param labels
	 *            The map from labels to their block locations
	 * @param block
	 *            The block being transformed over.
	 */
	protected List<VcBranch> transform(Bytecode.Quantify code, VcBranch branch,
			boolean isInvariant, Type[] environment,
			Map<String, BytecodeForest.Index> labels, BytecodeForest forest) {
		// Write an arbitrary value to the index operand. This is necessary to
		// ensure that there is something there if it is used within the loop
		// body.
		branch.havoc(code.indexOperand());
		//
		VcBranch original = branch.fork();
		branch = branch.fork();
		// This represents a quantifier looop
		Pair<VcBranch, List<VcBranch>> p = transformQuantifierHelper(code,
				branch, isInvariant, environment, labels, forest);
		return extractQuantifiers(code, original, p.first(), p.second());
	}

	/**
	 * This extracts quantifiers from exit branches of a forall loop. There are
	 * two kinds of quantifiers which will be generated. A universal quantifier
	 * is created for the fall thru branch, whilst existential quantifiers are
	 * created for exit branches which are either still active (i.e. still
	 * progressing) or have terminated or failed.
	 * 
	 * @param code
	 * @param root
	 * @param fallThru
	 * @param exitBranches
	 * @return
	 */
	protected List<VcBranch> extractQuantifiers(Bytecode.Quantify code,
			VcBranch root, VcBranch fallThru, List<VcBranch> exitBranches) {
		// First, setup some helper variables for use in the remainder.
		SyntacticType elementType = utils.convert(Type.T_INT,
				Collections.EMPTY_LIST);
		Expr index = root.read(code.indexOperand());
		TypePattern pattern = new TypePattern.Leaf(elementType,
				(Expr.Variable) index);		
		Expr lowerBound = new Expr.Binary(Expr.Binary.Op.LTEQ,
				root.read(code.startOperand()), index);
		Expr upperBound = new Expr.Binary(Expr.Binary.Op.LT, index,
				root.read(code.endOperand()));
		Expr range = new Expr.Binary(Expr.Binary.Op.AND, lowerBound, upperBound);
		ArrayList<VcBranch> qBranches = new ArrayList<VcBranch>();
		// Second, deal with the universally quantified fall-thru branch. We
		// fork the root for this in order not to disturb it. We also must
		// include the elementOf which implies for the forall body.
		Expr forallBody = generateAssumptions(fallThru, root);
		fallThru = root.fork();
		fallThru.assume(new Expr.ForAll(pattern, new Expr.Binary(
				Expr.Binary.Op.IMPLIES, range, forallBody)));
		fallThru.goTo(fallThru.pc().next());
		qBranches.add(fallThru);
		// Finally, deal with existential branches next. We must fork the root
		// again for this, since it will now be immutable.
		for (int i = 0; i != exitBranches.size(); ++i) {
			VcBranch b = exitBranches.get(i);
			Expr body = generateAssumptions(b, root);
			body = new Expr.Binary(Expr.Binary.Op.AND, range, body);
			BytecodeForest.Index target = b.pc();
			b = root.fork();
			b.assume(new Expr.Exists(pattern, body));
			b.goTo(target);
			qBranches.add(b);
		}
		return qBranches;
	}

	/**
	 * Transform an arbitrary quantifier. Quantifiers are treated differently
	 * from loop bytecodes as they do not include loop invariants, and generate
	 * quantified formulae.
	 * 
	 * @param code
	 *            The enclosing loop bytecode to be transformed.
	 * @param branch
	 *            The branch state going into this bytecode.
	 * @param environment
	 *            The mapping of registers to their declared types.
	 * @param labels
	 *            The mapping of labels to locations in the given block.
	 * @param block
	 *            The enclosing code block. This is needed to access source
	 *            location information.
	 * @return
	 */
	protected Pair<VcBranch, List<VcBranch>> transformQuantifierHelper(
			Bytecode.Loop code, VcBranch branch, boolean isInvariant,
			Type[] environment, Map<String, BytecodeForest.Index> labels,
			BytecodeForest forest) {
		// The loopPc gives the block index of the loop bytecode.
		BytecodeForest.Index loopPc = branch.pc();
		// This is the easy case, as there is no loop invariant. Therefore,
		// we just havoc modified variables at the beginning and then allow
		// branches to exit the loop as normal. Branches which reach the end
		// of the loop body are returned to be universally quantified
		havocVariables(code.modifiedOperands(), branch);
		VcBranch activeBranch = branch.fork();
		// Now, run through loop body. This will produce several kinds of
		// branch. Those which have terminated or branched out of the loop body,
		// and those which have reached the end of the loop body. ).
		return transform(code.block(),0,loopPc, activeBranch, false, isInvariant, environment, labels, forest);
	}

	/**
	 * Transform an arbitrary loop (i.e. one that could be generic or forall
	 * loop). This will first determine whether or not a loop invariant is
	 * present. If not, then a conservative approach to dealing with modified
	 * variables is taken. If so, then the loop invariant must be established on
	 * entry, and then restored on subsequence iterations assuming it held on
	 * the iteration before (i.e. the inductive hypothesis).
	 * 
	 * @param code
	 *            The enclosing loop bytecode to be transformed.
	 * @param branch
	 *            The branch state going into this bytecode.
	 * @param environment
	 *            The mapping of registers to their declared types.
	 * @param labels
	 *            The mapping of labels to locations in the given block.
	 * @param block
	 *            The enclosing code block. This is needed to access source
	 *            location information.
	 * @return
	 */
	protected Pair<VcBranch, List<VcBranch>> transformLoopHelper(Bytecode.Loop code, VcBranch branch, Type[] environment,
			Map<String, BytecodeForest.Index> labels, BytecodeForest forest) {
		// The loopPc gives the block index of the loop bytecode.
		BytecodeForest.Index loopPc = branch.pc();
		int invariantOffset = getInvariantOffset(code,forest);

		// First thing we need to do is determine whether or not this loop has a
		// loop invariant, as this affects how we will approach it.
		if (invariantOffset == -1) {
			return transformLoopWithoutInvariant(code, branch, environment, labels, forest);
		} else {
			// Determine how many invariant blocks there are, as there might be
			// more than one. In the case that there is more than one, they are
			// assumed to be arranged consecutively one after the other.
			BytecodeForest.Block block = forest.get(code.block());
			int numberOfInvariants = 0;
			for (int i = invariantOffset; i < block.size()
					&& block.get(i).first() instanceof Bytecode.Invariant; ++i) {
				numberOfInvariants = numberOfInvariants+1;
			}
			//
			BytecodeForest.Index firstInvariantPc = new BytecodeForest.Index(code.block(), invariantOffset);
			String invariantMacroPrefix = method.name() + "_loopinvariant_";
			
			// FIXME: this is a hack to determine which variables should be
			// passed into the loop invariant macro. However, it really is a
			// hack. Firstly, we use the prefixes to ensure that only named
			// variables are included in the loop invariant. Secondly, we check
			// whether the variable in question has been defined yet to further
			// eliminate variables.
			String[] prefixes = branch.prefixes();
			boolean[] variables = new boolean[environment.length];
			for (int i = 0; i != variables.length; ++i) {
				if (branch.read(i) != null && prefixes[i] != null) {
					variables[i] = true;
				}
			}
			// *** END ***
			for(int i=0;i!=numberOfInvariants;++i) {
				buildInvariantMacro(firstInvariantPc.next(i), variables, environment, forest);
			}
			// This is the harder case as we must account for the loop invariant
			// properly. To do this, we allow the loop to execute upto the loop
			// invariant using the current branch state. At this point, we havoc
			// modified variables and then assume the loop invariant, before
			// running through the loop until the invariant is reached again.
			Pair<VcBranch, List<VcBranch>> p = transform(code.block(), 0, loopPc, branch, true, false, environment, labels, forest);
			// At this point, any branch which has terminated or branched out of
			// the loop represents a true execution path. Any branch which has
			// failed corresponds to ensuring the loop invariant on entry.
			// Active branches which reach the invariant need special
			// processing.
			VcBranch activeBranch = p.first();
			List<VcBranch> exitBranches = p.second();
			// Enforce invariants on entry. To do this, we generate a
			// verification condition that asserts each invariant macro given the
			// current branch state.
			for (int i = 0; i != numberOfInvariants; ++i) {
				BytecodeForest.Index invariantPc = firstInvariantPc.next(i);
				String invariantMacroName = invariantMacroPrefix + invariantPc.toString().replace(":", "_");
				Expr.Invoke invariant = buildInvariantCall(activeBranch, invariantMacroName, variables);
				Expr vc = buildVerificationCondition(invariant, activeBranch, environment, forest);
				wyalFile.add(wyalFile.new Assert("loop invariant does not hold on entry", vc,
						VcUtils.toWycsAttributes(forest.get(invariantPc).attributes())));
			}
			// Assume invariant holds for inductive case. To this, we first
			// havoc all modified variables to ensure that information about
			// them is not carried forward from before the loop. Then, we assume
			// the invariant macro holds in the current branch state.
			havocVariables(code.modifiedOperands(), activeBranch);
			for (int i = 0; i != numberOfInvariants; ++i) {
				BytecodeForest.Index invariantPc = firstInvariantPc.next(i);
				String invariantMacroName = invariantMacroPrefix
						+ invariantPc.toString().replace(":", "_");
				Expr.Invoke invariant = buildInvariantCall(activeBranch, invariantMacroName,
						variables);
				activeBranch.assume(invariant);
			}
			// Process inductive case for this branch by allowing it to
			// execute around the loop until the invariant is found again.
			// Branches which prematurely exit the loop are passed into the list
			// of exit branches. These are valid as they only have information
			// from the loop invariant.
			p = transform(code.block(), invariantOffset + numberOfInvariants, loopPc, activeBranch, true, false,
					environment, labels, forest);
			activeBranch = p.first();
			exitBranches.addAll(p.second());
			// Reestablish loop invariant. To do this, we generate a
			// verification condition that asserts the invariant macro given the
			// current branch state.
			for (int i = 0; i != numberOfInvariants; ++i) {
				BytecodeForest.Index invariantPc = firstInvariantPc.next(i);
				String invariantMacroName = invariantMacroPrefix
						+ invariantPc.toString().replace(":", "_");
				Expr.Invoke invariant = buildInvariantCall(activeBranch,
						invariantMacroName, variables);
				Expr vc = buildVerificationCondition(invariant, activeBranch,
						environment, forest);
				wyalFile.add(wyalFile.new Assert("loop invariant not restored",
						vc, VcUtils.toWycsAttributes(forest.get(invariantPc).attributes())));
			}
			// Reposition fall-through
			activeBranch.goTo(loopPc.next());
			// Done.
			return new Pair<VcBranch, List<VcBranch>>(activeBranch,
					exitBranches);
		}
	}

	/**
	 * Transform the loop assuming it has no loop invariant. This means that
	 * effectively all information about variables modified in the loop is
	 * discarded.
	 * 
	 * @param code
	 *            The enclosing loop bytecode to be transformed.
	 * @param branch
	 *            The branch state going into this bytecode.
	 * @param environment
	 *            The mapping of registers to their declared types.
	 * @param labels
	 *            The mapping of labels to locations in the given block.
	 * @param block
	 *            The enclosing code block. This is needed to access source
	 *            location information.
	 * @return
	 */
	protected Pair<VcBranch, List<VcBranch>> transformLoopWithoutInvariant(
			Bytecode.Loop code, VcBranch branch, Type[] environment,
			Map<String, BytecodeForest.Index> labels, BytecodeForest forest) {
		BytecodeForest.Index loopPc = branch.pc();
		// This is the easy case, as there is no loop invariant. Therefore,
		// we just havoc modified variables at the beginning and then allow
		// branches to exit the loop as normal. Branches which reach the end
		// of the loop body can be discarded as they represent correct
		// execution through the loop.
		havocVariables(code.modifiedOperands(), branch);
		VcBranch fallThru = branch.fork();
		VcBranch activeBranch = branch.fork();
		// Now, run through loop body. This will produce several kinds of
		// branch. Those which have terminated or branched out of the loop body,
		// and those which have reached the end of the loop body. All branches
		// in the former case go straight onto the list of returned branches.
		// Those in the latter case are discarded (as discussed above).
		Pair<VcBranch, List<VcBranch>> p = transform(code.block(), 0, loopPc, activeBranch, false, false, environment, labels,
				forest);
		fallThru.goTo(loopPc.next());
		return new Pair<VcBranch, List<VcBranch>>(fallThru, p.second());
	}

	/**
	 * Construct a macro in the generated Wyal file which embodies the given
	 * loop invariant. Thus, the loop invariant can be asserted by asserting
	 * this macro, and can be assumed by assuming this macro (with appropriate
	 * arguments of course). For simplicity, the generated macro will accept all
	 * active variables (i.e. rather than one-time temporaries). This is not
	 * completely efficient in the case of variables which are not referenced by
	 * the invariant, but it's a simple and pragmatic choice.
	 * 
	 * @param invariantPC
	 *            The location of the invariant. This is used simply to give the
	 *            invariant a unique identifier to distinguish it from other
	 *            invariants in the same function or method.
	 * @param environment
	 *            The mapping of registers to their declared types.
	 * @param block
	 *            The enclosing code block
	 */
	private void buildInvariantMacro(BytecodeForest.Index invariantPC,
			boolean[] variables, Type[] environment, BytecodeForest forest) {
		Bytecode.Invariant code = (Bytecode.Invariant) forest.get(invariantPC).first();
		// FIXME: we don't need to include all variables, only those which are
		// "active".
		ArrayList<Type> types = new ArrayList<Type>();
		for (int i = 0; i != variables.length; ++i) {
			if (variables[i]) {
				types.add(environment[i]);
			} else {
				types.add(null);
			}
		}
		String pc = invariantPC.block() + "_" + invariantPC.offset();
		BytecodeForest.Index root = new BytecodeForest.Index(code.block(),0);
		buildMacroBlock(method.name() + "_loopinvariant_" + pc, root, forest, types, true);
	}

	/**
	 * Construct invocation of loop invariant macro. This is used both for
	 * asserting and assuming the invariant holds at different positions
	 * throughout the process.
	 * 
	 * @param branch
	 *            The branch in which the call is being constructed. This is
	 *            needed to access the current state of relevant varibles.
	 * @param name
	 *            The name of the loop invariant macro. This is determined using
	 *            the current method name and pc location.
	 * @param variables
	 *            the list of variables which are passed into the macro.
	 *            Essentially, this is the list of variables which existed
	 *            before the loop (i.e. rather than those local to the loop).
	 * @return
	 */
	protected Expr.Invoke buildInvariantCall(VcBranch branch, String name,
			boolean[] variables) {
		List<Expr> arguments = new ArrayList<Expr>();
		for (int i = 0; i != variables.length; ++i) {
			if (variables[i]) {
				arguments.add(branch.read(i));
			}
		}
		Expr argument = arguments.size() == 1 ? arguments.get(0)
				: new Expr.Nary(Expr.Nary.Op.TUPLE, arguments);
		return new Expr.Invoke(name, wyalFile.id(), Collections.EMPTY_LIST,
				argument);
	}

	/**
	 * Check whether a given loop bytecode contains an invariant, or not.
	 * 
	 * @param branch
	 * @return
	 */
	private int getInvariantOffset(Bytecode.Loop loop, BytecodeForest forest) {
		BytecodeForest.Block block = forest.get(loop.block());
		for (int i = 0; i != block.size(); ++i) {
			if (block.get(i).first() instanceof Bytecode.Invariant) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Send a given set of variables to havoc. This means that any knowledge of
	 * the current variables' state is discarded. In the context of a loop, this
	 * is necessary to account for the fact that variables modified in the loop
	 * have a range of unknown values.
	 * 
	 * @param variables
	 *            The set of variables to be sent to havoc.
	 * @param branch
	 *            The branch in which to havoc the variables.
	 */
	public void havocVariables(int[] variables, VcBranch branch) {
		for (int i = 0; i != variables.length; ++i) {
			int var = variables[i];
			Expr e = branch.read(var);
			if (e != null) {
				// FIXME: We only havoc variables that have already been
				// defined. This is possibly a workaround for a bug, where the
				// loop modified variables can contain variables local to the
				// loop.
				branch.havoc(var);
			}
		}
	}

	/**
	 * <p>
	 * Transform a branch through a conditional bytecode. This is done by
	 * splitting the entry branch into the case for the true branch and the case
	 * for the false branch. Control then continues down each branch.
	 * </p>
	 * <p>
	 * On the true branch, the condition is assumed to hold. In contrast, the
	 * condition's inverse is assumed to hold on the false branch.
	 * </p>
	 * 
	 * @param code
	 *            The bytecode being transformed.
	 * @param branch
	 *            The current branch being transformed
	 * @param branches
	 *            The list of branches currently being managed.
	 */
	protected List<VcBranch> transform(Bytecode.If code, VcBranch branch,
			Map<String, BytecodeForest.Index> labels, BytecodeForest forest) {
		// First, clone and register the true branch
		VcBranch trueBranch = branch.fork();
		VcBranch falseBranch = branch.fork();
		// Second assume the condition on each branch
		Expr.Binary trueTest = buildCondition(branch.read(code.operand(0)),forest,branch);
		trueBranch.assume(trueTest);
		falseBranch.assume(utils.invert(trueTest));
		// Third, dispatch branches to their targets
		falseBranch.goTo(branch.pc().next());
		trueBranch.goTo(labels.get(code.destination()));
		// Finally, return the branches
		ArrayList<VcBranch> exitBranches = new ArrayList<VcBranch>();
		exitBranches.add(trueBranch);
		exitBranches.add(falseBranch);
		return exitBranches;
	}

	/**
	 * <p>
	 * Transform a branch through a conditional bytecode. This is done by
	 * splitting the entry branch into the case for the true branch and the case
	 * for the false branch. Control then continues down each branch.
	 * </p>
	 * <p>
	 * On the true branch, the condition is assumed to hold. In contrast, the
	 * condition's inverse is assumed to hold on the false branch.
	 * </p>
	 * 
	 * @param code
	 *            The bytecode being transformed.
	 * @param branch
	 *            The current branch being transformed
	 * @param branches
	 *            The list of branches currently being managed.
	 */
	protected List<VcBranch> transform(Bytecode.IfIs code, VcBranch branch,
			Map<String, BytecodeForest.Index> labels, BytecodeForest forest) {
		ArrayList<VcBranch> exitBranches = new ArrayList<VcBranch>();
		// In this case, both branches are reachable.
		// First, clone and register the branch
		VcBranch falseBranch = branch.fork();
		VcBranch trueBranch = branch.fork();
		// Second add appropriate runtime type tests
		List<wyil.lang.Attribute> attributes = forest.get(branch.pc()).attributes();
		Collection<wycc.lang.Attribute> wycsAttributes = VcUtils.toWycsAttributes(attributes); 
		SyntacticType trueType = utils.convert(code.rightOperand(), attributes);
		SyntacticType falseType = new SyntacticType.Negation(utils.convert(code.rightOperand(),
				attributes), wycsAttributes);
		trueBranch.assume(new Expr.Is(branch.read(code.operand(0)), trueType,
				wycsAttributes));
		falseBranch.assume(new Expr.Is(branch.read(code.operand(0)), falseType,
				wycsAttributes));
		// Finally dispatch the branches
		falseBranch.goTo(branch.pc().next());
		trueBranch.goTo(labels.get(code.destination()));
		//
		exitBranches.add(trueBranch);
		exitBranches.add(falseBranch);
		// done
		return exitBranches;
	}

	/**
	 * <p>
	 * Transform a branch through a switch bytecode. This is done by splitting
	 * the entry branch into separate branches for each case. The entry branch
	 * then follows the default branch.
	 * </p>
	 * 
	 * @param code
	 *            The bytecode being transformed.
	 * @param branch
	 *            The current branch being transformed
	 * @param branches
	 *            The list of branches currently being managed.
	 */
	protected List<VcBranch> transform(Bytecode.Switch code, VcBranch branch,
			Map<String, BytecodeForest.Index> labels, BytecodeForest forest) {
		ArrayList<VcBranch> exitBranches = new ArrayList<VcBranch>();
		VcBranch defaultBranch = branch.fork();

		// Process each case individually, whilst also updating the default
		// branch.
		for (int i = 0; i != code.branches().size(); ++i) {
			// First, for each case fork a new branch to traverse it.
			VcBranch caseBranch = branch.fork();
			// Second, for each case, assume that the variable switched on
			// matches the give case value. Likewise, assume that the default
			// branch does *not* equal this value.
			Constant caseValue = code.branches().get(i).first();
			// Second, on the new branch we need assume that the variable being
			// switched on matches the given value.
			Expr src = branch.read(code.operand(0));
			Expr constant = new Expr.Constant(utils.convert(caseValue, forest, branch),
					VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes()));
			caseBranch.assume(new Expr.Binary(Expr.Binary.Op.EQ, src, constant,
					VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes())));
			// Third, on the default branch we can assume that the variable
			// being switched is *not* the given value.
			defaultBranch.assume(new Expr.Binary(Expr.Binary.Op.NEQ, src,
					constant, VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes())));
			// Finally, dispatch branch
			caseBranch.goTo(labels.get(code.branches().get(i).second()));
			exitBranches.add(caseBranch);
		}

		// Finally, the dispatch the default branch to the default target.
		defaultBranch.goTo(labels.get(code.defaultTarget()));
		exitBranches.add(defaultBranch);

		// TODO: here is where we can add a coverage check. Specifically, that
		// all cases of the input variable have been covered or not. This would
		// be in the case that there is no explicit default branch target.
		return exitBranches;
	}

	/**
	 * <p>
	 * Transform an assert, assume or invariant bytecode. This is done by
	 * transforming the contained bytecode block and processing the resulting
	 * branches depending on whether or not this is an assert or assume
	 * bytecode. In the former case, all failing branches give rise to
	 * corresponding verification conditions. In the latter case, all failing
	 * branches are simply ignored as we're assuming they don't happen.
	 * </p>
	 * 
	 * @param code
	 *            The bytecode being transformed.
	 * @param branch
	 *            The current branch being transformed
	 * @param environment
	 *            The mapping of registers to their declared types.
	 * @param labels
	 *            The map from labels to their block locations
	 * @param block
	 *            The block being transformed over.
	 * @return A pair consisting of: the active branch (if any) which has fallen
	 *         through the end of the block and is now located at the
	 *         instruction following the parent; and, the list of zero or more
	 *         branches which have terminated or failed.
	 */
	protected Pair<VcBranch, List<VcBranch>> transform(
			Bytecode.AssertOrAssume code, boolean isAssert, VcBranch branch,
			Type[] environment, Map<String, BytecodeForest.Index> labels,
			BytecodeForest forest) {
		int start = wyalFile.declarations().size();
		// First, transform the given branch through the assert or assume block.
		// This will produce one or more exit branches, some of which may have
		// reached failed states and need to be turned into verification
		// conditions (for asserts only).
		BytecodeForest.Index pc = branch.pc();
		Pair<VcBranch, List<VcBranch>> p = transform(code.block(), 0, pc, branch, false, true, environment, labels,
				forest);
		List<VcBranch> exitBranches = p.second();
		// Second, examine the list of exit branches and decide what to do with
		// them. In the case of a failing branch then we need to generate an
		// appropriate verification condition.
		for (int i = 0; i != exitBranches.size(); ++i) {
			VcBranch b = exitBranches.get(i);
			if (b.state() == VcBranch.State.FAILED && !isAssert) {
				// In the case of an assume state, we just ignore all
				// failing branches as we are simply "assuming" they don't
				// happen. Therefore, we silently remove them from the list of
				// exit branches.
				exitBranches.remove(i--);
			}
		}
		// Third, in the case of an assumption, we need to remove any
		// verification conditions that were generated when processing this
		// block.
		if(!isAssert) {
			// FIXME: this is something of a hack for now. A better solution would
			// be to pass a variable recursively down through the call stack which
			// signaled that no verification conditions should be generated.
			while(wyalFile.declarations().size() > start) {
				wyalFile.declarations().remove(wyalFile.declarations().size()-1);
			}
		}
		
		// Done
		return p;
	}

	/**
	 * <p>
	 * Transform a branch through an unconditional branching bytecode. This is
	 * pretty straightforward, and the branch is just directed to the given
	 * location.
	 * </p>
	 * 
	 * @param code
	 *            The bytecode being transformed.
	 * @param branch
	 *            The current branch being transformed
	 * @param branches
	 *            The list of branches currently being managed.
	 */
	protected void transform(Bytecode.Goto code, final VcBranch branch,
			Map<String, BytecodeForest.Index> labels, BytecodeForest forest) {
		branch.goTo(labels.get(code.destination()));
	}

	/**
	 * <p>
	 * Transform a branch through the special fail bytecode. In the normal case,
	 * we must establish this branch is unreachable. However, in the case that
	 * we are within an enclosing assume statement, then we can simply discard
	 * this branch.
	 * </p>
	 * 
	 * @param code
	 *            The bytecode being transformed.
	 * @param branchIndex
	 *            The index of the branch (in branches) which holds on entry to
	 *            the bytecode.
	 * @param branches
	 *            The list of branches currently being managed.
	 */
	protected void transform(Bytecode.Fail code, VcBranch branch,
			BytecodeForest forest) {
		// Update status of this branch to failed. This simply indicates that
		// this branch's location should be unreachable and, hence, we need a
		// verification condition to enforce this.
		branch.setState(VcBranch.State.FAILED);
	}

	/**
	 * <p>
	 * Transform a branch through a return bytecode. In this case, we need to
	 * ensure that the postcondition holds. After that, we can drop the branch
	 * since it is completed.
	 * </p>
	 * 
	 * @param code
	 *            The bytecode being transformed.
	 * @param branch
	 *            The current branch being transformed
	 * @param branches
	 *            The list of branches currently being managed.
	 */
	protected void transform(Bytecode.Return code, VcBranch branch) {
		// Marking the branch as terminated indicates that it is no longer
		// active. Thus, the original callers of this block transformation can
		// subsequently extract the constraints which hold at the point of the
		// return.
		branch.setState(VcBranch.State.TERMINATED);
	}

	

	/**
	 * Construct a macro with a given name representing a block of code. The
	 * macro can then be called elsewhere as a predicate. For example, a macro
	 * can be construct to represent the precondition of a function. This can
	 * then be assumed at the start of the function's block. Or, it can ensured
	 * at the point of an invocation.
	 *
	 * @param name
	 *            --- the nameto give to the generated macro.
	 * @param block
	 *            --- the block of code being translated.
	 * @param types
	 *            --- operand register in containing branch which should map to
	 *            the inputs of the block being translated.
	 * @return
	 */
	protected void buildMacroBlock(String name, BytecodeForest.Index root,
			BytecodeForest forest, List<Type> types, boolean isInvariant) {
		int start = wyalFile.declarations().size();
		
		// first, generate a branch for traversing the external block.
		VcBranch master = new VcBranch(Math.max(forest.numRegisters(), types.size()), root, null);

		Type[] environment = new Type[types.size()];
		ArrayList<TypePattern.Leaf> declarations = new ArrayList<TypePattern.Leaf>();
		// second, set initial environment
		for (int i = 0; i != types.size(); ++i) {
			Type type = types.get(i);
			environment[i] = type;
			if (type != null) {
				Expr.Variable v = new Expr.Variable("r" + i);
				master.write(i, v);
				// FIXME: what attributes to pass into convert?
				declarations.add(new TypePattern.Leaf(utils.convert(type,
						Collections.EMPTY_LIST), v));
			}
		}

		// Construct the type declaration for the new block macro
		TypePattern type;

		if (declarations.size() == 1) {
			type = declarations.get(0);
		} else {
			type = new TypePattern.Tuple(
					declarations.toArray(new TypePattern.Leaf[declarations
							.size()]));
		}
	
		// At this point, we are guaranteed exactly one branch because there
		// is only ever one exit point from a pre-/post-condition.
		List<VcBranch> exitBranches = transform(master, root, isInvariant, environment, forest);
		// Remove any verification conditions that were generated when
		// processing this block.  		
		// FIXME: this is something of a hack for now. A better solution would
		// be for the verification conditions to be returned so that they
		// can be discussed.
		while(wyalFile.declarations().size() > start) {
			wyalFile.declarations().remove(wyalFile.declarations().size()-1);
		}
		//
		for (VcBranch exitBranch : exitBranches) {
			if (exitBranch.state() == VcBranch.State.TERMINATED) {
				Expr body = generateAssumptions(exitBranch, null);
				wyalFile.add(wyalFile.new Macro(name, Collections.EMPTY_LIST,
						type, body));
				return;
			}
		}		
		
		// It should be impossible to reach here.
		internalFailure("unreachable code", filename);
	}

	

	/**
	 * Construct a verification condition which asserts a given expression on
	 * the current branch. Thus, all relevant assumptions are taken from the
	 * current branch (and its ancestors) to form the verification condition.
	 * This function must additional type each variable used within the
	 * verification condition.
	 * 
	 * @param assertion
	 *            --- The assertion which is to be ensured by the verification
	 *            condition, assuming the assumptions held.
	 * @param branch
	 *            --- The branch from which the verification condition is being
	 *            generated.
	 * @param block
	 *            --- The enclosing attributed block for this assertion.
	 * @param assumptions
	 *            --- Additional assumptions to take into consideration.
	 * @return
	 */
	protected Expr buildVerificationCondition(Expr assertion, VcBranch branch,
			Type[] environment, BytecodeForest forest, Expr... extraAssumptions) {
		// First construct the assertion which forms the basis of the
		// verification condition. The assertion must be shown to hold assuming
		// the assumptions did. Therefore, we construct an implication to
		// establish this.
		Expr assumptions = generateAssumptions(branch, null);
		
		for(Expr ea : extraAssumptions) {
			assumptions = new Expr.Binary(Expr.Binary.Op.AND, assumptions, ea);
		}
		
		assertion = new Expr.Binary(Expr.Binary.Op.IMPLIES, assumptions,
				assertion, VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes()));

		// Next, we determine the set of used variables within the assertion.
		// This is done to minimise the number of variables present in the final
		// verification condition as otherwise this can, potentially, make life
		// harder for the verifier.
		HashSet<String> uses = new HashSet<String>();
		assertion.freeVariables(uses);

		// Now, we determine the correct type for all used variables.

		// FIXME: this does not actually always find the correct type of a
		// variable. That's because it always uses the current type of a
		// variable, rather than the type which held at a given position. This
		// should be resolved by removing type tracking altogether and replacing
		// it with the use of type tests (see #316).

		ArrayList<TypePattern> vars = new ArrayList<TypePattern>();
		for (String var : uses) {
			Type type;
			if (var.startsWith("_")) {
				// FIXME: this is a hack to handle the fact that forall index
				// variables are not explicit. However, we know that they are
				// always integers.
				type = Type.T_INT;
			} else if (var.startsWith("null$")) {
				// FIXME: this is also something of a hack to deal with
				// expressions which currently have no sensible translation into
				// WyAL.
				type = Type.T_ANY;
			} else {
				int reg = VcUtils.determineRegister(var, branch.prefixes());
				type = environment[reg];
			}
			SyntacticType t = utils.convert(type, forest.get(branch.pc()).attributes());
			Expr.Variable v = new Expr.Variable(var);
			vars.add(new TypePattern.Leaf(t, v));
		}

		// Finally, we construct the verification condition. This is done by
		// universally quantifying each variable used in the assertion with its
		// corresponding type.
		if (vars.size() == 0) {
			// we have nothing to parameterise, so ignore it and just return the
			// assertion without any quantification.
			return assertion;
		} else if (vars.size() == 1) {
			return new Expr.ForAll(vars.get(0), assertion);
		} else {
			return new Expr.ForAll(new TypePattern.Tuple(vars), assertion);
		}
	}


	/**
	 * <p>
	 * Generate all constraints from a given branch. Those are the set of
	 * complete constraints which are known to hold in this branch and any of
	 * its ancestors. To do this, we need to traverse the branch graph and then
	 * recombine everything in the correct order. For example, consider this
	 * graph:
	 * </p>
	 * 
	 * <pre>
	 *      #1: y>=0
	 *    //  \\
	 *   //    \\
	 *  //      \\
	 * ||        ||   
	 * #2: x>=y  #3: x<y
	 * ||        ||
	 * ||        #4: z = 0
	 *  \\      //
	 *   \\    //
	 *    \\  //
	 *      #5:
	 * </pre>
	 * <p>
	 * Here, the assumptions are being generated from branch #5. To do this, we
	 * traverse backwards from #5. Constraints from the left-branch are
	 * disjuncted with those from the right branch, whilst those common to both
	 * are conjuncted with all. Thus, the resulting expressions would be:
	 * </p>
	 * 
	 * <pre>
	 * y &gt;= 0 &amp;&amp; (x &gt;= y || (x &lt; y || z == 0))
	 * </pre>
	 */
	private Expr generateAssumptions(VcBranch b, VcBranch end) {
		// We need to clone the return expression here to ensure there is no
		// aliasing between generated verification conditions as this can lead
		// to problems later on.
		return generateAssumptionsHelper(b,end).copy();
	}
	
	private Expr generateAssumptionsHelper(VcBranch b, VcBranch end) {

		if (b == end) {
			// The termination condition is reached.
			return new Expr.Constant(Value.Bool(true));
		} else {
			// FIXME: this method is not efficient and does not generate an
			// optimal decomposition of the branch graph.

			// First, compute disjunction of parent constraints.
			VcBranch[] b_parents = b.parents();
			Expr parents = null;
			for (int i = 0; i != b_parents.length; ++i) {
				VcBranch parent = b_parents[i];
				Expr parent_constraints = generateAssumptionsHelper(parent, end);
				if (i == 0) {
					parents = parent_constraints;
				} else if(parent_constraints instanceof Expr.Constant) {
					Expr.Constant c = (Expr.Constant) parent_constraints;
					Value.Bool v = (Value.Bool) c.value;					
					if(v.value) {
						// can short-circuit this
						parents = c;
						break;
					} else {
						// ignore false						
					}
				} else {
					parents = new Expr.Binary(Expr.Binary.Op.OR, parents,
							parent_constraints);
				}
			}

			// Second, include constraints from this node.
			Expr b_constraints = b.constraints();			
			if (parents == null) {
				return b.constraints();
			} else if(parents instanceof Expr.Constant) {
				Expr.Constant c = (Expr.Constant) parents;
				Value.Bool v = (Value.Bool) c.value;					
				if(v.value) {
					return b_constraints;
				} else {
					return c;
				}
			} else {
				return new Expr.Binary(Expr.Binary.Op.AND, parents,
						b_constraints);
			}
		}
	}
	
	/**
	 * Generate a formula representing a condition from an conditional bytecode.
	 *
	 * @param op
	 * @param stack
	 * @param elem
	 * @return
	 */
	private Expr.Binary buildCondition(Expr test, BytecodeForest forest, VcBranch branch) {
		if (test instanceof Expr.Binary) {
			return (Expr.Binary) test;
		} else {
			Collection<Attribute> attributes = VcUtils.toWycsAttributes(forest.get(branch.pc()).attributes());
			Expr.Constant tt = new Expr.Constant(Value.Bool(true), attributes);  
			return new Expr.Binary(Expr.Binary.Op.EQ, test, tt, attributes);
		}
	}
	
	private Type expand(Type t, Collection<wyil.lang.Attribute> attributes) {
		try {
			return expander.getUnderlyingType(t);
		} catch (ResolveError re) {
			internalFailure(re.getMessage(), filename, attributes);
		} catch (IOException re) {
			internalFailure(re.getMessage(), filename, attributes);
		}
		return null; // dead-code
	}
	
	private static <T> List<T> append(List<T> xs, List<T> ys) {
		ArrayList<T> rs = new ArrayList<T>();
		rs.addAll(xs);
		rs.addAll(ys);		
		return rs;
	}
}
