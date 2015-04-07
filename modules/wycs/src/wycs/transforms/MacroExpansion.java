package wycs.transforms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static wycc.lang.SyntaxError.*;
import wybs.lang.Builder;
import wycc.lang.Transform;
import wycc.util.Pair;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.Code;
import wycs.core.SemanticType;
import wycs.core.WycsFile;

/**
 * Responsible for inlining <i>macros</i> (i.e. named expressions or types
 * created with the <code>defined</code> statement). For example:
 *
 * <pre>
 * define implies(bool x, bool y) as !x || y
 * 
 * assert:
 *    implies(true,true)
 * </pre>
 *
 * The <code>define</code> statement creates the typed macro
 * <code>implies(bool,bool)->bool</code>. After macro expansion, we are left
 * with the following:
 *
 * <pre>
 * define implies(bool x, bool y) as !x || y
 * 
 * assert:
 *    !true || true
 * </pre>
 *
 * Here, we can see that the <code>implies</code> macro has simply been replaced
 * by its definition, with its parameters substituted accordingly for its
 * arguments. The purpose of this transform is simply to implement this
 * expansion procedure.
 *
 * @author David J. Pearce
 *
 */
public class MacroExpansion implements Transform<WycsFile> {

	/**
	 * Determines whether macro inlining is enabled or not.
	 */
	private boolean enabled = getEnable();

	private final Wyal2WycsBuilder builder;

	private String filename;

	// ======================================================================
	// Constructor(s)
	// ======================================================================

	public MacroExpansion(Builder builder) {
		this.builder = (Wyal2WycsBuilder) builder;
	}

	// ======================================================================
	// Configuration Methods
	// ======================================================================

	public static String describeEnable() {
		return "Enable/disable constraint inlining";
	}

	public static boolean getEnable() {
		return true; // default value
	}

	public void setEnable(boolean flag) {
		this.enabled = flag;
	}

	// ======================================================================
	// Apply Method
	// ======================================================================

	public void apply(WycsFile wf) {
		if (enabled) {
			this.filename = wf.filename();
			for (WycsFile.Declaration s : wf.declarations()) {
				transform(s);
			}
		}
	}

	private void transform(WycsFile.Declaration s) {
		if (s instanceof WycsFile.Function) {
			WycsFile.Function sf = (WycsFile.Function) s;
			transform(sf);
		} else if (s instanceof WycsFile.Macro) {
			WycsFile.Macro sf = (WycsFile.Macro) s;
			transform(sf);
		} else if (s instanceof WycsFile.Type) {
			transform((WycsFile.Type) s);
		} else if (s instanceof WycsFile.Assert) {
			transform((WycsFile.Assert) s);
		} else {
			internalFailure("unknown declaration encountered (" + s + ")",
					filename, s);
		}
	}

	private void transform(WycsFile.Function s) {
		if (s.constraint != null) {
			s.constraint = transform(s.constraint);
		}
	}

	private void transform(WycsFile.Macro s) {
		s.condition = transform(s.condition);
	}

	private void transform(WycsFile.Assert s) {
		s.condition = transform(s.condition);
	}

	private void transform(WycsFile.Type s) {
		if(s.invariant != null) {
			s.invariant = transform(s.invariant);
		}
	}

	private Code<?> transform(Code<?> e) {
		if (e instanceof Code.Variable || e instanceof Code.Constant) {
			// do nothing
			return e;
		} else if (e instanceof Code.Cast) {
			return transform((Code.Cast) e);
		}  else if (e instanceof Code.Unary) {
			return transform((Code.Unary) e);
		} else if (e instanceof Code.Binary) {
			return transform((Code.Binary) e);
		} else if (e instanceof Code.Nary) {
			return transform((Code.Nary) e);
		} else if (e instanceof Code.Load) {
			return transform((Code.Load) e);
		} else if (e instanceof Code.Is) {
			return transform((Code.Is) e);
		} else if (e instanceof Code.FunCall) {
			return transform((Code.FunCall) e);
		} else if (e instanceof Code.Quantifier) {
			return transform((Code.Quantifier) e);
		} else {
			internalFailure("invalid expression encountered (" + e + ", "
					+ e.getClass().getName() + ")", filename, e);
			return null; // dead code
		}
	}

	private Code<?> transform(Code.Cast e) {
		return Code.Cast(e.type, transform(e.operands[0]), e.target, e.attributes());
	}
	
	private Code<?> transform(Code.Unary e) {
		return Code.Unary(e.type, e.opcode, transform(e.operands[0]),
				e.attributes());
	}

	private Code<?> transform(Code.Binary e) {
		return Code.Binary(e.type, e.opcode, transform(e.operands[0]),
				transform(e.operands[1]), e.attributes());
	}

	private Code<?> transform(Code.Is e) {
		Code operand = transform(e.operands[0]);
		// Expand the type to extract any constraints associated with nominal
		// contained within. These will produce null if there are no such
		// constraints. 
		Pair<SemanticType, Code<?>> p1 = expand(operand, e.type, 0);
		Pair<SemanticType, Code<?>> p2 = expand(operand, e.test, 0);
		// Check whether any constraints were produced and, if so, include them.
		Code result = Code.Is(p1.first(), operand, p2.first(), e.attributes());
		if (p2.second() != null) {
			// Yes, there were some constraints included in the nominal type.
			return and(result, p2.second());
		} else {
			// No, there were no constraints included.
			return result;
		}
	}
	
	private Code<?> transform(Code.Nary e) {
		Code<?>[] e_operands = e.operands;
		Code<?>[] operands = new Code[e_operands.length];
		for (int i = 0; i != e_operands.length; ++i) {
			operands[i] = transform(e_operands[i]);
		}
		return Code.Nary(e.type, e.opcode, operands, e.attributes());
	}

	private Code<?> transform(Code.Load e) {
		return Code.Load(e.type, transform(e.operands[0]), e.index,
				e.attributes());
	}

	private Code<?> transform(Code.FunCall e) {
		Code<?> r = e;
		try {
			WycsFile module = builder.getModule(e.nid.module());
			// In principle, module should not be null if TypePropagation has
			// already passed. However, in the case of a function call inserted
			// during code generation, there is no guarantee that it was
			// previously resolved. This can cause problems if the standard
			// library is not on the path as e.g. x[i] is translated into
			// a call to wycs.core.Map.IndexOf().
			if (module == null) {
				internalFailure("cannot resolve as module: " + e.nid.module(),
						filename, e);
			}
			Object d = module.declaration(e.nid.name(),e.type);			
			if (d instanceof WycsFile.Function) {
				// Do nothing, since functions are not expanded like macros.
				// Instead, their axioms may be instantiated later either before
				// or during rewriting.
			} else if (d instanceof WycsFile.Macro) {
				WycsFile.Macro m = (WycsFile.Macro) d;
				HashMap<String, SemanticType> generics = buildGenericBinding(
						m.type.generics(), e.type.generics());
				HashMap<Integer, Code> binding = new HashMap<Integer, Code>();
				binding.put(0, e.operands[0]);
				r = m.condition.substitute(binding).instantiate(generics);
			} else {
				internalFailure("cannot resolve as function or macro call",
						filename, e);
			}
		} catch (InternalFailure ex) {
			throw ex;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), filename, e, ex);
		}

		transform(e.operands[0]);
		return r;
	}

	private HashMap<String, SemanticType> buildGenericBinding(
			SemanticType[] from, SemanticType[] to) {
		HashMap<String, SemanticType> binding = new HashMap<String, SemanticType>();
		for (int i = 0; i != to.length; ++i) {
			SemanticType.Var v = (SemanticType.Var) from[i];
			binding.put(v.name(), to[i]);
		}
		return binding;
	}

	private Code<?> transform(Code.Quantifier e) {
		// Need to expand type constraints
		Pair<SemanticType, Integer>[] e_types = e.types;
		Pair<SemanticType, Integer>[] n_types = new Pair[e_types.length];
		Code<?> invariant = null;
		for (int i = 0; i != e_types.length; ++i) {
			Pair<SemanticType, Integer> p1 = e_types[i];
			Code.Variable var = Code.Variable(p1.first(), p1.second());
			Pair<SemanticType,Code<?>> p2 = expand(var, p1.first(), 0);
			n_types[i] = new Pair<SemanticType,Integer>(p2.first(),p1.second());
			Code<?> ei = p2.second();			
			if (ei != null) {
				invariant = invariant == null ? ei : and(invariant, ei);
			}
		}
		Code<?> body = transform(e.operands[0]);
		if (invariant != null) {
			// We need to treat universal and existential quantifiers
			// differently.
			if(e.opcode == Code.Op.EXISTS) {
				body = and(invariant, body);
			} else {
				body = implies(invariant, body);
			}
		}
		return Code.Quantifier(e.type, e.opcode, body, n_types, e.attributes());
	}

	/**
	 * Expand a given type into a predicate which must be true for variables of
	 * that type. In many cases, this is just "true". However, for type
	 * invariants, this yields the invariant. For example:
	 * 
	 * <pre>
	 * type nat is (int x) where:
	 *    x >= 0
	 * </pre>
	 * 
	 * For variables of type <code>nat</code> the return predicate will be
	 * <code>x >= 0</code>. In the case of no invariant being given, this method
	 * will return null. This is a simplification to avoid unnecessary checks
	 * being written into the bytecode further upstream.
	 * 
	 * @param root
	 *            --- The root expression (e.g. the variable being constrained).
	 * @param type
	 *            --- the type being expanded
	 * @param freeVar
	 *            --- the next available free variable            
	 * @return An expression representing the expanded invariant of this type,
	 *         or null if no such invariant exists.
	 */
	private Pair<SemanticType,Code<?>> expand(Code<?> root, SemanticType type, int freeVar) {
		if (type instanceof SemanticType.Atom || type instanceof SemanticType.Var) {
			return new Pair<SemanticType,Code<?>>(type,null);
		} else if (type instanceof SemanticType.Tuple) {
			return expand(root, (SemanticType.Tuple) type, freeVar);
		} else if (type instanceof SemanticType.Set) {
			return expand(root, (SemanticType.Set) type, freeVar);
		} else if (type instanceof SemanticType.Nominal) {
			return expand(root, (SemanticType.Nominal) type, freeVar);			
		} else if(type instanceof SemanticType.Not) {
			return expand(root, (SemanticType.Not) type, freeVar);
		} else if(type instanceof SemanticType.And) {
			return expand(root, (SemanticType.And) type, freeVar);
		} else if(type instanceof SemanticType.Or) {
			return expand(root, (SemanticType.Or) type, freeVar);
		} else {
			internalFailure("deadcode reached (" + type.getClass().getName() + ")", filename, root);
			return null; // dead-code
		}
	}

	/**
	 * Expand a given tuple type into an invariant or null (if none exists).
	 * 
	 * @param root
	 *            --- The root expression (e.g. the variable being constrained).
	 * @param type
	 *            --- the type being expanded
	 * @param freeVar
	 *            --- the next available free variable
	 * @return An expression representing the expanded invariant of this type,
	 *         or null if no such invariant exists.
	 */
	private Pair<SemanticType,Code<?>> expand(Code<?> root, SemanticType.Tuple type, int freeVar) {
		SemanticType[] elements = type.elements();
		SemanticType[] nelements = new SemanticType[elements.length];
		Code<?> invariant = null;
		// Go through each type element expanding its invariants. In the end, if
		// no elements expand to an invariant then we stick with null.
		// Otherwise, we have some invariant at the end.
		for (int i = 0; i != elements.length; ++i) {
			Code<?> ri = Code.Load(type, root, i);
			Pair<SemanticType,Code<?>> p = expand(ri, elements[i], freeVar);
			Code<?> ei = p.second();
			nelements[i] = p.first();
			if (ei != null) {
				// This element has yielded an actual invariant. Now, decide
				// whether this it the first one, or just another one.
				if (invariant == null) {
					// Yes, it's the first element which has given an invariant,
					// therefore it is the current overall invariant.
					invariant = ei;
				} else {
					// No, this is just another element which has given an
					// invariant, therefore just and them together.
					invariant = and(invariant, ei);
				}
			}
		}
		return new Pair<SemanticType,Code<?>>(SemanticType.Tuple(nelements),invariant);
	}
	
	/**
	 * Expand a given set type into an invariant or null (if none exists). If an
	 * invariant is generated from the element, this will be generalised to all
	 * elements of the set using a universal quantifier.
	 * 
	 * @param root
	 *            --- The root expression (e.g. the variable being constrained).
	 * @param type
	 *            --- the type being expanded
	 * @param freeVar
	 *            --- the next available free variable
	 * @return An expression representing the expanded invariant of this type,
	 *         or null if no such invariant exists.
	 */
	private Pair<SemanticType, Code<?>> expand(Code<?> root,
			SemanticType.Set type, int freeVar) {
		Code.Variable variable = Code.Variable(type.element(), freeVar);
		Pair<SemanticType, Code<?>> p = expand(variable, type.element(),
				++freeVar);
		SemanticType.Set nType = SemanticType.Set(type.flag(),
				p.first());
		Code<?> invariant = p.second();
		if (invariant != null) {
			Code<?> elemOf = Code.Binary(nType, Code.Binary.Op.IN, variable,
					root);
			invariant = implies(elemOf, invariant);
			invariant = Code.Quantifier(SemanticType.Bool, Code.Op.FORALL,
					invariant,
					new Pair[] { new Pair<SemanticType, Integer>(p.first(),
							variable.index) });
		}
		return new Pair<SemanticType, Code<?>>(nType, invariant);
	}
	
	/**
	 * Expand a given nominal type into an invariant or null (if none exists). 
	 * 
	 * @param root
	 *            --- The root expression (e.g. the variable being constrained).
	 * @param type
	 *            --- the type being expanded
	 * @param freeVar
	 *            --- the next available free variable
	 * @return An expression representing the expanded invariant of this type,
	 *         or null if no such invariant exists.
	 */
	private Pair<SemanticType,Code<?>> expand(Code<?> root, SemanticType.Nominal type, int freeVar) {
		try {
			WycsFile wf = builder.getModule(type.name().module());
			WycsFile.Type td = wf.declaration(type.name().name(),
					WycsFile.Type.class);
			Pair<SemanticType,Code<?>> p = expand(root, td.type, freeVar);
			Code<?> invariant = p.second();
			Code<?> td_invariant = td.invariant;
			if (td_invariant != null) {
				// An explicit invariant is given. We now need to map the given
				// root to the parameter of the invariant (which is always at
				// index 0).
				HashMap<Integer,Code> binding = new HashMap<Integer,Code>();
				binding.put(0,root);
				td_invariant = td_invariant.substitute(binding);
				// Finally, decide whether to use as is or append to the
				// invariant generated from the element type.
				if (invariant == null) {
					invariant = td_invariant;
				} else {
					invariant = and(invariant, td_invariant);
				}
			}
			return new Pair<SemanticType,Code<?>>(p.first(),invariant);
		} catch (Exception e) {
			internalFailure(e.getMessage(), filename, root, e);
			return null; // deadcode
		}
	}
	
	/**
	 * Expand a given negation type into an invariant or null (if none exists).
	 * If an invariant is generated from the element, this will be required to
	 * fail (i.e. rather than suceeding).
	 * 
	 * @param root
	 *            --- The root expression (e.g. the variable being constrained).
	 * @param type
	 *            --- the type being expanded
	 * @param freeVar
	 *            --- the next available free variable
	 * @return An expression representing the expanded invariant of this type,
	 *         or null if no such invariant exists.
	 */
	private Pair<SemanticType,Code<?>> expand(Code<?> root, SemanticType.Not type, int freeVar) {
		Code.Variable variable = Code.Variable(type.element(), freeVar);
		Pair<SemanticType,Code<?>> p = expand(variable, type.element(), freeVar++);
		Code<?> invariant = p.second();
		if (invariant != null) {
			invariant = Code.Unary(SemanticType.Bool, Code.Unary.Op.NOT, invariant);			
		}
		return new Pair<SemanticType,Code<?>>(SemanticType.Not(p.first()),invariant);
	}
	
	/**
	 * Expand a given intersection type into an invariant or null (if none exists).
	 * 
	 * @param root
	 *            --- The root expression (e.g. the variable being constrained).
	 * @param type
	 *            --- the type being expanded
	 * @param freeVar
	 *            --- the next available free variable
	 * @return An expression representing the expanded invariant of this type,
	 *         or null if no such invariant exists.
	 */
	private Pair<SemanticType,Code<?>> expand(Code<?> root, SemanticType.And type, int freeVar) {
		SemanticType[] elements = type.elements();
		SemanticType[] nelements = new SemanticType[elements.length];
		Code<?> invariant = null;
		// Go through each type element expanding its invariants. In the end, if
		// no elements expand to an invariant then we stick with null.
		// Otherwise, we have some invariant at the end.
		for (int i = 0; i != elements.length; ++i) {
			Pair<SemanticType,Code<?>> p = expand(root, elements[i], freeVar);
			nelements[i] = p.first();
			Code<?> ei = p.second();
			if (ei != null) {
				// This element has yielded an actual invariant. Now, decide
				// whether this it the first one, or just another one.
				if (invariant == null) {
					// Yes, it's the first element which has given an invariant,
					// therefore it is the current overall invariant.
					invariant = ei;
				} else {
					// No, this is just another element which has given an
					// invariant, therefore just and them together.
					invariant = and(invariant, ei);
				}
			}
		}
		return new Pair<SemanticType,Code<?>>(SemanticType.And(nelements),invariant);
	}
	
	/**
	 * Expand a given union type into an invariant or null (if none exists).
	 * 
	 * @param root
	 *            --- The root expression (e.g. the variable being constrained).
	 * @param type
	 *            --- the type being expanded
	 * @param freeVar
	 *            --- the next available free variable
	 * @return An expression representing the expanded invariant of this type,
	 *         or null if no such invariant exists.
	 */
	private Pair<SemanticType,Code<?>> expand(Code<?> root, SemanticType.Or type, int freeVar) {
		SemanticType[] elements = type.elements();
		SemanticType[] nelements = new SemanticType[elements.length];
		Code<?> invariant = null;
		// Go through each type element expanding its invariants. In the end, if
		// no elements expand to an invariant then we stick with null.
		// Otherwise, we have some invariant at the end.
		for (int i = 0; i != elements.length; ++i) {
			Pair<SemanticType, Code<?>> p = expand(root, elements[i], freeVar);
			nelements[i] = p.first();
			Code<?> ei = p.second();
			if (ei != null) {
				// This element has yielded an actual invariant. The first thing
				// is to add the test that this is the given type.
				ei = implies(Code.Is(type, root, p.first()),ei);
				// Now, decide whether this it the first one, or just another
				// one.
				if (invariant == null) {
					// Yes, it's the first element which has given an invariant,
					// therefore it is the current overall invariant.
					invariant = ei;
				} else {
					// No, this is just another element which has given an
					// invariant, therefore just and them together.
					invariant = or(invariant, ei);
				}
			}
		}
		return new Pair<SemanticType,Code<?>>(SemanticType.Or(nelements),invariant);
	}
	
	private static Code<?> implies(Code<?> lhs, Code<?> rhs) {
		lhs = Code.Unary(SemanticType.Bool, Code.Unary.Op.NOT, lhs);
		return Code.Nary(SemanticType.Bool, Code.Op.OR, new Code[] { lhs, rhs });
	}
	
	private static Code<?> and(Code<?> lhs, Code<?> rhs) {
		return Code.Nary(SemanticType.Bool, Code.Op.AND, new Code[] { lhs, rhs });
	}
	
	private static Code<?> or(Code<?> lhs, Code<?> rhs) {
		return Code.Nary(SemanticType.Bool, Code.Op.OR, new Code[] { lhs, rhs });
	}
}
