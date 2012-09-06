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

package wyone.io;

import java.io.*;
import wyone.core.*;

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
	private final PrintStream writer;
	private final Type.Term[] schema;
	
	public PrettyAutomataWriter(PrintStream stream, Type.Term[] schema) {
		this.writer = stream;
		this.schema = schema;
	}
	
	public PrettyAutomataWriter(OutputStream stream, Type.Term[] schema) {
		this.writer = new PrintStream(stream);
		this.schema = schema;
	}
	
	public void write(Automaton automaton) throws IOException {	
		for(int i=0;i!=automaton.nRoots();++i) {
			write(automaton.root(i),automaton);
		}
	}
	
	protected void write(int index, Automaton automaton) throws IOException {
		Automaton.State state = automaton.get(index);
		if(state instanceof Automaton.Constant) {
			write((Automaton.Constant)state,automaton);
		} else if(state instanceof Automaton.Term) {
			write((Automaton.Term)state,automaton);
		} else {
			write((Automaton.Compound)state,automaton);
		}	
	}
	
	protected void write(Automaton.Constant item, Automaton automaton) throws IOException {
		Object payload = item.value;
		if (payload instanceof String) {
			writer.print("\"" + payload.toString() + "\"");
		} else {
			// default
			writer.print(payload.toString());
		}
	}

	protected void write(Automaton.Term term, Automaton automaton) throws IOException {
		writer.print(schema[term.kind].name);
		Type.Ref type = (Type.Ref) schema[term.kind].data;		
		if(type != null && type.element instanceof Type.Compound) {			
			write(term.contents,automaton);
		} else if(type != null) {
			writer.print("(");
			write(term.contents,automaton);
			writer.print(")");
		}
	}
	
	protected void write(Automaton.Compound state, Automaton automaton) throws IOException {
		int[] children = state.children;
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
		for(int i=0;i!=children.length;++i) {
			if(i != 0) {
				writer.print(",");
			}
			write(children[i],automaton);
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
}
