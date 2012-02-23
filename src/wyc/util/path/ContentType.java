package wyc.util.path;

public interface ContentType<T extends Path.Entry> {
	public T accept(Path.Entry e);		
	
	public static ContentType<Path.SourceEntry> WHILEY_FILE = new ContentType<Path.SourceEntry>() {
		public Path.SourceEntry accept(Path.Entry e) {
			if(e.suffix() == "whiley") {
				return (Path.SourceEntry) e;
			} else {
				return null;
			}
		}
	};	
}
