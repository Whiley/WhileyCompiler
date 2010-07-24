package wyjc.lang;

import java.util.Collection;
import java.util.List;

import wyjc.ast.attrs.*;
import wyil.lang.Type;

public class WhileyFile {
	
	public interface Decl extends SyntacticElement {
		public String name();
	}
	
	public static class ConstDecl extends
				SyntacticElementImpl implements Decl {
		
		public final List<Modifier> modifiers;
		public final Expr value;
		public final String name;

		public ConstDecl(List<Modifier> modifiers, Expr value, String name,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.value = value;
			this.name = name;
		}

		public ConstDecl(List<Modifier> modifiers, Expr value, String name,
				Collection<Attribute> attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.value = value;
			this.name = name;
		}

		public String name() {
			return name;
		}
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public String toString() {
			return "define " + value + " as " + name;
		}
	}
	
	public static class TypeDecl extends SyntacticElementImpl implements Decl {
		public final List<Modifier> modifiers;
		public final Type type;		
		public final String name;

		public TypeDecl(List<Modifier> modifiers, Type type, String name,
				Attribute... attributes) {
			super(attributes);
			this.modifiers = modifiers;
			this.type = type;
			this.name = name;
		}

		public TypeDecl(List<Modifier> modifiers, Type type, String name,
				Collection<Attribute> attributes) { 
			super(attributes);		
			this.modifiers = modifiers;
			this.type = type;			
			this.name = name;
		}				
		
		public boolean isPublic() {
			for(Modifier m : modifiers) {
				if(m instanceof Modifier.Public) {
					return true;
				}
			}
			return false;
		}
		
		public String name() { return name; }		
		
		public String toString() {
			return "define " + type + " as " + name;
		}
	}

}
