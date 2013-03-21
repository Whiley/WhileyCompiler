// Copyright (c) 2012, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil.builders;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

import wyautl.io.PrettyAutomataWriter;
import wybs.io.AbstractLexer;
import wybs.io.Token;
import wybs.lang.Builder;
import wybs.lang.Logger;
import wybs.lang.NameSpace;
import wybs.lang.Path;
import wybs.lang.Pipeline;
import wybs.lang.SyntaxError;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.lang.Transform;
import wybs.util.Pair;
import wybs.util.Trie;
import static wybs.lang.SyntaxError.internalFailure;
import static wybs.lang.SyntaxError.syntaxError;
import static wycs.solver.Solver.SCHEMA;
import wyil.lang.*;
import wyil.transforms.RuntimeAssertions;
import wycs.WycsBuilder;
import wycs.solver.Solver;
import wycs.transforms.ConstraintInline;
import wycs.transforms.TypePropagation;
import wycs.util.WycsBuildTask;
import wycs.lang.Expr;
import wycs.lang.WycsFile;
import wycs.io.WycsFileFormatter;
import wycs.io.WycsFileLexer;
import wycs.io.WycsFilePrinter;

/**
 * Responsible for converting a Wyil file into a Wycs file which can then be
 * passed into the Whiley Constraint Solver (Wycs).  
 * 
 * @author David J. Pearce
 * 
 */
public class Wyil2WycsBuilder implements Builder {
	
	private NameSpace namespace;
	
	private String filename;
	
	private Logger logger;

	public Wyil2WycsBuilder(NameSpace namespace) {
		this.namespace = namespace;
	}
	
	public NameSpace namespace() {
		return namespace;
	}
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public void build(List<Pair<Path.Entry<?>,Path.Entry<?>>> delta) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();
			
		// ========================================================================
		// Translate files
		// ========================================================================

		for(Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.second();
			if(f.contentType() == WycsFile.ContentType) {
				Path.Entry<WyilFile> sf = (Path.Entry<WyilFile>) p.first();
				Path.Entry<WycsFile> df = (Path.Entry<WycsFile>) f;
				WycsFile contents = build(sf.read());
						
				// finally, write the file into its destination
				df.write(contents);
			}
		}
		
		// ========================================================================
		// Done.
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => Wycs: compiled " + delta.size()
				+ " file(s)", endTime - start, memory - runtime.freeMemory());
	}
	
	protected WycsFile build(WyilFile wyilFile) {
		this.filename = wyilFile.filename();

		// TODO: definitely need a better module ID here.
		final WycsFile wycsFile = new WycsFile(wyilFile.id(), filename);

		wycsFile.add(wycsFile.new Import((Trie) wyilFile.id(), null));
		
		// Add import statement(s) needed for any calls to functions from
		// wycs.core. In principle, it would be nice to cull this down to
		// exactly those that are needed ... but that's future work.
		wycsFile.add(wycsFile.new Import(WYCS_CORE_ALL, null));

		for (WyilFile.TypeDeclaration type : wyilFile.types()) {
			transform(type);
		}
		for (WyilFile.MethodDeclaration method : wyilFile.methods()) {
			transform(method, wyilFile, wycsFile);
		}

		return wycsFile;
	}

	protected void transform(WyilFile.TypeDeclaration def) {

	}

	protected void transform(WyilFile.MethodDeclaration method,
			WyilFile wyilFile, WycsFile wycsFile) {
		for (WyilFile.Case c : method.cases()) {
			transform(c, method, wyilFile, wycsFile);
		}
	}

	protected void transform(WyilFile.Case methodCase,
			WyilFile.MethodDeclaration method, WyilFile wyilFile,
			WycsFile wycsFile) {

		if (!RuntimeAssertions.getEnable()) {
			// inline constraints if they have not already been done.
			RuntimeAssertions rac = new RuntimeAssertions(this, filename);
			methodCase = rac.transform(methodCase, method);
		}

		Type.FunctionOrMethod fmm = method.type();
		int paramStart = 0;

		Block body = methodCase.body();

		VcBranch master = new VcBranch(method, body);

		for (int i = paramStart; i != fmm.params().size(); ++i) {
			Type paramType = fmm.params().get(i);
			master.write(i, Expr.Variable("r" + Integer.toString(i)));
		}

		Block precondition = methodCase.precondition();

		if (precondition != null) {
			VcBranch precond = new VcBranch(method, precondition);

			// FIXME: following seems like a hack --- there must be a more
			// elegant way of doing this?
			for (int i = paramStart; i != fmm.params().size(); ++i) {
				precond.write(i, master.read(i));
			}

			Expr constraint = precond.transform(new VcTransformer(this,
					wycsFile, filename, true));

			master.add(constraint);
		}

		master.transform(new VcTransformer(this, wycsFile, filename, false));
	}
	
	private static final Trie WYCS_CORE_ALL = Trie.ROOT.append("wycs").append("core").append("*");
}
