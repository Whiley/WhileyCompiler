package wyil.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyil.lang.CodeBlock.Entry;

/**
 * <p>
 * An attributed code block represents a bytecode block where each bytecode can
 * be attributed with a pre-defined set of attributes. The block maintains an
 * <code>Attribute.Map</code> for each different kind of attribute, and ensures
 * that these maps are maintained in the presence of insertions, deletions and
 * updates.
 * </p>
 *
 * <p>
 * NOTE: an attributed block should never be used as part of an actual bytecode.
 * This is because it does not (indeed, cannot) implement the necessary equality
 * functions.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class AttributedCodeBlock extends CodeBlock {

	/**
	 * The map from attribute kinds to the intances of Attribute.Map responsible
	 * for storing them.
	 */
	private Map<Class<? extends Attribute>,Attribute.Map<? extends Attribute>> attributes;

	/**
	 * The ID of this block. For root blocks, this is empty. However, for nested
	 * blocks, this is the ID of the enclosing bytecode instruction.
	 */
	private final int[] ID;

	public AttributedCodeBlock(Attribute.Map<? extends Attribute>... attributeMaps) {
		this.attributes = new HashMap<Class<? extends Attribute>, wyil.lang.Attribute.Map<? extends Attribute>>();
		for (Attribute.Map<?> map : attributeMaps) {
			this.attributes.put(map.type(), map);
		}
		this.ID = new int[0];
	}

	public AttributedCodeBlock(Collection<Attribute.Map<? extends Attribute>> attributeMaps) {
		this.attributes = new HashMap<Class<? extends Attribute>, wyil.lang.Attribute.Map<? extends Attribute>>();
		for (Attribute.Map<?> map : attributeMaps) {
			this.attributes.put(map.type(), map);
		}
		this.ID = new int[0];
	}

	public AttributedCodeBlock(Collection<Code> bytecodes,
			Attribute.Map<? extends Attribute>... attributeMaps) {
		super(bytecodes);
		this.attributes = new HashMap<Class<? extends Attribute>, wyil.lang.Attribute.Map<? extends Attribute>>();
		for (Attribute.Map<?> map : attributeMaps) {
			this.attributes.put(map.type(), map);
		}
		this.ID = new int[0];
	}

	public AttributedCodeBlock(AttributedCodeBlock block) {
		super(block.bytecodes);
		this.attributes = new HashMap<Class<? extends Attribute>, wyil.lang.Attribute.Map<? extends Attribute>>();
		this.attributes.putAll(block.attributes);
		this.ID = block.ID;
	}

	// ===================================================================
	// Get Methods
	// ===================================================================

	public AttributedCodeBlock.Entry getEntry(int... id) {
		CodeBlock iterator = this;
		int i=0;
		while (i < id.length - 1) {
			iterator = (CodeBlock) iterator.get(id[i]);
			i = i + 1;
		}
		Code code = iterator.get(id[i]);
		// FIXME: need to include attributes!
		return new Entry(id,code);
	}

	public <T extends Attribute> T attribute(int[] id, Class<T> kind) {
		Attribute.Map<T> map = (Attribute.Map<T>) attributes.get(kind);
		return map.get(id);
	}

	/**
	 * Return all bytecodes contained in this block as entries, but not
	 * including those recursively contained in sub-blocks. Each entry
	 * contains the bytecode itself, along with its ID.
	 *
	 * @return
	 */
	public List<? extends AttributedCodeBlock.Entry> entries() {
		ArrayList<AttributedCodeBlock.Entry> entries = new ArrayList<AttributedCodeBlock.Entry>();
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
	public List<? extends AttributedCodeBlock.Entry> allEntries() {
		ArrayList<AttributedCodeBlock.Entry> entries = new ArrayList<AttributedCodeBlock.Entry>();
		addAllEntries(this,entries);
		return entries;
	}

	/**
	 * <p>
	 * Construct a temporary sub-block for use in creating an attributed
	 * compound bytecode (e.g. a loop). In essence, the created subblock is used
	 * to accumulate both bytecodes for the compound, along with attributes
	 * which are correctly associated.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> The compound bytecode is expected to be added next into this
	 * (i.e. the outer) AttributedCodeBlock. If other bytecodes are added
	 * inbetween, then attributes may not be correctly associated.
	 * </p>
	 *
	 * @return
	 */
	public AttributedCodeBlock createSubBlock() {
		AttributedCodeBlock r = new AttributedCodeBlock(attributes.values());
		int[] nid = Arrays.copyOf(ID, ID.length+1);
		nid[ID.length] = bytecodes.size();
		return r;
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
	 * @param attributes
	 *            --- attributes associated with bytecode.
	 */
	public boolean add(Code code, Attribute... attributes) {
		// TODO: actually add the attributes
		return add(code);
	}

	/**
	 * Append a bytecode onto the end of this block. It is assumed that the
	 * bytecode employs the same environment as this block.
	 *
	 * @param code
	 *            --- bytecode to append
	 * @param attributes
	 *            --- attributes associated with bytecode.
	 */
	public boolean add(Code code, Collection<Attribute> attributes) {
		// TODO: actually add the attributes
		return add(code);
	}

	/**
	 * Add all bytecodes to this block from another include all attributes
	 * associated with each bytecode.
	 *
	 * @param block
	 */
	public void addAll(AttributedCodeBlock block) {
		// FIXME:
		throw new RuntimeException("implement me!");
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
	 * @param attributes
	 */
	public void add(int index, Code code, Attribute... attributes) {
		// TODO: actually add the attributes
		add(index,code);
	}

	/**
	 * <p>Insert a bytecode at a given position in this block. It is assumed that
	 * the bytecode employs the same environment as this block. The bytecode at
	 * the given position (and any after it) are shifted one position down.</p>
	 *
	 * @param index --- position to insert at.
	 * @param code --- bytecode to insert at the given position.
	 * @param attributes
	 */
	public void add(int index, Code code, Collection<Attribute> attributes) {
		// TODO: actually add the attributes
		add(index,code);
	}

	// ===================================================================
	// Replace and Remove Methods
	// ===================================================================

	/**
	 * <p>
	 * Replace the bytecode at a given position in this block with another. It
	 * is assumed that the bytecode employs the same environment as this block.
	 * </p>
	 *
	 * @param index --- position of bytecode to replace.
	 * @param code --- bytecode to replace with.
	 * @param attributes
	 */
	public void set(int index, Code code, Attribute... attributes) {
		// TODO: actually update the attributes
		set(index,code);
	}

	/**
	 * <p>
	 * Replace the bytecode at a given position in this block with another. It
	 * is assumed that the bytecode employs the same environment as this block.
	 * </p>
	 *
	 * @param index --- position of bytecode to replace.
	 * @param code --- bytecode to replace with.
	 * @param attributes
	 */
	public void set(int index, Code code, Collection<Attribute> attributes) {
		// TODO: actually update the attributes
		set(index,code);
	}

	public static class Entry extends CodeBlock.Entry {
		public final Attribute[] attributes;

		public Entry(int[] id, Code code, Attribute... attributes) {
			super(id, code);
			this.attributes = Arrays.copyOf(attributes, attributes.length);
		}

		public Entry(int[] id, Code code, Collection<Attribute> attributes) {
			super(id, code);
			this.attributes = attributes.toArray(new Attribute[attributes
					.size()]);
		}

		public Collection<Attribute> attributes() {
			// FIXME: this is a temporary hack?
			ArrayList<Attribute> r = new ArrayList<Attribute>();
			Collections.addAll(r,attributes);
			return r;
		}

		public List<? extends AttributedCodeBlock.Entry> children() {
			if (code instanceof CodeBlock) {
				CodeBlock blk = (CodeBlock) code;
				ArrayList<AttributedCodeBlock.Entry> children = new ArrayList<AttributedCodeBlock.Entry>();
				for (int i = 0; i != blk.size(); ++i) {
					int[] nid = Arrays.copyOf(id, id.length + 1);
					nid[id.length] = i;
					// FIXME: include attributes here!
					children.add(new Entry(nid, blk.get(i)));
				}
				return children;
			} else {
				return Collections.EMPTY_LIST;
			}
		}
	}

	private static void addAllEntries(CodeBlock blk,
			ArrayList<AttributedCodeBlock.Entry> entries, int... baseId) {
		for (int i = 0; i != blk.size(); ++i) {
			int[] id = Arrays.copyOf(baseId, baseId.length + 1);
			id[baseId.length] = i;
			Code code = blk.get(i);
			// FIXME: include attributes
			entries.add(new Entry(id, code));
			if (code instanceof CodeBlock) {
				addAllEntries((CodeBlock) code, entries, id);
			}
		}
	}
}