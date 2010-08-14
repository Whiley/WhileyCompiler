package wyil.lang;

public interface Attribute {
	public static class Source implements Attribute {
		public final String filename;
		public final int start;	
		public final int end;	

		public Source(String filename, int start, int end) {
			this.filename = filename;
			this.start = start;
			this.end = end;		
		}
		
		public String toString() {
			return filename + ":" + start + ":" + end;
		}
	}
}
