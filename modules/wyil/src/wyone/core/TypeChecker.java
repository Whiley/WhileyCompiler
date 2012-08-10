package wyone.core;

import java.util.*;

import static wyone.core.Expr.*;
import static wyone.core.SpecFile.*;
import wyone.util.*;
import static wyone.util.SyntaxError.*;

public class TypeChecker {
	private String filename;
	// The hierarchy holds, for each type, the set of its children
	private final HashMap<String,Set<String>> hierarchy = new HashMap<String,Set<String>>();
	
	// maps constructor names to their declared types.
	private final HashMap<String,Type.Term> constructors = new HashMap<String,Type.Term>();
	
	// globals contains the list of global variables
	//private final HashMap<String,Type> globals = new HashMap();
	
	public void check(SpecFile spec) {
		filename = spec.filename;
		hierarchy.clear();
				
		for(Decl d : spec.declarations) {
			if(d instanceof ClassDecl) {
				ClassDecl cd = (ClassDecl) d;
				List<String> children = cd.children;
				hierarchy.put(cd.name,new HashSet<String>(children));
			} else if(d instanceof TermDecl) {
				TermDecl td = (TermDecl) d;
				constructors.put(td.name, Type.T_TERM(td.name,td.unbounded,td.params));
			}
		}
		
		for(Decl d : spec.declarations) {
			if(d instanceof RewriteDecl) {
				check((RewriteDecl)d);
			}
		}
	}
	
	public void check(RewriteDecl rd) {			
		HashMap<String,Type> environment = rd.pattern.environment();
		
		for(RuleDecl rule : rd.rules) {
			check(rule,environment);
		}
	}
	
	public void check(RuleDecl rule, HashMap<String,Type> environment) {
		for(Pair<String,Expr> let : rule.lets) {			
			environment.put(let.first(), resolve(let.second(),environment));
		}
		if(rule.condition != null) {
			resolve(rule.condition,environment);
		}
		resolve(rule.result,environment);
	}
		
	protected Type resolve(Expr e, HashMap<String,Type> environment) {
	    Type type = null;
		try {
	      if (e instanceof Constant) {
	        type = resolve((Constant) e, environment);
	      } else if (e instanceof Variable) {
	        type = resolve((Variable) e, environment);
	      } else if (e instanceof UnOp) {
	        type = resolve((UnOp) e, environment);
	      } else if (e instanceof Constructor) {
	        type = resolve((Constructor) e, environment);
	      } else if(e instanceof TypeConst) {
	    	type = resolve((TypeConst)e, environment);  
	      } else if (e instanceof BinOp) {
	        type = resolve((BinOp) e, environment);
	      } else if (e instanceof NaryOp) {
	        type = resolve((NaryOp) e, environment);
	      } else if(e instanceof Comprehension) {
	    	type = resolve((Comprehension) e,environment);  	      
	      } else if (e instanceof ListAccess) {
	        type = resolve((ListAccess) e, environment);
	      } else if (e instanceof TermAccess) {
	        type = resolve((TermAccess) e, environment);
	      } else {
	        syntaxError("unknown expression encountered", filename, e);
	      }
	    } catch (SyntaxError se) {
	      throw se;
	    } catch (Exception ex) {	    	
	    	syntaxError("internal failure", filename, e, ex);
	    }
	    e.attributes().add(new Attribute.TypeAttr(type));
	    return type;
	  }

	  protected Type resolve(Constant c, HashMap<String,Type> environment) {
	    Object v = c.value;
	    if (v instanceof Boolean) {
	      return Type.T_BOOL;
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

	  protected Type resolve(Variable v, HashMap<String,Type> environment) {
	    Type v_t = environment.get(v.var);
	    if (v_t != null) { return v_t; }	    
	    syntaxError("variable not defined", filename, v);
	    return null;
	  }

	  protected Type resolve(Constructor ivk, HashMap<String,Type> environment) {
	    
		  ArrayList<Type> types = new ArrayList<Type>();

		  for (Expr arg : ivk.arguments) {
			  Type arg_t = resolve(arg, environment);
			  types.add(arg_t);
		  }

		  // Second, we assume it's not a local variable and look outside the
		  // scope.
		  
		  // TODO: type check parameter arguments
		  
		  Type.Term type = constructors.get(ivk.name);
		  if(type == null) {
			  syntaxError("function not declared",filename,ivk);
		  }
		  return type;
	  }
	 
	  protected Type resolve(TypeConst tc, HashMap<String,Type> environment)  {
		  return tc.type;
	  }
	  
	  protected Type resolve(UnOp uop, HashMap<String,Type> environment) {
	    if(uop.op == UOp.NOT) {
			// this is necessary to prevent type inference in the presence of
			// invert type tests. e.g. <code>!x~=Var(*) && y~=Var(*)</code>
			// should not update the type of x.
	    	environment = new HashMap<String,Type>(environment);
	    }
		Type t = resolve(uop.mhs, environment);
	    switch (uop.op) {
	    case LENGTHOF:
	      checkSubtype(Type.T_LISTANY, t, uop.mhs);
	      return Type.T_INT;
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

	  protected Type resolve(BinOp bop, HashMap<String,Type> environment) {
		  
		  if(bop.op == BOp.OR) {
			  // TODO: the following is needed because of type inference.
			  // unfortunately, it is still a bit of a hack.
			  environment = new HashMap<String,Type>(environment);
		  }
		  
	    Type lhs_t = resolve(bop.lhs, environment);
	    Type rhs_t = resolve(bop.rhs, environment);

	    // FIXME: really need to add coercions somehow

	    switch (bop.op) {
	    case ADD: {	     
	      checkSubtype(Type.T_REAL, lhs_t, bop.lhs);
	      checkSubtype(Type.T_REAL, rhs_t, bop.rhs);
	      return Type.leastUpperBound(lhs_t, rhs_t, hierarchy);
	    }	    
	    case DIV:
	    case MUL: {
	      checkSubtype(Type.T_REAL, lhs_t, bop.lhs);
	      checkSubtype(Type.T_REAL, rhs_t, bop.rhs);
	      return Type.leastUpperBound(lhs_t, rhs_t, hierarchy);
	    }    
	    case EQ:
	    case NEQ: {	    	
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
	    case APPEND: {
	    	checkSubtype(Type.T_LISTANY, lhs_t, bop.lhs);
		    checkSubtype(Type.T_LISTANY, rhs_t, bop.rhs);
		    return Type.leastUpperBound(lhs_t, rhs_t, hierarchy);
	    }
	    case TYPEEQ:{
	    	checkSubtype(lhs_t, rhs_t, bop.lhs);
	    	if(bop.lhs instanceof Variable) {
	    		// type inference
	    		Variable v = (Variable) bop.lhs;
	    		// FIXME: should compute glb here
	    		environment.put(v.var, rhs_t);	    		
	    	}
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
			checkSubtype(Type.T_LIST(Type.T_ANYTERM), src_t, src);
			checkSubtype(Type.T_INT, start_t, start);
			checkSubtype(Type.T_INT, end_t, end);
			return src_t;
		} else {
			// Must be a set or list generator
			Type lub = Type.T_VOID;
			for (Expr e : nop.arguments) {
				Type t = resolve(e, environment);
				lub = Type.leastUpperBound(lub, t, hierarchy);
			}

			return Type.T_LIST(lub);
		}
	}

	  protected Type resolve(Comprehension comp, HashMap<String,Type> environment) {
		  HashMap<String,Type> nenv = new HashMap(environment);
		  for(Pair<String,Expr> src : comp.sources) {
			  if(environment.containsKey(src.first())) {
				  syntaxError("variable " + src.first() + " already declared",filename,comp);
			  }
			  Type t = resolve(src.second(),nenv);
			  checkSubtype(Type.T_LIST(Type.T_ANYTERM),t, src.second());
			  Type.SetList sl = (Type.SetList) t;
			  nenv.put(src.first(), sl.element());
		  }
		  if(comp.condition != null) {
			  resolve(comp.condition,nenv);
		  }
		  if(comp.cop == COp.LISTCOMP) {
			  Type r_t = resolve(comp.value,nenv);
			  return Type.T_LIST(r_t);
		  }
		  return Type.T_BOOL;
	  }
	
	  protected Type resolve(ListAccess ra, HashMap<String,Type> environment) {
		  Type src_t = resolve(ra.src, environment);
		  Type idx_t = resolve(ra.index, environment);
		  if(!(src_t instanceof Type.List)) {
			  syntaxError("expected list of term type, got " + src_t, filename, ra.src);
		  }
		  checkSubtype(Type.T_INT,idx_t,ra.index);
		  Type.List rt = (Type.List)src_t; 	    		    
		  return rt.element;
	  }
	  
	  protected Type resolve(TermAccess ra, HashMap<String,Type> environment) {
		  Type src_t = resolve(ra.src, environment);		  
		  if(!(src_t instanceof Type.Term)) {
			  syntaxError("expected list of term type, got " + src_t, filename, ra.src);
		  }
		  Type.Term tt = (Type.Term) src_t;		  		  
		  if(ra.index >= tt.params.size()) {
			  syntaxError("term index out-of-bounds", filename, ra);
		  }		  	   
		  return tt.params.get(ra.index);
	  }
	  	 

	   /**
	   * Check whether t1 :> t2; that is, whether t2 is a subtype of t1.
	   * 
	   * @param t1
	   * @param t2
	   * @param elem
	   */
	  public void checkSubtype(Type t1, Type t2, SyntacticElement elem) {		  
	    if (!Type.isSubtype(t1, t2, hierarchy)) {
	      syntaxError("expecting type " + t1 + ", got type " + t2, filename, elem);
	    }
	  }
}
