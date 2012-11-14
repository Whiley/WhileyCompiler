package wyone.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import wyone.core.Automaton;
import wyone.util.BigRational;

/**
 * Enables an automaton to be written to a binary output stream.
 * 
 * @author David J. Pearce
 * 
 */
public class BinaryAutomataWriter {
	protected final BinaryOutputStream output;

	public BinaryAutomataWriter(BinaryOutputStream output) {
		this.output = output;
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
			output.write_uv(automaton.marker(i));
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
			write((Automaton.Compound) state, automaton);
		}
	}

	protected void write(Automaton.Constant state) throws IOException {
		if (state instanceof Automaton.Int) {
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
			throw new IllegalArgumentException("Unknown state encountered");
		}
	}

	protected void write(Automaton.Term state, Automaton automaton)
			throws IOException {
		writeReference(state.contents, automaton);
	}

	protected void write(Automaton.Compound state, Automaton automaton)
			throws IOException {
		int size = state.size();
		output.write_uv(size);
		for (int i = 0; i != size; ++i) {
			writeReference(state.get(i), automaton);
		}
	}

	protected void writeReference(int ref, Automaton automaton)
			throws IOException {
		int raw = ref + -Automaton.K_FREE + automaton.schema().length;
		output.write_uv(raw);
	}

	public void close() throws IOException {
		output.close();
	}

	public void flush() throws IOException {
		output.flush();
	}
}
