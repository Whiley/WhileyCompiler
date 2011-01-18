package wyjx.attributes;

import java.io.IOException;
import java.util.Map;

import wyil.lang.*;
import wyjvm.io.BinaryInputStream;
import wyjvm.lang.Constant;
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
		
		public WhileyBlock read(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			return new Constraint(super.read(reader,constantPool).block());
		}
	}
}
