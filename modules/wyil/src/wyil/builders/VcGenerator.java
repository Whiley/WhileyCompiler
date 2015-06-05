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
import wyil.attributes.VariableDeclarations;
import wyil.builders.VcBranch.State;
import wyil.lang.*;
import wyil.lang.CodeBlock.Index;
import wyil.util.AttributedCodeBlock;
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
	private String filename;
	private WyalFile wycsFile;
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
		wycsFile = new WyalFile(wyilFile.id(), filename);

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

		return wycsFile;
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
		AttributedCodeBlock body = typeDecl.invariant();
		Expr invariant = null;
		// FIXME: get the register prefix!
		Expr.Variable var = new Expr.Variable("r0");
		if (body != null) {
			VcBranch master = new VcBranch(Math.max(1, body.numSlots()), null);
			master.write(0, var);
			// Pass the given branch through the type invariant, producing
			// exactly one exit branch from which we can generate the invariant
			// expression.
			Type[] environment = new Type[] { typeDecl.type() };
			List<VcBranch> exitBranches = transform(master,
					CodeBlock.Index.ROOT, environment, body);
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

		TypePattern.Leaf pattern = new TypePattern.Leaf(convert(
				typeDecl.type(), Collections.EMPTY_LIST), var);

		wycsFile.add(wycsFile.new Type(typeDecl.name(), Collections.EMPTY_LIST,
				pattern, invariant, toWycsAttributes(typeDecl.attributes())));
	}

	protected void transform(WyilFile.FunctionOrMethod method, WyilFile wyilFile) {

		this.method = method;

		Type.FunctionOrMethod fmm = method.type();
		AttributedCodeBlock body = method.body();
		List<AttributedCodeBlock> precondition = method.precondition();
		List<AttributedCodeBlock> postcondition = method.postcondition();
		VariableDeclarations rds = method.attribute(VariableDeclarations.class);
		// First, translate pre- and post-conditions into macro blocks. These
		// can then be used in various places to assume or enforce pre /
		// post-conditions. For example, when ensure a pre-condition is met at
		// an invocation site, we can call this macro directly.
		String prefix = method.name() + "_requires_";
		for (int i = 0; i != precondition.size(); ++i) {
			buildMacroBlock(prefix + i, CodeBlock.Index.ROOT,
					precondition.get(i), fmm.params());
		}
		prefix = method.name() + "_ensures_";
		List<Type> postEnvironment = prepend(fmm.ret(), fmm.params());
		for (int i = 0; i != postcondition.size(); ++i) {
			buildMacroBlock(prefix + i, CodeBlock.Index.ROOT,
					postcondition.get(i), postEnvironment);
		}

		// Finally, add a function representing this function or method.
		buildFunctionBlock(method.name(), fmm.params(), fmm.ret());

		if (method.hasModifier(Modifier.NATIVE)) {
			// We don't consider native methods because they have empty bodies,
			// and attempting to pass these through to the verification
			// condition generator will cause problems.
			return;
		}

		Pair<String[], Type[]> registerInfo = parseRegisterDeclarations(rds);
		String[] prefixes = registerInfo.first();
		Type[] bodyEnvironment = registerInfo.second();
		// Construct the master branch and initialise all parameters with their
		// declared types in the master branch. The master branch needs to have
		// at least as many slots as there are parameters, though may require
		// more if the body uses them.
		VcBranch master = new VcBranch(Math.max(body.numSlots(), fmm.params()
				.size()), prefixes);

		Expr[] arguments = new Expr[fmm.params().size()];
		for (int i = 0; i != fmm.params().size(); ++i) {
			Expr.Variable v = new Expr.Variable(prefixes[i]);
			master.write(i, v);
			arguments[i] = v;
		}

		// Second, assume all preconditions. To do this, we simply invoke the
		// precondition macro for each one.
		prefix = method.name() + "_requires_";
		for (int i = 0; i != precondition.size(); ++i) {
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
		List<VcBranch> exitBranches = transform(master, CodeBlock.Index.ROOT,
				bodyEnvironment, body);

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
				Expr vc = buildVerificationCondition(
						new Expr.Constant(Value.Bool(false)), branch,
						bodyEnvironment, body);
				wycsFile.add(wycsFile.new Assert("assertion failed", vc,
						toWycsAttributes(body.attributes(branch.pc()))));
				break;
			}
			case TERMINATED: {
				if (fmm.ret() instanceof Type.Void) {
					// In this case, there is not return value and, hence, there
					// is no need to ensure the postcondition holds.
				} else {
					List<wyil.lang.Attribute> attributes = body.attributes(branch.pc());
					Collection<wycc.lang.Attribute> wycsAttributes = toWycsAttributes(attributes);
					// Find the return statement in question
					Codes.Return ret = (Codes.Return) body.get(branch.pc());
					// Construct verification check to ensure that return
					// type invariant holds
					Expr returnedOperand = branch.read(ret.operand);					
					Type rawType = expand(bodyEnvironment[ret.operand],attributes);
					Expr rawTest = new Expr.Is(returnedOperand,
							convert(rawType, attributes));
					if (containsNominal(fmm.ret(),attributes)) {
						// FIXME: we need the raw test here, because the
						// verifier can't work out the type of the expression
						// otherwise.						
						Expr nominalTest = new Expr.Is(returnedOperand,
								convert(fmm.ret(), attributes));
						Expr vc = buildVerificationCondition(nominalTest,
								branch, bodyEnvironment, body, rawTest);
						// FIXME: add contextual information here
						wycsFile.add(wycsFile.new Assert(
								"return type invariant not satisfied", vc,
								wycsAttributes));
					}
					// Construct arguments for the macro invocation. Only
					// the returned value is read from the branch at the current
					// pc. The other arguments correspond to the parameters
					// which held on entry to this function.
					arguments = new Expr[fmm.params().size() + 1];
					for (int i = 0; i != fmm.params().size(); ++i) {
						arguments[i + 1] = new Expr.Variable(prefixes[i]);
					}
					
					arguments[0] = returnedOperand;
					// For each postcondition generate a separate
					// verification condition. Doing this allows us to gather
					// more detailed context information in the case of a
					// failure about which post-condition is failing.
					prefix = method.name() + "_ensures_";
					for (int i = 0; i != postcondition.size(); ++i) {
						Expr arg = arguments.length == 1 ? arguments[0]
								: new Expr.Nary(Expr.Nary.Op.TUPLE, arguments);
						Expr.Invoke macro = new Expr.Invoke(prefix + i,
								wyilFile.id(), Collections.EMPTY_LIST, arg);
						Expr vc = buildVerificationCondition(macro, branch,
								bodyEnvironment, body, rawTest);
						// FIXME: add contextual information here
						wycsFile.add(wycsFile.new Assert(
								"postcondition not satisfied", vc,
								toWycsAttributes(body.attributes(branch.pc()))));
					}
				}
				break;
			}
			default:
				// This should be impossible to reach!
				internalFailure("unreachable code reached! (" + branch.state()
						+ ")", filename, body.attributes(branch.pc()));
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
	public List<VcBranch> transform(VcBranch branch, CodeBlock.Index root,
			Type[] environment, AttributedCodeBlock block) {
		// Construct the label map which is needed for conditional branches
		Map<String, CodeBlock.Index> labels = CodeUtils.buildLabelMap(block);
		Pair<VcBranch, List<VcBranch>> p = transform(root, 0, branch, false,
				environment, labels, block);
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
	protected Pair<VcBranch, List<VcBranch>> transform(CodeBlock.Index parent,
			int offset, VcBranch entryState, boolean breakOnInvariant,
			Type[] environment, Map<String, CodeBlock.Index> labels, AttributedCodeBlock block) {
		// Move state to correct location
		CodeBlock.Index start = new CodeBlock.Index(parent);
		entryState.goTo(new CodeBlock.Index(parent, offset));
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
			CodeBlock.Index pc = branch.pc();

			// Determine whether to continue executing this branch, or whether
			// it has completed within this scope.
			if (!pc.isWithin(parent) || branch.state() != VcBranch.State.ACTIVE) {
				// This indicates the given branch has either exited this block
				// via a non-local branch, or terminated in some fashion.
				// Therefore, this branch is moved into the list of exit
				// branches.
				exitBranches.add(branch);
			} else if (!block.contains(pc)) {
				// This indicates the given branch has exited this block by
				// falling through. We now need to check for breakOnInvariant.
				if (breakOnInvariant) {
					// Break On Invariant is enabled, therefore continue going
					// around.
					branch.goTo(start);
				} else if (parent != null) {
					// No reset, allow to exit branch as normal. First, set
					// branch location to the next instruction following the
					// parent instruction containing this block.
					branch.goTo(parent.next());
				} else {
					internalFailure("unreachable code reached!", filename,
							block.attributes(pc));
				}
				fallThruBranches.add(branch);
			} else {
				// Continue executing this branch as it is still within the
				// scope of this block.
				Code code = block.get(pc);
				// Now, dispatch statements. Control statements are treated
				// specially from unit statements.
				if (code instanceof Codes.AssertOrAssume) {
					if (breakOnInvariant && code instanceof Codes.Invariant) {
						// In this special case, we have reached the invariant
						// bytecode and, hence, we break out of this loop. This
						// is needed for handling loop invariants where we need
						// to do special things when the invariant is
						// encountered.
						fallThruBranches.add(branch);
					} else {
						boolean isAssert = code instanceof Codes.Assert;
						Pair<VcBranch, List<VcBranch>> p = transform(
								(Codes.AssertOrAssume) code, isAssert, branch,
								environment, labels, block);
						worklist.add(p.first());
						worklist.addAll(p.second());
					}
				} else if (code instanceof Codes.If
						|| code instanceof Codes.IfIs
						|| code instanceof Codes.Switch
						|| code instanceof Code.Compound) {
					List<VcBranch> bs;
					if (code instanceof Codes.If) {
						bs = transform((Codes.If) code, branch, labels, block);
					} else if (code instanceof Codes.IfIs) {
						bs = transform((Codes.IfIs) code, branch, labels, block);
					} else if (code instanceof Codes.Switch) {
						bs = transform((Codes.Switch) code, branch, labels,
								block);
					} else if (code instanceof Codes.Quantify) {
						bs = transform((Codes.Quantify) code, branch,
								environment, labels, block);
					} else if (code instanceof Codes.ForAll) {
						bs = transform((Codes.ForAll) code, branch,
								environment, labels, block);
					} else {
						bs = transform((Codes.Loop) code, branch, environment,
								labels, block);
					}
					worklist.addAll(bs);

				} else if (code instanceof Codes.Goto) {
					transform((Codes.Goto) code, branch, labels, block);
					worklist.push(branch);
				} else if (code instanceof Codes.Return) {
					transform((Codes.Return) code, branch);
					exitBranches.add(branch);
				} else if (code instanceof Codes.Fail) {
					transform((Codes.Fail) code, branch, block);
					exitBranches.add(branch);
				} else {
					// Unit statement. First, check whether or not there are any
					// preconditions for this statement and, if so, add
					// appropriate verification conditions to enforce them.
					Code.Unit unit = (Code.Unit) code;
					Pair<String,Expr>[] preconditions = getPreconditions(unit, branch, environment, block);
					if (preconditions.length > 0) {
						// This bytecode has one or more preconditions which
						// need to be asserted. Therefore, for each, create a
						// failed branch to ensure the precondition is met.
						for (int i = 0; i != preconditions.length; ++i) {
							Pair<String,Expr> p = preconditions[i];							
							Expr vc = buildVerificationCondition(p.second(),
									branch, environment, block);
							wycsFile.add(wycsFile.new Assert(p.first(), vc,
									toWycsAttributes(block.attributes(branch
											.pc()))));
						}
						// We need to fork the branch here, because it must have
						// INTERNAL state by now (i.e. because of the forks
						// above).
						branch = branch.fork();
					}
					//
					transform(unit, block, branch);
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
			internalFailure("unreacahble code reached", filename,
					block.attributes(parent));
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
				CodeBlock.Index i_pc = i_branch.pc();
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

	/**
	 * Generate verification conditions to enforce the necessary preconditions
	 * for a given bytecode. For example, to protect against division by zero or
	 * an out-of-bounds access.
	 * 
	 * @param code
	 * @param branch
	 * @param branches
	 * @param block
	 */
	public Pair<String,Expr>[] getPreconditions(Code.Unit code, VcBranch branch,
			Type[] environment, AttributedCodeBlock block) {
		//
		try {
			switch (code.opcode()) {
			case Code.OPCODE_div:
			case Code.OPCODE_rem:
				return divideByZeroCheck((Codes.BinaryOperator) code, branch);
			case Code.OPCODE_indexof:
				return indexOutOfBoundsChecks((Codes.IndexOf) code, branch);
			case Code.OPCODE_update:
				return updateChecks((Codes.Update) code, branch);
			case Code.OPCODE_invokefn:
			case Code.OPCODE_invokefnv:
			case Code.OPCODE_invokemd:
			case Code.OPCODE_invokemdv:
				return preconditionCheck((Codes.Invoke) code, branch, environment, block);
			}
			return new Pair[0];
		} catch (Exception e) {
			internalFailure(e.getMessage(), filename, e);
			return null; // deadcode
		}
	}

	/**
	 * Generate preconditions to protected against a possible divide by zero.
	 * This essentially boils down to ensureing the divisor is non-zero.
	 * 
	 * @param binOp
	 *            --- The division or remainder bytecode
	 * @param branch
	 *            --- The branch the division is on.
	 * @return
	 */
	public Pair<String,Expr>[] divideByZeroCheck(Codes.BinaryOperator binOp, VcBranch branch) {
		Expr rhs = branch.read(binOp.operand(1));
		Value zero;
		if (binOp.type() instanceof Type.Int) {
			zero = Value.Integer(BigInteger.ZERO);
		} else {
			zero = Value.Decimal(BigDecimal.ZERO);
		}
		Expr.Constant constant = new Expr.Constant(zero, rhs.attributes());
		return new Pair[] { new Pair("division by zero", new Expr.Binary(
				Expr.Binary.Op.NEQ, rhs, constant, rhs.attributes())) };
	}

	/**
	 * Generate preconditions necessary to protect against an out-of-bounds
	 * access. For lists, this means ensuring the index is non-negative and less
	 * than the list length.
	 * 
	 * @param code
	 *            --- The indexOf bytecode
	 * @param branch
	 *            --- The branch the bytecode is on.
	 * @return
	 */
	public Pair<String,Expr>[] indexOutOfBoundsChecks(Codes.IndexOf code, VcBranch branch) {
		if (code.type() instanceof Type.EffectiveList) {
			Expr src = branch.read(code.operand(0));
			Expr idx = branch.read(code.operand(1));
			Expr zero = new Expr.Constant(Value.Integer(BigInteger.ZERO),
					idx.attributes());
			Expr length = new Expr.Unary(Expr.Unary.Op.LENGTHOF, src,
					idx.attributes());
			return new Pair[] {
					new Pair("index out of bounds (negative)", new Expr.Binary(
							Expr.Binary.Op.GTEQ, idx, zero, idx.attributes())),
					new Pair("index out of bounds (not less than length)",
							new Expr.Binary(Expr.Binary.Op.LT, idx, length,
									idx.attributes())), };			
		} else {
			// FIXME: should do something here! At a minimum, generate a warning
			// that this has not been implemented yet.
			return new Pair[0];
		}
	}

	/**
	 * Generate preconditions necessary to ensure the preconditions for a method
	 * or method invocation are met.
	 * 
	 * @param code
	 *            --- The invoke bytecode
	 * @param branch
	 *            --- The branch on which the invocation is on.
	 * @param block
	 *            --- The containing block of code.
	 * @return
	 * @throws Exception
	 */
	public Pair<String,Expr>[] preconditionCheck(Codes.Invoke code, VcBranch branch,
			Type[] environment, AttributedCodeBlock block) throws Exception {
		ArrayList<Pair<String,Expr>> preconditions = new ArrayList<>();
		//
		// First, check for any potentially constrained types.    
		//
		List<wyil.lang.Attribute> attributes = block.attributes(branch.pc());
		List<Type> code_type_params = code.type().params();		
		int[] code_operands = code.operands();
		for (int i = 0; i != code_operands.length; ++i) {
			Type t = code_type_params.get(i);
			if (containsNominal(t, attributes)) {
				int operand = code_operands[i];
				Type rawType = expand(environment[operand],attributes);
				Expr rawTest = new Expr.Is(branch.read(operand), convert(
						rawType, attributes));
				Expr nominalTest = new Expr.Is(branch.read(operand), convert(t,
						attributes));
				preconditions.add(new Pair(
						"type invariant not satisfied (argument " + i + ")",
						new Expr.Binary(Expr.Binary.Op.IMPLIES, rawTest,
								nominalTest)));
			}
		}
		//
		List<AttributedCodeBlock> requires = findPrecondition(code.name,
				code.type(), block, branch);
		//
		if (requires.size() > 0) {
			// First, read out the operands from the branch
			Expr[] operands = new Expr[code_operands.length];
			for (int i = 0; i != code_operands.length; ++i) {
				operands[i] = branch.read(code_operands[i]);
			}
			// To check the pre-condition holds after the method, we
			// simply called the corresponding pre-condition macros.
			String prefix = code.name.name() + "_requires_";

			Expr argument = operands.length == 1 ? operands[0] : new Expr.Nary(
					Expr.Nary.Op.TUPLE, operands);
			for (int i = 0; i != requires.size(); ++i) {
				Expr precondition = new Expr.Invoke(prefix + i,
						code.name.module(), Collections.EMPTY_LIST, argument);
				preconditions.add(new Pair<String,Expr>("precondition not satisfied",precondition));

			}
		}
		return preconditions.toArray(new Pair[preconditions.size()]);
	}

	/**
	 * Ensure all preconditions for an update bytecode are met. For example,
	 * that any list updates are within bounds, etc.
	 * 
	 * @param code
	 *            --- The update bytecode.
	 * @param branch
	 *            --- The branch containing the update bytecode.
	 * @return
	 */
	public Pair<String,Expr>[] updateChecks(Codes.Update code, VcBranch branch) {
		ArrayList<Pair<String,Expr>> preconditions = new ArrayList<Pair<String,Expr>>();

		Expr src = branch.read(code.target());

		for (Codes.LVal lval : code) {
			if (lval instanceof Codes.ListLVal) {
				Codes.ListLVal lv = (Codes.ListLVal) lval;
				Expr idx = branch.read(lv.indexOperand);
				Expr zero = new Expr.Constant(Value.Integer(BigInteger.ZERO),
						idx.attributes());
				Expr length = new Expr.Unary(Expr.Unary.Op.LENGTHOF, src,
						idx.attributes());
				preconditions.add(new Pair("index out of bounds (negative)",
						new Expr.Binary(Expr.Binary.Op.GTEQ, idx, zero, idx
								.attributes())));
				preconditions.add(new Pair(
						"index out of bounds (not less than length)",
						new Expr.Binary(Expr.Binary.Op.LT, idx, length, idx
								.attributes())));
				src = new Expr.IndexOf(src, idx);
			} else if (lval instanceof Codes.RecordLVal) {
				Codes.RecordLVal lv = (Codes.RecordLVal) lval;
				ArrayList<String> fields = new ArrayList<String>(lv.rawType()
						.fields().keySet());
				Collections.sort(fields);
				Expr index = new Expr.Constant(Value.Integer(BigInteger
						.valueOf(fields.indexOf(lv.field))));
				src = new Expr.IndexOf(src, index);
			} else {
				// FIXME: need to implement dereference operations.
			}
		}

		return preconditions.toArray(new Pair[preconditions.size()]);
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
	protected List<VcBranch> transform(Codes.Loop code, VcBranch branch,
			Type[] environment, Map<String, CodeBlock.Index> labels,
			AttributedCodeBlock block) {
		return transformLoopHelper(code, branch, environment, labels, block)
				.second();
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
	protected List<VcBranch> transform(Codes.ForAll code, VcBranch branch,
			Type[] environment, Map<String, CodeBlock.Index> labels,
			AttributedCodeBlock block) {

		// Write an arbitrary value to the index operand. This is necessary to
		// ensure that there is something there if it is used within the loop
		// body.
		branch.havoc(code.indexOperand);
		//
		Expr index = branch.read(code.indexOperand);
		if (code.type instanceof Type.List) {
			// FIXME: This case is needed to handle the discrepancy between
			// lists and maps. Eventually, I plan to eliminate this discrepancy.
			// FIXME: This probably doesn't work because the special
			// variable created won't be quantified when generating the
			// verification condition.
			Expr.Variable i = new Expr.Variable("_"
					+ ((Expr.Variable) index).name);
			index = new Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { i, index });
		}
		branch.assume(new Expr.Binary(Expr.Binary.Op.IN, index, branch
				.read(code.sourceOperand)));
		// This is the case for a normal forall loop.
		Pair<VcBranch, List<VcBranch>> p = transformLoopHelper(code, branch,
				environment, labels, block);
		//
		List<VcBranch> exitBranches = p.second();
		exitBranches.add(p.first());
		return exitBranches;
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
	protected List<VcBranch> transform(Codes.Quantify code, VcBranch branch,
			Type[] environment, Map<String, CodeBlock.Index> labels,
			AttributedCodeBlock block) {
		// Write an arbitrary value to the index operand. This is necessary to
		// ensure that there is something there if it is used within the loop
		// body.
		branch.havoc(code.indexOperand);
		//
		VcBranch original = branch.fork();
		branch = branch.fork();
		// This represents a quantifier looop
		Pair<VcBranch, List<VcBranch>> p = transformQuantifierHelper(code,
				branch, environment, labels, block);
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
	protected List<VcBranch> extractQuantifiers(Codes.ForAll code,
			VcBranch root, VcBranch fallThru, List<VcBranch> exitBranches) {
		// First, setup some helper variables for use in the remainder.
		SyntacticType elementType = convert(code.type.element(),
				Collections.EMPTY_LIST);
		Expr index = root.read(code.indexOperand);
		TypePattern pattern = new TypePattern.Leaf(elementType,
				(Expr.Variable) index);
		if (code.type instanceof Type.List) {
			// FIXME: This case is needed to handle the discrepancy between
			// lists and
			// maps. Eventually, I plan to eliminate this discrepancy.
			Expr.Variable i = new Expr.Variable("_"
					+ ((Expr.Variable) index).name);
			index = new Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { i, index });
			pattern = new TypePattern.Tuple(new TypePattern[] {
					new TypePattern.Leaf(new SyntacticType.Int(), i), pattern });
		}
		Expr elementOf = new Expr.Binary(Expr.Binary.Op.IN, index,
				root.read(code.sourceOperand));
		ArrayList<VcBranch> qBranches = new ArrayList<VcBranch>();
		// Second, deal with the universally quantified fall-thru branch. We
		// fork the root for this in order not to disturb it. We also must
		// include the elementOf which implies for the forall body.
		Expr forallBody = generateAssumptions(fallThru, root);
		fallThru = root.fork();
		fallThru.assume(new Expr.ForAll(pattern, new Expr.Binary(
				Expr.Binary.Op.IMPLIES, elementOf, forallBody)));
		fallThru.goTo(fallThru.pc().next());
		qBranches.add(fallThru);
		// Finally, deal with existential branches next. We must fork the root
		// again for this, since it will now be immutable.
		for (int i = 0; i != exitBranches.size(); ++i) {
			VcBranch b = exitBranches.get(i);
			Expr body = generateAssumptions(b, root);
			body = new Expr.Binary(Expr.Binary.Op.AND, elementOf, body);
			CodeBlock.Index target = b.pc();
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
			Codes.Loop code, VcBranch branch, Type[] environment,
			Map<String, CodeBlock.Index> labels, AttributedCodeBlock block) {
		// The loopPc gives the block index of the loop bytecode.
		CodeBlock.Index loopPc = branch.pc();
		// This is the easy case, as there is no loop invariant. Therefore,
		// we just havoc modified variables at the beginning and then allow
		// branches to exit the loop as normal. Branches which reach the end
		// of the loop body are returned to be universally quantified
		havocVariables(code.modifiedOperands, branch);
		VcBranch activeBranch = branch.fork();
		// Now, run through loop body. This will produce several kinds of
		// branch. Those which have terminated or branched out of the loop body,
		// and those which have reached the end of the loop body. ).
		return transform(loopPc, 0, activeBranch, false, environment, labels,
				block);
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
	protected Pair<VcBranch, List<VcBranch>> transformLoopHelper(
			Codes.Loop code, VcBranch branch, Type[] environment,
			Map<String, CodeBlock.Index> labels, AttributedCodeBlock block) {
		// The loopPc gives the block index of the loop bytecode.
		CodeBlock.Index loopPc = branch.pc();
		int invariantOffset = getInvariantOffset(code);

		// First thing we need to do is determine whether or not this loop has a
		// loop invariant, as this affects how we will approach it.
		if (invariantOffset == -1) {
			return transformLoopWithoutInvariant(code, branch, environment,
					labels, block);
		} else {
			// Determine how many invariant blocks there are, as there might be
			// more than one. In the case that there is more than one, they are
			// assumed to be arranged consecutively one after the other.
			int numberOfInvariants = 0;
			for (int i = invariantOffset; i < code.size()
					&& code.get(i) instanceof Codes.Invariant; ++i) {
				numberOfInvariants = numberOfInvariants+1;
			}
			//
			CodeBlock.Index firstInvariantPc = new CodeBlock.Index(loopPc,
					invariantOffset);			
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
				buildInvariantMacro(firstInvariantPc.next(i), variables, environment, block);
			}
			// This is the harder case as we must account for the loop invariant
			// properly. To do this, we allow the loop to execute upto the loop
			// invariant using the current branch state. At this point, we havoc
			// modified variables and then assume the loop invariant, before
			// running through the loop until the invariant is reached again.
			Pair<VcBranch, List<VcBranch>> p = transform(loopPc, 0, branch,
					true, environment, labels, block);
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
				CodeBlock.Index invariantPc = firstInvariantPc.next(i);
				String invariantMacroName = invariantMacroPrefix
						+ invariantPc.toString().replace(".", "_");
				Expr.Invoke invariant = buildInvariantCall(activeBranch,
						invariantMacroName, variables);
				Expr vc = buildVerificationCondition(invariant, activeBranch,
						environment, block);
				wycsFile.add(wycsFile.new Assert(
						"loop invariant does not hold on entry", vc,
						toWycsAttributes(block.attributes(invariantPc))));
			}
			// Assume invariant holds for inductive case. To this, we first
			// havoc all modified variables to ensure that information about
			// them is not carried forward from before the loop. Then, we assume
			// the invariant macro holds in the current branch state.
			havocVariables(code.modifiedOperands, activeBranch);
			for (int i = 0; i != numberOfInvariants; ++i) {
				CodeBlock.Index invariantPc = firstInvariantPc.next(i);
				String invariantMacroName = invariantMacroPrefix
						+ invariantPc.toString().replace(".", "_");
				Expr.Invoke invariant = buildInvariantCall(activeBranch, invariantMacroName,
						variables);
				activeBranch.assume(invariant);
			}
			// Process inductive case for this branch by allowing it to
			// execute around the loop until the invariant is found again.
			// Branches which prematurely exit the loop are passed into the list
			// of exit branches. These are valid as they only have information
			// from the loop invariant.
			p = transform(loopPc, invariantOffset + numberOfInvariants, activeBranch, true,
					environment, labels, block);
			activeBranch = p.first();
			exitBranches.addAll(p.second());
			// Reestablish loop invariant. To do this, we generate a
			// verification condition that asserts the invariant macro given the
			// current branch state.
			for (int i = 0; i != numberOfInvariants; ++i) {
				CodeBlock.Index invariantPc = firstInvariantPc.next(i);
				String invariantMacroName = invariantMacroPrefix
						+ invariantPc.toString().replace(".", "_");
				Expr.Invoke invariant = buildInvariantCall(activeBranch,
						invariantMacroName, variables);
				Expr vc = buildVerificationCondition(invariant, activeBranch,
						environment, block);
				wycsFile.add(wycsFile.new Assert("loop invariant not restored",
						vc, toWycsAttributes(block.attributes(invariantPc))));
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
			Codes.Loop code, VcBranch branch, Type[] environment,
			Map<String, CodeBlock.Index> labels, AttributedCodeBlock block) {
		CodeBlock.Index loopPc = branch.pc();
		// This is the easy case, as there is no loop invariant. Therefore,
		// we just havoc modified variables at the beginning and then allow
		// branches to exit the loop as normal. Branches which reach the end
		// of the loop body can be discarded as they represent correct
		// execution through the loop.
		havocVariables(code.modifiedOperands, branch);
		VcBranch fallThru = branch.fork();
		VcBranch activeBranch = branch.fork();
		// Now, run through loop body. This will produce several kinds of
		// branch. Those which have terminated or branched out of the loop body,
		// and those which have reached the end of the loop body. All branches
		// in the former case go straight onto the list of returned branches.
		// Those in the latter case are discarded (as discussed above).
		Pair<VcBranch, List<VcBranch>> p = transform(loopPc, 0, activeBranch,
				false, environment, labels, block);
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
	private void buildInvariantMacro(CodeBlock.Index invariantPC,
			boolean[] variables, Type[] environment, AttributedCodeBlock block) {
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
		String pc = invariantPC.toString().replace(".", "_");
		buildMacroBlock(method.name() + "_loopinvariant_" + pc, invariantPC,
				block, types);
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
		Expr argument = arguments.size() == 1 ? arguments.get(1)
				: new Expr.Nary(Expr.Nary.Op.TUPLE, arguments);
		return new Expr.Invoke(name, wycsFile.id(), Collections.EMPTY_LIST,
				argument);
	}

	/**
	 * Check whether a given loop bytecode contains an invariant, or not.
	 * 
	 * @param branch
	 * @return
	 */
	private int getInvariantOffset(Codes.Loop loop) {
		for (int i = 0; i != loop.size(); ++i) {
			if (loop.get(i) instanceof Codes.Invariant) {
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
	protected List<VcBranch> transform(Codes.If code, VcBranch branch,
			Map<String, CodeBlock.Index> labels, AttributedCodeBlock block) {
		// First, clone and register the true branch
		VcBranch trueBranch = branch.fork();
		VcBranch falseBranch = branch.fork();
		// Second assume the condition on each branch
		Expr.Binary trueTest = buildTest(code.op, code.leftOperand,
				code.rightOperand, code.type, block, trueBranch);
		trueBranch.assume(trueTest);
		falseBranch.assume(invert(trueTest));
		// Third, dispatch branches to their targets
		falseBranch.goTo(branch.pc().next());
		trueBranch.goTo(labels.get(code.target));
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
	protected List<VcBranch> transform(Codes.IfIs code, VcBranch branch,
			Map<String, CodeBlock.Index> labels, AttributedCodeBlock block) {
		ArrayList<VcBranch> exitBranches = new ArrayList<VcBranch>();
		// In this case, both branches are reachable.
		// First, clone and register the branch
		VcBranch falseBranch = branch.fork();
		VcBranch trueBranch = branch.fork();
		// Second add appropriate runtime type tests
		List<wyil.lang.Attribute> attributes = block.attributes(branch.pc());
		Collection<wycc.lang.Attribute> wycsAttributes = toWycsAttributes(attributes); 
		SyntacticType trueType = convert(code.rightOperand, attributes);
		SyntacticType falseType = new SyntacticType.Negation(convert(code.rightOperand,
				attributes), wycsAttributes);
		trueBranch.assume(new Expr.Is(branch.read(code.operand), trueType,
				wycsAttributes));
		falseBranch.assume(new Expr.Is(branch.read(code.operand), falseType,
				wycsAttributes));
		// Finally dispatch the branches
		falseBranch.goTo(branch.pc().next());
		trueBranch.goTo(labels.get(code.target));
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
	protected List<VcBranch> transform(Codes.Switch code, VcBranch branch,
			Map<String, CodeBlock.Index> labels, AttributedCodeBlock block) {
		ArrayList<VcBranch> exitBranches = new ArrayList<VcBranch>();
		VcBranch defaultBranch = branch.fork();

		// Process each case individually, whilst also updating the default
		// branch.
		for (int i = 0; i != code.branches.size(); ++i) {
			// First, for each case fork a new branch to traverse it.
			VcBranch caseBranch = branch.fork();
			// Second, for each case, assume that the variable switched on
			// matches the give case value. Likewise, assume that the default
			// branch does *not* equal this value.
			Constant caseValue = code.branches.get(i).first();
			// Second, on the new branch we need assume that the variable being
			// switched on matches the given value.
			Expr src = branch.read(code.operand);
			Expr constant = new Expr.Constant(
					convert(caseValue, block, branch),
					toWycsAttributes(block.attributes(branch.pc())));
			caseBranch.assume(new Expr.Binary(Expr.Binary.Op.EQ, src, constant,
					toWycsAttributes(block.attributes(branch.pc()))));
			// Third, on the default branch we can assume that the variable
			// being switched is *not* the given value.
			defaultBranch.assume(new Expr.Binary(Expr.Binary.Op.NEQ, src,
					constant, toWycsAttributes(block.attributes(branch.pc()))));
			// Finally, dispatch branch
			caseBranch.goTo(labels.get(code.branches.get(i).second()));
			exitBranches.add(caseBranch);
		}

		// Finally, the dispatch the default branch to the default target.
		defaultBranch.goTo(labels.get(code.defaultTarget));
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
			Codes.AssertOrAssume code, boolean isAssert, VcBranch branch,
			Type[] environment, Map<String, CodeBlock.Index> labels,
			AttributedCodeBlock block) {
		int start = wycsFile.declarations().size();
		// First, transform the given branch through the assert or assume block.
		// This will produce one or more exit branches, some of which may have
		// reached failed states and need to be turned into verification
		// conditions (for asserts only).
		CodeBlock.Index pc = branch.pc();
		Pair<VcBranch, List<VcBranch>> p = transform(pc, 0, branch, false,
				environment, labels, block);
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
			while(wycsFile.declarations().size() > start) {
				wycsFile.declarations().remove(wycsFile.declarations().size()-1);
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
	protected void transform(Codes.Goto code, final VcBranch branch,
			Map<String, CodeBlock.Index> labels, AttributedCodeBlock block) {
		branch.goTo(labels.get(code.target));
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
	protected void transform(Codes.Fail code, VcBranch branch,
			AttributedCodeBlock block) {
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
	protected void transform(Codes.Return code, VcBranch branch) {
		// Marking the branch as terminated indicates that it is no longer
		// active. Thus, the original callers of this block transformation can
		// subsequently extract the constraints which hold at the point of the
		// return.
		branch.setState(VcBranch.State.TERMINATED);
	}

	// ===============================================================================
	// Unit Bytecodes
	// ===============================================================================

	/**
	 * Dispatch transform over unit bytecodes. Each unit bytecode is guaranteed
	 * to continue afterwards, and not to fork any new branches.
	 * 
	 * @param code
	 *            The bytecode being transformed over.
	 * @param block
	 *            The root block being iterated over.
	 * @param branch
	 *            The branch on entry to the bytecode.
	 */
	protected void transform(Code.Unit code, AttributedCodeBlock block,
			VcBranch branch) {
		try {
			if (code instanceof Codes.LengthOf) {
				transformUnary(Expr.Unary.Op.LENGTHOF, (Codes.LengthOf) code,
						branch, block);
			} else if (code instanceof Codes.BinaryOperator) {
				Codes.BinaryOperator bc = (Codes.BinaryOperator) code;
				transformBinary(binaryOperatorMap[bc.kind.ordinal()], bc,
						branch, block);
			} else if (code instanceof Codes.ListOperator) {
				Codes.ListOperator bc = (Codes.ListOperator) code;
				transformBinary(Expr.Binary.Op.LISTAPPEND, bc, branch, block);
			} else if (code instanceof Codes.NewList) {
				transformNary(Expr.Nary.Op.LIST, (Codes.NewList) code, branch,
						block);
			} else if (code instanceof Codes.NewRecord) {
				transformNary(Expr.Nary.Op.TUPLE, (Codes.NewRecord) code,
						branch, block);
			} else if (code instanceof Codes.NewTuple) {
				transformNary(Expr.Nary.Op.TUPLE, (Codes.NewTuple) code,
						branch, block);
			} else if (code instanceof Codes.Convert) {
				transform((Codes.Convert) code, block, branch);
			} else if (code instanceof Codes.Const) {
				transform((Codes.Const) code, block, branch);
			} else if (code instanceof Codes.Debug) {
				// skip
			} else if (code instanceof Codes.FieldLoad) {
				transform((Codes.FieldLoad) code, block, branch);
			} else if (code instanceof Codes.IndirectInvoke) {
				transform((Codes.IndirectInvoke) code, block, branch);
			} else if (code instanceof Codes.Invoke) {
				transform((Codes.Invoke) code, block, branch);
			} else if (code instanceof Codes.Invert) {
				transform((Codes.Invert) code, block, branch);
			} else if (code instanceof Codes.Label) {
				// skip
			} else if (code instanceof Codes.SubList) {
				transform((Codes.SubList) code, block, branch);
			} else if (code instanceof Codes.IndexOf) {
				transform((Codes.IndexOf) code, block, branch);
			} else if (code instanceof Codes.Move) {
				transform((Codes.Move) code, block, branch);
			} else if (code instanceof Codes.Assign) {
				transform((Codes.Assign) code, block, branch);
			} else if (code instanceof Codes.Update) {
				transform((Codes.Update) code, block, branch);
			} else if (code instanceof Codes.UnaryOperator) {
				transform((Codes.UnaryOperator) code, block, branch);
			} else if (code instanceof Codes.Dereference) {
				transform((Codes.Dereference) code, block, branch);
			} else if (code instanceof Codes.Nop) {
				// skip
			} else if (code instanceof Codes.NewObject) {
				transform((Codes.NewObject) code, block, branch);
			} else if (code instanceof Codes.TupleLoad) {
				transform((Codes.TupleLoad) code, block, branch);
			} else if (code instanceof Codes.Lambda) {
				transform((Codes.Lambda) code, block, branch);
			} else {
				internalFailure("unknown: " + code.getClass().getName(),
						filename, block.attributes(branch.pc()));
			}
		} catch (InternalFailure e) {
			throw e;
		} catch (SyntaxError e) {
			throw e;
		} catch (Throwable e) {
			internalFailure(e.getMessage(), filename, e,
					block.attributes(branch.pc()));
		}
	}

	protected void transform(Codes.Assign code, AttributedCodeBlock block,
			VcBranch branch) {
		branch.write(code.target(), branch.read(code.operand(0)));
	}

	/**
	 * Maps binary bytecodes into expression opcodes.
	 */
	private static Expr.Binary.Op[] binaryOperatorMap = { Expr.Binary.Op.ADD,
			Expr.Binary.Op.SUB, Expr.Binary.Op.MUL, Expr.Binary.Op.DIV,
			Expr.Binary.Op.REM, Expr.Binary.Op.RANGE, null, // bitwise or
			null, // bitwise xor
			null, // bitwise and
			null, // left shift
			null // right shift
	};

	/**
	 * Maps binary bytecodes into expression opcodes.
	 */
	private static Expr.Binary.Op[] setOperatorMap = { Expr.Binary.Op.SETUNION,
			Expr.Binary.Op.SETINTERSECTION, Expr.Binary.Op.SETDIFFERENCE };

	protected void transform(Codes.Convert code, AttributedCodeBlock block,
			VcBranch branch) {
		Collection<Attribute> attributes = toWycsAttributes(block
				.attributes(branch.pc()));
		Expr result = branch.read(code.operand(0));
		SyntacticType type = convert(code.result, block.attributes(branch.pc()));
		branch.write(code.target(), new Expr.Cast(type, result, attributes));
	}

	protected void transform(Codes.Const code, AttributedCodeBlock block,
			VcBranch branch) {
		Value val = convert(code.constant, block, branch);
		branch.write(code.target(), new Expr.Constant(val,
				toWycsAttributes(block.attributes(branch.pc()))));
	}

	protected void transform(Codes.Debug code, AttributedCodeBlock block,
			VcBranch branch) {
		// do nout
	}

	protected void transform(Codes.Dereference code, AttributedCodeBlock block,
			VcBranch branch) {
		branch.havoc(code.target());
	}

	protected void transform(Codes.FieldLoad code, AttributedCodeBlock block,
			VcBranch branch) {
		ArrayList<String> fields = new ArrayList<String>(code.type().fields()
				.keySet());
		Collections.sort(fields);
		Expr src = branch.read(code.operand(0));
		Expr index = new Expr.Constant(Value.Integer(BigInteger.valueOf(fields
				.indexOf(code.field))));
		Expr result = new Expr.IndexOf(src, index,
				toWycsAttributes(block.attributes(branch.pc())));
		branch.write(code.target(), result);
	}

	protected void transform(Codes.IndirectInvoke code,
			AttributedCodeBlock block, VcBranch branch) {
		if (code.target() != Codes.NULL_REG) {
			branch.havoc(code.target());
		}
	}

	protected void transform(Codes.Invoke code, AttributedCodeBlock block,
			VcBranch branch) throws Exception {
		Collection<wyil.lang.Attribute> attributes =  block
				.attributes(branch.pc());
		Collection<Attribute> wyccAttributes = toWycsAttributes(attributes);
		int[] code_operands = code.operands();
		
		if (code.target() != Codes.NULL_REG) {
			// Need to assume the post-condition holds.
			Expr[] operands = new Expr[code_operands.length];
			for (int i = 0; i != code_operands.length; ++i) {
				operands[i] = branch.read(code_operands[i]);
			}
			Expr argument = operands.length == 1 ? operands[0] : new Expr.Nary(
					Expr.Nary.Op.TUPLE, operands,wyccAttributes);
			branch.write(code.target(), new Expr.Invoke(code.name.name(),
					code.name.module(), Collections.EMPTY_LIST, argument,
					wyccAttributes));

			// This is a potential fix for #488, although it doesn't work
			if(containsNominal(code.type().ret(),attributes)) {
				// This is required to handle the implicit constraints implied
				// by a nominal type. See #488.
				Expr nominalTest = new Expr.Is(branch.read(code.target()),
						convert(code.type().ret(), attributes));
				branch.assume(nominalTest);
			}
			
			// Here, we must find the name of the corresponding postcondition so
			// that we can assume it.
			List<AttributedCodeBlock> ensures = findPostcondition(code.name,
					code.type(), block, branch);

			if (ensures.size() > 0) {
				// To assume the post-condition holds after the method, we
				// simply called the corresponding post-condition macros.
				Expr[] arguments = new Expr[operands.length + 1];
				System.arraycopy(operands, 0, arguments, 1, operands.length);
				arguments[0] = branch.read(code.target());
				String prefix = code.name.name() + "_ensures_";
				for (int i = 0; i != ensures.size(); ++i) {
					Expr.Invoke macro = new Expr.Invoke(prefix + i,
							code.name.module(), Collections.EMPTY_LIST,
							new Expr.Nary(Expr.Nary.Op.TUPLE, arguments));
					branch.assume(macro);
				}
			}
		}
	}

	protected void transform(Codes.Invert code, AttributedCodeBlock block,
			VcBranch branch) {
		branch.havoc(code.target());
	}

	protected void transform(Codes.IndexOf code, AttributedCodeBlock block,
			VcBranch branch) {
		Expr src = branch.read(code.operand(0));
		Expr idx = branch.read(code.operand(1));
		branch.write(code.target(), new Expr.IndexOf(src, idx,
				toWycsAttributes(block.attributes(branch.pc()))));
	}

	protected void transform(Codes.Lambda code, AttributedCodeBlock block,
			VcBranch branch) {
		// TODO: implement lambdas somehow?
		branch.havoc(code.target());
	}

	protected void transform(Codes.Move code, VcBranch branch) {
		branch.write(code.target(), branch.read(code.operand(0)));
	}

	protected void transform(Codes.NewObject code, AttributedCodeBlock block,
			VcBranch branch) {
		branch.havoc(code.target());
	}

	protected void transform(Codes.Nop code, AttributedCodeBlock block,
			VcBranch branch) {
		// do nout
	}

	protected void transform(Codes.SubList code, AttributedCodeBlock block,
			VcBranch branch) {
		transformTernary(Expr.Ternary.Op.SUBLIST, code, branch, block);
	}

	protected void transform(Codes.TupleLoad code, AttributedCodeBlock block,
			VcBranch branch) {
		Expr src = branch.read(code.operand(0));
		Expr index = new Expr.Constant(Value.Integer(BigInteger
				.valueOf(code.index)));
		Expr result = new Expr.IndexOf(src, index,
				toWycsAttributes(block.attributes(branch.pc())));
		branch.write(code.target(), result);
	}

	protected void transform(Codes.UnaryOperator code,
			AttributedCodeBlock block, VcBranch branch) {
		switch (code.kind) {
		case NEG:
			transformUnary(Expr.Unary.Op.NEG, code, branch, block);
			break;
		case NUMERATOR:
		case DENOMINATOR:
			branch.havoc(code.target());
			break;
		default:
			branch.havoc(code.target());
		}
	}

	protected void transform(Codes.Update code, AttributedCodeBlock block,
			VcBranch branch) {
		Expr result = branch.read(code.result());
		Expr source = branch.read(code.target());
		branch.write(code.target(),
				updateHelper(code.iterator(), source, result, branch, block));
	}

	protected Expr updateHelper(Iterator<Codes.LVal> iter, Expr source,
			Expr result, VcBranch branch, AttributedCodeBlock block) {
		if (!iter.hasNext()) {
			return result;
		} else {
			Collection<Attribute> attributes = toWycsAttributes(block
					.attributes(branch.pc()));
			Codes.LVal lv = iter.next();
			if (lv instanceof Codes.RecordLVal) {
				Codes.RecordLVal rlv = (Codes.RecordLVal) lv;

				// FIXME: following is broken for open records.
				ArrayList<String> fields = new ArrayList<String>(rlv.rawType()
						.fields().keySet());
				Collections.sort(fields);
				int index = fields.indexOf(rlv.field);
				Expr[] operands = new Expr[fields.size()];
				for (int i = 0; i != fields.size(); ++i) {
					Expr _i = new Expr.Constant(Value.Integer(BigInteger
							.valueOf(i)));
					if (i != index) {
						operands[i] = new Expr.IndexOf(source, _i, attributes);
					} else {
						operands[i] = updateHelper(iter, new Expr.IndexOf(
								source, _i, attributes), result, branch, block);
					}
				}
				return new Expr.Nary(Expr.Nary.Op.TUPLE, operands, attributes);
			} else if (lv instanceof Codes.ListLVal) {
				Codes.ListLVal rlv = (Codes.ListLVal) lv;
				Expr index = branch.read(rlv.indexOperand);
				result = updateHelper(iter, new Expr.IndexOf(source, index,
						attributes), result, branch, block);
				return new Expr.Ternary(Expr.Ternary.Op.UPDATE, source, index,
						result, toWycsAttributes(block.attributes(branch.pc())));
			} else {
				return source; // TODO
			}
		}
	}

	/**
	 * Transform an assignable unary bytecode using a given target operator.
	 * This must read the operand and then create the appropriate target
	 * expression. Finally, the result of the bytecode must be written back to
	 * the enclosing branch.
	 * 
	 * @param operator
	 *            --- The target operator
	 * @param code
	 *            --- The bytecode being translated
	 * @param branch
	 *            --- The enclosing branch
	 */
	protected void transformUnary(Expr.Unary.Op operator,
			Code.AbstractUnaryAssignable code, VcBranch branch,
			AttributedCodeBlock block) {
		Expr lhs = branch.read(code.operand(0));

		branch.write(code.target(), new Expr.Unary(operator, lhs,
				toWycsAttributes(block.attributes(branch.pc()))));
	}

	/**
	 * Transform an assignable binary bytecode using a given target operator.
	 * This must read both operands and then create the appropriate target
	 * expression. Finally, the result of the bytecode must be written back to
	 * the enclosing branch.
	 * 
	 * @param operator
	 *            --- The target operator
	 * @param code
	 *            --- The bytecode being translated
	 * @param branch
	 *            --- The enclosing branch
	 */
	protected void transformBinary(Expr.Binary.Op operator,
			Code.AbstractBinaryAssignable code, VcBranch branch,
			AttributedCodeBlock block) {
		Expr lhs = branch.read(code.operand(0));
		Expr rhs = branch.read(code.operand(1));

		if (operator != null) {
			branch.write(code.target(), new Expr.Binary(operator, lhs, rhs,
					toWycsAttributes(block.attributes(branch.pc()))));
		} else {
			// In this case, we have a binary operator which we don't know how
			// to translate into WyCS. Therefore, we need to invalidate the
			// target register to signal this.
			branch.havoc(code.target());
		}
	}

	/**
	 * Transform an assignable ternary bytecode using a given target operator.
	 * This must read all operands and then create the appropriate target
	 * expression. Finally, the result of the bytecode must be written back to
	 * the enclosing branch.
	 * 
	 * @param operator
	 *            --- The target operator
	 * @param code
	 *            --- The bytecode being translated
	 * @param branch
	 *            --- The enclosing branch
	 */
	protected void transformTernary(Expr.Ternary.Op operator,
			Code.AbstractNaryAssignable code, VcBranch branch,
			AttributedCodeBlock block) {
		Expr one = branch.read(code.operand(0));
		Expr two = branch.read(code.operand(1));
		Expr three = branch.read(code.operand(2));
		branch.write(code.target(), new Expr.Ternary(operator, one, two, three,
				toWycsAttributes(block.attributes(branch.pc()))));
	}

	/**
	 * Transform an assignable nary bytecode using a given target operator. This
	 * must read all operands and then create the appropriate target expression.
	 * Finally, the result of the bytecode must be written back to the enclosing
	 * branch.
	 * 
	 * @param operator
	 *            --- The target operator
	 * @param code
	 *            --- The bytecode being translated
	 * @param branch
	 *            --- The enclosing branch
	 */
	protected void transformNary(Expr.Nary.Op operator,
			Code.AbstractNaryAssignable code, VcBranch branch,
			AttributedCodeBlock block) {
		int[] code_operands = code.operands();
		Expr[] vals = new Expr[code_operands.length];
		for (int i = 0; i != vals.length; ++i) {
			vals[i] = branch.read(code_operands[i]);
		}
		branch.write(code.target(), new Expr.Nary(operator, vals,
				toWycsAttributes(block.attributes(branch.pc()))));
	}

	/**
	 * Find the precondition associated with a given function or method. This
	 * maybe contained in the same file, or in a different file. This may
	 * require loading that file in memory to access this information.
	 * 
	 * @param name
	 *            --- Fully qualified name of function
	 * @param fun
	 *            --- Type of fucntion.
	 * @param block
	 *            --- Enclosing block (for debugging purposes).
	 * @param branch
	 *            --- Enclosing branch (for debugging purposes).
	 * @return
	 * @throws Exception
	 */
	protected List<AttributedCodeBlock> findPrecondition(NameID name,
			Type.FunctionOrMethod fun, AttributedCodeBlock block,
			VcBranch branch) throws Exception {
		Path.Entry<WyilFile> e = builder.project().get(name.module(),
				WyilFile.ContentType);
		if (e == null) {
			syntaxError(
					errorMessage(ErrorMessages.RESOLUTION_ERROR, name.module()
							.toString()), filename, block.attributes(branch
							.pc()));
		}
		WyilFile m = e.read();
		WyilFile.FunctionOrMethod method = m.functionOrMethod(name.name(), fun);
	
		return method.precondition();
	}

	/**
	 * Find the postcondition associated with a given function or method. This
	 * maybe contained in the same file, or in a different file. This may
	 * require loading that file in memory to access this information.
	 * 
	 * @param name
	 *            --- Fully qualified name of function
	 * @param fun
	 *            --- Type of fucntion.
	 * @param block
	 *            --- Enclosing block (for debugging purposes).
	 * @param branch
	 *            --- Enclosing branch (for debugging purposes).
	 * @return
	 * @throws Exception
	 */
	protected List<AttributedCodeBlock> findPostcondition(NameID name,
			Type.FunctionOrMethod fun, AttributedCodeBlock block,
			VcBranch branch) throws Exception {
		Path.Entry<WyilFile> e = builder.project().get(name.module(),
				WyilFile.ContentType);
		if (e == null) {
			syntaxError(
					errorMessage(ErrorMessages.RESOLUTION_ERROR, name.module()
							.toString()), filename, block.attributes(branch
							.pc()));
		}
		WyilFile m = e.read();
		WyilFile.FunctionOrMethod method = m.functionOrMethod(name.name(), fun);
		return method.postcondition();
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
	protected void buildMacroBlock(String name, CodeBlock.Index root,
			AttributedCodeBlock block, List<Type> types) {
		int start = wycsFile.declarations().size();
		
		// first, generate a branch for traversing the external block.
		VcBranch master = new VcBranch(
				Math.max(block.numSlots(), types.size()), null);

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
				declarations.add(new TypePattern.Leaf(convert(type,
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
		List<VcBranch> exitBranches = transform(master, root, environment,
				block);
		// Remove any verification conditions that were generated when
		// processing this block.  		
		// FIXME: this is something of a hack for now. A better solution would
		// be for the verification conditions to be returned so that they
		// can be discussed.
		while(wycsFile.declarations().size() > start) {
			wycsFile.declarations().remove(wycsFile.declarations().size()-1);
		}
		//
		for (VcBranch exitBranch : exitBranches) {
			if (exitBranch.state() == VcBranch.State.TERMINATED) {
				Expr body = generateAssumptions(exitBranch, null);
				wycsFile.add(wycsFile.new Macro(name, Collections.EMPTY_LIST,
						type, body));
				return;
			}
		}		
		
		// It should be impossible to reach here.
		internalFailure("unreachable code", filename);
	}

	/**
	 * Construct a function with a given name representing a block of code. The
	 * function can then be called elsewhere as an uninterpreted function.
	 *
	 * @param name
	 *            --- the nameto give to the generated macro.
	 * @param params
	 *            --- parameter types to use.
	 * @param ret
	 *            --- return type to use
	 * @return
	 */
	protected void buildFunctionBlock(String name, List<Type> params, Type ret) {

		TypePattern.Leaf[] declarations = new TypePattern.Leaf[params.size()];
		// second, set initial environment
		for (int i = 0; i != params.size(); ++i) {
			Expr.Variable v = new Expr.Variable("r" + i);
			// FIXME: what attributes to pass into convert?
			declarations[i] = new TypePattern.Leaf(convert(params.get(i),
					Collections.EMPTY_LIST), v);
		}

		// Construct the type declaration for the new block macro
		TypePattern from = new TypePattern.Tuple(declarations);
		TypePattern to = new TypePattern.Leaf(convert(ret,
				Collections.EMPTY_LIST), null);

		wycsFile.add(wycsFile.new Function(name, Collections.EMPTY_LIST, from,
				to, null));
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
			Type[] environment, AttributedCodeBlock block, Expr... extraAssumptions) {
		// First construct the assertion which forms the basis of the
		// verification condition. The assertion must be shown to hold assuming
		// the assumptions did. Therefore, we construct an implication to
		// establish this.
		Expr assumptions = generateAssumptions(branch, null);
		
		for(Expr ea : extraAssumptions) {
			assumptions = new Expr.Binary(Expr.Binary.Op.AND, assumptions, ea);
		}
		
		assertion = new Expr.Binary(Expr.Binary.Op.IMPLIES, assumptions,
				assertion, toWycsAttributes(block.attributes(branch.pc())));

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
				int reg = determineRegister(var, branch.prefixes());
				type = environment[reg];
			}
			SyntacticType t = convert(type, block.attributes(branch.pc()));
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
	 * Determine the originating register number for this variable. This is made
	 * difficult because of variable versioning. All variable names and versions
	 * are encoded into a string of the form "n$v", where n is the variable name
	 * (A.K.A. the prefix) and "v" is the version.
	 * 
	 * @param variable
	 * @return
	 */
	private static int determineRegister(String variable, String[] prefixes) {
		// First determine the variable name (i.e. the prefix).
		int dollarIndex = variable.indexOf('$');
		String prefix;
		if (dollarIndex != -1) {
			// In this case, the variable name was of the form "n$v" where n is
			// the name, and v is the version. We don't need the version here,
			// so strip it off.
			prefix = variable.substring(0, dollarIndex);
		} else {
			// In this case, no version is given and, hence, there is nothing to
			// strip off.
			prefix = variable;
		}
		// Now, check whether this is a raw register identifier, or a named
		// variable identifier.
		if(prefix.startsWith("r%")) {
			// This is a raw register identifier. Therefore, we can extract the
			// register number directly.
			return Integer.parseInt(prefix.substring(2));
		} else {
			// This is a named varaible identifier. Therefore, we need to look
			// through the known list of named variable prefixes to see whether
			// or not we can find it (which we should be able to do).
			for (int i = 0; i != prefixes.length; ++i) {
				if (prefix.equals(prefixes[i])) {
					return i;
				}
			}
			// Should be impossible to get here.
			throw new RuntimeException(
					"Unreachable code reached whilst looking for: " + variable);
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

	// private Expr generateAssumptions(VcBranch branch) {
	// // First, flattern the branch graph into a topological ordering
	// List<VcBranch> branches = flattern(branch);
	//
	// // Second, initialise the node data
	// HashMap<VcBranch, Node> data = new HashMap<VcBranch, Node>();
	// for (int i = 0; i != branches.size(); ++i) {
	// VcBranch b = branches.get(i);
	// data.put(b, new Node());
	// }
	//
	// // Third, initialise parent counts
	// for (int i = 0; i != branches.size(); ++i) {
	// VcBranch b = branches.get(i);
	// for(VcBranch parent : b.parents()) {
	// data.get(parent).count = 0;
	// }
	// }
	// }
	//
	// private class Node {
	// public Expr constraint;
	// public int count;
	// }
	//
	// private List<VcBranch> flattern(VcBranch root) {
	// HashSet<VcBranch> visited = new HashSet<VcBranch>();
	// ArrayList<VcBranch> branches = new ArrayList<VcBranch>();
	// // Now, perform a depth-first search of the branch graph adding branches
	// // in reverse topological order.
	// flattern(root,visited,branches);
	// // The depth-first search we just conducted loaded all branches into the
	// // list in reverse topological order. For sanity, we just put them
	// // around the right way here. Technically we don't need to do this, but
	// // it just simplifies the remainder of the algorithm.
	// Collections.reverse(branches);
	// return branches;
	// }
	//
	// /**
	// * Perform depth-first traversal of the branch graph, storing visited
	// nodes
	// * in reverse topological order.
	// *
	// * @param b
	// * --- Branch currently being visited.
	// * @param visited
	// * --- Set of previously visited branches.
	// * @param branches
	// * --- reverse topogolical order being constructed.
	// */
	// private void flattern(VcBranch b, HashSet<VcBranch> visited,
	// List<VcBranch> branches) {
	// if (!visited.contains(b)) {
	// // this branch has not already been visited.
	// visited.add(b);
	// for (VcBranch parent : b.parents()) {
	// flattern(parent, visited, branches);
	// }
	// branches.add(b);
	// }
	// }

	/**
	 * Generate a formula representing a condition from an conditional bytecode.
	 *
	 * @param op
	 * @param stack
	 * @param elem
	 * @return
	 */
	private Expr.Binary buildTest(Codes.Comparator cop, int leftOperand,
			int rightOperand, Type type, AttributedCodeBlock block,
			VcBranch branch) {
		Expr lhs = branch.read(leftOperand);
		Expr rhs = branch.read(rightOperand);
		Expr.Binary.Op op;
		switch (cop) {
		case EQ:
			op = Expr.Binary.Op.EQ;
			break;
		case NEQ:
			op = Expr.Binary.Op.NEQ;
			break;
		case GTEQ:
			op = Expr.Binary.Op.GTEQ;
			break;
		case GT:
			op = Expr.Binary.Op.GT;
			break;
		case LTEQ:
			op = Expr.Binary.Op.LTEQ;
			break;
		case LT:
			op = Expr.Binary.Op.LT;
			break;
		case SUBSET:
			op = Expr.Binary.Op.SUBSET;
			break;
		case SUBSETEQ:
			op = Expr.Binary.Op.SUBSETEQ;
			break;
		case IN:
			op = Expr.Binary.Op.IN;
			break;
		default:
			internalFailure("unknown comparator (" + cop + ")", filename,
					block.attributes(branch.pc()));
			return null;
		}

		return new Expr.Binary(op, lhs, rhs,
				toWycsAttributes(block.attributes(branch.pc())));
	}

	/**
	 * Generate the logically inverted expression corresponding to a given
	 * comparator. For example, inverting "<=" gives ">", inverting "==" gives
	 * "!=", etc.
	 *
	 * @param test
	 *            --- the binary comparator being inverted.
	 * @return
	 */
	private Expr invert(Expr.Binary test) {
		Expr.Binary.Op op;
		switch (test.op) {
		case EQ:
			op = Expr.Binary.Op.NEQ;
			break;
		case NEQ:
			op = Expr.Binary.Op.EQ;
			break;
		case GTEQ:
			op = Expr.Binary.Op.LT;
			break;
		case GT:
			op = Expr.Binary.Op.LTEQ;
			break;
		case LTEQ:
			op = Expr.Binary.Op.GT;
			break;
		case LT:
			op = Expr.Binary.Op.GTEQ;
			break;
		case SUBSET:
		case SUBSETEQ:
		case SUPSET:
		case SUPSETEQ:
		case IN:
			// NOTE: it's tempting to think that inverting x SUBSET y should
			// give x SUPSETEQ y, but this is not correct. See #423.
			op = Expr.Binary.Op.IN;
			return new Expr.Unary(Expr.Unary.Op.NOT, new Expr.Binary(test.op,
					test.leftOperand, test.rightOperand, test.attributes()),
					test.attributes());
		default:
			wycc.lang.SyntaxError.internalFailure("unknown comparator ("
					+ test.op + ")", filename, test);
			return null;
		}

		return new Expr.Binary(op, test.leftOperand, test.rightOperand,
				test.attributes());
	}

	/**
	 * Convert a WyIL constant into its equivalent WyCS constant. In some cases,
	 * this is a direct translation. In other cases, WyIL constants are encoded
	 * using more primitive WyCS values.
	 * 
	 * @param c
	 *            --- The WyIL constant to be converted.
	 * @param block
	 *            --- The block within which this conversion is taking place
	 *            (for debugging purposes)
	 * @param branch
	 *            --- The branch within which this conversion is taking place
	 *            (for debugging purposes)
	 * @return
	 */
	public Value convert(Constant c, AttributedCodeBlock block, VcBranch branch) {
		if (c instanceof Constant.Null) {
			return wycs.core.Value.Null;
		} else if (c instanceof Constant.Bool) {
			Constant.Bool cb = (Constant.Bool) c;
			return wycs.core.Value.Bool(cb.value);
		} else if (c instanceof Constant.Byte) {
			Constant.Byte cb = (Constant.Byte) c;
			return wycs.core.Value.Integer(BigInteger.valueOf(cb.value));
		} else if (c instanceof Constant.Integer) {
			Constant.Integer cb = (Constant.Integer) c;
			return wycs.core.Value.Integer(cb.value);
		} else if (c instanceof Constant.Decimal) {
			Constant.Decimal cb = (Constant.Decimal) c;
			return wycs.core.Value.Decimal(cb.value);
		} else if (c instanceof Constant.List) {
			Constant.List cb = (Constant.List) c;
			List<Constant> cb_values = cb.values;
			ArrayList<Value> pairs = new ArrayList<Value>();
			for (int i = 0; i != cb_values.size(); ++i) {
				ArrayList<Value> pair = new ArrayList<Value>();
				pair.add(Value.Integer(BigInteger.valueOf(i)));
				pair.add(convert(cb_values.get(i), block, branch));
				pairs.add(Value.Tuple(pair));
			}
			return Value.Set(pairs);
		} else if (c instanceof Constant.Tuple) {
			Constant.Tuple cb = (Constant.Tuple) c;
			ArrayList<Value> values = new ArrayList<Value>();
			for (Constant v : cb.values) {
				values.add(convert(v, block, branch));
			}
			return wycs.core.Value.Tuple(values);
		} else if (c instanceof Constant.Record) {
			Constant.Record rb = (Constant.Record) c;

			// NOTE:: records are currently translated into WyCS as tuples,
			// where
			// each field is allocated a slot based on an alphabetical sorting
			// of field names. It's unclear at this stage whether or not that is
			// a general solution. In particular, it would seem to be brokwn for
			// type testing.

			ArrayList<String> fields = new ArrayList<String>(rb.values.keySet());
			Collections.sort(fields);
			ArrayList<Value> values = new ArrayList<Value>();
			for (String field : fields) {
				values.add(convert(rb.values.get(field), block, branch));
			}
			return wycs.core.Value.Tuple(values);
		} else {
			internalFailure("unknown constant encountered (" + c + ")",
					filename, block.attributes(branch.pc()));
			return null;
		}
	}

	private SyntacticType convert(List<Type> types, AttributedCodeBlock block,
			VcBranch branch) {
		ArrayList<SyntacticType> ntypes = new ArrayList<SyntacticType>();
		for (int i = 0; i != types.size(); ++i) {
			ntypes.add(convert(types.get(i), block.attributes(branch.pc())));
		}
		return new SyntacticType.Tuple(ntypes);
	}

	/**
	 * Convert a WyIL type into its equivalent WyCS type. In some cases, this is
	 * a direct translation. In other cases, WyIL constants are encoded using
	 * more primitive WyCS types.
	 * 
	 * @param t
	 *            --- The WyIL type to be converted.
	 * @param attributes
	 *            --- The attributes associated with the point of this
	 *            conversion. These are used for debugging purposes to associate
	 *            any errors generated with a source line.
	 * @return
	 */
	private SyntacticType convert(Type t,
			Collection<wyil.lang.Attribute> attributes) {
		// FIXME: this is fundamentally broken in the case of recursive types.
		// See Issue #298.
		if (t instanceof Type.Any) {
			return new SyntacticType.Any(toWycsAttributes(attributes));
		} else if (t instanceof Type.Void) {
			return new SyntacticType.Void(toWycsAttributes(attributes));
		} else if (t instanceof Type.Null) {
			return new SyntacticType.Null(toWycsAttributes(attributes));
		} else if (t instanceof Type.Bool) {
			return new SyntacticType.Bool(toWycsAttributes(attributes));
		} else if (t instanceof Type.Byte) {
			// FIXME: implement SyntacticType.Byte
			// return new SyntacticType.Byte(attributes(branch));
			return new SyntacticType.Int(toWycsAttributes(attributes));
		} else if (t instanceof Type.Int) {
			return new SyntacticType.Int(toWycsAttributes(attributes));
		} else if (t instanceof Type.Real) {
			return new SyntacticType.Real(toWycsAttributes(attributes));
		} else if (t instanceof Type.List) {
			Type.List lt = (Type.List) t;
			SyntacticType element = convert(lt.element(), attributes);
			// ugly.
			return new SyntacticType.List(element);
		} else if (t instanceof Type.Tuple) {
			Type.Tuple tt = (Type.Tuple) t;
			ArrayList<SyntacticType> elements = new ArrayList<SyntacticType>();
			for (int i = 0; i != tt.size(); ++i) {
				elements.add(convert(tt.element(i), attributes));
			}
			return new SyntacticType.Tuple(elements);
		} else if (t instanceof Type.Record) {
			Type.Record rt = (Type.Record) t;
			HashMap<String, Type> fields = rt.fields();
			ArrayList<String> names = new ArrayList<String>(fields.keySet());
			ArrayList<SyntacticType> elements = new ArrayList<SyntacticType>();
			Collections.sort(names);
			for (int i = 0; i != names.size(); ++i) {
				String field = names.get(i);
				elements.add(convert(fields.get(field), attributes));
			}
			return new SyntacticType.Tuple(elements);
		} else if (t instanceof Type.Reference) {
			// FIXME: how to translate this??
			return new SyntacticType.Any();
		} else if (t instanceof Type.Union) {
			Type.Union tu = (Type.Union) t;
			HashSet<Type> tu_elements = tu.bounds();
			ArrayList<SyntacticType> elements = new ArrayList<SyntacticType>();
			for (Type te : tu_elements) {
				elements.add(convert(te, attributes));
			}
			return new SyntacticType.Union(elements);
		} else if (t instanceof Type.Negation) {
			Type.Negation nt = (Type.Negation) t;
			SyntacticType element = convert(nt.element(), attributes);
			return new SyntacticType.Negation(element);
		} else if (t instanceof Type.FunctionOrMethod) {
			Type.FunctionOrMethod ft = (Type.FunctionOrMethod) t;
			return new SyntacticType.Any();
		} else if (t instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) t;
			NameID nid = nt.name();
			ArrayList<String> names = new ArrayList<String>();
			for (String pc : nid.module()) {
				names.add(pc);
			}
			names.add(nid.name());
			return new SyntacticType.Nominal(names,
					toWycsAttributes(attributes));
		} else {
			internalFailure("unknown type encountered ("
					+ t.getClass().getName() + ")", filename, attributes);
			return null;
		}
	}

	/**
	 * Make an objective assessment as to whether a type may include an
	 * invariant or not. The purpose here is reduce the number of verification
	 * conditions generated with respect to constrained types. The algorithm is
	 * currently very simple. It essentially looks to see whether or not the
	 * type contains a nominal component. If so, the answer is "yes", otherwise
	 * the answer is "no".
	 * 
	 * @return
	 */
	private boolean containsNominal(Type t,
			Collection<wyil.lang.Attribute> attributes) {
		// FIXME: this is fundamentally broken in the case of recursive types.
		// See Issue #298.
		if (t instanceof Type.Any || t instanceof Type.Void
				|| t instanceof Type.Null || t instanceof Type.Bool
				|| t instanceof Type.Byte || t instanceof Type.Int
				|| t instanceof Type.Real) {
			return false;
		} else if (t instanceof Type.List) {
			Type.List lt = (Type.List) t;
			return containsNominal(lt.element(), attributes);
		} else if (t instanceof Type.Tuple) {
			Type.Tuple tt = (Type.Tuple) t;
			for (int i = 0; i != tt.size(); ++i) {
				if (containsNominal(tt.element(i), attributes)) {
					return true;
				}
			}
			return false;
		} else if (t instanceof Type.Record) {
			Type.Record rt = (Type.Record) t;
			for (Type field : rt.fields().values()) {
				if (containsNominal(field, attributes)) {
					return true;
				}
			}
			return false;
		} else if (t instanceof Type.Reference) {
			Type.Reference lt = (Type.Reference) t;
			return containsNominal(lt.element(), attributes);
		} else if (t instanceof Type.Union) {
			Type.Union tu = (Type.Union) t;
			for (Type te : tu.bounds()) {
				if (containsNominal(te, attributes)) {
					return true;
				}
			}
			return false;
		} else if (t instanceof Type.Negation) {
			Type.Negation nt = (Type.Negation) t;
			return containsNominal(nt.element(), attributes);
		} else if (t instanceof Type.FunctionOrMethod) {
			Type.FunctionOrMethod ft = (Type.FunctionOrMethod) t;
			for (Type pt : ft.params()) {
				if (containsNominal(pt, attributes)) {
					return true;
				}
			}
			return containsNominal(ft.ret(), attributes);
		} else if (t instanceof Type.Nominal) {
			return true;
		} else {
			internalFailure("unknown type encountered ("
					+ t.getClass().getName() + ")", filename, attributes);
			return false;
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
	
	private static <T> List<T> prepend(T x, List<T> xs) {
		ArrayList<T> rs = new ArrayList<T>();
		rs.add(x);
		rs.addAll(xs);
		return rs;
	}

	private static Type[] toArray(List<Type> xs) {
		Type[] ts = new Type[xs.size()];
		for (int i = 0; i != xs.size(); ++i) {
			ts[i] = xs.get(i);
		}
		return ts;
	}

	/**
	 * Convert a list of WyIL attributes into a corresponding list of
	 * WycsAttributes. Note that, in some cases, no conversion is possible and
	 * such attributes are silently dropped.
	 * 
	 * @param branch
	 * @return
	 */
	private static Collection<wycc.lang.Attribute> toWycsAttributes(
			Collection<wyil.lang.Attribute> wyilAttributes) {
		ArrayList<wycc.lang.Attribute> wycsAttributes = new ArrayList<wycc.lang.Attribute>();
		// iterate each attribute and convert those which can be convered.
		for (wyil.lang.Attribute attr : wyilAttributes) {
			if (attr instanceof wyil.attributes.SourceLocation) {
				wyil.attributes.SourceLocation l = (wyil.attributes.SourceLocation) attr;
				wycsAttributes.add(new wycc.lang.Attribute.Source(l.start(), l
						.end(), 0));
			}
		}
		return wycsAttributes;
	}

	/**
	 * Returns the prefix array which gives the names of all registers declared
	 * in a given block.
	 * 
	 * @param d
	 * @return
	 */
	private static Pair<String[], Type[]> parseRegisterDeclarations(
			VariableDeclarations rds) {
		if (rds != null) {
			String[] prefixes = new String[rds.size()];
			Type[] types = new Type[rds.size()];
			for (int i = 0; i != prefixes.length; ++i) {
				VariableDeclarations.Declaration d = rds.get(i);
				prefixes[i] = d.name();
				types[i] = d.type();
			}
			return new Pair<>(prefixes, types);
		} else {
			return null;
		}
	}
}
