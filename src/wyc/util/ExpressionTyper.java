package wyc.util;

import static wyil.util.ErrorMessages.*;
import static wyil.util.SyntaxError.syntaxError;
import static wyc.util.Context.internalFailure;
import static wyc.util.Context.syntaxError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wyc.Resolver;
import wyc.lang.*;
import wyil.lang.ModuleID;
import wyil.lang.NameID;
import wyil.lang.PkgID;
import wyil.lang.Type;
import wyil.lang.Value;
import wyil.util.Pair;
import wyil.util.ResolveError;
import wyil.util.SyntacticElement;
import wyil.util.SyntaxError;

/**
 * <p>
 * An expression typer is responsible for typing expressions in a given context.
 * The context varies and may represent an expression within a statement, or
 * within some global declaration (e.g. a constant or type definition). The key
 * point is that a local expression typer doesn't really care about the context
 * --- it just gets on with typing expressions.
 * </p>
 * <p>
 * The context must include the Whiley source file which contains the
 * expression, as well as the list of active import statements (i.e. those which
 * are active at the point where the expression is located in the source file).
 * </p>
 * 
 * <p>
 * In some cases, typing an expression must update the underlying objects to
 * reflect the latest knowledge. For example:
 * </p>
 * 
 * <pre>
 * {int} f({int} x, {int} y):
 *    return x+y
 * </pre>
 * 
 * <p>
 * Initially, the expression <code>x+y</code> is assumed to be arithmetic
 * addition. During type propagation, however, it becomes apparent that its
 * operands are both sets. Therefore, the underlying AST node is updated to
 * represent a set union.
 * </p>
 * 
 * <h3>References</h3>
 * <ul>
 * <li>
 * <p>
 * David J. Pearce and James Noble. Structural and Flow-Sensitive Types for
 * Whiley. Technical Report, Victoria University of Wellington, 2010.
 * </p>
 * </li>
 * </ul>
 * 
 * 
 * @author David J. Pearce
 * 
 */
public final class ExpressionTyper {
	private final Resolver resolver;
	private final Context context;
	
	public ExpressionTyper(Resolver resolver, Context context) {
		this.resolver = resolver;
		this.context = context;
	}
	
	public Context context() {
		return context;
	}
	
	public Pair<Expr, RefCountedHashMap<String, Nominal>> propagate(
			Expr expr, boolean sign,
			RefCountedHashMap<String, Nominal> environment) {
		
		if(expr instanceof Expr.UnOp) {
			return propagate((Expr.UnOp)expr,sign,environment);		
		} else if(expr instanceof Expr.BinOp) {  
			return propagate((Expr.BinOp)expr,sign,environment);
		} else {
			// for all others just default back to the base rules for expressions.
			expr = propagate(expr,environment);
			checkIsSubtype(Type.T_BOOL,expr);
			return new Pair(expr,environment);
		}		
	}
	
	private Pair<Expr, RefCountedHashMap<String, Nominal>> propagate(
			Expr.UnOp expr,
			boolean sign,
			RefCountedHashMap<String, Nominal> environment) {
		Expr.UnOp uop = (Expr.UnOp) expr; 
		if(uop.op == Expr.UOp.NOT) { 
			Pair<Expr,RefCountedHashMap<String, Nominal>> p = propagate(uop.mhs,!sign,environment);
			uop.mhs = p.first();			
			checkIsSubtype(Type.T_BOOL,uop.mhs);
			uop.type = Nominal.T_BOOL;
			return new Pair(uop,p.second());
		} else {
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),context,expr);
			return null; // deadcode
		}	
	}
	
	private Pair<Expr, RefCountedHashMap<String, Nominal>> propagate(
			Expr.BinOp bop,
			boolean sign,
			RefCountedHashMap<String, Nominal> environment) {		
		Expr.BOp op = bop.op;
		
		switch (op) {
		case AND:
		case OR:
		case XOR:
			return propagateNonLeafCondition(bop,sign,environment);
		case EQ:
		case NEQ:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case ELEMENTOF:
		case SUBSET:
		case SUBSETEQ:
		case IS:
			return propagateLeafCondition(bop,sign,environment);
		default:
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, bop);
			return null; // dead code
		}		
	}
	
	private Pair<Expr, RefCountedHashMap<String, Nominal>> propagateNonLeafCondition(
			Expr.BinOp bop,
			boolean sign,
			RefCountedHashMap<String, Nominal> environment) {
		Expr.BOp op = bop.op;
		Pair<Expr,RefCountedHashMap<String, Nominal>> p;
		boolean followOn = (sign && op == Expr.BOp.AND) || (!sign && op == Expr.BOp.OR);
		
		if(followOn) {			
			p = propagate(bop.lhs,sign,environment.clone());			
			bop.lhs = p.first();
			p = propagate(bop.rhs,sign,p.second());
			bop.rhs = p.first();
			environment = p.second();
		} else {
			// We could do better here
			p = propagate(bop.lhs,sign,environment.clone());
			bop.lhs = p.first();
			RefCountedHashMap<String, Nominal> local = p.second();
			// Recompue the lhs assuming that it is false. This is necessary to
			// generate the right environment going into the rhs, which is only
			// evaluated if the lhs is false.  For example:
			//
			// if(e is int && e > 0):
			//     //
			// else:
			//     // <-
			// 
			// In the false branch, we're determing the environment for 
			// !(e is int && e > 0).  This becomes !(e is int) || (e > 0) where 
			// on the rhs we require (e is int).
			p = propagate(bop.lhs,!sign,environment.clone());
			p = propagate(bop.rhs,sign,p.second());
			bop.rhs = p.first();
			environment = join(local,p.second());
		}
		
		checkIsSubtype(Type.T_BOOL,bop.lhs);
		checkIsSubtype(Type.T_BOOL,bop.rhs);	
		bop.srcType = Nominal.T_BOOL;
		
		return new Pair<Expr,RefCountedHashMap<String, Nominal>>(bop,environment);
	}
	
	private Pair<Expr, RefCountedHashMap<String, Nominal>> propagateLeafCondition(
			Expr.BinOp bop,
			boolean sign,
			RefCountedHashMap<String, Nominal> environment) {
		Expr.BOp op = bop.op;
		
		Expr lhs = propagate(bop.lhs,environment);
		Expr rhs = propagate(bop.rhs,environment);
		bop.lhs = lhs;
		bop.rhs = rhs;
		
		Type lhsRawType = lhs.result().raw();
		Type rhsRawType = rhs.result().raw();
		
		switch(op) {					
		case IS:
			// this one is slightly more difficult. In the special case that
			// we have a type constant on the right-hand side then we want
			// to check that it makes sense. Otherwise, we just check that
			// it has type meta.								
			
			if(rhs instanceof Expr.TypeVal) {									
				// yes, right-hand side is a constant
				Expr.TypeVal tv = (Expr.TypeVal) rhs;
				Type testRawType = tv.type.raw();					
				Nominal glb = Nominal.intersect(lhs.result(), tv.type);	
				
				if(Type.isSubtype(testRawType,lhsRawType)) {					
					// DEFINITE TRUE CASE										
					syntaxError(errorMessage(BRANCH_ALWAYS_TAKEN), context, bop);
				} else if (glb.raw() == Type.T_VOID) {				
					// DEFINITE FALSE CASE	
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsRawType, testRawType),
							context, bop);			
				} 
				
				// Finally, if the lhs is local variable then update its
				// type in the resulting environment. 
				if(lhs instanceof Expr.LocalVariable) {
					Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
					Nominal newType;
					if(sign) {
						newType = glb;
					} else {						
						newType = Nominal.intersect(lhs.result(), Nominal.Negation(tv.type));						
					}										
					environment = environment.put(lv.var,newType);
				}
			} else {
				// In this case, we can't update the type of the lhs since
				// we don't know anything about the rhs. It may be possible
				// to support bounds here in order to do that, but frankly
				// that's future work :)
				checkIsSubtype(Type.T_META,rhs);
			}	

			bop.srcType = lhs.result();
			break;
		case ELEMENTOF:			
			Type.EffectiveList listType = rhsRawType instanceof Type.EffectiveList ? (Type.EffectiveList) rhsRawType : null;
			Type.EffectiveSet setType = rhsRawType instanceof Type.EffectiveSet ? (Type.EffectiveSet) rhsRawType : null;			
			
			if (listType != null && !Type.isImplicitCoerciveSubtype(listType.element(), lhsRawType)) {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,listType.element()),
						context, bop);
			} else if (setType != null && !Type.isImplicitCoerciveSubtype(setType.element(), lhsRawType)) {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,setType.element()),
						context, bop);
			}						
			bop.srcType = rhs.result();
			break;
		case SUBSET:
		case SUBSETEQ:
		case LT:
		case LTEQ:
		case GTEQ:
		case GT:
			if(op == Expr.BOp.SUBSET || op == Expr.BOp.SUBSETEQ) {
				checkIsSubtype(Type.Set(Type.T_ANY,false),lhs);
				checkIsSubtype(Type.Set(Type.T_ANY,false),rhs);
			} else {
				checkIsSubtype(Type.T_REAL,lhs);
				checkIsSubtype(Type.T_REAL,rhs);
			}
			if(Type.isImplicitCoerciveSubtype(lhsRawType,rhsRawType)) {
				bop.srcType = lhs.result();
			} else if(Type.isImplicitCoerciveSubtype(rhsRawType,lhsRawType)) {
				bop.srcType = rhs.result();
			} else {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS),context,bop);	
				return null; // dead code
			}	
			break;
		case NEQ:
			// following is a sneaky trick for the special case below
			sign = !sign;
		case EQ:		
			
			// first, check for special case of e.g. x != null. This is then
			// treated the same as !(x is null) 
			
			if (lhs instanceof Expr.LocalVariable
					&& rhs instanceof Expr.Constant
					&& ((Expr.Constant) rhs).value == Value.V_NULL) {
				// bingo, special case
				Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
				Nominal newType;
				Nominal glb = Nominal.intersect(lhs.result(), Nominal.T_NULL);
				if(glb.raw() == Type.T_VOID) {
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS),context,bop);	
					return null;
				} else if(sign) {					
					newType = glb;
				} else {					
					newType = Nominal.intersect(lhs.result(), Nominal.T_NOTNULL);												
				}
				bop.srcType = lhs.result();
				environment = environment.put(lv.var,newType);
			} else {
				// handle general case
				if(Type.isImplicitCoerciveSubtype(lhsRawType,rhsRawType)) {
					bop.srcType = lhs.result();
				} else if(Type.isImplicitCoerciveSubtype(rhsRawType,lhsRawType)) {
					bop.srcType = rhs.result();
				} else {
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS),context,bop);	
					return null; // dead code
				}		
			}
		}			
		
		return new Pair(bop,environment);
	}
	
	public Expr propagate(Expr expr,
			RefCountedHashMap<String,Nominal> environment) {
		
		try {
			if(expr instanceof Expr.BinOp) {
				return propagate((Expr.BinOp) expr,environment); 
			} else if(expr instanceof Expr.UnOp) {
				return propagate((Expr.UnOp) expr,environment); 
			} else if(expr instanceof Expr.Comprehension) {
				return propagate((Expr.Comprehension) expr,environment); 
			} else if(expr instanceof Expr.Constant) {
				return propagate((Expr.Constant) expr,environment); 
			} else if(expr instanceof Expr.Convert) {
				return propagate((Expr.Convert) expr,environment); 
			} else if(expr instanceof Expr.Dictionary) {
				return propagate((Expr.Dictionary) expr,environment); 
			} else if(expr instanceof Expr.AbstractFunctionOrMethodOrMessage) {
				return propagate((Expr.AbstractFunctionOrMethodOrMessage) expr,environment); 
			} else if(expr instanceof Expr.AbstractInvoke) {
				return propagate((Expr.AbstractInvoke) expr,environment); 
			} else if(expr instanceof Expr.AbstractIndexAccess) {
				return propagate((Expr.AbstractIndexAccess) expr,environment); 
			} else if(expr instanceof Expr.AbstractLength) {
				return propagate((Expr.AbstractLength) expr,environment); 
			} else if(expr instanceof Expr.AbstractVariable) {
				return propagate((Expr.AbstractVariable) expr,environment); 
			} else if(expr instanceof Expr.List) {
				return propagate((Expr.List) expr,environment); 
			} else if(expr instanceof Expr.Set) {
				return propagate((Expr.Set) expr,environment); 
			} else if(expr instanceof Expr.SubList) {
				return propagate((Expr.SubList) expr,environment); 
			} else if(expr instanceof Expr.SubString) {
				return propagate((Expr.SubString) expr,environment); 
			} else if(expr instanceof Expr.AbstractDotAccess) {
				return propagate((Expr.AbstractDotAccess) expr,environment); 
			} else if(expr instanceof Expr.Dereference) {
				return propagate((Expr.Dereference) expr,environment); 
			} else if(expr instanceof Expr.Record) {
				return propagate((Expr.Record) expr,environment); 
			} else if(expr instanceof Expr.New) {
				return propagate((Expr.New) expr,environment); 
			} else if(expr instanceof Expr.Tuple) {
				return  propagate((Expr.Tuple) expr,environment); 
			} else if(expr instanceof Expr.TypeVal) {
				return propagate((Expr.TypeVal) expr,environment); 
			} 
		} catch(ResolveError e) {
			syntaxError(errorMessage(RESOLUTION_ERROR,e.getMessage()),context,expr,e);
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable e) {
			internalFailure(e.getMessage(),context,expr,e);
			return null; // dead code
		}		
		internalFailure("unknown expression: " + expr.getClass().getName(),context,expr);
		return null; // dead code
	}
	
	private Expr propagate(Expr.BinOp expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {
		
		// TODO: split binop into arithmetic and conditional operators. This
		// would avoid the following case analysis since conditional binary
		// operators and arithmetic binary operators actually behave quite
		// differently.
		
		switch(expr.op) {
		case AND:
		case OR:
		case XOR:
		case EQ:
		case NEQ:
		case LT:	
		case LTEQ:
		case GT:	
		case GTEQ:
		case ELEMENTOF:
		case SUBSET:	
		case SUBSETEQ:
		case IS:								
			return propagate(expr,true,environment).first();
		}
		
		Expr lhs = propagate(expr.lhs,environment);
		Expr rhs = propagate(expr.rhs,environment);
		expr.lhs = lhs;
		expr.rhs = rhs;
		Type lhsRawType = lhs.result().raw();
		Type rhsRawType = rhs.result().raw();
	
		boolean lhs_set = Type.isImplicitCoerciveSubtype(Type.Set(Type.T_ANY, false),lhsRawType);
		boolean rhs_set = Type.isImplicitCoerciveSubtype(Type.Set(Type.T_ANY, false),rhsRawType);		
		boolean lhs_list = Type.isImplicitCoerciveSubtype(Type.List(Type.T_ANY, false),lhsRawType);
		boolean rhs_list = Type.isImplicitCoerciveSubtype(Type.List(Type.T_ANY, false),rhsRawType);
		boolean lhs_str = Type.isSubtype(Type.T_STRING,lhsRawType);
		boolean rhs_str = Type.isSubtype(Type.T_STRING,rhsRawType);
		
		Type srcType;

		if(lhs_str || rhs_str) {
			
			switch(expr.op) {				
			case ADD:								
				expr.op = Expr.BOp.STRINGAPPEND;
			case STRINGAPPEND:
				break;
			default:			
				syntaxError("Invalid string operation: " + expr.op, context,
						expr);
			}
			
			srcType = Type.T_STRING;
		} else if(lhs_list && rhs_list) {
			checkIsSubtype(Type.List(Type.T_ANY,false),lhs);
			checkIsSubtype(Type.List(Type.T_ANY,false),rhs);
			
			switch(expr.op) {	
			case ADD:
				expr.op = Expr.BOp.LISTAPPEND;
			case LISTAPPEND:				
				srcType = Type.Union(lhsRawType,rhsRawType);
				break;
			default:
				syntaxError("invalid list operation: " + expr.op,context,expr);	
				return null; // dead-code
			}										
		} else if(lhs_set && rhs_set) {	
			checkIsSubtype(Type.Set(Type.T_ANY,false),lhs);
			checkIsSubtype(Type.Set(Type.T_ANY,false),rhs);						
			
			// FIXME: something tells me there should be a function for doing
			// this.  Perhaps effectiveSetType?
			
			if(lhs_list) {
				 Type.EffectiveList tmp = (Type.EffectiveList) lhsRawType;
				 lhsRawType = Type.Set(tmp.element(),false);
			} 
			
			if(rhs_list) {
				 Type.EffectiveList tmp = (Type.EffectiveList) rhsRawType;
				 rhsRawType = Type.Set(tmp.element(),false);
			}  
			
			// FIXME: loss of nominal information here
			Type.EffectiveSet ls = (Type.EffectiveSet) lhsRawType;
			Type.EffectiveSet rs = (Type.EffectiveSet) rhsRawType;	
			
			switch(expr.op) {				
				case ADD:																				
					expr.op = Expr.BOp.UNION;					
				case UNION:					
					// TODO: this forces unnecessary coercions, which would be
					// good to remove.
					srcType = Type.Set(Type.Union(ls.element(),rs.element()),false);					
					break;
				case BITWISEAND:																				
					expr.op = Expr.BOp.INTERSECTION;
				case INTERSECTION:
					// FIXME: this is just plain wierd.
					if(Type.isSubtype(lhsRawType, rhsRawType)) {
						srcType = rhsRawType;
					} else {
						srcType = lhsRawType;
					}					
					break;
				case SUB:																				
					expr.op = Expr.BOp.DIFFERENCE;
				case DIFFERENCE:
					srcType = lhsRawType;
					break;								
				default:
					syntaxError("invalid set operation: " + expr.op,context,expr);	
					return null; // deadcode
			}							
		} else {			
			switch(expr.op) {
			case IS:
			case AND:
			case OR:
			case XOR:
				return propagate(expr,true,environment).first();				
			case BITWISEAND:
			case BITWISEOR:
			case BITWISEXOR:
				checkIsSubtype(Type.T_BYTE,lhs);
				checkIsSubtype(Type.T_BYTE,rhs);
				srcType = Type.T_BYTE;
				break;
			case LEFTSHIFT:
			case RIGHTSHIFT:
				checkIsSubtype(Type.T_BYTE,lhs);
				checkIsSubtype(Type.T_INT,rhs);
				srcType = Type.T_BYTE;
				break;
			case RANGE:
				checkIsSubtype(Type.T_INT,lhs);
				checkIsSubtype(Type.T_INT,rhs);
				srcType = Type.List(Type.T_INT, false);
				break;
			case REM:
				checkIsSubtype(Type.T_INT,lhs);
				checkIsSubtype(Type.T_INT,rhs);
				srcType = Type.T_INT;
				break;			
			default:
				// all other operations go through here
				if(Type.isImplicitCoerciveSubtype(lhsRawType,rhsRawType)) {
					checkIsSubtype(Type.T_REAL,lhs);
					if(Type.isSubtype(Type.T_CHAR, lhsRawType)) {
						srcType = Type.T_INT;
					} else if(Type.isSubtype(Type.T_INT, lhsRawType)) {
						srcType = Type.T_INT;
					} else {
						srcType = Type.T_REAL;
					}				
				} else {
					checkIsSubtype(Type.T_REAL,lhs);
					checkIsSubtype(Type.T_REAL,rhs);				
					if(Type.isSubtype(Type.T_CHAR, rhsRawType)) {
						srcType = Type.T_INT;
					} else if(Type.isSubtype(Type.T_INT, rhsRawType)) {
						srcType = Type.T_INT;
					} else {
						srcType = Type.T_REAL;
					}
				}				
			}
		}	
		
		// FIXME: loss of nominal information
		expr.srcType = Nominal.construct(srcType,srcType);
		
		return expr;
	}
	
	private Expr propagate(Expr.UnOp expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {
		
		if(expr.op == Expr.UOp.NOT) {
			// hand off to special method for conditions
			return propagate(expr,true,environment).first();	
		}
		
		Expr src = propagate(expr.mhs, environment);
		expr.mhs = src;
		
		switch(expr.op) {
		case NEG:
			checkIsSubtype(Type.T_REAL,src);			
			break;
		case INVERT:
			checkIsSubtype(Type.T_BYTE,src);
			break;
				
		default:		
			internalFailure(
					"unknown operator: " + expr.op.getClass().getName(),
					context, expr);
		}
		
		expr.type = src.result();		
		
		return expr;
	}
	
	private Expr propagate(Expr.Comprehension expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {
		
		ArrayList<Pair<String,Expr>> sources = expr.sources;
		RefCountedHashMap<String,Nominal> local = environment.clone();
		for(int i=0;i!=sources.size();++i) {
			Pair<String,Expr> p = sources.get(i);
			Expr e = propagate(p.second(),local);			
			p = new Pair<String,Expr>(p.first(),e);
			sources.set(i,p);
			Nominal element;
			Nominal type = e.result();
			Nominal.EffectiveList listType = resolver.expandAsEffectiveList(type);
			Nominal.EffectiveSet setType = resolver.expandAsEffectiveSet(type);
			if(listType != null) {
				element = listType.element();
			} else if(setType != null) {
				element = setType.element();
			} else {
				syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION),context,e);
				return null; // dead code
			}
			// update environment for subsequent source expressions, the
			// condition and the value.
			local = local.put(p.first(),element);
		}
		
		if(expr.condition != null) {
			expr.condition = propagate(expr.condition,local);
		}
		
		if (expr.cop == Expr.COp.SETCOMP || expr.cop == Expr.COp.LISTCOMP) {						
			expr.value = propagate(expr.value,local);
			expr.type = Nominal.Set(expr.value.result(), false);
		} else {
			expr.type = Nominal.T_BOOL;
		}
		
		local.free();				
		
		return expr;
	}
	
	private Expr propagate(Expr.Constant expr,
			RefCountedHashMap<String,Nominal> environment) {
		return expr;
	}

	private Expr propagate(Expr.Convert c,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {
		c.expr = propagate(c.expr,environment);		
		c.type = resolver.resolveAsType(c.unresolvedType, context);
		Type from = c.expr.result().raw();		
		Type to = c.type.raw();
		if (!Type.isExplicitCoerciveSubtype(to, from)) {			
			syntaxError(errorMessage(SUBTYPE_ERROR, to, from), context, c);
		}	
		return c;
	}
	
	private Expr propagate(Expr.AbstractFunctionOrMethodOrMessage expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {
		
		if(expr instanceof Expr.FunctionOrMethodOrMessage) {
			return expr;
		} 		
		
		Pair<NameID, Nominal.FunctionOrMethodOrMessage> p;
		
		if (expr.paramTypes != null) {
			ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
			for (UnresolvedType t : expr.paramTypes) {
				paramTypes.add(resolver.resolveAsType(t, context));
			}
			// FIXME: clearly a bug here in the case of message reference
			p = (Pair) resolver
					.resolveAsFunctionOrMethod(expr.name, paramTypes, context);			
		} else {
			p = resolver.resolveAsFunctionOrMethodOrMessage(expr.name, context);			
		}
		
		expr = new Expr.FunctionOrMethodOrMessage(p.first(),expr.paramTypes,expr.attributes());
		expr.type = p.second();
		return expr;
	}
	
	private Expr propagate(Expr.AbstractInvoke expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {
		
		// first, propagate through receiver and parameters.
		
		Expr receiver = expr.qualification;
		
		if(receiver != null) {
			receiver = propagate(receiver,environment);
			expr.qualification = receiver;						
		}
		
		ArrayList<Expr> exprArgs = expr.arguments;
		ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
		for(int i=0;i!=exprArgs.size();++i) {
			Expr arg = propagate(exprArgs.get(i),environment);
			exprArgs.set(i, arg);
			paramTypes.add(arg.result());			
		}
		
		// second, determine whether we already have a fully qualified name and
		// then lookup the appropriate function.
		
		if(receiver instanceof Expr.ModuleAccess) {
			// Yes, this function or method is qualified
			Expr.ModuleAccess ma = (Expr.ModuleAccess) receiver;
			NameID name = new NameID(ma.mid,expr.name);
			Nominal.FunctionOrMethod funType = resolver.resolveAsFunctionOrMethod(name,  paramTypes);			
			if(funType instanceof Nominal.Function) {
				Expr.FunctionCall r = new Expr.FunctionCall(name, ma, exprArgs, expr.attributes());
				r.functionType = (Nominal.Function) funType;				
				return r;
			} else {
				Expr.MethodCall r = new Expr.MethodCall(name, ma, exprArgs, expr.attributes());
				r.methodType = (Nominal.Method) funType;
				return r;
			}
		} else if(receiver != null) {
			
			// function is qualified, so this is used as the scope for resolving
			// what the function is.
			
			Nominal.EffectiveRecord recType = resolver.expandAsEffectiveRecord(expr.qualification.result());
			
			if(recType != null) {
				
				Nominal fieldType = recType.field(expr.name);
				
				if(fieldType == null) {
					syntaxError(errorMessage(RECORD_MISSING_FIELD),context,expr);
				} else if(!(fieldType instanceof Nominal.FunctionOrMethod)) {
					syntaxError("function or method type expected",context,expr);
				}
				
				Nominal.FunctionOrMethod funType = (Nominal.FunctionOrMethod) fieldType;
				Expr.RecordAccess ra = new Expr.RecordAccess(receiver, expr.name, expr.attributes());
				ra.srcType = recType;
						
				if(funType instanceof Nominal.Method) { 
					Expr.IndirectMethodCall nexpr = new Expr.IndirectMethodCall(ra,expr.arguments,expr.attributes());
					// FIXME: loss of nominal information
					nexpr.methodType = (Nominal.Method) funType; 
					return nexpr;
				} else {
					Expr.IndirectFunctionCall nexpr = new Expr.IndirectFunctionCall(ra,expr.arguments,expr.attributes());
					// FIXME: loss of nominal information
					nexpr.functionType = (Nominal.Function) funType;
					return nexpr;
				}
				
			} else {
				// In this case, we definitely have an object type. 
				
				Type.Reference procType = checkType(expr.qualification.result().raw(),Type.Reference.class,receiver);							

				// ok, it's a message send (possibly indirect)
				Nominal type = environment.get(expr.name);
				Nominal.Message msgType = type != null ? resolver.expandAsMessage(type) : null;
				
				// FIXME: bad idea to use instanceof Nominal.Message here
				if(msgType != null) {
					// ok, matching local variable of message type.
					List<Nominal> funTypeParams = msgType.params();
					// FIXME: is this broken since should be equivalent, not subtype?
					checkIsSubtype(msgType.receiver(),expr.qualification);
					if(paramTypes.size() != funTypeParams.size()) {
						syntaxError("insufficient arguments to message send",context,expr);
					}
					for (int i = 0; i != funTypeParams.size(); ++i) {
						Nominal fpt = funTypeParams.get(i);
						checkIsSubtype(fpt, paramTypes.get(i), exprArgs.get(i));
					}
					
					Expr.LocalVariable lv = new Expr.LocalVariable(expr.name,expr.attributes());
					lv.type = type;								
					Expr.IndirectMessageSend nexpr = new Expr.IndirectMessageSend(
							lv, expr.qualification, expr.arguments,
							expr.synchronous, expr.attributes());
					nexpr.messageType = msgType; 
					return nexpr;					
				} else {

					Pair<NameID, Nominal.Message> p = resolver
							.resolveAsMessage(expr.name, procType, paramTypes,
									context);				
					Expr.MessageSend r = new Expr.MessageSend(p.first(), receiver,
							exprArgs, expr.synchronous, expr.attributes());			
					r.messageType = p.second();
					return r;
				}
			}
		} else {

			// no, function is not qualified ... so, it's either a local
			// variable or a function call the location of which we need to
			// identify.

			Nominal type = environment.get(expr.name);			
			Nominal.FunctionOrMethod funType = type != null ? resolver.expandAsFunctionOrMethod(type) : null;
			
			// FIXME: bad idea to use instanceof Nominal.FunctionOrMethod here
			if(funType != null) {
				// ok, matching local variable of function type.				
				List<Nominal> funTypeParams = funType.params();
				if(paramTypes.size() != funTypeParams.size()) {
					syntaxError("insufficient arguments to function call",context,expr);
				}
				for (int i = 0; i != funTypeParams.size(); ++i) {
					Nominal fpt = funTypeParams.get(i);
					checkIsSubtype(fpt, paramTypes.get(i), exprArgs.get(i));
				}
				
				Expr.LocalVariable lv = new Expr.LocalVariable(expr.name,expr.attributes());
				lv.type = type;
							
				if(funType instanceof Nominal.Method) { 
					Expr.IndirectMethodCall nexpr = new Expr.IndirectMethodCall(lv,expr.arguments,expr.attributes());				
					nexpr.methodType = (Nominal.Method) funType; 
					return nexpr;
				} else {
					Expr.IndirectFunctionCall nexpr = new Expr.IndirectFunctionCall(lv,expr.arguments,expr.attributes());
					nexpr.functionType = (Nominal.Function) funType;
					return nexpr;					
				}

			} else {				
				// no matching local variable, so attempt to resolve as direct
				// call.
				Pair<NameID, Nominal.FunctionOrMethod> p = resolver.resolveAsFunctionOrMethod(expr.name, paramTypes, context);
				funType = p.second();							
				if(funType instanceof Nominal.Function) {					
					Expr.FunctionCall mc = new Expr.FunctionCall(p.first(), null, exprArgs, expr.attributes());					
					mc.functionType = (Nominal.Function) funType;
					return mc;
				} else {								
					Expr.MethodCall mc = new Expr.MethodCall(p.first(), null, exprArgs, expr.attributes());					
					mc.methodType = (Nominal.Method) funType;					
					return mc;
				}																				
			}
		}		
	}			
	
	private Expr propagate(Expr.AbstractIndexAccess expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {			
		expr.src = propagate(expr.src,environment);
		expr.index = propagate(expr.index,environment);		
		Nominal srcType = expr.src.result();
		Type rawSrcType = srcType.raw();			
		
		// First, check whether this is still only an abstract access and, in
		// such case, upgrade it to the appropriate access expression.
		
		if (!(expr instanceof Expr.StringAccess
				|| expr instanceof Expr.ListAccess || expr instanceof Expr.DictionaryAccess)) {
			// first time through
			if (Type.isImplicitCoerciveSubtype(Type.T_STRING, rawSrcType)) {
				expr = new Expr.StringAccess(expr.src, expr.index,
						expr.attributes());
			} else if (Type.isImplicitCoerciveSubtype(
					Type.List(Type.T_ANY, false), rawSrcType)) {
				expr = new Expr.ListAccess(expr.src, expr.index,
						expr.attributes());
			} else if (Type.isImplicitCoerciveSubtype(
					Type.Dictionary(Type.T_ANY, Type.T_ANY), rawSrcType)) {
				expr = new Expr.DictionaryAccess(expr.src, expr.index,
						expr.attributes());
			} else {
				syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION), context, expr.src);
			}
		}
		
		// Second, determine the expanded src type for this access expression
		// and check the key value.
		
		if(expr instanceof Expr.StringAccess) {
			checkIsSubtype(Type.T_STRING,expr.src);	
			checkIsSubtype(Type.T_INT,expr.index);				
		} else if(expr instanceof Expr.ListAccess) {
			Expr.ListAccess la = (Expr.ListAccess) expr; 
			Nominal.EffectiveList list = resolver.expandAsEffectiveList(srcType);			
			if(list == null) {
				syntaxError(errorMessage(INVALID_LIST_EXPRESSION),context,expr);				
			}
			checkIsSubtype(Type.T_INT,expr.index);	
			la.srcType = list;			
		} else {
			Expr.DictionaryAccess da = (Expr.DictionaryAccess) expr; 
			Nominal.EffectiveDictionary dict = resolver.expandAsEffectiveDictionary(srcType);
			if(dict == null) {
				syntaxError(errorMessage(INVALID_DICTIONARY_EXPRESSION),context,expr);
			}			
			checkIsSubtype(dict.key(),expr.index);			
			da.srcType = dict;						
		}
		
		return expr;
	}
	
	private Expr propagate(Expr.AbstractLength expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {			
		expr.src = propagate(expr.src,environment);			
		Nominal srcType = expr.src.result();
		Type rawSrcType = srcType.raw();				
	
		// First, check whether this is still only an abstract access and, in
		// such case, upgrade it to the appropriate access expression.

		if (Type.isImplicitCoerciveSubtype(Type.T_STRING, rawSrcType)) {
			if(!(expr instanceof Expr.StringLength)) {
				expr = new Expr.StringLength(expr.src, expr.attributes());
			}
		} else if (Type.isImplicitCoerciveSubtype(Type.List(Type.T_ANY, false),
				rawSrcType)) {
			if(!(expr instanceof Expr.ListLength)) {
				expr = new Expr.ListLength(expr.src, expr.attributes());
			}
		} else if (Type.isImplicitCoerciveSubtype(Type.Set(Type.T_ANY, false),
				rawSrcType)) {
			if(!(expr instanceof Expr.SetLength)) {
				expr = new Expr.SetLength(expr.src, expr.attributes());
			}
		} else if (Type.isImplicitCoerciveSubtype(
				Type.Dictionary(Type.T_ANY, Type.T_ANY), rawSrcType)) {
			if(!(expr instanceof Expr.DictionaryLength)) {
				expr = new Expr.DictionaryLength(expr.src, expr.attributes());
			}
		} else {
			syntaxError("found " + expr.src.result().nominal()
					+ ", expected string, set, list or dictionary.", context,
					expr.src);
		}

		// Second, determine the expanded src type for this access expression
		// and check the key value.

		if(expr instanceof Expr.StringLength) {
			checkIsSubtype(Type.T_STRING,expr.src);								
		} else if(expr instanceof Expr.ListLength) {
			Expr.ListLength ll = (Expr.ListLength) expr; 
			Nominal.EffectiveList list = resolver.expandAsEffectiveList(srcType);			
			if(list == null) {
				syntaxError(errorMessage(INVALID_LIST_EXPRESSION),context,expr);				
			}
			ll.srcType = list;
		} else if(expr instanceof Expr.SetLength) {
			Expr.SetLength sl = (Expr.SetLength) expr; 
			Nominal.EffectiveSet set = resolver.expandAsEffectiveSet(srcType);			
			if(set == null) {
				syntaxError(errorMessage(INVALID_SET_EXPRESSION),context,expr);				
			}
			sl.srcType = set;			
		} else {
			Expr.DictionaryLength dl = (Expr.DictionaryLength) expr; 
			Nominal.EffectiveDictionary dict = resolver.expandAsEffectiveDictionary(srcType);
			if(dict == null) {
				syntaxError(errorMessage(INVALID_DICTIONARY_EXPRESSION),context,expr);
			}				
			dl.srcType = dict;	
		}
		
		return expr;
	}
	
	private Expr propagate(Expr.AbstractVariable expr,
			RefCountedHashMap<String, Nominal> environment) throws ResolveError {

		Nominal type = environment.get(expr.var);

		if (expr instanceof Expr.LocalVariable) {
			Expr.LocalVariable lv = (Expr.LocalVariable) expr;			
			lv.type = type;			
			return lv;
		} else if (type != null) {
			// yes, this is a local variable
			Expr.LocalVariable lv = new Expr.LocalVariable(expr.var,
					expr.attributes());	
			lv.type = type;			
			return lv;
		} else {
			// This variable access may correspond to an external access.
			// Therefore, we must determine which module this
			// is, and update the tree accordingly.
			try {
				NameID nid = resolver.resolveAsName(expr.var, context);					
				Expr.ConstantAccess ca = new Expr.ConstantAccess(null, expr.var, nid,
						expr.attributes());
				ca.value = resolver.resolveAsConstant(nid);
				return ca;
			} catch (ResolveError err) {
			}
			// In this case, we may still be OK if this corresponds to an
			// explicit module or package access.
			try {
				ModuleID mid = resolver.resolveAsModule(expr.var, context);
				return new Expr.ModuleAccess(null, expr.var, mid,
						expr.attributes());
			} catch (ResolveError err) {
			}
			PkgID pid = new PkgID(expr.var);
			if (resolver.isPackage(pid)) {
				return new Expr.PackageAccess(null, expr.var, pid,
						expr.attributes());
			}
			// ok, failed.
			syntaxError(errorMessage(UNKNOWN_VARIABLE), context, expr);
			return null; // deadcode
		}
	}
	
	private Expr propagate(Expr.Set expr,
			RefCountedHashMap<String,Nominal> environment) {
		Nominal element = Nominal.T_VOID;		
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = propagate(exprs.get(i),environment);
			Nominal t = e.result();
			exprs.set(i,e);
			element = Nominal.Union(t,element);			
		}
		
		expr.type = Nominal.Set(element,false);
		
		return expr;
	}
	
	private Expr propagate(Expr.List expr,
			RefCountedHashMap<String,Nominal> environment) {		
		Nominal element = Nominal.T_VOID;		
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = propagate(exprs.get(i),environment);
			Nominal t = e.result();
			exprs.set(i,e);
			element = Nominal.Union(t,element);			
		}	
		
		expr.type = Nominal.List(element,false);
				
		return expr;
	}
	
	
	private Expr propagate(Expr.Dictionary expr,
			RefCountedHashMap<String,Nominal> environment) {
		Nominal keyType = Nominal.T_VOID;
		Nominal valueType = Nominal.T_VOID;		
				
		ArrayList<Pair<Expr,Expr>> exprs = expr.pairs;
		for(int i=0;i!=exprs.size();++i) {
			Pair<Expr,Expr> p = exprs.get(i);
			Expr key = propagate(p.first(),environment);
			Expr value = propagate(p.second(),environment);
			Nominal kt = key.result();
			Nominal vt = value.result();
			exprs.set(i,new Pair<Expr,Expr>(key,value));
			
			keyType = Nominal.Union(kt,keyType);			
			valueType = Nominal.Union(vt,valueType);
		}
		
		expr.type = Nominal.Dictionary(keyType,valueType);
		
		return expr;
	}
	

	private Expr propagate(Expr.Record expr,
			RefCountedHashMap<String,Nominal> environment) {
		
		HashMap<String,Expr> exprFields = expr.fields;
		HashMap<String,Nominal> fieldTypes = new HashMap<String,Nominal>();
				
		ArrayList<String> fields = new ArrayList<String>(exprFields.keySet());
		for(String field : fields) {
			Expr e = propagate(exprFields.get(field),environment);
			Nominal t = e.result();
			exprFields.put(field,e);
			fieldTypes.put(field,t);				
		}		
		
		expr.type = Nominal.Record(false,fieldTypes);
		
		return expr;
	}
	
	private Expr propagate(Expr.Tuple expr,
			RefCountedHashMap<String,Nominal> environment) {
		ArrayList<Expr> exprFields = expr.fields;
		ArrayList<Nominal> fieldTypes = new ArrayList<Nominal>();
				
		for(int i=0;i!=exprFields.size();++i) {
			Expr e = propagate(exprFields.get(i),environment);
			Nominal t = e.result();
			exprFields.set(i,e);
			fieldTypes.add(t);			
		}
				
		expr.type = Nominal.Tuple(fieldTypes);
		
		return expr;
	}
	
	private Expr propagate(Expr.SubList expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {	
		
		expr.src = propagate(expr.src,environment);
		expr.start = propagate(expr.start,environment);
		expr.end = propagate(expr.end,environment);
		
		checkIsSubtype(Type.List(Type.T_ANY, false),expr.src);
		checkIsSubtype(Type.T_INT,expr.start);
		checkIsSubtype(Type.T_INT,expr.end);
		
		expr.type = resolver.expandAsEffectiveList(expr.src.result());
		if(expr.type == null) {
			// must be a substring
			return new Expr.SubString(expr.src,expr.start,expr.end,expr.attributes());
		}
		
		return expr;
	}
	
	private Expr propagate(Expr.SubString expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {	
		
		expr.src = propagate(expr.src,environment);
		expr.start = propagate(expr.start,environment);
		expr.end = propagate(expr.end,environment);
		
		checkIsSubtype(Type.T_STRING,expr.src);
		checkIsSubtype(Type.T_INT,expr.start);
		checkIsSubtype(Type.T_INT,expr.end);
		
		return expr;
	}
	
	private Expr propagate(Expr.AbstractDotAccess expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {	
				
		if (expr instanceof Expr.PackageAccess
				|| expr instanceof Expr.ModuleAccess) {			
			// don't need to do anything in these cases.
			return expr;
		}
		
		Expr src = expr.src;
		
		if(src != null) {
			src = propagate(expr.src,environment);
			expr.src = src;
		}
				
		if(expr instanceof Expr.RecordAccess) {			
			return propagate((Expr.RecordAccess)expr,environment);
		} else if(expr instanceof Expr.ConstantAccess) {
			return propagate((Expr.ConstantAccess)expr,environment);
		} else if(src instanceof Expr.PackageAccess) {
			// either a package access, module access or constant access
			// This variable access may correspond to an external access.			
			Expr.PackageAccess pa = (Expr.PackageAccess) src; 
			PkgID pid = pa.pid.append(expr.name);
			if (resolver.isPackage(pid)) {
				return new Expr.PackageAccess(pa, expr.name, pid,
						expr.attributes());
			}			
			ModuleID mid = new ModuleID(pa.pid,expr.name);
			if (resolver.isModule(mid)) {
				return new Expr.ModuleAccess(pa, expr.name, mid,
						expr.attributes());
			} else {
				syntaxError(errorMessage(INVALID_PACKAGE_ACCESS), context, expr);
				return null; // deadcode
			}		
		} else if(src instanceof Expr.ModuleAccess) {
			// must be a constant access
			Expr.ModuleAccess ma = (Expr.ModuleAccess) src; 													
			NameID nid = new NameID(ma.mid,expr.name);
			if (resolver.isName(nid)) {
				Expr.ConstantAccess ca = new Expr.ConstantAccess(ma,
						expr.name, nid, expr.attributes());
				ca.value = resolver.resolveAsConstant(nid);
				return ca;
			}						
			syntaxError(errorMessage(INVALID_MODULE_ACCESS),context,expr);			
			return null; // deadcode
		} else {
			// must be a RecordAccess
			Expr.RecordAccess ra = new Expr.RecordAccess(src,expr.name,expr.attributes());			
			return propagate(ra,environment);
		}
	}
		
	private Expr propagate(Expr.RecordAccess ra,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {
		Nominal srcType = ra.src.result();
		Nominal.EffectiveRecord recType = resolver.expandAsEffectiveRecord(srcType);
		if(recType == null) {
			syntaxError(errorMessage(RECORD_TYPE_REQUIRED,srcType.raw()),context,ra);
		} 
		Nominal fieldType = recType.field(ra.name);
		if(fieldType == null) {
			syntaxError(errorMessage(RECORD_MISSING_FIELD),context,ra);
		}
		ra.srcType = recType;		
		return ra;
	}	
	
	private Expr propagate(Expr.ConstantAccess expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {
		// we don't need to do anything here, since the value is already
		// resolved by case for AbstractDotAccess.
		return expr;
	}			

	private Expr propagate(Expr.Dereference expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {
		Expr src = propagate(expr.src,environment);
		expr.src = src;
		Nominal.Reference srcType = resolver.expandAsReference(src.result());
		if(srcType == null) {
			syntaxError("invalid reference expression",context,src);
		}
		expr.srcType = srcType;		
		return expr;
	}
	
	private Expr propagate(Expr.New expr,
			RefCountedHashMap<String,Nominal> environment) {
		expr.expr = propagate(expr.expr,environment);
		expr.type = Nominal.Reference(expr.expr.result());
		return expr;
	}
	
	private Expr propagate(Expr.TypeVal expr,
			RefCountedHashMap<String,Nominal> environment) throws ResolveError {
		expr.type = resolver.resolveAsType(expr.unresolvedType, context); 
		return expr;
	}
	
	private <T extends Type> T checkType(Type t, Class<T> clazz,
			SyntacticElement elem) {
		if (clazz.isInstance(t)) {
			return (T) t;
		} else {
			syntaxError(errorMessage(SUBTYPE_ERROR, clazz.getName().replace('$', '.'), t),
					context, elem);
			return null;
		}
	}
	
	// Check t1 :> t2
	private void checkIsSubtype(Nominal t1, Nominal t2,
			SyntacticElement elem) {
		if (!Type.isImplicitCoerciveSubtype(t1.raw(), t2.raw())) {
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.nominal()),
					context, elem);
		}
	}	
	
	private void checkIsSubtype(Nominal t1, Expr t2) {
		if (!Type.isImplicitCoerciveSubtype(t1.raw(), t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.result()
							.nominal()), context, t2);
		}
	}
	
	private void checkIsSubtype(Type t1, Expr t2) {
		if (!Type.isImplicitCoerciveSubtype(t1, t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(errorMessage(SUBTYPE_ERROR, t1, t2.result().nominal()),
					context, t2);
		}
	}
	
	private static final RefCountedHashMap<String,Nominal> BOTTOM = new RefCountedHashMap<String,Nominal>();
	
	private static final RefCountedHashMap<String, Nominal> join(
			RefCountedHashMap<String, Nominal> lhs,
			RefCountedHashMap<String, Nominal> rhs) {
		
		// first, need to check for the special bottom value case.
		
		if(lhs == BOTTOM) {
			return rhs;
		} else if(rhs == BOTTOM) {
			return lhs;
		}
		
		// ok, not bottom so compute intersection.
		
		lhs.free();
		rhs.free(); 		
		
		RefCountedHashMap<String,Nominal> result = new RefCountedHashMap<String,Nominal>();
		for(String key : lhs.keySet()) {
			if(rhs.containsKey(key)) {
				Nominal lhs_t = lhs.get(key);
				Nominal rhs_t = rhs.get(key);				
				result.put(key, Nominal.Union(lhs_t, rhs_t));
			}
		}
		
		return result;
	}	
}
