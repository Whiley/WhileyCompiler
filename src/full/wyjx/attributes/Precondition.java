package wyjx.attributes;

import wyil.lang.*;
import wyjx.jvm.attributes.WhileyBlock;

public class Precondition extends WhileyBlock implements Attribute {
	public final Block constraint;
	
	public Precondition(Block constraint) {
		super("Precondition",constraint);
		this.constraint = constraint;
	}
	
	public static class Reader extends WhileyBlock.Reader {
		public Reader() {
			super("Precondition");
		}
	}
}
