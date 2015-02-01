package wyil.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Represents a complete sequence of bytecode instructions. For example, every
 * function or method body is a single Block. Likewise, the invariant for a
 * given type is a Block. Finally, a Block permits attributes to be attached to
 * every bytecode it contains. An example attribute is one for holding the
 * location of the source code which generated the bytecode.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class CodeBlock implements Iterable<Code> {
	protected ArrayList<Code> bytecodes;

	public CodeBlock() {
		bytecodes = new ArrayList<Code>();
	}

	public CodeBlock(Code... bytecodes) {
		this.bytecodes = new ArrayList<Code>();
		Collections.addAll(this.bytecodes,bytecodes);
	}

	public CodeBlock(Collection<Code> bytecodes) {
		this.bytecodes = new ArrayList<Code>(bytecodes);
	}

	// ===================================================================
	// Accessor Methods
	// ===================================================================

	/**
	 * Determine the number of slots used in this block.
	 *
	 * @return
	 */
	public int numSlots() {
		HashSet<Integer> slots = new HashSet<Integer>();
		for (Code c : bytecodes) {
			c.registers(slots);
		}
		int r = 0;
		for (int i : slots) {
			r = Math.max(r, i + 1);
		}
		return r;
	}

	/**
	 * Determine the exact slots used in this block.
	 *
	 * @return
	 */
	public Set<Integer> slots() {
		HashSet<Integer> slots = new HashSet<Integer>();
		for (Code c : bytecodes) {
			c.registers(slots);
		}
		return slots;
	}

	public int size() {
		return bytecodes.size();
	}

	public Iterator<Code> iterator() {
		return bytecodes.iterator();
	}

	public Code get(int index) {
		return bytecodes.get(index);
	}
	
	/**
	 * Get the entry associated with a given bytecode id. This will recurse
	 * nested code blocks as necessary to locate this bytecode.
	 *
	 * @param id
	 * @return
	 */
	public Code get(Index index) {
		int[] indexArray = index.toArray();
		CodeBlock iterator = this;
		int i=0;
		while (i < indexArray.length - 1) {
			iterator = (CodeBlock) iterator.get(indexArray[i]);
			i = i + 1;
		}
		return iterator.get(indexArray[i]);
	}

	/**
	 * Check whether a given index is contained within this block. That is,
	 * whether or not a bytecode exists at the given index.
	 * 
	 * @param parent
	 * @return
	 */
	public boolean contains(Index index) {
		int[] indexArray = index.toArray();
		CodeBlock iterator = this;
		int i = 0;
		while (i < indexArray.length - 1) {
			int j = indexArray[i];
			if(j >= iterator.size()) {
				return false;
			}
			iterator = (CodeBlock) iterator.get(j);
			i = i + 1;
		}
		return indexArray[i] < iterator.size();
	}
	/**
	 * Returns a reference to the internal bytecode array. Note that this list
	 * is not intended to be modified.
	 *
	 * @return
	 */
	public List<Code> bytecodes() {
		return Collections.unmodifiableList(bytecodes);
	}

	// ===================================================================
	// Append Methods
	// ===================================================================

	/**
	 * Append a bytecode onto the end of this block. It is assumed that the
	 * bytecode employs the same environment as this block.
	 *
	 * @param code
	 *            --- bytecode to append
	 */
	public boolean add(Code code) {
		return bytecodes.add(code);
	}

	// ===================================================================
	// Insert Methods
	// ===================================================================

	/**
	 * <p>Insert a bytecode at a given position in this block. It is assumed that
	 * the bytecode employs the same environment as this block. The bytecode at
	 * the given position (and any after it) are shifted one position down.</p>
	 *
	 * @param index --- position to insert at.
	 * @param code --- bytecode to insert at the given position.
	 */
	public void add(int index, Code code) {
		bytecodes.add(index,code);
	}

	// ===================================================================
	// Update Methods
	// ===================================================================

	/**
	 * <p>
	 * Replace the bytecode at a given position in this block with another. It
	 * is assumed that the bytecode employs the same environment as this block.
	 * </p>
	 *
	 * @param index --- position of bytecode to replace.
	 * @param code --- bytecode to replace with.
	 */
	public void set(int index, Code code) {
		bytecodes.set(index, code);
	}

	// ===================================================================
	// Replace and Remove Methods
	// ===================================================================

	/**
	 * <p>
	 * Remove the bytecode at a given position in this block with another.
	 * </p>
	 *
	 * @param index --- position of bytecode to replace.		 *
	 */
	public Code remove(int index) {
		return bytecodes.remove(index);
	}

	/**
	 * Provides a mechanism for identifying bytecodes within arbitrarily nested
	 * blocks. Specifically, an index is n-dimensional sequence of integer
	 * indices (e.g. 0.1.3) which uniquely identifies a location within a nested
	 * block. Bytecode indices are read left-to-right with the leftmost index
	 * corresponding to the index in the outermost block.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Index {
		private final Index parent; // null only for ROOT index
		private final int value;

		public static final Index ROOT = null;
		
		/**
		 * Construct an index which is nested within the parent index, and whose
		 * initial value at the innermost level is 0. For example, "0.1.0" would
		 * be created from parent "0.1".
		 *
		 * @param parent
		 *            Parent index, which maybe null if there is no parent.
		 */
		public Index(Index parent) {
			this.parent = parent;
			this.value = 0;
		}

		/**
		 * Construct an index which is nested within the parent index, and whose
		 * initial value at the innermost level is determined by a given
		 * parameter. For example, "0.1.3" would be created from parent "0.1"
		 * and value 3.
		 *
		 * @param parent
		 *            Parent index, which maybe null if there is no parent.
		 * @param value
		 *            Value of innermost component
		 *
		 */
		public Index(Index parent, int value) {
			this.parent = parent;
			this.value = value;
		}

		/**
		 * Return the next index in sequence.
		 *
		 * @return
		 */
		public Index next() {
			return new Index(parent, value + 1);
		}

		/**
		 * Return the next index in sequence.
		 *
		 * @return
		 */
		public Index next(int n) {
			return new Index(parent, value + n);
		}
		
		public Index parent() {
			return parent;
		}
		
		/**
		 * Prepend a given index onto the front of this index;
		 * 
		 * @param index
		 *            Index to be prepended. May be null, in which case this
		 *            operation has no effect.
		 * @return
		 */
		public Index prepend(CodeBlock.Index index) {
			if(index == null) {
				return this;
			} else {
				int[] indices = toArray();
				for(int i=0;i!=indices.length;++i) {
					index = new Index(index,indices[i]);
				}
				return index;
			}
		}
		
		/**
		 * Append a given index onto the end of this index;
		 * 
		 * @param index
		 *            Index to be appended. Must not be null
		 * @return
		 */
		public Index append(CodeBlock.Index index) {
			int[] indices = index.toArray();
			CodeBlock.Index nIndex = this;
			for(int i=0;i!=indices.length;++i) {
				nIndex = new Index(nIndex,indices[i]);
			}
			return nIndex;
		}
		
		/**
		 * Return the first index nested within this index.
		 *
		 * @return
		 */
		public Index firstWithin() {
			return new Index(this);
		}

		/**
		 * Return the first index nested within this index.
		 *
		 * @return
		 */
		public Index nestedWithin(int value) {
			return new Index(this,value);
		}
		
		/**
		 * Check whether this index is contained within a given parent index. In
		 * otherwords, that the parent index is a prefix of this index.
		 * 
		 * @param parent
		 * @return
		 */
		public boolean isWithin(Index parent) {			
			if(parent == null) { return true; }
			
			int[] me = toArray();
			int[] pnt = parent.toArray();
						
			if(me.length < pnt.length) {
				return false;
			} else {
				for(int i=0;i!=pnt.length;++i) {					
					if(me[i] != pnt[i]) {
						return false;
					}
				}
				return true;
			}
		}

		public int size() {
			if(parent == null) {
				return 1;
			} else {
				return 1 + parent.size();
			}
		}

		public boolean equals(Object o) {
			if (o instanceof Index) {
				Index i = (Index) o;
				if (parent == null) {
					return value == i.value && i.parent == null;
				} else {
					return value == i.value && parent.equals(i.parent);
				}
			}
			return false;
		}

		public int hashCode() {
			if(parent == null) {
				return value;
			} else {
				return parent.hashCode() ^ value;
			}
		}

		public int[] toArray() {
			Index iterator = this;
			int[] r = new int[size()];
			for(int i = r.length;i > 0;) {
				i = i - 1;
				r[i] = iterator.value;
				iterator = iterator.parent;
			}
			return r;
		}
		
		public String toString() {
			String vstr = Integer.toString(value);
			if (parent == null) {
				return vstr;
			} else {
				return parent.toString() + "." + vstr;
			}
		}
	}
}