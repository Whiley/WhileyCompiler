package wyjx.attributes;

import java.io.IOException;
import java.util.Map;

import wyil.lang.*;
import wyjvm.io.BinaryInputStream;
import wyjvm.lang.Constant;
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
		public WhileyBlock read(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			return new Precondition(super.read(reader,constantPool).block());
		}
	}
}
