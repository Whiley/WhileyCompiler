package wyautl.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import wyautl.core.Automaton;
import wyautl.core.Schema;
import wyautl.util.BigRational;

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
		int nMarkers = automaton.nMarkers();		
		output.write_uv(nMarkers);
		for (int i = 0; i != nMarkers; ++i) {
			writeReference(automaton.getMarker(i),automaton);			
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
