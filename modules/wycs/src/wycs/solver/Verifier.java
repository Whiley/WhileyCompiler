package wycs.solver;

import static wybs.lang.SyntaxError.internalFailure;
import static wycs.solver.Solver.*;

import java.io.IOException;
import java.util.*;

import wyautl.core.*;
import wyautl.io.PrettyAutomataWriter;
import wyautl.util.BigRational;
import wybs.lang.SyntacticElement;
import wybs.util.Pair;
import wycs.lang.*;

/**
 * Responsible for converting a <code>WycsFile</code> into an automaton that can
 * then be simplified to set for satisfiability. The key challenge here is to
 * break down the rich language of expressions described by the
 * <code>WycsFile</code> format, such that they can be handled effectively by
 * the <code>Solver</code>.
 * 
 * @author David J. Pearce
 * 
 */
public class Verifier {
	private boolean debug = true;
	
	private String filename;
	
	public Verifier(boolean debug) {
		this.debug = debug;
	}
	
	/**
	 * Verify the given list of Wycs statements.
	 * 
	 * @param statements
	 * @return the set of failing assertions (if any).
	 */
	public List<Boolean> verify(WycsFile wf) {
		HashMap<String,Stmt.Define> environment = new HashMap<String,Stmt.Define>();
		this.filename = wf.filename();
		List<Stmt> statements = wf.stmts();		
		ArrayList<Boolean> results = new ArrayList<Boolean>();
		for (int i = 0; i != statements.size(); ++i) {
			Stmt stmt = statements.get(i);

			if (stmt instanceof Stmt.Assert) {
				boolean valid = unsat((Stmt.Assert) stmt, environment);
				results.add(valid);
			} else if(stmt instanceof Stmt.Define) {
				Stmt.Define def = (Stmt.Define) stmt;
				environment.put(def.name,def);
			} else {
				internalFailure("unknown statement encountered " + stmt,
						filename, stmt);
			}
		}
		return results;
	}
	
	private boolean unsat(Stmt.Assert stmt, HashMap<String,Stmt.Define> environment) {
		Automaton automaton = new Automaton();
		
		int assertion = translate(stmt.expr,environment,automaton);

		automaton.setRoot(0,Not(automaton, assertion));
		try {
			if (debug) {				
				new PrettyAutomataWriter(System.err, SCHEMA, "And",
						"Or").write(automaton);
			}

			infer(automaton);

			if (debug) {
				System.err.println("\n\n=> (" + Solver.numSteps
						+ " steps, " + Solver.numInferences
						+ " reductions, " + Solver.numInferences
						+ " inferences)\n");
				new PrettyAutomataWriter(System.err, SCHEMA, "And",
						"Or").write(automaton);
				System.err
				.println("\n============================================");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return automaton.get(automaton.getRoot(0)).equals(Solver.False);
	}
	
	private int translate(Expr expr, HashMap<String,Stmt.Define> environment, Automaton automaton) {
		if(expr instanceof Expr.Constant) {
			return translate((Expr.Constant) expr,environment,automaton);
		} else if(expr instanceof Expr.Variable) {
			return translate((Expr.Variable) expr,environment,automaton);
		} else if(expr instanceof Expr.Binary) {
			return translate((Expr.Binary) expr,environment,automaton);
		} else if(expr instanceof Expr.Unary) {
			return translate((Expr.Unary) expr,environment,automaton);
		} else if(expr instanceof Expr.FieldOf) {
			return translate((Expr.FieldOf) expr,environment,automaton);
		} else if(expr instanceof Expr.Nary) {
			return translate((Expr.Nary) expr,environment,automaton);
		} else if(expr instanceof Expr.Record) {
			return translate((Expr.Record) expr,environment,automaton);
		} else if(expr instanceof Expr.FunCall) {
			return translate((Expr.FunCall) expr,environment,automaton);
		} else if(expr instanceof Expr.FieldUpdate) {
			return translate((Expr.FieldUpdate) expr,environment,automaton);
		} else if(expr instanceof Expr.Quantifier) {
			return translate((Expr.Quantifier) expr,environment,automaton);
		} else {
			internalFailure("unknown: " + expr.getClass().getName(),
					filename, expr);
			return -1; // dead code
		}
	}
	
	private int translate(Expr.Constant expr,
			HashMap<String, Stmt.Define> environment, Automaton automaton) {
		return convert(expr.value,expr,automaton);
	}
	
	private int translate(Expr.Variable expr, HashMap<String, Stmt.Define> environment, Automaton automaton) {
		return Var(automaton,expr.name);
	}
	
	private int translate(Expr.FieldOf expr,
			HashMap<String, Stmt.Define> environment, Automaton automaton) {
		int src = translate(expr.operand, environment, automaton);
		int field = automaton.add(new Automaton.Strung(expr.field));
		//return FieldOf(automaton, src, field);
		return automaton.add(True);
	}
	
	private int translate(Expr.FieldUpdate expr,
			HashMap<String, Stmt.Define> environment, Automaton automaton) {
		int src = translate(expr.source, environment, automaton);
		int field = automaton.add(new Automaton.Strung(expr.field));
		int operand = translate(expr.operand, environment, automaton);
		//return FieldUpdate(automaton, src, field, operand);
		return automaton.add(True);
	}
	
	private int translate(Expr.Binary expr, HashMap<String, Stmt.Define> environment, Automaton automaton) {
		int lhs = translate(expr.leftOperand,environment,automaton);
		int rhs = translate(expr.rightOperand,environment,automaton);
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
		case DIFFERENCE:
			return Difference(automaton, lhs, rhs);
		case INDEXOF:
			// FIXME: may require axiom that {lhs[rhs]} {= rhs
			//return IndexOf(automaton, lhs, rhs);
			return automaton.add(True);
			
		}
		internalFailure("unknown binary expression encountered (" + expr + ")",
				filename, expr);
		return -1;
	}
	
	private int translate(Expr.Unary expr, HashMap<String, Stmt.Define> environment, Automaton automaton) {
		int e = translate(expr.operand,environment,automaton);
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
	
	private int translate(Expr.Nary expr, HashMap<String, Stmt.Define> environment, Automaton automaton) {
		Expr[] operands = expr.operands;
		int[] es = new int[operands.length];
		for(int i=0;i!=es.length;++i) {
			es[i] = translate(operands[i],environment,automaton); 
		}		
		switch(expr.op) {
		case AND:
			return And(automaton,es);
		case OR:
			return Or(automaton,es);
		case UNION:
			return Union(automaton, es);
		case INTERSECTION:
			return Intersect(automaton, es);
		case SET:
			return Set(automaton,es);
		case TUPLE:
			return Tuple(automaton,es);
		}
		internalFailure("unknown nary expression encountered (" + expr + ")",
				filename, expr);
		return -1;
	}
	
	private int translate(Expr.Record expr, HashMap<String, Stmt.Define> environment, Automaton automaton) {
		Expr[] operands = expr.operands;
		String[] fields = expr.fields;
		int[] es = new int[operands.length];
		for(int i=0;i!=es.length;++i) {
			int k = automaton.add(new Automaton.Strung(fields[i]));
			int v = translate(operands[i],environment,automaton); 
			es[i] = automaton.add(new Automaton.List(k, v));
		}		
		//return Record(automaton,es);
		return automaton.add(True);
	}
	
	private int translate(Expr.FunCall expr, HashMap<String, Stmt.Define> environment, Automaton automaton) {
		Stmt.Define def = environment.get(expr.name);
		Expr[] operands = expr.operands;
		
		if(def != null) {
			// inline macro definition
			int root = translate(def.expr,environment,automaton);
			
			// compute arguments and params (needs to be done first to know how
			// large to make mapping).
			int[] arguments = new int[operands.length];
			int[] params = new int[operands.length];
			for(int i=0;i!=operands.length;++i) {
				arguments[i] = translate(operands[i],environment,automaton);
				params[i] = Var(automaton,def.arguments.get(i).second());				
			}
			
			// construct intial mapping for substitution
			int[] mapping = new int[automaton.nStates()];
			for(int i=0;i!=mapping.length;++i) { mapping[i] = i; }
			
			// update mapping for each operand
			for(int i=0;i!=operands.length;++i) {
				mapping[params[i]] = arguments[i];				
			}
			
			// finally, apply substitution for all arguments atomically.
			return automaton.substitute(root, mapping);
		} else {
			// uninterpreted function call	
			int[] es = new int[operands.length+1];
			for(int i=0;i!=operands.length;++i) {
				es[i+1] = translate(operands[i],environment,automaton);
			}
			es[0] = automaton.add(new Automaton.Strung(expr.name));
			return Fn(automaton,es);
		}
	}
	
	private int translate(Expr.Quantifier expr,
			HashMap<String, Stmt.Define> environment, Automaton automaton) {
		List<Pair<Type, String>> expr_vars = expr.vars;
		int[] vars = new int[expr_vars.size()];
		for (int i = 0; i != expr_vars.size(); ++i) {
			Pair<Type, String> p = expr_vars.get(i);
			vars[i] = Var(automaton, p.second());
		}
		int avars = automaton.add(new Automaton.Set(vars));
		int root = translate(expr.expr, environment, automaton);
		if (expr instanceof Expr.ForAll) {
			return ForAll(automaton, avars, root);
		} else {
			return Exists(automaton, avars, root);
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
		} else if (value instanceof Value.Null) {
			return automaton.add(Null);
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
			internalFailure("unknown value encountered (" + value + ")",
					filename,element);
			return -1;
		}
	}
}
