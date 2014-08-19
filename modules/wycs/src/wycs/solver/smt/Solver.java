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
import java.util.Collections;
import java.util.List;

/**
 * Enumeration of the external solvers supported by the {@link wycs.transforms.SmtVerificationCheck}
 * transform.
 *
 * @author Henry J. Wylde
 */
public enum Solver {

    /**
     * CVC4 solver.
     * <p/>
     * Requires the "incremental" argument to allow usage of {@link wycs.solver.smt.Stmt.Push} and
     * {@link wycs.solver.smt.Stmt.Pop}.
     * <p/>
     * CVC4 is not recommended as it does not support overloading functions which is currently
     * required by the implementations of the {@link wycs.solver.smt.Sort.Set} and {@link
     * wycs.solver.smt.Sort.Tuple} encodings.
     */
    CVC4("--incremental"),
    /**
     * Z3 solver.
     */
    Z3;

    /**
     * A list of extra arguments the solver should be called with.
     */
    private List<String> args;

    /**
     * Creates a new {@code Solver} with the given extra arguments.
     *
     * @param args the extra arguments.
     */
    private Solver(String... args) {
        this.args = Arrays.asList(args);
    }

    /**
     * Gets the extra arguments this solver should be called with.
     *
     * @return the extra arguments.
     */
    public List<String> getArgs() {
        return Collections.unmodifiableList(args);
    }
}
