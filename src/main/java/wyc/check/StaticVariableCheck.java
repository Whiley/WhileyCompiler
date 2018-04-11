package wyc.check;

import java.util.HashSet;
import java.util.Set;

import wybs.lang.NameID;
import wybs.lang.NameResolver;
import wybs.lang.SyntaxError;
import wyc.lang.WhileyFile;
import wyc.task.CompileTask;
import wyc.util.AbstractConsumer;

import static wyc.lang.WhileyFile.*;
import static wyc.util.ErrorMessages.CYCLIC_STATIC_INITIALISER;
import static wyc.util.ErrorMessages.errorMessage;

public class StaticVariableCheck extends AbstractConsumer<Set<NameID>> {
	private final NameResolver resolver;

	public StaticVariableCheck(CompileTask builder) {
		this.resolver = builder.getNameResolver();
	}

	public void check(WhileyFile wf) {
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
				WhileyFile file = ((WhileyFile) initialiser.getHeap());
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
		try {
			Decl.StaticVariable decl = resolver.resolveExactly(expr.getName(), Decl.StaticVariable.class);
			NameID name = decl.getQualifiedName().toNameID();
			if (decl.hasInitialiser() && !accessed.contains(name)) {
				accessed.add(name);
				visitExpression(decl.getInitialiser(), accessed);
			}
		} catch (NameResolver.ResolutionError e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void visitType(Type type, Set<NameID> accessed) {
		// Don't need to visit types at all
	}
}
