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
import java.io.UnsupportedEncodingException;

import wyautl.core.Automaton;
import wyautl.core.Schema;
import wyautl.util.BigRational;
import wyfs.io.BinaryOutputStream;

/**
 * Enables an automaton to be written to a binary output stream.
 *
 * @author David J. Pearce
 *
 */
public class BinaryAutomataWriter {
	protected final BinaryOutputStream output;
	protected final Schema schema;

	public BinaryAutomataWriter(BinaryOutputStream output, Schema schema) {
		this.output = output;
		this.schema = schema;
	}

	public void write(Automaton automaton) throws IOException {
		int nStates = automaton.nStates();
		output.write_uv(nStates);
		for (int i = 0; i != nStates; ++i) {
			write(automaton.get(i), automaton);
		}
		int nMarkers = automaton.nRoots();
		output.write_uv(nMarkers);
		for (int i = 0; i != nMarkers; ++i) {
			writeReference(automaton.getRoot(i),automaton);
		}
	}

	protected void write(Automaton.State state, Automaton automaton)
			throws IOException {
		output.write_uv(state.kind + -Automaton.K_FREE);
		if (state instanceof Automaton.Constant) {
			write((Automaton.Constant) state);
		} else if (state instanceof Automaton.Term) {
			write((Automaton.Term) state, automaton);
		} else {
			write((Automaton.Collection) state, automaton);
		}
	}

	protected void write(Automaton.Constant state) throws IOException {
		if (state instanceof Automaton.Bool) {
			Automaton.Bool i = (Automaton.Bool) state;
			output.write_un(i.value == true ? 1 : 0,1);
		} else if (state instanceof Automaton.Int) {
			Automaton.Int i = (Automaton.Int) state;
			byte[] bytes = i.value.toByteArray();
			output.write_uv(bytes.length);
		} else if (state instanceof Automaton.Real) {
			Automaton.Real r = (Automaton.Real) state;
			BigRational br = r.value;

			byte[] numbytes = br.numerator().toByteArray();
			output.write_uv(numbytes.length);
			output.write(numbytes);

			byte[] denbytes = br.denominator().toByteArray();
			output.write_uv(denbytes.length);

		} else if (state instanceof Automaton.Strung) {
			Automaton.Strung str = (Automaton.Strung) state;
			try {
				byte[] bytes = str.value.getBytes("UTF-8");
				output.write_uv(bytes.length);
				output.write(bytes);
			} catch (UnsupportedEncodingException e) {
				// hmmm, this aint pretty ;)
			}
		} else {
			throw new IllegalArgumentException("Unknown state encountered (" + state + ")");
		}
	}

	protected void write(Automaton.Term state, Automaton automaton)
			throws IOException {
		writeReference(state.contents, automaton);
	}

	protected void write(Automaton.Collection state, Automaton automaton)
			throws IOException {
		int size = state.size();
		output.write_uv(size);
		for (int i = 0; i != size; ++i) {
			writeReference(state.get(i), automaton);
		}
	}

	protected void writeReference(int ref, Automaton automaton)
			throws IOException {
		int raw = ref + -Automaton.K_FREE + schema.size();
		output.write_uv(raw);
	}

	public void close() throws IOException {
		output.close();
	}

	public void flush() throws IOException {
		output.flush();
	}
}
