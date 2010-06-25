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

import static wyjc.util.SyntaxError.*;
import wyjc.ast.ResolvedWhileyFile;
import wyjc.ast.ResolvedWhileyFile.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.process.*;
import wyjc.ast.exprs.set.*;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.stmts.*;
import wyjc.util.SyntaxError;

public class FunctionChecker {
	public void check(ResolvedWhileyFile wf) {		
		for(Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {
				check((FunDecl)d);
			}
		}
	}
	
	public void check(FunDecl fd) {
		check(fd.preCondition());
		check(fd.postCondition());
		boolean isFunction = fd.isFunction();
		
		for(Stmt s : fd.statements()) {
			check(s,isFunction);
		}
	}
	
	public void check(Condition c) {
		if(c != null) {
			List<Invoke> imatches = c.match(Invoke.class);

			for(Invoke i : imatches) {
				if(i.target() != null) {
					syntaxError("message sends not permitted in condition",i);
				}
			}
			
			List<Spawn> spawns = c.match(Spawn.class);
			
			for(Spawn s : spawns) {		
				syntaxError("process spawning not permitted in condition",s);
			}
		}
		
		// FIXME: want to check for process reads as well?
	}
	
	public void check(Stmt statement, boolean isFunction) {
		try {
			if (statement instanceof Skip) {
				// nothing to do here.
			} else if (statement instanceof Print) {
				check((Print) statement, isFunction);
			} else if (statement instanceof Assign) {
				check((Assign) statement, isFunction);
			} else if (statement instanceof IfElse) {
				check((IfElse) statement, isFunction);
			} else if (statement instanceof Return) {
				check((Return) statement, isFunction);
			} else if (statement instanceof Assertion) {
				check((Assertion) statement, isFunction);
			} else if (statement instanceof Invoke) {
				check((Invoke) statement, isFunction);
			} else if (statement instanceof VarDecl) {
				check((VarDecl) statement, isFunction);
			} else if (statement instanceof Spawn) {
				check((Spawn) statement, isFunction);
			} else {
				syntaxError("Unknown statement encountered: " + statement,
						statement);
			}
		} catch(SyntaxError e) {
			throw e;
		} catch(Exception e) {
			syntaxError("internal failure",statement,e);
		}
	}
	
	public void check(Assign stmt, boolean isFunction) {
		check(stmt.lhs(),isFunction);
		check(stmt.rhs(),isFunction);
	}
	
	public void check(VarDecl stmt, boolean isFunction) {		
		if(stmt.initialiser() != null) {
			check(stmt.initialiser(),isFunction);
		}
	}
	
	public void check(Print stmt, boolean isFunction) {
		// FIXME: this should report an error I guess
		check(stmt.expr(),isFunction);
	}
	
	public void check(Spawn stmt, boolean isFunction) {
		// FIXME: this should report an error I guess
		if(isFunction) {
			syntaxError("process spawning not permitted in function",stmt);
		} else {
			check(stmt.mhs(),isFunction);
		}
	}
	
	public void check(Assertion stmt, boolean isFunction) {
		// FIXME: this should report an error I guess
		check(stmt.condition());
	}
	
	public void check(Return stmt, boolean isFunction) {
		if(stmt.expr() != null) {
			check(stmt.expr(),isFunction);
		}
	}
	
	public void check(Invoke stmt, boolean isFunction) {
		if(stmt.target() != null && isFunction) {
			syntaxError("message sends not permitted in functions",stmt);
		} else if(stmt.target() != null) {
			check(stmt.target(),isFunction);
		}
		
		for(Expr e : stmt.arguments()) {
			check(e,isFunction);
		}		
	}
	
	public void check(IfElse stmt, boolean isFunction) {
		check(stmt.condition());
		for(Stmt s : stmt.trueBranch()) {
			check(s,isFunction);
		}
		if(stmt.falseBranch() != null) {
			for(Stmt s : stmt.falseBranch()) {
				check(s,isFunction);
			}	
		}
	}
	
	public void check(Expr expr, boolean isFunction) {
		try {
			if (expr instanceof Variable || expr instanceof Value) {
				// do nothing
			} else if (expr instanceof Condition) {
				check((Condition) expr);
			} else if (expr instanceof UnOp) {
				check((UnOp) expr, isFunction);
			} else if (expr instanceof BinOp) {
				check((BinOp) expr, isFunction);
			} else if (expr instanceof Invoke) {
				check((Invoke) expr, isFunction);
			} else if (expr instanceof RangeGenerator) {
				check((RangeGenerator) expr, isFunction);
			} else if (expr instanceof ListGenerator) {
				check((ListGenerator) expr, isFunction);
			} else if (expr instanceof ListAccess) {
				check((ListAccess) expr, isFunction);
			} else if (expr instanceof ListSublist) {
				check((ListSublist) expr, isFunction);
			} else if (expr instanceof SetGenerator) {
				check((SetGenerator) expr, isFunction);
			} else if (expr instanceof SetComprehension) {
				check((SetComprehension) expr, isFunction);
			} else if (expr instanceof TupleGenerator) {
				check((TupleGenerator) expr, isFunction);
			} else if (expr instanceof TupleAccess) {
				check((TupleAccess) expr, isFunction);
			} else {
				syntaxError("unknown expression encountered: "
						+ expr.getClass().getName(), expr);
			}
		} catch(SyntaxError e) {
			throw e;
		} catch(Exception e) {
			syntaxError("internal failure",expr,e);
		}
	}
	
	public void check(UnOp uop, boolean isFunction) {
		if(uop instanceof Spawn && isFunction) {
			syntaxError("process spawning not allowed in function",uop);
		}
		check(uop.mhs(),isFunction);
	}
	
	public void check(BinOp uop, boolean isFunction) {
		check(uop.lhs(),isFunction);
		check(uop.rhs(),isFunction);
	}
	
	public void check(RangeGenerator rg, boolean isFunction) {
		check(rg.start(),isFunction);
		check(rg.end(),isFunction);
	}
	
	public void check(ListGenerator gen, boolean isFunction) {
		for(Expr e : gen.getValues()) {
			check(e,isFunction);
		}
	}
	
	public void check(ListAccess la, boolean isFunction) {
		check(la.source(),isFunction);
		check(la.index(),isFunction);
	}
	
	public void check(ListSublist la, boolean isFunction) {
		check(la.source(),isFunction);
		check(la.start(),isFunction);
		check(la.end(),isFunction);
	}
	
	public void check(SetGenerator gen, boolean isFunction) {
		for(Expr e : gen.getValues()) {
			check(e,isFunction);
		}
	}
	
	public void check(SetComprehension gen, boolean isFunction) {
		check(gen.sign(), isFunction);
		// FIXME: more to do here!
	}
	
	public void check(TupleGenerator gen, boolean isFunction) {
		for(Expr e : gen.values().values()) {
			check(e,isFunction);
		}
	}
	
	public void check(TupleAccess la, boolean isFunction) {
		check(la.source(),isFunction);
	}
}
