// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyautl_old.io;

import java.io.IOException;

import wyautl_old.lang.Automaton;
import wyfs.io.BinaryInputStream;

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

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
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