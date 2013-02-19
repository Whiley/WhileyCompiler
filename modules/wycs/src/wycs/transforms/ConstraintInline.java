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
		case IMPLIES:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case IN:
		case SUBSET:
		case SUBSETEQ:
		case SUPSET:
		case SUPSETEQ:
			ArrayList<Expr> constraints = new ArrayList<Expr>();
			transformExpression(e, constraints);
			if (constraints.size() > 0) {
				constraints.add(e);
				return Expr.Nary(Expr.Nary.Op.AND, constraints,
						e.attribute(Attribute.Source.class));
			} else {
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
		e.expr = transformCondition(e.expr);
		return e;
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
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case IN:
		case SUBSET:
		case SUBSETEQ:
		case SUPSET:
		case SUPSETEQ:
		case INDEXOF:
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
		
	}
}
