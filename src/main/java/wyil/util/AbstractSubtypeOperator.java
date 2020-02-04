// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.util;

import static wyil.lang.WyilFile.*;

import java.util.*;

import wybs.lang.SyntacticException;
import wybs.lang.SyntacticHeap;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wycc.util.ArrayUtils;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Type;

/**
 * <p>
 * Provides default implementations for <code>isSubtype</code> and
 * <code>bind</code>. The intention is that these be overriden to provide
 * different variants (e.g. relaxed subtype operators, etc).
 * </p>
 * <p>
 * <b>(Subtyping)</b> The default subtype operator checks whether one type is a
 * <i>strict subtype</i> of another. Unlike other subtype operators, this takes
 * into account the invariants on types. Consider these two types:
 *
 * <pre>
 * type nat is (int x) where x >= 0
 * type pos is (nat x) where x > 0
 * type tan is (int x) where x >= 0
 * </pre>
 *
 * In this case, we have <code>nat <: int</code> since <code>int</code> is
 * explicitly included in the definition of <code>nat</code>. Observe that this
 * applies transitively and, hence, <code>pos <: nat</code>. But, it does not
 * follow that <code>nat <: int</code> and, likewise, that
 * <code>pos <: nat</code>. Likewise, <code>nat <: tan</code> does not follow
 * (despite this being actually true) since we cannot reason about invariants.
 * </p>
 * <p>
 * <b>(Binding)</b> An important task is computing a "binding" between a
 * function, method or property declaration and a given set of concrete
 * arguments types. For example, consider:
 * </p>
 *
 * <pre>
 * template<T>
 * function get(T[] items, int i) -> T:
 *    return items[i]
 *
 *  function f(int[] items) -> int:
 *     return get(items,0)
 * </pre>
 *
 * <p>
 * At the point of the invocation for <code>get()</code> we must resolve the
 * declared type <code>function(T[],int)->(T)</code> against the declared
 * parameter types <code>(int[],int)</code>, yielding a binding
 * <code>T=int</code>.
 * </p>
 * <p>
 * Computing the binding between two types is non-trivial in Whiley. In addition
 * to template arguments (as above), we must handle lifetime arguments. For
 * example:
 * </p>
 *
 * <pre>
 * method <a> m(&a:int x) -> int:
 *    return *a
 *
 * ...
 *   &this:int ptr = new 1
 *   return m(ptr)
 * </pre>
 * <p>
 * At the invocation to <code>m()</code>, we need to infer the binding
 * <code>a=this</code>. A major challenge is the presence of union types. For
 * example, consider this binding problem:
 * </p>
 *
 * <pre>
 * template<S,T>
 * function f(S x, S|T y) -> S|T:
 *    return y
 *
 * function g(int p, bool|int q) -> (bool|int r):
 *    return f(p,q)
 * </pre>
 * <p>
 * At the invocation to <code>f</code> we must generate the binding
 * <code>S=int,T=bool</code>. When binding <code>bool|int</code> against
 * <code>S|T</code> we need to consider both cases where
 * <code>S=bool,T=int</code> and <code>S=int,T=bool</code>. Otherwise, we cannot
 * be sure to consider the right combination.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public abstract class AbstractSubtypeOperator implements SubtypeOperator {

	@Override
	public boolean isSatisfiableSubtype(Type t1, Type t2, LifetimeRelation lifetimes) {
		AbstractConstraints constraints = isSubtype(t1,t2,lifetimes);
		return !constraints.isEmpty();
	}

	@Override
	public AbstractConstraints isSubtype(Type t1, Type t2, LifetimeRelation lifetimes) {
		return isSubtype(t1,t2,lifetimes,null);
	}

	@Override
	public boolean isEmpty(QualifiedName nid, Type type) {
		return isContractive(nid, type, null);
	}

	// ===========================================================================
	// Contractivity
	// ===========================================================================

	/**
	 * Provides a helper implementation for isContractive.
	 * @param name
	 * @param type
	 * @param visited
	 * @return
	 */
	static boolean isContractive(QualifiedName name, Type type, HashSet<QualifiedName> visited) {
		switch (type.getOpcode()) {
		case TYPE_void:
		case TYPE_null:
		case TYPE_bool:
		case TYPE_int:
		case TYPE_property:
		case TYPE_byte:
		case TYPE_universal:
		case TYPE_unknown:
		case TYPE_function:
		case TYPE_method:
			return true;
		case TYPE_staticreference:
		case TYPE_reference: {
			Type.Reference t = (Type.Reference) type;
			return isContractive(name,t.getElement(),visited);
		}
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			return isContractive(name,t.getElement(),visited);
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> fields = t.getFields();
			for(int i=0;i!=fields.size();++i) {
				if(isContractive(name,fields.get(i).getType(),visited)) {
					return true;
				}
			}
			return false;
		}
		case TYPE_union: {
			Type.Union c = (Type.Union) type;
			for (int i = 0; i != c.size(); ++i) {
				if (isContractive(name, c.get(i), visited)) {
					return true;
				}
			}
			return false;
		}
		default:
		case TYPE_nominal:
			Type.Nominal n = (Type.Nominal) type;
			Decl.Link<Decl.Type> link = n.getLink();
			Decl.Type decl = link.getTarget();
			QualifiedName nid = decl.getQualifiedName();
			if (nid.equals(name)) {
				// We have identified a non-contractive type.
				return false;
			} else if (visited != null && visited.contains(nid)) {
				// NOTE: this identifies a type (other than the one we are looking for) which is
				// not contractive. It may seem odd then, that we pretend it is in fact
				// contractive. The reason for this is simply that we cannot tell here with the
				// type we are interested in is contractive or not. Thus, to improve the error
				// messages reported we ignore this non-contractiveness here (since we know
				// it'll be caught down the track anyway).
				return true;
			} else {
				// Lazily construct the visited set as, in the vast majority of cases, this is
				// never required.
				visited = new HashSet<>();
				visited.add(nid);
			}
			return isContractive(name, decl.getType(), visited);
		}
	}

	// ===========================================================================
	// Subtyping
	// ===========================================================================

	/**
	 * A subtype operator aimed at checking whether one type is a <i>strict
	 * subtype</i> of another. Unlike other subtype operators, this takes into
	 * account the invariants on types. Consider these two types:
	 *
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * type pos is (nat x) where x > 0
	 * type tan is (int x) where x >= 0
	 * </pre>
	 *
	 * In this case, we have <code>nat <: int</code> since <code>int</code> is
	 * explicitly included in the definition of <code>nat</code>. Observe that this
	 * applies transitively and, hence, <code>pos <: nat</code>. But, it does not
	 * follow that <code>nat <: int</code> and, likewise, that
	 * <code>pos <: nat</code>. Likewise, <code>nat <: tan</code> does not follow
	 * (despite this being actually true) since we cannot reason about invariants.
	 *
	 * @author David J. Pearce
	 *
	 */
	protected AbstractConstraints isSubtype(Type t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		// FIXME: only need to check for coinductive case when both types are recursive.
		// If either is not recursive, then are guaranteed to eventually terminate.
		if (cache != null && cache.get(t1, t2)) {
			return TOP;
		} else if (cache == null) {
			// Lazily construct cache.
			cache = new BinaryRelation.HashSet<>();
		}
		cache.set(t1, t2, true);
		// Normalise opcodes to align based on class
		int t1_opcode = normalise(t1.getOpcode());
		int t2_opcode = normalise(t2.getOpcode());
		//
		if (t1_opcode == t2_opcode) {
			switch (t1_opcode) {
			case TYPE_any:
			case TYPE_void:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				return TOP;
			case TYPE_array:
				return isSubtype((Type.Array) t1, (Type.Array) t2, lifetimes, cache);
			case TYPE_tuple:
				return isSubtype((Type.Tuple) t1, (Type.Tuple)t2, lifetimes, cache);
			case TYPE_record:
				return isSubtype((Type.Record) t1, (Type.Record)t2, lifetimes, cache);
			case TYPE_nominal:
				return isSubtype((Type.Nominal) t1, (Type.Nominal)t2, lifetimes, cache);
			case TYPE_union:
				return isSubtype(t1, (Type.Union) t2, lifetimes, cache);
			case TYPE_staticreference:
			case TYPE_reference:
				return isSubtype((Type.Reference) t1, (Type.Reference) t2, lifetimes, cache);
			case TYPE_method:
			case TYPE_function:
			case TYPE_property:
				return isSubtype((Type.Callable) t1, (Type.Callable) t2, lifetimes, cache);
			case TYPE_universal:
				return isSubtype((Type.UniversalVariable) t1, (Type.UniversalVariable) t2, lifetimes, cache);
			case TYPE_existential:
				return isSubtype(t1, (Type.ExistentialVariable) t2, lifetimes, cache);
			default:
				throw new IllegalArgumentException("unexpected type encountered: " + t1);
			}
		} else if(t1_opcode == TYPE_any || t2_opcode == TYPE_void) {
			return TOP;
		} else if (t2_opcode == TYPE_existential) {
			return isSubtype(t1, (Type.ExistentialVariable) t2, lifetimes, cache);
		} else if (t2_opcode == TYPE_nominal) {
			return isSubtype(t1, (Type.Nominal) t2, lifetimes, cache);
		} else if (t2_opcode == TYPE_union) {
			return isSubtype(t1, (Type.Union) t2, lifetimes, cache);
		} else if (t1_opcode == TYPE_union) {
			return isSubtype((Type.Union) t1, t2, lifetimes, cache);
		} else if (t1_opcode == TYPE_nominal) {
			return isSubtype((Type.Nominal) t1, (Type.Atom) t2, lifetimes, cache);
		} else if (t1_opcode == TYPE_existential) {
				return isSubtype((Type.ExistentialVariable) t1, (Type.Atom) t2, lifetimes, cache);
		} else {
			// Nothing else works except void
			return BOTTOM;
		}
	}

	protected AbstractConstraints isSubtype(Type.Array t1, Type.Array t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		return isSubtype(t1.getElement(), t2.getElement(), lifetimes, cache);
	}

	protected AbstractConstraints isSubtype(Type.Tuple t1, Type.Tuple t2, LifetimeRelation lifetimes,
			BinaryRelation<Type> cache) {
		AbstractConstraints constraints = TOP;
		// Check elements one-by-one
		for (int i = 0; i != t1.size(); ++i) {
			AbstractConstraints ith = isSubtype(t1.get(i), t2.get(i), lifetimes, cache);
			if (ith == null || constraints == null) {
				return BOTTOM;
			} else {
				constraints = constraints.intersect(ith,lifetimes);
			}
		}
		// Done
		return constraints;
	}

	protected AbstractConstraints isSubtype(Type.Record t1, Type.Record t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		AbstractConstraints constraints = TOP;
		Tuple<Type.Field> t1_fields = t1.getFields();
		Tuple<Type.Field> t2_fields = t2.getFields();
		// Sanity check number of fields are reasonable.
		if(t1_fields.size() != t2_fields.size()) {
			return BOTTOM;
		} else if(t1.isOpen() != t2.isOpen()) {
			return BOTTOM;
		}
		// Check fields one-by-one.
		for (int i = 0; i != t1_fields.size(); ++i) {
			Type.Field f1 = t1_fields.get(i);
			Type.Field f2 = t2_fields.get(i);
			if (!f1.getName().equals(f2.getName())) {
				// Fields have differing names
				return BOTTOM;
			}
			// Check whether fields are subtypes or not
			AbstractConstraints other = isSubtype(f1.getType(), f2.getType(), lifetimes, cache);
			if(other == null || constraints == null) {
				return BOTTOM;
			} else {
				constraints = constraints.intersect(other,lifetimes);
			}
		}
		// Done
		return constraints;
	}

	protected AbstractConstraints isSubtype(Type.Reference t1, Type.Reference t2, LifetimeRelation lifetimes,
			BinaryRelation<Type> cache) {
		String l1 = extractLifetime(t1);
		String l2 = extractLifetime(t2);
		//
		if (!lifetimes.isWithin(l1, l2)) {
			// Definitely unsafe
			return BOTTOM;
		}
		AbstractConstraints first = areEquivalent(t1.getElement(), t2.getElement(), lifetimes, cache);
		AbstractConstraints second = isWidthSubtype(t1.getElement(), t2.getElement(), lifetimes, cache);
		// Join them together
		return first.union(second);
	}

	protected AbstractConstraints isWidthSubtype(Type t1, Type t2, LifetimeRelation lifetimes,
			BinaryRelation<Type> cache) {
		// NOTE: this method could be significantly improved by allowing recursive width
		// subtyping.
		if (t1 instanceof Type.Nominal) {
			Type.Nominal n1 = (Type.Nominal) t1;
			return isWidthSubtype(n1.getConcreteType(), t2, lifetimes, cache);
		} else if (t2 instanceof Type.Nominal) {
			Type.Nominal n2 = (Type.Nominal) t2;
			return isWidthSubtype(t1, n2.getConcreteType(), lifetimes, cache);
		} else if(t1 instanceof Type.Record && t2 instanceof Type.Record) {
			AbstractConstraints constraints = TOP;
			Type.Record r1 = (Type.Record) t1;
			Type.Record r2 = (Type.Record) t2;
			Tuple<Type.Field> r1_fields = r1.getFields();
			Tuple<Type.Field> r2_fields = r2.getFields();
			if(r1.isOpen() && r1_fields.size() <= r2_fields.size()) {
				for(int i=0;i!=r1_fields.size();++i) {
					Type.Field f1 = r1_fields.get(i);
					Type.Field f2 = r2_fields.get(i);
					if (!f1.getName().equals(f2.getName())) {
						// Fields have differing names
						return BOTTOM;
					}
					AbstractConstraints other = areEquivalent(f1.getType(), f2.getType(), lifetimes, cache);
					constraints = constraints.intersect(other,lifetimes);
				}
				return constraints;
			}
		}
		return BOTTOM;
	}

	protected AbstractConstraints isSubtype(Type.Callable t1, Type.Callable t2, LifetimeRelation lifetimes,
			BinaryRelation<Type> cache) {
		Type t1_params = t1.getParameter();
		Type t2_params = t2.getParameter();
		Type t1_return = t1.getReturn();
		Type t2_return = t2.getReturn();
		// Eliminate easy cases first
		if (t1.getOpcode() != t2.getOpcode()) {
			return BOTTOM;
		}
		// Check parameters
		AbstractConstraints c_params = areEquivalent(t1_params, t2_params, lifetimes, cache);
		AbstractConstraints c_returns = areEquivalent(t1_return, t2_return, lifetimes, cache);
		//
		if (t1 instanceof Type.Method) {
			Type.Method m1 = (Type.Method) t1;
			Type.Method m2 = (Type.Method) t2;
			Tuple<Identifier> m1_lifetimes = m1.getLifetimeParameters();
			Tuple<Identifier> m2_lifetimes = m2.getLifetimeParameters();
			Tuple<Identifier> m1_captured = m1.getCapturedLifetimes();
			Tuple<Identifier> m2_captured = m2.getCapturedLifetimes();
			// FIXME: it's not clear to me what we need to do here. I think one problem is
			// that we must normalise lifetimes somehow.
			if (m1_lifetimes.size() > 0 || m2_lifetimes.size() > 0) {
				throw new RuntimeException("must implement this!");
			} else if (m1_captured.size() > 0 || m2_captured.size() > 0) {
				throw new RuntimeException("must implement this!");
			}
		}
		// Done
		return c_params.intersect(c_returns,lifetimes);
	}

	protected AbstractConstraints isSubtype(Type.UniversalVariable t1, Type.UniversalVariable t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		if(t1.getOperand().equals(t2.getOperand())) {
			return TOP;
		} else {
			return BOTTOM;
		}
	}

	protected AbstractConstraints isSubtype(Type.ExistentialVariable t1, Type.Atom t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		return new AbstractConstraints(t1, t2);
	}

	protected AbstractConstraints isSubtype(Type t1, Type.ExistentialVariable t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		return new AbstractConstraints(t1, t2);
	}

	protected AbstractConstraints isSubtype(Type t1, Type.Union t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		AbstractConstraints constraints = TOP;
		for(int i=0;i!=t2.size();++i) {
			AbstractConstraints other = isSubtype(t1, t2.get(i), lifetimes, cache);
			constraints = constraints.intersect(other,lifetimes);
		}
		return constraints;
	}

	protected AbstractConstraints isSubtype(Type.Union t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		AbstractConstraints constraints = BOTTOM;
		for (int i = 0; i != t1.size(); ++i) {
			AbstractConstraints ith = isSubtype(t1.get(i), t2, lifetimes, cache);
			constraints = constraints.union(ith);
		}
		return constraints;
	}

	protected AbstractConstraints isSubtype(Type.Nominal t1, Type.Nominal t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		Decl.Type d1 = t1.getLink().getTarget();
		Decl.Type d2 = t2.getLink().getTarget();
		//
		Tuple<Expr> t1_invariant = d1.getInvariant();
		Tuple<Expr> t2_invariant = d2.getInvariant();
		// Dispatch easy cases
		if (d1 == d2) {
			// NOTE: at this point it is possible to only consider the template arguments
			// provided. However, doing this requires knowledge of the variance requirements
			// for each template parameter position. Such information is currently
			// unavailable though, in principle, could be inferred.
			Tuple<Template.Variable> template = d1.getTemplate();
			Tuple<Type> t1_params = t1.getParameters();
			Tuple<Type> t2_params = t2.getParameters();
			AbstractConstraints constraints = TOP;
			for (int i = 0; i != template.size(); ++i) {
				Template.Variable ith = template.get(i);
				Template.Variance v = ith.getVariance();
				Type t1_ith_param = t1_params.get(i);
				Type t2_ith_param = t2_params.get(i);
				// Apply constraints
				if (v == Template.Variance.COVARIANT || v == Template.Variance.INVARIANT) {
					constraints = constraints.intersect(isSubtype(t1_ith_param, t2_ith_param, lifetimes, cache),
							lifetimes);
				}
				if (v == Template.Variance.CONTRAVARIANT || v == Template.Variance.INVARIANT) {
					constraints = constraints.intersect(isSubtype(t2_ith_param, t1_ith_param, lifetimes, cache),
							lifetimes);
				}
			}
			return constraints;
		} else {
			boolean left = isSubtype(t1_invariant, t2_invariant);
			boolean right = isSubtype(t2_invariant, t1_invariant);
			if(left || right) {
				Type tt1 = left ? t1.getConcreteType() : t1;
				Type tt2 = right ? t2.getConcreteType() : t2;
				return isSubtype(tt1,tt2, lifetimes, cache);
			} else {
				return BOTTOM;
			}
		}
	}

	/**
	 * Check whether a nominal type is a subtype of an atom (i.e. not a nominal or
	 * union). For example, <code>int :> nat</code> or <code>{nat f} :> rec</code>.
	 * This is actually easy as an invariants on the nominal type can be ignored
	 * (since they already imply it is a subtype).
	 *
	 * @param t1
	 * @param t2
	 * @param lifetimes
	 * @return
	 */
	protected AbstractConstraints isSubtype(Type t1, Type.Nominal t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		return isSubtype(t1, t2.getConcreteType(), lifetimes, cache);
	}

	/**
	 * Check whether a nominal type is a supertype of an atom (i.e. not a nominal or
	 * union). For example, <code>int <: nat</code> or <code>{nat f} <: rec</code>.
	 * This is harder because the invariant cannot be reasoned about. In fact, the
	 * only case where this can hold true is when there is no invariant.
	 *
	 * @param t1
	 * @param t2
	 * @param lifetimes
	 * @return
	 */
	protected AbstractConstraints isSubtype(Type.Nominal t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		//
		Decl.Type d1 = t1.getLink().getTarget();
		Tuple<Expr> t1_invariant = d1.getInvariant();
		// Dispatch easy cases
		if (isSubtype(t1_invariant,EMPTY_INVARIANT)) {
			return isSubtype(t1.getConcreteType(), t2, lifetimes, cache);
		} else {
			return BOTTOM;
		}
	}

	/**
	 * Determine whether one invariant is a subtype of another. In other words, the
	 * subtype invariant implies the supertype invariant.
	 *
	 * @param lhs The "super" type
	 * @param rhs The "sub" type
	 * @return
	 */
	protected abstract boolean isSubtype(Tuple<Expr> lhs, Tuple<Expr> rhs);

	/**
	 * Determine whether two types are "equivalent" or not.
	 *
	 * @param t1
	 * @param t2
	 * @param lifetimes
	 * @return
	 */
	protected AbstractConstraints areEquivalent(Type t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		// NOTE: this is a temporary solution.
		AbstractConstraints left = isSubtype(t1, t2, lifetimes, cache);
		AbstractConstraints right = isSubtype(t2, t1, lifetimes, cache);
		//
		return left.intersect(right,lifetimes);
	}

	// ===============================================================================
	// Type Subtraction
	// ===============================================================================

	/**
	 * Subtract one type from another.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	@Override
	public Type subtract(Type t1, Type t2) {
		return subtract(t1,t2,new BinaryRelation.HashSet<>());
	}

	private Type subtract(Type t1, Type t2, BinaryRelation<Type> cache) {
		// FIXME: only need to check for coinductive case when both types are recursive.
		// If either is not recursive, then are guaranteed to eventually terminate.
		if (cache != null && cache.get(t1, t2)) {
			return Type.Void;
		} else if (cache == null) {
			// Lazily construct cache.
			cache = new BinaryRelation.HashSet<>();
		}
		cache.set(t1, t2, true);
		//
		int t1_opcode = t1.getOpcode();
		int t2_opcode = t2.getOpcode();
		//
		if(t1.equals(t2)) {
			// Easy case
			return Type.Void;
		} else if(t1_opcode == t2_opcode) {
			switch(t1_opcode) {
			case TYPE_void:
			case TYPE_null:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
				return Type.Void;
			case TYPE_array:
			case TYPE_staticreference:
			case TYPE_reference:
			case TYPE_method:
			case TYPE_function:
			case TYPE_property:
				return t1;
			case TYPE_record:
				return subtract((Type.Record) t1, (Type.Record) t2, cache);
			case TYPE_nominal:
				return subtract((Type.Nominal) t1, (Type.Nominal) t2, cache);
			case TYPE_union:
				return subtract((Type.Union) t1, t2, cache);
			default:
				throw new IllegalArgumentException("unexpected type encountered: " + t1);
			}
		} else if (t2_opcode == TYPE_union) {
			return subtract(t1, (Type.Union) t2, cache);
		} else if (t1_opcode == TYPE_union) {
			return subtract((Type.Union) t1, t2, cache);
		} else if (t2_opcode == TYPE_nominal) {
			return subtract(t1, (Type.Nominal) t2, cache);
		} else if (t1_opcode == TYPE_nominal) {
			return subtract((Type.Nominal) t1, t2, cache);
		} else {
			return t1;
		}
	}

	/**
	 * Subtraction of records is possible in a limited number of cases.
	 *
	 * @param t1
	 * @param t2
	 * @return
	 */
	public Type subtract(Type.Record t1, Type.Record t2, BinaryRelation<Type> cache) {
		Tuple<Type.Field> t1_fields = t1.getFields();
		Tuple<Type.Field> t2_fields = t2.getFields();
		if(t1_fields.size() != t2_fields.size() || t1.isOpen() || t1.isOpen()) {
			// Don't attempt anything
			return t1;
		}
		Type.Field[] r_fields = new Type.Field[t1_fields.size()];
		boolean found = false;
		for(int i=0;i!=t1_fields.size();++i) {
			Type.Field f1 = t1_fields.get(i);
			Type.Field f2 = t2_fields.get(i);
			if(!f1.getName().equals(f2.getName())) {
				// Give up
				return t1;
			}
			if(!f1.getType().equals(f2.getType())) {
				if(found) {
					return t1;
				} else {
					found = true;
					Type tmp = subtract(f1.getType(), f2.getType(), cache);
					r_fields[i] = new Type.Field(f1.getName(), tmp);
				}
			} else {
				r_fields[i] = f1;
			}
		}
		return new Type.Record(false,new Tuple<>(r_fields));
	}

	public Type subtract(Type.Nominal t1, Type.Nominal t2, BinaryRelation<Type> cache) {
		//
		Decl.Type d1 = t1.getLink().getTarget();
		// NOTE: the following invariant check is essentially something akin to
		// determining whether or not this is a union.
		if (d1.getInvariant().size() == 0) {
			return subtract(t1.getConcreteType(), (Type) t2, cache);
		} else {
			return t1;
		}
	}

	public Type subtract(Type t1, Type.Nominal t2, BinaryRelation<Type> cache) {
		Decl.Type d2 = t2.getLink().getTarget();
		// NOTE: the following invariant check is essentially something akin to
		// determining whether or not this is a union.
		if (d2.getInvariant().size() == 0) {
			return subtract(t1,t2.getConcreteType(),cache);
		} else {
			return t1;
		}
	}

	public Type subtract(Type.Nominal t1, Type t2, BinaryRelation<Type> cache) {
		Decl.Type d1 = t1.getLink().getTarget();
		// NOTE: the following invariant check is essentially something akin to
		// determining whether or not this is a union.
		if (d1.getInvariant().size() == 0) {
			return subtract(t1.getConcreteType(), t2, cache);
		} else {
			return t1;
		}
	}

	public Type subtract(Type t1, Type.Union t2, BinaryRelation<Type> cache) {
		for (int i = 0; i != t2.size(); ++i) {
			t1 = subtract(t1, t2.get(i), cache);
		}
		return t1;
	}
	public Type subtract(Type.Union t1, Type t2, BinaryRelation<Type> cache) {
		Type[] types = new Type[t1.size()];
		for(int i=0;i!=t1.size();++i) {
			types[i] = subtract(t1.get(i),t2,cache);
		}
		// Remove any selected cases
		return Type.Union.create(ArrayUtils.removeAll(types, Type.Void));
	}

	// ===============================================================================
	// AbstractConstraints
	// ===============================================================================

	/**
	 * An empty solution to a set of concrete constraints
	 */
	private final ConcreteSolution EMPTY_CONCRETE_SOLUTION = new ConcreteSolution();
	/**
	 * A simple constant to avoid unnecessary allocations.
	 */
	private final ConcreteSolution[] EMPTY_CONCRETE_SOLUTIONS = new ConcreteSolution[0];
	/**
	 * An empty set of (potentially symbolic) constraints.
	 */
	private final AbstractSolution EMPTY_SYMBOLIC_SOLUTION = new AbstractSolution();
	/**
	 * A simple constant to avoid unnecessary allocations.
	 */
	private final AbstractSolution[] EMPTY_SYMBOLIC_SOLUTIONS = new AbstractSolution[0];
	/**
	 * A minimal implementation of the constraints interface.
	 */
	public final AbstractConstraints TOP = new AbstractConstraints(true);
	/**
	 * The empty constraint set which is, by construction, invalid.
	 */
	public final AbstractConstraints BOTTOM = new AbstractConstraints(false);

	/**
	 * Represents a set of <i>satisfiable</i> subtyping constraints. Satisfiability
	 * is understood through the presence of concrete solutions for the constraints.
	 * When constraint sets are intersected, new constraints are added which may
	 * invalidate active solutions. When the number of active solutions reaches
	 * zero, the entire constraint set is said to be <i>unsatisfiable</i> (which may
	 * mean, for example, that the original source program is untypable).
	 *
	 * @author David J. Pearce
	 *
	 */
	protected class AbstractConstraints implements SubtypeOperator.Constraints {
		private final AbstractSolution[] rows;

		public AbstractConstraints(boolean top) {
			this.rows = top ? new AbstractSolution[] { EMPTY_SYMBOLIC_SOLUTION }
					: EMPTY_SYMBOLIC_SOLUTIONS;
		}

		public AbstractConstraints(Type.ExistentialVariable lhs, Type.Atom rhs) {
			this.rows = new AbstractSolution[] { new AbstractSolution(lhs,rhs) };
		}
		public AbstractConstraints(Type lhs, Type.ExistentialVariable rhs) {
			this.rows = new AbstractSolution[] { new AbstractSolution(lhs,rhs) };
		}
		private AbstractConstraints(AbstractSolution[] rows) {
			this.rows = rows;
		}

		@Override
		public boolean isEmpty() {
			return rows.length == 0;
		}

		@Override
		public int size() {
			return rows.length;
		}

		@Override
		public AbstractSolution get(int i) {
			return rows[i];
		}

		/**
		 * Intersect this constraint set with another. This essentially determines the
		 * cross-product of rows in the two constraint sets.
		 *
		 * @param other
		 * @return
		 */
		public AbstractConstraints intersect(AbstractConstraints other, LifetimeRelation lifetimes) {
			if(this == TOP) {
				return other;
			} else if(other == TOP) {
				return this;
			}
			// NOTE: at this point, other could be top
			final int n = rows.length;
			final int m = other.rows.length;
			ArrayList<Solution> nrows = new ArrayList<>();
			for (int i = 0; i != n; ++i) {
				AbstractSolution ith = rows[i];
				for (int j = 0; j != m; ++j) {
					AbstractSolution jth = other.rows[j];
					ArrayUtils.addAll(ith.intersect(jth,lifetimes), nrows);
				}
			}
			// Sanity check what we have left
			if (nrows.size() == 0) {
				return BOTTOM;
			} else {
				// NOTE: could optimise for common case where result equivalent to this.
				return new AbstractConstraints(nrows.toArray(new AbstractSolution[nrows.size()]));
			}
		}

		public AbstractConstraints union(AbstractConstraints other) {
			if(this == BOTTOM) {
				return other;
			} else if(other == BOTTOM) {
				return this;
			}
			final int n = rows.length;
			final int m = other.rows.length;
			AbstractSolution[] nrows = new AbstractSolution[n + m];
			System.arraycopy(rows, 0, nrows, 0, n);
			System.arraycopy(other.rows, 0, nrows, n, m);
			nrows = ArrayUtils.removeDuplicates(nrows);
			return new AbstractConstraints(nrows);
		}

		@Override
		public Type[][] solve(int n, LifetimeRelation lifetimes) {
			// First compute all solutions
			Type[][] candidates = new Type[rows.length][];
			for (int i = 0; i != rows.length; ++i) {
				candidates[i] = rows[i].solve(n);
			}
			// Second identify clear winners (whilst ignoring nulls)
			for (int i = 0; i != candidates.length; ++i) {
				Type[] ith = candidates[i];
				if (ith != null) {
					for (int j = i + 1; j != candidates.length; ++j) {
						Type[] jth = candidates[j];
						if (jth == null) {
							continue;
						} else if (isSubsumedBy(ith, jth, lifetimes)) {
							// ith subsumed by jth
							candidates[i] = null;
							break;
						} else if (isSubsumedBy(jth, ith, lifetimes)) {
							// jth subsumed by ith
							candidates[j] = null;
							continue;
						}
					}
				}
			}
			// Finally, remove any losers
			return ArrayUtils.removeAll(candidates, null);
		}

		@Override
		public String toString() {
			if (rows.length == 0) {
				return "⊥";
			} else {
				String r = "";
				for (int i = 0; i != rows.length; ++i) {
					if(i != 0) {
						r += ",";
					}
					r += rows[i];
				}
				return r;
			}
		}

		/**
		 * Check whether or not lhs is subsumed by the rhs.
		 *
		 * @param lhs
		 * @param rhs
		 * @param lifetimes
		 * @return
		 */
		private boolean isSubsumedBy(Type[] lhs, Type[] rhs, LifetimeRelation lifetimes) {
			for (int i = 0; i != lhs.length; ++i) {
				if (!isSatisfiableSubtype(lhs[i], rhs[i], lifetimes)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Represents a set of constraints of the form <code>? :> T</code> or
	 * <code>T :> ?</code> and a valid solution. <i>symbolic constraints</i> are
	 * those where <code>T</code> itself contains existential variables. In
	 * contrast, <i>concrete constraints</i> are those where <code>T</code> itself
	 * is concrete. In the current implementation, symbolic constraints are kept as
	 * is whilst concrete constraints are immediately applied to the active
	 * solution.
	 *
	 * @author David J. Pearce
	 *
	 */
	private class AbstractSolution implements AbstractConstraints.Solution {
		private final ConcreteSolution solution;
		private final SymbolicConstraint[] constraints;

		public AbstractSolution() {
			this.solution = EMPTY_CONCRETE_SOLUTION;
			this.constraints = new SymbolicConstraint[0];
		}

		public AbstractSolution(Type.ExistentialVariable var, Type.Atom lower) {
			if (isConcrete(lower)) {
				this.solution = new ConcreteSolution(var.get(), Type.Any, lower);
				this.constraints = new SymbolicConstraint[0];
			} else {
				this.solution = EMPTY_CONCRETE_SOLUTION;
				this.constraints = new SymbolicConstraint[] { new SymbolicConstraint(var, lower) };
			}
		}

		public AbstractSolution(Type upper, Type.ExistentialVariable var) {
			if (isConcrete(upper)) {
				this.solution = new ConcreteSolution(var.get(), upper, Type.Void);
				this.constraints = new SymbolicConstraint[0];
			} else {
				this.solution = EMPTY_CONCRETE_SOLUTION;
				this.constraints = new SymbolicConstraint[] { new SymbolicConstraint(upper, var) };
			}
		}

		private AbstractSolution(ConcreteSolution solution, SymbolicConstraint[] constraints) {
			this.solution = solution;
			this.constraints = constraints;
		}

		private AbstractSolution[] intersect(AbstractSolution row, LifetimeRelation lifetimes) {
			ConcreteSolution s = solution.intersect(row.solution, lifetimes);
			SymbolicConstraint[] c = union(constraints, row.constraints);
			if (s == solution && c == constraints) {
				// Handle common case
				return new AbstractSolution[] { this };
			} else if (s != null) {
				// Apply closure over these constraints
				ConcreteSolution[] solutions = close(s, c, lifetimes);
				AbstractSolution[] rows = new AbstractSolution[solutions.length];
				for (int i = 0; i != rows.length; ++i) {
					rows[i] = new AbstractSolution(solutions[i], c);
				}
				return rows;
			} else {
				return EMPTY_SYMBOLIC_SOLUTIONS;
			}
		}

		@Override
		public int hashCode() {
			return solution.hashCode() ^ Arrays.hashCode(constraints);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof AbstractSolution) {
				AbstractSolution r = (AbstractSolution) o;
				return solution.equals(r.solution) && Arrays.equals(constraints, r.constraints);
			} else {
				return false;
			}
		}

		public Type[] solve(int n) {
			Type[] solutions = new Type[n];
			for (int i = 0; i != n; ++i) {
				Type upper = solution.ceil(i);
				Type lower = solution.floor(i);
				// Check whether a solution was found or not
				if (upper instanceof Type.Any && lower instanceof Type.Void) {
					return null;
				} else if (lower instanceof Type.Void) {
					solutions[i] = upper;
				} else {
					solutions[i] = lower;
				}
			}
			return solutions;
		}

		@Override
		public String toString() {
			String r = "";
			for (SymbolicConstraint constraint : constraints) {
				if (!r.equals("")) {
					r += ",";
				}
				r += constraint.first() + " :> " + constraint.second();
			}
			return "{" + r + "}" + solution;
		}

		private SymbolicConstraint[] union(SymbolicConstraint[] lhs, SymbolicConstraint[] rhs) {
			SymbolicConstraint[] cs = ArrayUtils.append(lhs, rhs);
			// Remove any duplicate items
			cs = ArrayUtils.removeDuplicates(cs);
			// NOTE: we could do better here by maintaining constraints in sorted order or
			// something.
			if (cs.length == lhs.length) {
				return lhs;
			} else if (cs.length == rhs.length) {
				return rhs;
			} else {
				return cs;
			}
		}
	}

	/**
	 * A simple implementation of a single symboling subtyping constraint.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class SymbolicConstraint extends wycc.util.Pair<Type, Type> {
		public SymbolicConstraint(Type lhs, Type rhs) {
			super(lhs, rhs);
		}

		@Override
		public String toString() {
			return first() + ":>" + second();
		}
	}

	/**
	 * Contains current best solution for a given typing problem. Observe that every
	 * tpe within the lower and upper bounds are concrete (i.e. do not contain
	 * existentials).
	 *
	 * @author David J. Pearce
	 *
	 */
	private class ConcreteSolution {
		private final Type[] upperBounds;
		private final Type[] lowerBounds;

		public ConcreteSolution() {
			this.upperBounds = new Type[0];
			this.lowerBounds = new Type[0];
		}

		public ConcreteSolution(int var, Type upper, Type lower) {
			this.upperBounds = fill(var+1,Type.Any);
			this.lowerBounds = fill(var+1,Type.Void);
			this.upperBounds[var] = upper;
			this.lowerBounds[var] = lower;
		}

		private ConcreteSolution(Type[] upperBounds, Type[] lowerBounds) {
			this.upperBounds = upperBounds;
			this.lowerBounds = lowerBounds;
		}

		public Type floor(int i) {
			if(i >= lowerBounds.length) {
				return Type.Void;
			} else {
				return lowerBounds[i];
			}
		}

		public Type ceil(int i) {
			if(i >= upperBounds.length) {
				return Type.Any;
			} else {
				return upperBounds[i];
			}
		}

		public ConcreteSolution intersect(ConcreteSolution other, LifetimeRelation lifetimes) {
			final int n = lowerBounds.length;
			final int m = other.lowerBounds.length;
			if (n < m) {
				return other.intersect(this, lifetimes);
			} else {
				Type[] nLowerBounds = lub(lowerBounds,other.lowerBounds,lifetimes);
				Type[] nUpperBounds = glb(upperBounds,other.upperBounds,lifetimes);
				//
				if (nLowerBounds == lowerBounds && nUpperBounds == upperBounds) {
					return this;
				} else {
					// Sanity check new bounds
					for (int i = 0; i != m; ++i) {
						Type lower = nLowerBounds[i];
						Type upper = nUpperBounds[i];
						if (!isSatisfiableSubtype(upper, lower, lifetimes)
								// Upper bound cannot be void
								|| isSatisfiableSubtype(Type.Void, upper, lifetimes)
								// Lower bound cannot be any
								|| isSatisfiableSubtype(lower, Type.Any, lifetimes)) {
							// Solution is invalid
							return null;
						}
					}
					//
					return new ConcreteSolution(nUpperBounds,nLowerBounds);
				}
			}
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof ConcreteSolution) {
				ConcreteSolution s = (ConcreteSolution) o;
				return Arrays.equals(lowerBounds, s.lowerBounds) && Arrays.equals(upperBounds, s.upperBounds);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(lowerBounds) ^ Arrays.hashCode(upperBounds);
		}

		@Override
		public String toString() {
			String r = "[";
			for(int i=0;i!=lowerBounds.length;++i) {
				if(i != 0) {
					r += ";";
				}
				Type lower = lowerBounds[i];
				Type upper = upperBounds[i];
				if(!(upper instanceof Type.Any)) {
					r += upper + " :> ";
				}
				r += "?" + i;
				if(!(lower instanceof Type.Void)) {
					r += " :> " + lower;
				}
			}
			return r + "]";
		}

		private Type[] lub(Type[] lhs, Type[] rhs, LifetimeRelation lifetimes) {
			final int n = lhs.length;
			final int m = rhs.length;
			Type[] ts = lhs;
			for (int i = 0; i != m; ++i) {
				Type l = lhs[i];
				Type r = rhs[i];
				if (!isSatisfiableSubtype(l, r, lifetimes)) {
					// Refined already subsumed
					if(ts == lhs) {
						ts = Arrays.copyOf(lhs, n);
					}
					ts[i] = AbstractSubtypeOperator.lub(l,r);
				}
			}
			return ts;
		}

		private Type[] glb(Type[] lhs, Type[] rhs, LifetimeRelation lifetimes) {
			final int n = lhs.length;
			final int m = rhs.length;
			Type[] ts = lhs;
			for (int i = 0; i != m; ++i) {
				Type l = lhs[i];
				Type r = rhs[i];
				if (!isSatisfiableSubtype(r, l, lifetimes)) {
					// Refined already subsumed
					if (ts == lhs) {
						ts = Arrays.copyOf(lhs, n);
					}
					ts[i] = AbstractSubtypeOperator.glb(l, r);
				}
			}
			return ts;
		}
	}

	// ===============================================================================
	// isConcrete
	// ===============================================================================

	/**
	 * Check whether a given type is "concrete" or not. That is, whether or not it
	 * contains a nested existential type variable. For example, the type
	 * <code>int</code> does not contain an existential variable! In contrast, the
	 * type <code>{?1 f}</code> does. This method performs a fairly straightforward
	 * recursive descent through the type tree search for existentiuals.
	 *
	 * @param type The type being tested for the presence of existential variables.
	 * @return
	 */
	public static boolean isConcrete(Type type) {
		switch (type.getOpcode()) {
		case TYPE_any:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
		case TYPE_null:
		case TYPE_void:
		case TYPE_universal:
			return true;
		case TYPE_existential:
			return false;
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			return isConcrete(t.getElement());
		}
		case TYPE_staticreference:
		case TYPE_reference: {
			Type.Reference t = (Type.Reference) type;
			return isConcrete(t.getElement());
		}
		case TYPE_function:
		case TYPE_method:
		case TYPE_property: {
			Type.Callable t = (Type.Callable) type;
			return isConcrete(t.getParameter()) && isConcrete(t.getReturn());
		}
		case TYPE_nominal: {
			Type.Nominal t = (Type.Nominal) type;
			return isConcrete(t.getParameters());
		}
		case TYPE_tuple: {
			Type.Tuple t = (Type.Tuple) type;
			return isConcrete(t.getAll());
		}
		case TYPE_union: {
			Type.Union t = (Type.Union) type;
			return isConcrete(t.getAll());
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> fields = t.getFields();
			for(int i=0;i!=fields.size();++i) {
				if(!isConcrete(fields.get(i).getType())) {
					return false;
				}
			}
			return true;
		}
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	private static boolean isConcrete(Tuple<Type> types) {
		for(int i=0;i!=types.size();++i) {
			if(!isConcrete(types.get(i))) {
				return false;
			}
		}
		return true;
	}

	private static boolean isConcrete(Type[] types) {
		for(int i=0;i!=types.length;++i) {
			if(!isConcrete(types[i])) {
				return false;
			}
		}
		return true;
	}

	// ===============================================================================
	// Constraint Closure
	// ===============================================================================

	/**
	 * <p>
	 * Apply a given set of symbolic constraints to a given solution until a
	 * fixpoint is reached. For example, consider this set of constraints and
	 * solution:
	 * </p>
	 *
	 * <pre>
	 * { #0 :> #1 }[int|bool :> #0; #1 :> int]
	 * </pre>
	 *
	 * <p>
	 * For each constraint, there are two directions of flow: <i>upwards</i> and
	 * <i>downwards</i>. In this case, <code>int|bool</code> flows downwards from
	 * <code>#0</code> to <code>#1</code>. Likewise, <code>int</code> flows upwards
	 * from <code>#1</code> to <code>#0</code>.
	 * </p>
	 *
	 * <p>
	 * To implement flow in a given direction we employ substitution. For example,
	 * to flow downwards through <code>#0 :> #1</code> we substitute <code>#0</code>
	 * for its current upper bound (i.e. <code>int|bool</code>). We then employ the
	 * subtype operator to generate appropriate constraints (or not). In this case,
	 * after substitution we'd have <code>int|bool :> #1</code> which, in fact, is
	 * the constraint that will be reported.
	 * </p>
	 *
	 * @param solution
	 * @param constraints
	 * @param subtyping
	 * @param lifetimes
	 * @return
	 */
	public ConcreteSolution[] close(ConcreteSolution solution, SymbolicConstraint[] constraints,
			LifetimeRelation lifetimes) {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (int i = 0; i != constraints.length; ++i) {
				if(solution == null) {
					// NOTE: we can get here either from a previous iteration which invalidated this
					// solution, or from a prior invocation with solution being null on entry.
					return EMPTY_CONCRETE_SOLUTIONS;
				} else {
					final ConcreteSolution s = solution;
					SymbolicConstraint ith = constraints[i];
					Type upper = ith.first();
					Type lower = ith.second();
					Type cUpper = substitute(upper, solution, true);
					Type cLower = substitute(lower, solution, false);
					// Generate new constraints
					AbstractConstraints left = isSubtype(cUpper, lower, lifetimes);
					AbstractConstraints right = isSubtype(upper, cLower, lifetimes);
					// Combine constraints
					AbstractConstraints cs = left.intersect(right,lifetimes);
					final int n = cs.size();
					//
					switch(n) {
					case 0:
						// Zero solutions arising
						return EMPTY_CONCRETE_SOLUTIONS;
					case 1:
						// NOTE: ignoring constraints from solution is reasonable as, by construction,
						// there cannot be any of them.
						solution = solution.intersect(cs.get(0).solution, lifetimes);
						break;
					default: {
						// Multiple solutions arising, therefore we have to "split" our results
						// accordingly.
						ArrayList<ConcreteSolution> solutions = new ArrayList<>();
						for(int j=0;j!=n;++j) {
							ConcreteSolution jthSol = solution.intersect(cs.get(j).solution, lifetimes);
							// NOTE: ignoring constraints from jthSol is reasonable as, by construction,
							// there cannot be any of them.
							ArrayUtils.addAll(close(jthSol,constraints,lifetimes), solutions);
						}
						// FIXME: can we have duplicates here?
						return solutions.toArray(new ConcreteSolution[solutions.size()]);
					}
					}
					// Update changed status
					changed |= (s != solution);
				}
			}
		}
		return new ConcreteSolution[] {solution};
	}

	// ================================================================================
	// Substitute
	// ================================================================================

	/**
	 * Substitute all existential type variables in a given a type in either an
	 * upper or lower bound position. For example, consider substituting into
	 * <code>{?0 f}</code> with a solution <code>int|bool :> ?0 :> int</code>. In
	 * the upper position, we end up with <code>{int|bool f}</code> and in the lower
	 * position we have <code>{int f}</code>. A key issue is that positional
	 * variance must be observed. This applies, for example, to lambda types where
	 * parameters are <i>contravariant</i>. Thus, consider substituting into
	 * <code>function(?0)->(?0)</code> with a solution
	 * <code>int|bool :> ?0 :> int</code>. In the upper bound position we get
	 * <code>function(int)->(int|bool)</code>, whilst in the lower bound position we
	 * have <code>function(int|bool)->(int)</code>.
	 *
	 * @param type     The type being substituted into.
	 * @param solution The solution being used for substitution.
	 * @param sign     Indicates the upper (<code>true</code>) or lower bound
	 *                 (<code>false</code>) position.
	 * @return
	 */
	private static Type substitute(Type type, ConcreteSolution solution, boolean sign) {
		switch (type.getOpcode()) {
		case TYPE_any:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
		case TYPE_null:
		case TYPE_void:
		case TYPE_universal:
			return type;
		case TYPE_existential: {
			Type.ExistentialVariable t = (Type.ExistentialVariable) type;
			int var = t.get();
			return sign ? solution.ceil(var) : solution.floor(var);
		}
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			Type element = t.getElement();
			Type nElement = substitute(element,solution,sign);
			if(element == nElement) {
				return type;
			} else if(nElement instanceof Type.Void) {
				return Type.Void;
			} else if(nElement instanceof Type.Any) {
				return Type.Any;
			} else {
				return new Type.Array(nElement);
			}
		}
		case TYPE_staticreference:
		case TYPE_reference: {
			Type.Reference t = (Type.Reference) type;
			Type element = t.getElement();
			// NOTE: this substitution is effectively a co-variant substitution. Whilst this
			// may seem problematic, it isn't because we'll always eliminate variables whose
			// bounds are not subtypes of each other. For example, <code>&(int|bool) :> ?1
			// :> &(int)</code> is not satisfiable.
			Type nElement = substitute(element,solution,sign);
			if(element == nElement) {
				return type;
			} else if(nElement instanceof Type.Void) {
				return Type.Void;
			} else if(nElement instanceof Type.Any) {
				return Type.Any;
			} else {
				return new Type.Reference(nElement);
			}
		}
		case TYPE_function:
		case TYPE_method:
		case TYPE_property: {
			Type.Callable t = (Type.Callable) type;
			Type parameters = t.getParameter();
			Type returns = t.getReturn();
			// NOTE: invert sign to account for contra-variance
			Type nParameters = substitute(parameters,solution,!sign);
			Type nReturns = substitute(returns,solution,sign);
			if(nParameters == parameters && nReturns == returns) {
				return type;
			} else if(nReturns instanceof Type.Void || nParameters instanceof Type.Any) {
				return Type.Void;
			} else if(nReturns instanceof Type.Any || nParameters instanceof Type.Void) {
				return Type.Any;
			} else if(type instanceof Type.Function) {
				return new Type.Function(nParameters, nReturns);
			} else if(type instanceof Type.Property) {
				return new Type.Property(nParameters, nReturns);
			} else {
				Type.Method m = (Type.Method) type;
				return new Type.Method(nParameters, nReturns, m.getCapturedLifetimes(), m.getLifetimeParameters());
			}
		}
		case TYPE_nominal: {
			Type.Nominal t = (Type.Nominal) type;
			Tuple<Type> parameters = t.getParameters();
			// NOTE: the following is problematic in the presence of contra-variant
			// parameter positions. However, this is not unsound per se. Rather it will just
			// mean some variables are eliminated because their bounds are considered
			// unsatisfiable.
			Tuple<Type> nParameters = substitute(parameters,solution,sign);
			if(parameters == nParameters) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for(int i=0;i!=parameters.size();++i) {
					Type ith = parameters.get(i);
					if (ith instanceof Type.Void) {
						return Type.Void;
					} else if (ith instanceof Type.Any) {
						return Type.Any;
					}
				}
				return new Type.Nominal(t.getLink(), nParameters);
			}
		}
		case TYPE_tuple: {
			Type.Tuple t = (Type.Tuple) type;
			Type[] elements = t.getAll();
			Type[] nElements = substitute(elements,solution,sign);
			if(elements == nElements) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for(int i=0;i!=nElements.length;++i) {
					Type ith = nElements[i];
					if (ith instanceof Type.Void) {
						return Type.Void;
					} else if (ith instanceof Type.Any) {
						return Type.Any;
					}
				}
				// Done
				return Type.Tuple.create(nElements);
			}
		}
		case TYPE_union: {
			Type.Union t = (Type.Union) type;
			Type[] elements = t.getAll();
			Type[] nElements = substitute(elements,solution,sign);
			if(elements == nElements) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for(int i=0;i!=nElements.length;++i) {
					Type ith = nElements[i];
					if (ith instanceof Type.Void) {
						return Type.Void;
					} else if (ith instanceof Type.Any) {
						return Type.Any;
					}
				}
				// Done
				return new Type.Union(nElements);
			}
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> fields = t.getFields();
			Tuple<Type.Field> nFields = substituteFields(fields,solution,sign);
			if(fields == nFields) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for(int i=0;i!=nFields.size();++i) {
					Type ith = nFields.get(i).getType();
					if (ith instanceof Type.Void) {
						return Type.Void;
					} else if (ith instanceof Type.Any) {
						return Type.Any;
					}
				}
				return new Type.Record(t.isOpen(),nFields);
			}
		}
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	private static Tuple<Type> substitute(Tuple<Type> types, ConcreteSolution solution, boolean sign) {
		for (int i = 0; i != types.size(); ++i) {
			Type t = types.get(i);
			Type n = substitute(t, solution, sign);
			if (t != n) {
				// Committed to change
				Type[] nTypes = new Type[types.size()];
				// Copy all visited so far over
				System.arraycopy(types.getAll(), 0, nTypes, 0, i + 1);
				// Continue substitution
				for (; i < nTypes.length; ++i) {
					nTypes[i] = substitute(types.get(i), solution, sign);
				}
				// Done
				return new Tuple<>(nTypes);
			}
		}
		return types;
	}

	private static Tuple<Type.Field> substituteFields(Tuple<Type.Field> fields, ConcreteSolution solution, boolean sign) {
		for(int i=0;i!=fields.size();++i) {
			Type.Field t = fields.get(i);
			Type.Field n = substituteField(t,solution,sign);
			if(t != n) {
				// Committed to change
				Type.Field[] nFields = new Type.Field[fields.size()];
				// Copy all visited so far over
				System.arraycopy(fields.getAll(), 0, nFields, 0, i + 1);
				// Continue substitution
				for(;i<nFields.length;++i) {
					nFields[i] = substituteField(fields.get(i),solution,sign);
				}
				// Done
				return new Tuple<>(nFields);
			}
		}
		return fields;
	}

	private static Type.Field substituteField(Type.Field field, ConcreteSolution solution, boolean sign) {
		Type type = field.getType();
		Type nType = substitute(type,solution,sign);
		if(type == nType) {
			return field;
		} else {
			return new Type.Field(field.getName(), nType);
		}
	}

	private static Type[] substitute(Type[] types, ConcreteSolution solution, boolean sign) {
		Type[] nTypes = types;
		for(int i=0;i!=nTypes.length;++i) {
			Type t = types[i];
			Type n = substitute(t,solution,sign);
			if(t != n && nTypes == types) {
				nTypes = Arrays.copyOf(types, types.length);
			}
			nTypes[i] = n;
		}
		return nTypes;
	}

	// ================================================================================
	// Helpers
	// ================================================================================

	private static final Tuple<Expr> EMPTY_INVARIANT = new Tuple<>();

	/**
	 * Determine the <i>Greatest Lower Bound</i> between two competing upper bounds
	 * for some variable. For example, if we have <code>int|null :> x</code> and
	 * <code>int :> x</code> then the glb is <code>int</code> since it is the
	 * largest type below both bounds.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	private static Type glb(Type lhs, Type rhs) {
		boolean u1 = lhs instanceof Type.Union;
		boolean u2 = rhs instanceof Type.Union;
		if(lhs instanceof Type.Any) {
			return rhs;
		} else if(rhs instanceof Type.Any) {
			return lhs;
		} else if(lhs.equals(rhs)) {
			return lhs;
		} else if(u1 && u2) {
			Type.Union l = (Type.Union) lhs;
			Type.Union r = (Type.Union) rhs;
			return intersect(l.getAll(),r.getAll());
		} else if(u1) {
			Type.Union l = (Type.Union) lhs;
			return intersect(l.getAll(),rhs);
		} else if(u2) {
			Type.Union r = (Type.Union) rhs;
			return intersect(r.getAll(),lhs);
		} else {
			return Type.Void;
		}
	}

	private static Type lub(Type lhs, Type rhs) {
		boolean u1 = lhs instanceof Type.Union;
		boolean u2 = rhs instanceof Type.Union;
		if(lhs instanceof Type.Void) {
			return rhs;
		} else if(rhs instanceof Type.Void) {
			return lhs;
		} else if(lhs.equals(rhs)) {
			return lhs;
		} else if(u1 && u2) {
			Type.Union l = (Type.Union) lhs;
			Type.Union r = (Type.Union) rhs;
			return Type.Union.create(ArrayUtils.append(l.getAll(),r.getAll()));
		} else if(u1) {
			Type.Union l = (Type.Union) lhs;
			return Type.Union.create(ArrayUtils.append(l.getAll(),rhs));
		} else if(u2) {
			Type.Union r = (Type.Union) rhs;
			return Type.Union.create(ArrayUtils.append(lhs,r.getAll()));
		} else {
			return new Type.Union(lhs,rhs);
		}
	}

	public static Type intersect(Type[] lhs, Type... rhs) {
		ArrayList<Type> types = new ArrayList<>();
		for (int i = 0; i != lhs.length; ++i) {
			Type ith = lhs[i];
			for (int j = 0; j != rhs.length; ++j) {
				Type jth = rhs[j];
				if (ith.equals(jth)) {
					types.add(ith);
				}
			}
		}
		return Type.Union.create(types.toArray(new Type[types.size()]));
	}

	/**
	 * Extract the lifetime from a given reference type.
	 *
	 * @param ref
	 * @return
	 */
	protected String extractLifetime(Type.Reference ref) {
		if (ref.hasLifetime()) {
			return ref.getLifetime().get();
		} else {
			return "*";
		}
	}


	/**
	 * Create an array of a given sized filled with a given initial type.
	 *
	 * @param n
	 * @param t
	 * @return
	 */
	public static Type[] fill(int n, Type t) {
		Type[] ts = new Type[n];
		Arrays.fill(ts, t);
		return ts;
	}

	/**
	 * Create an array of a given sized filled with a given initial type.
	 *
	 * @param n
	 * @param t
	 * @return
	 */
	public static Type[] expand(Type[] types, int n, Type t) {
		Type[] ts = Arrays.copyOf(types, n);
		for(int i=types.length;i < n;++i) {
			ts[i] = t;
		}
		return ts;
	}

	/**
	 * Normalise opcode for sake of simplicity. This allows us to compare the types
	 * of two operands more accurately using a switch.
	 *
	 * @param opcode
	 * @return
	 */
	protected int normalise(int opcode) {
		switch(opcode) {
		case TYPE_reference:
		case TYPE_staticreference:
			return TYPE_reference;
		case TYPE_method:
		case TYPE_property:
		case TYPE_function:
			return TYPE_function;
		}
		//
		return opcode;
	}
}
