package wycs.util;

import static wybs.lang.SyntaxError.internalFailure;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
	private static final String SET_DIFFERENCE = "Intersect";

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
	
	public static Expr IndexOf(Expr src, Expr idx,
			Collection<Attribute> attributes) {
		Expr argument = Expr.Nary(Expr.Nary.Op.TUPLE, new Expr[] { src, idx },
				attributes);
		return Expr.FunCall(MAP_INDEXOF, new SyntacticType[0], argument,
				attributes);
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
}
