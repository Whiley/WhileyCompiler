// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
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

package wyil.lang;

import java.util.*;

import wyil.util.*;

public final class Block implements Iterable<Block.Entry> {
	private final ArrayList<Entry> stmts;	
	
	public Block() {
		this.stmts = new ArrayList<Entry>();
	}
	
	public Block(Collection<Entry> stmts) {
		this.stmts = new ArrayList<Entry>();
		for(Entry s : stmts) {
			add(s.code,s.attributes());
		}
	}
	
	public void add(Code c, Attribute... attributes) {
		stmts.add(new Entry(c,attributes));
	}
	
	public void add(Code c, Collection<Attribute> attributes) {
		stmts.add(new Entry(c,attributes));		
	}
	
	public void add(int idx, Code c, Attribute... attributes) {
		stmts.add(idx,new Entry(c,attributes));
	}
	
	public void add(int idx, Code c, Collection<Attribute> attributes) {
		stmts.add(idx,new Entry(c,attributes));
	}
	
	public void addAll(Collection<Entry> stmts) {
		for(Entry s : stmts) {
			add(s.code,s.attributes());
		}
	}
	
	public void addAll(int idx, Collection<Entry> stmts) {		
		for(Entry s : stmts) {
			add(idx++,s.code,s.attributes());
		}
	}
	
	public void addAll(Block stmts) {
		for(Entry s : stmts) {
			add(s.code,s.attributes());
		}
	}
	
	public void addAll(int idx, Block stmts) {
		for(Entry s : stmts) {
			add(idx++, s.code,s.attributes());
		}
	}
	
	public int size() {
		return stmts.size();
	}
	
	public Entry get(int index) {
		return stmts.get(index);
	}
	
	public void set(int index, Code code, Attribute... attributes) {
		stmts.set(index,new Entry(code,attributes));
	}
	
	public void set(int index, Code code, Collection<Attribute> attributes) {
		stmts.set(index, new Entry(code, attributes));
	}
	
	public void remove(int index) {
		stmts.remove(index);
	}
	
	public Block subblock(int start, int end) {
		return new Block(stmts.subList(start, end));
	}
	
	public Iterator<Entry> iterator() {
		return stmts.iterator();
	}
	
	public String toString() {
		String r = "[";
		
		boolean firstTime=true;
		for(Entry s : stmts) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += s.toString();
		}
		
		return r + "]";
	}

	private static int _idx=0;
	public static String freshLabel() {
		return "blklab" + _idx++;
	}

	/**
	 * An Entry object represents a bytecode and those attributes currently
	 * associated with it (if any).
	 * 
	 * @author djp
	 * 
	 */
	public static final class Entry extends SyntacticElement.Impl {
		public final Code code;
		
		public Entry(Code code, Attribute... attributes) {
			super(attributes);
			this.code = code;
		}
		
		public Entry(Code code, Collection<Attribute> attributes) {
			super(attributes);
			this.code = code;
		}
				
		public String toString() {
			String r = code.toString();
			if(attributes().size() > 0) {
				r += " # ";
				boolean firstTime=true;
				for(Attribute a : attributes()) {
					if(!firstTime) {
						r += ", ";
					}
					firstTime=false;
					r += a;
				}
			}
			return r;
		}
	}
}
