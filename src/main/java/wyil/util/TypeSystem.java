// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wyautl_old.lang.Automata;
import wyautl_old.lang.Automaton;
import wyautl_old.lang.Automaton.State;
import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.util.ResolveError;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyil.lang.Type;
import wyil.lang.WyilFile;
import wyil.util.type.ExplicitCoercionOperator;
import wyil.util.type.LifetimeRelation;
import wyil.util.type.SubtypeOperator;
import wyil.util.type.TypeAlgorithms;

/**
 * <p>
 * The type system is responsible for managing the relationship between
 * nominal types and their underlying types. Every visible type has an
 * underlying type associated with it which, in some cases, will be the same.
 * For example, the underlying type associated with type <code>int</code> is
 * simply <code>int</code>. However, in many cases, there is a difference. For
 * example:
 * </p>
 *
 * <pre>
 * type nat is (int x) where x >= 0
 * </pre>
 *
 * <p>
 * In this case, the underlying type associated with the type <code>nat</code>
 * is <code>int</code>. This class provides a way to determine the underlying
 * type associated with a given type.
 * </p>
 * <p>
 * <b>NOTE:</b> in principle, this could cache expanded types for performance
 * reasons (though it currently does not).
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class TypeSystem {
	private final Build.Project project;

	public TypeSystem(Build.Project project) {
		this.project = project;
	}

	/**
	 * Determine whether or not this type corresponds to the empty type or not.
	 * This can happen in a number of ways.
	 *
	 * @param type
	 * @return
	 * @throws ResolveError
	 */
	public boolean isEmpty(Type type) throws ResolveError {
		Automaton automaton = toAutomaton(type);
		// FIXME: should include contractivity check?
		return automaton.states[0].kind == K_VOID;
	}

	/**
	 * <p>
	 * Contractive types are types which cannot accept value because they have
	 * an <i>unterminated cycle</i>. An unterminated cycle has no leaf nodes
	 * terminating it. For example, <code>X<{X field}></code> is contractive,
	 * where as <code>X<{null|X field}></code> is not.
	 * </p>
	 *
	 * <p>
	 * This method returns true if the type is contractive, or contains a
	 * contractive subcomponent. For example, <code>null|X<{X field}></code> is
	 * considered contracted.
	 * </p>
	 *
	 * @param type --- type to test for contractivity.
	 * @return
	 * @throws ResolveError
	 */
	public boolean isContractive(Type type) throws ResolveError {
		if (type instanceof Type.Leaf) {
			return false;
		} else {
			Automaton automaton = toAutomaton(type);
			return TypeAlgorithms.isContractive(automaton);
		}
	}

	/**
	 * Assuming given type is an effective record of some sort, expand to ensure
	 * record structure is visible. For example a type <code>myRecord</code>
	 * would expanded one level to look like <code>{T aField,...}</code> for
	 * some (potentially nominal) element type T.
	 *
	 * @param type
	 *            The type to be expanded
	 * @return null if given type is not an effective record
	 * @throws ResolveError
	 */
	public Type.EffectiveRecord expandAsEffectiveRecord(Type type) throws ResolveError {
		if (type instanceof Type.EffectiveRecord) {
			// This type is already an effective record. Therefore, no need to
			// do anything.
			return (Type.EffectiveRecord) type;
		} else {
			// This type may be an effective record. To find out, we need to
			// expand one level of nominal type information
			type = expandOneLevel(type);
			if (type instanceof Type.EffectiveRecord) {
				return (Type.EffectiveRecord) type;
			} else {
				return null;
			}
		}
	}

	/**
	 * Assuming given type is an effective array of some sort, expand to ensure
	 * array structure is visible. For example a type <code>myArray</code> would
	 * expanded one level to look like <code>T[]</code> for some (potentially
	 * nominal) element type T.
	 *
	 * @param type
	 *            The type to be expanded
	 * @return null if type is no an effective array
	 * @throws ResolveError
	 */
	public Type.EffectiveArray expandAsEffectiveArray(Type type) throws ResolveError {
		if (type instanceof Type.EffectiveArray) {
			// This type is already an effective array. Therefore, no need to
			// do anything.
			return (Type.EffectiveArray) type;
		} else {
			// This type may be an effective array. To find out, we need to
			// expand one level of nominal type information
			type = expandOneLevel(type);
			if(type instanceof Type.EffectiveArray) {
				return (Type.EffectiveArray) type;
			} else {
				return null;
			}
		}
	}

	/**
	 * Assuming given type is an effective reference of some sort, expand to
	 * ensure reference structure is visible. For example a type
	 * <code>myRef</code> would expanded one level to look like <code>&T</code>
	 * for some (potentially nominal) element type T.
	 *
	 * @param type
	 *            The type to be expanded
	 * @return
	 * @throws ResolveError
	 */
	public Type.Reference expandAsReference(Type type) throws ResolveError {
		if (type instanceof Type.Reference) {
			// This type is already a reference. Therefore, no need to
			// do anything.
			return (Type.Reference) type;
		} else {
			// This type may be a reference. To find out, we need to
			// expand one level of nominal type information
			type = expandOneLevel(type);
			if (type instanceof Type.Reference) {
				return (Type.Reference) type;
			} else {
				return null;
			}
		}
	}

	/**
	 * Assuming given type is an effective function or method type of some sort,
	 * expand to ensure structure is visible.
	 *
	 * @param type
	 *            The type to be expanded
	 * @return
	 * @throws ResolveError
	 */
	public Type.FunctionOrMethod expandAsFunctionOrMethod(Type type) throws ResolveError {
		if (type instanceof Type.FunctionOrMethod) {
			// This type is already a function or method. Therefore, no need to
			// do anything.
			return (Type.FunctionOrMethod) type;
		} else {
			// This type may be a reference. To find out, we need to
			// expand one level of nominal type information
			type = expandOneLevel(type);
			if (type instanceof Type.FunctionOrMethod) {
				return (Type.FunctionOrMethod) type;
			} else {
				return null;
			}
		}
	}

	// =============================================================
	// Subtype Operator(s)
	// =============================================================

	/**
	 * Determine whether type <code>t2</code> is an <i>explicit coercive
	 * subtype</i> of type <code>t1</code>.
	 *
	 * @throws ResolveError
	 *             If a named type within either of the operands cannot be
	 *             resolved within the enclosing project.
	 */
	public boolean isExplicitCoerciveSubtype(Type t1, Type t2, LifetimeRelation lr) throws ResolveError {
		Automaton a1 = toAutomaton(t1);
		Automaton a2 = toAutomaton(t2);
		ExplicitCoercionOperator relation = new ExplicitCoercionOperator(a1,a2,lr);
		return relation.isSubtype(0, 0);
	}

	/**
	 * Determine whether type <code>t2</code> is an <i>explicit coercive
	 * subtype</i> of type <code>t1</code>.
	 *
	 * @throws ResolveError
	 *             If a named type within either of the operands cannot be
	 *             resolved within the enclosing project.
	 */
	public boolean isExplicitCoerciveSubtype(Type t1, Type t2) throws ResolveError {
		return isExplicitCoerciveSubtype(t1, t2, LifetimeRelation.EMPTY);
	}

	/**
	 * Determine whether type <code>t2</code> is a <i>subtype</i> of type
	 * <code>t1</code> (written t1 :> t2). In other words, whether the set of
	 * all possible values described by the type <code>t2</code> is a subset of
	 * that described by <code>t1</code>.
	 *
	 * @throws ResolveError
	 *             If a named type within either of the operands cannot be
	 *             resolved within the enclosing project.
	 */
	public boolean isSubtype(Type t1, Type t2, LifetimeRelation lr) throws ResolveError {
		// FIXME: These two lines are a hack; they help, but are not a general
		// solution. See #696.
		t1 = expandOneLevel(t1);
		t2 = expandOneLevel(t2);
		// END
		Automaton a1 = toAutomaton(t1);
		Automaton a2 = toAutomaton(t2);
		SubtypeOperator relation = new SubtypeOperator(a1,a2,lr);
		return relation.isSubtype(0, 0);
	}

	/**
	 * Determine whether type <code>t2</code> is a <i>subtype</i> of type
	 * <code>t1</code> (written t1 :> t2). In other words, whether the set of
	 * all possible values described by the type <code>t2</code> is a subset of
	 * that described by <code>t1</code>.
	 *
	 * @throws ResolveError
	 *             If a named type within either of the operands cannot be
	 *             resolved within the enclosing project.
	 */
	public boolean isSubtype(Type t1, Type t2) throws ResolveError {
		return isSubtype(t1, t2, LifetimeRelation.EMPTY);
	}

	/**
	 * Expand a given syntactic type by exactly one level.
	 *
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws ResolveError
	 */
	public Type expandOneLevel(Type type) throws ResolveError {
		return expandOneLevel(type,true);
	}

	/**
	 * Expand a given syntactic type by exactly one level, according to a given
	 * goal of either maximising or minimising the resulting type.
	 *
	 * @param type
	 *            type to be expanded
	 * @param maximise
	 *            If true, then maximise resulting type
	 * @return
	 * @throws ResolveError
	 */
	private Type expandOneLevel(Type type, boolean maximise) throws ResolveError {
		try {
			if (type instanceof Type.Nominal) {
				Type.Nominal nt = (Type.Nominal) type;
				NameID nid = nt.name();
				Path.Entry<WyilFile> p = project.get(nid.module(), WyilFile.ContentType);
				if (p == null) {
					throw new ResolveError("name not found: " + nid);
				}
				WyilFile.Type td = p.read().type(nid.name());
				if(maximise || td.getInvariant().isEmpty()) {
					return expandOneLevel(td.type(),maximise);
				} else {
					return Type.T_VOID;
				}
			} else if (type instanceof Type.Leaf
					|| type instanceof Type.Reference
					|| type instanceof Type.Array
					|| type instanceof Type.Record
					|| type instanceof Type.FunctionOrMethod) {
				return type;
			} else if(type instanceof Type.Negation) {
				Type.Negation nt = (Type.Negation) type;
				Type element = expandOneLevel(nt.element(),!maximise);
				return Type.Negation(element);
			} else if(type instanceof Type.Union){
				Type.Union ut = (Type.Union) type;
				Type[] ut_bounds = ut.bounds();
				Type[] bounds = new Type[ut_bounds.length];
				for (int i=0;i!=ut_bounds.length;++i) {
					bounds[i] = expandOneLevel(ut_bounds[i],maximise);
				}
				return Type.Union(bounds);
			} else {
				Type.Intersection it = (Type.Intersection) type;
				Type[] it_bounds = it.bounds();
				Type[] bounds = new Type[it_bounds.length];
				for (int i=0;i!=it_bounds.length;++i) {
					bounds[i] = expandOneLevel(it_bounds[i],maximise);
				}
				return Type.Intersection(bounds);
			}
		} catch (IOException e) {
			throw new ResolveError(e.getMessage(), e);
		}
	}

	// =============================================================
	// Automaton Representation
	// =============================================================

	public static final class FunctionOrMethodState implements Comparable<FunctionOrMethodState> {
		public final int numParams;
		public final ArrayList<String> contextLifetimes;
		public final ArrayList<String> lifetimeParameters;

		public FunctionOrMethodState(int numParams, String[] contextLifetimes, String[] lifetimeParameters) {
			this.numParams = numParams;
			this.contextLifetimes = new ArrayList<>();
			for (int i = 0; i != contextLifetimes.length; ++i) {
				this.contextLifetimes.add(contextLifetimes[i]);
			}
			this.contextLifetimes.remove("*");
			Collections.sort(this.contextLifetimes);
			this.lifetimeParameters = new ArrayList<>();
			for (int i = 0; i != lifetimeParameters.length; ++i) {
				this.lifetimeParameters.add(lifetimeParameters[i]);
			}
		}

		@Override
		public int hashCode() {
			return 31 * (31 * numParams + contextLifetimes.hashCode()) + lifetimeParameters.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			FunctionOrMethodState other = (FunctionOrMethodState) obj;
			if (this.numParams != other.numParams) {
				return false;
			}
			if (!this.contextLifetimes.equals(other.contextLifetimes)) {
				return false;
			}
			if (!this.lifetimeParameters.equals(other.lifetimeParameters)) {
				return false;
			}
			return true;
		}

		@Override
		public int compareTo(FunctionOrMethodState other) {
			int r = Integer.compare(this.numParams, other.numParams);
			if (r != 0) {
				return r;
			}
			r = compareLists(this.contextLifetimes, other.contextLifetimes);
			if (r != 0) {
				return r;
			}
			return compareLists(this.lifetimeParameters, other.lifetimeParameters);
		}

		private static int compareLists(List<String> my, List<String> other) {
			Iterator<String> it1 = my.iterator();
			Iterator<String> it2 = other.iterator();
			while (true) {
				if (it1.hasNext()) {
					if (it2.hasNext()) {
						int r = it1.next().compareTo(it2.next());
						if (r != 0) {
							return r;
						}
					} else {
						return 1;
					}
				} else if (it2.hasNext()) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

	public static final class RecordState extends ArrayList<String> {
		public final boolean isOpen;

		public RecordState(boolean isOpen) {
			this.isOpen = isOpen;
		}

		public RecordState(boolean isOpen, Collection<String> values) {
			super(values);
			this.isOpen = isOpen;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof RecordState) {
				RecordState s = (RecordState) o;
				return isOpen == s.isOpen && super.equals(s);
			}
			return false;
		}
	}

	/**
	 * Expand a given type by inlining all visible nominal information. For
	 * example:
	 *
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * type listnat is [nat]
	 * </pre>
	 *
	 * Expanding the type <code>[nat]</code> will result in the type
	 * <code>[int]</code>. The key challenge here lies in handling nominal types
	 * when they are encountered. We need to determine where the type is
	 * located, and then incorporate the (expanded) body of that type into this
	 * type. In some cases, we're not permitted to inline the body because it's
	 * not visible to this file (e.g. it is marked as private).
	 *
	 * @param type
	 * @return
	 */
	public Automaton toAutomaton(Type type) throws ResolveError {

		// FIXME: this method should be hidden

		if(type == null) {
			throw new IllegalArgumentException();
		}
		ArrayList<Automaton.State> states = new ArrayList<>();
		HashMap<NameID,Integer> roots = new HashMap<>();
		toAutomatonHelper(type, true, states, roots);
		Automaton automaton = new Automaton(states);
		return normalise(automaton);
	}

	/**
	 * <p>
	 * Expand the given type by loading it's contents into the list of states.
	 * The location of nominal types, when encountered, are cached as "roots" in
	 * order to prevent infinite expansion and, instead, to construct a
	 * recursive cycle.
	 * </p>
	 *
	 * <p>
	 * Expansion proceeds by exploring every compound type until either a leaf
	 * or nominal type is encountered. In the case of a leaf type being
	 * encoutered, we can just add this directly as it cannot be further
	 * expanded. In the case of a nominal type, we then try to inline its body
	 * if permitted. Thus, for a type which contains no nominal types, this
	 * function will return an identical type.
	 * </p>
	 *
	 * @param type
	 *            The type currently being expanded.
	 * @param sign
	 *            Indicates the current "sign" with respect to negation types.
	 *            This is critical for expanding nominal types correctly in the
	 *            presence of constraints.
	 * @param states
	 *            The list of states being accumulated which will eventually
	 *            form the original type being exapnded.
	 * @param roots
	 *            The cache of previously inline nominal types which is
	 *            necessary to break recursive cycles.
	 * @return
	 * @throws IOException
	 */
	private int toAutomatonHelper(Type type, boolean sign, ArrayList<Automaton.State> states,
			HashMap<NameID, Integer> roots) throws ResolveError {
		// First, handle nominals (which are challenging) and primitive types
		// (which are simple).
		if(type instanceof Type.Nominal) {
			Type.Nominal tt = (Type.Nominal) type;
			NameID nid = tt.name();
			// First, check whether or not we have already expanded this type.
			// If so, then we form a recursive cycle.
			if(roots.containsKey(nid)) {
				// Yes. we have already expanded it. Therefore we simply need to
				// return the cached index to form the recursive cycle.
				return roots.get(nid);
			} else {
				// At this point, need to find the corresponding declaration.
				try {
					WyilFile mi = project.get(nid.module(),WyilFile.ContentType).read();
					WyilFile.Type td = mi.type(nid.name());
					if(td == null) {
						// This indicates the name is valid, but does not
						// correspond to a type per se. It must correspond to
						// something else, like a constant or method
						// declaration.
						throw new ResolveError("unknown type");
					} else if (!sign && td.getInvariant().size() > 0) {
						// In this specially case, we are asking for !T where T
						// is a constrained nominal type. In this case, the
						// correct translation of !T is always any. We return
						// void here, so that we end up with !void.
						states.add(new State(K_VOID, null, true, Automaton.NOCHILDREN));
						return states.size() - 1;
					} else {
						// Now, store the root of this expansion so that it can
						// be used subsequently to form a recursive cycle.
						roots.put(nid, states.size());
						return toAutomatonHelper(td.type(), sign, states, roots);
					}
				} catch (IOException e) {
					throw new ResolveError(e.getMessage(), e);
				}
			}
		} else if(type instanceof Type.Leaf) {
			// In ther case of a leaf node we simply copy over its states into
			// the list of states being accumulated.
			return append((Type.Leaf) type,states);
		}

		// Now handle all non-primitive types which need to be expanded in some
		// way,
		int myIndex = states.size();
		int myKind;
		int[] myChildren;
		Object myData = null;
		boolean myDeterministic = true;

		states.add(null); // reserve space for me!

		if (type instanceof Type.Array) {
			Type.Array tt = (Type.Array) type;
			myChildren = new int[1];
			myChildren[0] = toAutomatonHelper(tt.element(),sign,states,roots);
			myKind = K_ARRAY;
		} else if(type instanceof Type.Record) {
			Type.Record tt = (Type.Record) type;
			String[] names = tt.getFieldNames();
			RecordState fields = new RecordState(tt.isOpen(),Arrays.asList(names));
			Collections.sort(fields);
			myKind = K_RECORD;
			myChildren = new int[fields.size()];
			for (int i = 0; i != myChildren.length; ++i) {
				String field = fields.get(i);
				myChildren[i] = toAutomatonHelper(tt.getField(field), sign,states, roots);
			}
			myData = fields;
		} else if(type instanceof Type.Reference) {
			Type.Reference tt = (Type.Reference) type;
			myChildren = new int[1];
			myChildren[0] = toAutomatonHelper(tt.element(),sign,states,roots);
			myData = tt.lifetime();
			myKind = K_REFERENCE;
		} else if(type instanceof Type.Negation) {
			Type.Negation tt = (Type.Negation) type;
			myChildren = new int[1];
			myChildren[0] = toAutomatonHelper(tt.element(),!sign,states,roots);
			myKind = K_NEGATION;
		} else if(type instanceof Type.Union) {
			Type.Union tt = (Type.Union) type;
			Type[] bounds = tt.bounds();
			myChildren = new int[bounds.length];
			int i = 0;
			for(Type b : bounds) {
				myChildren[i++] = toAutomatonHelper(b,sign,states,roots);
			}
			myKind = K_UNION;
		} else if(type instanceof Type.Intersection) {
			Type.Intersection it = (Type.Intersection) type;
			// FIXME: this is an ugly hack. But it works for now, and eventually
			// I'll fix it :)
			Type[] tt_bounds = it.bounds();
			Type[] ut_bounds = new Type[tt_bounds.length];
			for (int i = 0; i != tt_bounds.length; ++i) {
				ut_bounds[i] = Type.Negation(tt_bounds[i]);
			}
			myChildren = new int[1];
			myChildren[0] = toAutomatonHelper(Type.Union(ut_bounds), !sign, states, roots);
			myKind = K_NEGATION;
		} else if(type instanceof Type.FunctionOrMethod) {
			Type.FunctionOrMethod tt = (Type.FunctionOrMethod) type;
			Type[] tt_params = tt.params();
			Type[] tt_returns = tt.returns();
			int tt_params_size = tt_params.length;
			int tt_returns_size = tt_returns.length;
			myChildren = new int[tt_params_size+tt_returns_size];
			for(int i=0;i!=tt_params_size;++i) {
				myChildren[i] = toAutomatonHelper(tt_params[i],sign,states,roots);
			}
			for(int i=0;i!=tt_returns_size;++i) {
				myChildren[i+tt_params_size] = toAutomatonHelper(tt_returns[i],sign,states,roots);
			}
			myData = new FunctionOrMethodState(tt_params_size, getContextLifetimes(tt), getLifetimeParams(tt));
			if(tt instanceof Type.Function) {
				myKind = K_FUNCTION;
			} else if(tt instanceof Type.Method){
				myKind = K_METHOD;
			} else {
				myKind = K_PROPERTY;
			}

		}else {
			// FIXME: Probably need to handle function and method types here
			throw new ResolveError("unknown type encountered: " + type);
		}

		states.set(myIndex, new Automaton.State(myKind, myData,myDeterministic, myChildren));

		return myIndex;
	}

	private static String[] getContextLifetimes(Type.FunctionOrMethod fm) {
		if(fm instanceof Type.Method) {
			Type.Method m = (Type.Method) fm;
			return m.contextLifetimes();
		} else {
			return new String[0];
		}
	}

	private static String[] getLifetimeParams(Type.FunctionOrMethod fm) {
		if(fm instanceof Type.Method) {
			Type.Method m = (Type.Method) fm;
			return m.lifetimeParams();
		} else {
			return new String[0];
		}
	}

	private static int append(Type.Leaf type, ArrayList<Automaton.State> states) {
		int kind;
		if (type == Type.T_ANY) {
			kind = K_ANY;
		} else if (type == Type.T_VOID) {
			kind = K_VOID;
		} else if (type == Type.T_NULL) {
			kind = K_NULL;
		} else if (type == Type.T_BOOL) {
			kind = K_BOOL;
		} else if (type == Type.T_BYTE) {
			kind = K_BYTE;
		} else if (type == Type.T_INT) {
			kind = K_INT;
		} else {
			kind = K_META;
		}
		states.add(new Automaton.State(kind));
		return states.size() - 1;
	}

	/**
	 * <p>
	 * The following algorithm simplifies a type. For example:
	 * </p>
	 *
	 * <pre>
	 * define InnerList as null|{int data, OuterList next}
	 * define OuterList as null|{int data, InnerList next}
	 * </pre>
	 * <p>
	 * This type is simplified into the following (equivalent) form:
	 * </p>
	 *
	 * <pre>
	 * define LinkedList as null|{int data, LinkedList next}
	 * </pre>
	 * <p>
	 * The simplification algorithm is made up of several different procedures
	 * which operate on the underlying <i>automaton</i> representing the type:
	 * </p>
	 * <ol>
	 * <li><b>Extraction.</b> Here, sub-components unreachable from the root are
	 * eliminated.</li>
	 * <li><b>Simplification.</b> Here, basic simplifications are applied. For
	 * example, eliminating unions of unions.</li>
	 * <li><b>Minimisation.</b>Here, equivalent states are merged together.</li>
	 * <li><b>Canonicalisation.</b> A canonical form of the type is computed</li>
	 * </ol>
	 *
	 * is based on the well-known algorithm for minimising a DFA (see e.g. <a
	 * href="http://en.wikipedia.org/wiki/DFA_minimization">[1]</a>). </p>
	 * <p>
	 * The algorithm operates by performing a subtype test of each node against
	 * all others. From this, we can identify nodes which are equivalent under
	 * the subtype operator. Using this information, the type is reconstructed
	 * such that for each equivalence class only a single node is created.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> this algorithm does not put the type into a canonical form.
	 * Additional work is necessary to do this.
	 * </p>
	 *
	 * @param afterType
	 * @return
	 */
	private static Automaton normalise(Automaton automaton) {
		//normalisedCount++;
		//unminimisedCount += automaton.size();
		TypeAlgorithms.simplify(automaton);
		// TODO: extract in place to avoid allocating data unless necessary
		automaton = Automata.extract(automaton, 0);
		// TODO: minimise in place to avoid allocating data unless necessary
		automaton = Automata.minimise(automaton);
		//minimisedCount += automaton.size();
		return automaton;
	}


	/**
	 * The following method constructs a string representation of the underlying
	 * automaton. This representation may be an expanded version of the underling
	 * graph, since one cannot easily represent aliasing in the type graph in a
	 * textual manner.
	 *
	 * @param automaton
	 *            --- the automaton being turned into a string.
	 * @return --- string representation of automaton.
	 */
	private final static String toString(Automaton automaton) {
		// First, we need to find the headers of the computation. This is
		// necessary in order to mark the start of a recursive type.
		BitSet headers = new BitSet(automaton.size());
		BitSet visited = new BitSet(automaton.size());
		BitSet onStack = new BitSet(automaton.size());
		findHeaders(0, visited, onStack, headers, automaton);
		visited.clear();
		String[] titles = new String[automaton.size()];
		int count = 0;
		for (int i = 0; i != automaton.size(); ++i) {
			if (headers.get(i)) {
				titles[i] = headerTitle(count++);
			}
		}
		return toString(0, visited, titles, automaton);
	}

	/**
	 * The following method constructs a string representation of the underlying
	 * automaton. This representation may be an expanded version of the underling
	 * graph, since one cannot easily represent aliasing in the type graph in a
	 * textual manner.
	 *
	 * @param index
	 *            --- the index to start from
	 * @param visited
	 *            --- the set of vertices already visited.
	 * @param headers
	 *            --- an array of strings which identify the name to be given to
	 *            each header.
	 * @param automaton
	 *            --- the automaton being turned into a string.
	 * @return --- string representation of automaton.
	 */
	private final static String toString(int index, BitSet visited,
			String[] headers, Automaton automaton) {
		if (visited.get(index)) {
			// node already visited
			return headers[index];
		} else if(headers[index] != null) {
			visited.set(index);
		}
		State state = automaton.states[index];
		String middle;
		switch (state.kind) {
		case K_VOID:
			return "void";
		case K_ANY:
			return "any";
		case K_NULL:
			return "null";
		case K_BOOL:
			return "bool";
		case K_BYTE:
			return "byte";
		case K_INT:
			return "int";
		case K_ARRAY: {
			middle = toString(state.children[0], visited, headers, automaton)
					+ "[]";
			break;
		}
		case K_NOMINAL:
			middle = state.data.toString();
			break;
		case K_REFERENCE:
			middle = "&";
			String lifetime = (String) state.data;
			if (!"*".equals(lifetime)) {
				middle += lifetime + ":";
			}
			middle += toString(state.children[0], visited, headers, automaton);
			break;
		case K_NEGATION: {
			middle = "!" + toBracesString(state.children[0], visited, headers, automaton);
			break;
		}
		case K_UNION: {
			int[] children = state.children;
			middle = "";
			for (int i = 0; i != children.length; ++i) {
				if(i != 0 || children.length == 1) {
					middle += "|";
				}
				middle += toBracesString(children[i], visited, headers, automaton);
			}
			break;
		}
		case K_RECORD: {
			// labeled nary node
			middle = "{";
			int[] children = state.children;
			RecordState fields = (RecordState) state.data;
			for (int i = 0; i != fields.size(); ++i) {
				if (i != 0) {
					middle += ",";
				}
				middle += toString(children[i], visited, headers, automaton) + " " + fields.get(i);
			}
			if(fields.isOpen) {
				if(children.length > 0) {
					middle = middle + ",...}";
				} else {
					middle = middle + "...}";
				}
			} else {
				middle = middle + "}";
			}
			break;
		}
		case K_METHOD:
		case K_FUNCTION:
		case K_PROPERTY: {
			String parameters = "";
			int[] children = state.children;
			FunctionOrMethodState data = (FunctionOrMethodState) state.data;
			int numParameters = data.numParams;
			for (int i = 0; i != numParameters; ++i) {
				if (i!=0) {
					parameters += ",";
				}
				parameters += toString(children[i], visited, headers, automaton);
			}
			String returns = "";
			for (int i = numParameters; i != children.length; ++i) {
				if (i!=numParameters) {
					returns += ",";
				}
				returns += toString(children[i], visited, headers, automaton);
			}
			StringBuilder sb = new StringBuilder();
			if(state.kind == K_FUNCTION) {
				sb.append("function");
			} else if(state.kind == K_METHOD) {
				sb.append("method");
			} else {
				sb.append("property");
			}
			if (!data.contextLifetimes.isEmpty()) {
				sb.append('[');
				boolean first = true;
				for (String l : data.contextLifetimes) {
					if (!first) {
						sb.append(',');
					} else {
						first = false;
					}
					sb.append(l);
				}
				sb.append(']');
			}
			if (!data.lifetimeParameters.isEmpty()) {
				sb.append('<');
				boolean first = true;
				for (String l : data.lifetimeParameters) {
					if (!first) {
						sb.append(',');
					} else {
						first = false;
					}
					sb.append(l);
				}
				sb.append('>');
			}
			sb.append('(');
			sb.append(parameters);
			sb.append(")->(");
			sb.append(returns);
			sb.append(')');
			middle = sb.toString();
			break;
		}
		default:
			throw new IllegalArgumentException("Invalid type encountered (kind: " + state.kind +")");
		}

		// Finally, check whether this is a header node, or not. If it is a
		// header then we need to insert the recursive type.
		String header = headers[index];
		if(header != null) {
			// The following case is interesting. Basically, we'll never revisit
			// a header. Therefore, if we have multiple edges landing on a
			// header we must update the header string to represent the full
			// type reachable from the header.
			String r = header + "<" + middle + ">";
			headers[index] = r;
			return r;
		} else {
			return middle;
		}
	}

	private final static String toBracesString(int index, BitSet visited,
			String[] headers, Automaton automaton) {
		if (visited.get(index)) {
			// node already visited
			return headers[index];
		}
		String middle = toString(index,visited,headers,automaton);
		State state = automaton.states[index];
		switch(state.kind) {
			case K_UNION:
			case K_PROPERTY:
			case K_FUNCTION:
			case K_METHOD:
				return "(" + middle + ")";
			default:
				return middle;
		}
	}

	/**
	 * The following method traverses the graph using a depth-first
	 * search to identify nodes which are "loop headers". That is, they are the
	 * target of one or more recursive edges in the graph.
	 *
	 * @param index
	 *            --- the index to search from.
	 * @param visited
	 *            --- the set of vertices already visited.
	 * @param onStack
	 *            --- the set of nodes currently on the DFS path from the root.
	 * @param headers
	 *            --- header nodes discovered during this search are set to true
	 *            in this bitset.
	 * @param automaton
	 *            --- the automaton we're traversing.
	 */
	private final static void findHeaders(int index, BitSet visited,
			BitSet onStack, BitSet headers, Automaton automaton) {
		if(visited.get(index)) {
			// node already visited
			if(onStack.get(index)) {
				headers.set(index);
			}
			return;
		}
		onStack.set(index);
		visited.set(index);
		State state = automaton.states[index];
		for(int child : state.children) {
			findHeaders(child,visited,onStack,headers,automaton);
		}
		onStack.set(index,false);
	}

	private static final char[] headers = { 'X','Y','Z','U','V','W','L','M','N','O','P','Q','R','S','T'};
	private static String headerTitle(int count) {
		String r = Character.toString(headers[count%headers.length]);
		int n = count / headers.length;
		if(n > 0) {
			return r + n;
		} else {
			return r;
		}
	}


	public static final byte K_VOID = 0;
	public static final byte K_ANY = 1;
	public static final byte K_META = 2;
	public static final byte K_NULL = 3;
	public static final byte K_BOOL = 4;
	public static final byte K_BYTE = 5;
	public static final byte K_INT = 7;
	public static final byte K_ARRAY = 12;
	public static final byte K_REFERENCE = 14;
	public static final byte K_RECORD = 15;
	public static final byte K_UNION = 16;
	public static final int K_INTERSECTION = 17;
	public static final byte K_NEGATION = 18;
	public static final byte K_FUNCTION = 19;
	public static final byte K_METHOD = 20;
	public static final byte K_NOMINAL = 21;
	public static final byte K_PROPERTY = 22;
}
