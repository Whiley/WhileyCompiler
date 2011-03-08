// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.lang;

import java.util.*;

import wyil.util.Pair;

/**
 * A structural type. See
 * http://whiley.org/2011/03/07/implementing-structural-types/ for more on how
 * this class works.
 * 
 * @author djp
 * 
 */
public abstract class NewType {

	// =============================================================
	// Type Constructors
	// =============================================================

	public static final Any T_ANY = new Any();
	public static final Void T_VOID = new Void();
	public static final Null T_NULL = new Null();	
	public static final Bool T_BOOL = new Bool();
	public static final Int T_INT = new Int();
	public static final Rational T_RATIONAL = new Rational();

	
	/**
	 * Construct a set type using the given element type.
	 * 
	 * @param element
	 */
	public static final Set T_SET(NewType element) {
		if (element instanceof Leaf) {
			return new Set(new Node[] { new Node(K_SET, 1),
					new Node(leafKind((Leaf) element), null) });
		} else {
			// Compound type
			Node[] nodes = insertComponent(((Compound) element).nodes);
			nodes[0] = new Node(K_SET, 1);
			return new Set(nodes);
		}
	}
	
	/**
	 * Construct a list type using the given element type.
	 * 
	 * @param element
	 */
	public static final List T_LIST(NewType element) {
		if (element instanceof Leaf) {
			return new List(new Node[] { new Node(K_LIST, 1),
					new Node(leafKind((Leaf) element), null) });
		} else {
			// Compound type
			Node[] nodes = insertComponent(((Compound) element).nodes);
			nodes[0] = new Node(K_LIST, 1);
			return new List(nodes);
		}
	}
	
	/**
	 * Construct a dictionary type using the given key and value types.
	 * 
	 * @param element
	 */
	public static final Dictionary T_DICTIONARY(NewType key, NewType value) {
		Node[] keyComps = nodes(key);
		Node[] valueComps = nodes(value);
		Node[] nodes = new Node[1 + keyComps.length + valueComps.length];
			
		insertNodes(1,keyComps,nodes);
		insertNodes(1+keyComps.length,valueComps,nodes);
		nodes[0] = new Node(K_DICTIONARY, new Pair(1,1+keyComps.length));
		return new Dictionary(nodes);		
	}
	
	/**
	 * Construct a union type using the given type bounds
	 * 
	 * @param element
	 */
	public static final Union T_UNION(NewType... bounds) {
		if(bounds.length < 1) {
			throw new IllegalArgumentException("Union requires more than one bound");
		}
		// include child unions
		ArrayList<NewType> nbounds = new ArrayList<NewType>();
		for(NewType t : bounds) {
			if(t instanceof Union) {
				nbounds.addAll(((Union)t).bounds());
			} else {
				nbounds.add(t);
			}
		}
		int len = 1;
		for(NewType b : nbounds) {
			// could be optimised slightly
			len += nodes(b).length;
		}		
		Node[] nodes = new Node[len];
		int[] children = new int[nbounds.size()];
		int start = 1;
		for(int i=0;i!=nbounds.size();++i) {
			children[i] = start;
			Node[] comps = nodes(nbounds.get(i));
			insertNodes(start,comps,nodes);
			start += comps.length;
		}
		nodes[0] = new Node(K_UNION, children);
		return new Union(nodes);		
	}

	/**
	 * Construct a function type using the given return and parameter types.
	 * 
	 * @param element
	 */
	public static final Fun T_FUN(NewType ret, NewType... params) {
		Node[] retcomps = nodes(ret); 
		int len = 1 + retcomps.length;
		for(NewType b : params) {
			// could be optimised slightly
			len += nodes(b).length;
		}		
		Node[] nodes = new Node[len];
		int[] children = new int[1 + params.length];
		insertNodes(1,retcomps,nodes);
		children[0] = 1;
		int start = 1 + retcomps.length;		
		for(int i=0;i!=params.length;++i) {
			children[i+1] = start;
			Node[] comps = nodes(params[i]);
			insertNodes(start,comps,nodes);
			start += comps.length;
		}
		nodes[0] = new Node(K_FUNCTION, children);
		return new Fun(nodes);		
	}
	
	/**
	 * Construct a record type using the given fields.
	 * 
	 * @param element
	 */
	public static final Record T_RECORD(Map<String,NewType> fields) {		
		ArrayList<String> keys = new ArrayList<String>(fields.keySet());
		Collections.sort(keys);		
		int len = 1;
		for(int i=0;i!=keys.size();++i) {
			String k = keys.get(i);
			NewType t = fields.get(k);			
			len += nodes(t).length;
		}
		Node[] nodes = new Node[len];
		Pair<String,Integer>[] children = new Pair[fields.size()];
		int start = 1;
		for(int i=0;i!=children.length;++i) {			
			String k = keys.get(i);
			children[i] = new Pair<String,Integer>(k,start);
			Node[] comps = nodes(fields.get(k));
			insertNodes(start,comps,nodes);
			start += comps.length;
		}
		nodes[0] = new Node(K_RECORD,children);
		return new Record(nodes);
	}

	/**
	 * Construct a label type. These are used in the construction of recursive
	 * types. Essentially, a label corresponds to the leaf of a recursive type,
	 * which we can then "close" later on as we build up the type. For example,
	 * we construct the recursive type <code>X<null|{X next}></code> as follows:
	 * 
	 * <pre>
	 * HashMap<String,Type> fields = new HashMap<String,Type>();
	 * fields.put("next",T_LABEL("X"));	 * 
	 * Type tmp = T_UNION(T_NULL,T_RECORD(fields));
	 * Type type = T_RECURSIVE("X",tmp);
	 * </pre>
	 * 
	 * <b>NOTE:</b> a type containing a label is not considered valid until it
	 * is closed using a recursive type.
	 * 
	 * @param label
	 * @return
	 */
	public static final NewType T_LABEL(String label) {
		return new Compound(new Node[]{new Node(K_LABEL,label)});
	}

	/**
	 * Close a recursive type using a given label. Essentially, this traverses
	 * the given type and routes each occurrence of the label to recursively
	 * point to the type's root. For example, we construct the recursive type
	 * <code>X<null|{X next}></code> as follows:
	 * 
	 * <pre>
	 * HashMap<String,Type> fields = new HashMap<String,Type>();
	 * fields.put("next",T_LABEL("X"));	 * 
	 * Type tmp = T_UNION(T_NULL,T_RECORD(fields));
	 * Type type = T_RECURSIVE("X",tmp);
	 * </pre>
	 * 
	 * <b>NOTE:</b> it is invalid to close a type which does not contain at
	 * least one instance of the given label. Doing this will cause an exception
	 * to be raised.
	 * 
	 * @param label
	 *            --- label to be used for closing.
	 * @param type
	 *            --- type to be closed.
	 * @return
	 */
	public static final NewType T_RECURSIVE(String label, NewType type) {
		// first stage, identify all matching labels
		if(type instanceof Leaf) { throw new IllegalArgumentException("cannot close a leaf type"); }
		Compound compound = (Compound) type;
		Node[] nodes = compound.nodes;
		int[] rmap = new int[nodes.length];		
		int nmatches = 0;
		for(int i=0;i!=nodes.length;++i) {
			Node c = nodes[i];
			if(c.kind == K_LABEL && c.data.equals(label)) {
				rmap[i] = 0;
				nmatches++;
			} else {
				rmap[i] = i - nmatches;
			}
		}
		if (nmatches == 0) {
			throw new IllegalArgumentException(
					"type cannot be closed, as it contains no matching labels");
		}
		Node[] newnodes = new Node[nodes.length-nmatches];
		nmatches = 0;
		for(int i=0;i!=nodes.length;++i) {
			Node c = nodes[i];
			if(c.kind == K_LABEL && c.data.equals(label)) {				
				nmatches++;
			} else {
				newnodes[i-nmatches] = remap(nodes[i],rmap);
			}
		}
		return construct(newnodes);
	}
	
	// =============================================================
	// Type operations
	// =============================================================

	/**
	 * Determine whether type <code>t2</code> is a <i>subtype</i> of type
	 * <code>t1</code> (written t1 :> t2). In other words, whether the set of
	 * all possible values described by the type <code>t2</code> is a subset of
	 * that described by <code>t1</code>.
	 */
	public static boolean isSubtype(NewType t1, NewType t2) {		
		Node[] g1 = nodes(t1);
		Node[] g2 = nodes(t2);
		Pair<BitSet,BitSet> matrices = buildSubtypeMatrices(g1,g2);
		BitSet suptypeMatrix = matrices.second();
		return suptypeMatrix.get(0); // compare root of g1 against root of g2 
	}
	
	/**
	 * Compute the <i>least upper bound</i> of two types t1 and t2. The least upper
	 * bound is a type t3 where <code>t3 :> t1</code>, <code>t3 :> t2</code> and
	 * there does not exist a type t4, where <code>t3 :> t4</code>,
	 * <code>t4 :> t1</code>, <code>t4 :> t2</code>.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static NewType leastUpperBound(NewType t1, NewType t2) {
		return minimise(T_UNION(t1,t2)); // so easy
	}
	
	/**
	 * Compute the <i>greatest lower bound</i> of two types t1 and t2. The
	 * greatest lower bound is a type t3 where <code>t1 :> t3</code>,
	 * <code>t2 :> t3</code> and there does not exist a type t4, where
	 * <code>t1 :> t4</code>, <code>t2 :> t4</code> and <code>t4 :> t3</code>.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static NewType greatestLowerBound(NewType t1, NewType t2) {
		return null;
	}

	/**
	 * Let <code>S</code> be determined by subtracting the set of values
	 * described by type <code>t2</code> from that described by <code>t1</code>.
	 * Then, this method returns the <i>least</i> type <code>t3</code> which
	 * covers <code>S</code> (that is, every value in <code>S</code> is in the
	 * set of values described by <code>t3</code>). Unfortunately, in some
	 * cases, <code>t3</code> may contain other (spurious) values not found in
	 * <code>S</code>.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static NewType leastDifference(NewType t1, NewType t2) {
		return null;
	}

	/**
	 * The effective record type gives a subset of the visible fields which are
	 * guaranteed to be in the type. For example, consider this type:
	 * 
	 * <pre>
	 * {int op, int x} | {int op, [int] y}
	 * </pre>
	 * 
	 * Here, the field op is guaranteed to be present. Therefore, the effective
	 * record type is just <code>{int op}</code>.
	 * 
	 * @param t
	 * @return
	 */
	public static Record effectiveRecordType(NewType t) {
		return null;
	}
	
	/**
	 * Minimise the given type to produce a fully minimised version.
	 * 
	 * @param type
	 * @return
	 */
	private static NewType minimise(NewType type) {
		// leaf types never need minmising!
		if (type instanceof Leaf) {
			return type;
		}
		// compound types need minimising.
		Node[] nodes = ((Compound) type).nodes;
		
		/* debug code
		for(int i=0;i!=components.length;++i) {
			System.out.println(i + ": " + components[i]);
		}
		*/
		BitSet matrix = buildSubtypeMatrix(nodes);	
		
		/* debug code
		for(int i=0;i!=components.length;++i) {
			for(int j=0;j!=components.length;++j) {
				if(matrix.get((i*components.length)+j)) {
					System.out.print(" 1");
				} else {
					System.out.print(" 0");
				}
			}	
			System.out.println();
		}
		*/
		ArrayList<Node> newnodes = new ArrayList<Node>();
		int[] allocated = new int[nodes.length];
		rebuild(0, nodes, allocated, newnodes, matrix);
		return construct(newnodes.toArray(new Node[newnodes.size()]));		
	}

	/**
	 * <p>
	 * This method determines both the subtype and supertype relationship
	 * between each node in the type graph. The resulting matrix <code>r</code>
	 * is organised into row-major order, where <code>r[i][j]</code> implies
	 * <code>i <: j</code>. The algorithm works by initially assuming that all
	 * subtypes hold. Then, it iteratively considers every relationship cross
	 * off those that no longer hold, until there is no change in the matrix.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b>The only reason we can compute both subtype and supertype
	 * relations in one matrix is because its for one matrix and, hence, they
	 * are symmetrical
	 * </p>
	 * 
	 * @param graph
	 *            --- graph to compute the matrix for
	 * @return
	 */
	private static BitSet buildSubtypeMatrix(Node[] graph) {
		int g1Size = graph.length;		
		BitSet matrix = new BitSet(g1Size * g1Size);
		// intially assume all subtype relationships hold 
		matrix.set(0,matrix.size(),true);
		
		boolean changed = true;
		while(changed) {
			changed=false;
			for(int i=0;i!=g1Size;i++) {
				for(int j=0;j!=g1Size;j++) {					
					boolean isj = isSubtype(i,graph,j,graph,matrix);
					boolean jsi = isSubtype(j,graph,i,graph,matrix);					
					if(matrix.get((i*g1Size)+j) != isj || matrix.get((j*g1Size)+i) != jsi) {
						matrix.set((i*g1Size)+j,isj);
						matrix.set((j*g1Size)+i,jsi);
						changed = true;
					}
				}	
			}
		}
		
		return matrix;
	}

	/**
	 * <p>
	 * This method determines the complete subtype and supertype relationship
	 * between each node in the type graphs <code>g1</code> and <code>g2</code>.
	 * The resulting matrix <code>r</code> is organised into row-major order,
	 * where <code>r[i][j]</code> implies <code>i <: j</code> if <code>i</code>
	 * is a node in <code>g1</code>, and <code>j</code> a node in
	 * <code>g2</code>. The algorithm works by initially assuming that all
	 * subtypes hold. Then, it iteratively considers every relationship cross
	 * off those that no longer hold, until there is no change in the matrix.
	 * </p>
	 * <p>
	 * <b>NOTE:</b>we must generate two matrices, because we have comparing two
	 * different graphs.
	 * </p>
	 * 
	 * @param graph1
	 *            --- graph whose nodes form the rows of the matrices
	 * @param graph2
	 *            --- graph whose nodes form the columns of the matrics
	 * @return --- A pair <code><m1,m2></code> of matrices, where
	 *         <code>m1</code> is the subtype matrix and <code>m2</code> is the
	 *         supertype matrix.
	 */
	private static Pair<BitSet,BitSet> buildSubtypeMatrices(Node[] graph1,Node[] graph2) {
		int g1Size = graph1.length;
		int g2Size = graph2.length;		
		BitSet subtypeMatrix = new BitSet(g1Size * g2Size);
		BitSet suptypeMatrix = new BitSet(g1Size * g2Size);
		// intially assume all subtype relationships hold 
		subtypeMatrix.set(0,subtypeMatrix.size(),true);
		suptypeMatrix.set(0,suptypeMatrix.size(),true);
		
		boolean changed = true;
		while(changed) {
			changed=false;
			for(int i=0;i!=g1Size;i++) {
				for(int j=0;j!=g2Size;j++) {					
					boolean isj = isSubtype(i,graph1,j,graph2,subtypeMatrix);
					boolean jsi = isSubtype(j,graph2,i,graph1,suptypeMatrix);									
					if(subtypeMatrix.get((i*g2Size)+j) != isj) {
						subtypeMatrix.set((i*g2Size)+j,isj);
						changed = true;
					}
					if(suptypeMatrix.get((i*g2Size)+j) != jsi) {
						suptypeMatrix.set((i*g2Size)+j,jsi);
						changed = true;
					}
				}	
			}
		}
		
		return new Pair<BitSet,BitSet>(subtypeMatrix,suptypeMatrix);
	}

	
	/**
	 * Check that node <code>n1</code> is a subtype of node
	 * <code>n2</code> in the given subtype matrix. This matrix is represented
	 * in row-major form, with <code>r[i][j]</code> indicating that
	 * <code>i <: j</code>.
	 * 
	 * @param c1
	 * @param c2
	 * @param graph1
	 * @param matrix
	 * @return
	 */
	private static boolean isSubtype(int n1, Node[] graph1, int n2, Node[] graph2, BitSet matrix) {
		Node c1 = graph1[n1];
		Node c2 = graph2[n2];		
		int g2Size = graph2.length;
		
		if(c1.kind == c2.kind) { 

			switch(c1.kind) {
			case K_SET:
			case K_LIST:
			case K_REFERENCE: {
				// unary node
				int e1 = (Integer) c1.data;
				int e2 = (Integer) c2.data;
				return matrix.get((e1*g2Size)+e2);
			}
			case K_DICTIONARY: {
				// binary node
				Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) c1.data;
				Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) c2.data;
				return matrix.get((p1.first() * g2Size) + p2.first())
				&& matrix.get((p1.second() * g2Size) + p2.second());
			}		
			case K_FUNCTION:  {
				// nary nodes
				int[] elems1 = (int[]) c1.data;
				int[] elems2 = (int[]) c2.data;
				if(elems1.length != elems2.length){
					return false;
				}
				// Check return value first (which is covariant)
				int e1 = elems1[0];
				int e2 = elems2[0];
				if(!matrix.get((e1*g2Size)+e2)) {
					return false;
				}
				// Now, check parameters (which are contra-variant)
				for(int i=1;i<elems1.length;++i) {
					e1 = elems1[i];
					e2 = elems2[i];
					if(!matrix.get((e2*g2Size)+e1)) {
						return false;
					}
				}
				return true;
			}
			case K_RECORD:
				// labeled nary nodes
				Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) c1.data;
				Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) c2.data;
				if(fields1.length != fields2.length) {
					return false;
				}
				// FIXME: need to take into account depth subtyping here.
				for (int i = 0; i != fields1.length; ++i) {
					Pair<String, Integer> e1 = fields1[i];
					Pair<String, Integer> e2 = fields2[i];
					if (!e1.first().equals(e2.first())
							|| !matrix
							.get((e1.second() * g2Size) + e2.second())) {
						return false;
					}
				}
				return true;
			case K_UNION: {
				// This is the hardest (i.e. most expensive) case. Essentially, I
				// just check that for each bound in one node, there is an
				// equivalent bound in the other.
				int[] bounds1 = (int[]) c1.data;
				int[] bounds2 = (int[]) c2.data;

				// check every bound in c1 is a subtype of some bound in c2.
				for(int i : bounds1) {
					boolean matched=false;
					for(int j : bounds2) {
						if(matrix.get((i*g2Size)+j)) {
							matched = true;
							break;
						}
					}
					if(!matched) { return false; }
				}

				return true;
			}
			case K_LABEL:
				throw new IllegalArgumentException("attempting to minimise open recurisve type");		
			default:
				// primitive types true immediately
				return true;
			}		
		} else if(c1.kind == K_INT && c2.kind == K_RATIONAL) {
			return true;
		} else if(c1.kind == K_VOID) {
			return true;
		} else if(c2.kind == K_ANY) {
			return true;
		} else if (c1.kind == K_UNION){
			int[] bounds1 = (int[]) c1.data;		

			// check every bound in c1 is a subtype of some bound in c2.
			for(int i : bounds1) {				
				if(!matrix.get((i*g2Size)+n2)) {
					return false;
				}								
			}

			return true;
		} else if (c2.kind == K_UNION) {
			int[] bounds2 = (int[]) c2.data;		

			// check some bound in c1 is a subtype of some bound in c2.
			for(int j : bounds2) {				
				if(matrix.get((n1*g2Size)+j)) {
					return true;
				}								
			}
		} 
		return false;
	}

	private static int rebuild(int idx, Node[] graph, int[] allocated,
			ArrayList<Node> newNodes, BitSet matrix) {
		int graph_size = graph.length;
		Node node = graph[idx]; 		
		int cidx = allocated[idx];		
		if(cidx > 0) {
			// node already constructed for this equivalence class
			return cidx - 1;
		} 
		
		cidx = newNodes.size(); // my new index
		// now, allocate all nodes in equivalence class
		for(int i=0;i!=graph_size;++i) {
			if(matrix.get((i*graph_size)+idx) && matrix.get((idx*graph_size)+i)) {
				allocated[i] = cidx + 1; 
			}
		}
		 
		newNodes.add(null); // reserve space for my node
		
		Object data = null;
		switch(node.kind) {
		case K_SET:
		case K_LIST:
		case K_REFERENCE: {
			int element = (Integer) node.data;
			data = (Integer) rebuild(element,graph,allocated,newNodes,matrix);
			break;
		}
		case K_DICTIONARY: {
			Pair<Integer,Integer> p = (Pair) node.data;
			int from = (Integer) rebuild(p.first(),graph,allocated,newNodes,matrix);
			int to = (Integer) rebuild(p.second(),graph,allocated,newNodes,matrix);
			data = new Pair(from,to);
			break;
		}		
		case K_FUNCTION: {
			int[] elems = (int[]) node.data;
			int[] nelems = new int[elems.length];
			for(int i = 0; i!=elems.length;++i) {				
				nelems[i]  = (Integer) rebuild(elems[i],graph,allocated,newNodes,matrix);
			}			
			data = nelems;
			break;			
		}
		case K_RECORD: {
			Pair<String,Integer>[] elems = (Pair[]) node.data;
			Pair<String,Integer>[] nelems = new Pair[elems.length];
			for(int i=0;i!=elems.length;++i) {
				Pair<String,Integer> p = elems[i];
				int j = (Integer) rebuild(p.second(),graph,allocated,newNodes,matrix);
				nelems[i] = new Pair<String,Integer>(p.first(),j);
			}
			data = nelems;			
			break;
		}	
		case K_UNION: {
			int[] elems = (int[]) node.data;
			
			// The aim here is to try and remove equivalent nodes, and nodes
			// which are subsumed by other nodes.
			
			HashSet<Integer> nelems = new HashSet<Integer>();			
			for(int i : elems) { nelems.add(i); }
			
			for(int i=0;i!=elems.length;i++) {
				int n1 = elems[i];
				for(int j=0;j<elems.length;j++) {
					if(i==j) { continue; }
					int n2 = elems[j];				
					if (matrix.get((n2 * graph_size) + n1)
							// following prevent all equivalences being removed.
							&& (!matrix.get((n1 * graph_size) + n2) || i < j)) {						
						nelems.remove(n2);												
					}
				}	
			}		
			
			// ok, let's see what we've got left			
			if (nelems.size() == 1) {				
				// ok, union node should be removed as it's entirely subsumed. I
				// need to undo what I've already done in allocating a new node.
				newNodes.remove(cidx);		
				for (int i = 0; i != graph_size; ++i) {
					if (matrix.get((i * graph_size) + idx)
							&& matrix.get((idx * graph_size) + i)) {
						allocated[i] = 0;
					}
				}
				return rebuild(nelems.iterator().next(), graph, allocated, newNodes,
						matrix);
			} else {
				int[] melems = new int[elems.length];
				int i=0;
				for (Integer j : nelems) {
					melems[i++] = (Integer) rebuild(j, graph,
							allocated, newNodes, matrix);
				}
				data = melems;
			}
			break;			
		}
		}
		// finally, create the new node!!!
		newNodes.set(cidx, new Node(node.kind,data));
		return cidx;
	}
	
	// =============================================================
	// Primitive Types
	// =============================================================

	/**
	 * A leaf type represents a type which has no component types. For example,
	 * primitive types like <code>int</code> and <code>real</code> are leaf
	 * types.
	 * 
	 * @author djp
	 * 
	 */
	public static class Leaf extends NewType {}

	/**
	 * A void type represents the type whose variables cannot exist! That is,
	 * they cannot hold any possible value. Void is used to represent the return
	 * type of a function which does not return anything. However, it is also
	 * used to represent the element type of an empty list of set. <b>NOTE:</b>
	 * the void type is a subtype of everything; that is, it is bottom in the
	 * type lattice.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Void extends Leaf {
		private Void() {}
		public boolean equals(Object o) {
			return this == o;
		}
		public int hashCode() {
			return 1;
		}
		public String toString() {
			return "void";
		}
	}

	/**
	 * The type any represents the type whose variables may hold any possible
	 * value. <b>NOTE:</b> the any type is top in the type lattice.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Any extends Leaf {
		private Any() {}
		public boolean equals(Object o) {
			return o == T_ANY;
		}
		public int hashCode() {
			return 1;
		}
		public String toString() {
			return "*";
		}
	}

	/**
	 * The null type is a special type which should be used to show the absence
	 * of something. It is distinct from void, since variables can hold the
	 * special <code>null</code> value (where as there is no special "void"
	 * value). With all of the problems surrounding <code>null</code> and
	 * <code>NullPointerException</code>s in languages like Java and C, it may
	 * seem that this type should be avoided. However, it remains a very useful
	 * abstraction to have around and, in Whiley, it is treated in a completely
	 * safe manner (unlike e.g. Java).
	 * 
	 * @author djp
	 * 
	 */
	public static final class Null extends Leaf {
		private Null() {}
		public boolean equals(Object o) {
			return this == o;
		}
		public int hashCode() {
			return 2;
		}
		public String toString() {
			return "null";
		}
	}
	/**
	 * Represents the set of boolean values (i.e. true and false)
	 * @author djp
	 *
	 */
	public static final class Bool extends Leaf {
		private Bool() {}
		public boolean equals(Object o) {
			return o == T_BOOL;
		}
		public int hashCode() {
			return 3;
		}
		public String toString() {
			return "bool";
		}
	}

	/**
	 * Represents the set of (unbound) integer values. Since integer types in
	 * Whiley are unbounded, there is no equivalent to Java's
	 * <code>MIN_VALUE</code> and <code>MAX_VALUE</code> for <code>int</code>
	 * types.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Int extends Leaf {
		private Int() {}
		public boolean equals(Object o) {
			return o == T_INT;
		}
		public int hashCode() {
			return 4;
		}
		public String toString() {
			return "int";
		}	
	}

	/**
	 * Represents the set of (unbound) rational numbers. 
	 * 
	 * @author djp
	 * 
	 */
	public static final class Rational extends Leaf {
		private Rational() {}
		public boolean equals(Object o) {
			return o == T_RATIONAL;
		}
		public int hashCode() {
			return 5;
		}
		public String toString() {
			return "rat";
		}
	}
	// =============================================================
	// Compound Type
	// =============================================================

	/**
	 * A Compound data structure is essentially a graph encoding of a type. Each
	 * node in the graph corresponds to a component of the type. Recursive
	 * cycles in the graph are permitted. <b>NOTE:</b> care must be take to
	 * ensure that the root of the graph (namely node at index 0) matches the
	 * actual Compound class (i.e. if its kind is K_SET, then this is an
	 * instance of Set).
	 */
	private static class Compound extends NewType {
		protected final Node[] nodes;
		
		public Compound(Node[] nodes) {
			this.nodes = nodes;
		}

		/**
		 * Determine the hashCode of a type.
		 */
		public int hashCode() {
			int r = 0;
			for(Node c : nodes) {
				r = r + c.hashCode();
			}
			return r;
		}

		/**
		 * This method compares two compound types to test whether they are
		 * <i>identical</i>. Observe that it does not perform an
		 * <i>isomorphism</i> test. Thus, two distinct types which are
		 * structurally isomorphic will <b>not</b> be considered equal under
		 * this method. <b>NOTE:</b> to test whether two types are structurally
		 * isomorphic, using the <code>isomorphic(t1,t20</code> method.
		 */
		public boolean equals(Object o) {
			if(o instanceof Compound) {
				Node[] cs = ((Compound) o).nodes;
				if(cs.length != nodes.length) {
					return false;
				}
				for(int i=0;i!=cs.length;++i) {
					if(!nodes[i].equals(cs[i])) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		
		/**
		 * The extract method basically performs a DFS from the root, extracts
		 * what it finds and minimises it.
		 * 
		 * @param root
		 *            --- the starting node to extract from.
		 * @return
		 */
		protected final NewType extract(int root) {
			// First, we perform the DFS.
			BitSet visited = new BitSet(nodes.length);
			// extracted maps new indices to old indices
			ArrayList<Integer> extracted = new ArrayList<Integer>();
			subgraph(root,visited,extracted,nodes);
			// rextracted is the reverse of extracted
			int[] rextracted = new int[extracted.size()];
			int i=0;
			for(int j : extracted) {
				rextracted[i++]=j;
			}
			Node[] newNodes = new Node[extracted.size()];
			i=0;
			for(int j : extracted) {
				newNodes[i++] = remap(nodes[j],rextracted);  
			}
				
			return construct(newNodes);
		}
		
		
		
		public String toString() {
			// First, we need to find the headers of the computation. This is
			// necessary in order to mark the start of a recursive type.
			BitSet headers = new BitSet(nodes.length);
			BitSet visited = new BitSet(nodes.length); 
			BitSet onStack = new BitSet(nodes.length);
			findHeaders(0,visited,onStack,headers,nodes);
			visited.clear();
			String[] titles = new String[nodes.length];
			int count = 0;
			for(int i=0;i!=nodes.length;++i) {
				if(headers.get(i)) {
					titles[i] = headerTitle(count++);
				}
			}
			return toString(0,visited,titles,nodes);
		}
	}

	/**
	 * The following method recursively extracts the subgraph rooted at
	 * <code>index</code> in the given graph using a depth-first search.
	 * Vertices in the subgraph are added to <code>extracted</code> in the order
	 * they are visited.
	 * 
	 * @param index
	 *            --- the node to extract the subgraph from.
	 * @param visited
	 *            --- the set of vertices already visited
	 * @param extracted
	 *            --- the list of vertices that make up the subgraph which is
	 *            built by this method.
	 * @param graph
	 *            --- the graph.
	 */
	private final static void subgraph(int index, BitSet visited,
			ArrayList<Integer> extracted, Node[] graph) {
		if(visited.get(index)) { return; } // node already visited}
		extracted.add(index);
		visited.set(index);
		Node node = graph[index];
		switch(node.kind) {
		case K_SET:
		case K_LIST:
		case K_REFERENCE:
			// unary nodes
			subgraph((Integer) node.data,visited,extracted,graph);
			break;
		case K_DICTIONARY:
			// binary node
			Pair<Integer,Integer> p = (Pair<Integer,Integer>) node.data;
			subgraph(p.first(),visited,extracted,graph);
			subgraph(p.second(),visited,extracted,graph);
			break;
		case K_UNION:
		case K_FUNCTION:
			// nary node
			int[] bounds = (int[]) node.data;
			for(Integer b : bounds) {
				subgraph(b,visited,extracted,graph);
			}
			break;
		case K_RECORD:
			// labeled nary node
			Pair<String,Integer>[] fields = (Pair<String,Integer>[]) node.data;
			for(Pair<String,Integer> f : fields) {
				subgraph(f.second(),visited,extracted,graph);
			}
			break;			
		}
	}

	/**
	 * The following method traverses the graph using a depth-first
	 * search to identify nodes which are "loop headers". That is, they are the
	 * target of one or more recursive edgesin the graph.
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
	 * @param graph
	 *            --- the graph.
	 */
	private final static void findHeaders(int index, BitSet visited,
			BitSet onStack, BitSet headers, Node[] graph) {
		if(visited.get(index)) {
			// node already visited
			if(onStack.get(index)) {
				headers.set(index);
			}
			return; 
		} 		
		onStack.set(index);
		visited.set(index);
		Node node = graph[index];
		switch(node.kind) {
		case K_SET:
		case K_LIST:
		case K_REFERENCE:
			// unary nodes
			findHeaders((Integer) node.data,visited,onStack,headers,graph);
			break;
		case K_DICTIONARY:
			// binary node
			Pair<Integer,Integer> p = (Pair<Integer,Integer>) node.data;
			findHeaders(p.first(),visited,onStack,headers,graph);
			findHeaders(p.second(),visited,onStack,headers,graph);
			break;
		case K_UNION:
		case K_FUNCTION:
			// nary node
			int[] bounds = (int[]) node.data;
			for(Integer b : bounds) {
				findHeaders(b,visited,onStack,headers,graph);
			}
			break;
		case K_RECORD:
			// labeled nary node
			Pair<String,Integer>[] fields = (Pair<String,Integer>[]) node.data;
			for(Pair<String,Integer> f : fields) {
				findHeaders(f.second(),visited,onStack,headers,graph);
			}
			break;			
		}
		onStack.set(index,false);
	}

	/**
	 * The following method constructs a string representation of the underlying
	 * graph. This representation may be an expanded version of the underling
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
	 * @param graph
	 *            --- the graph.
	 */
	private final static String toString(int index, BitSet visited,
			String[] headers, Node[] graph) {
		if (visited.get(index)) {
			// node already visited
			return headers[index];
		} else if(headers[index] != null) {
			visited.set(index);
		}
		Node node = graph[index];
		String middle;
		switch (node.kind) {
		case K_VOID:
			return "void";
		case K_ANY:
			return "any";
		case K_NULL:
			return "null";
		case K_BOOL:
			return "bool";
		case K_INT:
			return "int";
		case K_RATIONAL:
			return "rat";
		case K_SET:
			middle = "{" + toString((Integer) node.data, visited, headers, graph)
					+ "}";
			break;
		case K_LIST:
			middle = "[" + toString((Integer) node.data, visited, headers, graph)
					+ "]";
			break;
		case K_REFERENCE:
			middle = "*" + toString((Integer) node.data, visited, headers, graph);
			break;
		case K_DICTIONARY: {
			// binary node
			Pair<Integer, Integer> p = (Pair<Integer, Integer>) node.data;
			String k = toString(p.first(), visited, headers, graph);
			String v = toString(p.second(), visited, headers, graph);
			middle = "{" + k + "->" + v + "}";
			break;
		}
		case K_UNION: {
			int[] bounds = (int[]) node.data;
			middle = "";
			for (int i = 0; i != bounds.length; ++i) {
				if (i != 0) {
					middle += "|";
				}
				middle += toString(bounds[i], visited, headers, graph);
			}
			break;
		}
		case K_FUNCTION: {
			middle = "";
			int[] bounds = (int[]) node.data;
			String ret = toString(bounds[0], visited, headers, graph);
			for (int i = 1; i != bounds.length; ++i) {
				if (i != 1) {
					middle += ",";
				}
				middle += toString(bounds[i], visited, headers, graph);
			}
			middle = ret + "(" + middle + ")";
			break;
		}
		case K_RECORD: {
			// labeled nary node
			middle = "{";
			Pair<String, Integer>[] fields = (Pair<String, Integer>[]) node.data;
			for (int i = 0; i != fields.length; ++i) {
				if (i != 0) {
					middle += ",";
				}
				Pair<String, Integer> f = fields[i];
				middle += toString(f.second(), visited, headers, graph) + " " + f.first();
			}
			middle = middle + "}";
			break;
		}
		default: 
			throw new IllegalArgumentException("Invalid type encountered");
		}
		
		// Finally, check whether this is a header node, or not. If it is a
		// header then we need to insert the recursive type.
		String header = headers[index];
		if(header != null) {
			return header + "<" + middle + ">";
		} else {
			return middle;
		}
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
	
	// =============================================================
	// Compound Faces
	// =============================================================

	/*
	 * The compound faces are not technically necessary, as they simply provide
	 * interfaces to the underlying nodes of a compound type. However, they
	 * certainly make it more pleasant to use this library.
	 */

	/**
	 * A set type describes set values whose elements are subtypes of the
	 * element type. For example, <code>{1,2,3}</code> is an instance of set
	 * type <code>{int}</code>; however, <code>{1.345}</code> is not.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Set extends Compound  {
		private Set(Node[] nodes) {
			super(nodes);
		}
		public NewType element() {
			return extract(1);
		}		
	}

	/**
	 * A list type describes list values whose elements are subtypes of the
	 * element type. For example, <code>[1,2,3]</code> is an instance of list
	 * type <code>[int]</code>; however, <code>[1.345]</code> is not.
	 * 
	 * @author djp
	 * 
	 */
	public static final class List extends Compound  {
		private List(Node[] nodes) {
			super(nodes);
		}
		public NewType element() {
			return extract(1);
		}		
	}

	/**
	 * A reference (aka pointer) represents a reference to an object on a heap.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Reference extends Compound  {
		private Reference(Node[] nodes) {
			super(nodes);
		}
		public NewType element() {
			int i = (Integer) nodes[0].data;
			return extract(i);			
		}		
	}

	/**
	 * A dictionary represents a one-many mapping from variables of one type to
	 * variables of another type. For example, the dictionary type
	 * <code>int->real</code> represents a map from integers to real values. A
	 * valid instance of this type might be <code>{1->1.2,2->3}</code>.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Dictionary extends Compound  {
		private Dictionary(Node[] nodes) {
			super(nodes);
		}
		public NewType key() {
			Pair<Integer,Integer> p = (Pair) nodes[0].data;
			return extract(p.first());
		}
		public NewType value() {
			Pair<Integer,Integer> p = (Pair) nodes[0].data;
			return extract(p.second());			
		}
	}

	/**
	 * A record is made up of a number of fields, each of which has a unique
	 * name. Each field has a corresponding type. One can think of a record as a
	 * special kind of "fixed" dictionary (i.e. where we know exactly which
	 * entries we have).
	 * 
	 * @author djp
	 * 
	 */
	public static final class Record extends Compound  {
		private Record(Node[] nodes) {
			super(nodes);
		}

		/**
		 * Extract just the key set for this field. This is a cheaper operation
		 * than extracting the keys and their types (since types must be
		 * extracted).
		 * 
		 * @return
		 */
		public HashSet<String> keys() {
			Pair<String,Integer>[] fields = (Pair[]) nodes[0].data;
			HashSet<String> r = new HashSet<String>();
			for(Pair<String,Integer> f : fields) {
				r.add(f.first());
			}
			return r;
		}

		/**
		 * Return a mapping from field names to their types.
		 * 
		 * @return
		 */
		public HashMap<String,NewType> fields() {
			Pair<String,Integer>[] fields = (Pair[]) nodes[0].data;
			HashMap<String,NewType> r = new HashMap<String,NewType>();
			for(Pair<String,Integer> f : fields) {
				r.put(f.first(),extract(f.second()));
			}
			return r;
		}
	}

	/**
	 * A union type represents a type whose variables may hold values from any
	 * of its "bounds". For example, the union type null|int indicates a
	 * variable can either hold an integer value, or null. <b>NOTE:</b>There
	 * must be at least two bounds for a union type to make sense.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Union extends Compound {
		private Union(Node[] nodes) {
			super(nodes);
		}

		/**
		 * Return the bounds of this union type.
		 * 
		 * @return
		 */
		public HashSet<NewType> bounds() {
			int[] fields = (int[]) nodes[0].data;
			HashSet<NewType> r = new HashSet<NewType>();
			for(int i : fields) {
				r.add(extract(i));
			}
			return r;
		}
	}

	/**
	 * A function type, consisting of a list of zero or more parameters and a
	 * return type.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Fun extends Compound  {
		Fun(Node[] nodes) {
			super(nodes);
		}

		/**
		 * Get the return type of this function type.
		 * 
		 * @return
		 */
		public NewType ret() {
			Integer[] fields = (Integer[]) nodes[0].data;
			return extract(fields[0]);
		}

		/**
		 * Get the parameter types of this function type.
		 * 
		 * @return
		 */
		public ArrayList<NewType> params() {
			Integer[] fields = (Integer[]) nodes[0].data;
			ArrayList<NewType> r = new ArrayList<NewType>();
			for(int i=1;i<fields.length;++i) {
				r.add(extract(i));
			}
			return r;
		}
	}
	
	// =============================================================
	// Components
	// =============================================================

	private static final byte K_VOID = 0;
	private static final byte K_ANY = 1;
	private static final byte K_NULL = 2;
	private static final byte K_BOOL = 3;
	private static final byte K_INT = 4;
	private static final byte K_RATIONAL = 5;
	private static final byte K_SET = 6;
	private static final byte K_LIST = 7;
	private static final byte K_DICTIONARY = 8;
	private static final byte K_REFERENCE = 9;
	private static final byte K_RECORD = 10;
	private static final byte K_UNION = 11;
	private static final byte K_FUNCTION = 12;
	private static final byte K_LABEL = 13;

	/**
	 * Represents a node in the type graph. Each node has a kind, along with a
	 * data value identifying any children. For set, list and reference kinds
	 * the data value is an Integer; for records, it's a Pair<String,Integer>[]
	 * (sorted by key). For dictionaries, it's a Pair<Integer,Integer> and, for
	 * unions and functions it's int[] (for functions first element is return).
	 * 
	 * @author djp
	 * 
	 */
	private static final class Node {
		final byte kind;
		final Object data;
		
		public Node(byte kind, Object data) {
			this.kind = kind;
			this.data = data;
		}
		public boolean equals(final Object o) {
			if(o instanceof Node) {
				Node c= (Node) o;
				if(data == null) {
					return kind == c.kind && c.data == null;
				} else {
					// FIXME: this is broken!!!
					return kind == c.kind && data.equals(c.data);
				}
			}
			return false;
		}
		public int hashCode() {
			if(data == null) {
				return kind;
			} else {
				return kind + data.hashCode();
			}
		}
		
		public final static String[] kinds = { "void", "any", "null", "bool",
				"int", "rat", "dict", "set", "list", "ref", "record", "union",
				"fun", "label" };
		public String toString() {
			if(data instanceof Pair[]) {
				return kinds[kind] + " : " + Arrays.toString((Pair[])data);
			} else if(data instanceof int[]) {
				return kinds[kind] + " : " + Arrays.toString((int[])data);				
			} else {
				return kinds[kind] + " : " + data;
			}
		}
	}
	
	private static final Node[] nodes(NewType t) {
		if (t instanceof Leaf) {
			return new Node[]{new Node(leafKind((Leaf) t), null)};
		} else {
			// compound type
			return ((Compound)t).nodes;
		}
	}
	
	private static final byte leafKind(Leaf leaf) {
		if(leaf instanceof Void) {
			return K_VOID;
		} else if(leaf instanceof Any) {
			return K_ANY;
		} else if(leaf instanceof Null) {
			return K_NULL;
		} else if(leaf instanceof Bool) {
			return K_BOOL;
		} else if(leaf instanceof Int) {
			return K_INT;
		} else if(leaf instanceof Rational) {
			return K_RATIONAL;
		} else {
			// should be dead code
			throw new IllegalArgumentException("Invalid leaf node: " + leaf);
		}
	}

	/**
	 * This method inserts a blank node at the head of the nodes
	 * array, whilst remapping all existing nodes appropriately.
	 * 
	 * @param nodes
	 * @return
	 */
	private static Node[] insertComponent(Node[] nodes) {
		Node[] newnodes = new Node[nodes.length+1];		
		int[] rmap = new int[nodes.length];
		for(int i=0;i!=nodes.length;++i) {
			rmap[i] = i+1;			
		}
		for(int i=0;i!=nodes.length;++i) {
			newnodes[i+1] = remap(nodes[i],rmap);			
		}
		return newnodes;
	}

	/**
	 * The method inserts the nodes in
	 * <code>from</from> into those in <code>into</code> at the given index.
	 * This method remaps nodes in <code>from</code>, but does not remap
	 * any in <code>into</code>
	 * 
	 * @param start
	 * @param from
	 * @param into
	 * @return
	 */
	private static Node[] insertNodes(int start, Node[] from, Node[] into) {
		int[] rmap = new int[from.length];
		for(int i=0;i!=from.length;++i) {
			rmap[i] = i+start;			
		}
		for(int i=0;i!=from.length;++i) {
			into[i+start] = remap(from[i],rmap);			
		}
		return into;
	}
	/**
	 * The remap method takes a node, and mapping from vertices in the old
	 * space to the those in the new space. It then applies this mapping, so
	 * that the node produced refers to vertices in the new space. Or, in
	 * other words, it transposes the node into the new space.
	 * 
	 * @param node
	 *            --- node to be transposed.
	 * @param rmap
	 *            --- mapping from integers in old space to those in new
	 *            space.
	 * @return
	 */
	private static Node remap(Node node, int[] rmap) {
		Object data;

		switch (node.kind) {
		case K_SET:
		case K_LIST:
		case K_REFERENCE:
			// unary nodes
			Integer element = (Integer) node.data;
			data = rmap[element];
			break;
		case K_DICTIONARY:
			// binary node
			Pair<Integer, Integer> p = (Pair<Integer, Integer>) node.data;
			data = new Pair(rmap[p.first()], rmap[p.second()]);
			break;
		case K_UNION:
		case K_FUNCTION:
			// nary node
			int[] bounds = (int[]) node.data;
			int[] nbounds = new int[bounds.length];
			for (int i = 0; i != bounds.length; ++i) {
				nbounds[i] = rmap[bounds[i]];
			}
			data = nbounds;
			break;
		case K_RECORD:
			// labeled nary node
			Pair<String, Integer>[] fields = (Pair<String, Integer>[]) node.data;
			Pair<String, Integer>[] nfields = new Pair[fields.length];
			for (int i = 0; i != fields.length; ++i) {
				Pair<String, Integer> field = fields[i];
				nfields[i] = new Pair(field.first(), rmap[field.second()]);
			}
			data = nfields;
			break;
		default:
			return node;
		}
		return new Node(node.kind, data);
	}

	/**
	 * The construct methods constructs a Type from an array of Components.
	 * It carefully ensures the kind of the root node matches the class
	 * created (e.g. a kind K_SET results in a class Set).
	 * 
	 * @param nodes
	 * @return
	 */
	private final static NewType construct(Node[] nodes) {
		Node root = nodes[0];
		switch(root.kind) {
		case K_VOID:
			return T_VOID;
		case K_ANY:
			return T_ANY;
		case K_NULL:
			return T_NULL;			
		case K_BOOL:
			return T_BOOL;
		case K_INT:
			return T_INT;
		case K_RATIONAL:
			return T_RATIONAL;
		case K_SET:
			return new Set(nodes);
		case K_LIST:
			return new List(nodes);
		case K_DICTIONARY:
			return new Dictionary(nodes);
		case K_RECORD:
			return new Record(nodes);
		case K_UNION:
			return new Union(nodes);
		case K_FUNCTION:
			return new Fun(nodes);
		default:
			throw new IllegalArgumentException("invalid node kind: " + root.kind);
		}
	}
	
	public static void main(String[] args) {				
		NewType ft1 = minimise(T_RECURSIVE("X",linkedList(2,T_INT,"X")));
		NewType ft2 = minimise(T_RECURSIVE("X",linkedList(1,T_RATIONAL,"X")));
		
		System.out.println(ft1 + " :> " + ft2 + " : " + isSubtype(ft1,ft2));		
	}
	
	public static NewType linkedList(int nlinks, NewType dataType, String label) {
		NewType leaf;
		if(nlinks == 0) {
			leaf = T_LABEL(label);
		} else {
			leaf = linkedList(nlinks-1,dataType,label);
		}
		HashMap<String,NewType> fields = new HashMap<String,NewType>();
		fields.put("next",leaf);
		fields.put("data",dataType);	 
		return T_UNION(T_NULL,T_RECORD(fields));		
	}	
}
