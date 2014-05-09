package wycs.solver.smt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import wycc.util.Pair;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public abstract class Sort {

    public static final String ARRAY = "Array";
    public static final String BOOL = "Bool";
    public static final String INT = "Int";
    public static final String REAL = "Real";

    /**
     * This class can only be instantiated locally.
     */
    Sort() {}

    public abstract List<Line> generateLines();

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

        public List<Line> generateAddFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<>();
            parameters.add(new Pair<>("set", toString()));
            parameters.add(new Pair<>("t", type));
            String expr = "(store set t true)";

            return Arrays.<Line>asList(new Stmt.DefineFun(FUN_ADD_NAME, parameters, toString(),
                    expr));
        }

        public List<Line> generateContainsFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<>();
            parameters.add(new Pair<>("set", toString()));
            parameters.add(new Pair<>("t", type));
            String expr = "(select set t)";

            return Arrays.<Line>asList(new Stmt.DefineFun(FUN_CONTAINS_NAME, parameters, BOOL,
                    expr));
        }

        public List<Line> generateEmptyConstants() {
            List<Line> lines = new ArrayList<>();

            lines.add(new Stmt.DeclareFun(FUN_EMPTY_NAME, Collections.EMPTY_LIST, toString()));
            lines.add(new Stmt.Assert("(= (length empty) 0)"));

            return lines;
        }

        public List<Line> generateInitialisors() {
            List<String> parameters = Arrays.asList("T");
            String expr = "(" + ARRAY + " T " + BOOL + ")";

            return Arrays.<Line>asList(new Stmt.DefineSort(getName(), parameters, expr));
        }

        public List<Line> generateLengthFunctions() {
            List<Line> lines = new ArrayList<>();

            lines.add(new Stmt.DeclareFun(FUN_LENGTH_NAME, Arrays.asList(toString()), INT));
            lines.add(new Stmt.Assert("(forall ((set " + toString() + ")) (<= 0 (length set)))"));

//            List<Pair<String, String>> parameters = new ArrayList<>();
//            parameters.add(new Pair<>("set", toString()));
//            parameters.add(new Pair<>("length", INT));
//            String expr = "(ite (= 0 length) (not (exists ((t " + type
//                    + ")) (contains set t))) (exists ((t " + type
//                    + ")) (and (contains set t) (setLengthConjecture (remove set t) (- length 1)))))";

//            lines.add(new Stmt.DefineFun("setLengthConjecture", parameters, BOOL, expr));
//            lines.add(new Stmt.Assert(
//                    "(forall ((set " + toString() + ")) (setLengthConjecture set (length set)))"));

            return lines;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Line> generateLines() {
            List<Line> lines = new ArrayList<>();

            lines.addAll(generateInitialisors());
            lines.addAll(generateAddFunctions());
            lines.addAll(generateContainsFunctions());
            lines.addAll(generateRemoveFunctions());
            lines.addAll(generateLengthFunctions());
            lines.addAll(generateEmptyConstants());
            lines.addAll(generateSubsetFunctions());

            return lines;
        }

        public List<Line> generateRemoveFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<>();
            parameters.add(new Pair<>("set", toString()));
            parameters.add(new Pair<>("t", type));
            String expr = "(store set t false)";

            return Arrays.<Line>asList(new Stmt.DefineFun(FUN_REMOVE_NAME, parameters, toString(),
                    expr));
        }

        public List<Line> generateSubsetFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<>();
            parameters.add(new Pair<>("first", toString()));
            parameters.add(new Pair<>("second", toString()));
            String subseteqExpr =
                    "(forall ((t " + type + ")) (=> (contains first t) (contains second t)))";
            String subsetExpr = "(and (subseteq first second) (exists ((t " + type
                    + ")) (and (not (contains first t)) (contains second t))))";
            // Alternative that uses length
            // String subsetExpr = "(and (subseteq first second) (distinct (length first) (length second)))";

            List<Line> functions = new ArrayList<>();
            functions.add(new Stmt.DefineFun(FUN_SUBSETEQ_NAME, parameters, BOOL, subseteqExpr));
            functions.add(new Stmt.DefineFun(FUN_SUBSET_NAME, parameters, BOOL, subsetExpr));

            return functions;
        }

        public String getName() {
            return "Set";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "(" + getName() + " " + type + ")";
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

            this.types = new ArrayList<>(types);
        }

        public static String generateGetFunctionName(int index) {
            return FUN_GET_NAME + index;
        }

        public List<Line> generateGetFunctions() {
            List<Line> getFunctions = new ArrayList<>();

            for (int i = 0; i < types.size(); i++) {
                getFunctions.add(new Stmt.DeclareFun(generateGetFunctionName(i), Arrays.asList(
                        toString()), types.get(i)));
            }

            return getFunctions;
        }

        public List<Line> generateInitialisors() {
            return Arrays.<Line>asList(new Stmt.DeclareSort(getName(), types.size()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Line> generateLines() {
            List<Line> lines = new ArrayList<>();

            lines.addAll(generateInitialisors());
            lines.addAll(generateGetFunctions());

            return lines;
        }

        public String getName() {
            return "Tuple" + types.size();
        }

        /**
         * {@inheritDoc}
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
    }
}
