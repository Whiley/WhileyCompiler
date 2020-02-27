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
			(ConcreteSolution) null, true, null, null);
	/**
	 * A parent pointer to the enclosing environment. This is necessary for access
	 * to the subtype operator.
	 */
	private final Subtyping.AbstractEnvironment environment;
	/**
	 * The set of subtyping constraints that this class embodies. The key is that we
	 * want to able to solve these constraints easily to determine whether or not
	 * they are satisfiable.
	 */
	private Subtyping.Constraint[] constraints;
	/**
	 * A cache of the maximum number of variables used in any of the constraints.
	 * This just helps us know when we have a complete solution or not.
	 */
	private final int nVariables;
	/**
	 * The best current (valid) solution to the given set of constraints. This can
	 * be <code>null</code> if none computed yet. It can also be out-of-date with
	 * respect to the given set of constraints.
	 */
	private Subtyping.Constraints.Solution candidate;
	/**
	 * Dirty flag indicates whether or not the candidate solution is up to date with
	 * the given constraints. This allows us to be lazy in closing over constraints
	 * whilst they are being accumulated through intersection operations prior to an
	 * satisfiability query.
	 */
	private boolean dirty;

	public IncrementalSubtypeConstraints(Subtyping.AbstractEnvironment environment, Subtyping.Constraint... constraints) {
		this.environment = environment;
		this.constraints = ArrayUtils.removeDuplicates(constraints);
		this.candidate = null;
		this.nVariables = Subtyping.numberOfVariables(constraints);
	}

	private IncrementalSubtypeConstraints(int n, Subtyping.Constraints.Solution candidate, boolean dirty,
			Subtyping.Constraint[] constraints, Subtyping.AbstractEnvironment environment) {
		this.environment = environment;
		this.constraints = constraints;
		this.candidate = candidate;
		this.dirty = dirty;
		this.nVariables = n;
	}
	private static int COUNTER = 0;
	@Override
	public boolean isSatisfiable() {
//		System.out.println("===========================================================");
//		System.out.println("SATISFIABLE?" + (++COUNTER) + " " + Arrays.toString(constraints) + "(" + dirty + "," + nVariables +"): " + candidate);
//		System.out.println("===========================================================");
		if (constraints == null) {
			return false;
		} else if (candidate == null) {
			this.candidate = solve(environment.EMPTY_SOLUTION);
			this.dirty = false;
		} else {
			if (dirty) {
				this.candidate = solve(candidate);
				this.dirty = false;
			}
			if(candidate.isUnsatisfiable() || !candidate.isComplete(nVariables)) {
				// NOTE: we must attempt a restart here because the incremental solution may
				// have got stuck in a local minima during the propagation process.
				this.candidate = solve(environment.EMPTY_SOLUTION);
				this.dirty = false;
			}
		}
		if(candidate.isUnsatisfiable() || !candidate.isComplete(nVariables)) {
//			System.out.println("===========================================================");
//			System.out.println("UNSATISFIABLE " + (++COUNTER) + " " + Arrays.toString(constraints) + "(" + dirty + "," + nVariables +"): " + candidate);
//			System.out.println("===========================================================");
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
	public int maxVariable() {
		return nVariables - 1;
	}

	@Override
	public Subtyping.Constraint get(int ith) {
		return constraints[ith];
	}

	@Override
	public IncrementalSubtypeConstraints intersect(Subtyping.Constraint... oconstraints) {
		if (constraints == null) {
			return BOTTOM;
		} else {
			// NOTE: performance optimisation possible here
			Subtyping.Constraint[] nconstraints = ArrayUtils.append(constraints, oconstraints);
			// Remove all duplicates
			nconstraints = ArrayUtils.removeDuplicates(nconstraints);
			// Check what happened
			if (nconstraints.length == constraints.length) {
				// Nothing changed!
				return this;
			} else {
				int max = Math.max(nVariables, Subtyping.numberOfVariables(oconstraints));
				return new IncrementalSubtypeConstraints(max, candidate, true, nconstraints, environment);
			}
		}
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
			Subtyping.Constraint[] nconstraints = ArrayUtils.append(constraints, other.constraints);
			// Remove all duplicates
			nconstraints = ArrayUtils.removeDuplicates(nconstraints);
			// Check what happened
			if (nconstraints.length == constraints.length) {
				// Nothing changed!
				return this;
			} else {
				// FIXME: could attempt to update solution here which might provide some
				// performance gains.
				return new IncrementalSubtypeConstraints(Math.max(nVariables, other.nVariables), candidate, true,
						nconstraints, environment);
			}
		}
	}

	@Override
	public Subtyping.Constraints.Solution solve(int n) {
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
			for (Subtyping.Constraint constraint : constraints) {
				if (!r.equals("")) {
					r += ",";
				}
				r += constraint;
			}
			String c = (candidate == null) ? "_" : candidate.toString();
			return "{" + r + "}" + c;
		}
	}

	public static String tab(int t) {
		String r = "";
		for(int i=0;i<t;++i) {
			r = r + "\t";
		}
		return r;
	}
	private static int sCounter = 0;
	private static int sTab = 0;
	private Subtyping.Constraints.Solution solve(Subtyping.Constraints.Solution solution) {
//		System.out.println(tab(sTab) + "**************************************************************");
//		System.out.println(tab(sTab) + "**** SOLVING(" + sCounter++ + "): " + nVariables + " : " + solution);
//		System.out.println(tab(sTab) + "**************************************************************");
		sTab++;
		solution = close(solution, constraints, environment);
		// Sanity check whether we've finished or not
		if (!solution.isComplete(nVariables)) {
			// Solution not satisfiable yet. This maybe because there are unsolved
			// variables. To resolve these, we have to find "half-open" variables and close
			// them. Unfortunately, the order in which we do this matters. Therefore, in the
			// worst case, we may have to try all possible orderings.
			for(int i=0;i<nVariables;i++) {
				Type upper = solution.ceil(i);
				Type lower = solution.floor(i);
				boolean u = (upper instanceof Type.Any);
				boolean l = (lower instanceof Type.Void);
				// Check for half-open cases which allow an obvious guess. Guessing is critical
				// for solving some constraint forms.
				if(!u && l) {
//					System.out.println(tab(sTab) + "*** CONSTRAINING " + i + " :> " + upper);
					Subtyping.Constraints.Solution guess = solve(solution.constrain(i, upper));
					if(guess.isComplete(nVariables) && !guess.isUnsatisfiable()) {
						sTab--;
						return guess;
					}
//					System.out.println(tab(sTab) + "!!! IGNORING GUESS");
				} else if(u && !l) {
//					System.out.println(tab(sTab) + "*** CONSTRAINING " + i + " <: " + lower);
					Subtyping.Constraints.Solution guess = solve(solution.constrain(lower, i));
					if(guess.isComplete(nVariables) && !guess.isUnsatisfiable()) {
						sTab--;
						return guess;
					}
//					System.out.println(tab(sTab) + "!!! IGNORING GUESS");
				}
			}
		}
		sTab--;
//		System.out.println(tab(sTab) + "<<<<<<<<<<< SOLVING : " + solution);
		return solution;
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
	private static int CLOSING_COUNT=0;
	private static Subtyping.Constraints.Solution close(Subtyping.Constraints.Solution solution,
			Subtyping.Constraint[] constraints, Subtyping.Environment env) {
//		System.out.println(tab(sTab) + ">>> CLOSING(" + CLOSING_COUNT++ +"): " + solution + " " + Arrays.toString(constraints));
		boolean changed = true;
		int k = 0;
		// NOTE: this is a very HOT loop on benchmarks with large array initialCLOSINGisers.
		// The bound is introduced to prevent against infinite loops.
		while (changed && k < 10) {
			changed = false;
			final Subtyping.Constraints.Solution s = solution;
			//
			for (int i = 0; i < constraints.length; ++i) {
				Subtyping.Constraint ith = constraints[i];
				solution = ith.apply(solution);
//				System.out.println(tab(sTab) + "SOLUTION[" + k + "](" + i + "/" + constraints.length + "): " + ith + " & " + solution + " : " + (s!=solution));
			}
			// Update changed status
			changed |= (s != solution);
			k++;
		}
//		System.out.println(tab(sTab) + "<<< CLOSING : " + solution);
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
	public static Type substitute(Type type, Subtyping.Constraints.Solution solution, boolean sign) {
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

	private static Tuple<Type> substitute(Tuple<Type> types, Subtyping.Constraints.Solution solution, boolean sign) {
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

	private static Tuple<Type.Field> substituteFields(Tuple<Type.Field> fields, Subtyping.Constraints.Solution solution,
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

	private static Type.Field substituteField(Type.Field field, Subtyping.Constraints.Solution solution, boolean sign) {
		Type type = field.getType();
		Type nType = substitute(type, solution, sign);
		if (type == nType) {
			return field;
		} else {
			return new Type.Field(field.getName(), nType);
		}
	}

	private static Type[] substitute(Type[] types, Subtyping.Constraints.Solution solution, boolean sign) {
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
