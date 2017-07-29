// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.builder;

import static wyil.util.ErrorMessages.RESOLUTION_ERROR;
import static wyil.util.ErrorMessages.errorMessage;

import java.io.*;
import java.util.*;

import wyal.lang.WyalFile;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.checks.CoercionCheck;
import wyil.lang.*;
import wyil.util.MoveAnalysis;
import wyil.util.TypeSystem;
import wybs.lang.*;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.*;
import wyc.lang.*;
import wyc.lang.WhileyFile.Context;
import wycc.util.ArrayUtils;
import wycc.util.Logger;
import wycc.util.Pair;

/**
 * Responsible for managing the process of turning source files into binary code
 * for execution. Each source file is passed through a pipeline of stages that
 * modify it in a variety of ways. The main stages are:
 * <ol>
 * <li>
 * <p>
 * <b>Lexing and Parsing</b>, where the source file is converted into an
 * Abstract Syntax Tree (AST) representation.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Name Resolution</b>, where the fully qualified names of all external
 * symbols are determined.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Type Propagation</b>, where the types of all expressions are determined by
 * propagation from e.g. declared parameter types.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>WYIL Generation</b>, where the the AST is converted into the Whiley
 * Intermediate Language (WYIL). A number of passes are then made over this
 * before it is ready for code generation.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Code Generation</b>. Here, the executable code is finally generated. This
 * could be Java bytecode, or something else (e.g. JavaScript).
 * </p>
 * </li>
 * </ol>
 * Every stage of the compiler can be configured by setting various options.
 * Stages can also be bypassed (typically for testing) and new ones can be
 * added.
 *
 * @author David J. Pearce
 *
 */
public final class CompileTask implements Build.Task {

	/**
	 * The master project for identifying all resources available to the
	 * builder. This includes all modules declared in the project being compiled
	 * and/or defined in external resources (e.g. jar files).
	 */
	private final Build.Project project;

	/**
	 * Provides mechanism for operating on types. For example, expanding them
	 * and performing subtype tests, etc. This object may cache results to
	 * improve performance of some operations.
	 */
	private final TypeSystem typeSystem;

	/**
	 * The logger used for logging system events
	 */
	private Logger logger;

	/**
	 * A map of the source files currently being compiled.
	 */
	private final HashMap<Path.ID, Path.Entry<WhileyFile>> srcFiles = new HashMap<>();

	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import whiley.lang.*</code>
	 * corresponds to the triple <code>("whiley.lang",*,null)</code>.
	 */
	private final HashMap<Trie, ArrayList<Path.ID>> importCache = new HashMap<>();

	public CompileTask(Build.Project project) {
		this.logger = Logger.NULL;
		this.project = project;
		this.typeSystem = new TypeSystem(project);
	}

	public String id() {
		return "wyc.builder";
	}

	@Override
	public Build.Project project() {
		return project;
	}

	/**
	 * Access the type system object this compile task is using.
	 *
	 * @return
	 */
	public TypeSystem getTypeSystem() {
		return typeSystem;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Path.Entry<?>> build(Collection<Pair<Path.Entry<?>, Path.Root>> delta, Build.Graph graph)
			throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long startTime = System.currentTimeMillis();
		long startMemory = runtime.freeMemory();
		long tmpTime = startTime;
		long tmpMemory = startMemory;

		// ========================================================================
		// Parse and register source files
		// ========================================================================

		srcFiles.clear();
		int count = 0;
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			if (src.contentType() == WhileyFile.ContentType) {
				Path.Entry<WhileyFile> sf = (Path.Entry<WhileyFile>) src;
				sf.read(); // force file to be parsed
				count++;
				srcFiles.put(sf.id(), sf);
			}
		}

		logger.logTimedMessage("Parsed " + count + " source file(s).", System.currentTimeMillis() - tmpTime,
				tmpMemory - runtime.freeMemory());

		// ========================================================================
		// Flow Type source files
		// ========================================================================

		runtime = Runtime.getRuntime();
		tmpTime = System.currentTimeMillis();
		tmpMemory = runtime.freeMemory();

		ArrayList<WhileyFile> files = new ArrayList<>();
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> entry = p.first();
			if (entry.contentType() == WhileyFile.ContentType) {
				Path.Entry<WhileyFile> source = (Path.Entry<WhileyFile>) entry;
				// Parse Whiley source file. This may produce errors at this
				// stage, which means compilation of this file cannot proceed
				WhileyFile wf = source.read();
				files.add(wf);
				// Write WyIL skeleton. This is a stripped down version of the
				// source file which is easily translated into a temporary
				// WyilFile. This is needed for resolution.
				Path.Root dst = p.second();
				Path.Entry<WyilFile> target = dst.create(entry.id(), WyilFile.ContentType);
				target.write(createWyilSkeleton(wf,target));
				// Register the derivation in the build graph. This is important
				// to understand what a particular intermediate file was
				// derived from.
				graph.registerDerivation(source, target);
			}
		}

		FlowTypeChecker flowChecker = new FlowTypeChecker(this);
		flowChecker.propagate(files);

		logger.logTimedMessage("Typed " + count + " source file(s).", System.currentTimeMillis() - tmpTime,
				tmpMemory - runtime.freeMemory());

		// ========================================================================
		// Code Generation
		// ========================================================================

		runtime = Runtime.getRuntime();
		tmpTime = System.currentTimeMillis();
		tmpMemory = runtime.freeMemory();

		CodeGenerator generator = new CodeGenerator(this);
		HashSet<Path.Entry<?>> generatedFiles = new HashSet<>();
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			Path.Root dst = p.second();
			if (src.contentType() == WhileyFile.ContentType) {
				Path.Entry<WhileyFile> source = (Path.Entry<WhileyFile>) src;
				Path.Entry<WyilFile> target = dst.get(src.id(), WyilFile.ContentType);
				generatedFiles.add(target);
				WhileyFile wf = source.read();
				new DefiniteAssignmentAnalysis(wf).check();
				new ModuleCheck(wf).check();
				WyilFile wyil = generator.generate(wf, target);
				new MoveAnalysis(this).apply(wyil);
				target.write(wyil);
			}
		}

		logger.logTimedMessage("Generated code for " + count + " source file(s).", System.currentTimeMillis() - tmpTime,
				tmpMemory - runtime.freeMemory());

		// ========================================================================
		// Pipeline Stages
		// ========================================================================

		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			Path.Root dst = p.second();
			Path.Entry<WyilFile> wf = dst.get(src.id(), WyilFile.ContentType);
			process(wf.read(), new CoercionCheck(this));
		}

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Whiley => Wyil: compiled " + delta.size() + " file(s)", endTime - startTime,
				startMemory - runtime.freeMemory());

		return generatedFiles;
	}

	// ======================================================================
	// Public Accessors
	// ======================================================================

	public boolean exists(Path.ID id) {
		try {
			return project.exists(id, WhileyFile.ContentType) || project.exists(id, WyilFile.ContentType);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Determine whether a given name exists or not.
	 *
	 * @param nid
	 *            --- Name ID to check
	 * @return
	 */
	public boolean isName(NameID nid) throws IOException {
		Path.ID mid = nid.module();
		Path.Entry<WhileyFile> wf = srcFiles.get(mid);
		if (wf != null) {
			// FIXME: check for the right kind of name
			return wf.read().hasName(nid.name());
		} else {
			Path.Entry<WyilFile> m = project.get(mid, WyilFile.ContentType);
			if (m != null) {
				return m.read().hasName(nid.name());
			} else {
				return false;
			}
		}
	}

	/**
	 * Determine whether a name is visible in a given context. This effectively
	 * corresponds to checking whether or not the already name exists in the
	 * given context; or, a public or protected named is imported from another
	 * file.
	 *
	 * @param nid
	 *            Name to check modifiers of
	 * @param context
	 *            Context in which we are trying to access named item
	 *
	 * @return True if given context permitted to access name
	 * @throws IOException
	 */
	public boolean isNameVisible(NameID nid, Context context) throws IOException {
		// Any element in the same file is automatically visible
		if (nid.module().equals(context.file().getEntry().id())) {
			return true;
		} else {
			return hasModifier(nid, context, Modifier.PUBLIC);
		}
	}

	/**
	 * Determine whether a named type is fully visible in a given context. This
	 * effectively corresponds to checking whether or not the already type
	 * exists in the given context; or, a public type is imported from another
	 * file.
	 *
	 * @param nid
	 *            Name to check modifiers of
	 * @param context
	 *            Context in which we are trying to access named item
	 *
	 * @return True if given context permitted to access name
	 * @throws IOException
	 */
	public boolean isTypeVisible(NameID nid, Context context) throws IOException {
		// Any element in the same file is automatically visible
		if (nid.module().equals(context.file().getEntry().id())) {
			return true;
		} else {
			return hasModifier(nid, context, Modifier.PUBLIC);
		}
	}

	/**
	 * Determine whether a named item has a modifier matching one of a given
	 * list. This is particularly useful for checking visibility (e.g. public,
	 * private, etc) of named items.
	 *
	 * @param nid
	 *            Name to check modifiers of
	 * @param context
	 *            Context in which we are trying to access named item
	 * @param modifiers
	 *
	 * @return True if given context permitted to access name
	 * @throws IOException
	 */
	public boolean hasModifier(NameID nid, Context context, Modifier modifier) throws IOException {
		Path.ID mid = nid.module();
		// Attempt to access source file first.
		WhileyFile wf = getSourceFile(mid);
		if (wf != null) {
			// Source file location, so check visible of element.
			WhileyFile.NamedDeclaration nd = wf.declaration(nid.name());
			return nd != null && nd.hasModifier(modifier);
		} else {
			// Source file not being compiled, therefore attempt to access wyil
			// file directly.

			// we have to do the following basically because we don't load
			// modifiers properly out of jvm class files (at the moment).
			// return false;
			WyilFile w = getModule(mid);
			List<WyilFile.Block> blocks = w.blocks();
			for (int i = 0; i != blocks.size(); ++i) {
				WyilFile.Block d = blocks.get(i);
				if (d instanceof WyilFile.Declaration) {
					WyilFile.Declaration nd = (WyilFile.Declaration) d;
					if(nd.name().equals(nid.name())) {
						return nd != null && nd.hasModifier(modifier);
					}
				}
			}
			return false;
		}
	}

	/**
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 *
	 * @param imp
	 * @return
	 */
	public List<Path.ID> imports(Trie key) throws ResolveError {
		try {
			ArrayList<Path.ID> matches = importCache.get(key);
			if (matches != null) {
				// cache hit
				return matches;
			} else {
				// cache miss
				matches = new ArrayList<>();

				for (Path.Entry<WhileyFile> sf : srcFiles.values()) {
					if (key.matches(sf.id())) {
						matches.add(sf.id());
					}
				}
				if (key.isConcrete()) {
					// A concrete key is one which does not contain a wildcard.
					// Therefore, it corresponds to exactly one possible item.
					// It is helpful, from a performance perspective, to use
					// NameSpace.exists() in such case, as this conveys the fact
					// that we're only interested in a single item.
					if (project.exists(key, WyilFile.ContentType)) {
						matches.add(key);
					}
				} else {
					Content.Filter<?> binFilter = Content.filter(key, WyilFile.ContentType);
					for (Path.ID mid : project.match(binFilter)) {
						matches.add(mid);
					}
				}
				importCache.put(key, matches);
			}
			return matches;
		} catch (Exception e) {
			throw new ResolveError(e.getMessage(), e);
		}
	}

	/**
	 * Get the source file associated with a given module identifier. If the
	 * source file does not exist, null is returned.
	 *
	 * @param mid
	 * @return
	 * @throws IOException
	 */
	public WhileyFile getSourceFile(Path.ID mid) throws IOException {
		Path.Entry<WhileyFile> e = srcFiles.get(mid);
		if (e != null) {
			return e.read();
		} else {
			return null;
		}
	}

	/**
	 * Get the (compiled) module associated with a given module identifier. If
	 * the module does not exist, a resolve error is thrown.
	 *
	 * @param mid
	 * @return
	 * @throws IOException
	 */
	public WyilFile getModule(Path.ID mid) throws IOException {
		return project.get(mid, WyilFile.ContentType).read();
	}


	// =========================================================================
	// ResolveAsName
	// =========================================================================

	/**
	 * <p>
	 * Responsible for resolve names, types, constants and functions / methods
	 * at the global level. Resolution is determined by the context in which a
	 * given name/type/constant/function/method appears. That is, what imports
	 * are active in the enclosing WhileyFile. For example, consider this:
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
	 * In this example, the statement "<code>import whiley.lang.*</code>" is
	 * active for the type declaration, whilst the statement "
	 * <code>import whiley.ui.*</code>". The context of the type declaration is
	 * everything in the enclosing file up to the declaration itself. Therefore,
	 * in resolving the name <code>Int.uint</code>, this will examine the
	 * package whiley.lang to see whether a compilation unit named "Int" exists.
	 * If so, it will then resolve the name <code>Int.uint</code> to
	 * <code>whiley.lang.Int.uint</code>.
	 * </p>
	 *
	 * @param name
	 *            A module name without package specifier.
	 * @param context
	 *            --- context in which to resolve.
	 * @return The resolved name.
	 * @throws IOException
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(String name, Context context) throws IOException, ResolveError {
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
				for (Path.ID mid : imports(filter)) {
					NameID nid = new NameID(mid, name);
					if (isName(nid)) {
						// ok, we have found the name in question. But, is it
						// visible?
						if (isNameVisible(nid, context)) {
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
	 * @throws IOException
	 *             if it couldn't resolve the name
	 */
	public NameID resolveAsName(List<String> names, Context context) throws IOException, ResolveError {
		if (names.size() == 1) {
			return resolveAsName(names.get(0), context);
		} else if (names.size() == 2) {
			String name = names.get(1);
			Path.ID mid = resolveAsModule(names.get(0), context);
			NameID nid = new NameID(mid, name);
			if (isName(nid)) {
				if (isNameVisible(nid, context)) {
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
			if (isName(nid)) {
				if (isNameVisible(nid, context)) {
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
	 * @throws IOException
	 */
	public Path.ID resolveAsModule(String name, Context context) throws IOException, ResolveError {

		for (WhileyFile.Import imp : context.imports()) {
			Trie filter = imp.filter;
			String last = filter.last();
			if (last.equals("*")) {
				// this is generic import, so narrow the filter.
				filter = filter.parent().append(name);
			} else if (!last.equals(name)) {
				continue; // skip as not relevant
			}

			for (Path.ID mid : imports(filter)) {
				return mid;
			}
		}

		throw new ResolveError("module not found: " + name);
	}

	// ======================================================================
	// Private Implementation
	// ======================================================================

	private void process(WyilFile module, Build.Stage<WyilFile> stage) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();
		String name = name(stage.getClass().getSimpleName());

		try {
			stage.apply(module);
			logger.logTimedMessage("[" + module.getEntry().location() + "] applied " + name,
					System.currentTimeMillis() - start, memory - runtime.freeMemory());
			System.gc();
		} catch (RuntimeException ex) {
			logger.logTimedMessage(
					"[" + module.getEntry().location() + "] failed on " + name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start, memory - runtime.freeMemory());
			throw ex;
		} catch (IOException ex) {
			logger.logTimedMessage(
					"[" + module.getEntry().location() + "] failed on " + name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start, memory - runtime.freeMemory());
			throw ex;
		}
	}

	/**
	 * Create a "skeleton" version of the WyilFile corresponding to a given
	 * WhileyFile. The skeleton only includes public type declarations. These
	 * are needed for resolution, which relies on the ability to extract such
	 * information from WyilFiles.
	 *
	 * @param wf
	 * @return
	 */
	private WyilFile createWyilSkeleton(WhileyFile whileyFile, Path.Entry<WyilFile> target) {
		WyilFile wyilFile = new WyilFile(target);
		for (WhileyFile.Declaration d : whileyFile.declarations) {
			if (d instanceof WhileyFile.Type) {
				WhileyFile.Type td = (WhileyFile.Type) d;
				try {
					Type wyilType = toSemanticType(td.parameter.type, td);
					WyilFile.Type wyilTypeDecl = new WyilFile.Type(wyilFile, td.modifiers(), td.name(), wyilType);
					// At this point, if the original type contains an invariant
					// then we must add a dummy one here. This is critical as,
					// otherwise, the type system cannot tell that certain types
					// are constrained.
					if(td.invariant.size() > 0) {
						// Add null as a dummy invariant.
						wyilTypeDecl.getInvariant().add(null);
					}
					wyilFile.blocks().add(wyilTypeDecl);
				} catch (ResolveError e) {
					throw new SyntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()), whileyFile.getEntry(),
							td.parameter.type, e);
				} catch (Throwable t) {
					throw new InternalFailure(t.getMessage(), whileyFile.getEntry(), td.parameter.type, t);
				}
			}
		}
		return wyilFile;
	}

	/**
	 * Convert a Whiley "syntactic" type into a wyil type. This is essentially a
	 * straightforward process. The only complication is that the names for
	 * nominal types have to be properly resolved.
	 *
	 * @param type
	 *            The type to be converted
	 * @return A Wyil Type equivalent to the original Whiley type
	 * @throws ResolveError
	 *             If a named type within this condition cannot be resolved
	 *             within the enclosing project.
	 * @throws IOException
	 */
	public Type toSemanticType(WyalFile.Type type, WhileyFile.Context context) throws ResolveError, IOException {
		if (type instanceof SyntacticType.Any) {
			return Type.T_ANY;
		} else if (type instanceof SyntacticType.Void) {
			return Type.T_VOID;
		} else if (type instanceof SyntacticType.Bool) {
			return Type.T_BOOL;
		} else if (type instanceof SyntacticType.Null) {
			return Type.T_NULL;
		} else if (type instanceof SyntacticType.Byte) {
			return Type.T_BYTE;
		} else if (type instanceof SyntacticType.Int) {
			return Type.T_INT;
		} else if (type instanceof SyntacticType.Array) {
			SyntacticType.Array arrT = (SyntacticType.Array) type;
			Type element = toSemanticType(arrT.getElement(), context);
			return Type.Array(element);
		} else if (type instanceof SyntacticType.Reference) {
			SyntacticType.Reference refT = (SyntacticType.Reference) type;
			Type element = toSemanticType(refT.getElement(), context);
			if(refT.getLifetime() == null) {
				return Type.Reference("*",element);
			} else {
				return Type.Reference(refT.getLifetime().toString(),element);
			}
		} else if (type instanceof SyntacticType.Record) {
			SyntacticType.Record recT = (SyntacticType.Record) type;
			ArrayList<Pair<Type, String>> fields = new ArrayList<>();
			WyalFile.FieldDeclaration[] recFields = recT.getFields();
			for (int i=0;i!=recFields.length;++i) {
				WyalFile.FieldDeclaration fd = recFields[i];
				fields.add(new Pair<>(toSemanticType(fd.getType(), context), fd.getVariableName().toString()));
			}
			return Type.Record(recT.isOpen(), fields);
		} else if (type instanceof SyntacticType.Function) {
			SyntacticType.Function funT = (SyntacticType.Function) type;
			Type[] parameters = toSemanticTypes(funT.getParameters(), context);
			Type[] returns = toSemanticTypes(funT.getReturns(), context);
			return Type.Function(parameters, returns);
		} else if (type instanceof SyntacticType.Method) {
			SyntacticType.Method methT = (SyntacticType.Method) type;
			String[] lifetimeParameters = toStringArray(methT.getLifetimeParameters());
			String[] contextLifetimes = toStringArray(methT.getContextLifetimes());
			Type[] parameters = toSemanticTypes(methT.getParameters(), context);
			Type[] returns = toSemanticTypes(methT.getReturns(), context);
			return Type.Method(lifetimeParameters, contextLifetimes, parameters, returns);
		} else if (type instanceof SyntacticType.Property) {
			SyntacticType.Property funT = (SyntacticType.Property) type;
			Type[] parameters = toSemanticTypes(funT.getParameters(), context);
			return Type.Property(parameters);
		} else if (type instanceof SyntacticType.Union) {
			SyntacticType.Union unionT = (SyntacticType.Union) type;
			return Type.Union(toSemanticTypes(unionT.getOperands(), context));
		} else if (type instanceof SyntacticType.Intersection) {
			SyntacticType.Intersection intersectionT = (SyntacticType.Intersection) type;
			return Type.Intersection(toSemanticTypes(intersectionT.getOperands(), context));
		} else if (type instanceof SyntacticType.Negation) {
			SyntacticType.Negation negT = (SyntacticType.Negation) type;
			Type element = toSemanticType(negT.getElement(), context);
			return Type.Negation(element);
		} else if (type instanceof SyntacticType.Nominal) {
			SyntacticType.Nominal nominalT = (SyntacticType.Nominal) type;
			// FIXME: this should not be here!
			NameID name = resolveAsName(Arrays.asList(toStringArray(nominalT.getName().getComponents())), context);
			return Type.Nominal(name);
		} else {
			throw new InternalFailure("invalid type encountered", context.file().getEntry(), type);
		}
	}

	private Type[] toSemanticTypes(WyalFile.Tuple<WyalFile.Type> types, WhileyFile.Context context) throws ResolveError, IOException {
		Type[] wyilTypes = new Type[types.size()];
		for (int i = 0; i != wyilTypes.length; ++i) {
			wyilTypes[i] = toSemanticType(types.getOperand(i), context);
		}
		return wyilTypes;
	}

	private Type[] toSemanticTypes(WyalFile.Type[] types, WhileyFile.Context context) throws ResolveError, IOException {
		Type[] wyilTypes = new Type[types.length];
		for (int i = 0; i != wyilTypes.length; ++i) {
			wyilTypes[i] = toSemanticType(types[i], context);
		}
		return wyilTypes;
	}

	private String[] toStringArray(WyalFile.Tuple<WyalFile.Identifier> identifiers) {
		String[] strings = new String[identifiers.size()];
		for(int i=0;i!=strings.length;++i) {
			strings[i] = identifiers.getOperand(i).get();
		}
		return strings;
	}

	private String[] toStringArray(WyalFile.Identifier[] identifiers) {
		String[] strings = new String[identifiers.length];
		for(int i=0;i!=strings.length;++i) {
			strings[i] = identifiers[i].get();
		}
		return strings;
	}

	private static String name(String camelCase) {
		boolean firstTime = true;
		String r = "";
		for (int i = 0; i != camelCase.length(); ++i) {
			char c = camelCase.charAt(i);
			if (!firstTime && Character.isUpperCase(c)) {
				r += " ";
			}
			firstTime = false;
			r += Character.toLowerCase(c);
			;
		}
		return r;
	}
}
