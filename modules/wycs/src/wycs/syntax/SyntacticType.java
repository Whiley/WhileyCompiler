package wycs.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;
import wycc.util.Pair;
import wycs.core.SemanticType;

public interface SyntacticType extends SyntacticElement {

	public abstract SyntacticType instantiate(java.util.Map<String,SyntacticType> binding);

	public abstract boolean equivalent(SyntacticType t);
	
	public abstract SyntacticType copy();
	
	public static abstract class Primitive extends SyntacticElement.Impl
			implements SyntacticType {

		public Primitive(Attribute... attributes) {
			super(attributes);
		}

		public Primitive(Collection<Attribute> attributes) {
			super(attributes);
		}

		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {
			return this;
		}		
	}

	/**
	 * The type <code>any</code> represents the type whose variables may hold
	 * any possible value. <b>NOTE:</b> the any type is top in the type lattice.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Any extends Primitive {
		public Any(Attribute... attributes) {
			super(attributes);
		}

		public Any(Collection<Attribute> attributes) {
			super(attributes);
		}
		@Override
		public boolean equivalent(SyntacticType t) {
			return t instanceof Any;
		}
		@Override
		public SyntacticType.Any copy() {
			return new SyntacticType.Any(attributes());
		}
		public String toString() {
			return "any";
		}		
	}

	/**
	 * A void type represents the type whose variables cannot exist! That is,
	 * they cannot hold any possible value. Void is used to represent the return
	 * type of a function which does not return anything. However, it is also
	 * used to represent the element type of an empty list of set. <b>NOTE:</b>
	 * the void type is a subtype of everything; that is, it is bottom in the
	 * type lattice.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Void extends Primitive {
		public Void(Attribute... attributes) {
			super(attributes);
		}
		public Void(Collection<Attribute> attributes) {
			super(attributes);
		}
		@Override
		public boolean equivalent(SyntacticType t) {
			return t instanceof Void;
		}
		@Override
		public SyntacticType.Void copy() {
			return new SyntacticType.Void(attributes());
		}
		public String toString() {
			return "void";
		}
	}

	/**
	 * The null type is a special type which should be used to show the absence
	 * of something. It is distinct from void, since variables can hold the
	 * special <code>null</code> value (where as there is no special "void"
	 * value). With all of the problems surrounding <code>null</code> and
	 * <code>NullPointerException</code>s in languages like Java and C, it may
	 * seem that this type should be avoided. However, it remains a very useful
	 * abstraction to have around and, in Whiley, it is treated in a completely
	 * safe manner (unlike e.g. Java).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Null extends Primitive {
		public Null(Attribute... attributes) {
			super(attributes);
		}
		public Null(Collection<Attribute> attributes) {
			super(attributes);
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			return t instanceof Null;
		}
		@Override
		public SyntacticType.Null copy() {
			return new SyntacticType.Null(attributes());
		}
		public String toString() {
			return "null";
		}
	}

	/**
	 * Represents the set of boolean values (i.e. true and false)
	 * @author David J. Pearce
	 *
	 */
	public static final class Bool extends Primitive {
		public Bool(Attribute... attributes) {
			super(attributes);
		}

		public Bool(Collection<Attribute> attributes) {
			super(attributes);
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			return t instanceof Bool;
		}
		@Override
		public SyntacticType.Bool copy() {
			return new SyntacticType.Bool(attributes());
		}
		public String toString() {
			return "bool";
		}
	}

	/**
	 * Represents a sequence of 8 bits. Note that, unlike many languages, there
	 * is no representation associated with a byte. For example, to extract an
	 * integer value from a byte, it must be explicitly decoded according to
	 * some representation (e.g. two's compliment) using an auxillary function
	 * (e.g. <code>Byte.toInt()</code>).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Byte extends Primitive {
		public Byte(Attribute... attributes) {
			super(attributes);
		}

		public Byte(Collection<Attribute> attributes) {
			super(attributes);
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			return t instanceof Byte;
		}
		@Override
		public SyntacticType.Byte copy() {
			return new SyntacticType.Byte(attributes());
		}
		public String toString() {
			return "byte";
		}
	}

	/**
	 * Represents a unicode character.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Char extends Primitive {
		public Char(Attribute... attributes) {
			super(attributes);
		}

		public Char(Collection<Attribute> attributes) {
			super(attributes);
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			return t instanceof Char;
		}
		@Override
		public SyntacticType.Char copy() {
			return new SyntacticType.Char(attributes());
		}
		public String toString() {
			return "char";
		}
	}

	/**
	 * Represents the set of (unbound) integer values. Since integer types in
	 * Whiley are unbounded, there is no equivalent to Java's
	 * <code>MIN_VALUE</code> and <code>MAX_VALUE</code> for <code>int</code>
	 * types.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Int extends Primitive {
		public Int(Attribute... attributes) {
			super(attributes);
		}

		public Int(Collection<Attribute> attributes) {
			super(attributes);
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			return t instanceof Int;
		}
		@Override
		public SyntacticType.Int copy() {
			return new SyntacticType.Int(attributes());
		}
		public String toString() {
			return "int";
		}
	}

	/**
	 * The type <code>real</code> represents the set of (unbound) rational
	 * numbers.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Real extends Primitive {
		public Real(Attribute... attributes) {
			super(attributes);
		}

		public Real(Collection<Attribute> attributes) {
			super(attributes);
		}

		public Real(java.util.List<Attribute> attributes) {
			super(attributes);
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			return t instanceof Real;
		}
		@Override
		public SyntacticType.Real copy() {
			return new SyntacticType.Real(attributes());
		}
		public String toString() {
			return "real";
		}
	}

	/**
	 * The type <code>string</code> represents a string of characters
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Strung extends Primitive {
		public Strung(Attribute... attributes) {
			super(attributes);
		}

		public Strung(Collection<Attribute> attributes) {
			super(attributes);
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			return t instanceof Strung;
		}
		@Override
		public SyntacticType.Strung copy() {
			return new SyntacticType.Strung(attributes());
		}
		public String toString() {
			return "string";
		}
	}

	public static class Variable extends SyntacticElement.Impl implements
			SyntacticType {
		public final String var;

		public Variable(String var, Attribute... attributes) {
			super(attributes);
			this.var = var;
		}

		public Variable(String var, Collection<Attribute> attributes) {
			super(attributes);
			this.var = var;
		}
		
		public String toString() {
			return var;
		}

		@Override
		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {
			SyntacticType t = binding.get(var);
			if (var != null) {
				return t;
			} else {
				return this;
			}
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			if (t instanceof Variable) {
				Variable tt = (Variable) t;
				return var.equals(tt.var);
			}
			return false;
		}
		
		@Override
		public SyntacticType.Variable copy() {
			return new SyntacticType.Variable(var,attributes());
		}
	}

	/**
	 * Parse a negation type, which is of the form:
	 *
	 * <pre>
	 * ReferenceType ::= '!' Type
	 * </pre>
	 *
	 * @return
	 */
	public static class Negation extends SyntacticElement.Impl implements
			SyntacticType {
		public final SyntacticType element;

		public Negation(SyntacticType element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}

		public Negation(SyntacticType element, Collection<Attribute> attributes) {
			super(attributes);
			this.element = element;
		}

		public String toString() {
			return "!(" + element + ")";
		}

		@Override
		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {
			SyntacticType t = element.instantiate(binding);
			if (t != element) {
				return new Negation(t, attributes());
			} else {
				return this;
			}
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			if (t instanceof Negation) {
				Negation tt = (Negation) t;
				return element.equivalent(tt.element);
			}
			return false;
		}
		
		@Override
		public SyntacticType.Negation copy() {
			return new SyntacticType.Negation(element.copy(),attributes());
		}
	}

	/**
	 * Parse a reference type, which is of the form:
	 *
	 * <pre>
	 * ReferenceType ::= '&' Type
	 * </pre>
	 *
	 * @return
	 */
	public static class Reference extends SyntacticElement.Impl implements
			SyntacticType {
		public final SyntacticType element;

		public Reference(SyntacticType element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}

		public Reference(SyntacticType element, Collection<Attribute> attributes) {
			super(attributes);
			this.element = element;
		}

		public String toString() {
			return "&" + element;
		}

		@Override
		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {
			SyntacticType t = element.instantiate(binding);
			if (t != element) {
				return new Reference(t, attributes());
			} else {
				return this;
			}
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			if (t instanceof Reference) {
				Reference tt = (Reference) t;
				return element.equivalent(tt.element);
			}
			return false;
		}
		
		@Override
		public SyntacticType.Reference copy() {
			return new SyntacticType.Reference(element.copy(),attributes());
		}
	}

	/**
	 * Parse a nominal type, which is of the form:
	 *
	 * <pre>
	 * NominalType ::= Identifier
	 * </pre>
	 *
	 * @return
	 */
	public static class Nominal extends SyntacticElement.Impl implements
			SyntacticType {
		public final ArrayList<String> names;

		public Nominal(Collection<String> names, Attribute... attributes) {
			super(attributes);
			this.names = new ArrayList<String>(names);
		}

		public Nominal(Collection<String> names,
				Collection<Attribute> attributes) {
			super(attributes);
			this.names = new ArrayList<String>(names);
		}

		public String toString() {
			String r = "";
			for (int i = 0; i != names.size(); ++i) {
				if (i != 0) {
					r += ".";
				}
				r += names.get(i);
			}
			return r;
		}

		@Override
		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {
			return this;
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			if (t instanceof Nominal) {
				Nominal tt = (Nominal) t;
				return names.equals(tt.names);
			}
			return false;
		}
		
		@Override
		public SyntacticType.Nominal copy() {
			return new SyntacticType.Nominal((ArrayList<String>) names.clone(),attributes());
		}
	}

	/**
	 * Represents a union type, which is of the form:
	 *
	 * <pre>
	 * UnionType ::= IntersectionType ('|' IntersectionType)*
	 * </pre>
	 *
	 * Union types are used to compose types together. For example, the type
	 * <code>int|null</code> represents the type which is either an
	 * <code>int</code> or <code>null</code>.
	 *
	 * @return
	 */
	public static class Union extends SyntacticElement.Impl implements
			SyntacticType {
		public java.util.List<SyntacticType> elements;

		public Union(Collection<SyntacticType> types, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(types);
		}

		public Union(Collection<SyntacticType> types,
				Collection<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(types);
		}

		@Override
		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {
			java.util.List<SyntacticType> nElements = elements;
			for (int i = 0; i != nElements.size(); ++i) {
				SyntacticType e = nElements.get(i);
				SyntacticType t = e.instantiate(binding);
				if (nElements != elements) {
					nElements.set(i, t);
				} else if (e != t) {
					nElements = new ArrayList<SyntacticType>(elements);
					nElements.set(i, t);
				}
			}
			if (nElements != elements) {
				return new Union(nElements, attributes());
			} else {
				return this;
			}
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			if (t instanceof Union) {
				Union tt = (Union) t;
				return Util.equivalent(elements, tt.elements);
			}
			return false;
		}

		@Override
		public SyntacticType.Union copy() {
			return new SyntacticType.Union(Util.clone(elements),attributes());
		}
		
		public String toString() {
			String s = "";
			for (int i = 0; i != elements.size(); ++i) {
				if (i != 0) {
					s += " | ";
				}
				s += elements.get(i);
			}
			return s;
		}
	}

	/**
	 * Represents an intersection type, which is of the form:
	 *
	 * <pre>
	 * IntersectionType ::= BaseType ('&' BaseType)*
	 * </pre>
	 *
	 * Intersection types are used to unify types together. For example, the
	 * type <code>{int x, int y}&MyType</code> represents the type which is both
	 * an instanceof of <code>{int x, int y}</code> and an instance of
	 * <code>MyType</code>.
	 *
	 * @return
	 */
	public static class Intersection extends SyntacticElement.Impl implements
			SyntacticType {
		public java.util.List<SyntacticType> elements;

		public Intersection(Collection<SyntacticType> elements,
				Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}

		public Intersection(Collection<SyntacticType> elements,
				Collection<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}

		@Override
		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {
			java.util.List<SyntacticType> nElements = elements;
			for (int i = 0; i != nElements.size(); ++i) {
				SyntacticType e = nElements.get(i);
				SyntacticType t = e.instantiate(binding);
				if (nElements != elements) {
					nElements.set(i, t);
				} else if (e != t) {
					nElements = new ArrayList<SyntacticType>(elements);
					nElements.set(i, t);
				}
			}
			if (nElements != elements) {
				return new Intersection(nElements, attributes());
			} else {
				return this;
			}
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			if (t instanceof Intersection) {
				Intersection tt = (Intersection) t;
				return Util.equivalent(elements, tt.elements);
			}
			return false;
		}

		@Override
		public SyntacticType.Intersection copy() {
			return new SyntacticType.Intersection(Util.clone(elements),attributes());
		}
		
		public String toString() {
			String s = "";
			for (int i = 0; i != elements.size(); ++i) {
				if (i != 0) {
					s += " & ";
				}
				s += elements.get(i);
			}
			return s;
		}
	}

	/**
	 * Represents a list type, which is of the form:
	 *
	 * <pre>
	 * ListType ::= '[' Type ']'
	 * </pre>
	 *
	 * @return
	 */
	public static class List extends SyntacticElement.Impl implements
			SyntacticType {
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
		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {
			SyntacticType t = element.instantiate(binding);
			if (t != element) {
				return new List(t, attributes());
			} else {
				return this;
			}
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			if (t instanceof List) {
				List tt = (List) t;
				return element.equivalent(tt.element);
			}
			return false;
		}

		@Override
		public SyntacticType.List copy() {
			return new SyntacticType.List(element.copy(),attributes());
		}
		
		public String toString() {
			return "[" + element + "]";
		}
	}

	/**
	 * Parse a tuple type, which is of the form:
	 *
	 * <pre>
	 * TupleType ::= '(' Type (',' Type)* ')'
	 * </pre>
	 *
	 * @return
	 */
	public static class Tuple extends SyntacticElement.Impl implements
			SyntacticType {
		public final java.util.List<SyntacticType> elements;

		public Tuple(Collection<SyntacticType> elements,
				Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}

		public Tuple(Collection<SyntacticType> elements,
				Collection<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}

		@Override
		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {
			java.util.List<SyntacticType> nElements = elements;
			for (int i = 0; i != nElements.size(); ++i) {
				SyntacticType e = nElements.get(i);
				SyntacticType t = e.instantiate(binding);
				if (nElements != elements) {
					nElements.set(i, t);
				} else if (e != t) {
					nElements = new ArrayList<SyntacticType>(elements);
					nElements.set(i, t);
				}
			}
			if (nElements != elements) {
				return new Tuple(nElements, attributes());
			} else {
				return this;
			}
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			if (t instanceof Tuple) {
				Tuple tt = (Tuple) t;
				return Util.equivalent(elements, tt.elements);
			}
			return false;
		}

		@Override
		public SyntacticType.Tuple copy() {
			return new SyntacticType.Tuple(Util.clone(elements),attributes());
		}
		
		public String toString() {
			String s = "";
			for (int i = 0; i != elements.size(); ++i) {
				if (i != 0) {
					s += ", ";
				}
				s += elements.get(i);
			}
			return "(" + s + ")";
		}
	}

	/**
	 * Represents record type, which is of the form:
	 *
	 * <pre>
	 * RecordType ::= '{' Type Identifier (',' Type Identifier)* [ ',' "..." ] '}'
	 * </pre>
	 *
	 * @return
	 */
	public static class Record extends SyntacticElement.Impl implements
			SyntacticType {
		public final java.util.List<Pair<SyntacticType, Expr.Variable>> elements;
		public final boolean isOpen;

		public Record(boolean isOpen,
				Collection<Pair<SyntacticType, Expr.Variable>> elements,
				Attribute... attributes) {
			super(attributes);
			this.isOpen = isOpen;
			this.elements = new ArrayList<Pair<SyntacticType, Expr.Variable>>(
					elements);
		}

		public Record(boolean isOpen,
				Collection<Pair<SyntacticType, Expr.Variable>> elements,
				Collection<Attribute> attributes) {
			super(attributes);
			this.isOpen = isOpen;
			this.elements = new ArrayList<Pair<SyntacticType, Expr.Variable>>(
					elements);
		}

		@Override
		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {
			java.util.List<Pair<SyntacticType, Expr.Variable>> nElements = elements;
			for (int i = 0; i != nElements.size(); ++i) {
				Pair<SyntacticType, Expr.Variable> e = nElements.get(i);
				SyntacticType t = e.first().instantiate(binding);
				if (nElements != elements) {
					nElements.set(i, new Pair<SyntacticType, Expr.Variable>(t,
							e.second()));
				} else if (e.second() != t) {
					nElements = new ArrayList<Pair<SyntacticType, Expr.Variable>>(
							elements);
					nElements.set(i, new Pair<SyntacticType, Expr.Variable>(t,
							e.second()));
				}
			}
			if (nElements != elements) {
				return new Record(isOpen, nElements, attributes());
			} else {
				return this;
			}
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			if (t instanceof Record) {
				Record tt = (Record) t;
				if (elements.size() != tt.elements.size()) {
					return false;
				}
				for (int i = 0; i != elements.size(); ++i) {
					Pair<SyntacticType, Expr.Variable> p1 = elements.get(i);
					Pair<SyntacticType, Expr.Variable> p2 = tt.elements.get(i);
					if (!p1.first().equivalent(p2.first())
							|| !p1.second().equivalent(p2.second())) {
						return false;
					}
				}
				return isOpen == tt.isOpen;
			}
			return false;
		}

		@Override
		public SyntacticType.Record copy() {
			ArrayList<Pair<SyntacticType, Expr.Variable>> nElements = new ArrayList<>();
			for (int i = 0; i != elements.size(); ++i) {
				Pair<SyntacticType, Expr.Variable> e = elements.get(i);
				nElements.add(new Pair<>(e.first().copy(),e.second().copy()));
			}
			return new SyntacticType.Record(isOpen,nElements,attributes());
		}
		
		public String toString() {
			String s = "";
			for (int i = 0; i != elements.size(); ++i) {
				if (i != 0) {
					s += ", ";
				}
				s += elements.get(i);
			}
			return "(" + s + ")";
		}
	}

	public static class Function extends SyntacticElement.Impl implements
			SyntacticType {
		public final SyntacticType ret;
		public final SyntacticType throwType;
		public final ArrayList<SyntacticType> paramTypes;

		public Function(SyntacticType ret, SyntacticType throwType,
				Collection<SyntacticType> paramTypes, Attribute... attributes) {
			super(attributes);
			this.ret = ret;
			this.throwType = throwType;
			this.paramTypes = new ArrayList<SyntacticType>(paramTypes);
		}

		public Function(SyntacticType ret, SyntacticType throwType,
				Collection<SyntacticType> paramTypes,
				Collection<Attribute> attributes) {
			super(attributes);
			this.ret = ret;
			this.throwType = throwType;
			this.paramTypes = new ArrayList<SyntacticType>(paramTypes);
		}

		@Override
		public SyntacticType instantiate(
				java.util.Map<String, SyntacticType> binding) {

			java.util.List<SyntacticType> nParamTypes = paramTypes;
			SyntacticType nRet = ret.instantiate(binding);
			SyntacticType nThrow = throwType.instantiate(binding);

			for (int i = 0; i != nParamTypes.size(); ++i) {
				SyntacticType e = nParamTypes.get(i);
				SyntacticType t = e.instantiate(binding);
				if (nParamTypes != paramTypes) {
					nParamTypes.set(i, t);
				} else if (e != t) {
					nParamTypes = new ArrayList<SyntacticType>(paramTypes);
					nParamTypes.set(i, t);
				}
			}
			if (nParamTypes != paramTypes || nRet != ret || nThrow != throwType) {
				return new Function(nRet, nThrow, nParamTypes, attributes());
			} else {
				return this;
			}
		}

		@Override
		public boolean equivalent(SyntacticType t) {
			if (t instanceof Function) {
				Function tt = (Function) t;
				return ret.equivalent(tt.ret)
						&& throwType.equivalent(tt.throwType)
						&& Util.equivalent(paramTypes, tt.paramTypes);
			}
			return false;
		}
		
		@Override
		public SyntacticType.Function copy() {
			return new SyntacticType.Function(ret.copy(), throwType.copy(),
					Util.clone(paramTypes), attributes());
		}
	}

	class Util {
		public static boolean equivalent(java.util.List<SyntacticType> l1,
				java.util.List<SyntacticType> l2) {
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
		
		/**
		 * Clone a list of syntactic types.
		 * 
		 * @param types
		 * @return
		 */
		public static java.util.List<SyntacticType> clone(java.util.List<SyntacticType> types) {
			ArrayList<SyntacticType> nTypes = new ArrayList<SyntacticType>();
			for(int i=0;i!=types.size();++i) {
				nTypes.add(types.get(i).copy());
			}
			return nTypes;
		}
	}
}
