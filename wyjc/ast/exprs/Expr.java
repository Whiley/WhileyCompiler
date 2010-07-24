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

package wyjc.ast.exprs;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.types.Type;
import wyjc.lang.SyntacticElement;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.logic.*;

public interface Expr extends SyntacticElement {       
    /**
	 * This method substitutes all occurrences of a variable with the expression
	 * given in the binding. If no binding is given, the variable is left as is.
	 * 
	 * @param binding
	 * @return
	 */
    public Expr substitute(Map<String,Expr> binding);

    /**
	 * This method replaces all occurrences of an expression with the expression
	 * given in the binding. 
	 * 
	 * @param binding
	 * @return
	 */
    public Expr replace(Map<Expr,Expr> binding);

    /**
     * This method identifies the all matching occurrences of a class.
     * 
     * @return
     */
    public <T> List<T> match(Class<T> match);
    
    /**
     * This method identifies the names of all variables which are used in the
     * expression.
     * 
     * @return
     */
    public Set<Variable> uses();
    
    /**
     * This method reduces a given expression as much as possible. In some
     * circumstances, this will indeed produce a value.
     * @param environment TODO
     * 
     * @return
     */
    public Expr reduce(Map<String, Type> environment);  
    
    /**
	 * This method returns the type of value that this expression evaluates to.
	 * 
	 * @return
	 */
    public Type type(Map<String,Type> environment);
    
    /**
	 * This method converts a given value expression into a Rational (suitable
	 * for using in Wyone). The formula also produces a Formula, which is a
	 * condition that must hold true. This is needed for translating certain
	 * expressions which don't have immediate counterparts in the logic of
	 * Wyone. For example, the following expression:
	 * 
	 * <pre>
	 * {1, 2, 3}
	 * </pre>
	 * 
	 * Might be translated into something like this:
	 * 
	 * <pre>
	 *   $0, in(1,$0) &amp;&amp; in(2,$0) &amp;&amp; in(3,$0)
	 * </pre>
	 * 
	 * Observe that the Rational is a single variable, since Wyone has no direct
	 * support for sets.
	 * 
	 * @param environment
	 *            TODO
	 * @param loader
	 *            TODO
	 * @return
	 * @throws ResolveError
	 *             TODO
	 */
	public Pair<WExpr,WFormula> convert(
			Map<String, Type> environment, ModuleLoader loader)
			throws ResolveError;		
}


