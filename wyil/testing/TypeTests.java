package wyil.testing;

import org.junit.*;
import static org.junit.Assert.fail;

import java.util.*;
import wyil.util.Pair;
import wyil.lang.Type;
import static wyil.lang.Type.*;

/**
 * This class provides a sequence of unit tests for the Type class. The tests
 * are structured by drawing an equivalence between values and types. That is, a
 * type represents a set of values. Now, when we perform operations on types
 * (e.g. leastUpperBound), we are performing a set operation (e.g set union).
 * Thus, the tests mirror the type operations with set operations and check that
 * it all works out.
 * 
 * @author djp
 * 
 */
public class TypeTests {
	private HashSet EMPTYSET = new HashSet();

	private HashSet BOOLS = new HashSet() {
		{
			add(Boolean.TRUE);
			add(Boolean.FALSE);
		}
	};

	private HashSet INTEGERS = new HashSet() {
		{
			for (int i = -3; i < 3; ++i) {
				add(i);
			}
		}
	};

	private HashSet REALS = new HashSet() {
		{
			for (double i = -1; i < 1; i = i + 0.2) {
				add(i);
			}
			addAll(INTEGERS);
		}
	};

	private Object[][] tests = {
			{ T_VOID, EMPTYSET },
			{ T_BOOL, BOOLS },
			{ T_INT, INTEGERS },
			{ T_REAL, union(REALS, INTEGERS) },
			// sets
			{ T_SET(T_VOID), set(EMPTYSET) },
			{ T_SET(T_BOOL), set(BOOLS) },
			{ T_SET(T_INT), set(INTEGERS) },
			{ T_SET(T_REAL), set(REALS) },
			{ T_SET(new Type.Union(T_BOOL, T_INT)), set(union(BOOLS, INTEGERS)) },
			{ T_SET(new Type.Union(T_BOOL, T_REAL)), set(union(BOOLS, REALS)) },
			// lists
			{ T_LIST(T_VOID), list(EMPTYSET) },
			{ T_LIST(T_BOOL), list(BOOLS) },
			{ T_LIST(T_INT), list(INTEGERS) },
			{ T_LIST(T_REAL), list(REALS) },
			{ T_LIST(new Type.Union(T_BOOL, T_INT)),
					list(union(BOOLS, INTEGERS)) },
			{ T_LIST(new Type.Union(T_BOOL, T_REAL)), list(union(BOOLS, REALS)) }, };

	private HashSet ALLVALUES = new HashSet();

	{
		// Initialise TOP
		for (Object[] test : tests) {
			ALLVALUES.addAll((HashSet) test[1]);
		}
	}

	@Test
	public void leastUpperBound_1() {
		for (Object[] lhs : tests) {
			for (Object[] rhs : tests) {
				Type lhs_t = (Type) lhs[0];
				Type rhs_t = (Type) rhs[0];
				HashSet lhs_s = (HashSet) lhs[1];
				HashSet rhs_s = (HashSet) rhs[1];

				Type type = leastUpperBound(lhs_t, rhs_t);
				HashSet set = union(lhs_s, rhs_s);

				if (!checkAllIn(type, set)) {
					System.out.println("LHS: " + lhs_s);
					System.out.println("RHS: " + rhs_s);
					System.out.println("SET: " + set);
					fail(lhs_t + " + " + rhs_t + " != " + type + " (1)");
				}

				HashSet rem = difference(ALLVALUES, set);
				if (!checkNoneIn(type, rem)) {
					System.out.println("LHS: " + lhs_s);
					System.out.println("RHS: " + rhs_s);
					System.out.println("SET: " + set);
					System.out.println("REM: " + rem);
					fail(lhs_t + " + " + rhs_t + " != " + type + " (2)");
				}
			}
		}
	}

	@Test
	public void greatestLowerBound_1() {
		for (Object[] lhs : tests) {
			for (Object[] rhs : tests) {
				Type lhs_t = (Type) lhs[0];
				Type rhs_t = (Type) rhs[0];
				HashSet lhs_s = (HashSet) lhs[1];
				HashSet rhs_s = (HashSet) rhs[1];

				Type type = greatestLowerBound(lhs_t, rhs_t);
				HashSet set = intersect(lhs_s, rhs_s);

				if (!checkAllIn(type, set)) {
					System.out.println("LHS: " + lhs_s);
					System.out.println("RHS: " + rhs_s);
					System.out.println("SET: " + set);
					fail(lhs_t + " & " + rhs_t + " != " + type + " (1)");
				}

				HashSet rem = difference(ALLVALUES, set);
				if (!checkNoneIn(type, rem)) {
					System.out.println("LHS: " + lhs_s);
					System.out.println("RHS: " + rhs_s);
					System.out.println("SET: " + set);
					System.out.println("REM: " + rem);
					fail(lhs_t + " & " + rhs_t + " != " + type + " (2)");
				}
			}
		}
	}

	@Test
	public void greatestDifference_1() {
		for (Object[] lhs : tests) {
			for (Object[] rhs : tests) {
				Type lhs_t = (Type) lhs[0];
				Type rhs_t = (Type) rhs[0];
				HashSet lhs_s = (HashSet) lhs[1];
				HashSet rhs_s = (HashSet) rhs[1];

				Type type = greatestDifference(lhs_t, rhs_t);
				HashSet set = difference(lhs_s, rhs_s);

				if (!checkAllIn(type, set)) {
					System.out.println("LHS: " + lhs_s);
					System.out.println("RHS: " + rhs_s);
					System.out.println("SET: " + set);
					fail(lhs_t + " - " + rhs_t + " != " + type + " (1)");
				}

				// I can't run the following test, because the type system is
				// actually imprecise with respect to difference types.

				/*
				HashSet rem = difference(ALLVALUES, set);
				if (!checkNoneIn(type, rem)) {
					System.out.println("LHS: " + lhs_s);
					System.out.println("RHS: " + rhs_s);
					System.out.println("SET: " + set);
					System.out.println("REM: " + rem);
					fail(lhs_t + " - " + rhs_t + " != " + type + " (2)");
				}
				*/
			}
		}
	}

	/**
	 * Check that all values in set are subtype of the given type.
	 * 
	 * @param type
	 * @param set
	 * @return
	 */
	public static boolean checkAllIn(Type type, HashSet set) {
		for (Object o : set) {
			if (!isSubtype(type, o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check that no value in set is a subtype of the given type.
	 * 
	 * @param type
	 * @param set
	 * @return
	 */
	public static boolean checkNoneIn(Type type, HashSet set) {
		for (Object o : set) {
			if (isSubtype(type, o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check that the object o's type is a subtype of type. That is, check that
	 * o is (effectively) an instance of of type.
	 * 
	 * @param type
	 * @param o
	 * @return
	 */
	public static boolean isSubtype(Type type, Object o) {
		if (type == T_BOOL && o instanceof Boolean) {
			return true;
		} else if (type == T_INT && o instanceof Integer) {
			return true;
		} else if (type == T_REAL && o instanceof Integer) {
			return true;
		} else if (type == T_REAL && o instanceof Double) {
			return true;
		} else if (type instanceof Type.Set && o instanceof Collection) {
			Type.Set st = (Type.Set) type;
			Collection os = (Collection) o;
			for (Object e : os) {
				if (!isSubtype(st.element, e)) {
					return false;
				}
			}
			return true;
		} else if (type instanceof Type.List && o instanceof ArrayList) {
			Type.List st = (Type.List) type;
			ArrayList os = (ArrayList) o;
			for (Object e : os) {
				if (!isSubtype(st.element, e)) {
					return false;
				}
			}
			return true;
		} else if (type instanceof Union) {
			Union u = (Union) type;
			for (Type b : u.bounds) {
				if (isSubtype(b, o)) {
					return true;
				}
			}
		}

		return false;
	}

	public static HashSet set(HashSet s1) {
		HashSet r = new HashSet();
		for (Object o : s1) {
			HashSet set = new HashSet();
			set.add(o);
			r.add(set);
			ArrayList list = new ArrayList();
			list.add(o);
			r.add(list);
		}
		return r;
	}

	public static HashSet list(HashSet s1) {
		HashSet r = new HashSet();
		for (Object o : s1) {
			ArrayList list = new ArrayList();
			list.add(o);
			r.add(list);
		}
		return r;
	}

	public static HashSet union(HashSet s1, HashSet s2) {
		HashSet r = new HashSet();
		r.addAll(s1);
		r.addAll(s2);
		return r;
	}

	public static HashSet intersect(HashSet s1, HashSet s2) {
		HashSet r = new HashSet();
		for (Object o : s1) {
			if (s2.contains(o)) {
				r.add(o);
			}
		}
		return r;
	}

	public static HashSet difference(HashSet s1, HashSet s2) {
		HashSet r = new HashSet(s1);
		r.removeAll(s2);
		return r;
	}
}
