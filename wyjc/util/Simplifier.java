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

package wyjc.util;

import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.exprs.process.*;
import wyjc.ast.exprs.real.*;
import wyjc.ast.exprs.set.*;
import wyjc.ast.exprs.tuple.*;
import static wyjc.util.SyntaxError.*;

public class Simplifier {
	public Condition simplify(Condition c) {				
		c = notElimination(c); // put into NNF		
		// c = unitPropagation(c); // apply equalities such as x == 1 immediately.
		// c = factorise(c); // attempt to find common factors of subexpressions. 
		return c;
	}
		
	// ==========================================================================
	// Not Elimination
	// ==========================================================================
	
	public Condition notElimination(Condition c) {
		if(c instanceof BoolVal									
				|| c instanceof Some
				|| c instanceof None
				|| c instanceof BoolEquals
				|| c instanceof BoolNotEquals
				|| c instanceof IntEquals
				|| c instanceof IntNotEquals
				|| c instanceof IntLessThan
				|| c instanceof IntLessThanEquals
				|| c instanceof IntGreaterThan
				|| c instanceof IntGreaterThanEquals
				|| c instanceof RealEquals
				|| c instanceof RealNotEquals
				|| c instanceof RealLessThan
				|| c instanceof RealLessThanEquals
				|| c instanceof RealGreaterThan
				|| c instanceof RealGreaterThanEquals
				|| c instanceof TupleEquals
				|| c instanceof TupleNotEquals
				|| c instanceof ListEquals
				|| c instanceof ListNotEquals
				|| c instanceof ListElementOf
				|| c instanceof Subset
				|| c instanceof SubsetEq
				|| c instanceof SetEquals
				|| c instanceof SetNotEquals
				|| c instanceof SetElementOf
				|| c instanceof ProcessEquals
				|| c instanceof ProcessNotEquals) {
			return c;
		} else if(c instanceof BoolVal) {
			return c;
		} else if(c instanceof And) {
			return notElimination((And)c);
		} else if(c instanceof Or) {
			return notElimination((Or)c);
		} else if(c instanceof Not) {
			return notElimination((Not)c);
		} else if(c instanceof TypeGate) {
			return notElimination((TypeGate)c);
		} else {			
			syntaxError("unknown condition encountered (" + c.getClass().getName() + ")",c);
			return null;
		}
	}
	
	protected Condition notElimination(And ac) {
		return new And(notElimination(ac.lhs()), notElimination(ac.rhs()), ac
				.attributes());
	}
	
	protected Condition notElimination(TypeGate tg) {
		return new TypeGate(tg.lhsTest(), tg.lhs(), notElimination(tg.rhs()), tg
				.attributes());
	}
	
	protected Condition notElimination(Or ac) {
		return new Or(notElimination(ac.lhs()), notElimination(ac.rhs()), ac
				.attributes());
	}
	
	protected Condition notElimination(Not ac) {		
		Condition c = ac.mhs();
		if (c instanceof Subset || c instanceof SubsetEq
				|| c instanceof ListElementOf || c instanceof SetElementOf) {
			return ac;
		} else {
			return invert(ac.mhs());
		}
	}
	
	public Condition invert(Condition c) {
		if(c instanceof BoolVal) {
			return new BoolVal(!((BoolVal)c).value());
		} else if(c instanceof And) {
			return invert((And)c);
		} else if(c instanceof Or) {
			return invert((Or)c);
		} else if(c instanceof Not) {
			return invert((Not)c);
		} else if(c instanceof None) {
			return invert((None)c);
		} else if(c instanceof Some) {
			return invert((Some)c);
		} else if(c instanceof BoolEquals) {
			return invert((BoolEquals)c);
		} else if(c instanceof BoolNotEquals) {
			return invert((BoolNotEquals)c);
		} else if(c instanceof IntEquals) {
			return invert((IntEquals)c);
		} else if(c instanceof IntNotEquals) {
			return invert((IntNotEquals)c);
		} else if(c instanceof IntLessThan) {
			return invert((IntLessThan)c);
		} else if(c instanceof IntLessThanEquals) {
			return invert((IntLessThanEquals)c);
		} else if(c instanceof IntGreaterThan) {
			return invert((IntGreaterThan)c);
		} else if(c instanceof IntGreaterThanEquals) {
			return invert((IntGreaterThanEquals)c);
		} else if(c instanceof RealEquals) {
			return invert((RealEquals)c);
		} else if(c instanceof RealNotEquals) {
			return invert((RealNotEquals)c);
		} else if(c instanceof RealLessThan) {
			return invert((RealLessThan)c);
		} else if(c instanceof RealLessThanEquals) {
			return invert((RealLessThanEquals)c);
		} else if(c instanceof RealGreaterThan) {
			return invert((RealGreaterThan)c);
		} else if(c instanceof RealGreaterThanEquals) {
			return invert((RealGreaterThanEquals)c);
		} else if(c instanceof TupleEquals) {
			return invert((TupleEquals)c);
		} else if(c instanceof TupleNotEquals) {
			return invert((TupleNotEquals)c);
		} else if(c instanceof ListEquals) {
			return invert((ListEquals)c);
		} else if(c instanceof ListNotEquals) {
			return invert((ListNotEquals)c);
		} else if(c instanceof SetEquals) {
			return invert((SetEquals)c);
		} else if(c instanceof SetNotEquals) {
			return invert((SetNotEquals)c);
		} else if(c instanceof ProcessEquals) {
			return invert((ProcessEquals)c);
		} else if(c instanceof ProcessNotEquals) {
			return invert((ProcessNotEquals)c);
		} else {
			syntaxError("unknown condition encountered",c);
			return null;
		}
	}
	
	protected Condition invert(And c) {
		return new Or(invert(c.lhs()),invert(c.rhs()),c.attributes());
	}
	
	protected Condition invert(Or c) {
		return new And(invert(c.lhs()),invert(c.rhs()),c.attributes());
	}
	
	protected Condition invert(Not c) {
		return invert(c.mhs());
	}	
	
	protected Condition invert(None c) {
		return new Some(c.mhs(),c.attributes());
	}
	
	protected Condition invert(Some c) {
		return new None(c.mhs(),c.attributes());
	}
	
	protected Condition invert(BoolEquals c) {
		return new BoolNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(BoolNotEquals c) {
		return new BoolEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(IntEquals c) {
		return new IntNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(IntNotEquals c) {
		return new IntEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(IntLessThan c) {
		return new IntGreaterThanEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(IntLessThanEquals c) {
		return new IntGreaterThan(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(IntGreaterThan c) {
		return new IntLessThanEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(IntGreaterThanEquals c) {
		return new IntLessThan(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(RealEquals c) {
		return new RealNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(RealNotEquals c) {
		return new RealEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(RealLessThan c) {
		return new RealGreaterThanEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(RealLessThanEquals c) {
		return new RealGreaterThan(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(RealGreaterThan c) {
		return new RealLessThanEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(RealGreaterThanEquals c) {
		return new RealLessThan(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(TupleEquals c) {
		return new TupleNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(TupleNotEquals c) {
		return new TupleEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(ListEquals c) {
		return new ListNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(ListNotEquals c) {
		return new ListEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(SetEquals c) {
		return new SetNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(SetNotEquals c) {
		return new SetEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(ProcessEquals c) {
		return new ProcessNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected Condition invert(ProcessNotEquals c) {
		return new ProcessEquals(c.lhs(),c.rhs(),c.attributes());
	}
}
