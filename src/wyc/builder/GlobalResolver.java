package wyc.builder;

import static wyc.lang.WhileyFile.*;
import static wyil.util.ErrorMessages.INVALID_BINARY_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_BOOLEAN_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_LIST_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_NUMERIC_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_SET_EXPRESSION;
import static wyil.util.ErrorMessages.errorMessage;

import java.util.*;

import wyautl.lang.Automata;
import wyautl.lang.Automaton;
import wybs.lang.Path;
import wybs.lang.SyntaxError;
import wybs.util.ResolveError;
import wybs.util.Trie;
import wyil.lang.*;
import wyil.util.*;
import wyc.builder.WhileyBuilder;
import wyc.lang.Expr;
import wyc.lang.UnresolvedType;
import wyc.lang.WhileyFile;

/**
 * <p>
 * A Name Resolver is responsible for searching the WHILEYPATH to resolve given
 * names and packages. For example, we may wish to resolve a name "Reader"
 * within a given import context of "import whiley.io.*". In such case, the name
 * resolver will first locate the package "whiley.io", and then decide whether
 * there is a module named "Reader".
 * </p>
 * 
 * 
 * @author David J. Pearce
 * 
 */
public class GlobalResolver extends LocalResolver {
	/**
	 * The constant cache contains a cache of expanded constant values.
	 */
	private final HashMap<NameID, Value> constantCache = new HashMap();
	
	public GlobalResolver(WhileyBuilder project) {
		super(project);
	}
	
	public WhileyBuilder loader() {
		return builder;		
	}
	
	// =========================================================================
	// ResolveAsName
	// =========================================================================		
	
	/**
	 * This methods attempts to resolve the correct package for a named item in
	 * a given context. Resolving the correct package may require loading
	 * modules as necessary from the WHILEYPATH and/or compiling modules for
	 * which only source code is currently available.
	 * 
	 * @param name
	 *            A module name without package specifier.
	 * @param context
	 *            --- context in which to resolve.
	 * @return The resolved name.
	 * @throws Exception
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(String name, Context context)
			throws Exception {		
		for (WhileyFile.Import imp : context.imports()) {
			String impName = imp.name;
			if (impName == null || impName.equals(name) || impName.equals("*")) {
				Trie filter = imp.filter;
				if (impName == null) {
					// import name is null, but it's possible that a module of
					// the given name exists, in which case any matching names
					// are automatically imported.
					filter = filter.parent().append(name);
				}
				for (Path.ID mid : builder.imports(filter)) {
					NameID nid = new NameID(mid, name);
					if (builder.isName(nid)) {
						return nid;
					}
				}
			}
		}

		throw new ResolveError("name not found: " + name);
	}

	/**
	 * This methods attempts to resolve the given list of names into a single
	 * named item (e.g. type, method, constant, etc). For example,
	 * <code>["whiley","lang","Math","max"]</code> would be resolved, since
	 * <code>whiley.lang.Math.max</code> is a valid function name. In contrast,
	 * <code>["whiley","lang","Math"]</code> does not resolve since
	 * <code>whiley.lang.Math</code> refers to a module.
	 * 
	 * @param names
	 *            A list of components making up the name, which may include the
	 *            package and enclosing module.
	 * @param context
	 *            --- context in which to resolve *
	 * @return The resolved name.
	 * @throws Exception
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(List<String> names, Context context) throws Exception {		
		if(names.size() == 1) {
			return resolveAsName(names.get(0),context);
		} else if(names.size() == 2) {
			String name = names.get(1);
			Path.ID mid = resolveAsModule(names.get(0),context);		
			NameID nid = new NameID(mid, name); 
			if (builder.isName(nid)) {
				return nid;
			} 
		} else {
			String name = names.get(names.size()-1);
			String module = names.get(names.size()-2);
			Path.ID pkg = Trie.ROOT;
			for(int i=0;i!=names.size()-2;++i) {
				pkg = pkg.append(names.get(i));
			}
			Path.ID mid = pkg.append(module);
			NameID nid = new NameID(mid, name); 
			if (builder.isName(nid)) {
				return nid;
			} 			
		}
		
		String name = null;
		for(String n : names) {
			if(name != null) {
				name = name + "." + n;
			} else {
				name = n;
			}			
		}
		throw new ResolveError("name not found: " + name);
	}	
	
	/**
	 * This method attempts to resolve a name as a module in a given name
	 * context.
	 * 
	 * @param name
	 *            --- name to be resolved
	 * @param context
	 *            --- context in which to resolve
	 * @return
	 * @throws Exception
	 */
	public Path.ID resolveAsModule(String name, Context context)
			throws Exception {
		
		for (WhileyFile.Import imp : context.imports()) {
			Trie filter = imp.filter;
			String last = filter.last();			
			if (last.equals("*")) {
				// this is generic import, so narrow the filter.
				filter = filter.parent().append(name);
			} else if(!last.equals(name)) {
				continue; // skip as not relevant
			}
			
			for(Path.ID mid : builder.imports(filter)) {								
				return mid;				
			}
		}
				
		throw new ResolveError("module not found: " + name);
	}
	
	// =========================================================================
	// ResolveAsType
	// =========================================================================
	
	public Nominal.Function resolveAsType(UnresolvedType.Function t,
			Context context) {
		return (Nominal.Function) resolveAsType((UnresolvedType)t,context);
	}
	
	public Nominal.Method resolveAsType(UnresolvedType.Method t,
			Context context) {		
		return (Nominal.Method) resolveAsType((UnresolvedType)t,context);
	}
	
	public Nominal.Message resolveAsType(UnresolvedType.Message t,
			Context context) {		
		return (Nominal.Message) resolveAsType((UnresolvedType)t,context);
	}

	/**
	 * Resolve a type in a given context by identifying all unknown names and
	 * replacing them with nominal types.
	 * 
	 * @param type
	 *            --- type to be resolved.
	 * @param context
	 *            --- context in which to resolve the type.
	 * @return
	 * @throws Exception
	 */
	public Nominal resolveAsType(UnresolvedType type, Context context) {
		Type nominalType = resolveAsType(type, context, true, false);
		Type rawType = resolveAsType(type, context, false, false);
		return Nominal.construct(nominalType, rawType);
	}
	
	/**
	 * Resolve a type in a given context by identifying all unknown names and
	 * replacing them with nominal types. In this case, any constrained types
	 * are treated as void. This is critical for properly dealing with type
	 * tests, which may otherwise assume types are unconstrained.
	 * 
	 * @param type
	 *            --- type to be resolved.
	 * @param context
	 *            --- context in which to resolve the type.
	 * @return
	 * @throws Exception
	 */
	public Nominal resolveAsUnconstrainedType(UnresolvedType type, Context context) {
		Type nominalType = resolveAsType(type, context, true, true);
		Type rawType = resolveAsType(type, context, false, true);
		return Nominal.construct(nominalType, rawType);
	}
	
	private Type resolveAsType(UnresolvedType t, Context context,
			boolean nominal, boolean unconstrained) {
		if(t instanceof UnresolvedType.Primitive) { 
			if (t instanceof UnresolvedType.Any) {
				return Type.T_ANY;
			} else if (t instanceof UnresolvedType.Void) {
				return Type.T_VOID;
			} else if (t instanceof UnresolvedType.Null) {
				return Type.T_NULL;
			} else if (t instanceof UnresolvedType.Bool) {
				return Type.T_BOOL;
			} else if (t instanceof UnresolvedType.Byte) {
				return Type.T_BYTE;
			} else if (t instanceof UnresolvedType.Char) {
				return Type.T_CHAR;
			} else if (t instanceof UnresolvedType.Int) {
				return Type.T_INT;
			} else if (t instanceof UnresolvedType.Real) {
				return Type.T_REAL;
			} else if (t instanceof UnresolvedType.Strung) {
				return Type.T_STRING;
			} else {
				internalFailure("unrecognised type encountered ("
						+ t.getClass().getName() + ")",context,t);
				return null; // deadcode
			}
		} else {
			ArrayList<Automaton.State> states = new ArrayList<Automaton.State>();
			HashMap<NameID,Integer> roots = new HashMap<NameID,Integer>();
			resolveAsType(t,context,states,roots,nominal,unconstrained);
			return Type.construct(new Automaton(states));
		}
	}
	
	/**
	 * The following method resolves a type in a given context.
	 * 
	 * @param type
	 *            --- type to be resolved
	 * @param context
	 *            --- context in which to resolve the type
	 * @return
	 * @throws Exception
	 */
	private int resolveAsType(UnresolvedType type, Context context,
			ArrayList<Automaton.State> states, HashMap<NameID, Integer> roots,
			boolean nominal, boolean unconstrained) {			
		
		if(type instanceof UnresolvedType.Primitive) {
			return resolveAsType((UnresolvedType.Primitive)type,context,states);
		} 
		
		int myIndex = states.size();
		int myKind;
		int[] myChildren;
		Object myData = null;
		boolean myDeterministic = true;
		
		states.add(null); // reserve space for me
		
		if(type instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) type;
			myKind = Type.K_LIST;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(lt.element,context,states,roots,nominal,unconstrained);
			myData = false;
		} else if(type instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) type;
			myKind = Type.K_SET;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(st.element,context,states,roots,nominal,unconstrained);
			myData = false;
		} else if(type instanceof UnresolvedType.Dictionary) {
			UnresolvedType.Dictionary st = (UnresolvedType.Dictionary) type;
			myKind = Type.K_DICTIONARY;
			myChildren = new int[2];
			myChildren[0] = resolveAsType(st.key,context,states,roots,nominal,unconstrained);
			myChildren[1] = resolveAsType(st.value,context,states,roots,nominal,unconstrained);			
		} else if(type instanceof UnresolvedType.Record) {
			UnresolvedType.Record tt = (UnresolvedType.Record) type;
			HashMap<String,UnresolvedType> ttTypes = tt.types;			
			Type.Record.State fields = new Type.Record.State(tt.isOpen,ttTypes.keySet());
			Collections.sort(fields);			
			myKind = Type.K_RECORD;
			myChildren = new int[fields.size()];
			for(int i=0;i!=fields.size();++i) {	
				String field = fields.get(i);
				myChildren[i] = resolveAsType(ttTypes.get(field),context,states,roots,nominal,unconstrained);
			}						
			myData = fields;
		} else if(type instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) type;
			ArrayList<UnresolvedType> ttTypes = tt.types;
			myKind = Type.K_TUPLE;
			myChildren = new int[ttTypes.size()];
			for(int i=0;i!=ttTypes.size();++i) {
				myChildren[i] = resolveAsType(ttTypes.get(i),context,states,roots,nominal,unconstrained);				
			}			
		} else if(type instanceof UnresolvedType.Nominal) {
			// This case corresponds to a user-defined type. This will be
			// defined in some module (possibly ours), and we need to identify
			// what module that is here, and save it for future use.
			UnresolvedType.Nominal dt = (UnresolvedType.Nominal) type;									
			NameID nid;
			try {
				nid = resolveAsName(dt.names, context);

				if(nominal) {
					myKind = Type.K_NOMINAL;
					myData = nid;
					myChildren = Automaton.NOCHILDREN;
				} else {
					// At this point, we're going to expand the given nominal type.
					// We're going to use resolveAsType(NameID,...) to do this which
					// will load the expanded type onto states at the current point.
					// Therefore, we need to remove the initial null we loaded on.
					states.remove(myIndex); 
					return resolveAsType(nid,states,roots,unconstrained);				
				}	
			} catch(ResolveError e) {
				syntaxError(e.getMessage(),context,dt,e);
				return 0; // dead-code
			} catch(SyntaxError e) {
				throw e;
			} catch(Throwable e) {
				internalFailure(e.getMessage(),context,dt,e);
				return 0; // dead-code
			}
		} else if(type instanceof UnresolvedType.Not) {	
			UnresolvedType.Not ut = (UnresolvedType.Not) type;
			myKind = Type.K_NEGATION;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element,context,states,roots,nominal,unconstrained);			
		} else if(type instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) type;
			ArrayList<UnresolvedType.NonUnion> utTypes = ut.bounds;
			myKind = Type.K_UNION;
			myChildren = new int[utTypes.size()];
			for(int i=0;i!=utTypes.size();++i) {
				myChildren[i] = resolveAsType(utTypes.get(i),context,states,roots,nominal,unconstrained);				
			}	
			myDeterministic = false;
		} else if(type instanceof UnresolvedType.Intersection) {
			internalFailure("intersection types not supported yet",context,type);
			return 0; // dead-code
		} else if(type instanceof UnresolvedType.Reference) {	
			UnresolvedType.Reference ut = (UnresolvedType.Reference) type;
			myKind = Type.K_REFERENCE;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element,context,states,roots,nominal,unconstrained);		
		} else {			
			UnresolvedType.FunctionOrMethodOrMessage ut = (UnresolvedType.FunctionOrMethodOrMessage) type;			
			ArrayList<UnresolvedType> utParamTypes = ut.paramTypes;
			UnresolvedType receiver = null;
			int start = 0;
			
			if(ut instanceof UnresolvedType.Message) {
				UnresolvedType.Message mt = (UnresolvedType.Message) ut;
				receiver = mt.receiver;				
				myKind = Type.K_MESSAGE;
				start++;				
			} else if(ut instanceof UnresolvedType.Method) {
				myKind = Type.K_METHOD;
			} else {
				myKind = Type.K_FUNCTION;
			}
			
			myChildren = new int[start + 2 + utParamTypes.size()];
			
			if(receiver != null) {
				myChildren[0] = resolveAsType(receiver,context,states,roots,nominal,unconstrained);
			}			
			myChildren[start++] = resolveAsType(ut.ret,context,states,roots,nominal,unconstrained);
			if(ut.throwType == null) {
				// this case indicates the user did not provide a throws clause.
				myChildren[start++] = resolveAsType(new UnresolvedType.Void(),context,states,roots,nominal,unconstrained);
			} else {
				myChildren[start++] = resolveAsType(ut.throwType,context,states,roots,nominal,unconstrained);
			}
			for(UnresolvedType pt : utParamTypes) {
				myChildren[start++] = resolveAsType(pt,context,states,roots,nominal,unconstrained);				
			}						
		}
		
		states.set(myIndex,new Automaton.State(myKind,myData,myDeterministic,myChildren));
		
		return myIndex;
	}
	
	private int resolveAsType(NameID key, ArrayList<Automaton.State> states,
			HashMap<NameID, Integer> roots, boolean unconstrained) throws Exception {
		
		// First, check the various caches we have
		Integer root = roots.get(key);			
		if (root != null) { return root; } 		
		
		// check whether this type is external or not
		WhileyFile wf = builder.getSourceFile(key.module());
		if (wf == null) {						
			// indicates a non-local key which we can resolve immediately	
			
			// FIXME: need to properly support unconstrained types here
			
			WyilFile mi = builder.getModule(key.module());
			WyilFile.TypeDef td = mi.type(key.name());	
			return append(td.type(),states);			
		} 
		
		WhileyFile.TypeDef td = wf.typeDecl(key.name());
		if(td == null) {
			Type t = resolveAsConstant(key).type();			
			if(t instanceof Type.Set) {
				if(unconstrained) {
					// crikey this is ugly
					int myIndex = states.size();
					int kind = Type.leafKind(Type.T_VOID);			
					Object data = null;
					states.add(new Automaton.State(kind,data,true,Automaton.NOCHILDREN));
					return myIndex;					
				}
				Type.Set ts = (Type.Set) t;
				return append(ts.element(),states);	
			} else {
				throw new ResolveError("type not found: " + key);
			}
		}
		
		// following is needed to terminate any recursion
		roots.put(key, states.size());
		UnresolvedType type = td.unresolvedType;
		
		// now, expand the given type fully	
		if(unconstrained && td.constraint != null) {
			int myIndex = states.size();
			int kind = Type.leafKind(Type.T_VOID);			
			Object data = null;
			states.add(new Automaton.State(kind,data,true,Automaton.NOCHILDREN));
			return myIndex;
		} else if(type instanceof Type.Leaf) {
			// to avoid unnecessarily creating an array of size one
			int myIndex = states.size();
			int kind = Type.leafKind((Type.Leaf)type);			
			Object data = Type.leafData((Type.Leaf)type);
			states.add(new Automaton.State(kind,data,true,Automaton.NOCHILDREN));
			return myIndex;
		} else {						
			return resolveAsType(type,td,states,roots,false,unconstrained);			
		}
		
		// TODO: performance can be improved here, but actually assigning the
		// constructed type into a cache of previously expanded types cache.
		// This is challenging, in the case that the type may not be complete at
		// this point. In particular, if it contains any back-links above this
		// index there could be an issue.
	}	
	
	private int resolveAsType(UnresolvedType.Primitive t,
			Context context, ArrayList<Automaton.State> states) {
		int myIndex = states.size();
		int kind;
		if (t instanceof UnresolvedType.Any) {
			kind = Type.K_ANY;
		} else if (t instanceof UnresolvedType.Void) {
			kind = Type.K_VOID;
		} else if (t instanceof UnresolvedType.Null) {
			kind = Type.K_NULL;
		} else if (t instanceof UnresolvedType.Bool) {
			kind = Type.K_BOOL;
		} else if (t instanceof UnresolvedType.Byte) {
			kind = Type.K_BYTE;
		} else if (t instanceof UnresolvedType.Char) {
			kind = Type.K_CHAR;
		} else if (t instanceof UnresolvedType.Int) {
			kind = Type.K_INT;
		} else if (t instanceof UnresolvedType.Real) {
			kind = Type.K_RATIONAL;
		} else if (t instanceof UnresolvedType.Strung) {
			kind = Type.K_STRING;
		} else {		
			internalFailure("unrecognised type encountered ("
					+ t.getClass().getName() + ")",context,t);
			return 0; // dead-code
		}
		states.add(new Automaton.State(kind, null, true,
				Automaton.NOCHILDREN));
		return myIndex;
	}
	
	private static int append(Type type, ArrayList<Automaton.State> states) {
		int myIndex = states.size();
		Automaton automaton = Type.destruct(type);
		Automaton.State[] tStates = automaton.states;
		int[] rmap = new int[tStates.length];
		for (int i = 0, j = myIndex; i != rmap.length; ++i, ++j) {
			rmap[i] = j;
		}
		for (Automaton.State state : tStates) {
			states.add(Automata.remap(state, rmap));
		}
		return myIndex;
	}
	
	// =========================================================================
	// ResolveAsConstant
	// =========================================================================
	
	public Value resolveAsConstant(NameID nid) throws Exception {				
		return resolveAsConstant(nid,new HashSet<NameID>());		
	}

	public Value resolveAsConstant(Expr e, Context context) {		
		e = resolve(e, new Environment(),context);
		return resolveAsConstant(e,context,new HashSet<NameID>());		
	}

	/**
	 * Responsible for turning a named constant expression into a value. This is
	 * done by traversing the constant's expression and recursively expanding
	 * any named constants it contains. Simplification of constants is also
	 * performed where possible.
	 * 
	 * @param key
	 *            --- name of constant we are expanding.
	 * @param exprs
	 *            --- mapping of all names to their( declared) expressions
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 * @return
	 * @throws Exception
	 */
	private Value resolveAsConstant(NameID key, HashSet<NameID> visited) throws Exception {				
		Value result = constantCache.get(key);
		if(result != null) {
			return result;
		} else if(visited.contains(key)) {
			throw new ResolveError("cyclic constant definition encountered (" + key + " -> " + key + ")");
		} else {
			visited.add(key);
		}

		WhileyFile wf = builder.getSourceFile(key.module());

		if (wf != null) {			
			WhileyFile.Declaration decl = wf.declaration(key.name());
			if(decl instanceof WhileyFile.Constant) {
				WhileyFile.Constant cd = (WhileyFile.Constant) decl; 				
				if (cd.resolvedValue == null) {			
					cd.constant = resolve(cd.constant, new Environment(), cd);
					cd.resolvedValue = resolveAsConstant(cd.constant,
							cd, visited);
				}
				result = cd.resolvedValue;
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		} else {		
			WyilFile module = builder.getModule(key.module());
			WyilFile.ConstDef cd = module.constant(key.name());
			if(cd != null) {
				result = cd.constant();
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		}		

		constantCache.put(key, result);

		return result;
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
	 * @param context
	 *            --- context in which to resolve this constant.
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 */
	private Value resolveAsConstant(Expr expr, Context context,
			HashSet<NameID> visited) {
		try {
			if (expr instanceof Expr.Constant) {
				Expr.Constant c = (Expr.Constant) expr;
				return c.value;
			} else if (expr instanceof Expr.ConstantAccess) {
				Expr.ConstantAccess c = (Expr.ConstantAccess) expr;
				return resolveAsConstant(c.nid,visited);				
			} else if (expr instanceof Expr.BinOp) {
				Expr.BinOp bop = (Expr.BinOp) expr;
				Value lhs = resolveAsConstant(bop.lhs, context, visited);
				Value rhs = resolveAsConstant(bop.rhs, context, visited);
				return evaluate(bop,lhs,rhs,context);			
			} else if (expr instanceof Expr.Set) {
				Expr.Set nop = (Expr.Set) expr;
				ArrayList<Value> values = new ArrayList<Value>();
				for (Expr arg : nop.arguments) {
					values.add(resolveAsConstant(arg,context, visited));
				}			
				return Value.V_SET(values);			
			} else if (expr instanceof Expr.List) {
				Expr.List nop = (Expr.List) expr;
				ArrayList<Value> values = new ArrayList<Value>();
				for (Expr arg : nop.arguments) {
					values.add(resolveAsConstant(arg,context, visited));
				}			
				return Value.V_LIST(values);			
			} else if (expr instanceof Expr.Record) {
				Expr.Record rg = (Expr.Record) expr;
				HashMap<String,Value> values = new HashMap<String,Value>();
				for(Map.Entry<String,Expr> e : rg.fields.entrySet()) {
					Value v = resolveAsConstant(e.getValue(),context,visited);
					if(v == null) {
						return null;
					}
					values.put(e.getKey(), v);
				}
				return Value.V_RECORD(values);
			} else if (expr instanceof Expr.Tuple) {
				Expr.Tuple rg = (Expr.Tuple) expr;			
				ArrayList<Value> values = new ArrayList<Value>();			
				for(Expr e : rg.fields) {
					Value v = resolveAsConstant(e, context,visited);
					if(v == null) {
						return null;
					}
					values.add(v);				
				}
				return Value.V_TUPLE(values);
			}  else if (expr instanceof Expr.Dictionary) {
				Expr.Dictionary rg = (Expr.Dictionary) expr;			
				HashSet<Pair<Value,Value>> values = new HashSet<Pair<Value,Value>>();			
				for(Pair<Expr,Expr> e : rg.pairs) {
					Value key = resolveAsConstant(e.first(), context,visited);
					Value value = resolveAsConstant(e.second(), context,visited);
					if(key == null || value == null) {
						return null;
					}
					values.add(new Pair<Value,Value>(key,value));				
				}
				return Value.V_DICTIONARY(values);
			} else if (expr instanceof Expr.FunctionOrMethodOrMessage) {
				Expr.FunctionOrMethodOrMessage f = (Expr.FunctionOrMethodOrMessage) expr;
				return Value.V_FUN(f.nid, f.type.raw());
			}
		} catch(SyntaxError.InternalFailure e) {
			throw e;
		} catch(ResolveError e) {
			syntaxError(e.getMessage(),context,expr,e);
		} catch(Throwable e) {
			internalFailure(e.getMessage(),context,expr,e);
		}

		internalFailure("unknown constant expression: " + expr.getClass().getName(),context,expr);
		return null; // deadcode
	}

	private Value evaluate(Expr.BinOp bop, Value v1, Value v2, Context context) {
		Type lub = Type.Union(v1.type(), v2.type());

		// FIXME: there are bugs here related to coercions.

		if(Type.isSubtype(Type.T_BOOL, lub)) {
			return evaluateBoolean(bop,(Value.Bool) v1,(Value.Bool) v2, context);
		} else if(Type.isSubtype(Type.T_REAL, lub)) {
			return evaluate(bop,(Value.Rational) v1, (Value.Rational) v2, context);
		} else if(Type.isSubtype(Type.T_LIST_ANY, lub)) {
			return evaluate(bop,(Value.List)v1,(Value.List)v2, context);
		} else if(Type.isSubtype(Type.T_SET_ANY, lub)) {
			return evaluate(bop,(Value.Set) v1, (Value.Set) v2, context);
		} 
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION),context,bop);
		return null;
	}

	private Value evaluateBoolean(Expr.BinOp bop, Value.Bool v1, Value.Bool v2, Context context) {				
		switch(bop.op) {
		case AND:
			return Value.V_BOOL(v1.value & v2.value);
		case OR:		
			return Value.V_BOOL(v1.value | v2.value);
		case XOR:
			return Value.V_BOOL(v1.value ^ v2.value);
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),context,bop);
		return null;
	}

	private Value evaluate(Expr.BinOp bop, Value.Rational v1, Value.Rational v2, Context context) {		
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
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION),context,bop);
		return null;
	}

	private Value evaluate(Expr.BinOp bop, Value.List v1, Value.List v2, Context context) {
		switch(bop.op) {
		case ADD:
			ArrayList<Value> vals = new ArrayList<Value>(v1.values);
			vals.addAll(v2.values);
			return Value.V_LIST(vals);
		}
		syntaxError(errorMessage(INVALID_LIST_EXPRESSION),context,bop);
		return null;
	}

	private Value evaluate(Expr.BinOp bop, Value.Set v1, Value.Set v2, Context context) {		
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
		syntaxError(errorMessage(INVALID_SET_EXPRESSION),context,bop);
		return null;
	}		
}
