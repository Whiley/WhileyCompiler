package wyil.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyil.lang.Attribute;
import wyil.lang.Code;
import wyil.lang.CodeBlock;
import wyil.lang.CodeBlock.Index;

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
	private Map<Class<Attribute>,Attribute.Map<Attribute>> attributes;

	/**
	 * The ID of this block. For root blocks, this is empty. However, for nested
	 * blocks, this is the ID of the enclosing bytecode instruction.
	 */
	private final CodeBlock.Index ID;

	public AttributedCodeBlock(Attribute.Map<? extends Attribute>... attributeMaps) {
		this.attributes = new HashMap<Class<Attribute>, wyil.lang.Attribute.Map<Attribute>>();
		for (Attribute.Map map : attributeMaps) {
			this.attributes.put(map.type(), map);
		}
		this.ID = null;
	}

	public AttributedCodeBlock(Collection<? extends Attribute.Map<Attribute>> attributeMaps) {
		this.attributes = new HashMap<Class<Attribute>, wyil.lang.Attribute.Map<Attribute>>();
		for (Attribute.Map map : attributeMaps) {
			this.attributes.put(map.type(), map);
		}
		this.ID = null;
	}

	public AttributedCodeBlock(Collection<Code> bytecodes,
			Attribute.Map<? extends Attribute>... attributeMaps) {
		super(bytecodes);
		this.attributes = new HashMap<Class<Attribute>, wyil.lang.Attribute.Map<Attribute>>();
		for (Attribute.Map map : attributeMaps) {
			this.attributes.put(map.type(), map);
		}
		this.ID = null;
	}
	
	/**
	 * This constructor is used when creating a subblock only. They key is that
	 * updates to the attributes of this block are visible to the enclosing
	 * block as well.
	 * 
	 * @param index
	 * @param block
	 */
	private AttributedCodeBlock(CodeBlock.Index index, AttributedCodeBlock block) {
		super(block.bytecodes);
		this.attributes = new HashMap<Class<Attribute>, wyil.lang.Attribute.Map<Attribute>>();
		this.attributes.putAll(block.attributes);
		this.ID = index;
	}

	// ===================================================================
	// Get Methods
	// ===================================================================

	/**
	 * Return the attribute of a given kind associated with a bytecode. Such an
	 * attribute may not exist, in which case null is returned.
	 * 
	 * @param id
	 * @param kind
	 * @return
	 */
	public <T extends Attribute> T attribute(CodeBlock.Index id, Class<T> kind) {
		Attribute.Map<T> map = (Attribute.Map<T>) attributes.get(kind);
		if(map != null) {
			return map.get(id);
		} else {
			// no map of this kind exists.
			return null;
		}
	}

	public List<Attribute> attributes(Index index) {
		ArrayList<Attribute> results = new ArrayList<Attribute>();
		for(Attribute.Map<?> map : attributes.values()) {
			Attribute attr = map.get(index);
			if(attr != null) {
				results.add(attr);
			}
		}
		return results;
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
		CodeBlock.Index index = new CodeBlock.Index(ID,0); 
		return new AttributedCodeBlock(index,this);
	}

	// ===================================================================
	// Append Methods
	// ===================================================================

	/**
	 * Append a bytecode onto the end of this block, along with any attributes
	 * to be associated with that bytecode.
	 * 
	 * @param code
	 *            --- bytecode to append
	 * @param attributes
	 *            --- attributes associated with bytecode.
	 */
	public boolean add(Code code, Attribute... attributes) {
		CodeBlock.Index index = new CodeBlock.Index(ID,size());
		addAll(index,attributes);
		return add(code);
	}

	/**
	 * Append a bytecode onto the end of this block, along with any attributes
	 * to be associated with that bytecode.
	 * 
	 * @param code
	 *            --- bytecode to append
	 * @param attributes
	 *            --- attributes associated with bytecode.
	 */
	public boolean add(Code code, Collection<Attribute> attributes) {
		CodeBlock.Index index = new CodeBlock.Index(ID, size());
		addAll(index,attributes);	
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
	
	// ===================================================================
	// Helper Methods
	// ===================================================================
	private void addAll(CodeBlock.Index index, Collection<Attribute> attributes) {
		// Go through and add each attribute at the given index.
		for (Attribute attribute : attributes) {
			Attribute.Map<Attribute> map = this.attributes.get(attribute
					.getClass());
			// First, check whether an attribute map for this kind of attribute
			// exists.
			if (map != null) {
				// Yes, so add it.
				map.put(index, attribute);
			}
		}
	}
	
	private void addAll(CodeBlock.Index index, Attribute... attributes) {
		// Go through and add each attribute at the given index.
		for (Attribute attribute : attributes) {
			Attribute.Map<Attribute> map = this.attributes.get(attribute
					.getClass());
			// First, check whether an attribute map for this kind of attribute
			// exists.
			if (map != null) {
				// Yes, so add it.
				map.put(index, attribute);
			}
		}
	}
}