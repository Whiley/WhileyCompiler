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

package wyc.lang;

import java.util.*;

import wybs.lang.Attribute;
import wybs.lang.SyntacticElement;
import wyil.lang.Constant;
import wyil.util.*;

/**
 * Provides classes for representing statements in Whiley's source language.
 * Examples include <i>assignments</i>, <i>for-loops</i>, <i>conditions</i>,
 * etc. Each class is an instance of <code>SyntacticElement</code> and, hence,
 * can be adorned with certain information (such as source location, etc).
 *
 * @author David J. Pearce
 *
 */
public interface Stmt extends SyntacticElement {

	/**
	 * Represents an assignment statement of the form <code>lhs = rhs</code>.
	 * Here, the <code>rhs</code> is any expression, whilst the <code>lhs</code>
	 * must be an <code>LVal</code> --- that is, an expression permitted on the
	 * left-side of an assignment. The following illustrates different possible
	 * assignment statements:
	 *
	 * <pre>
	 * x = y       // variable assignment
	 * x.f = y     // field assignment
	 * x[i] = y    // list assignment
	 * x[i].f = y  // compound assignment
	 * </pre>
	 *
	 * The last assignment here illustrates that the left-hand side of an
	 * assignment can be arbitrarily complex, involving nested assignments into
	 * lists and records.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Assign extends SyntacticElement.Impl implements
			Stmt {
		public final List<Expr.LVal> lvals;
		public final List<Expr> rvals;

		/**
		 * Create an assignment from a given sequence of lvals and expressions on the right-hand side.
		 *
		 * @param lvals
		 *            Sequence of one or more lval expressions representing the
		 *            left-hand side
		 * @param rvals
		 *            Sequence of one or more expressions representing the
		 *            right-hand side
		 * @param attributes
		 */
		public Assign(List<Expr.LVal> lvals, List<Expr> rvals, Attribute... attributes) {
			super(attributes);
			this.lvals = new ArrayList<Expr.LVal>(lvals);
			this.rvals = new ArrayList<Expr>(rvals);
		}

		/**
		 * Create an assignment from a given sequence of lvals and expressions on the right-hand side.
		 *
		 * @param lvals
		 *            Sequence of one or more lval expressions representing the
		 *            left-hand side
		 * @param rvals
		 *            Sequence of one or more expressions representing the
		 *            right-hand side
		 * @param attributes
		 */
		public Assign(List<Expr.LVal> lvals, List<Expr> rvals, Collection<Attribute> attributes) {
			super(attributes);
			this.lvals = new ArrayList<Expr.LVal>(lvals);
			this.rvals = new ArrayList<Expr>(rvals);
		}
	}

	/**
	 * Represents a assert statement of the form <code>assert e</code>, where
	 * <code>e</code> is a boolean expression. The following illustrates:
	 *
	 * <pre>
	 * function abs(int x) -> int:
	 *     if x < 0:
	 *         x = -x
	 *     assert x >= 0
	 *     return x
	 * </pre>
	 *
	 * Assertions are either statically checked by the verifier, or turned into
	 * runtime checks.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Assert extends SyntacticElement.Impl implements Stmt {
		public Expr expr;

		/**
		 * Create a given assert statement.
		 *
		 * @param expr
		 *            the asserted condition, which may not be <code>null</code>.
		 * @param attributes
		 */
		public Assert(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;
		}

		/**
		 * Create a given assert statement.
		 *
		 * @param expr
		 *            the asserted condition, which may not be <code>null</code>.
		 * @param attributes
		 */
		public Assert(String msg, Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.expr = expr;
		}
	}

	/**
	 * Represents an assume statement of the form <code>assume e</code>, where
	 * <code>e</code> is a boolean expression. The following illustrates:
	 *
	 * <pre>
	 * function abs(int x) -> int:
	 *     if x < 0:
	 *         x = -x
	 *     assume x >= 0
	 *     return x
	 * </pre>
	 *
	 * Assumptions are assumed by the verifier and, since this may be unsound,
	 * always turned into runtime checks.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Assume extends SyntacticElement.Impl implements
			Stmt {
		public Expr expr;

		/**
		 * Create a given assume statement.
		 *
		 * @param expr
		 *            the assumed condition, which may not be <code>null</code>.
		 * @param attributes
		 */
		public Assume(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;
		}

		/**
		 * Create a given assume statement.
		 *
		 * @param expr
		 *            the assumed condition, which may not be <code>null</code>.
		 * @param attributes
		 */
		public Assume(String msg, Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.expr = expr;
		}
	}

	/**
	 * Represents a return statement, which has the form:
	 *
	 * <pre>
	 * ReturnStmt ::= "return" [Expression] NewLine
	 * </pre>
	 *
	 * The optional expression is referred to as the <i>return value</i>. Note
	 * that, the returned expression (if there is one) must begin on the same
	 * line as the return statement itself.
	 *
	 * The following illustrates:
	 *
	 * <pre>
	 * function f(int x) -> int:
	 * 	  return x + 1
	 * </pre>
	 *
	 * Here, we see a simple <code>return</code> statement which returns an
	 * <code>int</code> value.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Return extends SyntacticElement.Impl implements Stmt {
		public ArrayList<Expr> returns;

		/**
		 * Create a given return statement with an optional return value.
		 *
		 * @param expr
		 *            value being returned (may be null)
		 * @param attributes
		 */
		public Return(List<Expr> returns, Attribute... attributes) {
			super(attributes);
			this.returns = new ArrayList<Expr>(returns);
		}

		/**
		 * Create a given return statement with an optional return value.
		 *
		 * @param expr
		 *            the return value, which may be <code>null</code>.
		 * @param attributes
		 */
		public Return(List<Expr> returns, Collection<Attribute> attributes) {
			super(attributes);
			this.returns = new ArrayList<Expr>(returns);
		}

		public String toString() {
			String r = "return";
			for(int i=0;i!=returns.size();++i) {
				if(i != 0) {
					r = r + ",";
				}
				r = r + " " + returns.get(i);
			}
			return r;
		}
	}

	/**
	 * Represents a named block, which has the form:
	 *
	 * <pre>
	 * NamedBlcok ::= LifetimeIdentifier ':' NewLine Block
	 * </pre>
	 *
	 * As an example:
	 *
	 * <pre>
	 * function sum():
	 *   &this:int x = new:this x
	 *   myblock:
	 *     &myblock:int y = new:myblock y
	 * </pre>
	 */
	public static final class NamedBlock extends SyntacticElement.Impl implements Stmt {
		public final String name;
		public final ArrayList<Stmt> body;

		/**
		 * Construct a named block from a given name and body of statements.
		 *
		 * @param name
		 *            name of this named block.
		 * @param body
		 *            non-null collection which contains zero or more
		 *            statements.
		 * @param attributes
		 */
		public NamedBlock(String name, Collection<Stmt> body, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.body = new ArrayList<Stmt>(body);
		}

		/**
		 * Construct a named block from a given name and body of statements.
		 *
		 * @param name
		 *            name of this named block.
		 * @param body
		 *            non-null collection which contains zero or more
		 *            statements.
		 * @param attributes
		 */
		public NamedBlock(String name, Collection<Stmt> body, Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.body = new ArrayList<Stmt>(body);
		}
	}

	/**
	 * Represents a while statement, which has the form:
	 *
	 * <pre>
	 * WhileStmt ::= "while" Expression (where Expression)* ':' NewLine Block
	 * </pre>
	 *
	 * As an example:
	 *
	 * <pre>
	 * function sum([int] xs) -> int:
	 *   int r = 0
	 *   int i = 0
	 *   while i < |xs| where i >= 0:
	 *     r = r + xs[i]
	 *     i = i + 1
	 *   return r
	 * </pre>
	 *
	 * The optional <code>where</code> clause(s) are commonly referred to as the
	 * "loop invariant". When multiple clauses are given, these are combined
	 * using a conjunction. The combined invariant defines a condition which
	 * must be true on every iteration of the loop.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class While extends SyntacticElement.Impl implements Stmt {
		public Expr condition;
		public List<Expr> invariants;
		public final ArrayList<Stmt> body;

		/**
		 * Construct a While statement from a given condition and body of
		 * statements.
		 *
		 * @param condition
		 *            non-null expression.
		 * @param invariant
		 *            The loop invariant expression, which may be null (if no
		 *            invariant is given)
		 * @param body
		 *            non-null collection which contains zero or more
		 *            statements.
		 * @param attributes
		 */
		public While(Expr condition, List<Expr> invariants, Collection<Stmt> body, Attribute... attributes) {
			super(attributes);
			this.condition = condition;
			this.invariants = invariants;
			this.body = new ArrayList<Stmt>(body);
		}

		/**
		 * Construct a While statement from a given condition and body of
		 * statements.
		 *
		 * @param condition
		 *            non-null expression.
		 * @param invariant
		 *            The loop invariant expression, which may be null (if no
		 *            invariant is given)
		 * @param body
		 *            non-null collection which contains zero or more
		 *            statements.
		 * @param attributes
		 */
		public While(Expr condition, List<Expr> invariants, Collection<Stmt> body,
				Collection<Attribute> attributes) {
			super(attributes);
			this.condition = condition;
			this.invariants = invariants;
			this.body = new ArrayList<Stmt>(body);
		}
	}

	/**
	 * Represents a do-while statement whose body is made up from a block of
	 * statements separated by indentation. As an example:
	 *
	 * <pre>
	 * function sum([int] xs) -> int
	 * requires |xs| > 0:
	 *   int r = 0
	 *   int i = 0
	 *   do:
	 *     r = r + xs[i]
	 *     i = i + 1
	 *   while i < |xs| where i >= 0
	 *   return r
	 * </pre>
	 *
	 * Here, the <code>where</code> is optional, and commonly referred to as the
	 * <i>loop invariant</i>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class DoWhile extends SyntacticElement.Impl implements Stmt {
		public Expr condition;
		public final ArrayList<Expr> invariants;
		public final ArrayList<Stmt> body;

		/**
		 * Construct a Do-While statement from a given condition and body of
		 * statements.
		 *
		 * @param condition
		 *            non-null expression.
		 * @param invariant
		 *            The loop invariant expression, which may be null (if no
		 *            invariant is given)
		 * @param body
		 *            non-null collection which contains zero or more
		 *            statements.
		 * @param attributes
		 */
		public DoWhile(Expr condition, List<Expr> invariants, Collection<Stmt> body, Attribute... attributes) {
			super(attributes);
			this.condition = condition;
			this.invariants = new ArrayList<Expr>(invariants);
			this.body = new ArrayList<Stmt>(body);
		}

		/**
		 * Construct a Do-While statement from a given condition and body of
		 * statements.
		 *
		 * @param condition
		 *            non-null expression.
		 * @param invariant
		 *            The loop invariant expression, which may be null (if no
		 *            invariant is given)
		 * @param body
		 *            non-null collection which contains zero or more
		 *            statements.
		 * @param attributes
		 */
		public DoWhile(Expr condition, List<Expr> invariants,
				Collection<Stmt> body, Collection<Attribute> attributes) {
			super(attributes);
			this.condition = condition;
			this.invariants = new ArrayList<Expr>(invariants);
			this.body = new ArrayList<Stmt>(body);
		}
	}

	/**
	 * Represents a fail statement.
	 */
	public static final class Fail extends SyntacticElement.Impl implements Stmt {
		public Fail(Attribute... attributes) {
			super(attributes);
		}
	}

	/**
	 * Represents a classical if-else statement, which is has the form:
	 *
	 * <pre>
	 * "if" Expression ':' NewLine Block ["else" ':' NewLine Block]
	 * </pre>
	 *
	 * The first expression is referred to as the <i>condition</i>, while the
	 * first block is referred to as the <i>true branch</i>. The optional second
	 * block is referred to as the <i>false branch</i>. The following
	 * illustrates:
	 *
	 * <pre>
	 * function max(int x, int y) -> int:
	 *   if(x > y):
	 *     return x
	 *   else if(x == y):
	 *   	return 0
	 *   else:
	 *     return y
	 * </pre>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class IfElse extends SyntacticElement.Impl implements Stmt {
		public Expr condition;
		public final ArrayList<Stmt> trueBranch;
		public final ArrayList<Stmt> falseBranch;

		/**
		 * Construct an if-else statement from a condition, true branch and
		 * optional false branch.
		 *
		 * @param condition
		 *            May not be null.
		 * @param trueBranch
		 *            A list of zero or more statements to be executed when the
		 *            condition holds; may not be null.
		 * @param falseBranch
		 *            A list of zero of more statements to be executed when the
		 *            condition does not hold; may not be null.
		 * @param attributes
		 */
		public IfElse(Expr condition, List<Stmt> trueBranch,
				List<Stmt> falseBranch, Attribute... attributes) {
			super(attributes);
			this.condition = condition;
			this.trueBranch = new ArrayList<Stmt>(trueBranch);
			this.falseBranch = new ArrayList<Stmt>(falseBranch);
		}

		/**
		 * Construct an if-else statement from a condition, true branch and
		 * optional false branch.
		 *
		 * @param condition
		 *            May not be null.
		 * @param trueBranch
		 *            A list of zero or more statements to be executed when the
		 *            condition holds; may not be null.
		 * @param falseBranch
		 *            A list of zero of more statements to be executed when the
		 *            condition does not hold; may not be null.
		 * @param attributes
		 */
		public IfElse(Expr condition, List<Stmt> trueBranch,
				List<Stmt> falseBranch, Collection<Attribute> attributes) {
			super(attributes);
			this.condition = condition;
			this.trueBranch = new ArrayList<Stmt>(trueBranch);
			this.falseBranch = new ArrayList<Stmt>(falseBranch);
		}
	}

	/**
	 * Represents a variable declaration which has the form:
	 *
	 * <pre>
	 * Type Identifier ['=' Expression] NewLine
	 * </pre>
	 *
	 * The optional <code>Expression</code> assignment is referred to as an
	 * <i>initialiser</i>. If an initialiser is given, then this will be
	 * evaluated and assigned to the variable when the declaration is executed.
	 * Some example declarations:
	 *
	 * <pre>
	 * int x
	 * int y = 1
	 * int z = x + y
	 * </pre>
	 *
	 * Observe that, unlike C and Java, declarations that declare multiple
	 * variables (separated by commas) are not permitted.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class VariableDeclaration extends SyntacticElement.Impl implements
			Stmt {
		public final WhileyFile.Parameter parameter;
		public Nominal type;
		public Expr expr;

		/**
		 * Construct a variable declaration from a given type, variable name and
		 * optional initialiser expression.
		 *
		 * @param pattern
		 *            Type pattern declaring one or more types.
		 * @param expr
		 *            Optional initialiser expression, which may be null.
		 * @param attributes
		 */
		public VariableDeclaration(WhileyFile.Parameter parameter, Expr expr,
				Attribute... attributes) {
			super(attributes);
			this.parameter = parameter;
			this.expr = expr;
		}

		/**
		 * Construct a variable declaration from a given type, variable name and
		 * optional initialiser expression.
		 *
		 * @param pattern
		 *            Type pattern declaring one or more types.
		 * @param expr
		 *            Optional initialiser expression, which may be null.
		 * @param attributes
		 */
		public VariableDeclaration(WhileyFile.Parameter parameter, Expr expr,
				Collection<Attribute> attributes) {
			super(attributes);
			this.parameter = parameter;
			this.expr = expr;
		}
	}

	public static final class Case extends SyntacticElement.Impl {
		public ArrayList<Expr> expr; // needs to be proved all constants
		public ArrayList<Constant> constants; // needs to be proved all constants
		public final ArrayList<Stmt> stmts;

		public Case(List<Expr> values, List<Stmt> statements,
				Attribute... attributes) {
			super(attributes);
			this.expr = new ArrayList<Expr>(values);
			this.stmts = new ArrayList<Stmt>(statements);
		}
	}

	public static final class Break extends SyntacticElement.Impl implements Stmt {
		public Break(Attribute... attributes) {
			super(attributes);
			// TODO: update to include labelled breaks
		}
	}

	public static final class Continue extends SyntacticElement.Impl implements Stmt {
		public Continue(Attribute... attributes) {
			super(attributes);
			// TODO: update to include labelled breaks
		}
	}

	public static final class Switch extends SyntacticElement.Impl implements Stmt {
		public Expr expr;
		public final ArrayList<Case> cases;

		public Switch(Expr condition, List<Case> cases, Attribute... attributes) {
			super(attributes);
			this.expr = condition;
			this.cases = new ArrayList<Case>(cases);
		}

		public Switch(Expr condition, List<Case> cases, Collection<Attribute> attributes) {
			super(attributes);
			this.expr = condition;
			this.cases = new ArrayList<Case>(cases);
		}
	}

	public static class Skip extends SyntacticElement.Impl implements Stmt {
		public Skip(Attribute... attributes) {
			super(attributes);
		}

		public Skip(Collection<Attribute> attributes) {
			super(attributes);
		}
	}

	public static final class Debug extends Skip {
		public Expr expr;

		public Debug(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;
		}

		public Debug(Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.expr = expr;
		}

		public String toString() {
			return "debug " + expr;
		}
	}
}
