package wyone.spec;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import wyone.core.*;
import wyone.util.Pair;
import static wyone.util.SyntaxError.*;

public class Spec2WyoneBuilder {
	private String filename;

	public WyoneFile build(SpecFile file) {
		this.filename = file.filename;

		ArrayList<WyoneFile.Decl> declarations = new ArrayList<WyoneFile.Decl>();
		for (SpecFile.Decl d : file.declarations) {
			if (d instanceof SpecFile.TermDecl) {
				declarations.add(build((SpecFile.TermDecl) d));
			} else if (d instanceof SpecFile.ClassDecl) {
				declarations.add(build((SpecFile.ClassDecl) d));
			} else if (d instanceof SpecFile.RewriteDecl) {
				declarations.add(build((SpecFile.RewriteDecl) d));
			} else {
				// unknown?
				throw new IllegalArgumentException(
						"Unknown SpecFile Declaration encountered");
			}
		}

		declarations.add(buildRuleDispatch(file));

		return new WyoneFile(file.filename, declarations);
	}

	private WyoneFile.TermDecl build(SpecFile.TermDecl d) {
		return new WyoneFile.TermDecl(d.type, d.attributes());
	}

	private WyoneFile.ClassDecl build(SpecFile.ClassDecl d) {
		return new WyoneFile.ClassDecl(d.name, d.children, d.attributes());
	}

	private WyoneFile.FunDecl build(SpecFile.RewriteDecl d) {
		Type.Ref param = (Type.Ref) d.pattern.attribute(Attribute.Type.class).type;
		Environment environment = new Environment();
		ArrayList<Code> body = new ArrayList<Code>();
		int root = environment.allocate(param, "this");
		int ret = environment.allocate(Type.T_BOOL, "$");
		body.add(new Code.Constant(ret, false, d.attribute(Attribute.Source.class)));

		ArrayList<Code> codes = translate(d.pattern, root, environment, body);

		boolean conditional = true;
		for (SpecFile.RuleDecl rd : d.rules) {
			build(rd, environment, codes);
			conditional &= rd.condition != null;
		}

		body.add(new Code.Return(ret, d.attribute(Attribute.Source.class)));
		
		return new WyoneFile.FunDecl("rewrite", Type.T_FUN(Type.T_BOOL, param),
				environment.asList(), body, d.attributes());
	}

	private void build(SpecFile.RuleDecl rd, Environment environment,
			ArrayList<Code> codes) {
		ArrayList<Code> myCodes = codes;
		if (rd.condition != null) {
			myCodes = new ArrayList<Code>();
		}

		for (Pair<String, Expr> let : rd.lets) {
			int target = translate(let.second(), environment, myCodes);
			environment.put(target, let.first());
		}
		// translate rewrite body
		int operand = translate(rd.result, environment, myCodes);

		// add return vaue
		myCodes.add(new Code.Rewrite(environment.get("$"), environment.get("this"), operand,
				rd.attribute(Attribute.Source.class)));

		// translate condition (if applicable)
		if (rd.condition != null) {
			int condition = translate(rd.condition, environment, codes);
			codes.add(new Code.If(condition, myCodes, Collections.EMPTY_LIST,
					rd.condition.attribute(Attribute.Source.class)));
		}

		// undo name bindings as they're now out of scope.
		for (Pair<String, Expr> let : rd.lets) {
			int idx = environment.get(let.first());
			environment.put(idx, null);
		}
	}

	public WyoneFile.FunDecl buildRuleDispatch(SpecFile file) {
		ArrayList<Code> codes = new ArrayList<Code>();
		Environment environment = new Environment();
		environment.allocate(Type.T_REFANY, "this");
		environment.allocate(Type.T_BOOL, "$");

		codes.add(new Code.Constant(environment.get("$"), false));

		for (SpecFile.Decl d : file.declarations) {
			if (d instanceof SpecFile.RewriteDecl) {
				SpecFile.RewriteDecl fd = (SpecFile.RewriteDecl) d;
				Type.Fun type = Type.T_FUN(Type.T_BOOL,
						fd.pattern.attribute(Attribute.Type.class).type);
				ArrayList<Code> ifCodes = new ArrayList<Code>();
				int[] operands = new int[] { environment.get("this") };
				ifCodes.add(new Code.Invoke("rewrite", type, environment
						.get("$"), operands));
				codes.add(new Code.IfIs(environment.get("this"), type.param,
						ifCodes, Collections.EMPTY_LIST));
			}
		}

		codes.add(new Code.Return(environment.get("$")));

		return new WyoneFile.FunDecl("rewrite", Type.T_FUN(Type.T_BOOL,
				Type.T_REFANY), environment.asList(), codes);
	}

	private ArrayList<Code> translate(Pattern pattern, int source,
			Environment environment, ArrayList<Code> codes) {
		if (pattern instanceof Pattern.Leaf) {
			return translate((Pattern.Leaf) pattern, source, environment, codes);
		} else if (pattern instanceof Pattern.Term) {
			return translate((Pattern.Term) pattern, source, environment, codes);
		} else if (pattern instanceof Pattern.BagOrSet) {
			return translate((Pattern.BagOrSet) pattern, source, environment, codes);
		} else if (pattern instanceof Pattern.List) {
			return translate((Pattern.List) pattern, source, environment, codes);
		} else {
			syntaxError("unknown pattern encountered", filename, pattern);
			return null;
		}
	}

	private ArrayList<Code> translate(Pattern.Leaf pattern, int source,
			Environment environment, ArrayList<Code> codes) {
		// do nothing?
		return codes;
	}

	private ArrayList<Code> translate(Pattern.Term pattern, int source,
			Environment environment, ArrayList<Code> codes) {
		if (pattern.data != null) {
			Type.Ref<Type.Term> type = (Type.Ref<Type.Term>) pattern
					.attribute(Attribute.Type.class).type;
			int target = environment.allocate(type.element);
			int contents = environment.allocate(type.element.data);
			codes.add(new Code.Deref(target, source));
			codes.add(new Code.TermContents(contents, target));
			codes = translate(pattern.data, contents, environment, codes);
			if (pattern.variable != null) {
				environment.put(contents, pattern.variable);
			}
		}
		return codes;
	}

	private ArrayList<Code> translate(Pattern.BagOrSet pattern, int source,
			Environment environment, ArrayList<Code> codes) {
		
		// if you can figure this method out then you're doing well ;)
		
		Type.Ref<Type.Compound> type = (Type.Ref<Type.Compound>) pattern
				.attribute(Attribute.Type.class).type;
		
		Pair<Pattern, String>[] elements = pattern.elements;
		int target = environment.allocate(type.element);
		codes.add(new Code.Deref(target, source));
		
		Code.ForAll internal = null;
		ArrayList<Code> body = codes;
		int[] operands = new int[elements.length];

		// First, allocate all variables required for each element of the
		// pattern.

		for(int i=0;i!=elements.length;++i) {
			Pair<Pattern,String> p = elements[i];	
			Pattern pat = p.first();
			String var = p.second();
			Type.Ref pt = (Type.Ref) pat.attribute(Attribute.Type.class).type;				
			int i_operand;
			if(pattern.unbounded && (i+1) == elements.length) {			
				i_operand = environment.allocate(Type.T_SET(true,pt));				
			} else {
				i_operand = environment.allocate(pt);
			}
			operands[i] = i_operand;
			
			if(var != null) {
				environment.put(i_operand,var);
			}
		}
			
		// Second, generate the nested for-loops required for search for a
		// match for each element.

		int end = pattern.unbounded ? elements.length - 1 : elements.length;
		for (int i = end; i > 0; --i) {
			Code.ForAll last = internal;
			Pair<Pattern, String> p = elements[i - 1];
			Pattern pat = p.first();
			Type.Ref pt = (Type.Ref) pat.attribute(Attribute.Type.class).type;
			int i_operand = operands[i - 1];

			internal = new Code.ForAll(i_operand, target,
					Collections.EMPTY_LIST,
					pattern.attribute(Attribute.Source.class));
			ArrayList<Code> forCodes = internal.body; // v.naughty

			if (i > 1) {
				// Check current index is unique against those
				// already matched.
				int tmp1 = environment.allocate(Type.T_BOOL);
				int tmp2 = environment.allocate(Type.T_BOOL);
				for (int j = 0; j < (i - 1); ++j) {
					int j_operand = operands[j];
					if (j != 0) {
						forCodes.add(new Code.BinOp(Code.BOp.NEQ, tmp2,
								i_operand, j_operand));
						forCodes.add(new Code.BinOp(Code.BOp.AND, tmp1, tmp1,
								tmp2));
					} else {
						forCodes.add(new Code.BinOp(Code.BOp.NEQ, tmp1,
								i_operand, j_operand));
					}
				}
				Code.If iif = new Code.If(tmp1, Collections.EMPTY_LIST,
						Collections.EMPTY_LIST,
						pattern.attribute(Attribute.Source.class));
				forCodes.add(iif);
				forCodes = iif.trueBranch; // v.naughty
			}

			Code.IfIs is = new Code.IfIs(i_operand, pt, Collections.EMPTY_LIST,
					Collections.EMPTY_LIST,
					pattern.attribute(Attribute.Source.class));

			if (i == end) {
				body = is.trueBranch; // v.naughty
			} else {
				is.trueBranch.add(last);
			}
			forCodes.add(is);
		}

		// Third, translate nested pattern matches and extract unbounded
		// variables
		for (int i = 0; i != elements.length; ++i) {
			Pair<Pattern, String> p = elements[i];
			if (pattern.unbounded && (i+1) == elements.length) {
				int i_operand = operands[i];				
				body.add(new Code.NaryOp(
						pattern instanceof Pattern.Set ? Code.NOp.SETGEN
								: Code.NOp.BAGGEN, i_operand, Arrays.copyOf(
								operands, operands.length - 1), pattern
								.attribute(Attribute.Source.class)));
				body.add(new Code.BinOp(Code.BOp.DIFFERENCE, i_operand, target,
						i_operand, pattern.attribute(Attribute.Source.class)));
			} else {
				body = translate(p.first(), operands[i], environment, body);
			}
		}
		
		if (internal != null) {
			// internal can be null if the original non-sequential match had
			// only a single element.
			codes.add(internal);
		}
		return body;
	}
	
	private ArrayList<Code> translate(Pattern.List pattern, int source,
			Environment environment, ArrayList<Code> codes) {
		
		Type.Ref<Type.Compound> type = (Type.Ref<Type.Compound>) pattern
				.attribute(Attribute.Type.class).type;
		
		Pair<Pattern, String>[] elements = pattern.elements;
		int target = environment.allocate(type.element);
		codes.add(new Code.Deref(target, source));

		for (int i = 0; i != elements.length; ++i) {
			Pair<Pattern, String> p = elements[i];
			Pattern pat = p.first();
			String var = p.second();
			Type.Ref pt = (Type.Ref) pat.attribute(Attribute.Type.class).type;
			int element;
			if(pattern.unbounded && (i+1) == elements.length) {
				Type.Compound tc = type.element instanceof Type.List ? Type
						.T_LIST(true, pt) : Type.T_SET(true, pt);
				element = environment.allocate(tc);
				int start = environment.allocate(Type.T_INT);
				codes.add(new Code.Constant(start, BigInteger.valueOf(i), pattern
						.attribute(Attribute.Source.class)));				
				codes.add(new Code.SubList(element, target, start, pattern
						.attribute(Attribute.Source.class)));		
			} else {
				element = environment.allocate(pt);
				int index = environment.allocate(Type.T_INT);				
				codes.add(new Code.Constant(index, BigInteger.valueOf(i), pattern
						.attribute(Attribute.Source.class)));
				codes.add(new Code.IndexOf(element, target, index));				
			}
			codes = translate(pat, element, environment, codes);
			if (var != null) {
				environment.put(element, var);
			}
		}
		
		return codes;
	}

	private int translate(Expr expr, Environment environment,
			ArrayList<Code> codes) {
		if (expr instanceof Expr.BinOp) {
			return translate((Expr.BinOp) expr, environment, codes);
		} else if (expr instanceof Expr.Constant) {
			return translate((Expr.Constant) expr, environment, codes);
		} else if (expr instanceof Expr.Constructor) {
			return translate((Expr.Constructor) expr, environment, codes);
		} else if (expr instanceof Expr.NaryOp) {
			return translate((Expr.NaryOp) expr, environment, codes);
		} else if (expr instanceof Expr.UnOp) {
			return translate((Expr.UnOp) expr, environment, codes);
		} else if (expr instanceof Expr.Variable) {
			return translate((Expr.Variable) expr, environment, codes);
		} else {
			syntaxError("unknown expression encountered", filename, expr);
			return 0; // dead code
		}
	}

	private int translate(Expr.BinOp expr, Environment environment,
			ArrayList<Code> codes) {
		int result = environment
				.allocate(expr.attribute(Attribute.Type.class).type);
		Type lhs_t = expr.lhs.attribute(Attribute.Type.class).type;
		Type rhs_t = expr.rhs.attribute(Attribute.Type.class).type;
		int lhs = translate(expr.lhs, environment, codes);
		int rhs = translate(expr.rhs, environment, codes);
		switch(expr.op) {
		case APPEND:
			lhs_t = Type.unbox(lhs_t);
			rhs_t = Type.unbox(rhs_t);
			
			if(lhs_t instanceof Type.Compound) {
				lhs = coerceToValue(expr.lhs, lhs, environment, codes);				
			} else {
				lhs = coerceFromValue(expr.lhs, lhs, environment, codes);				
			}
			if(rhs_t instanceof Type.Compound) {
				rhs = coerceToValue(expr.rhs, rhs, environment, codes);	
			} else {
				rhs = coerceFromValue(expr.rhs, rhs, environment, codes);
			}
			 		
			break;
		case EQ:
		case NEQ:
			Type lt = expr.lhs.attribute(Attribute.Type.class).type;
			Type rt = expr.rhs.attribute(Attribute.Type.class).type;
			boolean lb = lt instanceof Type.Ref;
			boolean rb = rt instanceof Type.Ref;
			if(lb == rb) {
				// these ones don't need a coercion
				break;
			}
		default:		
			lhs = coerceToValue(expr.lhs, lhs, environment, codes);			
			rhs = coerceToValue(expr.rhs, rhs, environment, codes);
		}
		
		codes.add(new Code.BinOp(expr.op, result, lhs, rhs, expr.attributes()));
		return result;
	}

	private int translate(Expr.Constant expr, Environment environment,
			ArrayList<Code> codes) {
		int target = environment
				.allocate(expr.attribute(Attribute.Type.class).type);
		codes.add(new Code.Constant(target, expr.value, expr
				.attribute(Attribute.Source.class)));
		return target;
	}

	private int translate(Expr.Constructor expr, Environment environment,
			ArrayList<Code> codes) {
		int target = environment
				.allocate(expr.attribute(Attribute.Type.class).type);
		Code.Constructor code;
		if (expr.argument != null) {
			int operand = translate(expr.argument, environment, codes);
			operand = coerceFromValue(expr.argument,operand,environment,codes);
			code = new Code.Constructor(target, operand, expr.name,
					expr.attribute(Attribute.Source.class));
		} else {
			code = new Code.Constructor(target, expr.name,
					expr.attribute(Attribute.Source.class));
		}
		codes.add(code);
		return target;
	}

	private int translate(Expr.NaryOp expr, Environment environment,
			ArrayList<Code> codes) {
		int target = environment
				.allocate(expr.attribute(Attribute.Type.class).type);
		ArrayList<Expr> expr_operands = expr.arguments;
		int[] operands = new int[expr_operands.size()];
		for (int i = 0; i != operands.length; ++i) {
			Expr operand = expr_operands.get(i);			
			int optarget = translate(operand, environment, codes);
			optarget = coerceFromValue(operand, optarget, environment, codes);
			operands[i] = optarget;
		}
		codes.add(new Code.NaryOp(expr.op, target, operands, expr
				.attribute(Attribute.Source.class)));
		return target;
	}
	
	private int translate(Expr.UnOp expr, Environment environment,
			ArrayList<Code> codes) {
		// TODO
	}

	private int translate(Expr.Variable expr, Environment environment,
			ArrayList<Code> codes) {
		Integer target = environment.get(expr.var);
		if (target != null) {
			return target;
		} else {
			int result = environment.allocate(expr
					.attribute(Attribute.Type.class).type);
			codes.add(new Code.Constructor(result, expr.var, expr
					.attribute(Attribute.Source.class)));
			return result;
		}
	}

	/**
	 * Coerce the result of the given expression into a value. In other words,
	 * if the result of the expression is a reference then derference it!
	 * 
	 * @param expr
	 * @param codes
	 */
	private int coerceToValue(Expr expr, int target, Environment environment, ArrayList<Code> codes) {
		Type type = expr.attribute(Attribute.Type.class).type;
		if(type instanceof Type.Ref) {
			Type.Ref ref = (Type.Ref) type;
			int ntarget = environment.allocate(ref.element);
			codes.add(new Code.Deref(ntarget, target, expr
					.attribute(Attribute.Source.class)));
			return ntarget;
		} else {
			return target;
		}
	}

	/**
	 * Coerce the result of the given expression from a value. In other words,
	 * if the result of the expression is a value then allocate it on the heap!
	 * 
	 * @param expr
	 * @param codes
	 */
	private int coerceFromValue(Expr expr, int target, Environment environment, ArrayList<Code> codes) {
		Type type = expr.attribute(Attribute.Type.class).type;
		if(type instanceof Type.Ref) {
			return target;
		} else {
			int ntarget = environment.allocate(Type.T_REF(type));
			codes.add(new Code.New(ntarget, target, expr
					.attribute(Attribute.Source.class)));
			return ntarget;
		} 
	}
	
	private class Environment {
		private final HashMap<String, Integer> var2idx = new HashMap<String, Integer>();
		private final ArrayList<Type> idx2type = new ArrayList<Type>();

		public int allocate(Type t) {
			int idx = idx2type.size();
			idx2type.add(t);
			return idx;
		}

		public int allocate(Type t, String v) {
			int r = allocate(t);
			var2idx.put(v, r);
			return r;
		}

		public Integer get(String v) {
			return var2idx.get(v);
		}

		public void put(int idx, String v) {
			var2idx.put(v, idx);
		}

		public ArrayList<Type> asList() {
			return idx2type;
		}

		public String toString() {
			return idx2type.toString() + "," + var2idx.toString();
		}
	}
}
