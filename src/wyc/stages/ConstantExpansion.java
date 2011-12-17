package wyc.stages;

import static wyil.util.ErrorMessages.CYCLIC_CONSTANT_DECLARATION;
import static wyil.util.ErrorMessages.INVALID_BINARY_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_BOOLEAN_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_CONSTANT_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_LIST_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_NUMERIC_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_SET_EXPRESSION;
import static wyil.util.ErrorMessages.errorMessage;
import static wyil.util.SyntaxError.syntaxError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import wyc.lang.Attributes;
import wyc.lang.Expr;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.ConstDecl;
import wyc.lang.WhileyFile.Decl;
import wyil.ModuleLoader;
import wyil.lang.Module;
import wyil.lang.ModuleID;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.lang.Value;
import wyil.util.Pair;
import wyil.util.ResolveError;

public final class ConstantExpansion {
	private final ModuleLoader loader;
	private final HashSet<ModuleID> modules = new HashSet<ModuleID>();
	private final HashMap<NameID, Value> constants = new HashMap<NameID, Value>();
	private final HashMap<NameID, WhileyFile> filemap = new HashMap<NameID, WhileyFile>();
	
	public ConstantExpansion(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public void expand(List<WhileyFile> files) {
		HashMap<NameID, Expr> exprs = new HashMap();

		// first construct list.
		for (WhileyFile f : files) {
			for (Decl d : f.declarations) {
				if (d instanceof ConstDecl) {
					ConstDecl cd = (ConstDecl) d;
					NameID key = new NameID(f.module, cd.name());
					exprs.put(key, cd.constant);
					filemap.put(key, f);
				}
			}
		}

		for (NameID k : exprs.keySet()) {
			try {
				Value v = expandConstant(k, exprs, new HashSet<NameID>());	
				constants.put(k, v);
			} catch (ResolveError rex) {
				syntaxError(rex.getMessage(), filemap.get(k).filename, exprs
						.get(k), rex);
			}
		}
		
		for (WhileyFile f : files) {
			for (Decl d : f.declarations) {
				if (d instanceof ConstDecl) {
					ConstDecl cd = (ConstDecl) d;
					NameID key = new NameID(f.module, cd.name());
					cd.value = constants.get(key);
				}
			}
		}
	}
	
	/**
	 * The expand constant method is responsible for turning a named constant
	 * expression into a value. This is done by traversing the constant's
	 * expression and recursively expanding any named constants it contains.
	 * Simplification of constants is also performed where possible.
	 * 
	 * @param key
	 *            --- name of constant we are expanding.
	 * @param exprs
	 *            --- mapping of all names to their( declared) expressions
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 * @return
	 * @throws ResolveError
	 */
	private Value expandConstant(NameID key, HashMap<NameID, Expr> exprs,
			HashSet<NameID> visited) throws ResolveError {
		Expr e = exprs.get(key);
		Value value = constants.get(key);
		if (value != null) {
			return value;
		} else if (!modules.contains(key.module())) {
			// indicates a non-local key
			Module mi = loader.loadModule(key.module());
			return mi.constant(key.name()).constant();
		} else if (visited.contains(key)) {
			// this indicates a cyclic definition.
			String errMsg = errorMessage(CYCLIC_CONSTANT_DECLARATION);
			syntaxError(errMsg, filemap
					.get(key).filename, exprs.get(key));
		} else {
			visited.add(key); // mark this node as visited
		}

		// At this point, we need to replace every unresolved variable with a
		// constant definition.
		Value v = expandConstantHelper(e, filemap.get(key).filename, exprs,
				visited);
		constants.put(key, v);
		return v;
	}

	/**
	 * The following is a helper method for expandConstant. It takes a given
	 * expression (rather than the name of a constant) and expands to a value
	 * (where possible). If the expression contains, for example, method or
	 * function declarations then this will certainly fail (producing a syntax
	 * error).
	 * 
	 * @param key
	 *            --- name of constant we are expanding.
	 * @param exprs
	 *            --- mapping of all names to their( declared) expressions
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 */
	private Value expandConstantHelper(Expr expr, String filename,
			HashMap<NameID, Expr> exprs, HashSet<NameID> visited)
			throws ResolveError {
		if (expr instanceof Expr.Constant) {
			Expr.Constant c = (Expr.Constant) expr;
			return c.value;
		} else if (expr instanceof Expr.ExternalAccess) {
			Expr.ExternalAccess v = (Expr.ExternalAccess) expr;			
			return expandConstant(v.nid, exprs, visited);
		} else if (expr instanceof Expr.BinOp) {
			Expr.BinOp bop = (Expr.BinOp) expr;
			Value lhs = expandConstantHelper(bop.lhs, filename, exprs, visited);
			Value rhs = expandConstantHelper(bop.rhs, filename, exprs, visited);
			return evaluate(bop, lhs, rhs, filename);			
		} else if (expr instanceof Expr.NaryOp) {
			Expr.NaryOp nop = (Expr.NaryOp) expr;
			ArrayList<Value> values = new ArrayList<Value>();
			for (Expr arg : nop.arguments) {
				values.add(expandConstantHelper(arg, filename, exprs, visited));
			}
			if (nop.nop == Expr.NOp.LISTGEN) {
				return Value.V_LIST(values);
			} else if (nop.nop == Expr.NOp.SETGEN) {
				return Value.V_SET(values);
			}
		} else if (expr instanceof Expr.RecordGenerator) {
			Expr.RecordGenerator rg = (Expr.RecordGenerator) expr;
			HashMap<String,Value> values = new HashMap<String,Value>();
			for(Map.Entry<String,Expr> e : rg.fields.entrySet()) {
				Value v = expandConstantHelper(e.getValue(),filename,exprs,visited);
				if(v == null) {
					return null;
				}
				values.put(e.getKey(), v);
			}
			return Value.V_RECORD(values);
		} else if (expr instanceof Expr.TupleGenerator) {
			Expr.TupleGenerator rg = (Expr.TupleGenerator) expr;			
			ArrayList<Value> values = new ArrayList<Value>();			
			for(Expr e : rg.fields) {
				Value v = expandConstantHelper(e,filename,exprs,visited);
				if(v == null) {
					return null;
				}
				values.add(v);				
			}
			return Value.V_TUPLE(values);
		}  else if (expr instanceof Expr.DictionaryGenerator) {
			Expr.DictionaryGenerator rg = (Expr.DictionaryGenerator) expr;			
			HashSet<Pair<Value,Value>> values = new HashSet<Pair<Value,Value>>();			
			for(Pair<Expr,Expr> e : rg.pairs) {
				Value key = expandConstantHelper(e.first(),filename,exprs,visited);
				Value value = expandConstantHelper(e.second(),filename,exprs,visited);
				if(key == null || value == null) {
					return null;
				}
				values.add(new Pair<Value,Value>(key,value));				
			}
			return Value.V_DICTIONARY(values);
		} else if(expr instanceof Expr.Function) {
			Expr.Function f = (Expr.Function) expr;
			Attributes.Module mid = expr.attribute(Attributes.Module.class);
			if (mid != null) {
				NameID name = new NameID(mid.module, f.name);
				Type.Function tf = null;							
				return Value.V_FUN(name, tf);	
			}					
		}
		syntaxError(errorMessage(INVALID_CONSTANT_EXPRESSION), filename, expr);
		return null;
	}

	private Value evaluate(Expr.BinOp bop, Value v1, Value v2, String filename) {
		Type lub = Type.Union(v1.type(), v2.type());
		
		// FIXME: there are bugs here related to coercions.
		
		if(Type.isSubtype(Type.T_BOOL, lub)) {
			return evaluateBoolean(bop,(Value.Bool) v1,(Value.Bool) v2, filename);
		} else if(Type.isSubtype(Type.T_REAL, lub)) {
			return evaluate(bop,(Value.Rational) v1, (Value.Rational) v2, filename);
		} else if(Type.isSubtype(Type.List(Type.T_ANY, false), lub)) {
			return evaluate(bop,(Value.List)v1,(Value.List)v2, filename);
		} else if(Type.isSubtype(Type.Set(Type.T_ANY, false), lub)) {
			return evaluate(bop,(Value.Set) v1, (Value.Set) v2, filename);
		} 
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION),filename,bop);
		return null;
	}
	
	private Value evaluateBoolean(Expr.BinOp bop, Value.Bool v1, Value.Bool v2, String filename) {				
		switch(bop.op) {
		case AND:
			return Value.V_BOOL(v1.value & v2.value);
		case OR:		
			return Value.V_BOOL(v1.value | v2.value);
		case XOR:
			return Value.V_BOOL(v1.value ^ v2.value);
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),filename,bop);
		return null;
	}
	
	private Value evaluate(Expr.BinOp bop, Value.Rational v1, Value.Rational v2, String filename) {		
		switch(bop.op) {
		case ADD:
			return Value.V_RATIONAL(v1.value.add(v2.value));
		case SUB:
			return Value.V_RATIONAL(v1.value.subtract(v2.value));
		case MUL:
			return Value.V_RATIONAL(v1.value.multiply(v2.value));
		case DIV:
			return Value.V_RATIONAL(v1.value.divide(v2.value));
		case REM:
			return Value.V_RATIONAL(v1.value.intRemainder(v2.value));	
		}
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION),filename,bop);
		return null;
	}
	
	private Value evaluate(Expr.BinOp bop, Value.List v1, Value.List v2, String filename) {
		switch(bop.op) {
		case ADD:
			ArrayList<Value> vals = new ArrayList<Value>(v1.values);
			vals.addAll(v2.values);
			return Value.V_LIST(vals);
		}
		syntaxError(errorMessage(INVALID_LIST_EXPRESSION),filename,bop);
		return null;
	}
	
	private Value evaluate(Expr.BinOp bop, Value.Set v1, Value.Set v2, String filename) {		
		switch(bop.op) {
		case UNION:
		{
			HashSet<Value> vals = new HashSet<Value>(v1.values);			
			vals.addAll(v2.values);
			return Value.V_SET(vals);
		}
		case INTERSECTION:
		{
			HashSet<Value> vals = new HashSet<Value>();			
			for(Value v : v1.values) {
				if(v2.values.contains(v)) {
					vals.add(v);
				}
			}			
			return Value.V_SET(vals);
		}
		case SUB:
		{
			HashSet<Value> vals = new HashSet<Value>();			
			for(Value v : v1.values) {
				if(!v2.values.contains(v)) {
					vals.add(v);
				}
			}			
			return Value.V_SET(vals);
		}
		}
		syntaxError(errorMessage(INVALID_SET_EXPRESSION),filename,bop);
		return null;
	}	
}
