package wycs.transforms;

import static wybs.lang.SyntaxError.*;
import static wycs.solver.Solver.*;

import java.io.IOException;
import java.util.*;

import wyautl.core.*;
import wyautl.io.PrettyAutomataWriter;
import wyautl.util.BigRational;
import wybs.lang.Builder;
import wybs.lang.SyntacticElement;
import wybs.lang.Transform;
import wybs.util.Pair;
import wycs.WycsBuilder;
import wycs.lang.*;
import wycs.solver.Solver;

/**
 * Responsible for converting a <code>WycsFile</code> into an automaton that can
 * then be simplified to test for satisfiability. The key challenge here is to
 * break down the rich language of expressions described by the
 * <code>WycsFile</code> format, such that they can be handled effectively by
 * the <code>Solver</code>.
 * 
 * @author David J. Pearce
 * 
 */
public class VerificationCheck implements Transform<WycsFile> {
	
	/**
	 * Determines whether this transform is enabled or not.
	 */
	private boolean enabled = getEnable();

	/**
	 * Determines whether debugging is enabled or not
	 */
	private boolean debug = getDebug();
	
	private String filename;
	
	// ======================================================================
	// Constructor(s)
	// ======================================================================

	public VerificationCheck(Builder builder) {

	}

	// ======================================================================
	// Configuration Methods
	// ======================================================================

	public static String describeEnable() {
		return "Enable/disable verification";
	}

	public static boolean getEnable() {
		return true; // default value
	}

	public void setEnable(boolean flag) {
		this.enabled = flag;
	}

	public static String describeDebug() {
		return "Enable/disable debugging information";
	}

	public static boolean getDebug() {
		return false; // default value
	}

	public void setDebug(boolean flag) {
		this.debug = flag;
	}

	// ======================================================================
	// Apply Method
	// ======================================================================
	
	/**
	 * Verify the given list of Wycs statements.
	 * 
	 * @param statements
	 * @return the set of failing assertions (if any).
	 */
	public void apply(WycsFile wf) {
		if (enabled) {
			this.filename = wf.filename();
			List<WycsFile.Declaration> statements = wf.declarations();
			for (int i = 0; i != statements.size(); ++i) {
				WycsFile.Declaration stmt = statements.get(i);

				if (stmt instanceof WycsFile.Assert) {
					checkValid((WycsFile.Assert) stmt);
				} else if (stmt instanceof WycsFile.Function) {
					// TODO: we could try to verify that the function makes
					// sense (i.e. that it's specification is satisfiable for at
					// least one input).
				} else if (stmt instanceof WycsFile.Import) {

				} else {
					internalFailure("unknown statement encountered " + stmt,
							filename, stmt);
				}
			}
		}
	}
	
	private void checkValid(WycsFile.Assert stmt) {
		Automaton automaton = new Automaton();
		Automaton original = null;
		int assertion = translate(stmt.expr,automaton);
		
		automaton.setRoot(0, Not(automaton, assertion));
		automaton.minimise();
		
		if (debug) {				
			original = new Automaton(automaton);				
		}

		infer(automaton);
	
		if(!automaton.get(automaton.getRoot(0)).equals(Solver.False)) {
			String msg = stmt.message;
			msg = msg == null ? "assertion failure" : msg;
			throw new AssertionFailure(msg,stmt,automaton,original);
		}		
	}
	
	private int translate(Expr expr, Automaton automaton) {
		if(expr instanceof Expr.Constant) {
			return translate((Expr.Constant) expr,automaton);
		} else if(expr instanceof Expr.Variable) {
			return translate((Expr.Variable) expr,automaton);
		} else if(expr instanceof Expr.Binary) {
			return translate((Expr.Binary) expr,automaton);
		} else if(expr instanceof Expr.Unary) {
			return translate((Expr.Unary) expr,automaton);
		} else if(expr instanceof Expr.Nary) {
			return translate((Expr.Nary) expr,automaton);
		} else if(expr instanceof Expr.TupleLoad) {
			return translate((Expr.TupleLoad) expr,automaton);
		} else if(expr instanceof Expr.FunCall) {
			return translate((Expr.FunCall) expr,automaton);
		} else if(expr instanceof Expr.Quantifier) {
			return translate((Expr.Quantifier) expr,automaton);
		} else {
			internalFailure("unknown: " + expr.getClass().getName(),
					filename, expr);
			return -1; // dead code
		}
	}
	
	private int translate(Expr.Constant expr, Automaton automaton) {
		return convert(expr.value,expr,automaton);
	}
	
	private int translate(Expr.Variable expr, Automaton automaton) {
		return Var(automaton,expr.name);
	}	
	
	private int translate(Expr.Binary expr, Automaton automaton) {
		int lhs = translate(expr.leftOperand,automaton);
		int rhs = translate(expr.rightOperand,automaton);
		switch(expr.op) {		
		case ADD:
			return Sum(automaton, automaton.add(new Automaton.Real(0)),
					automaton.add(new Automaton.Bag(lhs, rhs)));
		case SUB:
			return Sum(automaton, automaton.add(new Automaton.Real(0)),
					automaton.add(new Automaton.Bag(lhs, Mul(automaton,
							automaton.add(new Automaton.Real(-1)),
							automaton.add(new Automaton.Bag(rhs))))));
		case MUL:
			return Mul(automaton, automaton.add(new Automaton.Real(1)),
					automaton.add(new Automaton.Bag(lhs, rhs)));
		case DIV:
			return Div(automaton, lhs, rhs);
		case REM:
			return automaton.add(False);
		case EQ:
			return Equals(automaton, lhs, rhs);
		case NEQ:
			return Not(automaton, Equals(automaton, lhs, rhs));
		case IMPLIES:
			return Or(automaton,Not(automaton, lhs),rhs);
		case IFF:
			return Or(automaton, And(automaton, lhs, rhs),
					And(automaton, Not(automaton, lhs), Not(automaton, rhs)));
		case LT:
			return LessThan(automaton, lhs, rhs);
		case LTEQ:
			return LessThanEq(automaton, lhs, rhs);
		case GT:
			return LessThan(automaton, rhs, lhs);
		case GTEQ:
			return LessThanEq(automaton, rhs, lhs);
		case IN:
			return SubsetEq(automaton, Set(automaton, lhs), rhs);
		case SUBSET:
			return And(automaton,SubsetEq(automaton, lhs, rhs),Not(automaton,Equals(automaton,lhs,rhs)));
		case SUBSETEQ:
			return SubsetEq(automaton, lhs, rhs);							
		}
		internalFailure("unknown binary expression encountered (" + expr + ")",
				filename, expr);
		return -1;
	}
	
	private int translate(Expr.Unary expr, Automaton automaton) {
		int e = translate(expr.operand,automaton);
		switch(expr.op) {
		case NOT:
			return Not(automaton, e);
		case NEG:
			return Mul(automaton, automaton.add(new Automaton.Real(-1)),
					automaton.add(new Automaton.Bag(e)));
		case LENGTHOF:
			return LengthOf(automaton, e);
		}
		internalFailure("unknown unary expression encountered (" + expr + ")",
				filename, expr);
		return -1;
	}
	
	private int translate(Expr.Nary expr, Automaton automaton) {
		Expr[] operands = expr.operands;
		int[] es = new int[operands.length];
		for(int i=0;i!=es.length;++i) {
			es[i] = translate(operands[i],automaton); 
		}		
		switch(expr.op) {
		case AND:
			return And(automaton,es);
		case OR:
			return Or(automaton,es);		
		case SET:
			return Set(automaton,es);
		case TUPLE:
			return Tuple(automaton,es);
		}
		internalFailure("unknown nary expression encountered (" + expr + ")",
				filename, expr);
		return -1;
	}
	
	private int translate(Expr.TupleLoad expr, Automaton automaton) {
		int e = translate(expr.operand,automaton);
		int i = automaton.add(new Automaton.Int(expr.index));
		return TupleLoad(automaton,e,i);
	}
	
	private int translate(Expr.FunCall expr, Automaton automaton) {
		// uninterpreted function call
		int argument = translate(expr.operand,automaton);						
		int[] es = new int[] {
				automaton.add(new Automaton.Strung(expr.name)), argument };
		// TODO: inline function constraint here.
		return Fn(automaton, es);	
	}
	
	private int translate(Expr.Quantifier expr, Automaton automaton) {
		List<SyntacticType> unboundedVariables = expr.unboundedVariables;
		List<Pair<String,Expr>> boundedVariables = expr.boundedVariables;
		int[] vars = new int[unboundedVariables.size()+boundedVariables.size()];
		for (int i = 0; i != unboundedVariables.size(); ++i) {
			SyntacticType p = unboundedVariables.get(i);
			if (p.name == null) {
				internalFailure("missing support for nested type names",
						filename, p);
			} 
			// FIXME: there is a hack here where we've registered the bound of
			// the variable as itself. In fact, it should be its type.
			vars[i] = automaton.add(new Automaton.List(Var(automaton, p.name),
					Var(automaton, p.name)));
		}
		for (int i = 0, j = unboundedVariables.size(); i != boundedVariables
				.size(); ++i, ++j) {
			Pair<String, Expr> p = boundedVariables.get(i);
			vars[j] = automaton.add(new Automaton.List(
					Var(automaton, p.first()), translate(p.second(),automaton)));
		}
		int avars = automaton.add(new Automaton.Set(vars));
		int root = translate(expr.operand, automaton);
		if (expr instanceof Expr.ForAll) {
			return ForAll(automaton, avars, root);
		} else {
			return Exists(automaton, avars, root);
		}		
	}
		
	private void bindArgument(int argument, SyntacticType parameter, HashMap<Integer,Integer> binding, Automaton automaton) {
		if(parameter.name != null) {
			int v = Var(automaton,parameter.name);
			binding.put(v, argument);
		}
		
		if(parameter instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple tuple = (SyntacticType.Tuple) parameter;
			List<SyntacticType> operands = tuple.elements;
			for(int i=0;i!=operands.size();++i) {
				SyntacticType operand = operands.get(i);
				int idx = automaton.add(new Automaton.Int(i));
				int tupleload = TupleLoad(automaton,argument,idx); 
				bindArgument(tupleload,operand,binding,automaton);
			}
		}
	}
	
	private void usedFunctions(Expr e, HashSet<String> uses) {
		if (e instanceof Expr.Variable || e instanceof Expr.Constant) {
			// do nothing
		} else if (e instanceof Expr.Unary) {
			Expr.Unary ue = (Expr.Unary) e; 
			usedFunctions(ue.operand,uses);
		} else if (e instanceof Expr.Binary) {
			Expr.Binary ue = (Expr.Binary) e; 
			usedFunctions(ue.leftOperand,uses);
			usedFunctions(ue.rightOperand,uses);
		} else if (e instanceof Expr.Nary) {
			Expr.Nary ne = (Expr.Nary) e;
			for(Expr operand : ne.operands) {
				usedFunctions(operand,uses);
			}
		} else if (e instanceof Expr.Quantifier) {
			Expr.Quantifier qe = (Expr.Quantifier) e; 
			usedFunctions(qe.operand,uses);
		} else if (e instanceof Expr.FunCall) {
			Expr.FunCall fe = (Expr.FunCall) e;
			usedFunctions(fe.operand,uses);
			uses.add(fe.name);
		} else {
			internalFailure("invalid expression encountered (" + e
					+ ")", filename, e);
		}
	}
	
	/**
	 * Convert between a WYIL value and a WYONE value. Basically, this is really
	 * stupid and it would be good for them to be the same.
	 * 
	 * @param value
	 * @return
	 */
	private int convert(Value value, SyntacticElement element, Automaton automaton) {
		
		if (value instanceof Value.Bool) {
			Value.Bool b = (Value.Bool) value;
			return b.value ? automaton.add(True) : automaton.add(False);
		} else if (value instanceof Value.Integer) {
			Value.Integer v = (Value.Integer) value;
			return Num(automaton , BigRational.valueOf(v.value));
		} else if (value instanceof Value.Rational) {
			Value.Rational v = (Value.Rational) value;
			wyautl.util.BigRational br = v.value;
			return Num(automaton ,
					new BigRational(br.numerator(), br.denominator()));
		} else if (value instanceof Value.String) {
			Value.String v = (Value.String) value;			
			return automaton.add(new Automaton.Strung(v.value));
		} else if (value instanceof Value.Set) {
			Value.Set vs = (Value.Set) value;
			int[] vals = new int[vs.values.size()];
			int i = 0;
			for (Value c : vs.values) {
				vals[i++] = convert(c,element,automaton);
			}
			return Set(automaton , vals);
		} else if (value instanceof Value.Tuple) {
			Value.Tuple vt = (Value.Tuple) value;
			int[] vals = new int[vt.values.size()];
			for (int i = 0; i != vals.length; ++i) {
				vals[i] = convert(vt.values.get(i),element,automaton);
			}
			return Tuple(automaton , vals);
		} else {
			internalFailure("unknown value encountered (" + value + ", " + value.getClass().getName() + ")",
					filename,element);
			return -1;
		}
	}
	
	public static class AssertionFailure extends RuntimeException {
		private final WycsFile.Assert assertion;
		private final Automaton reduced;
		private final Automaton original;
		
		public AssertionFailure(String msg, WycsFile.Assert assertion,
				Automaton reduced, Automaton original) {
			super(msg);
			this.assertion = assertion;
			this.reduced = reduced;
			this.original = original;
		}
		
		public WycsFile.Assert assertion() {
			return assertion;
		}
		
		public Automaton reduction() {
			return reduced;
		}
		
		public Automaton original() {
			return original;
		}
	}
}
