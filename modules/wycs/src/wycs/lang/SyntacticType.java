package wycs.lang;

import java.util.*;

import wybs.lang.SyntacticElement;
import wybs.lang.Attribute;

public abstract class SyntacticType extends SyntacticElement.Impl {
	public String name;
	
	private SyntacticType(String name, Attribute... attributes) {
		super(attributes);		
		this.name = name;
	}	
	
	private SyntacticType(String variable, Collection<Attribute> attributes) {
		super(attributes);
		this.name = variable;
	}
	
	public String toString() {
		if(name != null) {
			return " " + name;
		} else {
			return "";
		}
	}
	
	public static class Primitive extends SyntacticType {
		public final Type.Atom type;
		
		public Primitive(String name, Type.Atom type, Attribute... attributes) {
			super(name,attributes);
			this.type = type;
		}
		
		public String toString() {
			return type + super.toString();
		}
	}
	
	public static class Var extends SyntacticType {
		public final String var;
		
		public Var(String name, String var, Attribute... attributes) {
			super(name,attributes);
			this.var = var;
		}
		
		public String toString() {
			return var + super.toString();
		}
	}
	
	public static class Not extends SyntacticType {
		public final SyntacticType element;
		
		public Not(String name, SyntacticType element, Attribute... attributes) {
			super(name, attributes);
			this.element = element;
		}
		
		public String toString() {		
			return "!" + element + super.toString();
		}
	}
	
	public static class Or extends SyntacticType {
		public final ArrayList<SyntacticType> elements;
		
		public Or(String name, List<SyntacticType> elements, Attribute... attributes) {
			super(name, attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}
		
		public String toString() {
			String s = "";
			for(int i=0;i!=elements.size();++i) {
				if(i != 0) { s += " | "; }
				s += elements.get(i);
			}
			if(name != null) {
				return "(" + s + ")" + name;
			} else {
				return s;
			}
		}
	}
	
	public static class And extends SyntacticType {
		public final ArrayList<SyntacticType> elements;
		
		public And(String name, List<SyntacticType> elements, Attribute... attributes) {
			super(name, attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}
		
		public String toString() {
			String s = "";
			for(int i=0;i!=elements.size();++i) {
				if(i != 0) { s += " & "; }
				s += elements.get(i);
			}
			if(name != null) {
				return "(" + s + ")" + name;
			} else {
				return s;
			}
		}
	}
	
	public static class Set extends SyntacticType {
		public final SyntacticType element;
		
		public Set(String name, SyntacticType element, Attribute... attributes) {
			super(name,attributes);
			this.element = element;
		}
		
		public String toString() {		
			return "{" + element + "}" + super.toString();
		}
	}
	
	public static class Tuple extends SyntacticType {
		public final ArrayList<SyntacticType> elements;
		
		public Tuple(String name, List<SyntacticType> elements, Attribute... attributes) {
			super(name, attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}
		
		public String toString() {
			String s = "";
			for(int i=0;i!=elements.size();++i) {
				if(i != 0) { s += ", "; }
				s += elements.get(i);
			}
			return "(" + s + ")" + super.toString();			
		}
	}
}
