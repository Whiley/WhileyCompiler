package wyjx.attributes;

import wyil.lang.*;

public class Postcondition implements Attribute {
	public final Block constraint;
	
	public Postcondition(Block constraint) {
		this.constraint = constraint;
	}
}
