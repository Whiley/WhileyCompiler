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

import java.util.HashSet;
import java.util.Set;

import wycc.lang.Build;
import wycc.lang.SyntacticItem;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.QualifiedName;
import wyil.util.AbstractConsumer;
import wyc.util.ErrorMessages;
import static wyil.lang.WyilFile.*;

/**
 * <p>
 * Responsible for detecting cyclic static initialisers to ensure that every
 * static variable can be given an initial value. This differs from other
 * languages (e.g. Java) which don't detect static initialisers. Instead, in
 * such languages, static variables are given a _default_ value which is the
 * initial assignment used to resolve cyclic accesses. In Whiley, this approach
 * does not work because no default can be safely determined in the presence of
 * arbitrary type invariants. Furthermore, the approach taken in a language like
 * Java is not sensible. Static initialisers can be either _directly_ or
 * _indirectly_ cyclic, as illustrating with the following two examples:
 * </p>
 *
 * <pre>
 * int x = x + 1
 * int y = z + 1
 * int z = y
 * </pre>
 *
 * <p>
 * Here, static variable <code>x</code> is directly cyclic whereas
 * <code>y</code> and <code>z</code> are indirectly cyclic. An important
 * implication of eliminating static initialisers is that, for any given
 * `WyilFile`, one can determine a _safe_ initialisation order.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class StaticVariableCheck extends AbstractConsumer<Set<QualifiedName>> implements Compiler.Check {
	private boolean status = true;

	public StaticVariableCheck(Build.Meter meter) {
		super(meter.fork(StaticVariableCheck.class.getSimpleName()));
	}

	@Override
	public boolean check(WyilFile wf) {
		visitModule(wf, null);
		meter.done();
		return status;
	}

	@Override
	public void visitExternalUnit(Decl.Unit unit, Set<QualifiedName> accessed) {
		// NOTE: we override this to prevent unnecessarily traversing units
	}

	@Override
	public void visitStaticVariable(Decl.StaticVariable decl, Set<QualifiedName> accessed) {
		Expr initialiser = decl.getInitialiser();
		QualifiedName name = decl.getQualifiedName();
		accessed = new HashSet<>();
		visitExpression(initialiser, accessed);
		if (accessed.contains(name)) {
			// Indicates a cyclic static initialiser has been detected
			syntaxError(initialiser, CYCLIC_STATIC_INITIALISER);
		}
	}

	@Override
	public void visitType(Decl.Type decl, Set<QualifiedName> accessed) {
		// Don't need to visit other declarations
	}

	@Override
	public void visitCallable(Decl.Callable decl, Set<QualifiedName> accessed) {
		// Don't need to visit other declarations
	}

	@Override
	public void visitStatement(Stmt stmt, Set<QualifiedName> accessed) {
		// Don't need to visit statements at all
	}

	@Override
	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, Set<QualifiedName> accessed) {
		Decl.Link<Decl.StaticVariable> l = expr.getLink();
		Decl.StaticVariable decl = l.getTarget();
		QualifiedName name = decl.getQualifiedName();
		if (!accessed.contains(name)) {
			accessed.add(name);
			visitExpression(decl.getInitialiser(), accessed);
		}
	}

	@Override
	public void visitType(Type type, Set<QualifiedName> accessed) {
		// Don't need to visit types at all
	}

	private void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context);
	}
}
