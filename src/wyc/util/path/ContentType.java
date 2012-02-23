package wyc.util.path;

import java.io.*;

public interface ContentType<T> {
	public Path.Entry<T> accept(Path.Entry<?> e);

	public boolean matches(String suffix);
	
	public T read(InputStream input);
	
	public void write(OutputStream output, T value);
}
