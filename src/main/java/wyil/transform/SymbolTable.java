
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

import static wyil.lang.WyilFile.Modifier;
import static wyil.lang.WyilFile.Name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wybs.util.AbstractCompilationUnit.Identifier;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.QualifiedName;

/**
 * Represents the cumulative knowledge of all symbols used in resolving names
 * for the given module. This is determined by the module itself, along with any
 * dependencies.
 *
 * @author David J. Pearce
 *
 */
public class SymbolTable {
	/**
	 * The enclosing WyilFile this symbol table is operating on.
	 */
	private final WyilFile target;

	/**
	 * Cached information about symbols available in the given WyilFile and external
	 * dependencies.
	 */
	private final HashMap<Name, Group> symbolTable = new HashMap<>();

	/**
	 * Construct a symbol table containing meta-data on a given target file and its
	 * dependencies.
	 *
	 * @param target
	 * @param deps
	 */
	public SymbolTable(WyilFile target, List<WyilFile> deps) {
		this.target = target;
		// Register all internal symbols
		register(target.getModule(),false);
		// Register all external symbols
		for(int i=0;i!=deps.size();++i) {
			register(deps.get(i).getModule(),true);
		}
	}

	public boolean contains(QualifiedName name) {
		return symbolTable.containsKey(name);
	}

	/**
	 * Check whether a given symbol is currently external to the enclosing WyilFile.
	 * Specifically, this indicates whether or not a stub is available for it.
	 *
	 * @param name
	 * @return
	 */
	public boolean isExternal(QualifiedName name) {
		// FIXME: need to think this through
	}

	public List<Decl.Named> getDeclarations(QualifiedName name) {
		return symbolTable.get(name.getUnit()).entries.get(name.getName()).declarations;
	}

	/**
	 * Register all symbols in all compilation units contained within a given
	 * module. For those which are externally defined, only public members are
	 * registered.
	 *
	 * @param unit
	 * @param external
	 */
	public void register(Decl.Module module, boolean external) {
		for (Decl.Unit unit : module.getUnits()) {
			register(unit, external);
		}
	}

	/**
	 * Register all symbols in a given compilation unit with this symbol table. For
	 * those which are externally defined, only public members are registered.
	 *
	 * @param unit
	 * @param external
	 */
	public void register(Decl.Unit unit, boolean external) {
		// Go through each named declaration in this unit
		for (Decl d : unit.getDeclarations()) {
			if (d instanceof Decl.Named) {
				Decl.Named n = (Decl.Named) d;
				// External declarations must be public
				if(!external || isPublic(n)) {
					register(n, external);
				}
			}
		}
	}

	/**
	 * Register a new declaration with a given name.
	 *
	 * @param name
	 * @param declaration
	 */
	public void register(Decl.Named declaration, boolean external) {
		QualifiedName name = declaration.getQualifiedName();
		Name unit = name.getUnit();
		// Get information associated with this unit
		SymbolTable.Group group = symbolTable.get(unit);
		if(group == null) {
			// Create fresh group information
			group = new SymbolTable.Group();
			symbolTable.put(unit,group);
		}
		// Register this declaration with the group
		group.register(declaration,external);
	}

	/**
	 * Check whether named declaration is public or not.
	 *
	 * @param decl
	 * @return
	 */
	private boolean isPublic(Decl.Named decl) {
		return decl.getModifiers().match(Modifier.Public.class) != null;
	}

	/**
	 * Record information associated with a given group of symbols (i.e. as located
	 * in same compilation unit).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Group {
		private HashMap<Identifier, Entry> entries;

		public void register(Decl.Named declaration, boolean external) {
			Identifier name = declaration.getName();
			SymbolTable.Entry r = entries.get(name);
			//
			if (r == null) {
				r = new SymbolTable.Entry(external);
				entries.put(name, r);
			}
			// Add the declaration
			r.declarations.add(declaration);
		}
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
		 * defined within the current module.
		 */
		private final boolean external;

		public Entry(boolean external) {
			this.external = external;
			this.declarations = new ArrayList<>();
		}
	}
}
