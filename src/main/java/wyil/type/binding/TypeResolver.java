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
package wyil.type.binding;

import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.Bindable;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.SemanticType;
import wyil.lang.WyilFile.Type;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;

/**
 * <p>
 * Responsible for computing a "binding" between a function, method or property
 * declaration and a given set of concrete arguments types. For example,
 * consider:
 * </p>
 *
 * <pre>
 * template<T>
 * function get(T[] items, int i) -> T:
 *    return items[i]
 *
 *  function f(int[] items) -> int:
 *     return get(items,0)
 * </pre>
 *
 * <p>
 * At the point of the invocation for <code>get()</code> we must resolve the
 * declared type <code>function(T[],int)->(T)</code> against the declared
 * parameter types <code>(int[],int)</code>, yielding a binding
 * <code>T=int</code>.
 * </p>
 * <p>
 * Computing the binding between two types is non-trivial in Whiley. In addition
 * to template arguments (as above), we must handle lifetime arguments. For
 * example:
 * </p>
 *
 * <pre>
 * method <a> m(&a:int x) -> int:
 *    return *a
 *
 * ...
 *   &this:int ptr = new 1
 *   return m(ptr)
 * </pre>
 * <p>
 * At the invocation to <code>m()</code>, we need to infer the binding
 * <code>a=this</code>. A major challenge is the presence of union types. For
 * example, consider this binding problem:
 * </p>
 *
 * <pre>
 * template<S,T>
 * function f(S x, S|T y) -> S|T:
 *    return y
 *
 * function g(int p, bool|int q) -> (bool|int r):
 *    return f(p,q)
 * </pre>
 * <p>
 * At the invocation to <code>f</code> we must generate the binding
 * <code>S=int,T=bool</code>. When binding <code>bool|int</code> against
 * <code>S|T</code> we need to consider both cases where
 * <code>S=bool,T=int</code> and <code>S=int,T=bool</code>. Otherwise, we cannot
 * be sure to consider the right combination.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface TypeResolver {

	/**
	 * Bind a sequence of argument types against a given function, method or
	 * property declaration to produce a binding which maps type & lifetime
	 * variables to concrete types / lifetimes. Or, if not binding exists, return
	 * <code>null</code>.
	 *
	 * @param binding
	 *            Binding being resolved
	 * @param arguments
	 *            Argument types being used for inference
	 * @param environment
	 *            The enclosing lifetime relation
	 * @return
	 */
	public Type.Callable bind(Decl.Binding<Type.Callable,Decl.Callable> binding, Tuple<? extends SemanticType> arguments,
			LifetimeRelation environment);
}
