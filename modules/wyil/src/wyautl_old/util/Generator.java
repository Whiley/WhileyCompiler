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

package wyautl_old.util;

import java.io.*;
import java.util.*;

import wyfs.io.BinaryOutputStream;
import wyil.lang.*;
import wyautl_old.io.*;
import wyautl_old.lang.*;

/**
 * The generator class is used generate automata, primarily for testing
 * purposes.
 *
 * @author David J. Pearce
 *
 */
public class Generator {

	public interface Data {
		/**
		 * Generate all possible forms of supplementary data for the given
		 * state.
		 *
		 * @param state
		 * @return
		 */
		public List<Object> generate(Automaton.State state);
	}

	public static final class Kind {
		/**
		 * Determine whether this kind is deterministic or not.
		 */
		public boolean DETERMINISTIC;

		/**
		 * Determine minimum number of children this kind can have.
		 */
		public int MIN_CHILDREN;

		/**
		 * Determine maximum number of children this kind can have.
		 */
		public int MAX_CHILDREN;

		/**
		 * A method for generating approprate supplementary data.
		 */
		public Data DATA;

		public Kind(boolean deterministic, int min, int max, Data data) {
			this.DETERMINISTIC = deterministic;
			this.MIN_CHILDREN = min;
			this.MAX_CHILDREN = max;
			this.DATA = data;
		}
	}

	public static class Config {
		/**
		 * Provide details of kinds used.
		 */
		public Kind[] KINDS;

		/**
		 * Allow recursive links or not.
		 */
		public boolean RECURSIVE;

		/**
		 * Determine size of automata to generate.
		 */
		public int SIZE;
	}

	private final static class Template {
		public final int[] kinds;
		public final int[] children;
		public final BitSet transitions;

		public Template(int size) {
			this.kinds = new int[size];
			this.children = new int[size];
			transitions = new BitSet(size*size);
		}

		public final void add(int from, int to) {
			transitions.set((from*kinds.length)+to,true);
		}

		public final void remove(int from, int to) {
			transitions.set((from*kinds.length)+to,false);
		}

		public final boolean isTransition(int from, int to) {
			return transitions.get((from*kinds.length)+to);
		}
	}

	/**
	 * Turn a template into an actual automaton.
	 *
	 * @param template
	 * @param writer
	 */
	private static void generate(Template template,
			GenericWriter<Automaton> writer, Config config) throws IOException {

		Kind[] KINDS = config.KINDS;
		int[] kinds = template.kinds;
		int[] nchildren = template.children;
		Automaton.State[] states = new Automaton.State[kinds.length];

		for(int i=0;i!=kinds.length;++i) {
			int kind = kinds[i];
			int[] children = new int[nchildren[i]];
			int index = 0;
			for(int j=0;j!=kinds.length;++j) {
				if(template.isTransition(i,j)) {
					children[index++] = j;
				}
			}
			states[i] = new Automaton.State(kind,KINDS[kind].DETERMINISTIC,children);
		}
		Automaton automaton = new Automaton(states);
		generate(0,automaton,writer,config);
	}

	private static void generate(int index, Automaton automaton,
			GenericWriter<Automaton> writer, Config config) throws IOException {
		if(index >= automaton.size()) {
			writer.write(automaton);
			writer.flush();
			count++;
			if(verbose) {
				System.err.print("\rWrote " + count + " automata.");
			}
		} else {
			Automaton.State state = automaton.states[index];
			int[] state_children = state.children;
			for(int[] nchildren : Automata.permutations(state_children)) {
				state.children = nchildren;
				generateData(index,automaton,writer,config);
			}
		}
	}


	private static void generateData(int index, Automaton automaton,
			GenericWriter<Automaton> writer, Config config) throws IOException {
		Automaton.State state = automaton.states[index];
		Kind kind = config.KINDS[state.kind];
		if (kind.DATA != null) {
			// this kind requires supplementary data
			List<Object> datas = kind.DATA.generate(state);
			for (Object data : datas) {
				state.data = data;
				generate(index + 1, automaton, writer, config);
			}
		} else {
			generate(index + 1, automaton, writer, config);
		}
	}

	private static boolean verbose = false;
	private static int count = 0;

	private static void debug(Template base) {
		int[] kinds = base.kinds;
		for(int i=0;i!=kinds.length;++i) {
			int kind = kinds[i];
			System.out.print("(" + kind +")");
		}
		for(int i=0;i!=kinds.length;++i) {
			for(int j=0;j!=kinds.length;++j) {
				if(base.isTransition(i,j)) {
					System.out.print(i + "->" +j + " ");
				}
			}
		}
		System.out.println();
	}

	private static void generate(int from, int to, Template base,
			GenericWriter<Automaton> writer, Config config) throws IOException {
		int[] nchildren = base.children;
		int[] kinds = base.kinds;
		Kind fromKind = config.KINDS[kinds[from]];

		if(to >= config.SIZE) {
			if(nchildren[from] < fromKind.MIN_CHILDREN){
				// this indicates an invalid automaton, since this state doesn't
				// have enough children.
				return;
			}

			if(from > 0) {
				// non-root state, so ensure has parent
				boolean hasParent = false;
				for(int i=0;i!=from;++i) {
					if(base.isTransition(i,from)) {
						hasParent = true;
						break;
					}
				}

				if(!hasParent) { return; }
			}

			from = from + 1;
			to = from;

			if(to >= config.SIZE) {
				// ok, generate the automaton.
				generate(base,writer,config);
				return;
			}
		}

		// first, generate no edge (if allowed)
		generate(from,to+1,base,writer,config);
		Kind toKind = config.KINDS[kinds[to]];

		// second, generate forward edge (if allowed)
		if (from != to && nchildren[from] < fromKind.MAX_CHILDREN) {
			nchildren[from]++;
			base.add(from,to);
			generate(from,to+1,base,writer,config);

			// third, generate bidirectional edge (if allowed)
			if (config.RECURSIVE && nchildren[to] < toKind.MAX_CHILDREN) {
				nchildren[to]++;
				base.add(to, from);
				generate(from, to+1, base, writer, config);
				base.remove(to, from);
				nchildren[to]--;
			}
			base.remove(from,to);
			nchildren[from]--;
		}

		// fourth, generate reverse edge (if allowed)
		if (config.RECURSIVE && nchildren[to] < toKind.MAX_CHILDREN) {
			nchildren[to]++;
			base.add(to, from);
			generate(from, to+1, base, writer, config);
			base.remove(to, from);
			nchildren[to]--;
		}
	}

	private static void generate(int index, Template base,
			GenericWriter<Automaton> writer, Config config) throws IOException {
		if(index == config.SIZE) {
			// now start generating transitions
			generate(0,0,base,writer,config);
		} else {
			Kind[] kinds = config.KINDS;
			for(int k=0;k!=kinds.length;++k) {
				Kind kind = kinds[k];
				if(kind != null) {
					base.kinds[index] = k;
					generate(index+1,base,writer,config);
				}
			}
		}
	}

	/**
	 * The generate method generates all possible automata matching of a given
	 * size. Observe that this may be an extremely expensive operation, and
	 * significant care must be exercised in setting the configuration
	 * parameters!
	 *
	 * @param size
	 *            --- generated automata will have exactly this size.
	 * @param recursive
	 *            --- generated automata permit recursive links.
	 * @param writer
	 *            --- generate automata are written to this writer.
	 */
	public static void generate(GenericWriter<Automaton> writer, Config config) throws IOException {
		Template base = new Template(config.SIZE);
		generate(0,base,writer,config);
	}

	private static final Config config = new Config() {{
		KINDS = new Kind[]{
			new Kind(false,0,2,null),
			new Kind(true,1,1,null)
		};
		RECURSIVE = true;
		SIZE = 3;
	}};

	public static void main(String[] args) {
		boolean binary = false;
		GenericWriter<Automaton> writer;
		OutputStream out = System.out;
		int minSize = 1;
		int maxSize = config.SIZE;

		try {
			int index = 0;
			while(index < args.length) {
				if(args[index].equals("-b")) {
					binary=true;
				} else if(args[index].equals("-o")) {
					String filename = args[++index];
					out = new FileOutputStream(filename);
				} else if(args[index].equals("-s") || args[index].equals("-size")) {
					String arg = args[++index];
					if(arg.indexOf(':') >= 0) {
						String[] ss = arg.split(":");
						minSize = Integer.parseInt(ss[0]);
						maxSize = Integer.parseInt(ss[1]);
					} else {
						maxSize = Integer.parseInt(arg);
					}
				} else if(args[index].equals("-v") || args[index].equals("-verbose")) {
					verbose = true;
				} else if(args[index].equals("-m") || args[index].equals("-model")) {
					config.RECURSIVE = false;
					for(Kind k : config.KINDS) {
						if(!k.DETERMINISTIC) {
							k.DETERMINISTIC = true;
							k.MIN_CHILDREN = 0;
							k.MAX_CHILDREN = 1;
						}
					}
				}
				index++;
			}

			if(binary) {
				BinaryOutputStream bos = new BinaryOutputStream(out);
				writer = new BinaryAutomataWriter(bos);
			} else {
				writer = new TextAutomataWriter(out);
			}
			for(int i=minSize;i<=maxSize;++i) {
				config.SIZE = i;
				generate(writer,config);
			}
			if(!verbose) {
				System.err.print("\rWrote " + count + " automata.");
			}
			writer.close();
		} catch(IOException ex) {
			System.out.println("Exception: " + ex);
		}
	}
}
