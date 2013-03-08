package wycs.lang;

import java.util.Collection;

import wybs.lang.SyntacticElement;
import wybs.lang.Attribute;

public abstract class SyntacticType extends SyntacticElement.Impl {
	
	private SyntacticType(Attribute... attributes) {
		super(attributes);		
	}	
	
	private SyntacticType(String variable, Collection<Attribute> attributes) {
		super(attributes);
	}
	
	public static class Primitive extends SyntacticType {
		public final SemanticType.Atom type;
		
		public Primitive(SemanticType.Atom type, Attribute... attributes) {
			super(attributes);
			this.type = type;
		}
		
		public String toString() {
			return type.toString();
		}
	}
	
	public static class Variable extends SyntacticType {
		public final String var;
		
		public Variable(String var, Attribute... attributes) {
			super(attributes);
			this.var = var;
		}
		
		public String toString() {
			return var;
		}
	}
	
	public static class Not extends SyntacticType {
		public final SyntacticType element;
		
		public Not(SyntacticType element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}
		
		public String toString() {		
			return "!" + element;
		}
	}
	
	public static class Or extends SyntacticType {
		public SyntacticType[] elements;
		
		public Or(SyntacticType[] elements, Attribute... attributes) {
			super(attributes);
			this.elements = elements;
		}
		
		public String toString() {
			String s = "";
			for(int i=0;i!=elements.length;++i) {
				if(i != 0) { s += " | "; }
				s += elements.length;
			}			
			return s;			
		}
	}
	
	public static class And extends SyntacticType {
		public SyntacticType[] elements;
		
		public And(SyntacticType[] elements, Attribute... attributes) {
			super(attributes);
			this.elements = elements;
		}
		
		public String toString() {
			String s = "";
			for(int i=0;i!=elements.length;++i) {
				if(i != 0) { s += " & "; }
				s += elements[i];
			}			
			return s;			
		}
	}
	
	public static class Set extends SyntacticType {
		public final SyntacticType element;
		
		public Set(SyntacticType element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}
		
		public String toString() {		
			return "{" + element + "}";
		}
	}
	
	public static class Tuple extends SyntacticType {
		public final SyntacticType[] elements;
		
		public Tuple(SyntacticType[] elements, Attribute... attributes) {
			super(attributes);
			this.elements = elements;
		}
		
		public String toString() {
			String s = "";
			for(int i=0;i!=elements.length;++i) {
				if(i != 0) { s += ", "; }
				s += elements[i];
			}
			return "(" + s + ")";			
		}
	}
	
	public static class External extends SyntacticType {
		public final String name;
		public final SyntacticType[] generics;
		
		public External(String name, SyntacticType[] generics, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.generics = generics;
		}
		
		public String toString() {
			if(generics.length > 0) {
				String r = "<";
				for(int i=0;i!=generics.length;++i) {
					if(i != 0) {
						r = r + ",";
					}
					r = r + generics[i];
				}
				return name + r + ">";
			} else {
				return name;
			}
		}
	}
}
