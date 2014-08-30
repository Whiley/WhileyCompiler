package wyc.builder;

import java.util.*;

import wyil.lang.Code;
import wyil.lang.CodeUtils;
import wyil.lang.Codes;
import wyil.lang.Type;

/**
 * Decision trees are used for the constraints induced by union types. The key
 * problem arises when we have a union type of two or more constrained types
 * which have related base types. For example:
 * 
 * <pre>
 * type pos is (int x) where x > 0
 * type neg is (int x) where x < 0
 * type posneg is pos | neg
 * 
 * function isPosNeg(any v) => bool:
 *     if v is posneg:
 *         return true
 *     else: 
 *         return false
 * </pre>
 * 
 * The issue here is that we cannot (in general) generate the obvious
 * linearisation of the constrained types. That is, the following expansion does
 * not work in general (although it's OK for this particular case):
 * 
 * <pre>
 * function isPosNeg(any v) => bool:
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
 * function isPosNeg(any v) => bool:
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
 * type r1 is {int x, any y} where x > 0
 * type r2 is {int x, any y} where x < 0
 * type r3 is {int x, int y} where x < y
 * type recs as r1 | r2 | r3
 * 
 * function isRecs(any v) => bool:
 *     if v is recs:
 *         return true
 *     else: 
 *         return false
 * </pre>
 * 
 * In this case, we need to carefully narrow the type in question using a
 * succession of tests before checking the constraints.
 * 
 * <pre>
 * function isRecs(any v) => bool:
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
public final class DecisionTree {
	
	private static final class Node {		
		/**
		 * The test that will determines whether or not to traverse this node.
		 */
		private final Type type;

		/**
		 * The children of this node. The types of all child nodes must be
		 * independent, otherwise the tree is malformed.
		 */
		private final ArrayList<Node> children;
		
		/**
		 * Constraints for this level (if any)
		 */
		private Code.Block constraint;
		
		public Node(Type type, Code.Block constraint) {
			this.type = type;			
			this.constraint = constraint;
			this.children = new ArrayList<Node>();			
		}
		
		public Node(Type type, Code.Block constraint, ArrayList<Node> children) {
			this.type = type;			
			this.constraint = constraint;
			this.children = children;
		}
	}
	
	/**
	 * Root of the decision tree
	 */
	private Node root;
	
	public DecisionTree(Type type) {		
		root = new Node(type,null);
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
	public void add(Type type, Code.Block constraint) {		
		root = add(root,type,constraint);
	}
	
	private Node add(Node node, Type type, Code.Block constraint) {
		// requires node.type :> type
		
		ArrayList<Node> children = node.children;
		// first, check whether a child subsumes type
		for(int i=0;i!=children.size();++i) {
			Node n = children.get(i);			
			if(Type.isSubtype(n.type,type)) {
				children.set(i,add(n,type,constraint));
				return node;
			}
		}
		
		// No child subsumes type.  So construct its child set.
		ArrayList<Node> nchildren = new ArrayList<Node>();
		for(int i=0;i!=children.size();) {
			Node n = children.get(i);		
			Type nType = n.type;
			if(Type.isSubtype(type,nType)) {
				if(nType.equals(type)) {
					if(n.constraint != null) {
						String nextLabel = CodeUtils.freshLabel();			
						Code.Block blk = chainBlock(nextLabel, n.constraint);								
						blk.add(Codes.Label(nextLabel));
						blk.addAll(constraint);
						n.constraint = blk;
					} 
					return node;
				} else {
					if(constraint != null) {
						// child is completely subsumed
						nchildren.add(n);
					}
					children.remove(i);
				}				
			} else {
				++i;
			}
		}
		
		children.add(new Node(type,constraint,nchildren));
		
		return node;
	}
	
	/**
	 * Flattern this tree into a sequential block. If the test passes then the
	 * block exits, otherwise a fail statement is reached.
	 * 
	 * @return
	 */
	public Code.Block flattern() {
		Code.Block blk = new Code.Block(1);
		String exitLabel = CodeUtils.freshLabel();
		flattern(root,blk,exitLabel,false);
		blk.add(Codes.Label(exitLabel));
		return blk;
	}
	
	private void flattern(Node node, Code.Block blk, String target, boolean last) {
		if(node.constraint != null) {	
			if(last || node.children.isEmpty()) {
				// no chaining is required in this case
				blk.addAll(node.constraint);
			} else {
				String nextLabel = CodeUtils.freshLabel();
				blk.addAll(chainBlock(nextLabel, node.constraint));											
				blk.add(Codes.Goto(target));						
				blk.add(Codes.Label(nextLabel));
			}
		} else if(node != root) {
			// root is treated as special case because it's constraint is always
			// zero.			
			blk.add(Codes.Goto(target));				
			return;
		}

		ArrayList<Node> children = node.children;
		String nextLabel = null;

		int lastIndex = children.size()-1;
		for(int i=0;i!=children.size();++i) {			
			nextLabel =  CodeUtils.freshLabel();
			Node child = children.get(i);
			
			if(node != root || children.size() != 1) {
				// in the very special case that this node is actually the root,
				// and it only has one child then this test is unnecessary since
				// the type system will already have enforced it.
				if(child.constraint == null) {
					// in this case, we can perform a direct branch.
					blk.add(Codes.IfIs(node.type, Codes.REG_0,
							child.type, target));
					// FIXME: there is a bug here, since we should fail at this
					// point. To fix this we need to change the above iftype
					// into an assert statement; however, no such statement
					// exists.
				} else {
					// normal case
					blk.add(Codes.IfIs(node.type, Codes.REG_0,
							Type.Negation(child.type), nextLabel));
					flattern(child,blk,target,i == lastIndex);	
				}
			}
			// add label for next case (if appropriate)
			if(nextLabel != null) {
				blk.add(Codes.Label(nextLabel));
			}
		}
	}
	
	/**
	 * The chainBlock method takes a block and replaces every fail statement
	 * with a goto to a given label. This is useful for handling constraints in
	 * union types, since if the constraint is not met that doesn't mean its
	 * game over.
	 * 
	 * @param target
	 * @param blk
	 * @return
	 */
	private static Code.Block chainBlock(String target, Code.Block blk) {
		Code.Block nblock = new Code.Block(blk.numInputs());
		for (Code.Block.Entry e : blk) {
			if (e.code instanceof Codes.Fail) {				
				nblock.add(Codes.Goto(target));
			} else {
				nblock.add(e.code, e.attributes());
			}
		}
		return CodeUtils.relabel(nblock);
	}
}
