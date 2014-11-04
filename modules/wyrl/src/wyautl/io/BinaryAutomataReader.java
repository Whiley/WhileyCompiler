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
import java.math.BigInteger;

import wyautl.core.*;
import wyautl.util.BigRational;
import wyfs.io.BinaryInputStream;

/**
 * <p>
 * Responsible for reading an automaton in a binary format from an input stream.
 * </p>
 * <p>
 *
 * @author David J. Pearce
 *
 */
public class BinaryAutomataReader {
	protected final BinaryInputStream reader;
	protected final Schema schema;

	public BinaryAutomataReader(BinaryInputStream reader, Schema schema) {
		this.reader = reader;
		this.schema = schema;
	}

	public void close() throws IOException {
		reader.close();
	}

	public Automaton read() throws IOException {
		int nStates = reader.read_uv();
		Automaton.State[] states = new Automaton.State[nStates];
		for(int i=0;i!=nStates;++i) {
			states[i] = readState();
		}
		Automaton automaton = new Automaton(states);
		int nMarkers = reader.read_uv();
		for(int i=0;i!=nMarkers;++i) {
			automaton.setRoot(i,readReference());
		}
		return automaton;
	}

	protected Automaton.State readState() throws IOException {
		int kind = reader.read_uv() + Automaton.K_FREE;
		switch(kind) {
			case Automaton.K_BOOL:
				return readBool();
			case Automaton.K_INT:
				return readInt();
			case Automaton.K_REAL:
				return readReal();
			case Automaton.K_STRING:
				return readString();
			case Automaton.K_LIST:
			case Automaton.K_BAG:
			case Automaton.K_SET:
				return readCompound(kind);
			default:
				return readTerm(kind);
		}
	}

	protected Automaton.Bool readBool() throws IOException {
		int bit = reader.read_un(1);
		return new Automaton.Bool(bit == 1);
	}

	protected Automaton.Int readInt() throws IOException {
		int size = reader.read_uv();
		byte[] bytes = new byte[size];
		reader.read(bytes);
		return new Automaton.Int(new BigInteger(bytes));
	}

	protected Automaton.Real readReal() throws IOException {
		int size = reader.read_uv();
		byte[] numerator = new byte[size];
		reader.read(numerator);
		size = reader.read_uv();
		byte[] denominator = new byte[size];
		reader.read(denominator);
		return new Automaton.Real(new BigRational(new BigInteger(numerator),
				new BigInteger(denominator)));
	}

	protected Automaton.Strung readString() throws IOException {
		int length = reader.read_uv();
		byte[] data = new byte[length];
		reader.read(data);
		String str = new String(data,0,length,"UTF-8");
		return new Automaton.Strung(str);
	}

	protected Automaton.State readCompound(int kind) throws IOException {
		int size = reader.read_uv();
		int[] children = new int[size];
		for(int i=0;i!=size;++i) {
			children[i] = readReference();
		}
		switch(kind) {
			case Automaton.K_SET:
				return new Automaton.Set(children);
			case Automaton.K_BAG:
				return new Automaton.Bag(children);
			case Automaton.K_LIST:
				return new Automaton.List(children);
			default:
				throw new IllegalArgumentException("invalid compound kind");
		}
	}

	protected Automaton.State readTerm(int kind) throws IOException {
		int contents = readReference();
		return new Automaton.Term(kind,contents);
	}

	protected int readReference() throws IOException {
		int raw = reader.read_uv();
		return (raw - schema.size()) + Automaton.K_FREE;
	}
}
