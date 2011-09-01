package wyautl.io;

import java.io.*;

import wyautl.lang.*;

public final class TextAutomataWriter implements GenericWriter<Automata> {
	private final PrintStream writer;
	public int count = 0;
	
	public TextAutomataWriter(PrintStream stream) {
		this.writer = stream;
	}
	
	public TextAutomataWriter(OutputStream stream) {
		this.writer = new PrintStream(stream);
	}
	
	public void write(Automata automata) throws IOException {	
		count++;
		Automata.State[] states = automata.states;
		for(int i=0;i!=states.length;++i) {
			if(i != 0) {
				writer.print(", ");
			}
			Automata.State state = states[i];
			int kind = state.kind;
			writer.print("#");
			writer.print(i);
			writer.print("(");		
			boolean sequential = (kind & Automata.NONSEQUENTIAL) == 0;			
			writer.print(kind);
			
			if(state.data != null) {
				writer.print("," + state.data);
			}
			writer.print(")");
			if(sequential) {
				writer.print("[");
			} else {
				writer.print("{");
			}
			boolean firstTime=true;
			for(int c : state.children) {
				if(!firstTime) {
					writer.print(",");
				}
				firstTime=false;
				writer.print(c);
			}
			if(sequential) {
				writer.print("]");
			} else {
				writer.print("}");
			}
		}
		writer.println();
	}
	
	public void close() throws IOException {
		writer.close();
	}
	
	public void flush() throws IOException {
		writer.flush();
	}
}
