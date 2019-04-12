// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.check;

import java.io.IOException;
import java.util.List;

import wyal.lang.WyalFile;
import wyal.util.Interpreter;
import wyal.util.NameResolver;
import wyal.util.SmallWorldDomain;
import wyal.util.TypeChecker;
import wyal.util.WyalFileResolver;
import wybs.lang.Build;
import wybs.lang.SyntacticException;
import wybs.lang.SyntacticItem;
import wybs.lang.CompilationUnit.Name;
import wyc.lang.WhileyFile;
import wyfs.lang.Path;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.transform.VerificationConditionGenerator;
import wytp.provers.AutomatedTheoremProver;
import wytp.types.extractors.TypeInvariantExtractor;

public class VerificationCheck {
	private final Build.Project project;
	private final boolean counterexamples;
	private final Path.Root sourceRoot;

	public VerificationCheck(Build.Project project, Path.Root sourceRoot, boolean counterexamples) {
		this.project = project;
		this.counterexamples = counterexamples;
		this.sourceRoot = sourceRoot;
	}

	public void apply(Path.Entry<WyilFile> target, List<Path.Entry<WhileyFile>> sources) throws IOException {
		// FIXME: this is really a bit of a kludge right now. The basic issue is that,
		// in the near future, the VerificationConditionGenerator will operate directly
		// on the WyilFile rather than creating a WyalFile. Then, the theorem prover can
		// work on the WyilFile directly as well and, hence, this will become more like
		// a compilation stage (as per others above).
		try {
			wytp.types.TypeSystem typeSystem = new wytp.types.TypeSystem(project);
			// FIXME: this unfortunately puts it in the wrong directory.
			Path.Entry<WyalFile> wyalTarget = project.getRoot().get(target.id(),WyalFile.ContentType);
			if (wyalTarget == null) {
				wyalTarget = project.getRoot().create(target.id(), WyalFile.ContentType);
				wyalTarget.write(new WyalFile(wyalTarget));
			}
			WyalFile contents = new VerificationConditionGenerator(new WyalFile(wyalTarget)).translate(target.read());
			new TypeChecker(typeSystem, contents, target).check();
			wyalTarget.write(contents);
			wyalTarget.flush();
			// Now try to verfify it
			AutomatedTheoremProver prover = new AutomatedTheoremProver(typeSystem);
			// FIXME: this is horrendous :(
			prover.check(contents, sourceRoot);

		} catch(SyntacticException e) {
			//
			SyntacticItem item = e.getElement();
			String message = e.getMessage();
			if(counterexamples && item instanceof WyalFile.Declaration.Assert) {
				message += " (" + findCounterexamples((WyalFile.Declaration.Assert) item) + ")";
			}
			// FIXME: translate from WyilFile to WhileyFile. This is a temporary hack
			if(item != null && e.getEntry() != null && e.getEntry().contentType() == WyilFile.ContentType) {
				Decl.Unit unit = item.getAncestor(Decl.Unit.class);
				// Determine which source file this entry is contained in
				Path.Entry<WhileyFile> sf = getWhileySourceFile(sourceRoot, unit.getName(), sources);
				//
				throw new SyntacticException(message,sf,item,e.getCause());
			} else {
				throw new SyntacticException(message,e.getEntry(),item,e.getCause());
			}
		}
	}


	public String findCounterexamples(WyalFile.Declaration.Assert assertion) {
		// FIXME: it doesn't feel right creating new instances here.
		NameResolver resolver = new WyalFileResolver(project);
		TypeInvariantExtractor extractor = new TypeInvariantExtractor(resolver);
		Interpreter interpreter = new Interpreter(new SmallWorldDomain(resolver), resolver, extractor);
		try {
			Interpreter.Result result = interpreter.evaluate(assertion);
			if (!result.holds()) {
				// FIXME: this is broken
				return result.getEnvironment().toString();
			}
		} catch (Interpreter.UndefinedException e) {
			// do nothing for now
		}
		return "no counterexample";
	}


	// FIXME: this is totally broken
	private static Path.Entry<WhileyFile> getWhileySourceFile(Path.Root root, Name name,
			List<Path.Entry<WhileyFile>> sources) throws IOException {
		String nameStr = name.toString().replace("::", "/");
		//
		for (Path.Entry<WhileyFile> e : sources) {
			if (root.contains(e) && e.id().toString().endsWith(nameStr)) {
				return e;
			}
		}
		throw new IllegalArgumentException("unknown unit");
	}
}
