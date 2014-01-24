package wyc.builder;

import static wyc.lang.WhileyFile.*;
import static wyil.util.ErrorMessages.*;

import java.math.BigDecimal;
import java.util.*;

import wyautl_old.lang.Automata;
import wyautl_old.lang.Automaton;
import wybs.lang.NameID;
import wybs.lang.Path;
import wybs.lang.SyntaxError;
import wybs.util.Pair;
import wybs.util.ResolveError;
import wybs.util.Trie;
import wyautl.util.BigRational;
import wyil.lang.*;
import wyc.builder.WhileyBuilder;
import wyc.lang.Expr;
import wyc.lang.SyntacticType;
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
	private final HashMap<NameID, Constant> constantCache = new HashMap();
	
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
						// ok, we have found the name in question. But, is it
						// visible?
						if(isVisible(nid,context)) {
							return nid;
						} else {
							throw new ResolveError(nid + " is not visible");	
						}
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
				if(isVisible(nid,context)) {
					return nid;
				} else {
					throw new ResolveError(nid + " is not visible");	
				}
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
				if(isVisible(nid,context)) {
					return nid;
				} else {
					throw new ResolveError(nid + " is not visible");	
				}
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
	
	public Nominal.Function resolveAsType(SyntacticType.Function t,
			Context context) {
		return (Nominal.Function) resolveAsType((SyntacticType)t,context);
	}
	
	public Nominal.Method resolveAsType(SyntacticType.Method t,
			Context context) {		
		return (Nominal.Method) resolveAsType((SyntacticType)t,context);
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
	public Nominal resolveAsType(SyntacticType type, Context context) {
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
	public Nominal resolveAsUnconstrainedType(SyntacticType type, Context context) {
		Type nominalType = resolveAsType(type, context, true, true);
		Type rawType = resolveAsType(type, context, false, true);
		return Nominal.construct(nominalType, rawType);
	}
	
	private Type resolveAsType(SyntacticType t, Context context,
			boolean nominal, boolean unconstrained) {
		
		if(t instanceof SyntacticType.Primitive) { 
			if (t instanceof SyntacticType.Any) {
				return Type.T_ANY;
			} else if (t instanceof SyntacticType.Void) {
				return Type.T_VOID;
			} else if (t instanceof SyntacticType.Null) {
				return Type.T_NULL;
			} else if (t instanceof SyntacticType.Bool) {
				return Type.T_BOOL;
			} else if (t instanceof SyntacticType.Byte) {
				return Type.T_BYTE;
			} else if (t instanceof SyntacticType.Char) {
				return Type.T_CHAR;
			} else if (t instanceof SyntacticType.Int) {
				return Type.T_INT;
			} else if (t instanceof SyntacticType.Real) {
				return Type.T_REAL;
			} else if (t instanceof SyntacticType.Strung) {
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
	private int resolveAsType(SyntacticType type, Context context,
			ArrayList<Automaton.State> states, HashMap<NameID, Integer> roots,
			boolean nominal, boolean unconstrained) {			
		
		if(type instanceof SyntacticType.Primitive) {
			return resolveAsType((SyntacticType.Primitive)type,context,states);
		} 
		
		int myIndex = states.size();
		int myKind;
		int[] myChildren;
		Object myData = null;
		boolean myDeterministic = true;
		
		states.add(null); // reserve space for me
		
		if(type instanceof SyntacticType.List) {
			SyntacticType.List lt = (SyntacticType.List) type;
			myKind = Type.K_LIST;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(lt.element,context,states,roots,nominal,unconstrained);
			myData = false;
		} else if(type instanceof SyntacticType.Set) {
			SyntacticType.Set st = (SyntacticType.Set) type;
			myKind = Type.K_SET;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(st.element,context,states,roots,nominal,unconstrained);
			myData = false;
		} else if(type instanceof SyntacticType.Map) {
			SyntacticType.Map st = (SyntacticType.Map) type;
			myKind = Type.K_MAP;
			myChildren = new int[2];
			myChildren[0] = resolveAsType(st.key,context,states,roots,nominal,unconstrained);
			myChildren[1] = resolveAsType(st.value,context,states,roots,nominal,unconstrained);			
		} else if(type instanceof SyntacticType.Record) {
			SyntacticType.Record tt = (SyntacticType.Record) type;
			HashMap<String,SyntacticType> ttTypes = tt.types;			
			Type.Record.State fields = new Type.Record.State(tt.isOpen,ttTypes.keySet());
			Collections.sort(fields);			
			myKind = Type.K_RECORD;
			myChildren = new int[fields.size()];
			for(int i=0;i!=fields.size();++i) {	
				String field = fields.get(i);
				myChildren[i] = resolveAsType(ttTypes.get(field),context,states,roots,nominal,unconstrained);
			}						
			myData = fields;
		} else if(type instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple tt = (SyntacticType.Tuple) type;
			ArrayList<SyntacticType> ttTypes = tt.types;
			myKind = Type.K_TUPLE;
			myChildren = new int[ttTypes.size()];
			for(int i=0;i!=ttTypes.size();++i) {
				myChildren[i] = resolveAsType(ttTypes.get(i),context,states,roots,nominal,unconstrained);				
			}			
		} else if(type instanceof SyntacticType.Nominal) {
			// This case corresponds to a user-defined type. This will be
			// defined in some module (possibly ours), and we need to identify
			// what module that is here, and save it for future use.
			SyntacticType.Nominal dt = (SyntacticType.Nominal) type;									
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
		} else if(type instanceof SyntacticType.Negation) {	
			SyntacticType.Negation ut = (SyntacticType.Negation) type;
			myKind = Type.K_NEGATION;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element,context,states,roots,nominal,unconstrained);			
		} else if(type instanceof SyntacticType.Union) {
			SyntacticType.Union ut = (SyntacticType.Union) type;
			ArrayList<SyntacticType.NonUnion> utTypes = ut.bounds;
			myKind = Type.K_UNION;
			myChildren = new int[utTypes.size()];
			for(int i=0;i!=utTypes.size();++i) {
				myChildren[i] = resolveAsType(utTypes.get(i),context,states,roots,nominal,unconstrained);				
			}	
			myDeterministic = false;
		} else if(type instanceof SyntacticType.Intersection) {
			internalFailure("intersection types not supported yet",context,type);
			return 0; // dead-code
		} else if(type instanceof SyntacticType.Reference) {	
			SyntacticType.Reference ut = (SyntacticType.Reference) type;
			myKind = Type.K_REFERENCE;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element,context,states,roots,nominal,unconstrained);		
		} else {			
			SyntacticType.FunctionOrMethod ut = (SyntacticType.FunctionOrMethod) type;			
			ArrayList<SyntacticType> utParamTypes = ut.paramTypes;
			int start = 0;
			
			if(ut instanceof SyntacticType.Method) {
				myKind = Type.K_METHOD;
			} else {
				myKind = Type.K_FUNCTION;
			}
			
			myChildren = new int[start + 2 + utParamTypes.size()];
					
			myChildren[start++] = resolveAsType(ut.ret,context,states,roots,nominal,unconstrained);
			if(ut.throwType == null) {
				// this case indicates the user did not provide a throws clause.
				myChildren[start++] = resolveAsType(new SyntacticType.Void(),context,states,roots,nominal,unconstrained);
			} else {
				myChildren[start++] = resolveAsType(ut.throwType,context,states,roots,nominal,unconstrained);
			}
			for(SyntacticType pt : utParamTypes) {
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
			WyilFile.TypeDeclaration td = mi.type(key.name());	
			return append(td.type(),states);			
		} 
		
		WhileyFile.Type td = wf.typeDecl(key.name());
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
		SyntacticType type = td.pattern.toSyntacticType();
		
		// now, expand the given type fully	
		if(unconstrained && td.constraint != null) {
			int myIndex = states.size();
			int kind = Type.leafKind(Type.T_VOID);			
			Object data = null;
			states.add(new Automaton.State(kind,data,true,Automaton.NOCHILDREN));
			return myIndex;
		} else if(type instanceof Type.Leaf) {
			//
			// FIXME: I believe this code is now redundant, and should be
			// removed or updated. The problem is that SyntacticType no longer
			// extends Type.
			//
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
	
	private int resolveAsType(SyntacticType.Primitive t,
			Context context, ArrayList<Automaton.State> states) {
		int myIndex = states.size();
		int kind;
		if (t instanceof SyntacticType.Any) {
			kind = Type.K_ANY;
		} else if (t instanceof SyntacticType.Void) {
			kind = Type.K_VOID;
		} else if (t instanceof SyntacticType.Null) {
			kind = Type.K_NULL;
		} else if (t instanceof SyntacticType.Bool) {
			kind = Type.K_BOOL;
		} else if (t instanceof SyntacticType.Byte) {
			kind = Type.K_BYTE;
		} else if (t instanceof SyntacticType.Char) {
			kind = Type.K_CHAR;
		} else if (t instanceof SyntacticType.Int) {
			kind = Type.K_INT;
		} else if (t instanceof SyntacticType.Real) {
			kind = Type.K_RATIONAL;
		} else if (t instanceof SyntacticType.Strung) {
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
	
	public Constant resolveAsConstant(NameID nid) throws Exception {				
		return resolveAsConstant(nid,new HashSet<NameID>());		
	}

	public Constant resolveAsConstant(Expr e, Context context) {		
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
	private Constant resolveAsConstant(NameID key, HashSet<NameID> visited) throws Exception {				
		Constant result = constantCache.get(key);
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
			WyilFile.ConstantDeclaration cd = module.constant(key.name());
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
	private Constant resolveAsConstant(Expr expr, Context context,
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
				Constant lhs = resolveAsConstant(bop.lhs, context, visited);
				Constant rhs = resolveAsConstant(bop.rhs, context, visited);
				return evaluate(bop,lhs,rhs,context);			
			} else if (expr instanceof Expr.UnOp) {
				Expr.UnOp uop = (Expr.UnOp) expr;
				Constant lhs = resolveAsConstant(uop.mhs, context, visited);				
				return evaluate(uop,lhs,context);			
			} else if (expr instanceof Expr.Set) {
				Expr.Set nop = (Expr.Set) expr;
				ArrayList<Constant> values = new ArrayList<Constant>();
				for (Expr arg : nop.arguments) {
					values.add(resolveAsConstant(arg,context, visited));
				}			
				return Constant.V_SET(values);			
			} else if (expr instanceof Expr.List) {
				Expr.List nop = (Expr.List) expr;
				ArrayList<Constant> values = new ArrayList<Constant>();
				for (Expr arg : nop.arguments) {
					values.add(resolveAsConstant(arg,context, visited));
				}			
				return Constant.V_LIST(values);			
			} else if (expr instanceof Expr.Record) {
				Expr.Record rg = (Expr.Record) expr;
				HashMap<String,Constant> values = new HashMap<String,Constant>();
				for(Map.Entry<String,Expr> e : rg.fields.entrySet()) {
					Constant v = resolveAsConstant(e.getValue(),context,visited);
					if(v == null) {
						return null;
					}
					values.put(e.getKey(), v);
				}
				return Constant.V_RECORD(values);
			} else if (expr instanceof Expr.Tuple) {
				Expr.Tuple rg = (Expr.Tuple) expr;			
				ArrayList<Constant> values = new ArrayList<Constant>();			
				for(Expr e : rg.fields) {
					Constant v = resolveAsConstant(e, context,visited);
					if(v == null) {
						return null;
					}
					values.add(v);				
				}
				return Constant.V_TUPLE(values);
			}  else if (expr instanceof Expr.Map) {
				Expr.Map rg = (Expr.Map) expr;			
				HashSet<Pair<Constant,Constant>> values = new HashSet<Pair<Constant,Constant>>();			
				for(Pair<Expr,Expr> e : rg.pairs) {
					Constant key = resolveAsConstant(e.first(), context,visited);
					Constant value = resolveAsConstant(e.second(), context,visited);
					if(key == null || value == null) {
						return null;
					}
					values.add(new Pair<Constant,Constant>(key,value));				
				}
				return Constant.V_MAP(values);
			} else if (expr instanceof Expr.FunctionOrMethod) {
				// TODO: add support for proper lambdas
				Expr.FunctionOrMethod f = (Expr.FunctionOrMethod) expr;
				return Constant.V_LAMBDA(f.nid, f.type.raw());
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

	private Constant evaluate(Expr.UnOp bop, Constant v, Context context) {
		switch(bop.op) {
			case NOT:
				if(v instanceof Constant.Bool) {
					Constant.Bool b = (Constant.Bool) v;
					return Constant.V_BOOL(!b.value);
				}
				syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),context,bop);
				break;
			case NEG:
				if(v instanceof Constant.Integer) {
					Constant.Integer b = (Constant.Integer) v;
					return Constant.V_INTEGER(b.value.negate());
				} else if(v instanceof Constant.Decimal) {
					Constant.Decimal b = (Constant.Decimal) v;
					return Constant.V_DECIMAL(b.value.negate());
				}
				syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION),context,bop);
				break;
			case INVERT:
				if(v instanceof Constant.Byte) {
					Constant.Byte b = (Constant.Byte) v;
					return Constant.V_BYTE((byte) ~b.value);
				}
				break;
		}
		syntaxError(errorMessage(INVALID_UNARY_EXPRESSION),context,bop);
		return null;
	}
	
	private Constant evaluate(Expr.BinOp bop, Constant v1, Constant v2, Context context) {
		Type v1_type = v1.type();
		Type v2_type = v2.type();
		Type lub = Type.Union(v1_type, v2_type);

		// FIXME: there are bugs here related to coercions.

		if(Type.isSubtype(Type.T_BOOL, lub)) {
			return evaluateBoolean(bop,(Constant.Bool) v1,(Constant.Bool) v2, context);
		} else if(Type.isSubtype(Type.T_INT, lub)) {
			return evaluate(bop,(Constant.Integer) v1, (Constant.Integer) v2, context);
		} else if (Type.isImplicitCoerciveSubtype(Type.T_REAL, v1_type)
				&& Type.isImplicitCoerciveSubtype(Type.T_REAL, v1_type)) {			
			if(v1 instanceof Constant.Integer) {
				Constant.Integer i1 = (Constant.Integer) v1;
				v1 = Constant.V_DECIMAL(new BigDecimal(i1.value));
			} else if(v2 instanceof Constant.Integer) {
				Constant.Integer i2 = (Constant.Integer) v2;
				v2 = Constant.V_DECIMAL(new BigDecimal(i2.value));
			}
			return evaluate(bop,(Constant.Decimal) v1, (Constant.Decimal) v2, context);
		} else if(Type.isSubtype(Type.T_LIST_ANY, lub)) {
			return evaluate(bop,(Constant.List)v1,(Constant.List)v2, context);
		} else if(Type.isSubtype(Type.T_SET_ANY, lub)) {
			return evaluate(bop,(Constant.Set) v1, (Constant.Set) v2, context);
		} 
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION),context,bop);
		return null;
	}

	private Constant evaluateBoolean(Expr.BinOp bop, Constant.Bool v1, Constant.Bool v2, Context context) {				
		switch(bop.op) {
		case AND:
			return Constant.V_BOOL(v1.value & v2.value);
		case OR:		
			return Constant.V_BOOL(v1.value | v2.value);
		case XOR:
			return Constant.V_BOOL(v1.value ^ v2.value);
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),context,bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Integer v1, Constant.Integer v2, Context context) {		
		switch(bop.op) {
		case ADD:
			return Constant.V_INTEGER(v1.value.add(v2.value));
		case SUB:
			return Constant.V_INTEGER(v1.value.subtract(v2.value));
		case MUL:
			return Constant.V_INTEGER(v1.value.multiply(v2.value));
		case DIV:
			return Constant.V_INTEGER(v1.value.divide(v2.value));
		case REM:
			return Constant.V_INTEGER(v1.value.remainder(v2.value));	
		}
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION),context,bop);
		return null;
	}
	
	private Constant evaluate(Expr.BinOp bop, Constant.Decimal v1, Constant.Decimal v2, Context context) {		
		switch(bop.op) {
		case ADD:
			return Constant.V_DECIMAL(v1.value.add(v2.value));
		case SUB:
			return Constant.V_DECIMAL(v1.value.subtract(v2.value));
		case MUL:
			return Constant.V_DECIMAL(v1.value.multiply(v2.value));
		case DIV:
			return Constant.V_DECIMAL(v1.value.divide(v2.value));			
		}
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION),context,bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.List v1, Constant.List v2, Context context) {
		switch(bop.op) {
		case ADD:
			ArrayList<Constant> vals = new ArrayList<Constant>(v1.values);
			vals.addAll(v2.values);
			return Constant.V_LIST(vals);
		}
		syntaxError(errorMessage(INVALID_LIST_EXPRESSION),context,bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Set v1, Constant.Set v2, Context context) {		
		switch(bop.op) {
		case UNION:
		{
			HashSet<Constant> vals = new HashSet<Constant>(v1.values);			
			vals.addAll(v2.values);
			return Constant.V_SET(vals);
		}
		case INTERSECTION:
		{
			HashSet<Constant> vals = new HashSet<Constant>();			
			for(Constant v : v1.values) {
				if(v2.values.contains(v)) {
					vals.add(v);
				}
			}			
			return Constant.V_SET(vals);
		}
		case SUB:
		{
			HashSet<Constant> vals = new HashSet<Constant>();			
			for(Constant v : v1.values) {
				if(!v2.values.contains(v)) {
					vals.add(v);
				}
			}			
			return Constant.V_SET(vals);
		}
		}
		syntaxError(errorMessage(INVALID_SET_EXPRESSION),context,bop);
		return null;
	}		
	
	public boolean isVisible(NameID nid, Context context) throws Exception {		
		Path.ID mid = nid.module();
		if(mid.equals(context.file().module)) {
			return true;
		}
		WhileyFile wf = builder.getSourceFile(mid);
		if(wf != null) {
			Declaration d = wf.declaration(nid.name());
			if(d instanceof WhileyFile.Constant) {
				WhileyFile.Constant td = (WhileyFile.Constant) d;
				return td.isPublic() || td.isProtected();
			} else if(d instanceof WhileyFile.Type) {
				WhileyFile.Type td = (WhileyFile.Type) d;
				return td.isPublic() || td.isProtected();	
			}
			return false;
		} else {
			// we have to do the following basically because we don't load
			// modifiers properly out of jvm class files (at the moment).
			return true;
//			WyilFile w = builder.getModule(mid);
//			WyilFile.ConstDef c = w.constant(nid.name());
//			WyilFile.TypeDef t = w.type(nid.name());
//			if(c != null) {
//				return c.isPublic() || c.isProtected();
//			} else {
//				return t.isPublic() || t.isProtected();
//			}
		}		
	}
}
