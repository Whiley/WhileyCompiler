package wycs.lang;

import wybs.lang.Attribute;

public class TypeAttribute implements Attribute {
	public final SemanticType type;
	public TypeAttribute(SemanticType type) {
		this.type = type;
	}
}
