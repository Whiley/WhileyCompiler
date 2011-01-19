package wyjx.attributes;

import wyil.lang.Attribute;

public class BranchInfo implements Attribute {
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
