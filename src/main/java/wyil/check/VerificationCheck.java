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
import wybs.lang.CompilationUnit.Name;
import wybs.lang.SyntacticException;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.lang.WhileyFile;
import wyc.util.ErrorMessages;
import wyfs.lang.Path;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Attr.CounterExample;
import wyil.transform.VerificationConditionGenerator;
import wytp.provers.AutomatedTheoremProver;
import wytp.types.extractors.TypeInvariantExtractor;

public class VerificationCheck {
	private final Build.Meter meter;
	private final Build.Project project;
	private Path.Entry<WyalFile> wyalTarget;
	private Path.Entry<WyilFile> target;
	//private final Path.Root sourceRoot;

	public VerificationCheck(Build.Meter meter, Build.Project project, Path.Entry<WyilFile> target) throws IOException {
		this.meter = meter.fork(VerificationCheck.class.getSimpleName());
		this.project = project;
		//
		wyalTarget = project.getRoot().get(target.id(),WyalFile.ContentType);
		if (wyalTarget == null) {
			wyalTarget = project.getRoot().create(target.id(), WyalFile.ContentType);
			wyalTarget.write(new WyalFile(wyalTarget));
		}
		//
		this.target = target;
	}

	public boolean check(WyilFile target, boolean counterexamples) {
		// FIXME: this is really a bit of a kludge right now. The basic issue is that,
		// in the near future, the VerificationConditionGenerator will operate directly
		// on the WyilFile rather than creating a WyalFile. Then, the theorem prover can
		// work on the WyilFile directly as well and, hence, this will become more like
		// a compilation stage (as per others above).
		try {
			wytp.types.TypeSystem typeSystem = new wytp.types.TypeSystem(project);
			// FIXME: this unfortunately puts it in the wrong directory.
			WyalFile contents = new VerificationConditionGenerator(meter,new WyalFile(wyalTarget)).translate(target);
			new TypeChecker(typeSystem, contents, this.target).check();
//			wyalTarget.write(contents);
//			wyalTarget.flush();
			// Now try to verfify it
			AutomatedTheoremProver prover = new AutomatedTheoremProver(typeSystem);
			// FIXME: this is horrendous :(
			prover.check(contents);
			//
			return true;
		} catch(SyntacticException e) {
			//
			SyntacticItem item = e.getElement();
			String message = e.getMessage();
			// FIXME: translate from WyilFile to WhileyFile. This is a temporary hack
			if(item instanceof WyalFile.Declaration.Assert) {
				// Extract failed assertion
				WyalFile.Declaration.Assert assertion = (WyalFile.Declaration.Assert) item;
				//
				int code = codeFromMessage(e.getMessage());
				CounterExample[] cegs;
				if (counterexamples) {
					cegs = findCounterexamples(assertion);
				} else {
					cegs = new CounterExample[0];
				}
				ErrorMessages.syntaxError(assertion.getContext(), code, cegs);
				return false;
			} else {
				// FIXME: enjoy debugging this when the time comes :)
				throw new SyntacticException(message,null,item,e);
			}
		} finally {
			meter.done();
		}
	}

	public CounterExample[] findCounterexamples(WyalFile.Declaration.Assert assertion) {
		// FIXME: it doesn't feel right creating new instances here.
		NameResolver resolver = new WyalFileResolver(project);
		TypeInvariantExtractor extractor = new TypeInvariantExtractor(resolver);
		Interpreter interpreter = new Interpreter(new SmallWorldDomain(resolver), resolver, extractor);
		try {
			Interpreter.Result result = interpreter.evaluate(assertion);
			if (!result.holds()) {
				return toCounterExamples(result.getEnvironment());
			}
		} catch (Exception e) {
			// do nothing for now
		}
		return new CounterExample[0];
	}

	private static CounterExample[] toCounterExamples(Interpreter.Environment environment) {
		Value.Dictionary dict = environment.toDictionary();
		return new CounterExample[] { new CounterExample(dict) };
	}

	// FIXME: this is totally dumb
	private static int codeFromMessage(String message) {
		switch(message) {
		case "assertion failed":
			return WyilFile.STATIC_ASSERTION_FAILURE;
		case "possible panic":
			return WyilFile.STATIC_FAULT;
		case "type invariant may not be satisfied":
			return WyilFile.STATIC_TYPEINVARIANT_FAILURE;
		case "precondition may not be satisfied":
			return WyilFile.STATIC_PRECONDITION_FAILURE;
		case "postcondition may not be satisfied":
			return WyilFile.STATIC_POSTCONDITION_FAILURE;
		case "loop invariant may not be established by first iteration":
			return WyilFile.STATIC_ESTABLISH_LOOPINVARIANT_FAILURE;
		case "loop invariant may not hold on entry":
			return WyilFile.STATIC_ENTER_LOOPINVARIANT_FAILURE;
		case "loop invariant may not be restored":
			return WyilFile.STATIC_RESTORE_LOOPINVARIANT_FAILURE;
		case "division by zero":
			return WyilFile.STATIC_DIVIDEBYZERO_FAILURE;
		case "index out of bounds (negative)":
			return WyilFile.STATIC_BELOWBOUNDS_INDEX_FAILURE;
		case "index out of bounds (not less than length)":
			return WyilFile.STATIC_ABOVEBOUNDS_INDEX_FAILURE;
		case "negative length possible":
			return WyilFile.STATIC_NEGATIVE_LENGTH_FAILURE;
		default:
			throw new RuntimeException("unknown verification message encountered");
		}
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
