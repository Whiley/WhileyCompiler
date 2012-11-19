package wyautl.io;

import java.io.IOException;
import java.math.BigInteger;

import wyautl.core.*;
import wyautl.util.BigRational;

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
			automaton.setMarker(i,readReference());
		}
		return automaton;
	}

	protected Automaton.State readState() throws IOException {
		int kind = reader.read_uv() + Automaton.K_FREE;
		switch(kind) {
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
