
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jsynheap.util.AbstractCompilationUnit.Identifier;
import jsynheap.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Decl.Named;
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
	 * The consolidation list identifies those which need to be consolidated
	 */
	private final HashSet<ExternalGroup> consolidations = new HashSet<>();

	/**
	 * Construct a symbol table containing meta-data on a given target file and its
	 * dependencies.
	 *
	 * @param target
	 * @param deps
	 */
	public SymbolTable(WyilFile target, List<WyilFile> deps) {
		this.target = target;
		//
		Decl.Module module = target.getModule();
		// Register all internal symbols
		for (Decl.Unit unit : module.getUnits()) {
			symbolTable.put(unit.getName(), new LocalGroup(unit));
		}
		// Register all external symbols
		for(int i=0;i!=deps.size();++i) {
			Decl.Module dep = deps.get(i).getModule();
			for (Decl.Unit unit : dep.getUnits()) {
				symbolTable.put(unit.getName(), new ExternalGroup(unit));
			}
		}
		// Register any available (i.e. imported) external symbols
		for (Decl.Unit unit : module.getExterns()) {
			ExternalGroup group = (ExternalGroup) symbolTable.get(unit.getName());
			group.register(unit);
		}
	}

	/**
	 * Check whether a given qualified name is registered. That is, whether or not
	 * there is a corresponding name in a given group.
	 *
	 * @param name
	 * @return
	 */
	public boolean contains(QualifiedName name) {
		Name unit = name.getUnit();
		// Get information associated with this unit
		SymbolTable.Group group = symbolTable.get(unit);
		return group != null && group.isValid(name.getName());
	}

	/**
	 * Check whether a given unit is known to this symbol table.
	 *
	 * @param name
	 * @return
	 */
	public boolean contains(Name name) {
		// Get information associated with this unit
		SymbolTable.Group group = symbolTable.get(name);
		return group != null;
	}

	/**
	 * Check whether a given symbol is currently external to the enclosing WyilFile.
	 * Specifically, this indicates whether or not a stub is available for it.
	 *
	 * @param name
	 * @return
	 */
	public boolean isAvailable(QualifiedName name) {
		SymbolTable.Group group = symbolTable.get(name.getUnit());
		return group != null && group.isAvailable(name.getName());
	}

	/**
	 * Check whether a given unit is know to this symbol table.
	 *
	 * @param name
	 * @return
	 */
	public Group getGroup(Name name) {
		return symbolTable.get(name);
	}

	/**
	 * Get the actual declarations associated with a given symbol.
	 *
	 * @param name
	 * @return
	 */
	public List<Decl.Named> getRegisteredDeclarations(QualifiedName name) {
		Group g = symbolTable.get(name.getUnit());
		if (g != null) {
			return g.getRegisteredDeclarations(name.getName());
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * Get the available declarations associated with a given symbol.
	 *
	 * @param name
	 * @return
	 */
	public List<Decl.Named> getAvailableDeclarations(QualifiedName name) {
		Group g = symbolTable.get(name.getUnit());
		if (g != null) {
			return g.getAvailableDeclarations(name.getName());
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	/**
	 * Make available declarations for an external symbol. This makes those
	 * declarations available within the target for linking.
	 *
	 * @param name      Fully qualified name of symbol being consolidated.
	 * @param available List of declaration stubs which have now been imported into
	 *                  the target.
	 */
	public void addAvailable(QualifiedName name, List<Decl.Named> available) {
		ExternalGroup group = (ExternalGroup) symbolTable.get(name.getUnit());
		//
		for (int i = 0; i != available.size(); ++i) {
			group.addAvailable(available.get(i));
		}
	}

	/**
	 * Consolidate the status of external symbols. For example, this will ensure all
	 * external units which have imported symbols are made available. Likewise, it
	 * may garbage collect available symbols and units which are no longer required.
	 *
	 * @return
	 */
	public void consolidate() {
		Decl.Module module = target.getModule();
		for(ExternalGroup group : consolidations) {
			// TODO: this could be made way more efficient by collecting all consolidate
			// units into one batch
			module.putExtern(group.consolidate());
		}
		consolidations.clear();
	}

	/**
	 * Represents a group of symbols which are related through their enclosing
	 * compilation unit.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Group {
		/**
		 * Check whether a given symbol existing within this group or not.
		 *
		 * @param name
		 * @return
		 */
		public boolean isValid(Identifier name);

		/**
		 * Check whether a given symbol as a declaration available within the target.
		 * Local symbols are always available. Non-local symbols may be available if
		 * they have been imported.
		 *
		 * @param name
		 * @return
		 */
		public boolean isAvailable(Identifier name);

		/**
		 * Get the concrete declarations associated with a given symbol
		 *
		 * @param name
		 * @return
		 */
		public List<Decl.Named> getRegisteredDeclarations(Identifier name);

		/**
		 * Get the available declarations associated with a given symbol
		 *
		 * @param name
		 * @return
		 */
		public List<Decl.Named> getAvailableDeclarations(Identifier name);
	}

	private static abstract class AbstractGroup<T extends Entry> implements Group {

		/**
		 * The list of known symbols associated with this group.
		 */
		protected final HashMap<Identifier, T> entries = new HashMap<>();

		@Override
		public boolean isAvailable(Identifier name) {
			if(entries.containsKey(name)) {
				return entries.get(name).isAvailable();
			} else {
				return false;
			}
		}

		@Override
		public boolean isValid(Identifier name) {
			return entries.containsKey(name);
		}

		@Override
		public List<Named> getRegisteredDeclarations(Identifier name) {
			if(entries.containsKey(name)) {
				return entries.get(name).getDeclarations();
			} else {
				return Collections.EMPTY_LIST;
			}
		}

		@Override
		public List<Named> getAvailableDeclarations(Identifier name) {
			if (entries.containsKey(name)) {
				return entries.get(name).getAvailable();
			} else {
				return Collections.EMPTY_LIST;
			}
		}
	}

	/**
	 * Record information associated with a given group of symbols (i.e. as located
	 * in same compilation unit).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class LocalGroup extends AbstractGroup<LocalEntry> {

		/**
		 * Construct a local group to represent a given unit in the target.
		 *
		 * @param unit
		 */
		public LocalGroup(Decl.Unit unit) {
			for(Decl d : unit.getDeclarations()) {
				if(d instanceof Decl.Named) {
					Decl.Named n = (Decl.Named) d;
					get(n.getName()).addAvailable(n);
				}
			}
		}

		// FIXME: we will need some methods for handling adding / removing declarations
		// as a result of incremental updates to the target.

		private LocalEntry get(Identifier name) {
			SymbolTable.LocalEntry r = entries.get(name);
			//
			if (r == null) {
				r = new SymbolTable.LocalEntry();
				entries.put(name, r);
			}
			return r;
		}
	}

	public class ExternalGroup extends AbstractGroup<ExternalEntry> {
		/**
		 * The external declaration that this group is associated with.
		 */
		private final Decl.Unit external;
		/**
		 * The available declaration representing this group in the target. This may be
		 * null if the unit has not yet been imported.
		 */
		private Decl.Unit available;

		/**
		 * Construct a non-local group to represent a given unit in the target.
		 *
		 * @param unit
		 */
		public ExternalGroup(Decl.Unit unit) {
			this.external = unit;
			//
			for(Decl d : unit.getDeclarations()) {
				if(d instanceof Decl.Named) {
					Decl.Named n = (Decl.Named) d;
					// Add public members only
					if(isPublic(n)) {
						get(n.getName()).addExternal(n);
					}
				}
			}
		}

		/**
		 * Register an available unit declared within the target and all symbols
		 * contained therein.
		 *
		 * @param declaration
		 */
		public void register(Decl.Unit available) {
			this.available = available;
			// Register as available all internal symbols
			for (Decl d : available.getDeclarations()) {
				if (d instanceof Decl.Named) {
					addAvailable((Decl.Named) d);
				}
			}
		}

		public void addAvailable(Decl.Named available) {
			get(available.getName()).addAvailable(available);
			if(this.available == null) {
				consolidations.add(this);
			}
		}

		/**
		 * Called when this group has available symbols but there is no enclosing unit
		 * is available in the target.
		 */
		public Decl.Unit consolidate() {
			Tuple<Decl> declarations = new Tuple<>(getAllAvailable());
			available = new Decl.Unit(external.getName(), declarations);
			return available;
		}

		private List<Decl> getAllAvailable() {
			ArrayList<Decl> available = new ArrayList<>();
			for(Map.Entry<Identifier, ExternalEntry> e : entries.entrySet()) {
				available.addAll(e.getValue().getAvailable());
			}
			return available;
		}

		private Entry get(Identifier name) {
			SymbolTable.ExternalEntry r = entries.get(name);
			//
			if (r == null) {
				r = new SymbolTable.ExternalEntry();
				entries.put(name, r);
			}
			return r;
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
	}


	/**
	 * A generic interface for describing symbols.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Entry {

		/**
		 * Check whether this entry is currently available in the target. Local symbols
		 * are always available. Non-local symbols may be available if they have been
		 * imported at some point.
		 *
		 * @return
		 */
		public boolean isAvailable();

		/**
		 * Get the available declarations for this symbol which in the target.
		 *
		 * @return
		 */
		public List<Decl.Named> getAvailable();

		/**
		 * Get the actual declarations for this symbol.
		 *
		 * @return
		 */
		public List<Decl.Named> getDeclarations();

		/**
		 * Add an available declaration for this symbol.
		 *
		 * @param decl
		 */
		public void addAvailable(Decl.Named decl);

		/**
		 * Add an external declaration for this symbol.
		 *
		 * @param decl
		 */
		public void addExternal(Decl.Named decl);
	}

	/**
	 * Represents an entry which is declared in the target file. This is the easy
	 * case as we never need to import local entries.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class LocalEntry implements Entry {
		/**
		 * Identifies the complete set of declarations associated with this symbol.
		 */
		private final ArrayList<Decl.Named> declarations;

		public LocalEntry() {
			this.declarations = new ArrayList<>();
		}

		@Override
		public boolean isAvailable() {
			return true;
		}

		@Override
		public List<Decl.Named> getAvailable() {
			return declarations;
		}

		@Override
		public List<Decl.Named> getDeclarations() {
			return declarations;
		}

		@Override
		public void addAvailable(Named decl) {
			declarations.add(decl);
		}

		@Override
		public void addExternal(Named decl) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Represents a symbol external to the given target. Such a symbol may have
	 * already been imported into the target as a stub.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class ExternalEntry implements Entry {
		/**
		 * Identifies available declarations for this symbol
		 */
		private final ArrayList<Decl.Named> availables = new ArrayList<>();

		/**
		 * Identifies external declarations for this symbol
		 */
		private final ArrayList<Decl.Named> externals = new ArrayList<>();

		@Override
		public boolean isAvailable() {
			return availables.size() > 0;
		}

		@Override
		public List<Named> getAvailable() {
			return availables;
		}

		@Override
		public List<Decl.Named> getDeclarations() {
			return externals;
		}

		@Override
		public void addAvailable(Named decl) {
			availables.add(decl);
		}

		@Override
		public void addExternal(Named decl) {
			externals.add(decl);
		}
	}
}
