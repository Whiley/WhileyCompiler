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
}