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
	
	public static class BranchInfo implements Attribute {
		public final boolean trueBranch;
		public final boolean falseBranch;
		
		public BranchInfo(boolean tb, boolean fb) {
			this.trueBranch = tb;
			this.falseBranch = fb;
		}
		
		public String toString() {
			String r = trueBranch ? "T" : "?";
			r += falseBranch ? "F" : "?";
			return r;
		}
	}
}
