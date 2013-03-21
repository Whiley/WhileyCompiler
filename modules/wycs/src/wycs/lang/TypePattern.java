package wycs.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import wybs.lang.SyntacticElement;
import wybs.lang.Attribute;

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
	
	/**
	 * The variable name associated with this type pattern. Maybe
	 * <code>null</code> if not declared variable.
	 */
	public String var;
	
	public TypePattern(String var, Attribute... attributes) {
		super(attributes);
		this.var = var;
	}
	
	public TypePattern(String var,  Collection<Attribute> attributes) {
		super(attributes);
		this.var = var;
	}
	
	public abstract SyntacticType toSyntacticType();
	
	public abstract TypePattern instantiate(Map<String,SyntacticType> binding);
	
	public static class Leaf extends TypePattern {
		public SyntacticType type;
		
		public Leaf(SyntacticType type, String var, Attribute... attributes) {
			super(var,attributes);
			this.type = type;
		}
		
		public Leaf(SyntacticType type, String var, Collection<Attribute> attributes) {
			super(var,attributes);
			this.type = type;			
		}
		
		@Override
		public TypePattern instantiate(Map<String,SyntacticType> binding) {
			if(type instanceof SyntacticType.Variable) {
				SyntacticType.Variable sl = (SyntacticType.Variable) type;
				SyntacticType st = binding.get(sl.var);
				if(st != null) {
					return new Leaf(st,var,attributes());
				}
			} 			
			return this;
		}
		
		@Override
		public SyntacticType toSyntacticType() {
			return type;
		}
		
		public String toString() {
			if(var == null) {
				return type.toString();
			}
			String r = "(" + type;
			if(var != null) {
				r += " " + var;
			}			
			return r + ")";			
		}
	}
	
	public static class Tuple extends TypePattern {
		public TypePattern[] patterns;
		
		public Tuple(TypePattern[] patterns, String var,
				Attribute... attributes) {
			super(var, attributes);
			this.patterns = patterns;
		}

		public Tuple(TypePattern[] patterns, String var,
				Collection<Attribute> attributes) {
			super(var, attributes);
			this.patterns = patterns;
		}
		
		@Override
		public TypePattern instantiate(Map<String, SyntacticType> binding) {
			TypePattern[] types = new TypePattern[patterns.length];
			for (int i = 0; i != types.length; ++i) {
				types[i] = patterns[i].instantiate(binding);
			}
			return new TypePattern.Tuple(types, var, attributes());
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
