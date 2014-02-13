package wycs.syntax;

import wycc.lang.Attribute;
import wycs.core.SemanticType;

public class TypeAttribute implements Attribute {
	public final SemanticType type;
	public TypeAttribute(SemanticType type) {
		this.type = type;
	}
}
