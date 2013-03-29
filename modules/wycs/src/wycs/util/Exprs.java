package wycs.util;

import static wybs.lang.SyntaxError.internalFailure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import wybs.io.Token;
import wybs.lang.Attribute;
import wybs.util.Pair;
import wycs.core.SemanticType;
import wycs.core.Value;
import wycs.syntax.*;

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
public class Exprs {
	
	// =============================================================================
	// Sets
	// =============================================================================
	
	private static final String SET_UNION = "Union";
	private static final String SET_INTERSECTION = "Intersect";
	private static final String SET_DIFFERENCE = "Difference";

	private static Expr SetUnion(Expr lhs, Expr rhs, Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { lhs, rhs },
				attributes);
		return Expr.FunCall(SET_UNION, new SyntacticType[0], argument,
				attributes);
	}

	private static Expr SetIntersection(Expr lhs, Expr rhs, Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { lhs, rhs },
				attributes);
		return Expr.FunCall(SET_INTERSECTION, new SyntacticType[0], argument,
				attributes);
	}
	
	private static Expr SetDifference(Expr lhs, Expr rhs,
			Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { lhs, rhs },
				attributes);
		return Expr.FunCall(SET_DIFFERENCE, new SyntacticType[0], argument,
				attributes);
	}
	
	// =============================================================================
	// Maps
	// =============================================================================
	private static final String MAP_INDEXOF = "IndexOf";
	private static final String MAP_UPDATE = "Update";
	
	private static Expr IndexOf(Expr src, Expr idx, SyntacticType[] generics,
			Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { src, idx },
				attributes);
		return Expr.FunCall(MAP_INDEXOF, generics, argument, attributes);
	}
	
	// =============================================================================
	// Lists
	// =============================================================================
		
	private static final String LIST_APPEND = "Append";	
	private static final String LIST_UPDATE = "ListUpdate";
	
	private static Expr List(Expr[] operands, Collection<Attribute> attributes) {
		Expr[] pairs = new Expr[operands.length];

		int i = 0;
		for (Expr operand : operands) {
			Value.Integer idx = Value.Integer(BigInteger.valueOf(i));
			pairs[i++] = Expr.Nary(Expr.Nary.Op.TUPLE,
					new Expr[] { Expr.Constant(idx, attributes), operand },
					attributes);
		}

		return Expr.Nary(Expr.Nary.Op.SET, pairs, attributes);
	}
	
	public static Expr SubList(Expr src, Expr start, Expr end,
			Collection<Attribute> attributes) {
		throw new RuntimeException("need to implement Exprs.SubList");
	}
	
	private static Expr ListAppend(Expr lhs, Expr rhs,
			Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { lhs, rhs },
				attributes);
		return Expr.FunCall(LIST_APPEND, new SyntacticType[0], argument,
				attributes);
	}

	public static Expr ListUpdate(Expr src, Expr idx, Expr value,
			Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE,
				new Expr[] { src, idx, value },
				attributes);
		return Expr.FunCall(LIST_UPDATE, new SyntacticType[0], argument,
				attributes);
	}
	
	public static Expr ListRange(Expr start, Expr end,
			Collection<Attribute> attributes) {
		throw new RuntimeException("need to implement Exprs.ListRange");
	}
	
	
	// =============================================================================
	// Records
	// =============================================================================

	private static Expr Record(String[] fields, Expr[] values,
			Collection<Attribute> attributes) {
		if(fields.length != values.length) {
			throw new IllegalArgumentException("fields.length != values.length");
		}
		Expr[] pairs = new Expr[fields.length];

		for (int i = 0; i != fields.length; ++i) {
			Value.String field = Value.String(fields[i]);
			pairs[i] = Expr.Nary(Expr.Nary.Op.TUPLE,
					new Expr[] { Expr.Constant(field, attributes), values[i] },
					attributes);
		}

		return Expr.Nary(Expr.Nary.Op.SET, pairs, attributes);		
	}
	
	private static Expr FieldOf(Expr src, String field,
			Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE,
				new Expr[] { src, Expr.Constant(Value.String(field)) },
				attributes);
		return Expr.FunCall(MAP_INDEXOF, new SyntacticType[0], argument,
				attributes);
	}
	
	private static Expr FieldUpdate(Expr src, String field, Expr value,
			Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE,
				new Expr[] { src, Expr.Constant(Value.String(field)), value },
				attributes);
		return Expr.FunCall(MAP_UPDATE, new SyntacticType[0], argument,
				attributes);
	}
	
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
	public static Expr negationNormalForm(Expr e) {
		return negationNormalForm(e,false);
	}
	
	private static Expr negationNormalForm(Expr e, boolean negate) {
		if(e instanceof Expr.Variable || e instanceof Expr.Constant) {
			return negate(e,negate);
		} else if(e instanceof Expr.Unary) {
			return negationNormalForm((Expr.Unary)e,negate);
		} else if(e instanceof Expr.Binary) {
			return negationNormalForm((Expr.Binary)e,negate);
		} else if(e instanceof Expr.Nary) {
			return negationNormalForm((Expr.Nary)e,negate);
		} else if(e instanceof Expr.Quantifier) {
			return negationNormalForm((Expr.Quantifier)e,negate);
		} else if(e instanceof Expr.FunCall) {
			return negationNormalForm((Expr.FunCall)e,negate);
		} else if(e instanceof Expr.Load) {
			return negationNormalForm((Expr.Load)e,negate);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}
	
	private static Expr negationNormalForm(Expr.Unary e, boolean negate) {
		switch (e.op) {
		case LENGTHOF:
		case NEG:
			return negate(e, negate);
		case NOT:
			return negationNormalForm(e.operand, !negate);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}
	
	private static Expr negationNormalForm(Expr.Binary e, boolean negate) {
		switch (e.op) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case REM:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case IN:
		case SUBSET:
		case SUBSETEQ:
		case SUPSET:
		case SUPSETEQ:
		case EQ:
		case NEQ:
			// TODO: there is a potential bug here if the arguments of this
			// binary expression are boolean expressions.
			return negate(e,negate);			
		case IMPLIES:
			// RECALL: p => q is sugar for !p || q
			if(negate) {
				Expr lhs = negationNormalForm(e.leftOperand, false);
				Expr rhs = negationNormalForm(e.rightOperand, true);
				return Expr.Nary(Expr.Nary.Op.AND, new Expr[] { lhs, rhs },
						e.attributes());
			} else {
				Expr lhs = negationNormalForm(e.leftOperand, true);
				Expr rhs = negationNormalForm(e.rightOperand, false);
				return Expr.Nary(Expr.Nary.Op.OR, new Expr[] { lhs, rhs },
						e.attributes());
			}
		case IFF:
			// FIXME: implement this case!
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}
	
	private static Expr negationNormalForm(Expr.Nary e, boolean negate) {
		switch (e.op) {
		case SET:
		case TUPLE:
			return e; // noop
		case AND:
		case OR:
			Expr.Nary.Op op = e.op;
			Expr[] operands = new Expr[e.operands.length];
			if(negate) {
				for(int i=0;i!=operands.length;++i) {
					operands[i] = negationNormalForm(e.operands[i], true);
				}
				// swap the operation
				op = op == Expr.Nary.Op.AND ? Expr.Nary.Op.OR : Expr.Nary.Op.AND ;
			} else {
				for(int i=0;i!=operands.length;++i) {
					operands[i] = negationNormalForm(e.operands[i], false);
				}
			}
			return Expr.Nary(op, operands, e.attributes());
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}
	
	private static Expr negationNormalForm(Expr.Quantifier e, boolean negate) {
		if(negate) {			
			if(e instanceof Expr.Exists) {
				return Expr.ForAll(e.variables,
						negationNormalForm(e.operand, negate), e.attributes());
			} else {
				return Expr.Exists(e.variables,
						negationNormalForm(e.operand, negate), e.attributes());
			}
		} else if(e instanceof Expr.ForAll) {
			return Expr.ForAll(e.variables,
					negationNormalForm(e.operand, negate), e.attributes());
		} else {
			return Expr.Exists(e.variables,
					negationNormalForm(e.operand, negate), e.attributes());
		}
	}
	
	private static Expr negationNormalForm(Expr.FunCall e, boolean negate) {
		// TODO: there is a potential bug here if the arguments of this
		// binary expression are boolean expressions.
		return negate(e,negate);
	}
	
	private static Expr negationNormalForm(Expr.Load e, boolean negate) {
		// TODO: there is a potential bug here if the arguments of this
		// binary expression are boolean expressions.
		return e;
	}
	
	private static Expr negate(Expr e, boolean negate) {
		if (!negate) {
			return e;
		} else if (e instanceof Expr.Unary
				&& ((Expr.Unary) e).op == Expr.Unary.Op.NOT) {
			return e;
		} else if(e instanceof Expr.Binary) {
			Expr.Binary be = (Expr.Binary) e;
			Expr.Binary.Op op;
			switch(be.op) {
			case LT:		
				op = Expr.Binary.Op.GTEQ;
				break;
			case LTEQ:
				op = Expr.Binary.Op.GT;
				break;
			case GT:
				op = Expr.Binary.Op.LTEQ;
				break;
			case GTEQ:
				op = Expr.Binary.Op.LT;
				break;
			case SUBSET:
				op = Expr.Binary.Op.SUPSETEQ;
				break;
			case SUBSETEQ:
				op = Expr.Binary.Op.SUPSET;
				break;
			case SUPSET:
				op = Expr.Binary.Op.SUBSETEQ;
				break;
			case SUPSETEQ:
				op = Expr.Binary.Op.SUBSET;
				break;
			case EQ:
				op = Expr.Binary.Op.NEQ;
				break;
			case NEQ:
				op = Expr.Binary.Op.EQ;
				break;
			default:
				return Expr.Unary(Expr.Unary.Op.NOT, e, e.attributes());
			}
			return Expr.Binary(op, be.leftOperand, be.rightOperand, e.attributes());
		} 
		
		return Expr.Unary(Expr.Unary.Op.NOT, e, e.attributes());
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
	public static Expr prefixNormalForm(Expr e) {
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
	 * @param captured
	 * @return
	 */
	public static Expr renameVariables(Expr e) {
		return renameVariables(e, new HashMap<String, Integer>(),
				new HashMap<String, Integer>());
	}
			
	private static Expr renameVariables(Expr e,
			HashMap<String, Integer> binding, HashMap<String, Integer> globals) {
		if(e instanceof Expr.Constant) {
			return e;
		} else if(e instanceof Expr.Variable) {
			return renameVariables ((Expr.Variable)e,binding,globals);
		} else if(e instanceof Expr.Unary) {
			return renameVariables ((Expr.Unary)e,binding,globals);
		} else if(e instanceof Expr.Binary) {
			return renameVariables ((Expr.Binary)e,binding,globals);
		} else if(e instanceof Expr.Nary) {
			return renameVariables ((Expr.Nary)e,binding,globals);
		} else if(e instanceof Expr.Quantifier) {
			return renameVariables ((Expr.Quantifier)e,binding,globals);
		} else if(e instanceof Expr.FunCall) {
			return renameVariables ((Expr.FunCall)e,binding,globals);
		} else if(e instanceof Expr.Load) {
			return renameVariables ((Expr.Load)e,binding,globals);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}
	
	private static Expr renameVariables(Expr.Variable e,
			HashMap<String, Integer> binding, HashMap<String, Integer> globals) {
		Integer i = binding.get(e.name);
		return Expr.Variable(e.name + "$" + i,e.attributes());
	}
	
	private static Expr renameVariables(Expr.Unary e,
			HashMap<String, Integer> binding, HashMap<String, Integer> globals) {
		return Expr.Unary(e.op,renameVariables(e.operand,binding,globals),e.attributes());
	}
	
	private static Expr renameVariables(Expr.Binary e,
			HashMap<String, Integer> binding, HashMap<String, Integer> globals) {
		return Expr.Binary(e.op,
				renameVariables(e.leftOperand, binding, globals),
				renameVariables(e.rightOperand, binding, globals),
				e.attributes());
	}
	
	private static Expr renameVariables(Expr.Nary e,
			HashMap<String, Integer> binding, HashMap<String, Integer> globals) {
		Expr[] operands = new Expr[e.operands.length];
		for(int i=0;i!=operands.length;++i) {
			operands[i] = renameVariables(e.operands[i],binding,globals);
		}
		return Expr.Nary(e.op,operands,e.attributes());
	}
	
	private static Expr renameVariables(Expr.FunCall e,
			HashMap<String, Integer> binding, HashMap<String, Integer> globals) {
		return Expr.FunCall(e.name, e.generics,
				renameVariables(e.operand, binding, globals), e.attributes());
	}
	
	private static Expr renameVariables(Expr.Quantifier e,
			HashMap<String, Integer> binding, HashMap<String, Integer> globals) {
		binding = new HashMap<String, Integer>(binding);
		Pair<SyntacticType,Expr.Variable>[] variables = new Pair[e.variables.length];		
		for(int i=0;i!=variables.length;++i) {
			Pair<SyntacticType,Expr.Variable> p = e.variables[i];
			Expr.Variable var = p.second();
			Integer index = globals.get(var.name);
			if(index == null) {
				index = 0;				
			} else {
				index = index + 1;
			}
			binding.put(var.name,index);
			globals.put(var.name,index);
			var = Expr.Variable(var.name + "$" + index, var.attributes());
			variables[i] = new Pair<SyntacticType,Expr.Variable>(p.first(),var);
		}
		Expr operand = renameVariables(e.operand,binding,globals);
		if(e instanceof Expr.ForAll) {
			return Expr.ForAll(variables,operand,e.attributes());
		} else {
			return Expr.Exists(variables,operand,e.attributes());
		}
	}
	
	private static Expr renameVariables(Expr.Load e,
			HashMap<String, Integer> binding, HashMap<String, Integer> globals) {
		return Expr.Load(renameVariables(e.operand, binding, globals),
				e.index, e.attributes());
	}
	
	public static Expr skolemiseExistentials(Expr e) {
		return skolemiseExistentials(e, new HashMap(), new ArrayList());
	}
	
	private static Expr skolemiseExistentials(Expr e,
			HashMap<String, Expr> binding, ArrayList<Expr.Variable> captured) {
		if(e instanceof Expr.Constant) {
			return e;
		} else if(e instanceof Expr.Variable) {
			return skolemiseExistentials((Expr.Variable)e,binding,captured);
		} else if(e instanceof Expr.Unary) {
			return skolemiseExistentials((Expr.Unary)e,binding,captured);
		} else if(e instanceof Expr.Binary) {
			return skolemiseExistentials((Expr.Binary)e,binding,captured);
		} else if(e instanceof Expr.Nary) {
			return skolemiseExistentials((Expr.Nary)e,binding,captured);
		} else if(e instanceof Expr.Quantifier) {
			return skolemiseExistentials((Expr.Quantifier)e,binding,captured);
		} else if(e instanceof Expr.FunCall) {
			return skolemiseExistentials((Expr.FunCall)e,binding,captured);
		} else if(e instanceof Expr.Load) {
			return skolemiseExistentials((Expr.Load)e,binding,captured);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}
	
	private static Expr skolemiseExistentials(Expr.Variable e,
			HashMap<String, Expr> binding, ArrayList<Expr.Variable> captured) {
		Expr sub = binding.get(e.name);
		if(sub != null) {
			return sub;
		} else {
			return e;
		}
	}

	private static Expr skolemiseExistentials(Expr.Unary e,
			HashMap<String, Expr> binding, ArrayList<Expr.Variable> captured) {
		return Expr.Unary(e.op,
				skolemiseExistentials(e.operand, binding, captured),
				e.attributes());
	}
	
	private static Expr skolemiseExistentials(Expr.Binary e,
			HashMap<String, Expr> binding, ArrayList<Expr.Variable> captured) {
		Expr lhs = skolemiseExistentials(e.leftOperand, binding, captured);
		Expr rhs = skolemiseExistentials(e.rightOperand, binding, captured);
		return Expr.Binary(e.op, lhs, rhs, e.attributes());
	}
	
	private static Expr skolemiseExistentials(Expr.Nary e,
			HashMap<String, Expr> binding, ArrayList<Expr.Variable> captured) {
		Expr[] operands = new Expr[e.operands.length];
		for (int i = 0; i != operands.length; ++i) {
			operands[i] = skolemiseExistentials(e.operands[i], binding,
					captured);
		}
		return Expr.Nary(e.op, operands, e.attributes());
	}
	
	private static Expr skolemiseExistentials(Expr.Quantifier e,
			HashMap<String, Expr> binding, ArrayList<Expr.Variable> captured) {
		if(e instanceof Expr.ForAll) {
			captured = new ArrayList<Expr.Variable>(captured);
			for(Pair<SyntacticType,Expr.Variable> p : e.variables) {
				captured.add(p.second());
			}
			Expr operand = skolemiseExistentials(e.operand, binding, captured);
			return Expr.ForAll(e.variables, operand, e.attributes());
		} else {
			binding = new HashMap<String,Expr>(binding);
			for(Pair<SyntacticType,Expr.Variable> p : e.variables) {
				skolemiseVariable(p.first(),p.second(),binding,captured);
			}
			return skolemiseExistentials(e.operand,binding,captured);
		}
	}
	
	private static void skolemiseVariable(SyntacticType type, Expr.Variable var,
			HashMap<String, Expr> binding, ArrayList<Expr.Variable> captured) {

		if (captured.isEmpty()) {
			// easy case
			binding.put(var.name, var);
		} else {
			Expr[] operands = new Expr[captured.size()];
			SemanticType[] types = new SemanticType[operands.length];
			for (int i = 0; i != operands.length; ++i) {
				Expr.Variable c = captured.get(i);
				operands[i] = c;
				types[i] = c.attribute(TypeAttribute.class).type;
			}
			SemanticType.Tuple operandT = SemanticType.Tuple(types);
			Expr operand = Expr.Nary(Expr.Nary.Op.TUPLE, operands,
					new TypeAttribute(operandT));
			Expr skolem = Expr.FunCall(var.name, new SyntacticType[0], operand,
					var.attributes());
			binding.put(var.name, skolem);
		}
	}
	
	private static Expr skolemiseExistentials(Expr.FunCall e,
			HashMap<String, Expr> binding, ArrayList<Expr.Variable> captured) {
		Expr operand = skolemiseExistentials(e.operand, binding, captured);
		return Expr.FunCall(e.name, e.generics, operand, e.attributes());
	}
	
	private static Expr skolemiseExistentials(Expr.Load e,
			HashMap<String, Expr> binding, ArrayList<Expr.Variable> captured) {
		Expr operand = skolemiseExistentials(e.operand, binding, captured);
		return Expr.Load(operand, e.index, e.attributes());
	}
	
	public static Expr extractUniversals(Expr e) {
		ArrayList<Pair<SyntacticType,Expr.Variable>> environment = new ArrayList();
		e = extractUniversals(e, environment);
		if (environment.size() != 0) {
			Pair<SyntacticType, Expr.Variable>[] vars = environment
					.toArray(new Pair[environment.size()]);
			// FIXME: should really include attributes here
			return Expr.ForAll(vars, e);
		} else {
			return e;
		}
	}
	
	private static Expr extractUniversals(Expr e,
			ArrayList<Pair<SyntacticType, Expr.Variable>> environment) {
		if(e instanceof Expr.Constant) {
			return e;
		} else if(e instanceof Expr.Variable) {
			return extractUniversals((Expr.Variable)e,environment);
		} else if(e instanceof Expr.Unary) {
			return extractUniversals((Expr.Unary)e,environment);
		} else if(e instanceof Expr.Binary) {
			return extractUniversals((Expr.Binary)e,environment);
		} else if(e instanceof Expr.Nary) {
			return extractUniversals((Expr.Nary)e,environment);
		} else if(e instanceof Expr.Quantifier) {
			return extractUniversals((Expr.Quantifier)e,environment);
		} else if(e instanceof Expr.FunCall) {
			return extractUniversals((Expr.FunCall)e,environment);
		} else if(e instanceof Expr.Load) {
			return extractUniversals((Expr.Load)e,environment);
		}
		throw new IllegalArgumentException("unknown expression encountered: "
				+ e);
	}
	
	private static Expr extractUniversals(Expr.Variable e,
			ArrayList<Pair<SyntacticType, Expr.Variable>> environment) {
		return e;
	}
	
	private static Expr extractUniversals(Expr.Unary e,
			ArrayList<Pair<SyntacticType, Expr.Variable>> environment) {
		return Expr.Unary(e.op,extractUniversals(e.operand,environment),e.attributes());
	}
	
	private static Expr extractUniversals(Expr.Binary e,
			ArrayList<Pair<SyntacticType, Expr.Variable>> environment) {
		Expr lhs = extractUniversals(e.leftOperand,environment);
		Expr rhs = extractUniversals(e.rightOperand,environment);
		return Expr.Binary(e.op,lhs,rhs,e.attributes());
	}
	
	private static Expr extractUniversals(Expr.Nary e,
			ArrayList<Pair<SyntacticType, Expr.Variable>> environment) {
		Expr[] operands = new Expr[e.operands.length];
		for (int i = 0; i != operands.length; ++i) {
			operands[i] = extractUniversals(e.operands[i], environment);
		}
		return Expr.Nary(e.op,operands,e.attributes());
	}
	
	private static Expr extractUniversals(Expr.FunCall e,
			ArrayList<Pair<SyntacticType, Expr.Variable>> environment) {
		return Expr.FunCall(e.name,e.generics,extractUniversals(e.operand,environment),e.attributes());
	}
	
	private static Expr extractUniversals(Expr.Load e,
			ArrayList<Pair<SyntacticType, Expr.Variable>> environment) {
		return Expr.Load(extractUniversals(e.operand,environment),e.index,e.attributes());
	}
	
	private static Expr extractUniversals(Expr.Quantifier e,
			ArrayList<Pair<SyntacticType, Expr.Variable>> environment) {
		
		if(e instanceof Expr.ForAll) {
			for(Pair<SyntacticType, Expr.Variable> p : e.variables) {
				environment.add(p);
			}
		} else {
			// existentials should all be eliminated by skolemiseExistentials
			throw new IllegalArgumentException(
					"extenstential encountered: " + e);
		}
		
		return extractUniversals(e.operand,environment);
	}
}
