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

package wyil.checks;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import static wybs.lang.SyntaxError.internalFailure;
import wycs.solver.Solver;
import static wycs.solver.Solver.*;

import wyautl.core.Automaton;
import wyautl.io.PrettyAutomataWriter;
import wybs.lang.SyntaxError;
import wybs.lang.SyntaxError.InternalFailure;
import wyil.lang.Attribute;
import wyil.lang.Block;
import wyil.lang.Code;
import wycs.lang.Expr;
import wycs.lang.Stmt;

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
	 * Maintains the current assignment of variables to expressions.
	 */
	private final Expr[] environment;

	/**
	 * The stack of currently active scopes (e.g. for-loop). When the branch
	 * exits a scope, an exit scope event is generated in order that additional
	 * effects make be applied.
	 */
	private final ArrayList<Scope> scopes;
	
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
	 * Construct the master verification branch for a given code block. The
	 * master for a block has an origin <code>0</code> and an (initial) PC of
	 * <code>0</code> (i.e. the branch begins at the entry of the block).
	 * 
	 * @param automaton
	 *            --- the automaton to which constraints generated for this
	 *            block are stored.
	 * @param block
	 *            --- the block of code on which this branch is operating.
	 */
	public VerificationBranch(Block block) {
		this.parent = null;
		this.environment = new Expr[block.numSlots()];
		this.scopes = new ArrayList<Scope>();
		this.block = block;
		this.origin = 0;
		this.pc = 0;
		scopes.add(new Scope(block.size(), Collections.EMPTY_LIST));
	}

	/**
	 * Private constructor used for forking a child-branch from a parent branch.
	 * 
	 * @param parent
	 *            --- parent branch being forked from.
	 */
	private VerificationBranch(VerificationBranch parent) {
		this.parent = parent;
		this.environment = parent.environment.clone();
		this.scopes = new ArrayList<Scope>();
		this.block = parent.block;
		this.origin = parent.pc;
		this.pc = parent.pc;
		
		for(Scope scope : parent.scopes) {
			this.scopes.add(scope.clone());
		}
	}

	/**
	 * Return the current Program Counter (PC) value for this branch. This must
	 * be a valid index into the code block this branch is operating over.
	 * 
	 * @return
	 */
	public int pc() {
		return pc;
	}

	/**
	 * Get the block entry at the current PC position.
	 * 
	 * @return
	 */
	public Block.Entry entry() {
		return block.get(pc);
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
	 * Assign a given expression stored in the automaton to a given Wyil
	 * bytecode register.
	 * 
	 * @param register
	 * @param expr
	 */
	public void write(int register, Expr expr) {
		environment[register] = expr;
	}
	
	/**
	 * Generate a skolem constant (i.e. variable) which is guaranteed unique for the branch. 
	 * @return
	 */
	public Expr.Variable skolem() {
		return Expr.Variable("$" + skolemCount++);	
	}

	private static int skolemCount = 0;
	
	/**
	 * Terminate the current flow for a given register and begin a new one. In
	 * terms of static-single assignment, this means simply change the index of
	 * the register in question.
	 * 
	 * @param register
	 */
	public Expr.Variable invalidate(int register) {
		// to invalidate a variable, we assign it a "skolem" constant. That is,
		// a fresh variable which has been previously encountered in the
		// branch.
		Expr.Variable var = skolem();
		environment[register] = var;
		return var;
	}
	
	/**
	 * Invalidate all registers from <code>start</code> upto (but not including)
	 * <code>end</code>.
	 * 
	 * @param start
	 *            --- first register to invalidate.
	 * @param end
	 *            --- first register not to invalidate.
	 */
	public void invalidate(int start, int end) {
		for (int i = start; i != end; ++i) {
			invalidate(i);
		}
	}
	
	/**
	 * Return a reference into the automaton which represents all of the
	 * constraints that hold at this position in the branch.
	 * 
	 * @return
	 */
	public List<Stmt> constraints() {
		ArrayList<Stmt> constraints = new ArrayList<Stmt>();
		for (int i = 0; i != scopes.size(); ++i) {
			Scope scope = scopes.get(i);
			constraints.addAll(scope.constraints);
		}
		return constraints;
	}

	/**
	 * Add a given constraint to the list of constraints which are assumed to
	 * hold at this point.
	 * 
	 * @param constraint
	 */
	public void add(Stmt stmt) {
		topScope().constraints.add(stmt);
	}
	
	/**
	 * Add a given list of constraints to the list of constraints which are assumed to
	 * hold at this point.
	 * 
	 * @param constraints
	 */
	public void addAll(List<Stmt> stmts) {
		topScope().constraints.addAll(stmts);
	}

	/**
	 * Transform this branch into a list of constraints representing that which
	 * is known to hold at the end of the branch. The generated constraint will
	 * only be in terms of the given parameters and return value for the block.
	 * 
	 * @param transformer
	 *            --- responsible for transformining individual bytecodes into
	 *            constraints capturing their semantics.
	 * @return
	 */
	public List<Stmt> transform(VerificationTransformer transformer) {		
		ArrayList<VerificationBranch> children = new ArrayList<VerificationBranch>();
		int blockSize = block.size();
		while (pc < blockSize) {
									
			// first, check whether we're departing a scope or not.
			int top = scopes.size() - 1;
			while (top >= 0 && scopes.get(top).end < pc) {
				// yes, we're leaving a scope ... so notify transformer.
				Scope topScope = scopes.get(top);
				scopes.remove(top);
				dispatchExit(topScope, transformer);
				top = top - 1;
			}			
			
			// second, continue to transform the given bytecode
			Block.Entry entry = block.get(pc);
			Code code = entry.code;
			if(code instanceof Code.Goto) {				
				goTo(((Code.Goto) code).target);
			} else if(code instanceof Code.If) {
				Code.If ifc = (Code.If) code;
				VerificationBranch trueBranch = fork();	
				transformer.transform(ifc,this,trueBranch);
				trueBranch.goTo(ifc.target);
				children.add(trueBranch);
			} else if(code instanceof Code.IfIs) {
				Code.IfIs ifs = (Code.IfIs) code;
				VerificationBranch trueBranch = fork();				
				transformer.transform(ifs,this,trueBranch);
				trueBranch.goTo(ifs.target);
				children.add(trueBranch);
			} else if(code instanceof Code.ForAll) {
				Code.ForAll fall = (Code.ForAll) code;
				// FIXME: where should this go?
				for (int i : fall.modifiedOperands) {
					invalidate(i);
				}
				Expr.Variable var = invalidate(fall.indexOperand);
				
				scopes.add(new ForScope(fall, findLabelIndex(fall.target),
						Collections.EMPTY_LIST, read(fall.sourceOperand),
						var));
				transformer.transform(fall, this);
			} else if(code instanceof Code.Loop) {
				Code.Loop loop = (Code.Loop) code; 				
				// FIXME: where should this go?				
				for (int i : loop.modifiedOperands) {
					invalidate(i);
				}

				scopes.add(new LoopScope(loop, findLabelIndex(loop.target),
						Collections.EMPTY_LIST));
				transformer.transform(loop, this);
			} else if(code instanceof Code.LoopEnd) {
				top = scopes.size() - 1;
				LoopScope ls = (LoopScope) scopes.get(top);
				scopes.remove(top);
				if(ls instanceof ForScope) {
					ForScope fs = (ForScope) ls;
					transformer.end(fs,this);
				} else {
					// normal loop, so the branch ends here
					transformer.end(ls,this);
					break; 
				}
			} else if(code instanceof Code.TryCatch) {
				Code.TryCatch tc = (Code.TryCatch) code;
				scopes.add(new TryScope(findLabelIndex(tc.target),
						Collections.EMPTY_LIST));
				transformer.transform(tc, this);
			} else if(code instanceof Code.Return) {
				transformer.transform((Code.Return) code, this);
				break; // we're done!!!
			} else if(code instanceof Code.Throw) {
				transformer.transform((Code.Throw) code, this);
				break; // we're done!!!
			} else {				
				dispatch(transformer);				
			}

			// move on to next instruction.
			pc = pc + 1;
		}
		
		// Now, transform child branches!!!
		for(VerificationBranch child : children) {
			child.transform(transformer);
			join(child);
		}
		
		return constraints();
	}
		
	/**
	 * <p>
	 * Fork a child-branch from this branch. The child branch is (initially)
	 * identical in every way to the parent, however the expectation is that
	 * they will diverge.
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
	 * child branch to its logical starting point.
	 * </p>
	 * <p>
	 * A new environment is created for the child branch which, initially, is
	 * identical to that of the parent. As assignments to variables are made on
	 * either branch, these environments will move apart.
	 * </p>
	 * 
	 * @return --- The child branch which is forked off this branch.
	 */
	private VerificationBranch fork() {
		return new VerificationBranch(this);
	}
	
	/**
	 * <p>
	 * Merge descendant (i.e. a child or child-of-child, etc) branch back into
	 * this branch. The constraints for this branch must now correctly capture
	 * those constraints that hold coming from either branch (i.e. this
	 * represents a meet-point in the control-flow graph).
	 * </p>
	 * <p>
	 * To generate the constraints which hold after the meet, we take the
	 * logical OR of those constraints holding on this branch prior to the meet
	 * and those which hold on the incoming branch. For example, support we
	 * have:
	 * </p>
	 * 
	 * <pre>
	 * 	 y$0 != 0    y$0 != 0
	 *   && x$1 < 1  && x$2 >= 1   
	 *        ||      ||
	 *         \\    //
	 *          \\  //
	 *           \\//
	 *            ##
	 *   y$0 != 0 &&
	 * ((x$1 < 1 && x$3 == x$1) || 
	 *  (x$2 >= 1 && x$3 == x$2))
	 * </pre>
	 * <p>
	 * Here, we see that <code>y$0 != 0</code> is constant to both branches and
	 * is ommitted from the disjunction. Furthermore, we've added an assignment
	 * <code>x$3 == </code> onto both sides of the disjunction to capture the
	 * flow of variable <code>x</code> from both sides (since it was modified on
	 * at least one of the branches).
	 * </p>
	 * <p>
	 * One challenge is to determine constraints which are constant to both
	 * sides. Eliminating such constraints from the disjunction reduces the
	 * overall work of the constraint solver.
	 * </p>
	 * 
	 * @param incoming
	 *            --- The descendant branch which is being merged into this one.
	 */
	private void join(VerificationBranch incoming) {
		// First, determine new constraint sequence
		ArrayList<Stmt> common = new ArrayList<Stmt>();
		ArrayList<Expr> lhsConstraints = new ArrayList<Expr>();
		ArrayList<Expr> rhsConstraints = new ArrayList<Expr>();
		splitConstraints(incoming,common,lhsConstraints,rhsConstraints);				
			
		// Finally, put it all together
		Expr l = Expr.Nary(Expr.Nary.Op.AND,lhsConstraints);
		Expr r = Expr.Nary(Expr.Nary.Op.AND,rhsConstraints);
		
		// can now compute the logical OR of both branches
		Expr join = Expr.Nary(Expr.Nary.Op.OR, new Expr[]{l, r});

		// now, clear our sequential constraints since we can only have one
		// which holds now: namely, the or of the two branches.
		Scope top = topScope();
		top.constraints.clear();		
		top.constraints.addAll(common);
		top.constraints.add(Stmt.Assume(join));		
	}

	/**
	 * A region of bytecodes which requires special attention when the branch
	 * exits the scope. For example, when a branch exits the body of a for-loop,
	 * we must ensure that the appopriate loop-invariants hold, etc.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	private static class Scope implements Cloneable {
		public final ArrayList<Stmt> constraints;
		public int end;

		public Scope(int end, List<Stmt> constraints) {
			this.end = end;
			this.constraints = new ArrayList<Stmt>(constraints);
		}
		
		public Scope clone() {
			return new Scope(end, constraints);
		}
	}
			
	/**
	 * Represents the scope of a general loop bytecode.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 */
	public static class LoopScope<T extends Code.Loop> extends
			VerificationBranch.Scope {
		public final T loop;

		public LoopScope(T loop, int end, List<Stmt> constraints) {
			super(end,constraints);
			this.loop = loop;
		}
		
		public LoopScope<T> clone() {
			return new LoopScope(loop,end,constraints);
		}
	}
	
	/**
	 * Represents the scope of a forall bytecode
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class ForScope extends LoopScope<Code.ForAll> {
		public final Expr source;
		public final Expr.Variable index;
		
		public ForScope(Code.ForAll forall, int end, List<Stmt> constraints,
				Expr source, Expr.Variable index) {
			super(forall, end, constraints);
			this.index = index;
			this.source = source;
		}

		public ForScope clone() {
			return new ForScope(loop, end, constraints, source, index);
		}
	}
	
	/**
	 * Represents the scope of a general try-catch handler.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 */
	public static class TryScope extends
			VerificationBranch.Scope {
		
		public TryScope(int end, List<Stmt> constraints) {
			super(end,constraints);			
		}
		
		public TryScope clone() {
			return new TryScope(end,constraints);
		}
	}
	/**
	 * Dispatch on the given bytecode to the appropriate method in transformer
	 * for generating an appropriate constraint to capture the bytecodes
	 * semantics.
	 * 
	 * @return
	 */
	private void dispatch(VerificationTransformer transformer) {
		Code code = entry().code;		
		try {
			if(code instanceof Code.Assert) {
				transformer.transform((Code.Assert)code,this);
			} else if(code instanceof Code.Assume) {
				transformer.transform((Code.Assume)code,this);
			} else if(code instanceof Code.BinArithOp) {
				transformer.transform((Code.BinArithOp)code,this);
			} else if(code instanceof Code.Convert) {
				transformer.transform((Code.Convert)code,this);
			} else if(code instanceof Code.Const) {
				transformer.transform((Code.Const)code,this);
			} else if(code instanceof Code.Debug) {
				transformer.transform((Code.Debug)code,this);
			} else if(code instanceof Code.FieldLoad) {
				transformer.transform((Code.FieldLoad)code,this);			
			} else if(code instanceof Code.IndirectInvoke) {
				transformer.transform((Code.IndirectInvoke)code,this);
			} else if(code instanceof Code.Invoke) {
				transformer.transform((Code.Invoke)code,this);
			} else if(code instanceof Code.Invert) {
				transformer.transform((Code.Invert)code,this);
			} else if(code instanceof Code.Label) {
				// skip			
			} else if(code instanceof Code.BinListOp) {
				transformer.transform((Code.BinListOp)code,this);
			} else if(code instanceof Code.LengthOf) {
				transformer.transform((Code.LengthOf)code,this);
			} else if(code instanceof Code.SubList) {
				transformer.transform((Code.SubList)code,this);
			} else if(code instanceof Code.IndexOf) {
				transformer.transform((Code.IndexOf)code,this);
			} else if(code instanceof Code.Move) {
				transformer.transform((Code.Move)code,this);
			} else if(code instanceof Code.Assign) {
				transformer.transform((Code.Assign)code,this);
			} else if(code instanceof Code.Update) {
				transformer.transform((Code.Update)code,this);
			} else if(code instanceof Code.NewMap) {
				transformer.transform((Code.NewMap)code,this);
			} else if(code instanceof Code.NewList) {
				transformer.transform((Code.NewList)code,this);
			} else if(code instanceof Code.NewRecord) {
				transformer.transform((Code.NewRecord)code,this);
			} else if(code instanceof Code.NewSet) {
				transformer.transform((Code.NewSet)code,this);
			} else if(code instanceof Code.NewTuple) {
				transformer.transform((Code.NewTuple)code,this);
			} else if(code instanceof Code.UnArithOp) {
				transformer.transform((Code.UnArithOp)code,this);
			} else if(code instanceof Code.Dereference) {
				transformer.transform((Code.Dereference)code,this);
			} else if(code instanceof Code.Nop) {
				transformer.transform((Code.Nop)code,this);
			} else if(code instanceof Code.BinSetOp) {
				transformer.transform((Code.BinSetOp)code,this);
			} else if(code instanceof Code.BinStringOp) {
				transformer.transform((Code.BinStringOp)code,this);
			} else if(code instanceof Code.SubString) {
				transformer.transform((Code.SubString)code,this);
			} else if(code instanceof Code.NewObject) {
				transformer.transform((Code.NewObject)code,this);
			} else if(code instanceof Code.Throw) {
				transformer.transform((Code.Throw)code,this);
			} else if(code instanceof Code.TupleLoad) {
				transformer.transform((Code.TupleLoad)code,this);
			} else {			
				internalFailure("unknown: " + code.getClass().getName(),
						transformer.filename(), entry());			
			}
		} catch(InternalFailure e) {
			throw e;
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable e) {
			internalFailure(e.getMessage(), transformer.filename(), entry(), e);
		}
	}
	
	/**
	 * Dispatch exit scope events to the transformer.
	 * 
	 * @param scope
	 * @param transformer
	 */
	private void dispatchExit(Scope scope, VerificationTransformer transformer) {
		if (scope instanceof LoopScope) {
			if (scope instanceof ForScope) {
				ForScope fs = (ForScope) scope;
				transformer.exit(fs, this);
			} else if (scope instanceof LoopScope) {
				LoopScope ls = (LoopScope) scope;
				transformer.exit(ls, this);
			} else if (scope instanceof TryScope) {
				TryScope ls = (TryScope) scope;
				transformer.exit(ls, this);
			}
		}
	}

	/**
	 * Reposition the Program Counter (PC) for this branch to a given label in
	 * the block.
	 * 
	 * @param label
	 *            --- label to look for, which is assumed to occupy an index
	 *            greater than the current PC (this follows the Wyil requirement
	 *            that branches always go forward).
	 */
	private void goTo(String label) {
		pc = findLabelIndex(label);		
	}
	
	/**
	 * Find the bytecode index of a given label. If the label doesn't exist an
	 * exception is thrown.
	 * 
	 * @param label
	 * @return
	 */
	private int findLabelIndex(String label) {
		for (int i = pc; i != block.size(); ++i) {			
			Code code = block.get(i).code;
			if (code instanceof Code.Label) {
				Code.Label l = (Code.Label) code;
				if (l.label.equals(label)) {
					return i;
				}
			}
		}
		throw new IllegalArgumentException("unknown label --- " + label);
	}
	
	private Scope topScope() {
		return scopes.get(scopes.size()-1);
	}
	
	/**
	 * Split the constraints for this branch and the incoming branch into three
	 * sets: those common to both; those unique to this branch; and, those
	 * unique to the incoming branch.
	 * 
	 * @param incoming
	 * @param common
	 * @param myRemainder
	 * @param incomingRemainder
	 */
	private void splitConstraints(VerificationBranch incoming,
			ArrayList<Stmt> common, ArrayList<Expr> myRemainder,
			ArrayList<Expr> incomingRemainder) {
		ArrayList<Stmt> constraints = topScope().constraints;
		ArrayList<Stmt> incomingConstraints = incoming.topScope().constraints;
		
		int min = 0;
		
		while(min < constraints.size() && min < incomingConstraints.size()) {		
			Stmt is = constraints.get(min);
			Stmt js = incomingConstraints.get(min);
			if(is != js) {
				break;
			}
			min = min + 1;
		}
		
		for(int k=0;k<min;++k) {
			common.add(constraints.get(k));
		}
		for(int i = min;i < constraints.size();++i) {
			Stmt s = constraints.get(i);
			if(s instanceof Stmt.Assert) {
				myRemainder.add(((Stmt.Assert)s).expr);
			} else if(s instanceof Stmt.Assume) {
				myRemainder.add(((Stmt.Assume)s).expr);
			}
		}			
		for(int j = min;j < incomingConstraints.size();++j) {
			Stmt s = incomingConstraints.get(j);
			if(s instanceof Stmt.Assert) {
				incomingRemainder.add(((Stmt.Assert)s).expr);
			} else if(s instanceof Stmt.Assume) {
				incomingRemainder.add(((Stmt.Assume)s).expr);
			}
		}
	}	
}
