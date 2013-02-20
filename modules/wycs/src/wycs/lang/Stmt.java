package wycs.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wybs.lang.Attribute;
import wybs.lang.SyntacticElement;
import wybs.util.Pair;

public abstract class Stmt extends SyntacticElement.Impl implements SyntacticElement {
	
	public Stmt(Attribute... attributes) {
		super(attributes);
	}
	
	public Stmt(Collection<Attribute> attributes) {
		super(attributes);
	}

	// ==================================================================
	// Constructors
	// ==================================================================

	public static Assert Assert(String message, Expr expr, Attribute... attributes) {
		return new Assert(message,expr,attributes);
	}
	
	public static Assert Assert(String message, Expr expr, Collection<Attribute> attributes) {
		return new Assert(message,expr,attributes);
	}
	
	public static Function Function(String name, Collection<String> generics,
			SyntacticType from, SyntacticType to, Expr expr,
			Attribute... attributes) {
		return new Function(name, generics, from, to, expr, attributes);
	}
	
	public static Predicate Predicate(String name, Collection<String> generics,
			SyntacticType parameter, Expr expr,
			Attribute... attributes) {
		return new Predicate(name, generics, parameter, expr, attributes);
	}
	
	// ==================================================================
	// Classes
	// ==================================================================

	public static class Assert extends Stmt {
		public final String message;
		public Expr expr;
		
		private Assert(String message, Expr expr, Attribute... attributes) {
			super(attributes);
			this.message = message;
			this.expr = expr;
		}
		
		private Assert(String message, Expr expr, Collection<Attribute> attributes) {
			super(attributes);
			this.message = message;
			this.expr = expr;
		}
		
		public String toString() {
			if(message == null) {
				return "assert " + expr;	
			} else {
				return "assert " + expr + ", \"" + message + "\"";
			}
			
		}
	}	
	
	public static class Function extends Stmt {
		public final String name;
		public final ArrayList<String> generics;
		public final SyntacticType from;
		public final SyntacticType to;
		public Expr condition;

		public Function(String name, Collection<String> generics, SyntacticType from, SyntacticType to,
				Expr condition, Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.generics = new ArrayList<String>(generics);
			this.from = from;
			this.to = to;
			this.condition = condition;
		}
		
		public String toString() {
			String gens = "";
			if (generics.size() > 0) {
				gens += "<";
				for (int i = 0; i != generics.size(); ++i) {
					if (i != 0) {
						gens = gens + ", ";
					}
					gens = gens + generics.get(i);
				}
				gens += "> ";
			}

			String from = this.from.toString();
			String to = this.to.toString();
			String condition = this.condition != null ? " where "
					+ this.condition : "";

			if (this instanceof Predicate) {
				return "predicate " + name + gens + from + condition;
			} else {
				return "function " + name + gens + from + " => " + to
						+ condition;
			}
		}
	}
	
	public static class Predicate extends Function {
		public Predicate(String name, Collection<String> generics, SyntacticType parameter, 
				Expr condition, Attribute... attributes) {
			super(name,generics,parameter,new SyntacticType.Primitive(null,SemanticType.Bool),condition,attributes);
		}
	}
}
