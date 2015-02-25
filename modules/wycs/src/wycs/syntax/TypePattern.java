package wycs.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;
import wycc.util.Pair;
import wycs.core.SemanticType;
import wycs.syntax.SyntacticType.Tuple;
import wycs.syntax.SyntacticType.Util;

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

	public abstract void addDeclaredVariables(Collection<String> variables);

	public abstract boolean equivalent(TypePattern p);
	
	public abstract TypePattern copy();
	
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

		public Leaf(SyntacticType type, Expr.Variable var,Collection<Attribute> attributes) {
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

		@Override
		public TypePattern.Leaf instantiate(Map<String, SyntacticType> binding) {
			return new TypePattern.Leaf(type.instantiate(binding), var,
					attributes());
		}
		
		@Override
		public boolean equivalent(TypePattern t) {
			if (t instanceof Leaf) {
				Leaf tt = (Leaf) t;
				return type.equivalent(tt.type)
						&& (var == tt.var || (var != null && var
								.equivalent(tt.var)));
			}
			return false;
		}
		
		@Override
		public TypePattern.Leaf copy() {
			Expr.Variable v = var != null ? var.copy() : null;
			return new TypePattern.Leaf(type.copy(),v,attributes());
		}
		
		public String toString() {
			if(var != null) {
				return type + " " + var.name;
			} else {
				return type.toString();
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
				Collection<Attribute> attributes) {
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

		@Override
		public boolean equivalent(TypePattern t) {
			if (t instanceof Rational) {
				Rational tt = (Rational) t;
				return numerator.equivalent(tt.numerator)
						&& denominator.equivalent(tt.denominator);
			}
			return false;
		}
		
		@Override
		public TypePattern.Rational copy() {
			return new TypePattern.Rational(numerator.copy(),
					denominator.copy(), attributes());
		}
		
		@Override
		public TypePattern instantiate(Map<String, SyntacticType> binding) {
			// TODO Auto-generated method stub
			return null;
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
				Collection<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>(elements);
		}

		public Tuple(TypePattern[] elements,
				Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>();
			for(TypePattern p : elements) {
				this.elements.add(p);
			}
		}

		public Tuple(TypePattern[] elements,
				Collection<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern>();
			for(TypePattern p : elements) {
				this.elements.add(p);
			}
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

		@Override
		public TypePattern instantiate(Map<String, SyntacticType> binding) {
			ArrayList<TypePattern> types = new ArrayList<TypePattern>();
			for (int i = 0; i != elements.size(); ++i) {
				types.add(elements.get(i).instantiate(binding));
			}
			return new TypePattern.Tuple(types,attributes());
		}

		@Override
		public boolean equivalent(TypePattern t) {
			if (t instanceof Tuple) {
				Tuple tt = (Tuple) t;
				return equivalent(elements,tt.elements);
			}
			return false;
		}
		
		@Override
		public TypePattern.Tuple copy() {
			ArrayList<TypePattern> types = new ArrayList<TypePattern>();
			for (int i = 0; i != elements.size(); ++i) {
				types.add(elements.get(i).copy());
			}
			return new TypePattern.Tuple(types,attributes());
		}
		
		public String toString() {
			String r = "";
			boolean firstTime = true;
			for(TypePattern p : elements) {
				if(!firstTime) {
					r = r + ", ";
				}
				r += p.toString();
			}
			return r;
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
			for (Pair<SyntacticType, Expr.Variable> p : record.elements) {
				elements.add(new TypePattern.Leaf(p.first(),p.second()));
			}
		}

		public Record(List<TypePattern.Leaf> elements, boolean isOpen,
				Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern.Leaf>(elements);
			this.isOpen = isOpen;
		}

		public Record(List<TypePattern.Leaf> elements, boolean isOpen,
				Collection<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<TypePattern.Leaf>(elements);
			this.isOpen = isOpen;
		}

		public SyntacticType.Record toSyntacticType() {
			ArrayList<Pair<SyntacticType, Expr.Variable>> types = new ArrayList<Pair<SyntacticType, Expr.Variable>>();
			for (int i = 0; i != elements.size(); ++i) {
				TypePattern.Leaf tp = elements.get(i);
				types.add(new Pair<SyntacticType,Expr.Variable>(tp
						.toSyntacticType(), tp.var));
			}
			return new SyntacticType.Record(isOpen, types, attributes());
		}

		public void addDeclaredVariables(Collection<String> variables) {
			for(TypePattern p : elements) {
				p.addDeclaredVariables(variables);
			}
		}

		@Override
		public TypePattern instantiate(Map<String, SyntacticType> binding) {
			ArrayList<TypePattern.Leaf> types = new ArrayList<TypePattern.Leaf>();
			for (int i = 0; i != elements.size(); ++i) {
				TypePattern.Leaf tp = elements.get(i);
				types.add(tp.instantiate(binding));
			}
			return new TypePattern.Record(types, isOpen, attributes());		
		}
		
		@Override
		public boolean equivalent(TypePattern t) {
			if (t instanceof Record) {
				Record tt = (Record) t;
				return equivalent(elements,tt.elements) && isOpen == tt.isOpen;
			}
			return false;
		}
		
		@Override
		public TypePattern.Record copy() {
			ArrayList<TypePattern.Leaf> types = new ArrayList<TypePattern.Leaf>();
			for (int i = 0; i != elements.size(); ++i) {
				types.add(elements.get(i).copy());
			}
			return new TypePattern.Record(types,isOpen,attributes());
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

		public Union(List<TypePattern> elements, Collection<Attribute> attributes) {
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

		@Override
		public TypePattern.Union instantiate(Map<String, SyntacticType> binding) {
			ArrayList<TypePattern> types = new ArrayList<TypePattern>();
			for (int i = 0; i != elements.size(); ++i) {
				TypePattern tp = elements.get(i);
				types.add(tp.instantiate(binding));
			}
			return new TypePattern.Union(types, attributes());
		}
		
		@Override
		public boolean equivalent(TypePattern t) {
			if (t instanceof Union) {
				Union tt = (Union) t;
				return equivalent(elements,tt.elements);
			}
			return false;
		}
		
		@Override
		public TypePattern.Union copy() {
			ArrayList<TypePattern> types = new ArrayList<TypePattern>();
			for (int i = 0; i != elements.size(); ++i) {
				types.add(elements.get(i).copy());
			}
			return new TypePattern.Union(types,attributes());
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
				Collection<Attribute> attributes) {
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

		@Override
		public TypePattern.Intersection instantiate(Map<String, SyntacticType> binding) {
			ArrayList<TypePattern> types = new ArrayList<TypePattern>();
			for (int i = 0; i != elements.size(); ++i) {
				TypePattern tp = elements.get(i);
				types.add(tp.instantiate(binding));
			}
			return new TypePattern.Intersection(types, attributes());
		}
		
		@Override
		public boolean equivalent(TypePattern t) {
			if (t instanceof Intersection) {
				Intersection tt = (Intersection) t;
				return equivalent(elements,tt.elements);
			}
			return false;
		}
		
		@Override
		public TypePattern.Intersection copy() {
			ArrayList<TypePattern> types = new ArrayList<TypePattern>();
			for (int i = 0; i != elements.size(); ++i) {
				types.add(elements.get(i).copy());
			}
			return new TypePattern.Intersection(types,attributes());
		}
	}
	
	public static boolean equivalent(java.util.List<? extends TypePattern> l1,
			java.util.List<? extends TypePattern> l2) {
		if (l1.size() != l2.size()) {
			return false;
		} else {
			for (int i = 0; i != l1.size(); ++i) {
				if (!l1.get(i).equivalent(l2.get(i))) {
					return false;
				}
			}
			return true;
		}
	}
}
