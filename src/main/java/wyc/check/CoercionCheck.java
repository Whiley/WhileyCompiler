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

import java.util.*;

import wybs.lang.Build;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.lang.WhileyFile;
import wyc.type.TypeSystem;
import wycc.util.Pair;
import static wyc.lang.WhileyFile.*;

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
public class CoercionCheck implements Build.Stage<WhileyFile> {
	private WhileyFile file;
	private final TypeSystem typeSystem;

	public CoercionCheck(Build.Task builder) {
		this.typeSystem = new TypeSystem(builder.project());
	}

	@Override
	public void apply(WhileyFile module) {
		this.file = module;

		// Examine all entries in this block looking for a conversion bytecode
		for (int i = 0; i != module.size(); ++i) {
			SyntacticItem item = module.getSyntacticItem(i);
			if (item instanceof Expr) {
				Expr e = (Expr) item;
				if (e instanceof Expr.Cast) {
					Expr.Cast c = (Expr.Cast) e;
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
	protected void check(Type from, Type to, HashSet<Pair<Type, Type>> visited, SyntacticElement element)  {
		Pair<Type,Type> p = new Pair<>(from,to);
		if(visited.contains(p)) {
			return; // already checked this pair
		} else {
			visited.add(p);
		}
		if(from == Type.Void) {
			// also no problem
		} else if(from instanceof Type.Atom && to instanceof Type.Atom) {
			// no problem
		} else if(from instanceof Type.Reference && to instanceof Type.Reference) {
			Type.Reference t1 = (Type.Reference) from;
			Type.Reference t2 = (Type.Reference) to;
			check(t1.getElement(),t2.getElement(),visited,element);
		} else if(from instanceof Type.Array && to instanceof Type.Array) {
			Type.Array t1 = (Type.Array) from;
			Type.Array t2 = (Type.Array) to;
			check(t1.getElement(),t2.getElement(),visited,element);
		} else if(from instanceof Type.Record && to instanceof Type.Record) {
			Type.Record t1 = (Type.Record) from;
			Type.Record t2 = (Type.Record) to;
			Tuple<Decl.Variable> t1fields = t1.getFields();
			Tuple<Decl.Variable> t2fields = t1.getFields();
			throw new RuntimeException("FIX ME");
		} else if(from instanceof Type.Function && to instanceof Type.Function) {
			Type.Function t1 = (Type.Function) from;
			Type.Function t2 = (Type.Function) to;
			check(t1.getParameters(),t2.getParameters(),visited,element);
			check(t1.getReturns(),t2.getReturns(),visited,element);
		} else if(from instanceof Type.Union) {
			Type.Union t1 = (Type.Union) from;
			for(int i=0;i!=t1.size();++i) {
				check(t1.get(i),to,visited,element);
			}
		} else if(to instanceof Type.Union) {
			Type.Union t2 = (Type.Union) to;

			// First, check for identical type (i.e. no coercion necessary)

			for(int i=0;i!=t2.size();++i) {
				if(from.equals(t2.get(i))) {
					// no problem
					return;
				}
			}

			// Second, check for single non-coercive match
			Type match = null;

			for(int i=0;i!=t2.size();++i) {
//				if(typeSystem.isSubtype(b,from)) {
//					if(match != null) {
//						// found ambiguity
//						throw new SyntaxError(errorMessage(AMBIGUOUS_COERCION,from,to), file.getEntry(), element);
//					} else {
//						check(from,b,visited,element);
//						match = b;
//					}
//				}
			}

			if(match != null) {
				// ok, we have a hit on a non-coercive subtype.
				return;
			}

			// Third, test for single coercive match

			for(int i=0;i!=t2.size();++i) {
//				if(typeSystem.isExplicitCoerciveSubtype(b,from)) {
//					if(match != null) {
//						// found ambiguity
//						throw new SyntaxError("ambiguous coercion (" + from + " => " + to, file.getEntry(), element);
//					} else {
//						check(from,b,visited,element);
//						match = b;
//					}
//				}
			}
		}
	}

	private void check(Tuple<Type> params1, Tuple<Type> params2, HashSet<Pair<Type, Type>> visited,
			SyntacticElement element) {
		for (int i = 0; i != params1.size(); ++i) {
			Type e1 = params1.get(i);
			Type e2 = params2.get(i);
			check(e1, e2, visited, element);
		}
	}
}
