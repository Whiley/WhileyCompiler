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

package wyrl.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import wyautl.core.Automaton;
import wyrl.core.*;

/**
 * Provides various algorithms for statically computing the complexity of a
 * given rewrite rule. That is, the lower- and upper-bounds on the reduction
 * caused by a given rewrite. For example, consider this simple rewrite:
 *
 * <pre>
 * reduce Not(Not(BExpr e)):
 *     => e
 * </pre>
 *
 * This has a reduction complexity of 2 since it is guaranteed to eliminate
 * exactly two nodes.. Now, consider a variant:
 *
 * <pre>
 * reduce Not(Bool b):
 *     => False, if b == True
 * </pre>
 *
 * This has a reduction complexity of O(1) since it will eliminate one node
 * overall if it applies successfully, but this is not guaranteed because of the
 * conditional.
 *
 * @author David J. Pearce
 *
 */
public class RewriteComplexity {

	/**
	 * Determine the guaranteed minimum change in the size of an automaton after
	 * a given rewrite rule is applied. This is useful for statically judging
	 * how the rule will affect an automaton. Specifically, if the difference
	 * between the minimum size of the pattern and expression it is rewritten to
	 * is negative, then the automaton is guaranteed to reduce in size after a
	 * successful application.
	 *
	 * @param rule
	 *            The rewrite rule we are computing the complexity of.
	 * @return
	 */
	public static int minimumChange(SpecFile.RewriteDecl rw) {
		// First, check whether any of the rules are non-conditional.
		boolean isConditional = true;
		for (SpecFile.RuleDecl rd : rw.rules) {
			isConditional &= rd.condition != null;
		}
		if (isConditional) {
			return 0;
		}
		// Second calculate a lower bound on the rewrite complexity
		HashMap<String, Polynomial> bindings = new HashMap<String, Polynomial>();
		Polynomial startSize = minimumSize(rw.pattern, bindings);
		int min = Integer.MAX_VALUE;
		for (SpecFile.RuleDecl rd : rw.rules) {
			Polynomial endSize = RewriteComplexity.minimumSize(rd.result,
					bindings);
			Polynomial result = startSize.subtract(endSize);
			if (result.isConstant()) {
				int constant = result.constant().intValue();
				min = Math.min(constant, min);
			} else {
				min = 0;
			}
		}
		return min;
	}

	/**
	 * Determine the guaranteed minimum size of the automaton when a given
	 * pattern matches. This is useful for statically judging how a given
	 * rewrite rule will affect an automaton. Specifically, if the difference
	 * between the minimum size of the pattern and expression it is rewritten to
	 * is negative, then the automaton is guaranteed to reduce in size after a
	 * successful application.
	 *
	 * @param pattern
	 *            The pattern being examined.
	 * @param bindings
	 *            Variables encountered in the pattern will be bound to their
	 *            specific size values during this calculation.
	 * @return
	 */
	public static Polynomial minimumSize(Pattern pattern,
			Map<String, Polynomial> bindings) {
		if (pattern instanceof Pattern.Leaf) {
			Pattern.Leaf leaf = (Pattern.Leaf) pattern;
			return new Polynomial(minimumSize(leaf.type));
		} else if (pattern instanceof Pattern.Term) {
			Pattern.Term term = (Pattern.Term) pattern;
			if (term.data == null) {
				return Polynomial.ONE;
			} else {
				Polynomial result = minimumSize(term.data, bindings).add(
						Polynomial.ONE);
				if (term.variable != null) {
					bindings.put(term.variable, result);
				}
				return result;
			}
		} else {
			Pattern.Collection collection = (Pattern.Collection) pattern;
			int minSize = collection.elements.length;
			if (collection.unbounded) {
				// In the case of an unbounded pattern, then the last element
				// represents the multi-match. When computing the minimum size
				// of a pattern we simply assume this is zero.
				minSize = minSize - 1;
			}
			Polynomial result = Polynomial.ONE;
			for (int i = 0; i != minSize; ++i) {
				Pair<Pattern, String> p = collection.elements[i];
				Polynomial p_poly = minimumSize(p.first(), bindings);
				if (p.second() != null) {
					bindings.put(p.second(), p_poly);
				}
				result = result.add(p_poly);
			}
			return result;
		}
	}

	/**
	 * Determine the minimum size of a type match. This corresponds to the
	 * minimum size of any path through the non-deterministic choice nodes in
	 * the automaton. Observe that, even in the case of cyclic automata, there
	 * is guaranteed to be an ayclic path.
	 *
	 * @param type
	 * @return the minimal size, or Integer.MAX_VALUE (to signal infinity ---
	 *         which *should* be impossible).
	 */
	public static int minimumSize(Type type) {
		Automaton automaton = type.automaton();
		automaton.compact();
		automaton.minimise();
		BitSet onStack = new BitSet();
		int size = minimumSize(automaton.getRoot(0), onStack, automaton);
		if(size < 0) {
			throw new RuntimeException("PROBLEM --- " + type);
		}
		return size;
	}

	private static int minimumSize(int node, BitSet onStack, Automaton automaton) {
		if (node < 0) {
			// handle primitives directly
			return 1;
		} else if (onStack.get(node)) {
			// We are already visiting this node and, hence, we have detected a
			// cycle. In such case, we just return "infinity".
			return Integer.MAX_VALUE; // infinity!
		} else {
			onStack.set(node);
		}

		Automaton.Term term = (Automaton.Term) automaton.get(node);
		int size; // infinity
		switch (term.kind) {
		case Types.K_Bool:
		case Types.K_Int:
		case Types.K_Real:
		case Types.K_String: {
			size = 1;
			break;
		}
		case Types.K_Any:
		case Types.K_Not: {
			size = Integer.MAX_VALUE; // infinity
			break;
		}
		case Types.K_Meta:
		case Types.K_Ref: {
			size = minimumSize(term.contents, onStack, automaton);
			break;
		}
		case Types.K_Nominal: {
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			size = minimumSize(list.get(1), onStack, automaton);
			break;
		}
		case Types.K_And:
		case Types.K_Or: {
			Automaton.Set set = (Automaton.Set) automaton.get(term.contents);
			// for a non-deterministic choice node, the minimum size is the
			// minimum size of any node.
			size = Integer.MAX_VALUE;
			for (int i = 0; i != set.size(); ++i) {
				size = Math.min(size,
						minimumSize(set.get(i), onStack, automaton));
			}
			break;
		}
		case Types.K_Term: {
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			size = 0;
			if (list.size() > 1) {
				size = minimumSize(list.get(1), onStack, automaton);
			}
			// Increment whilst preserving infinity!
			size = size == Integer.MAX_VALUE ? size : size + 1;
			break;
		}
		case Types.K_Set:
		case Types.K_Bag:
		case Types.K_List: {
			Automaton.List list = (Automaton.List) automaton.get(term.contents);
			Automaton.Collection c = (Automaton.Collection) automaton.get(list
					.get(1));
			size = 0;
			for (int i = 0; i != c.size(); ++i) {
				int amt = minimumSize(c.get(i), onStack, automaton);
				// Increment whilst preserving infinity!
				if(amt == Integer.MAX_VALUE){
					size = Integer.MAX_VALUE;
				} else {
					size = size == Integer.MAX_VALUE ? size : size + amt;
				}
			}
			break;
		}
		default:
			throw new RuntimeException("Unknown automaton state encountered ("
					+ term.kind + ")");
		}

		onStack.clear(node);

		return size;
	}

	/**
	 * Determine the guaranteed minimum size of an automaton after evaluating a
	 * given expression. This is useful for statically judging how a given
	 * rewrite rule will affect an automaton. Specifically, if the difference
	 * between the minimum size of the pattern and expression it is rewritten to
	 * is negative, then the automaton is guaranteed to reduce in size after a
	 * successful application.
	 *
	 * @param Expr
	 *            The expr being examined.
	 * @param Environment
	 *            A mapping from variables to their guaranteed mininmal sizes.
	 * @return
	 */
	public static Polynomial minimumSize(Expr code,
			Map<String, Polynomial> environment) {
		if (code instanceof Expr.Constant) {
			return Polynomial.ONE;
		} else if (code instanceof Expr.UnOp) {
			// All unary operators return constants.
			return Polynomial.ONE;
		} else if (code instanceof Expr.BinOp) {
			return minimumSize((Expr.BinOp) code, environment);
		} else if (code instanceof Expr.NaryOp) {
			return minimumSize((Expr.NaryOp) code, environment);
		} else if (code instanceof Expr.Constructor) {
			return minimumSize((Expr.Constructor) code, environment);
		} else if (code instanceof Expr.ListAccess) {
			return minimumSize((Expr.ListAccess) code, environment);
		} else if (code instanceof Expr.ListUpdate) {
			return minimumSize((Expr.ListUpdate) code, environment);
		} else if (code instanceof Expr.Variable) {
			return minimumSize((Expr.Variable) code, environment);
		} else if (code instanceof Expr.Substitute) {
			return minimumSize((Expr.Substitute) code, environment);
		} else if (code instanceof Expr.Comprehension) {
			return Polynomial.ZERO;
		} else if (code instanceof Expr.TermAccess) {
			return minimumSize((Expr.TermAccess) code, environment);
		} else if (code instanceof Expr.Cast) {
			return minimumSize(((Expr.Cast) code).src, environment);
		} else {
			throw new RuntimeException("unknown expression encountered - "
					+ code);
		}
	}

	private static Polynomial minimumSize(Expr.Constructor code,
			Map<String, Polynomial> environment) {
		Polynomial result = Polynomial.ONE;
		if (code.argument != null) {
			result.add(minimumSize(code.argument, environment));
		}
		return result;
	}

	private static Polynomial minimumSize(Expr.Variable code,
			Map<String, Polynomial> environment) {
		Polynomial r = environment.get(code.var);
		if (r == null) {
			// indicates this must be a constructor
			return Polynomial.ONE;
		} else {
			return r;
		}
	}

	private static Polynomial minimumSize(Expr.BinOp code,
			Map<String, Polynomial> environment) {
		switch (code.op) {
		case AND:
		case OR:
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case EQ:
		case NEQ:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case IN:
		case IS:
			// All of these expression simply generate a constant value in the
			// automaton.
			return Polynomial.ONE;
		case DIFFERENCE:
		case APPEND:
			// The minimum size of an append/difference is zero (i.e. happens
			// when both arguments are zero).
			return Polynomial.ZERO;
		case RANGE:
			// The minimum size of a range is zero because it doesn't add
			// anything into the automaton.
			return Polynomial.ZERO;
		default:
			throw new RuntimeException("Unknown expression encountered ("
					+ code + ")");
		}
	}

	private static Polynomial minimumSize(Expr.NaryOp code,
			Map<String, Polynomial> environment) {
		switch (code.op) {
		case SETGEN:
		case BAGGEN:
		case LISTGEN:
			ArrayList<Expr> arguments = code.arguments;
			Polynomial result = Polynomial.ZERO;
			for(int i=0;i!=arguments.size();++i) {
				Polynomial p = minimumSize(arguments.get(i),environment);
				result = result.add(p);
			}
			return result;
		}
		throw new RuntimeException("need to implement!");
	}

	private static Polynomial minimumSize(Expr.ListAccess code,
			Map<String, Polynomial> environment) {

		// I'm not sure whether or not this is really the optimal choice here.
		// Certainly, it seems odd compared with how term accesses are handled.
		// On the other hand, I don't know what other options there are!

		Type type = code.attribute(Attribute.Type.class).type;
		return new Polynomial(minimumSize(type));
	}

	private static Polynomial minimumSize(Expr.ListUpdate code,
			Map<String, Polynomial> environment) {
		return minimumSize(code.src, environment);
	}

	private static Polynomial minimumSize(Expr.Substitute code,
			Map<String, Polynomial> environment) {
		// FIXME: can we do any better than this?
		return minimumSize(code.src, environment);
	}

	private static Polynomial minimumSize(Expr.TermAccess code,
			Map<String, Polynomial> environment) {
		// I think this makes sense...
		Polynomial min = minimumSize(code.src, environment);
		if (min.equals(Polynomial.ZERO)) {
			return Polynomial.ZERO;
		} else {
			return min.subtract(Polynomial.ONE);
		}
	}
}
