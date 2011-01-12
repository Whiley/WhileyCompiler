package wyjx.attributes;

import wyil.lang.*;
import wyjx.jvm.attributes.WhileyBlock;

public class Postcondition extends WhileyBlock implements Attribute {
	public final Block constraint;
	
	public Postcondition(Block constraint) {
		super("Postcondition",constraint);
		this.constraint = constraint;
	}
}
