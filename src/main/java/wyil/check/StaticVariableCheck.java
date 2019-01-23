package wyil.check;

import java.util.HashSet;
import java.util.Set;

import wybs.lang.Build;
import wybs.lang.SyntaxError;
import wyc.task.CompileTask;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.QualifiedName;
import wyil.util.AbstractConsumer;

import static wyc.util.ErrorMessages.CYCLIC_STATIC_INITIALISER;
import static wyc.util.ErrorMessages.errorMessage;
import static wyil.lang.WyilFile.*;

public class StaticVariableCheck extends AbstractConsumer<Set<QualifiedName>> {

	public void check(WyilFile wf) {
		visitModule(wf, null);
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
				String msg = errorMessage(CYCLIC_STATIC_INITIALISER);
				WyilFile file = ((WyilFile) initialiser.getHeap());
				throw new SyntaxError(msg, file.getEntry(), initialiser);
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
}
