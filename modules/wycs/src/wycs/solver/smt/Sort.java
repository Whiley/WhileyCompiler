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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import wycc.util.Pair;

/**
 * A sort is the term for a type in the SMT domain. This class provides a few default sorts as well
 * as the ability to create generic sorts, such as {@link wycs.solver.smt.Sort.Set}s or {@link
 * wycs.solver.smt.Sort.Tuple}s. In order to utilise these generic sorts, it is required that the
 * initialisers (from {@link #generateInitialisers(Solver)} be added to the surrounding {@link
 * wycs.solver.smt.Block} or {@link wycs.solver.smt.Smt2File}.
 * <p/>
 * This design pattern is required as it is not possible to easily write custom theorems to add to
 * SMT solvers.
 *
 * @author Henry J. Wylde
 */
public abstract class Sort {

    /**
     * The singleton any sort.
     */
    public static final Sort.Any ANY = new Sort.Any();
    /**
     * The singleton boolean sort.
     */
    public static final Sort.Bool BOOL = new Sort.Bool();
    /**
     * The singleton integer sort.
     */
    public static final Sort.Int INT = new Sort.Int();
    /**
     * The singleton real sort.
     */
    public static final Sort.Real REAL = new Sort.Real();

    /**
     * This class can only be instantiated locally.
     */
    Sort() {}

    /**
     * Generates the required statements to use this generic sort. Will also add in extra utility
     * functions for working with the sort.
     * <p/>
     * This function takes a parameter, the solver, to generate the initialisers for. This allows a
     * sort to provide specialised initialisers depending on which solver is being used. For
     * example, Z3 provides a {@code map} function that can be used to efficiently define a set
     * union operation.
     *
     * @param solver the solver to generate the initialisers for.
     * @return the generated initialisation statements.
     */
    public abstract List<? extends Stmt> generateInitialisers();

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Any extends Sort {

        private Any() {}

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends Stmt> generateInitialisers() {
            List<Stmt> initialisers = new ArrayList<Stmt>();

            initialisers.addAll(generateSorts());

            return initialisers;
        }

        public String getName() {
            return "Any";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return getName();
        }

        private Collection<? extends Stmt> generateSorts() {
            return Arrays.asList(new Stmt.DeclareSort(getName(), 0));
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Array extends Sort {

        private final String index;
        private final String element;

        public Array(String index, String element) {
            if (index == null) {
                throw new NullPointerException("index cannot be null");
            }
            if (element == null) {
                throw new NullPointerException("element cannot be null");
            }

            this.index = index;
            this.element = element;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends Stmt> generateInitialisers() {
            return Collections.emptyList();
        }

        public String getName() {
            return "Array";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(" + getName() + " " + index + " " + element + ")";
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Bool extends Sort {

        private Bool() {}

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends Stmt> generateInitialisers() {
            return Collections.emptyList();
        }

        public String getName() {
            return "Bool";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return getName();
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Int extends Sort {

        private Int() {}

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends Stmt> generateInitialisers() {
            return Collections.emptyList();
        }

        public String getName() {
            return "Int";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return getName();
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Real extends Sort {

        private Real() {}

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends Stmt> generateInitialisers() {
            return Collections.emptyList();
        }

        public String getName() {
            return "Real";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return getName();
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Set extends Sort {

        public static final String FUN_ADD_NAME = "add";
        public static final String FUN_CONTAINS_NAME = "contains";
        public static final String FUN_EMPTY_NAME = "empty";
        public static final String FUN_LENGTH_NAME = "length";
        public static final String FUN_REMOVE_NAME = "remove";
        public static final String FUN_SUBSET_NAME = "subset";
        public static final String FUN_SUBSETEQ_NAME = "subseteq";

        private final String type;

        public Set(String type) {
            if (type == null) {
                throw new NullPointerException("type cannot be null");
            }

            this.type = type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<? extends Stmt> generateInitialisers() {
            List<Stmt> initialisers = new ArrayList<Stmt>();

            initialisers.addAll(generateSorts());
            initialisers.addAll(generateAddFunctions());
            initialisers.addAll(generateContainsFunctions());
            initialisers.addAll(generateRemoveFunctions());
            initialisers.addAll(generateEmptyConstants());
            initialisers.addAll(generateLengthFunctions());
            initialisers.addAll(generateEmptyLengthAssertions());
            initialisers.addAll(generateSubsetFunctions());
            // Causes lots of the tests to timeout
            //initialisers.addAll(generateSubsetLengthAssertions());

            return initialisers;
        }

        public String getEmptyNameAsQualified() {
            return "(as " + FUN_EMPTY_NAME + " " + toString() + ")";
        }

        /**
         * Gets the name of this set sort. The name is simply "Set".
         * <p/>
         * For the sort name to use in functions etc., use {@link #toString()}.
         *
         * @return the name of this set sort.
         */
        public String getName() {
            return "Set";
        }

        /**
         * Gets the name of this particular set sort, with it's type. The whole name is surrounded
         * by parenthesis.
         */
        @Override
        public String toString() {
            return "(" + getName() + " " + type + ")";
        }

        private List<? extends Stmt> generateAddFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
            parameters.add(new Pair<String, String>("set", toString()));
            parameters.add(new Pair<String, String>("t", type));
            String expr = "(store set t true)";

            return Arrays.asList(new Stmt.DefineFun(FUN_ADD_NAME, parameters, toString(), expr));
        }

        private List<? extends Stmt> generateContainsFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
            parameters.add(new Pair<String, String>("set", toString()));
            parameters.add(new Pair<String, String>("t", type));
            String expr = "(select set t)";

            return Arrays.asList(new Stmt.DefineFun(FUN_CONTAINS_NAME, parameters, BOOL.toString(),
                    expr));
        }

        private List<? extends Stmt> generateEmptyConstants() {
            List<Stmt> stmts = new ArrayList<Stmt>();

            stmts.add(new Stmt.DeclareFun(FUN_EMPTY_NAME, Collections.EMPTY_LIST, toString()));
            // The empty set does not contain any elements
            stmts.add(new Stmt.Assert(
                    "(not (exists ((t " + type + ")) (contains " + getEmptyNameAsQualified()
                            + " t)))"));

            return stmts;
        }

        private List<? extends Stmt> generateEmptyLengthAssertions() {
            return Arrays.asList(new Stmt.Assert(
                    "(= (length " + getEmptyNameAsQualified() + ") 0)"));
        }

        private List<? extends Stmt> generateLengthFunctions() {
            List<Stmt> stmts = new ArrayList<Stmt>();

            stmts.add(new Stmt.DeclareFun(FUN_LENGTH_NAME, Arrays.asList(toString()),
                    INT.toString()));
            // The length of all sets is a natural number
            stmts.add(new Stmt.Assert("(forall ((set " + toString() + ")) (<= 0 (length set)))"));
            // A recursive conjecture for determining the length of sets
            // Either a set is empty (and hence its length is 0) or:
            // There exists some element t, contained within the set, hence its length must be 1 +
            // the length of the set minus t
            // TODO: This conjecture really should be iff or xor (going both ways), however using
            // xor causes it to time out, so for now we use implication
            stmts.add(new Stmt.Assert("(forall ((set " + toString() + ")) (=> (distinct set "
                    + getEmptyNameAsQualified() + ") (exists ((t " + type
                    + ")) (and (contains set t) (= (length set) (+ 1 (length (remove set t))))))))"));
            // lines.add(new Stmt.Assert(
            // "(forall ((set " + toString() + ")) (xor (= set " + FUN_EMPTY_NAME
            // + ") (exists ((t " + type
            // + ")) (and (contains set t) (= (length set) (+ 1 (length (remove set t))))))))"
            // ));

            return stmts;
        }

        private List<? extends Stmt> generateRemoveFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
            parameters.add(new Pair<String, String>("set", toString()));
            parameters.add(new Pair<String, String>("t", type));
            String expr = "(store set t false)";

            return Arrays.asList(new Stmt.DefineFun(FUN_REMOVE_NAME, parameters, toString(), expr));
        }

        private List<? extends Stmt> generateSorts() {
            List<String> parameters = Arrays.asList("T");
            String expr = new Sort.Array("T", BOOL.toString()).toString();

            return Arrays.asList(new Stmt.DefineSort(getName(), parameters, expr));
        }

        private List<? extends Stmt> generateSubsetFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();
            parameters.add(new Pair<String, String>("first", toString()));
            parameters.add(new Pair<String, String>("second", toString()));

            String subseteqExpr;
            String subsetExpr;

            // There is a bug here, I'm not 100% sure how to properly use the map function
            /*case Z3:
                    // Z3 supports the map function

                    // SubsetEq can be seen as creating the union of A => B
                    // This union should contain all elements, so if there exists an element that
                    // isn't contained within it, then the subseqEq expression is false
                    subseteqExpr = "(forall ((t " + type
                            + ")) (contains ((_ map or) ((_ map not) first) second) t))";
                    subsetExpr = "(and (subseteq first second) (distinct first second))";

                    break;*/

            subseteqExpr = "(forall ((t " + type
            		+ ")) (=> (contains first t) (contains second t)))";
            subsetExpr = "(and (subseteq first second) (distinct first second))";

            List<Stmt> functions = new ArrayList<Stmt>();
            functions.add(new Stmt.DefineFun(FUN_SUBSETEQ_NAME, parameters, BOOL.toString(),
                    subseteqExpr));
            functions.add(new Stmt.DefineFun(FUN_SUBSET_NAME, parameters, BOOL.toString(),
                    subsetExpr));

            return functions;
        }

        private List<? extends Stmt> generateSubsetLengthAssertions() {
            List<Stmt> stmts = new ArrayList<Stmt>();

            // If a set is a proper subset of another, then its length must be less than the other's
            // length
            stmts.add(new Stmt.Assert("(forall ((set0 " + toString() + ") (set1 " + toString()
                    + ")) (=> (subset set0 set1) (< (length set0) (length set1))))"));
            // If a set subsets another, then its length must be less than or equal to the other's
            // length
            stmts.add(new Stmt.Assert("(forall ((set0 " + toString() + ") (set1 " + toString()
                    + ")) (=> (subseteq set0 set1) (<= (length set0) (length set1))))"));

            return stmts;
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Tuple extends Sort {

        public static final String FUN_GET_NAME = "get";

        private final List<String> types;

        public Tuple(String... types) {
            this(Arrays.asList(types));
        }

        public Tuple(List<String> types) {
            if (types.contains(null)) {
                throw new NullPointerException("types cannot contain null");
            }

            this.types = Collections.unmodifiableList(new ArrayList<String>(types));
        }

        public static String generateGetFunctionName(int index) {
            return FUN_GET_NAME + index;
        }

        /**
         * {@inheritDoc}
         */
        @Override

        public List<? extends Stmt> generateInitialisers() {
            List<Stmt> initialisers = new ArrayList<Stmt>();

            initialisers.addAll(generateSorts());
            initialisers.addAll(generateGetFunctions());
            initialisers.addAll(generateEqualityAssertions());

            return initialisers;
        }

        /**
         * Gets the name of this tuple sort. The name is equivalent to "Tuple" with the elements
         * size appended.
         * <p/>
         * For the sort name to use in functions etc., use {@link #toString()}.
         *
         * @return the name of this tuple sort.
         */
        public String getName() {
            return "Tuple" + types.size();
        }

        /**
         * Gets the name of this particular tuple sort, with it's types. The whole name is
         * surrounded by parenthesis.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            sb.append(getName());
            for (String type : types) {
                sb.append(" ").append(type);
            }
            sb.append(")");

            return sb.toString();
        }

        private List<? extends Stmt> generateEqualityAssertions() {
            List<Stmt> stmts = new ArrayList<Stmt>();

            // Two tuples are equal if and only if all of their elements are equal
            StringBuilder premise = new StringBuilder("(and");
            for (int i = 0; i < types.size(); i++) {
                premise.append(" (= ");
                premise.append("(");
                premise.append(generateGetFunctionName(i));
                premise.append(" tuple0");
                premise.append(") ");
                premise.append("(");
                premise.append(generateGetFunctionName(i));
                premise.append(" tuple1");
                premise.append(")");
                premise.append(")");
            }
            premise.append(")");
            stmts.add(new Stmt.Assert(
                    "(forall ((tuple0 " + toString() + ") (tuple1 " + toString() + ")) (xor "
                            + premise + " (distinct tuple0 tuple1)))"));

            return stmts;
        }

        private List<? extends Stmt> generateGetFunctions() {
            List<Stmt> stmts = new ArrayList<Stmt>();

            for (int i = 0; i < types.size(); i++) {
                stmts.add(new Stmt.DeclareFun(generateGetFunctionName(i), Arrays.asList(toString()),
                        types.get(i)));
            }

            return stmts;
        }

        private List<? extends Stmt> generateSorts() {
            return Arrays.asList(new Stmt.DeclareSort(getName(), types.size()));
        }
    }
}
