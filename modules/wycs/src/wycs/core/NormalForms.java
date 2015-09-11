package wycs.core;

import static wycc.lang.SyntaxError.internalFailure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wycc.io.Token;
import wycc.lang.Attribute;
import wycc.util.Pair;
import wycc.util.Triple;

/**
 * Provides a number of encodings for many of the standard data types found in a
 * typical programming language (especially, of course, those found in Whiley).
 * More specifically, the supported data types are:
 * <ul>
 * <li><b>Sets/</b>
 * <li><b>Records.</b> These are map field names (i.e. strings) to values of
 * with heterogenous types (i.e. different fields can map to different types).
 * Records are encoded as sets of tuples which implement
 * <code>wycs.lang.Map</code> and where fields are represented as strings
 * mapping to their contents.</li>
 * <li><b>Lists.</b></li>
 * <li><b>Maps.</b></li>
 * <li><b>Strings.</b></li>
 * </ul>
 *
 * @author David J. Pearce
 *
 */
public class NormalForms {

	// =============================================================================
	// Negation Normal Form
	// =============================================================================

	/**
	 * Convert a given expression into negation normal form, where all logical
	 * negations are pushed inwards as far as possible.
	 *
	 * @param e
	 *            --- expression to be converted.
	 * @return
	 */
	public static Code negationNormalForm(Code e) {
		return negationNormalForm(e,false);
	}

	private static Code negationNormalForm(Code e, boolean negate) {
		if(e instanceof Code.Variable || e instanceof Code.Constant) {
			return negate(e,negate);
		} else if(e instanceof Code.Unary) {
			return negationNormalForm((Code.Unary)e,negate);
		} else if(e instanceof Code.Binary) {
			return negationNormalForm((Code.Binary)e,negate);
		} else if(e instanceof Code.Nary) {
			return negationNormalForm((Code.Nary)e,negate);
		} else if(e instanceof Code.Quantifier) {
			return negationNormalForm((Code.Quantifier)e,negate);
		} else if(e instanceof Code.FunCall) {
			return negationNormalForm((Code.FunCall)e,negate);
		} else if(e instanceof Code.Load) {
			return negationNormalForm((Code.Load)e,negate);
		} else if(e instanceof Code.IndexOf) {
			return negationNormalForm((Code.IndexOf)e,negate);
		} else if(e instanceof Code.Is) {
			return negationNormalForm((Code.Is)e,negate);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}

	private static Code negationNormalForm(Code.Unary e, boolean negate) {
		switch (e.opcode) {
		case LENGTH:
		case NEG:
			return negate(e, negate);
		case NOT:
			return negationNormalForm(e.operands[0], !negate);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}

	private static Code negationNormalForm(Code.Binary e, boolean negate) {
		switch (e.opcode) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case REM:
		case LT:
		case LTEQ:
		case EQ:
		case NEQ:
			// TODO: there is a potential bug here if the arguments of this
			// binary expression are boolean expressions.
			return negate(e,negate);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}

	private static Code negationNormalForm(Code.Nary e, boolean negate) {
		switch (e.opcode) {
		case ARRAY:
		case TUPLE:
			return e; // noop
		case AND:
		case OR:
			Code.Op op = e.opcode;
			Code[] operands = new Code[e.operands.length];
			if(negate) {
				for(int i=0;i!=operands.length;++i) {
					operands[i] = negationNormalForm(e.operands[i], true);
				}
				// swap the operation
				op = op == Code.Nary.Op.AND ? Code.Nary.Op.OR : Code.Nary.Op.AND ;
			} else {
				for(int i=0;i!=operands.length;++i) {
					operands[i] = negationNormalForm(e.operands[i], false);
				}
			}
			return Code.Nary(SemanticType.Bool,op, operands, e.attributes());
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}

	private static Code negationNormalForm(Code.Quantifier e, boolean negate) {
		Code.Op opcode = e.opcode;
		if (negate) {
			opcode = e.opcode == Code.Op.EXISTS ? Code.Op.FORALL
					: Code.Op.EXISTS;
		}
		return Code.Quantifier(SemanticType.Bool, opcode,
				negationNormalForm(e.operands[0], negate), e.types,
				e.attributes());
	}

	private static Code negationNormalForm(Code.FunCall e, boolean negate) {
		// TODO: there is a potential bug here if the arguments of this
		// binary expression are boolean expressions.
		return negate(e,negate);
	}

	private static Code negationNormalForm(Code.Load e, boolean negate) {
		return negate(e, negate);
	}

	private static Code negationNormalForm(Code.IndexOf e, boolean negate) {
		return negate(e, negate);
	}
	
	private static Code negationNormalForm(Code.Is e, boolean negate) {
		return negate(e, negate);
	}
	
	private static Code negate(Code e, boolean negate) {
		if (!negate) {
			return e;
		} else if (e instanceof Code.Unary
				&& ((Code.Unary) e).opcode == Code.Unary.Op.NOT) {
			return e;
		} else if(e instanceof Code.Binary) {
			Code.Binary be = (Code.Binary) e;
			Code lhs = e.operands[0];
			Code rhs = e.operands[1];
			Code tmp;
			Code.Op op;
			switch(be.opcode) {
			case LT:
				op = Code.Op.LTEQ;
				tmp = lhs; lhs = rhs; rhs = tmp;
				break;
			case LTEQ:
				op = Code.Op.LT;
				tmp = lhs; lhs = rhs; rhs = tmp;
				break;
			case EQ:
				op = Code.Op.NEQ;
				break;
			case NEQ:
				op = Code.Op.EQ;
				break;
			default:
				return Code.Unary(SemanticType.Bool, Code.Op.NOT, e, e.attributes());
			}
			return Code.Binary(e.type, op, lhs, rhs, e.attributes());
		}

		return Code.Unary(SemanticType.Bool, Code.Op.NOT, e, e.attributes());
	}

	// =============================================================================
	// Prefix Normal Form
	// =============================================================================

	/**
	 * Convert a given expression into Prefix Normal Form, where there at most
	 * one quantifier which universally quantifies the entire expression. For
	 * example:
	 *
	 * <pre>
	 * forall(int x)(x > 0 && forall(int y)(y < 0))
	 * </pre>
	 *
	 * would become
	 *
	 * <pre>
	 * forall(int x, int y)(x > 0 && y < 0)
	 * </pre>
	 *
	 * @param e
	 * @return
	 */
	public static Code prefixNormalForm(Code e) {
		e = renameVariables(e);
		e = skolemiseExistentials(e);
		e = extractUniversals(e);
		return e;
	}

	/**
	 * Traverse the expression rename bound variables to ensure there is no
	 * potential for variable capture during the conversion to PNF.
	 *
	 * @param e
	 * @return
	 */
	public static Code renameVariables(Code e) {
		return renameVariables(e, new HashMap<Integer, Integer>(),
				new HashSet<Integer>());
	}

	/**
	 * Traverse the expression rename bound variables to ensure there is no
	 * potential for variable capture during the conversion to PNF.
	 *
	 * @param e
	 * @param freeVariable
	 *            --- the first available free variable.
	 *            clashing with.
	 * @return
	 */
	public static Code renameVariables(Code e, int freeVariable) {
		HashSet<Integer> globals = new HashSet<Integer>();
		globals.add(freeVariable-1);
		return renameVariables(e, new HashMap<Integer, Integer>(),
				new HashSet<Integer>(globals));
	}

	private static Code renameVariables(Code e,
			HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		if(e instanceof Code.Constant) {
			return e;
		} else if(e instanceof Code.Variable) {
			return renameVariables ((Code.Variable)e,binding,globals);
		} else if(e instanceof Code.Unary) {
			return renameVariables ((Code.Unary)e,binding,globals);
		} else if(e instanceof Code.Binary) {
			return renameVariables ((Code.Binary)e,binding,globals);
		} else if(e instanceof Code.Nary) {
			return renameVariables ((Code.Nary)e,binding,globals);
		} else if(e instanceof Code.Quantifier) {
			return renameVariables ((Code.Quantifier)e,binding,globals);
		} else if(e instanceof Code.FunCall) {
			return renameVariables ((Code.FunCall)e,binding,globals);
		} else if(e instanceof Code.Cast) {
			return renameVariables ((Code.Cast)e,binding,globals);
		} else if(e instanceof Code.Is) {
			return renameVariables ((Code.Is)e,binding,globals);
		} else if(e instanceof Code.Load) {
			return renameVariables ((Code.Load)e,binding,globals);
		} else if(e instanceof Code.IndexOf) {
			return renameVariables ((Code.IndexOf)e,binding,globals);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}

	private static Code renameVariables(Code.Variable e,
			HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		Code[] operands = new Code[e.operands.length];
		for (int i = 0; i != operands.length; ++i) {
			operands[i] = renameVariables(e.operands[i], binding, globals);
		}
		Integer i = binding.get(e.index);
		if(i == null) {
			i = e.index;
		}
		return Code.Variable(e.type, operands, i, e.attributes());
	}

	private static Code renameVariables(Code.Unary e,
			HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		return Code.Unary(e.type,e.opcode,renameVariables(e.operands[0],binding,globals),e.attributes());
	}

	private static Code renameVariables(Code.Binary e,
			HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		return Code.Binary(e.type,e.opcode,
				renameVariables(e.operands[0], binding, globals),
				renameVariables(e.operands[1], binding, globals),
				e.attributes());
	}

	private static Code renameVariables(Code.Nary e,
			HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		Code[] operands = new Code[e.operands.length];
		for(int i=0;i!=operands.length;++i) {
			operands[i] = renameVariables(e.operands[i],binding,globals);
		}
		return Code.Nary(e.type,e.opcode,operands,e.attributes());
	}

	private static Code renameVariables(Code.FunCall e,
			HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		return Code.FunCall(e.type,
				renameVariables(e.operands[0], binding, globals), e.nid,
				e.binding, e.attributes());
	}

	private static Code renameVariables(Code.Quantifier e,
			HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		binding = new HashMap<Integer, Integer>(binding);
		Pair<SemanticType,Integer>[] variables = new Pair[e.types.length];
		int freeVariable = findLargest(globals) + 1;
		for (int i = 0; i != variables.length; ++i) {
			Pair<SemanticType,Integer> p = e.types[i];
			int var = p.second();
			int index = freeVariable++;
			binding.put(var, index);
			globals.add(index);
			variables[i] = new Pair<SemanticType,Integer>(p.first(),
					index);
		}
		Code operand = renameVariables(e.operands[0], binding, globals);
		return Code.Quantifier(e.type, e.opcode, operand, variables,
				e.attributes());
	}

	private static Code renameVariables(Code.Cast e, HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		return Code.Cast(e.type, renameVariables(e.operands[0], binding, globals), e.target, e.attributes());
	}

	private static Code renameVariables(Code.Is e, HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		return Code.Is(e.type, renameVariables(e.operands[0], binding, globals), e.test, e.attributes());
	}

	private static Code renameVariables(Code.Load e,
			HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		return Code.Load(e.type,
				renameVariables(e.operands[0], binding, globals), e.index,
				e.attributes());
	}

	private static Code renameVariables(Code.IndexOf e,
			HashMap<Integer, Integer> binding, HashSet<Integer> globals) {
		return Code.IndexOf(e.type,
				renameVariables(e.operands[0], binding, globals),
				renameVariables(e.operands[1], binding, globals),
				e.attributes());
	}
	
	private static int findLargest(Set<Integer> vars) {
		int max = -1;
		for(int i : vars) {
			max = Math.max(max,i);
		}
		return max;
	}

	public static Code skolemiseExistentials(Code e) {
		return skolemiseExistentials(e, new HashMap(), new ArrayList());
	}

	private static Code skolemiseExistentials(Code e,
			HashMap<Integer, Code> binding, ArrayList<Code.Variable> captured) {
		if(e instanceof Code.Constant) {
			return e;
		} else if(e instanceof Code.Variable) {
			return skolemiseExistentials((Code.Variable)e,binding,captured);
		} else if(e instanceof Code.Unary) {
			return skolemiseExistentials((Code.Unary)e,binding,captured);
		} else if(e instanceof Code.Binary) {
			return skolemiseExistentials((Code.Binary)e,binding,captured);
		} else if(e instanceof Code.Nary) {
			return skolemiseExistentials((Code.Nary)e,binding,captured);
		} else if(e instanceof Code.Quantifier) {
			return skolemiseExistentials((Code.Quantifier)e,binding,captured);
		} else if(e instanceof Code.FunCall) {
			return skolemiseExistentials((Code.FunCall)e,binding,captured);
		} else if(e instanceof Code.Load) {
			return skolemiseExistentials((Code.Load)e,binding,captured);
		} else if(e instanceof Code.IndexOf) {
			return skolemiseExistentials((Code.IndexOf)e,binding,captured);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}

	private static Code skolemiseExistentials(Code.Variable e,
			HashMap<Integer, Code> binding, ArrayList<Code.Variable> captured) {
		Code sub = binding.get(e.index);
		if(sub != null) {
			return sub;
		} else {
			return e;
		}
	}

	private static Code skolemiseExistentials(Code.Unary e,
			HashMap<Integer, Code> binding, ArrayList<Code.Variable> captured) {
		return Code.Unary(e.type, e.opcode,
				skolemiseExistentials(e.operands[0], binding, captured),
				e.attributes());
	}

	private static Code skolemiseExistentials(Code.Binary e,
			HashMap<Integer, Code> binding, ArrayList<Code.Variable> captured) {
		Code lhs = skolemiseExistentials(e.operands[0], binding, captured);
		Code rhs = skolemiseExistentials(e.operands[1], binding, captured);
		return Code.Binary(e.type, e.opcode, lhs, rhs, e.attributes());
	}

	private static Code skolemiseExistentials(Code.Nary e,
			HashMap<Integer, Code> binding, ArrayList<Code.Variable> captured) {
		Code[] operands = new Code[e.operands.length];
		for (int i = 0; i != operands.length; ++i) {
			operands[i] = skolemiseExistentials(e.operands[i], binding,
					captured);
		}
		return Code.Nary(e.type, e.opcode, operands, e.attributes());
	}

	private static Code skolemiseExistentials(Code.Quantifier e,
			HashMap<Integer, Code> binding, ArrayList<Code.Variable> captured) {
		if(e.opcode == Code.Op.FORALL) {
			captured = new ArrayList<Code.Variable>(captured);
			Pair<SemanticType,Integer>[] types = new Pair[e.types.length];
			for (int i = 0; i != types.length; ++i) {
				Pair<SemanticType,Integer> p = e.types[i];
				captured.add(Code.Variable(p.first(), new Code[0], p.second()));
				types[i] = p;
			}
			Code operand = skolemiseExistentials(e.operands[0], binding,
					captured);
			return Code.Quantifier(e.type, e.opcode, operand, types,
					e.attributes());
		} else {
			binding = new HashMap<Integer,Code>(binding);
			for(Pair<SemanticType,Integer> p : e.types) {
				skolemiseVariable(p.first(),p.second(),binding,captured);
			}
			return skolemiseExistentials(e.operands[0],binding,captured);
		}
	}

	private static void skolemiseVariable(SemanticType type, Integer var,
			HashMap<Integer, Code> binding, ArrayList<Code.Variable> captured) {

		if (captured.isEmpty()) {
			// easy case
			binding.put(var, Code.Variable(type, new Code[0], var));
		} else {
			Code[] operands = new Code[captured.size()];
			for (int i = 0; i != operands.length; ++i) {
				Code.Variable c = captured.get(i);
				operands[i] = c;
			}
			Code skolem = Code.Variable(type, operands, var);
			binding.put(var, skolem);
		}
	}

	private static Code skolemiseExistentials(Code.FunCall e,
			HashMap<Integer, Code> binding, ArrayList<Code.Variable> captured) {
		Code operand = skolemiseExistentials(e.operands[0], binding, captured);
		return Code.FunCall(e.type, operand, e.nid, e.binding, e.attributes());
	}

	private static Code skolemiseExistentials(Code.Load e,
			HashMap<Integer, Code> binding, ArrayList<Code.Variable> captured) {
		Code operand = skolemiseExistentials(e.operands[0], binding, captured);
		return Code.Load(e.type, operand, e.index, e.attributes());
	}

	private static Code skolemiseExistentials(Code.IndexOf e,
			HashMap<Integer, Code> binding, ArrayList<Code.Variable> captured) {
		Code operand1 = skolemiseExistentials(e.operands[0], binding, captured);
		Code operand2 = skolemiseExistentials(e.operands[1], binding, captured);
		return Code.IndexOf(e.type, operand1, operand2, e.attributes());
	}

	public static Code extractUniversals(Code e) {
		ArrayList<Pair<SemanticType,Integer>> environment = new ArrayList();
		e = extractUniversals(e, environment);
		if (environment.size() != 0) {
			Pair<SemanticType,Integer>[] vars = environment
					.toArray(new Pair[environment.size()]);
			// FIXME: should really include attributes here
			return Code.Quantifier(SemanticType.Bool,Code.Op.FORALL,e,vars);
		} else {
			return e;
		}
	}

	private static Code extractUniversals(Code e,
			ArrayList<Pair<SemanticType, Integer>> environment) {
		if(e instanceof Code.Constant) {
			return e;
		} else if(e instanceof Code.Variable) {
			return extractUniversals((Code.Variable)e,environment);
		} else if(e instanceof Code.Unary) {
			return extractUniversals((Code.Unary)e,environment);
		} else if(e instanceof Code.Binary) {
			return extractUniversals((Code.Binary)e,environment);
		} else if(e instanceof Code.Nary) {
			return extractUniversals((Code.Nary)e,environment);
		} else if(e instanceof Code.Quantifier) {
			return extractUniversals((Code.Quantifier)e,environment);
		} else if(e instanceof Code.FunCall) {
			return extractUniversals((Code.FunCall)e,environment);
		} else if(e instanceof Code.Load) {
			return extractUniversals((Code.Load)e,environment);
		} else if(e instanceof Code.IndexOf) {
			return extractUniversals((Code.IndexOf)e,environment);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}

	private static Code extractUniversals(Code.Variable e,
			ArrayList<Pair<SemanticType, Integer>> environment) {
		return e;
	}

	private static Code extractUniversals(Code.Unary e,
			ArrayList<Pair<SemanticType, Integer>> environment) {
		return Code.Unary(e.type, e.opcode,
				extractUniversals(e.operands[0], environment), e.attributes());
	}

	private static Code extractUniversals(Code.Binary e,
			ArrayList<Pair<SemanticType, Integer>> environment) {
		Code lhs = extractUniversals(e.operands[0],environment);
		Code rhs = extractUniversals(e.operands[1],environment);
		return Code.Binary(e.type,e.opcode,lhs,rhs,e.attributes());
	}

	private static Code extractUniversals(Code.Nary e,
			ArrayList<Pair<SemanticType, Integer>> environment) {
		Code[] operands = new Code[e.operands.length];
		for (int i = 0; i != operands.length; ++i) {
			operands[i] = extractUniversals(e.operands[i], environment);
		}
		return Code.Nary(e.type,e.opcode,operands,e.attributes());
	}

	private static Code extractUniversals(Code.FunCall e,
			ArrayList<Pair<SemanticType, Integer>> environment) {
		return Code.FunCall(e.type,
				extractUniversals(e.operands[0], environment), e.nid,
				e.binding, e.attributes());
	}

	private static Code extractUniversals(Code.Load e,
			ArrayList<Pair<SemanticType, Integer>> environment) {
		return Code.Load(e.type, extractUniversals(e.operands[0], environment),
				e.index, e.attributes());
	}

	private static Code extractUniversals(Code.IndexOf e,
			ArrayList<Pair<SemanticType, Integer>> environment) {
		return Code.IndexOf(e.type, extractUniversals(e.operands[0], environment),
				extractUniversals(e.operands[1], environment), e.attributes());
	}
	
	private static Code extractUniversals(Code.Quantifier e,
			ArrayList<Pair<SemanticType, Integer>> environment) {

		if(e.opcode == Code.Op.FORALL) {
			for(Pair<SemanticType, Integer> p : e.types) {
				environment.add(p);
			}
		} else {
			// existentials should all be eliminated by skolemiseExistentials
			throw new IllegalArgumentException(
					"extenstential encountered: " + e);
		}

		return extractUniversals(e.operands[0],environment);
	}
}
