package wyjx.attributes;

import wyil.lang.*;

public class Constraint implements Attribute {
	public final Block constraint;
	
	public Constraint(Block constraint) {
		this.constraint = constraint;
	}
}
