package wyil.check;

import java.util.HashSet;
import java.util.Set;

import wybs.lang.Build;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.task.CompileTask;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
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

	@Override
	public boolean check(WyilFile wf) {
		visitModule(wf, null);
		return status;
	}

	@Override
	public void visitStaticVariable(Decl.StaticVariable decl, Set<QualifiedName> accessed) {
		if (decl.hasInitialiser()) {
			Expr initialiser = decl.getInitialiser();
			QualifiedName name = decl.getQualifiedName();
			accessed = new HashSet<>();
			visitExpression(initialiser, accessed);
			if (accessed.contains(name)) {
				// Indicates a cyclic static initialiser has been detected
				syntaxError(initialiser, CYCLIC_STATIC_INITIALISER);
			}
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
		Decl.StaticVariable decl = expr.getDeclaration();
		QualifiedName name = decl.getQualifiedName();
		if (decl.hasInitialiser() && !accessed.contains(name)) {
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
