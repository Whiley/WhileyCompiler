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

import wybs.lang.*;
import wybs.util.AbstractCompilationUnit;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Ref;
import wybs.util.AbstractSyntacticHeap;

import wyc.util.ErrorMessages;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.ArrayUtils;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.*;

import static wyil.lang.WyilFile.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
	private final Build.Meter meter;
	private final WyilFile target;
	/**
	 * The resolver identifies unresolved names and produces patches based on them.
	 */
	private final Resolver resolver;

	private final SymbolTable symbolTable;

	private final List<Build.Package> packages;

	private boolean status = true;

	public NameResolution(Build.Meter meter, Build.Project project, WyilFile target) throws IOException {
		this(meter, project.getPackages(), target);
	}

	public NameResolution(Build.Meter meter, List<Build.Package> packages, WyilFile target) throws IOException {
		this.meter = meter.fork(NameResolution.class.getSimpleName());
		this.packages = packages;
		this.target = target;
		this.symbolTable = new SymbolTable(target,getExternals());
		this.resolver = new Resolver(meter);
	}
	
	/**
	 * Apply this name resolver to a given WyilFile.
	 *
	 * @param wf
	 */
	public boolean apply() {
		// FIXME: need to make this incremental
		checkImports(meter,target);
		// Create initial set of patches.
		List<Patch> patches = resolver.apply(target);
		// Keep iterating until all patches are resolved
		while (patches.size() > 0) {
			// Create importer
			Importer importer = new Importer(meter, target, true);
			// Now continue importing until patches all resolved.
			for (int i = 0; i != patches.size(); ++i) {
				// Import and link the given patch
				patches.get(i).apply(importer);
			}
			// Switch over to the next set of patches
			patches = importer.getPatches();
		}
		// Consolidate any imported declarations as externals.
		symbolTable.consolidate(meter);
		//
		meter.done();
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
		// Consider each package in turn and identify all contained WyilFiles
		for (int i = 0; i != packages.size(); ++i) {
			Build.Package p = packages.get(i);
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
	 * Sanity check that import statements make sense. Specifically. that: (1) the
	 * module being imported from exists; (2) any names being imported exist.
	 *
	 * @param target
	 */
	private void checkImports(Build.Meter meter, WyilFile target) {
		for(Decl.Unit unit : target.getModule().getUnits()) {
			for(Decl d : unit.getDeclarations()) {
				meter.step("check");
				if(d instanceof Decl.Import) {
					// Found one to check!
					Decl.Import imp = (Decl.Import) d;
					Tuple<Identifier> path = imp.getPath();
					// Sanity check module exists
					Name name = new Name(path.toArray(Identifier.class));
					// Sanity check imported module
					if(!name.toString().contains("*") && !symbolTable.contains(name)) {
						// Cannot identify name
						syntaxError(path.get(path.size()-1), RESOLUTION_ERROR);
					} else if(imp.hasFrom() || imp.hasWith()) {
						Tuple<Identifier> names = imp.getNames();
						for (int i = 0; i != names.size(); ++i) {
							Identifier id = names.get(i);
							if (!id.get().equals("*") && !symbolTable.contains(new QualifiedName(name, id))) {
								// Sanity check imported names (if applicable)
								syntaxError(id, RESOLUTION_ERROR);
							}
						}
					}
				}
			}
		}
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
		/**
		 * Cache of previously resolved names.
		 */
		private HashMap<Name,QualifiedName> cache = new HashMap<>();
		/**
		 * Used to indicate when resolving something which is exposed (and, hence,
		 * relevant members should themselves be exposed).
		 */
		private boolean isVisible = false;

		public Resolver(Build.Meter meter) {
			super(meter);
		}

		public List<Patch> apply(WyilFile module) {
			super.visitModule(module, null);
			return patches;
		}

		@Override
		public void visitUnit(Decl.Unit unit, List<Decl.Import> unused) {
			// Reset cache as this is necessary when moving between compilation units!
			cache.clear();
			// Create an initially empty list of import statements.
			super.visitUnit(unit, new ArrayList<>());
		}

		@Override
		public void visitExternalUnit(Decl.Unit unit, List<Decl.Import> unused) {
			// NOTE: we override this to prevent unnecessarily traversing units
		}

		@Override
		public void visitType(Decl.Type decl, List<Decl.Import> unused) {
			isVisible = isPublic(decl);
			super.visitType(decl, unused);
			isVisible = false;
		}

		@Override
		public void visitProperty(Decl.Property decl, List<Decl.Import> unused) {
			isVisible = isPublic(decl);
			super.visitProperty(decl, unused);
			isVisible = false;
		}

		@Override
		public void visitFunction(Decl.Function decl, List<Decl.Import> unused) {
			isVisible = isPublic(decl);
			visitVariables(decl.getParameters(), unused);
			visitVariables(decl.getReturns(), unused);
			visitExpressions(decl.getRequires(), unused);
			visitExpressions(decl.getEnsures(), unused);
			isVisible = false;
			visitStatement(decl.getBody(), unused);
		}

		@Override
		public void visitMethod(Decl.Method decl, List<Decl.Import> unused) {
			isVisible = isPublic(decl);
			visitVariables(decl.getParameters(), unused);
			visitVariables(decl.getReturns(), unused);
			visitExpressions(decl.getRequires(), unused);
			visitExpressions(decl.getEnsures(), unused);
			isVisible = false;
			visitStatement(decl.getBody(), unused);
		}

		@Override
		public void visitImport(Decl.Import decl, List<Decl.Import> imports) {
			// Reset cache as an import statement can have non-trivial effects. Ideally, it
			// would be nice to incrementally update the cache.
			cache.clear();
			//
			super.visitImport(decl, imports);
			// Add this import statements to list of visible imports
			imports.add(decl);
		}

		@Override
		public void visitLambdaAccess(Expr.LambdaAccess expr, List<Decl.Import> imports) {
			super.visitLambdaAccess(expr, imports);
			// Resolve to qualified name
			QualifiedName name = resolveAs(expr.getLink(), imports);
			// Sanity check result
			if(name != null) {
				// Create patch
				patches.add(new Patch(isVisible, name, null, expr));
			}
		}

		@Override
		public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, List<Decl.Import> imports) {
			super.visitStaticVariableAccess(expr, imports);
			// Resolve to qualified name
			QualifiedName name = resolveAs(expr.getLink(), imports);
			// Sanity check result
			if(name != null) {
				// Create patch
				patches.add(new Patch(isVisible, name, null, expr));
			}
		}

		@Override
		public void visitInvoke(Expr.Invoke expr, List<Decl.Import> imports) {
			super.visitInvoke(expr, imports);
			// Resolve to qualified name
			QualifiedName name = resolveAs(expr.getLink(), imports);
			// Sanity check result
			if(name != null) {
				// Create patch
				patches.add(new Patch(isVisible, name, null, expr));
			}
		}

		@Override
		public void visitTypeNominal(Type.Nominal type, List<Decl.Import> imports) {
			super.visitTypeNominal(type, imports);
			// Resolve to qualified name
			QualifiedName name = resolveAs(type.getLink(), imports);
			// Sanity check result
			if (name != null) {
				// Create patch
				patches.add(new Patch(isVisible, name, null, type));
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
		private QualifiedName resolveAs(Decl.Link<?> link, List<Decl.Import> imports) {
			QualifiedName r;
			Name name = link.getName();
			QualifiedName qualified = cache.get(name);
			if(qualified != null) {
				// Have previously resolved this particular name in this particular context.
				return qualified;
			} else {
				// Resolve unqualified name to qualified name
				switch (name.size()) {
				case 1:
					qualified = unqualifiedResolveAs(name.get(0), imports);
					break;
				case 2:
					qualified = partialResolveAs(name.get(0), name.get(1), imports);
					break;
				default:
					// NOTE: don't put these into cache as not necessary!
					return new QualifiedName(name.getPath(), name.getLast());
				}
				// Cache resolution for performance
				cache.put(name, qualified);
				// Done
				return qualified;
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
					if (imp.hasFrom() || imp.hasWith()) {
						Tuple<Identifier> names = imp.getNames();
						for (int j = 0; j != names.size(); ++j) {
							Identifier with = names.get(j);
							if (with.get().equals("*") || name.equals(with)) {
								return new QualifiedName(imp.getPath(), name);
							}
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
		public final boolean isPublic;
		public final QualifiedName name;
		public final Type type;
		private final SyntacticItem target;

		public Patch(boolean isPublic, QualifiedName name, Type type, SyntacticItem target) {
			if(name == null || target == null) {
				throw new IllegalArgumentException("name cannot be null");
			}
			this.isPublic = isPublic;
			this.name = name;
			this.type = type;
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
					// Sanity check import
					imported.add((Decl.Named) importer.allocate(d));
				}
				symbolTable.addAvailable(name, imported);
			} else {
				for (Decl.Named d : symbolTable.getRegisteredDeclarations(name)) {
					// Sanity check local declarations
					if(isPublic && !isPublic(d)) {
						syntaxError(target,EXPOSING_HIDDEN_DECLARATION);
					}
				}
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
					e.getLink().resolve(d);
				}
				break;
			}
			case EXPR_invoke: {
				Expr.Invoke e = (Expr.Invoke) target;
				Decl.Link<Decl.Callable> link = e.getLink();
				if(type != null) {
					// do nothing?
					Decl.Callable resolved = select(name, type, Decl.Callable.class);
					if (resolved != null) {
						e.getLink().resolve(resolved);
					}
				} else {
					Decl.Callable[] resolved = selectAll(name, Decl.Callable.class);
					if (resolved != null) {
						e.getLink().resolve(resolved);
					}
				}
				break;
			}
			case EXPR_lambdaaccess: {
				Expr.LambdaAccess e = (Expr.LambdaAccess) target;
				Decl.Callable[] resolved = selectAll(name, Decl.Callable.class);
				if(resolved != null) {
					e.getLink().resolve(filterParameters(e.getParameterTypes().size(), resolved));
				}
				break;
			}
			default:
			case TYPE_nominal: {
				Type.Nominal e = (Type.Nominal) target;
				Decl.Type d = select(name, Decl.Type.class);
				if(d != null && e.getParameters().size() != d.getTemplate().size()) {
					if(e.getParameters().size() > d.getTemplate().size()) {
						syntaxError(e.getLink().getName(), TOOMANY_TEMPLATE_PARAMETERS);
					} else {
						syntaxError(e.getLink().getName(), MISSING_TEMPLATE_PARAMETERS);
					}
				} else if(d != null){
					e.getLink().resolve(d);
				} else {
					// NOTE: if we get here, then we have a public member referring to a hidden
					// member.  For now, this is prohibited further upstream.
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
				//
				if (kind.isInstance(d)) {
					// Found direct instance
					return (T) d;
				}
			}
			syntaxError(id, RESOLUTION_ERROR);
			return null;
		}

		/**
		 * Resolve a name which is fully qualified (e.g.
		 * <code>std::array::equals</code>) and includes a distinguishing type signature.
		 *
		 * @param name
		 *            Fully qualified name
		 * @param kind
		 *            Declaration kind we are resolving.
		 * @return
		 */
		private <T extends Decl> T select(QualifiedName name, Type type, Class<T> kind) {
			List<Decl.Named> declarations = symbolTable.getAvailableDeclarations(name);
			Identifier id = name.getName();
			for (int i = 0; i != declarations.size(); ++i) {
				Decl.Named d = declarations.get(i);
				//
				if (kind.isInstance(d) && d.getType().equals(type)) {
					// Found direct instance
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
		private final Build.Meter meter;
		/**
		 * Signals whether or not to only import stubs.
		 */
		private final boolean stubsOnly;
		/**
		 * List of patches generated during imports by this importer.
		 */
		private final ArrayList<Patch> patches = new ArrayList<>();

		private final SyntacticItem REF_UNKNOWN;

		public Importer(Build.Meter meter, AbstractSyntacticHeap heap, boolean stubsOnly) {
			super(heap);
			this.meter = meter;
			this.stubsOnly = stubsOnly;
			this.REF_UNKNOWN = super.allocate(REF_UNKNOWN_DECL);
		}

		@Override
		public SyntacticItem allocate(SyntacticItem item) {
			meter.step("import");
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
				Linkable linkable = (Linkable) item;
				Decl.Link<? extends Decl.Named> link = linkable.getLink();
				item = super.allocate(item);
				// Register patch
				patches.add(new Patch(false, link.getTarget().getQualifiedName(), link.getTarget().getType(), item));
				// Done
				return item;
			}
			case DECL_property:
				// NOTE: properties are included here because we always import the full "body".
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
			}
			Tuple<Template.Variable> template = (Tuple<Template.Variable>) super.allocate(fm.getTemplate());
			Tuple<Decl.Variable> params = (Tuple<Decl.Variable>) super.allocate(fm.getParameters());
			Tuple<Decl.Variable> returns = (Tuple<Decl.Variable>) super.allocate(fm.getReturns());
			Tuple<Expr> requires = (Tuple<Expr>) super.allocate(fm.getRequires());
			Tuple<Expr> ensures = (Tuple<Expr>) super.allocate(fm.getEnsures());
			if (fm instanceof Decl.Function) {
				// Create function stub
				Decl.Function f = (Decl.Function) fm;
				item = new Decl.Function(f.getModifiers(), f.getName(), template, params, returns,
						requires, ensures, new Stmt.Block());
			} else {
				// Create method stub
				Decl.Method m = (Decl.Method) fm;
				item = new Decl.Method(m.getModifiers(), m.getName(), template, params,
						returns, requires, ensures, new Stmt.Block());
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

	/**
	 * Check whether named declaration is public or not.
	 *
	 * @param decl
	 * @return
	 */
	private boolean isPublic(Decl.Named decl) {
		return decl.getModifiers().match(Modifier.Public.class) != null;
	}

	private void syntaxError(SyntacticItem e, int code, SyntacticItem... context) {
		status = false;
		ErrorMessages.syntaxError(e, code, context);
	}
}
