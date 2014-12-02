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
		public String toString() {
			return "string";
		}
	}

	public static class Variable extends SyntacticElement.Impl implements SyntacticType {
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

	/**
	 * Parse a negation type, which is of the form:
	 *
	 * <pre>
	 * ReferenceType ::= '!' Type
	 * </pre>
	 *
	 * @return
	 */
	public static class Negation extends SyntacticElement.Impl implements SyntacticType {
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
			return "!" + element;
		}

		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			SyntacticType t = element.instantiate(binding);
			if(t != element) {
				return new Negation(t,attributes());
			} else {
				return this;
			}
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
	public static class Reference extends SyntacticElement.Impl implements SyntacticType {
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
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			SyntacticType t = element.instantiate(binding);
			if(t != element) {
				return new Reference(t,attributes());
			} else {
				return this;
			}
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
	public static class Nominal extends SyntacticElement.Impl implements SyntacticType {
		public final ArrayList<String> names;

		public Nominal(Collection<String> names, Attribute... attributes) {
			super(attributes);
			this.names = new ArrayList<String>(names);
		}

		public Nominal(Collection<String> names, Collection<Attribute> attributes) {
			super(attributes);
			this.names = new ArrayList<String>(names);
		}

		public String toString() {
			String r = "";
			for(int i=0;i!=names.size();++i) {
				if(i != 0) {
					r += ".";
				}
				r += names.get(i);
			}
			return r;
		}

		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			return this;
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
	public static class Union extends SyntacticElement.Impl implements SyntacticType {
		public java.util.List<SyntacticType> elements;

		public Union(Collection<SyntacticType> types, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(types);
		}

		public Union(Collection<SyntacticType> types, Collection<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(types);
		}

		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			java.util.List<SyntacticType> nElements = elements;
			for(int i=0;i!=nElements.size();++i) {
				SyntacticType e = nElements.get(i);
				SyntacticType t = e.instantiate(binding);
				if(nElements != elements) {
					nElements.set(i,t);
				} else if(e != t) {
					nElements = new ArrayList<SyntacticType>(elements);
					nElements.set(i,t);
				}
			}
			if(nElements != elements) {
				return new Union(nElements,attributes());
			} else {
				return this;
			}
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
	public static class Intersection extends SyntacticElement.Impl implements SyntacticType {
		public java.util.List<SyntacticType> elements;

		public Intersection(Collection<SyntacticType> elements, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}

		public Intersection(Collection<SyntacticType> elements, Collection<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}

		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			java.util.List<SyntacticType> nElements = elements;
			for(int i=0;i!=nElements.size();++i) {
				SyntacticType e = nElements.get(i);
				SyntacticType t = e.instantiate(binding);
				if(nElements != elements) {
					nElements.set(i,t);
				} else if(e != t) {
					nElements = new ArrayList<SyntacticType>(elements);
					nElements.set(i,t);
				}
			}
			if(nElements != elements) {
				return new Intersection(nElements,attributes());
			} else {
				return this;
			}
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

	/**
	 * Represents a set type, which is of the form:
	 *
	 * <pre>
	 * SetType ::= '{' Type '}'
	 * </pre>
	 *
	 * @return
	 */
	public static class Set extends SyntacticElement.Impl implements SyntacticType {
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

	/**
	 * Represents a map type, which is of the form:
	 *
	 * <pre>
	 * MapType ::= '{' Type "=>" Type '}'
	 * </pre>
	 *
	 * @return
	 */
	public static class Map extends SyntacticElement.Impl implements SyntacticType {
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

	/**
	 * Represents a list type, which is of the form:
	 *
	 * <pre>
	 * ListType ::= '[' Type ']'
	 * </pre>
	 *
	 * @return
	 */
	public static class List extends SyntacticElement.Impl implements SyntacticType {
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

	/**
	 * Parse a tuple type, which is of the form:
	 *
	 * <pre>
	 * TupleType ::= '(' Type (',' Type)* ')'
	 * </pre>
	 *
	 * @return
	 */
	public static class Tuple extends SyntacticElement.Impl implements SyntacticType {
		public final java.util.List<SyntacticType> elements;

		public Tuple(Collection<SyntacticType> elements, Attribute... attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}

		public Tuple(Collection<SyntacticType> elements, Collection<Attribute> attributes) {
			super(attributes);
			this.elements = new ArrayList<SyntacticType>(elements);
		}

		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			java.util.List<SyntacticType> nElements = elements;
			for(int i=0;i!=nElements.size();++i) {
				SyntacticType e = nElements.get(i);
				SyntacticType t = e.instantiate(binding);
				if(nElements != elements) {
					nElements.set(i,t);
				} else if(e != t) {
					nElements = new ArrayList<SyntacticType>(elements);
					nElements.set(i,t);
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
			for(int i=0;i!=elements.size();++i) {
				if(i != 0) { s += ", "; }
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
	public static class Record extends SyntacticElement.Impl implements SyntacticType {
		public final java.util.List<Pair<SyntacticType, Expr.Variable>> elements;
		public final boolean isOpen;

		public Record(boolean isOpen, Collection<Pair<SyntacticType, Expr.Variable>> elements,
				Attribute... attributes) {
			super(attributes);
			this.isOpen = isOpen;
			this.elements = new ArrayList<Pair<SyntacticType, Expr.Variable>>(
					elements);
		}

		public Record(boolean isOpen, Collection<Pair<SyntacticType, Expr.Variable>> elements,
				Collection<Attribute> attributes) {
			super(attributes);
			this.isOpen = isOpen;
			this.elements = new ArrayList<Pair<SyntacticType, Expr.Variable>>(
					elements);
		}

		@Override
		public SyntacticType instantiate(java.util.Map<String,SyntacticType> binding) {
			java.util.List<Pair<SyntacticType, Expr.Variable>> nElements = elements;
			for(int i=0;i!=nElements.size();++i) {
				Pair<SyntacticType, Expr.Variable> e = nElements.get(i);
				SyntacticType t = e.first().instantiate(binding);
				if(nElements != elements) {
					nElements.set(i,new Pair<SyntacticType, Expr.Variable>(t, e.second()));
				} else if(e.second() != t) {
					nElements = new ArrayList<Pair<SyntacticType, Expr.Variable>>(elements);
					nElements.set(i,new Pair<SyntacticType, Expr.Variable>(t, e.second()));
				}
			}
			if(nElements != elements) {
				return new Record(isOpen,nElements,attributes());
			} else {
				return this;
			}
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


	public static class Function extends
			SyntacticElement.Impl implements SyntacticType {
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

			for(int i=0;i!=nParamTypes.size();++i) {
				SyntacticType e = nParamTypes.get(i);
				SyntacticType t = e.instantiate(binding);
				if(nParamTypes != paramTypes) {
					nParamTypes.set(i,t);
				} else if(e != t) {
					nParamTypes = new ArrayList<SyntacticType>(paramTypes);
					nParamTypes.set(i,t);
				}
			}
			if(nParamTypes != paramTypes || nRet != ret || nThrow != throwType) {
				return new Function(nRet,nThrow,nParamTypes,attributes());
			} else {
				return this;
			}
		}
	}
}
