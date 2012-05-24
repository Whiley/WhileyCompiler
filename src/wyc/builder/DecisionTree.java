package wyc.builder;

import java.util.*;

import wyil.lang.Code;
import wyil.lang.Type;
import wyil.lang.Block;
import wyil.lang.Value;

/**
 * Decision trees are used for the constraints induced by union types. The key
 * problem arises when we have a union type of two or more constrained types
 * which have related base types. For example:
 * 
 * <pre>
 * define pos as int where $ > 0
 * define neg as int where $ < 0
 * define posneg as pos|neg
 * 
 * bool isPosNeg(any v):
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
 * bool isPosNeg(any v):
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
 * bool isPosNeg(any v):
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
 * bool isRecs(any v):
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
 * bool isRecs(any v):
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
		private Block constraint;
		
		public Node(Type type, Block constraint) {
			this.type = type;			
			this.constraint = constraint;
			this.children = new ArrayList<Node>();			
		}
		
		public Node(Type type, Block constraint, ArrayList<Node> children) {
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
	public void add(Type type, Block constraint) {		
		root = add(root,type,constraint);
	}
	
	private Node add(Node node, Type type, Block constraint) {
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
						String nextLabel = Block.freshLabel();			
						Block blk = chainBlock(nextLabel, n.constraint);								
						blk.append(Code.Label(nextLabel));
						blk.append(constraint);
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
	public Block flattern() {
		Block blk = new Block(1);
		String exitLabel = Block.freshLabel();
		flattern(root,blk,exitLabel,false);
		blk.append(Code.Label(exitLabel));
		return blk;
	}
	
	private void flattern(Node node, Block blk, String target, boolean last) {
		if(node.constraint != null) {	
			if(last || node.children.isEmpty()) {
				// no chaining is required in this case
				blk.append(node.constraint);
			} else {
				String nextLabel = Block.freshLabel();
				blk.append(chainBlock(nextLabel, node.constraint));											
				blk.append(Code.Goto(target));						
				blk.append(Code.Label(nextLabel));
			}
		} else if(node != root) {
			// root is treated as special case because it's constraint is always
			// zero.			
			blk.append(Code.Goto(target));				
			return;
		}

		ArrayList<Node> children = node.children;
		String nextLabel = null;

		int lastIndex = children.size()-1;
		for(int i=0;i!=children.size();++i) {			
			nextLabel =  Block.freshLabel();
			Node child = children.get(i);
			
			if(node != root || children.size() != 1) {
				// in the very special case that this node is actually the root,
				// and it only has one child then this test is unnecessary since
				// the type system will already have enforced it.
				if(child.constraint == null) {
					// in this case, we can perform a direct branch.
					blk.append(Code.IfType(node.type, Code.THIS_SLOT,
							child.type, target));
					// FIXME: there is a bug here, since we should fail at this
					// point. To fix this we need to change the above iftype
					// into an assert statement; however, no such statement
					// exists.
				} else {
					// normal case
					blk.append(Code.IfType(node.type, Code.THIS_SLOT,
							Type.Negation(child.type), nextLabel));
					flattern(child,blk,target,i == lastIndex);	
				}
			}
			// add label for next case (if appropriate)
			if(nextLabel != null) {
				blk.append(Code.Label(nextLabel));
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
	private static Block chainBlock(String target, Block blk) {	
		Block nblock = new Block(blk.numInputs());
		for (Block.Entry e : blk) {
			if (e.code instanceof Code.Assert) {
				Code.Assert a = (Code.Assert) e.code;				
				Code.COp iop = Code.invert(a.op);
				if(iop != null) {
					nblock.append(Code.IfGoto(a.type,iop,target), e.attributes());
				} else {
					// FIXME: avoid the branch here. This can be done by
					// ensuring that every Code.COp is invertible.
					String lab = Block.freshLabel();
					nblock.append(Code.IfGoto(a.type,a.op,lab), e.attributes());
					nblock.append(Code.Goto(target));
					nblock.append(Code.Label(lab));
				}
			} else {
				nblock.append(e.code, e.attributes());
			}
		}
		return nblock.relabel();
	}
}
