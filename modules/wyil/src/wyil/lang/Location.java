package wyil.lang;

import java.util.Arrays;
import java.util.List;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;

/**
 * Represents a location use to hold a value of some kind. This location could
 * correspond to a local variable, or an intermediate operand value.
 * 
 * @author David J. Pearce
 *
 */
public abstract class Location extends SyntacticElement.Impl {
	private final WyilFile.Declaration parent;
	
	public Location(WyilFile.Declaration parent, List<Attribute> attributes) {
		super(attributes);
		this.parent = parent;
	}
	
	public Location(WyilFile.Declaration parent, Attribute... attributes) {
		super(attributes);
		this.parent = parent;
	}
	
	public abstract int size();

	public abstract Type type(int i);
	
	/**
	 * Represents the result of an intermediate computation which is assigned to
	 * an anonymous location.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Operand extends Location {
		private final Type[] types;
		private final Bytecode.Expr value;		
				
		public Operand(Type type, Bytecode.Expr value, WyilFile.Declaration parent, List<Attribute> attributes) {
			super(parent,attributes);
			this.types = new Type[]{type};
			this.value = value;
		}
		
		public Operand(Type[] types, Bytecode.Expr value, WyilFile.Declaration parent, List<Attribute> attributes) {
			super(parent,attributes);
			this.types = types;
			this.value = value;
		}
		
		public Bytecode.Expr value() {
			return value;
		}
		
		public int size() {
			return types.length;
		}
		
		public Type type(int i) {
			return types[i];
		}
		
		public String toString() {
			return Arrays.toString(types) + " " + value;
		}
	}
	
	/**
	 * Represents the declaration information associated with a given named location (i.e. variable).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Variable extends Location {
		private final Type type;
		private final String name;

		public Variable(Type type, String name, WyilFile.Declaration parent, List<Attribute> attributes) {
			super(parent,attributes);
			this.type = type;
			this.name = name;
		}

		public String name() {
			return name;
		}
		
		public int size() {
			return 1;
		}
		
		public Type type(int i) {
			if(i != 0) {
				throw new IllegalArgumentException("invalid type index");
			}
			return type;
		}
		
		public String toString() {
			return type + " " + name;
		}
	}	
}
