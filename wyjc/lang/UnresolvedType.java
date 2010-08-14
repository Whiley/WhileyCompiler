package wyjc.lang;

import java.util.*;

public interface UnresolvedType extends SyntacticElement {

	public interface NonUnion extends UnresolvedType {
	}
	
	public static final class Any extends SyntacticElement.Impl implements NonUnion {
		public Any(Attributes... attributes) {
			super(attributes);
		}		
	}
	public static final class Void extends SyntacticElement.Impl implements NonUnion {
		public Void(Attributes... attributes) {
			super(attributes);
		}		
	}

	public static final class Existential extends SyntacticElement.Impl
			implements NonUnion {
		public Existential(Attributes... attributes) {
			super(attributes);
		}
	}
	public static final class Bool extends SyntacticElement.Impl implements NonUnion {
		public Bool(Attributes... attributes) {
			super(attributes);
		}		
	}
	public static final class Int extends SyntacticElement.Impl implements NonUnion {
		public Int(Attributes... attributes) {
			super(attributes);
		}		
	}
	public static final class Real extends SyntacticElement.Impl implements NonUnion {
		public Real(Attributes... attributes) {
			super(attributes);
		}		
	}
	public static final class Named extends SyntacticElement.Impl implements NonUnion {		
		public final String name;		
		public Named(String name, Attributes... attributes) {
			super(attributes);
			this.name = name;
		}		
	}
	public static final class List extends SyntacticElement.Impl implements NonUnion {
		public final UnresolvedType element;
		public List(UnresolvedType element, Attributes... attributes) {
			super(attributes);
			this.element = element;			
		}
	}
	public static final class Set extends SyntacticElement.Impl implements NonUnion {
		public final UnresolvedType element;
		public Set(UnresolvedType element, Attributes... attributes) {
			super(attributes);
			this.element = element;
		}
	}
	public static final class Union extends SyntacticElement.Impl implements UnresolvedType {
		public final ArrayList<NonUnion> bounds;

		public Union(Collection<NonUnion> bounds, Attributes... attributes) {
			if (bounds.size() < 2) {
				new IllegalArgumentException(
						"Cannot construct a type union with fewer than two bounds");
			}
			this.bounds = new ArrayList<NonUnion>(bounds);
		}	
	}
	
	public static final class Process extends SyntacticElement.Impl implements NonUnion {
		public final UnresolvedType element;
		public Process(UnresolvedType element, Attributes... attributes) {
			this.element = element;
		}
	}
	public static final class Tuple extends SyntacticElement.Impl implements NonUnion {
		public final HashMap<String,UnresolvedType> types;
		public Tuple(Map<String,UnresolvedType> types, Attributes... attributes) {
			super(attributes);
			if(types.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.types = new HashMap<String,UnresolvedType>(types);
		}
	}
}
