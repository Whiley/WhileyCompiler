package wyautl.io;

import java.io.IOException;

/**
 * A generic writer is simply an interface for writing out values to a stream.
 * This is used by the Generator class so allow writing of Automata and/or
 * values to the stream.
 * 
 * @author djp
 * 
 * @param <T>
 */
public interface GenericWriter<T> {
	public void write(T value) throws IOException;
}
