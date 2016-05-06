package wyil.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;
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
	private final ArrayList<Location> locations;
	private final ArrayList<Integer> roots;
	private final ArrayList<Block> blocks;

	public BytecodeForest() {
		this(Collections.EMPTY_LIST);
	}
	
	public BytecodeForest(BytecodeForest forest) {
		this.locations = new ArrayList<Location>(forest.locations);
		this.roots = new ArrayList<Integer>(forest.roots);
		this.blocks = new ArrayList<Block>();
		for(int i=0;i!=forest.blocks.size();++i) {
			this.blocks.add(new Block(forest.blocks.get(i)));
		}		
	}
	
	public BytecodeForest(List<Location> locations) {
		this.locations = new ArrayList<Location>(locations);
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
	
	public int numLocations() {
		return locations.size();
	}
	
	/**
	 * Get the list of registers declared for this block. This list is mutable
	 * and may be read or written.
	 * 
	 * @return
	 */
	public List<Location> locations() {
		return locations;
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
	
	public int[] getRoots() {
		int[] rs = new int[roots.size()];
		for(int i=0;i!=rs.length;++i) {
			rs[i] = roots.get(i);
		}
		return rs;
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
	 * Get a specific location declared in this forest.
	 * 
	 * @param index
	 * @return
	 */
	public Location getLocation(int index) {
		return locations.get(index);
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
		public void add(Bytecode.Stmt code, Attribute...attributes) {
			super.add(new Entry(code,attributes));
		}
		public void add(Bytecode.Stmt code, List<Attribute> attributes) {
			super.add(new Entry(code,attributes));
		}
		public void add(int start, Bytecode.Stmt code, Attribute...attributes) {
			super.add(start, new Entry(code,attributes));
		}
		public void add(int start, Bytecode.Stmt code, List<Attribute> attributes) {
			super.add(start, new Entry(code,attributes));
		}
		public void set(int i, Bytecode.Stmt code, Attribute...attributes) {
			super.set(i,new Entry(code,attributes));
		}
		public void set(int i, Bytecode.Stmt code, List<Attribute> attributes) {
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
	public static class Entry extends Pair<Bytecode.Stmt,List<Attribute>> {
		public Entry(Bytecode.Stmt code, List<Attribute> attributes) {
			super(code,attributes);
		}
		public Entry(Bytecode.Stmt code, Attribute... attributes) {
			super(code,Arrays.asList(attributes));
		}
		public Bytecode.Stmt code() {
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
	 * Represents a location use to hold a value of some kind. This location
	 * could correspond to a local variable, or an intermediate value.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public interface Location {
		public int size();
		public Type type(int i);
	}
	
	/**
	 * Represents the result of an intermediate computation which is assigned to
	 * an anonymous location.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Operand extends SyntacticElement.Impl implements Location {
		private final Type[] types;
		private final Bytecode.Expr value;		
				
		public Operand(Type type, Bytecode.Expr value, List<Attribute> attributes) {
			super(attributes);
			this.types = new Type[]{type};
			this.value = value;
		}
		
		public Operand(Type[] types, Bytecode.Expr value, List<Attribute> attributes) {
			super(attributes);
			this.types = types;
			this.value = value;
		}
		
		public Bytecode.Expr value() {
			return value;
		}
		
		public int size() {
			return types.length;
		}
		
		public Type type(int i) {
			return types[i];
		}
		
		public String toString() {
			return Arrays.toString(types) + " " + value;
		}
	}
	
	/**
	 * Represents the declaration information associated with a given named location (i.e. variable).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Variable extends SyntacticElement.Impl implements Location {
		private final Type type;
		private final String name;

		public Variable(Type type, String name, List<Attribute> attributes) {
			super(attributes);
			this.type = type;
			this.name = name;
		}

		public String name() {
			return name;
		}
		
		public int size() {
			return 1;
		}
		
		public Type type(int i) {
			if(i != 0) {
				throw new IllegalArgumentException("invalid type index");
			}
			return type;
		}
		
		public String toString() {
			return type + " " + name;
		}
	}
}