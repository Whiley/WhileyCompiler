package wyil.lang;

import java.util.*;

import wyil.lang.Type.Recursive;
import wyil.util.Pair;

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
			return new Set(new Component[] { new Component(K_SET, 1),
					new Component(leafKind((Leaf) element), null) });
		} else {
			// Compound type
			Component[] components = insertComponent(((Compound) element).components);
			components[0] = new Component(K_SET, 1);
			return new Set(components);
		}
	}
	
	/**
	 * Construct a list type using the given element type.
	 * 
	 * @param element
	 */
	public static final List T_LIST(NewType element) {
		if (element instanceof Leaf) {
			return new List(new Component[] { new Component(K_LIST, 1),
					new Component(leafKind((Leaf) element), null) });
		} else {
			// Compound type
			Component[] components = insertComponent(((Compound) element).components);
			components[0] = new Component(K_LIST, 1);
			return new List(components);
		}
	}
	
	/**
	 * Construct a dictionary type using the given key and value types.
	 * 
	 * @param element
	 */
	public static final Dictionary T_DICTIONARY(NewType key, NewType value) {
		Component[] keyComps = components(key);
		Component[] valueComps = components(value);
		Component[] components = new Component[1 + keyComps.length + valueComps.length];
			
		insertComponents(1,keyComps,components);
		insertComponents(1+keyComps.length,valueComps,components);
		components[0] = new Component(K_DICTIONARY, new Pair(1,1+keyComps.length));
		return new Dictionary(components);		
	}
	
	/**
	 * Construct a union type using the given type bounds
	 * 
	 * @param element
	 */
	private static final Union T_UNION(NewType... bounds) {
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
			len += components(b).length;
		}		
		Component[] components = new Component[len];
		int[] children = new int[nbounds.size()];
		int start = 1;
		for(int i=0;i!=nbounds.size();++i) {
			children[i] = start;
			Component[] comps = components(nbounds.get(i));
			insertComponents(start,comps,components);
			start += comps.length;
		}
		components[0] = new Component(K_UNION, children);
		return new Union(components);		
	}

	/**
	 * Construct a function type using the given return and parameter types.
	 * 
	 * @param element
	 */
	private static final Fun T_FUN(NewType ret, NewType... params) {
		Component[] retcomps = components(ret); 
		int len = 1 + retcomps.length;
		for(NewType b : params) {
			// could be optimised slightly
			len += components(b).length;
		}		
		Component[] components = new Component[len];
		int[] children = new int[1 + params.length];
		insertComponents(1,retcomps,components);
		children[0] = 1;
		int start = 1 + retcomps.length;		
		for(int i=0;i!=params.length;++i) {
			children[i+1] = start;
			Component[] comps = components(params[i]);
			insertComponents(start,comps,components);
			start += comps.length;
		}
		components[0] = new Component(K_FUNCTION, children);
		return new Fun(components);		
	}
	
	/**
	 * Construct a record type using the given fields.
	 * 
	 * @param element
	 */
	private static final Record T_RECORD(Map<String,NewType> fields) {		
		ArrayList<String> keys = new ArrayList<String>(fields.keySet());
		Collections.sort(keys);		
		int len = 1;
		for(int i=0;i!=keys.size();++i) {
			String k = keys.get(i);
			NewType t = fields.get(k);			
			len += components(t).length;
		}
		Component[] components = new Component[len];
		Pair<String,Integer>[] children = new Pair[fields.size()];
		int start = 1;
		for(int i=0;i!=children.length;++i) {			
			String k = keys.get(i);
			children[i] = new Pair<String,Integer>(k,start);
			Component[] comps = components(fields.get(k));
			insertComponents(start,comps,components);
			start += comps.length;
		}
		components[0] = new Component(K_RECORD,children);
		return new Record(components);
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
		return new Compound(new Component[]{new Component(K_LABEL,label)});
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
		Component[] components = compound.components;
		int[] rmap = new int[components.length];		
		int nmatches = 0;
		for(int i=0;i!=components.length;++i) {
			Component c = components[i];
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
		Component[] ncomponents = new Component[components.length-nmatches];
		nmatches = 0;
		for(int i=0;i!=components.length;++i) {
			Component c = components[i];
			if(c.kind == K_LABEL && c.data.equals(label)) {				
				nmatches++;
			} else {
				ncomponents[i-nmatches] = remap(components[i],rmap);
			}
		}
		return construct(ncomponents);
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
	public boolean isSubtype(Type t1, Type t2) {
		return false;
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
		return null;
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
	 * Determine whether two types are <i>structurally isomorphic</i> or not.
	 * Two types which are not identical may be structurally isomorphic (that
	 * is, have the same structure). This operation is very similar to the well
	 * known <i>graph isomorphism</i> problem. As such, this operation will, in
	 * the worst case, require an exponential number of steps.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static boolean isomorphic(NewType t1, NewType t2) {
		// I have a rather distinct feeling that I don't need to implement this
		// method!
		return false;
	}

	/**
	 * Minimise the given type to produce a <i>canonical</i> form. That is, any
	 * two non-identical types <code>t1</code>, <code>t2</code> which encode the
	 * same type, we have that <code>minimise(t1).equals(minimise(t2))</code>.
	 * 
	 * @param type
	 * @return
	 */
	public static NewType minimise(NewType type) {
		// first, check for leaf node since these are already minimised!
		if(type instanceof Leaf) { return type; } 
		// ok, non-leaf node ... now we need to do some work.
		Compound c = (Compound) type;
		Component[] components = c.components;
		// partitions holds the equivalence sets of nodes. Initially, all nodes
		// are considered equivalent and we then separate out nodes which are
		// not equivalent.
		ArrayList<HashSet<Integer>> partitions = new ArrayList();
		// rpartitions maps each node to its index in partitions. 
		int[] rpartitions = new int[c.components.length];
		// initialise partitions and rpartitions
		partitions.add(new HashSet());
		for(int i=0;i!=components.length;++i) {
			partitions.get(0).add(i);
			rpartitions[i] = 0;
		}
		// now, we need to iteratively separate partitions until we can do no
		// more work.
		boolean changed = true;
		while(changed) {
			changed = false;
			for(int i = 0; i != partitions.size(); ++i) {
				changed |= splitPartition(i,partitions,rpartitions,components);
			}
		}
		// finally, we need to rebuild the original type whilst taking into
		// account the equivalences we have identified.
		return mergeNodes(components,partitions,rpartitions);
	}
	
	/**
	 * This method is responsible for iterating all elements of a partition and
	 * separating them out as much as possible.
	 * 
	 * @param idx
	 *            --- partition number to split
	 * @param partitions
	 *            --- partition map
	 * @param rpartitions
	 *            --- reverse partition map
	 * @return
	 */
	private static boolean splitPartition(int idx,
			ArrayList<HashSet<Integer>> partitions, int[] rpartitions,
			Component[] components) {
		// The pivot is what we'll use to split the partition. Essentially, I'll
		// find all those equivalent to the pivot, and all those which are not
		// equivalent => thus, either we make two new sets or there is no change
		// to the old set.
		HashSet<Integer> partition = partitions.get(idx);
		Integer pivot = partition.iterator().next();
		HashSet<Integer> equivs = new HashSet<Integer>();
		HashSet<Integer> nequivs = new HashSet<Integer>();
		boolean change = false;
		
		for(Integer i : partition) {
			if(partitionEquiv(components[i],components[pivot],rpartitions)) {
				// node is equivalent to pivot --> so no change
				equivs.add(i);
			} else {
				// node is not equivalent to pivot
				nequivs.add(i);
				change = true;
			}
		}
		
		if(change) {
			partitions.set(idx,equivs);
			int neqidx = partitions.size();
			partitions.add(nequivs);
			for(Integer i : nequivs) {
				rpartitions[i] = neqidx;
			}
		}		
		
		return change;
	}
	
	/**
	 * The purpose of this method is to determine whether two nodes are
	 * "partition" equivalent. That is, given the current partitioning 
	 * are they equivalent. Two nodes are not equivalent if they have a
	 * different kind (e.g. NULL is never equivalent to INT; or a record is
	 * never equivalent to a list). Furthermore, two types are not equivalent if
	 * one or more of their (immediate) children is in a different partition.
	 * 
	 * @param t1
	 *            --- first type to check equivalence of
	 * @param t2
	 *            --- second type to check equivalence of	
	 * @param rpartitions
	 *            --- maps types to their partition numbers (i.e. same number ==
	 *            same partition).
	 * @return
	 */
	private static boolean partitionEquiv(Component c1, Component c2,
			int[] rpartitions) {
		if(c1.kind != c2.kind) { return false; }
		switch(c1.kind) {
		case K_SET:
		case K_LIST:
		case K_REFERENCE: {
			// unary node
			Integer e1 = (Integer) c1.data;
			Integer e2 = (Integer) c2.data;
			return rpartitions[e1] == rpartitions[e2];
		}
		case K_DICTIONARY: {
			// binary node
			Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) c1.data;
			Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) c2.data;
			return rpartitions[p1.first()] == rpartitions[p2.first()] && 
				rpartitions[p1.second()] == rpartitions[p2.second()];
		}		
		case K_FUNCTION:  {
			// nary nodes
			int[] elems1 = (int[]) c1.data;
			int[] elems2 = (int[]) c2.data;
			if(elems1.length != elems2.length){
				return false;
			}
			for(int i=0;i!=elems1.length;++i) {
				int e1 = elems1[i];
				int e2 = elems1[i];
				if(rpartitions[e1] != rpartitions[e2]) {
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
			for(int i=0;i!=fields1.length;++i) {
				Pair<String,Integer> e1 = fields1[i];
				Pair<String,Integer> e2 = fields2[i];
				if (!e1.first().equals(e2.first())
						|| rpartitions[e1.second()] != rpartitions[e2.second()]) {
					return false;
				}
			}
			return true;
		case K_UNION: {
			// This is the hardest (i.e. most expensive) case. Essentially, I
			// just check that for each bound in one component, there is an
			// equivalent bound in the other.
			int[] bounds1 = (int[]) c1.data;
			int[] bounds2 = (int[]) c2.data;
			
			// check every bound in c1 is equivalent to some bound in c2.
			for(int i : bounds1) {
				boolean matched=false;
				for(int j : bounds2) {
					if(rpartitions[i] == rpartitions[j]) {
						matched = true;
						break;
					}
				}
				if(!matched) { return false; }
			}
			
			// check every bound in c2 is equivalent to some bound in c1.
			for(int j : bounds2) {
				boolean matched=false;
				for(int i : bounds1) {
					if(rpartitions[i] == rpartitions[j]) {
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
	}

	/**
	 * This method merges nodes together in the given component graph according
	 * to the given partitioning. So, if e.g. nodes 1 and 4 are in the same
	 * partition then they are merged together.
	 */
	private static NewType mergeNodes(Component[] graph, 
			ArrayList<HashSet<Integer>> partitions,
			int[] rpartitions) {				
		ArrayList<Component> ncomponents = new ArrayList<Component>();
		// the p2cmap is maps partition numbers to their component indices.		
		int[] p2cmap = new int[partitions.size()];
		Arrays.fill(p2cmap,-1);
		rebuild(0,graph,rpartitions,p2cmap,ncomponents);		
		return construct(ncomponents.toArray(new Component[partitions.size()]));
	}
	
	private static int rebuild(int idx, Component[] graph, int[] rpartitions,
			int[] p2cmap, ArrayList<Component> ncomponents) {
		Component node = graph[idx]; 
		int partition = rpartitions[idx];
		int nidx = p2cmap[partition];
		if(nidx != -1) {
			// component already constructed for this equivalence class
			return nidx;
		} 
		nidx = ncomponents.size(); // my new index
		p2cmap[partition] = nidx; // update the map accordingly
		ncomponents.add(null); // reserve space for my node
		
		Object data = null;
		switch(node.kind) {
		case K_SET:
		case K_LIST:
		case K_REFERENCE: {
			int element = (Integer) node.data;
			data = (Integer) rebuild(element,graph,rpartitions,p2cmap,ncomponents);
			break;
		}
		case K_DICTIONARY: {
			Pair<Integer,Integer> p = (Pair) node.data;
			int from = (Integer) rebuild(p.first(),graph,rpartitions,p2cmap,ncomponents);
			int to = (Integer) rebuild(p.second(),graph,rpartitions,p2cmap,ncomponents);
			data = new Pair(from,to);
			break;
		}		
		case K_FUNCTION: {
			int[] elems = (int[]) node.data;
			int[] nelems = new int[elems.length];
			for(int i = 0; i!=elems.length;++i) {
				nelems[i]  = (Integer) rebuild(elems[i],graph,rpartitions,p2cmap,ncomponents);
			}			
			data = nelems;
			break;			
		}
		case K_RECORD: {
			Pair<String,Integer>[] elems = (Pair[]) node.data;
			Pair<String,Integer>[] nelems = new Pair[elems.length];
			for(int i=0;i!=elems.length;++i) {
				Pair<String,Integer> p = elems[i];
				int j = (Integer) rebuild(p.second(),graph,rpartitions,p2cmap,ncomponents);
				nelems[i] = new Pair<String,Integer>(p.first(),j);
			}
			data = nelems;			
			break;
		}
		case K_UNION: {
			int[] elems = (int[]) node.data;					
			HashSet<Integer> items = new HashSet<Integer>();
			for(int i = 0; i!=elems.length;++i) {
				int ni = (Integer) rebuild(elems[i],graph,rpartitions,p2cmap,ncomponents);
				items.add(ni);  
			}			
			if(items.size() == 1) {
				// In this case, we have just one node left which means this
				// union node should be removed. In fact, this is a non-trivial
				// operation since rebuilding this single node may have itself
				// created vertices on the ncomponents stack. Therefore, we need
				// to shift those nodes which have been created up one. 
				int[] rmap = new int[ncomponents.size()];
				for(int i=0;i!=ncomponents.size();++i) {
					if(i > nidx) {
						rmap[i] = i-1;
					} else {
						rmap[i] = i;
					}
				}
				// now remove my reserved slot, since I don't need it
				ncomponents.remove(nidx);
				// next, shift nodes up the index
				for(int i=nidx;i!=ncomponents.size();++i) {
					ncomponents.set(i,remap(ncomponents.get(i),rmap));
				}		
				// finally, fix up the p2cmap as well!
				for(int i=0;i!=p2cmap.length;++i) {
					int j = p2cmap[i];
					if(j > nidx) {
						p2cmap[i] = j-1;
					}
				}
				return nidx;
			} else {
				int[] nelems = new int[items.size()];
				int i=0;
				for(Integer j : items) {
					nelems[i++] = j;
				}				
				data = nelems;
			}
			break;			
		}
		}
		// finally, create the new node!!!
		ncomponents.set(nidx, new Component(node.kind,data));
		return nidx;
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
		protected final Component[] components;
		
		public Compound(Component[] components) {
			this.components = components;
		}

		/**
		 * Determine the hashCode of a type.
		 */
		public int hashCode() {
			int r = 0;
			for(Component c : components) {
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
				Component[] cs = ((Compound) o).components;
				if(cs.length != components.length) {
					return false;
				}
				for(int i=0;i!=cs.length;++i) {
					if(!components[i].equals(cs[i])) {
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
			BitSet visited = new BitSet(components.length);
			// extracted maps new indices to old indices
			ArrayList<Integer> extracted = new ArrayList<Integer>();
			subgraph(root,visited,extracted,components);
			// rextracted is the reverse of extracted
			int[] rextracted = new int[extracted.size()];
			int i=0;
			for(int j : extracted) {
				rextracted[i++]=j;
			}
			Component[] ncomponents = new Component[extracted.size()];
			i=0;
			for(int j : extracted) {
				ncomponents[i++] = remap(components[j],rextracted);  
			}
				
			return construct(ncomponents);
		}
		
		
		
		public String toString() {
			// First, we need to find the headers of the computation. This is
			// necessary in order to mark the start of a recursive type.
			BitSet headers = new BitSet(components.length);
			BitSet visited = new BitSet(components.length); 
			BitSet onStack = new BitSet(components.length);
			findHeaders(0,visited,onStack,headers,components);
			visited.clear();
			String[] titles = new String[components.length];
			int count = 0;
			for(int i=0;i!=components.length;++i) {
				if(headers.get(i)) {
					titles[i] = headerTitle(count++);
				}
			}
			return toString(0,visited,titles,components);
		}
	}

	/**
	 * The following method recursively extracts the subgraph rooted at
	 * <code>index</code> in the given component graph using a depth-first
	 * search. Vertices in the subgraph are added to <code>extracted</code> in
	 * the order they are visited.
	 * 
	 * @param index
	 *            --- the node to extract the subgraph from.
	 * @param visited
	 *            --- the set of vertices already visited
	 * @param extracted
	 *            --- the list of vertices that make up the subgraph which is
	 *            built by this method.
	 * @param graph
	 *            --- the component graph.
	 */
	private final static void subgraph(int index, BitSet visited,
			ArrayList<Integer> extracted, Component[] graph) {
		if(visited.get(index)) { return; } // node already visited}
		extracted.add(index);
		visited.set(index);
		Component node = graph[index];
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
	 * The following method traverses the component graph using a depth-first
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
	 *            --- the component graph.
	 */
	private final static void findHeaders(int index, BitSet visited,
			BitSet onStack, BitSet headers, Component[] graph) {
		if(visited.get(index)) {
			// node already visited
			if(onStack.get(index)) {
				headers.set(index);
			}
			return; 
		} 		
		onStack.set(index);
		visited.set(index);
		Component node = graph[index];
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
	 *            --- the component graph.
	 */
	private final static String toString(int index, BitSet visited,
			String[] headers, Component[] graph) {
		if (visited.get(index)) {
			// node already visited
			return headers[index];
		} else if(headers[index] != null) {
			visited.set(index);
		}
		Component node = graph[index];
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
				middle += f.first() + ": "
						+ toString(f.second(), visited, headers, graph);
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
	 * interfaces to the underlying components of a compound type. However, they
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
		private Set(Component[] components) {
			super(components);
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
		private List(Component[] components) {
			super(components);
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
		private Reference(Component[] components) {
			super(components);
		}
		public NewType element() {
			int i = (Integer) components[0].data;
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
		private Dictionary(Component[] components) {
			super(components);
		}
		public NewType key() {
			Pair<Integer,Integer> p = (Pair) components[0].data;
			return extract(p.first());
		}
		public NewType value() {
			Pair<Integer,Integer> p = (Pair) components[0].data;
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
		private Record(Component[] components) {
			super(components);
		}

		/**
		 * Extract just the key set for this field. This is a cheaper operation
		 * than extracting the keys and their types (since types must be
		 * extracted).
		 * 
		 * @return
		 */
		public HashSet<String> keys() {
			Pair<String,Integer>[] fields = (Pair[]) components[0].data;
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
			Pair<String,Integer>[] fields = (Pair[]) components[0].data;
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
		private Union(Component[] components) {
			super(components);
		}

		/**
		 * Return the bounds of this union type.
		 * 
		 * @return
		 */
		public HashSet<NewType> bounds() {
			int[] fields = (int[]) components[0].data;
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
		Fun(Component[] components) {
			super(components);
		}

		/**
		 * Get the return type of this function type.
		 * 
		 * @return
		 */
		public NewType ret() {
			Integer[] fields = (Integer[]) components[0].data;
			return extract(fields[0]);
		}

		/**
		 * Get the parameter types of this function type.
		 * 
		 * @return
		 */
		public ArrayList<NewType> params() {
			Integer[] fields = (Integer[]) components[0].data;
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
	 * A component represents a node in the type graph. Each node has a kind,
	 * along with a data value identifying any children. For set, list and
	 * reference kinds the data value is an Integer; for records, it's a
	 * Pair<String,Integer>[] (sorted by key). For dictionaries, it's a
	 * Pair<Integer,Integer> and, for unions and functions it's int[] (for
	 * functions first element is return).
	 * 
	 * @author djp
	 * 
	 */
	private static final class Component {
		final byte kind;
		final Object data;
		
		public Component(byte kind, Object data) {
			this.kind = kind;
			this.data = data;
		}
		public boolean equals(final Object o) {
			if(o instanceof Component) {
				Component c= (Component) o;
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
	}
	
	private static final Component[] components(NewType t) {
		if (t instanceof Leaf) {
			return new Component[]{new Component(leafKind((Leaf) t), null)};
		} else {
			// compound type
			return ((Compound)t).components;
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
	 * This method inserts a black component at the head of the components
	 * array, whilst remapping all existing components appropriately.
	 * 
	 * @param components
	 * @return
	 */
	private static Component[] insertComponent(Component[] components) {
		Component[] ncomponents = new Component[components.length+1];		
		int[] rmap = new int[components.length];
		for(int i=0;i!=components.length;++i) {
			rmap[i] = i+1;			
		}
		for(int i=0;i!=components.length;++i) {
			ncomponents[i+1] = remap(components[i],rmap);			
		}
		return ncomponents;
	}

	/**
	 * The method inserts the components in
	 * <code>from</from> into those in <code>into</code> at the given index.
	 * This method remaps components in <code>from</code>, but does not remap
	 * any components in <code>into</code>
	 * 
	 * @param start
	 * @param from
	 * @param into
	 * @return
	 */
	private static Component[] insertComponents(int start, Component[] from, Component[] into) {
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
	 * other words, it transposes the component into the new space.
	 * 
	 * @param node
	 *            --- component to be transposed.
	 * @param rmap
	 *            --- mapping from integers in old space to those in new
	 *            space.
	 * @return
	 */
	private static Component remap(Component node, int[] rmap) {
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
		return new Component(node.kind, data);
	}

	/**
	 * The construct methods constructs a Type from an array of Components.
	 * It carefully ensures the kind of the root component matches the class
	 * created (e.g. a kind K_SET results in a class Set).
	 * 
	 * @param ncomponents
	 * @return
	 */
	private final static NewType construct(Component[] components) {
		Component root = components[0];
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
			return new Set(components);
		case K_LIST:
			return new List(components);
		case K_DICTIONARY:
			return new Dictionary(components);
		case K_RECORD:
			return new Record(components);
		case K_UNION:
			return new Union(components);
		case K_FUNCTION:
			return new Fun(components);
		default:
			throw new IllegalArgumentException("invalid component kind: " + root.kind);
		}
	}
	
	public static void main(String[] args) {		
				
		NewType leaf = T_RECURSIVE("Z",linkedList(3,"Z"));
		HashMap<String,NewType> fields = new HashMap<String,NewType>();
		fields.put("next",leaf);
		fields.put("data",T_UNION(T_INT,T_INT));	 
		NewType type = T_UNION(T_NULL,T_RECORD(fields));	
		
		System.out.println("BEFORE: " + type);
		type = minimise(type);
		System.out.println("AFTER: " + type);
	}
	
	public static NewType linkedList(int nlinks, String label) {
		NewType leaf;
		if(nlinks == 0) {
			leaf = T_LABEL("Z");
		} else {
			leaf = linkedList(nlinks-1,label);
		}
		HashMap<String,NewType> fields = new HashMap<String,NewType>();
		fields.put("next",leaf);
		fields.put("data",T_INT);	 
		return T_UNION(T_NULL,T_RECORD(fields));		
	}	
}
