package wyone.core;

import wyil.lang.Attribute;

public class Attributes {
	public static final class TypeAttr implements Attribute {
		public final Type type;

		public TypeAttr(Type type) {
			this.type = type;
		}
	}
	
	public static final class FunAttr implements Attribute {
		public final Type.Fun type;

		public FunAttr(Type.Fun type) {
			this.type = type;
		}
	}
}
