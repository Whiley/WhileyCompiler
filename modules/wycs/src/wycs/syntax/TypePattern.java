package wycs.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;
import wycs.core.SemanticType;

/**
 * Represents a type pattern which is used for pattern matching.
 * 
 * @author djp
 * 
 */
public abstract class TypePattern extends SyntacticElement.Impl {
	
	// FIXME: at some point in the future, a type pattern should implement
	// WycsFile.Context. This would improve error reporting, especially with
	// constraints.
	
	public TypePattern(Attribute... attributes) {
		super(attributes);
	}

	public TypePattern(Collection<Attribute> attributes) {
		super(attributes);		
	}
	
	public abstract SyntacticType toSyntacticType();
	
	public abstract TypePattern instantiate(Map<String,SyntacticType> binding);
	
	public abstract TypePattern substitute(Map<String,Expr> binding);
	
	public abstract void addDeclaredVariables(Collection<String> variables);
	
	/**
	 * A type pattern leaf is simply a syntactic type, along with an optional
	 * variable identifier.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Leaf extends TypePattern {
		public final SyntacticType type;
		public final Expr.Variable var;
				
		public Leaf(SyntacticType type, Expr.Variable var, Attribute... attributes) {
			super(attributes);
			this.type = type;
			this.var = var;
		}
		
		public Leaf(SyntacticType type, Expr.Variable var,List<Attribute> attributes) {
			super(attributes);
			this.type = type;
			this.var = var;
		}
				
		public SyntacticType toSyntacticType() {
			return type;
		}		
		
		public void addDeclaredVariables(Collection<String> variables) {
			if(var != null) {
				variables.add(var.name);
			}
		}
	}
	
	/**
	 * A rational type pattern is simply a sequence of two type patterns
	 * seperated by '/' separated by commas.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Rational extends TypePattern {
		public final TypePattern numerator;
		public final TypePattern denominator;
		
		public Rational(TypePattern numerator, TypePattern denominator,
				Attribute... attributes) {
			super(attributes);
			this.numerator = numerator;
			this.denominator = denominator;
		}

		public Rational(TypePattern numerator, TypePattern denominator,
				List<Attribute> attributes) {
			super(attributes);
			this.numerator = numerator;
			this.denominator = denominator;
		}		
		
		public SyntacticType.Primitive toSyntacticType() {
			return new SyntacticType.Real(attributes());
		}
		
		public void addDeclaredVariables(Collection<String> variables) {
			numerator.addDeclaredVariables(variables);
			denominator.addDeclaredVariables(variables);
		}
	}
	
	/**
	 * A type pattern tuple is simply a sequence of two or type patterns
	 * separated by commas.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Tuple extends TypePattern {
		public final List<TypePattern> elements;

		public Tuple(List<TypePattern> elements,
				Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public Tuple(List<TypePattern> elements,
				List<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}
		
		public SyntacticType.Tuple toSyntacticType() {
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			for (int i = 0; i != elements.size(); ++i) {
				types.add(elements.get(i).toSyntacticType());
			}
			return new SyntacticType.Tuple(types, attributes());
		}
		
		public void addDeclaredVariables(Collection<String> variables) {		
			for(TypePattern p : elements) {
				p.addDeclaredVariables(variables);
			}
		}
	}
	
	/**
	 * A record type pattern is simply a sequence of two or type patterns
	 * separated by commas enclosed in curly braces.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Record extends TypePattern {
		public final List<TypePattern.Leaf> elements;
		public final boolean isOpen;

		public Record(SyntacticType.Record record, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern.Leaf>();
			this.isOpen = record.isOpen;
			for (Map.Entry<String, SyntacticType> e : record.types.entrySet()) {
				String field = e.getKey();
				// FIXME: missing source attribute information on local variable
				elements.add(new TypePattern.Leaf(e.getValue(),
						new Expr.Variable(field), e.getValue()
								.attributes()));
			}
		}
		
		public Record(List<TypePattern.Leaf> elements, boolean isOpen,
				Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern.Leaf>(elements);
			this.isOpen = isOpen;
		}

		public Record(List<TypePattern.Leaf> elements, boolean isOpen,
				List<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern.Leaf>(elements);
			this.isOpen = isOpen;
		}

		public SyntacticType.Record toSyntacticType() {
			HashMap<String, SyntacticType> types = new HashMap<String, SyntacticType>();
			for (int i = 0; i != elements.size(); ++i) {
				TypePattern.Leaf tp = elements.get(i);
				types.put(tp.var.name, tp.toSyntacticType());
			}
			return new SyntacticType.Record(isOpen, types, attributes());
		}
		
		public void addDeclaredVariables(Collection<String> variables) {
			for(TypePattern p : elements) {
				p.addDeclaredVariables(variables);
			}
		}
	}
	
	/**
	 * A union type pattern is a sequence of type patterns separated by a
	 * vertical bar ('|').
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Union extends TypePattern {
		public final List<TypePattern> elements;
		
		public Union(List<TypePattern> elements, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public Union(List<TypePattern> elements, List<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public SyntacticType.Union toSyntacticType() {
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			for (int i = 0; i != elements.size(); ++i) {
				TypePattern tp = (TypePattern) elements.get(i);
				types.add((SyntacticType) tp.toSyntacticType());
			}
			return new SyntacticType.Union(types, attributes());
		}
		
		public void addDeclaredVariables(Collection<String> variables) {
			// TODO: at some point, we can extend this further to look at the
			// elements type we have and try to extract common variables.
		}
	}
	
	/**
	 * An intersection type pattern is a sequence of type patterns separated by a
	 * vertical bar ('&').
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Intersection extends TypePattern {
		public final List<TypePattern> elements;
		
		public Intersection(List<TypePattern> elements,
				Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public Intersection(List<TypePattern> elements,
				List<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public SyntacticType.Intersection toSyntacticType() {
			ArrayList<SyntacticType> types = new ArrayList<SyntacticType>();
			for (int i = 0; i != elements.size(); ++i) {
				TypePattern tp = (TypePattern) elements.get(i);
				types.add(tp.toSyntacticType());
			}
			return new SyntacticType.Intersection(types, attributes());
		}
		
		public void addDeclaredVariables(Collection<String> variables) {
			// TODO: at some point, we can extend this further to look at the
			// elements type we have and try to extract common variables.
		}
	}
}
