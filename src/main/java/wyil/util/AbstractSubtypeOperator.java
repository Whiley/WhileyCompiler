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
import java.util.function.Function;

import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wycc.util.ArrayUtils;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.*;
import wyil.lang.WyilFile.Type.Union;
import wyil.util.SubtypeOperator.Constraints;

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
		ConstraintSet constraints = isSubtype(t1,t2,lifetimes);
		// FIXME: clearly this is broken!
		return !constraints.isEmpty();
	}

	@Override
	public ConstraintSet isSubtype(Type t1, Type t2, LifetimeRelation lifetimes) {
		return isSubtype(t1,t2,lifetimes,null);
	}

	@Override
	public boolean isEmpty(QualifiedName nid, Type type) {
		return isContractive(nid, type, null);
	}

	// ===========================================================================================
	// Reference Helpers
	// ===========================================================================================

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
		case TYPE_variable:
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
	protected ConstraintSet isSubtype(Type t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
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
			case TYPE_variable:
				return isSubtype((Type.Variable) t1, (Type.Variable) t2, lifetimes, cache);
			case TYPE_existential:
				return isSubtype(t1, (Type.Existential) t2, lifetimes, cache);
			default:
				throw new IllegalArgumentException("unexpected type encountered: " + t1);
			}
		} else if(t1_opcode == TYPE_any || t2_opcode == TYPE_void) {
			return TOP;
		} else if (t2_opcode == TYPE_existential) {
			return isSubtype(t1, (Type.Existential) t2, lifetimes, cache);
		} else if (t2_opcode == TYPE_nominal) {
			return isSubtype(t1, (Type.Nominal) t2, lifetimes, cache);
		} else if (t2_opcode == TYPE_union) {
			return isSubtype(t1, (Type.Union) t2, lifetimes, cache);
		} else if (t1_opcode == TYPE_union) {
			return isSubtype((Type.Union) t1, t2, lifetimes, cache);
		} else if (t1_opcode == TYPE_nominal) {
			return isSubtype((Type.Nominal) t1, (Type.Atom) t2, lifetimes, cache);
		} else if (t1_opcode == TYPE_existential) {
				return isSubtype((Type.Existential) t1, (Type.Atom) t2, lifetimes, cache);
		} else {
			// Nothing else works except void
			return BOTTOM;
		}
	}

	protected ConstraintSet isSubtype(Type.Array t1, Type.Array t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		return isSubtype(t1.getElement(), t2.getElement(), lifetimes, cache);
	}

	protected ConstraintSet isSubtype(Type.Tuple t1, Type.Tuple t2, LifetimeRelation lifetimes,
			BinaryRelation<Type> cache) {
		ConstraintSet constraints = TOP;
		// Check elements one-by-one
		for (int i = 0; i != t1.size(); ++i) {
			ConstraintSet ith = isSubtype(t1.get(i), t2.get(i), lifetimes, cache);
			if (ith == null || constraints == null) {
				return BOTTOM;
			} else {
				constraints = constraints.intersect(ith);
			}
		}
		// Done
		return constraints;
	}

	protected ConstraintSet isSubtype(Type.Record t1, Type.Record t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		ConstraintSet constraints = TOP;
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
			ConstraintSet other = isSubtype(f1.getType(), f2.getType(), lifetimes, cache);
			if(other == null || constraints == null) {
				return BOTTOM;
			} else {
				constraints = constraints.intersect(other);
			}
		}
		// Done
		return constraints;
	}

	protected ConstraintSet isSubtype(Type.Reference t1, Type.Reference t2, LifetimeRelation lifetimes,
			BinaryRelation<Type> cache) {
		String l1 = extractLifetime(t1);
		String l2 = extractLifetime(t2);
		//
		if (!lifetimes.isWithin(l1, l2)) {
			// Definitely unsafe
			return BOTTOM;
		}
		ConstraintSet first = areEquivalent(t1.getElement(), t2.getElement(), lifetimes, cache);
		ConstraintSet second = isWidthSubtype(t1.getElement(), t2.getElement(), lifetimes, cache);
		// Join them together
		return first.union(second);
	}

	protected ConstraintSet isWidthSubtype(Type t1, Type t2, LifetimeRelation lifetimes,
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
			ConstraintSet constraints = TOP;
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
					ConstraintSet other = areEquivalent(f1.getType(), f2.getType(), lifetimes, cache);
					constraints = constraints.intersect(other);
				}
				return constraints;
			}
		}
		return BOTTOM;
	}

	protected ConstraintSet isSubtype(Type.Callable t1, Type.Callable t2, LifetimeRelation lifetimes,
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
		ConstraintSet c_params = areEquivalent(t1_params, t2_params, lifetimes, cache);
		ConstraintSet c_returns = areEquivalent(t1_return, t2_return, lifetimes, cache);
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
		return c_params.intersect(c_returns);
	}

	protected ConstraintSet isSubtype(Type.Variable t1, Type.Variable t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		if(t1.getOperand().equals(t2.getOperand())) {
			return TOP;
		} else {
			return BOTTOM;
		}
	}

	protected ConstraintSet isSubtype(Type.Existential t1, Type.Atom t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		return new ConstraintSet(t1, t2, lifetimes, this);
	}

	protected ConstraintSet isSubtype(Type t1, Type.Existential t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		return new ConstraintSet(t1, t2, lifetimes, this);
	}

	protected ConstraintSet isSubtype(Type t1, Type.Union t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		ConstraintSet constraints = TOP;
		for(int i=0;i!=t2.size();++i) {
			ConstraintSet other = isSubtype(t1, t2.get(i), lifetimes, cache);
			constraints = constraints.intersect(other);
		}
		return constraints;
	}

	protected ConstraintSet isSubtype(Type.Union t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		ConstraintSet constraints = BOTTOM;
		for (int i = 0; i != t1.size(); ++i) {
			ConstraintSet ith = isSubtype(t1.get(i), t2, lifetimes, cache);
			constraints = constraints.union(ith);
		}
		return constraints;
	}

	protected ConstraintSet isSubtype(Type.Nominal t1, Type.Nominal t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		Decl.Type d1 = t1.getLink().getTarget();
		Decl.Type d2 = t2.getLink().getTarget();
		//
		Tuple<Expr> t1_invariant = d1.getInvariant();
		Tuple<Expr> t2_invariant = d2.getInvariant();
		// Dispatch easy cases
		if (d1 == d2) {
			ConstraintSet constraints = TOP;
			Tuple<Type> t1s = t1.getParameters();
			Tuple<Type> t2s = t2.getParameters();
			// FIXME: broken for contra-variant positions #989!!
			for (int i = 0; i != t1s.size(); ++i) {
				ConstraintSet other = isSubtype(t1s.get(i), t2s.get(i), lifetimes, cache);
				constraints = constraints.intersect(other);
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
	protected ConstraintSet isSubtype(Type t1, Type.Nominal t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
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
	protected ConstraintSet isSubtype(Type.Nominal t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		//
		Decl.Type d1 = t1.getLink().getTarget();
		Tuple<Expr> t1_invariant = d1.getInvariant();
		// Dispatch easy cases
		if (isSubtype(t1_invariant, EMPTY_INVARIANT)) {
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
	protected ConstraintSet areEquivalent(Type t1, Type t2, LifetimeRelation lifetimes, BinaryRelation<Type> cache) {
		// NOTE: this is a temporary solution.
		ConstraintSet left = isSubtype(t1, t2, lifetimes, cache);
		ConstraintSet right = isSubtype(t2, t1, lifetimes, cache);
		//
		return left.intersect(right);
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
		types = ArrayUtils.removeAll(types, Type.Void);
		//
		switch(types.length) {
		case 0:
			return Type.Void;
		case 1:
			return types[0];
		default:
			return new Type.Union(types);
		}
	}

	// ===============================================================================
	// ConstraintSet
	// ===============================================================================

	/**
	 * A minimal implementation of the constraints interface.
	 */
	public static final ConstraintSet TOP = new ConstraintSet(true);

	/**
	 * The empty constraint set which is, by construction, invalid.
	 */
	public static final ConstraintSet BOTTOM = new ConstraintSet(false);

	public static class ConstraintSet implements SubtypeOperator.Constraints {
		private final LifetimeRelation lifetimes;
		private final AbstractSubtypeOperator subtyping;
		private final Row[] rows;

		public ConstraintSet(boolean top) {
			this.rows = top ? new Row[] { new Row() } : new Row[0];
			this.lifetimes = null;
			this.subtyping = null;
		}

		public ConstraintSet(Type.Existential lhs, Type.Atom rhs, LifetimeRelation lifetimes, AbstractSubtypeOperator subtyping) {
			this.lifetimes = lifetimes;
			this.subtyping = subtyping;
			this.rows = new Row[] { new Row(lhs,rhs) };
		}
		public ConstraintSet(Type lhs, Type.Existential rhs, LifetimeRelation lifetimes, AbstractSubtypeOperator subtyping) {
			this.lifetimes = lifetimes;
			this.subtyping = subtyping;
			this.rows = new Row[] { new Row(lhs,rhs) };
		}
		private ConstraintSet(Row[] rows, LifetimeRelation lifetimes, AbstractSubtypeOperator subtyping) {
			this.lifetimes = lifetimes;
			this.subtyping = subtyping;
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
		public Row get(int i) {
			return rows[i];
		}

		/**
		 * Intersect this constraint set with another. This essentially determines the
		 * cross-product of rows in the two constraint sets.
		 *
		 * @param other
		 * @return
		 */
		public ConstraintSet intersect(ConstraintSet other) {
			if(this == TOP) {
				return other;
			} else if(other == TOP) {
				return this;
			}
			// NOTE: at this point, other could be top
			final int n = rows.length;
			final int m = other.rows.length;
			Row[] nrows = new Row[n * m];
			for (int i = 0; i != n; ++i) {
				Row ith = rows[i];
				for (int j = 0; j != m; ++j) {
					Row jth = other.rows[j];
					nrows[(i * m) + j] = ith.intersect(jth);
				}
			}
			// Remove all rows which have been invalidated
			nrows = ArrayUtils.removeAll(nrows, null);
			// Sanity check what we have left
			if (nrows.length == 0) {
				return BOTTOM;
			} else {
				// NOTE: could optimise for common case where result equivalent to this.
				return new ConstraintSet(nrows, lifetimes, subtyping);
			}
		}

		public ConstraintSet union(ConstraintSet other) {
			if(this == BOTTOM) {
				return other;
			} else if(other == BOTTOM) {
				return this;
			}
			final int n = rows.length;
			final int m = other.rows.length;
			Row[] nrows = new Row[n + m];
			System.arraycopy(rows, 0, nrows, 0, n);
			System.arraycopy(other.rows, 0, nrows, n, m);
			nrows = ArrayUtils.removeDuplicates(nrows);
			return new ConstraintSet(nrows, lifetimes, subtyping);
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

		private class Row implements Constraints.Row {
			private final Type[] upperBounds;
			private final Type[] lowerBounds;
			public Row() {
				this.upperBounds = new Type[0];
				this.lowerBounds = new Type[0];
			}
			public Row(Type.Existential lhs, Type.Atom rhs) {
				int n = lhs.get()+1;
				this.lowerBounds = fill(n,Type.Void);
				this.upperBounds = fill(n,Type.Any);
				lowerBounds[n-1] = rhs;
			}
			public Row(Type lhs, Type.Existential rhs) {
				int n = rhs.get()+1;
				this.lowerBounds = fill(n,Type.Void);
				this.upperBounds = fill(n,Type.Any);
				upperBounds[n-1] = lhs;
			}
			private Row(Type[] upperBounds, Type[] lowerBounds) {
				this.upperBounds = upperBounds;
				this.lowerBounds = lowerBounds;
			}
			private Row intersect(Row row) {
				return intersect(this, row);
			}

			private Row intersect(Row lhs, Row rhs) {
				// Choose largest to work with
				if(lhs.upperBounds.length < rhs.upperBounds.length) {
					Row tmp = rhs;
					rhs = lhs;
					lhs = tmp;
				}
				// Intersect individual types
				Type[] lhsLowerBounds = lhs.lowerBounds;
				Type[] lhsUpperBounds = lhs.upperBounds;
				Type[] rhsLowerBounds = rhs.lowerBounds;
				Type[] rhsUpperBounds = rhs.upperBounds;
				Type[] nLowerBounds = lub(lhsLowerBounds,rhsLowerBounds);
				Type[] nUpperBounds = glb(lhsUpperBounds,rhsUpperBounds);
				// Check whether anything changed
				if(nLowerBounds == lhsLowerBounds && nUpperBounds == rhsUpperBounds) {
					// Nothing has actually changed, hence, just return as is.
					return lhs;
				}
				// Sanity check any updated bounds
				for(int i=0;i!=nLowerBounds.length;++i) {
					// FIXME: could improve performance here by not repeating the subtype test for cases where neither bound has actually changed.
					Type lower = nLowerBounds[i];
					Type upper = nUpperBounds[i];
					// FIXME: need proper lifetime relation!
					ConstraintSet cs = subtyping.isSubtype(upper, lower, lifetimes);
					//
					if (cs.isEmpty()) {
						// The bounds are no longer viable, hence the overall constraint set is no
						// longer viable.
						return null;
					} else {
						// FIXME: what to do here? The constraints could actually be telling us
						// something useful, no?
					}
				}
				return new Row(nUpperBounds,nLowerBounds);
			}

			@Override
			public int hashCode() {
				return Arrays.hashCode(lowerBounds) ^ Arrays.hashCode(upperBounds);
			}

			@Override
			public boolean equals(Object o) {
				if(o instanceof Row) {
					Row r = (Row) o;
					return Arrays.equals(upperBounds, r.upperBounds) && Arrays.equals(lowerBounds, r.lowerBounds);
				} else {
					return false;
				}
			}

			@Override
			public Map<Integer, Type> solve(int n) {
//				if(n > lowerBounds.length) {
//					return null;
//				}
				HashMap<Integer,Type> map = new HashMap<>();
				for(int i=0;i!=lowerBounds.length;++i) {
					Type t = solve(upperBounds[i],lowerBounds[i]);
					if(t == null) {
						return null;
					} else {
						map.put(i, t);
					}
				}
				for(int i=lowerBounds.length;i<n;++i) {
					map.put(i, Type.Void);

				}
				return map;
			}

			private Type solve(Type upper, Type lower) {
				// FIXME: this method is very dodgy
				if (upper instanceof Type.Any && lower instanceof Type.Void) {
					return null;
				} else if (upper instanceof Type.Any) {
					return lower;
				} else {
					return upper;
				}
			}

			@Override
			public String toString() {
				String r = "{";
				for(int i=0;i!=lowerBounds.length;++i) {
					if(i != 0) {
						r += ",";
					}
					Type lb = lowerBounds[i];
					Type ub = upperBounds[i];
					r += ub + " :> #" + i + " :> " + lb;
				}
				return r + "}";
			}
		}

	}

	// ===============================================================================
	// Helpers
	// ===============================================================================

	private static final Tuple<Expr> EMPTY_INVARIANT = new Tuple<>();


	private static Type[] glb(Type[] lhs, Type[] rhs) {
		// REQUIRES: lhs.length >= rhs.length
		Type[] result = lhs;
		//
		for(int i=0;i!=rhs.length;++i) {
			Type lhs_ith = lhs[i];
			Type glb = glb(lhs_ith,rhs[i]);
			if(glb != lhs_ith && result == lhs) {
				result = Arrays.copyOf(lhs, lhs.length);
			}
			result[i] = glb;
		}
		return result;
	}

	private static Type[] lub(Type[] lhs, Type[] rhs) {
		// REQUIRES: lhs.length >= rhs.length
		Type[] result = lhs;
		//
		for(int i=0;i!=rhs.length;++i) {
			Type lhs_ith = lhs[i];
			Type lub = lub(lhs_ith,rhs[i]);
			if(lub != lhs_ith && result == lhs) {
				result = Arrays.copyOf(lhs, lhs.length);
			}
			result[i] = lub;
		}
		return result;
	}

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
			return union(ArrayUtils.append(l.getAll(),r.getAll()));
		} else if(u1) {
			Type.Union l = (Type.Union) lhs;
			return union(ArrayUtils.append(l.getAll(),rhs));
		} else if(u2) {
			Type.Union r = (Type.Union) rhs;
			return union(ArrayUtils.append(lhs,r.getAll()));
		} else {
			return new Type.Union(lhs,rhs);
		}
	}

	private static Type union(Type[] types) {
		types = ArrayUtils.removeDuplicates(types);
		switch(types.length) {
		case 0:
			return Type.Void;
		case 1:
			return types[0];
		default:
			return new Type.Union(types);
		}
	}

	public static Type intersect(Type[] lhs, Type... rhs) {
		ArrayList<Type> types = new ArrayList<>();
		for(int i=0;i!=lhs.length;++i) {
			Type ith = lhs[i];
			for(int j=0;j!=rhs.length;++j) {
				Type jth = rhs[j];
				if(ith.equals(jth)) {
					types.add(ith);
				}
			}
		}
		switch(types.size()) {
		case 0:
			return Type.Void;
		case 1:
			return types.get(0);
		default:
			return new Type.Union(types.toArray(new Type[types.size()]));
		}
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
