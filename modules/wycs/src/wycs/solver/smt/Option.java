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
 * A utility class for some options provided by some of the solvers specified in {@link
 * wycs.solver.smt.Solver}. These options are used in a {@link wycs.solver.smt.Stmt.SetOption}
 * statement.
 *
 * @author Henry J. Wylde
 */
public final class Option {

    /**
     * Boolean. Sets whether the solver should print out "success" for each statement executed.
     */
    public static final String PRINT_SUCCESS = ":print-success";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should automatically
     * configurate itself.
     */
    public static final String Z3_AUTO_CONFIG = ":auto-config";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should automatically
     * configurate itself.
     */
    public static final String Z3_SMT_AUTO_CONFIG = ":smt.auto-config";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should pull nested
     * quantifiers out to the front of an expression.
     */
    public static final String Z3_PULL_NESTED_QUANTIFIERS = ":pull-nested-quantifiers";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should pull nested
     * quantifiers out to the front of an expression.
     */
    public static final String Z3_SMT_PULL_NESTED_QUANTIFIERS = ":smt.pull-nested-quantifiers";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should use its model based
     * quantifier instantiation method for resolution.
     */
    public static final String Z3_MBQI = ":mbqi";
    /**
     * Boolean. {@link wycs.solver.smt.Solver#Z3} option. Sets whether Z3 should use its model based
     * quantifier instantiation method for resolution.
     */
    public static final String Z3_SMT_MBQI = ":smt.mbqi";

    /**
     * This class may not be instantiated.
     */
    private Option() {}
}
