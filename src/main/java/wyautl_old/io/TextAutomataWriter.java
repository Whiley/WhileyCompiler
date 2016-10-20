// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyautl_old.io;

import java.io.*;

import wyautl_old.lang.*;

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
public class TextAutomataWriter implements GenericWriter<Automaton> {
	private final PrintStream writer;

	public TextAutomataWriter(PrintStream stream) {
		this.writer = stream;
	}

	public TextAutomataWriter(OutputStream stream) {
		this.writer = new PrintStream(stream);
	}

	@Override
	public void write(Automaton automaton) throws IOException {
		writer.println(automaton);
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}
}
