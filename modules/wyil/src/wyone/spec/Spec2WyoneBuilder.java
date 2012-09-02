package wyone.spec;

import java.util.ArrayList;
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
		for(SpecFile.Decl d : file.declarations) {
			if(d instanceof SpecFile.TermDecl) {
				declarations.add(build((SpecFile.TermDecl)d));
			} else if(d instanceof SpecFile.ClassDecl) {
				declarations.add(build((SpecFile.ClassDecl)d));
			} else if(d instanceof SpecFile.RewriteDecl) {
				declarations.add(build((SpecFile.RewriteDecl)d));
			} else {
				// unknown?
				throw new IllegalArgumentException("Unknown SpecFile Declaration encountered");
			}		
		}
		
		declarations.add(buildRuleDispatch(file));
		
		return new WyoneFile(file.filename,declarations);
	}
	
	private WyoneFile.TermDecl build(SpecFile.TermDecl d) {
		return new WyoneFile.TermDecl(d.type,d.attributes());
	}
	
	private WyoneFile.ClassDecl build(SpecFile.ClassDecl d) {
		return new WyoneFile.ClassDecl(d.name,d.children,d.attributes());
	}
	
	private WyoneFile.FunDecl build(SpecFile.RewriteDecl d) {
		Type.Ref param = typeOf(d.pattern);
		Environment environment = new Environment();
		ArrayList<Code> body = new ArrayList<Code>();
		int root = environment.allocate(param, "this");
		
		translate(d.pattern, root, environment, body);

		boolean conditional = true;
		for (SpecFile.RuleDecl rd : d.rules) {
			build(rd, environment, body);
			conditional &= rd.condition != null;
		}

		if (conditional) {
			// indicates all rules in this block were conditional, so we need to
			// add a default case.
			int operand = environment.allocate(Type.T_BOOL);
			body.add(new Code.Constant(operand, false));
			body.add(new Code.Return(operand));
		}

		return new WyoneFile.FunDecl("rewrite", Type.T_FUN(Type.T_BOOL, param),
				environment.asList(), body, d.attributes());
	}
	
	private void build(SpecFile.RuleDecl rd, Environment environment,
			ArrayList<Code> codes) {
		ArrayList<Code> myCodes = codes;
		if(rd.condition != null) {
			myCodes = new ArrayList<Code>();
		}
		
		for(Pair<String,Expr> let : rd.lets) {
			int target = translate(let.second(),environment,myCodes);
			environment.put(target, let.first());
		}
		// translate rewrite body
		int operand = translate(rd.result,environment,myCodes);
		
		// add return vaue
		int target = environment.allocate(Type.T_BOOL);
		myCodes.add(new Code.Rewrite(target, environment.get("this"), operand,
				rd.attribute(Attribute.Source.class)));		
		myCodes.add(new Code.Return(target,rd.attribute(Attribute.Source.class)));

		// translate condition (if applicable)
		if(rd.condition != null) {
			int condition = translate(rd.condition,environment,codes);
			codes.add(new Code.If(condition, myCodes, Collections.EMPTY_LIST,
					rd.condition.attribute(Attribute.Source.class)));
		}
		
		// undo name bindings as they're now out of scope.
		for(Pair<String,Expr> let : rd.lets) {
			int idx = environment.get(let.first());
			environment.put(idx,null);
		}
	}
	
	public WyoneFile.FunDecl buildRuleDispatch(SpecFile file) {
		ArrayList<Code> codes = new ArrayList<Code>();
		Environment environment = new Environment();
		environment.allocate(Type.T_REFANY, "this");
		environment.allocate(Type.T_BOOL, "ret");
		
		codes.add(new Code.Constant(environment.get("ret"),false));
		
		for(SpecFile.Decl d : file.declarations) {
			if(d instanceof SpecFile.RewriteDecl) {
				SpecFile.RewriteDecl fd = (SpecFile.RewriteDecl) d;
				Type.Fun type = Type.T_FUN(Type.T_BOOL,typeOf(fd.pattern));
				ArrayList<Code> ifCodes = new ArrayList<Code>();
				int[] operands = new int[] { environment.get("this") };
				ifCodes.add(new Code.Invoke("rewrite", type, environment.get("ret"),
						operands));
				codes.add(new Code.IfIs(environment.get("this"), type.param,
						ifCodes, Collections.EMPTY_LIST));					
			}
		}
		
		codes.add(new Code.Return(environment.get("ret")));
		
		return new WyoneFile.FunDecl("rewrite", Type.T_FUN(Type.T_BOOL, Type.T_REFANY),
				environment.asList(), codes);
	}	
	
	private void translate(Pattern pattern, int source, Environment environment, ArrayList<Code> codes) {
		if(pattern instanceof Pattern.Leaf) {
			translate((Pattern.Leaf) pattern, source, environment, codes);
		} else if(pattern instanceof Pattern.Term) {
			translate((Pattern.Term) pattern, source, environment, codes);
		} else if(pattern instanceof Pattern.Compound) {
			translate((Pattern.Compound) pattern, source, environment, codes);
		} else {
			syntaxError("unknown pattern encountered",filename,pattern);
		}
	}
	
	private void translate(Pattern.Leaf pattern, int source, Environment environment, ArrayList<Code> codes) {
		// do nothing?
	}
	
	private void translate(Pattern.Term pattern, int source, Environment environment, ArrayList<Code> codes) {
		if(pattern.data != null) {
			int target = environment.allocate(Type.T_ANY);
			int contents = environment.allocate(Type.T_REFANY);		
			codes.add(new Code.Deref(target,source)); 
			codes.add(new Code.TermContents(contents,target));
			translate(pattern.data,contents,environment,codes);
			if(pattern.variable != null) {
				environment.put(contents,pattern.variable);
			}
		}
	}

	private void translate(Pattern.Compound pattern, int source,
			Environment environment, ArrayList<Code> codes) {
		Pair<Pattern, String>[] elements = pattern.elements;
		int target = environment.allocate(Type.T_ANY);
		codes.add(new Code.Deref(target, source));

		// TODO: unbound compound matches

		// TODO: non-sequential compound matches

		for (int i = 0; i != elements.length; ++i) {
			Pair<Pattern, String> p = elements[i];
			int index = environment.allocate(Type.T_INT);
			int element = environment.allocate(Type.T_REFANY);
			codes.add(new Code.Constant(index, i, pattern
					.attribute(Attribute.Source.class)));
			codes.add(new Code.IndexOf(element, target, index));
			translate(p.first(), element, environment, codes);
			if (p.second() != null) {
				environment.put(element, p.second());
			}
		}
	}
	
	private int translate(Expr expr, Environment environment, ArrayList<Code> codes) {
		if(expr instanceof Expr.BinOp) {
			return translate((Expr.BinOp) expr,environment,codes);
		} else if(expr instanceof Expr.Constant) {
			return translate((Expr.Constant) expr,environment,codes);
		} else if(expr instanceof Expr.Constructor) {
			return translate((Expr.Constructor) expr,environment,codes);
		} else if(expr instanceof Expr.UnOp) {
			return translate((Expr.UnOp) expr,environment,codes);
		} else if(expr instanceof Expr.Variable) {
			return translate((Expr.Variable) expr,environment,codes);
		} else {
			syntaxError("unknown expression encountered",filename,expr);
			return 0; // dead code
		}
	}
	
	private int translate(Expr.BinOp expr, Environment environment,
			ArrayList<Code> codes) {
		int result = environment.allocate(Type.T_ANY);
		int lhs = translate(expr.lhs, environment, codes);
		int rhs = translate(expr.rhs, environment, codes);
		codes.add(new Code.BinOp(expr.op, result, lhs, rhs, expr.attributes()));
		return result;
	}

	private int translate(Expr.Constant expr, Environment environment, ArrayList<Code> codes) {		
		int target = environment.allocate(Type.T_ANY);
		codes.add(new Code.Constant(target, expr.value, expr.attribute(Attribute.Source.class)));
		return target;
	}

	private int translate(Expr.Constructor expr, Environment environment, ArrayList<Code> codes) {
		// TODO
	}

	private int translate(Expr.UnOp expr, Environment environment, ArrayList<Code> codes) {
		// TODO
	}

	private int translate(Expr.Variable expr, Environment environment,
			ArrayList<Code> codes) {
		Integer target = environment.get(expr.var);
		if (target != null) {
			return target;
		} else {
			int result = environment.allocate(Type.T_ANY);
			codes.add(new Code.Constructor(result, expr.var, expr
					.attribute(Attribute.Source.class)));
			return result;
		}
	}

	
	/**
	 * Translate the declared type in a pattern construct into an actual type.
	 * Essentially, this corresponds to discarding all of the parameter(s) used
	 * for matching subcomponents of the type.
	 * 
	 * @param pattern
	 * @return
	 */
	private Type.Ref typeOf(Pattern pattern) {
		if (pattern instanceof Pattern.Leaf) {
			Pattern.Leaf pl = (Pattern.Leaf) pattern;
			return Type.T_REF(pl.type);
		} else if (pattern instanceof Pattern.Term) {
			Pattern.Term pt = (Pattern.Term) pattern;
			Type.Ref data = null;
			if(pt.data != null) {
				data = typeOf(pt.data);
			}
			return Type.T_REF(Type.T_TERM(pt.name, data));
		} else if (pattern instanceof Pattern.Compound) {
			Pattern.Compound pc = (Pattern.Compound) pattern;
			// FIXME: following should be an ArrayList<Type.Ref>
			ArrayList<Type> types = new ArrayList<Type>();
			for (Pair<Pattern, String> ps : pc.elements) {
				types.add(typeOf(ps.first()));
			}
			return Type.T_REF(Type.T_COMPOUND(pc.kind, pc.unbounded, types));
		} else {
			syntaxError("unknown pattern encountered", filename, pattern);
			return null;
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
