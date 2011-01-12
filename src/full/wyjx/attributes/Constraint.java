package wyjx.attributes;

import wyil.lang.*;
import wyjx.jvm.attributes.*;

public class Constraint extends WhileyBlock implements Attribute {
	public final Block constraint;
	
	public Constraint(Block constraint) {
		super("Constraint",constraint);
		this.constraint = constraint;
	}
	
	public static class Reader extends WhileyBlock.Reader {
		public Reader() {
			super("Constraint");
		}
	}
}
