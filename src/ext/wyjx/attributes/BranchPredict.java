package wyjx.attributes;

public class BranchPredict implements wyil.lang.Attribute {
	public final boolean trueBranch;
	public final boolean falseBranch;
	
	public BranchPredict(boolean trueBranch, boolean falseBranch) {
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}
	
	public String toString() {
		String r = trueBranch ? "T" : "";
		r += falseBranch ? "F" : "";
		return "e" + r;
	}		
}
