// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.checks;

import java.util.*;

import static wyil.util.ErrorMessages.*;
import wybs.lang.Attribute;
import wybs.lang.Build;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.ResolveError;
import wycc.util.Pair;
import wyil.lang.*;
import wyil.util.TypeSystem;

/**
 * <p>
 * The point of the coercion check is to check that all convert bytecodes make
 * sense, and are not ambiguous. For example, consider the following code:
 * </p>
 *
 * <pre>
 * define Rec1 as { real x, int y }
 * define Rec2 as { int x, real y }
 * define uRec1Rec2 as Rec1 | Rec2
 *
 * int f(uRec1Rec2 r):
 *  if r is Rec1:
 *      return r.y
 *  else:
 *      return r.x
 *
 * int g():
 *  rec = { x: 1, y: 1}
 *  return f(rec)
 * </pre>
 *
 * <p>
 * An implicit coercion will be inserted just before the last statement in
 * <code>g()</code>. This will be:
 * </p>
 *
 * <pre>
 * convert {int x,int y} => {real x,int y}|{int x,real y}
 * </pre>
 * <p>
 * However, this conversion is <i>ambiguous</i> because we could convert the
 * left-hand side to either of the two options in the right-hand side.
 * </p>
 *
 * @author David J. Pearce
 */
public class CoercionCheck implements Build.Stage<WyilFile> {
	private WyilFile file;
	private final TypeSystem typeSystem;

	public CoercionCheck(Build.Task builder) {
		this.typeSystem = new TypeSystem(builder.project());
	}

	@Override
	public void apply(WyilFile module) {
		this.file = module;

		for(WyilFile.Type type : module.types()) {
			check(type.getTree());
		}
		for(WyilFile.FunctionOrMethod method : module.functionOrMethods()) {
			check(method.getTree());
		}
	}

	protected void check(SyntaxTree tree) {
		// Examine all entries in this block looking for a conversion bytecode
		List<SyntaxTree.Location<?>> expressions = tree.getLocations();
		for (int i = 0; i != expressions.size(); ++i) {
			SyntaxTree.Location<?> l = expressions.get(i);
			if (l.getBytecode() instanceof Bytecode.Expr) {
				Bytecode.Expr e = (Bytecode.Expr) l.getBytecode();
				if (e instanceof Bytecode.Convert) {
					Bytecode.Convert c = (Bytecode.Convert) e;
					// FIXME: need to fix this :)
					// check(conv.type(0), c.type(), new HashSet<Pair<Type,
					// Type>>(), e.attribute(SourceLocation.class));
				}
			}
		}
	}

	/**
	 * Recursively check that there is no ambiguity in coercing type from into
	 * type to. The visited set is necessary to ensure this process terminates
	 * in the presence of recursive types.
	 *
	 * @param from
	 * @param to
	 * @param visited
	 *            - the set of pairs already checked.
	 * @param location
	 *            - source location attribute (if applicable).
	 * @throws ResolveError
	 *             If a named type within this condition cannot be resolved
	 *             within the enclosing project.
	 */
	protected void check(Type from, Type to, HashSet<Pair<Type, Type>> visited, SyntacticElement element) throws ResolveError {
		Pair<Type,Type> p = new Pair<Type,Type>(from,to);
		if(visited.contains(p)) {
			return; // already checked this pair
		} else {
			visited.add(p);
		}
		if(from == Type.T_VOID) {
			// also no problem
		} else if(from instanceof Type.Leaf && to instanceof Type.Leaf) {
			// no problem
		} else if(from instanceof Type.Reference && to instanceof Type.Reference) {
			Type.Reference t1 = (Type.Reference) from;
			Type.Reference t2 = (Type.Reference) to;
			check(t1.element(),t2.element(),visited,element);
		} else if(from instanceof Type.Array && to instanceof Type.Array) {
			Type.Array t1 = (Type.Array) from;
			Type.Array t2 = (Type.Array) to;
			check(t1.element(),t2.element(),visited,element);
		} else if(from instanceof Type.Record && to instanceof Type.Record) {
			Type.Record t1 = (Type.Record) from;
			Type.Record t2 = (Type.Record) to;
			String[] fields = t1.getFieldNames();
			for(String s : fields) {
				Type e1 = t1.getField(s);
				Type e2 = t2.getField(s);
				check(e1,e2,visited,element);
			}
		} else if(from instanceof Type.Function && to instanceof Type.Function) {
			Type.Function t1 = (Type.Function) from;
			Type.Function t2 = (Type.Function) to;
			check(t1.params(),t2.params(),visited,element);
			check(t1.returns(),t2.returns(),visited,element);
		} else if(from instanceof Type.Union) {
			Type.Union t1 = (Type.Union) from;
			for(Type b : t1.bounds()) {
				check(b,to,visited,element);
			}
		} else if(to instanceof Type.Union) {
			Type.Union t2 = (Type.Union) to;

			// First, check for identical type (i.e. no coercion necessary)

			for(Type b : t2.bounds()) {
				if(from.equals(b)) {
					// no problem
					return;
				}
			}

			// Second, check for single non-coercive match
			Type match = null;

			for(Type b : t2.bounds()) {
				if(typeSystem.isSubtype(b,from)) {
					if(match != null) {
						// found ambiguity
						throw new SyntaxError(errorMessage(AMBIGUOUS_COERCION,from,to), file.getEntry(), element);
					} else {
						check(from,b,visited,element);
						match = b;
					}
				}
			}

			if(match != null) {
				// ok, we have a hit on a non-coercive subtype.
				return;
			}

			// Third, test for single coercive match

			for(Type b : t2.bounds()) {
				if(typeSystem.isExplicitCoerciveSubtype(b,from)) {
					if(match != null) {
						// found ambiguity
						throw new SyntaxError("ambiguous coercion (" + from + " => " + to, file.getEntry(), element);
					} else {
						check(from,b,visited,element);
						match = b;
					}
				}
			}
		}
	}

	private void check(Type[] params1, Type[] params2, HashSet<Pair<Type, Type>> visited,
			SyntacticElement element) throws ResolveError {
		for (int i = 0; i != params1.length; ++i) {
			Type e1 = params1[i];
			Type e2 = params2[i];
			check(e1, e2, visited, element);
		}
	}
}
