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

/**
 * Utility class for constants representing the response of a SMT solver.
 *
 * @author Henry J. Wylde
 */
public final class Response {

    /**
     * Result is satisfiable; there is one or more models that make the assertions true.
     */
    public static final String SAT = "sat";
    /**
     * Result is unsatisfiable: there is one or more models that make the assertions false.
     */
    public static final String UNSAT = "unsat";
    /**
     * Result is unknown: may have timed out or was unable to come to a conclusion about the
     * assertions.
     */
    public static final String UNKNOWN = "unknown";
    /**
     * Response says unsupported: a solver may return this is a {@link
     * wycs.solver.smt.Stmt.SetOption} is not supported by that solver.
     */
    public static final String UNSUPPORTED = "unsupported";

    /**
     * This class cannot be instantiated.
     */
    private Response() {}
}
