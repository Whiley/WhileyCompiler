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
package wyil.stage;

import wybs.lang.Build;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wyc.util.AbstractConsumer;
import wyc.lang.WhileyFile;
import wyc.task.CompileTask;

import static wyc.lang.WhileyFile.*;

import java.util.HashSet;
import java.util.Set;

public class RecursiveTypeAnalysis extends AbstractConsumer<Set<Name>> implements Build.Stage<WhileyFile> {
  private final NameResolver resolver;

	public RecursiveTypeAnalysis(CompileTask builder) {
	  resolver = builder.getNameResolver();
	}

	@Override
	public void apply(WhileyFile module) {
		visitWhileyFile(module, new HashSet<>());
	}

	// ===========================================================================
	// DECLARATIONS
	// ===========================================================================

	@Override
	public void visitType(Decl.Type decl, Set<Name> visited) {
		Name name = decl.getQualifiedName();
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
	public void visitTypeNominal(Type.Nominal type, Set<Name> visited) {
		try {
			Decl.Type decl = resolver.resolveExactly(type.getName(), WhileyFile.Decl.Type.class);
			visitType(decl, visited);
		} catch (ResolutionError e) {
			throw new IllegalArgumentException("invalid nominal type: " + type);
		}
	}
}
