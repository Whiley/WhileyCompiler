package wyautl.io;

import java.io.*;

import wyautl.lang.*;

/**
 * <p>
 * The text automata writer is responsible for writing an automata in a textual
 * format to an output stream. Obviously, it cannot know how to handle the
 * supplementary data that can be provided as part of a state. Therefore, if the
 * automata contains states which have supplementary data, the client is expected
 * to deal with this.
 * </p>
 * <p>
 * <b>NOTE:</b> By default, this class completely ignores any supplementary data.
 * To allow writing this data, the client should extend this class and overwrite
 * the method <code>write(Automata.State)</code>. In such case, it is
 * recommended that <code>super.write(state)</code> is called before writing the
 * supplementary data. In other words, the standard information (i.e. kind and
 * children) for a state comes first, and the supplementary data is placed after
 * that.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class TextAutomataWriter implements GenericWriter<Automaton> {
	private final PrintStream writer;	
	
	public TextAutomataWriter(PrintStream stream) {
		this.writer = stream;
	}
	
	public TextAutomataWriter(OutputStream stream) {
		this.writer = new PrintStream(stream);
	}
	
	public void write(Automaton automata) throws IOException {	
		writer.println(automata);
	}
	
	public void close() throws IOException {
		writer.close();
	}
	
	public void flush() throws IOException {
		writer.flush();
	}
}
