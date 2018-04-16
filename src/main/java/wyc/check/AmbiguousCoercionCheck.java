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
package wyc.check;

import java.util.ArrayList;
import java.util.Arrays;

import wybs.lang.CompilationUnit;
import wybs.lang.NameResolver;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.Type;
import wyc.task.CompileTask;
import wyc.util.AbstractTypedVisitor;
import wyc.util.AbstractTypedVisitor.Environment;
import wyil.type.subtyping.StrictTypeEmptinessTest;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;

public class AmbiguousCoercionCheck extends AbstractTypedVisitor {

	public AmbiguousCoercionCheck(CompileTask builder) {
		super(builder.getNameResolver(),
				new SubtypeOperator(builder.getNameResolver(), new StrictTypeEmptinessTest(builder.getNameResolver())));
	}

	public void check(WhileyFile file) {
		visitWhileyFile(file);
	}

	@Override
	public void visitExpression(Expr expr, Type target, Environment environment) {
		checkCoercion(expr, target, environment);
		// Finally, recursively continue exploring this expression
		super.visitExpression(expr, target, environment);
	}

	private void checkCoercion(Expr expr, Type target, Environment environment) {
		Type.Atom[] targets = expand(target);
		Type.Atom[] sources = expand(expr.getType());
		for(int i=0;i!=sources.length;++i) {
			Type match = selectCandidate(targets,sources[i],environment);
			if(match == null) {
				syntaxError("ambiguous coercion required (" + sources[i] + " to " + target + ")",expr);
			}
		}
	}

	private Type.Atom[] expand(Type type) {
		ArrayList<Type.Atom> atoms = new ArrayList<>();
		expand(type,atoms);
		return atoms.toArray(new Type.Atom[atoms.size()]);
	}

	private ArrayList<Type.Atom> expand(Type type, ArrayList<Type.Atom> atoms) {
		if (type instanceof Type.Atom) {
			atoms.add((Type.Atom) type);
		} else if (type instanceof Type.Union) {
			Type.Union t = (Type.Union) type;
			for(int i=0;i!=t.size();++i) {
				expand(t.get(i),atoms);
			}
		} else {
			try {
				Type.Nominal t = (Type.Nominal) type;
				Decl.Type decl = resolver.resolveExactly(t.getName(), Decl.Type.class);
				expand(decl.getType(),atoms);
			} catch (NameResolver.ResolutionError e) {
				throw new IllegalArgumentException("invalid type: " + type);
			}
		}
		return atoms;
	}

	private <T> T syntaxError(String msg, SyntacticItem e) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntaxError(msg, cu.getEntry(), e);
	}
}
