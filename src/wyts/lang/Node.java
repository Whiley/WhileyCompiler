package wyts.lang;

import static wyts.lang.Node.K_ANY;
import static wyts.lang.Node.K_BOOL;
import static wyts.lang.Node.K_BYTE;
import static wyts.lang.Node.K_CHAR;
import static wyts.lang.Node.K_DICTIONARY;
import static wyts.lang.Node.K_EXISTENTIAL;
import static wyts.lang.Node.K_FUNCTION;
import static wyts.lang.Node.K_INT;
import static wyts.lang.Node.K_LIST;
import static wyts.lang.Node.K_META;
import static wyts.lang.Node.K_METHOD;
import static wyts.lang.Node.K_NULL;
import static wyts.lang.Node.K_PROCESS;
import static wyts.lang.Node.K_RATIONAL;
import static wyts.lang.Node.K_RECORD;
import static wyts.lang.Node.K_SET;
import static wyts.lang.Node.K_STRING;
import static wyts.lang.Node.K_TUPLE;
import static wyts.lang.Node.K_UNION;
import static wyts.lang.Node.K_VOID;

import java.util.Arrays;

import wyil.util.Pair;
import wyil.lang.NameID;
import wyts.lang.Type.Dictionary;
import wyts.lang.Type.Existential;
import wyts.lang.Type.Fun;
import wyts.lang.Type.List;
import wyts.lang.Type.Meth;
import wyts.lang.Type.Process;
import wyts.lang.Type.Record;
import wyts.lang.Type.Set;
import wyts.lang.Type.Tuple;
import wyts.lang.Type.Union;


	
		/**
	 * Determine the node kind of a Type.Leaf
	 * @param leaf
	 * @return
	 */
	public static final byte leafKind(Type.Leaf leaf) {
		if(leaf instanceof Type.Void) {
			return K_VOID;
		} else if(leaf instanceof Type.Any) {
			return K_ANY;
		} else if(leaf instanceof Type.Null) {
			return K_NULL;
		} else if(leaf instanceof Type.Bool) {
			return K_BOOL;
		} else if(leaf instanceof Type.Byte) {
			return K_BYTE;
		} else if(leaf instanceof Type.Char) {
			return K_CHAR;
		} else if(leaf instanceof Type.Int) {
			return K_INT;
		} else if(leaf instanceof Type.Real) {
			return K_RATIONAL;
		} else if(leaf instanceof Type.Strung) {
			return K_STRING;
		} else if(leaf instanceof Type.Meta) {
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
	public static Node[] insertComponent(Node[] nodes) {
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
	public static Node[] insertNodes(int start, Node[] from, Node[] into) {
		int[] rmap = new int[from.length];
		for(int i=0;i!=from.length;++i) {
			rmap[i] = i+start;			
		}
		for(int i=0;i!=from.length;++i) {
			into[i+start] = remap(from[i],rmap);			
		}
		return into;
	}
	}
