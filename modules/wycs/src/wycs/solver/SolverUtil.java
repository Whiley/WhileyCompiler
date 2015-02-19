package wycs.solver;

import static wycs.solver.Solver.Div;
import static wycs.solver.Solver.Mul;
import static wycs.solver.Solver.Sum;
import wyautl.core.Automaton;
import wycs.core.SemanticType;

/**
 * Provides a bunch of utility methods to simplify interfacing with the Solver
 * directly.
 *
 * @author David J. Pearce
 *
 */
public class SolverUtil {

	/**
	 * Construct an automaton node representing the negation of an arithmetic
	 * expression.
	 *
	 * @param automaton
	 *            --- automaton to create new node in.
	 * @param lhs
	 *            --- the expression to negate
	 * @return the index of the new node.
	 */
	static public int Neg(Automaton automaton, int lhs) {
		return Solver.Mul(automaton, automaton.add(new Automaton.Real(-1)),
				automaton.add(new Automaton.Bag(lhs)));
	}

	/**
	 * Construct an automaton node representing the addition of two arithmetic
	 * expressions.
	 *
	 * @param automaton
	 *            --- automaton to create new node in.
	 * @param lhs
	 *            --- left expression.
	 * @param rhs
	 *            --- right expression.
	 * @return the index of the new node.
	 */
	static public int Add(Automaton automaton, int lhs, int rhs) {
		return Solver.Sum(automaton,
				automaton.add(new Automaton.Real(0)),
				automaton.add(new Automaton.Bag(lhs, rhs)));
	}

	/**
	 * Construct an automaton node representing the subtraction of two arithmetic
	 * expressions.
	 *
	 * @param automaton
	 *            --- automaton to create new node in.
	 * @param lhs
	 *            --- left expression.
	 * @param rhs
	 *            --- right expression.
	 * @return the index of the new node.
	 */
	static public int Sub(Automaton automaton, int lhs, int rhs) {
		rhs = Neg(automaton,rhs);
		return Solver.Sum(automaton,
				automaton.add(new Automaton.Real(0)),
				automaton.add(new Automaton.Bag(lhs, rhs)));
	}

	/**
	 * Construct an automaton node representing the multiplication of two
	 * arithmetic expressions.
	 *
	 * @param automaton
	 *            --- automaton to create new node in.
	 * @param lhs
	 *            --- left expression.
	 * @param rhs
	 *            --- right expression.
	 * @return the index of the new node.
	 */
	static public int Mul(Automaton automaton, int lhs, int rhs) {
		return Solver.Mul(automaton,
				automaton.add(new Automaton.Real(1)),
				automaton.add(new Automaton.Bag(lhs, rhs)));
	}

	/**
	 * Construct an automaton node representing the multiplication of two
	 * arithmetic expressions.
	 *
	 * @param automaton
	 *            --- automaton to create new node in.
	 * @param lhs
	 *            --- left expression.
	 * @param rhs
	 *            --- right expression.
	 * @return the index of the new node.
	 */
	static public int Div(Automaton automaton, int lhs, int rhs) {
		return Solver.Div(automaton, lhs, rhs);
	}

	/**
	 * Construct an automaton node representing the equality of two
	 * expressions.
	 *
	 * @param automaton
	 *            --- automaton to create new node in.
	 * @param lhs
	 *            --- left expression.
	 * @param rhs
	 *            --- right expression.
	 * @return the index of the new node.
	 */
	static public int Equals(Automaton automaton, int type, int lhs, int rhs) {
		return Solver.Equals(automaton,
				type,
				automaton.add(new Automaton.Bag(lhs, rhs)));
	}

	/**
	 * Construct an automaton node representing an inequality comparing
	 * two arithmetic expressions.
	 *
	 * @param automaton
	 *            --- automaton to create new node in.
	 * @param lhs
	 *            --- left expression.
	 * @param rhs
	 *            --- right expression.
	 * @return the index of the new node.
	 */
	public static int LessThan(Automaton automaton, int type, int lhs, int rhs) {
		// lhs < rhs ==> lhs + 1 <= rhs ==> 0 < rhs -(lhs + 1)
		Automaton.State state = automaton.get(type);
		if(state.kind == Solver.K_IntT) {
			// integer case wheren inequality is non-strict.
			lhs = Add(automaton, lhs, Solver.Num(automaton, 1));
			int expr = Add(automaton, Neg(automaton, lhs), rhs);
			return Solver.Inequality(automaton,type,expr);
		} else {
			// non-integer case where inequality is strict.
			int expr = Add(automaton, Neg(automaton, lhs), rhs);
			return Solver.Inequality(automaton,type,expr);
		}
	}

	/**
	 * Construct an automaton node representing the addition of two arithmetic
	 * expressions.
	 *
	 * @param automaton
	 *            --- automaton to create new node in.
	 * @param lhs
	 *            --- left expression.
	 * @param rhs
	 *            --- right expression.
	 * @return the index of the new node.
	 */
	public static int LessThanEq(Automaton automaton, int type, int lhs,
			int rhs) {
		Automaton.State state = automaton.get(type);
		int expr = Add(automaton, Neg(automaton, lhs), rhs);
		int ieq = Solver.Inequality(automaton,type,expr);
		if(state.kind == Solver.K_IntT) {
			// integer case wheren inequality is non-strict.
			return ieq;
		} else {
			// non-integer case where inequality is strict.
			int eq = Solver.Equals(automaton, type,
					automaton.add(new Automaton.Bag(lhs, rhs)));
			return Solver.Or(automaton, ieq, eq);
		}
	}

	/**
	 * Construct an automaton node representing the logical implication between
	 * two nodes
	 *
	 * @param automaton
	 *            --- automaton to create new node in.
	 * @param lhs
	 *            --- left expression.
	 * @param rhs
	 *            --- right expression.
	 * @return the index of the new node.
	 */
	static public int Implies(Automaton automaton, int lhs, int rhs) {
		lhs = Solver.Not(automaton, lhs);
		return Solver.Or(automaton, new int[]{lhs,rhs});
	}
}
