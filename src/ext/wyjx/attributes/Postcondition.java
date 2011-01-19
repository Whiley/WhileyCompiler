package wyjx.attributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import wyil.lang.*;
import wyjvm.io.BinaryInputStream;
import wyjvm.lang.Constant;
import wyjx.jvm.attributes.WhileyBlock;

public class Postcondition extends WhileyBlock implements Attribute {
	public final Block constraint;
		
	public Postcondition(Block constraint) {
		super("Postcondition",constraint);
		this.constraint = constraint;
	}
	
	public static class Reader extends WhileyBlock.Reader {
		public Reader() {
			super("Postcondition");
		}
		
		public WhileyBlock read(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			return new Postcondition(super.read(reader,constantPool).block());
		}
	}
}
