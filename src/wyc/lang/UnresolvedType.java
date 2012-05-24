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

import wybs.lang.SyntacticElement;
import wyil.lang.Attribute;

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
public interface UnresolvedType extends SyntacticElement {

	/**
	 * A non-union type represents a type which is not an instance of
	 * <code>Union</code>.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public interface NonUnion extends UnresolvedType {
	}
	public interface Primitive extends UnresolvedType {
		
	}
	
	public static final class Any extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Any(Attribute... attributes) {
			super(attributes);
		}		
	}
	public static final class Void extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Void(Attribute... attributes) {
			super(attributes);
		}		
	}
	public static final class Null extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Null(Attribute... attributes) {
			super(attributes);
		}		
	}
	public static final class Bool extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Bool(Attribute... attributes) {
			super(attributes);
		}		
	}
	public static final class Byte extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Byte(Attribute... attributes) {
			super(attributes);
		}		
	}
	public static final class Char extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Char(Attribute... attributes) {
			super(attributes);
		}		
	}
	public static final class Int extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Int(Attribute... attributes) {
			super(attributes);
		}		
	}
	public static final class Real extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Real(Attribute... attributes) {
			super(attributes);
		}		
	}
	public static final class Strung extends SyntacticElement.Impl implements NonUnion,Primitive {
		public Strung(Attribute... attributes) {
			super(attributes);
		}		
	}
	public static final class Nominal extends SyntacticElement.Impl implements NonUnion {		
		public final ArrayList<String> names;		
		public Nominal(Collection<String> names, Attribute... attributes) {
			super(attributes);
			this.names = new ArrayList<String>(names);
		}		
	}
	public static final class List extends SyntacticElement.Impl implements NonUnion {
		public final UnresolvedType element;
		public List(UnresolvedType element, Attribute... attributes) {
			super(attributes);
			this.element = element;			
		}
	}
	public static final class Set extends SyntacticElement.Impl implements NonUnion {
		public final UnresolvedType element;
		public Set(UnresolvedType element, Attribute... attributes) {
			super(attributes);
			this.element = element;
		}
	}
	public static final class Dictionary extends SyntacticElement.Impl implements NonUnion {
		public final UnresolvedType key;
		public final UnresolvedType value;
		public Dictionary(UnresolvedType key,UnresolvedType value, Attribute... attributes) {
			super(attributes);
			this.key=key;
			this.value=value;
		}
	}
	public static final class Not extends SyntacticElement.Impl implements NonUnion {
		public final UnresolvedType element;
		public Not(UnresolvedType element, Attribute... attributes) {
			this.element = element;
		}
	}

	public static final class Union extends SyntacticElement.Impl implements UnresolvedType {
		public final ArrayList<NonUnion> bounds;

		public Union(Collection<NonUnion> bounds, Attribute... attributes) {
			if (bounds.size() < 2) {
				new IllegalArgumentException(
						"Cannot construct a type union with fewer than two bounds");
			}
			this.bounds = new ArrayList<NonUnion>(bounds);
		}	
	}
	public static final class Intersection extends SyntacticElement.Impl implements UnresolvedType {
		public final ArrayList<UnresolvedType> bounds;

		public Intersection(Collection<UnresolvedType> bounds, Attribute... attributes) {
			if (bounds.size() < 2) {
				new IllegalArgumentException(
						"Cannot construct a type intersection with fewer than two bounds");
			}
			this.bounds = new ArrayList<UnresolvedType>(bounds);
		}	
	}
	
	public static final class Reference extends SyntacticElement.Impl implements NonUnion {
		public final UnresolvedType element;
		public Reference(UnresolvedType element, Attribute... attributes) {
			this.element = element;
		}
	}
	public static final class Record extends SyntacticElement.Impl implements NonUnion {
		public final HashMap<String,UnresolvedType> types;
		public final boolean isOpen;
		public Record(boolean isOpen, Map<String,UnresolvedType> types, Attribute... attributes) {
			super(attributes);
			if(types.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.isOpen = isOpen;
			this.types = new HashMap<String,UnresolvedType>(types);
		}
	}
	public static final class Tuple extends SyntacticElement.Impl implements NonUnion {
		public final ArrayList<UnresolvedType> types;
		public Tuple(Collection<UnresolvedType> types, Attribute... attributes) {
			super(attributes);
			if(types.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.types = new ArrayList<UnresolvedType>(types);
		}
	}
	
	public abstract static class FunctionOrMethodOrMessage extends
			SyntacticElement.Impl implements NonUnion {
		public final UnresolvedType ret;
		public final UnresolvedType throwType;
		public final ArrayList<UnresolvedType> paramTypes;

		public FunctionOrMethodOrMessage(UnresolvedType ret, UnresolvedType throwType,
				Collection<UnresolvedType> paramTypes, Attribute... attributes) {
			super(attributes);
			this.ret = ret;
			this.throwType = throwType;
			this.paramTypes = new ArrayList<UnresolvedType>(paramTypes);
		}

		public FunctionOrMethodOrMessage(UnresolvedType ret, UnresolvedType throwType,
				Collection<UnresolvedType> paramTypes,
				Collection<Attribute> attributes) {
			super(attributes);
			this.ret = ret;
			this.throwType = throwType;
			this.paramTypes = new ArrayList<UnresolvedType>(paramTypes);
		}
	}

	public static class FunctionOrMethod extends FunctionOrMethodOrMessage
			implements NonUnion {
		public FunctionOrMethod(UnresolvedType ret, UnresolvedType throwType,
				Collection<UnresolvedType> paramTypes, Attribute... attributes) {
			super(ret, throwType, paramTypes, attributes);
		}

		public FunctionOrMethod(UnresolvedType ret, UnresolvedType throwType,
				Collection<UnresolvedType> paramTypes,
				Collection<Attribute> attributes) {
			super(ret, throwType, paramTypes, attributes);
		}
	}
	
	public static class Function extends FunctionOrMethod
	implements NonUnion {
		public Function(UnresolvedType ret, UnresolvedType throwType,
				Collection<UnresolvedType> paramTypes,
				Attribute... attributes) {
			super(ret,throwType,paramTypes,attributes);			
		}
		public Function(UnresolvedType ret, UnresolvedType throwType, Collection<UnresolvedType> paramTypes,
				Collection<Attribute> attributes) {
			super(ret,throwType,paramTypes,attributes);			
		}
	}
	
	public static class Method extends FunctionOrMethod
	implements NonUnion {
				public Method(UnresolvedType ret, UnresolvedType throwType, Collection<UnresolvedType> paramTypes,
				Attribute... attributes) {
			super(ret,throwType,paramTypes,attributes);			
		}
		public Method(UnresolvedType ret, UnresolvedType throwType, Collection<UnresolvedType> paramTypes,
				Collection<Attribute> attributes) {
			super(ret,throwType,paramTypes,attributes);			
		}
	}
		
	public static final class Message extends FunctionOrMethodOrMessage {
		public final UnresolvedType receiver;

		public Message(UnresolvedType receiver, UnresolvedType ret, UnresolvedType throwType,
				Collection<UnresolvedType> paramTypes, Attribute... attributes) {
			super(ret, throwType, paramTypes, attributes);
			this.receiver = receiver;
		}

		public Message(UnresolvedType receiver, UnresolvedType ret, UnresolvedType throwType,
				Collection<UnresolvedType> paramTypes,
				Collection<Attribute> attributes) {
			super(ret, throwType, paramTypes, attributes);
			this.receiver = receiver;
		}
	}
}

