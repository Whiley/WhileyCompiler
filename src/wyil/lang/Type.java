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

import java.io.PrintStream;
import java.util.*;

import wyil.util.Pair;
import wyjvm.lang.Constant;

/**
 * A structural type. See
 * http://whiley.org/2011/03/07/implementing-structural-types/ for more on how
 * this class works.
 * 
 * @author djp
 * 
 */
public abstract class Type {

	// =============================================================
	// Type Constructors
	// =============================================================

	public static final Any T_ANY = new Any();
	public static final Void T_VOID = new Void();
	public static final Null T_NULL = new Null();	
	public static final Bool T_BOOL = new Bool();
	public static final Int T_INT = new Int();
	public static final Real T_REAL = new Real();	
	public static final Meta T_META = new Meta();
	public static final Type T_NUMBER = T_UNION(T_INT,T_REAL);
	
	/**
	 * Construct a tuple type using the given element types.
	 * 
	 * @param element
	 */
	public static final Tuple T_TUPLE(Type... elements) {
		int len = 1;
		for(Type b : elements) {
			// could be optimised slightly
			len += nodes(b).length;
		}		
		Node[] nodes = new Node[len];
		int[] children = new int[elements.length];
		int start = 1;
		for(int i=0;i!=elements.length;++i) {
			children[i] = start;
			Node[] comps = nodes(elements[i]);
			insertNodes(start,comps,nodes);
			start += comps.length;
		}
		nodes[0] = new Node(K_TUPLE, children);		
		return new Tuple(nodes);
	}
	
	/**
	 * Construct a process type using the given element type.
	 * 
	 * @param element
	 */
	public static final Process T_PROCESS(Type element) {
		if (element instanceof Leaf) {
			return new Process(new Node[] { new Node(K_PROCESS, 1),
					new Node(leafKind((Leaf) element), null) });
		} else {
			// Compound type
			Node[] nodes = insertComponent(((Compound) element).nodes);
			nodes[0] = new Node(K_PROCESS, 1);
			return new Process(nodes);
		}
	}
	
	public static final Existential T_EXISTENTIAL(NameID name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"existential name cannot be null");
		}
		return new Existential(name);
	}
	
	/**
	 * Construct a set type using the given element type.
	 * 
	 * @param element
	 */
	public static final Set T_SET(Type element) {
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
	public static final List T_LIST(Type element) {
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
	public static final Dictionary T_DICTIONARY(Type key, Type value) {
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
	public static final Union T_UNION(Collection<Type> bounds) {
		Type[] ts = new Type[bounds.size()];
		int i = 0;
		for(Type t : bounds) {
			ts[i++] = t;
		}
		return T_UNION(ts);
	}
	
	/**
	 * Construct a union type using the given type bounds
	 * 
	 * @param element
	 */
	public static final Union T_UNION(Type... bounds) {		
		// include child unions			
		int len = 1;		
		for(Type b : bounds) {			
			// could be optimised slightly
			len += nodes(b).length;
		}		
		Node[] nodes = new Node[len];
		int[] children = new int[bounds.length];
		int start = 1;
		for(int i=0;i!=bounds.length;++i) {
			children[i] = start;
			Node[] comps = nodes(bounds[i]);
			insertNodes(start,comps,nodes);
			start += comps.length;
		}
		nodes[0] = new Node(K_UNION, children);
		return new Union(nodes);		
	}

	public static final Fun T_FUN(Process receiver, Type ret,
			Collection<Type> params) {
		Type[] ts = new Type[params.size()];
		int i = 0;
		for (Type t : params) {
			ts[i++] = t;
		}
		return T_FUN(receiver, ret, ts);
	}
	/**
	 * Construct a function type using the given return and parameter types.
	 * 
	 * @param element
	 */
	public static final Fun T_FUN(Process receiver, Type ret, Type... params) {
		Node[] reccomps;
		if(receiver != null) {
			reccomps = nodes(receiver);
		} else {
			reccomps = new Node[0];
		}
		Node[] retcomps = nodes(ret); 
		int len = 1 + reccomps.length + retcomps.length;
		for(Type b : params) {
			// could be optimised slightly
			len += nodes(b).length;
		}		
		Node[] nodes = new Node[len];
		int[] children = new int[2 + params.length];
		insertNodes(1,reccomps,nodes);
		insertNodes(1+reccomps.length,retcomps,nodes);
		children[0] = receiver == null ? -1 : 1;
		children[1] = 1 + reccomps.length;
		int start = 1 + reccomps.length + retcomps.length;		
		for(int i=0;i!=params.length;++i) {
			children[i+2] = start;
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
	public static final Record T_RECORD(Map<String,Type> fields) {		
		ArrayList<String> keys = new ArrayList<String>(fields.keySet());
		Collections.sort(keys);		
		int len = 1;
		for(int i=0;i!=keys.size();++i) {
			String k = keys.get(i);
			Type t = fields.get(k);			
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
	public static final Type T_LABEL(String label) {
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
	public static final Type T_RECURSIVE(String label, Type type) {
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

	/**
	 * The following code converts a "type string" into an actual type. This is
	 * useful, amongst other things, for debugging.
	 * 
	 * @param str
	 * @return
	 */
	public static Type fromString(String str) {
		return new TypeParser(str).parse();
	}

	private static class TypeParser {
		private int index;
		private String str;
		public TypeParser(String str) { 
			this.str = str;
		}
		public Type parse() {
			Type term = parseTerm();
			skipWhiteSpace();
			while(index < str.length() && str.charAt(index) == '|') {
				// union type
				match("|");
				term = T_UNION(term,parse());
				skipWhiteSpace();
			}
			return term;
		}
		public Type parseTerm() {
			skipWhiteSpace();
			char lookahead = str.charAt(index);

			switch (lookahead) {
			case 'a':
				match("any");
				return T_ANY;
			case 'v':
				match("void");
				return T_VOID;
			case 'n':
				match("null");
				return T_NULL;
			case 'b':
				match("bool");
				return T_BOOL;
			case 'i':
				match("int");
				return T_INT;
			case 'r':
				match("real");
				return T_REAL;
			case '[':
			{
				match("[");
				Type elem = parse();
				match("]");
				return T_LIST(elem);
			}
			case '{':
			{
				match("{");
				Type elem = parse();
				skipWhiteSpace();
				if(index < str.length() && str.charAt(index) != '}') {
					// record
					HashMap<String,Type> fields = new HashMap<String,Type>();
					String id = parseIdentifier();
					fields.put(id, elem);
					skipWhiteSpace();
					while(index < str.length() && str.charAt(index) == ',') {
						match(",");
						elem = parse();
						id = parseIdentifier();
						fields.put(id, elem);
						skipWhiteSpace();
					}
					match("}");
					return T_RECORD(fields);					
				}
				match("}");
				return T_SET(elem);
			}
			default:
				throw new IllegalArgumentException("invalid type string: "
						+ str);
			}
		}
		private String parseIdentifier() {
			skipWhiteSpace();
			int start = index;
			while (index < str.length()
					&& Character.isJavaIdentifierPart(str.charAt(index))) {
				index++;
			}
			return str.substring(start,index);
		}
		private void skipWhiteSpace() {
			while (index < str.length()
					&& Character.isWhitespace(str.charAt(index))) {
				index++;
			}
		}		
		private void match(String match) {
			skipWhiteSpace();
			if ((str.length() - index) < match.length()
					|| !str.startsWith(match, index)) {
				throw new IllegalArgumentException("invalid type string: "
						+ str);
			}
			index += match.length();
		}
	}
	
	/**
	 * This is a utility helper for constructing types. In particular, it's
	 * useful for determine whether or not a type needs to be closed. An open
	 * type is one which contains a "dangling" reference to some node which
	 * needs to be connected to back to form a cycle.
	 * 
	 * @param label
	 * @param t
	 * @return
	 */
	public static boolean isOpen(String label, Type t) {
		if (t instanceof Leaf) {
			return false;
		}
		Compound graph = (Compound) t;
		for (Node n : graph.nodes) {
			if (n.kind == K_LABEL && n.data.equals(label)) {
				return true;
			}
		}
		return false;
	}
	
	// =============================================================
	// Serialisation Helpers
	// =============================================================

	/**
	 * The Type.Builder interface is essentially a way of separating the
	 * internals of the type implementation from clients which may want to
	 * serialise a given type graph.
	 */
	public interface Builder {

		/**
		 * Set the number of nodes required for the type being built. This
		 * method is called once, before all other methods are called. The
		 * intention is to give builders a chance to statically initialise data
		 * structures based on the number of nodes required.
		 * 
		 * @param numNodes
		 */
		void initialise(int numNodes);

		void buildPrimitive(int index, Type.Leaf type);

		void buildExistential(int index, NameID name);

		void buildSet(int index, int element);

		void buildList(int index, int element);

		void buildProcess(int index, int element);

		void buildDictionary(int index, int key, int value);

		void buildTuple(int index, int... elements);

		void buildRecord(int index, Pair<String, Integer>... fields);

		void buildFunction(int index, int receiver, int ret, int... parameters);
		
		void buildUnion(int index, int... bounds);
	}

	/**
	 * This class provides an empty implementation of a type builder, which is
	 * useful for define simple builders.
	 * 
	 * @author djp
	 * 
	 */
	public static class AbstractBuilder implements Type.Builder {		
		public void initialise(int numNodes) {
		}

		public void buildPrimitive(int index, Type.Leaf type) {
		}

		public void buildExistential(int index, NameID name) {
		}

		public void buildSet(int index, int element) {
		}

		public void buildList(int index, int element) {
		}

		public void buildProcess(int index, int element) {
		}

		public void buildDictionary(int index, int key, int value) {
		}

		public void buildTuple(int index, int... elements) {
		}

		public void buildRecord(int index, Pair<String, Integer>... fields) {
		}

		public void buildFunction(int index, int receiver, int ret,
				int... parameters) {
		}

		public void buildUnion(int index, int... bounds) {
		}
	}

	/**
	 * The internal builder is essentially the way we deserialise types. That
	 * is, clients create an instance of internal builder and then call the
	 * methods (as directed by their own type representation). At the end, we
	 * have a fully built type --- neat!
	 * 
	 * @author djp
	 * 
	 */
	public static class InternalBuilder implements Type.Builder {	
		private Node[] nodes;
		
		public Type type() {
			return construct(nodes);
		}	
		public void initialise(int numNodes) {
			nodes = new Node[numNodes];
		}
		public void buildPrimitive(int index, Type.Leaf type) {
			nodes[index] = new Node(leafKind(type),null);
		}
		public void buildExistential(int index, NameID name) {
			if (name == null) {
				throw new IllegalArgumentException(
						"existential name cannot be null");
			}
			nodes[index] = new Node(K_EXISTENTIAL,name);
		}

		public void buildSet(int index, int element) {
			nodes[index] = new Node(K_SET,element);
		}

		public void buildList(int index, int element) {
			nodes[index] = new Node(K_LIST,element);
		}

		public void buildProcess(int index, int element) {
			nodes[index] = new Node(K_PROCESS,element);
		}

		public void buildDictionary(int index, int key, int value) {
			nodes[index] = new Node(K_DICTIONARY,new Pair(key,value));
		}

		public void buildTuple(int index, int... elements) {
			nodes[index] = new Node(K_TUPLE,elements);
		}

		public void buildRecord(int index, Pair<String, Integer>... fields) {
			nodes[index] = new Node(K_RECORD,fields);
		}

		public void buildFunction(int index, int receiver, int ret,
				int... parameters) {
			int[] items = new int[parameters.length+2];
			items[0] = receiver;
			items[1] = ret;
			System.arraycopy(parameters,0,items,2,parameters.length);
			nodes[index] = new Node(K_FUNCTION,items);
		}

		public void buildUnion(int index, int... bounds) {
			nodes[index] = new Node(K_UNION,bounds);
		}
	}
	/**
	 * The print builder is an example implementation of type builder which
	 * simply constructs a textual representation of the type in the form of a
	 * graph.
	 */
	public static class PrintBuilder implements Builder {
		private final PrintStream out;
		
		public PrintBuilder(PrintStream out) {
			this.out = out;
		}
		
		public void initialise(int numNodes) { }
		
		public void buildPrimitive(int index, Type.Leaf type) {
			out.println("#" + index + " = " + type);
		}
		
		public void buildExistential(int index, NameID name) {
			out.println("#" + index + " = ?" + name);
		}
		
		public void buildSet(int index, int element) {
			out.println("#" + index + " = {#" + element + "}");
		}

		public void buildList(int index, int element) {
			out.println("#" + index + " = [#" + element + "]");
		}

		public void buildProcess(int index, int element) {
			out.println("#" + index + " = process #" + element);
		}

		public void buildDictionary(int index, int key, int value) {
			out.println("#" + index + " = {#" + key + "->#" + value + "}");
		}

		public void buildTuple(int index, int... elements) {
			out.print("#" + index + " = (");
			boolean firstTime=true;
			for(int e : elements) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				out.print("#" + e);
			}
			out.println(")");
		}

		public void buildRecord(int index, Pair<String, Integer>... fields) {
			out.print("#" + index + " = {");
			boolean firstTime=true;
			for(Pair<String,Integer> e : fields) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				out.print("#" + e.second() + " " + e.first());
			}
			out.println("}");
		}

		public void buildFunction(int index, int receiver, int ret, int... parameters) {
			out.print("#" + index + " = ");
			if(receiver != -1) {
				out.print("#" + receiver + "::");
			}
			out.print("#" + ret + "(");
			boolean firstTime=true;
			for(int e : parameters) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				out.print("#" + e);
			}
			out.println(")");
		}
		
		public void buildUnion(int index, int... bounds) {
			out.print("#" + index + " = ");
			boolean firstTime=true;
			for(int e : bounds) {
				if(!firstTime) {
					out.print(" | ");
				}
				firstTime=false;
				out.print("#" + e);
			}
			out.println();
		}
	}
	
	/**
	 * Construct a copy of a given type using a given type builder. The normal
	 * reason to do this is that the representation of the type being built is
	 * not the internal one --- e.g. it's a textual or binary representation for
	 * serialisation.
	 * 
	 * @param writer
	 * @param type
	 */
	public static void build(Builder writer, Type type) {
		if(type instanceof Leaf) {			
			writer.initialise(1);
			writer.buildPrimitive(0,(Type.Leaf)type);
		} else {				
			Compound graph = (Compound) type;
			writer.initialise(graph.nodes.length);
			Node[] nodes = graph.nodes;
			for(int i=0;i!=nodes.length;++i) {
				Node node = nodes[i];						
				switch (node.kind) {
				case K_VOID:
					writer.buildPrimitive(i,T_VOID);
					break;
				case K_ANY:
					writer.buildPrimitive(i,T_ANY);
					break;
				case K_NULL:
					writer.buildPrimitive(i,T_NULL);
					break;
				case K_BOOL:
					writer.buildPrimitive(i,T_BOOL);
					break;
				case K_INT:
					writer.buildPrimitive(i,T_INT);
					break;
				case K_RATIONAL:
					writer.buildPrimitive(i,T_REAL);
					break;
				case K_SET:
					writer.buildSet(i,(Integer) node.data);
					break;
				case K_LIST:
					writer.buildList(i,(Integer) node.data);												
					break;
				case K_PROCESS:
					writer.buildProcess(i,(Integer) node.data);	
					break;
				case K_EXISTENTIAL:					
					writer.buildExistential(i,(NameID) node.data);	
					break;
				case K_DICTIONARY: {
					// binary node
					Pair<Integer, Integer> p = (Pair<Integer, Integer>) node.data;
					writer.buildDictionary(i,p.first(),p.second());										
					break;
				}
				case K_UNION: {
					int[] bounds = (int[]) node.data;			
					writer.buildUnion(i,bounds);
					break;
				}
				case K_TUPLE: {									
					int[] bounds = (int[]) node.data;								
					writer.buildTuple(i,bounds);					
					break;
				}
				case K_FUNCTION: {				
					int[] bounds = (int[]) node.data;
					int[] params = new int[bounds.length-2];
					System.arraycopy(bounds, 2, params,0, params.length);
					writer.buildFunction(i,bounds[0],bounds[1],params);
					break;
				}
				case K_RECORD: {
					// labeled nary node					
					Pair<String, Integer>[] fields = (Pair<String, Integer>[]) node.data;
					writer.buildRecord(i,fields);
					break;
				}
				default: 
					throw new IllegalArgumentException("Invalid type encountered");
				}
			}		
		}
	}
	
	
	// =============================================================
	// Type operations
	// =============================================================

	/**
	 * A subtype relation encodes both a subtype and a supertype relation
	 * between two separate domains (called <code>from</code> and
	 * <code>to</code>).
	 */
	public final static class SubtypeRelation {

		/** 
		 * Indicates the size of the "source" domain.
		 */
		private final int fromDomain;
		
		/**
		 * Indicates the size of the "target" domain.
		 */
		private final int toDomain;
		
		/**
		 * Stores subtype relation as a binary matrix for dimenaion
		 * <code>fromDomain</code> x <code>toDomain</code>. This matrix
		 * <code>r</code> is organised into row-major order, where
		 * <code>r[i][j]</code> implies <code>i :> j</code>.
		 */
		private final BitSet subTypes;
		
		/**
		 * Stores subtype relation as a binary matrix for dimenaion
		 * <code>fromDomain</code> x <code>toDomain</code>. This matrix
		 * <code>r</code> is organised into row-major order, where
		 * <code>r[i][j]</code> implies <code>i <: j</code>.
		 */
		private final BitSet superTypes;

		public SubtypeRelation(int fromDomain, int toDomain) {
			this.fromDomain = fromDomain;
			this.toDomain = toDomain;
			this.subTypes = new BitSet(fromDomain*toDomain);
			this.superTypes = new BitSet(fromDomain*toDomain);
			
			// Initially, set all sub- and super-types as true			
			subTypes.set(0,subTypes.size(),true);
			superTypes.set(0,superTypes.size(),true);
		}
		
		/**
		 * Check whether a a given node is a subtype of another.
		 * 
		 * @param from
		 * @param to
		 * @return
		 */
		public boolean isSubtype(int from, int to) {
			return subTypes.get((toDomain*from) + to);
		}
		
		/**
		 * Check whether a a given node is a supertype of another.
		 * 
		 * @param from
		 * @param to
		 * @return
		 */
		public boolean isSupertype(int from, int to) {
			return superTypes.get((toDomain*from) + to);
		}

		/**
		 * Set the subtype flag for a given pair in the relation.
		 * 
		 * @param from
		 * @param to
		 * @param flag
		 */
		public void setSubtype(int from, int to, boolean flag) {
			subTypes.set((toDomain*from) + to,flag);			
		}
		
		/**
		 * Set the supertype flag for a given pair in the relation.
		 * 
		 * @param from
		 * @param to
		 * @param flag
		 */
		public void setSupertype(int from, int to, boolean flag) {
			superTypes.set((toDomain*from) + to,flag);			
		}
		
		public String toString() {			
			return toString(subTypes) + "\n" + toString(superTypes);
		}
		
		public String toString(BitSet matrix) {
			String r = " |";
			for(int i=0;i!=toDomain;++i) {
				r = r + " " + (i%10);
			}
			r = r + "\n-+";
			for(int i=0;i!=toDomain;++i) {
				r = r + "--";
			}
			r = r + "\n";
			for(int i=0;i!=fromDomain;++i) {	
				r = r + (i%10) + "|";;
				for(int j=0;j!=toDomain;++j) {
					if(matrix.get((i*toDomain)+j)) {
						r += " 1";
					} else {
						r += " 0";
					}
				}	
				r = r + "\n";
			}
			return r;
		}

	}

	/**
	 * A subtype inference is responsible for computing a complete subtype
	 * relation between two given graphs. The class is abstract because there
	 * are different possible implementations of this. In particular, the case
	 * when coercions are being considered, versus the case when they are not.
	 * 
	 * @author djp
	 * 
	 */
	public static abstract class SubtypeInference {
		protected final Node[] fromGraph;
		protected final Node[] toGraph;
		protected final SubtypeRelation assumptions;
		
		public SubtypeInference(Node[] fromGraph, Node[] toGraph) {
			this.fromGraph = fromGraph;
			this.toGraph = toGraph;
			this.assumptions = new SubtypeRelation(fromGraph.length,toGraph.length);
		}
		
		public SubtypeRelation doInference() {
			int fromDomain = fromGraph.length;
			int toDomain = toGraph.length;
			
			boolean changed = true;
			while(changed) {
				changed=false;
				for(int i=0;i!=fromDomain;i++) {
					for(int j=0;j!=toDomain;j++) {					
						boolean isubj = isSubType(i,j);					
						boolean isupj = isSuperType(i,j);		
						
						if(assumptions.isSubtype(i,j) && !isubj) {
							assumptions.setSubtype(i,j,false);
							changed = true;
						}
						if(assumptions.isSupertype(i,j) && !isupj) {
							assumptions.setSupertype(i,j,false);
							changed = true;
						}						
					}	
				}
			}
			
			return assumptions;
		}
		
		/**
		 * <p>
		 * Determine whether type <code>to</code> is a <i>subtype</i> of type
		 * <code>from</code> (written from :> to). In other words, whether the set
		 * of all possible values described by the type <code>to</code> is a
		 * subset of that described by <code>from</code>.
		 * </p>
		 * 
		 * @param from --- An index into <code>fromGraph</code>.
		 * @param to --- An index into <code>toGraph</code>.
		 * @return
		 */
		public abstract boolean isSubType(int from, int to);
		
		/**
		 * <p>
		 * Determine whether type <code>to</code> is a <i>super type</i> of type
		 * <code>from</code> (written from <: to). In other words, whether the set
		 * of all possible values described by the type <code>to</code> is a
		 * super set of that described by <code>from</code>.
		 * </p>
		 * 
		 * @param from --- An index into <code>fromGraph</code>.
		 * @param to --- An index into <code>toGraph</code>.
		 * @return
		 */
		public abstract boolean isSuperType(int from, int to);
	}
	
	public static class DefaultSubtypeOperator extends SubtypeInference {
		public DefaultSubtypeOperator(Node[] fromGraph, Node[] toGraph) {
			super(fromGraph,toGraph);
		}
		
		public boolean isSubType(int from, int to) {
			Node fromNode = fromGraph[from];
			Node toNode = toGraph[to];	
			
			if(fromNode.kind == toNode.kind) { 
				switch(fromNode.kind) {
				case K_EXISTENTIAL:
					NameID nid1 = (NameID) fromNode.data;
					NameID nid2 = (NameID) toNode.data;				
					return nid1.equals(nid2);
				case K_SET:
				case K_LIST:
				case K_PROCESS: {
					return assumptions.isSubtype((Integer) fromNode.data,(Integer) toNode.data);
				}
				case K_DICTIONARY: {
					// binary node
					Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) fromNode.data;
					Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) toNode.data;
					return assumptions.isSubtype(p1.first(),p2.first()) && assumptions.isSubtype(p1.second(),p2.second());  					
				}		
				case K_TUPLE:  {
					// nary nodes
					int[] elems1 = (int[]) fromNode.data;
					int[] elems2 = (int[]) toNode.data;
					if(elems1.length != elems2.length){ return false; }
					for(int i=0;i<elems1.length;++i) {
						if(!assumptions.isSubtype(elems1[i],elems2[i])) { return false; }
					}
					return true;
				}
				case K_FUNCTION:  {
					// nary nodes
					int[] elems1 = (int[]) fromNode.data;
					int[] elems2 = (int[]) toNode.data;
					if(elems1.length != elems2.length){
						return false;
					}
					// Check (optional) receiver value first (which is contravariant)
					int e1 = elems1[0];
					int e2 = elems2[0];
					if((e1 == -1 || e2 == -1) && e1 != e2) {
						return false;
					} else if (e1 != -1 && e2 != -1
							&& !assumptions.isSupertype(e1,e2)) {
						return false;
					}
					// Check return value first (which is covariant)
					e1 = elems1[1];
					e2 = elems2[1];
					if(!assumptions.isSubtype(e1,e2)) {
						return false;
					}
					// Now, check parameters (which are contra-variant)
					for(int i=2;i<elems1.length;++i) {
						e1 = elems1[i];
						e2 = elems2[i];
						if(!assumptions.isSupertype(e1,e2)) {
							return false;
						}
					}
					return true;
				}
				case K_RECORD:		
				{
					Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) fromNode.data;
					Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) toNode.data;								
					if(fields1.length != fields2.length) {
						return false;
					}
					for (int i = 0; i != fields1.length; ++i) {
						Pair<String, Integer> e1 = fields1[i];
						Pair<String, Integer> e2 = fields2[i];
						if (!e1.first().equals(e2.first())
								|| !assumptions.isSubtype(e1.second(),e2.second())) {
							return false;
						}
					}					
					return true;
				} 		
				case K_UNION: {									
					int[] bounds2 = (int[]) toNode.data;		
					for(int j : bounds2) {				
						if(!assumptions.isSubtype(from,j)) { return false; }								
					}
					return true;					
				}
				case K_LABEL:
					throw new IllegalArgumentException("attempting to minimise open recurisve type");		
				default:
					// primitive types true immediately
					return true;
				}		
			} else if(fromNode.kind == K_ANY || toNode.kind == K_VOID) {
				return true;
			} else if(fromNode.kind == K_UNION) {
				int[] bounds1 = (int[]) fromNode.data;		

				// check every bound in c1 is a subtype of some bound in c2.
				for(int i : bounds1) {				
					if(assumptions.isSubtype(i,to)) {
						return true;
					}								
				}
				return false;	
			} else if(toNode.kind == K_UNION) {
				int[] bounds2 = (int[]) toNode.data;		

				// check some bound in c1 is a subtype of some bound in c2.
				for(int j : bounds2) {				
					if(!assumptions.isSubtype(from,j)) {
						return false;
					}								
				}
				return true;	
			}
			
			return false;
		}
		
		public boolean isSuperType(int from, int to) {
			Node fromNode = fromGraph[from];
			Node toNode = toGraph[to];	
			
			if(fromNode.kind == toNode.kind) { 
				switch(fromNode.kind) {
				case K_EXISTENTIAL:
					NameID nid1 = (NameID) fromNode.data;
					NameID nid2 = (NameID) toNode.data;				
					return nid1.equals(nid2);
				case K_SET:
				case K_LIST:
				case K_PROCESS: {
					return assumptions.isSupertype((Integer) fromNode.data,(Integer) toNode.data);
				}
				case K_DICTIONARY: {
					// binary node
					Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) fromNode.data;
					Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) toNode.data;
					return assumptions.isSupertype(p1.first(),p2.first()) && assumptions.isSupertype(p1.second(),p2.second());  					
				}		
				case K_TUPLE:  {
					// nary nodes
					int[] elems1 = (int[]) fromNode.data;
					int[] elems2 = (int[]) toNode.data;
					if(elems1.length != elems2.length){ return false; }
					for(int i=0;i<elems1.length;++i) {
						if(!assumptions.isSupertype(elems1[i],elems2[i])) { return false; }
					}
					return true;
				}
				case K_FUNCTION:  {
					// nary nodes
					int[] elems1 = (int[]) fromNode.data;
					int[] elems2 = (int[]) toNode.data;
					if(elems1.length != elems2.length){
						return false;
					}
					// Check (optional) receiver value first (which is contravariant)
					int e1 = elems1[0];
					int e2 = elems2[0];
					if((e1 == -1 || e2 == -1) && e1 != e2) {
						return false;
					} else if (e1 != -1 && e2 != -1
							&& !assumptions.isSubtype(e1,e2)) {
						return false;
					}
					// Check return value first (which is covariant)
					e1 = elems1[1];
					e2 = elems2[1];
					if(!assumptions.isSupertype(e1,e2)) {
						return false;
					}
					// Now, check parameters (which are contra-variant)
					for(int i=2;i<elems1.length;++i) {
						e1 = elems1[i];
						e2 = elems2[i];
						if(!assumptions.isSubtype(e1,e2)) {
							return false;
						}
					}
					return true;
				}
				case K_RECORD:		
				{
					Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) fromNode.data;
					Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) toNode.data;								
					if(fields1.length != fields2.length) {
						return false;
					}
					for (int i = 0; i != fields1.length; ++i) {
						Pair<String, Integer> e1 = fields1[i];
						Pair<String, Integer> e2 = fields2[i];
						if (!e1.first().equals(e2.first())
								|| !assumptions.isSupertype(e1.second(),e2.second())) {
							return false;
						}
					}					
					return true;
				} 		
				case K_UNION: {														
					int[] bounds1 = (int[]) toNode.data;		

					// check every bound in c1 is a subtype of some bound in toNode.
					for(int i : bounds1) {				
						if(!assumptions.isSupertype(i,to)) {
							return false;
						}								
					}
					return true;
				}									
				case K_LABEL:
					throw new IllegalArgumentException("attempting to minimise open recurisve type");		
				default:
					// primitive types true immediately
					return true;
				}		
			} else if(fromNode.kind == K_VOID || toNode.kind == K_ANY) {
				return true;
			} else if(fromNode.kind == K_UNION) {
				int[] bounds1 = (int[]) fromNode.data;		

				// check every bound in c1 is a subtype of some bound in c2.
				for(int i : bounds1) {				
					if(!assumptions.isSupertype(i,to)) {
						return false;
					}								
				}
				return true;	
			} else if(toNode.kind == K_UNION) {
				int[] bounds2 = (int[]) toNode.data;		

				// check some bound in c1 is a subtype of some bound in c2.
				for(int j : bounds2) {				
					if(assumptions.isSupertype(from,j)) {
						return true;
					}								
				}				
			}						
			
			return false;
		}
	}
	
	public static final class CoerciveSubtypeOperator extends DefaultSubtypeOperator {
		public CoerciveSubtypeOperator(Node[] fromGraph, Node[] toGraph) {
			super(fromGraph,toGraph);
		}
		
		public boolean isSubType(int from, int to) {
			Node fromNode = fromGraph[from];
			Node toNode = toGraph[to];	
			
			if(fromNode.kind == K_RATIONAL && toNode.kind == K_INT) {
				return true;
			} else if(fromNode.kind == K_SET && toNode.kind == K_LIST) {
				return assumptions.isSubtype((Integer) fromNode.data,(Integer) toNode.data);
			} else if(fromNode.kind == K_DICTIONARY && toNode.kind == K_LIST) {
				Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) fromNode.data;
				return fromGraph[p1.first()].kind == K_INT
						&& assumptions.isSubtype(p1.second(),
								(Integer) toNode.data);
			} else {
				return super.isSubType(from,to);
			}
		}
		
		public boolean isSuperType(int from, int to) {
			Node fromNode = fromGraph[from];
			Node toNode = toGraph[to];	
			
			if(fromNode.kind == K_INT && toNode.kind == K_RATIONAL) {
				return true;
			} else if(fromNode.kind == K_LIST && toNode.kind == K_SET) {
				return assumptions.isSupertype((Integer) fromNode.data,(Integer) toNode.data);
			} else if(fromNode.kind == K_LIST && toNode.kind == K_DICTIONARY) {
				Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) toNode.data;
				return toGraph[p2.first()].kind == K_INT
						&& assumptions.isSupertype((Integer)fromNode.data,p2.second());								
			} else {
				return super.isSuperType(from,to);
			}
		}
		
		/*
		 * follwing implements width subtyping and is disabled.				 
		if(sign) {
			// labeled nary nodes
			Pair<String, Integer>[] _fields1 = (Pair<String, Integer>[]) c1.data;
			Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) toNode.data;				
			HashMap<String,Integer> fields1 = new HashMap<String,Integer>();
			for(Pair<String,Integer> f : _fields1) {
				fields1.put(f.first(), f.second());
			}
			for (int i = 0; i != fields2.length; ++i) {
				Pair<String, Integer> e2 = fields2[i];
				Integer e1 = fields1.get(e2.first());
				if (e1 == null
						|| !subtypeMatrix.get((e1 * g2Size) + e2)) {
					return false;
				}
			}
		} else {
			// labeled nary nodes
			Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) c1.data;
			Pair<String, Integer>[] _fields2 = (Pair<String, Integer>[]) toNode.data;				
			HashMap<String,Integer> fields2 = new HashMap<String,Integer>();
			for(Pair<String,Integer> f : _fields2) {
				fields2.put(f.first(), f.second());
			}
			for (int i = 0; i != fields1.length; ++i) {
				Pair<String, Integer> e1 = fields1[i];
				Integer e2 = fields2.get(e1.first());
				if (e2 == null
						|| !subtypeMatrix.get((e1.second() * g2Size) + e2)) {
					return false;
				}
			}
		 */
	}
	
	/**
	 * Determine whether type <code>t2</code> is a <i>subtype</i> of type
	 * <code>t1</code> (written t1 :> t2). In other words, whether the set of
	 * all possible values described by the type <code>t2</code> is a subset of
	 * that described by <code>t1</code>.
	 */
	public static boolean isSubtype(Type t1, Type t2) {				
		Node[] g1 = nodes(t1);
		Node[] g2 = nodes(t2);
		SubtypeInference inference = new DefaultSubtypeOperator(g1,g2);		
		SubtypeRelation rel = inference.doInference();		
		return rel.isSubtype(0, 0); 
	}

	/**
	 * Determine whether type <code>t2</code> is a <i>coercive subtype</i> of
	 * type <code>t1</code> (written t1 :> t2). Note that it can happen where
	 * the following holds:
	 * 
	 * <pre>
	 * !isSubtype(t1, t2) &amp;&amp; isCoerciveSubtype(t1, t2)
	 * </pre>
	 * 
	 * This case indicates that a <i>coercion</i> is needed to flow from
	 * <code>t2</code> to <code>t1</code>.
	 */
	public static boolean isCoerciveSubtype(Type t1, Type t2) {				
		Node[] g1 = nodes(t1);
		Node[] g2 = nodes(t2);
		SubtypeInference inference = new CoerciveSubtypeOperator(g1,g2);		
		SubtypeRelation rel = inference.doInference();		
		return rel.isSubtype(0, 0); 
	}
	
	/**
	 * Check whether two types are <i>isomorphic</i>. This is true if they are
	 * identical, or encode the same structure.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static boolean isomorphic(Type t1, Type t2) {
		return isSubtype(t1,t2) && isSubtype(t2,t1);
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
	public static Type leastUpperBound(Type t1, Type t2) {
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
	public static Type greatestLowerBound(Type t1, Type t2) {
		// BUG FIX: this algorithm still isn't implemented correctly.
		// -------> it can infinite loop as the recursion isn't terminated.
		if(isSubtype(t1,t2)) {			
			return t2;
		} else if(isSubtype(t2,t1)) {			
			return t1;
		} else {
			Node[] graph1, graph2;
			if(t1 instanceof Leaf) {
				graph1 = new Node[] { new Node(leafKind((Type.Leaf) t1), null) };
			} else {
				graph1 = ((Compound)t1).nodes;
			}
			if(t2 instanceof Leaf) {
				graph2 = new Node[] { new Node(leafKind((Type.Leaf) t2), null) };
			} else {
				graph2 = ((Compound)t2).nodes;
			}
			ArrayList<Node> newNodes = new ArrayList<Node>();
			intersect(0,graph1,0,graph2,newNodes, new HashMap());
			Type glb = construct(newNodes.toArray(new Node[newNodes.size()]));				
			return minimise(glb);
		}
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
	public static Type leastDifference(Type t1, Type t2) {
		// BUG FIX: this algorithm still isn't implemented correctly.
		// -------> it can infinite loop as the recursion isn't terminated.
		if(isSubtype(t2,t1)) {			
			return T_VOID;
		} else {
			Node[] graph1, graph2;
			if(t1 instanceof Leaf) {
				graph1 = new Node[] { new Node(leafKind((Type.Leaf) t1), null) };
			} else {
				graph1 = ((Compound)t1).nodes;
			}
			if(t2 instanceof Leaf) {
				graph2 = new Node[] { new Node(leafKind((Type.Leaf) t2), null) };
			} else {
				graph2 = ((Compound)t2).nodes;
			}
			SubtypeRelation assumptions = new DefaultSubtypeOperator(graph1,graph2).doInference();			
			ArrayList<Node> newNodes = new ArrayList<Node>();
			difference(0,graph1,0,graph2,newNodes, new HashMap(),assumptions);
			Type ldiff = construct(newNodes.toArray(new Node[newNodes.size()]));							
			return minimise(ldiff);
		}
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
	public static Record effectiveRecordType(Type t) {
		if (t instanceof Type.Record) {
			return (Type.Record) t;
		} else if (t instanceof Type.Union) {
			Union ut = (Type.Union) t;
			Record r = null;
			for (Type b : ut.bounds()) {
				if (!(b instanceof Record)) {
					return null;
				}
				Record br = (Record) b;
				if (r == null) {
					r = br;
				} else {
					HashMap<String, Type> rfields = r.fields();
					HashMap<String, Type> bfields = br.fields();
					HashMap<String, Type> nfields = new HashMap();
					for (Map.Entry<String, Type> e : rfields.entrySet()) {
						Type bt = bfields.get(e.getKey());
						if (bt != null) {
							nfields.put(e.getKey(),
									leastUpperBound(e.getValue(), bt));
						}
					}					
					r = T_RECORD(nfields);
				}
			}
			return r;
		}
		return null;
	}

	public static Set effectiveSetType(Type t) {
		if (t instanceof Type.Set) {
			return (Type.Set) t;
		} else if (t instanceof Type.Union) {			
			Union ut = (Type.Union) t;
			Set r = null;
			for (Type b : ut.bounds()) {
				if (!(b instanceof Set)) {
					return null;
				}
				Set br = (Set) b;
				if (r == null) {
					r = br;
				} else {
					r = T_SET(leastUpperBound(r.element(),br.element()));
				}
			}			
			return r;
		}
		return null;
	}
	
	public static List effectiveListType(Type t) {
		if (t instanceof Type.List) {
			return (Type.List) t;
		} else if (t instanceof Type.Union) {			
			Union ut = (Type.Union) t;
			List r = null;
			for (Type b : ut.bounds()) {
				if (!(b instanceof List)) {
					return null;
				}
				List br = (List) b;
				if (r == null) {
					r = br;
				} else {
					r = T_LIST(leastUpperBound(r.element(),br.element()));
				}
			}			
			return r;
		}
		return null;
	}
	
	public static Dictionary effectiveDictionaryType(Type t) {
		if (t instanceof Type.Dictionary) {
			return (Type.Dictionary) t;
		} else if (t instanceof Type.Union) {
			Union ut = (Type.Union) t;
			Dictionary r = null;
			for (Type b : ut.bounds()) {
				if (!(b instanceof Dictionary)) {
					return null;
				}
				Dictionary br = (Dictionary) b;
				if (r == null) {
					r = br;
				} else {
					r = T_DICTIONARY(leastUpperBound(r.key(), br.key()),
							leastUpperBound(r.value(), br.value()));
				}
			}
			return r;
		}
		return null;
	}
	
	/**
	 * Minimise the given type to produce a fully minimised version.
	 * 
	 * @param type
	 * @return
	 */	
	public static Type minimise(Type type) {
		// leaf types never need minmising!
		if (type instanceof Leaf) {
			return type;
		}				
		
		// compound types need minimising.
		Node[] nodes = ((Compound) type).nodes;		
		SubtypeRelation relation;;
		relation = new DefaultSubtypeOperator(nodes,nodes).doInference();		
		ArrayList<Node> newnodes = new ArrayList<Node>();
		int[] allocated = new int[nodes.length];
		//System.out.println("REBUILDING: " + type);
		//build(new PrintBuilder(System.out),type);
		//System.out.println(relation.toString());
		rebuild(0, nodes, allocated, newnodes, relation);
		return construct(newnodes.toArray(new Node[newnodes.size()]));		
	}

	/**
	 * This method reconstructs a graph given a set of equivalent nodes. The
	 * equivalence classes for a node are determined by the given subtype
	 * matrix, whilst the allocate array identifies when a node has already been
	 * allocated for a given equivalence class.
	 * 
	 * @param idx
	 * @param graph
	 * @param allocated
	 * @param newNodes
	 * @param matrix
	 * @return
	 */
	private static int rebuild(int idx, Node[] graph, int[] allocated,
			ArrayList<Node> newNodes, SubtypeRelation assumptions) {	
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
			if(assumptions.isSubtype(i,idx) && assumptions.isSubtype(idx, i)) {
				allocated[i] = cidx + 1; 
			}
		}
		 
		newNodes.add(null); // reserve space for my node
		
		Object data = null;
		switch(node.kind) {
		case K_EXISTENTIAL:
			data = node.data;
			break;
		case K_SET:
		case K_LIST:
		case K_PROCESS: {
			int element = (Integer) node.data;
			data = (Integer) rebuild(element,graph,allocated,newNodes,assumptions);
			break;
		}
		case K_DICTIONARY: {
			Pair<Integer,Integer> p = (Pair) node.data;
			int from = (Integer) rebuild(p.first(),graph,allocated,newNodes,assumptions);
			int to = (Integer) rebuild(p.second(),graph,allocated,newNodes,assumptions);
			data = new Pair(from,to);
			break;
		}		
		case K_TUPLE:
		case K_FUNCTION: {
			int[] elems = (int[]) node.data;
			int[] nelems = new int[elems.length];
			for(int i = 0; i!=elems.length;++i) {
				if(elems[i] == -1) {
					// possible for K_FUNCTION
					nelems[i] = -1;
				} else {
					nelems[i]  = (Integer) rebuild(elems[i],graph,allocated,newNodes,assumptions);
				}
			}			
			data = nelems;
			break;			
		}
		case K_RECORD: {			
				Pair<String, Integer>[] elems = (Pair[]) node.data;
				Pair<String, Integer>[] nelems = new Pair[elems.length];
				for (int i = 0; i != elems.length; ++i) {
					Pair<String, Integer> p = elems[i];					
					int j = (Integer) rebuild(p.second(), graph, allocated,
							newNodes, assumptions);					
					nelems[i] = new Pair<String, Integer>(p.first(), j);
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
					if(assumptions.isSubtype(n1,n2) && (!assumptions.isSubtype(n2,n1) || i < j)) {				
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
					if(assumptions.isSubtype(i,idx) && assumptions.isSubtype(idx,i)) {					
						allocated[i] = 0;
					}
				}
				return rebuild(nelems.iterator().next(), graph, allocated, newNodes,
						assumptions);
			} else {
				// first off, we have to normalise this sucker
				ArrayList<Integer> nnelems = new ArrayList(nelems);
				Collections.sort(nnelems,new MinimiseComparator(graph,assumptions));
				// ok, now rebuild
				int[] melems = new int[nelems.size()];
				int i=0;
				for (Integer j : nnelems) {
					melems[i++] = (Integer) rebuild(j, graph,
							allocated, newNodes, assumptions);
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

	private static final class MinimiseComparator implements Comparator<Integer> {
		private Node[] graph;
		private SubtypeRelation subtypeMatrix;
		
		public MinimiseComparator(Node[] graph, SubtypeRelation matrix) {
			this.graph = graph;
			this.subtypeMatrix = matrix;
		}
		
		public int compare(Integer a, Integer b) {
			Node n1 = graph[a];
			Node n2 = graph[b];
			if(n1.kind < n2.kind) {
				return -1;
			} else if(n1.kind > n2.kind) {
				return 1;
			} else {
				// First try subtype relation
				if (subtypeMatrix.isSubtype(b,a)) {
					return -1;
				} else if (subtypeMatrix.isSubtype(a,b)) {
					return 1;
				}
				// Second try harder stuff
				Object data1 = n1.data;
				Object data2 = n2.data;
				
				switch(n1.kind){
				case K_VOID:
				case K_ANY:
				case K_META:
				case K_NULL:
				case K_BOOL:
				case K_INT:
				case K_RATIONAL:
					return 0;
				case K_EXISTENTIAL: {
					String s1 = (String) data1;
					String s2 = (String) data2;
					return s1.compareTo(s2);
				}
				case K_RECORD: {
					Pair[] fields1 = (Pair[]) data1;
					Pair[] fields2 = (Pair[]) data2;
					if(fields1.length < fields2.length) {
						return -1; 
					} else if(fields1.length > fields2.length) {
						return 1;
					}
					// FIXME: could presumably do more here.
				}
				// FIXME: could do more here!!
				}
				
				if(a < b) {
					return -1;
				} else if(a > b) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
	
	private static int intersect(int n1, Node[] graph1, int n2, Node[] graph2,
			ArrayList<Node> newNodes,
			HashMap<Pair<Integer, Integer>, Integer> allocations) {	
		Integer idx = allocations.get(new Pair(n1,n2));
		if(idx != null) {
			// this indicates an allocation has already been performed for this
			// pair.  
			return idx;
		}
		
		Node c1 = graph1[n1];
		Node c2 = graph2[n2];				
		int nid = newNodes.size(); // my node id
		newNodes.add(null); // reserve space for my node	
		allocations.put(new Pair(n1,n2), nid);
		Node node; // new node being created
		
		if(c1.kind == c2.kind) { 
			switch(c1.kind) {
			case K_VOID:
			case K_ANY:
			case K_META:
			case K_NULL:
			case K_BOOL:
			case K_INT:
			case K_RATIONAL:
				node = c1;
				break;
			case K_EXISTENTIAL:
				NameID nid1 = (NameID) c1.data;
				NameID nid2 = (NameID) c2.data;				
				if(nid1.name().equals(nid2.name())) {
					node = c1;
				} else {
					node = new Node(K_VOID,null);
				}
				break;
			case K_SET:
			case K_LIST:
			case K_PROCESS: {
				// unary node
				int e1 = (Integer) c1.data;
				int e2 = (Integer) c2.data;
				int element = intersect(e1,graph1,e2,graph2,newNodes,allocations);
				node = new Node(c1.kind,element);
				break;
			}
			case K_DICTIONARY: {
				// binary node
				Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) c1.data;
				Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) c2.data;
				int key = intersect(p1.first(),graph2,p2.first(),graph2,newNodes,allocations);
				int value = intersect(p1.second(),graph2,p2.second(),graph2,newNodes,allocations);
				node = new Node(K_DICTIONARY,new Pair(key,value));
				break;
			}		
			case K_TUPLE:  {
				// nary nodes
				int[] elems1 = (int[]) c1.data;
				int[] elems2 = (int[]) c2.data;
				if(elems1.length != elems2.length) {
					// TODO: can we do better here?
					node = new Node(K_VOID,null);
				} else {
					int[] nelems = new int[elems1.length];
					for(int i=0;i!=nelems.length;++i) {
						nelems[i] = intersect(elems1[i],graph1,elems2[i],graph2,newNodes,allocations);
					}
					node = new Node(K_TUPLE,nelems);
				}
				break;
			}
			case K_FUNCTION:  {
				// nary nodes
				int[] elems1 = (int[]) c1.data;
				int[] elems2 = (int[]) c2.data;
				int e1 = elems1[0];
				int e2 = elems2[0];
				if(elems1.length != elems2.length){
					node = new Node(K_VOID,null);
				} else if ((e1 == -1 || e2 == -1) && e1 != e2) {
					node = new Node(K_VOID, null);
				} else {
					int[] nelems = new int[elems1.length];
					// TODO: need to check here whether or not this is the right
					// thing to do. My gut is telling me that covariant and
					// contravariance should be treated differently ...
					for (int i = 0; i != nelems.length; ++i) {
						nelems[i] = intersect(elems1[i], graph1, elems2[i],
								graph2, newNodes,allocations);
					}
					node = new Node(K_FUNCTION, nelems);
				}
				break;
			}
			case K_RECORD: 
				// labeled nary nodes
				outer: {
				Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) c1.data;
				Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) c2.data;								
				int old = newNodes.size();
				if(fields1.length != fields2.length) {
					node = new Node(K_VOID,null);
				} else {
					Pair<String, Integer>[] nfields = new Pair[fields1.length];
					for (int i = 0; i != nfields.length; ++i) {
						Pair<String,Integer> e1 = fields1[i];
						Pair<String,Integer> e2 = fields2[i];						
						if (!e1.first().equals(e2.first())) {
							node = new Node(K_VOID, null);
							break outer;
						} else {												
							int nidx = intersect(e1.second(), graph1, e2.second(), graph2, newNodes,
									allocations);

							if (newNodes.get(nidx).kind == K_VOID) {
								// A record with a field of void type cannot
								// exist --- it's just equivalent to void.
								while (newNodes.size() != old) {
									newNodes.remove(newNodes.size() - 1);
								}
								node = new Node(K_VOID, null);
								break outer;
							}
							
							nfields[i] = new Pair<String, Integer>(e1.first(), nidx);
						}					
					}
					node = new Node(K_RECORD, nfields);
				}				
			}
			break;			
			case K_UNION: {
				// This is the hardest (i.e. most expensive) case. Essentially, I
				// just check that for each bound in one node, there is an
				// equivalent bound in the other.
				int[] bounds1 = (int[]) c1.data;
				int[] nbounds = new int[bounds1.length];
								
				// check every bound in c1 is a subtype of some bound in c2.
				for (int i = 0; i != bounds1.length; ++i) {
					nbounds[i] = intersect(bounds1[i], graph1, n2, graph2,
							newNodes,allocations);
				}
				node = new Node(K_UNION,nbounds);
				break;
			}					
			default:
				throw new IllegalArgumentException("attempting to minimise open recurisve type");
			}		
		} else if(c1.kind == K_ANY) {			
			newNodes.remove(newNodes.size()-1);
			extractOnto(n2,graph2,newNodes);
			return nid;
		} else if(c2.kind == K_ANY) {			
			newNodes.remove(newNodes.size()-1);
			extractOnto(n1,graph1,newNodes);
			return nid;
		} else if (c1.kind == K_UNION){					
			int[] obounds = (int[]) c1.data;			
			int[] nbounds = new int[obounds.length];
							
			// check every bound in c1 is a subtype of some bound in c2.
			for (int i = 0; i != obounds.length; ++i) {
				nbounds[i] = intersect(obounds[i], graph1, n2, graph2,
						newNodes,allocations);
			}
			node = new Node(K_UNION,nbounds);
		} else if (c2.kind == K_UNION) {			
			int[] obounds = (int[]) c2.data;			
			int[] nbounds = new int[obounds.length];
							
			// check every bound in c1 is a subtype of some bound in c2.
			for (int i = 0; i != obounds.length; ++i) {
				nbounds[i] = intersect(n1,graph1,obounds[i], graph2,
						newNodes,allocations);
			}
			node = new Node(K_UNION,nbounds);
		} else {
			// default case --> go to void
			node = new Node(K_VOID,null);
		}
		// finally, create the new node!!!
		newNodes.set(nid, node);
		return nid;
	}
	
	private static int difference(int n1, Node[] graph1, int n2, Node[] graph2,
			ArrayList<Node> newNodes,
			HashMap<Pair<Integer, Integer>, Integer> allocations, SubtypeRelation matrix) {
		
		int nid = newNodes.size(); // my node id		
		if(matrix.isSupertype(n1,n2)) {
			newNodes.add(new Node(K_VOID,null));
			return nid; 
		}
		
		Integer idx = allocations.get(new Pair(n1,n2));
		if(idx != null) {
			// this indicates an allocation has already been performed for this
			// pair.  
			return idx;
		}
		
		Node c1 = graph1[n1];
		Node c2 = graph2[n2];				
		
		allocations.put(new Pair(n1,n2), nid);
		newNodes.add(null); // reserve space for my node	
		Node node; // new node being created
		
		if(c1.kind == c2.kind) { 
			switch(c1.kind) {
			case K_VOID:
			case K_ANY:
			case K_META:
			case K_NULL:
			case K_BOOL:
			case K_INT:
			case K_RATIONAL:
				node = new Node(K_VOID,null);
				break;
			case K_EXISTENTIAL:
				NameID nid1 = (NameID) c1.data;
				NameID nid2 = (NameID) c2.data;				
				if(nid1.name().equals(nid2.name())) {
					node = new Node(K_VOID,null);					
				} else {
					node = c1;
				}
				break;
			case K_SET:
			case K_LIST:
			case K_PROCESS: {
				// unary node
				int e1 = (Integer) c1.data;
				int e2 = (Integer) c2.data;
				int element = difference(e1,graph1,e2,graph2,newNodes,allocations,matrix);
				node = new Node(c1.kind,element);
				break;
			}
			case K_DICTIONARY: {
				// binary node
				Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) c1.data;
				Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) c2.data;
				int key = difference(p1.first(),graph2,p2.first(),graph2,newNodes,allocations,matrix);
				int value = difference(p1.second(),graph2,p2.second(),graph2,newNodes,allocations,matrix);
				node = new Node(K_DICTIONARY,new Pair(key,value));
				break;
			}		
			case K_TUPLE:  {
				// nary nodes
				int[] elems1 = (int[]) c1.data;
				int[] elems2 = (int[]) c2.data;
				if(elems1.length != elems2.length) {
					node = c1;
				} else {
					int[] nelems = new int[elems1.length];
					for(int i=0;i!=nelems.length;++i) {
						nelems[i] = difference(elems1[i],graph1,elems2[i],graph2,newNodes,allocations,matrix);
					}
					node = new Node(K_TUPLE,nelems);
				}
				break;
			}
			case K_FUNCTION:  {
				// nary nodes
				int[] elems1 = (int[]) c1.data;
				int[] elems2 = (int[]) c2.data;
				int e1 = elems1[0];
				int e2 = elems2[0];
				if(elems1.length != elems2.length){
					node = c1;
				} else if ((e1 == -1 || e2 == -1) && e1 != e2) {
					node = c1;
				} else {
					int[] nelems = new int[elems1.length];
					// TODO: need to check here whether or not this is the right
					// thing to do. My gut is telling me that covariant and
					// contravariance should be treated differently ...
					for (int i = 0; i != nelems.length; ++i) {
						nelems[i] = difference(elems1[i], graph1, elems2[i],
								graph2, newNodes,allocations,matrix);
					}
					node = new Node(K_FUNCTION, nelems);
				}
				break;
			}
			case K_RECORD:
				// labeled nary nodes
				Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) c1.data;
				Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) c2.data;
				if(fields1.length != fields2.length) {
					node = c1;
				} else {
					outer: {
						Pair<String, Integer>[] nfields = new Pair[fields1.length];
						// FIXME: need to support WIDTH subtyping here.
						for (int i = 0; i != fields1.length; ++i) {
							Pair<String, Integer> e1 = fields1[i];
							Pair<String, Integer> e2 = fields2[i];
							if (!e1.first().equals(e2.first())) {
								node = c1;
								break outer;
							} else {
								nfields[i] = new Pair<String, Integer>(
										e1.first(), difference(e1.second(),
												graph1, e2.second(), graph2,
												newNodes,allocations,matrix));
							}
						}
						node = new Node(K_RECORD, nfields);					
					}
				}
				break;
			case K_UNION: {
				// This is the hardest (i.e. most expensive) case. Essentially, I
				// just check that for each bound in one node, there is an
				// equivalent bound in the other.
				int[] bounds1 = (int[]) c1.data;
				int[] nbounds = new int[bounds1.length];
								
				// check every bound in c1 is a subtype of some bound in c2.
				for (int i = 0; i != bounds1.length; ++i) {
					nbounds[i] = difference(bounds1[i], graph1, n2, graph2,
							newNodes,allocations,matrix);
				}
				node = new Node(K_UNION,nbounds);
				break;
			}					
			default:
				throw new IllegalArgumentException("attempting to minimise open recurisve type");
			}		
		} else if(c1.kind == K_ANY) {			
			// TODO: try to do better
			node = new Node(K_ANY,null);
		} else if(c2.kind == K_ANY) {			
			node = new Node(K_VOID,null);
		} else if (c1.kind == K_UNION){					
			int[] obounds = (int[]) c1.data;			
			int[] nbounds = new int[obounds.length];
							
			for (int i = 0; i != obounds.length; ++i) {
				nbounds[i] = difference(obounds[i], graph1, n2, graph2,
						newNodes,allocations,matrix);
			}
			node = new Node(K_UNION,nbounds);
		} else if (c2.kind == K_UNION) {			
			int[] obounds = (int[]) c2.data;			
			int[] nbounds = new int[obounds.length];
							
			for (int i = 0; i != obounds.length; ++i) {
				nbounds[i] = difference(n1,graph1,obounds[i], graph2,
						newNodes,allocations,matrix);
			}
			// FIXME: this is broken. need intersection types.
			node = new Node(K_UNION,nbounds);
		} else {
			// default case --> go to no change
			node = c1;			
		}								
		
		if(node == c1) {
			while(newNodes.size() > nid) {
				newNodes.remove(newNodes.size()-1);
			}
						
			extractOnto(n1,graph1,newNodes);			
			return nid;
		} else {
			// finally, create the new node!!!
			newNodes.set(nid, node);
			return nid;
		}
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
	public static class Leaf extends Type {}

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
			return "any";
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
	 * The type mets represents the type of types. That is, values of this type
	 * are themselves types. (think reflection, where we have
	 * <code>class Class {}</code>).
	 * 
	 * @author djp
	 * 
	 */
	public static final class Meta extends Leaf {
		private Meta() {}
		public boolean equals(Object o) {
			return o == T_META;
		}
		public int hashCode() {
			return 1;
		}
		public String toString() {
			return "type";
		}
	}

	/**
	 * The existential type represents the an unknown type, defined at a given
	 * position.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Existential extends Compound{
		private Existential(NameID name) {
			super(new Node[] { new Node(K_EXISTENTIAL,name) });
		}
		public boolean equals(Object o) {
			if(o instanceof Existential) {
				Existential e = (Existential) o;
				return nodes[0].data.equals(nodes[0].data);
			}
			return false;
		}
		public NameID name() {
			return (NameID) nodes[0].data;
		}
		public int hashCode() {
			return nodes[0].data.hashCode();
		}
		public String toString() {
			return "?" + name();
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
	public static final class Real extends Leaf {
		private Real() {}
		public boolean equals(Object o) {
			return o == T_REAL;
		}
		public int hashCode() {
			return 5;
		}
		public String toString() {
			return "real";
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
	private static class Compound extends Type {
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
		 * isomorphic, using the <code>isomorphic(t1,t2)</code> method.
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
		protected final Type extract(int root) {
			return construct(Type.extract(root,nodes));
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
			return Type.toString(0,visited,titles,nodes);
		}
	}

	private static final Node[] extract(int root, Node[] nodes) {
		// First, we perform the DFS.
		BitSet visited = new BitSet(nodes.length);
		// extracted maps new indices to old indices
		ArrayList<Integer> extracted = new ArrayList<Integer>();
		subgraph(root,visited,extracted,nodes);		
		// rextracted is the reverse of extracted
		int[] rextracted = new int[nodes.length];
		int i=0;
		for(int j : extracted) {
			rextracted[j]=i++;
		}
		Node[] newNodes = new Node[extracted.size()];
		i=0;
		for(int j : extracted) {
			newNodes[i++] = remap(nodes[j],rextracted);  
		}
			
		return newNodes;
	}
	
	private static final void extractOnto(int root, Node[] nodes,
			ArrayList<Node> newNodes) {
		// First, we perform the DFS.
		BitSet visited = new BitSet(nodes.length);
		// extracted maps new indices to old indices
		ArrayList<Integer> extracted = new ArrayList<Integer>();
		subgraph(root, visited, extracted, nodes);
		// rextracted is the reverse of extracted
		int[] rextracted = new int[nodes.length];
		int i = newNodes.size();
		for (int j : extracted) {
			rextracted[j] = i++;
		}				
		for (int j : extracted) {
			newNodes.add(remap(nodes[j], rextracted));
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
		case K_PROCESS:
			// unary nodes
			subgraph((Integer) node.data,visited,extracted,graph);
			break;
		case K_DICTIONARY:
			// binary node
			Pair<Integer,Integer> p = (Pair<Integer,Integer>) node.data;
			subgraph(p.first(),visited,extracted,graph);
			subgraph(p.second(),visited,extracted,graph);
			break;
		case K_TUPLE:
		case K_UNION:
		case K_FUNCTION:
			// nary node
			int[] bounds = (int[]) node.data;
			for(int b : bounds) {
				if(b == -1) { continue; } // possible with K_FUNCTION				
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
		case K_PROCESS:
			// unary nodes
			findHeaders((Integer) node.data,visited,onStack,headers,graph);
			break;
		case K_DICTIONARY:
			// binary node
			Pair<Integer,Integer> p = (Pair<Integer,Integer>) node.data;
			findHeaders(p.first(),visited,onStack,headers,graph);
			findHeaders(p.second(),visited,onStack,headers,graph);
			break;
		case K_TUPLE:
		case K_UNION:
		case K_FUNCTION:
			// nary node
			int[] bounds = (int[]) node.data;
			for(int b : bounds) {
				if(b == -1) { continue; } // possible with K_FUNCTION
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
		case K_EXISTENTIAL:
			middle = "?" + node.data.toString();
			break;
		case K_PROCESS:
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
		case K_TUPLE: {
			middle = "";
			int[] bounds = (int[]) node.data;			
			for (int i = 0; i != bounds.length; ++i) {
				if (i != 0) {
					middle += ",";
				}
				middle += toString(bounds[i], visited, headers, graph);
			}
			middle = "(" + middle + ")";
			break;
		}
		case K_FUNCTION: {
			middle = "";
			int[] bounds = (int[]) node.data;
			String rec = bounds[0] == -1 ? null : toString(bounds[0],visited,headers,graph);
			String ret = toString(bounds[1], visited, headers, graph);
			for (int i = 2; i != bounds.length; ++i) {
				if (i != 2) {
					middle += ",";
				}
				middle += toString(bounds[i], visited, headers, graph);
			}
			if(rec != null) {
				middle = rec + "::" + ret + "(" + middle + ")";
			} else {
				middle = ret + "(" + middle + ")";
			}
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
	 * A tuple type describes a compound type made up of two or more
	 * subcomponents. It is similar to a record, except that fields are
	 * effectively anonymous.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Tuple extends Compound  {
		private Tuple(Node[] nodes) {
			super(nodes);
		}		
		public java.util.List<Type> elements() {
			int[] values = (int[]) nodes[0].data;
			ArrayList<Type> elems = new ArrayList<Type>();
			for(Integer i : values) {
				elems.add(extract(i));
			}
			return elems;
		}		
	}	
	
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
		public Type element() {
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
		public Type element() {
			return extract(1);
		}		
	}

	/**
	 * A process represents a reference to an actor in Whiley.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Process extends Compound  {
		private Process(Node[] nodes) {
			super(nodes);
		}
		public Type element() {
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
		public Type key() {
			Pair<Integer,Integer> p = (Pair) nodes[0].data;
			return extract(p.first());
		}
		public Type value() {
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
		public HashMap<String,Type> fields() {
			Pair<String,Integer>[] fields = (Pair[]) nodes[0].data;
			HashMap<String,Type> r = new HashMap<String,Type>();
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
		public HashSet<Type> bounds() {			
			HashSet<Type> r = new HashSet<Type>();
			// FIXME: this is a bit of a cludge. The essential idea is to
			// flattern unions, so we never see a union of unions. This is
			// helpful for simplifying various algorithms which use them
			Stack<Union> stack = new Stack<Union>();
			stack.add(this);
			while(!stack.isEmpty()) {				
				Union u = stack.pop();
				int[] fields = (int[]) u.nodes[0].data;
				for(int i : fields) {
					Type b = u.extract(i);
					if(b instanceof Union) {
						stack.add((Union)b);
					} else {
						r.add(b);
					}
				}
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
		public Type ret() {						
			int[] fields = (int[]) nodes[0].data;			
			return extract(fields[1]);
		}

		/**
		 * Get the return type of this function type.
		 * 
		 * @return
		 */
		public Type receiver() {
			int[] fields = (int[]) nodes[0].data;
			int r = fields[0];
			if(r == -1) { return null; }
			return extract(r);
		}
		
		/**
		 * Get the parameter types of this function type.
		 * 
		 * @return
		 */
		public ArrayList<Type> params() {
			int[] fields = (int[]) nodes[0].data;
			ArrayList<Type> r = new ArrayList<Type>();
			for(int i=2;i<fields.length;++i) {
				r.add(extract(fields[i]));
			}
			return r;
		}
	}
	
	// =============================================================
	// Components
	// =============================================================

	private static final byte K_VOID = 0;
	private static final byte K_ANY = 1;
	private static final byte K_META = 2;
	private static final byte K_NULL = 3;
	private static final byte K_BOOL = 4;
	private static final byte K_INT = 5;
	private static final byte K_RATIONAL = 6;
	private static final byte K_TUPLE = 7;
	private static final byte K_SET = 8;
	private static final byte K_LIST = 9;
	private static final byte K_DICTIONARY = 10;	
	private static final byte K_PROCESS = 11;
	private static final byte K_RECORD = 12;
	private static final byte K_UNION = 13;
	private static final byte K_FUNCTION = 14;
	private static final byte K_EXISTENTIAL = 15;
	private static final byte K_LABEL = 16;
	
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
				Node c = (Node) o;
				if(kind == c.kind) {
					switch(kind) {
					case K_VOID:
					case K_ANY:
					case K_META:
					case K_NULL:
					case K_BOOL:
					case K_INT:
					case K_RATIONAL:
						return true;
					case K_SET:
					case K_LIST:
					case K_PROCESS:
					case K_EXISTENTIAL:
					case K_DICTIONARY:
						return data.equals(c.data);
					case K_TUPLE:					
					case K_FUNCTION:
					case K_UNION:
						return Arrays.equals((int[])data, (int[])c.data);
					case K_RECORD:
						return Arrays.equals((Pair[])data, (Pair[])c.data);
					}
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
		
		public final static String[] kinds = { "void", "any", "meta", "null", "bool",
				"int", "rat", "tuple", "dict", "set", "list", "ref", "record", "union",
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
	
	private static final Node[] nodes(Type t) {
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
		} else if(leaf instanceof Real) {
			return K_RATIONAL;
		} else if(leaf instanceof Meta) {
			return K_META;
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
		case K_PROCESS:
			// unary nodes
			int element = (Integer) node.data;
			data = rmap[element];
			break;
		case K_DICTIONARY:
			// binary node
			Pair<Integer, Integer> p = (Pair<Integer, Integer>) node.data;
			data = new Pair(rmap[p.first()], rmap[p.second()]);
			break;
		case K_TUPLE:
		case K_UNION:
		case K_FUNCTION:
			// nary node
			int[] bounds = (int[]) node.data;
			int[] nbounds = new int[bounds.length];
			for (int i = 0; i != bounds.length; ++i) {
				if(bounds[i] == -1) { 
					nbounds[i] = -1; // possible with K_FUNCTION
				} else {
					nbounds[i] = rmap[bounds[i]];	
				}
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
	private final static Type construct(Node[] nodes) {
		Node root = nodes[0];
		switch(root.kind) {
		case K_VOID:
			return T_VOID;
		case K_ANY:
			return T_ANY;
		case K_META:
			return T_META;
		case K_NULL:
			return T_NULL;			
		case K_BOOL:
			return T_BOOL;
		case K_INT:
			return T_INT;
		case K_RATIONAL:
			return T_REAL;
		case K_TUPLE:
			return new Tuple(nodes);
		case K_SET:
			return new Set(nodes);
		case K_LIST:
			return new List(nodes);
		case K_EXISTENTIAL:
			if(root.data == null) {
				throw new RuntimeException("Problem");
			}
			return new Existential((NameID) root.data);
		case K_PROCESS:
			return new Process(nodes);
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
		PrintBuilder printer = new PrintBuilder(System.out);
		Type t1 = linkedList();
		System.out.println("GOT: " + t1);
		System.out.println("MIN: " + minimise(t1));
		/*
		Type t2 = fromString("{int x,any y}");
		//Type t1 = T_REAL;
		//Type t2 = T_INT;
		System.out.println("Type: " + t1 + "\n------------------");
		build(printer,t1);		
		System.out.println("\nType: " + t2 + "\n------------------");
		build(printer,t2);		
		System.out.println("====================");
		System.out.println(isSubtype(t1,t2));
		System.out.println(isSubtype(t2,t1));
		Type glb = greatestLowerBound(t1,t2);
		System.out.println(glb);
		Type lub = leastUpperBound(t1,t2);
		System.out.println(lub);
		*/	
	}
	
	public static Type linkedList() {
		Type leaf = T_LABEL("X");
		HashMap<String,Type> fields = new HashMap<String,Type>();
		fields.put("next", T_UNION(T_NULL,leaf));
		fields.put("data", T_BOOL);
		Type.Record rec = T_RECORD(fields);
		return T_RECURSIVE("X",rec);
	}
}
