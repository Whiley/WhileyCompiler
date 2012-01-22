package wyc.core;

import java.util.*;

import wyc.lang.UnresolvedType;
import wyil.lang.Code;
import wyil.lang.Type;
import wyil.lang.Block;

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
	private static class Node {		
		/**
		 * The test that will determines whether or not to traverse this node.
		 */
		private Type test;

		/**
		 * The children of this node. The types of all child nodes must be
		 * independent, otherwise the tree is malformed.
		 */
		private ArrayList<Node> children;
		
		/**
		 * Constraints for this level (if any)
		 */
		private Block constraints;
	}
	
	/**
	 * Root of the decision tree
	 */
	private Node root;
	
	/**
	 * Raw type representing the value on entry to the tree. 
	 */
	private final Type type;
	
	public DecisionTree(Type type) {
		this.type = type;
	}
	
	/**
	 * Add another constrained type into the tree.
	 * 
	 * @param type
	 *            --- raw type to be tested.
	 * 
	 * @param constraint
	 *            --- constraint which must hold for given type. This may be
	 *            null if there is no constraint.
	 */
	public void add(Type type, Block constraint) {
		
	}
	
	/**
	 * Flattern this tree into a sequential block. If the test passes then the
	 * block exists, otherwise a fail statement is reached.
	 * 
	 * @return
	 */
	public Block flattern() {
		return null;
	}
	
	/*
	 for (int i = 0; i != ut_bounds.size(); ++i) {				
				boolean lastBound = (i + 1) == ut_bounds.size();
				UnresolvedType b = ut_bounds.get(i);
				Type bt = resolver.resolveAsType(b, context).raw();
				Block p = generate(b, context);				
				if (p != null) {
					// In this case, there are constraints so we check the
					// negated type and branch over the constraint test if we
					// don't have the require type.
					String nextLabel = Block.freshLabel();
					constraints = true;					
					if (!lastBound) {
						blk.append(
								Code.IfType(raw, Code.THIS_SLOT,
										Type.Negation(bt), nextLabel),
								t.attributes());
					}
					blk.append(chainBlock(nextLabel, p));
					blk.append(Code.Goto(exitLabel));
					blk.append(Code.Label(nextLabel));
				} else {
					// In this case, there are no constraints so we can use a
					// direct type test.					
					blk.append(
							Code.IfType(raw, Code.THIS_SLOT, bt, exitLabel),
							t.attributes());
					raw = Type.intersect(raw, Type.Negation(bt));					
				}
			}

			if (constraints) {
				blk.append(Code.Fail("type constraint not satisfied"),
						ut.attributes());
				blk.append(Code.Label(exitLabel));
			} else {
				blk = null;
			}
 */
}
