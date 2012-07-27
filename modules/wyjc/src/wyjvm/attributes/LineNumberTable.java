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

package wyjvm.attributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import wyjvm.lang.*;
import wyjvm.io.*;

public class LineNumberTable implements Code.BytecodeMapAttribute {
	private ArrayList<Entry> entries;
	
	public static final class Entry {
		public final int start;
		public final int line;
		
		public Entry(int start, int line) {
			this.start = start;
			this.line = line;
		}
	}
	
	public LineNumberTable(Collection<Entry> entries) {
		this.entries = new ArrayList<Entry>(entries);
	}
	
	public void apply(List<Code.Rewrite> rewrites) {
		// FIXME: this should be implemented		
	}
	
	public List<Entry> entries() {
		return Collections.unmodifiableList(entries);
	}
	
	public String name() {
		return "LineNumberTable";
	}
	
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {
		// should never be called
	}
	
	public void write(int[] bytecodeOffsets, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {
		writer.write_u16(constantPool.get(new Constant.Utf8("LineNumberTable")));
		writer.write_u32(2 + (4 * entries.size()));
		writer.write_u16(entries.size());	
		for(Entry e : entries) {
			writer.write_u16(bytecodeOffsets[e.start]);
			writer.write_u16(e.line);
		}
	}

	
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {
		Constant.addPoolItem(new Constant.Utf8("LineNumberTable"), constantPool);
	}
		
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {
		output.println("  LineNumberTable:");
		for(Entry e : entries) {
			output.print("   ");
			output.println("line " + e.line + ": " + e.start);			
		}
	}	
}
