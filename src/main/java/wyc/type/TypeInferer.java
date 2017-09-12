// Copyright 2017 David J. Pearce
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
package wyc.type;

import java.util.Set;

import static wyc.lang.WhileyFile.*;
import wybs.lang.NameResolver.ResolutionError;

/**
 * Responsible for inferring the type for a given expression. More specifically,
 * a conservative type which includes every value this expression could generate
 * in a given environment. For example, consider the expression
 * <code>x+1</code>. In an environment where <code>x</code> has type
 * <code>int</code> a reasonable type to infer would be <code>int</code>.
 * However, that is not the only possible type. For example, a very coarse
 * approximation would be <code>any</code>.
 *
 * @author David J. Pearce
 *
 */
public interface TypeInferer {

	/**
	 * Get the type inferred for a given expression in a given environment.
	 *
	 * @param environment
	 *            Environment to be used when resolving the type of a variable
	 * @param expression
	 *            The expression whose type is to be infered.
	 * @return
	 * @throws ResolutionError
	 *             Occurs when a particular named type cannot be resolved.
	 */
	public Type getInferredType(Expr expression) throws ResolutionError;

	/**
	 * The typing environment determines the current type for all variables at a
	 * given point. Observe that the type given to a variable may differ from
	 * its declared type in certain contexts. For example, support variable
	 * <code>x</code> is declared to have type <code>int|null</code>. Then, in a
	 * specific context we may have <code>x is int</code> and, in which case, we
	 * can refine the known type for <code>x</code> accodingly.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Environment {
		/**
		 * Return the current type associated with a given variable. It is
		 * generally assumed that this either matches its declared type, or is a
		 * refinement thereof.
		 *
		 * @param var
		 * @return
		 */
		Type getType(Decl.Variable var);

		/**
		 * Refine the type associated with a given variable declaration in this
		 * environment, which produces a completely new environment containing
		 * the refined type. Observe that the variable need not have been
		 * previously declared for this to make sense. Also, observe that the
		 * resulting type for a given variable is the intersection of its
		 * original type and the refined type.
		 *
		 * @param var
		 * @param type
		 * @return
		 */
		Environment refineType(Decl.Variable var, Type type);

		Set<Decl.Variable> getRefinedVariables();
	}
}
