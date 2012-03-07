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

import java.io.IOException;

import wyautl.lang.Automaton;
import wyjvm.io.BinaryOutputStream;

/**
 * <p>
 * Responsible for writing an automaton in a binary format to an output stream.
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
public class BinaryAutomataWriter implements GenericWriter<Automaton> {		
	protected final BinaryOutputStream writer;	
	
	public BinaryAutomataWriter(BinaryOutputStream writer) {
		this.writer = writer;			
	}
	
	public void write(Automaton automaton) throws IOException {
		int size = automaton.size();		
		writer.write_uv(size);
		for(int i=0;i!=size;++i) {
			write(automaton.states[i]);
		}		
	}
	
	protected void write(Automaton.State state) throws IOException {
		writer.write_uv(state.kind);
		writer.write_bit(state.deterministic);
		int[] children = state.children;		
		writer.write_uv(children.length);
		for(int i : children) {
			writer.write_uv(i);
		}
	}	
	
	public void close() throws IOException {
		writer.close();
	}
	
	public void flush() throws IOException {
		writer.flush();
	}
}	
