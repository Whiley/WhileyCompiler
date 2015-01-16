package wycs.builders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static wycc.lang.SyntaxError.*;
import static wycs.solver.Solver.SCHEMA;
import wyautl.io.PrettyAutomataWriter;
import wyautl.rw.Rewriter;
import wyautl.rw.SimpleRewriteStrategy;
import wybs.lang.*;
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.Pipeline;
import wycc.lang.SyntaxError;
import wycc.lang.Transform;
import wycc.util.Logger;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wycs.core.SemanticType;
import wycs.core.WycsFile;
import wycs.io.WyalFilePrinter;
import wycs.io.WycsFilePrinter;
import wycs.solver.Solver;
import wycs.syntax.SyntacticType;
import wycs.syntax.TypeAttribute;
import wycs.syntax.TypePattern;
import wycs.syntax.WyalFile;
import wycs.transforms.SmtVerificationCheck;
import wycs.transforms.TypePropagation;
import wycs.transforms.VerificationCheck;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyfs.util.Trie;

public class Wyal2WycsBuilder implements Builder, Logger {

	/**
	 * The master project for identifying all resources available to the
	 * builder. This includes all modules declared in the project being verified
	 * and/or defined in external resources (e.g. jar files).
	 */
	protected final Build.Project project;

	/**
	 * The list of stages which must be applied to a Wycs file.
	 */
	protected final List<Transform<WycsFile>> pipeline;

	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import wycs.lang.*</code>
	 * corresponds to the triple <code>("wycs.lang",*,null)</code>.
	 */
	protected final HashMap<Trie, ArrayList<Path.ID>> importCache = new HashMap();

	/**
	 * A map of the source files currently being compiled.
	 */
	protected final HashMap<Path.ID, Path.Entry<WyalFile>> srcFiles = new HashMap<Path.ID, Path.Entry<WyalFile>>();

	protected Logger logger = Logger.NULL;

	protected boolean debug = false;

	public Wyal2WycsBuilder(Build.Project project, Pipeline<WycsFile> pipeline) {
		this.project = project;
		this.pipeline = pipeline.instantiate(this);
	}

	public Build.Project project() {
		return project;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}


	@Override
	public void logTimedMessage(String msg, long time, long memory) {
		logger.logTimedMessage(msg, time, memory);
	}

	// ======================================================================
	// Build Method
	// ======================================================================

	@Override
	public Set<Path.Entry<?>> build(Collection<Pair<Entry<?>, Path.Root>> delta) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long startTime = System.currentTimeMillis();
		long startMemory = runtime.freeMemory();
		long tmpTime = startTime;
		long tmpMem = startMemory;

		// ========================================================================
		// Parse and register source files
		// ========================================================================

		srcFiles.clear();
		int count = 0;
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			if (src.contentType() == WyalFile.ContentType) {
				Path.Entry<WyalFile> sf = (Path.Entry<WyalFile>) src;
				WyalFile wf = sf.read();
				count++;
				srcFiles.put(wf.id(), sf);
			}
		}

		logger.logTimedMessage("Parsed " + count + " source file(s).",
				System.currentTimeMillis() - tmpTime,
				tmpMem - runtime.freeMemory());

		// ========================================================================
		// Stub Generation
		// ========================================================================
		runtime = Runtime.getRuntime();
		tmpTime = System.currentTimeMillis();
		tmpMem = runtime.freeMemory();
		HashSet<Path.Entry<?>> generatedFiles = new HashSet<Path.Entry<?>>();
		for(Pair<Path.Entry<?>,Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			Path.Root dst = p.second();
			if (src.contentType() == WyalFile.ContentType) {
				Path.Entry<WyalFile> source = (Path.Entry<WyalFile>) src;
				Path.Entry<WycsFile> target = (Path.Entry<WycsFile>) dst.create(src.id(),WycsFile.ContentType);
				generatedFiles.add(target);
				WyalFile wf = source.read();
				WycsFile wycs = getModuleStub(wf);
				target.write(wycs);
			}
		}
		logger.logTimedMessage("Generated stubs for " + count + " source file(s).",
				System.currentTimeMillis() - tmpTime, tmpMem - runtime.freeMemory());

		// ========================================================================
		// Type source files
		// ========================================================================
		runtime = Runtime.getRuntime();
		tmpTime = System.currentTimeMillis();
		tmpMem = runtime.freeMemory();

		TypePropagation typer = new TypePropagation(this);
		for(Pair<Path.Entry<?>,Path.Root> p : delta) {
			Path.Entry<?> f = p.first();
			if (f.contentType() == WyalFile.ContentType) {
				Path.Entry<WyalFile> sf = (Path.Entry<WyalFile>) f;
				WyalFile wf = sf.read();
				typer.apply(wf);
			}
		}

		logger.logTimedMessage("Typed " + count + " source file(s).",
				System.currentTimeMillis() - tmpTime, tmpMem - runtime.freeMemory());


		// ========================================================================
		// Code Generation
		// ========================================================================
		runtime = Runtime.getRuntime();
		tmpTime = System.currentTimeMillis();
		tmpMem = runtime.freeMemory();

		CodeGeneration generator = new CodeGeneration(this);
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			Path.Root dst = p.second();
			if (src.contentType() == WyalFile.ContentType) {
				Path.Entry<WyalFile> source = (Path.Entry<WyalFile>) src;
				Path.Entry<WycsFile> target = (Path.Entry<WycsFile>) dst
						.create(src.id(), WycsFile.ContentType);
				WyalFile wf = source.read();
				WycsFile wycs = generator.generate(wf);
				target.write(wycs);
			}
		}

		logger.logTimedMessage("Generated code for " + count + " source file(s).",
					System.currentTimeMillis() - tmpTime, tmpMem - runtime.freeMemory());

		// ========================================================================
		// Pipeline Stages
		// ========================================================================

		for (Transform<WycsFile> stage : pipeline) {
			for (Pair<Path.Entry<?>, Path.Root> p : delta) {
				Path.Root dst = p.second();
				Path.Entry<WycsFile> df = dst.get(p.first().id(),WycsFile.ContentType);
				WycsFile module = df.read();
				try {
					process(module, stage);
				} catch (VerificationCheck.AssertionFailure ex) {
					// FIXME: this feels a bit like a hack.
					if(debug && ex.original() != null) {
						Rewriter rw = ex.rewriter();
						PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,SCHEMA,"Or","And");
						writer.write(ex.original());
						writer.flush();
						System.err.println("\n\n=> (" + rw.getStats() + ")\n");
						writer.write(ex.reduction());
						writer.flush();
					}
					syntaxError(ex.getMessage(), module.filename(),
							ex.assertion(), ex);
				} catch (SmtVerificationCheck.AssertionFailure e) {
                    syntaxError(e.getMessage(), module.filename(),
                            e.getAssertion(), e);
                }
			}
		}


		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyal => Wycs: compiled " + delta.size() + " file(s)",
				endTime - startTime, startMemory - runtime.freeMemory());

		return generatedFiles;
	}

	// ======================================================================
	// Public Accessors
	// ======================================================================

	/**
	 * Check whether or not a given Wycs module exists.
	 *
	 * @param mid
	 *            --- fully qualified name.
	 * @return
	 */
	public boolean exists(Path.ID mid) {
		try {
			// first, check in those files being compiled.
			for(Map.Entry<Path.ID,Path.Entry<WyalFile>> e : srcFiles.entrySet()) {
				Path.Entry<WyalFile> pe = e.getValue();
				if(pe.id().equals(mid)) {
					return true;
				}
			}
			// second, check the wider namespace
			return project.exists(mid, WycsFile.ContentType);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the Wycs module associated with a given module identifier. If the
	 * module does not exist, null is returned.
	 *
	 * @param mid
	 * @return
	 * @throws Exception
	 */
	public WycsFile getModule(Path.ID mid) throws Exception {
		Path.Entry<WycsFile> wyf = project.get(mid, WycsFile.ContentType);
		if(wyf != null) {
			// In this case, we have found an appropriate WycsFile which matches
			// the given path.
			return wyf.read();
		} else {
			// Found nothing.
			return null;
		}
	}
	
	/**
	 * Resolve a name found at a given context in a source file to determine its
	 * fully qualified name. Essentially, the context will be used to determine
	 * the active import statements which will be used to search for the name.
	 *
	 * @param name
	 *            --- name to look for.
	 * @param context
	 *            --- context where name occurred.
	 * @return
	 * @throws ResolveError
	 */
	public NameID resolveAsName(String name, WyalFile.Context context)
			throws ResolveError {
		
		// First, we need to check whether or not the name is in the enclosing
		// file. If it is, then is what it must resolve to. Otherwise, we need
		// to look further afield.
		WyalFile enclosingFile = context.file();
		if(enclosingFile.declaration(name) != null) {
			// Ok, yes, there is a declaration of the given name in this file.
			return new NameID(enclosingFile.id(),name);
		} 
		
		// Otherwise, the name is defined in an external file and we need to
		// search the imports list to find it.		
		for (WyalFile.Import imp : context.imports()) {
			System.out.println("LOOKING FOR IMPORT: " + imp.filter + " " + imp.name);
			for (Path.ID id : imports(imp.filter)) {
				try {
					// First, look to see whether this is a file that is
					// currently being compoiled.
					Path.Entry<WyalFile> srcFile = srcFiles.get(id);
					if(srcFile != null) {
						// Ok, yes this is one of the files currently being
						// compiled. Therefore, let's see whether or not it
						// contains a declaration with the given name.
						WyalFile wf = srcFile.read();
						if(wf.declaration(name) != null) {
							return new NameID(id,name);
						}
					} else {
						// Ok, this is not one of the files being currently
						// compiled. Therefore, let's load the binary form of
						// this file and see whether or not it contains the
						// given name.
						WycsFile wf = getModule(id);
						if(wf != null && wf.declaration(name) != null) {
							return new NameID(id,name);
						}
					}
				} catch(SyntaxError e) {
					throw e;
				} catch (Exception e) {
					internalFailure(e.getMessage(), context.file().filename(),
							context, e);
				}
			}
		}
		
		throw new ResolveError("name not found: " + name);
	}
	/**
	 * Resolve a name found at a given context in a source file, and ensure it
	 * matches an expected type. Essentially, the context will be used to
	 * determine the active import statements which will be used to search for
	 * the name.
	 *
	 * @param name
	 *            --- name to look for.
	 * @param type
	 *            --- type it is expected to be.
	 * @param context
	 *            --- context where name occurred.
	 * @return
	 * @throws ResolveError
	 */
	public <T extends WycsFile.Declaration> Pair<NameID, T> resolveAs(
			String name, Class<T> type, WyalFile.Context context)
			throws ResolveError {

		for (WyalFile.Import imp : context.imports()) {
			for (Path.ID id : imports(imp.filter)) {
				try {
					WycsFile wf = getModule(id);
					if(wf == null) { continue; }
					T d = wf.declaration(name, type);
					if (d != null) {
						return new Pair<NameID, T>(new NameID(id, name), d);
					}
				} catch(SyntaxError e) {
					throw e;
				} catch (Exception e) {
					internalFailure(e.getMessage(), context.file().filename(),
							context, e);
				}
			}
		}

		throw new ResolveError("cannot resolve name as function: " + name);
	}

	public Pair<NameID, SemanticType.Function> resolveAsFunctionType(
			String name, WyalFile.Context context) throws ResolveError {
		// TODO: update the following line
		try {
			Pair<NameID, WycsFile.Function> wf = resolveAs(name,
					WycsFile.Function.class, context);
			return new Pair<NameID, SemanticType.Function>(wf.first(),
					wf.second().type);
		} catch (ResolveError e) {
			Pair<NameID, WycsFile.Macro> wm = resolveAs(name,
					WycsFile.Macro.class, context);
			return new Pair<NameID, SemanticType.Function>(wm.first(),
					wm.second().type);
		}
	}

	/**
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 *
	 * @param key
	 *            --- Path name which potentially contains a wildcard.
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
				matches = new ArrayList<Path.ID>();

				for (Path.Entry<WyalFile> sf : srcFiles.values()) {
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
					if (exists(key)) {
						matches.add(key);
					}
				} else {
					Content.Filter<?> binFilter = Content.filter(key,
							WyalFile.ContentType);
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

	public SemanticType convert(TypePattern tp, List<String> generics,
			WyalFile.Context context) throws ResolveError {
		return convert(tp.toSyntacticType(), new HashSet<String>(generics),
				context);
	}

	/**
	 * <p>
	 * Convert a syntactic type into a semantic type. A syntactic type
	 * represents something written at the source-level which may be invalid, or
	 * not expressed in the minial form.
	 * </p>
	 * <p>
	 * For example, consider a syntactic type <code>int | !int</code>. This is a
	 * valid type at the source level, and appears to be a union of two types.
	 * In fact, semantically, this type is equivalent to <code>any</code> and,
	 * for the purposes of subtype testing, needs to be represented as such.
	 * </p>
	 *
	 *
	 * @param type
	 *            --- Syntactic type to be converted.
	 * @param generics
	 *            --- Set of declared generic variables.
	 * @return
	 */
	public SemanticType convert(SyntacticType type, Set<String> generics,
			WyalFile.Context context) throws ResolveError {

		if(type instanceof SyntacticType.Void) {
			return SemanticType.Void;
		} else if(type instanceof SyntacticType.Any) {
			return SemanticType.Any;
		} else if(type instanceof SyntacticType.Null) {
			return SemanticType.Null;
		} else if(type instanceof SyntacticType.Bool) {
			return SemanticType.Bool;
		} else if(type instanceof SyntacticType.Char) {
			return SemanticType.Int;
		} else if(type instanceof SyntacticType.Int) {
			return SemanticType.Int;
		} else if(type instanceof SyntacticType.Real) {
			return SemanticType.Real;
		} else if (type instanceof SyntacticType.Variable) {
			SyntacticType.Variable p = (SyntacticType.Variable) type;
			if(!generics.contains(p.var)) {
				internalFailure("undeclared generic variable encountered (" + p + ")",
						context.file().filename(), type);
				return null; // deadcode
			}
			return SemanticType.Var(p.var);
		} else if(type instanceof SyntacticType.Negation) {
			SyntacticType.Negation t = (SyntacticType.Negation) type;
			return SemanticType.Not(convert(t.element,generics,context));
		} else if(type instanceof SyntacticType.Set) {
			SyntacticType.Set t = (SyntacticType.Set) type;
			return SemanticType.Set(true,convert(t.element,generics,context));
		} else if(type instanceof SyntacticType.Map) {
			// FIXME: need to include the map constraints here
			SyntacticType.Map t = (SyntacticType.Map) type;
			SemanticType key = convert(t.key,generics,context);
			SemanticType value = convert(t.value,generics,context);
			if (key instanceof SemanticType.Void
					|| value instanceof SemanticType.Void) {
				// surprisingly, this case is possible and does occur.
				return SemanticType.Set(true, SemanticType.Void);
			} else {
				return SemanticType.Set(true, SemanticType.Tuple(key, value));
			}
		} else if(type instanceof SyntacticType.List) {
			// FIXME: need to include the list constraints here
			SyntacticType.List t = (SyntacticType.List) type;
			SemanticType element = convert(t.element,generics,context);
			if (element instanceof SemanticType.Void) {
				// surprisingly, this case is possible and does occur.
				return SemanticType.Set(true, SemanticType.Void);
			} else {
				return SemanticType.Set(true,
						SemanticType.Tuple(SemanticType.Int, element));
			}
		} else if(type instanceof SyntacticType.Union) {
			SyntacticType.Union t = (SyntacticType.Union) type;
			SemanticType[] types = new SemanticType[t.elements.size()];
			for(int i=0;i!=t.elements.size();++i) {
				types[i] = convert(t.elements.get(i),generics,context);
			}
			return SemanticType.Or(types);
		} else if(type instanceof SyntacticType.Intersection) {
			SyntacticType.Intersection t = (SyntacticType.Intersection) type;
			SemanticType[] types = new SemanticType[t.elements.size()];
			for(int i=0;i!=t.elements.size();++i) {
				types[i] = convert(t.elements.get(i),generics,context);
			}
			return SemanticType.And(types);
		} else if(type instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple t = (SyntacticType.Tuple) type;
			SemanticType[] types = new SemanticType[t.elements.size()];
			for(int i=0;i!=t.elements.size();++i) {
				types[i] = convert(t.elements.get(i),generics,context);
			}
			return SemanticType.Tuple(types);
		} else if(type instanceof SyntacticType.Nominal) {
			SyntacticType.Nominal n = (SyntacticType.Nominal) type;
			List<String> n_names = n.names;
			NameID nid;
			// First, check whether or not we have an unqualified or fully
			// qualified name. Currently, there is no support for partially
			// qualified names which makes life easier at this point.
			if(n_names.size() == 1) {
				// This is an unqualified name and, hence, we need to qualify
				// it. This means determining what file this name is defined it.
				nid = resolveAsName(n_names.get(0), context);
			} else {
				// This is a fully qualified name. In this case, we don't need
				// to determine what file it is defined in as this is given. All
				// entries upto but not including the last name component
				// correspond to the package identified, whilst the last
				// component is the actual name.
				Trie pkg = Trie.ROOT;
				for (int i = 1; i != n_names.size(); ++i) {
					pkg = pkg.append(n_names.get(i-1));
				}
				String name = n_names.get(n_names.size()-1);
				nid = new NameID(pkg,name);
			}
			//
			return SemanticType.Nominal(nid);
		}

		internalFailure("unknown syntactic type encountered",
				context.file().filename(), type);
		return null; // deadcode
	}

	/**
	 * <p>
	 * Expand a semantic type by expanding all nominal types it contains with
	 * their underlying type. For example:
	 * </p>
	 * 
	 * <pre>
	 * type nat is int where:
	 *    x >= 0
	 *    
	 * assert:
	 *    forall(nat x):
	 *       x >= 0
	 * </pre>
	 * 
	 * <p>
	 * Here, the expanded version of type <code>nat</code> is <code>int</code>.
	 * </p>
	 *
	 *
	 * @param type
	 *            --- Syntactic type to be converted.
	 * @return
	 */
	public SemanticType expand(SemanticType type, WyalFile.Context context) {
		try {
			if (type instanceof SemanticType.Atom) {
				return type;
			} else if (type instanceof SemanticType.Var) {
				return type;
			} else if (type instanceof SemanticType.Tuple) {
				SemanticType.Tuple tt = (SemanticType.Tuple) type;
				SemanticType[] elements = tt.elements();
				boolean modified = false;
				for (int i = 0; i != elements.length; ++i) {
					SemanticType elem = elements[i];
					elements[i] = expand(elem, context);
					modified |= elements[i] != elem;
				}
				if (modified) {
					return SemanticType.Tuple(elements);
				} else {
					return type;
				}
			} else if (type instanceof SemanticType.Function) {
				SemanticType.Function ft = (SemanticType.Function) type;
				SemanticType from = expand(ft.from(), context);
				SemanticType to = expand(ft.to(), context);
				SemanticType[] generics = ft.generics();
				boolean modified = from != ft.from() || to != ft.to();
				for (int i = 0; i != generics.length; ++i) {
					SemanticType elem = generics[i];
					generics[i] = expand(elem, context);
					modified |= generics[i] != elem;
				}
				if (modified) {
					return SemanticType.Function(from, to, generics);
				} else {
					return type;
				}
			} else if (type instanceof SemanticType.Set) {
				SemanticType.Set st = (SemanticType.Set) type;
				SemanticType element = expand(st.element(), context);
				if (element != st.element()) {
					return SemanticType.Set(st.flag(), element);
				} else {
					return type;
				}
			} else if (type instanceof SemanticType.Not) {
				SemanticType.Not nt = (SemanticType.Not) type;
				SemanticType element = expand(nt.element(), context);
				if (element != nt.element()) {
					return SemanticType.Not(element);
				} else {
					return type;
				}
			} else if (type instanceof SemanticType.And) {
				SemanticType.And at = (SemanticType.And) type;
				SemanticType[] elements = at.elements();
				boolean modified = false;
				for (int i = 0; i != elements.length; ++i) {
					SemanticType elem = elements[i];
					elements[i] = expand(elem, context);
					modified |= elements[i] != elem;
				}
				if (modified) {
					return SemanticType.And(elements);
				} else {
					return type;
				}
			} else if (type instanceof SemanticType.Or) {
				SemanticType.Or at = (SemanticType.Or) type;
				SemanticType[] elements = at.elements();
				boolean modified = false;
				for (int i = 0; i != elements.length; ++i) {
					SemanticType elem = elements[i];
					elements[i] = expand(elem, context);
					modified |= elements[i] != elem;
				}
				if (modified) {
					return SemanticType.Or(elements);
				} else {
					return type;
				}
			} else if (type instanceof SemanticType.Nominal) {
				SemanticType.Nominal nt = (SemanticType.Nominal) type;
				WycsFile wf = getModule(nt.name().module());
				WycsFile.Type td = wf.declaration(nt.name().name(),
						WycsFile.Type.class);
				// FIXME: obviously, this doesn't work for recursive types!
				return expand(td.type, context);
			}
		} catch (Exception e) {
			internalFailure(e.getMessage(), context.file().filename(), context, e);
		}
		internalFailure("unknown type encountered", context.file().filename(),
				context);
		return null;
	}

	// ======================================================================
	// Private Implementation
	// ======================================================================

	protected void process(WycsFile module, Transform<WycsFile> stage)
			throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();
		String name = name(stage.getClass().getSimpleName());

		try {
			stage.apply(module);
			logger.logTimedMessage("[" + module.filename() + "] applied "
					+ name, System.currentTimeMillis() - start, memory
					- runtime.freeMemory());
			System.gc();
		} catch (RuntimeException ex) {
			logger.logTimedMessage("[" + module.filename() + "] failed on "
					+ name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start,
					memory - runtime.freeMemory());
			throw ex;
		} catch (IOException ex) {
			logger.logTimedMessage("[" + module.filename() + "] failed on "
					+ name + " (" + ex.getMessage() + ")",
					System.currentTimeMillis() - start,
					memory - runtime.freeMemory());
			throw ex;
		}
	}


	/**
	 * This converts a WyalFile into a WycsFile stub. A stub differs from a
	 * complete implementation, in that it only contains type information for
	 * functions and definitions. These are needed during the type propagation
	 * phase, and must be calculated before hand.
	 *
	 * @param wyalFile
	 * @return
	 */
	public WycsFile getModuleStub(WyalFile wyalFile) {
		ArrayList<WycsFile.Declaration> declarations = new ArrayList<WycsFile.Declaration>();
		for (WyalFile.Declaration d : wyalFile.declarations()) {
			try {
				if (d instanceof WyalFile.Macro) {
					WyalFile.Macro def = (WyalFile.Macro) d;
					SemanticType from = convert(def.from, def.generics, d);
					SemanticType to = SemanticType.Bool;
					SemanticType.Var[] generics = new SemanticType.Var[def.generics
					                                                   .size()];
					for (int i = 0; i != generics.length; ++i) {
						generics[i] = SemanticType.Var(def.generics.get(i));
					}
					SemanticType.Function type = SemanticType.Function(from, to,
							generics);
					declarations.add(new WycsFile.Macro(def.name, type, null, def
							.attribute(Attribute.Source.class)));
				} else if (d instanceof WyalFile.Function) {
					WyalFile.Function fun = (WyalFile.Function) d;
					SemanticType from = convert(fun.from, fun.generics, d);
					SemanticType to = convert(fun.to, fun.generics, d);
					SemanticType.Var[] generics = new SemanticType.Var[fun.generics
					                                                   .size()];
					for (int i = 0; i != generics.length; ++i) {
						generics[i] = SemanticType.Var(fun.generics.get(i));
					}
					SemanticType.Function type = SemanticType.Function(from, to,
							generics);
					declarations.add(new WycsFile.Function(fun.name, type, null,
							fun.attribute(Attribute.Source.class)));
				} else if (d instanceof WyalFile.Type) {
					WyalFile.Type t = (WyalFile.Type) d;
					SemanticType type = convert(t.type, t.generics, d);
					SemanticType.Var[] generics = new SemanticType.Var[t.generics
							.size()];
					for (int i = 0; i != generics.length; ++i) {
						generics[i] = SemanticType.Var(t.generics.get(i));
					}
					// FIXME: in the case of recursive type definitions, this is
					// broken.
					declarations.add(new WycsFile.Type(t.name, type, null, t
							.attribute(Attribute.Source.class)));
				}
			} catch (ResolveError re) {
				// should be unreachable if type propagation is already succeeded.
				syntaxError(re.getMessage(),
						wyalFile.filename(), d, re);
				return null;
			}
		}

		return new WycsFile(wyalFile.id(), wyalFile.filename(), declarations);
	}

	protected static String name(String camelCase) {
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
