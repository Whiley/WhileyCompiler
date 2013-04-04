package wycs.builders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static wybs.lang.SyntaxError.*;
import static wycs.solver.Solver.SCHEMA;
import wyautl.io.PrettyAutomataWriter;
import wybs.lang.*;
import wybs.lang.Path.Entry;
import wybs.util.Pair;
import wybs.util.ResolveError;
import wybs.util.Trie;
import wycs.core.WycsFile;
import wycs.io.WyalFileStructuredPrinter;
import wycs.io.WycsFilePrinter;
import wycs.solver.Solver;
import wycs.syntax.WyalFile;
import wycs.transforms.TypePropagation;
import wycs.transforms.VerificationCheck;

public class Wycs2WyalBuilder implements Builder {

	/**
	 * The master namespace for identifying all resources available to the
	 * builder. This includes all modules declared in the project being verified
	 * and/or defined in external resources (e.g. jar files).
	 */
	protected final NameSpace namespace;

	protected Logger logger = Logger.NULL;

	protected boolean debug = false;

	public Wycs2WyalBuilder(NameSpace namespace) {
		this.namespace = namespace;
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
		long startTime = System.currentTimeMillis();
		long startMemory = runtime.freeMemory();
		long tmpTime = startTime;
		long tmpMem = startMemory;
		
		// ========================================================================
		// Parse and register source files
		// ========================================================================

		int count = 0;

		for (Pair<Path.Entry<?>, Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.first();
			Path.Entry<?> s = p.second();
			if (f.contentType() == WycsFile.ContentType && s.contentType() == WyalFile.ContentType) {
				Path.Entry<WycsFile> sf = (Path.Entry<WycsFile>) f;
				Path.Entry<WyalFile> ff = (Path.Entry<WyalFile>) s;
				WycsFile wf = sf.read();
				// NOTE: following is really a temporary hack
				new WycsFilePrinter(System.err).write(wf);
				WyalFile waf = decompile(wf);				
				ff.write(waf);				
				count++;
			}
		}

		// ========================================================================
		// Done
		// ========================================================================
		
		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wycs => Wyal: decompiled " + delta.size() + " file(s)",
				endTime - startTime, startMemory - runtime.freeMemory());
	}

	protected WyalFile decompile(WycsFile wycsFile) {
		return new WyalFile(wycsFile.id(), wycsFile.filename());
	}
}
