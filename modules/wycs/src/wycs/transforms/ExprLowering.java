package wycs.transforms;

import static wybs.lang.SyntaxError.internalFailure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import wybs.lang.Builder;
import wybs.lang.Transform;
import wycs.WycsBuilder;
import wycs.core.SemanticType;
import wycs.syntax.Expr;
import wycs.syntax.TypeAttribute;
import wycs.syntax.WycsFile;

/**
 * Responsible for transforming Wycs expressions into a lower-level form.
 * 
 * @author David J. Pearce
 * 
 */
public class ExprLowering implements Transform<WycsFile> {
	
	/**
	 * Determines whether type propagation is enabled or not.
	 */
	private boolean enabled = getEnable();

	private final WycsBuilder builder;
	
	private String filename;

	// ======================================================================
	// Constructor(s)
	// ======================================================================

	public ExprLowering(Builder builder) {
		this.builder = (WycsBuilder) builder;
	}
	
	// ======================================================================
	// Configuration Methods
	// ======================================================================
		
	public static String describeEnable() {
		return "Enable/disable type propagation";
	}

	public static boolean getEnable() {
		return true; // default value
	}

	public void setEnable(boolean flag) {
		this.enabled = flag;
	}

	// ======================================================================
	// Apply method
	// ======================================================================
		
	public void apply(WycsFile wf) {
		if(enabled) {
			this.filename = wf.filename();

			for (WycsFile.Declaration s : wf.declarations()) {
				lower(s);
			}
		}
	}

	private void lower(WycsFile.Declaration s) {		
		if(s instanceof WycsFile.Function) {
			lower((WycsFile.Function)s);
		} else if(s instanceof WycsFile.Define) {
			lower((WycsFile.Define)s);
		} else if(s instanceof WycsFile.Assert) {
			lower((WycsFile.Assert)s);
		} else if(s instanceof WycsFile.Import) {
			
		} else {
			internalFailure("unknown statement encountered (" + s + ")",
					filename, s);
		}
	}
	
	private void lower(WycsFile.Function s) {
		if(s.constraint != null) {
			s.constraint = lower(s.constraint);
		}
	}
	
	private void lower(WycsFile.Define s) {
		s.condition = lower(s.condition);
	}

	private void lower(WycsFile.Assert s) {
		s.expr = lower(s.expr);
	}
	
	private Expr lower(Expr e) {
		if(e instanceof Expr.Variable) {
			return lower((Expr.Variable)e);
		} else if(e instanceof Expr.Constant) {
			return lower((Expr.Constant)e);
		} else if(e instanceof Expr.Unary) {
			return lower((Expr.Unary)e);
		} else if(e instanceof Expr.Binary) {
			return lower((Expr.Binary)e);
		} else if(e instanceof Expr.Nary) {
			return lower((Expr.Nary)e);
		} else if(e instanceof Expr.Quantifier) {
			return lower((Expr.Quantifier)e);
		} else if(e instanceof Expr.FunCall) {
			return lower((Expr.FunCall)e);
		} else if(e instanceof Expr.Load) {
			return lower((Expr.Load)e);
		} else if(e instanceof Expr.IndexOf) {
			return lower((Expr.IndexOf)e);
		} else {
			internalFailure("unknown expression encountered (" + e + ")",
					filename, e);
			return null;
		}
	}
	
	private Expr lower(Expr.Variable e) {
		return e;
	}
	
	private Expr lower(Expr.Constant e) {
		return e;
	}
	
	private Expr lower(Expr.Unary e) {
		Expr operand = lower(e.operand);
		if(operand != e.operand) {
			return Expr.Unary(e.op, operand, e.attributes()); 
		}
		return e;
	}

	private Expr lower(Expr.Binary e) {
		Expr lhs = lower(e.leftOperand);
		Expr rhs = lower(e.rightOperand);
		if (lhs != e.leftOperand || rhs != e.rightOperand) {
			return Expr.Binary(e.op, lhs, rhs, e.attributes());
		}
		return e;
	}
	
	private Expr lower(Expr.Nary e) {
		Expr[] operands = e.operands;
		for(int i=0;i!=operands.length;++i) {
			Expr o = operands[i];
			Expr n = lower(operands[i]);
			if(o != n && operands != e.operands) {
				operands = Arrays.copyOf(e.operands, operands.length);
			}
			operands[i]=n;
		}
		if(operands != e.operands) {
			return Expr.Nary(e.op,operands,e.attributes());
		} else {
			return e;
		}
	}
	
	private Expr lower(Expr.FunCall e) {
		Expr operand = lower(e.operand);
		if (operand != e.operand) {
			return Expr.FunCall(e.name, e.generics, operand, e.attributes());
		}
		return e;
	}
	
	private Expr lower(Expr.Quantifier e) {
		Expr operand = lower(e.operand);
		if (operand != e.operand) {
			if (e instanceof Expr.ForAll) {
				return Expr.ForAll(e.variables, operand, e.attributes());
			} else {
				return Expr.Exists(e.variables, operand, e.attributes());
			}
		}
		return e;
	}
	
	private Expr lower(Expr.Load e) {
		Expr operand = lower(e.operand);
		if (operand != e.operand) {
			return Expr.Load(operand, e.index, e.attributes());
		}
		return e;
	}
	
	private Expr lower(Expr.IndexOf e) {
		return null;
	}
}
