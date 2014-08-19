// Copyright (c) 2014, Henry J. Wylde (hjwylde@gmail.com)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wycs.solver.smt;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a block of statements. A block is surrounded by a {@link wycs.solver.smt.Stmt.Push}
 * and {@link wycs.solver.smt.Stmt.Pop} statement in order to have any declarations and assertions
 * localised to itself.
 *
 * @author Henry J. Wylde
 */
public final class Block implements Element {

    /**
     * A list of elements in this block. Uses a {@link java.util.LinkedHashSet} as the container to
     * remove any duplicate {@link wycs.solver.smt.Stmt.DeclareFun}, {@link
     * wycs.solver.smt.Stmt.DeclareSort}, {@link wycs.solver.smt.Stmt.DefineFun}, {@link
     * wycs.solver.smt.Stmt.DefineSort} and {@link wycs.solver.smt.Stmt.Assert}s. Doing this means
     * we don't have to manually check for their existence and ensures they are uniquely defined.
     */
    private final Set<Element> elements = new LinkedHashSet<Element>();

    /**
     * Appends the given elements to this block.
     *
     * @param elements the elements to add.
     */
    public void append(Element... elements) {
        append(Arrays.asList(elements));
    }

    /**
     * Appends the given elements to this block.
     *
     * @param elements the elements to add.
     */
    public void append(Collection<? extends Element> elements) {
        if (elements.contains(null)) {
            throw new NullPointerException("elements cannot contain null");
        }

        this.elements.addAll(elements);
    }

    /**
     * Clears this block, removing all elements.
     */
    public void clear() {
        elements.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (elements.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(new Stmt.Push(1));
        sb.append("\n");
        for (Element element : elements) {
            sb.append(element);
            sb.append("\n");
        }
        sb.append(new Stmt.Pop(1));
        sb.append("\n");

        return sb.toString();
    }
}
