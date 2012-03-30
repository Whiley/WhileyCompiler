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

import wybs.lang.SyntacticElement;
import wyc.builder.Nominal;
import wyil.lang.Attribute;
import wyil.lang.Type;
import wyil.lang.Value;
import wyil.util.*;
import wyjvm.lang.Bytecode;

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
	
	public static final class Assign extends SyntacticElement.Impl implements Stmt {
		public Expr.LVal lhs;
		public Expr rhs;

		public Assign(Expr.LVal lhs, Expr rhs, Attribute... attributes) {
			super(attributes);
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public Assign(Expr.LVal lhs, Expr rhs, Collection<Attribute> attributes) {
			super(attributes);
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public String toString() {
			return lhs + " = " + rhs;
		}
	}
	
	public static final class Assert extends SyntacticElement.Impl implements Stmt {
		public Expr expr;		

		public Assert(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;			
		}

		public Assert(String msg, Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.expr = expr;
		}
		
		public String toString() {
			return "assert " + expr;
		}
	}
	
	public static final class Return extends SyntacticElement.Impl implements Stmt {
		public Expr expr;		

		public Return(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;			
		}

		public Return(Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.expr = expr;			
		}
		
		public String toString() {
			if(expr != null) {
				return "return " + expr;
			} else {
				return "return";
			}
		}
	}
	
	public static final class While extends SyntacticElement.Impl implements Stmt {
		public Expr condition;
		public Expr invariant;	
		public final ArrayList<Stmt> body;

		public While(Expr condition, Expr invariant, Collection<Stmt> body, Attribute... attributes) {
			super(attributes);
			this.condition = condition;
			this.invariant = invariant;
			this.body = new ArrayList<Stmt>(body);
		}

		public While(Expr condition, Expr invariant, Collection<Stmt> body,
				Collection<Attribute> attributes) {
			super(attributes);
			this.condition = condition;
			this.invariant = invariant;
			this.body = new ArrayList<Stmt>(body);				
		}		
	}

	public static final class DoWhile extends SyntacticElement.Impl implements Stmt {
		public Expr condition;
		public Expr invariant;	
		public final ArrayList<Stmt> body;

		public DoWhile(Expr condition, Expr invariant, Collection<Stmt> body, Attribute... attributes) {
			super(attributes);
			this.condition = condition;
			this.invariant = invariant;
			this.body = new ArrayList<Stmt>(body);
		}

		public DoWhile(Expr condition, Expr invariant, Collection<Stmt> body,
				Collection<Attribute> attributes) {
			super(attributes);
			this.condition = condition;
			this.invariant = invariant;
			this.body = new ArrayList<Stmt>(body);				
		}		
	}
	
	public static final class ForAll extends SyntacticElement.Impl
			implements Stmt {
		public final ArrayList<String> variables;
		public Expr source;
		public Expr invariant;		
		public final ArrayList<Stmt> body;
		public Nominal.EffectiveCollection srcType;

		public ForAll(Collection<String> variables, Expr source,
				Expr invariant, Collection<Stmt> body, Attribute... attributes) {
			super(attributes);
			this.variables = new ArrayList<String>(variables);
			this.source = source;
			this.invariant = invariant;
			this.body = new ArrayList<Stmt>(body);
		}

		public ForAll(Collection<String> variables, Expr source,
				Expr invariant, Collection<Stmt> body,
				Collection<Attribute> attributes) {
			super(attributes);
			this.variables = new ArrayList<String>(variables);
			this.source = source;
			this.invariant = invariant;
			this.body = new ArrayList<Stmt>(body);
		}
	}
	
	public static final class IfElse extends SyntacticElement.Impl implements Stmt {
		public Expr condition;
		public final ArrayList<Stmt> trueBranch;
		public final ArrayList<Stmt> falseBranch;
		
		public IfElse(Expr condition, List<Stmt> trueBranch,
				List<Stmt> falseBranch, Attribute... attributes) {
			super(attributes);
			this.condition = condition;
			this.trueBranch = new ArrayList<Stmt>(trueBranch);			
			this.falseBranch = new ArrayList<Stmt>(falseBranch);			
		}
		
		public IfElse(Expr condition, List<Stmt> trueBranch,
				List<Stmt> falseBranch, Collection<Attribute> attributes) {
			super(attributes);
			this.condition = condition;
			this.trueBranch = new ArrayList<Stmt>(trueBranch);
			this.falseBranch = new ArrayList<Stmt>(falseBranch);
		}
	}
	
	public static final class Case extends SyntacticElement.Impl {
		public ArrayList<Expr> expr; // needs to be proved all constants
		public ArrayList<Value> constants; // needs to be proved all constants
		public final ArrayList<Stmt> stmts;
		
		public Case(List<Expr> values, List<Stmt> statements,
				Attribute... attributes) {
			super(attributes);
			this.expr = new ArrayList<Expr>(values);
			this.stmts = new ArrayList<Stmt>(statements);
		}
	}	
	
	public static final class Catch extends SyntacticElement.Impl {
		public UnresolvedType unresolvedType; 		
		public final String variable;
		public final ArrayList<Stmt> stmts;
		public Nominal type;

		public Catch(UnresolvedType type, String variable, List<Stmt> statements,
				Attribute... attributes) {
			super(attributes);
			this.unresolvedType = type;
			this.variable = variable;
			this.stmts = new ArrayList<Stmt>(statements);
		}
	}
	
	public static final class Break extends SyntacticElement.Impl implements Stmt {
		public Break(Attribute... attributes) {
			super(attributes);
			// TODO: update to include labelled breaks
		}
	}
	
	public static final class Throw extends SyntacticElement.Impl implements Stmt {
		public Expr expr;
		public Throw(Expr expr, Attribute... attributes) {
			super(attributes);
			this.expr = expr;
		}
	}
	
	public static final class TryCatch extends SyntacticElement.Impl implements
			Stmt {
		public ArrayList<Stmt> body;
		public final ArrayList<Catch> catches;

		public TryCatch(Collection<Stmt> body, List<Catch> catches,
				Attribute... attributes) {
			super(attributes);
			this.body = new ArrayList<Stmt>(body);
			this.catches = new ArrayList<Catch>(catches);
		}

		public TryCatch(Collection<Stmt> body, List<Catch> catches,
				Collection<Attribute> attributes) {
			super(attributes);
			this.body = new ArrayList<Stmt>(body);
			this.catches = new ArrayList<Catch>(catches);
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
