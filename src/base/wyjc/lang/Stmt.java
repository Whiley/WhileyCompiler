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

package wyjc.lang;

import java.util.*;

import wyil.lang.Attribute;
import wyil.util.*;
import wyjvm.lang.Bytecode;

public interface Stmt extends SyntacticElement {
	
	public static final class Assign extends SyntacticElement.Impl implements Stmt {
		public final Expr.LVal lhs;
		public final Expr rhs;

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
		public final Expr expr;		

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
		public final Expr expr;		

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
		public final Expr condition;
		public final Expr invariant;	
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

	public static final class For extends SyntacticElement.Impl implements Stmt {
		public final String variable;		
		public final Expr source;
		public final Expr invariant;
		public final ArrayList<Stmt> body;

		public For(String variable, Expr source, Expr invariant, Collection<Stmt> body, Attribute... attributes) {
			super(attributes);
			this.variable = variable;
			this.source = source;		
			this.invariant = invariant;
			this.body = new ArrayList<Stmt>(body);
		}

		public For(String variable, Expr source, Expr invariant,
				Collection<Stmt> body, Collection<Attribute> attributes) {
			super(attributes);
			this.variable = variable;
			this.source = source;	
			this.invariant = invariant;
			this.body = new ArrayList<Stmt>(body);
		}		
	}

	
	public static final class IfElse extends SyntacticElement.Impl implements Stmt {
		public final Expr condition;
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
		public final Expr condition; // needs to proved a constant
		public final ArrayList<Stmt> stmts;
		
		public Case(Expr condition, List<Stmt> statements,
				Attribute... attributes) {
			super(attributes);
			this.condition = condition;
			this.stmts = new ArrayList<Stmt>(statements);
		}
	}	
	
	public static final class Switch extends SyntacticElement.Impl implements Stmt {		
		public final Expr condition;
		public final ArrayList<Case> cases;		
		
		public Switch(Expr condition, List<Case> cases, Attribute... attributes) {
			super(attributes);
			this.condition = condition;
			this.cases = new ArrayList<Case>(cases);								
		}
		
		public Switch(Expr condition, List<Case> cases, Collection<Attribute> attributes) {			
			super(attributes);
			this.condition = condition;
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
		public final Expr expr;		

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
	
	public static final class ExternJvm extends Skip implements Stmt {
		public ArrayList<Bytecode> bytecodes;
		
		public ExternJvm(Collection<Bytecode> bytecodes, Attribute... attributes) {
			super(attributes);
			this.bytecodes = new ArrayList<Bytecode>(bytecodes);
		}
		
		public ExternJvm(Collection<Bytecode> bytecodes, Collection<Attribute> attributes) {
			super(attributes);
			this.bytecodes = new ArrayList<Bytecode>(bytecodes);
		}		
	}
}
