package wyjvm.attributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import wyjvm.lang.*;
import wyjvm.io.*;

public class LineNumberTable implements Code.Rewriteable,BytecodeAttribute {
	private ArrayList<Entry> entries;
	
	public static final class Entry {
		public final int start_pc;
		public final int line;
		
		public Entry(int start, int line) {
			this.start_pc = start;
			this.line = line;
		}
	}
	
	public LineNumberTable(Collection<Entry> entries) {
		this.entries = new ArrayList<Entry>(entries);
	}
	
	public void apply(List<Code.Rewrite> rewrites) {
		ArrayList<Integer> newMap = new ArrayList<Integer>();
		
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
		writer.write_u2(constantPool.get(new Constant.Utf8("LineNumberTable")));
		writer.write_u4(2 + (4 * entries.size()));
		writer.write_u2(entries.size());	
		for(Entry e : entries) {
			writer.write_u2(e.start_pc);
			writer.write_u2(e.line);
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
			output.println("line " + e.line + ": " + e.start_pc);			
		}
	}	
}
