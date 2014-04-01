package wycs.syntax;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;
import wycs.core.SemanticType;

public abstract class SyntacticType extends SyntacticElement.Impl {
	
	private SyntacticType(Attribute... attributes) {
		super(attributes);		
	}	
	
	private SyntacticType(Collection<Attribute> attributes) {
		super(attributes);
	}
	
	public abstract SyntacticType instantiate(java.util.Map<String,SyntacticType> binding);
	
	public static class Primitive extends SyntacticType {
		public final SemanticType.Atom type;
		
		public Primitive(SemanticType.Atom type, Attribute... attributes) {
			super(attributes);
			this.type = type;
		}
		
		public String toString() {
			return type.toString();
		}
		
		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			return this;
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
		
		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			SyntacticType t = binding.get(var);
			if(var != null) {
				return t;
			} else {
				return this;
			}
		}
	}
	
	public static class Not extends SyntacticType {
		public final SyntacticType element;
		
		public Not(SyntacticType element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}
		
		public Not(SyntacticType element, Collection<Attribute> attributes) {
			super(attributes);
			this.element = element;
		}
		
		public String toString() {		
			return "!" + element;
		}
		
		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			SyntacticType t = element.instantiate(binding);
			if(t != element) {
				return new Not(t,attributes());
			} else {
				return this;
			}
		}
	}
	
	public static class Or extends SyntacticType {
		public SyntacticType[] elements;
		
		public Or(SyntacticType[] elements, Attribute... attributes) {
			super(attributes);
			this.elements = elements;
		}
		
		public Or(SyntacticType[] elements, Collection<Attribute> attributes) {
			super(attributes);
			this.elements = elements;
		}
		
		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			SyntacticType[] nElements = elements;
			for(int i=0;i!=nElements.length;++i) {
				SyntacticType e = nElements[i];
				SyntacticType t = e.instantiate(binding);
				if(nElements != elements) {
					nElements[i] = t;	
				} else if(e != t) {
					nElements = Arrays.copyOf(elements, nElements.length);
					nElements[i] = t;
				}
			}
			if(nElements != elements) {
				return new Or(nElements,attributes());
			} else {
				return this;
			}
		}

		public String toString() {
			String s = "";
			for(int i=0;i!=elements.length;++i) {
				if(i != 0) { s += " | "; }
				s += elements[i];
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
		
		public And(SyntacticType[] elements, Collection<Attribute> attributes) {
			super(attributes);
			this.elements = elements;
		}
		
		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			SyntacticType[] nElements = elements;
			for(int i=0;i!=nElements.length;++i) {
				SyntacticType e = nElements[i];
				SyntacticType t = e.instantiate(binding);
				if(nElements != elements) {
					nElements[i] = t;	
				} else if(e != t) {
					nElements = Arrays.copyOf(elements, nElements.length);
					nElements[i] = t;
				}
			}
			if(nElements != elements) {
				return new And(nElements,attributes());
			} else {
				return this;
			}
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

		public Set(SyntacticType element, Collection<Attribute> attributes) {
			super(attributes);
			this.element = element;
		}
		
		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			SyntacticType t = element.instantiate(binding);
			if(t != element) {
				return new Set(t,attributes());
			} else {
				return this;
			}
		}

		public String toString() {		
			return "{" + element + "}";
		}
	}
	
	public static class Map extends SyntacticType {
		public final SyntacticType key;
		public final SyntacticType value;
		
		public Map(SyntacticType key, SyntacticType value, Attribute... attributes) {
			super(attributes);
			this.key = key;
			this.value = value;
		}

		public Map(SyntacticType key, SyntacticType value, Collection<Attribute> attributes) {
			super(attributes);
			this.key = key;
			this.value = value;
		}
		
		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			SyntacticType f = key.instantiate(binding);
			SyntacticType t = value.instantiate(binding);
			if(f != key || t != value) {
				return new Map(f,t,attributes());
			} else {
				return this;
			}
		}

		public String toString() {		
			return "{" + key +"=>" + value + "}";
		}
	}
	
	public static class List extends SyntacticType {
		public final SyntacticType element;
		
		public List(SyntacticType element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}

		public List(SyntacticType element, Collection<Attribute> attributes) {
			super(attributes);
			this.element = element;
		}
		
		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			SyntacticType t = element.instantiate(binding);
			if(t != element) {
				return new List(t,attributes());
			} else {
				return this;
			}
		}

		public String toString() {		
			return "[" + element + "]";
		}
	}
	
	public static class Tuple extends SyntacticType {
		public final SyntacticType[] elements;
		
		public Tuple(SyntacticType[] elements, Attribute... attributes) {
			super(attributes);
			this.elements = elements;
		}
		
		public Tuple(SyntacticType[] elements, Collection<Attribute> attributes) {
			super(attributes);
			this.elements = elements;
		}
		
		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			SyntacticType[] nElements = elements;
			for(int i=0;i!=nElements.length;++i) {
				SyntacticType e = nElements[i];
				SyntacticType t = e.instantiate(binding);
				if(nElements != elements) {
					nElements[i] = t;	
				} else if(e != t) {
					nElements = Arrays.copyOf(elements, nElements.length);
					nElements[i] = t;
				}
			}
			if(nElements != elements) {
				return new Tuple(nElements,attributes());
			} else {
				return this;
			}
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
}
