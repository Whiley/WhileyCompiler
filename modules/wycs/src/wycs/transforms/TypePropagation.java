package wycs.transforms;

import java.util.*;

import static wycc.lang.SyntaxError.*;
import wybs.lang.Builder;
import wycc.lang.NameID;
import wycc.lang.SyntacticElement;
import wycc.lang.Transform;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wycc.util.Triple;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.SemanticType;
import wycs.core.Value;
import wycs.syntax.*;
import wyfs.lang.Path;

public class TypePropagation implements Transform<WyalFile> {

	/**
	 * Determines whether type propagation is enabled or not.
	 */
	private boolean enabled = getEnable();

	private final Wyal2WycsBuilder builder;

	private String filename;

	// ======================================================================
	// Constructor(s)
	// ======================================================================

	public TypePropagation(Builder builder) {
		this.builder = (Wyal2WycsBuilder) builder;
	}

	// ======================================================================
	// Configuration Methods
	// ======================================================================

	public static String describeEnable() {
		return "Enable/disable type propagation";
	}

	public static boolean getEnable() {
		return true; // default value
	}

	public void setEnable(boolean flag) {
		this.enabled = flag;
	}

	// ======================================================================
	// Apply method
	// ======================================================================

	public void apply(WyalFile wf) {
		if (enabled) {
			this.filename = wf.filename();

			for (WyalFile.Declaration s : wf.declarations()) {
				propagate(s);
			}
		}
	}

	private void propagate(WyalFile.Declaration s) {
		if (s instanceof WyalFile.Function) {
			propagate((WyalFile.Function) s);
		} else if (s instanceof WyalFile.Macro) {
			propagate((WyalFile.Macro) s);
		} else if (s instanceof WyalFile.Type) {
			propagate((WyalFile.Type) s);
		} else if (s instanceof WyalFile.Assert) {
			propagate((WyalFile.Assert) s);
		} else if (s instanceof WyalFile.Import) {

		} else {
			internalFailure("unknown statement encountered (" + s + ")",
					filename, s);
		}
	}

	private void propagate(WyalFile.Function s) {
		if (s.constraint != null) {
			HashSet<String> generics = new HashSet<String>(s.generics);
			HashMap<String, SemanticType> environment = new HashMap<String, SemanticType>();
			addDeclaredVariables(s.from, environment, generics, s);
			addDeclaredVariables(s.to, environment, generics, s);
			SemanticType r = propagate(s.constraint, environment, generics, s);
			checkIsSubtype(SemanticType.Bool, r, s.constraint, s);
		}
	}

	private void propagate(WyalFile.Macro s) {
		HashSet<String> generics = new HashSet<String>(s.generics);
		HashMap<String, SemanticType> environment = new HashMap<String, SemanticType>();
		addDeclaredVariables(s.from, environment, generics, s);
		SemanticType r = propagate(s.body, environment, generics, s);
		checkIsSubtype(SemanticType.Bool, r, s.body, s);
	}

	private void propagate(WyalFile.Type s) {
		if (s.invariant != null) {
			HashSet<String> generics = new HashSet<String>(s.generics);
			HashMap<String, SemanticType> environment = new HashMap<String, SemanticType>();
			addDeclaredVariables(s.type, environment, generics, s);
			SemanticType r = propagate(s.invariant, environment, generics, s);
			checkIsSubtype(SemanticType.Bool, r, s.invariant, s);
		}
	}

	/**
	 * The purpose of this method is to add variable names declared within a
	 * type pattern to the given environment. For example, as follows:
	 *
	 * <pre>
	 * type tup is {int x, int y} where x < y
	 * </pre>
	 *
	 * In this case, <code>x</code> and <code>y</code> are variable names
	 * declared as part of the pattern.
	 *
	 * <p>
	 * Note, variables are both declared and initialised with the given type. In
	 * some cases (e.g. parameters), this makes sense. In other cases (e.g.
	 * local variable declarations), it does not. In the latter, the variable
	 * should then be updated with an appropriate type.
	 * </p>
	 *
	 * @param src
	 * @param t
	 * @param environment
	 */
	private HashMap<String, SemanticType> addDeclaredVariables(
			TypePattern pattern, HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		if (pattern instanceof TypePattern.Union) {
			// FIXME: in principle, we can do better here. However, I leave this
			// unusual case for the future.
		} else if (pattern instanceof TypePattern.Intersection) {
			// FIXME: in principle, we can do better here. However, I leave this
			// unusual case for the future.
		} else if (pattern instanceof TypePattern.Rational) {
			TypePattern.Rational tp = (TypePattern.Rational) pattern;
			environment = addDeclaredVariables(tp.numerator, environment,
					generics, context);
			environment = addDeclaredVariables(tp.denominator, environment,
					generics, context);
		} else if (pattern instanceof TypePattern.Record) {
			TypePattern.Record tp = (TypePattern.Record) pattern;
			for (TypePattern element : tp.elements) {
				environment = addDeclaredVariables(element, environment,
						generics, context);
			}
		} else if (pattern instanceof TypePattern.Tuple) {
			TypePattern.Tuple tp = (TypePattern.Tuple) pattern;
			for (TypePattern element : tp.elements) {
				environment = addDeclaredVariables(element, environment,
						generics, context);
			}
		} else {
			TypePattern.Leaf lp = (TypePattern.Leaf) pattern;

			if (lp.var != null) {
				try {
					SemanticType type = builder.convert(
							pattern.toSyntacticType(), generics, context);
					type = builder.expand(type, false, context);
					environment.put(lp.var.name, type);
				} catch (ResolveError re) {
					syntaxError(
							"cannot resolve as function or definition call",
							filename, pattern, re);
					return null;
				}
			}
		}

		return environment;
	}

	private void propagate(WyalFile.Assert s) {
		HashMap<String, SemanticType> environment = new HashMap<String, SemanticType>();
		SemanticType t = propagate(s.expr, environment, new HashSet<String>(),
				s);
		checkIsSubtype(SemanticType.Bool, t, s.expr, s);
	}

	/**
	 * Perform type propagation through a given expression, returning the type
	 * of value that is returned by evaluating this expression.
	 *
	 * @param e
	 * @param environment
	 * @param generics
	 * @param context
	 * @return
	 */
	private SemanticType propagate(Expr e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		SemanticType t;
		//		
		if (e instanceof Expr.Variable) {
			t = propagate((Expr.Variable) e, environment, generics, context);
		} else if (e instanceof Expr.Cast) {
			t = propagate((Expr.Cast) e, environment, generics, context);
		} else if (e instanceof Expr.Constant) {
			t = propagate((Expr.Constant) e, environment, generics, context);
		} else if (e instanceof Expr.Unary) {
			t = propagate((Expr.Unary) e, environment, generics, context);
		} else if (e instanceof Expr.Binary) {
			t = propagate((Expr.Binary) e, environment, generics, context);
		} else if (e instanceof Expr.Nary) {
			t = propagate((Expr.Nary) e, environment, generics, context);
		} else if (e instanceof Expr.Is) {
			t = propagate((Expr.Is) e, environment, generics, context);
		} else if (e instanceof Expr.Quantifier) {
			t = propagate((Expr.Quantifier) e, environment, generics, context);
		} else if (e instanceof Expr.Invoke) {
			t = propagate((Expr.Invoke) e, environment, generics, context);
		} else if (e instanceof Expr.IndexOf) {
			t = propagate((Expr.IndexOf) e, environment, generics, context);
		} else {
			internalFailure("unknown expression encountered (" + e + ")",
					filename, e);
			return null;
		}
		e.attributes().add(new TypeAttribute(t));
		return returnType(e);
	}

	private SemanticType propagate(Expr.Variable e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		SemanticType t = environment.get(e.name);
		if (t == null) {
			internalFailure("undeclared variable encountered (" + e + ")",
					filename, e);
		}
		return t;
	}

	private SemanticType propagate(Expr.Constant e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		return e.value.type();
	}

	private SemanticType propagate(Expr.Cast e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		try {
			SemanticType operandType = propagate(e.operand, environment, generics,
					context);
			SemanticType targetType = builder.convert(e.type, generics, context);
			// FIXME: what to do with constraints?
			operandType = builder.expand(operandType, false, context);
			targetType = builder.expand(targetType, false, context);
			// TODO: check cast is permitted.
			return targetType;
		} catch (ResolveError re) {
			syntaxError(re.getMessage(), filename, e, re);
			return null;
		}
	}

	private SemanticType propagate(Expr.Unary e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		// First, clone the environment. This is necessary to ensure that any
		// retyping caused within the left or right expressions does not
		// propagate out of this condition.
		environment = (HashMap<String, SemanticType>) environment.clone();
		//
		SemanticType op_type = propagate(e.operand, environment, generics,
				context);
		//
		switch (e.op) {
		case NOT:
			checkIsSubtype(SemanticType.Bool, op_type, e, context);
			break;
		case NEG:
			checkIsSubtype(SemanticType.IntOrReal, op_type, e, context);
			break;
		case LENGTHOF:
			checkIsSubtype(SemanticType.ArrayAny, op_type, e, context);
		}
		return op_type;
	}

	private SemanticType propagate(Expr.IndexOf e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		SemanticType src_type = propagate(e.operand, environment, generics,
				context);
		SemanticType index_type = propagate(e.index, environment, generics,
				context);
		if (src_type instanceof SemanticType.EffectiveTuple) {
			SemanticType.EffectiveTuple tt = (SemanticType.EffectiveTuple) src_type;
			checkIsSubtype(SemanticType.Int, index_type, e.operand, context);
			if (!(e.index instanceof Expr.Constant)) {
				syntaxError("constant index required for tuple load", filename,
						e.index);
			}
		} else {
			checkIsSubtype(SemanticType.ArrayAny, src_type, e.operand, context);
			// FIXME: handle case for effective array (i.e. union of sets)
			SemanticType.Array st = (SemanticType.Array) src_type;
			checkIsSubtype(SemanticType.Int, index_type, e.index, context);
		}

		return src_type;
	}

	private SemanticType propagate(Expr.Binary e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		HashMap<String, SemanticType> leftEnvironment = environment;
		HashMap<String, SemanticType> rightEnvironment = environment;;
		// First, clone the environment if appropriate. This is necessary to
		// ensure that any retyping caused within the left or right expressions
		// propagates out of this condition only when this makes sense.
		switch(e.op) {		
		case AND:
			// don't do anything.
			break;
		case OR:
			// protect any retypings from propagating out
			leftEnvironment = (HashMap<String, SemanticType>) environment.clone();
			rightEnvironment = (HashMap<String, SemanticType>) environment.clone();
			break;
		case IFF:					
		case IMPLIES:
			leftEnvironment = (HashMap<String, SemanticType>) environment.clone();
			rightEnvironment = leftEnvironment;
			break;
		}
		//
		SemanticType lhs_type = propagate(e.leftOperand, leftEnvironment, generics,
				context);
		SemanticType rhs_type = propagate(e.rightOperand, rightEnvironment,
				generics, context);
		
		if (SemanticType.And(lhs_type, rhs_type) instanceof SemanticType.Void) {
			// This is useful to sanity check that the operands make sense. For
			// example, the expression "1.0 == 1" does not yield an automaton
			// that reduces to "True" (i.e. because the Equality state has type
			// Or{Int,Real}). Therefore, to prevent subtle bugs which may arise
			// from this, we explicitly ensure that there is some value in
			// common with the left and right-hand sides.
			syntaxError("operand types are not compatible (" + lhs_type
					+ " vs " + rhs_type + ")", context.file().filename(), e);
		}

		switch (e.op) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case REM:
			checkIsSubtype(SemanticType.IntOrReal, lhs_type, e.leftOperand, context);
			checkIsSubtype(SemanticType.IntOrReal, rhs_type, e.rightOperand, context);
			return SemanticType.Or(lhs_type, rhs_type);
		case EQ:
		case NEQ:
			return SemanticType.Or(lhs_type, rhs_type);
		case AND:
		case OR:
		case IMPLIES:
		case IFF:
			checkIsSubtype(SemanticType.Bool, lhs_type, e.leftOperand, context);
			checkIsSubtype(SemanticType.Bool, rhs_type, e.rightOperand, context);
			return SemanticType.Bool;
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
			checkIsSubtype(SemanticType.IntOrReal, lhs_type, e.leftOperand, context);
			checkIsSubtype(SemanticType.IntOrReal, rhs_type, e.rightOperand, context);
			return SemanticType.Or(lhs_type, rhs_type);				
		}

		internalFailure("unknown binary expression encountered (" + e + ")",
				filename, e);
		return null; // deadcode
	}

	private SemanticType propagate(Expr.Nary e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		List<Expr> e_operands = e.operands;
		SemanticType[] op_types = new SemanticType[e_operands.size()];

		for (int i = 0; i != e_operands.size(); ++i) {
			op_types[i] = propagate(e_operands.get(i), environment, generics,
					context);
		}

		switch (e.op) {
		case TUPLE:
			if (op_types.length == 0) {
				return SemanticType.Void;
			} else {
				return SemanticType.Tuple(op_types);
			}
		case LIST:
			if (op_types.length == 0) {
				return SemanticType.Array(true, SemanticType.Void);
			} else {
				return SemanticType.Array(true, SemanticType.Or(op_types));
			}
		}

		internalFailure("unknown nary expression encountered (" + e + ")",
				filename, e);
		return null; // deadcode
	}

	private SemanticType propagate(Expr.Is e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		//
		try {
			SemanticType lhs = propagate(e.leftOperand, environment, generics,
					context);			
			SemanticType rhs = builder.convert(e.rightOperand,
					Collections.EMPTY_SET, context);			 	
			lhs = builder.expand(lhs,false,context);
			e.rightOperand.attributes().add(new TypeAttribute(rhs));				
			// The following is rather strange, but appears to work.
			// Essentially, it represents the greatest amount of knowledge we
			// know about the given type.
			rhs = SemanticType.Or(builder.expand(rhs,true,context),builder.expand(rhs,false,context));			
			retypeExpression(e.leftOperand, rhs, environment, context);			
			SemanticType intersection = SemanticType.And(lhs, rhs);
//			if (intersection instanceof SemanticType.Void) {
//				// These types have no intersection, hence this expression does
//				// not make sense.
//				syntaxError("incomparable operands", filename, e);
//			} else {
//				// Otherwise, we're all good.
//				return SemanticType.Bool;
//			}
			return SemanticType.Bool;
		} catch (ResolveError ex) {
			syntaxError("cannot resolve as type call", filename, e, ex);
		}
		return null; // dead-code
	}

	private SemanticType propagate(Expr.Quantifier e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {
		environment = new HashMap<String, SemanticType>(environment);
		propagate(e.pattern, environment, generics, context);
		SemanticType r = propagate(e.operand, environment, generics, context);
		checkIsSubtype(SemanticType.Bool, r, e.operand, context);

		return SemanticType.Bool;
	}

	private void propagate(TypePattern pattern,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {

		try {
			// First, convert the syntactic type into a semantic type. This may
			// still contain nominal types, however, and we need to get rid of
			// them for type checking purposes.
			SemanticType nominalType = builder.convert(
					pattern.toSyntacticType(), generics, context);

			if (pattern instanceof TypePattern.Tuple) {
				TypePattern.Tuple tt = (TypePattern.Tuple) pattern;
				for (TypePattern p : tt.elements) {
					propagate(p, environment, generics, context);
				}
			} else if (pattern instanceof TypePattern.Leaf) {
				TypePattern.Leaf l = (TypePattern.Leaf) pattern;
				// Get rid of any nominal types that may exist.
				SemanticType rawType = builder.expand(nominalType, false, context);
				// Add the raw type to the environment.
				environment.put(l.var.name, rawType);
			}

			pattern.attributes().add(new TypeAttribute(nominalType));
		} catch (ResolveError re) {
			syntaxError("cannot resolve as function or definition call",
					filename, pattern, re);
		}
	}

	private SemanticType propagate(Expr.Invoke e,
			HashMap<String, SemanticType> environment,
			HashSet<String> generics, WyalFile.Context context) {

		SemanticType argument = propagate(e.operand, environment, generics,
				context);
		
		// Construct concrete types for generic substitution
		ArrayList<SemanticType> ivkGenerics = new ArrayList<SemanticType>();
		for (int i = 0; i != e.generics.size(); ++i) {
			SyntacticType gt = e.generics.get(i);
			try {
				SemanticType t = builder.convert(gt, generics, context);
				ivkGenerics.add(t);
				gt.attributes().add(new TypeAttribute(t));
			} catch (ResolveError re) {
				syntaxError(re.getMessage(), filename, gt, re);
			}
		}
		// Now, attempt to resolve the function
		try {
			SemanticType.Function fnType;
			Map<String, SemanticType> binding;

			if (e.qualification == null) {
				// In this case, no package qualification is given. Hence, we
				// need to resolve the name based on the active important
				// statements and declarations within the current file.
				Triple<NameID, SemanticType.Function, Map<String, SemanticType>> p = builder
						.resolveAsFunctionType(e.name, argument, ivkGenerics,
								context);
				fnType = p.second();
				binding = p.third();
			} else {
				if(e.qualification.size() == 1) {
					e.qualification = builder.resolveAsModule(e.qualification.last(), context);
				} else {
					// In this case, a complete package qualification has been given. Hence,
					// we know the fully name identifier for this function and we
					// need only to check it exists and access the relevant
					// information.
				}				
				NameID nid = new NameID(e.qualification, e.name);
				Pair<SemanticType.Function, Map<String, SemanticType>> p = builder
						.resolveAsFunctionType(nid, argument, ivkGenerics,
								context);
				fnType = p.first();
				binding = p.second();
			}

			SemanticType[] fn_generics = fnType.generics();

			if (fn_generics.length != e.generics.size()) {
				// could resolve this with inference in the future.
				syntaxError(
						"incorrect number of generic arguments provided (got "
								+ e.generics.size() + ", required "
								+ fn_generics.length + ")", context.file()
								.filename(), e);
			}

			fnType = (SemanticType.Function) fnType.substitute(binding);
			return builder.expand(fnType, false, context);
		} catch (ResolveError re) {
			syntaxError("cannot resolve as function or definition call",
					context.file().filename(), e, re);
			return null;
		}
	}

	/**
	 * Calculate the most precise type that captures those possible values a
	 * given expression can evaluate to.
	 *
	 * @param e
	 * @return
	 */
	public static SemanticType returnType(Expr e) {
		SemanticType type = e.attribute(TypeAttribute.class).type;
		if (e instanceof Expr.Variable || e instanceof Expr.Constant
				|| e instanceof Expr.Quantifier || e instanceof Expr.Cast
				|| e instanceof Expr.Is) {
			return type;
		} else if (e instanceof Expr.Unary) {
			Expr.Unary ue = (Expr.Unary) e;
			switch (ue.op) {
			case NOT:
				return SemanticType.Bool;
			case NEG:
				return type;
			case LENGTHOF:
				return SemanticType.Int;
			}
		} else if (e instanceof Expr.Binary) {
			Expr.Binary ue = (Expr.Binary) e;
			switch (ue.op) {
			case ADD:
			case SUB:
			case MUL:
			case DIV:
			case REM:
				return type;
			case AND:
			case OR:
			case EQ:
			case NEQ:
			case IMPLIES:
			case IFF:
			case LT:
			case LTEQ:
			case GT:
			case GTEQ:
				return SemanticType.Bool;
			}
		} else if (e instanceof Expr.Nary) {
			Expr.Nary ue = (Expr.Nary) e;
			switch (ue.op) {
			case TUPLE:
			case LIST:
				return type;
			}
		} else if (e instanceof Expr.IndexOf) {
			Expr.IndexOf ue = (Expr.IndexOf) e;
			if (type instanceof SemanticType.EffectiveTuple) {
				SemanticType.EffectiveTuple tt = (SemanticType.EffectiveTuple) type;
				Value.Integer idx = (Value.Integer) ((Expr.Constant) ue.index).value;
				return tt.tupleElement(idx.value.intValue());
			} else {
				SemanticType.Array st = (SemanticType.Array) type;
				return st.element();
			}
		} else {
			Expr.Invoke fc = (Expr.Invoke) e;
			return ((SemanticType.Function) type).to();
		}
		// should be deadcode.
		throw new IllegalArgumentException("Invalid opcode for expression");
	}

	/**
	 * Apply constraints imposed by fixing a given expression to be a given
	 * type. For example, support we have:
	 * 
	 * <pre>
	 * assert:
	 *    forall(int|null x, null y):
	 *        if:
	 *           (x+1) is int
	 *        then:
	 *           x != y
	 * </pre>
	 * 
	 * This assertion is true because retyping "x+1" implies that x is an int
	 * 
	 * @param e
	 * @param type
	 * @param environment
	 * @param context
	 */
	public void retypeExpression(Expr e, SemanticType type,
			HashMap<String, SemanticType> environment, WyalFile.Context context) {
		if (e instanceof Expr.Variable) {
			Expr.Variable v = (Expr.Variable) e;
			// The new type is the intersection of the existing type and the
			// asserted type.
			SemanticType newType = SemanticType.And(environment.get(v.name),
					type);
			//
			environment.put(v.name, newType);
		} else {
			// FIXME: implement other cases
		}
	}

	/**
	 * Check that t1 :> t2 or, equivalently, that t2 is a subtype of t1. A type
	 * <code>t1</code> is said to be a subtype of another type <code>t2</code>
	 * iff the semantic set described by <code>t1</code> contains that described
	 * by <code>t2</code>.
	 *
	 * @param t1
	 *            --- Semantic type that should contain <code>t2</code>.
	 * @param t2
	 *            --- Semantic type that shold be contained by <code>t1/code>.
	 * @param element
	 *            --- Syntax error is reported against this element if <code>t1
	 *            </code> does not contain <code>t2</code>.
	 */
	private void checkIsSubtype(SemanticType t1, SemanticType t2,
			SyntacticElement element, WyalFile.Context context) {
		// First, we must eliminate all nominal information as much as possible
		// before we can perform this test.
		t1 = builder.expand(t1, false, context);
		t2 = builder.expand(t2, false, context);
		// Second, perform the subtype test.
		if (!SemanticType.isSubtype(t1, t2)) {
			syntaxError("expected type " + t1 + ", got type " + t2, filename,
					element);
		}
	}
}
