package wycs;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wybs.lang.SyntaxError.*;
import static wycs.solver.Solver.SCHEMA;
import wyautl.io.PrettyAutomataWriter;
import wybs.lang.*;
import wybs.lang.Path.Entry;
import wybs.io.Token;
import wybs.util.Pair;
import wybs.util.ResolveError;
import wybs.util.Trie;
import wycs.io.WycsFileFormatter;
import wycs.io.WycsFileLexer;
import wycs.io.WycsFilePrinter;
import wycs.lang.WycsFile;
import wycs.solver.Solver;
import wycs.transforms.VerificationCheck;

public class WycsBuilder implements Builder {

	/**
	 * The master namespace for identifying all resources available to the
	 * builder. This includes all modules declared in the project being verified
	 * and/or defined in external resources (e.g. jar files).
	 */
	private final NameSpace namespace;

	/**
	 * The list of stages which must be applied to a Wycs file.
	 */
	private final List<Transform<WycsFile>> pipeline;

	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import wycs.lang.*</code>
	 * corresponds to the triple <code>("wycs.lang",*,null)</code>.
	 */
	private final HashMap<Trie, ArrayList<Path.ID>> importCache = new HashMap();

	/**
	 * A map of the source files currently being compiled.
	 */
	private final HashMap<Path.ID, Path.Entry<WycsFile>> srcFiles = new HashMap<Path.ID, Path.Entry<WycsFile>>();

	private Logger logger;

	private boolean debug = false;

	public WycsBuilder(NameSpace namespace, Pipeline<WycsFile> pipeline) {
		this.logger = Logger.NULL;
		this.namespace = namespace;
		this.pipeline = pipeline.instantiate(this);
	}

	public NameSpace namespace() {
		return namespace;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	// ======================================================================
	// Build Method
	// ======================================================================

	@Override
	public void build(List<Pair<Entry<?>, Entry<?>>> delta) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();

		// ========================================================================
		// Parse and register source files
		// ========================================================================

		srcFiles.clear();
		int count = 0;
		for (Pair<Path.Entry<?>, Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.first();
			if (f.contentType() == WycsFile.ContentType) {
				Path.Entry<WycsFile> sf = (Path.Entry<WycsFile>) f;
				WycsFile wf = sf.read();
				count++;
				srcFiles.put(wf.module(), sf);
			}
		}

		logger.logTimedMessage("Parsed " + count + " source file(s).",
				System.currentTimeMillis() - start,
				memory - runtime.freeMemory());

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
						if (debug) {
							// this feels like a very long winded way to do
							// this....
							StringWriter writer = new StringWriter();
							new WycsFilePrinter(writer).write(module);
							String input = writer.toString();
							List<Token> tokens = new WycsFileLexer(
									new StringBufferInputStream(input)).scan();
							new WycsFileFormatter().format(tokens);
							for(Token t : tokens) {
								System.err.print(t.text);
							}
							
							if (ex.original() != null) {
								new PrettyAutomataWriter(System.err, SCHEMA,
										"And", "Or").write(ex.original());
								System.err.println("\n\n=> (" + Solver.numSteps
										+ " steps, " + Solver.numInferences
										+ " reductions, "
										+ Solver.numInferences
										+ " inferences)\n");
								new PrettyAutomataWriter(System.err, SCHEMA,
										"And", "Or").write(ex.reduction());
							}
						}
						// FIXME: this feels a bit like a hack.
						syntaxError(ex.getMessage(), module.filename(),
								ex.assertion(), ex);
					}
				}
			}
		}
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
			for(Map.Entry<Path.ID,Path.Entry<WycsFile>> e : srcFiles.entrySet()) {
				Path.Entry<WycsFile> pe = e.getValue();
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
	 * module does not exist, a resolve error is thrown.
	 * 
	 * @param mid
	 * @return
	 * @throws Exception
	 */
	public WycsFile getModule(Path.ID mid) throws Exception {
		// first, check in those files being compiled.
		for (Map.Entry<Path.ID, Path.Entry<WycsFile>> e : srcFiles.entrySet()) {
			Path.Entry<WycsFile> pe = e.getValue();
			if (pe.id().equals(mid)) {
				return pe.read();
			}
		}
		// second, check the wider namespace
		return namespace.get(mid, WycsFile.ContentType).read();
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
			String name, Class<T> type, WycsFile.Context context)
			throws ResolveError {

		for (WycsFile.Import imp : context.imports()) {
			for (Path.ID id : imports(imp.filter)) {
				try {
					System.out.println("TRYING: " + id);
					WycsFile wf = getModule(id);
					T d = wf.declaration(name, type);
					if (d != null) {
						return new Pair<NameID, T>(new NameID(id, name), d);
					}
				} catch (ResolveError e) {
					throw e;
				} catch (Exception e) {
					internalFailure(e.getMessage(), context.file().filename(),
							context, e);
				}
			}
		}

		throw new ResolveError("cannot resolve name as function: " + name);
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

				for (Path.Entry<WycsFile> sf : srcFiles.values()) {
					if (key.matches(sf.id())) {
						System.out.println("ADDING MATCH");
						matches.add(sf.id());
					}
				}

				if (key.isConcrete()) {
					System.out.println("CONCRETE: " + key);
					// A concrete key is one which does not contain a wildcard.
					// Therefore, it corresponds to exactly one possible item.
					// It is helpful, from a performance perspective, to use
					// NameSpace.exists() in such case, as this conveys the fact
					// that we're only interested in a single item.
					if (exists(key)) {
						System.out.println("MATCHED: " + key);
						matches.add(key);
					}
				} else {
					Content.Filter<?> binFilter = Content.filter(key,
							WycsFile.ContentType);
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

	// ======================================================================
	// Private Implementation
	// ======================================================================

	private void process(WycsFile module, Transform<WycsFile> stage)
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
