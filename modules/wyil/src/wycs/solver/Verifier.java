package wycs.solver;

import static wybs.lang.SyntaxError.internalFailure;
import static wycs.solver.Solver.*;

import java.io.IOException;
import java.util.*;

import wyautl.core.*;
import wyautl.io.PrettyAutomataWriter;
import wyautl.util.BigRational;
import wybs.lang.SyntacticElement;
import wycs.lang.*;
import wyil.lang.Constant;
import wyil.lang.Type;

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
	
	/**
	 * The automaton used for rewriting.
	 */
	private Automaton automaton;
	
	/**
	 * The set of constraints being constructed during verification.
	 */
	private ArrayList<Integer> constraints;
	
	private final String filename;
	
	public Verifier(String filename) {
		this.automaton = new Automaton();
		this.constraints = new ArrayList<Integer>();
		this.filename = filename;
	}
	
	/**
	 * Verify the given list of Wycs statements.
	 * 
	 * @param statements
	 * @return the set of failing assertions (if any).
	 */
	public List<Boolean> verify(List<Stmt> statements) {
		ArrayList<Boolean> results = new ArrayList<Boolean>();
		for (int i = 0; i != statements.size(); ++i) {
			Stmt stmt = statements.get(i);

			if (stmt instanceof Stmt.Assume) {
				Stmt.Assume a = (Stmt.Assume) stmt;
				constraints.add(translate(a.expr));
			} else if (stmt instanceof Stmt.Assert) {
				boolean valid = unsat((Stmt.Assert) stmt);
				results.add(valid);
			}
		}
		return results;
	}
	
	private boolean unsat(Stmt.Assert stmt) {
		int assertion = translate(stmt.expr);

		// FIXME: improve the efficiency of this!
		Automaton tmp = new Automaton(automaton);
		int nassertion = Not(tmp, assertion);
		int root = And(tmp,nassertion,And(tmp,constraints));
		tmp.setRoot(0,root);
		try {
			if (debug) {				
				new PrettyAutomataWriter(System.err, SCHEMA, "And",
						"Or").write(tmp);

			}

			infer(tmp);

			if (debug) {
				System.err.println("\n\n=> (" + Solver.numSteps
						+ " steps, " + Solver.numInferences
						+ " reductions, " + Solver.numInferences
						+ " inferences)\n");
				new PrettyAutomataWriter(System.err, SCHEMA, "And",
						"Or").write(tmp);
				System.err
				.println("\n============================================");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmp.get(tmp.getRoot(0)).equals(Solver.False);
	}
	
	private int translate(Expr expr) {
		if(expr instanceof Expr.Constant) {
			return translate((Expr.Constant) expr);
		} else if(expr instanceof Expr.Variable) {
			return translate((Expr.Variable) expr);
		} else if(expr instanceof Expr.Binary) {
			return translate((Expr.Binary) expr);
		} else if(expr instanceof Expr.Unary) {
			return translate((Expr.Unary) expr);
		} else if(expr instanceof Expr.Quantifier) {
			return translate((Expr.Quantifier) expr);
		} else {
			internalFailure("unknown: " + expr.getClass().getName(),
					filename, expr);
			return -1; // dead code
		}
	}
	
	private int translate(Expr.Constant expr) {
		return convert(expr.constant,expr);
	}
	
	private int translate(Expr.Variable expr) {
		return Var(automaton,expr.name);
	}
	
	private int translate(Expr.Binary expr) {
		int lhs = translate(expr.lhs);
		int rhs = translate(expr.rhs);
		switch(expr.op) {
		case AND:
			return And(automaton,lhs,rhs);
		case OR:
			return Or(automaton,lhs,rhs);
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
		case EQ:
			return Equals(automaton, lhs, rhs);
		case NEQ:
			return Not(automaton, Equals(automaton, lhs, rhs));
		case LT:
			// FIXME: eliminate LessThan
			return LessThan(automaton, lhs, rhs);
		case LTEQ:
			// FIXME: eliminte LessThanEq
			return LessThanEq(automaton, lhs, rhs);
		case GT:
			return LessThan(automaton, rhs, lhs);
		case GTEQ:
			return LessThanEq(automaton, rhs, lhs);
		case SUBSETEQ:
			return SubsetEq(automaton, lhs, rhs);
		}
		internalFailure("unknown binary expression encountered (" + expr + ")",
				filename, expr);
		return -1;
	}
	
	private int translate(Expr.Unary expr) {
		int e = translate(expr.expr);
		switch(expr.op) {
		case NOT:
			return Not(automaton, e);
		case NEG:
			return Mul(automaton, automaton.add(new Automaton.Real(-1)),
					automaton.add(new Automaton.Bag(e)));
		}
		internalFailure("unknown unary expression encountered (" + expr + ")",
				filename, expr);
		return -1;
	}
	
	private int translate(Expr.Quantifier expr) {
		internalFailure("unknown quantifier encountered (" + expr + ")",
				filename, expr);
		return -1;
	}
	
	/**
	 * Convert between a WYIL value and a WYONE value. Basically, this is really
	 * stupid and it would be good for them to be the same.
	 * 
	 * @param value
	 * @return
	 */
	private int convert(wyil.lang.Constant value, SyntacticElement element) {
		
		if (value instanceof wyil.lang.Constant.Bool) {
			wyil.lang.Constant.Bool b = (wyil.lang.Constant.Bool) value;
			return b.value ? automaton.add(True) : automaton.add(False);
		} else if (value instanceof wyil.lang.Constant.Byte) {
			wyil.lang.Constant.Byte v = (wyil.lang.Constant.Byte) value;
			return Num(automaton , BigRational.valueOf(v.value));
		} else if (value instanceof wyil.lang.Constant.Char) {
			wyil.lang.Constant.Char v = (wyil.lang.Constant.Char) value;
			// Simple, but mostly good translation
			return Num(automaton , v.value);
		} else if (value instanceof wyil.lang.Constant.Map) {
			return automaton.add(False); // TODO
		} else if (value instanceof wyil.lang.Constant.Integer) {
			wyil.lang.Constant.Integer v = (wyil.lang.Constant.Integer) value;
			return Num(automaton , BigRational.valueOf(v.value));
		} else if (value instanceof wyil.lang.Constant.Rational) {
			wyil.lang.Constant.Rational v = (wyil.lang.Constant.Rational) value;
			wyil.util.BigRational br = v.value;
			return Num(automaton ,
					new BigRational(br.numerator(), br.denominator()));
		} else if (value instanceof wyil.lang.Constant.Null) {
			return automaton.add(Null);
		} else if (value instanceof wyil.lang.Constant.List) {
			Constant.List vl = (Constant.List) value;
			int[] vals = new int[vl.values.size()];
			for (int i = 0; i != vals.length; ++i) {
				vals[i] = convert(vl.values.get(i),element);
			}
			return List(automaton , vals);
		} else if (value instanceof wyil.lang.Constant.Set) {
			Constant.Set vs = (Constant.Set) value;
			int[] vals = new int[vs.values.size()];
			int i = 0;
			for (Constant c : vs.values) {
				vals[i++] = convert(c,element);
			}
			return Set(automaton , vals);
		} else if (value instanceof wyil.lang.Constant.Record) {
			Constant.Record vt = (Constant.Record) value;
			int[] vals = new int[vt.values.size()];
			int i = 0;
			for (Map.Entry<String, Constant> e : vt.values.entrySet()) {
				int k = automaton 
						.add(new Automaton.Strung(e.getKey()));
				int v = convert(e.getValue(),element);
				vals[i++] = automaton .add(new Automaton.List(k, v));
			}
			return Record(automaton , vals);
		} else if (value instanceof wyil.lang.Constant.Strung) {
			Constant.Strung vs = (Constant.Strung) value;
			int[] vals = new int[vs.value.length()];
			for (int i = 0; i != vals.length; ++i) {
				vals[i] = Num(automaton , vs.value.charAt(i));
			}
			return List(automaton , vals);
		} else if (value instanceof wyil.lang.Constant.Tuple) {
			Constant.Tuple vt = (Constant.Tuple) value;
			int[] vals = new int[vt.values.size()];
			for (int i = 0; i != vals.length; ++i) {
				vals[i] = convert(vt.values.get(i),element);
			}
			return Tuple(automaton , vals);
		} else {
			internalFailure("unknown value encountered (" + value + ")",
					filename,element);
			return -1;
		}
	}
}
