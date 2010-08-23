package wyil.testing;

import org.junit.*;
import static org.junit.Assert.fail;

import wyil.lang.Type;
import static wyil.lang.Type.*;

public class TypeTests {
	@Test public void leastUpperBound_1() {
		Type[][] types = {
			{T_VOID, T_VOID, T_VOID},
			{T_BOOL, T_VOID, T_BOOL},
			{T_INT, T_VOID, T_INT},
			{T_REAL, T_VOID, T_REAL},
			{T_LIST(T_VOID), T_VOID, T_LIST(T_VOID)},
			{T_LIST(T_BOOL), T_VOID, T_LIST(T_BOOL)},
			{T_LIST(T_INT), T_VOID, T_LIST(T_INT)},
			{T_LIST(T_REAL), T_VOID, T_LIST(T_REAL)},
			{T_SET(T_VOID), T_VOID, T_SET(T_VOID)},
			{T_SET(T_BOOL), T_VOID, T_SET(T_BOOL)},
			{T_SET(T_INT), T_VOID, T_SET(T_INT)},
			{T_SET(T_REAL), T_VOID, T_SET(T_REAL)},
			// ============================			
			{T_VOID, T_BOOL, T_BOOL},
			{T_BOOL, T_BOOL, T_BOOL},
			{T_INT, T_BOOL, new Type.Union(T_INT,T_BOOL)},
			{T_REAL, T_BOOL, new Type.Union(T_REAL,T_BOOL)},
			{T_LIST(T_VOID), T_BOOL, new Type.Union(T_LIST(T_VOID),T_BOOL)},
			{T_LIST(T_BOOL), T_BOOL, new Type.Union(T_LIST(T_BOOL),T_BOOL)},
			{T_LIST(T_INT), T_BOOL, new Type.Union(T_LIST(T_INT),T_BOOL)},
			{T_LIST(T_REAL), T_BOOL, new Type.Union(T_LIST(T_REAL),T_BOOL)},
			{T_SET(T_VOID), T_BOOL, new Type.Union(T_SET(T_VOID),T_BOOL)},
			{T_SET(T_BOOL), T_BOOL, new Type.Union(T_SET(T_BOOL),T_BOOL)},
			{T_SET(T_INT), T_BOOL, new Type.Union(T_SET(T_INT),T_BOOL)},
			{T_SET(T_REAL), T_BOOL, new Type.Union(T_SET(T_REAL),T_BOOL)},
			// ============================
			{T_VOID, T_INT, T_INT},
			{T_BOOL, T_INT, new Type.Union(T_INT,T_BOOL)},
			{T_INT, T_INT, T_INT},
			{T_REAL, T_INT, T_REAL},
			{T_LIST(T_VOID), T_INT, new Type.Union(T_LIST(T_VOID),T_INT)},
			{T_LIST(T_BOOL), T_INT, new Type.Union(T_LIST(T_BOOL),T_INT)},
			{T_LIST(T_INT), T_INT, new Type.Union(T_LIST(T_INT),T_INT)},
			{T_LIST(T_REAL), T_INT, new Type.Union(T_LIST(T_REAL),T_INT)},
			{T_SET(T_VOID), T_INT, new Type.Union(T_SET(T_VOID),T_INT)},
			{T_SET(T_BOOL), T_INT, new Type.Union(T_SET(T_BOOL),T_INT)},
			{T_SET(T_INT), T_INT, new Type.Union(T_SET(T_INT),T_INT)},
			{T_SET(T_REAL), T_INT, new Type.Union(T_SET(T_REAL),T_INT)},
			// ============================
			{T_VOID, T_REAL, T_REAL},
			{T_BOOL, T_REAL, new Type.Union(T_REAL,T_BOOL)},
			{T_INT, T_REAL, T_REAL},
			{T_REAL, T_REAL, T_REAL},
			{T_LIST(T_VOID), T_REAL, new Type.Union(T_LIST(T_VOID),T_REAL)},
			{T_LIST(T_BOOL), T_REAL, new Type.Union(T_LIST(T_BOOL),T_REAL)},
			{T_LIST(T_INT), T_REAL, new Type.Union(T_LIST(T_INT),T_REAL)},
			{T_LIST(T_REAL), T_REAL, new Type.Union(T_LIST(T_REAL),T_REAL)},
			{T_SET(T_VOID), T_REAL, new Type.Union(T_SET(T_VOID),T_REAL)},
			{T_SET(T_BOOL), T_REAL, new Type.Union(T_SET(T_BOOL),T_REAL)},
			{T_SET(T_INT), T_REAL, new Type.Union(T_SET(T_INT),T_REAL)},
			{T_SET(T_REAL), T_REAL, new Type.Union(T_SET(T_REAL),T_REAL)},
			// ============================
		};
		for(Type[] test : types) {
			Type lub = leastUpperBound(test[0],test[1]);
			if(!lub.equals(test[2])) {
				fail(test[0] + " + " + test[1] + " should be " + test[2]
						+ ", not " + lub);
			}
		}
	}
}
