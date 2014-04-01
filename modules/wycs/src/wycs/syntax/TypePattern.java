package wycs.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import wycc.lang.Attribute;
import wycc.lang.SyntacticElement;

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
	
	/**
	 * The constraint associated with this type pattern. Maybe <code>null</code>
	 * if there is no constraint.
	 */
	public Expr constraint;

	/**
	 * The source set associated with this type pattern. Maybe <code>null</code>
	 * if there is no source set
	 */
	public Expr source;
	
	public TypePattern(String var, Expr source, Expr constraint,
			Attribute... attributes) {
		super(attributes);
		this.var = var;
		this.source = source;
		this.constraint = constraint;
	}

	public TypePattern(String var, Expr source, Expr constraint,
			Collection<Attribute> attributes) {
		super(attributes);
		this.var = var;
		this.source = source;
		this.constraint = constraint;
	}
	
	public abstract SyntacticType toSyntacticType();
	
	public abstract TypePattern instantiate(Map<String,SyntacticType> binding);
	
	public abstract TypePattern substitute(Map<String,Expr> binding);
	
	public static class Leaf extends TypePattern {
		public SyntacticType type;
		
		public Leaf(SyntacticType type, String var, Expr source,
				Expr constraint, Attribute... attributes) {
			super(var,source,constraint,attributes);
			this.type = type;		
		}
		
		public Leaf(SyntacticType type, String var, Expr source,
				Expr constraint, Collection<Attribute> attributes) {
			super(var,source,constraint,attributes);
			this.type = type;
		}
		
		@Override
		public TypePattern substitute(Map<String, Expr> binding) {
			Expr src = source;
			Expr con = constraint;
			if(src != null) {
				src = src.substitute(binding);
			}
			if(con != null) {
				con = con.substitute(binding);
			}
			if(src != source || con != constraint) {
				return new TypePattern.Leaf(type, var, src, con, attributes());
			} else {
				return this;
			}
		}

		@Override
		public TypePattern instantiate(Map<String, SyntacticType> binding) {
			SyntacticType t = type.instantiate(binding);
			Expr src = source;
			Expr con = constraint;
			if (src != null) {
				src = src.instantiate(binding);
			}
			if (con != null) {
				con = con.instantiate(binding);
			}
			if (t != type || src != source || con != constraint) {
				return new Leaf(t, var, src, con, attributes());
			}
			return this;
		}
		
		@Override
		public SyntacticType toSyntacticType() {
			return type;
		}
		
		public String toString() {
			String r = "(" + type;
			if(var != null) {
				r += " " + var;
			}
			if(source != null) {
				r += " in " + source;
			}
			if(constraint != null) {
				r += " where " + constraint;
			}
			return r + ")";			
		}
	}
	
	public static class Tuple extends TypePattern {
		public TypePattern[] patterns;
		
		public Tuple(TypePattern[] patterns, String var, Expr source,
				Expr constraint,  Attribute... attributes) {
			super(var, source, constraint, attributes);
			this.patterns = patterns;
		}

		public Tuple(TypePattern[] patterns, String var, Expr source,
				Expr constraint, Collection<Attribute> attributes) {
			super(var, source, constraint, attributes);
			this.patterns = patterns;
		}
		
		@Override
		public TypePattern substitute(Map<String, Expr> binding) {
			TypePattern[] types = new TypePattern[patterns.length];
			for (int i = 0; i != types.length; ++i) {
				types[i] = patterns[i].substitute(binding);
			}
			Expr src = source;
			Expr con = constraint;
			if(src != null) {
				src = src.substitute(binding);
			}
			if(con != null) {
				con = con.substitute(binding);
			}
			// FIXME: could make this more efficient by not always creating a
			// new types array.
			return new TypePattern.Tuple(types, var, src, con, attributes());			
		}
		
		@Override
		public TypePattern instantiate(Map<String, SyntacticType> binding) {
			TypePattern[] types = new TypePattern[patterns.length];
			for (int i = 0; i != types.length; ++i) {
				types[i] = patterns[i].instantiate(binding);
			}
			Expr src = source;
			Expr con = constraint;
			if(src != null) {
				src = src.instantiate(binding);
			}
			if(con != null) {
				con = con.instantiate(binding);
			}
			// FIXME: could make this more efficient by not always creating a
			// new types array.
			return new TypePattern.Tuple(types, var, src, con, attributes());
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
			if(source != null) {
				r += " in " + source;
			}
			if(constraint != null) {
				r += " where " + constraint;
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
