package wyautl.io;

import java.io.IOException;

import wyautl.lang.Automata;
import wyjvm.io.BinaryOutputStream;

/**
 * <p>
 * The binary automata write is responsible for writing an automata in a binary
 * format to an output stream. Obviously, it cannot know how to handle the
 * supplementary data that can be provided as part of a state. Therefore, if the
 * automata contains nodes which have supplementary data, the client is expected
 * to deal with this.
 * </p>
 * <p>
 * <b>NOTE:</b> By default, this class complete ignores any supplementary data.
 * To allow writing this data, the client should extend this class and overwrite
 * the method <code>write(Automata.State)</code>. In such case, it is
 * recommended that <code>super.write(state)</code> is called before writing the
 * supplementary data. In other words, the standard information (i.e. kind and
 * children) for a state comes first, and the supplementary data is placed after
 * that.
 * </p>
 * 
 * @author djp
 * 
 */
public class BinaryAutomataWriter implements GenericWriter<Automata> {		
	private final BinaryOutputStream writer;	
	
	public BinaryAutomataWriter(BinaryOutputStream writer) {
		this.writer = writer;			
	}
	
	public void write(Automata automata) throws IOException {
		int size = automata.size();		
		writer.write_uv(size);
		for(int i=0;i!=size;++i) {
			write(automata.states[i]);
		}		
	}
	
	protected void write(Automata.State state) throws IOException {
		writer.write_uv(state.kind);
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
