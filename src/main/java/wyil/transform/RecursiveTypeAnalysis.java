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
import wyil.lang.WyilFile;
import wyil.util.AbstractConsumer;
import wyc.task.CompileTask;

import static wyil.lang.WyilFile.*;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * A simple analysis of types to identify which are recurisve and which are not.
 * This works by traversing all nominal types in a given Whiley file and check
 * which lead back to themselves. For example:
 * </p>
 *
 * <pre>
 * type nat is (int x) where x >= 0
 *
 * type List is null|{int data, List next}
 * </pre>
 *
 * <p>
 * In traversing the type <code>nat</code>, we go into its definition and reach
 * the terminal type <code>int</code>. Thus, <code>nat</code> is not considered
 * a recursive type. However, in traversing the type <code>List</code> things
 * are more complex. In this case, we get to its definition
 * <code>null|{int data, List next}</code> and we then traverse down all paths
 * of the union. On the first path, we reach the terminal type <code>null</code>
 * and that traversal ends. On the second path, we reach
 * <code>{int data, List next}</code> and, again, traverse all paths within.
 * Eventually, we traverse the type <code>List</code> which has been encountered
 * before. Hence, <code>List</code> is hereafter marked as a recursive type.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class RecursiveTypeAnalysis extends AbstractConsumer<Set<QualifiedName>> implements Build.Stage<WyilFile> {

	@Override
	public void apply(WyilFile module) {
		visitModule(module, new HashSet<>());
	}

	// ===========================================================================
	// DECLARATIONS
	// ===========================================================================

	@Override
	public void visitType(Decl.Type decl, Set<QualifiedName> visited) {
		QualifiedName name = decl.getQualifiedName();
		if (visited.contains(name)) {
			// recursive type detected!!!
			decl.setRecursive();
		} else {
			visited.add(name);
			visitType(decl.getType(), visited);
			visited.remove(name);
		}
	}

	// ===========================================================================
	// STATEMENTS
	// ===========================================================================

	public void visitStatement(Decl.Type t, Set<Name> visited) {
		// do nothing
	}

	// ===========================================================================
	// EXPRESSIONS
	// ===========================================================================

	public void visitExpression(Decl.Type t, Set<Name> visited) {
		// do nothing
	}

	// ===========================================================================
	// TYPES
	// ===========================================================================

	@Override
	public void visitTypeNominal(Type.Nominal type, Set<QualifiedName> visited) {
		// Extract the declaration to which this type refers.
		Decl.Type decl = type.getDeclaration();
		// Recursively traverse it.
		visitType(decl, visited);
	}
}
