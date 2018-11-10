
/**
 * Responsible for resolving a name which occurs at some position in a
 * compilation unit. This takes into account the context and, if necessary, will
 * traverse important statements to resolve the query. For example, consider a
 * compilation unit entitled "file":
 *
 * <pre>
 * import std::ascii::*
 *
 * function f(T1 x, T2 y) -> (int r):
 *    return g(x,y)
 * </pre>
 *
 * Here the name "<code>g</code>" is not fully qualified. Depending on which
 * file the matching declaration of <code>g</code> occurs will depend on what
 * its fully qualified name is. For example, if <code>g</code> is declared in
 * the current compilation unit then it's fully quaified name would be
 * <code>test.g</code>. However, it could well be declared in a compilation unit
 * matching the import <code>whiley.lang.*</code>.
 *
 * @author David J. Pearce
 *
 */
package wyil.transform;

import static wyil.lang.WyilFile.EXPR_invoke;
import static wyil.lang.WyilFile.EXPR_lambdaaccess;
import static wyil.lang.WyilFile.EXPR_staticvariable;
import static wyil.lang.WyilFile.TYPE_nominal;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.QualifiedName;
import wyil.lang.WyilFile.Type;

/**
 * Represents the cumulative knowledge of all symbols used in resolving names
 * for the given module. This is determined by the module itself, along with any
 * dependencies.
 *
 * @author David J. Pearce
 *
 */
public class SymbolTable {
	private final HashMap<QualifiedName, Entry> symbolTable = new HashMap<>();

	public boolean contains(QualifiedName name) {
		return symbolTable.containsKey(name);
	}

	public Entry get(QualifiedName name) {
		return symbolTable.get(name);
	}

	/**
	 * Register a new declaration with a given name.
	 *
	 * @param name
	 * @param declaration
	 */
	public void register(Decl.Named declaration, boolean external) {
		QualifiedName name = declaration.getQualifiedName();
		SymbolTable.Entry r = symbolTable.get(name);
		if (r == null) {
			r = new SymbolTable.Entry(external);
			symbolTable.put(name, r);
		}
		// Add the declaration
		r.getDeclarations().add(declaration);
	}

	/**
	 * Records information associated with a given symbol, such as whether it is
	 * defined within the current module or externally.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Entry {
		/**
		 * Identifies the complete set of declarations associated with this symbol.
		 */
		private final ArrayList<Decl.Named> declarations;
		/**
		 * Indicates whether or not this entry is externally defined in a dependency, or
		 * defined within the current module. Observe that externally defined symbols
		 * become internally defined once they are imported.
		 */
		private boolean external;

		public Entry(boolean external) {
			this.setExternal(external);
			this.declarations = new ArrayList<>();
		}

		/**
		 * Check whether this entry is defined externally to the current module.
		 *
		 * @return
		 */
		public boolean isExternal() {
			return external;
		}

		public void setExternal(boolean external) {
			this.external = external;
		}

		public List<Decl.Named> getDeclarations() {
			return declarations;
		}


		public void setDeclarations(List<Decl.Named> declarations) {
			this.declarations.clear();
			this.declarations.addAll(declarations);
		}
	}
}
