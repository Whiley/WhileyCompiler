package wycs.builders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static wybs.lang.SyntaxError.*;
import static wycs.solver.Solver.SCHEMA;
import wyautl.io.PrettyAutomataWriter;
import wyautl.rw.Rewriter;
import wyautl.rw.SimpleRewriter;
import wybs.lang.*;
import wybs.lang.Path.Entry;
import wybs.util.Pair;
import wybs.util.ResolveError;
import wybs.util.Trie;
import wycs.core.SemanticType;
import wycs.core.WycsFile;
import wycs.io.WyalFileStructuredPrinter;
import wycs.io.WycsFilePrinter;
import wycs.solver.Solver;
import wycs.syntax.SyntacticType;
import wycs.syntax.TypeAttribute;
import wycs.syntax.TypePattern;
import wycs.syntax.WyalFile;
import wycs.transforms.TypePropagation;
import wycs.transforms.VerificationCheck;

public class Wyal2WycsBuilder implements Builder, Logger {

	/**
	 * The master namespace for identifying all resources available to the
	 * builder. This includes all modules declared in the project being verified
	 * and/or defined in external resources (e.g. jar files).
	 */
	protected final Path.Root namespace;

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

	public Wyal2WycsBuilder(Path.Root namespace, Pipeline<WycsFile> pipeline) {
		this.namespace = namespace;
		this.pipeline = pipeline.instantiate(this);
	}

	public Path.Root namespace() {
		return namespace;
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
	public void build(List<Pair<Entry<?>, Entry<?>>> delta) throws Exception {
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
		for (Pair<Path.Entry<?>, Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.first();
			if (f.contentType() == WyalFile.ContentType) {
				Path.Entry<WyalFile> sf = (Path.Entry<WyalFile>) f;
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

		for(Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.first();
			Path.Entry<?> s = (Path.Entry<?>) p.second();
			if (f.contentType() == WyalFile.ContentType && s.contentType() == WycsFile.ContentType) {
				Path.Entry<WyalFile> source = (Path.Entry<WyalFile>) f;
				Path.Entry<WycsFile> target = (Path.Entry<WycsFile>) s;				
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
		for(Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
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
		for(Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.first();
			Path.Entry<?> s = (Path.Entry<?>) p.second();
			if (f.contentType() == WyalFile.ContentType && s.contentType() == WycsFile.ContentType) {
				Path.Entry<WyalFile> source = (Path.Entry<WyalFile>) f;
				Path.Entry<WycsFile> target = (Path.Entry<WycsFile>) s;				
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
			for (Pair<Path.Entry<?>, Path.Entry<?>> p : delta) {
				Path.Entry<?> f = p.second();
				if (f.contentType() == WycsFile.ContentType) {
					Path.Entry<WycsFile> wf = (Path.Entry<WycsFile>) f;
					WycsFile module = wf.read();
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
					}
				}
			}
		}
		

		// ========================================================================
		// Done
		// ========================================================================
		
		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyal => Wycs: compiled " + delta.size() + " file(s)",
				endTime - startTime, startMemory - runtime.freeMemory());
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
			return namespace.exists(mid, WycsFile.ContentType);
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
		Path.Entry<WycsFile> wyf = namespace.get(mid, WycsFile.ContentType);
		if(wyf != null) {
			return wyf.read();
		} else {
			return null;
		}
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
					for (Path.ID mid : namespace.match(binFilter)) {
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

	public SemanticType convert(TypePattern tp, List<String> generics, WyalFile.Context context) {
		return convert(tp.toSyntacticType(),new HashSet<String>(generics),context);
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
	public SemanticType convert(SyntacticType type, Set<String> generics, WyalFile.Context context) {
		
		if (type instanceof SyntacticType.Primitive) {
			SyntacticType.Primitive p = (SyntacticType.Primitive) type;
			return p.type;
		} else if (type instanceof SyntacticType.Variable) {
			SyntacticType.Variable p = (SyntacticType.Variable) type;
			if(!generics.contains(p.var)) {
				internalFailure("undeclared generic variable encountered (" + p + ")",
						context.file().filename(), type);
				return null; // deadcode		
			}
			return SemanticType.Var(p.var);
		} else if(type instanceof SyntacticType.Not) {
			SyntacticType.Not t = (SyntacticType.Not) type;
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
		} else if(type instanceof SyntacticType.Or) {
			SyntacticType.Or t = (SyntacticType.Or) type;
			SemanticType[] types = new SemanticType[t.elements.length];
			for(int i=0;i!=t.elements.length;++i) {
				types[i] = convert(t.elements[i],generics,context);
			}
			return SemanticType.Or(types);
		} else if(type instanceof SyntacticType.And) {
			SyntacticType.And t = (SyntacticType.And) type;
			SemanticType[] types = new SemanticType[t.elements.length];
			for(int i=0;i!=t.elements.length;++i) {
				types[i] = convert(t.elements[i],generics,context);
			}
			return SemanticType.And(types);
		} else if(type instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple t = (SyntacticType.Tuple) type;
			SemanticType[] types = new SemanticType[t.elements.length];
			for(int i=0;i!=t.elements.length;++i) {
				types[i] = convert(t.elements[i],generics,context);
			}
			return SemanticType.Tuple(types);
		}
		
		internalFailure("unknown syntactic type encountered",
				context.file().filename(), type);
		return null; // deadcode
	}
	
	
	// ======================================================================
	// Private Implementation
	// ======================================================================

	protected void process(WycsFile module, Transform<WycsFile> stage)
			throws Exception {
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
	private WycsFile getModuleStub(WyalFile wyalFile) {
		ArrayList<WycsFile.Declaration> declarations = new ArrayList<WycsFile.Declaration>();
		for (WyalFile.Declaration d : wyalFile.declarations()) {
			if (d instanceof WyalFile.Define) {
				WyalFile.Define def = (WyalFile.Define) d;
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
