package wyc.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wycc.lang.Build;
import wycc.lang.Content;
import wycc.lang.Build.SnapShot;
import wycc.lang.Filter;
import wycc.lang.Path;
import wycc.util.AbstractCompilationUnit.Name;
import wycc.util.AbstractCompilationUnit.Tuple;
import wyc.io.WhileyFileParser;
import wyc.lang.WhileyFile;
import wycc.util.Pair;
import wyil.check.DefiniteAssignmentCheck;
import wyil.check.DefiniteUnassignmentCheck;
import wyil.check.FlowTypeCheck;
import wyil.check.FunctionalCheck;
import wyil.check.RecursiveTypeCheck;
import wyil.check.SignatureCheck;
import wyil.check.StaticVariableCheck;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.transform.MoveAnalysis;
import wyil.transform.NameResolution;

public class CompileTask implements Build.Task {
	private final Build.Meter meter = Build.NULL_METER;
	private final List<Build.Package> packages;
	/**
	 * Determine the set of files which will be compiled by this task.
	 */
	private final Filter includes = Filter.fromString("**/*");
	/**
	 * Identifier for target of this build task.
	 */
	private final Path target;

	public CompileTask(Path target, Collection<Build.Package> packages) {
		this.target = target;
		this.packages = new ArrayList<>(packages);
	}

	@Override
	public Path getTarget() {
		return target;
	}

	@Override
	public Content.Type<WyilFile> getContentType() {
		return WyilFile.ContentType;
	}
	
	@Override
	public Pair<SnapShot, Boolean> apply(SnapShot t) {
		// Identify all Whiley source files
		List<WhileyFile> sources = t.match(WhileyFile.class, includes);
		// Compile into a single binary target
		Pair<WyilFile, Boolean> r = compile(sources);
		// Write target into snapshot
		t = t.put(r.first());
		// Done
		return new Pair<>(t, r.second());
	}

	private Pair<WyilFile, Boolean> compile(List<WhileyFile> sources) {
		WyilFile target = new WyilFile(this.target, sources);
		// Construct root entry
		target.setRootItem(
				new WyilFile.Decl.Module(new Name(this.target), new Tuple<>(), new Tuple<>(), new Tuple<>()));
		// Identify success or failure
		boolean r = true;
		// Parse all source files
		for (int i = 0; i != sources.size(); ++i) {
			// Read ith source file
			WhileyFile source = sources.get(i);
			// Construct parser
			WhileyFileParser wyp = new WhileyFileParser(target, source);
			// Parse it
			r &= wyp.read(meter);
		}
		// Perform name resolution.
		try {
			r = r && new NameResolution(meter, packages, target).apply();
		} catch (IOException e) {
			// FIXME: this is clearly broken.
			throw new RuntimeException(e);
		}
		// ========================================================================
		// Recursive Type Check
		// ========================================================================
		new RecursiveTypeCheck(meter).check(target);
		// ========================================================================
		// Flow Type Checking
		// ========================================================================
		// Instantiate type checker
		FlowTypeCheck checker = new FlowTypeCheck(meter);
		r = r && checker.check(target);
		// ========================================================================
		// Compiler Checks
		// ========================================================================
		Compiler.Check[] stages = instantiateChecks(meter);
		for (int i = 0; i != stages.length; ++i) {
			r = r && stages[i].check(target);
		}
//		if(r && verification) {
//			// NOTE: cannot generate verification conditions if WyilFile is in a bad state
//			// (e.g. has unresolved links).
//			WyalFile obligations = verifier.initialise(target);
//			r = verifier.check(obligations,counterexamples);
//		}
		// Transforms
		if (r) {
			Compiler.Transform[] transforms = instantiateTransforms(meter);
			// Only apply if previous stages have all passed.
			for (int i = 0; i != transforms.length; ++i) {
				transforms[i].apply(target);
			}
		}
		// Collect garbage
		// target.gc();
		//
		meter.done();
		//
		// FIXME: how to handle errors?
		//
		return new Pair<>(target, r);
	}

	private static Compiler.Check[] instantiateChecks(Build.Meter m) {
		return new Compiler.Check[] { new DefiniteAssignmentCheck(m), new DefiniteUnassignmentCheck(m),
				new FunctionalCheck(m), new SignatureCheck(m), new StaticVariableCheck(m) };
	}

	private static Compiler.Transform[] instantiateTransforms(Build.Meter meter) {
		return new Compiler.Transform[] { new MoveAnalysis(meter),
//				new RecursiveTypeAnalysis(meter)
		};
	}
}
