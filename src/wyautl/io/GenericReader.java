package wyautl.io;

import java.io.IOException;

/**
 * An interface for reading specific values from a binary stream. This is used
 * by the Generator class so allow writing of Automata and/or values to the
 * stream.
 * 
 * @author djp
 * 
 * @param <T>
 */
public interface GenericReader<T> {
	public T read() throws IOException;
	public void close() throws IOException;
}
