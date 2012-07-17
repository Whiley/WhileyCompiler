package wyil.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import wyil.Transform;
import wybs.lang.Path;
import wyil.lang.*;
import wyjvm.io.BinaryOutputStream;

public class WyilFileWriter implements Transform {
	private static final int MAJOR_VERSION = 0;
	private static final int MINOR_VERSION = 1;
	
	private BinaryOutputStream output;	
	private ArrayList<String> stringPool;
	private ArrayList<PATH_Item> pathPool;
	private ArrayList<NAME_Item> idPool;
	private ArrayList<CONSTANT_Item> constantPool;
	private ArrayList<TYPE_Item> typePool;
	
	@Override
	public void apply(WyilFile module) throws IOException {
		String filename = module.filename().replace(".whiley", ".wyasm");
		output = new BinaryOutputStream(new FileOutputStream(filename));
		
		buildPools(module);
		
		writeHeader(module);
	}	
	
	/**
	 * Write the header information for this WYIL file.
	 * 
	 * @param module
	 * @throws IOException
	 */
	private void writeHeader(WyilFile module)
			throws IOException {
		
		// first, write magic number
		output.write(0x57); // W
		output.write(0x59); // Y
		output.write(0x49); // I
		output.write(0x4C); // L
		output.write(0x46); // F
		output.write(0x49); // I
		output.write(0x4C); // L
		output.write(0x45); // E
		
		// second, write the file version number 
		output.write_uv(MAJOR_VERSION); 
		output.write_uv(MINOR_VERSION); 
		
		// third, write the various pool sizes
		output.write_uv(stringPool.size());
		output.write_uv(pathPool.size());
		output.write_uv(idPool.size());
		output.write_uv(constantPool.size());
		output.write_uv(typePool.size());		
		
		// finally, write the number of blocks
		output.write_uv(module.declarations().size());
	}
	
	private void buildPools(WyilFile module) {
		
	}
	
	/**
	 * An PATH_Item represents a path item.
	 * 
	 * @author David J. Pearce
	 *
	 */
	private class PATH_Item {
		/**
		 * The index in the path pool of the parent for this item, or -1 if it
		 * has not parent.
		 */
		public final int parentIndex;
		
		/**
		 * The index of this path component in the string pool
		 */
		public final int stringIndex;
		
		public PATH_Item(int parentIndex, int stringIndex) {
			this.parentIndex = parentIndex;
			this.stringIndex = stringIndex;
		}		
	}
	
	private enum NAME_Kind {
		PACKAGE(0), 
		MODULE(1), 
		CONSTANT(2), 
		TYPE(3), 
		FUNCTION(4), 
		METHOD(5);

		private final int kind;

		private NAME_Kind(int kind) {
			this.kind = kind;
		}

		public int kind() {
			return kind;
		}
	}
	
	/**
	 * A NAME_Item represents a named path item, such as a package, module or
	 * something within a module (e.g. a function or method declaration).
	 * 
	 * @author David J. Pearce
	 * 
	 */
	private class NAME_Item {
		/**
		 * The kind of name item this represents.
		 */
		public final NAME_Kind kind;
		
		/**
		 * Index of path for this item in path pool
		 */
		public final int pathIndex;
		
		public NAME_Item(NAME_Kind kind, int pathIndex) {
			this.kind = kind;
			this.pathIndex = pathIndex;
		}		
	}		
	
	
	private enum CONSTANT_Kind {
		NULL(0), 
		BOOL(1),
		BYTE(2),
		CHAR(3),
		INT(4),
		RATIONAL(5),
		STRING(6),
		LIST(7),
		SET(8),
		MAP(9),
		TUPLE(10),
		RECORD(11),
		FUNCTION_OR_METHOD(12);

		private final int kind;

		private CONSTANT_Kind(int kind) {
			this.kind = kind;
		}

		public int kind() {
			return kind;
		}
	}

	/**
	 * Represents a WYIL Constant value.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	private class CONSTANT_Item {
		public final CONSTANT_Kind kind;

		public final Object data;

		public CONSTANT_Item(CONSTANT_Kind kind, Object data) {
			this.kind = kind;
			this.data = data;
		}
	}
	
	/**
	 * A pool item represents a WYIL type. For example, a <code>int</code> type
	 * or <code>[int]</code> (i.e. list of int) type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	private class TYPE_Item {
		/**
		 * Type kind (e.g. INT, REAL, etc)
		 */
		public final int kind;
		
		/**
		 * Indices of any children in type pool
		 */
		public final int[] children;
		
		public TYPE_Item(int kind, int[] children) {
			this.kind = kind;
			this.children = Arrays.copyOf(children, children.length);
		}
	}
}
