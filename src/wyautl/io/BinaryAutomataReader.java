package wyautl.io;

import java.io.IOException;

import wyautl.lang.Automata;
import wyjvm.io.BinaryInputStream;

/**
 * <p>
 * The binary automata reader is responsible for reader an automata in a binary
 * format from an input stream. Obviously, it cannot know how to handle the
 * supplementary data that can be provided as part of a state. Therefore, if the
 * automata may contain nodes which have supplementary data, the client is
 * expected to deal with this.
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
 * @author djp
 * 
 */
public class BinaryAutomataReader implements GenericReader<Automata> {		
	private final BinaryInputStream reader;	
	
	public BinaryAutomataReader(BinaryInputStream reader) {
		this.reader = reader;			
	}
	
	public void close() throws IOException {
		reader.close();
	}
	
	public Automata read() throws IOException {		
		int size = reader.read_uv();
		Automata.State[] states = new Automata.State[size];
		for(int i=0;i!=size;++i) {
			states[i] = readState();			
		}		
		return new Automata(states);
	}

	protected Automata.State readState() throws IOException {
		int kind = reader.read_uv();
		int nchildren = reader.read_uv();
		int[] children = new int[nchildren];		
		for (int i=0;i!=nchildren;++i) {
			children[i]=reader.read_uv();
		}
		return new Automata.State(kind,children);
	}
}	