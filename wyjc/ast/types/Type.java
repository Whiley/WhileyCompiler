// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjc.ast.types;

import wyjc.ast.exprs.Condition;
import wyjc.ast.types.unresolved.UnresolvedType;
import wyone.core.WExpr;
import wyone.core.WType;
import wyone.theory.logic.WFormula;

import java.util.*;

/**
 * This represents a primitive type in the language, such as "int" or "void".
 * 
 * @author djp
 */
public interface Type extends UnresolvedType {
	/**
	 * Determine whether a given type is a subtype of this type. Observe that
	 * this only considers the types themselves, not any constraints that may be
	 * imposed upon them (since we cannot reason about constraints here).
	 * Therefore, it's entirely possible that this will report two types are
	 * subtypes of each other when, in fact, they're not (if constraints are
	 * considered). For example, consider:
	 * 
	 * <pre>
	 * define nat as int where $ >= 0
	 * define pos as int where $ > 0
	 * </pre>
	 * 
	 * Now, this method will report that nat <: pos (since int <: int).
	 * 
	 * @param t
	 *            --- the type being tested to see whether it's a subtype of
	 *            this type.
	 * @param environment
	 *            --- this is used to propagate the types of internally
	 *            generated variables (i.e. arising from RecursiveType).
	 *            Initially, you should just supply Collections.EMPTY_MAP.
	 * @return
	 */
	public boolean isSubtype(Type t, Map<String, Type> environment);
	
	/**
	 * Strip off all named types.
	 */
	public Type flattern();
	
	/**
	 * Substitute matching type variables for new names 
	 * @param binding
	 * @return
	 */
	public Type substitute(Map<String, Type> binding);

	/**
	 * Return every subcomponent of this type which is an instanceof of the
	 * given class.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T> Set<T> match(Class<T> type);

	/**
	 * Determine whether or not this type contains an existential.
	 * 
	 * @return
	 */
	public boolean isExistential();

	/**
	 * Every type may have an optional constraint. If there is no constraint,
	 * this will be null. Within the constraint the special variable $ is used
	 * to refer to the variable of this type.
	 * 
	 * @return
	 */
	public Condition constraint();
	
	/**
	 * Convert a whiley type into a wyone type.
	 * @param target
	 * @return
	 */
	public WType convert();
}
