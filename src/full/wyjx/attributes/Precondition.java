package wyjx.attributes;

import wyil.lang.*;

public class Precondition extends WhileyBlock implements Attribute {
	public final Block constraint;
	
	public Precondition(Block constraint) {
		super("Precondition",constraint);
		this.constraint = constraint;
	}
}
