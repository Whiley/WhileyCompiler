package wycs.syntax;

import wybs.lang.Attribute;
import wycs.core.SemanticType;

public class TypeAttribute implements Attribute {
	public final SemanticType type;
	public TypeAttribute(SemanticType type) {
		this.type = type;
	}
}
