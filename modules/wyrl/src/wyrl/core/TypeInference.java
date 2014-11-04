// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyrl.core;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

import wyautl.util.BigRational;
import wyrl.util.*;
import static wyrl.util.SyntaxError.*;

public class TypeInference {
	private File file;

	private final HashMap<String, Type.Term> terms = new HashMap<String, Type.Term>();

	// maps constructor names to their declared types.
	private final HashMap<String, Pair<Type, Type>> constructors = new HashMap<String, Pair<Type, Type>>();

	// globals contains the list of global variables
	private final HashMap<String,Type> macros = new HashMap();

	public void infer(SpecFile spec) {
		file = spec.file;

		// First, we have to inline all the type declarations.
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.IncludeDecl) {
				SpecFile.IncludeDecl id = (SpecFile.IncludeDecl) d;
				File myFile = file; // save
				infer(id.file);
				file = myFile; // restore
			} else if (d instanceof SpecFile.TermDecl) {
				SpecFile.TermDecl td = (SpecFile.TermDecl) d;
				terms.put(td.type.name(), td.type);
				constructors.put(td.type.name(),
						new Pair<Type, Type>(td.type.element(), td.type));
			} else if (d instanceof SpecFile.FunctionDecl) {
				SpecFile.FunctionDecl td = (SpecFile.FunctionDecl) d;
				constructors.put(td.name, new Pair<Type, Type>(td.from, td.to));
			} else if (d instanceof SpecFile.TypeDecl) {
				SpecFile.TypeDecl td = (SpecFile.TypeDecl) d;
				macros.put(td.name, td.type);
			}
		}

		// Second, sanity check all type and function constructors
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.TermDecl) {
				SpecFile.TermDecl td = (SpecFile.TermDecl) d;
				check(td.type,td);
			} else if (d instanceof SpecFile.FunctionDecl) {
				SpecFile.FunctionDecl td = (SpecFile.FunctionDecl) d;
				check(td.from,td);
				check(td.to,td);
			}
		}

		// Third, type check all rewrite declarations
		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.RewriteDecl) {
				infer((SpecFile.RewriteDecl) d);
			}
		}
	}

	public void infer(SpecFile.RewriteDecl rd) {

		HashMap<String,Type> environment = new HashMap<String,Type>();

		infer(rd.pattern,environment);

		for(SpecFile.RuleDecl rule : rd.rules) {
			infer(rule,environment);
		}
	}

	public Type.Ref infer(Pattern pattern, HashMap<String,Type> environment) {
		Type.Ref type;
		if(pattern instanceof Pattern.Leaf) {
			Pattern.Leaf p = (Pattern.Leaf) pattern;
			type = Type.T_REF(p.type);
		} else if(pattern instanceof Pattern.Term) {
			Pattern.Term p = (Pattern.Term) pattern;
			Type.Term declared = terms.get(p.name);
			if(declared == null) {
				syntaxError("unknown type encountered", file, p);
			} else if(!(declared instanceof Type.Term)) {
				// should be dead code?
				syntaxError("expected term type", file, p);
			}
			Type.Ref declared_element = declared.element();
			if(declared_element == null && p.data != null) {
				syntaxError("term type does not have children", file, p);
			}
			Type.Ref d = null;
			if(p.data != null) {
				d = infer(p.data,environment);
			} else if(p.data == null && declared.element() != null) {
				d = declared.element(); // auto-complete
			}
			if(p.variable != null) {
				environment.put(p.variable, d);
			}
			type = Type.T_REF(Type.T_TERM(p.name, d));
		} else {
			Pattern.Collection p = (Pattern.Collection) pattern;
			ArrayList<Type> types = new ArrayList<Type>();
			Pair<Pattern,String>[] p_elements = p.elements;

			for (int i=0;i!=p_elements.length;++i) {
				Pair<Pattern,String> ps = p_elements[i];
				String var = ps.second();
				Pattern pat = ps.first();
				Type t = infer(pat,environment);
				types.add(t);
				if(var != null) {
					if(p.unbounded && (i+1) == p_elements.length) {
						if(p instanceof Pattern.List) {
							t = Type.T_LIST(true,t);
						} else if(pattern instanceof Pattern.Bag) {
							t = Type.T_BAG(true,t);
						} else {
							t = Type.T_SET(true,t);
						}
					}
					environment.put(var,t);
				}
			}

			if(p instanceof Pattern.List) {
				type = Type.T_REF(Type.T_LIST(p.unbounded, types));
			} else if(p instanceof Pattern.Bag) {
				type = Type.T_REF(Type.T_BAG(p.unbounded, types));
			} else {
				type = Type.T_REF(Type.T_SET(p.unbounded, types));
			}
		}
		pattern.attributes().add(new Attribute.Type(type));
		return type;
	}

	public void infer(SpecFile.RuleDecl rd, HashMap<String,Type> environment) {
		 environment = new HashMap<String,Type>(environment);
		ArrayList<Pair<String,Expr>> rd_lets = rd.lets;
		for(int i=0;i!=rd_lets.size();++i) {
			Pair<String,Expr> let = rd_lets.get(i);
			Pair<Expr,Type> p = resolve(let.second(),environment);
			rd.lets.set(i, new Pair(let.first(),p.first()));
			environment.put(let.first(), p.second());
		}

		if(rd.condition != null) {
			Pair<Expr,Type> p = resolve(rd.condition,environment);
			rd.condition = p.first();
			checkSubtype(Type.T_BOOL(),p.second(),rd.condition);
		}

		Pair<Expr,Type> result = resolve(rd.result,environment);
		rd.result = result.first();

		// TODO: check result is a ref?
	}

	protected Pair<Expr,Type> resolve(Expr expr, HashMap<String,Type> environment) {
		try {
			Type result;
			if (expr instanceof Expr.Constant) {
				result = resolve((Expr.Constant) expr, environment);
			} else if (expr instanceof Expr.UnOp) {
				result = resolve((Expr.UnOp) expr, environment);
			} else if (expr instanceof Expr.BinOp) {
				result = resolve((Expr.BinOp) expr, environment);
			} else if (expr instanceof Expr.NaryOp) {
				result = resolve((Expr.NaryOp) expr, environment);
			} else if (expr instanceof Expr.ListUpdate) {
				result = resolve((Expr.ListUpdate) expr, environment);
			} else if (expr instanceof Expr.ListAccess) {
				Pair<Expr,Type> tmp = resolve((Expr.ListAccess) expr, environment);
				expr = tmp.first();
				result = tmp.second();
			} else if (expr instanceof Expr.Substitute) {
				result = resolve((Expr.Substitute) expr, environment);
			} else if (expr instanceof Expr.Constructor) {
				result = resolve((Expr.Constructor) expr, environment);
			} else if (expr instanceof Expr.Variable) {
				result = resolve((Expr.Variable) expr, environment);
			} else if (expr instanceof Expr.Comprehension) {
				result = resolve((Expr.Comprehension) expr, environment);
			} else if (expr instanceof Expr.Cast) {
				result = resolve((Expr.Cast) expr, environment);
			} else if (expr instanceof Expr.TermAccess) {
				result = resolve((Expr.TermAccess) expr, environment);
			} else {
				syntaxError("unknown code encountered (" + expr.getClass().getName() + ")", file, expr);
				return null;
			}
			expr.attributes().add(new Attribute.Type(result));
			return new Pair(expr,result);
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			syntaxError("internal failure", file, expr, ex);
		}
		return null; // dead code
	}

	protected Type resolve(Expr.Constant expr, HashMap<String, Type> environment) {
		Object v = expr.value;
		if (v instanceof Boolean) {
			return Type.T_BOOL();
		} else if (v instanceof BigInteger) {
			return Type.T_INT();
		} else if (v instanceof BigRational) {
			return Type.T_REAL();
		} else if (v instanceof String) {
			return Type.T_STRING();
		} else if (v instanceof Type) {
			Type t = (Type) v;
			return Type.T_META(t);
		} else {
			syntaxError("unknown constant encountered ("
					+ v.getClass().getName() + ")", file, expr);
			return null; // deadcode
		}
	}

	protected Type resolve(Expr.Constructor expr, HashMap<String,Type> environment) {
		Pair<Type,Type> arrowType = constructors.get(expr.name);

		if (arrowType == null) {
			syntaxError("function not declared", file, expr);
		} else if (expr.argument != null && arrowType.first() == null) {
			syntaxError("constructor does not take a parameter", file, expr);
		} else if (expr.argument != null) {
			Pair<Expr, Type> arg_t = resolve(expr.argument, environment);
			expr.argument = arg_t.first();
			// FIXME: put back when subtyping works properly!
			//checkSubtype(term.element(), arg_t.second(), expr.argument);
		}

		expr.external = !terms.containsKey(expr.name);

		return arrowType.second();
	}

	protected Type resolve(Expr.UnOp uop, HashMap<String,Type> environment) {
		if(uop.op == Expr.UOp.NOT) {
			// We need to clone in this case to guard against potential
			// retypings inside the expression.
			environment = (HashMap<String,Type>) environment.clone();
		}
		Pair<Expr,Type> p = resolve(uop.mhs,environment);
		uop.mhs = p.first();
		Type t = Type.unbox(p.second());

		switch (uop.op) {
		case LENGTHOF:
			if(!(t instanceof Type.Collection || t instanceof Type.Strung)) {
				syntaxError("collection type required",file,uop.mhs);
			}
			t = Type.T_INT();
			break;
		case NEG:
			if(!(t instanceof Type.Int)) {
				checkSubtype(Type.T_REAL(), t, uop);
			}

			break;
		case DENOMINATOR:
		case NUMERATOR:
			checkSubtype(Type.T_REAL(), t, uop);
			t = Type.T_INT();
			break;
		case NOT:
			checkSubtype(Type.T_BOOL(), t, uop);
			break;
		default:
			syntaxError("unknown unary expression encountered", file, uop);
		}

		return t;
	}

	protected Type resolve(Expr.BinOp bop, HashMap<String,Type> environment) {

		// First, handle special case for OR
		Pair<Expr, Type> p1 = null;
		Pair<Expr, Type> p2 = null;
		switch (bop.op) {

		case OR:
			// We need to clone the environment because, otherwise, any retyping
			// which takes place inside may leak out of the disjunction.
			p1 = resolve(bop.lhs, (HashMap<String,Type>) environment.clone());
			p2 = resolve(bop.rhs, (HashMap<String,Type>) environment.clone());
			break;
		default:
			p1 = resolve(bop.lhs,environment);
			p2 = resolve(bop.rhs,environment);
		}

		// Second, handle remaining cases

		bop.lhs = p1.first();
		bop.rhs = p2.first();
		Type lhs_t = p1.second();
		Type rhs_t = p2.second();
		Type result;

		// deal with auto-unboxing
		switch(bop.op) {
		case EQ:
		case NEQ:
			break;
		case IN:
			rhs_t = Type.unbox(rhs_t);
			break;
		default:
			lhs_t = Type.unbox(lhs_t);
			rhs_t = Type.unbox(rhs_t);
		}

		// Second, do the thing for each

		switch (bop.op) {
		case ADD:
		case SUB:
		case DIV:
		case MUL: {
			if(lhs_t instanceof Type.Int || rhs_t instanceof Type.Int) {
				checkSubtype(Type.T_INT(), lhs_t, bop);
				checkSubtype(Type.T_INT(), rhs_t, bop);
				result = Type.T_INT();
			} else {
				checkSubtype(Type.T_REAL(), lhs_t, bop);
				checkSubtype(Type.T_REAL(), rhs_t, bop);
				result = Type.T_REAL();
			}
			break;
		}
		case EQ:
		case NEQ: {
			result = Type.T_BOOL();
			break;
		}
		case LT:
		case LTEQ:
		case GT:
		case GTEQ: {
			if(lhs_t instanceof Type.Int || rhs_t instanceof Type.Int) {
				checkSubtype(Type.T_INT(), lhs_t, bop);
				checkSubtype(Type.T_INT(), rhs_t, bop);
			} else if(lhs_t instanceof Type.Real || rhs_t instanceof Type.Real){
				checkSubtype(Type.T_REAL(), lhs_t, bop);
				checkSubtype(Type.T_REAL(), rhs_t, bop);
			} else if(lhs_t instanceof Type.Strung || rhs_t instanceof Type.Strung) {
				checkSubtype(Type.T_STRING(), lhs_t, bop);
				checkSubtype(Type.T_STRING(), rhs_t, bop);
			} else {
				checkSubtype(Type.T_REAL(), lhs_t, bop);
				checkSubtype(Type.T_REAL(), rhs_t, bop);
			}
			result = Type.T_BOOL();
			break;
		}
		case AND:
		case OR: {
			checkSubtype(Type.T_BOOL(), lhs_t, bop);
			checkSubtype(Type.T_BOOL(), rhs_t, bop);
			result = Type.T_BOOL();
			break;
		}
		case APPEND: {
			if (lhs_t instanceof Type.List && rhs_t instanceof Type.List) {
				Type.Collection ls = (Type.Collection) lhs_t;
				Type.Collection rs = (Type.Collection) rhs_t;
				if(ls.unbounded() || rs.unbounded()) {
					// FIXME: we could do better here.
					result = Type.T_LIST(true,Type.T_OR(ls.element(),rs.element()));
				} else {
					result = Type.T_LIST(false,append(ls.elements(),rs.elements()));
				}
			} else if (lhs_t instanceof Type.Bag && rhs_t instanceof Type.Bag) {
				Type.Collection ls = (Type.Collection) lhs_t;
				Type.Collection rs = (Type.Collection) rhs_t;
				// FIXME: we could do better here.
				result = Type.T_BAG(ls.unbounded() || rs.unbounded(),Type.T_OR(ls.element(),rs.element()));
			} else if (lhs_t instanceof Type.Set && rhs_t instanceof Type.Set) {
				Type.Collection ls = (Type.Collection) lhs_t;
				Type.Collection rs = (Type.Collection) rhs_t;
				// FIXME: we could do better here.
				result = Type.T_SET(ls.unbounded() || rs.unbounded(),Type.T_OR(ls.element(),rs.element()));
			} else if (lhs_t instanceof Type.Collection
					&& rhs_t instanceof Type.Collection) {
					// FIXME: we need better support for non-uniform appending
					// (e.g. set ++ bag, bag ++ list, etc).
				result = Type.T_OR(lhs_t, rhs_t);
			} else if (rhs_t instanceof Type.List) {
				lhs_t = Type.box(lhs_t);
				Type.List rhs_tc = (Type.List) rhs_t;
				// right append
				result = Type.T_LIST(rhs_tc.unbounded(),
						append(lhs_t, rhs_tc.elements()));
			} else if (lhs_t instanceof Type.List){
				// left append
				Type.List lhs_tc = (Type.List) lhs_t;
				rhs_t = Type.box(rhs_t);
				if (!lhs_tc.unbounded()) {
					result = Type.T_LIST(lhs_tc.unbounded(),
							append(lhs_tc.elements(), rhs_t));
				} else {
					Type[] lhs_elements = lhs_tc.elements();
					int length = lhs_elements.length;
					Type[] nelements = Arrays.copyOf(lhs_elements, length);
					length--;
					nelements[length] = Type.T_OR(rhs_t, nelements[length]);
					result = Type.T_LIST(true,nelements);
				}
			} else if (lhs_t instanceof Type.Collection) {
				Type.Collection lhs_tc = (Type.Collection) lhs_t;
				rhs_t = Type.box(rhs_t);
				Type element = Type.T_OR(append(rhs_t,lhs_tc.elements()));
				result = Type.T_COMPOUND(lhs_tc,true,element);
			} else if (rhs_t instanceof Type.Collection) {
				Type.Collection rhs_tc = (Type.Collection) rhs_t;
				lhs_t = Type.box(lhs_t);
				Type element = Type.T_OR(append(lhs_t,rhs_tc.elements()));
				result = Type.T_COMPOUND(rhs_tc,true,element);
			} else {
				syntaxError("cannot append non-list types",file,bop);
				return null;
			}
			break;
		}
		case IS: {
			checkSubtype(Type.T_METAANY(), rhs_t, bop);
			Type.Meta m = (Type.Meta) rhs_t;
			checkSubtype(lhs_t, m.element(), bop);
			if(bop.lhs instanceof Expr.Variable) {
				// retyping
				Expr.Variable v = (Expr.Variable) bop.lhs;
				// FIXME: should compute intersection here
				environment.put(v.var, m.element());
			}
			result = Type.T_BOOL();
			break;
		}
		case IN: {
			if(!(rhs_t instanceof Type.Collection)) {
				syntaxError("collection type required",file,bop.rhs);
			}
			Type.Collection tc = (Type.Collection) rhs_t;
			checkSubtype(tc.element(), lhs_t, bop);
			result = Type.T_BOOL();
			break;
		}
		case RANGE: {
			checkSubtype(Type.T_INT(), lhs_t, bop);
			checkSubtype(Type.T_INT(), rhs_t, bop);
			result = Type.T_LIST(true,Type.T_INT());
			break;
		}
		default:
			syntaxError("unknown binary expression encountered", file, bop);
			return null; // dead-code
		}
		return result;
	}

	protected Type resolve(Expr.Comprehension expr, HashMap<String, Type> environment) {
		environment = (HashMap) environment.clone();
		ArrayList<Pair<Expr.Variable,Expr>> expr_sources = expr.sources;
		for(int i=0;i!=expr_sources.size();++i) {
			Pair<Expr.Variable,Expr> p = expr_sources.get(i);
			Expr.Variable variable = p.first();
			Expr source = p.second();
			if(environment.containsKey(variable.var)) {
				syntaxError("duplicate variable '" + variable + "'",file,variable);
			}
			Pair<Expr,Type> tmp = resolve(source,environment);
			expr_sources.set(i, new Pair(variable,tmp.first()));
			Type type = tmp.second();
			if(!(type instanceof Type.Collection)) {
				syntaxError("collection type required",file,source);
			}
			Type.Collection sourceType = (Type.Collection) type;
			Type elementType = sourceType.element();
			variable.attributes().add(new Attribute.Type(elementType));
			environment.put(variable.var, elementType);
		}

		if(expr.condition != null) {
			Pair<Expr,Type> p = resolve(expr.condition,environment);
			expr.condition = p.first();
			checkSubtype(Type.T_BOOL(),p.second(),expr.condition);
		}

		switch(expr.cop) {
			case NONE:
			case SOME:
				return Type.T_BOOL();
			case SETCOMP: {
				Pair<Expr,Type> result = resolve(expr.value,environment);
				expr.value = result.first();
				return Type.T_SET(true,Type.box(result.second()));
			}
			case BAGCOMP: {
				Pair<Expr,Type> result = resolve(expr.value,environment);
				expr.value = result.first();
				return Type.T_BAG(true,Type.box(result.second()));
			}
			case LISTCOMP: {
				Pair<Expr,Type> result = resolve(expr.value,environment);
				expr.value = result.first();
				return Type.T_LIST(true,Type.box(result.second()));
			}
			default:
				throw new IllegalArgumentException("unknown comprehension kind");
		}
	}

	protected Pair<Expr,Type> resolve(Expr.ListAccess expr, HashMap<String, Type> environment) {
		Pair<Expr,Type> p2 = resolve(expr.index,environment);
		expr.index= p2.first();
		Type idx_t = p2.second();

		// First, a little check for the unusual case that this is, in fact, not
		// a list access but a constructor with a single element list valued
		// argument.

		if(expr.src instanceof Expr.Variable) {
			Expr.Variable v = (Expr.Variable) expr.src;
			if(!environment.containsKey(v.var) && constructors.containsKey(v.var)) {
				// ok, this is a candidate
				ArrayList<Expr> arguments = new ArrayList<Expr>();
				arguments.add(expr.index);
				Expr argument = new Expr.NaryOp(Expr.NOp.LISTGEN, arguments,
						expr.attribute(Attribute.Source.class));
				resolve(argument,environment); // must succeed ;)
				return new Pair<Expr, Type>(new Expr.Constructor(v.var,
						argument, expr.attributes()), constructors.get(v.var)
						.second());
			}
		}

		Pair<Expr,Type> p1 = resolve(expr.src,environment);
		expr.src = p1.first();

		Type src_t = Type.unbox(p1.second());
		checkSubtype(Type.T_LISTANY(), src_t, expr.src);
		checkSubtype(Type.T_INT(), idx_t, expr.index);

		Type.List list_t = (Type.List) src_t;
		Type[] list_elements = list_t.elements();

		if(expr.index instanceof Expr.Constant) {
			Expr.Constant idx = (Expr.Constant) expr.index;
			BigInteger v = (BigInteger) idx.value; // must succeed
			if(v.compareTo(BigInteger.ZERO) < 0) {
				syntaxError("negative list access",file,idx);
				return null; // dead-code
			} else if(!list_t.unbounded() && v.compareTo(BigInteger.valueOf(list_elements.length)) >= 0) {
				syntaxError("list access out-of-bounds",file,idx);
				return null; // dead-code
			} else {
				return new Pair<Expr,Type>(expr,list_elements[v.intValue()]);
			}
		} else {
			return new Pair<Expr, Type>(expr, list_t.element());
		}
	}

	protected Type resolve(Expr.ListUpdate expr, HashMap<String, Type> environment) {

		Pair<Expr,Type> p1 = resolve(expr.src,environment);
		Pair<Expr,Type> p2 = resolve(expr.index,environment);
		Pair<Expr,Type> p3 = resolve(expr.value,environment);

		expr.src = p1.first();
		expr.index = p2.first();
		expr.value = p3.first();

		Type src_t = p1.second();
		Type idx_t = p2.second();
		Type value_t = p3.second();

		checkSubtype(Type.T_LISTANY(), src_t, expr.src);
		checkSubtype(Type.T_INT(), idx_t, expr.index);
		Type.List src_lt = (Type.List) src_t;
		Type[] src_elements = src_lt.elements();
		Type[] new_elements = new Type[src_elements.length];
		for(int i=0;i!=src_elements.length;++i) {
			new_elements[i] = Type.T_OR(src_elements[i],value_t);
		}
		return Type.T_LIST(src_lt.unbounded(),new_elements);
	}

	protected Type resolve(Expr.Substitute expr, HashMap<String, Type> environment) {
		Pair<Expr,Type> p1 = resolve(expr.src,environment);
		Pair<Expr,Type> p2 = resolve(expr.original,environment);
		Pair<Expr,Type> p3 = resolve(expr.replacement,environment);

		expr.src = p1.first();
		expr.original= p2.first();
		expr.replacement= p3.first();

		Type src_t = p1.second();

		// FIXME: need to generate something better here!!
		return src_t;
	}

	protected Type resolve(Expr.NaryOp expr, HashMap<String, Type> environment) {
		ArrayList<Expr> operands = expr.arguments;
		Type[] types = new Type[operands.size()];

		for (int i = 0; i != types.length; ++i) {
			Pair<Expr,Type> p = resolve(operands.get(i),environment);
			operands.set(i, p.first());
			types[i] = Type.box(p.second());
		}
		if(expr.op == Expr.NOp.LISTGEN) {
			return Type.T_LIST(false, types);
		} else if(expr.op == Expr.NOp.BAGGEN) {
			return Type.T_BAG(false, types);
		} else {
			return Type.T_SET(false, types);
		}
	}

	protected Type resolve(Expr.Variable code, HashMap<String, Type> environment) {
		Type result = environment.get(code.var);
		if (result == null) {
			Pair<Type, Type> tmp = constructors.get(code.var);
			if (tmp == null) {
				syntaxError("unknown variable or constructor encountered",
						file, code);
			} else if (tmp.first() != null) {
				syntaxError("cannot instantiate non-unit term", file, code);
			}
			code.isConstructor = true;
			return tmp.second();
		} else {
			return result;
		}
	}

	protected Type resolve(Expr.Cast expr, HashMap<String, Type> environment) {
		Pair<Expr,Type> p = resolve(expr.src,environment);
		expr.src = p.first();
		Type type = p.second();
		type = Type.unbox(type);
		if(!(type instanceof Type.Int && expr.type instanceof Type.Real)) {
			syntaxError("cannot cast from " + type + " to " + expr.type, file, expr);
		}
		return expr.type;
	}

	protected Type resolve(Expr.TermAccess expr, HashMap<String, Type> environment) {
		Pair<Expr,Type> p = resolve(expr.src,environment);
		Expr src = p.first();
		Type type = p.second();

		expr.src = src;
		type = Type.unbox(type);
		if(!(type instanceof Type.Term)) {
			syntaxError("expecting term type, got type " + type, file, expr);
		}
		type = ((Type.Term) type).element();
		if(type == null) {
			return Type.T_VOID();
		} else {
			return type;
		}
	}

	public Type[] append(Type head, Type[] tail) {
		Type[] r = new Type[tail.length+1];
		System.arraycopy(tail,0,r,1,tail.length);
		r[0] = head;
		return r;
	}

	public Type[] append(Type[] head, Type tail) {
		Type[] r = new Type[head.length+1];
		System.arraycopy(head,0,r,0,head.length);
		r[head.length] = tail;
		return r;
	}

	public Type[] append(Type[] head, Type[] tail) {
		Type[] r = new Type[head.length + tail.length];
		System.arraycopy(head, 0, r, 0, head.length);
		System.arraycopy(tail, 0, r, head.length, tail.length);
		return r;
	}

	/**
	 * Check that this type is consistent and that all constructors used within
	 * it are declared.
	 *
	 * @param t
	 * @param constructors
	 */
	public void check(Type type, SyntacticElement elem) {
		if(type instanceof Type.Atom) {
			// Do nothing in this case
		} else if(type instanceof Type.Unary) {
			Type.Unary t = (Type.Unary) type;
			check(t.element(),elem);
		} else if(type instanceof Type.Term) {
			Type.Term t = (Type.Term) type;
			if(terms.get(t.name()) == null && macros.get(t.name()) == null) {
				syntaxError("unknown term encountered: " + t.name(), file, elem);
			}
			Type element = t.element();
			if(element != null) {
				check(t.element(),elem);
			}
		} else if(type instanceof Type.Nominal) {
			Type.Nominal t = (Type.Nominal) type;
			if(macros.get(t.name()) == null) {
				syntaxError("unknown nominal encountered: " + t.name(), file, elem);
			}
			// NOTE: we don't check the element of a nominal, since this may be
			// recursive and we don't need to here.
		} else if(type instanceof Type.Collection) {
			Type.Collection t = (Type.Collection) type;
			for(Type child : t.elements()) {
				check(child,elem);
			}
		} else if(type instanceof Type.Nary) {
			Type.Nary t = (Type.Nary) type;
			for(Type child : t.elements()) {
				check(child,elem);
			}
		} else {
			syntaxError("invalid type: " + type.getClass().getName(), file, elem);
		}
	}

	/**
	 * Check whether t1 :> t2; that is, whether t2 is a subtype of t1.
	 *
	 * @param t1
	 * @param t2
	 * @param elem
	 */
	public void checkSubtype(Type t1, Type t2, SyntacticElement elem) {
		t1 = Type.unbox(t1);
		t2 = Type.unbox(t2);

		if (t1.isSubtype(t2)) {
			return;
		}

		syntaxError("expecting type " + t1 + ", got type " + t2, file, elem);
	}
}
