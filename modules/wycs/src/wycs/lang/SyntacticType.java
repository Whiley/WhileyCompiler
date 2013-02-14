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
	
	public static class Primitive extends SyntacticType {
		public final Type.Atom type;
		
		public Primitive(String name, Type.Atom type, Attribute... attributes) {
			super(name,attributes);
			this.type = type;
		}
	}
	
	public static class Var extends SyntacticType {
		public final String var;
		
		public Var(String name, String var, Attribute... attributes) {
			super(name,attributes);
			this.var = var;
		}
	}
	
	public static class Not extends SyntacticType {
		public final SyntacticType element;
		
		public Not(String name, SyntacticType element, Attribute... attributes) {
			super(name, attributes);
			this.element = element;
		}
	}
	
	public static class Or extends SyntacticType {
		public final ArrayList<SyntacticType> elements;
		
		public Or(String name, List<SyntacticType> elements, Attribute... attributes) {
			super(name, attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}
	}
	
	public static class And extends SyntacticType {
		public final ArrayList<SyntacticType> elements;
		
		public And(String name, List<SyntacticType> elements, Attribute... attributes) {
			super(name, attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}
	}
	
	public static class Set extends SyntacticType {
		public final SyntacticType element;
		
		public Set(String name, SyntacticType element, Attribute... attributes) {
			super(name,attributes);
			this.element = element;
		}
	}
	
	public static class Tuple extends SyntacticType {
		public final ArrayList<SyntacticType> elements;
		
		public Tuple(String name, List<SyntacticType> elements, Attribute... attributes) {
			super(name, attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}
	}
}
