package wyc.builder;

import static wyc.lang.WhileyFile.internalFailure;
import static wyc.lang.WhileyFile.syntaxError;
import static wyil.util.ErrorMessages.INVALID_BINARY_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_BOOLEAN_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_LIST_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_NUMERIC_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_SET_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_UNARY_EXPRESSION;
import static wyil.util.ErrorMessages.errorMessage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import wyautl_old.lang.Automata;
import wyautl_old.lang.Automaton;
import wybs.lang.NameID;
import wybs.lang.Path;
import wybs.lang.SyntaxError;
import wybs.util.Pair;
import wybs.util.ResolveError;
import wybs.util.Trie;
import wyc.lang.Expr;
import wyc.lang.Nominal;
import wyc.lang.SyntacticType;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Context;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.WyilFile;

/**
 * <p>
 * Responsible for resolve names, types, constants and functions / methods at
 * the global level. Resolution is determined by the context in which a given
 * name/type/constant/function/method appears. That is, what imports are active
 * in the enclosing WhileyFile. For example, consider this:
 * </p>
 * 
 * <pre>
 * import whiley.lang.*
 * 
 * type nat is Int.uint
 * 
 * import whiley.ui.*
 * </pre>
 * 
 * <p>
 * In this example, the statement "<code>import whiley.lang.*</code>" is active
 * for the type declaration, whilst the statement "
 * <code>import whiley.ui.*</code>". The context of the type declaration is
 * everything in the enclosing file up to the declaration itself. Therefore, in
 * resolving the name <code>Int.uint</code>, this will examine the package
 * whiley.lang to see whether a compilation unit named "Int" exists. If so, it
 * will then resolve the name <code>Int.uint</code> to
 * <code>whiley.lang.Int.uint</code>.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class GlobalResolver {

	/**
	 * Access to the builder is needed to load declarations (e.g. types,
	 * functions, etc) from other compilation units (e.g. from other source
	 * files in the group being compiled or from WyIL files).
	 */
	private WhileyBuilder builder;

	/**
	 * The constant cache contains a cache of expanded constant values. This is
	 * simply to prevent recomputing them every time.
	 */
	private final HashMap<NameID, Constant> constantCache = new HashMap<NameID, Constant>();

	public GlobalResolver(WhileyBuilder builder) {
		this.builder = builder;
	}

	// =========================================================================
	// Resolve as Function or Method
	// =========================================================================

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
	public Nominal.FunctionOrMethod resolveAsFunctionOrMethod(NameID nid,
			List<Nominal> parameters, Context context) throws Exception {
		HashSet<Pair<NameID, Nominal.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Nominal.FunctionOrMethod>>();

		addCandidateFunctionsAndMethods(nid, parameters, candidates, context);

		return selectCandidateFunctionOrMethod(nid.name(), parameters,
				candidates, context).second();
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
	public Pair<NameID, Nominal.FunctionOrMethod> resolveAsFunctionOrMethod(
			String name, Context context) throws Exception {
		return resolveAsFunctionOrMethod(name, null, context);
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
	public Pair<NameID, Nominal.FunctionOrMethod> resolveAsFunctionOrMethod(
			String name, List<Nominal> parameters, Context context)
			throws Exception {

		HashSet<Pair<NameID, Nominal.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Nominal.FunctionOrMethod>>();
		// first, try to find the matching message
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
					addCandidateFunctionsAndMethods(nid, parameters,
							candidates, context);
				}
			}
		}

		return selectCandidateFunctionOrMethod(name, parameters, candidates,
				context);
	}

	private boolean paramSubtypes(Type.FunctionOrMethod f1,
			Type.FunctionOrMethod f2) {
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if (f1_params.size() == f2_params.size()) {
			for (int i = 0; i != f1_params.size(); ++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);
				if (!Type.isImplicitCoerciveSubtype(f1_param, f2_param)) {
					return false;
				}
			}

			return true;
		}
		return false;
	}

	private boolean paramStrictSubtypes(Type.FunctionOrMethod f1,
			Type.FunctionOrMethod f2) {
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if (f1_params.size() == f2_params.size()) {
			boolean allEqual = true;
			for (int i = 0; i != f1_params.size(); ++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);
				if (!Type.isImplicitCoerciveSubtype(f1_param, f2_param)) {
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
		if (paramTypes == null) {
			paramStr += "...";
		} else {
			for (Nominal t : paramTypes) {
				if (!firstTime) {
					paramStr += ",";
				}
				firstTime = false;
				paramStr += t.nominal();
			}
		}
		return paramStr + ")";
	}

	private Pair<NameID, Nominal.FunctionOrMethod> selectCandidateFunctionOrMethod(
			String name, List<Nominal> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates,
			Context context) throws Exception {

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
		for (Pair<NameID, Nominal.FunctionOrMethod> p : candidates) {
			Nominal.FunctionOrMethod nft = p.second();
			Type.FunctionOrMethod ft = nft.raw();
			if (parameters == null || paramSubtypes(ft, target)) {
				// this is now a genuine candidate
				if (candidateType == null
						|| paramStrictSubtypes(candidateType.raw(), ft)) {
					candidateType = nft;
					candidateID = p.first();
				} else if (!paramStrictSubtypes(ft, candidateType.raw())) {
					// this is an ambiguous error
					String msg = name + parameterString(parameters)
							+ " is ambiguous";
					// FIXME: should report all ambiguous matches here
					msg += "\n\tfound: " + candidateID + " : "
							+ candidateType.nominal();
					msg += "\n\tfound: " + p.first() + " : "
							+ p.second().nominal();
					throw new ResolveError(msg);
				}
			}
		}

		if (candidateType == null) {
			// second, didn't find matching message so generate error message
			String msg = "no match for " + name + parameterString(parameters);

			for (Pair<NameID, Nominal.FunctionOrMethod> p : candidates) {
				msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
			}

			throw new ResolveError(msg);
		} else {
			// now check protection modified
			WhileyFile wf = builder.getSourceFile(candidateID.module());
			if (wf != null) {
				if (wf != context.file()) {
					for (WhileyFile.FunctionOrMethod d : wf.declarations(
							WhileyFile.FunctionOrMethod.class,
							candidateID.name())) {
						if (d.parameters.equals(candidateType.params())) {
							if (!d.isPublic() && !d.isProtected()) {
								String msg = candidateID.module() + "." + name
										+ parameterString(parameters)
										+ " is not visible";
								throw new ResolveError(msg);
							}
						}
					}
				}
			} else {
				WyilFile m = builder.getModule(candidateID.module());
				WyilFile.MethodDeclaration d = m.method(candidateID.name(),
						candidateType.raw());
				if (!d.isPublic() && !d.isProtected()) {
					String msg = candidateID.module() + "." + name
							+ parameterString(parameters) + " is not visible";
					throw new ResolveError(msg);
				}
			}
		}

		return new Pair<NameID, Nominal.FunctionOrMethod>(candidateID,
				candidateType);
	}

	private void addCandidateFunctionsAndMethods(NameID nid,
			List<?> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates,
			Context context) throws Exception {
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
				for (WyilFile.MethodDeclaration mm : m.methods()) {
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

	private static List<Type> stripNominal(List<Nominal> types) {
		ArrayList<Type> r = new ArrayList<Type>();
		for (Nominal t : types) {
			r.add(t.raw());
		}
		return r;
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
	public NameID resolveAsName(String name, Context context) throws Exception {
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
						if (isVisible(nid, context)) {
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
	public NameID resolveAsName(List<String> names, Context context)
			throws Exception {
		if (names.size() == 1) {
			return resolveAsName(names.get(0), context);
		} else if (names.size() == 2) {
			String name = names.get(1);
			Path.ID mid = resolveAsModule(names.get(0), context);
			NameID nid = new NameID(mid, name);
			if (builder.isName(nid)) {
				if (isVisible(nid, context)) {
					return nid;
				} else {
					throw new ResolveError(nid + " is not visible");
				}
			}
		} else {
			String name = names.get(names.size() - 1);
			String module = names.get(names.size() - 2);
			Path.ID pkg = Trie.ROOT;
			for (int i = 0; i != names.size() - 2; ++i) {
				pkg = pkg.append(names.get(i));
			}
			Path.ID mid = pkg.append(module);
			NameID nid = new NameID(mid, name);
			if (builder.isName(nid)) {
				if (isVisible(nid, context)) {
					return nid;
				} else {
					throw new ResolveError(nid + " is not visible");
				}
			}
		}

		String name = null;
		for (String n : names) {
			if (name != null) {
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
			} else if (!last.equals(name)) {
				continue; // skip as not relevant
			}

			for (Path.ID mid : builder.imports(filter)) {
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
		return (Nominal.Function) resolveAsType((SyntacticType) t, context);
	}

	public Nominal.Method resolveAsType(SyntacticType.Method t, Context context) {
		return (Nominal.Method) resolveAsType((SyntacticType) t, context);
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
	public Nominal resolveAsUnconstrainedType(SyntacticType type,
			Context context) {
		Type nominalType = resolveAsType(type, context, true, true);
		Type rawType = resolveAsType(type, context, false, true);
		return Nominal.construct(nominalType, rawType);
	}

	private Type resolveAsType(SyntacticType t, Context context,
			boolean nominal, boolean unconstrained) {

		if (t instanceof SyntacticType.Primitive) {
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
						+ t.getClass().getName() + ")", context, t);
				return null; // deadcode
			}
		} else {
			ArrayList<Automaton.State> states = new ArrayList<Automaton.State>();
			HashMap<NameID, Integer> roots = new HashMap<NameID, Integer>();
			resolveAsType(t, context, states, roots, nominal, unconstrained);
			return Type.construct(new Automaton(states));
		}
	}

	/**
	 * The following method resolves a syntactic type in a given context.
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

		if (type instanceof SyntacticType.Primitive) {
			return resolveAsType((SyntacticType.Primitive) type, context,
					states);
		}

		int myIndex = states.size();
		int myKind;
		int[] myChildren;
		Object myData = null;
		boolean myDeterministic = true;

		states.add(null); // reserve space for me

		if (type instanceof SyntacticType.List) {
			SyntacticType.List lt = (SyntacticType.List) type;
			myKind = Type.K_LIST;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(lt.element, context, states, roots,
					nominal, unconstrained);
			myData = false;
		} else if (type instanceof SyntacticType.Set) {
			SyntacticType.Set st = (SyntacticType.Set) type;
			myKind = Type.K_SET;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(st.element, context, states, roots,
					nominal, unconstrained);
			myData = false;
		} else if (type instanceof SyntacticType.Map) {
			SyntacticType.Map st = (SyntacticType.Map) type;
			myKind = Type.K_MAP;
			myChildren = new int[2];
			myChildren[0] = resolveAsType(st.key, context, states, roots,
					nominal, unconstrained);
			myChildren[1] = resolveAsType(st.value, context, states, roots,
					nominal, unconstrained);
		} else if (type instanceof SyntacticType.Record) {
			SyntacticType.Record tt = (SyntacticType.Record) type;
			HashMap<String, SyntacticType> ttTypes = tt.types;
			Type.Record.State fields = new Type.Record.State(tt.isOpen,
					ttTypes.keySet());
			Collections.sort(fields);
			myKind = Type.K_RECORD;
			myChildren = new int[fields.size()];
			for (int i = 0; i != fields.size(); ++i) {
				String field = fields.get(i);
				myChildren[i] = resolveAsType(ttTypes.get(field), context,
						states, roots, nominal, unconstrained);
			}
			myData = fields;
		} else if (type instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple tt = (SyntacticType.Tuple) type;
			ArrayList<SyntacticType> ttTypes = tt.types;
			myKind = Type.K_TUPLE;
			myChildren = new int[ttTypes.size()];
			for (int i = 0; i != ttTypes.size(); ++i) {
				myChildren[i] = resolveAsType(ttTypes.get(i), context, states,
						roots, nominal, unconstrained);
			}
		} else if (type instanceof SyntacticType.Nominal) {
			// This case corresponds to a user-defined type. This will be
			// defined in some module (possibly ours), and we need to identify
			// what module that is here, and save it for future use.
			SyntacticType.Nominal dt = (SyntacticType.Nominal) type;
			NameID nid;
			try {
				nid = resolveAsName(dt.names, context);

				if (nominal) {
					myKind = Type.K_NOMINAL;
					myData = nid;
					myChildren = Automaton.NOCHILDREN;
				} else {
					// At this point, we're going to expand the given nominal
					// type.
					// We're going to use resolveAsType(NameID,...) to do this
					// which
					// will load the expanded type onto states at the current
					// point.
					// Therefore, we need to remove the initial null we loaded
					// on.
					states.remove(myIndex);
					return resolveAsType(nid, states, roots, unconstrained);
				}
			} catch (ResolveError e) {
				syntaxError(e.getMessage(), context, dt, e);
				return 0; // dead-code
			} catch (SyntaxError e) {
				throw e;
			} catch (Throwable e) {
				internalFailure(e.getMessage(), context, dt, e);
				return 0; // dead-code
			}
		} else if (type instanceof SyntacticType.Negation) {
			SyntacticType.Negation ut = (SyntacticType.Negation) type;
			myKind = Type.K_NEGATION;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element, context, states, roots,
					nominal, unconstrained);
		} else if (type instanceof SyntacticType.Union) {
			SyntacticType.Union ut = (SyntacticType.Union) type;
			ArrayList<SyntacticType.NonUnion> utTypes = ut.bounds;
			myKind = Type.K_UNION;
			myChildren = new int[utTypes.size()];
			for (int i = 0; i != utTypes.size(); ++i) {
				myChildren[i] = resolveAsType(utTypes.get(i), context, states,
						roots, nominal, unconstrained);
			}
			myDeterministic = false;
		} else if (type instanceof SyntacticType.Intersection) {
			internalFailure("intersection types not supported yet", context,
					type);
			return 0; // dead-code
		} else if (type instanceof SyntacticType.Reference) {
			SyntacticType.Reference ut = (SyntacticType.Reference) type;
			myKind = Type.K_REFERENCE;
			myChildren = new int[1];
			myChildren[0] = resolveAsType(ut.element, context, states, roots,
					nominal, unconstrained);
		} else {
			SyntacticType.FunctionOrMethod ut = (SyntacticType.FunctionOrMethod) type;
			ArrayList<SyntacticType> utParamTypes = ut.paramTypes;
			int start = 0;

			if (ut instanceof SyntacticType.Method) {
				myKind = Type.K_METHOD;
			} else {
				myKind = Type.K_FUNCTION;
			}

			myChildren = new int[start + 2 + utParamTypes.size()];

			myChildren[start++] = resolveAsType(ut.ret, context, states, roots,
					nominal, unconstrained);
			if (ut.throwType == null) {
				// this case indicates the user did not provide a throws clause.
				myChildren[start++] = resolveAsType(new SyntacticType.Void(),
						context, states, roots, nominal, unconstrained);
			} else {
				myChildren[start++] = resolveAsType(ut.throwType, context,
						states, roots, nominal, unconstrained);
			}
			for (SyntacticType pt : utParamTypes) {
				myChildren[start++] = resolveAsType(pt, context, states, roots,
						nominal, unconstrained);
			}
		}

		states.set(myIndex, new Automaton.State(myKind, myData,
				myDeterministic, myChildren));

		return myIndex;
	}

	private int resolveAsType(NameID key, ArrayList<Automaton.State> states,
			HashMap<NameID, Integer> roots, boolean unconstrained)
			throws Exception {

		// First, check the various caches we have
		Integer root = roots.get(key);
		if (root != null) {
			return root;
		}

		// check whether this type is external or not
		WhileyFile wf = builder.getSourceFile(key.module());
		if (wf == null) {
			// indicates a non-local key which we can resolve immediately

			// FIXME: need to properly support unconstrained types here

			WyilFile mi = builder.getModule(key.module());
			WyilFile.TypeDeclaration td = mi.type(key.name());
			return append(td.type(), states);
		}

		WhileyFile.Type td = wf.typeDecl(key.name());
		if (td == null) {
			Type t = resolveAsConstant(key).type();
			if (t instanceof Type.Set) {
				if (unconstrained) {
					// crikey this is ugly
					int myIndex = states.size();
					int kind = Type.leafKind(Type.T_VOID);
					Object data = null;
					states.add(new Automaton.State(kind, data, true,
							Automaton.NOCHILDREN));
					return myIndex;
				}
				Type.Set ts = (Type.Set) t;
				return append(ts.element(), states);
			} else {
				throw new ResolveError("type not found: " + key);
			}
		}

		// following is needed to terminate any recursion
		roots.put(key, states.size());
		SyntacticType type = td.pattern.toSyntacticType();

		// now, expand the given type fully
		if (unconstrained && td.invariant != null) {
			int myIndex = states.size();
			int kind = Type.leafKind(Type.T_VOID);
			Object data = null;
			states.add(new Automaton.State(kind, data, true,
					Automaton.NOCHILDREN));
			return myIndex;
		} else if (type instanceof Type.Leaf) {
			//
			// FIXME: I believe this code is now redundant, and should be
			// removed or updated. The problem is that SyntacticType no longer
			// extends Type.
			//
			int myIndex = states.size();
			int kind = Type.leafKind((Type.Leaf) type);
			Object data = Type.leafData((Type.Leaf) type);
			states.add(new Automaton.State(kind, data, true,
					Automaton.NOCHILDREN));
			return myIndex;
		} else {
			return resolveAsType(type, td, states, roots, false, unconstrained);
		}

		// TODO: performance can be improved here, but actually assigning the
		// constructed type into a cache of previously expanded types cache.
		// This is challenging, in the case that the type may not be complete at
		// this point. In particular, if it contains any back-links above this
		// index there could be an issue.
	}

	private int resolveAsType(SyntacticType.Primitive t, Context context,
			ArrayList<Automaton.State> states) {
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
					+ t.getClass().getName() + ")", context, t);
			return 0; // dead-code
		}
		states.add(new Automaton.State(kind, null, true, Automaton.NOCHILDREN));
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

	/**
	 * <p>
	 * Resolve a given name as a constant value. This is a global problem, since
	 * a constant declaration in one source file may refer to constants declared
	 * in other compilation units. This function will actually evaluate constant
	 * expressions (e.g. "1+2") to produce actual constant vales.
	 * </p>
	 * 
	 * <p>
	 * Constant declarations form a global graph spanning multiple compilation
	 * units. In resolving a given constant, this function must traverse those
	 * portions of the graph which make up the constant. Constants are not
	 * permitted to be declared recursively (i.e. in terms of themselves) and
	 * this function will report an error is such a recursive cycle is detected
	 * in the constant graph.
	 * </p>
	 * 
	 * @param nid
	 *            Fully qualified name identifier of constant to resolve
	 * @return Constant value representing named constant
	 * @throws Exception
	 */
	public Constant resolveAsConstant(NameID nid) throws Exception {
		return resolveAsConstant(nid, new HashSet<NameID>());
	}

	/**
	 * <p>
	 * Resolve a given <i>constant expression</i> as a constant value. A
	 * constant expression is one which refers only to known and visible
	 * constant values, rather than e.g. local variables. Constant expressions
	 * may still use operators (e.g. "1+2", or "1+c" where c is a declared
	 * constant).
	 * </p>
	 * 
	 * <p>
	 * Constant expressions used in a few places in Whiley. In particular, the
	 * cases of a <code>switch</code> statement must be defined using constant
	 * expressions.
	 * </p>
	 * 
	 * @param e
	 * @param context
	 * @return
	 */
	public Constant resolveAsConstant(Expr e, Context context) {
		return resolveAsConstant(e, context, new HashSet<NameID>());
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
	private Constant resolveAsConstant(NameID key, HashSet<NameID> visited)
			throws Exception {
		Constant result = constantCache.get(key);
		if (result != null) {
			return result;
		} else if (visited.contains(key)) {
			throw new ResolveError("cyclic constant definition encountered ("
					+ key + " -> " + key + ")");
		} else {
			visited.add(key);
		}

		WhileyFile wf = builder.getSourceFile(key.module());

		if (wf != null) {
			WhileyFile.Declaration decl = wf.declaration(key.name());
			if (decl instanceof WhileyFile.Constant) {
				WhileyFile.Constant cd = (WhileyFile.Constant) decl;
				if (cd.resolvedValue == null) {
					// cd.constant = propagate(cd.constant, new Environment(),
					// cd);
					cd.resolvedValue = resolveAsConstant(cd.constant, cd,
							visited);
				}
				result = cd.resolvedValue;
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		} else {
			WyilFile module = builder.getModule(key.module());
			WyilFile.ConstantDeclaration cd = module.constant(key.name());
			if (cd != null) {
				result = cd.constant();
			} else {
				throw new ResolveError("unable to find constant " + key);
			}
		}

		constantCache.put(key, result);

		return result;
	}

	/**
	 * The following is a helper method for resolveAsConstant. It takes a given
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
			} else if (expr instanceof Expr.AbstractVariable) {
				Expr.AbstractVariable v = (Expr.AbstractVariable) expr;
				NameID nid = resolveAsName(v.var,context);
				return resolveAsConstant(nid, visited);
			} else if (expr instanceof Expr.ConstantAccess) {
				Expr.ConstantAccess c = (Expr.ConstantAccess) expr;
				return resolveAsConstant(c.nid, visited);
			} else if (expr instanceof Expr.BinOp) {
				Expr.BinOp bop = (Expr.BinOp) expr;
				Constant lhs = resolveAsConstant(bop.lhs, context, visited);
				Constant rhs = resolveAsConstant(bop.rhs, context, visited);
				return evaluate(bop, lhs, rhs, context);
			} else if (expr instanceof Expr.UnOp) {
				Expr.UnOp uop = (Expr.UnOp) expr;
				Constant lhs = resolveAsConstant(uop.mhs, context, visited);
				return evaluate(uop, lhs, context);
			} else if (expr instanceof Expr.Set) {
				Expr.Set nop = (Expr.Set) expr;
				ArrayList<Constant> values = new ArrayList<Constant>();
				for (Expr arg : nop.arguments) {
					values.add(resolveAsConstant(arg, context, visited));
				}
				return Constant.V_SET(values);
			} else if (expr instanceof Expr.List) {
				Expr.List nop = (Expr.List) expr;
				ArrayList<Constant> values = new ArrayList<Constant>();
				for (Expr arg : nop.arguments) {
					values.add(resolveAsConstant(arg, context, visited));
				}
				return Constant.V_LIST(values);
			} else if (expr instanceof Expr.Record) {
				Expr.Record rg = (Expr.Record) expr;
				HashMap<String, Constant> values = new HashMap<String, Constant>();
				for (Map.Entry<String, Expr> e : rg.fields.entrySet()) {
					Constant v = resolveAsConstant(e.getValue(), context,
							visited);
					if (v == null) {
						return null;
					}
					values.put(e.getKey(), v);
				}
				return Constant.V_RECORD(values);
			} else if (expr instanceof Expr.Tuple) {
				Expr.Tuple rg = (Expr.Tuple) expr;
				ArrayList<Constant> values = new ArrayList<Constant>();
				for (Expr e : rg.fields) {
					Constant v = resolveAsConstant(e, context, visited);
					if (v == null) {
						return null;
					}
					values.add(v);
				}
				return Constant.V_TUPLE(values);
			} else if (expr instanceof Expr.Map) {
				Expr.Map rg = (Expr.Map) expr;
				HashSet<Pair<Constant, Constant>> values = new HashSet<Pair<Constant, Constant>>();
				for (Pair<Expr, Expr> e : rg.pairs) {
					Constant key = resolveAsConstant(e.first(), context,
							visited);
					Constant value = resolveAsConstant(e.second(), context,
							visited);
					if (key == null || value == null) {
						return null;
					}
					values.add(new Pair<Constant, Constant>(key, value));
				}
				return Constant.V_MAP(values);
			} else if (expr instanceof Expr.FunctionOrMethod) {
				// TODO: add support for proper lambdas
				Expr.FunctionOrMethod f = (Expr.FunctionOrMethod) expr;
				return Constant.V_LAMBDA(f.nid, f.type.raw());
			}
		} catch (SyntaxError.InternalFailure e) {
			throw e;
		} catch (ResolveError e) {
			syntaxError(e.getMessage(), context, expr, e);
		} catch (Throwable e) {
			internalFailure(e.getMessage(), context, expr, e);
		}

		internalFailure("unknown constant expression: "
				+ expr.getClass().getName(), context, expr);
		return null; // deadcode
	}

	// =========================================================================
	// Misc
	// =========================================================================

	/**
	 * Determine whether a given name is visible from a given context.
	 * 
	 * @param nid
	 *            Name to check visibility of
	 * @param context
	 *            Context in which we are trying to access named item
	 * @return True if given context permitted to access name
	 * @throws Exception
	 */
	public boolean isVisible(NameID nid, Context context) throws Exception {
		Path.ID mid = nid.module();
		if (mid.equals(context.file().module)) {
			return true;
		}
		WhileyFile wf = builder.getSourceFile(mid);
		if (wf != null) {
			WhileyFile.Declaration d = wf.declaration(nid.name());
			if (d instanceof WhileyFile.Constant) {
				WhileyFile.Constant td = (WhileyFile.Constant) d;
				return td.isPublic() || td.isProtected();
			} else if (d instanceof WhileyFile.Type) {
				WhileyFile.Type td = (WhileyFile.Type) d;
				return td.isPublic() || td.isProtected();
			}
			return false;
		} else {
			// we have to do the following basically because we don't load
			// modifiers properly out of jvm class files (at the moment).
			return true;
			// WyilFile w = builder.getModule(mid);
			// WyilFile.ConstDef c = w.constant(nid.name());
			// WyilFile.TypeDef t = w.type(nid.name());
			// if(c != null) {
			// return c.isPublic() || c.isProtected();
			// } else {
			// return t.isPublic() || t.isProtected();
			// }
		}
	}

	// =========================================================================
	// Constant Evaluation [this should not be located here?]
	// =========================================================================

	/**
	 * Evaluate a given unary operator on a given input value.
	 * 
	 * @param operator
	 *            Unary operator to evaluate
	 * @param operand
	 *            Operand to apply operator on
	 * @param context
	 *            Context in which to apply operator (useful for error
	 *            reporting)
	 * @return
	 */
	private Constant evaluate(Expr.UnOp operator, Constant operand, Context context) {
		switch (operator.op) {
		case NOT:
			if (operand instanceof Constant.Bool) {
				Constant.Bool b = (Constant.Bool) operand;
				return Constant.V_BOOL(!b.value);
			}
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, operator);
			break;
		case NEG:
			if (operand instanceof Constant.Integer) {
				Constant.Integer b = (Constant.Integer) operand;
				return Constant.V_INTEGER(b.value.negate());
			} else if (operand instanceof Constant.Decimal) {
				Constant.Decimal b = (Constant.Decimal) operand;
				return Constant.V_DECIMAL(b.value.negate());
			}
			syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context, operator);
			break;
		case INVERT:
			if (operand instanceof Constant.Byte) {
				Constant.Byte b = (Constant.Byte) operand;
				return Constant.V_BYTE((byte) ~b.value);
			}
			break;
		}
		syntaxError(errorMessage(INVALID_UNARY_EXPRESSION), context, operator);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant v1, Constant v2,
			Context context) {
		Type v1_type = v1.type();
		Type v2_type = v2.type();
		Type lub = Type.Union(v1_type, v2_type);

		// FIXME: there are bugs here related to coercions.

		if (Type.isSubtype(Type.T_BOOL, lub)) {
			return evaluateBoolean(bop, (Constant.Bool) v1, (Constant.Bool) v2,
					context);
		} else if (Type.isSubtype(Type.T_INT, lub)) {
			return evaluate(bop, (Constant.Integer) v1, (Constant.Integer) v2,
					context);
		} else if (Type.isImplicitCoerciveSubtype(Type.T_REAL, v1_type)
				&& Type.isImplicitCoerciveSubtype(Type.T_REAL, v1_type)) {
			if (v1 instanceof Constant.Integer) {
				Constant.Integer i1 = (Constant.Integer) v1;
				v1 = Constant.V_DECIMAL(new BigDecimal(i1.value));
			} else if (v2 instanceof Constant.Integer) {
				Constant.Integer i2 = (Constant.Integer) v2;
				v2 = Constant.V_DECIMAL(new BigDecimal(i2.value));
			}
			return evaluate(bop, (Constant.Decimal) v1, (Constant.Decimal) v2,
					context);
		} else if (Type.isSubtype(Type.T_LIST_ANY, lub)) {
			return evaluate(bop, (Constant.List) v1, (Constant.List) v2,
					context);
		} else if (Type.isSubtype(Type.T_SET_ANY, lub)) {
			return evaluate(bop, (Constant.Set) v1, (Constant.Set) v2, context);
		}
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluateBoolean(Expr.BinOp bop, Constant.Bool v1,
			Constant.Bool v2, Context context) {
		switch (bop.op) {
		case AND:
			return Constant.V_BOOL(v1.value & v2.value);
		case OR:
			return Constant.V_BOOL(v1.value | v2.value);
		case XOR:
			return Constant.V_BOOL(v1.value ^ v2.value);
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Integer v1,
			Constant.Integer v2, Context context) {
		switch (bop.op) {
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
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Decimal v1,
			Constant.Decimal v2, Context context) {
		switch (bop.op) {
		case ADD:
			return Constant.V_DECIMAL(v1.value.add(v2.value));
		case SUB:
			return Constant.V_DECIMAL(v1.value.subtract(v2.value));
		case MUL:
			return Constant.V_DECIMAL(v1.value.multiply(v2.value));
		case DIV:
			return Constant.V_DECIMAL(v1.value.divide(v2.value));
		}
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.List v1,
			Constant.List v2, Context context) {
		switch (bop.op) {
		case ADD:
			ArrayList<Constant> vals = new ArrayList<Constant>(v1.values);
			vals.addAll(v2.values);
			return Constant.V_LIST(vals);
		}
		syntaxError(errorMessage(INVALID_LIST_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Set v1, Constant.Set v2,
			Context context) {
		switch (bop.op) {
		case UNION: {
			HashSet<Constant> vals = new HashSet<Constant>(v1.values);
			vals.addAll(v2.values);
			return Constant.V_SET(vals);
		}
		case INTERSECTION: {
			HashSet<Constant> vals = new HashSet<Constant>();
			for (Constant v : v1.values) {
				if (v2.values.contains(v)) {
					vals.add(v);
				}
			}
			return Constant.V_SET(vals);
		}
		case SUB: {
			HashSet<Constant> vals = new HashSet<Constant>();
			for (Constant v : v1.values) {
				if (!v2.values.contains(v)) {
					vals.add(v);
				}
			}
			return Constant.V_SET(vals);
		}
		}
		syntaxError(errorMessage(INVALID_SET_EXPRESSION), context, bop);
		return null;
	}
}
