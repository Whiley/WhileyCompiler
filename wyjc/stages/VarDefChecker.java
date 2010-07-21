// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjc.stages;

import java.util.*;

import wyjc.ast.ResolvedWhileyFile;
import wyjc.ast.ResolvedWhileyFile.*;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.IntNegate;
import wyjc.ast.exprs.integer.IntVal;
import wyjc.ast.exprs.list.ListAccess;
import wyjc.ast.exprs.list.ListVal;
import wyjc.ast.exprs.logic.Not;
import wyjc.ast.exprs.real.RealNegate;
import wyjc.ast.exprs.real.RealVal;
import wyjc.ast.exprs.set.SetComprehension;
import wyjc.ast.exprs.set.SetVal;
import wyjc.ast.exprs.tuple.TupleAccess;
import wyjc.ast.exprs.tuple.TupleVal;
import wyjc.ast.stmts.*;
import wyjc.ast.types.*;
import wyjc.util.*;

/**
 * <p>
 * The purpose of this class is to check that all variables are defined before
 * being used. For example:
 * </p>
 * 
 * <pre>
 * int f() {
 * 	int z;
 * 	return z + 1;
 * }
 * </pre>
 * 
 * <p>
 * In the above example, variable z is used in the return statement before it
 * has been defined any value. This is considered a syntax error in whiley.
 * </p>
 * @author djp
 * 
 */
public class VarDefChecker {
	public void verify(ResolvedWhileyFile wf) {		
		for(Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {
				verify((FunDecl)d);
			}
		}
	}
	
	public void verify(FunDecl f) {
		HashSet<String> definitions = new HashSet<String>();
		
		for(FunDecl.Parameter p : f.parameters()) {
			definitions.add(p.name());
		}
				
		if(f.constraint() != null) {
			if(!(f.returnType().type() == Types.T_VOID)) {
				definitions.add("$");
				check(f.constraint(),definitions);
				definitions.remove("$");
			} else {
				check(f.constraint(),definitions);
			}
		}
		
		
		for(Stmt s : f.statements()) {
			check(s,definitions);
		}
	}
	
	/**
     * This method checks the given statement to see whether there are any
     * variables which are used that have not been defined. The definitions set
     * contains the set of variables which have been defined prior to this
     * statement.
     * 
     * @param statement
     * @param definitions
     * @param function
     */
	protected void check(Stmt statement, HashSet<String> definitions) {
		if (statement instanceof Skip || statement instanceof Read) {
			// nothing to do here.
		} else if (statement instanceof Print) {
			check((Print) statement, definitions);
		} else if (statement instanceof Assign) {
			check((Assign) statement, definitions);
		} else if (statement instanceof IfElse) {
			check((IfElse) statement, definitions);
		} else if (statement instanceof While) {
			check((While) statement, definitions);
		} else if (statement instanceof Return) {
			check((Return) statement, definitions);
		} else if (statement instanceof Assertion) {
			check((Assertion) statement, definitions);
		} else if (statement instanceof Invoke) {
			check((Invoke) statement, definitions);
		} else if (statement instanceof VarDecl) {
			check((VarDecl) statement, definitions);
		} else {
			syntaxError("Unknown statement encountered: " + statement,
					statement);
		}
	}
	
	protected void check(VarDecl s, HashSet<String> definitions) {		
		// actually don't do anything here, as currently variable definitions
        // cannot assign variables as well.
	}
	
	protected void check(Print s, HashSet<String> definitions) {
		check(s.expr(),definitions);				
	}

	protected void check(Assign s, HashSet<String> definitions) {
		check(s.rhs(),definitions);
		if(s.lhs() instanceof Variable) {
			Variable v = (Variable) s.lhs();
			definitions.add(v.name());
		}
		check(s.lhs(),definitions);						
	}

	protected void check(IfElse statement, HashSet<String> definitions) {
		check(statement.condition(),definitions);
		HashSet<String> trueDefinitions = (HashSet<String>) definitions.clone();
		for(Stmt st : statement.trueBranch()) {
			check(st,trueDefinitions);	
		}
		
		if(statement.falseBranch() != null) {			
			HashSet<String> falseDefinitions = (HashSet<String>) definitions.clone();
			
			for(Stmt st : statement.falseBranch()) {
				check(st,falseDefinitions);	
			}
			// At this point, we need to compute the intersection of definitions
            // coming from the true and false branches.
			for(String v : trueDefinitions) {
				if(falseDefinitions.contains(v)) {
					definitions.add(v);
				}
			}			
		} 
	}

	protected void check(While s, HashSet<String> definitions) {
		check(s.condition(),definitions);
		HashSet<String> bodyDefinitions = (HashSet<String>) definitions.clone();
		for(Stmt st : s.body()) {
			check(st,bodyDefinitions);	
		}		
	}
	
	protected void check(Return s, HashSet<String> definitions) {
		if(s.expr() != null) {
			check(s.expr(),definitions);
		}
	}
	
	protected void check(Assertion s, HashSet<String> definitions) {
		check(s.condition(),definitions);
	}
	
	protected void check(Invoke s, HashSet<String> definitions) {
		
		List<Expr> args = s.arguments();
		for(int i=0;i!=args.size();++i) {
			Expr arg = args.get(i);
			check(arg,definitions);			
		}		
	}
	
	/**
     * This method determines what variables are used in the expression e.
     * 
     * @param e
     * @return
     */
	protected void check(Expr e, HashSet<String> definitions) {				
		for(Variable v : e.uses()) {
			if(!definitions.contains(v.name())) {
				syntaxError("variable " + v.name() + " used before defined",e);
			}
		}		
	}
			
	private static void syntaxError(String msg, SyntacticElement elem) {
		int start = -1;
		int end = -1;
		String filename = null;
			
		SourceAttr attr = (SourceAttr) elem.attribute(SourceAttr.class);
		if(attr != null) {
			start=attr.start();
			end=attr.end();
			filename = attr.filename();
		}
		
		throw new SyntaxError(msg, filename, start, end);
	}
}
