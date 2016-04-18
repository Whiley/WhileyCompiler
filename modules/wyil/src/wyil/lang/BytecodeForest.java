package wyil.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import wycc.util.Pair;

/**
 * A bytecode forest can be thought of in different ways. For example, it can be
 * thought of as a forest of rooted trees; or, more simply, as an array of
 * bytecode sequences. Some bytecodes can be thought of as compound structures
 * containing nested blocks of bytecodes. In reality, such compound bytecodes
 * are not truly nested, but contain block identifiers representing their
 * contents.
 *
 * @author David J. Pearce
 *
 */
public class BytecodeForest {
	private final ArrayList<Register> registers;
	private final ArrayList<Integer> roots;
	private final ArrayList<Block> blocks;

	public BytecodeForest() {
		this(Collections.EMPTY_LIST);
	}
	
	public BytecodeForest(BytecodeForest forest) {
		this.registers = new ArrayList<Register>(forest.registers);
		this.roots = new ArrayList<Integer>(forest.roots);
		this.blocks = new ArrayList<Block>();
		for(int i=0;i!=forest.blocks.size();++i) {
			this.blocks.add(new Block(forest.blocks.get(i)));
		}		
	}
	
	public BytecodeForest(List<Register> registers) {
		this.registers = new ArrayList<Register>(registers);
		this.roots = new ArrayList<Integer>();
		this.blocks = new ArrayList<Block>();
	}

	// ===================================================================
	// Accessor Methods
	// ===================================================================

	/**
	 * Get the number of blocks within this code forest.
	 * 
	 * @return
	 */
	public int numBlocks() {
		return blocks.size();
	}

	/**
	 * Get the number of registered roots within this forest.
	 * 
	 * @return
	 */
	public int numRoots() {
		return roots.size();
	}
	
	public int numRegisters() {
		return registers.size();
	}
	
	/**
	 * Get the list of registers declared for this block. This list is mutable
	 * and may be read or written.
	 * 
	 * @return
	 */
	public List<Register> registers() {
		return registers;
	}
	
	/**
	 * Return a given root within the block. Roots are themselves indexed from
	 * 0.
	 * 
	 * @param i
	 * @return
	 */
	public int getRoot(int i) {
		return roots.get(i);
	}
	
	/**
	 * Get a specific block within this forest. The returned list is mutable and
	 * can be modified at will.
	 * 
	 * @param index
	 * @return
	 */
	public Block get(int index) {
		return blocks.get(index);
	}
	
	/**
	 * Get a specific register declared in this forest.
	 * 
	 * @param index
	 * @return
	 */
	public Register getRegister(int index) {
		return registers.get(index);
	}
		
	/**
	 * Get a specific bytecode withing this forest.
	 * @param index
	 * @return
	 */
	public Entry get(Index index) {
		return blocks.get(index.block).get(index.offset);
	}
	
	/**
	 * Add a new block to the forest, whilst returning its identifier.
	 * 
	 * @param block
	 * @return
	 */
	public int add(Block block) {
		blocks.add(block);
		return blocks.size()-1;
	}
	
	/**
	 * Add a new block to the forest which is also a root block. Root blocks are
	 * not subject to garbage collection.
	 * 
	 * @param block
	 * @return
	 */
	public int addAsRoot(Block block) {
		int index = add(block);
		roots.add(index);
		return index;
	}
	
	/**
	 * Mark block identifier as a root
	 * 
	 * @param root
	 */
	public void addRoot(int root) {
		roots.add(root);
	}
	
	/**
	 * Garbage collection unused blocks.
	 */
	public void gc() {
		
	}
	
	/**
	 * Represents a bytecode location within a code forest. This is simply a
	 * pair of the block identifier and the position within that block.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class Index {
		private int block;
		private int offset;
		
		public Index(int block, int offset) {
			this.block = block;
			this.offset = offset;
		}
		
		public int block() { return block; }
		public int offset() { return offset; }
		
		public boolean equals(Object o) {
			if(o instanceof Index) {
				Index i = (Index) o;
				return block == i.block && offset == i.offset;
			}
			return false;
		}
		
		public int hashCode() {
			return block ^ offset;
		}
		
		public Index next() {
			return new Index(block,offset+1);
		}
		
		public Index next(int i) {
			return new Index(block,offset+i);
		}
		
		public String toString() {
			return block + ":" + offset;
		}
	}
	
	/**
	 * Represents a sequence of bytecodes within a code forest.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Block extends ArrayList<Entry> {
		
		public Block() {
			super();
		}
		
		public Block(Collection<Entry> entries) {
			super(entries);
		}		
		public void add(Bytecode code, Attribute...attributes) {
			super.add(new Entry(code,attributes));
		}
		public void add(Bytecode code, List<Attribute> attributes) {
			super.add(new Entry(code,attributes));
		}
		public void add(int start, Bytecode code, Attribute...attributes) {
			super.add(start, new Entry(code,attributes));
		}
		public void add(int start, Bytecode code, List<Attribute> attributes) {
			super.add(start, new Entry(code,attributes));
		}
		public void set(int i, Bytecode code, Attribute...attributes) {
			super.set(i,new Entry(code,attributes));
		}
		public void set(int i, Bytecode code, List<Attribute> attributes) {
			super.set(i,new Entry(code,attributes));
		}
	}
	
	/**
	 * Represents an entry within a code block. This is a pairing of a bytecode
	 * and a list of bytecodes.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Entry extends Pair<Bytecode,List<Attribute>> {
		public Entry(Bytecode code, List<Attribute> attributes) {
			super(code,attributes);
		}
		public Entry(Bytecode code, Attribute... attributes) {
			super(code,Arrays.asList(attributes));
		}
		public Bytecode code() {
			return first();
		}
		public <T extends Attribute> T attribute(Class<T> clazz) {
			List<Attribute> attributes = second();
			for(int i=0;i!=attributes.size();++i) {
				Attribute a = attributes.get(i);
				if(clazz.isInstance(a)) {
					return (T) a;
				}
			}
			return null;
		}
		public List<Attribute> attributes() {
			return second();
		}		
	}
	
	/**
	 * Represents the declaration information associated with a given register.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Register {
		private final Type type;
		private final String name;

		public Register(Type type, String name) {
			this.type = type;
			this.name = name;
		}

		public Type type() {
			return type;
		}

		public String name() {
			return name;
		}
		
		public String toString() {
			return type + " " + name;
		}
	}
}