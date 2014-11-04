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
	public CodeBlock.Entry getEntry(int... id) {
		CodeBlock iterator = this;
		int i=0;
		while (i < id.length - 1) {
			iterator = (CodeBlock) iterator.get(id[i]);
			i = i + 1;
		}
		Code code = iterator.get(id[i]);
		return new Entry(id,code);
	}

	/**
	 * Return all bytecodes contained in this block as entries, but not
	 * including those recursively contained in sub-blocks. Each entry
	 * contains the bytecode itself, along with its ID.
	 *
	 * @return
	 */
	public List<? extends CodeBlock.Entry> entries() {
		ArrayList<CodeBlock.Entry> entries = new ArrayList<CodeBlock.Entry>();
		for (int i = 0; i != bytecodes.size(); ++i) {
			entries.add(new Entry(new int[] { i }, bytecodes.get(i)));
		}
		return entries;
	}

	/**
	 * Return all bytecodes contained in this block, including those
	 * recursively contained in sub-blocks. Each entry contains the bytecode
	 * itself, along with its ID.
	 *
	 * @return
	 */
	public List<? extends CodeBlock.Entry> allEntries() {
		ArrayList<CodeBlock.Entry> entries = new ArrayList<CodeBlock.Entry>();
		addAllEntries(this,entries);
		return entries;
	}
	
	/**
	 * Returns a reference to the internal bytecode array. Note that modifying
	 * this reference will modify the underlying array.
	 * 
	 * @return
	 */
	public List<Code> bytecodes() {
		return bytecodes;
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
	 * Provides a simple mechanism for accessing all bytecodes within a
	 * block, including those contained recursively in sub-blocks. An Entry
	 * includes the full identified for the bytecode, which identifies which
	 * sub-block the bytecode is contained in.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Entry {
		public final int[] id;
		public final Code code;

		public Entry(int[] id, Code code) {
			this.id = id;
			this.code = code;
		}

		public List<? extends CodeBlock.Entry> children() {
			if(code instanceof CodeBlock) {
				CodeBlock blk = (CodeBlock) code;
				ArrayList<CodeBlock.Entry> children = new ArrayList<CodeBlock.Entry>();
				for(int i=0;i!=blk.size();++i) {
					int[] nid = Arrays.copyOf(id, id.length+1);
					nid[id.length] = i;
					children.add(new Entry(nid,blk.get(i)));
				}
				return children;
			} else {
				return Collections.EMPTY_LIST;
			}
		}
	}

	private static void addAllEntries(CodeBlock blk,
			ArrayList<CodeBlock.Entry> entries, int... baseId) {
		for (int i = 0; i != blk.size(); ++i) {
			int[] id = Arrays.copyOf(baseId, baseId.length + 1);
			id[baseId.length] = i;
			Code code = blk.get(i);
			entries.add(new Entry(id, code));
			if (code instanceof CodeBlock) {
				addAllEntries((CodeBlock) code, entries, id);
			}
		}
	}
}