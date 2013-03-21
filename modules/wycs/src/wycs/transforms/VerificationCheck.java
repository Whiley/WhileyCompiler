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
				} else if (stmt instanceof WycsFile.Function
						|| stmt instanceof WycsFile.Define) {
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
		int assertion = translate(stmt.expr,automaton,new HashMap<String,Integer>());
		
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
	
	private int translate(Expr expr, Automaton automaton, HashMap<String,Integer> environment) {
		if(expr instanceof Expr.Constant) {
			return translate((Expr.Constant) expr,automaton,environment);
		} else if(expr instanceof Expr.Variable) {
			return translate((Expr.Variable) expr,automaton,environment);
		} else if(expr instanceof Expr.Binary) {
			return translate((Expr.Binary) expr,automaton,environment);
		} else if(expr instanceof Expr.Unary) {
			return translate((Expr.Unary) expr,automaton,environment);
		} else if(expr instanceof Expr.Nary) {
			return translate((Expr.Nary) expr,automaton,environment);
		} else if(expr instanceof Expr.TupleLoad) {
			return translate((Expr.TupleLoad) expr,automaton,environment);
		} else if(expr instanceof Expr.FunCall) {
			return translate((Expr.FunCall) expr,automaton,environment);
		} else if(expr instanceof Expr.Quantifier) {
			return translate((Expr.Quantifier) expr,automaton,environment);
		} else {
			internalFailure("unknown: " + expr.getClass().getName(),
					filename, expr);
			return -1; // dead code
		}
	}
	
	private int translate(Expr.Constant expr, Automaton automaton, HashMap<String,Integer> environment) {
		return convert(expr.value,expr,automaton);
	}
	
	private int translate(Expr.Variable expr, Automaton automaton, HashMap<String,Integer> environment) {
		Integer idx = environment.get(expr.name);
		if(idx == null) {
			// FIXME: this is a hack to work around modified operands after a
			// loop.
			return Var(automaton,expr.name); 
		} else {
			return idx;
		}
	}	
	
	private int translate(Expr.Binary expr, Automaton automaton, HashMap<String,Integer> environment) {
		int lhs = translate(expr.leftOperand,automaton,environment);
		int rhs = translate(expr.rightOperand,automaton,environment);
		SemanticType lhs_t = expr.leftOperand.attribute(TypeAttribute.class).type;
		SemanticType rhs_t = expr.rightOperand.attribute(TypeAttribute.class).type;
		boolean isInt = lhs_t instanceof SemanticType.Int
				&& rhs_t instanceof SemanticType.Int;
		
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
			if(isInt) {
				// FIXME: this could be improved!
				int l = LessThanEq(
						automaton,
						lhs,
						Sum(automaton, automaton.add(new Automaton.Real(-1)),
								automaton.add(new Automaton.Bag(rhs))));
				int r = LessThanEq(
						automaton,
						rhs,
						Sum(automaton, automaton.add(new Automaton.Real(-1)),
								automaton.add(new Automaton.Bag(lhs))));
				return Or(automaton, l, r);
			} else {
				return Not(automaton, Equals(automaton, lhs, rhs));
			}
		case IMPLIES:
			return Or(automaton,Not(automaton, lhs),rhs);
		case IFF:
			return Or(automaton, And(automaton, lhs, rhs),
					And(automaton, Not(automaton, lhs), Not(automaton, rhs)));
		case LT:
			if(isInt) {
				lhs = Sum(automaton, automaton.add(new Automaton.Real(1)),
								automaton.add(new Automaton.Bag(lhs)));
				return LessThanEq(automaton, lhs, rhs);
			} else {
				return LessThan(automaton, lhs, rhs);
			}
		case LTEQ:
			return LessThanEq(automaton, lhs, rhs);
		case GT:
			if(isInt) {
				rhs = Sum(automaton, automaton.add(new Automaton.Real(1)),
						automaton.add(new Automaton.Bag(rhs)));
				return LessThanEq(automaton, rhs, lhs);
			} else {
				return LessThan(automaton, rhs, lhs);
			}
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
	
	private int translate(Expr.Unary expr, Automaton automaton, HashMap<String,Integer> environment) {
		int e = translate(expr.operand,automaton,environment);
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
	
	private int translate(Expr.Nary expr, Automaton automaton, HashMap<String,Integer> environment) {
		Expr[] operands = expr.operands;
		int[] es = new int[operands.length];
		for(int i=0;i!=es.length;++i) {
			es[i] = translate(operands[i],automaton,environment); 
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
	
	private int translate(Expr.TupleLoad expr, Automaton automaton, HashMap<String,Integer> environment) {
		int e = translate(expr.operand,automaton,environment);
		int i = automaton.add(new Automaton.Int(expr.index));
		return TupleLoad(automaton,e,i);
	}
	
	private int translate(Expr.FunCall expr, Automaton automaton, HashMap<String,Integer> environment) {
		// uninterpreted function call
		int argument = translate(expr.operand,automaton,environment);						
		int[] es = new int[] {
				automaton.add(new Automaton.Strung(expr.name)), argument };
		// TODO: inline function constraint here.
		return Fn(automaton, es);	
	}
	
	private static int skolem = 0;
	
	private int translate(Expr.Quantifier expr, Automaton automaton, HashMap<String,Integer> environment) {
		HashMap<String,Integer> nEnvironment = new HashMap<String,Integer>(environment);
		Pair<TypePattern,Expr>[] variables = expr.variables;
		int[] vars = new int[variables.length];
		for (int i = 0; i != variables.length; ++i) {			
			Pair<TypePattern,Expr> p = variables[i];
			TypePattern pattern = p.first();
			Expr src = p.second();
			String root;
			if (pattern.var == null) {
				root = "$" + skolem++;
			} else {
				root = pattern.var;
			}
			int rootIdx = Var(automaton,root);
			nEnvironment.put(root, rootIdx);
			bindArgument(rootIdx,pattern,nEnvironment,automaton);
			if(src != null) {
				vars[i] = automaton.add(new Automaton.List(
					Var(automaton, root), translate(src,automaton,nEnvironment)));
			} else {
				// FIXME: there is a hack here where we've registered the bound of
				// the variable as itself. In fact, it should be its type.				
				vars[i] = automaton.add(new Automaton.List(
						Var(automaton, root), automaton.add(AnyT)));
			}
		}
		
		int avars = automaton.add(new Automaton.Set(vars));
		int root = translate(expr.operand, automaton, nEnvironment);
		if (expr instanceof Expr.ForAll) {
			return ForAll(automaton, avars, root);
		} else {
			return Exists(automaton, avars, root);
		}		
	}
		
	private void bindArgument(int argument, TypePattern parameter,
			HashMap<String, Integer> binding, Automaton automaton) {
		if (parameter.var != null) {
			binding.put(parameter.var, argument);
		}

		if (parameter instanceof TypePattern.Tuple) {
			TypePattern.Tuple tuple = (TypePattern.Tuple) parameter;
			TypePattern[] patterns = tuple.patterns;
			for (int i = 0; i != patterns.length; ++i) {
				TypePattern operand = patterns[i];
				int idx = automaton.add(new Automaton.Int(i));
				int tupleload = TupleLoad(automaton, argument, idx);
				bindArgument(tupleload, operand, binding, automaton);
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
			return Solver.String(automaton,v.value);
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
