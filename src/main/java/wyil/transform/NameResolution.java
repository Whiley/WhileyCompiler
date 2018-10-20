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
import wybs.util.AbstractCompilationUnit;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.lang.SyntaxError;

import wyc.util.ErrorMessages;
import static wyc.util.ErrorMessages.*;
import wyil.lang.WyilFile;

import static wyil.lang.WyilFile.DECL_function;
import static wyil.lang.WyilFile.DECL_method;
import static wyil.lang.WyilFile.DECL_property;
import static wyil.lang.WyilFile.DECL_rectype;
import static wyil.lang.WyilFile.DECL_staticvar;
import static wyil.lang.WyilFile.DECL_type;
import static wyil.lang.WyilFile.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.Type;
import wyil.util.AbstractConsumer;
import wyil.util.AbstractVisitor;

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
	private final HashSet<String> names;

	public NameResolution(Build.Task builder) {
		this.names = new HashSet<>();
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
		System.out.println("GOT IMPORT: " + decl);
		super.visitImport(decl, imports);
		// Add this import statements to list of visible imports
		imports.add(decl);
	}

	@Override
	public void visitLambdaAccess(Expr.LambdaAccess expr, List<Decl.Import> imports) {
		super.visitExpression(expr, imports);
		System.out.println("VISIT LAMBDA ACCESS: " + expr);
	}

	@Override
	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, List<Decl.Import> imports) {
		super.visitExpression(expr, imports);
		System.out.println("VISIT STATIC VARIABLE ACCESS: " + expr);
	}

	@Override
	public void visitInvoke(Expr.Invoke expr, List<Decl.Import> imports) {
		super.visitExpression(expr, imports);
		System.out.println("VISIT INVOCATION: " + expr);
	}

	@Override
	public void visitTypeNominal(Type.Nominal type, List<Decl.Import> imports) {
		Name resolved = resolve(type.getName(), imports);
		System.out.println("VISIT NOMINAL: " + type + ", resolved as " + resolved);
	}

	/**
	 * Resolve a given name in a given compilation Unit. If the name is already
	 * fully qualified then this amounts to checking that the name exists;
	 * otherwise, we have to process the list of important statements for this
	 * compilation unit in an effort to qualify the name.
	 *
	 * @param name
	 *            The name to be resolved
	 * @param enclosing
	 *            The enclosing declaration in which this name is contained.
	 * @return
	 */
	private Name resolve(Name name, List<Decl.Import> imports) {
		Name resolved = name;
		if (!isQualified(name)) {
			// Name is not qualified, therefore attempt to qualify it.
			Decl.Unit unit = name.getAncestor(Decl.Unit.class);
			// Check whether local to enclosing compilation unit
			Name local = createQualifiedName(unit.getName().getPath(), name.get(0));
			if(exists(local)) {
				return local;
			}
			// Second, iterate each visible import statement (in reverse order)
			for (int i = imports.size() - 1; i >= 0; --i) {
				resolved = matchImport(imports.get(i), name.get(0));
				if (resolved != null && exists(resolved)) {
					return resolved;
				}
			}
		} else if (exists(resolved)) {
			// Name is qualified and exists.
			return resolved;
		}
		// Resolution error
		return syntaxError(errorMessage(ErrorMessages.RESOLUTION_ERROR, name.toString()), name);
	}

	/**
	 * Check whether a fully qualified name exists or not.
	 *
	 * @param name
	 * @return
	 */
	public boolean exists(Name name) {
		return names.contains(name.toString());
	}

	/**
	 * Determine whether a name is fully qualified or not. That is, whether or not
	 * the name is a partial name which must be completed, or is already a complete
	 * name.
	 *
	 * @param name
	 * @return
	 */
	private boolean isQualified(Name name) {
		return name.size() > 1;
	}

	/**
	 * Match a given import against a given partially or fully quantified name. For
	 * example, we might match <code>import wyal.lang.*</code> against the name
	 * <code>Test.f</code>. This would succeed if the package <code>wyal.lang</code>
	 * contained a module <code>Test</code> which in turn contained a named
	 * declaration <code>f</code>.
	 *
	 * @param imp
	 * @param name
	 * @return
	 */
	private Name matchImport(Decl.Import imp, Identifier name) {
		if (imp.hasFrom()) {
			Identifier from = imp.getFrom();
			if (from.get().equals("*") || name.equals(from)) {
				System.out.println("MATCHED IMPORT: " + imp);
				return createQualifiedName(imp.getPath(), name);
			}
		}
		// FIXME: we'll need more stuff here I think
		return null;
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
					Name resolved = createQualifiedName(uid.getPath(), n.getName());
					names.add(resolved.toString());
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

}
