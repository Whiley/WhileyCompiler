package wyone.core;

import java.util.*;

import static wyone.core.Expr.*;
import static wyone.core.SpecFile.*;
import wyil.util.*;
import static wyil.util.SyntaxError.*;

public class TypeChecker {
	private String filename;
	public void check(SpecFile spec) {
		filename = spec.filename;
		for(Decl d : spec.declarations) {
			if(d instanceof RewriteDecl) {
				check((RewriteDecl)d);
			}
		}
	}
	
	public void check(RewriteDecl rd) {			
		HashMap<String,Type> environment = new HashMap<String,Type>();
		for(Pair<TypeDecl,String> td : rd.types){						
			environment.put(td.second(), td.first().type);
		}
		
		for(RuleDecl rule : rd.rules) {
			check(rule,environment);
		}
	}
	
	public void check(RuleDecl rule, HashMap<String,Type> environment) {
		for(Pair<String,Expr> let : rule.lets) {			
			environment.put(let.first(), resolve(let.second(),environment));
		}
		resolve(rule.condition,environment);
	}
		
	protected Type resolve(Expr e, HashMap<String,Type> environment) {
	    try {
	      if (e instanceof Constant) {
	        return resolve((Constant) e, environment);
	      } else if (e instanceof Variable) {
	        return resolve((Variable) e, environment);
	      } else if (e instanceof UnOp) {
	        return resolve((UnOp) e, environment);
	      } else if (e instanceof Invoke) {
	        return resolve((Invoke) e, environment);
	      } else if (e instanceof BinOp) {
	        return resolve((BinOp) e, environment);
	      } else if (e instanceof NaryOp) {
	        return resolve((NaryOp) e, environment);
	      } else if (e instanceof RecordGen) {
	        return resolve((RecordGen) e, environment);
	      } else if (e instanceof RecordAccess) {
	        return resolve((RecordAccess) e, environment);
	      } else if (e instanceof DictionaryGen) {
	        return resolve((DictionaryGen) e, environment);
	      } else if (e instanceof TupleGen) {
	        return resolve((TupleGen) e, environment);
	      } else {
	        syntaxError("unknown expression encountered", filename, e);
	      }
	    } catch (SyntaxError se) {
	      throw se;
	    } catch (Exception ex) {
	      syntaxError("internal failure", filename, e, ex);
	    }
	    return null;
	  }

	  protected Type resolve(Constant c, HashMap<String,Type> environment) {
	    Object v = c.value;
	    if (v instanceof Boolean) {
	      return Type.T_BOOL;
	    } else if (v instanceof Character) {
	      return Type.T_CHAR;
	    } else if (v instanceof Integer) {
	      return Type.T_INT;
	    } else if (v instanceof Double) {
	      return Type.T_REAL;
	    } else if (v instanceof String) {
	      return Type.T_STRING;
	    }
	    syntaxError("unknown constant encountered (" + v.getClass().getName() + ")", filename, c);
	    return null;
	  }

	  protected Type resolve(FunConst c, HashMap<String,Type> environment)
	      throws ResolveError {
	    ModuleID mid = c.attribute(Attribute.Module.class).module;
	    ArrayList<Type> types = new ArrayList<Type>();
	    for (UnresolvedType ut : c.paramTypes) {
	      types.add(resolve(ut));
	    }
	    NameID nid = new NameID(mid, c.name);
	    return bindFunction(nid, types, c);
	  }

	  protected Type resolve(Variable v, HashMap<String,Type> environment)
	      throws ResolveError {
	    Type v_t = environment.get(v.var);
	    if (v_t != null) {
	      return v_t;
	    }
	    // Not a variable, but could be a constant
	    Attribute.Module mattr = v.attribute(Attribute.Module.class);
	    if (mattr != null) {
	      Expr constant = constants.get(new NameID(mattr.module, v.var));
	      return resolve(constant, environment);
	    }
	    syntaxError("variable not defined", filename, v);
	    return null;
	  }

	  protected Type resolve(Invoke ivk, HashMap<String,Type> environment) {

	    // First, we look for a local variable with the matching name

	    Type t = environment.get(ivk.name);
	    if (t instanceof Type.Fun) {
	      Type.Fun ft = (Type.Fun) t;
	      if (ivk.arguments.size() != ft.params.size()) {
	        syntaxError("incorrect arguments for function call", filename, ivk);
	      }
	      for (int i = 0; i != ft.params.size(); ++i) {
	        Expr arg = ivk.arguments.get(i);
	        Type pt = ft.params.get(i);
	        Type at = resolve(arg, environment);
	        checkSubtype(pt, at, arg);
	      }

	      ivk.indirect = true;

	      return ft.ret;
	    } else {

	      ArrayList<Type> types = new ArrayList<Type>();

	      for (Expr arg : ivk.arguments) {
	        Type arg_t = resolve(arg, environment);
	        types.add(arg_t);
	      }

	      // Second, we assume it's not a local variable and look outside the
	      // scope.

	      try {
	        // FIXME: when putting name spacing back in, we'll need to fix this.
	        ModuleID mid = ivk.attribute(Attribute.Module.class).module;
	        NameID nid = new NameID(mid, ivk.name);
	        Type.Fun funtype = bindFunction(nid, types, ivk);
	        // now, update the invoke
	        ivk.attributes().add(new Attribute.FunType(funtype));
	        return funtype.ret;
	      } catch (ResolveError ex) {
	        syntaxError(ex.getMessage(), filename, ivk);
	        return null; // unreachable
	      }
	    }
	  }

	  /**
	   * Bind function is responsible for determining the true type of a method or
	   * function being invoked. To do this, it must find the function/method with
	   * the most precise type that matches the argument types. *
	   * 
	   * @param nid
	   * @param receiver
	   * @param paramTypes
	   * @param elem
	   * @return
	   * @throws ResolveError
	   */
	  protected Type.Fun bindFunction(NameID nid, List<Type> paramTypes,
	      SyntacticElement elem) throws ResolveError {
	    Type receiver = null; // dummy
	    Type.Fun target = Type.T_FUN(null, Type.T_ANY, paramTypes);
	    Type.Fun candidate = null;

	    List<Type.Fun> targets = lookupMethod(nid);

	    for (Type.Fun ft : targets) {
	      Type funrec = ft.receiver;
	      if (receiver == funrec
	          || (receiver != null && funrec != null && Type.isSubtype(funrec,
	              receiver))) {
	        // receivers match up OK ...
	        if (ft.params.size() == paramTypes.size() && paramSubtypes(ft, target)
	            && (candidate == null || paramSubtypes(candidate, ft))) {
	          candidate = ft;
	        }
	      }
	    }

	    // Check whether we actually found something. If not, print a useful
	    // error message.
	    if (candidate == null) {
	      String msg = "no match for " + nid.name() + parameterString(paramTypes);
	      boolean firstTime = true;
	      int count = 0;
	      for (Type.Fun ft : targets) {
	        if (firstTime) {
	          msg += "\n\tfound: " + nid.name() + parameterString(ft.params);
	        } else {
	          msg += "\n\tand: " + nid.name() + parameterString(ft.params);
	        }
	        if (++count < targets.size()) {
	          msg += ",";
	        }
	      }

	      syntaxError(msg + "\n", filename, elem);
	    }

	    return candidate;
	  }

	  private boolean paramSubtypes(Type.Fun f1, Type.Fun f2) {
	    List<Type> f1_params = f1.params;
	    List<Type> f2_params = f2.params;
	    if (f1_params.size() == f2_params.size()) {
	      for (int i = 0; i != f1_params.size(); ++i) {
	        if (!Type.isSubtype(f1_params.get(i), f2_params.get(i))) {
	          return false;
	        }
	      }
	      return true;
	    }
	    return false;
	  }

	  private String parameterString(List<Type> paramTypes) {
	    String paramStr = "(";
	    boolean firstTime = true;
	    for (Type t : paramTypes) {
	      if (!firstTime) {
	        paramStr += ",";
	      }
	      firstTime = false;
	      paramStr += Type.toShortString(t);
	    }
	    return paramStr + ")";
	  }

	  protected List<Type.Fun> lookupMethod(NameID nid) throws ResolveError {
	    List<Type.Fun> matches = functions.get(nid);

	    if (matches == null) {
	      Module m = loader.loadModule(nid.module());
	      List<FunDecl> fmatches = m.functions(nid.name());
	      matches = new ArrayList<Type.Fun>();
	      for (FunDecl fd : fmatches) {
	        partResolve(m.id(), fd);
	        matches.add(fd.attribute(Attribute.FunType.class).type);
	      }
	    }

	    return matches;
	  }

	  protected Type resolve(UnOp uop, HashMap<String,Type> environment) throws ResolveError {
	    Type t = resolve(uop.mhs, environment);
	    switch (uop.op) {
	    case LENGTHOF:
	      checkSubtype(Type.T_SET(Type.T_ANY), t, uop.mhs);
	      Type.SetList sl = (Type.SetList) t;
	      return sl.element();
	    case NEG:
	      checkSubtype(Type.T_REAL, t, uop.mhs);
	      return t;
	    case NOT:
	      checkSubtype(Type.T_BOOL, t, uop.mhs);
	      return t;
	    }
	    syntaxError("unknown unary expression encountered", filename, uop);
	    return null;
	  }

	  protected Type resolve(BinOp bop, HashMap<String,Type> environment)
	      throws ResolveError {
	    Type lhs_t = resolve(bop.lhs, environment);
	    Type rhs_t = resolve(bop.rhs, environment);

	    // FIXME: really need to add coercions somehow

	    switch (bop.op) {
	    case ADD: {
	      if (Type.isSubtype(Type.T_SET(Type.T_ANY), lhs_t)
	          || Type.isSubtype(Type.T_SET(Type.T_ANY), rhs_t)) {
	        checkSubtype(Type.T_SET(Type.T_ANY), lhs_t, bop.lhs);
	        checkSubtype(Type.T_SET(Type.T_ANY), rhs_t, bop.rhs);
	        // need to update operation
	        bop.op = BOp.UNION;
	        return Type.leastUpperBound(lhs_t, rhs_t);
	      }
	    }
	    case SUB:
	    case DIV:
	    case MUL: {
	      checkSubtype(Type.T_REAL, lhs_t, bop.lhs);
	      checkSubtype(Type.T_REAL, rhs_t, bop.rhs);
	      return Type.leastUpperBound(lhs_t, rhs_t);
	    }    
	    case EQ:
	    case NEQ: {
	    	Type lub = Type.greatestLowerBound(lhs_t, rhs_t);
	    	if(lub == Type.T_VOID) {
	    		syntaxError("incomparable types: " + lhs_t + ", " + rhs_t,filename,bop);
	    	}
	    	return Type.T_BOOL;
	    }    
	    case LT:
	    case LTEQ:
	    case GT:
	    case GTEQ: {
	      checkSubtype(Type.T_REAL, lhs_t, bop.lhs);
	      checkSubtype(Type.T_REAL, rhs_t, bop.rhs);
	      return Type.T_BOOL;
	    }
	    case AND:
	    case OR: {
	      checkSubtype(Type.T_BOOL, lhs_t, bop.lhs);
	      checkSubtype(Type.T_BOOL, rhs_t, bop.rhs);
	      return Type.T_BOOL;
	    }
	    }

	    syntaxError("unknown binary expression encountered", filename, bop);
	    return null;
	  }

	  protected Type resolve(NaryOp nop, HashMap<String,Type> environment) {
	    if (nop.op == NOp.SUBLIST) {
	      Expr src = nop.arguments.get(0);
	      Expr start = nop.arguments.get(1);
	      Expr end = nop.arguments.get(2);
	      Type src_t = resolve(src, environment);
	      Type start_t = resolve(start, environment);
	      Type end_t = resolve(end, environment);
	      checkSubtype(Type.T_LIST(Type.T_ANY), src_t, src);
	      checkSubtype(Type.T_INT, start_t, start);
	      checkSubtype(Type.T_INT, end_t, end);
	      return src_t;
	    } else {
	      // Must be a set or list generator
	      Type lub = Type.T_VOID;
	      for (Expr e : nop.arguments) {
	        Type t = resolve(e, environment);
	        lub = Type.leastUpperBound(lub, t);
	      }

	      if (nop.op == NOp.SETGEN) {
	        return Type.T_SET(lub);
	      } else {
	        return Type.T_LIST(lub);
	      }
	    }
	  }

	  protected Type resolve(RecordGen rg, HashMap<String,Type> environment) {
	    HashMap<String, Type> types = new HashMap<String, Type>();

	    for (Map.Entry<String, Expr> f : rg.fields.entrySet()) {
	      Type t = resolve(f.getValue(), environment);
	      types.put(f.getKey(), t);
	    }

	    return Type.T_RECORD(types);
	  }

	  protected Type resolve(RecordAccess ra, HashMap<String,Type> environment) {
	    Type src = resolve(ra.lhs, environment);
	    Type.Record ert = Type.effectiveRecordType(src);
	    if (ert == null) {
	      syntaxError("expected record type, got " + src, filename, ra.lhs);
	    }
	    Type t = ert.types.get(ra.name);
	    if (t == null) {
	      syntaxError("no such field in type: " + ert, filename, ra);
	    }
	    return t;
	  }

	  protected Type resolve(DictionaryGen rg, HashMap<String,Type> environment) {
	    Type keyType = Type.T_VOID;
	    Type valueType = Type.T_VOID;

	    for (Pair<Expr, Expr> p : rg.pairs) {
	      Type kt = resolve(p.first(), environment);
	      Type vt = resolve(p.second(), environment);
	      keyType = Type.leastUpperBound(keyType, kt);
	      valueType = Type.leastUpperBound(valueType, vt);
	    }

	    return Type.T_DICTIONARY(keyType, valueType);
	  }

	  protected Type resolve(Access ra, HashMap<String,Type> environment) {
	    Type src = resolve(ra.src, environment);
	    Type idx = resolve(ra.index, environment);
	    Type.Dictionary edt = Type.effectiveDictionaryType(src);

	    if (edt == null) {
	      syntaxError("expected dictionary or list type, got " + src, filename,
	          ra.src);
	    }

	    checkSubtype(edt.key, idx, ra.index);

	    return edt.value;
	  }

	  protected Type resolve(TupleGen rg, HashMap<String,Type> environment) {
	    HashMap<String, Type> types = new HashMap<String, Type>();
	    // FIXME: add proper support for tuple types.
	    int idx = 0;
	    for (Expr e : rg.fields) {
	      Type t = resolve(e, environment);
	      types.put("$" + idx++, t);
	    }
	    return Type.T_RECORD(types);
	  }

}
