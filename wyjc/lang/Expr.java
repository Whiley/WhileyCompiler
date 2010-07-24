package wyjc.lang;

import java.util.*;

import wyil.lang.*;
import wyil.util.Pair;

public interface Expr extends SyntacticElement {

	/**
	 * Substitute all matching variables with a given expression. Care must be
	 * taken when doing this to avoid substituting capture variables (i.e. as
	 * found in comprehensions).
	 * 
	 * @param binding
	 * @return
	 */
	public Expr substitute(Map<String,Expr> binding);
	
	public interface LVal {}
	
	public static class Variable extends SyntacticElement.Impl implements Expr, LVal {
		public final String var;

		public Variable(String var, Attribute... attributes) {
			super(attributes);
			this.var = var;
		}

		public Variable(String var, Collection<Attribute> attributes) {
			super(attributes);
			this.var = var;
		}

		public Expr substitute(Map<String,Expr> binding) {
			Expr e = binding.get(var);
			if(e != null) {
				return e;
			} else {
				return this;
			}
		}
		
		public String toString() {
			return var;
		}
	}
	
	public static class NamedConstant extends Variable {
		public final ModuleID mid;

		public NamedConstant(String var, ModuleID mid, Attribute... attributes) {
			super(var, attributes);
			this.mid = mid;
		}

		public NamedConstant(String var, ModuleID mid,
				Collection<Attribute> attributes) {
			super(var, attributes);
			this.mid = mid;
		}
	}

	public static class Constant extends SyntacticElement.Impl implements Expr {
		public final Value val;

		public Constant(Value val, Attribute... attributes) {
			super(attributes);
			this.val = val;
		}

		public Constant(Value val, Collection<Attribute> attributes) {
			super(attributes);
			this.val = val;
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			return this;
		}
	}

	public static class TypeConst extends SyntacticElement.Impl implements Expr {
		public final Type type;

		public TypeConst(Type val, Attribute... attributes) {
			super(attributes);
			this.type = val;
		}

		public TypeConst(Type val, Collection<Attribute> attributes) {
			super(attributes);
			this.type = val;
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			return this;
		}
	}
	
	public enum BOp { 
		AND,
		OR,
		ADD,
		SUB,
		MUL,
		DIV,		
		UNION,
		INTERSECTION,
		EQ,
		NEQ,
		LT,
		LTEQ,
		GT,
		GTEQ,
		SUBSET,
		SUBSETEQ,
		ELEMENTOF,
		LISTACCESS,
		LISTRANGE,
		TYPEEQ,
		TYPEIMPLIES
	};
		
	public static class BinOp extends SyntacticElement.Impl implements Expr {
		public final BOp op;
		public final Expr lhs;
		public final Expr rhs;
		
		public BinOp(BOp op, Expr lhs, Expr rhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public BinOp(BOp op, Expr lhs, Expr rhs,
				Collection<Attribute> attributes) {
			super(attributes);
			this.op = op;
			this.lhs = lhs;
			this.rhs = rhs;
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr l = lhs.substitute(binding);
			Expr r = rhs.substitute(binding);
			return new BinOp(op,l,r,attributes());
		}
	}


	public enum UOp {
		NOT,
		NEG,
		LENGTHOF,
		PROCESSACCESS		
	}
	
	public static class UnOp extends SyntacticElement.Impl implements Expr {
		public final UOp op;
		public final Expr mhs;		
		
		public UnOp(UOp op, Expr mhs, Attribute... attributes) {
			super(attributes);
			this.op = op;
			this.mhs = mhs;			
		}

		public UnOp(UOp op, Expr mhs,
				Collection<Attribute> attributes) {
			super(attributes);
			this.op = op;
			this.mhs = mhs;			
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr l = mhs.substitute(binding);			
			return new UnOp(op,l,attributes());
		}
	}
	
	public static class NaryOp extends SyntacticElement.Impl implements Expr {
		public final NOp nop;
		public final ArrayList<Expr> arguments;
		public NaryOp(NOp nop, Collection<Expr> arguments, Attribute... attributes) {
			super(attributes);
			this.nop = nop;
			this.arguments = new ArrayList<Expr>(arguments);
		}
		public NaryOp(NOp nop, Attribute attribute, Expr... arguments) {
			super(attribute);
			this.nop = nop;
			this.arguments = new ArrayList<Expr>();
			for(Expr a : arguments) {
				this.arguments.add(a);
			}
		}
		public NaryOp(NOp nop, Collection<Expr> arguments, Collection<Attribute> attributes) {
			super(attributes);
			this.nop = nop;
			this.arguments = new ArrayList<Expr>(arguments);
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			ArrayList<Expr> args = new ArrayList<Expr>();
			for(Expr e : arguments) {
				args.add(e.substitute(binding));
			}
			return new NaryOp(nop,args,attributes());
		}
	}
	
	public enum NOp {
		SETGEN,
		LISTGEN,
		SUBLIST					
	}
	
	public static class Comprehension extends SyntacticElement.Impl implements Expr {
		public final COp cop;
		public final Expr value;
		public final ArrayList<Pair<String,Expr>> sources;
		public final Expr condition;
		
		public Comprehension(COp cop, Expr value,
				Collection<Pair<String, Expr>> sources, Expr condition,
				Attribute... attributes) {
			super(attributes);
			this.cop = cop;
			this.value = value;
			this.condition = condition;
			this.sources = new ArrayList<Pair<String, Expr>>(sources);
		}

		public Comprehension(COp cop, Expr value,
				Collection<Pair<String, Expr>> sources, Expr condition,
				Collection<Attribute> attributes) {
			super(attributes);
			this.cop = cop;
			this.value = value;
			this.condition = condition;
			this.sources = new ArrayList<Pair<String,Expr>>(sources);
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			ArrayList<Pair<String,Expr>> srcs = new ArrayList<Pair<String,Expr>>();
			for(Pair<String,Expr> p : sources) {
				Expr e = p.second().substitute(binding);
				srcs.add(new Pair<String, Expr>(p.first(), e
						.substitute(binding)));
			}
			Expr v = value == null ? null : value.substitute(binding);
			Expr c = condition.substitute(binding);
			return new Comprehension(cop,v,srcs,c,attributes());
		}
	}
	
	public enum COp {
		SETCOMP,
		LISTCOMP,
		NONE, // implies value == null					
		SOME, // implies value == null
	}
	
	public static class TupleAccess extends SyntacticElement.Impl implements
			Expr {
		public final Expr lhs;
		public final String name;

		public TupleAccess(Expr lhs, String name, Attribute... attributes) {
			super(attributes);
			this.lhs = lhs;
			this.name = name;
		}

		public TupleAccess(Expr lhs, String name,
				Collection<Attribute> attributes) {
			super(attributes);
			this.lhs = lhs;
			this.name = name;
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr l = lhs.substitute(binding);			
			return new TupleAccess(l,name,attributes());
		}
	}		

	public static class TupleGen extends SyntacticElement.Impl implements Expr {
		public final HashMap<String,Expr> fields;		
		
		public TupleGen(Map<String, Expr> fields, Attribute... attributes) {
			super(attributes);
			this.fields = new HashMap<String, Expr>(fields);
		}
		
		public TupleGen(Map<String, Expr> fields, Collection<Attribute> attributes) {
			super(attributes);
			this.fields = new HashMap<String, Expr>(fields);
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			HashMap<String,Expr> fs = new HashMap<String,Expr>();
			for(Map.Entry<String,Expr> f : fields.entrySet()) {
				fs.put(f.getKey(),f.getValue().substitute(binding));
			}
			return new TupleGen(fields,attributes());
		}
	}
	
	public static class Invoke extends SyntacticElement.Impl implements Expr,Stmt {
		public final String name;
		public final Expr receiver;
		public final List<Expr> arguments;
		
		public Invoke(String name, Expr receiver, List<Expr> arguments,
				Attribute... attributes) {
			super(attributes);
			this.name = name;
			this.receiver = receiver;
			this.arguments = arguments;
		}
		
		public Invoke(String name, Expr receiver, List<Expr> arguments,
				Collection<Attribute> attributes) {
			super(attributes);
			this.name = name;
			this.receiver = receiver;
			this.arguments = arguments;
		}
		
		public Expr substitute(Map<String, Expr> binding) {
			ArrayList<Expr> args = new ArrayList<Expr>();
			for (Expr e : arguments) {
				args.add(e.substitute(binding));
			}
			Expr rec = receiver == null ? null : receiver.substitute(binding);
			return new Invoke(name, rec, args, attributes());
		}
	}
	
	public static class Spawn extends SyntacticElement.Impl implements Expr,Stmt {		
		// Spawn cannot be an UnOp since it needs to be a stmt and an expr
		public final Expr mhs;		
		
		public Spawn(Expr mhs, Attribute... attributes) {
			super(attributes);			
			this.mhs = mhs;			
		}

		public Spawn(Expr mhs,
				Collection<Attribute> attributes) {
			super(attributes);			
			this.mhs = mhs;			
		}
		
		public Expr substitute(Map<String,Expr> binding) {
			Expr l = mhs.substitute(binding);			
			return new Spawn(l,attributes());
		}
	}
}
