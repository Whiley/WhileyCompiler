package wyjx.attributes;

import wyil.lang.*;
import wyjx.jvm.attributes.*;

public class Constraint extends WhileyBlock implements Attribute {
	public final Block constraint;
	
	public Constraint(Block constraint) {
		super("constraint",constraint);
		this.constraint = constraint;
	}
}
