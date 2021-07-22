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

import java.io.PrintWriter;

import wycc.lang.SyntacticHeap;
import wycc.lang.SyntacticItem;

public class SyntacticHeapPrinter {
	private final PrintWriter out;

	/**
	 * Signals whether or not to show garbage.
	 */
	private final boolean showGarbage;

	public SyntacticHeapPrinter(PrintWriter out, boolean showGarbage) {
		this.out = out;
		this.showGarbage = showGarbage;
	}

	public void print(SyntacticHeap heap) {
		boolean[] reachable = new boolean[heap.size()];
		search(heap.getRootItem().getIndex(),heap, reachable);
		//
		out.println("root=" + heap.getRootItem().getIndex());
		for(int i=0;i!=heap.size();++i) {
			SyntacticItem item = heap.getSyntacticItem(i);
			if(reachable[i] || showGarbage) {
				out.print("#" + i);
				out.print(" ");
				out.print(item.getClass().getSimpleName());
				if(item.size() > 0) {
					out.print("(");
					for(int j=0;j!=item.size();++j) {
						if(j!=0) {
							out.print(",");
						}
						out.print(item.get(j).getIndex());
					}
					out.print(")");
				}
				byte[] data = item.getData();
				if(data != null && data.length > 0) {
					out.print("[");
					for(int j=0;j!=data.length;++j) {
						if(j!=0) {
							out.print(",");
						}
						out.print("0x" + Integer.toHexString(data[j]));
					}
					out.print("] ");
					// FIXME: there should be a better way of doing this really
					out.print(item);
				}
				out.println();
			}
		}
		out.flush();
	}

	private void search(int index, SyntacticHeap heap, boolean[] visited) {
		visited[index] = true;
		//
		SyntacticItem item = heap.getSyntacticItem(index);
		for (int j = 0; j != item.size(); ++j) {
			int jth = item.get(j).getIndex();
			if(!visited[jth]) {
				search(jth, heap, visited);
			}
		}
	}
}
