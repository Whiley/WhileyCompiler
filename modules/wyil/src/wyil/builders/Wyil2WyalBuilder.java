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

import java.util.*;

import wybs.lang.Builder;
import wybs.lang.Logger;
import wybs.lang.NameSpace;
import wybs.lang.Path;
import wybs.lang.Pipeline;
import wybs.lang.Transform;
import wybs.util.Pair;
import wybs.util.Trie;
import static wybs.lang.SyntaxError.syntaxError;
import wyil.lang.*;
import wyil.transforms.RuntimeAssertions;
import wycs.builders.Wyal2WycsBuilder;
import wycs.core.WycsFile;
import wycs.syntax.Expr;
import wycs.syntax.WyalFile;
import wycs.transforms.VerificationCheck;

/**
 * Responsible for converting a Wyil file into a Wycs file which can then be
 * passed into the Whiley Constraint Solver (Wycs).  
 * 
 * @author David J. Pearce
 * 
 */
public class Wyil2WyalBuilder implements Builder {

	/**
	 * The master namespace for identifying all resources available to the
	 * builder. This includes all modules declared in the project being verified
	 * and/or defined in external resources (e.g. jar files).
	 */
	protected final NameSpace namespace;

	protected Logger logger = Logger.NULL;

	private String filename;
	
	public Wyil2WyalBuilder(NameSpace namespace) {
		this.namespace = namespace;
	}
	

	public NameSpace namespace() {
		return namespace;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public void build(List<Pair<Path.Entry<?>,Path.Entry<?>>> delta) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();
			
		// ========================================================================
		// Translate files
		// ========================================================================

		for(Pair<Path.Entry<?>,Path.Entry<?>> p : delta) {
			Path.Entry<?> f = p.second();
			if(f.contentType() == WyalFile.ContentType) {
				Path.Entry<WyilFile> sf = (Path.Entry<WyilFile>) p.first();
				Path.Entry<WyalFile> df = (Path.Entry<WyalFile>) f;
				WyalFile contents = build(sf.read());
				// Write the file into its destination
				df.write(contents);
				// Then, flush contents to disk in case we generate an assertion
				// error later. In principle, this should be unnecessary when
				// syntax errors are no longer implemented as exceptions.
				df.flush();
			}
		}
		
		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => Wyal: compiled " + delta.size()
				+ " file(s)", endTime - start, memory - runtime.freeMemory());
	}
		
	protected WyalFile build(WyilFile wyilFile) {
		this.filename = wyilFile.filename();

		// TODO: definitely need a better module ID here.
		final WyalFile wyalFile = new WyalFile(wyilFile.id(), filename);

		for (WyilFile.TypeDeclaration type : wyilFile.types()) {
			transform(type);
		}
		for (WyilFile.MethodDeclaration method : wyilFile.methods()) {
			transform(method, wyilFile, wyalFile);
		}

		return wyalFile;
	}

	protected void transform(WyilFile.TypeDeclaration def) {

	}

	protected void transform(WyilFile.MethodDeclaration method,
			WyilFile wyilFile, WyalFile wycsFile) {
		for (WyilFile.Case c : method.cases()) {
			transform(c, method, wyilFile, wycsFile);
		}
	}

	protected void transform(WyilFile.Case methodCase,
			WyilFile.MethodDeclaration method, WyilFile wyilFile,
			WyalFile wycsFile) {

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
}
