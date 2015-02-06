// Copyright (c) 2014, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil.util;

import java.util.ArrayList;

import wyil.lang.Attribute;
import wyil.lang.CodeBlock;

/**
 * Provides a generic implementation of <code>Attribute.Map</code> which can be
 * used as a starting point for constructing specific instances of
 * <code>Attribute.Map</code>. The primary objective of this class is to
 * associate entries with given (nested) bytecode locations in the presence of
 * insertions and deletions.
 *
 * @author David J. Pearce
 *
 */
public class AbstractAttributeMap<T extends Attribute> {
	private final Block<T> root = new Block<T>();

	/**
	 * Get the attribute associated with a given bytecode location. If the
	 * location doesn't exist, then an exception is raised.
	 *
	 * @param location
	 *            Location to look for attribute. Cannot be an array of size 0.
	 * @return
	 */
	public T get(CodeBlock.Index location) {
		Block<T> blk = root;
		Entry<T> e = null;
		int[] components = location.toArray();
		
		for(int i = 0; i!=components.length;++i) {
			int loc = components[i];

			if(loc >= blk.entries.size()) {	
				return null;
			}

			// Second, examine the entry at the specified location.
			e = blk.entries.get(loc);

			if(e instanceof Block) {
				blk = (Block<T>) e;
			} else if(e == null) {
				// This indicate an invalid location identifier because the
				// location exists, but is not of the right kind.
				return null;
			}
		}

		// Finally, set the attribute
		return e.attribute;
	}

	/**
	 * Associate an attribute with a given bytecode location. If the location
	 * doesn't exist, then it is created. However, if the location is invalid,
	 * then an exception will still be raised.
	 *
	 * @param location
	 *            Location to for attribute to update. Cannot be an array of
	 *            size 0.
	 * @return
	 */
	public void put(CodeBlock.Index location, T data) {
		Block<T> blk = root;
		Entry<T> e = null;

		int[] components = location.toArray();
		
		for(int i = 0; i!=components.length;++i) {
			int loc = components[i];

			// First, make sure there is enough room for this location!
			blk.ensureSize(loc+1);

			// Second, examine the entry at the specified location.
			e = blk.entries.get(loc);

			if(e instanceof Block) {
				blk = (Block<T>) e;
			} else if(e == null) {
				// Location doesn't exist, so create it.
				Block<T> nblk = new Block<T>();
				blk.entries.set(loc,nblk);
				blk = nblk;
				e = blk;
			} else {
				// This indicate an invalid location identifier because the
				// location exists, but is not of the right kind.
				throw new IllegalArgumentException("invalid entry identifier");
			}
		}

		// Finally, set the attribute
		e.attribute = data;		
	}
	
	/**
	 * Insert meta-data associated with a given bytecode a given location.
	 * Here, a location is a n-dimensional numeric identifier to capture
	 * bytecodes which may be nested inside other bytecodes. Note that this
	 * will "shift down" all identifies which logically follow the given
	 * identify in its block, and those contained.
	 * 
	 * @param location
	 *            The location to be insert
	 * @param data
	 *            The data to assign to the given location
	 */	
	public void insert(CodeBlock.Index location, T data) {
		Block<T> blk = root;		

		int[] components = location.toArray();
		
		for(int i = 0; i!=components.length-1;++i) {
			int loc = components[i];

			// First, make sure there is enough room for this location!
			blk.ensureSize(loc+1);

			// Second, examine the entry at the specified location.
			Entry<T> e = blk.entries.get(loc);

			if(e instanceof Block) {
				blk = (Block<T>) e;
			} else if(e == null) {
				// Location doesn't exist, so create it.
				Block<T> nblk = new Block<T>();
				blk.entries.set(loc,nblk);
				blk = nblk;
				e = blk;
			} else {
				// This indicate an invalid location identifier because the
				// location exists, but is not of the right kind.
				throw new IllegalArgumentException("invalid entry identifier");
			}
		}

		int insertionPoint = components[components.length-1]; 
		Entry<T> e = new Block<T>();
		e.attribute = data;
		blk.ensureSize(insertionPoint); // ensure there is enough space
		blk.entries.add(insertionPoint,e);		
	}
	
	public void print() {
		print(root);
	}
	
	private static class Entry<T extends Attribute> {
		public T attribute;

		public Entry(T attribute) {
			this.attribute = attribute;
		}
	}

	private static class Block<T extends Attribute> extends Entry<T> {
		private final ArrayList<Entry<T>> entries = new ArrayList<Entry<T>>();

		public Block() {
			super(null);
		}

		public Block(T attribute) {
			super(attribute);
		}

		public void ensureSize(int minSize) {
			for(int i=entries.size();i<minSize;++i) {
				entries.add(null);
			}
		}
	}

	public static void print(Block<?> blk) {
		print("",blk);
	}

	public static void print(String prefix, Block<?> blk) {
		for(int i=0;i!=blk.entries.size();++i) {
			Entry<?> e = blk.entries.get(i);
			if(e == null) {
				System.out.println(prefix + "." + i + " [null]");
			} else {
				System.out.println(prefix + "." + i + " = " + e.attribute);
			}
			if(e instanceof Block) {
				print(prefix + "." + i,(Block)e);
			}
		}
	}
}
