package wycs.lang;

import java.util.*;

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
			return type + super.toString();
		}
	}
	
	public static class Variable extends SyntacticType {
		public final String var;
		
		public Variable(String var, Attribute... attributes) {
			super(attributes);
			this.var = var;
		}
		
		public String toString() {
			return var + super.toString();
		}
	}
	
	public static class Not extends SyntacticType {
		public final SyntacticType element;
		
		public Not(SyntacticType element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}
		
		public String toString() {		
			return "!" + element + super.toString();
		}
	}
	
	public static class Or extends SyntacticType {
		public final ArrayList<SyntacticType> elements;
		
		public Or(List<SyntacticType> elements, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}
		
		public String toString() {
			String s = "";
			for(int i=0;i!=elements.size();++i) {
				if(i != 0) { s += " | "; }
				s += elements.get(i);
			}			
			return s;			
		}
	}
	
	public static class And extends SyntacticType {
		public final ArrayList<SyntacticType> elements;
		
		public And(List<SyntacticType> elements, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}
		
		public String toString() {
			String s = "";
			for(int i=0;i!=elements.size();++i) {
				if(i != 0) { s += " & "; }
				s += elements.get(i);
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
		public final ArrayList<SyntacticType> elements;
		
		public Tuple(List<SyntacticType> elements, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}
		
		public String toString() {
			String s = "";
			for(int i=0;i!=elements.size();++i) {
				if(i != 0) { s += ", "; }
				s += elements.get(i);
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
