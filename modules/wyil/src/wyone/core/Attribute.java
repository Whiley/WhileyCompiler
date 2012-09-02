package wyone.core;


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
	
	public static final class Type implements Attribute {
		public final wyone.core.Type type;

		public Type(wyone.core.Type type) {
			this.type = type;
		}
	}	
}
