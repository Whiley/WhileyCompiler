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

import static wyil.lang.WyilFile.TYPE_any;
import static wyil.lang.WyilFile.TYPE_array;
import static wyil.lang.WyilFile.TYPE_bool;
import static wyil.lang.WyilFile.TYPE_byte;
import static wyil.lang.WyilFile.TYPE_existential;
import static wyil.lang.WyilFile.TYPE_function;
import static wyil.lang.WyilFile.TYPE_int;
import static wyil.lang.WyilFile.TYPE_method;
import static wyil.lang.WyilFile.TYPE_nominal;
import static wyil.lang.WyilFile.TYPE_null;
import static wyil.lang.WyilFile.TYPE_property;
import static wyil.lang.WyilFile.TYPE_record;
import static wyil.lang.WyilFile.TYPE_reference;
import static wyil.lang.WyilFile.TYPE_staticreference;
import static wyil.lang.WyilFile.TYPE_tuple;
import static wyil.lang.WyilFile.TYPE_union;
import static wyil.lang.WyilFile.TYPE_universal;
import static wyil.lang.WyilFile.TYPE_void;

import java.util.Arrays;

import wybs.util.AbstractCompilationUnit.Tuple;
import wycc.util.ArrayUtils;
import wyil.lang.WyilFile.Type;
import wyil.util.Subtyping.AbstractEnvironment.ConcreteSolution;

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
public class IncrementalSubtypeConstraints implements Subtyping.Constraints {

	/**
	 * The empty constraint set which is, by construction, invalid.
	 */
	public static final IncrementalSubtypeConstraints BOTTOM = new IncrementalSubtypeConstraints(0,
			(ConcreteSolution) null, 0, null, null);

	private final Subtyping.AbstractEnvironment environment;
	private final int nVariables;
	private SymbolicConstraint[] constraints;
	private ConcreteSolution candidate;
	private int index;

	public IncrementalSubtypeConstraints(Subtyping.AbstractEnvironment environment) {
		this.environment = environment;
		this.constraints = new SymbolicConstraint[0];
		this.candidate = null;
		this.index = 0;
		this.nVariables = 0 ;
	}

	public IncrementalSubtypeConstraints(Type.Existential lhs, Type rhs, Subtyping.AbstractEnvironment environment) {
		this.environment = environment;
		this.constraints = new SymbolicConstraint[] { new SymbolicConstraint(lhs, rhs) };
		this.candidate = null;
		this.nVariables = lhs.get()+1;
	}

	public IncrementalSubtypeConstraints(Type lhs, Type.Existential rhs, Subtyping.AbstractEnvironment environment) {
		this.environment = environment;
		this.constraints = new SymbolicConstraint[] { new SymbolicConstraint(lhs, rhs) };
		this.candidate = null;
		this.index = 0;
		this.nVariables = rhs.get()+1;
	}

	private IncrementalSubtypeConstraints(int n, ConcreteSolution candidate, int index,
			SymbolicConstraint[] constraints, Subtyping.AbstractEnvironment environment) {
		this.environment = environment;
		this.constraints = constraints;
		this.candidate = candidate;
		this.index = index;
		this.nVariables = n;
	}
	@Override
	public boolean isSatisfiable() {
//		System.out.println("===========================================================");
//		System.out.println("SATISFIABLE? " + Arrays.toString(constraints) + "(" + index + "): " + candidate);
//		System.out.println("===========================================================");
		if (constraints == null) {
			return false;
		} else if (candidate == null) {
			this.candidate = initialise(new ConcreteSolution(environment),constraints,0);
			this.candidate = solve(candidate,0);
			this.index = constraints.length;
		} else {
			if (index < constraints.length) {
				this.candidate = initialise(candidate,constraints,index);
				this.candidate = solve(candidate,index);
				this.index = constraints.length;
			}
			if(candidate.isUnsatisfiable() || !candidate.isComplete(nVariables)) {
				// NOTE: we must attempt a restart here because the incremental solution may
				// have got stuck in a local minima during the propagation process.
				this.candidate = initialise(new ConcreteSolution(environment),constraints,0);
				this.candidate = solve(candidate,0);
				this.index = constraints.length;
			}
		}
		if(candidate.isUnsatisfiable() || !candidate.isComplete(nVariables)) {
			// Failed
			constraints = null;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int size() {
		if(constraints == null) {
			return 0;
		} else {
			return constraints.length;
		}
	}

	@Override
	public int max() {
		return nVariables - 1;
	}

	@Override
	public SymbolicConstraint get(int ith) {
		return constraints[ith];
	}

	@Override
	public IncrementalSubtypeConstraints intersect(Subtyping.Constraints other) {
		// FIXME: we could do better here.
		return intersect((IncrementalSubtypeConstraints) other);
	}

	/**
	 * Intersect this constraint set with another. This essentially determines the
	 * cross-product of rows in the two constraint sets.
	 *
	 * @param other
	 * @return
	 */
	public IncrementalSubtypeConstraints intersect(IncrementalSubtypeConstraints other) {
		if (constraints == null || other.constraints == null) {
			return BOTTOM;
		} else {
			// NOTE: performance optimisation possible here
			SymbolicConstraint[] nconstraints = ArrayUtils.append(constraints, other.constraints);
			//
			if (nconstraints.length == constraints.length) {
				// Nothing changed
				return this;
			} else {
				// FIXME: could attempt to update solution here
				return new IncrementalSubtypeConstraints(Math.max(nVariables, other.nVariables), candidate, index,
						nconstraints, environment);
			}
		}
	}

	@Override
	public ConcreteSolution solve(int n) {
		return candidate;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(constraints);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IncrementalSubtypeConstraints) {
			IncrementalSubtypeConstraints r = (IncrementalSubtypeConstraints) o;
			return Arrays.equals(constraints, r.constraints);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		if (constraints == null) {
			return "⊥";
		} else {
			String r = "";
			for (SymbolicConstraint constraint : constraints) {
				if (!r.equals("")) {
					r += ",";
				}
				r += constraint;
			}
			return "{" + r + "}";
		}
	}

	/**
	 * Update a given solution with all semi-concrete constraints. Such constraints
	 * do not need to be closed over, since they imply concrete bounds on their
	 * respective variables.
	 *
	 * @param solution
	 * @param constraints
	 * @param start
	 * @return
	 */
	private static ConcreteSolution initialise(ConcreteSolution solution, SymbolicConstraint[] constraints, int start) {
		for (int i = start; i < constraints.length; ++i) {
			SymbolicConstraint ith = constraints[i];
			Type upper = ith.first();
			Type lower = ith.second();
			// Only consider fully symbolic constraints?
			if (Subtyping.isConcrete(upper)) {
				Type.Existential e = (Type.Existential) lower;
				solution = solution.constrain(upper, e.get());
			} else if (Subtyping.isConcrete(lower)) {
				Type.Existential e = (Type.Existential) upper;
				solution = solution.constrain(e.get(), lower);
			}
		}
		return solution;
	}

	private ConcreteSolution solve(ConcreteSolution solution, int n) {
//		System.out.println(">>>>>>>>>>>>> SOLVING : " + solution);
		solution = close(solution, constraints, n, environment);
		// Sanity check whether we've finished or not
		if (!solution.isComplete(nVariables)) {
			// Solution not satisfiable yet. This maybe because there are unsolved
			// variables. To resolve these, we have to find "half-open" variables and close
			// them. Unfortunately, the order in which we do this matters. Therefore, in the
			// worst case, we may have to try all possible orderings.
			for(int i=(nVariables-1);i>=0;--i) {
				Type upper = solution.ceil(i);
				Type lower = solution.floor(i);
				boolean u = (upper instanceof Type.Any);
				boolean l = (lower instanceof Type.Void);
				// Check for half-open cases which allow an obvious guess. Guessing is critical
				// for solving some constraint forms.
				if(!u && l) {
//					System.out.println("*** CONSTRAINING " + i + " :> " + upper);
					ConcreteSolution guess = solve(solution.constrain(i, upper), n);
					if(guess.isComplete(nVariables) && !guess.isUnsatisfiable()) {
						return guess;
					}
				} else if(u && !l) {
//					System.out.println("*** CONSTRAINING " + i + " <: " + lower);
					ConcreteSolution guess = solve(solution.constrain(lower, i), n);
					if(guess.isComplete(nVariables) && !guess.isUnsatisfiable()) {
						return guess;
					}
				}
			}
		}
//		System.out.println("<<<<<<<<<<< SOLVING : " + solution);
		return solution;
	}

	/**
	 * A simple implementation of a single symboling subtyping constraint.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class SymbolicConstraint extends wycc.util.Pair<Type, Type> implements Subtyping.Constraint {
		public SymbolicConstraint(Type upper, Type lower) {
			super(upper, lower);
		}

		@Override
		public String toString() {
			return first() + ":>" + second();
		}

		@Override
		public Type getUpperBound() {
			return first();
		}

		@Override
		public Type getLowerBound() {
			return second();
		}
	}

	// ===============================================================================
	// Constraint Solving
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
	private static ConcreteSolution close(ConcreteSolution solution, SymbolicConstraint[] constraints, int n, Subtyping.Environment env) {
//		System.out.println(">>> CLOSING: " + solution + " " + Arrays.toString(constraints));
		boolean changed = true;
		int k = 0;
		// NOTE: this is a very HOT loop on benchmarks with large array initialisers.
		// The bound is introduced to prevent against infinite loops.
		while (changed && k < 10) {
			changed = false;
			final ConcreteSolution s = solution;
			//
			for (int i = n; i < constraints.length; ++i) {
				SymbolicConstraint ith = constraints[i];
				Type upper = ith.first();
				Type lower = ith.second();
				// NOTE: this could be optimised
				if(!Subtyping.isConcrete(upper) && !Subtyping.isConcrete(lower)) {
					Type cUpper = substitute(upper, solution, true);
					Type cLower = substitute(lower, solution, false);
//					System.out.println("BEFORE: " + upper + ":>" + lower + " : " + solution);
					// NOTE: constraints generated in the following are all guaranteed to have one
					// bound as an existential and one as a concrete type.
					if(!(cUpper instanceof Type.Any)) {
//						System.out.println("AFTER(1): " + cUpper + ":>" + lower);
						solution = solution.intersect(env.isSubtype(cUpper, lower));
					}
					if(!(cLower instanceof Type.Void)) {
//						System.out.println("AFTER(2): " + upper + ":>" + cLower);
						solution = solution.intersect(env.isSubtype(upper, cLower));
					}
//					System.out.println("SOLUTION[" + k + "](" + i + "/" + constraints.length + "): " + ith + " & " + solution + " : " + (s!=solution));
				}
			}
			// Update changed status
			changed |= (s != solution);
			k++;
		}
//		System.out.println("<<< CLOSING : " + solution);
		return solution;
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
			Type.Existential t = (Type.Existential) type;
			int var = t.get();
			return sign ? solution.ceil(var) : solution.floor(var);
		}
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			Type element = t.getElement();
			Type nElement = substitute(element, solution, sign);
			if (element == nElement) {
				return type;
			} else if (nElement instanceof Type.Void) {
				return Type.Void;
			} else if (nElement instanceof Type.Any) {
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
			Type nElement = substitute(element, solution, sign);
			if (element == nElement) {
				return type;
			} else if (nElement instanceof Type.Void) {
				return Type.Void;
			} else if (nElement instanceof Type.Any) {
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
			Type nParameters = substitute(parameters, solution, !sign);
			Type nReturns = substitute(returns, solution, sign);
			if (nParameters == parameters && nReturns == returns) {
				return type;
			} else if (nReturns instanceof Type.Void || nParameters instanceof Type.Any) {
				return Type.Void;
			} else if (nReturns instanceof Type.Any || nParameters instanceof Type.Void) {
				return Type.Any;
			} else if (type instanceof Type.Function) {
				return new Type.Function(nParameters, nReturns);
			} else if (type instanceof Type.Property) {
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
			Tuple<Type> nParameters = substitute(parameters, solution, sign);
			if (parameters == nParameters) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for (int i = 0; i != nParameters.size(); ++i) {
					Type ith = nParameters.get(i);
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
			Type[] nElements = substitute(elements, solution, sign);
			if (elements == nElements) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for (int i = 0; i != nElements.length; ++i) {
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
			Type[] nElements = substitute(elements, solution, sign);
			if (elements == nElements) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for (int i = 0; i != nElements.length; ++i) {
					Type ith = nElements[i];
					if (ith instanceof Type.Void) {
						return Type.Void;
					} else if (ith instanceof Type.Any) {
						return Type.Any;
					}
				}
				// Done
				return Type.Union.create(nElements);
			}
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> fields = t.getFields();
			Tuple<Type.Field> nFields = substituteFields(fields, solution, sign);
			if (fields == nFields) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for (int i = 0; i != nFields.size(); ++i) {
					Type ith = nFields.get(i).getType();
					if (ith instanceof Type.Void) {
						return Type.Void;
					} else if (ith instanceof Type.Any) {
						return Type.Any;
					}
				}
				return new Type.Record(t.isOpen(), nFields);
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

	private static Tuple<Type.Field> substituteFields(Tuple<Type.Field> fields, ConcreteSolution solution,
			boolean sign) {
		for (int i = 0; i != fields.size(); ++i) {
			Type.Field t = fields.get(i);
			Type.Field n = substituteField(t, solution, sign);
			if (t != n) {
				// Committed to change
				Type.Field[] nFields = new Type.Field[fields.size()];
				// Copy all visited so far over
				System.arraycopy(fields.getAll(), 0, nFields, 0, i + 1);
				// Continue substitution
				for (; i < nFields.length; ++i) {
					nFields[i] = substituteField(fields.get(i), solution, sign);
				}
				// Done
				return new Tuple<>(nFields);
			}
		}
		return fields;
	}

	private static Type.Field substituteField(Type.Field field, ConcreteSolution solution, boolean sign) {
		Type type = field.getType();
		Type nType = substitute(type, solution, sign);
		if (type == nType) {
			return field;
		} else {
			return new Type.Field(field.getName(), nType);
		}
	}

	private static Type[] substitute(Type[] types, ConcreteSolution solution, boolean sign) {
		Type[] nTypes = types;
		for (int i = 0; i != nTypes.length; ++i) {
			Type t = types[i];
			Type n = substitute(t, solution, sign);
			if (t != n && nTypes == types) {
				nTypes = Arrays.copyOf(types, types.length);
			}
			nTypes[i] = n;
		}
		return nTypes;
	}


	// ================================================================================
	// Helpers
	// ================================================================================
}
