package wyc.builder;

import static wyc.lang.WhileyFile.*;
import static wyil.util.ErrorMessages.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import wyil.lang.WyilFile;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.ResolveError;
import wybs.util.Trie;
import wyc.lang.*;
import wyc.lang.WhileyFile.Context;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.lang.Value;
import wyil.util.Pair;

/**
 * <p>
 * A local resolver is responsible for typing and resolving names for
 * expressions in a given context. The context varies and may represent an
 * expression within a statement, or within some global declaration (e.g. a
 * constant or type definition). The key point is that a local expression typer
 * doesn't really care about the context --- it just gets on with typing
 * expressions.
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
public abstract class LocalResolver extends AbstractResolver {
	
	public LocalResolver(WhileyBuilder builder) {
		super(builder);
	}			
	
	public Pair<Expr, Environment> resolve(Expr expr, boolean sign,
			Environment environment, Context context) {
		
		if(expr instanceof Expr.UnOp) {
			return resolve((Expr.UnOp)expr,sign,environment,context);		
		} else if(expr instanceof Expr.BinOp) {  
			return resolve((Expr.BinOp)expr,sign,environment,context);
		} else {
			// for all others just default back to the base rules for expressions.
			expr = resolve(expr,environment,context);
			checkIsSubtype(Type.T_BOOL,expr,context);
			return new Pair(expr,environment);
		}		
	}
	
	private Pair<Expr, Environment> resolve(Expr.UnOp expr, boolean sign,
			Environment environment, Context context) {
		Expr.UnOp uop = (Expr.UnOp) expr; 
		if(uop.op == Expr.UOp.NOT) { 
			Pair<Expr,Environment> p = resolve(uop.mhs,!sign,environment,context);
			uop.mhs = p.first();			
			checkIsSubtype(Type.T_BOOL,uop.mhs,context);
			uop.type = Nominal.T_BOOL;
			return new Pair(uop,p.second());
		} else {
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),context,expr);
			return null; // deadcode
		}	
	}
	
	private Pair<Expr, Environment> resolve(Expr.BinOp bop, boolean sign,
			Environment environment, Context context) {		
		Expr.BOp op = bop.op;
		
		switch (op) {
		case AND:
		case OR:
		case XOR:
			return resolveNonLeafCondition(bop,sign,environment,context);
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
			return resolveLeafCondition(bop,sign,environment,context);
		default:
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, bop);
			return null; // dead code
		}		
	}
	
	private Pair<Expr, Environment> resolveNonLeafCondition(
			Expr.BinOp bop,
			boolean sign,
			Environment environment, Context context) {
		Expr.BOp op = bop.op;
		Pair<Expr,Environment> p;
		boolean followOn = (sign && op == Expr.BOp.AND) || (!sign && op == Expr.BOp.OR);
		
		if(followOn) {			
			p = resolve(bop.lhs,sign,environment.clone(),context);			
			bop.lhs = p.first();
			p = resolve(bop.rhs,sign,p.second(),context);
			bop.rhs = p.first();
			environment = p.second();
		} else {
			// We could do better here
			p = resolve(bop.lhs,sign,environment.clone(),context);
			bop.lhs = p.first();
			Environment local = p.second();
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
			p = resolve(bop.lhs,!sign,environment.clone(),context);
			p = resolve(bop.rhs,sign,p.second(),context);
			bop.rhs = p.first();
			environment = join(local,p.second());
		}
		
		checkIsSubtype(Type.T_BOOL,bop.lhs,context);
		checkIsSubtype(Type.T_BOOL,bop.rhs,context);	
		bop.srcType = Nominal.T_BOOL;
		
		return new Pair<Expr,Environment>(bop,environment);
	}
	
	private Pair<Expr, Environment> resolveLeafCondition(Expr.BinOp bop,
			boolean sign, Environment environment, Context context) {
		Expr.BOp op = bop.op;
		
		Expr lhs = resolve(bop.lhs,environment,context);
		Expr rhs = resolve(bop.rhs,environment,context);
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
				Nominal unconstrainedTestType = resolveAsUnconstrainedType(tv.unresolvedType,context);
				
				/**
				 * Determine the types guaranteed to hold on the true and false
				 * branches respectively. We have to use the negated
				 * unconstrainedTestType for the false branch because only that
				 * is guaranteed if the test fails. For example:
				 * 
				 * <pre>
				 * define nat as int where $ &gt;= 0
				 * define listnat as [int]|nat
				 * 
				 * int f([int]|int x):
				 *    if x if listnat:
				 *        x : [int]|int
				 *        ...
				 *    else:
				 *        x : int
				 * </pre>
				 * 
				 * The unconstrained type of listnat is [int], since nat is a
				 * constrained type.
				 */
				Nominal glbForFalseBranch = Nominal.intersect(lhs.result(),
						Nominal.Negation(unconstrainedTestType));
				Nominal glbForTrueBranch = Nominal.intersect(lhs.result(),
						tv.type);

				if(glbForFalseBranch.raw() == Type.T_VOID) {					
					// DEFINITE TRUE CASE										
					syntaxError(errorMessage(BRANCH_ALWAYS_TAKEN), context, bop);
				} else if (glbForTrueBranch.raw() == Type.T_VOID) {				
					// DEFINITE FALSE CASE	
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsRawType, tv.type.raw()),
							context, bop);			
				} 
				
				// Finally, if the lhs is local variable then update its
				// type in the resulting environment. 
				if(lhs instanceof Expr.LocalVariable) {
					Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
					Nominal newType;
					if(sign) {
						newType = glbForTrueBranch;
					} else {						
						newType = glbForFalseBranch;						
					}										
					environment = environment.put(lv.var,newType);
				}
			} else {
				// In this case, we can't update the type of the lhs since
				// we don't know anything about the rhs. It may be possible
				// to support bounds here in order to do that, but frankly
				// that's future work :)
				checkIsSubtype(Type.T_META,rhs,context);
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
				checkIsSubtype(Type.T_SET_ANY,lhs,context);
				checkIsSubtype(Type.T_SET_ANY,rhs,context);
			} else {
				checkIsSubtype(Type.T_REAL,lhs,context);
				checkIsSubtype(Type.T_REAL,rhs,context);
			}
			if(Type.isImplicitCoerciveSubtype(lhsRawType,rhsRawType)) {
				bop.srcType = lhs.result();
			} else if(Type.isImplicitCoerciveSubtype(rhsRawType,lhsRawType)) {
				bop.srcType = rhs.result();
			} else {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS,lhsRawType,rhsRawType),context,bop);	
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
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS,lhs.result().raw(),Type.T_NULL),context,bop);	
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
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS,lhsRawType,rhsRawType),context,bop);	
					return null; // dead code
				}		
			}
		}			
		
		return new Pair(bop,environment);
	}
	
	public Expr resolve(Expr expr, Environment environment, Context context) {
		
		try {
			if(expr instanceof Expr.BinOp) {
				return resolve((Expr.BinOp) expr,environment,context); 
			} else if(expr instanceof Expr.UnOp) {
				return resolve((Expr.UnOp) expr,environment,context); 
			} else if(expr instanceof Expr.Comprehension) {
				return resolve((Expr.Comprehension) expr,environment,context); 
			} else if(expr instanceof Expr.Constant) {
				return resolve((Expr.Constant) expr,environment,context); 
			} else if(expr instanceof Expr.Convert) {
				return resolve((Expr.Convert) expr,environment,context); 
			} else if(expr instanceof Expr.Dictionary) {
				return resolve((Expr.Dictionary) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractFunctionOrMethodOrMessage) {
				return resolve((Expr.AbstractFunctionOrMethodOrMessage) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractInvoke) {
				return resolve((Expr.AbstractInvoke) expr,environment,context); 
			} else if(expr instanceof Expr.IndexOf) {
				return resolve((Expr.IndexOf) expr,environment,context); 
			} else if(expr instanceof Expr.LengthOf) {
				return resolve((Expr.LengthOf) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractVariable) {
				return resolve((Expr.AbstractVariable) expr,environment,context); 
			} else if(expr instanceof Expr.List) {
				return resolve((Expr.List) expr,environment,context); 
			} else if(expr instanceof Expr.Set) {
				return resolve((Expr.Set) expr,environment,context); 
			} else if(expr instanceof Expr.SubList) {
				return resolve((Expr.SubList) expr,environment,context); 
			} else if(expr instanceof Expr.SubString) {
				return resolve((Expr.SubString) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractDotAccess) {
				return resolve((Expr.AbstractDotAccess) expr,environment,context); 
			} else if(expr instanceof Expr.Dereference) {
				return resolve((Expr.Dereference) expr,environment,context); 
			} else if(expr instanceof Expr.Record) {
				return resolve((Expr.Record) expr,environment,context); 
			} else if(expr instanceof Expr.New) {
				return resolve((Expr.New) expr,environment,context); 
			} else if(expr instanceof Expr.Tuple) {
				return  resolve((Expr.Tuple) expr,environment,context); 
			} else if(expr instanceof Expr.TypeVal) {
				return resolve((Expr.TypeVal) expr,environment,context); 
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
	
	private Expr resolve(Expr.BinOp expr,
			Environment environment, Context context) throws Exception {
		
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
			return resolve(expr,true,environment,context).first();
		}
		
		Expr lhs = resolve(expr.lhs,environment,context);
		Expr rhs = resolve(expr.rhs,environment,context);
		expr.lhs = lhs;
		expr.rhs = rhs;
		Type lhsRawType = lhs.result().raw();
		Type rhsRawType = rhs.result().raw();
	
		boolean lhs_set = Type.isImplicitCoerciveSubtype(Type.T_SET_ANY,lhsRawType);
		boolean rhs_set = Type.isImplicitCoerciveSubtype(Type.T_SET_ANY,rhsRawType);		
		boolean lhs_list = Type.isImplicitCoerciveSubtype(Type.T_LIST_ANY,lhsRawType);
		boolean rhs_list = Type.isImplicitCoerciveSubtype(Type.T_LIST_ANY,rhsRawType);
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
			checkIsSubtype(Type.T_LIST_ANY,lhs,context);
			checkIsSubtype(Type.T_LIST_ANY,rhs,context);
			Type.EffectiveList lel = (Type.EffectiveList) lhsRawType;
			Type.EffectiveList rel = (Type.EffectiveList) rhsRawType;
			
			switch(expr.op) {	
			case ADD:
				expr.op = Expr.BOp.LISTAPPEND;
			case LISTAPPEND:				
				srcType = Type.List(Type.Union(lel.element(),rel.element()),false);
				break;
			default:
				syntaxError("invalid list operation: " + expr.op,context,expr);	
				return null; // dead-code
			}										
		} else if(lhs_set && rhs_set) {	
			checkIsSubtype(Type.T_SET_ANY,lhs,context);
			checkIsSubtype(Type.T_SET_ANY,rhs,context);						
			
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
				return resolve(expr,true,environment,context).first();				
			case BITWISEAND:
			case BITWISEOR:
			case BITWISEXOR:
				checkIsSubtype(Type.T_BYTE,lhs,context);
				checkIsSubtype(Type.T_BYTE,rhs,context);
				srcType = Type.T_BYTE;
				break;
			case LEFTSHIFT:
			case RIGHTSHIFT:
				checkIsSubtype(Type.T_BYTE,lhs,context);
				checkIsSubtype(Type.T_INT,rhs,context);
				srcType = Type.T_BYTE;
				break;
			case RANGE:
				checkIsSubtype(Type.T_INT,lhs,context);
				checkIsSubtype(Type.T_INT,rhs,context);
				srcType = Type.List(Type.T_INT, false);
				break;
			case REM:
				checkIsSubtype(Type.T_INT,lhs,context);
				checkIsSubtype(Type.T_INT,rhs,context);
				srcType = Type.T_INT;
				break;			
			default:
				// all other operations go through here
				if(Type.isImplicitCoerciveSubtype(lhsRawType,rhsRawType)) {
					checkIsSubtype(Type.T_REAL,lhs,context);
					if(Type.isSubtype(Type.T_CHAR, lhsRawType)) {
						srcType = Type.T_INT;
					} else if(Type.isSubtype(Type.T_INT, lhsRawType)) {
						srcType = Type.T_INT;
					} else {
						srcType = Type.T_REAL;
					}				
				} else {
					checkIsSubtype(Type.T_REAL,lhs,context);
					checkIsSubtype(Type.T_REAL,rhs,context);				
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
	
	private Expr resolve(Expr.UnOp expr,
			Environment environment, Context context) throws Exception {
		
		if(expr.op == Expr.UOp.NOT) {
			// hand off to special method for conditions
			return resolve(expr,true,environment,context).first();	
		}
		
		Expr src = resolve(expr.mhs, environment,context);
		expr.mhs = src;
		
		switch(expr.op) {
		case NEG:
			checkIsSubtype(Type.T_REAL,src,context);			
			break;
		case INVERT:
			checkIsSubtype(Type.T_BYTE,src,context);
			break;
				
		default:		
			internalFailure(
					"unknown operator: " + expr.op.getClass().getName(),
					context, expr);
		}
		
		expr.type = src.result();		
		
		return expr;
	}
	
	private Expr resolve(Expr.Comprehension expr,
			Environment environment, Context context) throws Exception {
		
		ArrayList<Pair<String,Expr>> sources = expr.sources;
		Environment local = environment.clone();
		for(int i=0;i!=sources.size();++i) {
			Pair<String,Expr> p = sources.get(i);
			Expr e = resolve(p.second(),local,context);			
			p = new Pair<String,Expr>(p.first(),e);
			sources.set(i,p);
			Nominal element;
			Nominal type = e.result();
			Nominal.EffectiveCollection colType = expandAsEffectiveCollection(type);			
			if(colType == null) {
				syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION),context,e);
				return null; // dead code
			}
			// update environment for subsequent source expressions, the
			// condition and the value.
			local = local.put(p.first(),colType.element());
		}
		
		if(expr.condition != null) {
			expr.condition = resolve(expr.condition,local,context);
		}
		
		if (expr.cop == Expr.COp.SETCOMP || expr.cop == Expr.COp.LISTCOMP) {						
			expr.value = resolve(expr.value,local,context);
			expr.type = Nominal.Set(expr.value.result(), false);
		} else {
			expr.type = Nominal.T_BOOL;
		}
		
		local.free();				
		
		return expr;
	}
	
	private Expr resolve(Expr.Constant expr,
			Environment environment, Context context) {
		return expr;
	}

	private Expr resolve(Expr.Convert c,
			Environment environment, Context context) throws Exception {
		c.expr = resolve(c.expr,environment,context);		
		c.type = resolveAsType(c.unresolvedType, context);
		Type from = c.expr.result().raw();		
		Type to = c.type.raw();
		if (!Type.isExplicitCoerciveSubtype(to, from)) {			
			syntaxError(errorMessage(SUBTYPE_ERROR, to, from), context, c);
		}	
		return c;
	}
	
	private Expr resolve(Expr.AbstractFunctionOrMethodOrMessage expr,
			Environment environment, Context context) throws Exception {
		
		if(expr instanceof Expr.FunctionOrMethodOrMessage) {
			return expr;
		} 		
		
		Pair<NameID, Nominal.FunctionOrMethodOrMessage> p;
		
		if (expr.paramTypes != null) {
			ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
			for (UnresolvedType t : expr.paramTypes) {
				paramTypes.add(resolveAsType(t, context));
			}
			// FIXME: clearly a bug here in the case of message reference
			p = (Pair) resolveAsFunctionOrMethod(expr.name, paramTypes, context);			
		} else {
			p = resolveAsFunctionOrMethodOrMessage(expr.name, context);			
		}
		
		expr = new Expr.FunctionOrMethodOrMessage(p.first(),expr.paramTypes,expr.attributes());
		expr.type = p.second();
		return expr;
	}
	
	private Expr resolve(Expr.AbstractInvoke expr,
			Environment environment, Context context) throws Exception {
		
		// first, resolve through receiver and parameters.
		
		Expr receiver = expr.qualification;
		
		if(receiver != null) {
			receiver = resolve(receiver,environment,context);
			expr.qualification = receiver;						
		}
		
		ArrayList<Expr> exprArgs = expr.arguments;
		ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
		for(int i=0;i!=exprArgs.size();++i) {
			Expr arg = resolve(exprArgs.get(i),environment,context);
			exprArgs.set(i, arg);
			paramTypes.add(arg.result());			
		}
		
		// second, determine whether we already have a fully qualified name and
		// then lookup the appropriate function.
		
		if(receiver instanceof Expr.ModuleAccess) {
			// Yes, this function or method is qualified
			Expr.ModuleAccess ma = (Expr.ModuleAccess) receiver;
			NameID name = new NameID(ma.mid,expr.name);
			Nominal.FunctionOrMethod funType = resolveAsFunctionOrMethod(name,  paramTypes);			
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
			
			Nominal.EffectiveRecord recType = expandAsEffectiveRecord(expr.qualification.result());
			
			if(recType != null) {
				
				Nominal fieldType = recType.field(expr.name);
				
				if(fieldType == null) {
					syntaxError(errorMessage(RECORD_MISSING_FIELD,expr.name),context,expr);
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
				checkIsSubtype(Type.T_REF_ANY,expr.qualification,context);
				Type.Reference procType = (Type.Reference) expr.qualification.result().raw(); 						

				// ok, it's a message send (possibly indirect)
				Nominal type = environment.get(expr.name);
				Nominal.Message msgType = type != null ? expandAsMessage(type) : null;
				
				// FIXME: bad idea to use instanceof Nominal.Message here
				if(msgType != null) {
					// ok, matching local variable of message type.
					List<Nominal> funTypeParams = msgType.params();
					// FIXME: is this broken since should be equivalent, not subtype?
					checkIsSubtype(msgType.receiver(),expr.qualification,context);
					if(paramTypes.size() != funTypeParams.size()) {
						syntaxError("insufficient arguments to message send",context,expr);
					}
					for (int i = 0; i != funTypeParams.size(); ++i) {
						Nominal fpt = funTypeParams.get(i);
						checkIsSubtype(fpt, paramTypes.get(i), exprArgs.get(i),context);
					}
					
					Expr.LocalVariable lv = new Expr.LocalVariable(expr.name,expr.attributes());
					lv.type = type;								
					Expr.IndirectMessageSend nexpr = new Expr.IndirectMessageSend(
							lv, expr.qualification, expr.arguments,
							expr.synchronous, expr.attributes());
					nexpr.messageType = msgType; 
					return nexpr;					
				} else {

					Pair<NameID, Nominal.Message> p = resolveAsMessage(expr.name, procType, paramTypes,
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
			Nominal.FunctionOrMethod funType = type != null ? expandAsFunctionOrMethod(type) : null;
			
			// FIXME: bad idea to use instanceof Nominal.FunctionOrMethod here
			if(funType != null) {
				// ok, matching local variable of function type.				
				List<Nominal> funTypeParams = funType.params();
				if(paramTypes.size() != funTypeParams.size()) {
					syntaxError("insufficient arguments to function call",context,expr);
				}
				for (int i = 0; i != funTypeParams.size(); ++i) {
					Nominal fpt = funTypeParams.get(i);
					checkIsSubtype(fpt, paramTypes.get(i), exprArgs.get(i),context);
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
				Pair<NameID, Nominal.FunctionOrMethod> p = resolveAsFunctionOrMethod(expr.name, paramTypes, context);
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
	
	private Expr resolve(Expr.IndexOf expr,
			Environment environment, Context context) throws Exception {			
		expr.src = resolve(expr.src,environment,context);
		expr.index = resolve(expr.index,environment,context);		
		Nominal.EffectiveMap srcType = expandAsEffectiveMap(expr.src.result());
		
		if(srcType == null) {
			syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION), context, expr.src);
		} else {
			expr.srcType = srcType;
		}
		
		checkIsSubtype(srcType.key(),expr.index,context);
				
		return expr;
	}
	
	private Expr resolve(Expr.LengthOf expr, Environment environment,
			Context context) throws Exception {			
		expr.src = resolve(expr.src,environment, context);			
		Nominal srcType = expr.src.result();
		Type rawSrcType = srcType.raw();				
	
		// First, check whether this is still only an abstract access and, in
		// such case, upgrade it to the appropriate access expression.

		if (rawSrcType instanceof Type.EffectiveCollection) {
			expr.srcType = expandAsEffectiveCollection(srcType);
			return expr;
		} else {
			syntaxError("found " + expr.src.result().nominal()
					+ ", expected string, set, list or dictionary.", context,
					expr.src);
		}

		// Second, determine the expanded src type for this access expression
		// and check the key value.
		
		checkIsSubtype(Type.T_STRING,expr.src,context);								
		
		return expr;
	}
	
	private Expr resolve(Expr.AbstractVariable expr,
			Environment environment, Context context) throws Exception {

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
				NameID nid = resolveAsName(expr.var, context);					
				Expr.ConstantAccess ca = new Expr.ConstantAccess(null, expr.var, nid,
						expr.attributes());
				ca.value = resolveAsConstant(nid);
				return ca;
			} catch (ResolveError err) {
			}
			// In this case, we may still be OK if this corresponds to an
			// explicit module or package access.
			try {
				Path.ID mid = resolveAsModule(expr.var, context);
				return new Expr.ModuleAccess(null, expr.var, mid,
						expr.attributes());
			} catch (ResolveError err) {
			}
			Path.ID pid = Trie.ROOT.append(expr.var);
			if (builder.exists(pid)) {
				return new Expr.PackageAccess(null, expr.var, pid,
						expr.attributes());
			}
			// ok, failed.
			syntaxError(errorMessage(UNKNOWN_VARIABLE), context, expr);
			return null; // deadcode
		}
	}
	
	private Expr resolve(Expr.Set expr,
			Environment environment, Context context) {
		Nominal element = Nominal.T_VOID;		
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = resolve(exprs.get(i),environment,context);
			Nominal t = e.result();
			exprs.set(i,e);
			element = Nominal.Union(t,element);			
		}
		
		expr.type = Nominal.Set(element,false);
		
		return expr;
	}
	
	private Expr resolve(Expr.List expr,
			Environment environment, Context context) {		
		Nominal element = Nominal.T_VOID;		
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = resolve(exprs.get(i),environment,context);
			Nominal t = e.result();
			exprs.set(i,e);
			element = Nominal.Union(t,element);			
		}	
		
		expr.type = Nominal.List(element,false);
				
		return expr;
	}
	
	
	private Expr resolve(Expr.Dictionary expr,
			Environment environment, Context context) {
		Nominal keyType = Nominal.T_VOID;
		Nominal valueType = Nominal.T_VOID;		
				
		ArrayList<Pair<Expr,Expr>> exprs = expr.pairs;
		for(int i=0;i!=exprs.size();++i) {
			Pair<Expr,Expr> p = exprs.get(i);
			Expr key = resolve(p.first(),environment,context);
			Expr value = resolve(p.second(),environment,context);
			Nominal kt = key.result();
			Nominal vt = value.result();
			exprs.set(i,new Pair<Expr,Expr>(key,value));
			
			keyType = Nominal.Union(kt,keyType);			
			valueType = Nominal.Union(vt,valueType);
		}
		
		expr.type = Nominal.Dictionary(keyType,valueType);
		
		return expr;
	}
	

	private Expr resolve(Expr.Record expr,
			Environment environment, Context context) {
		
		HashMap<String,Expr> exprFields = expr.fields;
		HashMap<String,Nominal> fieldTypes = new HashMap<String,Nominal>();
				
		ArrayList<String> fields = new ArrayList<String>(exprFields.keySet());
		for(String field : fields) {
			Expr e = resolve(exprFields.get(field),environment,context);
			Nominal t = e.result();
			exprFields.put(field,e);
			fieldTypes.put(field,t);				
		}		
		
		expr.type = Nominal.Record(false,fieldTypes);
		
		return expr;
	}
	
	private Expr resolve(Expr.Tuple expr,
			Environment environment, Context context) {
		ArrayList<Expr> exprFields = expr.fields;
		ArrayList<Nominal> fieldTypes = new ArrayList<Nominal>();
				
		for(int i=0;i!=exprFields.size();++i) {
			Expr e = resolve(exprFields.get(i),environment,context);
			Nominal t = e.result();
			exprFields.set(i,e);
			fieldTypes.add(t);			
		}
				
		expr.type = Nominal.Tuple(fieldTypes);
		
		return expr;
	}
	
	private Expr resolve(Expr.SubList expr,
			Environment environment, Context context) throws Exception {	
		
		expr.src = resolve(expr.src,environment,context);
		expr.start = resolve(expr.start,environment,context);
		expr.end = resolve(expr.end,environment,context);
		
		checkIsSubtype(Type.T_LIST_ANY,expr.src,context);
		checkIsSubtype(Type.T_INT,expr.start,context);
		checkIsSubtype(Type.T_INT,expr.end,context);
		
		expr.type = expandAsEffectiveList(expr.src.result());
		if(expr.type == null) {
			// must be a substring
			return new Expr.SubString(expr.src,expr.start,expr.end,expr.attributes());
		}
		
		return expr;
	}
	
	private Expr resolve(Expr.SubString expr,
			Environment environment, Context context) throws Exception {	
		
		expr.src = resolve(expr.src,environment,context);
		expr.start = resolve(expr.start,environment,context);
		expr.end = resolve(expr.end,environment,context);
		
		checkIsSubtype(Type.T_STRING,expr.src,context);
		checkIsSubtype(Type.T_INT,expr.start,context);
		checkIsSubtype(Type.T_INT,expr.end,context);
		
		return expr;
	}
	
	private Expr resolve(Expr.AbstractDotAccess expr,
			Environment environment, Context context) throws Exception {	
				
		if (expr instanceof Expr.PackageAccess
				|| expr instanceof Expr.ModuleAccess) {			
			// don't need to do anything in these cases.
			return expr;
		}
		
		Expr src = expr.src;
		
		if(src != null) {
			src = resolve(expr.src,environment,context);
			expr.src = src;
		}
				
		if(expr instanceof Expr.RecordAccess) {			
			return resolve((Expr.RecordAccess)expr,environment,context);
		} else if(expr instanceof Expr.ConstantAccess) {
			return resolve((Expr.ConstantAccess)expr,environment,context);
		} else if(src instanceof Expr.PackageAccess) {
			// either a package access, module access or constant access
			// This variable access may correspond to an external access.			
			Expr.PackageAccess pa = (Expr.PackageAccess) src; 
			Path.ID pid = pa.pid.append(expr.name);
			if (builder.exists(pid)) {
				return new Expr.PackageAccess(pa, expr.name, pid,
						expr.attributes());
			}			
			Path.ID mid = pa.pid.append(expr.name);
			if (builder.exists(mid)) {
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
			if (builder.isName(nid)) {
				Expr.ConstantAccess ca = new Expr.ConstantAccess(ma,
						expr.name, nid, expr.attributes());
				ca.value = resolveAsConstant(nid);
				return ca;
			}						
			syntaxError(errorMessage(INVALID_MODULE_ACCESS),context,expr);			
			return null; // deadcode
		} else {
			// must be a RecordAccess
			Expr.RecordAccess ra = new Expr.RecordAccess(src,expr.name,expr.attributes());			
			return resolve(ra,environment,context);
		}
	}
		
	private Expr resolve(Expr.RecordAccess ra,
			Environment environment, Context context) throws Exception {
		Nominal srcType = ra.src.result();
		Nominal.EffectiveRecord recType = expandAsEffectiveRecord(srcType);
		if(recType == null) {
			syntaxError(errorMessage(RECORD_TYPE_REQUIRED,srcType.raw()),context,ra);
		} 
		Nominal fieldType = recType.field(ra.name);
		if(fieldType == null) {
			syntaxError(errorMessage(RECORD_MISSING_FIELD,ra.name),context,ra);
		}
		ra.srcType = recType;		
		return ra;
	}	
	
	private Expr resolve(Expr.ConstantAccess expr,
			Environment environment, Context context) throws Exception {
		// we don't need to do anything here, since the value is already
		// resolved by case for AbstractDotAccess.
		return expr;
	}			

	private Expr resolve(Expr.Dereference expr,
			Environment environment, Context context) throws Exception {
		Expr src = resolve(expr.src,environment,context);
		expr.src = src;
		Nominal.Reference srcType = expandAsReference(src.result());
		if(srcType == null) {
			syntaxError("invalid reference expression",context,src);
		}
		expr.srcType = srcType;		
		return expr;
	}
	
	private Expr resolve(Expr.New expr,
			Environment environment, Context context) {
		expr.expr = resolve(expr.expr,environment,context);
		expr.type = Nominal.Reference(expr.expr.result());
		return expr;
	}
	
	private Expr resolve(Expr.TypeVal expr,
			Environment environment, Context context) throws Exception {
		expr.type = resolveAsType(expr.unresolvedType, context); 
		return expr;
	}
	

	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. To do this, it must find the function/method with the most
	 * precise type that matches the argument types.
	 * 
	 * @param nid
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	private Nominal.FunctionOrMethod resolveAsFunctionOrMethod(NameID nid, 
			List<Nominal> parameters) throws Exception {
		HashSet<Pair<NameID, Nominal.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Nominal.FunctionOrMethod>>();

		addCandidateFunctionsAndMethods(nid, parameters, candidates);

		return selectCandidateFunctionOrMethod(nid.name(), parameters,
				candidates).second();		
	}

	private Nominal.Message resolveAsMessage(NameID nid,
			Type.Reference receiver, List<Nominal> parameters)
			throws Exception {
		HashSet<Pair<NameID, Nominal.Message>> candidates = new HashSet<Pair<NameID, Nominal.Message>>();

		addCandidateMessages(nid, parameters, candidates);

		return selectCandidateMessage(nid.name(), receiver, parameters,
				candidates).second();
	}


	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. In this case, no argument types are given. This means that any
	 * match is returned. However, if there are multiple matches, then an
	 * ambiguity error is reported.
	 * 
	 * @param name
	 *            --- function or method name whose type to determine.
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return
	 * @throws Exception
	 */
	public Pair<NameID,Nominal.FunctionOrMethod> resolveAsFunctionOrMethod(String name, 
			Context context) throws Exception {
		return resolveAsFunctionOrMethod(name,null,context);
	}

	/**
	 * Responsible for determining the true type of a funciont, method or
	 * message being invoked. In this case, no argument types are given. This
	 * means that any match is returned. However, if there are multiple matches,
	 * then an ambiguity error is reported.
	 * 
	 * @param name
	 *            --- function or method name whose type to determine.
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return
	 * @throws Exception
	 */
	public Pair<NameID, Nominal.FunctionOrMethodOrMessage> resolveAsFunctionOrMethodOrMessage(
			String name, Context context) throws Exception {
		try {
			return (Pair) resolveAsFunctionOrMethod(name, null, context);
		} catch (ResolveError e) {
			return (Pair) resolveAsMessage(name, null, null, context);
		}
	}
	
	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. To do this, it must find the function/method with the most
	 * precise type that matches the argument types.
	 * 
	 * @param name
	 *            --- name of function or method whose type to determine.
	 * @param parameters
	 *            --- required parameter types for the function or method.
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return
	 * @throws Exception
	 */
	public Pair<NameID,Nominal.FunctionOrMethod> resolveAsFunctionOrMethod(String name, 
			List<Nominal> parameters, Context context) throws Exception {

		HashSet<Pair<NameID,Nominal.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Nominal.FunctionOrMethod>>(); 		
		// first, try to find the matching message
		for (WhileyFile.Import imp : context.imports()) {
			String impName = imp.name;
			if (impName == null || impName.equals(name) || impName.equals("*")) {
				Trie filter = imp.filter;
				if(impName == null) {
					// import name is null, but it's possible that a module of
					// the given name exists, in which case any matching names
					// are automatically imported. 
					filter = filter.parent().append(name);
				}
				for (Path.ID mid : builder.imports(filter)) {					
					NameID nid = new NameID(mid,name);				
					addCandidateFunctionsAndMethods(nid,parameters,candidates);					
				}
			} 
		}

		return selectCandidateFunctionOrMethod(name,parameters,candidates);
	}
	
	public Pair<NameID,Nominal.Message> resolveAsMessage(String name, Type.Reference receiver,
			List<Nominal> parameters, Context context) throws Exception {

		HashSet<Pair<NameID,Nominal.Message>> candidates = new HashSet<Pair<NameID,Nominal.Message>>(); 

		// first, try to find the matching message
		for (WhileyFile.Import imp : context.imports()) {
			String impName = imp.name;
			if (impName == null || impName.equals(name) || impName.equals("*")) {
				Trie filter = imp.filter;
				if(impName == null) {
					// import name is null, but it's possible that a module of
					// the given name exists, in which case any matching names
					// are automatically imported. 
					filter = filter.parent().append(name);
				}
				for (Path.ID mid : builder.imports(filter)) {				
					NameID nid = new NameID(mid, name);
					addCandidateMessages(nid, parameters, candidates);					
				}
			}
		}

		return selectCandidateMessage(name,receiver,parameters,candidates);
	}
	
	private boolean paramSubtypes(Type.FunctionOrMethodOrMessage f1, Type.FunctionOrMethodOrMessage f2) {		
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if(f1_params.size() == f2_params.size()) {			
			for(int i=0;i!=f1_params.size();++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);				
				if(!Type.isImplicitCoerciveSubtype(f1_param,f2_param)) {				
					return false;
				}
			}			

			return true;
		}
		return false;
	}

	private boolean paramStrictSubtypes(Type.FunctionOrMethodOrMessage f1, Type.FunctionOrMethodOrMessage f2) {		
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if(f1_params.size() == f2_params.size()) {
			boolean allEqual = true;
			for(int i=0;i!=f1_params.size();++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);				
				if(!Type.isImplicitCoerciveSubtype(f1_param,f2_param)) {				
					return false;
				}
				allEqual &= f1_param.equals(f2_param);
			}			

			// This function returns true if the parameters are a strict
			// subtype. Therefore, if they are all equal it must return false.

			return !allEqual;
		}
		return false;
	}

	private String parameterString(List<Nominal> paramTypes) {
		String paramStr = "(";
		boolean firstTime = true;
		if(paramTypes == null) {
			paramStr += "...";
		} else {
			for(Nominal t : paramTypes) {
				if(!firstTime) {
					paramStr += ",";
				}
				firstTime=false;
				paramStr += t.nominal();
			}
		}
		return paramStr + ")";		
	}

	private Pair<NameID,Nominal.FunctionOrMethod> selectCandidateFunctionOrMethod(String name,
			List<Nominal> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates)
					throws Exception {

		List<Type> rawParameters; 
		Type.Function target;

		if (parameters != null) {
			rawParameters = stripNominal(parameters);
			target = (Type.Function) Type.Function(Type.T_ANY, Type.T_ANY,
					rawParameters);
		} else {
			rawParameters = null;
			target = null;
		}

		NameID candidateID = null;
		Nominal.FunctionOrMethod candidateType = null;					
		for (Pair<NameID,Nominal.FunctionOrMethod> p : candidates) {			
			Nominal.FunctionOrMethod nft = p.second();
			Type.FunctionOrMethod ft = nft.raw();			
			if (parameters == null || paramSubtypes(ft, target)) {
				// this is now a genuine candidate
				if(candidateType == null || paramStrictSubtypes(candidateType.raw(), ft)) {
					candidateType = nft;
					candidateID = p.first();
				} else if(!paramStrictSubtypes(ft, candidateType.raw())){ 
					// this is an ambiguous error
					String msg = name + parameterString(parameters) + " is ambiguous";
					// FIXME: should report all ambiguous matches here
					msg += "\n\tfound: " + candidateID + " : " + candidateType.nominal();
					msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
					throw new ResolveError(msg);
				}
			}			
		}				

		if(candidateType == null) {
			// second, didn't find matching message so generate error message
			String msg = "no match for " + name + parameterString(parameters);			

			for (Pair<NameID, Nominal.FunctionOrMethod> p : candidates) {
				msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
			}

			throw new ResolveError(msg);
		}				

		return new Pair<NameID,Nominal.FunctionOrMethod>(candidateID,candidateType);
	}

	private Pair<NameID,Nominal.Message> selectCandidateMessage(String name,
			Type.Reference receiver,
			List<Nominal> parameters,
			Collection<Pair<NameID, Nominal.Message>> candidates)
					throws Exception {

		List<Type> rawParameters; 
		Type.Function target;

		if (parameters != null) {
			rawParameters = stripNominal(parameters);
			target = (Type.Function) Type.Function(Type.T_ANY, Type.T_ANY,
					rawParameters);
		} else {
			rawParameters = null;
			target = null;
		}

		NameID candidateID = null;
		Nominal.Message candidateType = null;			

		for (Pair<NameID,Nominal.Message> p : candidates) {
			Nominal.Message nmt = p.second();
			Type.Message mt = nmt.raw();
			Type funrec = mt.receiver();

			if (receiver == funrec
					|| receiver == null
					|| (funrec != null && Type
					.isImplicitCoerciveSubtype(receiver, funrec))) {					
				// receivers match up OK ...				
				if (parameters == null || paramSubtypes(mt, target)) {
					// this is now a genuine candidate
					if(candidateType == null || paramStrictSubtypes(candidateType.raw(), mt)) {
						candidateType = nmt;
						candidateID = p.first();						
					} else if(!paramStrictSubtypes(mt, candidateType.raw())) {
						// this is an ambiguous error
						String msg = name + parameterString(parameters) + " is ambiguous";
						// FIXME: should report all ambiguous matches here
						msg += "\n\tfound: " + candidateID + " : " + candidateType.nominal();
						msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
						throw new ResolveError(msg);
					}
				}			
			}
		}				

		if (candidateType == null) {
			// second, didn't find matching message so generate error message
			String msg = "no match for " + receiver + "::" + name
					+ parameterString(parameters);

			for (Pair<NameID, Nominal.Message> p : candidates) {
				msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
			}

			throw new ResolveError(msg);
		}				

		return new Pair<NameID,Nominal.Message>(candidateID,candidateType);
	}

	private void addCandidateFunctionsAndMethods(NameID nid,
			List<?> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates)
					throws Exception {
		Path.ID mid = nid.module();

		int nparams = parameters != null ? parameters.size() : -1;				

		WhileyFile wf = builder.getSourceFile(mid);
		if (wf != null) {
			for (WhileyFile.FunctionOrMethod f : wf.declarations(
					WhileyFile.FunctionOrMethod.class, nid.name())) {
				if (nparams == -1 || f.parameters.size() == nparams) {
					Nominal.FunctionOrMethod ft = (Nominal.FunctionOrMethod) resolveAsType(
							f.unresolvedType(), f);
					candidates.add(new Pair<NameID, Nominal.FunctionOrMethod>(
							nid, ft));
				}
			}
		} else {
			try {
				WyilFile m = builder.getModule(mid);
				for (WyilFile.Method mm : m.methods()) {
					if ((mm.isFunction() || mm.isMethod())
							&& mm.name().equals(nid.name())
							&& (nparams == -1 || mm.type().params().size() == nparams)) {
						// FIXME: loss of nominal information
						Type.FunctionOrMethod t = (Type.FunctionOrMethod) mm
								.type();
						Nominal.FunctionOrMethod fom;
						if (t instanceof Type.Function) {
							Type.Function ft = (Type.Function) t;
							fom = new Nominal.Function(ft, ft);
						} else {
							Type.Method mt = (Type.Method) t;
							fom = new Nominal.Method(mt, mt);
						}
						candidates
								.add(new Pair<NameID, Nominal.FunctionOrMethod>(
										nid, fom));
					}
				}
			} catch (ResolveError e) {

			}
		}
	}

	private void addCandidateMessages(NameID nid, List<?> parameters,
			Collection<Pair<NameID, Nominal.Message>> candidates)
			throws Exception {
		Path.ID mid = nid.module();

		int nparams = parameters != null ? parameters.size() : -1;		
		WhileyFile wf = builder.getSourceFile(mid);
		if (wf != null) {
			for (WhileyFile.Message m : wf.declarations(
					WhileyFile.Message.class, nid.name())) {
				if (nparams == -1 || m.parameters.size() == nparams) {
					Nominal.Message ft = (Nominal.Message) resolveAsType(
							m.unresolvedType(), m);
					candidates.add(new Pair<NameID, Nominal.Message>(nid, ft));
				}
			}
		} else {			
			try {				
				WyilFile m = builder.getModule(mid);
				for (WyilFile.Method mm : m.methods()) {				
					if (mm.isMessage()
							&& mm.name().equals(nid.name())
							&& (nparams == -1 || mm.type().params().size() == nparams)) {
						// FIXME: loss of nominal information
						Nominal.Message ft = new Nominal.Message(
								(Type.Message) mm.type(),
								(Type.Message) mm.type());
						candidates.add(new Pair<NameID, Nominal.Message>(nid,
								ft));
					}
				}
			} catch (ResolveError e) {

			}
		}
	}

	private static List<Type> stripNominal(List<Nominal> types) {
		ArrayList<Type> r = new ArrayList<Type>();
		for (Nominal t : types) {
			r.add(t.raw());
		}
		return r;
	}
		
	// Check t1 :> t2
	private void checkIsSubtype(Nominal t1, Nominal t2, SyntacticElement elem, Context context) {
		if (!Type.isImplicitCoerciveSubtype(t1.raw(), t2.raw())) {
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.nominal()),
					context, elem);
		}
	}
	
	private void checkIsSubtype(Nominal t1, Expr t2, Context context) {
		if (!Type.isImplicitCoerciveSubtype(t1.raw(), t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.result()
							.nominal()), context, t2);
		}
	}
	
	private void checkIsSubtype(Type t1, Expr t2, Context context) {
		if (!Type.isImplicitCoerciveSubtype(t1, t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(errorMessage(SUBTYPE_ERROR, t1, t2.result().nominal()),
					context, t2);
		}
	}
	
	private static final Environment BOTTOM = new Environment();
	
	private static final Environment join(
			Environment lhs,
			Environment rhs) {
		
		// first, need to check for the special bottom value case.
		
		if(lhs == BOTTOM) {
			return rhs;
		} else if(rhs == BOTTOM) {
			return lhs;
		}
		
		// ok, not bottom so compute intersection.
		
		lhs.free();
		rhs.free(); 		
		
		Environment result = new Environment();
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
