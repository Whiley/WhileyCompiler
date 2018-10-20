package wyil.check;

import java.util.HashSet;
import java.util.Set;

import wybs.lang.NameID;
import wybs.lang.SyntaxError;
import wyc.task.CompileTask;
import wyil.lang.WyilFile;
import wyil.util.AbstractConsumer;

import static wyc.util.ErrorMessages.CYCLIC_STATIC_INITIALISER;
import static wyc.util.ErrorMessages.errorMessage;
import static wyil.lang.WyilFile.*;

public class StaticVariableCheck extends AbstractConsumer<Set<NameID>> {

	public StaticVariableCheck(CompileTask builder) {
	}

	public void check(WyilFile wf) {
		visitWhileyFile(wf, null);
	}

	@Override
	public void visitStaticVariable(Decl.StaticVariable decl, Set<NameID> accessed) {
		if (decl.hasInitialiser()) {
			Expr initialiser = decl.getInitialiser();
			NameID name = decl.getQualifiedName().toNameID();
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
	public void visitType(Decl.Type decl, Set<NameID> accessed) {
		// Don't need to visit other declarations
	}

	@Override
	public void visitCallable(Decl.Callable decl, Set<NameID> accessed) {
		// Don't need to visit other declarations
	}

	@Override
	public void visitStatement(Stmt stmt, Set<NameID> accessed) {
		// Don't need to visit statements at all
	}

	@Override
	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, Set<NameID> accessed) {
		Decl.StaticVariable decl = expr.getDeclaration();
		NameID name = decl.getQualifiedName().toNameID();
		if (decl.hasInitialiser() && !accessed.contains(name)) {
			accessed.add(name);
			visitExpression(decl.getInitialiser(), accessed);
		}
	}

	@Override
	public void visitType(Type type, Set<NameID> accessed) {
		// Don't need to visit types at all
	}
}
