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

package wyautl.io;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

import wyautl.core.*;

/**
 * <p>
 * Responsible for writing an automaton in a textual format to an output stream.
 * Obviously, it cannot know how to handle the supplementary data that can be
 * provided as part of a state. Therefore, if the automaton contains states
 * which have supplementary data, the client is expected to deal with this.
 * </p>
 * <p>
 * <b>NOTE:</b> By default, this class completely ignores any supplementary
 * data. To allow writing this data, the client should extend this class and
 * overwrite the method <code>write(Automata.State)</code>. In such case, it is
 * recommended that <code>super.write(state)</code> is called before writing the
 * supplementary data. In other words, the standard information (i.e. kind and
 * children) for a state comes first, and the supplementary data is placed after
 * that.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class PrettyAutomataWriter  {
	private final PrintWriter writer;
	private final Schema schema;
	private final HashSet<String> indents;
	private int level;

	public PrettyAutomataWriter(PrintStream stream, Schema schema, String... indents) {
		this(new PrintWriter(stream),schema,indents);
	}

	public PrettyAutomataWriter(OutputStream stream, Schema schema, String... indents) {
		this(new OutputStreamWriter(stream),schema,indents);
	}

	public PrettyAutomataWriter(PrintWriter stream, Schema schema, String... indents) {
		this.writer = stream;
		this.schema = schema;
		this.indents = new HashSet<String>();
		for(String indent : indents) {
			this.indents.add(indent);
		}
	}

	public PrettyAutomataWriter(Writer stream, Schema schema, String... indents) {
		this.writer = new PrintWriter(stream);
		this.schema = schema;
		this.indents = new HashSet<String>();
		for(String indent : indents) {
			this.indents.add(indent);
		}
	}

	public void write(Automaton automaton) throws IOException {
		int[] headers = new int[automaton.nStates()];
		for (int i = 0; i != automaton.nRoots(); ++i) {
			Arrays.fill(headers, 0);
			int root = automaton.getRoot(i);
			Automata.traverse(automaton, root, headers);
			write(root, headers, automaton, false);
		}
		writer.flush();
	}

	public void write(int root, Automaton automaton) throws IOException {
		int[] headers = new int[automaton.nStates()];
		Arrays.fill(headers, 0);
		Automata.traverse(automaton, root, headers);
		write(root, headers, automaton, false);
		writer.flush();
	}

	protected void write(int index, int[] headers, Automaton automaton,
			boolean indent) throws IOException {
		int header = 0;

		if(index >= 0) {
			header = headers[index];
			if(header == 3) {
				writer.print("$" + index + "<");
			} else if(header < 0) {
				writer.print("$" + index);
				return;
			}
		}

		Automaton.State state = automaton.get(index);
		if (state instanceof Automaton.Constant) {
			write((Automaton.Constant) state, headers, automaton, indent);
		} else if (state instanceof Automaton.Term) {
			write((Automaton.Term) state, headers, automaton, indent);
		} else {
			write((Automaton.Collection) state, headers, automaton, indent);
		}

		if(header == 3) {
			writer.print(">");
		}
	}

	protected void write(Automaton.Constant item, int[] headers, Automaton automaton, boolean indent) throws IOException {
		Object payload = item.value;
		if (payload instanceof String) {
			writer.print("\"" + payload.toString() + "\"");
		} else {
			// default
			writer.print(payload.toString());
		}
	}

	protected void write(Automaton.Term term, int[] headers, Automaton automaton,
			boolean indent) throws IOException {
		String name = schema.get(term.kind).name;
		indent = indents.contains(name);
		writer.print(name);
		Schema.State element = schema.get(term.kind).child;
		if (element != null && element instanceof Schema.Collection) {
			write(term.contents, headers, automaton, indent);
		} else if (element != null) {
			writer.print("(");
			write(term.contents, headers, automaton, indent);
			writer.print(")");
		}
	}

	protected void write(Automaton.Collection state, int[] headers, Automaton automaton, boolean indent) throws IOException {
		switch(state.kind) {
			case Automaton.K_LIST:
				writer.print("[");
				break;
			case Automaton.K_BAG:
				writer.print("{|");
				break;
			case Automaton.K_SET:
				writer.print("{");
				break;
		}
		if(indent) {
			level++;
		}
		for(int i=0;i!=state.size();++i) {
			if(i != 0) {
				writer.print(",");
			}
			if(indent) {
				writer.println();
				indent();
			}
			write(state.get(i),headers,automaton,false);
		}
		if(indent) {
			level--;
			writer.println();
			indent();
		}
		switch(state.kind) {
			case Automaton.K_LIST:
				writer.print("]");
				break;
			case Automaton.K_BAG:
				writer.print("|}");
				break;
			case Automaton.K_SET:
				writer.print("}");
				break;
		}
	}

	public void close() throws IOException {
		writer.close();
	}

	public void flush() throws IOException {
		writer.flush();
	}

	private void indent() {
		for(int i=0;i<level;++i) {
			writer.print("\t");
		}
	}
}
