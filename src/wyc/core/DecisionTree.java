package wyc.core;

/**
 * Decision tres are used for the constraints induced by union types. The key
 * problem arises when we have a union type of two or more constrained types
 * which have related base types. For example:
 * 
 * <pre>
 * define pos as int where $ > 0
 * define neg as int where $ < 0
 * define posneg as pos|neg
 * 
 * boolean isPosNeg(any v):
 *     if v is posneg:
 *         return true
 *     else: 
 *         return false
 * </pre>
 * 
 * The issue here is that we cannot generate the obvious linearisation of the
 * constrained types. That is, the following expansion does not work:
 * 
 * <pre>
 * boolean isPosNeg(any v):
 *     // expand pos
 *     if v is int:
 *         if v > 0:
 *             return true
 *     // expand neg
 *     if v is int:
 *         if v < 0:
 *             return true
 *     return false
 * </pre>
 * 
 * The problem is that the first type test dominates the second, since they are
 * the same. What we need is to collect up constraints for same kinded types.
 * That is, to expand our example like so:
 * 
 * <pre>
 * boolean isPosNeg(any v):
 *     if v is int:
 *         if v > 0:
 *             return true
 *         else if v < 0:
 *             return true    
 *     return false
 * </pre>
 * 
 * Now, in fact, we may have to deal with arbitrary levels of nesting, thus
 * giving rise to the notion of a "tree". For example:
 * 
 * <pre>
 * define r1 as {int x, any y} where x > 0
 * define r2 as {int x, any y} where x < 0
 * define r3 as {int x, int y} where x < y
 * define recs as r1|r2|r3
 * 
 * boolean isRecs(any v):
 *     if v is recs:
 *         return true
 *     else: 
 *         return false
 * 
 * </pre>
 * 
 * In this case, we need to carefully narrow the type in question using a
 * succession of tests before checking the constraints.
 * 
 * <pre>
 * boolean isRecs(any v):
 *     if v is {int x, any y}:
 *         if v.x > 0:
 *             return true
 *         else if v.x < 0:
 *             return true
 *         else if v is {int x, int y}:
 *             if v.x < v.y:
 *                 return true
 *     return false
 * </pre>
 * 
 * Careful ordering of the tests can reduce the amount of work done.
 * 
 * @author David J. Pearce
 * 
 */
public class DecisionTree {

}
