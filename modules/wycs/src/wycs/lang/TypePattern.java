package wycs.lang;

import java.util.ArrayList;
import java.util.Collection;

import wybs.lang.SyntacticElement;
import wybs.lang.Attribute;

/**
 * Represents a type pattern which is used for pattern matching.
 * 
 * @author djp
 * 
 */
public abstract class TypePattern extends SyntacticElement.Impl {
	
	/**
	 * The variable name associated with this type pattern. Maybe
	 * <code>null</code> if not declared variable.
	 */
	public String var;
	
	/**
	 * An optional (i.e. maybe null) constraint which may be associated with the
	 * type.
	 */
	public Expr constraint;
	
	public TypePattern(String var, Expr constraint, Attribute... attributes) {
		super(attributes);
		this.var = var;
		this.constraint = constraint;
	}
	
	public TypePattern(String var,  Expr constraint, Collection<Attribute> attributes) {
		super(attributes);
		this.var = var;
		this.constraint = constraint;
	}
	
	public abstract SyntacticType toSyntacticType();
	
	public static class Leaf extends TypePattern {
		public SyntacticType type;
		
		public Leaf(SyntacticType type, String var, Expr constraint, Attribute... attributes) {
			super(var,constraint,attributes);
			this.type = type;
		}
		
		public Leaf(SyntacticType type, String var, Expr constraint, Collection<Attribute> attributes) {
			super(var,constraint,attributes);
			this.type = type;			
		}
		
		@Override
		public SyntacticType toSyntacticType() {
			return type;
		}
		
		public String toString() {
			if(var != null) {
				return type + " " + var;
			} else {
				return type.toString();
			}
		}
	}
	
	public static class Tuple extends TypePattern {
		public TypePattern[] patterns;
		
		public Tuple(TypePattern[] patterns, String var, Expr constraint,
				Attribute... attributes) {
			super(var, constraint, attributes);
			this.patterns = patterns;
		}

		public Tuple(TypePattern[] patterns, String var, Expr constraint,
				Collection<Attribute> attributes) {
			super(var, constraint, attributes);
			this.patterns = patterns;
		}
		
		@Override
		public SyntacticType.Tuple toSyntacticType() {
			SyntacticType[] types = new SyntacticType[patterns.length];
			for (int i = 0; i != types.length; ++i) {
				types[i] = patterns[i].toSyntacticType();
			}
			return new SyntacticType.Tuple(types);
		}
		
		public String toString() {
			String r = "(";
			for(int i=0;i!=patterns.length;++i) {
				if(i!=0) {
					r = r + ", ";
				}
				r = r + patterns[i];
			}
			r = r + ")";
			if(var != null) {
				return r + " " + var;
			} else {
				return r;
			}
		}
	}
}
