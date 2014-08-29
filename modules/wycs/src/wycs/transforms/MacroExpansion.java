package wycs.transforms;

import java.util.ArrayList;
import java.util.HashMap;

import static wycc.lang.SyntaxError.*;
import wybs.lang.Builder;
import wycc.lang.Transform;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.Code;
import wycs.core.SemanticType;
import wycs.core.WycsFile;

/**
 * Responsible for inlining <i>macros</i> (i.e. named expressions or types
 * created with the <code>defined</code> statement). For example:
 * 
 * <pre>
 * define implies(bool x, bool y) as !x || y
 * 
 * assert:
 *    implies(true,true)
 * </pre>
 * 
 * The <code>define</code> statement creates the typed macro
 * <code>implies(bool,bool)->bool</code>. After macro expansion, we are left
 * with the following:
 * 
 * <pre>
 * define implies(bool x, bool y) as !x || y
 * 
 * assert:
 *    !true || true
 * </pre>
 * 
 * Here, we can see that the <code>implies</code> macro has simply been replaced
 * by its definition, with its parameters substituted accordingly for its
 * arguments. The purpose of this transform is simply to implement this
 * expansion procedure.
 * 
 * @author David J. Pearce
 * 
 */
public class MacroExpansion implements Transform<WycsFile> {
	
	/**
	 * Determines whether macro inlining is enabled or not.
	 */
	private boolean enabled = getEnable();

	private final Wyal2WycsBuilder builder;
	
	private String filename;

	// ======================================================================
	// Constructor(s)
	// ======================================================================
	
	public MacroExpansion(Builder builder) {
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
			WycsFile.Macro sf = (WycsFile.Macro) s;
			transform(sf);
		} else if(s instanceof WycsFile.Assert) {
			transform((WycsFile.Assert)s);
		} else {
			internalFailure("unknown declaration encountered (" + s + ")",
					filename, s);
		}
	}
	
	private void transform(WycsFile.Function s) {
		if(s.constraint != null) {
			s.constraint = transform(s.constraint);
		}
	}
	
	private void transform(WycsFile.Macro s) {
		s.condition = transform(s.condition);
	}
	
	private void transform(WycsFile.Assert s) {
		s.condition = transform(s.condition);
	}
		
	private Code transform(Code e) {
		if (e instanceof Code.Variable || e instanceof Code.Constant) {
			// do nothing
			return e;
		} else if (e instanceof Code.Unary) {
			return transform((Code.Unary) e);
		} else if (e instanceof Code.Binary) {
			return transform((Code.Binary) e);
		} else if (e instanceof Code.Nary) {
			return transform((Code.Nary) e);
		} else if (e instanceof Code.Load) {
			return transform((Code.Load) e);
		} else if (e instanceof Code.FunCall) {
			return transform((Code.FunCall) e);
		} else if (e instanceof Code.Quantifier) {
			return transform((Code.Quantifier) e);
		} else {
			internalFailure("invalid expression encountered (" + e + ", "
					+ e.getClass().getName() + ")", filename, e);
			return null; // dead code
		}
	}
	
	private Code transform(Code.Unary e) {
		return Code.Unary(e.type, e.opcode, transform(e.operands[0]),
					e.attributes());		
	}
	
	private Code transform(Code.Binary e) {
		return Code.Binary(e.type, e.opcode, transform(e.operands[0]),
				transform(e.operands[1]), e.attributes());		
	}
	
	private Code transform(Code.Nary e) {
		Code[] e_operands = e.operands;
		Code[] operands = new Code[e_operands.length];
		for (int i = 0; i != e_operands.length; ++i) {
			operands[i] = transform(e_operands[i]);
		}
		return Code.Nary(e.type, e.opcode, operands, e.attributes());
	}
	
	private Code transform(Code.Load e) {		
		return Code.Load(e.type, transform(e.operands[0]), e.index,
				e.attributes());
	}
	
	private Code transform(Code.FunCall e) {
		Code r = e;
		try {
			WycsFile module = builder.getModule(e.nid.module());
			// In principle, module should not be null if TypePropagation has
			// already passed. However, in the case of a function call inserted
			// during code generation, there is no guarantee that it was
			// previously resolved. This can cause problems if the standard
			// library is not on the path as e.g. x[i] is translated into
			// a call to wycs.core.Map.IndexOf(). 
			if(module == null) {
				internalFailure("cannot resolve as module: " + e.nid.module(), filename, e);
			}
			Object d = module.declaration(e.nid.name());
			if(d instanceof WycsFile.Function) {
				// Do nothing, since functions are not expanded like macros.
				// Instead, their axioms may be instantiated later either before
				// or during rewriting.
			} else if (d instanceof WycsFile.Macro) {
				WycsFile.Macro m = (WycsFile.Macro) d;
				HashMap<String, SemanticType> generics = buildGenericBinding(
						m.type.generics(), e.type.generics());
				HashMap<Integer, Code> binding = new HashMap<Integer, Code>();
				binding.put(0, e.operands[0]);
				r = m.condition.substitute(binding).instantiate(generics);
			} else {
				internalFailure("cannot resolve as function or macro call",
						filename, e);
			}
		} catch(InternalFailure ex) {
			throw ex;
		} catch(Exception ex) {
			internalFailure(ex.getMessage(), filename, e, ex);
		}		
		
		transform(e.operands[0]);
		return r;		
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
	
	private Code transform(Code.Quantifier e) {
		return Code.Quantifier(e.type, e.opcode,
				transform(e.operands[0]), e.types, e.attributes());
	}
}
