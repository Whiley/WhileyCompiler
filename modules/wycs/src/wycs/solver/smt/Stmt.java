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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import wycc.util.Pair;

/**
 * A statement in the SMT-LIB v2 language specification. The statements here are useful for
 * enforcing some structure on the generation of a {@link wycs.solver.smt.Smt2File}. Note that each
 * statement generally only takes {@link java.lang.String}s as parameters, so it is up to the user
 * of this class to ensure the arguments are well-formed.
 *
 * @author Henry J. Wylde
 */
public abstract class Stmt implements Element {

    /**
     * This class may only be instantiated locally.
     */
    Stmt() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

    /**
     * Computes a hash code of the given objects. The hash code is just the integer exclusive-or
     * operation on the hash code of all the objects.
     *
     * @param objects the objects to hash.
     * @return the hash code.
     */
    private static int Objects_hash(Object... objects) {
        int hash = 0;
        for (Object obj : objects) {
            hash ^= obj.hashCode();
        }

        return hash;
    }

    /**
     * An assertion statement. Used to state facts and conjectures within the SMT file.
     *
     * @author Henry J. Wylde
     */
    public static final class Assert extends Stmt {

        private final String expr;

        /**
         * Creates a new {@code Assert} statement with the given expression.
         *
         * @param expr the expression.
         */
        public Assert(String expr) {
            if (expr == null) {
                throw new NullPointerException("expr cannot be null");
            }

            this.expr = expr;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            return expr.equals(((Assert) obj).expr);
        }

        /**
         * Gets the expression.
         *
         * @return the expression.
         */
        public String getExpr() {
            return expr;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return expr.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(assert " + expr + ")";
        }
    }

    /**
     * A check satisfiable statement. This statement makes the SMT solver check whether the current
     * list of assertions is satisfiable. It's response is one of {@value
     * wycs.solver.smt.Response#SAT}, {@value wycs.solver.smt.Response#UNSAT} or {@value
     * wycs.solver.smt.Response#UNKNOWN}.
     *
     * @author Henry J. Wylde
     */
    public static final class CheckSat extends Stmt {

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(check-sat)";
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class DeclareFun extends Stmt {

        private final String name;
        private final List<String> parameterSorts;
        private final String returnSort;

        public DeclareFun(String name, List<String> parameterSorts, String returnSort) {
            if (name == null) {
                throw new NullPointerException("name cannot be null");
            }
            if (parameterSorts.contains(null)) {
                throw new NullPointerException("parameterSorts cannot contain null");
            }
            if (returnSort == null) {
                throw new NullPointerException("returnSort cannot be null");
            }

            this.name = name;
            this.returnSort = returnSort;
            this.parameterSorts = Collections.unmodifiableList(new ArrayList<String>(
                    parameterSorts));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            DeclareFun that = (DeclareFun) obj;

            if (!name.equals(that.name)) {
                return false;
            }
            if (!parameterSorts.equals(that.parameterSorts)) {
                return false;
            }

            return returnSort.equals(that.returnSort);
        }

        public String getName() {
            return name;
        }

        public List<String> getParameterSorts() {
            return parameterSorts;
        }

        public String getReturnSort() {
            return returnSort;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects_hash(name, parameterSorts, returnSort);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("(declare-fun ");
            sb.append(name);
            sb.append(" (");
            for (Iterator<String> it = parameterSorts.iterator(); it.hasNext(); ) {
                sb.append(it.next());

                if (it.hasNext()) {
                    sb.append(" ");
                }
            }
            sb.append(") ");
            sb.append(returnSort);
            sb.append(")");

            return sb.toString();
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class DeclareSort extends Stmt {

        private final String name;
        private final int size;

        public DeclareSort(String name, int size) {
            if (name == null) {
                throw new NullPointerException("name cannot be null");
            }
            if (size < 0) {
                throw new IllegalArgumentException("size cannot be negative");
            }

            this.name = name;
            this.size = size;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            DeclareSort that = (DeclareSort) obj;

            return name.equals(that.name) && size == that.size;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects_hash(name, size);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(declare-sort " + name + " " + size + ")";
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class DefineFun extends Stmt {

        private final String name;
        private final List<Pair<String, String>> parameters;
        private final String returnSort;
        private final String expr;

        public DefineFun(String name, List<Pair<String, String>> parameters, String returnSort,
                String expr) {
            if (name == null) {
                throw new NullPointerException("name cannot be null");
            }
            if (parameters.contains(null)) {
                throw new NullPointerException("parameters cannot contain null");
            }
            if (returnSort == null) {
                throw new NullPointerException("returnSort cannot be null");
            }
            if (expr == null) {
                throw new NullPointerException("expr cannot be null");
            }

            this.name = name;
            this.returnSort = returnSort;
            this.parameters = Collections.unmodifiableList(new ArrayList<Pair<String, String>>(
                    parameters));
            this.expr = expr;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            DefineFun that = (DefineFun) obj;

            if (!name.equals(that.name)) {
                return false;
            }
            if (!parameters.equals(that.parameters)) {
                return false;
            }

            return returnSort.equals(that.returnSort);
        }

        public String getExpr() {
            return expr;
        }

        public String getName() {
            return name;
        }

        public List<Pair<String, String>> getParameters() {
            return parameters;
        }

        public String getReturnSort() {
            return returnSort;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects_hash(name, parameters, returnSort, expr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("(define-fun ");
            sb.append(name);
            sb.append(" (");
            for (Iterator<Pair<String, String>> it = parameters.iterator(); it.hasNext(); ) {
                Pair<String, String> parameter = it.next();

                sb.append("(");
                sb.append(parameter.first());
                sb.append(" ");
                sb.append(parameter.second());
                sb.append(")");

                if (it.hasNext()) {
                    sb.append(" ");
                }
            }
            sb.append(") ");
            sb.append(returnSort);
            sb.append(" ");
            sb.append(expr);
            sb.append(")");

            return sb.toString();
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class DefineSort extends Stmt {

        private final String name;
        private final List<String> parameters;
        private final String expr;

        public DefineSort(String name, List<String> parameters, String expr) {
            if (name == null) {
                throw new NullPointerException("name cannot be null");
            }
            if (parameters.contains(null)) {
                throw new NullPointerException("parameters cannot contain null");
            }
            if (expr == null) {
                throw new NullPointerException("expr cannot be null");
            }

            this.name = name;
            this.parameters = Collections.unmodifiableList(new ArrayList<String>(parameters));
            this.expr = expr;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            DefineSort that = (DefineSort) obj;

            if (!name.equals(that.name)) {
                return false;
            }
            if (!parameters.equals(that.parameters)) {
                return false;
            }

            return expr.equals(that.expr);
        }

        public String getExpr() {
            return expr;
        }

        public String getName() {
            return name;
        }

        public List<String> getParameters() {
            return parameters;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects_hash(name, parameters, expr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("(define-sort ");
            sb.append(name);
            sb.append(" (");
            for (Iterator<String> it = parameters.iterator(); it.hasNext(); ) {
                sb.append(it.next());

                if (it.hasNext()) {
                    sb.append(" ");
                }
            }
            sb.append(") ");
            sb.append(expr);
            sb.append(")");

            return sb.toString();
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Exit extends Stmt {

        public boolean equals(Object obj) {
            return obj instanceof Exit;
        }

        public int hashCode() {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(exit)";
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Pop extends Stmt {

        private final int count;

        public Pop(int count) {
            if (count < 0) {
                throw new IllegalArgumentException("count cannot be less than 0");
            }

            this.count = count;
        }

        public int getCount() {
            return count;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(pop " + count + ")";
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Push extends Stmt {

        private final int count;

        public Push(int count) {
            if (count < 0) {
                throw new IllegalArgumentException("count cannot be less than 0");
            }

            this.count = count;
        }

        public int getCount() {
            return count;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(push " + count + ")";
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class SetLogic extends Stmt {

        private final Logic logic;

        public SetLogic(Logic logic) {
            if (logic == null) {
                throw new NullPointerException("logic cannot be null");
            }

            this.logic = logic;
        }

        public Logic getLogic() {
            return logic;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(set-logic " + logic + ")";
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class SetOption extends Stmt {

        private final String option;
        private final String value;

        public SetOption(String option, String value) {
            if (option == null) {
                throw new NullPointerException("option cannot be null");
            }
            if (value == null) {
                throw new NullPointerException("value cannot be null");
            }

            this.option = option;
            this.value = value;
        }

        public String getOption() {
            return option;
        }

        public String getValue() {
            return value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(set-option " + option + " " + value + ")";
        }
    }
}
