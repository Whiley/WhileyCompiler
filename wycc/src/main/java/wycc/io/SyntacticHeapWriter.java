// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wycc.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jbfs.util.Pair;
import wycc.lang.SyntacticHeap;
import wycc.lang.SyntacticItem;


/**
 * <p>
 * Responsible for writing a WyilFile to an output stream in binary form. The
 * binary format is structured to given maximum flexibility and to avoid
 * built-in limitations in terms of e.g. maximum sizes, etc.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public abstract class SyntacticHeapWriter {
	protected final BinaryOutputStream out;
	protected final SyntacticHeap.Schema schema;

	public SyntacticHeapWriter(OutputStream output, SyntacticHeap.Schema schema) {
		this.out = new BinaryOutputStream(output);
		this.schema = schema;
	}

	public void close() throws IOException {
		out.close();
	}

	public void write(SyntacticHeap module) throws IOException {
		// first, write magic number
		writeHeader();
		// second, write syntactic items
		out.write_uv(module.size());
		// third, write root item
		out.write_uv(module.getRootItem().getIndex());
		// Write out each item in turn
		for (int i = 0; i != module.size(); ++i) {
			writeSyntacticItem(module.getSyntacticItem(i));
		}
		// finally, flush to disk
		out.flush();
	}

	public abstract void writeHeader() throws IOException;

	public void writeSyntacticItem(SyntacticItem item) throws IOException {
		int d = out.length();
		// Write opcode
		out.write_u8(item.getOpcode());
		// Write operands
		writeOperands(item);
		// Write data (if any)
		writeData(item);
		//
		int size = out.length() - d;
		record(item.getOpcode(),size);
		// Pad to next byte boundary
		out.pad_u8();
	}

	private void writeOperands(SyntacticItem item) throws IOException {
		// Determine operand layout
		SyntacticItem.Operands layout = schema.getDescriptor(item.getOpcode()).getOperandLayout();
		// Write operands according to layout
		switch(layout) {
		case MANY:
			out.write_uv(item.size());
			break;
		default:
			if(layout.ordinal() != item.size()) {
				throw new IllegalArgumentException(
						"invalid number of operands for \"" + item.getClass().getSimpleName() + "\" (got " + item.size()
								+ ", expecting " + layout.ordinal() + ")");
			}
		}
		//
		for (int i = 0; i != item.size(); ++i) {
			SyntacticItem operand = item.get(i);
			out.write_uv(operand.getIndex());
		}
	}

	public void writeData(SyntacticItem item) throws IOException {
		// Determine data layout
		SyntacticItem.Data layout = schema.getDescriptor(item.getOpcode()).getDataLayout();
		byte[] bytes = item.getData();
		// Write data according to layout
		switch (layout) {
		case MANY:
			out.write_uv(bytes.length);
			break;
		default:
			if(bytes != null && layout.ordinal() != bytes.length) {
				throw new IllegalArgumentException(
						"invalid number of data bytes for " + item.getClass().getSimpleName() + " (got " + bytes.length
								+ ", expecting " + layout.ordinal() + ")");
			} else if(bytes == null && layout.ordinal() != 0) {
				throw new IllegalArgumentException(
						"invalid number of data bytes for " + item.getClass().getSimpleName() + " (got none, expecting "
								+ layout.ordinal() + ")");
			}
		}
		if(bytes != null) {
			for (int i = 0; i != bytes.length; ++i) {
				out.write_u8(bytes[i]);
			}
		}
	}

	public static final Map<Integer,Metric> metrics = new HashMap<>();

	private static class Metric {
		public int bytes;
		public int count;

		public Metric record(int size) {
			count++;
			bytes += size;
			return this;
		}

		@Override
		public int hashCode() {
			return bytes+count;
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof Metric) {
				Metric m = (Metric) o;
				return bytes == m.bytes && count == m.count;
			}
			return false;
		}
	}

	public static void record(int opcode, int size) {
		Metric old = metrics.get(opcode);
		old = old == null ? new Metric() : old;
		metrics.put(opcode, old.record(size));
	}

	public static void printMetrics(SyntacticHeap.Schema schema) {
		List<Pair<Integer,Metric>> items = new ArrayList();
		for(int i=0;i!=255;++i) {
			Metric c = metrics.get(i);
			if(c != null) {
				items.add(new Pair<>(i,c));
			}
		}
		Collections.sort(items, new Comparator<Pair<Integer,Metric>>() {

			@Override
			public int compare(Pair<Integer, Metric> o1, Pair<Integer, Metric> o2) {
				int c = Integer.compare(o1.second().bytes, o2.second().bytes);
				if(c == 0) {
					return o1.first().compareTo(o2.first());
				} else {
					return c;
				}
			}

		});
		int total = 0;
		for(Pair<Integer,Metric> p : items) {
			String n = schema.getDescriptor(p.first()).getMnemonic();
			total += p.second().bytes;
			System.out.println(n + ": " + p.second().bytes + " bytes (" + p.second().count + " items)");
		}
		System.out.println();
		System.out.println(total + " bytes");
	}
}
