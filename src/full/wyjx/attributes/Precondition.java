package wyjx.attributes;

import wyil.lang.*;

public class Precondition implements Attribute {
	public final Block constraint;
	
	public Precondition(Block constraint) {
		this.constraint = constraint;
	}
}
