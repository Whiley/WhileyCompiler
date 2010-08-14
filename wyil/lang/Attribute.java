package wyil.lang;

public interface Attribute {
	
	public static class Source implements Attribute {
		public final int start;	
		public final int end;	

		public Source(int start, int end) {			
			this.start = start;
			this.end = end;		
		}
		
		public String toString() {
			return "@" + start + ":" + end;
		}
	}
}
