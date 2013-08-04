package wycs.transforms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static wybs.lang.SyntaxError.*;
import wybs.lang.Attribute;
import wybs.lang.Builder;
import wybs.lang.NameID;
import wybs.lang.Transform;
import wybs.util.Pair;
import wybs.util.ResolveError;
import wybs.util.Triple;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.Code;
import wycs.core.SemanticType;
import wycs.core.Value;
import wycs.core.WycsFile;
import wycs.core.SemanticType.Function;
import wycs.syntax.*;

public class AxiomInstantiation implements Transform<WycsFile> {
	
	/**
	 * Determines whether constraint inlining is enabled or not.
	 */
	private boolean enabled = getEnable();

	private final Wyal2WycsBuilder builder;
	
	private String filename;

	// ======================================================================
	// Constructor(s)
	// ======================================================================
	
	public AxiomInstantiation(Builder builder) {
		this.builder = (Wyal2WycsBuilder) builder;
	}
	
	// ======================================================================
	// Configuration Methods
	// ======================================================================

	public static String describeEnable() {
		return "Enable/disable constraint inlining";
	}

	public static boolean getEnable() {
		return true; // default value
	}

	public void setEnable(boolean flag) {
		this.enabled = flag;
	}

	// ======================================================================
	// Apply Method
	// ======================================================================

	public void apply(WycsFile wf) {
		if(enabled) {
			this.filename = wf.filename();
			for(WycsFile.Declaration s : wf.declarations()) {
				transform(s);
			}
		}
	}
	
	private void transform(WycsFile.Declaration s) {
		if(s instanceof WycsFile.Function) {
			WycsFile.Function sf = (WycsFile.Function) s;
			transform(sf);
		} else if(s instanceof WycsFile.Macro) {
			// We can ignore macros because they should have been inlined by the
			// time this transform is executed.
		} else if(s instanceof WycsFile.Assert) {
			transform((WycsFile.Assert)s);
		} else {
			internalFailure("unknown declaration encountered (" + s + ")",
					filename, s);
		}
	}
	
	private void transform(WycsFile.Function s) {
		if(s.constraint != null) {
			s.constraint = transformCondition(s.constraint);
		}
	}
	
	private void transform(WycsFile.Assert s) {
		s.condition = transformCondition(s.condition);
	}
	
	private Code transformCondition(Code e) {
		if (e instanceof Code.Variable || e instanceof Code.Constant) {
			// do nothing
			return e;
		} else if (e instanceof Code.Unary) {
			return transformCondition((Code.Unary)e);
		} else if (e instanceof Code.Binary) {
			return transformCondition((Code.Binary)e);
		} else if (e instanceof Code.Nary) {
			return transformCondition((Code.Nary)e);
		} else if (e instanceof Code.Quantifier) {
			return transformCondition((Code.Quantifier)e);
		} else if (e instanceof Code.FunCall) {
			return transformCondition((Code.FunCall)e);
		} else if (e instanceof Code.Load) {
			return transformCondition((Code.Load)e);
		} else {
			internalFailure("invalid boolean expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Code transformCondition(Code.Unary e) {
		switch(e.opcode) {
		case NOT:
			return Code.Unary(e.type, e.opcode,
					transformCondition(e.operands[0]), e.attributes());
		default:
			internalFailure("invalid boolean expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Code transformCondition(Code.Binary e) {
		switch (e.opcode) {
		case EQ:
		case NEQ:
		case LT:
		case LTEQ:
		case IN:
		case SUBSET:
		case SUBSETEQ: {
			ArrayList<Code> axioms = new ArrayList<Code>();
			transformExpression(e, axioms);
			return implies(axioms,e);			
		}
		default:
			internalFailure("invalid boolean expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Code transformCondition(Code.Nary e) {
		switch(e.opcode) {
		case AND:
		case OR: {
			Code[] e_operands = new Code[e.operands.length];
			for(int i=0;i!=e_operands.length;++i) {
				e_operands[i] = transformCondition(e.operands[i]);
			}
			return Code.Nary(e.type, e.opcode, e_operands, e.attributes());
		}		
		default:
			internalFailure("invalid boolean expression encountered (" + e
					+ ")", filename, e);
			return null;
		}
	}
	
	private Code transformCondition(Code.Quantifier e) {
		ArrayList<Code> axioms = new ArrayList<Code>();

		e = Code.Quantifier(e.type, e.opcode,
				transformCondition(e.operands[0]), e.types, e.attributes());	
				
		return implies(assumptions,e);					
	}
	
	private Code transformCondition(Code.FunCall e) {
		ArrayList<Code> axioms = new ArrayList<Code>();		
		try {
			WycsFile module = builder.getModule(e.nid.module());			
			// module should not be null if TypePropagation has already passed.
			Object d = module.declaration(e.nid.name());
			if(d instanceof WycsFile.Function) {
				WycsFile.Function fn = (WycsFile.Function) d;
				if(fn.constraint != null) {
					HashMap<String,SemanticType> generics = buildGenericBinding(fn.type.generics(),e.type.generics());
					HashMap<Integer,Code> binding = new HashMap<Integer,Code>();
					binding.put(1, e.operands[0]);
					binding.put(0, e);		
					axioms.add(fn.constraint.substitute(binding).instantiate(generics));
				}
			} else if(d instanceof WycsFile.Macro){
				// we can ignore macros, because they are inlined separately by
				// MacroExpansion.
			} else {
				internalFailure("cannot resolve as function or macro call",
						filename, e);
			}
		} catch(Exception ex) {
			internalFailure(ex.getMessage(), filename, e, ex);
		}		
		
		transformExpression(e.operands[0], axioms);
		return implies(axioms,e);		
	}
	
	private HashMap<String, SemanticType> buildGenericBinding(
			SemanticType[] from, SemanticType[] to) {
		HashMap<String, SemanticType> binding = new HashMap<String, SemanticType>();
		for (int i = 0; i != to.length; ++i) {
			SemanticType.Var v = (SemanticType.Var) from[i];
			binding.put(v.name(), to[i]);
		}
		return binding;
	}
	
	private Code transformCondition(Code.Load e) {
		return Code.Load(e.type, transformCondition(e.operands[0]), e.index,
				e.attributes());
	}
	
	private void transformExpression(Code e, ArrayList<Code> axioms) {
		if (e instanceof Code.Variable || e instanceof Code.Constant) {
			// do nothing
		} else if (e instanceof Code.Unary) {
			transformExpression((Code.Unary)e,axioms);
		} else if (e instanceof Code.Binary) {
			transformExpression((Code.Binary)e,axioms);
		} else if (e instanceof Code.Nary) {
			transformExpression((Code.Nary)e,axioms);
		} else if (e instanceof Code.Load) {
			transformExpression((Code.Load)e,axioms);
		} else if (e instanceof Code.FunCall) {
			transformExpression((Code.FunCall)e,axioms);
		} else {
			internalFailure("invalid expression encountered (" + e
					+ ", " + e.getClass().getName() + ")", filename, e);
		}
	}
	
	private void transformExpression(Code.Unary e, ArrayList<Code> axioms) {
		transformExpression(e.operands[0],axioms);
	}
	
	private void transformExpression(Code.Binary e, ArrayList<Code> axioms) {		
		transformExpression(e.operands[0],axioms);
		transformExpression(e.operands[1],axioms);
	}
	
	private void transformExpression(Code.Nary e, ArrayList<Code> axioms) {
		Code[] e_operands = e.operands;
		for(int i=0;i!=e_operands.length;++i) {
			transformExpression(e_operands[i],axioms);
		}		
	}
	
	private void transformExpression(Code.Load e, ArrayList<Code> axioms) {
		transformExpression(e.operands[0],axioms);
	}
	
	private void transformExpression(Code.FunCall e, ArrayList<Code> axioms) {
		transformExpression(e.operands[0], axioms);
		try {
			WycsFile module = builder.getModule(e.nid.module());
			// module should not be null if TypePropagation has already passed.
			WycsFile.Function fn = module.declaration(e.nid.name(),
					WycsFile.Function.class);
			if (fn.constraint != null) {
				
				// OK, we have found some axioms we can instantiate!!
				
				HashMap<String, SemanticType> generics = buildGenericBinding(
						fn.type.generics(), e.type.generics());
				HashMap<Integer, Code> binding = new HashMap<Integer, Code>();
				binding.put(1, e.operands[0]);
				binding.put(0, e);
				axioms.add(fn.constraint.substitute(binding).instantiate(
						generics));
			}
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), filename, e, ex);
		}
	}
}
