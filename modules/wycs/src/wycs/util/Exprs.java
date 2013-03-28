package wycs.util;

import static wybs.lang.SyntaxError.internalFailure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import wybs.io.Token;
import wybs.lang.Attribute;
import wybs.util.Pair;
import wycs.lang.*;

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

	public static Expr SetUnion(Expr lhs, Expr rhs, Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { lhs, rhs },
				attributes);
		return Expr.FunCall(SET_UNION, new SyntacticType[0], argument,
				attributes);
	}

	public static Expr SetIntersection(Expr lhs, Expr rhs, Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { lhs, rhs },
				attributes);
		return Expr.FunCall(SET_INTERSECTION, new SyntacticType[0], argument,
				attributes);
	}
	
	public static Expr SetDifference(Expr lhs, Expr rhs,
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
	
	public static Expr IndexOf(Expr src, Expr idx, SyntacticType[] generics,
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
	
	public static Expr List(Expr[] operands, Collection<Attribute> attributes) {
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
	
	public static Expr ListAppend(Expr lhs, Expr rhs,
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

	public static Expr Record(String[] fields, Expr[] values,
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
	
	public static Expr FieldOf(Expr src, String field,
			Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE,
				new Expr[] { src, Expr.Constant(Value.String(field)) },
				attributes);
		return Expr.FunCall(MAP_INDEXOF, new SyntacticType[0], argument,
				attributes);
	}
	
	public static Expr FieldUpdate(Expr src, String field, Expr value,
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
}
