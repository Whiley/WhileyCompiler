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

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Line> generateLines() {
            List<Line> lines = new ArrayList<Line>();

            lines.addAll(generateInitialisors());
            lines.addAll(generateAddFunctions());
            lines.addAll(generateContainsFunctions());
            lines.addAll(generateRemoveFunctions());
            lines.addAll(generateEmptyConstants());
            lines.addAll(generateLengthFunctions());
            lines.addAll(generateEmptyLengthAssertions());
            lines.addAll(generateSubsetFunctions());

            return lines;
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

        private List<Line> generateAddFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<Pair<String,String>>();
            parameters.add(new Pair<String,String>("set", toString()));
            parameters.add(new Pair<String,String>("t", type));
            String expr = "(store set t true)";

            return Arrays.<Line>asList(new Stmt.DefineFun(FUN_ADD_NAME, parameters, toString(),
                    expr));
        }

        private List<Line> generateContainsFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<Pair<String,String>>();
            parameters.add(new Pair<String,String>("set", toString()));
            parameters.add(new Pair<String,String>("t", type));
            String expr = "(select set t)";

            return Arrays.<Line>asList(new Stmt.DefineFun(FUN_CONTAINS_NAME, parameters, BOOL,
                    expr));
        }

        private List<Line> generateEmptyConstants() {
            List<Line> lines = new ArrayList<Line>();

            lines.add(new Stmt.DeclareFun(FUN_EMPTY_NAME, Collections.EMPTY_LIST, toString()));
            lines.add(new Stmt.Assert("(not (exists ((t " + type + ")) (contains empty t)))"));

            return lines;
        }

        private List<Line> generateEmptyLengthAssertions() {
            List<Line> lines = new ArrayList<Line>();

            lines.add(new Stmt.Assert("(= (length empty) 0)"));

            return lines;
        }

        private List<Line> generateInitialisors() {
            List<String> parameters = Arrays.asList("T");
            String expr = "(" + ARRAY + " T " + BOOL + ")";

            return Arrays.<Line>asList(new Stmt.DefineSort(getName(), parameters, expr));
        }

        private List<Line> generateLengthFunctions() {
            List<Line> lines = new ArrayList<Line>();

            lines.add(new Stmt.DeclareFun(FUN_LENGTH_NAME, Arrays.asList(toString()), INT));
            lines.add(new Stmt.Assert("(forall ((set " + toString() + ")) (<= 0 (length set)))"));
            //            TODO: Make this IFF not implication
            lines.add(new Stmt.Assert(
                    "(forall ((set " + toString() + ")) (=> (not (= set empty)) (exists ((t " + type
                            + ")) (and (contains set t) (= (length set) (+ 1 (length (remove set t))))))))"));

            return lines;
        }

        private List<Line> generateRemoveFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<Pair<String,String>>();
            parameters.add(new Pair<String,String>("set", toString()));
            parameters.add(new Pair<String,String>("t", type));
            String expr = "(store set t false)";

            return Arrays.<Line>asList(new Stmt.DefineFun(FUN_REMOVE_NAME, parameters, toString(),
                    expr));
        }

        private List<Line> generateSubsetFunctions() {
            List<Pair<String, String>> parameters = new ArrayList<Pair<String,String>>();
            parameters.add(new Pair<String,String>("first", toString()));
            parameters.add(new Pair<String,String>("second", toString()));
            String subseteqExpr =
                    "(forall ((t " + type + ")) (=> (contains first t) (contains second t)))";
            String subsetExpr = "(and (subseteq first second) (exists ((t " + type
                    + ")) (and (not (contains first t)) (contains second t))))";
            // Alternative that uses length
            // String subsetExpr = "(and (subseteq first second) (distinct (length first) (length second)))";

            List<Line> functions = new ArrayList<Line>();
            functions.add(new Stmt.DefineFun(FUN_SUBSETEQ_NAME, parameters, BOOL, subseteqExpr));
            functions.add(new Stmt.DefineFun(FUN_SUBSET_NAME, parameters, BOOL, subsetExpr));

            return functions;
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

            this.types = new ArrayList<String>(types);
        }

        public static String generateGetFunctionName(int index) {
            return FUN_GET_NAME + index;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<Line> generateLines() {
            List<Line> lines = new ArrayList<Line>();

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

        private List<Line> generateGetFunctions() {
            List<Line> getFunctions = new ArrayList<Line>();

            for (int i = 0; i < types.size(); i++) {
                getFunctions.add(new Stmt.DeclareFun(generateGetFunctionName(i), Arrays.asList(
                        toString()), types.get(i)));
            }

            return getFunctions;
        }

        private List<Line> generateInitialisors() {
            return Arrays.<Line>asList(new Stmt.DeclareSort(getName(), types.size()));
        }
    }
}
