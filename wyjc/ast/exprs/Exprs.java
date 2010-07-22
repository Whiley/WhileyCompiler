package wyjc.ast.exprs;

import java.util.*;
import static wyjc.util.SyntaxError.syntaxError;
import wyjc.ast.attrs.SourceAttr;
import wyjc.ast.exprs.integer.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.exprs.process.*;
import wyjc.ast.exprs.real.*;
import wyjc.ast.exprs.set.*;
import wyjc.ast.exprs.tuple.*;

public class Exprs {

	/**
	 * Simplify a condition as much as possible. Currently, this only applies
	 * not elimination.
	 * 
	 * @param c
	 * @return
	 */
	public static Condition simplify(Condition c) {				
		c = notElimination(c); // put into NNF		
		// c = unitPropagation(c); // apply equalities such as x == 1 immediately.
		// c = factorise(c); // attempt to find common factors of subexpressions. 
		return c;
	}
		
	
	/**
	 * Not elimination attempts to simplify a condition by pushing "nots"
	 * through conjuncts and disjuncts, cancelling them where possible and
	 * inverting literals where necessary.  For example:
	 * <pre>
	 * !!x ===> x
	 * !(x < y) ===> x >= y
	 * !(x < y || x == z) ===> x >= y && x != z
	 * </pre>
	 * 
	 * @param c
	 * @return
	 */
	public static Condition notElimination(Condition c) {
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
		} else if(c instanceof TypeEquals) {
			return notElimination((TypeEquals)c);
		} else {			
			syntaxError("unknown condition encountered (" + c.getClass().getName() + ")",c);
			return null;
		}
	}
	
	protected static Condition notElimination(And ac) {
		return new And(notElimination(ac.lhs()), notElimination(ac.rhs()), ac
				.attributes());
	}
	
	protected static Condition notElimination(TypeGate tg) {		
		return new TypeGate(tg.lhsTest(), tg.variable(), tg.lhs(),
				notElimination(tg.rhs()), tg.attributes());		
	}
	
	protected static Condition notElimination(TypeEquals tg) {
		return new TypeEquals(tg.lhsTest(), tg.variable(), tg.lhs(),
				notElimination(tg.rhs()), tg.attributes());
	}
	
	protected static Condition notElimination(Or ac) {
		return new Or(notElimination(ac.lhs()), notElimination(ac.rhs()), ac
				.attributes());
	}
	
	protected static Condition notElimination(Not ac) {		
		Condition c = ac.mhs();
		if (c instanceof Subset || c instanceof SubsetEq
				|| c instanceof ListElementOf || c instanceof SetElementOf) {
			return ac;
		} else {
			return invert(ac.mhs());
		}
	}

	/**
	 * Invert a condition, such that the result is true iff the original
	 * condition was false and vice-versa.
	 * 
	 * @param c
	 * @return
	 */
	public static Condition invert(Condition c) {
		if (c instanceof Subset || c instanceof SubsetEq
				|| c instanceof SetElementOf || c instanceof ListElementOf) {
			return new Not(c, c.attribute(SourceAttr.class));
		} else if(c instanceof BoolVal) {
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
		} else if(c instanceof TypeGate) {
			return invert((TypeGate)c);
		} else if(c instanceof TypeEquals) {
			return invert((TypeEquals)c);
		} else {
			syntaxError("unknown condition encountered: " + c.getClass().getName(),c);
			return null;
		}
	}
	
	protected static Condition invert(And c) {
		return new Or(invert(c.lhs()),invert(c.rhs()),c.attributes());
	}
	
	protected static Condition invert(Or c) {
		return new And(invert(c.lhs()),invert(c.rhs()),c.attributes());
	}
	
	protected static Condition invert(Not c) {
		return invert(c.mhs());
	}	
	
	protected static Condition invert(None c) {
		return new Some(c.mhs(),c.attributes());
	}
	
	protected static Condition invert(Some c) {
		return new None(c.mhs(),c.attributes());
	}
	
	protected static Condition invert(BoolEquals c) {
		return new BoolNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(BoolNotEquals c) {
		return new BoolEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(IntEquals c) {
		return new IntNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(IntNotEquals c) {
		return new IntEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(IntLessThan c) {
		return new IntGreaterThanEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(IntLessThanEquals c) {
		return new IntGreaterThan(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(IntGreaterThan c) {
		return new IntLessThanEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(IntGreaterThanEquals c) {
		return new IntLessThan(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(RealEquals c) {
		return new RealNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(RealNotEquals c) {
		return new RealEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(RealLessThan c) {
		return new RealGreaterThanEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(RealLessThanEquals c) {
		return new RealGreaterThan(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(RealGreaterThan c) {
		return new RealLessThanEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(RealGreaterThanEquals c) {
		return new RealLessThan(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(TupleEquals c) {
		return new TupleNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(TupleNotEquals c) {
		return new TupleEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(ListEquals c) {
		return new ListNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(ListNotEquals c) {
		return new ListEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(SetEquals c) {
		return new SetNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(SetNotEquals c) {
		return new SetEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(ProcessEquals c) {
		return new ProcessNotEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(ProcessNotEquals c) {
		return new ProcessEquals(c.lhs(),c.rhs(),c.attributes());
	}
	
	protected static Condition invert(TypeGate c) {
		return new TypeEquals(c.lhsTest(), c.variable(), c.lhs(), invert(c
				.rhs()), c.attribute(SourceAttr.class));
	}
	
	protected static Condition invert(TypeEquals c) {		
		return new TypeGate(c.lhsTest(), c.variable(), c.lhs(), invert(c.rhs()), c
				.attribute(SourceAttr.class));
	}

	/**
	 * The purpose of this method is to split out the precondition from a
	 * function condition, where $ represents the return value.  
	 * 
	 * @param condition
	 * @return
	 */
	public static Condition splitPreCondition(Condition condition) {
		if(condition instanceof Or) {
			Or or = (Or) condition;
			Condition lhs = splitPreCondition(or.lhs());
			Condition rhs = splitPreCondition(or.rhs());
			return new Or(lhs,rhs,or.attribute(SourceAttr.class));
		} else if(condition instanceof And) {
			And and = (And) condition;
			Condition lhs = splitPreCondition(and.lhs());
			Condition rhs = splitPreCondition(and.rhs());
			return new And(lhs,rhs,and.attribute(SourceAttr.class));
		} else {
			Set<Variable> uses = condition.uses();
			for(Variable v : uses) {
				if(v.name().equals("$")) {
					// Ok, this literal uses $, so it's strictly part of the
					// pre-condition.
					return new BoolVal(true);
				}
			}
			return condition;
		}
	}
	
	/**
	 * The purpose of this method is to split out the postcondition from a
	 * function condition, where $ represents the return value.  
	 * 
	 * @param condition
	 * @return
	 */
	public static Condition splitPostCondition(Condition condition) {
		// FIXME: could do much better here by identifying the connected
		// component which contains "$" and extracting just that.
		return condition;
	}
}
