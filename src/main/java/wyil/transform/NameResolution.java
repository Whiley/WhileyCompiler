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
package wyil.transform;

import wybs.lang.Build;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Ref;
import wybs.util.AbstractSyntacticHeap;

import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;
import wyfs.lang.Content;
import wyfs.lang.Path;

import wyil.lang.WyilFile;
import static wyil.lang.WyilFile.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.QualifiedName;
import wyil.lang.WyilFile.Type;
import wyil.util.AbstractConsumer;

/**
 * <p>
 * Responsible for resolving a name which occurs at some position in a
 * compilation unit. This takes into account the context and, if necessary, will
 * traverse important statements to resolve the query. For example, consider a
 * compilation unit entitled "file":
 * </p>
 *
 * <pre>
 * import std::ascii::*
 *
 * function f(T1 x, T2 y) -> (int r):
 *    return g(x,y)
 * </pre>
 *
 * <p>
 * Here the name "<code>g</code>" is not fully qualified. Depending on which
 * file the matching declaration of <code>g</code> occurs will depend on what
 * its fully qualified name is. For example, if <code>g</code> is declared in
 * the current compilation unit then it's fully quaified name would be
 * <code>test.g</code>. However, it could well be declared in a compilation unit
 * matching the import <code>whiley.lang.*</code>.
 * </p>
 *
 * <p>
 * An important challenge faced is the linking of external declarations. In
 * particular, such external declarations must be brought into the current
 * WyilFile. Such external declarations may themselves require additional
 * external declarations be imported and we must transitively import them all.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class NameResolution {
	private final WyilFile target;
	/**
	 * The resolver identifies unresolved names and produces patches based on them.
	 */
	private final Resolver resolver;

	private final SymbolTable symbolTable;

	private final Build.Project project;

	private boolean status = true;

	public NameResolution(Build.Project project, WyilFile target) throws IOException {
		this.project = project;
		this.target = target;
		this.symbolTable = new SymbolTable(target,getExternals());
		this.resolver = new Resolver();
	}

	/**
	 * Apply this name resolver to a given WyilFile.
	 *
	 * @param wf
	 */
	public boolean apply() {
		// FIXME: need to make this incremental
		// Create initial set of patches.
		List<Patch> patches = resolver.apply(target);
		// Keep iterating until all patches are resolved
		while (patches.size() > 0) {
			// Create importer
			Importer importer = new Importer(target, true);
			// Now continue importing until patches all resolved.
			for (int i = 0; i != patches.size(); ++i) {
				// Import and link the given patch
				patches.get(i).apply(importer);
			}
			// Switch over to the next set of patches
			patches = importer.getPatches();
		}
		// Consolidate any imported declarations as externals.
		symbolTable.consolidate();
		//
		return status;
	}

	/**
	 * Read in all external packages so they can be used for name resolution. This
	 * amounts to loading in every WyilFile contained within an external package
	 * dependency.
	 */
	private List<WyilFile> getExternals() throws IOException {
		ArrayList<WyilFile> externals = new ArrayList<>();
		List<Build.Package> pkgs = project.getPackages();
		// Consider each package in turn and identify all contained WyilFiles
		for (int i = 0; i != pkgs.size(); ++i) {
			Build.Package p = pkgs.get(i);
			// FIXME: This is kind broken me thinks. Potentially, we should be able to
			// figure out what modules are supplied via the configuration.
			List<Path.Entry<WyilFile>> entries = p.getRoot().get(Content.filter("**/*", WyilFile.ContentType));
			for (int j = 0; j != entries.size(); ++j) {
				externals.add(entries.get(j).read());
			}
		}
		return externals;
	}

	/**
	 * Responsible for identifying unresolved names which remain to be resolved to
	 * their corresponding declarations. This is achieved by traversing from a given
	 * declaration and identifying all static variables and callables which are
	 * referred to. In each case, a "patch" is created which identifies a location
	 * within the module which needs to be resolved. Whilst in some cases we could
	 * resolve immediately, for external symbols we cannot. Therefore, patches can
	 * be thought of as "lazy" resolution which works for both local and non-local
	 * names.
	 *
	 * @author David J. Pearce
	 *
	 */
	private class Resolver extends AbstractConsumer<List<Decl.Import>> {
		/**
		 * The list of patches being constructed
		 */
		private ArrayList<Patch> patches = new ArrayList<>();

		public List<Patch> apply(WyilFile module) {
			super.visitModule(module, null);
			return patches;
		}

		@Override
		public void visitUnit(Decl.Unit unit, List<Decl.Import> unused) {
			// Create an initially empty list of import statements.
			super.visitUnit(unit, new ArrayList<>());
		}

		@Override
		public void visitImport(Decl.Import decl, List<Decl.Import> imports) {
			super.visitImport(decl, imports);
			// Add this import statements to list of visible imports
			imports.add(decl);
		}

		@Override
		public void visitLambdaAccess(Expr.LambdaAccess expr, List<Decl.Import> imports) {
			super.visitLambdaAccess(expr, imports);
			// Resolve to qualified name
			QualifiedName name = resolveAs(expr.getName(), imports);
			// Sanity check result
			if(name != null) {
				// Create patch
				patches.add(new Patch(name, expr));
			}
		}

		@Override
		public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, List<Decl.Import> imports) {
			super.visitStaticVariableAccess(expr, imports);
			// Resolve to qualified name
			QualifiedName name = resolveAs(expr.getName(), imports);
			// Sanity check result
			if(name != null) {
				// Create patch
				patches.add(new Patch(name, expr));
			}
		}

		@Override
		public void visitInvoke(Expr.Invoke expr, List<Decl.Import> imports) {
			super.visitInvoke(expr, imports);
			// Resolve to qualified name
			QualifiedName name = resolveAs(expr.getName(), imports);
			// Sanity check result
			if(name != null) {
				// Create patch
				patches.add(new Patch(name, expr));
			}
		}

		@Override
		public void visitTypeNominal(Type.Nominal type, List<Decl.Import> imports) {
			super.visitTypeNominal(type, imports);
			// Resolve to qualified name
			QualifiedName name = resolveAs(type.getName(), imports);
			// Sanity check result
			if(name != null) {
				// Create patch
				patches.add(new Patch(name, type));
			}
		}

		/**
		 * Resolve a given name in a given compilation Unit to all corresponding
		 * (callable) declarations. If the name is already fully qualified then this
		 * amounts to checking that the name exists and finding its declaration(s);
		 * otherwise, we have to process the list of important statements for this
		 * compilation unit in an effort to qualify the name.
		 *
		 * @param name
		 *            The name to be resolved
		 * @param enclosing
		 *            The enclosing declaration in which this name is contained.
		 * @return
		 */
		private QualifiedName resolveAs(Name name, List<Decl.Import> imports) {
			// Resolve unqualified name to qualified name
			switch (name.size()) {
			case 1:
				return unqualifiedResolveAs(name.get(0), imports);
			case 2:
				return partialResolveAs(name.get(0), name.get(1), imports);
			default:
				return new QualifiedName(name.getPath(), name.getLast());
			}
		}

		/**
		 * Resolve a name which is completely unqualified (e.g. <code>to_string</code>).
		 * That is, it's just an identifier. This could be a name in the current unit,
		 * or an explicitly imported name/
		 *
		 * @param name
		 * @param imports
		 * @return
		 */
		private QualifiedName unqualifiedResolveAs(Identifier name, List<Decl.Import> imports) {
			// Attempt to local resolve
			Decl.Unit unit = name.getAncestor(Decl.Unit.class);
			QualifiedName localName = new QualifiedName(unit.getName(), name);
			if (symbolTable.contains(localName)) {
				// Yes, matching local name
				return localName;
			} else {
				// No, attempt to non-local resolve
				for (int i = imports.size() - 1; i >= 0; --i) {
					Decl.Import imp = imports.get(i);
					if (imp.hasFrom()) {
						// Resolving unqualified names requires "import from".
						Identifier from = imp.getFrom();
						if (from.get().equals("*") || name.equals(from)) {
							return new QualifiedName(imp.getPath(), name);
						}
					}
				}
				// No dice.
				syntaxError(name, RESOLUTION_ERROR);
				return null;
			}
		}

		/**
		 * Resolve a name which is partially qualified (e.g.
		 * <code>ascii::to_string</code>). This consists of an unqualified unit and a
		 * name.
		 *
		 * @param unit
		 * @param name
		 * @param kind
		 * @param imports
		 * @return
		 */
		private QualifiedName partialResolveAs(Identifier unit, Identifier name, List<Decl.Import> imports) {
			Decl.Unit enclosing = name.getAncestor(Decl.Unit.class);
			if (unit.equals(enclosing.getName().getLast())) {
				// A local lookup on the enclosing compilation unit.
				return unqualifiedResolveAs(name, imports);
			} else {
				for (int i = imports.size() - 1; i >= 0; --i) {
					Decl.Import imp = imports.get(i);
					Tuple<Identifier> path = imp.getPath();
					Identifier last = path.get(path.size() - 1);
					//
					if (!imp.hasFrom() && last.equals(unit)) {
						// Resolving partially qualified names requires no "from".
						QualifiedName qualified = new QualifiedName(path, name);
						if (symbolTable.contains(qualified)) {
							return qualified;
						}
					}
				}
				// No dice.
				syntaxError(name, RESOLUTION_ERROR);
				return null;
			}
		}
	}

	/**
	 * Records information about a name which needs to be "patched" with its
	 * corresponding declaration in a given expression.
	 *
	 * @author David J. Pearce
	 *
	 */
	public class Patch {
		public final QualifiedName name;
		private final SyntacticItem target;

		public Patch(QualifiedName name, SyntacticItem target) {
			if(name == null || target == null) {
				throw new IllegalArgumentException("name cannot be null");
			}
			this.name = name;
			this.target = target;
		}

		/**
		 * Import and link a given patch. The import process is only necessary for
		 * external symbols. For these, their declarations must be imported lazily as
		 * stubs from the relevant declaration.
		 *
		 * @param importer
		 *            --- The importer to use for importing.
		 */
		public void apply(Importer importer) {
			// Import
			imPort(importer);
			// And, link.
			link();
		}

		private void imPort(Importer importer) {
			// Import external declarations as necessary
			if (!symbolTable.isAvailable(name)) {
				// FIXME: want to optimise so don't bring in the whole type unless we are doing
				// link-time analysis or generating a single binary.
				ArrayList<Decl.Named> imported = new ArrayList<>();
				for (Decl.Named d : symbolTable.getRegisteredDeclarations(name)) {
					imported.add((Decl.Named) importer.allocate(d));
				}
				symbolTable.addAvailable(name, imported);
			}
		}

		/**
		 * Connect the given syntactic item with its target declaration (which may have
		 * just been imported).
		 */
		private void link() {
			// Apply patch to target expression
			switch (target.getOpcode()) {
			case EXPR_staticvariable: {
				Expr.StaticVariableAccess e = (Expr.StaticVariableAccess) target;
				Decl.StaticVariable d = select(name, Decl.StaticVariable.class);
				if(d != null) {
					e.setDeclaration(d);
				}
				break;
			}
			case EXPR_invoke: {
				Expr.Invoke e = (Expr.Invoke) target;
				Decl.Callable[] resolved = selectAll(name, Decl.Callable.class);
				if(resolved != null) {
					e.setDeclarations(resolved);
				}
				break;
			}
			case EXPR_lambdaaccess: {
				Expr.LambdaAccess e = (Expr.LambdaAccess) target;
				Decl.Callable[] resolved = selectAll(name, Decl.Callable.class);
				if(resolved != null) {
					e.setDeclarations(filterParameters(e.getParameterTypes().size(), resolved));
				}
				break;
			}
			default:
			case TYPE_nominal: {
				Type.Nominal e = (Type.Nominal) target;
				Decl.Type d = select(name, Decl.Type.class);
				if(d != null) {
					e.setDeclaration(d);
				}
				break;
			}
			}
		}

		/**
		 * Resolve a name which is fully qualified (e.g.
		 * <code>std::ascii::to_string</code>) to first matching declaration. This
		 * consists of a qualified unit and a name.
		 *
		 * @param name
		 *            Fully qualified name
		 * @param kind
		 *            Declaration kind we are resolving.
		 * @return
		 */
		private <T extends Decl> T select(QualifiedName name, Class<T> kind) {
			List<Decl.Named> declarations = symbolTable.getAvailableDeclarations(name);
			Identifier id = name.getName();
			for (int i = 0; i != declarations.size(); ++i) {
				Decl.Named d = declarations.get(i);
				if (kind.isInstance(d)) {
					return (T) d;
				}
			}
			syntaxError(id, RESOLUTION_ERROR);
			return null;
		}

		/**
		 * Resolve a name which is fully qualified (e.g.
		 * <code>std::ascii::to_string</code>) to all matching declarations. This
		 * consists of a qualified unit and a name.
		 *
		 * @param name
		 *            Fully qualified name
		 * @param kind
		 *            Declaration kind we are resolving.
		 * @return
		 */
		private <T extends Decl> T[] selectAll(QualifiedName name, Class<T> kind) {
			List<Decl.Named> declarations = symbolTable.getAvailableDeclarations(name);
			Identifier id = name.getName();
			// Determine how many matches
			int count = 0;
			for (int i = 0; i != declarations.size(); ++i) {
				Decl.Named d = declarations.get(i);
				if (kind.isInstance(d)) {
					count++;
				}
			}
			// Create the array
			@SuppressWarnings("unchecked")
			T[] matches = (T[]) Array.newInstance(kind, count);
			// Populate the array
			for (int i = 0, j = 0; i != declarations.size(); ++i) {
				Decl.Named d = declarations.get(i);
				if (kind.isInstance(d)) {
					matches[j++] = (T) d;
				}
			}
			// Check for resolution error
			if (matches.length == 0) {
				syntaxError(id, RESOLUTION_ERROR);
				return null;
			} else {
				return matches;
			}
		}

		/**
		 * Filter the given callable declarations based on their parameter count.
		 *
		 * @param parameters
		 * @param resolved
		 * @return
		 */
		private Decl.Callable[] filterParameters(int parameters, Decl.Callable[] resolved) {
			// Remove any with incorrect number of parameters
			for (int i = 0; i != resolved.length; ++i) {
				Decl.Callable c = resolved[i];
				if (parameters > 0 && c.getParameters().size() != parameters) {
					resolved[i] = null;
				}
			}
			return ArrayUtils.removeAll(resolved, null);
		}

		@Override
		public String toString() {
			return "<" + name + "," + target + ">";
		}
	}

	private static Ref<Decl> REF_UNKNOWN_DECL = new Ref(new Decl.Unknown());

	/**
	 * Responsible for importing an item from one syntactic heap into another. This
	 * relies on all items externally referenced by the imported item having been
	 * already resolved.
	 *
	 * @author David J. Pearce
	 *
	 */
	private class Importer extends AbstractSyntacticHeap.Allocator {
		/**
		 * Signals whether or not to only import stubs.
		 */
		private final boolean stubsOnly;
		/**
		 * List of patches generated during imports by this importer.
		 */
		private final ArrayList<Patch> patches = new ArrayList<>();

		private final SyntacticItem REF_UNKNOWN;

		public Importer(AbstractSyntacticHeap heap, boolean stubsOnly) {
			super(heap);
			this.stubsOnly = stubsOnly;
			this.REF_UNKNOWN = super.allocate(REF_UNKNOWN_DECL);
		}

		@Override
		public SyntacticItem allocate(SyntacticItem item) {

			switch (item.getOpcode()) {
			case ITEM_ref:
				Ref<?> ref = (Ref<?>) item;
				SyntacticItem referent = ref.get();
				if (referent.getHeap() != heap && referent instanceof Decl.Named) {
					// This is a deference to a named declaration in a different module. This will
					// need to be patched.
					return REF_UNKNOWN;
				} else {
					return super.allocate(item);
				}
			case DECL_function:
			case DECL_method: {
				if (stubsOnly) {
					// drop the body from the function, making it a stub
					return allocateStub((Decl.FunctionOrMethod) item);
				} else {
					return super.allocate(item);
				}
			}
			// The linkable items require patching
			case EXPR_staticvariable:
			case EXPR_invoke:
			case EXPR_lambdaaccess:
			case TYPE_nominal: {
				Linkable l = (Linkable) item;
				item = super.allocate(item);
				// Register patch
				patches.add(new Patch(l.getDeclaration().getQualifiedName(), item));
				// Done
				return item;
			}
			default:
				return super.allocate(item);
			}
			//

		}

		public List<Patch> getPatches() {
			return patches;
		}

		/**
		 * Allocate a stub into the underlying heap.
		 *
		 * @param fm
		 * @return
		 */
		private SyntacticItem allocateStub(Decl.FunctionOrMethod fm) {
			SyntacticItem item = map.get(fm);
			if (item != null) {
				// item is already allocated as a stub, therefore must return that.
				return item;
			} else if (fm instanceof Decl.Function) {
				// Create function stub
				Decl.Function f = (Decl.Function) fm;
				item = new Decl.Function(f.getModifiers(), f.getName(), f.getParameters(), f.getReturns(),
						f.getRequires(), f.getEnsures(), new Stmt.Block());
			} else {
				// Create method stub
				Decl.Method m = (Decl.Method) fm;
				item = new Decl.Method(m.getModifiers(), m.getName(), m.getParameters(), m.getReturns(),
						m.getRequires(), m.getEnsures(), new Stmt.Block(), m.getLifetimes());
			}
			// Allocate new item using underlying allocator. This will recursively allocate
			// child nodes.
			item = super.allocate(item);
			// Store item in map to ensure we don't allocate it more than once.
			map.put(fm, item);
			// Done
			return item;
		}
	}

	private void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context);
	}
}
