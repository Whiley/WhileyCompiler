package wyc.util.path;

public interface ContentType<T extends Path.Entry> {
	public T accept(Path.Entry e);			
}
