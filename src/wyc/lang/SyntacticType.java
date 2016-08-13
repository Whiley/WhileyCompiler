// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyc.lang;

import java.util.*;

import wybs.lang.Attribute;
import wybs.lang.SyntacticElement;
import wyc.lang.SyntacticType.NonUnion;

/**
 * <p>
 * Provides classes for representing types in Whiley's source language. These
 * are referred to as <i>unresolved types</i> as they include nominal types
 * whose full NameID remains unknown. Unresolved types are <i>resolved</i>
 * during the name resolution> stage of the compiler.
 * </p>
 *
 * <p>
 * Each class is an instance of <code>SyntacticElement</code> and, hence, can be
 * adorned with certain information (such as source location, etc).
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface SyntacticType extends SyntacticElement {

	/**
	 * A non-union type represents a type which is not an instance of
	 * <code>Union</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface NonUnion extends SyntacticType {
		// FIXME: this interface should not exist!
	}
	public interface Primitive extends SyntacticType {

	}

	/**
	 * The type <code>any</code> represents the type whose variables may hold
	 * any possible value. <b>NOTE:</b> the any type is top in the type lattice.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Any extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Any(Attribute... attributes) {
			super(attributes);
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
	public static final class Void extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Void(Attribute... attributes) {
			super(attributes);
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
	public static final class Null extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Null(Attribute... attributes) {
			super(attributes);
		}
	}

	/**
	 * Represents the set of boolean values (i.e. true and false)
	 * @author David J. Pearce
	 *
	 */
	public static final class Bool extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Bool(Attribute... attributes) {
			super(attributes);
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
	public static final class Byte extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Byte(Attribute... attributes) {
			super(attributes);
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
	public static final class Int extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Int(Attribute... attributes) {
			super(attributes);
		}
	}

	/**
	 * Represents a nominal type, which is of the form:
	 *
	 * <pre>
	 * NominalType ::= Identifier ('.' Identifier)*
	 * </pre>
	 *
	 * A nominal type specifies the name of a type defined elsewhere. In some
	 * cases, this type can be expanded (or "inlined"). However, visibility
	 * modifiers can prevent this and, thus, give rise to true nominal types.
	 *
	 * @return
	 */
	public static final class Nominal extends SyntacticElement.Impl implements NonUnion {
		public final ArrayList<String> names;
		public Nominal(Collection<String> names, Attribute... attributes) {
			super(attributes);
			this.names = new ArrayList<String>(names);
		}
	}

	/**
	 * Represents a list type, which is of the form:
	 *
	 * <pre>
	 * ArrayType ::= Type '[' ']'
	 * </pre>
	 *
	 * @return
	 */
	public static final class Array extends SyntacticElement.Impl implements NonUnion {
		public final SyntacticType element;
		public Array(SyntacticType element, Attribute... attributes) {
			super(attributes);
			this.element = element;
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
	public static final class Negation extends SyntacticElement.Impl implements NonUnion {
		public final SyntacticType element;
		public Negation(SyntacticType element, Attribute... attributes) {
			this.element = element;
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
	public static final class Union extends SyntacticElement.Impl implements SyntacticType {
		public final ArrayList<NonUnion> bounds;

		public Union(Collection<NonUnion> bounds, Attribute... attributes) {
			super(attributes);
			if (bounds.size() < 2) {
				new IllegalArgumentException(
						"Cannot construct a type union with fewer than two bounds");
			}
			this.bounds = new ArrayList<NonUnion>(bounds);
		}

		public Union(ArrayList<NonUnion> bounds, java.util.Collection<Attribute> attributes) {
			super(attributes);
			if (bounds.size() < 2) {
				new IllegalArgumentException(
						"Cannot construct a type union with fewer than two bounds");
			}
			this.bounds = new ArrayList<NonUnion>(bounds);
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
	public static final class Intersection extends SyntacticElement.Impl implements SyntacticType {
		public final ArrayList<SyntacticType> bounds;

		public Intersection(Collection<SyntacticType> bounds, Attribute... attributes) {
			super(attributes);
			if (bounds.size() < 2) {
				new IllegalArgumentException(
						"Cannot construct a type intersection with fewer than two bounds");
			}
			this.bounds = new ArrayList<SyntacticType>(bounds);
		}

		public Intersection(ArrayList<SyntacticType> bounds,
				Collection<Attribute> attributes) {
			super(attributes);
			if (bounds.size() < 2) {
				new IllegalArgumentException(
						"Cannot construct a type intersection with fewer than two bounds");
			}
			this.bounds = new ArrayList<SyntacticType>(bounds);
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
	public static final class Reference extends SyntacticElement.Impl implements NonUnion {
		public final SyntacticType element;
		public final String lifetime;
		public final boolean lifetimeWasExplicit;
		public Reference(SyntacticType element, String lifetime, boolean lifetimeWasExplicit, Attribute... attributes) {
			this.element = element;
			this.lifetime = lifetime;
			this.lifetimeWasExplicit = lifetimeWasExplicit;
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
	public static final class Record extends SyntacticElement.Impl implements NonUnion {
		public final HashMap<String,SyntacticType> types;
		public final boolean isOpen;

		public Record(boolean isOpen,
				java.util.Map<String, SyntacticType> types,
				Attribute... attributes) {
			super(attributes);
			if(types.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.isOpen = isOpen;
			this.types = new HashMap<String,SyntacticType>(types);
		}

		public Record(boolean isOpen,
				java.util.Map<String, SyntacticType> types,
				java.util.List<Attribute> attributes) {
			super(attributes);
			if(types.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.isOpen = isOpen;
			this.types = new HashMap<String,SyntacticType>(types);
		}
	}

	public abstract static class FunctionOrMethod extends
			SyntacticElement.Impl implements NonUnion {
		public final ArrayList<SyntacticType> returnTypes;
		public final ArrayList<SyntacticType> paramTypes;
		public final ArrayList<String> contextLifetimes;
		public final ArrayList<String> lifetimeParameters;

		public FunctionOrMethod(Collection<SyntacticType> returnTypes, Collection<SyntacticType> paramTypes,
				Collection<String> contextLifetimes, Collection<String> lifetimeParameters, Attribute... attributes) {
			super(attributes);
			this.returnTypes = new ArrayList<SyntacticType>(returnTypes);
			this.paramTypes = new ArrayList<SyntacticType>(paramTypes);
			this.contextLifetimes = new ArrayList<String>(contextLifetimes);
			this.lifetimeParameters = new ArrayList<String>(lifetimeParameters);
			for(SyntacticType t : paramTypes) {
				if(t == null) {
					throw new IllegalArgumentException("parameter cannot be null"); 
				}
			}
			for(SyntacticType t : returnTypes) {
				if(t == null) {
					throw new IllegalArgumentException("return cannot be null"); 
				}
			}
			for(String s : lifetimeParameters) {
				if(s == null) {
					throw new IllegalArgumentException("lifetime cannot be null");
				}
			}
		}

		public FunctionOrMethod(Collection<SyntacticType> returnTypes, Collection<SyntacticType> paramTypes,
				Collection<String> contextLifetimes, Collection<String> lifetimeParameters, Collection<Attribute> attributes) {
			super(attributes);
			this.returnTypes = new ArrayList<SyntacticType>(returnTypes);
			this.paramTypes = new ArrayList<SyntacticType>(paramTypes);
			this.contextLifetimes = new ArrayList<String>(contextLifetimes);
			this.lifetimeParameters = new ArrayList<String>(lifetimeParameters);
			for(SyntacticType t : paramTypes) {
				if(t == null) {
					throw new IllegalArgumentException("parameter cannot be null"); 
				}
			}
			for(SyntacticType t : returnTypes) {
				if(t == null) {
					throw new IllegalArgumentException("return cannot be null"); 
				}
			}
			for(String s : lifetimeParameters) {
				if(s == null) {
					throw new IllegalArgumentException("lifetime cannot be null");
				}
			}
		}
	}

	public static class Function extends FunctionOrMethod
	implements NonUnion {
		public Function(Collection<SyntacticType> returnTypes, 
				Collection<SyntacticType> paramTypes,
				Attribute... attributes) {
			super(returnTypes,paramTypes,Collections.<String>emptySet(),Collections.<String>emptyList(),attributes);
		}
		public Function(Collection<SyntacticType> returnTypes, Collection<SyntacticType> paramTypes,
				Collection<Attribute> attributes) {
			super(returnTypes,paramTypes,Collections.<String>emptySet(),Collections.<String>emptyList(),attributes);
		}
	}

	public static class Method extends FunctionOrMethod
	implements NonUnion {
		public Method(Collection<SyntacticType> returnTypes, Collection<SyntacticType> paramTypes,
				Collection<String> contextLifetimes, Collection<String> lifetimeParameters, Attribute... attributes) {
			super(returnTypes,paramTypes,contextLifetimes,lifetimeParameters,attributes);
		}
		public Method(Collection<SyntacticType> returnTypes, Collection<SyntacticType> paramTypes,
				Collection<String> contextLifetimes, Collection<String> lifetimeParameters, Collection<Attribute> attributes) {
			super(returnTypes,paramTypes,contextLifetimes,lifetimeParameters,attributes);
		}
	}
}

