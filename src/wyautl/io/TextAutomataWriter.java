package wyautl.io;

import java.io.*;

import wyautl.lang.*;

public final class TextAutomataWriter implements GenericWriter<Automata> {
	private final PrintStream writer;	
	
	public TextAutomataWriter(PrintStream stream) {
		this.writer = stream;
	}
	
	public TextAutomataWriter(OutputStream stream) {
		this.writer = new PrintStream(stream);
	}
	
	public void write(Automata automata) throws IOException {	
		writer.println(automata);
	}
	
	public void close() throws IOException {
		writer.close();
	}
	
	public void flush() throws IOException {
		writer.flush();
	}
}
