package wycs.transforms;

import java.util.ArrayList;
import java.util.HashMap;

import static wybs.lang.SyntaxError.*;
import wybs.lang.Attribute;
import wycs.lang.*;

public class ConstraintInline {
	private String filename;
	private HashMap<String, Stmt.Function> fnEnvironment;
	
	public void transform(WycsFile wf) {
		fnEnvironment = new HashMap<String,Stmt.Function>();
		
		this.filename = wf.filename();
		for(Stmt s : wf.stmts()) {
			transform(s);
		}
	}
	
	private void transform(Stmt s) {
		if(s instanceof Stmt.Function) {
			Stmt.Function sf = (Stmt.Function) s;
			transform(sf);
			fnEnvironment.put(sf.name, sf);
		} else if(s instanceof Stmt.Assert) {
			transform((Stmt.Assert)s);
		} else {
			internalFailure("unknown declaration encountered (" + s + ")",
					filename, s);
		}
	}
	
	private void transform(Stmt.Function s) {
		s.condition = transformCondition(s.condition);
	}
	
	private void transform(Stmt.Assert s) {
		s.expr = transformCondition(s.expr);
	}
	
	private Expr transformCondition(Expr e) {
		if (e instanceof Expr.Variable || e instanceof Expr.Constant) {
			// do nothing
			return e;
		} else if (e instanceof Expr.Unary) {
			return transformCondition((Expr.Unary)e);
		} else if (e instanceof Expr.Binary) {
			return transformCondition((Expr.Binary)e);
		} else if (e instanceof Expr.Nary) {
			return transformCondition((Expr.Nary)e);
		} else if (e instanceof Expr.Quantifier) {
			return transformCondition((Expr.Quantifier)e);
		} else if (e instanceof Expr.FunCall) {
			return transformCondition((Expr.FunCall)e);
		} else {
			internalFailure("invalid boolean expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Expr transformCondition(Expr.Unary e) {
		switch(e.op) {
		case NOT:
			e.operand = transformCondition(e.operand);
			return e;
		default:
			internalFailure("invalid boolean expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Expr transformCondition(Expr.Binary e) {
		switch (e.op) {
		case EQ:
		case NEQ:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case IN:
		case SUBSET:
		case SUBSETEQ:
		case SUPSET:
		case SUPSETEQ: {
			ArrayList<Expr> assumptions = new ArrayList<Expr>();
			transformExpression(e, assumptions);
			if (assumptions.size() > 0) {
				Expr lhs = Expr.Nary(Expr.Nary.Op.AND, assumptions,
						e.attribute(Attribute.Source.class));				
				return Expr.Binary(Expr.Binary.Op.IMPLIES, lhs,e,
						e.attribute(Attribute.Source.class));
			} else {
				return e;
			}
		}
		case IMPLIES:
		case IFF: {
			e.leftOperand = transformCondition(e.leftOperand);
			e.rightOperand = transformCondition(e.rightOperand);
			return e;
		}
		default:
			internalFailure("invalid boolean expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Expr transformCondition(Expr.Nary e) {
		switch(e.op) {
		case AND:
		case OR: {
			Expr[] e_operands = e.operands;
			for(int i=0;i!=e_operands.length;++i) {
				e_operands[i] = transformCondition(e_operands[i]);
			}
			return e;
		}		
		default:
			internalFailure("invalid boolean expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Expr transformCondition(Expr.Quantifier e) {
		e.operand = transformCondition(e.operand);
		return e;
	}
	
	private Expr transformCondition(Expr.FunCall e) {
		// this must be a predicate
		Stmt.Function f = fnEnvironment.get(e.name);
		if(f instanceof Stmt.Predicate) { 
			if(f.condition == null) {
				internalFailure("predicate defined without a condition?",filename,e);
			}
			HashMap<String,Expr> binding = new HashMap<String,Expr>();
			bind(e.operand,f.from,binding);
			return substitute(f.condition,binding);
		} else if(f.condition != null) {
			HashMap<String,Expr> binding = new HashMap<String,Expr>();
			bind(e.operand,f.from,binding);
			// TODO: make this more general?
			bind(e,f.to,binding);	
			return Expr.Nary(Expr.Nary.Op.AND, new Expr[]{e,substitute(f.condition,binding)}, e.attribute(Attribute.Source.class));			
		} else {
			return e;
		}
	}
	
	private void transformExpression(Expr e, ArrayList<Expr> constraints) {
		if (e instanceof Expr.Variable || e instanceof Expr.Constant) {
			// do nothing
		} else if (e instanceof Expr.Unary) {
			transformExpression((Expr.Unary)e,constraints);
		} else if (e instanceof Expr.Binary) {
			transformExpression((Expr.Binary)e,constraints);
		} else if (e instanceof Expr.Nary) {
			transformExpression((Expr.Nary)e,constraints);
		} else if (e instanceof Expr.FunCall) {
			transformExpression((Expr.FunCall)e,constraints);
		} else {
			internalFailure("invalid expression encountered (" + e
					+ ")", filename, e);
		}
	}
	
	private void transformExpression(Expr.Unary e, ArrayList<Expr> constraints) {
		switch (e.op) {
		case NOT:
		case NEG:
		case LENGTHOF:
			transformExpression(e.operand,constraints);
			break;
		default:
			internalFailure("invalid unary expression encountered (" + e
					+ ")", filename, e);			
		}
	}
	
	private void transformExpression(Expr.Binary e, ArrayList<Expr> constraints) {
		switch (e.op) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case REM:
		case EQ:
		case NEQ:
		case IMPLIES:
		case IFF:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case IN:
		case SUBSET:
		case SUBSETEQ:
		case SUPSET:
		case SUPSETEQ:
			transformExpression(e.leftOperand,constraints);
			transformExpression(e.rightOperand,constraints);
			break;
		default:
			internalFailure("invalid binary expression encountered (" + e
					+ ")", filename, e);			
		}
	}
	
	private void transformExpression(Expr.Nary e, ArrayList<Expr> constraints) {
		switch(e.op) {
		case AND:
		case OR:
		case SET:
		case TUPLE: {
			Expr[] e_operands = e.operands;
			for(int i=0;i!=e_operands.length;++i) {
				transformExpression(e_operands[i],constraints);
			}
			break;
		}				
		default:
			internalFailure("invalid nary expression encountered (" + e
					+ ")", filename, e);
		}
	}
	
	private void transformExpression(Expr.FunCall e, ArrayList<Expr> constraints) {
		Stmt.Function f = fnEnvironment.get(e.name);
		if(f.condition != null) {
			HashMap<String,Expr> binding = new HashMap<String,Expr>();
			bind(e.operand,f.from,binding);
			// TODO: make this more general?
			bind(e,f.to,binding);	
			constraints.add(substitute(f.condition,binding));
		}
	}
	
	private void bind(Expr operand, SyntacticType t, HashMap<String,Expr> binding) {
		if(t.name != null) {
			binding.put(t.name,operand);
		}
		if (t instanceof SyntacticType.Tuple && operand instanceof Expr.Nary) {
			SyntacticType.Tuple tt = (SyntacticType.Tuple)t;
			Expr.Nary tc = (Expr.Nary) operand;
			if(tt.elements.size() != tc.operands.length || tc.op != Expr.Nary.Op.TUPLE) {
				internalFailure("cannot bind function call to declaration", filename, operand);
			}
			ArrayList<SyntacticType> parameters = tt.elements;
			Expr[] arguments = tc.operands;
			for(int i=0;i!=arguments.length;++i) {
				bind(arguments[i],parameters.get(i),binding);
			}
		}		
	}
	
	private Expr substitute(Expr e, HashMap<String,Expr> binding) {		
		if (e instanceof Expr.Constant) {
			// do nothing		
			return e;
		} else if (e instanceof Expr.Variable) {
			return substitute((Expr.Variable)e,binding);
		} else if (e instanceof Expr.Unary) {
			return substitute((Expr.Unary)e,binding);
		} else if (e instanceof Expr.Binary) {
			return substitute((Expr.Binary)e,binding);
		} else if (e instanceof Expr.Nary) {
			return substitute((Expr.Nary)e,binding);
		} else if (e instanceof Expr.FunCall) {
			return substitute((Expr.FunCall)e,binding);
		} else if (e instanceof Expr.Quantifier) {
			return substitute((Expr.Quantifier)e,binding);
		} else {
			internalFailure("invalid expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Expr substitute(Expr.Variable e, HashMap<String,Expr> binding) {
		Expr r = binding.get(e.name);
		if(r != null) {
			// FIXME: should clone here!!!
			return r;
		} else {
			return e;
		}
	}
	
	private Expr substitute(Expr.Unary e, HashMap<String,Expr> binding) {
		switch (e.op) {
		case NOT:
		case NEG:
		case LENGTHOF:
			Expr expr = substitute(e.operand,binding);
			return Expr.Unary(e.op, expr, e.attributes());
		default:
			internalFailure("invalid unary expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Expr substitute(Expr.Binary e, HashMap<String,Expr> binding) {
		switch (e.op) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case REM:
		case EQ:
		case NEQ:
		case IMPLIES:
		case IFF:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case IN:
		case SUBSET:
		case SUBSETEQ:
		case SUPSET:
		case SUPSETEQ:		
			Expr lhs = substitute(e.leftOperand,binding);
			Expr rhs = substitute(e.rightOperand,binding);
			return Expr.Binary(e.op, lhs, rhs, e.attributes());
		default:
			internalFailure("invalid binary expression encountered (" + e
					+ ")", filename, e);			
			return null;
		}				
	}
	
	private Expr substitute(Expr.Nary e, HashMap<String,Expr> binding) {
		switch(e.op) {
		case AND:
		case OR:
		case SET:
		case TUPLE: {
			Expr[] e_operands = e.operands;
			Expr[] r_operands = new Expr[e_operands.length];
			for(int i=0;i!=e_operands.length;++i) {
				r_operands[i] = substitute(e_operands[i],binding);
			}
			return Expr.Nary(e.op, r_operands, e.attributes());
		}				
		default:
			internalFailure("invalid nary expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Expr substitute(Expr.FunCall e, HashMap<String,Expr> binding) {
		Expr operand = substitute(e.operand,binding);		
		return Expr.FunCall(e.name, e.generics, operand, e.attributes());
	}
	
	private Expr substitute(Expr.Quantifier e, HashMap<String,Expr> binding) {
		Expr operand = substitute(e.operand,binding);		
		
		// FIXME: there is a potential problem here for variable capture.
		
		if(e instanceof Expr.ForAll) {
			return Expr.ForAll(e.vars,operand,e.attributes());
		} else {
			return Expr.Exists(e.vars,operand,e.attributes());
		}
	}
}
