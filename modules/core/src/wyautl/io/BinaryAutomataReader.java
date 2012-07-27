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
import wyjvm.io.BinaryInputStream;

/**
 * <p>
 * Responsible for reading an automaton in a binary format from an input stream.
 * Obviously, it cannot know how to handle the supplementary data that can be
 * provided as part of a state. Therefore, if the automaton may contain states
 * which have supplementary data, the client is expected to deal with this.
 * </p>
 * <p>
 * <b>NOTE:</b> To handle supplementary data, the client should extend this
 * class and overwrite the method <code>readState()</code>. In such case, it is
 * recommended that <code>super.readState()</code> is called before reading the
 * supplementary data. In other words, the standard information (i.e. kind and
 * children) for a state comes first, and the supplementary data is placed after
 * that.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class BinaryAutomataReader implements GenericReader<Automaton> {		
	protected final BinaryInputStream reader;	
	
	public BinaryAutomataReader(BinaryInputStream reader) {
		this.reader = reader;			
	}
	
	public void close() throws IOException {
		reader.close();
	}
	
	public Automaton read() throws IOException {		
		int size = reader.read_uv();		
		Automaton.State[] states = new Automaton.State[size];
		for(int i=0;i!=size;++i) {
			states[i] = readState();			
		}		
		return new Automaton(states);
	}

	protected Automaton.State readState() throws IOException {
		int kind = reader.read_uv();
		boolean deterministic = reader.read_bit();
		int nchildren = reader.read_uv();
		int[] children = new int[nchildren];		
		for (int i=0;i!=nchildren;++i) {
			children[i]=reader.read_uv();
		}
		return new Automaton.State(kind,deterministic,children);
	}
}	