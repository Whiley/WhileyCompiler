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
import wyjc.ast.attrs.SyntacticElement;
import wyjc.ast.types.BoolType;
import wyjc.ast.types.Type;
import wyjc.util.*;
import wyone.core.WEnvironment;
import wyone.core.WExpr;
import wyone.theory.logic.WFormula;

public interface Condition extends Expr, SyntacticElement {
    /**
	 * This method substitutes all occurrences of a variable with the expression
	 * given in the binding. If no binding is given, the variable is left as is.
	 * 
	 * @param binding
	 * @return
	 */
    public Condition substitute(Map<String,Expr> binding);
          
    /**
     * This method reduces a given expression as much as possible. In some
     * circumstances, this will indeed produce a value.
     * 
     * @return
     */
    public Condition reduce(Map<String, Type> environment);

    public BoolType type(Map<String,Type> environment);

    /**
     * This method converts a given condition into a pair of formulas (suitable
     * for using in Wyone). The first formula represents the translation of this
     * condition, whilst the second formula represents any implied constraints
     * which must hold true. This is needed for translating certain expressions
     * which don't have immediate counterparts in the logic of Wyone. For
     * example, the following condition:
     * 
     * <pre>
     *    x in {1, 2, 3}
     * </pre>
     * 
     * Might be translated into something like this:
     * 
     * <pre>
     *       in(x,$0), in(1,$0) &amp;&amp; in(2,$0) &amp;&amp; in(3,$0)
     * </pre>
     * 
     * Thus, the first formula represents the true translation, whilst the
     * remainder are the necessary constraints involved. We must distinguish
     * these two parts correctly, otherwise certain logical operations (e.g.
     * not) will yield incorrect results. For example, translating:
     * 
     * <pre>
     *    !(x in {1, 2, 3})
     * </pre>
     * 
     * should yield something like:
     * 
     * <pre>
     *       !in(x,$0), in(1,$0) &amp;&amp; in(2,$0) &amp;&amp; in(3,$0)
     * </pre>
     * 
     * rather than:
     * 
     * <pre>
     * !in(x, $0) || !in(1, $0) || !in(2, $0) || !in(3, $0)
     * </pre>
     * @param environment TODO
     * @param loader TODO
     * @return
     * @throws ResolveError TODO
     */
	public Triple<WFormula, WFormula, WEnvironment> convertCondition(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError;	
}

