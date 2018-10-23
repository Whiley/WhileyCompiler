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
import wybs.lang.CompilationUnit;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.lang.SyntaxError;

import wyc.util.ErrorMessages;
import wycc.util.ArrayUtils;

import static wyc.util.ErrorMessages.*;
import wyil.lang.WyilFile;

import static wyil.lang.WyilFile.Tuple;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.Type;
import wyil.util.AbstractConsumer;

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
public class NameResolution extends AbstractConsumer<List<Decl.Import>> {
	/**
	 * The master list of names and their corresponding records. For each name, this
	 * records whether we have a local declaration, a non-local declaration (which
	 * may or may not have been imported), etc.
	 */
	private final HashMap<Name, Record> names;

	public NameResolution(Build.Task builder) {
		this.names = new HashMap<>();
	}

	public void apply(WyilFile module) {
		updateUniversalNames(module.getModule());
		super.visitModule(module, null);
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
		Decl.Callable[] resolved = resolveAll(expr.getName(), Decl.Callable.class, imports);
		int parameters = expr.getParameterTypes().size();
		// Remove any with incorrect number of parameters
		for(int i=0;i!=resolved.length;++i) {
			Decl.Callable c = resolved[i];
			if(parameters > 0 && c.getParameters().size() != parameters) {
				resolved[i] = null;
			}
		}
		resolved = ArrayUtils.removeAll(resolved, null);
		// Bind the resolved declarations
		expr.setDeclarations(resolved);
	}

	@Override
	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, List<Decl.Import> imports) {
		super.visitStaticVariableAccess(expr, imports);
		Decl.StaticVariable resolved = resolveAs(expr.getName(), Decl.StaticVariable.class, imports);
		// Bind the resolved declaration
		expr.setDeclaration(resolved);
	}

	@Override
	public void visitInvoke(Expr.Invoke expr, List<Decl.Import> imports) {
		super.visitInvoke(expr, imports);
		Decl.Callable[] resolved = resolveAll(expr.getName(), Decl.Callable.class, imports);
		// Remove any with incorrect number of parameters
		for(int i=0;i!=resolved.length;++i) {
			Decl.Callable c = resolved[i];
			if(c.getParameters().size() != expr.getOperands().size()) {
				resolved[i] = null;
			}
		}
		resolved = ArrayUtils.removeAll(resolved, null);
		// Bind the resolved declarations
		expr.setDeclarations(resolved);
	}

	@Override
	public void visitTypeNominal(Type.Nominal type, List<Decl.Import> imports) {
		Decl.Type resolved = resolveAs(type.getName(), Decl.Type.class, imports);
		// Bind the resolved declaration
		type.setDeclaration(resolved);
	}

	/**
	 * Resolve a given name in a given compilation Unit to its corresponding
	 * declaration. If the name is already fully qualified then this amounts to
	 * checking that the name exists and finding its declaration; otherwise, we have
	 * to process the list of important statements for this compilation unit in an
	 * effort to qualify the name.
	 *
	 * @param name
	 *            The name to be resolved
	 * @param enclosing
	 *            The enclosing declaration in which this name is contained.
	 * @return
	 */
	private <T extends Decl> T resolveAs(Name name, Class<T> kind, List<Decl.Import> imports) {
		switch (name.size()) {
		case 1:
			name = unqualifiedResolveAs(name.get(0), imports);
			break;
		case 2:
			name = partialResolveAs(name.get(0), name.get(1), imports);
			break;
		}
		return select(name, kind);
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
	private <T extends Decl> T[] resolveAll(Name name, Class<T> kind, List<Decl.Import> imports) {
		switch (name.size()) {
		case 1:
			name = unqualifiedResolveAs(name.get(0), imports);
			break;
		case 2:
			name = partialResolveAs(name.get(0), name.get(1), imports);
			break;
		}
		return selectAll(name, kind);
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
	private Name unqualifiedResolveAs(Identifier name, List<Decl.Import> imports) {
		// Attempt to local resolve
		Decl.Unit unit = name.getAncestor(Decl.Unit.class);
		Name localName = createQualifiedName(unit.getName().getAll(), name);
		if (names.containsKey(localName)) {
			// Yes, matching local name
			return localName;
		} else {
			// No, attempt to non-local resolve
			for (int i = imports.size() - 1; i >= 0; ++i) {
				Decl.Import imp = imports.get(i);
				if (imp.hasFrom()) {
					// Resolving unqualified names requires "import from".
					Identifier from = imp.getFrom();
					if (from.get().equals("*") || name.equals(from)) {
						return createQualifiedName(imp.getPath(), name);
					}
				}
			}
			// No dice.
			return syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, name.toString()), name);
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
	private Name partialResolveAs(Identifier unit, Identifier name, List<Decl.Import> imports) {
		for (int i = imports.size() - 1; i >= 0; ++i) {
			Decl.Import imp = imports.get(i);
			Tuple<Identifier> path = imp.getPath();
			Identifier last = path.get(path.size() - 1);
			//
			if (!imp.hasFrom() && last.equals(unit)) {
				// Resolving partially qualified names requires no "from".
				Name qualified = createQualifiedName(path, name);
				if (names.containsKey(qualified)) {
					return qualified;
				}
			}
		}
		// No dice.
		return syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, name.toString()), name);
	}

	/**
	 * Resolve a name which is fully qualified (e.g.
	 * <code>std::ascii::to_string</code>) to a single declaration. This consists of
	 * a qualified unit and a name.
	 *
	 * @param name
	 *            Fully qualified name
	 * @param kind
	 *            Declaration kind we are resolving.
	 * @return
	 */
	private <T extends Decl> T select(Name name, Class<T> kind) {
		Record r = names.get(name);
		for (int i = 0; i != r.declarations.size(); ++i) {
			Decl.Named d = r.declarations.get(i);
			if (kind.isInstance(d)) {
				return (T) d;
			}
		}
		// Resolution error
		return syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, name.toString()), name);
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
	private <T extends Decl> T[] selectAll(Name name, Class<T> kind) {
		Record r = names.get(name);
		// Determine how many matches
		int count = 0;
		for (int i = 0; i != r.declarations.size(); ++i) {
			Decl.Named d = r.declarations.get(i);
			if (kind.isInstance(d)) {
				count++;
			}
		}
		// Create the array
		@SuppressWarnings("unchecked")
		T[] matches = (T[]) Array.newInstance(kind, count);
		// Populate the array
		for (int i = 0, j = 0; i != r.declarations.size(); ++i) {
			Decl.Named d = r.declarations.get(i);
			if (kind.isInstance(d)) {
				matches[j++] = (T) d;
			}
		}
		// Check for resolution error
		if (matches.length == 0) {
			return syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, name.toString()), name);
		} else {
			return matches;
		}
	}

	/**
	 * Update the universal list of names. This is used to determine whether a name
	 * is valid or not.
	 */
	private void updateUniversalNames(Decl.Module module) {
		// FIXME: this method is completely broken for so many reasons.
		for (Decl.Unit unit : module.getUnits()) {
			Name uid = unit.getName();
			for (Decl d : unit.getDeclarations()) {
				if (d instanceof Decl.Named) {
					Decl.Named n = (Decl.Named) d;
					Name resolved = createQualifiedName(uid.getAll(), n.getName());
					register(resolved, n);
				}
			}
		}
	}

	private Name createQualifiedName(Tuple<Identifier> path, Identifier name) {
		Identifier[] ids = new Identifier[path.size() + 1];
		for (int i = 0; i != path.size(); ++i) {
			ids[i] = path.get(i);
		}
		ids[path.size()] = name;
		return new Name(ids);
	}

	private Name createQualifiedName(Identifier[] path, Identifier name) {
		Identifier[] ids = new Identifier[path.length + 1];
		for (int i = 0; i != path.length; ++i) {
			ids[i] = path[i];
		}
		ids[path.length] = name;
		return new Name(ids);
	}

	/**
	 * Register a new declaration with a given name.
	 *
	 * @param name
	 * @param declaration
	 */
	private void register(Name name, Decl.Named declaration) {
		Record r = names.get(name);
		if (r == null) {
			r = new Record();
			names.put(name, r);
		}
		// Sanity check whether overloading is valid
		checkValidOverloading(r.declarations, declaration);
		// Add the declaration
		r.declarations.add(declaration);
	}

	/**
	 * Sanity check that there overloading is used correctly. More specifically, we
	 * cannot overload on types or static variables. Furthermore, overloading of
	 * methods or functions is permitted in some situations (i.e. when signatures
	 * vary).
	 *
	 * @param declarations
	 * @param kind
	 */
	private void checkValidOverloading(List<Decl.Named> declarations, Decl.Named declaration) {
		if (declaration instanceof Decl.Type && contains(declarations, Decl.Type.class)) {
			syntaxError("duplicate type declaration", declaration.getName());
		} else if (declaration instanceof Decl.StaticVariable && contains(declarations, Decl.StaticVariable.class)) {
			syntaxError("duplicate type declaration", declaration.getName());
		}
	}

	private <T extends Decl> boolean contains(List<Decl.Named> declarations, Class<T> kind) {
		for (int i = 0; i != declarations.size(); ++i) {
			if (kind.isInstance(declarations.get(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Throw an syntax error.
	 *
	 * @param msg
	 * @param e
	 * @return
	 */
	private <T> T syntaxError(String msg, SyntacticItem e) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntaxError(msg, cu.getEntry(), e);
	}

	/**
	 * Records information associated with a given name.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class Record {
		public final ArrayList<Decl.Named> declarations;

		public Record() {
			this.declarations = new ArrayList<>();
		}
	}
}
